package com.xx.avlibrary.gl.util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GLThreadPool
{
    private static ThreadPoolExecutor mThreadPool;

    public static void init()
    {
        if (mThreadPool == null)
        {
            mThreadPool = new ThreadPoolExecutor(3, 5, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        }
    }

    public static void executeTask(Runnable task)
    {
        if (mThreadPool != null)
        {
            mThreadPool.execute(task);
        }
    }

    public static void shutdownAll()
    {
        if (mThreadPool != null)
        {
            mThreadPool.shutdown();
        }
    }

    public static void shutdown(Runnable task)
    {
        if (mThreadPool != null)
        {
            mThreadPool.remove(task);
        }
    }
}
