package com.cordys.coe.tools.migration.xforms;

import com.cordys.coe.tools.migration.xforms.rules.IMessage;
import com.cordys.coe.tools.migration.xforms.rules.IXFormValidationRule;
import com.cordys.coe.util.swt.MessageBoxUtil;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Node;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * This class can be used to validate CAFs to make sure that they are properly set for C3.
 *
 * @author  pgussow
 */
public class XFormsMigrationValidator
{
    /**
     * Holds the static document reference.
     */
    private static Document s_dDoc = new Document();
    /**
     * Holds teh current shell.
     */
    protected Shell m_sShell;
    /**
     * Holds the original context.
     */
    private Text m_OriginalContext;
    /**
     * Holds all messages.
     */
    private Table m_tblMessages;
    /**
     * Holds the fixed caf content.
     */
    private Text m_tFixedCAF;
    /**
     * Holds the fixed context.
     */
    private Text m_tFixedContext;
    /**
     * Holds the original CAF file.
     */
    private Text m_tOriginalCAF;
    /**
     * Holds the human readable log.
     */
    private Text m_tReadableLog;
    /**
     * Holds the tree containing the rules.
     */
    private Tree m_tRules;
    /**
     * Holds the location of the XForm.
     */
    private Text m_tXFormLocation;
    /**
     * Holds the XML log of the differences.
     */
    private Text m_tXMLLog;

