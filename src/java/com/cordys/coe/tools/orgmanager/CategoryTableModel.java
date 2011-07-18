package com.cordys.coe.tools.orgmanager;

import com.cordys.coe.tools.orgmanager.log4j.Category;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.table.AbstractTableModel;

/**
 * This table model is used to display the categories.
 *
 * @author  pgussow
 */
public class CategoryTableModel extends AbstractTableModel
{
    /**
     * Holds all column names.
     */
    private static final String[] COLUMN_NAMES = new String[]
                                                 {
                                                     "Category", "Appender", "OFF", "TRACE",
                                                     "DEBUG", "INFO", "WARN", "ERROR", "FATAL"
                                                 };
    /**
     * Holds the list of currently shown categories.
     */
    private ArrayList<Category> m_alCategories = new ArrayList<Category>();

    /**
     * This method will add a new empty appender to the end of the list.
     *
     * @return  The row index.
     */
    public int addNewCategory()
    {
        Category c = new Category();
        m_alCategories.add(c);

        fireTableRowsInserted(m_alCategories.size() - 1, m_alCategories.size() - 1);

        return m_alCategories.size() - 1;
    }

    /**
     * This method removes all rows from the table.
     */
    public void clear()
    {
        m_alCategories.clear();
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

        switch (iColumnIndex)
        {
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                cReturn = Boolean.class;
                break;
        }

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
    public ArrayList<Category> getInternalData()
    {
        return m_alCategories;
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
        return m_alCategories.size();
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

        Category cCategory = m_alCategories.get(iRowIndex);

        if (cCategory != null)
        {
            switch (iColumnIndex)
            {
                case 0:
                    oReturn = cCategory.getName();
                    break;

                case 1:

                    ArrayList<String> alTemp = cCategory.getAppenderReferences();
                    StringBuilder sbTemp = new StringBuilder(2048);
                    for (Iterator<String> iAppenders = alTemp.iterator(); iAppenders.hasNext();)
                    {
                        String sAppender = iAppenders.next();
                        sbTemp.append(sAppender);

                        if (iAppenders.hasNext())
                        {
                            sbTemp.append(",");
                        }
                    }
                    oReturn = sbTemp.toString();
                    break;

                case 2:
                    oReturn = cCategory.isTurnedOff();
                    break;

                case 3:
                    oReturn = cCategory.isTraceEnabled();
                    break;

                case 4:
                    oReturn = cCategory.isDebugEnabled();
                    break;

                case 5:
                    oReturn = cCategory.isInfoEnabled();
                    break;

                case 6:
                    oReturn = cCategory.isWarnEnabled();
                    break;

                case 7:
                    oReturn = cCategory.isErrorEnabled();
                    break;

                case 8:
                    oReturn = cCategory.isFatalEnabled();
                    break;

                default:
                    oReturn = cCategory;
                    break;
            }
        }

        return oReturn;
    }

    /**
     * @see  javax.swing.table.AbstractTableModel#isCellEditable(int, int)
     */
    @Override public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        return true;
    }

    /**
     * This method is called when the data really changes.
     *
     * @param  alNewCategories  The list with new processors.
     */
    public void rebuildCategoryList(ArrayList<Category> alNewCategories)
    {
        m_alCategories = new ArrayList<Category>(alNewCategories);
        fireTableDataChanged();
    }

    /**
     * This method removes the given category.
     *
     * @param  sName  The name of the category.
     */
    public void removeCategory(String sName)
    {
        for (int iCount = 0; iCount < m_alCategories.size(); iCount++)
        {
            Category c = m_alCategories.get(iCount);

            if (sName == c.getName() || c.getName().equals(sName))
            {
                m_alCategories.remove(iCount);
                fireTableRowsDeleted(iCount, iCount);
            }
        }
    }

    /**
     * @see  javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
     */
    @Override public void setValueAt(Object oValue, int iRowIndex, int iColumnIndex)
    {
        Category c = m_alCategories.get(iRowIndex);

        if (c != null)
        {
            switch (iColumnIndex)
            {
                case 0:
                    c.setName((String) oValue);
                    break;

                case 1:
                    c.setAppenderReferences((String) oValue);
                    break;

                case 2:

                    boolean bEnabled = (Boolean) oValue;
                    if (bEnabled)
                    {
                        c.setLogLevel("off");
                    }
                    else
                    {
                        c.setLogLevel("error");
                    }
                    break;

                case 3:
                    bEnabled = (Boolean) oValue;
                    if (bEnabled)
                    {
                        c.setLogLevel("trace");
                    }
                    else
                    {
                        c.setLogLevel("bedug");
                    }
                    break;

                case 4:
                    bEnabled = (Boolean) oValue;
                    if (bEnabled)
                    {
                        c.setLogLevel("debug");
                    }
                    else
                    {
                        c.setLogLevel("info");
                    }
                    break;

                case 5:
                    bEnabled = (Boolean) oValue;
                    if (bEnabled)
                    {
                        c.setLogLevel("info");
                    }
                    else
                    {
                        c.setLogLevel("warn");
                    }
                    break;

                case 6:
                    bEnabled = (Boolean) oValue;
                    if (bEnabled)
                    {
                        c.setLogLevel("warn");
                    }
                    else
                    {
                        c.setLogLevel("error");
                    }
                    break;

                case 7:
                    bEnabled = (Boolean) oValue;
                    if (bEnabled)
                    {
                        c.setLogLevel("error");
                    }
                    else
                    {
                        c.setLogLevel("fatal");
                    }
                    break;

                case 8:
                    bEnabled = (Boolean) oValue;
                    if (bEnabled)
                    {
                        c.setLogLevel("fatal");
                    }
                    else
                    {
                        c.setLogLevel("off");
                    }
                    break;
            }

            fireTableRowsUpdated(iRowIndex, iRowIndex);
        }
    }
}
