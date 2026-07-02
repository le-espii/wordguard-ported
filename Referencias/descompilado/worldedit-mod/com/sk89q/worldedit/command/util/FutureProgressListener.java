/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.util.concurrent.ListenableFuture
 *  com.google.common.util.concurrent.MoreExecutors
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.command.util;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.sk89q.worldedit.command.util.MessageTimerTask;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import java.util.Timer;
import java.util.TimerTask;
import javax.annotation.Nullable;

public class FutureProgressListener
implements Runnable {
    private static final Timer timer = new Timer();
    private static final int MESSAGE_DELAY = 1000;
    private static final int MESSAGE_PERIOD = 10000;
    private final MessageTimerTask task;

    @Deprecated
    public FutureProgressListener(Actor sender, String message) {
        this(sender, TextComponent.of(message));
    }

    public FutureProgressListener(Actor sender, Component message) {
        this(sender, message, null);
    }

    public FutureProgressListener(Actor sender, Component message, @Nullable Component workingMessage) {
        Preconditions.checkNotNull((Object)sender);
        Preconditions.checkNotNull((Object)message);
        this.task = new MessageTimerTask(sender, message, workingMessage);
        timer.scheduleAtFixedRate((TimerTask)this.task, 1000L, 10000L);
    }

    @Override
    public void run() {
        this.task.cancel();
    }

    @Deprecated
    public static void addProgressListener(ListenableFuture<?> future, Actor sender, String message) {
        FutureProgressListener.addProgressListener(future, sender, TextComponent.of(message));
    }

    public static void addProgressListener(ListenableFuture<?> future, Actor sender, Component message) {
        future.addListener((Runnable)new FutureProgressListener(sender, message), MoreExecutors.directExecutor());
    }

    public static void addProgressListener(ListenableFuture<?> future, Actor sender, Component message, Component workingMessage) {
        future.addListener((Runnable)new FutureProgressListener(sender, message, workingMessage), MoreExecutors.directExecutor());
    }
}

