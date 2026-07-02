/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.info;

import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationInfo;
import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationState;
import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationType;
import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationVersion;
import com.sk89q.worldguard.internal.flywaydb.core.api.resolver.ResolvedMigration;
import com.sk89q.worldguard.internal.flywaydb.core.internal.info.MigrationInfoContext;
import com.sk89q.worldguard.internal.flywaydb.core.internal.metadatatable.AppliedMigration;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.ObjectUtils;
import java.util.Date;

public class MigrationInfoImpl
implements MigrationInfo {
    private final ResolvedMigration resolvedMigration;
    private final AppliedMigration appliedMigration;
    private final MigrationInfoContext context;

    public MigrationInfoImpl(ResolvedMigration resolvedMigration, AppliedMigration appliedMigration, MigrationInfoContext context) {
        this.resolvedMigration = resolvedMigration;
        this.appliedMigration = appliedMigration;
        this.context = context;
    }

    public ResolvedMigration getResolvedMigration() {
        return this.resolvedMigration;
    }

    public AppliedMigration getAppliedMigration() {
        return this.appliedMigration;
    }

    @Override
    public MigrationType getType() {
        if (this.appliedMigration != null) {
            return this.appliedMigration.getType();
        }
        return this.resolvedMigration.getType();
    }

    @Override
    public Integer getChecksum() {
        if (this.appliedMigration != null) {
            return this.appliedMigration.getChecksum();
        }
        return this.resolvedMigration.getChecksum();
    }

    @Override
    public MigrationVersion getVersion() {
        if (this.appliedMigration != null) {
            return this.appliedMigration.getVersion();
        }
        return this.resolvedMigration.getVersion();
    }

    @Override
    public String getDescription() {
        if (this.appliedMigration != null) {
            return this.appliedMigration.getDescription();
        }
        return this.resolvedMigration.getDescription();
    }

    @Override
    public String getScript() {
        if (this.appliedMigration != null) {
            return this.appliedMigration.getScript();
        }
        return this.resolvedMigration.getScript();
    }

    @Override
    public MigrationState getState() {
        if (this.appliedMigration == null) {
            if (this.resolvedMigration.getVersion().compareTo(this.context.init) < 0) {
                return MigrationState.PREINIT;
            }
            if (this.resolvedMigration.getVersion().compareTo(this.context.target) > 0) {
                return MigrationState.ABOVE_TARGET;
            }
            if (this.resolvedMigration.getVersion().compareTo(this.context.lastApplied) < 0 && !this.context.outOfOrder) {
                return MigrationState.IGNORED;
            }
            return MigrationState.PENDING;
        }
        if (this.resolvedMigration == null) {
            if (MigrationType.SCHEMA == this.appliedMigration.getType()) {
                return MigrationState.SUCCESS;
            }
            if (MigrationType.INIT == this.appliedMigration.getType()) {
                return MigrationState.SUCCESS;
            }
            if (this.getVersion().compareTo(this.context.lastResolved) < 0) {
                if (this.appliedMigration.isSuccess()) {
                    return MigrationState.MISSING_SUCCESS;
                }
                return MigrationState.MISSING_FAILED;
            }
            if (this.getVersion().compareTo(this.context.lastResolved) > 0) {
                if (this.appliedMigration.isSuccess()) {
                    return MigrationState.FUTURE_SUCCESS;
                }
                return MigrationState.FUTURE_FAILED;
            }
        }
        if (this.appliedMigration.isSuccess()) {
            if (this.appliedMigration.getVersionRank() == this.appliedMigration.getInstalledRank()) {
                return MigrationState.SUCCESS;
            }
            return MigrationState.OUT_OF_ORDER;
        }
        return MigrationState.FAILED;
    }

    @Override
    public Date getInstalledOn() {
        if (this.appliedMigration != null) {
            return this.appliedMigration.getInstalledOn();
        }
        return null;
    }

    @Override
    public Integer getExecutionTime() {
        if (this.appliedMigration != null) {
            return this.appliedMigration.getExecutionTime();
        }
        return null;
    }

    public String validate() {
        if (this.resolvedMigration == null && this.appliedMigration.getType() != MigrationType.SCHEMA && this.appliedMigration.getType() != MigrationType.INIT) {
            return "Detected applied migration missing on the classpath: " + this.getVersion();
        }
        if (!this.context.pending && MigrationState.PENDING == this.getState() || MigrationState.IGNORED == this.getState()) {
            return "Migration on the classpath has not been applied to database: " + this.getVersion();
        }
        if (this.resolvedMigration != null && this.appliedMigration != null && this.getVersion().compareTo(this.context.init) > 0) {
            if (this.resolvedMigration.getType() != this.appliedMigration.getType()) {
                return String.format("Migration Type mismatch for migration %s: DB=%s, Classpath=%s", new Object[]{this.appliedMigration.getScript(), this.appliedMigration.getType(), this.resolvedMigration.getType()});
            }
            if (!ObjectUtils.nullSafeEquals(this.resolvedMigration.getChecksum(), this.appliedMigration.getChecksum())) {
                return String.format("Migration Checksum mismatch for migration %s: DB=%s, Classpath=%s", this.appliedMigration.getScript(), this.appliedMigration.getChecksum(), this.resolvedMigration.getChecksum());
            }
            if (!this.resolvedMigration.getDescription().equals(this.appliedMigration.getDescription())) {
                return String.format("Migration Description mismatch for migration %s: DB=%s, Classpath=%s", this.appliedMigration.getScript(), this.appliedMigration.getDescription(), this.resolvedMigration.getDescription());
            }
        }
        return null;
    }

    @Override
    public int compareTo(MigrationInfo o) {
        return this.getVersion().compareTo(o.getVersion());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MigrationInfoImpl that = (MigrationInfoImpl)o;
        if (this.appliedMigration != null ? !this.appliedMigration.equals(that.appliedMigration) : that.appliedMigration != null) {
            return false;
        }
        if (!this.context.equals(that.context)) {
            return false;
        }
        return !(this.resolvedMigration == null ? that.resolvedMigration != null : !this.resolvedMigration.equals(that.resolvedMigration));
    }

    public int hashCode() {
        int result = this.resolvedMigration != null ? this.resolvedMigration.hashCode() : 0;
        result = 31 * result + (this.appliedMigration != null ? this.appliedMigration.hashCode() : 0);
        result = 31 * result + this.context.hashCode();
        return result;
    }
}

