/**
 * (c) 2007 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util.test.junit;

import com.cordys.coe.util.FileUtils;
import com.cordys.coe.util.IFileCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.SortedMap;
import java.util.TreeMap;

import junit.framework.TestCase;

/**
 * Utility method for file specific test cases.
 *
 * @author  mpoyhone
 */
public class FileTestUtils
{
    /**
     * Asserts that the contents of the two folders are equal. Both have to have to same files with
     * the same content.
     *
     * @param  folder1   Folder to compare
     * @param  folder2   Folder to compare
     * @param  callback  Optional callback object for filtering out files/folders.
     */
    public static void assertFoldersEqual(File folder1, File folder2, IFileCallback callback)
    {
        FileCompareCallback control = new FileCompareCallback(callback);
        FileCompareCallback test = new FileCompareCallback(callback);

        FileUtils.recurseFiles(folder1, control);
        FileUtils.recurseFiles(folder2, test);

        TestCase.assertEquals(control.fileChecksumMap, test.fileChecksumMap);
    }

    /**
     * Asserts that the contents of the two files are equal.
     *
     * @param  file1  File to compare
     * @param  file2  File to compare
     */
    public static void assertFilesEqual(File file1, File file2)
    {
        if (file1 == null)
        {
            throw new NullPointerException("file1 is null");
        }

        if (file2 == null)
        {
            throw new NullPointerException("file2 is null");
        }

        InputStream is1 = null;
        InputStream is2 = null;

        try
        {
            String hash1 = FileUtils.calculateHexStringHash(is1 = new FileInputStream(file1));
            String hash2 = FileUtils.calculateHexStringHash(is2 = new FileInputStream(file2));

            TestCase.assertEquals(hash1, hash2);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            TestCase.fail("File comparison failed: " + e.getMessage());
        }
        finally
        {
            FileUtils.closeStream(is1);
            FileUtils.closeStream(is2);
        }
    }

    /**
     * Creates all the folders in the given path or if the folder already exists, it's contents are
     * deleted.
     *
     * @param   folder  Folder to be created.
     *
     * @throws  IOException
     */
    public static void initializeFolder(File folder)
                                 throws IOException
    {
        if (folder.exists())
        {
            FileUtils.deleteRecursively(folder);
        }

        if (!folder.mkdirs())
        {
            throw new IOException("Unable to create folder: " + folder);
        }
    }

    /**
     * DOCUMENTME.
     *
     * @author  $author$
     */
    private static class FileCompareCallback
        implements IFileCallback
    {
        /**
         * DOCUMENTME.
         */
        public SortedMap<String, String> fileChecksumMap = new TreeMap<String, String>();
        /**
         * DOCUMENTME.
         */
        private IFileCallback callback = null;

        /**
         * Creates a new FileCompareCallback object.
         *
         * @param  callback  DOCUMENTME
         */
        public FileCompareCallback(IFileCallback callback)
        {
            this.callback = callback;
        }

        /**
         * @see  com.cordys.coe.util.IFileCallback#onAfterFolder(java.io.File, java.io.File)
         */
        public EResult onAfterFolder(File folder, File relFolder)
        {
            return (callback != null) ? callback.onAfterFolder(folder, relFolder)
                                      : EResult.CONTINUE;
        }

        /**
         * @see  com.cordys.coe.util.IFileCallback#onBeforeFolder(java.io.File, java.io.File)
         */
        public EResult onBeforeFolder(File folder, File relFolder)
        {
            if (callback != null)
            {
                EResult res = callback.onBeforeFolder(folder, relFolder);

                if (res != EResult.CONTINUE)
                {
                    return res;
                }
            }

            fileChecksumMap.put(relFolder.getPath(), "FOLDER");

            return EResult.CONTINUE;
        }

        /**
         * @see  com.cordys.coe.util.IFileCallback#onFile(java.io.File, java.io.File)
         */
        public EResult onFile(File file, File relFile)
        {
            if (callback != null)
            {
                EResult res = callback.onFile(file, relFile);

                if (res != EResult.CONTINUE)
                {
                    return res;
                }
            }

            try
            {
                InputStream is = new FileInputStream(file);
                String checksum = FileUtils.calculateHexStringHash(is);
                FileUtils.closeStream(is);
                fileChecksumMap.put(relFile.getPath(), checksum);
                return EResult.CONTINUE;
            }
            catch (Exception e)
            {
                throw new IllegalStateException("Unable to calculate file checksum: " + file, e);
            }
        }
    }
    ;
}
