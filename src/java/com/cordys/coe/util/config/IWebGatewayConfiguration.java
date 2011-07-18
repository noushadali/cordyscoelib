package com.cordys.coe.util.config;

import com.cordys.coe.util.cgc.config.EAuthenticationType;
import com.cordys.coe.util.cgc.config.ETrustMode;

/**
 * Interface for webgateway configurations.
 *
 * @author  pgussow
 */
public interface IWebGatewayConfiguration extends IConfiguration
{
    /**
     * Holds the name of the tag for the acceptwhenexpired setting.
     */
    String TAG_ACCEPT_WHEN_EXPIRED = "acceptwhenexpired";
    /**
     * Holds the name of the tag for the acceptwheninvalid setting.
     */
    String TAG_ACCEPT_WHEN_INVALID = "acceptwheninvalid";
    /**
     * Holds the name of the tag for the authentication type.
     */
    String TAG_AUTH_TYPE = "authenticationtype";
    /**
     * Holds the name of the tag for the client certificate location.
     */
    String TAG_CA_CERT_LOC = "cacertloc";
    /**
     * Holds the name of the tag for the client certificate password.
     */
    String TAG_CA_CERT_PWD = "cacertpwd";
    /**
     * Holds the name of the tag for the client certificate type.
     */
    String TAG_CA_CERT_TYPE = "cacerttype";
    /**
     * Holds the name of the tag for the trust store location.
     */
    String TAG_CA_TRUST_LOC = "catrustloc";
    /**
     * Holds the name of the tag for the trust store password.
     */
    String TAG_CA_TRUST_PWD = "catrustpwd";
    /**
     * Holds the name of the tag for the trust store type.
     */
    String TAG_CA_TRUST_TYPE = "catrusttype";
    /**
     * Holds the name of the tag for the domain.
     */
    String TAG_DA_DOMAIN = "dadomain";
    /**
     * Holds the name of the tag for the password name.
     */
    String TAG_DA_PASSWORD = "dapassword";
    /**
     * Holds the name of the tag for the user name.
     */
    String TAG_DA_USERNAME = "dausername";
    /**
     * Holds the name of the tag for the gateway URL.
     */
    String TAG_GATEWAY_URL = "gatewayurl";
    /**
     * Holds the name of the tag for the network timeout.
     */
    String TAG_NETWORK_TIMEOUT = "networktimeout";
    /**
     * Holds the name of the tag for the port number.
     */
    String TAG_PORT = "port";
    /**
     * Holds the name of the tag for the proxy server.
     */
    String TAG_PROXY_HOST = "proxyhost";
    /**
     * Holds the name of the tag for the proxy password.
     */
    String TAG_PROXY_PASSWORD = "proxypassword";
    /**
     * Holds the name of the tag for the proxy port.
     */
    String TAG_PROXY_PORT = "proxyport";
    /**
     * Holds the name of the tag for the proxy username.
     */
    String TAG_PROXY_USERNAME = "proxyusername";
    /**
     * Holds the name of the tag for the request timeout.
     */
    String TAG_REQUEST_TIMEOUT = "requesttimeout";
    /**
     * Holds the name of the tag for the server name.
     */
    String TAG_SERVER_NAME = "server";
    /**
     * Holds the name of the tag for the indication whether or not the server is running under SSL.
     */
    String TAG_SSL = "ssl";
    /**
     * Holds the tag for the timeout.
     */
    String TAG_TIMEOUT = "timeout";
    /**
     * Holds the name of the tag for the trust mode (truststore/all servers).
     */
    String TAG_TRUST_MODE = "trustmode";

    /**
     * This method checks if the configuration is valid. This is based on several criteria and
     * depending on the type of connection.
     *
     * @throws  ConfigurationManagerException  In case the configuration is not valid.
     */
    void checkValidity()
                throws ConfigurationManagerException;

