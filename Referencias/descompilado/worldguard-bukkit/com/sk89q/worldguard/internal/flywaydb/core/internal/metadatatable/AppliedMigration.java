/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.metadatatable;

import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationType;
import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationVersion;
import java.util.Date;

public class AppliedMigration
implements Comparable<AppliedMigration> {
    private int versionRank;
    private int installedRank;
    private MigrationVersion version;
    private String description;
    private MigrationType type;
    private String script;
    private Integer checksum;
    private Date installedOn;
    private String installedBy;
    private int executionTime;
    private boolean success;

    public AppliedMigration(int versionRank, int installedRank, MigrationVersion version, String description, MigrationType type, String script, Integer checksum, Date installedOn, String installedBy, int executionTime, boolean success) {
        this.versionRank = versionRank;
        this.installedRank = installedRank;
        this.version = version;
        this.description = description;
        this.type = type;
        this.script = script;
        this.checksum = checksum;
        this.installedOn = installedOn;
        this.installedBy = installedBy;
        this.executionTime = executionTime;
        this.success = success;
    }

    public AppliedMigration(MigrationVersion version, String description, MigrationType type, String script, Integer checksum, int executionTime, boolean success) {
        this.version = version;
        this.description = this.abbreviateDescription(description);
        this.type = type;
        this.script = this.abbreviateScript(script);
        this.checksum = checksum;
        this.executionTime = executionTime;
        this.success = success;
    }

    private String abbreviateDescription(String description) {
        if (description == null) {
            return null;
        }
        if (description.length() <= 200) {
            return description;
        }
        return description.substring(0, 197) + "...";
    }

    private String abbreviateScript(String script) {
        if (script == null) {
            return null;
        }
        if (script.length() <= 1000) {
            return script;
        }
        return "..." + script.substring(3, 1000);
    }

    public int getVersionRank() {
        return this.versionRank;
    }

    public int getInstalledRank() {
        return this.installedRank;
    }

    public MigrationVersion getVersion() {
        return this.version;
    }

    public String getDescription() {
        return this.description;
    }

    public MigrationType getType() {
        return this.type;
    }

    public String getScript() {
        return this.script;
    }

    public Integer getChecksum() {
        return this.checksum;
    }

    public Date getInstalledOn() {
        return this.installedOn;
    }

    public String getInstalledBy() {
        return this.installedBy;
    }

    public int getExecutionTime() {
        return this.executionTime;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AppliedMigration that = (AppliedMigration)o;
        if (this.executionTime != that.executionTime) {
            return false;
        }
        if (this.installedRank != that.installedRank) {
            return false;
        }
        if (this.success != that.success) {
            return false;
        }
        if (this.versionRank != that.versionRank) {
            return false;
        }
        if (this.checksum != null ? !this.checksum.equals(that.checksum) : that.checksum != null) {
            return false;
        }
        if (!this.description.equals(that.description)) {
            return false;
        }
        if (this.installedBy != null ? !this.installedBy.equals(that.installedBy) : that.installedBy != null) {
            return false;
        }
        if (this.installedOn != null ? !this.installedOn.equals(that.installedOn) : that.installedOn != null) {
            return false;
        }
        if (!this.script.equals(that.script)) {
            return false;
        }
        if (this.type != that.type) {
            return false;
        }
        return this.version.equals(that.version);
    }

    public int hashCode() {
        int result = this.versionRank;
        result = 31 * result + this.installedRank;
        result = 31 * result + this.version.hashCode();
        result = 31 * result + this.description.hashCode();
        result = 31 * result + this.type.hashCode();
        result = 31 * result + this.script.hashCode();
        result = 31 * result + (this.checksum != null ? this.checksum.hashCode() : 0);
        result = 31 * result + (this.installedOn != null ? this.installedOn.hashCode() : 0);
        result = 31 * result + (this.installedBy != null ? this.installedBy.hashCode() : 0);
        result = 31 * result + this.executionTime;
        result = 31 * result + (this.success ? 1 : 0);
        return result;
    }

    @Override
    public int compareTo(AppliedMigration o) {
        return this.version.compareTo(o.version);
    }
}

