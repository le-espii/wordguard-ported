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
import com.sk89q.worldedit.util.formatting.text.KeybindComponent;
import com.sk89q.worldedit.util.formatting.text.format.Style;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

final class KeybindComponentImpl
extends AbstractComponent
implements KeybindComponent {
    private final String keybind;

    KeybindComponentImpl(@NonNull List<Component> children, @NonNull Style style, @NonNull String keybind) {
        super(children, style);
        this.keybind = keybind;
    }

    @Override
    public @NonNull String keybind() {
        return this.keybind;
    }

    @Override
    public @NonNull KeybindComponent keybind(@NonNull String keybind) {
        if (Objects.equals(this.keybind, keybind)) {
            return this;
        }
        return new KeybindComponentImpl(this.children, this.style, Objects.requireNonNull(keybind, "keybind"));
    }

    @Override
    public @NonNull KeybindComponent children(@NonNull List<Component> children) {
        return new KeybindComponentImpl(children, this.style, this.keybind);
    }

    @Override
    public @NonNull KeybindComponent style(@NonNull Style style) {
        if (Objects.equals(this.style, style)) {
            return this;
        }
        return new KeybindComponentImpl(this.children, style, this.keybind);
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof KeybindComponent)) {
            return false;
        }
        if (!super.equals(other)) {
            return false;
        }
        KeybindComponent that = (KeybindComponent)other;
        return Objects.equals(this.keybind, that.keybind());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.keybind.hashCode();
        return result;
    }

    @Override
    protected void populateToString(@NonNull Map<String, Object> builder) {
        builder.put("keybind", this.keybind);
    }

    @Override
    public @NonNull KeybindComponent.Builder toBuilder() {
        return new BuilderImpl(this);
    }

    static final class BuilderImpl
    extends AbstractComponentBuilder<KeybindComponent, KeybindComponent.Builder>
    implements KeybindComponent.Builder {
        private @Nullable String keybind;

        BuilderImpl() {
        }

        BuilderImpl(@NonNull KeybindComponent component) {
            super(component);
            this.keybind = component.keybind();
        }

        @Override
        public @NonNull KeybindComponent.Builder keybind(@NonNull String keybind) {
            this.keybind = keybind;
            return this;
        }

        @Override
        public @NonNull KeybindComponent build() {
            if (this.keybind == null) {
                throw new IllegalStateException("keybind must be set");
            }
            return new KeybindComponentImpl(this.children, this.buildStyle(), this.keybind);
        }
    }
}

