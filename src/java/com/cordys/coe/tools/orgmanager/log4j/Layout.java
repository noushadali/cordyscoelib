package com.cordys.coe.tools.orgmanager.log4j;

import org.w3c.dom.Element;

/**
 * This class wraps the layout.
 *
 * @author  pgussow
 */
public class Layout extends BaseWithParam
{
    /**
     * Creates a new Layout object.
     */
    public Layout()
    {
        super("layout");
    }

    /**
     * Creates a new Layout object.
     *
     * @param  eLayout  The layout to parse.
     */
    public Layout(Element eLayout)
    {
        super(eLayout, "layout");
    }
}
