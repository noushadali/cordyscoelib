package com.cordys.coe.tools.migration.xforms.rules;

import com.eibus.xml.nom.Node;

/**
 * This class implements a message.
 *
 * @author  pgussow
 */
class Message
    implements IMessage
{
    /**
     * The context XML for this message.
     */
    private int m_iContextNode;
    /**
     * Holds the fixed context node.
     */
    private int m_iFixedContext = 0;
    /**
     * Holds the line number.
     */
    private int m_iLineNumber;
    /**
     * The message description.
     */
    private String m_sMessage;

    /**
     * Creates a new Message object.
     *
     * @param  sMessage      Holds the actual message.
     * @param  iContextNode  Holds the context XML.
     * @param  iLineNumber   Holds the line number.
     */
    Message(String sMessage, int iContextNode, int iLineNumber)
    {
        m_sMessage = sMessage;

        if (iContextNode != 0)
        {
            iContextNode = Node.duplicate(iContextNode);
        }
        m_iContextNode = iContextNode;
        m_iLineNumber = iLineNumber;
    }

    /**
     * This method cleans the XML references that are being held.
     *
     * @see  com.cordys.coe.tools.migration.xforms.rules.IMessage#cleanUp()
     */
    public void cleanUp()
    {
        if (m_iContextNode != 0)
        {
            Node.delete(m_iContextNode);
            m_iContextNode = 0;
        }

        if (m_iFixedContext != 0)
        {
            Node.delete(m_iFixedContext);
            m_iFixedContext = 0;
        }
    }

    /**
     * This method gets the context XML for this message.
     *
     * @return  The context XML for this message.
     *
     * @see     com.cordys.coe.tools.migration.xforms.rules.IMessage#getContextNode()
     */
    public int getContextNode()
    {
        return m_iContextNode;
    }

    /**
     * This method gets the message description.
     *
     * @return  The message description.
     *
     * @see     com.cordys.coe.tools.migration.xforms.rules.IMessage#getDescription()
     */
    public String getDescription()
    {
        return m_sMessage;
    }

    /**
     * This method gets the fixed context node.
     *
     * @return  The fixed context node.
     *
     * @see     com.cordys.coe.tools.migration.xforms.rules.IMessage#getFixedContext()
     */
    public int getFixedContext()
    {
        return m_iFixedContext;
    }

    /**
     * This method gets the line number for this tag.
     *
     * @return  The line number for this tag.
     *
     * @see     com.cordys.coe.tools.migration.xforms.rules.IMessage#getLineNumber()
     */
    public int getLineNumber()
    {
        return m_iLineNumber;
    }

    /**
     * This method prints the object to XML.
     *
     * @param  iParent  The parent node.
     *
     * @see    com.cordys.coe.tools.migration.xforms.rules.IMessage#printToXML(int)
     */
    public void printToXML(int iParent)
    {
        int iMessage = Node.createElement("message", iParent);

        Node.createTextElement("description", getDescription(), iMessage);

        if (m_iLineNumber > 0)
        {
            Node.createTextElement("linenumber", String.valueOf(getLineNumber()), iMessage);
        }

        if (m_iContextNode != 0)
        {
            int iTemp = Node.createElement("original", iMessage);
            Node.duplicateAndAppendToChildren(m_iContextNode, m_iContextNode, iTemp);
        }

        if (m_iFixedContext != 0)
        {
            int iTemp = Node.createElement("fixed", iMessage);
            Node.duplicateAndAppendToChildren(m_iFixedContext, m_iFixedContext, iTemp);
        }
    }

    /**
     * This method sets the fixed context node.
     *
     * @param  iFixedContext  The fixed context node.
     *
     * @see    com.cordys.coe.tools.migration.xforms.rules.IMessage#setFixedContext(int)
     */
    public void setFixedContext(int iFixedContext)
    {
        m_iFixedContext = Node.duplicate(iFixedContext);
    }
}
