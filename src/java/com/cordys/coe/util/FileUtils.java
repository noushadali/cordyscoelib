package com.cordys.coe.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import java.net.URL;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Contains utilities for file system operations.
 *
 * @author  mpoyhone
 */
public class FileUtils
{
    /**
     * Calculates hash string from the given input stream using the MD5 hashing algorithm.
     *
     * @param   is  Input stream.
     *
     * @return  Check sum as a hex string.
     *
     * @throws  IOException
     */
    public static String calculateHexStringHash(InputStream is)
                                         throws IOException
    {
        return calculateHexStringHash(is, "MD5");
    }

    /**
     * Calculates hash string from the given input stream using the given hashing algorithm.
     *
     * @param   is         Input stream.
     * @param   algorithm  Hashing algorithm (e.g. MD5, SHA).
     *
     * @return  Check sum as a hex string.
     *
     * @throws  IOException
     */
    public static String calculateHexStringHash(InputStream is, String algorithm)
                                         throws IOException
    {
        MessageDigest md;

        try
        {
            md = MessageDigest.getInstance(algorithm);
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new IOException("Unable to get the hash algoritm : " + e);
        }

        byte[] baBuffer = new byte[2048];
        int iReadBytes;

        while ((iReadBytes = is.read(baBuffer)) != -1)
        {
            md.update(baBuffer, 0, iReadBytes);
        }

        byte[] baResult = md.digest();
        StringBuffer sbRes = new StringBuffer(baResult.length * 2);
        final String sHex = "0123456789ABCDEF";

        for (int i = 0; i < baResult.length; i++)
        {
            int b = baResult[i];

            sbRes.append(sHex.charAt((b & 0xF0) >> 4));
            sbRes.append(sHex.charAt(b & 0x0F));
        }

        return sbRes.toString();
    }

    /**
     * Closes a reader. This ignores all exceptions that might be thrown.
     *
     * @param  rReader  The reader to be closed. If it is null, this method does nothing.
     */
    public static void closeReader(Reader rReader)
    {
        try
        {
            if (rReader != null)
            {
                rReader.close();
            }
        }
        catch (IOException ignored)
        {
        }
    }

    /**
     * Closes an input stream. This ignores all exceptions that might be thrown.
     *
     * @param  isInput  The input stream to be closed. If it is null, this method does nothing.
     */
    public static void closeStream(InputStream isInput)
    {
        try
        {
            if (isInput != null)
            {
                isInput.close();
            }
        }
        catch (IOException ignored)
        {
        }
    }

    /**
     * Closes an output stream. This ignores all exceptions that might be thrown.
     *
     * @param  osOutput  The output stream to be closed. If it is null, this method does nothing.
     */
    public static void closeStream(OutputStream osOutput)
    {
        try
        {
            if (osOutput != null)
            {
                osOutput.close();
            }
        }
        catch (IOException ignored)
        {
        }
    }

    /**
     * Closes a writer. This ignores all exceptions that might be thrown.
     *
     * @param  wWriter  The writer to be closed. If it is null, this method does nothing.
     */
    public static void closeWriter(Writer wWriter)
    {
        try
        {
            if (wWriter != null)
            {
                wWriter.close();
            }
        }
        catch (IOException ignored)
        {
        }
    }

    /**
     * Copies the file contents from one file to another. The destination file is overwritten.
     *
     * @param   fSrcFile   Source file
     * @param   fDestFile  Destination file. If this is a folder, file name is taken from the source
     *                     file.
     *
     * @throws  IOException  Thrown if the operation was not successful
     */
    public static void copyFile(File fSrcFile, File fDestFile)
                         throws IOException
    {
        if (fDestFile.isDirectory())
        {
            fDestFile = new File(fDestFile, fSrcFile.getName());
        }

        File fParent = fDestFile.getParentFile();

        if (!fParent.exists())
        {
            if (!fParent.mkdirs())
            {
                throw new IOException("Unable to create parent folder: " + fParent);
            }
        }

        InputStream in = null;
        OutputStream out = null;
        final int iCopyBufferSize = 32768;
        boolean bSuccess = false;

        try
        {
            // Open the source file
            in = new BufferedInputStream(new FileInputStream(fSrcFile));

            // Create the destination file
            out = new BufferedOutputStream(new FileOutputStream(fDestFile));

            // Create the buffer that is used while copying the data
            byte[] baBuffer = new byte[iCopyBufferSize];
            int iReadCount;

            // Copy the file contents
            while ((iReadCount = in.read(baBuffer)) > 0)
            {
                out.write(baBuffer, 0, iReadCount);
            }

            bSuccess = true;
            baBuffer = null;
        }
        finally
        {
            // Close the input stream
            closeStream(in);

            // Close the output stream
            closeStream(out);

            // If the copy operation failed, delete the destination file.
            if (!bSuccess && fDestFile.exists())
            {
                fDestFile.delete();
            }
        }
    }

