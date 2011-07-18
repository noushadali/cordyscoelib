package com.cordys.coe.test;

import com.cordys.coe.util.classpath.FindClass;

/**
 * Test class for the find class.
 *
 * @author pgussow
  */
public class TestFindClass
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
            System.out.println(FindClass.findClass("com.cordys.coe.test.TestFindClass"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
