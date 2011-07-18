/**
 * (c) 2008 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util;

import java.io.File;

/**
 * IFileCallback which forwards the methods to another callback. This is used to implement only some
 * of the methods.
 *
 * @author  mpoyhone
 */
public class ForwardingFileCallback
    implements IFileCallback
{
    /**
     * Contains the destination callback. Can be <code>null</code>.
     */
    private IFileCallback fcCallback;

    /**
     * Constructor for ForwardingFileCallback.
     *
     * @param  fcCallback  The destination callback. Can be <code>null</code>.
     */
    public ForwardingFileCallback(IFileCallback fcCallback)
    {
        this.fcCallback = fcCallback;
    }

    /**
     * @see  com.cordys.coe.util.IFileCallback#onAfterFolder(java.io.File, java.io.File)
     */
    public EResult onAfterFolder(File folder, File relFolder)
    {
        if (fcCallback != null)
        {
            return fcCallback.onAfterFolder(folder, relFolder);
        }

        return IFileCallback.EResult.CONTINUE;
    }

    /**
     * @see  com.cordys.coe.util.IFileCallback#onBeforeFolder(java.io.File, java.io.File)
     */
    public EResult onBeforeFolder(File folder, File relFolder)
    {
        if (fcCallback != null)
        {
            return fcCallback.onBeforeFolder(folder, relFolder);
        }

        return IFileCallback.EResult.CONTINUE;
    }

    /**
     * @see  com.cordys.coe.util.IFileCallback#onFile(java.io.File, java.io.File)
     */
    public IFileCallback.EResult onFile(File file, File relFile)
    {
        if (fcCallback != null)
        {
            return fcCallback.onFile(file, relFile);
        }

        return IFileCallback.EResult.CONTINUE;
    }
}
