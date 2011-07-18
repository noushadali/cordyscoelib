package com.cordys.coe.tools.orgmanager;

import com.cordys.coe.tools.orgmanager.log4j.Log4JConfigurationWrapper;
import com.cordys.coe.util.general.ldap.LDAPUtils;
import com.cordys.coe.util.xml.dom.NiceDOMWriter;
import com.cordys.coe.util.xml.dom.XMLHelper;
import com.cordys.coe.util.xml.dom.XPathHelper;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPEntry;

import java.io.ByteArrayInputStream;

import java.util.ArrayList;
import java.util.Iterator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class wraps the details of a SOAP processor. This will be the LDAP entry, the configuration
 * details and the runtime information for a SOAP processor.
 *
 * @author  pgussow
 */
public class Processor
{
    /**
     * Holds the status that identifies when a processor is stopped.
     */
    public static final String STATUS_STOPPED = "Stopped";
    /**
     * Holds the status that identifies when a processor is started.
     */
    public static final String STATUS_STARTED = "Started";
    /**
     * Holds the status that identifies when a processor is being started.
     */
    public static final String STATUS_STARTING = "Starting";
    /**
     * Holds the status that identifies when a processor has a configuration error.
     */
    public static final String STATUS_CONFIGURATION_ERROR = "Configuration Error";

    /**
     * Holds the logger that is used.
     */
    private static final Logger LOG = Logger.getLogger(Processor.class);
    /**
     * Holds the list of JVM options EXCLUDING the classpath.
     */
    private String[] m_asJVMOptions;
    /**
     * Holds whether or not the processor is configured to run in debug mode.
     */
    private boolean m_bInDebugMode;
    /**
     * Holds whether or not we're running Linux.
     */
    private boolean m_bIsLinux;
    /**
     * Holds whether or not this processor is a monitor SOAP processor.
     */
    private boolean m_bIsMonitor;
    /**
     * Holds whether or not the SOAP processor is configured to start automatically.
     */
    private boolean m_bStartAutomatically;
    /**
     * Holds the configuration XML for the SOAP processor.
     */
    private Element m_eConfigurationXML;
    /**
     * Holds the process-id.
     */
    private int m_iProcessID;
    /**
     * Holds the LDAP entry for this processor.
     */
    private LDAPEntry m_leEntry;
    /**
     * Holds the last processing time.
     */
    private long m_lLastProcessingTime;
    /**
     * Holds the total resident memory usage.
     */
    private long m_lResidentMemoryUsage;
    /**
     * Holds the total CPU time taken by this processor.
     */
    private String m_lTotalCpuTime;
    /**
     * Holds the total amount of memory taken by NOM.
     */
    private long m_lTotalNomMemory;
    /**
     * Holds the total amount of NOM nodes currently in memory for this processor.
     */
    private long m_lTotalNOMNodes;
    /**
     * Holds the total processing time.
     */
    private long m_lTotalProcessingTime;
    /**
     * Holds the total number of SOAP docs that this processor has processed.
     */
    private long m_lTotalSOAPDocumentsProcessed;
    /**
     * Holds the total amount of virtual memory being used.
     */
    private long m_lVirtualMemoryUsage;
    /**
     * Holds the computer on which the processor is running.
     */
    private String m_sComputer;
    /**
     * Holds the custom classpath for this processor.
     */
    private String[] m_sCustomClasspath;
    /**
     * Holds the description for the SOAP processor.
     */
    private String m_sDescription;
    /**
     * Holds the configuration error details.
     */
    private String m_sErrorDetails;
    /**
     * Holds the GUID for this running instance.
     */
    private String m_sGUID;
    /**
     * Holds the name of the SOAP processor.
     */
    private String m_sName;
    /**
     * Holds the organization in which the SP is running.
     */
    private String m_sOrganization;
    /**
     * Holds the short name for the organization.
     */
    private String m_sOrganizationShortName;
    /**
     * Holds the OS process configured for this processor.
     */
    private String m_sOSProcess;
    /**
     * Holds the name of the SOAP node parent.
     */
    private String m_sParentSoapNode;
    /**
     * Holds the short name for this SOAP processor.
     */
    private String m_sShortName;
    /**
     * Holds the status.
     */
    private String m_sStatus;

