/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.protection.flags;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.EnumFlag;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import javax.annotation.Nullable;

public class RegionGroupFlag
extends EnumFlag<RegionGroup> {
    private RegionGroup def;

    public RegionGroupFlag(String name, RegionGroup def) {
        super(name, RegionGroup.class, null);
        this.def = def;
    }

    @Override
    public RegionGroup getDefault() {
        return this.def;
    }

    @Override
    public RegionGroup detectValue(String input) {
        if ((input = input.trim()).equalsIgnoreCase("members") || input.equalsIgnoreCase("member")) {
            return RegionGroup.MEMBERS;
        }
        if (input.equalsIgnoreCase("owners") || input.equalsIgnoreCase("owner")) {
            return RegionGroup.OWNERS;
        }
        if (input.equalsIgnoreCase("nonowners") || input.equalsIgnoreCase("nonowner")) {
            return RegionGroup.NON_OWNERS;
        }
        if (input.equalsIgnoreCase("nonmembers") || input.equalsIgnoreCase("nonmember")) {
            return RegionGroup.NON_MEMBERS;
        }
        if (input.equalsIgnoreCase("everyone") || input.equalsIgnoreCase("anyone") || input.equalsIgnoreCase("all")) {
            return RegionGroup.ALL;
        }
        if (input.equalsIgnoreCase("none") || input.equalsIgnoreCase("noone") || input.equalsIgnoreCase("deny")) {
            return RegionGroup.NONE;
        }
        return null;
    }

    public static boolean isMember(ProtectedRegion region, RegionGroup group, @Nullable LocalPlayer player) {
        if (group == null || group == RegionGroup.ALL) {
            return true;
        }
        if (group == RegionGroup.OWNERS) {
            return player != null && region.isOwner(player);
        }
        if (group == RegionGroup.MEMBERS) {
            return player != null && region.isMember(player);
        }
        if (group == RegionGroup.NON_OWNERS) {
            return player == null || !region.isOwner(player);
        }
        if (group == RegionGroup.NON_MEMBERS) {
            return player == null || !region.isMember(player);
        }
        return false;
    }

    public static boolean isMember(ApplicableRegionSet set, @Nullable RegionGroup group, LocalPlayer player) {
        if (group == null || group == RegionGroup.ALL) {
            return true;
        }
        if (group == RegionGroup.OWNERS) {
            return set.isOwnerOfAll(player);
        }
        if (group == RegionGroup.MEMBERS) {
            return set.isMemberOfAll(player);
        }
        if (group == RegionGroup.NON_OWNERS) {
            return !set.isOwnerOfAll(player);
        }
        if (group == RegionGroup.NON_MEMBERS) {
            return !set.isMemberOfAll(player);
        }
        return false;
    }
}

