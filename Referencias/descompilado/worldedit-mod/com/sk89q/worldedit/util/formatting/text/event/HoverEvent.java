/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.sk89q.worldedit.util.formatting.text.event;

import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.util.NameMap;
import com.sk89q.worldedit.util.formatting.text.util.ShadyPines;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class HoverEvent {
    private final Action action;
    private final Component value;

    public static @NonNull HoverEvent of(@NonNull Action action, @NonNull Component value) {
        return new HoverEvent(action, value);
    }

    public static @NonNull HoverEvent showText(@NonNull Component text) {
        return HoverEvent.of(Action.SHOW_TEXT, text);
    }

    public static @NonNull HoverEvent showItem(@NonNull Component item) {
        return HoverEvent.of(Action.SHOW_ITEM, item);
    }

    public static @NonNull HoverEvent showEntity(@NonNull Component entity) {
        return HoverEvent.of(Action.SHOW_ENTITY, entity);
    }

    private HoverEvent(@NonNull Action action, @NonNull Component value) {
        this.action = Objects.requireNonNull(action, "action");
        this.value = Objects.requireNonNull(value, "value");
    }

    public @NonNull Action action() {
        return this.action;
    }

    public @NonNull Component value() {
        return this.value;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        HoverEvent that = (HoverEvent)other;
        return this.action == that.action && this.value.equals(that.value);
    }

    public int hashCode() {
        int result = this.action.hashCode();
        result = 31 * result + this.value.hashCode();
        return result;
    }

    public String toString() {
        return ShadyPines.toString((Object)this, map -> {
            map.put("action", this.action);
            map.put("value", this.value);
        });
    }

    public static enum Action {
        SHOW_TEXT("show_text", true),
        SHOW_ITEM("show_item", true),
        SHOW_ENTITY("show_entity", true);

        public static final NameMap<Action> NAMES;
        private final String name;
        private final boolean readable;

        private Action(String name, boolean readable) {
            this.name = name;
            this.readable = readable;
        }

        public boolean readable() {
            return this.readable;
        }

        public @NonNull String toString() {
            return this.name;
        }

        static {
            NAMES = NameMap.create(Action.class, constant -> constant.name);
        }
    }
}

