package com.cordys.coe.tools.jmx;

import com.cordys.coe.tools.jmx.factory.AttributeControlFactory;
import com.cordys.coe.tools.jmx.resources.MBeanAttributeInfoWrapper;
import com.cordys.coe.tools.jmx.resources.MBeanInfoWrapper;
import com.cordys.coe.tools.jmx.resources.MBeanOperationInfoWrapper;
import com.cordys.coe.tools.jmx.tables.AttrDetailsComposite;
import com.cordys.coe.tools.jmx.tables.MBeanAttributesTable;
import com.cordys.coe.tools.jmx.tables.MBeanOperationsTable;
import com.cordys.coe.tools.jmx.tree.DomainNode;
import com.cordys.coe.tools.jmx.tree.Node;
import com.cordys.coe.tools.jmx.tree.NodeUtils;
import com.cordys.coe.tools.jmx.tree.ObjectNameNode;
import com.cordys.coe.util.general.Util;
import com.cordys.coe.util.swt.MessageBoxUtil;
import com.cordys.coe.util.swt.SWTUtils;

import java.io.File;
import java.io.FileInputStream;

import java.lang.reflect.Method;

import java.net.URLDecoder;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import javax.management.openmbean.CompositeData;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

import sun.misc.BASE64Decoder;

/**
 * This composite holds all the functionality of the JMX tool.
 *
 * @author  pgussow
 */