    /**
     * Copies files recursively from the source folder to the destination folder.
     *
     * @param   fSource     Source folder.
     * @param   fDest       Destination folder.
     * @param   fcCallback  Optional callback object for filtering out file/folders to copy.
     *
     * @return  <code>true</code> if all files and folders were copied successfully.
     *
     * @throws  IOException
     */
    public static boolean copyFilesRecursively(final File fSource, final File fDest,
                                               final IFileCallback fcCallback)
                                        throws IOException
    {
        if (fSource == null)
        {
            throw new NullPointerException("Source folder is null");
        }

        if (fDest == null)
        {
            throw new NullPointerException("Destination folder is null");
        }

        if (!fSource.isDirectory())
        {
            throw new IllegalArgumentException("Source is not a folder.");
        }

        if (!fDest.isDirectory())
        {
            throw new IllegalArgumentException("Destination is not a folder.");
        }

        final IOException[] copyError = new IOException[1];

        IFileCallback copyCallback = new IFileCallback()
        {
            public IFileCallback.EResult onFile(File file, File relFile)
            {
                if (fcCallback != null)
                {
                    EResult result = fcCallback.onFile(file, relFile);

                    if (result != IFileCallback.EResult.CONTINUE)
                    {
                        return result;
                    }
                }

                try
                {
                    copyFile(file, new File(fDest, relFile.getPath()));
                }
                catch (IOException e)
                {
                    copyError[0] = e;
                    return IFileCallback.EResult.STOP;
                }

                return IFileCallback.EResult.CONTINUE;
            }

            public EResult onBeforeFolder(File folder, File relFolder)
            {
                if (fcCallback != null)
                {
                    EResult result = fcCallback.onBeforeFolder(folder, relFolder);

                    if (result != IFileCallback.EResult.CONTINUE)
                    {
                        return result;
                    }
                }

                File fDestFolder = new File(fDest, relFolder.getPath());

                if (!fDestFolder.exists())
                {
                    if (!fDestFolder.mkdir())
                    {
                        copyError[0] = new IOException("Unable to create folder: " +
                                                       fDestFolder.getAbsolutePath());
                        return IFileCallback.EResult.STOP;
                    }
                }

                return IFileCallback.EResult.CONTINUE;
            }

            public EResult onAfterFolder(File folder, File relFolder)
            {
                if (fcCallback != null)
                {
                    return fcCallback.onAfterFolder(folder, relFolder);
                }

                return IFileCallback.EResult.CONTINUE;
            }
        };

        if (copyError[0] != null)
        {
            throw copyError[0];
        }

        return recurseFiles(fSource, copyCallback);
    }

    /**
     * Creates all the parent directories needed by the given file.
     *
     * @param   fFile  The file whose parent directories are to be created.
     *
     * @throws  IOException  Thrown if the operation failed.
     */
    public static void createFileDirectories(File fFile)
                                      throws IOException
    {
        if (fFile == null)
        {
            throw new NullPointerException("File is null.");
        }

        // Get the file directory.
        File fParent = fFile.getParentFile();

        // Check if the directory already exists
        if (fParent.exists())
        {
            return;
        }

        if (!fParent.mkdirs())
        {
            throw new IOException("Unable to create the directory '" + fParent.getAbsolutePath() +
                                  "'");
        }
    }

    /**
     * Creates a temporary directory using File.createTempFile under the system default directory.
     *
     * @param   sPrefix  The prefix to be used in the directory name.
     *
     * @return  The created directory.
     *
     * @throws  IOException  Thrown if the directory could not be created.
     */
    public static File createTempDir(String sPrefix)
                              throws IOException
    {
        return createTempDir(sPrefix, null);
    }

