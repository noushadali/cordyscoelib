package com.cordys.coe.util.classpath;

import java.io.File;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * This classloader will load the classes from the classpath as required. This can be used to
 * instantiate classes in a different classloader. That way on-the-fly updating of your libraries
 * can be done. This is however limited. There are a couple of packages that will be loaded by this
 * classloader. If the package is different then the default classloader will be used. For
 * specifying the packages a regex can be used. If you want this classloader to load all classes you
 * need to specify .+ as a package pattern.
 *
 * @author  pgussow
 */
public class CoEClassLoader extends ClassLoader
{
    /**
     * Holds the logger to use.
     */
    private static final Logger LOGGER = Logger.getLogger(CoEClassLoader.class.getName());
    /**
     * Identifies the system property to read the classpath from.
     */
    private static final String CLASSPATH_PROPERTY = "java.class.path";
    /**
     * Identifies the system property to read the boot classpath from.
     */
    private static final String BOOT_CLASSPATH_PROPERTY = "sun.boot.class.path";
    /**
     * Holds the list of listeners for the classloader.
     */
    private ArrayList<ICoEClassLoaderListener> m_alListeners = new ArrayList<ICoEClassLoaderListener>();
    /**
     * Holds all the classpath entries.
     */
    private List<ClassPathEntry> m_alEntries = new ArrayList<ClassPathEntry>();
    /**
     * Holds the list of patterns to which the classname must match.
     */
    private List<Pattern> m_alPatterns = new ArrayList<Pattern>();
    /**
     * Holds the name of this classloader.
     */
    private String m_sName = "unknown";
    /**
     * Holds whether or not java. and javax. classes should be loaded by this classloader.
     */
    private boolean m_bLoadReserved = false;

    /**
     * Creates a new CoEClassLoader object. It will parse the classpath to make sure it knows where
     * to find the needed classes.
     *
     * @param  saPatterns  Holds the regex patterns for the classes that should be loaded via this
     *                     classloader. When null, no class will be loaded by this classloader.
     */
    public CoEClassLoader(String[] saPatterns)
    {
        this(null, saPatterns, "unknown", null);
    }

    /**
     * Creates a new CoEClassLoader object. It will parse the classpath to make sure it knows where
     * to find the needed classes.
     *
     * @param  clParent  The parent classloader.
     */
    public CoEClassLoader(ClassLoader clParent)
    {
        this(clParent, null, "unknown", null);
    }

    /**
     * Creates a new CoEClassLoader object. It will parse the classpath to make sure it knows where
     * to find the needed classes.
     *
     * @param  clParent    The parent classloader.
     * @param  saPatterns  Holds the regex patterns for the classes that should be loaded via this
     *                     classloader. When null, no class will be loaded by this classloader.
     * @param  sName       The name of the classloader.
     */
    public CoEClassLoader(ClassLoader clParent, String[] saPatterns, String sName)
    {
        this(clParent, saPatterns, sName, null);
    }

    /**
     * Creates a new CoEClassLoader object. It will parse the classpath to make sure it knows where
     * to find the needed classes.
     *
     * @param  clParent            The parent classloader.
     * @param  saPatterns          Holds the regex patterns for the classes that should be loaded
     *                             via this classloader. When null, no class will be loaded by this
     *                             classloader.
     * @param  sName               The name of the classloader.
     * @param  afClassPathEntries  The list of files that should be added to the classpath.
     */
    public CoEClassLoader(ClassLoader clParent, String[] saPatterns, String sName,
                          File[] afClassPathEntries)
    {
        this(clParent, saPatterns, sName, afClassPathEntries, false);
    }

