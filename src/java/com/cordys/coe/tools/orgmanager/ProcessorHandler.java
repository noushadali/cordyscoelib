package com.cordys.coe.tools.orgmanager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;

import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.cordys.coe.util.ObjectData;
import com.cordys.coe.util.cgc.CordysGatewayClientException;
import com.cordys.coe.util.cgc.CordysSOAPException;
import com.cordys.coe.util.cgc.ICordysGatewayClient;
import com.cordys.coe.util.general.Util;
import com.cordys.coe.util.general.ldap.LDAPUtils;
import com.cordys.coe.util.xml.dom.EmptyPrefixResolver;
import com.cordys.coe.util.xml.dom.XMLHelper;
import com.cordys.coe.util.xml.dom.XPathHelper;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;

/**
 * This class manages the actions for the given SOAP processors.
 * 
 * @author pgussow
 */
public class ProcessorHandler
{
    /**
     * Holds the logger that is used.
     */
    private static final Logger LOG = Logger.getLogger(ProcessorHandler.class);
    /**
     * Holds the list of monitors that are available within the cluster.
     */
    private LinkedHashMap<String, String> m_lhmMonitorProcessors = new LinkedHashMap<String, String>();
    /**
     * Holds the connection to the Cordys system.
     */
    private ICordysGatewayClient m_cgcClient;
    /**
     * Holds the prefix resolver to use.
     */
    private EmptyPrefixResolver m_epr;
    /**
     * Holds the monitor interval.
     */
    private int m_iInterval;
    /**
     * Holds all SOAP processors and organizations.
     */
    private LinkedHashMap<String, LinkedHashMap<String, Processor>> m_lhmOrgsAndProcessors = new LinkedHashMap<String, LinkedHashMap<String, Processor>>();
    /**
     * Holds the callback interface.
     */
    private IProcessHandlerCallback m_phcCallBack;
    /**
     * Holds the thread which updates the statuses.
     */
    private StatusUpdater m_tUpdater;

    /**
     * Creates a new ProcessorHandler object.
     * 
     * @param cgcClient Holds the client to use.
     * @param iInterval Holds the refresh interval.
     * @param phcCallBack The callback listener.
     * @throws CordysGatewayClientException In case of any exceptions.
     * @throws CordysSOAPException In case of any SOAp related exceptions.
     * @throws TransformerException In case of any XML related exceptions.
     */
    public ProcessorHandler(ICordysGatewayClient cgcClient, int iInterval, IProcessHandlerCallback phcCallBack)
            throws CordysGatewayClientException, CordysSOAPException, TransformerException
    {
        m_cgcClient = cgcClient;
        m_iInterval = iInterval;
        m_phcCallBack = phcCallBack;

        m_epr = new EmptyPrefixResolver();
        m_epr.addNamespacePrefixBinding("mon", "http://schemas.cordys.com/1.0/monitor");

        rebuildList();
    }

    /**
     * This method gets the current list of processors.
     * 
     * @return The current list of processors.
     */
    public LinkedHashMap<String, LinkedHashMap<String, Processor>> getAllProcessors()
    {
        return new LinkedHashMap<String, LinkedHashMap<String, Processor>>(m_lhmOrgsAndProcessors);
    }

    /**
     * This method gets the organizations available.
     * 
     * @return The organizations available.
     * @throws CordysGatewayClientException In case of any exceptions.
     */
    public ArrayList<ObjectData<LDAPEntry>> getOrganizations() throws CordysGatewayClientException
    {
        ArrayList<ObjectData<LDAPEntry>> alReturn = new ArrayList<ObjectData<LDAPEntry>>();

        for (String sOrgDN : m_lhmOrgsAndProcessors.keySet())
        {
            LDAPEntry le = m_cgcClient.readLDAPEntry(sOrgDN);
            alReturn.add(new ObjectData<LDAPEntry>(LDAPUtils.getAttrValue(le, "description"), le));
        }

        return alReturn;
    }

