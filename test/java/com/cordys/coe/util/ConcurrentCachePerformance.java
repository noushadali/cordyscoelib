/**
 * (c) 2008 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util;

import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

/**
 * TODO Describe the class.
 *
 * @author mpoyhone
 */
public class ConcurrentCachePerformance extends TestCase
{
    private ConcurrentCache<Integer, String> cache = new ConcurrentCache<Integer, String>();
    private int dataCount = 500;
    private List<Pair<Integer, String>> data = new ArrayList<Pair<Integer, String>>(1000);
    ValueLoader loader = new ValueLoader();
    private ConcurrentLinkedQueue<String> log = null;//new ConcurrentLinkedQueue<String>();
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        for (int i = 0; i < dataCount; i++) {
            Pair<Integer, String> e = new Pair<Integer, String>(i, "value-" + i);
            
            data.add(e);
        }
        
        super.setUp();
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        cache.clear();
        
        if (log != null) {
            Writer out = new FileWriter("build/dbg.out");
            
            for (String line : log)
            {
                out.write(line);
                out.write("\n");
            }
            FileUtils.closeWriter(out);
            out = null;
        }
        
        super.tearDown();
    }
    
    public void testBlocking() throws Exception
    {
        int threadCount = 50;
        int runCount = 100;
        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        final Worker[] workers = new Worker[threadCount];
        Runnable barrierAction = new Runnable() {
            public void run()
            {
                try {
                    assertEquals(data.size(), loader.loadCounter.get());
                    loader.loadCounter.set(0);
                    cache.clear();
                    
                    for (int j = 0; j < workers.length; j++) {
                        assertEquals(data.size(), workers[j].counter.get());
                        workers[j].counter.set(0);
                    }
                }
                catch (Throwable e)
                {
                    e.printStackTrace();
                }
            }
        };
        CyclicBarrier syncBarrier = new CyclicBarrier(threadCount + 1, barrierAction);
        
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker(0, data.size() - 1, runCount, syncBarrier);
            pool.submit(workers[i]);
        }
        
        for (int i = 0; i < runCount; i++) {
            syncBarrier.await();
            //System.out.println("Finished: " + i);
        }
        
        for (int i = 0; i < workers.length; i++) {
            Exception exception = workers[i].getException();
            
            if (exception != null) {
                throw exception;
            }
        }
    }

    private class Worker implements Runnable
    {
        private int start;
        private int end;
        private int runCount;
        private CyclicBarrier syncBarrier;
        private Exception exception;
        private AtomicInteger counter = new AtomicInteger(0);

        public Worker(int start, int end, int runCount,
                      CyclicBarrier syncBarrier)
        {
            super();
            this.start = start;
            this.end = end;
            this.runCount = runCount;
            this.syncBarrier = syncBarrier;
        }
        
        public void run()
        {
            try {
                List<Pair<Integer, String>> ownData = new ArrayList<Pair<Integer, String>>(20);
                
                for (int i = start; i <= end; i++) {
                    Pair<Integer, String> e = data.get(i);
                    
                    ownData.add(e);
                }
                
                Collections.shuffle(ownData);
               
                for (int r = 0; r < runCount; r++) {
                    for (int i = 0; i < ownData.size(); i++) {
                        Pair<Integer, String> e = ownData.get(i);
                        String res = cache.get(e.getFirst(), loader, e.getSecond());
                        
                        assertEquals(e.getSecond(), res);
                        
                        Thread.yield();
                        
                        if (log != null) {
                            log.add(Thread.currentThread().getName() + ": " + e.getFirst() + "=" + res);
                        }
                        
                        counter.incrementAndGet();
                    }
                    
                    Collections.shuffle(ownData);
                    syncBarrier.await(); // Notify that we are done.
                }
            }
            catch (Exception e) {
                exception = e;
                e.printStackTrace();
                return;
            }
        }

        /**
         * Returns the exception.
         *
         * @return Returns the exception.
         */
        public Exception getException()
        {
            return exception;
        }
    }
    
    private static class ValueLoader implements ConcurrentCache.IValueLoader<String> {
        AtomicInteger loadCounter = new AtomicInteger(0);
        public String loadEntry(Object... args)
        {
            assertNotNull(args);
            loadCounter.incrementAndGet();
            return (String) args[0];
        }
    }
}
