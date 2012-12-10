package com.cordys.coe.util.cgc.nom;

import com.cordys.coe.util.StringUtils;
import com.cordys.coe.util.cgc.CordysGatewayClientBase;
import com.cordys.coe.util.cgc.CordysGatewayClientException;
import com.cordys.coe.util.cgc.CordysSOAPException;
import com.cordys.coe.util.cgc.config.IAuthenticationConfiguration;
import com.cordys.coe.util.cgc.config.ICGCConfiguration;
import com.cordys.coe.util.cgc.message.CGCMessages;
import com.cordys.coe.util.exceptions.XMLWrapperException;
import com.cordys.coe.util.xml.NamespaceDefinitions;
import com.cordys.coe.util.xml.nom.XPathHelper;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Node;
import com.eibus.xml.nom.XMLException;
import com.eibus.xml.xpath.XPathMetaInfo;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * This class can be used to communicate with the Cordys Web Gateway. It supports 3 types of authentication: - Basic - NTLM -
 * Certificates. This class is thread safe. This means that multiple threads can use the same instance of this object to call
 * methods on the Cordys server. If you need to connect under multiple users you need to make an instance per user.<br>
 * Example code for NTLM: <code>String sUser = "pgussow"; String sPassword = "password"; String sServer =
 * "srv-nl-ces20"; String sDomain = "NTDOM"; int iPort = 80; ICordysGatewayClient cgc = new CordysGatewayClient(sUser,
 * sPassword, sServer, iPort, sDomain); cgc.connect();</code>
 */
public class CordysNomGatewayClient extends CordysGatewayClientBase implements ICordysNomGatewayClient
{
    /** Holds the logger to use for this class. */
    private static final Logger LOG = Logger.getLogger(CordysNomGatewayClient.class);
    /** A shared NOM document for all instances. */
    private static final Document dNomDoc = new Document();
    /** Holds the namespace prefix for the SOAP namespace. */
    private static final String PRE_SOAP = NamespaceDefinitions.PREFIX_SOAP_1_1;
    /** Holds the user details. */
    protected int m_xLogonInfo;
    /** Holds the namespace prefixes used in this class. */
    private static XPathMetaInfo m_xmi = new XPathMetaInfo();

    static
    {
        m_xmi.addNamespaceBinding("soap", NamespaceDefinitions.XMLNS_SOAP_1_1);
        m_xmi.addNamespaceBinding("ldap", "http://schemas.cordys.com/1.1/ldap");
    }

    /**
     * Constructor. Creates the Cordys Gateway Client for a certificate.
     * 
     * @param acAuthenticationDetails The authentication details.
     * @param ccConfiguration The configuration for the gateway.
     * @throws CordysGatewayClientException In case of any exceptions.
     */
    public CordysNomGatewayClient(IAuthenticationConfiguration acAuthenticationDetails, ICGCConfiguration ccConfiguration)
            throws CordysGatewayClientException
    {
        super(acAuthenticationDetails, ccConfiguration);
    }

    /**
     * This method creates a SOAP message with the given name and namespace.
     * 
     * @param xRequest The SOAP:Envelope to add it to.
     * @param sMethod The name of the method.
     * @param sNamespace The namespace of the method.
     * @return The NOM node of the method. To get the root element of the message call Node.getRoot()
     * @throws CordysGatewayClientException In case of any exceptions.
     */
    public int addMethod(int xRequest, String sMethod, String sNamespace) throws CordysGatewayClientException
    {
        int xReturn = 0;

        // First find the SOAP:Body
        int xBody = XPathHelper.selectSingleNode(xRequest, "//soap:Body", m_xmi);

        if (xBody != 0)
        {
            xReturn = Node.createElement(sMethod, xBody);
            Node.setAttribute(xReturn, "xmlns", sNamespace);
        }

        return xReturn;
    }

