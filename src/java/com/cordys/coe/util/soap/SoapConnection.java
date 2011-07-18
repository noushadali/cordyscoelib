package com.cordys.coe.util.soap;

import com.cordys.coe.util.log.LogInterface;
import com.cordys.coe.util.xml.Message;
import com.cordys.coe.util.xml.MessageContext;
import com.cordys.coe.util.xml.SharedXMLTree;
import com.cordys.coe.util.xml.XMLHelpers;

import com.eibus.connector.nom.Connector;

import com.eibus.xml.nom.Find;
import com.eibus.xml.nom.Node;

/**
 * A simple utility class to send SOAP messages using Message object.
 *
 * @author  mpoyhone
 */
public class SoapConnection
{
    /**
     * Name of the Connector created.
     */
    protected static final String CONNECTOR_NAME = "SoapConnection";
    /**
     * Indicates whether the connector should be closed when this object is closed.
     */
    protected boolean bCloseConnector = false;
    /**
     * The Connector object used for SOAP communication.
     */
    protected Connector cConnector;
    /**
     * A logger object for debug message.
     */
    protected LogInterface liLogger;
    /**
     * The SOAP request timeout value in millisecods. The default is 30 secs.
     */
    protected long lTimeout = 30000L;
    /**
     * All returned messages are added to this context.
     */
    protected MessageContext mcContext = null;
    /**
     * The organization that the messages are sent to.
     */
    protected String sOrganizationDN;
    /**
     * Calling user DN.
     */
    protected String sUserDN;

    /**
     * Creates a new SoapConnection object. This object creates an internal Connector that will be
     * used to make SOAP requests. This connector will be closed when this connection is closed.
     *
     * @param   sUserDN  The organization that the messages are sent to.
     *
     * @throws  SOAPException  DOCUMENTME
     */
    public SoapConnection(String sUserDN)
                   throws SOAPException
    {
        this.sUserDN = sUserDN;
        this.cConnector = createNewConnector();
        bCloseConnector = true;
    }

    /**
     * Creates a new SoapConnection object.
     *
     * @param  sUserDN  The organization that the messages are sent to.
     * @param  cConn    The Connector object used for SOAP communication
     */
    public SoapConnection(String sUserDN, Connector cConn)
    {
        this.sUserDN = sUserDN;
        this.cConnector = cConn;
    }

    /**
     * Creates a new SoapConnection object. This object creates an internal Connector that will be
     * used to make SOAP requests. This connector will be closed when this connection is closed.
     *
     * @param   sUserDN          The organization that the messages are sent to.
     * @param   sOrganizationDN  Calling user DN.
     *
     * @throws  SOAPException  DOCUMENTME
     */
    public SoapConnection(String sUserDN, String sOrganizationDN)
                   throws SOAPException
    {
        this.sUserDN = sUserDN;
        this.sOrganizationDN = sOrganizationDN;
        this.cConnector = createNewConnector();
        bCloseConnector = true;
    }

    /**
     * Creates a new SoapConnection object.
     *
     * @param  sUserDN          The organization that the messages are sent to.
     * @param  sOrganizationDN  Calling user DN.
     * @param  cConn            The Connector object used for SOAP communication
     */
    public SoapConnection(String sUserDN, String sOrganizationDN, Connector cConn)
    {
        this.sUserDN = sUserDN;
        this.sOrganizationDN = sOrganizationDN;
        this.cConnector = cConn;
    }

    /**
     * Closes this connection. The actual Connector object is closed only if it was created by this
     * connection.
     */
    public void close()
    {
        if (bCloseConnector && (cConnector != null))
        {
            cConnector.close();
            cConnector = null;
        }
    }

    /**
     * Standard java finalizer that will be called when this object will be cleaned up. This
     * implementation calls the close() method.
     */
    @Override protected void finalize()
    {
        // Make sure this finalizer doesn't throw any exceptions.
        try
        {
            close();
        }
        catch (Throwable ignored)
        {
        }
    }

    /**
     * Returns the message context that is be used for messages created by this connection.
     *
     * @return  The message context or null if none is set.
     */
    public MessageContext getMessageContext()
    {
        return mcContext;
    }

    /**
     * Returns the destination organization DN.
     *
     * @return  Returns organization DN.
     */
    public String getOrganizationDN()
    {
        return sOrganizationDN;
    }

    /**
     * Returns the SOAP request timeout value.
     *
     * @return  Returns the SOAP request timeout value.
     */
    public long getTimeout()
    {
        return lTimeout;
    }

    /**
     * Returns the calling user DN.
     *
     * @return  Returns the calling user DN.
     */
    public String getUserDN()
    {
        return sUserDN;
    }

    /**
     * Sends the SOAP request and waits for a reply.
     *
     * @param   mRequest  The request to be sent.
     *
     * @return  The SOAP reply received.
     *
     * @throws  SOAPException  Thrown if the sending failed.
     */
    public Message send(Message mRequest)
                 throws SOAPException
    {
        return send(mcContext, mRequest);
    }

