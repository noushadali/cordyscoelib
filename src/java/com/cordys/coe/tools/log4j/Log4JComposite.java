package com.cordys.coe.tools.log4j;

import com.cordys.coe.tools.es.ILogEvent;
import com.cordys.coe.tools.es.Log4JLogEvent;
import com.cordys.coe.tools.es.swt.DetailsComposite;
import com.cordys.coe.tools.es.swt.EventDetails;
import com.cordys.coe.tools.es.swt.IMessageHandler;
import com.cordys.coe.tools.es.swt.Log4JReceiver;
import com.cordys.coe.util.swt.BorderLayout;
import com.cordys.coe.util.swt.MessageBoxUtil;
import com.cordys.coe.util.system.SystemInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * This composite holds all controls for the Log4J viewer. It can load and save logfiles and add new Socket receivers.
 *
 * @author  pgussow
 */
public class Log4JComposite extends Composite
    implements IMessageHandler, ILog4JComposite
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
     * Identifies the image for clearing all events.
     */
    private static final String IMG_CLEAR_ALL = "delete";
    /**
     * Holds the default port for the receiver.
     */
    private static final int DEFAULT_PORT = 4445;
    /**
     * Holds the image registry for this application.
     */
    private static ImageRegistry s_irImages = null;
    /**
     * Holds all the receiver panels that are shown.
     */
    protected HashMap<String, Log4JPanel> m_hmReceiverPanels = new HashMap<String, Log4JPanel>();
    /**
     * Holds the composite showing all the details.
     */
    private DetailsComposite m_dcDetailsComposite;
    /**
     * Holds the log4j receiver.
     */
    private HashMap<String, Log4JReceiver> m_hmLog4JReceivers = new HashMap<String, Log4JReceiver>();
    /**
     * Holds the configuration.
     */
    private ILogViewerConfiguration m_lvcConfig;
    /**
     * This SashForm is the main one. It separates the subject edit and the rest.
     */
    private SashForm m_sfMain;
    /**
     * Holds the NDC for the log level.
     */
    private StyledText m_stNDC;
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
     * Creates a new Log4JComposite object.
     *
     * @param   cParent          The parent composite.
     * @param   bShowPortDialog  Whether or not to show the dialog to add a new listener.
     *
     * @throws  IOException  DOCUMENTME
     */
    public Log4JComposite(Composite cParent, boolean bShowPortDialog)
                   throws IOException
    {
        super(cParent, SWT.NONE);

        // Load the configuration
        m_lvcConfig = new LVConfiguration(Log4JComposite.class.getResourceAsStream("log4jviewer.properties"));

        setLayout(new FillLayout(SWT.FILL));

        createContents();

        // Add the general panel.
        addEventPanel("General");

        if (bShowPortDialog == true)
        {
            startLog4JListening();
        }
    }

    /**
     * Creates a new Log4JComposite object.
     *
     * @param   cParent          The parent composite.
     * @param   bShowPortDialog  Whether or not to show the dialog to add a new listener.
     * @param   lvcConfig        Holds the log viewer configuration.
     *
     * @throws  IOException  DOCUMENTME
     */
    public Log4JComposite(Composite cParent, boolean bShowPortDialog, ILogViewerConfiguration lvcConfig)
                   throws IOException
    {
        super(cParent, SWT.NONE);

        // Load the configuration
        m_lvcConfig = lvcConfig;

        setLayout(new FillLayout(SWT.FILL));

        createContents();

        // Add the general panel.
        addEventPanel("General");

        if (bShowPortDialog == true)
        {
            startLog4JListening();
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
     * This method gets the configuration for the Event Service Client.
     *
     * @return  The configuration for the Event Service Client.
     */
    public ILogViewerConfiguration getConfiguration()
    {
        return m_lvcConfig;
    }

    /**
     * This method gets the image registry for this client.
     *
     * @return  The image registry for this client.
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
     * @return  The title.
     */
    public String getTitle()
    {
        StringBuffer sbReturn = new StringBuffer(255);

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
     * This method handles the messages that are received from the SocketReceivers.
     *
     * @param  sLogName   The name of the log file.
     * @param  edDetails  The details of the Log4J event.
     *
     * @see    com.cordys.coe.tools.es.swt.IMessageHandler#handleMessage(java.lang.String,
     *         com.cordys.coe.tools.es.swt.EventDetails)
     */
    public void handleMessage(String sLogName, EventDetails edDetails)
    {
        Object oTemp = m_hmReceiverPanels.get(sLogName);

        if (oTemp != null)
        {
            Log4JPanel lpPanel = (Log4JPanel) oTemp;
            Log4JLogEvent lleEvent = new Log4JLogEvent(edDetails);
            lpPanel.receive(lleEvent);
        }
    }

    /**
     * This method loads the LogEvents from a file.
     *
     * @param  iFileType
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
                    mbPaging.setMessage("The file is bigger then " + MAX_FILESIZE +
                                        " bytes. Do you want to use paging (which is recommended)?");

                    if (mbPaging.open() == SWT.YES)
                    {
                        ILogContentProvider lcpProvider = LogContentFactory.createLog4JFileProviderByRecordCount(fTemp
                                                                                                                 .getAbsolutePath(),
                                                                                                                 m_lvcConfig
                                                                                                                 .getPageSize());
                        addEventPanel(fTemp.getName(), lcpProvider);
                        bOK = false;
                    }
                }

                if (bOK == true)
                {
                    // Add the panel
                    String sRootTag = "";
                    String sEndRootTag = "";

                    String sSourceName = fTemp.getName();
                    addEventPanel(sSourceName);

                    if (iFileType == TYPE_LOG4J)
                    {
                        sRootTag = "<log4j:event";
                        sEndRootTag = "</log4j:event>";
                    }
                    else if (iFileType == TYPE_CORDYS_LOG4J)
                    {
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

                            EventDetails ed = null;

                            if (iFileType == TYPE_LOG4J)
                            {
                                ed = EventDetails.createInstanceFromLog4JXML(sbCurrentMessage.toString());
                            }
                            else if (iFileType == TYPE_CORDYS_LOG4J)
                            {
                                ed = EventDetails.createInstanceFromCordysLog4JXML(sbCurrentMessage.toString());
                            }

                            if (ed != null)
                            {
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
                MessageBoxUtil.showError("Error loading file " + sFilename + ": " + e.getMessage(), e);
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
                if (
                    MessageBoxUtil.showConfirmation("The file " + sFilename +
                                                        " already exists. Do you want to overwrite it?") == true)
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
                // Now we need to get the active panel so that we save the proper Logs.
                CTabItem tiTabItem = m_tpTabEvents.getSelection();

                if (m_hmReceiverPanels.containsKey(tiTabItem.getText()))
                {
                    Log4JPanel lpPanel = m_hmReceiverPanels.get(tiTabItem.getText());

                    try
                    {
                        lpPanel.saveToFile(sFilename);
                        MessageBoxUtil.showInformation("File " + sFilename + " saved sucessfully.");
                    }
                    catch (IOException e)
                    {
                        MessageBoxUtil.showError("Error saving the logmessages.", e);
                    }
                }
            }
        }
    }

    /**
     * This method sets the text of the details.
     *
     * @param  leEvent  The logevent to display.
     */
    public synchronized void setDetailedText(final ILogEvent leEvent)
    {
        // Since this method can be called by different threads, we need to go to the UI thread.
        Display dTemp = getShell().getDisplay();

        if ((dTemp != null) && !dTemp.isDisposed())
        {
            dTemp.asyncExec(new Runnable()
                {
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
     * This method starts the socket reader to read the Log4J messages sent via the socketappender.
     *
     * @return  The portnumber used.
     */
    public String startLog4JListening()
    {
        String sReturn = "";

        // Start up the Log4J receiving thread.
        try
        {
            NewListenerDlg nld = new NewListenerDlg(getShell());
            nld.setDefaultPortNumber(DEFAULT_PORT);
            nld.setPanelOptions(getPanelNames());

            int iResult = nld.open();

            if (iResult == IDialogConstants.OK_ID)
            {
                int iPortnumber = nld.getPortnumber();
                sReturn = "" + iPortnumber;

                String sLogName = nld.getLogName();
                String sListenerName = nld.getListenerName();

                // Start the Log4J receiver.
                Log4JReceiver lrReceiver = new Log4JReceiver(this, iPortnumber, sListenerName, sLogName);
                lrReceiver.start();

                // Add the receiver to the list of receivers.
                m_hmLog4JReceivers.put(lrReceiver.getReceiverName(), lrReceiver);
                addEventPanel(sLogName);
            }
        }
        catch (IOException e1)
        {
            MessageBoxUtil.showError("Error starting Log4J", e1);
        }

        return sReturn;
    }

    /**
     * This method creates all the controls for the window.
     */
    protected void createContents()
    {
        // Create the coolbar and the rest of the page.
        Composite cMain = new Composite(this, SWT.NONE);
        cMain.setLayout(new BorderLayout());

        cMain.addKeyListener(new KeyAdapter()
            {
                /**
                 * This method handles the Alt-C shortcut to clear the current panel.
                 *
                 * @param  keEvent  The key event.
                 *
                 * @see    org.eclipse.swt.events.KeyAdapter#keyPressed(org.eclipse.swt.events.KeyEvent)
                 */
                public void keyPressed(KeyEvent keEvent)
                {
                    if ((keEvent.stateMask == SWT.ALT) && ((keEvent.keyCode == 'c') || (keEvent.keyCode == 'C')))
                    {
                        if (m_tpTabEvents != null)
                        {
                            CTabItem tiTempItem = m_tpTabEvents.getSelection();

                            if ((tiTempItem != null) && (tiTempItem.getControl() != null) &&
                                    (tiTempItem.getControl() instanceof Log4JPanel))
                            {
                                Log4JPanel lpTemp = (Log4JPanel) tiTempItem.getControl();
                                lpTemp.clear();
                            }
                        }
                    }
                    super.keyPressed(keEvent);
                }
            });

        // Create the toolbar for this application.
        final CoolBar cbCoolBar = new CoolBar(cMain, SWT.NONE);
        cbCoolBar.setLayoutData(BorderLayout.NORTH);

        // Toolbar open
        final ToolBar tbToolbar = new ToolBar(cbCoolBar, SWT.FLAT);

        // Open Cordys Logfile.
        ToolItem tiItem = new ToolItem(tbToolbar, SWT.NULL);
        tiItem.setImage(getOwnImageRegistry().get(IMG_OPEN_CORDYS));
        tiItem.setToolTipText("Open a Cordys Spy XML logfile");

        tiItem.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent arg0)
                {
                    loadFromFile(TYPE_CORDYS_SPY);
                }
            });

        tiItem = new ToolItem(tbToolbar, SWT.NULL);
        tiItem.setImage(getOwnImageRegistry().get(IMG_OPEN_CORDYS));
        tiItem.setToolTipText("Open a Cordys Log4J XML logfile");

        tiItem.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent arg0)
                {
                    loadFromFile(TYPE_CORDYS_LOG4J);
                }
            });

        tiItem = new ToolItem(tbToolbar, SWT.NULL);
        tiItem.setImage(getOwnImageRegistry().get(IMG_OPEN_LOG4J));
        tiItem.setToolTipText("Open a Log4J XML logfile");

        tiItem.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent arg0)
                {
                    loadFromFile(TYPE_LOG4J);
                }
            });

        tiItem = new ToolItem(tbToolbar, SWT.NULL);
        tiItem.setImage(getOwnImageRegistry().get(IMG_NEW_LISTENER));
        tiItem.setToolTipText("Add a new SocketListener for Log4J events");

        tiItem.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent arg0)
                {
                    startLog4JListening();
                }
            });

        tiItem = new ToolItem(tbToolbar, SWT.NULL);
        tiItem.setImage(getOwnImageRegistry().get(IMG_SAVE));
        tiItem.setToolTipText("Save the current events as a Log4J XML logfile");

        tiItem.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent arg0)
                {
                    saveLog4JLog();
                }
            });

        tiItem = new ToolItem(tbToolbar, SWT.NULL);
        tiItem.setImage(getOwnImageRegistry().get(IMG_CLEAR_ALL));

        tiItem.setToolTipText("Clears the events of all listeners");
        tiItem.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent e)
                {
                    // Clean all the panels that are shown.
                    for (Iterator<String> iPanels = m_hmReceiverPanels.keySet().iterator(); iPanels.hasNext();)
                    {
                        Log4JPanel lpPanel = m_hmReceiverPanels.get(iPanels.next());
                        lpPanel.clear();
                    }

                    // Clear the details.
                    if (m_dcDetailsComposite != null)
                    {
                        m_dcDetailsComposite.clearFields();
                    }

                    clearDetails();
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

        final Composite cMiddleFrame = new Composite(m_sfMain, SWT.NONE);
        cMiddleFrame.setLayout(new FillLayout());

        final Composite cEventFrame = new Composite(cMiddleFrame, SWT.NONE);
        cEventFrame.setLayout(new FillLayout());

        m_tpTabEvents = new CTabFolder(cEventFrame, SWT.NONE);

        // Create the default log event panel.
        final Composite cDetailFrame = new Composite(m_sfMain, SWT.NONE);
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
        // m_sfMain.setWeights(new int[] { 1, 1 });

        // Default values
        tfDetails.setSelection(tiMessage);

        // Create the details section.
        m_dcDetailsComposite = new DetailsComposite(tfDetails, SWT.NONE);

        m_dcDetailsComposite.setInformation(SystemInfo.getSystemInformation(), null, null);

        tiMessage.setControl(m_dcDetailsComposite);

        // Add the close listener to the tab folder to properly dispose of the panel.
        m_tpTabEvents.addCTabFolder2Listener(new CTabFolder2Adapter()
            {
                /**
                 * This method makes sure that the item is disposed of.
                 *
                 * @param  tfeEvent
                 *
                 * @see    org.eclipse.swt.custom.CTabFolder2Adapter#close(org.eclipse.swt.custom.CTabFolderEvent)
                 */
                public void close(CTabFolderEvent tfeEvent)
                {
                    CTabItem tiBeingClosed = (CTabItem) tfeEvent.item;
                    Log4JPanel lpPanel = (Log4JPanel) tiBeingClosed.getControl();
                    lpPanel.clear();
                    lpPanel.dispose();
                    m_hmReceiverPanels.remove(lpPanel.getPanelName());
                }
            });
    }

    /**
     * This method adds a new panel with the name sName.
     *
     * @param   sName  The name for the new panel.
     *
     * @return  The panel for these events.
     */
    private Log4JPanel addEventPanel(String sName)
    {
        return addEventPanel(sName, null);
    }

    /**
     * This method adds a new panel with the name sName.
     *
     * @param   sName        The name for the new panel.
     * @param   lcpProvider  The content provider. Can be null.
     *
     * @return  The panel for these events.
     */
    private Log4JPanel addEventPanel(String sName, ILogContentProvider lcpProvider)
    {
        Log4JPanel lpReturn = null;

        if (m_hmReceiverPanels.containsKey(sName) == false)
        {
            CTabItem tiItem = new CTabItem(m_tpTabEvents, SWT.CLOSE);
            tiItem.setText(sName);

            lpReturn = Log4JPanel.getInstance(sName, this, m_tpTabEvents, lcpProvider);
            tiItem.setControl(lpReturn);

            m_hmReceiverPanels.put(sName, lpReturn);

            m_tpTabEvents.setSelection(tiItem);
            m_tpTabEvents.redraw();
        }
        else
        {
            lpReturn = m_hmReceiverPanels.get(sName);
        }

        return lpReturn;
    }

    /**
     * This method returns the names of all panels in the list.
     *
     * @return  The panel names.
     */
    private String[] getPanelNames()
    {
        String[] saReturn = new String[m_hmReceiverPanels.size()];
        int iCount = 0;

        for (Iterator<String> iName = m_hmReceiverPanels.keySet().iterator(); iName.hasNext();)
        {
            String sName = iName.next();
            saReturn[iCount] = sName;
            iCount++;
        }

        return saReturn;
    }

    /**
     * This method registers all images that could be needed by this application.
     */
    private void registerImages()
    {
        s_irImages = new ImageRegistry();

        String iconPath = "filter/icons/";
        s_irImages.put(IMG_OPEN_CORDYS,
                       ImageDescriptor.createFromFile(Log4JViewer.class, iconPath + IMG_OPEN_CORDYS + ".gif"));
        s_irImages.put(IMG_SAVE, ImageDescriptor.createFromFile(Log4JViewer.class, iconPath + IMG_SAVE + ".gif"));
        s_irImages.put(IMG_OPEN_LOG4J,
                       ImageDescriptor.createFromFile(Log4JViewer.class, iconPath + IMG_OPEN_LOG4J + ".gif"));
        s_irImages.put(IMG_NEW_LISTENER,
                       ImageDescriptor.createFromFile(Log4JViewer.class, iconPath + IMG_NEW_LISTENER + ".gif"));
        s_irImages.put(IMG_CLEAR_ALL,
                       ImageDescriptor.createFromFile(Log4JViewer.class, iconPath + IMG_CLEAR_ALL + ".gif"));
    }
}
