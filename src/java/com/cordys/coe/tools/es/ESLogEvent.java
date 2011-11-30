package com.cordys.coe.tools.es;

import com.cordys.coe.exception.GeneralException;
import com.cordys.coe.tools.log4j.Util;
import com.cordys.coe.util.XMLProperties;

import com.eibus.xml.nom.Find;
import com.eibus.xml.nom.Node;

import java.io.BufferedWriter;
import java.io.IOException;

import java.util.Date;

/**
 * The class that wraps around an event received from the event service.
 *
 * @author  pgussow
 */
@SuppressWarnings("deprecation")
public class ESLogEvent
    implements ILogEvent
{
    /**
     * DOCUMENTME.
     */
    private static final String CONNECTORS = "connectors";
    /**
     * DOCUMENTME.
     */
    private static final String CONNECTOR = "connector";
    /**
     * DOCUMENTME.
     */
    private static final String MESSAGE = "message";
    /**
     * DOCUMENTME.
     */
    private static final String TIME = "date";
    /**
     * DOCUMENTME.
     */
    private static final String CATEGORY = "category";
    /**
     * DOCUMENTME.
     */
    private static final String HOST = "host";
    /**
     * DOCUMENTME.
     */
    private static final String TRACE_LEVEL = "tracelevel";
    /**
     * DOCUMENTME.
     */
    private static final String PID = "pid";
    /**
     * DOCUMENTME.
     */
    private static final String THREAD = "thread";
    /**
     * DOCUMENTME.
     */
    private ReceivedMessage m_rmMessage;
    /**
     * DOCUMENTME.
     */
    private XMLProperties m_xpProps;

    /**
     * Creates a new LogEvent object.
     *
     * @param   rmMessage  The message that was received.
     *
     * @throws  GeneralException  DOCUMENTME
     */
    public ESLogEvent(ReceivedMessage rmMessage)
               throws GeneralException
    {
        this.m_rmMessage = rmMessage;
        m_xpProps = new XMLProperties(rmMessage.getData());
    }

    /**
     * This method creates the object based on an XML node.
     *
     * @param   iNode  The node of the logevent.
     *
     * @throws  GeneralException  DOCUMENTME
     */
    public ESLogEvent(int iNode)
               throws GeneralException
    {
        m_xpProps = new XMLProperties(Node.duplicate(iNode));
    }

    /**
     * This method returns true if all the.
     *
     * @param   leTemp  The LogEvent to compare to.
     *
     * @return  true if the objects are equal. Otherwise false.
     */
    public boolean equals(ILogEvent leTemp)
    {
        boolean bReturn = false;

        if (leTemp != null)
        {
            bReturn = (leTemp.getTime().equals(getTime())) &&
                      (leTemp.getThread().equals(getThread())) &&
                      (leTemp.getCategory().equals(getCategory())) &&
                      (leTemp.getPID().equals(getPID())) &&
                      (leTemp.getTraceLevel().equals(getTraceLevel())) &&
                      (leTemp.getHost().equals(getHost())) &&
                      (leTemp.getMessage().equals(getMessage()));
        }

        return bReturn;
    }

    /**
     * This method returns the category.
     *
     * @return  The category.
     *
     * @see     com.cordys.coe.tools.es.ILogEvent#getCategory()
     */
    public String getCategory()
    {
        return m_xpProps.getStringValue(CATEGORY, "");
    }

    /**
     * This method returns the configuration XML it is based upon.
     *
     * @return  The configuration XML it is based upon.
     */
    public int getConfigXML()
    {
        return m_xpProps.getConfigNode();
    }

    /**
     * This method returns a string [] containing all the connectors.
     *
     * @return  A string [] containing all the connectors.
     *
     * @see     com.cordys.coe.tools.es.ILogEvent#getConnectors()
     */
    public String[] getConnectors()
    {
        String[] saReturn = null;

        int iConnectors = m_xpProps.getXMLNode(CONNECTORS);

        try
        {
            int[] aiConnectors = Find.match(iConnectors, "?<" + CONNECTOR + ">");
            saReturn = new String[aiConnectors.length];

            for (int iCount = 0; iCount < aiConnectors.length; iCount++)
            {
                saReturn[iCount] = Node.getDataWithDefault(aiConnectors[iCount], "");
            }
        }
        catch (Exception e)
        {
            saReturn = new String[] { "Exception: " + e.getMessage() };
        }
        return saReturn;
    }

    /**
     * This method returns the formatted message. If this message contains a piece of XML it is
     * formatted accordingly.
     *
     * @return  The formatted message.
     */
    public String getFormattedMessage()
    {
        String sReturn = "";

        try
        {
            sReturn = Util.formatXmlMessage(getMessage());
        }
        catch (Exception e)
        {
            // Ignore it.
        }
        return sReturn;
    }

    /**
     * This method returns the host.
     *
     * @return  The host.
     *
     * @see     com.cordys.coe.tools.es.ILogEvent#getHost()
     */
    public String getHost()
    {
        return m_xpProps.getStringValue(HOST, "");
    }

    /**
     * This method returns the actual message.
     *
     * @return  The actual message.
     *
     * @see     com.cordys.coe.tools.es.ILogEvent#getMessage()
     */
    public String getMessage()
    {
        return m_xpProps.getStringValue(MESSAGE, "");
    }

    /**
     * This method returns the NDC of the log message.
     *
     * @return  The NDC.
     *
     * @see     com.cordys.coe.tools.es.ILogEvent#getNDC()
     */
    public String getNDC()
    {
        return "";
    }

    /**
     * This method returns the process id.
     *
     * @return  The process id.
     *
     * @see     com.cordys.coe.tools.es.ILogEvent#getPID()
     */
    public String getPID()
    {
        return m_xpProps.getStringValue(PID, "");
    }

    /**
     * This method returns the received message.
     *
     * @return  The received message.
     */
    public ReceivedMessage getReceivedMessage()
    {
        return m_rmMessage;
    }

    /**
     * This method returns the thread.
     *
     * @return  The thread.
     *
     * @see     com.cordys.coe.tools.es.ILogEvent#getThread()
     */
    public String getThread()
    {
        return m_xpProps.getStringValue(THREAD, "");
    }

    /**
     * This method returns the date.
     *
     * @return  The date.
     *
     * @see     com.cordys.coe.tools.es.ILogEvent#getTime()
     */
    public Date getTime()
    {
        return new Date(m_xpProps.getLongValue(TIME, new Date().getTime()));
    }

    /**
     * This method returns the trace level.
     *
     * @return  The trace level.
     *
     * @see     com.cordys.coe.tools.es.ILogEvent#getTraceLevel()
     */
    public String getTraceLevel()
    {
        return m_xpProps.getStringValue(TRACE_LEVEL, "");
    }

    /**
     * This method returns the string representation of the object. In this case the category.
     *
     * @return  The string representation.
     */
    @Override public String toString()
    {
        return getCategory();
    }

    /**
     * This method should write the logevent details to a piece of XML.
     *
     * @param   bwOut  The writer to write to.
     *
     * @throws  IOException  If the writing fails.
     */
    public void writeToWriter(BufferedWriter bwOut)
                       throws IOException
    {
        bwOut.write(Node.writeToString(m_rmMessage.getData(), true));
    }

    /**
     * This method cleans up the XML node.
     */
    @Override protected void finalize()
    {
        Node.delete(m_xpProps.getConfigNode());
    }
}
