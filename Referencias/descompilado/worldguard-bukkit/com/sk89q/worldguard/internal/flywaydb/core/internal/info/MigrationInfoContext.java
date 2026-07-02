/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.info;

import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationVersion;

public class MigrationInfoContext {
    public boolean outOfOrder;
    public boolean pending;
    public MigrationVersion target;
    public MigrationVersion schema;
    public MigrationVersion init;
    public MigrationVersion lastResolved = MigrationVersion.EMPTY;
    public MigrationVersion lastApplied = MigrationVersion.EMPTY;

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MigrationInfoContext context = (MigrationInfoContext)o;
        if (this.outOfOrder != context.outOfOrder) {
            return false;
        }
        if (this.pending != context.pending) {
            return false;
        }
        if (this.schema != null ? !this.schema.equals(context.schema) : context.schema != null) {
            return false;
        }
        if (this.init != null ? !this.init.equals(context.init) : context.init != null) {
            return false;
        }
        if (!this.lastApplied.equals(context.lastApplied)) {
            return false;
        }
        if (!this.lastResolved.equals(context.lastResolved)) {
            return false;
        }
        return this.target.equals(context.target);
    }

    public int hashCode() {
        int result = this.outOfOrder ? 1 : 0;
        result = 31 * result + (this.pending ? 1 : 0);
        result = 31 * result + this.target.hashCode();
        result = 31 * result + (this.schema != null ? this.schema.hashCode() : 0);
        result = 31 * result + (this.init != null ? this.init.hashCode() : 0);
        result = 31 * result + this.lastResolved.hashCode();
        result = 31 * result + this.lastApplied.hashCode();
        return result;
    }
}

