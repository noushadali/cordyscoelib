package com.cordys.coe.tools.useradmin.swt;

import com.cordys.coe.tools.useradmin.LDAPItemEntry;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;

/**
 * DOCUMENTME.
 *
 * @author  $author$
 */
public class ListWithData extends List
{
    /**
     * Holds all the dataobjects for the list items.
     */
    private ArrayList<LDAPItemEntry> alObjects = new ArrayList<LDAPItemEntry>();

    /**
     * Creates a new ListWithData object.
     *
     * @param  cParent  DOCUMENTME
     * @param  iStyle   DOCUMENTME
     */
    public ListWithData(Composite cParent, int iStyle)
    {
        super(cParent, iStyle);
    }

    /**
     * This method adds the entry to the list.
     *
     * @param  lieEntry  The entry to add.
     */
    public void add(LDAPItemEntry lieEntry)
    {
        super.add(lieEntry.toString());
        alObjects.add(lieEntry);
    }

    /**
     * This method returns the object that was selected.
     *
     * @param   iIndex  The index of the object to return.
     *
     * @return  The corresponding object.
     */
    public LDAPItemEntry getSelectedObject(int iIndex)
    {
        LDAPItemEntry lieReturn = null;

        if (iIndex < alObjects.size())
        {
            lieReturn = alObjects.get(iIndex);
        }

        return lieReturn;
    }

    /**
     * This method removes items from the list.
     */
    @Override public void removeAll()
    {
        super.removeAll();
        alObjects.clear();
    }

    /**
     * This class can be subclassed.
     */
    @Override protected void checkSubclass()
    {
    }
}
