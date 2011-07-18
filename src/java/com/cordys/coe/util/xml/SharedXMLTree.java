package com.cordys.coe.util.xml;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Node;

/**
 * Allows a reference counted access to XML tree. When the all references to the XML tree are
 * released, the tree is deleted (if this object was set as an read only tree).
 *
 * @author  mpoyhone
 */
public class SharedXMLTree
{
    /**
     * Indicates if this tree is to be read-only, i.e. it will not be deleted when this object is
     * deleted.
     */
    private boolean bReadOnly = false;
    /**
     * The document that this tree belongs to. This reference is needed to keep the XML structure
     * alive. When the reference count reaches zero, the underlying XML tree is deleted.
     */
    private Document dDocument = null;
    /**
     * The tree reference count.
     */
    private int iReferenceCount = 0;
    /**
     * The tree root node that will be deleted when the reference count reaches zero.
     */
    private int iTreeRootXmlNode = 0;

    /**
     * Creates a new SharedXMLTree object.
     *
     * @param  iRootXmlNode  Tree root XML node.
     */
    public SharedXMLTree(int iRootXmlNode)
    {
        iTreeRootXmlNode = iRootXmlNode;
        dDocument = Node.getDocument(iTreeRootXmlNode);
    }

    /**
     * Creates a new SharedXMLTree object.
     *
     * @param  iRootXmlNode  Tree root XML node.
     * @param  bReadOnly     If true, this tree is set to be read-only.
     */
    public SharedXMLTree(int iRootXmlNode, boolean bReadOnly)
    {
        iTreeRootXmlNode = iRootXmlNode;
        dDocument = Node.getDocument(iTreeRootXmlNode);
        this.bReadOnly = bReadOnly;
    }

    /**
     * Increments the reference counter value.
     */
    public final synchronized void addRef()
    {
        if (iReferenceCount < 0)
        {
            throw new IllegalStateException("addRef: Reference count < 0");
        }

        iReferenceCount++;
    }

    /**
     * This method gets the document that is used internally.
     *
     * @return  The document that is used internally.
     */
    public Document getDocument()
    {
        return dDocument;
    }

    /**
     * Returns the reference counter value.
     *
     * @return  The reference counter value.
     */
    public final synchronized int getRefCount()
    {
        return iReferenceCount;
    }

    /**
     * Returns true if this object is set to read-only.
     *
     * @return  True if this object is set to read-only.
     */
    public final boolean isReadOnly()
    {
        return bReadOnly;
    }

    /**
     * Decrements the reference counter value.
     */
    public final synchronized void release()
    {
        if (iReferenceCount <= 0)
        {
            throw new IllegalStateException("release: Reference count <= 0");
        }

        iReferenceCount--;

        if ((iReferenceCount == 0) && !bReadOnly)
        {
            if (iTreeRootXmlNode != 0)
            {
                Node.delete(iTreeRootXmlNode);

                iTreeRootXmlNode = 0;
                dDocument = null;
            }
        }
    }

    /**
     * Sets this object's read only state.
     *
     * @param  bValue  The new read-only state.
     */
    public final void setReadOnly(boolean bValue)
    {
        bReadOnly = bValue;
    }
}
