/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Logger
 */
package com.sk89q.worldedit.util;

import com.sk89q.util.StringUtil;
import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.internal.util.LogManagerCompat;
import com.sk89q.worldedit.util.report.Unreported;
import com.sk89q.worldedit.world.registry.LegacyMapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import org.apache.logging.log4j.Logger;

public class PropertiesConfiguration
extends LocalConfiguration {
    @Unreported
    private static final Logger LOGGER = LogManagerCompat.getLogger();
    @Unreported
    protected Properties properties;
    @Unreported
    protected File path;

    public PropertiesConfiguration(Path path) {
        this.path = path.toFile();
        this.properties = new Properties();
    }

    @Deprecated
    public PropertiesConfiguration(File path) {
        this(path.toPath());
    }

    @Override
    public void load() {
        try (FileInputStream stream2 = new FileInputStream(this.path);){
            this.properties.load(stream2);
        }
        catch (FileNotFoundException stream2) {
        }
        catch (IOException e) {
            LOGGER.warn("Failed to read configuration", (Throwable)e);
        }
        this.loadExtra();
        this.profile = this.getBool("profile", this.profile);
        this.traceUnflushedSessions = this.getBool("trace-unflushed-sessions", this.traceUnflushedSessions);
        this.disallowedBlocks = this.getStringSet("disallowed-blocks", this.getDefaultDisallowedBlocks());
        this.defaultChangeLimit = this.getInt("default-max-changed-blocks", this.defaultChangeLimit);
        this.maxChangeLimit = this.getInt("max-changed-blocks", this.maxChangeLimit);
        this.defaultVerticalHeight = this.getInt("default-vertical-height", this.defaultVerticalHeight);
        this.defaultMaxPolygonalPoints = this.getInt("default-max-polygon-points", this.defaultMaxPolygonalPoints);
        this.maxPolygonalPoints = this.getInt("max-polygon-points", this.maxPolygonalPoints);
        this.defaultMaxPolyhedronPoints = this.getInt("default-max-polyhedron-points", this.defaultMaxPolyhedronPoints);
        this.maxPolyhedronPoints = this.getInt("max-polyhedron-points", this.maxPolyhedronPoints);
        this.shellSaveType = this.getString("shell-save-type", this.shellSaveType);
        this.maxRadius = this.getInt("max-radius", this.maxRadius);
        this.maxSuperPickaxeSize = this.getInt("max-super-pickaxe-size", this.maxSuperPickaxeSize);
        this.maxBrushRadius = this.getInt("max-brush-radius", this.maxBrushRadius);
        this.logCommands = this.getBool("log-commands", this.logCommands);
        this.logFile = this.getString("log-file", this.logFile);
        this.logFormat = this.getString("log-format", this.logFormat);
        this.registerHelp = this.getBool("register-help", this.registerHelp);
        this.wandItem = this.getString("wand-item", this.wandItem).toLowerCase(Locale.ROOT);
        try {
            this.wandItem = LegacyMapper.getInstance().getItemFromLegacy(Integer.parseInt(this.wandItem)).id();
        }
        catch (Throwable e) {
            // empty catch block
        }
        this.superPickaxeDrop = this.getBool("super-pickaxe-drop-items", this.superPickaxeDrop);
        this.superPickaxeManyDrop = this.getBool("super-pickaxe-many-drop-items", this.superPickaxeManyDrop);
        this.useInventory = this.getBool("use-inventory", this.useInventory);
        this.useInventoryOverride = this.getBool("use-inventory-override", this.useInventoryOverride);
        this.useInventoryCreativeOverride = this.getBool("use-inventory-creative-override", this.useInventoryCreativeOverride);
        this.navigationWand = this.getString("nav-wand-item", this.navigationWand).toLowerCase(Locale.ROOT);
        try {
            this.navigationWand = LegacyMapper.getInstance().getItemFromLegacy(Integer.parseInt(this.navigationWand)).id();
        }
        catch (Throwable e) {
            // empty catch block
        }
        this.navigationWandMaxDistance = this.getInt("nav-wand-distance", this.navigationWandMaxDistance);
        this.navigationUseGlass = this.getBool("nav-use-glass", this.navigationUseGlass);
        this.scriptTimeout = this.getInt("scripting-timeout", this.scriptTimeout);
        this.calculationTimeout = this.getInt("calculation-timeout", this.calculationTimeout);
        this.maxCalculationTimeout = this.getInt("max-calculation-timeout", this.maxCalculationTimeout);
        this.saveDir = this.getString("schematic-save-dir", this.saveDir);
        this.scriptsDir = this.getString("craftscript-dir", this.scriptsDir);
        this.butcherDefaultRadius = this.getInt("butcher-default-radius", this.butcherDefaultRadius);
        this.butcherMaxRadius = this.getInt("butcher-max-radius", this.butcherMaxRadius);
        this.allowSymlinks = this.getBool("allow-symbolic-links", this.allowSymlinks);
        this.serverSideCUI = this.getBool("server-side-cui", this.serverSideCUI);
        this.extendedYLimit = this.getBool("extended-y-limit", this.extendedYLimit);
        this.setDefaultLocaleName(this.getString("default-locale", this.defaultLocaleName));
        this.commandBlockSupport = this.getBool("command-block-support", this.commandBlockSupport);
        LocalSession.MAX_HISTORY_SIZE = Math.max(15, this.getInt("history-size", 15));
        String snapshotsDir = this.getString("snapshots-dir", "");
        boolean experimentalSnapshots = this.getBool("snapshots-experimental", false);
        this.initializeSnapshotConfiguration(snapshotsDir, experimentalSnapshots);
        this.path.getParentFile().mkdirs();
        try (FileOutputStream output = new FileOutputStream(this.path);){
            this.properties.store(output, "Don't put comments; they get removed");
        }
        catch (IOException e) {
            LOGGER.warn("Failed to write configuration", (Throwable)e);
        }
    }

    protected void loadExtra() {
    }

    protected String getString(String key, String def) {
        String val;
        if (def == null) {
            def = "";
        }
        if ((val = this.properties.getProperty(key)) == null) {
            this.properties.setProperty(key, def);
            return def;
        }
        return val;
    }

    protected boolean getBool(String key, boolean def) {
        String val = this.properties.getProperty(key);
        if (val == null) {
            this.properties.setProperty(key, def ? "true" : "false");
            return def;
        }
        return val.equalsIgnoreCase("true") || val.equals("1");
    }

    protected int getInt(String key, int def) {
        String val = this.properties.getProperty(key);
        if (val == null) {
            this.properties.setProperty(key, String.valueOf(def));
            return def;
        }
        try {
            return Integer.parseInt(val);
        }
        catch (NumberFormatException e) {
            this.properties.setProperty(key, String.valueOf(def));
            return def;
        }
    }

    protected double getDouble(String key, double def) {
        String val = this.properties.getProperty(key);
        if (val == null) {
            this.properties.setProperty(key, String.valueOf(def));
            return def;
        }
        try {
            return Double.parseDouble(val);
        }
        catch (NumberFormatException e) {
            this.properties.setProperty(key, String.valueOf(def));
            return def;
        }
    }

    protected Set<Integer> getIntSet(String key, int[] def) {
        String[] parts;
        String val = this.properties.getProperty(key);
        if (val == null) {
            this.properties.setProperty(key, StringUtil.joinString(def, ",", 0));
            HashSet<Integer> set = new HashSet<Integer>();
            for (int i : def) {
                set.add(i);
            }
            return set;
        }
        HashSet<Integer> set = new HashSet<Integer>();
        for (String part : parts = val.split(",")) {
            try {
                int v = Integer.parseInt(part.trim());
                set.add(v);
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return set;
    }

    protected Set<String> getStringSet(String key, String[] def) {
        String[] parts;
        String val = this.properties.getProperty(key);
        if (val == null) {
            this.properties.setProperty(key, StringUtil.joinString(def, ",", 0));
            return new HashSet<String>(Arrays.asList(def));
        }
        HashSet<String> set = new HashSet<String>();
        for (String part : parts = val.split(",")) {
            try {
                String v = part.trim();
                set.add(v);
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return set;
    }
}

