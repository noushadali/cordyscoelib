package com.cordys.coe.tools.wcpproperties;

import java.text.MessageFormat;

import org.eclipse.jface.viewers.DialogCellEditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * Cell editor for selecting a wcp property.
 *
 * @author  pgussow
 */
public class WcpPropertyDialogEditor extends DialogCellEditor
{
    /**
     * DOCUMENTME.
     */
    private boolean isDeleteable = false;
    /**
     * DOCUMENTME.
     */
    private boolean isSelectable = false;
    /**
     * State information for updating action enablement.
     */
    private boolean isSelection = false;
    /**
     * Holds the definition file.
     */
    private DefinitionFile m_dfMetaFile;
    /**
     * Holds the cordys version.
     */
    private String m_sCordysVersion;
    /**
     * Holds the display label.
     */
    private Text m_tText;
    /**
     * DOCUMENTME.
     */
    private ModifyListener modifyListener;

    /**
     * Creates a new WcpPropertyDialogEditor object.
     *
     * @param  cParent         The parent composite.
     * @param  dfMetaFile      The meta file containing all properties.
     * @param  sCordysVersion  The current Cordys version.
     */
    public WcpPropertyDialogEditor(final Composite cParent, DefinitionFile dfMetaFile, String sCordysVersion)
    {
        super(cParent, SWT.NONE);

        m_dfMetaFile = dfMetaFile;
        m_sCordysVersion = sCordysVersion;
    }

    /**
     * This method creates the control to display in the table viewer.
     *
     * @param   cCell  The composite parent.
     *
     * @return  The actual edit control.
     *
     * @see     org.eclipse.jface.viewers.DialogCellEditor#createContents(org.eclipse.swt.widgets.Composite)
     */
    @Override protected Control createContents(final Composite cCell)
    {
        m_tText = new Text(cCell, getStyle());

        m_tText.addKeyListener(new KeyAdapter()
            {
                // hook key pressed - see PR 14201
                public void keyPressed(KeyEvent e)
                {
                    keyReleaseOccured(e);
                }
            });

        m_tText.addSelectionListener(new SelectionAdapter()
            {
                public void widgetDefaultSelected(SelectionEvent e)
                {
                    handleDefaultSelection(e);
                }
            });
        m_tText.addKeyListener(new KeyAdapter()
            {
                // hook key pressed - see PR 14201
                public void keyPressed(KeyEvent e)
                {
                    keyReleaseOccured(e);

                    // as a result of processing the above call, clients may have
                    // disposed this cell editor
                    if ((getControl() == null) || getControl().isDisposed())
                    {
                        return;
                    }
                    checkSelection(); // see explaination below
                    checkDeleteable();
                    checkSelectable();
                }
            });
        m_tText.addTraverseListener(new TraverseListener()
            {
                public void keyTraversed(TraverseEvent e)
                {
                    if ((e.detail == SWT.TRAVERSE_ESCAPE) || (e.detail == SWT.TRAVERSE_RETURN))
                    {
                        e.doit = false;
                    }
                }
            });
        // We really want a selection listener but it is not supported so we
        // use a key listener and a mouse listener to know when selection changes
        // may have occured
        m_tText.addMouseListener(new MouseAdapter()
            {
                public void mouseUp(MouseEvent e)
                {
                    checkSelection();
                    checkDeleteable();
                    checkSelectable();
                }
            });
        m_tText.addFocusListener(new FocusAdapter()
            {
                public void focusLost(FocusEvent e)
                {
                    WcpPropertyDialogEditor.this.focusLost();
                }
            });
        m_tText.setFont(cCell.getFont());
        m_tText.setBackground(cCell.getBackground());
        m_tText.setText(""); // $NON-NLS-1$
        m_tText.addModifyListener(getModifyListener());

        return m_tText;
    }

    /**
     * Processes a modify event that occurred in this text cell editor. This framework method performs validation and
     * sets the error message accordingly, and then reports a change via <code>fireEditorValueChanged</code>. Subclasses
     * should call this method at appropriate times. Subclasses may extend or reimplement.
     *
     * @param  e  the SWT modify event
     */
    protected void editOccured(ModifyEvent e)
    {
        String value = m_tText.getText();

        if (value == null)
        {
            value = ""; // $NON-NLS-1$
        }

        Object typedValue = value;
        boolean oldValidState = isValueValid();
        boolean newValidState = isCorrect(typedValue);

        if (!newValidState)
        {
            // try to insert the current value into the error message.
            setErrorMessage(MessageFormat.format(getErrorMessage(), new Object[] { value }));
        }
        valueChanged(oldValidState, newValidState);
    }