    /**
     * Creates a temporary directory using File.createTempFile.
     *
     * @param   sPrefix     The prefix to be used in the directory name.
     * @param   fParentDir  The parent directory under which the directory is to be created or null,
     *                      if the system default is to be used.
     *
     * @return  The created directory.
     *
     * @throws  IOException  Thrown if the directory could not be created.
     */
    public static File createTempDir(String sPrefix, File fParentDir)
                              throws IOException
    {
        // Loop until we have successfully created the directory.
        // This should not wait forever.
        for (int iTryCount = 0; iTryCount < 1000; iTryCount++)
        {
            // Create a temp file to get the name of our temp directory.
            File fTempFile = File.createTempFile(sPrefix, "", fParentDir);

            // Delete the file and create a directory
            if (!fTempFile.delete())
            {
                throw new IOException("Unable to delete temporary file " + fTempFile);
            }

            if (!fTempFile.mkdir())
            {
                // Somebody already created this file.
                continue;
            }

            // Return the created directory.
            return fTempFile;
        }

        return null;
    }

    /**
     * Deletes all files and directories under the given directory.
     *
     * @param   fRoot  The root directory to be deleted.
     *
     * @throws  IOException  Thrown if the deletion operation failed.
     */
    public static void deleteRecursively(File fRoot)
                                  throws IOException
    {
        if (fRoot == null)
        {
            throw new NullPointerException("Root folder is null.");
        }

        // Check if the root is a file.
        if (fRoot.isFile())
        {
            // Try to delete it.
            if (!fRoot.delete())
            {
                // The deletion failed.
                throw new IOException("Unable to delete file '" + fRoot.getAbsolutePath() + "'");
            }

            return;
        }

        // List all files and directories under this directory.
        File[] faFiles = fRoot.listFiles();

        for (int iIndex = 0; iIndex < faFiles.length; iIndex++)
        {
            File fFile = faFiles[iIndex];

            // Call this method recursively
            deleteRecursively(fFile);
        }

        // Try to delete folder.
        if (!fRoot.delete())
        {
            // The deletion failed.
            throw new IOException("Unable to delete folder '" + fRoot.getAbsolutePath() + "'");
        }
    }

    /**
     * Extracts the zip file into the given directory.
     *
     * @param   zfFile       The zip file to be extracted.
     * @param   fExtractDir  The extract directory. This must exist.
     *
     * @throws  IOException  Thrown if the operation failed.
     */
    public static void extractZipFile(ZipFile zfFile, File fExtractDir)
                               throws IOException
    {
        if (zfFile == null)
        {
            throw new NullPointerException("Zip file is null.");
        }

        if (fExtractDir == null)
        {
            throw new NullPointerException("Extract folder is null.");
        }

        // Extract one entry at the time.
        for (Enumeration<?> e = zfFile.entries(); e.hasMoreElements();)
        {
            ZipEntry eEntry = (ZipEntry) e.nextElement();
            File fOutputFile = new File(fExtractDir, eEntry.getName());

            // Check the entry type.
            if (eEntry.isDirectory())
            {
                continue;
            }

            // Create the needed directories.
            createFileDirectories(fOutputFile);

            InputStream isIn = null;
            OutputStream osOut = null;

            // Copy the content from the zip file to the destination file.
            try
            {
                // Create the input and output streams.
                isIn = zfFile.getInputStream(eEntry);
                osOut = new FileOutputStream(fOutputFile);

                byte[] baBuffer = new byte[2048];
                int iReadBytes;

                // Copy the content.
                while ((iReadBytes = isIn.read(baBuffer)) != -1)
                {
                    osOut.write(baBuffer, 0, iReadBytes);
                }
            }
            finally
            {
                if (isIn != null)
                {
                    try
                    {
                        isIn.close();
                    }
                    catch (IOException ignored)
                    {
                    }
                }

                if (osOut != null)
                {
                    try
                    {
                        osOut.close();
                    }
                    catch (IOException ignored)
                    {
                    }
                }
            }
        }
    }

