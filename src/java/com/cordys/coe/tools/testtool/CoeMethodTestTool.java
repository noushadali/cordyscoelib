package com.cordys.coe.tools.testtool;

import com.cordys.coe.tools.testtool.methods.IMethodInfo;
import com.cordys.coe.tools.testtool.methods.MethodInfoFactory;
import com.cordys.coe.tools.testtool.methods.XReportMethodInfo;
import com.cordys.coe.util.cgc.CGCFactory;
import com.cordys.coe.util.cgc.CordysGatewayClientException;
import com.cordys.coe.util.cgc.ICordysGatewayClient;
import com.cordys.coe.util.cgc.config.CGCAuthenticationFactory;
import com.cordys.coe.util.cgc.config.CGCConfigFactory;
import com.cordys.coe.util.cgc.config.IAuthenticationConfiguration;
import com.cordys.coe.util.cgc.config.ICGCConfiguration;
import com.cordys.coe.util.cgc.userinfo.IOrganizationInfo;
import com.cordys.coe.util.cgc.userinfo.IUserInfo;
import com.cordys.coe.util.config.IWebGatewayConfiguration;
import com.cordys.coe.util.general.Util;
import com.cordys.coe.util.swt.CGCLoginForm;
import com.cordys.coe.util.swt.InProgressDialog;
import com.cordys.coe.util.swt.MessageBoxUtil;
import com.cordys.coe.util.swt.SWTResourceManager;
import com.cordys.coe.util.swt.SWTUtils;
import com.cordys.coe.util.system.SystemInfo;
import com.cordys.coe.util.xml.dom.NamespaceConstants;
import com.cordys.coe.util.xml.dom.NiceDOMWriter;
import com.cordys.coe.util.xml.dom.XMLHelper;
import com.cordys.coe.util.xml.dom.XPathHelper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

import java.util.concurrent.ArrayBlockingQueue;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import sun.misc.BASE64Decoder;

/**
 * This tool can be used to send messages to the web gateway.
 * 
 * @author $author$
 */
public class CoeMethodTestTool
{
    /** Holds the LDAP namespace. */
    private static final String HTTP_SCHEMAS_CORDYS_COM_1_0_LDAP = "http://schemas.cordys.com/1.0/ldap";
    /** Holds the number of threads that are used. */
    private static final int NR_OF_THREADS = 10;
    /** DOCUMENTME. */
    protected Shell m_sShell;
    /** DOCUMENTME. */
    private ArrayList<RequestRunnerThread> m_alRequestThreads;
    /** DOCUMENTME. */
    private Browser m_bBrowser;
    /** DOCUMENTME. */
    private Button m_bCompose;
    /** DOCUMENTME. */
    private Combo m_cbMethod;
    /** DOCUMENTME. */
    private Combo m_cbMethodLevel;
    /** DOCUMENTME. */
    private Combo m_cbMethodSet;
    /** DOCUMENTME. */
    private Combo m_cbOrganization;
    /** DOCUMENTME. */
    private ICordysGatewayClient m_cgcClient;
    /** DOCUMENTME. */
    private Display m_dDisplay;
    /** Holds the namespaces for the current methodset. */
    private LinkedHashMap<String, String> m_hmMethodSetNamespace = new LinkedHashMap<String, String>();
    /** DOCUMENTME. */
    private InProgressDialog m_ipbProgress;
    /** Holds the queue for the requests. */
    private ArrayBlockingQueue<RequestObject> m_qRequests;
    /** Holds teh queue for the responses. */
    private ArrayBlockingQueue<RequestObject> m_qResponses;
    /** Holds the current LDAP root. */
    private String m_sLDAPRoot;
    /** DOCUMENTME. */
    private Table m_tblHistory;
    /** DOCUMENTME. */
    private TabFolder m_tfTabs;
    /** DOCUMENTME. */
    private Text m_tHistRequest;
    /** DOCUMENTME. */
    private Text m_tHistResponse;
    /** DOCUMENTME. */
    private TabItem m_tiRequest;
    /** DOCUMENTME. */
    private TabItem m_tiResponse;
    /** DOCUMENTME. */
    private TabItem m_tiXReport;
    /** DOCUMENTME. */
    private Text m_tPDFLocation;
    /** DOCUMENTME. */
    private Text m_tRequestXML;
    /** DOCUMENTME. */
    private Text m_tResponse;
    /** DOCUMENTME. */
    private Thread m_tResponseListener;
    /** DOCUMENTME. */
    private Text m_tTimeOut;

