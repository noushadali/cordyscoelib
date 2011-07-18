/**
 * © 2003 Cordys R&D B.V. All rights reserved.     The computer program(s) is
 * the proprietary information of Cordys R&D B.V.     and provided under the
 * relevant License Agreement containing restrictions     on use and
 * disclosure. Use is subject to the License Agreement.
 */
package com.cordys.coe.util.general.oledb;

import com.cordys.coe.util.xml.dom.EmptyPrefixResolver;
import com.cordys.coe.util.xml.dom.XMLHelper;
import com.cordys.coe.util.xml.dom.XPathHelper;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Find;
import com.eibus.xml.nom.Node;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * JavaObject for navigating trough a cursor. It only works for OLEDB-compatible cursors.
 */
public class CursorNavigation
{
    /**
     * Indicates whether or not the end of the resultset was reached.
     */
    private boolean m_bEndReached;
    /**
     * Indicates whether or not the first set has been retrieved.
     */
    private boolean m_bFirstSet;
    /**
     * Indicates whether or not the recordcount is known upfront.
     */
    private boolean m_bKnownRecordCount;
    /**
     * Indicates whether or not the last set has been retrieved.
     */
    private boolean m_bLastSet;
    /**
     * Holds whether or not the same connection should be used.
     */
    private boolean m_bSameConnection = false;
    /**
     * Holds the number of rows to retrieve.
     */
    private int m_iCursorSize;
    /**
     * Holds the known recordcount of the resultset.
     */
    private int m_iKnownRecordCount;
    /**
     * Holds the maximum number of rows.
     */
    private int m_iMaxRows;
    /**
     * Holds the original cursorsize.
     */
    private int m_iOrigCursorSize;
    /**
     * Holds the current position of the cursor.
     */
    private int m_iPosition;
    /**
     * Holds the ID of the connection.
     */
    private String m_sConnectionID;
    /**
     * Holds the name of the method.
     */
    private String m_sMethodName;
    /**
     * Holds the name of the cursor.
     */
    private String m_sName;
    /**
     * Holds the namespace for the method.
     */
    private String m_sNamespace;

    /**
     * Constructor. Initializes the cursor-object.
     *
     * @param  iCursorSize  The number of rows to retrieve at once.
     * @param  sMethodName  The name of the method being called.
     */
    public CursorNavigation(int iCursorSize, String sMethodName)
    {
        // By default we're not working with a known recordcount.
        this(iCursorSize, sMethodName, null, -1);
    }

    /**
     * Constructor. Initializes the cursor-object.
     *
     * @param  iCursorSize  The number of rows to retrieve at once.
     * @param  sMethodName  The name of the method being called.
     * @param  sNamespace   DOCUMENTME
     */
    public CursorNavigation(int iCursorSize, String sMethodName, String sNamespace)
    {
        // By default we're not working with a known recordcount.
        this(iCursorSize, sMethodName, sNamespace, -1);
    }

    /**
     * Constructor. Initializes the cursor-object.
     *
     * @param  iCursorSize        The number of rows to retrieve at once.
     * @param  sMethodName        The name of the method being called.
     * @param  iKnownRecordCount  The known recordcount.
     */
    public CursorNavigation(int iCursorSize, String sMethodName, int iKnownRecordCount)
    {
        this(iCursorSize, sMethodName, null, iKnownRecordCount);
    }

