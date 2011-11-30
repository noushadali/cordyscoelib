package com.cordys.coe.util.checkclasspath;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Find;
import com.eibus.xml.nom.Node;

import com.twmacinta.util.MD5;
import com.twmacinta.util.MD5OutputStream;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.security.cert.Certificate;

import java.util.*;

import java.util.jar.*;

/**
 * DOCUMENTME.
 *
 * @author  tveldhui
 * 
 * @deprecated This class uses the old Find library for XML searching. This is not namespace safe!
 */
public class CheckClassPath
{
    /**
     * DOCUMENTME.
     */
    private static HashMap<String, HashMap<String, ClassInfo>> hmClasses;
    /**
     * DOCUMENTME.
     */
    private static int iNumberOfJars;
    /**
     * DOCUMENTME.
     */
    private static int iNumberOfDirs;

    /**
     * DOCUMENTME.
     */
    public static void generateClassList()
    {
        hmClasses = new HashMap<String, HashMap<String, ClassInfo>>();

        iNumberOfJars = 0;
        iNumberOfDirs = 0;

        int[] iJarIndex = null;
        String sSystemClassPath;
        String[] sSystemClassPaths;

        sSystemClassPath = System.getProperty("java.class.path");
        sSystemClassPaths = sSystemClassPath.split(";");
        iJarIndex = new int[sSystemClassPaths.length];

        for (int iCount = 0; iCount < sSystemClassPaths.length; iCount++)
        {
            if (sSystemClassPaths[iCount].endsWith(".jar") ||
                    sSystemClassPaths[iCount].endsWith(".zip"))
            {
                iJarIndex[iNumberOfJars] = iCount;
                iNumberOfJars++;
            }
            else
            {
                parseFolder(sSystemClassPaths[iCount] + "/", "", iCount);
                iNumberOfDirs++;
            }
        }

        /*
         * Get all class info from found jar files...
         */
        for (int iCount = 0; iCount < iNumberOfJars; iCount++)
        {
            try
            {
                Enumeration<?> eFilesInJar;
                JarFile jfJarFile = new JarFile(sSystemClassPaths[iJarIndex[iCount]]);
                eFilesInJar = jfJarFile.entries();

                while (eFilesInJar.hasMoreElements())
                {
                    try
                    {
                        JarEntry jeTemp = (JarEntry) eFilesInJar.nextElement();
                        byte[] baBuffer = new byte[32767];
                        InputStream isTemp = jfJarFile.getInputStream(jeTemp);
                        int iNrOfBytes = isTemp.read(baBuffer);
                        OutputStream osTemp = new ByteArrayOutputStream();
                        MD5OutputStream md5Out = new MD5OutputStream(osTemp);

                        while (iNrOfBytes > -1)
                        {
                            md5Out.write(baBuffer, 0, iNrOfBytes);
                            iNrOfBytes = isTemp.read(baBuffer);
                        }

                        String sHash = MD5.asHex(md5Out.hash());
                        storeJarFileData(jeTemp, sSystemClassPaths[iJarIndex[iCount]], sHash,
                                         iJarIndex[iCount]);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            catch (Exception e)
            {
                System.out.println("The Jar file named " + sSystemClassPaths[iJarIndex[iCount]] +
                                   " is curropt or non existing");
            }
        }
    } //

    /**
     * This method returns all classes.
     *
     * @return  a node with all classes.
     */
    public static int getAllClasses()
    {
        generateClassList();

        HashMap<String, HashMap<String, ClassInfo>> hmClassesCollection = hmClasses;
        Document dDoc = new Document();
        int iXmlNode = dDoc.createElement("classes");
        int iClassNode;
        int iEntryNode;
        int iNumberOfClasses = 0;

        for (Iterator<String> iClasses = hmClassesCollection.keySet().iterator();
                 iClasses.hasNext();)
        {
            iNumberOfClasses++;

            String sClassName = iClasses.next();

            HashMap<?, ?> hmClassData = hmClasses.get(sClassName);

            iClassNode = dDoc.createElement("class", iXmlNode);
            dDoc.createTextElement("name", sClassName, iClassNode);

            for (Iterator<?> iClassData = hmClassData.keySet().iterator(); iClassData.hasNext();)
            {
                iEntryNode = dDoc.createElement("entry", iClassNode);

                String sJarName = (String) iClassData.next();
                ClassInfo ciFileInfo = (ClassInfo) hmClassData.get(sJarName);
                dDoc.createTextElement("Jarfile", ciFileInfo.getJarName(), iEntryNode);
                dDoc.createTextElement("FileSize", String.valueOf(ciFileInfo.getFileSize()),
                                       iEntryNode);
                dDoc.createTextElement("Date", "" + ciFileInfo.getFileDate(), iEntryNode);
                dDoc.createTextElement("FileCRC", "" + ciFileInfo.getFileCrc(), iEntryNode);
                dDoc.createTextElement("Comment", ciFileInfo.getFileComment(), iEntryNode);
                dDoc.createTextElement("MD5Hash", ciFileInfo.getMD5Hash(), iEntryNode);
                dDoc.createTextElement("ClassOrderLevel", "" + ciFileInfo.getClassOrderLevel(),
                                       iEntryNode);
                dDoc.createTextElement("ClassDir", "" + ciFileInfo.getClassPathDir(), iEntryNode);

                if (ciFileInfo.getFileAttributes() != null)
                {
                    if (ciFileInfo.getFileAttributes().size() > 0)
                    {
                        dDoc.createTextElement("Attributes",
                                               ciFileInfo.getFileAttributes().toString(),
                                               iEntryNode);
                    }
                }

                if (ciFileInfo.getFileClass() != null)
                {
                    dDoc.createTextElement("Class", ciFileInfo.getFileClass().toString(),
                                           iEntryNode);
                }

                if (ciFileInfo.getRelativeDir() != null)
                {
                    dDoc.createTextElement("RelativePath", ciFileInfo.getRelativeDir(), iEntryNode);
                }
            }
        }

        int iInfoNode = dDoc.createElement("info", iXmlNode);
        dDoc.createTextElement("JarsInClasspath", "" + iNumberOfJars, iInfoNode);
        dDoc.createTextElement("DirsInClasspath", "" + iNumberOfDirs, iInfoNode);
        dDoc.createTextElement("TotalClasses", "" + iNumberOfClasses, iInfoNode);
        return iXmlNode;
    }

    /**
     * This searches the jarfiles that are in the classpath for duplicate classes. It will return
     * the classes and where they can be found. Even if the classes are simular.
     *
     * @return  XML containg info about the duplicate Classes
     */
    public static int getDuplicatAndDifferentClasses()
    {
        Document dDoc = new Document();
        int iXmlNode = dDoc.createElement("classes");
        int iClasses = getAllClasses();
        int iFoundClasses = 0;
        int[] iaClassNodes = Find.match(iClasses, "fChild<class>");

        for (int iCount = 0; iCount < iaClassNodes.length; iCount++)
        {
            int[] iaClassEntryNodes = Find.match(iaClassNodes[iCount], "fChild<entry>");

            if (iaClassEntryNodes.length > 1)
            {
                Node.appendToChildren(Node.clone(iaClassNodes[iCount], true), iXmlNode);
                iFoundClasses++;
            }
        }

        iaClassNodes = Find.match(iXmlNode, "fChild<class>");

        boolean bDifFound = false;

        for (int iCount = 0; iCount < iaClassNodes.length; iCount++)
        {
            int[] iaClassEntryNodes = Find.match(iaClassNodes[iCount], "fChild<entry>");

            int iFirstEntry = Find.firstMatch(iaClassEntryNodes[0], "fChild<MD5Hash>");
            String sFirstEntryHash = Node.getData(iFirstEntry);

            for (int iEntryCount = 1; iEntryCount < iaClassEntryNodes.length; iEntryCount++)
            {
                int iNextEntry = Find.firstMatch(iaClassEntryNodes[iEntryCount], "fChild<MD5Hash>");
                String sNextHash = Node.getData(iNextEntry);

                if (!sFirstEntryHash.equals(sNextHash))
                {
                    bDifFound = true;
                }
            }
        }

        if (!bDifFound)
        {
            iXmlNode = dDoc.createElement("classes");
        }
        Node.appendToChildren(Node.clone(Find.firstMatch(iClasses, "fChild<info>"), true),
                              iXmlNode);
        dDoc.createTextElement("Result", "" + iFoundClasses, iXmlNode);

        return iXmlNode;
    }

    /**
     * This searches the jarfiles that are in the classpath for duplicate classes. It will return
     * the classes and where they can be found. Even if the classes are simular.
     *
     * @return  XML containg info about the duplicate Classes
     */
    public static int getDuplicateClasses()
    {
        Document dDoc = new Document();
        int iXmlNode = dDoc.createElement("classes");
        int iFoundClasses = 0;
        int iClasses = getAllClasses();

        int[] iaClassNodes = Find.match(iClasses, "fChild<class>");

        for (int iCount = 0; iCount < iaClassNodes.length; iCount++)
        {
            int[] iaClassEntryNodes = Find.match(iaClassNodes[iCount], "fChild<entry>");

            if (iaClassEntryNodes.length > 1)
            {
                Node.appendToChildren(Node.clone(iaClassNodes[iCount], true), iXmlNode);
                iFoundClasses++;
            }
        }
        Node.appendToChildren(Node.clone(Find.firstMatch(iClasses, "fChild<info>"), true),
                              iXmlNode);
        dDoc.createTextElement("Result", "" + iFoundClasses, iXmlNode);

        return iXmlNode;
    }

    /**
     * This method returns all non-jar classes.
     *
     * @return  All non-jar classes.
     */
    public static int getNonJarClasses()
    {
        Document dDoc = new Document();
        int iXmlNode = dDoc.createElement("classes");
        int iClasses = getAllClasses();
        int iFoundClasses = 0;
        boolean bCloneClass = false;

        int[] iaClassNodes = Find.match(iClasses, "fChild<class>");

        for (int iCount = 0; iCount < iaClassNodes.length; iCount++)
        {
            int[] iaClassEntryNodes = Find.match(iaClassNodes[iCount], "fChild<entry>");

            for (int iEntryCount = 0; iEntryCount < iaClassEntryNodes.length; iEntryCount++)
            {
                int iEntryJar = Find.firstMatch(iaClassEntryNodes[iEntryCount], "fChild<Jarfile>");

                String sEntryJar = Node.getData(iEntryJar);

                if ((sEntryJar == null) || sEntryJar.equals(""))
                {
                    bCloneClass = true;
                    iFoundClasses++;
                }
            }

            if (bCloneClass)
            {
                bCloneClass = false;
                Node.appendToChildren(Node.clone(iaClassNodes[iCount], true), iXmlNode);
            }
        }
        Node.appendToChildren(Node.clone(Find.firstMatch(iClasses, "fChild<info>"), true),
                              iXmlNode);
        dDoc.createTextElement("Result", "" + iFoundClasses, iXmlNode);
        return iXmlNode;
    }

    /**
     * DOCUMENTME.
     *
     * @param  args
     */
    public static void main(String[] args)
    {
        int iClasses = getNonJarClasses();
        System.out.println(Node.writeToString(iClasses, true));
    }

    /**
     * DOCUMENTME.
     *
     * @param  sBaseFolder
     * @param  sRelative
     * @param  iClassOrder
     */
    private static void parseFolder(String sBaseFolder, String sRelative, int iClassOrder)
    {
        File fFolder = new File(sBaseFolder + sRelative);

        if (fFolder.isDirectory())
        {
            String[] saFiles = fFolder.list();

            for (int iCount = 0; iCount < saFiles.length; iCount++)
            {
                String sFilename = saFiles[iCount];
                File fTemp = new File(sBaseFolder + sRelative + sFilename);

                if (fTemp.isDirectory())
                {
                    // System.out.println("Dept of dir: " + iCount);

                    parseFolder(sBaseFolder, sRelative + sFilename + "/", iClassOrder);
                }
                else
                {
                    // It's a class-file
                    if (fTemp.getName().endsWith(".class"))
                    {
                        try
                        {
                            byte[] baBuffer = new byte[32767];
                            InputStream isTemp = new FileInputStream(fTemp.getCanonicalPath());
                            int iNrOfBytes = isTemp.read(baBuffer);
                            OutputStream osTemp = new ByteArrayOutputStream();
                            MD5OutputStream md5Out = new MD5OutputStream(osTemp);

                            while (iNrOfBytes > -1)
                            {
                                md5Out.write(baBuffer, 0, iNrOfBytes);
                                iNrOfBytes = isTemp.read(baBuffer);
                            }

                            String sHash = MD5.asHex(md5Out.hash());
                            storeClassFileData(fTemp, sBaseFolder, sHash, iClassOrder, sRelative);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        // System.out.println("Dept of class: " + fTemp.getName() + ": "+ iCount);
                    }
                }
            }
        }
    }

    /**
     * DOCUMENTME.
     *
     * @param  fClassEntry       This method stores the class info into the hashmap
     * @param  sDirPath          - Enumerator containing the JarEntry
     * @param  sHash             - String containing the name of the Jarfile where the JarEntry is
     *                           from.
     * @param  iClassOrderLever  DOCUMENTME
     * @param  sRelative         DOCUMENTME
     */
    private static void storeClassFileData(File fClassEntry, String sDirPath, String sHash,
                                           int iClassOrderLever, String sRelative)
    {
        String sClassName = fClassEntry.getName();

        if (sClassName.endsWith(".class"))
        {
            long lCrc = -1;
            long lFileDate = fClassEntry.lastModified();
            long lFileSize = fClassEntry.length();
            long lCompressedSize = -1;
            Attributes aAttributes = new Attributes();
            Class<?> cClass = fClassEntry.getClass();
            String sComment = "";
            Date dFileDate = new Date();
            dFileDate.setTime(lFileDate);

            ClassInfo ciFileInfo = new ClassInfo(sClassName, "", lCrc, dFileDate, lFileSize,
                                                 lCompressedSize, aAttributes, null, cClass,
                                                 sComment, sHash, iClassOrderLever, sDirPath,
                                                 sRelative);

            HashMap<String, ClassInfo> hmClassData;
            hmClassData = hmClasses.get(sClassName);

            if (hmClassData == null)
            {
                hmClassData = new HashMap<String, ClassInfo>();
                hmClasses.put(sClassName, hmClassData);
            }
            hmClassData.put(sRelative, ciFileInfo);
        }
    }

    /**
     * DOCUMENTME.
     *
     * @param  oJarEntry         This method stores the class info into the hashmap
     * @param  sJarName          - Enumerator containing the JarEntry
     * @param  sHash             - String containing the name of the Jarfile where the JarEntry is
     *                           from.
     * @param  iClassOrderLever  DOCUMENTME
     */
    private static void storeJarFileData(Object oJarEntry, String sJarName, String sHash,
                                         int iClassOrderLever)
    {
        JarEntry entry = (JarEntry) oJarEntry;
        String sClassName = entry.getName();

        if (sClassName.endsWith(".class"))
        {
            long lCrc = entry.getCrc();
            long lFileDate = entry.getTime();
            long lFileSize = entry.getSize();
            long lCompressedSize = entry.getCompressedSize();
            Attributes aAttributes = new Attributes();

            try
            {
                aAttributes = entry.getAttributes();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            Certificate[] acCertificates = entry.getCertificates();

            Class<?> cClass = entry.getClass();
            String sComment = entry.getComment();
            Date dFileDate = new Date();
            dFileDate.setTime(lFileDate);

            ClassInfo ciFileInfo = new ClassInfo(sClassName, sJarName, lCrc, dFileDate, lFileSize,
                                                 lCompressedSize, aAttributes, acCertificates,
                                                 cClass, sComment, sHash, iClassOrderLever, "", "");

            HashMap<String, ClassInfo> hmClassData;
            hmClassData = hmClasses.get(sClassName);

            if (hmClassData == null)
            {
                hmClassData = new HashMap<String, ClassInfo>();
                hmClasses.put(sClassName, hmClassData);
            }
            hmClassData.put(sJarName, ciFileInfo);
        }
    }
}
