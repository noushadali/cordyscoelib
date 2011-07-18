package com.cordys.coe.tools.migration.xforms.rules;

/**
 * This class contains the message that occurred.
 *
 * @author  pgussow
 */
public interface IMessage
{
    /**
     * This method cleans the XML references that are being held.
     */
    void cleanUp();

    /**
     * This method gets the context XML for this message.
     *
     * @return  The context XML for this message.
     */
    int getContextNode();

    /**
     * This method gets the message description.
     *
     * @return  The message description.
     */
    String getDescription();

    /**
     * This method gets the fixed context node.
     *
     * @return  The fixed context node.
     */
    int getFixedContext();

    /**
     * This method gets the line number for this tag.
     *
     * @return  The line number for this tag.
     */
    int getLineNumber();

    /**
     * This method prints the object to XML.
     *
     * @param  iParent  The parent node.
     */
    void printToXML(int iParent);

    /**
     * This method sets the fixed context node.
     *
     * @param  iFixedContext  The fixed context node.
     */
    void setFixedContext(int iFixedContext);
}
