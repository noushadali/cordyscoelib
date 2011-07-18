/**
 * Eclipse JMX Console
 * Copyright (C) 2006 Jeff Mesnil
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
package com.cordys.coe.tools.jmx.resources;

import javax.management.MBeanOperationInfo;

/**
 * DOCUMENTME.
 *
 * @author  $author$
 */
public class Impact
{
    /**
     * DOCUMENTME.
     */
    public static final Impact ACTION = new Impact("ACTION"); // $NON-NLS-1$
    /**
     * DOCUMENTME.
     */
    public static final Impact ACTION_INFO = new Impact("ACTION_INFO"); // $NON-NLS-1$
    /**
     * DOCUMENTME.
     */
    public static final Impact INFO = new Impact("INFO"); // $NON-NLS-1$
    /**
     * DOCUMENTME.
     */
    public static final Impact UNKNOWN = new Impact("UNKNOWN"); // $NON-NLS-1$
    /**
     * DOCUMENTME.
     */
    private final String name;

    /**
     * Creates a new Impact object.
     *
     * @param  name  DOCUMENTME
     */
    private Impact(String name)
    {
        this.name = name;
    }

    /**
     * DOCUMENTME.
     *
     * @param   impact  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    public static Impact parseInt(int impact)
    {
        switch (impact)
        {
            case MBeanOperationInfo.ACTION:
                return ACTION;

            case MBeanOperationInfo.ACTION_INFO:
                return ACTION_INFO;

            case MBeanOperationInfo.INFO:
                return INFO;

            default:
                return UNKNOWN;
        }
    }

    /**
     * DOCUMENTME.
     *
     * @return  DOCUMENTME
     */
    @Override public String toString()
    {
        return name;
    }
}
