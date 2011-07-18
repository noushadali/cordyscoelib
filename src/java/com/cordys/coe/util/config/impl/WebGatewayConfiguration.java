package com.cordys.coe.util.config.impl;

import com.cordys.coe.util.cgc.config.EAuthenticationType;
import com.cordys.coe.util.cgc.config.ETrustMode;
import com.cordys.coe.util.config.ConfigurationFactory;
import com.cordys.coe.util.config.ConfigurationManagerException;
import com.cordys.coe.util.config.IWebGatewayConfiguration;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class wraps around the web gateway type of communication.
 *
 * @author  pgussow
 */
public class WebGatewayConfiguration extends AbstractConfiguration
    implements IWebGatewayConfiguration
{
    /**
     * Holds the name of the configuration.
     */
    private String m_sName;

    /**
     * Default constructor.
     */
    public WebGatewayConfiguration()
    {
        super();
    }

    /**
     * Creates a new WebGatewayConfiguration object.
     *
     * @param  eConfigNode  The configuration XML.
     */
    public WebGatewayConfiguration(Element eConfigNode)
    {
        super(eConfigNode);
    }

    /**
     * This method checks if the configuration is valid. This is based on several criteria and
     * depending on the type of connection.
     *
     * @throws  ConfigurationManagerException  In case the configuration is not valid.
     *
     * @see     com.cordys.coe.util.config.IWebGatewayConfiguration#checkValidity()
     */
    public void checkValidity()
                       throws ConfigurationManagerException
    {
        if ((getServername() == null) || (getServername().length() == 0))
        {
            throw new ConfigurationManagerException(ConfigurationManagerException.EC_CONFIGURATION,
                                                    "Missing required field: Server name");
        }

        if ((getPort() <= 0) || (getPort() > 65535))
        {
            throw new ConfigurationManagerException(ConfigurationManagerException.EC_CONFIGURATION,
                                                    "Invalid server port: " + getPort());
        }

        // Check authentication type specific entries.
        switch (getAuthenticationType())
        {
            case NTLM:
                if ((getDomain() == null) || (getDomain().length() == 0))
                {
                    throw new ConfigurationManagerException(ConfigurationManagerException.EC_CONFIGURATION,
                                                            "Missing required field: NT domain");
                }
                // Note: Intentional no break statement. Check the username as well.

            case BASIC:
            case CORDYS_CUSTOM:
            case SSO:
                if ((getDomainUsername() == null) || (getDomainUsername().length() == 0))
                {
                    throw new ConfigurationManagerException(ConfigurationManagerException.EC_CONFIGURATION,
                                                            "Missing required field: Username");
                }
                break;

            case CLIENT_CERTIFICATE:
                if ((getCertificateLocation() == null) || (getCertificateLocation().length() == 0))
                {
                    throw new ConfigurationManagerException(ConfigurationManagerException.EC_CONFIGURATION,
                                                            "Missing required field: Certificate location");
                }
                if ((getCertificateType() == null) || (getCertificateType().length() == 0))
                {
                    throw new ConfigurationManagerException(ConfigurationManagerException.EC_CONFIGURATION,
                                                            "Missing required field: Certificate type");
                }
                break;
        }

        if (getSSL())
        {
            // Check the trustmode
            switch (getSSLTrustMode())
            {
                case USE_TRUSTORE:
                    if ((getTrustStoreLocation() == null) ||
                            (getTrustStoreLocation().length() == 0))
                    {
                        throw new ConfigurationManagerException(ConfigurationManagerException.EC_CONFIGURATION,
                                                                "Missing required field: Trust store location");
                    }
                    if ((getTrustStoreType() == null) || (getTrustStoreType().length() == 0))
                    {
                        throw new ConfigurationManagerException(ConfigurationManagerException.EC_CONFIGURATION,
                                                                "Missing required field: Trust store type");
                    }
                    break;

                case TRUST_EVERY_SERVER:
                    // Nothing to do for this case
            }
        }

        // If a proxy server is configured.
        if ((getProxyHost() != null) && (getProxyHost().length() > 0))
        {
            if ((getProxyPort() <= 0) || (getProxyPort() > 65535))
            {
                throw new ConfigurationManagerException(ConfigurationManagerException.EC_CONFIGURATION,
                                                        "Invalid proxy port: " + getPort());
            }
        }
    }

    /**
     * This method gets whether or not the server certificate will be accepted even though it has
     * expired or is not yet valid.
     *
     * @return  Whether or not the server certificate will be accepted even though it has expired or
     *          is not yet valid.
     *
     * @see     com.cordys.coe.util.config.IWebGatewayConfiguration#getAcceptWhenExpired()
     */
    public boolean getAcceptWhenExpired()
    {
        return getBooleanValue(TAG_ACCEPT_WHEN_EXPIRED);
    }

    /**
     * This method gets whether or not the server certificate is always accepted even though it's
     * invalid.
     *
     * @return  Whether or not the server certificate is always accepted even though it's invalid.
     *
     * @see     com.cordys.coe.util.config.IWebGatewayConfiguration#getAcceptWhenInvalid()
     */
    public boolean getAcceptWhenInvalid()
    {
        return getBooleanValue(TAG_ACCEPT_WHEN_INVALID);
    }

    /**
     * This method gets the authentication type for this configuration.
     *
     * @return  The authentication type for this configuration.
     *
     * @see     com.cordys.coe.util.config.IWebGatewayConfiguration#getAuthenticationType()
     */
    public EAuthenticationType getAuthenticationType()
    {
        String sTemp = getStringValue(TAG_AUTH_TYPE, EAuthenticationType.NTLM.name());

        return EAuthenticationType.valueOf(sTemp);
    }

    /**
     * This method gets the certificate location.
     *
     * @return  The certificate location.
     */
    public String getCertificateLocation()
    {
        return getStringValue(TAG_CA_CERT_LOC, "");
    }

    /**
     * This method gets the certificate password.
     *
     * @return  The certificate password.
     */
    public String getCertificatePassword()
    {
        return getStringValue(TAG_CA_CERT_PWD, "");
    }

    /**
     * This method gets the certificate type.
     *
     * @return  The certificate type.
     */
    public String getCertificateType()
    {
        return getStringValue(TAG_CA_CERT_TYPE, "");
    }

    /**
     * This method gets the actual domain.
     *
     * @return  The actual domain.
     */
    public String getDomain()
    {
        return getStringValue(TAG_DA_DOMAIN, "");
    }

    /**
     * This method gets the domain password.
     *
     * @return  The domain password.
     */
    public String getDomainPassword()
    {
        return getStringValue(TAG_DA_PASSWORD, "");
    }

    /**
     * This method gets the domain user.
     *
     * @return  The domain user.
     */
    public String getDomainUsername()
    {
        return getStringValue(TAG_DA_USERNAME, "");
    }

    /**
     * This method gets the gateway URL to use.
     *
     * @return  The gateway URL to use.
     */
    public String getGatewayURL()
    {
        return getStringValue(TAG_GATEWAY_URL, "");
    }

    /**
     * Returns the network TCP/IP timeout to be used for requests.
     *
     * @return  Network timeout.
     *
     * @see     com.cordys.coe.util.config.IWebGatewayConfiguration#getNetworkTimeout()
     */
    public long getNetworkTimeout()
    {
        return getLongValue(TAG_NETWORK_TIMEOUT, -1);
    }

    /**
     * This method gets the port number to use.
     *
     * @return  The port number to use.
     */
    public int getPort()
    {
        return getIntegerValue(TAG_PORT);
    }

    /**
     * This method gets the proxy host to use.
     *
     * @return  The proxy host to use.
     *
     * @see     com.cordys.coe.util.config.IWebGatewayConfiguration#getProxyHost()
     */
    public String getProxyHost()
    {
        return getStringValue(TAG_PROXY_HOST);
    }

    /**
     * This method gets the password for the proxy user.
     *
     * @return  The password for the proxy user.
     *
     * @see     com.cordys.coe.util.config.IWebGatewayConfiguration#getProxyPassword()
     */
    public String getProxyPassword()
    {
        return getStringValue(TAG_PROXY_PASSWORD);
    }

    /**
     * This method gets the proxy port to use.
     *
     * @return  The proxy port to use.
     *
     * @see     com.cordys.coe.util.config.IWebGatewayConfiguration#getProxyPort()
     */
    public int getProxyPort()
    {
        return getIntegerValue(TAG_PROXY_PORT);
    }

    /**
     * This method gets the username for the proxy server.
     *
     * @return  The username for the proxy server.
     *
     * @see     com.cordys.coe.util.config.IWebGatewayConfiguration#getProxyUsername()
     */
    public String getProxyUsername()
    {
        return getStringValue(TAG_PROXY_USERNAME);
    }

    /**
     * This method gets the server name.
     *
     * @return  The server name.
     */
    public String getServername()
    {
        return getStringValue(TAG_SERVER_NAME, "");
    }

    /**
     * This method gets whether or not the server is running under SSL.
     *
     * @return  Whether or not the server is running under SSL.
     *
     * @see     com.cordys.coe.util.config.IWebGatewayConfiguration#getSSL()
     */
    public boolean getSSL()
    {
        return getBooleanValue(TAG_SSL);
    }

    /**
     * This method gets the mode of trust that should be used (trust everything or use a trust
     * store.
     *
     * @return  The mode of trust that should be used (trust everything or use a trust store.
     *
     * @see     com.cordys.coe.util.config.IWebGatewayConfiguration#getSSLTrustMode()
     */
    public ETrustMode getSSLTrustMode()
    {
        String sTemp = getStringValue(TAG_TRUST_MODE, ETrustMode.TRUST_EVERY_SERVER.name());

        return ETrustMode.valueOf(sTemp);
    }

    /**
     * This method gets the trust store location.
     *
     * @return  The trust store location.
     */
    public String getTrustStoreLocation()
    {
        return getStringValue(TAG_CA_TRUST_LOC, "");
    }

    /**
     * This method gets the trust store password.
     *
     * @return  The trust store password.
     */
    public String getTrustStorePassword()
    {
        return getStringValue(TAG_CA_TRUST_PWD, "");
    }

    /**
     * This method gets the trust store type.
     *
     * @return  The trust store type.
     */
    public String getTrustStoreType()
    {
        return getStringValue(TAG_CA_TRUST_TYPE, "");
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
        return TYPE_WEBGATEWAY;
    }

    /**
     * This method returns whether or not the current configuration is valid.
     *
     * @return  true if the configuration is valid. Otherwise false.
     *
     * @see     com.cordys.coe.util.config.IWebGatewayConfiguration#isValid()
     */
    public boolean isValid()
    {
        boolean bReturn = true;

        try
        {
            checkValidity();
        }
        catch (ConfigurationManagerException e)
        {
            bReturn = false;
        }

        return bReturn;
    }

    /**
     * This method sets whether or not the server certificate will be accepted even though it has
     * expired or is not yet valid.
     *
     * @param  bAcceptWhenExpired  Whether or not the server certificate will be accepted even
     *                             though it has expired or is not yet valid.
     *
     * @see    com.cordys.coe.util.config.IWebGatewayConfiguration#setAcceptWhenExpired(boolean)
     */
    public void setAcceptWhenExpired(boolean bAcceptWhenExpired)
    {
        setValue(TAG_ACCEPT_WHEN_EXPIRED, bAcceptWhenExpired);
    }

    /**
     * This method sets wether or not the server certificate is always accepted even though it's
     * invalid.
     *
     * @param  bAcceptWhenInvalid  Whether or not the server certificate is always accepted even
     *                             though it's invalid.
     *
     * @see    com.cordys.coe.util.config.IWebGatewayConfiguration#setAcceptWhenInvalid(boolean)
     */
    public void setAcceptWhenInvalid(boolean bAcceptWhenInvalid)
    {
        setValue(TAG_ACCEPT_WHEN_INVALID, bAcceptWhenInvalid);
    }

    /**
     * This method sets the authentication type for this configuration.
     *
     * @param  atAuthenticationType  The authentication type for this configuration.
     *
     * @see    com.cordys.coe.util.config.IWebGatewayConfiguration#setAuthenticationType(com.cordys.coe.util.cgc.config.EAuthenticationType)
     */
    public void setAuthenticationType(EAuthenticationType atAuthenticationType)
    {
        setValue(TAG_AUTH_TYPE, atAuthenticationType.name());
    }

    /**
     * This method sets the certificate location.
     *
     * @param  sCertificateLocation  The certificate location.
     */
    public void setCertificateLocation(String sCertificateLocation)
    {
        setValue(TAG_CA_CERT_LOC, sCertificateLocation);
    }

    /**
     * This method sets the certificate password.
     *
     * @param  sCertificatePassword  The certificate password.
     */
    public void setCertificatePassword(String sCertificatePassword)
    {
        setValue(TAG_CA_CERT_PWD, sCertificatePassword);
    }

    /**
     * This method sets the certificate type.
     *
     * @param  sCertificateType  The certificate type.
     */
    public void setCertificateType(String sCertificateType)
    {
        setValue(TAG_CA_CERT_TYPE, sCertificateType);
    }

    /**
     * This method sets the actual domain.
     *
     * @param  sDomain  The actual domain.
     */
    public void setDomain(String sDomain)
    {
        setValue(TAG_DA_DOMAIN, sDomain);
    }

    /**
     * This method sets the domain password.
     *
     * @param  sDomainPassword  The domain password.
     */
    public void setDomainPassword(String sDomainPassword)
    {
        setValue(TAG_DA_PASSWORD, sDomainPassword);
    }

    /**
     * This method sets the domain user.
     *
     * @param  sDomainUsername  The domain user.
     */
    public void setDomainUsername(String sDomainUsername)
    {
        setValue(TAG_DA_USERNAME, sDomainUsername);
    }

    /**
     * This method sets the gateway URL to use.
     *
     * @param  sGatewayURL  The gateway URL to use.
     */
    public void setGatewayURL(String sGatewayURL)
    {
        setValue(TAG_GATEWAY_URL, sGatewayURL);
    }

    /**
     * Sets the network TCP/IP timeout to be used for requests. This is separate from the Cordys
     * timeout URL parameter.
     *
     * @param  lNetworkTimeout  Network timeout value (-1 means infinite wait).
     *
     * @see    com.cordys.coe.util.config.IWebGatewayConfiguration#setNetworkTimeout(long)
     */
    public void setNetworkTimeout(long lNetworkTimeout)
    {
        setValue(TAG_NETWORK_TIMEOUT, lNetworkTimeout);
    }

    /**
     * This method sets the port number to use.
     *
     * @param  iPort  The port number to use.
     */
    public void setPort(int iPort)
    {
        setValue(TAG_PORT, new Integer(iPort));
    }

    /**
     * This method sets the proxy host to use.
     *
     * @param  sProxyHost  The proxy host to use.
     *
     * @see    com.cordys.coe.util.config.IWebGatewayConfiguration#setProxyHost(java.lang.String)
     */
    public void setProxyHost(String sProxyHost)
    {
        setValue(TAG_PROXY_HOST, sProxyHost);
    }

    /**
     * This method sets the password for the proxy user.
     *
     * @param  sProxyPassword  The password for the proxy user.
     *
     * @see    com.cordys.coe.util.config.IWebGatewayConfiguration#setProxyPassword(java.lang.String)
     */
    public void setProxyPassword(String sProxyPassword)
    {
        setValue(TAG_PROXY_PASSWORD, sProxyPassword);
    }

    /**
     * This method sets the proxy port to use.
     *
     * @param  iProxyPort  The proxy port to use.
     *
     * @see    com.cordys.coe.util.config.IWebGatewayConfiguration#setProxyPort(int)
     */
    public void setProxyPort(int iProxyPort)
    {
        setValue(TAG_PROXY_PORT, iProxyPort);
    }

    /**
     * This method sets the username for the proxy server.
     *
     * @param  sProxyUsername  The username for the proxy server.
     *
     * @see    com.cordys.coe.util.config.IWebGatewayConfiguration#setProxyUsername(java.lang.String)
     */
    public void setProxyUsername(String sProxyUsername)
    {
        setValue(TAG_PROXY_USERNAME, sProxyUsername);
    }

    /**
     * This method sets the server name.
     *
     * @param  sServername  The server name.
     */
    public void setServername(String sServername)
    {
        setValue(TAG_SERVER_NAME, sServername);
    }

    /**
     * This method sets whether or not the server is running under SSL.
     *
     * @param  bSSL  Whether or not the server is running under SSL.
     *
     * @see    com.cordys.coe.util.config.IWebGatewayConfiguration#setSSL(boolean)
     */
    public void setSSL(boolean bSSL)
    {
        setValue(TAG_SSL, bSSL);
    }

    /**
     * This method sets the mode of trust that should be used (trust everything or use a trust
     * store.
     *
     * @param  tmTrustMode  The mode of trust that should be used (trust everything or use a trust
     *                      store.
     *
     * @see    com.cordys.coe.util.config.IWebGatewayConfiguration#setSSLTrustMode(com.cordys.coe.util.cgc.config.ETrustMode)
     */
    public void setSSLTrustMode(ETrustMode tmTrustMode)
    {
        setValue(TAG_TRUST_MODE, tmTrustMode.name());
    }

    /**
     * This method sets the trust store location.
     *
     * @param  sTrustStoreLocation  The trust store location.
     */
    public void setTrustStoreLocation(String sTrustStoreLocation)
    {
        setValue(TAG_CA_TRUST_LOC, sTrustStoreLocation);
    }

    /**
     * This method sets the trust store password.
     *
     * @param  sTrustStorePassword  The trust store password.
     */
    public void setTrustStorePassword(String sTrustStorePassword)
    {
        setValue(TAG_CA_TRUST_PWD, sTrustStorePassword);
    }

    /**
     * This method sets the trust store type.
     *
     * @param  sTrustStoreType  The trust store type.
     */
    public void setTrustStoreType(String sTrustStoreType)
    {
        setValue(TAG_CA_TRUST_TYPE, sTrustStoreType);
    }

    /**
     * This method returns the current values in an XML structure.
     *
     * @param   dDoc  The document to use to create the XML.
     *
     * @return  The current values in an XML structure.
     *
     * @see     com.cordys.coe.util.xml.dom.XMLProperties#toXML(org.w3c.dom.Document)
     */
    @Override public Node toXML(Document dDoc)
    {
        Element eReturn = (Element) super.toXML(dDoc);
        eReturn.setAttribute("name", m_sName);
        eReturn.setAttribute("type", ConfigurationFactory.TYPE_WEBGATEWAY);

        return eReturn;
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
        eReturn.setAttribute("type", ConfigurationFactory.TYPE_WEBGATEWAY);

        return eReturn;
    }

    /**
     * This method returns whether or not domain authentication should be used.
     *
     * @return  true if domain authentication should be used.
     */
    public boolean useDomainAuthentication()
    {
        boolean bReturn = true;

        if (getStringValue(TAG_DA_USERNAME, "").length() == 0)
        {
            bReturn = false;
        }

        return bReturn;
    }

    /**
     * This method registers all keys.
     *
     * @see  com.cordys.coe.util.xml.dom.XMLProperties#initializeKeys()
     */
    @Override protected void initializeKeys()
    {
        registerKey(TAG_SERVER_NAME);
        registerKey(TAG_PORT);
        registerKey(TAG_GATEWAY_URL);
        registerKey(TAG_DA_USERNAME);
        registerKey(TAG_DA_PASSWORD);
        registerKey(TAG_DA_DOMAIN);
        registerKey(TAG_CA_CERT_LOC);
        registerKey(TAG_CA_CERT_PWD);
        registerKey(TAG_CA_CERT_TYPE);
        registerKey(TAG_CA_TRUST_LOC);
        registerKey(TAG_CA_TRUST_PWD);
        registerKey(TAG_CA_TRUST_TYPE);
        registerKey(TAG_AUTH_TYPE);
        registerKey(TAG_SSL);
        registerKey(TAG_TRUST_MODE);

        registerKey(TAG_PROXY_HOST);
        registerKey(TAG_PROXY_PORT);
        registerKey(TAG_PROXY_USERNAME);
        registerKey(TAG_PROXY_PASSWORD);

        registerKey(TAG_ACCEPT_WHEN_EXPIRED);
        registerKey(TAG_ACCEPT_WHEN_INVALID);

        registerKey(TAG_NETWORK_TIMEOUT);
        registerKey(TAG_REQUEST_TIMEOUT);

        super.initializeKeys();
    }
}
