/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.util.task;

import com.sk89q.worldedit.util.task.Task;
import java.util.Comparator;

public class TaskStateComparator
implements Comparator<Task<?>> {
    @Override
    public int compare(Task<?> o1, Task<?> o2) {
        return Integer.compare(o1.getState().ordinal(), o2.getState().ordinal());
    }
}

