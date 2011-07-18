package com.cordys.coe.util.swing;

import com.cordys.coe.util.NameValuePair;
import com.cordys.coe.util.swing.BusyLabel;
import com.cordys.coe.util.swing.SwingUtils;

import java.awt.EventQueue;
import java.awt.Frame;

import java.io.IOException;

import javax.imageio.ImageIO;

import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.log4j.Logger;

/**
 * This dialog can be used in a multi threaded application to update the status.
 *
 * @author  pgussow
 */
public class ProgressDialog extends JDialog
{
    /**
     * Holds the logger that is used.
     */
    private static final Logger LOG = Logger.getLogger(ProgressDialog.class);
    /**
     * Holds the busy icon animation.
     */
    private BusyLabel m_lBusy;
    /**
     * Holds the progress bar that is being displayed.
     */
    private JProgressBar m_pbProgressBar;
    /**
     * Holds the detailed text.
     */
    private String m_sText;
    /**
     * Holds the title for the dialog.
     */
    private String m_sTitle;
    /**
     * Holds the actual text that is being displayed.
     */
    private JTextPane m_taText;

    /**
     * Creates new form InProgressDialog.
     *
     * @param  fParent  The parent frame.
     * @param  bModal   Whether or not the dialog is a modal dialog.
     * @param  sTitle   The title for the dialog.
     * @param  sText    The text to display.
     * @param  iPBMax   Holds the max clicks.
     */
    public ProgressDialog(Frame fParent, boolean bModal, String sTitle, String sText, int iPBMax)
    {
        super(fParent, bModal);

        m_sTitle = sTitle;

        try
        {
            initComponents();

            m_pbProgressBar.setMaximum(iPBMax);

            setTitle(m_sTitle);
            setIconImage(ImageIO.read(getClass().getResource("/com/cordys/coe/util/swing/cordys.gif")));
            setDialogText(sText);

            setSize(500, 130);
            SwingUtils.centerDialog(this);
        }
        catch (IOException ex)
        {
            LOG.error("Error creating dialog", ex);
        }
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

            EventQueue.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        final ProgressDialog dialog = new ProgressDialog(new javax.swing.JFrame(),
                                                                         true, "Please wait",
                                                                         "Processing agent 1\nGenerating certificate",
                                                                         9);
                        dialog.addWindowListener(new java.awt.event.WindowAdapter()
                            {
                                public void windowClosing(java.awt.event.WindowEvent e)
                                {
                                    System.exit(0);
                                }
                            });

                        new Thread(new Runnable()
                            {
                                public void run()
                                {
                                    long lTotal = 0;

                                    while (!dialog.isMaxedOut())
                                    {
                                        try
                                        {
                                            Thread.sleep(1000);
                                            lTotal += 1000;

                                            dialog.increaseCounter();
                                            dialog.changeText("Still agent 1\nBut total sleep time was: " +
                                                              lTotal);

                                            if (dialog.isMaxedOut())
                                            {
                                                EventQueue.invokeLater(new Runnable()
                                                    {
                                                        public void run()
                                                        {
                                                            dialog.setVisible(false);
                                                        }
                                                    });
                                            }
                                        }
                                        catch (InterruptedException e)
                                        {
                                            LOG.error("Error sleeping", e);
                                        }
                                    }
                                }
                            }).start();
                        dialog.setVisible(true);
                    }
                });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * This method can be called to change the text displayed.
     *
     * @param  sNewText  The new text.
     */
    public void changeText(final String sNewText)
    {
        EventQueue.invokeLater(new Runnable()
            {
                public void run()
                {
                    m_pbProgressBar.setValue(m_pbProgressBar.getValue() + 1);
                    setDialogText(sNewText);
                }
            });
    }

    /**
     * This method closes the dialog.
     */
    public void closeDialog()
    {
        setVisible(false);
    }

    /**
     * This method gets the text that is currently being displayed.
     *
     * @return  The text that is currently being displayed.
     */
    public String getDialogText()
    {
        return m_sText;
    }

    /**
     * This method increases the counter.
     */
    public void increaseCounter()
    {
        EventQueue.invokeLater(new Runnable()
            {
                public void run()
                {
                    m_pbProgressBar.setValue(m_pbProgressBar.getValue() + 1);
                }
            });
    }

    /**
     * This method maxes out the progress bar.
     */
    public void maxCounter()
    {
        EventQueue.invokeLater(new Runnable()
            {
                public void run()
                {
                    m_pbProgressBar.setValue(m_pbProgressBar.getMaximum());
                }
            });
    }

    /**
     * This method fills the text area with the proper text and proper settings.
     *
     * @param  sText  The text to display.
     */
    public void setDialogText(String sText)
    {
        m_sText = sText;

        m_taText.setText(sText);

        // Set the proper settings for the font.
        StyledDocument doc = m_taText.getStyledDocument();

        SimpleAttributeSet set = new SimpleAttributeSet();
        StyleConstants.setAlignment(set, StyleConstants.ALIGN_CENTER);
        StyleConstants.setFontFamily(set, getFont().getFamily());
        StyleConstants.setFontSize(set, getFont().getSize());

        doc.setParagraphAttributes(0, Integer.MAX_VALUE, set, true);
    }

    /**
     * This method is called when the dialog is shown.
     *
     * @param  bShow  Whether or not the dialog needs to be shown.
     *
     * @see    java.awt.Dialog#setVisible(boolean)
     */
    @Override public void setVisible(boolean bShow)
    {
        if ((bShow == false))
        {
            // Stop the updater thread.
            m_lBusy.stopAnimation();
        }

        // Start the updater thread
        if ((bShow == true))
        {
            m_lBusy.startAnimation();
        }

        super.setVisible(bShow);
    }

    /**
     * This method returns whether or not the progress bar is completed yet.
     *
     * @return  true if the progress bar is finished. Otherwise false.
     */
    protected boolean isMaxedOut()
    {
        final NameValuePair nvp = new NameValuePair("return", "false");

        try
        {
            EventQueue.invokeAndWait(new Runnable()
                {
                    public void run()
                    {
                        if (m_pbProgressBar.getValue() == m_pbProgressBar.getMaximum())
                        {
                            nvp.setValue("true");
                        }
                    }
                });
        }
        catch (Exception e)
        {
            LOG.error("Error getting max value.", e);
        }

        return nvp.getValue().equals("true");
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    private void initComponents()
    {
        m_pbProgressBar = new javax.swing.JProgressBar();
        m_lBusy = new BusyLabel();

        JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        m_taText = new javax.swing.JTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        m_pbProgressBar.setName("m_pbProgressBar"); // NOI18N

        jScrollPane1.setBorder(null);
        jScrollPane1.setName("jScrollPane1"); // NOI18N

        m_taText.setBackground(java.awt.SystemColor.activeCaptionBorder);
        m_taText.setEditable(false);
        m_taText.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        m_taText.setName("m_taText"); // NOI18N
        jScrollPane1.setViewportView(m_taText);

        m_lBusy.setMaximumSize(new java.awt.Dimension(16, 16));
        m_lBusy.setMinimumSize(new java.awt.Dimension(16, 16));
        m_lBusy.setName("m_lBusy"); // NOI18N
        m_lBusy.setPreferredSize(new java.awt.Dimension(16, 16));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                  .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                            layout.createSequentialGroup().addContainerGap()
                                            .addGroup(layout.createParallelGroup(javax.swing
                                                                                 .GroupLayout
                                                                                 .Alignment.TRAILING)
                                                      .addGroup(javax.swing.GroupLayout.Alignment.LEADING,
                                                                layout.createSequentialGroup()
                                                                .addComponent(m_lBusy,
                                                                              javax.swing
                                                                              .GroupLayout.PREFERRED_SIZE,
                                                                              javax.swing
                                                                              .GroupLayout.DEFAULT_SIZE,
                                                                              javax.swing
                                                                              .GroupLayout.PREFERRED_SIZE)
                                                                .addGap(10, 10, 10).addComponent(jScrollPane1,
                                                                                                 javax
                                                                                                 .swing
                                                                                                 .GroupLayout.DEFAULT_SIZE,
                                                                                                 368,
                                                                                                 Short.MAX_VALUE))
                                                      .addComponent(m_pbProgressBar,
                                                                    javax.swing.GroupLayout
                                                                    .Alignment.LEADING,
                                                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                    394, Short.MAX_VALUE))
                                            .addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                          layout.createSequentialGroup().addContainerGap().addGroup(layout
                                                                                                    .createParallelGroup(javax
                                                                                                                         .swing
                                                                                                                         .GroupLayout
                                                                                                                         .Alignment.LEADING)
                                                                                                    .addComponent(m_lBusy,
                                                                                                                  javax
                                                                                                                  .swing
                                                                                                                  .GroupLayout.DEFAULT_SIZE,
                                                                                                                  48,
                                                                                                                  Short.MAX_VALUE)
                                                                                                    .addComponent(jScrollPane1,
                                                                                                                  javax
                                                                                                                  .swing
                                                                                                                  .GroupLayout.DEFAULT_SIZE,
                                                                                                                  48,
                                                                                                                  Short.MAX_VALUE))
                                          .addPreferredGap(javax.swing.LayoutStyle
                                                           .ComponentPlacement.RELATED)
                                          .addComponent(m_pbProgressBar,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                          .addContainerGap()));

        pack();
    }
}
