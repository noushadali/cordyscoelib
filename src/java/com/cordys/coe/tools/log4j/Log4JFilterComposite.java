package com.cordys.coe.tools.log4j;

import com.cordys.coe.tools.es.Log4JLogEvent;
import com.cordys.coe.tools.es.swt.filter.ESCLog4JFilter;
import com.cordys.coe.tools.es.swt.filter.FilterCellModifier;
import com.cordys.coe.tools.es.swt.filter.ILog4JFilterListViewer;
import com.cordys.coe.tools.es.swt.filter.Log4JFilterLabelProvider;
import com.cordys.coe.tools.es.swt.filter.Log4JFilterList;
import com.cordys.coe.tools.log4j.filter.ILogMessageFilter;
import com.cordys.coe.util.swt.BorderLayout;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

/**
 * This composite contains the filtering options for the Log4J event panel.
 *
 * @author  pgussow
 */
public class Log4JFilterComposite extends Composite
    implements ILogMessageFilter
{
    /**
     * Column name for the category.
     */
    public static final String COL_CATEGORY = "Category";
    /**
     * Column name for the level.
     */
    public static final String COL_LEVEL = "Level";
    /**
     * Column name whether or not to capture the events from this category.
     */
    public static final String COL_CAPTURE = "Capture";
    /**
     * Holds all the column names.
     */
    public static final String[] COLUMN_NAMES = new String[]
                                                {
                                                    COL_CATEGORY, COL_LEVEL, COL_CAPTURE
                                                };
    /**
     * Holds all the levels.
     */
    public static final String[] LEVEL_STRINGS = new String[]
                                                 {
                                                     "(NO FILTER)", "TRACE", "DEBUG", "INFO",
                                                     "WARN", "ERROR", "FATAL"
                                                 };
    /**
     * Holds arraylist with the level strings.
     */
    public static final ArrayList<String> AL_LEVEL_STRINGS = new ArrayList<String>(Arrays.asList(LEVEL_STRINGS));
    /**
     * Holds the comboboc with all the levels.
     */
    private Combo cLevels;
    /**
     * Holds the event service client it is run within.
     */
    private ILog4JComposite m_escClient;
    /**
     * Holds the list of filters.
     */
    private Log4JFilterList m_lflFilterList = new Log4JFilterList();
    /**
     * Holds the currently selected level.
     */
    private String m_sCurrentLevel = LEVEL_STRINGS[0];
    /**
     * Holds the SWT table for the viewer.
     */
    private Table m_tblCategoryFilters;
    /**
     * Holds the table viewer.
     */
    private TableViewer tableViewer;
    /**
     * Holds the dateformat that should be used.
     */
    private Text m_tDateFormat;

    /**
     * Create the composite.
     *
     * @param  parent
     * @param  iStyle
     * @param  escClient  DOCUMENTME
     */
    public Log4JFilterComposite(Composite parent, int iStyle, ILog4JComposite escClient)
    {
        super(parent, iStyle);
        m_escClient = escClient;
        setLayout(new BorderLayout(0, 0));

        Composite cTemp = new Composite(this, SWT.NONE);
        cTemp.setLayout(new BorderLayout(0, 0));
        cTemp.setLayoutData(BorderLayout.CENTER);

        final Group gGeneral = new Group(cTemp, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.makeColumnsEqualWidth = true;
        gridLayout.numColumns = 3;
        gGeneral.setLayout(gridLayout);
        gGeneral.setLayoutData(BorderLayout.NORTH);
        gGeneral.setText(" General filtering options ");

        final Label generalLogLevelLabel = new Label(gGeneral, SWT.NONE);
        generalLogLevelLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false,
                                                        false));
        generalLogLevelLabel.setText("General Log Level:");

        cLevels = new Combo(gGeneral, SWT.READ_ONLY);
        cLevels.setItems(LEVEL_STRINGS);
        cLevels.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 2, 1));
        cLevels.addModifyListener(new ModifyListener()
            {
                public void modifyText(ModifyEvent meEvent)
                {
                    m_sCurrentLevel = (String) meEvent.data;
                }
            });

        final Label regexFilterLabel = new Label(gGeneral, SWT.NONE);
        regexFilterLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
        regexFilterLabel.setText("Regex filter:");

        final Text tRegEx = new Text(gGeneral, SWT.BORDER);
        tRegEx.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

        final Label lDateFormat = new Label(gGeneral, SWT.NONE);
        lDateFormat.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
        lDateFormat.setText("Date format:");

        m_tDateFormat = new Text(gGeneral, SWT.BORDER);
        m_tDateFormat.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2,
                                                 1));
        m_tDateFormat.setText(m_escClient.getConfiguration().getDateFormat());

        final Group gFilterTable = new Group(cTemp, SWT.NONE);
        gFilterTable.setText(" Filter per category ");
        gFilterTable.setLayout(new BorderLayout(0, 0));

        int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION |
                    SWT.HIDE_SELECTION;

        m_tblCategoryFilters = new Table(gFilterTable, style);

        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalSpan = 3;
        m_tblCategoryFilters.setLayoutData(gridData);

        m_tblCategoryFilters.setLinesVisible(true);
        m_tblCategoryFilters.setHeaderVisible(true);

        // 1st column holding the category name.
        TableColumn column = new TableColumn(m_tblCategoryFilters, SWT.LEFT, 0);
        column.setText(COL_CATEGORY);
        column.setWidth(400);

        // 2nd column with the level dropdown.
        column = new TableColumn(m_tblCategoryFilters, SWT.LEFT, 1);
        column.setText(COL_LEVEL);
        column.setWidth(150);

        // 3rd column with task Owner
        column = new TableColumn(m_tblCategoryFilters, SWT.CENTER, 2);
        column.setText(COL_CAPTURE);
        column.setWidth(100);

        // Create the table viewer
        createTableViewer(m_tblCategoryFilters);

        tableViewer.setContentProvider(new Log4JFilterContentProvider());
        tableViewer.setLabelProvider(new Log4JFilterLabelProvider());

        // The input for the table viewer is the instance of FilterList
        tableViewer.setInput(m_lflFilterList);
    }

    /**
     * This method adds the category to the current list of filters.
     *
     * @param  sCategory  The category to add.
     * @param  sLevel
     */
    public void addCategory(String sCategory, String sLevel)
    {
        m_lflFilterList.addFilter(sCategory, LEVEL_STRINGS[0], true);
    }

    /**
     * DOCUMENTME.
     */
    @Override public void dispose()
    {
        super.dispose();
    }

    /**
     * This method returns the date format for the events table.
     *
     * @return  The date format to use.
     */
    public String getDateFormat()
    {
        String sReturn = m_escClient.getConfiguration().getDateFormat();

        try
        {
            sReturn = m_tDateFormat.getText();
        }
        catch (Exception e)
        {
            // Ignore it.
        }
        return sReturn;
    }

    /**
     * This method checks the current event and sees if it should be rendered or not.
     *
     * @param   lleEvent  The event that occured.
     *
     * @return  true is that event matches the filters defined. Otherwise false.
     */
    public boolean shouldDisplay(Log4JLogEvent lleEvent)
    {
        boolean bReturn = true;

        String sCategory = lleEvent.getCategory();
        String sLevel = lleEvent.getTraceLevel();

        addCategory(sCategory, sLevel);

        // First we'll see if the capture boolean is set for this category.
        bReturn = m_lflFilterList.isCapture(sCategory);

        if (bReturn == true)
        {
            // Now check the level defined for this category.
            bReturn = meetsThreshold(sCategory, sLevel);
        }

        return bReturn;
    }

    /**
     * DOCUMENTME.
     */
    @Override protected void checkSubclass()
    {
        // Disable the check that prevents subclassing of SWT components
    }

    /**
     * This method creates the table viewer that can be used to specify filters.
     *
     * @param  tblParent  The parent group.
     */
    private void createTableViewer(Table tblParent)
    {
        tableViewer = new TableViewer(tblParent);
        tableViewer.setUseHashlookup(true);

        tableViewer.setColumnProperties(COLUMN_NAMES);

        // Create the cell editors
        CellEditor[] editors = new CellEditor[COLUMN_NAMES.length];

        // Column 1 : Category name.
        TextCellEditor textEditor = new TextCellEditor(tblParent);
        ((Text) textEditor.getControl()).setTextLimit(60);
        editors[0] = textEditor;

        // Column 2 : Level (Combo Box)
        editors[1] = new ComboBoxCellEditor(tblParent, LEVEL_STRINGS, SWT.DROP_DOWN);

        // Column 3 : Checkbox
        editors[2] = new CheckboxCellEditor(tblParent);

        // Assign the cell editors to the viewer
        tableViewer.setCellEditors(editors);

        // Set the cell modifier for the viewer
        tableViewer.setCellModifier(new FilterCellModifier(COLUMN_NAMES, m_lflFilterList));
    }

    /**
     * This method returns true if the current level meets the threshold. There are 2 levels set: -
     * Overall level - Category-specific level.
     *
     * @param   sCategory  The category.
     * @param   sLevel     The level.
     *
     * @return  true if the level should be shown. Otherwise false.
     */
    private boolean meetsThreshold(String sCategory, String sLevel)
    {
        boolean bReturn = true;

        int iCurrentLevelIndex = AL_LEVEL_STRINGS.indexOf(sLevel);
        int iCategoryLevelIndex = AL_LEVEL_STRINGS.indexOf(m_lflFilterList.getCategoryLevel(sCategory));
        int iMainThresholdIndex = AL_LEVEL_STRINGS.indexOf(m_sCurrentLevel);

        if ((iMainThresholdIndex > iCurrentLevelIndex) ||
                (iMainThresholdIndex > iCategoryLevelIndex) ||
                (iCategoryLevelIndex > iCurrentLevelIndex))
        {
            bReturn = false;
        }

        return bReturn;
    }

    /**
     * InnerClass that acts as a proxy for the ExampleTaskList providing content for the Table. It
     * implements the ITaskListViewer interface since it must register changeListeners with the
     * ExampleTaskList
     */
    class Log4JFilterContentProvider
        implements IStructuredContentProvider, ILog4JFilterListViewer
    {
        /**
         * DOCUMENTME.
         *
         * @param  elfFilter  DOCUMENTME
         */
        public void addLog4JFilter(final ESCLog4JFilter elfFilter)
        {
            getDisplay().asyncExec(new Runnable()
                {
                    public void run()
                    {
                        tableViewer.add(elfFilter);
                    }
                });
        }

        /**
         * DOCUMENTME.
         */
        public void dispose()
        {
            m_lflFilterList.removeChangeListener(this);
        }

        /**
         * DOCUMENTME.
         *
         * @param   parent  DOCUMENTME
         *
         * @return  DOCUMENTME
         */
        public Object[] getElements(Object parent)
        {
            return new ArrayList<Object>(m_lflFilterList.getFilters().values()).toArray();
        }

        /**
         * DOCUMENTME.
         *
         * @param  v         DOCUMENTME
         * @param  oldInput  DOCUMENTME
         * @param  newInput  DOCUMENTME
         */
        public void inputChanged(Viewer v, Object oldInput, Object newInput)
        {
            if (newInput != null)
            {
                ((Log4JFilterList) newInput).addChangeListener(this);
            }

            if (oldInput != null)
            {
                ((Log4JFilterList) oldInput).removeChangeListener(this);
            }
        }

        /**
         * DOCUMENTME.
         *
         * @param  elfFilter  DOCUMENTME
         */
        public void removeLog4JFilter(ESCLog4JFilter elfFilter)
        {
            tableViewer.remove(elfFilter);
        }

        /**
         * DOCUMENTME.
         *
         * @param  elfFilter  DOCUMENTME
         */
        public void updateLog4JFilter(ESCLog4JFilter elfFilter)
        {
            tableViewer.update(elfFilter, null);
        }
    }
}
