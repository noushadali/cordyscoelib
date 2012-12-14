package com.cordys.coe.util.cgc;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.Consts;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.cordys.coe.util.IPoolWorker;
import com.cordys.coe.util.StringUtils;
import com.cordys.coe.util.TokenPool;
import com.cordys.coe.util.cgc.config.CGCConfigFactory;
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

/**
 * This class can be used to communicate with the Cordys Web Gateway. It supports 3 types of authentication: - Basic - NTLM -
 * Certificates. This class is thread safe. This means that multiple threads can use the same instance of this object to call
 * methods on the Cordys server. If you need to connect under multiple users you need to make an instance per user.<br>
 * Example code for NTLM: <code>String sUser = "pgussow"; String sPassword = "password"; String
 * sServer = "srv-nl-ces20"; String sDomain = "NTDOM"; int iPort = 80; ICordysGatewayClient cgc =
 * new CordysGatewayClient(sUser, sPassword, sServer, iPort, sDomain); cgc.connect();</code>
 * 
 * @author pgussow
 */
public abstract class CordysGatewayClientBase implements ICordysGatewayClientBase
{
    /**
     * Holds the logger to use for this class.
     */
    private static final Logger LOG = Logger.getLogger(CordysGatewayClientBase.class);
    /**
     * This holds the base template for calling a Cordy soap method.
     */
    protected static final byte[] BASE_SOAP_REQUEST = ("<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\">"
            + "<SOAP:Body/></SOAP:Envelope>").getBytes();
    /**
     * This holds the GetUserDetails request.
     */
    protected static final byte[] XML_GET_USER_DETAILS = ("<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\">"
            + "<SOAP:Body>" + "<GetUserDetails xmlns=\"http://schemas.cordys.com/1.1/ldap\"/>" + "</SOAP:Body></SOAP:Envelope>")
            .getBytes();
    /**
     * This holds the logon request.
     */
    protected static final byte[] XML_AUTHENTICATE = ("<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\">"
            + "<SOAP:Body>" + "<Authenticate xmlns=\"http://schemas.cordys.com/1.0/webgateway\"/>"
            + "</SOAP:Body></SOAP:Envelope>").getBytes();
    /**
     * This holds the request to get a SAML token.
     */
    protected static final byte[] XML_SSO_LOGIN = ("<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\">"
            + "<SOAP:Header><wsse:Security xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">"
            + "<wsse:UsernameToken xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">"
            + "<wsse:Username></wsse:Username>"
            + "<wsse:Password></wsse:Password>"
            + "</wsse:UsernameToken>"
            + "</wsse:Security></SOAP:Header>"
            + "<SOAP:Body>"
            + "<samlp:Request xmlns:samlp=\"urn:oasis:names:tc:SAML:1.0:protocol\" MajorVersion=\"1\" MinorVersion=\"1\" IssueInstant=\"\" RequestID=\"\"><samlp:AuthenticationQuery>"
            + "<saml:Subject xmlns:saml=\"urn:oasis:names:tc:SAML:1.0:assertion\">"
            + "<saml:NameIdentifier Format=\"urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified\"></saml:NameIdentifier>"
            + "</saml:Subject></samlp:AuthenticationQuery></samlp:Request>" + "</SOAP:Body></SOAP:Envelope>").getBytes();
    /**
     * This holds the request for executing LDAP searches.
     */
    protected static final byte[] XML_SEARCH_LDAP = ("<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP:Body>"
            + "<SearchLDAP xmlns=\"http://schemas.cordys.com/1.0/ldap\">"
            + "<dn/><scope/><filter/><sort>ascending</sort><returnValues>true</returnValues>"
            + "</SearchLDAP></SOAP:Body></SOAP:Envelope>").getBytes();
    /**
     * This holds the request for reading a single LDAP entry.
     */
    protected static final byte[] XML_GET_LDAP_OBJECT = ("<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP:Body>"
            + "<GetLDAPObject xmlns=\"http://schemas.cordys.com/1.0/ldap\">"
            + "<dn/>"
            + "</GetLDAPObject></SOAP:Body></SOAP:Envelope>").getBytes();
    /**
     * This holds the request for updating a single LDAP entry.
     */
    protected static final byte[] XML_UPDATE_LDAP = ("<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP:Body>"
            + "<Update xmlns=\"http://schemas.cordys.com/1.0/ldap\">" + "<tuple/>" + "</Update></SOAP:Body></SOAP:Envelope>")
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
     * Indicates whether or not the gateway is connected.
     */
    protected boolean m_bConnected = false;
    /**
     * Holds the HTTP client.
     */
    protected DefaultHttpClient m_hcClient;
    /**
     * Holds the tokens for calls to the backend.
     */
    protected TokenPool m_oRequestTokens = null;
    /**
     * Holds the organization.
     */
    protected String m_sOrganization = null;
    /**
     * Holds the server watcher. This object is responsible for monitoring the server to make sure it is still running. It depends
     * on how this class is created whether or not is is being used.
     */
    protected ServerWatcher m_swWatcher = null;
    /**
     * This class contains the authentication details for the current client.
     */
    private IAuthenticationConfiguration m_acAuthenticationDetails;
    /**
     * Holds the configuration for this Cordys Gateway Client.
     */
    private ICGCConfiguration m_ccConfiguration;
    /**
     * Holds the user information.
     */
    private IUserInfo m_uiUserInfo;
    /** Holds the scheme that shouldbe used for this connection. */
    private SchemeRegistry m_schemeRegistry;
    /** Holds the proxy server to use for the connection. */
    private HttpHost m_proxy;

