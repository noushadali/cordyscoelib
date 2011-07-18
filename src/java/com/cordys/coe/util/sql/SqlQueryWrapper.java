/**
 * (c) 2006 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.

 */
package com.cordys.coe.util.sql;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.util.regex.Pattern;

/**
 * A helper class for creating dynamic queries. Keeps track of all parts of a SELECT statement so
 * that a valid SQL query can be created from this object. One benefit of this class it that parts
 * of the query can be added in a different order than just concatenating the query parts together.
 * For example :
 *
 * <pre>
     StringBuffer sbQuery = new StringBuffer();
     sbQuery.append("select T1.*");
     if (bAddT2) {
        sbQuery.append(", T2.*"):
     }
     sbQuery.append(" from T1");
     if (bAddT2) {
        sbQuery.append(", T2");
     }
     sbQuery.append(" where T1.Col1 = 'value'");
     if (bAddT2) {
        sbQuery.append(" and T2.Col2 = 'other value'");
     }
 *  </pre>
 *
 * <p>would become :</p>
 *
 * <pre>
    SqlQueryWrapper sqwQuery = new SqlQueryWrapper();
    sqwQuery.select("T1.*").from("T1").where("T1.Col1 = 'value'");
    if (bAddT2) {
        qwQuery.select("T2.*").from("T2").where("T2.Col2 = 'other value'");
    }
 * </pre>
 *
 * @author  mpoyhone
 */
public class SqlQueryWrapper
{
    /**
     * Holds the pattern that determines whether or not it's an inner/outer join.
     */
    private static final Pattern REGEX_IS_JOIN = Pattern.compile("\\s*(LEFT|RIGHT){0,1}\\s*(INNER|OUTER){0,1}\\s*JOIN.+",
                                                                 Pattern.CASE_INSENSITIVE |
                                                                 Pattern.DOTALL);
    /**
     * String that is used for indenting the beginning of the line for pretty prints. The length
     * determines the indent position.
     */
    protected static final String PRETTY_INDENT_STRING = "         ";
    /**
     * Holds the default where operator that should be used.
     */
    private static final String DEFAULT_WHERE_OPERATOR = "AND";
    /**
     * Contains all FROM part elements.
     */
    protected List<String> lFromList = new ArrayList<String>(5);
    /**
     * Contains all GROUP BY part elements.
     */
    protected List<String> lGroupBy = new ArrayList<String>(5);
    /**
     * Contains all ORDER BY part elements.
     */
    protected List<String> lOrderByList = new ArrayList<String>(5);
    /**
     * Contains all SELECT part elements.
     */
    protected List<String> lSelectList = new ArrayList<String>(5);
    /**
     * Contains all WHERE part elements.
     */
    protected List<String[]> lWhereList = new ArrayList<String[]>(5);
    /**
     * String used to separate elements in the WHERE clause.
     */
    protected String sDefaultOperator = DEFAULT_WHERE_OPERATOR;

    /**
     * Clears the all the query contents.
     */
    public void clear()
    {
        lFromList.clear();
        lOrderByList.clear();
        lSelectList.clear();
        lWhereList.clear();
        lGroupBy.clear();
    }

    /**
     * Adds an element to the FROM part. The elements are separated with a comma (i.e. using an
     * inner join) in the final query string.
     *
     * @param   sElementText  Text to be added.
     *
     * @return  This object for method chaining.
     */
    public SqlQueryWrapper from(String sElementText)
    {
        lFromList.add(sElementText);

        return this;
    }

    /**
     * Returns the default where operator string.
     *
     * @return  The default where operator string.
     */
    public String getDefaultWhereOperator()
    {
        return sDefaultOperator;
    }

    /**
     * Returns the where separator string.
     *
     * @return      The where separator string.
     *
     * @deprecated  Please use the {@linkplain SqlQueryWrapper#getDefaultWhereOperator()} method.
     */
    public String getWhereSeparator()
    {
        return sDefaultOperator;
    }

    /**
     * Adds an element to the GROUP BY part. The elements are separated with a comma in the final
     * query string.
     *
     * @param   sElementText  Text to be added.
     *
     * @return  This object for method chaining.
     */
    public SqlQueryWrapper groupBy(String sElementText)
    {
        lGroupBy.add(sElementText);

        return this;
    }

    /**
     * Adds an element to the ORDER BY part. The elements are separated with the where separator
     * string (by default 'and') in the final query string.
     *
     * @param   sElementText  Text to be added.
     *
     * @return  This object for method chaining.
     */
    public SqlQueryWrapper orderBy(String sElementText)
    {
        lOrderByList.add(sElementText);

        return this;
    }