    /**
     * Finds all files recursively from the given folder and returns them in an array.
     *
     * @param   fRoot       Root folder.
     * @param   bRelative   If <code>true</code> relative paths are returned.
     * @param   fcCallback  Optional callback object for filtering out file/folders to copy.
     *
     * @return  An array of all found file objects.
     *
     * @throws  IOException
     */
    public static File[] findFiles(File fRoot, final boolean bRelative, IFileCallback fcCallback)
                            throws IOException
    {
        if (fRoot == null)
        {
            throw new NullPointerException("Root folder is null");
        }

        if (!fRoot.isDirectory())
        {
            throw new IllegalArgumentException("Root folder is not a folder.");
        }

        final List<File> resList = new ArrayList<File>(50);

        IFileCallback findCallback = new ForwardingFileCallback(fcCallback)
        {
            public IFileCallback.EResult onFile(File file, File relFile)
            {
                EResult res = super.onFile(file, relFile);

                if (res == IFileCallback.EResult.CONTINUE)
                {
                    resList.add(bRelative ? relFile : file);
                }
                return IFileCallback.EResult.CONTINUE;
            }
        };

        recurseFiles(fRoot, findCallback);

        return (File[]) resList.toArray(new File[resList.size()]);
    }

    /**
     * Return the absolute filename.
     *
     * @param   filename            absolute or relative filename
     * @param   resolveRelativeDir  base path for relative filenames, e.g.
     *                              EIBProperties.getInstallDir()
     *
     * @return  absolute filename
     *
     * @throws  IOException  IOException
     */
    public static String getAbsoluteFileName(String filename, String resolveRelativeDir)
                                      throws IOException
    {
        if (filename == null)
        {
            throw new NullPointerException("File name is null.");
        }

        String filenameOut = filename;
        File fFileName = new File(filename);

        if (fFileName.isAbsolute())
        {
            filenameOut = filename;
        }
        else
        {
            String userDir = System.getProperty("user.dir");
            String absFile = fFileName.getAbsoluteFile().toString();

            File fAbsFile;

            if (absFile.startsWith(userDir))
            {
                fAbsFile = new File(resolveRelativeDir + File.separatorChar +
                                    absFile.substring(userDir.length() + 1));
            }
            else
            {
                fAbsFile = new File(absFile);
            }

            filenameOut = fAbsFile.getCanonicalFile().toString();
        }

        return filenameOut;
    }

    /**
     * Returns the given resource as a file. This method does not work for classes packaged in a jar
     * file.
     *
     * @param   sResourcePath  Path relative to the given class.
     * @param   cRefClass      Reference class.
     *
     * @return  File object pointing to the given file or <code>null</code> if the file was not
     *          found.
     */
    public static File getResourceFile(String sResourcePath, Class<?> cRefClass)
    {
        URL url = cRefClass.getResource(sResourcePath);

        if (url == null)
        {
            return null;
        }

        String filePath = url.getFile();

        if (filePath == null)
        {
            return null;
        }

        // Replace %20 in the URL with spaces.
        filePath = filePath.replaceAll("%20", " ");

        return new File(filePath);
    }

    /**
     * Reads the file contents and returns it as a byte array.
     *
     * @param   fFile  The file to be read.
     *
     * @return  File contents as a byte array.
     *
     * @throws  IOException  Thrown if the input stream read failed.
     */
    public static byte[] readFileContents(File fFile)
                                   throws IOException
    {
        if (fFile == null)
        {
            throw new NullPointerException("File is null.");
        }

        InputStream isInput = null;

        try
        {
            isInput = new FileInputStream(fFile);

            return readStreamContents(isInput);
        }
        finally
        {
            closeStream(isInput);
        }
    }

    /**
     * Reads the reader contents and returns it as a String.
     *
     * @param   rReader  The reader to be read.
     *
     * @return  Reader contents as a string.
     *
     * @throws  IOException  Thrown if the read failed.
     */
    public static String readReaderContents(Reader rReader)
                                     throws IOException
    {
        StringBuilder sbRes = new StringBuilder(2048);

        readReaderContents(rReader, sbRes);

        return sbRes.toString();
    }

    /**
     * Reads the reader contents and appends them to the given StringBuilder.
     *
     * @param   rReader   The reader to be read.
     * @param   sbBuffer  Contents are appended to this buffer.
     *
     * @return  Amount of characters read.
     *
     * @throws  IOException  Thrown if the read failed.
     */
    public static long readReaderContents(Reader rReader, StringBuilder sbBuffer)
                                   throws IOException
    {
        if (rReader == null)
        {
            throw new NullPointerException("Reader is null.");
        }

        char[] caTmpBuffer = new char[1024];
        long lTotal = 0;
        int iBytesRead;

        while ((iBytesRead = rReader.read(caTmpBuffer)) != -1)
        {
            sbBuffer.append(caTmpBuffer, 0, iBytesRead);
            lTotal += iBytesRead;
        }

        return lTotal;
    }

