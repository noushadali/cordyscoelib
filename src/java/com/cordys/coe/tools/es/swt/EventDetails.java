package com.cordys.coe.tools.es.swt;

import com.cordys.coe.util.general.ExceptionUtil;
import com.cordys.coe.util.general.Util;
import com.cordys.coe.util.xml.dom.EmptyPrefixResolver;
import com.cordys.coe.util.xml.dom.XMLHelper;
import com.cordys.coe.util.xml.dom.XPathHelper;

import java.util.Date;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Represents the details of a logging event. It is intended to overcome the problem that a
 * LoggingEvent cannot be constructed with purely fake data.
 *
 * @author   <a href="mailto:oliver@puppycrawl.com">Oliver Burn</a>
 * @version  1.0
 */
public class EventDetails
{
    /**
     * RegEx for finding the logger category.
     */
    private static final Pattern P_LOGGER = Pattern.compile("<log4j:event[^>]+logger=\"([^\"]+)\"");
    /**
     * RegEx for finding the timestamp.
     */
    private static final Pattern P_TIMESTAMP = Pattern.compile("<log4j:event[^>]+timestamp=\"([^\"]+)\"");
    /**
     * RegEx for finding the log level.
     */
    private static final Pattern P_LEVEL = Pattern.compile("<log4j:event[^>]+level=\"([^\"]+)\"");
    /**
     * RegEx for finding the thread name.
     */
    private static final Pattern P_THREAD = Pattern.compile("<log4j:event[^>]+thread=\"([^\"]+)\"");
    /**
     * RegEx for finding the NDC.
     */
    private static final Pattern P_NDC = Pattern.compile("<log4j:NDC>(<!\\[CDATA\\[)*(.+)(]]>)*</log4j:NDC>",
                                                         Pattern.DOTALL);
    /**
     * RegEx for finding the actual message.
     */
    private static final Pattern P_MESSAGE = Pattern.compile("<log4j:message>(<!\\[CDATA\\[)*(.+)(]]>)*</log4j:message>",
                                                             Pattern.DOTALL);
    /**
     * RegEx for finding the throwable (if available).
     */
    private static final Pattern P_THROWABLE = Pattern.compile("<log4j:throwable>(<!\\[CDATA\\[)*(.+)(]]>)*</log4j:throwable>",
                                                               Pattern.DOTALL);
    /**
     * RegEx for finding the location info class.
     */
    private static final Pattern P_LOC_CLASS = Pattern.compile("<log4j:locationInfo[^>]+class=\"([^\"]+)\"");
    /**
     * RegEx for finding the location info method.
     */
    private static final Pattern P_LOC_METHOD = Pattern.compile("<log4j:locationInfo[^>]+method=\"([^\"]+)\"");
    /**
     * RegEx for finding the location info file.
     */
    private static final Pattern P_LOC_FILE = Pattern.compile("<log4j:locationInfo[^>]+file=\"([^\"]+)\"");
    /**
     * RegEx for finding the location info line.
     */
    private static final Pattern P_LOC_LINE = Pattern.compile("<log4j:locationInfo[^>]+line=\"([^\"]+)\"");
    /**
     * Holds the logging event.
     */
    private LoggingEvent m_leEvent;
    /**
     * Holds the name of the host it was received from.
     */
    private String m_sHost;
    /**
     * the category of the event.
     */
    private final String mCategoryName;
    /**
     * the location details for the event.
     */
    private final String mLocationDetails;
    /**
     * the msg for the event.
     */
    private final String mMessage;
    /**
     * the NDC for the event.
     */
    private final String mNDC;
    /**
     * the priority of the event.
     */
    private final Priority mPriority;
    /**
     * the thread for the event.
     */
    private final String mThreadName;
    /**
     * the throwable details the event.
     */
    private final String[] mThrowableStrRep;
    /**
     * the time of the event.
     */
    private final long mTimeStamp;

    /**
     * Creates a new <code>EventDetails</code> instance.
     *
     * @param  aEvent  a <code>LoggingEvent</code> value
     * @param  sHost   DOCUMENTME
     */
    public EventDetails(LoggingEvent aEvent, String sHost)
    {
        this(aEvent.timeStamp, aEvent.getLevel(), aEvent.getLoggerName(), aEvent.getNDC(),
             aEvent.getThreadName(), aEvent.getRenderedMessage(), aEvent.getThrowableStrRep(),
             (aEvent.getLocationInformation() == null) ? null
                                                       : aEvent.getLocationInformation().fullInfo,
             sHost);
        m_leEvent = aEvent;
    }

