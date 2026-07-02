/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport;

import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.DbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.JdbcTemplate;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Schema;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.SchemaObject;

public abstract class Function
extends SchemaObject {
    protected String[] args;

    public Function(JdbcTemplate jdbcTemplate, DbSupport dbSupport, Schema schema, String name, String ... args) {
        super(jdbcTemplate, dbSupport, schema, name);
        this.args = args;
    }
}

