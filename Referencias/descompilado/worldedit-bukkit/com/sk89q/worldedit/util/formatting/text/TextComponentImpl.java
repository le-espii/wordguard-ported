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
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.format.Style;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

final class TextComponentImpl
extends AbstractComponent
implements TextComponent {
    static final TextComponent EMPTY = TextComponentImpl.createDirect("");
    static final TextComponent NEWLINE = TextComponentImpl.createDirect("\n");
    static final TextComponent SPACE = TextComponentImpl.createDirect(" ");
    private final String content;

    private static @NonNull TextComponent createDirect(@NonNull String content) {
        return new TextComponentImpl(EMPTY_COMPONENT_LIST, Style.empty(), content);
    }

    TextComponentImpl(@NonNull List<Component> children, @NonNull Style style, @NonNull String content) {
        super(children, style);
        this.content = content;
    }

    @Override
    public @NonNull String content() {
        return this.content;
    }

    @Override
    public @NonNull TextComponent content(@NonNull String content) {
        if (Objects.equals(this.content, content)) {
            return this;
        }
        return new TextComponentImpl(this.children, this.style, Objects.requireNonNull(content, "content"));
    }

    @Override
    public boolean isEmpty() {
        return this == EMPTY;
    }

    @Override
    public @NonNull TextComponent children(@NonNull List<Component> children) {
        return new TextComponentImpl(children, this.style, this.content);
    }

    @Override
    public @NonNull TextComponent style(@NonNull Style style) {
        if (Objects.equals(this.style, style)) {
            return this;
        }
        return new TextComponentImpl(this.children, style, this.content);
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TextComponentImpl)) {
            return false;
        }
        if (!super.equals(other)) {
            return false;
        }
        TextComponentImpl that = (TextComponentImpl)other;
        return Objects.equals(this.content, that.content);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.content.hashCode();
        return result;
    }

    @Override
    protected void populateToString(@NonNull Map<String, Object> builder) {
        builder.put("content", this.content);
    }

    @Override
    public @NonNull TextComponent.Builder toBuilder() {
        return new BuilderImpl(this);
    }

    static final class BuilderImpl
    extends AbstractComponentBuilder<TextComponent, TextComponent.Builder>
    implements TextComponent.Builder {
        private String content = "";

        BuilderImpl() {
        }

        BuilderImpl(@NonNull TextComponent component) {
            super(component);
            this.content = component.content();
        }

        @Override
        public @NonNull TextComponent.Builder content(@NonNull String content) {
            this.content = Objects.requireNonNull(content, "content");
            return this;
        }

        @Override
        public @NonNull TextComponent build() {
            if (this.isEmpty()) {
                return EMPTY;
            }
            return new TextComponentImpl(this.children, this.buildStyle(), this.content);
        }

        private boolean isEmpty() {
            return this.content.isEmpty() && this.children.isEmpty() && !this.hasStyle();
        }
    }
}

