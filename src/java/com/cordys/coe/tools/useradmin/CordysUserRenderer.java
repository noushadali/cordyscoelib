/**
 *      © 2003 Cordys R&D B.V. All rights reserved.
 *      The computer program(s) is the proprietary information of Cordys R&D B.V.
 *      and provided under the relevant License Agreement containing restrictions
 *      on use and disclosure. Use is subject to the License Agreement.
 */
package com.cordys.coe.tools.useradmin;

import java.awt.*;

import java.net.URL;

import javax.swing.*;

import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * class to show different icons for an user and an organization in a tree object.
 *
 * @author  gjlubber
 */
public class CordysUserRenderer extends DefaultTreeCellRenderer
{
    /**
     * holds the icons.
     */
    ImageIcon orgicon;
    /**
     * DOCUMENTME.
     */
    ImageIcon roleicon;
    /**
     * DOCUMENTME.
     */
    ImageIcon usericon;

    /**
     * Creates a new instance of MyRenderer it wil initalize the both images.
     */
    public CordysUserRenderer()
    {
        URL orgIconURL = ClassLoader.getSystemResource("com/cordys/coe/tools/useradmin/organization.gif");
        URL userIconURL = ClassLoader.getSystemResource("com/cordys/coe/tools/useradmin/organizationaluser.gif");
        URL roleIconURL = ClassLoader.getSystemResource("com/cordys/coe/tools/useradmin/organizationalrole.gif");

        orgicon = new ImageIcon(orgIconURL, "none");
        usericon = new ImageIcon(userIconURL, "none");
        roleicon = new ImageIcon(roleIconURL, "none");
    }

    /**
     * check wether the object is an organization or not.
     *
     * @param   value  the ocject to check
     *
     * @return  gives true is the object is an organization
     */
    public ImageIcon getItemIcon(Object value)
    {
        ImageIcon iReturn = null;

        if (value instanceof CordysTreeNode)
        {
            CordysTreeNode dmtnNode = (CordysTreeNode) value;

            if (dmtnNode.isUser())
            {
                iReturn = usericon;
            }

            if (dmtnNode.isOrganizationRole())
            {
                iReturn = roleicon;
            }

            if (dmtnNode.isOrganization() || dmtnNode.isOrganizationUnit())
            {
                iReturn = orgicon;
            }

            if (dmtnNode.isRoot())
            {
                iReturn = orgicon;
            }
        }

        return iReturn;
    }

    /**
     * overrule the component getTreeCellRendererComponent check if it is een organization then
     * assign the icon to it otherwise assign user icon.
     *
     * @param   tree      tree
     * @param   value     value
     * @param   sel       sel
     * @param   expanded  expanded
     * @param   leaf      leaf
     * @param   row       row
     * @param   hasFocus  hasFocus
     *
     * @return  this
     */

    @Override public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
                                                            boolean expanded, boolean leaf, int row,
                                                            boolean hasFocus)
    {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        ImageIcon iUsedIcon = getItemIcon(value);

        if (iUsedIcon != null)
        {
            setIcon(iUsedIcon);
        }
        return this;
    }
}
