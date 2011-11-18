package com.cordys.coe.tools.snapshot.data.collector;

import com.cordys.coe.tools.jmx.MBeanUtils;
import com.cordys.coe.tools.snapshot.config.JMXCounter;
import com.cordys.coe.tools.snapshot.config.Parameter;
import com.cordys.coe.tools.snapshot.data.IJMXDataCollector;
import com.cordys.coe.tools.snapshot.data.handler.DataHandlerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

/**
 * This class is the default way for collecting the data from JMX.
 *
 * @author  localpg
 */
public class DefaultDataCollector
    implements IJMXDataCollector
{
    /**
     * @see  com.cordys.coe.tools.snapshot.data.IJMXDataCollector#collectData(javax.management.MBeanServerConnection, com.cordys.coe.tools.snapshot.config.JMXCounter)
     */
    @Override public Object collectData(MBeanServerConnection mbsc, JMXCounter jmxCounter)
                                 throws Exception
    {
        Object retVal = null;

        // Build up the filter
        ObjectName on = new ObjectName(jmxCounter.getDomain(), jmxCounter.buildList());
        Set<ObjectName> beanInfo = mbsc.queryNames(on, null);

        // There should be only one result.
        if (beanInfo.size() > 0)
        {
            ObjectName objectName = beanInfo.iterator().next();

            // Now we have the proper object name, so we can get the actual data.
            MBeanInfo mbeanInfo = mbsc.getMBeanInfo(objectName);

            switch (jmxCounter.getCounterType())
            {
                case ATTRIBUTE:

                    retVal = getAttributeValue(mbsc, jmxCounter, objectName, mbeanInfo);
                    break;

                case OPERATION:

                    retVal = getOperationValue(mbsc, jmxCounter, objectName, mbeanInfo);
                    break;
            }
        }

        return retVal;
    }

    /**
     * This method executes the configured operation and gets the response object.
     *
     * @param   mbsc        The JMX connection to use.
     * @param   jmxCounter  The definition of the JMX counter.
     * @param   objectName  The name of the object.
     * @param   mbeanInfo   The information about the attribute.
     *
     * @return  The parsed response object based on the value of the attribute.
     *
     * @throws  Exception  In case of any exceptions
     */
    private Object getOperationValue(MBeanServerConnection mbsc, JMXCounter jmxCounter, ObjectName objectName,
                                     MBeanInfo mbeanInfo)
                              throws Exception
    {
        Object retVal = null;

        MBeanOperationInfo[] ai = mbeanInfo.getOperations();

        for (MBeanOperationInfo operation : ai)
        {
            if (operation.getName().equals(jmxCounter.getProperty()))
            {
                // Found the property that we want the value of!
                String methodName = operation.getName();

                // Build up the parameters for the method invocation
                MBeanParameterInfo[] paramInfos = operation.getSignature();
                Object[] parameterValues = new Object[paramInfos.length];
                List<Parameter> parameterConfiguredValues = jmxCounter.getParameterList();

                if (parameterConfiguredValues.size() != parameterValues.length)
                {
                    throw new Exception("Operation " + methodName +
                                        " doe not have enough parameter values. Should be " + parameterValues.length +
                                        " parameter values available instead of " + parameterConfiguredValues.size());
                }

                for (int count = 0; count < paramInfos.length; count++)
                {
                    parameterValues[count] = MBeanUtils.getParameterValue(paramInfos[count],
                                                                          parameterConfiguredValues.get(count)
                                                                          .getValue());
                }

                // Do the actual invoke of the method.
                Object result;

                if (parameterValues.length > 0)
                {
                    String[] paramSig = new String[paramInfos.length];

                    for (int i = 0; i < paramSig.length; i++)
                    {
                        paramSig[i] = paramInfos[i].getType();
                    }
                    result = mbsc.invoke(objectName, methodName, parameterValues, paramSig);
                }
                else
                {
                    result = mbsc.invoke(objectName, methodName, new Object[0], new String[0]);
                }

                // Parse the result object
                if (result != null)
                {
                    retVal = DataHandlerFactory.handleData(result, jmxCounter.getDataHandler());
                }

                break; // Break the for loop to find the proper operation to invoke.
            }
        }
        return retVal;
    }

    /**
     * This method gets the data from a JMX attribute.
     *
     * @param   mbsc        The JMX connection to use.
     * @param   jmxCounter  The definition of the JMX counter.
     * @param   objectName  The name of the object.
     * @param   mbeanInfo   The information about the attribute.
     *
     * @return  The parsed response object based on the value of the attribute.
     *
     * @throws  Exception  In case of any exceptions
     */
    private Object getAttributeValue(MBeanServerConnection mbsc, JMXCounter jmxCounter, ObjectName objectName,
                                     MBeanInfo mbeanInfo)
                              throws Exception
    {
        Object retVal = null;

        MBeanAttributeInfo[] attributes = mbeanInfo.getAttributes();

        for (MBeanAttributeInfo attribute : attributes)
        {
            if (attribute.getName().equals(jmxCounter.getProperty()))
            {
                // Found the property that we want the value of!
                Object value = mbsc.getAttribute(objectName, attribute.getName());

                // Based on the value we need to create different result object structures.
                Object parsedValue = DataHandlerFactory.handleData(value, jmxCounter.getDataHandler());
                retVal = parsedValue;

                break;
            }
        }

        return retVal;
    }

    /**
     * @see  com.cordys.coe.tools.snapshot.data.IJMXDataCollector#getResultClasses()
     */
    @Override public List<Class<?>> getResultClasses()
    {
        // No need to fill it. Already covered by the DatahandlerFactory.
        return new ArrayList<Class<?>>();
    }
}
