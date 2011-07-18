package com.cordys.coe.util.swing;

/**
 * This interface describes the methods which can be used to take action on when buttons are clicked
 * on the CGCLoginPanel.
 *
 * @author  pgussow
 */
public interface ICGCLoginPanelListener
{
    /**
     * This method is called when the user clicked the cancel button.
     *
     * @param  clpPanel  The current panel.
     */
    void onCancelClicked(CGCLoginPanel clpPanel);

    /**
     * DOCUMENTME.
     *
     * @param  clpPanel  The current panel.
     */
    void onOKClick(CGCLoginPanel clpPanel);

    /**
     * This method is called when the user clicks the test button.
     *
     * @param  clpPanel  The current panel.
     */
    void onTestClick(CGCLoginPanel clpPanel);
}
