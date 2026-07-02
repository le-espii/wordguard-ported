/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.api.resolver;

import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationType;
import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationVersion;
import com.sk89q.worldguard.internal.flywaydb.core.api.resolver.MigrationExecutor;

public interface ResolvedMigration {
    public MigrationVersion getVersion();

    public String getDescription();

    public String getScript();

    public Integer getChecksum();

    public MigrationType getType();

    public String getPhysicalLocation();

    public MigrationExecutor getExecutor();
}

