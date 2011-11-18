package com.cordys.coe.tools.snapshot.data.collector;

import com.cordys.coe.tools.snapshot.data.Constants;

import javax.xml.bind.annotation.XmlElement;

/**
 * Wraps the JMX information for a dispatcher.
 *
 * @author  localpg
 */
public class DispatcherInfo
{
    /**
     * Holds the maximum number of concurrent workers.
     */
    private int m_maxConcurrentWorkers;
    /**
     * Holds the minimum amount of concurrent workers.
     */
    private int m_minConcurrentWorkers;
    /**
     * Holds the current amount of workers.
     */
    private int m_currentWorkers;
    /**
     * Holds the number of idle workers.
     */
    private int m_idleWorkers;
    /**
     * Holds the number of active workers.
     */
    private int m_activeWorkers;

    /**
     * This method gets the number of active workers.
     *
     * @return  The number of active workers.
     */
    @XmlElement(name = "ActiveWorkers", namespace = Constants.NS)
    public int getActiveWorkers()
    {
        return m_activeWorkers;
    }

    /**
     * This method sets the number of active workers.
     *
     * @param  activeWorkers  The number of active workers.
     */
    public void setActiveWorkers(int activeWorkers)
    {
        m_activeWorkers = activeWorkers;
    }

    /**
     * This method gets the number of idle workers.
     *
     * @return  The number of idle workers.
     */
    @XmlElement(name = "IdleWorkers", namespace = Constants.NS)
    public int getIdleWorkers()
    {
        return m_idleWorkers;
    }

    /**
     * This method sets the number of idle workers.
     *
     * @param  nrOfIdleWorkers  The number of idle workers.
     */
    public void setIdleWorkers(int nrOfIdleWorkers)
    {
        m_idleWorkers = nrOfIdleWorkers;
    }

    /**
     * This method gets the current amount of workers.
     *
     * @return  The current amount of workers.
     */
    @XmlElement(name = "CurrentWorkers", namespace = Constants.NS)
    public int getCurrentWorkers()
    {
        return m_currentWorkers;
    }

    /**
     * This method sets the current amount of workers.
     *
     * @param  currentWorkers  The current amount of workers.
     */
    public void setCurrentWorkers(int currentWorkers)
    {
        m_currentWorkers = currentWorkers;
    }

    /**
     * This method gets the minimum amount of concurrent workers.
     *
     * @return  The minimum amount of concurrent workers.
     */
    @XmlElement(name = "MinConcurrentWorkers", namespace = Constants.NS)
    public int getMinConcurrentWorkers()
    {
        return m_minConcurrentWorkers;
    }

    /**
     * This method sets the minimum amount of concurrent workers.
     *
     * @param  minConcurrentWorkers  The minimum amount of concurrent workers.
     */
    public void setMinConcurrentWorkers(int minConcurrentWorkers)
    {
        m_minConcurrentWorkers = minConcurrentWorkers;
    }

    /**
     * This method gets the maximum number of concurrent workers.
     *
     * @return  The maximum number of concurrent workers.
     */
    @XmlElement(name = "MaxConcurrentWorkers", namespace = Constants.NS)
    public int getMaxConcurrentWorkers()
    {
        return m_maxConcurrentWorkers;
    }

    /**
     * This method sets the maximum number of concurrent workers.
     *
     * @param  maxConcurrentWorkers  The maximum number of concurrent workers.
     */
    public void setMaxConcurrentWorkers(int maxConcurrentWorkers)
    {
        m_maxConcurrentWorkers = maxConcurrentWorkers;
    }
}
