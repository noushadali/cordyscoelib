package com.cordys.coe.tools.snapshot.data.handler;

import com.cordys.coe.tools.snapshot.data.Constants;

import java.util.ArrayList;

import javax.management.openmbean.CompositeType;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class wraps the composite data.
 *
 * @author  localpg
 */
@XmlRootElement(name = "CompositeData", namespace = Constants.NS)
public class CompositeData
{
    /**
     * Holds the values retrieved from the composite data object.
     */
    @XmlElement(name = "Property", namespace = Constants.NS)
    @XmlElementWrapper(name = "PropertyList", namespace = Constants.NS)
    private ArrayList<Property> m_values = new ArrayList<Property>();

    /**
     * Creates a new CompositeData object.
     */
    public CompositeData()
    {
    }

    /**
     * Creates a new CompositeData object.
     *
     * @param  data  The JMX data object.
     */
    public CompositeData(javax.management.openmbean.CompositeData data)
    {
        CompositeType type = data.getCompositeType();

        for (String property : type.keySet())
        {
            Object value = data.get(property);

            if (value == null)
            {
                value = "NULL";
            }

            // The data can also be nested Composite data
            addProperty(new Property(property, DataHandlerFactory.handleData(value, null)));
        }
    }

    /**
     * This method returns the JMX counters to include in the dump.
     *
     * @return  The JMX counters to include in the dump.
     */
    public ArrayList<Property> getPropertyList()
    {
        return m_values;
    }

    /**
     * This method adds the given JMX counter.
     *
     * @param  property  The JMX counter to add.
     */
    public void addProperty(Property property)
    {
        m_values.add(property);
    }

    /**
     * @see  java.lang.Object#toString()
     */
    @Override public String toString()
    {
        StringBuilder sb = new StringBuilder(1024);

        for (Property property : m_values)
        {
            sb.append(property.getKey()).append("=").append(property.getValue()).append("\n");
        }

        return sb.toString();
    }

    /**
     * Wraps the value of a property with a given name.
     */
    @XmlType(name = "CompositeDataPropertyValue")
    public static class Property
    {
        /**
         * Holds the key of the property.
         */
        private String m_key;
        /**
         * Holds the value of the property.
         */
        private Object m_value;

        /**
         * Creates a new Property object.
         */
        public Property()
        {
        }

        /**
         * Creates a new Property object.
         *
         * @param  key    The key of the property.
         * @param  value  The value for the property.
         */
        public Property(String key, Object value)
        {
            m_key = key;
            m_value = value;
        }

        /**
         * This method gets the value of the property.
         *
         * @return  The value of the property.
         */
        @XmlElement(name = "Value", namespace = Constants.NS)
        public Object getValue()
        {
            return m_value;
        }

        /**
         * This method sets the value of the property.
         *
         * @param  value  The value of the property.
         */
        public void setValue(Object value)
        {
            m_value = value;
        }

        /**
         * This method gets the key of the property.
         *
         * @return  The key of the property.
         */
        @XmlElement(name = "Key", namespace = Constants.NS)
        public String getKey()
        {
            return m_key;
        }

        /**
         * This method sets the key of the property.
         *
         * @param  key  The key of the property.
         */
        public void setKey(String key)
        {
            m_key = key;
        }

        /**
         * @see  java.lang.Object#toString()
         */
        @Override public String toString()
        {
            return m_key + "=" + m_value;
        }
    }
}
