package com.cordys.coe.tools.snapshot.config;

import com.cordys.coe.tools.snapshot.data.Constants;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Wraps the criteria for the property.
 * 
 * @author localpg
 */
@XmlRootElement(name = "Property", namespace = Constants.NS)
public class Property implements Cloneable
{
    /**
     * Holds the key of the property.
     */
    private String m_key;
    /**
     * Holds the value of the property.
     */
    private String m_value;

    /**
     * Creates a new Property object.
     */
    public Property()
    {
    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        Property p = new Property();

        p.m_key = m_key;
        p.m_value = m_value;

        return p;
    }

    /**
     * Creates a new Property object.
     * 
     * @param key The key of the property.
     * @param value The value for the property.
     */
    public Property(String key, String value)
    {
        m_key = key;
        m_value = value;
    }

    /**
     * This method gets the value of the property.
     * 
     * @return The value of the property.
     */
    @XmlElement(name = "Value", namespace = Constants.NS)
    public String getValue()
    {
        return m_value;
    }

    /**
     * This method sets the value of the property.
     * 
     * @param value The value of the property.
     */
    public void setValue(String value)
    {
        m_value = value;
    }

    /**
     * This method gets the key of the property.
     * 
     * @return The key of the property.
     */
    @XmlElement(name = "Key", namespace = Constants.NS)
    public String getKey()
    {
        return m_key;
    }

    /**
     * This method sets the key of the property.
     * 
     * @param key The key of the property.
     */
    public void setKey(String key)
    {
        m_key = key;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return m_key + "=" + m_value;
    }
}
