package com.cordys.coe.util.xml.dom;

import com.cordys.coe.util.DateUtil;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.traversal.NodeIterator;

/**
 * This helper is written to be compatible with Java 1.4.2 AND Java 1.5.0.
 * 
 * @author pgussow
 */
public class XPathHelper
{
    /** Holds the method for executing the selectNodeIterator method. */
    private static Method s_mSelectNodeIterator;
    /** Holds the method for executing the selectSingleNode method. */
    private static Method s_mSelectSingleNode;
    /** Holds the method for executing the selectSingleNode method. */
    private static Method s_mSelectSingleNodeNamespace;
    /** Holds the method for executing the selectNodeList method. */
    private static Method s_mSelectNodeList;
    /** Holds the signature for three methods. */
    private static Class<?>[] caMethodSignature = { Node.class, String.class };
    /** Holds the signature for three methods. */
    private static Class<?>[] caMethodSignatureNamespace = { Node.class, String.class, Node.class };
    /** Holds the eval method. */
    private static Method s_mEval;
    /** Holds the constructor to create an OutputFormat object. */
    private static Constructor<?> s_cOutputFormat;
    /** Holds the method OutputFormat.setIdent(). */
    private static Method s_mOFSetIndent;
    /** Holds the method OutputFormat.setIdenting(). */
    private static Method s_mOFSetIndenting;
    /** Holds the constructor to create the XMLSerializer. */
    private static Constructor<?> s_cXMLSerializer;
    /** Holds the method serialize. */
    private static Method s_mXSSerialize;

    static
    {
        try
        {
            Class<?> cXPathApiClass;

            try
            {
                cXPathApiClass = Class.forName("com.sun.org.apache.xpath.internal.XPathAPI");
            }
            catch (Throwable ignored)
            {
                cXPathApiClass = Class.forName("org.apache.xpath.XPathAPI");
            }

            Class<?> cPrefixResolver = null;

            try
            {
                cPrefixResolver = Class.forName("com.sun.org.apache.xml.internal.utils.PrefixResolver");
            }
            catch (Throwable ignored)
            {
                cPrefixResolver = Class.forName("org.apache.xml.utils.PrefixResolver");
            }

            Class<?> cOutputFormat;

            try
            {
                cOutputFormat = Class.forName("com.sun.org.apache.xml.internal.serialize.OutputFormat");
            }
            catch (Throwable ignored)
            {
                cOutputFormat = Class.forName("org.apache.xml.serialize.OutputFormat");
            }

            Class<?> cXMLSerializer;

            try
            {
                cXMLSerializer = Class.forName("com.sun.org.apache.xml.internal.serialize.XMLSerializer");
            }
            catch (Throwable ignored)
            {
                cXMLSerializer = Class.forName("org.apache.xml.serialize.XMLSerializer");
            }

            s_cOutputFormat = cOutputFormat.getDeclaredConstructor(new Class[] { Document.class });
            s_cXMLSerializer = cXMLSerializer.getDeclaredConstructor(new Class[] { OutputStream.class, cOutputFormat });

            s_mOFSetIndenting = cOutputFormat.getMethod("setIndenting", new Class[] { boolean.class });
            s_mOFSetIndent = cOutputFormat.getMethod("setIndent", new Class[] { int.class });

            s_mXSSerialize = cXMLSerializer.getMethod("serialize", new Class[] { Element.class });

            s_mSelectNodeIterator = cXPathApiClass.getDeclaredMethod("selectNodeIterator", caMethodSignature);
            s_mSelectSingleNode = cXPathApiClass.getDeclaredMethod("selectSingleNode", caMethodSignature);
            s_mSelectNodeList = cXPathApiClass.getDeclaredMethod("selectNodeList", caMethodSignature);
            s_mSelectSingleNodeNamespace = cXPathApiClass.getDeclaredMethod("selectSingleNode", caMethodSignatureNamespace);

            s_mEval = cXPathApiClass.getDeclaredMethod("eval", new Class[] { Node.class, String.class, cPrefixResolver });
        }
        catch (Throwable e)
        {
            System.err.println("Unable to find the XPathAPI class");
            e.printStackTrace(System.err);
        }
    }

    /**
     * This method returns the first node that matches the given XPath expression.
     * 
     * @param eRoot The root node.
     * @param sPath The XPath to execute.
     * @return The first node that matches the XPath.
     */
    public static Node firstMatch(Element eRoot, String sPath)
    {
        try
        {
            Node nNode = (Node) s_mSelectSingleNode.invoke(null, new Object[] { eRoot, sPath });

            return nNode;
        }
        catch (Exception ignored)
        {
            return null;
        }
    }

    /**
     * This method returns the boolean value for the given XPath. This version return the boolean value for XPath expressions that
     * evaluate to a node, attribute or a boolean value (e.g. "boolean(.)"). This version returns only the first match.<br>
     * As a prefix resolver it uses the one from the NamespaceConstants class.
     * 
     * @param nContextNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @return The boolean value.
     * @throws TransformerException In case of any XPath exceptions.
     */
    public static boolean getBooleanValue(Node nContextNode, String sXPath) throws TransformerException
    {
        return getBooleanValue(nContextNode, sXPath, NamespaceConstants.getPrefixResolver(), false, false);
    }

