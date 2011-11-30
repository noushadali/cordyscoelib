package com.cordys.coe.util.xml;

import com.eibus.xml.nom.Find;
import com.eibus.xml.nom.FindHandler;
import com.eibus.xml.nom.Node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Holds the XQL query path. This is implemented as a separate class to contain a possible attribute
 * element. Also provides support for paths of form 'path/to/node' or 'path/to/node/&at;attribute'.
 * Currently handles XPath formats :
 *
 * <pre>
     - /a/b/c     Selection from root
     - a/b/c     Selection from the current node (must be 'a').
     - ./a/b/c   Selection with the current node identifier. -
     - ../a/b/c  Selection with a parent node identifier.
     - a/* /c    Selection with wildcards (without the space).
     - //c       Selection of any subtree.
     - a/b/c[&at;attr] Selection of a node with the given attribute.
     - a/b/c[&at;attr='abc'] Selection of a node with the given attribute having the given value.
 * </pre>
 *
 * @author  mpoyhone
 */
@SuppressWarnings("deprecation")
public class XMLQuery
{
    /**
     * Regex for parsing attributes of form [ &at;a = 'b'].
     */
    private static final Pattern pElementAttribPattern = Pattern.compile("\\s*@([^\\s=]+)\\s*(=\\s*'([^']*)')?");
    /**
     * Indicates wheter the path started at with / or not.
     */
    protected boolean bStartsAtRoot = false;
    /**
     * Holds the query attribute name or null the query does not end in an attribute.
     */
    protected String sQueryAttribute;
    /**
     * Holds the XQL query path string.
     */
    protected String sQueryPath;

    /**
     * Constructs a new query object.
     */
    public XMLQuery()
    {
    }

    /**
     * Constructs a new query object by the query string.
     *
     * @param  sPath  The XPath like path
     */
    public XMLQuery(String sPath)
    {
        composeQuery(sPath);
    }

    /**
     * Composes the query from form 'path/to/node' or 'path/to/node/&at;attribute'
     *
     * @param  sQueryPath  The query path to be parsed.
     */
    public void composeQuery(String sQueryPath)
    {
        if (sQueryPath.equals("/"))
        {
            // This is a special case, so treat it here.
            this.sQueryPath = "<>";
            bStartsAtRoot = true;
            return;
        }

        int iPrev = 0;
        StringBuffer sbQuery = new StringBuffer(128);

        // Find the path elements separated by '/' characters.
        while (true)
        {
            int iNext = sQueryPath.indexOf('/', iPrev);
            String sValue;

            if (iNext != -1)
            {
                sValue = sQueryPath.substring(iPrev, iNext);
            }
            else
            {
                sValue = sQueryPath.substring(iPrev);
            }

            if (sValue.length() == 0)
            {
                sValue = null;
            }

            // Check if this is a // notation or the root slash (sValue would be empty)/
            if (sValue == null)
            {
                // Check that this isn't the first character.
                if (iPrev > 0)
                {
                    // This is the // notation for any child so use ? notation.
                    sbQuery.append("?");
                }
                else
                {
                    // Set the flag indicating that we must find the root first.
                    bStartsAtRoot = true;
                }
            }

            boolean bHandled = false;

            // Check the first character.
            switch ((sValue != null) ? sValue.charAt(0) : 0)
            {
                case '\0':
                    // Exit kludge.
                    bHandled = true;
                    break;

                case '@':
                    // This is a query attribute.
                    sQueryAttribute = sValue.substring(1);
                    bHandled = true;
                    break;

                case '*':
                    sbQuery.append("<>");
                    bHandled = true;
                    break;

                case '.':

                    if (sValue.equals("."))
                    {
                        // This is the current node, so use <> notation.
                        sbQuery.append("<>");
                        bHandled = true;
                    }
                    else if (sValue.equals(".."))
                    {
                        // Check if we need a separator.
                        if (sbQuery.length() > 0)
                        {
                            switch (sbQuery.charAt(sbQuery.length() - 1))
                            {
                                case '>':
                                case '.':
                                    // These are legal separators.
                                    break;

                                default:
                                    // Otherwise add .
                                    sbQuery.append('.');
                                    break;
                            }
                        }
                        sbQuery.append("parent");
                        bHandled = true;
                    }
                    break;

                default:
                {
                    // This is a normal path element, so add it to the path.
                    int iParamStartPos = sValue.indexOf('[');
                    int iParamEndPos = ((iParamStartPos != -1) ? sValue.lastIndexOf(']') : (-1));

                    // Check if there is an attribute specification (e.g. /node[@attr='abc'].
                    if ((iParamStartPos != -1) && (iParamEndPos > iParamStartPos))
                    {
                        Matcher mMatcher = pElementAttribPattern.matcher(sValue.substring(iParamStartPos +
                                                                                          1,
                                                                                          iParamEndPos));

                        // Check if the pattern matched.
                        if (mMatcher.matches())
                        {
                            String sAttrName = mMatcher.group(1);
                            String sAttrValue = mMatcher.group(3);

                            sbQuery.append("<").append(sValue.substring(0, iParamStartPos))
                                   .append(" ").append(sAttrName);

                            if (sAttrValue != null)
                            {
                                sbQuery.append("=\"").append(sAttrValue).append("\"");
                            }
                            sbQuery.append(">");
                            bHandled = true;
                        }
                    }
                }
                break;
            }

            if (!bHandled)
            {
                // Check if we have an attribute in the name.
                int iAttribPos;

                if ((iAttribPos = sValue.indexOf('@')) >= 0)
                {
                    sQueryAttribute = sValue.substring(iAttribPos + 1);
                    sValue = sValue.substring(0, iAttribPos);

                    // The attribute must be set in the last element in the path.
                    if (iNext != -1)
                    {
                        throw new IllegalArgumentException("Query attribute can be set only for the last element.");
                    }
                }

                // The value was not a special element, so add it as a normal element.
                sbQuery.append("<").append(sValue).append(">");
            }

            // Check if this was the last element.
            if (iNext == -1)
            {
                break;
            }

            iPrev = iNext + 1;
        }

        // Set the query path.
        this.sQueryPath = sbQuery.toString();

        if (sQueryAttribute != null)
        {
            // If we have an attribute and the query part is empty or it is just '.',
            // can just forget the path and just the node passed to getValue, etc.
            if ((sQueryPath.length() == 0) || sQueryPath.equals("<>"))
            {
                sQueryPath = null;
            }
        }
    }

