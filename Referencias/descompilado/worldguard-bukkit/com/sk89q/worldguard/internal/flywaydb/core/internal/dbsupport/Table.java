/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport;

import com.sk89q.worldguard.internal.flywaydb.core.api.FlywayException;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.DbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.JdbcTemplate;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Schema;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.SchemaObject;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.jdbc.JdbcUtils;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.Log;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.LogFactory;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class Table
extends SchemaObject {
    private static final Log LOG = LogFactory.getLog(Table.class);

    public Table(JdbcTemplate jdbcTemplate, DbSupport dbSupport, Schema schema, String name) {
        super(jdbcTemplate, dbSupport, schema, name);
    }

    public boolean exists() {
        try {
            return this.doExists();
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to check whether table " + this + " exists", e);
        }
    }

    protected abstract boolean doExists() throws SQLException;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean exists(Schema catalog, Schema schema, String table, String ... tableTypes) throws SQLException {
        boolean found;
        String[] types = tableTypes;
        if (types.length == 0) {
            types = null;
        }
        ResultSet resultSet = null;
        try {
            resultSet = this.jdbcTemplate.getMetaData().getTables(catalog == null ? null : catalog.getName(), schema == null ? null : schema.getName(), table, types);
            found = resultSet.next();
        }
        catch (Throwable throwable) {
            JdbcUtils.closeResultSet(resultSet);
            throw throwable;
        }
        JdbcUtils.closeResultSet(resultSet);
        return found;
    }

    public boolean hasPrimaryKey() {
        boolean found;
        ResultSet resultSet = null;
        try {
            resultSet = this.dbSupport.catalogIsSchema() ? this.jdbcTemplate.getMetaData().getPrimaryKeys(this.schema.getName(), null, this.name) : this.jdbcTemplate.getMetaData().getPrimaryKeys(null, this.schema.getName(), this.name);
            found = resultSet.next();
        }
        catch (SQLException e) {
            try {
                throw new FlywayException("Unable to check whether table " + this + " has a primary key", e);
            }
            catch (Throwable throwable) {
                JdbcUtils.closeResultSet(resultSet);
                throw throwable;
            }
        }
        JdbcUtils.closeResultSet(resultSet);
        return found;
    }

    public boolean hasColumn(String column) {
        boolean found;
        ResultSet resultSet = null;
        try {
            resultSet = this.dbSupport.catalogIsSchema() ? this.jdbcTemplate.getMetaData().getColumns(this.schema.getName(), null, this.name, column) : this.jdbcTemplate.getMetaData().getColumns(null, this.schema.getName(), this.name, column);
            found = resultSet.next();
        }
        catch (SQLException e) {
            try {
                throw new FlywayException("Unable to check whether table " + this + " has a column named " + column, e);
            }
            catch (Throwable throwable) {
                JdbcUtils.closeResultSet(resultSet);
                throw throwable;
            }
        }
        JdbcUtils.closeResultSet(resultSet);
        return found;
    }

    public int getColumnSize(String column) {
        int columnSize;
        ResultSet resultSet = null;
        try {
            resultSet = this.dbSupport.catalogIsSchema() ? this.jdbcTemplate.getMetaData().getColumns(this.schema.getName(), null, this.name, column) : this.jdbcTemplate.getMetaData().getColumns(null, this.schema.getName(), this.name, column);
            resultSet.next();
            columnSize = resultSet.getInt("COLUMN_SIZE");
        }
        catch (SQLException e) {
            try {
                throw new FlywayException("Unable to check the size of column " + column + " in table " + this, e);
            }
            catch (Throwable throwable) {
                JdbcUtils.closeResultSet(resultSet);
                throw throwable;
            }
        }
        JdbcUtils.closeResultSet(resultSet);
        return columnSize;
    }

    public void lock() {
        try {
            LOG.debug("Locking table " + this + "...");
            this.doLock();
            LOG.debug("Lock acquired for table " + this);
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to lock table " + this, e);
        }
    }

    protected abstract void doLock() throws SQLException;
}

