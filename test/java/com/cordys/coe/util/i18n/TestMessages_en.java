package com.cordys.coe.util.i18n;

import java.util.ListResourceBundle;

/**
 * This class is used to test the CoEMessageSet and CoEMessage.
 *
 * @author pgussow
 */
public class TestMessages_en extends ListResourceBundle
{
    /**
     * This method returns the content.
     *
     * @return The content of the localization.
     *
     * @see java.util.ListResourceBundle#getContents()
     */
    @Override
    protected Object[][] getContents()
    {
        return new Object[][]
               {
                   { "some.nice.identifier", "some.nice.identifier from en class" },
                   { "different.string", "different.string from en class" }
               };
    }
}
