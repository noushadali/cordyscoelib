package com.cordys.coe.util.cgc.config;

import com.cordys.coe.util.config.IWebGatewayConfiguration;

import java.io.File;

import java.net.URL;

/**
 * This factory can be used to create configuration classes for the Cordys Gateway Client based on diffenrent parameters.
 * 
 * @author pgussow
 */
public class CGCConfigFactory
{
    /** The default port number for HTTPS traffic. */
    public static final int DEFAULT_HTTPS_PORT = 443;
    /** The default port for normal HTTP traffic. */
    public static final int DEFAULT_HTTP_PORT = 80;
    /** The default sleeptime to use when the server is not running anymore. */
    public static final long DEFAULT_SLEEP_TIME = 15000L;
    /** Holds the default gateway URL. */
    public static final String DEFAULT_GATEWAY_URL = "/cordys/com.eibus.web.soap.Gateway.wcp";
    /** Holds the default gateway class name. */
    public static final String GATEWAY_URL_CLASS = "com.eibus.web.soap.Gateway.wcp";
    /** Holds the default value for whether or not the server is running under SSL. */
    public static final boolean DEFAULT_SSL = false;
    /** Holds the default proxy host. */
    public static final String DEFAULT_PROXY_HOST = null;
    /** Holds the default proxy port. */
    public static final int DEFAULT_PROXY_PORT = -1;
    /** Holds the default value for checking for faults. */
    public static final boolean DEFAULT_CHECK_FOR_FAULTS = true;
    /** Holds the default value for sending the GetUserDetails request. */
    public static final boolean DEFAULT_LOGIN_TO_CORDYS = true;
    /** Holds the default network timeout. */
    public static final long DEFAULT_NETWORK_TIMEOUT = -1;
    /** Holds the default value for whether or not to use the server watcher. */
    public static final boolean DEFAULT_USE_SERVER_WATCHER = false;
    /** Holds the default value for the server watcher poll interval. */
    public static final long DEFAULT_SERVER_WATCHER_POLL_INTERVAL = 30000L;
    /** Holds the default SOAP timeout. */
    public static final long DEFAULT_SOAP_TIMEOUT = 30000L;
    /** Holds the default value for whether or not to accept server certificates are which expired. */
    public static final boolean DEFAULT_ACCEPT_WHEN_EXPIRED = false;
    /** Holds the default value for whether or not to accept server certificates which are invalid. */
    public static final boolean DEFAULT_ACCEPT_WHEN_INVALID = false;
    /** Holds the default number of connections per host. */
    public static final int DEFAULT_MAX_CONNECTIONS_PER_HOST = 20;
    /**
     * Holds the default value for whether or not the login request should be sent with each request. Setting it to false means it
     * will only send it when the server requests it.
     */
    public static final boolean DEFAULT_AUTHENTICATION_PREEMPTIVE = false;
    /** Holds the location of the default trust store. */
    public static final String DEFAULT_TRUST_STORE = System.getProperty("java.home") + File.separator + "lib" + File.separator
            + "security" + File.separator + "cacerts";
    /** Holds the password of the default trust store. */
    public static final String DEFAULT_TRUST_STORE_PASSWORD = "changeit";
    /** Holds the default type for the trust store. */
    public static final String DEFAULT_TRUST_STORE_TYPE = "jks";
    /** Holds the default proxy password. */
    private static final String DEFAULT_PROXY_PASSWORD = null;
    /** Holds the default proxy username. */
    private static final String DEFAULT_PROXY_USERNAME = null;
    /** Holds the default maximum number of concurrent calls. */
    private static final int DEFAULT_MAX_CONCURRENT_CALLS = 5;
    /** Holds the default trust mode that should be used. */
    private static final ETrustMode DEFAULT_TRUST_MODE = ETrustMode.USE_TRUSTORE;