    /**
     * This method gets the processors and their status.
     * 
     * @return The processors and their status.
     */
    public ArrayList<Processor> getProcessors()
    {
        ArrayList<Processor> alReturn = new ArrayList<Processor>();

        for (String sOrganization : m_lhmOrgsAndProcessors.keySet())
        {
            LinkedHashMap<String, Processor> lhmProcs = m_lhmOrgsAndProcessors.get(sOrganization);

            for (Processor pProcessor : lhmProcs.values())
            {
                alReturn.add(pProcessor);
            }
        }

        return alReturn;
    }

    /**
     * This method rebuilds the full list from LDAP.
     * 
     * @throws CordysGatewayClientException In case of any errors.
     * @throws CordysSOAPException In case of any SOAP related exceptions.
     * @throws TransformerException In case of any XML related exceptions.
     */
    public void rebuildList() throws CordysGatewayClientException, CordysSOAPException, TransformerException
    {
        if (m_tUpdater != null)
        {
            m_tUpdater.setStop(true);
            m_tUpdater.interrupt();

            try
            {
                m_tUpdater.join();
            }
            catch (InterruptedException e)
            {
                LOG.warn("Ignoring interrupt exception", e);
            }
        }

        m_lhmOrgsAndProcessors.clear();

        LDAPEntry[] aeOrgs = m_cgcClient.searchLDAP(m_cgcClient.getSearchRoot(), LDAPConnection.SCOPE_SUB,
                "(objectclass=organization)");

        // First we'll do the system organization
        for (LDAPEntry le : aeOrgs)
        {
            if (le.getDN().startsWith("o=system,"))
            {
                LinkedHashMap<String, Processor> lhmProcessors = new LinkedHashMap<String, Processor>();
                m_lhmOrgsAndProcessors.put(le.getDN(), lhmProcessors);

                addSoapProcessors(le.getDN(), lhmProcessors);
                break;
            }
        }

        for (LDAPEntry leOrganization : aeOrgs)
        {
            // Now get the SOAP processor definitions for all the SOAP processors.
            if (!leOrganization.getDN().startsWith("o=system,"))
            {
                LinkedHashMap<String, Processor> lhmProcessors = new LinkedHashMap<String, Processor>();
                m_lhmOrgsAndProcessors.put(leOrganization.getDN(), lhmProcessors);

                addSoapProcessors(leOrganization.getDN(), lhmProcessors);
            }
        }

        // Also update the runtime status of the SOAP processor.
        refreshStatus();

        m_tUpdater = new StatusUpdater(m_iInterval);
        m_tUpdater.start();
    }

