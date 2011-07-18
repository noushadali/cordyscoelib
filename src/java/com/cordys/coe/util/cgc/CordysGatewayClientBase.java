package com.cordys.coe.util.cgc;

import com.cordys.coe.util.IPoolWorker;
import com.cordys.coe.util.TokenPool;
import com.cordys.coe.util.cgc.config.IAuthenticationConfiguration;
import com.cordys.coe.util.cgc.config.ICGCConfiguration;
import com.cordys.coe.util.cgc.config.ICGCSSLConfiguration;
import com.cordys.coe.util.cgc.config.IClientCertificateAuthentication;
import com.cordys.coe.util.cgc.config.ICordysCustomAuthentication;
import com.cordys.coe.util.cgc.config.INTLMAuthentication;
import com.cordys.coe.util.cgc.config.IUsernamePasswordAuthentication;
import com.cordys.coe.util.cgc.message.CGCMessages;
import com.cordys.coe.util.cgc.serverwatcher.ServerWatcher;
import com.cordys.coe.util.cgc.serverwatcher.ServerWatcherSoapService;
import com.cordys.coe.util.cgc.ssl.AuthSSLProtocolSocketFactory;
import com.cordys.coe.util.cgc.userinfo.IUserInfo;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.net.URLEncoder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthPolicy;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;

import org.apache.log4j.Logger;

/**
 * This class can be used to communicate with the Cordys Web Gateway. It supports 3 types of
 * authentication: - Basic - NTLM - Certificates. This class is thread safe. This means that
 * multiple threads can use the same instance of this object to call methods on the Cordys server.
 * If you need to connect under multiple users you need to make an instance per user.<br>
 * Example code for NTLM: <code>String sUser = "pgussow"; String sPassword = "password"; String
 * sServer = "srv-nl-ces20"; String sDomain = "NTDOM"; int iPort = 80; ICordysGatewayClient cgc =
 * new CordysGatewayClient(sUser, sPassword, sServer, iPort, sDomain); cgc.connect();</code>
 *
 * @author  pgussow
 */
