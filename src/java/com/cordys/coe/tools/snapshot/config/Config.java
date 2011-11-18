package com.cordys.coe.tools.snapshot.config;

import com.cordys.coe.tools.snapshot.data.Constants;
import com.cordys.coe.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Holds the configuration for the SystemSnapshot program.
 *
 * @author  localpg
 */
@XmlRootElement(name = "Config", namespace = Constants.NS)
@XmlType(propOrder = { "username", "password", "m_servers", "m_serviceGroups" })
public class Config
{
    /**
     * Holds the server that are part of this cluster.
     */
    @XmlElement(name = "Server", namespace = Constants.NS)
    @XmlElementWrapper(name = "ServerList", namespace = Constants.NS)
    private ArrayList<Server> m_servers = new ArrayList<Server>();
    /**
     * Holds the username to connect to JMX.
     */
    private String m_username;
    /**
     * Holds the password for the JMX user.
     */
    private String m_password;
    /**
     * Holds the service groups that are part of this cluster.
     */
    @XmlElement(name = "ServiceGroup", namespace = Constants.NS)
    @XmlElementWrapper(name = "ServiceGroupList", namespace = Constants.NS)
    private ArrayList<ServiceGroup> m_serviceGroups = new ArrayList<ServiceGroup>();

    /**
     * This method gets the password for the JMX user.
     *
     * @return  The password for the JMX user.
     */
    @XmlElement(name = "Password", namespace = Constants.NS)
    public String getPassword()
    {
        return m_password;
    }

    /**
     * This method sets the password for the JMX user.
     *
     * @param  password  The password for the JMX user.
     */
    public void setPassword(String password)
    {
        m_password = password;
    }

    /**
     * This method gets the username to connect to JMX.
     *
     * @return  The username to connect to JMX.
     */
    @XmlElement(name = "Username", namespace = Constants.NS)
    public String getUsername()
    {
        return m_username;
    }

    /**
     * This method sets the username to connect to JMX.
     *
     * @param  username  The username to connect to JMX.
     */
    public void setUsername(String username)
    {
        m_username = username;
    }

    /**
     * This method returns the servers that are part of this cluster.
     *
     * @return  The servers that are part of this cluster
     */
    public ArrayList<Server> getServerList()
    {
        return m_servers;
    }

    /**
     * This method adds the given server.
     *
     * @param  server  The server to add.
     */
    public void addServer(Server server)
    {
        m_servers.add(server);
    }

    /**
     * This method returns the service groups to include in the dump.
     *
     * @return  The service groups to include in the dump.
     */
    public ArrayList<ServiceGroup> getServiceGroupList()
    {
        return m_serviceGroups;
    }

    /**
     * This method adds the given emailID.
     *
     * @param  serviceGroup  emailID The email ID to add.
     */
    public void addServiceGroup(ServiceGroup serviceGroup)
    {
        m_serviceGroups.add(serviceGroup);
    }

    /**
     * This method gets the list of all the custom datahandler classes that have been configured. This is used to
     * properly initialize the JAXB context.
     *
     * @return  The list of all the custom datahandler classes that have been configured.
     *
     * @throws  Exception  In case of any exceptions
     */
    public List<Class<?>> getCustomDataHandlers()
                                         throws Exception
    {
        ArrayList<Class<?>> retVal = new ArrayList<Class<?>>();

        for (ServiceGroup sg : m_serviceGroups)
        {
            ArrayList<ServiceContainer> containers = sg.getServiceContainerList();

            for (ServiceContainer container : containers)
            {
                ArrayList<JMXCounter> counters = container.getJMXCounterList();

                for (JMXCounter counter : counters)
                {
                    String handler = counter.getDataHandler();

                    if (StringUtils.isSet(handler))
                    {
                        retVal.add(Class.forName(handler));
                    }

                    // Also add the classes used by the data handler
                    retVal.addAll(counter.createCollector().getResultClasses());
                }
            }
        }

        return retVal;
    }

    /**
     * Main method.
     *
     * @param  saArguments  Commandline arguments.
     */
    public static void main(String[] saArguments)
    {
        try
        {
            JAXBContext context = JAXBContext.newInstance(Config.class);

            Config cfg = new Config();
            cfg.setUsername("admin");
            cfg.setPassword("CSCmanager");
            cfg.addServer(new Server("localhost", 1099));

            ServiceGroup sg = new ServiceGroup();
            sg.setName(".+");

            cfg.addServiceGroup(sg);

            ServiceContainer sc = new ServiceContainer();
            sc.setName(".+");

            sg.addServiceContainer(sc);

            JMXCounter c = new JMXCounter();
            c.setDomain("java.lang");
            c.addNameProperty(new Property("type", "Runtime"));
            c.setProperty("Name");
            c.setCounterType(EJMXCounterType.ATTRIBUTE);

            sc.addJMXCounter(c);

            c = new JMXCounter();
            c.setDomain("java.lang");
            c.addNameProperty(new Property("type", "Memory"));
            c.setProperty("HeapMemoryUsage");
            c.setCounterType(EJMXCounterType.ATTRIBUTE);

            sc.addJMXCounter(c);

            c = new JMXCounter();
            c.setDomain("java.lang");
            c.addNameProperty(new Property("type", "MemoryPool"));
            c.addNameProperty(new Property("name", "Perm Gen"));
            c.setProperty("Usage");
            c.setCounterType(EJMXCounterType.ATTRIBUTE);

            sc.addJMXCounter(c);

            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(cfg, System.out);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
