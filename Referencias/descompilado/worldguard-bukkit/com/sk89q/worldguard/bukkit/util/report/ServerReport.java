/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.util.report.DataReport
 *  org.bukkit.Bukkit
 *  org.bukkit.Server
 */
package com.sk89q.worldguard.bukkit.util.report;

import com.sk89q.worldedit.util.report.DataReport;
import com.sk89q.worldguard.bukkit.paperlib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Server;

public class ServerReport
extends DataReport {
    public ServerReport() {
        super("Server Information");
        Server server = Bukkit.getServer();
        this.append("Bukkit Version", server.getBukkitVersion());
        this.append("Implementation", server.getName() + " " + server.getVersion());
        this.append("Player Count", "%d/%d", new Object[]{Bukkit.getOnlinePlayers().size(), server.getMaxPlayers()});
        this.append("Server Class Source", server.getClass().getProtectionDomain().getCodeSource().getLocation());
        DataReport onlineMode = new DataReport("Online Mode");
        onlineMode.append("enabled?", server.getOnlineMode());
        if (PaperLib.isSpigot()) {
            onlineMode.append("BungeeCord support?", Bukkit.spigot().getConfig().getBoolean("settings.bungeecord", false));
        }
        if (PaperLib.isPaper()) {
            onlineMode.append("Velocity support?", Bukkit.spigot().getPaperConfig().getBoolean("proxies.velocity.enabled", false));
        }
        this.append(onlineMode.getTitle(), onlineMode);
        DataReport spawning = new DataReport("Spawning");
        spawning.append("Ambient Spawn Limit", server.getAmbientSpawnLimit());
        spawning.append("Animal Spawn Limit", server.getAnimalSpawnLimit());
        spawning.append("Monster Spawn Limit", server.getMonsterSpawnLimit());
        spawning.append("Ticks per Animal Spawn", server.getTicksPerAnimalSpawns());
        spawning.append("Ticks per Monster Spawn", server.getTicksPerMonsterSpawns());
        this.append(spawning.getTitle(), spawning);
        DataReport config = new DataReport("Configuration");
        config.append("Nether Enabled?", server.getAllowNether());
        config.append("The End Enabled?", server.getAllowEnd());
        config.append("Generate Structures?", server.getGenerateStructures());
        config.append("Flight Allowed?", server.getAllowFlight());
        config.append("Connection Throttle", server.getConnectionThrottle());
        config.append("Idle Timeout", server.getIdleTimeout());
        config.append("Shutdown Message", server.getShutdownMessage());
        config.append("Default Game Mode", (Object)server.getDefaultGameMode());
        config.append("Main World Type", server.getWorldType());
        config.append("View Distance", server.getViewDistance());
        this.append(config.getTitle(), config);
        DataReport protection = new DataReport("Protection");
        protection.append("Spawn Radius", server.getSpawnRadius());
        this.append(protection.getTitle(), protection);
    }
}

