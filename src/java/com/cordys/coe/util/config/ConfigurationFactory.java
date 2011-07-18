package com.cordys.coe.util.config;

import com.cordys.coe.util.config.impl.NativeConfiguration;
import com.cordys.coe.util.config.impl.WebGatewayConfiguration;

import org.w3c.dom.Element;

/**
 * This class creates the proper configurations based on the XML which is provided.
 *
 * @author  pgussow
 */
public class ConfigurationFactory
{
    /**
     * Identifies configurations of type web gateway.
     */
    public static final String TYPE_WEBGATEWAY = "webgateway";
    /**
     * Identifies configurations of type native.
     */
    public static final String TYPE_NATIVE = "native";

    /**
     * This method creates the configuration based on the XML.
     *
     * @param   nConfigNode  The configuration node to parse.
     *
     * @return  The created configuration.
     *
     * @throws  ConfigurationManagerException  DOCUMENTME
     */
    public static IConfiguration createConfiguration(Element nConfigNode)
                                              throws ConfigurationManagerException
    {
        IConfiguration cReturn = null;

        if (nConfigNode == null)
        {
            throw new ConfigurationManagerException(ConfigurationManagerException.EC_CONFIGURATION,
                                                    "Configuration node connot be null.");
        }

        String sType = nConfigNode.getAttribute("type");

        if ((sType == null) || (sType.length() == 0))
        {
            throw new ConfigurationManagerException(ConfigurationManagerException.EC_CONFIGURATION,
                                                    "The type is not set on the XML.");
        }

        if (sType.equals(TYPE_WEBGATEWAY))
        {
            cReturn = createWebGatewayConfig(nConfigNode);
        }
        else if (sType.equals(TYPE_NATIVE))
        {
            cReturn = createNativeConfig(nConfigNode);
        }
        else
        {
            throw new ConfigurationManagerException(ConfigurationManagerException.EC_CONFIGURATION,
                                                    "Unsupported config type: " + sType);
        }

        return cReturn;
    }

    /**
     * This method creates a new Native configuration.
     *
     * @param   sName            The name for the configuration.
     * @param   sLDAPServer      The servername.
     * @param   iLDAPPort        The LDAP port.
     * @param   sLDAPUsername    The LDAP username.
     * @param   sLDAPPassword    The LDAP password.
     * @param   sLDAPSearchRoot  The LDAP search root.
     * @param   bSSL             Whether or not the connection should be made via SSL.
     *
     * @return  The new configuration.
     */
    public static INativeConfiguration createNewNativeConfiguration(String sName,
                                                                    String sLDAPServer,
                                                                    int iLDAPPort,
                                                                    String sLDAPUsername,
                                                                    String sLDAPPassword,
                                                                    String sLDAPSearchRoot,
                                                                    boolean bSSL)
    {
        INativeConfiguration ncReturn = new NativeConfiguration();

        ncReturn.setName(sName);
        ncReturn.setServername(sLDAPServer);
        ncReturn.setPort(iLDAPPort);
        ncReturn.setLDAPUsername(sLDAPUsername);
        ncReturn.setLDAPPassword(sLDAPPassword);
        ncReturn.setLDAPSearchRoot(sLDAPSearchRoot);
        ncReturn.setSSL(bSSL);

        return ncReturn;
    }

    /**
     * This method creates a new configuration for the webgateway via domain authentication.
     *
     * @param   sName            The name for the configuration.
     * @param   sServer          The servername.
     * @param   iPort            The portnumber
     * @param   sGatewayURL      The gatewayURL.
     * @param   sDomainUsername  The domain username.
     * @param   sDomainPassword  The domain password.
     * @param   sDomain          The actual domain.
     *
     * @return  The new configuration object.
     */
    public static IWebGatewayConfiguration createNewWebGatewayConfiguration(String sName,
                                                                            String sServer,
                                                                            int iPort,
                                                                            String sGatewayURL,
                                                                            String sDomainUsername,
                                                                            String sDomainPassword,
                                                                            String sDomain)
    {
        IWebGatewayConfiguration wgcReturn = new WebGatewayConfiguration();
        wgcReturn.setName(sName);
        wgcReturn.setServername(sServer);
        wgcReturn.setPort(iPort);
        wgcReturn.setGatewayURL(sGatewayURL);
        wgcReturn.setDomainUsername(sDomainUsername);
        wgcReturn.setDomainPassword(sDomainPassword);
        wgcReturn.setDomain(sDomain);

        return wgcReturn;
    }

    /**
     * This method creates a new configuration for the webgateway via domain authentication.
     *
     * @param   sName                 The name for the configuration.
     * @param   sServer               The servername.
     * @param   iPort                 The portnumber
     * @param   sGatewayURL           The gatewayURL.
     * @param   sDomainUsername       The domain username.
     * @param   sDomainPassword       The domain password.
     * @param   sDomain               The actual domain.
     * @param   sCertificateLocation  The certificate location.
     * @param   sCertificatePassword  The certificate password.
     * @param   sCertificateType      The certificate type.
     * @param   sTrustStoreLocation   The trust store location.
     * @param   sTrustStorePassword   The trust store password.
     * @param   sTrustStoreType       The trust store type.
     *
     * @return  The new configuration object.
     */
    public static IWebGatewayConfiguration createNewWebGatewayConfiguration(String sName,
                                                                            String sServer,
                                                                            int iPort,
                                                                            String sGatewayURL,
                                                                            String sDomainUsername,
                                                                            String sDomainPassword,
                                                                            String sDomain,
                                                                            String sCertificateLocation,
                                                                            String sCertificatePassword,
                                                                            String sCertificateType,
                                                                            String sTrustStoreLocation,
                                                                            String sTrustStorePassword,
                                                                            String sTrustStoreType)
    {
        IWebGatewayConfiguration wgcReturn = new WebGatewayConfiguration();
        wgcReturn.setName(sName);
        wgcReturn.setServername(sServer);
        wgcReturn.setPort(iPort);
        wgcReturn.setGatewayURL(sGatewayURL);
        wgcReturn.setDomainUsername(sDomainUsername);
        wgcReturn.setDomainPassword(sDomainPassword);
        wgcReturn.setDomain(sDomain);
        wgcReturn.setCertificateLocation(sCertificateLocation);
        wgcReturn.setCertificatePassword(sCertificatePassword);
        wgcReturn.setCertificateType(sCertificateType);
        wgcReturn.setTrustStoreLocation(sTrustStoreLocation);
        wgcReturn.setTrustStorePassword(sTrustStorePassword);
        wgcReturn.setTrustStoreType(sTrustStoreType);

        return wgcReturn;
    }

    /**
     * This method creates the configuration object for the native configuration.
     *
     * @param   eConfigNode  The configuration XML.
     *
     * @return  The configuration for the native connections.
     */
    private static IConfiguration createNativeConfig(Element eConfigNode)
    {
        IConfiguration cReturn = new NativeConfiguration(eConfigNode);
        return cReturn;
    }

    /**
     * This method creates the configuration for a web-gateway connection.
     *
     * @param   eConfigNode  The configuration node.
     *
     * @return  The new WebGatewayConfiguration.
     */
    private static IConfiguration createWebGatewayConfig(Element eConfigNode)
    {
        IConfiguration cReturn = new WebGatewayConfiguration(eConfigNode);
        return cReturn;
    }
}
