/**
 * (c) 2006 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util.template;

import com.eibus.util.system.Native;

import com.eibus.xml.nom.Node;
import com.eibus.xml.nom.NodeType;
import com.eibus.xml.xpath.NodeSet;
import com.eibus.xml.xpath.ResultNode;
import com.eibus.xml.xpath.XPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Defines a variable that can be used in an XML template or in a string parameter.
 *
 * <pre> Variables are defined as:
    ${[Variable name] type="[Variable type]" [other parameters]}
    Valid variable types are: XML, string, base64 (defined in the EType enumeration). The default type is 'XML'.
    For XML variables an XPath expression can be given:
    ${MSG(XML) xpath="//path/to/node"}
    Subvariable value can be quoted like: "value", 'value', :value:, ?value? or #value#. This
    helps putting the variable inside an XML attribute. E.g.:
    &lt;a attr="${MSG xpath=$myxpath$}" /&gt;</pre>
 *
 * @author  mpoyhone
 */
public class VariableData
{
    /**
     * Defines the parameter name 'xpath' for XML variable types.
     */
    public static final String PARAM_XML_XPATH = "xpath";
    /**
     * Defines the parameter name 'start' for XML variable types.
     */
    public static final String PARAM_XML_START = "start";
    /**
     * Defines the parameter name 'count' for XML variable types.
     */
    public static final String PARAM_XML_COUNT = "count";
    /**
     * <pre> Variable matching regexp:
        \$\{([\w_-]+)(?:\(([\w_-]+)\))?\s*([^}]*)\}
        ${paramname(typename) whitespace* (subparamname(subparamvalue) whitespace*)*}
        Output groups are: 1 - Variable name.
        2 - Variable type. If empty, the default type is assumed.
        2 - Parameter string.</pre>
     */
    public static final Pattern pVariablePattern = Pattern.compile("\\$\\{([\\w_-]+)(?:\\(([\\w_-]+)\\))?\\s*([^}]*)\\}");
    /**
     * <pre> Subparameter matching regexp: ([\w_-]+)=(["':?#])(.*?)\2(?:\s|$)
        subparamname(subparamvalue)
        This regexp value to be quoted like: "value", 'value', :value:, ?value? or #value#. This
        helps putting the variable inside an XML attribute. Output groups are:
        1 - Parameter name. 2 - Quotation mark (any of: "':?#)
        3 - Parameter value.</pre>
     */
    public static final Pattern pVarParamPattern = Pattern.compile("([\\w_-]+)=([\"\':?#])(.*?)\\2(?:\\s|$)");
    /**
     * XPath node result set count. This amount of nodes are returned starting from the start index.
     * If -1, all nodes are returned.
     */
    private int iXPathNodeCount = -1;
    /**
     * Starting index for XPath node result set from which the nodes are returned.
     */
    private int iXPathStartNodeIndex = 0;
    /**
     * Variable name.
     */
    private String sVariableName;
    /**
     * Parameter type.
     */
    private EVariableType tVariableType;
    /**
     * Optional XPath search path for XML parameters.
     */
    private XPath xXmlVariableXPath;

    /**
     * Parses the variable from the string contained in the given matcher.
     *
     * @param   mVariableMatcher  Regexp matcher containing the variable match.
     *
     * @return  Parsed variable.
     *
     * @throws  IllegalArgumentException
     */
    public static VariableData parseVariableFromMatcher(Matcher mVariableMatcher)
                                                 throws IllegalArgumentException
    {
        return parseVariableFromMatcher(mVariableMatcher, null);
    }

