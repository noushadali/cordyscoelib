package com.cordys.coe.util.connection.impl;

import com.cordys.coe.util.config.INativeConfiguration;
import com.cordys.coe.util.connection.CordysConnectionException;
import com.cordys.coe.util.connection.INativeConnection;
import com.cordys.coe.util.general.ldap.LDAPUtils;
import com.cordys.coe.util.xml.dom.NiceDOMWriter;
import com.cordys.coe.util.xml.dom.XMLHelper;
import com.cordys.coe.util.xml.dom.XPathHelper;

import com.eibus.bdf.soap.Envelope;

import com.eibus.connector.dom.Connector;
import com.eibus.connector.dom.SOAPMessage;

import com.eibus.directory.soap.DirectoryException;
import com.eibus.directory.soap.LDAPDirectory;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class wraps around a native connection to the Cordys framework.
 *
 * @author  pgussow
 */
public class NativeConnection extends AbstractConnection
    implements INativeConnection
{
    /**
     * Holds the connector to Cordys.
     */
    private Connector m_cConn;

    /**
     * Creates a new NativeConnection object.
     *
     * @param   ncConfig         The native configuration.
     * @param   bCheckSoapFault  Whether or not the connection should check for soap faults.
     *
     * @throws  CordysConnectionException  DOCUMENTME
     */
    public NativeConnection(INativeConfiguration ncConfig, boolean bCheckSoapFault)
                     throws CordysConnectionException
    {
        super(ncConfig, bCheckSoapFault);

        // Create the LDAP directory.
        try
        {
            new LDAPDirectory(ncConfig.getServername(), ncConfig.getPort(),
                              ncConfig.getLDAPUsername(), ncConfig.getLDAPPassword());
        }
        catch (DirectoryException e)
        {
            throw new CordysConnectionException(CordysConnectionException.EC_CREATION,
                                                "Error logging on to LDAP.", e);
        }

        // Now create the connector.
        try
        {
            m_cConn = Connector.getInstance(ncConfig.getName() + "-Connector");
        }
        catch (Exception e)
        {
            throw new CordysConnectionException(CordysConnectionException.EC_CREATION,
                                                "Error opening the connector.", e);
        }

        m_cConn.open();

        m_cConn.setTimeout(ncConfig.getTimeout());
    }

    /**
     * This method modifies the LDAP attribute.
     *
     * @param   leEntry                     The entry.
     * @param   laAttribute                 The attribute to modify
     * @param   iLDAPModificationAttribute  The action that should be done.
     *
     * @throws  CordysConnectionException  DOCUMENTME
     */
    public void changeLDAPAttibute(LDAPEntry leEntry, LDAPAttribute laAttribute,
                                   int iLDAPModificationAttribute)
                            throws CordysConnectionException
    {
        try
        {
            LDAPUtils.changeAttibute(m_cConn.getMiddleware().getDirectory().getConnection(),
                                     leEntry, laAttribute, iLDAPModificationAttribute);
        }
        catch (LDAPException e)
        {
            throw new CordysConnectionException(CordysConnectionException.EC_LDAP,
                                                "Error modifying entry " + leEntry.getDN() +
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
     */
    public void changeLDAPEntry(LDAPEntry leEntry, int iLDAPModification)
                         throws CordysConnectionException
    {
        try
        {
            LDAPUtils.changeLDAP(m_cConn.getMiddleware().getDirectory().getConnection(), leEntry,
                                 iLDAPModification);
        }
        catch (LDAPException e)
        {
            throw new CordysConnectionException(CordysConnectionException.EC_LDAP,
                                                "Error modifying entry " + leEntry.getDN() +
                                                " from LDAP.", e);
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
        SOAPMessage smTemp = null;

        try
        {
            if ((sReceiver != null) && (sReceiver.length() > 0))
            {
                String sTmpURI = null;

                if ((sUri != null) && (sUri.length() > 0))
                {
                    sTmpURI = sUri;
                }
                smTemp = m_cConn.createSOAPMessage(sReceiver, sTmpURI);

                Element eBody = smTemp.getEnvelope().getBody(true);

                nReturn = (Element) XMLHelper.createElement(sName, eBody);

                nReturn.setAttribute("xmlns", sNamespace);
            }
            else
            {
                smTemp = m_cConn.createSOAPMethod(sNamespace, sName);
                nReturn = smTemp.getEnvelope().getBody(true).getFirstBodyBlock();
            }
        }
        catch (LDAPException e)
        {
            StringBuffer sbBuffer = new StringBuffer("Could not find a soapnode for method <");
            sbBuffer.append(sName);
            sbBuffer.append(" \"");
            sbBuffer.append(sNamespace);
            sbBuffer.append("\"/>");
            throw new CordysConnectionException(CordysConnectionException.EC_CREATE_SOAPMESSAGE,
                                                "Error creating soap message", e);
        }

        // Now the method-node has been created. Now we're going to replace the organizational user
        // in the document.
        if ((smTemp != null) && (sUser != null) && (sUser.length() > 0))
        {
            Element eSender = smTemp.getEnvelope().getHeader(true).getEIBHeader(true).getSender(true);
            Element eUser = null;

            try
            {
                eUser = (Element) XPathHelper.selectSingleNode(eSender, "./user");
            }
            catch (TransformerException e)
            {
                throw new CordysConnectionException(CordysConnectionException.EC_CREATE_SOAPMESSAGE,
                                                    "Could not find the user tag.", e);
            }

            if (eUser != null)
            {
                eUser.getParentNode().removeChild(eUser);
            }
            XMLHelper.createTextElement("user", sUser, eSender);
        }

        // Make sure the SOAPMessage instance is saved.
        if (smTemp != null)
        {
            smTemp.getEnvelope().setUserData(smTemp);
        }

        return nReturn;
    }

    /**
     * This method deletes the given LDAp entry.
     *
     * @param   leEntry     The entry to delete.
     * @param   bRecursive  Whether or not to delete it recursively.
     *
     * @throws  CordysConnectionException  DOCUMENTME
     */
    public void deleteLDAPEntry(LDAPEntry leEntry, boolean bRecursive)
                         throws CordysConnectionException
    {
        try
        {
            LDAPUtils.deleteEntry(m_cConn.getMiddleware().getDirectory().getConnection(), leEntry,
                                  bRecursive);
        }
        catch (LDAPException e)
        {
            throw new CordysConnectionException(CordysConnectionException.EC_LDAP,
                                                "Error deleting entry " + leEntry.getDN() +
                                                " from LDAP.", e);
        }
    }

    /**
     * This method gets the used LDAP directory.
     *
     * @return  The used LDAP directory.
     */
    public LDAPDirectory getLDAPDirectory()
    {
        return m_cConn.getMiddleware().getDirectory();
    }

    /**
     * This method gets the oranizational user for this connection.
     *
     * @return  The oranizational user for this connection.
     */
    public String getOrganizationalUser()
    {
        return m_cConn.getMiddleware().getDirectory().getOrganizationalUser();
    }

    /**
     * This method returns the search root of the current LDAP.
     *
     * @return  The search root.
     */
    public String getSearchRoot()
    {
        return m_cConn.getMiddleware().getDirectory().getDirectorySearchRoot();
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
            leReturn = m_cConn.getMiddleware().getDirectory().read(sDN);
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
     */
    public LDAPEntry[] searchLDAP(String sSearchRoot, int iLDAPScope, String sFilter)
                           throws CordysConnectionException
    {
        LDAPEntry[] aleReturn = null;

        try
        {
            aleReturn = LDAPUtils.searchLDAP(m_cConn.getMiddleware().getDirectory().getConnection(),
                                             sSearchRoot, LDAPConnection.SCOPE_SUB,
                                             "(objectclass=busauthenticationuser)");
        }
        catch (LDAPException e)
        {
            throw new CordysConnectionException(CordysConnectionException.EC_LDAP,
                                                "Error searching ldap with filter " + sFilter, e);
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

        // We need to get the SOAPMessage.
        Node nEnvelope = getEnvelope(nNode);

        if (nEnvelope instanceof Envelope)
        {
            Envelope eEnvelope = (Envelope) nEnvelope;
            Object oUserData = eEnvelope.getUserData();

            if (oUserData instanceof SOAPMessage)
            {
                SOAPMessage smMessage = (SOAPMessage) oUserData;
                eEnvelope.setUserData(null);

                try
                {
                    SOAPMessage smReturn = m_cConn.sendAndWait(smMessage, lTimeOut);

                    if (bCheckFault == true)
                    {
                        Element eResponse = smReturn.getEnvelope().getBody(false);

                        // Look for the SOAP fault
                        NodeList nlError = eResponse.getElementsByTagNameNS(eResponse
                                                                            .getNamespaceURI(),
                                                                            "Fault");

                        if (nlError.getLength() > 0)
                        {
                            throw new CordysConnectionException(CordysConnectionException.EC_SENDING,
                                                                "Cordys returned an error:\n" +
                                                                NiceDOMWriter.write(nlError.item(0)));
                        }

                        nReturn = smReturn.getEnvelope();
                    }
                }
                catch (Exception e)
                {
                    throw new CordysConnectionException(CordysConnectionException.EC_SENDING,
                                                        "Error sending the request to Cordys.", e);
                }
            }
            else
            {
                throw new CordysConnectionException(CordysConnectionException.EC_SENDING,
                                                    "Only XML created via the createSOAPMessage can be used (incorrect UserData).");
            }
        }
        else
        {
            throw new CordysConnectionException(CordysConnectionException.EC_SENDING,
                                                "Only XML created via the createSOAPMessage can be used.");
        }

        return nReturn;
    }
}
