/*
 * ----------------------------------------------------------------------------- (c) Copyright IBM
 * Corp. 2003  All rights reserved. The sample program(s) is/are owned by International Business
 * Machines Corporation or one of its subsidiaries ("IBM") and is/are copyrighted and licensed, not
 * sold. You may copy, modify, and distribute this/these sample program(s) in any form without
 * payment to IBM, for any purpose including developing, using, marketing or distributing programs
 * that include or are derivative works of the sample program(s). The sample program(s) is/are
 * provided to you on an "AS IS" basis, without warranty of any kind.  IBM HEREBY EXPRESSLY
 * DISCLAIMS ALL WARRANTIES, EITHER EXPRESS OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.  Some jurisdictions do not
 * allow for the exclusion or limitation of implied warranties, so the above limitations or
 * exclusions may not apply to you.  IBM shall not be liable for any damages you suffer as a result
 * of using, modifying or distributing the sample program(s) or its/their derivatives. Each copy of
 * any portion of this/these sample program(s) or any derivative work, must include the above
 * copyright notice and disclaimer of warranty.
 * -----------------------------------------------------------------------------
 */
package com.cordys.coe.util.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

/**
 * Port of AWT BorderLayout to SWT.
 *
 * @author  Yannick Saillet
 */
public class BorderLayout extends AWTLayout
{
    /**
     * DOCUMENTME.
     */
    public static final String CENTER = "Center";
    /**
     * DOCUMENTME.
     */
    public static final String EAST = "East";
    /**
     * DOCUMENTME.
     */
    public static final String NORTH = "North";
    /**
     * DOCUMENTME.
     */
    public static final String SOUTH = "South";
    /**
     * DOCUMENTME.
     */
    public static final String WEST = "West";
    /**
     * DOCUMENTME.
     */
    private Control centerChild;
    /**
     * DOCUMENTME.
     */
    private Control eastChild;

    // -----------------------
    /**
     * DOCUMENTME.
     */
    private int hgap;
    /**
     * DOCUMENTME.
     */
    private Control northChild;
    /**
     * DOCUMENTME.
     */
    private Control southChild;

    // -----------------------
    /**
     * DOCUMENTME.
     */
    /**
     * DOCUMENTME.
     */
    private int vgap;
    /**
     * DOCUMENTME.
     */
    private Control westChild;

    /**
     * Creates a new BorderLayout object.
     */
    public BorderLayout()
    {
        super();
    }

    /**
     * Creates a new BorderLayout object.
     *
     * @param  hgap  DOCUMENTME
     * @param  vgap  DOCUMENTME
     */
    public BorderLayout(int hgap, int vgap)
    {
        this.hgap = hgap;
        this.vgap = vgap;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  Returns the hgap.
     */
    public int getHgap()
    {
        return hgap;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  Returns the vgap.
     */
    public int getVgap()
    {
        return vgap;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  hgap  The hgap to set.
     */
    public void setHgap(int hgap)
    {
        this.hgap = hgap;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  vgap  The vgap to set.
     */
    public void setVgap(int vgap)
    {
        this.vgap = vgap;
    }

    /**
     * DOCUMENTME.
     *
     * @param   composite   DOCUMENTME
     * @param   wHint       DOCUMENTME
     * @param   hHint       DOCUMENTME
     * @param   flushCache  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    @Override protected Point computeSize(Composite composite, int wHint, int hHint,
                                          boolean flushCache)
    {
        readLayoutData(composite);

        Point size = new Point(0, 0);

        Point preferredSize;

        if (northChild != null)
        {
            preferredSize = getPreferredSize(northChild, wHint, SWT.DEFAULT, flushCache);
            size.y += (preferredSize.y + vgap);
        }

        if (southChild != null)
        {
            preferredSize = getPreferredSize(southChild, wHint, SWT.DEFAULT, flushCache);
            size.y += (preferredSize.y + vgap);
        }

        if (westChild != null)
        {
            preferredSize = getPreferredSize(westChild, SWT.DEFAULT, hHint, flushCache);
            size.x += (preferredSize.x + hgap);
        }

        if (eastChild != null)
        {
            preferredSize = getPreferredSize(eastChild, SWT.DEFAULT, hHint, flushCache);
            size.x += (preferredSize.x + hgap);
        }

        if (centerChild != null)
        {
            preferredSize = getPreferredSize(centerChild, wHint, hHint, flushCache);
            size.x += preferredSize.x;
            size.y += preferredSize.y;
        }
        return size;
    }

    /**
     * DOCUMENTME.
     *
     * @param  composite   DOCUMENTME
     * @param  flushCache  DOCUMENTME
     */
    @Override protected void layout(Composite composite, boolean flushCache)
    {
        readLayoutData(composite);

        Rectangle clientArea = composite.getClientArea();
        int top = clientArea.y;
        int bottom = clientArea.y + clientArea.height;
        int left = clientArea.x;
        int right = clientArea.x + clientArea.width;

        Point preferredSize;

        if (northChild != null)
        {
            preferredSize = getPreferredSize(northChild, clientArea.width, SWT.DEFAULT, flushCache);
            northChild.setBounds(left, top, right - left, preferredSize.y);
            top += (preferredSize.y + vgap);
        }

        if (southChild != null)
        {
            preferredSize = getPreferredSize(southChild, clientArea.width, SWT.DEFAULT, flushCache);
            southChild.setBounds(left, bottom - preferredSize.y, right - left, preferredSize.y);
            bottom -= (preferredSize.y + vgap);
        }

        if (westChild != null)
        {
            preferredSize = getPreferredSize(westChild, SWT.DEFAULT, bottom - top, flushCache);
            westChild.setBounds(left, top, preferredSize.x, bottom - top);
            left += (preferredSize.x + hgap);
        }

        if (eastChild != null)
        {
            preferredSize = getPreferredSize(eastChild, SWT.DEFAULT, bottom - top, flushCache);
            eastChild.setBounds(right - preferredSize.x, top, preferredSize.x, bottom - top);
            right -= (preferredSize.x + hgap);
        }

        if (centerChild != null)
        {
            centerChild.setBounds(left, top, right - left, bottom - top);
        }
    }

    /**
     * Read the layout data of the children of a composite.
     *
     * @param  composite  the parent composite
     */
    private void readLayoutData(Composite composite)
    {
        northChild = southChild = eastChild = westChild = centerChild = null;

        Control[] children = composite.getChildren();

        for (int i = 0; i < children.length; i++)
        {
            // if (!children[i].isVisible())
            // continue;
            Object layoutData = children[i].getLayoutData();

            if (NORTH.equals(layoutData))
            {
                northChild = children[i];
            }
            else if (SOUTH.equals(layoutData))
            {
                southChild = children[i];
            }
            else if (EAST.equals(layoutData))
            {
                eastChild = children[i];
            }
            else if (WEST.equals(layoutData))
            {
                westChild = children[i];
            }
            else
            {
                centerChild = children[i];
            }
        }
    }
}
