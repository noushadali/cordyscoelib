/**
 * (c) 2007 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util.sql;

import junit.framework.TestCase;

/**
 * TODO Describe the class.
 *
 * @author  mpoyhone
 */
public class SqlQueryWrapperTest extends TestCase
{
    /**
     * Test method for {@link com.cordys.coe.util.sql.SqlQueryWrapper#from(java.lang.String)}.
     */
    public void testFrom()
    {
        SqlQueryWrapper q = new SqlQueryWrapper();
        String text;

        text = q.select("*").from("T1").from("T2").toString(false);
        assertEquals("select * from T1, T2", text.replaceAll("\\s+", " "));
    }

    /**
     * This method tests inner joins.
     */
    public void testInnerJoins()
    {
        SqlQueryWrapper returnValue = new SqlQueryWrapper();

        String s_baseQuery = "*";

        returnValue.select(s_baseQuery).from("SOSICASE SC\n");

        returnValue.from("INNER JOIN	CASECATEGORY CC ON CC.Id = SC.FK_CASECATEGORY_Id\n");
        returnValue.from("LEFT OUTER JOIN	COUNTRY CN ON CN.Id = SC.FK_COUNTRY_Id\n");
        returnValue.from("LEFT OUTER JOIN	BUSINESSRELATION BR ON BR.FK_RELATION_RELATION_Id = SC.FK_RISKBEARER_Id\n");
        returnValue.from("LEFT OUTER JOIN CASEPERSON CP ON CP.FK_SOSICASE_Id = SC.Id AND CP.FK_CASEPERSONROLE_Id = 1\n");

        String text = returnValue.toString(false);

        assertEquals("select * from SOSICASE SC INNER JOIN CASECATEGORY CC ON CC.Id = SC.FK_CASECATEGORY_Id LEFT OUTER JOIN COUNTRY CN ON CN.Id = SC.FK_COUNTRY_Id LEFT OUTER JOIN BUSINESSRELATION BR ON BR.FK_RELATION_RELATION_Id = SC.FK_RISKBEARER_Id LEFT OUTER JOIN CASEPERSON CP ON CP.FK_SOSICASE_Id = SC.Id AND CP.FK_CASEPERSONROLE_Id = 1 ",
                     text.replaceAll("\\s+", " "));
    }

    /**
     * Test method for {@link com.cordys.coe.util.sql.SqlQueryWrapper#orderBy(java.lang.String)}.
     */
    public void testOrderBy()
    {
        SqlQueryWrapper q = new SqlQueryWrapper();
        String text;

        text = q.select("*").from("T1").from("T2").orderBy("A").orderBy("B").toString(false);
        assertEquals("select * from T1, T2 order by A, B", text.replaceAll("\\s+", " "));
    }

    /**
     * Test method for {@link com.cordys.coe.util.sql.SqlQueryWrapper#from(java.lang.String)}.
     */
    public void testSelectMultiple()
    {
        SqlQueryWrapper q = new SqlQueryWrapper();
        String[][] columns =
        {
            { "Col1" },
            { "Col2", "T1" },
            { "Col3", "T1", "Renamed3" },
            { "Col4", "", "Renamed4" },
        };
        String text;

        text = q.select(columns).from("T1").toString(false);
        assertEquals("select Col1, T1.Col2, T1.Col3 AS Renamed3, Col4 AS Renamed4 from T1",
                     text.replaceAll("\\s+", " "));
    }

    /**
     * Test method for {@link
     * com.cordys.coe.util.sql.SqlQueryWrapper#setWhereSeparator(java.lang.String)}.
     */
    public void testSetWhereSeparator()
    {
        SqlQueryWrapper q = new SqlQueryWrapper();
        String text;

        text = q.select("*").from("T1").where("F1 = 1").where("F2 = 2").toString(false);
        assertEquals("select * from T1 where F1 = 1 and F2 = 2", text.replaceAll("\\s+", " "));

        q.clear();
        text = q.select("*").from("T1").where("F1 = 1").where("F2 = 2", "or").toString(false);
        assertEquals("select * from T1 where F1 = 1 or F2 = 2", text.replaceAll("\\s+", " "));
    }

    /**
     * Test method for {@link com.cordys.coe.util.sql.SqlQueryWrapper#where(java.lang.String)}.
     */
    public void testWhereString()
    {
        SqlQueryWrapper q = new SqlQueryWrapper();
        String text;

        text = q.select("*").from("T1").from("T2").where("A = 1").where("B = A").toString(false);
        assertEquals("select * from T1, T2 where A = 1 and B = A", text.replaceAll("\\s+", " "));
    }

    /**
     * Test method for {@link com.cordys.coe.util.sql.SqlQueryWrapper#where(java.lang.String,
     * java.lang.String, java.lang.String, boolean)}.
     */
    public void testWhereStringStringStringBoolean()
    {
        String text;

        text = new SqlQueryWrapper().select("*").from("T1").where("field", "=", "1", true).toString(false);
        assertEquals("select * from T1 where field = '1'", text);

        text = new SqlQueryWrapper().select("*").from("T1").where("field", "=", "1", false)
                                    .toString(false);
        assertEquals("select * from T1 where field = 1", text);
    }

    /**
     * @see  junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp()
                  throws Exception
    {
        super.setUp();
    }

    /**
     * @see  junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown()
                     throws Exception
    {
        super.tearDown();
    }
}
