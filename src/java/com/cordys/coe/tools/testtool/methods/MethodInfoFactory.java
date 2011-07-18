package com.cordys.coe.tools.testtool.methods;

import com.cordys.coe.util.xml.dom.NamespaceConstants;
import com.cordys.coe.util.xml.dom.XMLHelper;
import com.cordys.coe.util.xml.dom.XPathHelper;
import com.eibus.directory.soap.DN;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Creates the method info object.
 *
 * @author  pgussow
 */
public class MethodInfoFactory
{
    /**
     * Holds teh LDAP namespace.
     */
    private static final String HTTP_SCHEMAS_CORDYS_COM_1_0_LDAP = "http://schemas.cordys.com/1.0/ldap";

    /**
     * This method created the method info object.
     *
     * @param   eMethodInfo  The LDAP entry for the method.
     * @param   sNamespace   The namespace for this method.
     *
     * @return  The method info object.
     *
     * @throws  TransformerException  DOCUMENTME
     */
    public static IMethodInfo createMethodInfo(Element eMethodInfo, String sNamespace)
                                        throws TransformerException
    {
        IMethodInfo miReturn = null;

        String sPrefix = NamespaceConstants.registerPrefix("ldap10",
                                                           HTTP_SCHEMAS_CORDYS_COM_1_0_LDAP);

        Node nName = XPathHelper.prSelectSingleNode(eMethodInfo,
                                                    "./" + sPrefix + ":cn/" + sPrefix +
                                                    ":string/text()");
        String sName = nName.getNodeValue();

        Node nImpl = XPathHelper.prSelectSingleNode(eMethodInfo,
                                                    "./" + sPrefix + ":busmethodimplementation/" +
                                                    sPrefix + ":string/text()");

        if (nImpl != null)
        {
            Document dDoc = XMLHelper.createDocumentFromXML(nImpl.getNodeValue(), true);
            String sMethodType = dDoc.getDocumentElement().getAttribute("type");

            if ((sMethodType != null) && (sMethodType.length() > 0))
            {
                if ("XReport".equals(sMethodType))
                {
                    miReturn = new XReportMethodInfo(sMethodType, sName, sNamespace);
                }
                else
                {
                    miReturn = new GenericMethodInfo(sMethodType, sName, sNamespace);
                }
                
                //Set the general information
                miReturn.setMethodSetDN(DN.getDN(eMethodInfo.getAttribute("dn")).getParent().toString());
            }
        }

        return miReturn;
    }
}
