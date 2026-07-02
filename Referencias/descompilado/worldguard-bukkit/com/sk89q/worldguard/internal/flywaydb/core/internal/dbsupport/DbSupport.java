/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport;

import com.sk89q.worldguard.internal.flywaydb.core.api.FlywayException;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.JdbcTemplate;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Schema;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.SqlStatementBuilder;
import java.sql.SQLException;

public abstract class DbSupport {
    protected final JdbcTemplate jdbcTemplate;

    public DbSupport(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public JdbcTemplate getJdbcTemplate() {
        return this.jdbcTemplate;
    }

    public abstract Schema getSchema(String var1);

    public abstract SqlStatementBuilder createSqlStatementBuilder();

    public abstract String getDbName();

    public Schema getCurrentSchema() {
        try {
            String schemaName = this.doGetCurrentSchema();
            if (schemaName == null) {
                return null;
            }
            return this.getSchema(schemaName);
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to retrieve the current schema for the connection", e);
        }
    }

    protected abstract String doGetCurrentSchema() throws SQLException;

    public void setCurrentSchema(Schema schema) {
        try {
            this.doSetCurrentSchema(schema);
        }
        catch (SQLException e) {
            throw new FlywayException("Error setting current schema to " + schema, e);
        }
    }

    protected abstract void doSetCurrentSchema(Schema var1) throws SQLException;

    public abstract String getCurrentUserFunction();

    public abstract boolean supportsDdlTransactions();

    public abstract String getBooleanTrue();

    public abstract String getBooleanFalse();

    public String quote(String ... identifiers) {
        String result = "";
        boolean first = true;
        for (String identifier : identifiers) {
            if (!first) {
                result = result + ".";
            }
            first = false;
            result = result + this.doQuote(identifier);
        }
        return result;
    }

    protected abstract String doQuote(String var1);

    public abstract boolean catalogIsSchema();
}

