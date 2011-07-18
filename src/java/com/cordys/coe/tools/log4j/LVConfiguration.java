package com.cordys.coe.tools.log4j;

import java.io.IOException;
import java.io.InputStream;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Properties;

/**
 * This class is used for the configuration.
 *
 * @author  pgussow
 */
public class LVConfiguration
    implements ILogViewerConfiguration
{
    /**
     * Holds the default date format.
     */
    private static final String DEFAULT_DATE_FORMAT = "HH:mm:ss.SSS";
    /**
     * Identifies the key in the properties file for the date format.
     */
    private static final String PROP_DATE_FORMAT = "date.format";
    /**
     * Holds the default page size.
     */
    private static final int DEFAULT_PAGE_SIZE = 1000;
    /**
     * Holds the key in the properties file for the page size.
     */
    private static final String PROP_PAGE_SIZE = "page.size";
    /**
     * Holds the validated formats.
     */
    private ArrayList<String> m_alValidated = new ArrayList<String>();
    /**
     * Holds the properties.
     */
    private Properties m_pProps;

    /**
     * Creates a new ESCConfiguration object.
     *
     * @param   isFile  The inputstream containing the properties.
     *
     * @throws  IOException  DOCUMENTME
     */
    public LVConfiguration(InputStream isFile)
                    throws IOException
    {
        m_pProps = new Properties();
        m_pProps.load(isFile);
    }

    /**
     * This method gets the dataformat that should be used for the date.
     *
     * @return  The dataformat that should be used for the date.
     */
    public String getDateFormat()
    {
        String sReturn = DEFAULT_DATE_FORMAT;

        String sTemp = m_pProps.getProperty(PROP_DATE_FORMAT);

        if ((sTemp != null) && (sTemp.length() > 0))
        {
            if (m_alValidated.contains(sTemp))
            {
                sReturn = sTemp;
            }
            else
            {
                try
                {
                    new SimpleDateFormat(sTemp);
                    sReturn = sTemp;
                    m_alValidated.add(sTemp);
                }
                catch (IllegalArgumentException iae)
                {
                    // The pattern is invalid.
                }
            }
        }

        return sReturn;
    }

    /**
     * This method returns the page size that should be used.
     *
     * @return  The page size that should be used.
     */
    public int getPageSize()
    {
        int iReturn = DEFAULT_PAGE_SIZE;

        try
        {
            iReturn = Integer.parseInt(m_pProps.getProperty(PROP_PAGE_SIZE));
        }
        catch (Exception e)
        {
            // Ignore it.
        }

        return iReturn;
    }
}