    /**
     * This method creates a SOAP message with the given name and namespace.
     * 
     * @param sMethodName The name of the method.
     * @param sNamespace The namespace of the method.
     * @return The NOM node of the method. To get the root element of the message call Node.getRoot()
     * @throws CordysGatewayClientException DOCUMENTME
     */
    public int createMessage(String sMethodName, String sNamespace) throws CordysGatewayClientException
    {
        int xReturn = 0;
        int xEnvelope = 0;

        // Parse the base request.
        try
        {
            xEnvelope = parseXML(BASE_SOAP_REQUEST);

            // Create the method element.
            xReturn = dNomDoc.createElement(sMethodName);
            Node.setAttribute(xReturn, "xmlns", sNamespace);

            // Find the SOAP:Body and append the method node.
            int xNode = XPathHelper.selectSingleNode(xEnvelope, "//soap:Body", m_xmi);

            if (xNode == 0)
            {
                Node.delete(xReturn);
                Node.delete(xEnvelope);
                throw new CordysGatewayClientException(CGCMessages.CGC_ERROR_NO_BODY_FOUND);
            }

            Node.appendToChildren(xReturn, xNode);
        }
        catch (CordysGatewayClientException cgce)
        {
            throw cgce;
        }
        catch (Exception e)
        {
            throw new CordysGatewayClientException(e, CGCMessages.CGC_ERROR_CREATE_MESSAGE, sNamespace, sMethodName);
        }

        return xReturn;
    }

    /**
     * Disconnect from the cordys gateway. Free all cordys resources
     */
    @Override
    public void disconnect()
    {
        super.disconnect();

        if (m_xLogonInfo != 0)
        {
            Node.delete(m_xLogonInfo);
            m_xLogonInfo = 0;
        }
    }

    /**
     * This method returns the DN of the authenticated user.
     * 
     * @return The DN of the authenticated user.
     */
    public String getAuthUserDN()
    {
        int xNode = XPathHelper.selectSingleNode(m_xLogonInfo, "//ldap:tuple/ldap:old/ldap:user/ldap:authuserdn", m_xmi);

        if (xNode == 0)
        {
            return null;
        }

        return Node.getData(xNode);
    }

    /**
     * This method gets the Logger for this class.
     * 
     * @return The Logger for this class.
     */
    @Override
    public Logger getLogger()
    {
        return LOG;
    }

    /**
     * This method sends the request to Cordys. The response is put back into an XML structure. The tag returned is the NOM node
     * of the SOAP:Envelope.
     * 
     * @param xRequest The request envelope NOM node.
     * @return The response NOM node.
     * @throws CordysGatewayClientException In case of any exception.
     * @throws CordysSOAPException In case of a SOAP fault.
     */
    public int requestFromCordys(int xRequest) throws CordysGatewayClientException, CordysSOAPException
    {
        return requestFromCordys(xRequest, getConfiguration().getTimeout());
    }

    /**
     * This method sends the request to Cordys. The response is put back into an XML structure. The tag returned is the NOM node
     * of the SOAP:Envelope.
     * 
     * @param xRequest The request envelope NOM node.
     * @param lTimeout The timeout to use.
     * @return The response NOM node.
     * @throws CordysGatewayClientException In case of any exception.
     * @throws CordysSOAPException In case of a SOAP fault.
     */
    public int requestFromCordys(int xRequest, long lTimeout) throws CordysGatewayClientException, CordysSOAPException
    {
        return requestFromCordys(xRequest, lTimeout, true, null);
    }

    /**
     * This method sends the request to Cordys and returns the response of the method.
     * 
     * @param aInputSoapRequest The input request.
     * @param lTimeout The timeout to use.
     * @return The response of the request.
     * @throws CordysGatewayClientException In case of any exception.
     */
    public String requestFromCordys(String aInputSoapRequest, long lTimeout) throws CordysGatewayClientException
    {
        return requestFromCordys(aInputSoapRequest, lTimeout, true, null, getGatewayURL(), m_sOrganization, null);
    }

