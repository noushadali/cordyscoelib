package com.cordys.coe.tools.snapshot.config;

import java.io.IOException;
import java.net.URLDecoder;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

/**
 * This class wraps around the RMI registry and retrieves all information from the registry.
 * 
 * @author pgussow
 */
public class JMXConnectionManager
{
    /** Holds the configuration used to wrap around the registry. */
    private Config m_config;
    /** This one holds all the available components. */
    private List<ActualServiceContainer> m_allEntries = new ArrayList<ActualServiceContainer>();
    /** Holds the connections that have already been made. Note that this is a Thread-safe map */
    Map<String, JMXConnector> m_connections = new ConcurrentHashMap<String, JMXConnector>();

    /**
     * Instantiates a new RMI registry details.
     * 
     * @param config The config to connect to the registry.
     * @throws Exception In case the building up of the registry fails.
     */
    public JMXConnectionManager(Config config) throws Exception
    {
        m_config = config;

        buildRMIRable();
    }

    /**
     * This method gets the configuration used to wrap around the registry.
     * 
     * @return The configuration used to wrap around the registry.
     */
    public Config getConfig()
    {
        return m_config;
    }

    /**
     * This method sets the configuration used to wrap around the registry.
     * 
     * @param config The configuration used to wrap around the registry.
     */
    public void setConfig(Config config)
    {
        m_config = config;
    }

    /**
     * This method gets the components that were found.
     * 
     * @return The components that were found.
     */
    public List<ActualServiceContainer> getComponents()
    {
        return m_allEntries;
    }

    /**
     * This method will build the table with all components that can be connected to.
     * 
     * @throws Exception In case of any exceptions.
     */
    public void buildRMIRable() throws Exception
    {
        m_allEntries.clear();

        ArrayList<Server> servers = m_config.getServerList();
        List<Exception> exceptions = new ArrayList<Exception>();

        boolean oneConnected = false;

        for (Server server : servers)
        {
            // Try to get the list of managed components via the rmi registry
            try
            {
                Registry rRegistry = LocateRegistry.getRegistry(server.getName(), server.getPort());
                String[] saEntries = rRegistry.list();

                for (int iCount = 0; iCount < saEntries.length; iCount++)
                {
                    String sEntry = saEntries[iCount];

                    String url = "service:jmx:rmi:///jndi/rmi://" + server.getName() + ":" + server.getPort() + "/" + sEntry;

                    if (sEntry.startsWith("cordys/"))
                    {
                        // It's a Cordys URL, so analyze it.
                        String sAnalysis = URLDecoder.decode(sEntry, "UTF8");

                        // Strip the cordys/
                        sAnalysis = sAnalysis.substring("cordys/".length());

                        ActualServiceContainer asc = new ActualServiceContainer();
                        asc.setServer(server.getName());
                        m_allEntries.add(asc);

                        if (sAnalysis.indexOf("#") > -1)
                        {
                            // It's a service container
                            String[] saOthers = sAnalysis.split("#");

                            asc.setJmxUrl(url);
                            asc.setServiceContainer(saOthers[2]);
                            asc.setServiceGroup(saOthers[1]);
                            asc.setOrganization(saOthers[0]);
                        }
                        else
                        {
                            asc.setJmxUrl(url);
                            asc.setServiceContainer(sAnalysis);
                            asc.setServiceGroup("");
                            asc.setOrganization("");
                        }
                    }
                }

                // Sort the entries.
                Collections.sort(m_allEntries, new Comparator<ActualServiceContainer>() {
                    @Override
                    public int compare(ActualServiceContainer o1, ActualServiceContainer o2)
                    {
                        // We'll compare the name of the organization, then the service group, then the container.
                        int retVal = o1.getOrganization().toLowerCase().compareTo(o2.getOrganization().toLowerCase());

                        if (retVal == 0)
                        {
                            retVal = o1.getServiceGroup().toLowerCase().compareTo(o2.getServiceGroup().toLowerCase());
                        }

                        if (retVal == 0)
                        {
                            retVal = o1.getServiceContainer().toLowerCase().compareTo(o2.getServiceContainer().toLowerCase());
                        }

                        if (retVal == 0)
                        {
                            retVal = o1.getServer().toLowerCase().compareTo(o2.getServer().toLowerCase());
                        }

                        return retVal;
                    }
                });

                oneConnected = true;
            }
            catch (Exception e)
            {
                exceptions.add(e);
            }
        }

        if (!oneConnected)
        {
            throw new Exception("Could not connect to any server defined. The cause is the first exception found.",
                    exceptions.get(0));
        }
    }

    /**
     * This method creates the connection to the actual process. The connections are cached. Also the connection is checked to
     * make sure it is still active. If the connection is broken it will re-connect.
     * 
     * @param asc The wrapper around the information for the connection.
     * @return The connection.
     * @throws Exception In case of any connection errors.
     */
    public JMXConnector connect(ActualServiceContainer asc) throws Exception
    {
        JMXConnector retVal = m_connections.get(asc.getJmxUrl());

        if (retVal == null)
        {
            try
            {
                // No connection yet.
                JMXServiceURL jmxServiceUrl = new JMXServiceURL(asc.getJmxUrl());

                // Now create the MBeanServerConnection.
                String[] credentials = new String[] { m_config.getUsername(), m_config.getPassword() };

                Map<String, String[]> env = new HashMap<String, String[]>();
                env.put("jmx.remote.credentials", credentials);

                retVal = JMXConnectorFactory.connect(jmxServiceUrl, env);
                m_connections.put(asc.getJmxUrl(), retVal);
            }
            catch (Exception e)
            {
                throw new Exception("Error connecting to " + asc.getJmxUrl(), e);
            }
        }

        return retVal;
    }

    /**
     * Close all connections.
     */
    public void closeAllConnections()
    {
        for (JMXConnector c : m_connections.values())
        {
            try
            {
                c.close();
            }
            catch (IOException e)
            {
                // Ignore the closing.
            }
        }

        m_connections.clear();
    }
}
