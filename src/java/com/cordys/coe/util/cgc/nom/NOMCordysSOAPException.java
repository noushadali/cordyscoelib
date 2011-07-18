package com.cordys.coe.util.cgc.nom;

import com.cordys.coe.util.cgc.CordysSOAPException;
import com.cordys.coe.util.cgc.FaultRelatedExceptionInformation;
import com.cordys.coe.util.cgc.LocalizableMessageInformation;
import com.cordys.coe.util.cgc.ParsedException;
import com.cordys.coe.util.xml.NamespaceDefinitions;
import com.cordys.coe.util.xml.nom.XPathHelper;

import com.eibus.xml.nom.Node;

/**
 * Wrapper around the NOMSoapException.
 *
 * @author  pgussow
 */
public class NOMCordysSOAPException extends CordysSOAPException
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
     * Constructor.
     *
     * @param  sFaultCode        The faultcode of the message.
     * @param  sFaultString      The fault string of the message.
     * @param  sDetailedMessage  The detailed message.
     */
    public NOMCordysSOAPException(String sFaultCode, String sFaultString, String sDetailedMessage)
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
    public NOMCordysSOAPException(String sFaultCode, String sFaultString, String sDetailedMessage,
                                  String sExceptionXML, String sRequestXML)
    {
        super(sFaultCode, sFaultString, sDetailedMessage, sExceptionXML, sRequestXML);
    }

    /**
     * Creates a new NOMCordysSOAPException object.
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
    public NOMCordysSOAPException(String sFaultCode, String sFaultString, String sDetailedMessage,
                                  String sExceptionXML, String sRequestXML, String sActor,
                                  LocalizableMessageInformation lmiLocalizable,
                                  FaultRelatedExceptionInformation freiStackTrace)
    {
        super(sFaultCode, sFaultString, sDetailedMessage, sExceptionXML, sRequestXML, sActor,
              lmiLocalizable, freiStackTrace);
    }

    /**
     * This method returns a new instance of the exception object. It parses the XML and initializes
     * the object.
     *
     * @param   iFault            The actual soap fault.
     * @param   iRequestEnvelope  The request that caused this fault.
     *
     * @return  A new exception object representing the exception.
     */
    public static NOMCordysSOAPException parseSOAPFault(int iFault, int iRequestEnvelope)
    {
        String sRequestEnvelope = null;

        if (iRequestEnvelope != 0)
        {
            sRequestEnvelope = Node.writeToString(iRequestEnvelope, false);
        }

        return parseSOAPFault(iFault, sRequestEnvelope);
    }

    /**
     * This method returns a new instance of the exception object. It parses the XML and initializes
     * the object.
     *
     * @param   iFault            The actual soap fault.
     * @param   sRequestEnvelope  The request that caused this fault.
     *
     * @return  A new exception object representing the exception.
     */
    public static NOMCordysSOAPException parseSOAPFault(int iFault, String sRequestEnvelope)
    {
        NOMCordysSOAPException cseReturn = null;

        if (iFault == 0)
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
        int iTemp = 0;
        int iCordysFaultDetails = 0;

        String sXPathPrefix = PRE_SOAP + ":";

        // Pre-C3 style this tag is prefixed. But BasicProfile compliance means it's not prefixed.
        iTemp = XPathHelper.selectSingleNode(iFault, "./" + PRE_SOAP + ":faultcode");

        // Only avaialable in C3.
        iCordysFaultDetails = XPathHelper.selectSingleNode(iFault,
                                                           "./detail/" + PRE_CORDYS +
                                                           ":FaultDetails");

        if (iCordysFaultDetails != 0)
        {
            bC3Style = true;

            // we also need to get the tags without the namespace prefix.
            sXPathPrefix = "";
            iTemp = XPathHelper.selectSingleNode(iFault, "./faultcode");
        }

        if (iTemp != 0)
        {
            sFaultCode = Node.getDataWithDefault(iTemp, null);
        }

        // Get the fault string.
        iTemp = XPathHelper.selectSingleNode(iFault, "./" + sXPathPrefix + "faultstring");

        if (iTemp != 0)
        {
            sFaultString = Node.getDataWithDefault(iTemp, null);
        }

        // Get the fault actor.
        iTemp = XPathHelper.selectSingleNode(iFault, "./" + sXPathPrefix + "faultactor");

        if (iTemp != 0)
        {
            sFaultActor = Node.getDataWithDefault(iTemp, null);
        }

        // Get the deailted message.
        int iDetail = 0;

        iDetail = XPathHelper.selectSingleNode(iFault, "./" + sXPathPrefix + "detail");

        if (iDetail != 0)
        {
            StringBuffer sbDetail = new StringBuffer("");

            if (bC3Style == true)
            {
                // Let's parse Localizable message information.
                int iLocalizableMessageInformation = 0;

                iLocalizableMessageInformation = XPathHelper.selectSingleNode(iCordysFaultDetails,
                                                                              "./" + PRE_CORDYS +
                                                                              ":LocalizableMessage");

                if (iLocalizableMessageInformation != 0)
                {
                    lmi = NOMLocalizableMessageInformation.parseLocalizableMessageInformation(iLocalizableMessageInformation);
                }

                // Let's parse the FaultRelatedException information.
                int iFaultRelatedException = 0;

                iFaultRelatedException = XPathHelper.selectSingleNode(iDetail,
                                                                      "./" + PRE_CORDYS +
                                                                      ":FaultRelatedException");

                if (iFaultRelatedException != 0)
                {
                    String sFaultRelatedException = Node.getDataWithDefault(iFaultRelatedException,
                                                                            null);

                    if (sFaultRelatedException != null)
                    {
                        frei = new FaultRelatedExceptionInformation(sFaultRelatedException);
                    }
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
                int iExcChild = Node.getFirstChild(iDetail);

                while (iExcChild != 0)
                {
                    sbDetail.append(Node.writeToString(iExcChild, true));
                    sbDetail.append("\n");

                    iExcChild = Node.getNextSibling(iExcChild);
                }

                sDetailedMessage = sbDetail.toString();
            }
        }

        // Write the original XML.
        String sExceptionXML = Node.writeToString(iFault, true);

        // Write the request XML to a string.
        String sRequestXML = null;

        if (sRequestEnvelope != null)
        {
            sRequestXML = sRequestEnvelope;
        }

        // Create the exception object.
        if (bC3Style == true)
        {
            cseReturn = new NOMCordysSOAPException(sFaultCode, sFaultString, sDetailedMessage,
                                                   sExceptionXML, sRequestXML, sFaultActor, lmi,
                                                   frei);
        }
        else
        {
            cseReturn = new NOMCordysSOAPException(sFaultCode, sFaultString, sDetailedMessage,
                                                   sExceptionXML, sRequestXML);
        }

        return cseReturn;
    }
}
