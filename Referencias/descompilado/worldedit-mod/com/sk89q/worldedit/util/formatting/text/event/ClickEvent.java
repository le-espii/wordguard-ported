/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.sk89q.worldedit.util.formatting.text.event;

import com.sk89q.worldedit.util.formatting.text.util.NameMap;
import com.sk89q.worldedit.util.formatting.text.util.ShadyPines;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class ClickEvent {
    private final Action action;
    private final String value;

    public static @NonNull ClickEvent of(@NonNull Action action, @NonNull String value) {
        return new ClickEvent(action, value);
    }

    public static @NonNull ClickEvent openUrl(@NonNull String url) {
        return ClickEvent.of(Action.OPEN_URL, url);
    }

    public static @NonNull ClickEvent openFile(@NonNull String file) {
        return ClickEvent.of(Action.OPEN_FILE, file);
    }

    public static @NonNull ClickEvent runCommand(@NonNull String command) {
        return ClickEvent.of(Action.RUN_COMMAND, command);
    }

    public static @NonNull ClickEvent suggestCommand(@NonNull String command) {
        return ClickEvent.of(Action.SUGGEST_COMMAND, command);
    }

    public static @NonNull ClickEvent changePage(@NonNull String page) {
        return ClickEvent.of(Action.CHANGE_PAGE, page);
    }

    public static @NonNull ClickEvent changePage(int page) {
        return ClickEvent.changePage(String.valueOf(page));
    }

    public static @NonNull ClickEvent copyToClipboard(@NonNull String text) {
        return ClickEvent.of(Action.COPY_TO_CLIPBOARD, text);
    }

    private ClickEvent(@NonNull Action action, @NonNull String value) {
        this.action = Objects.requireNonNull(action, "action");
        this.value = Objects.requireNonNull(value, "value");
    }

    public @NonNull Action action() {
        return this.action;
    }

    public @NonNull String value() {
        return this.value;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        ClickEvent that = (ClickEvent)other;
        return this.action == that.action && Objects.equals(this.value, that.value);
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
        OPEN_URL("open_url", true),
        OPEN_FILE("open_file", false),
        RUN_COMMAND("run_command", true),
        SUGGEST_COMMAND("suggest_command", true),
        CHANGE_PAGE("change_page", true),
        COPY_TO_CLIPBOARD("copy_to_clipboard", true);

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

