/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.util.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RowMapper<T> {
    public T mapRow(ResultSet var1) throws SQLException;
}

