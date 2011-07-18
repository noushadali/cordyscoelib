package com.cordys.coe.tools.jmx;

import com.cordys.coe.tools.jmx.resources.MBeanInfoWrapper;
import com.cordys.coe.tools.jmx.resources.MBeanNotificationInfoWrapper;
import com.cordys.coe.util.swt.MessageBoxUtil;

import java.text.SimpleDateFormat;

import java.util.Date;

import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationFilterSupport;
import javax.management.NotificationListener;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * This composite shows the details for the notifications.
 *
 * @author  pgussow
 */
public class NotificationComposite extends Composite
{
    /**
     * Holds the notification listener.
     */
    private JMXNotificationListener m_nlListener;
    /**
     * Holds the MBean for this composite.
     */
    private MBeanInfoWrapper m_biwBeanInfo;
    /**
     * Holds the filter to use.
     */
    private NotificationFilterSupport m_nfsFilter = new NotificationFilterSupport();
    /**
     * Holds the list of possible notifications.
     */
    private Table m_tblListNotifications;
    /**
     * Holds the actual notifications that occurred.
     */
    private Table m_tblNotifications;
    /**
     * The viewer listing all the possible notifications.
     */
    private TableViewer m_tvListNotifications;

    /**
     * Create the composite.
     *
     * @param  parent
     * @param  style
     */
    public NotificationComposite(Composite parent, int style)
    {
        super(parent, style);

        final GridLayout gridLayout = new GridLayout();
        gridLayout.makeColumnsEqualWidth = true;
        setLayout(gridLayout);

        m_tvListNotifications = new TableViewer(this, SWT.FULL_SELECTION | SWT.BORDER);
        m_tvListNotifications.setLabelProvider(new TableLabelProvider());
        m_tvListNotifications.setContentProvider(new ContentProvider());
        m_tblListNotifications = m_tvListNotifications.getTable();
        m_tblListNotifications.setLinesVisible(true);
        m_tblListNotifications.setHeaderVisible(true);
        m_tblListNotifications.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final TableColumn newColumnTableColumn_1 = new TableColumn(m_tblListNotifications,
                                                                   SWT.NONE);
        newColumnTableColumn_1.setWidth(330);
        newColumnTableColumn_1.setText("Description");

        final TableColumn newColumnTableColumn = new TableColumn(m_tblListNotifications, SWT.NONE);
        newColumnTableColumn.setWidth(150);
        newColumnTableColumn.setText("Name");

        final TableColumn newColumnTableColumn_2 = new TableColumn(m_tblListNotifications,
                                                                   SWT.NONE);
        newColumnTableColumn_2.setWidth(200);
        newColumnTableColumn_2.setText("Types");

        final Menu menu = new Menu(m_tblListNotifications);
        m_tblListNotifications.setMenu(menu);

        final MenuItem miSubscribe = new MenuItem(menu, SWT.NONE);
        miSubscribe.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    subscribe();
                }
            });
        miSubscribe.setAccelerator(SWT.ALT | 'S');
        miSubscribe.setText("Subscribe");

        final MenuItem miUnsubscribe = new MenuItem(menu, SWT.NONE);
        miUnsubscribe.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    unsubscribe();
                }
            });
        miUnsubscribe.setAccelerator(SWT.ALT | 'U');
        miUnsubscribe.setText("Unsubscribe");

        final Button clearButton = new Button(this, SWT.NONE);
        clearButton.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    m_tblNotifications.removeAll();
                }
            });

        final GridData gridData = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        gridData.widthHint = 75;
        clearButton.setLayoutData(gridData);
        clearButton.setText("&Clear");

        m_tblNotifications = new Table(this, SWT.FULL_SELECTION | SWT.BORDER);
        m_tblNotifications.setLinesVisible(true);
        m_tblNotifications.setHeaderVisible(true);
        m_tblNotifications.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final TableColumn newColumnTableColumn_3 = new TableColumn(m_tblNotifications, SWT.NONE);
        newColumnTableColumn_3.setWidth(125);
        newColumnTableColumn_3.setText("Date");

        final TableColumn newColumnTableColumn_4 = new TableColumn(m_tblNotifications, SWT.NONE);
        newColumnTableColumn_4.setWidth(60);
        newColumnTableColumn_4.setText("Sequence");

        final TableColumn newColumnTableColumn_5 = new TableColumn(m_tblNotifications, SWT.NONE);
        newColumnTableColumn_5.setWidth(300);
        newColumnTableColumn_5.setText("Message");

        final TableColumn newColumnTableColumn_6 = new TableColumn(m_tblNotifications, SWT.NONE);
        newColumnTableColumn_6.setWidth(200);
        newColumnTableColumn_6.setText("Type");

        final Menu menu_1 = new Menu(m_tblNotifications);
        m_tblNotifications.setMenu(menu_1);

        final MenuItem miDetails = new MenuItem(menu_1, SWT.NONE);
        miDetails.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    showMessageDetails();
                }
            });
        miDetails.setText("Message Details");
        //
    }

    /**
     * This method is called when the composite is disposed. It will remove the listener.
     */
    @Override public void dispose()
    {
        super.dispose();
    }

    /**
     * This method gets the notification information for this composite.
     *
     * @return  The notification information for this composite.
     */
    public MBeanInfoWrapper getMBeanInfoWrapper()
    {
        return m_biwBeanInfo;
    }

    /**
     * This method sets the notification information for this composite.
     *
     * @param  biwBeanInfo  The notification information for this composite.
     */
    public void setMBeanInfoWrapper(MBeanInfoWrapper biwBeanInfo)
    {
        if (m_nlListener != null)
        {
            // We need to remove the listener before showing a new one.
            try
            {
                m_biwBeanInfo.getMBeanServerConnection().removeNotificationListener(m_biwBeanInfo
                                                                                    .getObjectName(),
                                                                                    m_nlListener);
                m_nlListener = null;
                m_nfsFilter.disableAllTypes();
            }
            catch (Exception e)
            {
                // Ignore it.
                e.printStackTrace();
            }
        }
        m_biwBeanInfo = biwBeanInfo;
        m_tvListNotifications.setInput(biwBeanInfo);

        // Subscribe to the notifications, but only if the MBean exposes notifications.
        try
        {
            MBeanNotificationInfo[] anNotifications = m_biwBeanInfo.getMBeanInfo()
                                                                   .getNotifications();

            if ((anNotifications != null) && (anNotifications.length > 0))
            {
                m_nlListener = new JMXNotificationListener(getShell().getDisplay());

                // Not using a filter, since the filter will be executed in the remote JVM. This
                // means the class should be avialable on the classpath.
                m_biwBeanInfo.getMBeanServerConnection().addNotificationListener(m_biwBeanInfo
                                                                                 .getObjectName(),
                                                                                 m_nlListener, null,
                                                                                 null);
            }
        }
        catch (Exception e)
        {
            MessageBoxUtil.showError(getShell(), "Cannot subscribe to the notification", e);
        }
    }

    /**
     * DOCUMENTME.
     */
    @Override protected void checkSubclass()
    {
        // Disable the check that prevents subclassing of SWT components
    }

    /**
     * This method shows the message details of the notification.
     */
    protected void showMessageDetails()
    {
        TableItem[] atiTemp = m_tblNotifications.getSelection();

        if ((atiTemp != null) && (atiTemp.length > 0))
        {
            String sDetails = atiTemp[0].getText(2);
            ShowDetailDialog sds = new ShowDetailDialog(getShell());
            sds.setCurrentText(sDetails);
            sds.setEnabled(false);

            sds.open();
        }
    }

    /**
     * This method subscribes to the selected notification.
     */
    protected void subscribe()
    {
        TableItem[] atiTemp = m_tblListNotifications.getSelection();

        if ((atiTemp != null) && (atiTemp.length > 0))
        {
            Object oTemp = atiTemp[0].getData();

            if (oTemp instanceof MBeanNotificationInfoWrapper)
            {
                // MBeanNotificationInfoWrapper bniwNotification = (MBeanNotificationInfoWrapper)
                // oTemp; String sType =
                // bniwNotification.getMBeanNotificationInfo().getDescription();
                m_nfsFilter.enableType("com");
            }
        }
    }

    /**
     * This method unsubscribes from the current event.
     */
    protected void unsubscribe()
    {
        TableItem[] atiTemp = m_tblListNotifications.getSelection();

        if ((atiTemp != null) && (atiTemp.length > 0))
        {
            Object oTemp = atiTemp[0].getData();

            if (oTemp instanceof MBeanNotificationInfoWrapper)
            {
                // MBeanNotificationInfoWrapper bniwNotification = (MBeanNotificationInfoWrapper)
                // oTemp; String sType =
                // bniwNotification.getMBeanNotificationInfo().getDescription();
                m_nfsFilter.disableAllTypes();
            }
        }
    }

    /**
     * This class receives the notifications from the JMX layer.
     *
     * @author  pgussow
     */
    public class JMXNotificationListener
        implements NotificationListener
    {
        /**
         * Holds the display to use.
         */
        private Display m_dDisplay;

        /**
         * Constructor.
         *
         * @param  dDisplay  The display to use.
         */
        public JMXNotificationListener(Display dDisplay)
        {
            m_dDisplay = dDisplay;
        }

        /**
         * This method gets called when a notification is received.
         *
         * @param  nNotification  The actual notification.
         * @param  oHandback      The handback object.
         */
        public void handleNotification(Notification nNotification, Object oHandback)
        {
            final String sType = nNotification.getType();
            final String sMessage = nNotification.getMessage();
            final String sSequence = String.valueOf(nNotification.getSequenceNumber());
            Date dTimestamp = new Date(nNotification.getTimeStamp());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'hh:mm:ss");
            final String sTimestamp = sdf.format(dTimestamp);

            m_dDisplay.asyncExec(new Runnable()
                {
                    public void run()
                    {
                        String[] saDetails = new String[]
                                             {
                                                 sTimestamp, sSequence, sMessage, sType
                                             };

                        TableItem tiNew = new TableItem(m_tblNotifications, SWT.NONE);
                        tiNew.setText(saDetails);
                    }
                });
        }
    }

    /**
     * The content provider for the notification list viewer.
     *
     * @author  pgussow
     */
    class ContentProvider
        implements IStructuredContentProvider
    {
        /**
         * DOCUMENTME.
         */
        private MBeanNotificationInfoWrapper[] m_aNotifications;

        /**
         * DOCUMENTME.
         */
        public void dispose()
        {
        }

        /**
         * This method returns the objects to be displayed.
         *
         * @param   inputElement  The input element.
         *
         * @return  the list of objects to show.
         */
        public Object[] getElements(Object inputElement)
        {
            if (m_aNotifications != null)
            {
                return m_aNotifications;
            }
            else
            {
                return new Object[0];
            }
        }

        /**
         * DOCUMENTME.
         *
         * @param  viewer    DOCUMENTME
         * @param  oldInput  DOCUMENTME
         * @param  newInput  DOCUMENTME
         */
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
        {
            if (newInput instanceof MBeanInfoWrapper)
            {
                MBeanInfoWrapper biwInfo = (MBeanInfoWrapper) newInput;
                m_aNotifications = biwInfo.getMBeanNotificationInfoWrappers();
            }
        }
    }

    /**
     * This class provides the labels for the notifications.
     *
     * @author  pgussow
     */
    class TableLabelProvider extends LabelProvider
        implements ITableLabelProvider
    {
        /**
         * This method returns the image for this colum.
         *
         * @param   element      The element to display.
         * @param   columnIndex  The column index.
         *
         * @return  Always null.
         */
        public Image getColumnImage(Object element, int columnIndex)
        {
            return null;
        }

        /**
         * This method determines what to display in which column.
         *
         * @param   element      The element to show.
         * @param   columnIndex  The column index.
         *
         * @return  The string to display for the specified column.
         */
        public String getColumnText(Object element, int columnIndex)
        {
            String sReturn = "";

            if (element instanceof MBeanNotificationInfoWrapper)
            {
                MBeanNotificationInfoWrapper bniwNotification = (MBeanNotificationInfoWrapper)
                                                                    element;

                switch (columnIndex)
                {
                    case 0:
                        sReturn = bniwNotification.getMBeanNotificationInfo().getDescription();
                        break;

                    case 1:
                        sReturn = bniwNotification.getMBeanNotificationInfo().getName();
                        break;

                    case 2:
                        sReturn = MBeanUtils.prettySignature(bniwNotification
                                                             .getMBeanNotificationInfo());
                        break;
                }
            }
            return sReturn;
        }
    }
}
