/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.linbus.format.snbt.impl;

public final class Elusion {
    public static boolean isSafeCharacter(char c) {
        return 'A' <= c && c <= 'Z' || 'a' <= c && c <= 'z' || '0' <= c && c <= '9' || c == '_' || c == '.' || c == '+' || c == '-';
    }

    public static CharSequence escapeIfNeeded(String s) {
        boolean totallySafe = true;
        int singleCharCount = 0;
        int doubleCharCount = 0;
        int backslashCount = 0;
        block5: for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (totallySafe && Elusion.isSafeCharacter(c)) continue;
            totallySafe = false;
            switch (c) {
                case '\'': {
                    ++singleCharCount;
                    continue block5;
                }
                case '\"': {
                    ++doubleCharCount;
                    continue block5;
                }
                case '\\': {
                    ++backslashCount;
                }
            }
        }
        if (totallySafe) {
            return s;
        }
        int leastCharCount = Math.min(singleCharCount, doubleCharCount);
        StringBuilder builder = new StringBuilder(s.length() + backslashCount + leastCharCount + 2);
        char quoteChar = leastCharCount == singleCharCount ? (char)'\'' : '\"';
        builder.append(quoteChar);
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (c == '\\' || c == quoteChar) {
                builder.append('\\');
            }
            builder.append(c);
        }
        builder.append(quoteChar);
        return builder;
    }

    private Elusion() {
    }
}