    /**
     * This method returns the boolean value for the given XPath.
     * 
     * @param nContextNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param bMandatory Whether or not the field is mandatory.
     * @return The boolean value. If the value is not found and bMandatory is false the default value is false.
     * @throws TransformerException If the value cannot be found and bMandatory is true.
     */
    public static boolean getBooleanValue(Node nContextNode, String sXPath, boolean bMandatory) throws TransformerException
    {
        boolean bReturn = false;

        String sTemp = getStringValue(nContextNode, sXPath, bMandatory);

        if ((sTemp != null) && (sTemp.length() != 0))
        {
            bReturn = "true".equalsIgnoreCase(sTemp);
        }
        else if (bMandatory == true)
        {
            throw new TransformerException("Cannot find xpath " + sXPath + " in the definition.");
        }

        return bReturn;
    }

    /**
     * This method returns the boolean value for the given XPath. This version return the boolean value for XPath expressions that
     * evaluate to a node, attribute or a boolean value (e.g. "boolean(.)"). This version returns only the first match.
     * 
     * @param nContextNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @return The boolean value.
     * @throws TransformerException In case of any XPath exceptions.
     */
    public static boolean getBooleanValue(Node nContextNode, String sXPath, PrefixResolver xmiPathInfo)
            throws TransformerException
    {
        return getBooleanValue(nContextNode, sXPath, xmiPathInfo, false, false);
    }

    /**
     * This method returns the boolean value for the given XPath. This version return the boolean value for XPath expressions that
     * evaluate to a node, attribute or a boolean value (e.g. "boolean(.)").
     * 
     * @param nContextNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @param bDefault The default value to return if there was nothing found.
     * @return The boolean value.
     * @throws TransformerException In case of any XPath exceptions.
     */
    public static boolean getBooleanValue(Node nContextNode, String sXPath, PrefixResolver xmiPathInfo, boolean bDefault)
            throws TransformerException
    {
        return getBooleanValue(nContextNode, sXPath, xmiPathInfo, false, bDefault);
    }

    /**
     * This method returns the boolean value for the given XPath. This version return the boolean value for XPath expressions that
     * evaluate to a node, attribute or a boolean value (e.g. "number(.)").
     * 
     * @param nContextNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param prPathInfo Extra XPath information, e.g. namespace mappings
     * @param bAllMatches If <code>false</code> and there are multiple matches, only the first one is returned. Otherwise the
     *            results are concatenated together.
     * @param bDefault The default value to return if there was nothing found.
     * @return The boolean value.
     * @throws TransformerException In case of any XPath exceptions.
     */
    public static boolean getBooleanValue(Node nContextNode, String sXPath, PrefixResolver prPathInfo, boolean bAllMatches,
            boolean bDefault) throws TransformerException
    {
        boolean bReturn = bDefault;

        String sTemp = getStringValue(nContextNode, sXPath, prPathInfo, bAllMatches, String.valueOf(bDefault));

        bReturn = Boolean.parseBoolean(sTemp);

        return bReturn;
    }

    /**
     * This method returns the Date value for the given XPath. This version return the Date value for XPath expressions that
     * evaluate to a node, attribute or a Date value (e.g. "Date(.)"). This version returns only the first match. <br>
     * As a prefix resolver it uses the one from the NamespaceConstants class.
     * 
     * @param nContextNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @return The date value.
     * @throws TransformerException
     */
    public static Date getDateValue(Node nContextNode, String sXPath) throws TransformerException
    {
        return getDateValue(nContextNode, sXPath, NamespaceConstants.getPrefixResolver(), false, null);
    }

    /**
     * This method returns the Dateeger value for the given XPath.
     * 
     * @param nContextNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param bMandatory Whether or not the field is mandatory.
     * @return The Dateeger value. If the value is not found and bMandatory is false the default value is false.
     * @throws TransformerException If the value cannot be found and bMandatory is true.
     */
    public static Date getDateValue(Node nContextNode, String sXPath, boolean bMandatory) throws TransformerException
    {
        Date dReturn = null;

        String sTemp = getStringValue(nContextNode, sXPath, bMandatory);

        if ((sTemp != null) && (sTemp.length() != 0))
        {
            dReturn = DateUtil.parseDate(sTemp);
        }

        if ((bMandatory == true) && (dReturn == null))
        {
            throw new TransformerException("Cannot find xpath " + sXPath + " in the definition.");
        }

        return dReturn;
    }

    /**
     * This method returns the Date value for the given XPath. This version return the Date value for XPath expressions that
     * evaluate to a node, attribute or a Date value (e.g. "Date(.)"). This version returns only the first match.
     * 
     * @param nContextNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @return The date value.
     * @throws TransformerException
     */
    public static Date getDateValue(Node nContextNode, String sXPath, PrefixResolver xmiPathInfo) throws TransformerException
    {
        return getDateValue(nContextNode, sXPath, xmiPathInfo, false, null);
    }

