package com.cordys.coe.util.cgc;

import com.cordys.coe.util.cgc.config.CGCAuthenticationFactory;
import com.cordys.coe.util.cgc.config.CGCConfigFactory;
import com.cordys.coe.util.cgc.config.ETrustMode;
import com.cordys.coe.util.cgc.config.IAuthenticationConfiguration;
import com.cordys.coe.util.cgc.config.ICGCConfiguration;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Test class for testing different connection methods.
 *
 * @author  pgussow
 */
public class TestCGC
{
    /**
     * Holds the created authentication configuration.
     */
    private static IAuthenticationConfiguration ac = null;
    /**
     * Holds the created CGC configuration.
     */
    private static ICGCConfiguration cc;
    /**
     * Holds the description of the connection.
     */
    private static String sConnectionType;

    /**
     * Main method.
     *
     * @param  saArguments  The commandline arguments.
     */
    public static void main(String[] saArguments)
    {
        try
        {
            BasicConfigurator.configure();
            Logger.getRootLogger().setLevel(Level.ERROR);

// *
            testAllConfigurations();

/*/
 *          createConfig(EConfType.SSO);
 *
 *       ICordysGatewayClient cgc = CGCFactory.createCGC(ac, cc);
 *
 *       System.out.println("Connecting to " + sConnectionType);         cgc.connect();
 * System.out.println("Connected!");
 *
 *       System.out.println("Authenticated user: " + cgc.getUserInfo().getAuthenticatedUser());
 *    System.out.println("Default organization: " +
 * cgc.getUserInfo().getDefaultOrganization().getDN());//*/
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * DOCUMENTME.
     *
     * @param  ct  DOCUMENTME
     */
    private static void createConfig(EConfType ct)
    {
        sConnectionType = ct.name();

        switch (ct)
        {
            case SSO:
                ac = CGCAuthenticationFactory.createSSOAuthentication("HumanWave", "TYwe674$");
                cc = CGCConfigFactory.createConfiguration("www.humanwave.nu", 442, false);
                break;

            case CORDYS_CUSTOM:
                ac = CGCAuthenticationFactory.createCordysCustomAuthentication("admin", "admin");
                cc = CGCConfigFactory.createConfiguration("srv-nl-ces52", 80, false);
                break;

            case SSL_NTLM_EVERYTHING:
                ac = CGCAuthenticationFactory.createNTLMAuthentication("pgussow", "phillip27",
                                                                       "NTDOM");
                cc = CGCConfigFactory.createSSLConfiguration("srv-nl-ces70", 443,
                                                             ETrustMode.TRUST_EVERY_SERVER);
                break;

            case SSL_NTLM_TRUSTSTORE:
                ac = CGCAuthenticationFactory.createNTLMAuthentication("pgussow", "phillip27",
                                                                       "NTDOM");
                cc = CGCConfigFactory.createSSLConfiguration("srv-nl-ces70", 443,
                                                             ETrustMode.USE_TRUSTORE);
        }
    }

    /**
     * Test all configurations that are defined.
     */
    private static void testAllConfigurations()
    {
        EConfType[] act = EConfType.values();

        for (EConfType ct : act)
        {
            try
            {
                createConfig(ct);

                ICordysGatewayClient cgc = CGCFactory.createCGC(ac, cc);

                System.out.println("Connecting to " + sConnectionType);
                cgc.connect();
                System.out.println("Connected!");

                System.out.println("Authenticated user: " +
                                   cgc.getUserInfo().getAuthenticatedUser());
                System.out.println("Default organization: " +
                                   cgc.getUserInfo().getDefaultOrganization().getDN());
                
                cgc.disconnect();
            }
            catch (Exception e)
            {
                System.err.println("Error doing conf " + act);
                e.printStackTrace();
            }
        }
    }

    /**
     * DOCUMENTME.
     *
     * @author  $author$
     */
    private enum EConfType
    {
        SSO,
        CORDYS_CUSTOM,
        SSL_NTLM_EVERYTHING,
        SSL_NTLM_TRUSTSTORE
    }
}
