package com.cordys.coe.tools.es.swt;

/**
 * This interface is used to handle Log4J messages.
 *
 * @author  pgussow
 */
public interface IMessageHandler
{
    /**
     * Handle the event that occurred.
     *
     * @param  sSourceName  The name of the source.
     * @param  edDetails    The details of the event.
     */
    void handleMessage(String sSourceName, EventDetails edDetails);
}
