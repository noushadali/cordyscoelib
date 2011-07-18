package com.cordys.coe.tools.wcpproperties;

import com.cordys.coe.util.swt.MessageBoxUtil;

import java.util.regex.Pattern;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import org.eclipse.swt.widgets.Text;

/**
 * This dialog can be used to select a wcp property from the file.
 *
 * @author  pgussow
 */
public class WcpPropertySelectionDialog extends Dialog
{
    /**
     * Holds the result for this shell.
     */
    protected String m_sResult;
    /**
     * Holds the shell to use.
     */
    protected Shell m_sShell;
    /**
     * The definition file with the metadata.
     */
    private DefinitionFile m_dfMetaFile;
    /**
     * The current filter object.
     */
    private PropertyFilter m_pfFilter;
    /**
     * Holds teh Cordys version.
     */
    private String m_sCordysVersion;
    /**
     * Holds the title for this dialog.
     */
    private String m_sTitle = "";
    /**
     * Holds the table with the properties.
     */
    private Table m_tblWcpProperties;
    /**
     * Holds the description of the current property.
     */
    private Text m_tDescription;
    /**
     * Holds the table viewer.
     */
    private TableViewer m_tvViewer;
    /**
     * Holds the current property that should be returned.
     */
    private Text m_tWCPProperty;

    /**
     * Creates a new WcpPropertySelectionDialog object.
     *
     * @param  sParent         The parent shell.
     * @param  dfMetaFile      The file with the metadata definition.
     * @param  sCordysVersion  The cordys version to browse through.
     */
    public WcpPropertySelectionDialog(Shell sParent, DefinitionFile dfMetaFile,
                                      String sCordysVersion)
    {
        this(sParent, SWT.NONE, dfMetaFile, sCordysVersion);
    }

    /**
     * Creates a new WcpPropertySelectionDialog object.
     *
     * @param  sParent         The parent shell.
     * @param  iStyle          The SWT style.
     * @param  dfMetaFile      The file with the metadata definition.
     * @param  sCordysVersion  The cordys version to browse through.
     */
    public WcpPropertySelectionDialog(Shell sParent, int iStyle, DefinitionFile dfMetaFile,
                                      String sCordysVersion)
    {
        this(sParent, iStyle, dfMetaFile, sCordysVersion, "Select the WCP property");
    }

    /**
     * Creates a new WcpPropertySelectionDialog object.
     *
     * @param  sParent         The parent shell.
     * @param  dfMetaFile      The file with the metadata definition.
     * @param  sCordysVersion  The cordys version to browse through.
     * @param  sTitle          The title for this dialog.
     */
    public WcpPropertySelectionDialog(Shell sParent, DefinitionFile dfMetaFile,
                                      String sCordysVersion, String sTitle)
    {
        this(sParent, SWT.NONE, dfMetaFile, sCordysVersion, sTitle);
    }

    /**
     * Creates a new WcpPropertySelectionDialog object.
     *
     * @param  sParent         The parent shell.
     * @param  iStyle          The SWT style.
     * @param  dfMetaFile      The file with the metadata definition.
     * @param  sCordysVersion  The cordys version to browse through.
     * @param  sTitle          The title for this dialog.
     */
    public WcpPropertySelectionDialog(Shell sParent, int iStyle, DefinitionFile dfMetaFile,
                                      String sCordysVersion, String sTitle)
    {
        super(sParent, iStyle);
        m_dfMetaFile = dfMetaFile;
        m_sCordysVersion = sCordysVersion;
        m_sTitle = sTitle;
    }

    /**
     * Open the dialog.
     *
     * @return  the result
     */
    public String open()
    {
        createContents();
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
        return m_sResult;
    }

    /**
     * This method adjusts the filter for the current view.
     */
    protected void adjustFilter()
    {
        m_pfFilter.setFilterText(m_tWCPProperty.getText());

        m_tvViewer.refresh(true);

        // Set the selection to the first element.
        Table tTemp = m_tvViewer.getTable();

        if (tTemp != null)
        {
            if (tTemp.getItemCount() > 0)
            {
                TableItem tiTemp = tTemp.getItems()[0];

                if ((tiTemp != null) && (tiTemp.getData() != null))
                {
                    m_tvViewer.setSelection(new StructuredSelection(tiTemp.getData()));
                }
            }
        }
    }

