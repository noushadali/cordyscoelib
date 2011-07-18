package com.cordys.coe.util.cgc.serverwatcher;

/**
 * This class is used to describe the service that should be monitored. If the service is down.
 *
 * @author  pgussow
 */
public class ServerWatcherSoapService
{
    /**
     * Holds whether or not this service is running or not.
     */
    private boolean m_bIsRunning = true;
    /**
     * Holds the name of the SOAP method to execute.
     */
    private String m_sMethod;
    /**
     * Holds the name of the service that is being monitored.
     */
    private String m_sName;
    /**
     * Holds the namespace of the method that has to be executed.
     */
    private String m_sNamespace;

    /**
     * Creates a new ServerWatcherSoapService object.
     *
     * @param  sName       The name of the service that is being monitored.
     * @param  sMethod     The name of the SOAP method to execute.
     * @param  sNamespace  The namespace of the method that has to be executed.
     */
    public ServerWatcherSoapService(String sName, String sMethod, String sNamespace)
    {
        m_sName = sName;
        m_sMethod = sMethod;
        m_sNamespace = sNamespace;
    }

    /**
     * This method gets the name of the SOAP method to execute.
     *
     * @return  The name of the SOAP method to execute.
     */
    public String getMethod()
    {
        return m_sMethod;
    }

    /**
     * This method gets the name of the service that is being monitored..
     *
     * @return  The name of the service that is being monitored..
     */
    public String getName()
    {
        return m_sName;
    }

    /**
     * This method gets the namespace of the method that has to be executed..
     *
     * @return  The namespace of the method that has to be executed..
     */
    public String getNamespace()
    {
        return m_sNamespace;
    }

    /**
     * This method gets whether or not the service is currently running.
     *
     * @return  Whether or not the service is currently running.
     */
    public boolean isServiceRunning()
    {
        return m_bIsRunning;
    }

    /**
     * This method sets wether or not the service is currently running.
     *
     * @param  bIsRunning  Whether or not the service is currently running.
     */
    public void setIsServiceRunning(boolean bIsRunning)
    {
        m_bIsRunning = bIsRunning;
    }
}
