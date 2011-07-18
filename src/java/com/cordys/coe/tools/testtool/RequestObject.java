package com.cordys.coe.tools.testtool;

import com.cordys.coe.tools.testtool.methods.IMethodInfo;
import com.cordys.coe.util.cgc.ICordysGatewayClient;

import java.util.Date;

import org.eclipse.swt.widgets.TableItem;

import org.w3c.dom.Element;

/**
 * This class holds the details for the request that needs to be sent.
 *
 * @author  pgussow
 */
public class RequestObject
{
    /**
     * Holds whether or not the request was executed successfull.
     */
    private boolean m_bExecutedOK;
    /**
     * Holds the client to use for sending the request.
     */
    private ICordysGatewayClient m_cgcClient;
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
     * Holds the method information.
     */
    private IMethodInfo m_miInfo;
    /**
     * Holds the name of the method that is being executed.
     */
    private String m_sMethodName;
    /**
     * Holds the organization name in which to run the request.
     */
    private String m_sOrganization;
    /**
     * Holds the exception that is related to the request.
     */
    private Throwable m_tException;
    /**
     * Holds the corresponding table item.
     */
    private TableItem m_tiSource;

    /**
     * Creates a new RequestObject object.
     *
     * @param  cgcConnection  The Cordys Gateway Client to use.
     * @param  lTimeout       The timeout to use.
     * @param  eRequest       The actual request that was sent.
     * @param  sMethodName    The name of the method that is being executed.
     */
    public RequestObject(ICordysGatewayClient cgcConnection, long lTimeout, Element eRequest,
                         String sMethodName)
    {
        m_cgcClient = cgcConnection;
        m_lTimeOut = lTimeout;
        m_eRequest = eRequest;
        m_sMethodName = sMethodName;
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
     * This method gets the method information.
     *
     * @return  The method information.
     */
    public IMethodInfo getMethodInfo()
    {
        return m_miInfo;
    }

    /**
     * This method gets the name of the method that is being executed.
     *
     * @return  The name of the method that is being executed.
     */
    public String getMethodName()
    {
        return m_sMethodName;
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
     * This method gets the request that was sent.
     *
     * @return  The request that was sent.
     */
    public Element getRequest()
    {
        return m_eRequest;
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
     * This method gets the corresponding table item.
     *
     * @return  The corresponding table item.
     */
    public TableItem getTableItem()
    {
        return m_tiSource;
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
     * This method sets the method information.
     *
     * @param  miInfo  The method information.
     */
    public void setMethodInfo(IMethodInfo miInfo)
    {
        m_miInfo = miInfo;
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
     * This method sets the response as it was received.
     *
     * @param  eResponse  The response as it was received.
     */
    public void setResponse(Element eResponse)
    {
        m_eResponse = eResponse;
    }

    /**
     * This method sets the corresponding table item.
     *
     * @param  tiSource  The corresponding table item.
     */
    public void setTableItem(TableItem tiSource)
    {
        m_tiSource = tiSource;
    }

    /**
     * Call this method to set the start time.
     */
    public void start()
    {
        m_lStartTime = System.currentTimeMillis();
    }
}
