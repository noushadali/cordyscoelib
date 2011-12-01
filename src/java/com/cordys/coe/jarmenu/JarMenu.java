/**
 *  2003 Cordys R&D B.V. All rights reserved.     The computer program(s) is
 * the proprietary information of Cordys R&D B.V.     and provided under the
 * relevant License Agreement containing restrictions     on use and
 * disclosure. Use is subject to the License Agreement.
 */
package com.cordys.coe.jarmenu;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.cordys.coe.tools.contentcopier.MainForm;
import com.cordys.coe.tools.findclass.FindClassInJar;
import com.cordys.coe.tools.flowinfo.FlowInfo;
import com.cordys.coe.tools.jmx.JMXTestTool;
import com.cordys.coe.tools.log4j.Log4JViewer;
import com.cordys.coe.tools.migration.xforms.XFormsMigrationValidator;
import com.cordys.coe.tools.orgmanager.OrganizationManager;
import com.cordys.coe.tools.snapshot.SystemSnapshot;
import com.cordys.coe.tools.testtool.CoeMethodTestTool;
import com.cordys.coe.tools.wcpproperties.WCPPropertiesEditor;
import com.cordys.coe.tools.xmlformatter.CoEXMLFormatter;
import com.cordys.coe.util.general.Util;
import com.cordys.coe.util.wsdl.updateMethodGenerator.UpdateMethodsGenerator;

/**
 * Class to display all tools in the project.
 */
public class JarMenu extends JDialog
{
    /**
     * Holds the maximum width of a button.
     */
    private static final int BUTTON_WIDTH = 300;
    /**
     * Holds the maximum height of a button.
     */
    private static final int BUTTON_HEIGHT = 25;
    /**
     * Holds the margin.
     */
    private static final int MARGIN = 10;

    /**
     * Constructor.
     *
     * @param  jParent  The parent frame.
     * @param  bModal   Whether or not to show the dialog as a modal dialog.
     */
    public JarMenu(JFrame jParent, boolean bModal)
    {
        super(jParent, bModal);
        getContentPane().setLayout(null);
        setTitle("Center of Excellence Tools Menu");

        ArrayList<JButton> alTemp = new ArrayList<JButton>();
        JButton bTemp = null;

        // Log4J viewer.
        bTemp = createSWTStartButton("Log4J viewer (SWT)", Log4JViewer.class, "cordyslog4j.gif");

        alTemp.add(bTemp);

        // JMX viewer.
        bTemp = createSWTStartButton("JMX viewer (SWT)", JMXTestTool.class,
                                     "image/releng_gears.gif");

        alTemp.add(bTemp);
        
        bTemp = createSWTStartButton("System Snapshot Grabber (Swing)", SystemSnapshot.class, "jmx.png");
        alTemp.add(bTemp);

        // Class finder.
        bTemp = createSWTStartButton("Find Class (SWT)", FindClassInJar.class, "javaclass.gif");

        alTemp.add(bTemp);
        // CoE Method Test Tool
        bTemp = createSWTStartButton("CoE Method Test Tool (SWT)", CoeMethodTestTool.class,
                                     "methodtesttool.gif");

        alTemp.add(bTemp);

        // Flow Info
        bTemp = createSWTStartButton("CoE Process Instance Viewer (SWT)", FlowInfo.class, "bpm.gif");

        alTemp.add(bTemp);
        
        // Organization manager
        bTemp = createStartButton("Organization manager (Swing)", OrganizationManager.class, "orgman.gif");
        alTemp.add(bTemp);

        // Update method WSDL generator.
        bTemp = createStartButton("CoE XML Formatter (Swing)", CoEXMLFormatter.class, null);
        alTemp.add(bTemp);

        // XForm migration validation
        bTemp = createSWTStartButton("XForm migration validator (SWT)",
                                     XFormsMigrationValidator.class, "xform.gif");

        alTemp.add(bTemp);

        // Content copier.
        bTemp = createStartButton("Content Copier (Swing)", MainForm.class, "contentcopier.gif");
        alTemp.add(bTemp);

        // User admin tool
        bTemp = createSWTStartButton("User Admin (SWT)",
                                     com.cordys.coe.tools.useradmin.swt.UserAdmin.class,
                                     "useradmin.gif");
        alTemp.add(bTemp);

        // Event service client.
        bTemp = createSWTStartButton("EventServiceClient (SWT)",
                                     com.cordys.coe.tools.es.swt.EventServiceClient.class,
                                     "esc.gif");

        alTemp.add(bTemp);

        // WCP.properties editor
        bTemp = createSWTStartButton("wcp.properties editor (SWT)", WCPPropertiesEditor.class,
                                     "image/wcpproperties.gif");

        alTemp.add(bTemp);

        // Update method WSDL generator.
        bTemp = createStartButton("Update method WSDL generator (Swing)", UpdateMethodsGenerator.class,
                                  null);
        alTemp.add(bTemp);

        int iCurrentY = MARGIN;
        int iCurrentX = MARGIN;

        for (Iterator<JButton> iButtons = alTemp.iterator(); iButtons.hasNext();)
        {
            JButton baButton = iButtons.next();
            baButton.setLocation(iCurrentX, iCurrentY);
            getContentPane().add(baButton);
            iCurrentY += (MARGIN + BUTTON_HEIGHT);
        }

        // Add the close-button.
        addWindowListener(new WindowAdapter()
            {
                public void windowClosing(WindowEvent evt)
                {
                    System.exit(0);
                }
            });

        int iWidth = (MARGIN * 2) + BUTTON_WIDTH;
        int iHeight = iCurrentY + BUTTON_HEIGHT;
        int iWinX = Toolkit.getDefaultToolkit().getScreenSize().width;
        int iWinY = Toolkit.getDefaultToolkit().getScreenSize().height;
        iWinX = (iWinX / 2) - (iWidth / 2);
        iWinY = (iWinY / 2) - (iHeight / 2);
        setSize(iWidth, iHeight);
        setLocation(iWinX, iWinY);
    }

