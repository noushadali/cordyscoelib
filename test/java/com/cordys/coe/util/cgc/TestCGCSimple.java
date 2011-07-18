package com.cordys.coe.util.cgc;

import com.cordys.coe.util.cgc.config.CGCAuthenticationFactory;
import com.cordys.coe.util.cgc.config.CGCConfigFactory;
import com.cordys.coe.util.cgc.config.ETrustMode;
import com.cordys.coe.util.cgc.config.ICGCConfiguration;
import com.cordys.coe.util.cgc.config.ISSOAuthentication;

/**
 * Simple test class to test the CGC connection to a server.
 *
 * @author  pgussow
 */
public class TestCGCSimple
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
        	ISSOAuthentication ac = CGCAuthenticationFactory.createSSOAuthentication("HumanWave", "TYwe674$");
            ICGCConfiguration cc = CGCConfigFactory.createSSLConfiguration("www.humanwave.nu", 443, ETrustMode.TRUST_EVERY_SERVER);
            
            ICordysGatewayClient cgc = CGCFactory.createCGC(ac, cc);
            
            cgc.connect();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