    /**
     * Launch the application.
     *
     * @param  args
     */
    public static void main(String[] args)
    {
        try
        {
            XFormsMigrationValidator window = new XFormsMigrationValidator();
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
        createContents();
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
     * Create contents of the window.
     */
    protected void createContents()
    {
        m_sShell = new Shell();
        m_sShell.setLayout(new GridLayout());
        m_sShell.setSize(892, 693);
        m_sShell.setText("XForms Migration Checker");

        final Group chooseTheXformGroup = new Group(m_sShell, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        chooseTheXformGroup.setLayout(gridLayout);
        chooseTheXformGroup.setText(" Choose the XForm to validate");

        final GridData gd_chooseTheXformGroup = new GridData(SWT.FILL, SWT.CENTER, true, false);
        chooseTheXformGroup.setLayoutData(gd_chooseTheXformGroup);

        final Label xformLabel = new Label(chooseTheXformGroup, SWT.NONE);
        xformLabel.setText("XForm:");

        m_tXFormLocation = new Text(chooseTheXformGroup, SWT.BORDER);
        m_tXFormLocation.setText("D:\\temp\\xformss\\BLink\\Maintenance\\BFHldBLinkBehGbr.caf");

        final GridData gd_m_tXFormLocation = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_tXFormLocation.setLayoutData(gd_m_tXFormLocation);

        final Button button = new Button(chooseTheXformGroup, SWT.NONE);
        button.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    findXForm();
                }
            });
        button.setText("...");

        final Button validateButton = new Button(chooseTheXformGroup, SWT.NONE);
        validateButton.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    loadAndValidate();
                }
            });

        final GridData gd_validateButton = new GridData(SWT.CENTER, SWT.CENTER, false, false, 3, 1);
        validateButton.setLayoutData(gd_validateButton);
        validateButton.setText("Validate");

        final TabFolder tabFolder = new TabFolder(m_sShell, SWT.NONE);
        tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final TabItem messagesTabItem = new TabItem(tabFolder, SWT.NONE);
        messagesTabItem.setText("Messages");

        final Composite composite = new Composite(tabFolder, SWT.NONE);
        composite.setLayout(new GridLayout());
        messagesTabItem.setControl(composite);

        final SashForm sashForm = new SashForm(composite, SWT.NONE);

        final Composite composite_3 = new Composite(sashForm, SWT.NONE);
        composite_3.setLayout(new GridLayout());

        m_tRules = new Tree(composite_3, SWT.FULL_SELECTION | SWT.BORDER);
        m_tRules.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    showMessagesForRule();
                }
            });
        m_tRules.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final Composite composite_4 = new Composite(sashForm, SWT.NONE);
        composite_4.setLayout(new GridLayout());

        final SashForm sashForm_1 = new SashForm(composite_4, SWT.NONE);
        sashForm_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final Composite composite_8 = new Composite(sashForm_1, SWT.NONE);
        composite_8.setLayout(new GridLayout());

        m_tblMessages = new Table(composite_8, SWT.FULL_SELECTION | SWT.BORDER);
        m_tblMessages.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    showSelectedMessage();
                }
            });

        final GridData gd_m_tblMessages = new GridData(SWT.FILL, SWT.FILL, true, true);
        m_tblMessages.setLayoutData(gd_m_tblMessages);
        m_tblMessages.setLinesVisible(true);
        m_tblMessages.setHeaderVisible(true);

        final TableColumn newColumnTableColumn = new TableColumn(m_tblMessages, SWT.NONE);
        newColumnTableColumn.setWidth(48);
        newColumnTableColumn.setText("Line");

        final TableColumn newColumnTableColumn_1 = new TableColumn(m_tblMessages, SWT.NONE);
        newColumnTableColumn_1.setWidth(492);
        newColumnTableColumn_1.setText("Message");

        final Composite composite_5 = new Composite(sashForm_1, SWT.NONE);
        composite_5.setLayout(new GridLayout());

        final SashForm sashForm_2 = new SashForm(composite_5, SWT.NONE);
        sashForm_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final Composite composite_6 = new Composite(sashForm_2, SWT.NONE);
        composite_6.setLayout(new GridLayout());

        final Label originalContextLabel = new Label(composite_6, SWT.NONE);
        originalContextLabel.setText("Original context");

        m_OriginalContext = new Text(composite_6,
                                     SWT.V_SCROLL | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);

        final GridData gd_m_OriginalContext = new GridData(SWT.FILL, SWT.FILL, true, true);
        m_OriginalContext.setLayoutData(gd_m_OriginalContext);

        final Composite composite_7 = new Composite(sashForm_2, SWT.NONE);
        composite_7.setLayout(new GridLayout());

        final Label fixedContextLabel = new Label(composite_7, SWT.NONE);
        fixedContextLabel.setText("Fixed context");

        m_tFixedContext = new Text(composite_7,
                                   SWT.V_SCROLL | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);

        final GridData gd_m_tFixedContext = new GridData(SWT.FILL, SWT.FILL, true, true);
        m_tFixedContext.setLayoutData(gd_m_tFixedContext);
        sashForm_2.setWeights(new int[] { 1, 1 });
        sashForm_1.setOrientation(SWT.VERTICAL);
        sashForm_1.setWeights(new int[] { 116, 101 });
        sashForm.setWeights(new int[] { 121, 340 });

        final TabItem originalTabItem = new TabItem(tabFolder, SWT.NONE);
        originalTabItem.setText("Original");

        final Composite composite_1 = new Composite(tabFolder, SWT.NONE);
        composite_1.setLayout(new GridLayout());
        originalTabItem.setControl(composite_1);

        m_tOriginalCAF = new Text(composite_1,
                                  SWT.V_SCROLL | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);

        final GridData gd_m_tOriginalCAF = new GridData(SWT.FILL, SWT.FILL, true, true);
        m_tOriginalCAF.setLayoutData(gd_m_tOriginalCAF);

        final TabItem fixedTabItem = new TabItem(tabFolder, SWT.NONE);
        fixedTabItem.setText("Fixed");

        final Composite composite_2 = new Composite(tabFolder, SWT.NONE);
        composite_2.setLayout(new GridLayout());
        fixedTabItem.setControl(composite_2);

        m_tFixedCAF = new Text(composite_2, SWT.V_SCROLL | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);

        final GridData gd_m_tFixedCAF = new GridData(SWT.FILL, SWT.FILL, true, true);
        m_tFixedCAF.setLayoutData(gd_m_tFixedCAF);

        final TabItem readableLogTabItem = new TabItem(tabFolder, SWT.NONE);
        readableLogTabItem.setText("Readable log");

        final Composite composite_9 = new Composite(tabFolder, SWT.NONE);
        composite_9.setLayout(new GridLayout());
        readableLogTabItem.setControl(composite_9);

        m_tReadableLog = new Text(composite_9,
                                  SWT.V_SCROLL | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);

        final GridData gd_m_tReadableLog = new GridData(SWT.FILL, SWT.FILL, true, true);
        m_tReadableLog.setLayoutData(gd_m_tReadableLog);

        final TabItem xmlLogTabItem = new TabItem(tabFolder, SWT.NONE);
        xmlLogTabItem.setText("XML log");

        final Composite composite_10 = new Composite(tabFolder, SWT.NONE);
        composite_10.setLayout(new GridLayout());
        xmlLogTabItem.setControl(composite_10);

        m_tXMLLog = new Text(composite_10, SWT.V_SCROLL | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);

        final GridData gd_m_tXMLLog = new GridData(SWT.FILL, SWT.FILL, true, true);
        m_tXMLLog.setLayoutData(gd_m_tXMLLog);
        //
    }

    /**
     * This method shows a file dialog to enter the filename.
     */
    protected void findXForm()
    {
        FileDialog fcFile = new FileDialog(m_sShell, SWT.OPEN);
        String sFilename = fcFile.open();

        if ((sFilename != null) && (sFilename.length() > 0))
        {
            m_tXFormLocation.setText(sFilename);
        }
    }

    /**
     * This method will load the XForm and run the validation.
     */
    protected void loadAndValidate()
    {
        try
        {
            TreeItem[] atiCurrent = m_tRules.getItems();

            for (TreeItem ti : atiCurrent)
            {
                Object oData = ti.getData();

                if ((oData != null) && (oData instanceof MigrationValidator))
                {
                    MigrationValidator mv = (MigrationValidator) oData;
                    mv.cleanUp();
                }
            }
            m_tRules.removeAll();
            showMessagesForRule();

            File fFile = new File(m_tXFormLocation.getText());
            String[] asFiles = new String[] { fFile.getCanonicalPath() };

            if (fFile.isDirectory())
            {
                // Load all cafs in the dorectory.
                asFiles = fFile.list();

                ArrayList<String> alFilenames = new ArrayList<String>();

                for (String sFile : asFiles)
                {
                    if (sFile.endsWith(".caf"))
                    {
                        alFilenames.add(new File(fFile, sFile).getCanonicalPath());
                    }
                }

                asFiles = alFilenames.toArray(new String[0]);
            }

            for (String sFilename : asFiles)
            {
                int iXML = s_dDoc.load(sFilename);

                MigrationValidator mv = new MigrationValidator(iXML);
                mv.validateAndFix();

                m_tXMLLog.setText(Node.writeToString(mv.printMessagesToXML(), true));

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream ps = new PrintStream(baos);
                mv.printMessages(ps);

                m_tReadableLog.setText(baos.toString());

                m_tOriginalCAF.setText(Node.writeToString(mv.getCAFDefinition(), true));
                m_tFixedCAF.setText(Node.writeToString(mv.getFixedCAF(), true));

                // Create the tree
                TreeItem tiRules = new TreeItem(m_tRules, SWT.NONE);
                tiRules.setText(sFilename);
                tiRules.setData(mv);

                IXFormValidationRule[] axfRules = mv.getValidationRules();

                for (IXFormValidationRule xvr : axfRules)
                {
                    TreeItem tiTemp = new TreeItem(tiRules, SWT.NONE);
                    tiTemp.setText(xvr.getRuleName());
                    tiTemp.setData(xvr);

                    IMessage[] amMessages = xvr.getMessages();
                    Color cColor = Display.getCurrent().getSystemColor(SWT.COLOR_GREEN);

                    if (amMessages.length != 0)
                    {
                        // Errors, so make it red.
                        cColor = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
                    }

                    tiTemp.setForeground(cColor);

                    // Check the parent color.
                    if ((tiTemp.getParentItem().getForeground() != null) &&
                            !tiTemp.getParentItem().getForeground().equals(Display.getCurrent()
                                                                               .getSystemColor(SWT.COLOR_RED)))
                    {
                        tiTemp.getParentItem().setForeground(cColor);
                    }
                }
            }

            m_tRules.setSelection(m_tRules.getTopItem().getItem(0));
            showMessagesForRule();
        }
        catch (Exception e)
        {
            MessageBoxUtil.showError(m_sShell, "Error validating the XForm", e);
        }
    }

    /**
     * This method shows the messages for the current form.
     */
    protected void showMessagesForRule()
    {
        TreeItem[] atiSel = m_tRules.getSelection();

        boolean bClean = true;

        m_tblMessages.removeAll();

        if (atiSel.length > 0)
        {
            TreeItem tiRule = atiSel[0];
            Object oData = tiRule.getData();

            if ((oData != null) && (oData instanceof IXFormValidationRule))
            {
                IXFormValidationRule xvr = (IXFormValidationRule) oData;

                // Make sure the proper CAf definition is shown.
                Object oParentData = tiRule.getParentItem().getData();

                if ((oParentData != null) && (oParentData instanceof MigrationValidator))
                {
                    MigrationValidator mv = (MigrationValidator) oParentData;

                    m_tFixedCAF.setText(Node.writeToString(mv.getFixedCAF(), true));
                    m_tOriginalCAF.setText(Node.writeToString(mv.getCAFDefinition(), true));
                }

                IMessage[] amMessages = xvr.getMessages();
                Color cColor = Display.getCurrent().getSystemColor(SWT.COLOR_GREEN);

                if (amMessages.length != 0)
                {
                    // Errors, so make it red.
                    cColor = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
                }

                tiRule.setForeground(cColor);

                // Check the parent color.
                if ((tiRule.getParentItem().getForeground() == null) &&
                        !tiRule.getParentItem().getForeground().equals(Display.getCurrent()
                                                                           .getSystemColor(SWT.COLOR_RED)))
                {
                    tiRule.getParentItem().setForeground(cColor);
                }

                for (IMessage mMessage : amMessages)
                {
                    TableItem tiNew = new TableItem(m_tblMessages, SWT.NONE);
                    tiNew.setText(new String[]
                                  {
                                      String.valueOf(mMessage.getLineNumber()),
                                      mMessage.getDescription()
                                  });
                    tiNew.setData(mMessage);
                    bClean = false;
                }

                m_tblMessages.select(0);
                showSelectedMessage();
            }
            else if ((oData != null) && (oData instanceof MigrationValidator))
            {
                MigrationValidator mv = (MigrationValidator) oData;

                m_tFixedCAF.setText(Node.writeToString(mv.getFixedCAF(), true));
                m_tOriginalCAF.setText(Node.writeToString(mv.getCAFDefinition(), true));
            }
        }

        if (bClean == true)
        {
            m_OriginalContext.setText("");
            m_tFixedContext.setText("");
        }
    }

    /**
     * This method shows the selected context.
     */
    protected void showSelectedMessage()
    {
        TableItem[] ati = m_tblMessages.getSelection();
        boolean bClean = true;

        if (ati.length > 0)
        {
            TableItem tiTemp = ati[0];
            Object oData = tiTemp.getData();

            if ((oData != null) && (oData instanceof IMessage))
            {
                IMessage mMessage = (IMessage) oData;

                m_OriginalContext.setText(Node.writeToString(mMessage.getContextNode(), true));
                m_tFixedContext.setText(Node.writeToString(mMessage.getFixedContext(), true));
                bClean = false;
            }
        }

        if (bClean == true)
        {
            m_OriginalContext.setText("");
            m_tFixedContext.setText("");
        }
    }
}