    /**
     * This method sends the request to Cordys. The response is put back into an XML structure. The tag returned is the NOM node
     * of the SOAP:Envelope.
     * 
     * @param xRequest The request envelope NOM node.
     * @param lTimeout The timeout to use.
     * @param sSoapAction SOAP action to be set in the request.
     * @return The response NOM node.
     * @throws CordysGatewayClientException In case of any exception.
     * @throws CordysSOAPException In case of a SOAP fault.
     */
    public int requestFromCordys(int xRequest, long lTimeout, String sSoapAction) throws CordysGatewayClientException,
            CordysSOAPException
    {
        return requestFromCordys(xRequest, lTimeout, true, sSoapAction);
    }

    /**
     * This method sends the request to Cordys. The response is put back into an XML structure. The tag returned is the pointer to
     * the SOAP:Envelope. The resulting methods will ge a prefix 'res'. So if you want to use an XPath on the result use:
     * '//res:tuple' to get all the tuples. This method will not wait if the serverwatcher indicates the server is down.
     * 
     * @param xRequest The request envelope NOM node.
     * @return The response NOM node.
     * @throws CordysGatewayClientException In case of any exception.
     * @throws CordysSOAPException In case of a SOAP fault.
     */
    public int requestFromCordysNoBlocking(int xRequest) throws CordysGatewayClientException, CordysSOAPException
    {
        return requestFromCordysNoBlocking(xRequest, getConfiguration().getTimeout());
    }

    /**
     * This method sends the request to Cordys. The response is put back into an XML structure. The tag returned is the pointer to
     * the SOAP:Envelope. The resulting methods will ge a prefix 'res'. So if you want to use an XPath on the result use:
     * '//res:tuple' to get all the tuples. This method will not wait if the serverwatcher indicates the server is down.
     * 
     * @param xRequest The request envelope NOM node.
     * @param lTimeout The timeout to use.
     * @return The response NOM node.
     * @throws CordysGatewayClientException In case of any exception.
     * @throws CordysSOAPException In case of a SOAP fault.
     */
    public int requestFromCordysNoBlocking(int xRequest, long lTimeout) throws CordysGatewayClientException, CordysSOAPException
    {
        return requestFromCordys(xRequest, lTimeout, false, null);
    }

    /**
     * This method is called when the HTTP response code is set to 500. All servers that are basic-profile compliant will return
     * error code 500 in case of a SOAP fault. The base structure is:<br>
     * If the response was not valid XML this method will do nothing and expect the calling method to throw an HTTPException.
     * 
     * @param sHTTPResponse The response from the web server.
     * @param sRequestXML The request XML (used for filling the exception object with enough information).
     * @throws CordysSOAPException In case of a SOAP fault.
     * @see com.cordys.coe.util.cgc.CordysGatewayClientBase#checkForAndThrowCordysSOAPException(java.lang.String, java.lang.String)
     */
    @Override
    protected void checkForAndThrowCordysSOAPException(String sHTTPResponse, String sRequestXML) throws CordysSOAPException
    {
        int iNode = 0;

        try
        {
            iNode = dNomDoc.parseString(sHTTPResponse);
        }
        catch (Exception e)
        {
            LOG.error("Error parsing the XML", e);
        }

        if (iNode != 0)
        {
            // Figure out if we can find the SOAP:Fault structure.
            // Note: This code is for backwards compatibility. Because C3 will return code 500
            // in case of a SOAP fault.
            int iSoapFault = XPathHelper.selectSingleNode(iNode, "//" + PRE_SOAP + ":Fault");

            if (iSoapFault != 0)
            {
                if (getLogger().isDebugEnabled())
                {
                    getLogger().debug("Found a SOAP fault:\n" + Node.writeToString(iSoapFault, false));
                }

                // Create the SoapException object.
                CordysSOAPException cse = NOMCordysSOAPException.parseSOAPFault(iSoapFault, sRequestXML);

                throw cse;
            }
        }
    }

    /**
     * Sends a login message to the gateway.
     * 
     * @throws CordysGatewayClientException In case of any exception.
     */
    @Override
    protected void sendLoginMessage() throws CordysGatewayClientException
    {
        // need to send a message to see if the configuration is OK
        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("Sending logon request to the Cordys server (" + getConfiguration().getHost() + ")");
        }

