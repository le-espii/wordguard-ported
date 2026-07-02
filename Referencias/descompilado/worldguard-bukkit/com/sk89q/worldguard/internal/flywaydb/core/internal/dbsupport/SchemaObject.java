/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport;

import com.sk89q.worldguard.internal.flywaydb.core.api.FlywayException;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.DbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.JdbcTemplate;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Schema;
import java.sql.SQLException;

public abstract class SchemaObject {
    protected final JdbcTemplate jdbcTemplate;
    protected final DbSupport dbSupport;
    protected final Schema schema;
    protected final String name;

    public SchemaObject(JdbcTemplate jdbcTemplate, DbSupport dbSupport, Schema schema, String name) {
        this.name = name;
        this.jdbcTemplate = jdbcTemplate;
        this.dbSupport = dbSupport;
        this.schema = schema;
    }

    public final Schema getSchema() {
        return this.schema;
    }

    public final String getName() {
        return this.name;
    }

    public final void drop() {
        try {
            this.doDrop();
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to drop " + this, e);
        }
    }

    protected abstract void doDrop() throws SQLException;

    public String toString() {
        return this.dbSupport.quote(this.schema.getName(), this.name);
    }
}

