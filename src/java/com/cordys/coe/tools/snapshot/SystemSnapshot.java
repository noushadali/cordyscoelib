package com.cordys.coe.tools.snapshot;

import com.cordys.coe.tools.snapshot.config.ActualServiceContainer;
import com.cordys.coe.tools.snapshot.config.Config;
import com.cordys.coe.tools.snapshot.config.JMXCounter;
import com.cordys.coe.tools.snapshot.config.Server;
import com.cordys.coe.tools.snapshot.data.SnapshotData;
import com.cordys.coe.tools.snapshot.data.SnapshotResult;
import com.cordys.coe.tools.snapshot.data.ThrowableWrapper;
import com.cordys.coe.tools.snapshot.data.handler.DataHandlerFactory;
import com.cordys.coe.tools.snapshot.view.ViewDataFactory;
import com.cordys.coe.util.Pair;
import com.cordys.coe.util.swing.MessageBoxUtil;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import net.miginfocom.swing.MigLayout;

/**
 * This tool can be used to get a snapshot of a running system. The point is to get (also in a cluster) thread dumps and counter
 * information from all the running JVMs so that you can later on analyze this information.
 * 
 * @author pgussow
 */
public class SystemSnapshot implements PropertyChangeListener
{
    private static final String TITLE_SNAPSHOT_GRABBER = "Snapshot Grabber";
    /** The main frame. */
    private JFrame frmSnapshotGrabber;
    /** Holds teh configuration that should be used. */
    private Config m_config;
    /** Holds teh JAXB context to use for the XML serialization. */
    private JAXBContext m_context;
    /** Holds the details of the servers that this snapshot connects to. */
    private JTextField m_servers;
    /** The response from the snapshot grabber. */
    private SnapshotResult m_result;
    /** The raw XML result of the JMX counter result object. */
    private JTextArea m_rawResult;
    /** Holds teh result of all the counters. */
    private JTree m_resultTree;
    /** Holds the details. */
    private JPanel m_detailPanel;
    /** Holds the threadpool result panel. */
    private ThreadPoolPanel m_threadPoolPanel;
    /** Holds the memory panel details. */
    private MemoryPanel m_memoryPanel;
    /** Holds the DB Connection pool panel details. */
    private DBConnectionPoolPanel m_dbPoolPanel;
    /** Holds the name of the configuration file that was loaded. */
    private String m_configFile;
    /** Holds the m_grabber task. */
    private GrabberTask m_grabberTask;
    /** Holds the progress monitor that is being used. */
    private ProgressMonitor m_pm;
    /** Holds the JMX panel */
    private JMXWebServiceInspectorPanel m_jmxWSIPanel;

