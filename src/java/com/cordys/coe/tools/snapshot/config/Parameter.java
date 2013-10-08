package com.cordys.coe.tools.snapshot.config;

import com.cordys.coe.tools.snapshot.data.Constants;

import javax.xml.bind.annotation.XmlElement;

/**
 * Wrapper for parameters to operations.
 * 
 * @author localpg
 */
public class Parameter implements Cloneable
{
    /**
     * Holds the value of the parameter.
     */
    private String m_value;

    /**
     * Creates a new Parameter object.
     */
    public Parameter()
    {
    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        Parameter p = new Parameter();

        p.m_value = m_value;

        return p;
    }

    /**
     * Creates a new Parameter object.
     * 
     * @param value The value for the parameter.
     */
    public Parameter(String value)
    {
        m_value = value;
    }

    /**
     * This method gets the value of the parameter.
     * 
     * @return The value of the parameter.
     */
    @XmlElement(name = "Value", namespace = Constants.NS)
    public String getValue()
    {
        return m_value;
    }

    /**
     * This method sets the value of the parameter.
     * 
     * @param value The value of the parameter.
     */
    public void setValue(String value)
    {
        m_value = value;
    }
}
