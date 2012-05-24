package com.cordys.coe.tools.snapshot;

import com.cordys.coe.tools.snapshot.config.ActualServiceContainer;
import com.cordys.coe.tools.snapshot.config.JMXCounter;
import com.cordys.coe.tools.snapshot.data.SnapshotData;
import com.cordys.coe.tools.snapshot.data.SnapshotResult;
import com.cordys.coe.tools.snapshot.data.collector.DBConnectionPoolInfo;
import com.cordys.coe.tools.snapshot.data.collector.DBConnectionPoolInfo.WaitTime;
import com.cordys.coe.tools.snapshot.data.collector.DBPoolCollector;
import com.cordys.coe.tools.snapshot.data.collector.DBPoolResult;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import java.util.ArrayList;
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
 * Displays the details of the DB connection pools that are found.
 *
 * @author  localpg
 */
public class DBConnectionPoolPanel extends JPanel
{
    /**
     * The table containing all the memory details.
     */
    private JTable m_table;
    /**
     * Holds the details panel.
     */
    private JPanel m_details;

    /**
     * Create the panel.
     */
    public DBConnectionPoolPanel()
    {
        setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, BorderLayout.CENTER);

        m_table = new JTable();
        m_table.getSelectionModel().addListSelectionListener(new SelectionListener(m_table));