    /**
     * Creates a new Processor object.
     *
     * @param  leEntry  The LDAP entry for the SOAP processor.
     */
    public Processor(LDAPEntry leEntry)
    {
        m_leEntry = leEntry;

        m_sDescription = LDAPUtils.getAttrValue(leEntry, "description");
        m_bStartAutomatically = "true".equals(LDAPUtils.getAttrValue(leEntry, "automaticstart"));
        m_sOSProcess = LDAPUtils.getAttrValue(leEntry, "busosprocesshost");
        m_sComputer = LDAPUtils.getAttrValue(leEntry, "computer");

        String sConfiguration = LDAPUtils.getAttrValue(leEntry, "bussoapprocessorconfiguration");

        Document dDoc = null;

        try
        {
            dDoc = XMLHelper.createDocumentBuilder(false).parse(new ByteArrayInputStream(sConfiguration
                                                                                         .getBytes()));
        }
        catch (Exception ex)
        {
            LOG.warn("Error getting the configuration XML");
        }

        if (dDoc != null)
        {
            m_eConfigurationXML = dDoc.getDocumentElement();
        }
        else
        {
            throw new RuntimeException("Processor " + leEntry.getDN() +
                                       " has invalid XML as configuration.");
        }

        // Fix the proper namespace mapping
        Matcher m = java.util.regex.Pattern.compile("cn=([^,]+),cn=([^,]+),cn=soap nodes,(o=([^,]+),.+)")
                                           .matcher(leEntry.getDN());
        m.find();

        m_sShortName = m.group(1);
        m_sParentSoapNode = m.group(2);
        m_sOrganization = m.group(3);
        m_sOrganizationShortName = m.group(4);

        parseConfigurationXML();
    }

    /**
     * This method gets the autostart count.
     *
     * @return  The autostart count.
     */
    public long getAutoStartCount()
    {
        long lReturn = 3L;

        try
        {
            lReturn = XPathHelper.getLongValue(m_eConfigurationXML, "./@autoStartCount", 3L);
        }
        catch (TransformerException e)
        {
            LOG.warn("Error getting autoStartCount", e);
        }

        return lReturn;
    }

    /**
     * This method gets the cancelReplyInterval for this processor.
     *
     * @return  The cancelReplyInterval for this processor.
     */
    public long getCancelReplyInterval()
    {
        long lReturn = 30000L;

        try
        {
            return XPathHelper.getLongValue(m_eConfigurationXML, "./cancelReplyInterval/text()",
                                            30000L);
        }
        catch (TransformerException e)
        {
            LOG.warn("Error getting the attribute canReplyInterval", e);
        }

        return lReturn;
    }

    /**
     * This method gets the computer on which the processor is running.
     *
     * @return  The computer on which the processor is running.
     */
    public String getComputer()
    {
        return m_sComputer;
    }

    /**
     * This method gets the configuration XML for the SOAP processor.
     *
     * @return  The configuration XML for the SOAP processor.
     */
    public Element getConfigurationXML()
    {
        return m_eConfigurationXML;
    }

    /**
     * This method gets the custom classpath for this processor.
     *
     * @return  The custom classpath for this processor.
     */
    public String[] getCustomClasspath()
    {
        return m_sCustomClasspath;
    }

    /**
     * This method gets the description for the SOAP processor.
     *
     * @return  The description for the SOAP processor.
     */
    public String getDescription()
    {
        return m_sDescription;
    }

    /**
     * This method gets the DN for this Soap processor.
     *
     * @return  The DN for this Soap processor.
     */
    public String getDN()
    {
        return m_leEntry.getDN();
    }

    /**
     * This method gets the configuration error details.
     *
     * @return  The configuration error details.
     */
    public String getErrorDetails()
    {
        return m_sErrorDetails;
    }

    /**
     * This method gets the formatted configuration XML for the SOAP processor.
     *
     * @return  The formatted configuration XML for the SOAP processor.
     */
    public String getFormattedConfigurationXML()
    {
        return NiceDOMWriter.write(m_eConfigurationXML, 2, true, false, false);
    }

