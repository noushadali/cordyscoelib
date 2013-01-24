/**
 * (c) 2007 Cordys R&D B.V. All rights reserved. The computer program(s) is the proprietary information of Cordys B.V. and
 * provided under the relevant License Agreement containing restrictions on use and disclosure. Use is subject to the License
 * Agreement.
 */
package com.cordys.coe.util.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.cordys.coe.util.FileUtils;

import junit.framework.TestCase;

/**
 * Test case for class MaxLengthInputStream.
 * 
 * @author mpoyhone
 */
public class MaxLengthInputStreamTest extends TestCase
{

    /**
     * Test method for {@link com.cordys.coe.util.io.MaxLengthInputStream#read()}.
     */
    public void testRead() throws Exception
    {
        byte[] data;
        MaxLengthInputStream input;

        // ////////////////////////////////////////////
        // Test with length well past the end.
        // ////////////////////////////////////////////
        data = "my data".getBytes();
        input = new MaxLengthInputStream(new ByteArrayInputStream(data), data.length + 10);

        for (byte tmp : data)
        {
            int ch = input.read();

            assertEquals(tmp, ch);
        }

        // ////////////////////////////////////////////
        // Test with length one past the end.
        // ////////////////////////////////////////////
        data = "my data".getBytes();
        input = new MaxLengthInputStream(new ByteArrayInputStream(data), data.length + 1);

        for (byte tmp : data)
        {
            int ch = input.read();

            assertEquals(tmp, ch);
        }

        // ////////////////////////////////////////////
        // Test with equal length.
        // ////////////////////////////////////////////
        data = "my data".getBytes();
        input = new MaxLengthInputStream(new ByteArrayInputStream(data), data.length);

        for (byte tmp : data)
        {
            int ch = input.read();

            assertEquals(tmp, ch);
        }

        // ////////////////////////////////////////////
        // Test with length one less.
        // ////////////////////////////////////////////
        data = "my data".getBytes();
        input = new MaxLengthInputStream(new ByteArrayInputStream(data), data.length - 1);

        for (int i = 0; i < data.length - 1; i++)
        {
            int ch = input.read();

            assertEquals(data[i], ch);
        }
    }

    /**
     * Test method for {@link com.cordys.coe.util.io.MaxLengthInputStream#read(char[], int, int)}.
     */
    public void testReadCharArrayIntInt() throws Exception
    {
        byte[] data;
        byte[] buf = new byte[100];
        int amount;
        MaxLengthInputStream input;

        // ////////////////////////////////////////////
        // Test with length well past the end.
        // ////////////////////////////////////////////
        data = "my data".getBytes();
        input = new MaxLengthInputStream(new ByteArrayInputStream(data), data.length + 10);
        amount = input.read(buf, 0, buf.length);

        assertEquals(data.length, amount);
        assertTrue(areArraysEqual(data, 0, data.length, buf, 0, amount));
        assertEquals(-1, input.read(buf, 0, buf.length));

        // ////////////////////////////////////////////
        // Test with length one past the end.
        // ////////////////////////////////////////////
        data = "my data".getBytes();
        input = new MaxLengthInputStream(new ByteArrayInputStream(data), data.length + 1);
        amount = input.read(buf, 0, buf.length);

        assertEquals(data.length, amount);
        assertTrue(areArraysEqual(data, 0, data.length, buf, 0, amount));
        assertEquals(-1, input.read(buf, 0, buf.length));

        // ////////////////////////////////////////////
        // Test with equal length.
        // ////////////////////////////////////////////
        data = "my data".getBytes();
        input = new MaxLengthInputStream(new ByteArrayInputStream(data), data.length);
        amount = input.read(buf, 0, buf.length);

        assertEquals(data.length, amount);
        assertTrue(areArraysEqual(data, 0, data.length, buf, 0, amount));
        assertEquals(-1, input.read(buf, 0, buf.length));

        // ////////////////////////////////////////////
        // Test with length one less.
        // ////////////////////////////////////////////
        data = "my data".getBytes();
        input = new MaxLengthInputStream(new ByteArrayInputStream(data), data.length - 1);
        amount = input.read(buf, 0, buf.length);

        assertEquals(data.length - 1, amount);
        assertTrue(areArraysEqual(data, 0, data.length - 1, buf, 0, amount));
        assertEquals(-1, input.read(buf, 0, buf.length));
    }

