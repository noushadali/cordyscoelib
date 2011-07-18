/**
 * (c) 2008 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util.xml.nom;

import com.cordys.coe.util.xml.NamespaceDefinitions;

import com.eibus.xml.xpath.XPath;
import com.eibus.xml.xpath.XPathMetaInfo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * Wraps the NOM XPath and XPathMetainfo objects into one an provides some
 * convenience methods for typed value access as well as default namespace
 * handling.
 *
 * @see com.cordys.coe.util.xml.NamespaceDefinitions
 * @author mpoyhone
 */
public class XPathWrapper {

    /**
     * Contains the default namespace bindings.
     */
    private static final Map<String, String> defaultBindings;

    /**
     * Contains valid boolean values.
     */
    private static final Map<String, Boolean> validBooleanValues =
        new HashMap<String, Boolean>();

    static {
        defaultBindings = NamespaceDefinitions.getNamespaceMappings();
        validBooleanValues.put("true", Boolean.TRUE);
        validBooleanValues.put("false", Boolean.FALSE);
        validBooleanValues.put("on", Boolean.TRUE);
        validBooleanValues.put("off", Boolean.FALSE);
        validBooleanValues.put("yes", Boolean.TRUE);
        validBooleanValues.put("no", Boolean.FALSE);
        validBooleanValues.put("1", Boolean.TRUE);
        validBooleanValues.put("0", Boolean.FALSE);
    }

    /**
     * Contains the XPath used by this wrapper.
     */
    private XPath xpath;

    /**
     * Contains the XPath metainfo used by this wrapper.
     */
    private XPathMetaInfo metainfo;

    /**
     * Constructor for XPathWrapper
     * @param xpath XPath
     */
    private XPathWrapper(String xpath) {
        this.xpath = XPath.getXPathInstance(xpath);
    }

    /**
     * Constructor for XPathWrapper
     * @param xpath XPath
     * @param metainfo Metainfo
     */
    private XPathWrapper(XPath xpath, XPathMetaInfo metainfo) {
        this.xpath = xpath;
        this.metainfo = metainfo;
    }

    /**
     * Creates a new XPathWrapper instance.
     *
     * @param xpath XPath object to use.
     * @param metainfo XPath metainfo object to use for e.g. namespace bindings.
     * @return Created XPathWrapper instance.
     */
    public static XPathWrapper create(XPath xpath, XPathMetaInfo metainfo) {
        return new XPathWrapper(xpath, metainfo);
    }

    /**
     * Creates a new XPathWrapper instance.
     *
     * @param xpath XPath to use.
     * @param metainfo XPath metainfo object to use e.g. namespace bindings.
     * @return Created XPathWrapper instance.
     */
    public static XPathWrapper create(String xpath) {
        return new XPathWrapper(xpath);
    }

    /**
     * Creates a new XPathWrapper instance.
     *
     * @param xpath XPath to use.
     * @param bindings Namespace bindings as [prefix, namespace URI]
     * @return Created XPathWrapper instance.
     */
    public static XPathWrapper create(String xpath,
        Map<String, String> bindings) {
        XPathWrapper res = new XPathWrapper(xpath);

        if (bindings != null) {
            res.addNamespaceBindings(bindings);
        }

        return res;
    }

    /**
     * Returns the XPath object used by this wrapper.
     * 
     * @return The XPath object.
     */
    public XPath getXPath()
    {
        return xpath;
    }
    
    /**
     * Creates a new XPathWrapper instance.
     * Example:
     * <code>
     * XPathWrapper.create(".//test", "ns1 http://asdas.com/asd123", "ns2 http://234234/asd");
     * </code>
     *
     * @param xpath XPath to use.
     * @param bindings Namespace bindings as strings.
     * @return Created XPathWrapper instance.
     */
    public static XPathWrapper create(String xpath, String... bindings) {
        XPathWrapper res = new XPathWrapper(xpath);

        for (String bindingStr : bindings) {
            res.addNamespaceBinding(bindingStr);
        }

        return res;
    }

