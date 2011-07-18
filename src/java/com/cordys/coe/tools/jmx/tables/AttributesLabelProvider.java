/**
 * net.jmesnil.jmx.ui
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
package com.cordys.coe.tools.jmx.tables;

import com.cordys.coe.tools.jmx.JMXImageRegistry;
import com.cordys.coe.tools.jmx.StringUtils;
import com.cordys.coe.tools.jmx.resources.MBeanAttributeInfoWrapper;
import com.cordys.coe.util.swt.MessageBoxUtil;

import javax.management.MBeanAttributeInfo;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;

import org.eclipse.swt.graphics.Image;

/**
 * DOCUMENTME.
 *
 * @author  $author$
 */
class AttributesLabelProvider extends LabelProvider
    implements ITableLabelProvider
{
    /**
     * DOCUMENTME.
     *
     * @param   element      DOCUMENTME
     * @param   columnIndex  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    public Image getColumnImage(Object element, int columnIndex)
    {
        Image iReturn = null;

        if (!(element instanceof MBeanAttributeInfoWrapper))
        {
            return super.getImage(element);
        }

        MBeanAttributeInfoWrapper wrapper = (MBeanAttributeInfoWrapper) element;
        MBeanAttributeInfo attrInfo = wrapper.getMBeanAttributeInfo();

        switch (columnIndex)
        {
            case 0:
                if (attrInfo.isReadable() && attrInfo.isWritable())
                {
                    iReturn = JMXImageRegistry.loadImage(JMXImageRegistry.IMG_MBEAN_ATTR_READ_WRITE);
                }
                else if (attrInfo.isReadable())
                {
                    iReturn = JMXImageRegistry.loadImage(JMXImageRegistry.IMG_MBEAN_ATTR_READ);
                }
                else if (attrInfo.isWritable())
                {
                    iReturn = JMXImageRegistry.loadImage(JMXImageRegistry.IMG_MBEAN_ATTR_WRITE);
                }
                break;
        }
        return iReturn;
    }

    /**
     * DOCUMENTME.
     *
     * @param   element      DOCUMENTME
     * @param   columnIndex  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    public String getColumnText(Object element, int columnIndex)
    {
        if (!(element instanceof MBeanAttributeInfoWrapper))
        {
            return super.getText(element);
        }

        MBeanAttributeInfoWrapper wrapper = (MBeanAttributeInfoWrapper) element;
        MBeanAttributeInfo attrInfo = wrapper.getMBeanAttributeInfo();

        switch (columnIndex)
        {
            case 0:
                return attrInfo.getName();

            case 1:

                try
                {
                    String sValue = "<not readable>";

                    if (wrapper.getMBeanAttributeInfo().isReadable())
                    {
                        sValue = StringUtils.toString(wrapper.getValue(), false);
                    }
                    return sValue;
                }
                catch (Throwable t)
                {
                    MessageBoxUtil.showError("Error getting value of attribute " +
                                             attrInfo.getName(), t);
                    return "";
                }
        }
        return getText(element);
    }
}
