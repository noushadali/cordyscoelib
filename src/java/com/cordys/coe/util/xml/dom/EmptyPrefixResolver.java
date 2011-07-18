package com.cordys.coe.util.xml.dom;

import java.util.LinkedHashMap;

import org.w3c.dom.Node;

/**
 * This class can only be used in JDK 1.5 and up. It can be used to make a custom namespace-prefix
 * mapping.
 *
 * @author  pgussow
 */
public class EmptyPrefixResolver
    implements com.sun.org.apache.xml.internal.utils.PrefixResolver,
               com.cordys.coe.util.xml.dom.PrefixResolver
{
    /**
     * Holds all the prefix-to-namespace bindings;
     */
    private LinkedHashMap<String, String> m_lhmPrefixToNamespaceBindings;
    /**
     * Holds all the namespace-to-prefix bindings;
     */
    private LinkedHashMap<String, String> m_lhmNamespaceToPrefixBindings;

    /**
     * Holds the mutex object used for locking.
     */
    private static final Object s_oMutex = new Object();

    /**
     * Creates a new EmptyPrefixResolver object.
     */
    public EmptyPrefixResolver()
    {
        m_lhmPrefixToNamespaceBindings = new LinkedHashMap<String, String>();
        m_lhmNamespaceToPrefixBindings = new LinkedHashMap<String, String>();
    }

    /**
     * This method returns whether or not the given namespace is already registered within the
     * constant class.
     *
     * @param   sNamespace  The namespace to check.
     *
     * @return  true if there is already a mapping for the given namespace. Otherwise false.
     */
    public boolean isNamespaceRegistered(String sNamespace)
    {
        return (m_lhmNamespaceToPrefixBindings.containsKey(sNamespace));
    }

    /**
     * This method returns whether or not the given prefix is already registered within the constant
     * class.
     *
     * @param   sPrefix  The prefix to check.
     *
     * @return  true if there is already a mapping for the given prefix. Otherwise false.
     */
    public boolean isPrefixRegistered(String sPrefix)
    {
        return (m_lhmPrefixToNamespaceBindings.containsKey(sPrefix));
    }

    /**
     * This method adds a namespace and prefix to the static binding list. This needs to be done
     * only once.
     *
     * @param  sPrefix        The prefix to add.
     * @param  sNamespaceURI  The namespace for the prefix.
     */
    public void addNamespacePrefixBinding(String sPrefix, String sNamespaceURI)
    {
        m_lhmPrefixToNamespaceBindings.put(sPrefix, sNamespaceURI);
        m_lhmNamespaceToPrefixBindings.put(sNamespaceURI, sPrefix);
    }

    /**
     * The base identifier from where relative URIs should be absolutized, or null if the base ID is
     * unknown.<br>
     * CAVEAT: Note that the base URI in an XML document may vary with where you are in the
     * document, if part of the doc's contents were brought in via an external entity reference or
     * if mechanisms such as xml:base have been used. Unless this PrefixResolver is bound to a
     * specific portion of the document, or has been kept up to date via some other mechanism, it
     * may not accurately reflect that context information.
     *
     * @return  The base identifier.
     *
     * @see     com.cordys.coe.util.xml.dom#getBaseIdentifier()
     */
    public String getBaseIdentifier()
    {
        return "";
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
    public String getNamespaceForPrefix(String sPrefix)
    {
        return m_lhmPrefixToNamespaceBindings.get(sPrefix);
    }

    /**
     * Given a namespace, get the corresponding prefix, based on the context node.
     *
     * @param   sPrefix   The prefix.
     * @param   nContext  The context node.
     *
     * @return  The proper prefix.
     *
     * @see     com.cordys.coe.util.xml.dom#getNamespaceForPrefix(java.lang.String,
     *          org.w3c.dom.Node)
     */
    public String getNamespaceForPrefix(String sPrefix, Node nContext)
    {
        return m_lhmPrefixToNamespaceBindings.get(sPrefix);
    }

    /**
     * This method gets the prefix for the given namespace. If for the namespace no prefix is
     * registered a new prefix is generated.
     *
     * @param   sNamespaceURI  The namespace to find the URI for.
     *
     * @return  The prefix for the given namespace.
     */
    public String getPrefix(String sNamespaceURI)
    {
        String sReturn = null;

        if (m_lhmNamespaceToPrefixBindings.containsKey(sNamespaceURI))
        {
            sReturn = m_lhmNamespaceToPrefixBindings.get(sNamespaceURI);
        }

        return sReturn;
    }

    /**
     * This method gets the prefix for the namespace.
     *
     * @param   sNamespaceURI  The namespace for the prefix.
     *
     * @return  The prefix for the namespace.
     */
    public String getPrefixForNamespace(String sNamespaceURI)
    {
        return m_lhmNamespaceToPrefixBindings.get(sNamespaceURI);
    }

    /**
     * This method returns whether or not this resolver can handle null prefixes.
     *
     * @return  DOCUMENTME
     *
     * @see     com.cordys.coe.util.xml.dom#handlesNullPrefixes()
     */
    public boolean handlesNullPrefixes()
    {
        return false;
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
    public String registerPrefix(String sPreferredPrefix, String sNamespaceURI)
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
}
