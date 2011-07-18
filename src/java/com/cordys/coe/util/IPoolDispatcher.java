package com.cordys.coe.util;

/**
 * This interface gives the functionality for dispense the contents in a pool.
 *
 * @author  jverhaar
 */
public interface IPoolDispatcher
{
    /**
     * This method creates the ID for the new worker.
     *
     * @return  The new ID.
     */
    String createIDForWorker();

    /**
     * Gets a worker from the pool. The implementing pool could block until a new worker is free.
     *
     * @return  The currentworker.
     */
    IPoolWorker getWorker();

    /**
     * Put a worker back in the pool, when it's reusable. The implementing pool should also free a
     * place in the pool for a next worker.
     *
     * @param  aoWorker  The pool worker.
     */
    void putWorker(IPoolWorker aoWorker);
}
