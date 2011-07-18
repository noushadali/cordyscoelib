package com.cordys.coe.util.config;

import org.w3c.dom.Element;

/**
 * Interface around configuration details.
 *
 * @author  pgussow
 */
public interface IConfiguration
{
    /**
     * Holds the default timeout used.
     */
    long DEFAULT_TIMEOUT = 30000L;

    /**
     * Holds the tag for the timeout.
     */
    String TAG_TIMEOUT = "timeout";
    /**
     * Identifies native configurations.
     */
    int TYPE_NATIVE = 1;
    /**
     * Identifies connections via the web gateway.
     */
    int TYPE_WEBGATEWAY = 0;

    /**
     * This method returns the name for the configuration.
     *
     * @return  The name for the configuration.
     */
    String getName();

    /**
     * This method gets the port number to use.
     *
     * @return  The port number to use.
     */
    int getPort();

    /**
     * This method gets the server name.
     *
     * @return  The server name.
     */
    String getServername();

    /**
     * This method returns the timeout in milliseconds to use.
     *
     * @return  The timeout in milliseconds to use.
     */
    long getTimeout();

    /**
     * This method gets the type of configuration (WebGateway or native).
     *
     * @return  The type of configuration (WebGateway or native).
     */
    int getType();

    /**
     * This method writes the details for the configuration to XML.
     *
     * @param   eParent  The parent XML node.
     *
     * @return  The XML structure
     */
    Element toXMLStructure(Element eParent);
}
