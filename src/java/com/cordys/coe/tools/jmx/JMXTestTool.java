package com.cordys.coe.tools.jmx;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This tool is used to view JMX data through Cordys glasses.
 *
 * @author  pgussow
 */
public class JMXTestTool
{
    /**
     * Holds teh shell for this application.
     */
    protected Shell m_sShell;

    /**
     * Launch the application.
     *
     * @param  args
     */
    public static void main(String[] args)
    {
        try
        {
            JMXTestTool window = new JMXTestTool();
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
        m_sShell.setImage(SWTResourceManager.getImage(JMXTestTool.class,
                                                      "/com/cordys/coe/tools/jmx/image/releng_gears.gif"));
        m_sShell.setLayout(new GridLayout());
        m_sShell.setSize(800, 600);
        m_sShell.setText("Cordys CoE JMX viewer");
        m_sShell.setMaximized(true);
        
        m_sShell.addShellListener(new ShellAdapter() {
            public void shellClosed(ShellEvent e)
            {
                System.exit(0);
            }
        });

        CordysCoEJMXViewerComposite ccjv = new CordysCoEJMXViewerComposite(m_sShell, SWT.NONE);
        ccjv.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    }
}
