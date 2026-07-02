/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.resolver;

import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationType;
import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationVersion;
import com.sk89q.worldguard.internal.flywaydb.core.api.resolver.MigrationExecutor;
import com.sk89q.worldguard.internal.flywaydb.core.api.resolver.ResolvedMigration;

public class ResolvedMigrationImpl
implements ResolvedMigration {
    private MigrationVersion version;
    private String description;
    private String script;
    private Integer checksum;
    private MigrationType type;
    private String physicalLocation;
    private MigrationExecutor executor;

    @Override
    public MigrationVersion getVersion() {
        return this.version;
    }

    public void setVersion(MigrationVersion version) {
        this.version = version;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getScript() {
        return this.script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    @Override
    public Integer getChecksum() {
        return this.checksum;
    }

    public void setChecksum(Integer checksum) {
        this.checksum = checksum;
    }

    @Override
    public MigrationType getType() {
        return this.type;
    }

    public void setType(MigrationType type) {
        this.type = type;
    }

    @Override
    public String getPhysicalLocation() {
        return this.physicalLocation;
    }

    public void setPhysicalLocation(String physicalLocation) {
        this.physicalLocation = physicalLocation;
    }

    @Override
    public MigrationExecutor getExecutor() {
        return this.executor;
    }

    public void setExecutor(MigrationExecutor executor) {
        this.executor = executor;
    }

    public int compareTo(ResolvedMigrationImpl o) {
        return this.version.compareTo(o.version);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ResolvedMigrationImpl migration = (ResolvedMigrationImpl)o;
        if (this.checksum != null ? !this.checksum.equals(migration.checksum) : migration.checksum != null) {
            return false;
        }
        if (this.description != null ? !this.description.equals(migration.description) : migration.description != null) {
            return false;
        }
        if (this.physicalLocation != null ? !this.physicalLocation.equals(migration.physicalLocation) : migration.physicalLocation != null) {
            return false;
        }
        if (this.script != null ? !this.script.equals(migration.script) : migration.script != null) {
            return false;
        }
        if (this.type != migration.type) {
            return false;
        }
        return this.version.equals(migration.version);
    }

    public int hashCode() {
        int result = this.version.hashCode();
        result = 31 * result + (this.description != null ? this.description.hashCode() : 0);
        result = 31 * result + (this.script != null ? this.script.hashCode() : 0);
        result = 31 * result + (this.checksum != null ? this.checksum.hashCode() : 0);
        result = 31 * result + this.type.hashCode();
        result = 31 * result + (this.physicalLocation != null ? this.physicalLocation.hashCode() : 0);
        return result;
    }
}

