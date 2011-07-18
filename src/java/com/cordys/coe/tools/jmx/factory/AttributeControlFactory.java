/**
 * Eclipse JMX Console
 * Copyright (C) 2007 Jeff Mesnil
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cordys.coe.tools.jmx.factory;

import com.cordys.coe.tools.jmx.IWritableAttributeHandler;
import com.cordys.coe.tools.jmx.MBeanUtils;
import com.cordys.coe.tools.jmx.ShowJMXDataDialog;
import com.cordys.coe.tools.jmx.StringUtils;
import com.cordys.coe.util.swt.MessageBoxUtil;

import java.lang.reflect.Array;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * DOCUMENTME.
 *
 * @author  $author$
 */
public class AttributeControlFactory
{
    /**
     * Holds the double click for displaying the JMX details of the data.
     */
    private static MouseAdapter s_tableDoubleClick = new MouseAdapter()
    {
        /**
         * @see  org.eclipse.swt.events.MouseAdapter#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
         */
        @Override public void mouseDoubleClick(MouseEvent e)
        {
            Table table = (Table) e.widget;
            TableItem[] items = table.getSelection();

            if ((items != null) && (items.length > 0))
            {
                Object data = items[0].getData();

                // Now we need to create the popup that will display the data.
                ShowJMXDataDialog sjdd = new ShowJMXDataDialog(table.getShell(), data);
                sjdd.open();
            }
        }
    };

