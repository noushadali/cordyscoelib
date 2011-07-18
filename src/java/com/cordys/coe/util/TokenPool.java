package com.cordys.coe.util;

/**
 * This class provides a token pool. The pool is created with a max size which is the total number
 * of tokens in the pool.
 *
 * @author  jverhaar
 */
public class TokenPool extends Pool
{
    /**
     * Constructor.
     *
     * @param  aiMaxSize  The maximum number of tokens.
     */
    public TokenPool(int aiMaxSize)
    {
        this(aiMaxSize, getFactory());
    }

    /**
     * Creates a new TokenPool object.
     *
     * @param  aiMaxSize  The maximum number of tokens.
     * @param  aoFactory  The token factory to use.
     */
    private TokenPool(int aiMaxSize, IPoolWorkerFactory aoFactory)
    {
        super(aiMaxSize, aoFactory);
    }

    /**
     * This method returns the factory used for this pool.
     *
     * @return  The token factory.
     */
    private static IPoolWorkerFactory getFactory()
    {
        return new TokenFactory();
    }
}

/**
 * Helper class as token.
 *
 * @author  jverhaar
 */
class Token
    implements IPoolWorker
{
    /**
     * Holds the ID of the token.
     */
    private String m_sID;

    /**
     * Creates a new Token object.
     *
     * @param  sID  The ID of the token.
     */
    public Token(String sID)
    {
        m_sID = sID;
    }

    /**
     * This method gets the ID of the pool worker.
     *
     * @return  The ID of the pool worker.
     */
    public String getID()
    {
        return m_sID;
    }

    /**
     * Returns whether or not the worker can be placed back in the pool after working.
     *
     * @return  Whether or not the worker can be placed back in the pool after working.
     *
     * @see     com.cordys.coe.util.IPoolWorker#isReusable()
     */
    public boolean isReusable()
    {
        return true;
    }
}

/**
 * Inner helper class for creating new tokens.
 *
 * @author  jverhaar
 */
class TokenFactory
    implements IPoolWorkerFactory
{
    /**
     * Creates a new TokenFactory object.
     */
    public TokenFactory()
    {
    }

    /**
     * Creates a new worker in the pool.
     *
     * @param   aoPool  The pool dispatcher.
     *
     * @return  A worker.
     *
     * @see     com.cordys.coe.util.IPoolWorkerFactory#createNewWorker(com.cordys.coe.util.IPoolDispatcher)
     */
    public IPoolWorker createNewWorker(IPoolDispatcher aoPool)
    {
        return new Token(aoPool.createIDForWorker());
    }
}
