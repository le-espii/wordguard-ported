/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.oracle;

import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Delimiter;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.SqlStatementBuilder;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.StringUtils;

public class OracleSqlStatementBuilder
extends SqlStatementBuilder {
    private static final Delimiter PLSQL_DELIMITER = new Delimiter("/", true);
    private String statementStart = "";

    @Override
    protected Delimiter changeDelimiterIfNecessary(String line, Delimiter delimiter) {
        if (line.matches("DECLARE|DECLARE\\s.*") || line.matches("BEGIN|BEGIN\\s.*")) {
            return PLSQL_DELIMITER;
        }
        if (StringUtils.countOccurrencesOf(this.statementStart, " ") < 4) {
            this.statementStart = this.statementStart + line;
            this.statementStart = this.statementStart + " ";
            this.statementStart = this.statementStart.replaceAll("\\s+", " ");
        }
        if (this.statementStart.startsWith("CREATE FUNCTION") || this.statementStart.startsWith("CREATE PROCEDURE") || this.statementStart.startsWith("CREATE PACKAGE") || this.statementStart.startsWith("CREATE TYPE") || this.statementStart.startsWith("CREATE TRIGGER") || this.statementStart.startsWith("CREATE OR REPLACE FUNCTION") || this.statementStart.startsWith("CREATE OR REPLACE PROCEDURE") || this.statementStart.startsWith("CREATE OR REPLACE PACKAGE") || this.statementStart.startsWith("CREATE OR REPLACE TYPE") || this.statementStart.startsWith("CREATE OR REPLACE TRIGGER")) {
            return PLSQL_DELIMITER;
        }
        return delimiter;
    }

    @Override
    protected String removeCharsetCasting(String token) {
        if (token.startsWith("N'")) {
            return token.substring(1);
        }
        return token;
    }

    @Override
    protected String simplifyLine(String line) {
        String simplifiedQQuotes = StringUtils.replaceAll(StringUtils.replaceAll(line, "q'(", "q'["), ")'", "]'");
        return super.simplifyLine(simplifiedQQuotes);
    }

    @Override
    protected String extractAlternateOpenQuote(String token) {
        if (token.startsWith("Q'") && token.length() >= 3) {
            return token.substring(0, 3);
        }
        return null;
    }

    @Override
    protected String computeAlternateCloseQuote(String openQuote) {
        char specialChar = openQuote.charAt(2);
        switch (specialChar) {
            case '[': {
                return "]'";
            }
            case '(': {
                return ")'";
            }
            case '{': {
                return "}'";
            }
            case '<': {
                return ">'";
            }
        }
        return specialChar + "'";
    }
}

