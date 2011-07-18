package com.cordys.coe.utils.xsd;

import com.cordys.coe.util.FileUtils;
import com.cordys.coe.util.xml.Message;
import com.cordys.coe.util.xml.MessageContext;

import com.eibus.xml.nom.Document;

import java.io.File;
import java.io.FileOutputStream;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * A simple application that reads a bunch of XSD's and creates data for one XSD for a method WSDL.
 *
 * @author  jvkooten (adapted from XsdInliner by Mikko)
 */
public class XsdIncluder
{
    /**
     * DOCUMENTME.
     */
    MessageContext mcContext = new MessageContext(new Document());
    /**
     * DOCUMENTME.
     */
    Map<String, Message> mElementMap = new HashMap<String, Message>();
    /**
     * DOCUMENTME.
     */
    Map<String, Message> mTypeMap = new HashMap<String, Message>();
    /**
     * DOCUMENTME.
     */
    Vector<String> xsdOrdering = new Vector<String>();
    /**
     * DOCUMENTME.
     */
    Map<String, Message> xsds = new HashMap<String, Message>();

    /**
     * DOCUMENTME.
     *
     * @param  args  DOCUMENTME
     */
    public static void main(String[] args)
    {
        if (args.length < 3)
        {
            printUsage();
            return;
        }

        File fSchemaDir = new File(args[0]);

        if (!fSchemaDir.exists())
        {
            System.err.println("Directory " + fSchemaDir + " does not exist.");
            return;
        }

        File[] faFiles = fSchemaDir.listFiles();
        XsdIncluder xsdIncluder = new XsdIncluder();

        for (int i = 0; i < faFiles.length; i++)
        {
            File fFile = faFiles[i];

            if (!fFile.getName().endsWith(".xsd"))
            {
                continue;
            }

            try
            {
                xsdIncluder.readSchema(fFile);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return;
            }
        }
        xsdIncluder.printTypes();

        xsdIncluder.processXSDFile(args[1], args[2]);
    }

    /**
     * DOCUMENTME.
     *
     * @param   mNode  DOCUMENTME
     *
     * @throws  Exception  DOCUMENTME
     */
    public void analyseTypes(Message mNode)
                      throws Exception
    {
        String sType = mNode.getValue("./@type", null);

        if (sType != null)
        {
            if (!xsds.containsKey(sType))
            {
                Message mTypeNode = findType(sType);

                if (mTypeNode != null)
                {
                    xsds.put(sType, mTypeNode);
                    analyseTypes(mTypeNode);
                    xsdOrdering.addElement(sType);
                }
            }
        }

        String sRef = mNode.getValue("./@ref", null);

        if (sRef != null)
        {
            if (!xsds.containsKey(sRef))
            {
                Message mRefNode = findElement(sRef);

                if (mRefNode != null)
                {
                    xsds.put(sRef, mRefNode);
                    analyseTypes(mRefNode);
                    xsdOrdering.addElement(sRef);
                }
            }
        }

        String sBase = mNode.getValue("./@base", null);

        if (sBase != null)
        {
            if (!xsds.containsKey(sBase))
            {
                Message mBaseTypeNode = findType(sBase);

                if (mBaseTypeNode != null)
                {
                    xsds.put(sBase, mBaseTypeNode);
                    analyseTypes(mBaseTypeNode);
                    xsdOrdering.addElement(sBase);
                }
            }
        }

        for (Iterator<Object> iIter = mNode.selectAll("./."); iIter.hasNext();)
        {
            Message mChildNode = (Message) iIter.next();
            analyseTypes(mChildNode);
        }
    }

    /**
     * DOCUMENTME.
     *
     * @param  mNode  DOCUMENTME
     * @param  sbRes  DOCUMENTME
     */
    public void emitNodeEnd(Message mNode, StringBuffer sbRes)
    {
        sbRes.append("</").append(mNode.getName(true)).append(">\n");
    }

    /**
     * DOCUMENTME.
     *
     * @param   sType  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    public Message findElement(String sType)
    {
        return mElementMap.get(sType);
    }

    /**
     * DOCUMENTME.
     *
     * @param   sType  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    public Message findType(String sType)
    {
        return mTypeMap.get(sType);
    }

    /**
     * DOCUMENTME.
     */
    public void printElements()
    {
        Set<String> set = mElementMap.keySet();
        Iterator<String> iterator = set.iterator();
        int i = 0;

        while (iterator.hasNext())
        {
            i++;

            String key = iterator.next();
            System.out.println(i + " " + key);
        }
    }

    /**
     * DOCUMENTME.
     */
    public void printTypes()
    {
        Set<String> set = mTypeMap.keySet();
        Iterator<String> iterator = set.iterator();
        int i = 0;

        while (iterator.hasNext())
        {
            i++;

            String key = iterator.next();
            System.out.println(i + " " + key);
        }
    }

    /**
     * DOCUMENTME.
     *
     * @param  a_inputFileName   DOCUMENTME
     * @param  a_outputFileName  DOCUMENTME
     */
    public void processXSDFile(String a_inputFileName, String a_outputFileName)
    {
        File rootFile = new File(a_inputFileName);

        if ((rootFile == null) || !rootFile.exists())
        {
            System.err.println("File " + a_inputFileName + " does not exist.");
            return;
        }

        try
        {
            Message mFileNode = mcContext.createMessage(rootFile);
            Iterator<Object> childNodes = mFileNode.childIterator();

            for (int i = 0; childNodes.hasNext(); i++)
            {
                Message childNode = (Message) childNodes.next();
                String childNameAttrib = childNode.getValue("./@name", null);
                String childName = childNode.getName();

                if (!childName.equals("include") && (childNameAttrib != null))
                {
                    this.analyseTypes(childNode);

                    if (!xsds.containsKey(childNameAttrib))
                    {
                        xsds.put(childNameAttrib, childNode);
                        xsdOrdering.addElement(childNameAttrib);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }

        File fOutputFile = new File(a_outputFileName);
        FileOutputStream fisOutput = null;

        try
        {
            fisOutput = new FileOutputStream(fOutputFile);

            for (int i = 0; i < xsdOrdering.size(); i++)
            {
                String key = xsdOrdering.elementAt(i);
                Message xsd = xsds.get(key);
                fisOutput.write(xsd.toString().getBytes("UTF-8"));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }
        finally
        {
            FileUtils.closeStream(fisOutput);
        }
    }

    /**
     * DOCUMENTME.
     *
     * @param   fFile  DOCUMENTME
     *
     * @throws  Exception  DOCUMENTME
     */
    public void readSchema(File fFile)
                    throws Exception
    {
        Message mFileNode = mcContext.createMessage(fFile);

        for (Iterator<Object> iIter = mFileNode.selectAll("./."); iIter.hasNext();)
        {
            Message mNode = (Message) iIter.next();
            String sNodeName = mNode.getName();
            String sNameAttrib = mNode.getValue("./@name", null);

            if (sNameAttrib == null)
            {
                continue;
            }

            if (sNodeName.equals("element"))
            {
                mElementMap.put(sNameAttrib, mNode);
            }
            else
            {
                mTypeMap.put(sNameAttrib, mNode);
            }
        }
    }

    /**
     * DOCUMENTME.
     */
    protected static void printUsage()
    {
        System.out.println("Usage: <schema directory> <input.xsd> <output file name>");
    }
}
