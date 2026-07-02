/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.api;

import com.sk89q.worldguard.internal.flywaydb.core.api.FlywayException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class MigrationVersion
implements Comparable<MigrationVersion> {
    public static final MigrationVersion EMPTY = new MigrationVersion(null, "<< Empty Schema >>");
    public static final MigrationVersion LATEST = new MigrationVersion(Long.MAX_VALUE, "<< Latest Version >>");
    private static Pattern splitPattern = Pattern.compile("\\.(?=\\d)");
    private final List<Long> versionParts;
    private final String displayText;

    public static MigrationVersion fromVersion(String version) {
        if (LATEST.getVersion().equals(version)) {
            return LATEST;
        }
        if (version == null) {
            return EMPTY;
        }
        return new MigrationVersion(version);
    }

    private MigrationVersion(String version) {
        String normalizedVersion = version.replace('_', '.');
        this.versionParts = this.tokenizeToLongs(normalizedVersion);
        this.displayText = normalizedVersion;
    }

    private MigrationVersion(Long version, String displayText) {
        this.versionParts = new ArrayList<Long>();
        this.versionParts.add(version);
        this.displayText = displayText;
    }

    public String toString() {
        return this.displayText;
    }

    public String getVersion() {
        if (this.equals(EMPTY)) {
            return null;
        }
        if (this.equals(LATEST)) {
            return Long.toString(Long.MAX_VALUE);
        }
        return this.displayText;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MigrationVersion version1 = (MigrationVersion)o;
        return this.compareTo(version1) == 0;
    }

    public int hashCode() {
        return this.versionParts == null ? 0 : this.versionParts.hashCode();
    }

    @Override
    public int compareTo(MigrationVersion o) {
        if (o == null) {
            return 1;
        }
        if (this == EMPTY) {
            return o == EMPTY ? 0 : Integer.MIN_VALUE;
        }
        if (this == LATEST) {
            return o == LATEST ? 0 : Integer.MAX_VALUE;
        }
        if (o == EMPTY) {
            return Integer.MAX_VALUE;
        }
        if (o == LATEST) {
            return Integer.MIN_VALUE;
        }
        List<Long> elements1 = this.versionParts;
        List<Long> elements2 = o.versionParts;
        int largestNumberOfElements = Math.max(elements1.size(), elements2.size());
        for (int i = 0; i < largestNumberOfElements; ++i) {
            int compared = this.getOrZero(elements1, i).compareTo(this.getOrZero(elements2, i));
            if (compared == 0) continue;
            return compared;
        }
        return 0;
    }

    private Long getOrZero(List<Long> elements, int i) {
        return i < elements.size() ? elements.get(i) : 0L;
    }

    private List<Long> tokenizeToLongs(String str) {
        ArrayList<Long> numbers = new ArrayList<Long>();
        for (String number : splitPattern.split(str)) {
            try {
                numbers.add(Long.valueOf(number));
            }
            catch (NumberFormatException e) {
                throw new FlywayException("Invalid version containing non-numeric characters. Only 0..9 and . are allowed. Invalid version: " + str);
            }
        }
        for (int i = numbers.size() - 1; i > 0 && (Long)numbers.get(i) == 0L; --i) {
            numbers.remove(i);
        }
        return numbers;
    }
}

