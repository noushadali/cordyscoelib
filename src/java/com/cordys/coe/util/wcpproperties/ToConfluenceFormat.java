package com.cordys.coe.util.wcpproperties;

import com.cordys.coe.tools.wcpproperties.DefinitionFile;
import com.cordys.coe.tools.wcpproperties.WcpProperty;
import com.cordys.coe.tools.wcpproperties.WcpPropertyWhereUsed;
import com.cordys.coe.util.exceptions.XMLWrapperException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.transform.TransformerException;

/**
 * This class converts the latest version of the wcpproeprties.xml to the wiki table format.
 *
 * @author  pgussow
 */
public class ToConfluenceFormat
{
    /**
     * Holds the properties that need to be written to Confluence format.
     */
    private ArrayList<WcpProperty> m_alProperties;
    /**
     * Whether or not to include the where used part.
     */
    private boolean m_bIncludeWhereUsed = false;
    /**
     * Holds the definition file.
     */
    private DefinitionFile m_dfMetaFile;
    /**
     * The output stream to which the Wiki format will be written.
     */
    private PrintStream m_psOut;
    /**
     * Holds the current Cordys version.
     */
    private String m_sCurrentVersion;

    /**
     * Creates a new ToConfluenceFormat object.
     *
     * @throws  XMLWrapperException   DOCUMENTME
     * @throws  TransformerException  DOCUMENTME
     */
    public ToConfluenceFormat()
                       throws XMLWrapperException, TransformerException
    {
        m_dfMetaFile = new DefinitionFile();

        String[] asVersions = m_dfMetaFile.getCordysVersions();

        if ((asVersions != null) && (asVersions.length > 0))
        {
            m_sCurrentVersion = asVersions[asVersions.length - 1];
            m_alProperties = m_dfMetaFile.getProperties(m_sCurrentVersion);
        }

        m_psOut = System.out;
    }

