/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.mysql;

import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.DbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.JdbcTemplate;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Schema;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.SqlStatementBuilder;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.mysql.MySQLSchema;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.mysql.MySQLSqlStatementBuilder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class MySQLDbSupport
extends DbSupport {
    public MySQLDbSupport(Connection connection) {
        super(new JdbcTemplate(connection, 12));
    }

    @Override
    public String getDbName() {
        return "mysql";
    }

    @Override
    public String getCurrentUserFunction() {
        return "SUBSTRING_INDEX(USER(),'@',1)";
    }

    @Override
    protected String doGetCurrentSchema() throws SQLException {
        return this.jdbcTemplate.getConnection().getCatalog();
    }

    @Override
    protected void doSetCurrentSchema(Schema schema) throws SQLException {
        if ("".equals(schema.getName())) {
            String newDb = this.quote(UUID.randomUUID().toString());
            this.jdbcTemplate.execute("CREATE SCHEMA " + newDb, new Object[0]);
            this.jdbcTemplate.execute("USE " + newDb, new Object[0]);
            this.jdbcTemplate.execute("DROP SCHEMA " + newDb, new Object[0]);
        } else {
            this.jdbcTemplate.execute("USE " + schema, new Object[0]);
        }
    }

    @Override
    public boolean supportsDdlTransactions() {
        return false;
    }

    @Override
    public String getBooleanTrue() {
        return "1";
    }

    @Override
    public String getBooleanFalse() {
        return "0";
    }

    @Override
    public SqlStatementBuilder createSqlStatementBuilder() {
        return new MySQLSqlStatementBuilder();
    }

    @Override
    public String doQuote(String identifier) {
        return "`" + identifier + "`";
    }

    @Override
    public Schema getSchema(String name) {
        return new MySQLSchema(this.jdbcTemplate, this, name);
    }

    @Override
    public boolean catalogIsSchema() {
        return true;
    }
}