    /**
     * This method gets the GUID for this running instance.
     *
     * @return  The GUID for this running instance.
     */
    public String getGUID()
    {
        return m_sGUID;
    }

    /**
     * This method gets the list of JVM options EXCLUDING the classpath.
     *
     * @return  The list of JVM options EXCLUDING the classpath.
     */
    public String[] getJVMOptions()
    {
        return m_asJVMOptions;
    }

    /**
     * This method gets the last processing time.
     *
     * @return  The last processing time.
     */
    public long getLastProcessingTime()
    {
        return m_lLastProcessingTime;
    }

    /**
     * This method gets the LDAP entry for this processor.
     *
     * @return  The LDAP entry for this processor.
     */
    public LDAPEntry getLDAPEntry()
    {
        return m_leEntry;
    }

    /**
     * This method gets the Log4J configuration.
     *
     * @return  The Log4J configuration.
     */
    public Element getLog4JConfiguration()
    {
        Element eReturn = null;

        try
        {
            eReturn = (Element) XPathHelper.selectSingleNode(m_eConfigurationXML,
                                                             "//loggerconfiguration/configuration");
        }
        catch (TransformerException e)
        {
            LOG.warn("Error getting Log4J configuration", e);
        }

        return eReturn;
    }

    /**
     * This method gets the Log4JConfigurationWrapper for this processor.
     *
     * @return  The Log4JConfigurationWrapper for this processor.
     */
    public Log4JConfigurationWrapper getLog4JConfigurationWrapper()
    {
        return new Log4JConfigurationWrapper(getLog4JConfiguration());
    }

    /**
     * This method gets the name of the SOAP processor.
     *
     * @return  The name of the SOAP processor.
     */
    public String getName()
    {
        return m_sName;
    }

    /**
     * This method returns a new entry based on the current values for the class.
     *
     * @return  The new values.
     */
    public LDAPEntry getNewEntry()
    {
        LDAPEntry leReturn = new LDAPEntry(m_leEntry.getDN());

        // Copy the LDAP attributed
        LDAPAttributeSet as = m_leEntry.getAttributeSet();
        Iterator<?> iTemp = as.iterator();

        while (iTemp.hasNext())
        {
            LDAPAttribute la = (LDAPAttribute) iTemp.next();
            LDAPAttribute laNew = new LDAPAttribute(la.getName());
            String[] asValues = la.getStringValueArray();

            for (String sValue : asValues)
            {
                laNew.addValue(sValue);
            }
            leReturn.getAttributeSet().add(laNew);
        }

        // Now set the properties
        handleLDAPEntry("computer", getComputer(), leReturn);
        handleLDAPEntry("automaticstart",
                        (getStartupType().equals("Automatically") ? "true" : "false"), leReturn);
        handleLDAPEntry("busosprocesshost", getOSProcess(), leReturn);
        handleLDAPEntry("description", getDescription(), leReturn);

        handleLDAPEntry("bussoapprocessorconfiguration",
                        NiceDOMWriter.write(m_eConfigurationXML, 0, false, false, false), leReturn);

        return leReturn;
    }

    /**
     * This method gets the organization in which the SP is running.
     *
     * @return  The organization in which the SP is running.
     */
    public String getOrganization()
    {
        return m_sOrganization;
    }

    /**
     * This method gets the short name for the organization.
     *
     * @return  The short name for the organization.
     */
    public String getOrganizationShortName()
    {
        return m_sOrganizationShortName;
    }

    /**
     * This method gets the OS process configured for this processor.
     *
     * @return  The OS process configured for this processor.
     */
    public String getOSProcess()
    {
        return m_sOSProcess;
    }

    /**
     * This method gets the name of the SOAP node parent.
     *
     * @return  The name of the SOAP node parent.
     */
    public String getParentSoapNode()
    {
        return m_sParentSoapNode;
    }

    /**
     * This method gets the process-id.
     *
     * @return  The process-id.
     */
    public int getProcessID()
    {
        return m_iProcessID;
    }

