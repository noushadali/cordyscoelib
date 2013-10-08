package com.cordys.coe.tools.snapshot;

import com.cordys.coe.util.swing.MessageBoxUtil;

import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.lang.reflect.Method;

import javax.swing.JComboBox;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import javax.swing.text.JTextComponent;

/**
 * This class is used to bind the input to a certain setter of an object.
 */
final class BoundInputListener
    implements InputMethodListener, ItemListener
{
    /**
     * Holds the configuration detail panel so the dirty flag can be set.
     */
    private ConfigurationDetails m_configurationDetails;
    /**
     * Holds the wrapped text component.
     */
    private JTextComponent m_component;
    /**
     * Holds the bean to apply the setter to.
     */
    private Object m_bean;
    /**
     * Holds the name of the setter to call.
     */
    private String m_property;
    /**
     * Holds the type for the setter.
     */
    private Class<?> m_parameter;
    /**
     * Holds the actual method that should be invoked.
     */
    private Method m_setterMethod;
    /**
     * Holds the getter method that should be invoked.
     */
    private Method m_getterMethod;
    /**
     * Holds teh combobox this listener works on.
     */
    private JComboBox<?> m_combo;

    /**
     * Instantiates a new bound input listener.
     *
     * @param  configurationDetails  The configuration detail panel so the dirty flag can be set.
     * @param  component             The component to bind the bean to.
     * @param  bean                  The actual bean to set the changed value to.
     * @param  setter                The name of the setter to call.
     */
    public BoundInputListener(ConfigurationDetails configurationDetails, JTextComponent component, Object bean,
                              String setter)
    {
        this(configurationDetails, component, bean, setter, String.class);
    }

    /**
     * Instantiates a new bound input listener.
     *
     * @param  configurationDetails  The configuration detail panel so the dirty flag can be set.
     * @param  component             The component to bind the bean to.
     * @param  bean                  The actual bean to set the changed value to.
     * @param  property              The name of the setter to call.
     * @param  parameter             The type of the parameter for the setter.
     */
    public BoundInputListener(ConfigurationDetails configurationDetails, JTextComponent component, Object bean,
                              String property, Class<?> parameter)
    {
        m_configurationDetails = configurationDetails;
        m_component = component;
        m_bean = bean;
        m_property = property;
        m_parameter = parameter;

        // Find the method.
        try
        {
            m_setterMethod = bean.getClass().getMethod("set" + property, parameter);
            m_getterMethod = bean.getClass().getMethod("get" + property);
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Could not find method " + property, e);
        }

        m_component.getDocument().addDocumentListener(new DocumentListener()
            {
                @Override public void removeUpdate(DocumentEvent e)
                {
                    inputMethodTextChanged(null);
                }

                @Override public void insertUpdate(DocumentEvent e)
                {
                    inputMethodTextChanged(null);
                }

                @Override public void changedUpdate(DocumentEvent e)
                {
                    inputMethodTextChanged(null);
                }
            });
    }

    /**
     * Instantiates a new bound input listener.
     *
     * @param  configurationDetails  The configuration detail panel so the dirty flag can be set.
     * @param  component             The component to bind the bean to.
     * @param  bean                  The actual bean to set the changed value to.
     * @param  property              The name of the setter to call.
     * @param  parameter             The type of the parameter for the setter.
     */
    public BoundInputListener(ConfigurationDetails configurationDetails, JComboBox<?> component, Object bean,
                              String property, Class<?> parameter)
    {
        m_configurationDetails = configurationDetails;
        m_combo = component;
        m_bean = bean;
        m_property = property;
        m_parameter = parameter;

        // Find the method.
        try
        {
            m_setterMethod = bean.getClass().getMethod("set" + property, parameter);
            m_getterMethod = bean.getClass().getMethod("get" + property);
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Could not find method " + property, e);
        }
    }

    /**
     * @see  java.awt.event.InputMethodListener#caretPositionChanged(java.awt.event.InputMethodEvent)
     */
    public void caretPositionChanged(InputMethodEvent event)
    {
    }

    /**
     * @see  java.awt.event.InputMethodListener#inputMethodTextChanged(java.awt.event.InputMethodEvent)
     */
    public void inputMethodTextChanged(InputMethodEvent event)
    {
        doProperValue(m_component.getText());
    }

    /**
     * @see  java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
     */
    @Override public void itemStateChanged(ItemEvent e)
    {
        doProperValue(m_combo.getSelectedItem());
    }

    /**
     * Set the proper value in the bean.
     *
     * @param  tmp  The real value.
     */
    private void doProperValue(Object tmp)
    {
        if ((m_bean != null) && (tmp != null))
        {
            try
            {
                Object value = tmp;
                Object currentValue = m_getterMethod.invoke(m_bean);

                if (m_parameter == Integer.class)
                {
                    value = Integer.parseInt(tmp.toString());
                }
                else if (m_parameter == Long.class)
                {
                    value = Long.parseLong(tmp.toString());
                }

                if (!value.equals(currentValue))
                {
                    m_setterMethod.invoke(m_bean, value);
                    m_configurationDetails.setDirty();
                }
            }
            catch (Exception e)
            {
                MessageBoxUtil.showError("Error calling the method " + m_property + " on object " + m_bean, e);
            }
        }
    }
}