    /**
     * This method returns the Date value for the given XPath. This version return the Date value for XPath expressions that
     * evaluate to a node, attribute or a Date value (e.g. "Date(.)").<br>
     * As a prefix resolver it uses the one from the NamespaceConstants class.
     * 
     * @param nContextNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param dDefault The default value to return if there was nothing found.
     * @return The date value.
     * @throws TransformerException
     */
    public static Date getDateValue(Node nContextNode, String sXPath, Date dDefault) throws TransformerException
    {
        return getDateValue(nContextNode, sXPath, NamespaceConstants.getPrefixResolver(), false, dDefault);
    }

    /**
     * This method returns the Date value for the given XPath. This version return the Date value for XPath expressions that
     * evaluate to a node, attribute or a Date value (e.g. "Date(.)").
     * 
     * @param nContextNode The node to operate on.
     * @param sPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @param bAllMatches If <code>false</code> and there are multiple matches, only the first one is returned. Otherwise the
     *            results are concatenated together.
     * @return The date value.
     * @throws TransformerException
     */
    public static Date getDateValue(Node nContextNode, String sPath, PrefixResolver xmiPathInfo, boolean bAllMatches)
            throws TransformerException
    {
        return getDateValue(nContextNode, sPath, xmiPathInfo, bAllMatches, null);
    }

    /**
     * This method returns the Date value for the given XPath. This version return the Date value for XPath expressions that
     * evaluate to a node, attribute or a Date value (e.g. "Date(.)").
     * 
     * @param nContextNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @param dDefault The default value to return if there was nothing found.
     * @return The date value.
     * @throws TransformerException
     */
    public static Date getDateValue(Node nContextNode, String sXPath, PrefixResolver xmiPathInfo, Date dDefault)
            throws TransformerException
    {
        return getDateValue(nContextNode, sXPath, xmiPathInfo, false, dDefault);
    }

    /**
     * This method returns the Date value for the given XPath. This version return the Date value for XPath expressions that
     * evaluate to a node, attribute or a Date value (e.g. "number(.)").
     * 
     * @param nContextNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param prPathInfo Extra XPath information, e.g. namespace mappings
     * @param bAllMatches If <code>false</code> and there are multiple matches, only the first one is returned. Otherwise the
     *            results are concatenated together.
     * @param dDefault The default value to return if there was nothing found.
     * @return The date value.
     * @throws TransformerException
     */
    public static Date getDateValue(Node nContextNode, String sXPath, PrefixResolver prPathInfo, boolean bAllMatches,
            Date dDefault) throws TransformerException
    {
        Date dReturn = dDefault;

        String sTemp = getStringValue(nContextNode, sXPath, prPathInfo, bAllMatches, String.valueOf(dDefault));

        dReturn = DateUtil.parseDate(sTemp);

        return dReturn;
    }

    /**
     * This method returns the int value for the given XPath. This version return the int value for XPath expressions that
     * evaluate to a node, attribute or a int value (e.g. "int(.)"). This version returns only the first match.<br>
     * As a prefix resolver it uses the one from the NamespaceConstants class.
     * 
     * @param nContextNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @return The int value.
     * @throws TransformerException
     */
    public static int getIntegerValue(Node nContextNode, String sXPath) throws TransformerException
    {
        return getIntegerValue(nContextNode, sXPath, NamespaceConstants.getPrefixResolver(), false, -1);
    }

    /**
     * This method returns the integer value for the given XPath.
     * 
     * @param nContextNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param bMandatory Whether or not the field is mandatory.
     * @return The integer value. If the value is not found and bMandatory is false the default value is false.
     * @throws TransformerException If the value cannot be found and bMandatory is true.
     */
    public static int getIntegerValue(Node nContextNode, String sXPath, boolean bMandatory) throws TransformerException
    {
        int iReturn = -1;

        String sTemp = getStringValue(nContextNode, sXPath, bMandatory);

        if ((sTemp != null) && (sTemp.length() != 0))
        {
            try
            {
                iReturn = Integer.parseInt(sTemp);
            }
            catch (NumberFormatException nfe)
            {
                // Ignore it.
            }
        }

        if ((bMandatory == true) && (iReturn == -1))
        {
            throw new TransformerException("Cannot find xpath " + sXPath + " in the definition.");
        }

        return iReturn;
    }

    /**
     * This method returns the int value for the given XPath. This version return the int value for XPath expressions that
     * evaluate to a node, attribute or a int value (e.g. "int(.)"). This version returns only the first match.
     * 
     * @param nContextNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @return The int value.
     * @throws TransformerException
     */
    public static int getIntegerValue(Node nContextNode, String sXPath, PrefixResolver xmiPathInfo) throws TransformerException
    {
        return getIntegerValue(nContextNode, sXPath, xmiPathInfo, false, -1);
    }

    /**
     * This method returns the int value for the given XPath. This version return the int value for XPath expressions that
     * evaluate to a node, attribute or a int value (e.g. "int(.)").<br>
     * As a prefix resolver it uses the one from the NamespaceConstants class.
     * 
     * @param nContextNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param iDefault The default value to return if there was nothing found.
     * @return The int value.
     * @throws TransformerException
     */
    public static int getIntegerValue(Node nContextNode, String sXPath, int iDefault) throws TransformerException
    {
        return getIntegerValue(nContextNode, sXPath, NamespaceConstants.getPrefixResolver(), false, iDefault);
    }

