/**
 *  2005 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util.xml;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Find;
import com.eibus.xml.nom.Node;

/**
 * JavaCall wrapper for XMLQuery object.
 *
 * @author  mpoyhone
 */
public class XMLQueryJavaCall
{
    /**
     * Get the text value of the first element (or attribute) from the input XML structure that
     * matches the given XPath-like expression. For exact description of the syntax supported <code>
     * XQuery</code>.
     *
     * @param   xXml    Input XML structure. Note the the root element must be <code>XML</code> and
     *                  the actual root element of the input XML is to be placed under it.
     * @param   sXPath  XPath-like query string.
     *
     * @return  The following XML structure :
     *
     *          <pre>
         &lt;response>
           &lt;found>(true of false)&lt;/found>
           &lt;value>(the value found is placed here)&lt;/value>
         &lt;/response>
     *  </pre>
     */
    public static int getValue(int xXml, String sXPath)
    {
        if (xXml == 0)
        {
            throw new IllegalArgumentException("Input XML missing.");
        }

        XMLQuery xqQuery;

        try
        {
            xqQuery = new XMLQuery(sXPath);
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Invalid input XPath experssion : " + sXPath);
        }

        Document dDoc = Node.getDocument(xXml);
        int xResponse = dDoc.createElement("response");
        String sValue = null;

        // Get the first child of XML parameter. This does not support multiple
        // children.
        xXml = Find.firstMatch(xXml, "<><>");

        if (xXml != 0)
        {
            int xNode = xqQuery.findNode(xXml);

            if (xNode != 0)
            {
                if (xqQuery.getQueryAttribute() == null)
                {
                    sValue = Node.getDataWithDefault(xNode, "");
                }
                else
                {
                    sValue = Node.getAttribute(xNode, xqQuery.getQueryAttribute(), "");
                }
            }
        }

        if (sValue != null)
        {
            Node.createTextElement("found", "true", xResponse);
            Node.createTextElement("value", sValue, xResponse);
        }
        else
        {
            Node.createTextElement("found", "false", xResponse);
            Node.createElement("value", xResponse);
        }

        return xResponse;
    }

    /**
     * Test main.
     *
     * @param   args  None
     *
     * @throws  Exception  Thrown if failed.
     */
    public static void main(String[] args)
                     throws Exception
    {
        String sXml = "<XML><a>" + "  <b>XYZ</b>" +
                      "  <b a='test'>test text</b>" + "  <c>" +
                      "    <e>EEE</e>" + "  </c>" +
                      "<y/><y z='1'></y><y v='2'> </y></a></XML>";

        Document dDoc = new Document();
        int xInput;
        int xOutput;
        String sXPath;

        xInput = dDoc.parseString(sXml);
        sXPath = "//b";
        xOutput = selectNodes(xInput, sXPath);
        System.out.println("XPath: " + sXPath + " Output: " + Node.writeToString(xOutput, true));

        xInput = dDoc.parseString(sXml);
        sXPath = "//b";
        xOutput = selectSingleNode(xInput, sXPath);
        System.out.println("XPath: " + sXPath + " Output: " + Node.writeToString(xOutput, true));

        xInput = dDoc.parseString(sXml);
        sXPath = "//b@a";
        xOutput = selectSingleNode(xInput, sXPath);
        System.out.println("XPath: " + sXPath + " Output: " + Node.writeToString(xOutput, true));

        xInput = dDoc.parseString(sXml);
        sXPath = "//b";
        xOutput = getValue(xInput, sXPath);
        System.out.println("XPath: " + sXPath + " Output: " + Node.writeToString(xOutput, true));

        xInput = dDoc.parseString(sXml);
        sXPath = "//b@a";
        xOutput = getValue(xInput, sXPath);
        System.out.println("XPath: " + sXPath + " Output: " + Node.writeToString(xOutput, true));

        xInput = dDoc.parseString(sXml);
        sXPath = "a/c/e";
        xOutput = getValue(xInput, sXPath);
        System.out.println("XPath: " + sXPath + " Output: " + Node.writeToString(xOutput, true));

        xInput = dDoc.parseString(sXml);
        sXPath = "a/y";
        xOutput = getValue(xInput, sXPath);
        System.out.println("XPath: " + sXPath + " Output: " + Node.writeToString(xOutput, true));

        xInput = dDoc.parseString(sXml);
        sXPath = "a/y[@z]";
        xOutput = getValue(xInput, sXPath);
        System.out.println("XPath: " + sXPath + " Output: " + Node.writeToString(xOutput, true));

        xInput = dDoc.parseString(sXml);
        sXPath = "a/y[@v]";
        xOutput = getValue(xInput, sXPath);
        System.out.println("XPath: " + sXPath + " Output: " + Node.writeToString(xOutput, true));
    }

