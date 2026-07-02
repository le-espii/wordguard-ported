/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.h2;

import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.DbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.JdbcTemplate;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Schema;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.SqlStatementBuilder;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.h2.H2Schema;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.h2.H2SqlStatementBuilder;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.jdbc.JdbcUtils;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class H2DbSupport
extends DbSupport {
    public H2DbSupport(Connection connection) {
        super(new JdbcTemplate(connection, 12));
    }

    @Override
    public String getDbName() {
        return "h2";
    }

    @Override
    public String getCurrentUserFunction() {
        return "USER()";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected String doGetCurrentSchema() throws SQLException {
        ResultSet resultSet = null;
        String schema = null;
        try {
            resultSet = this.jdbcTemplate.getMetaData().getSchemas();
            while (resultSet.next()) {
                if (!resultSet.getBoolean("IS_DEFAULT")) continue;
                schema = resultSet.getString("TABLE_SCHEM");
            }
        }
        catch (Throwable throwable) {
            JdbcUtils.closeResultSet(resultSet);
            throw throwable;
        }
        JdbcUtils.closeResultSet(resultSet);
        return schema;
    }

    @Override
    protected void doSetCurrentSchema(Schema schema) throws SQLException {
        this.jdbcTemplate.execute("SET SCHEMA " + schema, new Object[0]);
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
        return new H2SqlStatementBuilder();
    }

    @Override
    public String doQuote(String identifier) {
        return "\"" + identifier + "\"";
    }

    @Override
    public Schema getSchema(String name) {
        return new H2Schema(this.jdbcTemplate, this, name);
    }

    @Override
    public boolean catalogIsSchema() {
        return false;
    }
}

