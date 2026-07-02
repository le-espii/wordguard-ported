/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.sk89q.worldedit.util.formatting.text;

import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.EntityNbtComponent;
import com.sk89q.worldedit.util.formatting.text.NbtComponentImpl;
import com.sk89q.worldedit.util.formatting.text.format.Style;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

final class EntityNbtComponentImpl
extends NbtComponentImpl<EntityNbtComponent, EntityNbtComponent.Builder>
implements EntityNbtComponent {
    private final String selector;

    EntityNbtComponentImpl(@NonNull List<Component> children, @NonNull Style style, String nbtPath, boolean interpret, String selector) {
        super(children, style, nbtPath, interpret);
        this.selector = selector;
    }

    @Override
    public @NonNull EntityNbtComponent nbtPath(@NonNull String nbtPath) {
        if (Objects.equals(this.nbtPath, nbtPath)) {
            return this;
        }
        return new EntityNbtComponentImpl(this.children, this.style, nbtPath, this.interpret, this.selector);
    }

    @Override
    public @NonNull EntityNbtComponent interpret(boolean interpret) {
        if (this.interpret == interpret) {
            return this;
        }
        return new EntityNbtComponentImpl(this.children, this.style, this.nbtPath, interpret, this.selector);
    }

    @Override
    public @NonNull String selector() {
        return this.selector;
    }

    @Override
    public @NonNull EntityNbtComponent selector(@NonNull String selector) {
        if (Objects.equals(this.selector, selector)) {
            return this;
        }
        return new EntityNbtComponentImpl(this.children, this.style, this.nbtPath, this.interpret, selector);
    }

    @Override
    public @NonNull EntityNbtComponent children(@NonNull List<Component> children) {
        return new EntityNbtComponentImpl(children, this.style, this.nbtPath, this.interpret, this.selector);
    }

    @Override
    public @NonNull EntityNbtComponent style(@NonNull Style style) {
        if (Objects.equals(this.style, style)) {
            return this;
        }
        return new EntityNbtComponentImpl(this.children, style, this.nbtPath, this.interpret, this.selector);
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof EntityNbtComponent)) {
            return false;
        }
        if (!super.equals(other)) {
            return false;
        }
        EntityNbtComponentImpl that = (EntityNbtComponentImpl)other;
        return Objects.equals(this.selector, that.selector());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.selector.hashCode();
        return result;
    }

    @Override
    protected void populateToString(@NonNull Map<String, Object> builder) {
        super.populateToString(builder);
        builder.put("selector", this.selector);
    }

    @Override
    public @NonNull EntityNbtComponent.Builder toBuilder() {
        return new BuilderImpl(this);
    }

    static final class BuilderImpl
    extends NbtComponentImpl.BuilderImpl<EntityNbtComponent, EntityNbtComponent.Builder>
    implements EntityNbtComponent.Builder {
        private @Nullable String selector;

        BuilderImpl() {
        }

        BuilderImpl(@NonNull EntityNbtComponent component) {
            super(component);
            this.selector = component.selector();
        }

        @Override
        public @NonNull EntityNbtComponent.Builder selector(@NonNull String selector) {
            this.selector = selector;
            return this;
        }

        @Override
        public @NonNull EntityNbtComponent build() {
            if (this.nbtPath == null) {
                throw new IllegalStateException("nbt path must be set");
            }
            if (this.selector == null) {
                throw new IllegalStateException("selector must be set");
            }
            return new EntityNbtComponentImpl(this.children, this.buildStyle(), this.nbtPath, this.interpret, this.selector);
        }
    }
}

