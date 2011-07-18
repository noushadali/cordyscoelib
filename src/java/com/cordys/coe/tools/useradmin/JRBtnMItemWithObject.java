/**
 * © 2003 Cordys R&D B.V. All rights reserved.     The computer program(s) is
 * the proprietary information of Cordys R&D B.V.     and provided under the
 * relevant License Agreement containing restrictions     on use and
 * disclosure. Use is subject to the License Agreement.
 */
package com.cordys.coe.tools.useradmin;

import javax.swing.*;

/**
 * this RadioButtonMenuItem enables adding Objects insite the menuitem.
 *
 * @author  gjlubber
 */
public class JRBtnMItemWithObject extends JRadioButtonMenuItem
{
    /**
     * The user-object.
     */
    private Object oObject;

    /**
     * creates a standard RadioButtonMenuItem.
     */
    public JRBtnMItemWithObject()
    {
        super();
    }

    /**
     * Creates a new instance of JRBtnMItemWithObject.
     *
     * @param  oObject  the object who should be added to the RadioButtonMenuItem
     */
    public JRBtnMItemWithObject(Object oObject)
    {
        super();
        this.oObject = oObject;
    }

    /**
     * method who gets the object from JRBtnMItemWithObject.
     *
     * @return  returns the Object
     */
    public Object getObject()
    {
        return this.oObject;
    }
}
