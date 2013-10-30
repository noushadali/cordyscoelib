package com.cordys.coe.tools.log4j;

import com.cordys.coe.tools.es.ILogEvent;

/**
 * Interface for the Log4J composite.
 *
 * @author  pgussow
 */
public interface ILog4JComposite
{
    /**
     * Holds the type for Cordys logfiles.
     */
    int TYPE_CORDYS_SPY = 0;
    /**
     * Holds the type for Log4J XML files.
     */
    int TYPE_LOG4J = 1;
    /**
     * Holds the type for Cordys Log4J XML files.
     */
    int TYPE_CORDYS_LOG4J = 2;

    /**
     * This method closes the connections.
     */
    void closeConnections();

    /**
     * This method gets the configuration for the Event Service Client.
     *
     * @return  The configuration for the Event Service Client.
     */
    ILogViewerConfiguration getConfiguration();

    /**
     * This method returns the title for this panel.
     *
     * @return  The title.
     */
    String getTitle();

    /**
     * This method loads the LogEvents from a file.
     *
     * @param  iFileType
     */
    void loadFromFile(int iFileType);

    /**
     * This method saves the logging of the log4J to the designated file.
     */
    void saveLog4JLog();

    /**
     * This method sets the text of the details.
     *
     * @param  leEvent  The logevent to display.
     */
    void setDetailedText(final ILogEvent leEvent);

    /**
     * This method starts the socket reader to read the Log4J messages sent via the socketappender.
     *
     * @return  The portnumber used.
     */
    String startLog4JListening();

    /**
     * This method will clear the currently selected log panel.
     */
    void clearCurrentView();
}
