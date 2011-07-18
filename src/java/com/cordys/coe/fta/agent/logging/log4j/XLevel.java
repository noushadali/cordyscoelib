package com.cordys.coe.fta.agent.logging.log4j;

import com.cordys.coe.fta.agent.logging.ILogLevel;
import com.cordys.coe.util.Bits;

import java.util.HashMap;

import org.apache.log4j.Level;

/**
 * This class adds an additional level called TRACE. In priority it is lower then DEBUG.
 *
 * @author  pgussow
 */
public class XLevel extends Level
{
    /**
     * Holds all the created XLevel objects for the different bitmasks.
     */
    private static HashMap<Integer, XLevel> hmLevels = null;
    /**
     * The integer for the level TRACE.
     */
    public static final int TRACE_INT = Level.DEBUG_INT - 1;
    /**
     * The string identifying the level TRACE.
     */
    private static String TRACE_STR = "TRACE";
    /**
     * Identifies the level TRACE.
     */
    public static final XLevel TRACE = new XLevel(TRACE_INT, TRACE_STR, 7, ILogLevel.TRACE);
    /**
     * Holds whether or not this level logs to the central server.
     */
    private int m_iFTALogLevel = 0;

    /**
     * Constructor.
     *
     * @param  lSource       The original level.
     * @param  iFTALogLevel  The FTA log level.
     */
    protected XLevel(Level lSource, int iFTALogLevel)
    {
        this(lSource.toInt(), lSource.toString(), lSource.getSyslogEquivalent(), iFTALogLevel);
    }

    /**
     * Constructor.
     *
     * @param  iLevelInt     The integer for this level.
     * @param  sLevelString  The String identifying this level.
     * @param  iSysLogEquiv  The system log equivelent.
     * @param  iFTALogLevel  The FTA log level.
     */
    protected XLevel(int iLevelInt, String sLevelString, int iSysLogEquiv, int iFTALogLevel)
    {
        super(iLevelInt, sLevelString, iSysLogEquiv);
        m_iFTALogLevel = iFTALogLevel;
    }

    /**
     * This method returns the proper XLevel object fot the given FTA bitmask level.
     *
     * @param   iLogLevel  The FTA loglevel.
     *
     * @return  The Log4J XLevel object.
     */
    public static XLevel getXLevelForFTALevel(int iLogLevel)
    {
        XLevel xlReturn = null;

        if (hmLevels == null)
        {
            hmLevels = new HashMap<Integer, XLevel>();
        }

        if (!hmLevels.containsKey(new Integer(iLogLevel)))
        {
            xlReturn = parseFTALevel(iLogLevel);
            hmLevels.put(new Integer(iLogLevel), xlReturn);
        }
        else
        {
            xlReturn = hmLevels.get(new Integer(iLogLevel));
        }

        return xlReturn;
    }

    /**
     * This method parses the string that identifies a level.
     *
     * @param   sLevel  The level string.
     *
     * @return  The log int identifying the current level.
     */
    public static XLevel parseLevelString(String sLevel)
    {
        XLevel xlReturn = null;

        if ((sLevel != null) && (sLevel.length() > 0))
        {
            if (ILogLevel.STR_TRACE.equals(sLevel.toUpperCase()))
            {
                xlReturn = getXLevelForFTALevel(ILogLevel.TRACE);
            }
            else if (ILogLevel.STR_DEBUG.equals(sLevel.toUpperCase()))
            {
                xlReturn = getXLevelForFTALevel(ILogLevel.DEBUG);
            }
            else if (ILogLevel.STR_INFO.equals(sLevel.toUpperCase()))
            {
                xlReturn = getXLevelForFTALevel(ILogLevel.INFO);
            }
            else if (ILogLevel.STR_WARNING.equals(sLevel.toUpperCase()) ||
                         XLevel.WARN.toString().equals(sLevel.toUpperCase()))
            {
                xlReturn = getXLevelForFTALevel(ILogLevel.WARNING);
            }
            else if (ILogLevel.STR_ERROR.equals(sLevel.toUpperCase()))
            {
                xlReturn = getXLevelForFTALevel(ILogLevel.ERROR);
            }
            else if (ILogLevel.STR_FATAL.equals(sLevel.toUpperCase()))
            {
                xlReturn = getXLevelForFTALevel(ILogLevel.FATAL);
            }
            else if (ILogLevel.STR_OFF.equals(sLevel.toUpperCase()))
            {
                xlReturn = getXLevelForFTALevel(ILogLevel.OFF);
            }
            else
            {
                // Default to off.
                xlReturn = getXLevelForFTALevel(ILogLevel.OFF);
            }
        }

        return xlReturn;
    }

