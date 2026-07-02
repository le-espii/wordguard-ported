/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Lists
 */
package com.sk89q.worldedit.history.changeset;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.sk89q.worldedit.history.change.Change;
import com.sk89q.worldedit.history.changeset.ChangeSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ArrayListHistory
implements ChangeSet {
    private final List<Change> changes = new ArrayList<Change>();
    private boolean recordChanges = true;

    @Override
    public void add(Change change) {
        Preconditions.checkNotNull((Object)change);
        if (this.recordChanges) {
            this.changes.add(change);
        }
    }

    @Override
    public boolean isRecordingChanges() {
        return this.recordChanges;
    }

    @Override
    public void setRecordChanges(boolean recordChanges) {
        this.recordChanges = recordChanges;
    }

    @Override
    public Iterator<Change> backwardIterator() {
        return Lists.reverse(this.changes).iterator();
    }

    @Override
    public Iterator<Change> forwardIterator() {
        return this.changes.iterator();
    }

    @Override
    public int size() {
        return this.changes.size();
    }
}