        int xRequest = 0;
        int xResponse = 0;

        try
        {
            xRequest = parseXML(XML_GET_USER_DETAILS);
            xResponse = requestFromCordys(xRequest);

            int xLogonTmp;

            xLogonTmp = XPathHelper.selectSingleNode(m_xLogonInfo, "/soap:Envelope/soap:Body", m_xmi);

            if (xLogonTmp == 0)
            {
                throw new CordysGatewayClientException(CGCMessages.CGC_ERROR_NOM_XML, "<SOAP:Body>", Node.writeToString(
                        xResponse, true), Node.writeToString(xRequest, true));
            }

            xLogonTmp = Node.getFirstElement(xLogonTmp);

            if (m_xLogonInfo != 0)
            {
                Node.delete(m_xLogonInfo);
            }

            m_xLogonInfo = Node.unlink(xLogonTmp);

            int xTuple = XPathHelper.selectSingleNode(m_xLogonInfo, ".//ldap:tuple", m_xmi);

            if (xTuple == 0)
            {
                throw new CordysGatewayClientException(CGCMessages.CGC_ERROR_NOM_XML, "<tuple>", Node.writeToString(xResponse,
                        true), Node.writeToString(xRequest, true));
            }

            // If there is a server watcher make sure it know the cgc to use
            if (m_swWatcher != null)
            {
                // TODO: Implement this.
                // m_swWatcher.setCordysGatewayClient(this);
            }
        }
        catch (CordysGatewayClientException cgce)
        {
            throw cgce;
        }
        catch (Exception e)
        {
            throw new CordysGatewayClientException(CGCMessages.CGC_ERROR_LOGIN_FAILED);
        }
        finally
        {
            if (xRequest != 0)
            {
                Node.delete(xRequest);
                xRequest = 0;
            }

            if (xResponse != 0)
            {
                Node.delete(xResponse);
                xResponse = 0;
            }
        }
    }

    /**
     * Returns the SOAP namespace prefix from the SOAP:Envelope. The prefix is returned as "SOAP:" or if the prefix is empty, an
     * empty string is returned.
     * 
     * @param iSoapEnvelopeNode SOAP envelope XML node.
     * @return The namespace prefix.
     */
    private static String getSoapPrefix(int iSoapEnvelopeNode)
    {
        int iCount = Node.getNumAttributes(iSoapEnvelopeNode);

        for (int i = 0; i <= iCount; i++)
        {
            String sPrefix = Node.getAttributePrefix(iSoapEnvelopeNode, i);

            if ((sPrefix == null) || !sPrefix.equals("xmlns"))
            {
                continue;
            }

            String sName = Node.getAttributeName(iSoapEnvelopeNode, i);

            if (sName == null)
            {
                continue;
            }

            String sValue = Node.getAttribute(iSoapEnvelopeNode, sName);

            if (sValue.toLowerCase().startsWith("http://schemas.xmlsoap.org/"))
            {
                String sRes = Node.getAttributeLocalName(iSoapEnvelopeNode, i);

                return ((sRes != null) && (sRes.length() > 0)) ? (sRes + ":") : "";
            }
        }

        // Return the default.
        return "SOAP:";
    }

    /**
     * This method returns a new instance of the exception object. It parses the XML and initializes the object.
     * 
     * @param xFault The actual soap fault NOM node.
     * @param xRequestEnvelope The root NOM node of the request that caused this fault.
     * @return A new exception object representing the exception.
     */
    private static CordysSOAPException parseSOAPFault(int xFault, int xRequestEnvelope)
    {
        CordysSOAPException cseReturn = null;

        if (xFault == 0)
        {
            throw new IllegalArgumentException("The XML of the SOAP fault must be provided.");
        }

        String sSoapPrefix = getSoapPrefix(xFault);
        String sCode = null;
        String sString = null;
        String sDetailedMessage = null;

        // A SOAP fault has occurred, so we need to throw it.
        // Get the fault code.
        sCode = Node.getDataElement(xFault, sSoapPrefix + "faultcode", "");
        sString = Node.getDataElement(xFault, sSoapPrefix + "faultstring", "");
        sCode = Node.getDataElement(xFault, sSoapPrefix + "faultcode", "");

        // Get the deailted message.
        int xDetail = 0;

        xDetail = XPathHelper.selectSingleNode(xFault, ".//soap:detail>", m_xmi);

        if (xDetail != 0)
        {
            StringBuffer sbDetail = new StringBuffer("");

            // Write the details.
            int xExcChild = Node.getFirstChild(xDetail);

            while (xExcChild != 0)
            {
                sbDetail.append(Node.writeToString(xExcChild, true));
                sbDetail.append("\n");

                xExcChild = Node.getNextSibling(xExcChild);
            }

            sDetailedMessage = sbDetail.toString();
        }

        // Write the original XML.
        String sExceptionXML = Node.writeToString(xFault, true);

        // Write the request XML to a string.
        String sRequestXML = null;

        if (xRequestEnvelope != 0)
        {
            sRequestXML = Node.writeToString(xRequestEnvelope, true);
            ;
        }

        // Create the exception object.
        cseReturn = new CordysSOAPException(sCode, sString, sDetailedMessage, sExceptionXML, sRequestXML);

        return cseReturn;
    }

    /**
     * This method returns a new document with the byte [] parsed.
     * 
     * @param baXML the actual XML.
     * @return The document with the parsed XML.
     * @throws XMLWrapperException DOCUMENTME
     */
    private int parseXML(byte[] baXML) throws XMLWrapperException
    {
        try
        {
            return dNomDoc.load(baXML);
        }
        catch (XMLException e)
        {
            throw new XMLWrapperException(e);
        }
    }

    /**
     * This method sends the request to Cordys. The response is put back into an XML structure. The tag returned is the NOM node
     * of the SOAP:Envelope. The resulting methods will ge a prefix 'res'. So if you want to use an XPath on the result use:
     * '//res:tuple' to get all the tuples.
     * 
     * @param xRequest The request envelope NOM node.
     * @param lTimeout The timeout to use.
     * @param bBlockIfServerIsDown If this is true then the call will block indefinately untill the server is back online.
     * @param sSoapAction SOAP action to be set in the request.
     * @return The response NOM node.
     * @throws CordysGatewayClientException DOCUMENTME
     * @throws CordysSOAPException DOCUMENTME
     */
    private int requestFromCordys(int xRequest, long lTimeout, boolean bBlockIfServerIsDown, String sSoapAction)
            throws CordysGatewayClientException, CordysSOAPException
    {
        int xReturn = 0;

        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("Request:\n" + Node.writeToString(xRequest, false));
        }

        Map<String, String> mExtraHeaders = new HashMap<String, String>();

        if (sSoapAction != null)
        {
            mExtraHeaders.put(SOAP_ACTION_HEADER, sSoapAction);
        }

        String responseContent = requestFromCordys(Node.writeToString(xRequest, false), lTimeout, bBlockIfServerIsDown,
                mExtraHeaders, getGatewayURL(), m_sOrganization, null);
        boolean bRequestOk = false;

        try
        {
            try
            {
                if (!StringUtils.isSet(responseContent))
                {
                    throw new CordysGatewayClientException(CGCMessages.CGC_ERROR_EMPTY_BODY);
                }

                xReturn = dNomDoc.parseString(responseContent);
                bRequestOk = true;
            }
            catch (Exception e)
            {
                throw new CordysGatewayClientException(e, CGCMessages.CGC_ERROR_PARSE_RESPONSE);
            }

            if (isCheckingForFaults())
            {
                // Parse for SOAP faults.
                int xFault = XPathHelper.selectSingleNode(xReturn, "/soap:Envelope/soap:Body/soap:Fault");

                if (xFault != 0)
                {
                    throw parseSOAPFault(xFault, xRequest);
                }
            }
        }
        finally
        {
            if (!bRequestOk)
            {
                if (xReturn != 0)
                {
                    Node.delete(xReturn);
                    xReturn = 0;
                }
            }
        }

        return xReturn;
    }
}
