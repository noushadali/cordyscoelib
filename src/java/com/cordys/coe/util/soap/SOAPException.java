package com.cordys.coe.util.soap;

import com.cordys.coe.exception.GeneralException;
import com.cordys.coe.util.xml.nom.XPathHelper;

import com.eibus.xml.nom.Node;
import com.eibus.xml.xpath.XPathMetaInfo;

/**
 * This class indicates exceptions related to SOAP.
 *
 * @author  pgussow
 */
public class SOAPException extends GeneralException
{
    /**
     * Holds the fault code of the SOAP fault.
     */
    private String m_faultCode;
    /**
     * Holds the fault string of the SOAP fault.
     */
    private String m_faultString;
    /**
     * Holds the fault actor.
     */
    private String m_faultActor;
    /**
     * Holds the request XML.
     */
    private String m_request;
    /**
     * Holds the response XML.
     */
    private String m_response;
    /**
     * Holds the SOAP details.
     */
    private String m_details;

    /**
     * Creates a new instance of <code>SOAPException</code> without a cause.
     */
    public SOAPException()
    {
        this(null, null, 0);
    }

    /**
     * Creates a new instance of <code>SOAPException</code> based on the the throwable.
     *
     * @param  tThrowable  The source throwable.
     */
    public SOAPException(Throwable tThrowable)
    {
        this(tThrowable, null, 0);
    }

    /**
     * Constructs an instance of <code>GeneralException</code> with the specified detail message.
     *
     * @param  sMessage  the detail message.
     */
    public SOAPException(String sMessage)
    {
        this(null, sMessage, 0);
    }

    /**
     * Creates a new instance of <code>SOAPException</code> based on the the throwable.
     *
     * @param  tThrowable  The cause.
     * @param  sMessage    The additional message.
     */
    public SOAPException(Throwable tThrowable, String sMessage)
    {
        this(tThrowable, sMessage, 0);
    }

    /**
     * Creates a new instance of <code>SOAPException</code> based on the the throwable.
     *
     * @param  tThrowable  The cause.
     * @param  sMessage    The additional message.
     * @param  faultXML    The XML containing the SOAP fault.
     */
    public SOAPException(Throwable tThrowable, String sMessage, int faultXML)
    {
        this(tThrowable, sMessage, null, faultXML);
    }

    /**
     * Creates a new instance of <code>SOAPException</code> based on the the throwable.
     *
     * @param  sMessage  The additional message.
     * @param  request   The request XML as a String to avoid NOM issues.
     * @param  faultXML  The XML containing the SOAP fault.
     */
    public SOAPException(String sMessage, int request, int faultXML)
    {
        this(null, sMessage, Node.writeToString(Node.getRoot(request), false), faultXML);
    }

    /**
     * Creates a new instance of <code>SOAPException</code> based on the the throwable.
     *
     * @param  tThrowable  The cause.
     * @param  sMessage    The additional message.
     * @param  request     The request XML as a String to avoid NOM issues.
     * @param  faultXML    The XML containing the SOAP fault.
     */
    public SOAPException(Throwable tThrowable, String sMessage, int request, int faultXML)
    {
        this(tThrowable, sMessage, Node.writeToString(Node.getRoot(request), false), faultXML);
    }

    /**
     * Creates a new instance of <code>SOAPException</code> based on the the throwable.
     *
     * @param  tThrowable  The cause.
     * @param  sMessage    The additional message.
     * @param  request     The request XML as a String to avoid NOM issues.
     * @param  faultXML    The XML containing the SOAP fault.
     */
    public SOAPException(Throwable tThrowable, String sMessage, String request, int faultXML)
    {
        super(tThrowable, sMessage);

        m_request = request;

        if (faultXML != 0)
        {
            m_response = Node.writeToString(Node.getRoot(faultXML), false);

            // Parse the SOAP fault
            XPathMetaInfo xmi = new XPathMetaInfo();
            xmi.addNamespaceBinding("SOAP", "http://schemas.xmlsoap.org/soap/envelope/");

            // Get the faults.
            int root = Node.getRoot(faultXML);
            m_faultCode = XPathHelper.getStringValue(root, "/SOAP:Envelope/SOAP:Body/SOAP:Fault/faultcode", xmi);
            m_faultString = XPathHelper.getStringValue(root, "/SOAP:Envelope/SOAP:Body/SOAP:Fault/faultstring", xmi);
            m_faultActor = XPathHelper.getStringValue(root, "/SOAP:Envelope/SOAP:Body/SOAP:Fault/faultactor", xmi);

            // Get the details.
            int detail = XPathHelper.selectSingleNode(root, "/SOAP:Envelope/SOAP:Body/SOAP:Fault/detail", xmi);
            m_details = Node.writeToString(detail, true);
        }
    }

    /**
     * This method gets the fault code of the SOAP fault.
     *
     * @return  The fault code of the SOAP fault.
     */
    public String getFaultCode()
    {
        return m_faultCode;
    }

    /**
     * This method gets the fault string of the SOAP fault.
     *
     * @return  The fault string of the SOAP fault.
     */
    public String getFaultString()
    {
        return m_faultString;
    }

    /**
     * This method gets the fault actor.
     *
     * @return  The fault actor.
     */
    public String getFaultActor()
    {
        return m_faultActor;
    }

    /**
     * This method gets the XML of the associated request.
     *
     * @return  The XML of the associated request.
     */
    public String getRequestXML()
    {
        return m_request;
    }

    /**
     * This method gets the XML of the associated response.
     *
     * @return  The XML of the associated response.
     */
    public String getResponseXML()
    {
        return m_response;
    }

    /**
     * This method gets the error details.
     *
     * @return  The error details.
     */
    public String getDetails()
    {
        return m_details;
    }
}
