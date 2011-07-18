/**
 * (c) 2006 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A simple output stream that writes the data to all the given output streams when it is being
 * written to this stream.
 *
 * @author  mpoyhone
 */
public class SplitOutputStream extends OutputStream
{
    /**
     * If defined, the underlying streams are not closed when close method is called for this
     * stream.
     */
    public static final int NOCLOSE = 1;
    /**
     * Calls the flush method in the underlying streams every time data is written.
     */
    public static final int AUTOFLUSH = 2;
    /**
     * Flags for the <code>osaOutputStreams</code> array.
     */
    private int[] iaFlagArray;

    /**
     * Contains all the underlying output streams.
     */
    private OutputStream[] osaOutputStreams;

    /**
     * Constructor for SplitOutputStream.
     *
     * @param  osaOutputs  Output streams.
     */
    public SplitOutputStream(OutputStream[] osaOutputs)
    {
        this.osaOutputStreams = osaOutputs;
        this.iaFlagArray = new int[this.osaOutputStreams.length];
    }

    /**
     * Constructor for SplitOutputStream.
     *
     * @param  osaOutputs   Output streams.
     * @param  iaFlagArray  Flags for each output stream.
     */
    public SplitOutputStream(OutputStream[] osaOutputs, int[] iaFlagArray)
    {
        if ((iaFlagArray != null) && (iaFlagArray.length != osaOutputs.length))
        {
            throw new IllegalArgumentException("Flag array must have the same size as the stream array");
        }
        this.osaOutputStreams = osaOutputs;
        this.iaFlagArray = iaFlagArray;
    }

    /**
     * @see  java.io.OutputStream#close()
     */
    @Override public void close()
                         throws IOException
    {
        if (osaOutputStreams == null)
        {
            return;
        }

        for (int i = 0; i < osaOutputStreams.length; i++)
        {
            OutputStream osStream = osaOutputStreams[i];

            if ((iaFlagArray[i] & NOCLOSE) == 0)
            {
                osStream.close();
            }
        }

        osaOutputStreams = null;
    }

    /**
     * Detaches the this stream from the real streams, but does not close them.
     */
    public void detach()
    {
        this.osaOutputStreams = null;
    }

    /**
     * @see  java.io.OutputStream#flush()
     */
    @Override public void flush()
                         throws IOException
    {
        if (osaOutputStreams == null)
        {
            return;
        }

        for (int i = 0; i < osaOutputStreams.length; i++)
        {
            OutputStream osStream = osaOutputStreams[i];

            osStream.flush();
        }
    }

    /**
     * @see  java.io.OutputStream#write(byte[])
     */
    @Override public void write(byte[] b)
                         throws IOException
    {
        if (osaOutputStreams == null)
        {
            return;
        }

        for (int i = 0; i < osaOutputStreams.length; i++)
        {
            OutputStream osStream = osaOutputStreams[i];

            osStream.write(b);

            if ((iaFlagArray[i] & AUTOFLUSH) != 0)
            {
                osStream.flush();
            }
        }
    }

    /**
     * @see  java.io.OutputStream#write(int)
     */
    @Override public void write(int b)
                         throws IOException
    {
        if (osaOutputStreams == null)
        {
            return;
        }

        for (int i = 0; i < osaOutputStreams.length; i++)
        {
            OutputStream osStream = osaOutputStreams[i];

            osStream.write(b);

            if ((iaFlagArray[i] & AUTOFLUSH) != 0)
            {
                osStream.flush();
            }
        }
    }

    /**
     * @see  java.io.OutputStream#write(byte[], int, int)
     */
    @Override public void write(byte[] b, int off, int len)
                         throws IOException
    {
        if (osaOutputStreams == null)
        {
            return;
        }

        for (int i = 0; i < osaOutputStreams.length; i++)
        {
            OutputStream osStream = osaOutputStreams[i];

            osStream.write(b, off, len);

            if ((iaFlagArray[i] & AUTOFLUSH) != 0)
            {
                osStream.flush();
            }
        }
    }
}