        m_table.setModel(new DefaultTableModel(new Object[][] {},
                                               new String[]
                                               {
                                                   "Service Group", "Service Container", "Organization", "Pool", "Read",
                                                   "Write", "Read Wait Average", "Write wait average", "Read Wait Occ",
                                                   "Write Wait Occ"
                                               }));
        m_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        m_table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
                                   {
                                       /**
                                        * @see  javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable,
                                        *       java.lang.Object, boolean, boolean, int, int)
                                        */
                                       @Override public Component getTableCellRendererComponent(JTable table,
                                                                                                Object value,
                                                                                                boolean isSelected,
                                                                                                boolean hasFocus,
                                                                                                int row, int column)
                                       {
                                           row = table.convertRowIndexToModel(row);

                                           DBConnectionPoolInfo pi = (DBConnectionPoolInfo) table.getModel().getValueAt(row,
                                                                                                                        3);

                                           if (column == 4)
                                           {
                                               if (pi.getActiveRead() == pi.getMaximumRead())
                                               {
                                                   setBackground(Color.ORANGE);
                                               }
                                               else
                                               {
                                                   setBackground(Color.GREEN);
                                               }
                                           }
                                           else if (column == 5)
                                           {
                                               if (pi.getActiveWrite() == pi.getMaximumWrite())
                                               {
                                                   setBackground(Color.ORANGE);
                                               }
                                               else
                                               {
                                                   setBackground(Color.GREEN);
                                               }
                                           }
                                           else if (column == 6)
                                           {
                                               if (pi.getReadWaitTime().getCurrent() > 0)
                                               {
                                                   setBackground(Color.ORANGE);
                                               }
                                               else
                                               {
                                                   setBackground(Color.GREEN);
                                               }
                                           }
                                           else if (column == 7)
                                           {
                                               if (pi.getWriteWaitTime().getCurrent() > 0)
                                               {
                                                   setBackground(Color.ORANGE);
                                               }
                                               else
                                               {
                                                   setBackground(Color.GREEN);
                                               }
                                           }
                                           else if (column == 8)
                                           {
                                               if (pi.getReadWaitTime().getTotalValueSinceReset() > 0)
                                               {
                                                   setBackground(Color.ORANGE);
                                               }
                                               else
                                               {
                                                   setBackground(Color.GREEN);
                                               }
                                           }
                                           else if (column == 9)
                                           {
                                               if (pi.getWriteWaitTime().getTotalValueSinceReset() > 0)
                                               {
                                                   setBackground(Color.ORANGE);
                                               }
                                               else
                                               {
                                                   setBackground(Color.GREEN);
                                               }
                                           }
                                           else
                                           {
                                               setBackground(null);
                                           }

                                           return super.getTableCellRendererComponent(table, value, isSelected,
                                                                                      hasFocus, row, column);
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
     * @param  sr  The snapshot result that should be displayed.
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
                    if (DBPoolCollector.class.getName().equals(counter.getDataCollector()))
                    {
                        DBPoolResult mr = (DBPoolResult) values.get(counter);

                        List<DBConnectionPoolInfo> pools = mr.getDBConnectionPoolInfoList();

                        for (DBConnectionPoolInfo pi : pools)
                        {
                            // Create the data for the row.
                            Object[] rowData = new Object[]
                                               {
                                                   asc.getServiceGroup(), asc.getServiceContainer(),
                                                   asc.getOrganization(), pi, pi.getReadSummary(), pi.getWriteSummary(),
                                                   pi.getReadWaitTime().getAverage(),
                                                   pi.getWriteWaitTime().getAverage(),
                                                   pi.getReadWaitTime().getTotalValueSinceReset(),
                                                   pi.getWriteWaitTime().getTotalValueSinceReset()
                                               };
                            dtm.addRow(rowData);
                        }
                    }
                }
            }
        }
    }

    /**
     * The row selection listener used.
     *
     * @author  localpg
     */
    public class SelectionListener
        implements ListSelectionListener
    {
        /**
         * The table to listen to.
         */
        JComponent m_comp;

        /**
         * It is necessary to keep the table since it is not possible to determine the table from the event's source.
         *
         * @param  table  The table to listen to.
         */
        SelectionListener(JComponent table)
        {
            this.m_comp = table;
        }

        /**
         * When the selection has changed.
         *
         * @param  e  The change event.
         */
        public void valueChanged(ListSelectionEvent e)
        {
            ListSelectionModel lsm = null;

            int selectedRow = -1;

            if (m_comp instanceof JTable)
            {
                JTable tbl = (JTable) m_comp;
                selectedRow = tbl.getSelectedRow();
                lsm = tbl.getSelectionModel();
            }
            else if (m_comp instanceof JList)
            {
                JList list = (JList) m_comp;
                selectedRow = list.getSelectedIndex();
                lsm = list.getSelectionModel();
            }

            if (e.getSource() == lsm)
            {
                if (m_comp == m_table)
                {
                    DefaultTableModel dtm = (DefaultTableModel) m_table.getModel();
                    List<DBConnectionPoolInfo> details = new ArrayList<DBConnectionPoolInfo>();

                    if (selectedRow > -1)
                    {
                        Object object = dtm.getValueAt(selectedRow, 3);

                        if (object instanceof DBPoolResult)
                        {
                            DBPoolResult mr = (DBPoolResult) object;

                            if (mr != null)
                            {
                                details = mr.getDBConnectionPoolInfoList();
                            }
                        }
                        else if (object instanceof DBConnectionPoolInfo)
                        {
                            details.add((DBConnectionPoolInfo) object);
                        }
                    }

                    // Create the labels and the text
                    m_details.removeAll();

                    int row = 0;

                    for (DBConnectionPoolInfo md : details)
                    {
                        // Add the summary
                        JLabel lblCodeCache = new JLabel(md.getName() + ":");
                        lblCodeCache.setHorizontalAlignment(SwingConstants.TRAILING);
                        m_details.add(lblCodeCache, "cell 0 " + row + ",alignx trailing");

                        JTextField textField = new JTextField();
                        m_details.add(textField, "cell 1 " + row + ",growx");
                        textField.setColumns(10);
                        textField.setEditable(false);
                        textField.setText("Read: " + md.getReadSummary() + ", Write: " + md.getWriteSummary());

                        row++;

                        // Add the read connection waits
                        lblCodeCache = new JLabel("Read connection wait:");
                        lblCodeCache.setHorizontalAlignment(SwingConstants.TRAILING);
                        m_details.add(lblCodeCache, "cell 0 " + row + ",alignx trailing");

                        textField = new JTextField();
                        m_details.add(textField, "cell 1 " + row + ",growx");
                        textField.setColumns(10);
                        textField.setEditable(false);

                        WaitTime wt = md.getReadWaitTime();
                        textField.setText(wt.toString());

                        row++;

                        // Add the write connection waits
                        lblCodeCache = new JLabel("Write connection wait:");
                        lblCodeCache.setHorizontalAlignment(SwingConstants.TRAILING);
                        m_details.add(lblCodeCache, "cell 0 " + row + ",alignx trailing");

                        textField = new JTextField();
                        m_details.add(textField, "cell 1 " + row + ",growx");
                        textField.setColumns(10);
                        textField.setEditable(false);

                        wt = md.getWriteWaitTime();
                        textField.setText(wt.toString());

                        row++;
                    }

                    m_details.revalidate();
                }
            }
        }
    }
}
