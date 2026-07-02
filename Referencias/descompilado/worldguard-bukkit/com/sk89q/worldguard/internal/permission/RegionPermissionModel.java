/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.entity.Player
 *  com.sk89q.worldedit.extension.platform.Actor
 *  com.sk89q.worldedit.world.World
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.internal.permission;

import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.permission.AbstractPermissionModel;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import javax.annotation.Nullable;

public class RegionPermissionModel
extends AbstractPermissionModel {
    public RegionPermissionModel(Actor sender) {
        super(sender);
    }

    @Deprecated
    public boolean mayIgnoreRegionProtection(World world) {
        if (this.getSender() instanceof LocalPlayer) {
            return WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass((LocalPlayer)this.getSender(), world);
        }
        return this.hasPluginPermission("region.bypass." + world.getName());
    }

    public boolean mayForceLoadRegions() {
        return this.hasPluginPermission("region.load");
    }

    public boolean mayForceSaveRegions() {
        return this.hasPluginPermission("region.save");
    }

    public boolean mayMigrateRegionStore() {
        return this.hasPluginPermission("region.migratedb");
    }

    public boolean mayMigrateRegionNames() {
        return this.hasPluginPermission("region.migrateuuid");
    }

    public boolean mayMigrateRegionHeights() {
        return this.hasPluginPermission("region.migrateheights");
    }

    public boolean mayDefine() {
        return this.hasPluginPermission("region.define");
    }

    public boolean mayRedefine(ProtectedRegion region) {
        return this.hasPatternPermission("redefine", region);
    }

    public boolean mayClaim() {
        return this.hasPluginPermission("region.claim");
    }

    public boolean mayClaimRegionsUnbounded() {
        return this.hasPluginPermission("region.unlimited");
    }

    public boolean mayDelete(ProtectedRegion region) {
        return this.hasPatternPermission("remove", region);
    }

    public boolean maySetPriority(ProtectedRegion region) {
        return this.hasPatternPermission("setpriority", region);
    }

    public boolean maySetParent(ProtectedRegion child, ProtectedRegion parent) {
        return this.hasPatternPermission("setparent", child) && (parent == null || this.hasPatternPermission("setparent", parent));
    }

    public boolean maySelect(ProtectedRegion region) {
        return this.hasPatternPermission("select", region);
    }

    public boolean mayLookup(ProtectedRegion region) {
        return this.hasPatternPermission("info", region);
    }

    public boolean mayTeleportTo(ProtectedRegion region) {
        return this.hasPatternPermission("teleport", region);
    }

    public boolean mayTeleportToCenter(ProtectedRegion region) {
        return this.hasPatternPermission("teleportcenter", region);
    }

    public boolean mayOverrideLocationFlagBounds(ProtectedRegion region) {
        return this.hasPatternPermission("locationoverride", region);
    }

    public boolean mayList() {
        return this.hasPluginPermission("region.list");
    }

    public boolean mayList(String targetPlayer) {
        if (targetPlayer == null) {
            return this.mayList();
        }
        if (targetPlayer.equalsIgnoreCase(this.getSender().getName())) {
            return this.hasPluginPermission("region.list.own");
        }
        return this.mayList();
    }

    public boolean maySetFlag(ProtectedRegion region) {
        return this.hasPatternPermission("flag.regions", region);
    }

    public boolean maySetFlag(ProtectedRegion region, Flag<?> flag) {
        return this.hasPatternPermission("flag.flags." + flag.getName().toLowerCase(), region);
    }

    public boolean maySetFlag(ProtectedRegion region, Flag<?> flag, @Nullable String value) {
        String sanitizedValue;
        if (value != null) {
            sanitizedValue = value.trim().toLowerCase().replaceAll("[^a-z0-9]", "");
            if (sanitizedValue.length() > 20) {
                sanitizedValue = sanitizedValue.substring(0, 20);
            }
        } else {
            sanitizedValue = "unset";
        }
        return this.hasPatternPermission("flag.flags." + flag.getName().toLowerCase() + "." + sanitizedValue, region);
    }

    public boolean mayAddMembers(ProtectedRegion region) {
        return this.hasPatternPermission("addmember", region);
    }

    public boolean mayAddOwners(ProtectedRegion region) {
        return this.hasPatternPermission("addowner", region);
    }

    public boolean mayRemoveMembers(ProtectedRegion region) {
        return this.hasPatternPermission("removemember", region);
    }

    public boolean mayRemoveOwners(ProtectedRegion region) {
        return this.hasPatternPermission("removeowner", region);
    }

    private boolean hasPatternPermission(String perm, ProtectedRegion region) {
        if (!(this.getSender() instanceof Player)) {
            return true;
        }
        String idLower = region.getId().toLowerCase();
        if (region.isOwner((LocalPlayer)this.getSender())) {
            return this.hasPluginPermission("region." + perm + ".own." + idLower) || this.hasPluginPermission("region." + perm + ".member." + idLower);
        }
        if (region.isMember((LocalPlayer)this.getSender())) {
            return this.hasPluginPermission("region." + perm + ".member." + idLower);
        }
        String effectivePerm = "region." + perm + "." + idLower;
        return this.hasPluginPermission(effectivePerm);
    }
}