    /**
     * This method creates a default configuration based on the URL.
     * 
     * @param uUrl URL containing the protocol, host name and port.
     * @return The configuration to use.
     */
    public static ICGCConfiguration createConfiguration(URL uUrl)
    {
        String host = uUrl.getHost();
        boolean isSsl = "https".equals(uUrl.getProtocol());
        int port = uUrl.getPort();

        if (port == -1)
        {
            port = (isSsl ? DEFAULT_HTTPS_PORT : DEFAULT_HTTP_PORT);
        }

        String path = uUrl.getPath();
        StringBuilder pathBuilder = new StringBuilder(path.length());

        if (!path.endsWith(GATEWAY_URL_CLASS))
        {
            // Append the class.
            pathBuilder.append(path);

            if (!path.endsWith("/"))
            {
                pathBuilder.append("/");
            }

            pathBuilder.append(GATEWAY_URL_CLASS);
        }
        else
        {
            pathBuilder.append(path);
        }

        return createConfiguration(host, port, pathBuilder.toString(), isSsl, DEFAULT_PROXY_HOST, DEFAULT_PROXY_PORT,
                DEFAULT_PROXY_USERNAME, DEFAULT_PROXY_PASSWORD, DEFAULT_CHECK_FOR_FAULTS, DEFAULT_LOGIN_TO_CORDYS,
                DEFAULT_NETWORK_TIMEOUT, DEFAULT_USE_SERVER_WATCHER, DEFAULT_SERVER_WATCHER_POLL_INTERVAL, DEFAULT_SLEEP_TIME,
                DEFAULT_SOAP_TIMEOUT, DEFAULT_MAX_CONNECTIONS_PER_HOST, DEFAULT_AUTHENTICATION_PREEMPTIVE, DEFAULT_TRUST_STORE,
                DEFAULT_TRUST_STORE_PASSWORD, DEFAULT_TRUST_STORE_TYPE, DEFAULT_ACCEPT_WHEN_EXPIRED, DEFAULT_ACCEPT_WHEN_INVALID,
                DEFAULT_MAX_CONCURRENT_CALLS, DEFAULT_TRUST_MODE);
    }

    /**
     * This method creates a default configuration based on the host name.
     * 
     * @param sHost The name of the host to connect to.
     * @return The configuration to use.
     */
    public static ICGCConfiguration createConfiguration(String sHost)
    {
        return createConfiguration(sHost, DEFAULT_HTTP_PORT, DEFAULT_GATEWAY_URL, DEFAULT_SSL, DEFAULT_PROXY_HOST,
                DEFAULT_PROXY_PORT, DEFAULT_PROXY_USERNAME, DEFAULT_PROXY_PASSWORD, DEFAULT_CHECK_FOR_FAULTS,
                DEFAULT_LOGIN_TO_CORDYS, DEFAULT_NETWORK_TIMEOUT, DEFAULT_USE_SERVER_WATCHER,
                DEFAULT_SERVER_WATCHER_POLL_INTERVAL, DEFAULT_SLEEP_TIME, DEFAULT_SOAP_TIMEOUT, DEFAULT_MAX_CONNECTIONS_PER_HOST,
                DEFAULT_AUTHENTICATION_PREEMPTIVE, DEFAULT_TRUST_STORE, DEFAULT_TRUST_STORE_PASSWORD, DEFAULT_TRUST_STORE_TYPE,
                DEFAULT_ACCEPT_WHEN_EXPIRED, DEFAULT_ACCEPT_WHEN_INVALID, DEFAULT_MAX_CONCURRENT_CALLS, DEFAULT_TRUST_MODE);
    }

    /**
     * This method creates the configuration for the Cordys Gateway Client based on the wgcConfig object.
     * 
     * @param wgcConfig The persistent configuration for the web gateway
     * @return The {@link ICGCConfiguration} object to use for creating the Gateway Client.
     */
    public static ICGCConfiguration createConfiguration(IWebGatewayConfiguration wgcConfig)
    {
        ICGCConfiguration ccReturn = null;

        if (wgcConfig.getSSL() == true)
        {
            ccReturn = new CGCSSLConfigurationImpl();

            ICGCSSLConfiguration cgcSSL = (ICGCSSLConfiguration) ccReturn;

            cgcSSL.setTrustMode(wgcConfig.getSSLTrustMode());

            if (wgcConfig.getSSLTrustMode() == ETrustMode.TRUST_EVERY_SERVER)
            {
                cgcSSL.setAcceptWhenExpired(true);
                cgcSSL.setAcceptWhenInvalid(true);
            }
            else
            {
                cgcSSL.setAcceptWhenExpired(DEFAULT_ACCEPT_WHEN_EXPIRED);
                cgcSSL.setAcceptWhenInvalid(DEFAULT_ACCEPT_WHEN_INVALID);
            }

            cgcSSL.setTrustStore(wgcConfig.getTrustStoreLocation());
            cgcSSL.setTrustStorePassword(wgcConfig.getTrustStorePassword());
            cgcSSL.setTrustStoreType(wgcConfig.getTrustStoreType());
        }
        else
        {
            ccReturn = new CGCConfigurationImpl();
        }

        ccReturn.setGatewayURL(wgcConfig.getGatewayURL());
        ccReturn.setCheckForFaults(DEFAULT_CHECK_FOR_FAULTS);
        ccReturn.setHost(wgcConfig.getServername());
        ccReturn.setLoginToCordysOnConnect(DEFAULT_LOGIN_TO_CORDYS);
        ccReturn.setNetworkTimeout(DEFAULT_NETWORK_TIMEOUT);
        ccReturn.setPort(wgcConfig.getPort());
        ccReturn.setProxyHost(DEFAULT_PROXY_HOST);
        ccReturn.setProxyPort(DEFAULT_PROXY_PORT);
        ccReturn.setServerWatcherPollInterval(DEFAULT_SERVER_WATCHER_POLL_INTERVAL);
        ccReturn.setSleepTimeBetweenServerWacther(DEFAULT_SLEEP_TIME);
        ccReturn.setSSL(wgcConfig.getSSL());
        ccReturn.setTimeout(wgcConfig.getTimeout());
        ccReturn.setUseServerWatcher(DEFAULT_USE_SERVER_WATCHER);
        ccReturn.setMaxConnectionsPerHost(DEFAULT_MAX_CONNECTIONS_PER_HOST);
        ccReturn.setAuthenticationPreemptive(DEFAULT_AUTHENTICATION_PREEMPTIVE);
        ccReturn.setMaxConcurrentCalls(DEFAULT_MAX_CONCURRENT_CALLS);

        return ccReturn;
    }

