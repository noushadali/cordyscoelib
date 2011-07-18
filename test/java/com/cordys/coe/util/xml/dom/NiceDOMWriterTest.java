package com.cordys.coe.util.xml.dom;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Testcases for the NiceDOMWriter class.
 *
 * @author pgussow
 */
public class NiceDOMWriterTest extends TestCase
{
    /**
     * The result for the testWithFormatting.
     */
    private static final String TEST_FORMATTING_RESULT_4 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<a>\n    <b>blaat</b>\n</a>\n";
    /**
     * The result for the testWithFormattingIdent.
     */
    private static final String TEST_FORMATTING_RESULT_2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<a>\n  <b>blaat</b>\n</a>\n";
    /**
     * The result for the testWithFormattingIdentZero.
     */
    private static final String TEST_FORMATTING_RESULT_0 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<a><b>blaat</b></a>";
    /**
     * The result for the testWithFormattingCData.
     */
    private static final String TEST_FORMATTING_CDATA_0 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<root><a><![CDATA[<nested><![CDATA[Nested CData]]]]><![CDATA[></nested>]]></a></root>";
    /**
     * The result for the testWithFormattingCData.
     */
    private static final String TEST_FORMATTING_CDATA_2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<root>\n  <a><![CDATA[<nested><![CDATA[Nested CData]]]]><![CDATA[></nested>]]></a>\n</root>\n";
    /**
     * The result for the testWithFormattingCData.
     */
    private static final String TEST_FORMATTING_CDATA_4 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<root>\n    <a><![CDATA[<nested><![CDATA[Nested CData]]]]><![CDATA[></nested>]]></a>\n</root>\n";
    /**
     * The result for the testNamespacePrefixes.
     */
    private static final String TEST_FORMATTING_NAEMSPACE_0 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP:Body><GetFullConfiguration xmlns=\"http://schemas.cordys.com\"><CertificateSerial>3736861487</CertificateSerial></GetFullConfiguration></SOAP:Body></SOAP:Envelope>";
    /**
     * The result for the testNamespacePrefixes.
     */
    private static final String TEST_FORMATTING_NAEMSPACE_2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\">\n  <SOAP:Body>\n    <GetFullConfiguration xmlns=\"http://schemas.cordys.com\">\n      <CertificateSerial>3736861487</CertificateSerial>\n    </GetFullConfiguration>\n  </SOAP:Body>\n</SOAP:Envelope>\n";
    /**
     * The result for the testNamespacePrefixes.
     */
    private static final String TEST_FORMATTING_NAEMSPACE_4 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\">\n    <SOAP:Body>\n        <GetFullConfiguration xmlns=\"http://schemas.cordys.com\">\n            <CertificateSerial>3736861487</CertificateSerial>\n        </GetFullConfiguration>\n    </SOAP:Body>\n</SOAP:Envelope>\n";

    /**
     * This method tests namespace prefixes and default namespaces.
     */
    public void testNamespacePrefixes()
    {
        Document dDoc = XMLHelper.createDocumentFromXML("<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP:Body><GetFullConfiguration xmlns=\"http://schemas.cordys.com\"><CertificateSerial>3736861487</CertificateSerial></GetFullConfiguration></SOAP:Body></SOAP:Envelope>");

        Element eTemp = dDoc.getDocumentElement();

        Node nTemp = eTemp.getFirstChild();

        while (nTemp != null)
        {
            System.out.println(nTemp.getNodeName() + " : " +
                               nTemp.getNamespaceURI());
            nTemp = nTemp.getFirstChild();
        }

        try
        {
            String sFormatResult = NiceDOMWriter.write(dDoc, 0);

            assertEquals("Formatted text is not correct.",
                         TEST_FORMATTING_NAEMSPACE_0, sFormatResult);

            sFormatResult = NiceDOMWriter.write(dDoc, 2);

            assertEquals("Formatted text is not correct.",
                         TEST_FORMATTING_NAEMSPACE_2, sFormatResult);
            sFormatResult = NiceDOMWriter.write(dDoc, 4);

            assertEquals("Formatted text is not correct.",
                         TEST_FORMATTING_NAEMSPACE_4, sFormatResult);
        }
        catch (NiceDOMWriterException e)
        {
            e.printStackTrace();
            fail("Should not happen");
        }
    }

    /**
     * This testcase tests the base formatting functionality.
     */
    public void testWithFormatting()
    {
        Document dDoc = XMLHelper.createDocumentFromXML("<a><b>blaat</b></a>");

        try
        {
            String sFormatResult = NiceDOMWriter.write(dDoc);

            assertEquals("Formatted text is not correct.",
                         TEST_FORMATTING_RESULT_4, sFormatResult);
        }
        catch (NiceDOMWriterException e)
        {
            e.printStackTrace();
            fail("Should not happen");
        }
    }

    /**
     * This testcase tests the base formatting functionality with the
     * ident set to 2.
     */
    public void testWithFormattingCData()
    {
        Document dDoc = XMLHelper.createDocumentFromXML("<root><a/></root>");
        dDoc.getDocumentElement().getFirstChild()
            .appendChild(dDoc.createCDATASection("<nested><![CDATA[Nested CData]]></nested>"));

        try
        {
            String sFormatResult = NiceDOMWriter.write(dDoc, 0);

            assertEquals("Formatted text is not correct.",
                         TEST_FORMATTING_CDATA_0, sFormatResult);

            sFormatResult = NiceDOMWriter.write(dDoc, 2);

            assertEquals("Formatted text is not correct.",
                         TEST_FORMATTING_CDATA_2, sFormatResult);
            sFormatResult = NiceDOMWriter.write(dDoc, 4);

            assertEquals("Formatted text is not correct.",
                         TEST_FORMATTING_CDATA_4, sFormatResult);
        }
        catch (NiceDOMWriterException e)
        {
            e.printStackTrace();
            fail("Should not happen");
        }
    }

    /**
     * This testcase tests the base formatting functionality with the
     * ident set to 2.
     */
    public void testWithFormattingIdent()
    {
        Document dDoc = XMLHelper.createDocumentFromXML("<a><b>blaat</b></a>");

        try
        {
            String sFormatResult = NiceDOMWriter.write(dDoc, 2);

            assertEquals("Formatted text is not correct.",
                         TEST_FORMATTING_RESULT_2, sFormatResult);
        }
        catch (NiceDOMWriterException e)
        {
            e.printStackTrace();
            fail("Should not happen");
        }
    }

    /**
     * This testcase tests the base formatting functionality with the
     * ident set to 2.
     */
    public void testWithFormattingIdentZero()
    {
        Document dDoc = XMLHelper.createDocumentFromXML("<a><b>blaat</b></a>");

        try
        {
            String sFormatResult = NiceDOMWriter.write(dDoc, 0);

            assertEquals("Formatted text is not correct.",
                         TEST_FORMATTING_RESULT_0, sFormatResult);
        }
        catch (NiceDOMWriterException e)
        {
            e.printStackTrace();
            fail("Should not happen");
        }
    }
}
