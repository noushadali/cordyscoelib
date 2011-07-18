package com.cordys.coe.tools.testtool.methods;

import com.cordys.coe.util.cgc.ICordysGatewayClient;

/**
 * Holds the method information for the XReport methods.
 *
 * @author  pgussow
 */
public class XReportMethodInfo extends BaseMethodInfo
{
    /**
     * Creates a new XReportMethodInfo object.
     *
     * @param  sImplementationType  The implementation type for the method.
     * @param  sName                The name of the method.
     * @param  sNamespace           The namespace for this method.
     */
    public XReportMethodInfo(String sImplementationType, String sName, String sNamespace)
    {
        super(sImplementationType, sName, sNamespace);
    }

    /**
     * This method composes a new request. This could be done based on the WSDL.
     *
     * @param   cordysGatewayClient  The cordys gateway client.
     * @param   organizationContext  The organizational context.
     *
     * @return  The request template.
     *
     * @see     com.cordys.coe.tools.testtool.methods.IMethodInfo#composeNewRequest(com.cordys.coe.util.cgc.ICordysGatewayClient)
     */
    public String composeNewRequest(ICordysGatewayClient cordysGatewayClient, String organizationContext)
    {
        return "<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
               "\t<SOAP:Body>\n" +
               "\t\t<" + getName() + " xmlns=\"" + getNamespace() + "\">\n" +
               "\t\t\t<outputformat>html</outputformat>\n" +
               "\t\t\t<locale>en-US</locale>\n" +
               "\t\t</" + getName() + ">\n" +
               "\t</SOAP:Body>\n" +
               "</SOAP:Envelope>";
    }
}
