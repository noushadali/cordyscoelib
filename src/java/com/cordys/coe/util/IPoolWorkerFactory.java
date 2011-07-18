package com.cordys.coe.util;

/**
 * Interface for creating a new worker in the pool.
 *
 * @author  jverhaar
 */
public interface IPoolWorkerFactory
{
    /**
     * Creates a new worker in the pool.
     *
     * @param   aoPool  The pool dispatcher.
     *
     * @return  A worker.
     */
    IPoolWorker createNewWorker(IPoolDispatcher aoPool);
}
