package com.cordys.coe.util.cgc.config;

/**
 * This class holds the details when using Cordys Custom Authentication.
 *
 * @author  pgussow
 */
public class CordysCustomAuthenticationImpl extends UsernamePasswordAuthentication
    implements ICordysCustomAuthentication
{
    /**
     * Holds the current WCP session ID.
     */
    private String m_sWCPSessionID;

    /**
     * This method gets the current session ID.
     *
     * @return  The current session ID.
     *
     * @see     com.cordys.coe.util.cgc.config.ICordysCustomAuthentication#getWCPSessionID()
     */
    public String getWCPSessionID()
    {
        return m_sWCPSessionID;
    }

    /**
     * This method sets the current session ID.
     *
     * @param  sWCPSessionID  The current session ID
     *
     * @see    com.cordys.coe.util.cgc.config.ICordysCustomAuthentication#setWCPSessionID(java.lang.String)
     */
    public void setWCPSessionID(String sWCPSessionID)
    {
        m_sWCPSessionID = sWCPSessionID;
    }
}
