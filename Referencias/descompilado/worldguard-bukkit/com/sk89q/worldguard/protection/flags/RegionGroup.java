/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldguard.protection.flags;

import com.google.common.base.Preconditions;
import com.sk89q.worldguard.domains.Association;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

public enum RegionGroup {
    MEMBERS(Association.MEMBER, Association.OWNER),
    OWNERS(Association.OWNER),
    NON_MEMBERS(Association.NON_MEMBER),
    NON_OWNERS(Association.MEMBER, Association.NON_MEMBER),
    ALL(Association.OWNER, Association.MEMBER, Association.NON_MEMBER),
    NONE(new Association[0]);

    private final Set<Association> contained;

    private RegionGroup(Association ... association) {
        this.contained = association.length > 0 ? EnumSet.copyOf(Arrays.asList(association)) : EnumSet.noneOf(Association.class);
    }

    public boolean contains(Association association) {
        Preconditions.checkNotNull((Object)((Object)association));
        return this.contained.contains((Object)association);
    }
}