    /**
     * Constructor. Initializes the cursor-object.
     *
     * @param  iCursorSize        The number of rows to retrieve at once.
     * @param  sMethodName        The name of the method being called.
     * @param  sNamespace         DOCUMENTME
     * @param  iKnownRecordCount  The known recordcount.
     */
    public CursorNavigation(int iCursorSize, String sMethodName, String sNamespace,
                            int iKnownRecordCount)
    {
        m_iCursorSize = iCursorSize;
        m_iOrigCursorSize = iCursorSize;
        m_sMethodName = sMethodName;
        m_sNamespace = sNamespace;
        m_iPosition = -1;
        m_sName = null;
        m_iMaxRows = -1;
        m_bLastSet = false;
        m_bFirstSet = true;
        m_bEndReached = false;
        m_iKnownRecordCount = iKnownRecordCount;

        if (iKnownRecordCount == -1)
        {
            m_bKnownRecordCount = false;
        }

        if ((sMethodName != null) && (sMethodName.length() > 0))
        {
            m_sMethodName = sMethodName;
        }
        else
        {
            m_sMethodName = "?";
        }
    }

    /**
     * This method clears the current status of the cursor.
     */
    public void clear()
    {
        m_iPosition = -1;
        m_sName = null;
        m_iCursorSize = m_iOrigCursorSize;
        m_iMaxRows = -1;
        m_bLastSet = false;
        m_bFirstSet = true;
        m_bEndReached = false;
        m_iKnownRecordCount = 0;
    }

    /**
     * This method gets the conenction ID.
     *
     * @return  The conenction ID.
     */
    public String getConnectionID()
    {
        return m_sConnectionID;
    }

    /**
     * Returns the current cursorsize.
     *
     * @return  The current cursorsize.
     */
    public int getCursorSize()
    {
        return m_iCursorSize;
    }

    /**
     * This method returns the cursor-element for the first set of records.
     *
     * @param   dDoc  The XML-document in which the cursor-tag should be created.
     *
     * @return  The cursor-object that represents the first cursor.
     */
    public int getFirstCursor(Document dDoc)
    {
        int iReturn = dDoc.createElement("cursor");

        if (m_iPosition != -1)
        {
            Node.setAttribute(iReturn, "position", "0");
        }

        if (m_iCursorSize != -1)
        {
            Node.setAttribute(iReturn, "position", String.valueOf(m_iCursorSize));
        }

        if (getSameConnection())
        {
            Node.setAttribute(iReturn, "sameConnection", "true");
        }

        return iReturn;
    }

    /**
     * This method returns the cursor-element for the first set of records.
     *
     * @param   eParent  dDoc The XML-document in which the cursor-tag should be created.
     *
     * @return  The cursor-object that represents the first cursor.
     */
    public Element getFirstCursor(Element eParent)
    {
        Element eReturn = XMLHelper.createElementWithParentNS("cursor", eParent);

        if (m_iPosition != -1)
        {
            eReturn.setAttribute("position", "0");
        }

        if (m_iCursorSize != -1)
        {
            eReturn.setAttribute("position", String.valueOf(m_iCursorSize));
        }

        if (getSameConnection())
        {
            eReturn.setAttribute("sameConnection", "true");
        }

        return eReturn;
    }

    /**
     * Returns the known recordcount of the cursor.
     *
     * @return  The known recordcount of the cursor.
     */
    public int getKnownRecordCount()
    {
        return m_iKnownRecordCount;
    }

    /**
     * This method returns the cursor-element for the last set of records. Since we don't know the
     * amount of records that are available the only option we have is using iRecordCount, which
     * contains the number of records we've retrieved so far.
     *
     * @param   dDoc  The XML-document in which the cursor-tag should be created.
     *
     * @return  The cursor-object that represents the last cursor.
     */
    public int getLastCursor(Document dDoc)
    {
        int iReturn = dDoc.createElement("cursor");
        int iNewPos = 0;

        if (m_iPosition != -1)
        {
            iNewPos = m_iKnownRecordCount;

            if (m_iCursorSize > -1)
            {
                iNewPos = iNewPos - m_iOrigCursorSize;
            }
            else
            {
                iNewPos = iNewPos - m_iCursorSize;
            }
            Node.setAttribute(iReturn, "position", String.valueOf(iNewPos));
        }

        if ((m_iPosition != -1) && (iNewPos > m_iPosition))
        {
            if (m_sName != null)
            {
                Node.setAttribute(iReturn, "id", m_sName);
            }
        }

        if (m_iMaxRows != -1)
        {
            Node.setAttribute(iReturn, "maxRows", String.valueOf(m_iMaxRows));
        }

        if (m_iCursorSize != -1)
        {
            Node.setAttribute(iReturn, "numRows", String.valueOf(m_iCursorSize));
        }

        if (getSameConnection())
        {
            Node.setAttribute(iReturn, "sameConnection", "true");
        }

        if ((m_sConnectionID != null) && (m_sConnectionID.length() > 0))
        {
            Node.setAttribute(iReturn, "connId", getConnectionID());
        }

        return iReturn;
    }

