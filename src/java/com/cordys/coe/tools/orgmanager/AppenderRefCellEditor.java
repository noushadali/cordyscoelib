package com.cordys.coe.tools.orgmanager;

import java.awt.Component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.AbstractCellEditor;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

import javax.swing.table.TableCellEditor;

/**
 * This renderer can be used to show the appender references and also provide an editor.
 *
 * @author  pgussow
 */
public class AppenderRefCellEditor extends AbstractCellEditor
    implements TableCellEditor
{
    /**
     * The appender provider.
     */
    private IAppenderProvider m_apProvider;
    /**
     * Holds the current appenders.
     */
    private JTextField tAppenders = null;

    /**
     * Creates a new AppenderRefCellEditor object.
     *
     * @param  apProvider  The appender provider.
     */
    public AppenderRefCellEditor(IAppenderProvider apProvider)
    {
        m_apProvider = apProvider;
    }

    /**
     * This mewthod returns the value to store.
     *
     * @return  The value to store.
     */
    public Object getCellEditorValue()
    {
        if (tAppenders != null)
        {
            return tAppenders.getText();
        }

        return null;
    }

    /**
     * @see  javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable,java.lang.Object,
     *       boolean, int, int)
     */
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
                                                 int row, int column)
    {
        JPanel pAppenderRefEditor = new JPanel();
        tAppenders = new JTextField();
        tAppenders.setEnabled(false);
        tAppenders.setBorder(null);
        tAppenders.setText(((value != null) ? value.toString() : ""));

        JButton bBrowse = new JButton();
        bBrowse.setText("...");
        bBrowse.setBorder(null);

        bBrowse.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    String[] asTemp = tAppenders.getText().split(",");
                    ArrayList<String> alSelected = new ArrayList<String>();

                    for (int iCount = 0; iCount < asTemp.length; iCount++)
                    {
                        alSelected.add(asTemp[iCount]);
                    }

                    ChooseAppender ca = new ChooseAppender(new JFrame(), true,
                                                           m_apProvider.getAppenders(), alSelected);
                    ca.setVisible(true);

                    if (ca.isOK())
                    {
                        ArrayList<String> alTemp = ca.getSelectedAppenders();
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
                        tAppenders.setText(sbTemp.toString());
                    }
                }
            });

        GroupLayout pAppenderRefEditorLayout = new GroupLayout(pAppenderRefEditor);
        pAppenderRefEditor.setLayout(pAppenderRefEditorLayout);
        pAppenderRefEditorLayout.setHorizontalGroup(pAppenderRefEditorLayout.createParallelGroup(javax
                                                                                                 .swing
                                                                                                 .GroupLayout
                                                                                                 .Alignment.LEADING)
                                                    .addGroup(GroupLayout.Alignment.TRAILING,
                                                              pAppenderRefEditorLayout
                                                              .createSequentialGroup().addComponent(tAppenders,
                                                                                                    javax
                                                                                                    .swing
                                                                                                    .GroupLayout.DEFAULT_SIZE,
                                                                                                    132,
                                                                                                    Short.MAX_VALUE)
                                                              .addPreferredGap(javax.swing
                                                                               .LayoutStyle
                                                                               .ComponentPlacement.RELATED)
                                                              .addComponent(bBrowse,
                                                                            GroupLayout.PREFERRED_SIZE,
                                                                            22,
                                                                            GroupLayout.PREFERRED_SIZE)));
        pAppenderRefEditorLayout.setVerticalGroup(pAppenderRefEditorLayout.createParallelGroup(javax
                                                                                               .swing
                                                                                               .GroupLayout
                                                                                               .Alignment.LEADING)
                                                  .addGroup(pAppenderRefEditorLayout
                                                            .createParallelGroup(javax.swing
                                                                                 .GroupLayout
                                                                                 .Alignment.BASELINE)
                                                            .addComponent(bBrowse).addComponent(tAppenders,
                                                                                                javax
                                                                                                .swing
                                                                                                .GroupLayout.PREFERRED_SIZE,
                                                                                                javax
                                                                                                .swing
                                                                                                .GroupLayout.DEFAULT_SIZE,
                                                                                                javax
                                                                                                .swing
                                                                                                .GroupLayout.PREFERRED_SIZE)));

        return pAppenderRefEditor;
    }
}
