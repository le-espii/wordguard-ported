/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.derby;

import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.SqlStatementBuilder;

public class DerbySqlStatementBuilder
extends SqlStatementBuilder {
    @Override
    protected String extractAlternateOpenQuote(String token) {
        if (token.startsWith("$$")) {
            return "$$";
        }
        return null;
    }
}

