package com.cordys.coe.util.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * DOCUMENTME.
 *
 * @author  $author$
 */
public class InputBox extends Dialog
{
    /**
     * DOCUMENTME.
     */
    private Label label;
    /**
     * DOCUMENTME.
     */
    private String message;
    /**
     * DOCUMENTME.
     */
    private String result;
    /**
     * DOCUMENTME.
     */
    private Shell shell;

    /**
     * Creates a new InputBox object.
     *
     * @param  parent        DOCUMENTME
     * @param  style         DOCUMENTME
     * @param  sDefaultText  DOCUMENTME
     */
    public InputBox(Shell parent, int style, String sDefaultText)
    {
        super(parent, checkStyle(style));

        shell = new Shell(parent, SWT.DIALOG_TRIM | checkStyle(style));
        shell.setText(getText());
        shell.setLayout(new GridLayout(2, false));

        new Label(shell, SWT.CENTER).setImage(shell.getDisplay().getSystemImage(checkImageStyle(style)));

        Composite body = new Composite(shell, SWT.NONE);

        GridData data0 = new GridData();
        data0.grabExcessHorizontalSpace = true;
        data0.grabExcessVerticalSpace = true;
        data0.horizontalAlignment = SWT.FILL;
        data0.verticalAlignment = SWT.FILL;
        body.setLayoutData(data0);

        body.setLayout(new GridLayout());

        label = new Label(body, SWT.LEFT);

        GridData data1 = new GridData();
        data1.grabExcessHorizontalSpace = true;
        data1.grabExcessVerticalSpace = true;
        data1.horizontalAlignment = SWT.FILL;
        data1.verticalAlignment = SWT.FILL;
        label.setLayoutData(data1);

        final Text text = new Text(body, SWT.SINGLE | SWT.BORDER);

        GridData data2 = new GridData();
        data2.grabExcessHorizontalSpace = true;
        data2.horizontalAlignment = SWT.FILL;
        text.setLayoutData(data2);

        if (sDefaultText != null)
        {
            text.setText(sDefaultText);
        }

        Composite footer = new Composite(shell, SWT.NONE);

        GridData data3 = new GridData();
        data3.grabExcessHorizontalSpace = true;
        data3.horizontalAlignment = SWT.FILL;
        data3.horizontalSpan = 2;
        footer.setLayoutData(data3);

        RowLayout layout = new RowLayout();
        layout.justify = true;
        layout.fill = true;
        footer.setLayout(layout);

        Button cancel = new Button(footer, SWT.PUSH);
        cancel.setText("  Cancel  ");
        cancel.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent e)
                {
                    shell.dispose();
                }
            });

        Button ok = new Button(footer, SWT.PUSH);
        ok.setText("    OK    ");
        ok.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent e)
                {
                    result = text.getText();

                    shell.dispose();
                }
            });
        shell.setDefaultButton(ok);
    }

    /**
     * DOCUMENTME.
     *
     * @return  DOCUMENTME
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * DOCUMENTME.
     *
     * @return  DOCUMENTME
     */
    public String open()
    {
        shell.pack();
        shell.open();
        shell.layout();

        while (!shell.isDisposed())
        {
            if (!shell.getDisplay().readAndDispatch())
            {
                shell.getDisplay().sleep();
            }
        }

        return result;
    }

    /**
     * DOCUMENTME.
     *
     * @param  message  DOCUMENTME
     */
    public void setMessage(String message)
    {
        if (message == null)
        {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }

        this.message = message;
        label.setText(message);
    }

    /**
     * DOCUMENTME.
     *
     * @param  string  DOCUMENTME
     */
    @Override public void setText(String string)
    {
        super.setText(string);

        shell.setText(string);
    }

    /**
     * DOCUMENTME.
     *
     * @param   style  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    protected static int checkImageStyle(int style)
    {
        if ((style & SWT.ICON_ERROR) == SWT.ICON_ERROR)
        {
            return SWT.ICON_ERROR;
        }
        else if ((style & SWT.ICON_INFORMATION) == SWT.ICON_INFORMATION)
        {
            return SWT.ICON_INFORMATION;
        }
        else if ((style & SWT.ICON_QUESTION) == SWT.ICON_QUESTION)
        {
            return SWT.ICON_QUESTION;
        }
        else if ((style & SWT.ICON_WARNING) == SWT.ICON_WARNING)
        {
            return SWT.ICON_WARNING;
        }
        else if ((style & SWT.ICON_WORKING) == SWT.ICON_WORKING)
        {
            return SWT.ICON_WORKING;
        }

        return SWT.NONE;
    }

    /**
     * DOCUMENTME.
     *
     * @param   style  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    protected static int checkStyle(int style)
    {
        if ((style & SWT.SYSTEM_MODAL) == SWT.SYSTEM_MODAL)
        {
            return SWT.SYSTEM_MODAL;
        }
        else if ((style & SWT.PRIMARY_MODAL) == SWT.PRIMARY_MODAL)
        {
            return SWT.PRIMARY_MODAL;
        }
        else if ((style & SWT.APPLICATION_MODAL) == SWT.APPLICATION_MODAL)
        {
            return SWT.APPLICATION_MODAL;
        }

        return SWT.APPLICATION_MODAL;
    }
}
