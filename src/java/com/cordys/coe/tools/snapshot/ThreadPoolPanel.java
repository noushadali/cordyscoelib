package com.cordys.coe.tools.snapshot;

import com.cordys.coe.tools.snapshot.config.ActualServiceContainer;
import com.cordys.coe.tools.snapshot.config.JMXCounter;
import com.cordys.coe.tools.snapshot.data.SnapshotData;
import com.cordys.coe.tools.snapshot.data.SnapshotResult;
import com.cordys.coe.tools.snapshot.data.collector.DispatcherInfo;
import com.cordys.coe.tools.snapshot.data.collector.WorkerThreadCollector;
import com.cordys.coe.tools.snapshot.data.collector.WorkerThreadResult;
import com.cordys.coe.tools.snapshot.data.handler.ThreadInfo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import java.util.List;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * This panel displays all the thread pools found in the responses. It gets the data from the WorkerThreadCollector.
 *
 * @author  localpg
 */
public class ThreadPoolPanel extends JPanel
{
    /**
     * Holds all threads that were found.
     */
    private JTable m_allThreads;
    /**
     * Holds the threads that are part of this thread pool.
     */
    private JList m_threads;
    /**
     * Holds teh stack trace of the individual thread.
     */
    private JTextArea m_threadInfoDetails;

    /**
     * Creates a new ThreadPoolPanel object.
     */
    public ThreadPoolPanel()
    {
        super();

        setLayout(new BorderLayout(0, 0));

        JSplitPane splitPane_1 = new JSplitPane();
        splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
        add(splitPane_1);

        JScrollPane scrollPane_3 = new JScrollPane();
        splitPane_1.setLeftComponent(scrollPane_3);

        m_allThreads = new JTable();
        m_allThreads.getSelectionModel().addListSelectionListener(new SelectionListener(m_allThreads));

        m_allThreads.setModel(new DefaultTableModel(new Object[][] {},
                                                    new String[]
                                                    {
                                                        "Service Group", "Service Container", "Organization",
                                                        "Dispatcher", "Status"
                                                    })
            {
                boolean[] columnEditables = new boolean[] { false, false, false, false, false, false, false, false };

                @Override public boolean isCellEditable(int row, int column)
                {
                    return columnEditables[column];
                }
            });
        m_allThreads.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        m_allThreads.setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
                                        {
                                            /**
                                             * @see  javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable,
                                             *       java.lang.Object, boolean, boolean, int, int)
                                             */
                                            @Override public Component getTableCellRendererComponent(JTable table,
                                                                                                     Object value,
                                                                                                     boolean isSelected,
                                                                                                     boolean hasFocus,
                                                                                                     int row,
                                                                                                     int column)
                                            {
                                                row = table.convertRowIndexToModel(row);

                                                DispatcherInfo di = (DispatcherInfo) table.getModel().getValueAt(row,
                                                                                                                 3);

                                                if (di.getCurrentWorkers() < di.getMaxConcurrentWorkers())
                                                {
                                                    setBackground(Color.GREEN);
                                                }
                                                else if (di.getCurrentWorkers() >= di.getMaxConcurrentWorkers())
                                                {
                                                    // All threads are created. Let's see if there are active ones. -1 means unlimited workers
                                                    // possible.
                                                    if (di.getMaxConcurrentWorkers() != -1)
                                                    {
                                                        if (di.getActiveWorkers() < di.getMaxConcurrentWorkers())
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
                                                        setBackground(Color.GREEN);
                                                    }
                                                }

                                                return super.getTableCellRendererComponent(table, value, isSelected,
                                                                                           hasFocus, row, column);
                                            }
                                        });
        scrollPane_3.setViewportView(m_allThreads);

        JSplitPane splitPane_2 = new JSplitPane();
        splitPane_1.setRightComponent(splitPane_2);

