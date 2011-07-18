package com.cordys.coe.tools.orgmanager.log4j;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.lang.reflect.Method;

import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;

import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * This class will use a different classloader to examine the classpath to find all appenders for
 * Log4J.
 *
 * @author  pgussow
 */
public class Log4JAppenderWrapper
{
    /**
     * Holds the logger that is used.
     */
    private static final Logger LOG = Logger.getLogger(Log4JAppenderWrapper.class);
    /**
     * Holds the list of appenders currently known to the classpath.
     */
    private LinkedHashMap<String, AppenderMetadata> m_lhmAppenders = new LinkedHashMap<String, AppenderMetadata>();
    /**
     * Holds the list of layouts currently known to the classpath.
     */
    private LinkedHashMap<String, LayoutMetadata> m_lhmLayouts = new LinkedHashMap<String, LayoutMetadata>();

    /**
     * Creates a new Log4JAppenderWrapper object.
     *
     * @throws  Exception  DOCUMENTME
     */
    public Log4JAppenderWrapper()
                         throws Exception
    {
        Package[] ap = Package.getPackages();

        ArrayList<Class<?>> alFound = new ArrayList<Class<?>>();

        for (Package p : ap)
        {
            List<Class<?>> l = getClassessOfInterface(p.getName(), Appender.class);

            if (l.size() > 0)
            {
                for (Class<?> c : l)
                {
                    if (!c.isInterface() &&
                            !c.getName().equals("org.apache.log4j.AppenderSkeleton") &&
                            !c.getName().equals("org.apache.log4j.lf5.LF5Appender"))
                    {
                        alFound.add(c);
                    }
                }
            }
        }

        // Now we have the list of classes, so we need to examine them to get the parameters.
        for (Class<?> cAppender : alFound)
        {
            LinkedHashMap<String, String> lhmFinalProperties = new LinkedHashMap<String, String>();

            // For each class we'll get the declared methods until we either reach the
            // appender interface or the AppenderSkeleton class.
            Class<?> cCurrent = cAppender;

            while (!cCurrent.getName().equals(Appender.class.getName()) &&
                       !cCurrent.getName().equals(AppenderSkeleton.class.getName()))
            {
                addPropertiesFromClass(cCurrent, lhmFinalProperties);

                cCurrent = cCurrent.getSuperclass();
            }

            // Now sort the properties.
            ArrayList<String> alTemp = new ArrayList<String>(lhmFinalProperties.keySet());
            Collections.sort(alTemp);

            LinkedHashMap<String, String> lhmTemp = new LinkedHashMap<String, String>();

            for (String sKey : alTemp)
            {
                lhmTemp.put(sKey, lhmFinalProperties.get(sKey));
            }
            lhmFinalProperties = lhmTemp;

            AppenderMetadata am = new AppenderMetadata();
            am.setName(cAppender.getName());

            for (String sKey : lhmFinalProperties.keySet())
            {
                PropertyMetadata pm = new PropertyMetadata();
                pm.setName(sKey);
                pm.setType(lhmFinalProperties.get(sKey));

                am.addPropertyMetadata(pm);
            }

            // Now we need to get the layout class for proper definition.
            Object oTemp = cAppender.newInstance();
            Method mGetLayout = cAppender.getMethod("requiresLayout");
            Object oLayout = mGetLayout.invoke(oTemp);

            if ((oLayout != null) && (((Boolean) oLayout) == true))
            {
                am.setRequiresLayout(true);
            }

            m_lhmAppenders.put(am.getName(), am);
        }

        // Now we need to get the different Layouts which are possible
        alFound = new ArrayList<Class<?>>();

        for (Package p : ap)
        {
            List<Class<?>> l = getClassessOfInterface(p.getName(), Layout.class);

            if (l.size() > 0)
            {
                for (Class<?> c : l)
                {
                    if (!c.isInterface() && !c.getName().equals(Layout.class.getName()))
                    {
                        alFound.add(c);
                    }
                }
            }
        }

        // Now we have the list of classes, so we need to examine them to get the parameters.
        for (Class<?> cLayout : alFound)
        {
            LinkedHashMap<String, String> lhmFinalProperties = new LinkedHashMap<String, String>();

            // For each class we'll get the declared methods until we either reach the
            // appender interface or the AppenderSkeleton class.
            Class<?> cCurrent = cLayout;

            while (!cCurrent.getName().equals(Layout.class.getName()))
            {
                addPropertiesFromClass(cCurrent, lhmFinalProperties);

                cCurrent = cCurrent.getSuperclass();
            }

            // Now sort the properties.
            ArrayList<String> alTemp = new ArrayList<String>(lhmFinalProperties.keySet());
            Collections.sort(alTemp);

            LinkedHashMap<String, String> lhmTemp = new LinkedHashMap<String, String>();

            for (String sKey : alTemp)
            {
                lhmTemp.put(sKey, lhmFinalProperties.get(sKey));
            }
            lhmFinalProperties = lhmTemp;

            LayoutMetadata lm = new LayoutMetadata();
            lm.setName(cLayout.getName());

            for (String sKey : lhmFinalProperties.keySet())
            {
                PropertyMetadata pm = new PropertyMetadata();
                pm.setName(sKey);
                pm.setType(lhmFinalProperties.get(sKey));

                lm.addPropertyMetadata(pm);
            }

            m_lhmLayouts.put(lm.getName(), lm);
        }
    }

