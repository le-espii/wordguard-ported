/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.sqlserver;

import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Delimiter;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.SqlStatementBuilder;

public class SQLServerSqlStatementBuilder
extends SqlStatementBuilder {
    @Override
    protected Delimiter getDefaultDelimiter() {
        return new Delimiter("GO", true);
    }

    @Override
    protected String extractAlternateOpenQuote(String token) {
        if (token.startsWith("N'")) {
            return "N'";
        }
        return null;
    }

    @Override
    protected String computeAlternateCloseQuote(String openQuote) {
        return "'";
    }
}