    /**
     * This method creates a standard connection to a server and port. You can specify whether or not the server is running under
     * SSL.
     * 
     * @param sHost The name of the host to connect to.
     * @param iPort The default port number.
     * @param bSsl Whether or not Cordys is running under SSL.
     * @return The configuration to use.
     */
    public static ICGCConfiguration createConfiguration(String sHost, int iPort, boolean bSsl)
    {
        return createConfiguration(sHost, iPort, DEFAULT_GATEWAY_URL, bSsl, DEFAULT_PROXY_HOST, DEFAULT_PROXY_PORT,
                DEFAULT_PROXY_USERNAME, DEFAULT_PROXY_PASSWORD, DEFAULT_CHECK_FOR_FAULTS, DEFAULT_LOGIN_TO_CORDYS,
                DEFAULT_NETWORK_TIMEOUT, DEFAULT_USE_SERVER_WATCHER, DEFAULT_SERVER_WATCHER_POLL_INTERVAL, DEFAULT_SLEEP_TIME,
                DEFAULT_SOAP_TIMEOUT, DEFAULT_MAX_CONNECTIONS_PER_HOST, DEFAULT_AUTHENTICATION_PREEMPTIVE, DEFAULT_TRUST_STORE,
                DEFAULT_TRUST_STORE_PASSWORD, DEFAULT_TRUST_STORE_TYPE, DEFAULT_ACCEPT_WHEN_EXPIRED, DEFAULT_ACCEPT_WHEN_INVALID,
                DEFAULT_MAX_CONCURRENT_CALLS, DEFAULT_TRUST_MODE);
    }

