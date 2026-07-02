/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.util.task;

import com.sk89q.worldedit.util.task.Task;
import java.util.List;

public interface Supervisor {
    public List<Task<?>> getTasks();

    public void monitor(Task<?> var1);
}