    /**
     * Main method.
     *
     * @param  args
     */
    public static void main(String[] args)
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            JarMenu jmMenu = new JarMenu(new JFrame(), true);
            jmMenu.setVisible(true);
        }
        catch (Exception e)
        {
            String sStack = Util.getStackTrace(e);
            JOptionPane.showMessageDialog(null, "General error:\n" + e + "\n" + sStack);
        }
    }

    /**
     * This method creates a button to start a certain application.
     *
     * @param   sCaption    The caption for the button.
     * @param   cForm       The Class-object for the form.
     * @param   sImageName  The name of the image to load.
     *
     * @return  The newly instantiated button.
     */
    private JButton createStartButton(String sCaption, Class<?> cForm, String sImageName)
    {
        JButton bReturn = new JButton(sCaption);
        bReturn.setFont(new Font("Tahoma", Font.PLAIN, 10));

        if ((sImageName != null) && (sImageName.length() > 0))
        {
            bReturn.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().createImage(cForm.getResource(sImageName))));
        }
        bReturn.addActionListener(new ButtonActionListener(cForm));
        bReturn.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        return bReturn;
    }

    /**
     * This method creates a button to start a certain application.
     *
     * @param   sCaption    The caption for the button.
     * @param   cForm       The Class-object for the form.
     * @param   sImageName  The name of the image to load.
     *
     * @return  The newly instantiated button.
     */
    private JButton createSWTStartButton(String sCaption, Class<?> cForm, String sImageName)
    {
        JButton bReturn = new JButton(sCaption);
        bReturn.setFont(new Font("Tahoma", Font.PLAIN, 10));

        if ((sImageName != null) && (sImageName.length() > 0))
        {
            bReturn.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().createImage(cForm.getResource(sImageName))));
        }
        bReturn.addActionListener(new SWTButtonActionListener(cForm));
        bReturn.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        return bReturn;
    }

    /**
     * Class to execute the different utilities.
     */
    public class ButtonActionListener
        implements ActionListener
    {
        /**
         * Holds the class to instantiate.
         */
        private Class<?> cClass;

        /**
         * Constructor.
         *
         * @param  cClass  The class to display
         */
        public ButtonActionListener(Class<?> cClass)
        {
            this.cClass = cClass;
        }

        /**
         * Instantiates the class and displays it.
         *
         * @param  e  The event that occurred.
         */
        public void actionPerformed(ActionEvent e)
        {
            try
            {
                Object oObject = cClass.newInstance();

                if (oObject instanceof JFrame)
                {
                    JarMenu.this.setVisible(false);
                    ((JFrame) oObject).setVisible(true);
                }
            }
            catch (Exception exc)
            {
                JOptionPane.showMessageDialog(JarMenu.this, "Error in application:\n" + exc,
                                              "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }
    }

    /**
     * Class to execute the different utilities.
     */
    public class SWTButtonActionListener
        implements ActionListener
    {
        /**
         * Holds the class to instantiate.
         */
        private Class<?> cClass;

        /**
         * Constructor.
         *
         * @param  cClass  The class to display
         */
        public SWTButtonActionListener(Class<?> cClass)
        {
            this.cClass = cClass;
        }

        /**
         * Instantiates the class and displays it.
         *
         * @param  e  The event that occurred.
         */
        public void actionPerformed(ActionEvent e)
        {
            try
            {
                Object oInstance = null;
                Method mMain = cClass.getDeclaredMethod("main", new Class[] { String[].class });
                JarMenu.this.setVisible(false);
                mMain.invoke(oInstance, new Object[] { new String[0] });
            }
            catch (Exception exc)
            {
                exc.printStackTrace();
                
                StringSelection data = new StringSelection(Util.getStackTrace(exc));
                Clipboard clipboard = 
                     Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(data, data);
                
                JOptionPane.showMessageDialog(JarMenu.this, "Error in application:\n" + Util.getStackTrace(exc),
                                              "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }
    }
}
