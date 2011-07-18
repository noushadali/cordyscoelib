/**
 * Eclipse JMX Console
 * Copyright (C) 2007 Jeff Mesnil
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cordys.coe.tools.jmx.resources;

import javax.management.MBeanNotificationInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.eclipse.core.runtime.Assert;

/**
 * DOCUMENTME.
 *
 * @author  $author$
 */
public class MBeanNotificationInfoWrapper
{
    /**
     * DOCUMENTME.
     */
    private MBeanNotificationInfo info;
    /**
     * DOCUMENTME.
     */
    private MBeanServerConnection mbsc;
    /**
     * DOCUMENTME.
     */
    private ObjectName on;

    /**
     * Creates a new MBeanNotificationInfoWrapper object.
     *
     * @param  info  DOCUMENTME
     * @param  on    DOCUMENTME
     * @param  mbsc  DOCUMENTME
     */
    public MBeanNotificationInfoWrapper(MBeanNotificationInfo info, ObjectName on,
                                        MBeanServerConnection mbsc)
    {
        Assert.isNotNull(info);
        Assert.isNotNull(on);
        Assert.isNotNull(mbsc);
        this.info = info;
        this.on = on;
        this.mbsc = mbsc;
    }

    /**
     * DOCUMENTME.
     *
     * @return  DOCUMENTME
     */
    public MBeanNotificationInfo getMBeanNotificationInfo()
    {
        return info;
    }

    /**
     * DOCUMENTME.
     *
     * @return  DOCUMENTME
     */
    public MBeanServerConnection getMBeanServerConnection()
    {
        return mbsc;
    }

    /**
     * DOCUMENTME.
     *
     * @return  DOCUMENTME
     */
    public ObjectName getObjectName()
    {
        return on;
    }
}
