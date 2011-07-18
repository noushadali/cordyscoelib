package com.cordys.coe.util.cgc.config;

/**
 * This interface contains the details for a username/password authentication. This class can be
 * used for Basic, NTLM and Digest authentication.
 *
 * @author  pgussow
 */
public interface IUsernamePasswordAuthentication extends IAuthenticationConfiguration
{
    /**
     * This method gets the password for this user.
     *
     * @return  The password for this user.
     */

    String getPassword();

    /**
     * This method gets the username to use.
     *
     * @return  The username to use.
     */
    String getUsername();

    /**
     * This method sets the password for this user.
     *
     * @param  sPassword  The password for this user.
     */
    void setPassword(String sPassword);

    /**
     * This method sets the username to use.
     *
     * @param  sUsername  The username to use.
     */
    void setUsername(String sUsername);
}