    /**
     * Main method.
     *
     * @param  saArguments  The commandline arguments.
     */
    public static void main(String[] saArguments)
    {
        try
        {
            ToConfluenceFormat tcf = new ToConfluenceFormat();
            tcf.setOutputFile(new File("c:/temp/wiki_new.txt"));
            tcf.execute();
            tcf.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("Done.");
    }

    /**
     * This method closes the output stream unless it's stdout.
     */
    public void close()
    {
        if (m_psOut != System.out)
        {
            m_psOut.close();
        }
    }

    /**
     * This method writes the confluence format to std out.
     */
    public void execute()
    {
        // Write the header
        m_psOut.println("h3. wcp.properties");
        m_psOut.println();
        m_psOut.println("This page contains all the properties that can influence the Cordys installation via the wcp.properties file in the {{cordys_install_dir}}");
        m_psOut.println();

        if (m_bIncludeWhereUsed)
        {
            m_psOut.println("||Name||Mandatory||Default Value||Description||Where used||");
        }
        else
        {
            m_psOut.println("||Name||Description||");
        }

        for (Iterator<WcpProperty> iProps = m_alProperties.iterator(); iProps.hasNext();)
        {
            WcpProperty wp = (WcpProperty) iProps.next();

            m_psOut.print("|{anchor:");
            m_psOut.print(getAnchorName("Main", wp.getName()));
            m_psOut.print("} [*");
            m_psOut.print(wp.getName());
            m_psOut.print("*|#");
            m_psOut.print(getAnchorName("", wp.getName()));
            m_psOut.print("]|");

            if (m_bIncludeWhereUsed)
            {
                if (wp.isMandatory())
                {
                    m_psOut.print("yes");
                }
                else
                {
                    m_psOut.print("no");
                }
                m_psOut.print("|");

                String sDefault = wp.getDefaultValue();

                if ((sDefault == null) || (sDefault.length() == 0))
                {
                    sDefault = " ";
                }
                m_psOut.print(sDefault);
                m_psOut.print("|");
            }

            m_psOut.print(toWikiFormat(wp.getDescription().trim()));

            if (m_bIncludeWhereUsed)
            {
                m_psOut.print("|");

                ArrayList<WcpPropertyWhereUsed> alTemp = wp.getWhereUsed();
                boolean bFirst = true;

                for (WcpPropertyWhereUsed wpwu : alTemp)
                {
                    if (bFirst)
                    {
                        bFirst = false;
                    }
                    else
                    {
                        m_psOut.println("\\\\");
                    }
                    m_psOut.print(wpwu.getClassName() + "(" + wpwu.getComponent() + ")");
                }
            }
            m_psOut.println("|");
        }
        m_psOut.println();
        m_psOut.println("h2. Details section");
        m_psOut.println();
        m_psOut.println("This section contains the details and more extensive description of the properties.");
        m_psOut.println();

        // Now we have the general list. Now print the details for each property.
        for (Iterator<WcpProperty> iProps = m_alProperties.iterator(); iProps.hasNext();)
        {
            WcpProperty wp = (WcpProperty) iProps.next();
            m_psOut.print("{anchor:");
            m_psOut.print(getAnchorName("", wp.getName()));
            m_psOut.println("}");
            m_psOut.print("h2. ");
            m_psOut.print(wp.getName());
            m_psOut.print(" (");
            m_psOut.print(wp.getComponent());
            m_psOut.println(")");

            m_psOut.print("{section}{column:width=100px}*Name:*{column}{column}");
            m_psOut.print(wp.getName());
            m_psOut.println("{column}{section}");

            m_psOut.print("{section}{column:width=100px}*Default value:*{column}{column}");
            m_psOut.print(wp.getDefaultValue());
            m_psOut.println("{column}{section}");

            m_psOut.print("{section}{column:width=100px}*Mandatory:*{column}{column}");
            m_psOut.print(wp.isMandatory() ? "yes" : "no");
            m_psOut.println("{column}{section}");

            m_psOut.print("{section}{column:width=100px}*Caption:*{column}{column}");
            m_psOut.print(wp.getCaption());
            m_psOut.println("{column}{section}");

            m_psOut.print("{section}{column:width=100px}*Description:*{column}{column}");
            m_psOut.print(toWikiFormat(wp.getDescription()));
            m_psOut.println("{column}{section}");

            ArrayList<WcpPropertyWhereUsed> alTemp = wp.getWhereUsed();

            if (alTemp.size() > 0)
            {
                m_psOut.println("{section}{column:width=100px}*Where used:*{column}{column}");
                m_psOut.println("||*Component*||*Class*||");

                for (WcpPropertyWhereUsed wpwu : alTemp)
                {
                    m_psOut.print("|");
                    m_psOut.print(wpwu.getComponent());
                    m_psOut.print("|");
                    m_psOut.print(wpwu.getClassName());
                    m_psOut.println("|");
                }

                m_psOut.println("{column}{section}");
            }

            // Return to top.
            m_psOut.print("[Return to main list|#");
            m_psOut.print(getAnchorName("Main", wp.getName()));
            m_psOut.println("]");
        }
    }

    /**
     * This method sets the output stream to a file.
     *
     * @param   fOutput  The output file.
     *
     * @throws  FileNotFoundException  DOCUMENTME
     */
    public void setOutputFile(File fOutput)
                       throws FileNotFoundException
    {
        if (fOutput.exists())
        {
            fOutput.delete();
        }

        FileOutputStream fos = new FileOutputStream(fOutput, false);
        m_psOut = new PrintStream(fos);
    }

    /**
     * This method builds up the Wiki anchorname based on the property name.
     *
     * @param   sPrefix        The prefix for the anchor.
     * @param   sPropertyName  The name of the property.
     *
     * @return  The name to use for the anchor.
     */
    private String getAnchorName(String sPrefix, String sPropertyName)
    {
        String sReturn = sPrefix;

        sReturn += sPropertyName;

        return sReturn;
    }

    /**
     * This method escapes the passed on string to match the Wiki format. It does the following:<br>
     * - Replace all \r\n to a \\ - Replace all | to a
     *
     * @param   sSource  The source string.
     *
     * @return  The escaped Wiki format string.
     */
    private String toWikiFormat(String sSource)
    {
        sSource = sSource.replaceAll("\r", "");
        sSource = sSource.replaceAll("\n\\s*", "\\\\\\\\");
        sSource = sSource.replaceAll("[\\|]", "\\\\|");

        return sSource;
    }
}
