package com.cordys.coe.tools.log4j.filter;

import com.cordys.coe.tools.es.Log4JLogEvent;
import com.cordys.coe.tools.log4j.ILog4JComposite;
import com.cordys.coe.tools.log4j.regex.RegExStoreDlg;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * This filter panel contains the filter options on the log messages.
 *
 * @author  pgussow
 */
public class NewLog4JFilterComposite extends Composite
    implements ILogMessageFilter
{
    /**
     * Holds arraylist with the level strings.
     */
    public static final ArrayList<String> AL_LEVEL_STRINGS = new ArrayList<String>(Arrays.asList(IFilterConfiguration.LEVEL_STRINGS));
    /**
     * Holds the current level.
     */
    protected String m_sCurrentLevel;
    /**
     * Holds the currently entered regex pattern.
     */
    protected String m_sRegExPattern;
    /**
     * DOCUMENTME.
     */
    private Button m_cbCANON_EQ;
    /**
     * DOCUMENTME.
     */
    private Button m_cbCASE_INSENSITIVE;
    /**
     * DOCUMENTME.
     */
    private Button m_cbDOTALL;
    /**
     * DOCUMENTME.
     */
    private Button m_cbMULTILINE;
    /**
     * DOCUMENTME.
     */
    private Button m_cbUNICODE_CASE;
    /**
     * Holds all the levels.
     */
    private Combo m_cLevels;
    /**
     * Holds the flags that should be used for the pattern.
     */
    private int m_iRegExPattern = Pattern.DOTALL;
    /**
     * Holds the Log4JComposite that is creating this filter.
     */
    private ILog4JComposite m_lcClient;
    /**
     * Holds the tree that contains the filters.
     */
    private ILoggerTreeViewer m_ltvTreeViewer;
    /**
     * Holds the regex pattern to match against.
     */
    private Pattern m_pPattern;
    /**
     * Holds the current date format.
     */
    private Text m_tDateFormat;
    /**
     * Holds the entered RegEx.
     */
    private Text m_tRegEx;

    /**
     * Create the composite.
     *
     * @param  parent
     * @param  style
     * @param  lcClient  DOCUMENTME
     */
    public NewLog4JFilterComposite(Composite parent, int style, ILog4JComposite lcClient)
    {
        super(parent, style);
        setLayout(new GridLayout());

        m_lcClient = lcClient;

        final Group generalFilteringOptionsGroup = new Group(this, SWT.NONE);
        generalFilteringOptionsGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        generalFilteringOptionsGroup.setText(" General filtering options ");

        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 7;
        generalFilteringOptionsGroup.setLayout(gridLayout);

        final Label generalLogLevelLabel = new Label(generalFilteringOptionsGroup, SWT.NONE);
        generalLogLevelLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        generalLogLevelLabel.setText("General log level:");

        m_cLevels = new Combo(generalFilteringOptionsGroup, SWT.READ_ONLY);
        m_cLevels.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 6, 1));
        m_cLevels.setItems(IFilterConfiguration.LEVEL_STRINGS);
        m_cLevels.addModifyListener(new ModifyListener()
            {
                public void modifyText(ModifyEvent meEvent)
                {
                    m_sCurrentLevel = (String) meEvent.data;
                }
            });

        final Label regexFilterLabel = new Label(generalFilteringOptionsGroup, SWT.NONE);
        regexFilterLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        regexFilterLabel.setText("Regex filter:");

        m_tRegEx = new Text(generalFilteringOptionsGroup, SWT.BORDER);
        m_tRegEx.addModifyListener(new ModifyListener()
            {
                public void modifyText(final ModifyEvent e)
                {
                    m_sRegExPattern = m_tRegEx.getText();

                    try
                    {
                        m_pPattern = Pattern.compile(m_sRegExPattern, m_iRegExPattern);
                    }
                    catch (Exception ex)
                    {
                        m_pPattern = null;
                        ex.printStackTrace();
                    }
                }
            });
        m_tRegEx.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));

        final Button button = new Button(generalFilteringOptionsGroup, SWT.FLAT);
        button.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    showRegExRepository();
                }
            });
        button.setText("...");

        final Label regexFlagsLabel = new Label(generalFilteringOptionsGroup, SWT.RIGHT);
        regexFlagsLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        regexFlagsLabel.setText("RegEx flags:");

        RegExSelectionListener resl = new RegExSelectionListener();

        m_cbDOTALL = new Button(generalFilteringOptionsGroup, SWT.CHECK);
        m_cbDOTALL.setSelection(true);
        m_cbDOTALL.setText("DOTALL");
        m_cbDOTALL.addSelectionListener(resl);

        m_cbMULTILINE = new Button(generalFilteringOptionsGroup, SWT.CHECK);
        m_cbMULTILINE.setText("MULTILINE");
        m_cbMULTILINE.addSelectionListener(resl);

        m_cbCASE_INSENSITIVE = new Button(generalFilteringOptionsGroup, SWT.CHECK);
        m_cbCASE_INSENSITIVE.setText("CASE_INSENSITIVE");
        m_cbCASE_INSENSITIVE.addSelectionListener(resl);

        m_cbUNICODE_CASE = new Button(generalFilteringOptionsGroup, SWT.CHECK);
        m_cbUNICODE_CASE.setText("UNICODE_CASE");
        m_cbUNICODE_CASE.addSelectionListener(resl);

        m_cbCANON_EQ = new Button(generalFilteringOptionsGroup, SWT.CHECK);
        m_cbCANON_EQ.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        m_cbCANON_EQ.setText("CANON_EQ");
        m_cbCANON_EQ.addSelectionListener(resl);

        final Label dateFormatLabel = new Label(generalFilteringOptionsGroup, SWT.NONE);
        dateFormatLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        dateFormatLabel.setText("Date format:");

        m_tDateFormat = new Text(generalFilteringOptionsGroup, SWT.BORDER);
        m_tDateFormat.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 6, 1));

        m_ltvTreeViewer = new LoggerTreeViewer(this, SWT.NONE);

        //
        m_tDateFormat.setText(m_lcClient.getConfiguration().getDateFormat());
    }

    /**
     * This method is called when the composite is disposed.
     */
    @Override public void dispose()
    {
        super.dispose();
    }

    /**
     * This method returns the date format that should be used to display the time of the event.
     *
     * @return  The date format that should be used to display the time of the event.
     *
     * @see     com.cordys.coe.tools.log4j.filter.ILogMessageFilter#getDateFormat()
     */
    public String getDateFormat()
    {
        String sReturn = m_lcClient.getConfiguration().getDateFormat();

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
     * This method returns whether or not the current event should be displayed or not.
     *
     * @param   lleEvent  The current log event.
     *
     * @return  true if the event should be displayed. Otherwise false.
     *
     * @see     com.cordys.coe.tools.log4j.filter.ILogMessageFilter#shouldDisplay(com.cordys.coe.tools.es.Log4JLogEvent)
     */
    public boolean shouldDisplay(Log4JLogEvent lleEvent)
    {
        boolean bReturn = true;

        if (lleEvent != null)
        {
            String sCategory = lleEvent.getCategory();
            String sLevel = lleEvent.getTraceLevel();

            bReturn = m_ltvTreeViewer.getFilterConfiguration(sCategory).shouldCapture();

            if (bReturn == true)
            {
                // Now check if the level meets the threshold
                bReturn = meetsThreshold(sCategory, sLevel);

                // Also see if the RegEx is ok.
                if (m_pPattern != null)
                {
                    bReturn = m_pPattern.matcher(lleEvent.getMessage()).matches();
                }
            }
        }

        return bReturn;
    }

    /**
     * Disable the check that prevents subclassing of SWT components.
     */
    @Override protected void checkSubclass()
    {
        // Disable the check that prevents subclassing of SWT components
    }

    /**
     * This method shows the regex repository screen.
     */
    protected void showRegExRepository()
    {
        RegExStoreDlg resd = new RegExStoreDlg(getShell());

        if (resd.open(m_sRegExPattern) == true)
        {
            m_tRegEx.setText(resd.getPatternToUse());
        }
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
        int iCategoryLevelIndex = AL_LEVEL_STRINGS.indexOf(m_ltvTreeViewer.getFilterConfiguration(sCategory)
                                                           .getLevel());
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
     * This selection listener is used for combining the regex pattern that should be used.
     *
     * @author  pgussow
     */
    private class RegExSelectionListener
        implements SelectionListener
    {
        /**
         * This method is called when the selection is changed.
         *
         * @param  seEvent  The selection event.
         */
        public void widgetDefaultSelected(SelectionEvent seEvent)
        {
            // Recombine the the pattern.
            m_iRegExPattern = 0;

            if (m_cbCANON_EQ.getSelection() == true)
            {
                m_iRegExPattern |= Pattern.CANON_EQ;
            }

            if (m_cbCASE_INSENSITIVE.getSelection() == true)
            {
                m_iRegExPattern |= Pattern.CASE_INSENSITIVE;
            }

            if (m_cbDOTALL.getSelection() == true)
            {
                m_iRegExPattern |= Pattern.DOTALL;
            }

            if (m_cbMULTILINE.getSelection() == true)
            {
                m_iRegExPattern |= Pattern.MULTILINE;
            }

            if (m_cbUNICODE_CASE.getSelection() == true)
            {
                m_iRegExPattern |= Pattern.UNICODE_CASE;
            }
        }

        /**
         * This method is called when the selection is changed. It is routed to the default method.
         *
         * @param  seEvent  The selection event.
         */
        public void widgetSelected(SelectionEvent seEvent)
        {
            widgetDefaultSelected(seEvent);
        }
    }
}
