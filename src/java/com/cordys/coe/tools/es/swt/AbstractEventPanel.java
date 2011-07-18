package com.cordys.coe.tools.es.swt;

import com.cordys.coe.tools.es.ILogEvent;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * DOCUMENTME.
 *
 * @author  $author$
 */
public abstract class AbstractEventPanel extends Composite
{
    /**
     * Identifies the LogEvent subject.
     */
    private static final String LOG_EVENT = "LogEvent";
    /**
     * Identifies the UpdateLDAPCache subject.
     */
    private static final String UPDATE_LDAP_CACHE = "UpdateLDAPCache";
    /**
     * Indicates Log4J events received via sockets.
     */
    private static final String LOG4J_EVENTS = "Log4JEvents";
    /**
     * Holds the eventservice client class.
     */
    private IEventServiceClient escClient;

    /**
     * Creates a new AbstractEventPanel object.
     *
     * @param  cParent    The parent composite.
     * @param  iStyle     The style.
     * @param  escClient  The parent EventServiceClient.
     */
    public AbstractEventPanel(Composite cParent, int iStyle, IEventServiceClient escClient)
    {
        super(cParent, iStyle);
        this.setLayout(new FillLayout());

        this.escClient = escClient;

        addControlListener(new ControlAdapter()
            {
                public void controlResized(ControlEvent ceEvent)
                {
                    calculateNewSizes(ceEvent);
                }
            });

        createContents();
    }

    /**
     * This method is called when the EventServiceClient's Clear-button was pressed.
     */
    public abstract void clear();

    /**
     * This method is called when a message is received.
     *
     * @param  leEvent  The received message.
     */
    public abstract void onReceive(ILogEvent leEvent);

    /**
     * This method should save all entries in the panel to a file.
     *
     * @param   sFilename  The name of the file.
     *
     * @throws  IOException  In case of any exceptions.
     */
    public abstract void saveToFile(String sFilename)
                             throws IOException;

    /**
     * This method returns an instance of the panel that can display the events of the given
     * subject.
     *
     * @param   sSubject   The subject to get the instance for.
     * @param   escClient  The EventServiceClient in which it should run.
     * @param   cParent    The parent compisite.
     *
     * @return  The abstract event panel.
     */
    public static AbstractEventPanel getInstance(String sSubject, IEventServiceClient escClient,
                                                 Composite cParent)
    {
        AbstractEventPanel remReturn = null;

        if (LOG_EVENT.equals(sSubject))
        {
            remReturn = new LogEventPanel(cParent, SWT.NONE, escClient);
        }
        else if (UPDATE_LDAP_CACHE.equals(sSubject))
        {
            remReturn = new UpdateLDAPCachePanel(cParent, SWT.NONE, escClient);
        }
        else if (LOG4J_EVENTS.equals(sSubject))
        {
            remReturn = new Log4JEventsPanel(cParent, SWT.NONE, escClient);
        }
        else
        {
            remReturn = new DefaultEventsPanel(cParent, SWT.NONE, escClient);
        }

        return remReturn;
    }

    /**
     * This method returns an instance of the panel that can display the events of the given
     * subject.
     *
     * @param   sSubject     The subject to get the instance for.
     * @param   escClient    The EventServiceClient in which it should run.
     * @param   cParent      The parent compisite.
     * @param   lcpProvider  DOCUMENTME
     *
     * @return  The abstract event panel.
     */
    public static AbstractEventPanel getInstance(String sSubject, IEventServiceClient escClient,
                                                 Composite cParent, ILogContentProvider lcpProvider)
    {
        AbstractEventPanel remReturn = null;

        if (sSubject.indexOf(LOG4J_EVENTS) > -1)
        {
            remReturn = new Log4JEventsPanel(cParent, SWT.NONE, escClient, lcpProvider);
        }

        return remReturn;
    }

    /**
     * DOCUMENTME.
     */
    @Override public void dispose()
    {
        super.dispose();
    }

    /**
     * This method is called when the panel is resized.
     *
     * @param  ceEvent  The event that occurred.
     */
    protected abstract void calculateNewSizes(ControlEvent ceEvent);

    /**
     * This method should be used to create the controls for this composite.
     */
    protected abstract void createContents();

    /**
     * DOCUMENTME.
     */
    @Override protected void checkSubclass()
    {
    }

    /**
     * This method returns the EventServiceClient.
     *
     * @return  The EventServiceClient.
     */
    protected IEventServiceClient getEventServiceClient()
    {
        return escClient;
    }
}
