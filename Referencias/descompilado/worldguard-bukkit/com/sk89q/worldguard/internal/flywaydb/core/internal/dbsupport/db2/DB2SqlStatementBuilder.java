/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.db2;

import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Delimiter;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.SqlStatementBuilder;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.StringUtils;

public class DB2SqlStatementBuilder
extends SqlStatementBuilder {
    private boolean insideBeginEndBlock;
    private String statementStart = "";

    @Override
    protected Delimiter changeDelimiterIfNecessary(String line, Delimiter delimiter) {
        if (StringUtils.countOccurrencesOf(this.statementStart, " ") < 4) {
            this.statementStart = this.statementStart + line;
            this.statementStart = this.statementStart + " ";
        }
        if (this.statementStart.startsWith("CREATE FUNCTION") || this.statementStart.startsWith("CREATE PROCEDURE") || this.statementStart.startsWith("CREATE TRIGGER") || this.statementStart.startsWith("CREATE OR REPLACE FUNCTION") || this.statementStart.startsWith("CREATE OR REPLACE PROCEDURE") || this.statementStart.startsWith("CREATE OR REPLACE TRIGGER")) {
            if (line.startsWith("BEGIN")) {
                this.insideBeginEndBlock = true;
            }
            if (line.endsWith("END;")) {
                this.insideBeginEndBlock = false;
            }
        }
        if (this.insideBeginEndBlock) {
            return null;
        }
        return this.getDefaultDelimiter();
    }
}

