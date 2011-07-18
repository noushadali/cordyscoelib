package com.cordys.coe.util.cgc.config;

/**
 * This class contains the configuration for a username/password based authentication.
 *
 * @author  pgussow
 */
class UsernamePasswordAuthentication
    implements IUsernamePasswordAuthentication
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
     * This method gets the password for this user.
     *
     * @return  The password for this user.
     *
     * @see     com.cordys.coe.util.cgc.config.IUsernamePasswordAuthentication#getPassword()
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
     * @see     com.cordys.coe.util.cgc.config.IUsernamePasswordAuthentication#getUsername()
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
     * @see    com.cordys.coe.util.cgc.config.IUsernamePasswordAuthentication#setPassword(java.lang.String)
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
     * @see    com.cordys.coe.util.cgc.config.IUsernamePasswordAuthentication#setUsername(java.lang.String)
     */
    public void setUsername(String sUsername)
    {
        m_sUsername = sUsername;
    }
}
