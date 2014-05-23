package com.cordys.coe.util.ac;

import com.cordys.coe.util.StringUtils;
import com.cordys.coe.util.soap.SOAPException;
import com.cordys.coe.util.soap.SOAPWrapper;
import com.cordys.coe.util.soap.SoapFaultInfo;
import com.eibus.connector.nom.Connector;
import com.eibus.exception.TimeoutException;
import com.eibus.xml.nom.Node;

/**
 * Holds the Class ConnectorWrapper.
 */
public class ConnectorWrapper implements IConnector
{
    /** Holds the actual connector. */
    private Connector m_connector;
    /** Holds the default organization for the requests. */
    private String m_defaultOrganization;
    /** Holds the default user for the requests */
    private String m_defaultUser;

    /**
     * Instantiates a new connector wrapper.
     * 
     * @param connector The connector
     */
    public ConnectorWrapper(Connector connector)
    {
        m_connector = connector;
    }

    /**
     * @see com.cordys.coe.util.ac.IConnector#getDefaultOrganization()
     */
    @Override
    public String getDefaultOrganization()
    {
        return m_defaultOrganization;
    }

    /**
     * @see com.cordys.coe.util.ac.IConnector#getDefaultUser()
     */
    @Override
    public String getDefaultUser()
    {
        return m_defaultUser;
    }

    /**
     * @see com.cordys.coe.util.ac.IConnector#setDefaultOrganization(java.lang.String)
     */
    @Override
    public void setDefaultOrganization(String defaultOrganization)
    {
        m_defaultOrganization = defaultOrganization;
    }

    /**
     * @see com.cordys.coe.util.ac.IConnector#setDefaultUser(java.lang.String)
     */
    @Override
    public void setDefaultUser(String defaultUser)
    {
        m_defaultUser = defaultUser;
    }

    /**
     * @see com.cordys.coe.util.ac.IConnector#createSOAPMethod(java.lang.String, java.lang.String)
     */
    @Override
    public int createSOAPMethod(String namespace, String operation) throws ACHelperException
    {
        return createSOAPMethod(m_defaultUser, m_defaultOrganization, namespace, operation);
    }

    /**
     * @see com.cordys.coe.util.ac.IConnector#createSOAPMethod(java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    public int createSOAPMethod(String userDN, String orgDN, String namespace, String operation) throws ACHelperException
    {
        try
        {
            if (!StringUtils.isSet(userDN))
            {
                userDN = m_defaultUser;
            }

            if (!StringUtils.isSet(orgDN))
            {
                orgDN = m_defaultOrganization;
            }

            return m_connector.createSOAPMethod(userDN, orgDN, namespace, operation);
        }
        catch (Exception e)
        {
            throw new ACHelperException(e, ACMessages.ERROR_CREATING_SOAP_OPERATION, namespace, operation, orgDN, userDN);
        }
    }

    /**
     * @see com.cordys.coe.util.ac.IConnector#sendAndWait(int, long)
     */
    public int sendAndWait(int envelope, long timeout) throws ACHelperException
    {
        return sendAndWait(envelope, timeout, false);
    }

    /**
     * @see com.cordys.coe.util.ac.IConnector#sendAndWait(int, long, boolean)
     */
    @Override
    public int sendAndWait(int envelope, long timeout, boolean checkSoapFault) throws ACHelperException
    {
        try
        {
            int retVal = m_connector.sendAndWait(envelope, timeout);

            if (checkSoapFault)
            {
                SoapFaultInfo faultInfo = SoapFaultInfo.findSoapFault(retVal);

                if (faultInfo != null)
                {
                    String msg = faultInfo.toString();

                    Node.delete(Node.getRoot(retVal));
                    retVal = 0;

                    throw new SOAPException(msg);
                }
            }

            return retVal;
        }
        catch (TimeoutException te)
        {
            throw new ACHelperException(te, ACMessages.TIMEOUT_SENDING_SOAP_CALL, Node.writeToString(envelope, false), timeout);
        }
        catch (Exception e)
        {
            throw new ACHelperException(e, ACMessages.ERROR_SENDING_SOAP_CALL, Node.writeToString(envelope, false), timeout);
        }
    }

    /**
     * @see com.cordys.coe.util.ac.IConnector#createSoapWrapper()
     */
    @Override
    public SOAPWrapper createSoapWrapper()
    {
        SOAPWrapper retVal = new SOAPWrapper(m_connector);

        if (StringUtils.isSet(m_defaultOrganization))
        {
            retVal.setOrganization(m_defaultOrganization);
        }
        
        if (StringUtils.isSet(m_defaultUser))
        {
            retVal.setUser(m_defaultUser);
        }

        return retVal;
    }
}
