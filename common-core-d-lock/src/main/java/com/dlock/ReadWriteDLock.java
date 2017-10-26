package com.dlock;

/**
 * @author Liu Hailin
 * @create 2017-10-26 下午9:47
 **/
public interface ReadWriteDLock {

    DLock readLock();

    DLock writeLock();
}
