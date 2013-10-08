package com.cordys.coe.tools.snapshot;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import net.miginfocom.swing.MigLayout;

import com.cordys.coe.tools.snapshot.config.Config;
import com.cordys.coe.tools.snapshot.config.EJMXCounterType;
import com.cordys.coe.tools.snapshot.config.JMXCounter;
import com.cordys.coe.tools.snapshot.config.Server;
import com.cordys.coe.tools.snapshot.config.ServiceContainer;
import com.cordys.coe.tools.snapshot.config.ServiceGroup;
import com.cordys.coe.util.swing.MessageBoxUtil;

/**
 * Holds the composite that shows the configuration details.
 * 
 * @author localpg
 */
public class ConfigurationDetails extends JPanel
{
    /** Holds the configuration that be displayed. */
    private Config m_config;
    /** Holds the tree displaying the service groups / containers and JMX counters. */
    private JTree m_tree;
    /** Holds the servers from which data will be retrieved. */
    private JTable m_serversTable;
    /** Holds teh raw configuration XML. */
    private JTextArea m_rawView;
    /** Holds teh JAXB context. */
    private JAXBContext m_context;
    /** Holds teh username to connect. */
    private JTextField m_username;
    /** Holds teh JMX password for the connection. */
    private JPasswordField m_password;
    /** Holds whether or not anything was changed in the configuration. */
    private boolean m_dirty = false;
    /** Holds teh details for the selected item in the tree. */
    private JPanel m_detailsPanel;

