package com.cordys.coe.util.cgc.config;

import com.cordys.coe.util.cgc.CordysGatewayClientException;
import com.cordys.coe.util.cgc.message.CGCMessages;

/**
 * This class holds the configuration for the CordysGatewayClient.
 *
 * @author  pgussow
 */
class CGCConfigurationImpl
    implements ICGCConfiguration
{
    /**
     * Holds whether or not the login request should be sent with each request. If set to false it
     * will only send it when the server requests it.
     */
    private boolean m_bAuthenticationPreemptive;
    /**
     * Holds whether or not to automatically check for SOAP faults in the response.
     */
    private boolean m_bCheckForFaults;
    /**
     * Holds whether or not to send the GetUserDetails request.
     */
    private boolean m_bLoginToCordysOnConnect;
    /**
     * Holds whether or not the Cordys server is running in SSL mode.
     */
    private boolean m_bSsl;
    /**
     * Holds whether or not to use the server watcher.
     */
    private boolean m_bUseServerWatcher;
    /**
     * Holds the maximum amount of concurrent calls to the requestCordys method.
     */
    private int m_iMaxConcurrentCalls;
    /**
     * Holds the maximum number of connections per host.
     */
    private int m_iMaxConnectionsPerHost;
    /**
     * Holds the port number on which the server is running.
     */
    private int m_iPort;
    /**
     * Holds the port for the proxy server.
     */
    private int m_iProxyPort;
    /**
     * Holds the network timeout to use.
     */
    private long m_lNetworkTimeout;
    /**
     * Holds the poll interval for the server watcher.
     */
    private long m_lServerWatcherPollInterval;
    /**
     * Holds the sleep time between checking if the server is still up.
     */
    private long m_lSleepTime;
    /**
     * Holds the timeout for the soap request.
     */
    private long m_lTimeout;
    /**
     * Holds the URL for the gateway.
     */
    private String m_sGatewayURL;
    /**
     * Holds the host name to connect to.
     */
    private String m_sHost;
    /**
     * Holds the name of the proxy server.
     */
    private String m_sProxyHost;
    /**
     * Holds the password for the proxy server.
     */
    private String m_sProxyPassword;
    /**
     * Holds the username for the proxy server.
     */
    private String m_sProxyUsername;

    /**
     * This method gets whether or not the login request should be sent with each request. If set to
     * false it will only send it when the server requests it.
     *
     * @return  Whether or not the login request should be sent with each request. If set to false
     *          it will only send it when the server requests it.
     *
     * @see     com.cordys.coe.util.cgc.config.ICGCConfiguration#getAuthenticationPreemptive()
     */
    public boolean getAuthenticationPreemptive()
    {
        return m_bAuthenticationPreemptive;
    }

    /**
     * This method gets the url of the Cordys gateway.
     *
     * @return  The url of the Cordys gateway.
     *
     * @see     com.cordys.coe.util.cgc.config.ICGCConfiguration#getGatewayURL()
     */
    public String getGatewayURL()
    {
        return m_sGatewayURL;
    }

    /**
     * This method gets the hostname of the cordys gateway.
     *
     * @return  The hostname of the cordys gateway.
     *
     * @see     com.cordys.coe.util.cgc.config.ICGCConfiguration#getHost()
     */
    public String getHost()
    {
        return m_sHost;
    }

    /**
     * Returns the flag indicating if a login request is sent when the connection is opened.
     *
     * @return  If <code>true</code>, a login request is sent.
     *
     * @see     com.cordys.coe.util.cgc.config.ICGCConfiguration#getLoginToCordysOnConnect()
     */
    public boolean getLoginToCordysOnConnect()
    {
        return m_bLoginToCordysOnConnect;
    }

    /**
     * This method gets the maximum amount of concurrent calls to this class.
     *
     * @return  The maximum amount of concurrent calls to this class.
     *
     * @see     com.cordys.coe.util.cgc.config.ICGCConfiguration#getMaxConcurrentCalls()
     */
    public int getMaxConcurrentCalls()
    {
        return m_iMaxConcurrentCalls;
    }

    /**
     * This method gets the maximum number of connections per host.
     *
     * @return  The maximum number of connections per host.
     *
     * @see     com.cordys.coe.util.cgc.config.ICGCConfiguration#getMaxConnectionsPerHost()
     */
    public int getMaxConnectionsPerHost()
    {
        return m_iMaxConnectionsPerHost;
    }

    /**
     * Returns the network TCP/IP timeout to be used for requests.
     *
     * @return  Network timeout.
     *
     * @see     com.cordys.coe.util.cgc.config.ICGCConfiguration#getNetworkTimeout()
     */
    public long getNetworkTimeout()
    {
        return m_lNetworkTimeout;
    }

    /**
     * This method gets the port where the Cordys web gateway is running.
     *
     * @return  The port where the Cordys web gateway is running.
     *
     * @see     com.cordys.coe.util.cgc.config.ICGCConfiguration#getPort()
     */
    public int getPort()
    {
        return m_iPort;
    }

    /**
     * This method gets the proxy host to use.
     *
     * @return  The proxy host to use.
     *
     * @see     com.cordys.coe.util.cgc.config.ICGCConfiguration#getProxyHost()
     */
    public String getProxyHost()
    {
        return m_sProxyHost;
    }

    /**
     * This method gets the password for the proxy user.
     *
     * @return  The password for the proxy user.
     *
     * @see     com.cordys.coe.util.cgc.config.ICGCConfiguration#getProxyPassword()
     */
    public String getProxyPassword()
    {
        return m_sProxyPassword;
    }

    /**
     * This method gets the proxy port to use.
     *
     * @return  The proxy port to use.
     *
     * @see     com.cordys.coe.util.cgc.config.ICGCConfiguration#getProxyPort()
     */
    public int getProxyPort()
    {
        return m_iProxyPort;
    }

    /**
     * This method gets the username for the proxy server.
     *
     * @return  The username for the proxy server.
     *
     * @see     com.cordys.coe.util.cgc.config.ICGCConfiguration#getProxyUsername()
     */
    public String getProxyUsername()
    {
        return m_sProxyUsername;
    }

    /**
     * This method gets the interval in which the server watcher will check if the webserver is
     * still available. If this CGC is created without a server watcher this call has no effect.
     *
     * @return  The new poll interval.
     *
     * @see     com.cordys.coe.util.cgc.config.ICGCConfiguration#getServerWatcherPollInterval()
     */
    public long getServerWatcherPollInterval()
    {
        return m_lServerWatcherPollInterval;
    }

    /**
     * This method gets the time to wait between asking the server watcher if the server is alive.
     *
     * @return  The time to wait between asking the server watcher if the server is alive.
     *
     * @see     com.cordys.coe.util.cgc.config.ICGCConfiguration#getSleepTimeBetweenServerWacther()
     */
    public long getSleepTimeBetweenServerWacther()
    {
        return m_lSleepTime;
    }

    /**
     * This method gets the timeout to use.
     *
     * @return  The timeout to use.
     *
     * @see     com.cordys.coe.util.cgc.config.ICGCConfiguration#getTimeout()
     */
    public long getTimeout()
    {
        return m_lTimeout;
    }

    /**
     * This method gets whether or not the gateway checks the responses for soap faults.
     *
     * @return  Whether or not the gateway checks the responses for soap faults.
     *
     * @see     com.cordys.coe.util.cgc.config.ICGCConfiguration#isCheckingForFaults()
     */
    public boolean isCheckingForFaults()
    {
        return m_bCheckForFaults;
    }

    /**
     * This method gets whether or not a proxy server is configured.
     *
     * @return  Whether or not a proxy server is configured.
     *
     * @see     com.cordys.coe.util.cgc.config.ICGCConfiguration#isProxyServerSet()
     */
    public boolean isProxyServerSet()
    {
        return !((m_sProxyHost == null) || (m_sProxyHost.length() == 0) || (m_iProxyPort == -1));
    }

    /**
     * This method gets whether or not SSL should be used.
     *
     * @return  Whether or not SSL should be used.
     *
     * @see     com.cordys.coe.util.cgc.config.ICGCConfiguration#isSSL()
     */
    public boolean isSSL()
    {
        return m_bSsl;
    }

    /**
     * This method sets wether or not the login request should be sent with each request. If set to
     * false it will only send it when the server requests it.
     *
     * @param  bAuthenticationPreemptive  Whether or not the login request should be sent with each
     *                                    request. If set to false it will only send it when the
     *                                    server requests it.
     *
     * @see    com.cordys.coe.util.cgc.config.ICGCConfiguration#setAuthenticationPreemptive(boolean)
     */
    public void setAuthenticationPreemptive(boolean bAuthenticationPreemptive)
    {
        m_bAuthenticationPreemptive = bAuthenticationPreemptive;
    }

    /**
     * This method sets wether or not the gateway checks the responses for soap faults.
     *
     * @param  bCheckForFaults  Whether or not the gateway checks the responses for soap faults.
     *
     * @see    com.cordys.coe.util.cgc.config.ICGCConfiguration#setCheckForFaults(boolean)
     */
    public void setCheckForFaults(boolean bCheckForFaults)
    {
        m_bCheckForFaults = bCheckForFaults;
    }

    /**
     * This method sets the url of the Cordys gateway.
     *
     * @param  sGatewayURL  The url of the Cordys gateway.
     *
     * @see    com.cordys.coe.util.cgc.config.ICGCConfiguration#setGatewayURL(java.lang.String)
     */
    public void setGatewayURL(String sGatewayURL)
    {
        m_sGatewayURL = sGatewayURL;
    }

    /**
     * This method sets the hostname of the cordys gateway.
     *
     * @param  sHost  The hostname of the cordys gateway.
     *
     * @see    com.cordys.coe.util.cgc.config.ICGCConfiguration#setHost(java.lang.String)
     */
    public void setHost(String sHost)
    {
        m_sHost = sHost;
    }

    /**
     * Sets the flag indicating if a login request is sent when the connection is opened.
     *
     * @param  bLoginToCordysOnConnect  If <code>true</code>, a login request is sent.
     *
     * @see    com.cordys.coe.util.cgc.config.ICGCConfiguration#setLoginToCordysOnConnect(boolean)
     */
    public void setLoginToCordysOnConnect(boolean bLoginToCordysOnConnect)
    {
        m_bLoginToCordysOnConnect = bLoginToCordysOnConnect;
    }

    /**
     * This method sets the maximum amount of concurrent calls to this class.
     *
     * @param  iMaxConcurrentCalls  The maximum amount of concurrent calls to this class.
     *
     * @see    com.cordys.coe.util.cgc.config.ICGCConfiguration#setMaxConcurrentCalls(int)
     */
    public void setMaxConcurrentCalls(int iMaxConcurrentCalls)
    {
        m_iMaxConcurrentCalls = iMaxConcurrentCalls;
    }

    /**
     * This method sets the maximum number of connections per host.
     *
     * @param  iMaxConnectionsPerHost  The maximum number of connections per host.
     *
     * @see    com.cordys.coe.util.cgc.config.ICGCConfiguration#setMaxConnectionsPerHost(int)
     */
    public void setMaxConnectionsPerHost(int iMaxConnectionsPerHost)
    {
        m_iMaxConnectionsPerHost = iMaxConnectionsPerHost;
    }

    /**
     * Sets the network TCP/IP timeout to be used for requests. This is separate from the Cordys
     * timeout URL parameter.
     *
     * @param  lNetworkTimeout  Network timeout value (-1 means infinite wait).
     *
     * @see    com.cordys.coe.util.cgc.config.ICGCConfiguration#setNetworkTimeout(long)
     */
    public void setNetworkTimeout(long lNetworkTimeout)
    {
        m_lNetworkTimeout = lNetworkTimeout;
    }

    /**
     * This method sets the port where the Cordys web gateway is running.
     *
     * @param  iPort  The port where the Cordys web gateway is running.
     *
     * @see    com.cordys.coe.util.cgc.config.ICGCConfiguration#setPort(int)
     */
    public void setPort(int iPort)
    {
        m_iPort = iPort;
    }

    /**
     * This method sets the proxy host to use.
     *
     * @param  sProxyHost  The proxy host to use.
     *
     * @see    com.cordys.coe.util.cgc.config.ICGCConfiguration#setProxyHost(java.lang.String)
     */
    public void setProxyHost(String sProxyHost)
    {
        m_sProxyHost = sProxyHost;
    }

    /**
     * This method sets the password for the proxy user.
     *
     * @param  sProxyPassword  The password for the proxy user.
     *
     * @see    com.cordys.coe.util.cgc.config.ICGCConfiguration#setProxyPassword(java.lang.String)
     */
    public void setProxyPassword(String sProxyPassword)
    {
        m_sProxyPassword = sProxyPassword;
    }

    /**
     * This method sets the proxy port to use.
     *
     * @param  iProxyPort  The proxy port to use.
     *
     * @see    com.cordys.coe.util.cgc.config.ICGCConfiguration#setProxyPort(int)
     */
    public void setProxyPort(int iProxyPort)
    {
        m_iProxyPort = iProxyPort;
    }

    /**
     * This method sets the username for the proxy server.
     *
     * @param  sProxyUsername  The username for the proxy server.
     *
     * @see    com.cordys.coe.util.cgc.config.ICGCConfiguration#setProxyUsername(java.lang.String)
     */
    public void setProxyUsername(String sProxyUsername)
    {
        m_sProxyUsername = sProxyUsername;
    }

    /**
     * This method sets the interval in which the server watcher will check if the webserver is
     * still available. If this CGC is created without a server watcher this call has no effect.
     *
     * @param  lServerWatcherPollInterval  The new poll interval.
     *
     * @see    com.cordys.coe.util.cgc.config.ICGCConfiguration#setServerWatcherPollInterval(long)
     */
    public void setServerWatcherPollInterval(long lServerWatcherPollInterval)
    {
        m_lServerWatcherPollInterval = lServerWatcherPollInterval;
    }

    /**
     * This method sets the time to wait between asking the server watcher if the server is alive.
     *
     * @param  lSleepTime  The new sleep time.
     *
     * @see    com.cordys.coe.util.cgc.config.ICGCConfiguration#setSleepTimeBetweenServerWacther(long)
     */
    public void setSleepTimeBetweenServerWacther(long lSleepTime)
    {
        m_lSleepTime = lSleepTime;
    }

    /**
     * This method sets whether or not this connection uses SSL.
     *
     * @param  bSsl  Whether or not this connection uses SSL.
     *
     * @see    com.cordys.coe.util.cgc.config.ICGCConfiguration#setSSL(boolean)
     */
    public void setSSL(boolean bSsl)
    {
        m_bSsl = bSsl;
    }

    /**
     * This method sets the timeout to use.
     *
     * @param  lTimeout  The timeout to use.
     *
     * @see    com.cordys.coe.util.cgc.config.ICGCConfiguration#setTimeout(long)
     */
    public void setTimeout(long lTimeout)
    {
        m_lTimeout = lTimeout;
    }

    /**
     * This method sets wether or not to use the serverwatcher to monitor te Cordys server.
     *
     * @param  bUseServerWatcher  Whether or not to use the serverwatcher to monitor te Cordys
     *                            server.
     *
     * @see    com.cordys.coe.util.cgc.config.ICGCConfiguration#setUseServerWatcher(boolean)
     */
    public void setUseServerWatcher(boolean bUseServerWatcher)
    {
        m_bUseServerWatcher = bUseServerWatcher;
    }

    /**
     * This method gets whether or not to use the serverwatcher to monitor te Cordys server.
     *
     * @return  Whether or not to use the serverwatcher to monitor te Cordys server.
     *
     * @see     com.cordys.coe.util.cgc.config.ICGCConfiguration#useServerWatcher()
     */
    public boolean useServerWatcher()
    {
        return m_bUseServerWatcher;
    }

    /**
     * This method validates the configuration to make sure it contains the minimum configuration to
     * connect to a server.
     *
     * @throws  CordysGatewayClientException  In case the configuration is not valid.
     *
     * @see     com.cordys.coe.util.cgc.config.ICGCConfiguration#validate()
     */

    public void validate()
                  throws CordysGatewayClientException
    {
        if ((m_sHost == null) || (m_sHost.length() == 0))
        {
            throw new CordysGatewayClientException(CGCMessages.CGC_ERROR_MISSING_HOST);
        }
    }
}
