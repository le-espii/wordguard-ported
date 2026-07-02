/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.WorldEdit
 *  com.sk89q.worldedit.extension.platform.Capability
 *  com.sk89q.worldedit.util.report.Unreported
 *  com.sk89q.worldedit.world.World
 */
package com.sk89q.worldguard.bukkit;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.util.report.Unreported;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.bukkit.BukkitWorldConfiguration;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.config.YamlConfigurationManager;
import java.io.File;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BukkitConfigurationManager
extends YamlConfigurationManager {
    @Unreported
    private WorldGuardPlugin plugin;
    @Unreported
    private ConcurrentMap<String, BukkitWorldConfiguration> worlds = new ConcurrentHashMap<String, BukkitWorldConfiguration>();
    private boolean hasCommandBookGodMode;
    boolean extraStats;

    public BukkitConfigurationManager(WorldGuardPlugin plugin) {
        this.plugin = plugin;
    }

    public Collection<BukkitWorldConfiguration> getWorldConfigs() {
        return this.worlds.values();
    }

    @Override
    public void load() {
        super.load();
        this.extraStats = this.getConfig().getBoolean("custom-metrics-charts", true);
    }

    @Override
    public File getDataFolder() {
        return this.plugin.getDataFolder();
    }

    @Override
    public void copyDefaults() {
        this.plugin.createDefaultConfiguration(new File(this.plugin.getDataFolder(), "config.yml"), "config.yml");
    }

    @Override
    public void unload() {
        this.worlds.clear();
    }

    @Override
    public void postLoad() {
        for (World world : WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.GAME_HOOKS).getWorlds()) {
            this.get(world);
        }
        this.getConfig().save();
    }

    @Override
    public BukkitWorldConfiguration get(World world) {
        String worldName = world.getName();
        return this.get(worldName);
    }

    public BukkitWorldConfiguration get(String worldName) {
        BukkitWorldConfiguration config = (BukkitWorldConfiguration)this.worlds.get(worldName);
        BukkitWorldConfiguration newConfig = null;
        while (config == null) {
            if (newConfig == null) {
                newConfig = new BukkitWorldConfiguration(this.plugin, worldName, this.getConfig());
            }
            this.worlds.putIfAbsent(worldName, newConfig);
            config = (BukkitWorldConfiguration)this.worlds.get(worldName);
        }
        return config;
    }

    public void updateCommandBookGodMode() {
        try {
            if (this.plugin.getServer().getPluginManager().isPluginEnabled("CommandBook")) {
                Class.forName("com.sk89q.commandbook.GodComponent");
                this.hasCommandBookGodMode = true;
                return;
            }
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        this.hasCommandBookGodMode = false;
    }

    public boolean hasCommandBookGodMode() {
        return this.hasCommandBookGodMode;
    }
}

