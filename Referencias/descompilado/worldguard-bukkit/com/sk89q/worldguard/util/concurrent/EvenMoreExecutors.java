/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.util.concurrent.ThreadFactoryBuilder
 */
package com.sk89q.worldguard.util.concurrent;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class EvenMoreExecutors {
    private EvenMoreExecutors() {
    }

    public static ExecutorService newBoundedCachedThreadPool(int minThreads, int maxThreads, int queueSize) {
        return EvenMoreExecutors.newBoundedCachedThreadPool(minThreads, maxThreads, queueSize, null);
    }

    public static ExecutorService newBoundedCachedThreadPool(int minThreads, int maxThreads, int queueSize, String threadFormat) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(minThreads, maxThreads, 60L, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(queueSize));
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        if (threadFormat != null) {
            threadPoolExecutor.setThreadFactory(new ThreadFactoryBuilder().setNameFormat(threadFormat).build());
        }
        return threadPoolExecutor;
    }
}

