package com.cordys.coe.util.jvm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.math.BigInteger;

import java.util.Enumeration;
import java.util.LinkedHashMap;

import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * This class has the possibility to tell what version is used for a specific jar or class file. In case of a jar the first .class
 * file is used.
 * 
 * @author pgussow
 */
public class ClassVersion
{
    /**
     * Holds all known java version.
     */
    private static LinkedHashMap<String, IJavaVersionInfo> s_lhmVersions = new LinkedHashMap<String, IJavaVersionInfo>();

    static
    {
        // Create the list.
        s_lhmVersions.put("45.3", new JVMInfo("45.3", "Java 1.1"));
        s_lhmVersions.put("46.0", new JVMInfo("46.0", "Java 1.2"));
        s_lhmVersions.put("47.0", new JVMInfo("47.0", "Java 1.3"));
        s_lhmVersions.put("48.0", new JVMInfo("48.0", "Java 1.4"));
        s_lhmVersions.put("49.0", new JVMInfo("49.0", "Java 1.5"));
        s_lhmVersions.put("50.0", new JVMInfo("50.0", "Java 6"));
    }

    /**
     * This method returns the JVM version for the given file.
     * 
     * @param sFilename The name of the class file.
     * @return The proper java version.
     * @throws IOException In case of any exceptions.
     */
    public static IJavaVersionInfo getVersionForClassFile(String sFilename) throws IOException
    {
        return getVersionForClassFile(new File(sFilename));
    }

    /**
     * This method returns the JVM version for the given file.
     * 
     * @param fFile The class file.
     * @return The proper java version.
     * @throws IOException In case of any exceptions.
     */
    public static IJavaVersionInfo getVersionForClassFile(File fFile) throws IOException
    {
        IJavaVersionInfo jviReturn = null;

        if (!fFile.exists())
        {
            throw new FileNotFoundException("File " + fFile.getAbsolutePath() + " not found.");
        }

        FileInputStream fis = new FileInputStream(fFile);

        try
        {
            jviReturn = getVersionForStream(fis);
        }
        finally
        {
            fis.close();
        }

        return jviReturn;
    }

    /**
     * This method returns the JVM version for the given jar file.
     * 
     * @param sFilename The name of the jar file.
     * @return The proper java version.
     * @throws IOException In case of any exceptions.
     */
    public static IJavaVersionInfo getVersionForJarFile(String sFilename) throws IOException
    {
        return getVersionForJarFile(new File(sFilename));
    }

    /**
     * This method returns the JVM version for the given jar file.
     * 
     * @param fFile The jar file.
     * @return The proper java version.
     * @throws IOException In case of any exceptions.
     */
    public static IJavaVersionInfo getVersionForJarFile(File fFile) throws IOException
    {
        if (!fFile.exists())
        {
            throw new FileNotFoundException("File " + fFile.getAbsolutePath() + " not found.");
        }

        // Find the first class in the jar.
        ZipFile xf = new ZipFile(fFile);
        try
        {
            Enumeration<? extends ZipEntry> e = xf.entries();

            while (e.hasMoreElements())
            {
                ZipEntry ze = e.nextElement();

                if (ze.getName().endsWith(".class"))
                {
                    return getVersionForStream(xf.getInputStream(ze));
                }
            }
        }
        finally
        {
            xf.close();
        }

        throw new IOException("File " + fFile.getAbsolutePath() + " does not have any class files");
    }

    /**
     * This method returns the java version for the given stream.
     * 
     * @param is The stream to read.
     * @return The Java version.
     * @throws IOException In case of any exceptions.
     */
    public static IJavaVersionInfo getVersionForStream(InputStream is) throws IOException
    {
        IJavaVersionInfo jviReturn = null;

        byte[] ba = new byte[8];
        is.read(ba);
        is.close();

        byte[] baMajor = new byte[2];
        byte[] baMinor = new byte[2];
        baMinor[0] = ba[4];
        baMinor[1] = ba[5];
        baMajor[0] = ba[6];
        baMajor[1] = ba[7];

        int iMajor = new BigInteger(baMajor).intValue();
        int iMinor = new BigInteger(baMinor).intValue();

        String sVersion = "" + iMajor + "." + iMinor;

        jviReturn = s_lhmVersions.get(sVersion);

        if (jviReturn == null)
        {
            throw new IOException("Unknown major.minor version (" + sVersion + ")");
        }

        return jviReturn;
    }

    /**
     * Main test method.
     * 
     * @param args Command line arguments.
     */
    public static void main(String[] args)
    {
        try
        {
            IJavaVersionInfo jvi = getVersionForClassFile(".\\classes\\com\\cordys\\coe\\tools\\es\\ESLogEvent.class");
            System.out.println(jvi.getMajorMinor() + ": " + jvi.getDisplayName());

            jvi = getVersionForJarFile(".\\sdk\\lib\\buildtasks.jar");
            System.out.println(jvi.getMajorMinor() + ": " + jvi.getDisplayName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * This class wraps the JVM information.
     * 
     * @author pgussow
     */
    public static class JVMInfo implements IJavaVersionInfo
    {
        /**
         * Holds the display name for the JVM.
         */
        private String m_sDisplay;
        /**
         * Holds the major minor.
         */
        private String m_sMajorMinor;

        /**
         * Constructor.
         * 
         * @param sMajorMinor The major minor.
         * @param sDisplay The display name for the JVM.
         */
        public JVMInfo(String sMajorMinor, String sDisplay)
        {
            m_sMajorMinor = sMajorMinor;
            m_sDisplay = sDisplay;
        }

        /**
         * This method returns the display for the given version.
         * 
         * @return The display for the given version.
         * @see com.cordys.coe.util.jvm.IJavaVersionInfo#getDisplayName()
         */
        public String getDisplayName()
        {
            return m_sDisplay;
        }

        /**
         * This method gets the major.minor for this version.
         * 
         * @return The major.minor for this version.
         * @see com.cordys.coe.util.jvm.IJavaVersionInfo#getMajorMinor()
         */
        public String getMajorMinor()
        {
            return m_sMajorMinor;
        }
    }
}