    /**
     * Launch the application.
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            BasicConfigurator.configure();
            Logger.getRootLogger().setLevel(Level.FATAL);

            CoeMethodTestTool window = new CoeMethodTestTool();
            window.open();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Open the window.
     */
    public void open()
    {
        m_dDisplay = Display.getDefault();
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

            m_ipbProgress = new InProgressDialog(m_sShell, "Please wait", "Connecting to Cordys...\nPlease wait");
            m_ipbProgress.open();
            new Thread(new Runnable() {
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
     * This method clears all outputs.
     */
    protected void clearAllOutput()
    {
        m_tResponse.setText("");
        m_tHistRequest.setText("");
        m_tHistResponse.setText("");

        m_tblHistory.removeAll();
    }

    /**
     * This method composes the new request. For now it will be without looking at the WSDL.
     */
    protected void composeRequest()
    {
        String sMethod = m_cbMethod.getItem(m_cbMethod.getSelectionIndex());
        IMethodInfo miInfo = (IMethodInfo) m_cbMethod.getData(sMethod);

        m_tRequestXML
                .setText(miInfo.composeNewRequest(m_cgcClient, (String) m_cbOrganization.getData(m_cbOrganization.getText())));

        m_tfTabs.setSelection(m_tiRequest);
    }

    /**
     * This method makes the connections.
     * 
     * @param wgcConfig The configuration to use.
     */
    protected void connectToCordys(IWebGatewayConfiguration wgcConfig)
    {
        try
        {
            IAuthenticationConfiguration iaAuth = CGCAuthenticationFactory.createAuthentication(wgcConfig);
            final ICGCConfiguration icConfig = CGCConfigFactory.createConfiguration(wgcConfig);

            m_cgcClient = CGCFactory.createCGC(iaAuth, icConfig);
            m_cgcClient.setNamespaceAwareResponses(true);
            m_cgcClient.setAutoParseGetUserDetails(false);
            m_cgcClient.connect();
            

            m_dDisplay.asyncExec(new Runnable() {
                public void run()
                {
                    m_sShell.setText("CoE web service operation test tool - " + icConfig.getDisplayURL());
                    
                    m_ipbProgress.setDetail("Initializing CoEMethodTestTool");

                    String sAuthDN = m_cgcClient.getAuthUserDN();
                    // Now get the LDAP root from it.
                    m_sLDAPRoot = sAuthDN.substring(sAuthDN.indexOf("cn=cordys,"));

                    fillOrgsAndISVs();

                    m_bCompose.setFocus();

                    // Create the needed queues and threads.
                    m_qRequests = new ArrayBlockingQueue<RequestObject>(50);
                    m_qResponses = new ArrayBlockingQueue<RequestObject>(50);

                    m_alRequestThreads = new ArrayList<RequestRunnerThread>();

                    for (int iCount = 0; iCount < NR_OF_THREADS; iCount++)
                    {
                        RequestRunnerThread rrt = new RequestRunnerThread(m_qRequests, m_qResponses);
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
        m_sShell = new Shell();
        m_sShell.setImage(SWTResourceManager.getImage(CoeMethodTestTool.class, "methodtesttool.gif"));
        m_sShell.addShellListener(new ShellAdapter() {
            public void shellClosed(final ShellEvent e)
            {
                storeEntries();

                System.exit(0);
            }
        });
        m_sShell.setLayout(new GridLayout());
        m_sShell.setSize(952, 733);
        m_sShell.setText("CoE method Test Tool");

        final Group selectMethodToGroup = new Group(m_sShell, SWT.NONE);
        final GridLayout gridLayout_1 = new GridLayout();
        gridLayout_1.numColumns = 3;
        selectMethodToGroup.setLayout(gridLayout_1);
        selectMethodToGroup.setText(" Select method to test ");

        final GridData gd_selectMethodToGroup = new GridData(SWT.FILL, SWT.CENTER, false, false);
        selectMethodToGroup.setLayoutData(gd_selectMethodToGroup);

        final Label organizationLabel = new Label(selectMethodToGroup, SWT.NONE);
        organizationLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        organizationLabel.setText("Organization:");

        m_cbOrganization = new Combo(selectMethodToGroup, SWT.READ_ONLY);
        m_cbOrganization.addModifyListener(new ModifyListener() {
            public void modifyText(final ModifyEvent arg0)
            {
                fillMethodLevels();
            }
        });

        final GridData gd_m_cbOrganization = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        m_cbOrganization.setLayoutData(gd_m_cbOrganization);

        final Label methodLevelLabel = new Label(selectMethodToGroup, SWT.NONE);
        methodLevelLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        methodLevelLabel.setText("Method level");

        m_cbMethodLevel = new Combo(selectMethodToGroup, SWT.READ_ONLY);
        m_cbMethodLevel.addModifyListener(new ModifyListener() {
            public void modifyText(final ModifyEvent arg0)
            {
                fillMethodSets();
            }
        });

        final GridData gd_m_cbMethodLevel = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        m_cbMethodLevel.setLayoutData(gd_m_cbMethodLevel);

        final Label methodSetLabel = new Label(selectMethodToGroup, SWT.NONE);
        methodSetLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        methodSetLabel.setText("Method Set:");

        m_cbMethodSet = new Combo(selectMethodToGroup, SWT.READ_ONLY);
        m_cbMethodSet.addModifyListener(new ModifyListener() {
            public void modifyText(final ModifyEvent arg0)
            {
                fillMethods();
            }
        });

        final GridData gd_m_cbMethodSet = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        gd_m_cbMethodSet.widthHint = 397;
        m_cbMethodSet.setLayoutData(gd_m_cbMethodSet);

        final Label methodLabel = new Label(selectMethodToGroup, SWT.NONE);
        methodLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        methodLabel.setText("Method:");

        m_cbMethod = new Combo(selectMethodToGroup, SWT.READ_ONLY);

        final GridData gd_m_cbMethod = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        m_cbMethod.setLayoutData(gd_m_cbMethod);

        final Label timeoutLabel = new Label(selectMethodToGroup, SWT.NONE);
        timeoutLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        timeoutLabel.setText("Timeout:");

        m_tTimeOut = new Text(selectMethodToGroup, SWT.BORDER);
        m_tTimeOut.setText("30000");

        final GridData gd_m_tTimeOut = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_tTimeOut.setLayoutData(gd_m_tTimeOut);

        m_bCompose = new Button(selectMethodToGroup, SWT.NONE);
        m_bCompose.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e)
            {
                composeRequest();
            }
        });

        final GridData gd_composeButton = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        gd_composeButton.widthHint = 70;
        m_bCompose.setLayoutData(gd_composeButton);
        m_bCompose.setText("Compose");
        //

        final Composite composite_3 = new Composite(m_sShell, SWT.NONE);
        composite_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final GridLayout gridLayout_2 = new GridLayout();
        gridLayout_2.numColumns = 2;
        composite_3.setLayout(gridLayout_2);

        final Button button = new Button(composite_3, SWT.NONE);
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e)
            {
                sendRequest();
            }
        });
        button.setText("Send");

        final Button button_1 = new Button(composite_3, SWT.NONE);
        button_1.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e)
            {
                clearAllOutput();
            }
        });
        button_1.setText("Clear");

