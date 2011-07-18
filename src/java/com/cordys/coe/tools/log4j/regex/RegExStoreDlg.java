package com.cordys.coe.tools.log4j.regex;

import com.cordys.coe.util.swt.MessageBoxUtil;
import com.cordys.coe.util.swt.SWTResourceManager;

import java.io.File;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * This dialog can be used to store/load regular expressions.
 *
 * @author  pgussow
 */
public class RegExStoreDlg extends Dialog
{
    /**
     * Identifies the regex column.
     */
    private static final int COL_REGEX = 0;
    /**
     * Identifies the description column.
     */
    private static final int COL_DESCRIPTION = 1;
    /**
     * Holds the local folder in which the Log4J viewer persists data.
     */
    private static final String LOG4J_VIEWER_FOLDER = ".coe_lv_store";
    /**
     * The name of the regex repository file.
     */
    private static final String FILE_REGEX_REPOSITORY = "lv_regexrepos.properties";
    /**
     * Holds the column names.
     */
    private static final String[] COLUMN_NAMES = new String[] { "RegEx", "Description" };
    /**
     * The result for the dialog.
     */
    protected boolean m_bResult;
    /**
     * The shell used.
     */
    protected Shell m_sShell;
    /**
     * Holds the current repository.
     */
    private RegExRepository m_rerRepos;
    /**
     * Holds the regex pattern that should be used.
     */
    private String m_sPatternToUse;
    /**
     * Holds the table with the regexes.
     */
    private Table m_tblRegExes;
    /**
     * Holds the table viewer.
     */
    private TableViewer m_tvRegExes;

    /**
     * Create the dialog.
     *
     * @param  sParent  The parent shell.
     */
    public RegExStoreDlg(Shell sParent)
    {
        this(sParent, SWT.NONE);
    }

    /**
     * Create the dialog.
     *
     * @param  sParent  The parent shell.
     * @param  iStyle   The style to use.
     */
    public RegExStoreDlg(Shell sParent, int iStyle)
    {
        super(sParent, iStyle);
    }

    /**
     * This method gets the regex pattern that should be used.
     *
     * @return  The regex pattern that should be used.
     */
    public String getPatternToUse()
    {
        return m_sPatternToUse;
    }

    /**
     * Open the dialog.
     *
     * @param   sRegExPattern  The current regex pattern.
     *
     * @return  the result
     */
    public boolean open(String sRegExPattern)
    {
        createContents();

        setCurrentRegEx(sRegExPattern);

        m_sShell.open();
        m_sShell.layout();

        Display display = getParent().getDisplay();

        while (!m_sShell.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }
        return m_bResult;
    }

