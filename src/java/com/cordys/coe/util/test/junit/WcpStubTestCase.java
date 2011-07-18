/**
 * (c) 2007 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util.test.junit;

import com.cordys.coe.actester.IStubFactory;
import com.cordys.coe.actester.StubFactoryFactory;

/**
 * Helper JUnit test case for using WCP stub classes.
 *
 * @author  mpoyhone
 */
public class WcpStubTestCase extends NomTestCase
{
    /**
     * Contains the stub factory for accessing the internal stub interfaces.
     */
    protected static IStubFactory stubFactory;

    static
    {
        try
        {
            stubFactory = StubFactoryFactory.createStubFactory();
        }
        catch (Exception e)
        {
            throw new IllegalStateException("Unable to load the stub factory.", e);
        }
    }
}
