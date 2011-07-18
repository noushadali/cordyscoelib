package com.cordys.coe.util.xml.dom;

import com.cordys.coe.util.xml.NamespaceDefinitions;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * This class is used to standardize namespaces and prefixes as they are used within the CoE lib.
 * This class is the default mapping class when the com.cordys.coe.util.xml.dom.XPathHelper class is
 * used.<br>
 * Proper use of this class:<br>
 * <code>private static String PREFIX = NamespaceConstants.registerPrefix("nspfx",
 * "http://namespaceuri");</code><br>
 * <code>String sXPath = "./" + PREFIX + ":tag/text()";</code>
 *
 * @author  pgussow
 */
public class NamespaceConstants
{
    /**
     * Holds all the prefix-to-namespace bindings;
     */
    private static LinkedHashMap<String, String> s_lhmPrefixToNamespaceBindings;
    /**
     * Holds all the namespace-to-prefix bindings;
     */
    private static LinkedHashMap<String, String> s_lhmNamespaceToPrefixBindings;
    /**
     * Holds the mutex object used for locking.
     */
    private static final Object s_oMutex = new Object();
    /**
     * Holds the actual prefix resolver.
     */
    private static PrefixResolver s_nprResolver;
    /**
     * Holds the prefix resolver class.
     */
    private static Class<?> s_cPrefixResolverImpl;
    /**
     * Holds the prefix resolver class.
     */
    private static Class<?> s_cDOMXPathMetaInfoImpl;

    static
    {
        s_lhmPrefixToNamespaceBindings = new LinkedHashMap<String, String>();
        s_lhmNamespaceToPrefixBindings = new LinkedHashMap<String, String>();

        HashMap<String, String> hmDefaultMappings = NamespaceDefinitions.getNamespaceMappings();

        for (String sPrefix : hmDefaultMappings.keySet())
        {
            String sNamespace = hmDefaultMappings.get(sPrefix);
            s_lhmPrefixToNamespaceBindings.put(sPrefix, sNamespace);
            s_lhmNamespaceToPrefixBindings.put(sNamespace, sPrefix);
        }

        try
        {
            s_cPrefixResolverImpl = null;
            s_cDOMXPathMetaInfoImpl = null;

            try
            {
                // This will throw an exception if the proper interface is not present.
                Class.forName("com.sun.org.apache.xml.internal.utils.PrefixResolver");
                s_cPrefixResolverImpl = Class.forName("com.cordys.coe.util.xml.dom.internal.NamespacePrefixResolver_Jdk15");
                s_cDOMXPathMetaInfoImpl = Class.forName("com.cordys.coe.util.xml.dom.internal.DOMXPathMetaInfo_Jdk15");
            }
            catch (Throwable ignored)
            {
                // This will throw an exception if the proper interface is not present.
                Class.forName("org.apache.xml.utils.PrefixResolver");
                s_cPrefixResolverImpl = Class.forName("com.cordys.coe.util.xml.dom.internal.NamespacePrefixResolver_Jdk14");
                s_cDOMXPathMetaInfoImpl = Class.forName("com.cordys.coe.util.xml.dom.internal.DOMXPathMetaInfo_Jdk14");
            }

            s_nprResolver = (PrefixResolver) s_cPrefixResolverImpl.newInstance();
        }
        catch (Exception e)
        {
            System.err.println("Unable to load the PrefixResolver class: " + e);
            e.printStackTrace(System.err);
        }
    }

    /**
     * This method adds a namespace and prefix to the static binding list. This needs to be done
     * only once.
     *
     * @param       sPrefix        The prefix to add.
     * @param       sNamespaceURI  The namespace for the prefix.
     *
     * @deprecated  This method should not be used, since it's not thread safe. Always use the
     *              registerPrefix method.
     */
    public static void addNamespaceBinding(String sPrefix, String sNamespaceURI)
    {
        s_lhmPrefixToNamespaceBindings.put(sPrefix, sNamespaceURI);
        s_lhmNamespaceToPrefixBindings.put(sNamespaceURI, sPrefix);
    }

    /**
     * This method creates a new prefix resolver with no mappings.
     *
     * @return  A new prefix resolver with no mappings.
     */
    public static DOMXPathMetaInfo createDOMXPathMetaInfo()
    {
        try
        {
            return (DOMXPathMetaInfo) s_cDOMXPathMetaInfoImpl.newInstance();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Unable to instantiate the resolver");
        }
    }

