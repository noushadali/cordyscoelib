package com.cordys.coe.tools.orgmanager;

import com.cordys.coe.tools.orgmanager.log4j.Appender;
import com.cordys.coe.tools.orgmanager.log4j.AppenderMetadata;
import com.cordys.coe.tools.orgmanager.log4j.Category;
import com.cordys.coe.tools.orgmanager.log4j.Layout;
import com.cordys.coe.tools.orgmanager.log4j.LayoutMetadata;
import com.cordys.coe.tools.orgmanager.log4j.Log4JAppenderWrapper;
import com.cordys.coe.tools.orgmanager.log4j.Log4JConfigurationWrapper;
import com.cordys.coe.tools.orgmanager.log4j.Parameter;
import com.cordys.coe.tools.orgmanager.log4j.Root;
import com.cordys.coe.util.cgc.ICordysGatewayClient;
import com.cordys.coe.util.swing.MessageBoxUtil;
import com.cordys.coe.util.swing.SwingUtils;
import com.cordys.coe.util.xml.dom.NiceDOMWriter;
import com.cordys.coe.util.xml.dom.XMLHelper;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.io.ByteArrayInputStream;

import java.net.InetAddress;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.net.SocketAppender;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This panel shows the details of a soap processor.
 *
 * @author  pgussow
 */
