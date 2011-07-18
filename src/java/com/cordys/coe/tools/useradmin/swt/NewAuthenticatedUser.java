package com.cordys.coe.tools.useradmin.swt;

import com.cordys.coe.util.connection.CordysConnectionException;
import com.cordys.coe.util.connection.ICordysConnection;
import com.cordys.coe.util.general.ldap.LDAPUtils;
import com.cordys.coe.util.swt.MessageBoxUtil;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPModification;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * This dialog can be used to add a new authenticated user.
 *
 * @author  pgussow
 */
public class NewAuthenticatedUser extends Dialog
{
    /**
     * The current shell.
     */
    protected Shell shell;
    /**
     * Holds the combo box with all organizations.
     */
    private Combo cOrganization;
    /**
     * Holds the LDAPConnection.
     */
    private ICordysConnection m_ccConnection;
    /**
     * Holds the DN under which the authenticated users are stored.
     */
    private String sAuthUserDN;
    /**
     * Holds the desired full name.
     */
    private Text tfFullName;
    /**
     * Holds the name for the new user.
     */
    private Text tfName;
    /**
     * Holds the desired OS identity.
     */
    private Text tfOSIdentity;

    /**
     * Creates a new NewAuthenticatedUser object.
     *
     * @param  sParent  The parent shell.
     * @param  lCon     The LDAP connection.
     */
    public NewAuthenticatedUser(Shell sParent, ICordysConnection lCon)
    {
        this(sParent, SWT.NONE, lCon);
    }

    /**
     * Creates a new LDAPNewAuthenticatedUserLogin object.
     *
     * @param  sParent  The parent shell.
     * @param  iStyle   The SWT style.
     * @param  lCon     The LDAP connection.
     */
    public NewAuthenticatedUser(Shell sParent, int iStyle, ICordysConnection lCon)
    {
        super(sParent, iStyle);
        this.m_ccConnection = lCon;
    }

    /**
     * Returns the LDAPConnection.
     *
     * @return  The LDAPConnection.
     */
    public ICordysConnection getCordysConnection()
    {
        return m_ccConnection;
    }

