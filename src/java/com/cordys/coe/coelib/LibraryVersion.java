/**
 *  2005 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.coelib;

import com.cordys.coe.exception.GeneralException;
import com.cordys.coe.util.FileUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Contains the library version information.
 *
 * @author  mpoyhone Created on Feb 1, 2005
 */
public class LibraryVersion
{
    /**
     * Contains the library version number.
     */
    private static final int iLibraryVersion = new Integer("@current-version@").intValue();
    /**
     * Contains the library build time. The value to be set in place of 'next-version' must be one
     * higher than the current version.
     */
    private static final String sLibraryBuildTimestamp = "@build-time@";
    /**
     * Name of the version file that will be packaged in the applications using this library.
     */
    private static final String DEFAULT_VERSION_FILE = "coelib.version";

    /**
     * Returns the library version number.
     *
     * @return  The library version number.
     */
    public static int getLibraryVersion()
    {
        return iLibraryVersion;
    }

    /**
     * Reads the CoELib version number from a coelib.version file and checks if it is valid. This
     * file must be present at the same directory as the calling class. This method is used to read
     * the version number that the application was compiled with.
     *
     * @param   cCallingClass  Class that checks
     * @param   bAllowNewer    If true newer versions are allowed. If false only the specified
     *                         version is accepted.
     *
     * @throws  GeneralException  Thrown if the version loading failed or there was a the library
     *                            version was not accepted.
     */
    public static void loadAndCheckLibraryVersionFromResource(Class<?> cCallingClass,
                                                              boolean bAllowNewer)
                                                       throws GeneralException
    {
        loadAndCheckLibraryVersionFromResource(cCallingClass, bAllowNewer, null);
    }

    /**
     * Reads the CoELib version number from a coelib.version file and checks if it is valid. This
     * file must be present at the same directory as the calling class. This method is used to read
     * the version number that the application was compiled with.
     *
     * @param   cCallingClass  Class that checks
     * @param   bAllowNewer    If true newer versions are allowed. If false only the specified
     *                         version is accepted.
     * @param   sFileName      Version file name. If null default file name coelib.version is used.
     *
     * @throws  GeneralException  Thrown if the version loading failed or there was a the library
     *                            version was not accepted.
     */
    public static void loadAndCheckLibraryVersionFromResource(Class<?> cCallingClass,
                                                              boolean bAllowNewer, String sFileName)
                                                       throws GeneralException
    {
        int iExpectedVersion;
        int iMyVersion = getLibraryVersion();

        try
        {
            iExpectedVersion = loadLibraryVersionFromResource(cCallingClass, sFileName);
        }
        catch (IOException e)
        {
            throw new GeneralException(e, "Unable to load the CoELib version number.");
        }

        // Check if the file was a development version.
        if (iExpectedVersion < 0)
        {
            return;
        }

        if (bAllowNewer)
        {
            if (iMyVersion < iExpectedVersion)
            {
                throw new GeneralException("CoeLibrary version is too old. Got version " +
                                           iMyVersion + " and expected version " +
                                           iExpectedVersion);
            }
        }
        else
        {
            if (iMyVersion != iExpectedVersion)
            {
                throw new GeneralException("CoeLibrary version mismatch. Got version " +
                                           iMyVersion + " and expected version " +
                                           iExpectedVersion);
            }
        }
    }

    /**
     * Reads the CoELib version number from a 'coelib.version file'. This file must be present at
     * the same directory as the calling class. This method is used to read the version number that
     * the application was compiled with and not the current version number of the CoELib it self.
     * If the file contents begin with &at; then the file is considered to be a development version
     * (the token has not been replaced) and a version of -1 is returned.
     *
     * @param   cCallingClass  Class that checks
     *
     * @return  The version number read from the file.
     *
     * @throws  IOException  Thrown if the operation failed.
     */
    public static int loadLibraryVersionFromResource(Class<?> cCallingClass)
                                              throws IOException
    {
        return loadLibraryVersionFromResource(cCallingClass, DEFAULT_VERSION_FILE);
    }

    /**
     * Reads the CoELib version number from a coelib.version file. This file must be present at the
     * same directory as the calling class. This method is used to read the version number that the
     * application was compiled with and not the current version number of the CoELib it self. If
     * the file contents begin with &at; then the file is considered to be a development version
     * (the token has not been replaced) and a version of -1 is returned.
     *
     * @param   cCallingClass  Class that checks
     * @param   sFileName      Version file name. If null default file name coelib.version is used.
     *
     * @return  The version number read from the file.
     *
     * @throws  IOException  Thrown if the operation failed.
     */
    public static int loadLibraryVersionFromResource(Class<?> cCallingClass, String sFileName)
                                              throws IOException
    {
        if (sFileName == null)
        {
            sFileName = DEFAULT_VERSION_FILE;
        }

        InputStream isInputStream = cCallingClass.getResourceAsStream(sFileName);

        if (isInputStream == null)
        {
            throw new IOException("Could not find resource:" + sFileName);
        }

        try
        {
            String sData = FileUtils.readTextStreamContents(isInputStream).trim();

            if (sData.startsWith("@"))
            {
                // This is a development version and does not have a valid version number.
                return -1;
            }

            try
            {
                return Integer.parseInt(sData);
            }
            catch (Exception e)
            {
                throw new IOException("Invalid version number contents : " + sData);
            }
        }
        finally
        {
            FileUtils.closeStream(isInputStream);
        }
    }

    /**
     * Prints out library version number to the standard output.
     *
     * @param  args  If set to '-number' only the version number is printed.
     */
    public static void main(String[] args)
    {
        if ((args.length > 0) && args[0].equals("-number"))
        {
            System.out.print("" + getLibraryVersion());
            return;
        }

        System.out.println("CoELib version " + getLibraryVersion() + ". Build on " +
                           sLibraryBuildTimestamp);
    }
}
