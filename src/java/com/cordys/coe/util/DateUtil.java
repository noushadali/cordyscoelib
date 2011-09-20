package com.cordys.coe.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.TimeZone;

/**
 * This class contains date utility functions.
 *
 * @author  localpg
 */
public class DateUtil
{
    /**
     * Holds the simpe date format that is used to parse a date.
     */
    private static SimpleDateFormat s_sdf;

    static
    {
        s_sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S");

        // All dates are assumed to be UTC
        s_sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    /**
     * This method parses the string according the given date. The given date must be UTC.
     *
     * @param   date  The date to parse.
     *
     * @return  The parsed date.
     */
    public static Date parseDate(String date)
    {
        Date dReturn = null;

        if (StringUtils.isSet(date))
        {
            // Make sure the date has no more then 3 ms digits.
            int li = date.lastIndexOf('.');

            if (li < (date.length() - 4))
            {
                // More digits are there
                date = date.substring(0, li + 4);
            }

            try
            {
                synchronized (s_sdf)
                {
                    dReturn = s_sdf.parse(date);
                }
            }
            catch (ParseException e)
            {
                // Ignore it.
            }
        }

        return dReturn;
    }
}
