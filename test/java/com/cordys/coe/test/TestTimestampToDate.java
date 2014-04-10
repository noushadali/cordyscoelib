package com.cordys.coe.test;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * DOCUMENTME .
 * 
 * @author pgussow
 */
public class TestTimestampToDate
{
    /**
     * Main method.
     * 
     * @param saArguments The commandline arguments.
     */
    public static void main(String[] saArguments)
    {
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S");
            Date d = null;

            d = new Date(1397136979128L);
            System.out.println(d.getTime() + ": " + sdf.format(d));

            d = sdf.parse("2014-04-10T15:30:00.0");
            System.out.println(d.getTime() + ": " + sdf.format(d));

            d = sdf.parse("2014-04-10T15:33:00.0");
            System.out.println(d.getTime() + ": " + sdf.format(d));

            d = sdf.parse("2014-04-10T15:45:00.0");
            System.out.println(d.getTime() + ": " + sdf.format(d));

            d = sdf.parse("2014-04-10T15:58:00.0");
            System.out.println(d.getTime() + ": " + sdf.format(d));

            d = sdf.parse("2014-04-10T16:06:00.0");
            System.out.println(d.getTime() + ": " + sdf.format(d));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