    /**
     * This method returns the int value for the given XPath. This version return the int value for XPath expressions that
     * evaluate to a node, attribute or a int value (e.g. "int(.)").
     * 
     * @param nContextNode The node to operate on.
     * @param sPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @param bAllMatches If <code>false</code> and there are multiple matches, only the first one is returned. Otherwise the
     *            results are concatenated together.
     * @return The int value.
     * @throws TransformerException
     */
    public static int getIntegerValue(Node nContextNode, String sPath, PrefixResolver xmiPathInfo, boolean bAllMatches)
            throws TransformerException
    {
        return getIntegerValue(nContextNode, sPath, xmiPathInfo, bAllMatches, -1);
    }

    /**
     * This method returns the int value for the given XPath. This version return the int value for XPath expressions that
     * evaluate to a node, attribute or a int value (e.g. "int(.)").
     * 
     * @param nContextNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @param iDefault The default value to return if there was nothing found.
     * @return The int value.
     * @throws TransformerException
     */
    public static int getIntegerValue(Node nContextNode, String sXPath, PrefixResolver xmiPathInfo, int iDefault)
            throws TransformerException
    {
        return getIntegerValue(nContextNode, sXPath, xmiPathInfo, false, iDefault);
    }

    /**
     * This method returns the int value for the given XPath. This version return the int value for XPath expressions that
     * evaluate to a node, attribute or a int value (e.g. "number(.)").
     * 
     * @param nContextNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param prPathInfo Extra XPath information, e.g. namespace mappings
     * @param bAllMatches If <code>false</code> and there are multiple matches, only the first one is returned. Otherwise the
     *            results are concatenated together.
     * @param iDefault The default value to return if there was nothing found.
     * @return The int value.
     * @throws TransformerException
     */
    public static int getIntegerValue(Node nContextNode, String sXPath, PrefixResolver prPathInfo, boolean bAllMatches,
            int iDefault) throws TransformerException
    {
        int iReturn = iDefault;

        String sTemp = getStringValue(nContextNode, sXPath, prPathInfo, bAllMatches, String.valueOf(iDefault));

        iReturn = Integer.parseInt(sTemp);

        return iReturn;
    }

    /**
     * This method returns the long value for the given XPath. This version return the long value for XPath expressions that
     * evaluate to a node, attribute or a long value (e.g. "long(.)"). This version returns only the first match. <br>
     * As a prefix resolver it uses the one from the NamespaceConstants class.
     * 
     * @param nContextNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @return The long value.
     * @throws TransformerException
     */
    public static long getLongValue(Node nContextNode, String sXPath) throws TransformerException
    {
        return getLongValue(nContextNode, sXPath, NamespaceConstants.getPrefixResolver(), false, -1);
    }

    /**
     * This method returns the long value for the given XPath.
     * 
     * @param nContextNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param bMandatory Whether or not the field is mandatory.
     * @return The long value. If the value is not found and bMandatory is false the default value is false.
     * @throws TransformerException If the value cannot be found and bMandatory is true.
     */
    public static long getLongValue(Node nContextNode, String sXPath, boolean bMandatory) throws TransformerException
    {
        long lReturn = -1;

        String sTemp = getStringValue(nContextNode, sXPath, bMandatory);

        if ((sTemp != null) && (sTemp.length() != 0))
        {
            try
            {
                lReturn = Long.parseLong(sTemp);
            }
            catch (NumberFormatException nfe)
            {
                // Ignore it.
            }
        }

        if ((bMandatory == true) && (lReturn == -1))
        {
            throw new TransformerException("Cannot find xpath " + sXPath + " in the definition.");
        }

        return lReturn;
    }

    /**
     * This method returns the long value for the given XPath. This version return the long value for XPath expressions that
     * evaluate to a node, attribute or a long value (e.g. "long(.)"). This version returns only the first match.
     * 
     * @param nContextNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @return The long value.
     * @throws TransformerException
     */
    public static long getLongValue(Node nContextNode, String sXPath, PrefixResolver xmiPathInfo) throws TransformerException
    {
        return getLongValue(nContextNode, sXPath, xmiPathInfo, false, -1);
    }

    /**
     * This method returns the long value for the given XPath. This version return the long value for XPath expressions that
     * evaluate to a node, attribute or a long value (e.g. "long(.)").<br>
     * As a prefix resolver it uses the one from the NamespaceConstants class.
     * 
     * @param nContextNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param iDefault The default value to return if there was nothing found.
     * @return The long value.
     * @throws TransformerException
     */
    public static long getLongValue(Node nContextNode, String sXPath, long iDefault) throws TransformerException
    {
        return getLongValue(nContextNode, sXPath, NamespaceConstants.getPrefixResolver(), false, iDefault);
    }

    /**
     * This method returns the long value for the given XPath. This version return the long value for XPath expressions that
     * evaluate to a node, attribute or a long value (e.g. "long(.)").
     * 
     * @param nContextNode The node to operate on.
     * @param sPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @param bAllMatches If <code>false</code> and there are multiple matches, only the first one is returned. Otherwise the
     *            results are concatenated together.
     * @return The long value.
     * @throws TransformerException
     */
    public static long getLongValue(Node nContextNode, String sPath, PrefixResolver xmiPathInfo, boolean bAllMatches)
            throws TransformerException
    {
        return getLongValue(nContextNode, sPath, xmiPathInfo, bAllMatches, -1);
    }

