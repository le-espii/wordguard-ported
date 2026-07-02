/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.commandbook.InfoComponent$PlayerWhoisEvent
 *  com.sk89q.worldedit.world.World
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 */
package com.sk89q.worldguard.bukkit.listener;

import com.sk89q.commandbook.InfoComponent;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class WorldGuardCommandBookListener
implements Listener {
    private final WorldGuardPlugin plugin;

    public WorldGuardCommandBookListener(WorldGuardPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerWhois(InfoComponent.PlayerWhoisEvent event) {
        if (event.getPlayer() instanceof Player) {
            Player player = (Player)event.getPlayer();
            LocalPlayer localPlayer = this.plugin.wrapPlayer(player);
            if (WorldGuard.getInstance().getPlatform().getGlobalStateManager().get((World)localPlayer.getWorld()).useRegions) {
                ApplicableRegionSet regions = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().getApplicableRegions(localPlayer.getLocation());
                StringBuilder regionStr = new StringBuilder();
                boolean first = true;
                for (ProtectedRegion region : regions) {
                    if (!first) {
                        regionStr.append(", ");
                    }
                    if (region.isOwner(localPlayer)) {
                        regionStr.append("+");
                    } else if (region.isMemberOnly(localPlayer)) {
                        regionStr.append("-");
                    }
                    regionStr.append(region.getId());
                    first = false;
                }
                if (regions.size() > 0) {
                    event.addWhoisInformation("Current Regions", (Object)regionStr);
                }
                event.addWhoisInformation("Can build", (Object)regions.testState(localPlayer, Flags.BUILD));
            }
        }
    }
}

