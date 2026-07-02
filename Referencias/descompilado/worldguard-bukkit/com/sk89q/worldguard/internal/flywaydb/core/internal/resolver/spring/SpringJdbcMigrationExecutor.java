/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.jdbc.core.JdbcTemplate
 *  org.springframework.jdbc.datasource.SingleConnectionDataSource
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.resolver.spring;

import com.sk89q.worldguard.internal.flywaydb.core.api.FlywayException;
import com.sk89q.worldguard.internal.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import com.sk89q.worldguard.internal.flywaydb.core.api.resolver.MigrationExecutor;
import java.sql.Connection;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

public class SpringJdbcMigrationExecutor
implements MigrationExecutor {
    private final SpringJdbcMigration springJdbcMigration;

    public SpringJdbcMigrationExecutor(SpringJdbcMigration springJdbcMigration) {
        this.springJdbcMigration = springJdbcMigration;
    }

    @Override
    public void execute(Connection connection) {
        try {
            this.springJdbcMigration.migrate(new JdbcTemplate((DataSource)new SingleConnectionDataSource(connection, true)));
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

