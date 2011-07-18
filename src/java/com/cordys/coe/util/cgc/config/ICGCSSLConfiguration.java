package com.cordys.coe.util.cgc.config;

import java.net.URL;

/**
 * This interface contains specific configuration options with regard to SSL.
 *
 * @author  pgussow
 */
public interface ICGCSSLConfiguration extends ICGCConfiguration
{
    /**
     * This method gets whether or not the server certificate will be accepted even though it has
     * expired or is not yet valid.
     *
     * @return  Whether or not the server certificate will be accepted even though it has expired or
     *          is not yet valid.
     */
    boolean getAcceptWhenExpired();

    /**
     * This method gets whether or not the server certificate is always accepted even though it's
     * invalid.
     *
     * @return  Whether or not the server certificate is always accepted even though it's invalid.
     */
    boolean getAcceptWhenInvalid();

    /**
     * This method gets the trust mode to use for the connection.
     *
     * @return  The trust mode to use for the connection.
     */
    ETrustMode getTrustMode();

    /**
     * This method gets the location of the trust store.
     *
     * @return  The location of the trust store.
     */
    String getTrustStore();

    /**
     * This method gets the password for the trust store.
     *
     * @return  The password for the trust store.
     */
    String getTrustStorePassword();

    /**
     * This method gets the type of the trust store.
     *
     * @return  The type of the trust store.
     */
    String getTrustStoreType();

    /**
     * This method gets the truststore URL object.
     *
     * @return  The truststore URL object.
     */
    URL getTrustStoreURL();

    /**
     * This method sets wether or not the server certificate will be accepted even though it has
     * expired or is not yet valid.
     *
     * @param  bAcceptWhenExpired  Whether or not the server certificate will be accepted even
     *                             though it has expired or is not yet valid.
     */
    void setAcceptWhenExpired(boolean bAcceptWhenExpired);

    /**
     * This method sets wether or not the server certificate is always accepted even though it's
     * invalid.
     *
     * @param  bAcceptWhenInvalid  Whether or not the server certificate is always accepted even
     *                             though it's invalid.
     */
    void setAcceptWhenInvalid(boolean bAcceptWhenInvalid);

    /**
     * This method sets the trust mode to use for the connection.
     *
     * @param  tmTrustMode  The trust mode to use for the connection.
     */
    void setTrustMode(ETrustMode tmTrustMode);

    /**
     * This method sets the location of the trust store.
     *
     * @param  sTrustStore  The location of the trust store.
     */
    void setTrustStore(String sTrustStore);

    /**
     * This method sets the password for the trust store.
     *
     * @param  sTrustStorePassword  The password for the trust store.
     */
    void setTrustStorePassword(String sTrustStorePassword);

    /**
     * This method sets the type of the trust store.
     *
     * @param  sTrustStoreType  The type of the trust store.
     */
    void setTrustStoreType(String sTrustStoreType);
}