    /**
     * Adds an element to the ORDER BY part. The elements are separated with the where separator
     * string (by default 'and') in the final query string. This version adds "ASC" or "DESC"
     * depending on the bAscending parameter.
     *
     * @param   sElementText  Text to be added.
     * @param   bAscending    If <code>true</code>, "ASC" is added. Otherwise "DESC".
     *
     * @return  This object for method chaining.
     */
    public SqlQueryWrapper orderBy(String sElementText, boolean bAscending)
    {
        lOrderByList.add(sElementText + (bAscending ? " ASC" : " DESC"));

        return this;
    }

    /**
     * Adds an element to the SELECT part. The elements are separated with a comma in the final
     * query string.
     *
     * @param   sElementText  Text to be added.
     *
     * @return  This object for method chaining.
     */
    public SqlQueryWrapper select(String sElementText)
    {
        lSelectList.add(sElementText);

        return this;
    }

    /**
     * Adds elements to the SELECT part. The elements are separated with a comma in the final query
     * string. This method supports four kinds of column definitions:
     *
     * <ul>
     *   <li>{ "column" } - Added as SELECT column</li>
     *   <li>{ "column", "TBL" } - Added as SELECT TBL.column</li>
     *   <li>{ "column", "TBL", "alias" } - Added as SELECT TBL.column AS alias</li>
     *   <li>{ "column", "", "alias" } - Added as SELECT column AS alias</li>
     * </ul>
     *
     * @param   saColumnArray  Columns to be added.
     *
     * @return  This object for method chaining.
     */
    public SqlQueryWrapper select(String[][] saColumnArray)
    {
        for (String[] column : saColumnArray)
        {
            switch (column.length)
            {
                case 0:
                    throw new IllegalArgumentException("Column definition is empty.");

                case 1:
                    lSelectList.add(column[0]);
                    break;

                case 2:
                    lSelectList.add(column[1] + "." + column[0]);
                    break;

                case 3:
                    if (column[1].length() > 0)
                    {
                        lSelectList.add(column[1] + "." + column[0] + " AS " + column[2]);
                    }
                    else
                    {
                        lSelectList.add(column[0] + " AS " + column[2]);
                    }
                    break;

                default:
                    throw new IllegalArgumentException("To many elements in the column definition: " +
                                                       column);
            }
        }

        return this;
    }

    /**
     * Adds an element to the SELECT part. The elements are separated with a comma in the final
     * query string. This method generates this SQL: SELECT columnName AS columnAlias.
     *
     * @param   columnName   DB column name.
     * @param   columnAlias  Column alias.
     *
     * @return  This object for method chaining.
     */
    public SqlQueryWrapper selectAs(String columnName, String columnAlias)
    {
        lSelectList.add(columnName + " AS \"" + columnAlias + "\"");

        return this;
    }

    /**
     * The where separator sting to set.
     *
     * @param  sDefaultOperator  sWhereSeparator The where separator string.
     */
    public void setDefaultWhereOperator(String sDefaultOperator)
    {
        this.sDefaultOperator = sDefaultOperator;
    }

    /**
     * The where separator sting to set.
     *
     * @param       sWhereSeparator  The where separator string.
     *
     * @deprecated  You should use the {@linkplain SqlQueryWrapper#where(String, String)} method to
     *              specify the operator. Or use the {@linkplain
     *              SqlQueryWrapper#setDefaultWhereOperator(String)} to influence the default
     *              operator.
     */
    public void setWhereSeparator(String sWhereSeparator)
    {
        sDefaultOperator = sWhereSeparator;
    }

    /**
     * Returns final the query string.
     *
     * @return  Final query string.
     *
     * @see     java.lang.Object#toString()
     */
    @Override public String toString()
    {
        return toString(true);
    }

