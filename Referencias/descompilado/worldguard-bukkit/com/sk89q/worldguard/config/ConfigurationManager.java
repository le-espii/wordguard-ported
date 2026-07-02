/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.util.report.Unreported
 *  com.sk89q.worldedit.world.World
 */
package com.sk89q.worldguard.config;

import com.sk89q.worldedit.util.report.Unreported;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.config.WorldConfiguration;
import com.sk89q.worldguard.protection.managers.storage.DriverType;
import com.sk89q.worldguard.protection.managers.storage.RegionDriver;
import com.sk89q.worldguard.session.handler.WaterBreathing;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public abstract class ConfigurationManager {
    protected static final Logger log = Logger.getLogger(ConfigurationManager.class.getCanonicalName());
    static final String CONFIG_HEADER = "#\r\n# WorldGuard's main configuration file\r\n#\r\n# This is the global configuration file. Anything placed into here will\r\n# be applied to all worlds. However, each world has its own configuration\r\n# file to allow you to replace most settings in here for that world only.\r\n#\r\n# About editing this file:\r\n# - DO NOT USE TABS. You MUST use spaces or Bukkit will complain. If\r\n#   you use an editor like Notepad++ (recommended for Windows users), you\r\n#   must configure it to \"replace tabs with spaces.\" In Notepad++, this can\r\n#   be changed in Settings > Preferences > Language Menu.\r\n# - Don't get rid of the indents. They are indented so some entries are\r\n#   in categories (like \"enforce-single-session\" is in the \"protection\"\r\n#   category.\r\n# - If you want to check the format of this file before putting it\r\n#   into WorldGuard, paste it into http://yaml-online-parser.appspot.com/\r\n#   and see if it gives \"ERROR:\".\r\n# - Lines starting with # are comments and so they are ignored.\r\n#\r\n";
    public boolean useRegionsCreatureSpawnEvent;
    public boolean activityHaltToggle = false;
    public boolean useGodPermission;
    public boolean useGodGroup;
    public boolean useAmphibiousGroup;
    public boolean usePlayerMove;
    public boolean usePlayerTeleports;
    public boolean deopOnJoin;
    public boolean blockInGameOp;
    public boolean migrateRegionsToUuid;
    public boolean keepUnresolvedNames;
    public boolean particleEffects;
    public boolean disablePermissionCache;
    public boolean disableDefaultBypass;
    public boolean announceBypassStatus;
    @Unreported
    public Map<String, String> hostKeys = new HashMap<String, String>();
    public boolean hostKeysAllowFMLClients;
    @Unreported
    public RegionDriver selectedRegionStoreDriver;
    @Unreported
    public Map<DriverType, RegionDriver> regionStoreDriverMap;

    public abstract File getDataFolder();

    public File getWorldsDataFolder() {
        return new File(this.getDataFolder(), "worlds");
    }

    public abstract void load();

    public abstract void unload();

    public abstract WorldConfiguration get(World var1);

    public abstract void disableUuidMigration();

    public boolean hasGodMode(LocalPlayer player) {
        return WorldGuard.getInstance().getPlatform().getSessionManager().get(player).isInvincible(player);
    }

    public void enableAmphibiousMode(LocalPlayer player) {
        WaterBreathing handler = WorldGuard.getInstance().getPlatform().getSessionManager().get(player).getHandler(WaterBreathing.class);
        if (handler != null) {
            handler.setWaterBreathing(true);
        }
    }

    public void disableAmphibiousMode(LocalPlayer player) {
        WaterBreathing handler = WorldGuard.getInstance().getPlatform().getSessionManager().get(player).getHandler(WaterBreathing.class);
        if (handler != null) {
            handler.setWaterBreathing(false);
        }
    }

    public boolean hasAmphibiousMode(LocalPlayer player) {
        WaterBreathing handler = WorldGuard.getInstance().getPlatform().getSessionManager().get(player).getHandler(WaterBreathing.class);
        return handler != null && handler.hasWaterBreathing();
    }
}

