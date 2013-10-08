package com.cordys.coe.tools.snapshot.data.collector;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import com.cordys.coe.tools.jmx.MBeanUtils;
import com.cordys.coe.tools.snapshot.config.JMXCounter;
import com.cordys.coe.tools.snapshot.data.IJMXDataCollector;

/**
 * <p>
 * This class collects the information from the JMX Web Service Inspector if it is installed for the given service container. If
 * not it will not throw any error, but just produce an empty result.
 * </p>
 * <p>
 * If the JMX WSI is available this will grab all the web services that have actually been called. It will read their counter
 * information.
 * </p>
 * 
 * @author pgussow
 */
public class JMXWSICollector implements IJMXDataCollector
{
    /**
     * @see com.cordys.coe.tools.snapshot.data.IJMXDataCollector#collectData(javax.management.MBeanServerConnection,
     *      com.cordys.coe.tools.snapshot.config.JMXCounter)
     */
    @Override
    public Object collectData(MBeanServerConnection mbsc, JMXCounter counter) throws Exception
    {
        JMXWSIResult retVal = new JMXWSIResult();

        // Step 1: Check if the JMX WSI is installed for this service container.
        String domain = "com.cordys:*";
        ObjectName filter = new ObjectName(domain);

        Set<ObjectName> result = mbsc.queryNames(filter, null);

        Map<String, WebServiceInterface> cache = new LinkedHashMap<String, WebServiceInterface>();

        for (ObjectName on : result)
        {
            // Check if its an object which originated from the web service inspector
            Hashtable<String, String> props = on.getKeyPropertyList();

            if (props.containsKey("AppConnector") && "\"Web Service Inspector\"".equals(props.get("AppConnector")))
            {
                retVal.setInstalled(true);
                if (props.containsKey("WebServiceOperation") && !props.get("WebServiceOperation").endsWith(".xsd\""))
                {
                    // This is a real operation, so we want to examine if this thing was called.
                    long totalOcc = MBeanUtils.getLongAttributeValue(mbsc, on, "ctrv_Total_occurrences");
                    if (totalOcc > 0)
                    {
                        long min = MBeanUtils.getLongAttributeValue(mbsc, on, "ctrv_Total_minimum");
                        long max = MBeanUtils.getLongAttributeValue(mbsc, on, "ctrv_Total_maximum");
                        long avg = MBeanUtils.getLongAttributeValue(mbsc, on, "ctrv_Total_average");
                        long total = MBeanUtils.getLongAttributeValue(mbsc, on, "ctrv_Total_total");

                        WebServiceOperation wso = new WebServiceOperation();

                        wso.setName(props.get("WebServiceOperation"));
                        wso.setTotalAverage(avg);
                        wso.setTotalMaximum(max);
                        wso.setTotalMinimum(min);
                        wso.setTotalOccurrences(totalOcc);
                        wso.setTotalTime(total);
                        wso.setWSI(props.get("WebServiceInterface"));

                        if (avg == 0)
                        {
                            // Calculate average
                            wso.setTotalAverage(total / totalOcc);
                        }

                        WebServiceInterface wsi = cache.get(props.get("WebServiceInterface"));
                        if (wsi == null)
                        {
                            wsi = new WebServiceInterface();
                            wsi.setName(props.get("WebServiceInterface"));
                            cache.put(props.get("WebServiceInterface"), wsi);
                            retVal.addWebServiceInterface(wsi);
                        }

                        wsi.addOperation(wso);
                    }
                }
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

        retVal.add(JMXWSIResult.class);

        return retVal;

    }
}
