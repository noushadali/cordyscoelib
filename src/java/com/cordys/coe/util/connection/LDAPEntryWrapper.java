package com.cordys.coe.util.connection;

import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPEntry;

import org.w3c.dom.Element;

/**
 * Wrapper around the LDAP entry to preserve the old-structure.
 *
 * @author  pgussow
 */
public class LDAPEntryWrapper extends LDAPEntry
{
    /**
     * Holds the original XML of the entry (for the tuple/old/new.
     */
    private Element m_eOriginal;

    /**
     * Creates a new LDAPEntryWrapper object.
     */
    public LDAPEntryWrapper()
    {
        super();
    }

    /**
     * Creates a new LDAPEntryWrapper object.
     *
     * @param  sDN  The DN.
     */
    public LDAPEntryWrapper(String sDN)
    {
        super(sDN);
    }

    /**
     * Creates a new LDAPEntryWrapper object.
     *
     * @param  sDN            The DN.
     * @param  lasAttributes  The aatribute set.
     */
    public LDAPEntryWrapper(String sDN, LDAPAttributeSet lasAttributes)
    {
        super(sDN, lasAttributes);
    }

    /**
     * This method gets the original XML node..
     *
     * @return  The original XML node..
     */
    public Element getOriginalXML()
    {
        return m_eOriginal;
    }

    /**
     * This method sets the original XML node..
     *
     * @param  eOriginal  The original XML node..
     */
    public void setOriginalXML(Element eOriginal)
    {
        m_eOriginal = eOriginal;
    }
}
