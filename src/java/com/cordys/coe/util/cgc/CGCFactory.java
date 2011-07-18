package com.cordys.coe.util.cgc;

import com.cordys.coe.util.cgc.config.CGCAuthenticationFactory;
import com.cordys.coe.util.cgc.config.CGCConfigFactory;
import com.cordys.coe.util.cgc.config.IAuthenticationConfiguration;
import com.cordys.coe.util.cgc.config.ICGCConfiguration;
import com.cordys.coe.util.cgc.nom.CordysNomGatewayClient;
import com.cordys.coe.util.cgc.nom.ICordysNomGatewayClient;

import java.net.URL;

/**
 * This factory class attempts to ease the creation of the Cordys Gateway Client for specific use.
 *
 * @author  pgussow
 */
public class CGCFactory
{
    /**
     * Indicates the default HTTP port.
     */
    private static final int DEFAULT_HTTP_PORT = 80;

    /**
     * This method creates a DOM based gateway client.
     *
     * @param   acAuthentication  The authentication configuration.
     * @param   ccConfiguration   The configuration.
     *
     * @return  The GatewayClient.
     *
     * @throws  CordysGatewayClientException  In case of any exception.
     */
    public static ICordysGatewayClient createCGC(IAuthenticationConfiguration acAuthentication,
                                                 ICGCConfiguration ccConfiguration)
                                          throws CordysGatewayClientException
    {
        return new CordysGatewayClient(acAuthentication, ccConfiguration);
    }

    /**
     * This method creates a CGC that logs in to Cordys using the custom authentication.
     *
     * @param   sUsername  The username.
     * @param   sPassword  The password.
     * @param   sHost      The name of the server.
     *
     * @return  The CGC to use.
     *
     * @throws  CordysGatewayClientException  In case of any exceptions.
     */
    public static ICordysGatewayClient createCGCForCustomAuthentication(String sUsername,
                                                                        String sPassword,
                                                                        String sHost)
        throws CordysGatewayClientException
    {
        return createCGCForCustomAuthentication(sUsername, sPassword, sHost, DEFAULT_HTTP_PORT,
                                                false);
    }

    /**
     * This method creates a CGC that logs in to Cordys using the custom authentication.
     *
     * @param   sUsername  The username.
     * @param   sPassword  The password.
     * @param   uUrl       The full URL of the web gateway.
     *
     * @return  The CGC to use.
     *
     * @throws  CordysGatewayClientException  In case of any exceptions.
     */
    public static ICordysGatewayClient createCGCForCustomAuthentication(String sUsername,
                                                                        String sPassword,
                                                                        URL uUrl)
        throws CordysGatewayClientException
    {
        ICordysGatewayClient cgcReturn = null;

        String sHost = uUrl.getHost();
        int iPort = uUrl.getPort();
        String sGatewayURL = getRelativePath(uUrl);

        cgcReturn = createCGCForCustomAuthentication(sUsername, sPassword, sHost, iPort);
        cgcReturn.setGatewayURL(sGatewayURL);

        return cgcReturn;
    }

    /**
     * This method creates a CGC that logs in to Cordys using the custom authentication.
     *
     * @param   sUsername  The username.
     * @param   sPassword  The password.
     * @param   sHost      The name of the server.
     * @param   iPort      The portnumber to use.
     *
     * @return  The CGC to use.
     *
     * @throws  CordysGatewayClientException  In case of any exceptions.
     */
    public static ICordysGatewayClient createCGCForCustomAuthentication(String sUsername,
                                                                        String sPassword,
                                                                        String sHost,
                                                                        int iPort)
        throws CordysGatewayClientException
    {
        return createCGCForCustomAuthentication(sUsername, sPassword, sHost, iPort, false);
    }

    /**
     * This method creates a CGC that logs in to Cordys using the custom authentication.
     *
     * @param   sUsername  The username.
     * @param   sPassword  The password.
     * @param   sServer    The name of the server.
     * @param   iPort      The portnumber to use.
     * @param   bUseSSL    Whether or not the server is running under SSL.
     *
     * @return  The CGC to use.
     *
     * @throws  CordysGatewayClientException  In case of any exceptions.
     */
    public static ICordysGatewayClient createCGCForCustomAuthentication(String sUsername,
                                                                        String sPassword,
                                                                        String sServer, int iPort,
                                                                        boolean bUseSSL)
        throws CordysGatewayClientException
    {
        ICordysGatewayClient cgcReturn = null;

        // First create the configuration
        ICGCConfiguration ccConfig = CGCConfigFactory.createConfiguration(sServer, iPort, bUseSSL);

        // Now create the authentication.
        IAuthenticationConfiguration acAuth = CGCAuthenticationFactory
                                              .createCordysCustomAuthentication(sUsername,
                                                                                sPassword);

        cgcReturn = new CordysGatewayClient(acAuth, ccConfig);

        return cgcReturn;
    }