    /**
     * Parses the variable from the string contained in the given matcher.
     *
     * @param   mVariableMatcher  Regexp matcher containing the variable match.
     * @param   etDefaultType     Default message type.
     *
     * @return  Parsed variable.
     *
     * @throws  IllegalArgumentException
     */
    public static VariableData parseVariableFromMatcher(Matcher mVariableMatcher,
                                                        EVariableType etDefaultType)
                                                 throws IllegalArgumentException
    {
        String sVarName = mVariableMatcher.group(1);
        String sVarTypeType = mVariableMatcher.group(2);
        String sVarParameterString = mVariableMatcher.group(3);
        Map<String, String> mParameterMap = new HashMap<String, String>();

        if ((sVarParameterString != null) && (sVarParameterString.length() > 0))
        {
            Matcher mParamMatcher = pVarParamPattern.matcher(sVarParameterString);
            int iLastMatchEndPos = 0;
            int iPos = 0;

            while ((iPos < sVarParameterString.length()) && mParamMatcher.find(iPos))
            {
                if ((mParamMatcher.start() - iLastMatchEndPos) > 1)
                {
                    // Check that we didn't skip any text (then this is not a valid parameter
                    // string). only white space is allowed between parameters.
                    String sSpace = sVarParameterString.substring(iLastMatchEndPos,
                                                                  mParamMatcher.start());

                    if (sSpace.trim().length() > 0)
                    {
                        return null;
                    }
                }

                String sParamName = mParamMatcher.group(1);
                String sParamValue = mParamMatcher.group(3);

                if ((sParamName != null) && (sParamValue != null))
                {
                    mParameterMap.put(sParamName, sParamValue);
                }

                iLastMatchEndPos = mParamMatcher.end();
                iPos = iLastMatchEndPos + 1;
            }

            if (iLastMatchEndPos < sVarParameterString.length())
            {
                // Check that we didn't skip any text (then this is not a valid parameter string).
                // only white space is allowed between parameters.
                String sSpace = sVarParameterString.substring(iLastMatchEndPos,
                                                              sVarParameterString.length());

                if (sSpace.trim().length() > 0)
                {
                    return null;
                }
            }
        }

        VariableData peRes = new VariableData();

        peRes.sVariableName = sVarName;

        if ((sVarTypeType != null) && (sVarTypeType.length() > 0))
        {
            peRes.tVariableType = EVariableType.valueOf(sVarTypeType.trim().toUpperCase());

            if (peRes.tVariableType == null)
            {
                throw new IllegalArgumentException("Invalid variable type: " + sVarTypeType);
            }
        }
        else
        {
            peRes.tVariableType = ((etDefaultType != null) ? etDefaultType
                                                           : EVariableType.getDefault());
        }

        switch (peRes.tVariableType)
        {
            case XML:

                String sXmlParamXpath = mParameterMap.get(PARAM_XML_XPATH);

                if ((sXmlParamXpath != null) && (sXmlParamXpath.length() > 0))
                {
                    peRes.xXmlVariableXPath = XPath.getXPathInstance(sXmlParamXpath);
                }

                String sXPathStartIndex = mParameterMap.get(PARAM_XML_START);

                if (sXPathStartIndex != null)
                {
                    try
                    {
                        peRes.iXPathStartNodeIndex = Integer.parseInt(sXPathStartIndex);
                    }
                    catch (Exception e)
                    {
                        throw new IllegalArgumentException("Invalid XPath start index value: " +
                                                           sXPathStartIndex, e);
                    }
                }

                String sXPathNodeCount = mParameterMap.get(PARAM_XML_COUNT);

                if (sXPathNodeCount != null)
                {
                    try
                    {
                        peRes.iXPathNodeCount = Integer.parseInt(sXPathNodeCount);
                    }
                    catch (Exception e)
                    {
                        throw new IllegalArgumentException("Invalid XPath node count value: " +
                                                           sXPathNodeCount, e);
                    }
                }
                break;
        }

        return peRes;
    }

    /**
     * Tries to parse the given string into a parameter.
     *
     * @param   sValue  Parameter string.
     *
     * @return  New <code>ParamEntry</code> object representing the parameter or <code>null</code>
     *          if the string is not a valid parameter.
     *
     * @throws  IllegalArgumentException  DOCUMENTME
     */
    public static VariableData parseVariableFromString(String sValue)
                                                throws IllegalArgumentException
    {
        if (sValue == null)
        {
            return null;
        }

        Matcher mVariableMatcher = pVariablePattern.matcher(sValue.trim());

        if (!mVariableMatcher.matches())
        {
            return null;
        }

        return parseVariableFromMatcher(mVariableMatcher, null);
    }

    /**
     * Returns the variableName.
     *
     * @return  Returns the variableName.
     */
    public String getVariableName()
    {
        return sVariableName;
    }

    /**
     * Returns the variableType.
     *
     * @return  Returns the variableType.
     */
    public EVariableType getVariableType()
    {
        return tVariableType;
    }

    /**
     * Returns the xmlVariableXPath.
     *
     * @return  Returns the xmlVariableXPath.
     */
    public XPath getXmlVariableXPath()
    {
        return xXmlVariableXPath;
    }

