package com.cordys.coe.tools.es.swt;

import com.cordys.coe.exception.GeneralException;
import com.cordys.coe.tools.es.ILogEvent;
import com.cordys.coe.tools.es.OrchestratorEvent;

import java.io.IOException;

import java.text.SimpleDateFormat;

import org.eclipse.swt.SWT;
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
 * This panel can show the events that Orchestrator produces.
 *
 * @author  pgussow
 */
public class OrchestratorEventPanel extends AbstractEventPanel
{
    /**
     * The splitter for the filter and the table.
     */
    private SashForm m_sfSplitter;
    /**
     * Table that holds all the events.
     */
    private Table m_tblEvents;
    /**
     * Holds the filter for the source.
     */
    private Text m_tfSource;
    /**
     * Holds the filter with which the source has to start.
     */
    private Text m_tfStart;

    /**
     * Constructor.
     *
     * @param  cParent    The parent composite.
     * @param  iStyle     The style for this composite.
     * @param  escClient  The parent EventServiceClient.
     */
    public OrchestratorEventPanel(Composite cParent, int iStyle, IEventServiceClient escClient)
    {
        super(cParent, iStyle, escClient);
    }

    /**
     * This method adds the details of this message to the table.
     *
     * @param  oeEvent  The event to add.
     */
    public void addOrchestratorEventToTable(OrchestratorEvent oeEvent)
    {
        TableItem tiNew = new TableItem(m_tblEvents, SWT.NONE);
        tiNew.setData(oeEvent);

        String sTemp = "";
        tiNew.setText(0, oeEvent.getPID());

        SimpleDateFormat sdf = new SimpleDateFormat(getEventServiceClient().getConfiguration()
                                                    .getDateFormat());
        sTemp = sdf.format(oeEvent.getTime());
        tiNew.setText(1, sTemp);

        tiNew.setText(2, oeEvent.getSource());
        tiNew.setText(3, oeEvent.getSplitSource());
        tiNew.setText(4, oeEvent.getTraceLevel());
        tiNew.setText(5, oeEvent.getMessage());
    }

    /**
     * This method clears all events.
     */
    @Override public void clear()
    {
        m_tblEvents.removeAll();
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
            if (leEvent.getCategory().startsWith(LogEventPanel.ORC_INDENTIFIER))
            {
                final OrchestratorEvent oeEvent = new OrchestratorEvent(leEvent);

                // Check if a flow was started. If so, subscribe to the flow.
                String sFlowID = oeEvent.getPID();

                if (sFlowID != null)
                {
                    getEventServiceClient().subscribeToSubject(sFlowID);
                }

                // Now we need to update the UI, so we need to do this in the UI thread.
                Display dTemp = getDisplay();

                if ((dTemp != null) && !dTemp.isDisposed())
                {
                    dTemp.asyncExec(new Runnable()
                        {
                            public void run()
                            {
                                boolean bDisplay = true;

                                if ((m_tfSource.getText() != null) &&
                                        (m_tfSource.getText().length() > 0))
                                {
                                    if (!(m_tfSource.getText().equalsIgnoreCase(oeEvent
                                                                                    .getSplitSource())))
                                    {
                                        bDisplay = false;
                                    }
                                }

                                if ((m_tfStart.getText() != null) &&
                                        (m_tfStart.getText().length() > 0))
                                {
                                    if (!(m_tfStart.getText().equalsIgnoreCase(oeEvent.getSource())))
                                    {
                                        bDisplay = false;
                                    }
                                }

                                if (bDisplay == true)
                                {
                                    OrchestratorEventPanel.this.addOrchestratorEventToTable(oeEvent);
                                }
                            }
                        });
                }
            }
        }
        catch (GeneralException e)
        {
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
     * This method is called when the panel is resized. It the recalculates the weights for the sash
     * form.
     *
     * @param  ceEvent  The event that occurred.
     */
    @Override protected void calculateNewSizes(ControlEvent ceEvent)
    {
        Point pSize = getSize();
        m_sfSplitter.setWeights(new int[] { 108, pSize.x - 108 });
    }

    /**
     * This method creates all the controls for the panel.
     */
    @Override protected void createContents()
    {
        final Composite cLogEventFrame = new Composite(this, SWT.NONE);
        cLogEventFrame.setLayout(new FillLayout());

        m_sfSplitter = new SashForm(cLogEventFrame, SWT.HORIZONTAL);

        // The left panel
        final Composite cLEFLeft = new Composite(m_sfSplitter, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        cLEFLeft.setLayout(gridLayout);

        // The filter panel
        final Label pidLabel = new Label(cLEFLeft, SWT.NONE);
        pidLabel.setText("Source:");

        m_tfSource = new Text(cLEFLeft, SWT.BORDER);

        final Label startLabel = new Label(cLEFLeft, SWT.NONE);
        startLabel.setText("Start:");

        m_tfStart = new Text(cLEFLeft, SWT.BORDER);

        // The event panel
        final Composite cLEFRight = new Composite(m_sfSplitter, SWT.NONE);
        cLEFRight.setLayout(new FillLayout());

        m_tblEvents = new Table(cLEFRight, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
        m_tblEvents.setLinesVisible(true);
        m_tblEvents.setHeaderVisible(true);

        final TableColumn tcPID = new TableColumn(m_tblEvents, SWT.NONE);
        tcPID.setWidth(55);
        tcPID.setText("PID");

        final TableColumn tcTime = new TableColumn(m_tblEvents, SWT.NONE);
        tcTime.setWidth(75);
        tcTime.setText("Time");

        final TableColumn tcFullSource = new TableColumn(m_tblEvents, SWT.NONE);
        tcFullSource.setWidth(120);
        tcFullSource.setText("Full source");

        final TableColumn tcSource = new TableColumn(m_tblEvents, SWT.NONE);
        tcSource.setWidth(85);
        tcSource.setText("Source");

        final TableColumn tcLevel = new TableColumn(m_tblEvents, SWT.NONE);
        tcLevel.setWidth(50);
        tcLevel.setText("Trace Level");

        final TableColumn tcDetails = new TableColumn(m_tblEvents, SWT.NONE);
        tcDetails.setWidth(300);
        tcDetails.setText("Details");
        m_sfSplitter.setWeights(new int[] { 108, 521 });

        // Add the selection listener
        m_tblEvents.addSelectionListener(new EventsSelectionListener());
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

            if ((oData != null) && (oData instanceof OrchestratorEvent))
            {
                OrchestratorEvent oeTemp = (OrchestratorEvent) oData;
                getEventServiceClient().setDetailedText(oeTemp);
            }
        }
    }
}
