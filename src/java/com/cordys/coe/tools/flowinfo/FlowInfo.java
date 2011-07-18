package com.cordys.coe.tools.flowinfo;

import com.cordys.coe.util.cgc.CGCFactory;
import com.cordys.coe.util.cgc.CordysGatewayClientException;
import com.cordys.coe.util.cgc.CordysSOAPException;
import com.cordys.coe.util.cgc.ICordysGatewayClient;
import com.cordys.coe.util.cgc.config.CGCAuthenticationFactory;
import com.cordys.coe.util.cgc.config.CGCConfigFactory;
import com.cordys.coe.util.cgc.config.IAuthenticationConfiguration;
import com.cordys.coe.util.cgc.config.ICGCConfiguration;
import com.cordys.coe.util.cgc.userinfo.IOrganizationInfo;
import com.cordys.coe.util.cgc.userinfo.IUserInfo;
import com.cordys.coe.util.config.IWebGatewayConfiguration;
import com.cordys.coe.util.swt.CGCLoginForm;
import com.cordys.coe.util.swt.InProgressDialog;
import com.cordys.coe.util.swt.MessageBoxUtil;
import com.cordys.coe.util.swt.SWTResourceManager;
import com.cordys.coe.util.swt.SWTUtils;
import com.cordys.coe.util.xml.dom.NamespaceConstants;
import com.cordys.coe.util.xml.dom.NiceDOMWriter;
import com.cordys.coe.util.xml.dom.PrefixResolver;
import com.cordys.coe.util.xml.dom.XMLHelper;
import com.cordys.coe.util.xml.dom.XPathHelper;

import java.io.File;
import java.io.FileInputStream;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import java.util.concurrent.ArrayBlockingQueue;

import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.TreeAdapter;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class provides information about the BPM.
 *
 * @author  pgussow
 */
public class FlowInfo
{
    /**
     * Holds the logger to use.
     */
    private static final Logger LOG = Logger.getLogger(FlowInfo.class);

    static
    {
        NamespaceConstants.registerPrefix("bpmdep", "http://schemas.cordys.com/bpm/deployment/1.0");
        NamespaceConstants.registerPrefix("bpminst", "http://schemas.cordys.com/bpm/instance/1.0");
        NamespaceConstants.registerPrefix("coboc", "http://schemas.cordys.com/1.0/coboc");
    }

    /**
     * Holds the date format to use.
     */
    private static SimpleDateFormat s_sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    /**
     * DOCUMENTME.
     */
    protected Shell m_sShell;
    /**
     * Holds the list of threads available for requests.
     */
    private ArrayList<RequestRunnerThread> m_alRequestThreads;
    /**
     * DOCUMENTME.
     */
    private Button m_bGetAllFlows;
    /**
     * DOCUMENTME.
     */
    private Combo m_cbOrganization;
    /**
     * DOCUMENTME.
     */
    private ICordysGatewayClient m_cgcClient;
    /**
     * DOCUMENTME.
     */
    private Color m_cRed;
    /**
     * DOCUMENTME.
     */
    private Display m_dDisplay;
    /**
     * DOCUMENTME.
     */
    private InProgressDialog m_ipbProgress;
    /**
     * DOCUMENTME.
     */
    private MessageMapBrowser m_mmbMessageMap;
    /**
     * DOCUMENTME.
     */
    private ProgressBar m_pbLoadingActivities;
    /**
     * Holds the queue for the requests.
     */
    private ArrayBlockingQueue<RequestObject> m_qRequests;
    /**
     * Holds the queue for the responses.
     */
    private ArrayBlockingQueue<RequestObject> m_qResponses;
    /**
     * DOCUMENTME.
     */
    private Text m_tActEndTime;
    /**
     * DOCUMENTME.
     */
    private Text m_tActExecutedBy;
    /**
     * DOCUMENTME.
     */
    private Text m_tActID;
    /**
     * DOCUMENTME.
     */
    private Text m_tActIteration;
    /**
     * DOCUMENTME.
     */
    private Text m_tActMessageID;
    /**
     * DOCUMENTME.
     */
    private Text m_tActName;
    /**
     * DOCUMENTME.
     */
    private Text m_tActParticipant;
    /**
     * DOCUMENTME.
     */
    private Text m_tActRequest;
    /**
     * DOCUMENTME.
     */
    private Text m_tActResponse;
    /**
     * DOCUMENTME.
     */
    private Text m_tActStartTime;
    /**
     * DOCUMENTME.
     */
    private Text m_tActStatus;
    /**
     * DOCUMENTME.
     */
    private Text m_tActType;
    /**
     * DOCUMENTME.
     */
    private Table m_tblAllFlows;
    /**
     * DOCUMENTME.
     */
    private Text m_tBPMLDetails;
    /**
     * DOCUMENTME.
     */
    private Text m_tBPMN;
    /**
     * DOCUMENTME.
     */
    private Text m_tBPMNDetails;
    /**
     * DOCUMENTME.
     */
    private Text m_tCurrentOwner;
    /**
     * DOCUMENTME.
     */
    private Text m_tDescription;
    /**
     * DOCUMENTME.
     */
    private Text m_tEndTime;
    /**
     * DOCUMENTME.
     */
    private Text m_tErrorMessage;
    /**
     * DOCUMENTME.
     */
    private Text m_tFullProcessName;
    /**
     * DOCUMENTME.
     */
    private TreeItem m_tiActivityRoot;
    /**
     * DOCUMENTME.
     */
    private ToolItem m_tiMMFirst;
    /**
     * DOCUMENTME.
     */
    private ToolItem m_tiMMLast;
    /**
     * DOCUMENTME.
     */
    private ToolItem m_tiMMNext;
    /**
     * DOCUMENTME.
     */
    private ToolItem m_tiMMPrevious;
    /**
     * DOCUMENTME.
     */
    private Text m_tInputMessage;
    /**
     * DOCUMENTME.
     */
    private Text m_tInstanceID;
    /**
     * DOCUMENTME.
     */
    private TreeItem m_tiRootItem;
    /**
     * DOCUMENTME.
     */
    private Text m_tMessageMapDetail;
    /**
     * DOCUMENTME.
     */
    private Tree m_tProcessDetails;
    /**
     * DOCUMENTME.
     */
    private Text m_tProcessPriority;
    /**
     * DOCUMENTME.
     */
    private Text m_tProcessType;
    /**
     * DOCUMENTME.
     */
    private Thread m_tResponseListener;
    /**
     * DOCUMENTME.
     */
    private Text m_tStartTime;
    /**
     * DOCUMENTME.
     */
    private Text m_tStartUser;
    /**
     * DOCUMENTME.
     */
    private Text m_tStatus;
    /**
     * DOCUMENTME.
     */
    private Text m_tTimeOut;
    /**
     * DOCUMENTME.
     */
    private Text m_tVersion;

    /**
     * Launch the application.
     *
     * @param  args
     */
    public static void main(String[] args)
    {
        try
        {
            FlowInfo window = new FlowInfo();

            window.open();
        }
        catch (Exception e)
        {
            LOG.error("Error opening window", e);
        }
    }

    /**
     * Open the window.
     */
    public void open()
    {
        m_dDisplay = Display.getDefault();
        m_cRed = new Color(Display.getCurrent(), 255, 0, 0);
        m_sShell = new Shell();

        CGCLoginForm cl = new CGCLoginForm(m_sShell);
        final IWebGatewayConfiguration wgcConfig = cl.open();

        if (wgcConfig != null)
        {
            createContents();

            loadProperties();

            m_sShell.layout();

            SWTUtils.centerShell(m_dDisplay, m_sShell);
            m_sShell.open();

            m_ipbProgress = new InProgressDialog(m_sShell, "Please wait",
                                                 "Connecting to Cordys...\nPlease wait");
            m_ipbProgress.open();
            new Thread(new Runnable()
                {
                    public void run()
                    {
                        connectToCordys(wgcConfig);
                    }
                }).start();

            while (!m_sShell.isDisposed())
            {
                if (!m_dDisplay.readAndDispatch())
                {
                    m_dDisplay.sleep();
                }
            }
        }
    }

