package com.cordys.coe.tools.log4j;

/**
 * Interface that describes the configuration for the log panel.
 *
 * @author  pgussow
 */
public interface ILogViewerConfiguration
{
    /**
     * This method gets the dataformat that should be used for the date.
     *
     * @return  The dataformat that should be used for the date.
     */
    String getDateFormat();

    /**
     * This method returns the page size that should be used.
     *
     * @return  The page size that should be used.
     */
    int getPageSize();
}