    /**
     * Create the panel.
     * 
     * @param config The configuration that be displayed.
     * @param context The JAXB context.
     */
    public ConfigurationDetails(Config config, JAXBContext context)
    {
        m_config = config;
        m_context = context;

        setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, BorderLayout.CENTER);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e)
            {
                JTabbedPane src = (JTabbedPane) e.getSource();

                if (src.getSelectedIndex() == 1)
                {
                    fillRawXML();
                }
            }
        });
        scrollPane.setViewportView(tabbedPane);

        JPanel panel = new JPanel();
        tabbedPane.addTab("Configuration details", null, panel, null);
        panel.setLayout(new BorderLayout(0, 0));

        JPanel panel_4 = new JPanel();
        panel_4.setBorder(new TitledBorder(null, " Logon details ", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel.add(panel_4, BorderLayout.NORTH);
        panel_4.setLayout(new MigLayout("", "[][grow]", "[][]"));

        JLabel lblUsername = new JLabel("Username:");
        panel_4.add(lblUsername, "cell 0 0,alignx trailing");

        m_username = new JTextField();

        panel_4.add(m_username, "cell 1 0,growx");
        m_username.setColumns(10);

        JLabel lblPassword = new JLabel("Password:");
        panel_4.add(lblPassword, "cell 0 1,alignx trailing");

        m_password = new JPasswordField();
        panel_4.add(m_password, "cell 1 1,growx");

        JSplitPane splitPane_1 = new JSplitPane();
        splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
        panel.add(splitPane_1, BorderLayout.CENTER);

        JScrollPane scrollPane_4 = new JScrollPane();
        splitPane_1.setLeftComponent(scrollPane_4);

        m_serversTable = new JTable();
        m_serversTable.setFillsViewportHeight(true);

        scrollPane_4.setViewportView(m_serversTable);

        JPanel panel_1 = new JPanel();
        splitPane_1.setRightComponent(panel_1);
        panel_1.setLayout(new BorderLayout(0, 0));

        JSplitPane splitPane = new JSplitPane();
        panel_1.add(splitPane, BorderLayout.CENTER);

        m_detailsPanel = new JPanel();
        m_detailsPanel.setLayout(new BorderLayout());

        splitPane.setRightComponent(m_detailsPanel);

        JPanel panel_2 = new JPanel();
        splitPane.setLeftComponent(panel_2);
        panel_2.setLayout(new BorderLayout(0, 0));

        DefaultMutableTreeNode m_root = new DefaultMutableTreeNode("Service Groups");

        if (config != null)
        {
            createTreeNodes(m_root, config.getServiceGroupList());
        }

        m_tree = new JTree(m_root);
        m_tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e)
            {
                showCounterDetails(e);
            }
        });
        m_tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

        panel_2.add(m_tree, BorderLayout.CENTER);
        splitPane.setDividerLocation(250);
        splitPane_1.setDividerLocation(100);

        JPanel rawPanel = new JPanel();
        tabbedPane.addTab("Raw", null, rawPanel, null);
        rawPanel.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane_1 = new JScrollPane();
        rawPanel.add(scrollPane_1, BorderLayout.CENTER);

        m_rawView = new JTextArea();
        m_rawView.setFont(new Font("Consolas", Font.PLAIN, 10));
        scrollPane_1.setViewportView(m_rawView);

        fillFromConfig();
    }

    /**
     * This method fills the raw XML of the confifguration.
     */
    protected void fillRawXML()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try
        {
            Marshaller m = m_context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(m_config, baos);

            m_rawView.setText(baos.toString());
        }
        catch (Exception e)
        {
            MessageBoxUtil.showError("Error getting raw data", e);
        }
    }

    /**
     * This method shows the details of the selected JMX counter.
     * 
     * @param e The tree selection event that occurred.
     */
    public void showCounterDetails(TreeSelectionEvent e)
    {
        TreePath path = m_tree.getSelectionPath();

        if (path != null)
        {
            DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) path.getLastPathComponent();

            Object userObject = dmtn.getUserObject();

            JPanel detailPanel = new JPanel();

            if (userObject instanceof JMXCounter)
            {
                JMXCounter counter = (JMXCounter) userObject;
                detailPanel = new JMXCounterPanel(counter, this);
            }
            else if (userObject instanceof ServiceGroup)
            {
                ServiceGroup sg = (ServiceGroup) userObject;
                detailPanel = new ServiceGroupPanel(sg, this);
            }
            else if (userObject instanceof ServiceContainer)
            {
                ServiceContainer sc = (ServiceContainer) userObject;
                detailPanel = new ServiceContainerPanel(sc, this);
            }

            m_detailsPanel.removeAll();

            if (detailPanel != null)
            {
                JScrollPane m_detailScrollPane = new JScrollPane();
                m_detailScrollPane.setViewportView(detailPanel);
                m_detailsPanel.add(m_detailScrollPane, BorderLayout.CENTER);
            }

            m_detailsPanel.revalidate();
        }
    }

    /**
     * This method creates all the tree nodes.
     * 
     * @param m_root The root node.
     * @param serviceGroupList The service groups that are
     */
    private void createTreeNodes(DefaultMutableTreeNode m_root, ArrayList<ServiceGroup> serviceGroupList)
    {
        for (ServiceGroup serviceGroup : serviceGroupList)
        {
            DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(serviceGroup, true);

            ArrayList<ServiceContainer> containers = serviceGroup.getServiceContainerList();

            for (ServiceContainer container : containers)
            {
                DefaultMutableTreeNode containerNode = new DefaultMutableTreeNode(container, true);

                ArrayList<JMXCounter> counters = container.getJMXCounterList();

                for (JMXCounter counter : counters)
                {
                    DefaultMutableTreeNode counterNode = new DefaultMutableTreeNode(counter, false);

                    containerNode.add(counterNode);
                }

                groupNode.add(containerNode);
            }
            m_root.add(groupNode);
        }
    }

    /**
     * This method fills the screen based on the given configuration.
     */
    private void fillFromConfig()
    {
        if (m_config == null)
        {
            m_config = new Config();
        }

        m_username.setText(m_config.getUsername());
        m_password.setText(m_config.getPassword());

        m_username.addInputMethodListener(new BoundInputListener(this, m_username, m_config, "Username"));
        m_password.addInputMethodListener(new BoundInputListener(this, m_password, m_config, "Password"));

        ArrayList<Server> servers = new ArrayList<Server>();

        if (m_config != null)
        {
            servers = m_config.getServerList();
        }

        m_serversTable.setModel(new ServerTableModel(servers));

        // Now add the popup menu for the 2 tables.
        new TablePopup("server", m_serversTable);

        new TreePopupHandler(m_tree);
    }

    /**
     * This method gets whether or not the configuration was changed.
     * 
     * @return Whether or not the configuration was changed.
     */
    public boolean isDirty()
    {
        return m_dirty;
    }

    /**
     * Gets the config that was used.
     * 
     * @return The configuration object used.
     */
    public Config getConfig()
    {
        return m_config;
    }

    /**
     * This method lets the panel know that something has changed.
     */
    public void setDirty()
    {
        m_dirty = true;
    }

    /**
     * Table model to wrap the servers.
     */
    public class ServerTableModel extends DefaultTableModel
    {
        /** Holds the servers that should be displayed. */
        private ArrayList<Server> m_servers;

        /**
         * Creates a new ServerTableModel object.
         * 
         * @param servers The servers to display.
         */
        public ServerTableModel(ArrayList<Server> servers)
        {
            super(new String[] { "Server", "Port" }, servers.size());
            m_servers = servers;
        }

        /**
         * Gets the value at.
         * 
         * @param row the row
         * @param column the column
         * @return the value at
         * @see javax.swing.table.DefaultTableModel#getValueAt(int, int)
         */
        @Override
        public Object getValueAt(int row, int column)
        {
            Object retVal = null;

            Server s = m_servers.get(row);

            switch (column)
            {
                case 0:
                    retVal = s.getName();
                    break;

                case 1:
                    retVal = s.getPort();
                    break;

                default:
                    break;
            }
            return retVal;
        }

        /**
         * Checks if is cell editable.
         * 
         * @param row the row
         * @param column the column
         * @return true, if is cell editable
         * @see javax.swing.table.DefaultTableModel#isCellEditable(int, int)
         */
        @Override
        public boolean isCellEditable(int row, int column)
        {
            return true;
        }

        /**
         * Sets the value at.
         * 
         * @param aValue the a value
         * @param row the row
         * @param column the column
         * @see javax.swing.table.DefaultTableModel#setValueAt(java.lang.Object, int, int)
         */
        @Override
        public void setValueAt(Object aValue, int row, int column)
        {
            Server s = m_servers.get(row);

            switch (column)
            {
                case 0:
                    s.setName((String) aValue);
                    break;

                case 1:
                    if (aValue instanceof String)
                    {
                        try
                        {
                            s.setPort(Integer.parseInt((String) aValue));
                        }
                        catch (Exception e)
                        {
                            MessageBoxUtil.showError("Value must be a valid integer", e);
                        }
                    }
                    else if (aValue instanceof Integer)
                    {
                        s.setPort((Integer) aValue);
                    }
                    break;

                default:
                    break;
            }
        }

        /**
         * Insert row.
         * 
         * @param row the row
         * @param rowData the row data
         * @see javax.swing.table.DefaultTableModel#insertRow(int, java.util.Vector)
         */
        @Override
        public void insertRow(int row, @SuppressWarnings("rawtypes") Vector rowData)
        {
            Server e = new Server();
            e.setPort(1099);

            m_servers.add(e);

            super.insertRow(row, rowData);
        }

        /**
         * Removes the row.
         * 
         * @param row the row
         * @see javax.swing.table.DefaultTableModel#removeRow(int)
         */
        @Override
        public void removeRow(int row)
        {
            m_servers.remove(row);

            super.removeRow(row);
        }
    }

    /**
     * This class holds the popup menu for the Tree.
     */
    private class TreePopupHandler
    {
        /** Holds the tree on which this popup menu should work. */
        private JTree m_tree;
        /** Holds the popup menu for the root level. */
        private JPopupMenu m_rootMenu;
        /** Holds the popup menu for the service group level. */
        private JPopupMenu m_serviceGroup;
        /** Holds the popup menu for the service container level. */
        private JPopupMenu m_serviceContainer;
        /** Holds the popup menu for the JMX counter level. */
        private JPopupMenu m_jmxCounter;
        /** Holds the JMX counters in the copy buffer */
        private ArrayList<DefaultMutableTreeNode> m_toBeCopied = new ArrayList<DefaultMutableTreeNode>();

        /**
         * Instantiates a new tree popup.
         * 
         * @param tree the tree
         */
        public TreePopupHandler(JTree tree)
        {
            m_tree = tree;

            // Create the popup menus as needed.
            m_rootMenu = new JPopupMenu("Service Groups");
            m_serviceGroup = new JPopupMenu("Service Group");
            m_serviceContainer = new JPopupMenu("Service Container");
            m_jmxCounter = new JPopupMenu("JMX Counter");

            createRootMenu();
            createServiceGroupMenu();
            createServiceContainerMenu();
            createJMXCounterMenu();

            m_tree.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e)
                {
                    showPopup(e);
                }

                @Override
                public void mouseReleased(MouseEvent e)
                {
                    showPopup(e);
                }

                private void showPopup(MouseEvent e)
                {
                    if (e.isPopupTrigger() && (m_tree.getModel() != null))
                    {
                        TreePath path = m_tree.getPathForLocation(e.getX(), e.getY());

                        if (m_tree.isSelectionEmpty())
                        {
                            m_tree.setSelectionPath(path);
                        }

                        // Now add the proper items
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) m_tree.getLastSelectedPathComponent();

                        JPopupMenu menuToShow = null;

                        switch (node.getLevel())
                        {
                            case 0:
                                menuToShow = m_rootMenu;
                                break;

                            case 1:
                                menuToShow = m_serviceGroup;
                                break;

                            case 2:
                                menuToShow = m_serviceContainer;
                                break;

                            case 3:
                                menuToShow = m_jmxCounter;
                                break;
                        }

                        if (menuToShow != null)
                        {
                            menuToShow.show(m_tree, e.getX(), e.getY());
                        }
                    }
                }
            });
        }

        /**
         * Creates the context menu for the JMX counters.
         */
        private void createJMXCounterMenu()
        {
            JMenuItem mi = new JMenuItem("Copy");
            mi.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    m_toBeCopied.clear();
                    TreePath[] paths = m_tree.getSelectionPaths();
                    for (TreePath path : paths)
                    {
                        DefaultMutableTreeNode dtm = (DefaultMutableTreeNode) path.getLastPathComponent();
                        if (dtm.getLevel() == 3)
                        {
                            m_toBeCopied.add(dtm);
                        }
                    }
                }
            });
            m_jmxCounter.add(mi);

            mi = new JMenuItem("Delete");
            mi.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    TreePath[] paths = m_tree.getSelectionPaths();
                    for (TreePath path : paths)
                    {
                        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                        ServiceContainer sc = (ServiceContainer) ((DefaultMutableTreeNode)treeNode.getParent()).getUserObject();

                        sc.removeJMXCounter((JMXCounter)treeNode.getUserObject());
                        DefaultTreeModel dtm = (DefaultTreeModel) m_tree.getModel();
                        dtm.removeNodeFromParent(treeNode);
                    }

                    m_tree.revalidate();
                }
            });
            m_jmxCounter.add(mi);

        }

        /**
         * Creates the context menu for the service containers.
         */
        private void createServiceContainerMenu()
        {
            JMenuItem mi = new JMenuItem("New JMX counter");
            mi.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    addNewJMXCounter();
                }
            });
            m_serviceContainer.add(mi);

            mi = new JMenuItem("Delete");
            mi.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) m_tree.getLastSelectedPathComponent();
                    
                    ServiceGroup sg = (ServiceGroup) ((DefaultMutableTreeNode)treeNode.getParent()).getUserObject();
                    sg.removeServiceContainer((ServiceContainer)treeNode.getUserObject());

                    DefaultTreeModel dtm = (DefaultTreeModel) m_tree.getModel();
                    dtm.removeNodeFromParent(treeNode);

                    m_tree.revalidate();
                }
            });
            m_serviceContainer.add(mi);

            mi = new JMenuItem("Delete all JMX counters");
            mi.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    if (MessageBoxUtil.showConfirmation("Are you sure you want to delete all defined JMX Counters?"))
                    {
                        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) m_tree.getLastSelectedPathComponent();
                        ServiceContainer sc = (ServiceContainer) treeNode.getUserObject();
                        sc.clearJMXCounterList();

                        DefaultTreeModel dtm = (DefaultTreeModel) m_tree.getModel();

                        while (treeNode.getChildCount() > 0)
                        {
                            dtm.removeNodeFromParent((MutableTreeNode) treeNode.getFirstChild());
                        }

                        m_tree.revalidate();
                    }
                }
            });
            m_serviceContainer.add(mi);

            mi = new JMenuItem("Paste");
            mi.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    if (m_toBeCopied.isEmpty())
                    {
                        MessageBoxUtil.showError("The copy buffer is empty");
                    }
                    else
                    {
                        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) m_tree.getLastSelectedPathComponent();
                        ServiceContainer sc = (ServiceContainer) treeNode.getUserObject();

                        for (DefaultMutableTreeNode source : m_toBeCopied)
                        {
                            JMXCounter jcSource = (JMXCounter) source.getUserObject();

                            try
                            {
                                JMXCounter cln = (JMXCounter) jcSource.clone();
                                sc.addJMXCounter(cln);
                                DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(cln);
                                treeNode.add(newChild);
                            }
                            catch (CloneNotSupportedException e1)
                            {
                                // Will not happen
                            }
                        }

                        DefaultTreeModel dtm = (DefaultTreeModel) m_tree.getModel();

                        dtm.reload(treeNode);
                    }

                    m_tree.revalidate();
                }
            });

            m_serviceContainer.add(mi);
        }

        /**
         * This method creates the popup menu for the root.
         */
        private void createServiceGroupMenu()
        {
            JMenuItem mi = new JMenuItem("New service container");
            mi.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    addNewServiceContainer();
                }
            });
            m_serviceGroup.add(mi);

            mi = new JMenuItem("Delete");
            mi.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) m_tree.getLastSelectedPathComponent();
                    
                    ConfigurationDetails.this.m_config.removeServiceGroup((ServiceGroup)treeNode.getUserObject());

                    DefaultTreeModel dtm = (DefaultTreeModel) m_tree.getModel();
                    dtm.removeNodeFromParent(treeNode);

                    m_tree.revalidate();
                }
            });
            m_serviceGroup.add(mi);

            mi = new JMenuItem("Delete all service containers");
            mi.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    if (MessageBoxUtil.showConfirmation("Are you sure you want to delete all defined service containers?"))
                    {
                        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) m_tree.getLastSelectedPathComponent();
                        ServiceGroup sg = (ServiceGroup) treeNode.getUserObject();
                        sg.clearServiceContainerList();

                        DefaultTreeModel dtm = (DefaultTreeModel) m_tree.getModel();

                        while (treeNode.getChildCount() > 0)
                        {
                            dtm.removeNodeFromParent((MutableTreeNode) treeNode.getFirstChild());
                        }

                        m_tree.revalidate();
                    }
                }
            });
            m_serviceGroup.add(mi);
        }

        /**
         * This method creates the popup menu for the root.
         */
        private void createRootMenu()
        {
            JMenuItem mi = new JMenuItem("New service group");
            mi.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    addNewServiceGroup();
                }
            });
            m_rootMenu.add(mi);

            mi = new JMenuItem("Delete all service groups");
            mi.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    if (MessageBoxUtil.showConfirmation("Are you sure you want to delete all defined service groups?"))
                    {
                        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) m_tree.getLastSelectedPathComponent();
                        ConfigurationDetails.this.m_config.clearServiceGroupList();

                        DefaultTreeModel dtm = (DefaultTreeModel) m_tree.getModel();

                        while (treeNode.getChildCount() > 0)
                        {
                            dtm.removeNodeFromParent((MutableTreeNode) treeNode.getFirstChild());
                        }

                        m_tree.revalidate();
                    }
                }
            });
            m_rootMenu.add(mi);
        }

        /**
         * Adds the new service group.
         */
        protected void addNewServiceGroup()
        {
            ServiceGroup sg = new ServiceGroup();
            sg.setName("New");
            m_config.addServiceGroup(sg);

            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) m_tree.getLastSelectedPathComponent();

            DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(sg);
            treeNode.add(newChild);

            DefaultTreeModel dtm = (DefaultTreeModel) m_tree.getModel();

            dtm.reload(treeNode);
        }

        /**
         * Adds the new service container.
         */
        protected void addNewServiceContainer()
        {
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) m_tree.getLastSelectedPathComponent();
            ServiceGroup sg = (ServiceGroup) treeNode.getUserObject();

            ServiceContainer sc = new ServiceContainer();
            sc.setName("New Container");

            sg.addServiceContainer(sc);

            DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(sc);
            treeNode.add(newChild);

            DefaultTreeModel dtm = (DefaultTreeModel) m_tree.getModel();

            dtm.reload(treeNode);
        }

        /**
         * Adds the new JMX counter.`
         */
        protected void addNewJMXCounter()
        {
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) m_tree.getLastSelectedPathComponent();
            ServiceContainer sc = (ServiceContainer) treeNode.getUserObject();

            JMXCounter jmxCounter = new JMXCounter();
            jmxCounter.setDomain("com.cordys");
            jmxCounter.setCounterType(EJMXCounterType.ATTRIBUTE);
            sc.addJMXCounter(jmxCounter);

            DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(jmxCounter);
            treeNode.add(newChild);

            DefaultTreeModel dtm = (DefaultTreeModel) m_tree.getModel();

            dtm.reload(treeNode);
        }
    }
}
