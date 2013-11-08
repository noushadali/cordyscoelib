package com.cordys.coe.tools.findclass;

import com.cordys.coe.util.classpath.ClassPathEntry;
import com.cordys.coe.util.classpath.FindClass;
import com.cordys.coe.util.classpath.IFindClassResult;
import com.cordys.coe.util.swt.MessageBoxUtil;
import com.cordys.coe.util.swt.SWTResourceManager;
import com.cordys.coe.util.system.SystemInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * This small program can be used to figure out from which jar a certain class is loaded.
 *
 * @author  pgussow
 */
public class FindClassInJar
{
    /**
     * Indicates the type Archive.
     */
    private static final String TYPE_ARCHIVE = "Archive";
    /**
     * Indicates the type folder.
     */
    private static final String TYPE_FOLDER = "Folder";
    /**
     * Indicates that the default JVM classpath is being used.
     */
    private static final int JVM_CLASSPATH = 0;
    /**
     * Indicates that the base folder is being used.
     */
    private static final int FOLDER_JARS = 1;
    /**
     * Indicates that the custom jars are being used.
     */
    private static final int CUSTOM_JARS = 2;
    /**
     * Holds the SWT shell.
     */
    protected Shell m_sShell;
    /**
     * Holds whether or not the folders should be recursed.
     */
    private Button m_cbRecursive;
    /**
     * Holds the tab-folder.
     */
    private CTabFolder m_tfTabFolder;
    /**
     * Holds the tabitem for the current classpath search.
     */
    private CTabItem m_tiCurrentClasspath;
    /**
     * Holds the tabitem for the custom jars search.
     */
    private CTabItem m_tiCustomJARs;
    /**
     * Holds the tabitem for the search results.
     */
    private CTabItem m_tiResult;
    /**
     * Holds the tabitem for the base folder search.
     */
    private CTabItem m_tiSelectedFolder;
    /**
     * Holds the current clipboard.
     */
    private Clipboard m_cbClipBoard;
    /**
     * Holds whether or not reserved (java.* and javax.*) classes should be loaded.
     */
    private Combo m_cLoadReservedClasses;
    /**
     * Holds the type of search to do.
     */
    private Combo m_cSearchType;
    /**
     * Holds whether or not the class was found.
     */
    private Label m_lStatus;
    /**
     * Holds the table containing all the jars that need to be searched.
     */
    private Table m_tblJars;
    /**
     * Holds the FQN of the class to find.
     */
    private Text m_tFQN;
    /**
     * Holds the regex for the files.
     */
    private Text m_tFileRegEx;
    /**
     * Holds the selected folder to search.
     */
    private Text m_tFolder;
    /**
     * Holds the location where it was found.
     */
    private Text m_tFoundLocation;
    /**
     * Holds the actual JVM classpath information.
     */
    private Text m_tJVMClasspath;
    /**
     * Holds the exception that occurred during searching.
     */
    private Text m_tThrowable;

