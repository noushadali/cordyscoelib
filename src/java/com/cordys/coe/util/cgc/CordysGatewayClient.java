package com.cordys.coe.util.cgc;

import com.cordys.coe.util.cgc.config.IAuthenticationConfiguration;
import com.cordys.coe.util.cgc.config.ICGCConfiguration;
import com.cordys.coe.util.cgc.config.ICordysCustomAuthentication;
import com.cordys.coe.util.cgc.config.ISSOAuthentication;
import com.cordys.coe.util.cgc.message.CGCMessages;
import com.cordys.coe.util.cgc.userinfo.UserInfoFactory;
import com.cordys.coe.util.exceptions.XMLWrapperException;
import com.cordys.coe.util.xml.NamespaceDefinitions;
import com.cordys.coe.util.xml.dom.NamespaceConstants;
import com.cordys.coe.util.xml.dom.NiceDOMWriter;
import com.cordys.coe.util.xml.dom.PrefixResolver;
import com.cordys.coe.util.xml.dom.XMLHelper;
import com.cordys.coe.util.xml.dom.XPathHelper;

import com.novell.ldap.LDAPEntry;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.TransformerException;

import org.apache.commons.httpclient.methods.PostMethod;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
class CordysGatewayClient extends CordysGatewayClientBase
    implements ICordysGatewayClient
{
    /**
     * The namespace for the web gateway.
     */
    private static final String XMLNS_WEBGATEWAY_1_0 = "http://schemas.cordys.com/1.0/webgateway";
    /**
     * Holds the logger to use for this class.
     */
    private static final Logger LOG = Logger.getLogger(CordysGatewayClient.class);
    /**
     * Holds the namespace prefix for the SOAP namespace.
     */
    private static final String PRE_SOAP = NamespaceDefinitions.PREFIX_SOAP_1_1;
    /**
     * Holds the namespace prefix for the LDAP 1.1 namespace.
     */
    private static final String PRE_LDAP = NamespaceConstants.registerPrefix("ldap11",
                                                                             "http://schemas.cordys.com/1.1/ldap");
    /**
     * Holds the namespace prefix for the LDAP 1.0 namespace.
     */
    private static final String PRE_LDAP_10 = NamespaceConstants.registerPrefix("ldap10",
                                                                                "http://schemas.cordys.com/1.0/ldap");
    /**
     * Holds the namespace prefix for the webgateway namespace.
     */
    private static final String PRE_AUTH = NamespaceConstants.registerPrefix("auth",
                                                                             XMLNS_WEBGATEWAY_1_0);
    /**
     * Holds the namespace prefix for the WS-Security namespace.
     */
    private static final String PRE_WSSE = NamespaceConstants.registerPrefix("wsse",
                                                                             "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
    /**
     * Holds the namespace prefix for the SAML protocol namespace.
     */
    private static final String PRE_SAMLP = NamespaceConstants.registerPrefix("samlp",
                                                                              "urn:oasis:names:tc:SAML:1.0:protocol");
    /**
     * Holds the namespace prefix for the SAML protocol namespace.
     */
    private static final String PRE_SAML = NamespaceConstants.registerPrefix("saml",
                                                                             "urn:oasis:names:tc:SAML:1.0:assertion");
    /**
     * Holds the namespace prefix for the XML Signature namespace.
     */
    private static final String PRE_XMLSIG = NamespaceConstants.registerPrefix("xmlsig",
                                                                               "http://www.w3.org/2000/09/xmldsig#");
    /**
     * If <code>true</code> response documents are namespace-aware.
     */
    protected boolean m_bNamespaceAwareResponses;
    /**
     * Holds the user details.
     */
    protected Node m_nLogonInfo;
    /**
     * Holds the prefix resolver to use.
     */
    private PrefixResolver m_pr = NamespaceConstants.getPrefixResolver();
    /**
     * Holds the LDAP search root for this connection.
     */
    private String m_sSearchRoot;

    /**
     * Constructor. Creates the Cordys Gateway Client for a certificate.
     *
     * @param   acAuthenticationDetails  sTrustStore The location of the trust store.
     * @param   ccConfiguration          sTrustStorePassword The password of the trust store.
     *
     * @throws  CordysGatewayClientException  All exceptions are wrapped in this exception.
     */
    public CordysGatewayClient(IAuthenticationConfiguration acAuthenticationDetails,
                               ICGCConfiguration ccConfiguration)
                        throws CordysGatewayClientException
    {
        super(acAuthenticationDetails, ccConfiguration);
        setNamespaceAwareResponses(true);
    }

    /**
     * This method creates a SOAP message with the given name and namespace.
     *
     * @param   nRequest     The SOAP:Envelope to add it to.
     * @param   sMethodName  The name of the method.
     * @param   sNamespace   The namespace of the method.
     *
     * @return  The Element of the method. To get the root element of the message call
     *          eReturn.getOwnerDocument().getDocumentElement()
     *
     * @throws  CordysGatewayClientException  All exceptions are wrapped in this exception.
     */
    public Node addMethod(Node nRequest, String sMethodName, String sNamespace)
                   throws CordysGatewayClientException
    {
        Node nReturn = null;

        try
        {
            // First find the SOAP:Body
            Node nBody = XPathHelper.selectSingleNode(nRequest, "//" + PRE_SOAP + ":Body",
                                                      NamespaceConstants.getPrefixResolver());

            if (nBody != null)
            {
                Document dDoc = nBody.getOwnerDocument();
                Element eTemp = dDoc.createElementNS(sNamespace, sMethodName);
                eTemp.setAttribute("xmlns", sNamespace);

                nReturn = eTemp;

                nBody.appendChild(nReturn);
            }
        }
        catch (Exception e)
        {
            throw new CordysGatewayClientException(e, CGCMessages.CGC_ERROR_ADD_MESSAGE, sNamespace,
                                                   sMethodName);
        }

        return nReturn;
    }

    /**
     * This method creates a SOAP message with the given name and namespace.
     *
     * @param   sMethodName  The name of the method.
     * @param   sNamespace   The namespace of the method.
     *
     * @return  The Element of the method. To get the root element of the message call
     *          eReturn.getOwnerDocument().getDocumentElement()
     *
     * @throws  CordysGatewayClientException  In case the message creation fails.
     */
    public Element createMessage(String sMethodName, String sNamespace)
                          throws CordysGatewayClientException
    {
        Element eReturn = null;

        try
        {
            // Parse the base request.
            Document dDoc = parseXML(BASE_SOAP_REQUEST);

            // Create the method element.
            eReturn = dDoc.createElementNS(sNamespace, sMethodName);
            eReturn.setAttribute("xmlns", sNamespace);

            // Find the SOAP:Body and append the method node.
            Node nNode = XPathHelper.selectSingleNode(dDoc.getDocumentElement(),
                                                      "//" + PRE_SOAP + ":Body",
                                                      NamespaceConstants.getPrefixResolver());

            if (nNode == null)
            {
                throw new CordysGatewayClientException(CGCMessages.CGC_ERROR_NO_BODY_FOUND);
            }

            nNode.appendChild(eReturn);
        }
        catch (CordysGatewayClientException cgce)
        {
            throw cgce;
        }
        catch (Exception e)
        {
            throw new CordysGatewayClientException(e, CGCMessages.CGC_ERROR_CREATE_MESSAGE,
                                                   sNamespace, sMethodName);
        }

        return eReturn;
    }

    /**
     * Disconnect from the cordys gateway. Free all cordys resources
     */
    @Override public void disconnect()
    {
        super.disconnect();
        m_nLogonInfo = null;
    }

    /**
     * This method returns the DN of the authenticated user.
     *
     * @return  The DN of the authenticated user.
     */
    public String getAuthUserDN()
    {
        String sReturn = null;

        try
        {
            sReturn = XMLHelper.getData(m_nLogonInfo, "./:tuple/:old/:user/:authuserdn/text()");
        }
        catch (XMLWrapperException e)
        {
            getLogger().error("Error finding the auth user DN", e);
        }

        return sReturn;
    }

    /**
     * This method gets the Logger for this class.
     *
     * @return  The Logger for this class.
     */
    @Override public Logger getLogger()
    {
        return LOG;
    }

    /**
     * @see  com.cordys.coe.util.cgc.ICordysGatewayClient#getNamespaceAwareResponses()
     */
    public boolean getNamespaceAwareResponses()
    {
        return m_bNamespaceAwareResponses;
    }

    /**
     * This method returns the SAML token for the current user.
     *
     * @return  The SAML token for the current user.
     *
     * @see     com.cordys.coe.util.cgc.ICordysGatewayClient#getSAMLToken()
     */
    public Node getSAMLToken()
    {
        Node nReturn = null;

        if (ISSOAuthentication.class.isAssignableFrom(getAuthenticationDetails().getClass()))
        {
            ISSOAuthentication sa = (ISSOAuthentication) getAuthenticationDetails();
            nReturn = sa.getSAMLToken();
        }

        return nReturn;
    }

    /**
     * This method returns the search root of the current LDAP.
     *
     * @return  The search root.
     *
     * @see     com.cordys.coe.util.cgc.ICordysGatewayClient#getSearchRoot()
     */
    @Override public String getSearchRoot()
    {
        return m_sSearchRoot;
    }

    /**
     * This method inserts the specified entry in LDAP.
     *
     * @param   leNew  The new entry to insert.
     *
     * @return  The new entry.
     *
     * @throws  CordysGatewayClientException  In case of any exceptions.
     *
     * @see     com.cordys.coe.util.cgc.ICordysGatewayClient#insertLDAPEntry(com.novell.ldap.LDAPEntry)
     */
    @Override public LDAPEntry insertLDAPEntry(LDAPEntry leNew)
                                        throws CordysGatewayClientException
    {
        return updateLDAPEntry(null, leNew);
    }

    /**
     * This method reads the specified entry from LDAP.
     *
     * @param   sDN  The DN to read.
     *
     * @return  The read entry.
     *
     * @throws  CordysGatewayClientException  In case of any exceptions.
     *
     * @see     com.cordys.coe.util.cgc.ICordysGatewayClient#readLDAPEntry(java.lang.String)
     */
    @Override public LDAPEntry readLDAPEntry(String sDN)
                                      throws CordysGatewayClientException
    {
        LDAPEntry leReturn = null;

        try
        {
            Document dRequest = parseXML(XML_SEARCH_LDAP);

            Node nDN = XPathHelper.selectSingleNode(dRequest.getDocumentElement(),
                                                    "//" + PRE_LDAP_10 + ":dn",
                                                    NamespaceConstants.getPrefixResolver());
            XMLHelper.createText(sDN, nDN);

            // Send the request.
            Node nResponse = requestFromCordys(dRequest.getDocumentElement());

            // Now convert the response into a LDAP Entry object.
            Element eEntry = (Element) XPathHelper.selectSingleNode(nResponse,
                                                                    "//" + PRE_LDAP_10 + ":tuple/" +
                                                                    PRE_LDAP_10 + ":old/" +
                                                                    PRE_LDAP_10 + ":entry",
                                                                    NamespaceConstants
                                                                    .getPrefixResolver());

            if (eEntry != null)
            {
                leReturn = LDAPDomXmlUtil.getEntryFromXML(eEntry);
            }
        }
        catch (Exception e)
        {
            throw new CordysGatewayClientException(e, CGCMessages.CGC_ERROR_LDAP_GETLDAPOBJECT,
                                                   sDN);
        }

        return leReturn;
    }

    /**
     * This method sends the request to Cordys. The response is put back into an XML structure. The
     * tag returned is the pointer to the SOAP:Envelope. The resulting methods will ge a prefix
     * 'res'. So if you want to use an XPath on the result use: '//res:tuple' to get all the tuples.
     *
     * @param   eRequest  The request envelope.
     *
     * @return  The response.
     *
     * @throws  CordysGatewayClientException  In case of any general exceptions.
     * @throws  CordysSOAPException           In case of any SOAP fault returned.
     */
    public Element requestFromCordys(Element eRequest)
                              throws CordysGatewayClientException, CordysSOAPException
    {
        return requestFromCordys(eRequest, getConfiguration().getTimeout());
    }

    /**
     * This method sends the request to Cordys. The response is put back into an XML structure. The
     * tag returned is the pointer to the SOAP:Envelope. The resulting methods will ge a prefix
     * 'res'. So if you want to use an XPath on the result use: '//res:tuple' to get all the tuples.
     *
     * @param   eRequest  The request envelope.
     * @param   lTimeout  The timeout to use.
     *
     * @return  The response.
     *
     * @throws  CordysGatewayClientException  In case of any general exceptions.
     * @throws  CordysSOAPException           In case of any SOAP fault returned.
     */
    public Element requestFromCordys(Element eRequest, long lTimeout)
                              throws CordysGatewayClientException, CordysSOAPException
    {
        return requestFromCordys(eRequest, lTimeout, true, null, getGatewayURL(), m_sOrganization,
                                 null);
    }

    /**
     * This method sends the request to Cordys. The response is put back into an XML structure. The
     * tag returned is the pointer to the SOAP:Envelope.
     *
     * @param   eRequest   The request envelope.
     * @param   sReceiver  The DN of the receiving SOAP processor
     *
     * @return  The response.
     *
     * @throws  CordysGatewayClientException  In case of any exception other then a SOAP exception.
     * @throws  CordysSOAPException           For SOAP related exceptions.
     *
     * @see     com.cordys.coe.util.cgc.ICordysGatewayClient#requestFromCordys(org.w3c.dom.Element, java.lang.String)
     */
    @Override public Element requestFromCordys(Element eRequest, String sReceiver)
                                        throws CordysGatewayClientException, CordysSOAPException
    {
        return requestFromCordys(eRequest, getConfiguration().getTimeout(), true, null,
                                 getGatewayURL(), m_sOrganization, sReceiver);
    }

    /**
     * This method sends the request to Cordys and returns the response of the method.
     *
     * @param   aInputSoapRequest  The input request.
     * @param   lTimeout           The timeout to use.
     *
     * @return  The response of the request.
     *
     * @throws  CordysGatewayClientException  In case of any general exceptions.
     */
    public String requestFromCordys(String aInputSoapRequest, long lTimeout)
                             throws CordysGatewayClientException
    {
        String sReturn = null;

        PostMethod pmMethod = requestFromCordys(aInputSoapRequest, lTimeout, true, null,
                                                getGatewayURL(), m_sOrganization, null);

        try
        {
            try
            {
                sReturn = pmMethod.getResponseBodyAsString();
            }
            catch (IOException e)
            {
                throw new CordysGatewayClientException(e, CGCMessages.CGC_ERROR_REQUEST_FAILED,
                                                       aInputSoapRequest, lTimeout);
            }
        }
        finally
        {
            pmMethod.releaseConnection();
        }

        return sReturn;
    }

    /**
     * This method sends the request to Cordys. The response is put back into an XML structure. The
     * tag returned is the pointer to the SOAP:Envelope. The resulting methods will ge a prefix
     * 'res'. So if you want to use an XPath on the result use: '//res:tuple' to get all the tuples.
     *
     * @param   eRequest     The request envelope.
     * @param   lTimeout     The timeout to use.
     * @param   sSoapAction  SOAP action to be set in the request.
     *
     * @return  The response.
     *
     * @throws  CordysGatewayClientException  In case of any general exceptions.
     * @throws  CordysSOAPException           In case of any SOAP fault returned.
     */
    public Element requestFromCordys(Element eRequest, long lTimeout, String sSoapAction)
                              throws CordysGatewayClientException, CordysSOAPException
    {
        return requestFromCordys(eRequest, lTimeout, true, sSoapAction, getGatewayURL(),
                                 m_sOrganization, null);
    }

    /**
     * This method sends the request to Cordys. The response is put back into an XML structure. The
     * tag returned is the pointer to the SOAP:Envelope.
     *
     * @param   eRequest       The request envelope.
     * @param   lTimeout       The timeout to use.
     * @param   sOrganization  The organization to send it to.
     * @param   sSoapAction    SOAP action to be set in the request.
     *
     * @return  The response.
     *
     * @throws  CordysGatewayClientException  In case of any exception other then a SOAP exception.
     * @throws  CordysSOAPException           For SOAP related exceptions.
     *
     * @see     com.cordys.coe.util.cgc.ICordysGatewayClient#requestFromCordys(org.w3c.dom.Element,long,
     *          java.lang.String, java.lang.String)
     */
    public Element requestFromCordys(Element eRequest, long lTimeout, String sOrganization,
                                     String sSoapAction)
                              throws CordysGatewayClientException, CordysSOAPException
    {
        return requestFromCordys(eRequest, lTimeout, true, sSoapAction, getGatewayURL(),
                                 sOrganization, null);
    }

    /**
     * This method sends the request to Cordys. The response is put back into an XML structure. The
     * tag returned is the pointer to the SOAP:Envelope. The resulting methods will ge a prefix
     * 'res'. So if you want to use an XPath on the result use: '//res:tuple' to get all the tuples.
     *
     * @param   eRequest       The request envelope.
     * @param   lTimeout       The timeout to use.
     * @param   sSoapAction    SOAP action to be set in the request.
     * @param   sGatewayURL    The URL to which the request should be posted.
     * @param   sOrganization  Organization to which the request is to be sent.
     *
     * @return  The response.
     *
     * @throws  CordysGatewayClientException  In case of any general exceptions.
     * @throws  CordysSOAPException           In case of any SOAP fault returned.
     */
    public Element requestFromCordys(Element eRequest, long lTimeout, String sSoapAction,
                                     String sGatewayURL, String sOrganization)
                              throws CordysGatewayClientException, CordysSOAPException
    {
        return requestFromCordys(eRequest, lTimeout, true, sSoapAction, sGatewayURL, sOrganization,
                                 null);
    }

    /**
     * This method sends the request to Cordys. The response is put back into an XML structure. The
     * tag returned is the pointer to the SOAP:Envelope. The resulting methods will ge a prefix
     * 'res'. So if you want to use an XPath on the result use: '//res:tuple' to get all the tuples.
     * This method will not wait if the serverwatcher indicates the server is down.
     *
     * @param   eRequest  The request envelope.
     *
     * @return  The response.
     *
     * @throws  CordysGatewayClientException  In case of any general exceptions.
     * @throws  CordysSOAPException           In case of any SOAP fault returned.
     */
    public Element requestFromCordysNoBlocking(Element eRequest)
                                        throws CordysGatewayClientException, CordysSOAPException
    {
        return requestFromCordysNoBlocking(eRequest, getConfiguration().getTimeout());
    }

    /**
     * This method sends the request to Cordys. The response is put back into an XML structure. The
     * tag returned is the pointer to the SOAP:Envelope.
     *
     * @param   eRequest   The request envelope.
     * @param   sReceiver  The DN of the receiving SOAP processor
     *
     * @return  The response.
     *
     * @throws  CordysGatewayClientException  In case of any exception other then a SOAP exception.
     * @throws  CordysSOAPException           For SOAP related exceptions.
     *
     * @see     com.cordys.coe.util.cgc.ICordysGatewayClient#requestFromCordysNoBlocking(org.w3c.dom.Element,
     *          java.lang.String)
     */
    @Override public Element requestFromCordysNoBlocking(Element eRequest,
                                                         String sReceiver)
                                                  throws CordysGatewayClientException,
                                                         CordysSOAPException
    {
        return requestFromCordys(eRequest, getConfiguration().getTimeout(), false, null,
                                 getGatewayURL(), m_sOrganization, null);
    }

    /**
     * This method sends the request to Cordys. The response is put back into an XML structure. The
     * tag returned is the pointer to the SOAP:Envelope. The resulting methods will ge a prefix
     * 'res'. So if you want to use an XPath on the result use: '//res:tuple' to get all the tuples.
     * This method will not wait if the serverwatcher indicates the server is down.
     *
     * @param   eRequest  The request envelope.
     * @param   lTimeout  The timeout to use.
     *
     * @return  The response.
     *
     * @throws  CordysGatewayClientException  In case of any general exceptions.
     * @throws  CordysSOAPException           In case of any SOAP fault returned.
     */
    public Element requestFromCordysNoBlocking(Element eRequest, long lTimeout)
                                        throws CordysGatewayClientException, CordysSOAPException
    {
        return requestFromCordys(eRequest, lTimeout, false, null, getGatewayURL(), m_sOrganization,
                                 null);
    }

    /**
     * This method sends the request to Cordys. The response is put back into an XML structure. The
     * tag returned is the pointer to the SOAP:Envelope. The resulting methods will ge a prefix
     * 'res'. So if you want to use an XPath on the result use: '//res:tuple' to get all the tuples.
     * This method will not wait if the serverwatcher indicates the server is down.
     *
     * @param   eRequest       The request envelope.
     * @param   lTimeout       The timeout to use.
     * @param   sOrganization  DOCUMENTME
     *
     * @return  The response.
     *
     * @throws  CordysGatewayClientException  In case of any general exceptions.
     * @throws  CordysSOAPException           In case of any SOAP fault returned.
     */
    public Element requestFromCordysNoBlocking(Element eRequest, long lTimeout,
                                               String sOrganization)
                                        throws CordysGatewayClientException, CordysSOAPException
    {
        return requestFromCordys(eRequest, lTimeout, false, null, getGatewayURL(), sOrganization,
                                 null);
    }

    /**
     * This method sends the request to Cordys. The response is put back into an XML structure. The
     * tag returned is the pointer to the SOAP:Envelope.
     *
     * @param   eRequest       The request envelope.
     * @param   lTimeout       The timeout to use.
     * @param   sOrganization  The organization to send it to.
     * @param   sSoapAction    SOAP action to be set in the request.
     *
     * @return  The response.
     *
     * @throws  CordysGatewayClientException  In case of any exception other then a SOAP exception.
     * @throws  CordysSOAPException           For SOAP related exceptions.
     *
     * @see     com.cordys.coe.util.cgc.ICordysGatewayClient#requestFromCordysNoBlocking(org.w3c.dom.Element,
     *          long, java.lang.String, java.lang.String)
     */
    @Override public Element requestFromCordysNoBlocking(Element eRequest, long lTimeout,
                                                         String sOrganization,
                                                         String sSoapAction)
                                                  throws CordysGatewayClientException,
                                                         CordysSOAPException
    {
        return requestFromCordys(eRequest, lTimeout, false, null, getGatewayURL(), sOrganization,
                                 null);
    }

    /**
     * This method sends the request to Cordys. The response is put back into an XML structure. The
     * tag returned is the pointer to the SOAP:Envelope.
     *
     * @param   eRequest       The request envelope.
     * @param   lTimeout       The timeout to use.
     * @param   sOrganization  The organization to send it to.
     * @param   sSoapAction    SOAP action to be set in the request.
     * @param   sReceiver      The DN of the receiving SOAP processor
     *
     * @return  The response.
     *
     * @throws  CordysGatewayClientException  In case of any exception other then a SOAP exception.
     * @throws  CordysSOAPException           For SOAP related exceptions.
     *
     * @see     com.cordys.coe.util.cgc.ICordysGatewayClient#requestFromCordysNoBlocking(org.w3c.dom.Element,
     *          long, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override public Element requestFromCordysNoBlocking(Element eRequest, long lTimeout,
                                                         String sOrganization, String sSoapAction,
                                                         String sReceiver)
                                                  throws CordysGatewayClientException,
                                                         CordysSOAPException
    {
        return requestFromCordys(eRequest, lTimeout, false, null, getGatewayURL(), sOrganization,
                                 sReceiver);
    }

    /**
     * This method searches LDAP for certain entries.
     *
     * @param   sSearchRoot  The search root.
     * @param   iLDAPScope   The scope.
     * @param   sFilter      The filter.
     *
     * @return  The list of LDAP entries.
     *
     * @throws  CordysGatewayClientException  In case of any exceptions.
     *
     * @see     com.cordys.coe.util.cgc.ICordysGatewayClient#searchLDAP(java.lang.String, int,
     *          java.lang.String)
     */
    @Override public LDAPEntry[] searchLDAP(String sSearchRoot, int iLDAPScope,
                                            String sFilter)
                                     throws CordysGatewayClientException
    {
        LDAPEntry[] aleReturn = null;

        try
        {
            Document dRequest = parseXML(XML_SEARCH_LDAP);

            if ((sSearchRoot != null) && (sSearchRoot.length() > 0))
            {
                Node nDN = XPathHelper.selectSingleNode(dRequest.getDocumentElement(),
                                                        "//" + PRE_LDAP_10 + ":dn",
                                                        NamespaceConstants.getPrefixResolver());
                XMLHelper.createText(sSearchRoot, nDN);
            }

            Node nScope = XPathHelper.selectSingleNode(dRequest.getDocumentElement(),
                                                       "//" + PRE_LDAP_10 + ":scope",
                                                       NamespaceConstants.getPrefixResolver());
            XMLHelper.createText(String.valueOf(iLDAPScope), nScope);

            if ((sFilter != null) && (sFilter.length() > 0))
            {
                Node nFilter = XPathHelper.selectSingleNode(dRequest.getDocumentElement(),
                                                            "//" + PRE_LDAP_10 + ":filter",
                                                            NamespaceConstants.getPrefixResolver());
                XMLHelper.createText(sFilter, nFilter);
            }

            // Send the request.
            Node nResponse = requestFromCordys(dRequest.getDocumentElement());
            ArrayList<LDAPEntry> alNodes = new ArrayList<LDAPEntry>();

            // Now convert the response into a LDAP Entry object.
            NodeList nlEntries = XPathHelper.selectNodeList(nResponse,
                                                            "//" + PRE_LDAP_10 + ":tuple/" +
                                                            PRE_LDAP_10 + ":old/" + PRE_LDAP_10 +
                                                            ":entry",
                                                            NamespaceConstants.getPrefixResolver());

            for (int iEntryCount = 0; iEntryCount < nlEntries.getLength(); iEntryCount++)
            {
                LDAPEntry leEntry = LDAPDomXmlUtil.getEntryFromXML((Element) nlEntries.item(iEntryCount));
                alNodes.add(leEntry);
            }

            aleReturn = new LDAPEntry[alNodes.size()];
            alNodes.toArray(aleReturn);
        }
        catch (Exception e)
        {
            throw new CordysGatewayClientException(e, CGCMessages.CGC_ERROR_LDAP_SEARCH, sFilter);
        }

        return aleReturn;
    }

    /**
     * @see  com.cordys.coe.util.cgc.ICordysGatewayClient#setNamespaceAwareResponses(boolean)
     */
    public void setNamespaceAwareResponses(boolean value)
    {
        m_bNamespaceAwareResponses = value;
    }

    /**
     * This method updates the specified entry in LDAP. If the leOld is null it will be considered
     * an insert.
     *
     * @param   leOld  The old version of the entry.
     * @param   leNew  The new version of the entry.
     *
     * @return  The updated/new entry.
     *
     * @throws  CordysGatewayClientException  In case of any exceptions.
     *
     * @see     com.cordys.coe.util.cgc.ICordysGatewayClient#updateLDAPEntry(com.novell.ldap.LDAPEntry,
     *          com.novell.ldap.LDAPEntry)
     */
    @Override public LDAPEntry updateLDAPEntry(LDAPEntry leOld, LDAPEntry leNew)
                                        throws CordysGatewayClientException
    {
        LDAPEntry leReturn = null;

        try
        {
            Document dRequest = parseXML(XML_UPDATE_LDAP);

            Node nTuple = XPathHelper.selectSingleNode(dRequest.getDocumentElement(),
                                                       "//" + PRE_LDAP_10 + ":tuple",
                                                       NamespaceConstants.getPrefixResolver());

            if (leOld != null)
            {
                Element eOld = XMLHelper.createElementWithParentNS("old", nTuple);
                LDAPDomXmlUtil.entryToXML(leOld, eOld);
            }

            Element eNew = XMLHelper.createElementWithParentNS("new", nTuple);
            LDAPDomXmlUtil.entryToXML(leNew, eNew);

            // Send the request.
            Node nResponse = requestFromCordys(dRequest.getDocumentElement());

            // Now convert the response into a LDAP Entry object.
            Element eEntry = (Element) XPathHelper.selectSingleNode(nResponse,
                                                                    "//" + PRE_LDAP_10 + ":tuple/" +
                                                                    PRE_LDAP_10 + ":old/" +
                                                                    PRE_LDAP_10 + ":entry",
                                                                    NamespaceConstants
                                                                    .getPrefixResolver());

            if (eEntry != null)
            {
                leReturn = LDAPDomXmlUtil.getEntryFromXML(eEntry);
            }
        }
        catch (Exception e)
        {
            throw new CordysGatewayClientException(e, CGCMessages.CGC_ERROR_LDAP_UPDATE,
                                                   leNew.getDN());
        }

        return leReturn;
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
     * @throws  CordysSOAPException  In case of any SOAP fault returned.
     */
    @Override protected void checkForAndThrowCordysSOAPException(String sHTTPResponse,
                                                                 String sRequestXML)
                                                          throws CordysSOAPException
    {
        Document dDoc = XMLHelper.createDocumentFromXML(sHTTPResponse);

        if (dDoc != null)
        {
            // Figure out if we can find the SOAP:Fault structure.
            // Note: This code is for backwards compatibility. Because C3 will return code 500
            // in case of a SOAP fault.
            try
            {
                Node nSoapFault = XPathHelper.selectSingleNode(dDoc.getDocumentElement(),
                                                               "//" + PRE_SOAP + ":Fault",
                                                               NamespaceConstants
                                                               .getPrefixResolver());

                if (nSoapFault != null)
                {
                    if (getLogger().isDebugEnabled())
                    {
                        getLogger().debug("Found a SOAP fault: " +
                                          XMLHelper.XML2String(nSoapFault));
                    }

                    // Create the SoapException object.
                    CordysSOAPException cse = CordysSOAPException.parseSOAPFault(nSoapFault,
                                                                                 sRequestXML);

                    throw cse;
                }
            }
            catch (TransformerException e)
            {
                if (getLogger().isInfoEnabled())
                {
                    getLogger().info("Error checking for SOAP fault", e);
                }
            }
        }
    }

    /**
     * This method handles the custom Cordys login procedure which results in a wcp-session-id.
     *
     * @throws  TransformerException          In case of XPath exceptions.
     * @throws  XMLWrapperException           In case of XML parse exceptions.
     * @throws  CordysGatewayClientException  In case of gateway communication exceptions
     * @throws  CordysSOAPException           In case Cordys returns a SOAP fault.
     */
    protected void handleCustomAuthLogin()
                                  throws TransformerException, XMLWrapperException,
                                         CordysGatewayClientException, CordysSOAPException
    {
        Document dLogin = parseXML(XML_AUTHENTICATE);

        Node nAuthenticate = XPathHelper.selectSingleNode(dLogin.getDocumentElement(),
                                                          "//" + PRE_AUTH + ":Authenticate",
                                                          NamespaceConstants.getPrefixResolver());

        Element eUsername = XMLHelper.createElementNS("username", XMLNS_WEBGATEWAY_1_0,
                                                      nAuthenticate);
        XMLHelper.createText(getUsername(), eUsername);

        Element ePassword = XMLHelper.createElementNS("password", XMLNS_WEBGATEWAY_1_0,
                                                      nAuthenticate);
        XMLHelper.createText(getPassword(), ePassword);

        // The login request has to go to a different URL.
        String sGatewayURL = getGatewayURL();
        Matcher mMatcher = Pattern.compile("[^/]+$").matcher(sGatewayURL);

        if (mMatcher.find())
        {
            sGatewayURL = sGatewayURL.substring(0, mMatcher.start());
            sGatewayURL += AUTHENTICATION_GATEWAY;
        }
        else
        {
            throw new CordysGatewayClientException(CGCMessages.CGC_ERROR_DETERMINE_URL);
        }

        Element eLoginResponse = requestFromCordys(dLogin.getDocumentElement(), sGatewayURL,
                                                   m_sOrganization);

        // Get the sessionID from the response.
        Node nSessionID = XPathHelper.selectSingleNode(eLoginResponse,
                                                       "//" + PRE_AUTH + ":wcp-session",
                                                       NamespaceConstants.getPrefixResolver());

        if (nSessionID == null)
        {
            throw new CordysGatewayClientException(CGCMessages.CGC_ERROR_CA_NO_SESSIONID);
        }

        String sSession = XMLHelper.getData(nSessionID, "./text()");

        if ((sSession == null) || (sSession.length() == 0))
        {
            throw new CordysGatewayClientException(CGCMessages.CGC_ERROR_CA_EMPTY_SESSIONID);
        }

        setWCPSessionID(sSession);
    }

    /**
     * This method handles the SSO login procedure which results in a SAML token. TODO: Add logic to
     * automatically refresh the SAML token when it expires.
     *
     * @throws  TransformerException          In case of XPath exceptions.
     * @throws  XMLWrapperException           In case of XML parse exceptions.
     * @throws  CordysGatewayClientException  In case of gateway communication exceptions
     * @throws  CordysSOAPException           In case Cordys returns a SOAP fault.
     */
    protected void handleSSOAuthLogin()
                               throws TransformerException, XMLWrapperException,
                                      CordysGatewayClientException, CordysSOAPException
    {
        // Create the proper request
        Document dLogin = parseXML(XML_SSO_LOGIN);

        Node nUsername = XPathHelper.selectSingleNode(dLogin.getDocumentElement(),
                                                      "//" + PRE_WSSE + ":UsernameToken/" +
                                                      PRE_WSSE + ":Username", m_pr);
        XMLHelper.createText(getUsername(), nUsername);

        Node nPassword = XPathHelper.selectSingleNode(dLogin.getDocumentElement(),
                                                      "//" + PRE_WSSE + ":UsernameToken/" +
                                                      PRE_WSSE + ":Password", m_pr);
        XMLHelper.createText(getPassword(), nPassword);

        Node nNameIdentifier = XPathHelper.selectSingleNode(dLogin.getDocumentElement(),
                                                            "//" + PRE_SAML + ":Subject/" +
                                                            PRE_SAML + ":NameIdentifier", m_pr);
        XMLHelper.createText(getUsername(), nNameIdentifier);

        // Fill in the datetime and request id.
        Element eRequest = (Element) XPathHelper.selectSingleNode(dLogin.getDocumentElement(),
                                                                  "//" + PRE_SOAP + ":Body/" +
                                                                  PRE_SAMLP + ":Request", m_pr);

        eRequest.setAttribute("IssueInstant", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                              .format(new Date()));
        eRequest.setAttribute("RequestID", UUID.randomUUID().toString());

        Element eLoginResponse = requestFromCordys(dLogin.getDocumentElement(), getGatewayURL(),
                                                   m_sOrganization);

        // First check the login status
        String sStatus = XPathHelper.getStringValue(eLoginResponse,
                                                    "//" + PRE_SAMLP + ":Status/" + PRE_SAMLP +
                                                    ":StatusCode/text()", m_pr, "samlp:Success");

        if (!sStatus.endsWith("Success"))
        {
            throw new CordysGatewayClientException(CGCMessages.CGC_ERROR_SSO_STATUS, sStatus);
        }

        // Now build up the token.
        Document dSAMLToken = XMLHelper.createDocumentFromXML("<wsse:Security xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\"/>",
                                                              true);

        Node nAssertion = XPathHelper.selectSingleNode(eLoginResponse,
                                                       "//" + PRE_SOAP + ":Body/" + PRE_SAMLP +
                                                       ":Response/" + PRE_SAML + ":Assertion",
                                                       m_pr);

        if (nAssertion == null)
        {
            throw new CordysGatewayClientException(CGCMessages.CGC_ERROR_SSO_ASSERTION);
        }

        Node nSignature = XPathHelper.selectSingleNode(eLoginResponse,
                                                       "//" + PRE_SOAP + ":Body/" + PRE_SAMLP +
                                                       ":Response/" + PRE_XMLSIG + ":Signature",
                                                       m_pr);

        if (nSignature == null)
        {
            throw new CordysGatewayClientException(CGCMessages.CGC_ERROR_SSO_SIGNATURE);
        }

        dSAMLToken.getDocumentElement().appendChild(dSAMLToken.importNode(nAssertion, true));
        dSAMLToken.getDocumentElement().appendChild(dSAMLToken.importNode(nSignature, true));

        // Set the SAML token to the session.
        ((ISSOAuthentication) getAuthenticationDetails()).setSAMLToken(dSAMLToken
                                                                       .getDocumentElement());

        if (LOG.isDebugEnabled())
        {
            LOG.debug("Received the SAML token: " + NiceDOMWriter.write(dSAMLToken));
        }
    }

    /**
     * Sends a login message to the gateway.
     *
     * @throws  CordysGatewayClientException  In case of any exceptions.
     */
    @Override protected void sendLoginMessage()
                                       throws CordysGatewayClientException
    {
        // need to send a message to see if the configuration is OK
        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("Sending logon request to the Cordys server (" +
                              getConfiguration().getHost() + ")");
        }

        try
        {
            Document dRequest = parseXML(XML_GET_USER_DETAILS);

            // First we need to do the login sequence for SSO and Cordys Custom auth.
            if (ICordysCustomAuthentication.class.isAssignableFrom(getAuthenticationDetails()
                                                                       .getClass()))
            {
                // In case of custom authentication we first need to send the login SOAP call.
                handleCustomAuthLogin();
            }
            else if (ISSOAuthentication.class.isAssignableFrom(getAuthenticationDetails()
                                                                   .getClass()))
            {
                // In case of SSO we first need to get the proper SAML token.
                handleSSOAuthLogin();
            }

            // Only if we're suppost to send the login message (which basically means executing
            // getUserDetails) we're going to.
            if (getConfiguration().getLoginToCordysOnConnect())
            {
                Element eResponse = requestFromCordys(dRequest.getDocumentElement());

                m_nLogonInfo = XPathHelper.selectSingleNode(eResponse,
                                                            "/" + PRE_SOAP + ":Envelope/" +
                                                            PRE_SOAP + ":Body/" + PRE_LDAP +
                                                            ":GetUserDetailsResponse",
                                                            NamespaceConstants.getPrefixResolver());

                if (m_nLogonInfo == null)
                {
                    throw new CordysGatewayClientException(CGCMessages.CGC_ERROR_NOM_XML,
                                                           "<GetUserDetailsResponse>",
                                                           NiceDOMWriter.write(eResponse),
                                                           NiceDOMWriter.write(dRequest
                                                                               .getDocumentElement()));
                }

                Node nTuple = XPathHelper.selectSingleNode(m_nLogonInfo, "//" + PRE_LDAP + ":tuple",
                                                           NamespaceConstants.getPrefixResolver());

                if (nTuple == null)
                {
                    throw new CordysGatewayClientException(CGCMessages.CGC_ERROR_NOM_XML, "<tuple>",
                                                           NiceDOMWriter.write(eResponse),
                                                           NiceDOMWriter.write(dRequest
                                                                               .getDocumentElement()));
                }

                // Parse the user information
                setUserInfo(UserInfoFactory.createUserInfo((Element) nTuple));

                // Get the directory search root.
                String sAuthUserDN = getAuthUserDN();
                Pattern pPattern = Pattern.compile("cn=cordys.+$");
                Matcher mMatcher = pPattern.matcher(sAuthUserDN);

                if (mMatcher.find())
                {
                    m_sSearchRoot = mMatcher.group();
                }
            }

            m_bConnected = true;
        }
        catch (CordysGatewayClientException cgce)
        {
            throw cgce;
        }
        catch (Exception e)
        {
            LOG.error("Unexpected error during login.", e);
        }

        // If there is a server watcher make sure it know the cgc to use
        if (m_swWatcher != null)
        {
            m_swWatcher.setCordysGatewayClient(this);
        }
    }

    /**
     * This method will add the SSO token if needed to the passed on XML if needed.
     *
     * @param  nRequest  The request to fix.
     */
    private void addSSOToken(Node nRequest)
    {
        if (ISSOAuthentication.class.isAssignableFrom(getAuthenticationDetails().getClass()))
        {
            try
            {
                ISSOAuthentication sa = (ISSOAuthentication) getAuthenticationDetails();

                // Only fix the request if the SAML token is already set. If this is not done we
                // screw up the login request.
                if (sa.getSAMLToken() != null)
                {
                    if (LOG.isDebugEnabled())
                    {
                        LOG.debug("Adding SSO token to the request.");
                    }

                    Document dDoc = null;

                    if (nRequest instanceof Document)
                    {
                        dDoc = (Document) nRequest;
                    }
                    else
                    {
                        dDoc = nRequest.getOwnerDocument();
                    }

                    Element eEnvelope = dDoc.getDocumentElement();
                    Node nHeader = XPathHelper.selectSingleNode(eEnvelope, PRE_SOAP + ":Header",
                                                                m_pr);

                    if (nHeader == null)
                    {
                        nHeader = dDoc.createElementNS(NamespaceDefinitions.XMLNS_SOAP_1_1,
                                                       "Header");
                        nHeader.setPrefix(PRE_SOAP);
                        nHeader = eEnvelope.insertBefore(nHeader, eEnvelope.getFirstChild());
                    }

                    Node nSecurity = XPathHelper.selectSingleNode(nHeader, PRE_WSSE + ":Security",
                                                                  m_pr);

                    if (nSecurity != null)
                    {
                        // Remove it
                        nSecurity.getParentNode().removeChild(nSecurity);
                    }

                    // Create it
                    Node nNew = dDoc.importNode(sa.getSAMLToken().cloneNode(true), true);
                    nHeader.appendChild(nNew);
                }
                else
                {
                    if (LOG.isDebugEnabled())
                    {
                        LOG.debug("There is no SSO token set yet.");
                    }
                }
            }
            catch (Exception e)
            {
                LOG.error("Error adding SAML token to the request.", e);
            }
        }
    }

    /**
     * This method returns a new document with the byte [] parsed.
     *
     * @param   baXML  the actual XML.
     *
     * @return  The document with the parsed XML.
     *
     * @throws  XMLWrapperException  DOCUMENTME
     */
    private Document parseXML(byte[] baXML)
                       throws XMLWrapperException
    {
        Document dDoc = null;

        dDoc = XMLHelper.createDocumentFromStream(new ByteArrayInputStream(baXML));

        return dDoc;
    }

    /**
     * This method sneds the given soap request to the given URL.
     *
     * @param   eRequest       The actual request to send.
     * @param   sGatewayURL    The URL to use.
     * @param   sOrganization  Organization to which the request is to be sent.
     *
     * @return  The response.
     *
     * @throws  CordysGatewayClientException  Thrown if the request failed.
     * @throws  CordysSOAPException           Thrown if a SOAP fault was received.
     */
    private Element requestFromCordys(Element eRequest, String sGatewayURL,
                                      String sOrganization)
                               throws CordysGatewayClientException, CordysSOAPException
    {
        return requestFromCordys(eRequest, getTimeout(), true, null, sGatewayURL, sOrganization,
                                 null);
    }

    /**
     * This method sends the request to Cordys. The response is put back into an XML structure. The
     * tag returned is the pointer to the SOAP:Envelope. The resulting methods will ge a prefix
     * 'res'. So if you want to use an XPath on the result use: '//res:tuple' to get all the tuples.
     *
     * @param   eRequest              The request envelope.
     * @param   lTimeout              The timeout to use.
     * @param   bBlockIfServerIsDown  If this is true then the call will block indefinately untill
     *                                the server is back online.
     * @param   sSoapAction           SOAP action to be set in the request.
     * @param   sGatewayURL           The URL to which the request should be posted.
     * @param   sOrganization         Organization to which the request is to be sent.
     * @param   sReceiver             The DN of the receiving SOAP processor
     *
     * @return  The response.
     *
     * @throws  CordysGatewayClientException  In case of any exceptions.
     * @throws  CordysSOAPException           If a SOAP fault is returned.
     */
    private Element requestFromCordys(Element eRequest, long lTimeout, boolean bBlockIfServerIsDown,
                                      String sSoapAction, String sGatewayURL, String sOrganization,
                                      String sReceiver)
                               throws CordysGatewayClientException, CordysSOAPException
    {
        Element eReturn = null;

        // Fix the request for SSO
        addSSOToken(eRequest);

        // Make sure the space is preserved, otherwise the SSO tokens signature might break.
        String sRequestXML = NiceDOMWriter.write(eRequest, 4, true, true, true);

        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("Request:\n" + sRequestXML);
        }

        Map<String, String> mExtraHeaders = new HashMap<String, String>();

        if (sSoapAction != null)
        {
            mExtraHeaders.put(SOAP_ACTION_HEADER, sSoapAction);
        }

        if (sGatewayURL == null)
        {
            sGatewayURL = getGatewayURL();
        }

        PostMethod pm = requestFromCordys(sRequestXML, lTimeout, bBlockIfServerIsDown,
                                          mExtraHeaders, sGatewayURL, sOrganization, sReceiver);

        try
        {
            Document dDoc;

            try
            {
                dDoc = XMLHelper.createDocumentFromStream(pm.getResponseBodyAsStream(),
                                                          m_bNamespaceAwareResponses);
            }
            catch (Exception e)
            {
                // Try to get the response as string
                String sData = "UNKNOWN";

                try
                {
                    sData = pm.getResponseBodyAsString();
                }
                catch (Exception eIgnore)
                {
                    // Ignore it.
                }
                throw new CordysGatewayClientException(e, CGCMessages.CGC_ERROR_PARSE_RESPONSE,
                                                       sData);
            }

            eReturn = dDoc.getDocumentElement();

            if (isCheckingForFaults())
            {
                // Parse for SOAP faults.
                try
                {
                    Node nFault = XPathHelper.selectSingleNode(eReturn,
                                                               "/" + PRE_SOAP + ":Envelope/" +
                                                               PRE_SOAP + ":Body/" + PRE_SOAP +
                                                               ":Fault",
                                                               NamespaceConstants
                                                               .getPrefixResolver());

                    if (nFault != null)
                    {
                        throw CordysSOAPException.parseSOAPFault(nFault, eRequest);
                    }
                }
                catch (TransformerException te)
                {
                    throw new CordysGatewayClientException(te,
                                                           CGCMessages.CGC_ERROR_SEARCH_FOR_FAULT);
                }
            }
        }
        finally
        {
            pm.releaseConnection();
        }

        return eReturn;
    }
}
