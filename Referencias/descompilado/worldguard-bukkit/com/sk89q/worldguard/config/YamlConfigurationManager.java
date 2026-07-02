/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.sk89q.util.yaml.YAMLFormat
 *  com.sk89q.util.yaml.YAMLProcessor
 *  com.sk89q.worldedit.util.report.Unreported
 */
package com.sk89q.worldguard.config;

import com.google.common.collect.ImmutableMap;
import com.sk89q.util.yaml.YAMLFormat;
import com.sk89q.util.yaml.YAMLProcessor;
import com.sk89q.worldedit.util.report.Unreported;
import com.sk89q.worldguard.config.ConfigurationManager;
import com.sk89q.worldguard.protection.managers.storage.DriverType;
import com.sk89q.worldguard.protection.managers.storage.file.DirectoryYamlDriver;
import com.sk89q.worldguard.protection.managers.storage.sql.SQLDriver;
import com.sk89q.worldguard.util.sql.DataSourceConfig;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class YamlConfigurationManager
extends ConfigurationManager {
    @Unreported
    private YAMLProcessor config;

    public abstract void copyDefaults();

    @Override
    public void load() {
        this.copyDefaults();
        this.config = new YAMLProcessor(new File(this.getDataFolder(), "config.yml"), true, YAMLFormat.EXTENDED);
        try {
            this.config.load();
        }
        catch (IOException e) {
            log.severe("Error reading configuration for global config: ");
            e.printStackTrace();
        }
        this.config.removeProperty("suppress-tick-sync-warnings");
        this.migrateRegionsToUuid = this.config.getBoolean("regions.uuid-migration.perform-on-next-start", true);
        this.keepUnresolvedNames = this.config.getBoolean("regions.uuid-migration.keep-names-that-lack-uuids", true);
        this.useRegionsCreatureSpawnEvent = this.config.getBoolean("regions.use-creature-spawn-event", true);
        this.disableDefaultBypass = this.config.getBoolean("regions.disable-bypass-by-default", false);
        this.announceBypassStatus = this.config.getBoolean("regions.announce-bypass-status", false);
        this.useGodPermission = this.config.getBoolean("auto-invincible", this.config.getBoolean("auto-invincible-permission", false));
        this.useGodGroup = this.config.getBoolean("auto-invincible-group", false);
        this.useAmphibiousGroup = this.config.getBoolean("auto-no-drowning-group", false);
        this.config.removeProperty("auto-invincible-permission");
        this.usePlayerMove = this.config.getBoolean("use-player-move-event", true);
        this.usePlayerTeleports = this.config.getBoolean("use-player-teleports", true);
        this.particleEffects = this.config.getBoolean("use-particle-effects", true);
        this.disablePermissionCache = this.config.getBoolean("disable-permission-cache", false);
        this.deopOnJoin = this.config.getBoolean("security.deop-everyone-on-join", false);
        this.blockInGameOp = this.config.getBoolean("security.block-in-game-op-command", false);
        this.hostKeys = new HashMap();
        Object hostKeysRaw = this.config.getProperty("host-keys");
        if (!(hostKeysRaw instanceof Map)) {
            this.config.setProperty("host-keys", new HashMap());
        } else {
            for (Map.Entry entry : ((Map)hostKeysRaw).entrySet()) {
                String key = String.valueOf(entry.getKey());
                String value = String.valueOf(entry.getValue());
                this.hostKeys.put(key.toLowerCase(), value);
            }
        }
        this.hostKeysAllowFMLClients = this.config.getBoolean("security.host-keys-allow-forge-clients", false);
        boolean useSqlDatabase = this.config.getBoolean("regions.sql.use", false);
        String sqlDsn = this.config.getString("regions.sql.dsn", "jdbc:mysql://localhost/worldguard");
        String sqlUsername = this.config.getString("regions.sql.username", "worldguard");
        String sqlPassword = this.config.getString("regions.sql.password", "worldguard");
        String sqlTablePrefix = this.config.getString("regions.sql.table-prefix", "");
        if (!useSqlDatabase) {
            this.config.removeProperty("regions.sql");
        } else {
            log.warning("SQL support for WorldGuard region storage is deprecated for removal in a future version. Please migrate to YAML storage.");
            log.warning("For details, see https://worldguard.enginehub.org/en/latest/regions/storage/");
        }
        DataSourceConfig dataSourceConfig = new DataSourceConfig(sqlDsn, sqlUsername, sqlPassword, sqlTablePrefix);
        SQLDriver sqlDriver = new SQLDriver(dataSourceConfig);
        DirectoryYamlDriver yamlDriver = new DirectoryYamlDriver(this.getWorldsDataFolder(), "regions.yml");
        this.regionStoreDriverMap = ImmutableMap.builder().put((Object)DriverType.MYSQL, (Object)sqlDriver).put((Object)DriverType.YAML, (Object)yamlDriver).build();
        this.selectedRegionStoreDriver = useSqlDatabase ? sqlDriver : yamlDriver;
        this.postLoad();
        this.config.setHeader("#\r\n# WorldGuard's main configuration file\r\n#\r\n# This is the global configuration file. Anything placed into here will\r\n# be applied to all worlds. However, each world has its own configuration\r\n# file to allow you to replace most settings in here for that world only.\r\n#\r\n# About editing this file:\r\n# - DO NOT USE TABS. You MUST use spaces or Bukkit will complain. If\r\n#   you use an editor like Notepad++ (recommended for Windows users), you\r\n#   must configure it to \"replace tabs with spaces.\" In Notepad++, this can\r\n#   be changed in Settings > Preferences > Language Menu.\r\n# - Don't get rid of the indents. They are indented so some entries are\r\n#   in categories (like \"enforce-single-session\" is in the \"protection\"\r\n#   category.\r\n# - If you want to check the format of this file before putting it\r\n#   into WorldGuard, paste it into http://yaml-online-parser.appspot.com/\r\n#   and see if it gives \"ERROR:\".\r\n# - Lines starting with # are comments and so they are ignored.\r\n#\r\n");
    }

    public void postLoad() {
    }

    public YAMLProcessor getConfig() {
        return this.config;
    }

    @Override
    public void disableUuidMigration() {
        this.config.setProperty("regions.uuid-migration.perform-on-next-start", (Object)false);
        if (!this.config.save()) {
            log.severe("Error saving configuration!");
        }
    }
}

