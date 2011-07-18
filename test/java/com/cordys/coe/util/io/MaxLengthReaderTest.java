/**
 * (c) 2007 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util.io;

import java.io.Reader;
import java.io.StringReader;

import junit.framework.TestCase;

/**
 * Test case for class MaxLengthReader.
 *
 * @author mpoyhone
 */
public class MaxLengthReaderTest extends TestCase
{

    /**
     * Test method for {@link com.cordys.coe.util.io.MaxLengthReader#read()}.
     */
    public void testRead() throws Exception
    {
        String s;
        MaxLengthReader reader;
        
        //////////////////////////////////////////////
        // Test with length well past the end.
        //////////////////////////////////////////////
        s = "my data";
        reader = new MaxLengthReader(new StringReader(s), s.length() + 10);
        
        for (char c : s.toCharArray())
        {
            int ch = reader.read();
            
            assertEquals(c, ch);
        }
        
        //////////////////////////////////////////////
        // Test with length one past the end.
        //////////////////////////////////////////////
        s = "my data";
        reader = new MaxLengthReader(new StringReader(s), s.length() + 1);
        
        for (char c : s.toCharArray())
        {
            int ch = reader.read();
            
            assertEquals(c, ch);
        }       
        
        //////////////////////////////////////////////
        // Test with equal length.
        //////////////////////////////////////////////
        s = "my data";
        reader = new MaxLengthReader(new StringReader(s), s.length());
        
        for (char c : s.toCharArray())
        {
            int ch = reader.read();
            
            assertEquals(c, ch);
        }        
        
        //////////////////////////////////////////////
        // Test with length one less.
        //////////////////////////////////////////////
        s = "my data";
        reader = new MaxLengthReader(new StringReader(s), s.length() - 1);
        
        for (char c : s.substring(0, s.length() - 1).toCharArray())
        {
            int ch = reader.read();
            
            assertEquals(c, ch);
        }       
    }

    /**
     * Test method for {@link com.cordys.coe.util.io.MaxLengthReader#read(char[], int, int)}.
     */
    public void testReadCharArrayIntInt() throws Exception
    {
        String s;
        char[] buf = new char[1000];
        int amount;
        MaxLengthReader reader;
        
        //////////////////////////////////////////////
        // Test with length well past the end.
        //////////////////////////////////////////////
        s = "my data";
        reader = new MaxLengthReader(new StringReader(s), s.length() + 10);
        amount = reader.read(buf, 0, buf.length);
        
        assertEquals(s.length(), amount);
        assertEquals(s, new String(buf, 0, amount));
        assertEquals(-1, reader.read(buf, 0, buf.length));
        
        //////////////////////////////////////////////
        // Test with length one past the end.
        //////////////////////////////////////////////
        s = "my data";
        reader = new MaxLengthReader(new StringReader(s), s.length() + 1);
        amount = reader.read(buf, 0, buf.length);
        
        assertEquals(s.length(), amount);
        assertEquals(s, new String(buf, 0, amount));        
        assertEquals(-1, reader.read(buf, 0, buf.length));
        
        //////////////////////////////////////////////
        // Test with equal length.
        //////////////////////////////////////////////
        s = "my data";
        reader = new MaxLengthReader(new StringReader(s), s.length());
        amount = reader.read(buf, 0, buf.length);
        
        assertEquals(s.length(), amount);
        assertEquals(s, new String(buf, 0, amount));    
        assertEquals(-1, reader.read(buf, 0, buf.length));
        
        //////////////////////////////////////////////
        // Test with length one less.
        //////////////////////////////////////////////
        s = "my data";
        reader = new MaxLengthReader(new StringReader(s), s.length() - 1);
        amount = reader.read(buf, 0, buf.length);
        
        assertEquals(s.length() - 1, amount);
        assertEquals(s.substring(0, s.length() - 1), new String(buf, 0, amount));        
        assertEquals(-1, reader.read(buf, 0, buf.length));
    }

