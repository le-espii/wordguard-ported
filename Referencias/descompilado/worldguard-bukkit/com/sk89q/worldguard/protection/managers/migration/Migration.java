/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.protection.managers.migration;

import com.sk89q.worldguard.protection.managers.migration.MigrationException;

public interface Migration {
    public void migrate() throws MigrationException;
}

