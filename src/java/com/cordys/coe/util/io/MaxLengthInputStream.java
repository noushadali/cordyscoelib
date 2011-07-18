/**
 * (c) 2006 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * A wrapper input stream that limits the number of bytes that can be read from the underlying
 * stream.
 *
 * @author  mpoyhone
 */
public class MaxLengthInputStream extends InputStream
{
    /**
     * Source stream.
     */
    private InputStream isSrcStream;
    /**
     * Number of bytes currently read from the source.
     */
    private long lCurRead;

    /**
     * Maximum number of bytes that can be read from the source.
     */
    private long lMaxLength;

    /**
     * Constructor for MaxLengthInputStream.
     *
     * @param  isSource    Underlying input stream.
     * @param  lMaxLength
     */
    public MaxLengthInputStream(InputStream isSource, long lMaxLength)
    {
        if (isSource == null)
        {
            throw new NullPointerException("Source is null.");
        }

        if (lMaxLength < 0)
        {
            throw new IllegalArgumentException("Maximum length cannot be negative.");
        }

        this.isSrcStream = isSource;
        this.lMaxLength = lMaxLength;

        lCurRead = 0;
    }

    /**
     * @see  java.io.InputStream#available()
     */
    @Override public int available()
                            throws IOException
    {
        if (isSrcStream == null)
        {
            return 0;
        }

        return (int) Math.min(bytesLeft(), isSrcStream.available());
    }

    /**
     * @see  java.io.InputStream#close()
     */
    @Override public void close()
                         throws IOException
    {
        if (isSrcStream != null)
        {
            isSrcStream.close();
            isSrcStream = null;
        }
    }

    /**
     * Detaches the source stream from this stream, but does not close the source stream. Read
     * method act as the stream has reached the end.
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

        isSrcStream = null;
    }

    /**
     * @see  java.io.InputStream#mark(int)
     */
    @Override public synchronized void mark(int readlimit)
    {
        if (isSrcStream != null)
        {
            isSrcStream.mark(readlimit);
        }
    }

    /**
     * @see  java.io.InputStream#markSupported()
     */
    @Override public boolean markSupported()
    {
        if (isSrcStream != null)
        {
            return isSrcStream.markSupported();
        }

        return false;
    }

    /**
     * @see  java.io.InputStream#read()
     */
    @Override public int read()
                       throws IOException
    {
        if (isSrcStream == null)
        {
            return -1;
        }

        if (bytesLeft() <= 0)
        {
            return -1;
        }

        int iRes = isSrcStream.read();

        if (iRes >= 0)
        {
            lCurRead++;
        }

        return iRes;
    }

    /**
     * @see  java.io.InputStream#read(byte[])
     */
    @Override public int read(byte[] b)
                       throws IOException
    {
        return read(b, 0, b.length);
    }

    /**
     * @see  java.io.InputStream#read(byte[], int, int)
     */
    @Override public int read(byte[] b, int off, int len)
                       throws IOException
    {
        if (isSrcStream == null)
        {
            return -1;
        }

        long lAmount = Math.min(bytesLeft(), len);

        if (lAmount <= 0)
        {
            return -1;
        }

        int iRes = isSrcStream.read(b, off, (int) lAmount);

        if (iRes > 0)
        {
            lCurRead += iRes;
        }

        return iRes;
    }

    /**
     * @see  java.io.InputStream#reset()
     */
    @Override public synchronized void reset()
                                      throws IOException
    {
        if (isSrcStream != null)
        {
            isSrcStream.reset();
        }
    }

    /**
     * @see  java.io.InputStream#skip(long)
     */
    @Override public long skip(long n)
                        throws IOException
    {
        if (isSrcStream == null)
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
     * Returns the number of bytes left in this stream.
     *
     * @return  Number of bytes left.
     */
    private long bytesLeft()
    {
        return lMaxLength - lCurRead;
    }
}
