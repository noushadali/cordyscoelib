package com.cordys.coe.util.connection.impl;

import com.cordys.coe.util.cgc.CGCFactory;
import com.cordys.coe.util.cgc.CordysGatewayClientException;
import com.cordys.coe.util.cgc.ICordysGatewayClient;
import com.cordys.coe.util.cgc.ICordysGatewayClientFactory;
import com.cordys.coe.util.cgc.config.CGCAuthenticationFactory;
import com.cordys.coe.util.cgc.config.CGCConfigFactory;
import com.cordys.coe.util.cgc.config.EAuthenticationType;
import com.cordys.coe.util.cgc.config.IAuthenticationConfiguration;
import com.cordys.coe.util.cgc.config.ICGCConfiguration;
import com.cordys.coe.util.cgc.config.ICGCSSLConfiguration;
import com.cordys.coe.util.config.IWebGatewayConfiguration;
import com.cordys.coe.util.connection.CordysConnectionException;
import com.cordys.coe.util.connection.IWebGatewayConnection;
import com.cordys.coe.util.connection.LDAPEntryWrapper;
import com.cordys.coe.util.exceptions.XMLWrapperException;
import com.cordys.coe.util.xml.dom.XMLHelper;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPModification;

import java.io.ByteArrayInputStream;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class wraps around the web gateway connection to Cordys.
 *
 * @author  pgussow
 */
