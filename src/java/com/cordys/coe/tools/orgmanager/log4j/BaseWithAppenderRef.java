package com.cordys.coe.tools.orgmanager.log4j;

import com.cordys.coe.util.xml.dom.XPathHelper;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This is a base class which also includes a .
 *
 * @author  pgussow
 */
public class BaseWithAppenderRef extends BaseWithParam
{
    /**
     * Holds the reference to nested appenders.
     */
    private ArrayList<String> m_alAppenderRef = new ArrayList<String>();

    /**
     * Creates a new BaseWithAppenderRef object.
     *
     * @param  sTagName  The name of the tag.
     */
    public BaseWithAppenderRef(String sTagName)
    {
        super(sTagName);
    }

    /**
     * Creates a new BaseWithAppenderRef object.
     *
     * @param  eParent   The parent element to parse.
     * @param  sTagName  The name of the tag.
     */
    public BaseWithAppenderRef(Element eParent, String sTagName)
    {
        super(eParent, sTagName);

        try
        {
            NodeList nl = XPathHelper.selectNodeList(eParent, "./appender-ref/@ref");

            for (int iCount = 0; iCount < nl.getLength(); iCount++)
            {
                m_alAppenderRef.add(nl.item(iCount).getNodeValue());
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error parsing parameters file", e);
        }
    }

    /**
     * This method gets the appender references.
     *
     * @return  The appender references.
     */
    public ArrayList<String> getAppenderReferences()
    {
        return new ArrayList<String>(m_alAppenderRef);
    }

    /**
     * This method sets the appender references for this object. The parameter must be a
     * comma-separated string.
     *
     * @param  sApenderReferences  The appender references to add.
     */
    public void setAppenderReferences(String sApenderReferences)
    {
        m_alAppenderRef.clear();

        String[] as = sApenderReferences.split(",");

        for (String sRef : as)
        {
            m_alAppenderRef.add(sRef);
        }
    }

    /**
     * @see  com.cordys.coe.tools.orgmanager.log4j.BaseWithParam#onAfterParameterCreation(org.w3c.dom.Element)
     */
    @Override protected void onAfterParameterCreation(Element eElement)
    {
        onBeforeAppenderReferenceCreation(eElement);

        for (String sRef : m_alAppenderRef)
        {
            if ((sRef != null) && (sRef.length() > 0))
            {
                Element eTemp = eElement.getOwnerDocument().createElementNS("http://jakarta.apache.org/log4j/", "appender-ref");
                eTemp.setAttribute("ref", sRef);
                eElement.appendChild(eTemp);
            }
        }
    }

    /**
     * Adapter method. Is called vefore the appender refenreces are created.
     *
     * @param  eElement  The created element.
     */
    protected void onBeforeAppenderReferenceCreation(Element eElement)
    {
    }
}
