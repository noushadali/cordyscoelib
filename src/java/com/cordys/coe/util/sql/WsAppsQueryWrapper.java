/**
 * (c) 2007 Cordys R&D B.V. All rights reserved. The computer program(s) is the proprietary information of Cordys B.V. and
 * provided under the relevant License Agreement containing restrictions on use and disclosure. Use is subject to the License
 * Agreement.
 */
package com.cordys.coe.util.sql;

import com.cordys.cpc.bsf.busobject.BusObject;
import com.cordys.cpc.bsf.busobject.BusObjectIterator;
import com.cordys.cpc.bsf.busobject.QueryObject;
import com.cordys.cpc.bsf.busobject.QueryParameter;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * WsAppServer query additions for the com.cordys.coe.util.sql.SqlQueryWrapper.
 * <p>
 * One use case for this is creating a query for a search, next and previous method which have a number of optional search critia.
 * In this example name and address search criteria are optional:
 * </p>
 * 
 * <pre>
 * private static WsAppsQueryWrapper createSearchQuery(String sLastQueryName, String sName, String sAddress, boolean bIsPrevious)
 * {
 *     WsAppsQueryWrapper qw = new WsAppsQueryWrapper();
 *     qw.select(&quot;*&quot;).from(&quot;ACCOUNTS&quot;);
 *     if (sName != null &amp;&amp; sName.length() &gt; 0)
 *     {
 *         qw.where(&quot;NAME like :NAME&quot;);
 *         qw.addParameter(&quot;NAME&quot;, &quot;ACCOUNTS.NAME&quot;, &quot;%&quot; + sName + &quot;%&quot;);
 *     }
 *     if (sAddress != null &amp;&amp; sAddress.length() &gt; 0)
 *     {
 *         qw.where(&quot;ADDRESS like :ADDRESS&quot;);
 *         qw.addParameter(&quot;ADDRESS&quot;, &quot;ACCOUNTS.ADDRESS&quot;, &quot;%&quot; + sAddress + &quot;%&quot;);
 *     }
 *     if (sLastQueryName != null)
 *     {
 *         qw.where(&quot;NAME&quot;, !bIsPrevious ? &quot;&gt;&quot; : &quot;&lt;&quot;, &quot;:LAST_NAME&quot;, false);
 *         qw.addParameter(&quot;LAST_NAME&quot;, &quot;ACCOUNTS.NAME&quot;, sLastQueryName);
 *     }
 *     if (!bIsPrevious)
 *     {
 *         qw.orderBy(&quot;NAME asc&quot;);
 *     }
 *     else
 *     {
 *         qw.orderBy(&quot;NAME desc&quot;);
 *     }
 *     return qw;
 * }
 * </pre>
 * <p>
 * The search method calls this like:
 * </p>
 * 
 * <pre>
 * WsAppsQueryWrapper qw = createSearchQuery(null, sName, sAddress, false);
 * QueryObject query = qw.createQueryObject();
 * query.setResultClass(Account.class);
 * return query.getObjects();
 * </pre>
 * <p>
 * The search next method calls this like:
 * </p>
 * 
 * <pre>
 * WsAppsQueryWrapper qw = createSearchQuery(sLastQueryName, sName, sAddress, false);
 * QueryObject query = qw.createQueryObject();
 * query.setResultClass(Account.class);
 * return query.getObjects();
 * </pre>
 * <p>
 * The search previous method calls this like:
 * </p>
 * 
 * <pre>
 * WsAppsQueryWrapper qw = createSearchQuery(sLastQueryName, sName, sAddress, true);
 * QueryObject query = qw.createQueryObject();
 * query.setResultClass(Account.class);
 * return query.getObjects();
 * </pre>
 * 
 * @author mpoyhone
 */
public class WsAppsQueryWrapper<T extends BusObject> extends SqlQueryWrapper
{
    /**
     * Holds the result field mapping that should be applied.
     */
    protected String[][] m_fieldMapping;
    /**
     * Contains all query parameters as <code>QueryParameter</code> objects.
     */
    protected List<QueryParameter> m_queryParams = new ArrayList<QueryParameter>(5);
    /**
     * Result class which is set into the create QueryObject.
     */
    protected Class<? extends T> m_resultClass;
    /**
     * Holds whether or not a result mapping should be set when the Query object is created.
     */
    protected int m_resultMapping = -1;
    /**
     * If <code>true</code> returned objects are marked as transient.
     */
    protected boolean m_returnTransient;

