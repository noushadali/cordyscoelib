package com.cordys.coe.util.ssm;

import com.cordys.coe.exception.GeneralException;
import com.cordys.coe.util.XMLProperties;
import com.cordys.coe.util.xml.nom.XPathHelper;

import com.eibus.xml.nom.Node;
import com.eibus.xml.xpath.XPathMetaInfo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class processes the response of the List request. It will write a file for each SOAP
 * processor with the details.
 *
 * <pre>
 <root>
    <execute>
        <timeout>30000</timeout>
        <usercontext>cn=root,cn=organizational users,o=system,cn=cordys,o=cordyslab.com</usercontext>
        <orgcontext>o=system,cn=cordys,o=cordyslab.com</orgcontext>
        <method>List</method>
        <ns>http://schemas.cordys.com/1.0/monitor</ns>
        <outputhandler>
            <class>com.cordys.coe.util.ssm.MonitorList</class>
            <parameters>
                <filename>CN|PID|FULL</filename>
                <separator>,</separator>
                <prefixes>
                    <prefix name="ns" namespace="http://schemas.cordys.com/1.0/monitor"/>
                </prefixes>
                <fields>
                    <field name="date" caption="Date" format="yyyyMMddHHmmssSSS"/>
                    <field name="ns:process-id" caption="PID"/>
                    <field name="ns:status" caption="Status"/>
                    <field name="ns:totalNOMMemory" caption="TotalNOMMemory"/>
                    <field name="ns:totalNOMNodesMemory" caption="TotalNOMNodesMemory"/>
                    <field name="ns:totalCpuTime" caption="TotalCpuTime"/>
                    <field name="ns:virtualMemoryUsage" caption="VirtualMemoryUsage"/>
                    <field name="ns:busdocs" caption="requests"/>
                    <field name="ns:totalNOMNodesMemory" caption="TotalNOMNodesMemory"/>
                    <field name="ns:processing-time" caption="Processing Time"/>
                </fields>
            </parameters>
        </outputhandler>
    </execute>
</root>
 * </pre>
 *
 * @author  pgussow
 */
