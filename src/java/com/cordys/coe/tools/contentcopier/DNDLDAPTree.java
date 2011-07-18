/**
 *      © 2003 Cordys R&D B.V. All rights reserved.
 *      The computer program(s) is the proprietary information of Cordys R&D B.V.
 *      and provided under the relevant License Agreement containing restrictions
 *      on use and disclosure. Use is subject to the License Agreement.
 */
package com.cordys.coe.tools.contentcopier;

import com.cordys.coe.exception.GeneralException;
import com.cordys.coe.util.general.ldap.LDAPExporter;
import com.cordys.coe.util.general.ldap.LDAPImporter;
import com.cordys.coe.util.general.ldap.LDAPUtils;

import com.eibus.xml.nom.Document;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import java.awt.datatransfer.DataFlavor;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.lang.reflect.Constructor;

import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * This class is a Drag 'n Drop tree for LDAP entries. Entries are displayed in the tree. Treenodes
 * are suppost to implement the LDAPTreeNode interface
 */
public class DNDLDAPTree extends JTree
    implements DropTargetListener, TreeExpansionListener, TreeSelectionListener,
               DragGestureListener, DragSourceListener
{
    /**
     * Holds whether or not to use a seperate thread for importing the data.
     */
    private boolean bThread = false;
    /**
     * Holds the class-file for the Treenode implementation.
     */
    private Class<ContentCopierTreeNode> cTreeNode;
    /**
     * Holds the coordinates on which the context-menu was clicked.
     */
    private Dimension dContextMenuPoint;
    /**
     * Holds the document to use for XML nodes.
     */
    private Document dDoc = new Document();
    /**
     * enables this component to be a Drag Source.
     */
    private DragSource dsDragSource = null;
    /**
     * Holds the connection to LDAP.
     */
    private LDAPConnection lCon;
    /**
     * Holds the base DN for this tree.
     */
    private LDAPEntry leRootEntry;
    /**
     * Holds the rootnode.
     */
    private LDAPTreeNode ltnRootNode;
    /**
     * The Delete-option.
     */
    private JMenuItem miDelete;
    /**
     * The DeleteRec-option.
     */
    private JMenuItem miDeleteRec;
    /**
     * The Refresh-option.
     */
    private JMenuItem miRefresh;
    /**
     * The popupmenu that will be displayed.
     */
    private JPopupMenu pmPopup;

    /**
     * Default Constructor.
     *
     * @throws  GeneralException  DOCUMENTME
     */
    public DNDLDAPTree()
                throws GeneralException
    {
        try
        {
            setFont(new java.awt.Font("Tahoma", 0, 10));
            setShowsRootHandles(true);
            getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
            setCellRenderer(new LDAPTreeCellRenderer());
            addTreeSelectionListener(this);

            DefaultTreeModel dtm = new DefaultTreeModel(null);
            setModel(dtm);
            dtm.reload();

            // Drag and Drop enabling
            dsDragSource = new DragSource();
            dsDragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY, this);

            // Contextmenu
            pmPopup = new JPopupMenu();

            JLabel lTemp = new JLabel("Options", JLabel.CENTER);
            lTemp.setAlignmentX(JLabel.CENTER_ALIGNMENT);
            lTemp.setBackground(new Color(10, 36, 106));
            lTemp.setFont(new Font("Tahoma", Font.BOLD, 10));

            miRefresh = new JMenuItem("Refresh");
            miRefresh.setFont(new Font("Tahoma", Font.PLAIN, 10));
            miRefresh.addActionListener(new RefreshActionListener());

            miDelete = new JMenuItem("Delete");
            miDelete.setFont(new Font("Tahoma", Font.PLAIN, 10));
            miDelete.addActionListener(new DeleteActionListener(LDAPUtils.LDAPCOMMAND_DELETE));
            miDeleteRec = new JMenuItem("Delete recursive");
            miDeleteRec.setFont(new Font("Tahoma", Font.PLAIN, 10));
            miDeleteRec.addActionListener(new DeleteActionListener(LDAPUtils.LDAPCOMMAND_DELETE_RECURSIVE));

            pmPopup.add(lTemp);
            pmPopup.addSeparator();
            pmPopup.add(miRefresh);
            pmPopup.addSeparator();
            pmPopup.add(miDelete);
            pmPopup.add(miDeleteRec);
            add(pmPopup);
            addMouseListener(new MouseAdapter()
                {
                    public void mouseClicked(MouseEvent me)
                    {
                        if (SwingUtilities.isRightMouseButton(me) && !isSelectionEmpty())
                        {
                            dContextMenuPoint = new Dimension(me.getX(), me.getY());
                            pmPopup.show(DNDLDAPTree.this, me.getX(), me.getY());
                        }
                    }
                });
        }
        catch (Exception e)
        {
            throw new GeneralException(e, "Error creating the tree");
        }
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
    public DNDLDAPTree(LDAPConnection lCon, String sBaseDN, Class<ContentCopierTreeNode> cTreeNode)
                throws GeneralException
    {
        this();

        initializeTree(lCon, sBaseDN, cTreeNode);
    } // DNDLDAPTree

    /**
     * DOCUMENTME.
     *
     * @param  dsde  DOCUMENTME
     */
    public void dragDropEnd(DragSourceDropEvent dsde)
    {
    }

    /**
     * This method checks whether or not to accept the incoming drag-stuff.
     *
     * @param  dtde  The event occured.
     */
    public void dragEnter(DropTargetDragEvent dtde)
    {
        if (lCon != null)
        {
            DataFlavor[] faFlavors = dtde.getCurrentDataFlavors();

            for (int iCount = 0; iCount < faFlavors.length; iCount++)
            {
                DataFlavor dataFlavor = faFlavors[iCount];

                if (DataTransferVector.isFlavor(dataFlavor))
                {
                    dtde.acceptDrag(dtde.getDropAction());
                    break;
                }
            }
        }
        else
        {
            dtde.rejectDrag();
        }
    } // dragEnter

    // UNUSED INTERAFCEMETHODS
    /**
     * DOCUMENTME.
     *
     * @param  dsde  DOCUMENTME
     */
    public void dragEnter(DragSourceDragEvent dsde)
    {
    }

    /**
     * DOCUMENTME.
     *
     * @param  dse  DOCUMENTME
     */
    public void dragExit(DragSourceEvent dse)
    {
    }

    /**
     * DOCUMENTME.
     *
     * @param  dte  DOCUMENTME
     */
    public void dragExit(DropTargetEvent dte)
    {
    }

    /**
     * This method initiates the dragging. It creates the dragable object.
     *
     * @param  dge  the <code>DragGestureEvent</code> describing the gesture that has just occurred
     */
    public void dragGestureRecognized(DragGestureEvent dge)
    {
        if ((dge.getDragAction() == DnDConstants.ACTION_COPY) ||
                (dge.getDragAction() == DnDConstants.ACTION_MOVE) ||
                (dge.getDragAction() == DnDConstants.ACTION_COPY_OR_MOVE))
        {
            TreePath[] tpa = getSelectionPaths();

            if (tpa != null)
            {
                DataTransferVector dtv = new DataTransferVector(lCon);

                for (int iCount = 0; iCount < tpa.length; iCount++)
                {
                    TreePath treePath = tpa[iCount];
                    Object oNode = treePath.getLastPathComponent();

                    if (oNode instanceof LDAPTreeNode)
                    {
                        dtv.addEntry(((LDAPTreeNode) oNode).getLDAPEntry());
                    }
                }

                if (dtv.getEntries().length > 0)
                {
                    dsDragSource.startDrag(dge, DragSource.DefaultMoveDrop, dtv, this);
                }
            }
        }
    } // dragGestureRecognized

    /**
     * DOCUMENTME.
     *
     * @param  dsde  DOCUMENTME
     */
    public void dragOver(DragSourceDragEvent dsde)
    {
    }

    /**
     * DOCUMENTME.
     *
     * @param  dtde  DOCUMENTME
     */
    public void dragOver(DropTargetDragEvent dtde)
    {
        if (lCon == null)
        {
            dtde.rejectDrag();
        }
    } // dragOver

    /**
     * This method gets fired when the object is dropped. The data is retrieved from the
     * transferable object and the specified content is importet into the current LDAP connection.
     *
     * @param  dtde  The occured event.
     */
    public void drop(DropTargetDropEvent dtde)
    {
        if (lCon != null)
        {
            DataFlavor df = new DataFlavor(DataTransferVector.class,
                                           DataTransferVector.DND_LDAPENRIES);
            Object oTransData = null;

            try
            {
                oTransData = dtde.getTransferable().getTransferData(df);

                if (oTransData instanceof DataTransferVector)
                {
                    DataTransferVector dtv = (DataTransferVector) oTransData;
                    LDAPEntry[] alEntries = dtv.getEntries();

                    // Now we'll have to export the entries from the first LDAP to the second.
                    TreePath tpClosest = getClosestPathForLocation(dtde.getLocation().x,
                                                                   dtde.getLocation().y);

                    if (tpClosest != null)
                    {
                        Object oObject = tpClosest.getLastPathComponent();

                        if (oObject instanceof LDAPTreeNode)
                        {
                            LDAPTreeNode ltn = (LDAPTreeNode) oObject;

                            if (
                                JOptionPane.showConfirmDialog(null,
                                                                  "Are you sure you want to move the selected entries and " +
                                                                  "it's children\nas new children of:\n" +
                                                                  ltn.getLDAPEntry().getDN()) ==
                                    JOptionPane.YES_OPTION)
                            {
                                exportContent(dtv.getConnection(), alEntries, lCon,
                                              ltn.getLDAPEntry());
                                ltn.setLoaded(false);
                                ltn.onExpand(null);
                                ((DefaultTreeModel) getModel()).reload(ltn);
                            }
                        }
                    }
                }
            }
            catch (Exception e)
            {
                System.out.println("Exception: " + e);
                e.printStackTrace();

                if (e.getCause() != null)
                {
                    e.getCause().printStackTrace();
                }
            }
        }
        else
        {
            JOptionPane.showMessageDialog(null, "First you have to connect to a LDAP server.");
        }
    } // drop

    /**
     * DOCUMENTME.
     *
     * @param  dsde  DOCUMENTME
     */
    public void dropActionChanged(DragSourceDragEvent dsde)
    {
    }

    /**
     * DOCUMENTME.
     *
     * @param  dtde  DOCUMENTME
     */
    public void dropActionChanged(DropTargetDragEvent dtde)
    {
    }

    /**
     * This function initializes the tree.
     *
     * @param   lCon       The connection to LDAP.
     * @param   sBaseDN    The Base DN to use.
     * @param   cTreeNode  The Class-object for the treenodes.
     *
     * @throws  GeneralException  DOCUMENTME
     */
    public void initializeTree(LDAPConnection lCon, String sBaseDN,
                               Class<ContentCopierTreeNode> cTreeNode)
                        throws GeneralException
    {
        this.lCon = lCon;
        this.cTreeNode = cTreeNode;

        // Check if the class-object is ok.
        Class<?> cSuper = cTreeNode.getSuperclass();
        boolean bOk = false;

        while (cSuper != null)
        {
            if (cSuper.getName().equals(LDAPTreeNode.class.getName()))
            {
                bOk = true;
                break;
            }
            cSuper = cSuper.getSuperclass();
        }

        if (!bOk)
        {
            throw new GeneralException("Treenode should extend from LDAPTreeNode");
        }

        // Now read the basedn from LDAP
        try
        {
            leRootEntry = lCon.read(sBaseDN);
        }
        catch (LDAPException e)
        {
            throw new GeneralException("Could not find base DN in LDAP.");
        }

        // Create the root-node.
        ltnRootNode = createTreeNode(leRootEntry);
        ltnRootNode.onExpand(null);

        DefaultTreeModel dtm = new DefaultTreeModel(ltnRootNode);
        setModel(dtm);
        dtm.reload();

        addTreeExpansionListener(this);
    } // initializeTree

    /**
     * Returns if the import will run in a seperate thread.
     *
     * @return  If the import will run in a seperate thread.
     */
    public boolean isThread()
    {
        return bThread;
    } // isThread

    /**
     * Adapter method. Gets called when a node is selected.
     *
     * @param  ltnNode  The selected node
     */
    public void onSelectNode(LDAPTreeNode ltnNode)
    {
    } // onSelectNode

    /**
     * Sets whether or not to use a thread.
     *
     * @param  bThread  Whether or not to use a thread.
     */
    public void setThread(boolean bThread)
    {
        this.bThread = bThread;
    } // setThread

    /**
     * Called whenever an item in the tree has been collapsed.
     *
     * @param  event  DOCUMENTME
     */
    public void treeCollapsed(TreeExpansionEvent event)
    {
        Object oItem = event.getPath().getLastPathComponent();

        if (oItem instanceof LDAPTreeNode)
        {
            ((LDAPTreeNode) oItem).onCollapse(event);
        }
    } // treeCollapsed

    /**
     * Called whenever an item in the tree has been expanded.
     *
     * @param  event  The event that occured.
     */
    public void treeExpanded(TreeExpansionEvent event)
    {
        Object oItem = event.getPath().getLastPathComponent();

        if (oItem instanceof LDAPTreeNode)
        {
            ((LDAPTreeNode) oItem).onExpand(event);

            DefaultTreeModel dtm = (DefaultTreeModel) getModel();
            dtm.reload((LDAPTreeNode) oItem);
        }
    } // treeExpanded

    /**
     * Called whenever the value of the selection changes.
     *
     * @param  tse  the event that characterizes the change.
     */
    public void valueChanged(TreeSelectionEvent tse)
    {
        TreePath tp = tse.getNewLeadSelectionPath();

        if (tp != null)
        {
            Object oNode = tp.getLastPathComponent();

            if (oNode instanceof LDAPTreeNode)
            {
                ((LDAPTreeNode) oNode).onSelect(tse);
                onSelectNode((LDAPTreeNode) oNode);
            }
        }
    } // valueChanged

    /**
     * This method instantiates the TreeNode.
     *
     * @param   leEntry  The LDAP-entry to display.
     *
     * @return  A new TreeNode.
     *
     * @throws  GeneralException  DOCUMENTME
     */
    private LDAPTreeNode createTreeNode(LDAPEntry leEntry)
                                 throws GeneralException
    {
        LDAPTreeNode ltnReturn = null;

        try
        {
            Constructor<ContentCopierTreeNode> cCons = cTreeNode.getConstructor(new Class[]
                                                                                {
                                                                                    LDAPConnection.class,
                                                                                    LDAPEntry.class
                                                                                });

            if (cCons != null)
            {
                Object oInstance = cCons.newInstance(new Object[] { lCon, leEntry });

                if (oInstance instanceof LDAPTreeNode)
                {
                    ltnReturn = (LDAPTreeNode) oInstance;
                }
                else
                {
                    throw new GeneralException("Instantiated object not a LDAPTreeNode.");
                }
            }
            else
            {
                throw new GeneralException("Could not find constructor.");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new GeneralException(e, "Could not instantiate TreeNode");
        }
        return ltnReturn;
    } // createTreeNode

    /**
     * This method exports the entries as part of the alEntries-array to direct children of
     * leDestination.
     *
     * @param   lConSource       The source-connection
     * @param   alEntries        The entries to export
     * @param   lConDestination  The destination connection
     * @param   leDestination    The destination-entry.
     *
     * @throws  GeneralException  DOCUMENTME
     */
    private void exportContent(LDAPConnection lConSource, LDAPEntry[] alEntries,
                               LDAPConnection lConDestination, LDAPEntry leDestination)
                        throws GeneralException
    {
        int iRootNode = 0;

        try
        {
            iRootNode = dDoc.createElement("root");

            if (iRootNode == 0)
            {
                throw new Exception("Something weird is going on with the XML library.");
            }

            LDAPExporter ldapExporter = new LDAPExporter(lConSource);
            ldapExporter.exportDN(alEntries, true, true, iRootNode);
        }
        catch (Exception e)
        {
            throw new GeneralException(e, "Error exporting the dropped DN's");
        }

        if (iRootNode != 0)
        {
            if (bThread == true)
            {
                try
                {
                    ImporterThread it = new ImporterThread(iRootNode, lConDestination,
                                                           leDestination.getDN());
                    Thread tThread = new Thread(it);
                    tThread.start();
                }
                catch (Exception e)
                {
                    throw new GeneralException(e, "Error importing the exported DN's");
                }
            }
            else
            {
                try
                {
                    LDAPImporter ldapImporter = new LDAPImporter(lConDestination);
                    ldapImporter.importContent(leDestination.getDN(), iRootNode, true);

                    // Publish event, so others update their cache.

                }
                catch (Exception e)
                {
                    throw new GeneralException(e, "Error importing the exported DN's");
                }
            }
        }
    } // exportContent

    /**
     * Class for the action-listener of the context-menu.
     */
    public class DeleteActionListener
        implements ActionListener
    {
        /**
         * Holds the type of deletion.
         */
        private int iLDAPCommand;

        /**
         * Constructor.
         *
         * @param  iLDAPCommand  The delete-command.
         */
        public DeleteActionListener(int iLDAPCommand)
        {
            this.iLDAPCommand = iLDAPCommand;
        } // DeleteActionListener

        /**
         * Invoked when the refresh-action is clicked.
         *
         * @param  ae  The event that occured.
         */
        public void actionPerformed(ActionEvent ae)
        {
            ArrayList<LDAPTreeNode> alToBeRefreshed = new ArrayList<LDAPTreeNode>();

            TreePath[] atpPaths = DNDLDAPTree.this.getSelectionPaths();

            for (int iCount = 0; iCount < atpPaths.length; iCount++)
            {
                TreePath tpSelected = atpPaths[iCount];
                // TreePath tpSelected =
                // DNDLDAPTree.this.getClosestPathForLocation(dContextMenuPoint.width,
                // dContextMenuPoint.height);
                Object oTemp = tpSelected.getLastPathComponent();

                if (oTemp instanceof LDAPTreeNode)
                {
                    LDAPTreeNode ltn = (LDAPTreeNode) oTemp;
                    StringBuffer sbBuffer = new StringBuffer();
                    sbBuffer.append("Are you sure that you want to delete\n");
                    sbBuffer.append(ltn.getLDAPEntry().getDN());

                    if (iLDAPCommand == LDAPUtils.LDAPCOMMAND_DELETE_RECURSIVE)
                    {
                        sbBuffer.append("\nand all it's children?");
                    }

                    if (JOptionPane.showConfirmDialog(DNDLDAPTree.this, sbBuffer.toString()) ==
                            JOptionPane.YES_OPTION)
                    {
                        LDAPTreeNode ltnParent = (LDAPTreeNode) ltn.getParent();

                        try
                        {
                            LDAPUtils.changeLDAP(ltn.getLDAPCon(), ltn.getLDAPEntry(),
                                                 iLDAPCommand);
                            alToBeRefreshed.add(ltnParent);
                        }
                        catch (Exception e)
                        {
                            JOptionPane.showMessageDialog(DNDLDAPTree.this,
                                                          "Error deleting entry:\n" + e, "Error",
                                                          JOptionPane.ERROR_MESSAGE);
                            e.printStackTrace();
                        }
                    }
                }
            }

            // Refresh the nodes
            for (int iCount = 0; iCount < alToBeRefreshed.size(); iCount++)
            {
                LDAPTreeNode ldapTreeNode = alToBeRefreshed.get(iCount);
                ldapTreeNode.onCollapse(null);
                ldapTreeNode.setLoaded(false);
                ldapTreeNode.onExpand(null);
                ((DefaultTreeModel) getModel()).reload(ldapTreeNode);
            }
        } // actionPerformed
    } // RefreshActionListener

    /**
     * Class for importing. This class can run in a seperate thread.
     */
    public class ImporterThread
        implements Runnable
    {
        /**
         * The XML to import.
         */
        private int iRootNode;
        /**
         * Holds the destination LDAP connection.
         */
        private LDAPConnection lCon;
        /**
         * Holds the DN under which the new entries should be added.
         */
        private String sEntryDN;

        /**
         * Constructor.
         *
         * @param  iRootNode  The rootnode of the XML
         * @param  lCon       DOCUMENTME
         * @param  sEntryDN   DOCUMENTME
         */
        public ImporterThread(int iRootNode, LDAPConnection lCon, String sEntryDN)
        {
            this.iRootNode = iRootNode;
            this.lCon = lCon;
            this.sEntryDN = sEntryDN;
        } // ImporterThread

        /**
         * Imports the content in LDap.
         */
        public void run()
        {
            try
            {
                LDAPImporter ldapImporter = new LDAPImporter(lCon);
                ldapImporter.importContent(sEntryDN, iRootNode, true);
            }
            catch (Exception e)
            {
                System.out.println("Error importing: " + e);
                e.printStackTrace();
            }
        } // run
    } // ImporterThread

    /**
     * Class for the action-listener of the context-menu.
     */
    public class RefreshActionListener
        implements ActionListener
    {
        /**
         * Invoked when the refresh-action is clicked.
         *
         * @param  ae  The event that occured.
         */
        public void actionPerformed(ActionEvent ae)
        {
            TreePath tpSelected = DNDLDAPTree.this.getClosestPathForLocation(dContextMenuPoint.width,
                                                                             dContextMenuPoint.height);
            Object oTemp = tpSelected.getLastPathComponent();

            if (oTemp instanceof LDAPTreeNode)
            {
                LDAPTreeNode ltn = (LDAPTreeNode) oTemp;
                ltn.onContext(pmPopup);
                ltn.setLoaded(false);
                ltn.onExpand(null);
                ((DefaultTreeModel) getModel()).reload(ltn);
            }
        } // actionPerformed
    } // RefreshActionListener
} // DNDLDAPTree
