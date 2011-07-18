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

import com.cordys.coe.tools.jmx.resources.MBeanAttributeInfoWrapper;
import com.cordys.coe.tools.jmx.resources.MBeanInfoWrapper;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * DOCUMENTME.
 *
 * @author  $author$
 */
class AttributesContentProvider
    implements IStructuredContentProvider
{
    /**
     * DOCUMENTME.
     */
    private MBeanAttributeInfoWrapper[] attributes;

    /**
     * DOCUMENTME.
     */
    public void dispose()
    {
    }

    /**
     * DOCUMENTME.
     *
     * @param   inputElement  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    public Object[] getElements(Object inputElement)
    {
        if (attributes == null)
        {
            return new Object[0];
        }
        return attributes;
    }

    /**
     * DOCUMENTME.
     *
     * @param  viewer    DOCUMENTME
     * @param  oldInput  DOCUMENTME
     * @param  newInput  DOCUMENTME
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
        if (newInput instanceof MBeanAttributeInfoWrapper[])
        {
            attributes = (MBeanAttributeInfoWrapper[]) newInput;
        }
        else if (newInput instanceof MBeanInfoWrapper)
        {
            MBeanInfoWrapper biwInfo = (MBeanInfoWrapper) newInput;
            attributes = biwInfo.getMBeanAttributeInfoWrappers();
        }
    }
}
