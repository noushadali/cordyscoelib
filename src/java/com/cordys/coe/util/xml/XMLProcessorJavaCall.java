/**
 *  2005 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util.xml;

import com.eibus.applicationconnector.java.Tupable;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Find;
import com.eibus.xml.nom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A simple XML processing class that can be called from a Studio process. Allows multiple
 * operations within the same call and allows multiple return values. Usually this is used to
 * perform operations that are not easy in Studio or to modify the XML into a form that is easier to
 * use in Studio. Also by combining multiple operations within the same Javacall you get better
 * performance.<br/>
 * Possible commands are :
 *
 * <pre>
   &lt;copy from="/path" to="/path" [multiple="true"] [silent="true"] />
         Copies and XML element from one place to another (the source element is placed under
     the destination element). If 'multiple' attribue is set to 'true', this copies all found
     elements. Destination path must exist unless attribute 'silent' is set to 'true', in which case
     command does not do anything if the destination does not exist.

   &lt;move from="/path" to="/path" [multiple="true"] [silent="true"] />
     Same as &lt;copy/> by elements are moved from one place to another. This is faster than
     first copying elements and then deleting them.

   &lt;delete path="/path" [multiple="true"] />
     Deletes XML element from the path. If 'multiple' attribue is set to 'true', this deletes
     all found elements.

    &lt;delete-matching path="/path" match-path="./path" [multiple="true"] />
     Deletes XML element from the path that have sub-elements that match the 'match-path'.
     If 'multiple' attribue is set to 'true', this deletes all elements found with 'path'.

    &lt;delete-nonmatching path="/path" match-path="./path" [multiple="true"] />
     Deletes XML element from the path that do not have sub-elements that match the 'match-path'.
     If 'multiple' attribue is set to 'true', this deletes all elements found with 'path'.

   &lt;create path="/path" [use-exising="false" ] />
     Creates elements in the given path (if they don't already exist. If the attribute 'use-existing'
     is set to 'false' elements are always created. The path cannot have any wild-cards.

   &lt;return src-path="/path" [return-path="/path"] [multiple="true"] [move-src="true"] />
     Adds the element(s) pointed by 'src-path' to the XML structure that will be returned. If 'return-path'
     is set and is non-empty, it will be created. Return path cannot contain any wild-cards.
     This does not have to be the last command in the list. You can have multiple return commands
     for returning multiple XML structures.
     If attribute 'multiple' is set to true, all elements pointed by 'src-path' are returned.
     If attribute 'delete-src' is set to 'true', source elements are actually to the return structure
     instead of copied.

   &lt;exists path="/path" set-path="/path" true-value="" false-value="" />
     Checks if the element pointed by the given path exists and sets the value pointed by
     'set-path' to either value given by 'true-value' (element exists) or 'false-value' (element
     does not exist).
     The destination path must exists.

   &lt;count path="/path" set-path="" />
     Counts the times the given path finds an XML element and sets this count to the element or attribute
     pointed by 'set-path'. This path must exists. If the 'set-path' already exists the value is added
     to the existing value (an exception is thrown if the existing value is not a number).
     And optional sub-element &lt;exclude name="" /> can be added to exclude the given child elements from could.

   &lt;copy-value from="/path" to="/path" [default=""] [silent="true"] />
     Copies a value from the source element (or attribute) to the destination element (or attribute).
     If the source value does not exist and the default is set, the default value is used.
     Destination path must exist unless attribute 'silent' is set to 'true', in which case
     command does not do anything if the destination does not exist.

   &lt;set-value path="/path" value="" [silent="true"] />
     Sets the value of an element (or attribute) pointed by the given path to the given value.
     Destination path must exist unless attribute 'silent' is set to 'true', in which case
     command does not do anything if the destination does not exist.

   &lt;split-copy from="/path" to="/path" split-path="/path" [multiple="true"] [silent="true"] />
     Copies all elements under path 'from' to 'to' by duplicating all child elements that appear
     under the relative path 'split-path'. This commands can be used to create multiple instances
     of XML elements under the 'split-path'. See the example for more clarification.
     If attribute 'multiple' is set to 'true', all elements pointed by 'from' are copied, otherwise
     only the first one.
     Note that 'split-path' is relative to the 'from' path (starting from it), so it is a good idea
     to use the form "./a/b".
     Destination path must exist unless attribute 'silent' is set to 'true', in which case
     command does not do anything if the destination does not exist.

   &lt;for-each path="/path" var="loop-var-name" [move-src="true"]>
     ... Sub-commands
   &lt;/for-each>
     Iterates over all elements pointed by 'path', sets the found element under a loop variable element
     which name is defined with 'var' and exucutes commands located under the loop element.
     The loop variable element is re-created in the XML root in every iteration and deleted after the loop.
     Attribute 'var' cannot contain any XPath notation and it must be a valid XML element name.
     If 'multiple' attribue is set to 'true', this found elements are moved instead of copied (use this
     for more performance).


 * </pre>
 *
 * <p>All paths are relative to the first element under the input element (it is the root element),
 * so 'element' is the same as '/element' (assuming 'element' is name of the root element). Paths
 * are XPath-like expressions they can contain wild-cards, like '.', '//', e.g. '/input/a/.' and
 * they can specify attributes, e.g. '/input/a/&at;attrib'. Paths are converted to NOM match
 * expressions, so only the functionality present in NOM can be used. Examples :</p>
 *
 * <pre>
   Move the 'c' element under 'b' and return the whole structure.
   &lt;input>
      &lt;a>
                &lt;b>
           &lt;value>My Value&lt;/value>
        &lt;/b>
                &lt;c>Test2&lt;/c>
      &lt;/a>
   &lt;/input>
   &lt;commands>
      &lt;copy from="a/c" to="a/b" />
      &lt;delete from="a/c" />
      &lt;return src-path="a" />
   &lt;/commands>
   &lt;result>
      &lt;a>
                &lt;b>
           &lt;value>My Value&lt;/value>
                   &lt;c>Test2&lt;/c>
        &lt;/b>
      &lt;/a>
   &lt;/result>
 * </pre>
 *
 * <p>Delete all other elements except 'wanted-value' from 'b' and return them under element
 * 'under-b'. Also return all 'wanted-value' elements from the whole XML under element 'all-wanted'.
 * </p>
 *
 * <pre>
   &lt;input>
      &lt;a>
                &lt;b>
           &lt;value>Dummy Value&lt;/value>
                   &lt;wanted-value>My Value&lt;/wanted-value>
                   &lt;value>Dummy Value&lt;/value>
        &lt;/b>
                &lt;c>
                        &lt;wanted-value>My value2&lt;/wanted-value>
                &lt;/c>
      &lt;/a>
   &lt;/input>
   &lt;commands>
      &lt;delete from="a/b/value" multiple="true"/>
      &lt;return src-path="a/b/." return-path="under-b" />
      &lt;return src-path="//wanted-value" return-path="all-wanted" />
   &lt;/commands>
   &lt;result>
      &lt;under-b>
                &lt;wanted-value>My Value&lt;/wanted-value>
      &lt;/under-b>
      &lt;all-wanted>
                &lt;wanted-value>My Value&lt;/wanted-value>
                &lt;wanted-value>My Value2&lt;/wanted-value>
      &lt;/all-wanted>
   &lt;/result>

   Split a multilevel XML structure into unique structures.
   &lt;input>
     &lt;a>
        &lt;b>
           &lt;dummy>d1&lt;/dummy>
           &lt;c>c1&lt;/c>
                   &lt;c>c2&lt;/c>
        &lt;/b>
        &lt;b>
           &lt;dummy>d2&lt;/dummy>
                   &lt;c>c3&lt;/c>
        &lt;/b>
     &lt;/a>
   &lt;/input>
   &lt;commands>
      &lt;create path="dest" />
      &lt;split-copy from="./a/b" to="./dest" split-path="b/c" multiple="true" />
      &lt;return src-path="./dest" />
   &lt;/commands>
   &lt;result>
      &lt;dest>
        &lt;b>
           &lt;dummy>d1&lt;/dummy>
           &lt;c>c1&lt;/c>
        &lt;/b>
           &lt;dummy>d1&lt;/dummy>
                   &lt;c>c2&lt;/c>
        &lt;/b>
        &lt;b>
           &lt;dummy>d2&lt;/dummy>
                   &lt;c>c3&lt;/c>
        &lt;/b>
      &lt;/dest>
   &lt;/result>
 * </pre>
 *
 * @author  mpoyhone
 */
