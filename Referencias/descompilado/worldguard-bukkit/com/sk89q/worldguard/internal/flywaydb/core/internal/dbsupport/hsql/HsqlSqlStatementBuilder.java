/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.hsql;

import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Delimiter;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.SqlStatementBuilder;

public class HsqlSqlStatementBuilder
extends SqlStatementBuilder {
    private boolean insideAtomicBlock;

    @Override
    protected Delimiter changeDelimiterIfNecessary(String line, Delimiter delimiter) {
        if (line.contains("BEGIN ATOMIC")) {
            this.insideAtomicBlock = true;
        }
        if (line.endsWith("END;")) {
            this.insideAtomicBlock = false;
        }
        if (this.insideAtomicBlock) {
            return null;
        }
        return this.getDefaultDelimiter();
    }
}

