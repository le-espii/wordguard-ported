/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.apache.logging.log4j.Logger
 */
package com.sk89q.worldedit;

import com.google.common.collect.Lists;
import com.sk89q.worldedit.internal.util.LogManagerCompat;
import com.sk89q.worldedit.util.formatting.component.TextUtils;
import com.sk89q.worldedit.util.io.file.ArchiveNioSupports;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldedit.world.registry.LegacyMapper;
import com.sk89q.worldedit.world.snapshot.SnapshotRepository;
import com.sk89q.worldedit.world.snapshot.experimental.SnapshotDatabase;
import com.sk89q.worldedit.world.snapshot.experimental.fs.FileSystemSnapshotDatabase;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import org.apache.logging.log4j.Logger;

public abstract class LocalConfiguration {
    private static final Logger LOGGER = LogManagerCompat.getLogger();
    public boolean profile = false;
    public boolean traceUnflushedSessions = true;
    public Set<String> disallowedBlocks = new HashSet<String>();
    public int defaultChangeLimit = -1;
    public int maxChangeLimit = -1;
    public int defaultVerticalHeight = 256;
    public int defaultMaxPolygonalPoints = -1;
    public int maxPolygonalPoints = 20;
    public int defaultMaxPolyhedronPoints = -1;
    public int maxPolyhedronPoints = 20;
    public String shellSaveType = "";
    public boolean snapshotsConfigured = false;
    public SnapshotRepository snapshotRepo = null;
    public SnapshotDatabase snapshotDatabase = null;
    public int maxRadius = -1;
    public int maxSuperPickaxeSize = 5;
    public int maxBrushRadius = 6;
    public boolean logCommands = false;
    public String logFile = "";
    public String logFormat = "[%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s]: %5$s%6$s%n";
    public boolean registerHelp = true;
    public String wandItem = "minecraft:wooden_axe";
    public boolean superPickaxeDrop = true;
    public boolean superPickaxeManyDrop = true;
    public boolean useInventory = false;
    public boolean useInventoryOverride = false;
    public boolean useInventoryCreativeOverride = false;
    public boolean navigationUseGlass = true;
    public String navigationWand = "minecraft:compass";
    public int navigationWandMaxDistance = 50;
    public int scriptTimeout = 3000;
    public int calculationTimeout = 100;
    public int maxCalculationTimeout = 300;
    public Set<String> allowedDataCycleBlocks = new HashSet<String>();
    public String saveDir = "schematics";
    public String scriptsDir = "craftscripts";
    public boolean showHelpInfo = true;
    public int butcherDefaultRadius = -1;
    public int butcherMaxRadius = -1;
    public boolean allowSymlinks = false;
    public boolean serverSideCUI = true;
    public boolean extendedYLimit = false;
    public boolean commandBlockSupport = false;
    public String defaultLocaleName = "default";
    public Locale defaultLocale = Locale.getDefault();

    protected String[] getDefaultDisallowedBlocks() {
        ArrayList blockTypes = Lists.newArrayList((Object[])new BlockType[]{BlockTypes.OAK_SAPLING, BlockTypes.JUNGLE_SAPLING, BlockTypes.DARK_OAK_SAPLING, BlockTypes.SPRUCE_SAPLING, BlockTypes.BIRCH_SAPLING, BlockTypes.ACACIA_SAPLING, BlockTypes.BLACK_BED, BlockTypes.BLUE_BED, BlockTypes.BROWN_BED, BlockTypes.CYAN_BED, BlockTypes.GRAY_BED, BlockTypes.GREEN_BED, BlockTypes.LIGHT_BLUE_BED, BlockTypes.LIGHT_GRAY_BED, BlockTypes.LIME_BED, BlockTypes.MAGENTA_BED, BlockTypes.ORANGE_BED, BlockTypes.PINK_BED, BlockTypes.PURPLE_BED, BlockTypes.RED_BED, BlockTypes.WHITE_BED, BlockTypes.YELLOW_BED, BlockTypes.POWERED_RAIL, BlockTypes.DETECTOR_RAIL, BlockTypes.GRASS, BlockTypes.SHORT_GRASS, BlockTypes.DEAD_BUSH, BlockTypes.MOVING_PISTON, BlockTypes.PISTON_HEAD, BlockTypes.SUNFLOWER, BlockTypes.ROSE_BUSH, BlockTypes.DANDELION, BlockTypes.POPPY, BlockTypes.BROWN_MUSHROOM, BlockTypes.RED_MUSHROOM, BlockTypes.TNT, BlockTypes.TORCH, BlockTypes.FIRE, BlockTypes.REDSTONE_WIRE, BlockTypes.WHEAT, BlockTypes.POTATOES, BlockTypes.CARROTS, BlockTypes.MELON_STEM, BlockTypes.PUMPKIN_STEM, BlockTypes.BEETROOTS, BlockTypes.RAIL, BlockTypes.LEVER, BlockTypes.REDSTONE_TORCH, BlockTypes.REDSTONE_WALL_TORCH, BlockTypes.REPEATER, BlockTypes.COMPARATOR, BlockTypes.STONE_BUTTON, BlockTypes.BIRCH_BUTTON, BlockTypes.ACACIA_BUTTON, BlockTypes.DARK_OAK_BUTTON, BlockTypes.JUNGLE_BUTTON, BlockTypes.OAK_BUTTON, BlockTypes.SPRUCE_BUTTON, BlockTypes.CACTUS, BlockTypes.SUGAR_CANE, BlockTypes.BEDROCK});
        return (String[])blockTypes.stream().filter(Objects::nonNull).map(BlockType::id).toArray(String[]::new);
    }

    public abstract void load();

    @Deprecated
    public File getWorkingDirectory() {
        return this.getWorkingDirectoryPath().toFile();
    }

    public Path getWorkingDirectoryPath() {
        return Paths.get(".", new String[0]);
    }

    public void initializeSnapshotConfiguration(String directory, boolean experimental) {
        this.snapshotRepo = null;
        this.snapshotDatabase = null;
        this.snapshotsConfigured = false;
        if (!directory.isEmpty()) {
            if (experimental) {
                try {
                    this.snapshotDatabase = FileSystemSnapshotDatabase.maybeCreate(Paths.get(directory, new String[0]), ArchiveNioSupports.combined());
                    this.snapshotsConfigured = true;
                }
                catch (IOException e) {
                    LOGGER.warn("Failed to open snapshotDatabase", (Throwable)e);
                }
            } else {
                this.snapshotRepo = new SnapshotRepository(directory);
                this.snapshotsConfigured = true;
            }
        }
    }

    public String convertLegacyItem(String legacy) {
        String item = legacy;
        try {
            String[] splitter = item.split(":", 2);
            int id = 0;
            int data = 0;
            if (splitter.length == 1) {
                id = Integer.parseInt(item);
            } else {
                id = Integer.parseInt(splitter[0]);
                data = Byte.parseByte(splitter[1]);
            }
            item = LegacyMapper.getInstance().getItemFromLegacy(id, data).id();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return item;
    }

    public void setDefaultLocaleName(String localeName) {
        this.defaultLocaleName = localeName;
        this.defaultLocale = localeName.equals("default") ? Locale.getDefault() : TextUtils.getLocaleByMinecraftTag(localeName);
    }
}

