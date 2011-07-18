package com.cordys.coe.util.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;

/**
 * This class holds an anymated busy icon.
 *
 * @author  pgussow
 */
public class BusyLabel extends JLabel
{
    /**
     * Holds the animation rate to use.
     */
    private static final int ANIMATION_RATE = 50;
    /**
     * Holds all busy icons.
     */
    private final Icon[] m_aiBusyIcons = new Icon[15];
    /**
     * Holds the current busy index.
     */
    private int m_iBusyIconIndex = 0;
    /**
     * Holds the icon for when work is done.
     */
    private Icon m_iIdleIcon;
    /**
     * Holds the timer to use to animate the icon.
     */
    private Timer m_tBusyIconTimer;

    /**
     * Constructor.
     */
    public BusyLabel()
    {
        // Load all the icons
        for (int i = 0; i < m_aiBusyIcons.length; i++)
        {
            m_aiBusyIcons[i] = new ImageIcon(getClass().getResource("icon/busy-icon" + i + ".png"));
        }

        m_tBusyIconTimer = new Timer(ANIMATION_RATE, new ActionListener()
                                     {
                                         public void actionPerformed(ActionEvent e)
                                         {
                                             m_iBusyIconIndex = (m_iBusyIconIndex + 1) %
                                                                m_aiBusyIcons.length;
                                             setIcon(m_aiBusyIcons[m_iBusyIconIndex]);
                                         }
                                     });

        m_iIdleIcon = new ImageIcon(getClass().getResource("icon/idle-icon.png"));

        setText("");
        setIcon(m_iIdleIcon);
    }

    /**
     * This method starts the busy animation.
     */
    public void startAnimation()
    {
        if (!m_tBusyIconTimer.isRunning())
        {
            m_iBusyIconIndex = 0;
            m_tBusyIconTimer.start();
        }
    }

    /**
     * This method stops the animation.
     */
    public void stopAnimation()
    {
        if (m_tBusyIconTimer.isRunning())
        {
            m_tBusyIconTimer.stop();
        }
    }
}
