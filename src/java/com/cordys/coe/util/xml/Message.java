package com.cordys.coe.util.xml;

import com.cordys.coe.exception.GeneralException;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Node;
import com.eibus.xml.nom.NodeType;
import com.eibus.xml.nom.XMLException;

import java.io.File;
import java.io.UnsupportedEncodingException;

import java.util.Iterator;

/**
 * A simple class to hold an XML structure and allow a safe view into it. When subtrees are selected
 * from this XML, the XML nodes are not cloned, but actually point to the same XML tree. This allows
 * an efficient way to iterate though a large XML structure with a simple interface to the XML data.
 * The underlying XML tree has a reference count and it is automatically deleted when the last
 * Message referencing it is deleted (or cleared). In order to avoid one messages to modify other
 * messages XML tree (if they are referencing the same tree), modification method (e.g. setValue)
 * create a clone of the sub-tree before modification.
 *
 * @author  mpoyhone
 */
public class Message
{
    /**
     * The child node of in this shared XML tree that this message points to. This node must exist
     * in the tree!
     */
    protected int iXmlNode;
    /**
     * The MessageContext this message belongs to, or null if this message is not added to any
     * context.
     */
    protected MessageContext mcContext = null;
    /**
     * Contains the actual shared XML tree and the reference count.
     */
    protected SharedXMLTree sxtXmlTree;

    /**
     * Creates a new Message object from the given XML node. This method creates a new shared XML
     * tree.
     *
     * @param  iData  The XML node to be used by this message.
     */
    public Message(int iData)
    {
        iXmlNode = Node.clone(iData, true);
        sxtXmlTree = new SharedXMLTree(iXmlNode);
        sxtXmlTree.addRef();
    }

    /**
     * Creates a new Message object that points to the same XML tree as the source message.
     *
     * @param  mSrc  The source message.
     */
    public Message(Message mSrc)
    {
        this.iXmlNode = mSrc.iXmlNode;

        sxtXmlTree = mSrc.sxtXmlTree;
        sxtXmlTree.addRef();
    }

    /**
     * Creates a new Message object from the given XML node. This method creates a new shared XML
     * tree.
     *
     * @param  iData        The XML node to be used by this message.
     * @param  bCreateCopy  If true, a clone of the node is is used. If false, the actual node will
     *                      be used and in this case the node must not be deleted outside this
     *                      message.
     */
    public Message(int iData, boolean bCreateCopy)
    {
        iXmlNode = (bCreateCopy ? Node.clone(iData, true) : iData);
        sxtXmlTree = new SharedXMLTree(iXmlNode);
        sxtXmlTree.addRef();
    }

    /**
     * Creates a new Message object from a shared XML tree object and a child node in that tree.
     *
     * @param  sxtDataTree  The shared XML tree to be used.
     * @param  iData        The child node in the XML tree that will be the root node if this
     *                      message.
     */
    public Message(SharedXMLTree sxtDataTree, int iData)
    {
        this.iXmlNode = iData;

        sxtXmlTree = sxtDataTree;
        sxtXmlTree.addRef();
    }

    /**
     * Creates a new Message object from the given XML string. This method creates a new shared XML
     * tree.
     *
     * @param   dDoc   The document used for parsing the XML data.
     * @param   sData  The XML data that will be used for this message.
     *
     * @throws  GeneralException  Thrown if XML parsing failed.
     */
    public Message(Document dDoc, String sData)
            throws GeneralException
    {
        try
        {
            iXmlNode = dDoc.parseString(sData);
            sxtXmlTree = new SharedXMLTree(iXmlNode);
            sxtXmlTree.addRef();
        }
        catch (UnsupportedEncodingException e)
        {
            throw new GeneralException(e, "Unable to parse the input XML");
        }
        catch (XMLException e)
        {
            throw new GeneralException(e, "Unable to parse the input XML");
        }
    }

    /**
     * Creates a new Message object from the given file contents. This method creates a new shared
     * XML tree.
     *
     * @param   dDoc   The document used for parsing the XML data.
     * @param   fFile  The XML file to be parsed.
     *
     * @throws  GeneralException  Thrown if XML parsing failed.
     */
    public Message(Document dDoc, File fFile)
            throws GeneralException
    {
        try
        {
            iXmlNode = dDoc.load(fFile.getAbsolutePath());
            sxtXmlTree = new SharedXMLTree(iXmlNode);
            sxtXmlTree.addRef();
        }
        catch (Exception e)
        {
            throw new GeneralException(e, "Unable to parse the input XML file");
        }
    }

