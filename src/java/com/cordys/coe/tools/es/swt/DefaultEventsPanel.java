package com.cordys.coe.tools.es.swt;

import com.cordys.coe.tools.es.ILogEvent;
import com.cordys.coe.tools.es.ReceivedMessage;
import com.cordys.coe.tools.log4j.Util;

import java.io.BufferedWriter;
import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.Date;

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
 * This is the default panel for events.
 *
 * @author  $author$
 */
public class DefaultEventsPanel extends AbstractEventPanel
{
    /**
     * Holds all events for this subject.
     */
    private Table tblEvents;

    /**
     * Creates a new DefaultEventsPanel object.
     *
     * @param  cParent    The parent composite.
     * @param  iStyle     The style for this composite.
     * @param  escClient  The parent EventServiceClient.
     */
    public DefaultEventsPanel(Composite cParent, int iStyle, IEventServiceClient escClient)
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
            TableItem tiTemp = tblEvents.getItem(iCount);
            Object oData = tiTemp.getData();

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
    @Override public void onReceive(final ILogEvent leEvent)
    {
        Display dTemp = getDisplay();

        if ((dTemp != null) && !dTemp.isDisposed())
        {
            dTemp.asyncExec(new Runnable()
                {
                    public void run()
                    {
                        TableItem tiNew = new TableItem(tblEvents, SWT.NONE);

                        String[] saText = new String[3];

                        SimpleDateFormat sdf = new SimpleDateFormat(getEventServiceClient()
                                                                    .getConfiguration()
                                                                    .getDateFormat());
                        saText[0] = sdf.format(leEvent.getTime());
                        saText[1] = leEvent.getCategory();
                        saText[2] = leEvent.getMessage();

                        tiNew.setText(saText);
                        tiNew.setData(leEvent);
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
     * This method is called when the panel is resized.
     *
     * @param  ceEvent  The event that occurred.
     */
    @Override protected void calculateNewSizes(ControlEvent ceEvent)
    {
    }

    /**
     * This method creates the controls for this panel.
     */
    @Override protected void createContents()
    {
        tblEvents = new Table(this, SWT.BORDER);
        tblEvents.setLinesVisible(true);
        tblEvents.setHeaderVisible(true);

        final TableColumn tcTime = new TableColumn(tblEvents, SWT.NONE);
        tcTime.setWidth(80);
        tcTime.setText("Time");

        final TableColumn tcCategory = new TableColumn(tblEvents, SWT.NONE);
        tcCategory.setWidth(80);
        tcCategory.setText("Category");

        final TableColumn tcDetails = new TableColumn(tblEvents, SWT.NONE);
        tcDetails.setWidth(400);
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

            if ((oData != null) && (oData instanceof ReceivedMessage))
            {
                ReceivedMessage rmMessage = (ReceivedMessage) oData;
                DummyLogEvent dle = new DummyLogEvent(rmMessage.getDataString());

                getEventServiceClient().setDetailedText(dle);
            }
        }
    }

    /**
     * A dummy log event.
     *
     * @author  pgussow
     */
    private class DummyLogEvent
        implements ILogEvent
    {
        /**
         * DOCUMENTME.
         */
        private Date m_dDate = new Date();
        /**
         * DOCUMENTME.
         */
        private String m_sMessage;

        /**
         * Creates a new DummyLogEvent object.
         *
         * @param  sMessage  DOCUMENTME
         */
        public DummyLogEvent(String sMessage)
        {
            m_sMessage = sMessage;
        }

        /**
         * @see  com.cordys.coe.tools.es.ILogEvent#getCategory()
         */
        public String getCategory()
        {
            return "";
        }

        /**
         * @see  com.cordys.coe.tools.es.ILogEvent#getConnectors()
         */
        public String[] getConnectors()
        {
            return new String[0];
        }

        /**
         * This method returns the formatted message. If this message contains a piece of XML it is
         * formatted accordingly.
         *
         * @return  The formatted message.
         */
        public String getFormattedMessage()
        {
            String sReturn = "";

            try
            {
                sReturn = Util.formatXmlMessage(m_sMessage);
            }
            catch (Exception e)
            {
                // Ignore it.
            }
            return sReturn;
        }

        /**
         * @see  com.cordys.coe.tools.es.ILogEvent#getHost()
         */
        public String getHost()
        {
            return "";
        }

        /**
         * @see  com.cordys.coe.tools.es.ILogEvent#getMessage()
         */
        public String getMessage()
        {
            return m_sMessage;
        }

        /**
         * @see  com.cordys.coe.tools.es.ILogEvent#getNDC()
         */
        public String getNDC()
        {
            return "";
        }

        /**
         * @see  com.cordys.coe.tools.es.ILogEvent#getPID()
         */
        public String getPID()
        {
            return "";
        }

        /**
         * @see  com.cordys.coe.tools.es.ILogEvent#getThread()
         */
        public String getThread()
        {
            return "";
        }

        /**
         * @see  com.cordys.coe.tools.es.ILogEvent#getTime()
         */
        public Date getTime()
        {
            return m_dDate;
        }

        /**
         * @see  com.cordys.coe.tools.es.ILogEvent#getTraceLevel()
         */
        public String getTraceLevel()
        {
            return "";
        }

        /**
         * @see  com.cordys.coe.tools.es.ILogEvent#writeToWriter(java.io.BufferedWriter)
         */
        public void writeToWriter(BufferedWriter bwOut)
                           throws IOException
        {
        }
    }
}
