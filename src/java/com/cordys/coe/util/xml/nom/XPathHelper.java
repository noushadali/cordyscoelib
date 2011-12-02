package com.cordys.coe.util.xml.nom;

import com.cordys.coe.util.DateUtil;
import com.cordys.coe.util.StringUtils;

import com.eibus.xml.nom.Node;
import com.eibus.xml.nom.NodeType;
import com.eibus.xml.xpath.NodeSet;
import com.eibus.xml.xpath.ResultNode;
import com.eibus.xml.xpath.XPath;
import com.eibus.xml.xpath.XPathMetaInfo;
import com.eibus.xml.xpath.XPathResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class contains helper methods for NOM XPaths.
 * 
 * @author pgussow
 */
public class XPathHelper
{
    /**
     * This method returns the boolean value for the given XPath. This version return the boolean value for XPath expressions that
     * evaluate to a node, attribute or a boolean value (e.g. "boolean(.)"). This version returns only the first match.<br>
     * As a prefix resolver it uses the one from the NamespaceConstants class.
     * <p>
     * Note: The XPath executed is not explicitly deleted. This means that it is up to the garbage collector to decide when the
     * XPath object (and thus the native object tree) is deleted. This has no influence on the involved NOM nodes, just the XPath
     * object. This means that you should use this method when having static xpaths like ./ns:item/text(). But when executing
     * dynamic XPaths like "./ns:item[@id='" + itemID "']/text()" you should use the get***ValueDynamic methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @return The boolean value.
     */
    public static boolean getBooleanValue(int iNode, String sXPath)
    {
        return getBooleanValue(iNode, sXPath, NamespaceConstants.getXPathMetaInfo(), false, false);
    }

    /**
     * This method returns the boolean value for the given XPath. This version return the boolean value for XPath expressions that
     * evaluate to a node, attribute or a boolean value (e.g. "boolean(.)"). This version returns only the first match.
     * <p>
     * Note: The XPath executed is not explicitly deleted. This means that it is up to the garbage collector to decide when the
     * XPath object (and thus the native object tree) is deleted. This has no influence on the involved NOM nodes, just the XPath
     * object. This means that you should use this method when having static xpaths like ./ns:item/text(). But when executing
     * dynamic XPaths like "./ns:item[@id='" + itemID "']/text()" you should use the get***ValueDynamic methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @return The boolean value.
     */
    public static boolean getBooleanValue(int iNode, String sXPath, XPathMetaInfo xmiPathInfo)
    {
        return getBooleanValue(iNode, sXPath, xmiPathInfo, false, false);
    }

    /**
     * This method returns the boolean value for the given XPath. This version return the boolean value for XPath expressions that
     * evaluate to a node, attribute or a boolean value (e.g. "boolean(.)").
     * <p>
     * Note: The XPath executed is not explicitly deleted. This means that it is up to the garbage collector to decide when the
     * XPath object (and thus the native object tree) is deleted. This has no influence on the involved NOM nodes, just the XPath
     * object. This means that you should use this method when having static xpaths like ./ns:item/text(). But when executing
     * dynamic XPaths like "./ns:item[@id='" + itemID "']/text()" you should use the get***ValueDynamic methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @param bDefault The default value to return if there was nothing found.
     * @return The boolean value.
     */
    public static boolean getBooleanValue(int iNode, String sXPath, XPathMetaInfo xmiPathInfo, boolean bDefault)
    {
        return getBooleanValue(iNode, sXPath, xmiPathInfo, false, bDefault);
    }

    /**
     * This method returns the boolean value for the given XPath.
     * <p>
     * Note: The XPath executed is not explicitly deleted. This means that it is up to the garbage collector to decide when the
     * XPath object (and thus the native object tree) is deleted. This has no influence on the involved NOM nodes, just the XPath
     * object. This means that you should use this method when having static xpaths like ./ns:item/text(). But when executing
     * dynamic XPaths like "./ns:item[@id='" + itemID "']/text()" you should use the get***ValueDynamic methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param bMandatory Whether or not the field is mandatory.
     * @return The boolean value. If the value is not found and bMandatory is false the default value is false.
     * @throws NOMXPathParseException If the value cannot be found and bMandatory is true.
     */
    public static boolean getBooleanValue(int iNode, String sXPath, boolean bMandatory) throws NOMXPathParseException
    {
        boolean bReturn = false;

        String sTemp = getStringValue(iNode, sXPath, bMandatory);

        if (StringUtils.isSet(sTemp))
        {
            bReturn = "true".equalsIgnoreCase(sTemp);
        }
        else if (bMandatory == true)
        {
            throw new NOMXPathParseException("Cannot find xpath " + sXPath + " in the definition.");
        }

        return bReturn;
    }

    /**
     * This method returns the boolean value for the given XPath. This version return the boolean value for XPath expressions that
     * evaluate to a node, attribute or a boolean value (e.g. "number(.)").
     * <p>
     * Note: The XPath executed is not explicitly deleted. This means that it is up to the garbage collector to decide when the
     * XPath object (and thus the native object tree) is deleted. This has no influence on the involved NOM nodes, just the XPath
     * object. This means that you should use this method when having static xpaths like ./ns:item/text(). But when executing
     * dynamic XPaths like "./ns:item[@id='" + itemID "']/text()" you should use the get***ValueDynamic methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param prPathInfo Extra XPath information, e.g. namespace mappings
     * @param bAllMatches If <code>false</code> and there are multiple matches, only the first one is returned. Otherwise the
     *            results are concatenated together.
     * @param bDefault The default value to return if there was nothing found.
     * @return The boolean value.
     */
    public static boolean getBooleanValue(int iNode, String sXPath, XPathMetaInfo prPathInfo, boolean bAllMatches,
            boolean bDefault)
    {
        boolean bReturn = bDefault;

        String sTemp = getStringValue(iNode, sXPath, prPathInfo, bAllMatches, String.valueOf(bDefault));

        bReturn = Boolean.parseBoolean(sTemp);

        return bReturn;
    }

    /**
     * This method returns the boolean value for the given XPath. This version return the boolean value for XPath expressions that
     * evaluate to a node, attribute or a boolean value (e.g. "boolean(.)"). This version returns only the first match.<br>
     * As a prefix resolver it uses the one from the NamespaceConstants class.
     * <p>
     * Note: The XPath executed is explicitly deleted. This means you should use this method for XPaths that are executed only
     * once like "./ns:item[@id='" + itemID "']/text()". For XPaths like "./ns:item/text()" you should use the get***Value
     * methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @return The boolean value.
     */
    public static boolean getBooleanValueDynamic(int iNode, String sXPath)
    {
        return getBooleanValueDynamic(iNode, sXPath, NamespaceConstants.getXPathMetaInfo(), false, false);
    }

    /**
     * This method returns the boolean value for the given XPath. This version return the boolean value for XPath expressions that
     * evaluate to a node, attribute or a boolean value (e.g. "boolean(.)"). This version returns only the first match.
     * <p>
     * Note: The XPath executed is explicitly deleted. This means you should use this method for XPaths that are executed only
     * once like "./ns:item[@id='" + itemID "']/text()". For XPaths like "./ns:item/text()" you should use the get***Value
     * methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @return The boolean value.
     */
    public static boolean getBooleanValueDynamic(int iNode, String sXPath, XPathMetaInfo xmiPathInfo)
    {
        return getBooleanValueDynamic(iNode, sXPath, xmiPathInfo, false, false);
    }

    /**
     * This method returns the boolean value for the given XPath. This version return the boolean value for XPath expressions that
     * evaluate to a node, attribute or a boolean value (e.g. "boolean(.)").
     * <p>
     * Note: The XPath executed is explicitly deleted. This means you should use this method for XPaths that are executed only
     * once like "./ns:item[@id='" + itemID "']/text()". For XPaths like "./ns:item/text()" you should use the get***Value
     * methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @param bDefault The default value to return if there was nothing found.
     * @return The boolean value.
     */
    public static boolean getBooleanValueDynamic(int iNode, String sXPath, XPathMetaInfo xmiPathInfo, boolean bDefault)
    {
        return getBooleanValueDynamic(iNode, sXPath, xmiPathInfo, false, bDefault);
    }

    /**
     * This method returns the boolean value for the given XPath.
     * <p>
     * Note: The XPath executed is explicitly deleted. This means you should use this method for XPaths that are executed only
     * once like "./ns:item[@id='" + itemID "']/text()". For XPaths like "./ns:item/text()" you should use the get***Value
     * methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param bMandatory Whether or not the field is mandatory.
     * @return The boolean value. If the value is not found and bMandatory is false the default value is false.
     * @throws NOMXPathParseException If the value cannot be found and bMandatory is true.
     */
    public static boolean getBooleanValueDynamic(int iNode, String sXPath, boolean bMandatory) throws NOMXPathParseException
    {
        boolean bReturn = false;

        String sTemp = getStringValueDynamic(iNode, sXPath, bMandatory);

        if (StringUtils.isSet(sTemp))
        {
            bReturn = "true".equalsIgnoreCase(sTemp);
        }
        else if (bMandatory == true)
        {
            throw new NOMXPathParseException("Cannot find xpath " + sXPath + " in the definition.");
        }

        return bReturn;
    }

    /**
     * This method returns the boolean value for the given XPath. This version return the boolean value for XPath expressions that
     * evaluate to a node, attribute or a boolean value (e.g. "number(.)").
     * <p>
     * Note: The XPath executed is explicitly deleted. This means you should use this method for XPaths that are executed only
     * once like "./ns:item[@id='" + itemID "']/text()". For XPaths like "./ns:item/text()" you should use the get***Value
     * methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param prPathInfo Extra XPath information, e.g. namespace mappings
     * @param bAllMatches If <code>false</code> and there are multiple matches, only the first one is returned. Otherwise the
     *            results are concatenated together.
     * @param bDefault The default value to return if there was nothing found.
     * @return The boolean value.
     */
    public static boolean getBooleanValueDynamic(int iNode, String sXPath, XPathMetaInfo prPathInfo, boolean bAllMatches,
            boolean bDefault)
    {
        boolean bReturn = bDefault;

        String sTemp = getStringValueDynamic(iNode, sXPath, prPathInfo, bAllMatches, String.valueOf(bDefault));

        bReturn = Boolean.parseBoolean(sTemp);

        return bReturn;
    }

