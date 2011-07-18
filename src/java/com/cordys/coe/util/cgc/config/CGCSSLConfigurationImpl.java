package com.cordys.coe.util.cgc.config;

import com.cordys.coe.util.cgc.CordysGatewayClientException;
import com.cordys.coe.util.cgc.message.CGCMessages;

import java.io.File;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class contains additional configuartion in case of SSL.
 *
 * @author  pgussow
 */
class CGCSSLConfigurationImpl extends CGCConfigurationImpl
    implements ICGCSSLConfiguration
{
    /**
     * Holds whether or not the server certificate will be accepted even though it has expired or is
     * not yet valid.
     */
    private boolean m_bAcceptWhenExpired;
    /**
     * Holds whether or not the server certificate is always accepted even though it's invalid.
     */
    private boolean m_bAcceptWhenInvalid;
    /**
     * Holds the location of the trust store.
     */
    private String m_sTrustStore;
    /**
     * Holds the password for the trust store.
     */
    private String m_sTrustStorePassword;
    /**
     * Holds the type of the trust store.
     */
    private String m_sTrustStoreType;
    /**
     * DOCUMENTME.
     */
    private ETrustMode m_tmTrustMode;
    /**
     * Holds the URL to the trust store.
     */
    private URL m_uTrustStore;

    /**
     * This method gets whether or not the server certificate will be accepted even though it has
     * expired or is not yet valid.
     *
     * @return  Whether or not the server certificate will be accepted even though it has expired or
     *          is not yet valid.
     *
     * @see     com.cordys.coe.util.cgc.config.ICGCSSLConfiguration#getAcceptWhenExpired()
     */
    public boolean getAcceptWhenExpired()
    {
        return m_bAcceptWhenExpired;
    }

    /**
     * This method gets whether or not the server certificate is always accepted even though it's
     * invalid.
     *
     * @return  Whether or not the server certificate is always accepted even though it's invalid.
     *
     * @see     com.cordys.coe.util.cgc.config.ICGCSSLConfiguration#getAcceptWhenInvalid()
     */
    public boolean getAcceptWhenInvalid()
    {
        return m_bAcceptWhenInvalid;
    }

    /**
     * This method gets the trust mode to use for the connection.
     *
     * @return  The trust mode to use for the connection.
     *
     * @see     com.cordys.coe.util.cgc.config.ICGCSSLConfiguration#getTrustMode()
     */
    public ETrustMode getTrustMode()
    {
        return m_tmTrustMode;
    }

    /**
     * This method gets the location of the trust store.
     *
     * @return  The location of the trust store.
     *
     * @see     com.cordys.coe.util.cgc.config.ICGCSSLConfiguration#getTrustStore()
     */
    public String getTrustStore()
    {
        return m_sTrustStore;
    }

    /**
     * This method gets the password for the trust store.
     *
     * @return  The password for the trust store.
     *
     * @see     com.cordys.coe.util.cgc.config.ICGCSSLConfiguration#getTrustStorePassword()
     */
    public String getTrustStorePassword()
    {
        return m_sTrustStorePassword;
    }

    /**
     * This method gets the type of the trust store.
     *
     * @return  The type of the trust store.
     *
     * @see     com.cordys.coe.util.cgc.config.ICGCSSLConfiguration#getTrustStoreType()
     */
    public String getTrustStoreType()
    {
        return m_sTrustStoreType;
    }

    /**
     * This method gets the truststore URL object.
     *
     * @return  The truststore URL object.
     *
     * @see     com.cordys.coe.util.cgc.config.ICGCSSLConfiguration#getTrustStoreURL()
     */
    public URL getTrustStoreURL()
    {
        return m_uTrustStore;
    }

    /**
     * This method sets whether or not the server certificate will be accepted even though it has
     * expired or is not yet valid.
     *
     * @param  bAcceptWhenExpired  Whether or not the server certificate will be accepted even
     *                             though it has expired or is not yet valid.
     *
     * @see    com.cordys.coe.util.cgc.config.ICGCSSLConfiguration#setAcceptWhenExpired(boolean)
     */
    public void setAcceptWhenExpired(boolean bAcceptWhenExpired)
    {
        m_bAcceptWhenExpired = bAcceptWhenExpired;
    }

    /**
     * This method sets whether or not the server certificate is always accepted even though it's
     * invalid.
     *
     * @param  bAcceptWhenInvalid  Whether or not the server certificate is always accepted even
     *                             though it's invalid.
     *
     * @see    com.cordys.coe.util.cgc.config.ICGCSSLConfiguration#setAcceptWhenInvalid(boolean)
     */
    public void setAcceptWhenInvalid(boolean bAcceptWhenInvalid)
    {
        m_bAcceptWhenInvalid = bAcceptWhenInvalid;
    }

    /**
     * This method sets the trust mode to use for the connection.
     *
     * @param  tmTrustMode  The trust mode to use for the connection.
     *
     * @see    com.cordys.coe.util.cgc.config.ICGCSSLConfiguration#setTrustMode(com.cordys.coe.util.cgc.config.ETrustMode)
     */
    public void setTrustMode(ETrustMode tmTrustMode)
    {
        m_tmTrustMode = tmTrustMode;
    }

    /**
     * This method sets the location of the trust store.
     *
     * @param  sTrustStore  The location of the trust store.
     *
     * @see    com.cordys.coe.util.cgc.config.ICGCSSLConfiguration#setTrustStore(java.lang.String)
     */
    public void setTrustStore(String sTrustStore)
    {
        m_sTrustStore = sTrustStore;
    }

    /**
     * This method sets the password for the trust store.
     *
     * @param  sTrustStorePassword  The password for the trust store.
     *
     * @see    com.cordys.coe.util.cgc.config.ICGCSSLConfiguration#setTrustStorePassword(java.lang.String)
     */
    public void setTrustStorePassword(String sTrustStorePassword)
    {
        m_sTrustStorePassword = sTrustStorePassword;
    }

    /**
     * This method sets the type of the trust store.
     *
     * @param  sTrustStoreType  The type of the trust store.
     *
     * @see    com.cordys.coe.util.cgc.config.ICGCSSLConfiguration#setTrustStoreType(java.lang.String)
     */
    public void setTrustStoreType(String sTrustStoreType)
    {
        m_sTrustStoreType = sTrustStoreType;
    }

    /**
     * This method validates the configuration to make sure it contains the minimum configuration to
     * connect to a server.
     *
     * @throws  CordysGatewayClientException  In case the configuration is not valid.
     *
     * @see     com.cordys.coe.util.cgc.config.ICGCConfiguration#validate()
     * @see     com.cordys.coe.util.cgc.config.CGCConfigurationImpl#validate()
     */
    @Override public void validate()
                            throws CordysGatewayClientException
    {
        super.validate();

        // Check the trust store if it should be used.
        if (m_tmTrustMode == ETrustMode.USE_TRUSTORE)
        {
            if ((m_sTrustStore == null) || (m_sTrustStore.length() == 0))
            {
                throw new CordysGatewayClientException(CGCMessages.CGC_ERROR_SSL_TRUSTSTORE_MISSING);
            }

            File fTrustStore = new File(m_sTrustStore);

            if (!fTrustStore.exists())
            {
                throw new CordysGatewayClientException(CGCMessages.CGC_ERROR_SSL_TRUSTSTORE_NOT_EXISTS,
                                                       fTrustStore.getAbsolutePath());
            }

            try
            {
                m_uTrustStore = fTrustStore.toURI().toURL();
            }
            catch (MalformedURLException e)
            {
                throw new CordysGatewayClientException(e,
                                                       CGCMessages.CGC_ERROR_SSL_TRUSTSTORE_CONVERT);
            }
        }
    }
}
