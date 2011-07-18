package com.cordys.coe.util.soap;

import com.eibus.connector.nom.Connector;

/**
 * This interface describes the SOAP wrapper.
 *
 * @author  pgussow
 */
public interface ISOAPWrapper
{
    /**
     * This method sets the user to use for sending requests.
     *
     * @param  sUser  The user to use for sending requests.
     */
    void setUser(String sUser);

    /**
     * This method sets the new timeout to use when sending requests.
     *
     * @param  lTimeOut  The new timeout to use.
     */
    void setTimeOut(long lTimeOut);

    /**
     * This method sets the organization.
     *
     * @param  sOrganization  The organization.
     */
    void setOrganization(String sOrganization);

    /**
     * This mehtod sets the connector to use for sending requests.
     *
     * @param  cConnector  The new connector.
     */
    void setConnector(Connector cConnector);

    /**
     * This method sets whether or not xml-nodes are being collected in the global arraylist.
     *
     * @param  bShouldCollect  Indicates whether or not xml-nodes should be collected in the global arraylist.
     */
    void setCollecting(boolean bShouldCollect);

    /**
     * This method sends the message to Cordys with the specified timeout. used. If any was found, a SOAPException is
     * thrown. The received response-envelope will be added to the global arraylist with XML nodes. Cleaning up all
     * nodes can be done by calling the freeXMLNodes() method.
     *
     * @param   iNode        The XML node to send. It doesn't have to be the envelope tag. The method will look in the
     *                       parents of this node to find the envelope.
     * @param   lTimeOut     The timeout to use for sending.
     * @param   bCheckFault  Whether or not to check the response for SOAP:faults.
     *
     * @return  The response-envelope.
     *
     * @throws  SOAPException  In case of any exceptions
     */
    int sendAndWait(int iNode, long lTimeOut, boolean bCheckFault)
             throws SOAPException;

    /**
     * This method sends the message to Cordys. The default timeout will be used. If specified the response will also be
     * checked for SOAP:Faults. If any was found, a SOAPException is thrown. The received response-envelope will be
     * added to the global arraylist with XML nodes. Cleaning up all nodes can be done by calling the freeXMLNodes()
     * method.
     *
     * @param   iNode        The XML node to send. It doesn't have to be the envelope tag. The method will look in the
     *                       parents of this node to find the envelope.
     * @param   bCheckFault  Whether or not to check the response for SOAP:faults.
     *
     * @return  The response-envelope.
     *
     * @throws  SOAPException  In case of any exceptions
     */
    int sendAndWait(int iNode, boolean bCheckFault)
             throws SOAPException;

    /**
     * This method sends the message to Cordys with the specified timeout. When the response is received, the response
     * will be checked for SOAP:Faults. If any was found, a SOAPException is thrown. The received response-envelope will
     * be added to the global arraylist with XML nodes. Cleaning up all nodes can be done by calling the freeXMLNodes()
     * method.
     *
     * @param   iNode     The XML node to send. It doesn't have to be the envelope tag. The method will look in the
     *                    parents of this node to find the envelope.
     * @param   lTimeOut  The timeout to use for sending.
     *
     * @return  The response-envelope.
     *
     * @throws  SOAPException  In case of any exceptions
     */
    int sendAndWait(int iNode, long lTimeOut)
             throws SOAPException;

    /**
     * This method sends the message to Cordys. The default timeout will be used. When the response is received, the
     * response will be checked for SOAP:Faults. If any was found, a SOAPException is thrown. The received
     * response-envelope will be added to the global arraylist with XML nodes. Cleaning up all nodes can be done by
     * calling the freeXMLNodes() method.
     *
     * @param   iNode  The XML node to send. It doesn't have to be the envelope tag. The method will look in the parents
     *                 of this node to find the envelope.
     *
     * @return  The response-envelope.
     *
     * @throws  SOAPException  In case of any exceptions.
     */
    int sendAndWait(int iNode)
             throws SOAPException;

    /**
     * This method sends the message to Cordys without waiting for the response.
     *
     * @param   iNode  The XML node to send. It doesn't have to be the envelope tag. The method will look in the parents
     *                 of this node to find the envelope.
     *
     * @throws  SOAPException  In case of any exceptions.
     */
    void sendAndForget(int iNode)
                throws SOAPException;

