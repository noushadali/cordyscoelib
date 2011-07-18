package com.cordys.coe.util.i18n;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class will wrap a message set holding all localizeable property files.
 *
 * @author  pgussow
 */
public class CoEMessageSet
{
    /**
     * Holds the regex pattern this should be used to find out the country, language and region.
     */
    private static final Pattern P_LOCALES = Pattern.compile("([^_]+)(_([^_]+)(_([^_]+)){0,1}){0,1}(\\.properties|\\.class)");
    /**
     * Holds the default resource bundle to fall back on.
     */
    private ResourceBundle m_rbDefault;
    /**
     * Holds all available locales for the given message set.
     */
    private Set<Locale> m_sAvailableLocales = null;
    /**
     * Holds the package of the the message bundle.
     */
    private String m_sPackage;
    /**
     * Holds the real name for the message set.
     */
    private String m_sRealName = "";

    /**
     * This class wraps the actual property files containing the translations. The class passed on
     * is used as a base location. So let's say the class passed on is com.cordys.coe.TestClass and
     * the value of sName is "MyResources" then the file com.cordys.coe.MyResources.properties
     * should be available.
     *
     * @param  cLocation  The bas class location.
     * @param  sName      The name of the set.
     */
    public CoEMessageSet(Class<?> cLocation, String sName)
    {
        if ((cLocation == null) || (sName == null) || (sName.length() == 0))
        {
            throw new IllegalArgumentException("The location and name must be filled.");
        }

        m_sPackage = cLocation.getPackage().getName();

        // Build up the real name.
        m_sRealName = m_sPackage + "." + sName;

        // Load the default resource bundle using the default locale.
        m_rbDefault = ResourceBundle.getBundle(m_sRealName, new Locale("", "", ""));

        // Load the avialable locales for this message set.
        fillAvailableLocales();
    }

    /**
     * This class wraps the actual property files containing the translations. The class passed on
     * is used as a base location. So let's say the class passed on is com.cordys.coe.TestClass and
     * the value of sName is "MyResources" then the file com.cordys.coe.MyResources.properties
     * should be available.
     *
     * @param  sPackage  The base Java package in which the text files are.
     * @param  sName     The name of the set.
     */
    public CoEMessageSet(String sPackage, String sName)
    {
        if ((sPackage == null) || (sPackage.length() == 0) || (sName == null) ||
                (sName.length() == 0))
        {
            throw new IllegalArgumentException("The location and name must be filled.");
        }

        m_sPackage = sPackage;

        // Build up the real name.
        m_sRealName = m_sPackage + "." + sName;

        // Load the default resource bundle using the default locale.
        m_rbDefault = ResourceBundle.getBundle(m_sRealName, new Locale("", "", ""));

        // Load the avialable locales for this message set.
        fillAvailableLocales();
    }

    /**
     * This method returns true or false based on whether the given full name matches the method set
     * name.
     *
     * @param   sFullName        The full name of the file.
     * @param   sMessageSetName  The name of the message set.
     *
     * @return  true if the full name matches the message set name.
     */
    public static boolean isMatch(String sFullName, String sMessageSetName)
    {
        boolean bReturn = true;

        if (!sFullName.startsWith(sMessageSetName))
        {
            bReturn = false;
        }
        else if (!(sFullName.endsWith(".class") || sFullName.endsWith(".properties")))
        {
            bReturn = false;
        }

        return bReturn;
    }

    /**
     * This method returns which locales have been explicitly defined. The only way to examine this
     * is by using the current classloader and figure out where it can be found.
     *
     * @return  The list of all available locales for the given set.
     */
    public Locale[] getAvailableLocales()
    {
        return m_sAvailableLocales.toArray(new Locale[0]);
    }

    /**
     * This method gets the real name for this message set.
     *
     * @return  The real name for this message set.
     */
    public String getFullyQualifiedName()
    {
        return m_sRealName;
    }

    /**
     * This method returns the message context for the given set.
     *
     * @return  The fully qualified name of the message set.
     */
    public String getMessageContext()
    {
        return getFullyQualifiedName();
    }

    /**
     * This method returns the actual text for this message.
     *
     * @param   lLocale     The locale which should be used.
     * @param   sMessageID  The IS of the message.
     *
     * @return  The text for the message.
     */
    public String getMessageText(Locale lLocale, String sMessageID)
    {
        String sReturn = null;

        if (isAvailable(lLocale))
        {
            sReturn = ResourceBundle.getBundle(m_sRealName, lLocale).getString(sMessageID);
        }
        else
        {
            sReturn = m_rbDefault.getString(sMessageID);
        }

        return sReturn;
    }