    /**
     * This method returns the long value for the given XPath. This version return the long value for XPath expressions that
     * evaluate to a node, attribute or a long value (e.g. "long(.)").
     * 
     * @param nContextNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @param iDefault The default value to return if there was nothing found.
     * @return The long value.
     * @throws TransformerException
     */
    public static long getLongValue(Node nContextNode, String sXPath, PrefixResolver xmiPathInfo, long iDefault)
            throws TransformerException
    {
        return getLongValue(nContextNode, sXPath, xmiPathInfo, false, iDefault);
    }

    /**
     * This method returns the long value for the given XPath. This version return the long value for XPath expressions that
     * evaluate to a node, attribute or a long value (e.g. "number(.)").
     * 
     * @param nContextNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param prPathInfo Extra XPath information, e.g. namespace mappings
     * @param bAllMatches If <code>false</code> and there are multiple matches, only the first one is returned. Otherwise the
     *            results are concatenated together.
     * @param iDefault The default value to return if there was nothing found.
     * @return The long value.
     * @throws TransformerException
     */
    public static long getLongValue(Node nContextNode, String sXPath, PrefixResolver prPathInfo, boolean bAllMatches,
            long iDefault) throws TransformerException
    {
        long lReturn = iDefault;

        String sTemp = getStringValue(nContextNode, sXPath, prPathInfo, bAllMatches, String.valueOf(iDefault));

        lReturn = Long.parseLong(sTemp);

        return lReturn;
    }

    /**
     * This method returns the double value for the given XPath. This version return the double value for XPath expressions that
     * evaluate to a node, attribute or a double value (e.g. "double(.)"). This version returns only the first match. <br>
     * As a prefix resolver it uses the one from the NamespaceConstants class.
     * 
     * @param nContextNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @return The double value.
     * @throws TransformerException
     */
    public static double getDoubleValue(Node nContextNode, String sXPath) throws TransformerException
    {
        return getDoubleValue(nContextNode, sXPath, NamespaceConstants.getPrefixResolver(), false, -1);
    }

    /**
     * This method returns the double value for the given XPath.
     * 
     * @param nContextNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param bMandatory Whether or not the field is mandatory.
     * @return The double value. If the value is not found and bMandatory is false the default value is false.
     * @throws TransformerException If the value cannot be found and bMandatory is true.
     */
    public static double getDoubleValue(Node nContextNode, String sXPath, boolean bMandatory) throws TransformerException
    {
        double lReturn = -1;

        String sTemp = getStringValue(nContextNode, sXPath, bMandatory);

        if ((sTemp != null) && (sTemp.length() != 0))
        {
            try
            {
                lReturn = Double.parseDouble(sTemp);
            }
            catch (NumberFormatException nfe)
            {
                // Ignore it.
            }
        }

        if ((bMandatory == true) && (lReturn == -1))
        {
            throw new TransformerException("Cannot find xpath " + sXPath + " in the definition.");
        }

        return lReturn;
    }

    /**
     * This method returns the double value for the given XPath. This version return the double value for XPath expressions that
     * evaluate to a node, attribute or a double value (e.g. "double(.)"). This version returns only the first match.
     * 
     * @param nContextNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @return The double value.
     * @throws TransformerException
     */
    public static double getDoubleValue(Node nContextNode, String sXPath, PrefixResolver xmiPathInfo) throws TransformerException
    {
        return getDoubleValue(nContextNode, sXPath, xmiPathInfo, false, -1);
    }

    /**
     * This method returns the double value for the given XPath. This version return the double value for XPath expressions that
     * evaluate to a node, attribute or a double value (e.g. "double(.)").<br>
     * As a prefix resolver it uses the one from the NamespaceConstants class.
     * 
     * @param nContextNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param iDefault The default value to return if there was nothing found.
     * @return The double value.
     * @throws TransformerException
     */
    public static double getDoubleValue(Node nContextNode, String sXPath, double iDefault) throws TransformerException
    {
        return getDoubleValue(nContextNode, sXPath, NamespaceConstants.getPrefixResolver(), false, iDefault);
    }

    /**
     * This method returns the double value for the given XPath. This version return the double value for XPath expressions that
     * evaluate to a node, attribute or a double value (e.g. "double(.)").
     * 
     * @param nContextNode The node to operate on.
     * @param sPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @param bAllMatches If <code>false</code> and there are multiple matches, only the first one is returned. Otherwise the
     *            results are concatenated together.
     * @return The double value.
     * @throws TransformerException
     */
    public static double getDoubleValue(Node nContextNode, String sPath, PrefixResolver xmiPathInfo, boolean bAllMatches)
            throws TransformerException
    {
        return getDoubleValue(nContextNode, sPath, xmiPathInfo, bAllMatches, -1);
    }

