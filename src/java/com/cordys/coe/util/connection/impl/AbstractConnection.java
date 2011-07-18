package com.cordys.coe.util.connection.impl;

import com.cordys.coe.util.config.IConfiguration;
import com.cordys.coe.util.connection.CordysConnectionException;
import com.cordys.coe.util.connection.ICordysConnection;

import org.w3c.dom.Node;

/**
 * This class is the base class for all cordys connections.
 *
 * @author  pgussow
 */
public abstract class AbstractConnection
    implements ICordysConnection
{
    /**
     * Holds whether or not the connection should check for soap faults and throw an exception.
     */
    private boolean m_bCheckSoapFault;
    /**
     * Holds the configuration.
     */
    private IConfiguration m_cConfig;
    /**
     * Holds the timeout.
     */
    private long m_lTimeout;
    /**
     * Holds the current organization.
     */
    private String m_sOrganization;

    /**
     * Creates a new AbstractConnection object.
     *
     * @param  cConfig          The configuration.
     * @param  bCheckSoapFault  Whether or not the connection should check for soap faults.
     */
    public AbstractConnection(IConfiguration cConfig, boolean bCheckSoapFault)
    {
        m_cConfig = cConfig;
        m_lTimeout = cConfig.getTimeout();
        m_bCheckSoapFault = bCheckSoapFault;
    }

    /**
     * This method returns whether or not to check for soap faults.
     *
     * @return  Whether or not to check for soap faults.
     */
    public boolean checkSoapFaults()
    {
        return m_bCheckSoapFault;
    }

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
     *
     * @see     com.cordys.coe.util.connection.ICordysConnection#createSoapMethod(java.lang.String,
     *          java.lang.String)
     */
    public Node createSoapMethod(String sName, String sNamespace)
                          throws CordysConnectionException
    {
        return createSoapMethod(null, null, sName, sNamespace, null);
    }

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
     *
     * @see     com.cordys.coe.util.connection.ICordysConnection#createSoapMethod(java.lang.String,
     *          java.lang.String, java.lang.String)
     */
    public Node createSoapMethod(String sReceiver, String sName, String sNamespace)
                          throws CordysConnectionException
    {
        return createSoapMethod(sReceiver, null, sName, sNamespace, null);
    }

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
     *
     * @see     com.cordys.coe.util.connection.ICordysConnection#createSoapMethod(java.lang.String,
     *          java.lang.String, java.lang.String, java.lang.String)
     */
    public Node createSoapMethod(String sReceiver, String sName, String sNamespace,
                                 String sUser)
                          throws CordysConnectionException
    {
        return createSoapMethod(sReceiver, null, sName, sNamespace, sUser);
    }

    /**
     * This method returns the configuration for this connection.
     *
     * @return  The configuration.
     */
    public IConfiguration getConfiguration()
    {
        return m_cConfig;
    }

    /**
     * This method returns the envelope node for the passed on method node.
     *
     * @param   nMethod  The method node.
     *
     * @return  The Envelope node.
     */
    public Node getEnvelope(Node nMethod)
    {
        Node nReturn = nMethod;

        while (nReturn.getParentNode() != null)
        {
            nReturn = nReturn.getParentNode();
        }

        return nReturn;
    }

    /**
     * This method gets the current organization.
     *
     * @return  The current organization.
     */
    public String getOrganization()
    {
        return m_sOrganization;
    }

    /**
     * This method returns the timeout in milliseconds to use.
     *
     * @return  The timeout in milliseconds to use.
     */
    public long getTimeout()
    {
        return m_lTimeout;
    }

    /**
     * This method sends the message to Cordys. The default timeout will be used. When the response
     * is received, the response will be checked for SOAP:Faults. If any was found, a SOAPException
     * is thrown. The received response-envelope will be added to the global arraylist with XML
     * nodes. Cleaning up all nodes can be done by calling the freeXMLNodes() method.
     *
     * @param   nNode  The XML node to send. It doesn't have to be the envelope tag. The method will
     *                 look in the parents of this node to find the envelope.
     *
     * @return  The response-envelope.
     *
     * @throws  CordysConnectionException  DOCUMENTME
     *
     * @see     com.cordys.coe.util.connection.ICordysConnection#sendAndWait(org.w3c.dom.Node)
     */
    public Node sendAndWait(Node nNode)
                     throws CordysConnectionException
    {
        return sendAndWait(nNode, m_lTimeout, checkSoapFaults());
    }

    /**
     * This method sends the message to Cordys. The default timeout will be used. If specified the
     * response will also be checked for SOAP:Faults. If any was found, a SOAPException is thrown.
     * The received response-envelope will be added to the global arraylist with XML nodes. Cleaning
     * up all nodes can be done by calling the freeXMLNodes() method.
     *
     * @param   nNode        The XML node to send. It doesn't have to be the envelope tag. The
     *                       method will look in the parents of this node to find the envelope.
     * @param   bCheckFault  Whether or not to check the response for SOAP:faults.
     *
     * @return  The response-envelope.
     *
     * @throws  CordysConnectionException  DOCUMENTME
     *
     * @see     com.cordys.coe.util.connection.ICordysConnection#sendAndWait(org.w3c.dom.Node,
     *          boolean)
     */
    public Node sendAndWait(Node nNode, boolean bCheckFault)
                     throws CordysConnectionException
    {
        return sendAndWait(nNode, m_lTimeout, bCheckFault);
    }

    /**
     * This method sends the message to Cordys with the specified timeout. When the response is
     * received, the response will be checked for SOAP:Faults. If any was found, a SOAPException is
     * thrown. The received response-envelope will be added to the global arraylist with XML nodes.
     * Cleaning up all nodes can be done by calling the freeXMLNodes() method.
     *
     * @param   nNode     The XML node to send. It doesn't have to be the envelope tag. The method
     *                    will look in the parents of this node to find the envelope.
     * @param   lTimeOut  The timeout to use for sending.
     *
     * @return  The response-envelope.
     *
     * @throws  CordysConnectionException  DOCUMENTME
     *
     * @see     com.cordys.coe.util.connection.ICordysConnection#sendAndWait(org.w3c.dom.Node, long)
     */
    public Node sendAndWait(Node nNode, long lTimeOut)
                     throws CordysConnectionException
    {
        return sendAndWait(nNode, lTimeOut, checkSoapFaults());
    }

    /**
     * This method sets the current organization.
     *
     * @param  sOrganization  The current organization.
     */
    public void setOrganization(String sOrganization)
    {
        this.m_sOrganization = sOrganization;
    }
}