public class XMLProcessorJavaCall
    implements Tupable
{
    /**
     * Checks if an XML element/attribute exists.
     *
     * @param  xRoot        Input XML root node.
     * @param  sPath        Path of the element/attribute to be checked.
     * @param  sSetPath     Path of the element/attribute to be set with the result.
     * @param  sTrueValue   Value that will be set in the destination when the element exists.
     * @param  sFalseValue  Value that will be set in the destination when the element does not
     *                      exist.
     */
    public static void checkExists(int xRoot, String sPath, String sSetPath, String sTrueValue,
                                   String sFalseValue)
    {
        XMLQuery xqPath = new XMLQuery(sPath);
        XMLQuery xqSetPath = new XMLQuery(sSetPath);
        int xDest;

        xDest = xqSetPath.findNode(xRoot);

        if (xDest == 0)
        {
            throw new IllegalArgumentException("check-exists: Destination path '" + sSetPath +
                                               "' does not exist.");
        }

        int xSource = xqPath.findNode(xRoot);
        String sValue;

        if (xqPath.getQueryAttribute() == null)
        {
            sValue = ((xSource != 0) ? sTrueValue : sFalseValue);
        }
        else
        {
            if (xSource != 0)
            {
                String sAttribValue = Node.getAttribute(xSource, xqPath.getQueryAttribute());

                sValue = ((sAttribValue != null) ? sTrueValue : sFalseValue);
            }
            else
            {
                sValue = sFalseValue;
            }
        }

        if (xqSetPath.getQueryAttribute() != null)
        {
            Node.setAttribute(xDest, xqSetPath.getQueryAttribute(), sValue);
        }
        else
        {
            Node.setDataElement(xDest, "", sValue);
        }
    }

    /**
     * Copies or moves XML element(s) under the destinanation element.
     *
     * @param   xInputRoot       Input XML root node.
     * @param   sFrom            Path of the source element(s).
     * @param   sTo              Path of the destination element.
     * @param   bMultiple        If <code>true</code>, all found elements are copied/moved,
     *                           otherwise only the first.
     * @param   bSilent          If <code>true</code> exception is not thrown if the destination
     *                           does not exist.
     * @param   bDeleteOriginal  If <code>true</code> the elements are actually moved instead of
     *                           copied.
     *
     * @throws  IllegalArgumentException  Thrown if the command failed.
     */
    public static void copyXml(int xInputRoot, String sFrom, String sTo, boolean bMultiple,
                               boolean bSilent, boolean bDeleteOriginal)
                        throws IllegalArgumentException
    {
        XMLQuery xqFrom = new XMLQuery(sFrom);
        XMLQuery xqTo = new XMLQuery(sTo);
        int xDest;

        xDest = xqTo.findNode(xInputRoot);

        if (xDest == 0)
        {
            if (bSilent)
            {
                return;
            }

            throw new IllegalArgumentException("Destination path '" + sTo + "' does not exist.");
        }

        int[] xaSource;

        if (bMultiple)
        {
            xaSource = xqFrom.findAllNodes(xInputRoot);
        }
        else
        {
            xaSource = new int[1];
            xaSource[0] = xqFrom.findNode(xInputRoot);
        }

        for (int i = 0; i < xaSource.length; i++)
        {
            int xNode = xaSource[i];

            if (xNode != 0)
            {
                int xNewNode;

                if (!bDeleteOriginal)
                {
                    xNewNode = Node.duplicate(xNode);
                }
                else
                {
                    Node.unlink(xNode);
                    xNewNode = xNode;
                }

                xNewNode = Node.appendToChildren(xNewNode, xDest);
            }
        }
    }

    /**
     * Copies XML element/attribute value under the destinanation element/attribute.
     *
     * @param  xRoot     Input XML root node.
     * @param  sFrom     Path of the source element/attribute.
     * @param  sTo       Path of the destination element/attribute.
     * @param  sDefault  Default value to be used when the source does not exist.
     * @param  bSilent   If <code>true</code> exception is not thrown if the destination does not
     *                   exist.
     */
    public static void copyXmlValue(int xRoot, String sFrom, String sTo, String sDefault,
                                    boolean bSilent)
    {
        XMLQuery xqFrom = new XMLQuery(sFrom);
        XMLQuery xqTo = new XMLQuery(sTo);
        int xDest;

        xDest = xqTo.findNode(xRoot);

        if (xDest == 0)
        {
            if (bSilent)
            {
                return;
            }

            throw new IllegalArgumentException("Destination path '" + sTo + "' does not exist.");
        }

        // int xSource = xqFrom.findNode(xRoot);
        String sValue;

        if (xqFrom.getQueryAttribute() != null)
        {
            sValue = Node.getAttribute(xDest, xqFrom.getQueryAttribute());
        }
        else
        {
            sValue = Node.getData(xDest);
        }

        if (sValue == null)
        {
            sValue = sDefault;
        }

        if (xqTo.getQueryAttribute() != null)
        {
            Node.setAttribute(xDest, xqTo.getQueryAttribute(), sValue);
        }
        else
        {
            Node.setDataElement(xDest, "", sValue);
        }
    }

    /**
     * Sets the number of elements pointed by the path to the destination element/attribute.
     *
     * @param  xRoot             Input XML root node.
     * @param  sPath             Path of the elements to be counted.
     * @param  sSetPath          Path of the destination element/attribute to be set.
     * @param  mExcludeElements  An optinal map of element names that will be excluded from the
     *                           count.
     */
    public static void countXml(int xRoot, String sPath, String sSetPath,
                                Map<String, Boolean> mExcludeElements)
    {
        XMLQuery xqPath = new XMLQuery(sPath);
        XMLQuery xqSetPath = new XMLQuery(sSetPath);
        int xDest;

        xDest = xqSetPath.findNode(xRoot);

        if (xDest == 0)
        {
            throw new IllegalArgumentException("Destination path '" + sSetPath +
                                               "' does not exist.");
        }

        int[] xaSource = xqPath.findAllNodes(xRoot);
        long lCount;

        if (mExcludeElements != null)
        {
            lCount = 0;

            for (int i = 0; i < xaSource.length; i++)
            {
                int xNode = xaSource[i];
                String sNodeName = Node.getName(xNode);

                if (sNodeName == null)
                {
                    continue;
                }

                if (mExcludeElements.get(sNodeName) == null)
                {
                    lCount++;
                }
            }
        }
        else
        {
            lCount = xaSource.length;
        }

        String sCurrentValue;

        if (xqSetPath.getQueryAttribute() != null)
        {
            sCurrentValue = Node.getAttribute(xDest, xqSetPath.getQueryAttribute());
        }
        else
        {
            sCurrentValue = Node.getData(xDest);
        }

        if (sCurrentValue != null)
        {
            if (sCurrentValue.length() > 0)
            {
                long lCurrentCount;

                try
                {
                    lCurrentCount = Long.parseLong(sCurrentValue);
                }
                catch (NumberFormatException e)
                {
                    throw new IllegalArgumentException("Invalid existing current count value: " +
                                                       sCurrentValue);
                }

                lCount += lCurrentCount;
            }
        }

        String sValue = "" + lCount;

        if (xqSetPath.getQueryAttribute() != null)
        {
            Node.setAttribute(xDest, xqSetPath.getQueryAttribute(), sValue);
        }
        else
        {
            Node.setDataElement(xDest, "", sValue);
        }
    }

    /**
     * Creates an XML element in the input XML.
     *
     * @param  xRoot         Input XML root node.
     * @param  sPath         Path to be created under the input XML.
     * @param  bUseExisting  If <code>true</code> existing elements are used when possible,
     *                       otherwise new elements are always created.
     */
    public static void createXml(int xRoot, String sPath, boolean bUseExisting)
    {
        String[] saPathElements = XMLHelpers.parsePath(sPath);

        XMLHelpers.createPath(saPathElements, xRoot, bUseExisting);
    }

    /**
     * Deletes XML element(s).
     *
     * @param   xInputRoot  Input XML root node.
     * @param   sPath       Path of the element(s) to be deleted.
     * @param   bMultiple   If <code>true</code>, all found elements are deleted, otherwise only the
     *                      first.
     *
     * @throws  IllegalArgumentException  Thrown if the command failed.
     */
    public static void deleteXml(int xInputRoot, String sPath, boolean bMultiple)
                          throws IllegalArgumentException
    {
        XMLQuery xqPath = new XMLQuery(sPath);

        int[] xaSource;

        if (bMultiple)
        {
            xaSource = xqPath.findAllNodes(xInputRoot);
        }
        else
        {
            xaSource = new int[1];
            xaSource[0] = xqPath.findNode(xInputRoot);
        }

        for (int i = 0; i < xaSource.length; i++)
        {
            int xNode = xaSource[i];

            if (xNode != 0)
            {
                Node.unlink(xNode);
                Node.delete(xNode);
            }
        }
    }

    /**
     * Deletes XML matching or non-matching element(s).
     *
     * @param   xInputRoot       Input XML root node.
     * @param   sPath            Path of the element(s) to be deleted.
     * @param   sMatchPath       Path for sub-elements that detemines if the element matched by
     *                           'path' is to be deleted.
     * @param   bMultiple        If <code>true</code>, all found elements are deleted, otherwise
     *                           only the first.
     * @param   bDeleteMatching  If <code>true</code> all elements that have matching sub-elements
     *                           for the the match path are deleted, otherwise all elements that do
     *                           not have matching sub-elements are deleted.
     *
     * @throws  IllegalArgumentException  Thrown if the command failed.
     */
    public static void deleteXmlWithMatch(int xInputRoot, String sPath, String sMatchPath,
                                          boolean bMultiple, boolean bDeleteMatching)
                                   throws IllegalArgumentException
    {
        XMLQuery xqPath = new XMLQuery(sPath);
        XMLQuery xqMatchPath = new XMLQuery(sMatchPath);

        int[] xaSource;

        if (bMultiple)
        {
            xaSource = xqPath.findAllNodes(xInputRoot);
        }
        else
        {
            xaSource = new int[1];
            xaSource[0] = xqPath.findNode(xInputRoot);
        }

        for (int i = 0; i < xaSource.length; i++)
        {
            int xNode = xaSource[i];

            if (xNode != 0)
            {
                int xMatchNode = xqMatchPath.findNode(xNode);
                // boolean bDelete = false;

                if ((bDeleteMatching && (xMatchNode != 0)) ||
                        (!bDeleteMatching && (xMatchNode == 0)))
                {
                    Node.unlink(xNode);
                    Node.delete(xNode);
                    xNode = 0;
                    xMatchNode = 0;
                }
            }
        }
    }

    /**
     * A simple test method.
     *
     * @param  args  [0] = Name of an input file. Must be like: &lt;root>&lt;input>input
     *               XML&lt;/input>&lt;commands>commands&lt;/commands>&lt;/root>
     */
    public static void main(String[] args)
    {
        Document dDoc = new Document();

        try
        {
            int xFile = dDoc.load(args[0]);
            int xInput = Find.firstMatch(xFile, "<><input>");
            int xCommands = Find.firstMatch(xFile, "<><commands>");
            int xResult;

            xResult = processXml(xInput, xCommands);

            System.out.println(Node.writeToString(xResult, true));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Main method for processing XML commands. This will be called from the Javacall method.
     *
     * @param   xInput     Input XML root node.
     * @param   xCommands  XML structure that contains the commands to be executed.
     *
     * @return  XML structure containg the XML returned by comanads.
     *
     * @throws  IllegalArgumentException  Thrown if the operation failed.
     */
    public static int processXml(int xInput, int xCommands)
                          throws IllegalArgumentException
    {
        if (xInput == 0)
        {
            throw new IllegalArgumentException("Input XML is missing.");
        }

        if (xCommands == 0)
        {
            throw new IllegalArgumentException("Command list is missing.");
        }

        // Create our own input node and move all input root nodes under it, so
        // we can delete this safely.
        int xRoot = Node.getDocument(xInput).createElement("input");
        int xTmp;

        xTmp = Node.getFirstElement(xInput);

        while (xTmp != 0)
        {
            int xNext = Node.getNextSibling(xTmp);

            Node.unlink(xTmp);
            Node.appendToChildren(xTmp, xRoot);

            xTmp = xNext;
        }

        int xResult = Node.getDocument(xInput).createElement("result");
        boolean bSuccess = false;

        try
        {
            processXmlCommands(xRoot, xResult, xCommands);
            bSuccess = true;
        }
        finally
        {
            Node.delete(xRoot);
            xRoot = 0;

            if (!bSuccess)
            {
                // We have an exception, so delete the output node.
                Node.delete(xResult);
                xResult = 0;
            }
        }

        return xResult;
    }

    /**
     * Copies element(s) to the return XML structure.
     *
     * @param  xRoot        Input XML root node.
     * @param  xReturn      Return XML root node.
     * @param  sSrcPath     Path of the element(s) to be returned.
     * @param  sReturnPath  Path in the return XML under which the elements are copied. If an empty
     *                      string, the root is used.
     * @param  bMultiple    If <code>true</code>, all found elements are returned, otherwise only
     *                      the first.
     * @param  bDeleteSrc   If <code>true</code>, elements are moved instead of copied.
     */
    public static void returnXml(int xRoot, int xReturn, String sSrcPath, String sReturnPath,
                                 boolean bMultiple, boolean bDeleteSrc)
    {
        int xReturnDest = xReturn;

        if (sReturnPath.length() > 0)
        {
            String[] saPathElements = XMLHelpers.parsePath(sReturnPath);

            xReturnDest = XMLHelpers.createPath(saPathElements, xRoot, true);
        }

        XMLQuery xqFrom = new XMLQuery(sSrcPath);
        int[] xaSource;

        if (bMultiple)
        {
            xaSource = xqFrom.findAllNodes(xRoot);
        }
        else
        {
            xaSource = new int[1];
            xaSource[0] = xqFrom.findNode(xRoot);
        }

        for (int i = 0; i < xaSource.length; i++)
        {
            int xNode = xaSource[i];

            if (xNode != 0)
            {
                int xNewNode;

                if (!bDeleteSrc)
                {
                    xNewNode = Node.duplicate(xNode);
                }
                else
                {
                    Node.unlink(xNode);
                    xNewNode = xNode;
                }

                xNewNode = Node.appendToChildren(xNewNode, xReturnDest);
            }
        }
    }

    /**
     * Executes a for-each loop.
     *
     * @param  xInputRoot        Input XML root node.
     * @param  xResult           Result XML root node.
     * @param  xForEachCommands  XML structure containing commands to be executed.
     * @param  sPath             Path elements that will be iterated over.
     * @param  sVariable         Name of the loop variable that will be set in the input XML in each
     *                           iteration.
     * @param  bMoveSrc          If <code>true</code> elements are moved to the loop variable
     *                           instead of copied.
     */
    public static void runForEach(int xInputRoot, int xResult, int xForEachCommands, String sPath,
                                  String sVariable, boolean bMoveSrc)
    {
        XMLQuery xqFrom = new XMLQuery(sPath);
        XMLQuery xqVariable = new XMLQuery("./" + sVariable);
        // int xDest;

        // Find all source nodes.
        int[] xaSource;

        xaSource = xqFrom.findAllNodes(xInputRoot);

        for (int i = 0; i < xaSource.length; i++)
        {
            int xChild = xaSource[i];
            int xVar;
            int xVarChild;

            // Create the loop variable.
            xVar = xqVariable.findNode(xInputRoot);

            if (xVar != 0)
            {
                // Delete the existing node.
                Node.unlink(xVar);
                Node.delete(xVar);
            }
            xVar = Node.createElement(sVariable, xInputRoot);

            if (xVar == 0)
            {
                throw new IllegalArgumentException("Unable to create loop variable " + sVariable);
            }

            if (!bMoveSrc)
            {
                xVarChild = Node.duplicate(xChild);
            }
            else
            {
                Node.unlink(xChild);
                xVarChild = xChild;
            }

            xVarChild = Node.appendToChildren(xVarChild, xVar);

            processXmlCommands(xInputRoot, xResult, xForEachCommands);
        }

        // Delete the loop variable.
        int xVar = xqVariable.findNode(xInputRoot);

        if (xVar != 0)
        {
            Node.unlink(xVar);
            Node.delete(xVar);
            xVar = 0;
        }
    }

    /**
     * Sets an XML element/attribute value.
     *
     * @param  xRoot    Input XML root node.
     * @param  sPath    Path of the element to be set.
     * @param  sValue   Value to be set.
     * @param  bSilent  If <code>true</code> this does not thrown an exception when the path does
     *                  not exist.
     */
    public static void setXmlValue(int xRoot, String sPath, String sValue, boolean bSilent)
    {
        XMLQuery xqPath = new XMLQuery(sPath);
        int xDest;

        xDest = xqPath.findNode(xRoot);

        if (xDest == 0)
        {
            if (bSilent)
            {
                return;
            }

            throw new IllegalArgumentException("set-value: Destination path '" + sPath +
                                               "' does not exist.");
        }

        if (xqPath.getQueryAttribute() != null)
        {
            Node.setAttribute(xDest, xqPath.getQueryAttribute(), sValue);
        }
        else
        {
            Node.setDataElement(xDest, "", sValue);
        }
    }

    /**
     * Splits an XML structure by leaf elements. See the example for more explanation.
     *
     * @param   xInputRoot  Input XML root node.
     * @param   sFrom       Root path of the source element(s).
     * @param   sTo         Path of the destination element under which the resulting XML strucures
     *                      will be copied.
     * @param   sSplitPath  Relative path under 'from' that specifies the leaf elements.
     * @param   bMultiple   If <code>true</code>, all found 'from' elements are processed, otherwise
     *                      only the first.
     * @param   bSilent     If <code>true</code> exception is not thrown if the destination does not
     *                      exist.
     *
     * @throws  IllegalArgumentException  Thrown if the command failed.
     */
    public static void splitCopyXml(int xInputRoot, String sFrom, String sTo, String sSplitPath,
                                    boolean bMultiple, boolean bSilent)
                             throws IllegalArgumentException
    {
        XMLQuery xqFrom = new XMLQuery(sFrom);
        XMLQuery xqTo = new XMLQuery(sTo);
        XMLQuery xqSplit = new XMLQuery(sSplitPath);
        int xDest;

        xDest = xqTo.findNode(xInputRoot);

        if (xDest == 0)
        {
            if (bSilent)
            {
                return;
            }

            throw new IllegalArgumentException("Destination path '" + sTo + "' does not exist.");
        }

        // First find all split root nodes.
        int[] xaSource;

        if (bMultiple)
        {
            xaSource = xqFrom.findAllNodes(xInputRoot);
        }
        else
        {
            xaSource = new int[1];
            xaSource[0] = xqFrom.findNode(xInputRoot);
        }

        // This will map from node to a map that contains all path that this
        // node is part of. Path is identified by the leaf node handle.
        Map<Integer, Map<Integer, Boolean>> mPathMap = new HashMap<Integer, Map<Integer, Boolean>>();

        // A simple data structure to keep path information in the list.
        /**
         * DOCUMENTME.
         *
         * @author  $author$
         */
        class Path
        {
            /**
             * ID of this path.
             */
            Integer iPathId;
            /**
             * Root XML element of this path.
             */
            int xPathRoot;
        }

        List<Path> lPathList = new ArrayList<Path>(xaSource.length);

        // From each root node find the split leaf node and find a path to the root.
        // Also a add all nodes belonging to that path to the map.
        for (int i = 0; i < xaSource.length; i++)
        {
            int xPathRoot = xaSource[i];

            if (xPathRoot == 0)
            {
                continue;
            }

            // Find to path from each leaf to its root.
            int[] xaPathLeafs = xqSplit.findAllNodes(xPathRoot);

            for (int j = 0; j < xaPathLeafs.length; j++)
            {
                int xLeaf = xaPathLeafs[j];
                int xCurrent = xLeaf;
                Path pPath = new Path();

                pPath.iPathId = new Integer(xLeaf);
                pPath.xPathRoot = xPathRoot;
                lPathList.add(pPath);

                while (true)
                {
                    if (xCurrent == 0)
                    {
                        throw new IllegalArgumentException("split-copy: Path root node not found!");
                    }

                    Integer iCurrentId = new Integer(xCurrent);
                    Map<Integer, Boolean> mNodeMap = mPathMap.get(iCurrentId);

                    if (mNodeMap == null)
                    {
                        mNodeMap = new HashMap<Integer, Boolean>();
                        mPathMap.put(iCurrentId, mNodeMap);
                    }

                    mNodeMap.put(pPath.iPathId, Boolean.TRUE);

                    if (xCurrent == xPathRoot)
                    {
                        // Stop at the path root.
                        break;
                    }

                    xCurrent = Node.getParent(xCurrent);
                }
            }
        }

        // Iterate over all paths and create the nodes that are part of this path in the
        // destination. If a child node is not part of any path it is cloned completely to the
        // destination, as we probably want the extra nodes.
        for (Iterator<Path> iIter = lPathList.iterator(); iIter.hasNext();)
        {
            Path pPath = iIter.next();

            cloneXmlPath(pPath.xPathRoot, xDest, pPath.iPathId, mPathMap);
        }
    }

    /**
     * Creates all XML elements specified in the given path.
     *
     * @param  xRoot     Path root XML element.
     * @param  xDest     The XML elements will be created under this element.
     * @param  iPathId   ID of the path to be created.
     * @param  mPathMap  Map containing XML structure path information.
     */
    private static void cloneXmlPath(int xRoot, int xDest, Integer iPathId,
                                     Map<Integer, Map<Integer, Boolean>> mPathMap)
    {
        int xClonedRoot;

        xClonedRoot = Node.appendToChildren(Node.clone(xRoot, false), xDest);

        for (int xChild = Node.getFirstChild(xRoot); xChild != 0;
                 xChild = Node.getNextSibling(xChild))
        {
            Integer iChildId = new Integer(xChild);
            Map<Integer, Boolean> mNodeMap = mPathMap.get(iChildId);

            if ((mNodeMap == null) || (mNodeMap.size() == 0))
            {
                // This node is not part of any path, so clone it completely.
                Node.appendToChildren(Node.clone(xChild, true), xClonedRoot);
                continue;
            }

            if (mNodeMap.containsKey(iPathId))
            {
                // This node is part of this path, so clone it and process the children.
                cloneXmlPath(xChild, xClonedRoot, iPathId, mPathMap);
            }
        }
    }

    /**
     * An utility method that returns a command attribute as boolean.
     *
     * @param   xNode        Command XML node.
     * @param   sCmdName     Command name used for error exception.
     * @param   sAttribName  Name of the attribute to be fetched.
     * @param   sDefault     Default value to be used if the attribute does not exist.
     * @param   bMandatory   If set to <code>true</code> and the attribute does not exist, an
     *                       exception is thrown.
     *
     * @return  The found attribute.
     */
    private static boolean getCmdBooleanParam(int xNode, String sCmdName, String sAttribName,
                                              String sDefault, boolean bMandatory)
    {
        String sValue = Node.getAttribute(xNode, sAttribName);

        if (sValue == null)
        {
            if (bMandatory)
            {
                throw new IllegalArgumentException("Mandatory attribute '" + sAttribName +
                                                   "' missing for element '" + sCmdName + "'");
            }

            sValue = sDefault;
        }

        if (sValue.equals("true"))
        {
            return true;
        }
        else if (sValue.equals("false"))
        {
            return false;
        }
        else
        {
            throw new IllegalArgumentException("Illegal boolean value '" + sValue +
                                               "'. Use 'true' or 'false'.");
        }
    }

    /**
     * An utility method that returns a command attribute as String. The attribute must exists.
     *
     * @param   xNode        Command XML node.
     * @param   sCmdName     Command name used for error exception.
     * @param   sAttribName  Name of the attribute to be fetched.
     *
     * @return  The found attribute.
     */
    private static String getCmdStringParam(int xNode, String sCmdName, String sAttribName)
    {
        String sValue = Node.getAttribute(xNode, sAttribName);

        if (sValue == null)
        {
            throw new IllegalArgumentException("Mandatory attribute '" + sAttribName +
                                               "' missing for element '" + sCmdName + "'");
        }

        return sValue;
    }

    /**
     * Executes the given commands by calling the respective methods in this class.
     *
     * @param   xRoot      Root input XML element.
     * @param   xResult    XML that will receive the output returned by the commands.
     * @param   xCommands  XML structure that contains the commands to be executed.
     *
     * @throws  IllegalArgumentException  Thrown if the execution failed.
     */
    private static void processXmlCommands(int xRoot, int xResult, int xCommands)
                                    throws IllegalArgumentException
    {
        int xCommand = Node.getFirstElement(xCommands);

        while (xCommand != 0)
        {
            String sName = Node.getLocalName(xCommand);

            if (sName == null)
            {
                continue;
            }

            if (sName.equals("copy"))
            {
                String sFrom = getCmdStringParam(xCommand, sName, "from");
                String sTo = getCmdStringParam(xCommand, sName, "to");
                boolean bMultiple = getCmdBooleanParam(xCommand, sName, "multiple", "false", false);
                boolean bSilent = getCmdBooleanParam(xCommand, sName, "silent", "false", false);

                copyXml(xRoot, sFrom, sTo, bMultiple, bSilent, false);
            }
            else if (sName.equals("move"))
            {
                String sFrom = getCmdStringParam(xCommand, sName, "from");
                String sTo = getCmdStringParam(xCommand, sName, "to");
                boolean bMultiple = getCmdBooleanParam(xCommand, sName, "multiple", "false", false);
                boolean bSilent = getCmdBooleanParam(xCommand, sName, "silent", "false", false);

                copyXml(xRoot, sFrom, sTo, bMultiple, bSilent, true);
            }
            else if (sName.equals("delete"))
            {
                String sPath = getCmdStringParam(xCommand, sName, "path");
                boolean bMultiple = getCmdBooleanParam(xCommand, sName, "multiple", "false", false);

                deleteXml(xRoot, sPath, bMultiple);
            }
            else if (sName.equals("delete-matching"))
            {
                String sPath = getCmdStringParam(xCommand, sName, "path");
                String sMatchPath = getCmdStringParam(xCommand, sName, "match-path");
                boolean bMultiple = getCmdBooleanParam(xCommand, sName, "multiple", "false", false);

                deleteXmlWithMatch(xRoot, sPath, sMatchPath, bMultiple, true);
            }
            else if (sName.equals("delete-nonmatching"))
            {
                String sPath = getCmdStringParam(xCommand, sName, "path");
                String sMatchPath = getCmdStringParam(xCommand, sName, "match-path");
                boolean bMultiple = getCmdBooleanParam(xCommand, sName, "multiple", "false", false);

                deleteXmlWithMatch(xRoot, sPath, sMatchPath, bMultiple, false);
            }
            else if (sName.equals("create"))
            {
                String sPath = getCmdStringParam(xCommand, sName, "path");
                boolean bUseExisting = getCmdBooleanParam(xCommand, sName, "use-existing", "true",
                                                          false);

                createXml(xRoot, sPath, bUseExisting);
            }
            else if (sName.equals("return"))
            {
                String sSrcPath = getCmdStringParam(xCommand, sName, "src-path");
                String sReturnPath = Node.getAttribute(xCommand, "return-path", "");
                boolean bMultiple = getCmdBooleanParam(xCommand, sName, "multiple", "false", false);
                boolean bDeleteSrc = getCmdBooleanParam(xCommand, sName, "move-src", "true", false);

                returnXml(xRoot, xResult, sSrcPath, sReturnPath, bMultiple, bDeleteSrc);
            }
            else if (sName.equals("exists"))
            {
                String sPath = getCmdStringParam(xCommand, sName, "path");
                String sSetPath = getCmdStringParam(xCommand, sName, "set-path");
                String sTrueValue = Node.getAttribute(xCommand, "true-value", "true");
                String sFalseValue = Node.getAttribute(xCommand, "false-value", "false");

                checkExists(xRoot, sPath, sSetPath, sTrueValue, sFalseValue);
            }
            else if (sName.equals("count"))
            {
                String sPath = getCmdStringParam(xCommand, sName, "path");
                String sSetPath = getCmdStringParam(xCommand, sName, "set-path");
                Map<String, Boolean> mExcludeMap = null;
                int[] xaExcludeNodes = Find.match(xCommand, "<><exclude>");

                for (int i = 0; i < xaExcludeNodes.length; i++)
                {
                    int xNode = xaExcludeNodes[i];
                    String sExcludeName = Node.getAttribute(xNode, "name");

                    if (sExcludeName == null)
                    {
                        throw new IllegalArgumentException("Attribute 'name' missing for exclude element.");
                    }

                    if (mExcludeMap == null)
                    {
                        mExcludeMap = new HashMap<String, Boolean>();
                    }

                    mExcludeMap.put(sExcludeName, Boolean.TRUE);
                }

                countXml(xRoot, sPath, sSetPath, mExcludeMap);
            }
            else if (sName.equals("copy-value"))
            {
                String sFrom = getCmdStringParam(xCommand, sName, "from");
                String sTo = getCmdStringParam(xCommand, sName, "to");
                String sDefault = Node.getAttribute(xCommand, "default", "");
                boolean bSilent = getCmdBooleanParam(xCommand, sName, "silent", "false", false);

                copyXmlValue(xRoot, sFrom, sTo, sDefault, bSilent);
            }
            else if (sName.equals("set-value"))
            {
                String sPath = getCmdStringParam(xCommand, sName, "path");
                String sValue = getCmdStringParam(xCommand, sName, "value");
                boolean bSilent = getCmdBooleanParam(xCommand, sName, "silent", "false", false);

                setXmlValue(xRoot, sPath, sValue, bSilent);
            }
            else if (sName.equals("split-copy"))
            {
                String sFrom = getCmdStringParam(xCommand, sName, "from");
                String sTo = getCmdStringParam(xCommand, sName, "to");
                String sSplitPath = getCmdStringParam(xCommand, sName, "split-path");
                boolean bMultiple = getCmdBooleanParam(xCommand, sName, "multiple", "false", false);
                boolean bSilent = getCmdBooleanParam(xCommand, sName, "silent", "false", false);

                splitCopyXml(xRoot, sFrom, sTo, sSplitPath, bMultiple, bSilent);
            }
            else if (sName.equals("for-each"))
            {
                String sPath = getCmdStringParam(xCommand, sName, "path");
                String sVariable = getCmdStringParam(xCommand, sName, "var");
                boolean bMoveSrc = getCmdBooleanParam(xCommand, sName, "move-src", "false", false);

                runForEach(xRoot, xResult, xCommand, sPath, sVariable, bMoveSrc);
            }
            else
            {
                throw new IllegalArgumentException("Invalid command '" + sName + "'");
            }

            xCommand = Node.getNextSibling(xCommand);
        }
    }
}
