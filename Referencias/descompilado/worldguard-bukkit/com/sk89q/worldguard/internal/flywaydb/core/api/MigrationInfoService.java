/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.api;

import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationInfo;

public interface MigrationInfoService {
    public MigrationInfo[] all();

    public MigrationInfo current();

    public MigrationInfo[] pending();

    public MigrationInfo[] applied();
}

