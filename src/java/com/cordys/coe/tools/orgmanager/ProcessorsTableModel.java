package com.cordys.coe.tools.orgmanager;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

/**
 * This table model is used to display.
 *
 * @author  pgussow
 */
public class ProcessorsTableModel extends AbstractTableModel
{
    /**
     * Holds all column names.
     */
    private static final String[] COLUMN_NAMES = new String[]
                                                 {
                                                     "Processor", "Organization", "Status", "PID",
                                                     "Computer", "Automatic", "Debug", "SOAP docs", 
                                                     "Processing time", "Last time", "Total NOM", "CPU time"
                                                 };
    /**
     * Holds the list of currently shown processors.
     */
    private ArrayList<Processor> m_alProcessors = new ArrayList<Processor>();

    /**
     * This method removes all rows from the table.
     */
    public void clear()
    {
        m_alProcessors.clear();
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
            case 3:
                cReturn = Integer.class;
                break;

            case 5:
            case 6:
                cReturn = Boolean.class;
                break;
                
            case 7:
            case 8:
            case 9:
            case 10:
                cReturn = Long.class;
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
    public ArrayList<Processor> getInternalData()
    {
        return m_alProcessors;
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
        return m_alProcessors.size();
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

        Processor pProcessor = m_alProcessors.get(iRowIndex);

        if (pProcessor != null)
        {
            switch (iColumnIndex)
            {
                case 0:
                    oReturn = pProcessor.getShortName();
                    break;

                case 1:
                    oReturn = pProcessor.getOrganizationShortName();
                    break;

                case 2:
                    oReturn = pProcessor.getStatus();
                    break;

                case 3:
                    oReturn = pProcessor.getProcessID();
                    break;

                case 4:
                    oReturn = pProcessor.getComputer();
                    break;

                case 5:
                    oReturn = pProcessor.startsAutomatically();
                    break;

                case 6:
                    oReturn = pProcessor.isInDebugMode();
                    break;

                case 7:
                    oReturn = pProcessor.getTotalSOAPDocumentsProcessed();
                    break;
                    
                case 8:
                    oReturn = pProcessor.getTotalProcessingTime();
                    break;
                    
                case 9:
                    oReturn = pProcessor.getLastProcessingTime();
                    break;
                    
                case 10:
                    oReturn = pProcessor.getTotalNomMemory();
                    break;
                    
                case 11:
                    oReturn = pProcessor.getTotalCpuTime();
                    break;
                    
                default:
                    oReturn = pProcessor;
                    break;
            }
        }

        return oReturn;
    }

    /**
     * This method is called when the data really changes.
     *
     * @param  alNewProcessors  The list with new processors.
     */
    public void rebuildProcessorList(ArrayList<Processor> alNewProcessors)
    {
        m_alProcessors = new ArrayList<Processor>(alNewProcessors);
        fireTableDataChanged();
    }
}