public class CordysCoEJMXViewerComposite extends Composite
    implements IUpdateAttributeDetails
{
    /**
     * Holds the JMX port.
     */
    private Text m_BBPort;
    /**
     * Holds all the operations for this mBean.
     */
    private MBeanOperationsTable m_botOperations;
    /**
     * Holds teh details of an attribute.
     */
    private AttrDetailsComposite m_cMainAttrDetails;
    /**
     * Group for the input parameters.
     */
    private Group m_gOpInputParam;
    /**
     * Group for the operations output parameters.
     */
    private Group m_gOpOutput;
    /**
     * Holds the connector to the JMX url.
     */
    private JMXConnector m_jcConnector;
    /**
     * Holds the composite taking care of the notifications.
     */
    private NotificationComposite m_ncNotifications;
    /**
     * Holds the handler for the operations.
     */
    private OperationsHandler m_ohOperationHandler;
    /**
     * Holds the JMX password.
     */
    private Text m_tBBPassword;
    /**
     * Holds teh servename.
     */
    private Text m_tBBServer;
    /**
     * Holds the username.
     */
    private Text m_tBBUsername;
    /**
     * Holds all exposed service URLs.
     */
    private Table m_tblBBProcessors;
    /**
     * Holds the tree with all the mbeans.
     */
    private Tree m_tMBeanTree;
    /**
     * Holds the name of the object.
     */
    private Text m_tObjectName;
    /**
     * Holds teh password.
     */
    private Text m_tPassword;
    /**
     * Holds teh service URL.
     */
    private Text m_tServiceURL;
    /**
     * Holds the username.
     */
    private Text m_tUsername;
    /**
     * Holds the viewer with all the attributes.
     */
    private Viewer m_tvAttributes;
    /**
     * Holds the viewer for the mbeans.
     */
    private TreeViewer m_tvMBeanTree;

    /**
     * Creates a new CordysCoEJMXViewerComposite object.
     *
     * @param  cParent  The parent compisite.
     * @param  iStyle   The SWT style to use.
     */
    public CordysCoEJMXViewerComposite(Composite cParent, int iStyle)
    {
        super(cParent, iStyle);
        setLayout(new GridLayout());

        final CTabFolder tabFolder_1 = new CTabFolder(this, SWT.NONE);
        tabFolder_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        final CTabItem byBrowsingTabItem = new CTabItem(tabFolder_1, SWT.NONE);
        byBrowsingTabItem.setText("By Browsing (Cordys)");

        final Composite composite_5 = new Composite(tabFolder_1, SWT.NONE);
        final GridLayout gridLayout_2 = new GridLayout();
        gridLayout_2.numColumns = 5;
        composite_5.setLayout(gridLayout_2);
        byBrowsingTabItem.setControl(composite_5);

        final Label serverLabel = new Label(composite_5, SWT.NONE);
        serverLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        serverLabel.setText("Server:");

        m_tBBServer = new Text(composite_5, SWT.BORDER);
        m_tBBServer.setText("127.0.0.1");

        final GridData gridData_1 = new GridData(SWT.FILL, SWT.CENTER, false, false);
        gridData_1.widthHint = 118;
        m_tBBServer.setLayoutData(gridData_1);

        final Label portLabel = new Label(composite_5, SWT.NONE);
        portLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        portLabel.setText("Port:");

        m_BBPort = new Text(composite_5, SWT.BORDER);
        m_BBPort.setText("1099");

        final GridData gridData_3 = new GridData(SWT.FILL, SWT.CENTER, false, false);
        gridData_3.widthHint = 52;
        m_BBPort.setLayoutData(gridData_3);

        final Button bBBConnect = new Button(composite_5, SWT.NONE);
        bBBConnect.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    buildRMIRable();
                }
            });
        bBBConnect.setText("&List");

        final Label usernameLabel_1 = new Label(composite_5, SWT.NONE);
        usernameLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        usernameLabel_1.setText("Username:");

        m_tBBUsername = new Text(composite_5, SWT.BORDER);
        m_tBBUsername.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

        final Label passwordLabel_1 = new Label(composite_5, SWT.NONE);
        passwordLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        passwordLabel_1.setText("Password:");

        m_tBBPassword = new Text(composite_5, SWT.PASSWORD | SWT.BORDER);

        final GridData gridData_2 = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
        gridData_2.widthHint = 331;
        m_tBBPassword.setLayoutData(gridData_2);

        m_tblBBProcessors = new Table(composite_5, SWT.FULL_SELECTION | SWT.BORDER);
        m_tblBBProcessors.addMouseListener(new MouseAdapter()
            {
                public void mouseDoubleClick(final MouseEvent e)
                {
                    connectFromBBTable();
                }
            });
        m_tblBBProcessors.setLinesVisible(true);
        m_tblBBProcessors.setHeaderVisible(true);

        final GridData gridData_4 = new GridData(SWT.FILL, SWT.FILL, true, true, 5, 1);
        gridData_4.heightHint = 100;
        m_tblBBProcessors.setLayoutData(gridData_4);

        final TableColumn newColumnTableColumn = new TableColumn(m_tblBBProcessors, SWT.NONE);
        newColumnTableColumn.setWidth(220);
        newColumnTableColumn.setText("Processor");

        final TableColumn newColumnTableColumn_1 = new TableColumn(m_tblBBProcessors, SWT.NONE);
        newColumnTableColumn_1.setWidth(205);
        newColumnTableColumn_1.setText("Service");

        final TableColumn newColumnTableColumn_2 = new TableColumn(m_tblBBProcessors, SWT.NONE);
        newColumnTableColumn_2.setWidth(134);
        newColumnTableColumn_2.setText("Organization");

        final TableColumn newColumnTableColumn_3 = new TableColumn(m_tblBBProcessors, SWT.NONE);
        newColumnTableColumn_3.setWidth(300);
        newColumnTableColumn_3.setText("JMX URL");

        // Create the popup menu
        Menu menu = new Menu((Decorations) cParent, SWT.POP_UP);
        MenuItem mi = new MenuItem(menu, SWT.PUSH);
        mi.setText("Copy JMX URL");

        mi.addSelectionListener(new SelectionAdapter()
            {
                /**
                 * @see  org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                @Override public void widgetSelected(SelectionEvent e)
                {
                    TableItem[] sel = m_tblBBProcessors.getSelection();

                    if ((sel != null) && (sel.length > 0))
                    {
                        String url = (String) sel[0].getData();

                        Clipboard cb = new Clipboard(Display.getCurrent());

                        TextTransfer tt = TextTransfer.getInstance();
                        cb.setContents(new String[] { url }, new Transfer[] { tt });
                    }
                }
            });
        m_tblBBProcessors.setMenu(menu);

        final CTabItem tiByURL = new CTabItem(tabFolder_1, SWT.NONE);
        tiByURL.setText("By URL");

        final Composite composite_7 = new Composite(tabFolder_1, SWT.NONE);
        composite_7.setLayout(new GridLayout());
        tiByURL.setControl(composite_7);

        final Group jmxConnectionDetailsGroup = new Group(composite_7, SWT.NONE);
        jmxConnectionDetailsGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        jmxConnectionDetailsGroup.setText(" JMX connection details");

        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 4;
        jmxConnectionDetailsGroup.setLayout(gridLayout);

        final Label serviceUrlLabel = new Label(jmxConnectionDetailsGroup, SWT.NONE);
        serviceUrlLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        serviceUrlLabel.setText("Service URL:");

        m_tServiceURL = new Text(jmxConnectionDetailsGroup, SWT.BORDER);
        m_tServiceURL.setText("service:jmx:rmi:///jndi/rmi://127.0.0.1:1099/cordys/Monitor");

        final GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
        gridData.widthHint = 350;
        m_tServiceURL.setLayoutData(gridData);

        final Label usernameLabel = new Label(jmxConnectionDetailsGroup, SWT.NONE);
        usernameLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        usernameLabel.setText("Username:");

        m_tUsername = new Text(jmxConnectionDetailsGroup, SWT.BORDER);
        m_tUsername.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Label passwordLabel = new Label(jmxConnectionDetailsGroup, SWT.NONE);
        passwordLabel.setText("Password:");

        m_tPassword = new Text(jmxConnectionDetailsGroup, SWT.PASSWORD | SWT.BORDER);
        m_tPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Composite composite = new Composite(jmxConnectionDetailsGroup, SWT.NONE);
        final GridLayout gridLayout_1 = new GridLayout();
        composite.setLayout(gridLayout_1);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 4, 1));

        final Button bConnect = new Button(composite, SWT.NONE);
        bConnect.setImage(JMXImageRegistry.loadImage(JMXImageRegistry.IMG_CONNECT));
        bConnect.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    makeJMXConnection(m_tServiceURL.getText(), m_tUsername.getText(), m_tPassword.getText());
                }
            });

        final GridData gridData_5 = new GridData(SWT.CENTER, SWT.CENTER, true, false);
        gridData_5.widthHint = 100;
        bConnect.setLayoutData(gridData_5);
        bConnect.setText("&Connect");

        final Composite composite_1 = new Composite(this, SWT.NONE);
        composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        composite_1.setLayout(new GridLayout());

        final SashForm sashForm = new SashForm(composite_1, SWT.NONE);

        final Composite composite_2 = new Composite(sashForm, SWT.NONE);
        composite_2.setLayout(new GridLayout());

        m_tvMBeanTree = new TreeViewer(composite_2, SWT.FULL_SELECTION | SWT.BORDER);
        m_tvMBeanTree.addSelectionChangedListener(new ISelectionChangedListener()
            {
                public void selectionChanged(final SelectionChangedEvent e)
                {
                    IStructuredSelection ssSel = (IStructuredSelection) e.getSelection();
                    Object oSelected = ssSel.getFirstElement();

                    if (oSelected instanceof ObjectNameNode)
                    {
                        ObjectNameNode pn = (ObjectNameNode) oSelected;
                        m_tvAttributes.setInput(pn.getMbeanInfoWrapper());
                        m_cMainAttrDetails.clean();
                        m_botOperations.getViewer().setInput(pn.getMbeanInfoWrapper());
                        m_ncNotifications.setMBeanInfoWrapper(pn.getMbeanInfoWrapper());

                        // Show the complete object name.
                        m_tObjectName.setText(pn.getObjectName().getKeyPropertyListString());
                    }
                }
            });
        m_tvMBeanTree.setContentProvider(new MBeanExplorerContentProvider());
        m_tvMBeanTree.setLabelProvider(new MBeanExplorerLabelProvider());

        m_tMBeanTree = m_tvMBeanTree.getTree();
        m_tMBeanTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final Composite composite_3 = new Composite(sashForm, SWT.NONE);
        composite_3.setLayout(new GridLayout());

        final CTabFolder tabFolder = new CTabFolder(composite_3, SWT.NONE);
        tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final CTabItem tiAttributes = new CTabItem(tabFolder, SWT.NONE);
        tiAttributes.setImage(SWTResourceManager.getImage(JMXTestTool.class,
                                                          "/com/cordys/coe/tools/jmx/image/attribute.gif"));
        tiAttributes.setText("Attributes");

        final Composite composite_4 = new Composite(tabFolder, SWT.NONE);
        composite_4.setLayout(new GridLayout());
        tiAttributes.setControl(composite_4);

        final SashForm sfAttribute = new SashForm(composite_4, SWT.VERTICAL);
        sfAttribute.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        MBeanAttributesTable batTable = new MBeanAttributesTable(sfAttribute, this);
        m_tvAttributes = batTable.getViewer();

        m_cMainAttrDetails = new AttrDetailsComposite(sfAttribute, SWT.NONE);

        final CTabItem tiOperations = new CTabItem(tabFolder, SWT.NONE);
        tiOperations.setImage(SWTResourceManager.getImage(JMXTestTool.class,
                                                          "/com/cordys/coe/tools/jmx/image/operation.gif"));
        tiOperations.setText("Operations");

        final Composite composite_6 = new Composite(tabFolder, SWT.NONE);
        composite_6.setLayout(new GridLayout());
        tiOperations.setControl(composite_6);

        m_ohOperationHandler = new OperationsHandler();
        m_botOperations = new MBeanOperationsTable(composite_6, m_ohOperationHandler);

        final SashForm sashForm_1 = new SashForm(composite_6, SWT.NONE);

        final Composite cOpInput = new Composite(sashForm_1, SWT.NONE);
        cOpInput.setLayout(new GridLayout());

        final Button invokeButton = new Button(cOpInput, SWT.NONE);
        invokeButton.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    invokeOperation();
                }
            });
        invokeButton.setText("&Invoke");

        m_gOpInputParam = new Group(cOpInput, SWT.NONE);
        m_gOpInputParam.setText(" Input ");
        m_gOpInputParam.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final GridLayout gridLayout_3 = new GridLayout();
        gridLayout_3.numColumns = 2;
        m_gOpInputParam.setLayout(gridLayout_3);

        final Composite cOpOutput = new Composite(sashForm_1, SWT.NONE);
        cOpOutput.setLayout(new GridLayout());

        m_gOpOutput = new Group(cOpOutput, SWT.NONE);
        m_gOpOutput.setText(" Output ");
        m_gOpOutput.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        GridLayout gdOutput = new GridLayout();
        gdOutput.numColumns = 1;
        m_gOpOutput.setLayout(gdOutput);
        sashForm_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        sashForm_1.setWeights(new int[] { 1, 1 });

        final CTabItem tiNotifications = new CTabItem(tabFolder, SWT.NONE);
        tiNotifications.setImage(SWTResourceManager.getImage(JMXTestTool.class,
                                                             "/com/cordys/coe/tools/jmx/image/read_obj.gif"));
        tiNotifications.setText("Notifications");

        m_ncNotifications = new NotificationComposite(tabFolder, SWT.NONE);
        tiNotifications.setControl(m_ncNotifications);
        sashForm.setWeights(new int[] { 177, 509 });
        //
        tabFolder.setSelection(tiAttributes);
        tabFolder_1.setSelection(byBrowsingTabItem);

        // Read the JMXusername and password from the wcp.properties.
        try
        {
            Class<?> cEIBProperties = Class.forName("com.eibus.util.system.EIBProperties");

            if (cEIBProperties != null)
            {
                Method mMethod = cEIBProperties.getDeclaredMethod("getProperty", String.class);

                if (mMethod != null)
                {
                    String sPassword = (String) mMethod.invoke(null, "com.eibus.management.jmxPassword");
                    sPassword = new String(new BASE64Decoder().decodeBuffer(sPassword));

                    String sUsername = (String) mMethod.invoke(null, "com.eibus.management.jmxUser");

                    if ((sUsername != null) && (sUsername.length() > 0) && (sPassword != null) &&
                            (sPassword.length() > 0))
                    {
                        m_tUsername.setText(sUsername);
                        m_tBBUsername.setText(sUsername);
                        m_tPassword.setText(sPassword);
                        m_tBBPassword.setText(sPassword);
                    }
                }
            }
        }
        catch (Throwable e)
        {
            // Print the stack trace, but ignore it.
            System.out.println(Util.getStackTrace(e));

            // Now see if we can find the wcp.properties by looking in these folders:
            String[] asFiles = new String[]
                               {
                                   "c:\\Cordys", "c:\\Program Files\\Cordys", "d:\\Cordys", "d:\\Program Files\\Cordys"
                               };
            File fFound = null;

            for (int iCount = 0; iCount < asFiles.length; iCount++)
            {
                File fTemp = new File(asFiles[iCount]);

                if (fTemp.exists() && fTemp.isDirectory())
                {
                    File fProps = new File(fTemp, "wcp.properties");

                    if (fProps.exists())
                    {
                        fFound = fProps;
                        break;
                    }
                }
            }

            // See if there is a cordys-home that we can use.
            if (fFound == null)
            {
                String sCordysHome = System.getenv().get("CORDYS_HOME");

                if ((sCordysHome != null) && (sCordysHome.length() > 0))
                {
                    File fTemp = new File(sCordysHome);

                    if (fTemp.exists() && fTemp.isDirectory())
                    {
                        File fProps = new File(fTemp, "wcp.properties");

                        if (fProps.exists())
                        {
                            fFound = fProps;
                        }
                    }
                }
            }

            if (fFound != null)
            {
                Properties pTemp = new Properties();

                try
                {
                    pTemp.load(new FileInputStream(fFound));

                    String sPassword = pTemp.getProperty("com.eibus.management.jmxPassword");
                    sPassword = new String(new BASE64Decoder().decodeBuffer(sPassword));

                    String sUsername = pTemp.getProperty("com.eibus.management.jmxUser");

                    if ((sUsername != null) && (sUsername.length() > 0) && (sPassword != null) &&
                            (sPassword.length() > 0))
                    {
                        m_tUsername.setText(sUsername);
                        m_tBBUsername.setText(sUsername);
                        m_tPassword.setText(sPassword);
                        m_tBBPassword.setText(sPassword);
                    }
                }
                catch (Exception e1)
                {
                    // Print stack trace, but ignore it.
                    e1.printStackTrace();
                }
            }
        }

        final Composite composite_8 = new Composite(this, SWT.NONE);
        composite_8.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        composite_8.setLayout(new GridLayout());

        m_tObjectName = new Text(composite_8, SWT.BORDER);
        m_tObjectName.setEditable(false);
        m_tObjectName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    }

    /**
     * This method cleans the data in the attribute details screen.
     *
     * @see  com.cordys.coe.tools.jmx.IUpdateAttributeDetails#clean()
     */
    public void clean()
    {
        m_cMainAttrDetails.clean();
    }

    /**
     * This method will make the actual JMX connection to the server.
     *
     * @param  sConnectionURL  The URL to connect to.
     * @param  sUsername       The JMX username.
     * @param  sPassword       The JMX password.
     */
    public void makeJMXConnection(String sConnectionURL, String sUsername, String sPassword)
    {
        try
        {
            JMXServiceURL jsuJMXServiceUrl = new JMXServiceURL(sConnectionURL);

            // Now create the MBeanServerConnection.
            String[] asCredentials = new String[] { sUsername, sPassword };

            Map<String, String[]> mEnv = new HashMap<String, String[]>();
            mEnv.put("jmx.remote.credentials", asCredentials);

            m_jcConnector = JMXConnectorFactory.connect(jsuJMXServiceUrl, mEnv);

            // Set the input for the tree.
            Node root = NodeUtils.createObjectNameTree(m_jcConnector.getMBeanServerConnection());
            m_tvMBeanTree.setInput(root);

            m_tvMBeanTree.getTree().setFocus();

            // Find the com.eibus domain and expand it.
            Node[] anDomains = root.getChildren();

            for (int iCount = 0; iCount < anDomains.length; iCount++)
            {
                Node nNode = anDomains[iCount];

                if (nNode instanceof DomainNode)
                {
                    DomainNode dnNode = (DomainNode) nNode;

                    if ("com.eibus".equals(dnNode.getDomain()))
                    {
                        m_tvMBeanTree.setSelection(new StructuredSelection(dnNode));
                        m_tvMBeanTree.expandToLevel(dnNode, 99);
                    }
                }
            }
        }
        catch (Exception e)
        {
            MessageBoxUtil.showError("Error creating connection to the JMX service URL.", e);
        }
    }

    /**
     * This method should update the details view.
     *
     * @param  baiwAttributeInfo  The attribute to show.
     *
     * @see    com.cordys.coe.tools.jmx.IUpdateAttributeDetails#updateDetails(com.cordys.coe.tools.jmx.resources.MBeanAttributeInfoWrapper)
     */
    public void updateDetails(MBeanAttributeInfoWrapper baiwAttributeInfo)
    {
        m_cMainAttrDetails.updateDetails(baiwAttributeInfo);
    }

    /**
     * This method will build the table with all components that can be connected to.
     */
    protected void buildRMIRable()
    {
        m_tblBBProcessors.removeAll();

        if ((m_tBBServer.getText().length() > 0) && (m_BBPort.getText().length() > 0))
        {
            // Try to get the list of managed components via the rmi registry
            try
            {
                Registry rRegistry = LocateRegistry.getRegistry(m_tBBServer.getText(),
                                                                Integer.parseInt(m_BBPort.getText()));
                String[] saEntries = rRegistry.list();
                List<String[]> entries = new ArrayList<String[]>();

                for (int iCount = 0; iCount < saEntries.length; iCount++)
                {
                    String sEntry = saEntries[iCount];
                    String[] saText = null;

                    String url = "service:jmx:rmi:///jndi/rmi://" + m_tBBServer.getText() + ":" + m_BBPort.getText() +
                                 "/" + sEntry;

                    if (sEntry.startsWith("cordys/"))
                    {
                        // It's a Cordys URL, so analyze it.
                        String sAnalysis = URLDecoder.decode(sEntry, "UTF8");

                        // Strip the cordys/
                        sAnalysis = sAnalysis.substring("cordys/".length());

                        if (sAnalysis.indexOf("#") > -1)
                        {
                            // It's a SP
                            String[] saOthers = sAnalysis.split("#");
                            saText = new String[4];
                            saText[0] = url;

                            for (int iTmpCount = 0; iTmpCount < saOthers.length; iTmpCount++)
                            {
                                saText[saText.length - (1 + iTmpCount)] = saOthers[iTmpCount];
                            }
                        }
                        else
                        {
                            // Another component like the monitor or gateway.
                            saText = new String[] { url, sAnalysis, "", "" };
                        }
                    }
                    else
                    {
                        saText = new String[] { url, sEntry, "", "" };
                    }

                    // Add the entry to the lit
                    entries.add(saText);
                }

                // Sort the entries.
                Collections.sort(entries, new Comparator<String[]>()
                                 {
                                     @Override public int compare(String[] o1, String[] o2)
                                     {
                                         // We'll compare the name of the organization, then the service group, then the container.
                                         int retVal = o1[3].toLowerCase().compareTo(o2[3].toLowerCase());

                                         if (retVal == 0)
                                         {
                                             retVal = o1[2].toLowerCase().compareTo(o2[2].toLowerCase());
                                         }

                                         if (retVal == 0)
                                         {
                                             retVal = o1[1].toLowerCase().compareTo(o2[1].toLowerCase());
                                         }

                                         return retVal;
                                     }
                                 });

                // Now add the sorted list to the table.
                for (String[] data : entries)
                {
                    String[] text = new String[4];

                    // The JMX URL should be added last.
                    text[3] = data[0];

                    for (int count = 0; count < (text.length - 1); count++)
                    {
                        text[count] = data[count + 1];
                    }

                    TableItem tiNew = new TableItem(m_tblBBProcessors, SWT.NONE);

                    tiNew.setText(text);
                    tiNew.setData(data[0]);
                }
            }
            catch (Exception e)
            {
                MessageBoxUtil.showError(getShell(), "Error getting the list of managed components", e);
            }
        }
        else
        {
            MessageBoxUtil.showError(getShell(), "You need to fill at least the servername and port.");
        }
    }

    /**
     * This method will connect to the specific JMX url which is selected in the table.
     */
    protected void connectFromBBTable()
    {
        TableItem[] atiSelected = m_tblBBProcessors.getSelection();

        if ((atiSelected != null) && (atiSelected.length > 0))
        {
            makeJMXConnection((String) atiSelected[0].getData(), m_tBBUsername.getText(), m_tBBPassword.getText());
        }
    }

    /**
     * This method invokes the method with the given parameters.
     */
    protected void invokeOperation()
    {
        // Get the selected method.
        IStructuredSelection ssSel = (IStructuredSelection) m_botOperations.getViewer().getSelection();

        if (ssSel != null)
        {
            Object oTemp = ssSel.getFirstElement();

            if (oTemp instanceof MBeanOperationInfoWrapper)
            {
                MBeanOperationInfoWrapper boiwOperation = (MBeanOperationInfoWrapper) oTemp;

                try
                {
                    MBeanParameterInfo[] paramInfos = boiwOperation.getMBeanOperationInfo().getSignature();
                    Object[] paramList = null;
                    LinkedHashMap<String, Text> lhmParams = m_ohOperationHandler.getParameterValues();

                    if ((lhmParams != null) && (lhmParams.size() > 0))
                    {
                        paramList = MBeanUtils.getParameters(lhmParams, paramInfos);
                    }

                    MBeanServerConnection mbsc = boiwOperation.getMBeanServerConnection();
                    ObjectName objectName = boiwOperation.getObjectName();
                    String methodName = boiwOperation.getMBeanOperationInfo().getName();
                    Object result;

                    if (paramList != null)
                    {
                        String[] paramSig = new String[paramInfos.length];

                        for (int i = 0; i < paramSig.length; i++)
                        {
                            paramSig[i] = paramInfos[i].getType();
                        }
                        result = mbsc.invoke(objectName, methodName, paramList, paramSig);
                    }
                    else
                    {
                        result = mbsc.invoke(objectName, methodName, new Object[0], new String[0]);
                    }

                    String sReturnType = boiwOperation.getMBeanOperationInfo().getReturnType();

                    if ("void".equals(sReturnType) || "java.lang.Void".equals(sReturnType))
                    {
                        MessageBoxUtil.showInformation("Operation " + boiwOperation.getMBeanOperationInfo().getName() +
                                                       " executed successfully.");
                    }
                    else
                    {
                        SWTUtils.disposeChildren(m_gOpOutput);

                        // Build up the response for the method.
                        Control attrControl = AttributeControlFactory.createControl(m_gOpOutput, result);

                        if (attrControl instanceof Text)
                        {
                            Text tTemp = (Text) attrControl;
                            createPopupMenu(tTemp, false);
                        }

                        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
                        attrControl.setLayoutData(gd);
                        m_gOpOutput.layout();

                        // Handle special cases that need a special implementation.
                        handleSpecialOperation(objectName, methodName, result);
                    }
                }
                catch (Exception e)
                {
                    MessageBoxUtil.showError(getShell(),
                                             "Error executing operation " +
                                             boiwOperation.getMBeanOperationInfo().getName(), e);
                }
            }
        }
    }

    /**
     * This method creates a popup menu for the control.
     *
     * @param  cText         The control to set the menu for.
     * @param  bEditEnabled  DOCUMENTME
     */
    private void createPopupMenu(final Text cText, final boolean bEditEnabled)
    {
        final Menu m_pmMaximize = new Menu(cText);
        cText.setMenu(m_pmMaximize);

        final MenuItem miMaximizeInput = new MenuItem(m_pmMaximize, SWT.NONE);
        miMaximizeInput.setText("Maximize input");

        miMaximizeInput.addSelectionListener(new SelectionAdapter()
            {
                /**
                 * @see  org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                @Override public void widgetSelected(SelectionEvent arg0)
                {
                    ShowDetailDialog sds = new ShowDetailDialog(getShell());
                    sds.setCurrentText(cText.getText());
                    sds.setEnabled(bEditEnabled);

                    if ((sds.open() == IDialogConstants.OK_ID) && (bEditEnabled == true))
                    {
                        cText.setText(sds.getCurrentText());
                    }
                }
            });
    }

    /**
     * This method handles the result of the dumpAllThreads response to build up a nice looking report.
     *
     * @param  result  The JMX result.
     */
    private void handleDumpAllThreads(Object result)
    {
        // First we get all threads.
        StringBuilder threadDetails = new StringBuilder(1024);
        CompositeData[] allThreads = (CompositeData[]) result;

        for (CompositeData threadData : allThreads)
        {
            String threadName = StringUtils.toString(threadData.get("threadName"), false);
            String threadState = StringUtils.toString(threadData.get("threadState"), false);
            String suspended = StringUtils.toString(threadData.get("suspended"), false);

            threadDetails.append(threadName).append("(").append(threadState).append(", ").append(suspended).append(")\n");

            // Now get the stack trace
            CompositeData[] elements = (CompositeData[]) threadData.get("stackTrace");

            for (Object temp : elements)
            {
                CompositeData stackTraceElement = (CompositeData) temp;
                threadDetails.append(StringUtils.toString(stackTraceElement.get("className"), false));
                threadDetails.append(".");
                threadDetails.append(StringUtils.toString(stackTraceElement.get("methodName"), false));

                boolean isNative = (Boolean) stackTraceElement.get("nativeMethod");
                threadDetails.append("(");

                if (isNative)
                {
                    threadDetails.append("Native Method");
                }
                else
                {
                    threadDetails.append(StringUtils.toString(stackTraceElement.get("fileName"), false));

                    int lineNumber = (Integer) stackTraceElement.get("lineNumber");

                    if (lineNumber > 0)
                    {
                        threadDetails.append(":").append(lineNumber);
                    }
                }
                threadDetails.append(")\n");
            }

            threadDetails.append("\n");
        }

        // Now we need to show a window containing all data.
        ShowJMXDataDialog d = new ShowJMXDataDialog(getShell(), threadDetails.toString());
        d.open();
    }

    /**
     * This method will handle special operations result.
     *
     * <p>Currently the following 'special' operations are supported:</p>
     *
     * <ul>
     *   <li>dumpAllThreads</li>
     * </ul>
     *
     * @param  objectName  The name of the JMX object.
     * @param  methodName  THe operation.
     * @param  result      THe result form the method call.
     */
    private void handleSpecialOperation(ObjectName objectName, String methodName, Object result)
    {
        if ("dumpAllThreads".equals(methodName))
        {
            handleDumpAllThreads(result);
        }
    }

    /**
     * DOCUMENTME.
     *
     * @author  $author$
     */
    class ContentProvider
        implements IStructuredContentProvider
    {
        /**
         * DOCUMENTME.
         */
        public void dispose()
        {
        }

        /**
         * DOCUMENTME.
         *
         * @param   inputElement  DOCUMENTME
         *
         * @return  DOCUMENTME
         */
        public Object[] getElements(Object inputElement)
        {
            if (inputElement instanceof MBeanInfoWrapper)
            {
                MBeanInfoWrapper biwInfo = (MBeanInfoWrapper) inputElement;
                return biwInfo.getMBeanAttributeInfoWrappers();
            }
            return new Object[0];
        }

        /**
         * DOCUMENTME.
         *
         * @param  viewer    DOCUMENTME
         * @param  oldInput  DOCUMENTME
         * @param  newInput  DOCUMENTME
         */
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
        {
        }
    }

    /**
     * Class that handles the operation details.
     *
     * @author  pgussow
     */
    private class OperationsHandler
        implements IOperationsHandler
    {
        /**
         * Holds the parameters and their values.
         */
        private LinkedHashMap<String, Text> m_lhmParams = new LinkedHashMap<String, Text>();

        /**
         * This method cleans the data in the operation details screen.
         *
         * @see  com.cordys.coe.tools.jmx.IOperationsHandler#clean()
         */
        public void clean()
        {
            m_lhmParams.clear();
            SWTUtils.disposeChildren(m_gOpInputParam);
            SWTUtils.disposeChildren(m_gOpOutput);
        }

        /**
         * This method gets the parameters and their values.
         *
         * @return  The parameters and their values.
         */
        public LinkedHashMap<String, Text> getParameterValues()
        {
            return m_lhmParams;
        }

        /**
         * Tells the object to update with the newly selected operation.
         *
         * @param  boiwOperation  The operation that is selected.
         *
         * @see    com.cordys.coe.tools.jmx.IOperationsHandler#updateDetails(com.cordys.coe.tools.jmx.resources.MBeanOperationInfoWrapper)
         */
        public void updateDetails(MBeanOperationInfoWrapper boiwOperation)
        {
            clean();

            // Analyze the operation and create the GUI components.
            MBeanOperationInfo oiInfo = boiwOperation.getMBeanOperationInfo();
            MBeanParameterInfo[] abpiParameters = oiInfo.getSignature();

            for (int iCount = 0; iCount < abpiParameters.length; iCount++)
            {
                MBeanParameterInfo bpiParam = abpiParameters[iCount];

                Label lTemp = new Label(m_gOpInputParam, SWT.RIGHT);
                lTemp.setText(bpiParam.getName());

                Text tTemp = new Text(m_gOpInputParam, SWT.BORDER);
                tTemp.setToolTipText(bpiParam.getType() + ": " + bpiParam.getDescription());

                tTemp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

                // Set the default value.
                tTemp.setText(MBeanUtils.getDefaultValue(bpiParam.getType()));

                createPopupMenu(tTemp, true);

                m_lhmParams.put(bpiParam.getName(), tTemp);
            }

            m_gOpInputParam.layout();
        }
    }
}
