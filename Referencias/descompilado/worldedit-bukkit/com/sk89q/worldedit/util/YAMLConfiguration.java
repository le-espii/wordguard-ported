/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.apache.logging.log4j.Logger
 */
package com.sk89q.worldedit.util;

import com.google.common.collect.Lists;
import com.sk89q.util.yaml.YAMLProcessor;
import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.util.report.Unreported;
import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import org.apache.logging.log4j.Logger;

public class YAMLConfiguration
extends LocalConfiguration {
    @Unreported
    protected final YAMLProcessor config;
    @Unreported
    protected final Logger logger;

    public YAMLConfiguration(YAMLProcessor config, Logger logger) {
        this.config = config;
        this.logger = logger;
    }

    @Override
    public void load() {
        try {
            this.config.load();
        }
        catch (IOException e) {
            this.logger.warn("Error loading WorldEdit configuration", (Throwable)e);
        }
        this.profile = this.config.getBoolean("debug", this.profile);
        this.traceUnflushedSessions = this.config.getBoolean("debugging.trace-unflushed-sessions", this.traceUnflushedSessions);
        this.wandItem = this.convertLegacyItem(this.config.getString("wand-item", this.wandItem)).toLowerCase(Locale.ROOT);
        this.defaultChangeLimit = Math.max(-1, this.config.getInt("limits.max-blocks-changed.default", this.defaultChangeLimit));
        this.maxChangeLimit = Math.max(-1, this.config.getInt("limits.max-blocks-changed.maximum", this.maxChangeLimit));
        this.defaultVerticalHeight = Math.max(1, this.config.getInt("limits.vertical-height.default", this.defaultVerticalHeight));
        this.defaultMaxPolygonalPoints = Math.max(-1, this.config.getInt("limits.max-polygonal-points.default", this.defaultMaxPolygonalPoints));
        this.maxPolygonalPoints = Math.max(-1, this.config.getInt("limits.max-polygonal-points.maximum", this.maxPolygonalPoints));
        this.defaultMaxPolyhedronPoints = Math.max(-1, this.config.getInt("limits.max-polyhedron-points.default", this.defaultMaxPolyhedronPoints));
        this.maxPolyhedronPoints = Math.max(-1, this.config.getInt("limits.max-polyhedron-points.maximum", this.maxPolyhedronPoints));
        this.maxRadius = Math.max(-1, this.config.getInt("limits.max-radius", this.maxRadius));
        this.maxBrushRadius = this.config.getInt("limits.max-brush-radius", this.maxBrushRadius);
        this.maxSuperPickaxeSize = Math.max(1, this.config.getInt("limits.max-super-pickaxe-size", this.maxSuperPickaxeSize));
        this.butcherDefaultRadius = Math.max(-1, this.config.getInt("limits.butcher-radius.default", this.butcherDefaultRadius));
        this.butcherMaxRadius = Math.max(-1, this.config.getInt("limits.butcher-radius.maximum", this.butcherMaxRadius));
        this.disallowedBlocks = new HashSet<String>(this.config.getStringList("limits.disallowed-blocks", Lists.newArrayList((Object[])this.getDefaultDisallowedBlocks())));
        this.allowedDataCycleBlocks = new HashSet<String>(this.config.getStringList("limits.allowed-data-cycle-blocks", null));
        this.registerHelp = this.config.getBoolean("register-help", true);
        this.logCommands = this.config.getBoolean("logging.log-commands", this.logCommands);
        this.logFile = this.config.getString("logging.file", this.logFile);
        this.logFormat = this.config.getString("logging.format", this.logFormat);
        this.superPickaxeDrop = this.config.getBoolean("super-pickaxe.drop-items", this.superPickaxeDrop);
        this.superPickaxeManyDrop = this.config.getBoolean("super-pickaxe.many-drop-items", this.superPickaxeManyDrop);
        this.useInventory = this.config.getBoolean("use-inventory.enable", this.useInventory);
        this.useInventoryOverride = this.config.getBoolean("use-inventory.allow-override", this.useInventoryOverride);
        this.useInventoryCreativeOverride = this.config.getBoolean("use-inventory.creative-mode-overrides", this.useInventoryCreativeOverride);
        this.navigationWand = this.convertLegacyItem(this.config.getString("navigation-wand.item", this.navigationWand)).toLowerCase(Locale.ROOT);
        this.navigationWandMaxDistance = this.config.getInt("navigation-wand.max-distance", this.navigationWandMaxDistance);
        this.navigationUseGlass = this.config.getBoolean("navigation.use-glass", this.navigationUseGlass);
        this.scriptTimeout = this.config.getInt("scripting.timeout", this.scriptTimeout);
        this.scriptsDir = this.config.getString("scripting.dir", this.scriptsDir);
        this.calculationTimeout = this.config.getInt("calculation.timeout", this.calculationTimeout);
        this.maxCalculationTimeout = this.config.getInt("calculation.max-timeout", this.maxCalculationTimeout);
        this.saveDir = this.config.getString("saving.dir", this.saveDir);
        this.allowSymlinks = this.config.getBoolean("files.allow-symbolic-links", false);
        LocalSession.MAX_HISTORY_SIZE = Math.max(0, this.config.getInt("history.size", 15));
        SessionManager.EXPIRATION_GRACE = this.config.getInt("history.expiration", 10) * 60 * 1000;
        this.showHelpInfo = this.config.getBoolean("show-help-on-first-use", true);
        this.serverSideCUI = this.config.getBoolean("server-side-cui", true);
        String snapshotsDir = this.config.getString("snapshots.directory", "");
        boolean experimentalSnapshots = this.config.getBoolean("snapshots.experimental", false);
        this.initializeSnapshotConfiguration(snapshotsDir, experimentalSnapshots);
        String type = this.config.getString("shell-save-type", "").trim();
        this.shellSaveType = type.isEmpty() ? null : type;
        this.extendedYLimit = this.config.getBoolean("compat.extended-y-limit", false);
        this.setDefaultLocaleName(this.config.getString("default-locale", this.defaultLocaleName));
        this.commandBlockSupport = this.config.getBoolean("command-block-support", false);
    }

    public void unload() {
    }
}

