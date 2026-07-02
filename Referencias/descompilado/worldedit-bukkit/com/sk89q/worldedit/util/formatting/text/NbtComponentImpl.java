/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.sk89q.worldedit.util.formatting.text;

import com.sk89q.worldedit.util.formatting.text.AbstractComponent;
import com.sk89q.worldedit.util.formatting.text.AbstractComponentBuilder;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.NbtComponent;
import com.sk89q.worldedit.util.formatting.text.NbtComponentBuilder;
import com.sk89q.worldedit.util.formatting.text.format.Style;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

abstract class NbtComponentImpl<C extends NbtComponent<C, B>, B extends NbtComponentBuilder<C, B>>
extends AbstractComponent
implements NbtComponent<C, B> {
    final String nbtPath;
    final boolean interpret;

    NbtComponentImpl(@NonNull List<Component> children, @NonNull Style style, String nbtPath, boolean interpret) {
        super(children, style);
        this.nbtPath = nbtPath;
        this.interpret = interpret;
    }

    @Override
    public @NonNull String nbtPath() {
        return this.nbtPath;
    }

    @Override
    public boolean interpret() {
        return this.interpret;
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof NbtComponent)) {
            return false;
        }
        if (!super.equals(other)) {
            return false;
        }
        NbtComponent that = (NbtComponent)other;
        return Objects.equals(this.nbtPath, that.nbtPath()) && this.interpret == that.interpret();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.nbtPath.hashCode();
        result = 31 * result + Boolean.hashCode(this.interpret);
        return result;
    }

    @Override
    protected void populateToString(@NonNull Map<String, Object> builder) {
        builder.put("nbtPath", this.nbtPath);
        builder.put("interpret", this.interpret);
    }

    static abstract class BuilderImpl<C extends NbtComponent<C, B>, B extends NbtComponentBuilder<C, B>>
    extends AbstractComponentBuilder<C, B>
    implements NbtComponentBuilder<C, B> {
        @Nullable String nbtPath;
        boolean interpret;

        BuilderImpl() {
        }

        BuilderImpl(@NonNull C component) {
            super(component);
            this.nbtPath = component.nbtPath();
            this.interpret = component.interpret();
        }

        @Override
        public @NonNull B nbtPath(@NonNull String nbtPath) {
            this.nbtPath = nbtPath;
            return (B)this;
        }

        @Override
        public @NonNull B interpret(boolean interpret) {
            this.interpret = interpret;
            return (B)this;
        }
    }
}

