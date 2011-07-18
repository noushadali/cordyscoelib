/**
 * (c) 2006 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A simple input stream that writes the data to the given output stream when it is being read from
 * the given input stream. This class can be used for sending data to another destination, e.g. for
 * debug log.
 *
 * @author  mpoyhone
 */
public class SplitInputStream extends InputStream
{
    /**
     * DOCUMENTME.
     */
    private InputStream isInput;
    /**
     * DOCUMENTME.
     */
    private OutputStream osSplitOutput;

    /**
     * Constructor for SplitInputStream.
     *
     * @param  isInput        The real input stream from which this input stream read data.
     * @param  osSplitOutput  Data is written to this output stream when is has been read from the
     *                        input stream.
     */
    public SplitInputStream(InputStream isInput, OutputStream osSplitOutput)
    {
        this.isInput = isInput;
        this.osSplitOutput = osSplitOutput;
    }

    /**
     * @see  java.io.InputStream#available()
     */
    @Override public int available()
                            throws IOException
    {
        if (isInput == null)
        {
            return 0;
        }

        return isInput.available();
    }

    /**
     * @see  java.io.InputStream#close()
     */
    @Override public void close()
                         throws IOException
    {
        if (isInput != null)
        {
            isInput.close();
        }

        isInput = null;
    }

    /**
     * Detaches the this stream from the real streams, but does not close them.
     */
    public void detach()
    {
        this.isInput = null;
        this.osSplitOutput = null;
    }

    /**
     * @see  java.io.InputStream#mark(int)
     */
    @Override public synchronized void mark(int readlimit)
    {
        if (isInput == null)
        {
            return;
        }

        isInput.mark(readlimit);
    }

    /**
     * @see  java.io.InputStream#markSupported()
     */
    @Override public boolean markSupported()
    {
        if (isInput == null)
        {
            return false;
        }

        return isInput.markSupported();
    }

    /**
     * @see  java.io.InputStream#read()
     */
    @Override public int read()
                       throws IOException
    {
        if (isInput == null)
        {
            return 0;
        }

        int iChar = isInput.read();

        if (iChar >= 0)
        {
            osSplitOutput.write(iChar);
        }

        return iChar;
    }

    /**
     * @see  java.io.InputStream#read(byte[])
     */
    @Override public int read(byte[] b)
                       throws IOException
    {
        if (isInput == null)
        {
            return 0;
        }

        int iCount = isInput.read(b);

        if (iCount > 0)
        {
            osSplitOutput.write(b, 0, iCount);
        }

        return iCount;
    }

    /**
     * @see  java.io.InputStream#read(byte[], int, int)
     */
    @Override public int read(byte[] b, int off, int len)
                       throws IOException
    {
        if (isInput == null)
        {
            return 0;
        }

        int iCount = isInput.read(b, off, len);

        if (iCount > 0)
        {
            osSplitOutput.write(b, off, iCount);
        }

        return iCount;
    }

    /**
     * @see  java.io.InputStream#reset()
     */
    @Override public synchronized void reset()
                                      throws IOException
    {
        if (isInput == null)
        {
            return;
        }

        isInput.reset();
    }

    /**
     * @see  java.io.InputStream#skip(long)
     */
    @Override public long skip(long n)
                        throws IOException
    {
        if (isInput == null)
        {
            return 0;
        }

        return isInput.skip(n);
    }
}
