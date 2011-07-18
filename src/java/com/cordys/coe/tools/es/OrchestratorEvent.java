package com.cordys.coe.tools.es;

import com.cordys.coe.exception.GeneralException;
import com.cordys.coe.tools.log4j.Util;
import com.cordys.coe.util.XMLProperties;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Node;

import java.io.BufferedWriter;
import java.io.IOException;

import java.util.Date;

/**
 * Wrapper around orchestrator.
 *
 * @author  pgussow
 */
public class OrchestratorEvent
    implements ILogEvent
{
    /**
     * Identifies the source.
     */
    private static final String SOURCE = "source";
    /**
     * Identifies the actual message.
     */
    private static final String MESSAGE = "message";
    /**
     * Identifies the date-time.
     */
    private static final String DATETIME = "datetime";
    /**
     * Identifies the tracelevel.
     */
    private static final String TRACE_LEVEL = "tracelevel";
    /**
     * Identifies the string to find the processid.
     */
    private static final String PROC_ID_IDENT = "PROCESS MANAGER : Process Instance ID :";
    /**
     * Static document for XML handling.
     */
    private static Document s_dDoc = new Document();
    /**
     * Holds the received message.
     */
    private ILogEvent m_leSourceEvent;
    /**
     * Holds the XML properties.
     */
    private XMLProperties m_xpProps;

    /**
     * Creates a new LogEvent object.
     *
     * @param   leEvent  The base LogEvent.
     *
     * @throws  GeneralException  DOCUMENTME
     */
    public OrchestratorEvent(ILogEvent leEvent)
                      throws GeneralException
    {
        this.m_leSourceEvent = leEvent;

        String sData = leEvent.getMessage();

        int iNode = 0;

        try
        {
            iNode = s_dDoc.parseString(sData);
        }
        catch (Exception e)
        {
        }

        if (iNode == 0)
        {
            throw new GeneralException("Could not find the data.");
        }
        m_xpProps = new XMLProperties(iNode);
    }

    /**
     * This method returns true if all the.
     *
     * @param   oeTemp  The LogEvent to compare to.
     *
     * @return  true if the objects are equal. Otherwise false.
     */
    public boolean equals(OrchestratorEvent oeTemp)
    {
        boolean bReturn = false;

        if (oeTemp != null)
        {
            bReturn = (oeTemp.getTime().equals(getTime())) &&
                      (oeTemp.getSource().equals(getSource())) &&
                      (oeTemp.getTraceLevel().equals(getTraceLevel())) &&
                      (oeTemp.getMessage().equals(getMessage()));
        }

        return bReturn;
    }

    /**
     * This method returns the category.
     *
     * @return  The category.
     */
    public String getCategory()
    {
        return getSource();
    }

    /**
     * This method returns a string [] containing all the connectors.
     *
     * @return  A string [] containing all the connectors.
     */
    public String[] getConnectors()
    {
        return m_leSourceEvent.getConnectors();
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
     */
    public String getHost()
    {
        return m_leSourceEvent.getHost();
    }

    /**
     * This method returns the actual message.
     *
     * @return  The actual message.
     */
    public String getMessage()
    {
        String sReturn = "";
        int iNode = m_xpProps.getXMLNode(MESSAGE);
        int iFirstSib = Node.getFirstChild(iNode);
        int iLastSib = Node.getLastChild(iNode);

        sReturn = new String(Node.write(iFirstSib, iLastSib, Node.WRITE_NORMAL));

        return sReturn;
    }

    /**
     * This method returns the NDC of the log message.
     *
     * @return  The NDC.
     */
    public String getNDC()
    {
        return m_leSourceEvent.getNDC();
    }

    /**
     * This method returns the processID which is found based on this path: PROCESS MANAGER :
     * Process Instance ID :
     *
     * @return  The ID is it was found. Otherwise false.
     */
    public String getPID()
    {
        String sReturn = null;

        String sDetails = getMessage();
        int iPos = sDetails.indexOf(PROC_ID_IDENT);

        if (iPos > -1)
        {
            iPos = iPos + PROC_ID_IDENT.length();
            sReturn = sDetails.substring(iPos);
        }
        return sReturn;
    }

    /**
     * This method returns the source.
     *
     * @return  The soruce.
     */
    public String getSource()
    {
        return m_xpProps.getStringValue(SOURCE);
    }

    /**
     * This method returns the received message.
     *
     * @return  The received message.
     */
    public ILogEvent getSourceLogEvent()
    {
        return m_leSourceEvent;
    }

    /**
     * This method returns the process id.
     *
     * @return  The process id.
     */
    public String getSplitSource()
    {
        String sReturn = getSource();
        sReturn = sReturn.substring(sReturn.lastIndexOf(".") + 1);

        return sReturn;
    }

    /**
     * This method returns the thread.
     *
     * @return  The thread.
     */
    public String getThread()
    {
        return m_leSourceEvent.getThread();
    }

    /**
     * This method returns the date.
     *
     * @return  The date.
     */
    public Date getTime()
    {
        return new Date(m_xpProps.getLongValue(DATETIME, new Date().getTime()));
    }

    /**
     * This method returns the trace level.
     *
     * @return  The trace level.
     */
    public String getTraceLevel()
    {
        return m_xpProps.getStringValue(TRACE_LEVEL);
    }

    /**
     * This method returns the string representation of the object. In this case the category.
     *
     * @return  The string representation.
     */
    @Override public String toString()
    {
        return getSource();
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
        m_leSourceEvent.writeToWriter(bwOut);
    }
}
