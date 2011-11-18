package com.cordys.coe.tools.snapshot.data.handler;

import java.util.ArrayList;
import java.util.List;

import com.cordys.coe.util.StringUtils;

/**
 * This facotry class creates a proper data handler.
 *
 * @author  localpg
 */
public class DataHandlerFactory
{
    /**
     * This method returns the list of known classes used in order to fill the JAXB context.
     *
     * @return  The list of known classes used in order to fill the JAXB context.
     */
    public static List<Class<?>> getKnownClasses()
    {
        List<Class<?>> retVal = new ArrayList<Class<?>>();

        retVal.add(CompositeData.class);
        retVal.add(StringWrappedValue.class);
        retVal.add(CompositeDataList.class);
        retVal.add(ObjectArrayList.class);

        return retVal;
    }

    /**
     * This method creates the proper data object that can produce nice report data in XML.
     *
     * @param   data         The data to process.
     * @param   dataHandler  The datahandler class to use.
     *
     * @return  The report data wrapper.
     */
    public static Object handleData(Object data, String dataHandler)
    {
        Object retVal = null;

        boolean done = false;

        if (StringUtils.isSet(dataHandler))
        {
            try
            {
                @SuppressWarnings("unchecked")
                Class<? extends ICustomDataHandler> handler = (Class<? extends ICustomDataHandler>) Class.forName(dataHandler);
                ICustomDataHandler customHandler = handler.newInstance();
                customHandler.parse(data);
                retVal = customHandler;

                done = true;
            }
            catch (Exception e)
            {
                // Let the exception go, but revert to the old.
            }
        }

        // If the custom data handler did not succeed, try the default classes.
        if (done == false)
        {
            if (data != null)
            {
                if (data instanceof javax.management.openmbean.CompositeData)
                {
                    retVal = new CompositeData((javax.management.openmbean.CompositeData) data);
                }
                else if (data instanceof javax.management.openmbean.CompositeData[])
                {
                    retVal = new CompositeDataList((javax.management.openmbean.CompositeData[]) data);
                }
                else if (data instanceof Object[])
                {
                    retVal = new ObjectArrayList((Object[]) data);
                }
                else
                {
                    retVal = new StringWrappedValue(data.toString());
                }
            }
            else
            {
                retVal = new StringWrappedValue("");
            }
        }

        return retVal;
    }
}
