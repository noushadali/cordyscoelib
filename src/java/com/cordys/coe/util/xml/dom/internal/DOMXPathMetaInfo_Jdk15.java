package com.cordys.coe.util.xml.dom.internal;

import java.util.LinkedHashMap;
import java.util.Map;

import org.w3c.dom.Node;

/**
 * Holds the Jdk15 implementation.
 *
 * @author  pgussow
 */
public class DOMXPathMetaInfo_Jdk15
    implements com.sun.org.apache.xml.internal.utils.PrefixResolver,
               com.cordys.coe.util.xml.dom.DOMXPathMetaInfo
{
    /**
     * Holds all the namespace-to-prefix bindings.
     */
    private LinkedHashMap<String, String> m_lhmNamespaceToPrefixBindings = new LinkedHashMap<String, String>();
    /**
     * Holds all the prefix-to-namespace bindings.
     */
    private LinkedHashMap<String, String> m_lhmPrefixToNamespaceBindings = new LinkedHashMap<String, String>();

    /**
     * @see  com.cordys.coe.util.xml.dom.DOMXPathMetaInfo#addNamespacePrefix(java.lang.String, java.lang.String)
     */
    @Override public void addNamespacePrefix(String prefix, String namespace)
    {
        m_lhmNamespaceToPrefixBindings.put(namespace, prefix);
        m_lhmPrefixToNamespaceBindings.put(prefix, namespace);
    }

    /**
     * @see  org.apache.xml.utils.PrefixResolver#getBaseIdentifier()
     */
    @Override public String getBaseIdentifier()
    {
        return "";
    }

    /**
     * @see  com.cordys.coe.util.xml.dom.DOMXPathMetaInfo#getMappings()
     */
    @Override public Map<String, String> getMappings()
    {
        return new LinkedHashMap<String, String>(m_lhmPrefixToNamespaceBindings);
    }

    /**
     * @see  org.apache.xml.utils.PrefixResolver#getNamespaceForPrefix(java.lang.String)
     */
    @Override public String getNamespaceForPrefix(String prefix)
    {
        return m_lhmPrefixToNamespaceBindings.get(prefix);
    }

    /**
     * @see  org.apache.xml.utils.PrefixResolver#getNamespaceForPrefix(java.lang.String, org.w3c.dom.Node)
     */
    @Override public String getNamespaceForPrefix(String prefix, Node context)
    {
        return getNamespaceForPrefix(prefix);
    }

    /**
     * @see  com.cordys.coe.util.xml.dom.PrefixResolver#handlesNullPrefixes()
     */
    @Override public boolean handlesNullPrefixes()
    {
        return false;
    }
}
