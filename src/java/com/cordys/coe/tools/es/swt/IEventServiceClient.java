package com.cordys.coe.tools.es.swt;

import com.cordys.coe.tools.log4j.ILog4JComposite;

/**
 * This is the interface used to communicate to the UI of the event service client.
 *
 * @author  pgussow
 */
public interface IEventServiceClient extends ILog4JComposite
{
    /**
     * This method subscribes to the passed on subject.
     *
     * @param  sSubject  The subject to subscribwe to.
     */
    void subscribeToSubject(String sSubject);
}