    /**
     * DOCUMENTME.
     *
     * @param   parent  DOCUMENTME
     * @param   value   DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    public static Control createControl(final Composite parent, final Object value)
    {
        return createControl(parent, false, value.getClass().getSimpleName(), value, null);
    }

    /**
     * DOCUMENTME.
     *
     * @param   parent    DOCUMENTME
     * @param   writable  DOCUMENTME
     * @param   type      DOCUMENTME
     * @param   value     DOCUMENTME
     * @param   handler   DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    @SuppressWarnings("rawtypes")
    public static Control createControl(final Composite parent, final boolean writable, final String type,
                                        final Object value, final IWritableAttributeHandler handler)
    {
        if ((value != null) && (value instanceof Boolean))
        {
            return createBooleanControl(parent, writable, value, handler);
        }

        if ((value != null) && value.getClass().isArray())
        {
            final Table table = createTable(parent, false, true);
            fillArray(table, value);

            // Add the doubleclick so that a popup can be shown with the details of the data.
            table.addMouseListener(s_tableDoubleClick);

            return table;
        }

        if ((value != null) && (value instanceof CompositeData))
        {
            final Table table = createTable(parent, true, true);
            fillCompositeData(table, (CompositeData) value);

            return table;
        }

        if ((value != null) && (value instanceof TabularData))
        {
            final Table table = createTable(parent, true, true);
            fillTabularData(table, (TabularData) value);
            return table;
        }

        if ((value != null) && (value instanceof Collection))
        {
            final Table table = createTable(parent, false, true);
            fillCollection(table, (Collection) value);
            return table;
        }

        if ((value != null) && (value instanceof Map))
        {
            final Table table = createTable(parent, true, true);
            fillMap(table, (Map) value);
            return table;
        }
        return createText(parent, writable, type, value, handler);
    }

    /**
     * DOCUMENTME.
     *
     * @param   parent    DOCUMENTME
     * @param   writable  DOCUMENTME
     * @param   value     DOCUMENTME
     * @param   handler   DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    private static Control createBooleanControl(final Composite parent, boolean writable, Object value,
                                                final IWritableAttributeHandler handler)
    {
        boolean booleanValue = ((Boolean) value).booleanValue();

        if (!writable)
        {
            Text text = new Text(parent, SWT.SINGLE);
            text.setText(Boolean.toString(booleanValue));
            return text;
        }

        final Combo combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);

        combo.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_BLUE));
        combo.setItems(new String[] { Boolean.TRUE.toString(), Boolean.FALSE.toString() });

        if (booleanValue)
        {
            combo.select(0);
        }
        else
        {
            combo.select(1);
        }

        if (handler != null)
        {
            combo.addListener(SWT.Selection, new Listener()
                              {
                                  public void handleEvent(Event event)
                                  {
                                      Boolean newValue = Boolean.valueOf(combo.getText());
                                      handler.write(newValue);
                                  }
                              });
        }
        return combo;
    }

    /**
     * This method creates a new table which will hold the data.
     *
     * @param   parent         DOCUMENTME
     * @param   visibleHeader  DOCUMENTME
     * @param   visibleLines   DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    private static Table createTable(Composite parent, boolean visibleHeader, boolean visibleLines)
    {
        int style = SWT.SINGLE | SWT.FULL_SELECTION;
        Table table = null;

        style |= SWT.BORDER;
        table = new Table(parent, style);

        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 20;
        gd.widthHint = 100;
        table.setLayoutData(gd);
        table.setHeaderVisible(visibleHeader);
        table.setLinesVisible(visibleLines);

        table.addMouseListener(s_tableDoubleClick);

        return table;
    }

    /**
     * DOCUMENTME.
     *
     * @param   parent    DOCUMENTME
     * @param   writable  DOCUMENTME
     * @param   type      DOCUMENTME
     * @param   value     DOCUMENTME
     * @param   handler   DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    private static Control createText(final Composite parent, final boolean writable, final String type,
                                      final Object value, final IWritableAttributeHandler handler)
    {
        String attrValue = ""; // $NON-NLS-1$

        try
        {
            attrValue = StringUtils.toString(value, true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            attrValue = "Unavailable";
        }

        int style = SWT.BORDER;

        // fixed issue #12
        if ((value instanceof Number) || (value instanceof Character))
        {
            style |= SWT.SINGLE;
        }
        else
        {
            style |= (SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
        }

        if (!writable)
        {
            final Text text = createTextControl(parent, style);
            text.setText(attrValue);
            text.setEditable(false);
            text.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_BLACK));
            return text;
        }
        else
        {
            // interpose a composite to contain both
            // the text control and an "update" button
            Composite composite = new Composite(parent, SWT.NONE);
            composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

            GridLayout layout = new GridLayout(2, false);
            composite.setLayout(layout);

            final Text text = createTextControl(composite, style);
            text.setText(attrValue);
            text.setEditable(true);
            text.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_BLUE));

            if (handler != null)
            {
                Button updateButton = new Button(composite, SWT.PUSH);
                updateButton.setText("Update");

                updateButton.setLayoutData(new GridData(SWT.END, SWT.TOP, false, false));
                updateButton.addSelectionListener(new SelectionListener()
                    {
                        public void widgetDefaultSelected(SelectionEvent event)
                        {
                            try
                            {
                                Object newValue = MBeanUtils.getValue(text.getText(), type);
                                handler.write(newValue);
                                text.setText(newValue.toString());
                            }
                            catch (Throwable t)
                            {
                                MessageBoxUtil.showError(parent.getShell(), "Error setting the new value", t);
                            }
                        }

                        public void widgetSelected(SelectionEvent event)
                        {
                            widgetDefaultSelected(event);
                        }
                    });
            }
            return text;
        }
    }

    /**
     * DOCUMENTME.
     *
     * @param   parent  DOCUMENTME
     * @param   style   DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    private static Text createTextControl(final Composite parent, int style)
    {
        final Text text;

        text = new Text(parent, style); // $NON-NLS-1$
        return text;
    }

    /**
     * DOCUMENTME.
     *
     * @param  table     DOCUMENTME
     * @param  arrayObj  DOCUMENTME
     */
    private static void fillArray(Table table, Object arrayObj)
    {
        TableColumn columnName = new TableColumn(table, SWT.NONE);
        columnName.setText("Name");
        columnName.setWidth(400);
        fillArrayItems(table, arrayObj);
    }