    /**
     * This method gets whether or not the server certificate will be accepted even though it has
     * expired or is not yet valid.
     *
     * @return  Whether or not the server certificate will be accepted even though it has expired or
     *          is not yet valid.
     */
    boolean getAcceptWhenExpired();

    /**
     * This method gets whether or not the server certificate is always accepted even though it's
     * invalid.
     *
     * @return  Whether or not the server certificate is always accepted even though it's invalid.
     */
    boolean getAcceptWhenInvalid();

    /**
     * This method gets the authentication type for this configuration.
     *
     * @return  The authentication type for this configuration.
     */
    EAuthenticationType getAuthenticationType();

    /**
     * This method gets the certificate location.
     *
     * @return  The certificate location.
     */
    String getCertificateLocation();

    /**
     * This method gets the certificate password.
     *
     * @return  The certificate password.
     */
    String getCertificatePassword();

    /**
     * This method gets the certificate type.
     *
     * @return  The certificate type.
     */
    String getCertificateType();

    /**
     * This method gets the actual domain.
     *
     * @return  The actual domain.
     */
    String getDomain();

    /**
     * This method gets the domain password.
     *
     * @return  The domain password.
     */
    String getDomainPassword();

    /**
     * This method gets the domain user.
     *
     * @return  The domain user.
     */
    String getDomainUsername();

    /**
     * This method gets the gateway URL to use.
     *
     * @return  The gateway URL to use.
     */
    String getGatewayURL();

    /**
     * Returns the network TCP/IP timeout to be used for requests.
     *
     * @return  Network timeout.
     */
    long getNetworkTimeout();

    /**
     * This method gets the proxy host to use.
     *
     * @return  The proxy host to use.
     */
    String getProxyHost();

    /**
     * This method gets the password for the proxy user.
     *
     * @return  The password for the proxy user.
     */
    String getProxyPassword();

    /**
     * This method gets the proxy port to use.
     *
     * @return  The proxy port to use.
     */
    int getProxyPort();

    /**
     * This method gets the username for the proxy server.
     *
     * @return  The username for the proxy server.
     */
    String getProxyUsername();

    /**
     * This method gets whether or not the server is running under SSL.
     *
     * @return  Whether or not the server is running under SSL.
     */
    boolean getSSL();

    /**
     * This method gets the mode of trust that should be used (trust everything or use a trust
     * store.
     *
     * @return  The mode of trust that should be used (trust everything or use a trust store.
     */
    ETrustMode getSSLTrustMode();

    /**
     * This method gets the timeout to use.
     *
     * @return  The timeout to use.
     */
    long getTimeout();

    /**
     * This method gets the trust store location.
     *
     * @return  The trust store location.
     */
    String getTrustStoreLocation();

    /**
     * This method gets the trust store password.
     *
     * @return  The trust store password.
     */
    String getTrustStorePassword();

    /**
     * This method gets the trust store type.
     *
     * @return  The trust store type.
     */
    String getTrustStoreType();

    /**
     * This method gets the type of configuration (WebGateway or native).
     *
     * @return  The type of configuration (WebGateway or native).
     *
     * @see     com.cordys.coe.util.config.IConfiguration#getType()
     */
    int getType();

    /**
     * This method returns whether or not the current configuration is valid.
     *
     * @return  true if the configuration is valid. Otherwise false.
     */
    boolean isValid();

    /**
     * This method sets whether or not the server certificate will be accepted even though it has
     * expired or is not yet valid.
     *
     * @param  bAcceptWhenExpired  Whether or not the server certificate will be accepted even
     *                             though it has expired or is not yet valid.
     */
    void setAcceptWhenExpired(boolean bAcceptWhenExpired);

    /**
     * This method sets wether or not the server certificate is always accepted even though it's
     * invalid.
     *
     * @param  bAcceptWhenInvalid  Whether or not the server certificate is always accepted even
     *                             though it's invalid.
     */
    void setAcceptWhenInvalid(boolean bAcceptWhenInvalid);

