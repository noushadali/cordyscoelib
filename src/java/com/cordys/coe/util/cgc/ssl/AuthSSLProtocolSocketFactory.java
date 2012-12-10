package com.cordys.coe.util.cgc.ssl;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Enumeration;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;

import com.cordys.coe.util.cgc.config.ETrustMode;
import com.cordys.coe.util.cgc.config.ICGCSSLConfiguration;
import com.cordys.coe.util.cgc.config.IClientCertificateAuthentication;

/**
 * This class is used to create the SSLSocketFactory for the HTTP client. This class can operate in several different modes:<br>
 * 1. Client certificate authentication or not 2. Use a separate trust store or not.
 * 
 * @author Oleg Kalnichevski
 * @author pgussow
 */
public class AuthSSLProtocolSocketFactory extends SSLSocketFactory
{
    /**
     * Log object for this class.
     */
    private static final Log LOG = LogFactory.getLog(AuthSSLProtocolSocketFactory.class);
    /**
     * Identifies the keystoretype for PKCS12.
     */
    private static final Object KSTYPE_PKCKS12 = "PKCS12";
    /**
     * String identifier of the provider for Bouncy Castle.
     */
    private static final String BOUNCY_CASTLE_PROVIDER = "BC";

    /**
     * Gets the single instance of AuthSSLProtocolSocketFactory.
     * 
     * @param cgcSSLConfig the cgc ssl config
     * @param ccaClientCertificate the cca client certificate
     * @return single instance of AuthSSLProtocolSocketFactory
     */
    public static AuthSSLProtocolSocketFactory getInstance(ICGCSSLConfiguration cgcSSLConfig,
            IClientCertificateAuthentication ccaClientCertificate)
    {
        SSLContext context = createSSLContext(cgcSSLConfig, ccaClientCertificate);

        AuthSSLProtocolSocketFactory retVal = new AuthSSLProtocolSocketFactory(context);

        return retVal;
    }

    /**
     * Constructor. Creates the object based on the CGC configuration and optional client certificate authentication.
     */
    private AuthSSLProtocolSocketFactory(SSLContext context)
    {
        super(context);
    }

    /**
     * This method creates the key managers for a specific keystore.
     * 
     * @param keystore The actual keystore.
     * @param password The keystore password.
     * @return The list of key managers.
     * @throws KeyStoreException DOCUMENTME
     * @throws NoSuchAlgorithmException DOCUMENTME
     * @throws UnrecoverableKeyException DOCUMENTME
     */
    private static KeyManager[] createKeyManagers(final KeyStore keystore, final String password) throws KeyStoreException,
            NoSuchAlgorithmException, UnrecoverableKeyException
    {
        if (keystore == null)
        {
            throw new IllegalArgumentException("Keystore may not be null");
        }

        if (LOG.isDebugEnabled())
        {
            LOG.debug("Initializing key manager");
        }

        KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmfactory.init(keystore, (password != null) ? password.toCharArray() : null);
        return kmfactory.getKeyManagers();
    }

    /**
     * This method creates the keystore from the given URL.
     * 
     * @param uURL URL to the keystore.
     * @param sPassword The password for the keystore.
     * @param sKeyStoreType The type of keystore.
     * @return The KeyStore instance.
     * @throws KeyStoreException DOCUMENTME
     * @throws NoSuchAlgorithmException DOCUMENTME
     * @throws CertificateException DOCUMENTME
     * @throws IOException DOCUMENTME
     */
    private static KeyStore createKeyStore(final URL uURL, final String sPassword, String sKeyStoreType)
            throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException
    {
        KeyStore ksReturn = null;

        if (uURL == null)
        {
            throw new IllegalArgumentException("Keystore url may not be null");
        }

        if (LOG.isDebugEnabled())
        {
            LOG.debug("Initializing key store");
        }

        if (KSTYPE_PKCKS12.equals(sKeyStoreType))
        {
            try
            {
                if (LOG.isDebugEnabled())
                {
                    LOG.debug("Creating keystore using provider " + BOUNCY_CASTLE_PROVIDER);
                }
                ksReturn = KeyStore.getInstance(sKeyStoreType, BOUNCY_CASTLE_PROVIDER);
            }
            catch (NoSuchProviderException e)
            {
                LOG.warn("Error creating keystore with provider \"" + BOUNCY_CASTLE_PROVIDER
                        + "\"\n Going to try it using the default.", e);
                ksReturn = KeyStore.getInstance(sKeyStoreType);
            }
        }
        else
        {
            ksReturn = KeyStore.getInstance(sKeyStoreType);
        }

        if (LOG.isDebugEnabled())
        {
            LOG.debug("Loading the keystore.\nKeystore information:\nName: " + ksReturn.getProvider().getName() + "\nInfo: "
                    + ksReturn.getProvider().getInfo());
        }
        ksReturn.load(uURL.openStream(), (sPassword != null) ? sPassword.toCharArray() : null);
        return ksReturn;
    }

    /**
     * DOCUMENTME.
     * 
     * @param keystore DOCUMENTME
     * @return DOCUMENTME
     * @throws KeyStoreException DOCUMENTME
     * @throws NoSuchAlgorithmException DOCUMENTME
     */
    private static TrustManager[] createTrustManagers(final KeyStore keystore) throws KeyStoreException, NoSuchAlgorithmException
    {
        if (keystore == null)
        {
            throw new IllegalArgumentException("Keystore may not be null");
        }

        if (LOG.isDebugEnabled())
        {
            LOG.debug("Initializing trust manager");
        }

        TrustManagerFactory tmfactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmfactory.init(keystore);

        TrustManager[] trustmanagers = tmfactory.getTrustManagers();

        for (int i = 0; i < trustmanagers.length; i++)
        {
            if (trustmanagers[i] instanceof X509TrustManager)
            {
                trustmanagers[i] = new AuthSSLX509TrustManager((X509TrustManager) trustmanagers[i]);
            }
        }
        return trustmanagers;
    }

