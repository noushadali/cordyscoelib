package com.cordys.coe.tools.snapshot;

import java.util.List;

import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.cordys.coe.tools.snapshot.data.SnapshotResult;
import com.cordys.coe.util.swing.MessageBoxUtil;

/**
 * Holds the Class GrabberTask.
 */
public class GrabberTask extends SwingWorker<SnapshotResult, GrabberData> implements ISnapshotGrabberProgress
{
    /** Holds the parent Swing frame */
    private SystemSnapshot m_systemSnapshot;
    /** Holds the progress monitor that is to be used. */
    ProgressMonitor m_pm;

    /**
     * Instantiates a new grabber task.
     * 
     * @param pm The progress monitor to be used.
     * @param systemSnapshot TODO
     */
    GrabberTask(SystemSnapshot systemSnapshot, ProgressMonitor pm)
    {
        this.m_systemSnapshot = systemSnapshot;
        m_pm = pm;
    }

    /**
     * @see javax.swing.SwingWorker#doInBackground()
     */
    @Override
    protected SnapshotResult doInBackground() throws Exception
    {
        SnapshotResult result = null;
        try
        {
            SystemSnapshotGrabber ssg = new SystemSnapshotGrabber(m_systemSnapshot.getConfig());

            result = ssg.buildSnapshot(this);
        }
        catch (final Exception e)
        {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run()
                {
                    MessageBoxUtil.showError("Error getting snapshot", e);
                }
            });
        }

        return result;
    }

    /**
     * @see com.cordys.coe.tools.snapshot.ISnapshotGrabberProgress#setGrabberProgress(int)
     */
    @Override
    public void setGrabberProgress(int progress)
    {
        setProgress(progress);
    }

    /**
     * @see javax.swing.SwingWorker#done()
     */
    @Override
    protected void done()
    {
        m_pm.close();

        try
        {
            m_systemSnapshot.setResult(get());
            m_systemSnapshot.updateResultView();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * @see com.cordys.coe.tools.snapshot.ISnapshotGrabberProgress#setMax(int)
     */
    @Override
    public void setMax(int max)
    {
        m_pm.setMaximum(max);
    }

    /**
     * @see com.cordys.coe.tools.snapshot.ISnapshotGrabberProgress#publishGrabberData(com.cordys.coe.tools.snapshot.GrabberData)
     */
    @Override
    public void publishGrabberData(GrabberData data)
    {
        if (data.getProgress() > 100)
        {
            setProgress(100);
        }
        else
        {
            setProgress(data.getProgress());
        }
        publish(data);
    }

    /**
     * @see javax.swing.SwingWorker#process(java.util.List)
     */
    @Override
    protected void process(List<GrabberData> chunks)
    {
        if ((chunks != null) && (chunks.size() > 0))
        {
            GrabberData gd = chunks.get(chunks.size() - 1);

            m_pm.setNote(gd.toString());
        }
    }
}