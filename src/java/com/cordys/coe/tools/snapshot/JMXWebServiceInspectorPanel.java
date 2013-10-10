package com.cordys.coe.tools.snapshot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import com.cordys.coe.tools.snapshot.config.ActualServiceContainer;
import com.cordys.coe.tools.snapshot.config.JMXCounter;
import com.cordys.coe.tools.snapshot.data.SnapshotData;
import com.cordys.coe.tools.snapshot.data.SnapshotResult;
import com.cordys.coe.tools.snapshot.data.collector.JMXWSICollector;
import com.cordys.coe.tools.snapshot.data.collector.JMXWSIResult;
import com.cordys.coe.tools.snapshot.data.collector.WebServiceInterface;
import com.cordys.coe.tools.snapshot.data.collector.WebServiceOperation;

/**
 * This panel displays the results of the data that was collected via the JMX Web Service Inspector.
 * 
 * @author pgussow
 */
public class JMXWebServiceInspectorPanel extends JPanel
{
    /** Holds all the operations that were found */
    private JTable m_operations;
    /** Holds the filter to be applied to the web service interface name */
    private JTextField tfWebServiceInterface;
    /** Holds the filter to be applied to the web service operation name */
    private JTextField tfWebServiceOperation;

    /**
     * Create the panel.
     */
    public JMXWebServiceInspectorPanel()
    {
        setLayout(new BorderLayout(0, 0));

        JSplitPane splitPane = new JSplitPane();
        splitPane.setOneTouchExpandable(true);
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        add(splitPane);

        JPanel pnlTop = new JPanel();
        pnlTop.setBorder(new TitledBorder(null, " Filter ", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        splitPane.setLeftComponent(pnlTop);
        GridBagLayout gbl_pnlTop = new GridBagLayout();
        gbl_pnlTop.columnWeights = new double[] { 0.0, 1.0 };
        gbl_pnlTop.rowWeights = new double[] { 0.0, 0.0, 0.0 };
        pnlTop.setLayout(gbl_pnlTop);

        JLabel lblWebServiceInterface = new JLabel("Web service interface name:");
        GridBagConstraints gbc_lblWebServiceInterface = new GridBagConstraints();
        gbc_lblWebServiceInterface.anchor = GridBagConstraints.EAST;
        gbc_lblWebServiceInterface.insets = new Insets(0, 0, 5, 5);
        gbc_lblWebServiceInterface.gridx = 0;
        gbc_lblWebServiceInterface.gridy = 0;
        pnlTop.add(lblWebServiceInterface, gbc_lblWebServiceInterface);

        tfWebServiceInterface = new JTextField();
        lblWebServiceInterface.setLabelFor(tfWebServiceInterface);
        GridBagConstraints gbc_tfWebServiceInterface = new GridBagConstraints();
        gbc_tfWebServiceInterface.insets = new Insets(0, 0, 5, 0);
        gbc_tfWebServiceInterface.fill = GridBagConstraints.BOTH;
        gbc_tfWebServiceInterface.gridx = 1;
        gbc_tfWebServiceInterface.gridy = 0;
        pnlTop.add(tfWebServiceInterface, gbc_tfWebServiceInterface);
        tfWebServiceInterface.setColumns(10);

        JLabel lblWebServiceOperation = new JLabel("Web service operation:");
        GridBagConstraints gbc_lblWebServiceOperation = new GridBagConstraints();
        gbc_lblWebServiceOperation.anchor = GridBagConstraints.EAST;
        gbc_lblWebServiceOperation.insets = new Insets(0, 0, 5, 5);
        gbc_lblWebServiceOperation.gridx = 0;
        gbc_lblWebServiceOperation.gridy = 1;
        pnlTop.add(lblWebServiceOperation, gbc_lblWebServiceOperation);

        tfWebServiceOperation = new JTextField();
        lblWebServiceOperation.setLabelFor(tfWebServiceOperation);
        GridBagConstraints gbc_tfWebServiceOperation = new GridBagConstraints();
        gbc_tfWebServiceOperation.insets = new Insets(0, 0, 5, 0);
        gbc_tfWebServiceOperation.anchor = GridBagConstraints.NORTH;
        gbc_tfWebServiceOperation.fill = GridBagConstraints.HORIZONTAL;
        gbc_tfWebServiceOperation.gridx = 1;
        gbc_tfWebServiceOperation.gridy = 1;
        pnlTop.add(tfWebServiceOperation, gbc_tfWebServiceOperation);
        tfWebServiceOperation.setColumns(10);

        JButton btnFilter = new JButton("Apply filter");
        GridBagConstraints gbc_btnFilter = new GridBagConstraints();
        gbc_btnFilter.gridwidth = 2;
        gbc_btnFilter.anchor = GridBagConstraints.EAST;
        gbc_btnFilter.gridx = 0;
        gbc_btnFilter.gridy = 2;
        pnlTop.add(btnFilter, gbc_btnFilter);

        JPanel pnlBottom = new JPanel();
        splitPane.setRightComponent(pnlBottom);
        pnlBottom.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane();
        pnlBottom.add(scrollPane, BorderLayout.CENTER);

        m_operations = new JTable();
        m_operations.getSelectionModel().addListSelectionListener(new SelectionListener(m_operations));
        m_operations.setAutoCreateRowSorter(true);

        m_operations.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "Host", "Organization", "Group",
                "Container", "Web Service Interface", "Web Service Operation", "Average", "Occurrences", "TotalTime" }) {
            boolean[] columnEditables = new boolean[] { false, false, false, false, false, false, false, false, false };

            @Override
            public boolean isCellEditable(int row, int column)
            {
                return columnEditables[column];
            }

            @Override
            public Object getValueAt(int row, int column)
            {
                Object retVal = super.getValueAt(row, column);
                if (column == 3)
                {
                    retVal = ((ActualServiceContainer) retVal).getServiceContainer();
                }
                return retVal;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex)
            {
                if (columnIndex >= 6 && columnIndex <= 8)
                {
                    return Long.class;
                }

                return super.getColumnClass(columnIndex);
            }
        });

