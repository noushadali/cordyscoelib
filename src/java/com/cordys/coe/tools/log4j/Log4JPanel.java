package com.cordys.coe.tools.log4j;

import com.cordys.coe.tools.es.ILogEvent;
import com.cordys.coe.tools.es.Log4JLogEvent;
import com.cordys.coe.tools.es.swt.AbstractEventPanel;
import com.cordys.coe.tools.log4j.filter.ILogMessageFilter;
import com.cordys.coe.tools.log4j.filter.NewLog4JFilterComposite;
import com.cordys.coe.util.swt.BorderLayout;
import com.cordys.coe.util.swt.MessageBoxUtil;

import java.awt.Dimension;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * This panel can receive and show the actual Log4J events.
 *
 * @author  pgussow
 */
public class Log4JPanel extends Composite
{
    /**
     * Identifies the time column.
     */
    private static final int COL_TIME = 0;
    /**
     * Identifies the host column.
     */
    private static final int COL_HOST = 1;
    /**
     * Identifies the PID column.
     */
    private static final int COL_PID = 2;
    /**
     * Identifies the category column.
     */
    private static final int COL_CATEGORY = 3;
    /**
     * Identifies the thread column.
     */
    private static final int COL_THREAD = 4;
    /**
     * Identifies the trace level column.
     */
    private static final int COL_TRACELEVEL = 5;
    /**
     * Identifies the message column.
     */
    private static final int COL_MESSAGE = 6;
    /**
     * Holds the navigation composite.
     */
    private Composite cNavigation;
    /**
     * Holds all the sub-panels.
     */
    private HashMap<String, AbstractEventPanel> hmSubCategories = new HashMap<String, AbstractEventPanel>();
    /**
     * Holds the composite to publish the events to.
     */
    private ILog4JComposite m_lcClient;
    /**
     * Holds the content provider for files.
     */
    private ILogContentProvider m_lcpProvider;
    /**
     * Holds the filter panel.
     */
    private ILogMessageFilter m_lfcFilter;
    /**
     * Holds the name of this panel.
     */
    private String m_sName;
    /**
     * Holds the table viewer.
     */
    private TableViewer m_tvViewer;
    /**
     * Holds the table with events.
     */
    private Table tblEvents;

