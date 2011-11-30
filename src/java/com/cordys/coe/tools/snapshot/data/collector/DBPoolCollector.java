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
 * This class collects all the information about all database connection pools that are running in the current connector
 * 
 * @author localpg
 */
public class DBPoolCollector implements IJMXDataCollector
{

    /**
     * @see com.cordys.coe.tools.snapshot.data.IJMXDataCollector#collectData(javax.management.MBeanServerConnection,
     *      com.cordys.coe.tools.snapshot.config.JMXCounter)
     */
    @Override
    public Object collectData(MBeanServerConnection mbsc, JMXCounter counter) throws Exception
    {
        DBPoolResult retVal = new DBPoolResult();

        String domain = "com.cordys:*";

        ObjectName filter = new ObjectName(domain);

        Set<ObjectName> result = mbsc.queryNames(filter, null);

        for (ObjectName on : result)
        {
            // Check if its a middle ware part
            Hashtable<String, String> props = on.getKeyPropertyList();

            if (props.containsKey("DBConnectionPool"))
            {
                String name = props.get("DBConnectionPool");
                if (name.startsWith("\""))
                {
                    name = name.substring(1, name.length() - 1);
                }
                
                DBConnectionPoolInfo pi = new DBConnectionPoolInfo();
                pi.setName(name);
                retVal.addDBConnectionPoolInfo(pi);

                // Collect the data
                pi.setActiveRead(MBeanUtils.getIntAttributeValue(mbsc, on, "ctrv_numOfCachedReadConnections_current"));
                pi.setMaximumRead(MBeanUtils.getIntAttributeValue(mbsc, on, "maximumReadConnections"));
                pi.setMinimumRead(MBeanUtils.getIntAttributeValue(mbsc, on, "minimumReadConnections"));

                pi.setActiveWrite(MBeanUtils.getIntAttributeValue(mbsc, on, "ctrv_numOfCachedWriteConnections_current"));
                pi.setMaximumWrite(MBeanUtils.getIntAttributeValue(mbsc, on, "maximumWriteConnections"));
                pi.setMinimumWrite(MBeanUtils.getIntAttributeValue(mbsc, on, "minimumWriteConnections"));

                // Connection usage
                CompositeData cd = (CompositeData) MBeanUtils.getAttributeValue(mbsc, on, "ctr_readConnectionUsageDuration");
                pi.setReadUsage(DBConnectionPoolInfo.Usage.getInstance(cd));
                cd = (CompositeData) MBeanUtils.getAttributeValue(mbsc, on, "ctr_writeConnectionUsageDuration");
                pi.setWriteUsage(DBConnectionPoolInfo.Usage.getInstance(cd));

                // Connection wait time
                cd = (CompositeData) MBeanUtils.getAttributeValue(mbsc, on, "ctr_readConnectionWait");
                pi.setReadWaitTime(DBConnectionPoolInfo.WaitTime.getInstance(cd));
                cd = (CompositeData) MBeanUtils.getAttributeValue(mbsc, on, "ctr_writeConnectionWait");
                pi.setWriteWaitTime(DBConnectionPoolInfo.WaitTime.getInstance(cd));

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

        retVal.add(DBPoolResult.class);

        return retVal;
    }

}
