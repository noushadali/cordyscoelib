package com.cordys.coe.tools.snapshot;

import com.cordys.coe.tools.snapshot.config.ActualServiceContainer;
import com.cordys.coe.tools.snapshot.config.JMXCounter;
import com.cordys.coe.tools.snapshot.data.SnapshotData;
import com.cordys.coe.tools.snapshot.data.SnapshotResult;
import com.cordys.coe.tools.snapshot.data.collector.MemoryCollector;
import com.cordys.coe.tools.snapshot.data.collector.MemoryDetail;
import com.cordys.coe.tools.snapshot.data.collector.MemoryResult;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import javax.swing.border.TitledBorder;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

/**
 * This class will display the memory statistics of all the results.
 * 
 * @author localpg
 */
public class MemoryPanel extends JPanel
{
    /**
     * The panel containing the memory details of the nested pools.
     */
    private JPanel m_details;
    /**
     * The table containing all the memory details.
     */
    private JTable m_table;

    /**
     * Create the panel.
     */
    public MemoryPanel()
    {
        setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, BorderLayout.CENTER);

        m_table = new JTable();
        m_table.getSelectionModel().addListSelectionListener(new SelectionListener(m_table));

        m_table.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "Service Group", "Service Container",
                "Organization", "Heap", "Non Heap" }));
        m_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        m_table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            /**
             * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object,
             *      boolean, boolean, int, int)
             */
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                    int row, int column)
            {
                row = table.convertRowIndexToModel(row);

                MemoryResult di = (MemoryResult) table.getModel().getValueAt(row, 3);
                MemoryDetail md = null;

                if (column == 3)
                {
                    md = di.getHeap();
                }
                else if (column == 4)
                {
                    md = di.getNonHeap();
                }

                if (md != null)
                {
                    int percentage = md.getPercentage();

                    if (percentage < 70)
                    {
                        setBackground(Color.GREEN);
                    }
                    else if (percentage < 90)
                    {
                        setBackground(Color.ORANGE);
                    }
                    else
                    {
                        setBackground(Color.RED);
                    }
                }
                else
                {
                    setBackground(null);
                }

                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        });
        scrollPane.setViewportView(m_table);

        m_details = new JPanel();
        m_details.setBorder(new TitledBorder(null, " Details ", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        add(m_details, BorderLayout.SOUTH);
        m_details.setLayout(new MigLayout("", "[][grow]", "[]"));
    }

    /**
     * This method updates the panel with the information from the Workerthread collector result.
     * 
     * @param sr The snapshot result that should be displayed.
     */
    public void updateData(SnapshotResult sr)
    {
        // First clean the current UI.
        DefaultTableModel dtm = (DefaultTableModel) m_table.getModel();

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
                    if (MemoryCollector.class.getName().equals(counter.getDataCollector()))
                    {
                        MemoryResult mr = (MemoryResult) values.get(counter);

                        // Create the data for the row.

                        Object[] rowData = new Object[] { asc.getServiceGroup(), asc.getServiceContainer(),
                                asc.getOrganization(), mr, mr.getNonHeap() };
                        dtm.addRow(rowData);
                    }
                }
            }
        }
    }

    /**
     * The row selection listener used.
     * 
     * @author localpg
     */
    public class SelectionListener implements ListSelectionListener
    {
        /**
         * The table to listen to.
         */
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
         * @param e The change event.
         */
        public void valueChanged(ListSelectionEvent e)
        {
            ListSelectionModel lsm = null;

            if (m_comp instanceof JTable)
            {
                lsm = ((JTable) m_comp).getSelectionModel();
            }
            else if (m_comp instanceof JList)
            {
                lsm = ((JList) m_comp).getSelectionModel();
            }

            if (e.getSource() == lsm)
            {
                // Column selection changed
                int first = e.getFirstIndex();

                if (m_comp == m_table)
                {
                    DefaultTableModel dtm = (DefaultTableModel) m_table.getModel();
                    MemoryResult mr = (MemoryResult) dtm.getValueAt(first, 3);

                    if (mr != null)
                    {
                        m_details.removeAll();

                        // Create the labels and the text
                        List<MemoryDetail> details = mr.getMemoryDetailList();

                        int row = 0;

                        for (MemoryDetail md : details)
                        {
                            JLabel lblCodeCache = new JLabel(md.getName() + ":");
                            lblCodeCache.setHorizontalAlignment(SwingConstants.TRAILING);
                            m_details.add(lblCodeCache, "cell 0 " + row + ",alignx trailing");

                            JTextField textField = new JTextField();
                            m_details.add(textField, "cell 1 " + row + ",growx");
                            textField.setColumns(10);
                            textField.setEditable(false);
                            textField.setText(md.toString());

                            row++;
                        }

                        m_details.revalidate();
                    }
                }
            }
        }
    }
}
