/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.api;

import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationState;
import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationType;
import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationVersion;
import java.util.Date;

public interface MigrationInfo
extends Comparable<MigrationInfo> {
    public MigrationType getType();

    public Integer getChecksum();

    public MigrationVersion getVersion();

    public String getDescription();

    public String getScript();

    public MigrationState getState();

    public Date getInstalledOn();

    public Integer getExecutionTime();
}