        m_operations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        m_operations.setDefaultRenderer(Object.class, new CustomRenderer());
        m_operations.setDefaultRenderer(Long.class, new CustomRenderer());

        scrollPane.setViewportView(m_operations);
    }

    /**
     * This method adds the web service operation as a new row in the table.
     * 
     * @param asc The actual service container this operation was executed by.
     * @param wsi The parent Web Service Interface object.
     * @param wso The operation to add.
     */
    private void addWebServiceOperation(ActualServiceContainer asc, WebServiceInterface wsi, WebServiceOperation wso)
    {
        DefaultTableModel dtm = (DefaultTableModel) m_operations.getModel();

        Object[] rowData = new Object[] { asc.getServer(), asc.getOrganization(), asc.getServiceGroup(), 
                asc, wsi, wso, wso.getTotalAverage(), wso.getTotalOccurrences(), wso.getTotalTime() };

        dtm.addRow(rowData);
    }

    /**
     * This method is called when there are new results to display. It will get all the JMX WSI results from the list and add
     * their calls to the table.
     * 
     * @param sr The overall snapshot result information.
     */
    public void updateData(SnapshotResult sr)
    {
        // First clean the current UI.
        DefaultTableModel dtm = (DefaultTableModel) m_operations.getModel();

        while (dtm.getRowCount() > 0)
        {
            dtm.removeRow(0);
        }

        List<SnapshotData> data = sr.getSnapshotDataList();

        for (SnapshotData snapshot : data)
        {
            ActualServiceContainer asc = snapshot.getActualServiceContainer();

            Map<JMXCounter, Object> values = snapshot.getCounterValuesList();

            if ((values != null) && (values.size() > 0))
            {
                for (JMXCounter counter : values.keySet())
                {
                    if (JMXWSICollector.class.getName().equals(counter.getDataCollector()))
                    {
                        JMXWSIResult wtr = (JMXWSIResult) values.get(counter);

                        List<WebServiceInterface> wsis = wtr.getWebServiceInterfaces();
                        for (WebServiceInterface wsi : wsis)
                        {
                            List<WebServiceOperation> operations = wsi.getOperations();
                            for (WebServiceOperation wso : operations)
                            {
                                addWebServiceOperation(asc, wsi, wso);
                            }
                        }
                    }
                }
            }
        }

        TableRowSorter<?> rowSorter = (TableRowSorter<?>) m_operations.getRowSorter();
        List<SortKey> keys = new ArrayList<SortKey>();
        SortKey sortKey = new SortKey(6, SortOrder.DESCENDING);// column index 2
        keys.add(sortKey);
        rowSorter.setSortKeys(keys);
    }

    /**
     * Custom renderer for colloring the ranges
     */
    private final class CustomRenderer extends DefaultTableCellRenderer
    {
        /**
         * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object,
         *      boolean, boolean, int, int)
         */
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                int column)
        {
            row = table.convertRowIndexToModel(row);

            Component retVal = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            WebServiceOperation wso = (WebServiceOperation) table.getModel().getValueAt(row, 5);

            if (wso.getTotalAverage() < 100000)
            {
                setBackground(Color.GREEN);
            }
            else if (wso.getTotalAverage() < 500000)
            {
                setBackground(Color.ORANGE);
            }
            else
            {
                setBackground(Color.RED);
            }

            return retVal;
        }
    }

    /**
     * The row selection listener used.
     * 
     * @author localpg
     */
    public class SelectionListener implements ListSelectionListener
    {
        /** The table to listen to. */
        JComponent m_comp;

        /**
         * It is necessary to keep the table since it is not possible to determine the table from the event's source.
         * 
         * @param table The table to listen to.
         */
        SelectionListener(JComponent table)
        {
            this.m_comp = table;
        }

        /**
         * When the selection has changed.
         * 
         * @param e DOCUMENTME
         */
        public void valueChanged(ListSelectionEvent e)
        {
//            ListSelectionModel lsm = null;
//
//            int row = -1;
//
//            if (m_comp instanceof JTable)
//            {
//                JTable tbl = (JTable) m_comp;
//                row = tbl.getSelectedRow();
//                lsm = tbl.getSelectionModel();
//            }
//            else if (m_comp instanceof JList)
//            {
//                JList<?> list = (JList<?>) m_comp;
//                row = list.getSelectedIndex();
//                lsm = list.getSelectionModel();
//            }
//
//            if (e.getSource() == lsm)
//            {
//                if (m_comp == m_operations)
//                {
//                    DefaultTableModel dtm = (DefaultTableModel) m_operations.getModel();
//                }
//            }
        }
    }

}
