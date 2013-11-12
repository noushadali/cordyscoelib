package com.cordys.coe.tools.snapshot;

import com.cordys.coe.tools.snapshot.config.Config;
import com.cordys.coe.util.swing.MessageBoxUtil;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import java.io.File;
import java.io.FileOutputStream;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import javax.swing.border.EmptyBorder;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

/**
 * Dialog to show the details of the configuration.
 * 
 * @author localpg
 */
public class ConfigurationDetailsDlg extends JDialog
{
    /**
     * Holds teh content of the dialog.
     */
    private final JPanel contentPanel = new JPanel();
    /**
     * Holds the name of the config file that was loaded.
     */
    private String m_configFile;
    /**
     * Holds the configuration details panel.
     */
    private ConfigurationDetails m_configDetails;

    /**
     * Creates a new ConfigurationDetailsDlg object.
     * 
     * @param owner The owner frame.
     * @param modal Whether or not the dialog should be visible.
     * @param config The configuration to display.
     * @param context The JAXBContext to use.
     * @param configFile The name of the configuration file used.
     * @param isNew Whether or not it is a new configuration.
     */
    public ConfigurationDetailsDlg(final Frame owner, final SystemSnapshot sg, boolean modal, Config config, final JAXBContext context, String configFile,
            final boolean isNew)
    {
        super(owner, modal);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e)
            {
                if ((m_configDetails != null) && m_configDetails.isDirty() || isNew == true)
                {
                    String message = "The configuration has changed. Do you want to save the changes ";

                    if (m_configFile != null)
                    {
                        message += " to file " + m_configFile + "?";
                    }
                    else
                    {
                        message += " to a new file?";
                    }

                    if (MessageBoxUtil.showConfirmation(message))
                    {
                        try
                        {
                            FileOutputStream fos = null;

                            if (m_configFile == null || isNew == true)
                            {
                                JFileChooser fc = new JFileChooser();

                                fc.setSelectedFile(new File("configuration.xml"));

                                if (fc.showDialog(owner, "Save") == JFileChooser.APPROVE_OPTION)
                                {
                                    File selectedFile = fc.getSelectedFile();
                                    m_configFile = selectedFile.getCanonicalPath();
                                    fos = new FileOutputStream(selectedFile);
                                }
                            }
                            else
                            {
                                fos = new FileOutputStream(new File(m_configFile));
                            }

                            // Now we can write the config
                            Marshaller m = context.createMarshaller();
                            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

                            m.marshal(m_configDetails.getConfig(), fos);

                            fos.close();
                            
                            sg.loadConfigurationFile(getConfigFile());
                        }
                        catch (Exception exception)
                        {
                            MessageBoxUtil.showError("Error saving file " + m_configFile, exception);
                        }
                    }
                }
            }
        });
        m_configFile = configFile;
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
                okButton.addActionListener(new ActionListener() {
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

        m_configDetails = new ConfigurationDetails(config, context);
        contentPanel.add(m_configDetails, BorderLayout.CENTER);
    }

    /**
     * This method gets the config file.
     * 
     * @return The config file
     */
    private File getConfigFile()
    {
        return new File(m_configFile);
    }
}