    /**
     * This method refreshes the runtime information for a specific SOAP processor.
     * 
     * @throws CordysGatewayClientException In case of any exceptions.
     * @throws CordysSOAPException In case of any SOAP related exceptions.
     * @throws TransformerException In case of any XML related exceptions.
     */
    public void refreshStatus() throws CordysGatewayClientException, CordysSOAPException, TransformerException
    {
        // TODO: If we run in a cluster we need to send the list request to each and every monitor
        // of the cluster in order to get all the statusses.
        for (String sDN : m_lhmMonitorProcessors.values())
        {
            Element eMessage = m_cgcClient.createMessage("List", "http://schemas.cordys.com/1.0/monitor");
            try
            {
                Element eResponse = m_cgcClient.requestFromCordys(eMessage.getOwnerDocument().getDocumentElement(), sDN);

                // Find all processors.
                NodeList nlProcesses = XPathHelper.selectNodeList(eResponse, "//mon:tuple/mon:old/mon:workerprocess", m_epr);

                for (int iCount = 0; iCount < nlProcesses.getLength(); iCount++)
                {
                    Node nWorkerProcess = nlProcesses.item(iCount);

                    String sName = XPathHelper.getStringValue(nWorkerProcess, "mon:name/text()", m_epr);
                    Matcher m = java.util.regex.Pattern.compile("cn=([^,]+),cn=([^,]+),cn=soap nodes,(o=.+)").matcher(sName);
                    m.find();

                    String sOrganization = m.group(3);

                    LinkedHashMap<String, Processor> lhmOrg = m_lhmOrgsAndProcessors.get(sOrganization);

                    if (lhmOrg == null)
                    {
                        throw new RuntimeException("Could not find list for organization " + sOrganization);
                    }

                    Processor pProcessor = lhmOrg.get(sName);

                    if (pProcessor == null)
                    {
                        throw new RuntimeException("Could not find entry for processor " + sName);
                    }

                    pProcessor.setProcessID(XPathHelper.getIntegerValue(nWorkerProcess, "mon:process-id/text()", m_epr, -1));
                    pProcessor.setStatus(XPathHelper.getStringValue(nWorkerProcess, "mon:status/text()", m_epr, "Stopped"));
                    pProcessor
                            .setTotalNomMemory(XPathHelper.getLongValue(nWorkerProcess, "mon:totalNOMMemory/text()", m_epr, -1));
                    pProcessor.setTotalNOMNodes(XPathHelper.getLongValue(nWorkerProcess, "mon:totalNOMNodesMemory/text()", m_epr,
                            -1));
                    pProcessor.setTotalCpuTime(XPathHelper.getStringValue(nWorkerProcess, "mon:totalCpuTime/text()", m_epr,
                            "00:00:00"));
                    pProcessor.setVirtualMemoryUsage(XPathHelper.getLongValue(nWorkerProcess, "mon:virtualMemoryUsage/text()",
                            m_epr, -1));
                    pProcessor.setResidentMemoryUsage(XPathHelper.getLongValue(nWorkerProcess, "mon:residentMemoryUsage/text()",
                            m_epr, -1));
                    pProcessor.setGUID(XPathHelper.getStringValue(nWorkerProcess, "mon:guid/text()", m_epr, ""));

                    pProcessor.setTotalSOAPDocumentsProcessed(XPathHelper.getLongValue(nWorkerProcess, "mon:busdocs/text()",
                            m_epr, -1));
                    pProcessor.setTotalProcessingTime(XPathHelper.getLongValue(nWorkerProcess, "mon:processing-time/text()",
                            m_epr, -1));
                    pProcessor.setLastProcessingTime(XPathHelper.getLongValue(nWorkerProcess, "mon:last-time/text()", m_epr, -1));

                    pProcessor.setErrorDetails(XPathHelper.getStringValue(nWorkerProcess, "mon:error/text()", m_epr, ""));
                }
            }
            catch (Exception e)
            {
                System.err.println("Cannot get status from monitor '" + sDN + "':\n" + Util.getStackTrace(e));
            }
        }

        // Call the callback
        m_phcCallBack.onStatusUpdate(new LinkedHashMap<String, LinkedHashMap<String, Processor>>(m_lhmOrgsAndProcessors));
    }

    /**
     * This method will reset the processor with the given DN.
     * 
     * @param pProcessor The processor to reset.
     * @throws CordysGatewayClientException In case of any gateway exceptions.
     * @throws CordysSOAPException In case of any SOAP faults.
     */
    public void resetProcessor(Processor pProcessor) throws CordysGatewayClientException, CordysSOAPException
    {
        doProcessorAction("Reset", pProcessor);
    }

    /**
     * This method will restart the processor with the given DN.
     * 
     * @param pProcessor The processor to restart.
     * @throws CordysGatewayClientException In case of any gateway exceptions.
     * @throws CordysSOAPException In case of any SOAP faults.
     */
    public void restartProcessor(Processor pProcessor) throws CordysGatewayClientException, CordysSOAPException
    {
        doProcessorAction("Restart", pProcessor);
    }

