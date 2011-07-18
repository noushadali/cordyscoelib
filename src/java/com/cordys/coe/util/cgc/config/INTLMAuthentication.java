package com.cordys.coe.util.cgc.config;

/**
 * This class is usedfor NTLM based authentication.
 *
 * @author  pgussow
 */
public interface INTLMAuthentication extends IUsernamePasswordAuthentication
{
    /**
     * This method gets the domain for the given user.
     *
     * @return  The domain for the given user.
     */
    String getDomain();

    /**
     * This method sets the domain for the given user.
     *
     * @param  sDomain  The domain for the given user.
     */
    void setDomain(String sDomain);
}