    /**
     * Returns the all nodes pointed by this query path.
     *
     * @param   iNode  The XML structure root node to be searched.
     *
     * @return  The nodes pointed by this query path.
     */
    public int[] findAllNodes(int iNode)
    {
        if (iNode == 0)
        {
            return null;
        }

        if (sQueryPath == null)
        {
            throw new UnsupportedOperationException("Query path is not set.");
        }

        // Find the root node if that is needed.
        if (bStartsAtRoot)
        {
            int iParent;

            while ((iParent = Node.getParent(iNode)) != 0)
            {
                iNode = iParent;
            }
        }

        int[] iaResultNodes;

        if (sQueryAttribute == null)
        {
            // Find the node pointed by the query path.
            iaResultNodes = Find.match(iNode, sQueryPath);
        }
        else
        {
            // We have an attribute parameter, so we must select
            // only nodes that have that attribute.
            AttributeFinder afFinder = new AttributeFinder(sQueryAttribute, null, false);

            Find.match(iNode, sQueryPath, afFinder);
            iaResultNodes = afFinder.getAllMatches();
        }

        return iaResultNodes;
    }

    /**
     * Returns the node pointed by this query path.
     *
     * @param   iNode  The XML structure root node to be searched.
     *
     * @return  The node pointed by this query path.
     */
    public int findNode(int iNode)
    {
        return findNode(iNode, false);
    }

    /**
     * Returns the node pointed by this query path.
     *
     * @param   iNode             The XML structure root node to be searched.
     * @param   bMatchAttributes  If true and query has attributes, only nodes with matching
     *                            attributes are searched.
     *
     * @return  The node pointed by this query path.
     */
    public int findNode(int iNode, boolean bMatchAttributes)
    {
        if (iNode == 0)
        {
            return 0;
        }

        if (sQueryPath == null)
        {
            throw new UnsupportedOperationException("Query path is not set.");
        }

        // Find the root node if that is needed.
        if (bStartsAtRoot)
        {
            int iParent;

            while ((iParent = Node.getParent(iNode)) != 0)
            {
                iNode = iParent;
            }
        }

        int iQueryNode;

        if ((sQueryAttribute == null) || !bMatchAttributes)
        {
            // Find the node pointed by the query path.
            iQueryNode = Find.firstMatch(iNode, sQueryPath);
        }
        else
        {
            // We have an attribute parameter, so we must select
            // only nodes that have that attribute.
            AttributeFinder afFinder = new AttributeFinder(sQueryAttribute, null, true);

            Find.match(iNode, sQueryPath, afFinder);
            iQueryNode = afFinder.getFirstMatch();
        }

        return iQueryNode;
    }