    /**
     * This method returns the cursor-element for the last set of records. Since we don't know the
     * amount of records that are available the only option we have is using iRecordCount, which
     * contains the number of records we've retrieved so far.
     *
     * @param   eParent  dDoc The XML-document in which the cursor-tag should be created.
     *
     * @return  The cursor-object that represents the last cursor.
     */
    public Element getLastCursor(Element eParent)
    {
        Element eReturn = XMLHelper.createElementWithParentNS("cursor", eParent);

        int iNewPos = 0;

        if (m_iPosition != -1)
        {
            iNewPos = m_iKnownRecordCount;

            if (m_iCursorSize > -1)
            {
                iNewPos = iNewPos - m_iOrigCursorSize;
            }
            else
            {
                iNewPos = iNewPos - m_iCursorSize;
            }
            eReturn.setAttribute("position", String.valueOf(iNewPos));
        }

        if ((m_iPosition != -1) && (iNewPos > m_iPosition))
        {
            if (m_sName != null)
            {
                eReturn.setAttribute("id", m_sName);
            }
        }

        if (m_iMaxRows != -1)
        {
            eReturn.setAttribute("maxRows", String.valueOf(m_iMaxRows));
        }

        if (m_iCursorSize != -1)
        {
            eReturn.setAttribute("numRows", String.valueOf(m_iCursorSize));
        }

        if (getSameConnection())
        {
            eReturn.setAttribute("sameConnection", "true");
        }

        if ((m_sConnectionID != null) && (m_sConnectionID.length() > 0))
        {
            eReturn.setAttribute("connId", getConnectionID());
        }

        return eReturn;
    }

    /**
     * Returns the maximum number of rows.
     *
     * @return  The maximum number of rows.
     */
    public int getMaxRows()
    {
        return m_iMaxRows;
    }

    /**
     * Returns the name of the method.
     *
     * @return  The name of the method.
     */
    public String getMethodName()
    {
        return m_sMethodName;
    }

    /**
     * Returns the name of the cursor.
     *
     * @return  The name of the cursor.
     */
    public String getName()
    {
        return m_sName;
    }