    /**
     * This method lists all classes in the package.
     *
     * @param   pckgname  The name of the package.
     *
     * @return  The list of classes in a package.
     *
     * @throws  ClassNotFoundException  In case a class could not be loaded.
     */
    public static List<Class<?>> getClassesForPackage(String pckgname)
                                               throws ClassNotFoundException
    {
        // This will hold a list of directories matching the pckgname.
        // There may be more than one if a package is split over multiple jars/paths
        List<Class<?>> classes = new ArrayList<Class<?>>();
        ArrayList<File> directories = new ArrayList<File>();

        try
        {
            ClassLoader cld = Thread.currentThread().getContextClassLoader();

            if (cld == null)
            {
                throw new ClassNotFoundException("Can't get class loader.");
            }

            // Ask for all resources for the path
            Enumeration<URL> resources = cld.getResources(pckgname.replace('.', '/'));

            while (resources.hasMoreElements())
            {
                URL res = resources.nextElement();

                if (res.getProtocol().equalsIgnoreCase("jar"))
                {
                    JarURLConnection conn = (JarURLConnection) res.openConnection();
                    JarFile jar = conn.getJarFile();

                    for (JarEntry e : Collections.list(jar.entries()))
                    {
                        if (e.getName().startsWith(pckgname.replace('.', '/')) &&
                                e.getName().endsWith(".class") && !e.getName().contains("$"))
                        {
                            String className = e.getName().replace("/", ".").substring(0,
                                                                                       e.getName()
                                                                                       .length() -
                                                                                       6);
                            Class<?> cTemp = null;

                            try
                            {
                                cTemp = Class.forName(className);
                            }
                            catch (Throwable t)
                            {
                                // Ignore it.
                            }

                            classes.add(cTemp);
                        }
                    }
                }
                else
                {
                    directories.add(new File(URLDecoder.decode(res.getPath(), "UTF-8")));
                }
            }
        }
        catch (NullPointerException x)
        {
            throw new ClassNotFoundException(pckgname + " does not appear to be " +
                                             "a valid package (Null pointer exception)");
        }
        catch (UnsupportedEncodingException encex)
        {
            throw new ClassNotFoundException(pckgname + " does not appear to be " +
                                             "a valid package (Unsupported encoding)");
        }
        catch (IOException ioex)
        {
            throw new ClassNotFoundException("IOException was thrown when trying " +
                                             "to get all resources for " + pckgname);
        }

        // For every directory identified capture all the .class files
        for (File directory : directories)
        {
            if (directory.exists())
            {
                // Get the list of the files contained in the package
                String[] files = directory.list();

                for (String file : files)
                {
                    // we are only interested in .class files
                    if (file.endsWith(".class"))
                    {
                        // removes the .class extension
                    	try
                    	{
                        classes.add(Class.forName(pckgname + '.' +
                                                  file.substring(0, file.length() - 6)));
                    	}
                    	catch(Throwable t)
                    	{
                    		//Ignore the exception
                    	}
                    }
                }
            }
            else
            {
                throw new ClassNotFoundException(pckgname + " (" + directory.getPath() +
                                                 ") does not appear to be a valid package");
            }
        }
        return classes;
    }

