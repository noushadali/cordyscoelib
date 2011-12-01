package com.cordys.coe.tools.snapshot.config;

import com.cordys.coe.tools.snapshot.data.Constants;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

/**
 * This class wraps the service group details.
 * 
 * @author localpg
 */
@XmlType(propOrder = { "name", "organization", "m_serviceContainers" })
public class ServiceGroup
{
    /**
     * Holds the name of the service group to monitor.
     */
    private String m_name;
    /**
     * Holds the organization in which the service group is running.
     */
    private String m_organization;
    /**
     * Holds the service groups that are part of this cluster.
     */
    @XmlElement(name = "ServiceContainer", namespace = Constants.NS)
    @XmlElementWrapper(name = "ServiceContainerList", namespace = Constants.NS)
    private ArrayList<ServiceContainer> m_serviceContainers = new ArrayList<ServiceContainer>();

    /**
     * This method gets the organization in which the service group is running.
     * 
     * @return The organization in which the service group is running.
     */
    @XmlElement(name = "Organization", namespace = Constants.NS)
    public String getOrganization()
    {
        return m_organization;
    }

    /**
     * This method sets the organization in which the service group is running.
     * 
     * @param organization The organization in which the service group is running.
     */
    public void setOrganization(String organization)
    {
        m_organization = organization;
    }

    /**
     * This method gets the name of the service group to monitor.
     * 
     * @return The name of the service group to monitor.
     */
    @XmlElement(name = "Name", namespace = Constants.NS)
    public String getName()
    {
        return m_name;
    }

    /**
     * This method sets the name of the service group to monitor.
     * 
     * @param name The name of the service group to monitor.
     */
    public void setName(String name)
    {
        m_name = name;
    }

    /**
     * This method returns the service containers to include in the dump.
     * 
     * @return The service containers to include in the dump.
     */
    public ArrayList<ServiceContainer> getServiceContainerList()
    {
        return m_serviceContainers;
    }

    /**
     * This method adds the given service container.
     * 
     * @param serviceContainer The service container to add.
     */
    public void addServiceContainer(ServiceContainer serviceContainer)
    {
        m_serviceContainers.add(serviceContainer);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return getName();
    }

    /**
     * This method clears all currently active service containers.
     */
    public void clearServiceContainerList()
    {
        m_serviceContainers.clear();
    }
}