    /**
     * This method returns the cursor-tag for retrieving the next recordset based on the current
     * status of the cursor.
     *
     * @param   dDoc  The XML-document in which the cursor-tag should be created.
     *
     * @return  The cursor-object that represents the next cursor.
     */
    public int getNextCursor(Document dDoc)
    {
        int iReturn = 0;

        if (m_bKnownRecordCount == false)
        {
            iReturn = dDoc.createElement("cursor");

            if (m_iPosition != -1)
            {
                Node.setAttribute(iReturn, "position", String.valueOf(m_iPosition));
            }

            if (m_sName != null)
            {
                Node.setAttribute(iReturn, "id", m_sName);
            }

            if (m_iMaxRows != -1)
            {
                Node.setAttribute(iReturn, "maxRows", String.valueOf(m_iMaxRows));
            }

            if (m_iCursorSize != -1)
            {
                Node.setAttribute(iReturn, "numRows", String.valueOf(m_iCursorSize));
            }

            if (getSameConnection())
            {
                Node.setAttribute(iReturn, "sameConnection", "true");
            }

            if ((m_sConnectionID != null) && (m_sConnectionID.length() > 0))
            {
                Node.setAttribute(iReturn, "connId", getConnectionID());
            }
        }
        else
        {
            // First check if we're going to be displaying the last record-set, since we know
            // how much records we have. We can determin this by looking at the 'position'
            // If the position is bigger then (TotalRecs - NumRows) then we're basicly
            // displaying the last recordset.
            if ((m_iPosition != -1) && ((m_iKnownRecordCount - m_iCursorSize) <= m_iPosition))
            {
                iReturn = getLastCursor(dDoc);
            }
            else
            {
                iReturn = dDoc.createElement("cursor");

                if (m_iPosition != -1)
                {
                    Node.setAttribute(iReturn, "position", String.valueOf(m_iPosition));
                }

                if (m_sName != null)
                {
                    Node.setAttribute(iReturn, "id", m_sName);
                }

                if (m_iMaxRows != -1)
                {
                    Node.setAttribute(iReturn, "maxRows", String.valueOf(m_iMaxRows));
                }

                if (m_iCursorSize != -1)
                {
                    Node.setAttribute(iReturn, "numRows", String.valueOf(m_iCursorSize));
                }

                if (getSameConnection())
                {
                    Node.setAttribute(iReturn, "sameConnection", "true");
                }

                if ((m_sConnectionID != null) && (m_sConnectionID.length() > 0))
                {
                    Node.setAttribute(iReturn, "connId", getConnectionID());
                }
            }
        }

        return iReturn;
    }

    /**
     * This method returns the cursor-tag for retrieving the next recordset based on the current
     * status of the cursor.
     *
     * @param   eParent  dDoc The XML-document in which the cursor-tag should be created.
     *
     * @return  The cursor-object that represents the next cursor.
     */
    public Element getNextCursor(Element eParent)
    {
        Element eReturn = null;

        if (m_bKnownRecordCount == false)
        {
            eReturn = XMLHelper.createElementWithParentNS("cursor", eParent);

            if (m_iPosition != -1)
            {
                eReturn.setAttribute("position", String.valueOf(m_iPosition));
            }

            if (m_sName != null)
            {
                eReturn.setAttribute("id", m_sName);
            }

            if (m_iMaxRows != -1)
            {
                eReturn.setAttribute("maxRows", String.valueOf(m_iMaxRows));
            }

            if (m_iCursorSize != -1)
            {
                eReturn.setAttribute("numRows", String.valueOf(m_iCursorSize));
            }

            if (getSameConnection())
            {
                eReturn.setAttribute("sameConnection", "true");
            }

            if ((m_sConnectionID != null) && (m_sConnectionID.length() > 0))
            {
                eReturn.setAttribute("connId", getConnectionID());
            }
        }
        else
        {
            // First check if we're going to be displaying the last record-set, since we know
            // how much records we have. We can determin this by looking at the 'position'
            // If the position is bigger then (TotalRecs - NumRows) then we're basicly
            // displaying the last recordset.
            if ((m_iPosition != -1) && ((m_iKnownRecordCount - m_iCursorSize) <= m_iPosition))
            {
                eReturn = getLastCursor(eParent);
            }
            else
            {
                eReturn = XMLHelper.createElementWithParentNS("cursor", eParent);

                if (m_iPosition != -1)
                {
                    eReturn.setAttribute("position", String.valueOf(m_iPosition));
                }

                if (m_sName != null)
                {
                    eReturn.setAttribute("id", m_sName);
                }

                if (m_iMaxRows != -1)
                {
                    eReturn.setAttribute("maxRows", String.valueOf(m_iMaxRows));
                }

                if (m_iCursorSize != -1)
                {
                    eReturn.setAttribute("numRows", String.valueOf(m_iCursorSize));
                }

                if (getSameConnection())
                {
                    eReturn.setAttribute("sameConnection", "true");
                }

                if ((m_sConnectionID != null) && (m_sConnectionID.length() > 0))
                {
                    eReturn.setAttribute("connId", getConnectionID());
                }
            }
        }

        return eReturn;
    }

