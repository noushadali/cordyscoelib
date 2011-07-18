package com.cordys.coe.tools.wcpproperties;

import com.cordys.coe.util.exceptions.XMLWrapperException;
import com.cordys.coe.util.swt.MessageBoxUtil;
import com.cordys.coe.util.swt.SWTResourceManager;

import java.io.File;

import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.transform.TransformerException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * This editor can be used to edit the wcp.properties. It uses an XML file which contains all the
 * properties available within the platform. It also contains information about where it's being
 * used.
 *
 * @author  pgussow
 */
public class WCPPropertiesEditor
{
    /**
     * Holds the name of the column for the value of a property.
     */
    private static final String COL_VALUE = "Value";
    /**
     * Holds the name of the column for the name of a property.
     */
    private static final String COL_PROPERTY = "Property";
    /**
     * Holds the column names for the table viewer.
     */
    public static final String[] COLUMN_NAMES = new String[] { COL_PROPERTY, COL_VALUE };
    /**
     * Holds the shell for this application.
     */
    protected Shell m_sShell;
    /**
     * Holds the selectbox for the Cordys version.
     */
    private Combo m_cbCordysVersion;
    /**
     * Holds the definition of all properties.
     */
    private DefinitionFile m_dfProperties;
    /**
     * Holds the currently loaded wcp.properties file.
     */
    private PropertiesConfiguration m_pcCurrentFile;
    /**
     * Holds the table with all properties of the currently loaded wcp.properties file.
     */
    private Table m_tblProperties;
    /**
     * Holds the where-used for the current property.
     */
    private Table m_tblWhereUsed;
    /**
     * Holds the caption for the current property.
     */
    private Text m_tCaption;
    /**
     * Holds the component for the current property.
     */
    private Text m_tComponent;
    /**
     * Holds the default value for a certain property.
     */
    private Text m_tDefaultValue;
    /**
     * Holds the description for the current property.
     */
    private Text m_tDescription;
    /**
     * Holds the loaction where to load the wcp.proeprties from.
     */
    private Text m_tLocation;
    /**
     * Holds the name for the current property.
     */
    private Text m_tName;
    /**
     * Holds the current value for the property.
     */
    private Text m_tValue;
    /**
     * Holds the table viewer for the current properties.
     */
    private TableViewer m_tvProperties;

    /**
     * Main method. It launches the application.
     *
     * @param  saArguments  The commandline arguments.
     */
    public static void main(String[] saArguments)
    {
        try
        {
            WCPPropertiesEditor window = new WCPPropertiesEditor();
            window.open();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * This method returns the currently selected Cordys version.
     *
     * @return  The currently selected Cordys version.
     */
    public String getCordysVersion()
    {
        return m_cbCordysVersion.getText();
    }

    /**
     * Open the window.
     */
    public void open()
    {
        final Display display = Display.getDefault();

        try
        {
            // Load the XML file with the different versions.
            loadWcpPropertiesXML();

            // Create the UI.
            createContents();

            // Initialize default values.
            m_cbCordysVersion.setItems(m_dfProperties.getCordysVersions());

            m_tLocation.setText("c:\\Cordys");
            m_cbCordysVersion.select(m_cbCordysVersion.getItemCount() - 1);
            m_tvProperties.setInput(m_pcCurrentFile);

            // Create the CellEditors. Needs to be done after the m_cbCordysVersion has been set.
            createCellEditors();

            m_sShell.open();
            m_sShell.layout();

            while (!m_sShell.isDisposed())
            {
                if (!display.readAndDispatch())
                {
                    display.sleep();
                }
            }
        }
        catch (Exception e)
        {
            MessageBoxUtil.showError("Error building up editor.", e);
        }
    }

    /**
     * This method opens the WCP.properties from the selected location.
     */
    public void openWcpPropertiesFile()
    {
        try
        {
            if (m_pcCurrentFile != null)
            {
                saveChanges();
            }

            File fTemp = new File(m_tLocation.getText());

            if (!fTemp.getAbsolutePath().endsWith("wcp.properties"))
            {
                fTemp = new File(fTemp, "wcp.properties");
            }

            if (!fTemp.exists())
            {
                throw new Exception("File " + fTemp.getCanonicalPath() + " does not exist.");
            }

            // Load the property file. We need to retain the structure and comments.
            m_pcCurrentFile = new PropertiesConfiguration(fTemp);

            m_tvProperties.setInput(m_pcCurrentFile);
        }
        catch (Exception e)
        {
            MessageBoxUtil.showError(m_sShell, "Error loading wcp.properties file", e);
        }
    }

    /**
     * This method saves the changes made to the current wcp.properties.
     */
    public void saveChanges()
    {
        if (MessageBoxUtil.showConfirmation(m_sShell, "Are you sure you want to save the changes?"))
        {
            try
            {
                m_pcCurrentFile.save();
            }
            catch (ConfigurationException e)
            {
                MessageBoxUtil.showError(m_sShell, "Error saving wcp.properties.", e);
            }
        }
    }

    /**
     * Create contents of the window.
     */
    protected void createContents()
    {
        m_sShell = new Shell();
        m_sShell.setImage(SWTResourceManager.getImage(WCPPropertiesEditor.class,
                                                      "image/wcpproperties.gif"));
        m_sShell.setLayout(new GridLayout());
        m_sShell.setSize(1024, 700);
        m_sShell.setText("Cordys CoE wcp.properties editor");
        m_sShell.addShellListener(new ShellAdapter()
            {
                public void shellClosed(ShellEvent e)
                {
                    if (MessageBoxUtil.showConfirmation(m_sShell,
                                                            "Do you want to save the changes?"))
                    {
                        saveChanges();
                    }

                    exitForm();
                }
            });

        final ToolBar toolBar = new ToolBar(m_sShell, SWT.NONE);
        toolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final ToolItem tiSave = new ToolItem(toolBar, SWT.PUSH);
        tiSave.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    saveChanges();
                }
            });
        tiSave.setImage(SWTResourceManager.getImage(WCPPropertiesEditor.class, "image/save.gif"));

