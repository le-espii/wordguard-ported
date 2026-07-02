/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 */
package com.sk89q.worldguard.bukkit.listener;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.event.player.ProcessPlayerEvent;
import com.sk89q.worldguard.bukkit.listener.AbstractListener;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.GodMode;
import com.sk89q.worldguard.session.handler.WaterBreathing;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class PlayerModesListener
extends AbstractListener {
    private static final Logger log = Logger.getLogger(PlayerModesListener.class.getCanonicalName());
    private static final String INVINCIBLE_PERMISSION = "worldguard.auto-invincible";
    private static final String INVINCIBLE_GROUP = "wg-invincible";
    private static final String AMPHIBIOUS_GROUP = "wg-amphibious";

    public PlayerModesListener(WorldGuardPlugin plugin) {
        super(plugin);
    }

    private boolean hasGodModeGroup(Player player) {
        return PlayerModesListener.getConfig().useGodGroup && PlayerModesListener.getPlugin().inGroup((OfflinePlayer)player, INVINCIBLE_GROUP);
    }

    private boolean hasGodModePermission(Player player) {
        return PlayerModesListener.getConfig().useGodPermission && PlayerModesListener.getPlugin().hasPermission((CommandSender)player, INVINCIBLE_PERMISSION);
    }

    private boolean hasAmphibiousGroup(Player player) {
        return PlayerModesListener.getConfig().useAmphibiousGroup && PlayerModesListener.getPlugin().inGroup((OfflinePlayer)player, AMPHIBIOUS_GROUP);
    }

    @EventHandler
    public void onProcessPlayer(ProcessPlayerEvent event) {
        Player player = event.getPlayer();
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        Session session = WorldGuard.getInstance().getPlatform().getSessionManager().get(localPlayer);
        if ((this.hasGodModeGroup(player) || this.hasGodModePermission(player)) && GodMode.set(localPlayer, session, true)) {
            log.log(Level.INFO, "Enabled auto-god mode for " + player.getName());
        }
        if (this.hasAmphibiousGroup(player) && WaterBreathing.set(localPlayer, session, true)) {
            log.log(Level.INFO, "Enabled water breathing mode for " + player.getName() + " (player is in group 'wg-amphibious')");
        }
    }
}

