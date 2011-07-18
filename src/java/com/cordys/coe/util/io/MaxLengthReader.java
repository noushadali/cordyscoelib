/**
 * (c) 2007 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util.io;

import java.io.IOException;
import java.io.Reader;

/**
 * A wrapper reader that limits the number of characters that can be read from the underlying
 * reader.
 *
 * @author  mpoyhone
 */
public class MaxLengthReader extends Reader
{
    /**
     * Source reader.
     */
    private Reader rSrcReader;

    /**
     * Maximum number of chacters that can be read from the source.
     */
    private long lMaxLength;
    /**
     * Number of characters currently read from the source.
     */
    private long lCurRead;

    /**
     * Constructor for MaxLengthInputStream.
     *
     * @param  rSource     Underlying input stream.
     * @param  lMaxLength
     */
    public MaxLengthReader(Reader rSource, long lMaxLength)
    {
        if (rSource == null)
        {
            throw new NullPointerException("Source is null.");
        }

        if (lMaxLength < 0)
        {
            throw new IllegalArgumentException("Maximum length cannot be negative.");
        }

        this.rSrcReader = rSource;
        this.lMaxLength = lMaxLength;

        lCurRead = 0;
    }

    /**
     * Skips the stream until the read given length has been reached.
     *
     * @return  Number of bytes read.
     *
     * @throws  IOException  Thrown if the skip operation failed.
     */
    public long skipUntilEnd()
                      throws IOException
    {
        long lAmount = bytesLeft();

        return skip(lAmount);
    }

    /**
     * Detaches the source reader from this reader, but does not close the source reader. Read
     * method act as the reader has reached the end.
     *
     * @param   bSkipUntilEnd  If <code>true</code> the <code>skipUntilEnd</code> method will be
     *                         called first.
     *
     * @throws  IOException  Thrown if the skip operation failed.
     */
    public void detachSource(boolean bSkipUntilEnd)
                      throws IOException
    {
        if (bSkipUntilEnd)
        {
            skipUntilEnd();
        }

        rSrcReader = null;
    }

    /**
     * @see  java.io.Reader#close()
     */
    @Override public void close()
                         throws IOException
    {
        if (rSrcReader != null)
        {
            rSrcReader.close();
            rSrcReader = null;
        }
    }

    /**
     * @see  java.io.Reader#mark(int)
     */
    @Override public void mark(int readAheadLimit)
                        throws IOException
    {
        if (rSrcReader != null)
        {
            rSrcReader.mark(readAheadLimit);
        }
    }

    /**
     * @see  java.io.Reader#markSupported()
     */
    @Override public boolean markSupported()
    {
        if (rSrcReader != null)
        {
            return rSrcReader.markSupported();
        }

        return false;
    }

    /**
     * @see  java.io.Reader#read()
     */
    @Override public int read()
                       throws IOException
    {
        if (rSrcReader == null)
        {
            return -1;
        }

        if (bytesLeft() <= 0)
        {
            return -1;
        }

        int iRes = rSrcReader.read();

        if (iRes >= 0)
        {
            lCurRead++;
        }

        return iRes;
    }

    /**
     * @see  java.io.Reader#read(char[], int, int)
     */
    @Override public int read(char[] b, int off, int len)
                       throws IOException
    {
        if (rSrcReader == null)
        {
            return -1;
        }

        long lAmount = Math.min(bytesLeft(), len);

        if (lAmount <= 0)
        {
            return -1;
        }

        int iRes = rSrcReader.read(b, off, (int) lAmount);

        if (iRes > 0)
        {
            lCurRead += iRes;
        }

        return iRes;
    }

    /**
     * @see  java.io.Reader#ready()
     */
    @Override public boolean ready()
                            throws IOException
    {
        if (rSrcReader == null)
        {
            return false;
        }

        return rSrcReader.ready();
    }

    /**
     * @see  java.io.Reader#reset()
     */
    @Override public void reset()
                         throws IOException
    {
        if (rSrcReader == null)
        {
            return;
        }

        rSrcReader.reset();
    }

    /**
     * @see  java.io.Reader#skip(long)
     */
    @Override public long skip(long n)
                        throws IOException
    {
        if (rSrcReader == null)
        {
            return 0;
        }

        long lAmount = Math.min(bytesLeft(), n);

        if (lAmount <= 0)
        {
            return 0;
        }

        long lRes = super.skip(lAmount);

        if (lRes > 0)
        {
            lCurRead += lRes;
        }

        return lRes;
    }

    /**
     * Returns the number of bytes left in this stream.
     *
     * @return  Number of bytes left.
     */
    private long bytesLeft()
    {
        return lMaxLength - lCurRead;
    }
}
