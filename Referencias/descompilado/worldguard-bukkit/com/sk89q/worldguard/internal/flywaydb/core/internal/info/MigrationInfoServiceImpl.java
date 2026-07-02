/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.info;

import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationInfo;
import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationInfoService;
import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationState;
import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationType;
import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationVersion;
import com.sk89q.worldguard.internal.flywaydb.core.api.resolver.MigrationResolver;
import com.sk89q.worldguard.internal.flywaydb.core.api.resolver.ResolvedMigration;
import com.sk89q.worldguard.internal.flywaydb.core.internal.info.MigrationInfoContext;
import com.sk89q.worldguard.internal.flywaydb.core.internal.info.MigrationInfoImpl;
import com.sk89q.worldguard.internal.flywaydb.core.internal.metadatatable.AppliedMigration;
import com.sk89q.worldguard.internal.flywaydb.core.internal.metadatatable.MetaDataTable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;

public class MigrationInfoServiceImpl
implements MigrationInfoService {
    private final MigrationResolver migrationResolver;
    private final MetaDataTable metaDataTable;
    private final MigrationVersion target;
    private boolean outOfOrder;
    private final boolean pending;
    private List<MigrationInfoImpl> migrationInfos;

    public MigrationInfoServiceImpl(MigrationResolver migrationResolver, MetaDataTable metaDataTable, MigrationVersion target, boolean outOfOrder, boolean pending) {
        this.migrationResolver = migrationResolver;
        this.metaDataTable = metaDataTable;
        this.target = target;
        this.outOfOrder = outOfOrder;
        this.pending = pending;
    }

    public void refresh() {
        Collection<ResolvedMigration> availableMigrations = this.migrationResolver.resolveMigrations();
        List<AppliedMigration> appliedMigrations = this.metaDataTable.allAppliedMigrations();
        this.migrationInfos = this.mergeAvailableAndAppliedMigrations(availableMigrations, appliedMigrations);
    }

    List<MigrationInfoImpl> mergeAvailableAndAppliedMigrations(Collection<ResolvedMigration> resolvedMigrations, List<AppliedMigration> appliedMigrations) {
        MigrationInfoContext context = new MigrationInfoContext();
        context.outOfOrder = this.outOfOrder;
        context.pending = this.pending;
        context.target = this.target;
        TreeMap<MigrationVersion, ResolvedMigration> resolvedMigrationsMap = new TreeMap<MigrationVersion, ResolvedMigration>();
        for (ResolvedMigration resolvedMigration : resolvedMigrations) {
            MigrationVersion version = resolvedMigration.getVersion();
            if (version.compareTo(context.lastResolved) > 0) {
                context.lastResolved = version;
            }
            resolvedMigrationsMap.put(version, resolvedMigration);
        }
        TreeMap<MigrationVersion, AppliedMigration> appliedMigrationsMap = new TreeMap<MigrationVersion, AppliedMigration>();
        for (AppliedMigration appliedMigration : appliedMigrations) {
            MigrationVersion version = appliedMigration.getVersion();
            if (version.compareTo(context.lastApplied) > 0) {
                context.lastApplied = version;
            }
            if (appliedMigration.getType() == MigrationType.SCHEMA) {
                context.schema = version;
            }
            if (appliedMigration.getType() == MigrationType.INIT) {
                context.init = version;
            }
            appliedMigrationsMap.put(version, appliedMigration);
        }
        HashSet allVersions = new HashSet();
        allVersions.addAll(resolvedMigrationsMap.keySet());
        allVersions.addAll(appliedMigrationsMap.keySet());
        ArrayList<MigrationInfoImpl> migrationInfos = new ArrayList<MigrationInfoImpl>();
        for (MigrationVersion version : allVersions) {
            ResolvedMigration resolvedMigration = (ResolvedMigration)resolvedMigrationsMap.get(version);
            AppliedMigration appliedMigration = (AppliedMigration)appliedMigrationsMap.get(version);
            migrationInfos.add(new MigrationInfoImpl(resolvedMigration, appliedMigration, context));
        }
        Collections.sort(migrationInfos);
        return migrationInfos;
    }

    @Override
    public MigrationInfo[] all() {
        return this.migrationInfos.toArray(new MigrationInfoImpl[this.migrationInfos.size()]);
    }

    @Override
    public MigrationInfo current() {
        for (int i = this.migrationInfos.size() - 1; i >= 0; --i) {
            MigrationInfo migrationInfo = this.migrationInfos.get(i);
            if (!migrationInfo.getState().isApplied()) continue;
            return migrationInfo;
        }
        return null;
    }

    public MigrationInfoImpl[] pending() {
        ArrayList<MigrationInfoImpl> pendingMigrations = new ArrayList<MigrationInfoImpl>();
        for (MigrationInfoImpl migrationInfo : this.migrationInfos) {
            if (MigrationState.PENDING != migrationInfo.getState()) continue;
            pendingMigrations.add(migrationInfo);
        }
        return pendingMigrations.toArray(new MigrationInfoImpl[pendingMigrations.size()]);
    }

    @Override
    public MigrationInfo[] applied() {
        ArrayList<MigrationInfo> appliedMigrations = new ArrayList<MigrationInfo>();
        for (MigrationInfo migrationInfo : this.migrationInfos) {
            if (!migrationInfo.getState().isApplied()) continue;
            appliedMigrations.add(migrationInfo);
        }
        return appliedMigrations.toArray(new MigrationInfo[appliedMigrations.size()]);
    }

    public MigrationInfo[] resolved() {
        ArrayList<MigrationInfo> resolvedMigrations = new ArrayList<MigrationInfo>();
        for (MigrationInfo migrationInfo : this.migrationInfos) {
            if (!migrationInfo.getState().isResolved()) continue;
            resolvedMigrations.add(migrationInfo);
        }
        return resolvedMigrations.toArray(new MigrationInfo[resolvedMigrations.size()]);
    }

    public MigrationInfo[] failed() {
        ArrayList<MigrationInfo> failedMigrations = new ArrayList<MigrationInfo>();
        for (MigrationInfo migrationInfo : this.migrationInfos) {
            if (!migrationInfo.getState().isFailed()) continue;
            failedMigrations.add(migrationInfo);
        }
        return failedMigrations.toArray(new MigrationInfo[failedMigrations.size()]);
    }

    public MigrationInfo[] future() {
        ArrayList<MigrationInfo> futureMigrations = new ArrayList<MigrationInfo>();
        for (MigrationInfo migrationInfo : this.migrationInfos) {
            if (migrationInfo.getState() != MigrationState.FUTURE_SUCCESS && migrationInfo.getState() != MigrationState.FUTURE_FAILED) continue;
            futureMigrations.add(migrationInfo);
        }
        return futureMigrations.toArray(new MigrationInfo[futureMigrations.size()]);
    }

    public MigrationInfo[] outOfOrder() {
        ArrayList<MigrationInfo> outOfOrderMigrations = new ArrayList<MigrationInfo>();
        for (MigrationInfo migrationInfo : this.migrationInfos) {
            if (migrationInfo.getState() != MigrationState.OUT_OF_ORDER) continue;
            outOfOrderMigrations.add(migrationInfo);
        }
        return outOfOrderMigrations.toArray(new MigrationInfo[outOfOrderMigrations.size()]);
    }

    public String validate() {
        for (MigrationInfoImpl migrationInfo : this.migrationInfos) {
            String message = migrationInfo.validate();
            if (message == null) continue;
            return message;
        }
        return null;
    }
}