    /**
     * Creates a new XPathWrapper instance.
     * Example:
     * <code>
     * String[][] bindingArray = {
     *   { "ns1", "http://asdas.com/asd123" },
     *   { "ns2", "http://234234/asd" }
     * };
     * XPathWrapper.create(".//test", bindingArray);
     * </code>
     *
     * @param xpath XPath to use.
     * @param bindings Namespace bindings as [prefix, namespace URI]
     * @return Created XPathWrapper instance.
     */
    public static XPathWrapper create(String xpath, String[][] bindings) {
        XPathWrapper res = new XPathWrapper(xpath);

        if (bindings != null) {
            res.addNamespaceBindings(bindings);
        }

        return res;
    }

    /**
     * Creates a a new XPathWrapper with the default namespace bindings.
     *
     * @param xpath XPath to use.
     * @return Created XPathWrapper instance.
     */
    public static XPathWrapper createWithDefaultBindings(String xpath) {
        XPathWrapper res = new XPathWrapper(xpath);

        res.addNamespaceBindings(defaultBindings);

        return res;
    }

    /**
     * Creates a a new XPathWrapper with the default and extra namespace bindings.
     *
     * @param xpath XPath to use.
     * @param bindings Extra namespace bindings as [prefix, namespace URI]
     * @return Created XPathWrapper instance.
     */
    public static XPathWrapper createWithDefaultBindings(String xpath,
        Map<String, String> bindings) {
        XPathWrapper res = createWithDefaultBindings(xpath);

        if (bindings != null) {
            res.addNamespaceBindings(bindings);
        }

        return res;
    }

    /**
     * Creates a a new XPathWrapper with the default and extra namespace bindings.
     * Example:
     * <code>
     * XPathWrapper.createWithDefaultBindings(".//test", "ns1 http://asdas.com/asd123", "ns2 http://234234/asd");
     * </code>
     *
     * @param xpath XPath to use.
     * @param bindings Extra namespace bindings as strings.
     * @return Created XPathWrapper instance.
     */
    public static XPathWrapper createWithDefaultBindings(String xpath,
        String... bindings) {
        XPathWrapper res = createWithDefaultBindings(xpath);

        for (String bindingStr : bindings) {
            res.addNamespaceBinding(bindingStr);
        }

        return res;
    }

    /**
     * Creates a a new XPathWrapper with the default and extra namespace bindings.
     * Example:
     * <code>
     * String[][] bindingArray = {
     *   { "ns1", "http://asdas.com/asd123" },
     *   { "ns2", "http://234234/asd" }
     * };
     * XPathWrapper.createWithDefaultBindings(".//test", bindingArray);
     * </code>
     *
     * @param xpath XPath to use.
     * @param bindings Extra namespace bindings as strings.
     * @return Created XPathWrapper instance.
     */
    public static XPathWrapper createWithDefaultBindings(String xpath,
        String[][] bindings) {
        XPathWrapper res = createWithDefaultBindings(xpath);

        res.addNamespaceBindings(defaultBindings);

        if (bindings != null) {
            res.addNamespaceBindings(bindings);
        }

        return res;
    }

    /**
     * Adds namespace bindings.
     *
     * @param bindings Namespace bindings as [prefix, namespace URI]
     * @return This instance for easy method chaining.
     */
    public XPathWrapper addNamespaceBindings(Map<String, String> bindings) {

        if (bindings == null) {
            throw new NullPointerException("'bindings' cannot be null.");
        }

        for (Map.Entry<String, String> e : bindings.entrySet()) {
            addNamespaceBinding(e.getKey(), e.getValue());
        }

        return this;
    }


