package com.cordys.coe.util.connection;

import com.cordys.coe.util.config.IConfiguration;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPEntry;

import org.w3c.dom.Node;

/**
 * Generic interface around Cordys connections.
 *
 * @author  pgussow
 */
public interface ICordysConnection
{
    /**
     * This method modifies the LDAP attribute.
     *
     * @param   leEntry                     The entry.
     * @param   laAttribute                 The attribute to modify
     * @param   iLDAPModificationAttribute  The action that should be done.
     *
     * @throws  CordysConnectionException  DOCUMENTME
     */
    void changeLDAPAttibute(LDAPEntry leEntry, LDAPAttribute laAttribute,
                            int iLDAPModificationAttribute)
                     throws CordysConnectionException;

    /**
     * This mehtod changes the LDAP on an entry basis.
     *
     * @param   leEntry            The entry to modify.
     * @param   iLDAPModification  The type of modification.
     *
     * @throws  CordysConnectionException  DOCUMENTME
     */
    void changeLDAPEntry(LDAPEntry leEntry, int iLDAPModification)
                  throws CordysConnectionException;

    /**
     * This method returns whether or not to check for soap faults.
     *
     * @return  Whether or not to check for soap faults.
     */
    boolean checkSoapFaults();

    /**
     * This method creates a SOAP-method for the given name and namespace. The user on who's behalf
     * the message will be sent is the default user configured for this wrapper. The envelope of the
     * created message will be added to the global nodes-arraylist.
     *
     * @param   sName       The name of the method.
     * @param   sNamespace  The namespace of the method.
     *
     * @return  The XMLNode to the method.
     *
     * @throws  CordysConnectionException  DOCUMENTME
     */
    Node createSoapMethod(String sName, String sNamespace)
                   throws CordysConnectionException;

    /**
     * This method creates a SOAP-method for the given name and namespace. In the header the
     * receiver is specified as the one that is passed on. The user on who's behalf the message will
     * be sent is the default user configured for this wrapper. The envelope of the created message
     * will be added to the global nodes-arraylist.
     *
     * @param   sReceiver   The receiver of the message.
     * @param   sName       The name of the method.
     * @param   sNamespace  The namespace of the method.
     *
     * @return  The XMLNode to the method.
     *
     * @throws  CordysConnectionException  DOCUMENTME
     */
    Node createSoapMethod(String sReceiver, String sName, String sNamespace)
                   throws CordysConnectionException;

    /**
     * This method creates a SOAP-method for the given name and namespace. In the header the
     * receiver is specified as the one that is passed on. The user on who's behalf the message will
     * be sent is the user that was passed on to this method. The envelope of the created message
     * will be added to the global nodes-arraylist.
     *
     * @param   sReceiver   The receiver of the message.
     * @param   sName       The name of the method.
     * @param   sNamespace  The namespace of the method.
     * @param   sUser       The user on who's behalf the message should be sent.
     *
     * @return  The XMLNode to the method.
     *
     * @throws  CordysConnectionException  DOCUMENTME
     */
    Node createSoapMethod(String sReceiver, String sName, String sNamespace,
                          String sUser)
                   throws CordysConnectionException;

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
     */
    Node createSoapMethod(String sReceiver, String sUri, String sName, String sNamespace,
                          String sUser)
                   throws CordysConnectionException;

    /**
     * This method deletes the given LDAp entry.
     *
     * @param   leEntry     The entry to delete.
     * @param   bRecursive  Whether or not to delete it recursively.
     *
     * @throws  CordysConnectionException  DOCUMENTME
     */
    void deleteLDAPEntry(LDAPEntry leEntry, boolean bRecursive)
                  throws CordysConnectionException;

    /**
     * This method returns the configuration for this connection.
     *
     * @return  The configuration.
     */
    IConfiguration getConfiguration();

    /**
     * This method returns the envelope node for the passed on method node.
     *
     * @param   nMethod  The method node.
     *
     * @return  The Envelope node.
     */
    Node getEnvelope(Node nMethod);

