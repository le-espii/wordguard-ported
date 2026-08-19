package com.sk89q.worldguard.mod;

import com.sk89q.util.yaml.YAMLFormat;
import com.sk89q.util.yaml.YAMLProcessor;
import com.sk89q.worldedit.world.entity.EntityType;
import com.sk89q.worldedit.world.entity.EntityTypes;
import com.sk89q.worldedit.world.item.ItemTypes;
import com.sk89q.worldguard.blacklist.Blacklist;
import com.sk89q.worldguard.commands.CommandUtils;
import com.sk89q.worldguard.config.YamlWorldConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;

/**
 * Puerta de enlace del port a la lectura real de worlds/&lt;mundo&gt;/config.yml (hallazgo C2
 * de la auditoría). Antes, loadConfiguration() estaba vacío y todos los campos de
 * WorldConfiguration usaban el valor por defecto de Java sin que el YAML tuviera ningún
 * efecto. La lista de claves y valores por defecto está portada línea por línea de
 * BukkitWorldConfiguration#loadConfiguration en el WorldGuard-Bukkit original, salvo los
 * campos que ese plugin declara a mayores de WorldConfiguration y que dependen de la API de
 * Bukkit (blockPotions/allowAllInteract/blockUseAtFeet/usePaperEntityOrigin, protección de
 * cofres por cartel): esos quedan fuera de alcance de este port por ahora.
 */
public class ModWorldConfiguration extends YamlWorldConfiguration {
    private final String worldName;

    public ModWorldConfiguration(String worldName, File dataFolder, YAMLProcessor parentConfig) {
        File baseFolder = new File(dataFolder, "worlds/" + worldName);
        File configFile = new File(baseFolder, "config.yml");
        this.blacklistFile = new File(baseFolder, "blacklist.txt");
        this.worldName = worldName;
        this.parentConfig = parentConfig;
        ModConfigurationManager.ensureFileExists(configFile);
        this.config = new YAMLProcessor(configFile, true, YAMLFormat.EXTENDED);
        loadConfiguration();
    }

