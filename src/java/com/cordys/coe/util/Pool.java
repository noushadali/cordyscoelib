package com.cordys.coe.util;

/**
 * The basic worker pool implementation.
 *
 * @author  jverhaar
 */
public class Pool
    implements IPoolDispatcher
{
    /**
     * Contains the maxsize in the pool.
     */
    private int m_iMaxSize;
    /**
     * Contains the size of the chain of pool workers.
     */
    private volatile int m_iSize = 0;
    /**
     * Holds the number of workers created.
     */
    private volatile int m_iWorkerID = 0;
    /**
     * Pointer to the first in the chain.
     */
    private volatile ChainEntry m_oFirst;
    /**
     * Pointer to the last in the chain.
     */
    private volatile ChainEntry m_oLast;
    /**
     * Lock for synchronizing the gets.
     */
    private Object m_oLock4Get = new Object();
    /**
     * Lock for synchronizing the puts.
     */
    private Object m_oLock4Put = new Object();
    /**
     * Contains the factory for creating new workers.
     */
    private IPoolWorkerFactory m_oWorkerFactory;

    /**
     * Creates a new Pool object.
     *
     * @param  aiMaxSize  DOCUMENTME
     * @param  aoFactory  DOCUMENTME
     */
    public Pool(int aiMaxSize, IPoolWorkerFactory aoFactory)
    {
        super();
        m_iMaxSize = aiMaxSize;
        m_oWorkerFactory = aoFactory;

        for (int i = 0; i < m_iMaxSize; ++i)
        {
            IPoolWorker oWorker = m_oWorkerFactory.createNewWorker(this);
            putWorker(oWorker);
        }
    }

    /**
     * This method creates the ID for the new worker.
     *
     * @return  The new ID.
     *
     * @see     com.cordys.coe.util.IPoolDispatcher#createIDForWorker()
     */
    public String createIDForWorker()
    {
        return String.valueOf(++m_iWorkerID);
    }

    /*
     * @see com.cordys.coe.util.IPoolDispatcher#getWorker()
     */
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public IPoolWorker getWorker()
    {
        try
        {
            synchronized (m_oLock4Get)
            {
                // While there is nothing in the pool wait
                while (null == m_oFirst)
                {
                    m_oLock4Get.wait();
                }

                // Take and get the first worker in the chain
                return getFirst();
            }
        }
        catch (InterruptedException ie)
        {
            Thread.currentThread().interrupt();
        }
        return null;
    }

    /**
     * Put a worker back in the pool, when it's reusable. The implementing pool should also free a
     * place in the pool for a next worker.
     *
     * @param  aoWorker  The pool worker.
     *
     * @see    com.cordys.coe.util.IPoolDispatcher#putWorker(com.cordys.coe.util.IPoolWorker)
     */
    public void putWorker(IPoolWorker aoWorker)
    {
        try
        {
            // Check if the pool is full, wait till
            synchronized (m_oLock4Put)
            {
                while ((m_iMaxSize > 0) && (m_iSize >= m_iMaxSize))
                {
                    m_oLock4Put.wait();
                }
            }

            synchronized (this)
            {
                if (!aoWorker.isReusable())
                {
                    aoWorker = m_oWorkerFactory.createNewWorker(this);
                }

                ChainEntry oNewEntry = new ChainEntry(aoWorker);

                if (null == m_oLast)
                {
                    m_oLast = m_oFirst = oNewEntry;
                }
                else
                {
                    // Adjust the chain
                    oNewEntry.previous = m_oLast;
                    m_oLast.next = oNewEntry;
                    m_oLast = oNewEntry;
                }

                // Update the size
                ++m_iSize;
                // getLogger().info("(put)Pool size = " + m_iSize);
            }

            synchronized (m_oLock4Get)
            {
                m_oLock4Get.notify();
            }
        }
        catch (InterruptedException ie)
        {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Returns the first worker in the chain.
     *
     * @return  The first worker in the pool.
     */
    private IPoolWorker getFirst()
    {
        // Used to backup the current first
        ChainEntry oOriginalFirst = null;

        synchronized (this)
        {
            oOriginalFirst = m_oFirst;

            // When at the last, make the whole chain null
            if (m_oFirst == m_oLast)
            {
                m_oFirst = m_oLast = null;
            }
            else
            {
                // Move to the next first
                m_oFirst = oOriginalFirst.next;
                // Make the first is the first
                m_oFirst.previous = null;
                // Make the original free
                oOriginalFirst.next = null;
            }

            // Adjust the size
            --m_iSize;
            // getLogger().info("(get)Pool size = " + m_iSize);
        }

        synchronized (m_oLock4Put)
        {
            m_oLock4Put.notify();
        }

        return oOriginalFirst.getWorker();
    }

    /**
     * Inner helper class for a chain of worker.
     *
     * @author  jverhaar
     */
    private class ChainEntry
    {
        /**
         * DOCUMENTME.
         */
        private IPoolWorker m_oWorker;
        /**
         * DOCUMENTME.
         */
        private ChainEntry next;
        /**
         * DOCUMENTME.
         */
        private ChainEntry previous;

        /**
         * Creates a new ChainEntry object.
         *
         * @param  aoWorker  DOCUMENTME
         */
        private ChainEntry(IPoolWorker aoWorker)
        {
            m_oWorker = aoWorker;
        }

        /**
         * This method gets the previous chain entry.
         *
         * @return  The previous chain entry.
         */
        @SuppressWarnings("unused")
		public ChainEntry getPrevious()
        {
            return previous;
        }

        /**
         * DOCUMENTME.
         *
         * @return  DOCUMENTME
         */
        private synchronized IPoolWorker getWorker()
        {
            return m_oWorker;
        }
    }
}
