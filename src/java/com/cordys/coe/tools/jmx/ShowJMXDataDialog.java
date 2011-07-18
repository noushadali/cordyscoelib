package com.cordys.coe.tools.jmx;

import com.cordys.coe.tools.jmx.factory.AttributeControlFactory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

/**
 * This dialog box will show the data retrieved via JMX on the screen.
 *
 * @author  pgussow
 */
public class ShowJMXDataDialog extends Dialog
{
    /**
     * The result for the dialog.
     */
    protected boolean m_bResult;
    /**
     * The shell used.
     */
    protected Shell m_sShell;
    /**
     * Holds the data object that needs to be displayed.
     */
    private Object m_data;

    /**
     * Creates a new ShowJMXDataDialog object.
     *
     * @param  parent  The parent shell.
     * @param  data    The data that needs to be displayed.
     */
    public ShowJMXDataDialog(Shell parent, Object data)
    {
        super(parent);
        m_data = data;
    }

    /**
     * Open the dialog.
     *
     * @return  the result
     */
    public boolean open()
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
        return m_bResult;
    }

    /**
     * Create contents of the dialog.
     */
    protected void createContents()
    {
        m_sShell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
        m_sShell.setLayout(new GridLayout());
        m_sShell.setSize(1024, 768);
        m_sShell.setText("JMX Data details");

        final Group repositoryGroup = new Group(m_sShell, SWT.NONE);
        repositoryGroup.setText(" JMX Data ");
        repositoryGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        final GridLayout gridLayout_1 = new GridLayout();
        gridLayout_1.numColumns = 2;
        repositoryGroup.setLayout(gridLayout_1);

        Control attrControl = AttributeControlFactory.createControl(repositoryGroup, m_data);

        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        attrControl.setLayoutData(gd);
        repositoryGroup.layout();
    }
}