    /**
     * Returns variable value taken from the value map and with the configured type. The result can
     * be a <code>String</code> or for NOM XML node selection an <code>int[]</code> int array, in
     * which case the called is responsible for returning the returned nodes. If the method return
     * <code>null</code>, then the value was not found from the value map.
     *
     * @param   vcContext  Context object containing all needed information.
     *
     * @return  <code>String</code> for string variables/XPath expressions that return a string or
     *          <code>int[]</code> for XML variables.
     */
    public Object resolveValue(VariableContext vcContext)
    {
        ITemplateVariable emValue = vcContext.getValueMap().get(sVariableName);

        if (emValue == null)
        {
            return null;
        }

        switch (tVariableType)
        {
            case STRING:
                return emValue.getAsString(vcContext.getStringEncoding());

            case HEXSTRING:
            {
                byte[] baByteValue = emValue.getAsByteArray(vcContext.getStringEncoding());
                String sHexChars = "0123456789ABCDEF";
                StringBuilder sbRes = new StringBuilder(baByteValue.length * 2);

                for (byte bByte : baByteValue)
                {
                    sbRes.append(sHexChars.charAt((0xF0 & bByte) >> 4));
                    sbRes.append(sHexChars.charAt(0x0F & bByte));
                }

                return sbRes.toString();
            }

            case BASE64:
            {
                byte[] baByteValue = emValue.getAsByteArray(vcContext.getStringEncoding());

                return new String(Native.encodeBinBase64(baByteValue, baByteValue.length));
            }

            case XML:

                // We are responsible of freeing the returned node!
                int xNodeValue = emValue.getAsNomXml(vcContext.getDoc());
                boolean bDeleteNodeValue = true;
                Object oResult = null;

                if (xXmlVariableXPath != null)
                {
                    NodeSet osResultSet = xXmlVariableXPath.selectNodeSet(xNodeValue);
                    List<Integer> lResultNodes = null;
                    StringBuilder sbResultString = null;
                    int iNodePos = 0;

                    // Iterate through the results
                    while (osResultSet.hasNext())
                    {
                        long lResultNode = osResultSet.next();

                        if (iNodePos < iXPathStartNodeIndex)
                        {
                            iNodePos++;
                            continue;
                        }

                        if (ResultNode.isAttribute(lResultNode))
                        {
                            // Set the text of the original text node to the XPath result
                            // attribute value.
                            String sAttribValue = ResultNode.getStringValue(lResultNode);

                            if (sbResultString == null)
                            {
                                sbResultString = new StringBuilder(256);
                            }

                            sbResultString.append(sAttribValue);
                            bDeleteNodeValue = true;
                        }
                        else
                        {
                            int xResultNode = ResultNode.getElementNode(lResultNode);

                            // Check that we haven't selected the root node.
                            bDeleteNodeValue = (xNodeValue != xResultNode);

                            if (lResultNodes == null)
                            {
                                lResultNodes = new ArrayList<Integer>(10);
                            }

                            xResultNode = Node.unlink(xResultNode);
                            lResultNodes.add(xResultNode);
                        }

                        if (iXPathNodeCount >= 0)
                        {
                            if (iNodePos >= ((iXPathStartNodeIndex + iXPathNodeCount) - 1))
                            {
                                break;
                            }
                        }

                        iNodePos++;
                    }

                    if (lResultNodes != null)
                    {
                        int[] iaRes = new int[lResultNodes.size()];
                        int iPos = 0;

                        for (Integer iValue : lResultNodes)
                        {
                            iaRes[iPos++] = iValue.intValue();
                        }

                        oResult = iaRes;
                    }
                    else if (sbResultString != null)
                    {
                        oResult = sbResultString.toString();
                    }
                }
                else
                {
                    oResult = new int[] { xNodeValue };
                    bDeleteNodeValue = false;
                }

                if (bDeleteNodeValue)
                {
                    // Delete the value node. Either it cannot be added to the result XML or some
                    // children are already added.
                    Node.delete(xNodeValue);
                    xNodeValue = 0;
                }

                return oResult;
        }

        return null;
    }

    /**
     * Same as <code>resolveValue</code> but always returns a string value (XML nodes values are
     * concatenated together).
     *
     * @param   vcContext  Context object containing all needed information.
     *
     * @return  Variable value as a string or <code>null</code> if the value was not found from the
     *          value map.
     */
    public String resolveValueAsString(VariableContext vcContext)
    {
        Object oRes = resolveValue(vcContext);

        if (oRes == null)
        {
            return null;
        }

        if (oRes instanceof String)
        {
            return (String) oRes;
        }

        if (oRes instanceof int[])
        {
            int[] xaResNodes = (int[]) oRes;
            StringBuilder sbResultString = new StringBuilder(512);

            for (int xNode : xaResNodes)
            {
                String sNodeValue;

                if (Node.getType(xNode) == NodeType.ELEMENT)
                {
                    sNodeValue = Node.writeToString(xNode, false);
                }
                else
                {
                    sNodeValue = Node.getData(xNode);
                }

                if (sNodeValue != null)
                {
                    sbResultString.append(sNodeValue);
                }
            }

            for (int xNode : xaResNodes)
            {
                Node.delete(xNode);
            }

            return sbResultString.toString();
        }

        throw new IllegalStateException("Invalid result value type: " + oRes.getClass());
    }

    /**
     * The variableName to set.
     *
     * @param  aVariableName  The variableName to set.
     */
    public void setVariableName(String aVariableName)
    {
        sVariableName = aVariableName;
    }

    /**
     * The variableType to set.
     *
     * @param  aVariableType  The variableType to set.
     */
    public void setVariableType(EVariableType aVariableType)
    {
        tVariableType = aVariableType;
    }

    /**
     * The xmlVariableXPath to set.
     *
     * @param  aXmlVariableXPath  The xmlVariableXPath to set.
     */
    public void setXmlVariableXPath(XPath aXmlVariableXPath)
    {
        xXmlVariableXPath = aXmlVariableXPath;
    }
}
