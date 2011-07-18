package com.cordys.coe.tools.wcpproperties;

import com.cordys.coe.util.xml.dom.XPathHelper;

import java.util.ArrayList;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Wrapper around the WcpProperty definitions XML.
 *
 * @author  pgussow
 */
public class WcpProperty
{
    /**
     * Holds the list of where the property is used.
     */
    private ArrayList<WcpPropertyWhereUsed> m_alWhereUsed = new ArrayList<WcpPropertyWhereUsed>();
    /**
     * Holds the caption for the property.
     */
    private String m_sCaption;
    /**
     * Holds the component name where it's defined.
     */
    private String m_sComponent;
    /**
     * Holds the default value for this property.
     */
    private String m_sDefaultValue;
    /**
     * Holds the description of this property.
     */
    private String m_sDescription;
    /**
     * Holds the name of the property.
     */
    private String m_sName;
    /**
     * Indeicates whether or not the property is mandatory.
     */
    private boolean m_bMandatory;

    /**
     * Constructor. Creates the wrapper based on the XML.
     *
     * @param   eProperty  The property XML.
     *
     * @throws  TransformerException  DOCUMENTME
     */
    public WcpProperty(Element eProperty)
                throws TransformerException
    {
        Node nTemp = XPathHelper.selectSingleNode(eProperty, "./name/text()");

        if (nTemp != null)
        {
            m_sName = nTemp.getNodeValue();
        }
        nTemp = XPathHelper.selectSingleNode(eProperty, "./caption/text()");

        if (nTemp != null)
        {
            m_sCaption = nTemp.getNodeValue();
        }
        nTemp = XPathHelper.selectSingleNode(eProperty, "./description/text()");

        if (nTemp != null)
        {
            m_sDescription = nTemp.getNodeValue();
        }
        nTemp = XPathHelper.selectSingleNode(eProperty, "./component/text()");

        if (nTemp != null)
        {
            m_sComponent = nTemp.getNodeValue();
        }
        nTemp = XPathHelper.selectSingleNode(eProperty, "./default/text()");

        if (nTemp != null)
        {
            m_sDefaultValue = nTemp.getNodeValue();
        }

        nTemp = XPathHelper.selectSingleNode(eProperty, "./mandatory/text()");

        if (nTemp != null)
        {
            String sTemp = nTemp.getNodeValue();

            if (sTemp.equalsIgnoreCase("true") || sTemp.equalsIgnoreCase("yes"))
            {
                m_bMandatory = true;
            }
        }

        NodeList nlWhereUsed = XPathHelper.selectNodeList(eProperty, "./whereused/class");

        for (int iCount = 0; iCount < nlWhereUsed.getLength(); iCount++)
        {
            Element eClass = (Element) nlWhereUsed.item(iCount);
            WcpPropertyWhereUsed wpwuNew = new WcpPropertyWhereUsed(eClass);
            m_alWhereUsed.add(wpwuNew);
        }
    }

    /**
     * This method gets the caption for the property.
     *
     * @return  The caption for the property.
     */
    public String getCaption()
    {
        return ((m_sCaption == null) ? "" : m_sCaption);
    }

    /**
     * This method gets the component name where it's defined.
     *
     * @return  The component name where it's defined.
     */
    public String getComponent()
    {
        return ((m_sComponent == null) ? "" : m_sComponent);
    }

    /**
     * This method gets the default value for this property.
     *
     * @return  The default value for this property.
     */
    public String getDefaultValue()
    {
        return ((m_sDefaultValue == null) ? "" : m_sDefaultValue);
    }

    /**
     * This method gets the description of this property.
     *
     * @return  The description of this property.
     */
    public String getDescription()
    {
        return ((m_sDescription == null) ? "" : m_sDescription);
    }

    /**
     * This method gets the name of the property.
     *
     * @return  The name of the property.
     */
    public String getName()
    {
        return ((m_sName == null) ? "" : m_sName);
    }

    /**
     * This method gets the where-used list for this property.
     *
     * @return  The where-used list for this property.
     */
    public ArrayList<WcpPropertyWhereUsed> getWhereUsed()
    {
        return m_alWhereUsed;
    }

    /**
     * This method gets whether or not the property is mandatory in the wcp.properties file.
     *
     * @return  Whether or not the property is mandatory in the wcp.properties file.
     */
    public boolean isMandatory()
    {
        return m_bMandatory;
    }
}
