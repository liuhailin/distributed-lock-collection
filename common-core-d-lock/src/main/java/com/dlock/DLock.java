package com.dlock;

import java.util.concurrent.TimeUnit;

/**
 * @author Liu Hailin
 * @create 2017-10-26 下午4:06
 **/
public interface DLock {

    /**
     * 阻塞，不可中断
     */
    void lock();

    /**
     * 阻塞，可响应中断
     */
    void lockInterruptibily() throws InterruptedException;

    /**
     * 尝试获取锁，获取不到，立即返回，不阻塞
     *
     * @return {@code true} if the lock was acquired and
     * {@code false} otherwise
     */
    boolean tryLock();

    /**
     * 给定时间内阻塞尝试获得，超时自动返回，不响应中断
     *
     * @param time
     * @param unit
     * @return {@code true} if the lock was acquired and
     * {@code false} otherwise
     */
    boolean tryLock(long time, TimeUnit unit);

    /**
     * 给定时间内阻塞尝试获得，超时自动返回，响应中断
     *
     * @param time
     * @param unit
     * @return {@code true} if the lock was acquired and
     * {@code false} otherwise
     * @throws InterruptedException
     */
    boolean tryLockInterruptibily(long time, TimeUnit unit) throws InterruptedException;

    /**
     * 释放锁
     */
    void unlock();

}