    /**
     * This method will check the validy of the certificates in the given keystore. Based on the configuration it will log an
     * error.
     * 
     * @param ksKeyStore The keystore to check.
     * @param config The SSL configuration details
     * @throws KeyStoreException In case any problem with the keystore occurs.
     * @throws CertificateExpiredException If a certificate is expired.
     * @throws CertificateNotYetValidException If a certificate is invalid.
     */
    private static void checkCertificates(KeyStore ksKeyStore, ICGCSSLConfiguration config) throws KeyStoreException,
            CertificateExpiredException, CertificateNotYetValidException
    {
        // Check the validity of the certificates.
        Enumeration<String> aliases = ksKeyStore.aliases();

        while (aliases.hasMoreElements())
        {
            String alias = (String) aliases.nextElement();
            Certificate[] certs = ksKeyStore.getCertificateChain(alias);

            if (certs != null)
            {
                if (LOG.isDebugEnabled())
                {
                    LOG.debug("Certificate chain '" + alias + "':");
                }

                for (int c = 0; c < certs.length; c++)
                {
                    if (certs[c] instanceof X509Certificate)
                    {
                        X509Certificate cert = (X509Certificate) certs[c];

                        if (LOG.isDebugEnabled())
                        {
                            LOG.debug(" Certificate " + (c + 1) + ":");
                            LOG.debug("  Subject DN: " + cert.getSubjectDN());
                            LOG.debug("  Signature Algorithm: " + cert.getSigAlgName());
                            LOG.debug("  Valid from: " + cert.getNotBefore());
                            LOG.debug("  Valid until: " + cert.getNotAfter());
                            LOG.debug("  Issuer: " + cert.getIssuerDN());
                        }

                        try
                        {
                            cert.checkValidity(new Date());
                        }
                        catch (CertificateExpiredException cee)
                        {
                            LOG.error("Certificate expired: " + cert.getSubjectDN() + " since " + cert.getNotAfter());

                            if (!config.getAcceptWhenExpired())
                            {
                                throw cee;
                            }
                        }
                        catch (CertificateNotYetValidException cnyv)
                        {
                            LOG.error("Certificate not yet valid: " + cert.getSubjectDN() + " after " + cert.getNotBefore());

                            if (!config.getAcceptWhenInvalid())
                            {
                                throw cnyv;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * This method creates the actual SSL context.
     * 
     * @param clientCert The client certificate that should be used.
     * @param config The SSL configuration details
     * @return The SSL context.
     */
    private static SSLContext createSSLContext(ICGCSSLConfiguration config, IClientCertificateAuthentication clientCert)
    {
        try
        {
            KeyManager[] keymanagers = new KeyManager[0];
            TrustManager[] trustmanagers = new TrustManager[0];

            if (clientCert != null)
            {
                // We're using a client certificate to connect to Cordys.
                KeyStore ksClientStore = createKeyStore(clientCert.getClientCertificateURL(),
                        clientCert.getCertificatePassword(), clientCert.getCertificateType());

                if ((config.getTrustMode() != ETrustMode.TRUST_EVERY_SERVER)
                        && (!config.getAcceptWhenExpired() || !config.getAcceptWhenInvalid()))
                {
                    // We need to check and validate the client certificate
                    checkCertificates(ksClientStore, config);
                }

                // Create the key manager around the store.
                keymanagers = createKeyManagers(ksClientStore, clientCert.getCertificatePassword());
            }

            // Now initialize the trust store manager if it is configured.
            if (config.getTrustMode() == ETrustMode.USE_TRUSTORE)
            {
                KeyStore ksTrustStore = createKeyStore(config.getTrustStoreURL(), config.getTrustStorePassword(),
                        config.getTrustStoreType());

                if ((!config.getAcceptWhenExpired() || !config.getAcceptWhenInvalid()))
                {
                    // We need to check and validate the client certificate
                    checkCertificates(ksTrustStore, config);
                }

                // Create the trust manager.
                trustmanagers = createTrustManagers(ksTrustStore);
            }
            else if (config.getTrustMode() == ETrustMode.TRUST_EVERY_SERVER)
            {
                // Create the trust manager that will accept anything
                trustmanagers = new TrustManager[] { new AuthSSLX509TrustManager() };
            }

            SSLContext sslcontext = SSLContext.getInstance("SSL");
            sslcontext.init(keymanagers, trustmanagers, null);
            return sslcontext;
        }
        catch (NoSuchAlgorithmException e)
        {
            LOG.error(e.getMessage(), e);
            throw new AuthSSLInitializationError("Unsupported algorithm exception: " + e.getMessage());
        }
        catch (KeyStoreException e)
        {
            LOG.error(e.getMessage(), e);
            throw new AuthSSLInitializationError("Keystore exception: " + e.getMessage());
        }
        catch (GeneralSecurityException e)
        {
            LOG.error(e.getMessage(), e);
            throw new AuthSSLInitializationError("Key management exception: " + e.getMessage());
        }
        catch (IOException e)
        {
            LOG.error(e.getMessage(), e);
            throw new AuthSSLInitializationError("I/O error reading keystore/truststore file: " + e.getMessage());
        }
    }
}
