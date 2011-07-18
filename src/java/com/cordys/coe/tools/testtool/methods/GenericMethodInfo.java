package com.cordys.coe.tools.testtool.methods;

import com.cordys.coe.util.cgc.ICordysGatewayClient;
import com.cordys.coe.util.cgc.config.ICGCConfiguration;
import com.cordys.coe.util.general.Util;
import com.cordys.coe.util.wsdl.WSDLUtil;

import java.net.URLEncoder;

import javax.wsdl.Definition;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlObject;

/**
 * Generic method information.
 *
 * @author  pgussow
 */
public class GenericMethodInfo extends BaseMethodInfo
{
    /**
     * Creates a new GenericMethodInfo object.
     *
     * @param  sImplementationType  The implementation type for the method.
     * @param  sName                The name of the method.
     * @param  sNamespace           The namespace for this method.
     */
    public GenericMethodInfo(String sImplementationType, String sName, String sNamespace)
    {
        super(sImplementationType, sName, sNamespace);
    }

    /**
     * @see  com.cordys.coe.tools.testtool.methods.IMethodInfo#composeNewRequest(ICordysGatewayClient, String))
     */
    public String composeNewRequest(ICordysGatewayClient cordysGatewayClient, String organizationContext)
    {
        String returnValue = "";

        // We need the following information: Server name, organizational context,
        try
        {
            String service = "service=" + URLEncoder.encode(getNamespace() + "/" + getName(), "UTF-8");
            String organization = "organization=" + URLEncoder.encode(organizationContext, "UTF-8");
            String methodset = "methodset=" + URLEncoder.encode(getMethodSetDN(), "UTF-8");

            StringBuilder sbURL = new StringBuilder(1024);
            ICGCConfiguration config = cordysGatewayClient.getConfiguration();

            sbURL.append("http");

            if (config.isSSL())
            {
                sbURL.append("s");
            }
            sbURL.append("://");
            sbURL.append(config.getHost());
            sbURL.append(":");
            sbURL.append(config.getPort());
            sbURL.append("/com.eibus.web.tools.wsdl.WSDLGateway.wcp?");
            sbURL.append(service);
            sbURL.append("&");
            sbURL.append(organization);
            sbURL.append("&");
            sbURL.append(methodset);

            String wsdlURL = sbURL.toString();

            // Now we have the WSDL URL. We can get the WSDL. NOTE: we don't know yet what happens with authentication!!
            Definition wsdl = WSDLUtil.readWSDL(wsdlURL);
            XmlObject[] schemas = WSDLUtil.getSchemas(wsdl);
            QName elementName = WSDLUtil.getRequestElementName(wsdl, getName());

            StringBuilder sb = new StringBuilder(1024);
            sb.append("<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\">\n\t<SOAP:Body>\n\t\t");

            // Do proper identing.
            String createSampleXML = WSDLUtil.createSampleXML(schemas, elementName);
            createSampleXML = createSampleXML.replaceAll("\\n", "\n\t\t");
            createSampleXML = createSampleXML.replaceAll("  ", "\t");

            sb.append(createSampleXML);

            sb.append("\n\t</SOAP:Body>\n</SOAP:Envelope>");

            returnValue = sb.toString();
        }
        catch (Exception e)
        {
            returnValue = "<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                          "\t<SOAP:Body>\n" +
                          "\t\t<" + getName() + " xmlns=\"" + getNamespace() + "\">\n" +
                          "\t\t\t\n" + Util.getStackTrace(e) + "\n" +
                          "\t\t\t\n" +
                          "\t\t</" + getName() + ">\n" +
                          "\t</SOAP:Body>\n" +
                          "</SOAP:Envelope>";
        }

        return returnValue;
    }
}
