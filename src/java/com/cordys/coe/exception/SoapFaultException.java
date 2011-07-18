/**
 * (c) 2007 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.exception;

import com.cordys.coe.util.soap.SoapFaultInfo;

/**
 * Exception class for SOAP:Faults. This class contains the information in a SoapFaultInfo object.
 *
 * @author  mpoyhone
 * @see     SoapFaultInfo
 */
public class SoapFaultException extends Exception
{
    /**
     * Contains the SOAP:Fault information.
     */
    private SoapFaultInfo faultInfo;

    /**
     * Constructor for SoapFaultException.
     *
     * @param  info  SOAP:Fault information.
     */
    public SoapFaultException(SoapFaultInfo info)
    {
        super();
        this.faultInfo = info;
    }

    /**
     * Constructor for SoapFaultException.
     *
     * @param  info   SOAP:Fault information.
     * @param  cause  Causing exception
     */
    public SoapFaultException(SoapFaultInfo info, Throwable cause)
    {
        super(cause);
        this.faultInfo = info;
    }

    /**
     * Returns the SOAP:Fault infomation object.
     *
     * @return  Returns the SOAP:Fault infomation object.
     */
    public SoapFaultInfo getFaultInfo()
    {
        return faultInfo;
    }

    /**
     * @see  java.lang.Throwable#getLocalizedMessage()
     */
    @Override public String getLocalizedMessage()
    {
        return super.getMessage();
    }

    /**
     * @see  java.lang.Throwable#getMessage()
     */
    @Override public String getMessage()
    {
        return (faultInfo != null) ? faultInfo.getFaultstring() : "";
    }
}
