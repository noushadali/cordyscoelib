package com.cordys.coe.util;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.TimeZone;

import static junit.framework.Assert.assertEquals;

/**
 * This class contains the test cases to test the parsing of the datea. This is done to make sure also dates mith
 * microseconds will be parsed properly.
 *
 * @author  localpg
 */
public class DateTest
{
    /**
     * This method test the dault simple date format to make sure that indeed microseconds are not parsed propely.
     *
     * @throws  Exception  In case of any exceptions
     */
    public void testSimpleDateFormatParsing()
                                     throws Exception
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S");

        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date d1 = sdf.parse("2011-01-01T15:00:00.123");

        assertEquals(1293894000123L, d1.getTime());

        Date d2 = sdf.parse("2011-01-01T15:00:00.1234");
        assertEquals(false, d2.getTime() == 1293894000123L);
    }
    
    
    
    
}