    /**
     * This method makes the connections.
     *
     * @param  wgcConfig  The configuration for the web gateway.
     */
    protected void connectToCordys(IWebGatewayConfiguration wgcConfig)
    {
        ICGCConfiguration icConfig = CGCConfigFactory.createConfiguration(wgcConfig);
        IAuthenticationConfiguration iaAuth = CGCAuthenticationFactory.createAuthentication(wgcConfig);

        try
        {
            m_cgcClient = CGCFactory.createCGC(iaAuth, icConfig);
            m_cgcClient.setNamespaceAwareResponses(true);
            m_cgcClient.connect();

            m_dDisplay.asyncExec(new Runnable()
                {
                    public void run()
                    {
                        fillOrgsAndISVs();

                        m_cbOrganization.setFocus();

                        // Create the needed queues and threads.
                        m_qRequests = new ArrayBlockingQueue<RequestObject>(150);
                        m_qResponses = new ArrayBlockingQueue<RequestObject>(150);

                        m_alRequestThreads = new ArrayList<RequestRunnerThread>();

                        int iMaxThreads = 5;

                        for (int iCount = 0; iCount < iMaxThreads; iCount++)
                        {
                            RequestRunnerThread rrt = new RequestRunnerThread(m_qRequests,
                                                                              m_qResponses);
                            m_alRequestThreads.add(rrt);
                        }

                        // Start all threads
                        for (RequestRunnerThread rrt : m_alRequestThreads)
                        {
                            rrt.start();
                        }

                        // Start the thread that is listening for the responses.
                        m_tResponseListener = new Thread(new ResponseListenThread());
                        m_tResponseListener.start();

                        m_ipbProgress.closeDialog();
                    }
                });
        }
        catch (CordysGatewayClientException e)
        {
            MessageBoxUtil.showError(m_sShell, "Error connecting to Cordys", e);
        }
    }

