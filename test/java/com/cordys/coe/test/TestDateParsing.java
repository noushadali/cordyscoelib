package com.cordys.coe.test;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.TimeZone;

/**
 * DOCUMENTME
 * .
 *
 * @author  pgussow
 */
public class TestDateParsing
{
    /**
     * Main method.
     *
     * @param  saArguments  The commandline arguments.
     */
    public static void main(String[] saArguments)
    {
        try
        {
            String[] a = TimeZone.getAvailableIDs(0);

            for (String string : a)
            {
                System.out.println(string);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date d = sdf.parse("2009-03-20T10:00:00.0");
            System.out.println(d.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