    /**
     * Appends the given message as a child message.
     *
     * @param   sAppendPath  The XMLPath like path to the node under which the message is to be
     *                       appended.
     * @param   mMsg         The message to be appended.
     *
     * @throws  GeneralException  Throw if the operation failed.
     */
    public final void append(String sAppendPath, Message mMsg)
                      throws GeneralException
    {
        modificationCheck();

        XMLQuery xqPath = new XMLQuery(sAppendPath);
        int iDestNode = xqPath.findNode(iXmlNode);

        if (iDestNode == 0)
        {
            throw new GeneralException("Append path '" + sAppendPath + "' does not exist.");
        }

        if (xqPath.getQueryAttribute() != null)
        {
            throw new GeneralException("Append path cannot point to an attribute.");
        }

        int iSrcNode = mMsg.getXmlNode();

        Node.appendToChildren(XMLHelpers.safeCloneNode(Node.getDocument(iDestNode), iSrcNode),
                              iDestNode);
    }

    /**
     * Returns an iterator for all child nodes.
     *
     * @return  An iterator returning child message objects.
     */
    public final Iterator<Object> childIterator()
    {
        return childIterator(mcContext);
    }

    /**
     * Returns an iterator for all child nodes. This version adds the messages returned by the
     * iterator to the given message context.
     *
     * @param   mcContext  The context to which the messages are to be added.
     *
     * @return  An iterator returning child message objects.
     */
    public final Iterator<Object> childIterator(MessageContext mcContext)
    {
        int iCount = 0;
        int[] iaResults;
        int iNode;
        int iPtr;

        iCount = getNumChildren();
        iaResults = new int[iCount];
        iNode = Node.getFirstChild(iXmlNode);
        iPtr = 0;

        while ((iNode != 0) && (iPtr < iCount))
        {
            if (Node.getType(iNode) == NodeType.ELEMENT)
            {
                iaResults[iPtr++] = iNode;
            }

            iNode = Node.getNextSibling(iNode);
        }

        if (iPtr < iaResults.length)
        {
            throw new IllegalStateException("childIterator: Node count is less than array size. (" +
                                            iPtr + " < " + iaResults.length + ")");
        }

        return new NodeIterator(mcContext, this, iaResults);
    }

    /**
     * Clears the message message contents and releases the reference to the XML tree. If this was
     * the only one referencing the tree, the tree will be deleted.
     */
    public final void clear()
    {
        if (sxtXmlTree != null)
        {
            sxtXmlTree.release();
            sxtXmlTree = null;
        }

        iXmlNode = 0;
    }

    /**
     * Returns the message context set to this message or null if no context is set.
     *
     * @return  The message context set to this message or null if no context is set.
     */
    public final MessageContext getContext()
    {
        return mcContext;
    }

    /**
     * Returns the XML element name. Element's namespace prefix is not returned.
     *
     * @return  Element name.
     */
    public final String getName()
    {
        return getName(false);
    }

    /**
     * Returns the XML element name.
     *
     * @param   bReturnPrefix  If true, the namespace prefix is also returned in the name.
     *
     * @return  The XML element name.
     */
    public final String getName(boolean bReturnPrefix)
    {
        if (iXmlNode == 0)
        {
            return "";
        }

        String sName = Node.getName(iXmlNode);

        if (sName == null)
        {
            return "";
        }

        if (bReturnPrefix)
        {
            return sName;
        }

        String sPrefix = getNamespacePrefix();

        if (sPrefix == null)
        {
            return sName;
        }

        return sName.substring(sPrefix.length() + 1);
    }

    /**
     * Returns the namespace prefix for this XML element.
     *
     * @return  The namespace prefix for this XML element or null if no prefix is set.
     */
    public final String getNamespacePrefix()
    {
        if (iXmlNode == 0)
        {
            return "";
        }

        String sPrefix = Node.getPrefix(iXmlNode);

        if ((sPrefix == null) || (sPrefix.length() == 0))
        {
            return null;
        }

        return sPrefix;
    }

    /**
     * Returns the number of subelements directly under this element. Does not count data elements.
     *
     * @return  Number of subelements directly under this element.
     */
    public final int getNumChildren()
    {
        if (iXmlNode == 0)
        {
            return 0;
        }

        int iChild = Node.getFirstChild(iXmlNode);
        int iCount = 0;

        while (iChild != 0)
        {
            if (Node.getType(iChild) == NodeType.ELEMENT)
            {
                iCount++;
            }

            iChild = Node.getNextSibling(iChild);
        }

        return iCount;
    }

