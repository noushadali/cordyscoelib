package com.cordys.coe.util.classpath;

import com.cordys.coe.util.system.SystemInfo;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import java.util.ArrayList;

import java.util.regex.Pattern;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * This class can be used to find out from which folder or jar file a certain class is loaded.
 *
 * @author  pgussow
 */
public class FindClass
    implements ICoEClassLoaderListener
{
    /**
     * Holds the list of files for the class loader.
     */
    private File[] m_afFiles;
    /**
     * Holds whether or not to load java. and javax. classes.
     */
    private boolean m_bLoadReservedClasses;
    /**
     * Holds whether or not the folder should be recursed to find files.
     */
    private boolean m_bRecursive;
    /**
     * Holds the result object.
     */
    private FindClassResult m_fcResult = null;
    /**
     * Holds the regex pattern to match files to add to the classpath.
     */
    private String m_sFilenamePattern;
    /**
     * Holds the fully qualified name of the class that will be searched for.
     */
    private String m_sFQN;
    /**
     * Holds the folder in which the jars are present.
     */
    private String m_sJarFolder;
    /**
     * Holds the search type to use.
     */
    private SearchType m_stSearchType = SearchType.CURRENT_CLASSPATH;

    /**
     * Creates a new FindClass object.
     *
     * @param  sFQN                  The fully qualified name of the class that will be searched
     *                               for.
     * @param  bLoadReservedClasses  pwWriter The stream to write the output to
     */
    public FindClass(String sFQN, boolean bLoadReservedClasses)
    {
        this(sFQN, null, bLoadReservedClasses);
    }

    /**
     * Creates a new FindClass object.
     *
     * @param  sFQN      The fully qualified name of the class that will be searched for.
     * @param  psStream  The stream to write the output to
     */
    public FindClass(String sFQN, PrintStream psStream)
    {
        this(sFQN, new OutputStreamWriter(psStream), false);
    }

    /**
     * Creates a new FindClass object.
     *
     * @param  sFQN                  The fully qualified name of the class that will be searched
     *                               for.
     * @param  pwWriter              The stream to write the output to.
     * @param  bLoadReservedClasses  Whether or not to load java.* and javax.* classes.
     */
    public FindClass(String sFQN, Writer pwWriter, boolean bLoadReservedClasses)
    {
        m_sFQN = sFQN;
        m_fcResult = new FindClassResult(pwWriter);
        m_bLoadReservedClasses = bLoadReservedClasses;
        m_stSearchType = SearchType.CURRENT_CLASSPATH;
    }

    /**
     * Creates a new FindClass object. This will find classes based on the file list passed on.
     *
     * @param  sFQN                  The The fully qualified name of the class that will be searched
     *                               for.
     * @param  bLoadReservedClasses  Whether or not to load java.* and javax.* classes.
     * @param  alFiles               The list of files for the classpath.
     */
    public FindClass(String sFQN, boolean bLoadReservedClasses, ArrayList<File> alFiles)
    {
        this(sFQN, null, bLoadReservedClasses);

        m_stSearchType = SearchType.FILE_LIST;

        m_afFiles = alFiles.toArray(new File[0]);
    }

    /**
     * Creates a new FindClass object.
     *
     * @param  sFQN                  The The fully qualified name of the class that will be searched
     *                               for.
     * @param  bLoadReservedClasses  Whether or not to load java.* and javax.* classes.
     * @param  sJarFolder            The base folder to find the jars.
     * @param  bRecursive            Whether or not to recurse the folders.
     * @param  sFilenamePattern      The filename pattern for the files.
     */
    public FindClass(String sFQN, boolean bLoadReservedClasses, String sJarFolder,
                     boolean bRecursive, String sFilenamePattern)
    {
        this(sFQN, null, bLoadReservedClasses);

        m_stSearchType = SearchType.JAR_FOLDER;

        m_sJarFolder = sJarFolder;
        m_bRecursive = bRecursive;
        m_sFilenamePattern = sFilenamePattern;
    }

    /**
     * This method returns the details about from which classpath entry the given class will be
     * loaded.
     *
     * @param   sClassName  The name of the class.
     *
     * @return  The details for the class.
     */
    public static String findClass(String sClassName)
    {
        String sReturn = null;
        FindClass fc = new FindClass(sClassName, true);
        IFindClassResult fcrResult = fc.execute();

        sReturn = fcrResult.getLogOutput();

        return sReturn;
    }

    /**
     * Main method.
     *
     * @param  saArguments  The commandline arguments.
     */
    public static void main(String[] saArguments)
    {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.DEBUG);

        if (saArguments.length == 0)
        {
            printUsage();
            System.exit(1);
        }

        try
        {
            FindClass fc = null;

            if (saArguments.length > 1)
            {
                fc = new FindClass(saArguments[0], true, saArguments[1], true, "^.+.jar$|^.+.zip$");
            }
            else
            {
                fc = new FindClass(saArguments[0], System.out);
            }

            IFindClassResult fcr = fc.execute();

            System.out.println("Found: " + fcr.isOK());

            if (fcr.isOK())
            {
                System.out.println("Location: " + fcr.getLocation());
            }
            else
            {
                System.out.println(fcr.getLogOutput());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * This method is called when a class is loaded by the classloader.
     *
     * @param  sFQN                     The fully qualified name of the class that was loaded.
     * @param  cpeSource                The ClassPathEntry from which it was loaded.
     * @param  bLoadedByCoEClassLoader  Whether or not the class was loaded by the CoEClassLoader.
     *
     * @see    com.cordys.coe.util.classpath.ICoEClassLoaderListener#classLoaded(java.lang.String,
     *         com.cordys.coe.util.classpath.ClassPathEntry, boolean)
     */
    public void classLoaded(String sFQN, ClassPathEntry cpeSource, boolean bLoadedByCoEClassLoader)
    {
        if (sFQN.equals(m_sFQN))
        {
            if (bLoadedByCoEClassLoader == true)
            {
                m_fcResult.println("Class " + m_sFQN + " loaded via " + cpeSource);
                m_fcResult.m_sLocation = cpeSource.toString();
            }
            else
            {
                m_fcResult.println("Class " + m_sFQN + " loaded via the system class loader.");
                m_fcResult.m_sLocation = "SYSTEM CLASSLOADER";
            }
        }
    }

    /**
     * This method will find the class in the current classpath.
     *
     * @return  The result object.
     */
    public IFindClassResult execute()
    {
        m_fcResult.println("Finding class: " + m_sFQN);
        m_fcResult.println();

        CoEClassLoader ccl = null;

        switch (m_stSearchType)
        {
            case CURRENT_CLASSPATH:
                m_fcResult.println(SystemInfo.getBootClasspath());
                m_fcResult.println(SystemInfo.getJVMClasspath());
                ccl = findClassViaCurrentClasspath();
                break;

            case JAR_FOLDER:
                m_fcResult.println("Searching files via a base folder.");
                ccl = findClassViaJarFolder();
                break;

            case FILE_LIST:
                m_fcResult.println("Searching files via a predefined file list.");
                ccl = findClassViaFileList();
                break;
        }

        ccl.addClassLoaderListener(this);

        try
        {
            ccl.loadClass(m_sFQN);
        }
        catch (ClassNotFoundException e)
        {
            m_fcResult.m_tThrowable = e;
            m_fcResult.printStackTrace(e);
        }

        return m_fcResult;
    }

    /**
     * This method prints the usage for this tool.
     */
    private static void printUsage()
    {
        System.out.println("Usage:\n");
        System.out.println("\tjava com.cordys.coe.util.classpath.FindClass <fully.qualified.name> <jar-folder>");
    }

    /**
     * This method recursively adds all jar files to the list.
     *
     * @param  fFolder           The folder.
     * @param  alfFiles          The list of files.
     * @param  bRecursive        Whether or not to recurse subfolders.
     * @param  sFilenamePattern  The pattern for the files to match.
     */
    private void addJarsFromFolder(File fFolder, ArrayList<File> alfFiles, boolean bRecursive,
                                   String sFilenamePattern)
    {
        String[] asFiles = fFolder.list();
        Pattern pPattern = Pattern.compile(sFilenamePattern);

        for (String sFile : asFiles)
        {
            File fFile = new File(fFolder, sFile);

            if (fFile.isDirectory() && bRecursive)
            {
                addJarsFromFolder(fFile, alfFiles, bRecursive, sFilenamePattern);
            }
            else if (pPattern.matcher(sFile).matches())
            {
                m_fcResult.println("Adding jar: " + fFile.getAbsolutePath());

                alfFiles.add(fFile);
            }
        }
    }

    /**
     * This method returns the classloader which will find the classes based on the current
     * classpath settings.
     *
     * @return  The classloader to use.
     */
    private CoEClassLoader findClassViaCurrentClasspath()
    {
        CoEClassLoader cclReturn = null;

        // If the environment variable FC_CP is set we'll prefix the current classpath with that one
        String sTemp = (String) System.getenv().get("FC_CP");

        if ((sTemp != null) && (sTemp.length() > 0))
        {
            System.setProperty("java.class.path",
                               sTemp + File.pathSeparator + System.getProperty("java.class.path"));
        }

        cclReturn = new CoEClassLoader(ClassLoader.getSystemClassLoader(), new String[] { ".+" },
                                       "CoEClassloader", null, m_bLoadReservedClasses);

        return cclReturn;
    }

    /**
     * This method will create the classloader to load the files via the given file list.
     *
     * @return  The classloader to use.
     */
    private CoEClassLoader findClassViaFileList()
    {
        return new CoEClassLoader(ClassLoader.getSystemClassLoader(), new String[] { ".+" },
                                  "CoEClassloader", m_afFiles, m_bLoadReservedClasses);
    }

    /**
     * This method creates the classloader to search for JARs based on a folder.
     *
     * @return  The classloader to use.
     */
    private CoEClassLoader findClassViaJarFolder()
    {
        CoEClassLoader cclReturn = null;

        ArrayList<File> alfFiles = new ArrayList<File>();

        if ((m_sJarFolder != null) && (m_sJarFolder.length() > 0))
        {
            parseJarFolder(alfFiles, m_sJarFolder, m_bRecursive, m_sFilenamePattern);
        }

        cclReturn = new CoEClassLoader(ClassLoader.getSystemClassLoader(), new String[] { ".+" },
                                       "CoEClassloader", alfFiles.toArray(new File[0]),
                                       m_bLoadReservedClasses);
        return cclReturn;
    }

    /**
     * This method fills the list files with the actual files in the JarFolder.
     *
     * @param  alfFiles          The list in which all entries should be added.
     * @param  sJARFolder        The base folder for the jar files
     * @param  bRecursive        Whether or not to recurse the folders.
     * @param  sFilenamePattern  The filename pattern for the actual files
     */
    private void parseJarFolder(ArrayList<File> alfFiles, String sJARFolder, boolean bRecursive,
                                String sFilenamePattern)
    {
        File fJarFolder = new File(sJARFolder);

        addJarsFromFolder(fJarFolder, alfFiles, bRecursive, sFilenamePattern);
    }

    /**
     * Holds the different search types.
     */
    private enum SearchType
    {
        CURRENT_CLASSPATH,
        JAR_FOLDER,
        FILE_LIST;
    }

    /**
     * Holds the result object when doing a query.
     *
     * @author  pgussow
     */
    private class FindClassResult
        implements IFindClassResult
    {
        /**
         * Holds the list of files searched.
         */
        private ArrayList<File> m_alfFiles = null;
        /**
         * Holds the writer to use.
         */
        private PrintWriter m_bwResultWriter;
        /**
         * Holds the location when it was found.
         */
        private String m_sLocation = "";
        /**
         * Holds the String writer that is used.
         */
        private StringWriter m_swStringWriter;
        /**
         * Holds the exception.
         */
        private Throwable m_tThrowable = null;

        /**
         * Constructor.
         *
         * @param  wWriter  The writer to use.
         */
        public FindClassResult(Writer wWriter)
        {
            if (wWriter == null)
            {
                m_swStringWriter = new StringWriter(2048);
                m_bwResultWriter = new PrintWriter(m_swStringWriter);
            }
            else
            {
                if (wWriter instanceof PrintWriter)
                {
                    m_bwResultWriter = (PrintWriter) wWriter;
                }
                else
                {
                    m_bwResultWriter = new PrintWriter(wWriter);
                }
            }
        }

        /**
         * This method gets the location of the JAR file.
         *
         * @return  The location of the JAR file.
         *
         * @see     com.cordys.coe.util.classpath.IFindClassResult#getLocation()
         */
        public String getLocation()
        {
            return m_sLocation;
        }

        /**
         * This method gets the log output for this search.
         *
         * @return  The log output for this search.
         *
         * @see     com.cordys.coe.util.classpath.IFindClassResult#getLogOutput()
         */
        public String getLogOutput()
        {
            if (m_swStringWriter != null)
            {
                return m_swStringWriter.getBuffer().toString();
            }
            else
            {
                return (isOK() ? ("Class " + FindClass.this.m_sFQN + " found in " + getLocation())
                               : ("Class " + FindClass.this.m_sFQN + " not found."));
            }
        }

        /**
         * This method gets the list of files/folders that this class has scanned.
         *
         * @return  The list of files/folders that this class has scanned.
         *
         * @see     com.cordys.coe.util.classpath.IFindClassResult#getSearchedFiles()
         */
        public ArrayList<File> getSearchedFiles()
        {
            return m_alfFiles;
        }

        /**
         * This method gets the exception that has occurred.
         *
         * @return  The exception that has occurred.
         *
         * @see     com.cordys.coe.util.classpath.IFindClassResult#getThrowable()
         */
        public Throwable getThrowable()
        {
            return m_tThrowable;
        }

        /**
         * This method gets whether or not the search was executed ok.
         *
         * @return  Whether or not the search was executed ok.
         *
         * @see     com.cordys.coe.util.classpath.IFindClassResult#isOK()
         */
        public boolean isOK()
        {
            return (m_tThrowable == null);
        }

        /**
         * This method prints an empty line.
         */
        public void println()
        {
            println("");
        }

        /**
         * This method writes the message to the writer.
         *
         * @param  sMessage  The message to log.
         */
        public void println(String sMessage)
        {
            if (m_bwResultWriter != null)
            {
                m_bwResultWriter.write(sMessage);
                m_bwResultWriter.write(System.getProperty("line.separator"));
            }
        }

        /**
         * This method prints the stacktrace to the proper stream.
         *
         * @param  tException  The exception to print.
         */
        public void printStackTrace(Throwable tException)
        {
            if (tException != null)
            {
                if (m_bwResultWriter != null)
                {
                    tException.printStackTrace(m_bwResultWriter);
                }
            }
        }
    }
}
