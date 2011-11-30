package com.cordys.coe.tools.snapshot.data.collector;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;

import com.cordys.coe.tools.jmx.MBeanUtils;
import com.cordys.coe.tools.snapshot.config.JMXCounter;
import com.cordys.coe.tools.snapshot.data.IJMXDataCollector;

/**
 * This class collects the memory information from all the different spaces. It collects the following information:
 * <ul>
 * <li>Memory.HeapMemoryUsage</li>
 * <li>Memory.NonHeapMemoryusage</li>
 * <li>MemoryPool.*</li>
 * </ul>
 * 
 * @author localpg
 */
public class MemoryCollector implements IJMXDataCollector
{
    /**
     * @see com.cordys.coe.tools.snapshot.data.IJMXDataCollector#collectData(javax.management.MBeanServerConnection,
     *      com.cordys.coe.tools.snapshot.config.JMXCounter)
     */
    @Override
    public Object collectData(MBeanServerConnection mbsc, JMXCounter counter) throws Exception
    {
        MemoryResult retVal = new MemoryResult();

        String domain = "java.lang:*";

        ObjectName filter = new ObjectName(domain);

        Set<ObjectName> result = mbsc.queryNames(filter, null);

        for (ObjectName on : result)
        {
            // Check if its a middle ware part
            Hashtable<String, String> props = on.getKeyPropertyList();

            if ("MemoryPool".equals(props.get("type")))
            {
                String name = props.get("name");
                String type = (String) MBeanUtils.getAttributeValue(mbsc, on, "Type");
                CompositeData usage = (CompositeData) MBeanUtils.getAttributeValue(mbsc, on, "Usage");

                retVal.addMemoryDetail(MemoryDetail.getInstance(usage, name, type));
            }
            else if ("Memory".equals(props.get("type")))
            {
                // From this one we need the HeapMemoryUsage attribute and the NonHeapMemoryUsage
                CompositeData cdHeap = (CompositeData) MBeanUtils.getAttributeValue(mbsc, on, "HeapMemoryUsage");
                CompositeData cdNonHeap = (CompositeData) MBeanUtils.getAttributeValue(mbsc, on, "NonHeapMemoryUsage");

                retVal.setHeap(MemoryDetail.getInstance(cdHeap, "Memory.HeapMemoryUsage", "HEAP"));
                retVal.setNonHeap(MemoryDetail.getInstance(cdNonHeap, "Memory.NonHeapMemoryUsage", "NON_HEAP"));
            }
        }

        return retVal;
    }

    /**
     * @see com.cordys.coe.tools.snapshot.data.IJMXDataCollector#getResultClasses()
     */
    @Override
    public List<Class<?>> getResultClasses()
    {
        List<Class<?>> retVal = new ArrayList<Class<?>>();

        retVal.add(MemoryResult.class);

        return retVal;
    }
}
