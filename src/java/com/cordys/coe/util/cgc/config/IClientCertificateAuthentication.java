package com.cordys.coe.util.cgc.config;

import com.cordys.coe.util.cgc.CordysGatewayClientException;

import java.net.URL;

/**
 * This interface describes the details needed for certificate-based autheentication.
 *
 * @author  pgussow
 */
public interface IClientCertificateAuthentication extends IAuthenticationConfiguration
{
    /**
     * This method gets the location of the client certificate.
     *
     * @return  The location of the client certificate.
     */
    String getCertificateLocation();

    /**
     * This method gets the password for the certificate file.
     *
     * @return  The password for the certificate file.
     */
    String getCertificatePassword();

    /**
     * This method gets the type for the certificate file.
     *
     * @return  The type for the certificate file.
     */
    String getCertificateType();

    /**
     * This method gets the truststore URL object.
     *
     * @return  The truststore URL object.
     */
    URL getClientCertificateURL();

    /**
     * This method sets the location of the client certificate.
     *
     * @param  sCertificateLocation  The location of the client certificate.
     */
    void setCertificateLocation(String sCertificateLocation);

    /**
     * This method sets the password for the certificate file.
     *
     * @param  sCertificatePassword  The password for the certificate file.
     */
    void setCertificatePassword(String sCertificatePassword);

    /**
     * This method sets the type for the certificate file.
     *
     * @param  sCertificateType  The type for the certificate file.
     */
    void setCertificateType(String sCertificateType);

    /**
     * This method will validate the current configuration. It will make sure that all the files are
     * available and that all fields are filled.
     *
     * @throws  CordysGatewayClientException  In case of any configuration errors.
     */
    void validate()
           throws CordysGatewayClientException;
}
