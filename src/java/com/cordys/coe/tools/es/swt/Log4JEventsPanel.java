package com.cordys.coe.tools.es.swt;

import com.cordys.coe.tools.es.ILogEvent;
import com.cordys.coe.tools.es.Log4JLogEvent;
import com.cordys.coe.util.swt.BorderLayout;

import java.awt.Dimension;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * Panel to show Log4J events that are sent to this client.
 *
 * @author  pgussow
 */
public class Log4JEventsPanel extends AbstractEventPanel
{
    /**
     * Holds the log event frame.
     */
    private Composite cLogEventFrame;
    /**
     * Holds the navigational buttons.
     */
    private Composite cNavigation;
    /**
     * Holds all the sub-panels.
     */
    private HashMap<?, ?> hmSubCategories;
    /**
     * Holds the log content provider.
     */
    private ILogContentProvider m_lcpProvider;
    /**
     * Holds the filter panel.
     */
    private Log4JFilterComposite m_lfcFilter;
    /**
     * Table that holds all the events.
     */
    private Table tblEvents;
    /**
     * Holds the tabbed pane with the events.
     */
    private CTabFolder tpTabSubCategories;

    /**
     * Creates a new LogEventPanel object.
     *
     * @param  cParent    The parent composite.
     * @param  iStyle     The style for this composite.
     * @param  escClient  The parent EventServiceClient.
     */
    public Log4JEventsPanel(Composite cParent, int iStyle, IEventServiceClient escClient)
    {
        super(cParent, iStyle, escClient);
    }

    /**
     * Creates a new LogEventPanel object.
     *
     * @param  cParent      The parent composite.
     * @param  iStyle       The style for this composite.
     * @param  escClient    The parent EventServiceClient.
     * @param  lcpProvider  DOCUMENTME
     */
    public Log4JEventsPanel(Composite cParent, int iStyle, IEventServiceClient escClient,
                            ILogContentProvider lcpProvider)
    {
        super(cParent, iStyle, escClient);
        m_lcpProvider = lcpProvider;
        initializeProvider();

        try
        {
            lcpProvider.getNextDataset(this);
        }
        catch (IOException e)
        {
            // Ignore it.
        }
    }

    /**
     * This method clears the table with received events.
     *
     * @see  com.cordys.coe.tools.es.swt.AbstractEventPanel#clear()
     */
    @Override public void clear()
    {
        tblEvents.removeAll();

        // Also clear the sub-categories.
        for (Iterator<?> iPanels = hmSubCategories.values().iterator(); iPanels.hasNext();)
        {
            AbstractEventPanel aepPanel = (AbstractEventPanel) iPanels.next();
            aepPanel.clear();
        }
    }

    /**
     * This method adds a new row to the table with the details for this event.
     *
     * @param  leEvent  The event to add
     */
    public void addEventToTable(ILogEvent leEvent)
    {
        TableItem tiNew = new TableItem(tblEvents, SWT.NONE);
        tiNew.setData(leEvent);

        String sTemp = "";

        // Time
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat(m_lfcFilter.getDateFormat());
            sTemp = sdf.format(leEvent.getTime());
        }
        catch (Exception e)
        {
            sTemp = leEvent.getTime().toString();
        }
        tiNew.setText(0, sTemp);

        // Host
        tiNew.setText(1, leEvent.getHost());

        // PID
        tiNew.setText(2, leEvent.getPID());

        // Category
        tiNew.setText(3, leEvent.getCategory());

        // Thread
        tiNew.setText(4, leEvent.getThread());

        // Level
        tiNew.setText(5, leEvent.getTraceLevel());