    /**
     * Constructor for WsAppsQueryWrapper.
     */
    public WsAppsQueryWrapper()
    {
    }

    /**
     * Constructor for WsAppsQueryWrapper.
     * 
     * @param resultClass Result class which is set into the create QueryObject.
     */
    public WsAppsQueryWrapper(Class<? extends T> resultClass)
    {
        this(resultClass, false);
    }

    /**
     * Constructor for WsAppsQueryWrapper.
     * 
     * @param resultClass Result class which is set into the create QueryObject.
     * @param returnTransient If <code>true</code> returned objects are marked as transient.
     */
    public WsAppsQueryWrapper(Class<? extends T> resultClass, boolean returnTransient)
    {
        this(resultClass, returnTransient, -1, null);
    }

    /**
     * Constructor for WsAppsQueryWrapper.
     * 
     * @param resultClass Result class which is set into the create QueryObject.
     * @param returnTransient If <code>true</code> returned objects are marked as transient.
     * @param resultMapping The result mapping that should be used.
     */
    public WsAppsQueryWrapper(Class<? extends T> resultClass, boolean returnTransient, int resultMapping)
    {
        this(resultClass, returnTransient, resultMapping, null);
    }

    /**
     * Constructor for WsAppsQueryWrapper.
     * 
     * @param resultClass Result class which is set into the create QueryObject.
     * @param returnTransient If <code>true</code> returned objects are marked as transient.
     * @param resultMapping The result mapping that should be used.
     * @param fieldMapping The field mapping for the result mapping.
     */
    public WsAppsQueryWrapper(Class<? extends T> resultClass, boolean returnTransient, int resultMapping, String[][] fieldMapping)
    {
        m_resultClass = resultClass;
        m_returnTransient = returnTransient;
        m_resultMapping = resultMapping;
        m_fieldMapping = fieldMapping;
    }

    /**
     * Adds a parameter to the query.
     * 
     * @param qpParam Query parameter object.
     */
    public void addParameter(QueryParameter qpParam)
    {
        m_queryParams.add(qpParam);
    }

    /**
     * Adds a parameter to the query. The type is determined by the value type.
     * 
     * @param sParamName Parameter name.
     * @param sFieldType Parameter field type.
     * @param sValue Parameter value.
     */
    public void addParameter(String sParamName, String sFieldType, String sValue)
    {
        m_queryParams.add(new QueryParameter(sParamName, sFieldType, QueryObject.PARAM_STRING, sValue));
    }

    /**
     * Adds a parameter to the query. The type is determined by the value type.
     * 
     * @param sParamName Parameter name.
     * @param sFieldType Parameter field type.
     * @param iValue Parameter value.
     */
    public void addParameter(String sParamName, String sFieldType, Integer iValue)
    {
        m_queryParams.add(new QueryParameter(sParamName, sFieldType, QueryObject.PARAM_INT, iValue));
    }

    /**
     * Adds a parameter to the query. The type is determined by the value type.
     * 
     * @param sParamName Parameter name.
     * @param sFieldType Parameter field type.
     * @param lValue Parameter value.
     */
    public void addParameter(String sParamName, String sFieldType, Long lValue)
    {
        m_queryParams.add(new QueryParameter(sParamName, sFieldType, QueryObject.PARAM_INT, lValue));
    }

    /**
     * Adds a parameter to the query. The type is determined by the value type.
     * 
     * @param sParamName Parameter name.
     * @param sFieldType Parameter field type.
     * @param dValue Parameter value.
     */
    public void addParameter(String sParamName, String sFieldType, Double dValue)
    {
        m_queryParams.add(new QueryParameter(sParamName, sFieldType, QueryObject.PARAM_DOUBLE, dValue));
    }

    /**
     * Adds a parameter to the query. The type is determined by the value type.
     * 
     * @param sParamName Parameter name.
     * @param sFieldType Parameter field type.
     * @param dValue Parameter value.
     */
    public void addParameter(String sParamName, String sFieldType, Date dValue)
    {
        m_queryParams.add(new QueryParameter(sParamName, sFieldType, QueryObject.PARAM_DATE, dValue));
    }

