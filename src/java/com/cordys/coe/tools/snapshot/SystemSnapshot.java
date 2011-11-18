package com.cordys.coe.tools.snapshot;

import com.cordys.coe.tools.snapshot.config.ActualServiceContainer;
import com.cordys.coe.tools.snapshot.config.Config;
import com.cordys.coe.tools.snapshot.config.JMXCounter;
import com.cordys.coe.tools.snapshot.config.Server;
import com.cordys.coe.tools.snapshot.data.SnapshotData;
import com.cordys.coe.tools.snapshot.data.SnapshotResult;
import com.cordys.coe.tools.snapshot.data.ThrowableWrapper;
import com.cordys.coe.tools.snapshot.data.handler.DataHandlerFactory;
import com.cordys.coe.util.swing.MessageBoxUtil;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import javax.swing.UIManager;

import javax.swing.border.TitledBorder;

import javax.swing.filechooser.FileFilter;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import net.miginfocom.swing.MigLayout;

/**
 * This tool can be used to get a snapshot of a running system. The point is to get (also in a cluster) thread dumps and
 * counter information from all the running JVMs so that you can later on analyze this information.
 *
 * @author  pgussow
 */
public class SystemSnapshot
{
    /**
     * The main frame.
     */
    private JFrame frmSnapshotGrabber;
    /**
     * Holds teh configuration that should be used.
     */
    private Config m_config;
    /**
     * Holds teh JAXB context to use for the XML serialization.
     */
    private JAXBContext m_context;
    /**
     * Holds the details of the servers that this snapshot connects to.
     */
    private JTextField m_servers;
    /**
     * DOCUMENTME.
     */
    private SnapshotResult m_result;
    /**
     * DOCUMENTME.
     */
    private JTextArea m_rawResult;
    /**
     * DOCUMENTME.
     */
    private JTree m_resultTree;

    /**
     * Launch the application.
     *
     * @param  args  The commandline arguments.
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

        EventQueue.invokeLater(new Runnable()
            {
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
        bOpen.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    openConfiguration();
                }
            });
        bOpen.setToolTipText("Open shapshot grabber configuration file");
        bOpen.setIcon(new ImageIcon(SystemSnapshot.class.getResource("/com/cordys/coe/tools/snapshot/open.gif")));
        toolBar.add(bOpen);

        JButton bConfigDetails = new JButton("");
        bConfigDetails.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    showConfigurationDetails();
                }
            });
        bConfigDetails.setToolTipText("Show configuration details");
        bConfigDetails.setIcon(new ImageIcon(SystemSnapshot.class.getResource("/com/cordys/coe/tools/snapshot/properties.gif")));
        toolBar.add(bConfigDetails);

        JButton bGrabSnapshot = new JButton("");
        bGrabSnapshot.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    buildSnapshot();
                }
            });
        bGrabSnapshot.setToolTipText("Get snapshot");
        bGrabSnapshot.setIcon(new ImageIcon(SystemSnapshot.class.getResource("/com/cordys/coe/tools/snapshot/import.gif")));
        toolBar.add(bGrabSnapshot);

        JPanel panel = new JPanel();
        frmSnapshotGrabber.getContentPane().add(panel, BorderLayout.CENTER);
        panel.setLayout(new BorderLayout(0, 0));

        JPanel panel_1 = new JPanel();
        panel_1.setBorder(new TitledBorder(null, " Configuration details ", TitledBorder.LEADING, TitledBorder.TOP,
                                           null, null));
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

        JPanel panel_2 = new JPanel();
        tabbedPane.addTab("Snapshot results", null, panel_2, null);
        panel_2.setBorder(null);
        panel_2.setLayout(new BorderLayout(0, 0));

        JSplitPane splitPane = new JSplitPane();
        panel_2.add(splitPane);

        JScrollPane scrollPane_1 = new JScrollPane();
        splitPane.setRightComponent(scrollPane_1);

        JScrollPane scrollPane = new JScrollPane();
        splitPane.setLeftComponent(scrollPane);

        m_resultTree = new JTree();
        scrollPane.setViewportView(m_resultTree);
        splitPane.setDividerLocation(250);

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
     * This method will execute the snapshot to get the details of the.
     */
    public void buildSnapshot()
    {
        try
        {
            SystemSnapshotGrabber ssg = new SystemSnapshotGrabber(m_config);
            m_result = ssg.buildSnapshot();

            updateResultView();
        }
        catch (Exception e)
        {
            MessageBoxUtil.showError("Cannot get the snapshot details", e);
        }
    }

