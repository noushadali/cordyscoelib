package com.cordys.coe.tools.es.swt;

import com.cordys.coe.util.general.Util;
import com.cordys.coe.util.swt.LDAPLogin;
import com.cordys.coe.util.swt.SWTResourceManager;

import com.eibus.connector.nom.Connector;

import com.eibus.directory.soap.LDAPDirectory;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * This tool can function as a client to the event service. You can subscribe to any subject and the
 * events will be shown.
 *
 * @author  pgussow
 */
public class EventServiceClient
{
    /**
     * Holds the name of the connector to open.
     */
    private static final String CONNECTOR_NAME = "ES Client";
    /**
     * The shell for this application.
     */
    protected Shell sShell;
    /**
     * Holds the composite that contains the actual display.
     */
    private ESCComposite m_ecESC;

    /**
     * Main method. This method starts the event service.
     *
     * @param  saArgs  the commandline arguments.
     */
    public static void main(String[] saArgs)
    {
        try
        {
            BasicConfigurator.configure();
            Logger.getRootLogger().setLevel(Level.WARN);

            EventServiceClient window = new EventServiceClient();
            window.open();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * This method opens the actual window. First a LDAPLogin screen is shown to log on to a
     * specific Cordys server. Then the window is cerated and built up.
     */
    public void open()
    {
        sShell = new Shell();
        sShell.setImage(SWTResourceManager.getImage(EventServiceClient.class, "esc.gif"));
        sShell.addShellListener(new ShellAdapter()
            {
                public void shellClosed(ShellEvent e)
                {
                    exitForm();
                }
            });

        // First log on to LDAP
        LDAPLogin llLogin = new LDAPLogin(sShell, SWT.NONE);
        llLogin.open();

        final Display display = Display.getDefault();

        String sShellName = "Event Service Client";
        Connector cCon = null;

        if (llLogin.isOk())
        {
            // Open the client connector
            try
            {
                cCon = Connector.getInstance(CONNECTOR_NAME);

                if (!cCon.isOpen())
                {
                    cCon.open();
                }

                LDAPDirectory ldDirectory = cCon.getMiddleware().getDirectory();

                sShellName += ("(" + ldDirectory.getConnection().getHost() + ":" +
                               ldDirectory.getConnection().getPort() + " Middleware: " +
                               cCon.getMiddleware().getAddress() + ")");
            }
            catch (Exception e)
            {
                showError("Error opening connector:\n" + Util.getStackTrace(e));
            }
        }

        createContents(cCon);

        sShell.addControlListener(new ControlAdapter()
            {
                public void controlResized(ControlEvent ceEvent)
                {
                    if (m_ecESC != null)
                    {
                        m_ecESC.calculateNewSizes(ceEvent);
                    }
                }
            });

        // Set the title bar.
        sShell.setText(sShellName + " " + m_ecESC.getTitle());

        // Show the screen
        sShell.open();
        sShell.layout();

        while (!sShell.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }
    }

    /**
     * This method shows an error dialog to the end user.
     *
     * @param  sMessage  the message to display.
     */
    public void showError(String sMessage)
    {
        MessageBox mb = new MessageBox(sShell, SWT.ICON_ERROR | SWT.OK);
        mb.setMessage(sMessage);
        mb.setText("Error");
        mb.open();
    }

    /**
     * This method shows an information dialog to the end user.
     *
     * @param  sMessage  The message to display.
     */
    public void showInformation(String sMessage)
    {
        MessageBox mb = new MessageBox(sShell, SWT.ICON_INFORMATION | SWT.OK);
        mb.setMessage(sMessage);
        mb.setText("Information");
        mb.open();
    }

    /**
     * This method creates all the controls for the window.
     *
     * @param  cConnector  DOCUMENTME
     */
    protected void createContents(Connector cConnector)
    {
        sShell.setLayout(new FillLayout());
        sShell.setSize(991, 647);
        sShell.setText("SWT Application");

        // Create the menu bar.
        final Menu mMenuBar = new Menu(sShell, SWT.BAR);
        sShell.setMenuBar(mMenuBar);

        // shell.setMenu(mMenuBar);
        final MenuItem miFile = new MenuItem(mMenuBar, SWT.CASCADE);
        miFile.setText("&File");

        final Menu menu_1 = new Menu(miFile);
        miFile.setMenu(menu_1);

        final MenuItem miLoadCordys = new MenuItem(menu_1, SWT.NONE);
        miLoadCordys.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent e)
                {
                    m_ecESC.loadFromFile(ESCComposite.TYPE_CORDYS_SPY);
                }
            });
        miLoadCordys.setText("Load &Cordys File");

        final MenuItem miLoadLog4J = new MenuItem(menu_1, SWT.NONE);
        miLoadLog4J.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent e)
                {
                    m_ecESC.loadFromFile(ESCComposite.TYPE_LOG4J);
                }
            });
        miLoadLog4J.setText("&Load Log4J XML File");

        new MenuItem(menu_1, SWT.SEPARATOR);

        final MenuItem miAddListener = new MenuItem(menu_1, SWT.NONE);
        miAddListener.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent e)
                {
                    m_ecESC.startLog4JListening();
                }
            });
        miAddListener.setText("&Add Log4J Socket listener");

        new MenuItem(menu_1, SWT.SEPARATOR);

        // Save button
        final MenuItem miSaveLog = new MenuItem(menu_1, SWT.NONE);
        miSaveLog.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent e)
                {
                    m_ecESC.saveLog4JLog();
                }
            });
        miSaveLog.setText("&Save Log4J log");

        new MenuItem(menu_1, SWT.SEPARATOR);

        final MenuItem miExit = new MenuItem(menu_1, SWT.NONE);
        miExit.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent e)
                {
                    System.exit(0);
                }
            });
        miExit.setText("E&xit");

        try
        {
            m_ecESC = new ESCComposite(sShell, cConnector);
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
        m_ecESC.closeConnections();
        System.exit(0);
    }
}