    /**
     * This method returns the Date value for the given XPath. This version return the Date value for XPath expressions based on
     * its String value. As a prefix resolver it uses the one from the NamespaceConstants class.
     * <p>
     * Note: The XPath executed is not explicitly deleted. This means that it is up to the garbage collector to decide when the
     * XPath object (and thus the native object tree) is deleted. This has no influence on the involved NOM nodes, just the XPath
     * object. This means that you should use this method when having static xpaths like ./ns:item/text(). But when executing
     * dynamic XPaths like "./ns:item[@id='" + itemID "']/text()" you should use the get***ValueDynamic methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @return The Date value.
     */
    public static Date getDateValue(int iNode, String sXPath)
    {
        return getDateValue(iNode, sXPath, NamespaceConstants.getXPathMetaInfo(), false, null);
    }

    /**
     * This method returns the Date value for the given XPath. This version return the Date value for XPath expressions based on
     * its String value. As a prefix resolver it uses the one from the NamespaceConstants class.
     * <p>
     * Note: The XPath executed is not explicitly deleted. This means that it is up to the garbage collector to decide when the
     * XPath object (and thus the native object tree) is deleted. This has no influence on the involved NOM nodes, just the XPath
     * object. This means that you should use this method when having static xpaths like ./ns:item/text(). But when executing
     * dynamic XPaths like "./ns:item[@id='" + itemID "']/text()" you should use the get***ValueDynamic methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @return The Date value.
     */
    public static Date getDateValue(int iNode, String sXPath, XPathMetaInfo xmiPathInfo)
    {
        return getDateValue(iNode, sXPath, xmiPathInfo, false, null);
    }

    /**
     * This method returns the Date value for the given XPath. This version return the Date value for XPath expressions based on
     * its String value. As a prefix resolver it uses the one from the NamespaceConstants class.
     * <p>
     * Note: The XPath executed is not explicitly deleted. This means that it is up to the garbage collector to decide when the
     * XPath object (and thus the native object tree) is deleted. This has no influence on the involved NOM nodes, just the XPath
     * object. This means that you should use this method when having static xpaths like ./ns:item/text(). But when executing
     * dynamic XPaths like "./ns:item[@id='" + itemID "']/text()" you should use the get***ValueDynamic methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param dDefault The default value to return if there was nothing found.
     * @return The Date value.
     */
    public static Date getDateValue(int iNode, String sXPath, Date dDefault)
    {
        return getDateValue(iNode, sXPath, NamespaceConstants.getXPathMetaInfo(), false, dDefault);
    }

    /**
     * This method returns the Date value for the given XPath. This version return the Date value for XPath expressions based on
     * its String value. As a prefix resolver it uses the one from the NamespaceConstants class.
     * <p>
     * Note: The XPath executed is not explicitly deleted. This means that it is up to the garbage collector to decide when the
     * XPath object (and thus the native object tree) is deleted. This has no influence on the involved NOM nodes, just the XPath
     * object. This means that you should use this method when having static xpaths like ./ns:item/text(). But when executing
     * dynamic XPaths like "./ns:item[@id='" + itemID "']/text()" you should use the get***ValueDynamic methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @param bAllMatches If <code>false</code> and there are multiple matches, only the first one is returned. Otherwise the
     *            results are concatenated together.
     * @return The Date value.
     */
    public static Date getDateValue(int iNode, String sPath, XPathMetaInfo xmiPathInfo, boolean bAllMatches)
    {
        return getDateValue(iNode, sPath, xmiPathInfo, bAllMatches, null);
    }

    /**
     * This method returns the Date value for the given XPath. This version return the Date value for XPath expressions based on
     * its String value. As a prefix resolver it uses the one from the NamespaceConstants class.
     * <p>
     * Note: The XPath executed is not explicitly deleted. This means that it is up to the garbage collector to decide when the
     * XPath object (and thus the native object tree) is deleted. This has no influence on the involved NOM nodes, just the XPath
     * object. This means that you should use this method when having static xpaths like ./ns:item/text(). But when executing
     * dynamic XPaths like "./ns:item[@id='" + itemID "']/text()" you should use the get***ValueDynamic methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @param dDefault The default value to return if there was nothing found.
     * @return The Date value.
     */
    public static Date getDateValue(int iNode, String sXPath, XPathMetaInfo xmiPathInfo, Date dDefault)
    {
        return getDateValue(iNode, sXPath, xmiPathInfo, false, dDefault);
    }

    /**
     * This method returns the Date value for the given XPath. This version return the Date value for XPath expressions based on
     * its String value. As a prefix resolver it uses the one from the NamespaceConstants class.
     * <p>
     * Note: The XPath executed is not explicitly deleted. This means that it is up to the garbage collector to decide when the
     * XPath object (and thus the native object tree) is deleted. This has no influence on the involved NOM nodes, just the XPath
     * object. This means that you should use this method when having static xpaths like ./ns:item/text(). But when executing
     * dynamic XPaths like "./ns:item[@id='" + itemID "']/text()" you should use the get***ValueDynamic methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param bMandatory Whether or not the field is mandatory.
     * @return The Date value. If the value is not found and bMandatory is false the default value is false.
     * @throws NOMXPathParseException If the value cannot be found and bMandatory is true.
     */
    public static Date getDateValue(int iNode, String sXPath, boolean bMandatory) throws NOMXPathParseException
    {
        Date dReturn = null;

        String sTemp = getStringValue(iNode, sXPath, bMandatory);

        if ((sTemp != null) && (sTemp.length() != 0))
        {
            dReturn = DateUtil.parseDate(sTemp);
        }

        if ((bMandatory == true) && (dReturn == null))
        {
            throw new NOMXPathParseException("Cannot find xpath " + sXPath + " in the definition.");
        }

        return dReturn;
    }

    /**
     * This method returns the Date value for the given XPath. This version return the Date value for XPath expressions based on
     * its String value. As a prefix resolver it uses the one from the NamespaceConstants class.
     * <p>
     * Note: The XPath executed is not explicitly deleted. This means that it is up to the garbage collector to decide when the
     * XPath object (and thus the native object tree) is deleted. This has no influence on the involved NOM nodes, just the XPath
     * object. This means that you should use this method when having static xpaths like ./ns:item/text(). But when executing
     * dynamic XPaths like "./ns:item[@id='" + itemID "']/text()" you should use the get***ValueDynamic methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param prPathInfo Extra XPath information, e.g. namespace mappings
     * @param bAllMatches If <code>false</code> and there are multiple matches, only the first one is returned. Otherwise the
     *            results are concatenated together.
     * @param dDefault The default value to return if there was nothing found.
     * @return The Date value.
     */
    public static Date getDateValue(int iNode, String sXPath, XPathMetaInfo prPathInfo, boolean bAllMatches, Date dDefault)
    {
        Date dReturn = dDefault;

        String sTemp = getStringValue(iNode, sXPath, prPathInfo, bAllMatches, String.valueOf(dDefault));

        dReturn = DateUtil.parseDate(sTemp);

        return dReturn;
    }

    /**
     * This method returns the Date value for the given XPath. This version return the Date value for XPath expressions based on
     * its String value. As a prefix resolver it uses the one from the NamespaceConstants class.
     * <p>
     * Note: The XPath executed is explicitly deleted. This means you should use this method for XPaths that are executed only
     * once like "./ns:item[@id='" + itemID "']/text()". For XPaths like "./ns:item/text()" you should use the get***Value
     * methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @return The Date value.
     */
    public static Date getDateValueDynamic(int iNode, String sXPath)
    {
        return getDateValueDynamic(iNode, sXPath, NamespaceConstants.getXPathMetaInfo(), false, null);
    }

    /**
     * This method returns the Date value for the given XPath. This version return the Date value for XPath expressions based on
     * its String value. As a prefix resolver it uses the one from the NamespaceConstants class.
     * <p>
     * Note: The XPath executed is explicitly deleted. This means you should use this method for XPaths that are executed only
     * once like "./ns:item[@id='" + itemID "']/text()". For XPaths like "./ns:item/text()" you should use the get***Value
     * methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @return The Date value.
     */
    public static Date getDateValueDynamic(int iNode, String sXPath, XPathMetaInfo xmiPathInfo)
    {
        return getDateValueDynamic(iNode, sXPath, xmiPathInfo, false, null);
    }

    /**
     * This method returns the Date value for the given XPath. This version return the Date value for XPath expressions based on
     * its String value. As a prefix resolver it uses the one from the NamespaceConstants class.
     * <p>
     * Note: The XPath executed is explicitly deleted. This means you should use this method for XPaths that are executed only
     * once like "./ns:item[@id='" + itemID "']/text()". For XPaths like "./ns:item/text()" you should use the get***Value
     * methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param dDefault The default value to return if there was nothing found.
     * @return The Date value.
     */
    public static Date getDateValueDynamic(int iNode, String sXPath, Date dDefault)
    {
        return getDateValueDynamic(iNode, sXPath, NamespaceConstants.getXPathMetaInfo(), false, dDefault);
    }

    /**
     * This method returns the Date value for the given XPath. This version return the Date value for XPath expressions based on
     * its String value. As a prefix resolver it uses the one from the NamespaceConstants class.
     * <p>
     * Note: The XPath executed is explicitly deleted. This means you should use this method for XPaths that are executed only
     * once like "./ns:item[@id='" + itemID "']/text()". For XPaths like "./ns:item/text()" you should use the get***Value
     * methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @param bAllMatches If <code>false</code> and there are multiple matches, only the first one is returned. Otherwise the
     *            results are concatenated together.
     * @return The Date value.
     */
    public static Date getDateValueDynamic(int iNode, String sPath, XPathMetaInfo xmiPathInfo, boolean bAllMatches)
    {
        return getDateValueDynamic(iNode, sPath, xmiPathInfo, bAllMatches, null);
    }

    /**
     * This method returns the Date value for the given XPath. This version return the Date value for XPath expressions based on
     * its String value. As a prefix resolver it uses the one from the NamespaceConstants class.
     * <p>
     * Note: The XPath executed is explicitly deleted. This means you should use this method for XPaths that are executed only
     * once like "./ns:item[@id='" + itemID "']/text()". For XPaths like "./ns:item/text()" you should use the get***Value
     * methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @param dDefault The default value to return if there was nothing found.
     * @return The Date value.
     */
    public static Date getDateValueDynamic(int iNode, String sXPath, XPathMetaInfo xmiPathInfo, Date dDefault)
    {
        return getDateValueDynamic(iNode, sXPath, xmiPathInfo, false, dDefault);
    }

