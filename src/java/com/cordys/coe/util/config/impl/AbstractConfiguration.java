package com.cordys.coe.util.config.impl;

import com.cordys.coe.util.config.IConfiguration;
import com.cordys.coe.util.xml.dom.XMLProperties;

import org.w3c.dom.Element;

/**
 * This is the abstract base class from which the configurations extend.
 *
 * @author  $author$
 */
public abstract class AbstractConfiguration extends XMLProperties
    implements IConfiguration
{
    /**
     * Holds the root tag.
     */
    private static final String TAG_ROOT = "configuration";
    /**
     * Holds the name of the configuration.
     */
    private String m_sName;

    /**
     * Default constructor.
     */
    public AbstractConfiguration()
    {
        super(TAG_ROOT);
    }

    /**
     * Creates a new WebGatewayConfiguration object.
     *
     * @param  eConfigNode  The configuration XML.
     */
    public AbstractConfiguration(Element eConfigNode)
    {
        super(TAG_ROOT);
        m_sName = eConfigNode.getAttribute("name");

        initializeData(eConfigNode);
    }

    /**
     * This method returns the name for the configuration.
     *
     * @return  The name for the configuration.
     *
     * @see     com.cordys.coe.util.config.IConfiguration#getName()
     */
    public String getName()
    {
        return m_sName;
    }

    /**
     * This method gets the timeout to use.
     *
     * @return  The timeout to use.
     */
    public long getTimeout()
    {
        return getLongValue(TAG_TIMEOUT, DEFAULT_TIMEOUT);
    }

    /**
     * This method sets the name for this configuration.
     *
     * @param  sName  The new name.
     *
     * @see    com.cordys.coe.util.config.INativeConfiguration#setName(java.lang.String)
     */
    public void setName(String sName)
    {
        m_sName = sName;
    }

    /**
     * This method sets the timeout to use.
     *
     * @param  lTimeout  The timeout to use.
     */
    public void setTimeout(long lTimeout)
    {
        setValue(TAG_TIMEOUT, new Long(lTimeout));
    }
}
