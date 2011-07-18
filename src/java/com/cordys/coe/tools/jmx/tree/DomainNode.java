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
public class DomainNode extends Node
{
    /**
     * DOCUMENTME.
     */
    private String domain;

    /**
     * Creates a new DomainNode object.
     *
     * @param  root    DOCUMENTME
     * @param  domain  DOCUMENTME
     */
    DomainNode(Node root, String domain)
    {
        super(root);
        this.domain = domain;
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
        DomainNode other = (DomainNode) o;
        return domain.compareTo(other.domain);
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

        final DomainNode other = (DomainNode) obj;

        if (domain == null)
        {
            if (other.domain != null)
            {
                return false;
            }
        }
        else if (!domain.equals(other.domain))
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
    public String getDomain()
    {
        return domain;
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
        result = (PRIME * result) + ((domain == null) ? 0 : domain.hashCode());
        return result;
    }

    /**
     * DOCUMENTME.
     *
     * @return  DOCUMENTME
     */
    @Override public String toString()
    {
        return "DomainNode[domain=" + domain + "]"; // $NON-NLS-1$ //$NON-NLS-2$
    }
}
