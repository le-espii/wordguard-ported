/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.util.concurrent.MoreExecutors
 */
package com.sk89q.worldedit.util.task;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.MoreExecutors;
import com.sk89q.worldedit.util.task.Supervisor;
import com.sk89q.worldedit.util.task.Task;
import java.util.ArrayList;
import java.util.List;

public class SimpleSupervisor
implements Supervisor {
    private final List<Task<?>> monitored = new ArrayList();
    private final Object lock = new Object();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<Task<?>> getTasks() {
        Object object = this.lock;
        synchronized (object) {
            return new ArrayList(this.monitored);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void monitor(Task<?> task) {
        Preconditions.checkNotNull(task);
        Object object = this.lock;
        synchronized (object) {
            this.monitored.add(task);
        }
        task.addListener(() -> {
            Object object = this.lock;
            synchronized (object) {
                this.monitored.remove(task);
            }
        }, MoreExecutors.directExecutor());
    }
}

