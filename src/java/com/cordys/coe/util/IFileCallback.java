/**
 * (c) 2007 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util;

import java.io.File;

/**
 * Callback interface for FileUtils.recurseFiles() methods.
 *
 * @author  mpoyhone
 */
public interface IFileCallback
{
    /**
     * Called when a file is found.
     *
     * @param   fFile          File object.
     * @param   fRelativeFile  File relative to the root folder.
     *
     * @return  Callback result.
     */
    EResult onFile(File fFile, File fRelativeFile);

    /**
     * Called before a folder is entered.
     *
     * @param   fFolder          Folder object.
     * @param   fRelativeFolder  Folder relative to the root folder.
     *
     * @return  Callback result.
     */
    EResult onBeforeFolder(File fFolder, File fRelativeFolder);

    /**
     * Called after a folder is exited.
     *
     * @param   fFolder          Folder object.
     * @param   fRelativeFolder  Folder relative to the root folder.
     *
     * @return  Callback result.
     */
    EResult onAfterFolder(File fFolder, File fRelativeFolder);

    /**
     * Describes the valid callback return values.
     *
     * @author  mpoyhone
     */
    public enum EResult
    {
        /**
         * Recursion can continue normally.
         */
        CONTINUE,
        /**
         * Recursion must be stopped after this file/folder.
         */
        STOP,
        /**
         * Contents of this folder must be skipped. This
         * has no effect for the handleFile callback method.
         */
        SKIP;
    }
}