    /**
     * This method sets the new refresh interval for the updater.
     * 
     * @param iRefreshInterval the new refresh interval.
     */
    public void setRefreshInterval(int iRefreshInterval)
    {
        m_iInterval = iRefreshInterval;

        if (m_tUpdater != null)
        {
            m_tUpdater.setRefreshInterval(iRefreshInterval);
            m_tUpdater.interrupt();
        }
        else
        {
            m_tUpdater = new StatusUpdater(m_iInterval);
            m_tUpdater.start();
        }
    }

    /**
     * This method starts a full organization.
     * 
     * @param sOrganization The organization to start.
     * @param bIncludeManual Whether or not to include the non-automatics in the action list.
     * @throws CordysGatewayClientException In case of any gateway exceptions.
     * @throws CordysSOAPException In case of any SOAP faults.
     */
    public void startOrganization(String sOrganization, boolean bIncludeManual) throws CordysGatewayClientException,
            CordysSOAPException
    {
        doFullOrgAction("Start", sOrganization, bIncludeManual);
    }

    /**
     * This method will start the processor with the given DN.
     * 
     * @param pProcessor The processor to start.
     * @throws CordysGatewayClientException In case of any gateway exceptions.
     * @throws CordysSOAPException In case of any SOAP faults.
     */
    public void startProcessor(Processor pProcessor) throws CordysGatewayClientException, CordysSOAPException
    {
        doProcessorAction("Start", pProcessor);
    }

    /**
     * This method stops a full organization.
     * 
     * @param sOrganization The organization to stop.
     * @param bIncludeManual Whether or not to include the non-automatics in the action list.
     * @throws CordysGatewayClientException In case of any gateway exceptions.
     * @throws CordysSOAPException In case of any SOAP faults.
     */
    public void stopOrganization(String sOrganization, boolean bIncludeManual) throws CordysGatewayClientException,
            CordysSOAPException
    {
        if (sOrganization == null)
        {
            throw new RuntimeException(
                    "You cannot stop the whole Cordys installation using this tool.\nYou need to stop the monitor service on the actual machine.");
        }
        else if ((sOrganization != null) && sOrganization.startsWith("o=system"))
        {
            throw new RuntimeException(
                    "Stopping the system organization is not supported since it will lead to unpredictable results.");
        }

        doFullOrgAction("Stop", sOrganization, bIncludeManual);
    }

    /**
     * This method will stop the processor with the given DN.
     * 
     * @param pProcessor The processor to start.
     * @throws CordysGatewayClientException In case of any gateway exceptions.
     * @throws CordysSOAPException In case of any SOAP faults.
     */
    public void stopProcessor(Processor pProcessor) throws CordysGatewayClientException, CordysSOAPException
    {
        doProcessorAction("Stop", pProcessor);
    }

    /**
     * This method queries LDAP for all the SOAP processors that are defined.
     * 
     * @param sDN The current organization.
     * @param lhmProcessors The list in which to add all processors.
     * @throws CordysGatewayClientException In case of any exceptions.
     */
    private void addSoapProcessors(String sDN, LinkedHashMap<String, Processor> lhmProcessors)
            throws CordysGatewayClientException
    {
        String sSearchFilter = "(objectclass=bussoapprocessor)";
        LDAPEntry[] aleSPs = m_cgcClient.searchLDAP(sDN, LDAPConnection.SCOPE_SUB, sSearchFilter);

        for (LDAPEntry leSoapProcessor : aleSPs)
        {
            Processor pNew = new Processor(leSoapProcessor);
            lhmProcessors.put(pNew.getDN(), pNew);

            // If the processor is a monitor we need to add it to the list.
            if (pNew.isMonitor())
            {
                m_lhmMonitorProcessors.put(pNew.getComputer(), pNew.getDN());
            }
        }
    }

