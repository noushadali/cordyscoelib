/**
 *  2003 Cordys R&D B.V. All rights reserved.     The computer program(s) is
 * the proprietary information of Cordys R&D B.V.     and provided under the
 * relevant License Agreement containing restrictions     on use and
 * disclosure. Use is subject to the License Agreement.
 */
package com.cordys.coe.tools.useradmin;

import com.cordys.coe.util.general.ldap.LDAPUtils;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.io.IOException;

import java.util.HashMap;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JTree;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * this class is taking care of the drag and drop functionalities.
 *
 * @author  gjlubber
 */
public class DnDjTree extends JTree
    implements DropTargetListener, DragSourceListener, DragGestureListener, KeyListener,
               TreeSelectionListener
{
    /**
     * LDAPConnection Needed to add a Cordys treeNode.
     */
    protected LDAPConnection LDAPCon;
    /**
     * enables this component to be a Drag Source.
     */
    DragSource dragSource = null;
    /**
     * enables this component to be a dropTarget.
     */
    DropTarget dropTarget = null;
    /**
     * Holds the list of DN's that have to be updated.
     */
    private HashMap<String, String> hmDNsToBeUpdated;
    /**
     * DOCUMENTME.
     */
    private CordysTreeNode rootNode;

    /**
     * enables to create an instance of this class without the string param.
     *
     * @param  hmDNsToBeUpdated  DOCUMENTME
     */
    public DnDjTree(HashMap<String, String> hmDNsToBeUpdated)
    {
        this("root tree node", null, hmDNsToBeUpdated);
    }

    /**
     * Creates a new instance of DnDjTree.
     *
     * @param  oObject           the Object for the root
     * @param  LDAPCon           the connection to the LDAP
     * @param  hmDNsToBeUpdated  DOCUMENTME
     */
    public DnDjTree(Object oObject, LDAPConnection LDAPCon,
                    HashMap<String, String> hmDNsToBeUpdated)
    {
        super();
        this.LDAPCon = LDAPCon;
        this.hmDNsToBeUpdated = hmDNsToBeUpdated;

        if (oObject instanceof LDAPItemEntry)
        {
            rootNode = new CordysTreeNode(oObject, LDAPCon, hmDNsToBeUpdated);
        }

        setModel(new javax.swing.tree.DefaultTreeModel(rootNode));
        ((DefaultTreeModel) getModel()).reload();
        setShowsRootHandles(false);
        getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
        setFont(new java.awt.Font("Tahoma", 0, 10));

        dropTarget = new DropTarget(this, this);
        dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY, this);

        addKeyListener(this);

        addTreeSelectionListener(this);
    }

    /**
     * enables to call the additem method without a parrent.
     *
     * @param   oObject  the object to add to the tree
     *
     * @return  returns the added treenode. enables you to add a node to the new node
     */
    public CordysTreeNode addItem(Object oObject)
    {
        return addItem(oObject, null);
    }

    /**
     * Adds the object to the tree under the dmtnParent node. if dmtnParent is emty oObject wil be
     * the root
     *
     * @param   oObject     object to add to the tree
     * @param   dmtnParent  parent where the oObject should be placed under.
     *
     * @return  returns the added treenode. enables you to add a node to the new node
     */
    public CordysTreeNode addItem(Object oObject, CordysTreeNode dmtnParent)
    {
        CordysTreeNode ctnReturn = null;
        CordysTreeNode newNode = new CordysTreeNode(oObject, LDAPCon, hmDNsToBeUpdated);
        CordysTreeNode dmtnParentNode;

        if (dmtnParent == null)
        {
            dmtnParentNode = rootNode;
        }
        else
        {
            dmtnParentNode = dmtnParent;
        }

        boolean bExist = false;

        for (int iCount = 0; iCount < dmtnParentNode.getChildCount(); iCount++)
        {
            if (((CordysTreeNode) dmtnParentNode.getChildAt(iCount)).getUserObject().equals(newNode
                                                                                                .getUserObject()))
            {
                bExist = true;
            }
        }

        if (!bExist)
        {
            dmtnParentNode.add(newNode);
            ((DefaultTreeModel) getModel()).reload(dmtnParentNode);
            ctnReturn = newNode;
        }

        return ctnReturn;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  dragSourceDropEvent
     */
    public void dragDropEnd(java.awt.dnd.DragSourceDropEvent dragSourceDropEvent)
    {
    }

    /**
     * DOCUMENT ME!
     *
     * @param  dropTargetDragEvent
     */
    public void dragEnter(java.awt.dnd.DropTargetDragEvent dropTargetDragEvent)
    {
        dropTargetDragEvent.acceptDrag(DnDConstants.ACTION_MOVE);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  dragSourceDragEvent
     */
    public void dragEnter(java.awt.dnd.DragSourceDragEvent dragSourceDragEvent)
    {
    }

    /**
     * DOCUMENT ME!
     *
     * @param  dropTargetEvent
     */
    public void dragExit(java.awt.dnd.DropTargetEvent dropTargetEvent)
    {
    }

    /**
     * DOCUMENT ME!
     *
     * @param  dragSourceEvent
     */
    public void dragExit(DragSourceEvent dragSourceEvent)
    {
    }

    /**
     * DOCUMENT ME!
     *
     * @param  dragGestureEvent
     */
    public void dragGestureRecognized(java.awt.dnd.DragGestureEvent dragGestureEvent)
    {
    }

    /**
     * DOCUMENT ME!
     *
     * @param  dropTargetDragEvent
     */
    public void dragOver(java.awt.dnd.DropTargetDragEvent dropTargetDragEvent)
    {
    }

    /**
     * DOCUMENT ME!
     *
     * @param  dragSourceDragEvent
     */
    public void dragOver(java.awt.dnd.DragSourceDragEvent dragSourceDragEvent)
    {
    }

    /**
     * After a item is dropped.
     *
     * @param  dropTargetDropEvent
     */
    public void drop(DropTargetDropEvent dropTargetDropEvent)
    {
        try
        {
            boolean bContinue = true;
            Transferable transferable = dropTargetDropEvent.getTransferable();

            TreePath tTreePath = getClosestPathForLocation(dropTargetDropEvent.getLocation().x,
                                                           dropTargetDropEvent.getLocation().y);
            CordysTreeNode dmtnParentNode = null;

            Object oVectorObject = transferable.getTransferData(new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType));

            if (oVectorObject instanceof Vector<?>)
            {
                Vector<?> vVector = (Vector<?>) oVectorObject;

                for (int iCountB = 0; iCountB < vVector.size(); iCountB++)
                {
                    Object oObject = vVector.elementAt(iCountB);
                    LDAPItemEntry lLDAPItemEntry = null;

                    String sType = null;

                    // check wath kind of object is droped
                    if (oObject instanceof LDAPItemEntry)
                    {
                        lLDAPItemEntry = (LDAPItemEntry) oObject;

                        if (LDAPUtils.checkAttriValueExists(lLDAPItemEntry.getEntry(),
                                                                "objectclass",
                                                                "busauthenticationuser"))
                        {
                            sType = "user";
                        }
                        else if (LDAPUtils.checkAttriValueExists(lLDAPItemEntry.getEntry(),
                                                                     "objectclass",
                                                                     "busorganizationalrole"))
                        {
                            sType = "role";
                        }

                        // get the parent object to add the new object
                        for (int iCount = tTreePath.getPathCount() - 1;; iCount--)
                        {
                            Object oMutable = tTreePath.getPathComponent(iCount);

                            if (oMutable instanceof CordysTreeNode)
                            {
                                dmtnParentNode = (CordysTreeNode) oMutable;

                                // if dropped type is user parent should be a organization or
                                // organization unit
                                if (sType == "user")
                                {
                                    // if orgaization or organization unit
                                    if (dmtnParentNode.isOrganization() ||
                                            dmtnParentNode.isOrganizationUnit())
                                    {
                                        break;
                                    }
                                }

                                // if dropped type is role parent should be a user
                                if (sType == "role")
                                {
                                    // if orgaization or organization unit
                                    if (dmtnParentNode.isUser())
                                    {
                                        break;
                                    }
                                }

                                if (iCount == 0)
                                {
                                    bContinue = false;
                                    break;
                                }
                            }
                        }
                    }

                    // if a parent is found
                    if (bContinue)
                    {
                        CordysTreeNode newCordysTreeNode = dmtnParentNode.addChild(lLDAPItemEntry
                                                                                   .getEntry());

                        if (sType == "user")
                        {
                            if (newCordysTreeNode != null)
                            {
                                // add the current selected role to the just added CordysTreeNode
                                LDAPEntry[] leaRoles = getSelectedRoles();

                                for (int iCount = 0; iCount < leaRoles.length; iCount++)
                                {
                                    newCordysTreeNode.addChild(leaRoles[iCount]);
                                }
                            }
                        }
                        ((DefaultTreeModel) getModel()).reload(dmtnParentNode);
                    }
                }
            }
            dropTargetDropEvent.acceptDrop(DnDConstants.ACTION_MOVE);
            dropTargetDropEvent.getDropTargetContext().dropComplete(true);
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
            System.err.println("Exception" + exception.getMessage());
            dropTargetDropEvent.rejectDrop();
        }
        catch (UnsupportedFlavorException ufException)
        {
            ufException.printStackTrace();
            System.err.println("Exception" + ufException.getMessage());
            dropTargetDropEvent.rejectDrop();
        }
        catch (ClassNotFoundException eException)
        {
            eException.printStackTrace();
            System.err.println("Exception" + eException.getMessage());
            dropTargetDropEvent.rejectDrop();
        }
    }

    /**
     * ...
     *
     * @param  dropTargetDragEvent
     */
    public void dropActionChanged(java.awt.dnd.DropTargetDragEvent dropTargetDragEvent)
    {
    }

    /**
     * ...
     *
     * @param  dragSourceDragEvent
     */
    public void dropActionChanged(java.awt.dnd.DragSourceDragEvent dragSourceDragEvent)
    {
    }

    /**
     * Function called when a key is presed Used to act if the del key is pressed.
     *
     * @param  keyEvent  DOCUMENTME
     */
    public void keyPressed(KeyEvent keyEvent)
    {
        // if key is Delete
        if (keyEvent.getKeyCode() == 127)
        {
            CordysTreeNode parentCTN = null;
            boolean bDoDelete = false;

            // Show a confirm dialog
            TreePath[] tpTreePath = getSelectionPaths();

            // ask only once for confiming;
            for (int iCount = 0; iCount < tpTreePath.length; iCount++)
            {
                CordysTreeNode node = (CordysTreeNode) tpTreePath[iCount].getLastPathComponent();

                if (node.isOrganizationRole() || node.isUser())
                {
                    int iConfirm = JOptionPane.showConfirmDialog(this,
                                                                 "Are you sure you want to delete this entry?",
                                                                 "Delete LDAPEntry",
                                                                 JOptionPane.YES_NO_OPTION,
                                                                 JOptionPane.WARNING_MESSAGE);

                    if (iConfirm == JOptionPane.YES_OPTION)
                    {
                        bDoDelete = true;
                        break;
                    }
                }
            }

            if (bDoDelete)
            {
                for (int iCount = 0; iCount < tpTreePath.length; iCount++)
                {
                    CordysTreeNode node = (CordysTreeNode) tpTreePath[iCount]
                                          .getLastPathComponent();

                    parentCTN = (CordysTreeNode) node.getParent();

                    if (node.isOrganizationRole() || node.isUser())
                    {
                        node.deleteThis();
                    }
                }

                if (parentCTN != null)
                {
                    ((DefaultTreeModel) getModel()).reload(parentCTN);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  keyEvent
     */
    public void keyReleased(java.awt.event.KeyEvent keyEvent)
    {
    }

    /**
     * DOCUMENT ME!
     *
     * @param  keyEvent  keyEvent
     */
    public void keyTyped(java.awt.event.KeyEvent keyEvent)
    {
    }

    /**
     * Sets the treecel rederer.
     *
     * @param  renderer  Default treecel renderer
     */
    public void setTreeCellRenderer(DefaultTreeCellRenderer renderer)
    {
        setCellRenderer(renderer);
    }

    /**
     * a drag gesture has been initiated.
     *
     * @param  event  event
     */
    public void treeDragGestureRecognized(DragGestureEvent event)
    {
    }

    /**
     * This method can be overwritten by subclasses.
     *
     * @param  tse  DOCUMENTME
     */
    public void valueChanged(TreeSelectionEvent tse)
    {
    }

    /**
     * function whitch can be used to get the current selcted role is overwritten by the class witch
     * extends from DnDjTree.
     *
     * @return  null
     */
    protected LDAPEntry[] getSelectedRoles()
    {
        return null;
    }
}
