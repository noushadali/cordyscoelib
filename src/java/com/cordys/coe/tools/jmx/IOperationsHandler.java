package com.cordys.coe.tools.jmx;

import com.cordys.coe.tools.jmx.resources.MBeanOperationInfoWrapper;

/**
 * This interface describes the operations handler, used to display the details of the currently
 * selected operation.
 *
 * @author  pgussow
 */
public interface IOperationsHandler
{
    /**
     * This method cleans the data in the operation details screen.
     */
    void clean();

    /**
     * Tells the object to update with the newly selected operation.
     *
     * @param  boiwOperation  The operation that is selected.
     */
    void updateDetails(MBeanOperationInfoWrapper boiwOperation);
}