public class MonitorList
    implements ISendSoapMessageOutputHandler
{
    /**
     * Holds all fields.
     */
    private ArrayList<Field> m_alFields = new ArrayList<Field>();
    /**
     * Holds the name to use for the filename.
     */
    private EFilename m_fFilenameType;
    /**
     * Holds the output folder.
     */
    private File m_fOutput;
    /**
     * Holds the base XPath to execute.
     */
    private String m_sBaseXPath;
    /**
     * Holds the separator character.
     */
    private String m_sSeparator;
    /**
     * Holds the XPath namespace mappings.
     */
    private XPathMetaInfo m_xmi;

    /**
     * This method is called with the parameter XML to configure the output handler.
     *
     * @param   iConfig  The configuration XML.
     * @param   fOutput  The output folder.
     *
     * @throws  GeneralException  In case of any exceptions.
     *
     * @see     com.cordys.coe.util.ssm.ISendSoapMessageOutputHandler#configure(int, java.io.File)
     */
    public void configure(int iConfig, File fOutput)
                   throws GeneralException
    {
        m_fOutput = fOutput;

        XMLProperties xp = new XMLProperties(iConfig);
        m_fFilenameType = EFilename.valueOf(xp.getStringValue("filename"));
        m_sSeparator = xp.getStringValue("separator");
        m_sBaseXPath = xp.getStringValue("basexpath");

        m_xmi = new XPathMetaInfo();

        int[] aiPrefixes = XPathHelper.selectNodes(iConfig, "./prefixes/prefix");

        for (int iPrefix : aiPrefixes)
        {
            m_xmi.addNamespaceBinding(Node.getAttribute(iPrefix, "name"),
                                      Node.getAttribute(iPrefix, "namespace"));
        }

        int[] aiFields = XPathHelper.selectNodes(iConfig, "./fields/field");

        for (int iField : aiFields)
        {
            Field fField = new Field(iField);
            m_alFields.add(fField);
        }
    }

    /**
     * This method is called to process the actual response.
     *
     * @param   iResponse  the actual response that was received.
     *
     * @return  true if the handler has processed the response. When this method returns false the
     *          default handler is used.
     *
     * @throws  GeneralException  In case of any exceptions
     *
     * @see     com.cordys.coe.util.ssm.ISendSoapMessageOutputHandler#processMethodResponse(int)
     */
    public boolean processMethodResponse(int iResponse)
                                  throws GeneralException
    {
        boolean bReturn = true;

        int[] aiWorkerProcesses = XPathHelper.selectNodes(iResponse, m_sBaseXPath, m_xmi);

        if ((aiWorkerProcesses != null) && (aiWorkerProcesses.length > 0))
        {
            for (int iWorkerProcess : aiWorkerProcesses)
            {
                String sName = "";

                switch (m_fFilenameType)
                {
                    case CN:
                        sName = XPathHelper.getStringValue(iWorkerProcess, "ns:name/text()", m_xmi);

                        Matcher mTemp = Pattern.compile("cn=([^,]+)").matcher(sName);
                        if (mTemp.find())
                        {
                            sName = mTemp.group(1);
                            sName = sName.replaceAll("[^a-zA-Z0-9]+", "_");
                        }
                        break;

                    case FULL:
                        sName = XPathHelper.getStringValue(iWorkerProcess, "ns:name/text()", m_xmi);
                        sName = sName.replaceAll("[^a-zA-Z0-9]+", "_");
                        break;

                    case PID:
                        sName = XPathHelper.getStringValue(iWorkerProcess, "ns:process-id/text()",
                                                           m_xmi);
                        break;
                }

                // Get the proper file
                File fFile = new File(m_fOutput, sName + ".log");
                FileWriter fos = null;

                try
                {
                    if (!fFile.exists())
                    {
                        fos = new FileWriter(fFile);

                        for (int iCount = 0; iCount < m_alFields.size(); iCount++)
                        {
                            Field f = m_alFields.get(iCount);
                            fos.write(f.getCaption());

                            if (iCount < (m_alFields.size() - 1))
                            {
                                fos.write(m_sSeparator);
                            }
                        }

                        fos.write("\n");
                    }
                    else
                    {
                        fos = new FileWriter(fFile, true);
                    }

                    // Now write all values.
                    for (int iCount = 0; iCount < m_alFields.size(); iCount++)
                    {
                        Field f = m_alFields.get(iCount);
                        String sValue = f.getValue(iWorkerProcess);
                        fos.write(sValue);

                        if (iCount < (m_alFields.size() - 1))
                        {
                            fos.write(m_sSeparator);
                        }
                    }

                    fos.write("\n");
                }
                catch (IOException e)
                {
                    throw new GeneralException(e, "Error writing file.");
                }
                finally
                {
                    if (fos != null)
                    {
                        try
                        {
                            System.out.println("Finished writing file " + fFile.getAbsolutePath());
                            fos.close();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return bReturn;
    }

    /**
     * Enum for the filename.
     *
     * @author  pgussow
     */
    private enum EFilename
    {
        CN,
        PID,
        FULL
    }

    /**
     * Enum for field types.
     *
     * @author  pgussow
     */
    private enum EFieldType
    {
        DATE,
        XPATH
    }

    /**
     * Wrapper around the field.
     *
     * @author  pgussow
     */
    public class Field
    {
        /**
         * Holds the field type..
         */
        private EFieldType m_ftType;
        /**
         * Holds the caption.
         */
        private String m_sCaption;
        /**
         * Holds the SimpleDateFormat.
         */
        private SimpleDateFormat m_sdf;
        /**
         * Holds the XPath.
         */
        private String m_sXPath;

        /**
         * Creates a new Field object.
         *
         * @param  iFieldNode  The node for the field.
         */
        public Field(int iFieldNode)
        {
            m_sCaption = Node.getAttribute(iFieldNode, "caption");
            m_ftType = EFieldType.valueOf(Node.getAttribute(iFieldNode, "type").toUpperCase());

            String sFormat;

            switch (m_ftType)
            {
                case DATE:
                    sFormat = Node.getAttribute(iFieldNode, "format");
                    m_sdf = new SimpleDateFormat(sFormat);
                    break;

                case XPATH:
                    m_sXPath = Node.getAttribute(iFieldNode, "xpath");
                    break;
            }
        }

        /**
         * This method gets the caption.
         *
         * @return  The caption.
         */
        public String getCaption()
        {
            return m_sCaption;
        }

        /**
         * This method gets the value for the current field.
         *
         * @param   iContext  The context.
         *
         * @return  The value for the current field.
         */
        public String getValue(int iContext)
        {
            String sReturn = "";

            switch (m_ftType)
            {
                case DATE:
                    sReturn = m_sdf.format(new Date());
                    break;

                case XPATH:
                    sReturn = XPathHelper.getStringValue(iContext, m_sXPath, m_xmi, "");
                    break;
            }
            return sReturn;
        }
    }
}
