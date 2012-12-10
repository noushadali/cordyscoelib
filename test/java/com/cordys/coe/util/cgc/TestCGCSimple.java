package com.cordys.coe.util.cgc;

import java.net.URL;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.cordys.coe.util.cgc.config.CGCAuthenticationFactory;
import com.cordys.coe.util.cgc.config.CGCConfigFactory;
import com.cordys.coe.util.cgc.config.ICGCConfiguration;
import com.cordys.coe.util.cgc.config.INTLMAuthentication;
import com.cordys.coe.util.cgc.userinfo.IUserInfo;

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
        Logger.getLogger("com.cordys.coe.util.cgc").setLevel(Level.DEBUG);
        
        try
        {
            INTLMAuthentication auth = CGCAuthenticationFactory.createNTLMAuthentication("a216076", "Tatasteel02", "CE");
            ICGCConfiguration cfg = CGCConfigFactory.createConfiguration(new URL("http://ijmesbapd00/home/system/com.eibus.web.soap.Gateway.wcp"));
            
            ICordysGatewayClient cgc = CGCFactory.createCGC(auth, cfg);
            
            cgc.connect();
            
            IUserInfo userInfo = cgc.getUserInfo();
            System.out.println(userInfo.getAuthenticatedUser() + ", " + userInfo.getDefaultOrganization());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
