/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.HashBasedTable
 *  com.google.common.collect.Table
 */
package com.sk89q.worldguard.blacklist;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.sk89q.worldguard.blacklist.BlacklistEntry;
import com.sk89q.worldguard.blacklist.target.Target;
import com.sk89q.worldguard.blacklist.target.TargetMatcher;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class MatcherIndex {
    private static final MatcherIndex EMPTY_INSTANCE = new MatcherIndex((Table<String, TargetMatcher, BlacklistEntry>)HashBasedTable.create());
    private final Table<String, TargetMatcher, BlacklistEntry> entries;

    private MatcherIndex(Table<String, TargetMatcher, BlacklistEntry> entries) {
        Preconditions.checkNotNull(entries);
        this.entries = entries;
    }

    public List<BlacklistEntry> getEntries(Target target) {
        ArrayList<BlacklistEntry> found = new ArrayList<BlacklistEntry>();
        for (Map.Entry entry : this.entries.row((Object)target.getTypeId()).entrySet()) {
            if (!((TargetMatcher)entry.getKey()).test(target)) continue;
            found.add((BlacklistEntry)entry.getValue());
        }
        return found;
    }

    public int size() {
        return this.entries.size();
    }

    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    public static MatcherIndex getEmptyInstance() {
        return EMPTY_INSTANCE;
    }

    public static class Builder {
        private final Table<String, TargetMatcher, BlacklistEntry> entries = HashBasedTable.create();

        public Builder add(TargetMatcher matcher, BlacklistEntry entry) {
            Preconditions.checkNotNull((Object)matcher);
            Preconditions.checkNotNull(this.entries);
            this.entries.put((Object)matcher.getMatchedTypeId(), (Object)matcher, (Object)entry);
            return this;
        }

        public MatcherIndex build() {
            return new MatcherIndex(this.entries);
        }
    }
}

