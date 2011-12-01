package com.cordys.coe.tools.snapshot;

import com.cordys.coe.tools.snapshot.config.ServiceGroup;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

/**
 * This panel displays the details of the selected service group.
 * 
 * @author localpg
 */
public class ServiceGroupPanel extends JPanel
{
    /**
     * Holds the name of the service group.
     */
    private JTextField m_name;
    /**
     * Holds the organization of the service group.
     */
    private JTextField m_organization;

    /**
     * Instantiates a new service group panel.
     * 
     * @param serviceGroup The service group to display
     * @param configurationDetails The configuration detail panel.
     */
    public ServiceGroupPanel(ServiceGroup serviceGroup, ConfigurationDetails configurationDetails)
    {
        setBorder(new TitledBorder(null, " Service Group ", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        setLayout(new MigLayout("", "[][grow]", "[][][][][][grow]"));

        // Name
        JLabel lblName = new JLabel("Name:");
        lblName.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lblName, "cell 0 0,alignx trailing");

        m_name = new JTextField();
        add(m_name, "cell 1 0,growx");
        m_name.setColumns(10);

        // Organization
        lblName = new JLabel("Organization:");
        lblName.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lblName, "cell 0 1,alignx trailing");

        m_organization = new JTextField();
        add(m_organization, "cell 1 1,growx");
        m_organization.setColumns(10);

        // Bind the data.
        m_name.setText(serviceGroup.getName());
        m_name.addInputMethodListener(new BoundInputListener(configurationDetails, m_name, serviceGroup, "Name"));

        m_organization.setText(serviceGroup.getOrganization());
        m_organization.addInputMethodListener(new BoundInputListener(configurationDetails, m_organization, serviceGroup,
                "Organization"));
    }
}