    /**
     * This method gets the total resident memory usage.
     *
     * @return  The total resident memory usage.
     */
    public long getResidentMemoryUsage()
    {
        return m_lResidentMemoryUsage;
    }

    /**
     * This method gets the short name for this SOAP processor.
     *
     * @return  The short name for this SOAP processor.
     */
    public String getShortName()
    {
        return m_sShortName;
    }

    /**
     * This method gets the start up type.
     *
     * @return  The start up type.
     */
    public String getStartupType()
    {
        if (startsAutomatically())
        {
            return "Automatically";
        }
        return "Manual";
    }

    /**
     * This method gets the status.
     *
     * @return  The status.
     */
    public String getStatus()
    {
        if (m_sStatus == null)
        {
            m_sStatus = STATUS_STOPPED;
        }
        return m_sStatus;
    }

    /**
     * This method gets whether or not the system policy should be used for logging.
     *
     * @return  Whether or not the system policy should be used for logging.
     */
    public boolean getSystemPolicy()
    {
        boolean bReturn = true;

        try
        {
            bReturn = XPathHelper.getBooleanValue(m_eConfigurationXML,
                                                  "//loggerconfiguration/systempolicy/text()", null,
                                                  true);
        }
        catch (TransformerException e)
        {
            LOG.warn("Error getting the system policy", e);
        }

        return bReturn;
    }

    /**
     * This method gets the total CPU time taken by this processor.
     *
     * @return  The total CPU time taken by this processor.
     */
    public String getTotalCpuTime()
    {
        return m_lTotalCpuTime;
    }

    /**
     * This method gets the total amount of memory taken by NOM.
     *
     * @return  The total amount of memory taken by NOM.
     */
    public long getTotalNomMemory()
    {
        return m_lTotalNomMemory;
    }

    /**
     * This method gets the total amount of NOM nodes currently in memory for this processor.
     *
     * @return  The total amount of NOM nodes currently in memory for this processor.
     */
    public long getTotalNOMNodes()
    {
        return m_lTotalNOMNodes;
    }

    /**
     * This method gets the total processing time.
     *
     * @return  The total processing time.
     */
    public long getTotalProcessingTime()
    {
        return m_lTotalProcessingTime;
    }

    /**
     * This method gets the total number of SOAP docs that this processor has processed.
     *
     * @return  The total number of SOAP docs that this processor has processed.
     */
    public long getTotalSOAPDocumentsProcessed()
    {
        return m_lTotalSOAPDocumentsProcessed;
    }

    /**
     * This method gets the total amount of virtual memory being used.
     *
     * @return  The total amount of virtual memory being used.
     */
    public long getVirtualMemoryUsage()
    {
        return m_lVirtualMemoryUsage;
    }

    /**
     * This method gets Whether or not the current processor has a configuration error.
     *
     * @return  Whether or not the current processor has a configuration error.
     */
    public boolean hasConfigurationError()
    {
        return "Configuration Error".equals(getStatus());
    }

    /**
     * This method gets whether or not this processor is configured to start in debug mode.
     *
     * @return  Whether or not this processor is configured to start in debug mode.
     */
    public boolean isInDebugMode()
    {
        return m_bInDebugMode;
    }

    /**
     * This method gets whether or not this processor is a monitor SOAP processor.
     *
     * @return  Whether or not this processor is a monitor SOAP processor.
     */
    public boolean isMonitor()
    {
        return m_bIsMonitor;
    }

    /**
     * This method gets whether or not the system policy is enabled for the logging.
     *
     * @return  Whether or not the system policy is enabled for the logging.
     */
    public boolean isSystemPolicyEnabled()
    {
        boolean bReturn = true;

        try
        {
            bReturn = XPathHelper.getBooleanValue(m_eConfigurationXML,
                                                  "//loggerconfiguration/systempolicy/text()");
        }
        catch (TransformerException e)
        {
            LOG.warn("Error getting systempolicy configuration", e);
        }

        return bReturn;
    }

    /**
     * This method sets the autostart count.
     *
     * @param  iAutoStartcount  The autostart count.
     */
    public void setAutoStartCount(long iAutoStartcount)
    {
        m_eConfigurationXML.setAttribute("autoStartCount", String.valueOf(iAutoStartcount));
    }