    /**
     * Creates a new <code>EventDetails</code> instance.
     *
     * @param  aTimeStamp        a <code>long</code> value
     * @param  aPriority         a <code>Priority</code> value
     * @param  aCategoryName     a <code>String</code> value
     * @param  aNDC              a <code>String</code> value
     * @param  aThreadName       a <code>String</code> value
     * @param  aMessage          a <code>String</code> value
     * @param  aThrowableStrRep  a <code>String[]</code> value
     * @param  aLocationDetails  a <code>String</code> value
     * @param  sHost             DOCUMENTME
     */
    public EventDetails(long aTimeStamp, Priority aPriority, String aCategoryName, String aNDC,
                        String aThreadName, String aMessage, String[] aThrowableStrRep,
                        String aLocationDetails, String sHost)
    {
        mTimeStamp = aTimeStamp;
        mPriority = aPriority;
        mCategoryName = aCategoryName;
        mNDC = aNDC;
        mThreadName = aThreadName;
        mMessage = aMessage;
        mThrowableStrRep = aThrowableStrRep;
        mLocationDetails = aLocationDetails;
        m_sHost = sHost;
    }

    /**
     * This method creates the object based on the details as they are written to file b y a Cordys
     * C1 Log.
     *
     * @param   sBaseXML  The XML for this message
     *
     * @return  The corresponding EventDetails.
     */
    public static EventDetails createInstanceFromCordysLog4JXML(String sBaseXML)
    {
        EventDetails edReturn = null;

        sBaseXML = sBaseXML.trim();

        try
        {
            Document dDoc = XMLHelper.createDocumentFromXML(sBaseXML, false);
            Element eRoot = dDoc.getDocumentElement();

            long lTimestamp = Long.parseLong(eRoot.getAttribute("timestamp"));
            Priority pLevel = Level.toLevel(eRoot.getAttribute("level"));
            String sLoggerName = eRoot.getAttribute("logger");
            String sNDC = "";
            String sThread = eRoot.getAttribute("thread");

            String sMessage = XPathHelper.getStringValue(eRoot, "log4j:message/text()", "notfound");
            String sThrowable = XPathHelper.getStringValue(eRoot, "log4j:throwable/text()", "");

            String[] saThrowable = null;

            if (sThrowable.length() > 0)
            {
                saThrowable = new String[] { sThrowable };
            }

            Element iLocationInfo = (Element) XPathHelper.selectSingleNode(eRoot,
                                                                           "log4j:locationInfo");
            String sLocationInfo = "";

            if (iLocationInfo != null)
            {
                sLocationInfo = iLocationInfo.getAttribute("class");
                sLocationInfo += ".";
                sLocationInfo += iLocationInfo.getAttribute("method");
                sLocationInfo += "(";
                sLocationInfo += iLocationInfo.getAttribute("file");
                sLocationInfo += ":";
                sLocationInfo += iLocationInfo.getAttribute("line");
                sLocationInfo += ")";
            }

            String sHost = eRoot.getAttribute("hostname");

            edReturn = new EventDetails(lTimestamp, pLevel, sLoggerName, sNDC, sThread, sMessage,
                                        saThrowable, sLocationInfo, sHost);
        }
        catch (Exception e)
        {
            System.err.println("Error: " + ExceptionUtil.getSimpleErrorTrace(e, true));
            System.err.println("\tSourceXML: " + sBaseXML);
        }

        return edReturn;
    }

