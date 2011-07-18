package com.cordys.coe.util.cgc.serverwatcher;

import com.cordys.coe.util.cgc.ICordysGatewayClient;

import java.io.IOException;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import org.w3c.dom.Element;

/**
 * This class can be used to monitor a Cordys Webserver. It will periodically check whether or not
 * the Cordys server is still reachable and if the Cordys environment is still running. This class
 * exposes a couple of methods that can be used to check whether or not the server and webgateway
 * are available.
 *
 * @author  pgussow
 */
public class ServerWatcher extends Thread
{
    /**
     * Holds the logger to use.
     */
    private static final Logger LOG = Logger.getLogger(ServerWatcher.class);
    /**
     * Holds the default sleep time for this watcher.
     */
    private static final long DEFAULT_SLEEP_TIME = 5000;
    /**
     * Holds all services to watch.
     */
    private ArrayList<ServerWatcherSoapService> m_alServices = new ArrayList<ServerWatcherSoapService>();
    /**
     * Holds whether or not all required services are running.
     */
    private boolean m_bAllServicesRunning = false;
    /**
     * Holds whether or not the server itself is reachable.
     */
    private boolean m_bIsServerUp = false;
    /**
     * Holds whether or not the webserver is running.
     */
    private boolean m_bIsWebServerRunning = false;
    /**
     * Holds whether or not this thread should stop.
     */
    private boolean m_bShouldStop = false;
    /**
     * Holds the Cordys gateway client that can be used.
     */
    private ICordysGatewayClient m_cgcGateway = null;
    /**
     * Holds the portnumber of the server.
     */
    private int m_iPort;
    /**
     * Holds the time between check intervals.
     */
    private long m_lPollInterval = DEFAULT_SLEEP_TIME;
    /**
     * Holds the hostname of the server.
     */
    private String m_sServer;

    /**
     * Constructor. Creates the server watcher thread.
     *
     * @param  sServer  The hostname of the server.
     * @param  iPort    The port on which the webserver is running.
     */
    public ServerWatcher(String sServer, int iPort)
    {
        this(sServer, iPort, DEFAULT_SLEEP_TIME);
    }

    /**
     * Constructor. Creates the server watcher thread.
     *
     * @param  sServer        The hostname of the server.
     * @param  iPort          The port on which the webserver is running.
     * @param  lPollInterval  The intervall between polling cycles.
     */
    public ServerWatcher(String sServer, int iPort, long lPollInterval)
    {
        m_sServer = sServer;
        m_iPort = iPort;
        m_lPollInterval = lPollInterval;
        setName("ServerWatcher_" + m_sServer + ":" + m_iPort);
    }

    /**
     * Main method.
     *
     * @param  saArguments  Commandline arguments.
     */
    public static void main(String[] saArguments)
    {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.DEBUG);

        ServerWatcher sw = new ServerWatcher("cnd0986", 80);
        sw.start();

