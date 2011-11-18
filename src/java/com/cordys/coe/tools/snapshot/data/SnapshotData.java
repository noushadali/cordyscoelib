package com.cordys.coe.tools.snapshot.data;

import com.cordys.coe.tools.snapshot.config.ActualServiceContainer;
import com.cordys.coe.tools.snapshot.config.JMXCounter;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * This class wraps the data that is captured for a specific service container. It will hold all data captured.
 *
 * @author  localpg
 */
@XmlRootElement(name = "SnapshotData", namespace = Constants.NS)
@XmlType(propOrder = { "actualServiceContainer", "exception", "m_counterResult" })
public class SnapshotData
{
    /**
     * Holds the actual service container for which this data was gathered.
     */
    private ActualServiceContainer m_asc;
    /**
     * Holds the exception that occurred while getting the data.
     */
    private ThrowableWrapper m_exception;
    /**
     * Holds the response for the counters.
     */
    @XmlElement(name = "JMXCounterResult", namespace = Constants.NS)
    @XmlJavaTypeAdapter(value = JMXCounterResultAdapter.class)
    private Map<JMXCounter, Object> m_counterResult = new LinkedHashMap<JMXCounter, Object>();

    /**
     * Creates a new SnapshotData object. Needed for JAXB.
     */
    public SnapshotData()
    {
    }

    /**
     * Creates a new SnapshotData object.
     *
     * @param  asc  The actual service container.
     * @param  t    The exception that occurred.
     */
    public SnapshotData(ActualServiceContainer asc, Throwable t)
    {
        m_asc = asc;
        m_exception = ThrowableWrapper.getInstance(t);
    }

    /**
     * Creates a new SnapshotData object.
     *
     * @param  asc  The actual service container.
     */
    public SnapshotData(ActualServiceContainer asc)
    {
        m_asc = asc;
    }

    /**
     * This method gets the exception that occurred while getting the data.
     *
     * @return  The exception that occurred while getting the data.
     */
    @XmlElement(name = "Throwable", namespace = Constants.NS)
    public ThrowableWrapper getException()
    {
        return m_exception;
    }

    /**
     * This method sets the exception that occurred while getting the data.
     *
     * @param  exception  The exception that occurred while getting the data.
     */
    public void setException(ThrowableWrapper exception)
    {
        m_exception = exception;
    }

    /**
     * This method sets the exception that occurred while getting the data.
     *
     * @param  exception  The exception that occurred while getting the data.
     */
    public void setException(Throwable exception)
    {
        m_exception = ThrowableWrapper.getInstance(exception);
    }

    /**
     * This method gets the actual service container for which this data was gathered.
     *
     * @return  The actual service container for which this data was gathered.
     */
    @XmlElement(name = "ActualServiceContainer", namespace = Constants.NS)
    public ActualServiceContainer getActualServiceContainer()
    {
        return m_asc;
    }

    /**
     * This method sets the actual service container for which this data was gathered.
     *
     * @param  asc  The actual service container for which this data was gathered.
     */
    public void setActualServiceContainer(ActualServiceContainer asc)
    {
        m_asc = asc;
    }

    /**
     * This method adds the result for the given counter.
     *
     * @param  jmxCounter  The counter that failed.
     * @param  object      The result object. Could be an exception.
     */
    public void addCounterValue(JMXCounter jmxCounter, Object object)
    {
        m_counterResult.put(jmxCounter, object);
    }

    /**
     * This method returns the list of counters and their results.
     *
     * @return  The results for the counters
     */
    public Map<JMXCounter, Object> getCounterValuesList()
    {
        return m_counterResult;
    }
}
