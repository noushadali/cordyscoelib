package com.cordys.coe.test;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.Types;

import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.schema.Schema;

import javax.wsdl.factory.WSDLFactory;

import javax.xml.XMLConstants;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.xsd2inst.SampleXmlUtil;

import org.w3c.dom.Element;

/**
 * DOCUMENTME.
 *
 * @author  $author$
 */
public class TestSchemaInstance
{
    /**
     * Main method.
     *
     * @param  saArguments  Commandline arguments.
     */
    public static void main(String[] saArguments)
    {
        try
        {
        	System.out.println(URLDecoder.decode("http://windev002.cordys.csc.nl/cordys/com.eibus.web.tools.wsdl.WSDLGateway.wcp?service=http%3A%2F%2Ftest.embraer.com%2F1.0%2Ftest%2FDoSomething&organization=o%3Dembraer%2Ccn%3Dcordys%2Ccn%3Dwindev002%2Co%3Dcordys.csc.nl&methodset=cn%3Dcom-embraer.embraerlibtest.webservice.Testing.WebServiceInterface%20test%2Ccn%3Dmethod%20sets%2Co%3Dembraer%2Ccn%3Dcordys%2Ccn%3Dwindev002%2Co%3Dcordys.csc.nl", "UTF-8"));
        	
            WSDLFactory factory = WSDLFactory.newInstance();
            Definition wsdl = factory.newWSDLReader().readWSDL(null,
                                                               "http://windev002.cordys.csc.nl/cordys/com.eibus.web.tools.wsdl.WSDLGateway.wcp?service=http%3A%2F%2Ftest.embraer.com%2F1.0%2Ftest%2FDoSomething&organization=o%3Dembraer%2Ccn%3Dcordys%2Ccn%3Dwindev002%2Co%3Dcordys.csc.nl&methodset=cn%3Dcom-embraer.embraerlibtest.webservice.Testing.WebServiceInterface%20test%2Ccn%3Dmethod%20sets%2Co%3Dembraer%2Ccn%3Dcordys%2Ccn%3Dwindev002%2Co%3Dcordys.csc.nl");

            Types types = wsdl.getTypes();

            List<XmlObject> allSchemas = new ArrayList<XmlObject>();

            List<?> xsds = types.getExtensibilityElements();

            for (Object schema : xsds)
            {
                ExtensibilityElement ee = (ExtensibilityElement) schema;

                if (ee instanceof Schema)
                {
                    Schema realSchema = (Schema) ee;
                    Element xsdElement = realSchema.getElement();

                    // Add the tns prefix definition if needed.
                    xsdElement.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:tns",
                                              realSchema.getElement().getAttribute("targetNamespace"));

                    XmlObject xo = XmlObject.Factory.parse(realSchema.getElement(),
                                                           (new XmlOptions()).setLoadLineNumbers()
                                                           .setLoadMessageDigest());

                    allSchemas.add(xo);
                }
            }

            // The root name is the name of the request element for the operation.
            QName rootName = getRequestElementName(wsdl);

            XmlObject[] schemas = (XmlObject[]) allSchemas.toArray(new XmlObject[allSchemas.size()]);

            SchemaTypeSystem sts = null;

            if (schemas.length > 0)
            {
                Collection<Object> errors = new ArrayList<Object>();
                XmlOptions compileOptions = new XmlOptions();
                compileOptions.setCompileDownloadUrls();

                try
                {
                    sts = XmlBeans.compileXsd(schemas, XmlBeans.getBuiltinTypeSystem(), compileOptions);
                }
                catch (Exception e)
                {
                    if (errors.isEmpty() || !(e instanceof XmlException))
                    {
                        e.printStackTrace();
                    }

                    System.out.println("Schema compilation errors: ");

                    for (Iterator<Object> i = errors.iterator(); i.hasNext();)
                    {
                        System.out.println(i.next());
                    }
                }
            }

            if (sts == null)
            {
                System.out.println("No Schemas to process.");
                return;
            }

            SchemaType[] globalElems = sts.documentTypes();
            SchemaType elem = null;

            for (int i = 0; i < globalElems.length; i++)
            {
            	System.out.println("Element found in XSD: " + globalElems[i].getDocumentElementName());
            	
                if (rootName.equals(globalElems[i].getDocumentElementName()))
                {
                    elem = globalElems[i];
                    //break;
                }
            }

            if (elem == null)
            {
                System.out.println("Could not find a global element with name \"" + rootName + "\"");
                return;
            }

            // Now generate it
            String result = SampleXmlUtil.createSampleForType(elem);

            System.out.println(result);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * DOCUMENTME.
     *
     * @param   wsdl  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    private static QName getRequestElementName(Definition wsdl)
    {
        QName returnValue = null;

        Map<?, ?> bindings = wsdl.getAllBindings();

        if (bindings.size() > 0)
        {
            for (Object key : bindings.keySet())
            {
                System.out.println("Key: " + key);

                Binding binding = (Binding) bindings.get(key);
                System.out.println("Binding: " + binding.getQName().getLocalPart());

                List<?> operations = binding.getPortType().getOperations();

                for (Object operationTemp : operations)
                {
                    Operation operation = (Operation) operationTemp;
                    System.out.println("Operation: " + operation.getName());

                    Map<?, ?> allParts = operation.getInput().getMessage().getParts();

                    for (Object partName : allParts.keySet())
                    {
                        System.out.println("Key: " + partName);

                        Part part = (Part) allParts.get(partName);

                        if (part.getName().equals("body"))
                        {
                            returnValue = part.getElementName();
                            System.out.println(returnValue);
                            break;
                        }
                    }
                }
            }
        }

        return returnValue;
    }
}
