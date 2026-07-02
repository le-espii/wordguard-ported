/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.postgresql;

import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.SqlStatementBuilder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostgreSQLSqlStatementBuilder
extends SqlStatementBuilder {
    static final String DOLLAR_QUOTE_REGEX = "\\$[A-Za-z0-9_]*\\$.*";

    @Override
    protected String extractAlternateOpenQuote(String token) {
        Matcher matcher = Pattern.compile(DOLLAR_QUOTE_REGEX).matcher(token);
        if (matcher.find()) {
            return token.substring(matcher.start(), matcher.end());
        }
        return null;
    }
}

