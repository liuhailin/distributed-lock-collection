package com.dlock;

import java.util.concurrent.TimeUnit;

import redis.clients.jedis.Jedis;

/**
 * <pre>
 *     <a href="http://carryingcoder.com/2017/10/24/%E5%88%86%E5%B8%83%E5%BC%8F%E9%94%81/">基于Redis的SETNX操作实现的分布式锁 </a>
 * </pre>
 *
 *  不可重入（ not reentran）
 *
 * @author Liu Hailin
 * @create 2017-10-26 下午5:17
 **/
public class RedisDLock implements DLock {

    private Jedis jedis;

    private String lockName;

    //锁住的时间，也就是有效时长
    private long lockedTime;


    public RedisDLock(Jedis jedis, String lockName, long lockedTime) {
        this.jedis = jedis;
        this.lockName = lockName;
        this.lockedTime = lockedTime;
    }

    @Override
    public void lock() {
        for (; ; ) {
            long expiredTime = System.currentTimeMillis() + lockedTime;

            if (jedis.setnx( lockName, String.valueOf( expiredTime ) ) == 1) {
                break;
            }

            String originExpriedTime = jedis.get( lockName );
            if (originExpriedTime != null && checkExpriedLock( originExpriedTime )) {
                expiredTime = System.currentTimeMillis() + lockedTime;
                String expectedTime = jedis.getSet( lockName, String.valueOf( expiredTime ) );

                if (expectedTime != null && checkExpriedLock( expectedTime )) {
                    break;
                }
            }
        }

    }

    private boolean checkExpriedLock(String originExpriedTime) {

        return System.currentTimeMillis() > Long.parseLong( originExpriedTime ) ? true : false;

    }

    @Override
    public void lockInterruptibily() throws InterruptedException {
        for (; ; ) {
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }
            long expiredTime = System.currentTimeMillis() + lockedTime;

            if (jedis.setnx( lockName, String.valueOf( expiredTime ) ) == 1) {
                break;
            }

            String originExpriedTime = jedis.get( lockName );
            if (originExpriedTime != null && checkExpriedLock( originExpriedTime )) {
                expiredTime = System.currentTimeMillis() + lockedTime;
                String expectedTime = jedis.getSet( lockName, String.valueOf( expiredTime ) );

                if (expectedTime != null && checkExpriedLock( expectedTime )) {
                    break;
                }
            }
        }
    }

    @Override
    public boolean tryLock() {

        long expiredTime = System.currentTimeMillis() + lockedTime;
        if (jedis.setnx( lockName, String.valueOf( expiredTime ) ) == 0) {
            return true;
        }

        String originExpriedTime = jedis.get( lockName );
        if (originExpriedTime != null && checkExpriedLock( originExpriedTime )) {
            expiredTime = System.currentTimeMillis() + lockedTime;
            String expectedTime = jedis.getSet( lockName, String.valueOf( expiredTime ) );

            if (expectedTime != null && checkExpriedLock( expectedTime )) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) {

        if (time <= 0) {
            return false;
        }
        long deadLine = System.currentTimeMillis() + unit.toMillis( time );
        for (; ; ) {
            time = deadLine - System.currentTimeMillis();
            if (time <= 0L) {
                return false;
            }
            long expiredTime = System.currentTimeMillis() + lockedTime;

            if (jedis.setnx( lockName, String.valueOf( expiredTime ) ) == 1) {
                return true;
            }

            String originExpriedTime = jedis.get( lockName );
            if (originExpriedTime != null && checkExpriedLock( originExpriedTime )) {
                expiredTime = System.currentTimeMillis() + lockedTime;
                String expectedTime = jedis.getSet( lockName, String.valueOf( expiredTime ) );
                if (expectedTime != null && checkExpriedLock( expectedTime )) {
                    return true;
                }
            }

        }
    }

    @Override
    public boolean tryLockInterruptibily(long time, TimeUnit unit) throws InterruptedException {
        if (time <= 0) {
            return false;
        }
        long deadLine = System.currentTimeMillis() + unit.toMillis( time );
        for (; ; ) {
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }

            time = deadLine - System.currentTimeMillis();
            if (time <= 0L) {
                return false;
            }
            long expiredTime = System.currentTimeMillis() + lockedTime;

            if (jedis.setnx( lockName, String.valueOf( expiredTime ) ) == 1) {
                return true;
            }

            String originExpriedTime = jedis.get( lockName );
            if (originExpriedTime != null && checkExpriedLock( originExpriedTime )) {
                expiredTime = System.currentTimeMillis() + lockedTime;
                String expectedTime = jedis.getSet( lockName, String.valueOf( expiredTime ) );
                if (expectedTime != null && checkExpriedLock( expectedTime )) {
                    return true;
                }
            }

        }
    }

    @Override
    public void unlock() {
        String time = jedis.get( lockName );
        if (time !=null && !checkExpriedLock( time )) {
            jedis.del( lockName );
        }
    }
}
