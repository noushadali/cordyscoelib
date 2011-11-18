package com.cordys.coe.test;

import sun.misc.BASE64Decoder;

/**
 * Simple program to decode BAse 64 strings.
 *
 * @author  localpg
 */
public class BASE64Decode
{
    /**
     * Main method.
     *
     * @param  saArguments  Commandline arguments.
     */
    public static void main(String[] saArguments)
    {
        try
        {
            String encoded = "RW1icmFlcjE5MA==";
            byte[] decoded = new BASE64Decoder().decodeBuffer(encoded);

            System.out.println(new String(decoded));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
