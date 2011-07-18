package com.cordys.coe.util.xml.dom;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Class that formats an XML DOM Node into a string.
 *
 * @deprecated  This class is not 100% correct. You should use the NiceDOMWriter.write(...) method
 *              for a formatter.
 */
public class CoENiceDOMWriter
{
    /**
     * DOCUMENTME.
     */
    private static final String COMMENT_START = "<!--";
    /**
     * DOCUMENTME.
     */
    private static final String COMMENT_END = "-->";
    /**
     * DOCUMENTME.
     */
    private static final String HEADER = "<?xml version=\"1.0\"?>";
    /**
     * DOCUMENTME.
     */
    private static final char WHITESPACE = ' ';
    /**
     * DOCUMENTME.
     */
    private static final char EQUALS = '=';
    /**
     * DOCUMENTME.
     */
    private static final char QUOTE = '"';
    /**
     * DOCUMENTME.
     */
    private static final char START_TAG = '<';
    /**
     * DOCUMENTME.
     */
    private static final char END_TAG = '>';
    /**
     * DOCUMENTME.
     */
    private static final char SLASH = '/';
    /**
     * DOCUMENTME.
     */
    private static final char[] AMP = { '&', 'a', 'm', 'p', ';' };
    /**
     * DOCUMENTME.
     */
    private static final char[] LT = { '&', 'l', 't', ';' };
    /**
     * DOCUMENTME.
     */
    private static final char[] QUOTE_ENTITY = { '&', 'q', 'u', 'o', 't', ';' };
    /**
     * DOCUMENTME.
     */
    private static final char[] APOS_ENTITY = { '&', 'a', 'p', 'o', 's', ';' };
    /**
     * DOCUMENTME.
     */
    private static final String CDATA_START_TAG = "<![CDATA[";
    /**
     * DOCUMENTME.
     */
    private static final String CDATA_END_TAG = "]]>";
    /**
     * DOCUMENTME.
     */
    private String encoding = "UTF-8";
    /**
     * DOCUMENTME.
     */
    private int initialIndent = 4;
    /**
     * DOCUMENTME.
     */
    private boolean initialized = false;
    /**
     * DOCUMENTME.
     */
    private int initialLevel = 0;
    /**
     * DOCUMENTME.
     */
    private boolean isCDATA = false;
    /**
     * DOCUMENTME.
     */
    private boolean needsNewLine = true;
    /**
     * DOCUMENTME.
     */
    private OutputStream output;
    /**
     * DOCUMENTME.
     */
    private Vector<String> prefixes = new Vector<String>(10);
    /**
     * DOCUMENTME.
     */
    private Node root;
    /**
     * DOCUMENTME.
     */
    private BufferedWriter writer;

    /**
     * Creates a new NiceDOMWriter object.
     */
    public CoENiceDOMWriter()
    {
    }

    /**
     * Writes the specified node into XML Format.
     *
     * @param  node  Node that should be written as XML. default indentation is taken as 4
     */
    public CoENiceDOMWriter(Node node)
    {
        this(node, new ByteArrayOutputStream(), 0, 4);
    }

    /**
     * Creates a new NiceDOMWriter object.
     *
     * @param  node    DOCUMENTME
     * @param  level   DOCUMENTME
     * @param  indent  DOCUMENTME
     */
    public CoENiceDOMWriter(Node node, int level, int indent)
    {
        this(node, new ByteArrayOutputStream(), level, indent);
    }

    /**
     * Creates a new NiceDOMWriter object.
     *
     * @param  node    DOCUMENTME
     * @param  target  DOCUMENTME
     * @param  level   DOCUMENTME
     * @param  indent  DOCUMENTME
     */
    public CoENiceDOMWriter(Node node, OutputStream target, int level, int indent)
    {
        this.root = node;
        this.output = target;
        this.initialLevel = level;
        this.initialIndent = indent;
    }

    /**
     * DOCUMENTME.
     */
    public void flush()
    {
        try
        {
            if (!initialized)
            {
                initialize();
            }
        }
        catch (IOException ioe)
        {
        }
    }

    /**
     * DOCUMENTME.
     *
     * @return  DOCUMENTME
     */
    public byte[] getBytes()
    {
        if (output instanceof ByteArrayOutputStream)
        {
            try
            {
                if (!initialized)
                {
                    initialize();
                }
                return ((ByteArrayOutputStream) output).toByteArray();
            }
            catch (IOException ioe)
            {
                return super.toString().getBytes();
            }
        }
        return super.toString().getBytes();
    }

    /**
     * DOCUMENTME.
     *
     * @return  DOCUMENTME
     */
    @Override public String toString()
    {
        if (output instanceof ByteArrayOutputStream)
        {
            try
            {
                if (!initialized)
                {
                    initialize();
                }
                return ((ByteArrayOutputStream) output).toString();
            }
            catch (IOException ioe)
            {
                return super.toString();
            }
        }
        return super.toString();
    }

