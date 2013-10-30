package com.cordys.coe.tools.log4j;

import com.cordys.coe.util.swt.SWTResourceManager;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.LogLog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

/**
 * This class is a pure Log4J viewer. It has no dependencies to the Cordys jars. It is based on the old Event Service Client
 * 
 * @author pgussow
 */
public class Log4JViewer
{
    /**
     * The shell for this application.
     */
    protected Shell sShell;
    /**
     * Holds the composite that will receive the actual Log4J events.
     */
    private ILog4JComposite m_lcLog4J;

    /**
     * Main method. This method starts the event service.
     * 
     * @param saArgs the commandline arguments.
     */
    public static void main(String[] saArgs)
    {
        try
        {
            LogLog.setQuietMode(true);
            BasicConfigurator.configure();
            Logger.getRootLogger().setLevel(Level.WARN);

            Log4JViewer window = new Log4JViewer();
            window.open();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * This method opens the actual window. First a LDAPLogin screen is shown to log on to a specific Cordys server. Then the
     * window is cerated and built up.
     */
    public void open()
    {
        sShell = new Shell();
        sShell.setImage(SWTResourceManager.getImage(Log4JViewer.class, "cordyslog4j.gif"));
        sShell.addShellListener(new ShellAdapter() {
            public void shellClosed(ShellEvent e)
            {
                exitForm();
            }
        });

        final Display display = Display.getDefault();

        String sShellName = "Cordys Log4J Viewer";

        // Create the controls for this application.
        createContents();

        // Set the title bar.
        sShell.setText(sShellName + " " + m_lcLog4J.getTitle());

        // Show the screen
        sShell.open();
        sShell.layout();

        display.addFilter(SWT.KeyDown, new Listener() {
            @Override
            public void handleEvent(Event e)
            {
                if ((((e.stateMask & SWT.ALT) == SWT.ALT) && (e.keyCode == 'c')) && (m_lcLog4J != null))
                {
                    m_lcLog4J.clearCurrentView();
                }
            }
        });

        while (!sShell.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }
    }

    /**
     * This method creates all the controls for the window.
     */
    protected void createContents()
    {
        sShell.setLayout(new FillLayout());
        sShell.setSize(991, 647);
        sShell.setText("CoE Log4J Viewer");

        // Create the menu bar.
        final Menu mMenuBar = new Menu(sShell, SWT.BAR);
        sShell.setMenuBar(mMenuBar);

        // shell.setMenu(mMenuBar);
        final MenuItem miFile = new MenuItem(mMenuBar, SWT.CASCADE);
        miFile.setText("&File");

        final Menu menu_1 = new Menu(miFile);
        miFile.setMenu(menu_1);

        final MenuItem miLoadCordys = new MenuItem(menu_1, SWT.NONE);
        miLoadCordys.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e)
            {
                m_lcLog4J.loadFromFile(ILog4JComposite.TYPE_CORDYS_SPY);
            }
        });
        miLoadCordys.setText("Load &Cordys File");

        final MenuItem miLoadLog4J = new MenuItem(menu_1, SWT.NONE);
        miLoadLog4J.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e)
            {
                m_lcLog4J.loadFromFile(ILog4JComposite.TYPE_LOG4J);
            }
        });
        miLoadLog4J.setText("&Load Log4J XML File");

        new MenuItem(menu_1, SWT.SEPARATOR);

        final MenuItem miAddListener = new MenuItem(menu_1, SWT.NONE);
        miAddListener.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e)
            {
                m_lcLog4J.startLog4JListening();
            }
        });
        miAddListener.setText("&Add Log4J Socket listener");

        new MenuItem(menu_1, SWT.SEPARATOR);

        // Save button
        final MenuItem miSaveLog = new MenuItem(menu_1, SWT.NONE);
        miSaveLog.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e)
            {
                m_lcLog4J.saveLog4JLog();
            }
        });
        miSaveLog.setText("&Save Log4J log");

        new MenuItem(menu_1, SWT.SEPARATOR);

        final MenuItem miExit = new MenuItem(menu_1, SWT.NONE);
        miExit.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e)
            {
                System.exit(0);
            }
        });
        miExit.setText("E&xit");

        try
        {
            m_lcLog4J = new Log4JComposite(sShell, true);
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
            exitForm();
        }
    }

    /**
     * This method exits the application.
     */
    private void exitForm()
    {
        m_lcLog4J.closeConnections();
        System.exit(0);
    }
}
