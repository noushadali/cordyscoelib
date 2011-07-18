/**
 * (c) 2006 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util.template;

/**
 * A simple interface for objects that need specific clean-up. This is needed for NOM node wrappers.
 *
 * @author  mpoyhone
 */
public interface ICleanupObject
{
    /**
     * Called when the object needs to be cleaned up.
     */
    void cleanup();
}
