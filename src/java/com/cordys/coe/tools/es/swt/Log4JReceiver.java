package com.cordys.coe.tools.es.swt;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

/**
 * This class can receive log messages on the designated port.
 *
 * @author  pgussow
 */
public class Log4JReceiver extends Thread
{
    /**
     * Used to log messages.
     */
    private static final Logger LOG = Logger.getLogger(Log4JReceiver.class);
    /**
     * Indicates whether or not the thread should stop.
     */
    private boolean m_bStop = false;
    /**
     * Holds the default port.
     */
    private int m_iPort = 4445;
    /**
     * Holds the handler that will process the messages.
     */
    private IMessageHandler m_mhHandler = null;
    /**
     * Holds the name of the logger.
     */
    private String m_sLogName;
    /**
     * Holds the name for this receiver.
     */
    private String m_sName;
    /**
     * Holds the server socket.
     */
    private ServerSocket m_ssServerSocket;

    /**
     * Creates a new Log4JReceiver object.
     *
     * @param   mhHandler  The handler to send the received messages to.
     * @param   iPort      The port to listen on.
     * @param   sLogName   The name of the logger to use.
     *
     * @throws  IOException  DOCUMENTME
     */
    public Log4JReceiver(IMessageHandler mhHandler, int iPort, String sLogName)
                  throws IOException
    {
        this(mhHandler, iPort, "SA:" + iPort, sLogName);
    }

    /**
     * Constructor.
     *
     * @param   mhHandler  The handler to send the received messages to.
     * @param   iPort      The port to listen on.
     * @param   sName      DOCUMENTME
     * @param   sLogName   The name of the logger to use.
     *
     * @throws  IOException  DOCUMENTME
     */
    public Log4JReceiver(IMessageHandler mhHandler, int iPort, String sName, String sLogName)
                  throws IOException
    {
        setDaemon(true);
        setName("Log4JReceiver-" + sName);
        m_sName = sName;
        m_sLogName = sLogName;
        m_mhHandler = mhHandler;
        m_iPort = iPort;
        m_ssServerSocket = new ServerSocket(m_iPort);
    }

    /**
     * This method gets the log name.
     *
     * @return  The log name.
     */
    public String getLogName()
    {
        return m_sLogName;
    }

    /**
     * This method gets the current port.
     *
     * @return  The current port.
     */
    public int getPort()
    {
        return m_iPort;
    }

    /**
     * This method gets the name for this receiver.
     *
     * @return  The name for this receiver.
     */
    public String getReceiverName()
    {
        return m_sName;
    }

    /**
     * Listens for client connections.
     */
    @Override public void run()
    {
        LOG.info("Thread started");

        try
        {
            while (!shouldStop())
            {
                LOG.debug("Waiting for a connection");

                final Socket client = m_ssServerSocket.accept();
                LOG.debug("Got a connection from " + client.getInetAddress().getHostName());

                final Thread t = new Thread(new Slurper(client));
                t.setDaemon(true);
                t.start();
            }
        }
        catch (IOException e)
        {
            LOG.error("Error in accepting connections, stopping.", e);
        }
    }

    /**
     * This method sets the log name.
     *
     * @param  sLogName  The log name.
     */
    public void setLogName(String sLogName)
    {
        m_sLogName = sLogName;
    }

    /**
     * This method sets wether or not the thread should stop.
     *
     * @param  bStop  Whether or not the thread should stop.
     */
    public void setShouldStop(boolean bStop)
    {
        m_bStop = bStop;
    }

    /**
     * This method gets whether or not the thread should stop.
     *
     * @return  Whether or not the thread should stop.
     */
    public boolean shouldStop()
    {
        return m_bStop;
    }

    /**
     * Helper that actually processes a client connection. It receives events and adds them to the
     * supplied model.
     *
     * @author  <a href="mailto:oliver@puppycrawl.com">Oliver Burn</a>
     */
    private class Slurper
        implements Runnable
    {
        /**
         * Holds the remote host.
         */
        private String m_sRemoteHost;
        /**
         * socket connection to read events from.
         */
        private final Socket mClient;

        /**
         * Creates a new <code>Slurper</code> instance.
         *
         * @param  aClient  socket to receive events from
         */
        Slurper(Socket aClient)
        {
            mClient = aClient;
            m_sRemoteHost = mClient.getInetAddress().getHostName();
            setName("Slurper-" + m_sRemoteHost);
        }

        /**
         * loops getting the events.
         */
        public void run()
        {
            if (LOG.isDebugEnabled())
            {
                LOG.debug("Starting to get data");
            }

            try
            {
                final ObjectInputStream ois = new ObjectInputStream(mClient.getInputStream());

                while (true)
                {
                    final LoggingEvent event = (LoggingEvent) ois.readObject();
                    EventDetails edDetails = new EventDetails(event, m_sRemoteHost);
                    m_mhHandler.handleMessage(getLogName(), edDetails);
                }
            }
            catch (EOFException e)
            {
                LOG.info("Reached EOF, closing connection");
            }
            catch (SocketException e)
            {
                LOG.info("Caught SocketException, closing connection");
            }
            catch (IOException e)
            {
                LOG.warn("Got IOException, closing connection", e);
            }
            catch (ClassNotFoundException e)
            {
                LOG.warn("Got ClassNotFoundException, closing connection", e);
            }

            try
            {
                mClient.close();
            }
            catch (IOException e)
            {
                LOG.warn("Error closing connection", e);
            }
        }
    }
}
