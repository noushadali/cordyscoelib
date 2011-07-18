package com.cordys.coe.util.cgc;

import com.cordys.coe.util.xml.NamespaceDefinitions;
import com.cordys.coe.util.xml.dom.XPathHelper;

import java.util.ArrayList;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class wraps the localizable message information that is passed on in SOAP faults from C3
 * onwards.
 *
 * @author  pgussow
 */
public class LocalizableMessageInformation
{
    /**
     * Prefix to use for the namespace of Cordys general in the SOAP faults.
     */
    protected static String PRE_CORDYS = NamespaceDefinitions.PREFIX_CORDYS_GENERAL_1_0;
    /**
     * Holds all parameters for the given message.
     */
    private ArrayList<String> m_alParameters = new ArrayList<String>();
    /**
     * Holds the message code for this error.
     */
    private String m_sMessageCode;

    /**
     * Creates a new LocalizableMessageInformation object.
     *
     * @param  sMessageCode  The code for the message.
     * @param  asParameters  The parameters for the message.
     */
    public LocalizableMessageInformation(String sMessageCode, String[] asParameters)
    {
        m_sMessageCode = sMessageCode;

        for (String sParam : asParameters)
        {
            m_alParameters.add(sParam);
        }
    }

    /**
     * This method parses the locvalizable message information from the XML.
     *
     * @param   nLocalizableMessageInformation  The tag containing the information.
     *
     * @return  The Localizable message information.
     */
    public static LocalizableMessageInformation parseLocalizableMessageInformation(Node nLocalizableMessageInformation)
    {
        Node nTemp = null;

        try
        {
            nTemp = XPathHelper.prSelectSingleNode(nLocalizableMessageInformation,
                                                   "./" + PRE_CORDYS + ":MessageCode/text()");
        }
        catch (TransformerException e)
        {
            // Ignore it.
        }

        String sMessageCode = "";
        ArrayList<String> alParameters = new ArrayList<String>();

        if (nTemp != null)
        {
            sMessageCode = nTemp.getNodeValue();
        }

        // Get all parameters.
        try
        {
            NodeList anParameters = XPathHelper.prSelectNodeList(nLocalizableMessageInformation,
                                                                 "./" + PRE_CORDYS +
                                                                 ":Insertion/text()");

            for (int iCount = 0; iCount < anParameters.getLength(); iCount++)
            {
                alParameters.add(anParameters.item(iCount).getNodeValue());
            }
        }
        catch (TransformerException e)
        {
            // Ignore it.
        }

        return new LocalizableMessageInformation(sMessageCode, alParameters.toArray(new String[0]));
    }

    /**
     * This method gets the message code for this error.
     *
     * @return  The message code for this error.
     */
    public String getMessageCode()
    {
        return m_sMessageCode;
    }

    /**
     * This method gets the parameters for the message.
     *
     * @return  The parameters for the message.
     */
    public ArrayList<String> getParameters()
    {
        return m_alParameters;
    }

    /**
     * This method returns the string representation of the object.
     *
     * @return  The string representation of the object.
     *
     * @see     java.lang.Object#toString()
     */
    @Override public String toString()
    {
        StringBuilder sbReturn = new StringBuilder();

        if (m_sMessageCode != null)
        {
            sbReturn.append("MessageCode: " + getMessageCode());
        }

        if (m_alParameters.size() > 0)
        {
            for (String sParameter : m_alParameters)
            {
                sbReturn.append("Param: " + sParameter + " ");
            }
        }

        return sbReturn.toString();
    }
}