        try
        {
            sw.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * This method adds the passed on service to be watched each interval of this watcher thread.
     *
     * @param  swssService  the soap service to watch.
     */
    public void addSoapServiceToWatch(ServerWatcherSoapService swssService)
    {
        m_alServices.add(swssService);
    }

    /**
     * This method gets the cordys gateway client to use.
     *
     * @return  The cordys gateway client to use.
     */
    public ICordysGatewayClient getCordysGatewayClient()
    {
        return m_cgcGateway;
    }

    /**
     * This method gets whether or not the server itself is reachable.
     *
     * @return  Whether or not the server itself is reachable.
     */
    public boolean isServerUp()
    {
        return m_bIsServerUp;
    }

    /**
     * This method gets whether or not the webserver is running.
     *
     * @return  Whether or not the webserver is running.
     */
    public boolean isWebServerRunnning()
    {
        return m_bIsWebServerRunning;
    }

    /**
     * This method is called when this thread is started. Each polling cycle it will check 2 things.
     * The first one is whether or not the host itself is still reachable. The second thing if it
     * can still connect to the webserver.
     *
     * @see  java.lang.Runnable#run()
     */
    @Override public void run()
    {
        if (LOG.isDebugEnabled())
        {
            LOG.debug("Starting the server watcher for " + m_sServer + ":" + m_iPort +
                      " with poll interval " + m_lPollInterval);
        }

        boolean bInitial = true;

        while (!m_bShouldStop)
        {
            try
            {
                // Try to see if the host is still reachable.
                try
                {
                    InetAddress iaAddress = InetAddress.getByName(m_sServer);

                    if (!m_bIsServerUp && !bInitial)
                    {
                        LOG.warn("Server " + m_sServer + " just became known again. Real host: " +
                                 iaAddress.getHostName() + "(" + iaAddress.getHostAddress() + ")");
                    }
                    m_bIsServerUp = true;
                }
                catch (UnknownHostException e)
                {
                    // The host is unknown. we need to set the m_bIsServerUp to false.
                    if (m_bIsServerUp && !bInitial)
                    {
                        LOG.warn("Server " + m_sServer + " is unknown.", e);
                    }
                    m_bIsServerUp = false;
                }

                // Now we try to make a connection to the webserver.
                try
                {
                    Socket sSocket = new Socket(m_sServer, m_iPort);
                    sSocket.close();

                    if (!m_bIsWebServerRunning && !bInitial)
                    {
                        LOG.warn("The webserver on " + m_sServer + ":" + m_iPort +
                                 " just came back to life.");
                    }
                    m_bIsWebServerRunning = true;
                }
                catch (UnknownHostException e)
                {
                    // The host is unknown. we need to set the m_bIsServerUp to false.
                    if (m_bIsServerUp && !bInitial)
                    {
                        LOG.warn("Server " + m_sServer + " is unknown.", e);
                    }
                    m_bIsServerUp = false;
                }
                catch (IOException e)
                {
                    // For some reason the connection to the webserver could not be made.
                    if (m_bIsWebServerRunning && !bInitial)
                    {
                        LOG.warn("The webserver " + m_sServer + ":" + m_iPort + " just went down.");
                    }
                    m_bIsWebServerRunning = false;
                }

                // Now see if the mandatory soap processors are still running.
                if (LOG.isDebugEnabled())
                {
                    if ((m_alServices.size() > 0) && (m_cgcGateway == null))
                    {
                        LOG.debug("There are " + m_alServices.size() +
                                  " services to check, but there is no CGC to do that with.");
                    }
                }

                boolean bOk = true;

                if (m_cgcGateway != null)
                {
                    for (Iterator<ServerWatcherSoapService> iServices = m_alServices.iterator();
                             iServices.hasNext();)
                    {
                        ServerWatcherSoapService swss = iServices.next();

                        if (LOG.isDebugEnabled())
                        {
                            LOG.debug("Going to see if service '" + swss.getName() +
                                      "' is still running using <" + swss.getMethod() +
                                      " xmlns=\"" + swss.getNamespace() + "\"/> request.");
                        }

                        try
                        {
                            Element eMessage = m_cgcGateway.createMessage(swss.getMethod(),
                                                                          swss.getNamespace());
                            m_cgcGateway.requestFromCordysNoBlocking(eMessage.getOwnerDocument()
                                                                     .getDocumentElement());
                            swss.setIsServiceRunning(true);
                        }
                        catch (Exception e)
                        {
                            if (swss.isServiceRunning() && !bInitial)
                            {
                                LOG.warn("The service " + swss.getName() + " just went down.");
                            }

                            if (LOG.isDebugEnabled())
                            {
                                LOG.debug("Error checking if service " + swss.getName() +
                                          " is up. Assuming it's down.", e);
                            }
                            swss.setIsServiceRunning(false);
                            bOk = false;
                            break;
                        }
                    }
                }

                if ((m_bAllServicesRunning == false) && (bOk == true) && !bInitial)
                {
                    LOG.warn("All required services seem to back up again.");
                }
                m_bAllServicesRunning = bOk;

                // Go into sleep mode.
                if (LOG.isDebugEnabled())
                {
                    LOG.debug("Sleeping for " + m_lPollInterval + " miliseconds.");
                }

                if (bInitial == true)
                {
                    bInitial = false;

                    if (m_bIsServerUp == false)
                    {
                        LOG.warn("The server is currently not up.");
                    }
                    else
                    {
                        if (m_bIsWebServerRunning == false)
                        {
                            LOG.warn("The webserver is currently not running.");
                        }
                    }
                }

                Thread.sleep(m_lPollInterval);
            }
            catch (InterruptedException e)
            {
                // Ignore the exception.
            }
        }

        if (LOG.isDebugEnabled())
        {
            LOG.debug("Exiting the server watcher.");
        }
    }

    /**
     * This method sets the cordys gateway client to use.
     *
     * @param  cgcGateway  The cordys gateway client to use.
     */
    public void setCordysGatewayClient(ICordysGatewayClient cgcGateway)
    {
        m_cgcGateway = cgcGateway;
    }

    /**
     * This method sets the poll interval for this server watcher.
     *
     * @param  lPollInterval  The new poll interval.
     */
    public void setPollInterval(long lPollInterval)
    {
        m_lPollInterval = lPollInterval;
    }

    /**
     * This method returns true if both the server is up and the webserver is running.
     *
     * @return  true if both the server is up and the webserver is running.
     */
    public boolean shouldServerFunction()
    {
        return m_bIsServerUp && m_bIsWebServerRunning && m_bAllServicesRunning;
    }

    /**
     * This method tells this thread that it should stop.
     */
    public void stopWatcher()
    {
        m_bShouldStop = true;
    }
}