    /**
     * Test method for {@link com.cordys.coe.util.io.MaxLengthInputStream#read(char[], int, int)}.
     */
    public void testReadCharArrayIntInt_smallBuffer() throws Exception
    {
        byte[] data;
        byte[] buf = new byte[5];
        ByteArrayOutputStream sb;
        int amount;
        MaxLengthInputStream input;

        // ////////////////////////////////////////////
        // Test with length well past the end.
        // ////////////////////////////////////////////
        data = "my data".getBytes();
        sb = new ByteArrayOutputStream();
        input = new MaxLengthInputStream(new ByteArrayInputStream(data), data.length + 10);
        amount = input.read(buf, 0, buf.length);
        assertEquals(amount, buf.length);
        sb.write(buf);
        amount = input.read(buf, 0, buf.length);
        assertEquals(data.length, amount + buf.length);
        sb.write(buf, 0, amount);
        assertTrue(areArraysEqual(data, 0, data.length, sb));
        assertEquals(-1, input.read(buf, 0, buf.length));

        // ////////////////////////////////////////////
        // Test with length one past the end.
        // ////////////////////////////////////////////
        data = "my data".getBytes();
        sb = new ByteArrayOutputStream();
        input = new MaxLengthInputStream(new ByteArrayInputStream(data), data.length + 1);
        amount = input.read(buf, 0, buf.length);
        assertEquals(buf.length, amount);
        sb.write(buf);
        amount = input.read(buf, 0, buf.length);
        assertEquals(data.length, amount + buf.length);
        sb.write(buf, 0, amount);
        assertTrue(areArraysEqual(data, 0, data.length, sb));
        assertEquals(-1, input.read(buf, 0, buf.length));

        // ////////////////////////////////////////////
        // Test with equal length.
        // ////////////////////////////////////////////
        data = "my data".getBytes();
        sb = new ByteArrayOutputStream();
        input = new MaxLengthInputStream(new ByteArrayInputStream(data), data.length);
        amount = input.read(buf, 0, buf.length);
        assertEquals(buf.length, amount);
        sb.write(buf);
        amount = input.read(buf, 0, buf.length);
        assertEquals(data.length, amount + buf.length);
        sb.write(buf, 0, amount);
        assertTrue(areArraysEqual(data, 0, data.length, sb));
        assertEquals(-1, input.read(buf, 0, buf.length));

        // ////////////////////////////////////////////
        // Test with length one less.
        // ////////////////////////////////////////////
        data = "my data".getBytes();
        sb = new ByteArrayOutputStream();
        input = new MaxLengthInputStream(new ByteArrayInputStream(data), data.length - 1);
        amount = input.read(buf, 0, buf.length);
        assertEquals(buf.length, amount);
        sb.write(buf);
        amount = input.read(buf, 0, buf.length);
        assertEquals(data.length - 1, amount + buf.length);
        sb.write(buf, 0, amount);
        assertTrue(areArraysEqual(data, 0, data.length - 1, sb));
        assertEquals(-1, input.read(buf, 0, buf.length));
    }

    /**
     * Test method for {@link com.cordys.coe.util.io.MaxLengthInputStream#skipUntilEnd()}.
     */
    public void testSkipUntilEnd() throws Exception
    {
        byte[] data;
        ByteArrayInputStream srcInput;
        MaxLengthInputStream testInput;
        long amount;

        // ////////////////////////////////////////////
        // Test with skipping
        // ////////////////////////////////////////////
        data = "my data".getBytes();
        srcInput = new ByteArrayInputStream(data);
        testInput = new MaxLengthInputStream(srcInput, data.length - 1);
        amount = testInput.skipUntilEnd();
        assertEquals(data.length - 1, amount);
        assertEquals(-1, testInput.read());
        FileUtils.closeStream(testInput);
    }

    /**
     * Test method for {@link com.cordys.coe.util.io.MaxLengthInputStream#detachSource(boolean)}.
     */
    public void testDetachSource() throws Exception
    {
        byte[] data;
        byte[] buf = new byte[10];
        ByteArrayInputStream srcInput;
        MaxLengthInputStream testInput;
        int ch;
        int amount;

        // ////////////////////////////////////////////
        // Test with no skipping
        // ////////////////////////////////////////////
        data = "my data".getBytes();
        srcInput = new ByteArrayInputStream(data);
        testInput = new MaxLengthInputStream(srcInput, data.length - 1);
        ch = testInput.read();

        assertEquals(data[0], ch);
        testInput.detachSource(false);
        amount = srcInput.read(buf);
        assertTrue(areArraysEqual(data, 1, data.length - 1, buf, 0, amount));
        FileUtils.closeStream(testInput);

        // ////////////////////////////////////////////
        // Test with skipping
        // ////////////////////////////////////////////
        data = "my data".getBytes();
        srcInput = new ByteArrayInputStream(data);
        testInput = new MaxLengthInputStream(srcInput, data.length - 1);
        ch = testInput.read();

        assertEquals(data[0], ch);
        testInput.detachSource(true);
        amount = srcInput.read(buf);
        assertTrue(areArraysEqual(data, data.length - 1, 1, buf, 0, amount));
        FileUtils.closeStream(testInput);
    }

    private static boolean areArraysEqual(byte[] a1, int offset1, int length1, byte[] a2, int offset2, int length2)
    {
        if (a1 == null || a2 == null)
        {
            return false;
        }

        if (offset1 + length1 > a1.length)
        {
            return false;
        }

        if (offset2 + length2 > a2.length)
        {
            return false;
        }

        if (length1 != length2)
        {
            return false;
        }

        for (int i = 0; i < length1; i++)
        {
            if (a1[i + offset1] != a2[i + offset2])
            {
                return false;
            }
        }

        return true;
    }

    private static boolean areArraysEqual(byte[] a1, int offset1, int length1, ByteArrayOutputStream b2)
    {
        byte[] b2array = b2.toByteArray();

        return areArraysEqual(a1, offset1, length1, b2array, 0, b2array.length);
    }
}
