package com.cordys.coe.tools.orgmanager.log4j;

import com.cordys.coe.util.xml.dom.XPathHelper;

import org.w3c.dom.Element;

/**
 * DOCUMENTME
 * .
 *
 * @author  pgussow
 */
public class Root extends BaseWithAppenderRef
{
    /**
     * Holds the priority for this appender.
     */
    private Priority m_pPriority;

    /**
     * Creates a new Root object.
     */
    public Root()
    {
        super("root");
    }

    /**
     * Creates a new Root object.
     *
     * @param  eParent  The parent to parse.
     */
    public Root(Element eParent)
    {
        super(eParent, "root");

        try
        {
            Element ePriority = (Element) XPathHelper.selectSingleNode(eParent, "./priority");

            if (ePriority != null)
            {
                m_pPriority = new Priority(ePriority);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error parsing priority", e);
        }
    }

    /**
     * This method gets the priority for this appender.
     *
     * @return  The priority for this appender.
     */
    public Priority getPriority()
    {
        return m_pPriority;
    }

    /**
     * This method sets the priority for this appender.
     *
     * @param  pPriority  The priority for this appender.
     */
    public void setPriority(Priority pPriority)
    {
        m_pPriority = pPriority;
    }

    /**
     * @see  com.cordys.coe.tools.orgmanager.log4j.BaseWithParam#onAfterAttributeCreation(org.w3c.dom.Element)
     */
    @Override protected void onAfterAttributeCreation(Element eElement)
    {
        if (getPriority() != null)
        {
            getPriority().toXML(eElement);
        }
    }
}
