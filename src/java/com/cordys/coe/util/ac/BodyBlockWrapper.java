package com.cordys.coe.util.ac;

import com.eibus.soap.BodyBlock;
import com.eibus.soap.MethodDefinition;
import com.eibus.xml.nom.Node;

/**
 * Holds the Class BodyBlockWrapper.
 */
public class BodyBlockWrapper implements IBodyBlock
{
    /** Holds in case of a real wrapper the Dummy request / response wrapper. */
    private int m_node = 0;
    /** Holds the implementation node. */
    private int m_implementation;
    /** Holds the namespace being executed. */
    private String m_namespace;
    /** Holds the operation being executed. */
    private String m_operation;
    /** Holds whether or not a real bodyblock is being used. */
    private boolean m_usingBB = false;

    /**
     * Instantiates a new body block wrapper.
     * 
     * @param bodyBlock The body block
     */
    public BodyBlockWrapper(BodyBlock bodyBlock)
    {
        m_usingBB = true;

        MethodDefinition md = bodyBlock.getMethodDefinition();

        m_node = bodyBlock.getXMLNode();
        m_implementation = md.getImplementation();
        m_operation = md.getMethodName();
        m_namespace = md.getNamespace();
    }

    /**
     * Instantiates a new body block wrapper.
     * 
     * @param operation The name of the operation.
     * @param namespace The namespace of the operation.
     * @param implementation The implementation of the operation.
     */
    public BodyBlockWrapper(String operation, String namespace, int implementation)
    {
        m_usingBB = false;

        m_implementation = implementation;
        m_operation = operation;
        m_namespace = namespace;

        m_node = Node.getDocument(m_implementation).createElementNS(operation, null, null, namespace, 0);
    }

    /**
     * Instantiates a new body block wrapper.
     * 
     * @param operation The name of the operation.
     * @param namespace The namespace of the operation.
     * @param implementation The implementation of the operation.
     */
    public BodyBlockWrapper(int request, int implementation)
    {
        m_usingBB = false;

        m_implementation = implementation;
        m_operation = Node.getLocalName(request);
        m_namespace = Node.getNamespaceURI(request);

        m_node = Node.duplicate(request);
    }

    /**
     * This method gets the xML node.
     * 
     * @return The xML node
     * @see com.cordys.coe.ac.jmsconnector.util.IBodyBlock#getXMLNode()
     */
    public int getXMLNode()
    {
        return m_node;
    }

    /**
     * Finalize.
     * 
     * @throws Throwable The throwable
     * @see java.lang.Object#finalize()
     */
    protected void finalize() throws Throwable
    {
        if (m_usingBB == false && m_node != 0)
        {
            Node.delete(m_node);
            Node.delete(m_implementation);
        }

        super.finalize();
    }

    /**
     * This method gets the implementation.
     * 
     * @return The implementation
     * @see com.cordys.coe.ac.jmsconnector.util.IBodyBlock#getImplementation()
     */
    public int getImplementation()
    {
        return m_implementation;
    }

    /**
     * This method gets the namespace.
     * 
     * @return The namespace
     * @see com.cordys.coe.ac.jmsconnector.util.IBodyBlock#getNamespace()
     */
    public String getNamespace()
    {
        return m_namespace;
    }

    /**
     * This method gets the operation.
     * 
     * @return The operation
     * @see com.cordys.coe.ac.jmsconnector.util.IBodyBlock#getOperation()
     */
    public String getOperation()
    {
        return m_operation;
    }
}
