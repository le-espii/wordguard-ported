/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.util.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class StatementUtils {
    private StatementUtils() {
    }

    public static String preparePlaceHolders(int length) {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        while (i < length) {
            builder.append("?");
            if (++i >= length) continue;
            builder.append(",");
        }
        return builder.toString();
    }

    public static void setValues(PreparedStatement preparedStatement, String ... values) throws SQLException {
        for (int i = 0; i < values.length; ++i) {
            preparedStatement.setString(i + 1, values[i]);
        }
    }
}