    /**
     * DOCUMENTME.
     *
     * @param  table     DOCUMENTME
     * @param  arrayObj  DOCUMENTME
     */
    private static void fillArrayItems(Table table, Object arrayObj)
    {
        int length = Array.getLength(arrayObj);

        for (int i = 0; i < length; i++)
        {
            Object element = Array.get(arrayObj, i);
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(StringUtils.toString(element, false));
            item.setData(element);
        }
    }

    /**
     * DOCUMENTME.
     *
     * @param  table       DOCUMENTME
     * @param  collection  DOCUMENTME
     */
    @SuppressWarnings({ "rawtypes" }) // $NON-NLS-1$
    private static void fillCollection(final Table table, Collection collection)
    {
        TableColumn columnName = new TableColumn(table, SWT.NONE);
        columnName.setText("Name");
        columnName.setWidth(400);

        Iterator iter = collection.iterator();

        while (iter.hasNext())
        {
            Object element = (Object) iter.next();
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(StringUtils.toString(element, false));
        }
    }

    /**
     * DOCUMENTME.
     *
     * @param  table  DOCUMENTME
     * @param  data   DOCUMENTME
     */
    private static void fillCompositeData(final Table table, CompositeData data)
    {
        TableColumn keyColumn = new TableColumn(table, SWT.NONE);
        keyColumn.setText("Key");
        keyColumn.setWidth(150);

        TableColumn valueColumn = new TableColumn(table, SWT.NONE);
        valueColumn.setText("Value");
        valueColumn.setWidth(250);

        Iterator<?> iter = data.getCompositeType().keySet().iterator();

        while (iter.hasNext())
        {
            String key = (String) iter.next();
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(0, key);
            item.setText(1, StringUtils.toString(data.get(key), false));
            item.setData(data.get(key));
        }
    }

    /**
     * DOCUMENTME.
     *
     * @param  table  DOCUMENTME
     * @param  map    DOCUMENTME
     */
    @SuppressWarnings("rawtypes")
    private static void fillMap(final Table table, Map map)
    {
        TableColumn keyColumn = new TableColumn(table, SWT.NONE);
        keyColumn.setText("Key");
        keyColumn.setWidth(150);

        TableColumn valueColumn = new TableColumn(table, SWT.NONE);
        valueColumn.setText("Value");
        valueColumn.setWidth(250);

        Iterator<?> iter = map.entrySet().iterator();

        while (iter.hasNext())
        {
            Map.Entry entry = (Map.Entry) iter.next();
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(0, StringUtils.toString(entry.getKey(), false));
            item.setText(1, StringUtils.toString(entry.getValue(), false));
        }
    }

    /**
     * DOCUMENTME.
     *
     * @param  table  DOCUMENTME
     * @param  data   DOCUMENTME
     */
    @SuppressWarnings("rawtypes")
    private static void fillTabularData(final Table table, TabularData data)
    {
        Set keySet = data.getTabularType().getRowType().keySet();
        Iterator keyIter = keySet.iterator();

        while (keyIter.hasNext())
        {
            String key = (String) keyIter.next();
            TableColumn column = new TableColumn(table, SWT.LEFT);
            column.setText(key);
            column.setWidth(150);
            column.setMoveable(true);
            column.setResizable(true);
        }

        Iterator valueIter = data.values().iterator();

        while (valueIter.hasNext())
        {
            CompositeData rowData = (CompositeData) valueIter.next();
            TableItem item = new TableItem(table, SWT.NONE);
            keyIter = keySet.iterator();

            int i = 0;

            while (keyIter.hasNext())
            {
                String key = (String) keyIter.next();
                item.setText(i, StringUtils.toString(rowData.get(key), false));
                item.setData(rowData.get(key));
                i++;
            }
        }
    }
}
