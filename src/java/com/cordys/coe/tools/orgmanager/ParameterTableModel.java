package com.cordys.coe.tools.orgmanager;

import com.cordys.coe.tools.orgmanager.log4j.BaseWithParam;
import com.cordys.coe.tools.orgmanager.log4j.IPropertyMetadata;
import com.cordys.coe.tools.orgmanager.log4j.Parameter;
import com.cordys.coe.tools.orgmanager.log4j.PropertyMetadata;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

/**
 * This model holds the parameters and their values.
 *
 * @author  pgussow
 */
public class ParameterTableModel extends AbstractTableModel
{
    /**
     * Holds all column names.
     */
    private static final String[] COLUMN_NAMES = new String[] { "Name", "Type", "Value" };
    /**
     * Holds the list of currently shown categories.
     */
    private ArrayList<Parameter> m_alParameters = new ArrayList<Parameter>();
    /**
     * Holds the metadata for the properties.
     */
    private Map<String, PropertyMetadata> m_mMetadata = new LinkedHashMap<String, PropertyMetadata>();
    /**
     * Holds the currently selected appender.
     */
    private BaseWithParam m_aCurrent = null;

    /**
     * This method will add a new empty parameter to the end of the list.
     *
     * @return  The row index.
     */
    public int addNewParameter()
    {
        Parameter c = new Parameter();
        m_alParameters.add(c);

        fireTableRowsInserted(m_alParameters.size() - 1, m_alParameters.size() - 1);

        return m_alParameters.size() - 1;
    }

    /**
     * This method removes all rows from the table.
     */
    public void clear()
    {
        m_alParameters.clear();
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
    public ArrayList<Parameter> getInternalData()
    {
        return m_alParameters;
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
        return m_alParameters.size();
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

        Parameter cParameter = m_alParameters.get(iRowIndex);

        if (cParameter != null)
        {
            switch (iColumnIndex)
            {
                case 0:
                    oReturn = cParameter.getName();
                    break;

                case 1:

                    PropertyMetadata pm = m_mMetadata.get(cParameter.getName());
                    if (pm != null)
                    {
                        oReturn = pm.getType();
                    }
                    break;

                case 2:
                    oReturn = cParameter.getValue();
                    break;

                default:
                    oReturn = cParameter;
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
        return columnIndex == 2;
    }

    /**
     * This method is called when the data really changes.
     *
     * @param  alNewParameters  The list with new processors.
     * @param  amMetadata       The metadata for the given appender.
     */
    public void rebuildParameterList(ArrayList<Parameter> alNewParameters,
                                     IPropertyMetadata amMetadata, BaseWithParam aCurrent)
    {
        m_alParameters = new ArrayList<Parameter>(alNewParameters);
        m_aCurrent = aCurrent;

        m_mMetadata.clear();
        for (PropertyMetadata pm : amMetadata.getProperties())
        {
            m_mMetadata.put(pm.getName(), pm);
        }

        // Now for each parameter that is not yet in the real list we'll add one
        // with an empty value.
        for (PropertyMetadata pm : m_mMetadata.values())
        {
            boolean bFound = false;

            for (Parameter p : m_alParameters)
            {
                if (p.getName().equalsIgnoreCase(pm.getName()))
                {
                    bFound = true;
                    break;
                }
            }

            if (!bFound)
            {
                // Not yet present, add it
                Parameter pNew = new Parameter();
                pNew.setName(pm.getName());
                m_alParameters.add(pNew);
            }
        }

        fireTableDataChanged();
    }

    /**
     * This method removes the given category.
     *
     * @param  sName  The name of the category.
     */
    public void removeParameter(String sName)
    {
        for (int iCount = 0; iCount < m_alParameters.size(); iCount++)
        {
            Parameter c = m_alParameters.get(iCount);

            if (c.getName().equals(sName))
            {
                m_alParameters.remove(iCount);
                fireTableRowsDeleted(iCount, iCount);
            }
        }
    }

    /**
     * This method sets the proper value for the parameter.
     *
     * @param  oValue        DOCUMENTME
     * @param  iRowIndex     DOCUMENTME
     * @param  iColumnIndex  DOCUMENTME
     *
     * @see    javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
     */
    @Override public void setValueAt(Object oValue, int iRowIndex, int iColumnIndex)
    {
        Parameter p = m_alParameters.get(iRowIndex);

        if (p != null)
        {
            switch (iColumnIndex)
            {
                case 2:
                    if (p.getValue() == null)
                    {
                        //Value was not yet set, so now it got an initial value
                        //This means we now need to add it to the current appender
                        if (m_aCurrent != null)
                        {
                            m_aCurrent.addParameter(p);
                        }
                    }

                    p.setValue((String) oValue);
                    break;
            }

            fireTableRowsUpdated(iRowIndex, iRowIndex);
        }
    }
}
