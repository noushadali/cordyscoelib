package com.cordys.coe.util.cgc;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.cordys.coe.util.cgc.config.CGCAuthenticationFactory;
import com.cordys.coe.util.cgc.config.CGCConfigFactory;
import com.cordys.coe.util.cgc.config.ETrustMode;
import com.cordys.coe.util.cgc.config.ICGCConfiguration;
import com.cordys.coe.util.cgc.config.IClientCertificateAuthentication;
import com.cordys.coe.util.cgc.userinfo.IUserInfo;

public class TestSSLP7B
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
            BasicConfigurator.configure();
            Logger.getRootLogger().setLevel(Level.INFO);
            System.out.println(System.getProperty("java.home"));
            
            IClientCertificateAuthentication ccaAuth = CGCAuthenticationFactory.createClientCertificateAuthentication("c:\\MyCA\\AnotherUser.pfx", "", "PKCS12");
            ICGCConfiguration cgcConfig = CGCConfigFactory.createSSLConfiguration("localhost", 443, ETrustMode.TRUST_EVERY_SERVER);
            
            ICordysGatewayClient cgc = CGCFactory.createCGC(ccaAuth, cgcConfig);
            cgc.setLoginToCordysOnConnect(true);
            cgc.setGatewayURL("/cordysssl/com.eibus.web.soap.Gateway.wcp");
            cgc.connect();
            
            IUserInfo ui = cgc.getUserInfo();
            System.out.println("Authenticated user: " + ui.getAuthenticatedUser());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
