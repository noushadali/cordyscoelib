package com.cordys.coe.tools.es.swt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.cordys.coe.tools.es.ESLogEvent;
import com.cordys.coe.tools.es.ILogEvent;
import com.cordys.coe.tools.es.Log4JLogEvent;
import com.cordys.coe.tools.es.ReceivedMessage;
import com.cordys.coe.tools.log4j.ILogViewerConfiguration;
import com.cordys.coe.util.general.Util;
import com.cordys.coe.util.soap.ISOAPWrapper;
import com.cordys.coe.util.soap.SOAPWrapper;
import com.cordys.coe.util.swt.BorderLayout;
import com.cordys.coe.util.swt.InputBox;
import com.cordys.coe.util.xml.nom.XPathHelper;
import com.eibus.connector.nom.Connector;
import com.eibus.connector.nom.SOAPMessageListener;
import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Node;

/**
 * This composite is the base for the Event Service client.
 * 
 * @author pgussow
 */
public class ESCComposite extends Composite implements SOAPMessageListener, IMessageHandler, IEventServiceClient
{
    /**
     * Identifies the image for opening Cordys logs.
     */
    private static final String IMG_OPEN_CORDYS = "opencordys";
    /**
     * Holds the max filesize after which the paging will kick in.
     */
    private static final long MAX_FILESIZE = 1024 * 1024 * 5;
    /**
     * Identifies the image for saving.
     */
    private static final String IMG_SAVE = "saveas";
    /**
     * Identifies the image for opening Log4J logs.
     */
    private static final String IMG_OPEN_LOG4J = "openlog4j";
    /**
     * Identifies the image for adding a new Socket lsitener.
     */
    private static final String IMG_NEW_LISTENER = "newlistener";
    /**
     * Holds the subject for the Log4J events.
     */
    private static final String SUB_LOG4J_EVENTS = "Log4JEvents";
    /**
     * Holds the default port for the receiver.
     */
    private static final int DEFAULT_PORT = 4445;
    /**
     * Holds the image registry for this application.
     */
    private static ImageRegistry s_irImages = null;
    /**
     * Holds the document to use for creating XML nodes.
     */
    private static Document s_dDoc = new Document();
    /**
     * The button for subscribing to a subject.
     */
    private Button m_bSubscribe;
    /**
     * The button for unsubscribing to a subject.
     */
    private Button m_bUnsubscribe;
    /**
     * TextField for entering the subject to subscribe/unsubscribe.
     */
    private Combo m_cbSubject;
    /**
     * Holds the composite showing all the details.
     */
    private DetailsComposite m_dcDetailsComposite;
    /**
     * Holds the log4j receiver.
     */
    private HashMap<String, Log4JReceiver> m_hmLog4JReceivers = new HashMap<String, Log4JReceiver>();
    /**
     * Holds all the categories currently subscribed to.
     */
    private HashMap<String, AbstractEventPanel> m_hmSubscribed = new HashMap<String, AbstractEventPanel>();
    /**
     * Holds the configuration.
     */
    private ILogViewerConfiguration m_lvcConfig;
    /**
     * This SashForm is the main one. It separates the subject edit and the rest.
     */
    private SashForm m_sfMain;
    /**
     * The Sashform between the subjects and the events.
     */
    private SashForm m_sfSubjectEvent;
    /**
     * Holds the NDC for the log level.
     */
    private StyledText m_stNDC;
    /**
     * Holds the SOAP-wrapper to use.
     */
    private ISOAPWrapper m_swSoap;
    /**
     * Table that holds all the currently subscribed subjects.
     */
    private Table m_tblSubjects;
    /**
     * Holds the category for the selected event.
     */
    private Text m_tCategory;
    /**
     * Holds the host for the selected event.
     */
    private Text m_tHost;
    /**
     * Holds the PID for the selected event.
     */
    private Text m_tPID;
    /**
     * Holds the tabbed pane with the events.
     */
    private CTabFolder m_tpTabEvents;
    /**
     * Holds the thread for the selected event.
     */
    private Text m_tThread;
    /**
     * Holds the time for the selected event.
     */
    private Text m_tTime;
    /**
     * Holds the trace level for the selected event.
     */
    private Text m_tTraceLevel;