    /**
     * Sends the SOAP request and waits for a reply.
     *
     * @param   iRequest  The request XML structure to be sent.
     *
     * @return  The SOAP reply XML structure received. The caller is responsible of freeing this.
     *
     * @throws  SOAPException  Thrown if the sending failed.
     */
    public int send(int iRequest)
             throws SOAPException
    {
        if (sUserDN == null)
        {
            throw new SOAPException("No user DN set for the SOAP connection.");
        }

        String sOrganization;

        // Figure out the right organization to be used.
        if (this.sOrganizationDN != null)
        {
            sOrganization = this.sOrganizationDN;
        }
        else
        {
            sOrganization = SoapHelpers.getOrganizationFromUser(sUserDN);
        }

        String sReceiver;
        String sMethod = Node.getName(iRequest);
        String sNamespace = Node.getAttribute(iRequest, "xmlns", "");
        int iRequestEnvelope = 0;
        int iResponse = 0;

        // Figure out the receiving SOAP node.
        sReceiver = SoapHelpers.getReceiver(sOrganization, sNamespace, sMethod);

        try
        {
            // Create the SOAP request envelope.
            iRequestEnvelope = cConnector.createSOAPMessage(sReceiver, sUserDN, null);

            // Add the request contents to SOAP message body.
            Node.appendToChildren(XMLHelpers.safeCloneNode(Node.getDocument(iRequestEnvelope),
                                                           iRequest), iRequestEnvelope);

            // Get the envelope root node.
            iRequestEnvelope = Node.getParent(iRequestEnvelope);

            if (liLogger != null)
            {
                liLogger.debug("Sending SOAP request :" +
                               Node.writeToString(iRequestEnvelope, true));
            }

            // Send the request.
            iResponse = cConnector.sendAndWait(iRequestEnvelope, lTimeout);

            if (liLogger != null)
            {
                liLogger.debug("Got a SOAP response : " + Node.writeToString(iResponse, true));
            }
        }
        catch (Exception e)
        {
            if (liLogger != null)
            {
                liLogger.info("An exception occured while sending the SOAP request." + e);
            }

            // Free the response data.
            if (iResponse != 0)
            {
                Node.delete(iResponse);
            }

            throw new SOAPException(e, "SOAP request failed.");
        }
        finally
        {
            // Free the request data.
            if (iRequestEnvelope != 0)
            {
                Node.delete(iRequestEnvelope);
            }
        }

        // Check for a SOAP fault from the response.
        int iError = Find.firstMatch(iResponse, "?<SOAP:Fault>");

        if (iError != 0)
        {
            throw new SOAPException("WCP returned an error:\n" + Node.writeToString(iError, true) +
                                    "\nOriginal request:\n" +
                                    Node.writeToString(iRequestEnvelope, true));
        }

        return iResponse;
    }

    /**
     * Sends the SOAP request and waits for a reply. This version adds the returned message to the
     * given message context.
     *
     * @param   mcContext  The context to which the message is to be added.
     * @param   mRequest   The request to be sent.
     *
     * @return  The SOAP reply received.
     *
     * @throws  SOAPException  Thrown if the sending failed.
     */
    public Message send(MessageContext mcContext, Message mRequest)
                 throws SOAPException
    {
        // Send the request.
        int iResponse = send(mRequest.getXmlNode());

        // Find the response content.
        int iResponseData = Find.firstMatch(iResponse, "?<SOAP:Body><>");

        if (iResponseData == 0)
        {
            if (iResponse != 0)
            {
                Node.delete(iResponse);
            }

            throw new SOAPException("No data in response SOAP body.");
        }

        // Construct a new Message object from the response.
        Message mResult = new Message(new SharedXMLTree(iResponse), iResponseData);

        if (mcContext != null)
        {
            mcContext.add(mResult);
        }

        return mResult;
    }

    /**
     * Sets the logger object for debug message.
     *
     * @param  liLogger  The logger object.
     */
    public void setLogger(LogInterface liLogger)
    {
        this.liLogger = liLogger;
    }

    /**
     * Sets the message context that will be used for messages created by this connection.
     *
     * @param  mcContext  The new message context
     */
    public void setMessageContext(MessageContext mcContext)
    {
        this.mcContext = mcContext;
    }

    /**
     * Sets the destination organization DN.
     *
     * @param  organizationDN  The organization DN to set.
     */
    public void setOrganizationDN(String organizationDN)
    {
        sOrganizationDN = organizationDN;
    }

    /**
     * Sets the SOAP request timeout value.
     *
     * @param  timeout  The SOAP request timeout value.
     */
    public void setTimeout(long timeout)
    {
        lTimeout = timeout;
    }

    /**
     * Sets the calling user DN.
     *
     * @param  userDN  The calling user DN to set.
     */
    public void setUserDN(String userDN)
    {
        sUserDN = userDN;
    }

    /**
     * Creates a new Connector object.
     *
     * @return  The created or exisiting Connector object.
     *
     * @throws  SOAPException  Thrown if the operation failed.
     */
    protected Connector createNewConnector()
                                    throws SOAPException
    {
        Connector cConn;

        try
        {
            cConn = Connector.getInstance(CONNECTOR_NAME);
        }
        catch (Exception e)
        {
            throw new SOAPException(e, "Unable to create a connector.");
        }

        if (!cConn.isOpen())
        {
            cConn.open();
        }

        return cConn;
    }
}
