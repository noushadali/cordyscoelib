package com.cordys.coe.tools.snapshot.config;

import com.cordys.coe.tools.snapshot.data.Constants;
import com.cordys.coe.util.StringUtils;

import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class wraps the information from the RMI registry.
 *
 * @author  localpg
 */
@XmlRootElement(name = "ActualServiceContainer", namespace = Constants.NS)
public class ActualServiceContainer
{
    /**
     * Holds the name of the service container.
     */
    private String m_serviceContainer;
    /**
     * Holds the name of the service group.
     */
    private String m_serviceGroup;
    /**
     * Holds the name of the organization (could be empty).
     */
    private String m_organization;
    /**
     * Holds the JMX URL.
     */
    private String m_jmxURL;
    /**
     * Holds the name of the server it is running on.
     */
    private String m_server;

    /**
     * This method gets the name of the server it is running on.
     *
     * @return  The name of the server it is running on.
     */
    @XmlElement(name = "Server", namespace = Constants.NS)
    public String getServer()
    {
        return m_server;
    }

    /**
     * This method sets the name of the server it is running on.
     *
     * @param  server  The name of the server it is running on.
     */
    public void setServer(String server)
    {
        m_server = server;
    }

    /**
     * This method gets the JMX URL.
     *
     * @return  The JMX URL.
     */
    @XmlElement(name = "JmxURL", namespace = Constants.NS)
    public String getJmxUrl()
    {
        return m_jmxURL;
    }

    /**
     * This method sets the JMX URL.
     *
     * @param  jmxURL  The JMX URL.
     */
    public void setJmxUrl(String jmxURL)
    {
        m_jmxURL = jmxURL;
    }

    /**
     * This method gets the name of the organization (could be empty).
     *
     * @return  The name of the organization (could be empty).
     */
    @XmlElement(name = "Organization", namespace = Constants.NS)
    public String getOrganization()
    {
        return m_organization;
    }

    /**
     * This method sets the name of the organization (could be empty).
     *
     * @param  organization  The name of the organization (could be empty).
     */
    public void setOrganization(String organization)
    {
        m_organization = organization;
    }

    /**
     * This method gets the name of the service group.
     *
     * @return  The name of the service group.
     */
    @XmlElement(name = "ServiceGroup", namespace = Constants.NS)
    public String getServiceGroup()
    {
        return m_serviceGroup;
    }

    /**
     * This method sets the name of the service group.
     *
     * @param  serviceGroup  The name of the service group.
     */
    public void setServiceGroup(String serviceGroup)
    {
        m_serviceGroup = serviceGroup;
    }

    /**
     * This method gets the name of the service container.
     *
     * @return  The name of the service container.
     */
    @XmlElement(name = "ServiceContainer", namespace = Constants.NS)
    public String getServiceContainer()
    {
        return m_serviceContainer;
    }

    /**
     * This method sets the name of the service container.
     *
     * @param  serviceContainer  The name of the service container.
     */
    public void setServiceContainer(String serviceContainer)
    {
        m_serviceContainer = serviceContainer;
    }

    /**
     * @see  java.lang.Object#toString()
     */
    @Override public String toString()
    {
        StringBuilder sb = new StringBuilder(1024);

        sb.append(m_server).append(";").append(m_organization).append(";").append(m_serviceGroup).append(";")
          .append(m_serviceContainer).append(";").append(m_jmxURL);

        return sb.toString();
    }

    /**
     * This method will return true if this service container matches the given service group.
     *
     * @param   group  The group it should match.
     *
     * @return  true if this service container matches the given service group.
     */
    public boolean matches(ServiceGroup group)
    {
        boolean retVal = Pattern.matches(group.getName(), m_serviceGroup);

        if (retVal && StringUtils.isSet(group.getOrganization()))
        {
            retVal = group.getOrganization().equals(m_organization);
        }

        return retVal;
    }

    /**
     * This method will return true if this service container matches the given service container.
     *
     * @param   container  The container it should match.
     *
     * @return  true if this service container matches the given service container.
     */
    public boolean matches(ServiceContainer container)
    {
        return Pattern.matches(container.getName(), m_serviceContainer);
    }
}
