package com.cordys.coe.util.cgc;

import java.io.File;
import java.net.URL;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.cordys.coe.util.cgc.config.CGCAuthenticationFactory;
import com.cordys.coe.util.cgc.config.CGCConfigFactory;
import com.cordys.coe.util.cgc.config.ICGCConfiguration;
import com.cordys.coe.util.cgc.config.INTLMAuthentication;
import com.cordys.coe.util.xml.dom.NiceDOMWriter;
import com.cordys.coe.util.xml.dom.XMLHelper;

/**
 * Simple test class to test the CGC connection to a server.
 *
 * @author  pgussow
 */
public class TestCGCSimple
{
    /**
     * Main method.
     *
     * @param  saArguments  The commandline arguments.
     */
    public static void main(String[] saArguments)
    {
        Logger.getLogger("com.cordys.coe.util.cgc").setLevel(Level.DEBUG);
        //Logger.getLogger("org.apache.http.wire").setLevel(Level.DEBUG);
        
        try
        {
            INTLMAuthentication auth = CGCAuthenticationFactory.createNTLMAuthentication("username", "password", "CE");
            ICGCConfiguration cfg = CGCConfigFactory.createConfiguration(new URL("http://ijmesbapd00/home/system/com.eibus.web.soap.Gateway.wcp"));
            
            ICordysGatewayClient cgc = CGCFactory.createCGC(auth, cfg);
            
            cgc.connect();
            
            //Now test the upload URL.
            Document doc = XMLHelper.createDocumentFromXML("<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP:Body><UploadCAP xmlns=\"http://schemas.cordys.com/cap/1.0\"><name>Upload:FileName1</name><content>Upload:FileContent1</content></UploadCAP></SOAP:Body></SOAP:Envelope>");
            
            cgc.setOrganization(cgc.getUserInfo().getDefaultOrganization().getDN());
            Element response = cgc.uploadFile(doc.getDocumentElement(), new File("./docs/internal/TataSteel Playground 1.0.0.cap"));
            
            System.out.println(NiceDOMWriter.write(response));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
