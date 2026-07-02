/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.api.migration;

import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationVersion;

public interface MigrationInfoProvider {
    public MigrationVersion getVersion();

    public String getDescription();
}