    /**
     * This method returns the Date value for the given XPath. This version return the Date value for XPath expressions based on
     * its String value. As a prefix resolver it uses the one from the NamespaceConstants class.
     * <p>
     * Note: The XPath executed is explicitly deleted. This means you should use this method for XPaths that are executed only
     * once like "./ns:item[@id='" + itemID "']/text()". For XPaths like "./ns:item/text()" you should use the get***Value
     * methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param bMandatory Whether or not the field is mandatory.
     * @return The Date value. If the value is not found and bMandatory is false the default value is false.
     * @throws NOMXPathParseException If the value cannot be found and bMandatory is true.
     */
    public static Date getDateValueDynamic(int iNode, String sXPath, boolean bMandatory) throws NOMXPathParseException
    {
        Date dReturn = null;

        String sTemp = getStringValueDynamic(iNode, sXPath, bMandatory);

        if ((sTemp != null) && (sTemp.length() != 0))
        {
            dReturn = DateUtil.parseDate(sTemp);
        }

        if ((bMandatory == true) && (dReturn == null))
        {
            throw new NOMXPathParseException("Cannot find xpath " + sXPath + " in the definition.");
        }

        return dReturn;
    }

    /**
     * This method returns the Date value for the given XPath. This version return the Date value for XPath expressions based on
     * its String value. As a prefix resolver it uses the one from the NamespaceConstants class.
     * <p>
     * Note: The XPath executed is explicitly deleted. This means you should use this method for XPaths that are executed only
     * once like "./ns:item[@id='" + itemID "']/text()". For XPaths like "./ns:item/text()" you should use the get***Value
     * methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param prPathInfo Extra XPath information, e.g. namespace mappings
     * @param bAllMatches If <code>false</code> and there are multiple matches, only the first one is returned. Otherwise the
     *            results are concatenated together.
     * @param dDefault The default value to return if there was nothing found.
     * @return The Date value.
     */
    public static Date getDateValueDynamic(int iNode, String sXPath, XPathMetaInfo prPathInfo, boolean bAllMatches, Date dDefault)
    {
        Date dReturn = dDefault;

        String sTemp = getStringValueDynamic(iNode, sXPath, prPathInfo, bAllMatches, String.valueOf(dDefault));

        dReturn = DateUtil.parseDate(sTemp);

        return dReturn;
    }

    /**
     * This method returns the int value for the given XPath. This version return the int value for XPath expressions that
     * evaluate to a node, attribute or a int value (e.g. "int(.)"). This version returns only the first match.<br>
     * As a prefix resolver it uses the one from the NamespaceConstants class.
     * <p>
     * Note: The XPath executed is not explicitly deleted. This means that it is up to the garbage collector to decide when the
     * XPath object (and thus the native object tree) is deleted. This has no influence on the involved NOM nodes, just the XPath
     * object. This means that you should use this method when having static xpaths like ./ns:item/text(). But when executing
     * dynamic XPaths like "./ns:item[@id='" + itemID "']/text()" you should use the get***ValueDynamic methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @return The int value.
     */
    public static int getIntegerValue(int iNode, String sXPath)
    {
        return getIntegerValue(iNode, sXPath, NamespaceConstants.getXPathMetaInfo(), false, -1);
    }

    /**
     * This method returns the int value for the given XPath. This version return the int value for XPath expressions that
     * evaluate to a node, attribute or a int value (e.g. "int(.)"). This version returns only the first match.
     * <p>
     * Note: The XPath executed is not explicitly deleted. This means that it is up to the garbage collector to decide when the
     * XPath object (and thus the native object tree) is deleted. This has no influence on the involved NOM nodes, just the XPath
     * object. This means that you should use this method when having static xpaths like ./ns:item/text(). But when executing
     * dynamic XPaths like "./ns:item[@id='" + itemID "']/text()" you should use the get***ValueDynamic methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @return The int value.
     */
    public static int getIntegerValue(int iNode, String sXPath, XPathMetaInfo xmiPathInfo)
    {
        return getIntegerValue(iNode, sXPath, xmiPathInfo, false, -1);
    }

    /**
     * This method returns the int value for the given XPath. This version return the int value for XPath expressions that
     * evaluate to a node, attribute or a int value (e.g. "int(.)").<br>
     * As a prefix resolver it uses the one from the NamespaceConstants class.
     * <p>
     * Note: The XPath executed is not explicitly deleted. This means that it is up to the garbage collector to decide when the
     * XPath object (and thus the native object tree) is deleted. This has no influence on the involved NOM nodes, just the XPath
     * object. This means that you should use this method when having static xpaths like ./ns:item/text(). But when executing
     * dynamic XPaths like "./ns:item[@id='" + itemID "']/text()" you should use the get***ValueDynamic methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param iDefault The default value to return if there was nothing found.
     * @return The int value.
     */
    public static int getIntegerValue(int iNode, String sXPath, int iDefault)
    {
        return getIntegerValue(iNode, sXPath, NamespaceConstants.getXPathMetaInfo(), false, iDefault);
    }

    /**
     * This method returns the int value for the given XPath. This version return the int value for XPath expressions that
     * evaluate to a node, attribute or a int value (e.g. "int(.)").
     * <p>
     * Note: The XPath executed is not explicitly deleted. This means that it is up to the garbage collector to decide when the
     * XPath object (and thus the native object tree) is deleted. This has no influence on the involved NOM nodes, just the XPath
     * object. This means that you should use this method when having static xpaths like ./ns:item/text(). But when executing
     * dynamic XPaths like "./ns:item[@id='" + itemID "']/text()" you should use the get***ValueDynamic methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @param bAllMatches If <code>false</code> and there are multiple matches, only the first one is returned. Otherwise the
     *            results are concatenated together.
     * @return The int value.
     */
    public static int getIntegerValue(int iNode, String sPath, XPathMetaInfo xmiPathInfo, boolean bAllMatches)
    {
        return getIntegerValue(iNode, sPath, xmiPathInfo, bAllMatches, -1);
    }

    /**
     * This method returns the int value for the given XPath. This version return the int value for XPath expressions that
     * evaluate to a node, attribute or a int value (e.g. "int(.)").
     * <p>
     * Note: The XPath executed is not explicitly deleted. This means that it is up to the garbage collector to decide when the
     * XPath object (and thus the native object tree) is deleted. This has no influence on the involved NOM nodes, just the XPath
     * object. This means that you should use this method when having static xpaths like ./ns:item/text(). But when executing
     * dynamic XPaths like "./ns:item[@id='" + itemID "']/text()" you should use the get***ValueDynamic methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @param iDefault The default value to return if there was nothing found.
     * @return The int value.
     */
    public static int getIntegerValue(int iNode, String sXPath, XPathMetaInfo xmiPathInfo, int iDefault)
    {
        return getIntegerValue(iNode, sXPath, xmiPathInfo, false, iDefault);
    }

    /**
     * This method returns the integer value for the given XPath.
     * <p>
     * Note: The XPath executed is not explicitly deleted. This means that it is up to the garbage collector to decide when the
     * XPath object (and thus the native object tree) is deleted. This has no influence on the involved NOM nodes, just the XPath
     * object. This means that you should use this method when having static xpaths like ./ns:item/text(). But when executing
     * dynamic XPaths like "./ns:item[@id='" + itemID "']/text()" you should use the get***ValueDynamic methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param bMandatory Whether or not the field is mandatory.
     * @return The integer value. If the value is not found and bMandatory is false the default value is false.
     * @throws NOMXPathParseException If the value cannot be found and bMandatory is true.
     */
    public static int getIntegerValue(int iNode, String sXPath, boolean bMandatory) throws NOMXPathParseException
    {
        int iReturn = -1;

        String sTemp = getStringValue(iNode, sXPath, bMandatory);

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
            throw new NOMXPathParseException("Cannot find xpath " + sXPath + " in the definition.");
        }

