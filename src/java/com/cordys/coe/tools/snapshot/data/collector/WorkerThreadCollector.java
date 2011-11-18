package com.cordys.coe.tools.snapshot.data.collector;

import com.cordys.coe.tools.jmx.MBeanUtils;
import com.cordys.coe.tools.snapshot.config.JMXCounter;
import com.cordys.coe.tools.snapshot.data.IJMXDataCollector;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

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
            // Check if its a middle ware part
            Hashtable<String, String> props = on.getKeyPropertyList();

            if (props.containsKey("SOAPConnector") && props.containsKey("MiddlewareWrapper") &&
                    props.containsKey("Dispatcher"))
            {
                // Found a middle ware pool with a dispatcher.
                DispatcherInfo di = new DispatcherInfo();

                di.setActiveWorkers(MBeanUtils.getIntAttributeValue(mbsc, on, "ctrv_numActiveWorkers_current"));
                di.setCurrentWorkers(MBeanUtils.getIntAttributeValue(mbsc, on, "ctrv_currentNumWorkers_current"));
                di.setIdleWorkers(MBeanUtils.getIntAttributeValue(mbsc, on, "ctrv_numIdleWorkers_current"));

                di.setMinConcurrentWorkers(MBeanUtils.getIntAttributeValue(mbsc, on, "minConcurrentWorkers"));
                di.setMaxConcurrentWorkers(MBeanUtils.getIntAttributeValue(mbsc, on, "maxConcurrentWorkers"));

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