    /**
     * Launch the application.
     * 
     * @param args The commandline arguments.
     */
    public static void main(String[] args)
    {
        try
        {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        EventQueue.invokeLater(new Runnable() {
            public void run()
            {
                try
                {
                    SystemSnapshot window = new SystemSnapshot();
                    window.frmSnapshotGrabber.setVisible(true);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public SystemSnapshot()
    {
        initialize();

        // Create the basic JAXB context for unmarshalling.
        try
        {
            m_context = JAXBContext.newInstance(Config.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize()
    {
        frmSnapshotGrabber = new JFrame();
        frmSnapshotGrabber.setTitle("Snapshot grabber");
        frmSnapshotGrabber.setBounds(100, 100, 1024, 729);
        frmSnapshotGrabber.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JToolBar toolBar = new JToolBar();
        frmSnapshotGrabber.getContentPane().add(toolBar, BorderLayout.NORTH);

        JButton bOpen = new JButton("");
        bOpen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                openConfiguration();
            }
        });
        bOpen.setToolTipText("Open shapshot grabber configuration file");
        bOpen.setIcon(new ImageIcon(SystemSnapshot.class.getResource("/com/cordys/coe/tools/snapshot/open.gif")));
        toolBar.add(bOpen);

        JButton bConfigDetails = new JButton("");
        bConfigDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                showConfigurationDetails();
            }
        });
        bConfigDetails.setToolTipText("Show configuration details");
        bConfigDetails.setIcon(new ImageIcon(SystemSnapshot.class.getResource("/com/cordys/coe/tools/snapshot/properties.gif")));
        toolBar.add(bConfigDetails);

        JButton bGrabSnapshot = new JButton("");
        bGrabSnapshot.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                buildSnapshot();
            }
        });
        bGrabSnapshot.setToolTipText("Get snapshot");
        bGrabSnapshot.setIcon(new ImageIcon(SystemSnapshot.class.getResource("/com/cordys/coe/tools/snapshot/download.gif")));

        toolBar.addSeparator();

        toolBar.add(bGrabSnapshot);

        toolBar.addSeparator();

        JButton bLoadSnapshot = new JButton("");
        bLoadSnapshot.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                loadSnapshot();
            }
        });
        bLoadSnapshot.setToolTipText("Load saved snapshot");
        bLoadSnapshot.setIcon(new ImageIcon(SystemSnapshot.class.getResource("/com/cordys/coe/tools/snapshot/lookup_file.png")));
        toolBar.add(bLoadSnapshot);

        JButton bSaveSnapshot = new JButton("");
        bSaveSnapshot.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                saveSnapshot();
            }
        });
        bSaveSnapshot.setToolTipText("Save saved snapshot");
        bSaveSnapshot.setIcon(new ImageIcon(SystemSnapshot.class.getResource("/com/cordys/coe/tools/snapshot/save.gif")));
        toolBar.add(bSaveSnapshot);

        JPanel panel = new JPanel();
        frmSnapshotGrabber.getContentPane().add(panel, BorderLayout.CENTER);
        panel.setLayout(new BorderLayout(0, 0));

        JPanel panel_1 = new JPanel();
        panel_1.setBorder(new TitledBorder(null, " Configuration details ", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel.add(panel_1, BorderLayout.NORTH);
        panel_1.setLayout(new MigLayout("", "[41px][grow,fill]", "[20px]"));

        JLabel lblServers = new JLabel("Servers:");
        panel_1.add(lblServers, "cell 0 0,alignx trailing,aligny center");

        m_servers = new JTextField();
        m_servers.setEditable(false);
        panel_1.add(m_servers, "cell 1 0,growx");
        m_servers.setColumns(10);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        panel.add(tabbedPane, BorderLayout.CENTER);

        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                JTabbedPane src = (JTabbedPane) e.getSource();

                if (src.getSelectedIndex() == 4)
                {
                    fillRawXML();
                }
            }
        });

        JPanel panel_2 = new JPanel();
        tabbedPane.addTab("Snapshot results", null, panel_2, null);
        panel_2.setBorder(null);
        panel_2.setLayout(new BorderLayout(0, 0));

        JSplitPane splitPane = new JSplitPane();
        panel_2.add(splitPane);

        JScrollPane scrollPane_1 = new JScrollPane();
        splitPane.setRightComponent(scrollPane_1);

        m_detailPanel = new JPanel();
        scrollPane_1.setViewportView(m_detailPanel);
        m_detailPanel.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane();
        splitPane.setLeftComponent(scrollPane);

        m_resultTree = new JTree(new DefaultTreeModel(new DefaultMutableTreeNode("Results")));
        m_resultTree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e)
            {
                displayProperData();
            }
        });
        scrollPane.setViewportView(m_resultTree);
        splitPane.setDividerLocation(250);

        m_threadPoolPanel = new ThreadPoolPanel();
        tabbedPane.addTab("Thread Pools", null, m_threadPoolPanel, null);

        m_memoryPanel = new MemoryPanel();
        tabbedPane.addTab("Memory Pools", null, m_memoryPanel, null);

        m_dbPoolPanel = new DBConnectionPoolPanel();
        tabbedPane.addTab("DB Connection Pools", null, m_dbPoolPanel, null);

        m_jmxWSIPanel = new JMXWebServiceInspectorPanel();
        tabbedPane.addTab("JMX Web Service Inspector", null, m_jmxWSIPanel, null);

        JPanel panel_3 = new JPanel();
        tabbedPane.addTab("Raw data", null, panel_3, null);
        panel_3.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane_2 = new JScrollPane();
        panel_3.add(scrollPane_2, BorderLayout.CENTER);

        m_rawResult = new JTextArea();
        m_rawResult.setFont(new Font("Consolas", Font.PLAIN, 10));
        m_rawResult.setEditable(false);
        scrollPane_2.setViewportView(m_rawResult);
    }

    /**
     * This method will save the snapshot result to a file.
     */
    protected void saveSnapshot()
    {
        JFileChooser fc = new JFileChooser();

        fc.setSelectedFile(new File("snapshot_" + System.currentTimeMillis() + ".snapshot"));

        if (fc.showDialog(frmSnapshotGrabber, "Save") == JFileChooser.APPROVE_OPTION)
        {
            FileOutputStream fos = null;

            try
            {
                fos = new FileOutputStream(fc.getSelectedFile(), false);

                ZipOutputStream zos = new ZipOutputStream(fos);

                Marshaller m = m_context.createMarshaller();
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

                // First write the used config. Because it is needed to initialize the JAXB context.
                zos.putNextEntry(new ZipEntry("config.xml"));
                m.marshal(m_config, zos);
                zos.closeEntry();

                zos.putNextEntry(new ZipEntry("snapshot.xml"));
                m.marshal(m_result, zos);
                zos.closeEntry();

                zos.close();
                
                frmSnapshotGrabber.setTitle(TITLE_SNAPSHOT_GRABBER + " - " + fc.getSelectedFile().getName());
            }
            catch (Exception e)
            {
                MessageBoxUtil.showError("Error saving the snapshot result", e);
            }
            finally
            {
                if (fos != null)
                {
                    try
                    {
                        fos.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * This method will load a snapshot file from the file system.
     */
    public void loadSnapshot()
    {
        JFileChooser fc = new JFileChooser();

        fc.setAcceptAllFileFilterUsed(true);
        fc.setFileFilter(new FileFilter() {
            @Override
            public String getDescription()
            {
                return "Snapshot grabber archives";
            }

            @Override
            public boolean accept(File f)
            {
                return f.getName().endsWith(".snapshot") || f.isDirectory();
            }
        });

        if (fc.showDialog(frmSnapshotGrabber, "Load") == JFileChooser.APPROVE_OPTION)
        {
            ZipFile zf = null;

            try
            {
                zf = new ZipFile(fc.getSelectedFile());

                ZipEntry entry = zf.getEntry("config.xml");

                // First load the config so that we can reinitialize the JAXB Context properly.
                Unmarshaller m = m_context.createUnmarshaller();

                m_config = (Config) m.unmarshal(zf.getInputStream(entry));

                // Now recreate the context.
                createJAXBContextForConfig();

                // Recreate the unmarshaller to include the proper classes.
                m = m_context.createUnmarshaller();

                // Load the snapshot details.
                entry = zf.getEntry("snapshot.xml");
                m_result = (SnapshotResult) m.unmarshal(zf.getInputStream(entry));

                // Show the data
                updateResultView();
                
                frmSnapshotGrabber.setTitle(TITLE_SNAPSHOT_GRABBER + " - " + fc.getSelectedFile().getName());
            }
            catch (Exception e)
            {
                MessageBoxUtil.showError("Error building XML report for the result", e);
            }
            finally
            {
                if (zf != null)
                {
                    try
                    {
                        zf.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * This method creates the proper JAXB context based on the currently active configuration.
     * 
     * @throws Exception In case of any exceptions
     * @throws JAXBException In case of any exceptions
     */
    private void createJAXBContextForConfig() throws Exception, JAXBException
    {
        List<Class<?>> classes = m_config.getCustomDataHandlers();
        classes.addAll(DataHandlerFactory.getKnownClasses());
        classes.add(SnapshotResult.class);
        classes.add(Config.class);

        m_context = JAXBContext.newInstance(classes.toArray(new Class<?>[0]));
    }

    /**
     * This method will display the composite for the selected tree item.
     */
    public void displayProperData()
    {
        m_detailPanel.removeAll();

        if ((m_resultTree.getSelectionPath() != null) && (m_resultTree.getSelectionPath().getLastPathComponent() != null))
        {
            Object lpc = m_resultTree.getSelectionPath().getLastPathComponent();

            if (lpc instanceof ResultTreeNode)
            {
                ResultTreeNode rtn = (ResultTreeNode) lpc;
                Object uo = rtn.getUserObject();

                if (uo instanceof Object[])
                {
                    Object[] tmp = (Object[]) uo;
                    JMXCounter counter = (JMXCounter) tmp[0];
                    Object data = tmp[1];

                    m_detailPanel.add(ViewDataFactory.createComponent(data, counter, m_context), BorderLayout.CENTER);
                }
            }
        }

        m_detailPanel.revalidate();
    }

    /**
     * This method will execute the snapshot to get the details from the configured servers.
     */
    public void buildSnapshot()
    {
        try
        {
            frmSnapshotGrabber.setTitle(TITLE_SNAPSHOT_GRABBER);
            
            m_pm = new ProgressMonitor(frmSnapshotGrabber, "Getting information from cordys", null, 0, m_config.getServerList()
                    .size());
            m_pm.setProgress(0);
            m_grabberTask = new GrabberTask(m_pm);
            m_grabberTask.addPropertyChangeListener(this);
            m_grabberTask.execute();
        }
        catch (Exception e)
        {
            MessageBoxUtil.showError("Cannot get the snapshot details", e);
        }
    }

    /**
     * Property change.
     * 
     * @param evt The evt
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        if (m_pm.isCanceled())
        {
            m_grabberTask.cancel(true);
        }
        else if (evt.getPropertyName().equals("progress"))
        {
            // get the % complete from the progress event
            // and set it on the progress monitor
            int progress = ((Integer) evt.getNewValue()).intValue();
            m_pm.setProgress(progress);
        }
    }

    /**
     * Holds the Class GrabberTask.
     */
    private class GrabberTask extends SwingWorker<Void, GrabberData> implements ISnapshotGrabberProgress
    {
        /** Holds the progress monitor that is to be used. */
        ProgressMonitor m_pm;

        /**
         * Instantiates a new grabber task.
         * 
         * @param pm The progress monitor to be used.
         */
        GrabberTask(ProgressMonitor pm)
        {
            m_pm = pm;
        }

        /**
         * @see javax.swing.SwingWorker#doInBackground()
         */
        @Override
        protected Void doInBackground() throws Exception
        {
            SystemSnapshotGrabber ssg = new SystemSnapshotGrabber(m_config);

            try
            {
                m_result = ssg.buildSnapshot(this);
            }
            catch (final Exception e)
            {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run()
                    {
                        MessageBoxUtil.showError("Error getting snapshot", e);
                    }
                });
            }

            return null;
        }

        /**
         * @see com.cordys.coe.tools.snapshot.ISnapshotGrabberProgress#setGrabberProgress(int)
         */
        @Override
        public void setGrabberProgress(int progress)
        {
            setProgress(progress);
        }

        /**
         * @see javax.swing.SwingWorker#done()
         */
        @Override
        protected void done()
        {
            m_pm.close();

            updateResultView();
        }

        /**
         * @see com.cordys.coe.tools.snapshot.ISnapshotGrabberProgress#setMax(int)
         */
        @Override
        public void setMax(int max)
        {
            m_pm.setMaximum(max);
        }

        /**
         * @see com.cordys.coe.tools.snapshot.ISnapshotGrabberProgress#publishGrabberData(com.cordys.coe.tools.snapshot.GrabberData)
         */
        @Override
        public void publishGrabberData(GrabberData data)
        {
            if (data.getProgress() > 100)
            {
                setProgress(100);
            }
            else
            {
                setProgress(data.getProgress());
            }
            publish(data);
        }

        /**
         * @see javax.swing.SwingWorker#process(java.util.List)
         */
        @Override
        protected void process(List<GrabberData> chunks)
        {
            if ((chunks != null) && (chunks.size() > 0))
            {
                GrabberData gd = chunks.get(chunks.size() - 1);

                m_pm.setNote(gd.toString());
            }
        }
    }

    /**
     * This method opens the dialog that contains the configuration details.
     */
    public void showConfigurationDetails()
    {
        boolean isNew = false;

        if (m_config == null)
        {
            if (MessageBoxUtil
                    .showConfirmation("You have not yet loaded a configuration. Do you want to create a default configuration?"))
            {
                // Load the default one
                try
                {
                    m_config = (Config) m_context.createUnmarshaller().unmarshal(
                            SystemSnapshot.class.getResourceAsStream("config-default.xml"));

                    createJAXBContextForConfig();

                    isNew = true;
                }
                catch (Exception e)
                {
                    MessageBoxUtil.showError("Error loading default configuration", e);
                }
            }
        }

        if (m_config != null)
        {
            ConfigurationDetailsDlg cdd = new ConfigurationDetailsDlg(frmSnapshotGrabber, true, m_config, m_context,
                    m_configFile, isNew);

            cdd.setVisible(true);

            displayConfigDetails();
        }
    }

    /**
     * This method shows a file open dialog so that a configuration can be loaded.
     */
    public void openConfiguration()
    {
        JFileChooser fc = null;
        File temp = new File(".\\src\\java\\com\\cordys\\coe\\tools\\snapshot");

        if (temp.exists())
        {
            fc = new JFileChooser(temp);
        }
        else
        {
            fc = new JFileChooser();
        }

        fc.setAcceptAllFileFilterUsed(true);

        fc.setFileFilter(new FileFilter() {
            @Override
            public String getDescription()
            {
                return "XML files (*.xml)";
            }

            @Override
            public boolean accept(File f)
            {
                return f.getName().endsWith(".xml") || f.isDirectory();
            }
        });

        int response = fc.showDialog(frmSnapshotGrabber, "Open");

        if (response == JFileChooser.APPROVE_OPTION)
        {
            loadConfigurationFile(fc.getSelectedFile());
        }
    }

    /**
     * This method will load the file that is passed on.
     * 
     * @param selectedFile The file that should be loaded.
     */
    private void loadConfigurationFile(File selectedFile)
    {
        try
        {
            m_configFile = selectedFile.getCanonicalPath();

            // Load the config.
            m_config = (Config) m_context.createUnmarshaller().unmarshal(new FileInputStream(selectedFile));

            createJAXBContextForConfig();

            displayConfigDetails();
        }
        catch (Exception e)
        {
            MessageBoxUtil.showError("Error loading configuration", e);
        }
    }

    /**
     * This method displays the configuration details of the loaded config.
     */
    private void displayConfigDetails()
    {
        // Set the server details.
        StringBuilder sb = new StringBuilder(1024);
        ArrayList<Server> servers = m_config.getServerList();
        boolean first = true;

        for (Server server : servers)
        {
            if (!first)
            {
                sb.append("; ");
            }
            else
            {
                first = false;
            }
            sb.append(server.toString());
        }
        m_servers.setText(sb.toString());
    }

    /**
     * This method binds the result data to the view.
     */
    public void updateResultView()
    {
        if (m_result == null)
        {
            return;
        }

        // Update the tree
        DefaultMutableTreeNode tm = new ResultTreeNode(m_result, "Result");

        // The map is organization - service group - service container.
        Map<String, Pair<ResultTreeNode, Map<String, ResultTreeNode>>> organizations = new LinkedHashMap<String, Pair<ResultTreeNode, Map<String, ResultTreeNode>>>();
        List<SnapshotData> data = m_result.getSnapshotDataList();

        for (SnapshotData snapshot : data)
        {
            ActualServiceContainer asc = snapshot.getActualServiceContainer();

            // Create the organization tree node.
            Pair<ResultTreeNode, Map<String, ResultTreeNode>> organization = organizations.get(asc.getOrganization());
            Map<String, ResultTreeNode> serviceGroups = null;

            if (organization == null)
            {
                ResultTreeNode orgNode = new ResultTreeNode(asc.getOrganization());
                serviceGroups = new LinkedHashMap<String, ResultTreeNode>();

                organization = new Pair<ResultTreeNode, Map<String, ResultTreeNode>>(orgNode, serviceGroups);
                tm.add(orgNode);
                organizations.put(asc.getOrganization(), organization);
            }
            else
            {
                serviceGroups = organization.getSecond();
            }

            // Create the node for the service group.
            ResultTreeNode group = serviceGroups.get(asc.getServiceGroup());

            if (group == null)
            {
                group = new ResultTreeNode(asc.getServiceGroup());

                organization.getFirst().add(group);

                serviceGroups.put(asc.getServiceGroup(), group);
            }

            // Now get the exception detail if there.
            ResultTreeNode container = new ResultTreeNode(snapshot, asc.getServiceContainer());
            group.add(container);

            ThrowableWrapper tw = snapshot.getException();

            if (tw != null)
            {
                container.add(new ResultTreeNode(tw, tw.getClassName()));
            }

            Map<JMXCounter, Object> values = snapshot.getCounterValuesList();

            if ((values != null) && (values.size() > 0))
            {
                ResultTreeNode valuesNode = new ResultTreeNode("JMX results");
                container.add(valuesNode);

                for (JMXCounter counter : values.keySet())
                {
                    Object value = values.get(counter);

                    ResultTreeNode counterResult = new ResultTreeNode(new Object[] { counter, value }, counter.toString());
                    valuesNode.add(counterResult);
                }
            }
        }

        // The dispatchers have a special treatment.
        m_threadPoolPanel.updateData(m_result);
        m_memoryPanel.updateData(m_result);
        m_dbPoolPanel.updateData(m_result);
        m_jmxWSIPanel.updateData(m_result);

        m_resultTree.setModel(new DefaultTreeModel(tm));
    }

    /**
     * This method fills the raw XML control with the full result.
     */
    private void fillRawXML()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try
        {
            Marshaller m = m_context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(m_result, baos);

            m_rawResult.setText(baos.toString());
        }
        catch (Exception e)
        {
            MessageBoxUtil.showError("Error building XML report for the result", e);
        }
    }

    /**
     * Wraps the result data that can be shown.
     */
    public class ResultTreeNode extends DefaultMutableTreeNode
    {
        /** The title to display. */
        private String m_title;

        /**
         * Creates a new ResultTreeNode object.
         * 
         * @param title The title for the node.
         */
        public ResultTreeNode(String title)
        {
            this(title, title, true);
        }

        /**
         * Creates a new ResultTreeNode object.
         * 
         * @param userObject The result object.
         * @param title The title for the node.
         */
        public ResultTreeNode(Object userObject, String title)
        {
            this(userObject, title, true);
        }

        /**
         * Creates a new ResultTreeNode object.
         * 
         * @param userObject The result object.
         * @param title The title for the node.
         * @param allowsChildren Whether or not the node can have children.
         */
        public ResultTreeNode(Object userObject, String title, boolean allowsChildren)
        {
            super(userObject, allowsChildren);
            m_title = title;
        }

        /**
         * To string.
         * 
         * @return The string
         * @see javax.swing.tree.DefaultMutableTreeNode#toString()
         */
        @Override
        public String toString()
        {
            return m_title;
        }
    }
}
