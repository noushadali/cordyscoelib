package com.cordys.coe.util.i18n;

import com.eibus.localization.IStringResource;

import java.util.Locale;

/**
 * This class is an implementation of the actual message. It takes a CoEMessageSet and an ID as
 * constructor parameters.
 *
 * @author  pgussow
 */
public final class CoEMessage
    implements IStringResource
{
    /**
     * Holds the message set to which this message belongs.
     */
    private final CoEMessageSet m_cmsMessageSet;
    /**
     * Holds the ID of the actual message.
     */
    private final String m_sMessageID;

    /**
     * Constructor. Creates the message based on the given set and id.
     *
     * @param  cmsMessageSet  The parent message set.
     * @param  sMessageID     The ID of the message.
     */
    public CoEMessage(CoEMessageSet cmsMessageSet, String sMessageID)
    {
        m_cmsMessageSet = cmsMessageSet;
        m_sMessageID = sMessageID;
    }

    /**
     * Returns the set of locales for which this localizable string is available.
     *
     * @return  the set of locales for which this localizable string is available.
     *
     * @see     com.eibus.localization.ILocalizableString#getAvailableLocales()
     */
    public Locale[] getAvailableLocales()
    {
        return m_cmsMessageSet.getAvailableLocales();
    }

    /**
     * This method returns the fully qualified ID for this message (uniquely identifiying it).
     *
     * @return  The fully qualified ID for this message (uniquely identifiying it).
     *
     * @see     com.eibus.localization.IStringResource#getFullyQualifiedResourceID()
     */
    public String getFullyQualifiedResourceID()
    {
        return getResourceContext() + ":" + getResourceID();
    }

    /**
     * Returns the message text for the given locale.
     *
     * @param   lLocale  the locale for which the message text is to be returned.
     *
     * @return  the message text for the given locale
     *
     * @see     com.eibus.localization.ILocalizableString#getMessage(java.util.Locale)
     */
    public String getMessage(Locale lLocale)
    {
        return m_cmsMessageSet.getMessageText(lLocale, m_sMessageID);
    }

    /**
     * This method returns the resource context (which is basically the Javapackage name.
     *
     * @return  The resource context (which is basically the Javapackage name.
     *
     * @see     com.eibus.localization.IStringResource#getResourceContext()
     */
    public String getResourceContext()
    {
        return m_cmsMessageSet.getMessageContext();
    }

    /**
     * This method returns the resource ID for this String resource.
     *
     * @return  The resource ID for this String resource.
     *
     * @see     com.eibus.localization.IStringResource#getResourceID()
     */
    public String getResourceID()
    {
        return m_sMessageID;
    }

    /**
     * This method returns the string representation of the object.
     *
     * @return  The string representation of the object.
     *
     * @see     java.lang.Object#toString()
     */
    @Override public String toString()
    {
        StringBuilder sbReturn = new StringBuilder();

        sbReturn.append(getFullyQualifiedResourceID());

        return sbReturn.toString();
    }
}