public class WebGatewayConnection extends AbstractConnection
    implements IWebGatewayConnection, ICordysGatewayClientFactory
{
    /**
     * Holds the base soap request.
     */
    private static final byte[] BASE_SOAP_REQUEST = "<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP:Body/></SOAP:Envelope>"
                                                    .getBytes();
    /**
     * Holds the namespace for the methodset LDAP.
     */
    private static final String NSP_LDAP = "http://schemas.cordys.com/1.0/ldap";
    /**
     * Holds the method name SearchLDAP.
     */
    private static final String MTD_SEARCH_LDAP = "SearchLDAP";
    /**
     * Holds the method name Update.
     */
    private static final String MTD_UPDATE = "Update";
    /**
     * Holds the method name DeleteRecursive.
     */
    private static final String MTD_DELETE_RECURSIVE = "DeleteRecursive";
    /**
     * Holds the instance of the Cordys gateway client.
     */
    private ICordysGatewayClient m_cgcGateway;
    /**
     * Holds the directory search root for this connection.
     */
    private String m_sSearchRoot;

    /**
     * Creates a new WebGatewayConnection object and connects it to the server.
     *
     * @param   wgcConfig        The configuration of the connection.
     * @param   bCheckSoapFault  Whether or not the connection should check for soap faults.
     *
     * @throws  CordysConnectionException  Thrown if the connection could not be created.
     */
    public WebGatewayConnection(IWebGatewayConfiguration wgcConfig, boolean bCheckSoapFault)
                         throws CordysConnectionException
    {
        this(wgcConfig, bCheckSoapFault, true);
    }

    /**
     * Creates a new WebGatewayConnection object.
     *
     * @param   wgcConfig        The configuration of the connection.
     * @param   bCheckSoapFault  Whether or not the connection should check for soap faults.
     * @param   bConnect         If <code>true</code> automatically connect to the server.
     *
     * @throws  CordysConnectionException  Thrown if the connection could not be created.
     */
    public WebGatewayConnection(IWebGatewayConfiguration wgcConfig, boolean bCheckSoapFault,
                                boolean bConnect)
                         throws CordysConnectionException
    {
        super(wgcConfig, bCheckSoapFault);

        // Connect it.
        try
        {
            if (wgcConfig.getAuthenticationType() == EAuthenticationType.SSO)
            {
                IAuthenticationConfiguration ac = CGCAuthenticationFactory.createSSOAuthentication(wgcConfig
                                                                                                   .getDomainUsername(),
                                                                                                   wgcConfig
                                                                                                   .getDomainPassword());
                ICGCConfiguration cc = CGCConfigFactory.createConfiguration(wgcConfig
                                                                            .getServername(),
                                                                            wgcConfig.getPort(),
                                                                            false);

                m_cgcGateway = CGCFactory.createCGC(ac, cc);
            }
            else if (wgcConfig.useDomainAuthentication())
            {
                IAuthenticationConfiguration ac = CGCAuthenticationFactory.createNTLMAuthentication(wgcConfig
                                                                                                    .getDomainUsername(),
                                                                                                    wgcConfig
                                                                                                    .getDomainPassword(),
                                                                                                    wgcConfig
                                                                                                    .getDomain());
                ICGCConfiguration cc = CGCConfigFactory.createConfiguration(wgcConfig
                                                                            .getServername(),
                                                                            wgcConfig.getPort(),
                                                                            false);

                m_cgcGateway = CGCFactory.createCGC(ac, cc);
            }
            else
            {
                try
                {
                    IAuthenticationConfiguration ac = CGCAuthenticationFactory
                                                      .createClientCertificateAuthentication(wgcConfig
                                                                                             .getCertificateLocation(),
                                                                                             wgcConfig
                                                                                             .getCertificatePassword(),
                                                                                             wgcConfig
                                                                                             .getCertificateType());

                    ICGCSSLConfiguration cc = (ICGCSSLConfiguration) CGCConfigFactory
                                              .createConfiguration(wgcConfig.getServername(),
                                                                   wgcConfig.getPort(), true);
                    cc.setTrustStore(wgcConfig.getTrustStoreLocation());
                    cc.setTrustStorePassword(wgcConfig.getTrustStorePassword());
                    cc.setTrustStoreType(wgcConfig.getTrustStoreType());

                    m_cgcGateway = CGCFactory.createCGC(ac, cc);
                }
                catch (CordysGatewayClientException e)
                {
                    throw new CordysConnectionException(CordysConnectionException.EC_CREATION,
                                                        "Error creating Cordys connection.", e);
                }
            }
            m_cgcGateway.setGatewayURL(wgcConfig.getGatewayURL());
            m_cgcGateway.setTimeout(wgcConfig.getTimeout());

            if (bConnect)
            {
                m_cgcGateway.connect();
            }
        }
        catch (Exception e)
        {
            throw new CordysConnectionException(CordysConnectionException.EC_CREATION,
                                                "Error logging on to Cordys.", e);
        }

        if (m_cgcGateway.isConnected())
        {
            // Get the directory search root.
            String sAuthUserDN = m_cgcGateway.getAuthUserDN();
            Pattern pPattern = Pattern.compile("cn=cordys.+$");
            Matcher mMatcher = pPattern.matcher(sAuthUserDN);

            if (mMatcher.find())
            {
                m_sSearchRoot = mMatcher.group();
            }
        }
    }

    /**
     * This method modifies the LDAP attribute.
     *
     * @param   leEntry                     The entry.
     * @param   laAttribute                 The attribute to modify
     * @param   iLDAPModificationAttribute  The action that should be done.
     *
     * @throws  CordysConnectionException  DOCUMENTME
     *
     * @see     com.cordys.coe.util.connection.ICordysConnection#changeLDAPAttibute(com.novell.ldap.LDAPEntry,
     *          com.novell.ldap.LDAPAttribute, int)
     */
    public void changeLDAPAttibute(LDAPEntry leEntry, LDAPAttribute laAttribute,
                                   int iLDAPModificationAttribute)
                            throws CordysConnectionException
    {
        try
        {
            Element eMethod = m_cgcGateway.createMessage(MTD_UPDATE, NSP_LDAP);

            Element eTuple = XMLHelper.createElement("tuple", eMethod);

            if (leEntry instanceof LDAPEntryWrapper)
            {
                LDAPEntryWrapper lew = (LDAPEntryWrapper) leEntry;
                Element eOriginal = lew.getOriginalXML();

                if (eOriginal != null)
                {
                    Element eOld = XMLHelper.createElement("old", eTuple);
                    Node nClone = eOriginal.cloneNode(true);
                    nClone = eOld.getOwnerDocument().importNode(nClone, true);
                    eOld.appendChild(nClone);
                }
            }

            Element eNew = XMLHelper.createElement("new", eTuple);
            LDAPDomXmlUtil.entryToXML(leEntry, eNew);

            // Send the request.
            Node nResponse = sendAndWait(getEnvelope(eMethod));

            if (leEntry instanceof LDAPEntryWrapper)
            {
                LDAPEntryWrapper lew = (LDAPEntryWrapper) leEntry;

                // Now convert the response into a LDAP Entry object.
                Node[] anResponses = XMLHelper.getMethodResponses(nResponse, "res");

                for (int iCount = 0; iCount < anResponses.length; iCount++)
                {
                    Node nMethod = anResponses[iCount];
                    NodeList nlEntries = XMLHelper.getNodeList(nMethod, ".//:tuple/:new/:entry");

                    if (nlEntries.getLength() > 0)
                    {
                        lew.setOriginalXML((Element) nlEntries.item(0));
                    }
                }
            }
        }
        catch (Exception e)
        {
            throw new CordysConnectionException(CordysConnectionException.EC_LDAP,
                                                "Error updating entry " + leEntry.getDN() +
                                                " from LDAP.", e);
        }
    }

    /**
     * This mehtod changes the LDAP on an entry basis.
     *
     * @param   leEntry            The entry to modify.
     * @param   iLDAPModification  The type of modification.
     *
     * @throws  CordysConnectionException  DOCUMENTME
     *
     * @see     com.cordys.coe.util.connection.ICordysConnection#changeLDAPEntry(com.novell.ldap.LDAPEntry,
     *          int)
     */
    public void changeLDAPEntry(LDAPEntry leEntry, int iLDAPModification)
                         throws CordysConnectionException
    {
        if (iLDAPModification == LDAPModification.REPLACE)
        {
            try
            {
                Element eMethod = m_cgcGateway.createMessage(MTD_UPDATE, NSP_LDAP);

                Element eTuple = XMLHelper.createElement("tuple", eMethod);

                if (leEntry instanceof LDAPEntryWrapper)
                {
                    LDAPEntryWrapper lew = (LDAPEntryWrapper) leEntry;
                    Element eOriginal = lew.getOriginalXML();

                    if (eOriginal != null)
                    {
                        Element eOld = XMLHelper.createElement("old", eTuple);
                        Node nClone = eOriginal.cloneNode(true);
                        nClone = eOld.getOwnerDocument().importNode(nClone, true);
                        eOld.appendChild(nClone);
                    }
                }

                Element eNew = XMLHelper.createElement("new", eTuple);
                LDAPDomXmlUtil.entryToXML(leEntry, eNew);

                // Send the request.
                Node nResponse = sendAndWait(getEnvelope(eMethod));

                if (leEntry instanceof LDAPEntryWrapper)
                {
                    LDAPEntryWrapper lew = (LDAPEntryWrapper) leEntry;

                    // Now convert the response into a LDAP Entry object.
                    Node[] anResponses = XMLHelper.getMethodResponses(nResponse, "res");

                    for (int iCount = 0; iCount < anResponses.length; iCount++)
                    {
                        Node nMethod = anResponses[iCount];
                        NodeList nlEntries = XMLHelper.getNodeList(nMethod,
                                                                   ".//:tuple/:new/:entry");

                        if (nlEntries.getLength() > 0)
                        {
                            lew.setOriginalXML((Element) nlEntries.item(0));
                        }
                    }
                }
            }
            catch (Exception e)
            {
                throw new CordysConnectionException(CordysConnectionException.EC_LDAP,
                                                    "Error updating entry " + leEntry.getDN() +
                                                    " from LDAP.", e);
            }
        }
        else if (iLDAPModification == LDAPModification.ADD)
        {
            try
            {
                Element eMethod = m_cgcGateway.createMessage(MTD_UPDATE, NSP_LDAP);

                Element eTuple = XMLHelper.createElement("tuple", eMethod);

                Element eNew = XMLHelper.createElement("new", eTuple);
                LDAPDomXmlUtil.entryToXML(leEntry, eNew);

                // Send the request.
                Node nResponse = sendAndWait(getEnvelope(eMethod));

                if (leEntry instanceof LDAPEntryWrapper)
                {
                    LDAPEntryWrapper lew = (LDAPEntryWrapper) leEntry;

                    // Now convert the response into a LDAP Entry object.
                    Node[] anResponses = XMLHelper.getMethodResponses(nResponse, "res");

                    for (int iCount = 0; iCount < anResponses.length; iCount++)
                    {
                        Node nMethod = anResponses[iCount];
                        NodeList nlEntries = XMLHelper.getNodeList(nMethod,
                                                                   ".//:tuple/:new/:entry");

                        if (nlEntries.getLength() > 0)
                        {
                            lew.setOriginalXML((Element) nlEntries.item(0));
                        }
                    }
                }
            }
            catch (Exception e)
            {
                throw new CordysConnectionException(CordysConnectionException.EC_LDAP,
                                                    "Error adding entry " + leEntry.getDN() +
                                                    " to LDAP.", e);
            }
        }
        else if (iLDAPModification == LDAPModification.DELETE)
        {
            deleteLDAPEntry(leEntry, false);
        }
    }

    /**
     * This method creates a SOAP-method for the given name and namespace. In the header the
     * receiver is specified as the one that is passed on. Also the URI of the receiving backend
     * component is specified in the header. The user on who's behalf the message will be sent is
     * the user that was passed on to this method. The envelope of the created message will be added
     * to the global nodes-arraylist.
     *
     * @param   sReceiver   The receiver of the message.
     * @param   sUri        The URI of the receiver.
     * @param   sName       The name of the method.
     * @param   sNamespace  The namespace of the method.
     * @param   sUser       The user on who's behalf the message should be sent.
     *
     * @return  The XMLNode to the method.
     *
     * @throws  CordysConnectionException  DOCUMENTME
     *
     * @see     com.cordys.coe.util.connection.ICordysConnection#createSoapMethod(java.lang.String,
     *          java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public Node createSoapMethod(String sReceiver, String sUri, String sName, String sNamespace,
                                 String sUser)
                          throws CordysConnectionException
    {
        Element nReturn = null;

        Node nBase = createBaseSoapEnvelope();

        nReturn = XMLHelper.createElementNS(sName, sNamespace, nBase);

        return nReturn;
    }

    /**
     * This method deletes the given LDAp entry.
     *
     * @param   leEntry     The entry to delete.
     * @param   bRecursive  Whether or not to delete it recursively.
     *
     * @throws  CordysConnectionException  DOCUMENTME
     *
     * @see     com.cordys.coe.util.connection.ICordysConnection#deleteLDAPEntry(com.novell.ldap.LDAPEntry,
     *          boolean)
     */
    public void deleteLDAPEntry(LDAPEntry leEntry, boolean bRecursive)
                         throws CordysConnectionException
    {
        String sMethodName = MTD_UPDATE;

        if (bRecursive == true)
        {
            sMethodName = MTD_DELETE_RECURSIVE;
        }

        try
        {
            Element eMethod = m_cgcGateway.createMessage(sMethodName, NSP_LDAP);

            Element eTuple = XMLHelper.createElement("tuple", eMethod);

            if (leEntry instanceof LDAPEntryWrapper)
            {
                LDAPEntryWrapper lew = (LDAPEntryWrapper) leEntry;
                Element eOriginal = lew.getOriginalXML();

                if (eOriginal != null)
                {
                    Element eOld = XMLHelper.createElement("old", eTuple);
                    Node nClone = eOriginal.cloneNode(true);
                    nClone = eOld.getOwnerDocument().importNode(nClone, true);
                    eOld.appendChild(nClone);
                }
            }
            else
            {
                Element eOld = XMLHelper.createElement("old", eTuple);
                LDAPDomXmlUtil.entryToXML(leEntry, eOld);
            }

            // Send the request.
            sendAndWait(getEnvelope(eMethod));
        }
        catch (Exception e)
        {
            throw new CordysConnectionException(CordysConnectionException.EC_LDAP,
                                                "Error updating entry " + leEntry.getDN() +
                                                " from LDAP.", e);
        }
    }

    /**
     * This method returns the envelope node for the passed on method node.
     *
     * @param   nMethod  The method node.
     *
     * @return  The Envelope node.
     */
    @Override
    public Node getEnvelope(Node nMethod)
    {
        return nMethod.getOwnerDocument().getDocumentElement();
    }

    /**
     * Return a client (the factory is also responsable for creating the gateway).
     *
     * @return  A cordys gateway client.
     *
     * @see     com.cordys.coe.util.cgc.ICordysGatewayClientFactory#getGatewayClientInstance()
     */
    public ICordysGatewayClient getGatewayClientInstance()
    {
        return m_cgcGateway;
    }

    /**
     * This method gets the oranizational user for this connection. This method returns null, since
     * this connection type does not support different users.
     *
     * @return  The oranizational user for this connection.
     */
    public String getOrganizationalUser()
    {
        return null;
    }

    /**
     * This method returns the search root of the current LDAP.
     *
     * @return  The search root.
     *
     * @see     com.cordys.coe.util.connection.ICordysConnection#getSearchRoot()
     */
    public String getSearchRoot()
    {
        return m_sSearchRoot;
    }

    /**
     * This method reads the specified entry from LDAP.
     *
     * @param   sDN  The DN to read.
     *
     * @return  The read entry.
     *
     * @throws  CordysConnectionException  DOCUMENTME
     */
    public LDAPEntry readLDAPEntry(String sDN)
                            throws CordysConnectionException
    {
        LDAPEntry leReturn = null;

        try
        {
            Element eMethod = m_cgcGateway.createMessage(MTD_SEARCH_LDAP, NSP_LDAP);
            LinkedHashMap<String, String> lhmParams = new LinkedHashMap<String, String>();
            lhmParams.put("dn", sDN);
            lhmParams.put("scope", "0");
            lhmParams.put("filter", "objectclass=*");

            XMLHelper.createXML(eMethod.getOwnerDocument(), eMethod, lhmParams);

            // Send the request.
            Node nResponse = sendAndWait(getEnvelope(eMethod));

            // Now convert the response into a LDAP Entry object.
            Node[] anResponses = XMLHelper.getMethodResponses(nResponse, "res");

            for (int iCount = 0; iCount < anResponses.length; iCount++)
            {
                Node nMethod = anResponses[iCount];
                NodeList nlEntries = XMLHelper.getNodeList(nMethod, ".//:tuple/:old/:entry");

                if (nlEntries.getLength() > 0)
                {
                    leReturn = LDAPDomXmlUtil.getEntryFromXML((Element) nlEntries.item(0));
                }
            }
        }
        catch (Exception e)
        {
            throw new CordysConnectionException(CordysConnectionException.EC_LDAP,
                                                "Error reading entry " + sDN + " from LDAP.", e);
        }
        return leReturn;
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
     * @throws  CordysConnectionException  DOCUMENTME
     *
     * @see     com.cordys.coe.util.connection.ICordysConnection#searchLDAP(java.lang.String, int,
     *          java.lang.String)
     */
    public LDAPEntry[] searchLDAP(String sSearchRoot, int iLDAPScope, String sFilter)
                           throws CordysConnectionException
    {
        LDAPEntry[] aleReturn = null;

        try
        {
            Element eMethod = m_cgcGateway.createMessage(MTD_SEARCH_LDAP, NSP_LDAP);
            LinkedHashMap<String, String> lhmParams = new LinkedHashMap<String, String>();
            lhmParams.put("dn", sSearchRoot);
            lhmParams.put("scope", String.valueOf(iLDAPScope));
            lhmParams.put("filter", sFilter);

            XMLHelper.createXML(eMethod.getOwnerDocument(), eMethod, lhmParams);

            // Send the request.
            Node nResponse = sendAndWait(getEnvelope(eMethod));

            // Now convert the response into a LDAP Entry object.
            Node[] anResponses = XMLHelper.getMethodResponses(nResponse, "res");
            ArrayList<LDAPEntry> alNodes = new ArrayList<LDAPEntry>();

            for (int iCount = 0; iCount < anResponses.length; iCount++)
            {
                Node nMethod = anResponses[iCount];
                NodeList nlEntries = XMLHelper.getNodeList(nMethod, "./:tuple/:old/:entry");

                for (int iEntryCount = 0; iEntryCount < nlEntries.getLength(); iEntryCount++)
                {
                    LDAPEntry leEntry = LDAPDomXmlUtil.getEntryFromXML((Element) nlEntries.item(iEntryCount));
                    alNodes.add(leEntry);
                }
            }

            aleReturn = new LDAPEntry[alNodes.size()];
            alNodes.toArray(aleReturn);
        }
        catch (Exception e)
        {
            throw new CordysConnectionException(CordysConnectionException.EC_LDAP,
                                                "Error searching entry " + sSearchRoot +
                                                " with filter " + sFilter + " from LDAP.", e);
        }
        return aleReturn;
    }

    /**
     * This method sends the message to Cordys with the specified timeout. used. If any was found, a
     * SOAPException is thrown. The received response-envelope will be added to the global arraylist
     * with XML nodes. Cleaning up all nodes can be done by calling the freeXMLNodes() method.
     *
     * @param   nNode        The XML node to send. It doesn't have to be the envelope tag. The
     *                       method will look in the parents of this node to find the envelope.
     * @param   lTimeOut     The timeout to use for sending.
     * @param   bCheckFault  Whether or not to check the response for SOAP:faults.
     *
     * @return  The response-envelope.
     *
     * @throws  CordysConnectionException  DOCUMENTME
     *
     * @see     com.cordys.coe.util.connection.ICordysConnection#sendAndWait(org.w3c.dom.Node, long,
     *          boolean)
     */
    public Node sendAndWait(Node nNode, long lTimeOut, boolean bCheckFault)
                     throws CordysConnectionException
    {
        Node nReturn = null;

        Node nEnvelope = getEnvelope(nNode);

        try
        {
            nReturn = m_cgcGateway.requestFromCordys((Element) nEnvelope);
        }
        catch (Exception e)
        {
            throw new CordysConnectionException(CordysConnectionException.EC_SENDING,
                                                "Error suring request.", e);
        }

        return nReturn;
    }

    /**
     * This method sets the current organization.
     *
     * @param  sOrganization  The current organization.
     *
     * @see    com.cordys.coe.util.connection.impl.AbstractConnection#setOrganization(java.lang.String)
     */
    @Override
    public void setOrganization(String sOrganization)
    {
        super.setOrganization(sOrganization);

        m_cgcGateway.setOrganization(sOrganization);
    }

    /**
     * This method creates the base envelope.
     *
     * @return  The pointer to the SOAP body.
     *
     * @throws  CordysConnectionException  DOCUMENTME
     */
    private Element createBaseSoapEnvelope()
                                    throws CordysConnectionException
    {
        Document dDoc;

        try
        {
            dDoc = XMLHelper.createDocumentFromStream(new ByteArrayInputStream(BASE_SOAP_REQUEST));
        }
        catch (XMLWrapperException e)
        {
            // This shouldn't happen
            throw new CordysConnectionException(CordysConnectionException.EC_CREATE_SOAPMESSAGE,
                                                "Error parsing the base XML.");
        }

        return (Element) dDoc.getDocumentElement().getFirstChild();
    }
}