    /**
     * This method creates a new prefix resolver with no mappings.
     *
     * @return  A new prefix resolver with no mappings.
     */
    public static PrefixResolver createEmptyPrefixResolver()
    {
        try
        {
            return (PrefixResolver) s_cPrefixResolverImpl.newInstance();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Unable to instantiate the resolver");
        }
    }

    /**
     * This method returns the namespace for a given prefix.
     *
     * @param   sPrefix  The prefix.
     *
     * @return  The proper namespace.
     *
     * @see     com.cordys.coe.util.xml.dom#getNamespaceForPrefix(java.lang.String)
     */
    public static String getNamespaceForPrefix(String sPrefix)
    {
        return s_lhmPrefixToNamespaceBindings.get(sPrefix);
    }

    /**
     * This method gets the prefix for the given namespace. If for the namespace no prefix is
     * registered a new prefix is generated.
     *
     * @param   sNamespaceURI  The namespace to find the URI for.
     *
     * @return  The prefix for the given namespace.
     */
    public static String getPrefix(String sNamespaceURI)
    {
        String sReturn = null;

        if (s_lhmNamespaceToPrefixBindings.containsKey(sNamespaceURI))
        {
            sReturn = s_lhmNamespaceToPrefixBindings.get(sNamespaceURI);
        }

        return sReturn;
    }

    /**
     * This method returns an XPath meta info object with the proper namespace/prefix mappings.
     *
     * @return  The XPathMetaInfo to use.
     */
    public static PrefixResolver getPrefixResolver()
    {
        return s_nprResolver;
    }

    /**
     * This method returns whether or not the given namespace is already registered within the
     * constant class.
     *
     * @param   sNamespace  The namespace to check.
     *
     * @return  true if there is already a mapping for the given namespace. Otherwise false.
     */
    public static boolean isNamespaceRegistered(String sNamespace)
    {
        return (s_lhmNamespaceToPrefixBindings.containsKey(sNamespace));
    }

    /**
     * This method returns whether or not the given prefix is already registered within the constant
     * class.
     *
     * @param   sPrefix  The prefix to check.
     *
     * @return  true if there is already a mapping for the given prefix. Otherwise false.
     */
    public static boolean isPrefixRegistered(String sPrefix)
    {
        return (s_lhmPrefixToNamespaceBindings.containsKey(sPrefix));
    }

    /**
     * This method will register the namespace using the preferred prefix if it is not already
     * registered. If the namespace is already registered this method will return the rprefix it was
     * previously registered with.<br>
     * Note: You should NEVER use the preferred prefix in your XPaths because this class does not
     * guarantuee that the preferred prefix will also be the actual prefix in the
     *
     * @param   sPreferredPrefix  Holds the preferred prefix.
     * @param   sNamespaceURI     Holds the namespace.
     *
     * @return  The actual prefix to use.
     */
    public static String registerPrefix(String sPreferredPrefix, String sNamespaceURI)
    {
        String sReturn = sPreferredPrefix;

        if (!isNamespaceRegistered(sNamespaceURI))
        {
            // Now we need to lock, because we're modifying the internal structures.
            // We'll use the s_xmiMetaInfo object for locking
            synchronized (s_oMutex)
            {
                if (!isNamespaceRegistered(sNamespaceURI))
                {
                    // Determine the prefix.
                    if (isPrefixRegistered(sPreferredPrefix))
                    {
                        // We have to find a proper prefix since the preferred one is already
                        // being used.
                        int iCount = 0;
                        String sCurrent = "ns0";

                        while (isPrefixRegistered(sCurrent))
                        {
                            sCurrent = "ns" + ++iCount;
                        }

                        sReturn = sCurrent;
                        addNamespacePrefixBinding(sCurrent, sNamespaceURI);
                    }
                    else
                    {
                        // We can use the preferred one
                        addNamespacePrefixBinding(sPreferredPrefix, sNamespaceURI);
                        sReturn = sPreferredPrefix;
                    }
                }
                else
                {
                    sReturn = getPrefix(sNamespaceURI);
                }
            }
        }
        else
        {
            sReturn = getPrefix(sNamespaceURI);
        }

        return sReturn;
    }

    /**
     * This method adds a namespace and prefix to the static binding list. This needs to be done
     * only once.
     *
     * @param  sPrefix        The prefix to add.
     * @param  sNamespaceURI  The namespace for the prefix.
     */
    private static void addNamespacePrefixBinding(String sPrefix, String sNamespaceURI)
    {
        s_lhmPrefixToNamespaceBindings.put(sPrefix, sNamespaceURI);
        s_lhmNamespaceToPrefixBindings.put(sNamespaceURI, sPrefix);
    }
}
