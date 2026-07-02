/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.util.concurrent.ListeningExecutorService
 *  com.google.common.util.concurrent.MoreExecutors
 */
package com.sk89q.worldedit.util.paste;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

final class Pasters {
    private static final ListeningExecutorService executor = MoreExecutors.listeningDecorator((ExecutorService)new ThreadPoolExecutor(0, Integer.MAX_VALUE, 2L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>()));

    private Pasters() {
    }

    static ListeningExecutorService getExecutor() {
        return executor;
    }
}

