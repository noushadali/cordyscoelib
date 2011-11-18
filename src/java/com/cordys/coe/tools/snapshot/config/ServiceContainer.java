package com.cordys.coe.tools.snapshot.config;

import com.cordys.coe.tools.snapshot.data.Constants;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

/**
 * Wraps the configuration for the service containers.
 *
 * @author  localpg
 */
@XmlType(propOrder = { "name", "jmxUrl", "m_jmxCounters" })
public class ServiceContainer
{
    /**
     * Holds the name of the service container to watch.
     */
    private String m_name;
    /**
     * Holds the JMX url to use for connecting to the container.
     */
    private String m_jmxURL;
    /**
     * Holds the JMX counters that should be retrieved when making a snapshot.
     */
    @XmlElement(name = "JMXCounter", namespace = Constants.NS)
    @XmlElementWrapper(name = "JMXCounterList", namespace = Constants.NS)
    private ArrayList<JMXCounter> m_jmxCounters = new ArrayList<JMXCounter>();

    /**
     * This method gets the JMX url to use for connecting to the container.
     *
     * @return  The JMX url to use for connecting to the container.
     */
    @XmlElement(name = "JmxURL", namespace = Constants.NS)
    public String getJmxUrl()
    {
        return m_jmxURL;
    }

    /**
     * This method sets the JMX url to use for connecting to the container.
     *
     * @param  jmxURL  The JMX url to use for connecting to the container.
     */
    public void setJmxUrl(String jmxURL)
    {
        m_jmxURL = jmxURL;
    }

    /**
     * This method gets the name of the service container to watch.
     *
     * @return  The name of the service container to watch.
     */
    @XmlElement(name = "Name", namespace = Constants.NS)
    public String getName()
    {
        return m_name;
    }

    /**
     * This method sets the name of the service container to watch.
     *
     * @param  name  The name of the service container to watch.
     */
    public void setName(String name)
    {
        m_name = name;
    }

    /**
     * This method returns the JMX counters to include in the dump.
     *
     * @return  The JMX counters to include in the dump.
     */
    public ArrayList<JMXCounter> getJMXCounterList()
    {
        return m_jmxCounters;
    }

    /**
     * This method adds the given JMX counter.
     *
     * @param  jmxCounter  The JMX counter to add.
     */
    public void addJMXCounter(JMXCounter jmxCounter)
    {
        m_jmxCounters.add(jmxCounter);
    }

    /**
     * @see  java.lang.Object#toString()
     */
    @Override public String toString()
    {
        return getName();
    }
}
