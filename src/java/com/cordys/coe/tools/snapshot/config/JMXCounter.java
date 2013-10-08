package com.cordys.coe.tools.snapshot.config;

import com.cordys.coe.tools.snapshot.data.Constants;
import com.cordys.coe.tools.snapshot.data.IJMXDataCollector;
import com.cordys.coe.tools.snapshot.data.collector.DefaultDataCollector;
import com.cordys.coe.util.StringUtils;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Wraps the counter details that should be logged.
 * 
 * @author localpg
 */
@XmlRootElement(name = "JMXCounter", namespace = Constants.NS)
@XmlType(propOrder = { "domain", "m_nameProperties", "property", "counterType", "m_parameters", "dataHandler", "dataCollector" })
public class JMXCounter implements Cloneable
{
    /**
     * Holds the name of the counter.
     */
    @XmlElement(name = "NameProperty", namespace = Constants.NS)
    @XmlElementWrapper(name = "NameProperties", namespace = Constants.NS)
    private List<Property> m_nameProperties = new ArrayList<Property>();
    /**
     * Holds the name of the counter.
     */
    @XmlElement(name = "Parameter", namespace = Constants.NS)
    @XmlElementWrapper(name = "ParameterList", namespace = Constants.NS)
    private List<Parameter> m_parameters = new ArrayList<Parameter>();
    /**
     * Holds the name of the property to get.
     */
    private String m_property;
    /**
     * Holds the domain of the property.
     */
    private String m_domain;
    /**
     * Holds the type of counter.
     */
    private EJMXCounterType m_type = EJMXCounterType.CUSTOM;
    /**
     * Holds the data handler class to use to process the result.
     */
    private String m_dataHandler;
    /**
     * Holds the data collector that should be used to collect the data from the JMX connection.
     */
    private String m_dataCollector;

    /**
     * This method gets the data collector that should be used to collect the data from the JMX connection.
     * 
     * @return The data collector that should be used to collect the data from the JMX connection.
     */
    @XmlElement(name = "DataCollector", namespace = Constants.NS)
    public String getDataCollector()
    {
        return m_dataCollector;
    }

    /**
     * Clones the object,
     */
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        JMXCounter retVal = new JMXCounter();

        retVal.m_property = m_property;
        retVal.m_domain = m_domain;
        retVal.m_type = m_type;
        retVal.m_dataHandler = m_dataHandler;
        retVal.m_dataCollector = m_dataCollector;

        for (Property p : m_nameProperties)
        {
            retVal.m_nameProperties.add((Property) p.clone());
        }

        for (Parameter p : m_parameters)
        {
            retVal.m_parameters.add((Parameter) p.clone());
        }

        return super.clone();
    }

    /**
     * This method sets the data collector that should be used to collect the data from the JMX connection.
     * 
     * @param dataCollector The data collector that should be used to collect the data from the JMX connection.
     */
    public void setDataCollector(String dataCollector)
    {
        m_dataCollector = dataCollector;
    }

    /**
     * This method gets the data handler class to use to process the result.
     * 
     * @return The data handler class to use to process the result.
     */
    @XmlElement(name = "DataHandler", namespace = Constants.NS)
    public String getDataHandler()
    {
        return m_dataHandler;
    }

    /**
     * This method sets the data handler class to use to process the result.
     * 
     * @param dataHandler The data handler class to use to process the result.
     */
    public void setDataHandler(String dataHandler)
    {
        m_dataHandler = dataHandler;
    }

    /**
     * This method gets the type of counter.
     * 
     * @return The type of counter.
     */
    @XmlElement(name = "CounterType", namespace = Constants.NS)
    public EJMXCounterType getCounterType()
    {
        return m_type;
    }

    /**
     * This method sets the type of counter.
     * 
     * @param type The type of counter.
     */
    public void setCounterType(EJMXCounterType type)
    {
        m_type = type;
    }

    /**
     * This method gets the domain of the property.
     * 
     * @return The domain of the property.
     */
    @XmlElement(name = "Domain", namespace = Constants.NS)
    public String getDomain()
    {
        return m_domain;
    }

    /**
     * This method sets the domain of the property.
     * 
     * @param domain The domain of the property.
     */
    public void setDomain(String domain)
    {
        m_domain = domain;
    }

    /**
     * This method gets the name of the property to get.
     * 
     * @return The name of the property to get.
     */
    @XmlElement(name = "Property", namespace = Constants.NS)
    public String getProperty()
    {
        return m_property;
    }

    /**
     * This method sets the name of the property to get.
     * 
     * @param property The name of the property to get.
     */
    public void setProperty(String property)
    {
        m_property = property;
    }

    /**
     * This method returns the name properties that should be matched.
     * 
     * @return The name properties that should be matched.
     */
    public List<Property> getNamePropertyList()
    {
        return m_nameProperties;
    }

    /**
     * This method adds the name properties that should be matched.
     * 
     * @param property The name properties that should be matched.
     */
    public void addNameProperty(Property property)
    {
        m_nameProperties.add(property);
    }

    /**
     * This method returns the parameters for this JMX counter operation.
     * 
     * @return The parameters for this JMX counter operation.
     */
    public List<Parameter> getParameterList()
    {
        return m_parameters;
    }

    /**
     * This method adds the parameter for this JMX counter operation.
     * 
     * @param parameter The parameter for this JMX counter operation.
     */
    public void addParameter(Parameter parameter)
    {
        m_parameters.add(parameter);
    }

    /**
     * This method builds the hashtable for finding the proper object for this counter.
     * 
     * @return he hashtable for finding the proper object for this counter.
     */
    public Hashtable<String, String> buildList()
    {
        Hashtable<String, String> retVal = new Hashtable<String, String>();

        for (Property property : m_nameProperties)
        {
            retVal.put(property.getKey(), property.getValue());
        }

        return retVal;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(1024);

        sb.append(m_domain).append(",");

        boolean first = true;

        for (Property prop : m_nameProperties)
        {
            if (first)
            {
                first = false;
            }
            else
            {
                sb.append(",");
            }
            sb.append(prop.getKey()).append("=").append(prop.getValue());
        }

        if (StringUtils.isSet(m_property))
        {
            sb.append(":").append(m_property);
        }
        else if (StringUtils.isSet(m_dataCollector))
        {
            sb.append(":").append(m_dataCollector);
        }

        return sb.toString();
    }

    /**
     * This method creates the data collector object that should be used to collect the data.
     * 
     * @return The proper data collector to use.
     */
    public IJMXDataCollector createCollector()
    {
        IJMXDataCollector retVal = new DefaultDataCollector();

        try
        {
            if (StringUtils.isSet(m_dataCollector))
            {
                Class<?> clazz = Class.forName(getDataCollector());

                if (IJMXDataCollector.class.isAssignableFrom(clazz))
                {
                    retVal = (IJMXDataCollector) clazz.newInstance();
                }
            }
        }
        catch (Throwable t)
        {
            // Ignore the exception
        }

        return retVal;
    }
}
