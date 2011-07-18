package com.cordys.coe.tools.orgmanager;

import com.cordys.coe.tools.orgmanager.log4j.Appender;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

/**
 * This table model is used to display and edit the details for a given appender.
 *
 * @author  pgussow
 */
public class AppenderTableModel extends AbstractTableModel
{
    /**
     * Holds all column names.
     */
    private static final String[] COLUMN_NAMES = new String[] { "Name", "Appender classname" };
    /**
     * Holds the list of currently shown appenders.
     */
    private ArrayList<Appender> m_alAppenders = new ArrayList<Appender>();

    /**
     * This method will add a new empty appender to the end of the list.
     *
     * @return  The row index.
     */
    public int addNewAppender()
    {
        Appender a = new Appender();
        m_alAppenders.add(a);

        fireTableRowsInserted(m_alAppenders.size() - 1, m_alAppenders.size() - 1);

        return m_alAppenders.size() - 1;
    }

    /**
     * This method removes all rows from the table.
     */
    public void clear()
    {
        m_alAppenders.clear();
        fireTableDataChanged();
    }

    /**
     * This method returns the data type for the given column.
     *
     * @param   iColumnIndex  The index of the column.
     *
     * @return  The data type for the given column.
     *
     * @see     javax.swing.table.AbstractTableModel#getColumnClass(int)
     */
    @Override public Class<?> getColumnClass(int iColumnIndex)
    {
        Class<?> cReturn = String.class;

        return cReturn;
    }

    /**
     * This method returns the number of columns that this table has.
     *
     * @return  The number of columns for this table.
     *
     * @see     javax.swing.table.TableModel#getColumnCount()
     */
    @Override public int getColumnCount()
    {
        return COLUMN_NAMES.length;
    }

    /**
     * This method returns the caption for the given column.
     *
     * @param   iColumnIndex  The index of the column.
     *
     * @return  The name of the column.
     *
     * @see     javax.swing.table.AbstractTableModel#getColumnName(int)
     */
    @Override public String getColumnName(int iColumnIndex)
    {
        return COLUMN_NAMES[iColumnIndex];
    }

    /**
     * This method gets the internal data array.
     *
     * @return  The internal data array.
     */
    public ArrayList<Appender> getInternalData()
    {
        return m_alAppenders;
    }

    /**
     * This method returns the number of processors in the list.
     *
     * @return  The number of processors in the list.
     *
     * @see     javax.swing.table.TableModel#getRowCount()
     */
    @Override public int getRowCount()
    {
        return m_alAppenders.size();
    }

    /**
     * This method returns the value for the given column.
     *
     * @param   iRowIndex     The row index.
     * @param   iColumnIndex  The column index.
     *
     * @return  The value at the given location.
     *
     * @see     javax.swing.table.TableModel#getValueAt(int, int)
     */
    @Override public Object getValueAt(int iRowIndex, int iColumnIndex)
    {
        Object oReturn = null;

        if (iRowIndex >= 0 && !m_alAppenders.isEmpty() && iRowIndex < m_alAppenders.size())
        {
        Appender aAppender = m_alAppenders.get(iRowIndex);

        if (aAppender != null)
        {
            switch (iColumnIndex)
            {
                case 0:
                    oReturn = aAppender.getName();
                    break;

                case 1:

                    oReturn = aAppender.getClassName();
                    break;

                default:
                    oReturn = aAppender;
                    break;
            }
        }
        }

        return oReturn;
    }

    /**
     * @see  javax.swing.table.AbstractTableModel#isCellEditable(int, int)
     */
    @Override public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        return false;
    }

    /**
     * This method is called when the data really changes.
     *
     * @param  alNewAppenders  The list with new processors.
     */
    public void rebuildAppenderList(ArrayList<Appender> alNewAppenders)
    {
        m_alAppenders = new ArrayList<Appender>(alNewAppenders);
        fireTableDataChanged();
    }

    /**
     * This method returns the appender with the given name.
     * 
     * @param sName The name of the appender.
     * 
     * @return The appender object.
     */
    public Appender getAppender(String sName)
    {
        Appender aReturn = null;
        for (int iCount = 0; iCount < m_alAppenders.size(); iCount++)
        {
            Appender a = m_alAppenders.get(iCount);

            //sName could be null.
            if (sName == a.getName() || a.getName().equals(sName))
            {
                aReturn = a;
                break;
            }
        }
        
        
        return aReturn;
    }

    /**
     * This method removes the given appender.
     *
     * @param  sName  The name of the appender.
     */
    public void removeAppender(String sName)
    {
        for (int iCount = 0; iCount < m_alAppenders.size(); iCount++)
        {
            Appender a = m_alAppenders.get(iCount);

            //sName could be null.
            if (sName == a.getName() || a.getName().equals(sName))
            {
                m_alAppenders.remove(iCount);
                fireTableRowsDeleted(iCount, iCount);
            }
        }
    }

    /**
     * @see  javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
     */
    @Override public void setValueAt(Object oValue, int iRowIndex, int iColumnIndex)
    {
        Appender a = m_alAppenders.get(iRowIndex);

        if (a != null)
        {
            switch (iColumnIndex)
            {
                case 0:
                    a.setName((String) oValue);
                    break;

                case 1:
                    a.setClassName((String) oValue);
                    break;
            }

            fireTableRowsUpdated(iRowIndex, iRowIndex);
        }
    }
}
