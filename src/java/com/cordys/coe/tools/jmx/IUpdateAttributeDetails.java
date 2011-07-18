package com.cordys.coe.tools.jmx;

import com.cordys.coe.tools.jmx.resources.MBeanAttributeInfoWrapper;

/**
 * The interface to update the attribute details view.
 *
 * @author  pgussow
 */
public interface IUpdateAttributeDetails
{
    /**
     * This method cleans the data in the attribute details screen.
     */
    void clean();

    /**
     * This method should update the details view.
     *
     * @param  baiwAttributeInfo  The attribute to show.
     */
    void updateDetails(MBeanAttributeInfoWrapper baiwAttributeInfo);
}
