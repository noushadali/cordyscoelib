package com.cordys.coe.tools.snapshot;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.cordys.coe.tools.snapshot.config.ActualServiceContainer;
import com.cordys.coe.tools.snapshot.config.JMXConnectionManager;
import com.cordys.coe.tools.snapshot.config.ServiceContainer;
import com.cordys.coe.tools.snapshot.config.ServiceGroup;
import com.cordys.coe.util.swing.MessageBoxUtil;

/**
 * Holds the Class ResetCounterTask.
 */
public class ResetCounterTask extends SwingWorker<Void, GrabberData> implements ISnapshotGrabberProgress
{
    /** Holds the parent system snapshot */
    private final SystemSnapshot m_systemSnapshot;
    /** Holds the progress monitor that is to be used. */
    private ProgressMonitor m_pm;
    /** Holds the progress for grabbing the data. */
    private volatile int m_actualProgress = 0;

    /**
     * Instantiates a new grabber task.
     * 
     * @param pm The progress monitor to be used.
     * @param systemSnapshot TODO
     */
    ResetCounterTask(SystemSnapshot systemSnapshot, ProgressMonitor pm)
    {
        m_systemSnapshot = systemSnapshot;
        m_pm = pm;
    }

    /**
     * @see javax.swing.SwingWorker#doInBackground()
     */
    @Override
    protected Void doInBackground() throws Exception
    {
        try
        {
            if (m_systemSnapshot.getConfig() == null)
            {
                throw new Exception("You must first load a configuration");
            }

            final JMXConnectionManager rrd = new JMXConnectionManager(m_systemSnapshot.getConfig());
            final List<String> errors = new ArrayList<String>();

            // Step 1: gather all the objects to get data from so that we can determine the progress that that should be
            // made.
            Map<ActualServiceContainer, ServiceContainer> matchingNodes = new LinkedHashMap<ActualServiceContainer, ServiceContainer>();

            ArrayList<ServiceGroup> groups = m_systemSnapshot.getConfig().getServiceGroupList();

            for (ServiceGroup group : groups)
            {
                // Step 1: Find all groups that match the configuration.
                for (ActualServiceContainer asc : rrd.getComponents())
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
            setMax(matchingNodes.size());

            // Create the threadpool
            ExecutorService es = Executors.newFixedThreadPool(10);
            List<Future<Void>> allFutures = new ArrayList<Future<Void>>();

            m_actualProgress = 0;
            for (final ActualServiceContainer asc : matchingNodes.keySet())
            {
                // Do the actual work in the executor service.
                Future<Void> f = es.submit(new Callable<Void>() {

                    @Override
                    public Void call() throws Exception
                    {
                        GrabberData gd = new GrabberData();
                        gd.setProgress(m_actualProgress++);
                        gd.setHost(asc.getServer());
                        gd.setServiceContainer(asc.getServiceContainer());
                        gd.setDetail("Resetting counter for " + asc.getServiceContainer());

                        publishGrabberData(gd);

                        clearActualCounter(rrd, errors, asc);
                        return null;
                    }

                });
                allFutures.add(f);

            }
            
            //Wait for all resets to finish
            for (Future<Void> f : allFutures)
            {
                f.get();
            }

            // Display errors if any
            if (errors.size() > 0)
            {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run()
                    {
                        StringBuilder sb = new StringBuilder(1024);

                        for (String error : errors)
                        {
                            sb.append("\n").append(error);
                        }

                        MessageBoxUtil.showInformation("Error resetting counter for:" + sb);
                    }
                });
            }
        }
        catch (final Exception e)
        {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run()
                {
                    MessageBoxUtil.showError("Error resetting all counters: " + e.getLocalizedMessage(), e);
                }
            });
        }

        return null;
    }

    /**
     * Clear actual counter.
     * 
     * @param rrd The registry.
     * @param errors The list to register errors.
     * @param asc The container to reset.
     */
    private boolean clearActualCounter(JMXConnectionManager rrd, List<String> errors, ActualServiceContainer asc)
    {
        boolean done = false;
        try
        {
            JMXConnector connection = rrd.connect(asc);
            MBeanServerConnection mbsc = connection.getMBeanServerConnection();

            // Step 1: Check if the JMX WSI is installed for this service container.
            String domain = "com.cordys:*";
            ObjectName filter = new ObjectName(domain);

            Set<ObjectName> result = mbsc.queryNames(filter, null);

            for (ObjectName on : result)
            {
                // Check if its an object which originated from the web service inspector
                Hashtable<String, String> props = on.getKeyPropertyList();

                if (props.containsKey("AppConnector") && "\"Web Service Inspector\"".equals(props.get("AppConnector"))
                        && !props.containsKey("WebServiceOperation") && !props.contains("WebServiceOperation"))
                {
                    // Now we need to execute the resetCounters operation.
                    MBeanInfo info = mbsc.getMBeanInfo(on);
                    MBeanOperationInfo[] ops = info.getOperations();
                    for (MBeanOperationInfo op : ops)
                    {
                        if ("resetCounters".equals(op.getName()))
                        {
                            mbsc.invoke(on, op.getName(), new Object[] { Boolean.TRUE }, new String[] { "java.lang.Boolean" });
                            done = true;
                            break;
                        }
                    }
                }
            }

        }
        catch (Exception e)
        {
            errors.add(asc.getJmxUrl() + ": " + e.getLocalizedMessage());
        }

        return done;
    }

    /**
     * @see com.cordys.coe.tools.snapshot.ISnapshotGrabberProgress#setGrabberProgress(int)
     */
    @Override
    public void setGrabberProgress(int progress)
    {
        setProgress(progress);
    }

    /**
     * @see javax.swing.SwingWorker#done()
     */
    @Override
    protected void done()
    {
        m_pm.close();
    }

    /**
     * @see com.cordys.coe.tools.snapshot.ISnapshotGrabberProgress#setMax(int)
     */
    @Override
    public void setMax(int max)
    {
        m_pm.setMaximum(max);
    }

    /**
     * @see com.cordys.coe.tools.snapshot.ISnapshotGrabberProgress#publishGrabberData(com.cordys.coe.tools.snapshot.GrabberData)
     */
    @Override
    public void publishGrabberData(GrabberData data)
    {
        if (data.getProgress() > 100)
        {
            setProgress(100);
        }
        else
        {
            setProgress(data.getProgress());
        }
        publish(data);
    }

    /**
     * @see javax.swing.SwingWorker#process(java.util.List)
     */
    @Override
    protected void process(List<GrabberData> chunks)
    {
        if ((chunks != null) && (chunks.size() > 0))
        {
            GrabberData gd = chunks.get(chunks.size() - 1);

            m_pm.setNote(gd.toString());
        }
    }
}