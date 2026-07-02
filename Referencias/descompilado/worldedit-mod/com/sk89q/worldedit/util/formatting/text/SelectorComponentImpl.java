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
import com.sk89q.worldedit.util.formatting.text.SelectorComponent;
import com.sk89q.worldedit.util.formatting.text.format.Style;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

final class SelectorComponentImpl
extends AbstractComponent
implements SelectorComponent {
    private final String pattern;

    SelectorComponentImpl(@NonNull List<Component> children, @NonNull Style style, @NonNull String pattern) {
        super(children, style);
        this.pattern = pattern;
    }

    @Override
    public @NonNull String pattern() {
        return this.pattern;
    }

    @Override
    public @NonNull SelectorComponent pattern(@NonNull String pattern) {
        if (Objects.equals(this.pattern, pattern)) {
            return this;
        }
        return new SelectorComponentImpl(this.children, this.style, Objects.requireNonNull(pattern, "pattern"));
    }

    @Override
    public @NonNull SelectorComponent children(@NonNull List<Component> children) {
        return new SelectorComponentImpl(children, this.style, this.pattern);
    }

    @Override
    public @NonNull SelectorComponent style(@NonNull Style style) {
        if (Objects.equals(this.style, style)) {
            return this;
        }
        return new SelectorComponentImpl(this.children, style, this.pattern);
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SelectorComponent)) {
            return false;
        }
        if (!super.equals(other)) {
            return false;
        }
        SelectorComponent that = (SelectorComponent)other;
        return Objects.equals(this.pattern, that.pattern());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.pattern.hashCode();
        return result;
    }

    @Override
    protected void populateToString(@NonNull Map<String, Object> builder) {
        builder.put("pattern", this.pattern);
    }

    @Override
    public @NonNull SelectorComponent.Builder toBuilder() {
        return new BuilderImpl(this);
    }

    static final class BuilderImpl
    extends AbstractComponentBuilder<SelectorComponent, SelectorComponent.Builder>
    implements SelectorComponent.Builder {
        private @Nullable String pattern;

        BuilderImpl() {
        }

        BuilderImpl(@NonNull SelectorComponent component) {
            super(component);
            this.pattern = component.pattern();
        }

        @Override
        public @NonNull SelectorComponent.Builder pattern(@NonNull String pattern) {
            this.pattern = pattern;
            return this;
        }

        @Override
        public @NonNull SelectorComponent build() {
            if (this.pattern == null) {
                throw new IllegalStateException("pattern must be set");
            }
            return new SelectorComponentImpl(this.children, this.buildStyle(), this.pattern);
        }
    }
}

