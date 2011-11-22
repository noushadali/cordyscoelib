package com.cordys.coe.tools.snapshot.data.collector;

import com.cordys.coe.tools.snapshot.data.Constants;
import com.cordys.coe.tools.snapshot.data.handler.ThreadInfo;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Wraps the JMX information for a dispatcher.
 *
 * @author  localpg
 */
@XmlRootElement(name = "DispatcherInfo", namespace = Constants.NS)
@XmlType(
         propOrder =
         {
             "name", "maxConcurrentWorkers", "minConcurrentWorkers", "currentWorkers", "idleWorkers", "activeWorkers",
             "m_threadInformation"
         }
        )
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
     * Holds the name of the dispatcher.
     */
    private String m_name;
    /**
     * Holds all the threads that belong to this dispatcher.
     */
    @XmlElement(name = "ThreadInfo", namespace = Constants.NS)
    @XmlElementWrapper(name = "ThreadInfoList", namespace = Constants.NS)
    private List<ThreadInfo> m_threadInformation = new ArrayList<ThreadInfo>();

    /**
     * This method gets the name of the dispatcher.
     *
     * @return  The name of the dispatcher.
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * This method sets the name of the dispatcher.
     *
     * @param  name  The name of the dispatcher.
     */
    public void setName(String name)
    {
        m_name = name;
    }

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
     * This method adds the given ThreadInfo.
     *
     * @param  data  The ThreadInfo to add.
     */
    public void addThreadInfo(ThreadInfo data)
    {
        m_threadInformation.add(data);
    }

    /**
     * This method returns the ThreadInfo to include in the dump.
     *
     * @return  The ThreadInfo to include in the dump.
     */
    public List<ThreadInfo> getThreadInfoList()
    {
        return m_threadInformation;
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

    /**
     * DOCUMENTME.
     *
     * @return  DOCUMENTME
     */
    @Override public String toString()
    {
        return getName();
    }
}
