package com.cordys.coe.test.i18n;

import com.cordys.coe.util.i18n.CoEMessageClassGenerator;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * DOCUMENTME .
 *
 * @author  pgussow
 */
public class TestGenJavaScript
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
            CoEMessageClassGenerator cmcg = new CoEMessageClassGenerator(new File("D:\\development\\workspaces\\MainWorkspace\\Ekko_PoC_main\\src\\web\\founter\\localization\\com.cordys.energy.poc.Messages.xml"),
                                                                         "com.cordys.energy.poc.Messages",
                                                                         "/cordys/founter/localization",
                                                                         new String[] { "nl", "de" });
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            cmcg.generateJavaScript("com.cordys.energy.poc.Messages", new File("D:/development/workspaces/MainWorkspace/Ekko_PoC_main/src/web/founter/localization"));

            System.out.println(baos.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
