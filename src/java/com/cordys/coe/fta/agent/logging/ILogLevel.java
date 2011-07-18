package com.cordys.coe.fta.agent.logging;

/**
 * This interface defines all the different log levels that the FTA supports. The first 16 bits are
 * used for identifying the different categories. The last 16 bits can be used for control masks.
 *
 * @author  jverhaar
 * @author  pgussow
 */
public interface ILogLevel
{
    /**
     * Identifies the level 'OFF'. This means that no logging should take place.
     */
    int OFF = 0;
    /**
     * Identifies the level 'TRACE'.
     */
    int TRACE = (int) Math.pow(2, 1);
    /**
     * Identifies the level 'DEBUG'.
     */
    int DEBUG = (int) Math.pow(2, 2);
    /**
     * Identifies the level 'INFO'.
     */
    int INFO = (int) Math.pow(2, 3);
    /**
     * Identifies the level 'WARNING'.
     */
    int WARNING = (int) Math.pow(2, 4);
    /**
     * Identifies the level 'ERROR'.
     */
    int ERROR = (int) Math.pow(2, 5);
    /**
     * Identifies the level 'FATAL'.
     */
    int FATAL = (int) Math.pow(2, 6);
    /**
     * Identifies the log control 'TO_CENTRAL'. This means that the logmessage should be logged in
     * the central repository as well.
     */
    int TO_CENTRAL = (int) Math.pow(2, 18);
    /**
     * Identifies the level 'INFO' with the control flag TO_CENTRAL enabled.
     */
    int INFO_TO_CENTRAL = INFO | TO_CENTRAL;
    /**
     * Identifies the level 'WARNING' with the control flag TO_CENTRAL enabled.
     */
    int WARNING_TO_CENTRAL = WARNING | TO_CENTRAL;
    /**
     * Identifies the string that can identify the level 'OFF'.
     */
    String STR_OFF = "OFF";
    /**
     * Identifies the string that can identify the level 'TRACE'.
     */
    String STR_TRACE = "TRACE";
    /**
     * Identifies the string that can identify the level 'DEBUG'.
     */
    String STR_DEBUG = "DEBUG";
    /**
     * Identifies the string that can identify the level 'INFO'.
     */
    String STR_INFO = "INFO";
    /**
     * Identifies the string that can identify the level 'WARNING'.
     */
    String STR_WARNING = "WARNING";
    /**
     * Identifies the string that can identify the level 'ERROR'.
     */
    String STR_ERROR = "ERROR";
    /**
     * Identifies the string that can identify the level 'FATAL'.
     */
    String STR_FATAL = "FATAL";
}