        m_threads = new JList();
        m_threads.setModel(new DefaultListModel());
        m_threads.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        m_threads.getSelectionModel().addListSelectionListener(new SelectionListener(m_threads));
        m_threads.setCellRenderer(new DefaultListCellRenderer()
            {
                /**
                 * @see  javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList,java.lang.Object,
                 *       int, boolean, boolean)
                 */
                @Override public Component getListCellRendererComponent(JList list, Object value, int index,
                                                                        boolean isSelected, boolean cellHasFocus)
                {
                    ThreadInfo ti = (ThreadInfo) value;

                    if (ti != null)
                    {
                        if (Thread.State.WAITING.name().equals(ti.getState()))
                        {
                            setBackground(Color.GREEN);
                        }
                        else if (Thread.State.BLOCKED.name().equals(ti.getState()))
                        {
                            setBackground(Color.RED);
                        }
                    }
                    return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                }
            });

        JScrollPane scrollPane_5 = new JScrollPane();
        scrollPane_5.setViewportView(m_threads);

        splitPane_2.setLeftComponent(scrollPane_5);

        JPanel panel_5 = new JPanel();
        splitPane_2.setRightComponent(panel_5);
        panel_5.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane_4 = new JScrollPane();
        panel_5.add(scrollPane_4);

        m_threadInfoDetails = new JTextArea();
        scrollPane_4.setViewportView(m_threadInfoDetails);
        splitPane_2.setDividerLocation(200);
        splitPane_1.setDividerLocation(300);
    }

    /**
     * This method updates the panel with the information from the Workerthread collector result.
     *
     * @param  sr  The snapshot result that should be displayed.
     */
    public void updateData(SnapshotResult sr)
    {
        // First clean the current UI.
        DefaultTableModel dtm = (DefaultTableModel) m_allThreads.getModel();

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
                    if (WorkerThreadCollector.class.getName().equals(counter.getDataCollector()))
                    {
                        WorkerThreadResult wtr = (WorkerThreadResult) values.get(counter);
                        List<DispatcherInfo> infos = wtr.getDispatcherInfoList();

                        for (DispatcherInfo info : infos)
                        {
                            addThreadResults(info, asc);
                        }
                    }
                }
            }
        }
    }

    /**
     * This method adds the results from the thread pool to the table.
     *
     * @param  info  The dispatcher information.
     * @param  asc   The service container where it came from.
     */
    private void addThreadResults(DispatcherInfo info, ActualServiceContainer asc)
    {
        DefaultTableModel dtm = (DefaultTableModel) m_allThreads.getModel();

        Object[] rowData = new Object[]
                           {
                               asc.getServiceGroup(), asc.getServiceContainer(), asc.getOrganization(), info,
                               "" + info.getActiveWorkers() + " ( " + info.getCurrentWorkers() + " / " +
                               info.getMaxConcurrentWorkers() + ")"
                           };
        dtm.addRow(rowData);
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
         * @param  e  DOCUMENTME
         */
        public void valueChanged(ListSelectionEvent e)
        {
            ListSelectionModel lsm = null;

            int row = -1;

            if (m_comp instanceof JTable)
            {
                JTable tbl = (JTable) m_comp;
                row = tbl.getSelectedRow();
                lsm = tbl.getSelectionModel();
            }
            else if (m_comp instanceof JList)
            {
                JList list = (JList) m_comp;
                row = list.getSelectedIndex();
                lsm = list.getSelectionModel();
            }

            if (e.getSource() == lsm)
            {
                if (m_comp == m_allThreads)
                {
                    DefaultTableModel dtm = (DefaultTableModel) m_allThreads.getModel();
                    DefaultListModel dlm = (DefaultListModel) m_threads.getModel();
                    
                    //Clean the selection.
                    dlm.removeAllElements();
                    m_threadInfoDetails.setText("");

                    if (row > -1)
                    {
                        DispatcherInfo di = (DispatcherInfo) dtm.getValueAt(row, 3);

                        if (di != null)
                        {
                            List<ThreadInfo> threads = di.getThreadInfoList();

                            for (ThreadInfo ti : threads)
                            {
                                dlm.addElement(ti);
                            }
                        }
                    }
                }
                else if (m_comp == m_threads)
                {
                    Object value = m_threads.getSelectedValue();

                    if (value != null)
                    {
                        ThreadInfo ti = (ThreadInfo) value;
                        m_threadInfoDetails.setText(ti.getStackTrace());
                        m_threadInfoDetails.setCaretPosition(0);
                    }
                    else
                    {
                    	m_threadInfoDetails.setText("");
                    }
                }
            }
        }
    }
}
