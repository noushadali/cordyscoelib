package com.cordys.coe.util.xml;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Node;

import org.xml.sax.SAXParseException;

/**
 * Inner class to wrap around the parse errors.
 */
public class ParseError
{
    /**
     * Holds the column of the error.
     */
    private int iColumnNumber;
    /**
     * Holds the linenumber of the error.
     */
    private int iLineNumber;
    /**
     * Holds the detailed error message.
     */
    private String sMessage;

    /**
     * Constructor.
     *
     * @param  speException  The source exception.
     */
    public ParseError(SAXParseException speException)
    {
        iLineNumber = speException.getLineNumber();
        iColumnNumber = speException.getColumnNumber();
        sMessage = speException.getMessage();
    }

    /**
     * This method gets the column number of the error.
     *
     * @return  The column number of the error.
     */
    public int getcolumnNumber()
    {
        return iColumnNumber;
    }

    /**
     * This method gets the details error message.
     *
     * @return  The details error message.
     */
    public String getErrorMessage()
    {
        return sMessage;
    }

    /**
     * This method gets the linenumber for the error.
     *
     * @return  The linenumber for the error.
     */
    public int getLineNumber()
    {
        return iLineNumber;
    }

    /**
     * This method converts the class to a XML structure:
     *
     * <pre>
       <error>
         <line></line>
         <column></column>
         <message></message>
       </error>
     * </pre>
     *
     * @param  iErrorsNode  The parent XML node.
     */
    public void toXML(int iErrorsNode)
    {
        Document dDoc = Node.getDocument(iErrorsNode);
        int iError = dDoc.createElement("error", iErrorsNode);
        dDoc.createTextElement("line", String.valueOf(iLineNumber), iError);
        dDoc.createTextElement("column", String.valueOf(iColumnNumber), iError);
        dDoc.createTextElement("message", sMessage, iError);
    }
}