    /**
     * Returns the original cursorsize.
     *
     * @return  The original cursorsize.
     */
    public int getOrigCursorSize()
    {
        return m_iOrigCursorSize;
    }

    /**
     * Returns the position of the cursor.
     *
     * @return  The position of the cursor.
     */
    public int getPosition()
    {
        return m_iPosition;
    }

    /**
     * This method returns the cursor-element for the previous set of records.
     *
     * @param   dDoc  The XML document to create the cursor-element in.
     *
     * @return  The appropiate cursor element.
     */
    public int getPrevCursor(Document dDoc)
    {
        int iReturn = dDoc.createElement("cursor");

        // If the position is null, it means that the last set of data has been retrieved. That
        // means the iRecordCount contains the number of records in the dataset. If this.position
        // contains data it holds the new position for the NEXT set of data. Say we have 6 records
        // with a numRows of 2 and we retrieved records 3-4 then position will contain 4. Getting
        // the previous means we want to see records 1-2 so from the current position we'll have to
        // substract 2 * numRows in order to get the appropiate position vor the previous set.
        int iCurrentPos = m_iPosition;

        if (m_iPosition == -1)
        {
            // We're displaying the last recordset. It is possible that the last recordset is for
            // example 1 row, while the numRows is 2. So if we're displayig record 7, we now want to
            // display records 5-6. If the currentPos isn't dividable trough numRows, we'll increase
            // currentpos to simulate a 'complete' recordset.
            int iRestValue = m_iKnownRecordCount % m_iCursorSize;

            if (iRestValue > 0)
            {
                iCurrentPos = (m_iKnownRecordCount + m_iCursorSize) - iRestValue;
            }
            else
            {
                iCurrentPos = m_iKnownRecordCount;
            }
        }

        int iNewPos = iCurrentPos - (2 * m_iCursorSize);

        if (iNewPos < 0)
        {
            iNewPos = 0;
        }
        Node.setAttribute(iReturn, "position", String.valueOf(iNewPos));

        // Set the number of rows to retrieve.
        if (m_iCursorSize != -1)
        {
            Node.setAttribute(iReturn, "position", String.valueOf(m_iCursorSize));
        }

        if (getSameConnection())
        {
            Node.setAttribute(iReturn, "sameConnection", "true");
        }

        if ((m_sConnectionID != null) && (m_sConnectionID.length() > 0))
        {
            Node.setAttribute(iReturn, "connId", getConnectionID());
        }

        return iReturn;
    }

    /**
     * This method returns the cursor-element for the previous set of records.
     *
     * @param   eParent  dDoc The XML document to create the cursor-element in.
     *
     * @return  The appropiate cursor element.
     */
    public Element getPrevCursor(Element eParent)
    {
        Element eReturn = XMLHelper.createElementWithParentNS("cursor", eParent);

        // If the position is null, it means that the last set of data has been retrieved. That
        // means the iRecordCount contains the number of records in the dataset. If this.position
        // contains data it holds the new position for the NEXT set of data. Say we have 6 records
        // with a numRows of 2 and we retrieved records 3-4 then position will contain 4. Getting
        // the previous means we want to see records 1-2 so from the current position we'll have to
        // substract 2 * numRows in order to get the appropiate position vor the previous set.
        int iCurrentPos = m_iPosition;

        if (m_iPosition == -1)
        {
            // We're displaying the last recordset. It is possible that the last recordset is for
            // example 1 row, while the numRows is 2. So if we're displayig record 7, we now want to
            // display records 5-6. If the currentPos isn't dividable trough numRows, we'll increase
            // currentpos to simulate a 'complete' recordset.
            int iRestValue = m_iKnownRecordCount % m_iCursorSize;

            if (iRestValue > 0)
            {
                iCurrentPos = (m_iKnownRecordCount + m_iCursorSize) - iRestValue;
            }
            else
            {
                iCurrentPos = m_iKnownRecordCount;
            }
        }

        int iNewPos = iCurrentPos - (2 * m_iCursorSize);

        if (iNewPos < 0)
        {
            iNewPos = 0;
        }
        eReturn.setAttribute("position", String.valueOf(iNewPos));

        // Set the number of rows to retrieve.
        if (m_iCursorSize != -1)
        {
            eReturn.setAttribute("position", String.valueOf(m_iCursorSize));
        }

        if (getSameConnection())
        {
            eReturn.setAttribute("sameConnection", "true");
        }

        // Query results are always forward, not previous. For previous a new query will be
        // executed.

        return eReturn;
    }

