/**
 * (c) 2008 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util;

import java.util.regex.Pattern;

import junit.framework.TestCase;

/**
 * Test cases for StringUtils class.
 *
 * @author  mpoyhone
 */
public class StringUtilsTest extends TestCase
{
    /**
     * Test method for.{@link com.cordys.coe.util.StringUtils#replacePatterns(String, Pattern[],
     * String[])}
     */
    public void testReplacePatterns()
    {
        String res;

        res = StringUtils.replacePatterns("Test String",
                                          new Pattern[]
                                          {
                                              Pattern.compile("[TS]"), Pattern.compile("[X]")
                                          }, new String[] { "X", "B" });
        assertEquals("Best Btring", res);
    }

    /**
     * Test method for.{@link com.cordys.coe.util.StringUtils#replacePatternsWithCount(String,
     * Pattern[], String[])}
     */
    public void testReplacePatternsWithCount()
    {
        Pair<String, Integer> res;

        res = StringUtils.replacePatternsWithCount("xxx", new Pattern[] { Pattern.compile("x+") },
                                                   new String[] { "y" });
        assertEquals("y", res.getFirst());
        assertEquals(1, (int) res.getSecond());

        res = StringUtils.replacePatternsWithCount("xxx", new Pattern[] { Pattern.compile("x") },
                                                   new String[] { "y" });
        assertEquals("yyy", res.getFirst());
        assertEquals(3, (int) res.getSecond());

        res = StringUtils.replacePatternsWithCount("Test String",
                                                   new Pattern[]
                                                   {
                                                       Pattern.compile("[TS]"),
                                                       Pattern.compile("[X]")
                                                   }, new String[] { "X", "B" });
        assertEquals("Best Btring", res.getFirst());
        assertEquals(4, (int) res.getSecond());
    }
}
