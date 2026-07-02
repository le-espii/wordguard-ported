/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.util.jdbc;

import java.sql.SQLException;

public interface TransactionCallback<T> {
    public T doInTransaction() throws SQLException;
}