    /**
     * This method gets whether or not the same DB connection should be reused.
     *
     * @return  Whether or not the same DB connection should be reused.
     */
    public boolean getSameConnection()
    {
        return m_bSameConnection;
    }

    /**
     * Returns whether or not the end of data was retrieved.
     *
     * @return  Whether or not the end of data was retrieved.
     */
    public boolean isEndReached()
    {
        return m_bEndReached;
    }

    /**
     * Returns whether or not the first set of data was retrieved.
     *
     * @return  Whether or not the first set of data was retrieved.
     */
    public boolean isFirstSet()
    {
        return m_bFirstSet;
    }

    /**
     * Returns whether or not the last set of data was retrieved.
     *
     * @return  Whether or not the last set of data was retrieved.
     */
    public boolean isLastSet()
    {
        return m_bLastSet;
    }

    /**
     * This method parses the cursor-element passed on and saves the appropiate data in this
     * cursor-object.
     *
     * @param  iResponse  The current response
     */
    public void parseCursorElement(int iResponse)
    {
        if (iResponse != 0)
        {
            int iCursor = Find.firstMatch(iResponse, "?<" + m_sMethodName + "Response><cursor>");

            if (iCursor != 0)
            {
                m_sName = Node.getAttribute(iCursor, "id", null);

                String sTemp = Node.getAttribute(iCursor, "numRows", "-1");

                try
                {
                    m_iCursorSize = Integer.parseInt(sTemp);
                }
                catch (Exception e)
                {
                    m_iCursorSize = -1;
                }
                sTemp = Node.getAttribute(iCursor, "position", "-1");

                try
                {
                    m_iPosition = Integer.parseInt(sTemp);
                }
                catch (Exception e)
                {
                    m_iPosition = -1;
                }
                sTemp = Node.getAttribute(iCursor, "maxRows", "-1");

                try
                {
                    m_iMaxRows = Integer.parseInt(sTemp);
                }
                catch (Exception e)
                {
                    m_iMaxRows = -1;
                }

                // Check if the last record was retrieved
                if (m_sName != null)
                {
                    if ((m_bEndReached == true) && (m_iKnownRecordCount == m_iPosition))
                    {
                        m_bLastSet = true;
                    }
                    else
                    {
                        m_bLastSet = false;
                    }

                    if (m_iKnownRecordCount < m_iPosition)
                    {
                        m_iKnownRecordCount = m_iPosition;
                    }

                    if ((m_iPosition - m_iCursorSize) <= 0)
                    {
                        m_bFirstSet = true;
                    }
                    else
                    {
                        m_bFirstSet = false;
                    }

                    // Also parse the sameConnection params.
                    sTemp = Node.getAttribute(iCursor, "sameConnection", "false");

                    if (sTemp.equalsIgnoreCase("true"))
                    {
                        setSameConnection(true);
                    }

                    setConnectionID(Node.getAttribute(iCursor, "connId", null));
                }
                else
                {
                    m_bLastSet = true;

                    // If this is the first time the end of the data has been reached, save the
                    // total recordcount
                    if (m_bEndReached == false)
                    {
                        m_bEndReached = true;

                        // Retrieve the amount of tuples to add to the max-pos of the cursor.
                        int[] aiTuples = Find.match(iResponse,
                                                    "?<" + m_sMethodName + "Response><tuple>");
                        int iRealCount = 0;

                        for (int iCount = 0; iCount < aiTuples.length; iCount++)
                        {
                            if (Node.getAttribute(aiTuples[iCount], "id", null) == null)
                            {
                                iRealCount++;
                            }
                        }
                        m_iKnownRecordCount += iRealCount;
                    }

                    if (m_iKnownRecordCount <= m_iCursorSize)
                    {
                        m_bFirstSet = true;
                    }
                    else
                    {
                        m_bFirstSet = false;
                    }
                }
            }
        }
    }