    /**
     * Writes the specified node into XMl Format.
     *
     * @param   node    Node that should be written into XMLFormat
     * @param   indent  int that specifies the indent length between parentNode and childNode
     *
     * @return  DOCUMENTME
     */
    public String writeNode(Node node, int indent)
    {
        try
        {
            this.initialIndent = indent;
            this.root = node;
            this.output = new ByteArrayOutputStream();
            initialize();
            return toString();
        }
        catch (IOException ioe)
        {
            return "Could not serialize node";
        }
    }

    /**
     * DOCUMENTME.
     *
     * @param   level   DOCUMENTME
     * @param   indent  DOCUMENTME
     *
     * @throws  IOException  DOCUMENTME
     */
    void indent(int level, int indent)
         throws IOException
    {
        for (int i = 0; i < level; i++)
        {
            for (int j = 0; j < indent; j++)
            {
                writer.write(' ');
            }
        }
    }

    /**
     * DOCUMENTME.
     *
     * @param   string  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    boolean isSpace(String string)
    {
        if (string == null)
        {
            return true;
        }

        int length = string.length();

        for (int i = 0; i < length; i++)
        {
            if (!Character.isWhitespace(string.charAt(i)))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * DOCUMENTME.
     *
     * @throws  IOException  DOCUMENTME
     */
    private void initialize()
                     throws IOException
    {
        writer = new BufferedWriter(new OutputStreamWriter(output, encoding));
        write(root, initialLevel, initialIndent);
        writer.flush();
        initialized = true;
    }

    /**
     * DOCUMENTME.
     *
     * @throws  IOException  DOCUMENTME
     */
    private void newLine()
                  throws IOException
    {
        writer.write(10);
    }