    /**
     * Creates a new Log4JPanel object.
     *
     * @param  parent    The parent composite.
     * @param  style     The style.
     * @param  lcClient  The parent Log4JComposite.
     * @param  sName     The name of the panel.
     */
    public Log4JPanel(Composite parent, int style, ILog4JComposite lcClient, String sName)
    {
        super(parent, style);
        setLayout(new BorderLayout());
        m_lcClient = lcClient;
        m_sName = sName;

        cNavigation = new Composite(this, SWT.NONE);

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
                        if (m_lcpProvider != null)
                        {
                            clear();
                            m_lcpProvider.getNextDataset(Log4JPanel.this);
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
                        if (m_lcpProvider != null)
                        {
                            clear();

                            m_lcpProvider.getPreviousDataset(Log4JPanel.this);
                        }
                    }
                    catch (IOException e)
                    {
                        // Ignore it.
                    }
                }
            });

        final CTabFolder tbSubCategories = new CTabFolder(this, SWT.NONE);
        tbSubCategories.setLayoutData(BorderLayout.CENTER);

        final CTabItem tiEvents = new CTabItem(tbSubCategories, SWT.NONE);
        tiEvents.setText("Log events");

        final Composite cStandard = new Composite(tbSubCategories, SWT.NONE);
        cStandard.setLayout(new GridLayout());
        tiEvents.setControl(cStandard);

        final CTabItem tiFilter = new CTabItem(tbSubCategories, SWT.NONE);
        tiFilter.setText("Filter");

        NewLog4JFilterComposite lfcFilter = new NewLog4JFilterComposite(tbSubCategories, SWT.NONE,
                                                                        m_lcClient);
        tiFilter.setControl(lfcFilter);
        m_lfcFilter = lfcFilter;

        m_tvViewer = new TableViewer(cStandard, SWT.FULL_SELECTION | SWT.BORDER);
        m_tvViewer.setContentProvider(new ContentProvider());
        m_tvViewer.setLabelProvider(new TableLabelProvider());
        tblEvents = m_tvViewer.getTable();
        tblEvents.setLinesVisible(true);
        tblEvents.setHeaderVisible(true);
        tblEvents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final TableColumn tcTime = new TableColumn(tblEvents, SWT.NONE);
        tblEvents.setSortColumn(tcTime);
        tcTime.setWidth(115);
        tcTime.setText("Time");

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

        m_tvViewer.setInput(new Object());
        tblEvents.addSelectionListener(new EventsSelectionListener());

        tbSubCategories.setSelection(tiEvents);

        //
    }

    /**
     * Creates a new LogEventPanel object.
     *
     * @param  cParent      The parent composite.
     * @param  iStyle       The style for this composite.
     * @param  lcClient     The parent EventServiceClient.
     * @param  sName        The name of the panel.
     * @param  lcpProvider  The ILogContentProvider.
     */
    public Log4JPanel(Composite cParent, int iStyle, ILog4JComposite lcClient, String sName,
                      ILogContentProvider lcpProvider)
    {
        this(cParent, iStyle, lcClient, sName);
        m_lcpProvider = lcpProvider;

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
     * This method returns an instance of the panel that can display the events of the given
     * subject.
     *
     * @param   sName        The subject to get the instance for.
     * @param   lcClient     The EventServiceClient in which it should run.
     * @param   cParent      The parent compisite.
     * @param   lcpProvider  The content provider.
     *
     * @return  The abstract event panel.
     */
    public static Log4JPanel getInstance(String sName, ILog4JComposite lcClient, Composite cParent,
                                         ILogContentProvider lcpProvider)
    {
        Log4JPanel remReturn = null;

        if (lcpProvider != null)
        {
            remReturn = new Log4JPanel(cParent, SWT.NONE, lcClient, sName, lcpProvider);
        }
        else
        {
            remReturn = new Log4JPanel(cParent, SWT.NONE, lcClient, sName);
        }

        return remReturn;
    }

    /**
     * This method adds a new row to the table with the details for this event.
     *
     * @param  leEvent  The event to add
     */
    public void addEventToTable(ILogEvent leEvent)
    {
        m_tvViewer.add(leEvent);
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
                                Log4JPanel.this.addEventToTable(leTemp);
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
     * This method clears the table with received events.
     *
     * @see  com.cordys.coe.tools.es.swt.AbstractEventPanel#clear()
     */
    public void clear()
    {
        tblEvents.removeAll();

        // Also clear the sub-categories.
        for (Iterator<AbstractEventPanel> iPanels = hmSubCategories.values().iterator();
                 iPanels.hasNext();)
        {
            AbstractEventPanel aepPanel = iPanels.next();
            aepPanel.clear();
        }
    }

    /**
     * DOCUMENTME.
     */
    @Override public void dispose()
    {
        super.dispose();
    }

    /**
     * This method gets the name for this panel.
     *
     * @return  The name for this panel.
     */
    public String getPanelName()
    {
        return m_sName;
    }

    /**
     * This method handles the received message. It add's it to the table that shows all the
     * received messages.
     *
     * @param  lleEvent  The received message.
     */
    public void receive(Log4JLogEvent lleEvent)
    {
        try
        {
            addLogEvent(lleEvent);
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
    public void saveToFile(String sFilename)
                    throws IOException
    {
        BufferedWriter bwOut = new BufferedWriter(new FileWriter(sFilename));

        try
        {
            for (Iterator<ILogEvent> iTemp = getEventIterator(); iTemp.hasNext();)
            {
                ILogEvent leEvent = (ILogEvent) iTemp.next();

                try
                {
                    leEvent.writeToWriter(bwOut);
                    bwOut.newLine();
                }
                catch (Exception e)
                {
                    MessageBoxUtil.showError("Error writing log event: " + leEvent.getMessage(), e);
                }
            }
        }
        finally
        {
            bwOut.flush();
            bwOut.close();
        }
    }

    /**
     * DOCUMENTME.
     */
    @Override protected void checkSubclass()
    {
        // Disable the check that prevents subclassing of SWT components
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
                String sToBeDisplayed = leTemp.getMessage();

                // Get the appropiate message to display
                try
                {
                    sToBeDisplayed = Util.formatXmlMessage(sToBeDisplayed);
                }
                catch (Exception e)
                {
                    // Ignore it.
                }

                m_lcClient.setDetailedText(leTemp);
            }
        }
    }

    /**
     * The content provider for the table viewer.
     *
     * @author  pgussow
     */
    class ContentProvider
        implements IStructuredContentProvider
    {
        /**
         * Is called when the content can be disposed.
         */
        public void dispose()
        {
        }

        /**
         * By default there are no items in the provider. They are only dynamically added.
         *
         * @param   oInputElement  The input element.
         *
         * @return  The elements for this object.
         */
        public Object[] getElements(Object oInputElement)
        {
            return new Object[0];
        }

        /**
         * This method is called when the input object changes.
         *
         * @param  tvViewer   The current viewer.
         * @param  oOldInput  The old object.
         * @param  oNewInput  The new object.
         */
        public void inputChanged(Viewer tvViewer, Object oOldInput, Object oNewInput)
        {
        }
    }

    /**
     * This class is the content provider for the table viewer.
     *
     * @author  $author$
     */
    class TableLabelProvider extends LabelProvider
        implements ITableLabelProvider, ITableColorProvider, DisposeListener
    {
        /**
         * Holds the color red.
         */
        private Color m_cRed = new Color(null, new RGB(200, 0, 0));
        /**
         * Holds the color white.
         */
        private Color m_cWhite = new Color(null, new RGB(255, 255, 255));
        /**
         * Holds the color white.
         */
        private Color m_cYellow = new Color(null, new RGB(255, 255, 0));

        /**
         * Creates a new TableLabelProvider object.
         */
        public TableLabelProvider()
        {
            addDisposeListener(this);
        }

        /**
         * This method returns the background color for the column.
         *
         * @param   oInput        The input object.
         * @param   iColumnIndex  The column index.
         *
         * @return  The background color.
         *
         * @see     org.eclipse.jface.viewers.ITableColorProvider#getBackground(java.lang.Object,
         *          int)
         */
        public Color getBackground(Object oInput, int iColumnIndex)
        {
            Color cReturn = null;

            if (oInput instanceof ILogEvent)
            {
                ILogEvent leEvent = (ILogEvent) oInput;

                if ((iColumnIndex == COL_TRACELEVEL) &&
                        ("ERROR".equalsIgnoreCase(leEvent.getTraceLevel()) ||
                             "FATAL".equalsIgnoreCase(leEvent.getTraceLevel())))
                {
                    cReturn = m_cRed;
                }
                else if ((iColumnIndex == COL_TRACELEVEL) &&
                             ("WARN".equalsIgnoreCase(leEvent.getTraceLevel()) ||
                                  "WARNING".equalsIgnoreCase(leEvent.getTraceLevel())))
                {
                    cReturn = m_cYellow;
                }
            }

            return cReturn;
        }

        /**
         * This method returns the icon for the row. No images are used.
         *
         * @param   oInput        The input element.
         * @param   iColumnIndex  The column index.
         *
         * @return  The column image. Always null.
         */
        public Image getColumnImage(Object oInput, int iColumnIndex)
        {
            return null;
        }

        /**
         * This method returns the text that should be shown for the column.
         *
         * @param   oInput        The input element.
         * @param   iColumnIndex  The column index.
         *
         * @return  The string to display for this entry.
         */
        public String getColumnText(Object oInput, int iColumnIndex)
        {
            String sReturn = "";

            if (oInput instanceof ILogEvent)
            {
                ILogEvent leEvent = (ILogEvent) oInput;

                switch (iColumnIndex)
                {
                    case COL_TIME:

                        // Time
                        try
                        {
                            SimpleDateFormat sdf = new SimpleDateFormat(m_lfcFilter
                                                                        .getDateFormat());
                            sReturn = sdf.format(leEvent.getTime());
                        }
                        catch (Exception e)
                        {
                            sReturn = leEvent.getTime().toString();
                        }
                        break;

                    case COL_HOST:
                        sReturn = leEvent.getHost();
                        break;

                    case COL_PID:
                        sReturn = leEvent.getPID();
                        break;

                    case COL_CATEGORY:
                        sReturn = leEvent.getCategory();
                        break;

                    case COL_THREAD:
                        sReturn = leEvent.getThread();
                        break;

                    case COL_TRACELEVEL:
                        sReturn = leEvent.getTraceLevel();
                        break;

                    case COL_MESSAGE:
                        sReturn = leEvent.getMessage();
                        break;

                    default:
                        sReturn = "Invalid column";
                }
            }

            return sReturn;
        }

        /**
         * This method returns the foreground color.
         *
         * @param   oInput        The input element.
         * @param   iColumnIndex  The column index.
         *
         * @return  Always null.
         *
         * @see     org.eclipse.jface.viewers.ITableColorProvider#getForeground(java.lang.Object,
         *          int)
         */
        public Color getForeground(Object oInput, int iColumnIndex)
        {
            Color cReturn = null;

            if (oInput instanceof ILogEvent)
            {
                ILogEvent leEvent = (ILogEvent) oInput;

                if ((iColumnIndex == COL_TRACELEVEL) &&
                        ("ERROR".equalsIgnoreCase(leEvent.getTraceLevel()) ||
                             "FATAL".equalsIgnoreCase(leEvent.getTraceLevel())))
                {
                    cReturn = m_cWhite;
                }
            }

            return cReturn;
        }

        /**
         * DOCUMENTME.
         *
         * @param  arg0  DOCUMENTME
         */
        public void widgetDisposed(DisposeEvent arg0)
        {
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
}
