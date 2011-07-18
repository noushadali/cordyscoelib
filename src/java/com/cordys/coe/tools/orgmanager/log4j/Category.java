package com.cordys.coe.tools.orgmanager.log4j;

import com.cordys.coe.util.xml.dom.XPathHelper;

import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Element;

/**
 * This class wraps the category.
 *
 * @author  pgussow
 */
public class Category extends BaseWithAppenderRef
{
    /**
     * Holds all log levels.
     */
    private static List<String> LOG_LEVELS = Arrays.asList("trace", "debug", "info", "warn",
                                                           "error", "fatal", "off");
    /**
     * Holds the current log level.
     */
    private int m_iLogLevel = LOG_LEVELS.indexOf("off");
    /**
     * Holds the priority for this appender.
     */
    private Priority m_pPriority;
    /**
     * Holds the additivity for the category.
     */
    private String m_sAdditivity;
    /**
     * Holds the name for this category.
     */
    private String m_sName;

    /**
     * Creates a new Category object.
     */
    public Category()
    {
        super("category");
    }

    /**
     * Creates a new Category object.
     *
     * @param  eParent  The parent to parse.
     */
    public Category(Element eParent)
    {
        super(eParent, "category");

        m_sAdditivity = eParent.getAttribute("additivity");
        m_sName = eParent.getAttribute("name");

        try
        {
            Element ePriority = (Element) XPathHelper.selectSingleNode(eParent, "./priority");

            if (ePriority != null)
            {
                m_pPriority = new Priority(ePriority);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error parsing priority", e);
        }

        // Initialize the log level
        if (m_pPriority != null)
        {
            String sLevel = m_pPriority.getValue().toLowerCase();
            m_iLogLevel = LOG_LEVELS.indexOf(sLevel);
        }
    }

    /**
     * This method gets the additivity for the category.
     *
     * @return  The additivity for the category.
     */
    public String getAdditivity()
    {
        return m_sAdditivity;
    }

    /**
     * This method gets the name for this category.
     *
     * @return  The name for this category.
     */
    public String getName()
    {
        return m_sName;
    }

    /**
     * This method gets the priority for this appender.
     *
     * @return  The priority for this appender.
     */
    public Priority getPriority()
    {
        return m_pPriority;
    }

    /**
     * This method returns whether or not the debug level is enabled.
     *
     * @return  Whether or not the debug level is enabled.
     */
    public boolean isDebugEnabled()
    {
        return isLogLevelEnabled(LOG_LEVELS.indexOf("debug"));
    }

    /**
     * This method returns whether or not the error level is enabled.
     *
     * @return  Whether or not the error level is enabled.
     */
    public boolean isErrorEnabled()
    {
        return isLogLevelEnabled(LOG_LEVELS.indexOf("error"));
    }

    /**
     * This method returns whether or not the fatal level is enabled.
     *
     * @return  Whether or not the fatal level is enabled.
     */
    public boolean isFatalEnabled()
    {
        return isLogLevelEnabled(LOG_LEVELS.indexOf("fatal"));
    }

    /**
     * This method returns whether or not the info level is enabled.
     *
     * @return  Whether or not the info level is enabled.
     */
    public boolean isInfoEnabled()
    {
        return isLogLevelEnabled(LOG_LEVELS.indexOf("info"));
    }

    /**
     * This method returns whether or not the trace level is enabled.
     *
     * @return  Whether or not the trace level is enabled.
     */
    public boolean isTraceEnabled()
    {
        return isLogLevelEnabled(LOG_LEVELS.indexOf("trace"));
    }

    /**
     * This method returns whether or not logging has been turned off for this category.
     *
     * @return  Whether or not logging has been turned off for this category.
     */
    public boolean isTurnedOff()
    {
        return (m_iLogLevel == (LOG_LEVELS.size() - 1));
    }

    /**
     * This method returns whether or not the warn level is enabled.
     *
     * @return  Whether or not the warn level is enabled.
     */
    public boolean isWarnEnabled()
    {
        return isLogLevelEnabled(LOG_LEVELS.indexOf("warn"));
    }

    /**
     * This method sets the additivity for the category.
     *
     * @param  sAdditivity  The additivity for the category.
     */
    public void setAdditivity(String sAdditivity)
    {
        m_sAdditivity = sAdditivity;
    }

    /**
     * This method sets the level to the given level.
     *
     * @param  sLevel  The new level.
     */
    public void setLogLevel(String sLevel)
    {
        sLevel = sLevel.toLowerCase();

        m_iLogLevel = LOG_LEVELS.indexOf(sLevel);

        if (m_pPriority == null)
        {
            m_pPriority = new Priority();
        }

        m_pPriority.setValue(sLevel);
    }

    /**
     * This method sets the name for this category.
     *
     * @param  sName  The name for this category.
     */
    public void setName(String sName)
    {
        m_sName = sName;
    }

    /**
     * This method sets the priority for this appender.
     *
     * @param  pPriority  The priority for this appender.
     */
    public void setPriority(Priority pPriority)
    {
        m_pPriority = pPriority;
    }

    /**
     * This method returns the string representation of the object.
     *
     * @return  The string representation of the object.
     *
     * @see     java.lang.Object#toString()
     */
    @Override public String toString()
    {
        StringBuilder sbReturn = new StringBuilder();

        sbReturn.append(getName());

        if (getPriority() != null)
        {
            sbReturn.append(": ").append(getPriority().getValue());
        }

        return sbReturn.toString();
    }

    /**
     * @see  com.cordys.coe.tools.orgmanager.log4j.BaseWithParam#onAfterAttributeCreation(org.w3c.dom.Element)
     */
    @Override protected void onAfterAttributeCreation(Element eElement)
    {
        if ((getAdditivity() != null) && (getAdditivity().length() > 0))
        {
            eElement.setAttribute("additivity", getAdditivity());
        }

        if ((getName() != null) && (getName().length() > 0))
        {
            eElement.setAttribute("name", getName());
        }

        if (getPriority() != null)
        {
            getPriority().toXML(eElement);
        }
    }

    /**
     * This method returns whether or not a certain log level is enabled.
     *
     * @param   iLogLevel  The log level to check.
     *
     * @return  true if the level is enabled. Otherwise false.
     */
    private boolean isLogLevelEnabled(int iLogLevel)
    {
        boolean bReturn = false;

        if (m_iLogLevel <= iLogLevel)
        {
            bReturn = true;
        }

        return bReturn;
    }
}