    /**
     * This method opens the dialog that contains the configuration details.
     */
    public void showConfigurationDetails()
    {
        ConfigurationDetailsDlg cdd = new ConfigurationDetailsDlg(frmSnapshotGrabber, true, m_config);

        cdd.setVisible(true);
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

        fc.setFileFilter(new FileFilter()
            {
                @Override public String getDescription()
                {
                    return "XML files (*.xml)";
                }

                @Override public boolean accept(File f)
                {
                    return f.getName().endsWith(".xml");
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
     * @param  selectedFile  The file that should be loaded.
     */
    private void loadConfigurationFile(File selectedFile)
    {
        try
        {
            m_context = JAXBContext.newInstance(Config.class);

            // Load the config.
            m_config = (Config) m_context.createUnmarshaller().unmarshal(new FileInputStream(selectedFile));

            // Recreate the context with all the classes that participate.
            List<Class<?>> classes = m_config.getCustomDataHandlers();
            classes.addAll(DataHandlerFactory.getKnownClasses());
            classes.add(SnapshotResult.class);
            m_context = JAXBContext.newInstance(classes.toArray(new Class<?>[0]));

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
        catch (Exception e)
        {
            MessageBoxUtil.showError("Error loading configuration", e);
        }
    }

    /**
     * This method binds the result data to the view.
     */
    public void updateResultView()
    {
        // First we do the XML
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

        // Update the tree
        DefaultMutableTreeNode tm = new ResultTreeNode(m_result, "Result");

        Map<String, ResultTreeNode> serviceGroups = new LinkedHashMap<String, ResultTreeNode>();
        List<SnapshotData> data = m_result.getSnapshotDataList();

        for (SnapshotData snapshot : data)
        {
            ActualServiceContainer asc = snapshot.getActualServiceContainer();
            ResultTreeNode group = serviceGroups.get(asc.getServiceGroup());

            if (group == null)
            {
                group = new ResultTreeNode(asc.getServiceGroup());
                tm.add(group);
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

                    ResultTreeNode counterResult = new ResultTreeNode(new Object[] { counter, value },
                                                                      counter.toString());
                    valuesNode.add(counterResult);
                }
            }
        }

        m_resultTree.setModel(new DefaultTreeModel(tm));
    }

    /**
     * Wraps the result data that can be shown.
     */
    public class ResultTreeNode extends DefaultMutableTreeNode
    {
        /**
         * The title to display.
         */
        private String m_title;

        /**
         * Creates a new ResultTreeNode object.
         *
         * @param  title  The title for the node.
         */
        public ResultTreeNode(String title)
        {
            this(title, title, true);
        }

        /**
         * Creates a new ResultTreeNode object.
         *
         * @param  userObject  The result object.
         * @param  title       The title for the node.
         */
        public ResultTreeNode(Object userObject, String title)
        {
            this(userObject, title, true);
        }

        /**
         * Creates a new ResultTreeNode object.
         *
         * @param  userObject      The result object.
         * @param  title           The title for the node.
         * @param  allowsChildren  Whether or not the node can have children.
         */
        public ResultTreeNode(Object userObject, String title, boolean allowsChildren)
        {
            super(userObject, allowsChildren);
            m_title = title;
        }

        /**
         * @see  javax.swing.tree.DefaultMutableTreeNode#toString()
         */
        @Override public String toString()
        {
            return m_title;
        }
    }
}
