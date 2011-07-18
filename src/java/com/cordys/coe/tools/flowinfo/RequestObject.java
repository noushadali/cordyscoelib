package com.cordys.coe.tools.flowinfo;

import com.cordys.coe.util.cgc.ICordysGatewayClient;

import java.util.Date;

import org.eclipse.swt.widgets.TreeItem;

import org.w3c.dom.Element;

/**
 * This class is used to execute a specific request for more details.
 *
 * @author  pgussow
 */
public class RequestObject
{
    /**
     * Holds whether or not the request was executed successful.
     */
    private boolean m_bExecutedOK;
    /**
     * Holds the client to use for sending the request.
     */
    private ICordysGatewayClient m_cgcClient;
    /**
     * Holds the request type.
     */
    private EDetailRequest m_drType;
    /**
     * Holds the request that should be sent.
     */
    private Element m_eRequest;
    /**
     * Holds the response as it was received.
     */
    private Element m_eResponse;
    /**
     * Holds the end time when the response was received.
     */
    private long m_lEndTime;
    /**
     * Holds the start time when the request was sent.
     */
    private long m_lStartTime;
    /**
     * Holds the timeout to use.
     */
    private long m_lTimeOut;
    /**
     * Holds the organization name in which to run the request.
     */
    private String m_sOrganization;
    /**
     * Holds the exception that is related to the request.
     */
    private Throwable m_tException;
    /**
     * DOCUMENTME.
     */
    private TreeItem m_tiParent;

    /**
     * Creates a new RequestObject object.
     *
     * @param  cgcConnection  The Cordys Gateway Client to use.
     * @param  lTimeout       The timeout to use.
     * @param  eRequest       The actual request that was sent.
     * @param  drType         sMethodName The name of the method that is being executed.
     * @param  tiParent       DOCUMENTME
     */
    public RequestObject(ICordysGatewayClient cgcConnection, long lTimeout, Element eRequest,
                         EDetailRequest drType, TreeItem tiParent)
    {
        m_cgcClient = cgcConnection;
        m_lTimeOut = lTimeout;
        m_eRequest = eRequest;
        m_drType = drType;
        m_tiParent = tiParent;
    }

    /**
     * This method gets whether or not the request was executed successfull.
     *
     * @return  Whether or not the request was executed successfull.
     */
    public boolean executedOK()
    {
        return m_bExecutedOK;
    }

    /**
     * Call this method to set the end time.
     */
    public void finish()
    {
        m_lEndTime = System.currentTimeMillis();
    }

    /**
     * This method gets the CGC to use for sending the request.
     *
     * @return  The CGC to use for sending the request.
     */
    public ICordysGatewayClient getCGC()
    {
        return m_cgcClient;
    }

    /**
     * This method gets the duration of the call in miliseconds.
     *
     * @return  The duration of the call in miliseconds.
     */
    public long getDuration()
    {
        return m_lEndTime - m_lStartTime;
    }

    /**
     * This method gets the end time for the request.
     *
     * @return  The end time for the request.
     */
    public Date getEndTime()
    {
        return new Date(m_lEndTime);
    }

    /**
     * This method gets the exception that is related to the request.
     *
     * @return  The exception that is related to the request.
     */
    public Throwable getException()
    {
        return m_tException;
    }

    /**
     * This method gets the organization name in which to run the request.
     *
     * @return  The organization name in which to run the request.
     */
    public String getOrganization()
    {
        return m_sOrganization;
    }

    /**
     * This method gets the parent tree item.
     *
     * @return  The parent tree item.
     */
    public TreeItem getParentTreeItem()
    {
        return m_tiParent;
    }

    /**
     * This method gets the request that was sent.
     *
     * @return  The request that was sent.
     */
    public Element getRequest()
    {
        return m_eRequest;
    }

    /**
     * This method gets the request type.
     *
     * @return  The request type.
     */
    public EDetailRequest getRequestType()
    {
        return m_drType;
    }

    /**
     * This method gets the response as it was received.
     *
     * @return  The response as it was received.
     */
    public Element getResponse()
    {
        return m_eResponse;
    }

    /**
     * This method gets the start time for the request.
     *
     * @return  The start time for the request.
     */
    public Date getStartTime()
    {
        return new Date(m_lStartTime);
    }

    /**
     * This method gets the timeout to use.
     *
     * @return  The timeout to use.
     */
    public long getTimeout()
    {
        return m_lTimeOut;
    }

    /**
     * This method sets the exception that is related to the request.
     *
     * @param  tException  The exception that is related to the request.
     */
    public void setException(Throwable tException)
    {
        m_tException = tException;
    }

    /**
     * This method sets wether or not the request was executed successfull.
     *
     * @param  bExecutedOK  Whether or not the request was executed successfull.
     */
    public void setExecutedOK(boolean bExecutedOK)
    {
        m_bExecutedOK = bExecutedOK;
    }

    /**
     * This method sets the organization name in which to run the request.
     *
     * @param  sOrganization  The organization name in which to run the request.
     */
    public void setOrganization(String sOrganization)
    {
        m_sOrganization = sOrganization;
    }

    /**
     * This method sets the parent tree item.
     *
     * @param  tiParent  The parent tree item.
     */
    public void setParentTreeItem(TreeItem tiParent)
    {
        m_tiParent = tiParent;
    }

    /**
     * This method sets the request type.
     *
     * @param  drType  The request type.
     */
    public void setRequestType(EDetailRequest drType)
    {
        m_drType = drType;
    }

    /**
     * This method sets the response as it was received.
     *
     * @param  eResponse  The response as it was received.
     */
    public void setResponse(Element eResponse)
    {
        m_eResponse = eResponse;
    }

    /**
     * Call this method to set the start time.
     */
    public void start()
    {
        m_lStartTime = System.currentTimeMillis();
    }
}
