package com.cordys.coe.tools.snapshot.data;

import com.cordys.coe.tools.snapshot.config.JMXCounter;

import java.util.List;

import javax.management.MBeanServerConnection;

/**
 * This interface is used to allow custom classes to collect data from JMX and produce a.
 *
 * @author  localpg
 */
public interface IJMXDataCollector
{
    /**
     * This method is called to collect the data.
     *
     * @param   mbsc     The JMX connection to use to get the desired information.
     * @param   counter  The configuration details for this collector.
     *
     * @return  The result object that can go into the result (JAXB object).
     *
     * @throws  Exception  In case of any exceptions
     */
    Object collectData(MBeanServerConnection mbsc, JMXCounter counter)
                throws Exception;

    /**
     * Provides the result classes that can be returned. This is needed to be able to initialize the JAXB context
     * properly.
     *
     * @return  The list of classes that could be returned.
     */
    List<Class<?>> getResultClasses();
}