    /**
     * Returns this XML node parent node.
     *
     * @return  The parent message object, or null if no parent was set.
     */
    public final Message getParent()
    {
        return getParent(mcContext);
    }

    /**
     * Returns this XML node parent node. This version adds the returned message to the given
     * message context.
     *
     * @param   mcContext  The context to which the message is to be added.
     *
     * @return  The parent message object, or null if no parent was set.
     */
    public final Message getParent(MessageContext mcContext)
    {
        if (iXmlNode == 0)
        {
            return null;
        }

        int iResult = Node.getParent(iXmlNode);

        if (iResult == 0)
        {
            return null;
        }

        Message mResult = new Message(sxtXmlTree, iResult);

        if (mcContext != null)
        {
            mcContext.add(mResult);
        }

        return mResult;
    }

    /**
     * Returns the shared XML tree object used by this message.
     *
     * @return  The shared XML tree object used by this message.
     */
    public final SharedXMLTree getSharedXmlTree()
    {
        return sxtXmlTree;
    }

    /**
     * Returns an XML node text or attribute value.
     *
     * @param   sSrcPath  The XMLPath like path to the node/attribute to be returned.
     *
     * @return  The source XML node text or attribute value.
     */
    public final String getValue(String sSrcPath)
    {
        try
        {
            return getValue(sSrcPath, false);
        }
        catch (GeneralException e)
        {
            // This should never happen.
            return null;
        }
    }

    /**
     * Returns an XML node text or attribute value.
     *
     * @param   sSrcPath       The XMLPath like path to the node/attribute to be returned.
     * @param   sDefaultValue  The default value if the source node could not be found.
     *
     * @return  The source XML node text or attribute value.
     */
    public final String getValue(String sSrcPath, String sDefaultValue)
    {
        String sRes = getValue(sSrcPath);

        return (sRes != null) ? sRes : sDefaultValue;
    }

    /**
     * Returns an XML node text or attribute value.
     *
     * @param   sSrcPath       The XMLPath like path to the node/attribute to be returned.
     * @param   bExpectResult  If true, an exception is thrown if the value was not found.
     *
     * @return  The source XML node text or attribute value.
     *
     * @throws  GeneralException  Thrown if the value was not found and bExpectResult was set to
     *                            true.
     */
    public final String getValue(String sSrcPath, boolean bExpectResult)
                          throws GeneralException
    {
        XMLQuery xqPath = new XMLQuery(sSrcPath);
        int iSrcNode = xqPath.findNode(iXmlNode);
        String sResult = null;

        if (iSrcNode != 0)
        {
            if (xqPath.getQueryAttribute() == null)
            {
                sResult = Node.getData(iSrcNode);
            }
            else
            {
                sResult = Node.getAttribute(iSrcNode, xqPath.getQueryAttribute());
            }
        }

        if ((sResult == null) && bExpectResult)
        {
            throw new GeneralException("XML element '" + sSrcPath + "' does not exist.");
        }

        return sResult;
    }

    /**
     * Returns the XML root node pointed by this message. NOTE! This node or any other nodes in the
     * same tree must node be modified outside Message class. If this is needed, the method
     * modificationCheck(), must be called before calling this method.
     *
     * @return  The XML root node pointed by this message.
     */
    public final int getXmlNode()
    {
        return iXmlNode;
    }

    /**
     * Insers a message as a child tree under the node pointed by insertion path.
     *
     * @param   sInsertPath  The XMLPath like path to the node under which the message will be
     *                       inserted.
     * @param   iPosition    The child element number after which the message will be inserted. If
     *                       the value is -1 the message will be inserted as the first child and if
     *                       the value is greater or equals the number of children in the node, the
     *                       message will be added as the last child.
     * @param   mMsg         The message to be inserted.
     *
     * @throws  GeneralException  Thrown if the search path was invalid.
     */
    public final void insertAfter(String sInsertPath, int iPosition, Message mMsg)
                           throws GeneralException
    {
        modificationCheck();

        XMLQuery xqPath = new XMLQuery(sInsertPath);
        int iDestNode = xqPath.findNode(iXmlNode);

        if (iDestNode == 0)
        {
            throw new GeneralException("Insert path '" + sInsertPath + "' does not exist.");
        }

        if (xqPath.getQueryAttribute() != null)
        {
            throw new GeneralException("Append path cannot point to an attribute.");
        }

        int iSrcNode = mMsg.getXmlNode();
        int iClonedSrcNode;

        // Clone the sub-tree to be inserted.
        iClonedSrcNode = XMLHelpers.safeCloneNode(Node.getDocument(iDestNode), iSrcNode);

        if (Node.getNumChildren(iDestNode) == 0)
        {
            // This node doesn't have any children so the position parameter
            // value doesn't matter.
            Node.appendToChildren(iClonedSrcNode, iDestNode);
            return;
        }

        // Find the right child node.
        int iCurChild = Node.getFirstChild(iDestNode);
        int iCounter = 0;
        int iTmp;

        // Check we are inserting before the first child.
        if (iPosition < 0)
        {
            // Insert the source node before this child.
            Node.insert(iClonedSrcNode, iCurChild);
            return;
        }

        // Find the right child node.
        while ((iCounter < iPosition) && ((iTmp = Node.getNextSibling(iCurChild)) != 0))
        {
            iCurChild = iTmp;
            iCounter++;
        }

        // Insert the source node after this child.
        Node.add(iClonedSrcNode, iCurChild);
    }

