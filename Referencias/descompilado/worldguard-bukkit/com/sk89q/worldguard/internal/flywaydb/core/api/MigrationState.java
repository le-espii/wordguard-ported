/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.api;

public enum MigrationState {
    PENDING("Pending", true, false, false),
    ABOVE_TARGET(">Target", true, false, false),
    PREINIT("PreInit", true, false, false),
    IGNORED("Ignored", true, false, false),
    MISSING_SUCCESS("Missing", false, true, false),
    MISSING_FAILED("MisFail", false, true, true),
    SUCCESS("Success", true, true, false),
    FAILED("Failed", true, true, true),
    OUT_OF_ORDER("OutOrder", true, true, false),
    FUTURE_SUCCESS("Future", false, true, false),
    FUTURE_FAILED("FutFail", false, true, true);

    private final String displayName;
    private final boolean resolved;
    private final boolean applied;
    private final boolean failed;

    private MigrationState(String displayName, boolean resolved, boolean applied, boolean failed) {
        this.displayName = displayName;
        this.resolved = resolved;
        this.applied = applied;
        this.failed = failed;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public boolean isApplied() {
        return this.applied;
    }

    public boolean isResolved() {
        return this.resolved;
    }

    public boolean isFailed() {
        return this.failed;
    }
}