    /**
     * Creates a new CoEClassLoader object. It will parse the classpath to make sure it knows where
     * to find the needed classes.
     *
     * @param  clParent            The parent classloader.
     * @param  saPatterns          Holds the regex patterns for the classes that should be loaded
     *                             via this classloader. When null, no class will be loaded by this
     *                             classloader.
     * @param  sName               The name of the classloader.
     * @param  afClassPathEntries  The list of files that should be added to the classpath.
     * @param  bLoadReserved       Whether or not java.* and javax.* classes should be loaded.
     */
    public CoEClassLoader(ClassLoader clParent, String[] saPatterns, String sName,
                          File[] afClassPathEntries, boolean bLoadReserved)
    {
        super(clParent);
        m_sName = sName;
        initialize(afClassPathEntries);

        // Add the patterns to a hashmap.
        if ((saPatterns != null) && (saPatterns.length > 0))
        {
            for (int iCount = 0; iCount < saPatterns.length; iCount++)
            {
                if (LOGGER.isDebugEnabled())
                {
                    LOGGER.debug("Adding class pattern: " + saPatterns[iCount]);
                }

                Pattern pPattern = Pattern.compile(saPatterns[iCount]);
                m_alPatterns.add(pPattern);
            }
        }

        m_bLoadReserved = bLoadReserved;
    }

    /**
     * This methods adds the listener to this class loader.
     *
     * @param  ccllListener  The listener to add.
     */
    public void addClassLoaderListener(ICoEClassLoaderListener ccllListener)
    {
        if (!m_alListeners.contains(ccllListener))
        {
            m_alListeners.add(ccllListener);
        }
    }

    /**
     * This method gets the name of this classloader.
     *
     * @return  The name of this classloader.
     */
    public String getName()
    {
        return m_sName;
    }

    /**
     * Insers the a new classpath entry to the class loader.
     *
     * @param   sPath
     * @param   bAsFirst
     *
     * @throws  Exception
     */
    public void insertClasspathEntry(String sPath, boolean bAsFirst)
                              throws Exception
    {
        if (bAsFirst)
        {
            m_alEntries.add(0, new ClassPathEntry(sPath));
        }
        else
        {
            m_alEntries.add(new ClassPathEntry(sPath));
        }
    }

    /**
     * This method loads the class. It will examine the classpath and try to load the classes from
     * it.
     *
     * @param   sFullyQualifiedName  The name of the class to load.
     * @param   bResolve             Whether or not to resolve the class.
     *
     * @return  The class object for this class.
     *
     * @throws  ClassNotFoundException  When the class can not be found.
     */
    @Override public Class<?> loadClass(String sFullyQualifiedName, boolean bResolve)
                                 throws ClassNotFoundException
    {
        Class<?> cReturn = null;

        // First, see if we've already dealt with this one
        cReturn = findLoadedClass(sFullyQualifiedName);

        // Appearantly we did not load it yet.
        if (cReturn == null)
        {
            // Check to see if we SHOULD load it.
            boolean bLoad = false;

            for (Iterator<Pattern> iPatterns = m_alPatterns.iterator(); iPatterns.hasNext();)
            {
                Pattern pPattern = iPatterns.next();

                synchronized (pPattern)
                {
                    if (pPattern.matcher(sFullyQualifiedName).matches())
                    {
                        bLoad = true;
                        break;
                    }
                }
            }

            // Check a few prohibited packages:
            if (sFullyQualifiedName.startsWith("java.lang") ||
                    ((m_bLoadReserved == false) &&
                         (sFullyQualifiedName.startsWith("java.") ||
                              sFullyQualifiedName.startsWith("javax."))))
            {
                if (LOGGER.isDebugEnabled())
                {
                    LOGGER.debug("Cannot load prohibited class: " + sFullyQualifiedName);
                }
                bLoad = false;
            }

            // Try to load the class ourselves.
            if (bLoad == true)
            {
                if (LOGGER.isDebugEnabled())
                {
                    LOGGER.debug("Loading class " + sFullyQualifiedName);
                }

                for (Iterator<ClassPathEntry> iEntries = m_alEntries.iterator();
                         iEntries.hasNext();)
                {
                    ClassPathEntry cpe = iEntries.next();

                    if (cpe.hasClass(sFullyQualifiedName))
                    {
                        byte[] baClass = cpe.getBytes(sFullyQualifiedName);
                        cReturn = defineClass(sFullyQualifiedName, baClass, 0, baClass.length);

                        if (LOGGER.isDebugEnabled())
                        {
                            LOGGER.debug("Loaded via: " + cpe);
                        }
                        fireClassLoaded(sFullyQualifiedName, cpe, true);
                        break;
                    }
                }
            }

            // If the class was not found in this classpath we'll let the super figure it out.
            if (cReturn == null)
            {
                if (LOGGER.isDebugEnabled())
                {
                    if (bLoad == true)
                    {
                        LOGGER.debug("Could not find the class in the JVM classpath. Going to pass it on to the system classloader.");
                    }
                    else
                    {
                        LOGGER.debug("We're not responsible for loading class " +
                                     sFullyQualifiedName +
                                     ". Having the system classloader load it.");
                    }
                }
                cReturn = findSystemClass(sFullyQualifiedName);
                fireClassLoaded(sFullyQualifiedName, null, false);
            }
        }

        // It the class could still not be found, it's an error.
        if (cReturn == null)
        {
            throw new ClassNotFoundException(sFullyQualifiedName);
        }

        return cReturn;
    }

