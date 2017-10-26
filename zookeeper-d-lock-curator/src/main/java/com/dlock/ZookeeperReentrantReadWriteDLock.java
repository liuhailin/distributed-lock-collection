package com.dlock;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;

/**
 * @author Liu Hailin
 * @create 2017-10-26 下午9:02
 **/
@Slf4j
public class ZookeeperReentrantReadWriteDLock implements ReadWriteDLock, Serializable {

    private CuratorFramework client;
    private String lockName;
    private InterProcessReadWriteLock mutex;
    private InterProcessMutex readMutex;
    private InterProcessMutex writeMutex;

    private DReadLock dReadLock;
    private DWriteLock dWriteLock;

    public ZookeeperReentrantReadWriteDLock(CuratorFramework client, String lockName) {
        this.client = client;
        this.lockName = lockName;
        this.mutex = new InterProcessReadWriteLock( this.client, this.lockName );
        readMutex = this.mutex.readLock();
        writeMutex = this.mutex.writeLock();
    }

    @Override
    public DReadLock readLock() {
        return new DReadLock( this );
    }

    @Override
    public DWriteLock writeLock() {
        return new DWriteLock( this );
    }

    @Slf4j
    public static class DReadLock implements DLock, Serializable {

        private InterProcessMutex readMutex;

        public DReadLock(ZookeeperReentrantReadWriteDLock lock) {
            this.readMutex = lock.readMutex;
        }

        @Override
        public void lock() {
            try {
                readMutex.acquire();
            } catch (Exception e) {
                log.error( "can not get lock", e );
            }

        }

        @Override
        public void lockInterruptibily() throws InterruptedException {
            try {
                readMutex.acquire();
            } catch (InterruptedException ie) {
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
                readMutex.release();
            } catch (Exception e) {
                log.error( "can not unlock", e );
            }

        }
    }

    @Slf4j
    public static class DWriteLock implements DLock, Serializable {

        private InterProcessMutex writeMutex;

        public DWriteLock(ZookeeperReentrantReadWriteDLock lock) {
            this.writeMutex = lock.writeMutex;
        }

        @Override
        public void lock() {
            try {
                writeMutex.acquire();
            } catch (Exception e) {
                log.error( "can not get lock", e );
            }

        }

        @Override
        public void lockInterruptibily() throws InterruptedException {
            try {
                writeMutex.acquire();
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
                writeMutex.release();
            } catch (Exception e) {
                log.error( "can not unlock", e );
            }

        }
    }
}