    /**
     * This method sets the cancelReplyInterval for this processor.
     *
     * @param  lCancelReplyInterval  The cancelReplyInterval for this processor.
     */
    public void setCancelReplyInterval(long lCancelReplyInterval)
    {
        try
        {
            Node nNode = XPathHelper.selectSingleNode(m_eConfigurationXML, "./cancelReplyInterval");

            if (nNode == null)
            {
                XMLHelper.createTextElementWithParentNS("cancelReplyInterval",
                                                        String.valueOf(lCancelReplyInterval),
                                                        m_eConfigurationXML);
            }
        }
        catch (TransformerException e)
        {
            LOG.warn("Error getting tag cancelReplyInterval", e);
        }
    }

    /**
     * This method sets the computer on which the processor is running.
     *
     * @param  sComputer  The computer on which the processor is running.
     */
    public void setComputer(String sComputer)
    {
        m_sComputer = sComputer;
    }

    /**
     * This method sets the configuration XML for the SOAP processor.
     *
     * @param  eConfigurationXML  The configuration XML for the SOAP processor.
     */
    public void setConfigurationXML(Element eConfigurationXML)
    {
        m_eConfigurationXML = eConfigurationXML;

        parseConfigurationXML();
    }

    /**
     * This method sets the custom classpath for this processor.
     *
     * @param  asCustomClasspath  The custom classpath for this processor.
     */
    public void setCustomClasspath(String[] asCustomClasspath)
    {
        m_sCustomClasspath = asCustomClasspath;

        try
        {
            // We need to update the current XML. First we clean the existing ones
            // except the cp.
            Element eJREConfig = (Element) XPathHelper.selectSingleNode(m_eConfigurationXML,
                                                                        "jreconfig");
            NodeList nl = XPathHelper.selectNodeList(eJREConfig, "param");
            Element eFound = null;

            for (int iCount = 0; iCount < nl.getLength(); iCount++)
            {
                Element eParam = (Element) nl.item(iCount);

                if (eParam.getAttribute("value").startsWith("- cp"))
                {
                    eFound = eParam;
                    break;
                }
            }

            if (eFound == null)
            {
                // Create the new ones.
                eFound = XMLHelper.createElementWithParentNS("param", eJREConfig);
            }

            StringBuilder sb = new StringBuilder(2048);
            sb.append("-cp ");

            for (int iCount = 0; iCount < asCustomClasspath.length; iCount++)
            {
                sb.append(asCustomClasspath[iCount]);

                if (iCount < (asCustomClasspath.length - 1))
                {
                    sb.append((m_bIsLinux ? ":" : ";"));
                }
            }

            eFound.setAttribute("value", sb.toString());
        }
        catch (TransformerException ex)
        {
            LOG.warn("Error updating custom class path", ex);
        }
    }

    /**
     * This method sets the description for the SOAP processor.
     *
     * @param  sDescription  The description for the SOAP processor.
     */
    public void setDescription(String sDescription)
    {
        m_sDescription = sDescription;
    }

    /**
     * This method sets the configuration error details.
     *
     * @param  sErrorDetails  The configuration error details.
     */
    public void setErrorDetails(String sErrorDetails)
    {
        m_sErrorDetails = sErrorDetails;
    }

    /**
     * This method sets the GUID for this running instance.
     *
     * @param  sGUID  The GUID for this running instance.
     */
    public void setGUID(String sGUID)
    {
        m_sGUID = sGUID;
    }

    /**
     * This method sets whether or not this processor is configured to start in debug mode.
     *
     * @param  bInDebugMode  Whether or not this processor is configured to start in debug mode.
     */
    public void setInDebugMode(boolean bInDebugMode)
    {
        m_bInDebugMode = bInDebugMode;
    }

