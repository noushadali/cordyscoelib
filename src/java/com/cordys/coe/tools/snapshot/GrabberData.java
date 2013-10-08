package com.cordys.coe.tools.snapshot;

import com.cordys.coe.util.StringUtils;

/**
 * Holds the Class GrabberData. This class holds the progress data for the snapshot grabber and the current note details to show.
 */
public class GrabberData
{
    /** Holds the current progress. */
    private int m_progress;
    /** Holds the current hostname it is grabbing data from. */
    private String m_host;
    /** Holds the service container where the data is being retrieved from. */
    private String m_serviceContainer;
    /** Holds the detailed message. */
    private String m_detail;

    /**
     * This method gets the detailed message.
     * 
     * @return The detailed message.
     */
    public String getDetail()
    {
        return m_detail;
    }

    /**
     * This method sets the detailed message.
     * 
     * @param detail The detailed message.
     */
    public void setDetail(String detail)
    {
        m_detail = detail;
    }

    /**
     * This method gets the service container where the data is being retrieved from.
     * 
     * @return The service container where the data is being retrieved from.
     */
    public String getServiceContainer()
    {
        return m_serviceContainer;
    }

    /**
     * This method sets the service container where the data is being retrieved from.
     * 
     * @param serviceContainer The service container where the data is being retrieved from.
     */
    public void setServiceContainer(String serviceContainer)
    {
        m_serviceContainer = serviceContainer;
    }

    /**
     * This method gets the current hostname it is grabbing data from.
     * 
     * @return The current hostname it is grabbing data from.
     */
    public String getHost()
    {
        return m_host;
    }

    /**
     * This method sets the current hostname it is grabbing data from.
     * 
     * @param host The current hostname it is grabbing data from.
     */
    public void setHost(String host)
    {
        m_host = host;
    }

    /**
     * This method gets the current progress.
     * 
     * @return The current progress.
     */
    public int getProgress()
    {
        return m_progress;
    }

    /**
     * This method sets the current progress.
     * 
     * @param progress The current progress.
     */
    public void setProgress(int progress)
    {
        m_progress = progress;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(1024);

        if (StringUtils.isSet(m_host))
        {
            sb.append("Host ").append(m_host).append(" / ");
        }

        if (StringUtils.isSet(m_serviceContainer))
        {
            sb.append("SC ").append(m_serviceContainer).append(": ");
        }

        if (StringUtils.isSet(m_detail))
        {
            sb.append(m_detail);
        }

        return sb.toString();
    }
}
