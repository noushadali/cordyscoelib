package com.cordys.coe.tools.snapshot.data.collector;

import com.cordys.coe.tools.snapshot.data.Constants;

import javax.management.openmbean.CompositeData;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class contains the information for the DB connection pool information.
 * 
 * @author localpg
 */
@XmlRootElement(name = "DBPoolResult", namespace = Constants.NS)
public class DBConnectionPoolInfo
{
    /**
     * Holds the name of the connection pool.
     */
    private String m_name;
    /**
     * Holds the minimum configured read connections.
     */
    private int m_mimRead;
    /**
     * Holds the maximum configured read connections.
     */
    private int m_maxRead;
    /**
     * Holds the current number of active read connections.
     */
    private int m_activeRead;
    /**
     * Holds the minimum configured write connections.
     */
    private int m_mimWrite;
    /**
     * Holds the maximum configured write connections.
     */
    private int m_maxWrite;
    /**
     * Holds the current number of active write connections.
     */
    private int m_activeWrite;
    /**
     * Holds the read connection waiting times.
     */
    private WaitTime m_readWaitTime;
    /**
     * Holds the write connection waiting times.
     */
    private WaitTime m_writeWaitTime;
    /**
     * Holds the read connection usage.
     */
    private Usage m_readUsage;
    /**
     * Holds the write connection usage.
     */
    private Usage m_writeUsage;

    /**
     * This method gets the write connection usage.
     * 
     * @return The write connection usage.
     */
    @XmlElement(name = "WriteUsage", namespace = Constants.NS)
    public Usage getWriteUsage()
    {
        return m_writeUsage;
    }

    /**
     * This method sets the write connection usage.
     * 
     * @param writeUsage The write connection usage.
     */
    public void setWriteUsage(Usage writeUsage)
    {
        m_writeUsage = writeUsage;
    }

    /**
     * This method gets the read connection usage.
     * 
     * @return The read connection usage.
     */
    @XmlElement(name = "ReadUsage", namespace = Constants.NS)
    public Usage getReadUsage()
    {
        return m_readUsage;
    }

    /**
     * This method sets the read connection usage.
     * 
     * @param readUsage The read connection usage.
     */
    public void setReadUsage(Usage readUsage)
    {
        m_readUsage = readUsage;
    }

    /**
     * This method gets the write connection waiting times.
     * 
     * @return The write connection waiting times.
     */
    @XmlElement(name = "WriteWaitTime", namespace = Constants.NS)
    public WaitTime getWriteWaitTime()
    {
        return m_writeWaitTime;
    }

    /**
     * This method sets the write connection waiting times.
     * 
     * @param writeWaitTime The write connection waiting times.
     */
    public void setWriteWaitTime(WaitTime writeWaitTime)
    {
        m_writeWaitTime = writeWaitTime;
    }

    /**
     * This method gets the read connection waiting times.
     * 
     * @return The read connection waiting times.
     */
    @XmlElement(name = "ReadWaitTime", namespace = Constants.NS)
    public WaitTime getReadWaitTime()
    {
        return m_readWaitTime;
    }

    /**
     * This method sets the read connection waiting times.
     * 
     * @param readWaitTime The read connection waiting times.
     */
    public void setReadWaitTime(WaitTime readWaitTime)
    {
        m_readWaitTime = readWaitTime;
    }

    /**
     * This method gets the current number of active write connections.
     * 
     * @return The current number of active write connections.
     */
    @XmlElement(name = "ActiveWrite", namespace = Constants.NS)
    public int getActiveWrite()
    {
        return m_activeWrite;
    }

    /**
     * This method sets the current number of active write connections.
     * 
     * @param activeWrite The current number of active write connections.
     */
    public void setActiveWrite(int activeWrite)
    {
        m_activeWrite = activeWrite;
    }

    /**
     * This method gets the maximum configured write connections.
     * 
     * @return The maximum configured write connections.
     */
    @XmlElement(name = "MaximumWrite", namespace = Constants.NS)
    public int getMaximumWrite()
    {
        return m_maxWrite;
    }

    /**
     * This method sets the maximum configured write connections.
     * 
     * @param maxWrite The maximum configured write connections.
     */
    public void setMaximumWrite(int maxWrite)
    {
        m_maxWrite = maxWrite;
    }

    /**
     * This method gets the minimum configured write connections.
     * 
     * @return The minimum configured write connections.
     */
    @XmlElement(name = "MinimumWrite", namespace = Constants.NS)
    public int getMinimumWrite()
    {
        return m_mimWrite;
    }

