package com.cordys.coe.tools.testtool;

import com.cordys.coe.util.cgc.ICordysGatewayClient;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * This thread will execute the request using a specific timeout. When the response is received it
 * will notify the main UI that the response was received.
 *
 * @author  pgussow
 */
public class RequestRunnerThread extends Thread
{
    /**
     * Holds the thread sequence used.
     */
    private static volatile int s_iThreadCount = 0;
    /**
     * Holds whether or not the thread should be stopped.
     */
    private boolean m_bShouldStop;
    /**
     * Holds the queue to read the requests from.
     */
    private ArrayBlockingQueue<RequestObject> m_qRequestQueue;
    /**
     * Holds the queue to put the response on.
     */
    private ArrayBlockingQueue<RequestObject> m_qResponseQueue;

    /**
     * Creates a new RequestRunnerThread object.
     *
     * @param  qRequestQueue   The queue from which to read the requests.
     * @param  qResponseQueue  The queue to put the responses on.
     */
    public RequestRunnerThread(ArrayBlockingQueue<RequestObject> qRequestQueue,
                               ArrayBlockingQueue<RequestObject> qResponseQueue)
    {
        super("RequestRunnerThread-" + s_iThreadCount++);

        m_qRequestQueue = qRequestQueue;
        m_qResponseQueue = qResponseQueue;
    }

    /**
     * This method is called when the thread is run.
     *
     * @see  java.lang.Runnable#run()
     */
    @Override public void run()
    {
        while (!shouldStop())
        {
            RequestObject roRequest = null;

            try
            {
                roRequest = m_qRequestQueue.take();

                try
                {
                    roRequest.start();

                    ICordysGatewayClient cgc = roRequest.getCGC();

                    try
                    {
                        roRequest.setResponse(cgc.requestFromCordys(roRequest.getRequest(),
                                                                    roRequest.getTimeout(),
                                                                    roRequest.getOrganization(),
                                                                    null));
                        roRequest.setExecutedOK(true);
                    }
                    catch (Exception e)
                    {
                        roRequest.setException(e);
                        roRequest.setExecutedOK(false);
                    }
                }
                finally
                {
                    roRequest.finish();
                    m_qResponseQueue.put(roRequest);
                }
            }
            catch (InterruptedException e1)
            {
                e1.printStackTrace();
            }
        }
    }

    /**
     * This method sets wether or not the thread should be stopped.
     *
     * @param  bShouldStop  Whether or not the thread should be stopped.
     */
    public void setShouldStop(boolean bShouldStop)
    {
        m_bShouldStop = bShouldStop;

        if (m_bShouldStop == true)
        {
            // Interrupt the thread.
            this.interrupt();
        }
    }

    /**
     * This method gets whether or not the thread should be stopped.
     *
     * @return  Whether or not the thread should be stopped.
     */
    public boolean shouldStop()
    {
        return m_bShouldStop;
    }
}