    /**
     * Creates a new ESCComposite object.
     * 
     * @param cParent The parent composite.
     * @param cConnector The Cordys connector to use.
     * @throws IOException DOCUMENTME
     */
    public ESCComposite(Composite cParent, Connector cConnector) throws IOException
    {
        super(cParent, SWT.NONE);

        // Load the configuration
        m_lvcConfig = new ESCConfiguration(ESCComposite.class.getResourceAsStream("esc.properties"));

        setLayout(new FillLayout(SWT.FILL));

        createContents();

        startLog4JListening();

        // Listen for events.
        if (cConnector != null)
        {
            cConnector.getListenerPool().setDefaultListener(this);
            m_swSoap = new SOAPWrapper(cConnector);

            fillSubjectCombo();
        }
        else
        {
            // Disable the subscribe and unsunscribe buttons.
            m_bSubscribe.setEnabled(false);
            m_bUnsubscribe.setEnabled(false);
        }
    }

    /**
     * @see com.cordys.coe.tools.log4j.ILog4JComposite#clearCurrentView()
     */
    @Override
    public void clearCurrentView()
    {
        // Clean all the panels that are shown.
        for (Iterator<String> iPanels = m_hmSubscribed.keySet().iterator(); iPanels.hasNext();)
        {
            AbstractEventPanel aepPanel = m_hmSubscribed.get(iPanels.next());
            aepPanel.clear();
        }

        // Clear the details.
        if (m_dcDetailsComposite != null)
        {
            m_dcDetailsComposite.clearFields();
        }

        clearDetails();
    }

    /**
     * When the shell is resized try to keep the proportions.
     * 
     * @param ceEvent The event that took place.
     */
    public void calculateNewSizes(ControlEvent ceEvent)
    {
        Point pActualSize = getShell().getSize();

        // The upper sashform, we'll set a new weight for it.
        if (m_sfMain != null)
        {
            m_sfMain.setWeights(new int[] { 35, (pActualSize.y - 35) });
        }

        if (m_sfSubjectEvent != null)
        {
            m_sfSubjectEvent.setWeights(new int[] { 113, pActualSize.x - 113 });
        }
    }

    /**
     * This method clears the details.
     */
    public void clearDetails()
    {
        m_tTime.setText("");
        m_tCategory.setText("");
        m_tTraceLevel.setText("");
        m_tThread.setText("");
        m_tPID.setText("");
        m_tHost.setText("");
        m_stNDC.setText("");
    }

    /**
     * This method closes the connections.
     */
    public void closeConnections()
    {
        if (m_hmLog4JReceivers.size() > 0)
        {
            for (Iterator<Log4JReceiver> iListeners = m_hmLog4JReceivers.values().iterator(); iListeners.hasNext();)
            {
                Log4JReceiver lrReceiver = iListeners.next();
                lrReceiver.setShouldStop(true);
            }
        }
    }

    /**
     * This method retrieves the list of current subjects from the event service. All subjects are added to the combo box.
     */
    public void fillSubjectCombo()
    {
        int iEnvelope = 0;
        int iResponse = 0;

        try
        {
            int iMethod = m_swSoap.createSoapMethod("GetAllSubscriptions", "http://schemas.cordys.com/1.0/eventservice");
            iEnvelope = SOAPWrapper.getEnvelope(iMethod);

            iResponse = m_swSoap.sendAndWait(iEnvelope);

            int[] aiSubjects = XPathHelper.selectNodes(iResponse, "//GetAllSubscriptionsResponse/subject");

            for (int iCount = 0; iCount < aiSubjects.length; iCount++)
            {
                m_cbSubject.add(Node.getAttribute(aiSubjects[iCount], "name"));
            }
        }
        catch (Exception e)
        {
            // Ignore exceptions
        }
        finally
        {
            if (iEnvelope != 0)
            {
                Node.delete(iEnvelope);
            }

            if (iResponse != 0)
            {
                Node.delete(iResponse);
            }
        }
    }

    /**
     * This method gets the configuration for the Event Service Client.
     * 
     * @return The configuration for the Event Service Client.
     */
    public ILogViewerConfiguration getConfiguration()
    {
        return m_lvcConfig;
    }

    /**
     * This method gets the image registry for this client.
     * 
     * @return The image registry for this client.
     */
    public ImageRegistry getOwnImageRegistry()
    {
        if (s_irImages == null)
        {
            registerImages();
        }
        return s_irImages;
    }

