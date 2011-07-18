package com.cordys.coe.util.config.impl;

import com.cordys.coe.util.config.ConfigurationFactory;
import com.cordys.coe.util.config.INativeConfiguration;

import org.w3c.dom.Element;

/**
 * This class wraps around the configuration details for a native connection.
 *
 * @author  pgussow
 */
public class NativeConfiguration extends AbstractConfiguration
    implements INativeConfiguration
{
    /**
     * Holds the name of the tag holding the ldapserver.
     */
    private static final String TAG_LDAP_SERVER = "ldapserver";
    /**
     * Holds the name of the tag holding the ldap port.
     */
    private static final String TAG_LDAP_PORT = "ldapport";
    /**
     * Holds the name of the tag holding the ldap username.
     */
    private static final String TAG_LDAP_USERNAME = "ldapusername";
    /**
     * Holds the name of the tag holding the ldap password.
     */
    private static final String TAG_LDAP_PASSWORD = "ldappassword";
    /**
     * Holds the name of the tag holding the ldap keystore.
     */
    private static final String TAG_LDAP_KEYSTORE = "ldapkeystore";
    /**
     * Holds the name of the tag holding the ldap keystore password.
     */
    private static final String TAG_LDAP_KEYSTORE_PASSWORD = "ldapkeystorepassword";
    /**
     * Holds the name of the tag holding whether or not the LDAP is running in SSL mode.
     */
    private static final String TAG_LDAP_SSL = "ldapssl";
    /**
     * Holds the name of the tag holding the ldap searchroot.
     */
    private static final String TAG_LDAP_SEARCHROOT = "ldapsearchroot";

    /**
     * Default constructor.
     */
    public NativeConfiguration()
    {
        super();
    }

    /**
     * Creates a new WebGatewayConfiguration object.
     *
     * @param  eConfigNode  The configuration XML.
     */
    public NativeConfiguration(Element eConfigNode)
    {
        super(eConfigNode);
    }

    /**
     * This method gets the LDAP keystore containing the LDAP server's certificate.
     *
     * @return  The LDAP keystore containing the LDAP server's certificate.
     *
     * @see     com.cordys.coe.util.config.INativeConfiguration#getLDAPKeystore()
     */
    public String getLDAPKeystore()
    {
        return getStringValue(TAG_LDAP_KEYSTORE, "");
    }

    /**
     * This method gets the password for the keystore holding the certificate of the LDAP server.
     *
     * @return  The password for the keystore holding the certificate of the LDAP server.
     *
     * @see     com.cordys.coe.util.config.INativeConfiguration#getLDAPKeystorePassword()
     */
    public String getLDAPKeystorePassword()
    {
        return getStringValue(TAG_LDAP_KEYSTORE_PASSWORD, "");
    }

    /**
     * This method gets the ldap password.
     *
     * @return  The ldap password.
     *
     * @see     com.cordys.coe.util.config.INativeConfiguration#getLDAPPassword()
     */
    public String getLDAPPassword()
    {
        return getStringValue(TAG_LDAP_PASSWORD, "");
    }

    /**
     * This method gets the LDAP search root.
     *
     * @return  The LDAP search root.
     *
     * @see     com.cordys.coe.util.config.INativeConfiguration#getLDAPSearchRoot()
     */
    public String getLDAPSearchRoot()
    {
        return getStringValue(TAG_LDAP_SEARCHROOT, "");
    }

    /**
     * This method gets the LDAP user name.
     *
     * @return  The LDAP user name.
     *
     * @see     com.cordys.coe.util.config.INativeConfiguration#getLDAPUsername()
     */
    public String getLDAPUsername()
    {
        return getStringValue(TAG_LDAP_USERNAME, "");
    }

    /**
     * This method gets the port number to use.
     *
     * @return  The port number to use.
     *
     * @see     com.cordys.coe.util.config.IConfiguration#getPort()
     */
    public int getPort()
    {
        return getIntegerValue(TAG_LDAP_PORT);
    }

    /**
     * This method gets the server name.
     *
     * @return  The server name.
     *
     * @see     com.cordys.coe.util.config.IConfiguration#getServername()
     */
    public String getServername()
    {
        return getStringValue(TAG_LDAP_SERVER, "");
    }

    /**
     * This method gets the type of configuration (WebGateway or native).
     *
     * @return  The type of configuration (WebGateway or native).
     *
     * @see     com.cordys.coe.util.config.IConfiguration#getType()
     */
    public int getType()
    {
        return TYPE_NATIVE;
    }

    /**
     * This method gets whether or not SSL is enabled.
     *
     * @return  Whether or not SSL is enabled.
     *
     * @see     com.cordys.coe.util.config.INativeConfiguration#isSSL()
     */
    public boolean isSSL()
    {
        return getBooleanValue(TAG_LDAP_SSL);
    }

    /**
     * This method sets the LDAP keystore containing the LDAP server's certificate.
     *
     * @param  sLDAPKeystore  The LDAP keystore containing the LDAP server's certificate.
     *
     * @see    com.cordys.coe.util.config.INativeConfiguration#setLDAPKeystore(java.lang.String)
     */
    public void setLDAPKeystore(String sLDAPKeystore)
    {
        setValue(TAG_LDAP_KEYSTORE, sLDAPKeystore);
    }

    /**
     * This method sets the password for the keystore holding the certificate of the LDAP server.
     *
     * @param  sLDAPKeystorePassword  The password for the keystore holding the certificate of the
     *                                LDAP server.
     *
     * @see    com.cordys.coe.util.config.INativeConfiguration#setLDAPKeystorePassword(java.lang.String)
     */
    public void setLDAPKeystorePassword(String sLDAPKeystorePassword)
    {
        setValue(TAG_LDAP_KEYSTORE_PASSWORD, sLDAPKeystorePassword);
    }

    /**
     * This method sets the ldap password.
     *
     * @param  sLDAPPassword  The ldap password.
     *
     * @see    com.cordys.coe.util.config.INativeConfiguration#setLDAPPassword(java.lang.String)
     */
    public void setLDAPPassword(String sLDAPPassword)
    {
        setValue(TAG_LDAP_PASSWORD, sLDAPPassword);
    }

    /**
     * This method sets the LDAP search root.
     *
     * @param  sLDAPSearchRoot  The LDAP search root.
     *
     * @see    com.cordys.coe.util.config.INativeConfiguration#setLDAPSearchRoot(java.lang.String)
     */
    public void setLDAPSearchRoot(String sLDAPSearchRoot)
    {
        setValue(TAG_LDAP_SEARCHROOT, sLDAPSearchRoot);
    }

    /**
     * This method sets the LDAP user name.
     *
     * @param  sLDAPUsername  The LDAP user name.
     *
     * @see    com.cordys.coe.util.config.INativeConfiguration#setLDAPUsername(java.lang.String)
     */
    public void setLDAPUsername(String sLDAPUsername)
    {
        setValue(TAG_LDAP_USERNAME, sLDAPUsername);
    }

    /**
     * This method sets the port on which the LDAP server is running.
     *
     * @param  iLDAPPort  The port on which the LDAP server is running.
     *
     * @see    com.cordys.coe.util.config.INativeConfiguration#setPort(int)
     */
    public void setPort(int iLDAPPort)
    {
        setValue(TAG_LDAP_PORT, new Integer(iLDAPPort));
    }

    /**
     * This method sets the LDAP server name.
     *
     * @param  sLDAPServer  The LDAP server name.
     *
     * @see    com.cordys.coe.util.config.INativeConfiguration#setServername(java.lang.String)
     */
    public void setServername(String sLDAPServer)
    {
        setValue(TAG_LDAP_SERVER, sLDAPServer);
    }

    /**
     * This method sets whether or not SSL is enabled.
     *
     * @param  bSSL  Whether or not SSL is enabled.
     *
     * @see    com.cordys.coe.util.config.INativeConfiguration#setSSL(boolean)
     */
    public void setSSL(boolean bSSL)
    {
        setValue(TAG_LDAP_SSL, new Boolean(bSSL));
    }

    /**
     * This method writes the values to XML and appends the XML to the passed on root node.
     *
     * @param   eParent  The parent node.
     *
     * @return  The XML structure
     *
     * @see     com.cordys.coe.util.config.IConfiguration#toXMLStructure(org.w3c.dom.Element)
     */
    public Element toXMLStructure(Element eParent)
    {
        Element eReturn = super.toXML(eParent);
        eReturn.setAttribute("name", getName());
        eReturn.setAttribute("type", ConfigurationFactory.TYPE_NATIVE);

        return eReturn;
    }

    /**
     * This method registers all keys.
     *
     * @see  com.cordys.coe.util.xml.dom.XMLProperties#initializeKeys()
     */
    @Override protected void initializeKeys()
    {
        super.initializeKeys();
        registerKey(TAG_LDAP_SERVER);
        registerKey(TAG_LDAP_USERNAME);
        registerKey(TAG_LDAP_PORT);
        registerKey(TAG_LDAP_SSL);
        registerKey(TAG_LDAP_SEARCHROOT);
        registerKey(TAG_LDAP_PASSWORD);
    }
}
