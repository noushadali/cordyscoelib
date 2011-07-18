package com.cordys.coe.tools.orgmanager;

import com.cordys.coe.util.ObjectData;
import com.cordys.coe.util.cgc.CordysGatewayClientException;
import com.cordys.coe.util.cgc.ICordysGatewayClient;
import com.cordys.coe.util.swing.CGCLoginDialog;
import com.cordys.coe.util.swing.MessageBoxUtil;

import com.novell.ldap.LDAPEntry;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedHashMap;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.UIManager;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * This tool can be used to manage SOAP processors.
 *
 * @author  pgussow
 */
public class OrganizationManager extends javax.swing.JFrame
{
    /**
     * Variables declaration - do not modify//GEN-BEGIN:variables.
     */
    javax.swing.JCheckBox m_cbIncludeNonAutomatic;
    /**
     * DOCUMENTME.
     */
    javax.swing.JComboBox m_cbOrganizations;
    /**
     * DOCUMENTME.
     */
    javax.swing.JMenuItem m_miShowError;
    /**
     * DOCUMENTME.
     */
    javax.swing.JSeparator m_sErrorSep;
    /**
     * DOCUMENTME.
     */
    com.cordys.coe.tools.orgmanager.SoapProcessorDetails m_spdDetails;
    /**
     * DOCUMENTME.
     */
    javax.swing.JTable m_tblProcessors;
    /**
     * DOCUMENTME.
     */
    javax.swing.JTextField m_tInterval;
    /**
     * DOCUMENTME.
     */
    javax.swing.JMenuItem miReset;
    /**
     * DOCUMENTME.
     */
    javax.swing.JMenuItem miStop;
    /**
     * DOCUMENTME.
     */
    javax.swing.JPopupMenu pmPopup;
    // End of variables declaration//GEN-END:variables

    /**
     * Holds the current sort-order for the column.
     */
    private boolean[] abSortOrder;
    /**
     * Holds the connection to use.
     */
    private ICordysGatewayClient m_cgcClient;
    /**
     * Holds the model for the table.
     */
    private ProcessorsTableModel m_mdlProcessors;
    /**
     * Holds the currently applied filter.
     */
    private ObjectData<LDAPEntry> m_odSelected = null;
    /**
     * Holds the process handler to use.
     */
    private ProcessorHandler m_phProcesses;

    /**
     * Holds the currently selecte processor based on the X/Y coordinates.
     */
    private Processor m_pSelectedProcessor = null;

    /**
     * Creates new form OrganizationManager.
     */
    public OrganizationManager()
    {
        initComponents();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension labelSize = getSize();
        setLocation((screenSize.width / 2) - (labelSize.width / 2),
                    (screenSize.height / 2) - (labelSize.height / 2));

        TableColumnModel cm = m_tblProcessors.getColumnModel();
        Enumeration<TableColumn> e = cm.getColumns();

        while (e.hasMoreElements())
        {
            TableColumn tc = e.nextElement();

            if (tc.getHeaderValue().equals("Status"))
            {
                tc.setCellRenderer(new ProcessorStatusRenderer());
            }
        }

        setExtendedState(JFrame.MAXIMIZED_BOTH);

        CGCLoginDialog cld = new CGCLoginDialog(this, true);
        cld.setVisible(true);

        if (cld.getConnection() == null)
        {
            System.exit(1);
        }

        setConnection(cld.getConnection());
    }