    /**
     * Convert the string passed as argument to a level. If the conversion fails, then this method
     * returns {@link #TRACE}.
     *
     * @param   sLevelName  The string to get the level of.
     *
     * @return  The proper level.
     */
    public static Level toLevel(String sLevelName)
    {
        return toLevel(sLevelName, XLevel.TRACE);
    }

    /**
     * Returns the level for the integer.
     *
     * @param   iLevelInt  The integer defining the level.
     *
     * @return  The proper level.
     *
     * @throws  IllegalArgumentException  DOCUMENTME
     */
    public static Level toLevel(int iLevelInt)
                         throws IllegalArgumentException
    {
        switch (iLevelInt)
        {
            case TRACE_INT:
                return XLevel.TRACE;
        }
        return Level.toLevel(iLevelInt);
    }

    /**
     * Convert the string passed as argument to a level. If the conversion fails, then this method
     * returns {@link #TRACE}.
     *
     * @param   sLevelName     The string to get the level of.
     * @param   lDefaultValue  The default level if it can't be found.
     *
     * @return  The proper level.
     */
    public static Level toLevel(String sLevelName, Level lDefaultValue)
    {
        if (sLevelName == null)
        {
            return lDefaultValue;
        }

        String stringVal = sLevelName.toUpperCase();

        if (stringVal.equals(TRACE_STR))
        {
            return XLevel.TRACE;
        }

        return Level.toLevel(sLevelName, lDefaultValue);
    }

    /**
     * This method checks if both XLevels are equal to eachother.
     *
     * @param   oOther  The object to compare with.
     *
     * @return  DOCUMENTME
     *
     * @see     org.apache.log4j.Priority#equals(java.lang.Object)
     */
    @Override public boolean equals(Object oOther)
    {
        boolean bReturn = false;

        if (oOther instanceof XLevel)
        {
            XLevel xlOther = (XLevel) oOther;
            bReturn = (xlOther.getFTALogLevel() == getFTALogLevel());
        }
        else
        {
            bReturn = super.equals(oOther);
        }
        return bReturn;
    }

    /**
     * This method gets the FTA loglevel for this level.
     *
     * @return  The FTA loglevel for this level.
     */
    public int getFTALogLevel()
    {
        return m_iFTALogLevel;
    }

    /**
     * This method returns whether or not this level should be logged to the central server.
     *
     * @return  Whether or not this level should be logged to the central server.
     */
    public boolean isToCentral()
    {
        return Bits.isBitSet(m_iFTALogLevel, ILogLevel.TO_CENTRAL);
    }

    /**
     * This method sets the FTA log level for this level.
     *
     * @param  iFTALogLevel  The FTA log level.
     */
    public void setFTALogLevel(int iFTALogLevel)
    {
        m_iFTALogLevel = iFTALogLevel;
    }

    /**
     * This method returns a new instance of XLevel that corresponds to the desired FTA level.
     *
     * @param   iLogLevel  The FTA log level.
     *
     * @return  An XLevel object matching the FTA loglevel.
     */
    private static XLevel parseFTALevel(int iLogLevel)
    {
        XLevel xlReturn = null;

        if (Bits.isBitSet(iLogLevel, ILogLevel.TRACE))
        {
            xlReturn = new XLevel(XLevel.TRACE, iLogLevel);
        }
        else if (Bits.isBitSet(iLogLevel, ILogLevel.DEBUG))
        {
            xlReturn = new XLevel(XLevel.DEBUG, iLogLevel);
        }
        else if (Bits.isBitSet(iLogLevel, ILogLevel.INFO))
        {
            xlReturn = new XLevel(XLevel.INFO, iLogLevel);
        }
        else if (Bits.isBitSet(iLogLevel, ILogLevel.WARNING))
        {
            xlReturn = new XLevel(XLevel.WARN, iLogLevel);
        }
        else if (Bits.isBitSet(iLogLevel, ILogLevel.ERROR))
        {
            xlReturn = new XLevel(XLevel.ERROR, iLogLevel);
        }
        else if (Bits.isBitSet(iLogLevel, ILogLevel.FATAL))
        {
            xlReturn = new XLevel(XLevel.FATAL, iLogLevel);
        }
        else if (Bits.isBitSet(iLogLevel, ILogLevel.OFF))
        {
            xlReturn = new XLevel(XLevel.OFF, iLogLevel);
        }

        // Add a default level of OFF
        if (xlReturn == null)
        {
            xlReturn = new XLevel(XLevel.OFF, iLogLevel);
        }

        return xlReturn;
    }
}
