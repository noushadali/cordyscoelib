package com.cordys.coe.tools.useradmin.swt;

import com.eibus.connector.nom.Connector;

import java.io.Serializable;

/**
 * This class wraps around a XML store item.
 *
 * @author  pgussow
 */
public class XMLStoreItem
    implements Serializable
{
    /**
     * Identifies the ISV level.
     */
    public static final int LEVEL_ISV = 0;
    /**
     * Identifies the organization level,.
     */
    public static final int LEVEL_ORGANIZATION = 1;
    /**
     * Identifies the user level.
     */
    public static final int LEVEL_USER = 2;
    /**
     * Identifies a menu.
     */
    public static final int TYPE_MENU = 0;
    /**
     * Identifies a toolbar.
     */
    public static final int TYPE_TOOLBAR = 1;
    /**
     * Identifies a XForm.
     */
    public static final int TYPE_XFORM = 2;
    /**
     * Identifies an application connector.
     */
    public static final int TYPE_APPLICATION_CONNECTOR = 3;
    /**
     * Indicates whether or not the item is a folder or an actual entry.
     */
    private boolean bFolder;
    /**
     * Holds the content of the file.
     */
    private int iContent;
    /**
     * Holds the level of the file (user, org or isv).
     */
    private int iLevel;
    /**
     * Holds the type of XML store item.
     */
    private int iType;
    /**
     * Holds the string to display.
     */
    private String sDisplay;
    /**
     * Holds the key for the object.
     */
    private String sKey;
    /**
     * Holds the organization in which the file resides.
     */
    private String sOrganization;

    /**
     * Creates a new XMLStoreItem object.
     *
     * @param  sKey  The key of the object.
     */
    public XMLStoreItem(String sKey)
    {
        this.sKey = sKey;
        sDisplay = sKey.substring(sKey.lastIndexOf("/") + 1);
        determineType();
    }

    /**
     * This method gets the content of the file.
     *
     * @return  The content of the file.
     */
    public int getContent()
    {
        return iContent;
    }

    /**
     * This method gets the display label for this item.
     *
     * @return  The display label for this item.
     */
    public String getDisplay()
    {
        return sDisplay;
    }

    /**
     * This method gets the key of this XMLStore item.
     *
     * @return  The key of this XMLStore item.
     */
    public String getKey()
    {
        return sKey;
    }

    /**
     * This method gets the level of this entry.
     *
     * @return  The level of this entry.
     */
    public int getLevel()
    {
        return iLevel;
    }

    /**
     * This method returns the description for the level.
     *
     * @return  the description for the level.
     */
    public String getLevelDesc()
    {
        String sReturn = "";
        int iLevel = getLevel();

        if (iLevel == XMLStoreItem.LEVEL_ISV)
        {
            sReturn = "isv";
        }
        else if (iLevel == XMLStoreItem.LEVEL_ORGANIZATION)
        {
            sReturn = "organization";
        }
        else if (iLevel == XMLStoreItem.LEVEL_USER)
        {
            sReturn = "user";
        }

        return sReturn;
    }

    /**
     * This method gets the organization..
     *
     * @return  The organization..
     */
    public String getOrganization()
    {
        return sOrganization;
    }

    /**
     * This method gets the type of XML store item.
     *
     * @return  The type of XML store item.
     */
    public int getType()
    {
        return iType;
    }

    /**
     * This method gets whether or not the entry is a folder.
     *
     * @return  Whether or not the entry is a folder.
     */
    public boolean isFolder()
    {
        return bFolder;
    }

    /**
     * This method reads the content of the file using the passed on connector.
     *
     * @param  cCon  The connector to use.
     */
    public void readContent(Connector cCon)
    {
    }

    /**
     * This method sets the display label for this item.
     *
     * @param  sDisplay  The new display label.
     */
    public void setDisplay(String sDisplay)
    {
        this.sDisplay = sDisplay;
    }

    /**
     * This method sets wether or not the entry is a folder.
     *
     * @param  bFolder  Whether or not the entry is a folder.
     */
    public void setFolder(boolean bFolder)
    {
        this.bFolder = bFolder;
    }

    /**
     * this method sets the level for this object.
     *
     * @param  iLevel  The new level.
     */
    public void setLevel(int iLevel)
    {
        this.iLevel = iLevel;
    }

    /**
     * This method sets the new level.
     *
     * @param  sLevel  The new level.
     */
    public void setLevel(String sLevel)
    {
        if (sLevel.equals("isv"))
        {
            iLevel = LEVEL_ISV;
        }
        else if (sLevel.equals("organization"))
        {
            iLevel = LEVEL_ORGANIZATION;
        }
        else if (sLevel.equals("user"))
        {
            iLevel = LEVEL_USER;
        }
    }

    /**
     * This method sets the organization..
     *
     * @param  sOrganization  The organization..
     */
    public void setOrganization(String sOrganization)
    {
        this.sOrganization = sOrganization;
    }

    /**
     * This method sets the type of XML store item.
     *
     * @param  iType  The type of XML store item.
     */
    public void setType(int iType)
    {
        this.iType = iType;
    }

    /**
     * Returns the string representation of the object.
     *
     * @return  The string representation of the object.
     */
    @Override public String toString()
    {
        return sDisplay;
    }

    /**
     * This method determines the type for this node.
     */
    private void determineType()
    {
        String sKey = getKey();

        if (sKey.startsWith("/Cordys/WCP/Menu") == true)
        {
            setType(TYPE_MENU);
        }
        else if (sKey.startsWith("/Cordys/WCP/Toolbar") == true)
        {
            setType(TYPE_TOOLBAR);
        }
        else if (sKey.startsWith("/Cordys/WCP/XForms/runtime") == true)
        {
            setType(TYPE_XFORM);
        }
        else if (sKey.startsWith("/Cordys/WCP/Application Connector") == true)
        {
            setType(TYPE_APPLICATION_CONNECTOR);
        }
    }
}
