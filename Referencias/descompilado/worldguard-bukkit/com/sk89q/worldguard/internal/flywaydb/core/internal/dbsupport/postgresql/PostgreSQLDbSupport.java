/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.postgresql;

import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.DbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.JdbcTemplate;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Schema;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.SqlStatementBuilder;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.postgresql.PostgreSQLSchema;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.postgresql.PostgreSQLSqlStatementBuilder;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.StringUtils;
import java.sql.Connection;
import java.sql.SQLException;

public class PostgreSQLDbSupport
extends DbSupport {
    public PostgreSQLDbSupport(Connection connection) {
        super(new JdbcTemplate(connection, 0));
    }

    @Override
    public String getDbName() {
        return "postgresql";
    }

    @Override
    public String getCurrentUserFunction() {
        return "current_user";
    }

    @Override
    protected String doGetCurrentSchema() throws SQLException {
        return this.jdbcTemplate.queryForString("SELECT current_schema()", new String[0]);
    }

    @Override
    protected void doSetCurrentSchema(Schema schema) throws SQLException {
        if (schema == null) {
            this.jdbcTemplate.execute("SELECT set_config('search_path', '', false)", new Object[0]);
            return;
        }
        String searchPath = this.jdbcTemplate.queryForString("SHOW search_path", new String[0]);
        if (StringUtils.hasText(searchPath)) {
            this.jdbcTemplate.execute("SET search_path = " + schema + "," + searchPath, new Object[0]);
        } else {
            this.jdbcTemplate.execute("SET search_path = " + schema, new Object[0]);
        }
    }

    @Override
    public boolean supportsDdlTransactions() {
        return true;
    }

    @Override
    public String getBooleanTrue() {
        return "TRUE";
    }

    @Override
    public String getBooleanFalse() {
        return "FALSE";
    }

    @Override
    public SqlStatementBuilder createSqlStatementBuilder() {
        return new PostgreSQLSqlStatementBuilder();
    }

    @Override
    public String doQuote(String identifier) {
        return "\"" + StringUtils.replaceAll(identifier, "\"", "\"\"") + "\"";
    }

    @Override
    public Schema getSchema(String name) {
        return new PostgreSQLSchema(this.jdbcTemplate, this, name);
    }

    @Override
    public boolean catalogIsSchema() {
        return false;
    }
}

