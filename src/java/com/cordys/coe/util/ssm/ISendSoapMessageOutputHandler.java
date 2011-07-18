package com.cordys.coe.util.ssm;

import com.cordys.coe.exception.GeneralException;

import java.io.File;

/**
 * This interface describes the output handlers that can be written for the SendSoapMessage class..
 *
 * @author  pgussow
 */
public interface ISendSoapMessageOutputHandler
{
    /**
     * This method is called with the parameter XML to configure the output handler.
     *
     * @param   iConfig  The configuration XML.
     * @param   fOutput  The output folder.
     *
     * @throws  GeneralException  In case of any exceptions.
     */
    void configure(int iConfig, File fOutput)
            throws GeneralException;

    /**
     * This method is called to process the actual response.
     *
     * @param   iResponse  the actual response that was received.
     *
     * @return  true if the handler has processed the response. When this method returns false the
     *          default handler is used.
     *
     * @throws  GeneralException  In case of any exceptions
     */
    boolean processMethodResponse(int iResponse)
                           throws GeneralException;
}