    /**
     * Create contents of the dialog.
     */
    protected void createContents()
    {
        m_sShell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        m_sShell.setLayout(new GridLayout());
        m_sShell.setSize(498, 376);
        m_sShell.setText(m_sTitle);

        final Group enterTheWcpGroup = new Group(m_sShell, SWT.NONE);
        enterTheWcpGroup.setText(" Enter the wcp property you want to use:");
        enterTheWcpGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        enterTheWcpGroup.setLayout(new GridLayout());

        m_tWCPProperty = new Text(enterTheWcpGroup, SWT.BORDER);
        m_tWCPProperty.addKeyListener(new KeyAdapter()
            {
                public void keyPressed(final KeyEvent e)
                {
                    if (e.character == '\r')
                    {
                        doOK();
                    }
                }
            });
        m_tWCPProperty.addModifyListener(new ModifyListener()
            {
                public void modifyText(final ModifyEvent e)
                {
                    adjustFilter();
                }
            });
        m_tWCPProperty.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        m_tvViewer = new TableViewer(m_sShell, SWT.FULL_SELECTION | SWT.BORDER);
        m_tvViewer.addDoubleClickListener(new IDoubleClickListener()
            {
                public void doubleClick(final DoubleClickEvent e)
                {
                    doOK();
                }
            });
        m_tvViewer.setLabelProvider(new TableLabelProvider());
        m_tvViewer.addPostSelectionChangedListener(new ISelectionChangedListener()
            {
                public void selectionChanged(final SelectionChangedEvent e)
                {
                    IStructuredSelection ss = (IStructuredSelection) e.getSelection();

                    if (ss.getFirstElement() != null)
                    {
                        WcpProperty wp = (WcpProperty) ss.getFirstElement();
                        m_tDescription.setText(wp.getDescription());
                    }
                    else
                    {
                        m_tDescription.setText("");
                    }
                }
            });
        m_tvViewer.setContentProvider(new ContentProvider());
        m_tblWcpProperties = m_tvViewer.getTable();
        m_tblWcpProperties.setLinesVisible(true);
        m_tblWcpProperties.setHeaderVisible(true);
        m_tblWcpProperties.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final TableColumn newColumnTableColumn = new TableColumn(m_tblWcpProperties, SWT.NONE);
        newColumnTableColumn.setWidth(300);
        newColumnTableColumn.setText("Property");

        final TableColumn newColumnTableColumn_1 = new TableColumn(m_tblWcpProperties, SWT.NONE);
        newColumnTableColumn_1.setWidth(150);
        newColumnTableColumn_1.setText("Default value");
        m_tvViewer.setInput(new Object());

        m_tDescription = new Text(m_sShell, SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.WRAP);
        m_tDescription.setEditable(false);

        final GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gridData.heightHint = 84;
        m_tDescription.setLayoutData(gridData);

        final Composite composite = new Composite(m_sShell, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        composite.setLayout(gridLayout);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Button okButton = new Button(composite, SWT.NONE);
        okButton.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    doOK();
                }
            });

        final GridData gridData_1 = new GridData(SWT.RIGHT, SWT.CENTER, true, false);
        gridData_1.minimumWidth = 75;
        okButton.setLayoutData(gridData_1);
        okButton.setText("&OK");

        final Button cancelButton = new Button(composite, SWT.NONE);
        cancelButton.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    m_sShell.close();
                }
            });

        final GridData gridData_2 = new GridData(SWT.LEFT, SWT.CENTER, true, false);
        gridData_2.minimumWidth = 75;
        cancelButton.setLayoutData(gridData_2);
        cancelButton.setText("&Cancel");

        //
        m_pfFilter = new PropertyFilter();
        m_tvViewer.addFilter(m_pfFilter);
    }

    /**
     * This method handles the Ok button press. This can also happen via the double-click on the
     * viewer.
     */
    protected void doOK()
    {
        IStructuredSelection ss = (IStructuredSelection) m_tvViewer.getSelection();

        if ((ss != null) && (ss.getFirstElement() != null))
        {
            m_sResult = ((WcpProperty) ss.getFirstElement()).getName();
            m_sShell.close();
        }
        else
        {
            // Nothing is selected, thus we will assume a custom property.
            m_sResult = m_tWCPProperty.getText();

            if ((m_sResult == null) || (m_sResult.length() == 0))
            {
                MessageBoxUtil.showError(m_sShell, "No WCP property selected.");
            }
            else
            {
                m_sShell.close();
            }
        }
    }

    /**
     * Content provider.
     *
     * @author  pgussow
     */
    class ContentProvider
        implements IStructuredContentProvider
    {
        /**
         * Is called when the provider is disposed.
         */
        public void dispose()
        {
        }

        /**
         * Returns the elements for the viewer.
         *
         * @param   oInputElement  The input element.
         *
         * @return  The objects to shown.
         */
        public Object[] getElements(Object oInputElement)
        {
            return m_dfMetaFile.getProperties(m_sCordysVersion).toArray();
        }

        /**
         * Is called when the input changes.
         *
         * @param  vViewer    The viewer.
         * @param  oOldInput  The old input.
         * @param  oNewInput  The new input.
         */
        public void inputChanged(Viewer vViewer, Object oOldInput, Object oNewInput)
        {
        }
    }

    /**
     * This class returns the labels for the given property.
     *
     * @author  pgussow
     */
    class TableLabelProvider extends LabelProvider
        implements ITableLabelProvider
    {
        /**
         * Returns the image to use for this column.
         *
         * @param   oElement      The element to get the image for.
         * @param   iColumnIndex  The column index.
         *
         * @return  Always null.
         */
        public Image getColumnImage(Object oElement, int iColumnIndex)
        {
            return null;
        }

        /**
         * Returns the caption to use for this column.
         *
         * @param   oElement      The element to get the image for.
         * @param   iColumnIndex  The column index.
         *
         * @return  The caption.
         */
        public String getColumnText(Object oElement, int iColumnIndex)
        {
            String sReturn = "";

            if (oElement instanceof WcpProperty)
            {
                WcpProperty wp = (WcpProperty) oElement;

                switch (iColumnIndex)
                {
                    case 0:
                        sReturn = wp.getName();
                        break;

                    case 1:
                        sReturn = wp.getDefaultValue();
                        break;
                }
            }
            return sReturn;
        }
    }

    /**
     * Implements a {@link ViewFilter} based on content typed in the filter field.
     */
    private class PropertyFilter extends ViewerFilter
    {
        /**
         * The current regex pattern.
         */
        private Pattern m_pPattern;

        /**
         * This method determines if the current object will match the filter.
         *
         * @param   vViewer         The current viewer.
         * @param   oParentElement  The parent element.
         * @param   oElement        The actual element.
         *
         * @return  true if the object should be shown. Otherwise false.
         */
        @Override public boolean select(final Viewer vViewer, final Object oParentElement,
                                        final Object oElement)
        {
            boolean returnValue = true;

            if (this.m_pPattern != null)
            {
                returnValue = this.m_pPattern.matcher(((WcpProperty) oElement).getName()).matches();
            }
            return returnValue;
        }

        /**
         * This method sets the text for the filter.
         *
         * @param  sFilterText  The new filter text.
         */
        public void setFilterText(final String sFilterText)
        {
            String sNewText = sFilterText + "*"; // $NON-NLS-1$

            if (sNewText.trim().equals(""))
            { // $NON-NLS-1$
                m_pPattern = null;
            }
            else
            {
                sNewText = sNewText.replace("\\", "\\\\"); // $NON-NLS-1$ //$NON-NLS-2$
                sNewText = sNewText.replace(".", "\\."); // $NON-NLS-1$ //$NON-NLS-2$
                sNewText = sNewText.replace("*", ".*"); // $NON-NLS-1$ //$NON-NLS-2$
                sNewText = sNewText.replace("?", ".?"); // $NON-NLS-1$ //$NON-NLS-2$
                m_pPattern = Pattern.compile(sNewText, Pattern.CASE_INSENSITIVE);
            }
        }
    }
}
