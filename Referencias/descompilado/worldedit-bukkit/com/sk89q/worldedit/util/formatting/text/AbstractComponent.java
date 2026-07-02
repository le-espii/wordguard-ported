/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.sk89q.worldedit.util.formatting.text;

import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.format.Style;
import com.sk89q.worldedit.util.formatting.text.util.ShadyPines;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractComponent
implements Component {
    protected static final List<Component> EMPTY_COMPONENT_LIST = Collections.emptyList();
    protected final List<Component> children;
    protected final Style style;

    static List<Component> unmodifiableCopy(List<? extends Component> list) {
        return list.isEmpty() ? EMPTY_COMPONENT_LIST : Collections.unmodifiableList(new ArrayList<Component>(list));
    }

    protected AbstractComponent(@NonNull List<Component> children, @NonNull Style style) {
        this.children = AbstractComponent.unmodifiableCopy(children);
        this.style = style;
    }

    @Override
    public @NonNull List<Component> children() {
        return this.children;
    }

    @Override
    public @NonNull Style style() {
        return this.style;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AbstractComponent)) {
            return false;
        }
        return this.equals((AbstractComponent)other);
    }

    protected boolean equals(@NonNull AbstractComponent that) {
        return Objects.equals(this.children, that.children) && Objects.equals(this.style, that.style);
    }

    public int hashCode() {
        int result = this.children.hashCode();
        result = 31 * result + this.style.hashCode();
        return result;
    }

    public String toString() {
        return ShadyPines.toString((Object)this, map -> {
            this.populateToString((Map<String, Object>)map);
            map.put("children", this.children);
            map.put("style", this.style);
        });
    }

    protected void populateToString(@NonNull Map<String, Object> builder) {
    }
}

