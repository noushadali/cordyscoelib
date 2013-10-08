package com.cordys.coe.tools.snapshot;

/**
 * Interface for the use of the progress monitor. This interface is in between to make sure the actual grabbing code is not linked
 * to Swing in any way.
 * 
 * @author pgussow
 */
public interface ISnapshotGrabberProgress
{
    /**
     * This method sets the progress.
     * 
     * @param progress The new progress
     */
    void setGrabberProgress(int progress);

    /**
     * This method publishes the progress data for the job.
     * 
     * @param data The data
     */
    void publishGrabberData(GrabberData data);

    /**
     * This method sets the max progress for the monitor.
     * 
     * @param max The new max.
     */
    void setMax(int max);
}
