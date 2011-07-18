package com.cordys.coe.util.xml;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Find;
import com.eibus.xml.nom.Node;
import com.eibus.xml.nom.XMLException;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Helper methods for XML structure manipulation.
 *
 * @author  mpoyhone
 */
public class XMLHelpers
{
    /**
     * An utility method for creating XML elements in the given path.
     *
     * @param   saPath        Path of XML elements names.
     * @param   xParentNode   Root node under which the path is the be created.
     * @param   bUseExisting  If true, existing nodes are used (if found). Otherwise new nodes are
     *                        always created.
     *
     * @return  Last XML element of the created path.
     *
     * @throws  IllegalArgumentException  Thrown if the creation failed.
     */
    public static int createPath(String[] saPath, int xParentNode, boolean bUseExisting)
                          throws IllegalArgumentException
    {
        if (saPath == null)
        {
            throw new IllegalArgumentException("Destination path not set.");
        }

        int xCurrent = xParentNode;

        for (int i = 0; i < saPath.length; i++)
        {
            String sElem = saPath[i];
            int xNode = (bUseExisting ? Find.firstMatch(xCurrent, "<><" + sElem + ">") : 0);

            if (xNode == 0)
            {
                xNode = Node.createElement(sElem, xCurrent);
            }

            // Simulate recursion.
            xCurrent = xNode;
        }

        return xCurrent;
    }

    /**
     * Returns XML namespace prefix from the given XML node. The returned value will contain a colon
     * at the end (e.g. SOAP:) if the prefix was found or an it will be empty string if the prefix
     * was not set.
     *
     * @param   iNode          XML node.
     * @param   sNamespaceUri  Namespace URI of the prefix to be searched.
     *
     * @return  The namespace prefix.
     */
    public static String getNamespacePrefix(int iNode, String sNamespaceUri)
    {
        int iCount = Node.getNumAttributes(iNode);

        for (int i = 1; i <= iCount; i++)
        {
            String sPrefix = Node.getAttributePrefix(iNode, i);
            String sName = Node.getAttributeName(iNode, i);

            if (sName == null)
            {
                continue;
            }

            if (sPrefix == null)
            {
                if (!sName.equals("xmlns"))
                {
                    // Not a namespace declaration.
                    continue;
                }
            }
            else if (!sPrefix.equals("xmlns"))
            {
                // Not a namespace declaration.
                continue;
            }

            String sValue = Node.getAttribute(iNode, sName);

            if (sValue.equals(sNamespaceUri))
            {
                // Returns the "PREFIX:" or "" depending if we have a prefix or have
                // just the default namespace.
                return (sPrefix != null) ? (Node.getAttributeLocalName(iNode, i) + ":") : "";
            }
        }

        // The namespace URI was not found.
        return null;
    }

    /**
     * Returns the XML element name without namespace prefix (if any was specified).
     *
     * @param       iNode  Input XML node.
     *
     * @return      The XML element name.
     *
     * @deprecated  Use Node.getLocaname() instead.
     */
    public static String getNameWithoutPrefix(int iNode)
    {
        if (iNode == 0)
        {
            return "";
        }

        String sName = Node.getName(iNode);

        if (sName == null)
        {
            return "";
        }

        String sPrefix = Node.getPrefix(iNode);

        if ((sPrefix == null) || (sPrefix.length() == 0))
        {
            return sName;
        }

        return sName.substring(sPrefix.length() + 1);
    }

    /**
     * Parses the XPath expression in a string array containing the XPath elements. The expressiong
     * must be a simple /a/b/c for and it cannot contain any wild-cards or attributes.
     *
     * @param   sPath  Path to be parsed.
     *
     * @return  The XPath expression in a string array.
     *
     * @throws  IllegalArgumentException  Thrown if the parsing failed.
     */
    public static String[] parsePath(String sPath)
                              throws IllegalArgumentException
    {
        if (sPath.indexOf("//") >= 0)
        {
            throw new IllegalArgumentException("Destination path cannot have wild-cards.");
        }

        List<String> lElemList = new LinkedList<String>(Arrays.asList(sPath.split("/")));

        for (ListIterator<String> iIter = lElemList.listIterator(); iIter.hasNext();)
        {
            String sElem = iIter.next();

            if (sElem.equals("."))
            {
                throw new IllegalArgumentException("Destination path cannot have wild-cards.");
            }

            int iIndex = sElem.indexOf("@");

            if (iIndex >= 0)
            {
                sElem = ((iIndex > 0) ? sElem.substring(0, iIndex) : "");
            }

            if (sElem.length() == 0)
            {
                iIter.remove();
            }
        }

        return lElemList.toArray(new String[lElemList.size()]);
    }

    /**
     * Clones an XML node to the destination document. If the node document and the destination
     * document are different the node is first converted into binary format and then read into the
     * destination document. This method also copies the node's children.
     *
     * @param   dDestDoc  The document that will contain the returned node.
     * @param   iNode     The node to be cloned.
     *
     * @return  The cloned node.
     */
    public static int safeCloneNode(Document dDestDoc, int iNode)
    {
        if (Node.getDocument(iNode) == dDestDoc)
        {
            return Node.clone(iNode, true);
        }

        try
        {
            return dDestDoc.load(Node.write(iNode, false));
        }
        catch (XMLException e)
        {
            // This should not happen!
            return 0;
        }
    }

    /**
     * Sets the data element in an existing node. If the data element does not exists, it is
     * created.
     *
     * @param  iNode      The XML node in question.
     * @param  sNodeText  Value of the data element to be set.
     */
    public static void setNodeText(int iNode, String sNodeText)
    {
        // Try to get the destination element text node.
        int iTextNode = Node.getFirstDataNode(iNode);

        if (iTextNode == 0)
        {
            // Text node was not set, so create a new one.
            iTextNode = Node.getDocument(iNode).createText(sNodeText, iNode);
        }
        else
        {
            // Set the text node contents
            Node.setData(iTextNode, sNodeText);
        }
    }
}
