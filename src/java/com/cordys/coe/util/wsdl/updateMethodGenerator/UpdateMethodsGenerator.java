package com.cordys.coe.util.wsdl.updateMethodGenerator;

import com.cordys.coe.util.general.Util;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Update method WSDL generator Original author: Mark van der Wal Please note: this is not 100% WSDL compliant.
 * Limitations: - Generates only the update method for one get function - Will possibly be deprecated after C2
 */
public class UpdateMethodsGenerator extends JFrame
    implements ActionListener
{
    /**
     * DOCUMENTME.
     */
    private static final long serialVersionUID = 6033335414483586112L;
    /**
     * DOCUMENTME.
     */
    private JTextArea inputWsdl;
    /**
     * DOCUMENTME.
     */
    private JTextArea outputWsdl;
    /**
     * DOCUMENTME.
     */
    private JCheckBox testWorkaroundCheck;

    /**
     * Creates a new UpdateMethodsGenerator object.
     */
    public UpdateMethodsGenerator()
    {
        super("Create Update Methods");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GridBagConstraints gbc = new GridBagConstraints();
        JPanel panel = new JPanel(new GridBagLayout());

        JLabel label = new JLabel("Input wsdl:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(label, gbc);

        inputWsdl = new JTextArea();

        JScrollPane scrollPane = new JScrollPane(inputWsdl, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                                 JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        panel.add(scrollPane, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));

        JButton insertButton = new JButton("Generate Insert");
        insertButton.setActionCommand("genInsert");
        insertButton.addActionListener(this);
        buttonPanel.add(insertButton);

        JButton changeButton = new JButton("Generate Update");
        changeButton.setActionCommand("genUpdate");
        changeButton.addActionListener(this);
        buttonPanel.add(changeButton);

        JButton deleteButton = new JButton("Generate Delete");
        deleteButton.setActionCommand("genDelete");
        deleteButton.addActionListener(this);
        buttonPanel.add(deleteButton);

        JButton renameButton = new JButton("Rename");
        renameButton.setActionCommand("genRename");
        renameButton.addActionListener(this);
        buttonPanel.add(renameButton);

        testWorkaroundCheck = new JCheckBox("Test-tool workaround (no 'all' element in input)");
        testWorkaroundCheck.setSelected(true);
        buttonPanel.add(testWorkaroundCheck);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.weighty = 0;
        panel.add(buttonPanel, gbc);

        outputWsdl = new JTextArea();

        JScrollPane scrollPane2 = new JScrollPane(outputWsdl, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                                  JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        panel.add(scrollPane2, gbc);

        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1;
        gbc.weighty = 1;
        contentPane.add(panel, gbc);

        this.pack();
        this.setSize(800, 600);
    }

    /**
     * DOCUMENTME.
     *
     * @param  args  DOCUMENTME
     */
    public static void main(String[] args)
    {
        if (args.length == 0)
        {
            UpdateMethodsGenerator umg = new UpdateMethodsGenerator();
            umg.setVisible(true); // deprecated in Java 1.5, but still used to provide backward
                                  // compatibility
            return;
        }

        if (args[0].equals("-?"))
        {
            System.out.println("Usage: java com.cordys.nl.wsdl.UpdateMethodsGenerator [filename] [workaround]");
            System.out.println("");
            System.out.println("Where: - filename is the name of the .wsdl file that contains the Get* method.");
            System.out.println("         if omitted a GUI is started.");
            System.out.println("       - workaround is the boolean (true or false) that specfies if the 'all'");
            System.out.println("         element should be removed from the input-tuple (true: remove, ");
            System.out.println("         false: keep). Default true.");
            return;
        }

        boolean workaround = true;

        if (args.length == 2)
        {
            workaround = Boolean.valueOf(args[1]).booleanValue();
        }

        String newName;
        Document doc;
        UpdateMethodsGenerator umg = new UpdateMethodsGenerator();

        doc = umg.readDocumentFromFile(args[0]);
        newName = umg.getNewMethodName(doc, "Update");
        umg.rename(doc, newName);
        umg.convertToUpdate(doc, workaround);
        umg.writeDocumentAsFile(doc, newName);

        doc = umg.readDocumentFromFile(args[0]);
        newName = umg.getNewMethodName(doc, "Insert");
        umg.rename(doc, newName);
        umg.convertToInsert(doc, workaround);
        umg.writeDocumentAsFile(doc, newName);

        doc = umg.readDocumentFromFile(args[0]);
        newName = umg.getNewMethodName(doc, "Delete");
        umg.rename(doc, newName);
        umg.convertToDelete(doc, workaround);
        umg.writeDocumentAsFile(doc, newName);
    }

    /**
     * DOCUMENTME.
     *
     * @param  e  DOCUMENTME
     */
    public void actionPerformed(ActionEvent e)
    {
        try
        {
            if (inputWsdl.getText().trim().equals(""))
            {
                throw new Exception("No WSDL given");
            }

            if (e.getActionCommand().equals("genInsert"))
            {
                outputWsdl.setText(generateInsert(inputWsdl.getText(), testWorkaroundCheck.isSelected()));
            }
            else if (e.getActionCommand().equals("genUpdate"))
            {
                outputWsdl.setText(generateUpdate(inputWsdl.getText(), testWorkaroundCheck.isSelected()));
            }
            else if (e.getActionCommand().equals("genDelete"))
            {
                outputWsdl.setText(generateDelete(inputWsdl.getText(), testWorkaroundCheck.isSelected()));
            }
            else if (e.getActionCommand().equals("genRename"))
            {
                String newName = JOptionPane.showInputDialog(this, "Name");

                if (newName != null)
                {
                    outputWsdl.setText(rename(inputWsdl.getText(), newName));
                }
            }
            else
            {
                throw new IllegalArgumentException("Unknown action '" + e.getActionCommand());
            }
        }
        catch (Throwable t)
        {
            JOptionPane.showMessageDialog(this, t.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.err.println(Util.getStackTrace(t));
        }
    }

    /**
     * DOCUMENTME.
     *
     * @param   sourceWsdl          DOCUMENTME
     * @param   testToolWorkaround  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    public String generateDelete(String sourceWsdl, boolean testToolWorkaround)
    {
        Document doc = this.readDocumentFromString(sourceWsdl);
        String newName = this.getNewMethodName(doc, "Delete");
        this.rename(doc, newName);
        this.convertToDelete(doc, testToolWorkaround);

        String result = this.writeDocumentAsString(doc);
        return result;
    }

    /**
     * DOCUMENTME.
     *
     * @param   sourceWsdl          DOCUMENTME
     * @param   testToolWorkaround  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    public String generateInsert(String sourceWsdl, boolean testToolWorkaround)
    {
        Document doc = this.readDocumentFromString(sourceWsdl);
        String newName = this.getNewMethodName(doc, "Insert");
        this.rename(doc, newName);
        this.convertToInsert(doc, testToolWorkaround);

        String result = this.writeDocumentAsString(doc);
        return result;
    }

    /**
     * DOCUMENTME.
     *
     * @param   sourceWsdl          DOCUMENTME
     * @param   testToolWorkaround  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    public String generateUpdate(String sourceWsdl, boolean testToolWorkaround)
    {
        Document doc = this.readDocumentFromString(sourceWsdl);
        String newName = this.getNewMethodName(doc, "Update");
        this.rename(doc, newName);
        this.convertToUpdate(doc, testToolWorkaround);

        String result = this.writeDocumentAsString(doc);
        return result;
    }

    /**
     * DOCUMENTME.
     *
     * @param   sourceWsdl  DOCUMENTME
     * @param   name        DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    public String rename(String sourceWsdl, String name)
    {
        Document doc = this.readDocumentFromString(sourceWsdl);
        this.rename(doc, name);

        String result = this.writeDocumentAsString(doc);
        return result;
    }

    /**
     * DOCUMENTME.
     *
     * @param  document            DOCUMENTME
     * @param  testToolWorkaround  DOCUMENTME
     */
    private void convertToDelete(Document document, boolean testToolWorkaround)
    {
        String methodName = this.getMethodName(document);

        // Find the Request element
        Node requestElement = moveToEltByAttr(document, "element", "name", methodName);

        if (requestElement == null)
        {
            throw new RuntimeException("No '" + methodName + "' element found");
        }

        // Find the complexType element in the Request element
        Node complexTypeRequest = moveToElt(requestElement, "complexType");

        if (complexTypeRequest == null)
        {
            throw new RuntimeException("No 'complexType' element found in Request");
        }

        complexTypeRequest.getParentNode().removeChild(complexTypeRequest);

        // ///////////////////////////////////////////////////////////////////////////////////////////

        // Find the Response Element
        Node responseElement = moveToEltByAttr(document, "element", "name", methodName + "Response");

        if (responseElement == null)
        {
            throw new RuntimeException("No '" + methodName + "Response' element found");
        }

        // Find the complexType element in the Response element
        Node complexTypeResponse = moveToElt(responseElement, "complexType");

        if (complexTypeResponse == null)
        {
            throw new RuntimeException("No 'complexType' element found in Response");
        }

        // Find the cursor element (optional) and if it's there remove it
        Node cursorElement = moveToEltByAttr(complexTypeResponse, "element", "name", "cursor");

        if (cursorElement != null)
        {
            cursorElement.getParentNode().removeChild(cursorElement);
        }

        // Find the <tuple> node
        Node tupleElement = moveToEltByAttr(complexTypeResponse, "element", "name", "tuple");

        if (tupleElement == null)
        {
            throw new RuntimeException("No 'tuple' element found in response complexType");
        }

        // Set the maxOccurs to unbounded
        tupleElement.getAttributes().getNamedItem("maxOccurs").setNodeValue("unbounded");

        // Find the <old> element in the tuple
        Node oldElement = moveToEltByAttr(tupleElement, "element", "name", "old");

        if (oldElement == null)
        {
            throw new RuntimeException("No 'old' element found in 'tuple' element");
        }

        // ///////////////////////////////////////////////////////////////////////////////////////////

        // Copy the complexTypeResponse to complexTypeRequest
        complexTypeRequest = complexTypeResponse.cloneNode(true);

        if (testToolWorkaround)
        {
            // Find the 'all' element in the complexTypeRequest
            Node allNode = moveToElt(complexTypeRequest, "all");
            NodeList childNodes = allNode.getChildNodes();
            int j = 0;

            while (j < childNodes.getLength())
            {
                Node n = childNodes.item(j);

                if (n.getNodeType() == Node.ELEMENT_NODE)
                {
                    allNode.getParentNode().appendChild(n);
                }
                j++;
            }
            allNode.getParentNode().removeChild(allNode);
        }

        requestElement.appendChild(complexTypeRequest);
    }

    /**
     * DOCUMENTME.
     *
     * @param  document            DOCUMENTME
     * @param  testToolWorkaround  DOCUMENTME
     */
    private void convertToInsert(Document document, boolean testToolWorkaround)
    {
        String methodName = this.getMethodName(document);

        // Find the Request element
        Node requestElement = moveToEltByAttr(document, "element", "name", methodName);

        if (requestElement == null)
        {
            throw new RuntimeException("No '" + methodName + "' element found");
        }

        // Find the complexType element in the Request element
        Node complexTypeRequest = moveToElt(requestElement, "complexType");

        if (complexTypeRequest == null)
        {
            throw new RuntimeException("No 'complexType' element found in Request");
        }

        complexTypeRequest.getParentNode().removeChild(complexTypeRequest);

        // ///////////////////////////////////////////////////////////////////////////////////////////

        // Find the Response Element
        Node responseElement = moveToEltByAttr(document, "element", "name", methodName + "Response");

        if (responseElement == null)
        {
            throw new RuntimeException("No '" + methodName + "Response' element found");
        }

        // Find the complexType element in the Response element
        Node complexTypeResponse = moveToElt(responseElement, "complexType");

        if (complexTypeResponse == null)
        {
            throw new RuntimeException("No 'complexType' element found in Response");
        }

        // Find the cursor element (optional) and if it's there remove it
        Node cursorElement = moveToEltByAttr(complexTypeResponse, "element", "name", "cursor");

        if (cursorElement != null)
        {
            cursorElement.getParentNode().removeChild(cursorElement);
        }

        // Find the <tuple> node
        Node tupleElement = moveToEltByAttr(complexTypeResponse, "element", "name", "tuple");

        if (tupleElement == null)
        {
            throw new RuntimeException("No 'tuple' element found in response complexType");
        }

        // Set the maxOccurs to unbounded
        tupleElement.getAttributes().getNamedItem("maxOccurs").setNodeValue("unbounded");

        // Find the <old> element in the tuple
        Node oldElement = moveToEltByAttr(tupleElement, "element", "name", "old");

        if (oldElement == null)
        {
            throw new RuntimeException("No 'old' element found in 'tuple' element");
        }

        // Rename the oldElement to 'new' (Insert only has new)
        oldElement.getAttributes().getNamedItem("name").setNodeValue("new");

        // ///////////////////////////////////////////////////////////////////////////////////////////

        // Copy the complexTypeResponse to complexTypeRequest

        complexTypeRequest = complexTypeResponse.cloneNode(true);

        if (testToolWorkaround)
        {
            // Find the 'all' element in the complexTypeRequest
            Node allNode = moveToElt(complexTypeRequest, "all");
            NodeList childNodes = allNode.getChildNodes();
            int j = 0;

            while (j < childNodes.getLength())
            {
                Node n = childNodes.item(j);

                if (n.getNodeType() == Node.ELEMENT_NODE)
                {
                    allNode.getParentNode().appendChild(n);
                }
                j++;
            }
            allNode.getParentNode().removeChild(allNode);
        }

        requestElement.appendChild(complexTypeRequest);
    }

    /**
     * DOCUMENTME.
     *
     * @param  document            DOCUMENTME
     * @param  testToolWorkaround  DOCUMENTME
     */
    private void convertToUpdate(Document document, boolean testToolWorkaround)
    {
        String methodName = this.getMethodName(document);

        // Find the Request element
        Node requestElement = moveToEltByAttr(document, "element", "name", methodName);

        if (requestElement == null)
        {
            throw new RuntimeException("No '" + methodName + "' element found");
        }

        // Find the complexType element in the Request element
        Node complexTypeRequest = moveToElt(requestElement, "complexType");

        if (complexTypeRequest == null)
        {
            throw new RuntimeException("No 'complexType' element found in Request");
        }

        complexTypeRequest.getParentNode().removeChild(complexTypeRequest);

        // ///////////////////////////////////////////////////////////////////////////////////////////

        // Find the Response Element
        Node responseElement = moveToEltByAttr(document, "element", "name", methodName + "Response");

        if (responseElement == null)
        {
            throw new RuntimeException("No '" + methodName + "Response' element found");
        }

        // Find the complexType element in the Response element
        Node complexTypeResponse = moveToElt(responseElement, "complexType");

        if (complexTypeResponse == null)
        {
            throw new RuntimeException("No 'complexType' element found in Response");
        }

        // Find the cursor element (optional) and if it's there remove it
        Node cursorElement = moveToEltByAttr(complexTypeResponse, "element", "name", "cursor");

        if (cursorElement != null)
        {
            cursorElement.getParentNode().removeChild(cursorElement);
        }

        // Find the <tuple> node
        Node tupleElement = moveToEltByAttr(complexTypeResponse, "element", "name", "tuple");

        if (tupleElement == null)
        {
            throw new RuntimeException("No 'tuple' element found in response complexType");
        }

        // Set the maxOccurs to unbounded
        tupleElement.getAttributes().getNamedItem("maxOccurs").setNodeValue("unbounded");

        // Find the <old> element in the tuple
        Node oldElement = moveToEltByAttr(tupleElement, "element", "name", "old");

        if (oldElement == null)
        {
            throw new RuntimeException("No 'old' element found in 'tuple' element");
        }

        // Copy the oldElement to newElement
        Node newElement = oldElement.cloneNode(true);

        // Rename the newElement to 'new' (after clone it's called old
        oldElement.getAttributes().getNamedItem("name").setNodeValue("new");

        // Request from Eddie Baron: switch order of old and new node
        // so that the new node is first and then the old node
        Node parent = oldElement.getParentNode();

        parent.appendChild(newElement);
        // ///////////////////////////////////////////////////////////////////////////////////////////

        complexTypeRequest = complexTypeResponse.cloneNode(true);

        if (testToolWorkaround)
        {
            Node requestOld = moveToEltByAttr(complexTypeRequest, "element", "name", "old");

            // Find the 'all' element in the complexTypeRequest
            Node allNode = moveToElt(requestOld, "all");
            NodeList childNodes = allNode.getChildNodes();
            int j = 0;

            while (j < childNodes.getLength())
            {
                Node n = childNodes.item(j);

                if (n.getNodeType() == Node.ELEMENT_NODE)
                {
                    allNode.getParentNode().appendChild(n);
                }
                j++;
            }
            allNode.getParentNode().removeChild(allNode);

            Node requestNew = moveToEltByAttr(complexTypeRequest, "element", "name", "new");

            // Find the 'all' element in the complexTypeRequest
            allNode = moveToElt(requestNew, "all");
            childNodes = allNode.getChildNodes();
            j = 0;

            while (j < childNodes.getLength())
            {
                Node n = childNodes.item(j);

                if (n.getNodeType() == Node.ELEMENT_NODE)
                {
                    allNode.getParentNode().appendChild(n);
                }
                j++;
            }
            allNode.getParentNode().removeChild(allNode);
        }

        requestElement.appendChild(complexTypeRequest);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   doc  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getMethodName(Document doc)
    {
        // get the name of the method
        Node nameNode = moveToElt(doc, "definitions");

        if (nameNode == null)
        {
            throw new RuntimeException("No element node found with method name.");
        }

        String name = nameNode.getAttributes().getNamedItem("name").getNodeValue();

        return name;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   doc        DOCUMENT ME!
     * @param   newPrefix  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getNewMethodName(Document doc, String newPrefix)
    {
        String oldName = getMethodName(doc);

        if (!oldName.startsWith("Get"))
        {
            throw new RuntimeException("Method name does not start with 'Get': " + oldName);
        }

        String newName = newPrefix + oldName.substring(3);

        return newName;
    }

    /**
     * DOCUMENTME.
     *
     * @param   current  DOCUMENTME
     * @param   eltName  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    private Node moveToElt(Node current, String eltName)
    {
        NodeList children = current.getChildNodes();
        Node found = null;

        int i = 0;

        while (i < children.getLength())
        {
            Node n = children.item(i);

            if (n.getNodeType() == Node.ELEMENT_NODE)
            {
                if (n.getNodeName().equals(eltName))
                {
                    found = n;
                    break;
                }
                found = moveToElt(n, eltName);

                if (found != null)
                {
                    break;
                }
            }
            i++;
        }

        return found;
    }

    /**
     * DOCUMENTME.
     *
     * @param   current    DOCUMENTME
     * @param   eltName    DOCUMENTME
     * @param   attrName   DOCUMENTME
     * @param   attrValue  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    private Node moveToEltByAttr(Node current, String eltName, String attrName, String attrValue)
    {
        NodeList children = current.getChildNodes();
        Node found = null;

        int i = 0;

        while (i < children.getLength())
        {
            Node n = children.item(i);

            if (n.getNodeType() == Node.ELEMENT_NODE)
            {
                if (n.getNodeName().equals(eltName) && !(n.getAttributes().getNamedItem(attrName) == null) &&
                        (n.getAttributes().getNamedItem(attrName).getNodeValue().equals(attrValue)))
                {
                    found = n;
                    break;
                }
                found = moveToEltByAttr(n, eltName, attrName, attrValue);

                if (found != null)
                {
                    break;
                }
            }
            i++;
        }

        return found;
    }

    /**
     * DOCUMENTME.
     *
     * @param   sourceFile  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    private Document readDocumentFromFile(String sourceFile)
    {
        Document document = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // factory.setValidating(true);

        factory.setNamespaceAware(true);

        try
        {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(new File(sourceFile)); // new InputSource(new
                                                            // StringReader(sourceWsdl)));
        }
        catch (SAXException sxe)
        {
            // Error generated during parsing
            Exception x = sxe;

            if (sxe.getException() != null)
            {
                x = sxe.getException();
            }
            x.printStackTrace();
        }
        catch (ParserConfigurationException pce)
        {
            // Parser with specified options can't be built
            pce.printStackTrace();
        }
        catch (IOException ioe)
        {
            // I/O error
            ioe.printStackTrace();
        }

        return document;
    }

    /**
     * DOCUMENTME.
     *
     * @param   sourceWsdl  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    private Document readDocumentFromString(String sourceWsdl)
    {
        Document document = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // factory.setValidating(true);

        factory.setNamespaceAware(true);

        try
        {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(new InputSource(new StringReader(sourceWsdl)));
        }
        catch (SAXException sxe)
        {
            // Error generated during parsing
            Exception x = sxe;

            if (sxe.getException() != null)
            {
                x = sxe.getException();
            }
            x.printStackTrace();
        }
        catch (ParserConfigurationException pce)
        {
            // Parser with specified options can't be built
            pce.printStackTrace();
        }
        catch (IOException ioe)
        {
            // I/O error
            ioe.printStackTrace();
        }

        return document;
    }

    /**
     * DOCUMENTME.
     *
     * @param  document       DOCUMENTME
     * @param  newMethodName  DOCUMENTME
     */
    private void rename(Document document, String newMethodName)
    {
        NodeList children = document.getChildNodes();

        // First find the 'definitions' element
        if (children.getLength() > 1)
        {
            throw new RuntimeException("Expected 1 child, found " + children.getLength() + " children");
        }

        Node definitionsNode = children.item(0);

        if (!definitionsNode.getNodeName().equals("definitions"))
        {
            throw new RuntimeException("Expected element with name 'definitions', found: '" +
                                       definitionsNode.getNodeName() + "'");
        }

        // Get the old method name from the definitions element
        String oldMethodName = definitionsNode.getAttributes().getNamedItem("name").getNodeValue();
        // if(!oldMethodName.startsWith("Get")) { throw new RuntimeException("Method name does not start with 'Get': " +
        // oldMethodName); System.exit(1); }

        // Create the new method name
        definitionsNode.getAttributes().getNamedItem("name").setNodeValue(newMethodName);

        // Find the input element (contains the input parameterers for the webservice)
        Node elementInput = moveToEltByAttr(definitionsNode, "element", "name", oldMethodName);

        if (elementInput == null)
        {
            throw new RuntimeException("Required element 'element' with attribute 'name' = '" + oldMethodName +
                                       "' not found");
        }
        // Rename the input element
        elementInput.getAttributes().getNamedItem("name").setNodeValue(newMethodName);

        // Find the response element (the output definition of the webservice)
        Node elementResponse = moveToEltByAttr(definitionsNode, "element", "name", oldMethodName + "Response");

        if (elementResponse == null)
        {
            throw new RuntimeException("Required element 'element' with attribute 'name' = '" + oldMethodName +
                                       "Response' not found");
        }
        // Rename the response element
        elementResponse.getAttributes().getNamedItem("name").setNodeValue(newMethodName + "Response");

        // find the tuple node in the response
        Node messageInput = moveToEltByAttr(definitionsNode, "message", "name", oldMethodName + "Input");

        if (messageInput == null)
        {
            throw new RuntimeException("Required element 'message' with attribute 'name' = '" + oldMethodName +
                                       "Input' not found");
        }
        messageInput.getAttributes().getNamedItem("name").setNodeValue(newMethodName + "Input");

        Node part = moveToEltByAttr(messageInput, "part", "element", "tns:" + oldMethodName);

        if (part == null)
        {
            throw new RuntimeException("Required element 'part' with attribute 'element' = 'tns:" + oldMethodName +
                                       "' not found");
        }
        part.getAttributes().getNamedItem("element").setNodeValue("tns:" + newMethodName);

        Node messageOutput = moveToEltByAttr(definitionsNode, "message", "name", oldMethodName + "Output");

        if (messageOutput == null)
        {
            throw new RuntimeException("Required element 'message' with attribute 'name' = '" + oldMethodName +
                                       "Output' not found");
        }
        messageOutput.getAttributes().getNamedItem("name").setNodeValue(newMethodName + "Output");

        part = moveToEltByAttr(messageOutput, "part", "element", "tns:" + oldMethodName + "Response");

        if (part == null)
        {
            throw new RuntimeException("Required element 'part' with attribute 'element' = 'tns:" + oldMethodName +
                                       "Response' not found");
        }
        part.getAttributes().getNamedItem("element").setNodeValue("tns:" + newMethodName + "Response");

        Node operation = moveToEltByAttr(definitionsNode, "operation", "name", oldMethodName);

        operation.getAttributes().getNamedItem("name").setNodeValue(newMethodName);

        Node input = moveToEltByAttr(operation, "input", "message", "tns:" + oldMethodName + "Input");

        if (input == null)
        {
            throw new RuntimeException("Required element 'input' with attribute 'message' = 'tns:" + oldMethodName +
                                       "Input' not found");
        }
        input.getAttributes().getNamedItem("message").setNodeValue("tns:" + newMethodName + "Input");

        Node output = moveToEltByAttr(operation, "output", "message", "tns:" + oldMethodName + "Output");

        output.getAttributes().getNamedItem("message").setNodeValue("tns:" + newMethodName + "Output");
    }

    /**
     * DOCUMENTME.
     *
     * @param  document  DOCUMENTME
     * @param  fileName  DOCUMENTME
     */
    private void writeDocumentAsFile(Document document, String fileName)
    {
        try
        {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            document.normalize();
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            DOMSource source = new DOMSource(document);
            File outputFile = new File(fileName);
            StreamResult result = new StreamResult(outputFile);
            transformer.transform(source, result);
        }
        catch (TransformerConfigurationException tce)
        {
            // Error generated by the parser
            System.out.println("\n** Transformer Factory error");
            System.out.println("   " + tce.getMessage());

            // Use the contained exception, if any
            Throwable x = tce;

            if (tce.getException() != null)
            {
                x = tce.getException();
            }
            x.printStackTrace();
        }
        catch (TransformerException te)
        {
            // Error generated by the parser
            System.out.println("\n** Transformation error");
            System.out.println("   " + te.getMessage());

            // Use the contained exception, if any
            Throwable x = te;

            if (te.getException() != null)
            {
                x = te.getException();
            }
            x.printStackTrace();
        }
    }

    // private void dump( Node n )
    // {
    // String outputWsdl = null;
    // try
    // {
    // TransformerFactory tFactory = TransformerFactory.newInstance();
    // Transformer transformer = tFactory.newTransformer();
    // n.normalize();
    // transformer.setOutputProperty( "{http://xml.apache.org/xslt}indent-amount", "4" );
    // transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
    //
    // DOMSource source = new DOMSource( n );
    // StringWriter sw = new StringWriter();
    // StreamResult result = new StreamResult( sw );
    //
    // transformer.transform( source, result );
    // outputWsdl = sw.toString();
    //
    // } catch ( TransformerConfigurationException tce )
    // {
    // // Error generated by the parser
    // System.out.println( "\n** Transformer Factory error" );
    // System.out.println( "   " + tce.getMessage() );
    //
    // // Use the contained exception, if any
    // Throwable x = tce;
    // if( tce.getException() != null )
    // x = tce.getException();
    // x.printStackTrace();
    //
    // } catch ( TransformerException te )
    // {
    // // Error generated by the parser
    // System.out.println( "\n** Transformation error" );
    // System.out.println( "   " + te.getMessage() );
    //
    // // Use the contained exception, if any
    // Throwable x = te;
    // if( te.getException() != null )
    // x = te.getException();
    // x.printStackTrace();
    // }
    //
    // System.out.println( outputWsdl );
    // }

    /**
     * DOCUMENTME.
     *
     * @param   document  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    private String writeDocumentAsString(Document document)
    {
        String outputWsdl = null;

        try
        {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            document.normalize();
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            DOMSource source = new DOMSource(document);
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);

            transformer.transform(source, result);
            outputWsdl = sw.toString();
        }
        catch (TransformerConfigurationException tce)
        {
            // Error generated by the parser
            System.out.println("\n** Transformer Factory error");
            System.out.println("   " + tce.getMessage());

            // Use the contained exception, if any
            Throwable x = tce;

            if (tce.getException() != null)
            {
                x = tce.getException();
            }
            x.printStackTrace();
        }
        catch (TransformerException te)
        {
            // Error generated by the parser
            System.out.println("\n** Transformation error");
            System.out.println("   " + te.getMessage());

            // Use the contained exception, if any
            Throwable x = te;

            if (te.getException() != null)
            {
                x = te.getException();
            }
            x.printStackTrace();
        }

        return outputWsdl;
    }
}
