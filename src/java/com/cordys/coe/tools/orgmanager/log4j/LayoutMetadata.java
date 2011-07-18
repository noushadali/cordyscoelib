package com.cordys.coe.tools.orgmanager.log4j;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * This class wraps the layout definition.
 *
 * @author  pgussow
 */
public class LayoutMetadata implements IPropertyMetadata
{
    /**
     * Holds the properties for this appender.
     */
    private LinkedHashMap<String, PropertyMetadata> m_lhmProperties = new LinkedHashMap<String, PropertyMetadata>();
    /**
     * Holds the name for this appender.
     */
    private String m_sName;

    /**
     * This method adds the property metadata to the class.
     *
     * @param  pm  The property metadata.
     */
    public void addPropertyMetadata(PropertyMetadata pm)
    {
        m_lhmProperties.put(pm.getName(), pm);
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
     * This method gets the property metadata for this appender.
     *
     * @return  The property metadata for this appender.
     */
    public ArrayList<PropertyMetadata> getProperties()
    {
        return new ArrayList<PropertyMetadata>(m_lhmProperties.values());
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

		sbReturn.append("Layout name: ").append(getName()).append("\nProperties:\n");
		
		for (PropertyMetadata pm : m_lhmProperties.values())
		{
			sbReturn.append(pm.toString()).append("\n");
		}

		return sbReturn.toString();
	}
}
