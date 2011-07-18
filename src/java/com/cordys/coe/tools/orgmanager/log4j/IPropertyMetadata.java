package com.cordys.coe.tools.orgmanager.log4j;

import java.util.ArrayList;

/**
 * This interface describes the property metadata storage.
 *
 * @author  pgussow
 */
public interface IPropertyMetadata
{
    /**
     * This method gets the property metadata for this appender.
     *
     * @return  The property metadata for this appender.
     */
    ArrayList<PropertyMetadata> getProperties();
}
