package com.cordys.coe.tools.wcpproperties;

/**
 * This class wraps around an actual property and it's value.
 *
 * @author  pgussow
 */
public class ActualProperty
{
    /**
     * Holds the name of the property.
     */
    private String m_sName;
    /**
     * Holds the value of the property.
     */
    private String m_sValue;
    /**
     * Holds the meta information for this property.
     */
    private WcpProperty m_wpMeta;

    /**
     * Creates a new ActualProperty object.
     *
     * @param  sName   The name of the property.
     * @param  sValue  The value of the property.
     */
    public ActualProperty(String sName, String sValue)
    {
        m_sName = sName;
        m_sValue = sValue;
    }

    /**
     * This method gets the name of the property.
     *
     * @return  The name of the property.
     */
    public String getName()
    {
        return m_sName;
    }

    /**
     * This method gets the value for the property.
     *
     * @return  The value for the property.
     */
    public String getValue()
    {
        return m_sValue;
    }

    /**
     * This method gets the metadata for the current property.
     *
     * @return  The metadata for the current property.
     */
    public WcpProperty getWcpProperty()
    {
        return m_wpMeta;
    }

    /**
     * This method sets the name of the property.
     *
     * @param  sName  The name of the property.
     */
    public void setName(String sName)
    {
        m_sName = sName;
    }

    /**
     * This method sets the value for the property.
     *
     * @param  sValue  The value for the property.
     */
    public void setValue(String sValue)
    {
        m_sValue = sValue;
    }

    /**
     * This method sets the metadata for the current property.
     *
     * @param  wpMeta  The metadata for the current property.
     */
    public void setWcpProperty(WcpProperty wpMeta)
    {
        m_wpMeta = wpMeta;
    }
}
