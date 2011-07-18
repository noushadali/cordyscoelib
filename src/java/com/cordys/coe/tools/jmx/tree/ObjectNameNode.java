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
 *
 */
package com.cordys.coe.tools.jmx.tree;

import com.cordys.coe.tools.jmx.resources.MBeanInfoWrapper;
import com.cordys.coe.util.swt.MessageBoxUtil;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

/**
 * DOCUMENTME.
 *
 * @author  $author$
 */
public class ObjectNameNode extends PropertyNode
{
    /**
     * DOCUMENTME.
     */
    private ObjectName on;
    /**
     * DOCUMENTME.
     */
    private MBeanInfoWrapper wrapper;

    /**
     * Creates a new ObjectNameNode object.
     *
     * @param  parent  DOCUMENTME
     * @param  key     DOCUMENTME
     * @param  value   DOCUMENTME
     * @param  on      DOCUMENTME
     */
    public ObjectNameNode(Node parent, String key, String value, ObjectName on)
    {
        super(parent, key, value);

        Root root = getRoot(parent);
        MBeanServerConnection mbsc = root.getMBeanServerConnection();
        this.on = on;

        try
        {
            this.wrapper = new MBeanInfoWrapper(on, mbsc.getMBeanInfo(on), mbsc);
        }
        catch (Exception e)
        {
            MessageBoxUtil.showError("Error creating MBeanInfoWrapper", e);
        }
    }

    /**
     * DOCUMENTME.
     *
     * @param   obj  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    @Override public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!super.equals(obj))
        {
            return false;
        }

        if (!(obj instanceof ObjectNameNode))
        {
            return false;
        }

        final ObjectNameNode other = (ObjectNameNode) obj;

        if (on == null)
        {
            if (other.on != null)
            {
                return false;
            }
        }
        else if (!on.equals(other.on))
        {
            return false;
        }
        return true;
    }

    /**
     * DOCUMENTME.
     *
     * @return  DOCUMENTME
     */
    public MBeanInfoWrapper getMbeanInfoWrapper()
    {
        return wrapper;
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

    /**
     * DOCUMENTME.
     *
     * @return  DOCUMENTME
     */
    @Override public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = (prime * result) + ((on == null) ? 0 : on.hashCode());
        return result;
    }

    /**
     * DOCUMENTME.
     *
     * @return  DOCUMENTME
     */
    @Override public String toString()
    {
        return "ObjectNameNode[on=" + on.getKeyPropertyListString() + "]"; // $NON-NLS-1$
                                                                           // //$NON-NLS-2$
    }
}