    /**
     * This method returns the double value for the given XPath. This version return the double value for XPath expressions that
     * evaluate to a node, attribute or a double value (e.g. "double(.)").
     * 
     * @param nContextNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @param iDefault The default value to return if there was nothing found.
     * @return The double value.
     * @throws TransformerException
     */
    public static double getDoubleValue(Node nContextNode, String sXPath, PrefixResolver xmiPathInfo, double iDefault)
            throws TransformerException
    {
        return getDoubleValue(nContextNode, sXPath, xmiPathInfo, false, iDefault);
    }

    /**
     * This method returns the double value for the given XPath. This version return the double value for XPath expressions that
     * evaluate to a node, attribute or a double value (e.g. "number(.)").
     * 
     * @param nContextNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param prPathInfo Extra XPath information, e.g. namespace mappings
     * @param bAllMatches If <code>false</code> and there are multiple matches, only the first one is returned. Otherwise the
     *            results are concatenated together.
     * @param iDefault The default value to return if there was nothing found.
     * @return The double value.
     * @throws TransformerException
     */
    public static double getDoubleValue(Node nContextNode, String sXPath, PrefixResolver prPathInfo, boolean bAllMatches,
            double iDefault) throws TransformerException
    {
        double lReturn = iDefault;

        String sTemp = getStringValue(nContextNode, sXPath, prPathInfo, bAllMatches, String.valueOf(iDefault));

        lReturn = Double.parseDouble(sTemp);

        return lReturn;
    }

    /**
     * This method sets the string value for the given XPath. If the resulting node is a text node then it's value is set. If the
     * result is an element all child text nodes are removed and a single text node is added with the given value.
     * 
     * @param context the context node to execute the XPath on.
     * @param xPath the xpath to execute.
     * @param value The ne value for the tag.
     * @return true if the tag was found and a new value was set. Otherwise false.
     * @throws TransformerException In case of any exceptions.
     */
    public static boolean setStringValue(Node context, String xPath, String value) throws TransformerException
    {
        return setStringValue(context, xPath, null, value);
    }

    /**
     * This method sets the string value for the given XPath. If the resulting node is a text node then it's value is set. If the
     * result is an element all child text nodes are removed and a single text node is added with the given value.
     * 
     * @param context the context node to execute the XPath on.
     * @param xPath the xpath to execute.
     * @param xmi the namespace prefix resolver to use.
     * @param value The ne value for the tag.
     * @return true if the tag was found and a new value was set. Otherwise false.
     * @throws TransformerException In case of any exceptions.
     */
    public static boolean setStringValue(Node context, String xPath, PrefixResolver xmi, String value)
            throws TransformerException
    {
        boolean retVal = false;

        Node result = selectSingleNode(context, xPath, xmi);

        if (result != null)
        {
            // Check if it is a text node.
            if (result.getNodeType() == Node.CDATA_SECTION_NODE || result.getNodeType() == Node.TEXT_NODE)
            {
                result.setNodeValue(value);
                retVal = true;
            }
            else if (result.getNodeType() == Node.ELEMENT_NODE)
            {
                while (result.hasChildNodes())
                {
                    result.removeChild(result.getFirstChild());
                }

                Text newText = result.getOwnerDocument().createTextNode(value);
                result.appendChild(newText);
                retVal = true;
            }
            else
            {
                // Unknown node.
            }
        }

        return retVal;
    }

    /**
     * This method returns the string value for the given XPath. This version return the string value for XPath expressions that
     * evaluate to a node, attribute or a string value (e.g. "string(.)"). This version returns only the first match.<br>
     * As a prefix resolver it uses the one from the NamespaceConstants class.
     * 
     * @param nContextNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @return The string value.
     * @throws TransformerException
     */
    public static String getStringValue(Node nContextNode, String sXPath) throws TransformerException
    {
        return getStringValue(nContextNode, sXPath, NamespaceConstants.getPrefixResolver(), false, null);
    }

    /**
     * This method returns the string value for the given XPath.
     * 
     * @param nContextNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param bMandatory Whether or not the field is mandatory.
     * @return The string value.
     * @throws TransformerException If the value cannot be found and bMandatory is true.
     */
    public static String getStringValue(Node nContextNode, String sXPath, boolean bMandatory) throws TransformerException
    {
        String sReturn = null;

        NodeList nlResult = selectNodeList(nContextNode, sXPath, NamespaceConstants.getPrefixResolver());

        if (nlResult.getLength() > 0)
        {
            Node nResult = nlResult.item(0);

            switch (nResult.getNodeType())
            {
                case Node.TEXT_NODE:
                case Node.ATTRIBUTE_NODE:
                case Node.CDATA_SECTION_NODE:
                    sReturn = nResult.getNodeValue();
                    break;

                case Node.ELEMENT_NODE:
                    sReturn = getStringValue(nContextNode, "./text()", NamespaceConstants.getPrefixResolver(), true, "");
                    break;
            }
        }

        if ((bMandatory == true) && ((sReturn == null) || (sReturn.length() == 0)))
        {
            throw new TransformerException("Cannot find xpath " + sXPath + " in the definition.");
        }

        return sReturn;
    }

    /**
     * This method returns the string value for the given XPath. This version return the string value for XPath expressions that
     * evaluate to a node, attribute or a string value (e.g. "string(.)"). This version returns only the first match.
     * 
     * @param nContextNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @return The string value.
     * @throws TransformerException
     */
    public static String getStringValue(Node nContextNode, String sXPath, PrefixResolver xmiPathInfo) throws TransformerException
    {
        return getStringValue(nContextNode, sXPath, xmiPathInfo, false, null);
    }

