package com.cordys.coe.tools.snapshot.data.collector;

import com.cordys.coe.tools.snapshot.data.Constants;
import com.cordys.coe.util.general.Util;

import javax.management.openmbean.CompositeData;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Holds the memory details for a certain pool.
 * 
 * @author localpg
 */
@XmlRootElement(name = "MemoryDetail", namespace = Constants.NS)
public class MemoryDetail
{
    /**
     * Holds the name of the memory space.
     */
    private String m_name;
    /**
     * Holds the committed amount of bytes.
     */
    private long m_comitted;
    /**
     * Holds the maximum amount of bytes available.
     */
    private long m_maximum;
    /**
     * Holds the initial amount of bytes.
     */
    private long m_initial;
    /**
     * Holds the amount of bytes actually used.
     */
    private long m_used;
    /**
     * Holds the type of memory (HEAP or NON_HEAP).
     */
    private String m_type;

    /**
     * This method gets the type of memory (HEAP or NON_HEAP).
     * 
     * @return The type of memory (HEAP or NON_HEAP).
     */
    public String getType()
    {
        return m_type;
    }

    /**
     * This method sets the type of memory (HEAP or NON_HEAP).
     * 
     * @param type The type of memory (HEAP or NON_HEAP).
     */
    public void setType(String type)
    {
        m_type = type;
    }

    /**
     * This method gets the amount of bytes actually used.
     * 
     * @return The amount of bytes actually used.
     */
    public long getUsed()
    {
        return m_used;
    }

    /**
     * This method sets the amount of bytes actually used.
     * 
     * @param used The amount of bytes actually used.
     */
    public void setUsed(long used)
    {
        m_used = used;
    }

    /**
     * This method gets the initial amount of bytes.
     * 
     * @return The initial amount of bytes.
     */
    public long getInitial()
    {
        return m_initial;
    }

    /**
     * This method sets the initial amount of bytes.
     * 
     * @param initial The initial amount of bytes.
     */
    public void setInitial(long initial)
    {
        m_initial = initial;
    }

    /**
     * This method gets the maximum amount of bytes available.
     * 
     * @return The maximum amount of bytes available.
     */
    public long getMaximum()
    {
        return m_maximum;
    }

    /**
     * This method sets the maximum amount of bytes available.
     * 
     * @param maximum The maximum amount of bytes available.
     */
    public void setMaximum(long maximum)
    {
        m_maximum = maximum;
    }

    /**
     * This method gets the committed amount of bytes.
     * 
     * @return The committed amount of bytes.
     */
    public long getCommitted()
    {
        return m_comitted;
    }

    /**
     * This method sets the committed amount of bytes.
     * 
     * @param comitted The committed amount of bytes.
     */
    public void setCommitted(long comitted)
    {
        m_comitted = comitted;
    }

    /**
     * This method gets the name of the memory space.
     * 
     * @return The name of the memory space.
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * This method sets the name of the memory space.
     * 
     * @param name The name of the memory space.
     */
    public void setName(String name)
    {
        m_name = name;
    }

    /**
     * This method gets the percentage of memory used based on the max.
     * 
     * @return The percentage of memory used based on the max.
     */
    public int getPercentage()
    {
        float tmp = (float) getUsed() / getMaximum();
        int percentage = Math.round(tmp * 100);

        return percentage;
    }

    /**
     * This method creates the memory detail object based on the composite memory data.
     * 
     * @param data The composite data holding the memory details.
     * @param name The name of the memory detail.
     * @param type The type of memory (HEAP or NON_HEAP).
     * @return The created memory details.
     */
    public static MemoryDetail getInstance(CompositeData data, String name, String type)
    {
        MemoryDetail retVal = new MemoryDetail();

        retVal.setName(name);
        retVal.setType(type);

        retVal.setCommitted((Long) data.get("committed"));
        retVal.setInitial((Long) data.get("init"));
        retVal.setMaximum((Long) data.get("max"));
        retVal.setUsed((Long) data.get("used"));

        return retVal;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(1024);

        String used = Util.readableSize(m_used);
        String committed = Util.readableSize(m_comitted);
        String max = Util.readableSize(m_maximum);

        sb.append(used).append(" / ").append(committed).append(" / ").append(max).append("(").append(getPercentage())
                .append("%)");

        return sb.toString();
    }
}
