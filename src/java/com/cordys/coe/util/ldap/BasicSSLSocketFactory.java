package com.cordys.coe.util.ldap;

import java.io.IOException;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.SocketFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;

/**
 * This simple SSL socket factory accepts all SSL connections automatically without checking the
 * certificates.
 *
 * @author  pgussow
 */
public class BasicSSLSocketFactory extends SocketFactory
{
    /**
     * Holds the logger that is used.
     */
    private static final Logger LOG = Logger.getLogger(BasicSSLSocketFactory.class);
    /**
     * Holds the SSL context.
     */
    private SSLContext m_scSSLContext = null;

    /**
     * This method returns the socket factory to use.
     *
     * @return  The socket facotry to use.
     */
    public static SocketFactory getDefault()
    {
        return new BasicSSLSocketFactory();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   host
     * @param   port
     *
     * @return
     *
     * @throws  IOException
     *
     * @see     javax.net.SocketFactory#createSocket(java.net.InetAddress, int)
     */
    @Override public Socket createSocket(InetAddress host, int port)
                                  throws IOException
    {
        return getSSLContext().getSocketFactory().createSocket(host, port);
    }

    /**
     * This method creates a socket to use.
     *
     * @param   sHost  The name of the host.
     * @param   iPort  The port of the host.
     *
     * @return  The SSL socket to use.
     *
     * @throws  IOException           DOCUMENTME
     * @throws  UnknownHostException  DOCUMENTME
     *
     * @see     javax.net.SocketFactory#createSocket(java.lang.String, int)
     */
    @Override public Socket createSocket(String sHost, int iPort)
                                  throws IOException, UnknownHostException
    {
        return getSSLContext().getSocketFactory().createSocket(sHost, iPort);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   address
     * @param   port
     * @param   localAddress
     * @param   localPort
     *
     * @return
     *
     * @throws  IOException
     *
     * @see     javax.net.SocketFactory#createSocket(java.net.InetAddress, int,
     *          java.net.InetAddress, int)
     */
    @Override public Socket createSocket(InetAddress address, int port, InetAddress localAddress,
                                         int localPort)
                                  throws IOException
    {
        return getSSLContext().getSocketFactory().createSocket(address, port, localAddress,
                                                               localPort);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   sHost
     * @param   iPort
     * @param   iaLocal
     * @param   iLocalPort
     *
     * @return
     *
     * @throws  IOException
     * @throws  UnknownHostException
     *
     * @see     javax.net.SocketFactory#createSocket(java.lang.String, int, java.net.InetAddress,
     *          int)
     */
    @Override public Socket createSocket(String sHost, int iPort, InetAddress iaLocal,
                                         int iLocalPort)
                                  throws IOException, UnknownHostException
    {
        return getSSLContext().getSocketFactory().createSocket(sHost, iPort, iaLocal, iLocalPort);
    }

    /**
     * This method creates the actual SSL context.
     *
     * @return  The SSL context.
     *
     * @throws  Exception  DOCUMENTME
     */
    private SSLContext createSSLContext()
                                 throws Exception
    {
        SSLContext sslcontext = SSLContext.getInstance("SSL");
        sslcontext.init(null, new TrustManager[] { new DummyTrustManager() }, null);
        return sslcontext;
    }

    /**
     * DOCUMENTME.
     *
     * @return  DOCUMENTME
     */
    private SSLContext getSSLContext()
    {
        if (this.m_scSSLContext == null)
        {
            try
            {
                this.m_scSSLContext = createSSLContext();
            }
            catch (Exception e)
            {
                LOG.error("Error creating context.", e);
            }
        }
        return this.m_scSSLContext;
    }

    /**
     * This trust manager accepts any certificate.
     *
     * @author  pgussow
     */
    private static class DummyTrustManager
        implements X509TrustManager
    {
        /**
         * DOCUMENT ME!
         *
         * @param   chain
         * @param   authType
         *
         * @throws  CertificateException
         *
         * @see     javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[],
         *          java.lang.String)
         */
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                                throws CertificateException
        {
            LOG.debug("checkClientTrusted");
        }

        /**
         * DOCUMENT ME!
         *
         * @param   chain
         * @param   authType
         *
         * @throws  CertificateException
         *
         * @see     javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[],
         *          java.lang.String)
         */
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                                throws CertificateException
        {
            LOG.debug("checkServerTrusted");
        }

        /**
         * DOCUMENT ME!
         *
         * @return
         *
         * @see     javax.net.ssl.X509TrustManager#getAcceptedIssuers()
         */
        public X509Certificate[] getAcceptedIssuers()
        {
            LOG.debug("getAcceptedIssuers");
            return null;
        }
    }
}
