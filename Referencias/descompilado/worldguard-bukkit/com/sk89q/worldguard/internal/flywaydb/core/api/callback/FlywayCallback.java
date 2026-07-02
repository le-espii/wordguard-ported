/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.api.callback;

import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationInfo;
import java.sql.Connection;

public interface FlywayCallback {
    public void beforeClean(Connection var1);

    public void afterClean(Connection var1);

    public void beforeMigrate(Connection var1);

    public void afterMigrate(Connection var1);

    public void beforeEachMigrate(Connection var1, MigrationInfo var2);

    public void afterEachMigrate(Connection var1, MigrationInfo var2);

    public void beforeValidate(Connection var1);

    public void afterValidate(Connection var1);

    public void beforeInit(Connection var1);

    public void afterInit(Connection var1);

    public void beforeRepair(Connection var1);

    public void afterRepair(Connection var1);

    public void beforeInfo(Connection var1);

    public void afterInfo(Connection var1);
}