    /**
     * Constructor.
     * 
     * @param acAuthentication The authentication details for connecting to the web gateway.
     * @param ccConfiguration The CGC configuration.
     * @throws CordysGatewayClientException In case of any configuration exceptions.
     */
    protected CordysGatewayClientBase(IAuthenticationConfiguration acAuthentication, ICGCConfiguration ccConfiguration)
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
        if ((ccConfiguration.isSSL() == false)
                && IClientCertificateAuthentication.class.isAssignableFrom(acAuthentication.getClass()))
        {
            throw new CordysGatewayClientException(CGCMessages.CGC_ERROR_MUST_BE_SSL);
        }

        // Create the worker pool.
        m_oRequestTokens = new TokenPool(ccConfiguration.getMaxConcurrentCalls());

        m_schemeRegistry = new SchemeRegistry();

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

            AuthSSLProtocolSocketFactory psfFactory = AuthSSLProtocolSocketFactory
                    .getInstance(cgcSSLConfig, ccaClientCertificate);

            Scheme https = new Scheme("https", ccConfiguration.getPort(), psfFactory);
            m_schemeRegistry.register(https);
        }
        else
        {
            // Standard HTTP traffic.
            Scheme http = new Scheme("http", 80, PlainSocketFactory.getSocketFactory());
            m_schemeRegistry.register(http);
        }

        // If a proxy server is defined configure it.
        if (ccConfiguration.isProxyServerSet())
        {
            m_proxy = new HttpHost(ccConfiguration.getProxyHost(), ccConfiguration.getProxyPort());
        }

        // Create the server watcher if needed.
        if (ccConfiguration.useServerWatcher() == true)
        {
            if (getLogger().isDebugEnabled())
            {
                getLogger().debug(
                        "Going to create the server watcher for " + ccConfiguration.getHost() + ":" + ccConfiguration.getPort()
                                + " with an interval of " + ccConfiguration.getServerWatcherPollInterval() + "ms.");
            }

            m_swWatcher = new ServerWatcher(ccConfiguration.getHost(), ccConfiguration.getPort(),
                    ccConfiguration.getServerWatcherPollInterval());
            m_swWatcher.start();
        }
    }

    /**
     * This method adds the passed on service to be watched each interval of this watcher thread.
     * 
     * @param swssService the soap service to watch.
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
     * @throws CordysGatewayClientException Exception something is wrong TODO: bedenk wat mooie excepties
     */
    public void connect() throws CordysGatewayClientException
    {
        if (m_hcClient != null)
        {
            this.disconnect();
        }

        try
        {
            // Create the connection pooling manager to allow only x connections per host.
            PoolingClientConnectionManager pccm = new PoolingClientConnectionManager();
            HttpRoute hr = new HttpRoute(new HttpHost(m_ccConfiguration.getHost(), m_ccConfiguration.getPort()));
            pccm.setMaxPerRoute(hr, m_ccConfiguration.getMaxConnectionsPerHost());

            // Create the actual HTTP client.
            m_hcClient = new DefaultHttpClient(pccm);

            // Set the configured timeout
            if (m_ccConfiguration.getNetworkTimeout() > 0)
            {
                HttpConnectionParams.setConnectionTimeout(m_hcClient.getParams(), (int) m_ccConfiguration.getNetworkTimeout());
            }

            // Add the proxy server if configured
            if (m_proxy != null)
            {
                m_hcClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, m_proxy);

                // If the proxy requires authentication
                if (StringUtils.isSet(m_ccConfiguration.getProxyUsername()))
                {
                    UsernamePasswordCredentials upc = new UsernamePasswordCredentials(m_ccConfiguration.getProxyUsername(),
                            m_ccConfiguration.getProxyPassword());
                    m_hcClient.getCredentialsProvider().setCredentials(
                            new AuthScope(m_ccConfiguration.getProxyHost(), m_ccConfiguration.getPort()), upc);
                }
            }

            List<String> authPrefs = new ArrayList<String>(3);

            authPrefs.add(AuthPolicy.BASIC);
            authPrefs.add(AuthPolicy.DIGEST);
            authPrefs.add(AuthPolicy.NTLM);

            m_hcClient.getParams().setParameter(AuthPNames.TARGET_AUTH_PREF, authPrefs);

            // Set the character set to use for the credentials.
            m_hcClient.getParams().setParameter(AuthPNames.CREDENTIAL_CHARSET, "UTF-8");

            // Cookies (saml artifact cookies) need not be set when SAML assertion is included in
            // the SOAP Header. For all the requests which use this class if it is SSO login, then SAML
            // assertion will be included in the SOAP Header. So the following line will make sure that
            // it does not use cookies. This is required because with a hotfix delivered by Cordys - which
            // will be included in BOP 4.2, if the cookies are sent along with the SAML assertion, the
            // soap request fails.
            m_hcClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.IGNORE_COOKIES);

            // Create the proper credentials.
            if (INTLMAuthentication.class.isAssignableFrom(m_acAuthenticationDetails.getClass()))
            {
                INTLMAuthentication naNTLM = (INTLMAuthentication) m_acAuthenticationDetails;

                NTCredentials ncCredentials = new NTCredentials(naNTLM.getUsername(), naNTLM.getPassword(),
                        m_ccConfiguration.getHost(), naNTLM.getDomain());

                m_hcClient.getCredentialsProvider().setCredentials(AuthScope.ANY, ncCredentials);
            }
            else if (ICordysCustomAuthentication.class.isAssignableFrom(m_acAuthenticationDetails.getClass()))
            {
                // Nothing needs to be done, because the authentication will be done differently.
            }
            else if (IUsernamePasswordAuthentication.class.isAssignableFrom(m_acAuthenticationDetails.getClass()))
            {
                IUsernamePasswordAuthentication upa = (IUsernamePasswordAuthentication) m_acAuthenticationDetails;

                UsernamePasswordCredentials upcCredentials = new UsernamePasswordCredentials(upa.getUsername(), upa.getPassword());
                m_hcClient.getCredentialsProvider().setCredentials(AuthScope.ANY, upcCredentials);
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
            throw new CordysGatewayClientException(ex, CGCMessages.CGC_CONNECT, getHost(), getPort());
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
     * @return The authentication details.
     */
    public IAuthenticationConfiguration getAuthenticationDetails()
    {
        return m_acAuthenticationDetails;
    }

    /**
     * This method gets the configuration details.
     * 
     * @return The configuration details.
     */
    public ICGCConfiguration getConfiguration()
    {
        return m_ccConfiguration;
    }

    /**
     * This method gets the NT domain to use.
     * 
     * @return The NT domain to use.
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
     * @return The url of the Cordys gateway.
     */
    public String getGatewayURL()
    {
        return m_ccConfiguration.getGatewayURL();
    }

    /**
     * This method gets the full URL for the gateway.
     * 
     * @return The full URL for the gateway.
     */
    public URL getRealGatewayURL()
    {
        URL retVal = null;

        String protocol = "http";
        if (m_ccConfiguration.isSSL())
        {
            protocol += "s";
        }

        try
        {
            retVal = new URL(protocol, m_ccConfiguration.getHost(), m_ccConfiguration.getPort(), getGatewayURL());
        }
        catch (Exception e)
        {
            // Should never happen, since we've already used the URL.
        }

        return retVal;
    }

    /**
     * This method gets the hostname of the cordys gateway.
     * 
     * @return The hostname of the cordys gateway.
     */
    public String getHost()
    {
        return m_ccConfiguration.getHost();
    }

    /**
     * This method gets the Logger for this class.
     * 
     * @return The Logger for this class.
     */
    public Logger getLogger()
    {
        return LOG;
    }

    /**
     * Returns the flag indicating if a login request is sent when the connection is opened.
     * 
     * @return If <code>true</code>, a login request is sent.
     */
    public boolean getLoginToCordysOnConnect()
    {
        return m_ccConfiguration.getLoginToCordysOnConnect();
    }

    /**
     * Get a list of organisations for this user.
     * 
     * @return Returns the logonInfo - organisations as an array of strings.
     * @deprecated This method has never been implemented and will be removed. Use getuserInfo instead.
     */
    public ArrayList<String> getLogonInfoOrganisations()
    {
        throw new RuntimeException("Will be removed");
    }

    /**
     * This method returns the list of roles this user has in a certain organization.
     * 
     * @param aOrganisation The organization.
     * @return Returns the logonInfo - roles.
     * @deprecated This method has never been implemented and will be removed. Use getuserInfo instead.
     */
    public ArrayList<String> getLogonInfoRoles(String aOrganisation)
    {
        throw new RuntimeException("Will be removed");
    }

    /**
     * Returns the network TCP/IP timeout to be used for requests.
     * 
     * @return Network timeout.
     */
    public long getNetworkTimeout()
    {
        return m_ccConfiguration.getNetworkTimeout();
    }

    /**
     * This method gets the password to use..
     * 
     * @return The password to use..
     */
    public String getPassword()
    {
        String sReturn = null;

        if (IUsernamePasswordAuthentication.class.isAssignableFrom(m_acAuthenticationDetails.getClass()))
        {
            IUsernamePasswordAuthentication na = (IUsernamePasswordAuthentication) m_acAuthenticationDetails;
            sReturn = na.getPassword();
        }

        return sReturn;
    }

    /**
     * This method gets the port where the Cordys web gateway is running.
     * 
     * @return The port where the Cordys web gateway is running.
     */
    public int getPort()
    {
        return m_ccConfiguration.getPort();
    }

    /**
     * This method gets the proxy host to use.
     * 
     * @return The proxy host to use.
     */
    public String getProxyHost()
    {
        return m_ccConfiguration.getProxyHost();
    }

    /**
     * This method gets the proxy port to use.
     * 
     * @return The proxy port to use.
     */
    public int getProxyPort()
    {
        return m_ccConfiguration.getProxyPort();
    }

    /**
     * This method gets the timeout to use.
     * 
     * @return The timeout to use.
     */
    public long getTimeout()
    {
        return m_ccConfiguration.getTimeout();
    }

    /**
     * This method gets the username to use.
     * 
     * @return The username to use.
     */
    public String getUsername()
    {
        String sReturn = null;

        if (IUsernamePasswordAuthentication.class.isAssignableFrom(m_acAuthenticationDetails.getClass()))
        {
            IUsernamePasswordAuthentication na = (IUsernamePasswordAuthentication) m_acAuthenticationDetails;
            sReturn = na.getUsername();
        }

        return sReturn;
    }

    /**
     * @see com.cordys.coe.util.cgc.ICordysGatewayClientBase#getUserInfo()
     */
    public IUserInfo getUserInfo()
    {
        if (m_uiUserInfo == null)
        {
            try
            {
                m_uiUserInfo = parseUserInfo();
            }
            catch (CordysGatewayClientException e)
            {
                LOG.error("Error parsing user information", e);
            }
        }

        return m_uiUserInfo;
    }

    /**
     * This method needs to return the user info based on the login response.
     * 
     * @return The parsed variant of the user info.
     */
    protected abstract IUserInfo parseUserInfo() throws CordysGatewayClientException;

    /**
     * This method gets the WCP session ID to use.
     * 
     * @return The WCP session ID to use.
     * @see com.cordys.coe.util.cgc.ICordysGatewayClientBase#getWCPSessionID()
     */
    public String getWCPSessionID()
    {
        String sReturn = null;

        if (ICordysCustomAuthentication.class.isAssignableFrom(m_acAuthenticationDetails.getClass()))
        {
            ICordysCustomAuthentication cca = (ICordysCustomAuthentication) m_acAuthenticationDetails;
            sReturn = cca.getWCPSessionID();
        }

        return sReturn;
    }

    /**
     * Check if the logged on user has a certain cordys role.
     * 
     * @param sRoleDN The role to check for
     * @return true if the role is assigned to the logged on user
     * @throws CordysGatewayClientException In case the user information is not available.
     */
    public boolean hasRole(String sRoleDN) throws CordysGatewayClientException
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
     * @return Whether or not the gateway checks the responses for soap faults.
     */
    public boolean isCheckingForFaults()
    {
        return m_ccConfiguration.isCheckingForFaults();
    }

    /**
     * Test if we have an open connection to cordys.
     * 
     * @return true if connected
     */
    public boolean isConnected()
    {
        return m_bConnected;
    }

    /**
     * This method gets whether or not this connection uses SSL.
     * 
     * @return Whether or not this connection uses SSL.
     */
    public boolean isSSL()
    {
        return m_ccConfiguration.isSSL();
    }

    /**
     * This method sets wether or not the gateway checks the responses for soap faults.
     * 
     * @param bCheckForFaults Whether or not the gateway checks the responses for soap faults.
     */
    public void setCheckForFaults(boolean bCheckForFaults)
    {
        m_ccConfiguration.setCheckForFaults(bCheckForFaults);
    }

    /**
     * This method sets the NT domain to use.
     * 
     * @param sNTDomain The NT domain to use.
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
     * @param sGatewayURL The url of the Cordys gateway.
     */
    public void setGatewayURL(String sGatewayURL)
    {
        m_ccConfiguration.setGatewayURL(sGatewayURL);
    }

    /**
     * This method sets the hostname of the cordys gateway.
     * 
     * @param sHost The hostname of the cordys gateway.
     */
    public void setHost(String sHost)
    {
        m_ccConfiguration.setHost(sHost);
    }

    /**
     * Sets the flag indicating if a login request is sent when the connection is opened.
     * 
     * @param bValue If <code>true</code>, a login request is sent.
     */
    public void setLoginToCordysOnConnect(boolean bValue)
    {
        m_ccConfiguration.setLoginToCordysOnConnect(bValue);
    }

    /**
     * Sets the network TCP/IP timeout to be used for requests. This is separate from the Cordys timeout URL parameter.
     * 
     * @param lValue Network timeout value (-1 means infinite wait).
     */
    public void setNetworkTimeout(long lValue)
    {
        m_ccConfiguration.setNetworkTimeout(lValue);
    }

    /**
     * This method sets the organization to use for this gateway.
     * 
     * @param sOrganization The new organization.
     */
    public void setOrganization(String sOrganization)
    {
        this.m_sOrganization = sOrganization;
    }

    /**
     * This method sets the password to use..
     * 
     * @param sPassword The password to use..
     */
    public void setPassword(String sPassword)
    {
        if (m_acAuthenticationDetails instanceof IUsernamePasswordAuthentication)
        {
            IUsernamePasswordAuthentication upa = (IUsernamePasswordAuthentication) m_acAuthenticationDetails;
            upa.setPassword(sPassword);
        }
    }

    /**
     * This method sets the port where the Cordys web gateway is running.
     * 
     * @param iPort The port where the Cordys web gateway is running.
     */
    public void setPort(int iPort)
    {
        m_ccConfiguration.setPort(iPort);
    }

    /**
     * This method sets the proxy host to use.
     * 
     * @param sProxyHost The proxy host to use.
     */
    public void setProxyHost(String sProxyHost)
    {
        m_ccConfiguration.setProxyHost(sProxyHost);
    }

    /**
     * This method sets the proxy port to use.
     * 
     * @param iProxyPort The proxy port to use.
     */
    public void setProxyPort(int iProxyPort)
    {
        m_ccConfiguration.setProxyPort(iProxyPort);
    }

    /**
     * This method sets the interval in which the server watcher will check if the webserver is still available. If this CGC is
     * created without a server watcher this call has no effect.
     * 
     * @param lServerWatcherPollInterval The new poll interval.
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
     * @param lSleepTime The new sleep time.
     */
    public void setSleepTimeBetweenServerWacther(long lSleepTime)
    {
        m_ccConfiguration.setSleepTimeBetweenServerWacther(lSleepTime);
    }

    /**
     * This method sets wether or not this connection uses SSL.
     * 
     * @param bSsl Whether or not this connection uses SSL.
     */
    public void setSSL(boolean bSsl)
    {
        m_ccConfiguration.setSSL(bSsl);
    }

    /**
     * This method sets the timeout to use.
     * 
     * @param lTimeout The timeout to use.
     */
    public void setTimeout(long lTimeout)
    {
        m_ccConfiguration.setTimeout(lTimeout);
    }

    /**
     * This method sets the username to use.
     * 
     * @param sUsername The username to use.
     */
    public void setUsername(String sUsername)
    {
        if (m_acAuthenticationDetails instanceof IUsernamePasswordAuthentication)
        {
            IUsernamePasswordAuthentication upa = (IUsernamePasswordAuthentication) m_acAuthenticationDetails;
            upa.setUsername(sUsername);
        }
    }

    /**
     * This method sets the WCP session ID to use.
     * 
     * @param sWCPSessionID The WCP session ID to use.
     * @see com.cordys.coe.util.cgc.ICordysGatewayClientBase#setWCPSessionID(java.lang.String)
     */
    public void setWCPSessionID(String sWCPSessionID)
    {
        if (ICordysCustomAuthentication.class.isAssignableFrom(m_acAuthenticationDetails.getClass()))
        {
            ICordysCustomAuthentication cca = (ICordysCustomAuthentication) m_acAuthenticationDetails;
            cca.setWCPSessionID(sWCPSessionID);
        }
    }

    /**
     * This method is called when the HTTP response code is set to 500. All servers that are basic-profile compliant will return
     * error code 500 in case of a SOAP fault. The base structure is:<br>
     * If the response was not valid XML this method will do nothing and expect the calling method to throw an HTTPException.
     * 
     * @param sHTTPResponse The response from the web server.
     * @param sRequestXML The request XML (used for filling the exception object with enough information).
     * @throws CordysSOAPException When a SOAP fault has occurred.
     */
    protected abstract void checkForAndThrowCordysSOAPException(String sHTTPResponse, String sRequestXML)
            throws CordysSOAPException;

    /**
     * Sends a login message to the gateway.
     * 
     * @throws CordysGatewayClientException Thrown if the operation failed.
     */
    protected abstract void sendLoginMessage() throws CordysGatewayClientException;

    /**
     * Finalizer, clean up resources. Note : not sure if we need it for now
     * 
     * @throws Throwable Thrown by the suport class.
     */
    @Override
    protected void finalize() throws Throwable
    {
        if (m_hcClient != null)
        {
            m_hcClient = null;
        }

        super.finalize();
    }

    /**
     * Send a soap message to cordys.
     * 
     * @param sRequestXML The soap request as string
     * @param lTimeout The timeout to use.
     * @param bBlockIfServerIsDown If this is true then the call will block indefinately untill the server is back online.
     * @param mExtraHeaders Contains optional HTTP headers to be added to the request. Can be <code>null</code>.
     * @param sGatewayURL The actual URL to which the request should be send.
     * @param sOrganization Organization to which the request is to be sent.
     * @param sReceiver The DN of the receiving SOAP processor.
     * @return The resulting HttpPost. NOTE: The callee is responsible for calling the PostMethod.releaseConnection() when it's
     *         finished.
     * @throws CordysGatewayClientException Thrown if the request failed.
     */
    protected String requestFromCordys(String sRequestXML, long lTimeout, boolean bBlockIfServerIsDown,
            Map<String, String> mExtraHeaders, String sGatewayURL, String sOrganization, String sReceiver)
            throws CordysGatewayClientException
    {
        String retVal = null;

        if (m_hcClient == null)
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
                        getLogger()
                                .info("Server watcher indicates that the server is not running. Waiting for the server to come back online.");
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
        IPoolWorker oToken = null;

        try
        {
            oToken = m_oRequestTokens.getWorker();

            // pmReturn.setRequestContentLength(aInputSoapRequest.length());
            String sActualURL = sGatewayURL;
            boolean bHasQuery = (sActualURL != null) && (sActualURL.indexOf('?') >= 0);

            if ((sOrganization != null) && (sOrganization.length() > 0))
            {
                sActualURL += (((!bHasQuery) ? "?" : "&") + "organization=" + URLEncoder.encode(sOrganization, "UTF8"));
                bHasQuery = true;
            }

            if (lTimeout > 0)
            {
                sActualURL += (((!bHasQuery) ? "?" : "&") + "timeout=" + URLEncoder.encode(String.valueOf(lTimeout), "UTF8"));
                bHasQuery = true;
            }

            if ((sReceiver != null) && (sReceiver.length() > 0))
            {
                sActualURL += (((!bHasQuery) ? "?" : "&") + "receiver=" + URLEncoder.encode(sReceiver, "UTF8"));
                bHasQuery = true;
            }

            // Add the WCP session if needed
            if (ICordysCustomAuthentication.class.isAssignableFrom(m_acAuthenticationDetails.getClass()))
            {
                ICordysCustomAuthentication cca = (ICordysCustomAuthentication) m_acAuthenticationDetails;

                if ((cca.getWCPSessionID() != null) && (cca.getWCPSessionID().length() > 0))
                {
                    sActualURL += (((!bHasQuery) ? "?" : "&") + "wcp-session=" + URLEncoder.encode(getWCPSessionID(), "UTF8"));
                    bHasQuery = true;
                }
            }

            // Create the Post method
            HttpPost pmReturn = new HttpPost(m_schemeRegistry.getSchemeNames().get(0) + "://" + m_ccConfiguration.getHost() + ":"
                    + m_ccConfiguration.getPort() + sActualURL);

            // Set the payload.
            StringEntity entity = new StringEntity(sRequestXML, ContentType.create("text/xml", Consts.UTF_8));
            pmReturn.setEntity(entity);

            if (getLogger().isDebugEnabled())
            {
                getLogger().debug("Posting to url " + sActualURL);
            }

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
                            getLogger().debug("Adding an HTTP header '" + sName + ": " + sValue + "'");
                        }

                        pmReturn.addHeader(sName, sValue);
                    }
                }
            }

            HttpResponse response = null;
            try
            {
                long lStart = System.currentTimeMillis();
                response = m_hcClient.execute(pmReturn);

                long lEnd = System.currentTimeMillis();

                if (getLogger().isDebugEnabled())
                {
                    getLogger().debug("Request took " + (lEnd - lStart) + " miliseconds.");
                }

                // Read the response data. This can be done only once.
                retVal = EntityUtils.toString(response.getEntity());

                if (getLogger().isDebugEnabled())
                {
                    getLogger().debug("Response: " + retVal.replace("\n", " "));
                }

                // From C3 if a SOAP:Fault has occurred we will get a HTTP error code 500
                // so we need to check for that and make sure a proper CordysSOAPException.
                // The C3 Soap faults contain more information then the C2 exceptions.
                if (response.getStatusLine().getStatusCode() != 200)
                {
                    if (response.getStatusLine().getStatusCode() == 500)
                    {
                        checkForAndThrowCordysSOAPException(retVal, sRequestXML);
                    }

                    throw new HttpException("Wrong responsecode: " + response.getStatusLine().getStatusCode() + "\n" + retVal);
                }
            }
            catch (IOException ioe)
            {
                throw new CordysGatewayClientException(ioe, CGCMessages.CGC_ERROR_HTTP_ERROR);
            }
            finally
            {
                if (response != null && response.getEntity() != null)
                {
                    EntityUtils.consumeQuietly(response.getEntity());
                }
            }
        }
        catch (Exception e)
        {
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

        return retVal;
    }

    /**
     * This method sets the user info for this object.
     * 
     * @param uiUserInfo The user info for this object.
     */
    protected void setUserInfo(IUserInfo uiUserInfo)
    {
        m_uiUserInfo = uiUserInfo;
    }

    /**
     * @see com.cordys.coe.util.cgc.ICordysGatewayClientBase#uploadFile(java.lang.String, java.io.File)
     */
    @Override
    public String uploadFile(String request, File file, String organization, long timeout, String receiver,
            boolean blockIfServerIsDown) throws CordysGatewayClientException
    {
        String retVal = null;

        if (m_hcClient == null)
        {
            throw new CordysGatewayClientException(CGCMessages.CGC_ERROR_NOT_CONNECTED);
        }

        // If this CGC is configured to use the serverwatcher we need to make sure that
        // the server is running before we attempt to send the request.
        if (m_ccConfiguration.useServerWatcher() && blockIfServerIsDown)
        {
            boolean bFirst = true;
            boolean bServerDown = false;

            while (!m_swWatcher.shouldServerFunction())
            {
                if (bFirst == true)
                {
                    if (getLogger().isInfoEnabled())
                    {
                        getLogger()
                                .info("Server watcher indicates that the server is not running. Waiting for the server to come back online.");
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
        IPoolWorker oToken = null;

        try
        {
            oToken = m_oRequestTokens.getWorker();

            // pmReturn.setRequestContentLength(aInputSoapRequest.length());
            String sActualURL = getGatewayURL();
            boolean bHasQuery = (sActualURL != null) && (sActualURL.indexOf('?') >= 0);

            // The details are wrapped in a multipart/from-data
            MultipartEntity mpe = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            FileBody fb = new FileBody(file, "application/octet-stream");
            mpe.addPart("file1", fb);
            mpe.addPart("methodXML", new StringBody(request, Consts.UTF_8));
            mpe.addPart("encode", new StringBody("true", Consts.UTF_8));
            mpe.addPart("xmlcontent", new StringBody("true", Consts.UTF_8));
            if (StringUtils.isSet(organization))
            {
                mpe.addPart("organizationalcontext", new StringBody(organization, Consts.UTF_8));
            }
            if (timeout > 0)
            {
                mpe.addPart("timeout", new StringBody(String.valueOf(timeout), Consts.UTF_8));
            }
            if (StringUtils.isSet(receiver))
            {
                mpe.addPart("receiver", new StringBody(receiver, Consts.UTF_8));
            }
            mpe.addPart("contentType", new StringBody("application/octet-stream", Consts.UTF_8));
            mpe.addPart("resultHtml", new StringBody("", Consts.UTF_8));

            // Add the WCP session if needed
            if (ICordysCustomAuthentication.class.isAssignableFrom(m_acAuthenticationDetails.getClass()))
            {
                ICordysCustomAuthentication cca = (ICordysCustomAuthentication) m_acAuthenticationDetails;

                if ((cca.getWCPSessionID() != null) && (cca.getWCPSessionID().length() > 0))
                {
                    sActualURL += (((!bHasQuery) ? "?" : "&") + "wcp-session=" + URLEncoder.encode(getWCPSessionID(), "UTF8"));
                    bHasQuery = true;
                }
            }

            // Create the Post method
            sActualURL = sActualURL.replace(CGCConfigFactory.GATEWAY_URL_CLASS, "com.eibus.web.tools.upload.Upload.wcp");
            HttpPost pmReturn = new HttpPost(m_schemeRegistry.getSchemeNames().get(0) + "://" + m_ccConfiguration.getHost() + ":"
                    + m_ccConfiguration.getPort() + sActualURL);

            // Set the payload.
            pmReturn.setEntity(mpe);

            if (getLogger().isDebugEnabled())
            {
                getLogger().debug("Posting to url " + sActualURL);
            }

            HttpResponse response = null;
            try
            {
                long lStart = System.currentTimeMillis();
                response = m_hcClient.execute(pmReturn);

                long lEnd = System.currentTimeMillis();

                if (getLogger().isDebugEnabled())
                {
                    getLogger().debug("Request took " + (lEnd - lStart) + " miliseconds.");
                }

                // Read the response data. This can be done only once.
                retVal = EntityUtils.toString(response.getEntity());

                if (getLogger().isDebugEnabled())
                {
                    getLogger().debug("Response: " + retVal.replace("\n", " "));
                }

                // From C3 if a SOAP:Fault has occurred we will get a HTTP error code 500
                // so we need to check for that and make sure a proper CordysSOAPException.
                // The C3 Soap faults contain more information then the C2 exceptions.
                if (response.getStatusLine().getStatusCode() != 200)
                {
                    if (response.getStatusLine().getStatusCode() == 500)
                    {
                        checkForAndThrowCordysSOAPException(retVal, request);
                    }

                    throw new HttpException("Wrong responsecode: " + response.getStatusLine().getStatusCode() + "\n" + retVal);
                }
            }
            catch (IOException ioe)
            {
                throw new CordysGatewayClientException(ioe, CGCMessages.CGC_ERROR_HTTP_ERROR);
            }
            finally
            {
                if (response != null && response.getEntity() != null)
                {
                    EntityUtils.consumeQuietly(response.getEntity());
                }
            }
        }
        catch (Exception e)
        {
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

        return retVal;
    }
}
