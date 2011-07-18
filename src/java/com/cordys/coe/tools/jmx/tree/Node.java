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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DOCUMENTME.
 *
 * @author  $author$
 */
public abstract class Node
    implements Comparable<Node>
{
    /**
     * DOCUMENTME.
     */
    List<Node> children = new ArrayList<Node>();
    /**
     * DOCUMENTME.
     */
    Node parent;

    /**
     * Creates a new Node object.
     *
     * @param  parent  DOCUMENTME
     */
    Node(Node parent)
    {
        this.parent = parent;
    }

    /**
     * DOCUMENTME.
     *
     * @return  DOCUMENTME
     */
    public Node[] getChildren()
    {
        return children.toArray(new Node[children.size()]);
    }

    /**
     * DOCUMENTME.
     *
     * @return  DOCUMENTME
     */
    public Node getParent()
    {
        return parent;
    }

    /**
     * DOCUMENTME.
     *
     * @param   node  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    Node addChildren(Node node)
    {
        if (!children.contains(node))
        {
            children.add(node);
            Collections.sort(children);
            return node;
        }
        else
        {
            return children.get(children.indexOf(node));
        }
    }

    /**
     * DOCUMENTME.
     *
     * @param   parent  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    Root getRoot(Node parent)
    {
        if (parent.getParent() == null)
        {
            return (Root) parent;
        }
        return getRoot(parent.getParent());
    }
}