    /**
     * Checks if the XML tree referenced by this message is shared by other tree and if so, creates
     * a new shared XML tree base on this message in order to avoid modications to the shared XMl
     * tree. NOTE: This method must be called before any modifications are done to the XML structure
     * outside this message class!
     */
    public final void modificationCheck()
    {
        if ((sxtXmlTree.getRefCount() > 1) || sxtXmlTree.isReadOnly())
        {
            int iNewXmlNode = Node.clone(iXmlNode, true);

            if (sxtXmlTree != null)
            {
                sxtXmlTree.release();
            }

            sxtXmlTree = new SharedXMLTree(iNewXmlNode);
            sxtXmlTree.addRef();
            iXmlNode = iNewXmlNode;
        }
    }

    /**
     * Selects the first subtree that matches the select path expression.
     *
     * @param   sSelectPath  The XMLPath like path to the node which will be returned.
     *
     * @return  The message object that contains the selected subtree, or null if no node was found.
     *
     * @throws  GeneralException  Thrown if the selection path was invalid.
     */
    public final Message select(String sSelectPath)
                         throws GeneralException
    {
        return select(mcContext, sSelectPath);
    }

    /**
     * Selects the first subtree that matches the select path expression. This version adds the
     * returned message to the given message context.
     *
     * @param   mcContext    The context to which the message is to be added.
     * @param   sSelectPath  The XMLPath like path to the node which will be returned.
     *
     * @return  The message object that contains the selected subtree, or null if no node was found.
     *
     * @throws  GeneralException  Thrown if the selection path was invalid.
     */
    public final Message select(MessageContext mcContext, String sSelectPath)
                         throws GeneralException
    {
        XMLQuery xqSelect = new XMLQuery(sSelectPath);

        if (xqSelect.getQueryAttribute() != null)
        {
            throw new GeneralException("Select path cannot point to an attribute.");
        }

        int iResult = xqSelect.findNode(iXmlNode);

        if (iResult == 0)
        {
            return null;
        }

        Message mResult = new Message(sxtXmlTree, iResult);

        if (mcContext != null)
        {
            mcContext.add(mResult);
        }

        return mResult;
    }

    /**
     * Selects all the subtrees that match the select path expression.
     *
     * @param   sSelectPath  The XMLPath like path to the nodes which will be returned.
     *
     * @return  An iterator returning message objects that contain the selected subtrees.
     *
     * @throws  GeneralException  Thrown if the selection path was invalid.
     */
    public final Iterator<Object> selectAll(String sSelectPath)
                                     throws GeneralException
    {
        return selectAll(mcContext, sSelectPath);
    }

    /**
     * Selects all the subtrees that match the select path expression. This version adds the
     * messages returned by the iterator to the given message context.
     *
     * @param   mcContext    The context to which the messages are to be added.
     * @param   sSelectPath  The XMLPath like path to the nodes which will be returned.
     *
     * @return  An iterator returning message objects that contain the selected subtrees.
     *
     * @throws  GeneralException  Thrown if the selection path was invalid.
     */
    public final Iterator<Object> selectAll(MessageContext mcContext, String sSelectPath)
                                     throws GeneralException
    {
        XMLQuery xqSelect = new XMLQuery(sSelectPath);

        if (xqSelect.getQueryAttribute() != null)
        {
            throw new GeneralException("Select path cannot point to an attribute.");
        }

        int[] iaResults = xqSelect.findAllNodes(iXmlNode);

        if (iaResults == null)
        {
            iaResults = new int[0];
        }

        return new NodeIterator(mcContext, this, iaResults);
    }

