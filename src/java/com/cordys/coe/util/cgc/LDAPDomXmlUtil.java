package com.cordys.coe.util.cgc;

import com.cordys.coe.util.exceptions.XMLWrapperException;
import com.cordys.coe.util.xml.dom.XMLHelper;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPEntry;

import java.util.ArrayList;
import java.util.Iterator;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class contains utils to convert LDAP entries to XML.
 *
 * @author  pgussow
 */
public class LDAPDomXmlUtil
{
    /**
     * This method creates the XML structure for the given entry.
     *
     * @param  leEntry  The entry to write to XML.
     * @param  eParent  The parent element.
     */
    public static void entryToXML(LDAPEntry leEntry, Element eParent)
    {
        if (leEntry != null)
        {
            Element eEntry = XMLHelper.createElementWithParentNS("entry", eParent);
            eEntry.setAttribute("dn", leEntry.getDN());

            LDAPAttributeSet asAttributes = leEntry.getAttributeSet();

            for (Iterator<?> iAttrs = asAttributes.iterator(); iAttrs.hasNext();)
            {
                LDAPAttribute laAttr = (LDAPAttribute) iAttrs.next();
                Element eAttr = XMLHelper.createElementWithParentNS(laAttr.getName(), eEntry);
                String[] saValues = laAttr.getStringValueArray();

                for (int iCount = 0; iCount < saValues.length; iCount++)
                {
                    XMLHelper.createTextElementWithParentNS("string", saValues[iCount], eAttr);
                }
            }
        }
    }

    /**
     * This method converts the XML to and LDAP entry.
     *
     * @param   eEntry  The entry to convert.
     *
     * @return  The corresponding LDAP entry.
     */
    public static LDAPEntry getEntryFromXML(Element eEntry)
    {
        LDAPEntryWrapper lewReturn = null;

        String sDN = eEntry.getAttribute("dn");
        LDAPAttributeSet attributes = new LDAPAttributeSet();

        LDAPAttribute[] attrs = getAttributes(eEntry);

        for (int i = 0; i < attrs.length; i++)
        {
            attributes.add(attrs[i]);
        }

        lewReturn = new LDAPEntryWrapper(sDN, attributes);
        lewReturn.setOriginalXML(eEntry);

        return lewReturn;
    }

    /**
     * This method gets the attributes for the given entry.
     *
     * @param   eEntry  The entry to parse.
     *
     * @return  The LDAP attributes for this entry.
     */
    protected static LDAPAttribute[] getAttributes(Element eEntry)
    {
        ArrayList<LDAPAttribute> alAttributes = new ArrayList<LDAPAttribute>();

        Node nCurrent = eEntry.getFirstChild();

        while (nCurrent != null)
        {
            if (nCurrent.getNodeType() == Node.ELEMENT_NODE)
            {
                String sName = nCurrent.getNodeName();

                String[] attributeValues = readAttributeValues((Element) nCurrent);

                if (attributeValues.length > 0) // Only add if there are values.
                {
                    // attributes.addElement(new LDAPAttribute(attributeName,attributeValues));
                    alAttributes.add(new LDAPAttribute(sName, attributeValues));
                }
            }

            // go to next attribute
            nCurrent = nCurrent.getNextSibling();
        }

        LDAPAttribute[] attributeArray = new LDAPAttribute[alAttributes.size()];
        alAttributes.toArray(attributeArray);
        return attributeArray;
    }

    /**
     * This method reads the string values.
     *
     * @param   eAttributeNode  The attribute node.
     *
     * @return  The string values.
     */
    private static String[] readAttributeValues(Element eAttributeNode)
    {
        String[] saReturn = null;

        ArrayList<String> alValues = new ArrayList<String>();
        Node nCurrent = eAttributeNode.getFirstChild();

        while (nCurrent != null)
        {
            if (nCurrent.getNodeType() == Node.ELEMENT_NODE)
            {
                String sData;

                try
                {
                    sData = XMLHelper.getData(nCurrent, "./text()");

                    if (sData.trim().length() > 0)
                    {
                        alValues.add(sData);
                    }
                }
                catch (XMLWrapperException e)
                {
                    // Ignore it.
                }
            }

            nCurrent = nCurrent.getNextSibling();
        }
        saReturn = new String[alValues.size()];
        alValues.toArray(saReturn);
        return saReturn;
    }
}
