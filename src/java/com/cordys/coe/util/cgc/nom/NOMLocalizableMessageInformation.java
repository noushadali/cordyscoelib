package com.cordys.coe.util.cgc.nom;

import com.cordys.coe.util.cgc.LocalizableMessageInformation;
import com.cordys.coe.util.xml.nom.XPathHelper;

import com.eibus.xml.nom.Node;

import java.util.ArrayList;

/**
 * This class wraps the localizable message information that is passed on in SOAP faults from C3
 * onwards.
 *
 * @author  pgussow
 */
public class NOMLocalizableMessageInformation extends LocalizableMessageInformation
{
    /**
     * Creates a new NOMLocalizableMessageInformation object.
     *
     * @param  sMessageCode  The code for the message.
     * @param  asParameters  The parameters for the message.
     */
    public NOMLocalizableMessageInformation(String sMessageCode, String[] asParameters)
    {
        super(sMessageCode, asParameters);
    }

    /**
     * This method parses the locvalizable message information from the XML.
     *
     * @param   iLocalizableMessageInformation  The tag containing the information.
     *
     * @return  The Localizable message information.
     */
    public static NOMLocalizableMessageInformation parseLocalizableMessageInformation(int iLocalizableMessageInformation)
    {
        int iTemp = 0;

        iTemp = XPathHelper.selectSingleNode(iLocalizableMessageInformation,
                                             "./" + PRE_CORDYS + ":MessageCode");

        String sMessageCode = "";
        ArrayList<String> alParameters = new ArrayList<String>();

        if (iTemp != 0)
        {
            sMessageCode = Node.getDataWithDefault(iTemp, null);
        }

        // Get all parameters.
        int[] anParameters = XPathHelper.selectNodes(iLocalizableMessageInformation,
                                                     "./" + PRE_CORDYS + ":Insertion");

        for (int iCount = 0; iCount < anParameters.length; iCount++)
        {
            String sValue = Node.getDataWithDefault(anParameters[iCount], null);

            if (sValue != null)
            {
                alParameters.add(sValue);
            }
        }

        return new NOMLocalizableMessageInformation(sMessageCode,
                                                    alParameters.toArray(new String[0]));
    }
}
