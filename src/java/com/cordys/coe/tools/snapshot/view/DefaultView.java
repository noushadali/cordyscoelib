package com.cordys.coe.tools.snapshot.view;

import com.cordys.coe.util.general.Util;

import java.awt.BorderLayout;
import java.awt.Font;

import java.io.ByteArrayOutputStream;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

/**
 * The default used to display the data for which no specific panel exists.
 *
 * @author  localpg
 */
public class DefaultView extends JPanel
{
    /**
     * Holds the XML of the data.
     */
    private JTextArea m_xml;

    /**
     * Create the panel.
     */
    public DefaultView()
    {
        setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, BorderLayout.CENTER);

        m_xml = new JTextArea();
        m_xml.setFont(new Font("Consolas", Font.PLAIN, 10));
        scrollPane.setViewportView(m_xml);
    }

    /**
     * Creates a new DefaultView object.
     *
     * @param  data     The data to marshal.
     * @param  context  The JAXB context.
     */
    public DefaultView(Object data, JAXBContext context)
    {
        this();

        if (data instanceof String)
        {
            m_xml.setText((String) data);
        }
        else
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            try
            {
                Marshaller m = context.createMarshaller();
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                m.marshal(data, baos);

                m_xml.setText(baos.toString());
            }
            catch (Exception e)
            {
                m_xml.setText(Util.getStackTrace(e));
            }
        }
    }
}
