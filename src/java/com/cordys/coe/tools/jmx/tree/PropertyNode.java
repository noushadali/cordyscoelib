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

/**
 * DOCUMENTME.
 *
 * @author  $author$
 */
public class PropertyNode extends Node
{
    /**
     * DOCUMENTME.
     */
    private String key;
    /**
     * DOCUMENTME.
     */
    private String value;

    /**
     * Creates a new PropertyNode object.
     *
     * @param  parent  DOCUMENTME
     * @param  key     DOCUMENTME
     * @param  value   DOCUMENTME
     */
    PropertyNode(Node parent, String key, String value)
    {
        super(parent);
        this.key = key;
        this.value = value;
    }

    /**
     * DOCUMENTME.
     *
     * @param   o  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    public int compareTo(Node o)
    {
        PropertyNode other = (PropertyNode) o;

        if (key.equals(other.key))
        {
            return value.compareTo(other.value);
        }
        return key.compareTo(other.key);
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

        if (obj == null)
        {
            return false;
        }

        if (getClass() != obj.getClass())
        {
            return false;
        }

        final PropertyNode other = (PropertyNode) obj;

        if (key == null)
        {
            if (other.key != null)
            {
                return false;
            }
        }
        else if (!key.equals(other.key))
        {
            return false;
        }

        if (value == null)
        {
            if (other.value != null)
            {
                return false;
            }
        }
        else if (!value.equals(other.value))
        {
            return false;
        }

        if (parent == null)
        {
            if (other.parent != null)
            {
                return false;
            }
        }
        else if (!parent.equals(other.parent))
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
    public String getKey()
    {
        return key;
    }

    /**
     * DOCUMENTME.
     *
     * @return  DOCUMENTME
     */
    public String getValue()
    {
        return value;
    }

    /**
     * DOCUMENTME.
     *
     * @return  DOCUMENTME
     */
    @Override public int hashCode()
    {
        final int PRIME = 31;
        int result = 1;
        result = (PRIME * result) + ((key == null) ? 0 : key.hashCode());
        result = (PRIME * result) + ((value == null) ? 0 : value.hashCode());
        result = (PRIME * result) + ((parent == null) ? 0 : parent.hashCode());
        return result;
    }

    /**
     * DOCUMENTME.
     *
     * @return  DOCUMENTME
     */
    @Override public String toString()
    {
        return "PropertyNode[" + key + "=" + value + "]"; // $NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
}