    /**
     * Sets the message context of this message. Note! This does not add the message to the context.
     *
     * @param  mcNewContext  The new MessageContext.
     */
    public final void setContext(MessageContext mcNewContext)
    {
        mcContext = mcNewContext;
    }

    /**
     * Sets an XML node text or attribute value.
     *
     * @param   sDestPath  The XMLPath like path to the node/attribute to be set.
     * @param   sValue     The value to be set.
     *
     * @throws  GeneralException  Thrown if the destination node could node be found.
     */
    public final void setValue(String sDestPath, String sValue)
                        throws GeneralException
    {
        modificationCheck();

        XMLQuery xqPath = new XMLQuery(sDestPath);
        int iDestNode = xqPath.findNode(iXmlNode);

        if (iDestNode == 0)
        {
            throw new GeneralException("Destination path '" + sDestPath + "' does not exist.");
        }

        if (xqPath.getQueryAttribute() == null)
        {
            XMLHelpers.setNodeText(iDestNode, sValue);
        }
        else
        {
            Node.setAttribute(iDestNode, xqPath.getQueryAttribute(), sValue);
        }
    }

    /**
     * Returns the XML tree contents as a string. This is done using Node.writeToString()
     *
     * @return  The XML tree contents as a string
     */
    @Override public final String toString()
    {
        return Node.writeToString(iXmlNode, true);
    }

    /**
     * Standard java finalizer that will be called when this object will be cleaned up. This
     * implementation calls the clear() method.
     */
    @Override protected void finalize()
    {
        // Make sure this finalizer doesn't throw any exceptions.
        try
        {
            clear();
        }
        catch (Throwable ignored)
        {
        }
    }

    /**
     * An iterator object that returns selection results.
     *
     * @author  mpoyhone
     */
    public class NodeIterator
        implements Iterator<Object>
    {
        /**
         * The nodes that will be returned by this iterator.
         */
        protected int[] iaNodes;
        /**
         * Current iterator position in the node array.
         */
        protected int iCurrentNodePtr;
        /**
         * Messages that are created by this iterator will be added to this context. If set to null,
         * no message context is used.
         */
        protected MessageContext mcContext;
        /**
         * The shared tree that the contains the nodes returned by this iterator.
         */
        protected SharedXMLTree sxtXmlTree;

        /**
         * Creates a new NodeIterator object.
         *
         * @param  mcContext  The context to which the messages are to be added.
         * @param  mMessage   The message that contains the iterated nodes.
         * @param  iaNodes    The nodes to be returned by this iterator.
         */
        public NodeIterator(MessageContext mcContext, Message mMessage, int[] iaNodes)
        {
            this.mcContext = mcContext;
            this.sxtXmlTree = mMessage.sxtXmlTree;
            this.sxtXmlTree.addRef();
            this.iaNodes = iaNodes;
            iCurrentNodePtr = 0;
        }

        /**
         * Standard Iterator method. Returns true if there are still elements to be returned by
         * next() method.
         *
         * @return  True, if this iterator still has more elements.
         */
        public boolean hasNext()
        {
            if (iaNodes == null)
            {
                return false;
            }

            return iCurrentNodePtr < iaNodes.length;
        }

        /**
         * Standard Iterator method. Returns the next message pointed by this iterator and moves
         * this iterator to point to the message after that.
         *
         * @return  The next message pointed by this iterator as a Message object.
         */
        public Object next()
        {
            // Check if we are at the end.
            if ((iaNodes == null) || (iCurrentNodePtr >= iaNodes.length))
            {
                return null;
            }

            // Create a new Message object to contain the current node.
            Message mReturn = new Message(sxtXmlTree, iaNodes[iCurrentNodePtr]);

            // Add the message to the message context, if it is set.
            if (mcContext != null)
            {
                mcContext.add(mReturn);
            }

            // Move to the next message.
            iCurrentNodePtr++;

            // Check if we have reached the end.
            if (iCurrentNodePtr >= iaNodes.length)
            {
                // Clear the references to this iterator.
                clear();
            }

            return mReturn;
        }

        /**
         * Standard Iterator method. This is not implemented.
         */
        public void remove()
        {
            throw new UnsupportedOperationException("remove method unimplemented.");
        }

        /**
         * Clears the iterator contents.
         */
        protected void clear()
        {
            if (sxtXmlTree != null)
            {
                sxtXmlTree.release();
                sxtXmlTree = null;
            }

            iaNodes = null;

            mcContext = null;
        }

        /**
         * Called when this object is being cleaned up.
         */
        @Override protected void finalize()
        {
            clear();
        }
    }
}
