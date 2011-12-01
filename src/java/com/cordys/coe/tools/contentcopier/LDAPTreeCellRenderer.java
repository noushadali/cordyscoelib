/**
 *  2003 Cordys R&D B.V. All rights reserved.     The computer program(s) is
 * the proprietary information of Cordys R&D B.V.     and provided under the
 * relevant License Agreement containing restrictions     on use and
 * disclosure. Use is subject to the License Agreement.
 */
package com.cordys.coe.tools.contentcopier;

import com.cordys.coe.util.general.Util;
import com.cordys.coe.util.general.ldap.LDAPUtils;

import com.novell.ldap.LDAPEntry;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;

import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Created by IntelliJ IDEA. User: pgussow Date: 18-aug-2003 Time: 9:49:15 To change this template
 * use Options | File Templates.
 */
public class LDAPTreeCellRenderer extends DefaultTreeCellRenderer
{
    /**
     * DOCUMENTME.
     */
    private ImageIcon iiACL;
    /**
     * DOCUMENTME.
     */
    private ImageIcon iiAuthUser;
    /**
     * DOCUMENTME.
     */
    private ImageIcon iiConPoint;
    /**
     * DOCUMENTME.
     */
    private ImageIcon iiDefault;
    /**
     * DOCUMENTME.
     */
    private ImageIcon iiMethod;
    /**
     * DOCUMENTME.
     */
    private ImageIcon iiMethodSet;
    /**
     * DOCUMENTME.
     */
    private ImageIcon iiOrganization;
    /**
     * DOCUMENTME.
     */
    private ImageIcon iiOrgUser;
    /**
     * DOCUMENTME.
     */
    private ImageIcon iiProcessor;
    /**
     * DOCUMENTME.
     */
    private ImageIcon iiRole;
    /**
     * DOCUMENTME.
     */
    private ImageIcon iiSoapNode;
    /**
     * DOCUMENTME.
     */
    private ImageIcon iiSoftwarePackages;

    /**
     * Creates the cellrenderer. It pre-loads all the images.
     */
    public LDAPTreeCellRenderer()
    {
        iiDefault = loadImage("default.gif");
        iiOrganization = loadImage("organization.gif");
        iiSoapNode = loadImage("soapnode.gif");
        iiProcessor = loadImage("soapprocessor.gif");
        iiConPoint = loadImage("connectionpoint.gif");
        iiMethodSet = loadImage("methodset.gif");
        iiMethod = loadImage("method.gif");
        iiAuthUser = loadImage("authenticateduser.gif");
        iiOrgUser = loadImage("organizationaluser.gif");
        iiRole = loadImage("organizationalrole.gif");
        iiSoftwarePackages = loadImage("softwarepackages.gif");
        iiACL = loadImage("acs.gif");
    }

    /**
     * Determine the icon to use.
     *
     * @param   entry  The LDAPEntry to determine the icon for.
     *
     * @return  The icon to use.
     */
    public ImageIcon getItemIcon(LDAPEntry entry)
    {
        ImageIcon iReturn = null;

        if (LDAPUtils.checkAttriValueExists(entry, "objectclass", "busorganizationalrole"))
        {
            iReturn = iiRole;
        }
        else if (LDAPUtils.checkAttriValueExists(entry, "objectclass", "organization"))
        {
            iReturn = iiOrganization;
        }
        else if (LDAPUtils.checkAttriValueExists(entry, "objectclass", "busauthenticationuser"))
        {
            iReturn = iiAuthUser;
        }
        else if (LDAPUtils.checkAttriValueExists(entry, "objectclass", "busmethodset"))
        {
            iReturn = iiMethodSet;
        }
        else if (LDAPUtils.checkAttriValueExists(entry, "objectclass", "busmethod"))
        {
            iReturn = iiMethod;
        }
        else if (LDAPUtils.checkAttriValueExists(entry, "objectclass", "bussoapnode"))
        {
            iReturn = iiSoapNode;
        }
        else if (LDAPUtils.checkAttriValueExists(entry, "objectclass", "bussoapprocessor"))
        {
            iReturn = iiProcessor;
        }
        else if (LDAPUtils.checkAttriValueExists(entry, "objectclass", "busconnectionpoint"))
        {
            iReturn = iiConPoint;
        }
        else if (LDAPUtils.checkAttriValueExists(entry, "objectclass", "groupOfNames"))
        {
            iReturn = iiSoftwarePackages;
        }
        else if (LDAPUtils.checkAttriValueExists(entry, "objectclass", "busorganizationaluser"))
        {
            iReturn = iiOrgUser;
        }
        else if (LDAPUtils.checkAttriValueExists(entry, "objectclass", "busaccesscontrolset"))
        {
            iReturn = iiACL;
        }
        else
        {
            iReturn = iiDefault;
        }

        return iReturn;
    } // getTreeCellRendererComponent

    /**
     * This method returns the component to use.
     *
     * @param   tree
     * @param   value
     * @param   sel
     * @param   expanded
     * @param   leaf
     * @param   row
     * @param   hasFocus
     *
     * @return  The component to use.
     */
    @Override public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
                                                            boolean expanded, boolean leaf, int row,
                                                            boolean hasFocus)
    {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        if (value instanceof LDAPTreeNode)
        {
            ImageIcon iUsedIcon = getItemIcon(((LDAPTreeNode) value).getLDAPEntry());

            if (iUsedIcon != null)
            {
                setIcon(iUsedIcon);
            }
        }
        return this;
    } // getTreeCellRendererComponent

    /**
     * This method loads the icon.
     *
     * @param   sName  The name of the image.
     *
     * @return  The actual image.
     */
    private ImageIcon loadImage(String sName)
    {
        ImageIcon iiReturn = null;

        if (Util.DEBUG)
        {
            System.out.println("Loading " + sName + ".");
        }
        iiReturn = new ImageIcon(LDAPTreeCellRenderer.class.getResource(sName), "none");

        return iiReturn;
    }
} // LDAPTreeCellRenderer