    /**
     * This method shows the screen.
     *
     * @return  the return object.
     */
    public Object open()
    {
        Shell parent = getParent();
        shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        shell.setText(getText());

        createContents();

        fillOrganizationComboBox();

        // Find the DN where the authenticated users are stored.
        try
        {
            sAuthUserDN = findAuthUserDN();
        }
        catch (Exception exc)
        {
            MessageBoxUtil.showError(shell, "Error retrieving the base-DN for authenticated users:",
                                     exc);
            sAuthUserDN = null;
        }

        shell.open();
        shell.layout();

        Display display = parent.getDisplay();

        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }
        return null;
    }

    /**
     * This method adds a new authenticated user to LDAP.
     */
    protected void createAuthenticatedUser()
    {
        String sName = tfName.getText();
        String sFullName = tfFullName.getText();
        String sOSIdentity = tfOSIdentity.getText();
        String sOrgDesc = cOrganization.getText();
        String sFullOrg = ((CBOrgEntry) cOrganization.getData(sOrgDesc)).getEntry().getDN();

        if ((sName == null) || sName.equals("") || (sFullName == null) || sFullName.equals("") ||
                (sOSIdentity == null) || sOSIdentity.equals("") || (sOrgDesc == null) ||
                (sOrgDesc.length() == 0))
        {
            MessageBoxUtil.showError(getParent(), "Not all fields are filled.");
        }
        else
        {
            // All data is valid, add the user.
            if ((sAuthUserDN != null) && !sAuthUserDN.equals(""))
            {
                LDAPAttributeSet asAttrs = new LDAPAttributeSet();
                LDAPAttribute laTemp = new LDAPAttribute("objectclass", "top");
                laTemp.addValue("busauthenticationuser");
                asAttrs.add(laTemp);

                asAttrs.add(new LDAPAttribute("cn", sName));
                asAttrs.add(new LDAPAttribute("description", sFullName));
                asAttrs.add(new LDAPAttribute("osidentity", sOSIdentity));
                asAttrs.add(new LDAPAttribute("defaultcontext", sFullOrg));

                LDAPEntry newEntry = new LDAPEntry("cn=" + sName + "," + sAuthUserDN, asAttrs);

                // Add the entry to LDAP
                try
                {
                    m_ccConnection.changeLDAPEntry(newEntry, LDAPModification.ADD);

                    // Clear all input-fields
                    tfName.setText("");
                    // Set the focussed control to tfName
                    tfName.setFocus();
                }
                catch (Exception exc)
                {
                    MessageBoxUtil.showError(getParent(),
                                             "Error adding the authenticated user to LDAP.", exc);
                }
            }
            else
            {
                MessageBoxUtil.showError(getParent(),
                                         "Since no base-DN is available for storing the authenticated users the user could not be added.");
            }
        }
    }

    /**
     * This method creates the controls for this window.
     */
    protected void createContents()
    {
        shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        shell.setLayout(new FillLayout());
        shell.setSize(345, 185);
        shell.setText("New Authenticated user");

        final Group group = new Group(shell, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.marginRight = 5;
        gridLayout.marginLeft = 5;
        gridLayout.numColumns = 2;
        group.setLayout(gridLayout);

        final Label ldapServerLabel = new Label(group, SWT.NONE);
        ldapServerLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
        ldapServerLabel.setAlignment(SWT.RIGHT);
        ldapServerLabel.setText("Name:");

        tfName = new Text(group, SWT.BORDER);
        tfName.addModifyListener(new ModifyListener()
            {
                public void modifyText(ModifyEvent e)
                {
                    tfFullName.setText(tfName.getText());
                    tfOSIdentity.setText(tfName.getText());
                }
            });

        final GridData gridData_2 = new GridData(GridData.FILL, GridData.CENTER, false, false);
        gridData_2.widthHint = 162;
        tfName.setLayoutData(gridData_2);

        final Label fullNameLabel = new Label(group, SWT.NONE);
        fullNameLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
        fullNameLabel.setText("Full name:");

        tfFullName = new Text(group, SWT.BORDER);
        tfFullName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));

        final Label osIdentityLabel = new Label(group, SWT.NONE);
        osIdentityLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
        osIdentityLabel.setText("OS Identity:");

        tfOSIdentity = new Text(group, SWT.BORDER);
        tfOSIdentity.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));

        final Label defaultOrganizationLabel = new Label(group, SWT.NONE);
        defaultOrganizationLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false,
                                                            false));
        defaultOrganizationLabel.setText("Default organization:");

        cOrganization = new Combo(group, SWT.READ_ONLY);
        cOrganization.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));

        final Composite composite = new Composite(group, SWT.NONE);
        final GridLayout gridLayout_1 = new GridLayout();
        gridLayout_1.makeColumnsEqualWidth = true;
        gridLayout_1.numColumns = 4;
        composite.setLayout(gridLayout_1);

        final GridData gridData_1 = new GridData(GridData.FILL, GridData.CENTER, false, false, 2,
                                                 1);
        gridData_1.widthHint = 315;
        composite.setLayoutData(gridData_1);

        final Label label = new Label(composite, SWT.NONE);
        label.setText("");

        final Button bAdd = new Button(composite, SWT.NONE);
        bAdd.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent e)
                {
                    createAuthenticatedUser();
                }
            });
        bAdd.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
        bAdd.setText("&Add");

        final Button bCancel = new Button(composite, SWT.NONE);
        bCancel.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent e)
                {
                    shell.close();
                }
            });

        final GridData gridData = new GridData(GridData.END, GridData.CENTER, false, false);
        gridData.widthHint = 52;
        bCancel.setLayoutData(gridData);
        bCancel.setText("C&ancel");

        final Button bClose = new Button(composite, SWT.NONE);
        bClose.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent e)
                {
                    shell.close();
                }
            });
        bClose.setText("&Close");
        //
    }

    /**
     * This method retrieves all the organizations from LDAP. It then places all the found
     * organizations in the combobox cbOrganization. The object added is an instance of the
     * CBEntry-class to the combobox.
     */
    private void fillOrganizationComboBox()
    {
        try
        {
            // Clear the current data in the combobox.
            cOrganization.removeAll();

            LDAPEntry[] results = m_ccConnection.searchLDAP(m_ccConnection.getSearchRoot(),
                                                            LDAPConnection.SCOPE_SUB,
                                                            "objectclass=organization");

            for (int iCount = 0; iCount < results.length; iCount++)
            {
                CBOrgEntry oeEntry = new CBOrgEntry(results[iCount]);
                cOrganization.add(oeEntry.toString());
                cOrganization.setData(oeEntry.toString(), oeEntry);
            }

            // Set the selectedIndex.
            if (results.length > 0)
            {
                cOrganization.select(0);
            }
        }
        catch (Exception exc)
        {
            MessageBoxUtil.showError(getParent(), "Error filling the organizational combobox", exc);
        }
    }

    /**
     * This method searches LDAP for the base-DN to use to store authenticated users. The found DN
     * will be returned.
     *
     * @return  The base-DN where the authenticated users are stored.
     *
     * @throws  CordysConnectionException  DOCUMENTME
     */
    private String findAuthUserDN()
                           throws CordysConnectionException
    {
        String sReturn = null;

        LDAPEntry[] aeResults = m_ccConnection.searchLDAP(m_ccConnection.getSearchRoot(),
                                                          LDAPConnection.SCOPE_SUB,
                                                          "(&(objectclass=busauthenticatedusers)(cn=authenticated users))");

        if (aeResults.length > 0)
        {
            sReturn = aeResults[0].getDN();
        }

        return sReturn;
    }

    /**
     * Innerclass CBOrgEntry. This class is used to store items in the combobox so the LDAPEntry can
     * be saved.
     */
    private class CBOrgEntry
    {
        /**
         * Holds the LDAPEntry for this obejct.
         */
        private LDAPEntry entry;

        /**
         * Constructor.
         *
         * @param  entry  The LDAPEntry to use for the instance.
         */
        public CBOrgEntry(LDAPEntry entry)
        {
            this.entry = entry;
        }

        /**
         * This method returns the LDAPEntry for this object.
         *
         * @return  The LDAPEntry for this object.
         */
        public LDAPEntry getEntry()
        {
            return entry;
        }

        /**
         * This method returns the String-representation of the organization-entry in LDAP.
         *
         * @return  The String-representation of the organization-entry in LDAP.
         */
        @Override public String toString()
        {
            return LDAPUtils.getAttrValue(entry, "description");
        }
    }
}
