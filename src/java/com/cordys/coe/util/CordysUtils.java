package com.cordys.coe.util;

import java.io.File;

import com.eibus.util.system.EIBProperties;

/**
 * Holds the Class CordysUtils which contains general Cordys utilities.
 */
public class CordysUtils
{
    /**
     * This method returns the web root to use. This is to ensure backwards compatibility between 4.2 and 4.1. For 4.1 it is Web
     * and for 4.2 it is webroot/shared
     * 
     * @return The web folder to use.
     */
    public static String getWebRoot()
    {
        File temp = new File(EIBProperties.getInstallDir());

        File root = new File(temp, "webroot/shared");
        if (!root.exists())
        {
            return "Web";
        }

        return "webroot/shared";
    }
}
