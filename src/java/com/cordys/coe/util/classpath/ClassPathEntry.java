package com.cordys.coe.util.classpath;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;

/**
 * This class wraps around.
 *
 * @author  pgussow
 */
public class ClassPathEntry
{
    /**
     * Holds the logger to use.
     */
    private static final Logger LOGGER = Logger.getLogger(ClassPathEntry.class.getName());
    /**
     * Identifies a jarfile.
     */
    public static final int TYPE_JAR = 0;
    /**
     * Identifies a normal folder in the classpath.
     */
    public static final int TYPE_FOLDER = 1;
    /**
     * Holds the type of entry.
     */
    private int m_iType = TYPE_JAR;
    /**
     * Holds the location of this entry.
     */
    private String m_sLocation = null;

    /**
     * Creates a new ClassPathEntry object.
     *
     * @param   sLocation  The location of this entry.
     *
     * @throws  Exception  DOCUMENTME
     */
    public ClassPathEntry(String sLocation)
                   throws Exception
    {
        m_sLocation = sLocation;

        File fTemp = new File(m_sLocation);

        if (!fTemp.exists())
        {
            throw new Exception("Jar or folder " + sLocation + " does not exist.");
        }

        if (fTemp.isDirectory())
        {
            m_iType = TYPE_FOLDER;
        }
        else
        {
            m_iType = TYPE_JAR;
        }
    }

    /**
     * This method will load the actual classfile from the location.
     *
     * @param   sClassName  The name of the class to load.
     *
     * @return  The byte[] containing the class.
     */
    public byte[] getBytes(String sClassName)
    {
        byte[] baReturn = null;

        // First transfer the classname into a real filename.
        String sFilename = getFilename(sClassName);

        if (getType() == TYPE_FOLDER)
        {
            File fTemp = new File(getLocation(), sFilename);

            if (fTemp.exists())
            {
                InputStream is = null;

                try
                {
                    is = new FileInputStream(fTemp);
                    baReturn = new byte[is.available()];
                    is.read(baReturn);
                }
                catch (Exception e)
                {
                    LOGGER.error("Error opening the file " + fTemp.getAbsolutePath(), e);
                }
                finally
                {
                    try
                    {
                        is.close();
                    }
                    catch (IOException e)
                    {
                        LOGGER.error("Error closing the stream for file " + fTemp.getAbsolutePath(),
                                     e);
                    }
                }
            }
        }
        else if (getType() == TYPE_JAR)
        {
            ZipFile zfZipfile = null;

            try
            {
                zfZipfile = new ZipFile(new File(getLocation()));

                ZipEntry zeEntry = zfZipfile.getEntry(sFilename);

                if (zeEntry != null)
                {
                    InputStream is = zfZipfile.getInputStream(zeEntry);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream((int) zeEntry.getSize());
                    baReturn = new byte[is.available()];

                    int iRet = 0;

                    while ((iRet = is.read(baReturn)) != -1)
                    {
                        baos.write(baReturn, 0, iRet);
                    }

                    baReturn = baos.toByteArray();
                }
            }
            catch (Exception e)
            {
                LOGGER.error("Error reading " + sFilename + " from jar " + getLocation(), e);
            }
            finally
            {
                try
                {
                    if (zfZipfile != null)
                    {
                        zfZipfile.close();
                    }
                }
                catch (IOException e)
                {
                    LOGGER.error("Error closing jar " + getLocation(), e);
                }
            }
        }

        return baReturn;
    }

    /**
     * This method gets the location of this entry.
     *
     * @return  The location of this entry.
     */
    public String getLocation()
    {
        return m_sLocation;
    }

    /**
     * This method gets the type of entry.
     *
     * @return  The type of entry.
     */
    public int getType()
    {
        return m_iType;
    }

    /**
     * This method returns whether or not this entry can provide the given class.
     *
     * @param   sName  The name of the class.
     *
     * @return  true if the class could be loaded via this classpath entry. Otherwise false.
     */
    public boolean hasClass(String sName)
    {
        boolean bReturn = false;

        // First transfer the classname into a real filename.
        String sFilename = getFilename(sName);

        if (getType() == TYPE_FOLDER)
        {
            File fTemp = new File(getLocation(), sFilename);

            if (fTemp.exists())
            {
                bReturn = true;
            }
        }
        else if (getType() == TYPE_JAR)
        {
            ZipFile zfZipfile = null;

            try
            {
                zfZipfile = new ZipFile(new File(getLocation()));

                ZipEntry zeEntry = zfZipfile.getEntry(sFilename);

                if (zeEntry != null)
                {
                    bReturn = true;
                }
            }
            catch (Exception e)
            {
                LOGGER.error("Error reading ZIP file " + getLocation());
            }
            finally
            {
                if (zfZipfile != null)
                {
                    try
                    {
                        zfZipfile.close();
                    }
                    catch (IOException e)
                    {
                        LOGGER.error("Error closing ZIP file " + getLocation());
                    }
                }
            }
        }

        return bReturn;
    }

    /**
     * This method returns a string representation of the object.
     *
     * @return  A string representation of the object.
     *
     * @see     java.lang.Object#toString()
     */
    @Override public String toString()
    {
        StringBuffer sbReturn = new StringBuffer("");

        switch (getType())
        {
            case TYPE_FOLDER:
                sbReturn.append("FLD: ");
                break;

            case TYPE_JAR:
                sbReturn.append("JAR: ");
                break;
        }
        sbReturn.append(getLocation());
        return sbReturn.toString();
    }

    /**
     * This method transforms the classname into a filename.
     *
     * @param   sClassname  The name of the class.
     *
     * @return  The filename for the given class.
     */
    private String getFilename(String sClassname)
    {
        String sReturn = sClassname.replace('.', '/');

        sReturn += ".class";

        return sReturn;
    }
}
