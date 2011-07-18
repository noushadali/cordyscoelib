package com.cordys.coe.util.config;

/**
 * This interface describes the native connection properties.
 *
 * @author  pgussow
 */
public interface INativeConfiguration extends IConfiguration
{
    /**
     * This method gets the ldap password.
     *
     * @return  The ldap password.
     */
    String getLDAPPassword();

    /**
     * This method gets the LDAP search root.
     *
     * @return  The LDAP search root.
     */
    String getLDAPSearchRoot();

    /**
     * This method gets the LDAP user name.
     *
     * @return  The LDAP user name.
     */
    String getLDAPUsername();

    /**
     * This method gets whether or not SSL is enabled.
     *
     * @return  Whether or not SSL is enabled.
     */
    boolean isSSL();

    /**
     * This method sets the ldap password.
     *
     * @param  sLDAPPassword  The ldap password.
     */
    void setLDAPPassword(String sLDAPPassword);

    /**
     * This method sets the LDAP search root.
     *
     * @param  sLDAPSearchRoot  The LDAP search root.
     */
    void setLDAPSearchRoot(String sLDAPSearchRoot);

    /**
     * This method sets the LDAP user name.
     *
     * @param  sLDAPUsername  The LDAP user name.
     */
    void setLDAPUsername(String sLDAPUsername);

    /**
     * This method sets the name for the configuration.
     *
     * @param  sName  The name for the configuration.
     */
    void setName(String sName);

    /**
     * This method sets the port on which the LDAP server is running.
     *
     * @param  iLDAPPort  The port on which the LDAP server is running.
     */
    void setPort(int iLDAPPort);

    /**
     * This method sets the LDAP server name.
     *
     * @param  sLDAPServer  The LDAP server name.
     */
    void setServername(String sLDAPServer);

    /**
     * This method sets whether or not SSL is enabled.
     *
     * @param  bSSL  Whether or not SSL is enabled.
     */
    void setSSL(boolean bSSL);

    /**
     * This method gets the LDAP keystore containing the LDAP server's certificate.
     *
     * @return  The LDAP keystore containing the LDAP server's certificate.
     */
    String getLDAPKeystore();

    /**
     * This method gets the password for the keystore holding the certificate of the LDAP server.
     *
     * @return  The password for the keystore holding the certificate of the LDAP server.
     */
    String getLDAPKeystorePassword();

    /**
     * This method sets the LDAP keystore containing the LDAP server's certificate.
     *
     * @param  sLDAPKeystore  The LDAP keystore containing the LDAP server's certificate.
     */
    void setLDAPKeystore(String sLDAPKeystore);

    /**
     * This method sets the password for the keystore holding the certificate of the LDAP server.
     *
     * @param  sLDAPKeystorePassword  The password for the keystore holding the certificate of the
     *                                LDAP server.
     */
    void setLDAPKeystorePassword(String sLDAPKeystorePassword);
}
