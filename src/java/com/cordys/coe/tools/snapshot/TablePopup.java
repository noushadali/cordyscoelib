package com.cordys.coe.tools.snapshot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import javax.swing.table.DefaultTableModel;

/**
 * This class wraps a popup menu for a table.
 */
class TablePopup extends JPopupMenu
{
    /**
     * Holds the table that is wrapped.
     */
    private JTable m_table;

    /**
     * Creates the popup menu for the given table.
     * 
     * @param type The type name.
     * @param table The table that is being wrapped.
     */
    public TablePopup(String type, JTable table)
    {
        super(type);
        m_table = table;

        JMenuItem miInsert = new JMenuItem("Insert new " + type);
        miInsert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                DefaultTableModel dtm = (DefaultTableModel) m_table.getModel();
                int selectedRow = m_table.getSelectedRow();

                if (selectedRow == -1)
                {
                    selectedRow = 0;
                }

                dtm.insertRow(selectedRow, new Vector<Object>());
            }
        });
        add(miInsert);

        JMenuItem miDelete = new JMenuItem("Delete " + type);
        miDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                DefaultTableModel dtm = (DefaultTableModel) m_table.getModel();
                int selectedRow = m_table.getSelectedRow();

                if (selectedRow != -1)
                {
                    dtm.removeRow(selectedRow);
                }
            }
        });
        add(miDelete);

        m_table.addMouseListener(new MouseAdapter() {
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
                if (e.isPopupTrigger() && (m_table.getModel() != null))
                {
                    JTable source = (JTable) e.getSource();
                    int row = source.rowAtPoint(e.getPoint());
                    int column = source.columnAtPoint(e.getPoint());

                    if (!source.isRowSelected(row))
                    {
                        source.changeSelection(row, column, false, false);
                    }

                    TablePopup.this.show(m_table, e.getX(), e.getY());
                }
            }
        });
    }
}
