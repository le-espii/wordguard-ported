/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport;

import com.sk89q.worldguard.internal.flywaydb.core.api.FlywayException;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.DbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Function;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.JdbcTemplate;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Table;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Type;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.jdbc.JdbcUtils;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public abstract class Schema {
    protected final JdbcTemplate jdbcTemplate;
    protected final DbSupport dbSupport;
    protected final String name;

    public Schema(JdbcTemplate jdbcTemplate, DbSupport dbSupport, String name) {
        this.jdbcTemplate = jdbcTemplate;
        this.dbSupport = dbSupport;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public boolean exists() {
        try {
            return this.doExists();
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to check whether schema " + this + " exists", e);
        }
    }

    protected abstract boolean doExists() throws SQLException;

    public boolean empty() {
        try {
            return this.doEmpty();
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to check whether schema " + this + " is empty", e);
        }
    }

    protected abstract boolean doEmpty() throws SQLException;

    public void create() {
        try {
            this.doCreate();
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to create schema " + this, e);
        }
    }

    protected abstract void doCreate() throws SQLException;

    public void drop() {
        try {
            this.doDrop();
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to drop schema " + this, e);
        }
    }

    protected abstract void doDrop() throws SQLException;

    public void clean() {
        try {
            this.doClean();
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to clean schema " + this, e);
        }
    }

    protected abstract void doClean() throws SQLException;

    public Table[] allTables() {
        try {
            return this.doAllTables();
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to retrieve all tables in schema " + this, e);
        }
    }

    protected abstract Table[] doAllTables() throws SQLException;

    public final Type[] allTypes() {
        Type[] typeArray;
        ResultSet resultSet = null;
        try {
            resultSet = this.jdbcTemplate.getMetaData().getUDTs(null, this.name, null, null);
            ArrayList<Type> types = new ArrayList<Type>();
            while (resultSet.next()) {
                types.add(this.getType(resultSet.getString("TYPE_NAME")));
            }
            typeArray = types.toArray(new Type[types.size()]);
        }
        catch (SQLException e) {
            try {
                throw new FlywayException("Unable to retrieve all types in schema " + this, e);
            }
            catch (Throwable throwable) {
                JdbcUtils.closeResultSet(resultSet);
                throw throwable;
            }
        }
        JdbcUtils.closeResultSet(resultSet);
        return typeArray;
    }

    protected Type getType(String typeName) {
        return null;
    }

    public abstract Table getTable(String var1);

    public Function getFunction(String functionName, String ... args) {
        throw new UnsupportedOperationException("getFunction()");
    }

    public final Function[] allFunctions() {
        try {
            return this.doAllFunctions();
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to retrieve all functions in schema " + this, e);
        }
    }

    protected Function[] doAllFunctions() throws SQLException {
        return new Function[0];
    }

    public String toString() {
        return this.dbSupport.quote(this.name);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Schema schema = (Schema)o;
        return this.name.equals(schema.name);
    }

    public int hashCode() {
        return this.name.hashCode();
    }
}