    /**
     * This method returns the string value for the given XPath. This version return the string value for XPath expressions that
     * evaluate to a node, attribute or a string value (e.g. "string(.)").<br>
     * As a prefix resolver it uses the one from the NamespaceConstants class.
     * 
     * @param nContextNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param sDefault The default value to return if there was nothing found.
     * @return The string value.
     * @throws TransformerException
     */
    public static String getStringValue(Node nContextNode, String sXPath, String sDefault) throws TransformerException
    {
        return getStringValue(nContextNode, sXPath, NamespaceConstants.getPrefixResolver(), false, sDefault);
    }

    /**
     * This method returns the string value for the given XPath. This version return the string value for XPath expressions that
     * evaluate to a node, attribute or a string value (e.g. "string(.)").
     * 
     * @param nContextNode The node to operate on.
     * @param sPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @param bAllMatches If <code>false</code> and there are multiple matches, only the first one is returned. Otherwise the
     *            results are concatenated together.
     * @return The string value.
     * @throws TransformerException
     */
    public static String getStringValue(Node nContextNode, String sPath, PrefixResolver xmiPathInfo, boolean bAllMatches)
            throws TransformerException
    {
        return getStringValue(nContextNode, sPath, xmiPathInfo, bAllMatches, null);
    }

    /**
     * This method returns the string value for the given XPath. This version return the string value for XPath expressions that
     * evaluate to a node, attribute or a string value (e.g. "string(.)").
     * 
     * @param nContextNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @param sDefault The default value to return if there was nothing found.
     * @return The string value.
     * @throws TransformerException
     */
    public static String getStringValue(Node nContextNode, String sXPath, PrefixResolver xmiPathInfo, String sDefault)
            throws TransformerException
    {
        return getStringValue(nContextNode, sXPath, xmiPathInfo, false, sDefault);
    }

    /**
     * This method returns the string value for the given XPath. This version return the string value for XPath expressions that
     * evaluate to a node, attribute or a string value (e.g. "string(.)").
     * 
     * @param nContextNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param prPathInfo Extra XPath information, e.g. namespace mappings
     * @param bAllMatches If <code>false</code> and there are multiple matches, only the first one is returned. Otherwise the
     *            results are concatenated together.
     * @param sDefault The default value to return if there was nothing found.
     * @return The string value.
     * @throws TransformerException
     */
    public static String getStringValue(Node nContextNode, String sXPath, PrefixResolver prPathInfo, boolean bAllMatches,
            String sDefault) throws TransformerException
    {
        String sReturn = sDefault;

        NodeList nlResult = selectNodeList(nContextNode, sXPath, prPathInfo);

        if (nlResult.getLength() > 0)
        {
            Node nResult = nlResult.item(0);

            switch (nResult.getNodeType())
            {
                case Node.TEXT_NODE:
                case Node.ATTRIBUTE_NODE:
                case Node.CDATA_SECTION_NODE:
                    sReturn = nResult.getNodeValue();
                    break;

                case Node.ELEMENT_NODE:
                    sReturn = getStringValue(nResult, "./text()", prPathInfo, bAllMatches, sDefault);
                    break;
            }
        }

        return sReturn;
    }

    /**
     * This method returns an array of nodes that match the passed on criteria.
     * 
     * @param eRoot The root element.
     * @param sPath The XPath to execute.
     * @return The array of nodes.
     */
    public static Node[] match(Element eRoot, String sPath)
    {
        try
        {
            NodeIterator niIter = (NodeIterator) s_mSelectNodeIterator.invoke(null, new Object[] { eRoot, sPath });
            Node nNode;
            List<Node> lResList = new ArrayList<Node>(128);

            while ((nNode = niIter.nextNode()) != null)
            {
                lResList.add(nNode);
            }

            return lResList.toArray(new Node[lResList.size()]);
        }
        catch (Exception ignored)
        {
            return new Element[0];
        }
    }

    /**
     * Use an XPath string to select a nodelist. XPath namespace prefixes are resolved using the NamespaceConstants class.
     * 
     * @param nContextNode The node to start searching from.
     * @param sXPath A valid XPath string.
     * @return A NodeIterator, should never be null.
     * @throws TransformerException
     */
    public static NodeList prSelectNodeList(Node nContextNode, String sXPath) throws TransformerException
    {
        return selectNodeList(nContextNode, sXPath, NamespaceConstants.getPrefixResolver());
    }

    /**
     * Use an XPath string to select a single node. namespace prefixes are resolved using the NamespaceConstants class.
     * 
     * @param nContextNode The node to start searching from.
     * @param sXPath A valid XPath string.
     * @return The first node found that matches the XPath, or null.
     * @throws TransformerException
     */
    public static Node prSelectSingleNode(Node nContextNode, String sXPath) throws TransformerException
    {
        Node nReturn = null;

        NodeList nl = prSelectNodeList(nContextNode, sXPath);

        if ((nl != null) && (nl.getLength() > 0))
        {
            nReturn = nl.item(0);
        }

        return nReturn;
    }

