package com.cordys.coe.tools.snapshot.view;

import com.cordys.coe.tools.snapshot.config.JMXCounter;
import com.cordys.coe.tools.snapshot.data.handler.HostPIDInfo;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import javax.swing.border.TitledBorder;

import javax.xml.bind.JAXBContext;

/**
 * This factory creates the composite that should be used to show the details of the selected tree item.
 *
 * @author  localpg
 */
public class ViewDataFactory
{
    /**
     * This method creates the composite to display the data object.
     *
     * @param   data     The data to display.
     * @param   counter  The JMX counter for which the data is displayed.
     * @param   context  The jaxb context.
     *
     * @return  The created
     */
    public static JPanel createComponent(Object data, JMXCounter counter, JAXBContext context)
    {
        JPanel retVal = new JPanel();
        retVal.setLayout(new BorderLayout());
        retVal.setBorder(new TitledBorder(" " + counter.toString() + " "));

        // Create the specific details.
        if (data instanceof HostPIDInfo)
        {
            HostPIDInfo pidInfo = (HostPIDInfo) data;

            retVal.add(new HostPIDInfoView(pidInfo), BorderLayout.CENTER);
        }
        else
        {
            retVal.add(new DefaultView(data, context), BorderLayout.CENTER);
        }

        return retVal;
    }
}