    /**
     * This method does the action for the full organization.
     * 
     * @param sAction The action to do (Stop or Start).
     * @param sOrganization The organization to stop/start. If left null the action will be done for all organizations.
     * @param bIncludeManual Whether or not to include the non-automatics in the action list.
     * @throws CordysGatewayClientException In case of any connection related exceptions.
     * @throws CordysSOAPException In case of any SOAP faults.
     */
    private void doFullOrgAction(String sAction, String sOrganization, boolean bIncludeManual)
            throws CordysGatewayClientException, CordysSOAPException
    {
        ArrayList<Processor> alToExecute = getProcessors();

        for (Processor pProcessor : alToExecute)
        {
            if (((sOrganization == null) || pProcessor.getOrganization().equals(sOrganization)))
            {
                if ((pProcessor.startsAutomatically() == true) || (pProcessor.startsAutomatically() && (bIncludeManual == true)))
                {
                    if ("Start".equals(sAction) && !"Started".equals(pProcessor.getStatus()))
                    {
                        // Only start if the processor is not started.
                        doProcessorAction(sAction, pProcessor);
                    }
                    else if ("Stop".equals(sAction) && "Started".equals(pProcessor.getStatus()))
                    {
                        // Only stop if the processor is currently started.
                        doProcessorAction(sAction, pProcessor);
                    }
                }
            }
        }
    }

    /**
     * This method does the appropriate action of the SP: Start, Stop, Reset, Restart.
     * 
     * @param sName The name of the action.
     * @param pProcessor sDN The DN of the SOAP processor.
     * @throws CordysGatewayClientException In case of any gateway exceptions.
     * @throws CordysSOAPException In case of any SOAP faults.
     */
    private void doProcessorAction(final String sName, Processor pProcessor) throws CordysGatewayClientException,
            CordysSOAPException
    {
        final Element eMessage = m_cgcClient.createMessage(sName, "http://schemas.cordys.com/1.0/monitor");
        final String sMonitor = m_lhmMonitorProcessors.get(pProcessor.getComputer());

        XMLHelper.createTextElementWithParentNS("dn", pProcessor.getDN(), eMessage);

        // Run the request in a different thread, since we don't need the response
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                try
                {
                    m_cgcClient.requestFromCordys(eMessage.getOwnerDocument().getDocumentElement(), sMonitor);
                }
                catch (Exception e)
                {
                    LOG.warn("Error executing action " + sName, e);
                }
            }
        }).start();
    }

    /**
     * This class is used to automatically update the status of the SOAP processors.
     * 
     * @author pgussow
     */
    private class StatusUpdater extends Thread
    {
        /**
         * Holds whether or not the thread should stop.
         */
        private boolean m_bStop;
        /**
         * Holds the interval to use.
         */
        private int m_iInterval;

        /**
         * Creates the updater thread.
         * 
         * @param iInterval The interval to use between list requests.
         */
        public StatusUpdater(int iInterval)
        {
            m_iInterval = iInterval;

            if (m_iInterval <= 0)
            {
                m_iInterval = 15000;
            }
        }

        /**
         * This method will send the SOAP call to update the runtime status.
         * 
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            while (!m_bStop)
            {
                try
                {
                    Thread.sleep(m_iInterval * 1000);
                }
                catch (InterruptedException e)
                {
                    // Ignore it.
                }

                if (!m_bStop)
                {
                    try
                    {
                        ProcessorHandler.this.refreshStatus();
                    }
                    catch (Exception e)
                    {
                        LOG.error("Error refreshing status", e);
                    }
                }
            }
        }

        /**
         * This method sets the refresh interval to use.
         * 
         * @param iRefreshInterval The new refresh interval.
         */
        public void setRefreshInterval(int iRefreshInterval)
        {
            m_iInterval = iRefreshInterval;
        }

        /**
         * This method sets whether or not the thread should stop.
         * 
         * @param bStop Whether or not the thread should stop.
         */
        public void setStop(boolean bStop)
        {
            m_bStop = bStop;
        }
    }
}
