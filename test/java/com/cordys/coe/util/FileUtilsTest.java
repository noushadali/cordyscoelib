/**
 * (c) 2007 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import junit.framework.TestCase;

import com.cordys.coe.util.test.junit.FileTestUtils;

/**
 * TODO Describe the class.
 *
 * @author mpoyhone
 */
public class FileUtilsTest extends TestCase
{
    File tempFolder;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        tempFolder = null;
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        if (tempFolder != null && tempFolder.exists()) {
            try
            {
                FileUtils.deleteRecursively(tempFolder);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                fail("Unable to delete the temp folder: " + e);
            }
        }
        
        super.tearDown();
    }

    /**
     * Test method for {@link com.cordys.coe.util.FileUtils#closeReader(java.io.Reader)}.
     */
    public void testCloseReader()
    {
        FileUtils.closeReader(null);
    }

    /**
     * Test method for {@link com.cordys.coe.util.FileUtils#closeStream(java.io.InputStream)}.
     */
    public void testCloseStreamInputStream()
    {
        FileUtils.closeStream((InputStream) null);
    }

    /**
     * Test method for {@link com.cordys.coe.util.FileUtils#closeStream(java.io.OutputStream)}.
     */
    public void testCloseStreamOutputStream()
    {
        FileUtils.closeStream((OutputStream) null);
    }

    /**
     * Test method for {@link com.cordys.coe.util.FileUtils#closeWriter(java.io.Writer)}.
     */
    public void testCloseWriter()
    {
        FileUtils.closeWriter(null);
    }
    
    /**
     * Test method for {@link com.cordys.coe.util.FileUtils#calculateHexStringHash(InputStream, String)}.
     * @throws IOException 
     */
    public void testCalculateHexStringChecksum() throws IOException
    {
        String controlData = "TEST DATA STRING.";
        String controlChecksum = "86a3d69b4b492e1ac1169e3c0de9222f";
        String testChecksum = FileUtils.calculateHexStringHash(new ByteArrayInputStream(controlData.getBytes()));
        
        assertEquals(controlChecksum.toUpperCase(), testChecksum.toUpperCase());
    }
    

    /**
     * Test method for {@link  com.cordys.coe.util.FileUtils#getResourceFile(String, Class)
     * @throws IOException
     */
    public void testGetResourceFile() throws IOException
    {
        String fileName = "testdata/testdata.txt";
        byte[] controlData = FileUtils.readResourceContents(fileName, FileUtilsTest.class);
        File file = FileUtils.getResourceFile(fileName, FileUtilsTest.class);
        byte[] fileData = FileUtils.readFileContents(file);
        
        assertTrue(Arrays.equals(controlData, fileData));
    }
    

    /**
     * Test method for {@link  com.cordys.coe.util.FileUtils#getResourceFile(String, Class)
     * @throws IOException
     */
    public void testGetResourceFileWithSpaces() throws IOException
    {
        String fileName = "testdata/test data with spaces in the name.txt";
        byte[] controlData = FileUtils.readResourceContents(fileName, FileUtilsTest.class);
        File file = FileUtils.getResourceFile(fileName, FileUtilsTest.class);
        byte[] fileData = FileUtils.readFileContents(file);
        
        assertTrue(Arrays.equals(controlData, fileData));
    }
    
    /**
     * Test method for {@link com.cordys.coe.util.FileUtils#copyFile(java.io.File, java.io.File)}.
     * @throws IOException 
     */
    public void testCopyFile() throws IOException
    {
        createTempFolder();
        
        String fileName = "testdata/testdata.txt";
        File srcFile = FileUtils.getResourceFile(fileName, FileUtilsTest.class);
        File destFile = new File(tempFolder, srcFile.getName());
        
        try
        {
            FileUtils.copyFile(srcFile, destFile);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail("File copy failed.");
        }
        
        byte[] controlData = FileUtils.readResourceContents(fileName, FileUtilsTest.class);
        byte[] fileData = FileUtils.readFileContents(destFile);
        
        assertTrue(Arrays.equals(controlData, fileData));
    }
    
    /**
     * Test method for {@link com.cordys.coe.util.FileUtils#copyFilesRecursively(File, File, IFileCallback)
     */
    public void testCopyFilesRecursively()
    {
        createTempFolder();
        
        String folderName = "testdata";
        File srcFolder = FileUtils.getResourceFile(folderName, FileUtilsTest.class);
        boolean ok = false;
        
        try
        {
            ok = FileUtils.copyFilesRecursively(srcFolder, tempFolder, null);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail("File copy failed.");
        }
        
        assertTrue(ok);
        
        FileTestUtils.assertFoldersEqual(srcFolder, tempFolder, null);
    }
    
    /**
     * Test method for {@link com.cordys.coe.util.FileUtils#copyFilesRecursively(File, File, IFileCallback)
     */
    public void testCopyFilesRecursively_NoFolders()
    {
        createTempFolder();
        
        String folderName = "testdata";
        File srcFolder = FileUtils.getResourceFile(folderName, FileUtilsTest.class);
        boolean ok = false;
        
        try
        {
            ok = FileUtils.copyFilesRecursively(srcFolder, tempFolder,  new IFileCallback() {
                public EResult onAfterFolder(File arg0, File arg1)
                {
                    return EResult.SKIP;
                }
                public EResult onBeforeFolder(File arg0, File arg1)
                {
                    return EResult.SKIP;
                }

                public EResult onFile(File arg0, File arg1)
                {
                    return EResult.CONTINUE;
                }
            });
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail("File copy failed.");
        }
        
        assertTrue(ok);
        
        assertFalse(new File(tempFolder, "empty").exists());
    }
    
    private void createTempFolder()
    {
        tempFolder = new File("./build/test/fileutils-test");
        
        if (tempFolder.exists()) {
            try
            {
                FileUtils.deleteRecursively(tempFolder);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                fail("Unable to delete the temp folder: " + e);
            }
        }
            
        boolean ok = tempFolder.mkdirs();
        assertTrue("Unable to create the temp folder: " + tempFolder, ok);
    }
}