    @Override
    public void loadConfiguration() {
        try {
            this.config.load();
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error reading configuration for world " + worldName + ": ", e);
        }

        boolean needParentSave = false;

        this.summaryOnStart = getBoolean("summary-on-start", true);
        this.opPermissions = getBoolean("op-permissions", true);
        this.buildPermissions = getBoolean("build-permission-nodes.enable", false);
        this.buildPermissionDenyMessage = CommandUtils.replaceColorMacros(
            getString("build-permission-nodes.deny-message", "&eSorry, but you are not permitted to do that here."));
        this.strictEntitySpawn = getBoolean("event-handling.block-entity-spawns-with-untraceable-cause", false);
        this.ignoreHopperMoveEvents = getBoolean("event-handling.ignore-hopper-item-move-events", false);
        this.breakDeniedHoppers = getBoolean("event-handling.break-hoppers-on-denied-move", true);
        this.itemDurability = getBoolean("protection.item-durability", true);
        this.removeInfiniteStacks = getBoolean("protection.remove-infinite-stacks", false);
        this.disableExpDrops = getBoolean("protection.disable-xp-orb-drops", false);
        this.useMaxPriorityAssociation = getBoolean("protection.use-max-priority-association", false);

        this.blockPotionsAlways = getBoolean("gameplay.block-potions-overly-reliably", false);
        this.disableConduitEffects = getBoolean("gameplay.disable-conduit-effects", false);

        this.simulateSponge = getBoolean("simulation.sponge.enable", false);
        this.spongeRadius = Math.max(1, getInt("simulation.sponge.radius", 3)) - 1;
        this.redstoneSponges = getBoolean("simulation.sponge.redstone", false);
        if (!this.simulateSponge) {
            needParentSave |= removeProperty("simulation");
        }

        this.pumpkinScuba = getBoolean("default.pumpkin-scuba", false);
        this.disableHealthRegain = getBoolean("default.disable-health-regain", false);

        this.noPhysicsGravel = getBoolean("physics.no-physics-gravel", false);
        this.noPhysicsSand = getBoolean("physics.no-physics-sand", false);
        this.ropeLadders = getBoolean("physics.vine-like-rope-ladders", false);
        this.allowPortalAnywhere = getBoolean("physics.allow-portal-anywhere", false);
        this.preventWaterDamage = new HashSet<>(convertLegacyBlocks(getStringList("physics.disable-water-damage-blocks", null)));

        this.blockTNTExplosions = getBoolean("ignition.block-tnt", false);
        this.blockTNTBlockDamage = getBoolean("ignition.block-tnt-block-damage", false);
        this.blockLighter = getBoolean("ignition.block-lighter", false);

        this.preventLavaFire = getBoolean("fire.disable-lava-fire-spread", false);
        this.disableFireSpread = getBoolean("fire.disable-all-fire-spread", false);
        this.disableFireSpreadBlocks = new HashSet<>(convertLegacyBlocks(getStringList("fire.disable-fire-spread-blocks", null)));
        this.allowedLavaSpreadOver = new HashSet<>(convertLegacyBlocks(getStringList("fire.lava-spread-blocks", null)));

        this.blockCreeperExplosions = getBoolean("mobs.block-creeper-explosions", false);
        this.blockCreeperBlockDamage = getBoolean("mobs.block-creeper-block-damage", false);
        this.blockWitherExplosions = getBoolean("mobs.block-wither-explosions", false);
        this.blockWitherBlockDamage = getBoolean("mobs.block-wither-block-damage", false);
        this.blockWitherSkullExplosions = getBoolean("mobs.block-wither-skull-explosions", false);
        this.blockWitherSkullBlockDamage = getBoolean("mobs.block-wither-skull-block-damage", false);
        this.blockEnderDragonBlockDamage = getBoolean("mobs.block-enderdragon-block-damage", false);
        this.blockEnderDragonPortalCreation = getBoolean("mobs.block-enderdragon-portal-creation", false);
        this.blockFireballExplosions = getBoolean("mobs.block-fireball-explosions", false);
        this.blockFireballBlockDamage = getBoolean("mobs.block-fireball-block-damage", false);
        this.blockWindChargeExplosions = getBoolean("mobs.block-windcharge-explosions", false);
        this.antiWolfDumbness = getBoolean("mobs.anti-wolf-dumbness", false);
        this.allowTamedSpawns = getBoolean("mobs.allow-tamed-spawns", true);
        this.disableEndermanGriefing = getBoolean("mobs.disable-enderman-griefing", false);
        this.disableSnowmanTrails = getBoolean("mobs.disable-snowman-trails", false);
        this.blockEntityPaintingDestroy = getBoolean("mobs.block-painting-destroy", false);
        this.blockEntityItemFrameDestroy = getBoolean("mobs.block-item-frame-destroy", false);
        this.blockEntityArmorStandDestroy = getBoolean("mobs.block-armor-stand-destroy", false);
        this.blockPluginSpawning = getBoolean("mobs.block-plugin-spawning", true);
        this.blockGroundSlimes = getBoolean("mobs.block-above-ground-slimes", false);
        this.blockOtherExplosions = getBoolean("mobs.block-other-explosions", false);
        this.blockZombieDoorDestruction = getBoolean("mobs.block-zombie-door-destruction", false);
        this.blockEntityVehicleEntry = getBoolean("mobs.block-vehicle-entry", false);

        this.disableFallDamage = getBoolean("player-damage.disable-fall-damage", false);
        this.disableLavaDamage = getBoolean("player-damage.disable-lava-damage", false);
        this.disableFireDamage = getBoolean("player-damage.disable-fire-damage", false);
        this.disableLightningDamage = getBoolean("player-damage.disable-lightning-damage", false);
        this.disableDrowningDamage = getBoolean("player-damage.disable-drowning-damage", false);
        this.disableSuffocationDamage = getBoolean("player-damage.disable-suffocation-damage", false);
        this.disableContactDamage = getBoolean("player-damage.disable-contact-damage", false);
        this.teleportOnSuffocation = getBoolean("player-damage.teleport-on-suffocation", false);
        this.disableVoidDamage = getBoolean("player-damage.disable-void-damage", false);
        this.teleportOnVoid = getBoolean("player-damage.teleport-on-void-falling", false);
        this.safeFallOnVoid = getBoolean("player-damage.reset-fall-on-void-teleport", false);
        this.disableExplosionDamage = getBoolean("player-damage.disable-explosion-damage", false);
        this.disableMobDamage = getBoolean("player-damage.disable-mob-damage", false);
        this.disableDeathMessages = getBoolean("player-damage.disable-death-messages", false);

        this.signChestProtection = getBoolean("chest-protection.enable", false);
        this.disableSignChestProtectionCheck = getBoolean("chest-protection.disable-off-check", true);
        if (!this.signChestProtection) {
            needParentSave |= removeProperty("chest-protection");
        }

        this.disableCreatureCropTrampling = getBoolean("crops.disable-creature-trampling", false);
        this.disablePlayerCropTrampling = getBoolean("crops.disable-player-trampling", false);
        this.disableCreatureTurtleEggTrampling = getBoolean("turtle-egg.disable-creature-trampling", false);
        this.disablePlayerTurtleEggTrampling = getBoolean("turtle-egg.disable-player-trampling", false);
        this.disableCreatureSnifferEggTrampling = getBoolean("sniffer-egg.disable-creature-trampling", false);
        this.disablePlayerSnifferEggTrampling = getBoolean("sniffer-egg.disable-player-trampling", false);

        this.disallowedLightningBlocks = new HashSet<>(convertLegacyBlocks(getStringList("weather.prevent-lightning-strike-blocks", null)));
        this.preventLightningFire = getBoolean("weather.disable-lightning-strike-fire", false);
        this.disableThunder = getBoolean("weather.disable-thunderstorm", false);
        this.disableWeather = getBoolean("weather.disable-weather", false);
        this.disablePigZap = getBoolean("weather.disable-pig-zombification", false);
        this.disableVillagerZap = getBoolean("weather.disable-villager-witchification", false);
        this.disableCreeperPower = getBoolean("weather.disable-powered-creepers", false);
        this.alwaysRaining = getBoolean("weather.always-raining", false);
        this.alwaysThundering = getBoolean("weather.always-thundering", false);

        this.disableMushroomSpread = getBoolean("dynamics.disable-mushroom-spread", false);
        this.disableIceMelting = getBoolean("dynamics.disable-ice-melting", false);
        this.disableSnowMelting = getBoolean("dynamics.disable-snow-melting", false);
        this.disableSnowFormation = getBoolean("dynamics.disable-snow-formation", false);
        this.disableIceFormation = getBoolean("dynamics.disable-ice-formation", false);
        this.disableLeafDecay = getBoolean("dynamics.disable-leaf-decay", false);
        this.disableGrassGrowth = getBoolean("dynamics.disable-grass-growth", false);
        this.disableMyceliumSpread = getBoolean("dynamics.disable-mycelium-spread", false);
        this.disableVineGrowth = getBoolean("dynamics.disable-vine-growth", false);
        this.disableRockGrowth = getBoolean("dynamics.disable-rock-growth", false);
        this.disableSculkGrowth = getBoolean("dynamics.disable-sculk-growth", false);
        this.disableCropGrowth = getBoolean("dynamics.disable-crop-growth", false);
        this.disableSoilDehydration = getBoolean("dynamics.disable-soil-dehydration", false);
        this.disableSoilMoistureChange = getBoolean("dynamics.disable-soil-moisture-change", false);
        this.disableCoralBlockFade = getBoolean("dynamics.disable-coral-block-fade", false);
        this.disableCopperBlockFade = getBoolean("dynamics.disable-copper-block-fade", false);
        this.allowedSnowFallOver = new HashSet<>(convertLegacyBlocks(getStringList("dynamics.snow-fall-blocks", null)));

        this.useRegions = getBoolean("regions.enable", true);
        this.regionInvinciblityRemovesMobs = getBoolean("regions.invincibility-removes-mobs", false);
        this.regionCancelEmptyChatEvents = getBoolean("regions.cancel-chat-without-recipients", true);
        this.regionNetherPortalProtection = getBoolean("regions.nether-portal-protection", true);
        this.forceDefaultTitleTimes = this.config.getBoolean("regions.titles-always-use-default-times", true);
        this.fakePlayerBuildOverride = getBoolean("regions.fake-player-build-override", true);
        this.explosionFlagCancellation = getBoolean("regions.explosion-flags-block-entity-damage", true);
        this.highFreqFlags = getBoolean("regions.high-frequency-flags", false);
        this.checkLiquidFlow = getBoolean("regions.protect-against-liquid-flow", false);
        this.regionWand = convertLegacyItem(getString("regions.wand", ItemTypes.LEATHER.getId()));
        this.maxClaimVolume = getInt("regions.max-claim-volume", 30000);
        this.claimOnlyInsideExistingRegions = getBoolean("regions.claim-only-inside-existing-regions", false);
        this.setParentOnClaim = getString("regions.set-parent-on-claim", "");
        this.boundedLocationFlags = getBoolean("regions.location-flags-only-inside-regions", false);
        this.maxRegionCountPerPlayer = getInt("regions.max-region-count-per-player.default", 7);
        this.maxRegionCounts = new HashMap<>();
        this.maxRegionCounts.put(null, this.maxRegionCountPerPlayer);
        for (String key : getKeys("regions.max-region-count-per-player")) {
            if (key.equalsIgnoreCase("default")) {
                continue;
            }
            Object val = getProperty("regions.max-region-count-per-player." + key);
            if (val instanceof Number) {
                this.maxRegionCounts.put(key, ((Number) val).intValue());
            }
        }

        this.blockCreatureSpawn = new HashSet<>();
        for (String creatureName : getStringList("mobs.block-creature-spawn", null)) {
            EntityType creature = EntityTypes.get(creatureName.toLowerCase());
            if (creature == null) {
                log.warning("Unknown entity type '" + creatureName + "'");
                continue;
            }
            this.blockCreatureSpawn.add(creature);
        }

        // Blacklist de materiales (sin los manejadores de logging por archivo/BD/consola del
        // plugin original, que dependían de su propio logger de Bukkit; el chequeo en sí ya
        // queda disponible vía getBlacklist() para cuando se implemente el enganche).
        boolean useBlacklistAsWhitelist = getBoolean("blacklist.use-as-whitelist", false);
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
                    log.log(Level.INFO, "(" + worldName + ") Blacklist loaded with " + blist.getItemCount() + " entries.");
                }
            }
        } catch (FileNotFoundException e) {
            log.log(Level.WARNING, "WorldGuard blacklist does not exist.");
        } catch (IOException e) {
            log.log(Level.WARNING, "Could not load WorldGuard blacklist: " + e.getMessage());
        }

        this.config.setHeader(
            "#\n# WorldGuard's world configuration file\n#\n# This is a world configuration file. Anything placed into here will only\n"
            + "# affect this world. If you don't put anything in this file, then the\n# settings will be inherited from the main configuration file.\n#\n");
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
}
