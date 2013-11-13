package com.cordys.coe.tools.snapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;

import com.cordys.coe.tools.snapshot.config.ActualServiceContainer;
import com.cordys.coe.tools.snapshot.config.Config;
import com.cordys.coe.tools.snapshot.config.JMXCounter;
import com.cordys.coe.tools.snapshot.config.Server;
import com.cordys.coe.tools.snapshot.config.ServiceContainer;
import com.cordys.coe.tools.snapshot.config.ServiceGroup;
import com.cordys.coe.tools.snapshot.data.SnapshotData;
import com.cordys.coe.tools.snapshot.data.SnapshotResult;
import com.cordys.coe.tools.snapshot.data.ThrowableWrapper;
import com.cordys.coe.tools.snapshot.data.handler.DataHandlerFactory;
import com.cordys.coe.util.xml.dom.NiceDOMWriter;

/**
 * This class will get the snapshot of the system. based on the given configuration.
 * 
 * @author localpg
 */
public class SystemSnapshotGrabber
{
    /**
     * Holds the configuration for the JMX grabber.
     */
    private Config m_config;
    /**
     * This one holds all the available components.
     */
    private List<ActualServiceContainer> m_allEntries = new ArrayList<ActualServiceContainer>();

    /**
     * Creates a new SystemSnapshotGrabber object.
     * 
     * @param config The configuration.
     * @throws Exception In case of any exceptions.
     */
    public SystemSnapshotGrabber(Config config) throws Exception
    {
        m_config = config;

        buildRMIRable();
    }

    /**
     * This method will build up the snapshot for the containers that match the configuration.
     * 
     * @return The result containing the snapshot.
     */
    public SnapshotResult buildSnapshot(ISnapshotGrabberProgress progress)
    {
        SnapshotResult retVal = new SnapshotResult();
        
        //Step 1: gather all the objects to get data from so that we can determine the progress that that should be made.
        Map<ActualServiceContainer, ServiceContainer> matchingNodes = new LinkedHashMap<ActualServiceContainer, ServiceContainer>();

        ArrayList<ServiceGroup> groups = m_config.getServiceGroupList();

        for (ServiceGroup group : groups)
        {
            // Step 1: Find all groups that match the configuration.
            for (ActualServiceContainer asc : m_allEntries)
            {
                if (asc.matches(group))
                {
                    ArrayList<ServiceContainer> containers = group.getServiceContainerList();

                    for (ServiceContainer container : containers)
                    {
                        if (asc.matches(container))
                        {
                            matchingNodes.put(asc, container);
                            break;
                        }
                    }
                }
            }
        }
        
        //Now we know how many containers we will question, so we can set the max.
        progress.setMax(matchingNodes.size());
        
        //Step 3: Execute the gathering of data
        int actualProgress = 0;
        for (Entry<ActualServiceContainer, ServiceContainer> entry : matchingNodes.entrySet())
        {
            ActualServiceContainer sc = entry.getKey();
            try
            {
                GrabberData gd = new GrabberData();
                gd.setProgress(actualProgress++);
                gd.setHost(sc.getServer());
                gd.setServiceContainer(sc.getServiceContainer());
                gd.setDetail("Grabbing data for service container " + sc.getServiceContainer());
                
                progress.publishGrabberData(gd);
                
                SnapshotData sd = gatherData(sc, entry.getValue());
                retVal.addSnapshotData(sd);
                
            }
            catch (Exception e)
            {
                retVal.addSnapshotData(new SnapshotData(sc, e));
            }
        }

        return retVal;
    }

    /**
     * This method will gather the data from the given container with the given counters.
     * 
     * @param asc The container to get the details for.
     * @param container The container containing the counters that should be retrieved.
     * @return The captured JMX data.
     * @throws Exception In case of any exceptions
     */
    public SnapshotData gatherData(ActualServiceContainer asc, ServiceContainer container) throws Exception
    {
        SnapshotData retVal = new SnapshotData(asc);

        JMXServiceURL jmxServiceUrl = new JMXServiceURL(asc.getJmxUrl());

        // Now create the MBeanServerConnection.
        String[] credentials = new String[] { m_config.getUsername(), m_config.getPassword() };

        Map<String, String[]> env = new HashMap<String, String[]>();
        env.put("jmx.remote.credentials", credentials);

        JMXConnector jmxConnector = JMXConnectorFactory.connect(jmxServiceUrl, env);

        try
        {
            MBeanServerConnection mbsc = jmxConnector.getMBeanServerConnection();

            ArrayList<JMXCounter> counters = container.getJMXCounterList();

            for (JMXCounter jmxCounter : counters)
            {
                // Collect the data.
                try
                {
                    Object result = jmxCounter.createCollector().collectData(mbsc, jmxCounter);
                    retVal.addCounterValue(jmxCounter, result);
                }
                catch (Throwable e)
                {
                    retVal.addCounterValue(jmxCounter, ThrowableWrapper.getInstance(e));
                }
            }
        }
        finally
        {
            jmxConnector.close();
        }

        return retVal;
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
            throw new Exception("Could not connect to any server defined. The cause is the first exception found.", exceptions.get(0));
        }
    }

    /**
     * Main method.
     * 
     * @param saArguments Commandline arguments.
     */
    public static void main(String[] saArguments)
    {
        try
        {
            JAXBContext context = JAXBContext.newInstance(Config.class);

            // Load the config.
            Config config = (Config) context.createUnmarshaller().unmarshal(
                    SystemSnapshotGrabber.class.getResourceAsStream("config-local.xml"));

            // Recreate the context with all the classes that participate.
            List<Class<?>> classes = config.getCustomDataHandlers();
            classes.addAll(DataHandlerFactory.getKnownClasses());
            classes.add(SnapshotResult.class);
            context = JAXBContext.newInstance(classes.toArray(new Class<?>[0]));

            // Generate the schema
            final List<DOMResult> results = new ArrayList<DOMResult>();
            context.generateSchema(new SchemaOutputResolver() {
                @Override
                public Result createOutput(String namespaceUri, String file) throws IOException
                {
                    DOMResult result = new DOMResult();
                    result.setSystemId(file);
                    results.add(result);

                    results.add(result);

                    return result;
                }
            });

            for (DOMResult result : results)
            {
                System.out.println(NiceDOMWriter.write(result.getNode()));
            }

            // Get the data from Cordys via JMX.
            SystemSnapshotGrabber ssg = new SystemSnapshotGrabber(config);
            SnapshotResult result = ssg.buildSnapshot(new ISnapshotGrabberProgress() {

                @Override
                public void setGrabberProgress(int progress)
                {
                    System.out.println("Progress: " + progress);
                }

                @Override
                public void publishGrabberData(GrabberData data)
                {
                    System.out.println(data.toString());
                }
                
                @Override
                public void setMax(int max)
                {
                    System.out.println("Max ticks will be: " + max);
                }
            });

            // Write the XML to a file
            File outputFile = new File(System.getProperty("TEMP"), "snapshot.xml");
            System.out.println(outputFile.getCanonicalPath());

            FileOutputStream fos = new FileOutputStream(outputFile, false);

            try
            {
                Marshaller m = context.createMarshaller();
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                m.marshal(result, fos);
            }
            finally
            {
                fos.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
