package com.cordys.coe.util.cgc.config;

import com.cordys.coe.util.cgc.CordysGatewayClientException;

/**
 * This interface contains the methods for the basic configuration of the Cordys Gateway Client. This interface has no connection
 * with either the Authentication scheme to be used nor with any XML library.
 * 
 * @author pgussow
 */
public interface ICGCConfiguration
{
    /**
     * This method gets whether or not the login request should be sent with each request. If set to false it will only send it
     * when the server requests it.
     * 
     * @return Whether or not the login request should be sent with each request. If set to false it will only send it when the
     *         server requests it.
     */
    boolean getAuthenticationPreemptive();

    /**
     * This method gets the url of the Cordys gateway.
     * 
     * @return The url of the Cordys gateway.
     */
    String getGatewayURL();

    /**
     * This method gets the hostname of the cordys gateway.
     * 
     * @return The hostname of the cordys gateway.
     */
    String getHost();

    /**
     * Returns the flag indicating if a login request is sent when the connection is opened.
     * 
     * @return If <code>true</code>, a login request is sent.
     */
    boolean getLoginToCordysOnConnect();

    /**
     * Returns the flag indicating if a the login response should be parsed automatically when it is received. A reason not to do
     * this is because the parsing takes a couple of seconds.
     * 
     * @return If <code>true</code>, the response is parsed.
     */
    boolean getAutoParseGetUserDetails();

    /**
     * This method gets the maximum amount of concurrent calls to this class.
     * 
     * @return The maximum amount of concurrent calls to this class.
     */
    int getMaxConcurrentCalls();

    /**
     * This method gets the maximum number of connections per host.
     * 
     * @return The maximum number of connections per host.
     */
    int getMaxConnectionsPerHost();

    /**
     * Returns the network TCP/IP timeout to be used for requests.
     * 
     * @return Network timeout.
     */
    long getNetworkTimeout();

    /**
     * This method gets the port where the Cordys web gateway is running.
     * 
     * @return The port where the Cordys web gateway is running.
     */
    int getPort();

    /**
     * This method gets the proxy host to use.
     * 
     * @return The proxy host to use.
     */
    String getProxyHost();

    /**
     * This method gets the password for the proxy user.
     * 
     * @return The password for the proxy user.
     */
    String getProxyPassword();

    /**
     * This method gets the proxy port to use.
     * 
     * @return The proxy port to use.
     */
    int getProxyPort();

    /**
     * This method gets the username for the proxy server.
     * 
     * @return The username for the proxy server.
     */
    String getProxyUsername();

    /**
     * This method gets the interval in which the server watcher will check if the webserver is still available. If this CGC is
     * created without a server watcher this call has no effect.
     * 
     * @return The new poll interval.
     */
    long getServerWatcherPollInterval();

    /**
     * This method gets the time to wait between asking the server watcher if the server is alive.
     * 
     * @return The time to wait between asking the server watcher if the server is alive.
     */
    long getSleepTimeBetweenServerWacther();

    /**
     * This method gets the timeout to use.
     * 
     * @return The timeout to use.
     */
    long getTimeout();

    /**
     * This method gets whether or not the gateway checks the responses for soap faults. This method has no effect from C3 since
     * C# is BasicProfile compliant and this will return a HTTP error code 500.
     * 
     * @return Whether or not the gateway checks the responses for soap faults.
     */
    boolean isCheckingForFaults();

    /**
     * This method gets whether or not a proxy server is configured.
     * 
     * @return Whether or not a proxy server is configured.
     */
    boolean isProxyServerSet();

    /**
     * This method gets whether or not SSL should be used.
     * 
     * @return Whether or not SSL should be used.
     */
    boolean isSSL();

    /**
     * This method sets wether or not the login request should be sent with each request. If set to false it will only send it
     * when the server requests it.
     * 
     * @param bAuthenticationPreemptive Whether or not the login request should be sent with each request. If set to false it will
     *            only send it when the server requests it.
     */
    void setAuthenticationPreemptive(boolean bAuthenticationPreemptive);

    /**
     * This method sets wether or not the gateway checks the responses for soap faults.
     * 
     * @param bCheckForFaults Whether or not the gateway checks the responses for soap faults.
     */
    void setCheckForFaults(boolean bCheckForFaults);

