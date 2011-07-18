package com.cordys.coe.util.log;

/**
 * A simple log class to write log messages to standard output.
 *
 * @author  mpoyhone
 */
public class StdoutLogger
    implements LogInterface
{
    /**
     * Writes a debug log message.
     *
     * @param  sMsg  Message to be written.
     */
    public void debug(String sMsg)
    {
        System.out.println("DEBUG: " + sMsg);
    }

    /**
     * Writes an error log message.
     *
     * @param  sMsg  Message to be written.
     */
    public void error(String sMsg)
    {
        System.out.println("ERROR: " + sMsg);
    }

    /**
     * Writes an error log message with an exception.
     *
     * @param  sMsg        Message to be written.
     * @param  tException  The exception to be written.
     */
    public void error(String sMsg, Throwable tException)
    {
        System.out.println("ERROR: " + sMsg + " : " + tException);
    }

    /**
     * Writes an info log message.
     *
     * @param  sMsg  Message to be written.
     */
    public void info(String sMsg)
    {
        System.out.println("INFO : " + sMsg);
    }

    /**
     * @see  com.cordys.coe.util.log.LogInterface#isDebugEnabled()
     */
    public boolean isDebugEnabled()
    {
        return true;
    }

    /**
     * @see  com.cordys.coe.util.log.LogInterface#isInfoEnabled()
     */
    public boolean isInfoEnabled()
    {
        return true;
    }
}