    /**
     * Returns final the query string.
     *
     * @param   bPretty  If <code>true</code> each part is written on its own line.
     *
     * @return  Query string.
     */
    public String toString(boolean bPretty)
    {
        StringBuffer sbQuery = new StringBuffer(512);

        boolean bFirst;

        indent(bPretty, "select", sbQuery);

        bFirst = true;

        for (Iterator<String> iIter = lSelectList.iterator(); iIter.hasNext();)
        {
            String sElem = iIter.next();

            if (!bFirst)
            {
                sbQuery.append(", ");
            }

            sbQuery.append(sElem);

            bFirst = false;
        }

        if (bPretty)
        {
            sbQuery.append('\n');
        }
        else
        {
            sbQuery.append(' ');
        }

        indent(bPretty, "from", sbQuery);

        bFirst = true;

        for (Iterator<String> iIter = lFromList.iterator(); iIter.hasNext();)
        {
            String sElem = iIter.next();

            // pgussow: It could be just another table. In that case it's OK to add the ','.
            // But if you want to use left|right inner|outer joins, then you don't need the ','
            if (!bFirst)
            {
                if (!REGEX_IS_JOIN.matcher(sElem).matches())
                {
                    sbQuery.append(", ");
                }
                else
                {
                    sbQuery.append("\n");
                }
            }

            sbQuery.append(sElem);

            bFirst = false;
        }

        if (!lWhereList.isEmpty())
        {
            if (bPretty)
            {
                sbQuery.append('\n');
            }
            else
            {
                sbQuery.append(' ');
            }

            indent(bPretty, "where", sbQuery);

            bFirst = true;

            for (Iterator<String[]> iIter = lWhereList.iterator(); iIter.hasNext();)
            {
                String[] sElem = iIter.next();

                if (!bFirst)
                {
                    if (bPretty)
                    {
                        sbQuery.append('\n');
                    }
                    else
                    {
                        sbQuery.append(' ');
                    }

                    // indent(bPretty, sWhereSeparatorAnd, sbQuery);
                    indent(bPretty, sElem[1], sbQuery);
                }

                sbQuery.append(sElem[0]);

                bFirst = false;
            }
        }

        if (!lGroupBy.isEmpty())
        {
            if (bPretty)
            {
                sbQuery.append('\n');
            }
            else
            {
                sbQuery.append(' ');
            }

            indent(bPretty, "group by", sbQuery);

            bFirst = true;

            for (Iterator<String> iIter = lGroupBy.iterator(); iIter.hasNext();)
            {
                String sElem = iIter.next();

                if (!bFirst)
                {
                    if (bPretty)
                    {
                        sbQuery.append('\n');
                    }

                    indent(bPretty, ",", sbQuery);
                }

                sbQuery.append(sElem);

                bFirst = false;
            }
        }

        if (!lOrderByList.isEmpty())
        {
            if (bPretty)
            {
                sbQuery.append('\n');
            }
            else
            {
                sbQuery.append(' ');
            }

            indent(bPretty, "order by", sbQuery);

            bFirst = true;

            for (Iterator<String> iIter = lOrderByList.iterator(); iIter.hasNext();)
            {
                String sElem = iIter.next();

                if (!bFirst)
                {
                    if (bPretty)
                    {
                        sbQuery.append('\n');
                    }

                    indent(bPretty, ",", sbQuery);
                }

                sbQuery.append(sElem);

                bFirst = false;
            }
        }

        return sbQuery.toString();
    }

    /**
     * Adds an element to the WHERE part. The elements are separated with the where separator string
     * (by default 'and') in the final query string.
     *
     * @param   sElementText  Text to be added.
     *
     * @return  This object for method chaining.
     */
    public SqlQueryWrapper where(String sElementText)
    {
        return where(sElementText, sDefaultOperator);
    }

    /**
     * Adds an element to the WHERE part. The elements are separated with the where separator string
     * (by default 'and') in the final query string.
     *
     * @param   sElementText  Text to be added.
     * @param   sOperator     Where operator - and, or..
     *
     * @return  This object for method chaining.
     */
    public SqlQueryWrapper where(String sElementText, String sOperator)
    {
        lWhereList.add(new String[] { sElementText, sOperator });

        return this;
    }

    /**
     * Adds an element to the WHERE part. The elements are separated with the where separator string
     * (by default 'and') in the final query string. This form takes the expression parts as
     * separate arguments.
     *
     * <p>For example:</p>
     *
     * <pre>
       q.where("field", "=", "1", true);
     * </pre>
     *
     * <p>produces:</p>
     *
     * <pre>
       "where field = '1'"
     * </pre>
     *
     * @param   sField       Comparison field.
     * @param   sOperand     Comparison operand, e.g. '='
     * @param   sValue       Comparison value
     * @param   bQuoteValue  If <code>true</code> comparison value is quoted.
     *
     * @return  This object for method chaining.
     */
    public SqlQueryWrapper where(String sField, String sOperand, String sValue, boolean bQuoteValue)
    {
        if (sValue == null)
        {
            sValue = "";
        }

        StringBuffer sbTmp = new StringBuffer(sField.length() + sValue.length() + 10);

        sbTmp.append(sField).append(' ').append(sOperand).append(' ');

        if (bQuoteValue)
        {
            sbTmp.append("'");
        }

        sbTmp.append(sValue);

        if (bQuoteValue)
        {
            sbTmp.append("'");
        }

        return where(sbTmp.toString());
    }

    /**
     * Adds the given text to the string buffer based on the pretty print indenting. If the text is
     * longer than the indent position, a space is added.
     *
     * @param  bPretty  if<code>true</code> the text is intended, otherwise just added with a space.
     * @param  sText    Text to be added.
     * @param  sbRes    Receiving string buffer.
     */
    private static void indent(boolean bPretty, String sText, StringBuffer sbRes)
    {
        sbRes.append(sText);

        if (bPretty && (sText.length() < PRETTY_INDENT_STRING.length()))
        {
            sbRes.append(PRETTY_INDENT_STRING.substring(sText.length()));
        }
        else
        {
            sbRes.append(' ');
        }
    }
}
