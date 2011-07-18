package com.cordys.coe.tools.es.swt.filter;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;

import org.eclipse.swt.graphics.Image;

/**
 * DOCUMENTME.
 *
 * @author  $author$
 */
public class Log4JFilterLabelProvider extends LabelProvider
    implements ITableLabelProvider
{
    // Names of images used to represent checkboxes

    /**
     * DOCUMENTME.
     */
    public static final String CHECKED_IMAGE = "checked";
    /**
     * DOCUMENTME.
     */
    public static final String UNCHECKED_IMAGE = "unchecked";

    // For the checkbox images
    /**
     * DOCUMENTME.
     */
    private static ImageRegistry imageRegistry = new ImageRegistry();

    /**
     * Note: An image registry owns all of the image objects registered with it, and automatically
     * disposes of them the SWT Display is disposed.
     */
    static
    {
        String iconPath = "icons/";
        imageRegistry.put(CHECKED_IMAGE,
                          ImageDescriptor.createFromFile(Log4JFilterLabelProvider.class,
                                                         iconPath + CHECKED_IMAGE + ".gif"));
        imageRegistry.put(UNCHECKED_IMAGE,
                          ImageDescriptor.createFromFile(Log4JFilterLabelProvider.class,
                                                         iconPath + UNCHECKED_IMAGE + ".gif"));
    }

    /**
     * @see  org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
     */
    public Image getColumnImage(Object element, int columnIndex)
    {
        return (columnIndex == 2) ? // CAPTURE
                                   getImage(((ESCLog4JFilter) element).shouldCapture()) : null;
    }

    /**
     * @see  org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
     */
    public String getColumnText(Object element, int columnIndex)
    {
        String result = "";
        ESCLog4JFilter task = (ESCLog4JFilter) element;

        switch (columnIndex)
        {
            case 0: // CATEGORY
                result = task.getCategory();
                break;

            case 1: // LEVEL
                result = task.getLevel();
                break;

            case 2: // CAPTURE
                break;

            default:
                break;
        }
        return result;
    }

    /**
     * Returns the image with the given key, or <code>null</code> if not found.
     *
     * @param   isSelected  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    private Image getImage(boolean isSelected)
    {
        String key = isSelected ? CHECKED_IMAGE : UNCHECKED_IMAGE;
        return imageRegistry.get(key);
    }
}
