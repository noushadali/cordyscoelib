package com.cordys.coe.util;

import com.eibus.connector.nom.Connector;

import com.eibus.directory.soap.DirectoryException;

import com.eibus.exception.ExceptionGroup;

/**
 * This class provides a CoeLib Connector Instance.
 *
 * @author   $author$
 * @version  $Revision$
 */
public class CoeLibConnector
{
    /**
     * DOCUMENTME.
     */
    private static final String CONNECTOR_NAME = "CoeLib Connector";
    /**
     * DOCUMENTME.
     */
    private static Object s_oMutex = new Object();
    /**
     * DOCUMENTME.
     */
    private static Connector s_cConnector;

    /**
     * Gets a CoeLib connector instance.
     *
     * @return  Connector
     *
     * @throws  ExceptionGroup      DOCUMENTME
     * @throws  DirectoryException  DOCUMENTME
     */
    public static Connector getInstance()
                                 throws ExceptionGroup, DirectoryException
    {
        Connector cReturn = s_cConnector;

        if (cReturn == null)
        {
            synchronized (s_oMutex)
            {
                if (cReturn == null)
                {
                    s_cConnector = Connector.getInstance(CONNECTOR_NAME);

                    if (!s_cConnector.isOpen())
                    {
                        s_cConnector.open();
                    }

                    cReturn = s_cConnector;
                }
            }
        }

        return cReturn;
    }
}
