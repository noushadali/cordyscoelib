package com.cordys.coe.tools.snapshot.view;

import com.cordys.coe.tools.snapshot.data.handler.HostPIDInfo;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

/**
 * Displays the Host and PID information.
 *
 * @author  localpg
 */
public class HostPIDInfoView extends JPanel
{
    /**
     * Holds the host name.
     */
    private JTextField m_host;
    /**
     * Holds the process ID.
     */
    private JTextField m_pid;

    /**
     * Create the panel.
     */
    public HostPIDInfoView()
    {
        setLayout(new MigLayout("", "[][grow]", "[][]"));

        JLabel lblNewLabel = new JLabel("Hostname:");
        add(lblNewLabel, "cell 0 0,alignx trailing");

        m_host = new JTextField();
        m_host.setEditable(false);
        add(m_host, "cell 1 0,growx");
        m_host.setColumns(10);

        JLabel lblNewLabel_1 = new JLabel("PID:");
        add(lblNewLabel_1, "cell 0 1,alignx trailing");

        m_pid = new JTextField();
        m_pid.setEditable(false);
        add(m_pid, "cell 1 1,growx");
        m_pid.setColumns(10);
    }

    /**
     * Creates a new HostPIDInfoView object.
     *
     * @param  pidInfo  The process info.
     */
    public HostPIDInfoView(HostPIDInfo pidInfo)
    {
        this();

        if (pidInfo != null)
        {
            m_host.setText(pidInfo.getHost());
            m_pid.setText(String.valueOf(pidInfo.getProcessID()));
        }
    }
}
