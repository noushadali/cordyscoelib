package com.cordys.coe.util.cgc.config;

/**
 * The authentication class for NTLM based authentication.
 *
 * @author  pgussow
 */
class NTLMAuthentication extends UsernamePasswordAuthentication
    implements INTLMAuthentication
{
    /**
     * Holds the domain for the given user.
     */
    private String m_sDomain;

    /**
     * This method gets the domain for the given user.
     *
     * @return  The domain for the given user.
     *
     * @see     com.cordys.coe.util.cgc.config.INTLMAuthentication#getDomain()
     */
    public String getDomain()
    {
        return m_sDomain;
    }

    /**
     * This method sets the domain for the given user.
     *
     * @param  sDomain  The domain for the given user.
     *
     * @see    com.cordys.coe.util.cgc.config.INTLMAuthentication#setDomain(java.lang.String)
     */
    public void setDomain(String sDomain)
    {
        m_sDomain = sDomain;
    }
}
