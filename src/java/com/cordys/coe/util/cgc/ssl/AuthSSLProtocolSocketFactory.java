package com.cordys.coe.util.cgc.ssl;

import com.cordys.coe.util.cgc.config.ETrustMode;
import com.cordys.coe.util.cgc.config.ICGCSSLConfiguration;
import com.cordys.coe.util.cgc.config.IClientCertificateAuthentication;

import java.io.IOException;

import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

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

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.ControllerThreadSocketFactory;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is used to create the SSLSocketFactory for the HTTP client. This class can operate in
 * several different modes:<br>
 * 1. Client certificate authentication or not 2. Use a separate trust store or not.
 *
 * @author  Oleg Kalnichevski
 * @author  pgussow
 */
public class AuthSSLProtocolSocketFactory
    implements ProtocolSocketFactory
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
     * Holds the details for the client certificate.
     */
    private IClientCertificateAuthentication m_ccaClientCertificate;

    /**
     * Holds the SSL configuration.
     */
    private ICGCSSLConfiguration m_cgcSSLConfig;
    /**
     * Holds the SSL context.
     */
    private SSLContext m_scSSLContext = null;

    /**
     * Constructor. Creates the object based on the CGC configuration and optional client
     * certificate authentication.
     *
     * @param  cgcSSLConfig  The SSL configuration for the CGC.
     */
    public AuthSSLProtocolSocketFactory(ICGCSSLConfiguration cgcSSLConfig)
    {
        this(cgcSSLConfig, null);
    }

    /**
     * Constructor. Creates the object based on the CGC configuration and optional client
     * certificate authentication.
     *
     * @param  cgcSSLConfig          The SSL configuration for the CGC.
     * @param  ccaClientCertificate  The optional client certificate.
     */
    public AuthSSLProtocolSocketFactory(ICGCSSLConfiguration cgcSSLConfig,
                                        IClientCertificateAuthentication ccaClientCertificate)
    {
        m_cgcSSLConfig = cgcSSLConfig;
        m_ccaClientCertificate = ccaClientCertificate;
    }

    /**
     * This method creates the socket based on the host name and the port.
     *
     * @param   sHost  The name of the host.
     * @param   iPort  The port number.
     *
     * @return  The newly created socket.
     *
     * @throws  IOException           In case of any IOExceptions.
     * @throws  UnknownHostException  If the host could not be found.
     *
     * @see     org.apache.commons.httpclient.protocol.ProtocolSocketFactory#createSocket(java.lang.String,
     *          int)
     */
    public Socket createSocket(String sHost, int iPort)
                        throws IOException, UnknownHostException
    {
        return getSSLContext().getSocketFactory().createSocket(sHost, iPort);
    }

    /**
     * This method creates the socket based on the host name and the port.
     *
     * @param   sHost         The name of the host.
     * @param   iPort         The port number.
     * @param   iaClientHost  The client host name.
     * @param   iClientPort   The client port.
     *
     * @return  The newly created socket.
     *
     * @throws  IOException           In case of any IOExceptions.
     * @throws  UnknownHostException  If the host could not be found.
     *
     * @see     org.apache.commons.httpclient.protocol.ProtocolSocketFactory#createSocket(java.lang.String,
     *          int, java.net.InetAddress, int)
     */
    public Socket createSocket(String sHost, int iPort, InetAddress iaClientHost,
                               int iClientPort)
                        throws IOException, UnknownHostException
    {
        return getSSLContext().getSocketFactory().createSocket(sHost, iPort, iaClientHost,
                                                               iClientPort);
    }

    /**
     * This method creates a socket.
     *
     * @param   sSocket     The source socket.
     * @param   sHost       The hostname.
     * @param   iPort       The portnumber.
     * @param   bAutoClose  Whether or not to automatically close the socket.
     *
     * @return  The SSL socket.
     *
     * @throws  IOException           In case of any IOExceptions.
     * @throws  UnknownHostException  If the host could not be found.
     */
    public Socket createSocket(Socket sSocket, String sHost, int iPort, boolean bAutoClose)
                        throws IOException, UnknownHostException
    {
        return getSSLContext().getSocketFactory().createSocket(sSocket, sHost, iPort, bAutoClose);
    }

    /**
     * Attempts to get a new socket connection to the given host within the given time limit.
     *
     * <p>To circumvent the limitations of older JREs that do not support connect timeout a
     * controller thread is executed. The controller thread attempts to create a new socket within
     * the given limit of time. If socket constructor does not return until the timeout expires, the
     * controller terminates and throws an {@link ConnectTimeoutException}</p>
     *
     * @param   sHost           the host name/IP
     * @param   iPort           the port on the host
     * @param   iaLocalAddress  the local host name/IP to bind the socket to
     * @param   iLocalPort      the port on the local machine
     * @param   hcpParams {@link HttpConnectionParams Http connection parameters}
     *
     * @return  Socket a new socket
     *
     * @throws  IOException              if an I/O error occurs while creating the socket
     * @throws  UnknownHostException     if the IP address of the host cannot be determined
     * @throws  ConnectTimeoutException  When the connection times out.
     */
    public Socket createSocket(final String sHost, final int iPort,
                               final InetAddress iaLocalAddress, final int iLocalPort,
                               final HttpConnectionParams hcpParams)
                        throws IOException, UnknownHostException, ConnectTimeoutException
    {
        if (hcpParams == null)
        {
            throw new IllegalArgumentException("Parameters may not be null");
        }

        int timeout = hcpParams.getConnectionTimeout();

        if (timeout == 0)
        {
            return createSocket(sHost, iPort, iaLocalAddress, iLocalPort);
        }

        // To be eventually deprecated when migrated to Java 1.4 or above
        return ControllerThreadSocketFactory.createSocket(this, sHost, iPort, iaLocalAddress,
                                                          iLocalPort, timeout);
    }

    /**
     * This method creates the key managers for a specific keystore.
     *
     * @param   keystore  The actual keystore.
     * @param   password  The keystore password.
     *
     * @return  The list of key managers.
     *
     * @throws  KeyStoreException          DOCUMENTME
     * @throws  NoSuchAlgorithmException   DOCUMENTME
     * @throws  UnrecoverableKeyException  DOCUMENTME
     */
    private static KeyManager[] createKeyManagers(final KeyStore keystore, final String password)
                                           throws KeyStoreException, NoSuchAlgorithmException,
                                                  UnrecoverableKeyException
    {
        if (keystore == null)
        {
            throw new IllegalArgumentException("Keystore may not be null");
        }

        if (LOG.isDebugEnabled())
        {
            LOG.debug("Initializing key manager");
        }

        KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory
                                                                    .getDefaultAlgorithm());
        kmfactory.init(keystore, (password != null) ? password.toCharArray() : null);
        return kmfactory.getKeyManagers();
    }

    /**
     * This method creates the keystore from the given URL.
     *
     * @param   uURL           URL to the keystore.
     * @param   sPassword      The password for the keystore.
     * @param   sKeyStoreType  The type of keystore.
     *
     * @return  The KeyStore instance.
     *
     * @throws  KeyStoreException         DOCUMENTME
     * @throws  NoSuchAlgorithmException  DOCUMENTME
     * @throws  CertificateException      DOCUMENTME
     * @throws  IOException               DOCUMENTME
     */
    private static KeyStore createKeyStore(final URL uURL, final String sPassword,
                                           String sKeyStoreType)
                                    throws KeyStoreException, NoSuchAlgorithmException,
                                           CertificateException, IOException
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
                LOG.warn("Error creating keystore with provider \"" + BOUNCY_CASTLE_PROVIDER +
                         "\"\n Going to try it using the default.", e);
                ksReturn = KeyStore.getInstance(sKeyStoreType);
            }
        }
        else
        {
            ksReturn = KeyStore.getInstance(sKeyStoreType);
        }

        if (LOG.isDebugEnabled())
        {
            LOG.debug("Loading the keystore.\nKeystore information:\nName: " +
                      ksReturn.getProvider().getName() + "\nInfo: " +
                      ksReturn.getProvider().getInfo());
        }
        ksReturn.load(uURL.openStream(), (sPassword != null) ? sPassword.toCharArray() : null);
        return ksReturn;
    }

    /**
     * DOCUMENTME.
     *
     * @param   keystore  DOCUMENTME
     *
     * @return  DOCUMENTME
     *
     * @throws  KeyStoreException         DOCUMENTME
     * @throws  NoSuchAlgorithmException  DOCUMENTME
     */
    private static TrustManager[] createTrustManagers(final KeyStore keystore)
                                               throws KeyStoreException, NoSuchAlgorithmException
    {
        if (keystore == null)
        {
            throw new IllegalArgumentException("Keystore may not be null");
        }

        if (LOG.isDebugEnabled())
        {
            LOG.debug("Initializing trust manager");
        }

        TrustManagerFactory tmfactory = TrustManagerFactory.getInstance(TrustManagerFactory
                                                                        .getDefaultAlgorithm());
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
     * This method will check the validy of the certificates in the given keystore. Based on the
     * configuration it will log an error.
     *
     * @param   ksKeyStore  The keystore to check.
     *
     * @throws  KeyStoreException                In case any problem with the keystore occurs.
     * @throws  CertificateExpiredException      If a certificate is expired.
     * @throws  CertificateNotYetValidException  If a certificate is invalid.
     */
    private void checkCertificates(KeyStore ksKeyStore)
                            throws KeyStoreException, CertificateExpiredException,
                                   CertificateNotYetValidException
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
                            LOG.error("Certificate expired: " + cert.getSubjectDN() + " since " +
                                      cert.getNotAfter());

                            if (!m_cgcSSLConfig.getAcceptWhenExpired())
                            {
                                throw cee;
                            }
                        }
                        catch (CertificateNotYetValidException cnyv)
                        {
                            LOG.error("Certificate not yet valid: " + cert.getSubjectDN() +
                                      " after " + cert.getNotBefore());

                            if (!m_cgcSSLConfig.getAcceptWhenInvalid())
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
     * @return  The SSL context.
     */
    private SSLContext createSSLContext()
    {
        try
        {
            KeyManager[] keymanagers = new KeyManager[0];
            TrustManager[] trustmanagers = new TrustManager[0];

            if (m_ccaClientCertificate != null)
            {
                // We're using a client certificate to connect to Cordys.
                KeyStore ksClientStore = createKeyStore(m_ccaClientCertificate
                                                        .getClientCertificateURL(),
                                                        m_ccaClientCertificate
                                                        .getCertificatePassword(),
                                                        m_ccaClientCertificate
                                                        .getCertificateType());

                if ((m_cgcSSLConfig.getTrustMode() != ETrustMode.TRUST_EVERY_SERVER) &&
                        (!m_cgcSSLConfig.getAcceptWhenExpired() ||
                             !m_cgcSSLConfig.getAcceptWhenInvalid()))
                {
                    // We need to check and validate the client certificate
                    checkCertificates(ksClientStore);
                }

                // Create the key manager around the store.
                keymanagers = createKeyManagers(ksClientStore,
                                                m_ccaClientCertificate.getCertificatePassword());
            }

            // Now initialize the trust store manager if it is configured.
            if (m_cgcSSLConfig.getTrustMode() == ETrustMode.USE_TRUSTORE)
            {
                KeyStore ksTrustStore = createKeyStore(m_cgcSSLConfig.getTrustStoreURL(),
                                                       m_cgcSSLConfig.getTrustStorePassword(),
                                                       m_cgcSSLConfig.getTrustStoreType());

                if ((!m_cgcSSLConfig.getAcceptWhenExpired() ||
                         !m_cgcSSLConfig.getAcceptWhenInvalid()))
                {
                    // We need to check and validate the client certificate
                    checkCertificates(ksTrustStore);
                }

                // Create the trust manager.
                trustmanagers = createTrustManagers(ksTrustStore);
            }
            else if (m_cgcSSLConfig.getTrustMode() == ETrustMode.TRUST_EVERY_SERVER)
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
            throw new AuthSSLInitializationError("Unsupported algorithm exception: " +
                                                 e.getMessage());
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
            throw new AuthSSLInitializationError("I/O error reading keystore/truststore file: " +
                                                 e.getMessage());
        }
    }

    /**
     * This method returns the SSL context to use.
     *
     * @return  The SSL context
     */
    private SSLContext getSSLContext()
    {
        if (m_scSSLContext == null)
        {
            m_scSSLContext = createSSLContext();
        }
        return m_scSSLContext;
    }
}
