package com.cordys.coe.tools.snapshot.data.collector;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.cordys.coe.tools.snapshot.data.Constants;
import com.cordys.coe.util.StringUtils;

/**
 * This class holds the performance information of the web service operation totals.
 * 
 * @author pgussow
 */
@XmlRootElement(name = "WebServiceOperation", namespace = Constants.NS)
@XmlType(propOrder = { "name", "totalMinimum", "totalMaximum", "totalTime", "totalOccurrences", "totalAverage", "WSI" })
public class WebServiceOperation
{
    /** Holds the name of the web service operation. */
    private String m_name;
    /** Holds the total number of occurrences for this operation. */
    private long m_totalOccurrences;
    /** Holds the total minimum response time. */
    private long m_totalMinimum;
    /** Holds the total maximum response time. */
    private long m_totalMaximum;
    /** Holds the total average response time. */
    private long m_totalAverage;
    /** Holds the total number of nanoseconds that this operation has taken. */
    private long m_totalTime;
    /** Holds the parent web service interface. */
    private String m_wsi;

    /**
     * This method gets the parent web service interface.
     * 
     * @return The parent web service interface.
     */
    @XmlElement(name = "WebServiceInterface", namespace = Constants.NS)
    public String getWSI()
    {
        return m_wsi;
    }

    /**
     * This method sets the parent web service interface.
     * 
     * @param wsi The parent web service interface.
     */
    public void setWSI(String wsi)
    {
        if (StringUtils.isSet(wsi) && wsi.startsWith("\"") && wsi.endsWith("\""))
        {
            wsi = wsi.substring(1, wsi.length() - 1);
        }

        m_wsi = wsi;
    }

    /**
     * This method gets the total number of nanoseconds that this operation has taken.
     * 
     * @return The total number of nanoseconds that this operation has taken.
     */
    @XmlElement(name = "TotalTime", namespace = Constants.NS)
    public long getTotalTime()
    {
        return m_totalTime;
    }

    /**
     * This method sets the total number of nanoseconds that this operation has taken.
     * 
     * @param totalTime The total number of nanoseconds that this operation has taken.
     */
    public void setTotalTime(long totalTime)
    {
        m_totalTime = totalTime;
    }

    /**
     * This method gets the total average response time.
     * 
     * @return The total average response time.
     */
    @XmlElement(name = "TotalAverage", namespace = Constants.NS)
    public long getTotalAverage()
    {
        return m_totalAverage;
    }

    /**
     * This method sets the total average response time.
     * 
     * @param totalAverage The total average response time.
     */
    public void setTotalAverage(long totalAverage)
    {
        m_totalAverage = totalAverage;
    }

    /**
     * This method gets the total maximum response time.
     * 
     * @return The total maximum response time.
     */
    @XmlElement(name = "TotalMaximum", namespace = Constants.NS)
    public long getTotalMaximum()
    {
        return m_totalMaximum;
    }

    /**
     * This method sets the total maximum response time.
     * 
     * @param totalMaximum The total maximum response time.
     */
    public void setTotalMaximum(long totalMaximum)
    {
        m_totalMaximum = totalMaximum;
    }

    /**
     * This method gets the total minimum response time.
     * 
     * @return The total minimum response time.
     */
    @XmlElement(name = "TotalMinimum", namespace = Constants.NS)
    public long getTotalMinimum()
    {
        return m_totalMinimum;
    }

    /**
     * This method sets the total minimum response time.
     * 
     * @param totalMinimum The total minimum response time.
     */
    public void setTotalMinimum(long totalMinimum)
    {
        m_totalMinimum = totalMinimum;
    }

    /**
     * This method gets the total number of occurrences for this operation.
     * 
     * @return The total number of occurrences for this operation.
     */
    @XmlElement(name = "TotalOccurrences", namespace = Constants.NS)
    public long getTotalOccurrences()
    {
        return m_totalOccurrences;
    }

    /**
     * This method sets the total number of occurrences for this operation.
     * 
     * @param totalOccurrences The total number of occurrences for this operation.
     */
    public void setTotalOccurrences(long totalOccurrences)
    {
        m_totalOccurrences = totalOccurrences;
    }

    /**
     * This method gets the name of the web service operation.
     * 
     * @return The name of the web service operation.
     */
    @XmlElement(name = "Name", namespace = Constants.NS)
    public String getName()
    {
        return m_name;
    }

    /**
     * This method sets the name of the web service operation.
     * 
     * @param name The name of the web service operation.
     */
    public void setName(String name)
    {
        if (StringUtils.isSet(name) && name.startsWith("\"") && name.endsWith("\""))
        {
            name = name.substring(1, name.length() - 1);
        }

        m_name = name;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return getName();
    }
}