    /**
     * Main method.
     *
     * @param  saArguments  The commandline arguments.
     */
    public static void main(String[] saArguments)
    {
        try
        {
            FindClassInJar window = new FindClassInJar();
            window.open();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Open the window.
     */
    public void open()
    {
        final Display display = Display.getDefault();
        m_cbClipBoard = new Clipboard(display);
        createContents();

        m_tJVMClasspath.setText(SystemInfo.getBootClasspath() + "\n" +
                                SystemInfo.getJVMClasspath());
        m_tfTabFolder.setSelection(0);

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

    /**
     * This method adds a folder which contains classes to the list.
     */
    protected void addClassFolder()
    {
        DirectoryDialog dd = new DirectoryDialog(m_sShell);
        String sFolder = dd.open();

        if ((sFolder != null) && (sFolder.length() > 0))
        {
            TableItem tiNew = new TableItem(m_tblJars, SWT.None);
            tiNew.setText(new String[] { TYPE_FOLDER, sFolder });
        }
    }

    /**
     * This method adds a java class archive to the list.
     */
    protected void addJavaArchive()
    {
        FileDialog fd = new FileDialog(m_sShell);
        fd.setFilterExtensions(new String[] { "*.jar", "*.zip", "*.*" });

        String sFilename = fd.open();

        if ((sFilename != null) && (sFilename.length() > 0))
        {
            TableItem tiNew = new TableItem(m_tblJars, SWT.None);
            tiNew.setText(new String[] { TYPE_ARCHIVE, sFilename });
        }
    }

    /**
     * This method allows browsing for a given base folder.
     */
    protected void browseFolder()
    {
        DirectoryDialog fcFile = new DirectoryDialog(m_sShell, SWT.OPEN);
        String sFilename = fcFile.open();

        if ((sFilename != null) && (sFilename.length() > 0))
        {
            m_tFolder.setText(sFilename);
        }
    }

    /**
     * Create contents of the window.
     */
    protected void createContents()
    {
        m_sShell = new Shell();
        m_sShell.setImage(SWTResourceManager.getImage(FindClassInJar.class, "javaclass.gif"));
        m_sShell.setLayout(new GridLayout());
        m_sShell.setSize(645, 434);
        m_sShell.setText("Java Class finder");
        m_sShell.addShellListener(new ShellAdapter() {
            public void shellClosed(ShellEvent e)
            {
                System.exit(0);
            }
        });

        final Group searchParametersGroup = new Group(m_sShell, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        searchParametersGroup.setLayout(gridLayout);
        searchParametersGroup.setText(" Search Parameters ");

        final GridData gd_searchParametersGroup = new GridData(SWT.FILL, SWT.CENTER, true, false);
        searchParametersGroup.setLayoutData(gd_searchParametersGroup);

        final Label fullyQualifiedNameLabel = new Label(searchParametersGroup, SWT.NONE);
        fullyQualifiedNameLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        fullyQualifiedNameLabel.setText("Fully Qualified Name:");

        m_tFQN = new Text(searchParametersGroup, SWT.BORDER);

        final GridData gd_tFQN = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_tFQN.setLayoutData(gd_tFQN);

        final Label loadReservedClassesLabel = new Label(searchParametersGroup, SWT.NONE);
        loadReservedClassesLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        loadReservedClassesLabel.setText("Load reserved classes:");

        m_cLoadReservedClasses = new Combo(searchParametersGroup, SWT.READ_ONLY);
        m_cLoadReservedClasses.setItems(new String[] { "Yes", "No" });
        m_cLoadReservedClasses.select(0);

        final GridData gd_cLoadReservedClasses = new GridData(SWT.LEFT, SWT.CENTER, true, false);
        m_cLoadReservedClasses.setLayoutData(gd_cLoadReservedClasses);

        final Label searchMethodLabel = new Label(searchParametersGroup, SWT.NONE);
        final GridData gd_searchMethodLabel = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        searchMethodLabel.setLayoutData(gd_searchMethodLabel);
        searchMethodLabel.setText("Search Method");

        m_cSearchType = new Combo(searchParametersGroup, SWT.READ_ONLY);
        m_cSearchType.setItems(new String[] { "JVM Classpath", "Selected folder", "Custom jars" });
        m_cSearchType.select(0);
        m_cSearchType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Button searchButton = new Button(searchParametersGroup, SWT.NONE);
        searchButton.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    searchForClass();
                }
            });

        final GridData gd_searchButton = new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1);
        searchButton.setLayoutData(gd_searchButton);
        searchButton.setText("&Search");

        m_tfTabFolder = new CTabFolder(m_sShell, SWT.NONE);

        final GridData gd_m_tfTabFolder = new GridData(SWT.FILL, SWT.FILL, true, true);
        m_tfTabFolder.setLayoutData(gd_m_tfTabFolder);

        m_tiCurrentClasspath = new CTabItem(m_tfTabFolder, SWT.NONE);
        m_tiCurrentClasspath.setText("JVM Classpath");

        m_tiSelectedFolder = new CTabItem(m_tfTabFolder, SWT.NONE);
        m_tiSelectedFolder.setText("Selected folder");

        final Composite composite_1 = new Composite(m_tfTabFolder, SWT.NONE);
        final GridLayout gridLayout_1 = new GridLayout();
        gridLayout_1.numColumns = 3;
        composite_1.setLayout(gridLayout_1);
        m_tiSelectedFolder.setControl(composite_1);

        final Label folderLabel = new Label(composite_1, SWT.NONE);
        folderLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        folderLabel.setText("Folder:");

        m_tFolder = new Text(composite_1, SWT.BORDER);

        final GridData gd_m_tFolder = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_tFolder.setLayoutData(gd_m_tFolder);

        final Button button = new Button(composite_1, SWT.NONE);
        button.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    browseFolder();
                }
            });
        button.setText("...");

        final Label regularExpressionLabel = new Label(composite_1, SWT.NONE);
        regularExpressionLabel.setText("RegEx for files:");

        m_tFileRegEx = new Text(composite_1, SWT.BORDER);
        m_tFileRegEx.setText("^.+.jar$|^.+.zip$");

        final GridData gd_m_tFileRegEx = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        m_tFileRegEx.setLayoutData(gd_m_tFileRegEx);
        new Label(composite_1, SWT.NONE);

        m_cbRecursive = new Button(composite_1, SWT.CHECK);
        m_cbRecursive.setSelection(true);
        m_cbRecursive.setText("Recursive");
        new Label(composite_1, SWT.NONE);

        m_tiCustomJARs = new CTabItem(m_tfTabFolder, SWT.NONE);
        m_tiCustomJARs.setText("Custom JARs");

        final Composite composite_3 = new Composite(m_tfTabFolder, SWT.NONE);
        composite_3.setLayout(new GridLayout());
        m_tiCustomJARs.setControl(composite_3);

        final ToolBar toolBar = new ToolBar(composite_3, SWT.NONE);
        final GridData gd_toolBar = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gd_toolBar.widthHint = 162;
        toolBar.setLayoutData(gd_toolBar);

        final ToolItem tiAddArchive = new ToolItem(toolBar, SWT.PUSH);
        tiAddArchive.setImage(SWTResourceManager.getImage(FindClassInJar.class, "javapackage.gif"));
        tiAddArchive.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    addJavaArchive();
                }
            });

        final ToolItem newItemToolItem = new ToolItem(toolBar, SWT.PUSH);
        newItemToolItem.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    addClassFolder();
                }
            });
        newItemToolItem.setImage(SWTResourceManager.getImage(FindClassInJar.class,
                                                             "openfoldericon.png"));

        final ToolItem newItemToolItem_1 = new ToolItem(toolBar, SWT.PUSH);
        newItemToolItem_1.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    deleteJAROrFolder();
                }
            });
        newItemToolItem_1.setImage(SWTResourceManager.getImage(FindClassInJar.class, "delete.gif"));

        new ToolItem(toolBar, SWT.SEPARATOR);

        final ToolItem newItemToolItem_3 = new ToolItem(toolBar, SWT.PUSH);
        newItemToolItem_3.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    moveJarUp();
                }
            });
        newItemToolItem_3.setImage(SWTResourceManager.getImage(FindClassInJar.class,
                                                               "moveup_eb.gif"));

        final ToolItem newItemToolItem_4 = new ToolItem(toolBar, SWT.PUSH);
        newItemToolItem_4.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    moveJarDown();
                }
            });
        newItemToolItem_4.setImage(SWTResourceManager.getImage(FindClassInJar.class,
                                                               "movedown_eb.gif"));

        new ToolItem(toolBar, SWT.SEPARATOR);

        final ToolItem newItemToolItem_5 = new ToolItem(toolBar, SWT.PUSH);
        newItemToolItem_5.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    pasteClasspathFromClipboard();
                }
            });
        newItemToolItem_5.setImage(SWTResourceManager.getImage(FindClassInJar.class,
                                                               "pasteclasspath.gif"));

        m_tblJars = new Table(composite_3, SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
        m_tblJars.setLinesVisible(true);
        m_tblJars.setHeaderVisible(true);

        final GridData gd_m_tblJars = new GridData(SWT.FILL, SWT.FILL, true, true);
        m_tblJars.setLayoutData(gd_m_tblJars);

        final TableColumn newColumnTableColumn_1 = new TableColumn(m_tblJars, SWT.NONE);
        newColumnTableColumn_1.setWidth(100);
        newColumnTableColumn_1.setText("Type");

        final TableColumn newColumnTableColumn = new TableColumn(m_tblJars, SWT.NONE);
        newColumnTableColumn.setWidth(552);
        newColumnTableColumn.setText("JAR location");

        m_tiResult = new CTabItem(m_tfTabFolder, SWT.NONE);
        m_tiResult.setText("Result");

        final Composite composite_2 = new Composite(m_tfTabFolder, SWT.NONE);
        final GridLayout gridLayout_2 = new GridLayout();
        gridLayout_2.numColumns = 2;
        composite_2.setLayout(gridLayout_2);
        m_tiResult.setControl(composite_2);

        m_lStatus = new Label(composite_2, SWT.CENTER);
        m_lStatus.setText(" ");
        m_lStatus.setForeground(SWTResourceManager.getColor(255, 0, 0));
        m_lStatus.setFont(SWTResourceManager.getFont("", 12, SWT.BOLD));

        final GridData gd_m_lStatus = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        m_lStatus.setLayoutData(gd_m_lStatus);

        final Label locationLabel = new Label(composite_2, SWT.NONE);
        locationLabel.setText("Location:");

        m_tFoundLocation = new Text(composite_2, SWT.BORDER);

        final GridData gd_m_tFoundLocation = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_tFoundLocation.setLayoutData(gd_m_tFoundLocation);

        final Label exceptionLabel = new Label(composite_2, SWT.NONE);
        final GridData gd_exceptionLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
        exceptionLabel.setLayoutData(gd_exceptionLabel);
        exceptionLabel.setText("Log:");

        m_tThrowable = new Text(composite_2, SWT.V_SCROLL | SWT.MULTI | SWT.BORDER);

        final GridData gd_m_tThrowable = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
        m_tThrowable.setLayoutData(gd_m_tThrowable);

        final Composite composite = new Composite(m_tfTabFolder, SWT.NONE);
        composite.setLayout(new GridLayout());
        m_tiCurrentClasspath.setControl(composite);

        m_tJVMClasspath = new Text(composite, SWT.V_SCROLL | SWT.MULTI | SWT.BORDER);

        final GridData gd_m_tJVMClasspath = new GridData(SWT.FILL, SWT.FILL, true, true);
        m_tJVMClasspath.setLayoutData(gd_m_tJVMClasspath);
        //
        m_cSearchType.addModifyListener(new ModifyListener()
            {
                public void modifyText(final ModifyEvent me)
                {
                    switch (m_cSearchType.getSelectionIndex())
                    {
                        case 0:
                            m_tfTabFolder.setSelection(m_tiCurrentClasspath);
                            break;

                        case 1:
                            m_tfTabFolder.setSelection(m_tiSelectedFolder);
                            break;

                        case 2:
                            m_tfTabFolder.setSelection(m_tiCustomJARs);
                            break;
                    }
                }
            });

        m_tfTabFolder.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent se)
                {
                    if (m_tfTabFolder.getSelection() == m_tiCurrentClasspath)
                    {
                        m_cSearchType.select(0);
                    }
                    else if (m_tfTabFolder.getSelection() == m_tiSelectedFolder)
                    {
                        m_cSearchType.select(1);
                    }
                    else if (m_tfTabFolder.getSelection() == m_tiCustomJARs)
                    {
                        m_cSearchType.select(2);
                    }
                }
            });
    }

    /**
     * This method will delete the currently selected jar.
     */
    protected void deleteJAROrFolder()
    {
        TableItem[] atiSelected = m_tblJars.getSelection();

        if ((atiSelected != null) && (atiSelected.length > 0))
        {
            for (TableItem tiToBeRemoved : atiSelected)
            {
                tiToBeRemoved.dispose();
            }
        }
    }

    /**
     * This method will move the currently selected row 1 down.
     */
    protected void moveJarDown()
    {
        int iIndex = m_tblJars.getSelectionIndex();

        if ((iIndex >= 0) && (iIndex < (m_tblJars.getItemCount() - 1)))
        {
            TableItem tiToBeMoved = m_tblJars.getItem(iIndex);
            TableItem tiNext = m_tblJars.getItem(iIndex + 1);

            String[] asOld = new String[2];
            asOld[0] = tiNext.getText(0);
            asOld[1] = tiNext.getText(1);

            tiNext.setText(new String[] { tiToBeMoved.getText(0), tiToBeMoved.getText(1) });
            tiToBeMoved.setText(asOld);

            m_tblJars.setSelection(iIndex + 1);
        }
    }

    /**
     * This method will move the currently selected row 1 up.
     */
    protected void moveJarUp()
    {
        int iIndex = m_tblJars.getSelectionIndex();

        if (iIndex > 0)
        {
            TableItem tiToBeMoved = m_tblJars.getItem(iIndex);
            TableItem tiPrevious = m_tblJars.getItem(iIndex - 1);

            String[] asOld = new String[2];
            asOld[0] = tiPrevious.getText(0);
            asOld[1] = tiPrevious.getText(1);

            tiPrevious.setText(new String[] { tiToBeMoved.getText(0), tiToBeMoved.getText(1) });
            tiToBeMoved.setText(asOld);

            m_tblJars.setSelection(iIndex - 1);
        }
    }

    /**
     * This method will read the clipboard and parses it into a classpath.
     */
    protected void pasteClasspathFromClipboard()
    {
        String sClipboardContent = (String) (m_cbClipBoard.getContents(TextTransfer.getInstance()));

        if (sClipboardContent != null)
        {
            // There is text, so it might be a path
            String[] saEntries = sClipboardContent.split(File.pathSeparator);

            List<ClassPathEntry> alEntries = new ArrayList<ClassPathEntry>();

            for (int iCount = 0; iCount < saEntries.length; iCount++)
            {
                ClassPathEntry cpe;

                try
                {
                    cpe = new ClassPathEntry(saEntries[iCount]);
                    alEntries.add(cpe);
                }
                catch (Exception e)
                {
                    // Ignoring the entry
                }
            }

            if (alEntries.size() > 0)
            {
                // We've found some actual entries: add them.
                for (ClassPathEntry cpe : alEntries)
                {
                    TableItem tiNew = new TableItem(m_tblJars, SWT.NONE);
                    String[] asText = new String[2];
                    asText[1] = cpe.getLocation();

                    if (cpe.getType() == ClassPathEntry.TYPE_FOLDER)
                    {
                        asText[0] = TYPE_FOLDER;
                    }
                    else
                    {
                        asText[0] = TYPE_ARCHIVE;
                    }
                    tiNew.setText(asText);
                }
            }
        }
    }

    /**
     * This method searches for the given class.
     */
    protected void searchForClass()
    {
        // We need to figure out the type of search
        int iSelection = m_cSearchType.getSelectionIndex();

        switch (iSelection)
        {
            case JVM_CLASSPATH:
                searchJVMClasspath();
                break;

            case FOLDER_JARS:
                searchFolderJARs();
                break;

            case CUSTOM_JARS:
                searchCustomJARs();
                break;
        }
        m_tfTabFolder.setSelection(3);
    }

    /**
     * This method searches for the class based on the jars and folders specified.
     */
    private void searchCustomJARs()
    {
        ArrayList<File> alFiles = new ArrayList<File>();

        TableItem[] atiItems = m_tblJars.getItems();

        for (TableItem tiTemp : atiItems)
        {
            File fFile = new File(tiTemp.getText(1));
            alFiles.add(fFile);
        }

        if (alFiles.size() > 0)
        {
            FindClass fc = new FindClass(m_tFQN.getText(),
                                         m_cLoadReservedClasses.getSelectionIndex() == 0, alFiles);

            IFindClassResult fcrResult = fc.execute();

            displayResult(fcrResult);
        }
        else
        {
            MessageBoxUtil.showError(m_sShell, "No files are selected");
        }
    }

    /**
     * This method displays the result object.
     *
     * @param  fcrResult  The result object.
     */
    private void displayResult(IFindClassResult fcrResult)
    {
        if (fcrResult.isOK())
        {
            m_lStatus.setForeground(SWTResourceManager.getColor(0, 255, 0));
            m_lStatus.setText("Class was found");

            m_tFoundLocation.setText(fcrResult.getLocation());
        }
        else
        {
            m_lStatus.setForeground(SWTResourceManager.getColor(255, 0, 0));
            m_lStatus.setText("Class was not found");
        }

        m_tThrowable.setText(fcrResult.getLogOutput());
    }

    /**
     * This method searches using the Folder settings.
     */
    private void searchFolderJARs()
    {
        if ((m_tFQN.getText() == null) || (m_tFQN.getText().length() == 0))
        {
            MessageBoxUtil.showError(m_sShell, "The Fully Qualified Name must be filled.");
        }
        else if ((m_tFolder.getText() == null) || (m_tFolder.getText().length() == 0))
        {
            MessageBoxUtil.showError(m_sShell, "The base folder must be filled.");
        }
        else if ((m_tFileRegEx.getText() == null) || (m_tFileRegEx.getText().length() == 0))
        {
            MessageBoxUtil.showError(m_sShell,
                                     "The regular expression for the filenames must be filled.");
        }
        else
        {
            FindClass fc = new FindClass(m_tFQN.getText(),
                                         m_cLoadReservedClasses.getSelectionIndex() == 0,
                                         m_tFolder.getText(), m_cbRecursive.getSelection(),
                                         m_tFileRegEx.getText());

            IFindClassResult fcrResult = fc.execute();

            displayResult(fcrResult);
        }
    }

    /**
     * This method searches the default classpath.
     */
    private void searchJVMClasspath()
    {
        if ((m_tFQN.getText() == null) || (m_tFQN.getText().length() == 0))
        {
            MessageBoxUtil.showError(m_sShell, "The Fully Qualified Name must be filled.");
        }
        else
        {
            FindClass fc = new FindClass(m_tFQN.getText(),
                                         m_cLoadReservedClasses.getSelectionIndex() == 0);

            IFindClassResult fcrResult = fc.execute();

            displayResult(fcrResult);
        }
    }
}
