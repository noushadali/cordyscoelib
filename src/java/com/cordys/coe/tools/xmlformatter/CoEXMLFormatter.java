package com.cordys.coe.tools.xmlformatter;

import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;

import com.cordys.coe.util.general.Util;
import com.cordys.coe.util.swing.MessageBoxUtil;
import com.cordys.coe.util.xml.dom.XMLHelper;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * This class can be used to format an XML message.
 *
 * @author  pgussow
 */
public class CoEXMLFormatter extends JFrame
{
    /**
     * Holds whether or not the XML should be formatted.
     */
    private JComboBox<String> m_cFormat;
    /**
     * Holds whether or not the whitespace should be preserved.
     */
    private JComboBox<String> m_cPreserveWhitespace;
    /**
     * Holds whether or not the XML declaration should be printed.
     */
    private JComboBox<String> m_cPrintDecl;
    /**
     * Holds the source XML.
     */
    private JTextArea m_taSourceXML;
    /**
     * Holds the ident to use.
     */
    private JTextField m_tIdent;

    /**
     * Creates new form CoEXMLFormatter.
     */
    public CoEXMLFormatter()
    {
        initComponents();

        addWindowListener(new WindowAdapter()
            {
                public void windowClosed(WindowEvent e)
                {
                    System.exit(0);
                }
            });

        setTitle("CoE XML Formatter");

        m_taSourceXML.requestFocus();
        setSize(1024, 768);

        Toolkit tk = Toolkit.getDefaultToolkit();
        setLocation((int) ((tk.getScreenSize().getWidth() - getWidth()) / 2),
                    (int) ((tk.getScreenSize().getHeight() - getHeight()) / 2));
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
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            java.awt.EventQueue.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        new CoEXMLFormatter().setVisible(true);
                    }
                });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * This method formats the .
     *
     * @param  evt  DOCUMENTME
     */
    private void formatXML(java.awt.event.ActionEvent evt)
    {
        DocumentBuilder dbBuilder = XMLHelper.createDocumentBuilder(true);
        Document dDoc = null;

        try
        {
            dDoc = dbBuilder.parse(new ByteArrayInputStream(m_taSourceXML.getText().getBytes("UTF-8")));
        }
        catch (Exception e1)
        {
            MessageBoxUtil.showError("Error formatting XML", e1);
        }

        if (dDoc != null)
        {
            int iIdent = 4;

            try
            {
                iIdent = Integer.parseInt(m_tIdent.getText());
            }
            catch (Exception e)
            {
                iIdent = 4;
            }

            boolean bPrintDecl;

            try
            {
                bPrintDecl = Boolean.parseBoolean(m_cPrintDecl.getSelectedItem().toString());
            }
            catch (Exception e)
            {
                bPrintDecl = true;
            }

            boolean bPreserveWhitespace;

            try
            {
                bPreserveWhitespace = Boolean.parseBoolean(m_cPreserveWhitespace.getSelectedItem()
                                                           .toString());
            }
            catch (Exception e)
            {
                bPreserveWhitespace = true;
            }

            boolean bFormat;

            try
            {
                bFormat = Boolean.parseBoolean(m_cFormat.getSelectedItem().toString());
            }
            catch (Exception e)
            {
                bFormat = true;
            }

            // Do the formatting
            OutputFormat of = new OutputFormat(dDoc);

            of.setIndent(iIdent);
            of.setIndenting(bFormat);
            of.setOmitXMLDeclaration(!bPrintDecl);
            of.setPreserveSpace(bPreserveWhitespace);
            of.setLineWidth(0);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            XMLSerializer xs = new XMLSerializer(baos, of);

            try
            {
                xs.serialize(dDoc.getDocumentElement());
                m_taSourceXML.setText(baos.toString("UTF-8"));
            }
            catch (IOException e)
            {
                m_taSourceXML.setText(Util.getStackTrace(e));
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    private void initComponents()
    {
        JPanel pButtons = new javax.swing.JPanel();
        JButton bFormat = new javax.swing.JButton();
        JTabbedPane tpTabs = new javax.swing.JTabbedPane();

        JScrollPane spData = new javax.swing.JScrollPane();
        m_taSourceXML = new javax.swing.JTextArea();

        JPanel pConfig = new javax.swing.JPanel();
        JLabel jLabel1 = new javax.swing.JLabel();
        m_cFormat = new javax.swing.JComboBox<String>();

        JLabel jLabel2 = new javax.swing.JLabel();
        m_tIdent = new javax.swing.JTextField();

        JLabel jLabel3 = new javax.swing.JLabel();
        m_cPrintDecl = new javax.swing.JComboBox<String>();
        m_cPreserveWhitespace = new javax.swing.JComboBox<String>();

        JLabel jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setName("Form"); // NOI18N

        pButtons.setName("pButtons"); // NOI18N
        pButtons.setLayout(new java.awt.GridBagLayout());

        bFormat.setText("Format"); // NOI18N
        bFormat.setName("bFormat"); // NOI18N
        bFormat.setMnemonic('f');
        bFormat.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    formatXML(evt);
                }
            });
        pButtons.add(bFormat, new java.awt.GridBagConstraints());

        tpTabs.setName("tpTabs"); // NOI18N

        spData.setName("spData"); // NOI18N

        m_taSourceXML.setColumns(20);
        m_taSourceXML.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        m_taSourceXML.setRows(5);
        m_taSourceXML.setName("m_taSourceXML"); // NOI18N
        spData.setViewportView(m_taSourceXML);

        tpTabs.addTab("XML Data", spData); // NOI18N

        pConfig.setName("pConfig"); // NOI18N

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Format:"); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        m_cFormat.setModel(new javax.swing.DefaultComboBoxModel<String>(new String[] { "true", "false" }));
        m_cFormat.setName("m_cFormat"); // NOI18N

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Spacing:"); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        m_tIdent.setText("4"); // NOI18N
        m_tIdent.setName("m_tIdent"); // NOI18N

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Print XML declaration:"); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        m_cPrintDecl.setModel(new javax.swing.DefaultComboBoxModel<String>(new String[]
                                                                   {
                                                                       "true", "false"
                                                                   }));
        m_cPrintDecl.setName("m_cPrintDecl"); // NOI18N

        m_cPreserveWhitespace.setModel(new javax.swing.DefaultComboBoxModel<String>(new String[]
                                                                            {
                                                                                "true", "false"
                                                                            }));
        m_cPreserveWhitespace.setSelectedIndex(1);
        m_cPreserveWhitespace.setName("m_cPreserveWhitespace"); // NOI18N

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Preserve whitespace:"); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        javax.swing.GroupLayout pConfigLayout = new javax.swing.GroupLayout(pConfig);
        pConfig.setLayout(pConfigLayout);
        pConfigLayout.setHorizontalGroup(pConfigLayout.createParallelGroup(javax.swing.GroupLayout
                                                                           .Alignment.LEADING)
                                         .addGroup(pConfigLayout.createSequentialGroup()
                                                   .addContainerGap().addGroup(pConfigLayout
                                                                               .createParallelGroup(javax
                                                                                                    .swing
                                                                                                    .GroupLayout
                                                                                                    .Alignment.LEADING)
                                                                               .addGroup(pConfigLayout
                                                                                         .createSequentialGroup()
                                                                                         .addGroup(pConfigLayout
                                                                                                   .createParallelGroup(javax
                                                                                                                        .swing
                                                                                                                        .GroupLayout
                                                                                                                        .Alignment.LEADING)
                                                                                                   .addComponent(jLabel2,
                                                                                                                 javax
                                                                                                                 .swing
                                                                                                                 .GroupLayout.PREFERRED_SIZE,
                                                                                                                 104,
                                                                                                                 javax
                                                                                                                 .swing
                                                                                                                 .GroupLayout.PREFERRED_SIZE)
                                                                                                   .addComponent(jLabel1))
                                                                                         .addPreferredGap(javax
                                                                                                          .swing
                                                                                                          .LayoutStyle
                                                                                                          .ComponentPlacement.RELATED)
                                                                                         .addGroup(pConfigLayout
                                                                                                   .createParallelGroup(javax
                                                                                                                        .swing
                                                                                                                        .GroupLayout
                                                                                                                        .Alignment.LEADING,
                                                                                                                        false)
                                                                                                   .addComponent(m_tIdent)
                                                                                                   .addComponent(m_cFormat,
                                                                                                                 0,
                                                                                                                 79,
                                                                                                                 Short.MAX_VALUE)))
                                                                               .addGroup(pConfigLayout
                                                                                         .createSequentialGroup()
                                                                                         .addGroup(pConfigLayout
                                                                                                   .createParallelGroup(javax
                                                                                                                        .swing
                                                                                                                        .GroupLayout
                                                                                                                        .Alignment.LEADING)
                                                                                                   .addComponent(jLabel3)
                                                                                                   .addComponent(jLabel4))
                                                                                         .addPreferredGap(javax
                                                                                                          .swing
                                                                                                          .LayoutStyle
                                                                                                          .ComponentPlacement.RELATED)
                                                                                         .addGroup(pConfigLayout
                                                                                                   .createParallelGroup(javax
                                                                                                                        .swing
                                                                                                                        .GroupLayout
                                                                                                                        .Alignment.LEADING)
                                                                                                   .addComponent(m_cPreserveWhitespace,
                                                                                                                 javax
                                                                                                                 .swing
                                                                                                                 .GroupLayout.PREFERRED_SIZE,
                                                                                                                 79,
                                                                                                                 javax
                                                                                                                 .swing
                                                                                                                 .GroupLayout.PREFERRED_SIZE)
                                                                                                   .addComponent(m_cPrintDecl,
                                                                                                                 javax
                                                                                                                 .swing
                                                                                                                 .GroupLayout.PREFERRED_SIZE,
                                                                                                                 79,
                                                                                                                 javax
                                                                                                                 .swing
                                                                                                                 .GroupLayout.PREFERRED_SIZE))))
                                                   .addContainerGap(198, Short.MAX_VALUE)));

        pConfigLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL,
                               new java.awt.Component[] { jLabel1, jLabel2, jLabel3, jLabel4 });

        pConfigLayout.setVerticalGroup(pConfigLayout.createParallelGroup(javax.swing.GroupLayout
                                                                         .Alignment.LEADING)
                                       .addGroup(pConfigLayout.createSequentialGroup()
                                                 .addContainerGap().addGroup(pConfigLayout
                                                                             .createParallelGroup(javax
                                                                                                  .swing
                                                                                                  .GroupLayout
                                                                                                  .Alignment.BASELINE)
                                                                             .addComponent(jLabel2)
                                                                             .addComponent(m_tIdent,
                                                                                           javax
                                                                                           .swing
                                                                                           .GroupLayout.PREFERRED_SIZE,
                                                                                           javax
                                                                                           .swing
                                                                                           .GroupLayout.DEFAULT_SIZE,
                                                                                           javax
                                                                                           .swing
                                                                                           .GroupLayout.PREFERRED_SIZE))
                                                 .addPreferredGap(javax.swing.LayoutStyle
                                                                  .ComponentPlacement.RELATED)
                                                 .addGroup(pConfigLayout.createParallelGroup(javax
                                                                                             .swing
                                                                                             .GroupLayout
                                                                                             .Alignment.BASELINE)
                                                           .addComponent(m_cFormat,
                                                                         javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                         javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                         javax.swing.GroupLayout.PREFERRED_SIZE)
                                                           .addComponent(jLabel1)).addPreferredGap(javax
                                                                                                   .swing
                                                                                                   .LayoutStyle
                                                                                                   .ComponentPlacement.RELATED)
                                                 .addGroup(pConfigLayout.createParallelGroup(javax
                                                                                             .swing
                                                                                             .GroupLayout
                                                                                             .Alignment.BASELINE)
                                                           .addComponent(jLabel3).addComponent(m_cPrintDecl,
                                                                                               javax
                                                                                               .swing
                                                                                               .GroupLayout.PREFERRED_SIZE,
                                                                                               javax
                                                                                               .swing
                                                                                               .GroupLayout.DEFAULT_SIZE,
                                                                                               javax
                                                                                               .swing
                                                                                               .GroupLayout.PREFERRED_SIZE))
                                                 .addPreferredGap(javax.swing.LayoutStyle
                                                                  .ComponentPlacement.RELATED)
                                                 .addGroup(pConfigLayout.createParallelGroup(javax
                                                                                             .swing
                                                                                             .GroupLayout
                                                                                             .Alignment.BASELINE)
                                                           .addComponent(m_cPreserveWhitespace,
                                                                         javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                         javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                         javax.swing.GroupLayout.PREFERRED_SIZE)
                                                           .addComponent(jLabel4)).addContainerGap(167,
                                                                                                   Short.MAX_VALUE)));

        tpTabs.addTab("Settings", pConfig); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                  .addGroup(layout.createSequentialGroup().addGap(10, 10, 10)
                                            .addComponent(pButtons,
                                                          javax.swing.GroupLayout.DEFAULT_SIZE, 390,
                                                          Short.MAX_VALUE)).addComponent(tpTabs,
                                                                                         javax.swing
                                                                                         .GroupLayout.DEFAULT_SIZE,
                                                                                         400,
                                                                                         Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                          layout.createSequentialGroup().addComponent(tpTabs,
                                                                                      javax.swing
                                                                                      .GroupLayout.DEFAULT_SIZE,
                                                                                      307,
                                                                                      Short.MAX_VALUE)
                                          .addPreferredGap(javax.swing.LayoutStyle
                                                           .ComponentPlacement.RELATED)
                                          .addComponent(pButtons,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 29,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)));

        pack();
    } // </editor-fold>
}
