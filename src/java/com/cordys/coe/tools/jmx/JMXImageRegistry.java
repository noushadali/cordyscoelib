package com.cordys.coe.tools.jmx;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.eclipse.swt.graphics.Image;

/**
 * This class keeps track of all the images.
 *
 * @author  pgussow
 */
public class JMXImageRegistry
{
    /**
     * Holds the connect image.
     */
    public static final String IMG_CONNECT = "/com/cordys/coe/tools/jmx/image/attachAgent.gif";
    /**
     * Holds the name of the image for domains.
     */
    public static final String IMG_DOMAIN_NODE = "/com/cordys/coe/tools/jmx/image/domain.gif";
    /**
     * Holds the name of the image for Mbeans.
     */
    public static final String IMG_OBJECT_NAME_NODE = "/com/cordys/coe/tools/jmx/image/objectname.gif";
    /**
     * Holds the name of the image for properties.
     */
    public static final String IMG_PROPERTY_NODE = "/com/cordys/coe/tools/jmx/image/propertynode.gif";
    /**
     * Holds the name of the image for 'connect'.
     */
    public static final String IMG_MBEAN_INFO_WRAPPER = "/com/cordys/coe/tools/jmx/image/attachAgent.gif";
    /**
     * Holds the name of the image for attributes.
     */
    public static final String IMG_MBEAN_ATTR_INFO_WRAPPER = "/com/cordys/coe/tools/jmx/image/attribute.gif";
    /**
     * Holds the name of the image for operations.
     */
    public static final String IMG_MBEAN_OPERATION_INFO_WRAPPER = "/com/cordys/coe/tools/jmx/image/operation.gif";
    /**
     * Holds the name of the image for read-only attributes.
     */
    public static final String IMG_MBEAN_ATTR_READ = "/com/cordys/coe/tools/jmx/image/read_obj.gif";
    /**
     * Holds the name of the image for write-only attributes.
     */
    public static final String IMG_MBEAN_ATTR_WRITE = "/com/cordys/coe/tools/jmx/image/write_obj.gif";
    /**
     * Holds the name of the image for read-write attributes.
     */
    public static final String IMG_MBEAN_ATTR_READ_WRITE = "/com/cordys/coe/tools/jmx/image/readwrite_obj.gif";
    /**
     * Holds all images.
     */
    private static HashMap<String, Image> s_hmImages = new LinkedHashMap<String, Image>();

    /**
     * This method loads an image.
     *
     * @param   sName  The name of the image.
     *
     * @return  The image.
     */
    public static Image loadImage(String sName)
    {
        Image iReturn = s_hmImages.get(sName);

        if (iReturn == null)
        {
            iReturn = SWTResourceManager.getImage(JMXTestTool.class, sName);
            s_hmImages.put(sName, iReturn);
        }
        return iReturn;
    }
}
