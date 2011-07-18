package com.cordys.coe.tools.log4j.regex;

import com.cordys.coe.util.xml.dom.XMLHelper;
import com.cordys.coe.util.xml.dom.XPathHelper;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Node;

/**
 * This class wraps around the regex entries in the repository.
 *
 * @author  pgussow
 */
public class RegExEntry
{
    /**
     * Holds the description.
     */
    private String m_sDescription = "";
    /**
     * Holds the regex.
     */
    private String m_sRegEx = "";

    /**
     * Creates a new RegExEntry object.
     *
     * @param   nEntry  The entry XML.
     *
     * @throws  TransformerException  DOCUMENTME
     */
    public RegExEntry(Node nEntry)
               throws TransformerException
    {
        Node nTemp = XPathHelper.selectSingleNode(nEntry, "./description/text()");

        if (nTemp != null)
        {
            m_sDescription = nTemp.getNodeValue();
        }
        nTemp = XPathHelper.selectSingleNode(nEntry, "./regex/text()");

        if (nTemp != null)
        {
            m_sRegEx = nTemp.getNodeValue();
        }
    }

    /**
     * Creates a new RegExEntry object.
     *
     * @param  sRegEx        The regex.
     * @param  sDescription  The description for the regex.
     */
    public RegExEntry(String sRegEx, String sDescription)
    {
        m_sRegEx = sRegEx;
        m_sDescription = sDescription;
    }

    /**
     * This method gets the description for the regex.
     *
     * @return  The description for the regex.
     */
    public String getDescription()
    {
        return m_sDescription;
    }

    /**
     * This method gets the actual regex.
     *
     * @return  The actual regex.
     */
    public String getRegEx()
    {
        return m_sRegEx;
    }

    /**
     * DOCUMENTME.
     *
     * @return
     *
     * @see     java.lang.Object#hashCode()
     */
    @Override public int hashCode()
    {
        return getRegEx().hashCode();
    }

    /**
     * This method sets the description for the regex.
     *
     * @param  sDescription  The description for the regex.
     */
    public void setDescription(String sDescription)
    {
        m_sDescription = sDescription;
    }

    /**
     * This method sets the actual regex.
     *
     * @param  sRegEx  The actual regex.
     */
    public void setRegEx(String sRegEx)
    {
        m_sRegEx = sRegEx;
    }

    /**
     * DOCUMENTME.
     *
     * @return
     *
     * @see     java.lang.Object#toString()
     */
    @Override public String toString()
    {
        return getRegEx();
    }

    /**
     * This method writes the data to XML. The node is appended to the given parent.
     *
     * @param  nParent  The parent node.
     */
    public void toXML(Node nParent)
    {
        if (nParent != null)
        {
            Node nEntry = XMLHelper.createElement("regexentry", nParent);
            XMLHelper.createTextElement("regex", getRegEx(), nEntry);
            XMLHelper.createTextElement("description", getDescription(), nEntry);
        }
    }
}
