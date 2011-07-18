package com.cordys.coe.tools.orgmanager;

import java.util.LinkedHashMap;

/**
 * This interface can be used to get notifications from the processhandler class.
 *
 * @author  pgussow
 */
public interface IProcessHandlerCallback
{
    /**
     * This method is called when the status of the processors has been updated.
     *
     * @param  lhmOrgsAndProcessors  The list of updated processors.
     */
    void onStatusUpdate(LinkedHashMap<String, LinkedHashMap<String, Processor>> lhmOrgsAndProcessors);
}
