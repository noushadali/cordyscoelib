package com.cordys.coe.util.cgc;

import com.cordys.coe.util.xml.NamespaceDefinitions;
import com.cordys.coe.util.xml.dom.NiceDOMWriter;
import com.cordys.coe.util.xml.dom.XPathHelper;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Node;

/**
 * This class wraps around soap faults retuned by Cordys. This class supports both pre-C3 soap
 * faults and C3 soap faults.
 *
 * @author  pgussow
 */
public class CordysSOAPException extends Exception
{
    /**
     * Prefix to use for the namespace of Cordys general in the SOAP faults.
     */
    private static String PRE_CORDYS = NamespaceDefinitions.PREFIX_CORDYS_GENERAL_1_0;
    /**
     * Prefix to use for the SOAP envelope.
     */
    private static String PRE_SOAP = NamespaceDefinitions.PREFIX_SOAP_1_1;
    /**
     * Identifies the faultcode for a timeout.
     */
    public static final String FC_TIMEOUT = "Server.Timeout";
    /**
     * Identfies the faultcode for the BsfObjectChangedException (Tuple changed by other user).
     */
    public static final Object FC_BSFOBJECTCHANGEDEXCEPTION = "BsfObjectChangedException";
    /**
     * Holds the fault related stack trace.
     */
    private FaultRelatedExceptionInformation m_freiStackTrace;
    /**
     * Holds the localizaable message.
     */
    private LocalizableMessageInformation m_lmiLocalizable;
    /**
     * Holds the fault actor.
     */
    private String m_sActor;
    /**
     * Holds the detailed message.
     */
    private String m_sDetailedMessage;
    /**
     * Holds the original XML.
     */
    private String m_sExceptionXML;
    /**
     * Holds the fault code.
     */
    private String m_sFaultCode;
    /**
     * Holds the fault string.
     */
    private String m_sFaultString;
    /**
     * Holds the soap message that caused this exception.
     */
    private String m_sRequestXML;

    /**
     * Constructor.
     *
     * @param  sFaultCode        The faultcode of the message.
     * @param  sFaultString      The fault string of the message.
     * @param  sDetailedMessage  The detailed message.
     */
    public CordysSOAPException(String sFaultCode, String sFaultString, String sDetailedMessage)
    {
        this(sFaultCode, sFaultString, sDetailedMessage, null, null);
    }

    /**
     * Constructor.
     *
     * @param  sFaultCode        The faultcode of the message.
     * @param  sFaultString      The fault string of the message.
     * @param  sDetailedMessage  The detailed message.
     * @param  sExceptionXML     The original exception XML as a formatted string.
     * @param  sRequestXML       The XML of the request that caused this exception.
     */
    public CordysSOAPException(String sFaultCode, String sFaultString, String sDetailedMessage,
                               String sExceptionXML, String sRequestXML)
    {
        super(sFaultString);
        this.m_sFaultCode = sFaultCode;
        this.m_sFaultString = sFaultString;
        this.m_sDetailedMessage = sDetailedMessage;
        this.m_sExceptionXML = sExceptionXML;
        this.m_sRequestXML = sRequestXML;
    }

    /**
     * Creates a new CordysSOAPException object.
     *
     * @param  sFaultCode        The faultcode of the message.
     * @param  sFaultString      The fault string of the message.
     * @param  sDetailedMessage  The detailed message.
     * @param  sExceptionXML     The original exception XML as a formatted string.
     * @param  sRequestXML       The XML of the request that caused this exception.
     * @param  sActor            The fault actor.
     * @param  lmiLocalizable    The localization message information.
     * @param  freiStackTrace    The stacktrace for this message.
     */
    public CordysSOAPException(String sFaultCode, String sFaultString, String sDetailedMessage,
                               String sExceptionXML, String sRequestXML, String sActor,
                               LocalizableMessageInformation lmiLocalizable,
                               FaultRelatedExceptionInformation freiStackTrace)
    {
        this(sFaultCode, sFaultString, sDetailedMessage, sExceptionXML, sRequestXML);

        m_sActor = sActor;
        m_lmiLocalizable = lmiLocalizable;
        m_freiStackTrace = freiStackTrace;
    }

    /**
     * This method returns a new instance of the exception object. It parses the XML and initializes
     * the object.
     *
     * @param   nFault            The actual soap fault.
     * @param   nRequestEnvelope  The request that caused this fault.
     *
     * @return  A new exception object representing the exception.
     */
    public static CordysSOAPException parseSOAPFault(Node nFault, Node nRequestEnvelope)
    {
        String sRequestEnvelope = null;

        if (nRequestEnvelope != null)
        {
            sRequestEnvelope = NiceDOMWriter.write(nRequestEnvelope);
        }

        return parseSOAPFault(nFault, sRequestEnvelope);
    }

