/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.resolver.jdbc;

import com.sk89q.worldguard.internal.flywaydb.core.api.FlywayException;
import com.sk89q.worldguard.internal.flywaydb.core.api.migration.jdbc.JdbcMigration;
import com.sk89q.worldguard.internal.flywaydb.core.api.resolver.MigrationExecutor;
import java.sql.Connection;

public class JdbcMigrationExecutor
implements MigrationExecutor {
    private final JdbcMigration jdbcMigration;

    public JdbcMigrationExecutor(JdbcMigration jdbcMigration) {
        this.jdbcMigration = jdbcMigration;
    }

    @Override
    public void execute(Connection connection) {
        try {
            this.jdbcMigration.migrate(connection);
        }
        catch (Exception e) {
            throw new FlywayException("Migration failed !", e);
        }
    }

    @Override
    public boolean executeInTransaction() {
        return true;
    }
}