    /**
     * Reads the resource contents and returns it as a byte array.
     *
     * <p>For how the resource path is found @see
     * java.lang.Class.getResourceAsStream(java.lang.String)</p>
     *
     * @param   sResourcePath  The resource to be read.
     * @param   cRefClass      Reference class. Relative paths are relative to this class's package.
     *
     * @return  Resource contents as a byte array.
     *
     * @throws  IOException  Thrown if the input stream read failed.
     */
    public static byte[] readResourceContents(String sResourcePath, Class<?> cRefClass)
                                       throws IOException
    {
        if (sResourcePath == null)
        {
            throw new NullPointerException("Resource path is null.");
        }

        if (cRefClass == null)
        {
            throw new NullPointerException("Resource reference class is null.");
        }

        InputStream is = null;

        try
        {
            is = cRefClass.getResourceAsStream(sResourcePath);

            if (is == null)
            {
                throw new IOException("Resource not found with path: " + sResourcePath);
            }

            return readStreamContents(is);
        }
        finally
        {
            FileUtils.closeStream(is);
        }
    }

    /**
     * Reads the input stream contents and returns it as a byte array.
     *
     * @param   isInput  The input stream to be read.
     *
     * @return  Input stream contents as a byte array.
     *
     * @throws  IOException  Thrown if the input stream read failed.
     */
    public static byte[] readStreamContents(InputStream isInput)
                                     throws IOException
    {
        if (isInput == null)
        {
            throw new NullPointerException("Input stream is null.");
        }

        ByteArrayOutputStream osBuffer = null;

        osBuffer = new ByteArrayOutputStream();

        byte[] baTmpBuffer = new byte[2048];
        int iBytesRead;

        while ((iBytesRead = isInput.read(baTmpBuffer)) != -1)
        {
            osBuffer.write(baTmpBuffer, 0, iBytesRead);
        }

        return osBuffer.toByteArray();
    }

    /**
     * Reads the input stream contents and returns it as a byte array.
     *
     * @param   isInput    The input stream to be read.
     * @param   iNumBytes  DOCUMENTME
     *
     * @return  Input stream contents as a byte array.
     *
     * @throws  IOException  Thrown if the input stream read failed.
     */
    public static byte[] readStreamContents(InputStream isInput, int iNumBytes)
                                     throws IOException
    {
        if (isInput == null)
        {
            throw new NullPointerException("Input stream is null.");
        }

        ByteArrayOutputStream osBuffer = null;

        osBuffer = new ByteArrayOutputStream(iNumBytes);

        byte[] baTmpBuffer = new byte[2048];
        int iFullReads = iNumBytes / baTmpBuffer.length;
        int iLastReadSize = iNumBytes - (baTmpBuffer.length * iFullReads);

        for (int i = 0; i < iFullReads; i++)
        {
            int iBytesRead = isInput.read(baTmpBuffer);

            if (iBytesRead <= 0)
            {
                throw new IOException("Unexcepted end of stream as position: " +
                                      (i * baTmpBuffer.length));
            }

            osBuffer.write(baTmpBuffer, 0, iBytesRead);
        }

        if (iLastReadSize > 0)
        {
            int iBytesRead = isInput.read(baTmpBuffer, 0, iLastReadSize);

            if (iBytesRead <= 0)
            {
                throw new IOException("Unexcepted end of stream as position: " +
                                      (iFullReads * baTmpBuffer.length));
            }

            osBuffer.write(baTmpBuffer, 0, iBytesRead);
        }

        return osBuffer.toByteArray();
    }

    /**
     * Reads the text file contents and returns it as a string.
     *
     * @param   fFile  The file to be read.
     *
     * @return  File contents as a string.
     *
     * @throws  IOException  Thrown if the input stream read failed.
     */
    public static String readTextFileContents(File fFile)
                                       throws IOException
    {
        if (fFile == null)
        {
            throw new NullPointerException("File is null.");
        }

        Reader rReader = null;

        try
        {
            rReader = new FileReader(fFile);

            return readReaderContents(rReader);
        }
        finally
        {
            closeReader(rReader);
        }
    }

