package com.cordys.coe.tools.log4j.filter;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * This contains the log category viewer for the log filtering.
 *
 * @author  pgussow
 */
public class LoggerTreeViewer extends Composite
    implements ILoggerTreeViewer
{
    /**
     * Holds the color red.
     */
    private static final Color RED = new Color(Display.getCurrent(), new RGB(128, 0, 0));
    /**
     * Holds the color red.
     */
    private static final Color GREEN = new Color(Display.getCurrent(), new RGB(0, 128, 0));
    /**
     * Holds the root logger.
     */
    private IFilterConfiguration m_lcRoot;
    /**
     * Holds the tree component for the tree viewer.
     */
    private Tree m_tLoggers;
    /**
     * Holds the tree viewer.
     */
    private TreeViewer m_tvLoggers;
    /**
     * Holds the menu item that identifies whether or not this category is being captured.
     */
    private MenuItem miCapture;

    /**
     * Creates a new LoggerTreeViewer object.
     *
     * @param  cParent  The parent composite.
     * @param  iStyle   The style for this composite.
     */
    public LoggerTreeViewer(Composite cParent, int iStyle)
    {
        super(cParent, iStyle);
        setLayout(new GridLayout());
        setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        m_lcRoot = new FilterConfiguration(null, null);

        m_tvLoggers = new TreeViewer(this, SWT.BORDER);
        m_tvLoggers.addSelectionChangedListener(new ISelectionChangedListener()
            {
                public void selectionChanged(final SelectionChangedEvent e)
                {
                    handleSelectionChanged((IStructuredSelection) e.getSelection(),
                                           e.getSelectionProvider(), e.getSource());
                }
            });
        m_tvLoggers.setSorter(new Sorter());
        m_tvLoggers.setLabelProvider(new TreeLabelProvider());
        m_tvLoggers.setContentProvider(new TreeContentProvider());
        m_tvLoggers.setInput(m_lcRoot);

        m_tLoggers = m_tvLoggers.getTree();
        m_tLoggers.setHeaderVisible(true);
        m_tLoggers.setLinesVisible(true);
        m_tLoggers.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final TreeColumn newColumnTreeColumn = new TreeColumn(m_tLoggers, SWT.NONE);
        newColumnTreeColumn.setWidth(300);
        newColumnTreeColumn.setText("Category");

        final TreeColumn newColumnTreeColumn_1 = new TreeColumn(m_tLoggers, SWT.NONE);
        newColumnTreeColumn_1.setWidth(100);
        newColumnTreeColumn_1.setText("Level");

        // Create the cell editors
        CellEditor[] aceCellEditors = new CellEditor[2];

        // Column 1 : Category name.
        TextCellEditor textEditor = new TextCellEditor(m_tLoggers);
        ((Text) textEditor.getControl()).setTextLimit(60);
        aceCellEditors[0] = textEditor;

        // Column 2 : Level (Combo Box)
        aceCellEditors[1] = new ComboBoxCellEditor(m_tLoggers, IFilterConfiguration.LEVEL_STRINGS,
                                                   SWT.DROP_DOWN);

        // Assign the cell editors to the viewer
        m_tvLoggers.setColumnProperties(new String[] { "Category", "Level" });
        m_tvLoggers.setCellEditors(aceCellEditors);
        m_tvLoggers.setCellModifier(new FilterCellModifier(new String[] { "Category", "Level" },
                                                           this));

        // Create the popup menu.
        final Menu pmPopup = new Menu(m_tLoggers);
        pmPopup.addMenuListener(new MenuAdapter()
            {
                @Override public void menuShown(final MenuEvent e)
                {
                    TreeItem[] ati = m_tLoggers.getSelection();

                    if (ati.length > 0)
                    {
                        TreeItem ti = ati[0];
                        Object oData = ti.getData();

                        if (oData instanceof IFilterConfiguration)
                        {
                            IFilterConfiguration fcTemp = (IFilterConfiguration) oData;
                            miCapture.setSelection(fcTemp.shouldCapture());
                        }
                    }
                }
            });
        m_tLoggers.setMenu(pmPopup);

        miCapture = new MenuItem(pmPopup, SWT.CHECK);
        miCapture.setText("Capture");
        miCapture.addSelectionListener(new SelectionAdapter()
            {
                @Override public void widgetSelected(SelectionEvent se)
                {
                    TreeItem[] ati = m_tLoggers.getSelection();

                    if (ati.length > 0)
                    {
                        TreeItem ti = ati[0];
                        Object oData = ti.getData();

                        if (oData instanceof IFilterConfiguration)
                        {
                            IFilterConfiguration fcTemp = (IFilterConfiguration) oData;
                            miCapture.setSelection(!fcTemp.shouldCapture());
                            fcTemp.setCapture(!fcTemp.shouldCapture());
                            m_tvLoggers.refresh(fcTemp, true);
                        }
                    }
                }
            });

        new MenuItem(pmPopup, SWT.SEPARATOR);

        final MenuItem miCaptureIncSub = new MenuItem(pmPopup, SWT.NONE);
        miCaptureIncSub.setText("Capture (Including sub categories)");
        miCaptureIncSub.addSelectionListener(new SelectionAdapter()
            {
                @Override public void widgetSelected(SelectionEvent se)
                {
                    TreeItem[] ati = m_tLoggers.getSelection();

                    if (ati.length > 0)
                    {
                        TreeItem ti = ati[0];
                        Object oData = ti.getData();

                        if (oData instanceof IFilterConfiguration)
                        {
                            IFilterConfiguration fcTemp = (IFilterConfiguration) oData;
                            miCapture.setSelection(true);
                            fcTemp.setCaptureAndAllSubs(true);
                            m_tvLoggers.refresh(fcTemp, true);
                        }
                    }
                }
            });

        final MenuItem miDoNotCaptureIncSub = new MenuItem(pmPopup, SWT.NONE);
        miDoNotCaptureIncSub.setText("Do not capture (Including sub categories)");
        miDoNotCaptureIncSub.addSelectionListener(new SelectionAdapter()
            {
                @Override public void widgetSelected(SelectionEvent se)
                {
                    TreeItem[] ati = m_tLoggers.getSelection();

                    if (ati.length > 0)
                    {
                        TreeItem ti = ati[0];
                        Object oData = ti.getData();

                        if (oData instanceof IFilterConfiguration)
                        {
                            IFilterConfiguration fcTemp = (IFilterConfiguration) oData;
                            miCapture.setSelection(false);
                            fcTemp.setCaptureAndAllSubs(false);
                            m_tvLoggers.refresh(fcTemp, true);
                        }
                    }
                }
            });

        new MenuItem(pmPopup, SWT.SEPARATOR);

        final MenuItem miCopyLevelToAll = new MenuItem(pmPopup, SWT.NONE);
        miCopyLevelToAll.setText("Copy current level to all child categories");
        miCopyLevelToAll.addSelectionListener(new SelectionAdapter()
            {
                @Override public void widgetSelected(SelectionEvent se)
                {
                    TreeItem[] ati = m_tLoggers.getSelection();

                    if (ati.length > 0)
                    {
                        TreeItem ti = ati[0];
                        Object oData = ti.getData();

                        if (oData instanceof IFilterConfiguration)
                        {
                            IFilterConfiguration fcTemp = (IFilterConfiguration) oData;
                            String sNewLevel = ti.getText(1);
                            fcTemp.setLevelAndAllSubs(sNewLevel);
                            m_tvLoggers.refresh(fcTemp, true);
                        }
                    }
                }
            });
    }

    /**
     * This method adds the category to the logging.
     *
     * @param   sCategory  The full category.
     *
     * @return  The corresponding logger config.
     *
     * @see     com.cordys.coe.tools.log4j.filter.ILoggerTreeViewer#addCategory(java.lang.String)
     */
    public IFilterConfiguration addCategory(String sCategory)
    {
        return getFilterConfiguration(sCategory);
    }

    /**
     * This method is called when the filter configuration has changed.
     *
     * @param  fcChanged  The changed filter configuration.
     *
     * @see    com.cordys.coe.tools.log4j.filter.ILoggerTreeViewer#filterChanged(com.cordys.coe.tools.log4j.filter.IFilterConfiguration)
     */
    public void filterChanged(IFilterConfiguration fcChanged)
    {
        m_tvLoggers.refresh(fcChanged, true);
    }

    /**
     * This method returns the logger config for the specified category.
     *
     * @param   sCategory  The full category (com.cordys.something.Class).
     *
     * @return  The found logger config. If it could not be found it will be created.
     *
     * @see     com.cordys.coe.tools.log4j.filter.ILoggerTreeViewer#getFilterConfiguration(java.lang.String)
     */
    public IFilterConfiguration getFilterConfiguration(String sCategory)
    {
        IFilterConfiguration lcReturn = null;

        String[] asPath = parseCategoryPath(sCategory);
        IFilterConfiguration lcParent = m_lcRoot;
        boolean bRefresh = false;

        for (int iCount = 0; iCount < asPath.length; iCount++)
        {
            String sPart = asPath[iCount];
            lcReturn = lcParent.getChildLogger(sPart);

            if (lcReturn == null)
            {
                lcReturn = new FilterConfiguration(sPart, lcParent);
                lcParent.addChild(lcReturn);

                if (bRefresh == false)
                {
                    bRefresh = true;
                }
            }

            lcParent = lcReturn;
        }

        if (bRefresh == true)
        {
            getDisplay().asyncExec(new Runnable()
                {
                    public void run()
                    {
                        m_tvLoggers.setInput(m_lcRoot);
                        m_tvLoggers.refresh(m_lcRoot);
                    }
                });
        }

        return lcReturn;
    }

    /**
     * This method gets the root logger config.
     *
     * @return  The root logger config.
     *
     * @see     com.cordys.coe.tools.log4j.filter.ILoggerTreeViewer#getRoot()
     */
    public IFilterConfiguration getRoot()
    {
        return m_lcRoot;
    }

    /**
     * This method refreshes the tree.
     *
     * @see  com.cordys.coe.tools.log4j.filter.ILoggerTreeViewer#refresh()
     */
    public void refresh()
    {
        m_tvLoggers.refresh(m_lcRoot, true);
    }

    /**
     * this method cleans the current category.
     *
     * @param  sCategory  The full category.
     *
     * @see    com.cordys.coe.tools.log4j.filter.ILoggerTreeViewer#removeCategorySpecificConfig(java.lang.String)
     */
    public void removeCategorySpecificConfig(String sCategory)
    {
        getFilterConfiguration(sCategory).clean();
    }

    /**
     * This method sets the level of the category config.
     *
     * @param  sCategory  The category.
     * @param  sLevel     The level to set.
     *
     * @see    com.cordys.coe.tools.log4j.filter.ILoggerTreeViewer#setCategoryLevel(java.lang.String,
     *         java.lang.String)
     */
    public void setCategoryLevel(String sCategory, String sLevel)
    {
        getFilterConfiguration(sCategory).setLevel(sLevel);
    }

    /**
     * This method sets the root level.
     *
     * @param  lcRoot  The root config.
     *
     * @see    com.cordys.coe.tools.log4j.filter.ILoggerTreeViewer#setRoot(com.cordys.coe.tools.log4j.filter.IFilterConfiguration)
     */
    public void setRoot(IFilterConfiguration lcRoot)
    {
        if (m_lcRoot != null)
        {
            IFilterConfiguration[] alcLoggers = m_lcRoot.getChildLoggers();

            for (int iCount = 0; iCount < alcLoggers.length; iCount++)
            {
                lcRoot.addChild(alcLoggers[iCount]);
            }
        }
        m_lcRoot = lcRoot;
        m_tvLoggers.setInput(m_lcRoot);
    }

    /**
     * This method handles the selection of a different object.
     *
     * @param  ssSelection          DOCUMENTME
     * @param  spSelectionProvider  DOCUMENTME
     * @param  oSource              DOCUMENTME
     */
    protected void handleSelectionChanged(IStructuredSelection ssSelection,
                                          ISelectionProvider spSelectionProvider, Object oSource)
    {
        Object oSelected = ssSelection.getFirstElement();

        if ((oSelected != null) && (oSelected instanceof IFilterConfiguration))
        {
            IFilterConfiguration lc = (IFilterConfiguration) oSelected;
            onSelected(lc);
        }
    }

    /**
     * Adapter method.
     *
     * @param  lcSelected  The selected item in the tree.
     */
    protected void onSelected(IFilterConfiguration lcSelected)
    {
    }

    /**
     * This method parses the path of the passed on category.
     *
     * @param   sCategory  The full category (com.cordys.something.Class).
     *
     * @return  The parsed path.
     */
    private String[] parseCategoryPath(String sCategory)
    {
        return sCategory.split("\\.");
    }

    /**
     * This class can be used to sort the content of the tree.
     *
     * @author  pgussow
     */
    private class Sorter extends ViewerSorter
    {
        /**
         * This method sorts the entries.
         *
         * @param   vViewer  The current viewer.
         * @param   oItem1   First object.
         * @param   oItem2   Second object.
         *
         * @return  Which one 1 bigger.
         */
        @Override public int compare(Viewer vViewer, Object oItem1, Object oItem2)
        {
            int iReturn = 0;

            if ((oItem1 instanceof IFilterConfiguration) &&
                    (oItem2 instanceof IFilterConfiguration))
            {
                IFilterConfiguration lcItem1 = (IFilterConfiguration) oItem1;
                IFilterConfiguration lcItem2 = (IFilterConfiguration) oItem2;

                iReturn = lcItem1.getSubCategory().compareTo(lcItem2.getSubCategory());
            }

            return iReturn;
        }
    }

    /**
     * This class provides the content for the tree.
     *
     * @author  pgussow
     */
    private class TreeContentProvider
        implements IStructuredContentProvider, ITreeContentProvider
    {
        /**
         * Is called when the provider is disposed.
         */
        public void dispose()
        {
        }

        /**
         * This method returns the children for the given element.
         *
         * @param   oElement  The element which children should be returned.
         *
         * @return  The children of oElement.
         */
        public Object[] getChildren(Object oElement)
        {
            IFilterConfiguration[] alcReturn = null;

            if (oElement instanceof IFilterConfiguration)
            {
                IFilterConfiguration lcConfig = (IFilterConfiguration) oElement;

                alcReturn = lcConfig.getChildLoggers();
            }

            return alcReturn;
        }

        /**
         * DOCUMENTME.
         *
         * @param   oElement  DOCUMENTME
         *
         * @return  DOCUMENTME
         */
        public Object[] getElements(Object oElement)
        {
            Object[] oaReturn = null;

            if (oElement instanceof IFilterConfiguration)
            {
                oaReturn = getChildren(oElement);
            }

            return oaReturn;
        }

        /**
         * This method returns the parent object for the passed on object.
         *
         * @param   oElement  The element to return the parent of.
         *
         * @return  the parent object.
         */
        public Object getParent(Object oElement)
        {
            IFilterConfiguration lcReturn = null;

            if (oElement instanceof IFilterConfiguration)
            {
                IFilterConfiguration lcConfig = (IFilterConfiguration) oElement;

                lcReturn = lcConfig.getParentLogger();
            }

            return lcReturn;
        }

        /**
         * DOCUMENTME.
         *
         * @param   oElement  DOCUMENTME
         *
         * @return  DOCUMENTME
         */
        public boolean hasChildren(Object oElement)
        {
            boolean bReturn = false;

            if (oElement instanceof IFilterConfiguration)
            {
                IFilterConfiguration lcConfig = (IFilterConfiguration) oElement;

                bReturn = lcConfig.hasChildren();
            }

            return bReturn;
        }

        /**
         * DOCUMENTME.
         *
         * @param  viewer    DOCUMENTME
         * @param  oldInput  DOCUMENTME
         * @param  newInput  DOCUMENTME
         */
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
        {
        }
    }

    /**
     * This class provides the labels for the tree item.
     *
     * @author  pgussow
     */
    private class TreeLabelProvider extends LabelProvider
        implements ITableColorProvider, ITableLabelProvider
    {
        /**
         * DOCUMENT ME!
         *
         * @param  arg0
         *
         * @see    org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
         */
        @Override public void addListener(ILabelProviderListener arg0)
        {
        }

        /**
         * @see  org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
         */
        @Override public void dispose()
        {
        }

        /**
         * DOCUMENT ME!
         *
         * @param   arg0
         * @param   arg1
         *
         * @return  The background color.
         *
         * @see     org.eclipse.jface.viewers.ITableColorProvider#getBackground(java.lang.Object,
         *          int)
         */
        public Color getBackground(Object arg0, int arg1)
        {
            return null;
        }

        /**
         * DOCUMENT ME!
         *
         * @param   arg0
         * @param   arg1
         *
         * @return  The column image.
         *
         * @see     org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object,
         *          int)
         */
        public Image getColumnImage(Object arg0, int arg1)
        {
            return null;
        }

        /**
         * DOCUMENT ME!
         *
         * @param   oElement
         * @param   iColumn
         *
         * @return  The column text.
         *
         * @see     org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object,
         *          int)
         */
        public String getColumnText(Object oElement, int iColumn)
        {
            String sReturn = "";

            if (oElement instanceof IFilterConfiguration)
            {
                IFilterConfiguration lcConfig = (IFilterConfiguration) oElement;

                switch (iColumn)
                {
                    case 0:
                        sReturn = lcConfig.getSubCategory();
                        break;

                    case 1:
                        sReturn = lcConfig.getLevel();
                        break;
                }
            }
            return sReturn;
        }

        /**
         * DOCUMENT ME!
         *
         * @param   oElement
         * @param   iColumn
         *
         * @return  The forground color.
         *
         * @see     org.eclipse.jface.viewers.ITableColorProvider#getForeground(java.lang.Object,
         *          int)
         */
        public Color getForeground(Object oElement, int iColumn)
        {
            Color cReturn = null;

            if (oElement instanceof IFilterConfiguration)
            {
                IFilterConfiguration lcConfig = (IFilterConfiguration) oElement;

                switch (iColumn)
                {
                    case 0:

                        if (lcConfig.shouldCapture())
                        {
                            cReturn = GREEN;
                        }
                        else
                        {
                            cReturn = RED;
                        }
                        break;

                    case 1:
                        cReturn = null;
                        break;
                }
            }
            return cReturn;
        }

        /**
         * This method returns the image for this item.
         *
         * @param   element  The element to return the image for.
         *
         * @return  Always null.
         */
        @Override public Image getImage(Object element)
        {
            return null;
        }

        /**
         * This method returns the label for the given element.
         *
         * @param   element  The element to return the label of.
         *
         * @return  The label for the element.
         */
        @Override public String getText(Object element)
        {
            String sReturn = "";

            if (element instanceof IFilterConfiguration)
            {
                IFilterConfiguration lcConfig = (IFilterConfiguration) element;
                sReturn = lcConfig.getSubCategory();
            }
            return sReturn;
        }

        /**
         * This method returns whether or not the property affectes the label.
         *
         * @param   arg0
         * @param   arg1
         *
         * @return  Whether or not the property affectes the label.
         *
         * @see     org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object,
         *          java.lang.String)
         */
        @Override public boolean isLabelProperty(Object arg0, String arg1)
        {
            return false;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  arg0
         *
         * @see    org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
         */
        @Override public void removeListener(ILabelProviderListener arg0)
        {
        }
    }
}
