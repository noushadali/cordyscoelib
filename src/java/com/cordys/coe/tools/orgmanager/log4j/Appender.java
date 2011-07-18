package com.cordys.coe.tools.orgmanager.log4j;

import com.cordys.coe.util.xml.dom.XPathHelper;

import org.w3c.dom.Element;

/**
 * This class wraps the appender configuration.
 *
 * @author  pgussow
 */
public class Appender extends BaseWithAppenderRef
{
    /**
     * Holds the layout to use for this appender.
     */
    private Layout m_lLayout;
    /**
     * Holds the name for this appender.
     */
    private String m_sName;

    /**
     * Creates a new Appender object.
     */
    public Appender()
    {
        super("appender");
    }

    /**
     * Creates a new Appender object.
     *
     * @param  eAppender  The appender to parse.
     */
    public Appender(Element eAppender)
    {
        super(eAppender, "appender");
        m_sName = eAppender.getAttribute("name");

        try
        {
            Element eLayout = (Element) XPathHelper.selectSingleNode(eAppender, "./layout");

            if (eLayout != null)
            {
                m_lLayout = new Layout(eLayout);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error parsing layout", e);
        }
    }

    /**
     * This method gets the layout to use for this appender.
     *
     * @return  The layout to use for this appender.
     */
    public Layout getLayout()
    {
        return m_lLayout;
    }

    /**
     * This method gets the name for this appender.
     *
     * @return  The name for this appender.
     */
    public String getName()
    {
        return m_sName;
    }

    /**
     * This method sets the layout to use for this appender.
     *
     * @param  lLayout  The layout to use for this appender.
     */
    public void setLayout(Layout lLayout)
    {
        m_lLayout = lLayout;
    }

    /**
     * This method sets the name for this appender.
     *
     * @param  sName  The name for this appender.
     */
    public void setName(String sName)
    {
        m_sName = sName;
    }

    /**
     * @see  com.cordys.coe.tools.orgmanager.log4j.BaseWithParam#onAfterAttributeCreation(org.w3c.dom.Element)
     */
    @Override protected void onAfterAttributeCreation(Element eElement)
    {
        eElement.setAttribute("name", getName());

        if (getLayout() != null)
        {
            getLayout().toXML(eElement);
        }
    }
}
