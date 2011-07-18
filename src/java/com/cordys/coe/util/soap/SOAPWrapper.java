package com.cordys.coe.util.soap;

import com.cordys.coe.util.general.Util;
import com.cordys.coe.util.xml.NamespaceDefinitions;
import com.cordys.coe.util.xml.nom.NamespaceConstants;
import com.cordys.coe.util.xml.nom.XPathHelper;

import com.eibus.connector.nom.Connector;

import com.eibus.util.spy.Spy;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Find;
import com.eibus.xml.nom.Node;

import com.novell.ldap.LDAPException;

import java.util.ArrayList;

/**
 * This class is a wrapper around creation and sending soap-messages. All requests and responses are added to an
 * arraylist. To clean up the memory allocated for the soapmessages call the freeXMLNodes() method.
 *
 * @author  pgussow
 */
public class SOAPWrapper
    implements ISOAPWrapper
{
    /**
     * Holds the name of the default connector that will be used.
     */
    private static final String DEFAULT_CONNECTOR_NAME = "Anonymous Connector";
    /**
     * This arraylist holds all nodes that are created through this class.
     */
    private ArrayList<Integer> alNodes;
    /**
     * This boolean indicates whether or not to collect XML-nodes in the global ArrayList.
     */
    private boolean bCollect = true;
    /**
     * Holds the connector to use.
     */
    private Connector cConnector;
    /**
     * Holds the timeout to use for sending messages.
     */
    private long lTimeOut = 30000L;
    /**
     * Holds the organization to use for creating and sending soap messages.
     */
    private String m_sOrganization = null;
    /**
     * Holds the user to use for sending requests.
     */
    private String sDefaultUser;

    /**
     * Creates a new SOAPWrapper object. It opens a connector with the name "Anonymous Connector".
     *
     * @throws  SOAPException  In case of any exceptions.
     */
    public SOAPWrapper()
                throws SOAPException
    {
        Connector cCon = null;

        try
        {
            cCon = Connector.getInstance(DEFAULT_CONNECTOR_NAME);

            if (!cCon.isOpen())
            {
                cCon.open();
            }
        }
        catch (Exception e)
        {
            throw new SOAPException("Error opening the connector: " + e);
        }

        cConnector = cCon;
        alNodes = new ArrayList<Integer>();
    }

    /**
     * Creates a new SOAPWrapper object.
     *
     * @param  cConnector  The connector to use.
     */
    public SOAPWrapper(Connector cConnector)
    {
        this.cConnector = cConnector;
        alNodes = new ArrayList<Integer>();
    }

    /**
     * This method returns the SOAP:Body- of the passed on message.
     *
     * @param   iXMLNode  The node to find the body of.
     *
     * @return  The body.
     */
    public static final int getBody(int iXMLNode)
    {
        int iRoot = Node.getRoot(iXMLNode);

        int iReturn = XPathHelper.selectSingleNode(iRoot,
                                                   "./" +
                                                   NamespaceConstants.getPrefix(NamespaceDefinitions.XMLNS_SOAP_1_1) +
                                                   ":Body");

        return iReturn;
    }

    /**
     * This method returns the SOAP:Envelope-parent of the passed on node.
     *
     * @param   iXMLNode  The node to find the envelope of.
     *
     * @return  The Envelope.
     */
    public static final int getEnvelope(int iXMLNode)
    {
        int iReturn = Node.getRoot(iXMLNode);

        String sName = Node.getLocalName(iReturn);

        if (!sName.equals("Envelope"))
        {
            iReturn = 0;
        }

        return iReturn;
    }

    /**
     * This method gets the user from a certain request.
     *
     * @param   iEnvelope  Any node, part of the SOAP message.
     *
     * @return  The dn of the user that sent the request.
     */
    public static String getRequestUser(int iEnvelope)
    {
        String sReturn = "";

        int iRealEnvelope = getEnvelope(iEnvelope);

        if (iRealEnvelope != 0)
        {
            int iUserNode = XPathHelper.selectSingleNode(iRealEnvelope,
                                                         ".//" +
                                                         NamespaceConstants.getPrefix(NamespaceDefinitions.XMLNS_SOAP_1_1) +
                                                         ":Header/header/sender/user");

            if (iUserNode != 0)
            {
                sReturn = Node.getDataWithDefault(iUserNode, "");
            }
        }

        return sReturn;
    }

    /**
     * This method sets the user for the passed on request.
     *
     * @param  iSOAPEnvelope  The request for which the user needs to be set.
     * @param  sUser          The user that should be used.
     */
    public static void setRequestUser(int iSOAPEnvelope, String sUser)
    {
        int iNewHeader = XPathHelper.selectSingleNode(iSOAPEnvelope,
                                                      ".//" +
                                                      NamespaceConstants.getPrefix(NamespaceDefinitions.XMLNS_SOAP_1_1) +
                                                      ":Header/header/sender");

        if (iNewHeader != 0)
        {
            int iTmp = Find.firstMatch(iNewHeader, "?<user>");

            if (iTmp != 0)
            {
                Node.unlink(iTmp);
                Node.delete(iTmp);
            }
            Node.getDocument(iNewHeader).createTextElement("user", sUser, iNewHeader);
        }
    }

    /**
     * @see  com.cordys.coe.util.soap.ISOAPWrapper#addMethod(int, java.lang.String, java.lang.String)
     */
    @Override public int addMethod(int iMethod, String sName, String sNamespace)
                            throws SOAPException
    {
        int iReturn = 0;

        if (iMethod == 0)
        {
            iReturn = createSoapMethod(sName, sNamespace);
        }
        else
        {
            int iBody = SOAPWrapper.getBody(iMethod);
            Document dDoc = Node.getDocument(iBody);

            iReturn = dDoc.createElement(sName, iBody);
            Node.setAttribute(iReturn, "xmlns", sNamespace);
        }

        return iReturn;
    }

    /**
     * @see  com.cordys.coe.util.soap.ISOAPWrapper#addXMLGarbage(int)
     */
    @Override public void addXMLGarbage(int iXMLNode)
    {
        if ((iXMLNode != 0) && (bCollect == true))
        {
            alNodes.add(new Integer(iXMLNode));
        }
    }

    /**
     * @see  com.cordys.coe.util.soap.ISOAPWrapper#createSoapMethod(java.lang.String, java.lang.String)
     */
    @Override public int createSoapMethod(String sName, String sNamespace)
                                   throws SOAPException
    {
        return createSoapMethod(null, null, sName, sNamespace, sDefaultUser);
    }

    /**
     * @see  com.cordys.coe.util.soap.ISOAPWrapper#createSoapMethod(java.lang.String, java.lang.String,
     *       java.lang.String)
     */
    @Override public int createSoapMethod(String sReceiver, String sName, String sNamespace)
                                   throws SOAPException
    {
        return createSoapMethod(sReceiver, null, sName, sNamespace, sDefaultUser);
    }

    /**
     * @see  com.cordys.coe.util.soap.ISOAPWrapper#createSoapMethod(java.lang.String, java.lang.String,
     *       java.lang.String, java.lang.String)
     */
    @Override public int createSoapMethod(String sReceiver, String sName, String sNamespace, String sUser)
                                   throws SOAPException
    {
        return createSoapMethod(sReceiver, null, sName, sNamespace, sUser);
    }

    /**
     * @see  com.cordys.coe.util.soap.ISOAPWrapper#createSoapMethod(java.lang.String, java.lang.String,
     *       java.lang.String, java.lang.String, java.lang.String)
     */
    @Override public int createSoapMethod(String sReceiver, String sUri, String sName, String sNamespace, String sUser)
                                   throws SOAPException
    {
        int iReturn = 0;

        try
        {
            // If the receiver is null we need to find the proper SOAP node to send the
            // request to. We will first find out the organization to use.
            if (sReceiver == null)
            {
                String sActualOrg = null;

                // If the organization is set we will use this one.
                if (m_sOrganization != null)
                {
                    sActualOrg = m_sOrganization;
                }

                // If the user is passed on we'll need to use the organization from
                // the request user.
                if (sUser != null)
                {
                    sActualOrg = Util.getOrganizationFromUser(sUser);
                }

                // Now we have the organization, so let's find the receiver.
                sReceiver = cConnector.getMiddleware().getDirectory().findSOAPNode(sActualOrg, sNamespace, sName);
            }

            if ((sReceiver != null) && (sReceiver.length() > 0))
            {
                String sTmpURI = null;

                if ((sUri != null) && (sUri.length() > 0))
                {
                    sTmpURI = sUri;
                }
                iReturn = cConnector.createSOAPMessage(sReceiver, sTmpURI);

                Document dDoc = Node.getDocument(iReturn);

                iReturn = dDoc.createElement(sName, iReturn);
                Node.setAttribute(iReturn, "xmlns", sNamespace);
            }
            else
            {
                iReturn = cConnector.createSOAPMethod(sNamespace, sName);
            }

            // Make sure the node is cleaned up.
            addXMLGarbage(SOAPWrapper.getEnvelope(iReturn));
        }
        catch (LDAPException e)
        {
            StringBuffer sbBuffer = new StringBuffer("Could not find a soapnode for method <");
            sbBuffer.append(sName);
            sbBuffer.append(" \"");
            sbBuffer.append(sNamespace);
            sbBuffer.append("\"/>");
            throw new SOAPException(e, sbBuffer.toString());
        }

        // Now the method-node has been created. Now we're going to replace the organizational user
        // in the document.
        int iEnvelope = SOAPWrapper.getEnvelope(iReturn);

        if ((iEnvelope != 0) && (sUser != null) && (sUser.length() > 0))
        {
            SOAPWrapper.setRequestUser(iEnvelope, sUser);
        }
        return iReturn;
    }

    /**
     * @see  com.cordys.coe.util.soap.ISOAPWrapper#freeXMLNodes()
     */
    @Override public void freeXMLNodes()
    {
        if (alNodes != null)
        {
            while (alNodes.size() > 0)
            {
                Object oObject = alNodes.remove(alNodes.size() - 1);
                Node.delete(((Integer) oObject).intValue());
            }
        }
    }

    /**
     * @see  com.cordys.coe.util.soap.ISOAPWrapper#getConnector()
     */
    @Override public Connector getConnector()
    {
        return cConnector;
    }

    /**
     * This method returns the document of the connector that is being used.
     *
     * @return      The document of the connector that is being used.
     *
     * @deprecated  since Cordys C1. This method uses the Connector.getDocument() method which is deprecated since
     *              Cordys C1
     */
    public Document getDocument()
    {
        return cConnector.getDocument();
    }

    /**
     * @see  com.cordys.coe.util.soap.ISOAPWrapper#getOrganization()
     */
    @Override public String getOrganization()
    {
        return m_sOrganization;
    }

    /**
     * @see  com.cordys.coe.util.soap.ISOAPWrapper#getTimeOut()
     */
    @Override public long getTimeOut()
    {
        return lTimeOut;
    }

    /**
     * @see  com.cordys.coe.util.soap.ISOAPWrapper#getUser()
     */
    @Override public String getUser()
    {
        return sDefaultUser;
    }

    /**
     * @see  com.cordys.coe.util.soap.ISOAPWrapper#isCollecting()
     */
    @Override public boolean isCollecting()
    {
        return bCollect;
    }

    /**
     * @see  com.cordys.coe.util.soap.ISOAPWrapper#sendAndForget(int)
     */
    @Override public void sendAndForget(int iNode)
                                 throws SOAPException
    {
        int iRealEnvelope = iNode;

        // Make sure that we send the Envelope-tag. Otherwise the messageid won't be set properly.
        iRealEnvelope = SOAPWrapper.getEnvelope(iNode);

        try
        {
            cConnector.send(iRealEnvelope);
        }
        catch (Exception e)
        {
            throw new SOAPException(e,
                                    "Error sending the request to WCP. Original request:\n" +
                                    Node.writeToString(iNode, true));
        }
    }

    /**
     * @see  com.cordys.coe.util.soap.ISOAPWrapper#sendAndWait(int)
     */
    @Override public int sendAndWait(int iNode)
                              throws SOAPException
    {
        return sendAndWait(iNode, lTimeOut, true);
    }

    /**
     * @see  com.cordys.coe.util.soap.ISOAPWrapper#sendAndWait(int, long)
     */
    @Override public int sendAndWait(int iNode, long lTimeOut)
                              throws SOAPException
    {
        return sendAndWait(iNode, lTimeOut, true);
    }

    /**
     * @see  com.cordys.coe.util.soap.ISOAPWrapper#sendAndWait(int, boolean)
     */
    @Override public int sendAndWait(int iNode, boolean bCheckFault)
                              throws SOAPException
    {
        return sendAndWait(iNode, lTimeOut, bCheckFault);
    }

    /**
     * @see  com.cordys.coe.util.soap.ISOAPWrapper#sendAndWait(int, long, boolean)
     */
    @Override public int sendAndWait(int iNode, long lTimeOut, boolean bCheckFault)
                              throws SOAPException
    {
        int iReturn = 0;
        int iRealEnvelope = iNode;

        // Make sure that we send the Envelope-tag. Otherwise the messageid won't be set properly.
        iRealEnvelope = SOAPWrapper.getEnvelope(iNode);

        try
        {
            if (iRealEnvelope == 0)
            {
                throw new SOAPException("Could not find the Envelope-tag in the message.");
            }

            // Send the actual message
            if (Spy.active)
            {
                log("Sending message:\n" + Node.writeToString(iRealEnvelope, true));
            }

            iReturn = cConnector.sendAndWait(iRealEnvelope, lTimeOut);

            if (Spy.active)
            {
                log("Response received: " + Node.writeToString(iReturn, true));
            }

            // Add the response to the XMLGarbagecollector
            addXMLGarbage(iReturn);
        }
        catch (Exception e)
        {
            // Find the methodname of the request.
            throw new SOAPException(e, "Error sending the request to Cordys.", iNode, 0);
        }

        // Check for SOAP:faults
        if (bCheckFault == true)
        {
            int iError = SoapFaultInfo.findSoapFaultNode(iReturn);

            if (iError != 0)
            {
                throw new SOAPException("Cordys returned an error:\n" + Node.writeToString(iError, true), iRealEnvelope,
                                        iReturn);
            }
        }
        return iReturn;
    }

    /**
     * @see  com.cordys.coe.util.soap.ISOAPWrapper#setCollecting(boolean)
     */
    @Override public void setCollecting(boolean bShouldCollect)
    {
        bCollect = bShouldCollect;
    }

    /**
     * @see  com.cordys.coe.util.soap.ISOAPWrapper#setConnector(com.eibus.connector.nom.Connector)
     */
    @Override public void setConnector(Connector cConnector)
    {
        this.cConnector = cConnector;
    }

    /**
     * @see  com.cordys.coe.util.soap.ISOAPWrapper#setOrganization(java.lang.String)
     */
    @Override public void setOrganization(String sOrganization)
    {
        m_sOrganization = sOrganization;
    }

    /**
     * @see  com.cordys.coe.util.soap.ISOAPWrapper#setTimeOut(long)
     */
    @Override public void setTimeOut(long lTimeOut)
    {
        this.lTimeOut = lTimeOut;
    }

    /**
     * @see  com.cordys.coe.util.soap.ISOAPWrapper#setUser(java.lang.String)
     */
    @Override public void setUser(String sUser)
    {
        sDefaultUser = sUser;
    }

    /**
     * This method creates the XML representation of the exception. The default values: - SOAP:faultcode:
     * Server.Internal - SOAP:faultstring: Server Error - SOAP:detail prefix: Server Error
     *
     * @param   tException  The exception that occured.
     *
     * @return  A XML node with the XML representation of the exception
     */
    protected int createSoapFault(Throwable tException)
    {
        return createSoapFault(tException, "Server.Internal", "Server Error", "Server Error.");
    }

    /**
     * This method creates the XML representation of the exception.
     *
     * @param   tException    The exception that occured.
     * @param   sCode         The code for the exception (SOAP:faultcode)
     * @param   sFaultString  The faultstring (SOAP:faultstring)
     * @param   sPrefix       The message that should be set before the details of the exception are appended.
     *
     * @return  A XML node with the XML representation of the exception
     */
    protected int createSoapFault(Throwable tException, String sCode, String sFaultString, String sPrefix)
    {
        Document dDoc = getDocument();

        int iReturn = dDoc.createElement("SOAP:Fault");
        dDoc.createTextElement("SOAP:faultcode", sCode, iReturn);
        dDoc.createTextElement("SOAP:faultstring", sFaultString, iReturn);

        // Get the stack-trace
        String sStackTrace = Util.getStackTrace(tException);

        dDoc.createTextElement("SOAP:detail", sPrefix + "\nJavaException:\n" + sStackTrace, iReturn);

        return iReturn;
    }

    /**
     * This method logs a message to System.out.
     *
     * @param  sMessage  The message to log
     */
    private void log(String sMessage)
    {
        if (Spy.isActive())
        {
            Spy.send("SOAP-WRAPPER", sMessage);
        }
    }
}