    /**
     * Main method.
     *
     * @param  saArguments  The commandline arguments.
     */
    public static void main(String[] saArguments)
    {
        try
        {
            BasicConfigurator.configure();
            Logger.getRootLogger().setLevel(Level.DEBUG);

            Log4JAppenderWrapper law = new Log4JAppenderWrapper();

            ArrayList<AppenderMetadata> alAppender = law.getAppenderMetadata();

            for (AppenderMetadata am : alAppender)
            {
                System.out.println(am.toString());
            }

            ArrayList<LayoutMetadata> alLayout = law.getLayoutMetadata();

            for (LayoutMetadata am : alLayout)
            {
                System.out.println(am.toString());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * This method returns a list with all known appenders.
     *
     * @return  A list with all known appenders.
     */
    public ArrayList<AppenderMetadata> getAppenderMetadata()
    {
        return new ArrayList<AppenderMetadata>(m_lhmAppenders.values());
    }

    /**
     * This method returns all classes that implement the given interface.
     *
     * @param   thePackage    The package to check.
     * @param   theInterface  The interface to check.
     *
     * @return  The list of classes implementing the interface.
     *
     * @throws  ClassNotFoundException  DOCUMENTME
     */
    public List<Class<?>> getClassessOfInterface(String thePackage, Class<?> theInterface)
                                          throws ClassNotFoundException
    {
        List<Class<?>> classList = new ArrayList<Class<?>>();

        for (Class<?> discovered : getClassesForPackage(thePackage))
        {
            Class<?> aClass = discovered;

            if ((theInterface != null) && (aClass != null) && theInterface.isAssignableFrom(aClass))
            {
                LOG.debug("Class implements required interface: " + aClass.getName());
                classList.add(aClass);
            }
        }
        return classList;
    }

    /**
     * This method returns a list with all known layouts.
     *
     * @return  A list with all known layouts.
     */
    public ArrayList<LayoutMetadata> getLayoutMetadata()
    {
        return new ArrayList<LayoutMetadata>(m_lhmLayouts.values());
    }

    /**
     * This method adds the properties for this specific class.
     *
     * @param  cAppender           The class to examine.
     * @param  lhmFinalProperties  The list to add the properties to.
     */
    private void addPropertiesFromClass(Class<?> cAppender,
                                        LinkedHashMap<String, String> lhmFinalProperties)
    {
        LinkedHashMap<String, String> lhmTemp = new LinkedHashMap<String, String>();

        Method[] am = cAppender.getDeclaredMethods();

        for (Method m : am)
        {
            if (m.getName().startsWith("get") && (m.getReturnType() != null))
            {
                lhmTemp.put(m.getName().substring(3), m.getReturnType().getName());
            }
        }

        for (Method m : am)
        {
            String sName = m.getName().substring(3);

            if (m.getName().startsWith("set") && lhmTemp.containsKey(sName) &&
                    !lhmFinalProperties.containsKey(sName))
            {
                lhmFinalProperties.put(sName, lhmTemp.get(sName));
            }
        }
    }
}
