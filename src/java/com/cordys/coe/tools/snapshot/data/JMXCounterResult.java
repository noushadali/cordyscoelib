package com.cordys.coe.tools.snapshot.data;

import com.cordys.coe.tools.snapshot.config.JMXCounter;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class wraps the result of a JMX counter. This is used to make sure the result can be
 *
 * @author  localpg
 */
@XmlRootElement(name = "JMXCounterResult", namespace = Constants.NS)
public class JMXCounterResult
{
    /**
     * Holds the results of the counter.
     */
    @XmlElement(name = "JMXCounterResult", namespace = Constants.NS)
    @XmlElementWrapper(name = "JMXCounterResultList", namespace = Constants.NS)
    private List<ResultWrapper> m_results = new ArrayList<JMXCounterResult.ResultWrapper>();

    /**
     * This method returns the result wrappers to include in the dump.
     *
     * @return  The result wrappers to include in the dump.
     */
    public List<ResultWrapper> getResultWrapperList()
    {
        return m_results;
    }

    /**
     * This method adds the given result wrapper.
     *
     * @param  data  The result wrapper to add.
     */
    public void addResultWrapper(ResultWrapper data)
    {
        m_results.add(data);
    }

    /**
     * This method adds the given SnapshotData.
     *
     * @param  counter  The JMX counter.
     * @param  value    The value for the JMX counter.
     */
    public void addResultWrapper(JMXCounter counter, Object value)
    {
        ResultWrapper rw = new ResultWrapper();

        rw.setJmxCounter(counter);
        rw.setValue(value);

        m_results.add(rw);
    }

    /**
     * Wraps the counter result.
     */
    public static class ResultWrapper
    {
        /**
         * Holds the JMX Counter definition that was the source.
         */
        private JMXCounter m_jmxCounter;
        /**
         * Holds the value for the result.
         */
        private Object m_value;

        /**
         * This method gets the value for the result.
         *
         * @return  The value for the result.
         */
        @XmlElement(name = "Value", namespace = Constants.NS)
        public Object getValue()
        {
            return m_value;
        }

        /**
         * This method sets the value for the result.
         *
         * @param  value  The value for the result.
         */
        public void setValue(Object value)
        {
            m_value = value;
        }

        /**
         * This method gets the JMX Counter definition that was the source.
         *
         * @return  The JMX Counter definition that was the source.
         */
        @XmlElement(name = "JMXCounter", namespace = Constants.NS)
        public JMXCounter getJmxCounter()
        {
            return m_jmxCounter;
        }

        /**
         * This method sets the JMX Counter definition that was the source.
         *
         * @param  jmxCounter  The JMX Counter definition that was the source.
         */
        public void setJmxCounter(JMXCounter jmxCounter)
        {
            m_jmxCounter = jmxCounter;
        }
    }
}
