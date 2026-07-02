/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.mysql;

import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Delimiter;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.SqlStatementBuilder;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.StringUtils;
import java.util.regex.Pattern;

public class MySQLSqlStatementBuilder
extends SqlStatementBuilder {
    private static final String DELIMITER_KEYWORD = "DELIMITER";
    private final String[] charSets = new String[]{"ARMSCII8", "ASCII", "BIG5", "BINARY", "CP1250", "CP1251", "CP1256", "CP1257", "CP850", "CP852", "CP866", "CP932", "DEC8", "EUCJPMS", "EUCKR", "GB2312", "GBK", "GEOSTD8", "GREEK", "HEBREW", "HP8", "KEYBCS2", "KOI8R", "KOI8U", "LATIN1", "LATIN2", "LATIN5", "LATIN7", "MACCE", "MACROMAN", "SJIS", "SWE7", "TIS620", "UCS2", "UJIS", "UTF8"};
    boolean isInMultiLineCommentDirective = false;

    @Override
    public Delimiter extractNewDelimiterFromLine(String line) {
        if (line.toUpperCase().startsWith(DELIMITER_KEYWORD)) {
            return new Delimiter(line.substring(DELIMITER_KEYWORD.length()).trim(), false);
        }
        return null;
    }

    @Override
    protected Delimiter changeDelimiterIfNecessary(String line, Delimiter delimiter) {
        if (line.toUpperCase().startsWith(DELIMITER_KEYWORD)) {
            return new Delimiter(line.substring(DELIMITER_KEYWORD.length()).trim(), false);
        }
        return delimiter;
    }

    @Override
    public boolean isCommentDirective(String line) {
        if (line.matches("^" + Pattern.quote("/*!") + "\\d{5} .*" + Pattern.quote("*/") + "\\s*;?")) {
            return true;
        }
        if (this.isInMultiLineCommentDirective && line.matches(".*" + Pattern.quote("*/") + "\\s*;?")) {
            this.isInMultiLineCommentDirective = false;
            return true;
        }
        if (line.matches("^" + Pattern.quote("/*!") + "\\d{5} .*")) {
            this.isInMultiLineCommentDirective = true;
            return true;
        }
        return this.isInMultiLineCommentDirective;
    }

    @Override
    public boolean isSingleLineComment(String line) {
        return line.startsWith("--") || line.startsWith("#");
    }

    @Override
    protected String removeEscapedQuotes(String token) {
        String noEscapedBackslashes = StringUtils.replaceAll(token, "\\\\", "");
        String noBackslashEscapes = StringUtils.replaceAll(StringUtils.replaceAll(noEscapedBackslashes, "\\'", ""), "\\\"", "");
        return StringUtils.replaceAll(noBackslashEscapes, "''", "");
    }

    @Override
    protected String removeCharsetCasting(String token) {
        if (token.startsWith("_")) {
            for (String charSet : this.charSets) {
                String cast = "_" + charSet;
                if (!token.startsWith(cast)) continue;
                return token.substring(cast.length());
            }
        }
        return token;
    }

    @Override
    protected String extractAlternateOpenQuote(String token) {
        if (token.startsWith("\"")) {
            return "\"";
        }
        if (token.startsWith("B'") && token.length() > 2) {
            return "B'";
        }
        if (token.startsWith("X'") && token.length() > 2) {
            return "X'";
        }
        return null;
    }

    @Override
    protected String computeAlternateCloseQuote(String openQuote) {
        if ("B'".equals(openQuote) || "X'".equals(openQuote)) {
            return "'";
        }
        return openQuote;
    }
}