    /**
     * This method sets the list of JVM options EXCLUDING the classpath.
     *
     * @param  asJVMOptions  The list of JVM options EXCLUDING the classpath.
     */
    public void setJVMOptions(String[] asJVMOptions)
    {
        m_asJVMOptions = asJVMOptions;

        try
        {
            // We need to update the current XML. First we clean the existing ones
            // except the cp.
            Element eJREConfig = (Element) XPathHelper.selectSingleNode(m_eConfigurationXML,
                                                                        "jreconfig");
            NodeList nl = XPathHelper.selectNodeList(eJREConfig, "param");

            for (int iCount = 0; iCount < nl.getLength(); iCount++)
            {
                Element eParam = (Element) nl.item(iCount);

                if (!eParam.getAttribute("value").startsWith("- cp"))
                {
                    eParam.getParentNode().removeChild(eParam);
                }
            }

            // Create the new ones.
            for (int iCount = 0; iCount < asJVMOptions.length; iCount++)
            {
                Element eNew = XMLHelper.createElementWithParentNS("param", eJREConfig);
                eNew.setAttribute("value", asJVMOptions[iCount]);
            }
        }
        catch (TransformerException ex)
        {
            LOG.warn("Error updating JVM propreties", ex);
        }
    }

    /**
     * This method sets the last processing time.
     *
     * @param  lLastProcessingTime  The last processing time.
     */
    public void setLastProcessingTime(long lLastProcessingTime)
    {
        m_lLastProcessingTime = lLastProcessingTime;
    }

    /**
     * This method sets the Log4J configuration.
     *
     * @param  eLog4JConfiguration  The Log4J configuration.
     */
    public void setLog4JConfiguration(Element eLog4JConfiguration)
    {
        try
        {
            Node nLoggerConf = XPathHelper.selectSingleNode(m_eConfigurationXML,
                                                            "./loggerconfiguration");
            Node nNode = XPathHelper.selectSingleNode(nLoggerConf, "./configuration");
            Document dDoc = nLoggerConf.getOwnerDocument();

            if (nNode != null)
            {
                // Remove the old configuration
                nLoggerConf.removeChild(nNode);
            }

            Node nTemp = dDoc.importNode(eLog4JConfiguration.cloneNode(true), true);
            nLoggerConf.appendChild(nTemp);
        }
        catch (TransformerException e)
        {
            LOG.warn("Error getting tag cancelReplyInterval", e);
        }
    }

    /**
     * This method sets the name of the SOAP processor.
     *
     * @param  sName  The name of the SOAP processor.
     */
    public void setName(String sName)
    {
        m_sName = sName;
    }

    /**
     * This method sets the organization in which the SP is running.
     *
     * @param  sOrganization  The organization in which the SP is running.
     */
    public void setOrganization(String sOrganization)
    {
        m_sOrganization = sOrganization;
    }

    /**
     * This method sets the short name for the organization.
     *
     * @param  sOrganizationShortName  The short name for the organization.
     */
    public void setOrganizationShortName(String sOrganizationShortName)
    {
        m_sOrganizationShortName = sOrganizationShortName;
    }

    /**
     * This method sets the OS process configured for this processor.
     *
     * @param  sOSProcess  The OS process configured for this processor.
     */
    public void setOSProcess(String sOSProcess)
    {
        m_sOSProcess = sOSProcess;
    }

    /**
     * This method sets the name of the SOAP node parent.
     *
     * @param  sParentSoapNode  The name of the SOAP node parent.
     */
    public void setParentSoapNode(String sParentSoapNode)
    {
        m_sParentSoapNode = sParentSoapNode;
    }

    /**
     * This method sets the process-id.
     *
     * @param  iProcessID  The process-id.
     */
    public void setProcessID(int iProcessID)
    {
        m_iProcessID = iProcessID;
    }

    /**
     * This method sets the total resident memory usage.
     *
     * @param  lResidentMemoryUsage  The total resident memory usage.
     */
    public void setResidentMemoryUsage(long lResidentMemoryUsage)
    {
        m_lResidentMemoryUsage = lResidentMemoryUsage;
    }

    /**
     * This method sets the short name for this SOAP processor.
     *
     * @param  sShortName  The short name for this SOAP processor.
     */
    public void setShortName(String sShortName)
    {
        m_sShortName = sShortName;
    }