    /**
     * This method will return all filenames which are found using the current classloader. Note:
     * The Java ResourceBundle allows also classes extending the ListResourceBundle class.
     *
     * @param   sPackageName  The name of the base package.
     * @param   sFullName     The full name of the message bundle.
     *
     * @return  The list of full strings which are avaliable on the current classpath
     *          (com.cordys.Messages_nl.properties).
     */
    private static List<String> findAvailableVersions(String sPackageName, String sFullName)
    {
        // This will hold a list of directories matching the packagename.
        // There may be more than one if a package is split over multiple jars/paths
        List<String> lReturn = new ArrayList<String>();
        List<File> alDirectories = new ArrayList<File>();

        try
        {
            ClassLoader cld = Thread.currentThread().getContextClassLoader();

            if (cld == null)
            {
                throw new IllegalStateException("Can't get class loader.");
            }

            // Ask for all resources for the path
            Enumeration<URL> resources = cld.getResources(sPackageName.replace('.', '/'));

            while (resources.hasMoreElements())
            {
                URL res = resources.nextElement();

                if (res.getProtocol().equalsIgnoreCase("jar"))
                {
                    JarURLConnection conn = (JarURLConnection) res.openConnection();
                    JarFile jar = conn.getJarFile();

                    for (JarEntry e : Collections.list(jar.entries()))
                    {
                        if (e.getName().startsWith(sFullName.replace('.', '/')))
                        {
                            // Build up the full name.
                            String sFilename = e.getName().replace('/', '.');

                            if (isMatch(sFilename, sFullName))
                            {
                                lReturn.add(sFilename);
                            }
                        }
                    }
                }
                else if (res.getProtocol().equalsIgnoreCase("file"))
                {
                    alDirectories.add(new File(URLDecoder.decode(res.getPath(), "UTF-8")));
                }
                else
                {
                    // Unknown protocol. So there is no way to figure out which locales are actually
                    // available.
                }
            }
        }
        catch (NullPointerException x)
        {
            throw new IllegalStateException(sPackageName + " does not appear to be " +
                                            "a valid package (Null pointer exception)");
        }
        catch (UnsupportedEncodingException encex)
        {
            throw new IllegalStateException(sPackageName + " does not appear to be " +
                                            "a valid package (Unsupported encoding)");
        }
        catch (IOException ioex)
        {
            throw new IllegalStateException("IOException was thrown when trying " +
                                            "to get all resources for " + sPackageName);
        }

        // For every directory identified capture all the .class files
        for (File directory : alDirectories)
        {
            if (directory.exists())
            {
                // Get the list of the files contained in the package
                String[] files = directory.list();

                for (String file : files)
                {
                    // we are only interested in .class files
                    String sFilename = sPackageName + "." + file;

                    if (sFilename.startsWith(sFullName))
                    {
                        // It's either a class file or a property file.
                        if (isMatch(sFilename, sFullName))
                        {
                            lReturn.add(sFilename);
                        }
                    }
                }
            }
            else
            {
                throw new IllegalStateException(sPackageName + " (" + directory.getPath() +
                                                ") does not appear to be a valid package");
            }
        }

        return lReturn;
    }

    /**
     * This method returns the list of available locales for the given message set.
     */
    private void fillAvailableLocales()
    {
        m_sAvailableLocales = new HashSet<Locale>();

        // Get all available language files.
        List<String> alFiles = findAvailableVersions(m_sPackage, m_sRealName);

        // Parse all the names to get the locale version.
        for (String sFilename : alFiles)
        {
            // The file ends with either .properties or .class.
            String sBase = sFilename.substring(m_sRealName.length() + 1);

            // Parse the language, country and region from the filename.
            Matcher mTemp = P_LOCALES.matcher(sBase);

            if (mTemp.find())
            {
                String sLanguage = mTemp.group(1);
                String sCountry = mTemp.group(3);
                String sVariant = mTemp.group(5);

                if (sCountry == null)
                {
                    sCountry = "";
                }

                if (sVariant == null)
                {
                    sVariant = "";
                }

                Locale lTemp = new Locale(sLanguage, sCountry, sVariant);
                m_sAvailableLocales.add(lTemp);
            }
        }
    }

    /**
     * This method returns whether or not the passed on locale is available.
     *
     * @param   lLocale  The locale to check.
     *
     * @return  true if the locale is available. Otherwise false.
     */
    private boolean isAvailable(Locale lLocale)
    {
        return m_sAvailableLocales.contains(lLocale);
    }
}