    /**
     * This method parses the cursor-element passed on and saves the appropiate data in this
     * cursor-object.
     *
     * @param   eResponse  The current response
     *
     * @throws  TransformerException  In case the XPaths fail.
     */
    public void parseCursorElement(Element eResponse)
                            throws TransformerException
    {
        EmptyPrefixResolver epr = new EmptyPrefixResolver();
        epr.registerPrefix("ns", m_sNamespace);

        if (eResponse != null)
        {
            Element eCursor = null;

            if ("cursor".equals(eResponse.getLocalName()))
            {
                eCursor = eResponse;
            }
            else
            {
                eCursor = (Element) XPathHelper.selectSingleNode(eResponse,
                                                                 "//ns:" + m_sMethodName +
                                                                 "Response/ns:cursor", epr);
            }

            if (eCursor != null)
            {
                m_sName = eCursor.getAttribute("id");

                String sTemp = eCursor.getAttribute("numRows");

                try
                {
                    m_iCursorSize = Integer.parseInt(sTemp);
                }
                catch (Exception e)
                {
                    m_iCursorSize = -1;
                }

                sTemp = eCursor.getAttribute("position");

                try
                {
                    m_iPosition = Integer.parseInt(sTemp);
                }
                catch (Exception e)
                {
                    m_iPosition = -1;
                }
                sTemp = eCursor.getAttribute("maxRows");

                try
                {
                    m_iMaxRows = Integer.parseInt(sTemp);
                }
                catch (Exception e)
                {
                    m_iMaxRows = -1;
                }

                // Check if the last record was retrieved
                if (m_sName != null)
                {
                    if ((m_bEndReached == true) && (m_iKnownRecordCount == m_iPosition))
                    {
                        m_bLastSet = true;
                    }
                    else
                    {
                        m_bLastSet = false;
                    }

                    if (m_iKnownRecordCount < m_iPosition)
                    {
                        m_iKnownRecordCount = m_iPosition;
                    }

                    if ((m_iPosition - m_iCursorSize) <= 0)
                    {
                        m_bFirstSet = true;
                    }
                    else
                    {
                        m_bFirstSet = false;
                    }

                    // Also parse the sameConnection params.
                    sTemp = eCursor.getAttribute("sameConnection");

                    if ("true".equalsIgnoreCase(sTemp))
                    {
                        setSameConnection(true);
                    }

                    setConnectionID(eCursor.getAttribute("connId"));
                }
                else
                {
                    m_bLastSet = true;

                    // If this is the first time the end of the data has been reached, save the
                    // total recordcount
                    if (m_bEndReached == false)
                    {
                        m_bEndReached = true;

                        // Retrieve the amount of tuples to add to the max-pos of the cursor.
                        NodeList nlTuples = XPathHelper.selectNodeList(eResponse,
                                                                       "//ns:" + m_sMethodName +
                                                                       "Response/ns:tuple");
                        int iRealCount = 0;

                        for (int iCount = 0; iCount < nlTuples.getLength(); iCount++)
                        {
                            if (((Element) nlTuples.item(iCount)).getAttribute("id") == null)
                            {
                                iRealCount++;
                            }
                        }
                        m_iKnownRecordCount += iRealCount;
                    }

                    if (m_iKnownRecordCount <= m_iCursorSize)
                    {
                        m_bFirstSet = true;
                    }
                    else
                    {
                        m_bFirstSet = false;
                    }
                }
            }
        }
    }