    /**
     * Returns the node or attribute value specified by this query path.
     *
     * @param   iNode          The XML structure root node to be searched.
     * @param   sDefaultValue  The default value returned if the node/attribute was not found.
     *
     * @return  The node/attribute value or the default value when no match was found. Returns null
     *          when the node was not found.
     */
    public String findValue(int iNode, String sDefaultValue)
    {
        int iQueryNode;
        String sValue;

        // Find the node pointed by the query path.
        iQueryNode = ((sQueryPath != null) ? findNode(iNode) : iNode);

        if (iQueryNode == 0)
        {
            return null;
        }

        if (sQueryAttribute != null)
        {
            // Get the node's attribute value.
            sValue = Node.getAttribute(iQueryNode, sQueryAttribute);
        }
        else
        {
            // Get node's data.
            sValue = Node.getData(iQueryNode);
        }

        // If the value was not found, use default.
        if (sValue == null)
        {
            sValue = sDefaultValue;
        }

        return sValue;
    }

    /**
     * Returns the query attribute name.
     *
     * @return  The attribute name.
     */
    public String getQueryAttribute()
    {
        return sQueryAttribute;
    }

    /**
     * Returns the query path element.
     *
     * @return  The path string.
     */
    public String getQueryPath()
    {
        return sQueryPath;
    }

    /**
     * Sets the query attribute name.
     *
     * @param  sQueryAttribute  The new attribute name.
     */
    public void setQueryAttribute(String sQueryAttribute)
    {
        this.sQueryAttribute = sQueryAttribute;
    }

    /**
     * Sets the query path element.
     *
     * @param  sQueryPath  The new path.
     */
    public void setQueryPath(String sQueryPath)
    {
        this.sQueryPath = sQueryPath;
    }

    /**
     * Returns a textual representation of this query.
     *
     * @return  A textual representation of this query.
     */
    @Override public String toString()
    {
        if (sQueryPath == null)
        {
            return "<unset>";
        }

        return sQueryPath + ((sQueryAttribute != null) ? ("/@" + sQueryAttribute) : "");
    }

    /**
     * A class that implements the com.eibus.xml.nom.FindHandler interface. This class is used to
     * collect nodes that have the given attribute set.
     *
     * @author  mpoyhone
     */
    private static class AttributeFinder
        implements FindHandler
    {
        /**
         * If true, only the first match is returned.
         */
        private boolean bSelectSingleNode;
        /**
         * Result nodes are collected in this list as Integer objects.
         */
        private List<Integer> lResultList = new ArrayList<Integer>(25);
        /**
         * Destination attribute name (required).
         */
        private String sAttribName;
        /**
         * Destination attribute value (optional).
         */
        private String sAttribValue;

        /**
         * Constructor.
         *
         * @param  sAttribName        Name of the attribute to be found.
         * @param  sAttribValue       An optional value for the attribute.
         * @param  bSelectSingleNode  If true, only the first match is returned.
         */
        public AttributeFinder(String sAttribName, String sAttribValue, boolean bSelectSingleNode)
        {
            this.sAttribName = sAttribName;
            this.sAttribValue = sAttribValue;
            this.bSelectSingleNode = bSelectSingleNode;
        }

        /**
         * @see  com.eibus.xml.nom.FindHandler#found(int)
         */
        public int found(int xNode)
        {
            String sValue = Node.getAttribute(xNode, sAttribName);

            if (sValue == null)
            {
                return CONTINUE_SEARCH;
            }

            if ((sAttribValue != null) && !sAttribValue.equals(sValue))
            {
                return CONTINUE_SEARCH;
            }

            lResultList.add(new Integer(xNode));

            return (!bSelectSingleNode) ? CONTINUE_SEARCH : ABORT_SEARCH;
        }

        /**
         * Returns all found nodes. Note that the 'select single node' must be set to false in order
         * to get more than one result.
         *
         * @return  All found nodes. Can be an empty array if no nodes were found.
         */
        public int[] getAllMatches()
        {
            int[] iaResultArray = new int[lResultList.size()];
            int i = 0;

            for (Iterator<Integer> iIter = lResultList.iterator(); iIter.hasNext(); i++)
            {
                iaResultArray[i] = iIter.next().intValue();
            }

            return iaResultArray;
        }

        /**
         * Returns the first matched node.
         *
         * @return  First matched node or zero if none was found.
         */
        public int getFirstMatch()
        {
            if (lResultList.isEmpty())
            {
                return 0;
            }

            return lResultList.get(0).intValue();
        }
    }
}