    /**
     * Reads the text resource contents and returns it as a string.
     *
     * <p>For how the resource path is found @see java.lang.Class.getResourceAsStream(String)</p>
     *
     * @param   sResourcePath  The resource to be read.
     * @param   cRefClass      Reference class. Relative paths are relative to this class's package.
     *
     * @return  Resource contents as a string.
     *
     * @throws  IOException  Thrown if the input stream read failed.
     */
    public static String readTextResourceContents(String sResourcePath, Class<?> cRefClass)
                                           throws IOException
    {
        if (sResourcePath == null)
        {
            throw new NullPointerException("Resource path is null.");
        }

        if (cRefClass == null)
        {
            throw new NullPointerException("Resource reference class is null.");
        }

        InputStream is = null;

        try
        {
            is = cRefClass.getResourceAsStream(sResourcePath);

            if (is == null)
            {
                throw new IOException("Resource not found with path: " + sResourcePath);
            }

            return readTextStreamContents(is);
        }
        finally
        {
            FileUtils.closeStream(is);
        }
    }

    /**
     * Reads the input stream contents and returns it as a String. Character set used is UTF-8.
     *
     * @param       isInput  The input stream to be read.
     *
     * @return      Input stream contents as a byte array.
     *
     * @throws      IOException  Thrown if the input stream read failed.
     *
     * @deprecated  replaced by readTextStreamContents
     */
    public static String readTextSreamContents(InputStream isInput)
                                        throws IOException
    {
        return readTextSreamContents(isInput, "UTF-8");
    }

    /**
     * Reads the input stream contents and returns it as a String.
     *
     * @param       isInput   The input stream to be read.
     * @param       sCharSet  Character set to be used to convert file contents into a string.
     *
     * @return      Input stream contents as a byte array.
     *
     * @throws      IOException  Thrown if the input stream read failed.
     *
     * @deprecated  replaced by readTextStreamContents
     */
    public static String readTextSreamContents(InputStream isInput, String sCharSet)
                                        throws IOException
    {
        byte[] baBuffer = readStreamContents(isInput);

        if ((baBuffer == null) || (baBuffer.length == 0))
        {
            return "";
        }

        return new String(baBuffer, sCharSet);
    }

    /**
     * Reads the input stream contents and returns it as a String. Character set used is UTF-8.
     *
     * @param   isInput  The input stream to be read.
     *
     * @return  Input stream contents as a byte array.
     *
     * @throws  IOException  Thrown if the input stream read failed.
     */
    public static String readTextStreamContents(InputStream isInput)
                                         throws IOException
    {
        return readTextStreamContents(isInput, "UTF-8");
    }

    /**
     * Reads the input stream contents and returns it as a String.
     *
     * @param   isInput   The input stream to be read.
     * @param   sCharSet  Character set to be used to convert file contents into a string.
     *
     * @return  Input stream contents as a byte array.
     *
     * @throws  IOException  Thrown if the input stream read failed.
     */
    public static String readTextStreamContents(InputStream isInput, String sCharSet)
                                         throws IOException
    {
        byte[] baBuffer = readStreamContents(isInput);

        if ((baBuffer == null) || (baBuffer.length == 0))
        {
            return "";
        }

        return new String(baBuffer, sCharSet);
    }

    /**
     * Reads the input zip entry contents and returns it as a String. Character set used is UTF-8.
     *
     * @param   zfZipFile  The zip file.
     * @param   zeEntry    The zip entry inside the zip file.
     *
     * @return  File contents as a string.
     *
     * @throws  IOException  Thrown if the operation failed.
     */
    public static String readTextZipEntryContents(ZipFile zfZipFile, ZipEntry zeEntry)
                                           throws IOException
    {
        return readTextZipEntryContents(zfZipFile, zeEntry, "UTF-8");
    }

    /**
     * Reads the input zip entry contents and returns it as a String.
     *
     * @param   zfZipFile  The zip file.
     * @param   zeEntry    The zip entry inside the zip file.
     * @param   sCharSet   Character set to be used to convert file contents into a string.
     *
     * @return  File contents as a string.
     *
     * @throws  IOException  Thrown if the operation failed.
     */
    public static String readTextZipEntryContents(ZipFile zfZipFile, ZipEntry zeEntry,
                                                  String sCharSet)
                                           throws IOException
    {
        byte[] baBuffer = readZipEntryContents(zfZipFile, zeEntry);

        if ((baBuffer == null) || (baBuffer.length == 0))
        {
            return "";
        }

        return new String(baBuffer, sCharSet);
    }