    /**
     * This method sets the minimum configured write connections.
     * 
     * @param mimWrite The minimum configured write connections.
     */
    public void setMinimumWrite(int mimWrite)
    {
        m_mimWrite = mimWrite;
    }

    /**
     * This method gets the current number of active read connections.
     * 
     * @return The current number of active read connections.
     */
    @XmlElement(name = "ActiveRead", namespace = Constants.NS)
    public int getActiveRead()
    {
        return m_activeRead;
    }

    /**
     * This method sets the current number of active read connections.
     * 
     * @param activeRead The current number of active read connections.
     */
    public void setActiveRead(int activeRead)
    {
        m_activeRead = activeRead;
    }

    /**
     * This method gets the maximum configured read connections.
     * 
     * @return The maximum configured read connections.
     */
    @XmlElement(name = "MaximumRead", namespace = Constants.NS)
    public int getMaximumRead()
    {
        return m_maxRead;
    }

    /**
     * This method sets the maximum configured read connections.
     * 
     * @param maxRead The maximum configured read connections.
     */
    public void setMaximumRead(int maxRead)
    {
        m_maxRead = maxRead;
    }

    /**
     * This method gets the minimum configured read connections.
     * 
     * @return The minimum configured read connections.
     */
    @XmlElement(name = "MinimumRead", namespace = Constants.NS)
    public int getMinimumRead()
    {
        return m_mimRead;
    }

    /**
     * This method sets the minimum configured read connections.
     * 
     * @param mimRead The minimum configured read connections.
     */
    public void setMinimumRead(int mimRead)
    {
        m_mimRead = mimRead;
    }

    /**
     * This method gets the name of the connection pool.
     * 
     * @return The name of the connection pool.
     */
    @XmlElement(name = "Name", namespace = Constants.NS)
    public String getName()
    {
        return m_name;
    }

    /**
     * This method sets the name of the connection pool.
     * 
     * @param name The name of the connection pool.
     */
    public void setName(String name)
    {
        m_name = name;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return m_name;
    }

    /**
     * This method returns the summary for the read connections.
     * 
     * @return The summary for the read connections.
     */
    public String getReadSummary()
    {
        StringBuilder sb = new StringBuilder(1024);
        sb.append(m_activeRead).append(" (").append(m_mimRead).append("-").append(m_maxRead).append(")");
        return sb.toString();
    }

    /**
     * This method returns the summary for the write connections.
     * 
     * @return The summary for the write connections.
     */
    public String getWriteSummary()
    {
        StringBuilder sb = new StringBuilder(1024);
        sb.append(m_activeWrite).append(" (").append(m_mimWrite).append("-").append(m_maxWrite).append(")");
        return sb.toString();
    }

    /**
     * Holds the waiting times.
     */
    public static class WaitTime
    {
        /**
         * Holds the average waiting time.
         */
        private long m_average;
        /**
         * Holds the current waiting time.
         */
        private long m_current;
        /**
         * Holds the number of events per second.
         */
        private long m_eventsPerSecond;
        /**
         * Holds the minimum waiting time.
         */
        private long m_minimum;
        /**
         * Holds the maximum waiting time.
         */
        private long m_maximum;

        /**
         * This method gets the maximum waiting time.
         * 
         * @return The maximum waiting time.
         */
        @XmlElement(name = "Maximum", namespace = Constants.NS)
        public long getMaximum()
        {
            return m_maximum;
        }

        /**
         * This method sets the maximum waiting time.
         * 
         * @param maximum The maximum waiting time.
         */
        public void setMaximum(long maximum)
        {
            m_maximum = maximum;
        }

        /**
         * This method gets the minimum waiting time.
         * 
         * @return The minimum waiting time.
         */
        @XmlElement(name = "Minimum", namespace = Constants.NS)
        public long getMinimum()
        {
            return m_minimum;
        }

        /**
         * This method sets the minimum waiting time.
         * 
         * @param minimum The minimum waiting time.
         */
        public void setMinimum(long minimum)
        {
            m_minimum = minimum;
        }

        /**
         * This method gets the number of events per second.
         * 
         * @return The number of events per second.
         */
        @XmlElement(name = "EventsPerSecond", namespace = Constants.NS)
        public long getEventsPerSecond()
        {
            return m_eventsPerSecond;
        }

        /**
         * This method sets the number of events per second.
         * 
         * @param eventsPerSecond The number of events per second.
         */
        public void setEventsPerSecond(long eventsPerSecond)
        {
            m_eventsPerSecond = eventsPerSecond;
        }

