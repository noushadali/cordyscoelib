/**
 * (c) 2007 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util.xml.dom;

import org.w3c.dom.Node;

/**
 * Copy of interface org.apache.xml.utils.PrefixResolver. Implemented in this package to remove
 * unnecessary dependencies.
 *
 * @author  mpoyhone
 */
public interface PrefixResolver
{
    /**
     * Returns the base identifier.
     *
     * @return  The base identifier.
     */
    String getBaseIdentifier();

    /**
     * Returns a namespace URI for the given prefix.
     *
     * @param   prefix  Prefix.
     *
     * @return  Namespace.
     */
    String getNamespaceForPrefix(String prefix);

    /**
     * Returns a namespace URI for the given prefix, based on the context node.
     *
     * @param   prefix   Prefix.
     * @param   context  Context node
     *
     * @return  Namespace.
     */
    String getNamespaceForPrefix(java.lang.String prefix, Node context);

    /**
     * True if this interface handles null prefixes.
     *
     * @return  <code>true</code> if this interface handles null prefixes.
     */
    boolean handlesNullPrefixes();
}