    /**
     * Use an XPath string to select a nodelist. XPath namespace prefixes are resolved from the contextNode.
     * 
     * @param nContextNode The node to start searching from.
     * @param sXPath A valid XPath string.
     * @return A NodeIterator, should never be null.
     * @throws TransformerException
     */
    public static NodeIterator selectNodeIterator(Node nContextNode, String sXPath) throws TransformerException
    {
        try
        {
            return (NodeIterator) s_mSelectNodeIterator.invoke(null, new Object[] { nContextNode, sXPath });
        }
        catch (Exception e)
        {
            throw new TransformerException(e);
        }
    }

    /**
     * Use an XPath string to select a nodelist. XPath namespace prefixes are resolved from the contextNode.
     * 
     * @param nContextNode The node to start searching from.
     * @param sXPath A valid XPath string.
     * @return A NodeIterator, should never be null.
     * @throws TransformerException
     */
    public static NodeList selectNodeList(Node nContextNode, String sXPath) throws TransformerException
    {
        try
        {
            return (NodeList) s_mSelectNodeList.invoke(null, new Object[] { nContextNode, sXPath });
        }
        catch (Exception e)
        {
            throw new TransformerException(e);
        }
    }

    /**
     * This method returns the list of nodes based on the given prefix resolver.
     * 
     * @param nContextNode The context node.
     * @param sXPath THe XPath expression.
     * @param prResolver The prefix resolver to use.
     * @return The list of nodes.
     * @throws TransformerException In case of any exceptions.
     */
    public static NodeList selectNodeList(Node nContextNode, String sXPath, PrefixResolver prResolver)
            throws TransformerException
    {
        NodeList nlReturn = null;

        try
        {
            Object oReturn = s_mEval.invoke(null, new Object[] { nContextNode, sXPath, prResolver });

            if (oReturn != null)
            {
                Method mNodeList = oReturn.getClass().getMethod("nodelist", new Class[0]);
                nlReturn = (NodeList) mNodeList.invoke(oReturn, new Object[0]);
            }
        }
        catch (Exception e)
        {
            throw new TransformerException(e);
        }

        return nlReturn;
    }

    /**
     * Use an XPath string to select a single node. XPath namespace prefixes are resolved from the context node, which may not be
     * what you want (see the next method).
     * 
     * @param nContextNode The node to start searching from.
     * @param sXPath A valid XPath string.
     * @return The first node found that matches the XPath, or null.
     * @throws TransformerException
     */
    public static Node selectSingleNode(Node nContextNode, String sXPath) throws TransformerException
    {
        try
        {
            return (Node) s_mSelectSingleNode.invoke(null, new Object[] { nContextNode, sXPath });
        }
        catch (Exception e)
        {
            throw new TransformerException(e);
        }
    }

    /**
     * Use an XPath string to select a single node. namespace prefixes are resolved using the NamespaceConstants class.
     * 
     * @param nContextNode The node to start searching from.
     * @param sXPath A valid XPath string.
     * @param prResolver The prefix resolver to use.
     * @return The first node found that matches the XPath, or null.
     * @throws TransformerException
     */
    public static Node selectSingleNode(Node nContextNode, String sXPath, PrefixResolver prResolver) throws TransformerException
    {
        Node nReturn = null;

        NodeList nl = selectNodeList(nContextNode, sXPath, prResolver);

        if ((nl != null) && (nl.getLength() > 0))
        {
            nReturn = nl.item(0);
        }

        return nReturn;
    }

    /**
     * Use an XPath string to select a single node. XPath namespace prefixes are resolved from the context node, which may not be
     * what you want (see the next method).
     * 
     * @param nContextNode The node to start searching from.
     * @param sXPath A valid XPath string.
     * @param nNamespaceNode The node containing the namespace declarations.
     * @return The first node found that matches the XPath, or null.
     * @throws TransformerException
     */
    public static Node selectSingleNode(Node nContextNode, String sXPath, Node nNamespaceNode) throws TransformerException
    {
        try
        {
            return (Node) s_mSelectSingleNodeNamespace.invoke(null, new Object[] { nContextNode, sXPath, nNamespaceNode });
        }
        catch (Exception e)
        {
            throw new TransformerException(e);
        }
    }

    /**
     * This method writes the XML into a nicely formatted string.
     * 
     * @param eRoot The root element.
     * @return The nicely formatted string.
     */
    public static String writePretty(Element eRoot)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        writePretty(eRoot, baos);

        return new String(baos.toByteArray());
    }

    /**
     * This method writes the XML into a nicely formatted string.
     * 
     * @param eRoot The root element.
     * @param osOutput The stream to write the data to.
     */
    public static void writePretty(Element eRoot, OutputStream osOutput)
    {
        Object of;

        try
        {
            of = s_cOutputFormat.newInstance(new Object[] { eRoot.getOwnerDocument() });

            s_mOFSetIndent.invoke(of, new Object[] { 4 });
            s_mOFSetIndenting.invoke(of, new Object[] { true });

            Object xs = s_cXMLSerializer.newInstance(new Object[] { osOutput, of });

            s_mXSSerialize.invoke(xs, new Object[] { eRoot });
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error writing the XML to a pretty format", e);
        }
    }
}
