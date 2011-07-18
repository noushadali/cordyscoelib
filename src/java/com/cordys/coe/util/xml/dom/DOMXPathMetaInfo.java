package com.cordys.coe.util.xml.dom;

import java.util.Map;

/**
 * This interface describes a prefix resolver which will allow namespaces and prefixes to be added
 * to the mapping.
 *
 * <p>Note: The implementation is JDK specific, since there is a difference between 1.4 and 1.5</p>
 *
 * @author  pgussow
 */
public interface DOMXPathMetaInfo extends PrefixResolver
{
    /**
     * This method adds a namespace/prefix mapping.
     *
     * @param  prefix     The prefix for the namespace.
     * @param  namespace  The actual namespace.
     */
    void addNamespacePrefix(String prefix, String namespace);

    /**
     * This method returns the current mappings.
     *
     * @return  The currently available mappings. 
     */
    Map<String, String> getMappings();
}
