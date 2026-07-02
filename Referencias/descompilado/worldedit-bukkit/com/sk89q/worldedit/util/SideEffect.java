/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.util;

import java.util.Locale;

public enum SideEffect {
    LIGHTING(State.ON, true),
    NEIGHBORS(State.ON, true),
    UPDATE(State.ON, true),
    VALIDATION(State.OFF, true),
    ENTITY_AI(State.OFF, true),
    EVENTS(State.OFF, true),
    POI_UPDATE(State.ON, false),
    NETWORK(State.ON, false);

    private final String displayName = "worldedit.sideeffect." + this.name().toLowerCase(Locale.US);
    private final String description = "worldedit.sideeffect." + this.name().toLowerCase(Locale.US) + ".description";
    private final State defaultValue;
    private final boolean exposed;

    private SideEffect(State defaultValue, boolean exposed) {
        this.defaultValue = defaultValue;
        this.exposed = exposed;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getDescription() {
        return this.description;
    }

    public State getDefaultValue() {
        return this.defaultValue;
    }

    public boolean isExposed() {
        return this.exposed;
    }

    public static enum State {
        OFF,
        ON,
        DELAYED;

        private final String displayName = "worldedit.sideeffect.state." + this.name().toLowerCase(Locale.US);

        public String getDisplayName() {
            return this.displayName;
        }
    }
}

