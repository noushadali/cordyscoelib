package com.cordys.coe.tools.snapshot.data.handler;

import com.cordys.coe.tools.snapshot.data.Constants;

import javax.xml.bind.annotation.XmlElement;

/**
 * Holds the information parsed from the Runtime name of the JVM.
 *
 * @author  localpg
 */
public class HostPIDInfo
    implements ICustomDataHandler
{
    /**
     * Holds the process ID.
     */
    private int m_processID;
    /**
     * Holds the actual server name on which the process is running.
     */
    private String m_host;

    /**
     * This method gets the actual server name on which the process is running.
     *
     * @return  The actual server name on which the process is running.
     */
    @XmlElement(name = "Host", namespace = Constants.NS)
    public String getHost()
    {
        return m_host;
    }

    /**
     * This method sets the actual server name on which the process is running.
     *
     * @param  host  The actual server name on which the process is running.
     */
    public void setHost(String host)
    {
        m_host = host;
    }

    /**
     * This method gets the process ID.
     *
     * @return  The process ID.
     */
    @XmlElement(name = "ProcessID", namespace = Constants.NS)
    public int getProcessID()
    {
        return m_processID;
    }

    /**
     * This method sets the process ID.
     *
     * @param  processID  The process ID.
     */
    public void setProcessID(int processID)
    {
        m_processID = processID;
    }

    /**
     * @see  com.cordys.coe.tools.snapshot.data.handler.ICustomDataHandler#parse(java.lang.Object)
     */
    @Override public void parse(Object value)
    {
        if (value != null)
        {
            String temp = value.toString();
            String[] tmp = temp.split("@");

            if (tmp.length == 2)
            {
                setProcessID(Integer.parseInt(tmp[0]));
                setHost(tmp[1]);
            }
        }
    }
}