public class SoapProcessorDetails extends javax.swing.JPanel
    implements IAppenderProvider
{
    /**
     * DOCUMENTME.
     */
    javax.swing.JComboBox<String> m_cbRootPriority;
    /**
     * DOCUMENTME.
     */
    javax.swing.JCheckBox m_cbStartAutomatically;
    /**
     * DOCUMENTME.
     */
    javax.swing.JCheckBox m_cbSystemPolicy;
    /**
     * DOCUMENTME.
     */
    javax.swing.JEditorPane m_epLog4J;
    /**
     * DOCUMENTME.
     */
    javax.swing.JEditorPane m_epXML;
    /**
     * DOCUMENTME.
     */
    javax.swing.JTextField m_tAppenderName;
    /**
     * DOCUMENTME.
     */
    javax.swing.JTable m_tblAppender;
    /**
     * DOCUMENTME.
     */
    javax.swing.JTable m_tblCategories;
    /**
     * DOCUMENTME.
     */
    javax.swing.JTable m_tblClasspath;
    /**
     * DOCUMENTME.
     */
    javax.swing.JTable m_tblJVMOptions;
    /**
     * DOCUMENTME.
     */
    javax.swing.JTextField m_tComputer;
    /**
     * DOCUMENTME.
     */
    javax.swing.JTextField m_tCustomAttempts;
    /**
     * DOCUMENTME.
     */
    javax.swing.JTextField m_tDN;
    /**
     * DOCUMENTME.
     */
    javax.swing.JTextField m_tName;
    /**
     * DOCUMENTME.
     */
    javax.swing.JTextField m_tOSProcess;
    /**
     * DOCUMENTME.
     */
    javax.swing.JTabbedPane m_tpLog4JDetails;
    /**
     * DOCUMENTME.
     */
    javax.swing.JTextField m_tRequestNotification;
    /**
     * DOCUMENTME.
     */
    javax.swing.JTextField m_tRootAppenders;
    /**
     * DOCUMENTME.
     */
    javax.swing.JRadioButton rbCustom;
    /**
     * DOCUMENTME.
     */
    javax.swing.JRadioButton rbDefault;
    /**
     * DOCUMENTME.
     */
    javax.swing.JRadioButton rbInfinite;
    // End of variables declaration//GEN-END:variables
    /**
     * Variables declaration - do not modify//GEN-BEGIN:variables.
     */
    private javax.swing.ButtonGroup bgAutostart;
    /**
     * DOCUMENTME.
     */
    private javax.swing.JComboBox<String> m_cbLayout;
    /**
     * Holds the connection to use for saving the configuration.
     */
    private ICordysGatewayClient m_cgcClient;
    /**
     * Holds the currently selected tab.
     */
    private int m_iCurrentTab = 0;
    /**
     * Holds the index of the main tab pages.
     */
    private int m_iMainIndex = 0;
    /**
     * Holds the appender metadata.
     */
    private Log4JAppenderWrapper m_law = null;
    /**
     * Holds the configuration wrapper for this processor.
     */
    private Log4JConfigurationWrapper m_lcLog4JConfiguration;
    /**
     * Holds the table model for the appenders.
     */
    private AppenderTableModel m_mdlAppenders = new AppenderTableModel();
    /**
     * Holds the table model for the categories.
     */
    private CategoryTableModel m_mdlCategories = new CategoryTableModel();
    /**
     * Holds the table model for the layout parameters.
     */
    private ParameterTableModel m_mdlLayoutParameters = new ParameterTableModel();
    /**
     * Holds the table model for the parameters.
     */
    private ParameterTableModel m_mdlParameters = new ParameterTableModel();
    /**
     * Holds the processor handler to use.
     */
    private ProcessorHandler m_phProcesses;
    /**
     * Holds the currently selected processor.
     */
    private Processor m_pProcessor;
    /**
     * DOCUMENTME.
     */
    private javax.swing.JTextField m_tAppender;
    /**
     * DOCUMENTME.
     */
    private javax.swing.JTable m_tblLayoutProperties;
    /**
     * Holds the tab pane that contains the main tabs.
     */
    private javax.swing.JTabbedPane m_tpMain;

    /**
     * Creates new form SoapProcessorDetails.
     */
    public SoapProcessorDetails()
    {
        initComponents();

        TableColumnModel cm = m_tblCategories.getColumnModel();
        Enumeration<TableColumn> e = cm.getColumns();

        while (e.hasMoreElements())
        {
            TableColumn tc = e.nextElement();

            if (tc.getHeaderValue().equals("Appender"))
            {
                tc.setCellEditor(new AppenderRefCellEditor(this));
            }
        }

        // Add the default appenders to the list.
        try
        {
            m_law = new Log4JAppenderWrapper();

            ArrayList<LayoutMetadata> al = m_law.getLayoutMetadata();

            for (LayoutMetadata lm : al)
            {
                m_cbLayout.addItem(lm.getName());
            }
        }
        catch (Exception e1)
        {
            MessageBoxUtil.showError("Error getting appender metadata", e1);
        }

        // Add the row change listener for the currently selected appender
        m_tblAppender.getSelectionModel().addListSelectionListener(new ListSelectionListener()
            {
                public void valueChanged(ListSelectionEvent lse)
                {
                    if (!lse.getValueIsAdjusting())
                    {
                        ListSelectionModel rowSM = (ListSelectionModel) lse.getSource();
                        int iSelectedIndex = rowSM.getMinSelectionIndex();

                        Appender a = (Appender) m_mdlAppenders.getValueAt(iSelectedIndex, -1);

                        if (a != null)
                        {
                            m_tAppenderName.setText(a.getName());
                            m_tAppender.setText(a.getClassName());

                            // Now first fill the possible parameters.
                            ArrayList<AppenderMetadata> al = m_law.getAppenderMetadata();

                            for (AppenderMetadata am : al)
                            {
                                if (am.getName().equals(m_tAppender.getText()))
                                {
                                    m_mdlParameters.rebuildParameterList(a.getParameters(), am, a);

                                    // Now also do the layout if needed.
                                    if (am.getRequiresLayout())
                                    {
                                        m_cbLayout.setEnabled(true);
                                        m_tblLayoutProperties.setEnabled(true);

                                        Layout l = a.getLayout();

                                        if (l == null)
                                        {
                                            l = new Layout();
                                            a.setLayout(l);
                                        }

                                        m_cbLayout.setSelectedItem(l.getClassName());

                                        showLayoutProperties();
                                    }
                                    else
                                    {
                                        m_cbLayout.setEnabled(false);
                                        m_tblLayoutProperties.setEnabled(false);
                                        m_mdlLayoutParameters.clear();
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            });

        m_cbLayout.addItemListener(new ItemListener()
            {
                public void itemStateChanged(ItemEvent e)
                {
                    // The layout was changed, so we need to clear the properties.
                    Appender a = (Appender) m_mdlAppenders.getValueAt(m_tblAppender
                                                                      .getSelectedRow(), -1);

                    if (a != null)
                    {
                        Layout l = a.getLayout();

                        if (l == null)
                        {
                            l = new Layout();
                            a.setLayout(l);
                        }

                        l.setClassName((String) e.getItem());
                    }

                    showLayoutProperties();
                }
            });
    }

    /**
     * This method returns all current appenders.
     *
     * @return  The list of current appenders.
     */
    public ArrayList<Appender> getAppenders()
    {
        return m_lcLog4JConfiguration.getAppenders();
    }

    /**
     * This method sets the processor that should be displayed.
     *
     * @param  pProcessor  The processor to display. When left null all information will be cleared.
     */
    public void setProcessor(Processor pProcessor)
    {
        if (pProcessor == null)
        {
            clearDetails();
        }
        else
        {
            m_pProcessor = pProcessor;
            m_tDN.setText(pProcessor.getDN());
            m_epXML.setText(NiceDOMWriter.write(pProcessor.getConfigurationXML(), 2, true, true,
                                                false));
            m_epXML.setCaretPosition(0);
            m_tName.setText(pProcessor.getShortName());
            m_tComputer.setText(pProcessor.getComputer());
            m_tOSProcess.setText(pProcessor.getOSProcess());
            m_cbStartAutomatically.setSelected(pProcessor.startsAutomatically());
            m_tRequestNotification.setText(String.valueOf(pProcessor.getCancelReplyInterval()));

            long iAutoStart = pProcessor.getAutoStartCount();
            m_tCustomAttempts.setEnabled(false);

            if (iAutoStart == 3)
            {
                rbDefault.setSelected(true);
            }
            else if (iAutoStart == 0)
            {
                rbInfinite.setSelected(true);
            }
            else
            {
                rbCustom.setSelected(true);
                m_tCustomAttempts.setText(String.valueOf(iAutoStart));
                m_tCustomAttempts.setEnabled(true);
            }

            // Set the classpath
            String[] asClasspath = pProcessor.getCustomClasspath();
            DefaultTableModel dtm = (DefaultTableModel) m_tblClasspath.getModel();

            while (dtm.getRowCount() > 0)
            {
                dtm.removeRow(0);
            }

            for (int iCount = 0; iCount < asClasspath.length; iCount++)
            {
                String sEntry = asClasspath[iCount];
                dtm.addRow(new Object[] { sEntry });
            }

            // Now set the JRE options.
            dtm = (DefaultTableModel) m_tblJVMOptions.getModel();

            while (dtm.getRowCount() > 0)
            {
                dtm.removeRow(0);
            }

            String[] asJVMOptions = pProcessor.getJVMOptions();

            for (int iCount = 0; iCount < asJVMOptions.length; iCount++)
            {
                String sEntry = asJVMOptions[iCount];
                dtm.addRow(new Object[] { sEntry });
            }

            m_cbSystemPolicy.setSelected(pProcessor.isSystemPolicyEnabled());

            if (m_cbSystemPolicy.isSelected())
            {
                m_tpLog4JDetails.setSelectedIndex(0);
                m_tpLog4JDetails.setEnabled(false);
            }
            else
            {
                m_tpLog4JDetails.setEnabled(true);
            }

            // Set the Log4J XML.
            showLog4JSettings();
        }
    }

    /**
     * This method will update the processor XML based on the currently entered values.
     */
    public void updateProcessorXML()
    {
        // We need to figure out which tab had the focus and only update when the
        // tab has changed.
        if (m_iMainIndex == 2)
        {
            // Perhaps the XML was edited
            String sNewXML = m_epXML.getText();
            String sCurrent = NiceDOMWriter.write(m_pProcessor.getConfigurationXML(), 2, true,
                                                  false, false);

            if (!sNewXML.equals(sCurrent))
            {
                // Reparse it.
                try
                {
                    Document dDoc = XMLHelper.createDocumentBuilder(false).parse(new ByteArrayInputStream(sNewXML
                                                                                                          .getBytes()));
                    m_pProcessor.setConfigurationXML(dDoc.getDocumentElement());
                    setProcessor(m_pProcessor);
                }
                catch (Exception ex)
                {
                    MessageBoxUtil.showError("Error parsing new processor configuration", ex);
                }
            }
        }
        else
        {
            if (m_iMainIndex == 0)
            {
                // Update the details for the current processor.
                m_pProcessor.setComputer(m_tComputer.getText());
                m_pProcessor.setOSProcess(m_tOSProcess.getText());
                m_pProcessor.setCancelReplyInterval(Long.parseLong(m_tRequestNotification
                                                                   .getText()));
                m_pProcessor.setStartAutomatically(m_cbStartAutomatically.isSelected());

                Enumeration<AbstractButton> e = bgAutostart.getElements();

                while (e.hasMoreElements())
                {
                    AbstractButton ab = e.nextElement();

                    if (ab.isSelected())
                    {
                        String sActionCommand = ab.getActionCommand();

                        if (sActionCommand.equals("Default"))
                        {
                            m_pProcessor.setAutoStartCount(3);
                        }
                        else if (sActionCommand.equals("Infinite"))
                        {
                            m_pProcessor.setAutoStartCount(0);
                        }
                        else
                        {
                            m_pProcessor.setAutoStartCount(Long.parseLong(m_tCustomAttempts
                                                                          .getText()));
                        }
                    }
                }

                // Now we need to update the classpath AND the custom JVM properties.
                ArrayList<String> al = new ArrayList<String>();
                DefaultTableModel dtm = (DefaultTableModel) m_tblClasspath.getModel();

                for (int iCount = 0; iCount < dtm.getRowCount(); iCount++)
                {
                    al.add((String) dtm.getValueAt(iCount, 0));
                }
                m_pProcessor.setCustomClasspath(al.toArray(new String[0]));

                al = new ArrayList<String>();
                dtm = (DefaultTableModel) m_tblJVMOptions.getModel();

                for (int iCount = 0; iCount < dtm.getRowCount(); iCount++)
                {
                    al.add((String) dtm.getValueAt(iCount, 0));
                }
                m_pProcessor.setJVMOptions(al.toArray(new String[0]));
            }
            else if (m_iMainIndex == 1)
            {
                // Make sure that the logging is set properly.
                updateLog4JConfiguration(null);

                try
                {
                    Document dDoc = XMLHelper.createDocumentBuilder(false).parse(new ByteArrayInputStream(m_epLog4J
                                                                                                          .getText()
                                                                                                          .getBytes()));
                    m_pProcessor.setLog4JConfiguration(dDoc.getDocumentElement());
                    showLog4JSettings();
                }
                catch (Exception ex)
                {
                    MessageBoxUtil.showError("Error parsing new Log4J configuration", ex);
                }
            }

            try
            {
                m_epXML.setText(NiceDOMWriter.write(m_pProcessor.getConfigurationXML(), 2, true,
                                                    false, false));
            }
            catch (Exception e)
            {
                MessageBoxUtil.showError("Error writing Log4J configuration", e);
            }
        }
    }

    /**
     * This method sets the connection that should be used to communicate with Cordys.
     *
     * @param  cgcClient  The client to use.
     */
    void setConnection(ICordysGatewayClient cgcClient)
    {
        m_cgcClient = cgcClient;
    }

    /**
     * This method sets the processor handler to use for restarting the processor.
     *
     * @param  phProcesses  The processor handler.
     */
    void setProcessorHandler(ProcessorHandler phProcesses)
    {
        m_phProcesses = phProcesses;
    }

    /**
     * DOCUMENTME.
     *
     * @param  evt  DOCUMENTME
     */
    private void addDebuggerToJVMOptions(java.awt.event.ActionEvent evt) //GEN-FIRST:event_addDebuggerToJVMOptions
    {
        String sReturn = (String) JOptionPane.showInputDialog(this,
                                                              "Please neter the portnumber for the debugger",
                                                              "Enter debugger port number",
                                                              JOptionPane.QUESTION_MESSAGE, null,
                                                              null, "8840");
        int iPortNumber = -1;

        if (sReturn != null)
        {
            try
            {
                iPortNumber = Integer.parseInt(sReturn);
            }
            catch (NumberFormatException nfe)
            {
                MessageBoxUtil.showError("You must enter a valid integer", nfe);
            }
        }

        if ((iPortNumber != -1) && ((iPortNumber <= 0) || (iPortNumber >= 65535)))
        {
            MessageBoxUtil.showError("You must enter a valid portnumber between 1 and 65535");
        }
        else
        {
            // We should remove the current entries for the debugger
            removeDebuggerFromOptions();

            DefaultTableModel dtm = (DefaultTableModel) m_tblJVMOptions.getModel();
            dtm.addRow(new Object[] { "-Xdebug" });
            dtm.addRow(new Object[] { "-Xnoagent" });
            dtm.addRow(new Object[] { "-Djava.compiler=NONE" });
            dtm.addRow(new Object[]
                       {
                           "-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=" + iPortNumber
                       });
        }
    } //GEN-LAST:event_addDebuggerToJVMOptions

    /**
     * This method sets the new class name for the currently selected appender.
     *
     * @param  tbl  evt
     */
    /**
     * This method adds a new row to the table.
     *
     * @param  tbl  The table add a row to.
     */
    private void addTableRow(JTable tbl)
    {
        DefaultTableModel dtm = (DefaultTableModel) tbl.getModel();
        dtm.addRow(new Object[] { "" });
    }

    /**
     * DOCUMENTME.
     *
     * @param  evt  DOCUMENTME
     */
    private void bAddActionPerformed(java.awt.event.ActionEvent evt) //GEN-FIRST:event_bAddActionPerformed
    {
        addTableRow(m_tblClasspath);
    } //GEN-LAST:event_bAddActionPerformed

    /**
     * DOCUMENTME.
     *
     * @param  evt  DOCUMENTME
     */
    private void bAddAppenderActionPerformed(java.awt.event.ActionEvent evt) //GEN-FIRST:event_bAddAppenderActionPerformed
    {
        NewAppenderDialog nad = new NewAppenderDialog(null, true);
        SwingUtils.centerDialog(nad);
        nad.setVisible(true);

        if (nad.isOK())
        {
            int iRow = m_mdlAppenders.addNewAppender();
            Appender a = (Appender) m_mdlAppenders.getValueAt(iRow, -1);
            a.setName(nad.getAppenderName());
            a.setClassName(nad.getAppenderClassName());

            m_tblAppender.setRowSelectionInterval(iRow, iRow);
        }
    } //GEN-LAST:event_bAddAppenderActionPerformed

    /**
     * DOCUMENTME.
     *
     * @param  evt  DOCUMENTME
     */
    private void bAddCategoryActionPerformed(java.awt.event.ActionEvent evt) //GEN-FIRST:event_bAddCategoryActionPerformed
    {
        int iRow = m_mdlCategories.addNewCategory();
        m_tblCategories.setRowSelectionInterval(iRow, iRow);
    } //GEN-LAST:event_bAddCategoryActionPerformed

    /**
     * DOCUMENTME.
     *
     * @param  evt  DOCUMENTME
     */
    private void bAddJVMActionPerformed(java.awt.event.ActionEvent evt) //GEN-FIRST:event_bAddJVMActionPerformed
    {
        addTableRow(m_tblJVMOptions);
    } //GEN-LAST:event_bAddJVMActionPerformed

    /**
     * DOCUMENTME.
     *
     * @param  evt  DOCUMENTME
     */
    private void bAddSocketAppenderActionPerformed(java.awt.event.ActionEvent evt) //GEN-FIRST:event_bAddSocketAppenderActionPerformed
    {
        // Try to fill it with local machine
        String sDefault = "localhost";

        try
        {
            sDefault = InetAddress.getLocalHost().getHostName();
        }
        catch (Exception e)
        {
            // Ignore it.
        }

        String sHostname = (String) JOptionPane.showInputDialog(this, "Please enter the host name",
                                                                "Enter SocketAppender host name",
                                                                JOptionPane.QUESTION_MESSAGE, null,
                                                                null, sDefault);

        if (sHostname == null)
        {
            return;
        }

        String sReturn = (String) JOptionPane.showInputDialog(this,
                                                              "Please enter the portnumber for the socket appender",
                                                              "Enter SocketAppender port number",
                                                              JOptionPane.QUESTION_MESSAGE, null,
                                                              null, "12555");

        int iPortNumber = -1;

        if (sReturn != null)
        {
            try
            {
                iPortNumber = Integer.parseInt(sReturn);
            }
            catch (NumberFormatException nfe)
            {
                MessageBoxUtil.showError("You must enter a valid integer", nfe);
            }
        }

        if ((iPortNumber != -1) && ((iPortNumber <= 0) || (iPortNumber >= 65535)))
        {
            MessageBoxUtil.showError("You must enter a valid portnumber between 1 and 65535");
        }
        else
        {
            Appender a = m_mdlAppenders.getAppender("SocketAppender");

            if (a == null)
            {
                int iRow = m_mdlAppenders.addNewAppender();
                a = (Appender) m_mdlAppenders.getValueAt(iRow, -1);
            }

            a.setName("SocketAppender");
            a.setClassName(SocketAppender.class.getName());

            a.clearParameters();

            a.addParameter(new Parameter("RemoteHost", sHostname));
            a.addParameter(new Parameter("Port", String.valueOf(iPortNumber)));
            a.addParameter(new Parameter("LocationInfo", "true"));

            Layout l = new Layout();
            l.setClassName("org.apache.log4j.xml.XMLLayout");
            l.addParameter(new Parameter("LocationInfo", "true"));
            a.setLayout(l);
        }
    } //GEN-LAST:event_bAddSocketAppenderActionPerformed

    /**
     * DOCUMENTME.
     *
     * @param  evt  DOCUMENTME
     */
    private void bClasspathMoveBottomActionPerformed(java.awt.event.ActionEvent evt) //GEN-FIRST:event_bClasspathMoveBottomActionPerformed
    {
        moveBottom(m_tblClasspath);
    } //GEN-LAST:event_bClasspathMoveBottomActionPerformed

    /**
     * DOCUMENTME.
     *
     * @param  evt  DOCUMENTME
     */
    private void bClasspathMoveDownActionPerformed(java.awt.event.ActionEvent evt) //GEN-FIRST:event_bClasspathMoveDownActionPerformed
    {
        moveDown(m_tblClasspath);
    } //GEN-LAST:event_bClasspathMoveDownActionPerformed

    /**
     * DOCUMENTME.
     *
     * @param  evt  DOCUMENTME
     */
    private void bClasspathMoveTopActionPerformed(java.awt.event.ActionEvent evt) //GEN-FIRST:event_bClasspathMoveTopActionPerformed
    {
        moveTop(m_tblClasspath);
    } //GEN-LAST:event_bClasspathMoveTopActionPerformed

    /**
     * DOCUMENTME.
     *
     * @param  evt  DOCUMENTME
     */
    private void bClasspathMoveUpActionPerformed(java.awt.event.ActionEvent evt) //GEN-FIRST:event_bClasspathMoveUpActionPerformed
    {
        moveUp(m_tblClasspath);
    } //GEN-LAST:event_bClasspathMoveUpActionPerformed

    /**
     * DOCUMENTME.
     *
     * @param  evt  DOCUMENTME
     */
    private void bJVMMoveBottomActionPerformed(java.awt.event.ActionEvent evt) //GEN-FIRST:event_bJVMMoveBottomActionPerformed
    {
        moveBottom(m_tblJVMOptions);
    } //GEN-LAST:event_bJVMMoveBottomActionPerformed

    /**
     * DOCUMENTME.
     *
     * @param  evt  DOCUMENTME
     */
    private void bJVMMoveDownActionPerformed(java.awt.event.ActionEvent evt) //GEN-FIRST:event_bJVMMoveDownActionPerformed
    {
        moveDown(m_tblJVMOptions);
    } //GEN-LAST:event_bJVMMoveDownActionPerformed

    /**
     * DOCUMENTME.
     *
     * @param  evt  DOCUMENTME
     */
    private void bJVMMoveTopActionPerformed(java.awt.event.ActionEvent evt) //GEN-FIRST:event_bJVMMoveTopActionPerformed
    {
        moveTop(m_tblJVMOptions);
    } //GEN-LAST:event_bJVMMoveTopActionPerformed

    /**
     * DOCUMENTME.
     *
     * @param  evt  DOCUMENTME
     */
    private void bJVMMoveUpActionPerformed(java.awt.event.ActionEvent evt) //GEN-FIRST:event_bJVMMoveUpActionPerformed
    {
        moveUp(m_tblJVMOptions);
    } //GEN-LAST:event_bJVMMoveUpActionPerformed

    /**
     * DOCUMENTME.
     *
     * @param  evt  DOCUMENTME
     */
    private void bRemoveActionPerformed(java.awt.event.ActionEvent evt) //GEN-FIRST:event_bRemoveActionPerformed
    {
        removeTableRow(m_tblClasspath);
    } //GEN-LAST:event_bRemoveActionPerformed

    /**
     * DOCUMENTME.
     *
     * @param  evt  DOCUMENTME
     */
    private void bRemoveAppenderActionPerformed(java.awt.event.ActionEvent evt) //GEN-FIRST:event_bRemoveAppenderActionPerformed
    {
        int iRow = m_tblAppender.getSelectedRow();

        if (iRow >= 0)
        {
            m_mdlAppenders.removeAppender((String) m_mdlAppenders.getValueAt(iRow, 0));
        }
    } //GEN-LAST:event_bRemoveAppenderActionPerformed

    /**
     * DOCUMENTME.
     *
     * @param  evt  DOCUMENTME
     */
    private void bRemoveCategoryActionPerformed(java.awt.event.ActionEvent evt) //GEN-FIRST:event_bRemoveCategoryActionPerformed
    {
        int iRow = m_tblCategories.getSelectedRow();

        if (iRow >= 0)
        {
            m_mdlCategories.removeCategory((String) m_mdlCategories.getValueAt(iRow, 0));
        }
    } //GEN-LAST:event_bRemoveCategoryActionPerformed

    /**
     * DOCUMENTME.
     *
     * @param  evt  DOCUMENTME
     */
    private void bRemoveJVMActionPerformed(java.awt.event.ActionEvent evt) //GEN-FIRST:event_bRemoveJVMActionPerformed
    {
        removeTableRow(m_tblJVMOptions);
    } //GEN-LAST:event_bRemoveJVMActionPerformed

    /**
     * DOCUMENTME.
     *
     * @param  evt  DOCUMENTME
     */
    private void bShowAppenderChooserActionPerformed(java.awt.event.ActionEvent evt) //GEN-FIRST:event_bShowAppenderChooserActionPerformed
    {
        // Get the currently selected appenders
        String[] asTemp = m_tRootAppenders.getText().split(",");
        ArrayList<String> alSelected = new ArrayList<String>();

        for (int iCount = 0; iCount < asTemp.length; iCount++)
        {
            alSelected.add(asTemp[iCount]);
        }

        ChooseAppender ca = new ChooseAppender(new JFrame(), true,
                                               m_lcLog4JConfiguration.getAppenders(), alSelected);
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
            m_tRootAppenders.setText(sbTemp.toString());
        }
    } //GEN-LAST:event_bShowAppenderChooserActionPerformed

    /**
     * This method clears all inputs.
     */
    private void clearDetails()
    {
        m_tDN.setText("");
        m_epXML.setText("");
        m_tName.setText("");
        m_tComputer.setText("");
        m_tOSProcess.setText("");
        m_cbStartAutomatically.setSelected(false);
        m_tRequestNotification.setText("");
        rbDefault.setSelected(true);
        m_tCustomAttempts.setText("3");
        m_tCustomAttempts.setEnabled(false);

        DefaultTableModel dtm = (DefaultTableModel) m_tblClasspath.getModel();

        while (dtm.getRowCount() > 0)
        {
            dtm.removeRow(0);
        }

        dtm = (DefaultTableModel) m_tblJVMOptions.getModel();

        while (dtm.getRowCount() > 0)
        {
            dtm.removeRow(0);
        }

        m_epLog4J.setText("");

        m_mdlCategories.clear();
        m_mdlAppenders.clear();

        m_cbRootPriority.setSelectedIndex(0);
        m_tRootAppenders.setText("");
    }

    /**
     * This method enables/disabled the Log4J editing based on the system policy.
     *
     * @param  evt  The action event.
     */
    private void enableDisableLog4J(java.awt.event.ActionEvent evt) //GEN-FIRST:event_enableDisableLog4J
    {
        if (m_cbSystemPolicy.isSelected())
        {
            m_tpLog4JDetails.setSelectedIndex(0);
            m_tpLog4JDetails.setEnabled(false);
        }
        else
        {
            m_tpLog4JDetails.setEnabled(true);
        }
    } //GEN-LAST:event_enableDisableLog4J

    /**
     * DOCUMENTME.
     *
     * @param  evt  DOCUMENTME
     */
    private void enableProperBox(java.awt.event.ActionEvent evt) //GEN-FIRST:event_enableProperBox
    {
        if (evt.getSource() == rbCustom)
        {
            m_tCustomAttempts.setEnabled(true);
        }
        else
        {
            m_tCustomAttempts.setEnabled(false);
        }
    } //GEN-LAST:event_enableProperBox

    /**
     * This method sets a certain priority for all categories.
     *
     * @param  evt  The action event.
     */
    private void handleClickAll(java.awt.event.ActionEvent evt) //GEN-FIRST:event_handleClickAll
    {
        String sLevel = evt.getActionCommand().toLowerCase();
        ArrayList<Category> al = m_mdlCategories.getInternalData();

        for (Category cCategory : al)
        {
            cCategory.setLogLevel(sLevel);
        }
        m_mdlCategories.fireTableDataChanged();
    } //GEN-LAST:event_handleClickAll

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    private void initComponents()
    {
        bgAutostart = new javax.swing.ButtonGroup();
        m_tDN = new javax.swing.JTextField();
        m_tpMain = new javax.swing.JTabbedPane();

        javax.swing.JPanel m_pDetails = new javax.swing.JPanel();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        m_tName = new javax.swing.JTextField();

        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        m_tComputer = new javax.swing.JTextField();

        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        m_tOSProcess = new javax.swing.JTextField();

        javax.swing.JLabel jLabel4 = new javax.swing.JLabel();
        m_tRequestNotification = new javax.swing.JTextField();

        javax.swing.JLabel jLabel5 = new javax.swing.JLabel();
        rbDefault = new javax.swing.JRadioButton();
        rbInfinite = new javax.swing.JRadioButton();
        rbCustom = new javax.swing.JRadioButton();
        m_tCustomAttempts = new javax.swing.JTextField();
        m_cbStartAutomatically = new javax.swing.JCheckBox();

        javax.swing.JSplitPane jSplitPane1 = new javax.swing.JSplitPane();
        javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
        javax.swing.JScrollPane jScrollPane3 = new javax.swing.JScrollPane();
        m_tblClasspath = new javax.swing.JTable();

        javax.swing.JButton bClasspathMoveTop = new javax.swing.JButton();
        javax.swing.JButton bClasspathMoveUp = new javax.swing.JButton();
        javax.swing.JButton bClasspathMoveDown = new javax.swing.JButton();
        javax.swing.JButton bClasspathMoveBottom = new javax.swing.JButton();
        javax.swing.JToolBar jToolBar1 = new javax.swing.JToolBar();
        javax.swing.JLabel jLabel6 = new javax.swing.JLabel();
        javax.swing.JToolBar.Separator jSeparator1 = new javax.swing.JToolBar.Separator();
        javax.swing.JButton bAdd = new javax.swing.JButton();
        javax.swing.JButton bRemove = new javax.swing.JButton();
        javax.swing.JPanel jPanel2 = new javax.swing.JPanel();
        javax.swing.JScrollPane jScrollPane4 = new javax.swing.JScrollPane();
        m_tblJVMOptions = new javax.swing.JTable();

        javax.swing.JButton bJVMMoveTop = new javax.swing.JButton();
        javax.swing.JButton bJVMMoveUp = new javax.swing.JButton();
        javax.swing.JButton bJVMMoveDown = new javax.swing.JButton();
        javax.swing.JButton bJVMMoveBottom = new javax.swing.JButton();
        javax.swing.JToolBar jToolBar2 = new javax.swing.JToolBar();
        javax.swing.JLabel jLabel7 = new javax.swing.JLabel();
        javax.swing.JToolBar.Separator jSeparator2 = new javax.swing.JToolBar.Separator();
        javax.swing.JButton bAddJVM = new javax.swing.JButton();
        javax.swing.JButton bRemoveJVM = new javax.swing.JButton();
        javax.swing.JToolBar.Separator jSeparator3 = new javax.swing.JToolBar.Separator();
        javax.swing.JButton bAddDebugger = new javax.swing.JButton();
        javax.swing.JButton bRemoveDebugger = new javax.swing.JButton();
        javax.swing.JPanel m_pLogging = new javax.swing.JPanel();
        m_cbSystemPolicy = new javax.swing.JCheckBox();
        m_tpLog4JDetails = new javax.swing.JTabbedPane();

        javax.swing.JPanel pLog4JXML = new javax.swing.JPanel();
        javax.swing.JScrollPane jScrollPane2 = new javax.swing.JScrollPane();
        m_epLog4J = new javax.swing.JEditorPane();

        javax.swing.JPanel pCategories = new javax.swing.JPanel();
        javax.swing.JScrollPane jScrollPane5 = new javax.swing.JScrollPane();
        JComboBox<String> cbLogLevels = new JComboBox<String>();
        cbLogLevels.addItem("TRACE");
        cbLogLevels.addItem("DEBUG");
        cbLogLevels.addItem("INFO");
        cbLogLevels.addItem("WARN");
        cbLogLevels.addItem("ERROR");
        cbLogLevels.addItem("FATAL");
        m_tblCategories = new javax.swing.JTable();

        javax.swing.JToolBar jToolBar3 = new javax.swing.JToolBar();
        javax.swing.JButton bAddCategory = new javax.swing.JButton();
        javax.swing.JButton bRemoveCategory = new javax.swing.JButton();
        javax.swing.JToolBar.Separator jSeparator4 = new javax.swing.JToolBar.Separator();
        javax.swing.JLabel jLabel10 = new javax.swing.JLabel();
        javax.swing.JButton jButton3 = new javax.swing.JButton();
        javax.swing.JButton jButton9 = new javax.swing.JButton();
        javax.swing.JButton jButton4 = new javax.swing.JButton();
        javax.swing.JButton jButton5 = new javax.swing.JButton();
        javax.swing.JButton jButton6 = new javax.swing.JButton();
        javax.swing.JButton jButton7 = new javax.swing.JButton();
        javax.swing.JButton jButton8 = new javax.swing.JButton();
        javax.swing.JPanel jPanel4 = new javax.swing.JPanel();
        javax.swing.JLabel jLabel11 = new javax.swing.JLabel();
        m_cbRootPriority = new javax.swing.JComboBox<String>();

        javax.swing.JLabel jLabel12 = new javax.swing.JLabel();
        m_tRootAppenders = new javax.swing.JTextField();

        javax.swing.JButton bShowAppenderChooser = new javax.swing.JButton();
        javax.swing.JPanel pAppenders = new javax.swing.JPanel();
        javax.swing.JScrollPane jScrollPane6 = new javax.swing.JScrollPane();
        m_tblAppender = new javax.swing.JTable();

        javax.swing.JToolBar jToolBar4 = new javax.swing.JToolBar();
        javax.swing.JButton bAddAppender = new javax.swing.JButton();
        javax.swing.JButton bRemoveAppender = new javax.swing.JButton();
        javax.swing.JToolBar.Separator jSeparator5 = new javax.swing.JToolBar.Separator();
        javax.swing.JButton bAddSocketAppender = new javax.swing.JButton();
        javax.swing.JPanel jPanel3 = new javax.swing.JPanel();
        javax.swing.JLabel jLabel8 = new javax.swing.JLabel();
        m_tAppenderName = new javax.swing.JTextField();

        javax.swing.JLabel jLabel9 = new javax.swing.JLabel();
        m_tAppender = new javax.swing.JTextField();

        javax.swing.JSplitPane jSplitPane2 = new javax.swing.JSplitPane();
        javax.swing.JScrollPane jScrollPane7 = new javax.swing.JScrollPane();
        javax.swing.JTable m_tblAppenderProperties = new javax.swing.JTable();
        javax.swing.JPanel jPanel5 = new javax.swing.JPanel();
        javax.swing.JLabel jLabel13 = new javax.swing.JLabel();
        m_cbLayout = new javax.swing.JComboBox<String>();

        javax.swing.JScrollPane jScrollPane8 = new javax.swing.JScrollPane();
        m_tblLayoutProperties = new javax.swing.JTable();

        javax.swing.JPanel m_pXMLConfig = new javax.swing.JPanel();
        javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        m_epXML = new javax.swing.JEditorPane();

        javax.swing.JButton bSave = new javax.swing.JButton();

        m_tDN.setEditable(false);

        jLabel1.setText("Name:");

        m_tName.setEditable(false);

        jLabel2.setText("Computer:");

        jLabel3.setText("OS process:");

        jLabel4.setText("Request Notification Timeout (ms)");

        jLabel5.setText("Number Of Attempts For Auto Start:");

        bgAutostart.add(rbDefault);
        rbDefault.setSelected(true);
        rbDefault.setText("Default");
        rbDefault.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    enableProperBox(evt);
                }
            });

        bgAutostart.add(rbInfinite);
        rbInfinite.setText("Infinite");
        rbInfinite.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    enableProperBox(evt);
                }
            });

        bgAutostart.add(rbCustom);
        rbCustom.setText("Custom:");
        rbCustom.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    enableProperBox(evt);
                }
            });

        m_tCustomAttempts.setText("3");

        m_cbStartAutomatically.setText("Start automatically");

        jSplitPane1.setDividerSize(7);
        jSplitPane1.setResizeWeight(0.5);
        jSplitPane1.setOneTouchExpandable(true);

        m_tblClasspath.setModel(new javax.swing.table.DefaultTableModel(new Object[][]
                                                                        {
                                                                            { null }
                                                                        },
                                                                        new String[]
                                                                        {
                                                                            "Location"
                                                                        }));
        jScrollPane3.setViewportView(m_tblClasspath);

        bClasspathMoveTop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/cordys/coe/tools/orgmanager/movetop_eb.gif"))); // NOI18N
        bClasspathMoveTop.setActionCommand("top");
        bClasspathMoveTop.setMaximumSize(new java.awt.Dimension(21, 21));
        bClasspathMoveTop.setMinimumSize(new java.awt.Dimension(21, 21));
        bClasspathMoveTop.setPreferredSize(new java.awt.Dimension(21, 21));
        bClasspathMoveTop.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    bClasspathMoveTopActionPerformed(evt);
                }
            });

        bClasspathMoveUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/cordys/coe/tools/orgmanager/moveup_eb.gif"))); // NOI18N
        bClasspathMoveUp.setActionCommand("up");
        bClasspathMoveUp.setMaximumSize(new java.awt.Dimension(21, 21));
        bClasspathMoveUp.setMinimumSize(new java.awt.Dimension(21, 21));
        bClasspathMoveUp.setPreferredSize(new java.awt.Dimension(21, 21));
        bClasspathMoveUp.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    bClasspathMoveUpActionPerformed(evt);
                }
            });

        bClasspathMoveDown.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/cordys/coe/tools/orgmanager/movedown_eb.gif"))); // NOI18N
        bClasspathMoveDown.setActionCommand("down");
        bClasspathMoveDown.setMaximumSize(new java.awt.Dimension(21, 21));
        bClasspathMoveDown.setMinimumSize(new java.awt.Dimension(21, 21));
        bClasspathMoveDown.setPreferredSize(new java.awt.Dimension(21, 21));
        bClasspathMoveDown.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    bClasspathMoveDownActionPerformed(evt);
                }
            });

        bClasspathMoveBottom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/cordys/coe/tools/orgmanager/movebottom_eb.gif"))); // NOI18N
        bClasspathMoveBottom.setActionCommand("bottom");
        bClasspathMoveBottom.setMaximumSize(new java.awt.Dimension(21, 21));
        bClasspathMoveBottom.setMinimumSize(new java.awt.Dimension(21, 21));
        bClasspathMoveBottom.setPreferredSize(new java.awt.Dimension(21, 21));
        bClasspathMoveBottom.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    bClasspathMoveBottomActionPerformed(evt);
                }
            });

        jToolBar1.setRollover(true);

        jLabel6.setText("Classpath details");
        jToolBar1.add(jLabel6);
        jToolBar1.add(jSeparator1);

        bAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/cordys/coe/tools/orgmanager/add_eb.gif"))); // NOI18N
        bAdd.setFocusable(false);
        bAdd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bAdd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bAdd.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    bAddActionPerformed(evt);
                }
            });
        jToolBar1.add(bAdd);

        bRemove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/cordys/coe/tools/orgmanager/delete_eb.gif"))); // NOI18N
        bRemove.setFocusable(false);
        bRemove.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bRemove.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bRemove.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    bRemoveActionPerformed(evt);
                }
            });
        jToolBar1.add(bRemove);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout
                                                                           .Alignment.LEADING)
                                         .addGroup(jPanel1Layout.createSequentialGroup()
                                                   .addContainerGap().addComponent(jScrollPane3,
                                                                                   javax.swing
                                                                                   .GroupLayout.DEFAULT_SIZE,
                                                                                   336,
                                                                                   Short.MAX_VALUE)
                                                   .addPreferredGap(javax.swing.LayoutStyle
                                                                    .ComponentPlacement.RELATED)
                                                   .addGroup(jPanel1Layout.createParallelGroup(javax
                                                                                               .swing
                                                                                               .GroupLayout
                                                                                               .Alignment.LEADING)
                                                             .addComponent(bClasspathMoveTop,
                                                                           javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                           javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                           javax.swing.GroupLayout.PREFERRED_SIZE)
                                                             .addComponent(bClasspathMoveUp,
                                                                           javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                           javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                           javax.swing.GroupLayout.PREFERRED_SIZE)
                                                             .addComponent(bClasspathMoveDown,
                                                                           javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                           javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                           javax.swing.GroupLayout.PREFERRED_SIZE)
                                                             .addComponent(bClasspathMoveBottom,
                                                                           javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                           javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                           javax.swing.GroupLayout.PREFERRED_SIZE))
                                                   .addContainerGap()).addComponent(jToolBar1,
                                                                                    javax.swing
                                                                                    .GroupLayout.DEFAULT_SIZE,
                                                                                    383,
                                                                                    Short.MAX_VALUE));
        jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout
                                                                         .Alignment.LEADING)
                                       .addGroup(jPanel1Layout.createSequentialGroup().addComponent(jToolBar1,
                                                                                                    javax
                                                                                                    .swing
                                                                                                    .GroupLayout.PREFERRED_SIZE,
                                                                                                    25,
                                                                                                    javax
                                                                                                    .swing
                                                                                                    .GroupLayout.PREFERRED_SIZE)
                                                 .addPreferredGap(javax.swing.LayoutStyle
                                                                  .ComponentPlacement.RELATED)
                                                 .addGroup(jPanel1Layout.createParallelGroup(javax
                                                                                             .swing
                                                                                             .GroupLayout
                                                                                             .Alignment.LEADING)
                                                           .addGroup(jPanel1Layout
                                                                     .createSequentialGroup()
                                                                     .addComponent(bClasspathMoveTop,
                                                                                   javax.swing
                                                                                   .GroupLayout.PREFERRED_SIZE,
                                                                                   javax.swing
                                                                                   .GroupLayout.DEFAULT_SIZE,
                                                                                   javax.swing
                                                                                   .GroupLayout.PREFERRED_SIZE)
                                                                     .addPreferredGap(javax.swing
                                                                                      .LayoutStyle
                                                                                      .ComponentPlacement.RELATED)
                                                                     .addComponent(bClasspathMoveUp,
                                                                                   javax.swing
                                                                                   .GroupLayout.PREFERRED_SIZE,
                                                                                   javax.swing
                                                                                   .GroupLayout.DEFAULT_SIZE,
                                                                                   javax.swing
                                                                                   .GroupLayout.PREFERRED_SIZE)
                                                                     .addPreferredGap(javax.swing
                                                                                      .LayoutStyle
                                                                                      .ComponentPlacement.RELATED)
                                                                     .addComponent(bClasspathMoveDown,
                                                                                   javax.swing
                                                                                   .GroupLayout.PREFERRED_SIZE,
                                                                                   javax.swing
                                                                                   .GroupLayout.DEFAULT_SIZE,
                                                                                   javax.swing
                                                                                   .GroupLayout.PREFERRED_SIZE)
                                                                     .addPreferredGap(javax.swing
                                                                                      .LayoutStyle
                                                                                      .ComponentPlacement.RELATED)
                                                                     .addComponent(bClasspathMoveBottom,
                                                                                   javax.swing
                                                                                   .GroupLayout.PREFERRED_SIZE,
                                                                                   javax.swing
                                                                                   .GroupLayout.DEFAULT_SIZE,
                                                                                   javax.swing
                                                                                   .GroupLayout.PREFERRED_SIZE))
                                                           .addComponent(jScrollPane3,
                                                                         javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                         120, Short.MAX_VALUE))
                                                 .addContainerGap()));

        jSplitPane1.setLeftComponent(jPanel1);

        m_tblJVMOptions.setModel(new javax.swing.table.DefaultTableModel(new Object[][]
                                                                         {
                                                                             { null }
                                                                         },
                                                                         new String[]
                                                                         {
                                                                             "JVM option"
                                                                         }));
        jScrollPane4.setViewportView(m_tblJVMOptions);

        bJVMMoveTop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/cordys/coe/tools/orgmanager/movetop_eb.gif"))); // NOI18N
        bJVMMoveTop.setMaximumSize(new java.awt.Dimension(21, 21));
        bJVMMoveTop.setMinimumSize(new java.awt.Dimension(21, 21));
        bJVMMoveTop.setPreferredSize(new java.awt.Dimension(21, 21));
        bJVMMoveTop.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    bJVMMoveTopActionPerformed(evt);
                }
            });

        bJVMMoveUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/cordys/coe/tools/orgmanager/moveup_eb.gif"))); // NOI18N
        bJVMMoveUp.setMaximumSize(new java.awt.Dimension(21, 21));
        bJVMMoveUp.setMinimumSize(new java.awt.Dimension(21, 21));
        bJVMMoveUp.setPreferredSize(new java.awt.Dimension(21, 21));
        bJVMMoveUp.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    bJVMMoveUpActionPerformed(evt);
                }
            });

        bJVMMoveDown.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/cordys/coe/tools/orgmanager/movedown_eb.gif"))); // NOI18N
        bJVMMoveDown.setMaximumSize(new java.awt.Dimension(21, 21));
        bJVMMoveDown.setMinimumSize(new java.awt.Dimension(21, 21));
        bJVMMoveDown.setPreferredSize(new java.awt.Dimension(21, 21));
        bJVMMoveDown.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    bJVMMoveDownActionPerformed(evt);
                }
            });

        bJVMMoveBottom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/cordys/coe/tools/orgmanager/movebottom_eb.gif"))); // NOI18N
        bJVMMoveBottom.setMaximumSize(new java.awt.Dimension(21, 21));
        bJVMMoveBottom.setMinimumSize(new java.awt.Dimension(21, 21));
        bJVMMoveBottom.setPreferredSize(new java.awt.Dimension(21, 21));
        bJVMMoveBottom.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    bJVMMoveBottomActionPerformed(evt);
                }
            });

        jToolBar2.setRollover(true);

        jLabel7.setText("Custom JVM options");
        jToolBar2.add(jLabel7);
        jToolBar2.add(jSeparator2);

        bAddJVM.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/cordys/coe/tools/orgmanager/add_eb.gif"))); // NOI18N
        bAddJVM.setFocusable(false);
        bAddJVM.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bAddJVM.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bAddJVM.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    bAddJVMActionPerformed(evt);
                }
            });
        jToolBar2.add(bAddJVM);

        bRemoveJVM.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/cordys/coe/tools/orgmanager/delete_eb.gif"))); // NOI18N
        bRemoveJVM.setFocusable(false);
        bRemoveJVM.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bRemoveJVM.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bRemoveJVM.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    bRemoveJVMActionPerformed(evt);
                }
            });
        jToolBar2.add(bRemoveJVM);
        jToolBar2.add(jSeparator3);

        bAddDebugger.setText("Set debugger");
        bAddDebugger.setFocusable(false);
        bAddDebugger.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bAddDebugger.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bAddDebugger.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    addDebuggerToJVMOptions(evt);
                }
            });
        jToolBar2.add(bAddDebugger);

        bRemoveDebugger.setText("Remove debugger");
        bRemoveDebugger.setFocusable(false);
        bRemoveDebugger.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bRemoveDebugger.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bRemoveDebugger.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    removeDebuggerFromOptions(evt);
                }
            });
        jToolBar2.add(bRemoveDebugger);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout
                                                                           .Alignment.LEADING)
                                         .addGroup(jPanel2Layout.createSequentialGroup()
                                                   .addContainerGap().addComponent(jScrollPane4,
                                                                                   javax.swing
                                                                                   .GroupLayout.DEFAULT_SIZE,
                                                                                   347,
                                                                                   Short.MAX_VALUE)
                                                   .addPreferredGap(javax.swing.LayoutStyle
                                                                    .ComponentPlacement.RELATED)
                                                   .addGroup(jPanel2Layout.createParallelGroup(javax
                                                                                               .swing
                                                                                               .GroupLayout
                                                                                               .Alignment.LEADING)
                                                             .addComponent(bJVMMoveTop,
                                                                           javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                           javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                           javax.swing.GroupLayout.PREFERRED_SIZE)
                                                             .addComponent(bJVMMoveUp,
                                                                           javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                           javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                           javax.swing.GroupLayout.PREFERRED_SIZE)
                                                             .addComponent(bJVMMoveDown,
                                                                           javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                           javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                           javax.swing.GroupLayout.PREFERRED_SIZE)
                                                             .addComponent(bJVMMoveBottom,
                                                                           javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                           javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                           javax.swing.GroupLayout.PREFERRED_SIZE))
                                                   .addContainerGap()).addGroup(jPanel2Layout
                                                                                .createSequentialGroup()
                                                                                .addGap(1, 1, 1)
                                                                                .addComponent(jToolBar2,
                                                                                              javax
                                                                                              .swing
                                                                                              .GroupLayout.DEFAULT_SIZE,
                                                                                              393,
                                                                                              Short.MAX_VALUE)));
        jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout
                                                                         .Alignment.LEADING)
                                       .addGroup(jPanel2Layout.createSequentialGroup().addComponent(jToolBar2,
                                                                                                    javax
                                                                                                    .swing
                                                                                                    .GroupLayout.PREFERRED_SIZE,
                                                                                                    25,
                                                                                                    javax
                                                                                                    .swing
                                                                                                    .GroupLayout.PREFERRED_SIZE)
                                                 .addPreferredGap(javax.swing.LayoutStyle
                                                                  .ComponentPlacement.RELATED)
                                                 .addGroup(jPanel2Layout.createParallelGroup(javax
                                                                                             .swing
                                                                                             .GroupLayout
                                                                                             .Alignment.LEADING)
                                                           .addGroup(jPanel2Layout
                                                                     .createSequentialGroup()
                                                                     .addComponent(bJVMMoveTop,
                                                                                   javax.swing
                                                                                   .GroupLayout.PREFERRED_SIZE,
                                                                                   javax.swing
                                                                                   .GroupLayout.DEFAULT_SIZE,
                                                                                   javax.swing
                                                                                   .GroupLayout.PREFERRED_SIZE)
                                                                     .addPreferredGap(javax.swing
                                                                                      .LayoutStyle
                                                                                      .ComponentPlacement.RELATED)
                                                                     .addComponent(bJVMMoveUp,
                                                                                   javax.swing
                                                                                   .GroupLayout.PREFERRED_SIZE,
                                                                                   javax.swing
                                                                                   .GroupLayout.DEFAULT_SIZE,
                                                                                   javax.swing
                                                                                   .GroupLayout.PREFERRED_SIZE)
                                                                     .addPreferredGap(javax.swing
                                                                                      .LayoutStyle
                                                                                      .ComponentPlacement.RELATED)
                                                                     .addComponent(bJVMMoveDown,
                                                                                   javax.swing
                                                                                   .GroupLayout.PREFERRED_SIZE,
                                                                                   javax.swing
                                                                                   .GroupLayout.DEFAULT_SIZE,
                                                                                   javax.swing
                                                                                   .GroupLayout.PREFERRED_SIZE)
                                                                     .addPreferredGap(javax.swing
                                                                                      .LayoutStyle
                                                                                      .ComponentPlacement.RELATED)
                                                                     .addComponent(bJVMMoveBottom,
                                                                                   javax.swing
                                                                                   .GroupLayout.PREFERRED_SIZE,
                                                                                   javax.swing
                                                                                   .GroupLayout.DEFAULT_SIZE,
                                                                                   javax.swing
                                                                                   .GroupLayout.PREFERRED_SIZE))
                                                           .addComponent(jScrollPane4,
                                                                         javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                         120, Short.MAX_VALUE))
                                                 .addContainerGap()));

        jSplitPane1.setRightComponent(jPanel2);

        javax.swing.GroupLayout m_pDetailsLayout = new javax.swing.GroupLayout(m_pDetails);
        m_pDetails.setLayout(m_pDetailsLayout);
        m_pDetailsLayout.setHorizontalGroup(m_pDetailsLayout.createParallelGroup(javax.swing
                                                                                 .GroupLayout
                                                                                 .Alignment.LEADING)
                                            .addGroup(m_pDetailsLayout.createSequentialGroup()
                                                      .addContainerGap().addGroup(m_pDetailsLayout
                                                                                  .createParallelGroup(javax
                                                                                                       .swing
                                                                                                       .GroupLayout
                                                                                                       .Alignment.LEADING)
                                                                                  .addGroup(m_pDetailsLayout
                                                                                            .createSequentialGroup()
                                                                                            .addGroup(m_pDetailsLayout
                                                                                                      .createParallelGroup(javax
                                                                                                                           .swing
                                                                                                                           .GroupLayout
                                                                                                                           .Alignment.LEADING,
                                                                                                                           false)
                                                                                                      .addComponent(jLabel3,
                                                                                                                    javax
                                                                                                                    .swing
                                                                                                                    .GroupLayout.DEFAULT_SIZE,
                                                                                                                    70,
                                                                                                                    Short.MAX_VALUE)
                                                                                                      .addComponent(jLabel2,
                                                                                                                    javax
                                                                                                                    .swing
                                                                                                                    .GroupLayout.DEFAULT_SIZE,
                                                                                                                    javax
                                                                                                                    .swing
                                                                                                                    .GroupLayout.DEFAULT_SIZE,
                                                                                                                    Short.MAX_VALUE)
                                                                                                      .addComponent(jLabel1,
                                                                                                                    javax
                                                                                                                    .swing
                                                                                                                    .GroupLayout.DEFAULT_SIZE,
                                                                                                                    javax
                                                                                                                    .swing
                                                                                                                    .GroupLayout.DEFAULT_SIZE,
                                                                                                                    Short.MAX_VALUE))
                                                                                            .addPreferredGap(javax
                                                                                                             .swing
                                                                                                             .LayoutStyle
                                                                                                             .ComponentPlacement.RELATED,
                                                                                                             10,
                                                                                                             Short.MAX_VALUE)
                                                                                            .addGroup(m_pDetailsLayout
                                                                                                      .createParallelGroup(javax
                                                                                                                           .swing
                                                                                                                           .GroupLayout
                                                                                                                           .Alignment.LEADING,
                                                                                                                           false)
                                                                                                      .addComponent(m_tOSProcess,
                                                                                                                    javax
                                                                                                                    .swing
                                                                                                                    .GroupLayout
                                                                                                                    .Alignment.TRAILING)
                                                                                                      .addComponent(m_tComputer,
                                                                                                                    javax
                                                                                                                    .swing
                                                                                                                    .GroupLayout
                                                                                                                    .Alignment.TRAILING)
                                                                                                      .addComponent(m_tName,
                                                                                                                    javax
                                                                                                                    .swing
                                                                                                                    .GroupLayout.PREFERRED_SIZE,
                                                                                                                    262,
                                                                                                                    javax
                                                                                                                    .swing
                                                                                                                    .GroupLayout.PREFERRED_SIZE))
                                                                                            .addPreferredGap(javax
                                                                                                             .swing
                                                                                                             .LayoutStyle
                                                                                                             .ComponentPlacement.RELATED)
                                                                                            .addGroup(m_pDetailsLayout
                                                                                                      .createParallelGroup(javax
                                                                                                                           .swing
                                                                                                                           .GroupLayout
                                                                                                                           .Alignment.LEADING)
                                                                                                      .addGroup(javax
                                                                                                                .swing
                                                                                                                .GroupLayout
                                                                                                                .Alignment.TRAILING,
                                                                                                                m_pDetailsLayout
                                                                                                                .createSequentialGroup()
                                                                                                                .addComponent(jLabel4,
                                                                                                                              javax
                                                                                                                              .swing
                                                                                                                              .GroupLayout.DEFAULT_SIZE,
                                                                                                                              168,
                                                                                                                              Short.MAX_VALUE)
                                                                                                                .addPreferredGap(javax
                                                                                                                                 .swing
                                                                                                                                 .LayoutStyle
                                                                                                                                 .ComponentPlacement.RELATED)
                                                                                                                .addComponent(m_tRequestNotification,
                                                                                                                              javax
                                                                                                                              .swing
                                                                                                                              .GroupLayout.PREFERRED_SIZE,
                                                                                                                              268,
                                                                                                                              javax
                                                                                                                              .swing
                                                                                                                              .GroupLayout.PREFERRED_SIZE))
                                                                                                      .addGroup(m_pDetailsLayout
                                                                                                                .createSequentialGroup()
                                                                                                                .addComponent(jLabel5)
                                                                                                                .addPreferredGap(javax
                                                                                                                                 .swing
                                                                                                                                 .LayoutStyle
                                                                                                                                 .ComponentPlacement.RELATED)
                                                                                                                .addGroup(m_pDetailsLayout
                                                                                                                          .createParallelGroup(javax
                                                                                                                                               .swing
                                                                                                                                               .GroupLayout
                                                                                                                                               .Alignment.LEADING)
                                                                                                                          .addComponent(m_cbStartAutomatically,
                                                                                                                                        javax
                                                                                                                                        .swing
                                                                                                                                        .GroupLayout
                                                                                                                                        .Alignment.TRAILING,
                                                                                                                                        javax
                                                                                                                                        .swing
                                                                                                                                        .GroupLayout.DEFAULT_SIZE,
                                                                                                                                        265,
                                                                                                                                        Short.MAX_VALUE)
                                                                                                                          .addGroup(m_pDetailsLayout
                                                                                                                                    .createSequentialGroup()
                                                                                                                                    .addComponent(rbDefault)
                                                                                                                                    .addPreferredGap(javax
                                                                                                                                                     .swing
                                                                                                                                                     .LayoutStyle
                                                                                                                                                     .ComponentPlacement.RELATED)
                                                                                                                                    .addComponent(rbInfinite)
                                                                                                                                    .addPreferredGap(javax
                                                                                                                                                     .swing
                                                                                                                                                     .LayoutStyle
                                                                                                                                                     .ComponentPlacement.RELATED)
                                                                                                                                    .addComponent(rbCustom)
                                                                                                                                    .addPreferredGap(javax
                                                                                                                                                     .swing
                                                                                                                                                     .LayoutStyle
                                                                                                                                                     .ComponentPlacement.RELATED)
                                                                                                                                    .addComponent(m_tCustomAttempts,
                                                                                                                                                  javax
                                                                                                                                                  .swing
                                                                                                                                                  .GroupLayout.DEFAULT_SIZE,
                                                                                                                                                  78,
                                                                                                                                                  Short.MAX_VALUE))))))
                                                                                  .addComponent(jSplitPane1,
                                                                                                javax
                                                                                                .swing
                                                                                                .GroupLayout.DEFAULT_SIZE,
                                                                                                786,
                                                                                                Short.MAX_VALUE))
                                                      .addContainerGap()));
        m_pDetailsLayout.setVerticalGroup(m_pDetailsLayout.createParallelGroup(javax.swing
                                                                               .GroupLayout
                                                                               .Alignment.LEADING)
                                          .addGroup(m_pDetailsLayout.createSequentialGroup()
                                                    .addGroup(m_pDetailsLayout.createParallelGroup(javax
                                                                                                   .swing
                                                                                                   .GroupLayout
                                                                                                   .Alignment.TRAILING)
                                                              .addGroup(m_pDetailsLayout
                                                                        .createSequentialGroup()
                                                                        .addGroup(m_pDetailsLayout
                                                                                  .createParallelGroup(javax
                                                                                                       .swing
                                                                                                       .GroupLayout
                                                                                                       .Alignment.BASELINE)
                                                                                  .addComponent(m_cbStartAutomatically)
                                                                                  .addComponent(jLabel1)
                                                                                  .addComponent(m_tName,
                                                                                                javax
                                                                                                .swing
                                                                                                .GroupLayout.PREFERRED_SIZE,
                                                                                                javax
                                                                                                .swing
                                                                                                .GroupLayout.DEFAULT_SIZE,
                                                                                                javax
                                                                                                .swing
                                                                                                .GroupLayout.PREFERRED_SIZE))
                                                                        .addPreferredGap(javax.swing
                                                                                         .LayoutStyle
                                                                                         .ComponentPlacement.RELATED)
                                                                        .addGroup(m_pDetailsLayout
                                                                                  .createParallelGroup(javax
                                                                                                       .swing
                                                                                                       .GroupLayout
                                                                                                       .Alignment.BASELINE)
                                                                                  .addComponent(jLabel2)
                                                                                  .addComponent(m_tComputer,
                                                                                                javax
                                                                                                .swing
                                                                                                .GroupLayout.PREFERRED_SIZE,
                                                                                                javax
                                                                                                .swing
                                                                                                .GroupLayout.DEFAULT_SIZE,
                                                                                                javax
                                                                                                .swing
                                                                                                .GroupLayout.PREFERRED_SIZE)))
                                                              .addGroup(m_pDetailsLayout
                                                                        .createParallelGroup(javax
                                                                                             .swing
                                                                                             .GroupLayout
                                                                                             .Alignment.BASELINE)
                                                                        .addComponent(jLabel5)
                                                                        .addComponent(rbDefault)
                                                                        .addComponent(rbInfinite)
                                                                        .addComponent(rbCustom)
                                                                        .addComponent(m_tCustomAttempts,
                                                                                      javax.swing
                                                                                      .GroupLayout.PREFERRED_SIZE,
                                                                                      javax.swing
                                                                                      .GroupLayout.DEFAULT_SIZE,
                                                                                      javax.swing
                                                                                      .GroupLayout.PREFERRED_SIZE)))
                                                    .addPreferredGap(javax.swing.LayoutStyle
                                                                     .ComponentPlacement.RELATED)
                                                    .addGroup(m_pDetailsLayout.createParallelGroup(javax
                                                                                                   .swing
                                                                                                   .GroupLayout
                                                                                                   .Alignment.BASELINE)
                                                              .addComponent(m_tRequestNotification,
                                                                            javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                            javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                            javax.swing.GroupLayout.PREFERRED_SIZE)
                                                              .addComponent(jLabel4).addComponent(m_tOSProcess,
                                                                                                  javax
                                                                                                  .swing
                                                                                                  .GroupLayout.PREFERRED_SIZE,
                                                                                                  javax
                                                                                                  .swing
                                                                                                  .GroupLayout.DEFAULT_SIZE,
                                                                                                  javax
                                                                                                  .swing
                                                                                                  .GroupLayout.PREFERRED_SIZE)
                                                              .addComponent(jLabel3))
                                                    .addPreferredGap(javax.swing.LayoutStyle
                                                                     .ComponentPlacement.RELATED)
                                                    .addComponent(jSplitPane1,
                                                                  javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                  164,
                                                                  javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addContainerGap(239, Short.MAX_VALUE)));

        m_tpMain.addTab("Details", m_pDetails);

        m_cbSystemPolicy.setText("Enable system policy");
        m_cbSystemPolicy.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    enableDisableLog4J(evt);
                }
            });

        m_tpLog4JDetails.addChangeListener(new javax.swing.event.ChangeListener()
            {
                public void stateChanged(javax.swing.event.ChangeEvent evt)
                {
                    storeSelectedIndex(evt);
                }
            });
        m_tpLog4JDetails.addFocusListener(new java.awt.event.FocusAdapter()
            {
                public void focusLost(java.awt.event.FocusEvent evt)
                {
                    updateLog4JConfiguration(evt);
                }
            });

        pLog4JXML.addComponentListener(new java.awt.event.ComponentAdapter()
            {
                public void componentHidden(java.awt.event.ComponentEvent evt)
                {
                    pCategoriesComponentHidden(evt);
                }
            });

        m_epLog4J.setFont(new java.awt.Font("Courier New", 0, 11));
        jScrollPane2.setViewportView(m_epLog4J);

        javax.swing.GroupLayout pLog4JXMLLayout = new javax.swing.GroupLayout(pLog4JXML);
        pLog4JXML.setLayout(pLog4JXMLLayout);
        pLog4JXMLLayout.setHorizontalGroup(pLog4JXMLLayout.createParallelGroup(javax.swing
                                                                               .GroupLayout
                                                                               .Alignment.LEADING)
                                           .addGroup(pLog4JXMLLayout.createSequentialGroup()
                                                     .addContainerGap().addComponent(jScrollPane2)
                                                     .addContainerGap()));
        pLog4JXMLLayout.setVerticalGroup(pLog4JXMLLayout.createParallelGroup(javax.swing.GroupLayout
                                                                             .Alignment.LEADING)
                                         .addGroup(pLog4JXMLLayout.createSequentialGroup()
                                                   .addContainerGap().addComponent(jScrollPane2,
                                                                                   javax.swing
                                                                                   .GroupLayout.DEFAULT_SIZE,
                                                                                   398,
                                                                                   Short.MAX_VALUE)
                                                   .addContainerGap()));

        m_tpLog4JDetails.addTab("Log4J XML", pLog4JXML);

        pCategories.addComponentListener(new java.awt.event.ComponentAdapter()
            {
                public void componentHidden(java.awt.event.ComponentEvent evt)
                {
                    pCategoriesComponentHidden(evt);
                }
            });

        m_tblCategories.setModel(m_mdlCategories);
        jScrollPane5.setViewportView(m_tblCategories);
        m_tblCategories.getColumnModel().getColumn(2).setMaxWidth(50);
        m_tblCategories.getColumnModel().getColumn(3).setMaxWidth(50);
        m_tblCategories.getColumnModel().getColumn(4).setMaxWidth(50);
        m_tblCategories.getColumnModel().getColumn(5).setMaxWidth(50);
        m_tblCategories.getColumnModel().getColumn(6).setMaxWidth(50);
        m_tblCategories.getColumnModel().getColumn(7).setMaxWidth(50);
        m_tblCategories.getColumnModel().getColumn(8).setMaxWidth(50);
        m_tblCategories.getColumnModel().getColumn(2).setMinWidth(50);
        m_tblCategories.getColumnModel().getColumn(3).setMinWidth(50);
        m_tblCategories.getColumnModel().getColumn(4).setMinWidth(50);
        m_tblCategories.getColumnModel().getColumn(5).setMinWidth(50);
        m_tblCategories.getColumnModel().getColumn(6).setMinWidth(50);
        m_tblCategories.getColumnModel().getColumn(7).setMinWidth(50);
        m_tblCategories.getColumnModel().getColumn(8).setMinWidth(50);
        m_tblCategories.getColumnModel().getColumn(2).setPreferredWidth(50);
        m_tblCategories.getColumnModel().getColumn(3).setPreferredWidth(50);
        m_tblCategories.getColumnModel().getColumn(4).setPreferredWidth(50);
        m_tblCategories.getColumnModel().getColumn(5).setPreferredWidth(50);
        m_tblCategories.getColumnModel().getColumn(6).setPreferredWidth(50);
        m_tblCategories.getColumnModel().getColumn(7).setPreferredWidth(50);
        m_tblCategories.getColumnModel().getColumn(8).setPreferredWidth(50);

        jToolBar3.setRollover(true);

        bAddCategory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/cordys/coe/tools/orgmanager/add_eb.gif"))); // NOI18N
        bAddCategory.setFocusable(false);
        bAddCategory.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bAddCategory.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bAddCategory.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    bAddCategoryActionPerformed(evt);
                }
            });
        jToolBar3.add(bAddCategory);

        bRemoveCategory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/cordys/coe/tools/orgmanager/delete_eb.gif"))); // NOI18N
        bRemoveCategory.setFocusable(false);
        bRemoveCategory.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bRemoveCategory.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bRemoveCategory.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    bRemoveCategoryActionPerformed(evt);
                }
            });
        jToolBar3.add(bRemoveCategory);
        jToolBar3.add(jSeparator4);

        jLabel10.setText("Set globally:");
        jToolBar3.add(jLabel10);

        jButton3.setText("OFF");
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton3.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    handleClickAll(evt);
                }
            });
        jToolBar3.add(jButton3);

        jButton9.setText("TRACE");
        jButton9.setFocusable(false);
        jButton9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton9.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton9.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    handleClickAll(evt);
                }
            });
        jToolBar3.add(jButton9);

        jButton4.setText("DEBUG");
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton4.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    handleClickAll(evt);
                }
            });
        jToolBar3.add(jButton4);

        jButton5.setText("INFO");
        jButton5.setFocusable(false);
        jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton5.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    handleClickAll(evt);
                }
            });
        jToolBar3.add(jButton5);

        jButton6.setText("WARN");
        jButton6.setFocusable(false);
        jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton6.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    handleClickAll(evt);
                }
            });
        jToolBar3.add(jButton6);

        jButton7.setText("ERROR");
        jButton7.setFocusable(false);
        jButton7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton7.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton7.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    handleClickAll(evt);
                }
            });
        jToolBar3.add(jButton7);

        jButton8.setText("FATAL");
        jButton8.setFocusable(false);
        jButton8.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton8.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton8.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    handleClickAll(evt);
                }
            });
        jToolBar3.add(jButton8);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(" Root logger details "));

        jLabel11.setText("Priority:");

        m_cbRootPriority.setModel(new javax.swing.DefaultComboBoxModel<String>(new String[]
                                                                       {
                                                                           "OFF", "TRACE", "DEBUG",
                                                                           "INFO", "WARN", "ERROR",
                                                                           "FATAL"
                                                                       }));

        jLabel12.setText("Appenders:");

        m_tRootAppenders.setEditable(false);

        bShowAppenderChooser.setText("...");
        bShowAppenderChooser.setMaximumSize(new java.awt.Dimension(23, 23));
        bShowAppenderChooser.setMinimumSize(new java.awt.Dimension(23, 23));
        bShowAppenderChooser.setPreferredSize(new java.awt.Dimension(23, 23));
        bShowAppenderChooser.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    bShowAppenderChooserActionPerformed(evt);
                }
            });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout
                                                                           .Alignment.LEADING)
                                         .addGroup(jPanel4Layout.createSequentialGroup()
                                                   .addContainerGap().addGroup(jPanel4Layout
                                                                               .createParallelGroup(javax
                                                                                                    .swing
                                                                                                    .GroupLayout
                                                                                                    .Alignment.TRAILING,
                                                                                                    false)
                                                                               .addComponent(jLabel12,
                                                                                             javax
                                                                                             .swing
                                                                                             .GroupLayout
                                                                                             .Alignment.LEADING,
                                                                                             javax
                                                                                             .swing
                                                                                             .GroupLayout.DEFAULT_SIZE,
                                                                                             javax
                                                                                             .swing
                                                                                             .GroupLayout.DEFAULT_SIZE,
                                                                                             Short.MAX_VALUE)
                                                                               .addComponent(jLabel11,
                                                                                             javax
                                                                                             .swing
                                                                                             .GroupLayout
                                                                                             .Alignment.LEADING,
                                                                                             javax
                                                                                             .swing
                                                                                             .GroupLayout.DEFAULT_SIZE,
                                                                                             74,
                                                                                             Short.MAX_VALUE))
                                                   .addPreferredGap(javax.swing.LayoutStyle
                                                                    .ComponentPlacement.RELATED)
                                                   .addGroup(jPanel4Layout.createParallelGroup(javax
                                                                                               .swing
                                                                                               .GroupLayout
                                                                                               .Alignment.LEADING)
                                                             .addComponent(m_cbRootPriority,
                                                                           javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                           134,
                                                                           javax.swing.GroupLayout.PREFERRED_SIZE)
                                                             .addGroup(jPanel4Layout
                                                                       .createSequentialGroup()
                                                                       .addComponent(m_tRootAppenders,
                                                                                     javax.swing
                                                                                     .GroupLayout.PREFERRED_SIZE,
                                                                                     296,
                                                                                     javax.swing
                                                                                     .GroupLayout.PREFERRED_SIZE)
                                                                       .addPreferredGap(javax.swing
                                                                                        .LayoutStyle
                                                                                        .ComponentPlacement.RELATED)
                                                                       .addComponent(bShowAppenderChooser,
                                                                                     javax.swing
                                                                                     .GroupLayout.PREFERRED_SIZE,
                                                                                     21,
                                                                                     javax.swing
                                                                                     .GroupLayout.PREFERRED_SIZE)))
                                                   .addContainerGap(358, Short.MAX_VALUE)));
        jPanel4Layout.setVerticalGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout
                                                                         .Alignment.LEADING)
                                       .addGroup(jPanel4Layout.createSequentialGroup().addGroup(jPanel4Layout
                                                                                                .createParallelGroup(javax
                                                                                                                     .swing
                                                                                                                     .GroupLayout
                                                                                                                     .Alignment.BASELINE)
                                                                                                .addComponent(m_cbRootPriority,
                                                                                                              javax
                                                                                                              .swing
                                                                                                              .GroupLayout.PREFERRED_SIZE,
                                                                                                              javax
                                                                                                              .swing
                                                                                                              .GroupLayout.DEFAULT_SIZE,
                                                                                                              javax
                                                                                                              .swing
                                                                                                              .GroupLayout.PREFERRED_SIZE)
                                                                                                .addComponent(jLabel11))
                                                 .addPreferredGap(javax.swing.LayoutStyle
                                                                  .ComponentPlacement.RELATED)
                                                 .addGroup(jPanel4Layout.createParallelGroup(javax
                                                                                             .swing
                                                                                             .GroupLayout
                                                                                             .Alignment.BASELINE)
                                                           .addComponent(jLabel12).addComponent(m_tRootAppenders,
                                                                                                javax
                                                                                                .swing
                                                                                                .GroupLayout.PREFERRED_SIZE,
                                                                                                javax
                                                                                                .swing
                                                                                                .GroupLayout.DEFAULT_SIZE,
                                                                                                javax
                                                                                                .swing
                                                                                                .GroupLayout.PREFERRED_SIZE)
                                                           .addComponent(bShowAppenderChooser,
                                                                         javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                         javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                         javax.swing.GroupLayout.PREFERRED_SIZE))
                                                 .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                  Short.MAX_VALUE)));

        javax.swing.GroupLayout pCategoriesLayout = new javax.swing.GroupLayout(pCategories);
        pCategories.setLayout(pCategoriesLayout);
        pCategoriesLayout.setHorizontalGroup(pCategoriesLayout.createParallelGroup(javax.swing
                                                                                   .GroupLayout
                                                                                   .Alignment.LEADING)
                                             .addComponent(jToolBar3,
                                                           javax.swing.GroupLayout.DEFAULT_SIZE,
                                                           801, Short.MAX_VALUE).addGroup(pCategoriesLayout
                                                                                          .createSequentialGroup()
                                                                                          .addContainerGap()
                                                                                          .addComponent(jPanel4,
                                                                                                        javax
                                                                                                        .swing
                                                                                                        .GroupLayout.DEFAULT_SIZE,
                                                                                                        javax
                                                                                                        .swing
                                                                                                        .GroupLayout.DEFAULT_SIZE,
                                                                                                        Short.MAX_VALUE)
                                                                                          .addContainerGap())
                                             .addGroup(pCategoriesLayout.createSequentialGroup()
                                                       .addContainerGap().addComponent(jScrollPane5,
                                                                                       javax.swing
                                                                                       .GroupLayout.DEFAULT_SIZE,
                                                                                       781,
                                                                                       Short.MAX_VALUE)
                                                       .addContainerGap()));
        pCategoriesLayout.setVerticalGroup(pCategoriesLayout.createParallelGroup(javax.swing
                                                                                 .GroupLayout
                                                                                 .Alignment.LEADING)
                                           .addGroup(pCategoriesLayout.createSequentialGroup()
                                                     .addComponent(jToolBar3,
                                                                   javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                   25,
                                                                   javax.swing.GroupLayout.PREFERRED_SIZE)
                                                     .addPreferredGap(javax.swing.LayoutStyle
                                                                      .ComponentPlacement.RELATED)
                                                     .addComponent(jPanel4,
                                                                   javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                   javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                   javax.swing.GroupLayout.PREFERRED_SIZE)
                                                     .addPreferredGap(javax.swing.LayoutStyle
                                                                      .ComponentPlacement.RELATED)
                                                     .addComponent(jScrollPane5,
                                                                   javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                   284, Short.MAX_VALUE)
                                                     .addContainerGap()));

        m_tpLog4JDetails.addTab("Categories", pCategories);

        pAppenders.addComponentListener(new java.awt.event.ComponentAdapter()
            {
                public void componentHidden(java.awt.event.ComponentEvent evt)
                {
                    pCategoriesComponentHidden(evt);
                }
            });
        pAppenders.addPropertyChangeListener(new java.beans.PropertyChangeListener()
            {
                public void propertyChange(java.beans.PropertyChangeEvent evt)
                {
                    testPropertyChange(evt);
                }
            });

        m_tblAppender.setModel(m_mdlAppenders);
        m_tblAppender.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane6.setViewportView(m_tblAppender);

        jToolBar4.setRollover(true);

        bAddAppender.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/cordys/coe/tools/orgmanager/add_eb.gif"))); // NOI18N
        bAddAppender.setFocusable(false);
        bAddAppender.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bAddAppender.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bAddAppender.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    bAddAppenderActionPerformed(evt);
                }
            });
        jToolBar4.add(bAddAppender);

        bRemoveAppender.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/cordys/coe/tools/orgmanager/delete_eb.gif"))); // NOI18N
        bRemoveAppender.setFocusable(false);
        bRemoveAppender.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bRemoveAppender.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bRemoveAppender.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    bRemoveAppenderActionPerformed(evt);
                }
            });
        jToolBar4.add(bRemoveAppender);
        jToolBar4.add(jSeparator5);

        bAddSocketAppender.setText("Add SocketAppender");
        bAddSocketAppender.setFocusable(false);
        bAddSocketAppender.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bAddSocketAppender.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bAddSocketAppender.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    bAddSocketAppenderActionPerformed(evt);
                }
            });
        jToolBar4.add(bAddSocketAppender);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(" Appender details "));

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel8.setText("Name:");

        m_tAppenderName.setEditable(false);

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel9.setText("Appender:");

        m_tAppender.setEditable(false);

        jSplitPane2.setDividerLocation(100);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        m_tblAppenderProperties.setModel(m_mdlParameters);
        jScrollPane7.setViewportView(m_tblAppenderProperties);

        jSplitPane2.setTopComponent(jScrollPane7);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(" Layout details "));

        jLabel13.setText("Layout class:");

        m_tblLayoutProperties.setModel(m_mdlLayoutParameters);
        jScrollPane8.setViewportView(m_tblLayoutProperties);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout
                                                                           .Alignment.LEADING)
                                         .addGroup(jPanel5Layout.createSequentialGroup()
                                                   .addContainerGap().addComponent(jLabel13)
                                                   .addPreferredGap(javax.swing.LayoutStyle
                                                                    .ComponentPlacement.UNRELATED)
                                                   .addComponent(m_cbLayout, 0, 642,
                                                                 Short.MAX_VALUE).addContainerGap())
                                         .addGroup(jPanel5Layout.createParallelGroup(javax.swing
                                                                                     .GroupLayout
                                                                                     .Alignment.LEADING)
                                                   .addGroup(jPanel5Layout.createSequentialGroup()
                                                             .addContainerGap().addComponent(jScrollPane8,
                                                                                             javax
                                                                                             .swing
                                                                                             .GroupLayout.DEFAULT_SIZE,
                                                                                             715,
                                                                                             Short.MAX_VALUE)
                                                             .addContainerGap())));
        jPanel5Layout.setVerticalGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout
                                                                         .Alignment.LEADING)
                                       .addGroup(jPanel5Layout.createSequentialGroup().addGroup(jPanel5Layout
                                                                                                .createParallelGroup(javax
                                                                                                                     .swing
                                                                                                                     .GroupLayout
                                                                                                                     .Alignment.BASELINE)
                                                                                                .addComponent(jLabel13)
                                                                                                .addComponent(m_cbLayout,
                                                                                                              javax
                                                                                                              .swing
                                                                                                              .GroupLayout.PREFERRED_SIZE,
                                                                                                              javax
                                                                                                              .swing
                                                                                                              .GroupLayout.DEFAULT_SIZE,
                                                                                                              javax
                                                                                                              .swing
                                                                                                              .GroupLayout.PREFERRED_SIZE))
                                                 .addContainerGap(41, Short.MAX_VALUE)).addGroup(jPanel5Layout
                                                                                                 .createParallelGroup(javax
                                                                                                                      .swing
                                                                                                                      .GroupLayout
                                                                                                                      .Alignment.LEADING)
                                                                                                 .addGroup(jPanel5Layout
                                                                                                           .createSequentialGroup()
                                                                                                           .addGap(29,
                                                                                                                   29,
                                                                                                                   29)
                                                                                                           .addComponent(jScrollPane8,
                                                                                                                         javax
                                                                                                                         .swing
                                                                                                                         .GroupLayout.DEFAULT_SIZE,
                                                                                                                         28,
                                                                                                                         Short.MAX_VALUE)
                                                                                                           .addContainerGap())));

        jSplitPane2.setRightComponent(jPanel5);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout
                                                                           .Alignment.LEADING)
                                         .addGroup(jPanel3Layout.createSequentialGroup()
                                                   .addContainerGap().addGroup(jPanel3Layout
                                                                               .createParallelGroup(javax
                                                                                                    .swing
                                                                                                    .GroupLayout
                                                                                                    .Alignment.LEADING)
                                                                               .addComponent(jSplitPane2,
                                                                                             javax
                                                                                             .swing
                                                                                             .GroupLayout.DEFAULT_SIZE,
                                                                                             749,
                                                                                             Short.MAX_VALUE)
                                                                               .addGroup(jPanel3Layout
                                                                                         .createSequentialGroup()
                                                                                         .addGroup(jPanel3Layout
                                                                                                   .createParallelGroup(javax
                                                                                                                        .swing
                                                                                                                        .GroupLayout
                                                                                                                        .Alignment.TRAILING,
                                                                                                                        false)
                                                                                                   .addComponent(jLabel9,
                                                                                                                 javax
                                                                                                                 .swing
                                                                                                                 .GroupLayout
                                                                                                                 .Alignment.LEADING,
                                                                                                                 javax
                                                                                                                 .swing
                                                                                                                 .GroupLayout.DEFAULT_SIZE,
                                                                                                                 javax
                                                                                                                 .swing
                                                                                                                 .GroupLayout.DEFAULT_SIZE,
                                                                                                                 Short.MAX_VALUE)
                                                                                                   .addComponent(jLabel8,
                                                                                                                 javax
                                                                                                                 .swing
                                                                                                                 .GroupLayout
                                                                                                                 .Alignment.LEADING,
                                                                                                                 javax
                                                                                                                 .swing
                                                                                                                 .GroupLayout.DEFAULT_SIZE,
                                                                                                                 100,
                                                                                                                 Short.MAX_VALUE))
                                                                                         .addPreferredGap(javax
                                                                                                          .swing
                                                                                                          .LayoutStyle
                                                                                                          .ComponentPlacement.UNRELATED)
                                                                                         .addGroup(jPanel3Layout
                                                                                                   .createParallelGroup(javax
                                                                                                                        .swing
                                                                                                                        .GroupLayout
                                                                                                                        .Alignment.LEADING)
                                                                                                   .addComponent(m_tAppender,
                                                                                                                 javax
                                                                                                                 .swing
                                                                                                                 .GroupLayout.DEFAULT_SIZE,
                                                                                                                 639,
                                                                                                                 Short.MAX_VALUE)
                                                                                                   .addComponent(m_tAppenderName,
                                                                                                                 javax
                                                                                                                 .swing
                                                                                                                 .GroupLayout.DEFAULT_SIZE,
                                                                                                                 639,
                                                                                                                 Short.MAX_VALUE))))
                                                   .addContainerGap()));
        jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout
                                                                         .Alignment.LEADING)
                                       .addGroup(jPanel3Layout.createSequentialGroup().addGroup(jPanel3Layout
                                                                                                .createParallelGroup(javax
                                                                                                                     .swing
                                                                                                                     .GroupLayout
                                                                                                                     .Alignment.BASELINE)
                                                                                                .addComponent(jLabel8)
                                                                                                .addComponent(m_tAppenderName,
                                                                                                              javax
                                                                                                              .swing
                                                                                                              .GroupLayout.PREFERRED_SIZE,
                                                                                                              javax
                                                                                                              .swing
                                                                                                              .GroupLayout.DEFAULT_SIZE,
                                                                                                              javax
                                                                                                              .swing
                                                                                                              .GroupLayout.PREFERRED_SIZE))
                                                 .addPreferredGap(javax.swing.LayoutStyle
                                                                  .ComponentPlacement.RELATED)
                                                 .addGroup(jPanel3Layout.createParallelGroup(javax
                                                                                             .swing
                                                                                             .GroupLayout
                                                                                             .Alignment.BASELINE)
                                                           .addComponent(jLabel9).addComponent(m_tAppender,
                                                                                               javax
                                                                                               .swing
                                                                                               .GroupLayout.PREFERRED_SIZE,
                                                                                               javax
                                                                                               .swing
                                                                                               .GroupLayout.DEFAULT_SIZE,
                                                                                               javax
                                                                                               .swing
                                                                                               .GroupLayout.PREFERRED_SIZE))
                                                 .addPreferredGap(javax.swing.LayoutStyle
                                                                  .ComponentPlacement.RELATED)
                                                 .addComponent(jSplitPane2,
                                                               javax.swing.GroupLayout.DEFAULT_SIZE,
                                                               195, Short.MAX_VALUE)
                                                 .addContainerGap()));

        javax.swing.GroupLayout pAppendersLayout = new javax.swing.GroupLayout(pAppenders);
        pAppenders.setLayout(pAppendersLayout);
        pAppendersLayout.setHorizontalGroup(pAppendersLayout.createParallelGroup(javax.swing
                                                                                 .GroupLayout
                                                                                 .Alignment.LEADING)
                                            .addComponent(jToolBar4,
                                                          javax.swing.GroupLayout.DEFAULT_SIZE, 801,
                                                          Short.MAX_VALUE).addGroup(pAppendersLayout
                                                                                    .createSequentialGroup()
                                                                                    .addContainerGap()
                                                                                    .addComponent(jScrollPane6,
                                                                                                  javax
                                                                                                  .swing
                                                                                                  .GroupLayout.DEFAULT_SIZE,
                                                                                                  781,
                                                                                                  Short.MAX_VALUE)
                                                                                    .addContainerGap())
                                            .addGroup(pAppendersLayout.createSequentialGroup()
                                                      .addContainerGap().addComponent(jPanel3,
                                                                                      javax.swing
                                                                                      .GroupLayout.DEFAULT_SIZE,
                                                                                      javax.swing
                                                                                      .GroupLayout.DEFAULT_SIZE,
                                                                                      Short.MAX_VALUE)
                                                      .addContainerGap()));
        pAppendersLayout.setVerticalGroup(pAppendersLayout.createParallelGroup(javax.swing
                                                                               .GroupLayout
                                                                               .Alignment.LEADING)
                                          .addGroup(pAppendersLayout.createSequentialGroup()
                                                    .addComponent(jToolBar4,
                                                                  javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                  25,
                                                                  javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle
                                                                     .ComponentPlacement.RELATED)
                                                    .addComponent(jScrollPane6,
                                                                  javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                  88,
                                                                  javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle
                                                                     .ComponentPlacement.RELATED)
                                                    .addComponent(jPanel3,
                                                                  javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                  javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                  Short.MAX_VALUE)
                                                    .addContainerGap()));

        m_tpLog4JDetails.addTab("Appenders", pAppenders);

        javax.swing.GroupLayout m_pLoggingLayout = new javax.swing.GroupLayout(m_pLogging);
        m_pLogging.setLayout(m_pLoggingLayout);
        m_pLoggingLayout.setHorizontalGroup(m_pLoggingLayout.createParallelGroup(javax.swing
                                                                                 .GroupLayout
                                                                                 .Alignment.LEADING)
                                            .addComponent(m_tpLog4JDetails,
                                                          javax.swing.GroupLayout.DEFAULT_SIZE, 806,
                                                          Short.MAX_VALUE).addGroup(m_pLoggingLayout
                                                                                    .createSequentialGroup()
                                                                                    .addGap(6, 6,
                                                                                            6)
                                                                                    .addComponent(m_cbSystemPolicy)
                                                                                    .addContainerGap()));
        m_pLoggingLayout.setVerticalGroup(m_pLoggingLayout.createParallelGroup(javax.swing
                                                                               .GroupLayout
                                                                               .Alignment.LEADING)
                                          .addGroup(m_pLoggingLayout.createSequentialGroup()
                                                    .addContainerGap().addComponent(m_cbSystemPolicy)
                                                    .addPreferredGap(javax.swing.LayoutStyle
                                                                     .ComponentPlacement.UNRELATED)
                                                    .addComponent(m_tpLog4JDetails)));

        m_tpMain.addTab("Logging", m_pLogging);

        m_epXML.setFont(new java.awt.Font("Courier New", 0, 11));
        jScrollPane1.setViewportView(m_epXML);

        javax.swing.GroupLayout m_pXMLConfigLayout = new javax.swing.GroupLayout(m_pXMLConfig);
        m_pXMLConfig.setLayout(m_pXMLConfigLayout);
        m_pXMLConfigLayout.setHorizontalGroup(m_pXMLConfigLayout.createParallelGroup(javax.swing
                                                                                     .GroupLayout
                                                                                     .Alignment.LEADING)
                                              .addGroup(m_pXMLConfigLayout.createSequentialGroup()
                                                        .addContainerGap().addComponent(jScrollPane1)
                                                        .addContainerGap()));
        m_pXMLConfigLayout.setVerticalGroup(m_pXMLConfigLayout.createParallelGroup(javax.swing
                                                                                   .GroupLayout
                                                                                   .Alignment.LEADING)
                                            .addGroup(m_pXMLConfigLayout.createSequentialGroup()
                                                      .addContainerGap().addComponent(jScrollPane1,
                                                                                      javax.swing
                                                                                      .GroupLayout.DEFAULT_SIZE,
                                                                                      460,
                                                                                      Short.MAX_VALUE)
                                                      .addContainerGap()));

        m_tpMain.addTab("XML configuration", m_pXMLConfig);

        bSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/cordys/coe/tools/orgmanager/save.gif"))); // NOI18N
        bSave.setText("Save");
        bSave.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    saveCurrentProcessorConfiguration(evt);
                }
            });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                  .addGroup(layout.createSequentialGroup().addComponent(bSave)
                                            .addPreferredGap(javax.swing.LayoutStyle
                                                             .ComponentPlacement.RELATED)
                                            .addComponent(m_tDN,
                                                          javax.swing.GroupLayout.DEFAULT_SIZE, 726,
                                                          Short.MAX_VALUE)).addComponent(m_tpMain,
                                                                                         javax.swing
                                                                                         .GroupLayout.DEFAULT_SIZE,
                                                                                         811,
                                                                                         Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup().addGroup(layout
                                                                                  .createParallelGroup(javax
                                                                                                       .swing
                                                                                                       .GroupLayout
                                                                                                       .Alignment.BASELINE)
                                                                                  .addComponent(bSave)
                                                                                  .addComponent(m_tDN,
                                                                                                javax
                                                                                                .swing
                                                                                                .GroupLayout.PREFERRED_SIZE,
                                                                                                javax
                                                                                                .swing
                                                                                                .GroupLayout.DEFAULT_SIZE,
                                                                                                javax
                                                                                                .swing
                                                                                                .GroupLayout.PREFERRED_SIZE))
                                          .addPreferredGap(javax.swing.LayoutStyle
                                                           .ComponentPlacement.RELATED)
                                          .addComponent(m_tpMain)));

        ChangeListener clMain = new ChangeListener()
        {
            public void stateChanged(ChangeEvent changeEvent)
            {
                JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
                int iIndex = sourceTabbedPane.getSelectedIndex();

                if (m_iMainIndex != iIndex)
                {
                    updateProcessorXML();
                }
                m_iMainIndex = iIndex;
            }
        };

        m_tpMain.addChangeListener(clMain);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * This method moves the currently selected row to the bottom.
     *
     * @param  tbl  The table to move the row of.
     */
    private void moveBottom(JTable tbl)
    {
        int iRowSelected = tbl.getSelectedRow();

        if ((iRowSelected >= 0) && (iRowSelected < (tbl.getRowCount() - 1)))
        {
            DefaultTableModel dtm = (DefaultTableModel) tbl.getModel();
            dtm.moveRow(iRowSelected, iRowSelected, tbl.getRowCount() - 1);
            tbl.setRowSelectionInterval(tbl.getRowCount() - 1, tbl.getRowCount() - 1);
        }
    }

    /**
     * This method moves the currently selected row down 1 row.
     *
     * @param  tbl  The table to move the row of.
     */
    private void moveDown(JTable tbl)
    {
        int iRowSelected = tbl.getSelectedRow();

        if ((iRowSelected >= 0) && (iRowSelected < (tbl.getRowCount() - 1)))
        {
            DefaultTableModel dtm = (DefaultTableModel) tbl.getModel();
            dtm.moveRow(iRowSelected, iRowSelected, iRowSelected + 1);
            tbl.setRowSelectionInterval(iRowSelected + 1, iRowSelected + 1);
        }
    }

    /**
     * This method moves the currently selected row to the top.
     *
     * @param  tbl  The table to move the row of.
     */
    private void moveTop(JTable tbl)
    {
        int iRowSelected = tbl.getSelectedRow();

        if (iRowSelected >= 0)
        {
            DefaultTableModel dtm = (DefaultTableModel) tbl.getModel();
            dtm.moveRow(iRowSelected, iRowSelected, 0);
            tbl.setRowSelectionInterval(0, 0);
        }
    }

    /**
     * This method moves the currently selected row up 1 row.
     *
     * @param  tbl  The table to move the row of.
     */
    private void moveUp(JTable tbl)
    {
        int iRowSelected = tbl.getSelectedRow();

        if (iRowSelected > 0)
        {
            DefaultTableModel dtm = (DefaultTableModel) tbl.getModel();
            dtm.moveRow(iRowSelected, iRowSelected, iRowSelected - 1);
            tbl.setRowSelectionInterval(iRowSelected - 1, iRowSelected - 1);
        }
    }

    /**
     * DOCUMENTME.
     *
     * @param  evt  DOCUMENTME
     */
    private void pCategoriesComponentHidden(java.awt.event.ComponentEvent evt) //GEN-FIRST:event_pCategoriesComponentHidden
    {
    } //GEN-LAST:event_pCategoriesComponentHidden

    /**
     * This method removes the debugger from the JVM options.
     */
    private void removeDebuggerFromOptions()
    {
        DefaultTableModel dtm = (DefaultTableModel) m_tblJVMOptions.getModel();
        int iCurrent = 0;

        while (iCurrent < dtm.getRowCount())
        {
            String sEntry = (String) dtm.getValueAt(iCurrent, 0);

            if (sEntry.matches("^-Xdebug$|^-Xnoagent$|^-Djava.compiler=.+|^-Xint$|^-Xrunjdwp:transport=.+"))
            {
                dtm.removeRow(iCurrent);
            }
            else
            {
                iCurrent++;
            }
        }
    }

    /**
     * This method removes the debugger from the JVM options.
     *
     * @param  evt  The event that occurred.
     */
    private void removeDebuggerFromOptions(java.awt.event.ActionEvent evt) //GEN-FIRST:event_removeDebuggerFromOptions
    {
        removeDebuggerFromOptions();
    } //GEN-LAST:event_removeDebuggerFromOptions

    /**
     * This method remove a row from the table.
     *
     * @param  tbl  The table to remove the row from.
     */
    private void removeTableRow(JTable tbl)
    {
        if (tbl.getSelectedRow() >= 0)
        {
            DefaultTableModel dtm = (DefaultTableModel) tbl.getModel();
            dtm.removeRow(tbl.getSelectedRow());
        }
    }

    /**
     * This method saves the current changes to the LDAP. First it need to update the processor
     * object with the data entered in the UI.
     *
     * @param  evt  The event that occurred.
     */
    private void saveCurrentProcessorConfiguration(java.awt.event.ActionEvent evt) //GEN-FIRST:event_saveCurrentProcessorConfiguration
    {
        if (MessageBoxUtil.showConfirmation("Are you sure you want to save the changes?"))
        {
            // Update the processor object
            updateProcessorXML();

            try
            {
                // Now form the update request to update the actual configuration.
                m_cgcClient.updateLDAPEntry(m_pProcessor.getLDAPEntry(),
                                            m_pProcessor.getNewEntry());

                if (MessageBoxUtil.showConfirmation("Configuration saved. Do you want to restart?"))
                {
                    m_phProcesses.restartProcessor(m_pProcessor);
                }
            }
            catch (Exception ex)
            {
                MessageBoxUtil.showError("Error updating SOAP processor configuration", ex);
            }
        }
    } //GEN-LAST:event_saveCurrentProcessorConfiguration

    /**
     * This method will show the proper layout.
     */
    private void showLayoutProperties()
    {
        Appender a = (Appender) m_mdlAppenders.getValueAt(m_tblAppender.getSelectedRow(), -1);

        if (a != null)
        {
            ArrayList<LayoutMetadata> al = m_law.getLayoutMetadata();

            for (LayoutMetadata lm : al)
            {
                if (lm.getName().equals(m_cbLayout.getSelectedItem()))
                {
                    m_mdlLayoutParameters.rebuildParameterList(a.getLayout().getParameters(), lm,
                                                               a.getLayout());
                }
            }
        }
    }

    /**
     * This method displays the Log4J settings.
     */
    private void showLog4JSettings()
    {
        Element eLog4J = m_pProcessor.getLog4JConfiguration();

        if (eLog4J != null)
        {
            m_epLog4J.setText(NiceDOMWriter.write(eLog4J, 2, true, false, false));
            m_epLog4J.setCaretPosition(0);

            try
            {
                m_lcLog4JConfiguration = m_pProcessor.getLog4JConfigurationWrapper();
                m_mdlCategories.clear();
                m_mdlCategories.rebuildCategoryList(m_lcLog4JConfiguration.getCategories());

                Root rRoot = m_lcLog4JConfiguration.getRoot();

                if (rRoot != null)
                {
                    m_cbRootPriority.setSelectedItem(rRoot.getPriority().getValue().toUpperCase());

                    ArrayList<String> alTemp = rRoot.getAppenderReferences();
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
                    m_tRootAppenders.setText(sbTemp.toString());
                }

                // Now let's get the appenders.
                m_mdlAppenders.clear();
                m_mdlAppenders.rebuildAppenderList(m_lcLog4JConfiguration.getAppenders());
            }
            catch (Exception ex)
            {
                MessageBoxUtil.showError("Error parsing the Log4J XML", ex);
            }
        }
    }

    /**
     * DOCUMENTME.
     *
     * @param  evt  DOCUMENTME
     */
    private void storeSelectedIndex(javax.swing.event.ChangeEvent evt) //GEN-FIRST:event_storeSelectedIndex
    {
        if (m_iCurrentTab != m_tpLog4JDetails.getSelectedIndex())
        {
            updateLog4JConfiguration(null);
        }

        m_iCurrentTab = m_tpLog4JDetails.getSelectedIndex();
    } //GEN-LAST:event_storeSelectedIndex

    /**
     * DOCUMENTME.
     *
     * @param  evt  DOCUMENTME
     */
    private void testPropertyChange(java.beans.PropertyChangeEvent evt) //GEN-FIRST:event_testPropertyChange
    {
    } //GEN-LAST:event_testPropertyChange

    /**
     * This method is called when the Log4J tabbed pane loses focus. Then we need ot update the
     * configuration object to keep it accurate.
     *
     * @param  evt  The event that occurred.
     */
    private void updateLog4JConfiguration(java.awt.event.FocusEvent evt) //GEN-FIRST:event_updateLog4JConfiguration
    {
        // We need to figure out which tab had the focus and only update when the
        // tab has changed.
        if (m_iCurrentTab == 0)
        {
            // Perhaps the XML was edited
            String sNewXML = m_epLog4J.getText();
            String sCurrent = NiceDOMWriter.write(m_lcLog4JConfiguration.toXML(), 2, true, false,
                                                  false);

            if (!sNewXML.equals(sCurrent))
            {
                // Reparse it.
                try
                {
                    Document dDoc = XMLHelper.createDocumentBuilder(false).parse(new ByteArrayInputStream(sNewXML
                                                                                                          .getBytes()));
                    m_pProcessor.setLog4JConfiguration(dDoc.getDocumentElement());
                    showLog4JSettings();
                }
                catch (Exception ex)
                {
                    MessageBoxUtil.showError("Error parsing new Log4J configuration", ex);
                }
            }
        }
        else
        {
            if (m_iCurrentTab == 1)
            {
                // Update the categories.
                m_lcLog4JConfiguration.setCategories(m_mdlCategories.getInternalData());
            }
            else if (m_iCurrentTab == 2)
            {
                // Update the appenders.
                m_lcLog4JConfiguration.setAppenders(m_mdlAppenders.getInternalData());
            }

            try
            {
                m_epLog4J.setText(NiceDOMWriter.write(m_lcLog4JConfiguration.toXML(), 2, true,
                                                      false, false));
            }
            catch (Exception e)
            {
                MessageBoxUtil.showError("Error writing Log4J configuration", e);
            }
        }
    } //GEN-LAST:event_updateLog4JConfiguration
}
