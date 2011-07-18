package com.cordys.coe.tools.orgmanager;

import com.cordys.coe.tools.orgmanager.log4j.Appender;

import java.util.ArrayList;

/**
 * This interface is used for the cell editor.
 *
 * @author  pgussow
 */
public interface IAppenderProvider
{
    /**
     * This method returns all appenders.
     *
     * @return  The appenders.
     */
    ArrayList<Appender> getAppenders();
}
