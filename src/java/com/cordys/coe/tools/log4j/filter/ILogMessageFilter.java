package com.cordys.coe.tools.log4j.filter;

import com.cordys.coe.tools.es.Log4JLogEvent;

/**
 * This interface describes the filter possibilities that can be put on the panel.
 *
 * @author  pgussow
 */
public interface ILogMessageFilter
{
    /**
     * This method returns the date format that should be used to display the time of the event.
     *
     * @return  The date format that should be used to display the time of the event.
     */
    String getDateFormat();

    /**
     * This method returns whether or not the current event should be displayed or not.
     *
     * @param   lleEvent  The current log event.
     *
     * @return  true if the event should be displayed. Otherwise false.
     */
    boolean shouldDisplay(Log4JLogEvent lleEvent);
}
