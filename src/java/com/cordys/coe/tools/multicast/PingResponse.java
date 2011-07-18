package com.cordys.coe.tools.multicast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DOCUMENTME.
 *
 * @author  pgussow
 */
public class PingResponse
{
    /**
     * DOCUMENTME.
     */
    private double m_dTime;
    /**
     * DOCUMENTME.
     */
    private int m_iSequence;
    /**
     * DOCUMENTME.
     */
    private int m_iTTL;
    /**
     * DOCUMENTME.
     */
    private String m_sIP;

    /**
     * Creates a new PingResponse object.
     *
     * @param  sLine  DOCUMENTME
     */
    public PingResponse(String sLine)
    {
        Matcher m = Pattern.compile("64 bytes from ([0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}):[\\W]+icmp_seq=([0-9]+)[\\W]+ttl=([0-9]+)[\\W]+time=([0-9.]+)[\\W]+ms([\\W]*\\(DUP!\\)){0,1}")
                           .matcher(sLine);

        if (m.find())
        {
            m_sIP = m.group(1);
            m_iSequence = Integer.parseInt(m.group(2));
            m_iTTL = Integer.parseInt(m.group(3));
            m_dTime = Double.parseDouble(m.group(4));
        }
    }

    /**
     * This method gets the IP address of the response.
     *
     * @return  The IP address of the response.
     */
    public String getIP()
    {
        return m_sIP;
    }

    /**
     * This method gets the response time.
     *
     * @return  The response time.
     */
    public double getResponseTime()
    {
        return m_dTime;
    }

    /**
     * This method gets the sequence of the packet.
     *
     * @return  The sequence of the packet.
     */
    public int getSequence()
    {
        return m_iSequence;
    }

    /**
     * This method gets the TTL.
     *
     * @return  The TTL.
     */
    public int getTTL()
    {
        return m_iTTL;
    }

    /**
     * This method sets the IP address of the response.
     *
     * @param  sIP  The IP address of the response.
     */
    public void setIP(String sIP)
    {
        m_sIP = sIP;
    }

    /**
     * This method sets the response time.
     *
     * @param  dTime  The response time.
     */
    public void setResponseTime(double dTime)
    {
        m_dTime = dTime;
    }

    /**
     * This method sets the sequence of the packet.
     *
     * @param  iSequence  The sequence of the packet.
     */
    public void setSequence(int iSequence)
    {
        m_iSequence = iSequence;
    }

    /**
     * This method sets the TTL.
     *
     * @param  iTTL  m_variable The TTL.
     */
    public void setTTL(int iTTL)
    {
        m_iTTL = iTTL;
    }

    /**
     * This method returns the string representation of the object.
     *
     * @return  The string representation of the object.
     *
     * @see     java.lang.Object#toString()
     */
    @Override public String toString()
    {
        StringBuilder sbReturn = new StringBuilder();

        sbReturn.append(getIP() + " - TTL: " + getTTL() + " - Seq: " + getSequence() + " - Time: " +
                        m_dTime);

        return sbReturn.toString();
    }
}
