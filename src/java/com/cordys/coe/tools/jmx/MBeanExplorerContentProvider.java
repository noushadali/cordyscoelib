/**
 * Eclipse JMX Console
 * Copyright (C) 2007 Jeff Mesnil
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

import com.cordys.coe.tools.jmx.resources.MBeanFeatureInfoWrapper;
import com.cordys.coe.tools.jmx.tree.DomainNode;
import com.cordys.coe.tools.jmx.tree.Node;
import com.cordys.coe.tools.jmx.tree.ObjectNameNode;
import com.cordys.coe.tools.jmx.tree.Root;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * DOCUMENTME.
 *
 * @author  $author$
 */
class MBeanExplorerContentProvider
    implements IStructuredContentProvider, ITreeContentProvider
{
    /**
     * DOCUMENTME.
     */
    private boolean flatLayout;

    /**
     * Creates a new MBeanExplorerContentProvider object.
     */
    public MBeanExplorerContentProvider()
    {
    }

    /**
     * DOCUMENTME.
     */
    public void dispose()
    {
    }

    /**
     * DOCUMENTME.
     *
     * @param   parent  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    public Object[] getChildren(Object parent)
    {
        if (parent instanceof Root)
        {
            Root root = (Root) parent;
            return root.getChildren();
        }

        if (parent instanceof DomainNode)
        {
            DomainNode node = (DomainNode) parent;

            if (flatLayout)
            {
                List<?> objectNameNodes = findOnlyObjectNames(node);
                return objectNameNodes.toArray();
            }
            else
            {
                return node.getChildren();
            }
        }

        if (parent instanceof ObjectNameNode)
        {
            ObjectNameNode node = (ObjectNameNode) parent;
            ArrayList<Object> alReturn = new ArrayList<Object>(Arrays.asList((Object[]) node.getChildren()));

            // alReturn.addAll(Arrays.asList((Object[])node.getMbeanInfoWrapper().getMBeanFeatureInfos()));
            return alReturn.toArray();
        }

        if (parent instanceof Node)
        {
            Node node = (Node) parent;
            return node.getChildren();
        }
        return new Object[0];
    }

    /**
     * DOCUMENTME.
     *
     * @param   parent  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    public Object[] getElements(Object parent)
    {
        return getChildren(parent);
    }

    /**
     * DOCUMENTME.
     *
     * @param   child  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    public Object getParent(Object child)
    {
        if (child instanceof Node)
        {
            Node node = (Node) child;
            return node.getParent();
        }
        return null;
    }

    /**
     * DOCUMENTME.
     *
     * @param   parent  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    public boolean hasChildren(Object parent)
    {
        if (parent instanceof ObjectNameNode)
        {
            ObjectNameNode node = (ObjectNameNode) parent;
            return (node.getMbeanInfoWrapper().getMBeanFeatureInfos().length > 0);
        }

        if (parent instanceof Node)
        {
            Node node = (Node) parent;
            return (node.getChildren().length > 0);
        }

        if (parent instanceof MBeanFeatureInfoWrapper)
        {
            return false;
        }
        return true;
    }

    /**
     * DOCUMENTME.
     *
     * @param  v         DOCUMENTME
     * @param  oldInput  DOCUMENTME
     * @param  newInput  DOCUMENTME
     */
    public void inputChanged(Viewer v, Object oldInput, Object newInput)
    {
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

    /**
     * DOCUMENTME.
     *
     * @param   node  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    private List<Node> findOnlyObjectNames(Node node)
    {
        List<Node> objectNameNodes = new ArrayList<Node>();
        Node[] children = node.getChildren();

        for (int i = 0; i < children.length; i++)
        {
            Node child = children[i];

            if (child instanceof ObjectNameNode)
            {
                objectNameNodes.add(child);
            }
            else
            {
                objectNameNodes.addAll(findOnlyObjectNames(child));
            }
        }
        return objectNameNodes;
    }
}