        m_tfTabs = new TabFolder(m_sShell, SWT.NONE);

        final GridData gd_m_tfTabs = new GridData(SWT.FILL, SWT.FILL, true, true);
        m_tfTabs.setLayoutData(gd_m_tfTabs);

        m_tiRequest = new TabItem(m_tfTabs, SWT.NONE);
        m_tiRequest.setText("Request");

        final Composite composite = new Composite(m_tfTabs, SWT.NONE);
        composite.setLayout(new GridLayout());
        m_tiRequest.setControl(composite);

        m_tRequestXML = new Text(composite, SWT.V_SCROLL | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);
        m_tRequestXML.setFont(SWTResourceManager.getFont("Courier New", 8, SWT.NONE));

        final GridData gd_m_tRequestEXL = new GridData(SWT.FILL, SWT.FILL, true, true);
        m_tRequestXML.setLayoutData(gd_m_tRequestEXL);

        m_tiResponse = new TabItem(m_tfTabs, SWT.NONE);
        m_tiResponse.setText("Response");

        final Composite composite_1 = new Composite(m_tfTabs, SWT.NONE);
        composite_1.setLayout(new GridLayout());
        m_tiResponse.setControl(composite_1);

        m_tResponse = new Text(composite_1, SWT.V_SCROLL | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);
        m_tResponse.setFont(SWTResourceManager.getFont("Courier New", 8, SWT.NONE));