    /**
     * This method returns the title for this panel.
     * 
     * @return The title.
     */
    public String getTitle()
    {
        StringBuffer sbReturn = new StringBuffer(255);

        if ((m_swSoap != null) && (m_swSoap.getConnector() != null))
        {
            sbReturn.append(" (");
            sbReturn.append(m_swSoap.getConnector().getMiddleware().getDirectory().getConnection().getHost());
            sbReturn.append(") ");
        }

        if ((m_hmLog4JReceivers != null) && (m_hmLog4JReceivers.size() > 0))
        {
            sbReturn.append(" Log4J ports: ");

            for (Iterator<String> iPorts = m_hmLog4JReceivers.keySet().iterator(); iPorts.hasNext();)
            {
                String sPort = iPorts.next();
                sbReturn.append(sPort);

                if (iPorts.hasNext())
                {
                    sbReturn.append(", ");
                }
            }
        }
        return sbReturn.toString();
    }

    /**
     * This method handles the messages that are received from the Log4J messages.
     * 
     * @param sName DOCUMENTME
     * @param edDetails The event details.
     */
    public void handleMessage(String sName, EventDetails edDetails)
    {
        if (edDetails != null)
        {
            Log4JEventsPanel lepPanel = (Log4JEventsPanel) m_hmSubscribed.get(SUB_LOG4J_EVENTS);

            if (lepPanel != null)
            {
                Log4JLogEvent lleEvent = new Log4JLogEvent(edDetails);
                lepPanel.onReceive(lleEvent);
            }
        }
    }

