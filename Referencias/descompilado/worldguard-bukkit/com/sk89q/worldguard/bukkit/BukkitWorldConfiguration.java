/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.util.yaml.YAMLFormat
 *  com.sk89q.util.yaml.YAMLProcessor
 *  com.sk89q.worldedit.util.Location
 *  com.sk89q.worldedit.util.report.Unreported
 *  com.sk89q.worldedit.world.entity.EntityType
 *  com.sk89q.worldedit.world.entity.EntityTypes
 *  com.sk89q.worldedit.world.item.ItemTypes
 *  org.bukkit.potion.PotionEffectType
 *  org.yaml.snakeyaml.error.YAMLException
 */
package com.sk89q.worldguard.bukkit;

import com.sk89q.util.yaml.YAMLFormat;
import com.sk89q.util.yaml.YAMLProcessor;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.report.Unreported;
import com.sk89q.worldedit.world.entity.EntityType;
import com.sk89q.worldedit.world.entity.EntityTypes;
import com.sk89q.worldedit.world.item.ItemTypes;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.blacklist.Blacklist;
import com.sk89q.worldguard.blacklist.BlacklistLoggerHandler;
import com.sk89q.worldguard.blacklist.logger.ConsoleHandler;
import com.sk89q.worldguard.blacklist.logger.DatabaseHandler;
import com.sk89q.worldguard.blacklist.logger.FileHandler;
import com.sk89q.worldguard.blacklist.target.TargetMatcherParseException;
import com.sk89q.worldguard.blacklist.target.TargetMatcherParser;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.chest.BukkitSignChestProtection;
import com.sk89q.worldguard.bukkit.internal.TargetMatcherSet;
import com.sk89q.worldguard.chest.ChestProtection;
import com.sk89q.worldguard.commands.CommandUtils;
import com.sk89q.worldguard.config.YamlWorldConfiguration;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.potion.PotionEffectType;
import org.yaml.snakeyaml.error.YAMLException;