        final GridData gd_m_tResponse = new GridData(SWT.FILL, SWT.FILL, true, true);
        m_tResponse.setLayoutData(gd_m_tResponse);

        final TabItem m_tiHistory = new TabItem(m_tfTabs, SWT.NONE);
        m_tiHistory.setText("History");

        final Composite composite_2 = new Composite(m_tfTabs, SWT.NONE);
        composite_2.setLayout(new GridLayout());
        m_tiHistory.setControl(composite_2);

        final SashForm sashForm = new SashForm(composite_2, SWT.NONE);
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        sashForm.setOrientation(SWT.VERTICAL);

        m_tblHistory = new Table(sashForm, SWT.FULL_SELECTION | SWT.BORDER);
        m_tblHistory.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent selectionevent)
            {
                showHistoryRequestResponse();
            }
        });
        m_tblHistory.setLinesVisible(true);
        m_tblHistory.setHeaderVisible(true);

        final TableColumn newColumnTableColumn = new TableColumn(m_tblHistory, SWT.NONE);
        newColumnTableColumn.setWidth(365);
        newColumnTableColumn.setText("Method");

        final TableColumn newColumnTableColumn_1 = new TableColumn(m_tblHistory, SWT.NONE);
        newColumnTableColumn_1.setWidth(100);
        newColumnTableColumn_1.setText("Start");

        final TableColumn newColumnTableColumn_2 = new TableColumn(m_tblHistory, SWT.NONE);
        newColumnTableColumn_2.setWidth(100);
        newColumnTableColumn_2.setText("End");

        final TableColumn newColumnTableColumn_3 = new TableColumn(m_tblHistory, SWT.NONE);
        newColumnTableColumn_3.setWidth(100);
        newColumnTableColumn_3.setText("Duration");

        final TableColumn newColumnTableColumn_4 = new TableColumn(m_tblHistory, SWT.NONE);
        newColumnTableColumn_4.setWidth(100);
        newColumnTableColumn_4.setText("Status");

        final TabFolder tabFolder = new TabFolder(sashForm, SWT.NONE);

        final TabItem m_tiHistRequest = new TabItem(tabFolder, SWT.NONE);
        m_tiHistRequest.setText("Request");

        m_tHistRequest = new Text(tabFolder, SWT.V_SCROLL | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);
        m_tHistRequest.setFont(SWTResourceManager.getFont("Courier New", 8, SWT.NONE));
        m_tiHistRequest.setControl(m_tHistRequest);

        final TabItem m_tiHistResponse = new TabItem(tabFolder, SWT.NONE);
        m_tiHistResponse.setText("Response");

        m_tHistResponse = new Text(tabFolder, SWT.V_SCROLL | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);
        m_tHistResponse.setFont(SWTResourceManager.getFont("Courier New", 8, SWT.NONE));
        m_tiHistResponse.setControl(m_tHistResponse);
        sashForm.setWeights(new int[] { 1, 1 });

        m_tiXReport = new TabItem(m_tfTabs, SWT.NONE);
        m_tiXReport.setText("XReport");

        final Composite composite_4 = new Composite(m_tfTabs, SWT.NONE);
        final GridLayout gridLayout_3 = new GridLayout();
        gridLayout_3.numColumns = 2;
        composite_4.setLayout(gridLayout_3);
        m_tiXReport.setControl(composite_4);

        final Label pdfLocationLabel = new Label(composite_4, SWT.NONE);
        pdfLocationLabel.setText("PDF location");

        m_tPDFLocation = new Text(composite_4, SWT.BORDER);

        final GridData gd_m_tPDFLocation = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_tPDFLocation.setLayoutData(gd_m_tPDFLocation);

        m_bBrowser = new Browser(composite_4, SWT.NONE);

        final GridData gd_m_bBrowser = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
        m_bBrowser.setLayoutData(gd_m_bBrowser);
        m_bBrowser.setUrl("http://www.eclipse.org");
        //
        m_tRequestXML.setText(SystemInfo.getSystemInformation());
    }

    /**
     * This method fills the method levels.
     */
    protected void fillMethodLevels()
    {
        if (m_cbOrganization.getSelectionIndex() > -1)
        {
            String sOrganizationDN = (String) m_cbOrganization.getData(m_cbOrganization.getItem(m_cbOrganization
                    .getSelectionIndex()));
            m_cbMethodLevel.removeAll();
            m_hmMethodSetNamespace.clear();

            // Add the organizational level.
            m_cbMethodLevel.add("Organizational Level");
            m_cbMethodLevel.setData("Organizational Level", sOrganizationDN);

            try
            {
                Element eMessage = m_cgcClient.createMessage("GetSoftwarePackages", HTTP_SCHEMAS_CORDYS_COM_1_0_LDAP);
                Element eDN = XMLHelper.createElementNS("dn", HTTP_SCHEMAS_CORDYS_COM_1_0_LDAP, eMessage);
                XMLHelper.createText(m_sLDAPRoot, eDN);

                Element eSort = XMLHelper.createElementNS("sort", HTTP_SCHEMAS_CORDYS_COM_1_0_LDAP, eMessage);
                XMLHelper.createText("ascending", eSort);

                Element eResponse = m_cgcClient.requestFromCordys(eMessage.getOwnerDocument().getDocumentElement());

                String sPrefix = NamespaceConstants.registerPrefix("ldap10", HTTP_SCHEMAS_CORDYS_COM_1_0_LDAP);

                NodeList nlEntries = XPathHelper.prSelectNodeList(eResponse, ".//" + sPrefix + ":tuple/" + sPrefix + ":old/"
                        + sPrefix + ":entry");

                for (int iCount = 0; iCount < nlEntries.getLength(); iCount++)
                {
                    Element eEntry = (Element) nlEntries.item(iCount);

                    String sDN = eEntry.getAttribute("dn");
                    Node nName = XPathHelper.prSelectSingleNode(eEntry, "./" + sPrefix + ":cn/" + sPrefix + ":string/text()");
                    String sFriendlyName = sDN;

                    if (nName != null)
                    {
                        sFriendlyName = nName.getNodeValue();
                    }

                    m_cbMethodLevel.add("ISV: " + sFriendlyName);
                    m_cbMethodLevel.setData("ISV: " + sFriendlyName, sDN);
                }

                m_cbMethodLevel.select(0);
                fillMethodSets();
            }
            catch (Exception e)
            {
                MessageBoxUtil.showError(m_sShell, "Error getting ISV packages from Cordys", e);
            }
        }
    }

    /**
     * This method handles the response as it was received.
     * 
     * @param ro The response object.
     */
    protected void handleResponse(RequestObject ro)
    {
        TableItem ti = ro.getTableItem();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");

        ti.setText(new String[] { ro.getMethodName(), sdf.format(ro.getStartTime()), sdf.format(ro.getEndTime()),
                String.valueOf(ro.getDuration()), ro.executedOK() ? "OK" : "Failed" });
        ti.setData(ro);

        if (ro.executedOK())
        {
            // Request executed ok, so we have an actual response.
            IMethodInfo mi = ro.getMethodInfo();

            m_tResponse.setText(NiceDOMWriter.write(ro.getResponse()));

            if (mi instanceof XReportMethodInfo)
            {
                try
                {
                    handleXReportResponse(mi, ro.getRequest(), ro.getResponse());
                }
                catch (Exception e)
                {
                    m_tResponse.setText(Util.getStackTrace(e));
                    m_tfTabs.setSelection(m_tiResponse);
                }
            }
            else
            {
                m_tfTabs.setSelection(m_tiResponse);
            }
        }
        else
        {
            if (ro.getException() != null)
            {
                m_tResponse.setText(Util.getStackTrace(ro.getException()));
            }
            else
            {
                m_tResponse.setText("Unknown error occurred");
            }

            m_tfTabs.setSelection(m_tiResponse);
        }
    }

    /**
     * This method handles responses from an XReport method.
     * 
     * @param miMethodInfo The method information.
     * @param eRequest The request XML.
     * @param eResponse The response XML.
     * @throws Exception In case of any errors.
     */
    protected void handleXReportResponse(IMethodInfo miMethodInfo, Element eRequest, Element eResponse) throws Exception
    {
        // Check to see if the output was XReports.
        m_tfTabs.setSelection(m_tiXReport);

        String sPrefix = NamespaceConstants.registerPrefix("somepref", miMethodInfo.getNamespace());

        Node nFormat = XPathHelper.prSelectSingleNode(eRequest, ".//" + sPrefix + ":outputformat/text()");
        String sFormat = "html";

        if (nFormat != null)
        {
            sFormat = nFormat.getNodeValue();
        }

        Node nData = XPathHelper.prSelectSingleNode(eResponse, "//" + sPrefix + ":XReport/text()");

        if (nData != null)
        {
            String sResult = nData.getNodeValue();

            if ("html".equalsIgnoreCase(sFormat))
            {
                m_bBrowser.setText(sResult);
            }
            else if ("pdf".equalsIgnoreCase(sFormat))
            {
                // Write it to a temp file and start it.

                File fTempFolder = new File(System.getProperty("java.io.tmpdir"));
                File fPDF = new File(fTempFolder, "" + System.currentTimeMillis() + ".pdf");
                FileOutputStream fos = null;

                try
                {
                    fos = new FileOutputStream(fPDF, false);

                    ByteArrayInputStream bais = new ByteArrayInputStream(sResult.getBytes("UTF-8"));
                    BASE64Decoder bdDecoder = new BASE64Decoder();
                    bdDecoder.decodeBuffer(bais, fos);
                }
                finally
                {
                    if (fos != null)
                    {
                        fos.close();
                    }
                }

                // Start it
                m_tPDFLocation.setText(fPDF.getCanonicalPath());

                m_bBrowser.setUrl(fPDF.toURI().toURL().toExternalForm());
            }
        }
    }

    /**
     * This method loads the property file is available.
     */
    protected void loadProperties()
    {
        File fMethodTestToolPref = new File(new File(System.getProperty("user.home")), "coe/coe_mtt/methodtesttool.properties");

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
     * This method will actually send the request.
     */
    protected void sendRequest()
    {
        try
        {
            Document dDoc = XMLHelper.createDocumentFromXMLExc(m_tRequestXML.getText(), true);

            String sMethod = m_cbMethod.getItem(m_cbMethod.getSelectionIndex());
            IMethodInfo miInfo = (IMethodInfo) m_cbMethod.getData(sMethod);
            sMethod = miInfo.getNamespace() + ":" + miInfo.getName();

            RequestObject ro = new RequestObject(m_cgcClient, Long.parseLong(m_tTimeOut.getText()), dDoc.getDocumentElement(),
                    sMethod);
            ro.setOrganization((String) m_cbOrganization.getData(m_cbOrganization.getItem(m_cbOrganization.getSelectionIndex())));
            ro.setMethodInfo(miInfo);

            TableItem ti = new TableItem(m_tblHistory, SWT.NONE);

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");

            ti.setText(new String[] { ro.getMethodName(), sdf.format(new Date()), "", "", "In progress" });
            ti.setData(ro);
            ro.setTableItem(ti);

            // Put the request on the queue.
            m_qRequests.put(ro);
        }
        catch (Exception e)
        {
            m_tResponse.setText(Util.getStackTrace(e));
            m_tfTabs.setSelection(m_tiResponse);
        }
    }

    /**
     * This method shows the request and the response as they are in the history table.
     */
    protected void showHistoryRequestResponse()
    {
        TableItem[] ati = m_tblHistory.getSelection();

        if (ati.length > 0)
        {
            TableItem ti = ati[0];
            RequestObject ro = (RequestObject) ti.getData();

            if (ro != null)
            {
                m_tHistRequest.setText(NiceDOMWriter.write(ro.getRequest()));

                if (ro.getResponse() != null)
                {
                    m_tHistResponse.setText(NiceDOMWriter.write(ro.getResponse()));
                }
                else
                {
                    if (ro.executedOK() == false)
                    {
                        StringBuffer sbTemp = new StringBuffer(1048);
                        sbTemp.append("Request failed\n\n");

                        if (ro.getException() != null)
                        {
                            sbTemp.append(Util.getStackTrace(ro.getException()));
                        }
                        m_tHistResponse.setText(sbTemp.toString());
                    }
                }
            }
        }
    }

    /**
     * This method will store the credentials on the disk.
     */
    protected void storeEntries()
    {
        File fMethodTestToolPref = new File(new File(System.getProperty("user.home")), "coe");

        if (!fMethodTestToolPref.exists())
        {
            fMethodTestToolPref.mkdirs();
        }

        fMethodTestToolPref = new File(fMethodTestToolPref, "coe_mtt");

        if (!fMethodTestToolPref.exists())
        {
            fMethodTestToolPref.mkdirs();
        }

        File fProps = new File(fMethodTestToolPref, "methodtesttool.properties");

        // Create the property file.
        Properties pProp = new Properties();

        try
        {
            pProp.store(new FileOutputStream(fProps), "");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * This method retrieves the methodsets for the given organization/isv.
     */
    private void fillMethods()
    {
        if (m_cbMethodSet.getSelectionIndex() > -1)
        {
            String sDN = (String) m_cbMethodSet.getData(m_cbMethodSet.getItem(m_cbMethodSet.getSelectionIndex()));
            m_cbMethod.removeAll();

            String sNamespace = m_hmMethodSetNamespace.get(m_cbMethodSet.getItem(m_cbMethodSet.getSelectionIndex()));

            try
            {
                Element eMessage = m_cgcClient.createMessage("GetChildren", HTTP_SCHEMAS_CORDYS_COM_1_0_LDAP);
                Element eDN = XMLHelper.createElementNS("dn", HTTP_SCHEMAS_CORDYS_COM_1_0_LDAP, eMessage);
                XMLHelper.createText(sDN, eDN);

                Element eSort = XMLHelper.createElementNS("sort", HTTP_SCHEMAS_CORDYS_COM_1_0_LDAP, eMessage);
                XMLHelper.createText("ascending", eSort);

                Element eResponse = m_cgcClient.requestFromCordys(eMessage.getOwnerDocument().getDocumentElement());

                String sPrefix = NamespaceConstants.registerPrefix("ldap10", HTTP_SCHEMAS_CORDYS_COM_1_0_LDAP);

                NodeList nlEntries = XPathHelper.prSelectNodeList(eResponse, ".//" + sPrefix + ":tuple/" + sPrefix + ":old/"
                        + sPrefix + ":entry");

                for (int iCount = 0; iCount < nlEntries.getLength(); iCount++)
                {
                    Element eEntry = (Element) nlEntries.item(iCount);

                    String sMethodDN = eEntry.getAttribute("dn");
                    Node nName = XPathHelper.prSelectSingleNode(eEntry, "./" + sPrefix + ":cn/" + sPrefix + ":string/text()");
                    String sFriendlyName = sMethodDN;

                    if (nName != null)
                    {
                        sFriendlyName = nName.getNodeValue();
                    }

                    m_cbMethod.add(sFriendlyName);

                    IMethodInfo miInfo = MethodInfoFactory.createMethodInfo(eEntry, sNamespace);
                    m_cbMethod.setData(sFriendlyName, miInfo);
                }

                m_cbMethod.select(0);
            }
            catch (Exception e)
            {
                MessageBoxUtil.showError(m_sShell, "Error getting method sets from Cordys", e);
            }
        }
    }

    /**
     * This method retrieves the methodsets for the given organization/isv.<br>
     * 
     * <pre>
     *        <SOAP:Envelope xmlns:SOAP="http://schemas.xmlsoap.org/soap/envelope/" url="com.eibus.web.soap.Gateway.wcp?organization=o%3DXReports%2Ccn%3Dcordys%2Co%3Dvanenburg.com&amp;messageOptions=0">
     *          <SOAP:Body>
     *            <GetMethodSets xmlns="http://schemas.cordys.com/1.0/ldap">
     *              <dn>cn=Cordys EJB Connector,cn=cordys,o=vanenburg.com</dn>
     *              <labeleduri>*</labeleduri>
     *              <sort>ascending</sort>
     *            </GetMethodSets>
     *          </SOAP:Body>
     *        </SOAP:Envelope>
     * </pre>
     */
    private void fillMethodSets()
    {
        if (m_cbMethodLevel.getSelectionIndex() > -1)
        {
            String sDN = (String) m_cbMethodLevel.getData(m_cbMethodLevel.getItem(m_cbMethodLevel.getSelectionIndex()));
            m_cbMethodSet.removeAll();
            m_hmMethodSetNamespace.clear();

            try
            {
                Element eMessage = m_cgcClient.createMessage("GetMethodSets", HTTP_SCHEMAS_CORDYS_COM_1_0_LDAP);
                Element eDN = XMLHelper.createElementNS("dn", HTTP_SCHEMAS_CORDYS_COM_1_0_LDAP, eMessage);
                XMLHelper.createText(sDN, eDN);

                Element eLabeledURI = XMLHelper.createElementNS("labeleduri", HTTP_SCHEMAS_CORDYS_COM_1_0_LDAP, eMessage);
                XMLHelper.createText("*", eLabeledURI);

                Element eSort = XMLHelper.createElementNS("sort", HTTP_SCHEMAS_CORDYS_COM_1_0_LDAP, eMessage);
                XMLHelper.createText("ascending", eSort);

                Element eResponse = m_cgcClient.requestFromCordys(eMessage.getOwnerDocument().getDocumentElement());

                String sPrefix = NamespaceConstants.registerPrefix("ldap10", HTTP_SCHEMAS_CORDYS_COM_1_0_LDAP);

                NodeList nlEntries = XPathHelper.prSelectNodeList(eResponse, ".//" + sPrefix + ":tuple/" + sPrefix + ":old/"
                        + sPrefix + ":entry");

                for (int iCount = 0; iCount < nlEntries.getLength(); iCount++)
                {
                    Element eEntry = (Element) nlEntries.item(iCount);

                    String sMethodSetDN = eEntry.getAttribute("dn");
                    Node nName = XPathHelper.prSelectSingleNode(eEntry, "./" + sPrefix + ":cn/" + sPrefix + ":string/text()");
                    String sFriendlyName = sMethodSetDN;

                    if (nName != null)
                    {
                        sFriendlyName = nName.getNodeValue();
                    }

                    Node nNamespace = XPathHelper.prSelectSingleNode(eEntry, "./" + sPrefix + ":labeleduri/" + sPrefix
                            + ":string/text()");

                    if (nNamespace != null)
                    {
                        m_hmMethodSetNamespace.put(sFriendlyName, nNamespace.getNodeValue());
                    }

                    m_cbMethodSet.add(sFriendlyName);
                    m_cbMethodSet.setData(sFriendlyName, sMethodSetDN);
                }

                m_cbMethodSet.select(0);
                fillMethods();
            }
            catch (Exception e)
            {
                MessageBoxUtil.showError(m_sShell, "Error getting method sets from Cordys", e);
            }
        }
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

        fillMethodSets();
    }

    /**
     * This thread receives the requests and displays them.
     * 
     * @author pgussow
     */
    public class ResponseListenThread implements Runnable
    {
        /**
         * This method reads the messages from the queue and displays them.
         * 
         * @see java.lang.Runnable#run()
         */
        public void run()
        {
            while (true)
            {
                try
                {
                    final RequestObject ro = m_qResponses.take();

                    m_dDisplay.syncExec(new Runnable() {
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
