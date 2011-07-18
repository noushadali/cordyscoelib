package com.cordys.coe.tools.orgmanager.log4j;

import org.w3c.dom.Element;

/**
 * Wraps the priority definition.
 *
 * @author  pgussow
 */
public class Priority extends BaseWithParam
{
    /**
     * Holds the value for this priority.
     */
    private String m_sValue;

    /**
     * Creates a new Priority object.
     */
    public Priority()
    {
        super("priority");
    }

    /**
     * Creates a new Priority object.
     *
     * @param  eElement  The parent element.
     */
    public Priority(Element eElement)
    {
        super(eElement, "priority");
        m_sValue = eElement.getAttribute("value");
    }

    /**
     * This method gets the value for this priority.
     *
     * @return  The value for this priority.
     */
    public String getValue()
    {
        return m_sValue;
    }

    /**
     * This method sets the value for this priority.
     *
     * @param  sValue  The value for this priority.
     */
    public void setValue(String sValue)
    {
        m_sValue = sValue;
    }

    /**
     * @see  com.cordys.coe.tools.orgmanager.log4j.BaseWithParam#onAfterAttributeCreation(org.w3c.dom.Element)
     */
    @Override protected void onAfterAttributeCreation(Element eElement)
    {
        eElement.setAttribute("value", getValue());

        super.onAfterAttributeCreation(eElement);
    }
}
