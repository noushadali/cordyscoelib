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

import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

/**
 * DOCUMENTME.
 *
 * @author  $author$
 */
public class NodeBuilder
{
    /**
     * DOCUMENTME.
     *
     * @param  root  DOCUMENTME
     * @param  on    DOCUMENTME
     */
    public static void addToList(Node root, ObjectName on)
    {
        Node node = buildDomainNode(root, on.getDomain());
        node = buildObjectNameNode(node, "on", on.getKeyPropertyListString(), on); // $NON-NLS-1$
    }

    /**
     * DOCUMENTME.
     *
     * @param  root           DOCUMENTME
     * @param  on             DOCUMENTME
     * @param  bscConnection  DOCUMENTME
     */
    public static void addToTree(Node root, ObjectName on, MBeanServerConnection bscConnection)
    {
        Node node = buildDomainNode(root, on.getDomain());
        String keyPropertyListString = on.getKeyPropertyListString();
        String[] properties = keyPropertyListString.split(","); // $NON-NLS-1$

        StringBuilder sbPropertyName = new StringBuilder();

        for (int i = 0; i < properties.length; i++)
        {
            String property = properties[i];
            sbPropertyName.append(property);

            String key = property.substring(0, property.indexOf('='));
            String value = property.substring(property.indexOf('=') + 1, property.length());

            if (i == (properties.length - 1))
            {
                node = buildObjectNameNode(node, key, value, on);
            }
            else
            {
                // First we'll check if there is an object with this name. If so we'll create
                // an ObjectNameNode anyway.
                try
                {
                    Set<ObjectName> sResult = bscConnection.queryNames(new ObjectName(on.getDomain() +
                                                                                      ":" +
                                                                                      sbPropertyName
                                                                                      .toString()),
                                                                       null);

                    if ((sResult != null) && (sResult.size() > 0))
                    {
                        node = buildObjectNameNode(node, key, value,
                                                   (ObjectName) sResult.iterator().next());
                    }
                    else
                    {
                        node = buildPropertyNode(node, key, value);
                    }
                }
                catch (Exception e)
                {
                    node = buildPropertyNode(node, key, value);
                }
            }

            if (i < properties.length)
            {
                sbPropertyName.append(",");
            }
        }
    }

    /**
     * DOCUMENTME.
     *
     * @param   mbsc  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    public static Node createRoot(MBeanServerConnection mbsc)
    {
        return new Root(mbsc);
    }

    /**
     * DOCUMENTME.
     *
     * @param   parent  DOCUMENTME
     * @param   domain  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    static Node buildDomainNode(Node parent, String domain)
    {
        Node n = new DomainNode(parent, domain);

        if (parent != null)
        {
            return parent.addChildren(n);
        }
        return n;
    }

    /**
     * DOCUMENTME.
     *
     * @param   parent  DOCUMENTME
     * @param   key     DOCUMENTME
     * @param   value   DOCUMENTME
     * @param   on      DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    static Node buildObjectNameNode(Node parent, String key, String value, ObjectName on)
    {
        Node n = new ObjectNameNode(parent, key, value, on);

        if (parent != null)
        {
            return parent.addChildren(n);
        }
        return n;
    }

    /**
     * DOCUMENTME.
     *
     * @param   parent  DOCUMENTME
     * @param   key     DOCUMENTME
     * @param   value   DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    static Node buildPropertyNode(Node parent, String key, String value)
    {
        Node n = new PropertyNode(parent, key, value);

        if (parent != null)
        {
            return parent.addChildren(n);
        }
        return n;
    }
}
