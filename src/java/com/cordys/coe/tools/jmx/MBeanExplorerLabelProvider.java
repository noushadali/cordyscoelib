/**
 * Eclipse JMX Console
 * Copyright (C) 2006 Jeff Mesnil
 * Contact: http://www.jmesnil.net
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
package com.cordys.coe.tools.jmx;

import com.cordys.coe.tools.jmx.resources.MBeanAttributeInfoWrapper;
import com.cordys.coe.tools.jmx.resources.MBeanInfoWrapper;
import com.cordys.coe.tools.jmx.resources.MBeanOperationInfoWrapper;
import com.cordys.coe.tools.jmx.tree.DomainNode;
import com.cordys.coe.tools.jmx.tree.ObjectNameNode;
import com.cordys.coe.tools.jmx.tree.PropertyNode;

import org.eclipse.jface.viewers.LabelProvider;

import org.eclipse.swt.graphics.Image;

/**
 * DOCUMENTME.
 *
 * @author  $author$
 */
public class MBeanExplorerLabelProvider extends LabelProvider
{
    /**
     * DOCUMENTME.
     */
    private boolean flatLayout;

    /**
     * Creates a new MBeanExplorerLabelProvider object.
     */
    public MBeanExplorerLabelProvider()
    {
    }

    /**
     * DOCUMENTME.
     *
     * @param   obj  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    @Override public Image getImage(Object obj)
    {
        if (obj instanceof DomainNode)
        {
            return JMXImageRegistry.loadImage(JMXImageRegistry.IMG_DOMAIN_NODE);
        }

        if (obj instanceof ObjectNameNode)
        {
            return JMXImageRegistry.loadImage(JMXImageRegistry.IMG_OBJECT_NAME_NODE);
        }

        if (obj instanceof PropertyNode)
        {
            return JMXImageRegistry.loadImage(JMXImageRegistry.IMG_PROPERTY_NODE);
        }

        if (obj instanceof MBeanInfoWrapper)
        {
            return JMXImageRegistry.loadImage(JMXImageRegistry.IMG_MBEAN_INFO_WRAPPER);
        }

        if (obj instanceof MBeanAttributeInfoWrapper)
        {
            return JMXImageRegistry.loadImage(JMXImageRegistry.IMG_MBEAN_ATTR_INFO_WRAPPER);
        }

        if (obj instanceof MBeanOperationInfoWrapper)
        {
            return JMXImageRegistry.loadImage(JMXImageRegistry.IMG_MBEAN_OPERATION_INFO_WRAPPER);
        }

        return null;
    }

    /**
     * DOCUMENTME.
     *
     * @param   obj  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    @Override
    public String getText(Object obj)
    {
        if (obj instanceof DomainNode)
        {
            DomainNode node = (DomainNode) obj;
            return node.getDomain();
        }

        if (obj instanceof ObjectNameNode)
        {
            ObjectNameNode node = (ObjectNameNode) obj;

            if (flatLayout)
            {
                return node.getObjectName().getKeyPropertyListString();
            }
            else
            {
                return node.getValue();
            }
        }

        if (obj instanceof PropertyNode)
        {
            PropertyNode node = (PropertyNode) obj;
            return node.getValue();
        }

        if (obj instanceof MBeanInfoWrapper)
        {
            MBeanInfoWrapper wrapper = (MBeanInfoWrapper) obj;
            return wrapper.getObjectName().toString();
        }

        if (obj instanceof MBeanOperationInfoWrapper)
        {
            MBeanOperationInfoWrapper wrapper = (MBeanOperationInfoWrapper) obj;
            return MBeanUtils.prettySignature(wrapper.getMBeanOperationInfo());
        }

        if (obj instanceof MBeanAttributeInfoWrapper)
        {
            MBeanAttributeInfoWrapper wrapper = (MBeanAttributeInfoWrapper) obj;
            return wrapper.getMBeanAttributeInfo().getName();
        }
        return obj.toString();
    }

    /**
     * DOCUMENTME.
     *
     * @param  state  DOCUMENTME
     */
    public void setFlatLayout(boolean state)
    {
        flatLayout = state;
    }
}
