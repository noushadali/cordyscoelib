package com.cordys.coe.tools.snapshot.config;

import com.cordys.coe.tools.snapshot.data.Constants;

import javax.xml.bind.annotation.XmlElement;

/**
 * Wraps the configuration details for the server.
 *
 * @author  localpg
 */
public class Server
{
    /**
     * Holds the name of the server.
     */
    private String m_name;
    /**
     * Holds the port on which to connect.
     */
    private int m_port;

    /**
     * Creates a new Server object.
     */
    public Server()
    {
    }

    /**
     * Creates a new Server object.
     *
     * @param  name  The name of the server.
     * @param  port  The port on which to connect.
     */
    public Server(String name, int port)
    {
        m_name = name;
        m_port = port;
    }

    /**
     * This method gets the port on which to connect.
     *
     * @return  The port on which to connect.
     */
    @XmlElement(name = "Port", namespace = Constants.NS)
    public int getPort()
    {
        return m_port;
    }

    /**
     * This method sets the port on which to connect.
     *
     * @param  port  The port on which to connect.
     */
    public void setPort(int port)
    {
        m_port = port;
    }

    /**
     * This method gets the name of the server.
     *
     * @return  The name of the server.
     */
    @XmlElement(name = "Name", namespace = Constants.NS)
    public String getName()
    {
        return m_name;
    }

    /**
     * This method sets the name of the server.
     *
     * @param  name  The name of the server.
     */
    public void setName(String name)
    {
        m_name = name;
    }

    /**
     * @see  java.lang.Object#toString()
     */
    @Override public String toString()
    {
        StringBuilder sb = new StringBuilder(1024);

        sb.append(getName());

        if (m_port > 0)
        {
            sb.append(":").append(getPort());
        }

        return sb.toString();
    }
}