    /**
     * This method gets the current organization.
     *
     * @return  The current organization.
     */
    String getOrganization();

    /**
     * This method gets the oranizational user for this connection.
     *
     * @return  The oranizational user for this connection.
     */
    String getOrganizationalUser();

    /**
     * This method returns the search root of the current LDAP.
     *
     * @return  The search root.
     */
    String getSearchRoot();

    /**
     * This method reads the specified entry from LDAP.
     *
     * @param   sDN  The DN to read.
     *
     * @return  The read entry.
     *
     * @throws  CordysConnectionException  DOCUMENTME
     */
    LDAPEntry readLDAPEntry(String sDN)
                     throws CordysConnectionException;

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
    LDAPEntry[] searchLDAP(String sSearchRoot, int iLDAPScope, String sFilter)
                    throws CordysConnectionException;

    /**
     * This method sends the message to Cordys. The default timeout will be used. When the response
     * is received, the response will be checked for SOAP:Faults. If any was found, a SOAPException
     * is thrown. The received response-envelope will be added to the global arraylist with XML
     * nodes. Cleaning up all nodes can be done by calling the freeXMLNodes() method.
     *
     * @param   iNode  The XML node to send. It doesn't have to be the envelope tag. The method will
     *                 look in the parents of this node to find the envelope.
     *
     * @return  The response-envelope.
     *
     * @throws  CordysConnectionException  DOCUMENTME
     */
    Node sendAndWait(Node iNode)
              throws CordysConnectionException;

    /**
     * This method sends the message to Cordys with the specified timeout. When the response is
     * received, the response will be checked for SOAP:Faults. If any was found, a SOAPException is
     * thrown. The received response-envelope will be added to the global arraylist with XML nodes.
     * Cleaning up all nodes can be done by calling the freeXMLNodes() method.
     *
     * @param   iNode     The XML node to send. It doesn't have to be the envelope tag. The method
     *                    will look in the parents of this node to find the envelope.
     * @param   lTimeOut  The timeout to use for sending.
     *
     * @return  The response-envelope.
     *
     * @throws  CordysConnectionException  DOCUMENTME
     */
    Node sendAndWait(Node iNode, long lTimeOut)
              throws CordysConnectionException;

    /**
     * This method sends the message to Cordys. The default timeout will be used. If specified the
     * response will also be checked for SOAP:Faults. If any was found, a SOAPException is thrown.
     * The received response-envelope will be added to the global arraylist with XML nodes. Cleaning
     * up all nodes can be done by calling the freeXMLNodes() method.
     *
     * @param   iNode        The XML node to send. It doesn't have to be the envelope tag. The
     *                       method will look in the parents of this node to find the envelope.
     * @param   bCheckFault  Whether or not to check the response for SOAP:faults.
     *
     * @return  The response-envelope.
     *
     * @throws  CordysConnectionException  DOCUMENTME
     */
    Node sendAndWait(Node iNode, boolean bCheckFault)
              throws CordysConnectionException;

    /**
     * This method sends the message to Cordys with the specified timeout. used. If any was found, a
     * SOAPException is thrown. The received response-envelope will be added to the global arraylist
     * with XML nodes. Cleaning up all nodes can be done by calling the freeXMLNodes() method.
     *
     * @param   iNode        The XML node to send. It doesn't have to be the envelope tag. The
     *                       method will look in the parents of this node to find the envelope.
     * @param   lTimeOut     The timeout to use for sending.
     * @param   bCheckFault  Whether or not to check the response for SOAP:faults.
     *
     * @return  The response-envelope.
     *
     * @throws  CordysConnectionException  DOCUMENTME
     */
    Node sendAndWait(Node iNode, long lTimeOut, boolean bCheckFault)
              throws CordysConnectionException;

    /**
     * This method sets the current organization.
     *
     * @param  sOrganization  The current organization.
     */
    void setOrganization(String sOrganization);
}
