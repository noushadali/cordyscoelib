package com.cordys.coe.tools.es.swt;

import com.cordys.coe.tools.log4j.Util;
import com.cordys.coe.util.swt.BorderLayout;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Detailed panel holding the specific details of a message.
 *
 * @author  pgussow
 */
public class DetailsComposite extends Composite
{
    /**
     * Holds the exception details.
     */
    private StyledText m_stException;
    /**
     * Holds the message details.
     */
    private StyledText m_stMessage;
    /**
     * Holds the location info.
     */
    private Text m_tLocationInfo;

    /**
     * Create the composite.
     *
     * @param  cParent  The parent composite.
     * @param  iStyle   The SWT style.
     */
    public DetailsComposite(Composite cParent, int iStyle)
    {
        super(cParent, iStyle);
        setLayout(new BorderLayout(0, 0));

        final Composite cLocation = new Composite(this, SWT.NONE);
        cLocation.setLayoutData(BorderLayout.NORTH);

        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        cLocation.setLayout(gridLayout);

        final Label locationLabel = new Label(cLocation, SWT.NONE);
        locationLabel.setText("Location:");

        m_tLocationInfo = new Text(cLocation, SWT.BORDER);
        m_tLocationInfo.setEnabled(false);
        m_tLocationInfo.setEditable(false);
        m_tLocationInfo.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

        final Composite cDetails = new Composite(this, SWT.NONE);
        cDetails.setLayout(new BorderLayout(0, 0));

        final SashForm sashForm = new SashForm(cDetails, SWT.NONE);

        final Composite cMessageDetails = new Composite(sashForm, SWT.NONE);
        cMessageDetails.setLayout(new BorderLayout(0, 0));

        final Label messageLabel = new Label(cMessageDetails, SWT.NONE);
        messageLabel.setLayoutData(BorderLayout.NORTH);
        messageLabel.setText("Message");

        m_stMessage = new StyledText(cMessageDetails,
                                     SWT.V_SCROLL | SWT.MULTI | SWT.READ_ONLY | SWT.BORDER |
                                     SWT.H_SCROLL);
        m_stMessage.setEditable(false);

        final Composite cExceptionDetails = new Composite(sashForm, SWT.NONE);
        cExceptionDetails.setLayout(new BorderLayout(0, 0));

        final Label exceptionLabel = new Label(cExceptionDetails, SWT.NONE);
        exceptionLabel.setLayoutData(BorderLayout.NORTH);
        exceptionLabel.setText("Exception");

        m_stException = new StyledText(cExceptionDetails,
                                       SWT.V_SCROLL | SWT.MULTI | SWT.READ_ONLY | SWT.BORDER |
                                       SWT.H_SCROLL);
        m_stException.setEditable(false);
        sashForm.setWeights(new int[] { 3, 1 });
        //
    }

    /**
     * This method clears the values.
     */
    public void clearFields()
    {
        m_stException.setText("");
        m_stMessage.setText("");
        m_tLocationInfo.setText("");
    }

    /**
     * This method is called when the composite is disposed.
     */
    @Override public void dispose()
    {
        super.dispose();
    }

    /**
     * This method sets the information for this panel.
     *
     * @param  sMessage       The message.
     * @param  sException     The exception.
     * @param  sLocationInfo  The location information.
     */
    public void setInformation(String sMessage, String sException, String sLocationInfo)
    {
        if (sException == null)
        {
            sException = "";
        }
        m_stException.setText(sException);

        if (sMessage == null)
        {
            sMessage = "";
        }
        m_stMessage.setText(Util.formatXmlMessage(sMessage));

        if (sLocationInfo == null)
        {
            sLocationInfo = "";
        }
        m_tLocationInfo.setText(sLocationInfo);
    }

    /**
     * Disable the check that prevents subclassing of SWT components.
     */
    @Override protected void checkSubclass()
    {
        // Disable the check that prevents subclassing of SWT components
    }
}
