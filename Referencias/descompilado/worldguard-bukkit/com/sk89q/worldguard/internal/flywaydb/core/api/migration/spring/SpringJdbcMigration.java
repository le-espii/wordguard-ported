/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.jdbc.core.JdbcTemplate
 */
package com.sk89q.worldguard.internal.flywaydb.core.api.migration.spring;

import org.springframework.jdbc.core.JdbcTemplate;

public interface SpringJdbcMigration {
    public void migrate(JdbcTemplate var1) throws Exception;
}

