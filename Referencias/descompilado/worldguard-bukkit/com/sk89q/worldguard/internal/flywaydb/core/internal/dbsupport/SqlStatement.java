/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport;

public class SqlStatement {
    private int lineNumber;
    private String sql;

    public SqlStatement(int lineNumber, String sql) {
        this.lineNumber = lineNumber;
        this.sql = sql;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public String getSql() {
        return this.sql;
    }
}

