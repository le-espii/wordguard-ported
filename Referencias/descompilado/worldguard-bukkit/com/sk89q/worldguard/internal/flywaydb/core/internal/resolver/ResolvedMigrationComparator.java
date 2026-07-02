/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.resolver;

import com.sk89q.worldguard.internal.flywaydb.core.api.resolver.ResolvedMigration;
import java.util.Comparator;

public class ResolvedMigrationComparator
implements Comparator<ResolvedMigration> {
    @Override
    public int compare(ResolvedMigration o1, ResolvedMigration o2) {
        return o1.getVersion().compareTo(o2.getVersion());
    }
}