public class BukkitWorldConfiguration
extends YamlWorldConfiguration {
    private static final TargetMatcherParser matcherParser = new TargetMatcherParser();
    @Unreported
    private String worldName;
    @Unreported
    private ChestProtection chestProtection = new BukkitSignChestProtection();
    public Set<PotionEffectType> blockPotions;
    public TargetMatcherSet allowAllInteract;
    public TargetMatcherSet blockUseAtFeet;
    public boolean usePaperEntityOrigin;

    public BukkitWorldConfiguration(WorldGuardPlugin plugin, String worldName, YAMLProcessor parentConfig) {
        File baseFolder = new File(plugin.getDataFolder(), "worlds/" + worldName);
        File configFile = new File(baseFolder, "config.yml");
        this.blacklistFile = new File(baseFolder, "blacklist.txt");
        this.worldName = worldName;
        this.parentConfig = parentConfig;
        plugin.createDefaultConfiguration(configFile, "config_world.yml");
        plugin.createDefaultConfiguration(this.blacklistFile, "blacklist.txt");
        this.config = new YAMLProcessor(configFile, true, YAMLFormat.EXTENDED);
        this.loadConfiguration();
        if (this.summaryOnStart) {
            log.info("Loaded configuration for world '" + worldName + "'");
        }
    }

    private TargetMatcherSet getTargetMatchers(String node) {
        TargetMatcherSet set = new TargetMatcherSet();
        List inputs = this.parentConfig.getStringList(node, null);
        if (inputs == null || inputs.isEmpty()) {
            this.parentConfig.setProperty(node, new ArrayList());
        }
        if (this.config.getProperty(node) != null) {
            inputs = this.config.getStringList(node, null);
        }
        if (inputs == null || inputs.isEmpty()) {
            return set;
        }
        for (String input : inputs) {
            try {
                set.add(matcherParser.fromInput(input));
            }
            catch (TargetMatcherParseException e) {
                log.warning("Failed to parse the block / item type specified as '" + input + "'");
            }
        }
        return set;
    }

    @Override
    public void loadConfiguration() {
        try {
            this.config.load();
        }
        catch (IOException e) {
            log.log(Level.SEVERE, "Error reading configuration for world " + this.worldName + ": ", e);
        }
        catch (YAMLException e) {
            log.severe("Error parsing configuration for world " + this.worldName + ". ");
            throw e;
        }
        boolean needParentSave = false;
        this.summaryOnStart = this.getBoolean("summary-on-start", true);
        this.opPermissions = this.getBoolean("op-permissions", true);
        this.buildPermissions = this.getBoolean("build-permission-nodes.enable", false);
        this.buildPermissionDenyMessage = CommandUtils.replaceColorMacros(this.getString("build-permission-nodes.deny-message", "&eSorry, but you are not permitted to do that here."));
        this.strictEntitySpawn = this.getBoolean("event-handling.block-entity-spawns-with-untraceable-cause", false);
        this.allowAllInteract = this.getTargetMatchers("event-handling.interaction-whitelist");
        this.blockUseAtFeet = this.getTargetMatchers("event-handling.emit-block-use-at-feet");
        this.ignoreHopperMoveEvents = this.getBoolean("event-handling.ignore-hopper-item-move-events", false);
        this.breakDeniedHoppers = this.getBoolean("event-handling.break-hoppers-on-denied-move", true);
        this.usePaperEntityOrigin = this.getBoolean("regions.use-paper-entity-origin", false);
        this.itemDurability = this.getBoolean("protection.item-durability", true);
        this.removeInfiniteStacks = this.getBoolean("protection.remove-infinite-stacks", false);
        this.disableExpDrops = this.getBoolean("protection.disable-xp-orb-drops", false);
        needParentSave |= this.removeProperty("protection.disable-obsidian-generators");
        this.useMaxPriorityAssociation = this.getBoolean("protection.use-max-priority-association", false);
        this.blockPotions = new HashSet<PotionEffectType>();
        for (String potionName : this.getStringList("gameplay.block-potions", null)) {
            PotionEffectType effect = PotionEffectType.getByName((String)potionName);
            if (effect == null) {
                log.warning("Unknown potion effect type '" + potionName + "'");
                continue;
            }
            this.blockPotions.add(effect);
        }
        this.blockPotionsAlways = this.getBoolean("gameplay.block-potions-overly-reliably", false);
        this.disableConduitEffects = this.getBoolean("gameplay.disable-conduit-effects", false);
        this.simulateSponge = this.getBoolean("simulation.sponge.enable", false);
        this.spongeRadius = Math.max(1, this.getInt("simulation.sponge.radius", 3)) - 1;
        this.redstoneSponges = this.getBoolean("simulation.sponge.redstone", false);
        if (this.simulateSponge) {
            log.warning("Sponge simulation is deprecated for removal in a future version. We recommend using CraftBook's sponge simulation instead.");
        } else {
            needParentSave |= this.removeProperty("simulation");
        }
        this.pumpkinScuba = this.getBoolean("default.pumpkin-scuba", false);
        this.disableHealthRegain = this.getBoolean("default.disable-health-regain", false);
        this.noPhysicsGravel = this.getBoolean("physics.no-physics-gravel", false);
        this.noPhysicsSand = this.getBoolean("physics.no-physics-sand", false);
        this.ropeLadders = this.getBoolean("physics.vine-like-rope-ladders", false);
        this.allowPortalAnywhere = this.getBoolean("physics.allow-portal-anywhere", false);
        this.preventWaterDamage = new HashSet<String>(this.convertLegacyBlocks(this.getStringList("physics.disable-water-damage-blocks", null)));
        this.blockTNTExplosions = this.getBoolean("ignition.block-tnt", false);
        this.blockTNTBlockDamage = this.getBoolean("ignition.block-tnt-block-damage", false);
        this.blockLighter = this.getBoolean("ignition.block-lighter", false);
        this.preventLavaFire = this.getBoolean("fire.disable-lava-fire-spread", false);
        this.disableFireSpread = this.getBoolean("fire.disable-all-fire-spread", false);
        this.disableFireSpreadBlocks = new HashSet<String>(this.convertLegacyBlocks(this.getStringList("fire.disable-fire-spread-blocks", null)));
        this.allowedLavaSpreadOver = new HashSet<String>(this.convertLegacyBlocks(this.getStringList("fire.lava-spread-blocks", null)));
        this.blockCreeperExplosions = this.getBoolean("mobs.block-creeper-explosions", false);
        this.blockCreeperBlockDamage = this.getBoolean("mobs.block-creeper-block-damage", false);
        this.blockWitherExplosions = this.getBoolean("mobs.block-wither-explosions", false);
        this.blockWitherBlockDamage = this.getBoolean("mobs.block-wither-block-damage", false);
        this.blockWitherSkullExplosions = this.getBoolean("mobs.block-wither-skull-explosions", false);
        this.blockWitherSkullBlockDamage = this.getBoolean("mobs.block-wither-skull-block-damage", false);
        this.blockEnderDragonBlockDamage = this.getBoolean("mobs.block-enderdragon-block-damage", false);
        this.blockEnderDragonPortalCreation = this.getBoolean("mobs.block-enderdragon-portal-creation", false);
        this.blockFireballExplosions = this.getBoolean("mobs.block-fireball-explosions", false);
        this.blockFireballBlockDamage = this.getBoolean("mobs.block-fireball-block-damage", false);
        this.blockWindChargeExplosions = this.getBoolean("mobs.block-windcharge-explosions", false);
        this.antiWolfDumbness = this.getBoolean("mobs.anti-wolf-dumbness", false);
        this.allowTamedSpawns = this.getBoolean("mobs.allow-tamed-spawns", true);
        this.disableEndermanGriefing = this.getBoolean("mobs.disable-enderman-griefing", false);
        this.disableSnowmanTrails = this.getBoolean("mobs.disable-snowman-trails", false);
        this.blockEntityPaintingDestroy = this.getBoolean("mobs.block-painting-destroy", false);
        this.blockEntityItemFrameDestroy = this.getBoolean("mobs.block-item-frame-destroy", false);
        this.blockEntityArmorStandDestroy = this.getBoolean("mobs.block-armor-stand-destroy", false);
        this.blockPluginSpawning = this.getBoolean("mobs.block-plugin-spawning", true);
        this.blockGroundSlimes = this.getBoolean("mobs.block-above-ground-slimes", false);
        this.blockOtherExplosions = this.getBoolean("mobs.block-other-explosions", false);
        this.blockZombieDoorDestruction = this.getBoolean("mobs.block-zombie-door-destruction", false);
        this.blockEntityVehicleEntry = this.getBoolean("mobs.block-vehicle-entry", false);
        this.disableFallDamage = this.getBoolean("player-damage.disable-fall-damage", false);
        this.disableLavaDamage = this.getBoolean("player-damage.disable-lava-damage", false);
        this.disableFireDamage = this.getBoolean("player-damage.disable-fire-damage", false);
        this.disableLightningDamage = this.getBoolean("player-damage.disable-lightning-damage", false);
        this.disableDrowningDamage = this.getBoolean("player-damage.disable-drowning-damage", false);
        this.disableSuffocationDamage = this.getBoolean("player-damage.disable-suffocation-damage", false);
        this.disableContactDamage = this.getBoolean("player-damage.disable-contact-damage", false);
        this.teleportOnSuffocation = this.getBoolean("player-damage.teleport-on-suffocation", false);
        this.disableVoidDamage = this.getBoolean("player-damage.disable-void-damage", false);
        this.teleportOnVoid = this.getBoolean("player-damage.teleport-on-void-falling", false);
        this.safeFallOnVoid = this.getBoolean("player-damage.reset-fall-on-void-teleport", false);
        this.disableExplosionDamage = this.getBoolean("player-damage.disable-explosion-damage", false);
        this.disableMobDamage = this.getBoolean("player-damage.disable-mob-damage", false);
        this.disableDeathMessages = this.getBoolean("player-damage.disable-death-messages", false);
        this.signChestProtection = this.getBoolean("chest-protection.enable", false);
        this.disableSignChestProtectionCheck = this.getBoolean("chest-protection.disable-off-check", true);
        if (this.signChestProtection) {
            log.warning("Sign-based chest protection is deprecated for removal in a future version. See https://worldguard.enginehub.org/en/latest/chest-protection/ for details.");
        } else {
            needParentSave |= this.removeProperty("chest-protection");
        }
        this.disableCreatureCropTrampling = this.getBoolean("crops.disable-creature-trampling", false);
        this.disablePlayerCropTrampling = this.getBoolean("crops.disable-player-trampling", false);
        this.disableCreatureTurtleEggTrampling = this.getBoolean("turtle-egg.disable-creature-trampling", false);
        this.disablePlayerTurtleEggTrampling = this.getBoolean("turtle-egg.disable-player-trampling", false);
        this.disableCreatureSnifferEggTrampling = this.getBoolean("sniffer-egg.disable-creature-trampling", false);
        this.disablePlayerSnifferEggTrampling = this.getBoolean("sniffer-egg.disable-player-trampling", false);
        this.disallowedLightningBlocks = new HashSet<String>(this.convertLegacyBlocks(this.getStringList("weather.prevent-lightning-strike-blocks", null)));
        this.preventLightningFire = this.getBoolean("weather.disable-lightning-strike-fire", false);
        this.disableThunder = this.getBoolean("weather.disable-thunderstorm", false);
        this.disableWeather = this.getBoolean("weather.disable-weather", false);
        this.disablePigZap = this.getBoolean("weather.disable-pig-zombification", false);
        this.disableVillagerZap = this.getBoolean("weather.disable-villager-witchification", false);
        this.disableCreeperPower = this.getBoolean("weather.disable-powered-creepers", false);
        this.alwaysRaining = this.getBoolean("weather.always-raining", false);
        this.alwaysThundering = this.getBoolean("weather.always-thundering", false);
        this.disableMushroomSpread = this.getBoolean("dynamics.disable-mushroom-spread", false);
        this.disableIceMelting = this.getBoolean("dynamics.disable-ice-melting", false);
        this.disableSnowMelting = this.getBoolean("dynamics.disable-snow-melting", false);
        this.disableSnowFormation = this.getBoolean("dynamics.disable-snow-formation", false);
        this.disableIceFormation = this.getBoolean("dynamics.disable-ice-formation", false);
        this.disableLeafDecay = this.getBoolean("dynamics.disable-leaf-decay", false);
        this.disableGrassGrowth = this.getBoolean("dynamics.disable-grass-growth", false);
        this.disableMyceliumSpread = this.getBoolean("dynamics.disable-mycelium-spread", false);
        this.disableVineGrowth = this.getBoolean("dynamics.disable-vine-growth", false);
        this.disableRockGrowth = this.getBoolean("dynamics.disable-rock-growth", false);
        this.disableSculkGrowth = this.getBoolean("dynamics.disable-sculk-growth", false);
        this.disableCropGrowth = this.getBoolean("dynamics.disable-crop-growth", false);
        this.disableSoilDehydration = this.getBoolean("dynamics.disable-soil-dehydration", false);
        this.disableSoilMoistureChange = this.getBoolean("dynamics.disable-soil-moisture-change", false);
        this.disableCoralBlockFade = this.getBoolean("dynamics.disable-coral-block-fade", false);
        this.disableCopperBlockFade = this.getBoolean("dynamics.disable-copper-block-fade", false);
        this.allowedSnowFallOver = new HashSet<String>(this.convertLegacyBlocks(this.getStringList("dynamics.snow-fall-blocks", null)));
        this.useRegions = this.getBoolean("regions.enable", true);
        this.regionInvinciblityRemovesMobs = this.getBoolean("regions.invincibility-removes-mobs", false);
        this.regionCancelEmptyChatEvents = this.getBoolean("regions.cancel-chat-without-recipients", true);
        this.regionNetherPortalProtection = this.getBoolean("regions.nether-portal-protection", true);
        this.forceDefaultTitleTimes = this.config.getBoolean("regions.titles-always-use-default-times", true);
        this.fakePlayerBuildOverride = this.getBoolean("regions.fake-player-build-override", true);
        this.explosionFlagCancellation = this.getBoolean("regions.explosion-flags-block-entity-damage", true);
        this.highFreqFlags = this.getBoolean("regions.high-frequency-flags", false);
        this.checkLiquidFlow = this.getBoolean("regions.protect-against-liquid-flow", false);
        this.regionWand = this.convertLegacyItem(this.getString("regions.wand", ItemTypes.LEATHER.getId()));
        this.maxClaimVolume = this.getInt("regions.max-claim-volume", 30000);
        this.claimOnlyInsideExistingRegions = this.getBoolean("regions.claim-only-inside-existing-regions", false);
        this.setParentOnClaim = this.getString("regions.set-parent-on-claim", "");
        this.boundedLocationFlags = this.getBoolean("regions.location-flags-only-inside-regions", false);
        this.maxRegionCountPerPlayer = this.getInt("regions.max-region-count-per-player.default", 7);
        this.maxRegionCounts = new HashMap();
        this.maxRegionCounts.put(null, this.maxRegionCountPerPlayer);
        for (String key : this.getKeys("regions.max-region-count-per-player")) {
            Object val;
            if (key.equalsIgnoreCase("default") || !((val = this.getProperty("regions.max-region-count-per-player." + key)) instanceof Number)) continue;
            this.maxRegionCounts.put(key, ((Number)val).intValue());
        }
        this.blockCreatureSpawn = new HashSet();
        for (String creatureName : this.getStringList("mobs.block-creature-spawn", null)) {
            EntityType creature = EntityTypes.get((String)creatureName.toLowerCase());
            if (creature == null) {
                log.warning("Unknown entity type '" + creatureName + "'");
                continue;
            }
            this.blockCreatureSpawn.add(creature);
        }
        boolean useBlacklistAsWhitelist = this.getBoolean("blacklist.use-as-whitelist", false);
        boolean logConsole = this.getBoolean("blacklist.logging.console.enable", true);
        boolean logDatabase = this.getBoolean("blacklist.logging.database.enable", false);
        String dsn = this.getString("blacklist.logging.database.dsn", "jdbc:mysql://localhost:3306/minecraft");
        String user = this.getString("blacklist.logging.database.user", "root");
        String pass = this.getString("blacklist.logging.database.pass", "");
        String table = this.getString("blacklist.logging.database.table", "blacklist_events");
        boolean logFile = this.getBoolean("blacklist.logging.file.enable", false);
        String logFilePattern = this.getString("blacklist.logging.file.path", "worldguard/logs/%Y-%m-%d.log");
        int logFileCacheSize = Math.max(1, this.getInt("blacklist.logging.file.open-files", 10));
        try {
            if (this.blacklist != null) {
                this.blacklist.getLogger().close();
            }
            Blacklist blist = new Blacklist(useBlacklistAsWhitelist);
            blist.load(this.blacklistFile);
            if (blist.isEmpty()) {
                this.blacklist = null;
            } else {
                this.blacklist = blist;
                if (this.summaryOnStart) {
                    log.log(Level.INFO, "({0}) Blacklist loaded with {1} entries.", new Object[]{this.worldName, this.blacklist.getItemCount()});
                }
                BlacklistLoggerHandler blacklistLogger = blist.getLogger();
                if (logDatabase) {
                    blacklistLogger.addHandler(new DatabaseHandler(dsn, user, pass, table, this.worldName, log));
                }
                if (logConsole) {
                    blacklistLogger.addHandler(new ConsoleHandler(this.worldName, log));
                }
                if (logFile) {
                    FileHandler handler = new FileHandler(logFilePattern, logFileCacheSize, this.worldName, log);
                    blacklistLogger.addHandler(handler);
                }
            }
        }
        catch (FileNotFoundException e) {
            log.log(Level.WARNING, "WorldGuard blacklist does not exist.");
        }
        catch (IOException e) {
            log.log(Level.WARNING, "Could not load WorldGuard blacklist: " + e.getMessage());
        }
        if (this.summaryOnStart) {
            log.log(Level.INFO, this.blockTNTExplosions ? "(" + this.worldName + ") TNT ignition is blocked." : "(" + this.worldName + ") TNT ignition is PERMITTED.");
            log.log(Level.INFO, this.blockLighter ? "(" + this.worldName + ") Lighters are blocked." : "(" + this.worldName + ") Lighters are PERMITTED.");
            log.log(Level.INFO, this.preventLavaFire ? "(" + this.worldName + ") Lava fire is blocked." : "(" + this.worldName + ") Lava fire is PERMITTED.");
            if (this.disableFireSpread) {
                log.log(Level.INFO, "(" + this.worldName + ") All fire spread is disabled.");
            } else if (!this.disableFireSpreadBlocks.isEmpty()) {
                log.log(Level.INFO, "(" + this.worldName + ") Fire spread is limited to " + this.disableFireSpreadBlocks.size() + " block types.");
            } else {
                log.log(Level.INFO, "(" + this.worldName + ") Fire spread is UNRESTRICTED.");
            }
        }
        this.config.setHeader("#\r\n# WorldGuard's world configuration file\r\n#\r\n# This is a world configuration file. Anything placed into here will only\r\n# affect this world. If you don't put anything in this file, then the\r\n# settings will be inherited from the main configuration file.\r\n#\r\n# If you see {} below, that means that there are NO entries in this file.\r\n# Remove the {} and add your own entries.\r\n#\r\n");
        this.config.save();
        if (needParentSave) {
            this.parentConfig.save();
        }
    }

    private boolean removeProperty(String prop) {
        if (this.config.getProperty(prop) != null) {
            this.config.removeProperty(prop);
        }
        if (this.parentConfig.getProperty(prop) != null) {
            this.parentConfig.removeProperty(prop);
            return true;
        }
        return false;
    }

    public boolean isChestProtected(Location block, LocalPlayer player) {
        if (!this.signChestProtection) {
            return false;
        }
        if (player.hasPermission("worldguard.chest-protection.override") || player.hasPermission("worldguard.override.chest-protection")) {
            return false;
        }
        return this.chestProtection.isProtected(block, player);
    }

    public boolean isChestProtected(Location block) {
        return this.signChestProtection && this.chestProtection.isProtected(block, null);
    }

    public boolean isChestProtectedPlacement(Location block, LocalPlayer player) {
        if (!this.signChestProtection) {
            return false;
        }
        if (player.hasPermission("worldguard.chest-protection.override") || player.hasPermission("worldguard.override.chest-protection")) {
            return false;
        }
        return this.chestProtection.isProtectedPlacement(block, player);
    }

    public boolean isAdjacentChestProtected(Location block, LocalPlayer player) {
        if (!this.signChestProtection) {
            return false;
        }
        if (player.hasPermission("worldguard.chest-protection.override") || player.hasPermission("worldguard.override.chest-protection")) {
            return false;
        }
        return this.chestProtection.isAdjacentChestProtected(block, player);
    }

    public ChestProtection getChestProtection() {
        return this.chestProtection;
    }
}

