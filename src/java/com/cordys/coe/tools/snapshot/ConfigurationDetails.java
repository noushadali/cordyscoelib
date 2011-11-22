package com.cordys.coe.tools.snapshot;

import com.cordys.coe.tools.snapshot.config.Config;
import com.cordys.coe.tools.snapshot.config.JMXCounter;
import com.cordys.coe.tools.snapshot.config.Property;
import com.cordys.coe.tools.snapshot.config.Server;
import com.cordys.coe.tools.snapshot.config.ServiceContainer;
import com.cordys.coe.tools.snapshot.config.ServiceGroup;
import com.cordys.coe.util.swing.MessageBoxUtil;

import java.awt.BorderLayout;

import java.io.ByteArrayOutputStream;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;

import javax.swing.border.TitledBorder;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import javax.swing.table.DefaultTableModel;

import javax.swing.tree.DefaultMutableTreeNode;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import net.miginfocom.swing.MigLayout;
import java.awt.Font;

/**
 * Holds the composite that shows the configuration details.
 *
 * @author  localpg
 */
public class ConfigurationDetails extends JPanel
{
    /**
     * Holds the configuration that be displayed.
     */
    private Config m_config;
    /**
     * Holds the tree displaying the service groups / containers and JMX counters.
     */
    private JTree m_tree;
    /**
     * Holds the servers from which data will be retrieved.
     */
    private JTable m_serversTable;
    /**
     * Holds the domain of the property.
     */
    private JTextField m_domain;
    /**
     * Holds teh name of the property to get.
     */
    private JTextField m_property;
    /**
     * Holds teh counter type.
     */
    private JTextField m_type;
    /**
     * Holds the class name for the data handler.
     */
    private JTextField m_dataHandler;
    /**
     * Holds the class name for the data collector.
     */
    private JTextField m_dataCollector;
    /**
     * Holds the proeprties for the selected counter.
     */
    private JTable m_counterProperties;
    /**
     * Holds teh raw configuration XML.
     */
    private JTextArea m_rawView;
    /**
     * Holds teh JAXB context.
     */
    private JAXBContext m_context;

    /**
     * Create the panel.
     *
     * @param  config   The configuration that be displayed.
     * @param  context  The JAXB context.
     */
    public ConfigurationDetails(Config config, JAXBContext context)
    {
        m_config = config;
        m_context = context;

        setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, BorderLayout.CENTER);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        scrollPane.setViewportView(tabbedPane);

        JPanel panel = new JPanel();
        tabbedPane.addTab("Configuration details", null, panel, null);
        panel.setLayout(new BorderLayout(0, 0));

        JSplitPane splitPane_1 = new JSplitPane();
        splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
        panel.add(splitPane_1);

        JScrollPane scrollPane_4 = new JScrollPane();
        splitPane_1.setLeftComponent(scrollPane_4);

        m_serversTable = new JTable();

        scrollPane_4.setViewportView(m_serversTable);

        JPanel panel_1 = new JPanel();
        splitPane_1.setRightComponent(panel_1);
        panel_1.setLayout(new BorderLayout(0, 0));

        JSplitPane splitPane = new JSplitPane();
        panel_1.add(splitPane, BorderLayout.CENTER);

        JScrollPane scrollPane_3 = new JScrollPane();
        splitPane.setRightComponent(scrollPane_3);

        JPanel panel_3 = new JPanel();
        panel_3.setBorder(new TitledBorder(null, " JMX Counter details ", TitledBorder.LEADING, TitledBorder.TOP, null,
                                           null));
        scrollPane_3.setViewportView(panel_3);
        panel_3.setLayout(new MigLayout("", "[][grow]", "[][][][][][grow]"));

        JLabel lblName = new JLabel("Domain:");
        lblName.setHorizontalAlignment(SwingConstants.TRAILING);
        panel_3.add(lblName, "cell 0 0,alignx trailing");

        m_domain = new JTextField();
        panel_3.add(m_domain, "cell 1 0,growx");
        m_domain.setColumns(10);

        JLabel lblProperty = new JLabel("Property:");
        panel_3.add(lblProperty, "cell 0 1,alignx trailing");

        m_property = new JTextField();
        panel_3.add(m_property, "cell 1 1,growx");
        m_property.setColumns(10);

