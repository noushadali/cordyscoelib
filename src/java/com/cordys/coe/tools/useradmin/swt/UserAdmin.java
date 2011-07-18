package com.cordys.coe.tools.useradmin.swt;

import com.cordys.coe.util.config.IConfiguration;
import com.cordys.coe.util.connection.CordysConnectionFactory;
import com.cordys.coe.util.connection.ICordysConnection;
import com.cordys.coe.util.swt.CordysLogin;
import com.cordys.coe.util.swt.MessageBoxUtil;
import com.cordys.coe.util.swt.SWTResourceManager;
import com.cordys.coe.util.system.SystemInfo;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

/**
 * This program can do the user management for Cordys users. You can add/delete/modify authenticated
 * users and organizational users.
 *
 * @author  pgussow
 */
public class UserAdmin
{
    /**
     * Holds the logger to use.
     */
    private static final Logger LOG = Logger.getLogger(UserAdmin.class);
    /**
     * The SWT shell.
     */
    protected Shell sShell;
    /**
     * DOCUMENTME.
     */
    private UserAdminComposite m_uacUserAdmin;

    /**
     * Main method. Starts the useradmin program.
     *
     * @param  args  Commandline arguments.
     */
    public static void main(String[] args)
    {
        try
        {
            BasicConfigurator.configure();
            Logger.getRootLogger().setLevel(Level.WARN);
            Logger.getLogger("com.cordys.coe.tools.useradmin.swt").setLevel(Level.INFO);

            System.out.println(SystemInfo.getSystemInformation());

            UserAdmin window = new UserAdmin();
            window.open();
        }
        catch (Exception e)
        {
            LOG.error("General error", e);
        }
    }

    /**
     * This method shows the form.
     */
    public void open()
    {
        sShell = new Shell();
        sShell.setImage(SWTResourceManager.getImage(UserAdmin.class, "useradmin.gif"));
        sShell.addShellListener(new ShellAdapter()
            {
                public void shellClosed(ShellEvent e)
                {
                    exitForm();
                }
            });

        // First log on to LDAP
        CordysLogin llLogin = new CordysLogin(sShell, SWT.NONE);
        llLogin.open();

        if (llLogin.isOk())
        {
            // Create the connection to Cordys.
            IConfiguration cConfig = llLogin.getConfiguration();
            ICordysConnection ccConnection = null;

            try
            {
                ccConnection = CordysConnectionFactory.createCordysConnection(cConfig, null, true);
            }
            catch (Exception e)
            {
                LOG.error("General error", e);
                MessageBoxUtil.showError(sShell, "Error opening connection. The program will exit.",
                                         e);
                System.exit(1);
            }

            final Display display = Display.getDefault();
            createContents(ccConnection);

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
    }

    /**
     * This method creates all the needed controls.
     *
     * @param  ccConnection
     */
    protected void createContents(ICordysConnection ccConnection)
    {
        sShell.setLayout(new FillLayout());
        sShell.setSize(1024, 768);
        sShell.setText("Cordys user manager");

        final Composite composite = new Composite(sShell, SWT.NONE);
        composite.setLayout(new FillLayout());

        m_uacUserAdmin = new UserAdminComposite(composite, SWT.NONE, null);

        m_uacUserAdmin.setCordysConnection(ccConnection);

        final Menu menu = new Menu(sShell, SWT.BAR);
        sShell.setMenuBar(menu);

        final MenuItem fileMenuItem = new MenuItem(menu, SWT.CASCADE);
        fileMenuItem.setAccelerator(SWT.ALT | 'f');
        fileMenuItem.setText("File");

        final Menu menu_1 = new Menu(fileMenuItem);
        fileMenuItem.setMenu(menu_1);

        final MenuItem miNewAuthUser = new MenuItem(menu_1, SWT.NONE);
        miNewAuthUser.setAccelerator(SWT.ALT | 'n');
        miNewAuthUser.setText("New Authenticated user");
        miNewAuthUser.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent e)
                {
                    m_uacUserAdmin.showNewAuthScreen();
                }
            });

        new MenuItem(menu_1, SWT.SEPARATOR);

        final MenuItem miExit = new MenuItem(menu_1, SWT.NONE);
        miExit.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent e)
                {
                    exitForm();
                }
            });
        miExit.setAccelerator(SWT.ALT | 'x');
        miExit.setText("Exit");
    }

    /**
     * This method exits the application.
     */
    private void exitForm()
    {
        System.exit(0);
    }
}
