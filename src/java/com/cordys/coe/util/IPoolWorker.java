package com.cordys.coe.util;

/**
 * Interface for workers in a pool.
 *
 * @author  jverhaar
 */
public interface IPoolWorker
{
    /**
     * This method gets the ID of the pool worker.
     *
     * @return  The ID of the pool worker.
     */
    String getID();

    /**
     * Returns whether or not the worker can be placed back in the pool after working.
     *
     * @return  Whether or not the worker can be placed back in the pool after working.
     */
    boolean isReusable();
}