        /**
         * This method gets the current waiting time.
         * 
         * @return The current waiting time.
         */
        @XmlElement(name = "Current", namespace = Constants.NS)
        public long getCurrent()
        {
            return m_current;
        }

        /**
         * This method sets the current waiting time.
         * 
         * @param current The current waiting time.
         */
        public void setCurrent(long current)
        {
            m_current = current;
        }

        /**
         * This method gets the average waiting time.
         * 
         * @return The average waiting time.
         */
        @XmlElement(name = "Average", namespace = Constants.NS)
        public long getAverage()
        {
            return m_average;
        }

        /**
         * This method sets the average waiting time.
         * 
         * @param average The average waiting time.
         */
        public void setAverage(long average)
        {
            m_average = average;
        }

        /**
         * Gets a new instance of the Usage report based on the composite data.
         * 
         * @param cd the data to use as a source.
         * @return The new instance.
         */
        public static WaitTime getInstance(CompositeData cd)
        {
            WaitTime retVal = new WaitTime();

            retVal.m_average = (Integer) cd.get("averageValue");
            retVal.m_current = (Integer) cd.get("currentValue");
            retVal.m_eventsPerSecond = (Integer) cd.get("eventsPerSecond");
            retVal.m_maximum = (Long) cd.get("maxValue");
            retVal.m_minimum = (Long) cd.get("minValue");

            return retVal;
        }
    }

    /**
     * Holds the detail of the usage.
     */
    public static class Usage
    {
        /**
         * Holds the average waiting time.
         */
        private long m_average;
        /**
         * Holds the current waiting time.
         */
        private long m_current;
        /**
         * Holds the number of events per second.
         */
        private long m_eventsPerSecond;
        /**
         * Holds the minimum waiting time.
         */
        private long m_minimum;
        /**
         * Holds the maximum waiting time.
         */
        private long m_maximum;

        /**
         * This method gets the maximum waiting time.
         * 
         * @return The maximum waiting time.
         */
        @XmlElement(name = "Maximum", namespace = Constants.NS)
        public long getMaximum()
        {
            return m_maximum;
        }

        /**
         * This method sets the maximum waiting time.
         * 
         * @param maximum The maximum waiting time.
         */
        public void setMaximum(long maximum)
        {
            m_maximum = maximum;
        }

        /**
         * This method gets the minimum waiting time.
         * 
         * @return The minimum waiting time.
         */
        @XmlElement(name = "Minimum", namespace = Constants.NS)
        public long getMinimum()
        {
            return m_minimum;
        }

        /**
         * This method sets the minimum waiting time.
         * 
         * @param minimum The minimum waiting time.
         */
        public void setMinimum(long minimum)
        {
            m_minimum = minimum;
        }

        /**
         * This method gets the number of events per second.
         * 
         * @return The number of events per second.
         */
        @XmlElement(name = "EventsPerSecond", namespace = Constants.NS)
        public long getEventsPerSecond()
        {
            return m_eventsPerSecond;
        }

        /**
         * This method sets the number of events per second.
         * 
         * @param eventsPerSecond The number of events per second.
         */
        public void setEventsPerSecond(long eventsPerSecond)
        {
            m_eventsPerSecond = eventsPerSecond;
        }

        /**
         * This method gets the current waiting time.
         * 
         * @return The current waiting time.
         */
        @XmlElement(name = "Current", namespace = Constants.NS)
        public long getCurrent()
        {
            return m_current;
        }

        /**
         * This method sets the current waiting time.
         * 
         * @param current The current waiting time.
         */
        public void setCurrent(long current)
        {
            m_current = current;
        }

        /**
         * This method gets the average waiting time.
         * 
         * @return The average waiting time.
         */
        @XmlElement(name = "Average", namespace = Constants.NS)
        public long getAverage()
        {
            return m_average;
        }

        /**
         * This method sets the average waiting time.
         * 
         * @param average The average waiting time.
         */
        public void setAverage(long average)
        {
            m_average = average;
        }

        /**
         * Gets a new instance of the Usage report based on the composite data.
         * 
         * @param cd the data to use as a source.
         * @return The new instance.
         */
        public static Usage getInstance(CompositeData cd)
        {
            Usage retVal = new Usage();

            retVal.m_average = (Integer) cd.get("averageValue");
            retVal.m_current = (Integer) cd.get("currentValue");
            retVal.m_eventsPerSecond = (Integer) cd.get("eventsPerSecond");
            retVal.m_maximum = (Long) cd.get("maxValue");
            retVal.m_minimum = (Long) cd.get("minValue");

            return retVal;
        }
    }
}
