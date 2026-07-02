/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.metadatatable;

import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationVersion;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Schema;
import com.sk89q.worldguard.internal.flywaydb.core.internal.metadatatable.AppliedMigration;
import java.util.List;

public interface MetaDataTable {
    public void lock();

    public void addAppliedMigration(AppliedMigration var1);

    public boolean hasAppliedMigrations();

    public List<AppliedMigration> allAppliedMigrations();

    public void addInitMarker(MigrationVersion var1, String var2);

    public boolean hasInitMarker();

    public AppliedMigration getInitMarker();

    public void removeFailedMigrations();

    public void addSchemasMarker(Schema[] var1);

    public boolean hasSchemasMarker();

    public void updateChecksum(MigrationVersion var1, Integer var2);
}