        return iReturn;
    }

    /**
     * This method returns the int value for the given XPath. This version return the int value for XPath expressions that
     * evaluate to a node, attribute or a int value (e.g. "number(.)").
     * <p>
     * Note: The XPath executed is not explicitly deleted. This means that it is up to the garbage collector to decide when the
     * XPath object (and thus the native object tree) is deleted. This has no influence on the involved NOM nodes, just the XPath
     * object. This means that you should use this method when having static xpaths like ./ns:item/text(). But when executing
     * dynamic XPaths like "./ns:item[@id='" + itemID "']/text()" you should use the get***ValueDynamic methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param prPathInfo Extra XPath information, e.g. namespace mappings
     * @param bAllMatches If <code>false</code> and there are multiple matches, only the first one is returned. Otherwise the
     *            results are concatenated together.
     * @param iDefault The default value to return if there was nothing found.
     * @return The int value.
     */
    public static int getIntegerValue(int iNode, String sXPath, XPathMetaInfo prPathInfo, boolean bAllMatches, int iDefault)
    {
        int iReturn = iDefault;

        String sTemp = getStringValue(iNode, sXPath, prPathInfo, bAllMatches, String.valueOf(iDefault));

        iReturn = Integer.parseInt(sTemp);

        return iReturn;
    }

    /**
     * This method returns the int value for the given XPath. This version return the int value for XPath expressions that
     * evaluate to a node, attribute or a int value (e.g. "int(.)"). This version returns only the first match.<br>
     * As a prefix resolver it uses the one from the NamespaceConstants class.
     * <p>
     * Note: The XPath executed is explicitly deleted. This means you should use this method for XPaths that are executed only
     * once like "./ns:item[@id='" + itemID "']/text()". For XPaths like "./ns:item/text()" you should use the get***Value
     * methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @return The int value.
     */
    public static int getIntegerValueDynamic(int iNode, String sXPath)
    {
        return getIntegerValueDynamic(iNode, sXPath, NamespaceConstants.getXPathMetaInfo(), false, -1);
    }

    /**
     * This method returns the int value for the given XPath. This version return the int value for XPath expressions that
     * evaluate to a node, attribute or a int value (e.g. "int(.)"). This version returns only the first match.
     * <p>
     * Note: The XPath executed is explicitly deleted. This means you should use this method for XPaths that are executed only
     * once like "./ns:item[@id='" + itemID "']/text()". For XPaths like "./ns:item/text()" you should use the get***Value
     * methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @return The int value.
     */
    public static int getIntegerValueDynamic(int iNode, String sXPath, XPathMetaInfo xmiPathInfo)
    {
        return getIntegerValueDynamic(iNode, sXPath, xmiPathInfo, false, -1);
    }

    /**
     * This method returns the int value for the given XPath. This version return the int value for XPath expressions that
     * evaluate to a node, attribute or a int value (e.g. "int(.)").<br>
     * As a prefix resolver it uses the one from the NamespaceConstants class.
     * <p>
     * Note: The XPath executed is explicitly deleted. This means you should use this method for XPaths that are executed only
     * once like "./ns:item[@id='" + itemID "']/text()". For XPaths like "./ns:item/text()" you should use the get***Value
     * methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param iDefault The default value to return if there was nothing found.
     * @return The int value.
     */
    public static int getIntegerValueDynamic(int iNode, String sXPath, int iDefault)
    {
        return getIntegerValueDynamic(iNode, sXPath, NamespaceConstants.getXPathMetaInfo(), false, iDefault);
    }

    /**
     * This method returns the int value for the given XPath. This version return the int value for XPath expressions that
     * evaluate to a node, attribute or a int value (e.g. "int(.)").
     * <p>
     * Note: The XPath executed is explicitly deleted. This means you should use this method for XPaths that are executed only
     * once like "./ns:item[@id='" + itemID "']/text()". For XPaths like "./ns:item/text()" you should use the get***Value
     * methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @param bAllMatches If <code>false</code> and there are multiple matches, only the first one is returned. Otherwise the
     *            results are concatenated together.
     * @return The int value.
     */
    public static int getIntegerValueDynamic(int iNode, String sPath, XPathMetaInfo xmiPathInfo, boolean bAllMatches)
    {
        return getIntegerValueDynamic(iNode, sPath, xmiPathInfo, bAllMatches, -1);
    }

    /**
     * This method returns the int value for the given XPath. This version return the int value for XPath expressions that
     * evaluate to a node, attribute or a int value (e.g. "int(.)").
     * <p>
     * Note: The XPath executed is explicitly deleted. This means you should use this method for XPaths that are executed only
     * once like "./ns:item[@id='" + itemID "']/text()". For XPaths like "./ns:item/text()" you should use the get***Value
     * methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @param iDefault The default value to return if there was nothing found.
     * @return The int value.
     */
    public static int getIntegerValueDynamic(int iNode, String sXPath, XPathMetaInfo xmiPathInfo, int iDefault)
    {
        return getIntegerValueDynamic(iNode, sXPath, xmiPathInfo, false, iDefault);
    }

    /**
     * This method returns the integer value for the given XPath.
     * <p>
     * Note: The XPath executed is explicitly deleted. This means you should use this method for XPaths that are executed only
     * once like "./ns:item[@id='" + itemID "']/text()". For XPaths like "./ns:item/text()" you should use the get***Value
     * methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param bMandatory Whether or not the field is mandatory.
     * @return The integer value. If the value is not found and bMandatory is false the default value is false.
     * @throws NOMXPathParseException If the value cannot be found and bMandatory is true.
     */
    public static int getIntegerValueDynamic(int iNode, String sXPath, boolean bMandatory) throws NOMXPathParseException
    {
        int iReturn = -1;

        String sTemp = getStringValueDynamic(iNode, sXPath, bMandatory);

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
            throw new NOMXPathParseException("Cannot find xpath " + sXPath + " in the definition.");
        }

        return iReturn;
    }

    /**
     * This method returns the int value for the given XPath. This version return the int value for XPath expressions that
     * evaluate to a node, attribute or a int value (e.g. "number(.)").
     * <p>
     * Note: The XPath executed is explicitly deleted. This means you should use this method for XPaths that are executed only
     * once like "./ns:item[@id='" + itemID "']/text()". For XPaths like "./ns:item/text()" you should use the get***Value
     * methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param prPathInfo Extra XPath information, e.g. namespace mappings
     * @param bAllMatches If <code>false</code> and there are multiple matches, only the first one is returned. Otherwise the
     *            results are concatenated together.
     * @param iDefault The default value to return if there was nothing found.
     * @return The int value.
     */
    public static int getIntegerValueDynamic(int iNode, String sXPath, XPathMetaInfo prPathInfo, boolean bAllMatches, int iDefault)
    {
        int iReturn = iDefault;

        String sTemp = getStringValueDynamic(iNode, sXPath, prPathInfo, bAllMatches, String.valueOf(iDefault));

        iReturn = Integer.parseInt(sTemp);

        return iReturn;
    }

    /**
     * This method returns the long value for the given XPath. This version return the long value for XPath expressions that
     * evaluate to a node, attribute or a long value (e.g. "long(.)"). This version returns only the first match. <br>
     * As a prefix resolver it uses the one from the NamespaceConstants class.
     * <p>
     * Note: The XPath executed is not explicitly deleted. This means that it is up to the garbage collector to decide when the
     * XPath object (and thus the native object tree) is deleted. This has no influence on the involved NOM nodes, just the XPath
     * object. This means that you should use this method when having static xpaths like ./ns:item/text(). But when executing
     * dynamic XPaths like "./ns:item[@id='" + itemID "']/text()" you should use the get***ValueDynamic methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @return The long value.
     */
    public static long getLongValue(int iNode, String sXPath)
    {
        return getLongValue(iNode, sXPath, NamespaceConstants.getXPathMetaInfo(), false, -1);
    }

    /**
     * This method returns the long value for the given XPath. This version return the long value for XPath expressions that
     * evaluate to a node, attribute or a long value (e.g. "long(.)"). This version returns only the first match.
     * <p>
     * Note: The XPath executed is not explicitly deleted. This means that it is up to the garbage collector to decide when the
     * XPath object (and thus the native object tree) is deleted. This has no influence on the involved NOM nodes, just the XPath
     * object. This means that you should use this method when having static xpaths like ./ns:item/text(). But when executing
     * dynamic XPaths like "./ns:item[@id='" + itemID "']/text()" you should use the get***ValueDynamic methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @return The long value.
     */
    public static long getLongValue(int iNode, String sXPath, XPathMetaInfo xmiPathInfo)
    {
        return getLongValue(iNode, sXPath, xmiPathInfo, false, -1);
    }

    /**
     * This method returns the long value for the given XPath. This version return the long value for XPath expressions that
     * evaluate to a node, attribute or a long value (e.g. "long(.)").<br>
     * As a prefix resolver it uses the one from the NamespaceConstants class.
     * <p>
     * Note: The XPath executed is not explicitly deleted. This means that it is up to the garbage collector to decide when the
     * XPath object (and thus the native object tree) is deleted. This has no influence on the involved NOM nodes, just the XPath
     * object. This means that you should use this method when having static xpaths like ./ns:item/text(). But when executing
     * dynamic XPaths like "./ns:item[@id='" + itemID "']/text()" you should use the get***ValueDynamic methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param iDefault The default value to return if there was nothing found.
     * @return The long value.
     */
    public static long getLongValue(int iNode, String sXPath, long iDefault)
    {
        return getLongValue(iNode, sXPath, NamespaceConstants.getXPathMetaInfo(), false, iDefault);
    }

    /**
     * This method returns the long value for the given XPath. This version return the long value for XPath expressions that
     * evaluate to a node, attribute or a long value (e.g. "long(.)").
     * <p>
     * Note: The XPath executed is not explicitly deleted. This means that it is up to the garbage collector to decide when the
     * XPath object (and thus the native object tree) is deleted. This has no influence on the involved NOM nodes, just the XPath
     * object. This means that you should use this method when having static xpaths like ./ns:item/text(). But when executing
     * dynamic XPaths like "./ns:item[@id='" + itemID "']/text()" you should use the get***ValueDynamic methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @param bAllMatches If <code>false</code> and there are multiple matches, only the first one is returned. Otherwise the
     *            results are concatenated together.
     * @return The long value.
     */
    public static long getLongValue(int iNode, String sPath, XPathMetaInfo xmiPathInfo, boolean bAllMatches)
    {
        return getLongValue(iNode, sPath, xmiPathInfo, bAllMatches, -1);
    }

    /**
     * This method returns the long value for the given XPath. This version return the long value for XPath expressions that
     * evaluate to a node, attribute or a long value (e.g. "long(.)").
     * <p>
     * Note: The XPath executed is not explicitly deleted. This means that it is up to the garbage collector to decide when the
     * XPath object (and thus the native object tree) is deleted. This has no influence on the involved NOM nodes, just the XPath
     * object. This means that you should use this method when having static xpaths like ./ns:item/text(). But when executing
     * dynamic XPaths like "./ns:item[@id='" + itemID "']/text()" you should use the get***ValueDynamic methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @param iDefault The default value to return if there was nothing found.
     * @return The long value.
     */
    public static long getLongValue(int iNode, String sXPath, XPathMetaInfo xmiPathInfo, long iDefault)
    {
        return getLongValue(iNode, sXPath, xmiPathInfo, false, iDefault);
    }

    /**
     * This method returns the long value for the given XPath.
     * <p>
     * Note: The XPath executed is not explicitly deleted. This means that it is up to the garbage collector to decide when the
     * XPath object (and thus the native object tree) is deleted. This has no influence on the involved NOM nodes, just the XPath
     * object. This means that you should use this method when having static xpaths like ./ns:item/text(). But when executing
     * dynamic XPaths like "./ns:item[@id='" + itemID "']/text()" you should use the get***ValueDynamic methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param bMandatory Whether or not the field is mandatory.
     * @return The long value. If the value is not found and bMandatory is false the default value is false.
     * @throws NOMXPathParseException In case mandatory is true and the XPath was not found.
     */
    public static long getLongValue(int iNode, String sXPath, boolean bMandatory) throws NOMXPathParseException
    {
        long lReturn = -1;

        String sTemp = getStringValue(iNode, sXPath, bMandatory);

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
            throw new NOMXPathParseException("Cannot find xpath " + sXPath + " in the definition.");
        }

        return lReturn;
    }

    /**
     * This method returns the long value for the given XPath. This version return the long value for XPath expressions that
     * evaluate to a node, attribute or a long value (e.g. "number(.)").
     * <p>
     * Note: The XPath executed is not explicitly deleted. This means that it is up to the garbage collector to decide when the
     * XPath object (and thus the native object tree) is deleted. This has no influence on the involved NOM nodes, just the XPath
     * object. This means that you should use this method when having static xpaths like ./ns:item/text(). But when executing
     * dynamic XPaths like "./ns:item[@id='" + itemID "']/text()" you should use the get***ValueDynamic methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param prPathInfo Extra XPath information, e.g. namespace mappings
     * @param bAllMatches If <code>false</code> and there are multiple matches, only the first one is returned. Otherwise the
     *            results are concatenated together.
     * @param iDefault The default value to return if there was nothing found.
     * @return The long value.
     */
    public static long getLongValue(int iNode, String sXPath, XPathMetaInfo prPathInfo, boolean bAllMatches, long iDefault)
    {
        long lReturn = iDefault;

        String sTemp = getStringValue(iNode, sXPath, prPathInfo, bAllMatches, String.valueOf(iDefault));

        lReturn = Long.parseLong(sTemp);

        return lReturn;
    }

    /**
     * This method returns the long value for the given XPath. This version return the long value for XPath expressions that
     * evaluate to a node, attribute or a long value (e.g. "long(.)"). This version returns only the first match. <br>
     * As a prefix resolver it uses the one from the NamespaceConstants class.
     * <p>
     * Note: The XPath executed is explicitly deleted. This means you should use this method for XPaths that are executed only
     * once like "./ns:item[@id='" + itemID "']/text()". For XPaths like "./ns:item/text()" you should use the get***Value
     * methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @return The long value.
     */
    public static long getLongValueDynamic(int iNode, String sXPath)
    {
        return getLongValueDynamic(iNode, sXPath, NamespaceConstants.getXPathMetaInfo(), false, -1);
    }

    /**
     * This method returns the long value for the given XPath. This version return the long value for XPath expressions that
     * evaluate to a node, attribute or a long value (e.g. "long(.)"). This version returns only the first match.
     * <p>
     * Note: The XPath executed is explicitly deleted. This means you should use this method for XPaths that are executed only
     * once like "./ns:item[@id='" + itemID "']/text()". For XPaths like "./ns:item/text()" you should use the get***Value
     * methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @return The long value.
     */
    public static long getLongValueDynamic(int iNode, String sXPath, XPathMetaInfo xmiPathInfo)
    {
        return getLongValueDynamic(iNode, sXPath, xmiPathInfo, false, -1);
    }

    /**
     * This method returns the long value for the given XPath. This version return the long value for XPath expressions that
     * evaluate to a node, attribute or a long value (e.g. "long(.)").<br>
     * As a prefix resolver it uses the one from the NamespaceConstants class.
     * <p>
     * Note: The XPath executed is explicitly deleted. This means you should use this method for XPaths that are executed only
     * once like "./ns:item[@id='" + itemID "']/text()". For XPaths like "./ns:item/text()" you should use the get***Value
     * methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param iDefault The default value to return if there was nothing found.
     * @return The long value.
     */
    public static long getLongValueDynamic(int iNode, String sXPath, long iDefault)
    {
        return getLongValueDynamic(iNode, sXPath, NamespaceConstants.getXPathMetaInfo(), false, iDefault);
    }

    /**
     * This method returns the long value for the given XPath. This version return the long value for XPath expressions that
     * evaluate to a node, attribute or a long value (e.g. "long(.)").
     * <p>
     * Note: The XPath executed is explicitly deleted. This means you should use this method for XPaths that are executed only
     * once like "./ns:item[@id='" + itemID "']/text()". For XPaths like "./ns:item/text()" you should use the get***Value
     * methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @param bAllMatches If <code>false</code> and there are multiple matches, only the first one is returned. Otherwise the
     *            results are concatenated together.
     * @return The long value.
     */
    public static long getLongValueDynamic(int iNode, String sPath, XPathMetaInfo xmiPathInfo, boolean bAllMatches)
    {
        return getLongValueDynamic(iNode, sPath, xmiPathInfo, bAllMatches, -1);
    }

    /**
     * This method returns the long value for the given XPath. This version return the long value for XPath expressions that
     * evaluate to a node, attribute or a long value (e.g. "long(.)").
     * <p>
     * Note: The XPath executed is explicitly deleted. This means you should use this method for XPaths that are executed only
     * once like "./ns:item[@id='" + itemID "']/text()". For XPaths like "./ns:item/text()" you should use the get***Value
     * methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @param iDefault The default value to return if there was nothing found.
     * @return The long value.
     */
    public static long getLongValueDynamic(int iNode, String sXPath, XPathMetaInfo xmiPathInfo, long iDefault)
    {
        return getLongValueDynamic(iNode, sXPath, xmiPathInfo, false, iDefault);
    }

    /**
     * This method returns the long value for the given XPath.
     * <p>
     * Note: The XPath executed is explicitly deleted. This means you should use this method for XPaths that are executed only
     * once like "./ns:item[@id='" + itemID "']/text()". For XPaths like "./ns:item/text()" you should use the get***Value
     * methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param bMandatory Whether or not the field is mandatory.
     * @return The long value. If the value is not found and bMandatory is false the default value is false.
     * @throws NOMXPathParseException In case mandatory is true and the XPath was not found.
     */
    public static long getLongValueDynamic(int iNode, String sXPath, boolean bMandatory) throws NOMXPathParseException
    {
        long lReturn = -1;

        String sTemp = getStringValueDynamic(iNode, sXPath, bMandatory);

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
            throw new NOMXPathParseException("Cannot find xpath " + sXPath + " in the definition.");
        }

        return lReturn;
    }

    /**
     * This method returns the long value for the given XPath. This version return the long value for XPath expressions that
     * evaluate to a node, attribute or a long value (e.g. "number(.)").
     * <p>
     * Note: The XPath executed is explicitly deleted. This means you should use this method for XPaths that are executed only
     * once like "./ns:item[@id='" + itemID "']/text()". For XPaths like "./ns:item/text()" you should use the get***Value
     * methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param prPathInfo Extra XPath information, e.g. namespace mappings
     * @param bAllMatches If <code>false</code> and there are multiple matches, only the first one is returned. Otherwise the
     *            results are concatenated together.
     * @param iDefault The default value to return if there was nothing found.
     * @return The long value.
     */
    public static long getLongValueDynamic(int iNode, String sXPath, XPathMetaInfo prPathInfo, boolean bAllMatches, long iDefault)
    {
        long lReturn = iDefault;

        String sTemp = getStringValueDynamic(iNode, sXPath, prPathInfo, bAllMatches, String.valueOf(iDefault));

        lReturn = Long.parseLong(sTemp);

        return lReturn;
    }

    /**
     * This method returns the string value for the given XPath. This version return the string value for XPath expressions that
     * evaluate to a node, attribute or a string value (e.g. "string(.)"). This version returns only the first match.<br>
     * As a prefix resolver it uses the one from the NamespaceConstants class.
     * <p>
     * Note: The XPath executed is not explicitly deleted. This means that it is up to the garbage collector to decide when the
     * XPath object (and thus the native object tree) is deleted. This has no influence on the involved NOM nodes, just the XPath
     * object. This means that you should use this method when having static xpaths like ./ns:item/text(). But when executing
     * dynamic XPaths like "./ns:item[@id='" + itemID "']/text()" you should use the get***ValueDynamic methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @return The string value.
     */
    public static String getStringValue(int iNode, String sXPath)
    {
        return getStringValue(iNode, XPath.getXPathInstance(sXPath), NamespaceConstants.getXPathMetaInfo(), false, null);
    }

    /**
     * This method returns the string value for the given XPath. This version return the string value for XPath expressions that
     * evaluate to a node, attribute or a string value (e.g. "string(.)"). This version returns only the first match.
     * <p>
     * Note: The XPath executed is not explicitly deleted. This means that it is up to the garbage collector to decide when the
     * XPath object (and thus the native object tree) is deleted. This has no influence on the involved NOM nodes, just the XPath
     * object. This means that you should use this method when having static xpaths like ./ns:item/text(). But when executing
     * dynamic XPaths like "./ns:item[@id='" + itemID "']/text()" you should use the get***ValueDynamic methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @return The string value.
     */
    public static String getStringValue(int iNode, String sXPath, XPathMetaInfo xmiPathInfo)
    {
        return getStringValue(iNode, XPath.getXPathInstance(sXPath), xmiPathInfo, false, null);
    }

    /**
     * This method returns the string value for the given XPath. This version return the string value for XPath expressions that
     * evaluate to a node, attribute or a string value (e.g. "string(.)").<br>
     * As a prefix resolver it uses the one from the NamespaceConstants class.
     * <p>
     * Note: The XPath executed is not explicitly deleted. This means that it is up to the garbage collector to decide when the
     * XPath object (and thus the native object tree) is deleted. This has no influence on the involved NOM nodes, just the XPath
     * object. This means that you should use this method when having static xpaths like ./ns:item/text(). But when executing
     * dynamic XPaths like "./ns:item[@id='" + itemID "']/text()" you should use the get***ValueDynamic methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param sDefault The default value to return if there was nothing found.
     * @return The string value.
     */
    public static String getStringValue(int iNode, String sXPath, String sDefault)
    {
        return getStringValue(iNode, XPath.getXPathInstance(sXPath), NamespaceConstants.getXPathMetaInfo(), false, sDefault);
    }

    /**
     * This method returns the string value for the given XPath. This version return the string value for XPath expressions that
     * evaluate to a node, attribute or a string value (e.g. "string(.)").
     * <p>
     * Note: The XPath executed is not explicitly deleted. This means that it is up to the garbage collector to decide when the
     * XPath object (and thus the native object tree) is deleted. This has no influence on the involved NOM nodes, just the XPath
     * object. This means that you should use this method when having static xpaths like ./ns:item/text(). But when executing
     * dynamic XPaths like "./ns:item[@id='" + itemID "']/text()" you should use the get***ValueDynamic methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @param bAllMatches If <code>false</code> and there are multiple matches, only the first one is returned. Otherwise the
     *            results are concatenated together.
     * @return The string value.
     */
    public static String getStringValue(int iNode, String sXPath, XPathMetaInfo xmiPathInfo, boolean bAllMatches)
    {
        return getStringValue(iNode, XPath.getXPathInstance(sXPath), xmiPathInfo, bAllMatches, null);
    }

    /**
     * This method returns the string value for the given XPath. This version return the string value for XPath expressions that
     * evaluate to a node, attribute or a string value (e.g. "string(.)").
     * <p>
     * Note: The XPath executed is not explicitly deleted. This means that it is up to the garbage collector to decide when the
     * XPath object (and thus the native object tree) is deleted. This has no influence on the involved NOM nodes, just the XPath
     * object. This means that you should use this method when having static xpaths like ./ns:item/text(). But when executing
     * dynamic XPaths like "./ns:item[@id='" + itemID "']/text()" you should use the get***ValueDynamic methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @param sDefault The default value to return if there was nothing found.
     * @return The string value.
     */
    public static String getStringValue(int iNode, String sXPath, XPathMetaInfo xmiPathInfo, String sDefault)
    {
        return getStringValue(iNode, XPath.getXPathInstance(sXPath), xmiPathInfo, false, sDefault);
    }

    /**
     * This method returns the string value for the given XPath. This version return the string value for XPath expressions that
     * evaluate to a node, attribute or a string value (e.g. "string(.)").
     * <p>
     * Note: The XPath executed is not explicitly deleted. This means that it is up to the garbage collector to decide when the
     * XPath object (and thus the native object tree) is deleted. This has no influence on the involved NOM nodes, just the XPath
     * object. This means that you should use this method when having static xpaths like ./ns:item/text(). But when executing
     * dynamic XPaths like "./ns:item[@id='" + itemID "']/text()" you should use the get***ValueDynamic methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @param bAllMatches If <code>false</code> and there are multiple matches, only the first one is returned. Otherwise the
     *            results are concatenated together.
     * @param sDefault The default value to return if there was nothing found.
     * @return The string value.
     */
    public static String getStringValue(int iNode, String sXPath, XPathMetaInfo xmiPathInfo, boolean bAllMatches, String sDefault)
    {
        return getStringValue(iNode, XPath.getXPathInstance(sXPath), xmiPathInfo, bAllMatches, sDefault);
    }

    /**
     * This method returns the string value for the given XPath. This version return the string value for XPath expressions that
     * evaluate to a node, attribute or a string value (e.g. "string(.)"). This version returns only the first match.<br>
     * As a prefix resolver it uses the one from the NamespaceConstants class.
     * <p>
     * Note: The XPath executed is explicitly deleted. This means you should use this method for XPaths that are executed only
     * once like "./ns:item[@id='" + itemID "']/text()". For XPaths like "./ns:item/text()" you should use the get***Value
     * methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @return The string value.
     */
    public static String getStringValueDynamic(int iNode, String sXPath)
    {
        return getStringValueDynamic(iNode, sXPath, NamespaceConstants.getXPathMetaInfo(), false, null);
    }

    /**
     * This method returns the string value for the given XPath. This version return the string value for XPath expressions that
     * evaluate to a node, attribute or a string value (e.g. "string(.)"). This version returns only the first match.
     * <p>
     * Note: The XPath executed is explicitly deleted. This means you should use this method for XPaths that are executed only
     * once like "./ns:item[@id='" + itemID "']/text()". For XPaths like "./ns:item/text()" you should use the get***Value
     * methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @return The string value.
     */
    public static String getStringValueDynamic(int iNode, String sXPath, XPathMetaInfo xmiPathInfo)
    {
        return getStringValueDynamic(iNode, sXPath, xmiPathInfo, false, null);
    }

    /**
     * This method returns the string value for the given XPath. This version return the string value for XPath expressions that
     * evaluate to a node, attribute or a string value (e.g. "string(.)").<br>
     * As a prefix resolver it uses the one from the NamespaceConstants class.
     * <p>
     * Note: The XPath executed is explicitly deleted. This means you should use this method for XPaths that are executed only
     * once like "./ns:item[@id='" + itemID "']/text()". For XPaths like "./ns:item/text()" you should use the get***Value
     * methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param sDefault The default value to return if there was nothing found.
     * @return The string value.
     */
    public static String getStringValueDynamic(int iNode, String sXPath, String sDefault)
    {
        return getStringValueDynamic(iNode, sXPath, NamespaceConstants.getXPathMetaInfo(), false, sDefault);
    }

    /**
     * This method returns the string value for the given XPath. This version return the string value for XPath expressions that
     * evaluate to a node, attribute or a string value (e.g. "string(.)").
     * <p>
     * Note: The XPath executed is explicitly deleted. This means you should use this method for XPaths that are executed only
     * once like "./ns:item[@id='" + itemID "']/text()". For XPaths like "./ns:item/text()" you should use the get***Value
     * methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @param bAllMatches If <code>false</code> and there are multiple matches, only the first one is returned. Otherwise the
     *            results are concatenated together.
     * @return The string value.
     */
    public static String getStringValueDynamic(int iNode, String sXPath, XPathMetaInfo xmiPathInfo, boolean bAllMatches)
    {
        return getStringValueDynamic(iNode, sXPath, xmiPathInfo, bAllMatches, null);
    }

    /**
     * This method returns the string value for the given XPath. This version return the string value for XPath expressions that
     * evaluate to a node, attribute or a string value (e.g. "string(.)").
     * <p>
     * Note: The XPath executed is explicitly deleted. This means you should use this method for XPaths that are executed only
     * once like "./ns:item[@id='" + itemID "']/text()". For XPaths like "./ns:item/text()" you should use the get***Value
     * methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @param sDefault The default value to return if there was nothing found.
     * @return The string value.
     */
    public static String getStringValueDynamic(int iNode, String sXPath, XPathMetaInfo xmiPathInfo, String sDefault)
    {
        return getStringValueDynamic(iNode, sXPath, xmiPathInfo, false, sDefault);
    }

    /**
     * This method returns the string value for the given XPath. This version return the string value for XPath expressions that
     * evaluate to a node, attribute or a string value (e.g. "string(.)").
     * <p>
     * Note: The XPath executed is explicitly deleted. This means you should use this method for XPaths that are executed only
     * once like "./ns:item[@id='" + itemID "']/text()". For XPaths like "./ns:item/text()" you should use the get***Value
     * methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @param bAllMatches If <code>false</code> and there are multiple matches, only the first one is returned. Otherwise the
     *            results are concatenated together.
     * @param sDefault The default value to return if there was nothing found.
     * @return The string value.
     */
    public static String getStringValueDynamic(int iNode, String sXPath, XPathMetaInfo xmiPathInfo, boolean bAllMatches,
            String sDefault)
    {
        XPath xpath = XPath.getXPathInstance(sXPath);

        try
        {
            return getStringValue(iNode, xpath, xmiPathInfo, bAllMatches, sDefault);
        }
        finally
        {
            xpath.delete();
        }
    }

    /**
     * This method returns the string value for the given XPath.
     * <p>
     * Note: The XPath executed is explicitly deleted. This means you should use this method for XPaths that are executed only
     * once like "./ns:item[@id='" + itemID "']/text()". For XPaths like "./ns:item/text()" you should use the get***Value
     * methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param bMandatory Whether or not the field is mandatory.
     * @return The string value.
     * @throws NOMXPathParseException If the value cannot be found and bMandatory is true.
     */
    public static String getStringValueDynamic(int iNode, String sXPath, boolean bMandatory) throws NOMXPathParseException
    {
        XPath xpath = XPath.getXPathInstance(sXPath);

        try
        {
            return getStringValueMandatory(iNode, xpath, bMandatory);
        }
        finally
        {
            xpath.delete();
        }
    }

    /**
     * This method returns the string value for the given XPath.
     * <p>
     * Note: The XPath executed is not explicitly deleted. This means that it is up to the garbage collector to decide when the
     * XPath object (and thus the native object tree) is deleted. This has no influence on the involved NOM nodes, just the XPath
     * object. This means that you should use this method when having static xpaths like ./ns:item/text(). But when executing
     * dynamic XPaths like "./ns:item[@id='" + itemID "']/text()" you should use the get***ValueDynamic methods.
     * </p>
     * 
     * @param iNode The node to operate on.
     * @param sXPath The XPath to execute.
     * @param bMandatory Whether or not the field is mandatory.
     * @return The string value.
     * @throws NOMXPathParseException If the value cannot be found and bMandatory is true.
     */
    public static String getStringValue(int iNode, String sXPath, boolean bMandatory) throws NOMXPathParseException
    {
        return getStringValueMandatory(iNode, XPath.getXPathInstance(sXPath), bMandatory);
    }

    /**
     * This method returns the string value for the given XPath.
     * 
     * @param iNode The node to operate on.
     * @param xpath sXPath The XPath to execute.
     * @param bMandatory Whether or not the field is mandatory.
     * @return The string value.
     * @throws NOMXPathParseException If the value cannot be found and bMandatory is true.
     */
    private static String getStringValueMandatory(int iNode, XPath xpath, boolean bMandatory) throws NOMXPathParseException
    {
        String sReturn = null;

        NodeSet nsResult = null;

        try
        {
            nsResult = xpath.selectNodeSet(iNode, NamespaceConstants.getXPathMetaInfo());

            if (nsResult.hasNext())
            {
                long lResult = nsResult.next();

                if (ResultNode.isAttribute(lResult))
                {
                    sReturn = ResultNode.getStringValue(lResult);
                }
                else
                {
                    int iResNode = ResultNode.getElementNode(lResult);

                    if ((Node.getType(iResNode) == NodeType.CDATA) || (Node.getType(iResNode) == NodeType.DATA))
                    {
                        sReturn = Node.getData(iResNode);
                    }
                    else
                    {
                        sReturn = Node.getDataWithDefault(iResNode, null);
                    }
                }
            }

            if ((bMandatory == true) && ((sReturn == null) || (sReturn.length() == 0)))
            {
                throw new NOMXPathParseException("Cannot find xpath " + xpath.getSourceExpression() + " in the definition.");
            }

            return sReturn;
        }
        finally
        {
            if (nsResult != null)
            {
                nsResult.delete();
            }
        }
    }

    /**
     * This method returns the string value for the given XPath. This version return the string value for XPath expressions that
     * evaluate to a node, attribute or a string value (e.g. "string(.)").<br>
     * As a prefix resolver it uses the one from the NamespaceConstants class.
     * 
     * @param iNode The node to operate on.
     * @param xPath The XPath to execute.
     * @param bAllMatches If <code>false</code> and there are multiple matches, only the first one is returned. Otherwise the
     *            results are concatenated together.
     * @return The string value.
     */
    public static String getStringValue(int iNode, XPath xPath, boolean bAllMatches)
    {
        return getStringValue(iNode, xPath, NamespaceConstants.getXPathMetaInfo(), bAllMatches, null);
    }

    /**
     * This method returns the string value for the given XPath. This version return the string value for XPath expressions that
     * evaluate to a node, attribute or a string value (e.g. "string(.)").
     * 
     * @param iNode The node to operate on.
     * @param xPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @param bAllMatches If <code>false</code> and there are multiple matches, only the first one is returned. Otherwise the
     *            results are concatenated together.
     * @return The string value.
     */
    public static String getStringValue(int iNode, XPath xPath, XPathMetaInfo xmiPathInfo, boolean bAllMatches)
    {
        return getStringValue(iNode, xPath, xmiPathInfo, bAllMatches, null);
    }

    /**
     * This method returns the string value for the given XPath. This version return the string value for XPath expressions that
     * evaluate to a node, attribute or a string value (e.g. "string(.)"). This version returns only the first match.<br>
     * As a prefix resolver it uses the one from the NamespaceConstants class.
     * 
     * @param iNode The node to operate on.
     * @param xPath The XPath to execute.
     * @return The string value.
     */
    public static String getStringValue(int iNode, XPath xPath)
    {
        return getStringValue(iNode, xPath, NamespaceConstants.getXPathMetaInfo(), false, null);
    }

    /**
     * This method returns the string value for the given XPath. This version return the string value for XPath expressions that
     * evaluate to a node, attribute or a string value (e.g. "string(.)"). This version returns only the first match.
     * 
     * @param iNode The node to operate on.
     * @param xPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @return The string value.
     */
    public static String getStringValue(int iNode, XPath xPath, XPathMetaInfo xmiPathInfo)
    {
        return getStringValue(iNode, xPath, xmiPathInfo, false, null);
    }

    /**
     * This method returns the string value for the given XPath. This version return the string value for XPath expressions that
     * evaluate to a node, attribute or a string value (e.g. "string(.)").
     * 
     * @param iNode The node to operate on.
     * @param xPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @param bAllMatches If <code>false</code> and there are multiple matches, only the first one is returned. Otherwise the
     *            results are concatenated together.
     * @param sDefault The default value to return if there was nothing found.
     * @return The string value.
     */
    public static String getStringValue(int iNode, XPath xPath, XPathMetaInfo xmiPathInfo, boolean bAllMatches, String sDefault)
    {
        String sReturn = sDefault;

        XPathResult xrResult = xPath.evaluate(iNode, xmiPathInfo);
        NodeSet nsResult = null;

        try
        {
            int iType = xrResult.getType();

            switch (iType)
            {
                case XPathResult.XPATH_BOOLEAN:
                    sReturn = Boolean.toString(xrResult.getBooleanResult());

                    break;

                case XPathResult.XPATH_NUMBER:
                    sReturn = Double.toString(xrResult.getNumberResult());

                    break;

                case XPathResult.XPATH_STRING:
                    sReturn = xrResult.getStringResult();

                    break;

                case XPathResult.XPATH_NODESET:

                    nsResult = xrResult.removeNodeSetFromResult();

                    StringBuilder sb = null;

                    while (nsResult.hasNext())
                    {
                        long lResult = nsResult.next();
                        String str = null;

                        if (ResultNode.isAttribute(lResult))
                        {
                            str = ResultNode.getStringValue(lResult);
                        }
                        else
                        {
                            int iResNode = ResultNode.getElementNode(lResult);

                            if ((Node.getType(iResNode) == NodeType.CDATA) || (Node.getType(iResNode) == NodeType.DATA))
                            {
                                if (!bAllMatches)
                                {
                                    // Use the parent node because there can be multiple
                                    // text elements one node (e.g. in case of <a>x&b</a>.
                                    // Usually we are interested in the whole text, not
                                    // just parts of it.
                                    iResNode = Node.getParent(iResNode);
                                }
                                else
                                {
                                    // We are getting all the matching text nodes, so
                                    // they will be concatenated anyway.
                                    str = Node.getData(iResNode);
                                }
                            }

                            if (iResNode != 0)
                            {
                                str = Node.getDataWithDefault(iResNode, null);
                            }
                        }

                        if (str != null)
                        {
                            if (!bAllMatches)
                            {
                                return str;
                            }

                            if (sb == null)
                            {
                                sb = new StringBuilder();
                            }

                            sb.append(str);
                        }
                    }

                    if (sb != null)
                    {
                        sReturn = sb.toString();
                    }

                    break;

                default:
                    throw new IllegalArgumentException("Invalid XPath result type: " + iType);
            }

            return sReturn;
        }
        finally
        {
            if (nsResult != null)
            {
                nsResult.delete();
            }
        }
    }

    /**
     * Uses the XPath.evaluate method to get the XPath result as a string. All kind of matches are supported by this method (e.g.
     * boolean, number, string or node matches). For node matches, the node text of the matching node is returned.
     * 
     * @param node Match root node.
     * @param xpath XPath to be evaluated.
     * @param metainfo Optional XPath metainfo. Can be <code>null</code>.
     * @return Evaluation result.
     */
    public static String evaluateToString(int node, XPath xpath, XPathMetaInfo metainfo)
    {
        XPathResult xpathResult = xpath.evaluate(node, metainfo);
        int iType = xpathResult.getType();

        switch (iType)
        {
            case XPathResult.XPATH_BOOLEAN:
                return Boolean.toString(xpathResult.getBooleanResult());

            case XPathResult.XPATH_NUMBER:
                return formatAsString(xpathResult.getNumberResult());

            case XPathResult.XPATH_STRING:
                return xpathResult.getStringResult();

            case XPathResult.XPATH_NODESET:

                NodeSet resultNodeSet = xpathResult.removeNodeSetFromResult();
                try
                {
                    // Iterate over the the result set until we get a non-null result.
                    // This is needed because otherwise //node and //node/text() would
                    // return different results.
                    while (resultNodeSet.hasNext())
                    {
                        String str = getNextNodeSetMatch(resultNodeSet);

                        if (str != null)
                        {
                            return str;
                        }
                    }
                }
                finally
                {
                    resultNodeSet.delete();
                }

                return null;

            default:
                throw new IllegalArgumentException("Invalid XPath result type: " + iType);
        }
    }

    /**
     * Uses the XPath.evaluate method to get the XPath result as a string. All kind of matches are supported by this method (e.g.
     * boolean, number, string or node matches). For node matches, the node text is returned. Note that non-nodeset results return
     * an array of one string.
     * 
     * @param node Match root node.
     * @param xpath XPath to be evaluated.
     * @param metainfo Optional XPath metainfo. Can be <code>null</code>.
     * @return An array containing all the matches.
     */
    public static String[] evaluateToStringArray(int node, XPath xpath, XPathMetaInfo metainfo)
    {
        XPathResult xpathResult = xpath.evaluate(node, metainfo);

        int iType = xpathResult.getType();

        switch (iType)
        {
            case XPathResult.XPATH_BOOLEAN:
                return new String[] { Boolean.toString(xpathResult.getBooleanResult()) };

            case XPathResult.XPATH_NUMBER:
                return new String[] { formatAsString(xpathResult.getNumberResult()) };

            case XPathResult.XPATH_STRING:
                return new String[] { xpathResult.getStringResult() };

            case XPathResult.XPATH_NODESET:

                NodeSet resultNodeSet = xpathResult.removeNodeSetFromResult();
                try
                {
                    List<String> res = new ArrayList<String>(16);

                    while (resultNodeSet.hasNext())
                    {
                        String str = getNextNodeSetMatch(resultNodeSet);

                        if (str != null)
                        {
                            res.add(str);
                        }
                    }

                    return (String[]) res.toArray(new String[res.size()]);
                }
                finally
                {
                    resultNodeSet.delete();
                }

            default:
                throw new IllegalArgumentException("Invalid XPath result type: " + iType);
        }
    }

    /**
     * Returns the next match from the nodeset as a string. For node matches, the node text is returned.
     * 
     * @param resultNodeSet Node set to be evaluated.
     * @return Next match as a string or <code>null</code> if no more matches are found from the node set.
     */
    public static String getNextNodeSetMatch(NodeSet resultNodeSet)
    {
        if (!resultNodeSet.hasNext())
        {
            return null;
        }

        long lResult = resultNodeSet.next();
        String str = null;

        if (ResultNode.isAttribute(lResult))
        {
            str = ResultNode.getStringValue(lResult);
        }
        else
        {
            int iResNode = ResultNode.getElementNode(lResult);

            if ((Node.getType(iResNode) == NodeType.CDATA) || (Node.getType(iResNode) == NodeType.DATA))
            {
                // Use the parent node because there can be multiple
                // text elements in one node (e.g. in case of <a>x&b</a>).
                // Usually we are interested in the whole text, not
                // just parts of it.
                iResNode = Node.getParent(iResNode);
            }

            if (iResNode != 0)
            {
                str = Node.getDataWithDefault(iResNode, null);
            }
        }

        return str;
    }

    /**
     * This method returns all the nodes that match the given XPath. Note: This method ONLY returns element nodes. Element nodes
     * include text nodes.
     * <p>
     * Note: The XPath executed is not explicitly deleted. This means that it is up to the garbage collector to decide when the
     * XPath object (and thus the native object tree) is deleted. This has no influence on the involved NOM nodes, just the XPath
     * object. This means that you should use this method when having static xpaths like ./ns:item/text(). But when executing
     * dynamic XPaths like "./ns:item[@id='" + itemID "']/text()" you should use the get***ValueDynamic methods.
     * </p>
     * 
     * @param iXML The source XML.
     * @param sXPath The XPath to execute.
     * @return The result node or 0 if there was no match.
     */
    public static int[] selectNodes(int iXML, String sXPath)
    {
        return selectNodes(iXML, XPath.getXPathInstance(sXPath), NamespaceConstants.getXPathMetaInfo());
    }

    /**
     * This method returns all the nodes that match the given XPath. Note: This method ONLY returns element nodes. Element nodes
     * include text nodes.
     * <p>
     * Note: The XPath executed is not explicitly deleted. This means that it is up to the garbage collector to decide when the
     * XPath object (and thus the native object tree) is deleted. This has no influence on the involved NOM nodes, just the XPath
     * object. This means that you should use this method when having static xpaths like ./ns:item/text(). But when executing
     * dynamic XPaths like "./ns:item[@id='" + itemID "']/text()" you should use the get***ValueDynamic methods.
     * </p>
     * 
     * @param iXML The source XML.
     * @param sXPath The XPath to execute.
     * @param xmiMetaInfo The prefix binding information.
     * @return The result node or 0 if there was no match.
     */
    public static int[] selectNodes(int iXML, String sXPath, XPathMetaInfo xmiMetaInfo)
    {
        return selectNodes(iXML, XPath.getXPathInstance(sXPath), xmiMetaInfo);
    }

    /**
     * This method returns all the nodes that match the given XPath. Note: This method ONLY returns element nodes. Element nodes
     * include text nodes.*
     * <p>
     * Note: The XPath executed is explicitly deleted. This means you should use this method for XPaths that are executed only
     * once like "./ns:item[@id='" + itemID "']/text()". For XPaths like "./ns:item/text()" you should use the get***Value
     * methods.
     * </p>
     * 
     * @param iXML The source XML.
     * @param sXPath The XPath to execute.
     * @return The result node or 0 if there was no match.
     */
    public static int[] selectNodesDynamic(int iXML, String sXPath)
    {
        return selectNodesDynamic(iXML, sXPath, NamespaceConstants.getXPathMetaInfo());
    }

    /**
     * This method returns all the nodes that match the given XPath. Note: This method ONLY returns element nodes. Element nodes
     * include text nodes.*
     * <p>
     * Note: The XPath executed is explicitly deleted. This means you should use this method for XPaths that are executed only
     * once like "./ns:item[@id='" + itemID "']/text()". For XPaths like "./ns:item/text()" you should use the get***Value
     * methods.
     * </p>
     * 
     * @param iXML The source XML.
     * @param sXPath The XPath to execute.
     * @param xmiMetaInfo The prefix binding information.
     * @return The result node or 0 if there was no match.
     */
    public static int[] selectNodesDynamic(int iXML, String sXPath, XPathMetaInfo xmiMetaInfo)
    {
        XPath xpath = XPath.getXPathInstance(sXPath);

        try
        {
            return selectNodes(iXML, xpath, xmiMetaInfo);
        }
        finally
        {
            xpath.delete();
        }
    }

    /**
     * This method returns all the nodes that match the given XPath. Note: This method ONLY returns element nodes. Element nodes
     * include text nodes.
     * 
     * @param iXML The source XML.
     * @param xPath The XPath to execute.
     * @param xmiMetaInfo The prefix binding information.
     * @return The result node or 0 if there was no match.
     */
    public static int[] selectNodes(int iXML, XPath xPath, XPathMetaInfo xmiMetaInfo)
    {
        ArrayList<Integer> alReturn = new ArrayList<Integer>();

        NodeSet ns = xPath.selectNodeSet(iXML, xmiMetaInfo);

        try
        {
            if (ns.hasNext())
            {
                while (ns.hasNext())
                {
                    long lResult = ns.next();

                    if (ResultNode.isElement(lResult))
                    {
                        alReturn.add(ResultNode.getElementNode(lResult));
                    }
                }
            }

            int[] aiReturn = new int[alReturn.size()];

            for (int iCount = 0; iCount < alReturn.size(); iCount++)
            {
                aiReturn[iCount] = alReturn.get(iCount);
            }

            return aiReturn;
        }
        finally
        {
            if (ns != null)
            {
                ns.delete();
            }
        }
    }

    /**
     * This method returns the first match that was found for the given XPath. Note: This method ONLY returns element nodes.
     * Element nodes include text nodes.
     * <p>
     * Note: The XPath executed is not explicitly deleted. This means that it is up to the garbage collector to decide when the
     * XPath object (and thus the native object tree) is deleted. This has no influence on the involved NOM nodes, just the XPath
     * object. This means that you should use this method when having static xpaths like ./ns:item/text(). But when executing
     * dynamic XPaths like "./ns:item[@id='" + itemID "']/text()" you should use the get***ValueDynamic methods.
     * </p>
     * 
     * @param iXML The source XML.
     * @param sXPath The XPath to execute.
     * @return The result node or 0 if there was no match.
     */
    public static int selectSingleNode(int iXML, String sXPath)
    {
        return selectSingleNode(iXML, XPath.getXPathInstance(sXPath), NamespaceConstants.getXPathMetaInfo());
    }

    /**
     * This method returns the first match that was found for the given XPath. Note: This method ONLY returns element nodes.
     * Element nodes include text nodes.
     * <p>
     * Note: The XPath executed is not explicitly deleted. This means that it is up to the garbage collector to decide when the
     * XPath object (and thus the native object tree) is deleted. This has no influence on the involved NOM nodes, just the XPath
     * object. This means that you should use this method when having static xpaths like ./ns:item/text(). But when executing
     * dynamic XPaths like "./ns:item[@id='" + itemID "']/text()" you should use the get***ValueDynamic methods.
     * </p>
     * 
     * @param iXML The source XML.
     * @param sXPath The XPath to execute.
     * @param xmiPathInfo The XPathMetoInfo for namespace resolving.
     * @return The result node or 0 if there was no match.
     */
    public static int selectSingleNode(int iXML, String sXPath, XPathMetaInfo xmiPathInfo)
    {
        return selectSingleNode(iXML, XPath.getXPathInstance(sXPath), xmiPathInfo);
    }

    /**
     * This method returns the first match that was found for the given XPath. Note: This method ONLY returns element nodes.
     * Element nodes include text nodes.
     * <p>
     * Note: The XPath executed is explicitly deleted. This means you should use this method for XPaths that are executed only
     * once like "./ns:item[@id='" + itemID "']/text()". For XPaths like "./ns:item/text()" you should use the get***ValueDynamic
     * methods.
     * </p>
     * 
     * @param iXML The source XML.
     * @param sXPath The XPath to execute.
     * @return The result node or 0 if there was no match.
     */
    public static int selectSingleNodeDynamic(int iXML, String sXPath)
    {
        return selectSingleNodeDynamic(iXML, sXPath, NamespaceConstants.getXPathMetaInfo());
    }

    /**
     * This method returns the first match that was found for the given XPath. Note: This method ONLY returns element nodes.
     * Element nodes include text nodes.
     * <p>
     * Note: The XPath executed is explicitly deleted. This means you should use this method for XPaths that are executed only
     * once like "./ns:item[@id='" + itemID "']/text()". For XPaths like "./ns:item/text()" you should use the get***ValueDynamic
     * methods.
     * </p>
     * 
     * @param iXML The source XML.
     * @param sXPath The XPath to execute.
     * @param xmiPathInfo The XPathMetoInfo for namespace resolving.
     * @return The result node or 0 if there was no match.
     */
    public static int selectSingleNodeDynamic(int iXML, String sXPath, XPathMetaInfo xmiPathInfo)
    {
        XPath xpath = XPath.getXPathInstance(sXPath);

        try
        {
            return selectSingleNode(iXML, xpath, xmiPathInfo);
        }
        finally
        {
            xpath.delete();
        }
    }

    /**
     * This method returns the first match that was found for the given XPath. Note: This method ONLY returns element nodes.
     * Element nodes include text nodes.
     * 
     * @param iXML The source XML.
     * @param xPath The XPath to execute.
     * @return The result node or 0 if there was no match.
     */
    public static int selectSingleNode(int iXML, XPath xPath)
    {
        return selectSingleNode(iXML, xPath, NamespaceConstants.getXPathMetaInfo());
    }

    /**
     * This method returns the first match that was found for the given XPath. Note: This method ONLY returns element nodes.
     * Element nodes include text nodes.
     * 
     * @param iXML The source XML.
     * @param xPath The XPath to execute.
     * @param xmiPathInfo The XPathMetoInfo for namespace resolving.
     * @return The result node or 0 if there was no match.
     */
    public static int selectSingleNode(int iXML, XPath xPath, XPathMetaInfo xmiPathInfo)
    {
        int iReturn = 0;

        NodeSet ns = xPath.selectNodeSet(iXML, xmiPathInfo);

        try
        {
            if ((ns != null) && ns.hasNext())
            {
                long lResult = ns.next();

                if (ResultNode.isElement(lResult))
                {
                    iReturn = ResultNode.getElementNode(lResult);
                }
            }

            return iReturn;
        }
        finally
        {
            if (ns != null)
            {
                ns.delete();
            }
        }
    }

    /**
     * Sets a string value for the nodes matched by the XPath expression or for only the first node if <code>
     * bAllMatches</code> is <code>false</code>. This version sets only the value of the first matched node.
     * 
     * @param iNode The node to operate on.
     * @param xPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @param sValue The string value to set.
     * @return <code>true</code> if the value was set correctly, otherwise <code>false</code> (e.g. the XPath expression didn't
     *         match any nodes.
     */
    public static boolean setNodeValue(int iNode, XPath xPath, XPathMetaInfo xmiPathInfo, String sValue)
    {
        return setNodeValue(iNode, xPath, xmiPathInfo, sValue, false);
    }

    /**
     * Sets a string value for the nodes matched by the XPath expression or for only the first node if <code>
     * bAllMatches</code> is <code>false</code>.
     * 
     * @param iNode The node to operate on.
     * @param xPath The XPath to execute.
     * @param xmiPathInfo Extra XPath information, e.g. namespace mappings
     * @param sValue The string value to set.
     * @param bAllMatches If <code>false</code> and there are multiple matches, only the first node value is set. Otherwise all
     *            values of all matched nodes are set.
     * @return <code>true</code> if the value was set correctly, otherwise <code>false</code> (e.g. the XPath expression didn't
     *         match any nodes.
     */
    public static boolean setNodeValue(int iNode, XPath xPath, XPathMetaInfo xmiPathInfo, String sValue, boolean bAllMatches)
    {
        NodeSet ns = xPath.selectNodeSet(iNode, xmiPathInfo);
        boolean matched = false;

        try
        {
            while (ns.hasNext())
            {
                long lResult = ns.next();
                int node = ResultNode.getElementNode(lResult);

                if (ResultNode.isAttribute(lResult))
                {
                    Node.setAttribute(node, ResultNode.getName(lResult), sValue);
                }
                else
                {
                    Node.setDataElement(node, "", sValue);
                }

                matched = true;

                if (!bAllMatches)
                {
                    break;
                }
            }

            return matched;
        }
        finally
        {
            if (ns != null)
            {
                ns.delete();
            }
        }
    }

    /**
     * Formats the double as a string which is cleaned up a bit. This will return 0 for 0.0.
     * 
     * @param num Number to be formatted.
     * @return Number as a string.
     */
    private static String formatAsString(double num)
    {
        String res = Double.toString(num);

        if (res.endsWith(".0"))
        {
            res = res.substring(0, res.length() - 2);
        }

        if (res.equals("-0"))
        {
            res = "0";
        }

        return res;
    }
}
