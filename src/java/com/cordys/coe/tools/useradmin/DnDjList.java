/**
 * © 2003 Cordys R&D B.V. All rights reserved.     The computer program(s) is
 * the proprietary information of Cordys R&D B.V.     and provided under the
 * relevant License Agreement containing restrictions     on use and
 * disclosure. Use is subject to the License Agreement.
 */
package com.cordys.coe.tools.useradmin;

import java.awt.dnd.*;

import javax.swing.*;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * this class enables the drag and drop functionalities.
 *
 * @author  gjlubber
 */
public class DnDjList extends JList<Object>
    implements DropTargetListener, DragSourceListener, DragGestureListener, ListSelectionListener
{
    /**
     * enables this component to be a Drag Source.
     */
    DragSource dragSource = null;
    /**
     * enables this component to be a dropTarget.
     */
    DropTarget dropTarget = null;
    /**
     * constructor - initializes the DropTarget and DragSource.
     */
    boolean isSource;
    /**
     * if the list is a target this bool is set to true.
     */
    boolean isTarget;
    /**
     * if elements have to be removed after being used.
     */
    boolean removeAfterCopy;

    /**
     * enables to call this DNDjList without arguments.
     */
    public DnDjList()
    {
        this(true, false, DnDConstants.ACTION_COPY, true);
    }

    /**
     * creates a instance of DnDjList.
     *
     * @param  isSource         if the list is used as a source
     * @param  isTarget         true if the list is used as a target
     * @param  dndaction        acction to be preformd on drag (DnDConstants)
     * @param  removeAfterCopy  true if the object has to be removed after being used
     */
    public DnDjList(boolean isSource, boolean isTarget, int dndaction, boolean removeAfterCopy)
    {
        super();
        this.isSource = isSource;
        this.isTarget = isTarget;
        this.removeAfterCopy = removeAfterCopy;
        setModel(new DefaultListModel<Object>());
        getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        dropTarget = new DropTarget(this, this);
        dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(this, dndaction, this);
        addListSelectionListener(this);
    }

    /**
     * adds elements to itself.
     *
     * @param  oObject
     */
    public void addElement(Object oObject)
    {
        ((DefaultListModel<Object>) getModel()).add(0, oObject);
    }

    /**
     * this message goes to DragSourceListener, informing it that the dragging has ended.
     *
     * @param  event
     */
    public void dragDropEnd(DragSourceDropEvent event)
    {
        if (removeAfterCopy)
        {
            if (event.getDropSuccess())
            {
                removeElement();
            }
        }
    }

    /**
     * is invoked when you are dragging over the DropSite.
     *
     * @param  event
     */
    public void dragEnter(DropTargetDragEvent event)
    {
        if (isTarget)
        {
            event.acceptDrag(DnDConstants.ACTION_MOVE);
        }
    }

    /**
     * this message goes to DragSourceListener, informing it that the dragging has entered the
     * DropSite.
     *
     * @param  event
     */
    public void dragEnter(DragSourceDragEvent event)
    {
    }

    /**
     * is invoked when you are exit the DropSite without dropping.
     *
     * @param  event
     */
    public void dragExit(DropTargetEvent event)
    {
    }

    /**
     * this message goes to DragSourceListener, informing it that the dragging has exited the
     * DropSite.
     *
     * @param  event
     */
    public void dragExit(DragSourceEvent event)
    {
    }

    /**
     * a drag gesture has been initiated.
     *
     * @param  event
     */
    public void dragGestureRecognized(DragGestureEvent event)
    {
        TransferObjectsVector tovObject = new TransferObjectsVector();
        int[] selectionsindex = getSelectedIndices();

        for (int iCount = 0; iCount < selectionsindex.length; iCount++)
        {
            tovObject.addTransferObjectToVector(((DefaultListModel<Object>) getModel()).elementAt(selectionsindex[iCount]));
        }

        if (tovObject.size() > 0)
        {
            dragSource.startDrag(event, DragSource.DefaultMoveDrop, tovObject, this);
        }
        else
        {
            System.out.println("nothing was selected");
        }
    }

    /**
     * is invoked when a drag operation is going on.
     *
     * @param  event
     */
    public void dragOver(DropTargetDragEvent event)
    {
    }

    /**
     * this message goes to DragSourceListener, informing it that the dragging is currently ocurring
     * over the DropSite.
     *
     * @param  event
     */
    public void dragOver(DragSourceDragEvent event)
    {
    }

    /**
     * a drop has occurred.
     *
     * @param  event
     */
    public void drop(DropTargetDropEvent event)
    {
    }

    /**
     * is invoked if the use modifies the current drop gesture.
     *
     * @param  event
     */
    public void dropActionChanged(DropTargetDragEvent event)
    {
    }

    /**
     * is invoked when the user changes the dropAction.
     *
     * @param  event
     */
    public void dropActionChanged(DragSourceDragEvent event)
    {
    }

    /**
     * This method removes all elements from the list.
     */
    public void removeAllElements()
    {
        DefaultListModel<Object> dlmModel = (DefaultListModel<Object>) getModel();
        dlmModel.clear();
    }

    /**
     * removes an element from itself.
     */
    public void removeElement()
    {
        ((DefaultListModel<Object>) getModel()).removeElement(getSelectedValue());
    }

    /**
     * Is fired when an item is selected.
     *
     * @param  lse  The event.
     */
    public void valueChanged(ListSelectionEvent lse)
    {
    }
}
