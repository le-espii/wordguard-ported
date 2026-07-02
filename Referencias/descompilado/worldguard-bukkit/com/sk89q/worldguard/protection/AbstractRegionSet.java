/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.protection;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.flags.StateFlag;
import javax.annotation.Nullable;

public abstract class AbstractRegionSet
implements ApplicableRegionSet {
    @Override
    public boolean testState(@Nullable RegionAssociable subject, StateFlag ... flags) {
        return StateFlag.test(this.queryState(subject, flags));
    }

    @Override
    @Nullable
    public StateFlag.State queryState(@Nullable RegionAssociable subject, StateFlag ... flags) {
        StateFlag.State value = null;
        for (StateFlag flag : flags) {
            if ((value = StateFlag.combine(value, this.queryValue(subject, flag))) == StateFlag.State.DENY) break;
        }
        return value;
    }
}

