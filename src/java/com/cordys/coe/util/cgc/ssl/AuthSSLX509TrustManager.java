/*
 * $Header:
 * /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//httpclient/src/contrib/org/apache/commons/httpclient/contrib/ssl/AuthSSLX509TrustManager.java,v
 * 1.2 2004/06/10 18:25:24 olegk Exp $ $Revision: 155418 $ $Date: 2005-02-26 08:01:52 -0500 (Sat, 26
 * Feb 2005) $
 *
 * ====================================================================
 *
 * Copyright 2002-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License. ====================================================================
 *
 * This software consists of voluntary contributions made by many individuals on behalf of the Apache
 * Software Foundation.  For more information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package com.cordys.coe.util.cgc.ssl;

import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>AuthSSLX509TrustManager can be used to extend the default {@link X509TrustManager} with
 * additional trust decisions.</p>
 *
 * @author  <a href="mailto:oleg@ural.ru">Oleg Kalnichevski</a>
 */
public class AuthSSLX509TrustManager
    implements X509TrustManager
{
    /**
     * Log object for this class.
     */
    private static final Log LOG = LogFactory.getLog(AuthSSLX509TrustManager.class);
    /**
     * The default trust manager.
     */
    private X509TrustManager m_xtmDefault = null;

    /**
     * Constructor for AuthSSLX509TrustManager. This class will accept any certificate.
     */
    public AuthSSLX509TrustManager()
    {
        super();
    }

    /**
     * Constructor for AuthSSLX509TrustManager.
     *
     * @param  xtmDefaultTrustManager  The parent trust manager
     */
    public AuthSSLX509TrustManager(final X509TrustManager xtmDefaultTrustManager)
    {
        super();

        if (xtmDefaultTrustManager == null)
        {
            throw new IllegalArgumentException("Trust manager may not be null");
        }

        m_xtmDefault = xtmDefaultTrustManager;
    }

    /**
     * This method checks if the certificate can be trusted. If you do not want to accept the
     * certificate you need to throw an exception.
     *
     * @param   certificates  The certificates to check.
     * @param   sAuthType     The authentication type.
     *
     * @throws  CertificateException  In case the certificate should not be accepted.
     */
    public void checkClientTrusted(X509Certificate[] certificates, String sAuthType)
                            throws CertificateException
    {
        if (m_xtmDefault != null)
        {
            if (certificates != null)
            {
                for (int c = 0; c < certificates.length; c++)
                {
                    X509Certificate cert = certificates[c];

                    if (LOG.isInfoEnabled())
                    {
                        LOG.info(" Client certificate " + (c + 1) + ":");
                        LOG.info("  Subject DN: " + cert.getSubjectDN());
                        LOG.info("  Signature Algorithm: " + cert.getSigAlgName());
                        LOG.info("  Valid from: " + cert.getNotBefore());
                        LOG.info("  Valid until: " + cert.getNotAfter());
                        LOG.info("  Issuer: " + cert.getIssuerDN());
                    }

                    try
                    {
                        cert.checkValidity();
                    }
                    catch (CertificateExpiredException e)
                    {
                        LOG.fatal("Client certificate " + cert.getSubjectDN() + " is expired.");
                    }
                    catch (CertificateNotYetValidException e)
                    {
                        LOG.fatal("Client certificate " + cert.getSubjectDN() +
                                  " is not yet valid.");
                    }
                }
            }

            // Call the super to do the actual checking.
            m_xtmDefault.checkClientTrusted(certificates, sAuthType);
        }
    }

    /**
     * This method checks if the server certificate is trusted.
     *
     * @param   certificates  The list of certificates.
     * @param   sAuthType     The authentication type.
     *
     * @throws  CertificateException  DOCUMENTME
     */
    public void checkServerTrusted(X509Certificate[] certificates, String sAuthType)
                            throws CertificateException
    {
        if (m_xtmDefault != null)
        {
            if (certificates != null)
            {
                for (int c = 0; c < certificates.length; c++)
                {
                    X509Certificate cert = certificates[c];

                    if (LOG.isInfoEnabled())
                    {
                        LOG.info(" Server certificate " + (c + 1) + ":");
                        LOG.info("  Subject DN: " + cert.getSubjectDN());
                        LOG.info("  Signature Algorithm: " + cert.getSigAlgName());
                        LOG.info("  Valid from: " + cert.getNotBefore());
                        LOG.info("  Valid until: " + cert.getNotAfter());
                        LOG.info("  Issuer: " + cert.getIssuerDN());
                    }

                    try
                    {
                        cert.checkValidity();
                    }
                    catch (CertificateExpiredException e)
                    {
                        LOG.fatal("Server certificate " + cert.getSubjectDN() + " is expired.");
                    }
                    catch (CertificateNotYetValidException e)
                    {
                        LOG.fatal("Server certificate " + cert.getSubjectDN() +
                                  " is not yet valid.");
                    }
                }
            }

            // Call the super to do the actual checking.
            m_xtmDefault.checkServerTrusted(certificates, sAuthType);
        }
    }

    /**
     * This method returns the list of accepted issuer certificates.
     *
     * @return  The list of accepted issuer certificates.
     */
    public X509Certificate[] getAcceptedIssuers()
    {
        if (m_xtmDefault != null)
        {
            return this.m_xtmDefault.getAcceptedIssuers();
        }

        return null;
    }
}
