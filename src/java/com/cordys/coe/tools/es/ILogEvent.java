package com.cordys.coe.tools.es;

import java.io.BufferedWriter;
import java.io.IOException;

import java.util.Date;

/**
 * This interface describes a log message and which fields it has.
 *
 * @author  pgussow
 */
public interface ILogEvent
{
    /**
     * This method returns the category.
     *
     * @return  The category.
     */
    String getCategory();

    /**
     * This method returns a string [] containing all the connectors.
     *
     * @return  A string [] containing all the connectors.
     */
    String[] getConnectors();

    /**
     * This method returns the host.
     *
     * @return  The host.
     */
    String getHost();

    /**
     * This method returns the actual message.
     *
     * @return  The actual message.
     */
    String getMessage();

    /**
     * This method returns the NDC of the log message.
     *
     * @return  The NDC.
     */
    String getNDC();

    /**
     * This method returns the process id.
     *
     * @return  The process id.
     */
    String getPID();

    /**
     * This method returns the thread.
     *
     * @return  The thread.
     */
    String getThread();

    /**
     * This method returns the date.
     *
     * @return  The date.
     */
    Date getTime();

    /**
     * This method returns the trace level.
     *
     * @return  The trace level.
     */
    String getTraceLevel();

    /**
     * This method should write the logevent details to a piece of XML.
     *
     * @param   bwOut  The writer to write to.
     *
     * @throws  IOException  If the writing fails.
     */
    void writeToWriter(BufferedWriter bwOut)
                throws IOException;

    /**
     * This method returns the formatted message. If this message contains a piece of XML it is
     * formatted accordingly.
     *
     * @return  The formatted message.
     */
    String getFormattedMessage();
}
