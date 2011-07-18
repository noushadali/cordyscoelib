/**
 * Eclipse JMX Console
 * Copyright (C) 2006 Jeff Mesnil
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

import java.util.Iterator;
import java.util.Set;

import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.eclipse.core.runtime.Assert;

/**
 * DOCUMENTME.
 *
 * @author  $author$
 */
public class DomainWrapper
{
    /**
     * DOCUMENTME.
     */
    private final MBeanServerConnection mbsc;
    /**
     * DOCUMENTME.
     */
    private final String name;

    /**
     * Creates a new DomainWrapper object.
     *
     * @param  name  DOCUMENTME
     * @param  mbsc  DOCUMENTME
     */
    public DomainWrapper(String name, MBeanServerConnection mbsc)
    {
        Assert.isNotNull(name);
        Assert.isNotNull(mbsc);
        this.name = name;
        this.mbsc = mbsc;
    }

    /**
     * DOCUMENTME.
     *
     * @return  DOCUMENTME
     */
    public MBeanInfoWrapper[] getMBeanInfos()
    {
        try
        {
            Set<ObjectName> set = mbsc.queryNames(getPattern(), null);
            MBeanInfoWrapper[] instances = new MBeanInfoWrapper[set.size()];
            int i = 0;

            for (Iterator<ObjectName> iter = set.iterator(); iter.hasNext();)
            {
                ObjectName on = iter.next();
                MBeanInfo info = mbsc.getMBeanInfo(on);
                instances[i] = new MBeanInfoWrapper(on, info, mbsc);
                i++;
            }
            return instances;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return new MBeanInfoWrapper[0];
        }
    }

    /**
     * DOCUMENTME.
     *
     * @return  DOCUMENTME
     */
    public String getName()
    {
        return name;
    }

    /**
     * DOCUMENTME.
     *
     * @return  DOCUMENTME
     *
     * @throws  MalformedObjectNameException  DOCUMENTME
     */
    private ObjectName getPattern()
                           throws MalformedObjectNameException
    {
        return new ObjectName(name + ":*"); // $NON-NLS-1$
    }
}