    /**
     * This method sets the conenction ID.
     *
     * @param  sConnectionID  The conenction ID.
     */
    public void setConnectionID(String sConnectionID)
    {
        m_sConnectionID = sConnectionID;
    }

    /**
     * Sets the new cursorsize.
     *
     * @param  iCursorSize  The new cursorsize.
     */
    public void setCursorSize(int iCursorSize)
    {
        m_iCursorSize = iCursorSize;
    }

    /**
     * This method sets the cursor to an empty resultset.
     */
    public void setEmptySet()
    {
        m_iKnownRecordCount = 0;
        m_bFirstSet = true;
        m_bLastSet = true;
    }

    /**
     * Sets whether or not the end of data was retrieved.
     *
     * @param  bEndReached  Whether or not the end of data was retrieved.
     */
    public void setEndReached(boolean bEndReached)
    {
        m_bEndReached = bEndReached;
    }

    /**
     * Sets whether or not the first set of data was retrieved.
     *
     * @param  bFirstSet  Whether or not the first set of data was retrieved.
     */
    public void setFirstSet(boolean bFirstSet)
    {
        m_bFirstSet = bFirstSet;
    }

    /**
     * Sets the known recordcount of the cursor.
     *
     * @param  iKnownRecordCount  The known recordcount of the cursor.
     */
    public void setKnownRecordCount(int iKnownRecordCount)
    {
        m_iKnownRecordCount = iKnownRecordCount;
    }

    /**
     * Sets whether or not the last set of data was retrieved.
     *
     * @param  bLastSet  Whether or not the last set of data was retrieved.
     */
    public void setLastSet(boolean bLastSet)
    {
        m_bLastSet = bLastSet;
    }

    /**
     * Sets the maximum number of rows.
     *
     * @param  iMaxRows  The maximum number of rows.
     */
    public void setMaxRows(int iMaxRows)
    {
        m_iMaxRows = iMaxRows;
    }

    /**
     * Sets the name of the method.
     *
     * @param  sMethodName  The name of the method.
     */
    public void setMethodName(String sMethodName)
    {
        m_sMethodName = sMethodName;
    }

    /**
     * Sets the name of the cursor.
     *
     * @param  sName  The name of the cursor.
     */
    public void setName(String sName)
    {
        m_sName = sName;
    }

    /**
     * Sets the original cursorsize.
     *
     * @param  iOrigCursorSize  The original cursorsize.
     */
    public void setOrigCursorSize(int iOrigCursorSize)
    {
        m_iOrigCursorSize = iOrigCursorSize;
    }

    /**
     * Sets the position of the cursor.
     *
     * @param  iPosition  The position of the cursor.
     */
    public void setPosition(int iPosition)
    {
        m_iPosition = iPosition;
    }

    /**
     * This method sets whether or not the same DB connection should be reused.
     *
     * @param  bSameConnection  Whether or not the same DB connection should be reused.
     */
    public void setSameConnection(boolean bSameConnection)
    {
        m_bSameConnection = bSameConnection;
    }

    /**
     * Returns a string-representation of the object.
     *
     * @return  A string-representation of the object.
     */
    @Override public String toString()
    {
        return "Position: " + m_iPosition + "\nOrigNumRows: " + m_iOrigCursorSize + "\nName: " +
               m_sName + "\nCursorSize: " + m_iCursorSize + "\nmaxRows: " + m_iMaxRows +
               "\nbLastSet: " + m_bLastSet + "\nbFirstSet: " + m_bFirstSet + "\niRecordCount: " +
               m_iKnownRecordCount;
    }
}
