/**
 * (c) 2006 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util.template;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Node;
import com.eibus.xml.nom.NodeType;
import com.eibus.xml.nom.XMLException;

import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NOM XML template that can contain variables.
 *
 * <pre>
   These variables are defined as :
   ${[Variable name](Variable type)" [other subparameters]}

   Valid variable types are: XML, string, base64.
   The default type is 'string'.

   For XML parameters an XPath expression can be given:
   ${MSG(xml) xpath="//path/to/node"}
 * </pre>
 *
 * @author  mpoyhone
 * @see     VariableData
 */
public class XmlTemplate
{
    /**
     * Maps NOM node handles to a list of parameters for that node.
     */
    private Map<Integer, List<ParamEntry>> mParameterNodeMap = new HashMap<Integer, List<ParamEntry>>();

    /**
     * Contains the root node of the template XML.
     */
    private int xTemplateRoot;

    /**
     * Creates a new template object based on the given XML.
     *
     * @param   xTemplateXml  Template XML node.
     *
     * @return  New template instance.
     */
    public static XmlTemplate createFromXml(int xTemplateXml)
    {
        XmlTemplate xtRes = new XmlTemplate();

        xtRes.xTemplateRoot = Node.clone(xTemplateXml, true);
        xtRes.parseTemplateRecursively(xtRes.xTemplateRoot);

        return xtRes;
    }

    /**
     * Creates a new template object based on the given XML string.
     *
     * @param   sTemplateXml  Template XML string.
     * @param   dDoc          NOM document used to parse the string.
     *
     * @return  New template instance.
     *
     * @throws  UnsupportedEncodingException  Thrown if the parsing failed.
     * @throws  XMLException                  Thrown if the parsing failed.
     */
    public static XmlTemplate createFromXml(String sTemplateXml, Document dDoc)
                                     throws UnsupportedEncodingException, XMLException
    {
        int xTemplateXml = dDoc.parseString(sTemplateXml);

        return createFromXml(xTemplateXml);
    }

    /**
     * Frees all NOM handles allocated by this template.
     */
    public void clear()
    {
        mParameterNodeMap.clear();

        if (xTemplateRoot != 0)
        {
            Node.delete(xTemplateRoot);
            xTemplateRoot = 0;
        }
    }

    /**
     * Returns the template root NOM node.
     *
     * @return  Template root NOM node.
     */
    public int getTemplateRootNode()
    {
        return xTemplateRoot;
    }

    /**
     * Renders the template using the given parameters.
     *
     * @param   vcContext  Context object containing all needed information.
     *
     * @return  Rendered XML. The called must free these nodes.
     */
    public int renderTemplate(VariableContext vcContext)
    {
        if (vcContext == null)
        {
            // No variables given, so just clone the template XML.
            return Node.clone(xTemplateRoot, true);
        }

        return renderRecursively(vcContext, xTemplateRoot, 0);
    }

    /**
     * Maps the NOM node handle with the given parameter entry. The entry is added to the parameter
     * list for that node.
     *
     * @param  xNode    Node handle.
     * @param  peEntry  Parameter entry.
     */
    private void mapParameter(int xNode, ParamEntry peEntry)
    {
        List<ParamEntry> lParamList = mParameterNodeMap.get(xNode);

        if (lParamList == null)
        {
            lParamList = new ArrayList<ParamEntry>(5);
            mParameterNodeMap.put(xNode, lParamList);
        }

        lParamList.add(peEntry);
    }

    /**
     * Parses the template contents into the parameter map.
     *
     * @param  xCurrentRoot  Current node that should be parsed.
     */
    private void parseTemplateRecursively(int xCurrentRoot)
    {
        switch (Node.getType(xCurrentRoot))
        {
            case NodeType.ELEMENT:

                // First parse the attributes.
                int iAttribCount = Node.getNumAttributes(xCurrentRoot);

                for (int i = 0; i < iAttribCount; i++)
                {
                    String sName = Node.getAttributeName(xCurrentRoot, i + 1);
                    String sValue = Node.getAttribute(xCurrentRoot, sName, "");

                    if (sValue.length() == 0)
                    {
                        // This cannot be an attribute.
                    }

                    // Create a text template from the value and create
                    // new text nodes for each component of needed.
                    TextTemplate ttTmp = TextTemplate.createFromString(sValue, false);

                    if (ttTmp == null)
                    {
                        // This wasn't a valid parameter.
                        break;
                    }

                    ParamEntry peEntry = new ParamEntry();

                    peEntry.ttAttribTemplate = ttTmp;
                    peEntry.sAttribName = sName;
                    mapParameter(xCurrentRoot, peEntry);
                }

                // Recurse into the children.
                int xChild = Node.getFirstChild(xCurrentRoot);

                while (xChild != 0)
                {
                    int xNextChild = Node.getNextSibling(xChild);

                    parseTemplateRecursively(xChild);
                    xChild = xNextChild;
                }

                break;

            case NodeType.DATA:
            case NodeType.CDATA:

                // Try to parse the data into a parameter.
                String sValue = Node.getDataWithDefault(xCurrentRoot, "").trim();

                if (sValue.length() == 0)
                {
                    // Ignore whitespace.
                    Node.delete(xCurrentRoot);
                    xCurrentRoot = 0;
                    break;
                }

                // Create a text template from the value and create
                // new text nodes for each component of needed.
                TextTemplate ttTmp = TextTemplate.createFromString(sValue, false);

                if (ttTmp == null)
                {
                    // This wasn't a valid parameter.
                    break;
                }

                Object[] oaComponents = ttTmp.getOaComponents();

                if ((oaComponents == null) || (oaComponents.length == 0))
                {
                    // This wasn't a valid parameter.
                    break;
                }

                boolean bTemplateNodeUsed = false;

                for (int i = 0; i < oaComponents.length; i++)
                {
                    Object oComp = oaComponents[i];
                    int xCompNode;

                    if (oComp instanceof String)
                    {
                        // Ignore white space.
                        if (((String) oComp).trim().length() == 0)
                        {
                            continue;
                        }
                    }

                    if (!bTemplateNodeUsed)
                    {
                        // Re-use the template text node.
                        xCompNode = xCurrentRoot;
                        bTemplateNodeUsed = true;
                    }
                    else
                    {
                        // Create a new data node with same type as previous.
                        if (Node.getType(xCurrentRoot) == NodeType.DATA)
                        {
                            xCompNode = Node.getDocument(xCurrentRoot).createText("",
                                                                                  Node.getParent(xCurrentRoot));
                        }
                        else
                        {
                            xCompNode = Node.getDocument(xCurrentRoot).createCData("");
                            Node.add(xCompNode, Node.getParent(xCurrentRoot));
                        }
                    }

                    if (oComp instanceof String)
                    {
                        Node.setData(xCompNode, (String) oComp);
                    }
                    else
                    {
                        ParamEntry peEntry = new ParamEntry();

                        peEntry.vVariable = (VariableData) oComp;

                        mapParameter(xCompNode, peEntry);
                    }
                }
                break;
        }
    }