    /**
     * Test method for {@link com.cordys.coe.util.io.MaxLengthReader#read(char[], int, int)}.
     */
    public void testReadCharArrayIntInt_smallBuffer() throws Exception
    {
        String s;
        char[] buf = new char[5];
        StringBuilder sb;
        int amount;
        MaxLengthReader reader;
        
        //////////////////////////////////////////////
        // Test with length well past the end.
        //////////////////////////////////////////////
        s = "my data";
        sb = new StringBuilder();
        reader = new MaxLengthReader(new StringReader(s), s.length() + 10);
        amount = reader.read(buf, 0, buf.length);
        assertEquals(amount, buf.length);
        sb.append(buf);
        amount = reader.read(buf, 0, buf.length);
        assertEquals(amount + sb.length(), s.length());
        sb.append(buf, 0, amount);
        assertEquals(s, sb.toString());
        assertEquals(-1, reader.read(buf, 0, buf.length));
        
        //////////////////////////////////////////////
        // Test with length one past the end.
        //////////////////////////////////////////////
        s = "my data";
        sb = new StringBuilder();
        reader = new MaxLengthReader(new StringReader(s), s.length() + 1);
        amount = reader.read(buf, 0, buf.length);
        assertEquals(buf.length, amount);
        sb.append(buf);
        amount = reader.read(buf, 0, buf.length);
        assertEquals(s.length(), amount + sb.length());
        sb.append(buf, 0, amount);
        assertEquals(s, sb.toString());       
        assertEquals(-1, reader.read(buf, 0, buf.length));
        
        //////////////////////////////////////////////
        // Test with equal length.
        //////////////////////////////////////////////
        s = "my data";
        sb = new StringBuilder();
        reader = new MaxLengthReader(new StringReader(s), s.length());
        amount = reader.read(buf, 0, buf.length);
        assertEquals(buf.length, amount);
        sb.append(buf);
        amount = reader.read(buf, 0, buf.length);
        assertEquals(s.length(), amount + sb.length());
        sb.append(buf, 0, amount);
        assertEquals(s, sb.toString());  
        assertEquals(-1, reader.read(buf, 0, buf.length));
        
        //////////////////////////////////////////////
        // Test with length one less.
        //////////////////////////////////////////////
        s = "my data";
        sb = new StringBuilder();
        reader = new MaxLengthReader(new StringReader(s), s.length() - 1);
        amount = reader.read(buf, 0, buf.length);
        assertEquals(buf.length, amount);
        sb.append(buf);
        amount = reader.read(buf, 0, buf.length);
        assertEquals(s.length() - 1, amount + sb.length());
        sb.append(buf, 0, amount);
        assertEquals(s.substring(0, s.length() - 1), sb.toString());
        assertEquals(-1, reader.read(buf, 0, buf.length));
    }

    /**
     * Test method for {@link com.cordys.coe.util.io.MaxLengthReader#skipUntilEnd()}.
     */
    public void testSkipUntilEnd() throws Exception
    {
        String s;
        Reader srcReader;
        MaxLengthReader testReader;
        long amount;
        
        //////////////////////////////////////////////
        // Test with skipping
        //////////////////////////////////////////////
        s = "my data";
        srcReader = new StringReader(s);
        testReader = new MaxLengthReader(srcReader, s.length() - 1);
        amount = testReader.skipUntilEnd();
        assertEquals(s.length() - 1, amount);
        assertEquals(-1, testReader.read());
    }

    /**
     * Test method for {@link com.cordys.coe.util.io.MaxLengthReader#detachSource(boolean)}.
     */
    public void testDetachSource() throws Exception
    {
        String s;
        char[] buf = new char[10];
        Reader srcReader;
        MaxLengthReader testReader;
        int ch;
        int amount;
        
        //////////////////////////////////////////////
        // Test with no skipping
        //////////////////////////////////////////////
        s = "my data";
        srcReader = new StringReader(s);
        testReader = new MaxLengthReader(srcReader, s.length() - 1);
        ch = testReader.read();
        
        assertEquals(s.charAt(0), ch);
        testReader.detachSource(false);
        amount = srcReader.read(buf);
        assertEquals(s.substring(1, s.length()), new String(buf, 0, amount));
        
        //////////////////////////////////////////////
        // Test with skipping
        //////////////////////////////////////////////
        s = "my data";
        srcReader = new StringReader(s);
        testReader = new MaxLengthReader(srcReader, s.length() - 1);
        ch = testReader.read();
        
        assertEquals(s.charAt(0), ch);
        testReader.detachSource(true);
        amount = srcReader.read(buf);
        assertEquals(s.substring(s.length() - 1), new String(buf, 0, amount));        
    }
}
