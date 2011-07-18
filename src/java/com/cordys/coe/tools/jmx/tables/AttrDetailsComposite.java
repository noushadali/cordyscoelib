package com.cordys.coe.tools.jmx.tables;

import com.cordys.coe.tools.jmx.IUpdateAttributeDetails;
import com.cordys.coe.tools.jmx.IWritableAttributeHandler;
import com.cordys.coe.tools.jmx.factory.AttributeControlFactory;
import com.cordys.coe.tools.jmx.resources.MBeanAttributeInfoWrapper;
import com.cordys.coe.util.swt.MessageBoxUtil;
import com.cordys.coe.util.swt.SWTUtils;

import javax.management.Attribute;
import javax.management.MBeanAttributeInfo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * This composite shows the details of a certain attribute.
 *
 * @author  pgussow
 */
public class AttrDetailsComposite extends Composite
    implements IUpdateAttributeDetails, IWritableAttributeHandler
{
    /**
     * Holds teh attribute currently shown.
     */
    private MBeanAttributeInfoWrapper m_baiwAttributeInfo;
    /**
     * Holds the composite where the details of the attribute will be shown.
     */
    private Composite m_cAttrDetailsComposite;
    /**
     * DOCUMENTME.
     */
    private Control m_cCurrentAttrControl;
    /**
     * Holds teh attribute description.
     */
    private Text m_tAttrDescription;
    /**
     * Holds the attribute name.
     */
    private Text m_tAttrName;
    /**
     * Holds teh attribute type.
     */
    private Text m_tAttrType;

    /**
     * Create the composite.
     *
     * @param  parent
     * @param  style
     */
    public AttrDetailsComposite(Composite parent, int style)
    {
        super(parent, style);
        //
        setLayout(new GridLayout());

        final Group detailsGroup = new Group(this, SWT.NONE);
        detailsGroup.setText(" Details ");
        detailsGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final GridLayout gridLayout_2 = new GridLayout();
        gridLayout_2.numColumns = 2;
        detailsGroup.setLayout(gridLayout_2);

        final Label nameLabel = new Label(detailsGroup, SWT.NONE);
        nameLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        nameLabel.setText("Name:");

        m_tAttrName = new Text(detailsGroup, SWT.READ_ONLY);
        m_tAttrName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Label typeLabel = new Label(detailsGroup, SWT.NONE);
        typeLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        typeLabel.setText("Type:");

        m_tAttrType = new Text(detailsGroup, SWT.READ_ONLY);
        m_tAttrType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Label descriptionLabel = new Label(detailsGroup, SWT.NONE);
        descriptionLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        descriptionLabel.setText("Description:");

        m_tAttrDescription = new Text(detailsGroup, SWT.READ_ONLY);
        m_tAttrDescription.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        m_cAttrDetailsComposite = new Composite(this, SWT.NONE);
        m_cAttrDetailsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        m_cAttrDetailsComposite.setLayout(new GridLayout());
    }

    /**
     * @see  com.cordys.coe.tools.jmx.IUpdateAttributeDetails#clean()
     */
    public void clean()
    {
        m_tAttrName.setText("");
        m_tAttrType.setText("");
        m_tAttrDescription.setText("");

        // Clean up the current
        SWTUtils.disposeChildren(m_cAttrDetailsComposite);
    }

    /**
     * DOCUMENTME.
     */
    @Override public void dispose()
    {
        super.dispose();
    }

    /**
     * @see  com.cordys.coe.tools.jmx.IUpdateAttributeDetails#updateDetails(com.cordys.coe.tools.jmx.resources.MBeanAttributeInfoWrapper)
     */
    public void updateDetails(MBeanAttributeInfoWrapper baiwAttributeInfo)
    {
        m_baiwAttributeInfo = baiwAttributeInfo;
        m_tAttrName.setText(baiwAttributeInfo.getMBeanAttributeInfo().getName());
        m_tAttrType.setText(baiwAttributeInfo.getMBeanAttributeInfo().getType());
        m_tAttrDescription.setText(baiwAttributeInfo.getMBeanAttributeInfo().getDescription());

        // Clean up the current
        SWTUtils.disposeChildren(m_cAttrDetailsComposite);

        // Based on the attribute details create the composite.
        try
        {
            String sType = String.class.getName();
            Object oValue = baiwAttributeInfo.getValue();

            if (oValue != null)
            {
                sType = oValue.getClass().getName();
            }
            m_cCurrentAttrControl = AttributeControlFactory.createControl(m_cAttrDetailsComposite,
                                                                          baiwAttributeInfo
                                                                          .getMBeanAttributeInfo()
                                                                          .isWritable(), sType,
                                                                          oValue, this);

            GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
            m_cCurrentAttrControl.setLayoutData(gd);
            m_cAttrDetailsComposite.layout();
        }
        catch (Exception e)
        {
            MessageBoxUtil.showError(getShell(), "Error creating attribute control", e);
        }
    }

    /**
     * This method updates the value for the attribute.
     *
     * @param  oNewValue  The new value for the attribute.
     *
     * @see    com.cordys.coe.tools.jmx.IWritableAttributeHandler#write(java.lang.Object)
     */
    public void write(Object oNewValue)
    {
        if (m_baiwAttributeInfo != null)
        {
            // Check if we can set the value.
            MBeanAttributeInfo baiAttribute = m_baiwAttributeInfo.getMBeanAttributeInfo();
            Attribute aNewAttrValue = new Attribute(m_baiwAttributeInfo.getMBeanAttributeInfo()
                                                    .getName(), oNewValue);

            try
            {
                m_baiwAttributeInfo.getMBeanServerConnection().setAttribute(m_baiwAttributeInfo
                                                                            .getMBeanInfoWrapper()
                                                                            .getObjectName(),
                                                                            aNewAttrValue);
            }
            catch (Exception e)
            {
                MessageBoxUtil.showError(getShell(),
                                         "Error setting the value for attribute " +
                                         baiAttribute.getName(), e);
            }
        }
    }

    /**
     * Disable the check that prevents subclassing of SWT components.
     */
    @Override protected void checkSubclass()
    {
        // Disable the check that prevents subclassing of SWT components
    }
}
