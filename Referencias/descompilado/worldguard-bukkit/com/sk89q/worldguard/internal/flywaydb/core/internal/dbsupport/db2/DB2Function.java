/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.db2;

import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.DbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Function;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.JdbcTemplate;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Schema;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.StringUtils;
import java.sql.SQLException;

public class DB2Function
extends Function {
    public DB2Function(JdbcTemplate jdbcTemplate, DbSupport dbSupport, Schema schema, String name, String ... args) {
        super(jdbcTemplate, dbSupport, schema, name, args);
    }

    @Override
    protected void doDrop() throws SQLException {
        this.jdbcTemplate.execute("DROP FUNCTION " + this.dbSupport.quote(this.schema.getName(), this.name) + "(" + StringUtils.arrayToCommaDelimitedString(this.args) + ")", new Object[0]);
    }

    @Override
    public String toString() {
        return super.toString() + "(" + StringUtils.arrayToCommaDelimitedString(this.args) + ")";
    }
}

