package com.cordys.coe.util.cgc;

import javax.swing.UIManager;

import com.cordys.coe.util.swing.CGCLoginDialog;
import com.cordys.coe.util.swing.MessageBoxUtil;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;

/**
 * @author pgussow
 *
 */
public class TestCGCUsage
{
	/**
	 * Main method.
	 *
	 * @param saArguments The commandline arguments.
	 */
	public static void main(String[] saArguments)
	{
		try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            CGCLoginDialog cld = new CGCLoginDialog(null, true);
            cld.setVisible(true);
            
            ICordysGatewayClient cgc = cld.getConnection();
            
            LDAPEntry[] a = cgc.searchLDAP("cn=cordys,o=cordyslab.com", LDAPConnection.SCOPE_SUB, "(objectclass=organization)");
            for (LDAPEntry entry : a)
			{
				System.out.println(entry);
			}
            
            System.out.println(cgc.readLDAPEntry("o=system,cn=cordys,o=cordyslab.com"));
        }
        catch (Exception e)
        {
            MessageBoxUtil.showError("Error starting Organization Manager Screen", e);
        }
        
        System.exit(0);
	}
}
