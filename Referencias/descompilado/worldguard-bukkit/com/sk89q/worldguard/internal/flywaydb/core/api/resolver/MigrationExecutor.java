/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.api.resolver;

import java.sql.Connection;
import java.sql.SQLException;

public interface MigrationExecutor {
    public void execute(Connection var1) throws SQLException;

    public boolean executeInTransaction();
}

