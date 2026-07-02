/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.registry;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.registry.Keyed;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public abstract class Category<T extends Keyed>
implements Keyed {
    private final Set<T> set = new HashSet<T>();
    private final Supplier<Set<T>> supplier;
    protected final String id;
    private boolean empty = true;

    public Category(String id) {
        this.id = id;
        this.supplier = null;
    }

    public Category(String id, Supplier<Set<T>> contentSupplier) {
        this.id = id;
        this.supplier = contentSupplier;
    }

    @Override
    public final String id() {
        return this.id;
    }

    public final Set<T> getAll() {
        if (this.empty) {
            this.set.addAll(this.load());
            this.empty = false;
        }
        return this.set;
    }

    @Deprecated
    protected Set<T> load() {
        Preconditions.checkState((this.supplier != null ? 1 : 0) != 0, (Object)"load() should be overriden or a supplier should be provided in the constructor.");
        return this.supplier.get();
    }

    public boolean contains(T object) {
        return this.getAll().contains(object);
    }

    public void invalidateCache() {
        this.set.clear();
        this.empty = true;
    }

    public String toString() {
        return this.id();
    }
}

