package com.cordys.coe.tools.snapshot.data.handler;

import javax.xml.bind.annotation.XmlValue;

/**
 * The wrapper class for a simple string value.
 *
 * @author  localpg
 */
public class StringWrappedValue
{
    /**
     * Holds the string representation of the object.
     */
    private String m_value;

    /**
     * Creates a new StringWrappedValue object.
     */
    public StringWrappedValue()
    {
        super();
    }

    /**
     * Creates a new StringWrappedValue object.
     *
     * @param  value  The value for this object.
     */
    public StringWrappedValue(String value)
    {
        m_value = value;
    }

    /**
     * This method gets the string representation of the object.
     *
     * @return  The string representation of the object.
     */
    @XmlValue public String getValue()
    {
        return m_value;
    }

    /**
     * This method sets the string representation of the object.
     *
     * @param  value  The string representation of the object.
     */
    public void setValue(String value)
    {
        m_value = value;
    }
}
