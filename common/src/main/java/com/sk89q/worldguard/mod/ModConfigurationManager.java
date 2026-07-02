package com.sk89q.worldguard.mod;

import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.config.ConfigurationManager;
import com.sk89q.worldguard.config.WorldConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.sk89q.worldguard.protection.managers.storage.file.DirectoryYamlDriver;

public class ModConfigurationManager extends ConfigurationManager {
    private final Map<String, WorldConfiguration> worldConfigs = new HashMap<>();

    @Override
    public File getDataFolder() {
        return new File("config/worldguard");
    }

    @Override
    public void load() {
        File worldsDir = new File(getDataFolder(), "worlds");
        worldsDir.mkdirs();
        this.selectedRegionStoreDriver = new DirectoryYamlDriver(worldsDir, "regions.yml");
    }

    @Override
    public void unload() {
        worldConfigs.clear();
    }

    @Override
    public WorldConfiguration get(World world) {
        return worldConfigs.computeIfAbsent(world.getName(), name -> {
            ModWorldConfiguration cfg = new ModWorldConfiguration(world.getName());
            cfg.loadConfiguration();
            return cfg;
        });
    }

    @Override
    public void disableUuidMigration() {
        // No-op
    }
}