    /**
     * This method sets whether or not the SOAP processor is configured to start automatically.
     *
     * @param  bStartAutomatically  Whether or not the SOAP processor is configured to start
     *                              automatically.
     */
    public void setStartAutomatically(boolean bStartAutomatically)
    {
        m_bStartAutomatically = bStartAutomatically;
    }

    /**
     * This method sets the start up type.
     *
     * @param  sStartupType  The start up type.
     */
    public void setStartupType(String sStartupType)
    {
        if ("Automatically".equals(sStartupType))
        {
            m_bStartAutomatically = true;
        }
        else
        {
            m_bStartAutomatically = false;
        }
    }

    /**
     * This method sets the status.
     *
     * @param  sStatus  The status.
     */
    public void setStatus(String sStatus)
    {
        m_sStatus = sStatus;
    }

    /**
     * This method sets whether or not the system policy should be used for logging.
     *
     * @param  bSystemPolicy  Whether or not the system policy should be used for logging.
     */
    public void setSystemPolicy(boolean bSystemPolicy)
    {
        try
        {
            Node nNode = XPathHelper.selectSingleNode(m_eConfigurationXML,
                                                      "//loggerconfiguration/systempolicy/text()");

            if (nNode != null)
            {
                nNode.setNodeValue(String.valueOf(bSystemPolicy));
            }
        }
        catch (TransformerException e)
        {
            LOG.warn("Error setting the system policy", e);
        }
    }

    /**
     * This method sets whether or not the system policy is enabled for the logging.
     *
     * @param  bSystemPolicy  Whether or not the system policy is enabled for the logging.
     */
    public void setSystemPolicyEnabled(boolean bSystemPolicy)
    {
        try
        {
            Node nLoggerConfig = XPathHelper.selectSingleNode(m_eConfigurationXML,
                                                              "//loggerconfiguration");

            if (nLoggerConfig != null)
            {
                Node nNode = XPathHelper.selectSingleNode(nLoggerConfig, "systempolicy/text()");

                if (nNode == null)
                {
                    XMLHelper.createTextElementWithParentNS("systempolicy",
                                                            String.valueOf(bSystemPolicy),
                                                            nLoggerConfig);
                }
                else
                {
                    nNode.setNodeValue(String.valueOf(bSystemPolicy));
                }
            }
        }
        catch (TransformerException e)
        {
            LOG.warn("Error getting tag cancelReplyInterval", e);
        }
    }

    /**
     * This method sets the total CPU time taken by this processor.
     *
     * @param  lTotalCpuTime  The total CPU time taken by this processor.
     */
    public void setTotalCpuTime(String lTotalCpuTime)
    {
        m_lTotalCpuTime = lTotalCpuTime;
    }

    /**
     * This method sets the total amount of memory taken by NOM.
     *
     * @param  lTotalNomMemory  The total amount of memory taken by NOM.
     */
    public void setTotalNomMemory(long lTotalNomMemory)
    {
        m_lTotalNomMemory = lTotalNomMemory;
    }

    /**
     * This method sets the total amount of NOM nodes currently in memory for this processor.
     *
     * @param  lTotalNOMNodes  The total amount of NOM nodes currently in memory for this processor.
     */
    public void setTotalNOMNodes(long lTotalNOMNodes)
    {
        m_lTotalNOMNodes = lTotalNOMNodes;
    }

    /**
     * This method sets the total processing time.
     *
     * @param  lTotalProcessingTime  The total processing time.
     */
    public void setTotalProcessingTime(long lTotalProcessingTime)
    {
        m_lTotalProcessingTime = lTotalProcessingTime;
    }

    /**
     * This method sets the total number of SOAP docs that this processor has processed.
     *
     * @param  lTotalSOAPDocumentsProcessed  The total number of SOAP docs that this processor has
     *                                       processed.
     */
    public void setTotalSOAPDocumentsProcessed(long lTotalSOAPDocumentsProcessed)
    {
        m_lTotalSOAPDocumentsProcessed = lTotalSOAPDocumentsProcessed;
    }

    /**
     * This method sets the total amount of virtual memory being used.
     *
     * @param  lVirtualMemoryUsage  The total amount of virtual memory being used.
     */
    public void setVirtualMemoryUsage(long lVirtualMemoryUsage)
    {
        m_lVirtualMemoryUsage = lVirtualMemoryUsage;
    }

