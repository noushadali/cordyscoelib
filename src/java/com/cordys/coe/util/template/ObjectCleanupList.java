/**
 * (c) 2006 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util.template;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A list that contains objects that need to be cleaned up at a later time.
 *
 * @param   <T>  List item type.
 *
 * @author  mpoyhone
 */
public class ObjectCleanupList<T extends ICleanupObject> extends ArrayList<T>
{
    /**
     * Constructor for ObjectCleanupList.
     */
    public ObjectCleanupList()
    {
        super();
    }

    /**
     * Constructor for ObjectCleanupList.
     *
     * @param  c
     */
    public ObjectCleanupList(Collection<? extends T> c)
    {
        super(c);
    }

    /**
     * Constructor for ObjectCleanupList.
     *
     * @param  initialCapacity
     */
    public ObjectCleanupList(int initialCapacity)
    {
        super(initialCapacity);
    }

    /**
     * Calls the cleanup method for all objects on this list.
     */
    public void cleanupObjects()
    {
        for (T coObj : this)
        {
            coObj.cleanup();
        }

        clear();
    }
}