    /**
     * This method loads the LogEvents from a file.
     * 
     * @param iFileType
     */
    public void loadFromFile(int iFileType)
    {
        FileDialog fcFile = new FileDialog(getShell(), SWT.OPEN);
        String sFilename = fcFile.open();

        if ((sFilename != null) && (sFilename.length() > 0))
        {
            BufferedReader brIn = null;

            try
            {
                // Check if the file exists.
                File fTemp = new File(sFilename);

                if (!fTemp.exists())
                {
                    throw new Exception("File " + fTemp.getAbsolutePath() + " does not exist.");
                }

                boolean bOK = true;

                // Check the filesize.
                if (fTemp.length() > MAX_FILESIZE)
                {
                    MessageBox mbPaging = new MessageBox(getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
                    mbPaging.setText("Confirmation");
                    mbPaging.setMessage("The file is bigger then " + MAX_FILESIZE
                            + " bytes. Do you want to use paging (which is recommended)?");

                    if (mbPaging.open() == SWT.YES)
                    {
                        ILogContentProvider lcpProvider = LogContentFactory.createLog4JFileProviderByRecordCount(
                                fTemp.getAbsolutePath(), m_lvcConfig.getPageSize());
                        addSubscribedSubject("Log4JEvents", lcpProvider);
                        bOK = false;
                    }
                }

                if (bOK == true)
                {
                    // Add the panel
                    String sRootTag = "";
                    String sEndRootTag = "";

                    String sSourceName = fTemp.getName();

                    if (iFileType == TYPE_CORDYS_SPY)
                    {
                        addSubscribedSubject("LogEvent");
                        sRootTag = "<LogEvent";
                        sEndRootTag = "</LogEvent>";
                    }
                    else if (iFileType == TYPE_LOG4J)
                    {
                        addSubscribedSubject("Log4JEvents");
                        sRootTag = "<log4j:event";
                        sEndRootTag = "</log4j:event>";
                    }
                    else if (iFileType == TYPE_CORDYS_LOG4J)
                    {
                        addSubscribedSubject("Log4JEvents");
                        sRootTag = "<LogMessage";
                        sEndRootTag = "</LogMessage>";
                    }

                    brIn = new BufferedReader(new FileReader(fTemp));

                    String sBuffer = brIn.readLine();
                    int iCurrentStartPos = -1;
                    int iEndPos = -1;
                    StringBuffer sbCurrentMessage = null;

                    while (sBuffer != null)
                    {
                        if (iCurrentStartPos == -1)
                        {
                            iCurrentStartPos = sBuffer.indexOf(sRootTag);
                        }
                        iEndPos = sBuffer.indexOf(sEndRootTag);

                        if ((iCurrentStartPos > -1) && (iEndPos == -1) && (sbCurrentMessage == null))
                        {
                            // Found a start-entry without an end-entry
                            String sTemp = sBuffer.substring(iCurrentStartPos);
                            sbCurrentMessage = new StringBuffer();
                            sbCurrentMessage.append(sTemp);
                            sbCurrentMessage.append(System.getProperty("line.separator"));
                        }
                        else if ((sbCurrentMessage != null) && (iEndPos == -1))
                        {
                            sbCurrentMessage.append(sBuffer);
                            sbCurrentMessage.append(System.getProperty("line.separator"));
                        }
                        else if ((sbCurrentMessage != null) && (iEndPos > -1))
                        {
                            String sTemp = sBuffer.substring(iCurrentStartPos, iEndPos + sEndRootTag.length());
                            sbCurrentMessage.append(sTemp);
                            sbCurrentMessage.append(System.getProperty("line.separator"));

                            // Found the full message
                            if (iFileType == TYPE_CORDYS_SPY)
                            {
                                addMessage(sbCurrentMessage.toString());
                            }
                            else if (iFileType == TYPE_LOG4J)
                            {
                                EventDetails ed = EventDetails.createInstanceFromLog4JXML(sbCurrentMessage.toString());
                                handleMessage(sSourceName, ed);
                            }
                            else if (iFileType == TYPE_CORDYS_LOG4J)
                            {
                                EventDetails ed = EventDetails.createInstanceFromCordysLog4JXML(sbCurrentMessage.toString());
                                handleMessage(sSourceName, ed);
                            }
                            sbCurrentMessage = null;
                        }
                        sBuffer = brIn.readLine();
                    }
                }
            }
            catch (Exception e)
            {
                showError("Error: " + e.getMessage());
            }
            finally
            {
                try
                {
                    if (brIn != null)
                    {
                        brIn.close();
                    }
                }
                catch (Exception e)
                {
                    // Ignore it.
                }
            }
        }
    }

    /**
     * This methos gets called when a message is received by the connector.
     * 
     * @param iMSG THe message that was received.
     * @return Always false. This application will take care of Node-deletion.
     */
    public boolean onReceive(int iMSG)
    {
        ReceivedMessage rmMessage = new ReceivedMessage(iMSG);

        try
        {
            if (m_hmSubscribed.containsKey(rmMessage.getSubject()))
            {
                // Get the appropiate panel from the hashtable.
                AbstractEventPanel aepPanel = m_hmSubscribed.get(rmMessage.getSubject());
                ILogEvent leEvent = new ESLogEvent(rmMessage);
                aepPanel.onReceive(leEvent);
            }
            else
            {
                // Ignore the message
                rmMessage.cleanUp();
            }
        }
        catch (Exception e)
        {
            // In case of an exception, clean it up.
            rmMessage.cleanUp();
        }

        return false;
    }

    /**
     * This method saves the logging of the log4J to the designated file.
     */
    public void saveLog4JLog()
    {
        FileDialog fcFile = new FileDialog(getShell(), SWT.SAVE);
        fcFile.setFilterExtensions(new String[] { "*.xml", "*.*" });
        fcFile.setFilterNames(new String[] { "Log4J xml file (*.xml)", "All files (*.*)" });
        fcFile.setFileName("log4jlog.xml");

        String sFilename = fcFile.open();

        if ((sFilename != null) && (sFilename.length() > 0))
        {
            // Check if the file exists.
            File fTemp = new File(sFilename);

            boolean bSave = false;

            if (fTemp.exists())
            {
                MessageBox mbQuestion = new MessageBox(getShell(), SWT.YES | SWT.NO);
                mbQuestion.setText("Confirmation");
                mbQuestion.setMessage("The file " + sFilename + " already exists. Do you want to overwrite it?");

                int iResult = mbQuestion.open();

                if (iResult == SWT.YES)
                {
                    bSave = true;
                }
            }
            else
            {
                bSave = true;
            }

            if (bSave == true)
            {
                // Now we have the logfile. We need to create the XML for these entries.
                if (!m_hmSubscribed.containsKey(SUB_LOG4J_EVENTS))
                {
                    showError("There are no Log4J listeners subscribed.");
                }

                Log4JEventsPanel lep = (Log4JEventsPanel) m_hmSubscribed.get(SUB_LOG4J_EVENTS);

                if (lep != null)
                {
                    try
                    {
                        lep.saveToFile(sFilename);
                        showInformation("File " + sFilename + " saved sucessfully.");
                    }
                    catch (IOException e)
                    {
                        showError("Error saving the logmessages\n" + e);
                    }
                }
            }
        }
    }

    /**
     * This method sets the text of the details.
     * 
     * @param leEvent The logevent to display.
     */
    public synchronized void setDetailedText(final ILogEvent leEvent)
    {
        // Since this method can be called by different threads, we need to go to the UI thread.
        Display dTemp = getShell().getDisplay();

        if ((dTemp != null) && !dTemp.isDisposed())
        {
            dTemp.asyncExec(new Runnable() {
                public void run()
                {
                    if ((leEvent == null) || !(leEvent instanceof Log4JLogEvent))
                    {
                        m_dcDetailsComposite.setInformation(leEvent.getMessage(), "", "");
                    }
                    else
                    {
                        Log4JLogEvent lleEvent = (Log4JLogEvent) leEvent;
                        m_dcDetailsComposite.setInformation(lleEvent.getMessage(), lleEvent.getException(),
                                lleEvent.getLocationInformation());
                    }

                    String sTemp = "";

                    // Time
                    try
                    {
                        SimpleDateFormat sdf = new SimpleDateFormat(getConfiguration().getDateFormat());
                        sTemp = sdf.format(leEvent.getTime());
                    }
                    catch (Exception e)
                    {
                        sTemp = leEvent.getTime().toString();
                    }
                    m_tTime.setText(sTemp);

                    // Host
                    m_tHost.setText(leEvent.getHost());

                    // PID
                    m_tPID.setText(leEvent.getPID());

                    // Category
                    m_tCategory.setText(leEvent.getCategory());

                    // Thread
                    m_tThread.setText(leEvent.getThread());

                    // Level
                    m_tTraceLevel.setText(leEvent.getTraceLevel());

                    // Set the NDC
                    m_stNDC.setText(leEvent.getNDC());
                }
            });
        }
    }

    /**
     * This method shows an error dialog to the end user.
     * 
     * @param sMessage the message to display.
     */
    public void showError(String sMessage)
    {
        MessageBox mb = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
        mb.setMessage(sMessage);
        mb.setText("Error");
        mb.open();
    }

    /**
     * This method shows an information dialog to the end user.
     * 
     * @param sMessage The message to display.
     */
    public void showInformation(String sMessage)
    {
        MessageBox mb = new MessageBox(getShell(), SWT.ICON_INFORMATION | SWT.OK);
        mb.setMessage(sMessage);
        mb.setText("Information");
        mb.open();
    }

    /**
     * This method starts the socket reader to read the Log4J messages sent via the socketappender.
     * 
     * @return The portnumber used.
     */
    public String startLog4JListening()
    {
        String sReturn = "";

        // Start up the Log4J receiving thread.
        try
        {
            InputBox ib = new InputBox(getShell(), SWT.APPLICATION_MODAL, String.valueOf(DEFAULT_PORT));
            ib.setMessage("Enter a free portnumber for the log4j receiver:");
            ib.setText("Portnumber for Log4J listener.");

            sReturn = ib.open();

            Log4JReceiver lrReceiver = new Log4JReceiver(this, Integer.parseInt(sReturn), "Log4JEvents");
            addSubscribedSubject(SUB_LOG4J_EVENTS);
            lrReceiver.start();
            m_hmLog4JReceivers.put(sReturn, lrReceiver);
        }
        catch (IOException e1)
        {
            showError("Error starting Log4J: " + e1);
        }

        return sReturn;
    }

    /**
     * This method subscribes to the passed on subject.
     * 
     * @param sSubject The subject to subscribwe to.
     */
    public void subscribeToSubject(String sSubject)
    {
        int iRequest = 0;

        try
        {
            // Create the subscription-message.
            Connector cCon = m_swSoap.getConnector();
            iRequest = cCon.createSOAPMethod("http://schemas.cordys.com/1.0/eventservice", "Subscribe");

            Document dDoc = Node.getDocument(iRequest);
            dDoc.createTextElement("subject", sSubject, iRequest);
            dDoc.createTextElement("subscriber", cCon.getMiddleware().getAddress(), iRequest);

            cCon.send(Node.getParent(Node.getParent(iRequest)));

            addSubscribedSubject(sSubject);
        }
        catch (Exception e)
        {
            showError("Error subscribing to subject " + sSubject + "\r\n" + Util.getStackTrace(e));
        }
        finally
        {
            Node.delete(iRequest);
        }
    }

    /**
     * This method creates all the controls for the window.
     */
    protected void createContents()
    {
        // Create the coolbar and the rest of the page.
        Composite cMain = new Composite(this, SWT.NONE);
        cMain.setLayout(new BorderLayout());

        // Create the toolbar for this application.
        final CoolBar cbCoolBar = new CoolBar(cMain, SWT.NONE);
        cbCoolBar.setLayoutData(BorderLayout.NORTH);

        // Toolbar open
        final ToolBar tbToolbar = new ToolBar(cbCoolBar, SWT.FLAT);

        // Open Cordys Logfile.
        ToolItem tiItem = new ToolItem(tbToolbar, SWT.NULL);
        tiItem.setImage(getOwnImageRegistry().get(IMG_OPEN_CORDYS));
        tiItem.setToolTipText("Open a Cordys Spy XML logfile");

        tiItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0)
            {
                loadFromFile(TYPE_CORDYS_SPY);
            }
        });

        tiItem = new ToolItem(tbToolbar, SWT.NULL);
        tiItem.setImage(getOwnImageRegistry().get(IMG_OPEN_CORDYS));
        tiItem.setToolTipText("Open a Cordys Log4J XML logfile");

        tiItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0)
            {
                loadFromFile(TYPE_CORDYS_LOG4J);
            }
        });

        tiItem = new ToolItem(tbToolbar, SWT.NULL);
        tiItem.setImage(getOwnImageRegistry().get(IMG_OPEN_LOG4J));
        tiItem.setToolTipText("Open a Log4J XML logfile");

        tiItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0)
            {
                loadFromFile(TYPE_LOG4J);
            }
        });

        tiItem = new ToolItem(tbToolbar, SWT.NULL);
        tiItem.setImage(getOwnImageRegistry().get(IMG_NEW_LISTENER));
        tiItem.setToolTipText("Add a new SocketListener for Log4J events");

        tiItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0)
            {
                startLog4JListening();
            }
        });

        tiItem = new ToolItem(tbToolbar, SWT.NULL);
        tiItem.setImage(getOwnImageRegistry().get(IMG_SAVE));
        tiItem.setToolTipText("Save the current events as a Log4J XML logfile");

        tiItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0)
            {
                saveLog4JLog();
            }
        });

        // Add a coolItem to a coolBar
        CoolItem ciCoolItem = new CoolItem(cbCoolBar, SWT.NULL);
        ciCoolItem.setControl(tbToolbar);

        Point size = tbToolbar.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        Point pSize = ciCoolItem.computeSize(size.x, size.y);
        ciCoolItem.setSize(pSize);

        // Create the main splitter.
        m_sfMain = new SashForm(cMain, SWT.VERTICAL | SWT.SMOOTH);
        m_sfMain.setLayoutData(BorderLayout.CENTER);

        final Composite cTop = new Composite(m_sfMain, SWT.NONE);
        final GridLayout glTopFrame = new GridLayout();
        glTopFrame.numColumns = 5;
        cTop.setLayout(glTopFrame);

        final Label subjectLabel = new Label(cTop, SWT.NONE);
        subjectLabel.setLayoutData(new GridData());
        subjectLabel.setText("Subject:");

        m_cbSubject = new Combo(cTop, SWT.BORDER);
        m_cbSubject.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

        m_bSubscribe = new Button(cTop, SWT.NONE);
        m_bSubscribe.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e)
            {
                subscribeToSubject(m_cbSubject.getText());
            }
        });
        m_bSubscribe.setLayoutData(new GridData());
        m_bSubscribe.setText("&Subscribe");

        m_bUnsubscribe = new Button(cTop, SWT.NONE);
        m_bUnsubscribe.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e)
            {
                unsubscribeToSubject(m_cbSubject.getText());
            }
        });
        m_bUnsubscribe.setText("&Unsubscribe");

        final Button bClear = new Button(cTop, SWT.NONE);
        bClear.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e)
            {
                clearCurrentView();
            }
        });
        bClear.setText("&Clear");

        final Composite cBottomFrame = new Composite(m_sfMain, SWT.NONE);
        cBottomFrame.setLayout(new FillLayout());

        final SashForm sashForm_1 = new SashForm(cBottomFrame, SWT.VERTICAL | SWT.SMOOTH);

        final Composite cMiddleFrame = new Composite(sashForm_1, SWT.NONE);
        cMiddleFrame.setLayout(new FillLayout());

        m_sfSubjectEvent = new SashForm(cMiddleFrame, SWT.NONE | SWT.SMOOTH);
        m_sfSubjectEvent.setCapture(true);

        final Composite cSubjectFrame = new Composite(m_sfSubjectEvent, SWT.NONE);
        cSubjectFrame.setLayout(new FillLayout());

        m_tblSubjects = new Table(cSubjectFrame, SWT.BORDER);
        m_tblSubjects.setLinesVisible(true);
        m_tblSubjects.setHeaderVisible(true);

        final TableColumn newColumnTableColumn = new TableColumn(m_tblSubjects, SWT.NONE);
        newColumnTableColumn.setWidth(100);
        newColumnTableColumn.setText("Subjects");

        final Composite cEventFrame = new Composite(m_sfSubjectEvent, SWT.NONE);
        cEventFrame.setLayout(new FillLayout());

        m_tpTabEvents = new CTabFolder(cEventFrame, SWT.NONE);

        // Create the default log event panel.
        m_sfSubjectEvent.setWeights(new int[] { 113, 867 });

        final Composite cDetailFrame = new Composite(sashForm_1, SWT.NONE);
        cDetailFrame.setLayout(new FillLayout());

        final CTabFolder tfDetails = new CTabFolder(cDetailFrame, SWT.NONE);

        final CTabItem tiMessage = new CTabItem(tfDetails, SWT.NONE);
        tiMessage.setText("Message");

        final CTabItem tiDetails = new CTabItem(tfDetails, SWT.NONE);
        tiDetails.setText("Details");

        final Composite cDetails = new Composite(tfDetails, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        cDetails.setLayout(gridLayout);
        tiDetails.setControl(cDetails);

        final Label label = new Label(cDetails, SWT.NONE);
        label.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
        label.setText("Time:");

        m_tTime = new Text(cDetails, SWT.BORDER);

        final GridData gridData_3 = new GridData(GridData.FILL, GridData.CENTER, false, false);
        gridData_3.widthHint = 130;
        m_tTime.setLayoutData(gridData_3);

        final Label label_1 = new Label(cDetails, SWT.NONE);
        label_1.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
        label_1.setText("Host:");

        m_tHost = new Text(cDetails, SWT.BORDER);

        final GridData gridData_2 = new GridData(GridData.FILL, GridData.CENTER, false, false);
        gridData_2.widthHint = 141;
        m_tHost.setLayoutData(gridData_2);

        final Label label_2 = new Label(cDetails, SWT.NONE);
        label_2.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
        label_2.setText("PID:");

        m_tPID = new Text(cDetails, SWT.BORDER);
        m_tPID.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));

        final Label label_3 = new Label(cDetails, SWT.NONE);
        label_3.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
        label_3.setText("Category:");

        m_tCategory = new Text(cDetails, SWT.BORDER);
        m_tCategory.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));

        final Label label_4 = new Label(cDetails, SWT.NONE);
        label_4.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
        label_4.setText("Thread:");

        m_tThread = new Text(cDetails, SWT.BORDER);

        final GridData gridData_1 = new GridData(GridData.FILL, GridData.CENTER, false, false);
        gridData_1.widthHint = 215;
        m_tThread.setLayoutData(gridData_1);

        final Label traceLevelLabel = new Label(cDetails, SWT.NONE);
        traceLevelLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
        traceLevelLabel.setText("Trace Level:");

        m_tTraceLevel = new Text(cDetails, SWT.BORDER);
        m_tTraceLevel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));

        // Create the NDC.
        final Label ndcLabel = new Label(cDetails, SWT.NONE);
        ndcLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false, 2, 1));
        ndcLabel.setText("NDC:");

        m_stNDC = new StyledText(cDetails, SWT.V_SCROLL | SWT.MULTI | SWT.READ_ONLY | SWT.BORDER | SWT.H_SCROLL);
        m_stNDC.setEditable(false);
        m_stNDC.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
        sashForm_1.setWeights(new int[] { 1, 1 });
        m_sfMain.setWeights(new int[] { 35, 563 });

        // Default values
        m_cbSubject.setText("LogEvent");

        tfDetails.setSelection(tiMessage);

        // Create the details section.
        m_dcDetailsComposite = new DetailsComposite(tfDetails, SWT.NONE);
        tiMessage.setControl(m_dcDetailsComposite);

        // Set the focus to the combobox.
        m_cbSubject.forceFocus();
    }

    /**
     * This method adds a specific message to the LogEvent panel.
     * 
     * @param sMessage The message to add.
     */
    private void addMessage(String sMessage)
    {
        int iNode = 0;

        try
        {
            iNode = s_dDoc.parseString(sMessage);

            LogEventPanel lep = (LogEventPanel) m_hmSubscribed.get("LogEvent");

            if (lep != null)
            {
                lep.addLogEvent(new ESLogEvent(iNode));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * This method adds the subject to the subscription list.
     * 
     * @param sSubject The subject that was subscribed to.
     */
    private void addSubscribedSubject(String sSubject)
    {
        if (m_hmSubscribed.containsKey(sSubject) == false)
        {
            CTabItem tiItem = new CTabItem(m_tpTabEvents, SWT.NONE);
            tiItem.setText(sSubject);

            // Composite cTabComposite = new Composite(tpTabEvents, SWT.NONE);
            // cTabComposite.setLayout(new FillLayout());
            // tiItem.setControl(cTabComposite);
            // Get the panel for it.
            AbstractEventPanel aepPanel = AbstractEventPanel.getInstance(sSubject, this, m_tpTabEvents);
            tiItem.setControl(aepPanel);

            m_hmSubscribed.put(sSubject, aepPanel);

            m_tpTabEvents.setSelection(tiItem);
            m_tpTabEvents.redraw();

            rebuildSubjects();
        }
    }

    /**
     * This method adds the subject to the subscription list based on a content provider.
     * 
     * @param sSubject The subject that was subscribed to.
     * @param lcpProvider The content provider.
     */
    private void addSubscribedSubject(String sSubject, ILogContentProvider lcpProvider)
    {
        sSubject = sSubject + ":" + lcpProvider.getName();

        if (m_hmSubscribed.containsKey(sSubject) == false)
        {
            CTabItem tiItem = new CTabItem(m_tpTabEvents, SWT.NONE);
            tiItem.setText(sSubject);

            // Composite cTabComposite = new Composite(tpTabEvents, SWT.NONE);
            // cTabComposite.setLayout(new FillLayout());
            // tiItem.setControl(cTabComposite);
            // Get the panel for it.
            AbstractEventPanel aepPanel = AbstractEventPanel.getInstance(sSubject, this, m_tpTabEvents, lcpProvider);
            tiItem.setControl(aepPanel);

            m_hmSubscribed.put(sSubject, aepPanel);

            m_tpTabEvents.setSelection(tiItem);
            m_tpTabEvents.redraw();

            rebuildSubjects();
        }
    }

    /**
     * This method build up the table with subscribed subjects.
     */
    private void rebuildSubjects()
    {
        m_tblSubjects.removeAll();

        for (Iterator<String> iSubjects = m_hmSubscribed.keySet().iterator(); iSubjects.hasNext();)
        {
            String sSubject = iSubjects.next();

            TableItem tiNew = new TableItem(m_tblSubjects, SWT.NONE);
            tiNew.setText(new String[] { sSubject });
        }
    }

    /**
     * This method registers all images that could be needed by this application.
     */
    private void registerImages()
    {
        s_irImages = new ImageRegistry();

        String iconPath = "filter/icons/";
        s_irImages.put(IMG_OPEN_CORDYS,
                ImageDescriptor.createFromFile(EventServiceClient.class, iconPath + IMG_OPEN_CORDYS + ".gif"));
        s_irImages.put(IMG_SAVE, ImageDescriptor.createFromFile(EventServiceClient.class, iconPath + IMG_SAVE + ".gif"));
        s_irImages.put(IMG_OPEN_LOG4J,
                ImageDescriptor.createFromFile(EventServiceClient.class, iconPath + IMG_OPEN_LOG4J + ".gif"));
        s_irImages.put(IMG_NEW_LISTENER,
                ImageDescriptor.createFromFile(EventServiceClient.class, iconPath + IMG_NEW_LISTENER + ".gif"));
    }

    /**
     * This method unsubscribes to a certain subject.
     * 
     * @param sSubject The subject to unsubscribe.
     */
    private void unsubscribeToSubject(String sSubject)
    {
        if (m_hmSubscribed.containsKey(sSubject))
        {
            AbstractEventPanel aepPanel = m_hmSubscribed.get(sSubject);
            aepPanel.clear();
            m_hmSubscribed.remove(sSubject);
            // tpTabEvents.remove(aepPanel);
            rebuildSubjects();

            int iRequest = 0;

            try
            {
                // Create the subscription-message.
                Connector cCon = m_swSoap.getConnector();
                iRequest = cCon.createSOAPMethod("http://schemas.cordys.com/1.0/eventservice", "Unsubscribe");

                Document dDoc = Node.getDocument(iRequest);
                dDoc.createTextElement("subject", sSubject, iRequest);

                cCon.send(Node.getParent(Node.getParent(iRequest)));
            }
            catch (Exception e)
            {
                showError("Error subscribing to subject " + sSubject + "\r\n" + Util.getStackTrace(e));
            }
            finally
            {
                Node.delete(iRequest);
            }
        }
    }
}