    /**
     * This method creates a new EventDetails from the Log4J XML.
     *
     * @param   sBaseXML  the full XML.
     *
     * @return  The new EventDetails object.
     */
    public static EventDetails createInstanceFromLog4JXML(String sBaseXML)
    {
        EventDetails edReturn = null;

        // We'll wrap the XML with a prefix and namespace definition otherwise it won't parse.
        String sWrappedXML = "<log4j:wrapper xmlns:log4j=\"http://jakarta.apache.org/log4j/\">" +
                             sBaseXML.trim() + "</log4j:wrapper>";

        try
        {
            EmptyPrefixResolver epr = new EmptyPrefixResolver();
            epr.addNamespacePrefixBinding("log4j", "http://jakarta.apache.org/log4j/");

            Document dDoc = XMLHelper.createDocumentFromXML(sWrappedXML, true);

            if (dDoc != null)
            {
                Element eRoot = dDoc.getDocumentElement();
                Element eEvent = (Element) eRoot.getFirstChild();

                long lTimestamp = Long.parseLong(eEvent.getAttribute("timestamp"));
                Priority pLevel = Level.toLevel(eEvent.getAttribute("level"));
                String sLoggerName = eEvent.getAttribute("logger");
                String sNDC = "";
                String sThread = eEvent.getAttribute("thread");

                String sMessage = XPathHelper.getStringValue(eEvent, "./log4j:message/text()", epr,
                                                             "notfound");

                String sThrowable = XPathHelper.getStringValue(eEvent, "./log4j:throwable/text()",
                                                               epr, "");
                sNDC = XPathHelper.getStringValue(eEvent, "./log4j:NDC/text()", epr, "");

                String[] saThrowable = null;

                if (sThrowable.length() > 0)
                {
                    saThrowable = new String[] { sThrowable };
                }

                Element eLocationInfo = (Element) XPathHelper.selectSingleNode(eEvent,
                                                                               "log4j:locationInfo",
                                                                               epr);
                String sLocationInfo = "";

                if (eLocationInfo != null)
                {
                    sLocationInfo = eLocationInfo.getAttribute("class");
                    sLocationInfo += ".";
                    sLocationInfo += eLocationInfo.getAttribute("method");
                    sLocationInfo += "(";
                    sLocationInfo += eLocationInfo.getAttribute("file");
                    sLocationInfo += ":";
                    sLocationInfo += eLocationInfo.getAttribute("line");
                    sLocationInfo += ")";
                }

                String sHost = eEvent.getAttribute("hostname");

                edReturn = new EventDetails(lTimestamp, pLevel, sLoggerName, sNDC, sThread,
                                            sMessage, saThrowable, sLocationInfo, sHost);
            }
            else
            {
                // The XML could not be parsed. Most likely either the message or the throwable
                // contained a nested CDATA which gave the problem. So we're going to try the regex
                // approach.
                String sLoggerName = getFromRegex(P_LOGGER, sBaseXML, 1);
                long lTimestamp = Long.parseLong(getFromRegex(P_TIMESTAMP, sBaseXML, 1));
                Priority pLevel = Level.toLevel(getFromRegex(P_LEVEL, sBaseXML, 1));
                String sThread = getFromRegex(P_THREAD, sBaseXML, 1);

                String sNDC = getFromRegex(P_NDC, sBaseXML, 2);
                String sMessage = getFromRegex(P_MESSAGE, sBaseXML, 2);

                String[] saThrowable = new String[0];
                String sThrowable = getFromRegex(P_THROWABLE, sBaseXML, 2);

                if ((sThrowable != null) && (sThrowable.length() > 0))
                {
                    saThrowable = new String[] { sThrowable };
                }

                StringBuilder sbLocInfo = new StringBuilder(2048);
                sbLocInfo.append(getFromRegex(P_LOC_CLASS, sBaseXML, 1));
                sbLocInfo.append(".");
                sbLocInfo.append(getFromRegex(P_LOC_METHOD, sBaseXML, 1));
                sbLocInfo.append("(");
                sbLocInfo.append(getFromRegex(P_LOC_FILE, sBaseXML, 1));
                sbLocInfo.append(":");
                sbLocInfo.append(getFromRegex(P_LOC_LINE, sBaseXML, 1));
                sbLocInfo.append(")");

                if (sLoggerName != null)
                {
                    edReturn = new EventDetails(lTimestamp, pLevel, sLoggerName, sNDC, sThread,
                                                sMessage, saThrowable, sbLocInfo.toString(), "");
                }
            }
        }
        catch (Exception e)
        {
            // We'll add the raw matched base XML
            edReturn = new EventDetails(new Date().getTime(), Level.ERROR, "unknown", "unknown",
                                        "unknown", sBaseXML, new String[] { Util.getStackTrace(e) },
                                        "unknown", "unknown");
        }

        return edReturn;
    }

    /**
     * @see  #mCategoryName
     */
    public String getCategoryName()
    {
        return mCategoryName;
    }

    /**
     * This method gets the host from which the event was received.
     *
     * @return  The host from which the event was received.
     */
    public String getHost()
    {
        return m_sHost;
    }

    /**
     * @see  #mLocationDetails
     */
    public String getLocationDetails()
    {
        return mLocationDetails;
    }

    /**
     * This method gets the LoggingEvent.
     *
     * @return  The LoggingEvent.
     */
    public LoggingEvent getLoggingEvent()
    {
        return m_leEvent;
    }

    /**
     * @see  #mMessage
     */
    public String getMessage()
    {
        return mMessage;
    }

    /**
     * @see  #mNDC
     */
    public String getNDC()
    {
        return mNDC;
    }

    /**
     * @see  #mPriority
     */
    public Priority getPriority()
    {
        return mPriority;
    }

    /**
     * @see  #mThreadName
     */
    public String getThreadName()
    {
        return mThreadName;
    }

    /**
     * @see  #mThrowableStrRep
     */
    public String[] getThrowableStrRep()
    {
        return mThrowableStrRep;
    }

    /**
     * @see  #mTimeStamp
     */
    public long getTimeStamp()
    {
        return mTimeStamp;
    }

    /**
     * This method will return the value of the string based on a regex.
     *
     * @param   pRegEx   The regex to execute.
     * @param   sSource  The source string to apply it to.
     * @param   iGroup   The number of the group to return.
     *
     * @return  THe string found.
     */
    private static String getFromRegex(Pattern pRegEx, String sSource, int iGroup)
    {
        String sReturn = null;

        Matcher m = pRegEx.matcher(sSource);

        if (m.find() && (m.groupCount() >= iGroup))
        {
            sReturn = m.group(iGroup);
        }

        return sReturn;
    }
}
