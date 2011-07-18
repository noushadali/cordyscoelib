package com.cordys.coe.test.i18n;

import com.cordys.coe.util.i18n.CoEMessage;
import com.cordys.coe.util.i18n.CoEMessageSet;
import com.cordys.coe.util.i18n.CoEMessageSetTest;

import com.eibus.localization.IStringResource;

import java.util.Locale;

/**
 * DOCUMENTME
 *
 * @author $author$
 */
public class TestI18N
{
    /**
     * Main method.
     *
     * @param saArguments The commandline arguments.
     */
    public static void main(String[] saArguments)
    {
        try
        {
            CoEMessageSet cms = new CoEMessageSet(CoEMessageSetTest.class,
                                                  "TestMessages");
            IStringResource sr1 = new CoEMessage(cms, "different.string");
            IStringResource sr2 = new CoEMessage(cms, "some.nice.identifier");

            //Output the available localizations.
            System.out.println("Avalable localized files for " +
                               cms.getFullyQualifiedName());

            Locale[] al = cms.getAvailableLocales();

            for (Locale locale : al)
            {
                System.out.println("- " + locale.toString());
            }

            //Do some actual localzations:
            Locale[] alAllLocales = Locale.getAvailableLocales();

            for (Locale lTemp : alAllLocales)
            {
                System.out.println(lTemp.toString() + ": " +
                                   sr1.getMessage(lTemp));
            }

            Locale lNL = new Locale("nl");

            System.out.println("string.1: " + sr1.getMessage(lNL));
            System.out.println("string.2: " + sr2.getMessage(lNL));
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
    }
}
