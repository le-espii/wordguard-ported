/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.util.profile.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class UUIDs {
    private static final Pattern DASHLESS_PATTERN = Pattern.compile("^([A-Fa-f0-9]{8})([A-Fa-f0-9]{4})([A-Fa-f0-9]{4})([A-Fa-f0-9]{4})([A-Fa-f0-9]{12})$");

    private UUIDs() {
    }

    public static String addDashes(String uuid) {
        Matcher matcher = DASHLESS_PATTERN.matcher(uuid = uuid.replace("-", ""));
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid UUID format");
        }
        return matcher.replaceAll("$1-$2-$3-$4-$5");
    }

    public static String stripDashes(String uuid) {
        Matcher matcher = DASHLESS_PATTERN.matcher(uuid = uuid.replace("-", ""));
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid UUID format");
        }
        return uuid;
    }
}