    /**
     * This method creates a default configuration based on the host name.
     * 
     * @param sHost The name of the host to connect to.
     * @param iPort The default port number.
     * @param sGatewayURL The URL for the gateway.
     * @param bSsl Whether or not Cordys is running under SSL.
     * @param sProxyHost The proxy host.
     * @param iProxyPort The proxy port.
     * @param sProxyUsername Holds the username for the proxy user.
     * @param sProxyPassword Holds the password for the proxy user.
     * @param bCheckForFaults Whether or not to check for faults.
     * @param bLoginToCordysOnConnect Whether or not to send the GetUserDetails request.
     * @param lNetworkTimeout The network timeout to do.
     * @param bUseServerWatcher Whether or not to use the server watcher.
     * @param lServerWatcherPollInterval Holds the poll interval for the server watcher.
     * @param lSleepTime Holds the sleep time between
     * @param lTimeout Holds the timeout for the SOAP requests.
     * @param iMaxConnectionsPerHost The maximum number of connections to the given host.
     * @param bAuthenticationPreemptive Holds the default value for whether or not the login request should be sent with each
     *            request.
     * @param sTrustStore The location of the trust store.
     * @param sTrustStorePassword The password of the trust store.
     * @param sTrustStoreType The type of the trust store.
     * @param bAcceptWhenExpired Whether or not to accept expired server certificates.
     * @param bAcceptWhenInvalid Whether or not to accept invalid server certificates.
     * @param iMaxConcurrentCalls The maximum number of concurrent calls to the requestFromCordys method.
     * @param tmTrustMode The trust mode to use.
     * @return The configuration to use.
     */
    public static ICGCConfiguration createConfiguration(String sHost, int iPort, String sGatewayURL, boolean bSsl,
            String sProxyHost, int iProxyPort, String sProxyUsername, String sProxyPassword, boolean bCheckForFaults,
            boolean bLoginToCordysOnConnect, long lNetworkTimeout, boolean bUseServerWatcher, long lServerWatcherPollInterval,
            long lSleepTime, long lTimeout, int iMaxConnectionsPerHost, boolean bAuthenticationPreemptive, String sTrustStore,
            String sTrustStorePassword, String sTrustStoreType, boolean bAcceptWhenExpired, boolean bAcceptWhenInvalid,
            int iMaxConcurrentCalls, ETrustMode tmTrustMode)
    {
        return createConfiguration(sHost, iPort, sGatewayURL, bSsl, sProxyHost, iProxyPort, sProxyUsername, sProxyPassword,
                bCheckForFaults, bLoginToCordysOnConnect, true, lNetworkTimeout, bUseServerWatcher, lServerWatcherPollInterval,
                lSleepTime, lTimeout, iMaxConnectionsPerHost, bAuthenticationPreemptive, sTrustStore, sTrustStorePassword,
                sTrustStoreType, bAcceptWhenExpired, bAcceptWhenInvalid, iMaxConcurrentCalls, tmTrustMode);
    }

    /**
     * This method creates a default configuration based on the host name.
     * 
     * @param sHost The name of the host to connect to.
     * @param iPort The default port number.
     * @param sGatewayURL The URL for the gateway.
     * @param bSsl Whether or not Cordys is running under SSL.
     * @param sProxyHost The proxy host.
     * @param iProxyPort The proxy port.
     * @param sProxyUsername Holds the username for the proxy user.
     * @param sProxyPassword Holds the password for the proxy user.
     * @param bCheckForFaults Whether or not to check for faults.
     * @param bLoginToCordysOnConnect Whether or not to send the GetUserDetails request.
     * @param autoParseGetUserDetails Whether or not the login response should be parsed autmatically.
     * @param lNetworkTimeout The network timeout to do.
     * @param bUseServerWatcher Whether or not to use the server watcher.
     * @param lServerWatcherPollInterval Holds the poll interval for the server watcher.
     * @param lSleepTime Holds the sleep time between
     * @param lTimeout Holds the timeout for the SOAP requests.
     * @param iMaxConnectionsPerHost The maximum number of connections to the given host.
     * @param bAuthenticationPreemptive Holds the default value for whether or not the login request should be sent with each
     *            request.
     * @param sTrustStore The location of the trust store.
     * @param sTrustStorePassword The password of the trust store.
     * @param sTrustStoreType The type of the trust store.
     * @param bAcceptWhenExpired Whether or not to accept expired server certificates.
     * @param bAcceptWhenInvalid Whether or not to accept invalid server certificates.
     * @param iMaxConcurrentCalls The maximum number of concurrent calls to the requestFromCordys method.
     * @param tmTrustMode The trust mode to use.
     * @return The configuration to use.
     */
    public static ICGCConfiguration createConfiguration(String sHost, int iPort, String sGatewayURL, boolean bSsl,
            String sProxyHost, int iProxyPort, String sProxyUsername, String sProxyPassword, boolean bCheckForFaults,
            boolean bLoginToCordysOnConnect, boolean autoParseGetUserDetails, long lNetworkTimeout, boolean bUseServerWatcher,
            long lServerWatcherPollInterval, long lSleepTime, long lTimeout, int iMaxConnectionsPerHost,
            boolean bAuthenticationPreemptive, String sTrustStore, String sTrustStorePassword, String sTrustStoreType,
            boolean bAcceptWhenExpired, boolean bAcceptWhenInvalid, int iMaxConcurrentCalls, ETrustMode tmTrustMode)
    {
        ICGCConfiguration ccReturn = null;

        if (bSsl == true)
        {
            ccReturn = new CGCSSLConfigurationImpl();

            ICGCSSLConfiguration cgcSSL = (ICGCSSLConfiguration) ccReturn;

            cgcSSL.setTrustMode(tmTrustMode);

            cgcSSL.setAcceptWhenExpired(bAcceptWhenExpired);
            cgcSSL.setAcceptWhenInvalid(bAcceptWhenInvalid);

            cgcSSL.setTrustStore(sTrustStore);
            cgcSSL.setTrustStorePassword(sTrustStorePassword);
            cgcSSL.setTrustStoreType(sTrustStoreType);
        }
        else
        {
            ccReturn = new CGCConfigurationImpl();
        }

        ccReturn.setGatewayURL(sGatewayURL);
        ccReturn.setCheckForFaults(bCheckForFaults);
        ccReturn.setHost(sHost);
        ccReturn.setLoginToCordysOnConnect(bLoginToCordysOnConnect);
        ccReturn.setNetworkTimeout(lNetworkTimeout);
        ccReturn.setPort(iPort);
        ccReturn.setProxyHost(sProxyHost);
        ccReturn.setProxyPort(iProxyPort);
        ccReturn.setServerWatcherPollInterval(lServerWatcherPollInterval);
        ccReturn.setSleepTimeBetweenServerWacther(lSleepTime);
        ccReturn.setSSL(bSsl);
        ccReturn.setTimeout(lTimeout);
        ccReturn.setUseServerWatcher(bUseServerWatcher);
        ccReturn.setMaxConnectionsPerHost(iMaxConnectionsPerHost);
        ccReturn.setAuthenticationPreemptive(bAuthenticationPreemptive);
        ccReturn.setMaxConcurrentCalls(iMaxConcurrentCalls);

        return ccReturn;
    }