    /**
     * Create contents of the dialog.
     */
    protected void createContents()
    {
        m_sShell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        m_sShell.setLayout(new GridLayout());
        m_sShell.setSize(563, 375);
        m_sShell.setText("RegEx repository");

        final Group repositoryGroup = new Group(m_sShell, SWT.NONE);
        repositoryGroup.setText(" Repository ");
        repositoryGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final GridLayout gridLayout_1 = new GridLayout();
        gridLayout_1.numColumns = 2;
        repositoryGroup.setLayout(gridLayout_1);

        final ToolBar toolBar = new ToolBar(repositoryGroup, SWT.NONE);
        toolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));

        final ToolItem tiNew = new ToolItem(toolBar, SWT.PUSH);
        tiNew.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    RegExEntry reeNew = new RegExEntry("", "");
                    m_rerRepos.addEntry(reeNew);
                    m_tvRegExes.refresh();
                    m_tvRegExes.setSelection(new StructuredSelection(reeNew));
                }
            });
        tiNew.setImage(SWTResourceManager.getImage(RegExStoreDlg.class, "new.gif"));

        final ToolItem tiDelete = new ToolItem(toolBar, SWT.PUSH);
        tiDelete.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    ISelection sSel = m_tvRegExes.getSelection();

                    if (sSel instanceof IStructuredSelection)
                    {
                        IStructuredSelection ss = (IStructuredSelection) sSel;
                        Object oTemp = ss.getFirstElement();

                        if (oTemp instanceof RegExEntry)
                        {
                            RegExEntry reeEntry = (RegExEntry) oTemp;
                            m_rerRepos.removeEntry(reeEntry);
                            m_tvRegExes.refresh();
                        }
                    }
                }
            });
        tiDelete.setImage(SWTResourceManager.getImage(RegExStoreDlg.class, "delete.gif"));

        new ToolItem(toolBar, SWT.SEPARATOR);

        final ToolItem tiSave = new ToolItem(toolBar, SWT.PUSH);
        tiSave.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    saveFile();
                }
            });
        tiSave.setImage(SWTResourceManager.getImage(RegExStoreDlg.class, "save.gif"));

        m_tvRegExes = new TableViewer(repositoryGroup, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
        m_tvRegExes.addDoubleClickListener(new IDoubleClickListener()
            {
                public void doubleClick(final DoubleClickEvent e)
                {
                    m_bResult = true;

                    RegExEntry reeCurrent = getSelectedEntry();

                    if (reeCurrent != null)
                    {
                        m_sPatternToUse = reeCurrent.getRegEx();
                    }
                    m_sShell.close();
                }
            });
        m_tvRegExes.setColumnProperties(COLUMN_NAMES);
        m_tvRegExes.setCellModifier(new CellModifier());
        m_tvRegExes.setLabelProvider(new TableLabelProvider());
        m_tvRegExes.setContentProvider(new ContentProvider());

        m_tblRegExes = m_tvRegExes.getTable();
        m_tblRegExes.setLinesVisible(true);
        m_tblRegExes.setHeaderVisible(true);
        m_tblRegExes.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

        CellEditor[] ace = new CellEditor[COL_DESCRIPTION + 1];
        ace[COL_REGEX] = new TextCellEditor(m_tblRegExes);
        ace[COL_DESCRIPTION] = new TextCellEditor(m_tblRegExes);

        m_tvRegExes.setCellEditors(ace);

        final TableColumn tcRegEx = new TableColumn(m_tblRegExes, SWT.NONE);
        tcRegEx.setWidth(250);
        tcRegEx.setText("RegEx");

        final TableColumn tcDescription = new TableColumn(m_tblRegExes, SWT.NONE);
        tcDescription.setWidth(250);
        tcDescription.setText("Description");
        m_tvRegExes.setInput(new Object());

        final Composite composite = new Composite(m_sShell, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

        final GridLayout gridLayout_2 = new GridLayout();
        gridLayout_2.numColumns = 2;
        composite.setLayout(gridLayout_2);

        final Button bCloseAndUse = new Button(composite, SWT.NONE);
        bCloseAndUse.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    m_bResult = true;

                    RegExEntry reeCurrent = getSelectedEntry();

                    if (reeCurrent != null)
                    {
                        m_sPatternToUse = reeCurrent.getRegEx();
                    }
                    m_sShell.close();
                }
            });
        bCloseAndUse.setLayoutData(new GridData(120, SWT.DEFAULT));
        bCloseAndUse.setText("&Close and use selected");

        final Button bCancel = new Button(composite, SWT.NONE);
        bCancel.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    m_bResult = false;
                    m_sShell.close();
                }
            });
        bCancel.setLayoutData(new GridData(120, SWT.DEFAULT));
        bCancel.setText("C&ancel");
        //
        loadRegExFile();
    }

    /**
     * This method saves the RegEx file.
     */
    protected void saveFile()
    {
        try
        {
            m_rerRepos.saveFile();
        }
        catch (Exception e)
        {
            MessageBoxUtil.showError(getParent(), "Error saving regex repository file.", e);
        }
    }

    /**
     * This method returns the selected entry.
     *
     * @return  The selected Entry.
     */
    private RegExEntry getSelectedEntry()
    {
        RegExEntry reeReturn = null;

        ISelection sSel = m_tvRegExes.getSelection();

        if (sSel instanceof IStructuredSelection)
        {
            IStructuredSelection ss = (IStructuredSelection) sSel;
            Object oTemp = ss.getFirstElement();

            if (oTemp instanceof RegExEntry)
            {
                reeReturn = (RegExEntry) oTemp;
            }
        }

        return reeReturn;
    }

    /**
     * This method loads all the regex which are stored on this machine.
     */
    private void loadRegExFile()
    {
        File fUserHome = new File(System.getProperty("user.home"));
        File fSubFolder = new File(fUserHome, LOG4J_VIEWER_FOLDER);

        if (!fSubFolder.exists())
        {
            fSubFolder.mkdirs();
        }

        File fRegExRepos = new File(fSubFolder, FILE_REGEX_REPOSITORY);

        // Load the file.
        try
        {
            m_rerRepos = new RegExRepository(fRegExRepos);

            m_tvRegExes.setInput(m_rerRepos);
        }
        catch (Exception e)
        {
            MessageBoxUtil.showError(getParent(), "Error loading regex repository.", e);
        }
    }

    /**
     * This method sets the currently used regex.
     *
     * @param  sRegEx  The currently selected RegEx.
     */
    private void setCurrentRegEx(String sRegEx)
    {
        final RegExEntry reeEntry = m_rerRepos.getRegExEntry(sRegEx);
        m_tvRegExes.setSelection(new StructuredSelection(reeEntry));
    }

    /**
     * Cell editor for the table.
     *
     * @author  pgussow
     */
    class CellModifier
        implements ICellModifier
    {
        /**
         * All values in this table can be edited.
         *
         * @param   oElement   The element to edit.
         * @param   sProperty  The proeprty to edit.
         *
         * @return  Always true.
         */
        public boolean canModify(Object oElement, String sProperty)
        {
            return true;
        }

        /**
         * This method returns the value for the editor.
         *
         * @param   oElement   The element to edit.
         * @param   sProperty  The proeprty to edit.
         *
         * @return  The value for the editor.
         */
        public Object getValue(Object oElement, String sProperty)
        {
            String sReturn = "";

            if (oElement instanceof RegExEntry)
            {
                RegExEntry reeEntry = (RegExEntry) oElement;

                if (sProperty.equals(COLUMN_NAMES[COL_DESCRIPTION]))
                {
                    sReturn = reeEntry.getDescription();
                }
                else if (sProperty.equals(COLUMN_NAMES[COL_REGEX]))
                {
                    sReturn = reeEntry.getRegEx();
                }
            }

            if (sReturn == null)
            {
                sReturn = "";
            }

            return sReturn;
        }

        /**
         * This method modifies the object for the given property.
         *
         * @param  oElement   the element to edit.
         * @param  sProperty  The property to edit.
         * @param  oValue     The new value.
         */
        public void modify(Object oElement, String sProperty, Object oValue)
        {
            TableItem tiTemp = null;

            if (oElement instanceof TableItem)
            {
                tiTemp = (TableItem) oElement;
                oElement = tiTemp.getData();
            }

            if (oElement instanceof RegExEntry)
            {
                RegExEntry reeEntry = (RegExEntry) oElement;

                if (sProperty.equals(COLUMN_NAMES[COL_DESCRIPTION]))
                {
                    reeEntry.setDescription(oValue.toString());
                }
                else if (sProperty.equals(COLUMN_NAMES[COL_REGEX]))
                {
                    reeEntry.setRegEx(oValue.toString());
                }
                m_tvRegExes.refresh(reeEntry);
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
            Object[] aoReturn = new Object[0];

            if (oInputElement instanceof RegExRepository)
            {
                RegExRepository rerRepos = (RegExRepository) oInputElement;
                aoReturn = rerRepos.getAllEntries();
            }
            return aoReturn;
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
     * @author  pgussow
     */
    class TableLabelProvider extends LabelProvider
        implements ITableLabelProvider
    {
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

            if (oInput instanceof RegExEntry)
            {
                RegExEntry reeEntry = (RegExEntry) oInput;

                switch (iColumnIndex)
                {
                    case COL_REGEX:
                        sReturn = reeEntry.getRegEx();
                        break;

                    case COL_DESCRIPTION:
                        sReturn = reeEntry.getDescription();
                        break;

                    default:
                        sReturn = "Invalid column";
                }
            }

            return sReturn;
        }
    }
}
