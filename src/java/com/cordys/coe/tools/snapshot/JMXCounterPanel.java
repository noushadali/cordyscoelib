package com.cordys.coe.tools.snapshot;

import com.cordys.coe.tools.snapshot.config.EJMXCounterType;
import com.cordys.coe.tools.snapshot.config.JMXCounter;
import com.cordys.coe.tools.snapshot.config.Property;

import java.util.List;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import javax.swing.border.TitledBorder;

import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

/**
 * This panel displays the details of the selected JMX counter.
 *
 * @author  localpg
 */
public class JMXCounterPanel extends JPanel
{
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
    private JComboBox<EJMXCounterType> m_type;
    /**
     * Holds the class name for the data handler.
     */
    private JTextField m_dataHandler;
    /**
     * Holds the class name for the data collector.
     */
    private JTextField m_dataCollector;
    /**
     * Holds the properties for the selected counter.
     */
    private JTable m_counterProperties;
    /**
     * The JMX counter to display.
     */
    private JMXCounter m_counter;
    /**
     * Holds teh main configuration details panel.
     */
    private ConfigurationDetails m_configurationDetails;

    /**
     * Instantiates a new jMX counter panel.
     *
     * @param  counter               the counter
     * @param  configurationDetails  The main config panel for the dirty flag.
     */
    public JMXCounterPanel(JMXCounter counter, ConfigurationDetails configurationDetails)
    {
        super();

        m_counter = counter;
        m_configurationDetails = configurationDetails;

        setBorder(new TitledBorder(null, " JMX Counter details ", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        setLayout(new MigLayout("", "[][grow]", "[][][][][][grow]"));

        JLabel lblName = new JLabel("Domain:");
        lblName.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lblName, "cell 0 0,alignx trailing");

        m_domain = new JTextField();
        add(m_domain, "cell 1 0,growx");
        m_domain.setColumns(10);

        JLabel lblProperty = new JLabel("Property:");
        add(lblProperty, "cell 0 1,alignx trailing");

        m_property = new JTextField();
        add(m_property, "cell 1 1,growx");
        m_property.setColumns(10);

        JLabel lblType = new JLabel("Type:");
        add(lblType, "cell 0 2,alignx trailing");

        m_type = new JComboBox<EJMXCounterType>(EJMXCounterType.values());
        add(m_type, "cell 1 2,growx");

        JLabel lblDataHandler = new JLabel("Data handler:");
        add(lblDataHandler, "cell 0 3,alignx trailing");

        m_dataHandler = new JTextField();
        add(m_dataHandler, "cell 1 3,growx");
        m_dataHandler.setColumns(10);

        JLabel lblDataCollector = new JLabel("Data collector:");
        add(lblDataCollector, "cell 0 4,alignx trailing");

        m_dataCollector = new JTextField();
        add(m_dataCollector, "cell 1 4,growx");
        m_dataCollector.setColumns(10);

        JScrollPane scrollPane_2 = new JScrollPane();
        add(scrollPane_2, "cell 0 5 2 1,grow");

        m_counterProperties = new JTable();
        m_counterProperties.setFillsViewportHeight(true);
        scrollPane_2.setViewportView(m_counterProperties);

        fillFromCounter();
    }

    /**
     * This method fills the UI based on the given counter.
     */
    private void fillFromCounter()
    {
        m_domain.setText(m_counter.getDomain());
        m_domain.addInputMethodListener(new BoundInputListener(m_configurationDetails, m_domain, m_counter, "Domain"));

        m_property.setText(m_counter.getProperty());
        m_property.addInputMethodListener(new BoundInputListener(m_configurationDetails, m_property, m_counter,
                                                                 "Property"));

        m_type.setSelectedItem(m_counter.getCounterType());
        m_type.addItemListener(new BoundInputListener(m_configurationDetails, m_type, m_counter, "CounterType",
                                                      EJMXCounterType.class));

        m_dataHandler.setText(m_counter.getDataHandler());
        m_dataHandler.addInputMethodListener(new BoundInputListener(m_configurationDetails, m_dataHandler, m_counter,
                                                                    "DataHandler"));

        m_dataCollector.setText(m_counter.getDataCollector());
        m_dataCollector.addInputMethodListener(new BoundInputListener(m_configurationDetails, m_dataCollector,
                                                                      m_counter, "DataCollector"));

        List<Property> properties = m_counter.getNamePropertyList();
        m_counterProperties.setModel(new PropertiesTableModel(properties));

        new TablePopup("JMX property", m_counterProperties);
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

            if (row < m_properties.size())
            {
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
            }

            return retVal;
        }

        /**
         * @see  javax.swing.table.DefaultTableModel#setValueAt(java.lang.Object, int, int)
         */
        @Override public void setValueAt(Object aValue, int row, int column)
        {
            Property s = m_properties.get(row);

            switch (column)
            {
                case 0:
                    s.setKey((String) aValue);
                    break;

                case 1:
                    s.setValue((String) aValue);
                    break;

                default:
                    break;
            }
        }

        /**
         * @see  javax.swing.table.DefaultTableModel#isCellEditable(int, int)
         */
        @Override public boolean isCellEditable(int row, int column)
        {
            return true;
        }

        /**
         * @see  javax.swing.table.DefaultTableModel#insertRow(int, java.util.Vector)
         */
        @Override public void insertRow(int row, @SuppressWarnings("rawtypes") Vector rowData)
        {
            m_properties.add(new Property());

            super.insertRow(row, rowData);
        }

        /**
         * @see  javax.swing.table.DefaultTableModel#removeRow(int)
         */
        @Override public void removeRow(int row)
        {
            m_properties.remove(row);

            super.removeRow(row);
        }
    }
}
