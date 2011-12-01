/**
 *      
 * � 2003 Cordys R&D B.V. All rights reserved.
 *      The computer program(s) is the proprietary information of Cordys R&D B.V.
 *      and provided under the relevant License Agreement containing restrictions
 *      on use and disclosure. Use is subject to the License Agreement.
 */
package com.cordys.coe.tools.contentcopier;

import com.cordys.coe.exception.GeneralException;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;

import java.awt.BorderLayout;

import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import javax.swing.border.EtchedBorder;

/**
 * Class for LDAP panel.
 */
public class LDAPPanel extends JPanel
{
    /**
     * Holds the Drag 'N Drop tree with LDAPentries.
     */
    private DNDLDAPTree dndTree;
    /**
     * Holds the label of the panel.
     */
    private JLabel lLabel;
    /**
     * The lower panel in the scrollbar.
     */
    private JScrollPane spLower;
    /**
     * The splitter.
     */
    private JSplitPane spSplitter;
    /**
     * The upper panel in the scrollbar.
     */
    private JScrollPane spUpper;
    /**
     * Holds the details of an entry.
     */
    private JTable tblDetails;
    /**
     * Holds the complete DN of an entry.
     */
    private JTextField tfCompleteDN;

    /**
     * Constructor.
     *
     * @throws  GeneralException  DOCUMENTME
     */
    public LDAPPanel()
              throws GeneralException
    {
        super();

        setLayout(new BorderLayout());

        lLabel = new JLabel("LDAP Server: ");
        add(lLabel, BorderLayout.NORTH);

        LDAPTreeModel ltm = new LDAPTreeModel(new Object[] { "Property", "Value" }, 1);
        ltm.setEditable(false);
        tblDetails = new JTable(ltm);

        dndTree = new LDAPPanelTree();

        spUpper = new JScrollPane();
        spUpper.setViewportView(dndTree);
        spLower = new JScrollPane();
        spLower.setViewportView(tblDetails);

        spSplitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        spSplitter.setLeftComponent(spUpper);
        spSplitter.setRightComponent(spLower);

        add(spSplitter, BorderLayout.CENTER);

        tfCompleteDN = new JTextField("");
        tfCompleteDN.setEditable(false);
        tfCompleteDN.setBorder(new EtchedBorder());
        add(tfCompleteDN, BorderLayout.SOUTH);
    } // LDAPPanel

    /**
     * Returns the tree.
     *
     * @return  The tree.
     */
    public DNDLDAPTree getDndTree()
    {
        return dndTree;
    } // getDndTree

    /**
     * Returns the table with the entry-details.
     *
     * @return  The table with the entry-details.
     */
    public JTable getTblDetails()
    {
        return tblDetails;
    } // getTblDetails

    /**
     * Inits the tree.
     *
     * @param   lCon     DOCUMENTME
     * @param   sBaseDN  DOCUMENTME
     *
     * @throws  GeneralException  DOCUMENTME
     */
    public void initTree(LDAPConnection lCon, String sBaseDN)
                  throws GeneralException
    {
        dndTree.initializeTree(lCon, sBaseDN, ContentCopierTreeNode.class);
    } // initTree

    /**
     * Adapter method.
     *
     * @param  ltnNode  The selected node.
     */
    public void onSelectNode(LDAPTreeNode ltnNode)
    {
        LDAPEntry entry = ltnNode.getLDAPEntry();
        tfCompleteDN.setText(entry.getDN());

        // Display the details in the table.
        LDAPTreeModel dtm = (LDAPTreeModel) tblDetails.getModel();

        while (dtm.getRowCount() > 0)
        {
            dtm.removeRow(0);
        }

        LDAPAttributeSet las = entry.getAttributeSet();
        Iterator<?> iAttributes = las.iterator();

        while (iAttributes.hasNext())
        {
            LDAPAttribute attr = (LDAPAttribute) iAttributes.next();
            String[] saValues = attr.getStringValueArray();

            for (int iCount = 0; iCount < saValues.length; iCount++)
            {
                String value = saValues[iCount];
                dtm.addRow(new Object[] { attr.getName(), value });
            }
        }
    } // onSelectNode

    /**
     * Sets the title of the panel.
     *
     * @param  sTitle  The new title.
     */
    public void setTitle(String sTitle)
    {
        lLabel.setText(sTitle);
    } // setTitle

    /**
     * Local subclass of the DNDLDAPTree.
     */
    public class LDAPPanelTree extends DNDLDAPTree
    {
        /**
         * Default Constructor.
         *
         * @throws  GeneralException  DOCUMENTME
         */
        public LDAPPanelTree()
                      throws GeneralException
        {
            super();
        } // DNDLDAPTree

        /**
         * Constructor.
         *
         * @param   lCon       The connection to LDAP.
         * @param   sBaseDN    The Base DN to use.
         * @param   cTreeNode  The Class-object for the treenodes.
         *
         * @throws  GeneralException  DOCUMENTME
         */
        public LDAPPanelTree(LDAPConnection lCon, String sBaseDN,
                             Class<ContentCopierTreeNode> cTreeNode)
                      throws GeneralException
        {
            super(lCon, sBaseDN, cTreeNode);
        } // DNDLDAPTree

        /**
         * Adapter method. Gets called when a node is selected.
         *
         * @param  ltnNode  The selected node
         */
        @Override public void onSelectNode(LDAPTreeNode ltnNode)
        {
            LDAPPanel.this.onSelectNode(ltnNode);
        } // onSelectNode
    } // LDAPPanelTree
} // LDAPPanel
