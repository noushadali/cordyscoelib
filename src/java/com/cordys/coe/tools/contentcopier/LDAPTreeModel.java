/**
 *       2003 Cordys R&D B.V. All rights reserved.
 *      The computer program(s) is the proprietary information of Cordys R&D B.V.
 *      and provided under the relevant License Agreement containing restrictions
 *      on use and disclosure. Use is subject to the License Agreement.
 */
package com.cordys.coe.tools.contentcopier;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

/**
 * Created by IntelliJ IDEA. User: pgussow Date: 18-aug-2003 Time: 12:55:35 To change this template
 * use Options | File Templates.
 */
public class LDAPTreeModel extends DefaultTableModel
{
    /**
     * Holds whether or not the content is editable.
     */
    private boolean bEditable;

    /**
     * Constructs a default <code>LDAPTreeModel</code> which is a table of zero columns and zero
     * rows.
     */
    public LDAPTreeModel()
    {
        bEditable = false;
    } // LDAPTreeModel

    /**
     * Constructs a <code>LDAPTreeModel</code> with as many columns as there are elements in <code>
     * columnNames</code> and <code>rowCount</code> of <code>null</code> object values. Each
     * column's name will be taken from the <code>columnNames</code> vector.
     *
     * @param  columnNames  <code>vector</code> containing the names of the new columns; if this is
     *                      <code>null</code> then the model has no columns
     * @param  rowCount     the number of rows the table holds
     *
     * @see    #setDataVector
     * @see    #setValueAt
     */
    public LDAPTreeModel(Vector<?> columnNames, int rowCount)
    {
        super(columnNames, rowCount);
    } // LDAPTreeModel

    /**
     * Constructs a <code>LDAPTreeModel</code> with as many columns as there are elements in <code>
     * columnNames</code> and <code>rowCount</code> of <code>null</code> object values. Each
     * column's name will be taken from the <code>columnNames</code> array.
     *
     * @param  columnNames  <code>array</code> containing the names of the new columns; if this is
     *                      <code>null</code> then the model has no columns
     * @param  rowCount     the number of rows the table holds
     *
     * @see    #setDataVector
     * @see    #setValueAt
     */
    public LDAPTreeModel(Object[] columnNames, int rowCount)
    {
        super(columnNames, rowCount);
    } // LDAPTreeModel

    /**
     * Constructs a LDAPTreeModel and initializes the table.
     *
     * @param  data         the data of the table
     * @param  columnNames  <code>vector</code> containing the names of the new columns
     *
     * @see    #getDataVector
     * @see    #setDataVector
     */
    public LDAPTreeModel(Vector<?> data, Vector<?> columnNames)
    {
        super(data, columnNames);
    } // LDAPTreeModel

    /**
     * Constructs a <code>LDAPTreeModel</code> and initializes the table by passing <code>
     * data</code> and <code>columnNames</code> to the <code>setDataVector</code> method. The first
     * index in the <code>Object[][]</code> array is the row index and the second is the column
     * index.
     *
     * @param  data         the data of the table
     * @param  columnNames  the names of the columns
     *
     * @see    #getDataVector
     * @see    #setDataVector
     */
    public LDAPTreeModel(Object[][] data, Object[] columnNames)
    {
        super(data, columnNames);
    } // LDAPTreeModel

    /**
     * Returns true is the model is editable.
     *
     * @param   iRow     the row whose value is to be queried
     * @param   iColumn  the column whose value is to be queried
     *
     * @return  true
     */
    @Override public boolean isCellEditable(int iRow, int iColumn)
    {
        boolean bReturn = false;

        if (isEditable() && (iColumn == 1))
        {
            bReturn = true;
        }
        return bReturn;
    } // isCellEditable

    /**
     * Returns whether or not the cell is editable.
     *
     * @return  Whether or not the cell is editable.
     */
    public boolean isEditable()
    {
        return bEditable;
    } // isEditable

    /**
     * Sets whether or not the cell is editable.
     *
     * @param  bEditable  Whether or not the cell is editable.
     */
    public void setEditable(boolean bEditable)
    {
        this.bEditable = bEditable;
    } // setEditable
} // LDAPTreeModel
