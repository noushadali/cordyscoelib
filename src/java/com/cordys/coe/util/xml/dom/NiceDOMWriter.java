package com.cordys.coe.util.xml.dom;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.w3c.dom.Node;

/**
 * This class replaces the CoENiceDomWriter. It will work in both Java 1.4 and 1.5+
 *
 * @author  pgussow
 */
public class NiceDOMWriter
{
    /**
     * Holds the actual method that needs to be called to do the formatting.
     */
    private static Method s_mImplMethod = null;

    static
    {
        // Figure out which class and method to use.
        try
        {
            Class<?> cNiceDOMWriterImpl = null;

            try
            {
                // This will throw an exception if the proper interface is not present.
                Class.forName("com.sun.org.apache.xml.internal.serialize.XMLSerializer");
                cNiceDOMWriterImpl = Class.forName("com.cordys.coe.util.xml.dom.internal.NiceDOMWriter_Jdk15");
            }
            catch (Throwable ignored)
            {
                // This will throw an exception if the proper interface is not present.
                Class.forName("org.apache.xml.serialize.XMLSerializer");
                cNiceDOMWriterImpl = Class.forName("com.cordys.coe.util.xml.dom.internal.NiceDOMWriter_Jdk14");
            }

            s_mImplMethod = cNiceDOMWriterImpl.getDeclaredMethod("write", Node.class,
                                                                 OutputStream.class, int.class,
                                                                 boolean.class, boolean.class,
                                                                 boolean.class);
        }
        catch (Exception e)
        {
            System.err.println("Unable to load the NiceDOMWriter class: " + e);
            e.printStackTrace(System.err);
        }
    }

    /**
     * This method quickly dumps the XML to a string without formatting it. It does not preserve
     * whitespace and doe not print the XML declaration.
     *
     * @param   nNode  The XML to dump.
     *
     * @return  The XML as a string.
     */
    public static String compactWrite(Node nNode)
    {
        return write(nNode, 0, false, false, false);
    }

    /**
     * This method will write the XML to a string. Formatting is set to true and the ident size is
     * 4.
     *
     * @param   nNode  The node that should be written to the string.This should be either an
     *                 Element or a Document.
     *
     * @return  The string containing the XML.
     *
     * @throws  NiceDOMWriterException  In case of any formatting problems.
     */
    public static String write(Node nNode)
                        throws NiceDOMWriterException
    {
        return write(nNode, true);
    }

    /**
     * This method will write the XML to a string. If bFormat is set to true an ident of 4 is used.
     *
     * @param   nNode    The node that should be written to the string. This should be either an
     *                   Element or a Document.
     * @param   bFormat  Whether or not to format the XML.
     *
     * @return  The string containing the XML.
     *
     * @throws  NiceDOMWriterException  In case of any formatting problems.
     */
    public static String write(Node nNode, boolean bFormat)
                        throws NiceDOMWriterException
    {
        return write(nNode, (bFormat ? 4 : 0), bFormat);
    }

    /**
     * This method will write the XML to a string using an ident of 4.
     *
     * @param   nNode   The node that should be written to the string. This should be either an
     *                  Element or a Document.
     * @param   iIdent  The ident size.
     *
     * @return  The string containing the XML.
     *
     * @throws  NiceDOMWriterException  In case of any formatting problems.
     */
    public static String write(Node nNode, int iIdent)
                        throws NiceDOMWriterException
    {
        return write(nNode, iIdent, true);
    }

    /**
     * This method will write the XML to a string using the given ident. Identing is set based on
     * bFormat. The whitespace is not preserved and the the XML declaration is printed.
     *
     * @param   nNode    The node that should be written to the string. This should be either an
     *                   Element or a Document.
     * @param   iIdent   The ident size.
     * @param   bFormat  Whether or not to format the XML.
     *
     * @return  The string containing the XML.
     *
     * @throws  NiceDOMWriterException  In case of any formatting problems.
     */
    public static String write(Node nNode, int iIdent, boolean bFormat)
                        throws NiceDOMWriterException
    {
        return write(nNode, iIdent, bFormat, true, false);
    }

    /**
     * This method will write the XML to a string using the given ident. Identing is set based on
     * bFormat. The whitespace is preserved based on bPreserveSpace and the the XML declaration is
     * only printed if bPrintXMLDeclaration is set to true.
     *
     * @param   nNode                 The node that should be written to the string. This should be
     *                                either an Element or a Document.
     * @param   iIdent                The ident size.
     * @param   bFormat               Whether or not to format the XML.
     * @param   bPrintXMLDeclaration  Whether or not to output the XML declaration on top of the
     *                                document.
     * @param   bPreserveSpace        Whether or not to preserve the whitespace.
     *
     * @return  The string containing the XML.
     *
     * @throws  NiceDOMWriterException  In case of any formatting problems.
     */
    public static String write(Node nNode, int iIdent, boolean bFormat,
                               boolean bPrintXMLDeclaration, boolean bPreserveSpace)
                        throws NiceDOMWriterException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        write(nNode, baos, iIdent, bFormat, bPrintXMLDeclaration, bPreserveSpace);

        try
        {
            return baos.toString("UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new NiceDOMWriterException("Unable to convert to UTF-8", e);
        }
    }

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
     * @throws  NiceDOMWriterException  In case of any formatting problems.
     */
    public static void write(Node nNode, OutputStream osStream, int iIdent, boolean bFormat,
                             boolean bPrintXMLDeclaration, boolean bPreserveSpace)
                      throws NiceDOMWriterException
    {
        try
        {
            s_mImplMethod.invoke(null, nNode, osStream, iIdent, bFormat, bPrintXMLDeclaration,
                                 bPreserveSpace);
        }
        catch (IllegalArgumentException e)
        {
            throw new NiceDOMWriterException("Illegal argument", e);
        }
        catch (IllegalAccessException e)
        {
            throw new NiceDOMWriterException("Error accessing the proper methods", e);
        }
        catch (InvocationTargetException e)
        {
            Throwable tActual = e;

            if (e.getCause() != null)
            {
                tActual = e.getCause();
            }
            throw new NiceDOMWriterException("Error formatting the XML", tActual);
        }
    }
}
