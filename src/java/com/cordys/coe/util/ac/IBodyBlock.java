package com.cordys.coe.util.ac;

/**
 * Holds the Interface IBodyBlock. It wraps the Cordys BodyBlock so that we can more easily test the funcitonality outside Cordys.
 */
public interface IBodyBlock
{
    /**
     * This method gets the xML node.
     * 
     * @return The xML node
     */
    int getXMLNode();
    
    /**
     * This method gets the namespace of the web service operation.
     * 
     * @return The namespace of the web service operation.
     */
    String getNamespace();
    
    /**
     * This method gets the operation name that is being executed.
     * 
     * @return The operation name that is being executed.
     */
    String getOperation();
    
    /**
     * This method gets the implementation XML of the operation that is being executed.
     * 
     * @return The implementation XML of the operation that is being executed.
     */
    int getImplementation();
}