    /**
     * Renders the template using the given parameters recursively.
     *
     * @param   vcContext             DOCUMENTME
     * @param   xCurrentTemplateRoot  DOCUMENTME
     * @param   xCurrentResRoot       Current root node.
     *
     * @return  Rendered XML. The called must free these nodes.
     */
    private int renderRecursively(VariableContext vcContext, int xCurrentTemplateRoot,
                                  int xCurrentResRoot)
    {
        // First clone the template root node.
        int xResNode;

        if (Node.getType(xCurrentTemplateRoot) == NodeType.ELEMENT)
        {
            xResNode = Node.clone(xCurrentTemplateRoot, false);
        }
        else
        {
            xResNode = Node.duplicate(xCurrentTemplateRoot);
        }

        if (xCurrentResRoot != 0)
        {
            xResNode = Node.appendToChildren(xResNode, xCurrentResRoot);
        }

        // Check if we have any parameters for this node.
        List<ParamEntry> lNodeParams = mParameterNodeMap.get(xCurrentTemplateRoot);

        if (lNodeParams != null)
        {
            for (ParamEntry peEntry : lNodeParams)
            {
                setValueFromParameter(vcContext, xResNode, peEntry);
            }
        }

        if (Node.getType(xCurrentTemplateRoot) == NodeType.ELEMENT)
        {
            // Recurse into the children.
            int xTemplateChild = Node.getFirstChild(xCurrentTemplateRoot);

            while (xTemplateChild != 0)
            {
                renderRecursively(vcContext, xTemplateChild, xResNode);

                xTemplateChild = Node.getNextSibling(xTemplateChild);
            }
        }

        return xResNode;
    }

    /**
     * Sets the data node/attribute value from the given parameter.
     *
     * @param  vcContext  Document used to create the result XML parameters.
     * @param  xNode      Data node/attribute element handle.
     * @param  peEntry    Parameter entry.
     */
    private void setValueFromParameter(VariableContext vcContext, int xNode, ParamEntry peEntry)
    {
        if (peEntry.sAttribName == null)
        {
            // This is an XML element value.
            Object oValue = peEntry.vVariable.resolveValue(vcContext);

            if (oValue == null)
            {
                oValue = "";
            }

            if (oValue instanceof String)
            {
                String sStrValue = (String) oValue;

                if (Node.getType(xNode) != NodeType.CDATA)
                {
                    Node.setData(xNode, sStrValue);
                }
                else
                {
                    // Node.setData not seem to work for CDATA nodes, so create a new one.
                    int xTmp = Node.getDocument(xNode).createCData(sStrValue);

                    Node.insert(xTmp, xNode);
                    Node.delete(xNode);
                    xNode = 0;
                }
            }
            else if (oValue instanceof int[])
            {
                int[] xaResNodes = (int[]) oValue;

                if (peEntry.sAttribName == null)
                {
                    for (int xValueNode : xaResNodes)
                    {
                        // Add the value as a sibling of the parameter text node.
                        Node.insert(xValueNode, xNode);
                    }

                    // Delete the original text node as it contains the parameter definition.
                    Node.delete(xNode);
                }
                else
                {
                    // Set the attribute value as combined value of all selected nodes.
                    StringBuilder sbTmp = new StringBuilder(100);

                    for (int xValueNode : xaResNodes)
                    {
                        // Set the value
                        String sValue = Node.getData(xValueNode);

                        if (sValue != null)
                        {
                            sbTmp.append(sValue);
                        }
                    }

                    Node.setAttribute(xNode, peEntry.sAttribName, sbTmp.toString());
                }
            }
            else
            {
                throw new IllegalStateException("Invalid result value type: " + oValue.getClass());
            }
        }
        else
        {
            // This is an attribute.
            String sAttribValue = peEntry.ttAttribTemplate.renderTemplate(vcContext);

            Node.setAttribute(xNode, peEntry.sAttribName, sAttribValue);
        }
    }

    /**
     * Contains parameter information.
     *
     * @author  mpoyhone
     */
    private static class ParamEntry
    {
        /**
         * If set, this entry points to an XML attribute instead of a XML data node.
         */
        String sAttribName;
        /**
         * Contains XML element attribute parameter information as a text template.
         */
        TextTemplate ttAttribTemplate;
        /**
         * Contains XML element data parameter information parsed into a variable classe.
         */
        VariableData vVariable;
    }
}
