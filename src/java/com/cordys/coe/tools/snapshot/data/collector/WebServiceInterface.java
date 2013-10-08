package com.cordys.coe.tools.snapshot.data.collector;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.cordys.coe.tools.snapshot.data.Constants;
import com.cordys.coe.util.StringUtils;

/**
 * This class contains the results of the Web Service Inspector of the web services that have been called.
 * 
 * @author pgussow
 */
@XmlRootElement(name = "WebServiceInterface", namespace = Constants.NS)
@XmlType(propOrder = { "name", "m_operations" })
public class WebServiceInterface
{
    /** Holds the web service operations that have been called for this web service interface. */
    @XmlElement(name = "WebServiceOperation", namespace = Constants.NS)
    @XmlElementWrapper(name = "WebServiceOperationList", namespace = Constants.NS)
    private List<WebServiceOperation> m_operations = new ArrayList<WebServiceOperation>();
    /** Holds the name of the web service interface. */
    private String m_name;

    /**
     * This method gets the name of the web service interface.
     * 
     * @return The name of the web service interface.
     */
    @XmlElement(name = "Name", namespace = Constants.NS)
    public String getName()
    {
        return m_name;
    }

    /**
     * This method sets the name of the web service interface.
     * 
     * @param name The name of the web service interface.
     */
    public void setName(String name)
    {
        if (StringUtils.isSet(name) && name.startsWith("\"") && name.endsWith("\""))
        {
            name = name.substring(1, name.length() - 1);
        }

        m_name = name;
    }

    /**
     * This method gets the web service operations that have been called for this web service interface.
     * 
     * @return The web service operations that have been called for this web service interface.
     */
    public List<WebServiceOperation> getOperations()
    {
        return new ArrayList<WebServiceOperation>(m_operations);
    }

    /**
     * This method adds the given operation to he web service operations that have been called for this web service interface.
     * 
     * @param operation The operation to add.
     */
    public void addOperation(WebServiceOperation operation)
    {
        if (!m_operations.contains(operation))
        {
            m_operations.add(operation);
        }
    }

    /**
     * This method removes the given operation from the web service operations that have been called for this web service
     * interface.
     * 
     * @param operation The operation to remove.
     */
    public void removeOperation(WebServiceOperation operation)
    {
        if (m_operations.contains(operation))
        {
            m_operations.remove(operation);
        }
    }

    /**
     * This method clears the web service operations that have been called for this web service interface.
     */
    public void clearOperations()
    {
        m_operations.clear();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return getName();
    }
}
