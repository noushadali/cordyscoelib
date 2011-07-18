package com.cordys.coe.util.checkclasspath;

import java.security.cert.Certificate;

import java.util.Date;

import java.util.jar.Attributes;

/**
 * DOCUMENTME.
 *
 * @author  $author$
 */
public class ClassInfo
{
    /**
     * DOCUMENTME.
     */
    private Certificate[] acFileCertificates;
    /**
     * DOCUMENTME.
     */
    private Attributes aFileAttributes;
    /**
     * DOCUMENTME.
     */
    private Class<?> cFileClass;
    /**
     * DOCUMENTME.
     */
    private Date dFileDate = new Date();
    /**
     * DOCUMENTME.
     */
    private int iClassOrderLevel;
    /**
     * DOCUMENTME.
     */
    private long lFileCompressedSize;
    /**
     * DOCUMENTME.
     */
    private long lFileCrc;
    /**
     * DOCUMENTME.
     */
    private long lFileSize;
    /**
     * DOCUMENTME.
     */
    private String sClassDirPath;
    /**
     * DOCUMENTME.
     */
    private Class<?> sFileClass;
    /**
     * DOCUMENTME.
     */
    private String sFileComment;
    /**
     * DOCUMENTME.
     */
    private String sFileName;
    /**
     * DOCUMENTME.
     */
    private String sJarName;
    /**
     * DOCUMENTME.
     */
    private String sMD5Hash;
    /**
     * DOCUMENTME.
     */
    private String sRelativeDir;

