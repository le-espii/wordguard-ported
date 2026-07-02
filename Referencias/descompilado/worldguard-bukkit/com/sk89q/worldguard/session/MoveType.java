/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.session;

public enum MoveType {
    RESPAWN(false, true),
    EMBARK(true, false),
    MOVE(true, false),
    GLIDE(true, false),
    SWIM(true, false),
    TELEPORT(true, true),
    RIDE(true, false),
    OTHER_NON_CANCELLABLE(false, false),
    OTHER_CANCELLABLE(true, false);

    private final boolean cancellable;
    private final boolean teleport;

    private MoveType(boolean cancellable, boolean teleport) {
        this.cancellable = cancellable;
        this.teleport = teleport;
    }

    public boolean isCancellable() {
        return this.cancellable;
    }

    public boolean isTeleport() {
        return this.teleport;
    }
}

