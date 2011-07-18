package com.cordys.coe.util.cgc.config;

import com.cordys.coe.util.config.IWebGatewayConfiguration;

/**
 * This factory alows the creation of the authentication configurations.
 *
 * @author  pgussow
 */
public class CGCAuthenticationFactory
{
    /**
     * This method will create the authentication based on the WebGateway configuration object.
     *
     * @param   wgcConfig  The configuration object.
     *
     * @return  The proper authentication object.
     */
    public static IAuthenticationConfiguration createAuthentication(IWebGatewayConfiguration wgcConfig)
    {
        IAuthenticationConfiguration acReturn = null;

        switch (wgcConfig.getAuthenticationType())
        {
            case BASIC:
                acReturn = createUsernamePasswordAuthentication(wgcConfig.getDomainUsername(),
                                                                wgcConfig.getDomainPassword());
                break;

            case NTLM:
                acReturn = createNTLMAuthentication(wgcConfig.getDomainUsername(),
                                                    wgcConfig.getDomainPassword(),
                                                    wgcConfig.getDomain());
                break;

            case CLIENT_CERTIFICATE:
                acReturn = createClientCertificateAuthentication(wgcConfig.getCertificateLocation(),
                                                                 wgcConfig.getCertificatePassword(),
                                                                 wgcConfig.getCertificateType());
                break;

            case CORDYS_CUSTOM:
                acReturn = createCordysCustomAuthentication(wgcConfig.getDomainUsername(),
                                                            wgcConfig.getDomainPassword());
                break;

            case SSO:
                acReturn = createSSOAuthentication(wgcConfig.getDomainUsername(),
                                                   wgcConfig.getDomainPassword());
                break;
        }
        return acReturn;
    }

    /**
     * This method creates the authentication object if you want to use a certificate to
     * authenticate the session.
     *
     * @param   sCertificateLocation  The location of the pfx file.
     * @param   sCertificatePassword  The password of the certificate.
     * @param   sCertificateType      The keystore type of the passed on certificate.
     *
     * @return  The certificate based login authentication.
     */
    public static IClientCertificateAuthentication createClientCertificateAuthentication(String sCertificateLocation,
                                                                                         String sCertificatePassword,
                                                                                         String sCertificateType)
    {
        IClientCertificateAuthentication ccaReturn = new ClientCertificateAuthenticationImpl();

        ccaReturn.setCertificateLocation(sCertificateLocation);
        ccaReturn.setCertificatePassword(sCertificatePassword);
        ccaReturn.setCertificateType(sCertificateType);

        return ccaReturn;
    }

    /**
     * This method returns a authentication object based on Cordys custom authentication.
     *
     * @param   sUsername  The username to use.
     * @param   sPassword  The password to use.
     *
     * @return  The authentication object to use.
     */
    public static ICordysCustomAuthentication createCordysCustomAuthentication(String sUsername,
                                                                               String sPassword)
    {
        ICordysCustomAuthentication upaReturn = new CordysCustomAuthenticationImpl();

        upaReturn.setUsername(sUsername);
        upaReturn.setPassword(sPassword);
        upaReturn.setWCPSessionID(null);

        return upaReturn;
    }

    /**
     * This method returns a authentication object based on username/password.
     *
     * @param   sUsername  The username to use.
     * @param   sPassword  The password to use.
     * @param   sDomain    The domain to use.
     *
     * @return  The authentication object to use.
     */
    public static INTLMAuthentication createNTLMAuthentication(String sUsername, String sPassword,
                                                               String sDomain)
    {
        INTLMAuthentication naReturn = new NTLMAuthentication();

        naReturn.setUsername(sUsername);
        naReturn.setPassword(sPassword);
        naReturn.setDomain(sDomain);

        return naReturn;
    }

    /**
     * This method returns a authentication object based on username/password.
     *
     * @param   sUsername  The username to use.
     * @param   sPassword  The password to use.
     *
     * @return  The authentication object to use.
     */
    public static IUsernamePasswordAuthentication createUsernamePasswordAuthentication(String sUsername,
                                                                                       String sPassword)
    {
        IUsernamePasswordAuthentication upaReturn = new UsernamePasswordAuthentication();

        upaReturn.setUsername(sUsername);
        upaReturn.setPassword(sPassword);

        return upaReturn;
    }

    /**
     * This method creates the authentication based on SSO.
     *
     * @param   sUsername  The username to use.
     * @param   sPassword  The password to use.
     *
     * @return  The authentication object.
     */
    public static ISSOAuthentication createSSOAuthentication(String sUsername, String sPassword)
    {
        ISSOAuthentication saReturn = new SSOAuthentication();

        saReturn.setUsername(sUsername);
        saReturn.setPassword(sPassword);

        return saReturn;
    }
}
