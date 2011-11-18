package com.cordys.coe.tools.snapshot.data.handler;

/**
 * This interface describes the custom data handler classes.
 *
 * @author  localpg
 */
public interface ICustomDataHandler
{
    /**
     * This method is called to parse the result class of the JMX value into the proper structure.
     *
     * @param  value  The JMX value object to parse.
     */
    void parse(Object value);
}