    /**
     * Creates a new ClassInfo object.
     *
     * @param  sClassName       DOCUMENTME
     * @param  sJarName         DOCUMENTME
     * @param  lCrc             DOCUMENTME
     * @param  dDate            DOCUMENTME
     * @param  lSize            DOCUMENTME
     * @param  lCompressedSize  DOCUMENTME
     * @param  aAttributes      DOCUMENTME
     * @param  acCertificates   DOCUMENTME
     * @param  cClass           DOCUMENTME
     * @param  sComment         DOCUMENTME
     * @param  sHash            DOCUMENTME
     * @param  iClassOrder      DOCUMENTME
     * @param  sClassPathDir    DOCUMENTME
     * @param  sRelative        DOCUMENTME
     */
    public ClassInfo(String sClassName, String sJarName, long lCrc, Date dDate, long lSize,
                     long lCompressedSize, Attributes aAttributes, Certificate[] acCertificates,
                     Class<?> cClass, String sComment, String sHash, int iClassOrder,
                     String sClassPathDir, String sRelative)
    {
        sFileName = sClassName;
        this.sJarName = sJarName;
        lFileCrc = lCrc;
        dFileDate = dDate;
        lFileSize = lSize;
        lFileCompressedSize = lCompressedSize;
        aFileAttributes = aAttributes;
        acFileCertificates = acCertificates;
        cFileClass = cClass;
        sFileComment = sComment;
        sMD5Hash = sHash;
        iClassOrderLevel = iClassOrder;
        sClassDirPath = sClassPathDir;
        sRelativeDir = sRelative;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getClassOrderLevel()
    {
        return iClassOrderLevel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getClassPathDir()
    {
        return sClassDirPath;
    }

    /**
     * This method returns the value of aFileAttributes.
     *
     * @return  Returns the aFileAttributes.
     */
    public Attributes getFileAttributes()
    {
        return aFileAttributes;
    }

    /**
     * This method returns the value of acFileCertificates.
     *
     * @return  Returns the acFileCertificates.
     */
    public Certificate[] getFileCertificates()
    {
        return acFileCertificates;
    }

    /**
     * This method returns the value of sFileClass.
     *
     * @return  Returns the sFileClass.
     */
    public Class<?> getFileClass()
    {
        return sFileClass;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Class<?> getFileClazz()
    {
        return cFileClass;
    }

    /**
     * This method returns the value of sFileComment.
     *
     * @return  Returns the sFileComment.
     */
    public String getFileComment()
    {
        return sFileComment;
    }

    /**
     * This method returns the value of lFileCompressedSize.
     *
     * @return  Returns the lFileCompressedSize.
     */
    public long getFileCompressedSize()
    {
        return lFileCompressedSize;
    }

    /**
     * This method returns the value of lFileCrc.
     *
     * @return  Returns the lFileCrc.
     */
    public long getFileCrc()
    {
        return lFileCrc;
    }

    /**
     * This method returns the value of dFileDate.
     *
     * @return  Returns the dFileDate.
     */
    public Date getFileDate()
    {
        return dFileDate;
    }

    /**
     * This method returns the value of sFileName.
     *
     * @return  Returns the sFileName.
     */
    public String getFileName()
    {
        return sFileName;
    }

    /**
     * This method returns the value of lFileSize.
     *
     * @return  Returns the lFileSize.
     */
    public long getFileSize()
    {
        return lFileSize;
    }

    /**
     * This method returns the value of sJarName.
     *
     * @return  Returns the sJarName.
     */
    public String getJarName()
    {
        return sJarName;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getMD5Hash()
    {
        return sMD5Hash;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getRelativeDir()
    {
        return sRelativeDir;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  iClassOrder  DOCUMENT ME!
     */
    public void setClassOrderLevel(int iClassOrder)
    {
        iClassOrderLevel = iClassOrder;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  sClassPathDir  DOCUMENT ME!
     */
    public void setClassPathDir(String sClassPathDir)
    {
        sClassDirPath = sClassPathDir;
    }

    /**
     * This method sets the value for aFileAttributes.
     *
     * @param  fileAttributes  The aFileAttributes to set.
     */
    public void setFileAttributes(Attributes fileAttributes)
    {
        aFileAttributes = fileAttributes;
    }

    /**
     * This method sets the value for acFileCertificates.
     *
     * @param  acFileCertificates  The acFileCertificates to set.
     */
    public void setFileCertificates(Certificate[] acFileCertificates)
    {
        this.acFileCertificates = acFileCertificates;
    }

    /**
     * This method sets the value for sFileClass.
     *
     * @param  fileClass  The sFileClass to set.
     */
    public void setFileClass(Class<?> fileClass)
    {
        sFileClass = fileClass;
    }

    /**
     * This method sets the value for sFileComment.
     *
     * @param  fileComment  The sFileComment to set.
     */
    public void setFileComment(String fileComment)
    {
        sFileComment = fileComment;
    }

    /**
     * This method sets the value for lFileCompressedSize.
     *
     * @param  fileCompressedSize  The lFileCompressedSize to set.
     */
    public void setFileCompressedSize(long fileCompressedSize)
    {
        lFileCompressedSize = fileCompressedSize;
    }

    /**
     * This method sets the value for lFileCrc.
     *
     * @param  fileCrc  The lFileCrc to set.
     */
    public void setFileCrc(long fileCrc)
    {
        lFileCrc = fileCrc;
    }

    /**
     * This method sets the value for dFileDate.
     *
     * @param  fileDate  The dFileDate to set.
     */
    public void setFileDate(Date fileDate)
    {
        dFileDate = fileDate;
    }

    /**
     * This method sets the value for sFileName.
     *
     * @param  fileName  The sFileName to set.
     */
    public void setFileName(String fileName)
    {
        sFileName = fileName;
    }

    /**
     * This method sets the value for lFileSize.
     *
     * @param  fileSize  The lFileSize to set.
     */
    public void setFileSize(long fileSize)
    {
        lFileSize = fileSize;
    }

    /**
     * This method sets the value for sJarName.
     *
     * @param  jarName  The sJarName to set.
     */
    public void setJarName(String jarName)
    {
        sJarName = jarName;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  sHash  DOCUMENT ME!
     */
    public void setMD5Hash(String sHash)
    {
        sMD5Hash = sHash;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  sRelative  DOCUMENT ME!
     */
    public void setRelativeDir(String sRelative)
    {
        sRelativeDir = sRelative;
    }
}