    /**
     * This method gets whether or not the SOAP processor is configured to start automatically.
     *
     * @return  Whether or not the SOAP processor is configured to start automatically.
     */
    public boolean startsAutomatically()
    {
        return m_bStartAutomatically;
    }

    /**
     * This method determines whether or not Linux is being used.
     */
    private void determineLinux()
    {
        try
        {
            String sClasspath = XPathHelper.getStringValue(m_eConfigurationXML,
                                                           "//jreconfig/param/@value[starts-with(.,'-cp')]");

            if ((sClasspath != null) && (sClasspath.length() > 4))
            {
                sClasspath = sClasspath.substring(4).trim();

                Matcher m = Pattern.compile("^[a-zA-Z]:[/\\\\]|;[a-zA-Z]:[/\\\\]").matcher(sClasspath);

                if ((sClasspath.indexOf('\\') < 0) && (sClasspath.indexOf(';') < 0) && !m.find())
                {
                    m_bIsLinux = true;
                }
            }
        }
        catch (Exception e)
        {
            LOG.warn("Error determining Linux/Windows.");
        }
    }

    /**
     * This method adds/replaces the value for the given attribute.
     *
     * @param  sName   The name of the attribute.
     * @param  sValue  The new value for the attribute.
     * @param  le      The actual LDAP entry.
     */
    private void handleLDAPEntry(String sName, String sValue, LDAPEntry le)
    {
        LDAPAttribute la = le.getAttribute(sName);

        if (la == null)
        {
            la = new LDAPAttribute(sName);
            le.getAttributeSet().add(la);
        }

        // Check if the attribute should be removed.
        if ((sValue == null) || (sValue.length() == 0))
        {
            le.getAttributeSet().remove(la);
        }
        else
        {
            while (la.size() > 0)
            {
                la.removeValue(la.getStringValue());
            }

            la.addValue(sValue);
        }
    }

    /**
     * This method parses the current configuration XML.
     */
    private void parseConfigurationXML()
    {
        // Determine whether or not we're running on Linux.
        determineLinux();

        try
        {
            m_sCustomClasspath = new String[0];

            String sClasspath = XPathHelper.getStringValue(m_eConfigurationXML,
                                                           "//jreconfig/param/@value[starts-with(.,'-cp')]");

            if ((sClasspath != null) && (sClasspath.length() > 4))
            {
                sClasspath = sClasspath.substring(4).trim();

                // To determine Linux we'll do a simple test. If there are no back slashes AND no
                // semicolons then Linux is assumed.
                if (m_bIsLinux)
                {
                    m_sCustomClasspath = sClasspath.split("[:]");
                }
                else
                {
                    m_sCustomClasspath = sClasspath.split("[;]");
                }
            }
        }
        catch (Exception e)
        {
            LOG.warn("Error getting classpath", e);
        }

        // Now get all the normal JVM parameters
        try
        {
            NodeList nl = XPathHelper.selectNodeList(m_eConfigurationXML,
                                                     "//jreconfig/param/@value");
            ArrayList<String> alTemp = new ArrayList<String>();

            for (int iCount = 0; iCount < nl.getLength(); iCount++)
            {
                String sValue = nl.item(iCount).getNodeValue();

                if (!sValue.startsWith("-cp "))
                {
                    alTemp.add(sValue);
                }
            }

            m_asJVMOptions = alTemp.toArray(new String[0]);
        }
        catch (TransformerException e)
        {
            LOG.warn("Error getting JVM parameters", e);
        }

        // Now see if the processor is a Monitor-processor
        try
        {
            Node nNode = XPathHelper.selectSingleNode(m_eConfigurationXML,
                                                      "//configuration[@implementation=\"com.eibus.applicationconnector.monitor.Monitor\"]");

            if (nNode != null)
            {
                m_bIsMonitor = true;
            }
        }
        catch (TransformerException e)
        {
            LOG.warn("Error checking if the processor is a monitor soap processor", e);
        }
    }
}