    /**
     * This method sets the authentication type for this configuration.
     *
     * @param  atAuthenticationType  The authentication type for this configuration.
     */
    void setAuthenticationType(EAuthenticationType atAuthenticationType);

    /**
     * This method sets the certificate location.
     *
     * @param  sCertificateLocation  The certificate location.
     */
    void setCertificateLocation(String sCertificateLocation);

    /**
     * This method sets the certificate password.
     *
     * @param  sCertificatePassword  The certificate password.
     */
    void setCertificatePassword(String sCertificatePassword);

    /**
     * This method sets the certificate type.
     *
     * @param  sCertificateType  The certificate type.
     */
    void setCertificateType(String sCertificateType);

    /**
     * This method sets the actual domain.
     *
     * @param  sDomain  The actual domain.
     */
    void setDomain(String sDomain);

    /**
     * This method sets the domain password.
     *
     * @param  sDomainPassword  The domain password.
     */
    void setDomainPassword(String sDomainPassword);

    /**
     * This method sets the domain user.
     *
     * @param  sDomainUsername  The domain user.
     */
    void setDomainUsername(String sDomainUsername);

    /**
     * This method sets the gateway URL to use.
     *
     * @param  sGatewayURL  The gateway URL to use.
     */
    void setGatewayURL(String sGatewayURL);

    /**
     * This method sets the name for the configuration.
     *
     * @param  sName  The name for the configuration.
     */
    void setName(String sName);

    /**
     * Sets the network TCP/IP timeout to be used for requests. This is separate from the Cordys
     * timeout URL parameter.
     *
     * @param  lNetworkTimeout  Network timeout value (-1 means infinite wait).
     */
    void setNetworkTimeout(long lNetworkTimeout);

    /**
     * This method sets the port number to use.
     *
     * @param  iPort  The port number to use.
     */
    void setPort(int iPort);

    /**
     * This method sets the proxy host to use.
     *
     * @param  sProxyHost  The proxy host to use.
     */
    void setProxyHost(String sProxyHost);

    /**
     * This method sets the password for the proxy user.
     *
     * @param  sProxyPassword  The password for the proxy user.
     */
    void setProxyPassword(String sProxyPassword);

    /**
     * This method sets the proxy port to use.
     *
     * @param  iProxyPort  The proxy port to use.
     */
    void setProxyPort(int iProxyPort);

    /**
     * This method sets the username for the proxy server.
     *
     * @param  sProxyUsername  The username for the proxy server.
     */
    void setProxyUsername(String sProxyUsername);

    /**
     * This method sets the server name.
     *
     * @param  sServername  The server name.
     */
    void setServername(String sServername);

    /**
     * This method sets wether or not the server is running under SSL.
     *
     * @param  bSSL  Whether or not the server is running under SSL.
     */
    void setSSL(boolean bSSL);

    /**
     * This method sets the mode of trust that should be used (trust everything or use a trust
     * store.
     *
     * @param  tmTrustMode  The mode of trust that should be used (trust everything or use a trust
     *                      store.
     */
    void setSSLTrustMode(ETrustMode tmTrustMode);

    /**
     * This method sets the timeout to use.
     *
     * @param  lTimeout  The timeout to use.
     */
    void setTimeout(long lTimeout);

    /**
     * This method sets the trust store location.
     *
     * @param  sTrustStoreLocation  The trust store location.
     */
    void setTrustStoreLocation(String sTrustStoreLocation);

    /**
     * This method sets the trust store password.
     *
     * @param  sTrustStorePassword  The trust store password.
     */
    void setTrustStorePassword(String sTrustStorePassword);

    /**
     * This method sets the trust store type.
     *
     * @param  sTrustStoreType  The trust store type.
     */
    void setTrustStoreType(String sTrustStoreType);

    /**
     * This method returns whether or not domain authentication should be used.
     *
     * @return  true if domain authentication should be used.
     */
    boolean useDomainAuthentication();
}
