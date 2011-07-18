package com.cordys.coe.util.log;

/**
 * A simple log interface to allow logging in different levels.
 *
 * @author  mpoyhone
 */
public interface LogInterface
{
    /**
     * Writes a debug log message.
     *
     * @param  sMsg  Message to be written.
     */
    void debug(String sMsg);

    /**
     * Writes an error log message.
     *
     * @param  sMsg  Message to be written.
     */
    void error(String sMsg);

    /**
     * Writes an error log message with an exception.
     *
     * @param  sMsg        Message to be written.
     * @param  tException  The exception to be written.
     */
    void error(String sMsg, Throwable tException);

    /**
     * Writes an info log message.
     *
     * @param  sMsg  Message to be written.
     */
    void info(String sMsg);

    /**
     * Indicates whether the debug level is enabled for this logger.
     *
     * @return  <code>true</code> if the debug level is enabled.
     */
    boolean isDebugEnabled();

    /**
     * Indicates whether the info level is enabled for this logger.
     *
     * @return  <code>true</code> if the info level is enabled.
     */
    boolean isInfoEnabled();
}