public abstract class CordysGatewayClientBase
    implements ICordysGatewayClientBase
{
    /**
     * Holds the logger to use for this class.
     */
    private static final Logger LOG = Logger.getLogger(CordysGatewayClientBase.class);
    /**
     * This holds the base template for calling a Cordy soap method.
     */
    protected static final byte[] BASE_SOAP_REQUEST = ("<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                                                       "<SOAP:Body/></SOAP:Envelope>").getBytes();
    /**
     * This holds the GetUserDetails request.
     */
    protected static final byte[] XML_GET_USER_DETAILS = ("<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                                                          "<SOAP:Body>" +
                                                          "<GetUserDetails xmlns=\"http://schemas.cordys.com/1.1/ldap\"/>" +
                                                          "</SOAP:Body></SOAP:Envelope>")
                                                         .getBytes();
    /**
     * This holds the logon request.
     */
    protected static final byte[] XML_AUTHENTICATE = ("<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                                                      "<SOAP:Body>" +
                                                      "<Authenticate xmlns=\"http://schemas.cordys.com/1.0/webgateway\"/>" +
                                                      "</SOAP:Body></SOAP:Envelope>").getBytes();
    /**
     * This holds the request to get a SAML token.
     */
    protected static final byte[] XML_SSO_LOGIN = ("<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                                                   "<SOAP:Header><wsse:Security xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">" +
                                                   "<wsse:UsernameToken xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">" +
                                                   "<wsse:Username></wsse:Username>" +
                                                   "<wsse:Password></wsse:Password>" +
                                                   "</wsse:UsernameToken>" +
                                                   "</wsse:Security></SOAP:Header>" +
                                                   "<SOAP:Body>" +
                                                   "<samlp:Request xmlns:samlp=\"urn:oasis:names:tc:SAML:1.0:protocol\" MajorVersion=\"1\" MinorVersion=\"1\" IssueInstant=\"\" RequestID=\"\"><samlp:AuthenticationQuery>" +
                                                   "<saml:Subject xmlns:saml=\"urn:oasis:names:tc:SAML:1.0:assertion\">" +
                                                   "<saml:NameIdentifier Format=\"urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified\"></saml:NameIdentifier>" +
                                                   "</saml:Subject></samlp:AuthenticationQuery></samlp:Request>" +
                                                   "</SOAP:Body></SOAP:Envelope>").getBytes();
    /**
     * This holds the request for executing LDAP searches.
     */
    protected static final byte[] XML_SEARCH_LDAP = ("<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP:Body>" +
                                                     "<SearchLDAP xmlns=\"http://schemas.cordys.com/1.0/ldap\">" +
                                                     "<dn/><scope/><filter/><sort>ascending</sort><returnValues>true</returnValues>" +
                                                     "</SearchLDAP></SOAP:Body></SOAP:Envelope>")
                                                    .getBytes();
    /**
     * This holds the request for reading a single LDAP entry.
     */
    protected static final byte[] XML_GET_LDAP_OBJECT = ("<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP:Body>" +
                                                         "<GetLDAPObject xmlns=\"http://schemas.cordys.com/1.0/ldap\">" +
                                                         "<dn/>" +
                                                         "</GetLDAPObject></SOAP:Body></SOAP:Envelope>")
                                                        .getBytes();
    /**
     * This holds the request for updating a single LDAP entry.
     */
    protected static final byte[] XML_UPDATE_LDAP = ("<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP:Body>" +
                                                     "<Update xmlns=\"http://schemas.cordys.com/1.0/ldap\">" +
                                                     "<tuple/>" +
                                                     "</Update></SOAP:Body></SOAP:Envelope>")
                                                    .getBytes();
    /**
     * Contains the name of the SOAP action header.
     */
    protected static final String SOAP_ACTION_HEADER = "SOAPAction";
    /**
     * Contains the name of the HTTP Content-Type header.
     */
    protected static final String HTTP_CONTENTYPE_HEADER = "Content-Type";
    /**
     * Contains the SOAP Content-Type header value.
     */
    protected static final String SOAP_CONTENTYPE_UTF_8 = "text/xml; charset=utf-8";
    /**
     * Holds the custom authentication gateway.
     */
    protected static final String AUTHENTICATION_GATEWAY = "com.eibus.web.soap.Authenticate.wcp";
    /**
     * Idicates whether or not the gateway is connected.
     */
    protected boolean m_bConnected = false;
    /**
     * Holds the HTTP client.
     */
    protected HttpClient m_hcClient;
    /**
     * Holds the host configuration.
     */
    protected HostConfiguration m_hcHostConfiguration;
    /**
     * Holds the tokens for calls to the backend.
     */
    protected TokenPool m_oRequestTokens = null;
    /**
     * Holds the protocol for HTTPS.
     */
    protected Protocol m_pHTTPSProtocol = null;
    /**
     * Holds the organization.
     */
    protected String m_sOrganization = null;
    /**
     * Holds the server watcher. This object is responsible for monitoring the server to make sure
     * it is still running. It depends on how this class is created whether or not is is being used.
     */
    protected ServerWatcher m_swWatcher = null;
    /**
     * This class contains the authentication details for the current client.
     */
    private IAuthenticationConfiguration m_acAuthenticationDetails;
    /**
     * If true login is sent with every request, otherwise it is sent only when the server requests
     * it first (server will send a reply with error code unauthorized and in the second request we
     * set the login credentials.
     */
    private boolean m_bAuthenticationPreemptive = false;
    /**
     * Holds the configuration for this Cordys Gateway Client.
     */
    private ICGCConfiguration m_ccConfiguration;
    /**
     * Holds the user information.
     */
    private IUserInfo m_uiUserInfo;

    /**
     * Constructor.
     *
     * @param   acAuthentication  The authentication details for connecting to the web gateway.
     * @param   ccConfiguration   The CGC configuration.
     *
     * @throws  CordysGatewayClientException  In case of any configuration exceptions.
     */
    protected CordysGatewayClientBase(IAuthenticationConfiguration acAuthentication,
                                      ICGCConfiguration ccConfiguration)
                               throws CordysGatewayClientException
    {
        m_acAuthenticationDetails = acAuthentication;
        m_ccConfiguration = ccConfiguration;

        if (IClientCertificateAuthentication.class.isAssignableFrom(acAuthentication.getClass()))
        {
            ((IClientCertificateAuthentication) acAuthentication).validate();
        }

        // Validate the configuration.
        m_ccConfiguration.validate();

        // When using client certificate logins you must use SSL as well.
        if ((ccConfiguration.isSSL() == false) &&
                IClientCertificateAuthentication.class.isAssignableFrom(acAuthentication.getClass()))
        {
            throw new CordysGatewayClientException(CGCMessages.CGC_ERROR_MUST_BE_SSL);
        }

        // Create the worker pool.
        m_oRequestTokens = new TokenPool(ccConfiguration.getMaxConcurrentCalls());

        // Already create the Apache HTTP Client host configuration.
        m_hcHostConfiguration = new HostConfiguration();

        // If needed configure the SSL.
        if (ccConfiguration.isSSL() == true)
        {
            // There are several SSL combinations:
            // 1. Client certificate authetication + SSL
            // 2. User/Pass authentication + SSL
            // 3. Use the default Java trust store.
            // 4. USe a configured keystore.
            ICGCSSLConfiguration cgcSSLConfig = (ICGCSSLConfiguration) ccConfiguration;
            IClientCertificateAuthentication ccaClientCertificate = null;

            if (IClientCertificateAuthentication.class.isAssignableFrom(acAuthentication.getClass()))
            {
                ccaClientCertificate = (IClientCertificateAuthentication) acAuthentication;
            }

            AuthSSLProtocolSocketFactory psfFactory = new AuthSSLProtocolSocketFactory(cgcSSLConfig,
                                                                                       ccaClientCertificate);

            m_pHTTPSProtocol = new Protocol("https", psfFactory, ccConfiguration.getPort());
            Protocol.registerProtocol("https", m_pHTTPSProtocol);

            m_hcHostConfiguration.setHost(ccConfiguration.getHost(), ccConfiguration.getPort(),
                                          m_pHTTPSProtocol);
        }
        else
        {
            // Standard HTTP traffic.
            m_hcHostConfiguration.setHost(ccConfiguration.getHost(), ccConfiguration.getPort(),
                                          "http");
        }

        // If a proxy server is defined configure it.
        if (ccConfiguration.isProxyServerSet())
        {
            m_hcHostConfiguration.setProxy(ccConfiguration.getProxyHost(),
                                           ccConfiguration.getProxyPort());
        }

        // Create the server watcher if needed.
        if (ccConfiguration.useServerWatcher() == true)
        {
            if (getLogger().isDebugEnabled())
            {
                getLogger().debug("Going to create the server watcher for " +
                                  ccConfiguration.getHost() + ":" + ccConfiguration.getPort() +
                                  " with an interval of " +
                                  ccConfiguration.getServerWatcherPollInterval() + "ms.");
            }

            m_swWatcher = new ServerWatcher(ccConfiguration.getHost(), ccConfiguration.getPort(),
                                            ccConfiguration.getServerWatcherPollInterval());
            m_swWatcher.start();
        }
    }

    /**
     * This method adds the passed on service to be watched each interval of this watcher thread.
     *
     * @param  swssService  the soap service to watch.
     */
    public void addSoapServiceToWatch(ServerWatcherSoapService swssService)
    {
        if (m_swWatcher != null)
        {
            m_swWatcher.addSoapServiceToWatch(swssService);
        }
    }

    /**
     * Connect to Cordys via cordys gateway.
     *
     * @throws  CordysGatewayClientException  Exception something is wrong TODO: bedenk wat mooie
     *                                        excepties
     */
    public void connect()
                 throws CordysGatewayClientException
    {
        if (m_hcClient != null)
        {
            this.disconnect();
        }

        try
        {
            // Create the multi threaded pool.
            HttpConnectionManagerParams hcmp = new HttpConnectionManagerParams();
            hcmp.setDefaultMaxConnectionsPerHost(m_ccConfiguration.getMaxConnectionsPerHost());

            if (m_ccConfiguration.getNetworkTimeout() > 0)
            {
                hcmp.setConnectionTimeout((int) m_ccConfiguration.getNetworkTimeout());
            }

            MultiThreadedHttpConnectionManager mthcmManager = new MultiThreadedHttpConnectionManager();
            mthcmManager.setParams(hcmp);

            // Create the actual HTTP client.
            m_hcClient = new HttpClient(mthcmManager);
            m_hcClient.setHostConfiguration(m_hcHostConfiguration);

            // Create the proper credentials.
            if (INTLMAuthentication.class.isAssignableFrom(m_acAuthenticationDetails.getClass()))
            {
                INTLMAuthentication naNTLM = (INTLMAuthentication) m_acAuthenticationDetails;

                NTCredentials ncCredentials = new NTCredentials(naNTLM.getUsername(),
                                                                naNTLM.getPassword(),
                                                                m_ccConfiguration.getHost(),
                                                                naNTLM.getDomain());

                m_hcClient.getState().setCredentials(new AuthScope(m_ccConfiguration.getHost(),
                                                                   m_ccConfiguration.getPort()),
                                                     ncCredentials);
            }
            else if (ICordysCustomAuthentication.class.isAssignableFrom(m_acAuthenticationDetails
                                                                            .getClass()))
            {
                // Nothing needs to be done, because the authentication will be done differently.
            }
            else if (IUsernamePasswordAuthentication.class.isAssignableFrom(m_acAuthenticationDetails
                                                                                .getClass()))
            {
                IUsernamePasswordAuthentication upa = (IUsernamePasswordAuthentication)
                                                          m_acAuthenticationDetails;

                UsernamePasswordCredentials upcCredentials = new UsernamePasswordCredentials(upa.getUsername(),
                                                                                             upa.getPassword());
                m_hcClient.getState().setCredentials(new AuthScope(m_ccConfiguration.getHost(),
                                                                   m_ccConfiguration.getPort()),
                                                     upcCredentials);

                // Move NTLM authentication as last, because it is being tried first
                // event if basic authentication is being used.
                List<String> authPrefs = new ArrayList<String>(3);

                if (m_bAuthenticationPreemptive)
                {
                    m_hcClient.getParams().setAuthenticationPreemptive(true);
                }

                authPrefs.add(AuthPolicy.BASIC);
                authPrefs.add(AuthPolicy.DIGEST);
                authPrefs.add(AuthPolicy.NTLM);

                m_hcClient.getParams().setParameter(AuthPolicy.AUTH_SCHEME_PRIORITY, authPrefs);
            }

            // Send the message to Cordys.
            sendLoginMessage();

            m_bConnected = true;
        }
        catch (CordysGatewayClientException cgce)
        {
            throw cgce;
        }
        catch (Exception ex)
        {
            throw new CordysGatewayClientException(ex, CGCMessages.CGC_CONNECT, getHost(),
                                                   getPort());
        }
    }

    /**
     * Disconnect from the cordys gateway. Free all cordys resources
     */
    public void disconnect()
    {
        m_hcClient = null;
        m_bConnected = false;
    }

    /**
     * This method gets the authentication details.
     *
     * @return  The authentication details.
     */
    public IAuthenticationConfiguration getAuthenticationDetails()
    {
        return m_acAuthenticationDetails;
    }

    /**
     * This method gets the configuration details.
     *
     * @return  The configuration details.
     */
    public ICGCConfiguration getConfiguration()
    {
        return m_ccConfiguration;
    }

    /**
     * This method gets the NT domain to use.
     *
     * @return  The NT domain to use.
     */
    public String getDomain()
    {
        String sReturn = null;

        if (INTLMAuthentication.class.isAssignableFrom(m_acAuthenticationDetails.getClass()))
        {
            INTLMAuthentication na = (INTLMAuthentication) m_acAuthenticationDetails;
            sReturn = na.getDomain();
        }

        return sReturn;
    }

    /**
     * This method gets the url of the Cordys gateway.
     *
     * @return  The url of the Cordys gateway.
     */
    public String getGatewayURL()
    {
        return m_ccConfiguration.getGatewayURL();
    }

    /**
     * This method gets the hostname of the cordys gateway.
     *
     * @return  The hostname of the cordys gateway.
     */
    public String getHost()
    {
        return m_ccConfiguration.getHost();
    }

    /**
     * This method gets the Logger for this class.
     *
     * @return  The Logger for this class.
     */
    public Logger getLogger()
    {
        return LOG;
    }

    /**
     * Returns the flag indicating if a login request is sent when the connection is opened.
     *
     * @return  If <code>true</code>, a login request is sent.
     */
    public boolean getLoginToCordysOnConnect()
    {
        return m_ccConfiguration.getLoginToCordysOnConnect();
    }

    /**
     * Get a list of organisations for this user.
     *
     * @return      Returns the logonInfo - organisations as an array of strings.
     *
     * @deprecated  This method has never been implemented and will be removed. Use getuserInfo
     *              instead.
     */
    public ArrayList<String> getLogonInfoOrganisations()
    {
        throw new RuntimeException("Will be removed");
    }

    /**
     * This method returns the list of roles this user has in a certain organization.
     *
     * @param       aOrganisation  The organization.
     *
     * @return      Returns the logonInfo - roles.
     *
     * @deprecated  This method has never been implemented and will be removed. Use getuserInfo
     *              instead.
     */
    public ArrayList<String> getLogonInfoRoles(String aOrganisation)
    {
        throw new RuntimeException("Will be removed");
    }

    /**
     * Returns the network TCP/IP timeout to be used for requests.
     *
     * @return  Network timeout.
     */
    public long getNetworkTimeout()
    {
        return m_ccConfiguration.getNetworkTimeout();
    }

    /**
     * This method gets the password to use..
     *
     * @return  The password to use..
     */
    public String getPassword()
    {
        String sReturn = null;

        if (IUsernamePasswordAuthentication.class.isAssignableFrom(m_acAuthenticationDetails
                                                                       .getClass()))
        {
            IUsernamePasswordAuthentication na = (IUsernamePasswordAuthentication)
                                                     m_acAuthenticationDetails;
            sReturn = na.getPassword();
        }

        return sReturn;
    }

    /**
     * This method gets the port where the Cordys web gateway is running.
     *
     * @return  The port where the Cordys web gateway is running.
     */
    public int getPort()
    {
        return m_ccConfiguration.getPort();
    }

    /**
     * This method gets the proxy host to use.
     *
     * @return  The proxy host to use.
     */
    public String getProxyHost()
    {
        return m_ccConfiguration.getProxyHost();
    }

    /**
     * This method gets the proxy port to use.
     *
     * @return  The proxy port to use.
     */
    public int getProxyPort()
    {
        return m_ccConfiguration.getProxyPort();
    }

    /**
     * This method gets the timeout to use.
     *
     * @return  The timeout to use.
     */
    public long getTimeout()
    {
        return m_ccConfiguration.getTimeout();
    }

    /**
     * This method gets the information about the current user.
     *
     * @return  The information about the current user.
     */
    public IUserInfo getUserInfo()
    {
        if (m_uiUserInfo == null)
        {
            try
            {
                sendLoginMessage();
            }
            catch (CordysGatewayClientException e)
            {
                throw new RuntimeException(e);
            }
        }
        return m_uiUserInfo;
    }

    /**
     * This method gets the username to use.
     *
     * @return  The username to use.
     */
    public String getUsername()
    {
        String sReturn = null;

        if (IUsernamePasswordAuthentication.class.isAssignableFrom(m_acAuthenticationDetails
                                                                       .getClass()))
        {
            IUsernamePasswordAuthentication na = (IUsernamePasswordAuthentication)
                                                     m_acAuthenticationDetails;
            sReturn = na.getUsername();
        }

        return sReturn;
    }

    /**
     * This method gets the WCP session ID to use.
     *
     * @return  The WCP session ID to use.
     *
     * @see     com.cordys.coe.util.cgc.ICordysGatewayClientBase#getWCPSessionID()
     */
    public String getWCPSessionID()
    {
        String sReturn = null;

        if (ICordysCustomAuthentication.class.isAssignableFrom(m_acAuthenticationDetails.getClass()))
        {
            ICordysCustomAuthentication cca = (ICordysCustomAuthentication)
                                                  m_acAuthenticationDetails;
            sReturn = cca.getWCPSessionID();
        }

        return sReturn;
    }

    /**
     * Check if the logged on user has a certain cordys role.
     *
     * @param   sRoleDN  The role to check for
     *
     * @return  true if the role is assigned to the logged on user
     *
     * @throws  CordysGatewayClientException  In case the user information is not available.
     */
    public boolean hasRole(String sRoleDN)
                    throws CordysGatewayClientException
    {
        if (m_uiUserInfo == null)
        {
            throw new CordysGatewayClientException(CGCMessages.CGC_ERROR_USERINFO_MISSING);
        }

        return m_uiUserInfo.hasRole(m_sOrganization, sRoleDN);
    }

    /**
     * This method gets whether or not the gateway checks the responses for soap faults.
     *
     * @return  Whether or not the gateway checks the responses for soap faults.
     */
    public boolean isCheckingForFaults()
    {
        return m_ccConfiguration.isCheckingForFaults();
    }

    /**
     * Test if we have an open connection to cordys.
     *
     * @return  true if connected
     */
    public boolean isConnected()
    {
        return m_bConnected;
    }

    /**
     * This method gets whether or not this connection uses SSL.
     *
     * @return  Whether or not this connection uses SSL.
     */
    public boolean isSSL()
    {
        return m_ccConfiguration.isSSL();
    }

    /**
     * This method sets wether or not the gateway checks the responses for soap faults.
     *
     * @param  bCheckForFaults  Whether or not the gateway checks the responses for soap faults.
     */
    public void setCheckForFaults(boolean bCheckForFaults)
    {
        m_ccConfiguration.setCheckForFaults(bCheckForFaults);
    }

    /**
     * This method sets the NT domain to use.
     *
     * @param  sNTDomain  The NT domain to use.
     */
    public void setDomain(String sNTDomain)
    {
        if (m_acAuthenticationDetails instanceof INTLMAuthentication)
        {
            INTLMAuthentication upa = (INTLMAuthentication) m_acAuthenticationDetails;
            upa.setDomain(sNTDomain);
        }
    }

    /**
     * This method sets the url of the Cordys gateway.
     *
     * @param  sGatewayURL  The url of the Cordys gateway.
     */
    public void setGatewayURL(String sGatewayURL)
    {
        m_ccConfiguration.setGatewayURL(sGatewayURL);
    }

    /**
     * This method sets the hostname of the cordys gateway.
     *
     * @param  sHost  The hostname of the cordys gateway.
     */
    public void setHost(String sHost)
    {
        m_ccConfiguration.setHost(sHost);
    }

    /**
     * Sets the flag indicating if a login request is sent when the connection is opened.
     *
     * @param  bValue  If <code>true</code>, a login request is sent.
     */
    public void setLoginToCordysOnConnect(boolean bValue)
    {
        m_ccConfiguration.setLoginToCordysOnConnect(bValue);
    }

    /**
     * Sets the network TCP/IP timeout to be used for requests. This is separate from the Cordys
     * timeout URL parameter.
     *
     * @param  lValue  Network timeout value (-1 means infinite wait).
     */
    public void setNetworkTimeout(long lValue)
    {
        m_ccConfiguration.setNetworkTimeout(lValue);
    }

    /**
     * This method sets the organization to use for this gateway.
     *
     * @param  sOrganization  The new organization.
     */
    public void setOrganization(String sOrganization)
    {
        this.m_sOrganization = sOrganization;
    }

    /**
     * This method sets the password to use..
     *
     * @param  sPassword  The password to use..
     */
    public void setPassword(String sPassword)
    {
        if (m_acAuthenticationDetails instanceof IUsernamePasswordAuthentication)
        {
            IUsernamePasswordAuthentication upa = (IUsernamePasswordAuthentication)
                                                      m_acAuthenticationDetails;
            upa.setPassword(sPassword);
        }
    }

    /**
     * This method sets the port where the Cordys web gateway is running.
     *
     * @param  iPort  The port where the Cordys web gateway is running.
     */
    public void setPort(int iPort)
    {
        m_ccConfiguration.setPort(iPort);
    }

    /**
     * This method sets the proxy host to use.
     *
     * @param  sProxyHost  The proxy host to use.
     */
    public void setProxyHost(String sProxyHost)
    {
        m_ccConfiguration.setProxyHost(sProxyHost);
    }

    /**
     * This method sets the proxy port to use.
     *
     * @param  iProxyPort  The proxy port to use.
     */
    public void setProxyPort(int iProxyPort)
    {
        m_ccConfiguration.setProxyPort(iProxyPort);
    }

    /**
     * This method sets the interval in which the server watcher will check if the webserver is
     * still available. If this CGC is created without a server watcher this call has no effect.
     *
     * @param  lServerWatcherPollInterval  The new poll interval.
     */
    public void setServerWatcherPollInterval(long lServerWatcherPollInterval)
    {
        if (m_ccConfiguration.useServerWatcher() && (m_swWatcher != null))
        {
            m_swWatcher.setPollInterval(lServerWatcherPollInterval);
            m_ccConfiguration.setServerWatcherPollInterval(lServerWatcherPollInterval);
        }
    }

    /**
     * This method sets the time to wait between asking the server watcher if the server is alive.
     *
     * @param  lSleepTime  The new sleep time.
     */
    public void setSleepTimeBetweenServerWacther(long lSleepTime)
    {
        m_ccConfiguration.setSleepTimeBetweenServerWacther(lSleepTime);
    }

    /**
     * This method sets wether or not this connection uses SSL.
     *
     * @param  bSsl  Whether or not this connection uses SSL.
     */
    public void setSSL(boolean bSsl)
    {
        m_ccConfiguration.setSSL(bSsl);
    }

    /**
     * This method sets the timeout to use.
     *
     * @param  lTimeout  The timeout to use.
     */
    public void setTimeout(long lTimeout)
    {
        m_ccConfiguration.setTimeout(lTimeout);
    }

    /**
     * This method sets the username to use.
     *
     * @param  sUsername  The username to use.
     */
    public void setUsername(String sUsername)
    {
        if (m_acAuthenticationDetails instanceof IUsernamePasswordAuthentication)
        {
            IUsernamePasswordAuthentication upa = (IUsernamePasswordAuthentication)
                                                      m_acAuthenticationDetails;
            upa.setUsername(sUsername);
        }
    }

    /**
     * This method sets the WCP session ID to use.
     *
     * @param  sWCPSessionID  The WCP session ID to use.
     *
     * @see    com.cordys.coe.util.cgc.ICordysGatewayClientBase#setWCPSessionID(java.lang.String)
     */
    public void setWCPSessionID(String sWCPSessionID)
    {
        if (ICordysCustomAuthentication.class.isAssignableFrom(m_acAuthenticationDetails.getClass()))
        {
            ICordysCustomAuthentication cca = (ICordysCustomAuthentication)
                                                  m_acAuthenticationDetails;
            cca.setWCPSessionID(sWCPSessionID);
        }
    }

    /**
     * This method is called when the HTTP response code is set to 500. All servers that are
     * basic-profile compliant will return error code 500 in case of a SOAP fault. The base
     * structure is:<br>
     * If the response was not valid XML this method will do nothing and expect the calling method
     * to throw an HTTPException.
     *
     * @param   sHTTPResponse  The response from the web server.
     * @param   sRequestXML    The request XML (used for filling the exception object with enough
     *                         information).
     *
     * @throws  CordysSOAPException  When a SOAP fault has occurred.
     */
    protected abstract void checkForAndThrowCordysSOAPException(String sHTTPResponse,
                                                                String sRequestXML)
                                                         throws CordysSOAPException;

    /**
     * Sends a login message to the gateway.
     *
     * @throws  CordysGatewayClientException  Thrown if the operation failed.
     */
    protected abstract void sendLoginMessage()
                                      throws CordysGatewayClientException;

    /**
     * Finalizer, clean up resources. Note : not sure if we need it for now
     *
     * @throws  Throwable  Thrown by the suport class.
     */
    @Override protected void finalize()
                               throws Throwable
    {
        if (m_hcHostConfiguration != null)
        {
            m_hcHostConfiguration = null;
        }

        if (m_hcClient != null)
        {
            m_hcClient = null;
        }

        super.finalize();
    }

    /**
     * Send a soap message to cordys.
     *
     * @param   sRequestXML           The soap request as string
     * @param   lTimeout              The timeout to use.
     * @param   bBlockIfServerIsDown  If this is true then the call will block indefinately untill
     *                                the server is back online.
     * @param   mExtraHeaders         Contains optional HTTP headers to be added to the request. Can
     *                                be <code>null</code>.
     * @param   sGatewayURL           The actual URL to which the request should be send.
     * @param   sOrganization         Organization to which the request is to be sent.
     * @param   sReceiver             The DN of the receiving SOAP processor.
     *
     * @return  The resulting PostMethod. NOTE: The callee is responsible for calling the
     *          PostMethod.releaseConnection() when it's finished.
     *
     * @throws  CordysGatewayClientException  Thrown if the request failed.
     */
    protected PostMethod requestFromCordys(String sRequestXML, long lTimeout,
                                           boolean bBlockIfServerIsDown,
                                           Map<String, String> mExtraHeaders, String sGatewayURL,
                                           String sOrganization, String sReceiver)
                                    throws CordysGatewayClientException
    {
        if (m_hcHostConfiguration == null)
        {
            throw new CordysGatewayClientException(CGCMessages.CGC_ERROR_NOT_CONNECTED);
        }

        // If this CGC is configured to use the serverwatcher we need to make sure that
        // the server is running before we attempt to send the request.
        if (m_ccConfiguration.useServerWatcher() && bBlockIfServerIsDown)
        {
            boolean bFirst = true;
            boolean bServerDown = false;

            while (!m_swWatcher.shouldServerFunction())
            {
                if (bFirst == true)
                {
                    if (getLogger().isInfoEnabled())
                    {
                        getLogger().info("Server watcher indicates that the server is not running. Waiting for the server to come back online.");
                    }
                    bFirst = false;
                }
                bServerDown = true;

                try
                {
                    Thread.sleep(m_ccConfiguration.getSleepTimeBetweenServerWacther());
                }
                catch (InterruptedException e)
                {
                    if (getLogger().isDebugEnabled())
                    {
                        getLogger().debug("Waiting for server loop interrupted.", e);
                    }
                }
            }

            if (bServerDown == true)
            {
                if (getLogger().isInfoEnabled())
                {
                    getLogger().info("The server was down but is up again. So continuing.");
                }
            }
        }

        // Now send the actual request.
        PostMethod pmReturn = new PostMethod();
        IPoolWorker oToken = null;

        try
        {
            oToken = m_oRequestTokens.getWorker();

            ByteArrayInputStream isSoapRequest = new ByteArrayInputStream(sRequestXML.getBytes());
            pmReturn.setRequestEntity(new InputStreamRequestEntity(isSoapRequest));

            // pmReturn.setRequestContentLength(aInputSoapRequest.length());
            String sActualURL = sGatewayURL;
            boolean bHasQuery = (sActualURL != null) && (sActualURL.indexOf('?') >= 0);

            if ((sOrganization != null) && (sOrganization.length() > 0))
            {
                sActualURL += (((!bHasQuery) ? "?" : "&") + "organization=" +
                               URLEncoder.encode(sOrganization, "UTF8"));
                bHasQuery = true;
            }

            if (lTimeout > 0)
            {
                sActualURL += (((!bHasQuery) ? "?" : "&") + "timeout=" +
                               URLEncoder.encode(String.valueOf(lTimeout), "UTF8"));
                bHasQuery = true;
            }

            if ((sReceiver != null) && (sReceiver.length() > 0))
            {
                sActualURL += (((!bHasQuery) ? "?" : "&") + "receiver=" +
                               URLEncoder.encode(sReceiver, "UTF8"));
                bHasQuery = true;
            }

            // Add the WCP session if needed
            if (ICordysCustomAuthentication.class.isAssignableFrom(m_acAuthenticationDetails
                                                                       .getClass()))
            {
                ICordysCustomAuthentication cca = (ICordysCustomAuthentication)
                                                      m_acAuthenticationDetails;

                if ((cca.getWCPSessionID() != null) && (cca.getWCPSessionID().length() > 0))
                {
                    sActualURL += (((!bHasQuery) ? "?" : "&") + "wcp-session=" +
                                   URLEncoder.encode(getWCPSessionID(), "UTF8"));
                    bHasQuery = true;
                }
            }

            pmReturn.setPath(sActualURL);

            if (getLogger().isDebugEnabled())
            {
                getLogger().debug("Posting to url " + sActualURL);
            }

            pmReturn.setRequestHeader(HTTP_CONTENTYPE_HEADER, SOAP_CONTENTYPE_UTF_8);

            if (mExtraHeaders != null)
            {
                for (Iterator<String> iIter = mExtraHeaders.keySet().iterator(); iIter.hasNext();)
                {
                    String sName = (String) iIter.next();
                    String sValue = (String) mExtraHeaders.get(sName);

                    if ((sName != null) && (sValue != null))
                    {
                        if (getLogger().isDebugEnabled())
                        {
                            getLogger().debug("Adding an HTTP header '" + sName + ": " + sValue +
                                              "'");
                        }

                        pmReturn.addRequestHeader(sName, sValue);
                    }
                }
            }

            HttpMethodParams hmpMethodParams = pmReturn.getParams();

            if (hmpMethodParams != null)
            {
                String sCredCharset = hmpMethodParams.getCredentialCharset();

                if ((sCredCharset == null) || (sCredCharset.length() == 0))
                {
                    hmpMethodParams.setCredentialCharset("UTF-8");
                }

                if (m_ccConfiguration.getNetworkTimeout() > 0)
                {
                    hmpMethodParams.setSoTimeout((int) m_ccConfiguration.getNetworkTimeout());
                }
            }

            try
            {
                long lStart = System.currentTimeMillis();
                int iResp = m_hcClient.executeMethod(m_hcHostConfiguration, pmReturn);
                long lEnd = System.currentTimeMillis();

                if (getLogger().isDebugEnabled())
                {
                    getLogger().debug("Request took " + (lEnd - lStart) + " miliseconds.");
                }

                if (getLogger().isDebugEnabled())
                {
                    String sResponse = pmReturn.getResponseBodyAsString();

                    if (!getLogger().isDebugEnabled())
                    {
                        sResponse = sResponse.replaceAll("\n", " ");
                    }

                    getLogger().debug("Response: " + sResponse);
                }

                // From C3 if a SOAP:Fault has occurred we will get a HTTP error code 500
                // so we need to check for that and make sure a proper CordysSOAPException.
                // The C3 Soap faults contain more information then the C2 exceptions.
                if (iResp != 200)
                {
                    if (iResp == 500)
                    {
                        checkForAndThrowCordysSOAPException(pmReturn.getResponseBodyAsString(),
                                                            sRequestXML);
                    }

                    throw new HttpException("Wrong responsecode: " + iResp + "\n" +
                                            pmReturn.getResponseBodyAsString());
                }
            }
            catch (IOException ioe)
            {
                throw new CordysGatewayClientException(ioe, CGCMessages.CGC_ERROR_HTTP_ERROR);
            }
        }
        catch (Exception e)
        {
            pmReturn.releaseConnection();

            if (e instanceof CordysGatewayClientException)
            {
                throw (CordysGatewayClientException) e;
            }
            throw new CordysGatewayClientException(e, CGCMessages.CGC_ERROR_SENDING_REQUEST);
        }
        finally
        {
            if (null != oToken)
            {
                m_oRequestTokens.putWorker(oToken);
            }
        }

        return pmReturn;
    }

    /**
     * This method sets the user info for this object.
     *
     * @param  uiUserInfo  The user info for this object.
     */
    protected void setUserInfo(IUserInfo uiUserInfo)
    {
        m_uiUserInfo = uiUserInfo;
    }
}
