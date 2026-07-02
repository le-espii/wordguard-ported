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
import com.sk89q.worldedit.util.formatting.text.ScoreComponent;
import com.sk89q.worldedit.util.formatting.text.format.Style;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

final class ScoreComponentImpl
extends AbstractComponent
implements ScoreComponent {
    private final String name;
    private final String objective;
    private final @Nullable String value;

    ScoreComponentImpl(@NonNull List<Component> children, @NonNull Style style, @NonNull String name, @NonNull String objective, @Nullable String value) {
        super(children, style);
        this.name = name;
        this.objective = objective;
        this.value = value;
    }

    @Override
    public @NonNull String name() {
        return this.name;
    }

    @Override
    public @NonNull ScoreComponent name(@NonNull String name) {
        if (Objects.equals(this.name, name)) {
            return this;
        }
        return new ScoreComponentImpl(this.children, this.style, Objects.requireNonNull(name, "name"), this.objective, this.value);
    }

    @Override
    public @NonNull String objective() {
        return this.objective;
    }

    @Override
    public @NonNull ScoreComponent objective(@NonNull String objective) {
        if (Objects.equals(this.objective, objective)) {
            return this;
        }
        return new ScoreComponentImpl(this.children, this.style, this.name, Objects.requireNonNull(objective, "objective"), this.value);
    }

    @Override
    public @Nullable String value() {
        return this.value;
    }

    @Override
    public @NonNull ScoreComponent value(@Nullable String value) {
        if (Objects.equals(this.value, value)) {
            return this;
        }
        return new ScoreComponentImpl(this.children, this.style, this.name, this.objective, value);
    }

    @Override
    public @NonNull ScoreComponent children(@NonNull List<Component> children) {
        return new ScoreComponentImpl(children, this.style, this.name, this.objective, this.value);
    }

    @Override
    public @NonNull ScoreComponent style(@NonNull Style style) {
        if (Objects.equals(this.style, style)) {
            return this;
        }
        return new ScoreComponentImpl(this.children, style, this.name, this.objective, this.value);
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ScoreComponent)) {
            return false;
        }
        if (!super.equals(other)) {
            return false;
        }
        ScoreComponent that = (ScoreComponent)other;
        return Objects.equals(this.name, that.name()) && Objects.equals(this.objective, that.objective()) && Objects.equals(this.value, that.value());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.name.hashCode();
        result = 31 * result + this.objective.hashCode();
        result = 31 * result + Objects.hashCode(this.value);
        return result;
    }

    @Override
    protected void populateToString(@NonNull Map<String, Object> builder) {
        builder.put("name", this.name);
        builder.put("objective", this.objective);
        builder.put("value", this.value);
    }

    @Override
    public @NonNull ScoreComponent.Builder toBuilder() {
        return new BuilderImpl(this);
    }

    static final class BuilderImpl
    extends AbstractComponentBuilder<ScoreComponent, ScoreComponent.Builder>
    implements ScoreComponent.Builder {
        private @Nullable String name;
        private @Nullable String objective;
        private @Nullable String value;

        BuilderImpl() {
        }

        BuilderImpl(@NonNull ScoreComponent component) {
            super(component);
            this.name = component.name();
            this.objective = component.objective();
            this.value = component.value();
        }

        @Override
        public @NonNull ScoreComponent.Builder name(@NonNull String name) {
            this.name = name;
            return this;
        }

        @Override
        public @NonNull ScoreComponent.Builder objective(@NonNull String objective) {
            this.objective = objective;
            return this;
        }

        @Override
        public @NonNull ScoreComponent.Builder value(@Nullable String value) {
            this.value = value;
            return this;
        }

        @Override
        public @NonNull ScoreComponent build() {
            if (this.name == null) {
                throw new IllegalStateException("name must be set");
            }
            if (this.objective == null) {
                throw new IllegalStateException("objective must be set");
            }
            return new ScoreComponentImpl(this.children, this.buildStyle(), this.name, this.objective, this.value);
        }
    }
}