    /**
     * Adds namespace bindings.
     * Example:
     * <code>
     * String[][] bindingArray = {
     *   { "ns1", "http://asdas.com/asd123" },
     *   { "ns2", "http://234234/asd" }
     * };
     * XPathWrapper.create("//ns1:a/ns2:b").addNamespaceBindings(bindingArray);
     * </code>
     *
     * @param bindings Namespace bindings as [prefix, namespace URI]
     * @return This instance for easy method chaining.
     */
    public XPathWrapper addNamespaceBindings(String[][] bindings) {

        for (String[] b : bindings) {

            if (b.length != 2) {
                throw new IllegalArgumentException(
                    "Invalid namespace binding: " + Arrays.toString(b));
            }

            addNamespaceBinding(b[0], b[1]);
        }

        return this;
    }

    /**
     * Adds the default namespace bindings.
     *
     * @return This instance for easy method chaining.
     */
    public XPathWrapper addDefaultNamespaceBindings() {
        addNamespaceBindings(defaultBindings);

        return this;
    }

    /**
     * Clears all namespace bindings.
     *
     * @return This instance for easy method chaining.
     */
    public XPathWrapper clearNamespaceBindings() {
        metainfo = null;

        return this;
    }

    /**
     * Returns the XPath metainfo object used by this wrapper.
     *
     * @return XPath metainfo object or <code>null</code> is none is used.
     */
    public XPathMetaInfo getMetaInfo() {
        return metainfo;
    }

    /**
     * Adds a namespace binding.
     *
     * @param prefix Binding prefix.
     * @param namespaceUri Binding namespace URI.
     * @return This instance for easy method chaining.
     */
    public XPathWrapper addNamespaceBinding(String prefix,
        String namespaceUri) {

        if (prefix == null) {
            throw new NullPointerException("'prefix' cannot be null.");
        }

        if (namespaceUri == null) {
            throw new NullPointerException("'namespaceUri' cannot be null.");
        }

        if (metainfo == null) {

            // Create a new metainfo object.
            metainfo = new XPathMetaInfo();
        }

        metainfo.addNamespaceBinding(prefix, namespaceUri);

        return this;
    }

    /**
     * Adds a namespace binding.
     *
     * @param bindingStr Binding string: "ns http://namespace/uri"
     * @return This instance for easy method chaining.
     */
    public XPathWrapper addNamespaceBinding(String bindingStr) {

        if (bindingStr == null) {
            throw new NullPointerException("'bindingStr' cannot be null.");
        }

        int sep = bindingStr.indexOf(' ');

        if ((sep <= 0) || (sep >= (bindingStr.length() - 1))) {
            throw new IllegalArgumentException("Invalid binding string '" +
                bindingStr + "'. Expected format 'prefix namespaceuri'");
        }

        addNamespaceBinding(bindingStr.substring(0, sep),
            bindingStr.substring(sep + 1));


        return this;
    }

    /**
     * Returns XPath result as a string.
     *
     * @param node Match root node.
     * @return Result or <code>null</code> of no match was found.
     */
    public String getStringValue(int node) {
        return getStringValue(node, null);
    }

    /**
     * Returns XPath result as a string.
     *
     * @param node Match root node.
     * @param defaultValue Value to use when no match was found.
     * @return Result or the default value.
     */
    public String getStringValue(int node, String defaultValue) {
        String res = XPathHelper.evaluateToString(node, xpath, metainfo);

        return (res != null) ? res : defaultValue;
    }

    /**
     * Returns all matches as a string array.
     *
     * @param node Match root node.
     * @return All found matches as an array (length can be zero).
     */
    public String[] getAllStringValues(int node) {
        String[] res = XPathHelper.evaluateToStringArray(node, xpath, metainfo);

        if (res == null) {
            return new String[0];
        }

        return res;
    }

    /**
     * Returns XPath result as an integer.
     *
     * @param node Match root node.
     * @param defaultValue Value to use when no match was found.
     * @return Result or the default value.
     */
    public int getIntegerValue(int node, int defaultValue) {
        String res = XPathHelper.evaluateToString(node, xpath, metainfo);

        return (res != null) ? Integer.parseInt(res) : defaultValue;
    }

