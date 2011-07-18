package com.cordys.coe.util.swing;

import java.awt.Frame;

import java.util.ArrayList;

import javax.swing.GroupLayout;

import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;

/**
 * This class shows a message which is resizable.
 *
 * @author  pgussow
 */
public class MessageDialog extends JDialog
{
    /**
     * Holds the dialog result.
     */
    private int m_iResult;
    /**
     * Holds the type of message.
     */
    private int m_iType;
    /**
     * Holds the caption.
     */
    private String m_sCaption;
    /**
     * Holds the details.
     */
    private String m_sDetails;
    /**
     * Holds the icon to show.
     */
    private String m_sIconName;
    /**
     * Holds the short message.
     */
    private String m_sShortMessage;

    /**
     * Creates new form MessageDialog.
     *
     * @param  fParent        THe parent frame.
     * @param  sCaption       The caption for the dialog.
     * @param  sShortMessage  The short message.
     * @param  sDetails       The detailed message.
     * @param  iType          The type. JOptionPane.ERROR_MESSAGE, JOptionPane.WARNING_MESSAGE,
     *                        JOptionPane.INFORMATION_MESSAGE and JOptionPane.QUESTION_MESSAGE are
     *                        supported.
     */
    public MessageDialog(Frame fParent, String sCaption, String sShortMessage, String sDetails,
                         int iType)
    {
        super(fParent, true);

        m_sShortMessage = sShortMessage;
        m_sDetails = sDetails;
        m_sCaption = sCaption;
        m_iType = iType;

        if (iType == JOptionPane.ERROR_MESSAGE)
        {
            m_sIconName = "error32.gif";
        }
        else if (iType == JOptionPane.WARNING_MESSAGE)
        {
            m_sIconName = "warning32.gif";
        }
        else if (iType == JOptionPane.INFORMATION_MESSAGE)
        {
            m_sIconName = "info32.gif";
        }
        else if (iType == JOptionPane.QUESTION_MESSAGE)
        {
            m_sIconName = "question32.gif";
        }

        initComponents();

        setTitle(m_sCaption);

        SwingUtils.centerDialog(this);
        setResizable(true);
    }

    /**
     * Main method.
     *
     * @param  saArguments  The command line arguments.
     */
    public static void main(String[] saArguments)
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            MessageBoxUtil.showError(":blaat", "nog meer");

            MessageBoxUtil.showConfirmation("Wat moet ik doen?");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * This method returns the result. For now always OK_option.
     *
     * @return  The result.
     */
    public int getResult()
    {
        return m_iResult;
    }