    /**
     * This methods removes the listener to this class loader.
     *
     * @param  ccllListener  The listener to add.
     */
    public void removeClassLoaderListener(ICoEClassLoaderListener ccllListener)
    {
        if (m_alListeners.contains(ccllListener))
        {
            m_alListeners.remove(ccllListener);
        }
    }

    /**
     * This method is fires the classLoaded event.
     *
     * @param  sFQN                     The fully qualified name of the class that was loaded.
     * @param  cpeSource                The ClassPathEntry from which it was loaded.
     * @param  bLoadedByCoEClassLoader  Whether or not the class was loaded by the CoEClassLoader.
     */
    private void fireClassLoaded(String sFQN, ClassPathEntry cpeSource,
                                 boolean bLoadedByCoEClassLoader)
    {
        for (Iterator<ICoEClassLoaderListener> iListeners = m_alListeners.iterator();
                 iListeners.hasNext();)
        {
            ICoEClassLoaderListener ccll = iListeners.next();
            ccll.classLoaded(sFQN, cpeSource, bLoadedByCoEClassLoader);
        }
    }

    /**
     * This method initializes the classpath that can be used.
     *
     * @param  afClassPathEntries  The entries for the classpath. If null it will use the classpath
     *                             as defined by the sun.boot.class.path and the java.class.path.
     */
    private void initialize(File[] afClassPathEntries)
    {
        if (afClassPathEntries == null)
        {
            String[] asClassPathProperties = new String[]
                                             {
                                                 BOOT_CLASSPATH_PROPERTY, CLASSPATH_PROPERTY
                                             };

            for (int iCPCount = 0; iCPCount < asClassPathProperties.length; iCPCount++)
            {
                String sClassPath = System.getProperty(asClassPathProperties[iCPCount]);

                if ((sClassPath != null) && (sClassPath.length() > 0))
                {
                    String[] saEntries = sClassPath.split(File.pathSeparator);

                    for (int iCount = 0; iCount < saEntries.length; iCount++)
                    {
                        ClassPathEntry cpe;

                        try
                        {
                            cpe = new ClassPathEntry(saEntries[iCount]);
                            m_alEntries.add(cpe);
                        }
                        catch (Exception e)
                        {
                            LOGGER.error("Error adding " + saEntries[iCount] + " to the classpath.",
                                         e);
                        }
                    }
                }
            }
        }
        else
        {
            // Add the files from the array.
            for (int iCount = 0; iCount < afClassPathEntries.length; iCount++)
            {
                File fEntry = afClassPathEntries[iCount];
                ClassPathEntry cpe;

                try
                {
                    cpe = new ClassPathEntry(fEntry.getCanonicalPath());
                    m_alEntries.add(cpe);
                }
                catch (Exception e)
                {
                    LOGGER.error("Error adding " + fEntry.getAbsolutePath() + " to the classpath.",
                                 e);
                }
            }
        }
    }
}
