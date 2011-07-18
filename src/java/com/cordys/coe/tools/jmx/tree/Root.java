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

import javax.management.MBeanServerConnection;

/**
 * DOCUMENTME.
 *
 * @author  $author$
 */
public class Root extends Node
{
    /**
     * DOCUMENTME.
     */
    private MBeanServerConnection mbsc;

    /**
     * Creates a new Root object.
     *
     * @param  mbsc  DOCUMENTME
     */
    Root(MBeanServerConnection mbsc)
    {
        super(null);
        this.mbsc = mbsc;
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
        return 0;
    }

    /**
     * DOCUMENTME.
     *
     * @return  DOCUMENTME
     */
    @Override public String toString()
    {
        return "Root"; // $NON-NLS-1$
    }

    /**
     * DOCUMENTME.
     *
     * @return  DOCUMENTME
     */
    public MBeanServerConnection getMBeanServerConnection()
    {
        return mbsc;
    }
}
