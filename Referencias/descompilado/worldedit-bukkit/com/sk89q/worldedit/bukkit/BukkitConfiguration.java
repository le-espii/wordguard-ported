/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 */
package com.sk89q.worldedit.bukkit;

import com.sk89q.util.yaml.YAMLProcessor;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.util.YAMLConfiguration;
import com.sk89q.worldedit.util.report.Unreported;
import java.nio.file.Path;
import org.apache.logging.log4j.LogManager;

public class BukkitConfiguration
extends YAMLConfiguration {
    public boolean noOpPermissions = false;
    public boolean unsupportedVersionEditing = false;
    @Unreported
    private final WorldEditPlugin plugin;

    public BukkitConfiguration(YAMLProcessor config, WorldEditPlugin plugin) {
        super(config, LogManager.getLogger((String)plugin.getLogger().getName()));
        this.plugin = plugin;
    }

    @Override
    public void load() {
        super.load();
        this.noOpPermissions = this.config.getBoolean("no-op-permissions", false);
        this.unsupportedVersionEditing = "I accept that I will receive no support with this flag enabled.".equals(this.config.getString("allow-editing-on-unsupported-versions", "false"));
        if (this.unsupportedVersionEditing) {
            WorldEdit.logger.warn("Editing without a Bukkit adapter has been enabled. You will not receive support for any issues that arise as a result.");
        }
    }

    @Override
    public Path getWorkingDirectoryPath() {
        return this.plugin.getDataFolder().toPath();
    }
}

