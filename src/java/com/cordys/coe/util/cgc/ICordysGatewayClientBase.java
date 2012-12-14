package com.cordys.coe.util.cgc;

import com.cordys.coe.util.cgc.serverwatcher.ServerWatcherSoapService;
import com.cordys.coe.util.cgc.userinfo.IUserInfo;

import java.io.File;

/**
 * A base interface for DOM and NOM versions of Cordys gateway client.
 * 
 * @author pgussow
 */
public interface ICordysGatewayClientBase
{
    /**
     * This method adds the passed on service to be watched each interval of this watcher thread.
     * 
     * @param swssService the soap service to watch.
     */
    void addSoapServiceToWatch(ServerWatcherSoapService swssService);

    /**
     * Connect to Cordys via cordys gateway.
     * 
     * @throws CordysGatewayClientException Exception something is wrong TODO: bedenk wat mooie excepties
     */
    void connect() throws CordysGatewayClientException;

    /**
     * Disconnect from the cordys gateway. Free all cordys resources
     */
    void disconnect();

    /**
     * This method returns the DN of the authenticated user.
     * 
     * @return The DN of the authenticated user.
     */
    String getAuthUserDN();

    /**
     * This method gets the NT domain to use.
     * 
     * @return The NT domain to use.
     */
    String getDomain();

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
     * Returns the network TCP/IP timeout to be used for requests.
     * 
     * @return Network timeout.
     */
    long getNetworkTimeout();

    /**
     * This method gets the password to use..
     * 
     * @return The password to use..
     */
    String getPassword();

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
     * This method gets the proxy port to use.
     * 
     * @return The proxy port to use.
     */
    int getProxyPort();

    /**
     * This method gets the timeout to use.
     * 
     * @return The timeout to use.
     */
    long getTimeout();

    /**
     * This method gets the information about the current user.
     * 
     * @return The information about the current user.
     */
    IUserInfo getUserInfo();

    /**
     * This method gets the username to use.
     * 
     * @return The username to use.
     */
    String getUsername();

    /**
     * This method gets the WCP session ID to use.
     * 
     * @return The WCP session ID to use.
     */
    String getWCPSessionID();

    /**
     * Check if the logged on user has a certain cordys role.
     * 
     * @param aRole The role to check for
     * @return true if the role is assigned to the logged on user
     * @throws CordysGatewayClientException In case the user info is not available.
     */
    boolean hasRole(String aRole) throws CordysGatewayClientException;

    /**
     * This method gets whether or not the gateway checks the responses for soap faults.
     * 
     * @return Whether or not the gateway checks the responses for soap faults.
     */
    boolean isCheckingForFaults();

    /**
     * Test if we have an open connection to cordys.
     * 
     * @return true if connected
     */
    boolean isConnected();

    /**
     * This method gets whether or not this connection uses SSL.
     * 
     * @return Whether or not this connection uses SSL.
     */
    boolean isSSL();

    /**
     * Send a soap message to cordys.
     * 
     * @param aInputSoapRequest The soap request as string
     * @param lTimeout The timeout to use.
     * @return the result as soap response as string
     * @throws CordysGatewayClientException In case of any exceptions.
     */
    String requestFromCordys(String aInputSoapRequest, long lTimeout) throws CordysGatewayClientException;

    /**
     * This method sets wether or not the gateway checks the responses for soap faults.
     * 
     * @param bCheckForFaults Whether or not the gateway checks the responses for soap faults.
     */
    void setCheckForFaults(boolean bCheckForFaults);

    /**
     * This method sets the NT domain to use.
     * 
     * @param sNTDomain The NT domain to use.
     */
    void setDomain(String sNTDomain);

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
     * @param bValue If <code>true</code>, a login request is sent.
     */
    void setLoginToCordysOnConnect(boolean bValue);
    
    /**
     * Sets the flag indicating if a the login response should be parsed automatically when it is received. A reason not to do
     * this is because the parsing takes a couple of seconds.
     * 
     * @param autoParseGetUserDetails If <code>true</code>, the login response is parsed.
     */
    void setAutoParseGetUserDetails(boolean autoParseGetUserDetails);

    /**
     * Sets the network TCP/IP timeout to be used for requests. This is separate from the Cordys timeout URL parameter.
     * 
     * @param lValue Network timeout value (-1 means infinite wait).
     */
    void setNetworkTimeout(long lValue);

    /**
     * This method sets the organization to use for this gateway.
     * 
     * @param sOrganization The new organization.
     */
    void setOrganization(String sOrganization);

    /**
     * This method sets the password to use..
     * 
     * @param sPassword The password to use..
     */
    void setPassword(String sPassword);

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
     * This method sets the proxy port to use.
     * 
     * @param iProxyPort The proxy port to use.
     */
    void setProxyPort(int iProxyPort);

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
     * This method sets the username to use.
     * 
     * @param sUsername The username to use.
     */
    void setUsername(String sUsername);

    /**
     * This method sets the WCP session ID to use.
     * 
     * @param sWCPSessionID The WCP session ID to use.
     */
    void setWCPSessionID(String sWCPSessionID);

    /**
     * This method will upload a file using the Upload.wcp extension.
     * 
     * @param request The XML request that needs to be sent.
     * @param file The actual file that needs to be uploaded.
     * @param organization The current organizational context.
     * @param timeout the timeout to use.
     * @param receiver the receiver to send it to.
     * @param blockIfServerIsDown Whether or not to block in case the server is down.
     * @return The response XML message.
     * @throws CordysGatewayClientException IOn case of any exceptions.
     */
    String uploadFile(String request, File file, String organization, long timeout, String receiver, boolean blockIfServerIsDown)
            throws CordysGatewayClientException;
}