    /**
     * This method sets the url of the Cordys gateway.
     * 
     * @param sGatewayURL The url of the Cordys gateway.
     */
    void setGatewayURL(String sGatewayURL);

    /**
     * This method sets the hostname of the cordys gateway.
     * 
     * @param sHost The hostname of the cordys gateway.
     */
    void setHost(String sHost);

    /**
     * Sets the flag indicating if a login request is sent when the connection is opened.
     * 
     * @param bLoginToCordysOnConnect If <code>true</code>, a login request is sent.
     */
    void setLoginToCordysOnConnect(boolean bLoginToCordysOnConnect);

    /**
     * Sets the flag indicating if a the login response should be parsed automatically when it is received. A reason not to do
     * this is because the parsing takes a couple of seconds.
     * 
     * @param autoParseGetUserDetails If <code>true</code>, the login response is parsed.
     */
    void setAutoParseGetUserDetails(boolean autoParseGetUserDetails);

    /**
     * This method sets the maximum amount of concurrent calls to this class.
     * 
     * @param iMaxConcurrentCalls The maximum amount of concurrent calls to this class.
     */
    void setMaxConcurrentCalls(int iMaxConcurrentCalls);

    /**
     * This method sets the maximum number of connections per host.
     * 
     * @param iMaxConnectionsPerHost The maximum number of connections per host.
     */
    void setMaxConnectionsPerHost(int iMaxConnectionsPerHost);

    /**
     * Sets the network TCP/IP timeout to be used for requests. This is separate from the Cordys timeout URL parameter.
     * 
     * @param lNetworkTimeout Network timeout value (-1 means infinite wait).
     */
    void setNetworkTimeout(long lNetworkTimeout);

    /**
     * This method sets the port where the Cordys web gateway is running.
     * 
     * @param iPort The port where the Cordys web gateway is running.
     */
    void setPort(int iPort);

    /**
     * This method sets the proxy host to use.
     * 
     * @param sProxyHost The proxy host to use.
     */
    void setProxyHost(String sProxyHost);

    /**
     * This method sets the password for the proxy user.
     * 
     * @param sProxyPassword The password for the proxy user.
     */
    void setProxyPassword(String sProxyPassword);

    /**
     * This method sets the proxy port to use.
     * 
     * @param iProxyPort The proxy port to use.
     */
    void setProxyPort(int iProxyPort);

    /**
     * This method sets the username for the proxy server.
     * 
     * @param sProxyUsername The username for the proxy server.
     */
    void setProxyUsername(String sProxyUsername);

    /**
     * This method sets the interval in which the server watcher will check if the webserver is still available. If this CGC is
     * created without a server watcher this call has no effect.
     * 
     * @param lServerWatcherPollInterval The new poll interval.
     */
    void setServerWatcherPollInterval(long lServerWatcherPollInterval);

    /**
     * This method sets the time to wait between asking the server watcher if the server is alive.
     * 
     * @param lSleepTime The new sleep time.
     */
    void setSleepTimeBetweenServerWacther(long lSleepTime);

    /**
     * This method sets whether or not this connection uses SSL.
     * 
     * @param bSsl Whether or not this connection uses SSL.
     */
    void setSSL(boolean bSsl);

    /**
     * This method sets the timeout to use.
     * 
     * @param lTimeout The timeout to use.
     */
    void setTimeout(long lTimeout);

    /**
     * This method sets wether or not to use the serverwatcher to monitor te Cordys server.
     * 
     * @param bUseServerWatcher Whether or not to use the serverwatcher to monitor te Cordys server.
     */
    void setUseServerWatcher(boolean bUseServerWatcher);

    /**
     * This method gets whether or not to use the serverwatcher to monitor te Cordys server.
     * 
     * @return Whether or not to use the serverwatcher to monitor te Cordys server.
     */
    boolean useServerWatcher();

    /**
     * This method validates the configuration to make sure it contains the minimum configuration to connect to a server.
     * 
     * @throws CordysGatewayClientException DOCUMENTME
     */
    void validate() throws CordysGatewayClientException;
    
    /**
     * This method gets the display url.
     * 
     * @return The display url.
     */
    String getDisplayURL();
}
