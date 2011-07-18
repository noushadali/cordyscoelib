package com.cordys.coe.util.cgc.config;

/**
 * Holds the details for Cordys Custom authentication.
 *
 * @author  pgussow
 */
public interface ICordysCustomAuthentication extends IUsernamePasswordAuthentication
{
    /**
     * This method gets the current session ID.
     *
     * @return  The current session ID.
     */
    String getWCPSessionID();

    /**
     * This method sets the current session ID.
     *
     * @param  sWCPSessionID  The current session ID.
     */
    void setWCPSessionID(String sWCPSessionID);
}
