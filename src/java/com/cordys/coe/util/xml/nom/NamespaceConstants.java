package com.cordys.coe.util.xml.nom;

import com.cordys.coe.util.xml.NamespaceDefinitions;

import com.eibus.xml.xpath.XPathMetaInfo;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * This class is used to standardize namespaces and prefixes as they are used within the CoE lib.
 * This class is the default mapping class when the com.cordys.coe.util.xml.nom.XPathHelper class is
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
     * Holds the XPathMetaInfo with the proper namspace bindings.
     */
    private static XPathMetaInfo s_xmiMetaInfo = null;
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

        // Add all defined namespaces to the static XPathMetaInfo object.
        s_xmiMetaInfo = new XPathMetaInfo();

        for (String sPrefix : s_lhmPrefixToNamespaceBindings.keySet())
        {
            String sNamespace = s_lhmPrefixToNamespaceBindings.get(sPrefix);
            s_xmiMetaInfo.addNamespaceBinding(sPrefix, sNamespace);
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

        s_xmiMetaInfo.addNamespaceBinding(sPrefix, sNamespaceURI);
    }

    /**
     * This method gets the prefix for the given namespace.
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
    public static XPathMetaInfo getXPathMetaInfo()
    {
        return s_xmiMetaInfo;
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

        s_xmiMetaInfo.addNamespaceBinding(sPrefix, sNamespaceURI);
    }
}
