package com.cordys.coe.util.xml;

import com.cordys.coe.exception.GeneralException;

import com.eibus.xml.nom.Document;

import java.io.File;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A class that is used to keep track of Message XML objects so that all the unused objects can be
 * cleaned when they are no longer needed and relying on garbage collection is not desired.
 *
 * @author  mpoyhone
 */
public class MessageContext
{
    /**
     * A document that can be se to this class, so Message objects can be created more easily.
     */
    protected Document dDocument = null;
    /**
     * A map containing the messages added to this context.
     */
    protected Map<Message, Message> mInstanceMap = new HashMap<Message, Message>();

    /**
     * Creates a new MessageContext object.
     */
    public MessageContext()
    {
    }

    /**
     * Creates a new MessageContext object.
     *
     * @param  dDoc  The new document for this context.
     */
    public MessageContext(Document dDoc)
    {
        this.dDocument = dDoc;
    }

    /**
     * Adds a message to this context.
     *
     * @param  mMessage  The message to be added.
     */
    public synchronized void add(Message mMessage)
    {
        // Remove the message from the old context.
        if (mMessage.getContext() != null)
        {
            mMessage.getContext().remove(mMessage);
        }

        mInstanceMap.put(mMessage, mMessage);

        mMessage.setContext(this);
    }

    /**
     * Clears (calls Message.clear()) all the message that as in this context.
     */
    public synchronized void clear()
    {
        for (Iterator<Message> iIter = mInstanceMap.keySet().iterator(); iIter.hasNext();)
        {
            Message mMsg = iIter.next();

            mMsg.clear();
        }

        mInstanceMap.clear();
    }

    /**
     * Creates a new Message and adds it to this context. For more information about the parameters,
     * see the Message class constructor.
     *
     * @param   iData  Message constructor parameter.
     *
     * @return  The constructed message.
     */
    public Message createMessage(int iData)
    {
        Message mMsg = new Message(iData);

        add(mMsg);

        return mMsg;
    }

    /**
     * Creates a new Message and adds it to this context. For more information about the parameters,
     * see the Message class constructor.
     *
     * @param   mSrc  Message constructor parameter.
     *
     * @return  The constructed message.
     */
    public Message createMessage(Message mSrc)
    {
        Message mMsg = new Message(mSrc);

        add(mMsg);

        return mMsg;
    }

    /**
     * Creates a new Message and adds it to this context. Uses the document set to this context. For
     * more information about the parameters, see the Message class constructor.
     *
     * @param   sData  Message constructor parameter.
     *
     * @return  The constructed message.
     *
     * @throws  GeneralException  DOCUMENTME
     */
    public Message createMessage(String sData)
                          throws GeneralException
    {
        if (dDocument == null)
        {
            throw new GeneralException("Document not set.");
        }

        Message mMsg = new Message(dDocument, sData);

        add(mMsg);

        return mMsg;
    }

    /**
     * Creates a new Message and adds it to this context. Uses the document set to this context. For
     * more information about the parameters, see the Message class constructor.
     *
     * @param   fFile  Message constructor parameter.
     *
     * @return  The constructed message.
     *
     * @throws  GeneralException  DOCUMENTME
     */
    public Message createMessage(File fFile)
                          throws GeneralException
    {
        if (dDocument == null)
        {
            throw new GeneralException("Document not set.");
        }

        Message mMsg = new Message(dDocument, fFile);

        add(mMsg);

        return mMsg;
    }

    /**
     * Creates a new Message and adds it to this context. For more information about the parameters,
     * see the Message class constructor.
     *
     * @param   iData        Message constructor parameter.
     * @param   bCreateCopy  Message constructor parameter.
     *
     * @return  The constructed message.
     */
    public Message createMessage(int iData, boolean bCreateCopy)
    {
        Message mMsg = new Message(iData, bCreateCopy);

        add(mMsg);

        return mMsg;
    }

    /**
     * Creates a new Message and adds it to this context. For more information about the parameters,
     * see the Message class constructor.
     *
     * @param   sxtDataTree  Message constructor parameter.
     * @param   iData        Message constructor parameter.
     *
     * @return  The constructed message.
     */
    public Message createMessage(SharedXMLTree sxtDataTree, int iData)
    {
        Message mMsg = new Message(sxtDataTree, iData);

        add(mMsg);

        return mMsg;
    }

    /**
     * Creates a new Message and adds it to this context. For more information about the parameters,
     * see the Message class constructor.
     *
     * @param   dDoc   Message constructor parameter.
     * @param   sData  Message constructor parameter.
     *
     * @return  The constructed message.
     *
     * @throws  GeneralException  DOCUMENTME
     */
    public Message createMessage(Document dDoc, String sData)
                          throws GeneralException
    {
        Message mMsg = new Message(dDoc, sData);

        add(mMsg);

        return mMsg;
    }

    /**
     * Creates a new Message and adds it to this context. For more information about the parameters,
     * see the Message class constructor.
     *
     * @param   dDoc   Message constructor parameter.
     * @param   fFile  Message constructor parameter.
     *
     * @return  The constructed message.
     *
     * @throws  GeneralException  DOCUMENTME
     */
    public Message createMessage(Document dDoc, File fFile)
                          throws GeneralException
    {
        Message mMsg = new Message(dDoc, fFile);

        add(mMsg);

        return mMsg;
    }

    /**
     * Returns the document set to this context.
     *
     * @return  The document set to this context.
     */
    public Document getDocument()
    {
        return dDocument;
    }

    /**
     * Removes a message from this context. This message is not cleared.
     *
     * @param  mMessage  The message to be removed.
     */
    public synchronized void remove(Message mMessage)
    {
        if (mMessage.getContext() != this)
        {
            throw new IllegalStateException("Message is not set to this context!");
        }

        mInstanceMap.remove(mMessage);

        mMessage.setContext(this);
    }

    /**
     * Removes a message from this context.
     *
     * @param  mMessage  The message to be removed.
     * @param  bClear    If true, the message is cleared.
     */
    public synchronized void remove(Message mMessage, boolean bClear)
    {
        remove(mMessage);

        if (bClear)
        {
            mMessage.clear();
        }
    }

    /**
     * Sets the document that can be used to create new Message objects.
     *
     * @param  dDoc  The new document for this context.
     */
    public void setDocument(Document dDoc)
    {
        dDocument = dDoc;
    }
}
