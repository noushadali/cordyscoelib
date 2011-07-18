/**
 * Eclipse JMX Console
 * Copyright (C) 2006 Jeff Mesnil
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
 *
 *  Code was inspired from org.eclipse.equinox.client source, (c) 2006 IBM
 */
package com.cordys.coe.tools.jmx.tables;

import com.cordys.coe.tools.jmx.IUpdateAttributeDetails;
import com.cordys.coe.tools.jmx.resources.MBeanAttributeInfoWrapper;
import com.cordys.coe.tools.jmx.resources.MBeanInfoWrapper;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * DOCUMENTME.
 *
 * @author  $author$
 */
public class MBeanAttributesTable
{
    /**
     * The viewer to show.
     */
    private TableViewer viewer;
    /**
     * Holds the view that should be updated with the details for the attribute.
     */
    private IUpdateAttributeDetails m_uadDetailsView;

    /**
     * Creates a new MBeanAttributesTable object.
     *
     * @param  parent          DOCUMENTME
     * @param  uadDetailsView  DOCUMENTME
     */
    public MBeanAttributesTable(Composite parent, IUpdateAttributeDetails uadDetailsView)
    {
        m_uadDetailsView = uadDetailsView;
        viewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);

        final Table attrTable = viewer.getTable();
        attrTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createColumns(attrTable);
        attrTable.setLinesVisible(true);
        attrTable.setHeaderVisible(true);

        viewer.setContentProvider(new AttributesContentProvider());
        viewer.setLabelProvider(new AttributesLabelProvider());
        viewer.addSelectionChangedListener(new ISelectionChangedListener()
            {
                public void selectionChanged(SelectionChangedEvent event)
                {
                    IStructuredSelection ssSelection = (IStructuredSelection) event.getSelection();
                    Object oTemp = ssSelection.getFirstElement();

                    if (oTemp != null)
                    {
                        m_uadDetailsView.updateDetails((MBeanAttributeInfoWrapper) oTemp);
                    }
                }
            });
    }

    /**
     * DOCUMENTME.
     *
     * @return  DOCUMENTME
     */
    public Viewer getViewer()
    {
        return viewer;
    }

    /**
     * DOCUMENTME.
     *
     * @param  input  DOCUMENTME
     */
    public void setInput(MBeanInfoWrapper input)
    {
        if ((input == null) || (input.getMBeanInfo() == null))
        {
            viewer.setInput(null);
        }
        else
        {
            viewer.setInput(input.getMBeanAttributeInfoWrappers());
        }
        viewer.getTable().redraw();
    }

    /**
     * DOCUMENTME.
     *
     * @param  attrTable  DOCUMENTME
     */
    private void createColumns(final Table attrTable)
    {
        final TableColumn attrName = new TableColumn(attrTable, SWT.NONE);
        attrName.setText("Name");
        attrName.setWidth(150);

        final TableColumn attrValue = new TableColumn(attrTable, SWT.NONE);
        attrValue.setText("Value");
        attrValue.setWidth(350);

        Listener sortListener = new Listener()
        {
            public void handleEvent(Event e)
            {
                // determine new sort column and direction
                TableColumn sortColumn = attrTable.getSortColumn();
                TableColumn currentColumn = (TableColumn) e.widget;

                int dir = attrTable.getSortDirection();

                if (sortColumn == currentColumn)
                {
                    dir = (dir == SWT.UP) ? SWT.DOWN : SWT.UP;
                }
                else
                {
                    attrTable.setSortColumn(currentColumn);
                    dir = SWT.UP;
                }

                int colIndex;

                if (currentColumn == attrName)
                {
                    colIndex = 0;
                }
                else if (currentColumn == attrValue)
                {
                    colIndex = 1;
                }
                else
                {
                    return;
                }

                // sort the data based on column and direction
                attrTable.setSortDirection(dir);
                viewer.setSorter(new AttributesViewerSorter(dir, colIndex));
            }
        };
        attrName.addListener(SWT.Selection, sortListener);
        attrTable.setSortColumn(attrName);
        attrTable.setSortDirection(SWT.UP);
    }
}
