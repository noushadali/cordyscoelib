package com.cordys.coe.tools.jmx;

import java.io.Serializable;

import java.util.HashSet;

import javax.management.Notification;
import javax.management.NotificationFilter;

/**
 * This class holds the filter.
 *
 * @author  pgussow
 */
public class JMXNotificationFilter
    implements NotificationFilter, Serializable
{
    /**
     * Holds the subscribed.
     */
    private HashSet<String> m_hsSubscribed = new HashSet<String>();

    /**
     * Creates a new JMXNotificationFilter object.
     */
    public JMXNotificationFilter()
    {
    }

    /**
     * This method returns if the incoming notification has to be shown.
     *
     * @param   nNotification  The notification that occurred.
     *
     * @return  true if the notification should be shown. Otherwise false.
     */
    public boolean isNotificationEnabled(Notification nNotification)
    {
        boolean bReturn = true;

        String sType = nNotification.getType();

        if (!m_hsSubscribed.contains(sType))
        {
            bReturn = false;
        }

        return bReturn;
    }

    /**
     * This method subscribes to the notification type.
     *
     * @param  sType  The notification type.
     */
    public void subscribe(String sType)
    {
        if (!m_hsSubscribed.contains(sType))
        {
            m_hsSubscribed.add(sType);
        }
    }

    /**
     * This method unsubscribes the notification type.
     *
     * @param  sType  The notification type.
     */
    public void unsubscribe(String sType)
    {
        if (m_hsSubscribed.contains(sType))
        {
            m_hsSubscribed.remove(sType);
        }
    }
}
