package com.cordys.coe.tools.snapshot;

import com.cordys.coe.tools.snapshot.config.ServiceContainer;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

/**
 * This panel displays the details of the selected service container.
 * 
 * @author localpg
 */
public class ServiceContainerPanel extends JPanel
{
    /**
     * Holds the name of the service container.
     */
    private JTextField m_name;
    /**
     * Holds the organization of the service container.
     */
    private JTextField m_jmxURL;

    /**
     * Instantiates a new service container panel.
     * 
     * @param serviceContainer The service container to display
     * @param configurationDetails The configuration detail panel.
     */
    public ServiceContainerPanel(ServiceContainer serviceContainer, ConfigurationDetails configurationDetails)
    {
        setBorder(new TitledBorder(null, " Service Container ", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        setLayout(new MigLayout("", "[][grow]", "[][][][][][grow]"));

        // Name
        JLabel lblName = new JLabel("Name:");
        lblName.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lblName, "cell 0 0,alignx trailing");

        m_name = new JTextField();
        add(m_name, "cell 1 0,growx");
        m_name.setColumns(10);

        // Organization
        lblName = new JLabel("JMX URL:");
        lblName.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lblName, "cell 0 1,alignx trailing");

        m_jmxURL = new JTextField();
        add(m_jmxURL, "cell 1 1,growx");
        m_jmxURL.setColumns(10);

        // Bind the data.
        m_name.setText(serviceContainer.getName());
        m_name.addInputMethodListener(new BoundInputListener(configurationDetails, m_name, serviceContainer, "Name"));

        m_jmxURL.setText(serviceContainer.getJmxUrl());
        m_jmxURL.addInputMethodListener(new BoundInputListener(configurationDetails, m_jmxURL, serviceContainer, "JmxUrl"));
    }
}
