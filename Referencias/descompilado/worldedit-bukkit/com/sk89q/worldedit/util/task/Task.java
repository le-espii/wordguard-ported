/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.util.concurrent.ListenableFuture
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.util.task;

import com.google.common.util.concurrent.ListenableFuture;
import com.sk89q.worldedit.util.task.progress.ProgressObservable;
import java.util.Date;
import java.util.UUID;
import javax.annotation.Nullable;

public interface Task<V>
extends ListenableFuture<V>,
ProgressObservable {
    public UUID getUniqueId();

    public String getName();

    @Nullable
    public Object getOwner();

    public State getState();

    public Date getCreationDate();

    public static enum State {
        SCHEDULED,
        CANCELLED,
        RUNNING,
        FAILED,
        SUCCEEDED;

    }
}