    /**
     * Adds a parameter to the query. The type is determined by the value type.
     * 
     * @param sParamName Parameter name.
     * @param sFieldType Parameter field type.
     * @param bdValue Parameter value.
     */
    public void addParameter(String sParamName, String sFieldType, BigDecimal bdValue)
    {
        m_queryParams.add(new QueryParameter(sParamName, sFieldType, QueryObject.PARAM_BIGDECIMAL, bdValue));
    }

    /**
     * Adds a parameter to the query.
     * 
     * @param sParamName Parameter name.
     * @param sFieldType Parameter field type.
     * @param iValueType Parameter value type. These are PARAM_* constants from the QueryObject class.
     * @param oValue Parameter value.
     */
    public void addParameter(String sParamName, String sFieldType, int iValueType, Object oValue)
    {
        m_queryParams.add(new QueryParameter(sParamName, sFieldType, iValueType, oValue));
    }

    /**
     * Adds the parameters to the WsAppServer query object.
     * 
     * @param qoQuery Query object.
     */
    public void addParametersToQuery(QueryObject qoQuery)
    {
        for (Iterator<QueryParameter> iIter = m_queryParams.iterator(); iIter.hasNext();)
        {
            QueryParameter qpParam = iIter.next();

            qoQuery.addParameter(qpParam.getParameterName(), qpParam.getColumnName(), qpParam.getType(), qpParam.getValue());
        }
    }

    /**
     * @see com.cordys.coe.util.sql.SqlQueryWrapper#clear()
     */
    @Override
    public void clear()
    {
        super.clear();

        m_queryParams.clear();
    }

    /**
     * Creates a new QueryObject from the contents of this object.
     * 
     * @return WsAppServer New QueryObject
     */
    public QueryObject createQueryObject()
    {
        QueryObject q = new QueryObject(toString(false));

        addParametersToQuery(q);

        if (m_resultClass != null)
        {
            q.setResultClass(m_resultClass);
        }

        if (m_returnTransient)
        {
            q.setTransientResult(m_returnTransient);
        }

        if (m_resultMapping != -1)
        {
            q.setResultMapping(m_resultMapping, m_fieldMapping);
        }
        return q;
    }

    /**
     * This method executes the getObjects method and returns the bus object iterator.
     * 
     * @return The bus object iterator.
     */
    public BusObjectIterator<T> getObjects()
    {
        return createQueryObject().getObjects();
    }

    /**
     * This method executes the query and returns the object.
     * 
     * @return The object that is the result of the query
     */
    @SuppressWarnings("unchecked")
    public T getObject()
    {
        return (T) createQueryObject().getObject();
    }

    /**
     * Same as toString(true) but adds the parameters to the end of the string. This is intended for debug messages.
     * 
     * @return Debug version of this query.
     */
    public String toDebugString()
    {
        return toDebugString(true);
    }

    /**
     * Same as toString() but adds the parameters to the end of the string. This is intended for debug messages.
     * 
     * @param bPretty Pretty-print flag.
     * @return Debug version of this query.
     */
    public String toDebugString(boolean bPretty)
    {
        String sql = toString(bPretty);
        StringBuilder sb = new StringBuilder(sql.length() + 200);

        sb.append(sql);
        sb.append("\nParameters:\n");

        for (Iterator<QueryParameter> iIter = m_queryParams.iterator(); iIter.hasNext();)
        {
            QueryParameter qpParam = iIter.next();

            sb.append("  Name: ").append(qpParam.getParameterName());
            sb.append(", Value: ").append(qpParam.getValue());
            sb.append(", Column: ").append(qpParam.getColumnName());
            sb.append(", Type: ");

            switch (qpParam.getType())
            {
                case QueryObject.PARAM_STRING:
                    sb.append("string");
                    break;

                case QueryObject.PARAM_BOOLEAN:
                    sb.append("boolean");
                    break;

                case QueryObject.PARAM_DATE:
                    sb.append("date");
                    break;

                case QueryObject.PARAM_DOUBLE:
                    sb.append("double");
                    break;

                case QueryObject.PARAM_INT:
                    sb.append("int");
                    break;

                case QueryObject.PARAM_BIGDECIMAL:
                    sb.append("big decimal");
                    break;

                default:
                    sb.append("unknown");
                    break;
            }

            sb.append("\n");
        }

        return sb.toString();
    }
}