    /**
     * DOCUMENTME.
     *
     * @param  pButtons  DOCUMENTME
     */
    private void createButtons(JPanel pButtons)
    {
        ArrayList<JButton> alButtons = new ArrayList<JButton>();

        JButton bOK = new JButton();
        bOK.setMnemonic('o');
        bOK.setText("OK"); // NOI18N
        bOK.setName("bOK"); // NOI18N
        bOK.setPreferredSize(new java.awt.Dimension(75, 23));
        bOK.setSelected(true);
        bOK.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    m_iResult = JOptionPane.OK_OPTION;
                    setVisible(false);
                }
            });

        alButtons.add(bOK);

        if (m_iType == JOptionPane.QUESTION_MESSAGE)
        {
            JButton bCancel = new JButton();
            bCancel.setMnemonic('c');
            bCancel.setText("Cancel"); // NOI18N
            bCancel.setName("bCancel"); // NOI18N
            bCancel.setPreferredSize(new java.awt.Dimension(75, 23));
            bCancel.setSelected(true);
            bCancel.addActionListener(new java.awt.event.ActionListener()
                {
                    public void actionPerformed(java.awt.event.ActionEvent evt)
                    {
                        m_iResult = JOptionPane.CANCEL_OPTION;
                        setVisible(false);
                    }
                });

            alButtons.add(bCancel);
        }

        GroupLayout m_pButtonsLayout = new javax.swing.GroupLayout(pButtons);
        pButtons.setLayout(m_pButtonsLayout);

        SequentialGroup sqSequential = m_pButtonsLayout.createSequentialGroup().addContainerGap(222,
                                                                                                Short.MAX_VALUE);
        ParallelGroup pgParallelGroup = m_pButtonsLayout.createParallelGroup(javax.swing.GroupLayout
                                                                             .Alignment.BASELINE);

        for (int iCount = 0; iCount < alButtons.size(); iCount++)
        {
            JButton bButton = alButtons.get(iCount);
            sqSequential.addComponent(bButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80,
                                      javax.swing.GroupLayout.PREFERRED_SIZE);

            if (iCount < (alButtons.size() - 1))
            {
                sqSequential.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED);
            }

            pgParallelGroup.addComponent(bButton, javax.swing.GroupLayout.PREFERRED_SIZE,
                                         javax.swing.GroupLayout.DEFAULT_SIZE,
                                         javax.swing.GroupLayout.PREFERRED_SIZE);
        }

        m_pButtonsLayout.setHorizontalGroup(m_pButtonsLayout.createParallelGroup(javax.swing
                                                                                 .GroupLayout
                                                                                 .Alignment.LEADING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                                      sqSequential));

        m_pButtonsLayout.setVerticalGroup(m_pButtonsLayout.createParallelGroup(javax.swing
                                                                               .GroupLayout
                                                                               .Alignment.LEADING)
                                          .addGroup(pgParallelGroup));
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    private void initComponents()
    {
        JPopupMenu jPopupMenu1 = new javax.swing.JPopupMenu();
        javax.swing.JPanel pTop = new javax.swing.JPanel();

        JLabel lImage = new javax.swing.JLabel();
        javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        JTextPane tpTitleText = new javax.swing.JTextPane();

        JPanel m_pButtons = new javax.swing.JPanel();

        jPopupMenu1.setFocusable(false);
        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        pTop.setName("pTop"); // NOI18N
        pTop.setLayout(new java.awt.BorderLayout(10, 0));

        lImage.setIcon(new ImageIcon(getClass().getResource(m_sIconName)));
        lImage.setName("lImage"); // NOI18N
        lImage.setPreferredSize(new java.awt.Dimension(32, 32));
        pTop.add(lImage, java.awt.BorderLayout.WEST);

        jScrollPane1.setBorder(null);
        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tpTitleText.setBackground(java.awt.SystemColor.control);
        tpTitleText.setEditable(false);
        tpTitleText.setText(m_sShortMessage);
        tpTitleText.setFocusable(false);
        tpTitleText.setName("tpTitleText"); // NOI18N
        jScrollPane1.setViewportView(tpTitleText);

        pTop.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        m_pButtons.setName("m_pButtons"); // NOI18N

        createButtons(m_pButtons);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        ParallelGroup pgMain = layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING);
        SequentialGroup sgMain = layout.createSequentialGroup().addContainerGap()
                                       .addComponent(pTop, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                     javax.swing.GroupLayout.DEFAULT_SIZE,
                                                     javax.swing.GroupLayout.PREFERRED_SIZE)
                                       .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED);

        if ((m_sDetails != null) && (m_sDetails.length() > 0))
        {
            JScrollPane spDetail = new javax.swing.JScrollPane();
            JEditorPane epDetail = new javax.swing.JEditorPane();

            spDetail.setName("jScrollPane2"); // NOI18N

            epDetail.setBackground(new java.awt.Color(212, 208, 200));
            epDetail.setEditable(false);
            epDetail.setText(m_sDetails);
            epDetail.setName("m_epDetail"); // NOI18N
            spDetail.setViewportView(epDetail);

            pgMain.addComponent(spDetail, javax.swing.GroupLayout.Alignment.TRAILING,
                                javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE);
            sgMain.addComponent(spDetail, javax.swing.GroupLayout.DEFAULT_SIZE, 141,
                                Short.MAX_VALUE).addPreferredGap(javax.swing.LayoutStyle
                                                                 .ComponentPlacement.UNRELATED);
        }

        pgMain.addComponent(m_pButtons, javax.swing.GroupLayout.Alignment.TRAILING,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(pTop,
                                                                                                javax
                                                                                                .swing
                                                                                                .GroupLayout
                                                                                                .Alignment.TRAILING,
                                                                                                javax
                                                                                                .swing
                                                                                                .GroupLayout.DEFAULT_SIZE,
                                                                                                380,
                                                                                                Short.MAX_VALUE);
        sgMain.addComponent(m_pButtons, javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap();

        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                  .addGroup(layout.createSequentialGroup().addContainerGap()
                                            .addGroup(pgMain).addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sgMain));

        pack();
    } // </editor-fold>
}