    /**
     * Returns all matches as an integer array.
     *
     * @param node Match root node.
     * @return All found matches as an array (length can be zero).
     */
    public int[] getAllIntegerValues(int node) {
        String[] stringRes = XPathHelper.evaluateToStringArray(node, xpath,
                metainfo);

        if (stringRes == null) {
            return new int[0];
        }

        int[] res = new int[stringRes.length];

        for (int i = 0; i < res.length; i++) {
            res[i] = Integer.parseInt(stringRes[i]);
        }

        return res;
    }

    /**
     * Returns XPath result as a long.
     *
     * @param node Match root node.
     * @param defaultValue Value to use when no match was found.
     * @return Result or the default value.
     */
    public long getLongValue(int node, long defaultValue) {
        String res = XPathHelper.evaluateToString(node, xpath, metainfo);

        return (res != null) ? Long.parseLong(res) : defaultValue;
    }

    /**
     * Returns all matches as a long array.
     *
     * @param node Match root node.
     * @return All found matches as an array (length can be zero).
     */
    public long[] getAllLongValues(int node) {
        String[] stringRes = XPathHelper.evaluateToStringArray(node, xpath,
                metainfo);

        if (stringRes == null) {
            return new long[0];
        }

        long[] res = new long[stringRes.length];

        for (int i = 0; i < res.length; i++) {
            res[i] = Long.parseLong(stringRes[i]);
        }

        return res;
    }

    /**
     * Returns XPath result as a double.
     *
     * @param node Match root node.
     * @param defaultValue Value to use when no match was found.
     * @return Result or the default value.
     */
    public double getDoubleValue(int node, double defaultValue) {
        String res = XPathHelper.evaluateToString(node, xpath, metainfo);

        return (res != null) ? Double.parseDouble(res) : defaultValue;
    }

    /**
     * Returns all matches as a double array.
     *
     * @param node Match root node.
     * @return All found matches as an array (length can be zero).
     */
    public double[] getAllDoubleValues(int node) {
        String[] stringRes = XPathHelper.evaluateToStringArray(node, xpath,
                metainfo);

        if (stringRes == null) {
            return new double[0];
        }

        double[] res = new double[stringRes.length];

        for (int i = 0; i < res.length; i++) {
            res[i] = Double.parseDouble(stringRes[i]);
        }

        return res;
    }

    /**
     * Returns XPath result as a boolean.
     *
     * @param node Match root node.
     * @param defaultValue Value to use when no match was found.
     * @return Result or the default value.
     */
    public boolean getBooleanValue(int node, boolean defaultValue) {
        String res = XPathHelper.evaluateToString(node, xpath, metainfo);

        if (res == null) {
            return defaultValue;
        }

        if (!validBooleanValues.containsKey(res)) {
            throw new IllegalArgumentException("Invalid boolean value: " + res);
        }

        return validBooleanValues.get(res);
    }

    /**
     * Returns all matches as a boolean array.
     *
     * @param node Match root node.
     * @return All found matches as an array (length can be zero).
     */
    public boolean[] getAllBooleanValues(int node) {
        String[] stringRes = XPathHelper.evaluateToStringArray(node, xpath,
                metainfo);

        if (stringRes == null) {
            return new boolean[0];
        }

        boolean[] res = new boolean[stringRes.length];

        for (int i = 0; i < res.length; i++) {
            String valStr = stringRes[i];

            if (!validBooleanValues.containsKey(valStr)) {
                throw new IllegalArgumentException("Invalid boolean value: " +
                    valStr);
            }

            res[i] = validBooleanValues.get(valStr);
        }

        return res;
    }

    /**
     * Returns the first matched node.
     *
     * @param node Match root node.
     * @return First matched node or zero, if the XPath didn't match any nodes.
     */
    public int selectSingleNode(int node) {
        return xpath.firstMatch(node, metainfo);
    }

    /**
     * Returns the first matched node.
     *
     * @param node Match root node.
     * @return All matched nodes as an array (the length can be zero).
     */
    public int[] selectNodes(int node) {
        int[] res = xpath.selectElementNodes(node, metainfo);

        if (res == null) {
            return new int[0];
        }

        return res;
    }
}