        // Details
        tiNew.setText(6, leEvent.getMessage());
    }

    /**
     * This method handles the received message. It add's it to the table that shows all the
     * received messages.
     *
     * @param  leEvent  The LogEvent to add.
     */
    public void addLogEvent(ILogEvent leEvent)
    {
        try
        {
            boolean bDisplay = true;

            // Check if the event matches the filter
            bDisplay = m_lfcFilter.shouldDisplay((Log4JLogEvent) leEvent);

            // UI modifications should be done via the UI thread only.
            final boolean bTempDisplay = bDisplay;
            final ILogEvent leTemp = leEvent;

            getDisplay().asyncExec(new Runnable()
                {
                    public void run()
                    {
                        Display dTemp = getParent().getDisplay();

                        if ((dTemp != null) && !dTemp.isDisposed())
                        {
                            boolean bDisplay = bTempDisplay;

                            if (bDisplay == true)
                            {
                                Log4JEventsPanel.this.addEventToTable(leTemp);
                            }
                        }
                    }
                });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * This method handles the received message. It add's it to the table that shows all the
     * received messages.
     *
     * @param  leEvent  The received message.
     */
    @Override public void onReceive(ILogEvent leEvent)
    {
        try
        {
            addLogEvent(leEvent);
        }
        catch (Exception e)
        {
            e.printStackTrace();
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
        BufferedWriter bwOut = new BufferedWriter(new FileWriter(sFilename));

        try
        {
            for (Iterator<ILogEvent> iTemp = getEventIterator(); iTemp.hasNext();)
            {
                ILogEvent leEvent = (ILogEvent) iTemp.next();
                leEvent.writeToWriter(bwOut);
                bwOut.newLine();
            }
        }
        finally
        {
            bwOut.flush();
            bwOut.close();
        }
    }

    /**
     * This method sorts the entries in the table based on the passed on column.
     *
     * @param  tblEvents     The table to sort.
     * @param  tcSortColumn  The column to base the sorting on.
     * @param  bAscending    The direction for the sorting.
     */
    public void sortTable(Table tblEvents, TableColumn tcSortColumn, boolean bAscending)
    {
        // TODO: In order to do sorting we must convert the table to a table viewer.
        // getEventServiceClient().showInformation("Sorting column " +
        // tcSortColumn.getText() + " " +
        // (bAscending ? "ascending"
        // : "decending"));
        //
        // List lList = Arrays.asList(tblEvents.getItems());
        // Collections.sort(lList, new TableItemSorter(bAscending));
        //
        // //Now republic it to the table.
        // tblEvents.removeAll();
        // for (Iterator iTableItems = lList.iterator(); iTableItems.hasNext();)
        // {
        // TableItem tiTemp = (TableItem) iTableItems.next();
        // }
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
     * This method creates all the controls for the panel.
     */
    @Override protected void createContents()
    {
        cLogEventFrame = new Composite(this, SWT.NONE);
        cLogEventFrame.setLayout(new BorderLayout());

        initializeProvider();

        tpTabSubCategories = new CTabFolder(cLogEventFrame, SWT.NONE);
        tpTabSubCategories.setLayoutData(BorderLayout.CENTER);

        final CTabItem standardTabItem = new CTabItem(tpTabSubCategories, SWT.NONE);

        standardTabItem.setText("Standard");

        final Composite cStandard = new Composite(tpTabSubCategories, SWT.NONE);
        cStandard.setLayout(new FillLayout());
        standardTabItem.setControl(cStandard);

        final CTabItem tiFilter = new CTabItem(tpTabSubCategories, SWT.NONE);
        tiFilter.setText("Filter");

        m_lfcFilter = new Log4JFilterComposite(tpTabSubCategories, SWT.NONE,
                                               getEventServiceClient());
        m_lfcFilter.setLayout(new FillLayout());
        tiFilter.setControl(m_lfcFilter);

        tblEvents = new Table(cStandard, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
        tblEvents.setLinesVisible(true);
        tblEvents.setHeaderVisible(true);

        final TableColumn tcTime = new TableColumn(tblEvents, SWT.NONE);
        tcTime.setWidth(115);
        tcTime.setText("Time");

        tcTime.addSelectionListener(new Log4JEventsPanel.EventSorterSelectionListener(tblEvents,
                                                                                      tcTime));

        final TableColumn tcHost = new TableColumn(tblEvents, SWT.NONE);
        tcHost.setWidth(65);
        tcHost.setText("Host");

        final TableColumn tcPID = new TableColumn(tblEvents, SWT.NONE);
        tcPID.setWidth(55);
        tcPID.setText("PID");

        final TableColumn tcCategory = new TableColumn(tblEvents, SWT.NONE);
        tcCategory.setWidth(200);
        tcCategory.setText("Category");

        final TableColumn tcThread = new TableColumn(tblEvents, SWT.NONE);
        tcThread.setWidth(100);
        tcThread.setText("Thread");

        final TableColumn tcLevel = new TableColumn(tblEvents, SWT.NONE);
        tcLevel.setWidth(60);
        tcLevel.setText("Level");

        final TableColumn tcDetails = new TableColumn(tblEvents, SWT.NONE);
        tcDetails.setWidth(400);
        tcDetails.setText("Details");

        // Create the HashMap for the sub categories.
        hmSubCategories = new LinkedHashMap<Object, Object>();

        tpTabSubCategories.setSelection(standardTabItem);

        // Add the selection listener
        tblEvents.addSelectionListener(new EventsSelectionListener());
    }

    /**
     * This method returns an iterator for all the events in this panel.
     *
     * @return  An iterator for all the events in this panel.
     */
    protected Iterator<ILogEvent> getEventIterator()
    {
        return new TableEventIterator(tblEvents);
    }

    /**
     * This method initializes the contentprovider. It adds the next and previous buttons to the
     * panel.
     */
    private void initializeProvider()
    {
        // Create the composite that will hold the buttons.
        cNavigation = new Composite(cLogEventFrame, SWT.NONE);
        cNavigation.setLayoutData(BorderLayout.NORTH);
        cNavigation.setLayout(new BorderLayout());
        cNavigation.setData(BorderLayout.KEY_PREFERRED_SIZE, new Dimension(100, 20));

        Button btnNext = new Button(cNavigation, SWT.NONE);
        btnNext.setText("Next");
        btnNext.setData(BorderLayout.KEY_PREFERRED_SIZE, new Dimension(100, 20));
        btnNext.setLayoutData(BorderLayout.EAST);

        btnNext.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent seEvent)
                {
                    try
                    {
                        clear();

                        if (m_lcpProvider != null)
                        {
                            m_lcpProvider.getNextDataset(Log4JEventsPanel.this);
                        }
                    }
                    catch (IOException e)
                    {
                        // Ignore it.
                    }
                }
            });

        Button btnPrevious = new Button(cNavigation, SWT.NONE);
        btnPrevious.setText("Previous");
        btnPrevious.setLayoutData(BorderLayout.WEST);
        btnPrevious.setData(BorderLayout.KEY_PREFERRED_SIZE, new Dimension(100, 20));
        btnPrevious.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent seEvent)
                {
                    try
                    {
                        clear();

                        if (m_lcpProvider != null)
                        {
                            m_lcpProvider.getPreviousDataset(Log4JEventsPanel.this);
                        }
                    }
                    catch (IOException e)
                    {
                        // Ignore it.
                    }
                }
            });
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

            if ((oData != null) && (oData instanceof ILogEvent))
            {
                ILogEvent leTemp = (ILogEvent) oData;
                getEventServiceClient().setDetailedText(leTemp);
            }
        }
    }

    /**
     * The table comparator.
     *
     * @author  pgussow
     */
    protected static class TableItemSorter
        implements Comparator<Object>
    {
        /**
         * Holds the direction.
         */
        private boolean m_bAscending;

        /**
         * Constructor.
         *
         * @param  bAscending  The direction.
         */
        public TableItemSorter(boolean bAscending)
        {
            m_bAscending = bAscending;
        }

        /**
         * Compares the objects.
         *
         * @param   oFirst   The first object.
         * @param   oSecond  The second object.
         *
         * @return  A negative integer, zero, or a positive integer as the first argument is less
         *          than, equal to, or greater than the second.
         */
        public int compare(Object oFirst, Object oSecond)
        {
            int iReturn = 0;

            Object oData = ((TableItem) oFirst).getData();
            ILogEvent leEvent1 = (ILogEvent) oData;
            oData = ((TableItem) oSecond).getData();

            ILogEvent leEvent2 = (ILogEvent) oData;

            long lTime1 = leEvent1.getTime().getTime();
            long lTime2 = leEvent2.getTime().getTime();

            if (!m_bAscending)
            {
                long lTemp = lTime1;
                lTime1 = lTime2;
                lTime2 = lTemp;
            }

            if (lTime1 < lTime2)
            {
                iReturn = -1;
            }
            else if (lTime1 == lTime2)
            {
                iReturn = 0;
            }
            else if (lTime1 > lTime2)
            {
                iReturn = 1;
            }

            // if (o1 != null)
            return iReturn;
        }
    }

    /**
     * This class implements a table event iterator.
     *
     * @author  pgussow
     */
    private static class TableEventIterator
        implements Iterator<ILogEvent>
    {
        /**
         * Holds the current index.
         */
        private int m_iCurrent = 0;
        /**
         * Holds the event table.
         */
        private Table m_tblEvents;

        /**
         * Creates a new TableEventIterator object.
         *
         * @param  tblEvents  The table to iterate.
         */
        public TableEventIterator(Table tblEvents)
        {
            m_tblEvents = tblEvents;
        }

        /**
         * Returns whether or not there are more elements.
         *
         * @return  true if there are more elements.
         */
        public boolean hasNext()
        {
            return m_iCurrent < m_tblEvents.getItemCount();
        }

        /**
         * Returns the next object.
         *
         * @return  The next object.
         */
        public ILogEvent next()
        {
            ILogEvent oReturn = null;

            if (m_iCurrent < m_tblEvents.getItemCount())
            {
                TableItem tiTemp = m_tblEvents.getItem(m_iCurrent);
                Object oData = tiTemp.getData();

                if ((tiTemp != null) && (oData instanceof ILogEvent))
                {
                    oReturn = (ILogEvent) oData;
                }
                m_iCurrent++;
            }
            return oReturn;
        }

        /**
         * DOCUMENTME.
         */
        public void remove()
        {
        }
    }

    /**
     * This class is used to sort the events based on the column.
     *
     * @author  pgussow
     */
    private class EventSorterSelectionListener
        implements SelectionListener
    {
        /**
         * Holds the directoin for the sorting.
         */
        private boolean m_bAscending = true;
        /**
         * Holds the table to sort.
         */
        private Table m_tblEvents;
        /**
         * Holds the column to sort.
         */
        private TableColumn m_tcColumn;

        /**
         * Creates a new EventSorterSelectionListener object.
         *
         * @param  tblEvents  The table to sort.
         * @param  tcColumn   The column to sort.
         */
        public EventSorterSelectionListener(Table tblEvents, TableColumn tcColumn)
        {
            m_tblEvents = tblEvents;
            m_tcColumn = tcColumn;
        }

        /**
         * This method is called when the widget is selected.
         *
         * @param  seEvent  The event that occurred.
         */
        public void widgetDefaultSelected(SelectionEvent seEvent)
        {
            widgetSelected(seEvent);
        }

        /**
         * This method is called when the widget is selected. It will sort the table.
         *
         * @param  arg0  The event that occurred.
         */
        public void widgetSelected(SelectionEvent arg0)
        {
            sortTable(m_tblEvents, m_tcColumn, m_bAscending);
            m_bAscending = !m_bAscending;
        }
    }
}