    /**
     * This method returns a new instance of the exception object. It parses the XML and initializes
     * the object.
     *
     * @param   nFault            The actual soap fault.
     * @param   sRequestEnvelope  The request that caused this fault.
     *
     * @return  A new exception object representing the exception.
     */
    public static CordysSOAPException parseSOAPFault(Node nFault, String sRequestEnvelope)
    {
        CordysSOAPException cseReturn = null;

        if (nFault == null)
        {
            throw new IllegalArgumentException("The XML of the SOAP fault must be provided.");
        }

        // Determine whether or not it's a C3 style SOAP fault.
        boolean bC3Style = false;

        String sFaultCode = null;
        String sFaultString = null;
        String sDetailedMessage = null;
        String sFaultActor = null;
        LocalizableMessageInformation lmi = null;
        FaultRelatedExceptionInformation frei = null;

        // A SOAP fault has occurred, so we need to throw it.
        // Get the fault code.
        Node nTemp = null;
        Node nCordysFaultDetails = null;

        String sXPathPrefix = PRE_SOAP + ":";

        try
        {
            // Pre-C3 style this tag is prefixed. But BasicProfile compliance means it's not
            // prefixed.
            nTemp = XPathHelper.prSelectSingleNode(nFault, "./" + PRE_SOAP + ":faultcode/text()");

            // Only avaialable in C3.
            nCordysFaultDetails = XPathHelper.prSelectSingleNode(nFault,
                                                                 "./detail/" + PRE_CORDYS +
                                                                 ":FaultDetails");

            if (nCordysFaultDetails != null)
            {
                bC3Style = true;

                // we also need to get the tags without the namespace prefix.
                sXPathPrefix = "";
                nTemp = XPathHelper.prSelectSingleNode(nFault, "./faultcode/text()");
            }
        }
        catch (TransformerException e)
        {
            e.printStackTrace();
            // Ignore it.
        }

        if (nTemp != null)
        {
            sFaultCode = nTemp.getNodeValue();
        }

        // Get the fault string.
        try
        {
            nTemp = XPathHelper.prSelectSingleNode(nFault,
                                                   "./" + sXPathPrefix + "faultstring/text()");
        }
        catch (TransformerException e)
        {
            // Ignore it.
        }

        if (nTemp != null)
        {
            sFaultString = nTemp.getNodeValue();
        }

        // Get the fault actor.
        try
        {
            nTemp = XPathHelper.prSelectSingleNode(nFault,
                                                   "./" + sXPathPrefix + "faultactor/text()");
        }
        catch (TransformerException e)
        {
            // Ignore it.
        }

        if (nTemp != null)
        {
            sFaultActor = nTemp.getNodeValue();
        }

        // Get the deailted message.
        Node nDetail = null;

        try
        {
            nDetail = XPathHelper.prSelectSingleNode(nFault, "./" + sXPathPrefix + "detail");
        }
        catch (TransformerException e)
        {
            // Ignore it.
        }

        if (nDetail != null)
        {
            StringBuffer sbDetail = new StringBuffer("");

            if (bC3Style == true)
            {
                // Let's parse Localizable message information.
                Node nLocalizableMessageInformation = null;

                try
                {
                    nLocalizableMessageInformation = XPathHelper.prSelectSingleNode(nCordysFaultDetails,
                                                                                    "./" +
                                                                                    PRE_CORDYS +
                                                                                    ":LocalizableMessage");
                }
                catch (TransformerException e)
                {
                    // Ignore it.
                }

                if (nLocalizableMessageInformation != null)
                {
                    lmi = LocalizableMessageInformation.parseLocalizableMessageInformation(nLocalizableMessageInformation);
                }

                // Let's parse the FaultRelatedException information.
                Node nFaultRelatedException = null;

                try
                {
                    nFaultRelatedException = XPathHelper.prSelectSingleNode(nDetail,
                                                                            "./" + PRE_CORDYS +
                                                                            ":FaultRelatedException/text()");
                }
                catch (TransformerException e)
                {
                    // Ignore it.
                }

                if (nFaultRelatedException != null)
                {
                    frei = new FaultRelatedExceptionInformation(nFaultRelatedException
                                                                .getNodeValue());
                }

                // For C3 we will modifiy the sFaultString and append the message of
                // the lowest exception of the stacktrace (if it has a message)
                if ((frei != null) && (frei.getParsedException() != null))
                {
                    ParsedException pe = frei.getParsedException();

                    while (pe.getCause() != null)
                    {
                        pe = pe.getCause();
                    }

                    sFaultString += ("\nRoot cause: " + pe.getExceptionClass());

                    if (pe.getExceptionMessage() != null)
                    {
                        sFaultString += (": " + pe.getExceptionMessage());
                    }
                }
            }
            else
            {
                // Pre-C3 style faults.
                // Write the details.
                Node nExcChild = nDetail.getFirstChild();

                while (nExcChild != null)
                {
                    sbDetail.append(NiceDOMWriter.write(nExcChild));
                    sbDetail.append("\n");

                    nExcChild = nExcChild.getNextSibling();
                }

                sDetailedMessage = sbDetail.toString();
            }
        }

        // Write the original XML.
        String sExceptionXML = NiceDOMWriter.write(nFault);

        // Write the request XML to a string.
        String sRequestXML = null;

        if (sRequestEnvelope != null)
        {
            sRequestXML = sRequestEnvelope;
        }

        // Create the exception object.
        if (bC3Style == true)
        {
            cseReturn = new CordysSOAPException(sFaultCode, sFaultString, sDetailedMessage,
                                                sExceptionXML, sRequestXML, sFaultActor, lmi, frei);
        }
        else
        {
            cseReturn = new CordysSOAPException(sFaultCode, sFaultString, sDetailedMessage,
                                                sExceptionXML, sRequestXML);
        }

        return cseReturn;
    }