    /**
     * Reads the input zip entry contents and returns it as a byte array.
     *
     * @param   zfZipFile  The zip file.
     * @param   zeEntry    The zip entry inside the zip file.
     *
     * @return  File contents as a byte array.
     *
     * @throws  IOException  Thrown if the operation failed.
     */
    public static byte[] readZipEntryContents(ZipFile zfZipFile, ZipEntry zeEntry)
                                       throws IOException
    {
        if (zfZipFile == null)
        {
            throw new NullPointerException("Zip file is null.");
        }

        if (zeEntry == null)
        {
            throw new NullPointerException("Zip entry is null.");
        }

        InputStream isInput = null;

        try
        {
            isInput = zfZipFile.getInputStream(zeEntry);

            return readStreamContents(isInput);
        }
        finally
        {
            if (isInput != null)
            {
                try
                {
                    isInput.close();
                }
                catch (Exception ignored)
                {
                }
            }
        }
    }

    /**
     * Lists the files recursively under the given root folder and call the handleFile or
     * handleFolder method in the callback object. Files and folders are returned in breadth-first
     * order.
     *
     * @param   fRoot       Recursion root folder.
     * @param   ffCallback  Callback object which will be called.
     *
     * @return  <code>false</code> if the callback object terminated the search.
     */
    public static boolean recurseFiles(File fRoot, IFileCallback ffCallback)
    {
        if (fRoot == null)
        {
            throw new NullPointerException("Root folder is null.");
        }

        if (ffCallback == null)
        {
            throw new NullPointerException("Callback is null.");
        }

        if (!fRoot.isDirectory())
        {
            throw new IllegalArgumentException("Root folder is not a folder.");
        }

        return recurseFilesInternal(fRoot, null, ffCallback);
    }

    /**
     * Returns a file path relative to the parent folder.
     *
     * @param   fParentFolder
     * @param   fFile
     *
     * @return
     */
    private static File createRelativeFilePath(File fParentFolder, File fFile)
    {
        return (fParentFolder != null) ? new File(fParentFolder, fFile.getName())
                                       : new File(fFile.getName());
    }

    /**
     * Internal recursion method.
     *
     * @param   fRoot       Recursion root folder.
     * @param   fCurrent    Current folder. This path is relative to the root folder.
     * @param   ffCallback  Callback object.
     *
     * @return
     *
     * @see     recurseFiles(File, IFileCallback)
     */
    private static boolean recurseFilesInternal(File fRoot, File fCurrent, IFileCallback ffCallback)
    {
        // Create file object for the current folder.
        File fAbsCurrent = (fCurrent != null) ? new File(fRoot, fCurrent.getPath()) : fRoot;

        // List all files and directories under this directory.
        File[] faFiles = fAbsCurrent.listFiles();

        // First list all the files.
        for (int iIndex = 0; iIndex < faFiles.length; iIndex++)
        {
            File fFile = faFiles[iIndex];
            IFileCallback.EResult sCallbackResult;
            File fRelFile = createRelativeFilePath(fCurrent, fFile);

            if (!fFile.isFile())
            {
                continue;
            }

            sCallbackResult = ffCallback.onFile(fFile, fRelFile);

            if (sCallbackResult == null)
            {
                throw new NullPointerException("Callback returned a null result.");
            }

            if (sCallbackResult == IFileCallback.EResult.STOP)
            {
                return false;
            }
        }

        // Then list all the folders.
        for (int iIndex = 0; iIndex < faFiles.length; iIndex++)
        {
            File fFile = faFiles[iIndex];
            IFileCallback.EResult sCallbackResult;
            File fRelFolder = createRelativeFilePath(fCurrent, fFile);

            if (!fFile.isDirectory())
            {
                continue;
            }

            sCallbackResult = ffCallback.onBeforeFolder(fFile, fRelFolder);

            if (sCallbackResult == null)
            {
                throw new NullPointerException("Callback returned a null result.");
            }

            switch (sCallbackResult)
            {
                case SKIP:
                    continue;

                case STOP:
                    return false;
            }

            // Call this method recursively
            if (!recurseFilesInternal(fRoot, fRelFolder, ffCallback))
            {
                return false;
            }

            sCallbackResult = ffCallback.onAfterFolder(fFile, fRelFolder);

            if (sCallbackResult == null)
            {
                throw new NullPointerException("Callback returned a null result.");
            }

            if (sCallbackResult == IFileCallback.EResult.STOP)
            {
                return false;
            }
        }

        return true;
    }
}
