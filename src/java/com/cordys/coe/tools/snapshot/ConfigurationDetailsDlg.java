package com.cordys.coe.tools.snapshot;

import com.cordys.coe.tools.snapshot.config.Config;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import javax.swing.border.EmptyBorder;

import javax.xml.bind.JAXBContext;

/**
 * Dialog to show the details of the configuration.
 *
 * @author  localpg
 */
public class ConfigurationDetailsDlg extends JDialog
{
    /**
     * Holds teh content of the dialog.
     */
    private final JPanel contentPanel = new JPanel();

    /**
     * Creates a new ConfigurationDetailsDlg object.
     *
     * @param  owner    The owner frame.
     * @param  modal    Whether or not the dialog should be visible.
     * @param  config   The configuration to display.
     * @param  context  The JAXBContext to use.
     */
    public ConfigurationDetailsDlg(Frame owner, boolean modal, Config config, JAXBContext context)
    {
        super(owner, modal);
        setModalityType(ModalityType.APPLICATION_MODAL);
        setModal(true);
        setTitle("Configuration details");
        setBounds(100, 100, 920, 626);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout(0, 0));

        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);

            {
                JButton okButton = new JButton("OK");
                okButton.addActionListener(new ActionListener()
                    {
                        public void actionPerformed(ActionEvent e)
                        {
                            setVisible(false);
                        }
                    });
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
        }

        contentPanel.add(new ConfigurationDetails(config, context), BorderLayout.CENTER);
    }
}
