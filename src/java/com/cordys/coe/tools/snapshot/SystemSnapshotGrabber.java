package com.cordys.coe.tools.snapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;

import com.cordys.coe.tools.snapshot.config.ActualServiceContainer;
import com.cordys.coe.tools.snapshot.config.Config;
import com.cordys.coe.tools.snapshot.config.JMXConnectionManager;
import com.cordys.coe.tools.snapshot.config.JMXCounter;
import com.cordys.coe.tools.snapshot.config.ServiceContainer;
import com.cordys.coe.tools.snapshot.config.ServiceGroup;
import com.cordys.coe.tools.snapshot.data.SnapshotData;
import com.cordys.coe.tools.snapshot.data.SnapshotResult;
import com.cordys.coe.tools.snapshot.data.ThrowableWrapper;
import com.cordys.coe.tools.snapshot.data.handler.DataHandlerFactory;
import com.cordys.coe.util.xml.dom.NiceDOMWriter;

/**
 * This class will get the snapshot of the system. based on the given configuration.
 */
public class SystemSnapshotGrabber
{
    /** Holds the configuration for the JMX grabber. */
    private Config m_config;
    /** Holds the registry information */
    private JMXConnectionManager m_registry;
    /** Holds the progress for grabbing the data. */
    private volatile int m_actualProgress = 0;

    /**
     * Creates a new SystemSnapshotGrabber object.
     * 
     * @param config The configuration.
     * @throws Exception In case of any exceptions.
     */
    public SystemSnapshotGrabber(Config config) throws Exception
    {
        m_config = config;

        m_registry = new JMXConnectionManager(config);
    }

    /**
     * This method will build up the snapshot for the containers that match the configuration.
     * 
     * @param progress The progress reporter to use.
     * @return The result containing the snapshot.
     */
    public SnapshotResult buildSnapshot(final ISnapshotGrabberProgress progress)
    {
        SnapshotResult retVal = new SnapshotResult();

        // Step 1: gather all the objects to get data from so that we can determine the progress that that should be
        // made.
        Map<ActualServiceContainer, ServiceContainer> matchingNodes = new LinkedHashMap<ActualServiceContainer, ServiceContainer>();

        ArrayList<ServiceGroup> groups = m_config.getServiceGroupList();

        for (ServiceGroup group : groups)
        {
            // Step 1: Find all groups that match the configuration.
            for (ActualServiceContainer asc : m_registry.getComponents())
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

        // Now we know how many containers we will question, so we can set the max.
        progress.setMax(matchingNodes.size());

        // Create the threadpool
        ExecutorService es = Executors.newFixedThreadPool(10);
        List<Future<SnapshotData>> allFutures = new ArrayList<Future<SnapshotData>>();

        // Step 3: Execute the gathering of data
        m_actualProgress = 0;

        try
        {
            for (final Entry<ActualServiceContainer, ServiceContainer> entry : matchingNodes.entrySet())
            {
                final ActualServiceContainer sc = entry.getKey();

                try
                {
                    Future<SnapshotData> future = es.submit(new Callable<SnapshotData>() {
                        @Override
                        public SnapshotData call() throws Exception
                        {
                            try
                            {
                                // In order to speed up the grabbing of the data we're going to use a thread pool
                                GrabberData gd = new GrabberData();
                                gd.setProgress(m_actualProgress++);
                                gd.setHost(sc.getServer());
                                gd.setServiceContainer(sc.getServiceContainer());
                                gd.setDetail("Grabbing data for service container " + sc.getServiceContainer());

                                progress.publishGrabberData(gd);

                                return gatherData(sc, entry.getValue());
                            }
                            catch (Exception e)
                            {
                                return new SnapshotData(sc, e);
                            }
                        }
                    });
                    allFutures.add(future);
                }
                catch (Exception e)
                {
                    retVal.addSnapshotData(new SnapshotData(sc, e));
                }
            }

            // Step 4: Get the result of all the futures and add them to the response.
            for (Future<SnapshotData> future : allFutures)
            {
                try
                {
                    retVal.addSnapshotData(future.get());
                }
                catch (Exception e)
                {
                    // For now we add it to the first one.
                    retVal.addSnapshotData(new SnapshotData(matchingNodes.keySet().iterator().next(), e));
                }
            }
        }
        finally
        {
            m_registry.closeAllConnections();
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

        JMXConnector jmxConnector = m_registry.connect(asc);

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

        return retVal;
    }

    /**
     * This method gets the components that were found.
     * 
     * @return The components that were found.
     */
    public List<ActualServiceContainer> getComponents()
    {
        return m_registry.getComponents();
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
