package com.cordys.coe.tools.es;

import com.cordys.coe.tools.es.swt.EventDetails;
import com.cordys.coe.tools.log4j.Util;
import com.cordys.coe.util.xml.dom.NiceDOMWriter;
import com.cordys.coe.util.xml.dom.XMLHelper;
import com.cordys.coe.util.xml.dom.XPathHelper;

import java.io.BufferedWriter;
import java.io.IOException;

import java.util.Date;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.spi.LocationInfo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class wraps around the Log4J events.
 *
 * @author  pgussow
 */
public class Log4JLogEvent
    implements ILogEvent
{
    /**
     * Holds the connectors.
     */
    private String[] m_asConnectors = new String[] { "unknown" };
    /**
     * Holds the Log4J event details.
     */
    private EventDetails m_edDetails = null;
    /**
     * Holds the name of the host.
     */
    private String m_sHost = "unknown";
    /**
     * Holds the actual message.
     */
    private String m_sMessage = "unknown";
    /**
     * Holds the NDC for the message.
     */
    private String m_sNDC;
    /**
     * Holds the process id of the event.
     */
    private String m_sPID = "unknown";

    /**
     * Creates a new Log4JLogEvent object.
     *
     * @param  edDetails  The event details.
     */
    public Log4JLogEvent(EventDetails edDetails)
    {
        m_edDetails = edDetails;
        m_sMessage = edDetails.getMessage();
        m_sNDC = edDetails.getNDC();

        if (m_sNDC == null)
        {
            m_sNDC = "";
        }

        // Check if it's a Cordys message.
        if (getMessage().startsWith("<host>"))
        {
            // It's a Cordys message, so we're going to parse the XML.
            try
            {
                Document dDoc = XMLHelper.createDocumentFromXML("<a>" + getMessage() + "</a>",
                                                                false);
                m_sHost = XPathHelper.getStringValue(dDoc.getDocumentElement(), "host/text()",
                                                     "notfound");
                m_sPID = XPathHelper.getStringValue(dDoc.getDocumentElement(), "fprocessid/text()",
                                                    "notfound");
                m_sMessage = XPathHelper.getStringValue(dDoc.getDocumentElement(), "message/text()",
                                                        "notfound");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else if (m_sNDC.startsWith("host="))
        {
            // The format of NDC is host=xxx processid=xxx
            Matcher mMatcher = Pattern.compile("(([a-zA-Z0-9_\\-]+)=([a-zA-Z0-9_\\-]+))").matcher(m_sNDC);

            while (mMatcher.find())
            {
                String sNDCValue = mMatcher.group(2);

                if (sNDCValue.equalsIgnoreCase("host"))
                {
                    // Hostname
                    m_sHost = mMatcher.group(3);
                }
                else if (sNDCValue.equalsIgnoreCase("processid"))
                {
                    // ProcessID.
                    m_sPID = mMatcher.group(3);
                }
            }
        }
        else
        {
            m_sHost = edDetails.getHost();
        }
    }

    /**
     * This method returns the category.
     *
     * @return  The category.
     */
    public String getCategory()
    {
        return m_edDetails.getCategoryName();
    }

    /**
     * This method returns a string [] containing all the connectors.
     *
     * @return  A string [] containing all the connectors.
     */
    public String[] getConnectors()
    {
        return m_asConnectors;
    }

    /**
     * This method returns the exception string.
     *
     * @return  The exception string.
     */
    public String getException()
    {
        String sReturn = "";

        String[] saException = m_edDetails.getThrowableStrRep();

        if ((saException != null) && (saException.length > 0))
        {
            StringBuffer sbTemp = new StringBuffer("");

            for (int iCount = 0; iCount < saException.length; iCount++)
            {
                sbTemp.append(saException[iCount]);
                sbTemp.append("\n");
            }
            sReturn = sbTemp.toString();
        }

        return sReturn;
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
        return m_sHost;
    }

    /**
     * This method returns the location information.
     *
     * @return  The location information.
     */
    public String getLocationInformation()
    {
        String sReturn = m_edDetails.getLocationDetails();

        if (sReturn == null)
        {
            sReturn = "";
        }
        return sReturn;
    }

    /**
     * This method returns the actual message.
     *
     * @return  The actual message.
     */
    public String getMessage()
    {
        return m_sMessage;
    }

    /**
     * @see  com.cordys.coe.tools.es.ILogEvent#getNDC()
     */
    public String getNDC()
    {
        return m_sNDC;
    }

    /**
     * This method returns the process id.
     *
     * @return  The process id.
     */
    public String getPID()
    {
        return m_sPID;
    }

    /**
     * This method returns the thread.
     *
     * @return  The thread.
     */
    public String getThread()
    {
        return m_edDetails.getThreadName();
    }

    /**
     * This method returns the date.
     *
     * @return  The date.
     */
    public Date getTime()
    {
        return new Date(m_edDetails.getTimeStamp());
    }

    /**
     * This method returns the trace level of this event.
     *
     * @return  The trace level.
     */
    public String getTraceLevel()
    {
        return m_edDetails.getPriority().toString();
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
        Document dDoc = XMLHelper.createDocumentFromXML("<log4j:event/>", false);
        Element eRoot = dDoc.getDocumentElement();
        eRoot.setAttribute("logger", getCategory());
        eRoot.setAttribute("timestamp", String.valueOf(getTime().getTime()));
        eRoot.setAttribute("level", getTraceLevel());
        eRoot.setAttribute("thread", getThread());

        if ((getHost() != null) && (getHost().length() > 0))
        {
            eRoot.setAttribute("hostname", getHost());
        }

        String sMessage = m_edDetails.getMessage();

        if (sMessage.indexOf("<![CDATA[") > -1)
        {
            XMLHelper.createTextElementWithParentNS("log4j:message", sMessage, eRoot);
        }
        else
        {
            XMLHelper.createCDataElementWithParentNS("log4j:message", sMessage, eRoot);
        }

        String[] saException = m_edDetails.getThrowableStrRep();

        if ((saException != null) && (saException.length > 0))
        {
            StringBuffer sbTemp = new StringBuffer("");

            for (int iCount = 0; iCount < saException.length; iCount++)
            {
                sbTemp.append(saException[iCount]);
                sbTemp.append("\n");
            }

            String sThrowable = sbTemp.toString();

            if (sThrowable.indexOf("<![CDATA[") > -1)
            {
                XMLHelper.createTextElementWithParentNS("log4j:throwable", sThrowable, eRoot);
            }
            else
            {
                XMLHelper.createCDataElementWithParentNS("log4j:throwable", sThrowable, eRoot);
            }
        }

        if (m_edDetails.getLoggingEvent() != null)
        {
            LocationInfo liInfo = m_edDetails.getLoggingEvent().getLocationInformation();

            if (liInfo != null)
            {
                Element eLocInfo = XMLHelper.createElementWithParentNS("log4j:locationInfo", eRoot);
                eLocInfo.setAttribute("class", liInfo.getClassName());
                eLocInfo.setAttribute("method", liInfo.getMethodName());
                eLocInfo.setAttribute("file", liInfo.getFileName());
                eLocInfo.setAttribute("line", liInfo.getLineNumber());
            }
        }

        // Write it.
        bwOut.write(NiceDOMWriter.write(eRoot, 2, true, false, true));
    }
}
