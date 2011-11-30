package com.cordys.coe.tools.es.swt;

import com.cordys.coe.exception.GeneralException;
import com.cordys.coe.tools.es.ESLogEvent;
import com.cordys.coe.tools.es.ILogEvent;
import com.cordys.coe.tools.es.ReceivedMessage;

import com.eibus.xml.nom.Find;
import com.eibus.xml.nom.Node;

import java.io.IOException;

import java.text.SimpleDateFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * This panel is specific for showing the UpdateLdapCache events.
 *
 * @author  pgussow
 */
@SuppressWarnings("deprecation")
public class UpdateLDAPCachePanel extends AbstractEventPanel
{
    /**
     * Table for all the received events.
     */
    private Table tblEvents;

    /**
     * Constructor.
     *
     * @param  cParent    The parent composite.
     * @param  iStyle     The style for this composite.
     * @param  escClient  The parent EventServiceClient.
     */
    public UpdateLDAPCachePanel(Composite cParent, int iStyle, IEventServiceClient escClient)
    {
        super(cParent, iStyle, escClient);
    }

    /**
     * This method clears the table with received events.
     *
     * @see  com.cordys.coe.tools.es.swt.AbstractEventPanel#clear()
     */
    @Override public void clear()
    {
        for (int iCount = 0; iCount < tblEvents.getItemCount(); iCount++)
        {
            Object oData = tblEvents.getItem(iCount).getData();

            if (oData instanceof ReceivedMessage)
            {
                ReceivedMessage rmReceived = (ReceivedMessage) oData;
                rmReceived.cleanUp();
            }
        }

        tblEvents.removeAll();
    }

    /**
     * This method handles the received message. It add's it to the table that shows all the
     * received messages.
     *
     * @param  leEvent  The received message.
     *
     * @see    com.cordys.coe.tools.es.swt.AbstractEventPanel#onReceive(com.cordys.coe.tools.es.ILogEvent)
     */
    @Override public void onReceive(ILogEvent leEvent)
    {
        if (leEvent instanceof ESLogEvent)
        {
            try
            {
                UpdateLDAPCacheLogEvent ulcleEvent = new UpdateLDAPCacheLogEvent(Node.duplicate(((ESLogEvent)
                                                                                                     leEvent)
                                                                                                .getConfigXML()));
                leEvent = ulcleEvent;
            }
            catch (GeneralException e)
            {
                // Ignore it.
            }
        }

        final ILogEvent leRealEvent = leEvent;

        Display dTemp = getDisplay();

        if ((dTemp != null) && !dTemp.isDisposed())
        {
            dTemp.asyncExec(new Runnable()
                {
                    public void run()
                    {
                        TableItem tiNew = new TableItem(tblEvents, SWT.NONE);
                        tiNew.setData(leRealEvent);

                        SimpleDateFormat sdf = new SimpleDateFormat(getEventServiceClient()
                                                                    .getConfiguration()
                                                                    .getDateFormat());
                        tiNew.setText(0, sdf.format(leRealEvent.getTime()));
                        tiNew.setText(1, leRealEvent.getCategory());
                        tiNew.setText(2, leRealEvent.getMessage());
                    }
                });
        }
    }

    /**
     * This method should save all entries in the panel to a file.
     *
     * @param   sFilename  The name of the file.
     *
     * @throws  IOException  In case of any exceptions.
     */
    @Override public void saveToFile(String sFilename)
                              throws IOException
    {
        throw new IOException("Save not supported.");
    }

    /**
     * Adapter method for doing something with the sizing when the composite is resized.
     *
     * @param  ceEvent  The event that occurred.
     */
    @Override protected void calculateNewSizes(ControlEvent ceEvent)
    {
    }

    /**
     * This method creates the needed controls for this panel.
     */
    @Override protected void createContents()
    {
        tblEvents = new Table(this, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
        tblEvents.setLinesVisible(true);
        tblEvents.setHeaderVisible(true);

        final TableColumn tcTime = new TableColumn(tblEvents, SWT.NONE);
        tcTime.setWidth(75);
        tcTime.setText("Time");

        final TableColumn tcCategory = new TableColumn(tblEvents, SWT.NONE);
        tcCategory.setWidth(85);
        tcCategory.setText("Category");

        final TableColumn tcDetails = new TableColumn(tblEvents, SWT.NONE);
        tcDetails.setWidth(300);
        tcDetails.setText("Details");

        tblEvents.addSelectionListener(new EventsSelectionListener());
    }

    /**
     * Class to handle the selection changes on the Events-table so that the details can be
     * displayed in the details-page.
     *
     * @author  pgussow
     */
    public class EventsSelectionListener
        implements SelectionListener
    {
        /**
         * This method shows the data of the event in the details-page.
         *
         * @param  seEvent  The event that occured.
         */
        public void widgetDefaultSelected(SelectionEvent seEvent)
        {
            widgetSelected(seEvent);
        }

        /**
         * This method shows the data of the event in the details-page.
         *
         * @param  seEvent  The event that occured.
         */
        public void widgetSelected(SelectionEvent seEvent)
        {
            TableItem tiItem = (TableItem) seEvent.item;
            Object oData = tiItem.getData();

            if ((oData != null) && (oData instanceof UpdateLDAPCacheLogEvent))
            {
                UpdateLDAPCacheLogEvent rmMessage = (UpdateLDAPCacheLogEvent) oData;
                getEventServiceClient().setDetailedText(rmMessage);
            }
        }
    }

    /**
     * This class wraps around the Update LDAP cache event.
     *
     * @author  pgussow
     */
    private class UpdateLDAPCacheLogEvent extends ESLogEvent
    {
        /**
         * Holds the DN's.
         */
        private String[] m_asDNs;
        /**
         * Holds the message.
         */
        private String m_sMessage;

        /**
         * Constructor.
         *
         * @param   iNode  The XML node containing the message.
         *
         * @throws  GeneralException
         */
        public UpdateLDAPCacheLogEvent(int iNode)
                                throws GeneralException
        {
            super(iNode);
            m_sMessage = Node.writeToString(iNode, false);

            int[] aiDNs = Find.match(iNode, "?<dn>");
            m_asDNs = new String[aiDNs.length];

            for (int iCount = 0; iCount < aiDNs.length; iCount++)
            {
                m_asDNs[iCount] = Node.getDataWithDefault(aiDNs[iCount], "");
            }
        }

        /**
         * This method returns the category.
         *
         * @return  The category.
         *
         * @see     com.cordys.coe.tools.es.ESLogEvent#getCategory()
         */
        @Override public String getCategory()
        {
            return "UpdateLDAPCache";
        }

        /**
         * This method returns a string [] containing all the connectors.
         *
         * @return  A string [] containing all the connectors.
         *
         * @see     com.cordys.coe.tools.es.ESLogEvent#getConnectors()
         */
        @Override public String[] getConnectors()
        {
            return m_asDNs;
        }

        /**
         * This method returns the actual message.
         *
         * @return  The actual message.
         *
         * @see     com.cordys.coe.tools.es.ESLogEvent#getMessage()
         */
        @Override public String getMessage()
        {
            return m_sMessage;
        }
    }
}
