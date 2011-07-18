package com.cordys.coe.util.xml.dom.internal;

import com.sun.org.apache.xml.internal.serialize.Method;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import java.io.OutputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class writes the XML based on the JDK 1.4 Xerces implementation.
 *
 * @author  pgussow
 */
public class NiceDOMWriter_Jdk15
{
    /**
     * This method will write the XML to a stream using the given ident. Identing is set based on
     * bFormat.
     *
     * @param   nNode                 The node that should be written to the string. This should be
     *                                either an Element or a Document.
     * @param   osStream              The stream to write the XML to.
     * @param   iIdent                The ident size.
     * @param   bFormat               Whether or not to format the XML.
     * @param   bPrintXMLDeclaration  Whether or not to output the XML declaration on top of the
     *                                document.
     * @param   bPreserveSpace        Whether or not to preserve the whitespace.
     *
     * @throws  Exception  Thrown if the operation failed.
     */
    public static void write(Node nNode, OutputStream osStream, int iIdent, boolean bFormat,
                             boolean bPrintXMLDeclaration, boolean bPreserveSpace)
                      throws Exception
    {
        Document dDoc = nNode.getOwnerDocument();

        if (dDoc == null)
        {
            // Could be a document
            if (nNode instanceof Document)
            {
                dDoc = (Document) nNode;
            }
        }

        OutputFormat of = new OutputFormat(dDoc);
        of.setIndenting(bFormat);
        of.setIndent(iIdent);
        of.setMethod(Method.XML);
        of.setPreserveSpace(bPreserveSpace);
        of.setEncoding("UTF-8");
        of.setOmitXMLDeclaration(!bPrintXMLDeclaration);

        XMLSerializer xs = new XMLSerializer(osStream, of);
        xs.asDOMSerializer();

        if (nNode instanceof Element)
        {
            xs.serialize((Element) nNode);
        }
        else if (nNode instanceof Document)
        {
            xs.serialize((Document) nNode);
        }
        else
        {
            throw new Exception("Unsuppored class: " + nNode.getClass().getName());
        }
    }
}
