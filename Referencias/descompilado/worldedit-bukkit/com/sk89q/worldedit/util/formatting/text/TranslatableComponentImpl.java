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
import com.sk89q.worldedit.util.formatting.text.ComponentBuilder;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.formatting.text.format.Style;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

final class TranslatableComponentImpl
extends AbstractComponent
implements TranslatableComponent {
    private final String key;
    private final List<Component> args;

    TranslatableComponentImpl(@NonNull List<Component> children, @NonNull Style style, @NonNull String key, @NonNull Component @NonNull [] args) {
        this(children, style, key, Arrays.asList(args));
    }

    TranslatableComponentImpl(@NonNull List<Component> children, @NonNull Style style, @NonNull String key, @NonNull List<? extends Component> args) {
        super(children, style);
        this.key = key;
        this.args = TranslatableComponentImpl.unmodifiableCopy(args);
    }

    @Override
    public @NonNull String key() {
        return this.key;
    }

    @Override
    public @NonNull TranslatableComponent key(@NonNull String key) {
        if (Objects.equals(this.key, key)) {
            return this;
        }
        return new TranslatableComponentImpl((List<Component>)this.children, this.style, Objects.requireNonNull(key, "key"), this.args);
    }

    @Override
    public @NonNull List<Component> args() {
        return this.args;
    }

    @Override
    public @NonNull TranslatableComponent args(Component ... args) {
        return new TranslatableComponentImpl((List<Component>)this.children, this.style, this.key, args);
    }

    @Override
    public @NonNull TranslatableComponent args(@NonNull List<? extends Component> args) {
        return new TranslatableComponentImpl((List<Component>)this.children, this.style, this.key, args);
    }

    @Override
    public @NonNull TranslatableComponent children(@NonNull List<Component> children) {
        return new TranslatableComponentImpl(children, this.style, this.key, this.args);
    }

    @Override
    public @NonNull TranslatableComponent style(@NonNull Style style) {
        if (Objects.equals(this.style, style)) {
            return this;
        }
        return new TranslatableComponentImpl((List<Component>)this.children, style, this.key, this.args);
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TranslatableComponent)) {
            return false;
        }
        if (!super.equals(other)) {
            return false;
        }
        TranslatableComponent that = (TranslatableComponent)other;
        return Objects.equals(this.key, that.key()) && Objects.equals(this.args, that.args());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.key.hashCode();
        result = 31 * result + this.args.hashCode();
        return result;
    }

    @Override
    protected void populateToString(@NonNull Map<String, Object> builder) {
        builder.put("key", this.key);
        builder.put("args", this.args);
    }

    @Override
    public @NonNull TranslatableComponent.Builder toBuilder() {
        return new BuilderImpl(this);
    }

    static final class BuilderImpl
    extends AbstractComponentBuilder<TranslatableComponent, TranslatableComponent.Builder>
    implements TranslatableComponent.Builder {
        private @Nullable String key;
        private List<? extends Component> args = AbstractComponent.EMPTY_COMPONENT_LIST;

        BuilderImpl() {
        }

        BuilderImpl(@NonNull TranslatableComponent component) {
            super(component);
            this.key = component.key();
            this.args = component.args();
        }

        @Override
        public @NonNull TranslatableComponent.Builder key(@NonNull String key) {
            this.key = key;
            return this;
        }

        @Override
        public @NonNull TranslatableComponent.Builder args(@NonNull ComponentBuilder<?, ?> arg) {
            return this.args(Collections.singletonList(arg.build()));
        }

        @Override
        public @NonNull TranslatableComponent.Builder args(ComponentBuilder<?, ?> ... args) {
            if (args.length == 0) {
                return this.args(AbstractComponent.EMPTY_COMPONENT_LIST);
            }
            return this.args(Stream.of(args).map(ComponentBuilder::build).collect(Collectors.toList()));
        }

        @Override
        public @NonNull TranslatableComponent.Builder args(@NonNull Component arg) {
            return this.args(Collections.singletonList(arg));
        }

        @Override
        public @NonNull TranslatableComponent.Builder args(Component ... args) {
            if (args.length == 0) {
                return this.args(AbstractComponent.EMPTY_COMPONENT_LIST);
            }
            return this.args(Arrays.asList(args));
        }

        @Override
        public @NonNull TranslatableComponent.Builder args(@NonNull List<? extends Component> args) {
            this.args = args;
            return this;
        }

        @Override
        public @NonNull TranslatableComponentImpl build() {
            if (this.key == null) {
                throw new IllegalStateException("key must be set");
            }
            return new TranslatableComponentImpl((List<Component>)this.children, this.buildStyle(), this.key, this.args);
        }
    }
}

