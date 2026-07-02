/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 */
package com.sk89q.worldedit.function.operation;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.RunContext;
import com.sk89q.worldedit.util.formatting.text.Component;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.List;

public class OperationQueue
implements Operation {
    private final List<Operation> operations = Lists.newArrayList();
    private final Deque<Operation> queue = new ArrayDeque<Operation>();
    private Operation current;

    public OperationQueue() {
    }

    public OperationQueue(Collection<Operation> operations) {
        Preconditions.checkNotNull(operations);
        for (Operation operation : operations) {
            this.offer(operation);
        }
        this.operations.addAll(operations);
    }

    public OperationQueue(Operation ... operation) {
        Preconditions.checkNotNull((Object)operation);
        for (Operation o : operation) {
            this.offer(o);
        }
    }

    public void offer(Operation operation) {
        Preconditions.checkNotNull((Object)operation);
        this.queue.offer(operation);
    }

    @Override
    public Operation resume(RunContext run) throws WorldEditException {
        if (this.current == null && !this.queue.isEmpty()) {
            this.current = this.queue.poll();
        }
        if (this.current != null) {
            this.current = this.current.resume(run);
            if (this.current == null) {
                this.current = this.queue.poll();
            }
        }
        return this.current != null ? this : null;
    }

    @Override
    public void cancel() {
        for (Operation operation : this.queue) {
            operation.cancel();
        }
        this.queue.clear();
    }

    @Override
    public Iterable<Component> getStatusMessages() {
        return Iterables.concat((Iterable)this.operations.stream().map(Operation::getStatusMessages).toList());
    }
}