    /**
     * DOCUMENTME.
     *
     * @param  args  the command line arguments
     */
    public static void main(String[] args)
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            java.awt.EventQueue.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        new OrganizationManager().setVisible(true);
                    }
                });
        }
        catch (Exception e)
        {
            MessageBoxUtil.showError("Error starting Organization Manager Screen", e);
        }
    }

    /**
     * This method sorts the rows in the table.
     *
     * @param  model      The current table model.
     * @param  colIndex   The index of the column to sort.
     * @param  ascending  Whether or not it should be ascending or decending.
     */
    public void sortAllRowsBy(ProcessorsTableModel model, int colIndex, boolean ascending)
    {
        ArrayList<Processor> data = model.getInternalData();
        Collections.sort(data, new ColumnSorter(colIndex, ascending));
        model.fireTableStructureChanged();
    }

    /**
     * DOCUMENTME.
     *
     * @param  evt  DOCUMENTME
     */
    private void bRefreshActionPerformed(java.awt.event.ActionEvent evt) //GEN-FIRST:event_bRefreshActionPerformed
    {
        try
        {
            m_phProcesses.refreshStatus(); //GEN-LAST:event_bRefreshActionPerformed
        }
        catch (Exception ex)
        {
            MessageBoxUtil.showError(this, "Error refreshing status", ex);
        }
    }

    /**
     * DOCUMENTME.
     *
     * @param  evt  DOCUMENTME
     */
    private void bReloadActionPerformed(java.awt.event.ActionEvent evt) //GEN-FIRST:event_bReloadActionPerformed
    {
        try
        {
            m_phProcesses.rebuildList();
        }
        catch (Exception ex)
        {
            MessageBoxUtil.showError(this, "Error refreshing status", ex);
        }
    } //GEN-LAST:event_bReloadActionPerformed

    /**
     * DOCUMENTME.
     *
     * @param  evt  DOCUMENTME
     */
    @SuppressWarnings("unchecked")
    private void bShowSPsActionPerformed(java.awt.event.ActionEvent evt) //GEN-FIRST:event_bShowSPsActionPerformed
    {
        ObjectData<LDAPEntry> od = (ObjectData<LDAPEntry>) m_cbOrganizations.getSelectedItem();

        m_odSelected = od;

        String sOrganization = null;

        if ((od != null) && (od.getValue() != null))
        {
            // Show all
            sOrganization = od.getValue().getDN();
        }

        showProperSPs(sOrganization);
    } //GEN-LAST:event_bShowSPsActionPerformed

    /**
     * DOCUMENTME.
     *
     * @param  evt  DOCUMENTME
     */
    private void bStartOrganizationActionPerformed(java.awt.event.ActionEvent evt) //GEN-FIRST:event_bStartOrganizationActionPerformed
    {
        handleFullOrgAction(true);
    } //GEN-LAST:event_bStartOrganizationActionPerformed

    /**
     * DOCUMENTME.
     *
     * @param  evt  DOCUMENTME
     */
    private void bStopOrganizationActionPerformed(java.awt.event.ActionEvent evt) //GEN-FIRST:event_bStopOrganizationActionPerformed
    {
        handleFullOrgAction(false);
    } //GEN-LAST:event_bStopOrganizationActionPerformed

    /**
     * This method clears the current information.
     */
    private void cleanInformation()
    {
        // Clear the organization box.
        DefaultComboBoxModel dcbm = (DefaultComboBoxModel) m_cbOrganizations.getModel();

        while (dcbm.getSize() > 0)
        {
            dcbm.removeElementAt(0);
        }

        clearTable();
    }

    /**
     * This method clears the current list of SOAP processor.
     */
    private void clearTable()
    {
        m_mdlProcessors.clear();
    }

    /**
     * This method does the actual action processing (start/stop/reset/restart).
     *
     * @param  p        The processor.
     * @param  sAction  The action to execute.
     */
    private void doActionOnProcessor(Processor p, String sAction)
    {
        try
        {
            if ("Start".equals(sAction))
            {
                m_phProcesses.startProcessor(p);
            }
            else if ("Stop".equals(sAction))
            {
                m_phProcesses.stopProcessor(p);
            }
            else if ("Reset".equals(sAction))
            {
                m_phProcesses.resetProcessor(p);
            }
            else if ("Restart".equals(sAction))
            {
                m_phProcesses.restartProcessor(p);
            }

            m_phProcesses.refreshStatus();
        }
        catch (Exception e)
        {
            MessageBoxUtil.showError(this,
                                     "Error doing action '" + sAction + "' on processor " +
                                     p.getDN(), e);
        }
    }

    /**
     * This method is called to start/stop a full organization.
     *
     * @param  bStart  Whether or not the the processors should be started (true) or stopped
     *                 (false).
     */
    @SuppressWarnings("unchecked")
    private void handleFullOrgAction(boolean bStart)
    {
        ObjectData<LDAPEntry> od = (ObjectData<LDAPEntry>) m_cbOrganizations.getSelectedItem();

        String sOrganization = null;

        if ((od != null) && (od.getValue() != null))
        {
            // Show all
            sOrganization = od.getValue().getDN();
        }

        if ((sOrganization == null) && (bStart == false))
        {
            // They chose 'All' which is not supported
            MessageBoxUtil.showError(this,
                                     "You cannot stop the whole Cordys installation using this tool.\nYou need to stop the monitor service on the actual machine.");
        }
        else if ((sOrganization != null) && sOrganization.startsWith("o=system") &&
                     (bStart == false))
        {
            MessageBoxUtil.showError(this,
                                     "Stopping the system organization is not supported since it will lead to unpredictable results.");
        }
        else
        {
            try
            {
                if (bStart)
                {
                    m_phProcesses.startOrganization(sOrganization,
                                                    m_cbIncludeNonAutomatic.isSelected());
                }
                else
                {
                    m_phProcesses.stopOrganization(sOrganization,
                                                   m_cbIncludeNonAutomatic.isSelected());
                }

                m_phProcesses.refreshStatus();
            }
            catch (Exception e)
            {
                MessageBoxUtil.showError(this,
                                         "Error " + (bStart ? "starting" : "stopping") +
                                         " the organization " + sOrganization, e);
            }
        }
    }

    /**
     * DOCUMENTME.
     *
     * @param  evt  DOCUMENTME
     */
    private void handleProcessorAction(java.awt.event.ActionEvent evt) //GEN-FIRST:event_handleProcessorAction
    {
        String sAction = evt.getActionCommand();

        // Figure out which processor was clicked
        int[] aiSelected = m_tblProcessors.getSelectedRows();

        if (aiSelected.length == 0)
        {
            doActionOnProcessor(m_pSelectedProcessor, sAction);
        }
        else
        {
            for (int iCount = 0; iCount < aiSelected.length; iCount++)
            {
                Processor p = (Processor) m_mdlProcessors.getValueAt(aiSelected[iCount], -1);

                if (p != null)
                {
                    doActionOnProcessor(p, sAction);
                }
            }
        }
    } //GEN-LAST:event_handleProcessorAction

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    private void initComponents()
    {
        pmPopup = new javax.swing.JPopupMenu();

        javax.swing.JMenuItem miStart = new javax.swing.JMenuItem();
        miStop = new javax.swing.JMenuItem();

        javax.swing.JMenuItem miRestart = new javax.swing.JMenuItem();
        javax.swing.JSeparator jSeparator2 = new javax.swing.JSeparator();
        miReset = new javax.swing.JMenuItem();
        m_sErrorSep = new javax.swing.JSeparator();
        m_miShowError = new javax.swing.JMenuItem();

        javax.swing.JToolBar jToolBar1 = new javax.swing.JToolBar();
        javax.swing.JButton jButton1 = new javax.swing.JButton();
        javax.swing.JToolBar.Separator jSeparator4 = new javax.swing.JToolBar.Separator();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        m_cbOrganizations = new javax.swing.JComboBox();

        javax.swing.JButton bShowSPs = new javax.swing.JButton();
        javax.swing.JButton bRefresh = new javax.swing.JButton();
        javax.swing.JButton bReload = new javax.swing.JButton();
        javax.swing.JToolBar.Separator jSeparator1 = new javax.swing.JToolBar.Separator();
        javax.swing.JButton bStartOrganization = new javax.swing.JButton();
        javax.swing.JButton bStopOrganization = new javax.swing.JButton();
        m_cbIncludeNonAutomatic = new javax.swing.JCheckBox();

        javax.swing.JToolBar.Separator jSeparator3 = new javax.swing.JToolBar.Separator();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        m_tInterval = new javax.swing.JTextField();

        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        javax.swing.JSplitPane spSplitPane = new javax.swing.JSplitPane();
        javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        m_mdlProcessors = new ProcessorsTableModel();
        m_tblProcessors = new javax.swing.JTable();

        javax.swing.JScrollPane jScrollPane2 = new javax.swing.JScrollPane();
        m_spdDetails = new com.cordys.coe.tools.orgmanager.SoapProcessorDetails();

        miStart.setText("Start");
        miStart.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    handleProcessorAction(evt);
                }
            });
        pmPopup.add(miStart);

        miStop.setText("Stop");
        miStop.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    handleProcessorAction(evt);
                }
            });
        pmPopup.add(miStop);

        miRestart.setText("Restart");
        miRestart.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    handleProcessorAction(evt);
                }
            });
        pmPopup.add(miRestart);
        pmPopup.add(jSeparator2);

        miReset.setText("Reset");
        miReset.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    handleProcessorAction(evt);
                }
            });
        pmPopup.add(miReset);
        pmPopup.add(m_sErrorSep);

        m_miShowError.setText("Show configuration error");
        m_miShowError.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    showConfigurationError(evt);
                }
            });
        pmPopup.add(m_miShowError);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Organization manager"); // NOI18N
        setIconImage(Toolkit.getDefaultToolkit().createImage(OrganizationManager.class.getResource("orgman.gif")));

        jToolBar1.setRollover(true);

        jButton1.setText("Connect");
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    jButton1ActionPerformed(evt);
                }
            });
        jToolBar1.add(jButton1);
        jToolBar1.add(jSeparator4);

        jLabel1.setText("Organization");
        jToolBar1.add(jLabel1);

        m_cbOrganizations.setModel(new javax.swing.DefaultComboBoxModel(new String[]
                                                                        {
                                                                            "Item 1", "Item 2",
                                                                            "Item 3", "Item 4"
                                                                        }));
        jToolBar1.add(m_cbOrganizations);

        bShowSPs.setText("Show SPs");
        bShowSPs.setFocusable(false);
        bShowSPs.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bShowSPs.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bShowSPs.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    bShowSPsActionPerformed(evt);
                }
            });
        jToolBar1.add(bShowSPs);

        bRefresh.setText("Refresh");
        bRefresh.setFocusable(false);
        bRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bRefresh.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    bRefreshActionPerformed(evt);
                }
            });
        jToolBar1.add(bRefresh);

        bReload.setText("Reload");
        bReload.setFocusable(false);
        bReload.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bReload.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bReload.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    bReloadActionPerformed(evt);
                }
            });
        jToolBar1.add(bReload);
        jToolBar1.add(jSeparator1);

        bStartOrganization.setText("Start organization");
        bStartOrganization.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    bStartOrganizationActionPerformed(evt);
                }
            });
        jToolBar1.add(bStartOrganization);

        bStopOrganization.setText("Stop organization");
        bStopOrganization.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    bStopOrganizationActionPerformed(evt);
                }
            });
        jToolBar1.add(bStopOrganization);

        m_cbIncludeNonAutomatic.setSelected(true);
        m_cbIncludeNonAutomatic.setText("Include non-automatic");
        jToolBar1.add(m_cbIncludeNonAutomatic);
        jToolBar1.add(jSeparator3);

        jLabel2.setText("Refresh interval:");
        jToolBar1.add(jLabel2);

        m_tInterval.setText("15");
        m_tInterval.setMaximumSize(new java.awt.Dimension(50, 20));
        m_tInterval.setMinimumSize(new java.awt.Dimension(30, 20));
        m_tInterval.setPreferredSize(new java.awt.Dimension(30, 20));
        m_tInterval.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    updateInterval(evt);
                }
            });
        m_tInterval.addFocusListener(new java.awt.event.FocusAdapter()
            {
                public void focusLost(java.awt.event.FocusEvent evt)
                {
                    m_tIntervalFocusLost(evt);
                }
            });
        m_tInterval.addHierarchyListener(new java.awt.event.HierarchyListener()
            {
                public void hierarchyChanged(java.awt.event.HierarchyEvent evt)
                {
                    m_tIntervalHierarchyChanged(evt);
                }
            });
        jToolBar1.add(m_tInterval);

        jLabel3.setText("seconds");
        jToolBar1.add(jLabel3);

        spSplitPane.setDividerLocation(200);
        spSplitPane.setDividerSize(7);
        spSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        spSplitPane.setResizeWeight(0.5);
        spSplitPane.setOneTouchExpandable(true);

        m_tblProcessors = new JProcessorTable();
        m_tblProcessors.setModel(m_mdlProcessors);
        m_tblProcessors.addMouseListener(new PopupListener());

        JTableHeader thHeader = m_tblProcessors.getTableHeader();
        thHeader.addMouseListener(new ColumnHeaderListener());
        jScrollPane1.setViewportView(m_tblProcessors);

        DefaultTableColumnModel dcmModel = (DefaultTableColumnModel)
                                               m_tblProcessors.getColumnModel();
        dcmModel.getColumn(1).setMaxWidth(70);
        dcmModel.getColumn(2).setMaxWidth(150);
        dcmModel.getColumn(3).setMaxWidth(70);
        dcmModel.getColumn(4).setMaxWidth(70);
        dcmModel.getColumn(5).setMaxWidth(70);
        dcmModel.getColumn(6).setMaxWidth(70);

        // Initialize sort order.
        abSortOrder = new boolean[dcmModel.getColumnCount()];

        for (int iCount = 0; iCount < abSortOrder.length; iCount++)
        {
            abSortOrder[iCount] = true;
        }

        m_tblProcessors.addMouseListener(new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    if (e.getClickCount() == 2)
                    {
                        Point pOrigin = e.getPoint();
                        int iRow = m_tblProcessors.rowAtPoint(pOrigin);

                        if (iRow >= 0)
                        {
                            Processor pProcessor = (Processor) m_mdlProcessors.getValueAt(iRow, -1);

                            showProcessorDetails(pProcessor);
                        }
                    }
                }
            });

        spSplitPane.setTopComponent(jScrollPane1);

        jScrollPane2.setViewportView(m_spdDetails);

        spSplitPane.setBottomComponent(jScrollPane2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                  .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                963, Short.MAX_VALUE).addComponent(spSplitPane,
                                                                                   javax.swing
                                                                                   .GroupLayout.DEFAULT_SIZE,
                                                                                   963,
                                                                                   Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup().addComponent(jToolBar1,
                                                                                      javax.swing
                                                                                      .GroupLayout.PREFERRED_SIZE,
                                                                                      25,
                                                                                      javax.swing
                                                                                      .GroupLayout.PREFERRED_SIZE)
                                          .addPreferredGap(javax.swing.LayoutStyle
                                                           .ComponentPlacement.RELATED)
                                          .addComponent(spSplitPane,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE, 390,
                                                        Short.MAX_VALUE)));

        pack();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENTME.
     *
     * @param  evt  DOCUMENTME
     */
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) //GEN-FIRST:event_jButton1ActionPerformed
    {
        CGCLoginDialog cld = new CGCLoginDialog(this, true);
        cld.setVisible(true);

        if (cld.getConnection() != null)
        {
            setConnection(cld.getConnection());
        }
    } //GEN-LAST:event_jButton1ActionPerformed

    /**
     * DOCUMENTME.
     *
     * @param  evt  DOCUMENTME
     */
    private void m_tIntervalFocusLost(java.awt.event.FocusEvent evt) //GEN-FIRST:event_m_tIntervalFocusLost
    {
        updateInterval(null);
    } //GEN-LAST:event_m_tIntervalFocusLost

    /**
     * DOCUMENTME.
     *
     * @param  evt  DOCUMENTME
     */
    private void m_tIntervalHierarchyChanged(java.awt.event.HierarchyEvent evt) //GEN-FIRST:event_m_tIntervalHierarchyChanged
    {
        updateInterval(null);
    } //GEN-LAST:event_m_tIntervalHierarchyChanged

    /**
     * This method sets the connection that should be used for this connection.
     *
     * @param  cgcClient  The connection to use.
     */
    private void setConnection(ICordysGatewayClient cgcClient)
    {
        m_cgcClient = cgcClient;

        try
        {
            // Create the process handler
            int iInterval = Integer.parseInt(m_tInterval.getText());
            m_phProcesses = new ProcessorHandler(m_cgcClient, iInterval,
                                                 new LocalProcessHandlerCallback());

            cleanInformation();

            // Fill the organization box.
            updateTableAndCombo();

            // Set the proper connection for the detail screen
            m_spdDetails.setConnection(m_cgcClient);
            m_spdDetails.setProcessorHandler(m_phProcesses);
        }
        catch (Exception ex)
        {
            MessageBoxUtil.showError(this, "Error getting organizations", ex);
        }
    }

    /**
     * DOCUMENTME.
     *
     * @param  evt  DOCUMENTME
     */
    private void showConfigurationError(java.awt.event.ActionEvent evt) //GEN-FIRST:event_showConfigurationError
    {
        if ((m_pSelectedProcessor != null) && m_pSelectedProcessor.hasConfigurationError())
        {
            MessageBoxUtil.showError(this,
                                     "SOAP Processor '" + m_pSelectedProcessor.getDN() +
                                     "' could not be started.",
                                     m_pSelectedProcessor.getErrorDetails());
        }
    } //GEN-LAST:event_showConfigurationError

    /**
     * This method is called when the details of a Soap Processor should be shown.
     *
     * @param  pProcessor  The processor to display.
     */
    private void showProcessorDetails(Processor pProcessor)
    {
        m_spdDetails.setProcessor(pProcessor);
    }

    /**
     * This method shows the proper soap processors in the table based on the selection.
     *
     * @param  sOrganization  The organization to show the SPs from. If null all SPs will be shown.
     */
    private void showProperSPs(String sOrganization)
    {
        // Now we can fill the SOAP processors. First we'll clear the table.
        ArrayList<Processor> alProcessors = m_phProcesses.getProcessors();
        ArrayList<Processor> alToBeShown = new ArrayList<Processor>();

        for (Processor pProcessor : alProcessors)
        {
            // Add the processor to the table.
            if ((sOrganization == null) || pProcessor.getOrganization().equals(sOrganization))
            {
                alToBeShown.add(pProcessor);
            }
        }

        m_mdlProcessors.rebuildProcessorList(alToBeShown);
    }

    /**
     * DOCUMENTME.
     *
     * @param  evt  DOCUMENTME
     */
    private void updateInterval(java.awt.event.ActionEvent evt) //GEN-FIRST:event_updateInterval
    {
        try
        {
            m_phProcesses.setRefreshInterval(Integer.parseInt(m_tInterval.getText()));
        }
        catch (Exception e)
        {
            // Ignore it.
        }
    } //GEN-LAST:event_updateInterval

    /**
     * This method will update the status for the proper fields.
     *
     * @param  lhmOrgsAndProcessors
     */
    private void updateProcessorStatistics(LinkedHashMap<String, LinkedHashMap<String, Processor>> lhmOrgsAndProcessors)
    {
        ObjectData<LDAPEntry> od = m_odSelected;

        String sOrganization = null;

        if ((od != null) && (od.getValue() != null))
        {
            // Show all
            sOrganization = od.getValue().getDN();
        }

        showProperSPs(sOrganization);
    }

    /**
     * This method updates the current combo and table with the proper data.
     *
     * @throws  CordysGatewayClientException
     */
    private void updateTableAndCombo()
                              throws CordysGatewayClientException
    {
        DefaultComboBoxModel dcbm = (DefaultComboBoxModel) m_cbOrganizations.getModel();
        dcbm.addElement(new ObjectData<LDAPEntry>("All", null));

        ArrayList<ObjectData<LDAPEntry>> alOrgs = m_phProcesses.getOrganizations();

        for (ObjectData<LDAPEntry> od : alOrgs)
        {
            dcbm.addElement(od);
        }

        showProperSPs(null);
    }
    // End of variables declaration

    /**
     * Class that listens for the clicks on the colum headers.
     */
    public class ColumnHeaderListener extends MouseAdapter
    {
        /**
         * Occurs when the header is clicked.
         *
         * @param  evt  The event that occured.
         */
        @Override public void mouseClicked(MouseEvent evt)
        {
            JTable table = ((JTableHeader) evt.getSource()).getTable();
            TableColumnModel colModel = table.getColumnModel();

            // The index of the column whose header was clicked
            int vColIndex = colModel.getColumnIndexAtX(evt.getX());

            // int mColIndex = table.convertColumnIndexToModel(vColIndex);
            // Return if not clicked on any column header
            if (vColIndex == -1)
            {
                return;
            }

            // Determine if mouse was clicked between column heads
            Rectangle headerRect = table.getTableHeader().getHeaderRect(vColIndex);

            if (vColIndex == 0)
            {
                headerRect.width -= 3; // Hard-coded constant
            }
            else
            {
                headerRect.grow(-3, 0); // Hard-coded constant
            }

            if (!headerRect.contains(evt.getX(), evt.getY()))
            {
                // Mouse was clicked between column heads
                // vColIndex is the column head closest to the click
                // vLeftColIndex is the column head to the left of the click
                // int vLeftColIndex = vColIndex;
                // if (evt.getX() < headerRect.x)
                // {
                // vLeftColIndex--;
                // }
            }
            else
            {
                // Sort the column
                boolean bAsc = abSortOrder[vColIndex];
                sortAllRowsBy(m_mdlProcessors, vColIndex, bAsc);
                abSortOrder[vColIndex] = !bAsc;
            }
        }
    }

    /**
     * Class to sort the rows.
     */
    public class ColumnSorter
        implements Comparator<Object>
    {
        /**
         * Indicates whether or not the sort should be ascending.
         */
        boolean ascending;
        /**
         * The index of the column.
         */
        int iColumnIndex;

        /**
         * Constructor.
         *
         * @param  colIndex   The index of the column.
         * @param  ascending  Indicates whether or not the sort should be ascending.
         */
        ColumnSorter(int colIndex, boolean ascending)
        {
            iColumnIndex = colIndex;
            this.ascending = ascending;
        }

        /**
         * Compares its two arguments for order. Returns a negative integer, zero, or a positive
         * integer as the first argument is less than, equal to, or greater than the second.
         *
         * @param   a  the first object to be compared.
         * @param   b  the second object to be compared.
         *
         * @return  a negative integer, zero, or a positive integer as the first argument is less
         *          than, equal to, or greater than the second.
         */

        @SuppressWarnings("unchecked")
        public int compare(Object a, Object b)
        {
            Processor v1 = (Processor) a;
            Processor v2 = (Processor) b;
            Object o1 = null;
            Object o2 = null;

            // Get the proper values
            switch (iColumnIndex)
            {
                case 0:
                    o1 = v1.getShortName();
                    o2 = v2.getShortName();
                    break;

                case 1:
                    o1 = v1.getOrganizationShortName();
                    o2 = v2.getOrganizationShortName();
                    break;

                case 2:
                    o1 = v1.getStatus();
                    o2 = v2.getStatus();
                    break;

                case 3:
                    o1 = v1.getProcessID();
                    o2 = v2.getProcessID();
                    break;

                case 4:
                    o1 = v1.getComputer();
                    o2 = v2.getComputer();
                    break;

                case 5:
                    o1 = v1.startsAutomatically();
                    o2 = v2.startsAutomatically();
                    break;

                case 6:
                    o1 = v1.isInDebugMode();
                    o2 = v2.isInDebugMode();
                    break;

                default:
                    o1 = v1;
                    o2 = v2;
                    break;
            }

            // Treat empty strains like nulls
            if (o1 instanceof String)
            {
                if ((((String) o1).length() == 0))
                {
                    o1 = null;
                }
                else
                {
                    o1 = ((String) o1).toLowerCase();
                }
            }

            if (o2 instanceof String)
            {
                if ((((String) o2).length() == 0))
                {
                    o2 = null;
                }
                else
                {
                    o2 = ((String) o2).toLowerCase();
                }
            }

            // Sort nulls so they appear last, regardless
            // of sort order
            if ((o1 == null) && (o2 == null))
            {
                return 0;
            }
            else if (o1 == null)
            {
                return 1;
            }
            else if (o2 == null)
            {
                return -1;
            }
            else if (o1 instanceof Comparable)
            {
                if (ascending)
                {
                    return ((Comparable<Object>) o1).compareTo(o2);
                }
                return ((Comparable<Object>) o2).compareTo(o1);
            }
            else
            {
                if (ascending)
                {
                    return o1.toString().compareTo(o2.toString());
                }
                return o2.toString().compareTo(o1.toString());
            }
        }
    }

    /**
     * This class is used to determine whether or not the popup menu should be shown.
     */
    public class PopupListener extends MouseAdapter
    {
        /**
         * This method shows the popupmenu if needed.
         *
         * @param  meEvent  The mouseevent that occured.
         */
        @Override public void mousePressed(MouseEvent meEvent)
        {
            maybeShowPopup(meEvent);
        }

        /**
         * This method shows the popupmenu if needed.
         *
         * @param  meEvent  The mouseevent that occured.
         */
        @Override public void mouseReleased(MouseEvent meEvent)
        {
            maybeShowPopup(meEvent);
        }

        /**
         * This method shows the popupmenu if needed.
         *
         * @param  meEvent  The mouseevent that occured.
         */
        private void maybeShowPopup(MouseEvent meEvent)
        {
            if (meEvent.isPopupTrigger())
            {
                // Check to see if the processor under the X/Y points
                // has a configuration error.
                int iRow = m_tblProcessors.rowAtPoint(new Point(meEvent.getX(), meEvent.getY()));
                m_pSelectedProcessor = (Processor) m_mdlProcessors.getValueAt(iRow, -1);

                if ((m_pSelectedProcessor != null) && m_pSelectedProcessor.hasConfigurationError())
                {
                    m_sErrorSep.setVisible(true);
                    m_miShowError.setVisible(true);
                }
                else
                {
                    m_sErrorSep.setVisible(false);
                    m_miShowError.setVisible(false);
                }

                pmPopup.show(meEvent.getComponent(), meEvent.getX(), meEvent.getY());
            }
        }
    }

    /**
     * The processor table is customized to be able to set backgrounds for the rows based on the
     * status of a processor.
     */
    private class JProcessorTable extends JTable
    {
        /**
         * This method will set the proper background based on the status.
         *
         * @param   renderer  The actual renderer.
         * @param   iRow      The index of the row.
         * @param   iColumn   The column to render.
         *
         * @return  The component to show.
         */
        @Override public Component prepareRenderer(TableCellRenderer renderer, int iRow,
                                                   int iColumn)
        {
            Component comp = super.prepareRenderer(renderer, iRow, iColumn);

            // Only change the background if the row is not selected.
            Color cRed = new Color(255, 180, 180);
            Color cGreen = new Color(219, 255, 197);
            Color cOrange = new Color(255, 214, 126);

            if (!isCellSelected(iRow, iColumn))
            {
                Processor p = (Processor) m_mdlProcessors.getValueAt(iRow, -1);

                if (p != null)
                {
                    if (p.getStatus().equals(Processor.STATUS_STARTED))
                    {
                        comp.setBackground(cGreen);
                    }
                    else if (p.getStatus().equals(Processor.STATUS_STARTING))
                    {
                        comp.setBackground(cOrange);
                    }
                    else if (p.getStatus().equals(Processor.STATUS_CONFIGURATION_ERROR))
                    {
                        comp.setBackground(cRed);
                    }
                    else if (p.getStatus().equals(Processor.STATUS_STOPPED))
                    {
                        comp.setBackground(Color.getColor("Table.foreground"));
                    }
                }
            }

            return comp;
        }
    }

    /**
     * This method handles the callback when the status of the processors was updated.
     */
    private class LocalProcessHandlerCallback
        implements IProcessHandlerCallback
    {
        /**
         * @see  com.cordys.coe.tools.orgmanager.IProcessHandlerCallback#onStatusUpdate(java.util.LinkedHashMap)
         */
        public void onStatusUpdate(final LinkedHashMap<String, LinkedHashMap<String, Processor>> lhmOrgsAndProcessors)
        {
            EventQueue.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        updateProcessorStatistics(lhmOrgsAndProcessors);
                    }
                });
        }
    }
}