    /**
     * This method returns whether or not xml-nodes are being collected in the global arraylist.
     *
     * @return  Whether or not xml-nodes are being collected in the global arraylist.
     */
    boolean isCollecting();

    /**
     * This method returns the DN of the currently used user when sending requests.
     *
     * @return  The DN of the currently used user when sending requests.
     */
    String getUser();

    /**
     * This method returns the currently used timeout when sending requests.
     *
     * @return  The currently used timeout when sending requests.
     */
    long getTimeOut();

    /**
     * This method gets the organization.
     *
     * @return  The organization.
     */
    String getOrganization();

    /**
     * This method returns the connector that's being used.
     *
     * @return  The connector.
     */
    Connector getConnector();

    /**
     * This method cleans all the XML garbage in the arraylist.
     */
    void freeXMLNodes();

    /**
     * This method creates a SOAP-method for the given name and namespace. In the header the receiver is specified as
     * the one that is passed on. Also the URI of the receiving backend component is specified in the header. The user
     * on who's behalf the message will be sent is the user that was passed on to this method. The envelope of the
     * created message will be added to the global nodes-arraylist.
     *
     * @param   sReceiver   The receiver of the message.
     * @param   sUri        The URI of the receiver.
     * @param   sName       The name of the method.
     * @param   sNamespace  The namespace of the method.
     * @param   sUser       The user on who's behalf the message should be sent.
     *
     * @return  The XMLNode to the method.
     *
     * @throws  SOAPException  In case of any exceptions
     */
    int createSoapMethod(String sReceiver, String sUri, String sName, String sNamespace, String sUser)
                  throws SOAPException;

    /**
     * This method creates a SOAP-method for the given name and namespace. In the header the receiver is specified as
     * the one that is passed on. The user on who's behalf the message will be sent is the user that was passed on to
     * this method. The envelope of the created message will be added to the global nodes-arraylist.
     *
     * @param   sReceiver   The receiver of the message.
     * @param   sName       The name of the method.
     * @param   sNamespace  The namespace of the method.
     * @param   sUser       The user on who's behalf the message should be sent.
     *
     * @return  The XMLNode to the method.
     *
     * @throws  SOAPException  In case of any exceptions
     */
    int createSoapMethod(String sReceiver, String sName, String sNamespace, String sUser)
                  throws SOAPException;

    /**
     * This method creates a SOAP-method for the given name and namespace. In the header the receiver is specified as
     * the one that is passed on. The user on who's behalf the message will be sent is the default user configured for
     * this wrapper. The envelope of the created message will be added to the global nodes-arraylist.
     *
     * @param   sReceiver   The receiver of the message.
     * @param   sName       The name of the method.
     * @param   sNamespace  The namespace of the method.
     *
     * @return  The XMLNode to the method.
     *
     * @throws  SOAPException  In case of any exceptions
     */
    int createSoapMethod(String sReceiver, String sName, String sNamespace)
                  throws SOAPException;

    /**
     * This method creates a SOAP-method for the given name and namespace. The user on who's behalf the message will be
     * sent is the default user configured for this wrapper. The envelope of the created message will be added to the
     * global nodes-arraylist.
     *
     * @param   sName       The name of the method.
     * @param   sNamespace  The namespace of the method.
     *
     * @return  The XMLNode to the method.
     *
     * @throws  SOAPException  In case of any exceptions
     */
    int createSoapMethod(String sName, String sNamespace)
                  throws SOAPException;

    /**
     * This method adds the passed on XML-node to the arraylist. All the nodes in this list will be disposed before the
     * flow finishes. This is to ensure there are no memoryleaks.
     *
     * @param  iXMLNode  The node that needs to be disposed. The value 0 is allowed, but it won't be added to the list,
     *                   since it does not represent an actual node
     */
    void addXMLGarbage(int iXMLNode);

    /**
     * This method adds a new method to the same message with the passed on name and namespace. iMethod can be any node
     * in a message since it will first get the envelope and then find the body. If iMethod is 0, the method is created
     * through createSoapMethod
     *
     * @param   iMethod     The node of the current message.
     * @param   sName       The name of the method.
     * @param   sNamespace  The namespace of the method.
     *
     * @return  The node of the newly created method.
     *
     * @throws  SOAPException  In case of any exceptions
     */
    int addMethod(int iMethod, String sName, String sNamespace)
           throws SOAPException;
}