    /**
     * Selects all elements from the input XML structure that match the given XPath-like expression.
     * For exact description of the syntax supported <code>XQuery</code>. Note! This method moves
     * the found elements from input XML to the response, so if this is method called outside
     * JavaCall, the input XML will not contain the original elements anymore.
     *
     * @param   xXml    Input XML structure. Note the the root element must be <code>XML</code> and
     *                  the actual root element of the input XML is to be placed under it.
     * @param   sXPath  XPath-like query string.
     *
     * @return  The following XML structure :
     *
     *          <pre>
          &lt;response>
            &lt;count>(number of nodes found)&lt;/found>
            &lt;data>(all found nodes are placed here)&lt;/data>
         &lt;/response>
     *  </pre>
     */
    public static int selectNodes(int xXml, String sXPath)
    {
        if (xXml == 0)
        {
            throw new IllegalArgumentException("Input XML missing.");
        }

        XMLQuery xqQuery;

        try
        {
            xqQuery = new XMLQuery(sXPath);
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Invalid input XPath expression : " + sXPath);
        }

        Document dDoc = Node.getDocument(xXml);
        int xResponse = dDoc.createElement("response");
        int xResponseData = dDoc.createElement("data", xResponse);
        int[] xaNodes = null;

        // Get the first child of XML parameter. This does not support multiple
        // children.
        xXml = Find.firstMatch(xXml, "<><>");

        if (xXml != 0)
        {
            xaNodes = xqQuery.findAllNodes(xXml);
        }

        if ((xaNodes != null) && (xaNodes.length > 0))
        {
            Node.createTextElement("count", "" + xaNodes.length, xResponse);

            for (int i = 0; i < xaNodes.length; i++)
            {
                int xNode = xaNodes[i];

                // Move the request node to the response.
                Node.unlink(xNode);
                Node.appendToChildren(xNode, xResponseData);
            }
        }
        else
        {
            Node.createTextElement("count", "0", xResponse);
        }

        return xResponse;
    }

    /**
     * Selects the first element from the input XML structure that matches the given XPath-like
     * expression. For exact description of the syntax supported <code>XQuery</code>. Note! This
     * method moves the found elements from input XML to the response, so if this is method called
     * outside JavaCall, the input XML will not contain the original elements anymore.
     *
     * @param   xXml    Input XML structure. Note the the root element must be <code>XML</code> and
     *                  the actual root element of the input XML is to be placed under it.
     * @param   sXPath  XPath-like query string.
     *
     * @return  The following XML structure :
     *
     *          <pre>
          &lt;response>
            &lt;found>(true of false)&lt;/found>
            &lt;data>(the found node is placed here)&lt;/data>
         &lt;/response>
     *  </pre>
     */
    public static int selectSingleNode(int xXml, String sXPath)
    {
        if (xXml == 0)
        {
            throw new IllegalArgumentException("Input XML missing.");
        }

        XMLQuery xqQuery;

        try
        {
            xqQuery = new XMLQuery(sXPath);
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Invalid input XPath expression : " + sXPath);
        }

        Document dDoc = Node.getDocument(xXml);
        int xResponse = dDoc.createElement("response");
        int xResponseData = dDoc.createElement("data", xResponse);
        int xNode = 0;

        // Get the first child of XML parameter. This does not support multiple
        // children.
        xXml = Find.firstMatch(xXml, "<><>");

        if (xXml != 0)
        {
            xNode = xqQuery.findNode(xXml);
        }

        if (xNode != 0)
        {
            Node.createTextElement("found", "true", xResponse);

            // Move the request node to the response.
            Node.unlink(xNode);
            Node.appendToChildren(xNode, xResponseData);
        }
        else
        {
            Node.createTextElement("found", "false", xResponse);
        }

        return xResponse;
    }
}
