package com.cordys.coe.util.cgc.config;

import com.cordys.coe.util.cgc.CordysGatewayClientException;
import com.cordys.coe.util.cgc.message.CGCMessages;

import java.io.File;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class contains the.
 *
 * @author  pgussow
 */
class ClientCertificateAuthenticationImpl
    implements IClientCertificateAuthentication
{
    /**
     * Holds the location of the client certificate.
     */
    private String m_sCertificateLocation;
    /**
     * Holds the password for the certificate file.
     */
    private String m_sCertificatePassword;
    /**
     * Holds the type for the certificate file.
     */
    private String m_sCertificateType;
    /**
     * Holds the URL to the client certificate store.
     */
    private URL m_uAuthCertificateStore;

    /**
     * This method gets the location of the client certificate.
     *
     * @return  The location of the client certificate.
     *
     * @see     com.cordys.coe.util.cgc.config.IClientCertificateAuthentication#getCertificateLocation()
     */
    public String getCertificateLocation()
    {
        return m_sCertificateLocation;
    }

    /**
     * This method gets the password for the certificate file.
     *
     * @return  The password for the certificate file.
     *
     * @see     com.cordys.coe.util.cgc.config.IClientCertificateAuthentication#getCertificatePassword()
     */
    public String getCertificatePassword()
    {
        return m_sCertificatePassword;
    }

    /**
     * This method gets the type for the certificate file.
     *
     * @return  The type for the certificate file.
     *
     * @see     com.cordys.coe.util.cgc.config.IClientCertificateAuthentication#getCertificateType()
     */
    public String getCertificateType()
    {
        return m_sCertificateType;
    }

    /**
     * This method gets the truststore URL object.
     *
     * @return  The truststore URL object.
     *
     * @see     com.cordys.coe.util.cgc.config.IClientCertificateAuthentication#getClientCertificateURL()
     */
    public URL getClientCertificateURL()
    {
        return m_uAuthCertificateStore;
    }

    /**
     * This method sets the location of the client certificate.
     *
     * @param  sCertificateLocation  The location of the client certificate.
     *
     * @see    com.cordys.coe.util.cgc.config.IClientCertificateAuthentication#setCertificateLocation(java.lang.String)
     */
    public void setCertificateLocation(String sCertificateLocation)
    {
        m_sCertificateLocation = sCertificateLocation;
    }

    /**
     * This method sets the password for the certificate file.
     *
     * @param  sCertificatePassword  The password for the certificate file.
     *
     * @see    com.cordys.coe.util.cgc.config.IClientCertificateAuthentication#setCertificatePassword(java.lang.String)
     */
    public void setCertificatePassword(String sCertificatePassword)
    {
        m_sCertificatePassword = sCertificatePassword;
    }

    /**
     * This method sets the type for the certificate file.
     *
     * @param  sCertificateType  The type for the certificate file.
     *
     * @see    com.cordys.coe.util.cgc.config.IClientCertificateAuthentication#setCertificateType(java.lang.String)
     */
    public void setCertificateType(String sCertificateType)
    {
        m_sCertificateType = sCertificateType;
    }

    /**
     * This method will validate the current configuration. It will make sure that all the files are
     * available and that all fields are filled.
     *
     * @throws  CordysGatewayClientException  In case of any configuration errors.
     *
     * @see     com.cordys.coe.util.cgc.config.IClientCertificateAuthentication#validate()
     */
    public void validate()
                  throws CordysGatewayClientException
    {
        // Check the client certificate.
        if ((m_sCertificateLocation == null) || (m_sCertificateLocation.length() == 0))
        {
            throw new CordysGatewayClientException(CGCMessages.CGC_ERROR_SSL_CERTIFICATE_MISSING);
        }

        File fAuthCertificateStore = new File(m_sCertificateLocation);

        if (!fAuthCertificateStore.exists())
        {
            throw new CordysGatewayClientException(CGCMessages.CGC_ERROR_SSL_CERTIFICATE_NOT_EXISTS,
                                                   fAuthCertificateStore.getAbsolutePath());
        }

        try
        {
            m_uAuthCertificateStore = fAuthCertificateStore.toURI().toURL();
        }
        catch (MalformedURLException e)
        {
            throw new CordysGatewayClientException(e,
                                                   CGCMessages.CGC_ERROR_SSL_CERTIFICATE_CONVERT);
        }
    }
}