        JLabel lblType = new JLabel("Type:");
        panel_3.add(lblType, "cell 0 2,alignx trailing");

        m_type = new JTextField();
        panel_3.add(m_type, "cell 1 2,growx");
        m_type.setColumns(10);

        JLabel lblDataHandler = new JLabel("Data handler:");
        panel_3.add(lblDataHandler, "cell 0 3,alignx trailing");

        m_dataHandler = new JTextField();
        panel_3.add(m_dataHandler, "cell 1 3,growx");
        m_dataHandler.setColumns(10);

        JLabel lblDataCollector = new JLabel("Data collector:");
        panel_3.add(lblDataCollector, "cell 0 4,alignx trailing");

        m_dataCollector = new JTextField();
        panel_3.add(m_dataCollector, "cell 1 4,growx");
        m_dataCollector.setColumns(10);

        JScrollPane scrollPane_2 = new JScrollPane();
        panel_3.add(scrollPane_2, "cell 0 5 2 1,grow");

        m_counterProperties = new JTable();
        scrollPane_2.setViewportView(m_counterProperties);

        JPanel panel_2 = new JPanel();
        splitPane.setLeftComponent(panel_2);
        panel_2.setLayout(new BorderLayout(0, 0));

        DefaultMutableTreeNode m_root = new DefaultMutableTreeNode("Service Groups");

        if (config != null)
        {
            createTreeNodes(m_root, config.getServiceGroupList());
        }

        m_tree = new JTree(m_root);
        m_tree.addTreeSelectionListener(new TreeSelectionListener()
            {
                public void valueChanged(TreeSelectionEvent e)
                {
                    showCounterDetails(e);
                }
            });
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
     * This method shows the details of the selected JMX counter.
     *
     * @param  e  The tree selection event that occurred.
     */
    public void showCounterDetails(TreeSelectionEvent e)
    {
        DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) m_tree.getSelectionPath().getLastPathComponent();
        Object userObject = dmtn.getUserObject();

        if (userObject instanceof JMXCounter)
        {
            JMXCounter counter = (JMXCounter) userObject;

            m_domain.setText(counter.getDomain());
            m_property.setText(counter.getProperty());
            m_type.setText(counter.getCounterType().name());

            m_dataHandler.setText(counter.getDataHandler());
            m_dataCollector.setText(counter.getDataCollector());

            List<Property> properties = counter.getNamePropertyList();
            m_counterProperties.setModel(new PropertiesTableModel(properties));
        }
    }

    /**
     * This method creates all the tree nodes.
     *
     * @param  m_root            The root node.
     * @param  serviceGroupList  The service groups that are
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
        ArrayList<Server> servers = new ArrayList<Server>();

        if (m_config != null)
        {
            servers = m_config.getServerList();
        }

        m_serversTable.setModel(new ServerTableModel(servers));

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
     * Table model to wrap the servers.
     */
    public class ServerTableModel extends DefaultTableModel
    {
        /**
         * Holds the servers that should be displayed.
         */
        private ArrayList<Server> m_servers;

        /**
         * Creates a new ServerTableModel object.
         *
         * @param  servers  The servers to display.
         */
        public ServerTableModel(ArrayList<Server> servers)
        {
            super(new String[] { "Server", "Port" }, servers.size());
            m_servers = servers;
        }

        /**
         * @see  javax.swing.table.DefaultTableModel#getValueAt(int, int)
         */
        @Override public Object getValueAt(int row, int column)
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
    }

    /**
     * Table model to wrap the JMX counter properties.
     */
    public class PropertiesTableModel extends DefaultTableModel
    {
        /**
         * Holds the properties that should be displayed.
         */
        private List<Property> m_properties;

        /**
         * Creates a new PropertiesTableModel object.
         *
         * @param  properties  The servers to display.
         */
        public PropertiesTableModel(List<Property> properties)
        {
            super(new String[] { "Key", "Value" }, properties.size());
            m_properties = properties;
        }

        /**
         * @see  javax.swing.table.DefaultTableModel#getValueAt(int, int)
         */
        @Override public Object getValueAt(int row, int column)
        {
            Object retVal = null;

            Property s = m_properties.get(row);

            switch (column)
            {
                case 0:
                    retVal = s.getKey();
                    break;

                case 1:
                    retVal = s.getValue();
                    break;

                default:
                    break;
            }
            return retVal;
        }
    }
}
