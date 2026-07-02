/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.api.migration.jdbc;

import java.sql.Connection;

public interface JdbcMigration {
    public void migrate(Connection var1) throws Exception;
}

