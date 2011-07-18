package com.cordys.coe.tools.es.swt.filter;

/**
 * Interface for changing filters.
 *
 * @author  pgussow
 */
public interface ILog4JFilterListViewer
{
    /**
     * Update the view to reflect the fact that a task was added to the task list.
     *
     * @param  elfFilter  The filter.
     */
    void addLog4JFilter(ESCLog4JFilter elfFilter);

    /**
     * Update the view to reflect the fact that a task was removed from the task list.
     *
     * @param  elfFilter  The filter.
     */
    void removeLog4JFilter(ESCLog4JFilter elfFilter);

    /**
     * Update the view to reflect the fact that one of the tasks was modified.
     *
     * @param  elfFilter  The filter.
     */
    void updateLog4JFilter(ESCLog4JFilter elfFilter);
}