    /**
     * This method gets the actor for this SOAP fault.
     *
     * @return  The actor for this SOAP fault.
     */
    public String getActor()
    {
        return m_sActor;
    }

    /**
     * This method gets the detailed message for this soap fault.
     *
     * @return  The detailed message for this soap fault.
     */
    public String getDetailedMessage()
    {
        return m_sDetailedMessage;
    }

    /**
     * This method gets the fault code.
     *
     * @return  The fault code.
     */
    public String getFaultCode()
    {
        return m_sFaultCode;
    }

    /**
     * This method gets the FaultRelatedExceptionInformation for this fault.
     *
     * @return  The FaultRelatedExceptionInformation for this fault.
     */
    public FaultRelatedExceptionInformation getFaultRelatedExceptionInformation()
    {
        return m_freiStackTrace;
    }

    /**
     * This method gets the fault string.
     *
     * @return  The fault string.
     */
    public String getFaultString()
    {
        return m_sFaultString;
    }

    /**
     * This method gets the LocalizableMessageInformation for this fault.
     *
     * @return  The LocalizableMessageInformation for this fault.
     */
    public LocalizableMessageInformation getLocalizableMessageInformation()
    {
        return m_lmiLocalizable;
    }

    /**
     * This method gets the original XML..
     *
     * @return  The original XML..
     */
    public String getOriginalXML()
    {
        return m_sExceptionXML;
    }

    /**
     * This method gets the request XML that caused this exception.
     *
     * @return  The request XML that caused this exception.
     */
    public String getRequestXML()
    {
        return m_sRequestXML;
    }

    /**
     * This method returns a string representation of the exception.
     *
     * @return  A string representation of the exception.
     */
    @Override public String toString()
    {
        StringBuffer sbReturn = new StringBuffer("Fault code:       ");
        sbReturn.append(getFaultCode());

        if ((m_sFaultString != null) && (m_sFaultString.length() > 0))
        {
            sbReturn.append("\nFault string:     ");
            sbReturn.append(getFaultString());
        }

        if ((m_sActor != null) && (m_sActor.length() > 0))
        {
            sbReturn.append("\nActor string:     ");
            sbReturn.append(getActor());
        }

        if ((m_sDetailedMessage != null) && (m_sDetailedMessage.length() > 0))
        {
            sbReturn.append("\nDetailed message:\n");
            sbReturn.append(getDetailedMessage());
        }

        if ((m_lmiLocalizable != null))
        {
            sbReturn.append("\nLocalizable info:\n");
            sbReturn.append(m_lmiLocalizable.toString());
        }

        if ((m_sExceptionXML != null) && (m_sExceptionXML.length() > 0))
        {
            sbReturn.append("\nOriginal XML:");
            sbReturn.append(getOriginalXML());
        }

        if ((m_sRequestXML != null) && (m_sRequestXML.length() > 0))
        {
            sbReturn.append("\nRequest XML:\n");
            sbReturn.append(getRequestXML());
        }

        return sbReturn.toString();
    }
}
