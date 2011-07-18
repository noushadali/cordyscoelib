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
 * DOCUMENTME.
 *
 * @author      $author$
 * @deprecated  This class is not 100% correct. You should use the NiceDOMWriter.getInstance(...)
 *              method for a formatter.
 */
public class CoEDOMWriter
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
    private static final String CDATA_START = "<![CDATA[";
    /**
     * DOCUMENTME.
     */
    private static final String CDATA_END = "]]>";
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
    private static final char[] GT = { '&', 'g', 't', ';' };
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
    private String encoding = "UTF-8";
    /**
     * DOCUMENTME.
     */
    private boolean initialized = false;
    /**
     * When true prefixes are added to the nodes.
     */
    private boolean m_bHandlePrefixes = true;
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
     * Creates a new CoEDOMWriter object.
     */
    public CoEDOMWriter()
    {
    }

    /**
     * Creates a new CoEDOMWriter object.
     *
     * @param  node  DOCUMENTME
     */
    public CoEDOMWriter(Node node)
    {
        this(node, new ByteArrayOutputStream());
    }

    /**
     * Creates a new CoEDOMWriter object.
     *
     * @param  node    DOCUMENTME
     * @param  target  DOCUMENTME
     */
    public CoEDOMWriter(Node node, OutputStream target)
    {
        this.root = node;
        this.output = target;
    }

    /**
     * Ignore the prefixes in the output.
     */
    public void disablePrefixes()
    {
        m_bHandlePrefixes = false;
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
     * DOCUMENTME.
     *
     * @param   node  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    public String writeNode(Node node)
    {
        try
        {
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
     * @throws  IOException  DOCUMENTME
     */
    private void initialize()
                     throws IOException
    {
        writer = new BufferedWriter(new OutputStreamWriter(output, encoding));
        write(root);
        writer.flush();
        initialized = true;
    }

    /**
     * DOCUMENTME.
     *
     * @param   node  DOCUMENTME
     *
     * @throws  IOException  DOCUMENTME
     */
    private void write(Node node)
                throws IOException
    {
        if (node == null)
        {
            return;
        }

        switch (node.getNodeType())
        {
            case Node.ELEMENT_NODE:
            {
                writer.write(START_TAG);

                String name = ((Element) node).getTagName();

                if (!m_bHandlePrefixes)
                {
                    if (-1 != name.indexOf(':'))
                    {
                        name = name.substring(name.indexOf(':') + 1);
                    }
                }

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

                        if (m_bHandlePrefixes)
                        {
                            String namespaceURI = ((Element) node).getNamespaceURI();
                            writer.write(" xmlns:" + prefix + "=\"" + namespaceURI + "\"");
                        }
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
                        if (m_bHandlePrefixes)
                        {
                            if ((namespaceURI != null) && (namespaceURI.length() > 0))
                            {
                                writer.write(" xmlns=\"" + namespaceURI + "\"");
                            }
                        }
                    }
                    else
                    {
                        String parentNamespaceURI = parentNode.getNamespaceURI();

                        if (!namespaceURI.equals(parentNamespaceURI))
                        {
                            if (m_bHandlePrefixes)
                            {
                                if ((namespaceURI != null) && (namespaceURI.length() > 0))
                                {
                                    writer.write(" xmlns=\"" + namespaceURI + "\"");
                                }
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
                            write(attributes.item(i));
                        }
                    }
                }

                Node child = node.getFirstChild();

                if (child == null)
                {
                    writer.write(SLASH);
                    writer.write(END_TAG);
                }
                else
                {
                    writer.write(END_TAG);

                    while (child != null)
                    {
                        write(child);
                        child = child.getNextSibling();
                    }

                    writer.write(START_TAG);
                    writer.write(SLASH);
                    writer.write(name);
                    writer.write(END_TAG);
                }

                break;
            }

            case Node.TEXT_NODE:
            {
                String data = node.getNodeValue();

                if (data != null)
                {
                    int length = data.length();

                    for (int i = 0; i < length; i++)
                    {
                        char ch = data.charAt(i);

                        if (ch == '<')
                        {
                            writer.write(LT);
                        }
                        else if (ch == '&')
                        {
                            writer.write(AMP);
                        }
                        else if (ch == '>')
                        {
                            writer.write(GT);
                        }
                        else
                        {
                            writer.write(ch);
                        }
                    }
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

            case Node.DOCUMENT_NODE:
            {
                writer.write(HEADER);

                Node child = node.getFirstChild();

                while (child != null)
                {
                    write(child);
                    child = child.getNextSibling();
                }

                break;
            }

            case Node.CDATA_SECTION_NODE:
            {
                writeCDATA(node);

                break;
            }

            case Node.COMMENT_NODE:
            {
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
        writer.write(CDATA_START);
        writer.write(node.getNodeValue());
        writer.write(CDATA_END);
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
