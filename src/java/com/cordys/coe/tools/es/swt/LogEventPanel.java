package com.cordys.coe.tools.es.swt;

import com.cordys.coe.tools.es.ILogEvent;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * This panel shows the standard log events.
 *
 * @author  pgussow
 */
public class LogEventPanel extends AbstractEventPanel
{
    /**
     * Identifies the orchestrator subclass.
     */
    public static final String ORC_INDENTIFIER = "ORC_";
    /**
     * The edit that holds the PID to filter on.
     */
    private Text ePID;
    /**
     * Holds all the sub-panels.
     */
    private HashMap<String, AbstractEventPanel> hmSubCategories;
    /**
     * The splitter for the filter and the table.
     */
    private SashForm sfSplitter;
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
    public LogEventPanel(Composite cParent, int iStyle, IEventServiceClient escClient)
    {
        super(cParent, iStyle, escClient);
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
            SimpleDateFormat sdf = new SimpleDateFormat(getEventServiceClient().getConfiguration()
                                                        .getDateFormat());
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

        // Connectors
        StringBuffer sbReturn = new StringBuffer();
        String[] saConnectors = leEvent.getConnectors();

        for (int iCount = 0; iCount < saConnectors.length; iCount++)
        {
            sbReturn.append(saConnectors[iCount]);

            if (iCount < (saConnectors.length - 1))
            {
                sbReturn.append("/");
            }
        }
        sTemp = sbReturn.toString();
        tiNew.setText(4, sTemp);

        // Thread
        tiNew.setText(5, leEvent.getThread());

        // Level
        tiNew.setText(6, leEvent.getTraceLevel());

        // Details
        tiNew.setText(7, leEvent.getMessage());
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

            if (leEvent.getCategory().startsWith(ORC_INDENTIFIER))
            {
                // This is an orchestrator logevent. Put it in the seperate tab.
                OrchestratorEventPanel oepOrchestrator = (OrchestratorEventPanel)
                                                             hmSubCategories.get(ORC_INDENTIFIER);

                if (oepOrchestrator == null)
                {
                    // Panel doesn't exist yet. Create it.
                    oepOrchestrator = (OrchestratorEventPanel) addSubCategory(ORC_INDENTIFIER);
                }

                if (oepOrchestrator != null)
                {
                    oepOrchestrator.onReceive(leEvent);

                    // No need to display it here as well.
                    bDisplay = false;
                }
            }

            // UI modifications should be done via the UI thread only.
            final String sPID = leEvent.getPID();
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

                            if ((ePID.getText() != null) && (ePID.getText().length() > 0))
                            {
                                if (!(ePID.getText().equalsIgnoreCase(sPID)))
                                {
                                    bDisplay = false;
                                }
                            }

                            if (bDisplay == true)
                            {
                                LogEventPanel.this.addEventToTable(leTemp);
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
    @Override public void clear()
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
            for (Iterator<?> iTemp = getEventIterator(); iTemp.hasNext();)
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
     * This method is called when the panel is resized. It the recalculates the weights for the sash
     * form.
     *
     * @param  ceEvent  The event that occurred.
     */
    @Override protected void calculateNewSizes(ControlEvent ceEvent)
    {
        Point pActualSize = getSize();

        if (sfSplitter != null)
        {
            sfSplitter.setWeights(new int[] { 108, pActualSize.x - 108 });
        }
    }

    /**
     * This method creates all the controls for the panel.
     */
    @Override protected void createContents()
    {
        final Composite cLogEventFrame = new Composite(this, SWT.NONE);
        cLogEventFrame.setLayout(new FillLayout());

        sfSplitter = new SashForm(cLogEventFrame, SWT.NONE);

        final Composite cLEFLeft = new Composite(sfSplitter, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        cLEFLeft.setLayout(gridLayout);

        final Label pidLabel = new Label(cLEFLeft, SWT.NONE);
        pidLabel.setText("PID");

        ePID = new Text(cLEFLeft, SWT.BORDER);

        final Composite cLEFRight = new Composite(sfSplitter, SWT.NONE);
        cLEFRight.setLayout(new FillLayout());

        tpTabSubCategories = new CTabFolder(cLEFRight, SWT.NONE);

        final CTabItem standardTabItem = new CTabItem(tpTabSubCategories, SWT.NONE);
        standardTabItem.setText("Standard");

        final Composite cStandard = new Composite(tpTabSubCategories, SWT.NONE);
        cStandard.setLayout(new FillLayout());
        standardTabItem.setControl(cStandard);

        tblEvents = new Table(cStandard, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
        tblEvents.setLinesVisible(true);
        tblEvents.setHeaderVisible(true);

        final TableColumn tcTime = new TableColumn(tblEvents, SWT.NONE);
        tcTime.setWidth(75);
        tcTime.setText("Time");

        final TableColumn tcHost = new TableColumn(tblEvents, SWT.NONE);
        tcHost.setWidth(65);
        tcHost.setText("Host");

        final TableColumn tcPID = new TableColumn(tblEvents, SWT.NONE);
        tcPID.setWidth(55);
        tcPID.setText("PID");

        final TableColumn tcCategory = new TableColumn(tblEvents, SWT.NONE);
        tcCategory.setWidth(85);
        tcCategory.setText("Category");

        final TableColumn tcConnectors = new TableColumn(tblEvents, SWT.NONE);
        tcConnectors.setWidth(75);
        tcConnectors.setText("Connectors");

        final TableColumn tcThread = new TableColumn(tblEvents, SWT.NONE);
        tcThread.setWidth(100);
        tcThread.setText("Thread");

        final TableColumn tcLevel = new TableColumn(tblEvents, SWT.NONE);
        tcLevel.setWidth(50);
        tcLevel.setText("Level");

        final TableColumn tcDetails = new TableColumn(tblEvents, SWT.NONE);
        tcDetails.setWidth(300);
        tcDetails.setText("Details");
        sfSplitter.setWeights(new int[] { 108, 521 });

        // Create the HashMap for the sub categories.
        hmSubCategories = new HashMap<String, AbstractEventPanel>();

        // Already add the sub-category
        addSubCategory(ORC_INDENTIFIER);

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
     * This method adds a new panel for a specific subcategory.
     *
     * @param   sIdent  The identification-string.
     *
     * @return  The panel that was created for the subcategory.
     */
    private AbstractEventPanel addSubCategory(String sIdent)
    {
        AbstractEventPanel aepReturn = null;

        aepReturn = hmSubCategories.get(sIdent);

        if (aepReturn == null)
        {
            if (sIdent.equals(ORC_INDENTIFIER))
            {
                CTabItem tiNew = new CTabItem(tpTabSubCategories, SWT.NONE);
                tiNew.setText("Orchestrator");

                aepReturn = new OrchestratorEventPanel(tpTabSubCategories, SWT.NONE,
                                                       getEventServiceClient());
                hmSubCategories.put(sIdent, aepReturn);

                tiNew.setControl(aepReturn);

                tpTabSubCategories.setSelection(tiNew);
                tpTabSubCategories.redraw();
            }
        }

        return aepReturn;
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
