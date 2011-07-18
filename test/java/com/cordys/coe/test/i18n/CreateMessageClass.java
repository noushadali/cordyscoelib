package com.cordys.coe.test.i18n;

import com.cordys.coe.util.i18n.CoEMessageClassGenerator;

import java.io.File;

/**
 * DOCUMENTME.
 *
 * @author  $author$
 */
public class CreateMessageClass
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
            CoEMessageClassGenerator cmcg = new CoEMessageClassGenerator("com.cordys.coe.util.cgc.message",
                                                                         "CGCMessages");
            cmcg.generateCoEJavaClass("com.cordys.coe.util.cgc.message.CGCMessages",
                                      new File("./src/java"));

            System.out.println("done.");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
