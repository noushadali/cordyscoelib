package com.cordys.coe.util.cgc.config;

import org.w3c.dom.Node;

/**
 * Implementation class for the SSO based authentication.
 *
 * @author  pgussow
 */
class SSOAuthentication
    implements ISSOAuthentication
{
    /**
     * Holds the password for this user.
     */
    private String m_sPassword;
    /**
     * Holds the username to use.
     */
    private String m_sUsername;
    /**
     * Holds the current SAML token.
     */
    private Node m_nSAMLToken;

    /**
     * This method gets the password for this user.
     *
     * @return  The password for this user.
     *
     * @see     com.cordys.coe.util.cgc.config.ISSOAuthentication#getPassword()
     */
    public String getPassword()
    {
        return m_sPassword;
    }

    /**
     * This method gets the username to use.
     *
     * @return  The username to use.
     *
     * @see     com.cordys.coe.util.cgc.config.ISSOAuthentication#getUsername()
     */
    public String getUsername()
    {
        return m_sUsername;
    }

    /**
     * This method sets the password for this user.
     *
     * @param  sPassword  The password for this user.
     *
     * @see    com.cordys.coe.util.cgc.config.ISSOAuthentication#setPassword(java.lang.String)
     */
    public void setPassword(String sPassword)
    {
        m_sPassword = sPassword;
    }

    /**
     * This method sets the username to use.
     *
     * @param  sUsername  The username to use.
     *
     * @see    com.cordys.coe.util.cgc.config.ISSOAuthentication#setUsername(java.lang.String)
     */
    public void setUsername(String sUsername)
    {
        m_sUsername = sUsername;
    }

    /**
     * This method gets the SAML token for the given user.
     *
     * @return  The SAML token for the given user.
     *
     * @see com.cordys.coe.util.cgc.config.ISSOAuthentication#getSAMLToken()
     */
    public Node getSAMLToken()
    {
        return m_nSAMLToken;
    }

    /**
     * This method sets the SAML token for the given user.
     *
     * @param  nSAMLToken  The SAML token for the given user.
     *
     * @see com.cordys.coe.util.cgc.config.ISSOAuthentication#setSAMLToken(org.w3c.dom.Node)
     */
    public void setSAMLToken(Node nSAMLToken)
    {
        m_nSAMLToken = nSAMLToken;
    }
    
    
}
