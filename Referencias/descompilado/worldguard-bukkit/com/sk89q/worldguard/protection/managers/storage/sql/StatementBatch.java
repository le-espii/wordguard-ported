/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.protection.managers.storage.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

class StatementBatch {
    public static final int MAX_BATCH_SIZE = 100;
    private final PreparedStatement stmt;
    private final int batchSize;
    private int count = 0;

    StatementBatch(PreparedStatement stmt, int batchSize) {
        this.stmt = stmt;
        this.batchSize = batchSize;
    }

    public void addBatch() throws SQLException {
        this.stmt.addBatch();
        ++this.count;
        if (this.count > this.batchSize) {
            this.stmt.executeBatch();
            this.count = 0;
        }
    }

    public void executeRemaining() throws SQLException {
        if (this.count > 0) {
            this.count = 0;
            this.stmt.executeBatch();
        }
    }
}

