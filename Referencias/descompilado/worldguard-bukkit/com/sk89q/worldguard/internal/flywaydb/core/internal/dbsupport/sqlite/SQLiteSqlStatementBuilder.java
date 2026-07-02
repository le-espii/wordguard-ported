/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.sqlite;

import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Delimiter;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.SqlStatementBuilder;

public class SQLiteSqlStatementBuilder
extends SqlStatementBuilder {
    private boolean insideBeginEndBlock;

    @Override
    protected Delimiter changeDelimiterIfNecessary(String line, Delimiter delimiter) {
        if (line.contains("BEGIN")) {
            this.insideBeginEndBlock = true;
        }
        if (line.endsWith("END;")) {
            this.insideBeginEndBlock = false;
        }
        if (this.insideBeginEndBlock) {
            return null;
        }
        return this.getDefaultDelimiter();
    }
}