    /**
     * DOCUMENTME.
     *
     * @param   node    DOCUMENTME
     * @param   level   DOCUMENTME
     * @param   indent  DOCUMENTME
     *
     * @throws  IOException  DOCUMENTME
     */
    private void write(Node node, int level, int indent)
                throws IOException
    {
        if (node == null)
        {
            return;
        }

        switch (node.getNodeType())
        {
            case Node.DOCUMENT_NODE:
            {
                writer.write(HEADER);

                NamedNodeMap attributes = node.getAttributes();

                if (attributes != null)
                {
                    int numAttribs = attributes.getLength();

                    for (int i = 0; i < numAttribs; i++)
                    {
                        write(attributes.item(i), level + 1, indent);
                    }
                }

                Node child = node.getFirstChild();

                while (child != null)
                {
                    write(child, level, indent);
                    child = child.getNextSibling();
                }

                if (needsNewLine)
                {
                    newLine();
                    indent(level, indent);
                }
                break;
            }

            case Node.ELEMENT_NODE:
            {
                newLine();
                indent(level, indent);
                writer.write(START_TAG);

                String name = ((Element) node).getTagName();

                if (name != null)
                {
                    writer.write(name);
                }

                String prefix = ((Element) node).getPrefix();

                if (prefix != null)
                {
                    if (!prefixes.contains(prefix))
                    {
                        prefixes.addElement(prefix);

                        String namespaceURI = ((Element) node).getNamespaceURI();
                        writer.write(" xmlns:" + prefix + "=\"" + namespaceURI + "\"");
                    }
                }
                else
                {
                    String namespaceURI = ((Element) node).getNamespaceURI();

                    // Ensure we will not encounter NullPointers
                    if (namespaceURI == null)
                    {
                        namespaceURI = "";
                    }

                    Node parentNode = node.getParentNode();

                    if (parentNode == null)
                    {
                        if ((namespaceURI != null) && (namespaceURI.length() > 0))
                        {
                            writer.write(" xmlns=\"" + namespaceURI + "\"");
                        }
                    }
                    else
                    {
                        String parentNamespaceURI = parentNode.getNamespaceURI();

                        if (!namespaceURI.equals(parentNamespaceURI))
                        {
                            if ((namespaceURI != null) && (namespaceURI.length() > 0))
                            {
                                writer.write(" xmlns=\"" + namespaceURI + "\"");
                            }
                        }
                    }
                }

                NamedNodeMap attributes = node.getAttributes();

                if (attributes != null)
                {
                    int numAttribs = attributes.getLength();

                    for (int i = 0; i < numAttribs; i++)
                    {
                        if (!attributes.item(i).toString().startsWith("xmlns"))
                        {
                            write(attributes.item(i), level + 1, indent);
                        }
                    }
                }

                Node firstChild = node.getFirstChild();

                if ((firstChild == null) ||
                        ((firstChild.getNodeType() == Node.TEXT_NODE) &&
                             ((firstChild.getNodeValue() == null) ||
                                  (firstChild.getNodeValue().length() == 0))))
                {
                    writer.write(SLASH);
                    writer.write(END_TAG);
                }
                else
                {
                    writer.write(END_TAG);

                    while (firstChild != null)
                    {
                        write(firstChild, level + 1, indent);
                        firstChild = firstChild.getNextSibling();
                    }

                    if (needsNewLine)
                    {
                        newLine();
                        indent(level, indent);
                    }
                    writer.write(START_TAG);
                    writer.write(SLASH);
                    writer.write(name);
                    writer.write(END_TAG);
                    needsNewLine = true;
                }

                break;
            }

            case Node.TEXT_NODE:
            {
                if (isCDATA)
                {
                    isCDATA = false;
                    return;
                }

                String data = node.getNodeValue();

                if (isSpace(data))
                {
                    needsNewLine = true;
                }
                else
                {
                    needsNewLine = false;
                    writer.write(data);
                }

                break;
            }

            case Node.ATTRIBUTE_NODE:
            {
                writer.write(WHITESPACE);
                writer.write(node.getNodeName());
                writer.write(EQUALS);
                writer.write(QUOTE);

                String data = node.getNodeValue();

                if (data != null)
                {
                    for (int i = 0; i < data.length(); i++)
                    {
                        char ch = data.charAt(i);

                        if (ch == '"')
                        {
                            writer.write(QUOTE_ENTITY);
                        }
                        else if (ch == '\'')
                        {
                            writer.write(APOS_ENTITY);
                        }
                        else if (ch == '<')
                        {
                            writer.write(LT);
                        }
                        else if (ch == '&')
                        {
                            writer.write(AMP);
                        }
                        else
                        {
                            writer.write(ch);
                        }
                    }
                }
                writer.write(QUOTE);
                break;
            }

            case Node.CDATA_SECTION_NODE:
            {
                writeCDATA(node);
                break;
            }

            case Node.COMMENT_NODE:
            {
                newLine();
                indent(level, indent);
                writer.write(COMMENT_START);
                writer.write(node.getNodeValue());
                writer.write(COMMENT_END);
                break;
            }

            case Node.DOCUMENT_FRAGMENT_NODE:
            {
                writeDocumentFragment(node);
                break;
            }

            case Node.DOCUMENT_TYPE_NODE:
            {
                writeDTD(node);
                break;
            }

            case Node.ENTITY_NODE:
            {
                writeEntity(node);
                break;
            }

            case Node.ENTITY_REFERENCE_NODE:
            {
                writeEntityReference(node);
                break;
            }

            case Node.NOTATION_NODE:
            {
                writeNotation(node);
                break;
            }

            case Node.PROCESSING_INSTRUCTION_NODE:
            {
                writePI(node);
                break;
            }
        }
    }

    /**
     * DOCUMENTME.
     *
     * @param   node  DOCUMENTME
     *
     * @throws  IOException  DOCUMENTME
     */
    private void writeCDATA(Node node)
                     throws IOException
    {
        isCDATA = true;

        String data = node.getNextSibling().getNodeValue();

        if (isSpace(data))
        {
            needsNewLine = true;
        }
        else
        {
            needsNewLine = false;
            writer.write(CDATA_START_TAG);
            writer.write(data);
            writer.write(CDATA_END_TAG);
        }
    }

    /**
     * DOCUMENTME.
     *
     * @param   node  DOCUMENTME
     *
     * @throws  IOException  DOCUMENTME
     */
    private void writeDocumentFragment(Node node)
                                throws IOException
    {
    }

    /**
     * DOCUMENTME.
     *
     * @param   node  DOCUMENTME
     *
     * @throws  IOException  DOCUMENTME
     */
    private void writeDTD(Node node)
                   throws IOException
    {
    }

    /**
     * DOCUMENTME.
     *
     * @param   node  DOCUMENTME
     *
     * @throws  IOException  DOCUMENTME
     */
    private void writeEntity(Node node)
                      throws IOException
    {
    }

    /**
     * DOCUMENTME.
     *
     * @param   node  DOCUMENTME
     *
     * @throws  IOException  DOCUMENTME
     */
    private void writeEntityReference(Node node)
                               throws IOException
    {
    }

    /**
     * DOCUMENTME.
     *
     * @param   node  DOCUMENTME
     *
     * @throws  IOException  DOCUMENTME
     */
    private void writeNotation(Node node)
                        throws IOException
    {
    }

    /**
     * DOCUMENTME.
     *
     * @param   node  DOCUMENTME
     *
     * @throws  IOException  DOCUMENTME
     */
    private void writePI(Node node)
                  throws IOException
    {
    }
}
