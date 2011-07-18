/**
 * (c) 2007 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util.xml.dom.internal;

import com.cordys.coe.util.xml.dom.NamespaceConstants;

import org.w3c.dom.Node;

/**
 * This class implements the namespace prefix resolver for JDK 1.4.
 *
 * @author  mpoyhone
 */
public class NamespacePrefixResolver_Jdk14
    implements org.apache.xml.utils.PrefixResolver, com.cordys.coe.util.xml.dom.PrefixResolver
{
    /**
     * The base identifier from where relative URIs should be absolutized, or null if the base ID is
     * unknown.<br>
     * CAVEAT: Note that the base URI in an XML document may vary with where you are in the
     * document, if part of the doc's contents were brought in via an external entity reference or
     * if mechanisms such as xml:base have been used. Unless this PrefixResolver is bound to a
     * specific portion of the document, or has been kept up to date via some other mechanism, it
     * may not accurately reflect that context information.
     *
     * @return  DOCUMENTME
     *
     * @see     com.cordys.coe.util.xml.dom#getBaseIdentifier()
     */
    public String getBaseIdentifier()
    {
        return "";
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
        return NamespaceConstants.getNamespaceForPrefix(sPrefix);
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
        return NamespaceConstants.getNamespaceForPrefix(sPrefix);
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
}
