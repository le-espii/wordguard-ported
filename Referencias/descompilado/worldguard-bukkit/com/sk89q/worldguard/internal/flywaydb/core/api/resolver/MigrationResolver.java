/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.api.resolver;

import com.sk89q.worldguard.internal.flywaydb.core.api.resolver.ResolvedMigration;
import java.util.Collection;

public interface MigrationResolver {
    public Collection<ResolvedMigration> resolveMigrations();
}