        final Group openTheWcppropertiesGroup = new Group(m_sShell, SWT.NONE);
        openTheWcppropertiesGroup.setText(" Open the wcp.properties ");
        openTheWcppropertiesGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        openTheWcppropertiesGroup.setLayout(gridLayout);

        final Label locationLabel = new Label(openTheWcppropertiesGroup, SWT.NONE);
        locationLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        locationLabel.setText("Location:");

        m_tLocation = new Text(openTheWcppropertiesGroup, SWT.BORDER);
        m_tLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Button btnBrowse = new Button(openTheWcppropertiesGroup, SWT.NONE);
        btnBrowse.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    FileDialog fd = new FileDialog(m_sShell, SWT.OPEN);
                    fd.setText("Open wcp.properties file");
                    fd.setFilterPath("c:\\Cordys");
                    fd.setFilterNames(new String[] { "wcp.properties" });
                    fd.setFilterExtensions(new String[] { "wcp.properties" });

                    String sSelected = fd.open();

                    if (sSelected != null)
                    {
                        m_tLocation.setText(sSelected);
                    }
                }
            });
        btnBrowse.setText("&Browse");

        final Label cordysVersionLabel = new Label(openTheWcppropertiesGroup, SWT.NONE);
        cordysVersionLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        cordysVersionLabel.setText("Cordys version:");

        m_cbCordysVersion = new Combo(openTheWcppropertiesGroup, SWT.READ_ONLY);

        final GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
        gridData.widthHint = 248;
        m_cbCordysVersion.setLayoutData(gridData);

        final Button btnOpen = new Button(openTheWcppropertiesGroup, SWT.NONE);
        btnOpen.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    openWcpPropertiesFile();
                }
            });
        btnOpen.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 3, 1));
        btnOpen.setText("&Open wcp.properties");

        m_tvProperties = new TableViewer(m_sShell, SWT.FULL_SELECTION | SWT.BORDER);
        m_tvProperties.setSorter(new WcpPropertySorter(WcpPropertySorter.SORT_PROPERTY_NAME,
                                                       SWT.UP));
        m_tvProperties.addSelectionChangedListener(new ISelectionChangedListener()
            {
                public void selectionChanged(final SelectionChangedEvent e)
                {
                    ISelection sSel = e.getSelection();

                    if (sSel instanceof IStructuredSelection)
                    {
                        IStructuredSelection ssSelection = (IStructuredSelection) sSel;
                        Object oTemp = ssSelection.getFirstElement();

                        if (oTemp instanceof ActualProperty)
                        {
                            ActualProperty apProperty = (ActualProperty) oTemp;

                            showPropertyDetails(apProperty);
                        }
                    }
                }
            });
        m_tvProperties.setColumnProperties(COLUMN_NAMES);
        m_tblProperties = m_tvProperties.getTable();
        m_tvProperties.setLabelProvider(new WcpPropertyLabelProvider(m_tblProperties));
        m_tvProperties.setContentProvider(new WcpPropertyContentProvider(this, m_dfProperties));
        m_tblProperties.setHeaderVisible(true);
        m_tblProperties.setLinesVisible(true);
        m_tblProperties.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final TableColumn tcPropertyName = new TableColumn(m_tblProperties, SWT.NONE);
        tcPropertyName.setMoveable(true);
        tcPropertyName.setWidth(250);
        tcPropertyName.setText(COL_PROPERTY);

        final TableColumn tcPropertyValue = new TableColumn(m_tblProperties, SWT.NONE);
        tcPropertyValue.setMoveable(true);
        tcPropertyValue.setWidth(400);
        tcPropertyValue.setText(COL_VALUE);

        // Do the sort listener
        Listener lSortListener = new Listener()
        {
            public void handleEvent(Event e)
            {
                // determine new sort column and direction
                TableColumn tcSortColumn = m_tvProperties.getTable().getSortColumn();
                TableColumn tcCurrentColumn = (TableColumn) e.widget;
                int iDirection = m_tvProperties.getTable().getSortDirection();

                if (tcSortColumn == tcCurrentColumn)
                {
                    iDirection = (iDirection == SWT.UP) ? SWT.DOWN : SWT.UP;
                }
                else
                {
                    m_tvProperties.getTable().setSortColumn(tcCurrentColumn);
                    iDirection = SWT.UP;
                }

                // sort the data based on column and direction
                String sSortIdentifier = null;

                if (tcCurrentColumn == tcPropertyName)
                {
                    sSortIdentifier = WcpPropertySorter.SORT_PROPERTY_NAME;
                }
                else if (tcCurrentColumn == tcPropertyValue)
                {
                    sSortIdentifier = WcpPropertySorter.SORT_PROPERTY_VALUE;
                }
                m_tvProperties.getTable().setSortDirection(iDirection);
                m_tvProperties.setSorter(new WcpPropertySorter(sSortIdentifier, iDirection));
            }
        };

        tcPropertyName.addListener(SWT.Selection, lSortListener);
        tcPropertyValue.addListener(SWT.Selection, lSortListener);

        final Menu menu = new Menu(m_tblProperties);
        m_tblProperties.setMenu(menu);

        final MenuItem miNewProperty = new MenuItem(menu, SWT.NONE);
        miNewProperty.setAccelerator(SWT.ALT | 'a');
        miNewProperty.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    addNewProperty();
                }
            });
        miNewProperty.setText("Add new property");

        final MenuItem miDeleteProperty = new MenuItem(menu, SWT.NONE);
        miDeleteProperty.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    deleteProperty();
                }
            });
        miDeleteProperty.setSelection(true);
        miDeleteProperty.setText("Delete property");

        final Group detailsGroup = new Group(m_sShell, SWT.NONE);
        detailsGroup.setText(" Details ");
        detailsGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final GridLayout gridLayout_1 = new GridLayout();
        gridLayout_1.numColumns = 3;
        detailsGroup.setLayout(gridLayout_1);

        final Label nameLabel = new Label(detailsGroup, SWT.NONE);
        nameLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        nameLabel.setText("Name:");

        m_tName = new Text(detailsGroup, SWT.BORDER);
        m_tName.setEditable(false);

        final GridData gridData_3 = new GridData(SWT.FILL, SWT.CENTER, false, false);
        gridData_3.widthHint = 443;
        m_tName.setLayoutData(gridData_3);

        final Label whereUsedInLabel = new Label(detailsGroup, SWT.NONE);
        whereUsedInLabel.setText("Where used in the standard product:");

        final Label currentValueLabel = new Label(detailsGroup, SWT.NONE);
        currentValueLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        currentValueLabel.setText("Current value:");

        m_tValue = new Text(detailsGroup, SWT.BORDER);
        m_tValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        new Label(detailsGroup, SWT.NONE);

        final Label captionLabel = new Label(detailsGroup, SWT.NONE);
        captionLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        captionLabel.setText("Caption:");

        m_tCaption = new Text(detailsGroup, SWT.BORDER);
        m_tCaption.setEditable(false);
        m_tCaption.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        m_tblWhereUsed = new Table(detailsGroup, SWT.BORDER);
        m_tblWhereUsed.setLinesVisible(true);
        m_tblWhereUsed.setHeaderVisible(true);

        final GridData gridData_2 = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 4);
        gridData_2.widthHint = 650;
        m_tblWhereUsed.setLayoutData(gridData_2);

        final TableColumn newColumnTableColumn_2 = new TableColumn(m_tblWhereUsed, SWT.NONE);
        newColumnTableColumn_2.setWidth(101);
        newColumnTableColumn_2.setText("Component");

        final TableColumn newColumnTableColumn_3 = new TableColumn(m_tblWhereUsed, SWT.NONE);
        newColumnTableColumn_3.setWidth(333);
        newColumnTableColumn_3.setText("Classname");

        final Label descriptionLabel = new Label(detailsGroup, SWT.NONE);
        descriptionLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        descriptionLabel.setText("Component:");

        m_tComponent = new Text(detailsGroup, SWT.BORDER);
        m_tComponent.setEditable(false);
        m_tComponent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Label defaultValueLabel = new Label(detailsGroup, SWT.NONE);
        defaultValueLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        defaultValueLabel.setText("Default value:");

        m_tDefaultValue = new Text(detailsGroup, SWT.BORDER);
        m_tDefaultValue.setEditable(false);
        m_tDefaultValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Label componentLabel = new Label(detailsGroup, SWT.NONE);
        componentLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
        componentLabel.setText("Description:");

        m_tDescription = new Text(detailsGroup, SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.WRAP);
        m_tDescription.setEditable(false);

        final GridData gridData_1 = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gridData_1.heightHint = 74;
        m_tDescription.setLayoutData(gridData_1);
        //
    }

    /**
     * This method closes the application.
     */
    protected void exitForm()
    {
        System.exit(0);
    }

    /**
     * This method shows the details for the given property.
     *
     * @param  apProperty  The property to display the details of.
     */
    protected void showPropertyDetails(ActualProperty apProperty)
    {
        if (apProperty != null)
        {
            String sName = apProperty.getName();
            WcpProperty wpPropertyDefinition = m_dfProperties.getProperty(m_cbCordysVersion
                                                                          .getText(), sName);
            m_tblWhereUsed.removeAll();
            m_tValue.setText(((apProperty.getValue() == null) ? "" : apProperty.getValue()));

            if (wpPropertyDefinition != null)
            {
                m_tCaption.setText(wpPropertyDefinition.getCaption());
                m_tName.setText(wpPropertyDefinition.getName());

                // Fix the description to make sure it displays nicely
                String sDescription = wpPropertyDefinition.getDescription();
                sDescription = sDescription.replaceAll("\r*\n", " ");
                sDescription = sDescription.replaceAll("\\s{2,}", "");
                m_tDescription.setText(sDescription);
                m_tComponent.setText(wpPropertyDefinition.getComponent());
                m_tDefaultValue.setText(wpPropertyDefinition.getDefaultValue());

                // Now do the where-used.
                ArrayList<WcpPropertyWhereUsed> alWhereused = wpPropertyDefinition.getWhereUsed();

                for (Iterator<WcpPropertyWhereUsed> iWhereUsed = alWhereused.iterator();
                         iWhereUsed.hasNext();)
                {
                    WcpPropertyWhereUsed wpwuWhereUsed = iWhereUsed.next();

                    TableItem tiNew = new TableItem(m_tblWhereUsed, SWT.NONE);
                    tiNew.setText(new String[]
                                  {
                                      wpwuWhereUsed.getComponent(), wpwuWhereUsed.getClassName()
                                  });
                }
            }
            else
            {
                // A custom property which is not defined in the XML.
                m_tCaption.setText(sName);
                m_tName.setText(sName);
                m_tDefaultValue.setText("");
                m_tDescription.setText("Custom property");
                m_tComponent.setText("");
            }
        }
    }

    /**
     * This method shows the dialog in which the user can select the new property he wants to add.
     * That property is added with an empty value to the current property file.
     */
    private void addNewProperty()
    {
        WcpPropertySelectionDialog wpsd = new WcpPropertySelectionDialog(m_sShell, m_dfProperties,
                                                                         m_cbCordysVersion
                                                                         .getText(),
                                                                         "Select the new property to add.");
        String sResult = wpsd.open();

        if ((sResult != null) && (sResult.length() > 0))
        {
            if (m_pcCurrentFile.containsKey(sResult))
            {
                MessageBoxUtil.showError(m_sShell,
                                         "There is already a property with name " + sResult +
                                         " defined.");
            }
            else
            {
                // Add it and refresh the table.
                m_pcCurrentFile.setProperty(sResult, "");
                m_tvProperties.refresh(true);

                // Select the new property.
                WcpPropertyContentProvider wpcp = (WcpPropertyContentProvider)
                                                      m_tvProperties.getContentProvider();
                ActualProperty ap = wpcp.getActualProperties().get(sResult);
                m_tvProperties.setSelection(new StructuredSelection(ap));
            }
        }
    }

    /**
     * This method creates the cell editors for the viewer.
     */
    private void createCellEditors()
    {
        CellEditor[] aceEditors = new CellEditor[COLUMN_NAMES.length];
        String sCordysVersion = m_cbCordysVersion.getText();

        if ((sCordysVersion == null) || (sCordysVersion.length() == 0))
        {
            sCordysVersion = m_cbCordysVersion.getItem(m_cbCordysVersion.getItemCount() - 1);
        }

        aceEditors[0] = new WcpPropertyDialogEditor(m_tblProperties, m_dfProperties,
                                                    sCordysVersion);
        aceEditors[1] = new TextCellEditor(m_tblProperties);
        m_tvProperties.setCellEditors(aceEditors);
        m_tvProperties.setCellModifier(new WcpPropertyCellModifier());
    }

    /**
     * This method deletes the currently selected property from the properties file.
     */
    private void deleteProperty()
    {
        IStructuredSelection ss = (IStructuredSelection) m_tvProperties.getSelection();

        if (ss != null)
        {
            ActualProperty apSelected = (ActualProperty) ss.getFirstElement();

            if (apSelected != null)
            {
                m_pcCurrentFile.clearProperty(apSelected.getName());

                m_tvProperties.refresh(true);
            }
        }
    }

    /**
     * This method loads the definition file.
     *
     * @throws  XMLWrapperException   DOCUMENTME
     * @throws  TransformerException  DOCUMENTME
     */
    private void loadWcpPropertiesXML()
                               throws XMLWrapperException, TransformerException
    {
        m_dfProperties = new DefinitionFile();
    }

    /**
     * This class handles the modifications to the table.
     *
     * @author  pgussow
     */
    private class WcpPropertyCellModifier
        implements ICellModifier
    {
        /**
         * This method returns if the.
         *
         * @param   oElement     The element that needs to be modified.
         * @param   sColumnName  The name of the column.
         *
         * @return  Always true.
         */
        public boolean canModify(Object oElement, String sColumnName)
        {
            return true;
        }

        /**
         * This method should return the value for the given column.
         *
         * @param   oElement     The element being modified.
         * @param   sColumnName  The name of the column.
         *
         * @return  The value for this column.
         */
        public Object getValue(Object oElement, String sColumnName)
        {
            Object oReturn = "";

            if (oElement instanceof ActualProperty)
            {
                ActualProperty apProperty = (ActualProperty) oElement;

                if (COL_PROPERTY.equals(sColumnName))
                {
                    oReturn = apProperty.getName();
                }
                else if (COL_VALUE.equals(sColumnName))
                {
                    oReturn = apProperty.getValue();
                }
            }
            return oReturn;
        }

        /**
         * This method does the actual modify of the underlying object.
         *
         * @param  oElement     The element being modified.
         * @param  sColumnName  The name of the column.
         * @param  oValue       The new value for the column.
         */
        public void modify(Object oElement, String sColumnName, Object oValue)
        {
            oElement = ((TableItem) oElement).getData();

            if (oElement instanceof ActualProperty)
            {
                ActualProperty apProperty = (ActualProperty) oElement;

                if (COL_PROPERTY.equals(sColumnName))
                {
                    // Translate the integer to the actual property name.
                    String sNewName = (String) oValue;

                    String sOldName = apProperty.getName();
                    apProperty.setName(sNewName);

                    // Update the properties, but only if the the newly chosen property name is
                    // not already set.
                    boolean bContinue = false;

                    if (m_pcCurrentFile.containsKey(apProperty.getName()) &&
                            !apProperty.getName().equals(sOldName))
                    {
                        if (MessageBoxUtil.showConfirmation(m_sShell,
                                                                "Are you sure you want to override the property " +
                                                                apProperty.getName() +
                                                                " with value " +
                                                                m_pcCurrentFile.getString(apProperty
                                                                                              .getName())))
                        {
                            bContinue = true;
                        }
                    }
                    else
                    {
                        bContinue = true;
                    }

                    if (bContinue)
                    {
                        m_pcCurrentFile.clearProperty(sOldName);
                        m_pcCurrentFile.setProperty(apProperty.getName(), apProperty.getValue());

                        // Since a property is removed and a new one entered, we need to
                        // completely refresh the table.
                        if (!sOldName.equals(sNewName))
                        {
                            m_tvProperties.refresh(true);

                            // Now we need to make sure that the new one stays selected.
                            m_tvProperties.setSelection(new StructuredSelection(apProperty));
                        }
                    }
                }
                else if (COL_VALUE.equals(sColumnName))
                {
                    apProperty.setValue((String) oValue);

                    // Update the properties.
                    m_pcCurrentFile.setProperty(apProperty.getName(), apProperty.getValue());

                    m_tvProperties.refresh(apProperty, true);
                }
            }
        }
    }
}
