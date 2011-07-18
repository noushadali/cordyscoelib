package com.cordys.coe.tools.orgmanager.log4j;

/**
 * This class wraps a property for an appender.
 *
 * @author  pgussow
 */
public class PropertyMetadata
{
    /**
     * Holds the name for the property.
     */
    private String m_sName;
    /**
     * Holds the type for this property.
     */
    private String m_sType;

    /**
     * This method gets the name for the property.
     *
     * @return  The name for the property.
     */
    public String getName()
    {
        return m_sName;
    }

    /**
     * This method gets the type for this property.
     *
     * @return  The type for this property.
     */
    public String getType()
    {
        return m_sType;
    }

    /**
     * This method sets the name for the property.
     *
     * @param  sName  The name for the property.
     */
    public void setName(String sName)
    {
        m_sName = sName;
    }

    /**
     * This method sets the type for this property.
     *
     * @param  sType  The type for this property.
     */
    public void setType(String sType)
    {
        m_sType = sType;
    }
    
    /**
	 * This method returns the string representation of the object.
	 * 
	 * @return The string representation of the object.
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder sbReturn = new StringBuilder();

		sbReturn.append(getName()).append(": ").append(getType());

		return sbReturn.toString();
	}
}