    /**
     * This method creates the configuration using an SSL connection and it will trust any server.
     * 
     * @param sHost The name of the host to connect to.
     * @param iPort The default port number.
     * @param tmTrustMode The trustmode to use. When USE_TRUSTSTORE is passed on, it will use the default JRE trust store.
     * @return The configuration to use.
     */
    public static ICGCConfiguration createSSLConfiguration(String sHost, int iPort, ETrustMode tmTrustMode)
    {
        return createConfiguration(sHost, iPort, DEFAULT_GATEWAY_URL, true, DEFAULT_PROXY_HOST, DEFAULT_PROXY_PORT,
                DEFAULT_PROXY_USERNAME, DEFAULT_PROXY_PASSWORD, DEFAULT_CHECK_FOR_FAULTS, DEFAULT_LOGIN_TO_CORDYS,
                DEFAULT_NETWORK_TIMEOUT, DEFAULT_USE_SERVER_WATCHER, DEFAULT_SERVER_WATCHER_POLL_INTERVAL, DEFAULT_SLEEP_TIME,
                DEFAULT_SOAP_TIMEOUT, DEFAULT_MAX_CONNECTIONS_PER_HOST, DEFAULT_AUTHENTICATION_PREEMPTIVE, DEFAULT_TRUST_STORE,
                DEFAULT_TRUST_STORE_PASSWORD, DEFAULT_TRUST_STORE_TYPE, DEFAULT_ACCEPT_WHEN_EXPIRED, DEFAULT_ACCEPT_WHEN_INVALID,
                DEFAULT_MAX_CONCURRENT_CALLS, tmTrustMode);
    }

    /**
     * This method creates the configuration using an SSL connection and it will trust any server.
     * 
     * @param sHost The name of the host to connect to.
     * @param iPort The default port number.
     * @return The configuration to use.
     */
    public static ICGCConfiguration createSSLNoTrustConfiguration(String sHost, int iPort)
    {
        return createConfiguration(sHost, iPort, DEFAULT_GATEWAY_URL, true, DEFAULT_PROXY_HOST, DEFAULT_PROXY_PORT,
                DEFAULT_PROXY_USERNAME, DEFAULT_PROXY_PASSWORD, DEFAULT_CHECK_FOR_FAULTS, DEFAULT_LOGIN_TO_CORDYS,
                DEFAULT_NETWORK_TIMEOUT, DEFAULT_USE_SERVER_WATCHER, DEFAULT_SERVER_WATCHER_POLL_INTERVAL, DEFAULT_SLEEP_TIME,
                DEFAULT_SOAP_TIMEOUT, DEFAULT_MAX_CONNECTIONS_PER_HOST, DEFAULT_AUTHENTICATION_PREEMPTIVE, DEFAULT_TRUST_STORE,
                DEFAULT_TRUST_STORE_PASSWORD, DEFAULT_TRUST_STORE_TYPE, DEFAULT_ACCEPT_WHEN_EXPIRED, DEFAULT_ACCEPT_WHEN_INVALID,
                DEFAULT_MAX_CONCURRENT_CALLS, ETrustMode.TRUST_EVERY_SERVER);
    }
}
