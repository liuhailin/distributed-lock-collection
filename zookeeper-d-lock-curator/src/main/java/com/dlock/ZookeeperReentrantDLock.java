package com.dlock;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

/**
 * @author Liu Hailin
 * @create 2017-10-26 下午9:02
 **/
@Slf4j
public class ZookeeperReentrantDLock implements DLock,Serializable {

    private CuratorFramework client;
    private String lockName;
    private InterProcessMutex mutex;

    public ZookeeperReentrantDLock(CuratorFramework client, String lockName) {
        this.client = client;
        this.lockName = lockName;
        this.mutex = new InterProcessMutex( this.client, this.lockName );
    }

    @Override
    public void lock() {
        try {
            mutex.acquire();
        } catch (Exception e) {
            log.error( "can not get lock", e );
        }
    }

    @Override
    public void lockInterruptibily() throws InterruptedException {
        try {
            mutex.acquire();
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            log.error( "interrupt happens,can not get lock" );
        } catch (Exception e) {
            log.error( "can not get lock", e );
        }
    }

    @Override
    public boolean tryLock() {
        throw new RuntimeException( "Curator client does not support this operation" );
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) {
        throw new RuntimeException( "Curator client does not support this operation" );
    }

    @Override
    public boolean tryLockInterruptibily(long time, TimeUnit unit) throws InterruptedException {
        throw new RuntimeException( "Curator client does not support this operation" );
    }

    @Override
    public void unlock() {
        try {
            mutex.release();
        } catch (Exception e) {
            log.error( "can not unlock", e );
        }
    }
}