    /**
     * This method creates a CGC that logs in to Cordys using the custom authentication.
     *
     * @param   sUsername  The username.
     * @param   sPassword  The password.
     * @param   sServer    The name of the server.
     * @param   iPort      The portnumber to use.
     * @param   bUseSSL    Whether or not the server is running under SSL.
     *
     * @return  The CGC to use.
     *
     * @throws  CordysGatewayClientException  In case of any exceptions.
     */
    public static ICordysGatewayClient createCGCForUsernamePasswordAuthentication(String sUsername,
                                                                                  String sPassword,
                                                                                  String sServer,
                                                                                  int iPort,
                                                                                  boolean bUseSSL)
        throws CordysGatewayClientException
    {
        ICordysGatewayClient cgcReturn = null;

        // First create the configuration
        ICGCConfiguration ccConfig = CGCConfigFactory.createConfiguration(sServer, iPort, bUseSSL);

        // Now create the authentication.
        IAuthenticationConfiguration acAuth = CGCAuthenticationFactory
                                              .createUsernamePasswordAuthentication(sUsername,
                                                                                    sPassword);

        cgcReturn = new CordysGatewayClient(acAuth, ccConfig);

        return cgcReturn;
    }

    /**
     * This method creates a NOM based gateway client.
     *
     * @param   acAuthentication  The authentication configuration.
     * @param   ccConfiguration   The configuration.
     *
     * @return  The GatewayClient.
     *
     * @throws  CordysGatewayClientException  In case of any exception.
     */
    public static ICordysNomGatewayClient createNOMBasedCGC(IAuthenticationConfiguration acAuthentication,
                                                            ICGCConfiguration ccConfiguration)
                                                     throws CordysGatewayClientException
    {
        return new CordysNomGatewayClient(acAuthentication, ccConfiguration);
    }

    /**
     * This method creates a CGC that logs in to Cordys using the custom authentication.
     *
     * @param   sUsername  The username.
     * @param   sPassword  The password.
     * @param   uUrl       The full URL of the web gateway.
     *
     * @return  The CGC to use.
     *
     * @throws  CordysGatewayClientException  In case of any exceptions.
     */
    public static ICordysNomGatewayClient createNOMBasedCGC(String sUsername, String sPassword,
                                                            URL uUrl)
                                                     throws CordysGatewayClientException
    {
        ICordysNomGatewayClient cgcReturn = null;

        String sHost = uUrl.getHost();
        int iPort = uUrl.getPort();
        boolean bSSL = "https".equals(uUrl.getProtocol().toLowerCase());

        String sGatewayURL = getRelativePath(uUrl);

        // First create the configuration
        ICGCConfiguration ccConfig = CGCConfigFactory.createConfiguration(sHost, iPort, bSSL);

        // Now create the authentication.
        IAuthenticationConfiguration acAuth = CGCAuthenticationFactory
                                              .createUsernamePasswordAuthentication(sUsername,
                                                                                    sPassword);

        cgcReturn = new CordysNomGatewayClient(acAuth, ccConfig);

        cgcReturn.setGatewayURL(sGatewayURL);

        return cgcReturn;
    }

    /**
     * This method will return the relative path for the given URL.
     *
     * @param   uURL  The URL to return the relative path of.
     *
     * @return  The relative path for the given URL.
     */
    private static String getRelativePath(URL uURL)
    {
        StringBuilder sb = new StringBuilder(100);
        String tmp;

        tmp = uURL.getPath();

        if ((tmp != null) && (tmp.length() > 0))
        {
            sb.append(tmp);
        }

        tmp = uURL.getQuery();

        if ((tmp != null) && (tmp.length() > 0))
        {
            sb.append('?').append(tmp);
        }

        tmp = uURL.getRef();

        if ((tmp != null) && (tmp.length() > 0))
        {
            sb.append('#').append(tmp);
        }

        return sb.toString();
    }
}
