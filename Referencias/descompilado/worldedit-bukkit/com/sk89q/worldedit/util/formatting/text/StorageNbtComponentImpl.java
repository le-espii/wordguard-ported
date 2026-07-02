/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.MonotonicNonNull
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.sk89q.worldedit.util.formatting.text;

import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.NbtComponentImpl;
import com.sk89q.worldedit.util.formatting.text.StorageNbtComponent;
import com.sk89q.worldedit.util.formatting.text.format.Style;
import com.sk89q.worldedit.util.kyori.Key;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

final class StorageNbtComponentImpl
extends NbtComponentImpl<StorageNbtComponent, StorageNbtComponent.Builder>
implements StorageNbtComponent {
    private final Key storage;

    protected StorageNbtComponentImpl(@NonNull List<Component> children, @NonNull Style style, String nbtPath, boolean interpret, Key storage) {
        super(children, style, nbtPath, interpret);
        this.storage = storage;
    }

    @Override
    public @NonNull StorageNbtComponent nbtPath(@NonNull String nbtPath) {
        if (Objects.equals(this.nbtPath, nbtPath)) {
            return this;
        }
        return new StorageNbtComponentImpl(this.children, this.style, nbtPath, this.interpret, this.storage);
    }

    @Override
    public @NonNull StorageNbtComponent interpret(boolean interpret) {
        if (this.interpret == interpret) {
            return this;
        }
        return new StorageNbtComponentImpl(this.children, this.style, this.nbtPath, interpret, this.storage);
    }

    @Override
    public @NonNull Key storage() {
        return this.storage;
    }

    @Override
    public @NonNull StorageNbtComponent storage(@NonNull Key storage) {
        if (Objects.equals(this.storage, storage)) {
            return this;
        }
        return new StorageNbtComponentImpl(this.children, this.style, this.nbtPath, this.interpret, storage);
    }

    @Override
    public @NonNull StorageNbtComponent children(@NonNull List<Component> children) {
        return new StorageNbtComponentImpl(children, this.style, this.nbtPath, this.interpret, this.storage);
    }

    @Override
    public @NonNull StorageNbtComponent style(@NonNull Style style) {
        if (Objects.equals(this.style, style)) {
            return this;
        }
        return new StorageNbtComponentImpl(this.children, style, this.nbtPath, this.interpret, this.storage);
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof StorageNbtComponent)) {
            return false;
        }
        if (!super.equals(other)) {
            return false;
        }
        StorageNbtComponentImpl that = (StorageNbtComponentImpl)other;
        return Objects.equals(this.storage, that.storage());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.storage.hashCode();
        return result;
    }

    @Override
    protected void populateToString(@NonNull Map<String, Object> builder) {
        super.populateToString(builder);
        builder.put("storage", this.storage);
    }

    @Override
    public @NonNull StorageNbtComponent.Builder toBuilder() {
        return new BuilderImpl(this);
    }

    static class BuilderImpl
    extends NbtComponentImpl.BuilderImpl<StorageNbtComponent, StorageNbtComponent.Builder>
    implements StorageNbtComponent.Builder {
        private @MonotonicNonNull Key storage;

        BuilderImpl() {
        }

        BuilderImpl(@NonNull StorageNbtComponent component) {
            super(component);
            this.storage = component.storage();
        }

        @Override
        public @NonNull StorageNbtComponent.Builder storage(@NonNull Key storage) {
            this.storage = storage;
            return this;
        }

        @Override
        public @NonNull StorageNbtComponent build() {
            if (this.nbtPath == null) {
                throw new IllegalStateException("nbt path must be set");
            }
            if (this.storage == null) {
                throw new IllegalStateException("storage must be set");
            }
            return new StorageNbtComponentImpl(this.children, this.buildStyle(), this.nbtPath, this.interpret, this.storage);
        }
    }
}