    /**
     * Handles a default selection event from the text control by applying the editor value and deactivating this cell
     * editor.
     *
     * @param  event  the selection event
     *
     * @since  3.0
     */
    protected void handleDefaultSelection(SelectionEvent event)
    {
        // same with enter-key handling code in keyReleaseOccured(e);
        fireApplyEditorValue();
        deactivate();
    }

    /**
     * This method makes sure that it responds to the 'escape' and 'tab' characters.
     *
     * @param  keKeyEvent  The event that occurred.
     *
     * @see    org.eclipse.jface.viewers.CellEditor#keyReleaseOccured(org.eclipse.swt.events.KeyEvent)
     */
    @Override protected void keyReleaseOccured(KeyEvent keKeyEvent)
    {
        if (keKeyEvent.character == '\u001b')
        {
            // Escape character
            fireCancelEditor();
        }
        else if ((keKeyEvent.character == '\t') || (keKeyEvent.character == '\r'))
        {
            // tab key or enter key
            applyEditorValueAndDeactivate();
        }
    }

    /**
     * This method opens the dialog that allows the user to choose a property or enter a custom property.
     *
     * @param   cCellEditorWindow  The window for the cell editor.
     *
     * @return  The entered value.
     *
     * @see     org.eclipse.jface.viewers.DialogCellEditor#openDialogBox(org.eclipse.swt.widgets.Control)
     */
    @Override protected Object openDialogBox(final Control cCellEditorWindow)
    {
        Object oReturn = null;

        // opening the dialog
        final WcpPropertySelectionDialog dialog = new WcpPropertySelectionDialog(cCellEditorWindow.getShell(),
                                                                                 m_dfMetaFile, m_sCordysVersion);

        oReturn = dialog.open();

        if (oReturn != null)
        {
            setValue(oReturn);
        }

        return oReturn;
    }

    /**
     * This method updates the content of the label.
     *
     * @param  oValue  The new value for the control.
     *
     * @see    org.eclipse.jface.viewers.DialogCellEditor#updateContents(java.lang.Object)
     */
    @Override protected void updateContents(final Object oValue)
    {
        String sContent = "";

        if (oValue instanceof String)
        {
            sContent = (String) oValue;
        }

        if ((m_tText != null) && (oValue != null))
        {
            m_tText.setText(sContent);
        }
    }

    /**
     * Applies the currently selected value and deactiavates the cell editor.
     */
    void applyEditorValueAndDeactivate()
    {
        markDirty();
        setValueValid(true);
        fireApplyEditorValue();
        deactivate();
    }

    /**
     * Checks to see if the "deleteable" state (can delete/ nothing to delete) has changed and if so fire an enablement
     * changed notification.
     */
    private void checkDeleteable()
    {
        boolean oldIsDeleteable = isDeleteable;
        isDeleteable = isDeleteEnabled();

        if (oldIsDeleteable != isDeleteable)
        {
            fireEnablementChanged(DELETE);
        }
    }

    /**
     * Checks to see if the "selectable" state (can select) has changed and if so fire an enablement changed
     * notification.
     */
    private void checkSelectable()
    {
        boolean oldIsSelectable = isSelectable;
        isSelectable = isSelectAllEnabled();

        if (oldIsSelectable != isSelectable)
        {
            fireEnablementChanged(SELECT_ALL);
        }
    }

    /**
     * Checks to see if the selection state (selection / no selection) has changed and if so fire an enablement changed
     * notification.
     */
    private void checkSelection()
    {
        boolean oldIsSelection = isSelection;
        isSelection = m_tText.getSelectionCount() > 0;

        if (oldIsSelection != isSelection)
        {
            fireEnablementChanged(COPY);
            fireEnablementChanged(CUT);
        }
    }

    /**
     * Return the modify listener.
     *
     * @return  DOCUMENTME
     */
    private ModifyListener getModifyListener()
    {
        if (modifyListener == null)
        {
            modifyListener = new ModifyListener()
                {
                    public void modifyText(ModifyEvent e)
                    {
                        editOccured(e);
                    }
                };
        }
        return modifyListener;
    }
}
