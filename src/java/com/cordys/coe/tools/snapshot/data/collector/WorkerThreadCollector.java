package com.cordys.coe.tools.snapshot.data.collector;

import com.cordys.coe.tools.jmx.MBeanUtils;
import com.cordys.coe.tools.snapshot.config.JMXCounter;
import com.cordys.coe.tools.snapshot.data.IJMXDataCollector;
import com.cordys.coe.tools.snapshot.data.handler.ThreadInfo;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;

/**
 * This class collects the information about the middleware threads that are available.
 *
 * <p>The information that it collects is all info from the different worker pools. So as soon as there is a SOAP
 * Connector it will get the following data:</p>
 *
 * <ul>
 *   <li>maxConcurrentWorkers - The maximum number of workers for this thread.</li>
 *   <li>ctrv_currentNumWorkers_current - The current number of worker threads that are in the pool.</li>
 *   <li>ctrv_numIdleWorkers_current - The number of workers that are currently idle.</li>
 *   <li>ctrv_numActiveWorkers_current - The number of currently active workers.</li>
 * </ul>
 *
 * @author  localpg
 */
public class WorkerThreadCollector
    implements IJMXDataCollector
{
    /**
     * @see  com.cordys.coe.tools.snapshot.data.IJMXDataCollector#collectData(javax.management.MBeanServerConnection, com.cordys.coe.tools.snapshot.config.JMXCounter)
     */
    @Override public Object collectData(MBeanServerConnection mbsc, JMXCounter counter)
                                 throws Exception
    {
        WorkerThreadResult retVal = new WorkerThreadResult();

        String domain = "com.cordys:*";

        ObjectName filter = new ObjectName(domain);

        Set<ObjectName> result = mbsc.queryNames(filter, null);

        for (ObjectName on : result)
        {
            // Check if its a middle ware thread pool. We'll ignore the LDAP cache thread pool as it not of interest. 
            Hashtable<String, String> props = on.getKeyPropertyList();

            if (props.containsKey("SOAPConnector") && props.containsKey("MiddlewareWrapper") &&
                    props.containsKey("Dispatcher") && !props.containsKey("LDAPCache"))
            {
            	String dispatcherName = props.get("Dispatcher");
            	//The result is an escaped Java string.   
            	
            	//The \\ are not escaped properly, so we'll compensate
            	dispatcherName = dispatcherName.replaceAll("\\\\\\\\\\\\\\\\", "\\\\\\\\");
            	dispatcherName = dispatcherName.replaceAll("\"", "");

                // Found a middle ware pool with a dispatcher.
                DispatcherInfo di = new DispatcherInfo();

                di.setName(dispatcherName);
                di.setActiveWorkers(MBeanUtils.getIntAttributeValue(mbsc, on, "ctrv_numActiveWorkers_current"));
                di.setCurrentWorkers(MBeanUtils.getIntAttributeValue(mbsc, on, "ctrv_currentNumWorkers_current"));
                di.setIdleWorkers(MBeanUtils.getIntAttributeValue(mbsc, on, "ctrv_numIdleWorkers_current"));

                di.setMinConcurrentWorkers(MBeanUtils.getIntAttributeValue(mbsc, on, "minConcurrentWorkers"));
                di.setMaxConcurrentWorkers(MBeanUtils.getIntAttributeValue(mbsc, on, "maxConcurrentWorkers"));
                
                //Now we need to get the threads that belong to this dispather information.
                ObjectName threading = new ObjectName("java.lang", "type", "Threading");
                CompositeData[] threadInfo = (CompositeData[]) mbsc.invoke(threading, "dumpAllThreads", new Object[]{true, true}, new String[]{"boolean", "boolean"});
                
                for (CompositeData cd : threadInfo)
				{
					//Check to see if this thread belongs to this dispatcher
					if (((String)cd.get("threadName")).startsWith(dispatcherName + "/Worker"))
                	{
                		ThreadInfo ti = new ThreadInfo();
                		ti.parseData(cd);
                		di.addThreadInfo(ti);
                	}
				}

                retVal.addDispatcherInfo(di);
            }
            //Check for the long lived thread pool
            else if ("\"Cordys BPM SOAP Processor\"".equals(props.get("AppConnector")) && "\"Business Process Engine\"".equals(props.get("processEngine")))
            {
                String dispatcherName = props.get("processEngine").replaceAll("\"+", "");
                
                DispatcherInfo di = new DispatcherInfo();

                di.setName(dispatcherName);
                di.setActiveWorkers(MBeanUtils.getIntAttributeValue(mbsc, on, "ctrv_numActiveThreads_current"));
                di.setCurrentWorkers(MBeanUtils.getIntAttributeValue(mbsc, on, "ctrv_threadPoolSize_current"));
                di.setIdleWorkers(MBeanUtils.getIntAttributeValue(mbsc, on, "ctrv_numFreeThreads_current"));

                di.setMinConcurrentWorkers(1);
                di.setMaxConcurrentWorkers(MBeanUtils.getIntAttributeValue(mbsc, on, "ctrv_threadPoolSize_current"));
                
                //Now we need to get the threads that belong to this dispather information.
                ObjectName threading = new ObjectName("java.lang", "type", "Threading");
                CompositeData[] threadInfo = (CompositeData[]) mbsc.invoke(threading, "dumpAllThreads", new Object[]{true, true}, new String[]{"boolean", "boolean"});
                
                for (CompositeData cd : threadInfo)
                {
                    //Check to see if this thread belongs to this dispatcher
                    if (((String)cd.get("threadName")).startsWith("BPMEngine_LongLivedProcess_ThreadPool/WorkerThread"))
                    {
                        ThreadInfo ti = new ThreadInfo();
                        ti.parseData(cd);
                        di.addThreadInfo(ti);
                    }
                }

                retVal.addDispatcherInfo(di);

            }
        }

        return retVal;
    }

    /**
     * @see  com.cordys.coe.tools.snapshot.data.IJMXDataCollector#getResultClasses()
     */
    @Override public List<Class<?>> getResultClasses()
    {
        List<Class<?>> retVal = new ArrayList<Class<?>>();

        retVal.add(WorkerThreadResult.class);

        return retVal;
    }
}