    /**
     * Create contents of the window.
     */
    protected void createContents()
    {
        m_sShell.setImage(SWTResourceManager.getImage(FlowInfo.class, "icons/bpm.gif"));
        m_sShell.addShellListener(new ShellAdapter()
            {
                public void shellClosed(final ShellEvent e)
                {
                    System.exit(0);
                }
            });
        m_sShell.setLayout(new GridLayout());
        m_sShell.setSize(945, 829);
        m_sShell.setText("Flow Info");

        final Group selectMethodToGroup = new Group(m_sShell, SWT.NONE);
        final GridData gd_selectMethodToGroup = new GridData(SWT.FILL, SWT.CENTER, false, false);
        selectMethodToGroup.setLayoutData(gd_selectMethodToGroup);

        final GridLayout gridLayout_1 = new GridLayout();
        gridLayout_1.numColumns = 3;
        selectMethodToGroup.setLayout(gridLayout_1);
        selectMethodToGroup.setText(" Select organization to read flows from ");

        final Label organizationLabel = new Label(selectMethodToGroup, SWT.NONE);
        organizationLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        organizationLabel.setText("Organization:");

        m_cbOrganization = new Combo(selectMethodToGroup, SWT.READ_ONLY);
        m_cbOrganization.addKeyListener(new KeyAdapter()
            {
                public void keyPressed(final KeyEvent ke)
                {
                    if (ke.character == 13)
                    {
                        m_bGetAllFlows.setFocus();
                    }
                }
            });

        final GridData gd_m_cbOrganization = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        m_cbOrganization.setLayoutData(gd_m_cbOrganization);

        final Label timeoutLabel = new Label(selectMethodToGroup, SWT.NONE);
        timeoutLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        timeoutLabel.setText("Timeout:");

        m_tTimeOut = new Text(selectMethodToGroup, SWT.BORDER);
        m_tTimeOut.setText("30000");

        final GridData gd_m_tTimeOut = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_tTimeOut.setLayoutData(gd_m_tTimeOut);

        m_bGetAllFlows = new Button(selectMethodToGroup, SWT.NONE);
        m_bGetAllFlows.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    getAllFlows();
                }
            });

        final GridData gd_composeButton = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        gd_composeButton.widthHint = 84;
        m_bGetAllFlows.setLayoutData(gd_composeButton);
        m_bGetAllFlows.setText("Get All Flows");

        final SashForm sashForm = new SashForm(m_sShell, SWT.NONE);
        sashForm.setOrientation(SWT.VERTICAL);

        m_tblAllFlows = new Table(sashForm, SWT.FULL_SELECTION | SWT.BORDER);
        m_tblAllFlows.addKeyListener(new KeyAdapter()
            {
                public void keyPressed(final KeyEvent keyevent)
                {
                    if (keyevent.character == 13)
                    {
                        try
                        {
                            displayProcessDetails();
                        }
                        catch (Exception e)
                        {
                            MessageBoxUtil.showError(m_sShell, "Error getting process details", e);
                        }
                    }
                }
            });
        m_tblAllFlows.addMouseListener(new MouseAdapter()
            {
                public void mouseDoubleClick(final MouseEvent mouseevent)
                {
                    try
                    {
                        displayProcessDetails();
                    }
                    catch (Exception e)
                    {
                        MessageBoxUtil.showError(m_sShell, "Error getting process details", e);
                    }
                }
            });
        m_tblAllFlows.setLinesVisible(true);
        m_tblAllFlows.setHeaderVisible(true);

        final TableColumn newColumnTableColumn = new TableColumn(m_tblAllFlows, SWT.NONE);
        newColumnTableColumn.setWidth(242);
        newColumnTableColumn.setText("InstanceID");

        final TableColumn newColumnTableColumn_1 = new TableColumn(m_tblAllFlows, SWT.NONE);
        newColumnTableColumn_1.setWidth(262);
        newColumnTableColumn_1.setText("Process Name");

        final TableColumn newColumnTableColumn_2 = new TableColumn(m_tblAllFlows, SWT.NONE);
        newColumnTableColumn_2.setWidth(166);
        newColumnTableColumn_2.setText("Start date");

        final TableColumn newColumnTableColumn_3 = new TableColumn(m_tblAllFlows, SWT.NONE);
        newColumnTableColumn_3.setWidth(167);
        newColumnTableColumn_3.setText("End time");

        final TableColumn newColumnTableColumn_4 = new TableColumn(m_tblAllFlows, SWT.NONE);
        newColumnTableColumn_4.setWidth(100);
        newColumnTableColumn_4.setText("Status");
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final Composite composite = new Composite(sashForm, SWT.NONE);
        composite.setLayout(new GridLayout());

        final SashForm sashForm_1 = new SashForm(composite, SWT.NONE);

        m_tProcessDetails = new Tree(sashForm_1, SWT.FULL_SELECTION | SWT.BORDER);
        m_tProcessDetails.addTreeListener(new TreeAdapter()
            {
                public void treeExpanded(final TreeEvent te)
                {
                    loadActivityDetails(te);
                }
            });
        m_tProcessDetails.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent selectionevent)
                {
                    selectTreeItem();
                }
            });
        sashForm_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final Composite composite_1 = new Composite(sashForm_1, SWT.NONE);
        composite_1.setLayout(new GridLayout());

        final TabFolder m_tfDetails = new TabFolder(composite_1, SWT.NONE);
        final GridData gd_m_tfDetails = new GridData(SWT.FILL, SWT.FILL, true, true);
        m_tfDetails.setLayoutData(gd_m_tfDetails);

        final TabItem m_tiProcessDetails = new TabItem(m_tfDetails, SWT.NONE);
        m_tiProcessDetails.setText("Process Instance Details");

        final TabItem m_tiMessageMap = new TabItem(m_tfDetails, SWT.NONE);
        m_tiMessageMap.setText("Message Map");

        final Composite composite_4 = new Composite(m_tfDetails, SWT.NONE);
        composite_4.setLayout(new GridLayout());
        m_tiMessageMap.setControl(composite_4);

        final ToolBar toolBar = new ToolBar(composite_4, SWT.NONE);
        toolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        m_tiMMFirst = new ToolItem(toolBar, SWT.PUSH);
        m_tiMMFirst.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent selectionevent)
                {
                    if (m_mmbMessageMap != null)
                    {
                        m_mmbMessageMap.first();
                    }
                }
            });
        m_tiMMFirst.setImage(SWTResourceManager.getImage(FlowInfo.class, "icons/first.gif"));

        m_tiMMPrevious = new ToolItem(toolBar, SWT.PUSH);
        m_tiMMPrevious.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent selectionevent)
                {
                    if (m_mmbMessageMap != null)
                    {
                        m_mmbMessageMap.previous();
                    }
                }
            });
        m_tiMMPrevious.setImage(SWTResourceManager.getImage(FlowInfo.class, "icons/previous.gif"));

        m_tiMMNext = new ToolItem(toolBar, SWT.PUSH);
        m_tiMMNext.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent selectionevent)
                {
                    if (m_mmbMessageMap != null)
                    {
                        m_mmbMessageMap.next();
                    }
                }
            });
        m_tiMMNext.setImage(SWTResourceManager.getImage(FlowInfo.class, "icons/next.gif"));

        m_tiMMLast = new ToolItem(toolBar, SWT.PUSH);
        m_tiMMLast.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent selectionevent)
                {
                    if (m_mmbMessageMap != null)
                    {
                        m_mmbMessageMap.last();
                    }
                }
            });
        m_tiMMLast.setImage(SWTResourceManager.getImage(FlowInfo.class, "icons/last.gif"));

        new ToolItem(toolBar, SWT.SEPARATOR);

        final ToolItem m_tiMMAllMessages = new ToolItem(toolBar, SWT.NONE);
        m_tiMMAllMessages.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent selectionevent)
                {
                    if (m_mmbMessageMap != null)
                    {
                        m_mmbMessageMap.showAll();
                    }
                }
            });
        m_tiMMAllMessages.setImage(SWTResourceManager.getImage(FlowInfo.class,
                                                               "icons/zoomout.gif"));

        m_tMessageMapDetail = new Text(composite_4,
                                       SWT.V_SCROLL | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);

        final GridData gd_m_tMessageMapDetail = new GridData(SWT.FILL, SWT.FILL, true, true);
        m_tMessageMapDetail.setLayoutData(gd_m_tMessageMapDetail);

        final TabItem m_tiInputOutput = new TabItem(m_tfDetails, SWT.NONE);
        m_tiInputOutput.setText("Input/Error");

        final Composite composite_5 = new Composite(m_tfDetails, SWT.NONE);
        composite_5.setLayout(new GridLayout());
        m_tiInputOutput.setControl(composite_5);

        final Group group = new Group(composite_5, SWT.NONE);
        group.setText(" Input Message ");
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        group.setLayout(new GridLayout());

        m_tInputMessage = new Text(group, SWT.V_SCROLL | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);

        final GridData gd_m_tInputMessage = new GridData(SWT.FILL, SWT.FILL, true, true);
        m_tInputMessage.setLayoutData(gd_m_tInputMessage);

        final Group group_1 = new Group(composite_5, SWT.NONE);
        group_1.setText(" Output Message ");
        group_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        group_1.setLayout(new GridLayout());

        m_tErrorMessage = new Text(group_1, SWT.V_SCROLL | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);

        final GridData gd_m_tErrorMessage = new GridData(SWT.FILL, SWT.FILL, true, true);
        m_tErrorMessage.setLayoutData(gd_m_tErrorMessage);

        final TabItem m_tiActivityDetails = new TabItem(m_tfDetails, SWT.NONE);
        m_tiActivityDetails.setToolTipText("The details for the currently selected activity");
        m_tiActivityDetails.setText("Activity details");

        final Composite composite_3 = new Composite(m_tfDetails, SWT.NONE);
        composite_3.setLayout(new GridLayout());
        m_tiActivityDetails.setControl(composite_3);

        final SashForm sashForm_2 = new SashForm(composite_3, SWT.NONE);
        sashForm_2.setOrientation(SWT.VERTICAL);
        sashForm_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final Group activityDetailsGroup = new Group(sashForm_2, SWT.NONE);
        final GridLayout gridLayout_4 = new GridLayout();
        gridLayout_4.numColumns = 4;
        activityDetailsGroup.setLayout(gridLayout_4);
        activityDetailsGroup.setText(" Activity details ");

        final Label label = new Label(activityDetailsGroup, SWT.NONE);
        label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        label.setText("Activity ID:");

        m_tActID = new Text(activityDetailsGroup, SWT.BORDER);

        final GridData gd_m_tActID = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_tActID.setLayoutData(gd_m_tActID);

        final Label label_1 = new Label(activityDetailsGroup, SWT.NONE);
        label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        label_1.setText("Iteration count:");

        m_tActIteration = new Text(activityDetailsGroup, SWT.BORDER);

        final GridData gd_m_tActIteration = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_tActIteration.setLayoutData(gd_m_tActIteration);

        final Label label_2 = new Label(activityDetailsGroup, SWT.NONE);
        label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        label_2.setText("Name:");

        m_tActName = new Text(activityDetailsGroup, SWT.BORDER);

        final GridData gd_m_tActname = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_tActName.setLayoutData(gd_m_tActname);

        final Label label_3 = new Label(activityDetailsGroup, SWT.NONE);
        label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        label_3.setText("Type:");

        m_tActType = new Text(activityDetailsGroup, SWT.BORDER);

        final GridData gd_m_tActType = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_tActType.setLayoutData(gd_m_tActType);

        final Label label_4 = new Label(activityDetailsGroup, SWT.NONE);
        label_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        label_4.setText("Status:");

        m_tActStatus = new Text(activityDetailsGroup, SWT.BORDER);

        final GridData gd_m_tActStatus = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_tActStatus.setLayoutData(gd_m_tActStatus);

        final Label label_9 = new Label(activityDetailsGroup, SWT.NONE);
        label_9.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        label_9.setText("Message ID:");

        m_tActMessageID = new Text(activityDetailsGroup, SWT.BORDER);

        final GridData gd_m_tActMessageID = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_tActMessageID.setLayoutData(gd_m_tActMessageID);

        final Label label_5 = new Label(activityDetailsGroup, SWT.NONE);
        label_5.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        label_5.setText("Start time:");

        m_tActStartTime = new Text(activityDetailsGroup, SWT.BORDER);

        final GridData gd_m_tActStartTime = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_tActStartTime.setLayoutData(gd_m_tActStartTime);

        final Label label_6 = new Label(activityDetailsGroup, SWT.NONE);
        label_6.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        label_6.setText("End time:");

        m_tActEndTime = new Text(activityDetailsGroup, SWT.BORDER);

        final GridData gd_m_tActEndTime = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_tActEndTime.setLayoutData(gd_m_tActEndTime);

        final Label label_7 = new Label(activityDetailsGroup, SWT.NONE);
        label_7.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        label_7.setText("Executed by:");

        m_tActExecutedBy = new Text(activityDetailsGroup, SWT.BORDER);

        final GridData gd_m_tActExecutedBy = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
        m_tActExecutedBy.setLayoutData(gd_m_tActExecutedBy);

        final Composite composite_9 = new Composite(sashForm_2, SWT.NONE);
        composite_9.setLayout(new GridLayout());

        final TabFolder tabFolder = new TabFolder(composite_9, SWT.NONE);
        tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final TabItem requestTabItem = new TabItem(tabFolder, SWT.NONE);
        requestTabItem.setText("Request");

        final Composite composite_10 = new Composite(tabFolder, SWT.NONE);
        composite_10.setLayout(new GridLayout());
        requestTabItem.setControl(composite_10);

        m_tActRequest = new Text(composite_10,
                                 SWT.V_SCROLL | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);

        final GridData gd_m_tActRequest = new GridData(SWT.FILL, SWT.FILL, true, true);
        m_tActRequest.setLayoutData(gd_m_tActRequest);

        final TabItem responseTabItem = new TabItem(tabFolder, SWT.NONE);
        responseTabItem.setText("Response");

        final Composite composite_10_1 = new Composite(tabFolder, SWT.NONE);
        composite_10_1.setLayout(new GridLayout());
        responseTabItem.setControl(composite_10_1);

        m_tActResponse = new Text(composite_10_1,
                                  SWT.V_SCROLL | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);

        final GridData gd_m_tActResponse = new GridData(SWT.FILL, SWT.FILL, true, true);
        m_tActResponse.setLayoutData(gd_m_tActResponse);

        final TabItem participantTabItem = new TabItem(tabFolder, SWT.NONE);
        participantTabItem.setText("Participant");

        final Composite composite_10_2 = new Composite(tabFolder, SWT.NONE);
        composite_10_2.setLayout(new GridLayout());
        participantTabItem.setControl(composite_10_2);

        m_tActParticipant = new Text(composite_10_2,
                                     SWT.V_SCROLL | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);

        final GridData gd_m_tActParticipant = new GridData(SWT.FILL, SWT.FILL, true, true);
        m_tActParticipant.setLayoutData(gd_m_tActParticipant);
        sashForm_2.setWeights(new int[] { 137, 211 });

        final TabItem m_tiBPML = new TabItem(m_tfDetails, SWT.NONE);
        m_tiBPML.setText("BPML");

        final Composite composite_6 = new Composite(m_tfDetails, SWT.NONE);
        composite_6.setLayout(new GridLayout());
        m_tiBPML.setControl(composite_6);

        m_tBPMLDetails = new Text(composite_6,
                                  SWT.V_SCROLL | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);

        final GridData gd_m_tBPMLDetails = new GridData(SWT.FILL, SWT.FILL, true, true);
        m_tBPMLDetails.setLayoutData(gd_m_tBPMLDetails);

        final TabItem m_tiBPMN = new TabItem(m_tfDetails, SWT.NONE);
        m_tiBPMN.setText("BPMN");

        final Composite composite_7 = new Composite(m_tfDetails, SWT.NONE);
        composite_7.setLayout(new GridLayout());
        m_tiBPMN.setControl(composite_7);

        m_tBPMNDetails = new Text(composite_7,
                                  SWT.V_SCROLL | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);

        final GridData gd_m_tBPMNDetails = new GridData(SWT.FILL, SWT.FILL, true, true);
        m_tBPMNDetails.setLayoutData(gd_m_tBPMNDetails);

        final Composite composite_2 = new Composite(m_tfDetails, SWT.NONE);
        composite_2.setLayout(new GridLayout());
        m_tiProcessDetails.setControl(composite_2);

        final Group mainDetailsGroup = new Group(composite_2, SWT.NONE);
        mainDetailsGroup.setText(" Main details ");

        final GridData gd_mainDetailsGroup = new GridData(SWT.FILL, SWT.CENTER, true, false);
        mainDetailsGroup.setLayoutData(gd_mainDetailsGroup);

        final GridLayout gridLayout_2 = new GridLayout();
        gridLayout_2.numColumns = 2;
        mainDetailsGroup.setLayout(gridLayout_2);

        final Label instanceIdLabel = new Label(mainDetailsGroup, SWT.NONE);
        instanceIdLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        instanceIdLabel.setText("Instance ID:");

        m_tInstanceID = new Text(mainDetailsGroup, SWT.BORDER);

        final GridData gd_m_tInstanceID = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_tInstanceID.setLayoutData(gd_m_tInstanceID);

        final Label fullProcessKeyLabel = new Label(mainDetailsGroup, SWT.NONE);
        final GridData gd_fullProcessKeyLabel = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        fullProcessKeyLabel.setLayoutData(gd_fullProcessKeyLabel);
        fullProcessKeyLabel.setText("Full process key:");

        m_tFullProcessName = new Text(mainDetailsGroup, SWT.BORDER);

        final GridData gd_m_tFullProcessName = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_tFullProcessName.setLayoutData(gd_m_tFullProcessName);

        final Label descriptionLabel = new Label(mainDetailsGroup, SWT.NONE);
        final GridData gd_descriptionLabel = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        descriptionLabel.setLayoutData(gd_descriptionLabel);
        descriptionLabel.setText("Description:");

        m_tDescription = new Text(mainDetailsGroup, SWT.BORDER);

        final GridData gd_m_tDescription = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_tDescription.setLayoutData(gd_m_tDescription);

        final Label statusLabel = new Label(mainDetailsGroup, SWT.NONE);
        final GridData gd_statusLabel = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        statusLabel.setLayoutData(gd_statusLabel);
        statusLabel.setText("Status:");

        m_tStatus = new Text(mainDetailsGroup, SWT.BORDER);

        final GridData gd_m_tStatus = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_tStatus.setLayoutData(gd_m_tStatus);

        final Label startTimeLabel = new Label(mainDetailsGroup, SWT.NONE);
        startTimeLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        startTimeLabel.setText("Start time:");

        m_tStartTime = new Text(mainDetailsGroup, SWT.BORDER);

        final GridData gd_m_tStartTime = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_tStartTime.setLayoutData(gd_m_tStartTime);

        final Label endTimeLabel = new Label(mainDetailsGroup, SWT.NONE);
        endTimeLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        endTimeLabel.setText("End time:");

        m_tEndTime = new Text(mainDetailsGroup, SWT.BORDER);

        final GridData gd_m_tEndTime = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_tEndTime.setLayoutData(gd_m_tEndTime);

        final Label versionLabel = new Label(mainDetailsGroup, SWT.NONE);
        final GridData gd_versionLabel = new GridData(SWT.RIGHT, SWT.TOP, false, false);
        versionLabel.setLayoutData(gd_versionLabel);
        versionLabel.setText("Version:");

        m_tVersion = new Text(mainDetailsGroup, SWT.BORDER);

        final GridData gd_m_tVersion = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_tVersion.setLayoutData(gd_m_tVersion);

        final Label startUserLabel = new Label(mainDetailsGroup, SWT.NONE);
        startUserLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        startUserLabel.setText("Start user:");

        m_tStartUser = new Text(mainDetailsGroup, SWT.BORDER);

        final GridData gd_m_tStartUser = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_tStartUser.setLayoutData(gd_m_tStartUser);

        final Label currentOwnerLabel = new Label(mainDetailsGroup, SWT.NONE);
        currentOwnerLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        currentOwnerLabel.setText("Current owner:");

        m_tCurrentOwner = new Text(mainDetailsGroup, SWT.BORDER);

        final GridData gd_m_tCurrentOwner = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_tCurrentOwner.setLayoutData(gd_m_tCurrentOwner);

        final Label processTypeLabel = new Label(mainDetailsGroup, SWT.NONE);
        final GridData gd_processTypeLabel = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        processTypeLabel.setLayoutData(gd_processTypeLabel);
        processTypeLabel.setText("Process type:");

        m_tProcessType = new Text(mainDetailsGroup, SWT.BORDER);

        final GridData gd_m_tProcessType = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_tProcessType.setLayoutData(gd_m_tProcessType);

        final Label priorityLabel = new Label(mainDetailsGroup, SWT.NONE);
        final GridData gd_priorityLabel = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        priorityLabel.setLayoutData(gd_priorityLabel);
        priorityLabel.setText("Priority:");

        m_tProcessPriority = new Text(mainDetailsGroup, SWT.BORDER);

        final GridData gd_m_tProcessPriority = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gd_m_tProcessPriority.widthHint = 570;
        m_tProcessPriority.setLayoutData(gd_m_tProcessPriority);

        final Label bpmnKeyLabel = new Label(mainDetailsGroup, SWT.NONE);
        final GridData gd_bpmnKeyLabel = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        bpmnKeyLabel.setLayoutData(gd_bpmnKeyLabel);
        bpmnKeyLabel.setText("BPMN Key:");

        m_tBPMN = new Text(mainDetailsGroup, SWT.BORDER);

        final GridData gd_m_tBPMN = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_tBPMN.setLayoutData(gd_m_tBPMN);

        final Composite composite_8 = new Composite(mainDetailsGroup, SWT.NONE);
        final GridLayout gridLayout_3 = new GridLayout();
        gridLayout_3.numColumns = 3;
        composite_8.setLayout(gridLayout_3);
        composite_8.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1));

        final Button loadActivityDetailsButton = new Button(composite_8, SWT.NONE);
        loadActivityDetailsButton.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    loadActivityDetails();
                }
            });
        loadActivityDetailsButton.setText("Load activity &details");

        final Button retrieveBpmlButton = new Button(composite_8, SWT.NONE);
        retrieveBpmlButton.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent selectionevent)
                {
                    retrieveBPML();
                }
            });
        retrieveBpmlButton.setText("Retrieve BPM&L");

        final Button retrieveButton = new Button(composite_8, SWT.NONE);
        retrieveButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
        retrieveButton.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent selectionevent)
                {
                    retrieveBPMN();
                }
            });
        retrieveButton.setText("Retrieve BPM&N");

        m_pbLoadingActivities = new ProgressBar(mainDetailsGroup, SWT.NONE);

        final GridData gd_m_pbLoadingActivities = new GridData(SWT.FILL, SWT.CENTER, false, false,
                                                               2, 1);
        m_pbLoadingActivities.setLayoutData(gd_m_pbLoadingActivities);
        sashForm_1.setWeights(new int[] { 194, 720 });
        sashForm.setWeights(new int[] { 117, 355 });
        //
    }

    /**
     * This method will display the details for the currently selected process.
     *
     * @throws  CordysGatewayClientException  DOCUMENTME
     * @throws  CordysSOAPException           DOCUMENTME
     * @throws  TransformerException          DOCUMENTME
     */
    protected void displayProcessDetails()
                                  throws CordysGatewayClientException, CordysSOAPException,
                                         TransformerException
    {
        TableItem[] ati = m_tblAllFlows.getSelection();

        if ((ati != null) && (ati.length > 0))
        {
            cleanDetails();

            String sInstanceID = ati[0].getText(0);

            Element eEnvelope = createQuery("select * from PROCESS_INSTANCE where INSTANCE_ID = '" +
                                            sInstanceID + "'");
            Element eResponse = m_cgcClient.requestFromCordys(eEnvelope);

            NamespaceConstants.registerPrefix("admin",
                                              "http://schemas.cordys.com/bpm/monitoring/1.0");

            PrefixResolver pr = NamespaceConstants.getPrefixResolver();
            Element eProcessInstance = (Element) XPathHelper.selectSingleNode(eResponse,
                                                                              "//admin:tuple/admin:old/admin:PROCESS_INSTANCE",
                                                                              pr);

            if (eProcessInstance == null)
            {
                throw new TransformerException("Could not find the process details.");
            }

            showProcessDetails(eProcessInstance, pr);

            // Now build up the tree, first with the activities. We're going to do this
            // asynchronously.
            m_tiRootItem = new TreeItem(m_tProcessDetails, SWT.NULL);
            m_tiRootItem.setText(m_tDescription.getText());
            m_tiRootItem.setImage(SWTResourceManager.getImage(FlowInfo.class, "icons/bpm.gif"));
            m_tiRootItem.setData("pid", sInstanceID);
            m_tiRootItem.setData("type", "process");
            m_tiRootItem.setData("xml", eProcessInstance);

            loadProcessTree(sInstanceID, m_tiRootItem);
        }
    }

    /**
     * This method gets all flows for the current organization.
     */
    protected void getAllFlows()
    {
        String sOrg = (String) m_cbOrganization.getData(m_cbOrganization.getText());

        try
        {
            Element eEnvelope = createQuery("SELECT INSTANCE_ID, PROCESS_NAME, DESCRIPTION, START_TIME, END_TIME, STATUS FROM PROCESS_INSTANCE WHERE ORGANIZATION = '" +
                                            sOrg + "' ORDER BY START_TIME DESC");
            Element eResponse = m_cgcClient.requestFromCordys(eEnvelope);

            NamespaceConstants.registerPrefix("admin",
                                              "http://schemas.cordys.com/bpm/monitoring/1.0");

            m_tblAllFlows.removeAll();
            cleanDetails();

            NodeList nlInstances = XPathHelper.selectNodeList(eResponse,
                                                              "//admin:tuple/admin:old/admin:PROCESS_INSTANCE",
                                                              NamespaceConstants
                                                              .getPrefixResolver());

            if (nlInstances.getLength() > 0)
            {
                for (int iCount = 0; iCount < nlInstances.getLength(); iCount++)
                {
                    Node nInstance = nlInstances.item(iCount);
                    String sInstance = XPathHelper.getStringValue(nInstance,
                                                                  "admin:INSTANCE_ID/text()");
                    String sName = XPathHelper.getStringValue(nInstance,
                                                              "admin:PROCESS_NAME/text()");

                    String sStartDate = XPathHelper.getStringValue(nInstance,
                                                                   "admin:START_TIME/text()");

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");

                    if ((sStartDate != null) && (sStartDate.length() > 0))
                    {
                        Date dDate = new Date(Long.parseLong(sStartDate));
                        sStartDate = sdf.format(dDate);
                    }

                    String sEndTime = XPathHelper.getStringValue(nInstance,
                                                                 "admin:END_TIME/text()");

                    if ((sEndTime != null) && (sEndTime.length() > 0))
                    {
                        Date dDate = new Date(Long.parseLong(sEndTime));
                        sEndTime = sdf.format(dDate);
                    }

                    String sStatus = XPathHelper.getStringValue(nInstance, "admin:STATUS/text()");

                    TableItem tiNew = new TableItem(m_tblAllFlows, SWT.NONE);
                    tiNew.setText(new String[] { sInstance, sName, sStartDate, sEndTime, sStatus });
                }
                m_tblAllFlows.setSelection(0);
                m_tblAllFlows.setFocus();
            }
        }
        catch (Exception e)
        {
            MessageBoxUtil.showError(m_sShell, "Error getting flow instances", e);
        }
    }

    /**
     * This method handles the response as it was received.
     *
     * @param  ro  The response object.
     */
    protected void handleResponse(RequestObject ro)
    {
        if (!ro.executedOK())
        {
            MessageBoxUtil.showError(m_sShell, "Error executing request", ro.getException());
        }
        else
        {
            try
            {
                switch (ro.getRequestType())
                {
                    case GET_ACTIVITIES:
                        handleGetActivities(ro);
                        break;

                    case GET_SUB_PROCESS:
                        handleGetSubProcess(ro);
                        break;

                    case GET_BPML:
                        handleBPML(ro);
                        break;

                    case GET_BPMN:
                        handleBPMN(ro);
                        break;
                }
            }
            catch (Exception e)
            {
                MessageBoxUtil.showError(m_sShell, "Error handling the sub process details", e);
            }
        }
    }

    /**
     * This method will load all the activity details for the current process instance.It will
     * initialize the tree control and show the details.
     */
    protected void loadActivityDetails()
    {
        try
        {
            String sParentID = m_tDescription.getText() + "_" + m_tVersion.getText() + ".bpm";

            if (m_tiActivityRoot != null)
            {
                m_tiActivityRoot.dispose();
                m_tiActivityRoot = null;
            }

            m_tiActivityRoot = new TreeItem(m_tProcessDetails, SWT.NONE);
            m_tiActivityRoot.setText("Actual activities");
            m_tiActivityRoot.setImage(SWTResourceManager.getImage(FlowInfo.class,
                                                                  "icons/sequence.gif"));
            m_tiActivityRoot.setData("instanceid", m_tInstanceID.getText());
            m_tiActivityRoot.setData("activityid", sParentID);

            // First of all we will get how many records there could be.
            // First of all we will get how many records there could be.
            Element eEnvelope = createQuery("select count(*) NR_OF_ACTIVITIES from PROCESS_ACTIVITY where INSTANCE_ID = '" +
                                            m_tInstanceID.getText() + "'");

            Element eResponse = m_cgcClient.requestFromCordys(eEnvelope);

            int iNrOfActivities = XPathHelper.getIntegerValue(eResponse,
                                                              "//admin:NR_OF_ACTIVITIES/text()",
                                                              false);

            boolean bLoad = true;

            if (iNrOfActivities > 50)
            {
                if (
                    MessageBoxUtil.showConfirmation(m_sShell,
                                                        "There are more then " + 50 +
                                                        " activities. Are you sure you want to load the details?") ==
                        false)
                {
                    bLoad = false;
                }
            }

            // Initialize the progress bar
            m_pbLoadingActivities.setMaximum(iNrOfActivities);
            m_pbLoadingActivities.setSelection(0);

            if (bLoad)
            {
                eEnvelope = createQuery("select * from PROCESS_ACTIVITY where INSTANCE_ID = '" +
                                        m_tInstanceID.getText() + "' and PARENT_ID = '" +
                                        sParentID + "' ORDER BY START_TIME");

                RequestObject ro = new RequestObject(m_cgcClient,
                                                     Long.parseLong(m_tTimeOut.getText()),
                                                     eEnvelope, EDetailRequest.GET_ACTIVITIES,
                                                     m_tiActivityRoot);

                ro.setOrganization((String) m_cbOrganization.getData(m_cbOrganization.getItem(m_cbOrganization
                                                                                              .getSelectionIndex())));

                // Put the request on the queue.
                m_qRequests.put(ro);
            }
        }
        catch (Exception e)
        {
            MessageBoxUtil.showError(m_sShell, "Error loading process tree", e);
        }
    }

    /**
     * This method will load the details on demand.
     *
     * @param  te  The tree event that occurred.
     */
    protected void loadActivityDetails(TreeEvent te)
    {
        TreeItem ti = (TreeItem) te.item;
        TreeItem[] ati = ti.getItems();
        ArrayList<RequestObject> alDetailRequests = new ArrayList<RequestObject>();

        for (TreeItem tiChild : ati)
        {
            RequestObject ro = (RequestObject) tiChild.getData("detailro");

            if (ro != null)
            {
                alDetailRequests.add(ro);
            }
        }

        for (RequestObject roTemp : alDetailRequests)
        {
            try
            {
                // Put the request on the queue.
                m_qRequests.put(roTemp);
            }
            catch (Exception e)
            {
                MessageBoxUtil.showError(m_sShell, "Error loading activity details.");
            }
        }
    }

    /**
     * This method loads the property file is available.
     */
    protected void loadProperties()
    {
        File fMethodTestToolPref = new File(new File(System.getProperty("user.home")),
                                            "coe/coe_mtt/flowinfo.properties");

        // Create the property file.
        Properties pProp = new Properties();

        try
        {
            pProp.load(new FileInputStream(fMethodTestToolPref));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * This method retrieves the BPML for this flow.
     */
    protected void retrieveBPML()
    {
        if (m_tStatus.getText().equals("COMPLETE"))
        {
            MessageBoxUtil.showInformation(m_sShell, "For a completed flow the BPML is not saved.");
        }
        else
        {
            try
            {
                Element eEnvelope = createQuery("SELECT INSTANCE_DATA FROM PROCESS_INSTANCE_DATA WHERE INSTANCE_ID = '" +
                                                m_tInstanceID.getText() + "'");

                RequestObject ro = new RequestObject(m_cgcClient,
                                                     Long.parseLong(m_tTimeOut.getText()),
                                                     eEnvelope, EDetailRequest.GET_BPML, null);

                ro.setOrganization((String) m_cbOrganization.getData(m_cbOrganization.getItem(m_cbOrganization
                                                                                              .getSelectionIndex())));

                // Put the request on the queue.
                m_qRequests.put(ro);
            }
            catch (Exception e)
            {
                MessageBoxUtil.showError(m_sShell, "Error loading BPML", e);
            }
        }
    }

    /**
     * This method retrieves the BPMN for the current process ID.
     */
    protected void retrieveBPMN()
    {
        try
        {
            Element eEnvelope = createGetProcessModel(m_tBPMN.getText(), "organization");

            RequestObject ro = new RequestObject(m_cgcClient, Long.parseLong(m_tTimeOut.getText()),
                                                 eEnvelope, EDetailRequest.GET_BPMN, null);

            ro.setOrganization((String) m_cbOrganization.getData(m_cbOrganization.getItem(m_cbOrganization
                                                                                          .getSelectionIndex())));

            // Put the request on the queue.
            m_qRequests.put(ro);
        }
        catch (Exception e)
        {
            MessageBoxUtil.showError(m_sShell, "Error loading process tree", e);
        }
    }

    /**
     * This method is called when the selection in the tree changes. Based on the node type action
     * is taken to display the appropriate information.
     */
    protected void selectTreeItem()
    {
        TreeItem[] ati = m_tProcessDetails.getSelection();

        if ((ati != null) && (ati.length > 0))
        {
            TreeItem ti = ati[0];

            if ("process".equals((String) ti.getData("type")))
            {
                // It's a process
                try
                {
                    showProcessDetails((Element) ti.getData("xml"),
                                       NamespaceConstants.getPrefixResolver());
                }
                catch (TransformerException e)
                {
                    MessageBoxUtil.showError(m_sShell,
                                             "Error displaying information for selected process");
                }
            }
            else if ("activity".equals((String) ti.getData("type")))
            {
                // It's a process
                try
                {
                    showActivityDetails((Element) ti.getData("xml"),
                                        NamespaceConstants.getPrefixResolver());
                }
                catch (TransformerException e)
                {
                    MessageBoxUtil.showError(m_sShell,
                                             "Error displaying information for selected process");
                }
            }
        }
    }

    /**
     * This method cleans the details section for the process details.
     */
    private void cleanDetails()
    {
        m_tInstanceID.setText("");
        m_tFullProcessName.setText("");
        m_tDescription.setText("");
        m_tStatus.setText("");
        m_tStartTime.setText("");
        m_tEndTime.setText("");
        m_tVersion.setText("");
        m_tStartUser.setText("");
        m_tCurrentOwner.setText("");
        m_tProcessType.setText("");
        m_tProcessPriority.setText("");
        m_tBPMN.setText("");

        m_tInputMessage.setText("");
        m_tErrorMessage.setText("");

        m_tBPMLDetails.setText("");
        m_tBPMNDetails.setText("");
        m_tBPMNDetails.setData(null);

        m_mmbMessageMap = null;
        m_tMessageMapDetail.setText("");

        m_tiRootItem = null;
        m_tiActivityRoot = null;
        m_tProcessDetails.removeAll();

        // Clean activity details.
        m_tActID.setText("");
        m_tActIteration.setText("");
        m_tActName.setText("");
        m_tActType.setText("");
        m_tActParticipant.setText("");
        m_tActResponse.setText("");
        m_tActRequest.setText("");
        m_tActExecutedBy.setText("");
        m_tActEndTime.setText("");
        m_tActStatus.setText("");
        m_tActMessageID.setText("");
        m_tActStartTime.setText("");
    }

    /**
     * This method creates the getProcessModel request.
     *
     * @param   sBPMNKey     The key for the BPMN
     * @param   sModelSpace  The model space (organization/isv)
     *
     * @return  The request.
     *
     * @throws  CordysGatewayClientException  In case of any exception.
     */
    private Element createGetProcessModel(String sBPMNKey, String sModelSpace)
                                   throws CordysGatewayClientException
    {
        Element nReturn = null;

        Element eMessage = m_cgcClient.createMessage("GetProcessModel",
                                                     "http://schemas.cordys.com/bpm/deployment/1.0");

        nReturn = eMessage.getOwnerDocument().getDocumentElement();

        Element eProcessName = XMLHelper.createElementWithParentNS("processname", eMessage);
        eProcessName.setAttribute("bpmn", "true");
        XMLHelper.createText(sBPMNKey, eProcessName);

        Element eModelSpace = XMLHelper.createElementWithParentNS("modelSpace", eMessage);
        XMLHelper.createText(sModelSpace, eModelSpace);

        return nReturn;
    }

    /**
     * THis method creates the method with the given query.
     *
     * @param   sSQL  The SQL to execute.
     *
     * @return  The method that was created.
     *
     * @throws  CordysGatewayClientException
     */
    private Element createQuery(String sSQL)
                         throws CordysGatewayClientException
    {
        Element nReturn = null;

        Element eMessage = m_cgcClient.createMessage("QueryAdminData",
                                                     "http://schemas.cordys.com/bpm/monitoring/1.0");

        nReturn = eMessage.getOwnerDocument().getDocumentElement();

        Element eDataset = XMLHelper.createElementWithParentNS("dataset", eMessage);

        Element eConstructor = XMLHelper.createElementWithParentNS("constructor", eDataset);
        eConstructor.setAttribute("language", "DBSQL");

        Element eCursor = XMLHelper.createElementWithParentNS("cursor", eConstructor);
        eCursor.setAttribute("numRows", "250");

        Element eQuery = XMLHelper.createElementWithParentNS("query", eConstructor);
        XMLHelper.createText(sSQL, eQuery);

        XMLHelper.createElementWithParentNS("parameters", eConstructor);

        return nReturn;
    }

    /**
     * This method will fill the organization and ISV drop down.
     */
    private void fillOrgsAndISVs()
    {
        IUserInfo ui = m_cgcClient.getUserInfo();
        List<IOrganizationInfo> lOrgs = ui.getOrganizations();

        m_cbOrganization.removeAll();

        for (IOrganizationInfo oiInfo : lOrgs)
        {
            m_cbOrganization.add(oiInfo.getDescription());
            m_cbOrganization.setData(oiInfo.getDescription(), oiInfo.getDN());
        }

        m_cbOrganization.select(0);
    }

    /**
     * This method sets the proper image based on the type.
     *
     * @param  tiNew          The tree item to set the image for.
     * @param  sActivityType  The activity type.
     * @param  sActivityName  The name of the activity
     */
    private void findProperImage(TreeItem tiNew, String sActivityType, String sActivityName)
    {
        String sLocation = "icons/activity.gif";

        if ("ACTIVITY".equals(sActivityType) && "Start Event".equals(sActivityName))
        {
            sLocation = "icons/start.gif";
        }
        else if ("END PROCESS".equals(sActivityType) && "End Event".equals(sActivityName))
        {
            sLocation = "icons/end.gif";
        }
        else if ("SEQUENCE".equals(sActivityType))
        {
            sLocation = "icons/sequence.gif";
        }
        else if ("WHILE".equals(sActivityType))
        {
            sLocation = "icons/while.gif";
        }
        else if ("FOR EACH".equals(sActivityType))
        {
            sLocation = "icons/while.gif";
        }
        else if ("WEBSERVICE".equals(sActivityType))
        {
            sLocation = "icons/activity.gif";
        }

        tiNew.setImage(SWTResourceManager.getImage(FlowInfo.class, sLocation));
    }

    /**
     * This method converts the long back to a Java date.
     *
     * @param   sDate  The date to convert.
     *
     * @return  The formatted date.
     */
    private String formatDate(String sDate)
    {
        String sReturn = "";

        if ((sDate != null) && (sDate.length() > 0))
        {
            Date dDate = new Date(Long.parseLong(sDate));
            sReturn = s_sdf.format(dDate);
        }

        return sReturn;
    }

    /**
     * This method formats a string that COULD contain XML.
     *
     * @param   sPossibleXML  The possible XML.
     *
     * @return  The formatted XML.
     */
    private String formatXML(String sPossibleXML)
    {
        String sReturn = sPossibleXML;

        try
        {
            Document dDoc = XMLHelper.createDocumentFromXML(sPossibleXML);

            if (dDoc != null)
            {
                sReturn = NiceDOMWriter.write(dDoc.getDocumentElement(), 4, true, false, false);
            }
        }
        catch (Exception e)
        {
            // Ignore it.
        }

        return sReturn;
    }

    /**
     * This method is called to handle the getprocessmodel response with the BPML.
     *
     * @param   ro  The request object.
     *
     * @throws  TransformerException  In case of any exceptions.
     */
    private void handleBPML(RequestObject ro)
                     throws TransformerException
    {
        PrefixResolver pr = NamespaceConstants.getPrefixResolver();
        String sBPML = XPathHelper.getStringValue(ro.getResponse(),
                                                  "//admin:tuple/admin:old/admin:PROCESS_INSTANCE_DATA/admin:INSTANCE_DATA/text()",
                                                  pr, "");
        m_tBPMLDetails.setText(formatXML(sBPML));
    }

    /**
     * This method is called to handle the getprocessmodel response with the BPMN.
     *
     * @param   ro  The request object.
     *
     * @throws  TransformerException  In case of any exceptions.
     */
    private void handleBPMN(RequestObject ro)
                     throws TransformerException
    {
        Node eBPMN = XPathHelper.selectSingleNode(ro.getResponse(),
                                                  "//bpmdep:data/bpmdep:tuple/bpmdep:old/bpmdep:bizprocess",
                                                  NamespaceConstants.getPrefixResolver());
        m_tBPMNDetails.setText(NiceDOMWriter.write(eBPMN, 4, true, false, false));
        m_tBPMNDetails.setData(eBPMN);
    }

    /**
     * This method handles the response from the getActivities.
     *
     * @param   ro  The request object.
     *
     * @throws  TransformerException  DOCUMENTME
     */
    private void handleGetActivities(RequestObject ro)
                              throws TransformerException
    {
        TreeItem tiParent = ro.getParentTreeItem();
        PrefixResolver pr = NamespaceConstants.getPrefixResolver();
        NodeList nlActivities = XPathHelper.selectNodeList(ro.getResponse(),
                                                           "//admin:tuple/admin:old/admin:PROCESS_ACTIVITY",
                                                           pr);

        if (nlActivities.getLength() > 0)
        {
            IterationCount icCurrent = null;
            TreeItem tiIterationParent = tiParent;

            // Update the progress bar
            m_pbLoadingActivities.setSelection(m_pbLoadingActivities.getSelection() +
                                               nlActivities.getLength());

            for (int iCount = 0; iCount < nlActivities.getLength(); iCount++)
            {
                Element eActivity = (Element) nlActivities.item(iCount);
                String sActivityID = XPathHelper.getStringValue(eActivity,
                                                                "./admin:ACTIVITY_ID/text()", pr,
                                                                "");
                String sIterationCount = XPathHelper.getStringValue(eActivity,
                                                                    "./admin:ITERATION_COUNT/text()",
                                                                    pr, "1");
                String sStatus = XPathHelper.getStringValue(eActivity, "./admin:STATUS/text()", pr,
                                                            "");
                String sParentID = XPathHelper.getStringValue(eActivity, "./admin:PARENT_ID/text()",
                                                              pr, "");
                String sActivityType = XPathHelper.getStringValue(eActivity,
                                                                  "./admin:ACTIVITY_TYPE/text()",
                                                                  pr, "");
                String sActivityName = XPathHelper.getStringValue(eActivity,
                                                                  "./admin:ACTIVITY_NAME/text()",
                                                                  pr, sActivityID);

                // If it's an iteration the parent will be different.
                IterationCount icIteration = IterationCount.getInstance(sIterationCount);
                TreeItem tiRealParent = tiParent;
                boolean bReuse = false;

                if (sIterationCount.indexOf(';') > 0)
                {
                    // A loop was detected, so we need to make sure it is for the proper iteration
                    // We need to be sure that it is a new loop by checking the current iteration
                    // against the parent iteration.
                    if (icIteration.equals(tiParent.getData("iteration")))
                    {
                        // It's the same, so nothing needs to be done.
                    }
                    else
                    {
                        // Different loop.
                        if (icCurrent == null)
                        {
                            // The first entry of the loop was detected
                            icCurrent = icIteration;
                            tiIterationParent = new TreeItem(tiParent, SWT.NONE);
                            tiIterationParent.setData("type", "activity");
                            findProperImage(tiIterationParent, sActivityType, sActivityName);
                            tiIterationParent.setText(sActivityName + ": Iteration " +
                                                      icCurrent.getCount());
                            bReuse = true;
                        }
                        else
                        {
                            // There is already a loop going. We need to move to a different loop if
                            // the current iteration count is bigger.
                            if (!icCurrent.equals(icIteration))
                            {
                                // New iteration count
                                icCurrent = icIteration;
                                tiIterationParent = new TreeItem(tiParent, SWT.NONE);
                                findProperImage(tiIterationParent, sActivityType, sActivityName);
                                tiIterationParent.setText(sActivityName + ": Iteration " +
                                                          icCurrent.getCount());
                                tiIterationParent.setData("type", "activity");
                                bReuse = true;
                            }
                        }
                        tiRealParent = tiIterationParent;
                    }
                }

                // Now we can check whether or not it belongs to the current iteration or not.
                TreeItem tiNew = null;

                if (bReuse == true)
                {
                    tiNew = tiIterationParent;
                }
                else
                {
                    tiNew = new TreeItem(tiRealParent, SWT.NONE);
                    tiNew.setText(sActivityName);
                    tiNew.setData("type", "activity");
                }
                findProperImage(tiNew, sActivityType, sActivityName);

                tiNew.setData("instanceid", tiParent.getData("instanceid"));
                tiNew.setData("activityid", sActivityID);
                tiNew.setData("parentid", sParentID);
                tiNew.setData("iteration", icIteration);
                tiNew.setData("xml", eActivity);

                if (!sStatus.equals("COMPLETE") && !sStatus.equals("ITERATION COMPLETE"))
                {
                    tiNew.setBackground(m_cRed);
                }

                // Check if a sub request is needed.
                int iNrOfChildren = XPathHelper.getIntegerValue(eActivity,
                                                                "./admin:NR_OF_CHILDREN/text()", 1);

                if (iNrOfChildren > 0)
                {
                    String sQuery = "select     pa.*\n" +
                                    ",           (\n" +
                                    "                select      count(*)\n" +
                                    "                from        PROCESS_ACTIVITY pac\n" +
                                    "                where       pac.INSTANCE_ID = '" +
                                    m_tInstanceID.getText() + "'\n" +
                                    "                and         pac.PARENT_ID = pa.ACTIVITY_ID\n" +
                                    "                and         pac.ITERATION_COUNT = pa.ITERATION_COUNT\n" +
                                    "            ) AS NR_OF_CHILDREN\n" +
                                    "from        PROCESS_ACTIVITY pa\n" +
                                    "where       pa.INSTANCE_ID = '" + m_tInstanceID.getText() +
                                    "'\n" +
                                    "and         pa.PARENT_ID = '" + sActivityID + "'\n";

                    if (icCurrent != null)
                    {
                        // It's a Loop, so we need to only get the children for the current
                        // iteration.
                        sQuery += " and (ITERATION_COUNT = '" + icCurrent.toString() +
                                  "' OR ITERATION_COUNT like '" + icCurrent.toString() + ";%')\n";
                    }

                    sQuery += "order by START_TIME, convert(bigint, replace(iteration_count, ';', ''))";

                    // Now kick off the load for the sub activities
                    try
                    {
                        Element eEnvelope = createQuery(sQuery);

                        RequestObject roNew = new RequestObject(m_cgcClient,
                                                                Long.parseLong(m_tTimeOut
                                                                               .getText()),
                                                                eEnvelope,
                                                                EDetailRequest.GET_ACTIVITIES,
                                                                tiNew);

                        roNew.setOrganization((String) m_cbOrganization.getData(m_cbOrganization
                                                                                .getItem(m_cbOrganization
                                                                                         .getSelectionIndex())));
                        tiNew.setData("detailro", roNew);
                    }
                    catch (Exception e)
                    {
                        MessageBoxUtil.showError(m_sShell,
                                                 "Error loading activity details for " +
                                                 sActivityID, e);
                    }
                }
            }

            if (m_pbLoadingActivities.getSelection() >= m_pbLoadingActivities.getMaximum())
            {
                MessageBoxUtil.showInformation(m_sShell, "Finished loading details");
            }
        }
    }

    /**
     * This method handles the response from the getSubProcess.
     *
     * @param   ro  The request object.
     *
     * @throws  TransformerException  In case of any exceptions.
     */
    private void handleGetSubProcess(RequestObject ro)
                              throws TransformerException
    {
        PrefixResolver pr = NamespaceConstants.getPrefixResolver();
        NodeList nlProcessInstance = XPathHelper.selectNodeList(ro.getResponse(),
                                                                "//admin:tuple/admin:old/admin:PROCESS_INSTANCE",
                                                                pr);

        if (nlProcessInstance.getLength() > 0)
        {
            for (int iCount = 0; iCount < nlProcessInstance.getLength(); iCount++)
            {
                Element eProcessInstance = (Element) nlProcessInstance.item(iCount);

                String sInstanceID = XPathHelper.getStringValue(eProcessInstance,
                                                                "admin:INSTANCE_ID/text()", pr, "");
                String sDescription = XPathHelper.getStringValue(eProcessInstance,
                                                                 "admin:DESCRIPTION/text()", pr,
                                                                 "");

                // Now build up the tree, first with the activities. We're going to do this
                // asynchronously.
                TreeItem tiNewItem = new TreeItem(ro.getParentTreeItem(), SWT.NULL);
                tiNewItem.setText(sDescription);
                tiNewItem.setImage(SWTResourceManager.getImage(FlowInfo.class, "icons/bpm.gif"));
                tiNewItem.setData("pid", sInstanceID);
                tiNewItem.setData("type", "process");
                tiNewItem.setData("xml", eProcessInstance);

                // Load the details
                loadProcessTree(sInstanceID, tiNewItem);
            }
        }
    }

    /**
     * This method initiates the loading of the complete tree. This wil lbe done in async mode.
     *
     * @param  sProcessID  The ID of the process.
     * @param  tiParent    The parent tree node.
     */
    private void loadProcessTree(String sProcessID, TreeItem tiParent)
    {
        try
        {
            Element eEnvelope = createQuery("select * from PROCESS_INSTANCE where PARENT_ID = '" +
                                            sProcessID + "' ORDER BY START_TIME");

            RequestObject ro = new RequestObject(m_cgcClient, Long.parseLong(m_tTimeOut.getText()),
                                                 eEnvelope, EDetailRequest.GET_SUB_PROCESS,
                                                 tiParent);

            ro.setOrganization((String) m_cbOrganization.getData(m_cbOrganization.getItem(m_cbOrganization
                                                                                          .getSelectionIndex())));

            // Put the request on the queue.
            m_qRequests.put(ro);
        }
        catch (Exception e)
        {
            MessageBoxUtil.showError(m_sShell, "Error loading process tree", e);
        }
    }

    /**
     * This method shows the details for the given activity.
     *
     * @param   eActivity  The details of the activity.
     * @param   pr         The prefix mapping to use.
     *
     * @throws  TransformerException  In case of any exceptions.
     */
    private void showActivityDetails(Element eActivity, PrefixResolver pr)
                              throws TransformerException
    {
        m_tActID.setText(XPathHelper.getStringValue(eActivity, "admin:ACTIVITY_ID/text()", pr, ""));

        m_tActIteration.setText(XPathHelper.getStringValue(eActivity,
                                                           "admin:ITERATION_COUNT/text()", pr, ""));

        m_tActName.setText(XPathHelper.getStringValue(eActivity, "admin:ACTIVITY_NAME/text()", pr,
                                                      ""));

        m_tActType.setText(XPathHelper.getStringValue(eActivity, "admin:ACTIVITY_TYPE/text()", pr,
                                                      ""));

        m_tActParticipant.setText(formatXML(XPathHelper.getStringValue(eActivity,
                                                                       "admin:PARTICIPANT/text()",
                                                                       pr, "")));

        m_tActResponse.setText(formatXML(XPathHelper.getStringValue(eActivity,
                                                                    "admin:RESPONSE/text()", pr,
                                                                    "")));

        m_tActRequest.setText(formatXML(XPathHelper.getStringValue(eActivity,
                                                                   "admin:REQUEST/text()", pr,
                                                                   "")));

        m_tActExecutedBy.setText(XPathHelper.getStringValue(eActivity, "admin:EXECUTED_BY/text()",
                                                            pr, ""));

        m_tActEndTime.setText(formatDate(XPathHelper.getStringValue(eActivity,
                                                                    "admin:END_TIME/text()", pr,
                                                                    "")));

        m_tActStatus.setText(XPathHelper.getStringValue(eActivity, "admin:STATUS/text()", pr, ""));

        m_tActMessageID.setText(XPathHelper.getStringValue(eActivity, "admin:MESSAGE_ID/text()", pr,
                                                           ""));

        m_tActStartTime.setText(formatDate(XPathHelper.getStringValue(eActivity,
                                                                      "admin:START_TIME/text()", pr,
                                                                      "")));
    }

    /**
     * This method displays the details of the selected process.
     *
     * @param   eProcessInstance  The process instance to display the details of.
     * @param   pr                The prefix resolver to use.
     *
     * @throws  TransformerException  In case of any exceptions.
     */
    private void showProcessDetails(Element eProcessInstance, PrefixResolver pr)
                             throws TransformerException
    {
        m_tInstanceID.setText(XPathHelper.getStringValue(eProcessInstance,
                                                         "admin:INSTANCE_ID/text()", pr, ""));
        m_tFullProcessName.setText(XPathHelper.getStringValue(eProcessInstance,
                                                              "admin:PROCESS_NAME/text()", pr, ""));
        m_tDescription.setText(XPathHelper.getStringValue(eProcessInstance,
                                                          "admin:DESCRIPTION/text()", pr, ""));
        m_tStatus.setText(XPathHelper.getStringValue(eProcessInstance, "admin:STATUS/text()", pr,
                                                     ""));
        m_tStartTime.setText(formatDate(XPathHelper.getStringValue(eProcessInstance,
                                                                   "admin:START_TIME/text()", pr,
                                                                   "")));
        m_tEndTime.setText(formatDate(XPathHelper.getStringValue(eProcessInstance,
                                                                 "admin:END_TIME/text()", pr, "")));
        m_tVersion.setText(XPathHelper.getStringValue(eProcessInstance, "admin:VERSION/text()", pr,
                                                      ""));
        m_tStartUser.setText(XPathHelper.getStringValue(eProcessInstance, "admin:USER_NAME/text()",
                                                        pr, ""));
        m_tCurrentOwner.setText(XPathHelper.getStringValue(eProcessInstance,
                                                           "admin:CURRENT_OWNER/text()", pr, ""));
        m_tProcessType.setText(XPathHelper.getStringValue(eProcessInstance,
                                                          "admin:PROCESS_TYPE/text()", pr, ""));
        m_tProcessPriority.setText(XPathHelper.getStringValue(eProcessInstance,
                                                              "admin:PROCESS_PRIORITY/text()", pr,
                                                              ""));
        m_tBPMN.setText(XPathHelper.getStringValue(eProcessInstance, "admin:BPMN/text()", pr, ""));

        // Get the optional start and error message
        m_tInputMessage.setText(formatXML(XPathHelper.getStringValue(eProcessInstance,
                                                                     "admin:MESSAGE/text()", pr,
                                                                     "")));
        m_tErrorMessage.setText(formatXML(XPathHelper.getStringValue(eProcessInstance,
                                                                     "admin:ERROR_TEXT/text()", pr,
                                                                     "")));

        // Display the message map.
        Document dMessageMap = XMLHelper.createDocumentFromXML(XPathHelper.getStringValue(eProcessInstance,
                                                                                          "admin:MESSAGE_MAP/text()",
                                                                                          pr,
                                                                                          "<messagemap xmlns=\"http://schemas.cordys.com/bpm/instance/1.0\"/>"));
        m_mmbMessageMap = new MessageMapBrowser(dMessageMap, m_tMessageMapDetail, m_tiMMFirst,
                                                m_tiMMPrevious, m_tiMMNext, m_tiMMLast);
    }

    /**
     * This thread receives the requests and displays them.
     *
     * @author  pgussow
     */
    public class ResponseListenThread
        implements Runnable
    {
        /**
         * This method reads the messages from the queue and displays them.
         *
         * @see  java.lang.Runnable#run()
         */
        public void run()
        {
            while (true)
            {
                try
                {
                    final RequestObject ro = m_qResponses.take();

                    m_dDisplay.syncExec(new Runnable()
                        {
                            public void run()
                            {
                                handleResponse(ro);
                            }
                        });
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
