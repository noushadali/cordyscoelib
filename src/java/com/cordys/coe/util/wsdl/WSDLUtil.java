package com.cordys.coe.util.wsdl;

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
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.xsd2inst.SampleXmlUtil;

import org.w3c.dom.Element;

/**
 * This class contains WSDL utility functions.
 *
 * @author  pgussow
 */
public class WSDLUtil
{
    /**
     * This method generates a sample XML for the given schema.
     *
     * @param   schemas      The list of schemas.
     * @param   elementName  The name of the element to generate.
     *
     * @return  The sample XML structure.
     *
     * @throws  Exception  In case of any exceptions
     */
    public static String createSampleXML(XmlObject[] schemas, QName elementName)
                                  throws Exception
    {
        SchemaTypeSystem sts = null;

        if (schemas.length > 0)
        {
            Collection<Object> errors = new ArrayList<Object>();
            XmlOptions compileOptions = new XmlOptions();
            compileOptions.setCompileDownloadUrls();
            compileOptions.setErrorListener(errors);

            try
            {
                sts = XmlBeans.compileXsd(schemas, XmlBeans.getBuiltinTypeSystem(), compileOptions);
            }
            catch (Exception e)
            {
                StringBuilder sb = new StringBuilder(1024);

                for (Iterator<Object> i = errors.iterator(); i.hasNext();)
                {
                    sb.append(i.next()).append("\n");
                }

                throw new Exception(sb.toString(), e);
            }
        }

        if (sts == null)
        {
            throw new Exception("No Schemas to process.");
        }

        SchemaType[] globalElems = sts.documentTypes();
        SchemaType elementToBeGenerated = null;

        // Find the element to generate.
        for (int count = 0; count < globalElems.length; count++)
        {
            if (elementName.equals(globalElems[count].getDocumentElementName()))
            {
                elementToBeGenerated = globalElems[count];
                // break;
            }
        }

        if (elementToBeGenerated == null)
        {
            throw new Exception("Could not find a global element with name \"" + elementName + "\"");
        }

        // Now generate it
        return SampleXmlUtil.createSampleForType(elementToBeGenerated);
    }

    /**
     * This method returns the Qname of the request element for the given operation.
     *
     * <p>Limitation: it assumes 1 binding only.</p>
     *
     * @param   wsdl           The WSDL definition.
     * @param   operationName  The name of the operation.
     *
     * @return  The QName of the element to use for the request.
     */
    public static QName getRequestElementName(Definition wsdl, String operationName)
    {
        QName returnValue = null;

        Map<?, ?> bindings = wsdl.getAllBindings();

        if (bindings.size() > 0)
        {
            for (Object key : bindings.keySet())
            {
                Binding binding = (Binding) bindings.get(key);
                List<?> operations = binding.getPortType().getOperations();

                for (Object operationTemp : operations)
                {
                    Operation operation = (Operation) operationTemp;

                    if (operationName.equals(operation.getName()))
                    {
                        Map<?, ?> allParts = operation.getInput().getMessage().getParts();

                        // Looking for the body part.
                        if (allParts.size() > 0)
                        {
                            for (Object partName : allParts.keySet())
                            {
                                Part part = (Part) allParts.get(partName);

                                if (part.getName().equals("body"))
                                {
                                    returnValue = part.getElementName();
                                    break;
                                }
                            }

                            if (returnValue == null)
                            {
                                // Use the first part.
                                Part part = (Part) allParts.get(allParts.keySet().iterator().next());
                                returnValue = part.getElementName();
                            }
                        }
                    }

                    if (returnValue != null)
                    {
                        break;
                    }
                }

                if (returnValue != null)
                {
                    break;
                }
            }
        }

        return returnValue;
    }

    /**
     * This method returns all schemas that are part of the WSDL.
     *
     * @param   wsdl  The WSDL definition.
     *
     * @return  The list of schemas in the given WSDL.
     *
     * @throws  Exception  In case of any exceptions.
     */
    public static XmlObject[] getSchemas(Definition wsdl)
                                  throws Exception
    {
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

                // Add all the namespace prefixes that are defined at the root, but not on schema level.
                xsdElement.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:tns",
                                          realSchema.getElement().getAttribute("targetNamespace"));
                
                Map<?, ?> nss = wsdl.getNamespaces();
                for (Object key : nss.keySet())
				{
					String uri = (String) nss.get(key);
					
					xsdElement.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:" + key,
							uri);
				}


                XmlObject xo = XmlObject.Factory.parse(realSchema.getElement(),
                                                       (new XmlOptions()).setLoadLineNumbers().setLoadMessageDigest());

                allSchemas.add(xo);
            }
        }

        return (XmlObject[]) allSchemas.toArray(new XmlObject[allSchemas.size()]);
    }

    /**
     * This method loads the WSDL.
     *
     * @param   url  The URL to load the WSDL from.
     *
     * @return  The WSDL that was loaded.
     *
     * @throws  Exception  In case of any exceptions
     */
    public static Definition readWSDL(String url)
                               throws Exception
    {
        WSDLFactory factory = WSDLFactory.newInstance();
        Definition wsdl = factory.newWSDLReader().readWSDL(null, url);

        return wsdl;
    }
}
