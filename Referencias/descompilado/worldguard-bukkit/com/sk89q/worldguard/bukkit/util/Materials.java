/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.HashBiMap
 *  javax.annotation.Nullable
 *  org.bukkit.Keyed
 *  org.bukkit.Material
 *  org.bukkit.Tag
 *  org.bukkit.entity.EntityType
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 */
package com.sk89q.worldguard.bukkit.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Materials {
    private static final Logger logger = Logger.getLogger(Materials.class.getSimpleName());
    private static final int MODIFIED_ON_RIGHT = 1;
    private static final int MODIFIED_ON_LEFT = 2;
    private static final int MODIFIES_BLOCKS = 4;
    private static final BiMap<EntityType, Material> ENTITY_ITEMS = HashBiMap.create();
    private static final Map<Material, Integer> MATERIAL_FLAGS = new EnumMap<Material, Integer>(Material.class);
    private static final Set<PotionEffectType> DAMAGE_EFFECTS = new HashSet<PotionEffectType>();

    private static void putMaterialTag(Tag<Material> tag, Integer value) {
        if (tag == null) {
            return;
        }
        tag.getValues().forEach(mat -> MATERIAL_FLAGS.put((Material)mat, value));
    }

    private Materials() {
    }

    @Nullable
    public static Material getRelatedMaterial(EntityType type) {
        return (Material)ENTITY_ITEMS.get((Object)type);
    }

    @Nullable
    public static EntityType getRelatedEntity(Material material) {
        return (EntityType)ENTITY_ITEMS.inverse().get((Object)material);
    }

    public static Material getBucketBlockMaterial(Material type) {
        return switch (type) {
            case Material.LAVA_BUCKET -> Material.LAVA;
            case Material.WATER_BUCKET -> Material.WATER;
            default -> Material.WATER;
        };
    }

    public static boolean isMushroom(Material material) {
        return material == Material.RED_MUSHROOM || material == Material.BROWN_MUSHROOM;
    }

    public static boolean isLeaf(Material material) {
        return Tag.LEAVES.isTagged((Keyed)material);
    }

    public static boolean isLiquid(Material material) {
        return Materials.isWater(material) || Materials.isLava(material);
    }

    public static boolean isWater(Material material) {
        return material == Material.WATER || material == Material.BUBBLE_COLUMN || material == Material.KELP_PLANT || material == Material.SEAGRASS || material == Material.TALL_SEAGRASS;
    }

    public static boolean isLava(Material material) {
        return material == Material.LAVA;
    }

    public static boolean isPortal(Material material) {
        return material == Material.NETHER_PORTAL || material == Material.END_PORTAL;
    }

    public static boolean isRailBlock(Material material) {
        return Tag.RAILS.isTagged((Keyed)material);
    }

    public static boolean isPistonBlock(Material material) {
        return material == Material.PISTON || material == Material.STICKY_PISTON || material == Material.MOVING_PISTON;
    }

    public static boolean isMinecart(Material material) {
        return material == Material.MINECART || material == Material.COMMAND_BLOCK_MINECART || material == Material.TNT_MINECART || material == Material.HOPPER_MINECART || material == Material.FURNACE_MINECART || material == Material.CHEST_MINECART;
    }

    public static boolean isBoat(Material material) {
        return Tag.ITEMS_BOATS.isTagged((Keyed)material);
    }

    public static boolean isShulkerBox(Material material) {
        return Tag.SHULKER_BOXES.isTagged((Keyed)material);
    }

    public static boolean isInventoryBlock(Material material) {
        return material == Material.CHEST || material == Material.JUKEBOX || material == Material.DISPENSER || material == Material.FURNACE || material == Material.BREWING_STAND || material == Material.TRAPPED_CHEST || material == Material.HOPPER || material == Material.DROPPER || material == Material.BARREL || material == Material.BLAST_FURNACE || material == Material.SMOKER || material == Material.CHISELED_BOOKSHELF || material == Material.CRAFTER || Tag.ITEMS_CHEST_BOATS.isTagged((Keyed)material) || Tag.SHULKER_BOXES.isTagged((Keyed)material);
    }

    public static boolean isSpawnEgg(Material material) {
        return Materials.getEntitySpawnEgg(material) != null;
    }

    public static EntityType getEntitySpawnEgg(Material material) {
        return switch (material) {
            case Material.ALLAY_SPAWN_EGG -> EntityType.ALLAY;
            case Material.ARMADILLO_SPAWN_EGG -> EntityType.ARMADILLO;
            case Material.AXOLOTL_SPAWN_EGG -> EntityType.AXOLOTL;
            case Material.BAT_SPAWN_EGG -> EntityType.BAT;
            case Material.BEE_SPAWN_EGG -> EntityType.BEE;
            case Material.BLAZE_SPAWN_EGG -> EntityType.BLAZE;
            case Material.BOGGED_SPAWN_EGG -> EntityType.BOGGED;
            case Material.BREEZE_SPAWN_EGG -> EntityType.BREEZE;
            case Material.CAT_SPAWN_EGG -> EntityType.CAT;
            case Material.CAMEL_SPAWN_EGG -> EntityType.CAMEL;
            case Material.CAVE_SPIDER_SPAWN_EGG -> EntityType.CAVE_SPIDER;
            case Material.CHICKEN_SPAWN_EGG -> EntityType.CHICKEN;
            case Material.COD_SPAWN_EGG -> EntityType.COD;
            case Material.COW_SPAWN_EGG -> EntityType.COW;
            case Material.CREEPER_SPAWN_EGG -> EntityType.CREEPER;
            case Material.DOLPHIN_SPAWN_EGG -> EntityType.DOLPHIN;
            case Material.DONKEY_SPAWN_EGG -> EntityType.DONKEY;
            case Material.DROWNED_SPAWN_EGG -> EntityType.DROWNED;
            case Material.ELDER_GUARDIAN_SPAWN_EGG -> EntityType.ELDER_GUARDIAN;
            case Material.ENDER_DRAGON_SPAWN_EGG -> EntityType.ENDER_DRAGON;
            case Material.ENDERMAN_SPAWN_EGG -> EntityType.ENDERMAN;
            case Material.ENDERMITE_SPAWN_EGG -> EntityType.ENDERMITE;
            case Material.EVOKER_SPAWN_EGG -> EntityType.EVOKER;
            case Material.FOX_SPAWN_EGG -> EntityType.FOX;
            case Material.FROG_SPAWN_EGG -> EntityType.FROG;
            case Material.GHAST_SPAWN_EGG -> EntityType.GHAST;
            case Material.GLOW_SQUID_SPAWN_EGG -> EntityType.GLOW_SQUID;
            case Material.GOAT_SPAWN_EGG -> EntityType.GOAT;
            case Material.GUARDIAN_SPAWN_EGG -> EntityType.GUARDIAN;
            case Material.HOGLIN_SPAWN_EGG -> EntityType.HOGLIN;
            case Material.HORSE_SPAWN_EGG -> EntityType.HORSE;
            case Material.HUSK_SPAWN_EGG -> EntityType.HUSK;
            case Material.IRON_GOLEM_SPAWN_EGG -> EntityType.IRON_GOLEM;
            case Material.LLAMA_SPAWN_EGG -> EntityType.LLAMA;
            case Material.MAGMA_CUBE_SPAWN_EGG -> EntityType.MAGMA_CUBE;
            case Material.MOOSHROOM_SPAWN_EGG -> EntityType.MOOSHROOM;
            case Material.MULE_SPAWN_EGG -> EntityType.MULE;
            case Material.OCELOT_SPAWN_EGG -> EntityType.OCELOT;
            case Material.PANDA_SPAWN_EGG -> EntityType.PANDA;
            case Material.PARROT_SPAWN_EGG -> EntityType.PARROT;
            case Material.PHANTOM_SPAWN_EGG -> EntityType.PHANTOM;
            case Material.PIGLIN_BRUTE_SPAWN_EGG -> EntityType.PIGLIN_BRUTE;
            case Material.PIGLIN_SPAWN_EGG -> EntityType.PIGLIN;
            case Material.PILLAGER_SPAWN_EGG -> EntityType.PILLAGER;
            case Material.POLAR_BEAR_SPAWN_EGG -> EntityType.POLAR_BEAR;
            case Material.PUFFERFISH_SPAWN_EGG -> EntityType.PUFFERFISH;
            case Material.RABBIT_SPAWN_EGG -> EntityType.RABBIT;
            case Material.RAVAGER_SPAWN_EGG -> EntityType.RAVAGER;
            case Material.SALMON_SPAWN_EGG -> EntityType.SALMON;
            case Material.SHEEP_SPAWN_EGG -> EntityType.SHEEP;
            case Material.SHULKER_SPAWN_EGG -> EntityType.SHULKER;
            case Material.SILVERFISH_SPAWN_EGG -> EntityType.SILVERFISH;
            case Material.SKELETON_HORSE_SPAWN_EGG -> EntityType.SKELETON_HORSE;
            case Material.SKELETON_SPAWN_EGG -> EntityType.SKELETON;
            case Material.SLIME_SPAWN_EGG -> EntityType.SLIME;
            case Material.SNIFFER_SPAWN_EGG -> EntityType.SNIFFER;
            case Material.SNOW_GOLEM_SPAWN_EGG -> EntityType.SNOW_GOLEM;
            case Material.SPIDER_SPAWN_EGG -> EntityType.SPIDER;
            case Material.SQUID_SPAWN_EGG -> EntityType.SQUID;
            case Material.STRAY_SPAWN_EGG -> EntityType.STRAY;
            case Material.STRIDER_SPAWN_EGG -> EntityType.STRIDER;
            case Material.TADPOLE_SPAWN_EGG -> EntityType.TADPOLE;
            case Material.TRADER_LLAMA_SPAWN_EGG -> EntityType.TRADER_LLAMA;
            case Material.TROPICAL_FISH_SPAWN_EGG -> EntityType.TROPICAL_FISH;
            case Material.TURTLE_SPAWN_EGG -> EntityType.TURTLE;
            case Material.VEX_SPAWN_EGG -> EntityType.VEX;
            case Material.VILLAGER_SPAWN_EGG -> EntityType.VILLAGER;
            case Material.VINDICATOR_SPAWN_EGG -> EntityType.VINDICATOR;
            case Material.WANDERING_TRADER_SPAWN_EGG -> EntityType.WANDERING_TRADER;
            case Material.WARDEN_SPAWN_EGG -> EntityType.WARDEN;
            case Material.WITCH_SPAWN_EGG -> EntityType.WITCH;
            case Material.WITHER_SPAWN_EGG -> EntityType.WITHER;
            case Material.WITHER_SKELETON_SPAWN_EGG -> EntityType.WITHER_SKELETON;
            case Material.WOLF_SPAWN_EGG -> EntityType.WOLF;
            case Material.ZOGLIN_SPAWN_EGG -> EntityType.ZOGLIN;
            case Material.ZOMBIE_HORSE_SPAWN_EGG -> EntityType.ZOMBIE_HORSE;
            case Material.ZOMBIFIED_PIGLIN_SPAWN_EGG -> EntityType.ZOMBIFIED_PIGLIN;
            case Material.ZOMBIE_SPAWN_EGG -> EntityType.ZOMBIE;
            case Material.ZOMBIE_VILLAGER_SPAWN_EGG -> EntityType.ZOMBIE_VILLAGER;
            case Material.PIG_SPAWN_EGG -> EntityType.PIG;
            default -> null;
        };
    }

    public static boolean isBed(Material material) {
        return Tag.BEDS.isTagged((Keyed)material);
    }

    public static boolean isAnvil(Material material) {
        return Tag.ANVIL.isTagged((Keyed)material);
    }

    public static boolean isCoral(Material material) {
        return Tag.CORAL_BLOCKS.isTagged((Keyed)material) || Tag.CORAL_PLANTS.isTagged((Keyed)material) || Tag.CORALS.isTagged((Keyed)material) || Tag.WALL_CORALS.isTagged((Keyed)material);
    }

    public static boolean isCrop(Material type) {
        if (Tag.CROPS.isTagged((Keyed)type)) {
            return true;
        }
        return switch (type) {
            case Material.PUMPKIN, Material.MELON, Material.CACTUS, Material.SUGAR_CANE, Material.BAMBOO, Material.BAMBOO_SAPLING, Material.SWEET_BERRY_BUSH, Material.NETHER_WART, Material.CAVE_VINES, Material.CAVE_VINES_PLANT -> true;
            default -> false;
        };
    }

    public static boolean isVine(Material newType) {
        return newType == Material.VINE || newType == Material.KELP || newType == Material.TWISTING_VINES || newType == Material.WEEPING_VINES || Tag.CAVE_VINES.isTagged((Keyed)newType);
    }

    public static boolean isUseFlagApplicable(Material material) {
        if (Tag.BUTTONS.isTagged((Keyed)material) || Tag.DOORS.isTagged((Keyed)material) || Tag.TRAPDOORS.isTagged((Keyed)material) || Tag.FENCE_GATES.isTagged((Keyed)material) || Tag.PRESSURE_PLATES.isTagged((Keyed)material)) {
            return true;
        }
        return switch (material) {
            case Material.LEVER, Material.LECTERN, Material.ENCHANTING_TABLE, Material.BELL, Material.LOOM, Material.CARTOGRAPHY_TABLE, Material.STONECUTTER, Material.GRINDSTONE, Material.VAULT -> true;
            default -> false;
        };
    }

    public static boolean isBlockModifiedOnClick(Material material, boolean rightClick) {
        Integer flags = MATERIAL_FLAGS.get(material);
        return flags == null || rightClick && (flags & 1) == 1 || !rightClick && (flags & 2) == 2;
    }

    public static boolean isItemAppliedToBlock(Material item, Material block) {
        Integer flags = MATERIAL_FLAGS.get(item);
        return flags == null || (flags & 4) == 4 || Materials.isToolApplicable(item, block);
    }

    public static boolean isConsideredBuildingIfUsed(Material type) {
        return type == Material.REPEATER || type == Material.COMPARATOR || type == Material.CAKE || type == Material.DRAGON_EGG || Tag.FLOWER_POTS.isTagged((Keyed)type) || Tag.CANDLES.isTagged((Keyed)type) || Tag.CANDLE_CAKES.isTagged((Keyed)type) || Tag.ALL_SIGNS.isTagged((Keyed)type);
    }

    public static boolean hasDamageEffect(Collection<PotionEffect> effects) {
        for (PotionEffect effect : effects) {
            if (!DAMAGE_EFFECTS.contains(effect.getType())) continue;
            return true;
        }
        return false;
    }

    public static boolean isArmor(Material type) {
        if (Tag.ITEMS_HEAD_ARMOR.isTagged((Keyed)type) || Tag.ITEMS_CHEST_ARMOR.isTagged((Keyed)type) || Tag.ITEMS_LEG_ARMOR.isTagged((Keyed)type) || Tag.ITEMS_FOOT_ARMOR.isTagged((Keyed)type) || Tag.ITEMS_SKULLS.isTagged((Keyed)type)) {
            return true;
        }
        return switch (type) {
            case Material.CARVED_PUMPKIN, Material.ELYTRA -> true;
            default -> false;
        };
    }

    public static boolean isToolApplicable(Material toolMaterial, Material targetMaterial) {
        switch (toolMaterial) {
            case WOODEN_HOE: 
            case STONE_HOE: 
            case IRON_HOE: 
            case GOLDEN_HOE: 
            case DIAMOND_HOE: 
            case NETHERITE_HOE: {
                return switch (targetMaterial) {
                    case Material.GRASS_BLOCK, Material.DIRT, Material.DIRT_PATH, Material.ROOTED_DIRT -> true;
                    default -> false;
                };
            }
            case WOODEN_AXE: 
            case STONE_AXE: 
            case IRON_AXE: 
            case GOLDEN_AXE: 
            case DIAMOND_AXE: 
            case NETHERITE_AXE: {
                if (Materials.isWaxedCopper(targetMaterial)) {
                    return true;
                }
                if (Tag.LOGS.isTagged((Keyed)targetMaterial)) {
                    return true;
                }
                return switch (targetMaterial) {
                    case Material.PUMPKIN, Material.OAK_WOOD, Material.DARK_OAK_WOOD, Material.ACACIA_WOOD, Material.BIRCH_WOOD, Material.SPRUCE_WOOD, Material.BAMBOO_BLOCK, Material.JUNGLE_WOOD, Material.CRIMSON_STEM, Material.WARPED_STEM, Material.CRIMSON_HYPHAE, Material.WARPED_HYPHAE -> true;
                    default -> false;
                };
            }
            case WOODEN_SHOVEL: 
            case STONE_SHOVEL: 
            case IRON_SHOVEL: 
            case GOLDEN_SHOVEL: 
            case DIAMOND_SHOVEL: 
            case NETHERITE_SHOVEL: {
                return switch (targetMaterial) {
                    case Material.GRASS_BLOCK, Material.CAMPFIRE, Material.SOUL_CAMPFIRE -> true;
                    default -> false;
                };
            }
            case SHEARS: {
                return switch (targetMaterial) {
                    case Material.PUMPKIN, Material.BEE_NEST, Material.BEEHIVE -> true;
                    default -> false;
                };
            }
            case BLACK_DYE: 
            case BLUE_DYE: 
            case BROWN_DYE: 
            case CYAN_DYE: 
            case GRAY_DYE: 
            case GREEN_DYE: 
            case LIGHT_BLUE_DYE: 
            case LIGHT_GRAY_DYE: 
            case LIME_DYE: 
            case MAGENTA_DYE: 
            case ORANGE_DYE: 
            case PINK_DYE: 
            case PURPLE_DYE: 
            case RED_DYE: 
            case WHITE_DYE: 
            case YELLOW_DYE: 
            case GLOW_INK_SAC: 
            case INK_SAC: {
                return Tag.ALL_SIGNS.isTagged((Keyed)targetMaterial);
            }
            case HONEYCOMB: {
                return Materials.isUnwaxedCopper(targetMaterial) || Tag.ALL_SIGNS.isTagged((Keyed)targetMaterial);
            }
            case BRUSH: {
                return switch (targetMaterial) {
                    case Material.SUSPICIOUS_GRAVEL, Material.SUSPICIOUS_SAND -> true;
                    default -> false;
                };
            }
            case WRITTEN_BOOK: 
            case WRITABLE_BOOK: {
                return targetMaterial == Material.LECTERN;
            }
        }
        return false;
    }

    public static boolean isFire(Material type) {
        return type == Material.FIRE || type == Material.SOUL_FIRE;
    }

    public static boolean isWaxedCopper(Material type) {
        return type.name().startsWith("WAXED_") && type.name().contains("COPPER");
    }

    public static boolean isUnwaxedCopper(Material type) {
        return switch (type) {
            case Material.COPPER_BLOCK, Material.CHISELED_COPPER, Material.COPPER_DOOR, Material.COPPER_TRAPDOOR, Material.COPPER_GRATE, Material.COPPER_BULB -> true;
            default -> type.name().startsWith("EXPOSED_") || type.name().startsWith("WEATHERED_") || type.name().startsWith("OXIDIZED_") || type.name().startsWith("CUT_COPPER");
        };
    }

    public static boolean isAmethystGrowth(Material mat) {
        return mat == Material.BUDDING_AMETHYST || mat == Material.AMETHYST_CLUSTER || mat == Material.LARGE_AMETHYST_BUD || mat == Material.MEDIUM_AMETHYST_BUD || mat == Material.SMALL_AMETHYST_BUD;
    }

    public static boolean isSculkGrowth(Material mat) {
        return mat == Material.SCULK || mat == Material.SCULK_VEIN;
    }

    static {
        ENTITY_ITEMS.put((Object)EntityType.PAINTING, (Object)Material.PAINTING);
        ENTITY_ITEMS.put((Object)EntityType.ARROW, (Object)Material.ARROW);
        ENTITY_ITEMS.put((Object)EntityType.SNOWBALL, (Object)Material.SNOWBALL);
        ENTITY_ITEMS.put((Object)EntityType.FIREBALL, (Object)Material.FIRE_CHARGE);
        ENTITY_ITEMS.put((Object)EntityType.ENDER_PEARL, (Object)Material.ENDER_PEARL);
        ENTITY_ITEMS.put((Object)EntityType.EXPERIENCE_BOTTLE, (Object)Material.EXPERIENCE_BOTTLE);
        ENTITY_ITEMS.put((Object)EntityType.ITEM_FRAME, (Object)Material.ITEM_FRAME);
        ENTITY_ITEMS.put((Object)EntityType.GLOW_ITEM_FRAME, (Object)Material.GLOW_ITEM_FRAME);
        ENTITY_ITEMS.put((Object)EntityType.TNT, (Object)Material.TNT);
        ENTITY_ITEMS.put((Object)EntityType.FIREWORK_ROCKET, (Object)Material.FIREWORK_ROCKET);
        ENTITY_ITEMS.put((Object)EntityType.COMMAND_BLOCK_MINECART, (Object)Material.COMMAND_BLOCK_MINECART);
        ENTITY_ITEMS.put((Object)EntityType.BOAT, (Object)Material.OAK_BOAT);
        ENTITY_ITEMS.put((Object)EntityType.MINECART, (Object)Material.MINECART);
        ENTITY_ITEMS.put((Object)EntityType.CHEST_MINECART, (Object)Material.CHEST_MINECART);
        ENTITY_ITEMS.put((Object)EntityType.FURNACE_MINECART, (Object)Material.FURNACE_MINECART);
        ENTITY_ITEMS.put((Object)EntityType.TNT_MINECART, (Object)Material.TNT_MINECART);
        ENTITY_ITEMS.put((Object)EntityType.HOPPER_MINECART, (Object)Material.HOPPER_MINECART);
        ENTITY_ITEMS.put((Object)EntityType.POTION, (Object)Material.POTION);
        ENTITY_ITEMS.put((Object)EntityType.EGG, (Object)Material.EGG);
        ENTITY_ITEMS.put((Object)EntityType.ARMOR_STAND, (Object)Material.ARMOR_STAND);
        ENTITY_ITEMS.put((Object)EntityType.END_CRYSTAL, (Object)Material.END_CRYSTAL);
        Materials.putMaterialTag((Tag<Material>)Tag.DOORS, 1);
        Materials.putMaterialTag((Tag<Material>)Tag.TRAPDOORS, 1);
        MATERIAL_FLAGS.put(Material.AIR, 0);
        MATERIAL_FLAGS.put(Material.STONE, 0);
        MATERIAL_FLAGS.put(Material.GRASS_BLOCK, 0);
        MATERIAL_FLAGS.put(Material.DIRT, 0);
        MATERIAL_FLAGS.put(Material.COBBLESTONE, 0);
        MATERIAL_FLAGS.put(Material.BEDROCK, 0);
        MATERIAL_FLAGS.put(Material.WATER, 0);
        MATERIAL_FLAGS.put(Material.LAVA, 0);
        MATERIAL_FLAGS.put(Material.SAND, 0);
        MATERIAL_FLAGS.put(Material.GRAVEL, 0);
        MATERIAL_FLAGS.put(Material.SPONGE, 0);
        MATERIAL_FLAGS.put(Material.GLASS, 0);
        MATERIAL_FLAGS.put(Material.LAPIS_BLOCK, 0);
        MATERIAL_FLAGS.put(Material.DISPENSER, 1);
        MATERIAL_FLAGS.put(Material.SANDSTONE, 0);
        MATERIAL_FLAGS.put(Material.NOTE_BLOCK, 1);
        MATERIAL_FLAGS.put(Material.POWERED_RAIL, 0);
        MATERIAL_FLAGS.put(Material.DETECTOR_RAIL, 0);
        MATERIAL_FLAGS.put(Material.STICKY_PISTON, 0);
        MATERIAL_FLAGS.put(Material.COBWEB, 0);
        MATERIAL_FLAGS.put(Material.SHORT_GRASS, 0);
        MATERIAL_FLAGS.put(Material.DEAD_BUSH, 0);
        MATERIAL_FLAGS.put(Material.PISTON, 0);
        MATERIAL_FLAGS.put(Material.PISTON_HEAD, 0);
        MATERIAL_FLAGS.put(Material.MOVING_PISTON, 0);
        MATERIAL_FLAGS.put(Material.SUNFLOWER, 0);
        MATERIAL_FLAGS.put(Material.LILAC, 0);
        MATERIAL_FLAGS.put(Material.PEONY, 0);
        MATERIAL_FLAGS.put(Material.ROSE_BUSH, 0);
        MATERIAL_FLAGS.put(Material.BROWN_MUSHROOM, 0);
        MATERIAL_FLAGS.put(Material.RED_MUSHROOM, 0);
        MATERIAL_FLAGS.put(Material.GOLD_BLOCK, 0);
        MATERIAL_FLAGS.put(Material.IRON_BLOCK, 0);
        MATERIAL_FLAGS.put(Material.BRICK, 0);
        MATERIAL_FLAGS.put(Material.TNT, 1);
        MATERIAL_FLAGS.put(Material.BOOKSHELF, 1);
        MATERIAL_FLAGS.put(Material.MOSSY_COBBLESTONE, 0);
        MATERIAL_FLAGS.put(Material.OBSIDIAN, 0);
        MATERIAL_FLAGS.put(Material.TORCH, 0);
        MATERIAL_FLAGS.put(Material.FIRE, 0);
        MATERIAL_FLAGS.put(Material.SPAWNER, 1);
        MATERIAL_FLAGS.put(Material.CHEST, 1);
        MATERIAL_FLAGS.put(Material.REDSTONE_WIRE, 1);
        MATERIAL_FLAGS.put(Material.DIAMOND_BLOCK, 0);
        MATERIAL_FLAGS.put(Material.CRAFTING_TABLE, 0);
        MATERIAL_FLAGS.put(Material.WHEAT, 0);
        MATERIAL_FLAGS.put(Material.FARMLAND, 0);
        MATERIAL_FLAGS.put(Material.FURNACE, 1);
        MATERIAL_FLAGS.put(Material.LADDER, 0);
        MATERIAL_FLAGS.put(Material.RAIL, 0);
        MATERIAL_FLAGS.put(Material.COBBLESTONE_STAIRS, 0);
        MATERIAL_FLAGS.put(Material.LEVER, 1);
        MATERIAL_FLAGS.put(Material.STONE_PRESSURE_PLATE, 0);
        MATERIAL_FLAGS.put(Material.REDSTONE_WALL_TORCH, 0);
        MATERIAL_FLAGS.put(Material.REDSTONE_TORCH, 0);
        MATERIAL_FLAGS.put(Material.SNOW, 0);
        MATERIAL_FLAGS.put(Material.ICE, 0);
        MATERIAL_FLAGS.put(Material.SNOW_BLOCK, 0);
        MATERIAL_FLAGS.put(Material.CACTUS, 0);
        MATERIAL_FLAGS.put(Material.CLAY, 0);
        MATERIAL_FLAGS.put(Material.JUKEBOX, 1);
        MATERIAL_FLAGS.put(Material.PUMPKIN, 0);
        MATERIAL_FLAGS.put(Material.NETHERRACK, 0);
        MATERIAL_FLAGS.put(Material.SOUL_SAND, 0);
        MATERIAL_FLAGS.put(Material.GLOWSTONE, 0);
        MATERIAL_FLAGS.put(Material.NETHER_PORTAL, 0);
        MATERIAL_FLAGS.put(Material.JACK_O_LANTERN, 0);
        MATERIAL_FLAGS.put(Material.CAKE, 1);
        MATERIAL_FLAGS.put(Material.REPEATER, 1);
        MATERIAL_FLAGS.put(Material.INFESTED_STONE, 0);
        MATERIAL_FLAGS.put(Material.INFESTED_STONE_BRICKS, 0);
        MATERIAL_FLAGS.put(Material.INFESTED_MOSSY_STONE_BRICKS, 0);
        MATERIAL_FLAGS.put(Material.INFESTED_CRACKED_STONE_BRICKS, 0);
        MATERIAL_FLAGS.put(Material.INFESTED_CHISELED_STONE_BRICKS, 0);
        MATERIAL_FLAGS.put(Material.INFESTED_COBBLESTONE, 0);
        MATERIAL_FLAGS.put(Material.STONE_BRICKS, 0);
        MATERIAL_FLAGS.put(Material.MOSSY_STONE_BRICKS, 0);
        MATERIAL_FLAGS.put(Material.CRACKED_STONE_BRICKS, 0);
        MATERIAL_FLAGS.put(Material.CHISELED_STONE_BRICKS, 0);
        MATERIAL_FLAGS.put(Material.BROWN_MUSHROOM_BLOCK, 0);
        MATERIAL_FLAGS.put(Material.RED_MUSHROOM_BLOCK, 0);
        MATERIAL_FLAGS.put(Material.IRON_BARS, 0);
        MATERIAL_FLAGS.put(Material.GLASS_PANE, 0);
        MATERIAL_FLAGS.put(Material.MELON, 0);
        MATERIAL_FLAGS.put(Material.PUMPKIN_STEM, 0);
        MATERIAL_FLAGS.put(Material.MELON_STEM, 0);
        MATERIAL_FLAGS.put(Material.VINE, 0);
        MATERIAL_FLAGS.put(Material.BRICK_STAIRS, 0);
        MATERIAL_FLAGS.put(Material.MYCELIUM, 0);
        MATERIAL_FLAGS.put(Material.LILY_PAD, 0);
        MATERIAL_FLAGS.put(Material.NETHER_BRICK, 0);
        MATERIAL_FLAGS.put(Material.NETHER_BRICK_STAIRS, 0);
        MATERIAL_FLAGS.put(Material.ENCHANTING_TABLE, 1);
        MATERIAL_FLAGS.put(Material.BREWING_STAND, 1);
        MATERIAL_FLAGS.put(Material.END_PORTAL, 0);
        MATERIAL_FLAGS.put(Material.END_PORTAL_FRAME, 0);
        MATERIAL_FLAGS.put(Material.END_STONE, 0);
        MATERIAL_FLAGS.put(Material.DRAGON_EGG, 3);
        MATERIAL_FLAGS.put(Material.REDSTONE_LAMP, 0);
        MATERIAL_FLAGS.put(Material.COCOA, 0);
        MATERIAL_FLAGS.put(Material.SANDSTONE_STAIRS, 0);
        MATERIAL_FLAGS.put(Material.ENDER_CHEST, 0);
        MATERIAL_FLAGS.put(Material.TRIPWIRE_HOOK, 0);
        MATERIAL_FLAGS.put(Material.TRIPWIRE, 0);
        MATERIAL_FLAGS.put(Material.EMERALD_BLOCK, 0);
        MATERIAL_FLAGS.put(Material.COMMAND_BLOCK, 1);
        MATERIAL_FLAGS.put(Material.BEACON, 1);
        MATERIAL_FLAGS.put(Material.ANVIL, 1);
        MATERIAL_FLAGS.put(Material.CHIPPED_ANVIL, 1);
        MATERIAL_FLAGS.put(Material.DAMAGED_ANVIL, 1);
        MATERIAL_FLAGS.put(Material.TRAPPED_CHEST, 1);
        MATERIAL_FLAGS.put(Material.HEAVY_WEIGHTED_PRESSURE_PLATE, 0);
        MATERIAL_FLAGS.put(Material.LIGHT_WEIGHTED_PRESSURE_PLATE, 0);
        MATERIAL_FLAGS.put(Material.COMPARATOR, 1);
        MATERIAL_FLAGS.put(Material.DAYLIGHT_DETECTOR, 1);
        MATERIAL_FLAGS.put(Material.REDSTONE_BLOCK, 0);
        MATERIAL_FLAGS.put(Material.NETHER_QUARTZ_ORE, 0);
        MATERIAL_FLAGS.put(Material.HOPPER, 1);
        MATERIAL_FLAGS.put(Material.QUARTZ_BLOCK, 0);
        MATERIAL_FLAGS.put(Material.QUARTZ_STAIRS, 0);
        MATERIAL_FLAGS.put(Material.ACTIVATOR_RAIL, 0);
        MATERIAL_FLAGS.put(Material.DROPPER, 1);
        MATERIAL_FLAGS.put(Material.ACACIA_STAIRS, 0);
        MATERIAL_FLAGS.put(Material.DARK_OAK_STAIRS, 0);
        MATERIAL_FLAGS.put(Material.HAY_BLOCK, 0);
        MATERIAL_FLAGS.put(Material.COAL_BLOCK, 0);
        MATERIAL_FLAGS.put(Material.PACKED_ICE, 0);
        MATERIAL_FLAGS.put(Material.TALL_GRASS, 0);
        MATERIAL_FLAGS.put(Material.TALL_SEAGRASS, 0);
        MATERIAL_FLAGS.put(Material.LARGE_FERN, 0);
        MATERIAL_FLAGS.put(Material.PRISMARINE, 0);
        MATERIAL_FLAGS.put(Material.SEA_LANTERN, 0);
        MATERIAL_FLAGS.put(Material.SLIME_BLOCK, 0);
        MATERIAL_FLAGS.put(Material.IRON_TRAPDOOR, 0);
        MATERIAL_FLAGS.put(Material.RED_SANDSTONE, 0);
        MATERIAL_FLAGS.put(Material.RED_SANDSTONE_STAIRS, 0);
        MATERIAL_FLAGS.put(Material.DIRT_PATH, 0);
        MATERIAL_FLAGS.put(Material.CHORUS_PLANT, 0);
        MATERIAL_FLAGS.put(Material.CHORUS_FLOWER, 0);
        MATERIAL_FLAGS.put(Material.BEETROOTS, 0);
        MATERIAL_FLAGS.put(Material.END_ROD, 0);
        MATERIAL_FLAGS.put(Material.END_STONE_BRICKS, 0);
        MATERIAL_FLAGS.put(Material.END_GATEWAY, 0);
        MATERIAL_FLAGS.put(Material.FROSTED_ICE, 0);
        MATERIAL_FLAGS.put(Material.PURPUR_BLOCK, 0);
        MATERIAL_FLAGS.put(Material.PURPUR_STAIRS, 0);
        MATERIAL_FLAGS.put(Material.PURPUR_PILLAR, 0);
        MATERIAL_FLAGS.put(Material.PURPUR_SLAB, 0);
        MATERIAL_FLAGS.put(Material.STRUCTURE_BLOCK, 3);
        MATERIAL_FLAGS.put(Material.REPEATING_COMMAND_BLOCK, 1);
        MATERIAL_FLAGS.put(Material.CHAIN_COMMAND_BLOCK, 1);
        MATERIAL_FLAGS.put(Material.MAGMA_BLOCK, 0);
        MATERIAL_FLAGS.put(Material.NETHER_WART_BLOCK, 0);
        MATERIAL_FLAGS.put(Material.RED_NETHER_BRICKS, 0);
        MATERIAL_FLAGS.put(Material.BONE_BLOCK, 0);
        MATERIAL_FLAGS.put(Material.BARRIER, 0);
        MATERIAL_FLAGS.put(Material.STRUCTURE_VOID, 0);
        MATERIAL_FLAGS.put(Material.BLACK_CONCRETE, 0);
        MATERIAL_FLAGS.put(Material.BLUE_CONCRETE, 0);
        MATERIAL_FLAGS.put(Material.BROWN_CONCRETE, 0);
        MATERIAL_FLAGS.put(Material.CYAN_CONCRETE, 0);
        MATERIAL_FLAGS.put(Material.GRAY_CONCRETE, 0);
        MATERIAL_FLAGS.put(Material.GREEN_CONCRETE, 0);
        MATERIAL_FLAGS.put(Material.LIGHT_BLUE_CONCRETE, 0);
        MATERIAL_FLAGS.put(Material.YELLOW_CONCRETE, 0);
        MATERIAL_FLAGS.put(Material.LIGHT_GRAY_CONCRETE, 0);
        MATERIAL_FLAGS.put(Material.LIME_CONCRETE, 0);
        MATERIAL_FLAGS.put(Material.MAGENTA_CONCRETE, 0);
        MATERIAL_FLAGS.put(Material.ORANGE_CONCRETE, 0);
        MATERIAL_FLAGS.put(Material.PINK_CONCRETE, 0);
        MATERIAL_FLAGS.put(Material.PURPLE_CONCRETE, 0);
        MATERIAL_FLAGS.put(Material.RED_CONCRETE, 0);
        MATERIAL_FLAGS.put(Material.WHITE_CONCRETE, 0);
        MATERIAL_FLAGS.put(Material.BLACK_CONCRETE_POWDER, 0);
        MATERIAL_FLAGS.put(Material.BLUE_CONCRETE_POWDER, 0);
        MATERIAL_FLAGS.put(Material.BROWN_CONCRETE_POWDER, 0);
        MATERIAL_FLAGS.put(Material.CYAN_CONCRETE_POWDER, 0);
        MATERIAL_FLAGS.put(Material.GRAY_CONCRETE_POWDER, 0);
        MATERIAL_FLAGS.put(Material.GREEN_CONCRETE_POWDER, 0);
        MATERIAL_FLAGS.put(Material.LIGHT_BLUE_CONCRETE_POWDER, 0);
        MATERIAL_FLAGS.put(Material.YELLOW_CONCRETE_POWDER, 0);
        MATERIAL_FLAGS.put(Material.LIGHT_GRAY_CONCRETE_POWDER, 0);
        MATERIAL_FLAGS.put(Material.LIME_CONCRETE_POWDER, 0);
        MATERIAL_FLAGS.put(Material.MAGENTA_CONCRETE_POWDER, 0);
        MATERIAL_FLAGS.put(Material.ORANGE_CONCRETE_POWDER, 0);
        MATERIAL_FLAGS.put(Material.PINK_CONCRETE_POWDER, 0);
        MATERIAL_FLAGS.put(Material.PURPLE_CONCRETE_POWDER, 0);
        MATERIAL_FLAGS.put(Material.RED_CONCRETE_POWDER, 0);
        MATERIAL_FLAGS.put(Material.WHITE_CONCRETE_POWDER, 0);
        MATERIAL_FLAGS.put(Material.WHITE_GLAZED_TERRACOTTA, 0);
        MATERIAL_FLAGS.put(Material.ORANGE_GLAZED_TERRACOTTA, 0);
        MATERIAL_FLAGS.put(Material.MAGENTA_GLAZED_TERRACOTTA, 0);
        MATERIAL_FLAGS.put(Material.LIGHT_BLUE_GLAZED_TERRACOTTA, 0);
        MATERIAL_FLAGS.put(Material.YELLOW_GLAZED_TERRACOTTA, 0);
        MATERIAL_FLAGS.put(Material.LIME_GLAZED_TERRACOTTA, 0);
        MATERIAL_FLAGS.put(Material.PINK_GLAZED_TERRACOTTA, 0);
        MATERIAL_FLAGS.put(Material.GRAY_GLAZED_TERRACOTTA, 0);
        MATERIAL_FLAGS.put(Material.LIGHT_GRAY_GLAZED_TERRACOTTA, 0);
        MATERIAL_FLAGS.put(Material.CYAN_GLAZED_TERRACOTTA, 0);
        MATERIAL_FLAGS.put(Material.PURPLE_GLAZED_TERRACOTTA, 0);
        MATERIAL_FLAGS.put(Material.BLUE_GLAZED_TERRACOTTA, 0);
        MATERIAL_FLAGS.put(Material.BROWN_GLAZED_TERRACOTTA, 0);
        MATERIAL_FLAGS.put(Material.GREEN_GLAZED_TERRACOTTA, 0);
        MATERIAL_FLAGS.put(Material.RED_GLAZED_TERRACOTTA, 0);
        MATERIAL_FLAGS.put(Material.BLACK_GLAZED_TERRACOTTA, 0);
        MATERIAL_FLAGS.put(Material.ANDESITE, 0);
        MATERIAL_FLAGS.put(Material.ATTACHED_MELON_STEM, 0);
        MATERIAL_FLAGS.put(Material.ATTACHED_PUMPKIN_STEM, 0);
        MATERIAL_FLAGS.put(Material.BLACK_STAINED_GLASS, 0);
        MATERIAL_FLAGS.put(Material.BLACK_STAINED_GLASS_PANE, 0);
        MATERIAL_FLAGS.put(Material.BLACK_TERRACOTTA, 0);
        MATERIAL_FLAGS.put(Material.BLUE_ICE, 0);
        MATERIAL_FLAGS.put(Material.BLUE_STAINED_GLASS, 0);
        MATERIAL_FLAGS.put(Material.BLUE_STAINED_GLASS_PANE, 0);
        MATERIAL_FLAGS.put(Material.BLUE_TERRACOTTA, 0);
        MATERIAL_FLAGS.put(Material.BROWN_STAINED_GLASS, 0);
        MATERIAL_FLAGS.put(Material.BROWN_STAINED_GLASS_PANE, 0);
        MATERIAL_FLAGS.put(Material.BROWN_TERRACOTTA, 0);
        MATERIAL_FLAGS.put(Material.BUBBLE_COLUMN, 0);
        MATERIAL_FLAGS.put(Material.CARROTS, 0);
        MATERIAL_FLAGS.put(Material.CARVED_PUMPKIN, 0);
        MATERIAL_FLAGS.put(Material.CAVE_AIR, 0);
        MATERIAL_FLAGS.put(Material.CHISELED_QUARTZ_BLOCK, 0);
        MATERIAL_FLAGS.put(Material.CHISELED_RED_SANDSTONE, 0);
        MATERIAL_FLAGS.put(Material.CHISELED_SANDSTONE, 0);
        MATERIAL_FLAGS.put(Material.COARSE_DIRT, 0);
        MATERIAL_FLAGS.put(Material.CONDUIT, 0);
        MATERIAL_FLAGS.put(Material.CUT_RED_SANDSTONE, 0);
        MATERIAL_FLAGS.put(Material.CUT_SANDSTONE, 0);
        MATERIAL_FLAGS.put(Material.CYAN_STAINED_GLASS, 0);
        MATERIAL_FLAGS.put(Material.CYAN_STAINED_GLASS_PANE, 0);
        MATERIAL_FLAGS.put(Material.CYAN_TERRACOTTA, 0);
        MATERIAL_FLAGS.put(Material.DARK_PRISMARINE, 0);
        MATERIAL_FLAGS.put(Material.DIORITE, 0);
        MATERIAL_FLAGS.put(Material.DRIED_KELP_BLOCK, 0);
        MATERIAL_FLAGS.put(Material.FERN, 0);
        MATERIAL_FLAGS.put(Material.GRANITE, 0);
        MATERIAL_FLAGS.put(Material.GRAY_STAINED_GLASS, 0);
        MATERIAL_FLAGS.put(Material.GRAY_STAINED_GLASS_PANE, 0);
        MATERIAL_FLAGS.put(Material.GRAY_TERRACOTTA, 0);
        MATERIAL_FLAGS.put(Material.GREEN_STAINED_GLASS, 0);
        MATERIAL_FLAGS.put(Material.GREEN_STAINED_GLASS_PANE, 0);
        MATERIAL_FLAGS.put(Material.GREEN_TERRACOTTA, 0);
        MATERIAL_FLAGS.put(Material.KELP, 0);
        MATERIAL_FLAGS.put(Material.KELP_PLANT, 0);
        MATERIAL_FLAGS.put(Material.LIGHT_BLUE_STAINED_GLASS, 0);
        MATERIAL_FLAGS.put(Material.LIGHT_BLUE_STAINED_GLASS_PANE, 0);
        MATERIAL_FLAGS.put(Material.LIGHT_BLUE_TERRACOTTA, 0);
        MATERIAL_FLAGS.put(Material.LIGHT_GRAY_STAINED_GLASS, 0);
        MATERIAL_FLAGS.put(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 0);
        MATERIAL_FLAGS.put(Material.LIGHT_GRAY_TERRACOTTA, 0);
        MATERIAL_FLAGS.put(Material.LIME_STAINED_GLASS, 0);
        MATERIAL_FLAGS.put(Material.LIME_STAINED_GLASS_PANE, 0);
        MATERIAL_FLAGS.put(Material.LIME_TERRACOTTA, 0);
        MATERIAL_FLAGS.put(Material.MAGENTA_STAINED_GLASS, 0);
        MATERIAL_FLAGS.put(Material.MAGENTA_STAINED_GLASS_PANE, 0);
        MATERIAL_FLAGS.put(Material.MAGENTA_TERRACOTTA, 0);
        MATERIAL_FLAGS.put(Material.MUSHROOM_STEM, 0);
        MATERIAL_FLAGS.put(Material.OBSERVER, 0);
        MATERIAL_FLAGS.put(Material.ORANGE_STAINED_GLASS, 0);
        MATERIAL_FLAGS.put(Material.ORANGE_STAINED_GLASS_PANE, 0);
        MATERIAL_FLAGS.put(Material.ORANGE_TERRACOTTA, 0);
        MATERIAL_FLAGS.put(Material.PINK_STAINED_GLASS, 0);
        MATERIAL_FLAGS.put(Material.PINK_STAINED_GLASS_PANE, 0);
        MATERIAL_FLAGS.put(Material.PINK_TERRACOTTA, 0);
        MATERIAL_FLAGS.put(Material.PODZOL, 0);
        MATERIAL_FLAGS.put(Material.POLISHED_ANDESITE, 0);
        MATERIAL_FLAGS.put(Material.POLISHED_DIORITE, 0);
        MATERIAL_FLAGS.put(Material.POLISHED_GRANITE, 0);
        MATERIAL_FLAGS.put(Material.POTATOES, 0);
        MATERIAL_FLAGS.put(Material.PRISMARINE_BRICKS, 0);
        MATERIAL_FLAGS.put(Material.PURPLE_STAINED_GLASS, 0);
        MATERIAL_FLAGS.put(Material.PURPLE_STAINED_GLASS_PANE, 0);
        MATERIAL_FLAGS.put(Material.PURPLE_TERRACOTTA, 0);
        MATERIAL_FLAGS.put(Material.QUARTZ_PILLAR, 0);
        MATERIAL_FLAGS.put(Material.RED_SAND, 0);
        MATERIAL_FLAGS.put(Material.RED_STAINED_GLASS, 0);
        MATERIAL_FLAGS.put(Material.RED_STAINED_GLASS_PANE, 0);
        MATERIAL_FLAGS.put(Material.RED_TERRACOTTA, 0);
        MATERIAL_FLAGS.put(Material.SEAGRASS, 0);
        MATERIAL_FLAGS.put(Material.SEA_PICKLE, 0);
        MATERIAL_FLAGS.put(Material.SMOOTH_QUARTZ, 0);
        MATERIAL_FLAGS.put(Material.SMOOTH_RED_SANDSTONE, 0);
        MATERIAL_FLAGS.put(Material.SMOOTH_SANDSTONE, 0);
        MATERIAL_FLAGS.put(Material.SMOOTH_STONE, 0);
        MATERIAL_FLAGS.put(Material.TERRACOTTA, 0);
        MATERIAL_FLAGS.put(Material.TURTLE_EGG, 0);
        MATERIAL_FLAGS.put(Material.VOID_AIR, 0);
        MATERIAL_FLAGS.put(Material.WALL_TORCH, 0);
        MATERIAL_FLAGS.put(Material.WET_SPONGE, 0);
        MATERIAL_FLAGS.put(Material.WHITE_STAINED_GLASS, 0);
        MATERIAL_FLAGS.put(Material.WHITE_STAINED_GLASS_PANE, 0);
        MATERIAL_FLAGS.put(Material.WHITE_TERRACOTTA, 0);
        MATERIAL_FLAGS.put(Material.YELLOW_STAINED_GLASS, 0);
        MATERIAL_FLAGS.put(Material.YELLOW_STAINED_GLASS_PANE, 0);
        MATERIAL_FLAGS.put(Material.YELLOW_TERRACOTTA, 0);
        MATERIAL_FLAGS.put(Material.BAMBOO, 0);
        MATERIAL_FLAGS.put(Material.BAMBOO_SAPLING, 0);
        MATERIAL_FLAGS.put(Material.BARREL, 1);
        MATERIAL_FLAGS.put(Material.BELL, 1);
        MATERIAL_FLAGS.put(Material.BLAST_FURNACE, 1);
        MATERIAL_FLAGS.put(Material.CAMPFIRE, 3);
        MATERIAL_FLAGS.put(Material.CARTOGRAPHY_TABLE, 0);
        MATERIAL_FLAGS.put(Material.COMPOSTER, 1);
        MATERIAL_FLAGS.put(Material.FLETCHING_TABLE, 0);
        MATERIAL_FLAGS.put(Material.GRINDSTONE, 0);
        MATERIAL_FLAGS.put(Material.JIGSAW, 3);
        MATERIAL_FLAGS.put(Material.LANTERN, 0);
        MATERIAL_FLAGS.put(Material.LECTERN, 0);
        MATERIAL_FLAGS.put(Material.LOOM, 0);
        MATERIAL_FLAGS.put(Material.SCAFFOLDING, 0);
        MATERIAL_FLAGS.put(Material.SMITHING_TABLE, 0);
        MATERIAL_FLAGS.put(Material.SMOKER, 1);
        MATERIAL_FLAGS.put(Material.STONECUTTER, 0);
        MATERIAL_FLAGS.put(Material.SWEET_BERRY_BUSH, 1);
        MATERIAL_FLAGS.put(Material.IRON_SHOVEL, 0);
        MATERIAL_FLAGS.put(Material.IRON_PICKAXE, 0);
        MATERIAL_FLAGS.put(Material.IRON_AXE, 0);
        MATERIAL_FLAGS.put(Material.FLINT_AND_STEEL, 0);
        MATERIAL_FLAGS.put(Material.APPLE, 0);
        MATERIAL_FLAGS.put(Material.BOW, 0);
        MATERIAL_FLAGS.put(Material.ARROW, 0);
        MATERIAL_FLAGS.put(Material.COAL, 0);
        MATERIAL_FLAGS.put(Material.DIAMOND, 0);
        MATERIAL_FLAGS.put(Material.IRON_INGOT, 0);
        MATERIAL_FLAGS.put(Material.GOLD_INGOT, 0);
        MATERIAL_FLAGS.put(Material.IRON_SWORD, 0);
        MATERIAL_FLAGS.put(Material.WOODEN_SWORD, 0);
        MATERIAL_FLAGS.put(Material.WOODEN_SHOVEL, 0);
        MATERIAL_FLAGS.put(Material.WOODEN_PICKAXE, 0);
        MATERIAL_FLAGS.put(Material.WOODEN_AXE, 0);
        MATERIAL_FLAGS.put(Material.STONE_SWORD, 0);
        MATERIAL_FLAGS.put(Material.STONE_SHOVEL, 0);
        MATERIAL_FLAGS.put(Material.STONE_PICKAXE, 0);
        MATERIAL_FLAGS.put(Material.STONE_AXE, 0);
        MATERIAL_FLAGS.put(Material.DIAMOND_SWORD, 0);
        MATERIAL_FLAGS.put(Material.DIAMOND_SHOVEL, 0);
        MATERIAL_FLAGS.put(Material.DIAMOND_PICKAXE, 0);
        MATERIAL_FLAGS.put(Material.DIAMOND_AXE, 0);
        MATERIAL_FLAGS.put(Material.STICK, 0);
        MATERIAL_FLAGS.put(Material.BOWL, 0);
        MATERIAL_FLAGS.put(Material.MUSHROOM_STEW, 0);
        MATERIAL_FLAGS.put(Material.GOLDEN_SWORD, 0);
        MATERIAL_FLAGS.put(Material.GOLDEN_SHOVEL, 0);
        MATERIAL_FLAGS.put(Material.GOLDEN_PICKAXE, 0);
        MATERIAL_FLAGS.put(Material.GOLDEN_AXE, 0);
        MATERIAL_FLAGS.put(Material.STRING, 0);
        MATERIAL_FLAGS.put(Material.FEATHER, 0);
        MATERIAL_FLAGS.put(Material.GUNPOWDER, 0);
        MATERIAL_FLAGS.put(Material.WOODEN_HOE, 0);
        MATERIAL_FLAGS.put(Material.STONE_HOE, 0);
        MATERIAL_FLAGS.put(Material.IRON_HOE, 0);
        MATERIAL_FLAGS.put(Material.DIAMOND_HOE, 0);
        MATERIAL_FLAGS.put(Material.GOLDEN_HOE, 0);
        MATERIAL_FLAGS.put(Material.WHEAT_SEEDS, 0);
        MATERIAL_FLAGS.put(Material.BREAD, 0);
        MATERIAL_FLAGS.put(Material.LEATHER_HELMET, 0);
        MATERIAL_FLAGS.put(Material.LEATHER_CHESTPLATE, 0);
        MATERIAL_FLAGS.put(Material.LEATHER_LEGGINGS, 0);
        MATERIAL_FLAGS.put(Material.LEATHER_BOOTS, 0);
        MATERIAL_FLAGS.put(Material.CHAINMAIL_HELMET, 0);
        MATERIAL_FLAGS.put(Material.CHAINMAIL_CHESTPLATE, 0);
        MATERIAL_FLAGS.put(Material.CHAINMAIL_LEGGINGS, 0);
        MATERIAL_FLAGS.put(Material.CHAINMAIL_BOOTS, 0);
        MATERIAL_FLAGS.put(Material.IRON_HELMET, 0);
        MATERIAL_FLAGS.put(Material.IRON_CHESTPLATE, 0);
        MATERIAL_FLAGS.put(Material.IRON_LEGGINGS, 0);
        MATERIAL_FLAGS.put(Material.IRON_BOOTS, 0);
        MATERIAL_FLAGS.put(Material.DIAMOND_HELMET, 0);
        MATERIAL_FLAGS.put(Material.DIAMOND_CHESTPLATE, 0);
        MATERIAL_FLAGS.put(Material.DIAMOND_LEGGINGS, 0);
        MATERIAL_FLAGS.put(Material.DIAMOND_BOOTS, 0);
        MATERIAL_FLAGS.put(Material.GOLDEN_HELMET, 0);
        MATERIAL_FLAGS.put(Material.GOLDEN_CHESTPLATE, 0);
        MATERIAL_FLAGS.put(Material.GOLDEN_LEGGINGS, 0);
        MATERIAL_FLAGS.put(Material.GOLDEN_BOOTS, 0);
        MATERIAL_FLAGS.put(Material.FLINT, 0);
        MATERIAL_FLAGS.put(Material.PORKCHOP, 0);
        MATERIAL_FLAGS.put(Material.COOKED_PORKCHOP, 0);
        MATERIAL_FLAGS.put(Material.PAINTING, 0);
        MATERIAL_FLAGS.put(Material.GOLDEN_APPLE, 0);
        MATERIAL_FLAGS.put(Material.BUCKET, 0);
        MATERIAL_FLAGS.put(Material.WATER_BUCKET, 0);
        MATERIAL_FLAGS.put(Material.LAVA_BUCKET, 0);
        MATERIAL_FLAGS.put(Material.MINECART, 0);
        MATERIAL_FLAGS.put(Material.SADDLE, 0);
        MATERIAL_FLAGS.put(Material.IRON_DOOR, 0);
        MATERIAL_FLAGS.put(Material.REDSTONE, 0);
        MATERIAL_FLAGS.put(Material.SNOWBALL, 0);
        MATERIAL_FLAGS.put(Material.LEATHER, 0);
        MATERIAL_FLAGS.put(Material.MILK_BUCKET, 0);
        MATERIAL_FLAGS.put(Material.BRICKS, 0);
        MATERIAL_FLAGS.put(Material.CLAY_BALL, 0);
        MATERIAL_FLAGS.put(Material.SUGAR_CANE, 0);
        MATERIAL_FLAGS.put(Material.PAPER, 0);
        MATERIAL_FLAGS.put(Material.BOOK, 0);
        MATERIAL_FLAGS.put(Material.SLIME_BALL, 0);
        MATERIAL_FLAGS.put(Material.CHEST_MINECART, 0);
        MATERIAL_FLAGS.put(Material.FURNACE_MINECART, 0);
        MATERIAL_FLAGS.put(Material.EGG, 0);
        MATERIAL_FLAGS.put(Material.COMPASS, 0);
        MATERIAL_FLAGS.put(Material.FISHING_ROD, 0);
        MATERIAL_FLAGS.put(Material.CLOCK, 0);
        MATERIAL_FLAGS.put(Material.GLOWSTONE_DUST, 0);
        MATERIAL_FLAGS.put(Material.COD, 0);
        MATERIAL_FLAGS.put(Material.COOKED_COD, 0);
        MATERIAL_FLAGS.put(Material.INK_SAC, 0);
        MATERIAL_FLAGS.put(Material.BLACK_DYE, 0);
        MATERIAL_FLAGS.put(Material.BLUE_DYE, 0);
        MATERIAL_FLAGS.put(Material.BROWN_DYE, 0);
        MATERIAL_FLAGS.put(Material.CYAN_DYE, 0);
        MATERIAL_FLAGS.put(Material.GRAY_DYE, 0);
        MATERIAL_FLAGS.put(Material.GREEN_DYE, 0);
        MATERIAL_FLAGS.put(Material.LIGHT_BLUE_DYE, 0);
        MATERIAL_FLAGS.put(Material.LIGHT_GRAY_DYE, 0);
        MATERIAL_FLAGS.put(Material.LIME_DYE, 0);
        MATERIAL_FLAGS.put(Material.MAGENTA_DYE, 0);
        MATERIAL_FLAGS.put(Material.ORANGE_DYE, 0);
        MATERIAL_FLAGS.put(Material.PINK_DYE, 0);
        MATERIAL_FLAGS.put(Material.PURPLE_DYE, 0);
        MATERIAL_FLAGS.put(Material.RED_DYE, 0);
        MATERIAL_FLAGS.put(Material.WHITE_DYE, 0);
        MATERIAL_FLAGS.put(Material.YELLOW_DYE, 0);
        MATERIAL_FLAGS.put(Material.COCOA_BEANS, 0);
        MATERIAL_FLAGS.put(Material.BONE_MEAL, 4);
        MATERIAL_FLAGS.put(Material.BONE, 0);
        MATERIAL_FLAGS.put(Material.SUGAR, 0);
        MATERIAL_FLAGS.put(Material.COOKIE, 0);
        MATERIAL_FLAGS.put(Material.MAP, 0);
        MATERIAL_FLAGS.put(Material.SHEARS, 0);
        MATERIAL_FLAGS.put(Material.MELON_SLICE, 0);
        MATERIAL_FLAGS.put(Material.PUMPKIN_SEEDS, 0);
        MATERIAL_FLAGS.put(Material.MELON_SEEDS, 0);
        MATERIAL_FLAGS.put(Material.BEEF, 0);
        MATERIAL_FLAGS.put(Material.COOKED_BEEF, 0);
        MATERIAL_FLAGS.put(Material.CHICKEN, 0);
        MATERIAL_FLAGS.put(Material.COOKED_CHICKEN, 0);
        MATERIAL_FLAGS.put(Material.ROTTEN_FLESH, 0);
        MATERIAL_FLAGS.put(Material.ENDER_PEARL, 0);
        MATERIAL_FLAGS.put(Material.BLAZE_ROD, 0);
        MATERIAL_FLAGS.put(Material.GHAST_TEAR, 0);
        MATERIAL_FLAGS.put(Material.GOLD_NUGGET, 0);
        MATERIAL_FLAGS.put(Material.NETHER_WART, 0);
        MATERIAL_FLAGS.put(Material.POTION, 0);
        MATERIAL_FLAGS.put(Material.GLASS_BOTTLE, 0);
        MATERIAL_FLAGS.put(Material.SPIDER_EYE, 0);
        MATERIAL_FLAGS.put(Material.FERMENTED_SPIDER_EYE, 0);
        MATERIAL_FLAGS.put(Material.BLAZE_POWDER, 0);
        MATERIAL_FLAGS.put(Material.MAGMA_CREAM, 0);
        MATERIAL_FLAGS.put(Material.ENDER_EYE, 0);
        MATERIAL_FLAGS.put(Material.GLISTERING_MELON_SLICE, 0);
        MATERIAL_FLAGS.put(Material.EXPERIENCE_BOTTLE, 0);
        MATERIAL_FLAGS.put(Material.FIRE_CHARGE, 0);
        MATERIAL_FLAGS.put(Material.WRITABLE_BOOK, 0);
        MATERIAL_FLAGS.put(Material.WRITTEN_BOOK, 0);
        MATERIAL_FLAGS.put(Material.EMERALD, 0);
        MATERIAL_FLAGS.put(Material.ITEM_FRAME, 0);
        MATERIAL_FLAGS.put(Material.CARROT, 0);
        MATERIAL_FLAGS.put(Material.POTATO, 0);
        MATERIAL_FLAGS.put(Material.BAKED_POTATO, 0);
        MATERIAL_FLAGS.put(Material.POISONOUS_POTATO, 0);
        MATERIAL_FLAGS.put(Material.FILLED_MAP, 0);
        MATERIAL_FLAGS.put(Material.GOLDEN_CARROT, 0);
        MATERIAL_FLAGS.put(Material.CREEPER_HEAD, 0);
        MATERIAL_FLAGS.put(Material.CREEPER_WALL_HEAD, 0);
        MATERIAL_FLAGS.put(Material.DRAGON_HEAD, 0);
        MATERIAL_FLAGS.put(Material.DRAGON_WALL_HEAD, 0);
        MATERIAL_FLAGS.put(Material.PLAYER_HEAD, 0);
        MATERIAL_FLAGS.put(Material.PLAYER_WALL_HEAD, 0);
        MATERIAL_FLAGS.put(Material.ZOMBIE_HEAD, 0);
        MATERIAL_FLAGS.put(Material.ZOMBIE_WALL_HEAD, 0);
        MATERIAL_FLAGS.put(Material.SKELETON_SKULL, 0);
        MATERIAL_FLAGS.put(Material.SKELETON_WALL_SKULL, 0);
        MATERIAL_FLAGS.put(Material.WITHER_SKELETON_SKULL, 0);
        MATERIAL_FLAGS.put(Material.WITHER_SKELETON_WALL_SKULL, 0);
        MATERIAL_FLAGS.put(Material.CARROT_ON_A_STICK, 0);
        MATERIAL_FLAGS.put(Material.NETHER_STAR, 0);
        MATERIAL_FLAGS.put(Material.PUMPKIN_PIE, 0);
        MATERIAL_FLAGS.put(Material.FIREWORK_ROCKET, 0);
        MATERIAL_FLAGS.put(Material.FIREWORK_STAR, 0);
        MATERIAL_FLAGS.put(Material.ENCHANTED_BOOK, 0);
        MATERIAL_FLAGS.put(Material.NETHER_BRICKS, 0);
        MATERIAL_FLAGS.put(Material.QUARTZ, 0);
        MATERIAL_FLAGS.put(Material.TNT_MINECART, 0);
        MATERIAL_FLAGS.put(Material.HOPPER_MINECART, 0);
        MATERIAL_FLAGS.put(Material.LEAD, 0);
        MATERIAL_FLAGS.put(Material.NAME_TAG, 0);
        MATERIAL_FLAGS.put(Material.COMMAND_BLOCK_MINECART, 0);
        MATERIAL_FLAGS.put(Material.PRISMARINE_SHARD, 0);
        MATERIAL_FLAGS.put(Material.PRISMARINE_CRYSTALS, 0);
        MATERIAL_FLAGS.put(Material.RABBIT, 0);
        MATERIAL_FLAGS.put(Material.COOKED_RABBIT, 0);
        MATERIAL_FLAGS.put(Material.RABBIT_STEW, 0);
        MATERIAL_FLAGS.put(Material.RABBIT_FOOT, 0);
        MATERIAL_FLAGS.put(Material.RABBIT_HIDE, 0);
        MATERIAL_FLAGS.put(Material.ARMOR_STAND, 0);
        MATERIAL_FLAGS.put(Material.LEATHER_HORSE_ARMOR, 0);
        MATERIAL_FLAGS.put(Material.IRON_HORSE_ARMOR, 0);
        MATERIAL_FLAGS.put(Material.GOLDEN_HORSE_ARMOR, 0);
        MATERIAL_FLAGS.put(Material.DIAMOND_HORSE_ARMOR, 0);
        MATERIAL_FLAGS.put(Material.MUTTON, 0);
        MATERIAL_FLAGS.put(Material.COOKED_MUTTON, 0);
        MATERIAL_FLAGS.put(Material.BEETROOT, 0);
        MATERIAL_FLAGS.put(Material.BEETROOT_SOUP, 0);
        MATERIAL_FLAGS.put(Material.BEETROOT_SEEDS, 0);
        MATERIAL_FLAGS.put(Material.CHORUS_FRUIT, 0);
        MATERIAL_FLAGS.put(Material.POPPED_CHORUS_FRUIT, 0);
        MATERIAL_FLAGS.put(Material.SHIELD, 0);
        MATERIAL_FLAGS.put(Material.SPECTRAL_ARROW, 0);
        MATERIAL_FLAGS.put(Material.TIPPED_ARROW, 0);
        MATERIAL_FLAGS.put(Material.DRAGON_BREATH, 0);
        MATERIAL_FLAGS.put(Material.LINGERING_POTION, 0);
        MATERIAL_FLAGS.put(Material.ELYTRA, 0);
        MATERIAL_FLAGS.put(Material.END_CRYSTAL, 0);
        MATERIAL_FLAGS.put(Material.TOTEM_OF_UNDYING, 0);
        MATERIAL_FLAGS.put(Material.SHULKER_SHELL, 0);
        MATERIAL_FLAGS.put(Material.KNOWLEDGE_BOOK, 0);
        MATERIAL_FLAGS.put(Material.CHARCOAL, 0);
        MATERIAL_FLAGS.put(Material.COD_BUCKET, 0);
        MATERIAL_FLAGS.put(Material.COOKED_SALMON, 0);
        MATERIAL_FLAGS.put(Material.DEBUG_STICK, 0);
        MATERIAL_FLAGS.put(Material.DRIED_KELP, 0);
        MATERIAL_FLAGS.put(Material.ENCHANTED_GOLDEN_APPLE, 0);
        MATERIAL_FLAGS.put(Material.HEART_OF_THE_SEA, 0);
        MATERIAL_FLAGS.put(Material.IRON_NUGGET, 0);
        MATERIAL_FLAGS.put(Material.LAPIS_LAZULI, 0);
        MATERIAL_FLAGS.put(Material.NAUTILUS_SHELL, 0);
        MATERIAL_FLAGS.put(Material.PHANTOM_MEMBRANE, 0);
        MATERIAL_FLAGS.put(Material.PUFFERFISH, 0);
        MATERIAL_FLAGS.put(Material.PUFFERFISH_BUCKET, 0);
        MATERIAL_FLAGS.put(Material.SALMON, 0);
        MATERIAL_FLAGS.put(Material.SALMON_BUCKET, 0);
        MATERIAL_FLAGS.put(Material.TURTLE_SCUTE, 0);
        MATERIAL_FLAGS.put(Material.SPLASH_POTION, 0);
        MATERIAL_FLAGS.put(Material.TURTLE_HELMET, 0);
        MATERIAL_FLAGS.put(Material.TRIDENT, 0);
        MATERIAL_FLAGS.put(Material.TROPICAL_FISH, 0);
        MATERIAL_FLAGS.put(Material.TROPICAL_FISH_BUCKET, 0);
        MATERIAL_FLAGS.put(Material.CREEPER_BANNER_PATTERN, 0);
        MATERIAL_FLAGS.put(Material.FLOWER_BANNER_PATTERN, 0);
        MATERIAL_FLAGS.put(Material.GLOBE_BANNER_PATTERN, 0);
        MATERIAL_FLAGS.put(Material.MOJANG_BANNER_PATTERN, 0);
        MATERIAL_FLAGS.put(Material.SKULL_BANNER_PATTERN, 0);
        MATERIAL_FLAGS.put(Material.CROSSBOW, 0);
        MATERIAL_FLAGS.put(Material.SUSPICIOUS_STEW, 0);
        MATERIAL_FLAGS.put(Material.SWEET_BERRIES, 0);
        MATERIAL_FLAGS.put(Material.BEEHIVE, 1);
        MATERIAL_FLAGS.put(Material.BEE_NEST, 1);
        MATERIAL_FLAGS.put(Material.HONEY_BLOCK, 0);
        MATERIAL_FLAGS.put(Material.HONEYCOMB_BLOCK, 0);
        MATERIAL_FLAGS.put(Material.HONEY_BOTTLE, 0);
        MATERIAL_FLAGS.put(Material.HONEYCOMB, 0);
        MATERIAL_FLAGS.put(Material.ANCIENT_DEBRIS, 0);
        MATERIAL_FLAGS.put(Material.BASALT, 0);
        MATERIAL_FLAGS.put(Material.BLACKSTONE, 0);
        MATERIAL_FLAGS.put(Material.CHAIN, 0);
        MATERIAL_FLAGS.put(Material.CHISELED_NETHER_BRICKS, 0);
        MATERIAL_FLAGS.put(Material.CHISELED_POLISHED_BLACKSTONE, 0);
        MATERIAL_FLAGS.put(Material.CRACKED_NETHER_BRICKS, 0);
        MATERIAL_FLAGS.put(Material.CRACKED_POLISHED_BLACKSTONE_BRICKS, 0);
        MATERIAL_FLAGS.put(Material.CRIMSON_FUNGUS, 0);
        MATERIAL_FLAGS.put(Material.CRIMSON_NYLIUM, 0);
        MATERIAL_FLAGS.put(Material.CRIMSON_ROOTS, 0);
        MATERIAL_FLAGS.put(Material.CRYING_OBSIDIAN, 0);
        MATERIAL_FLAGS.put(Material.GILDED_BLACKSTONE, 0);
        MATERIAL_FLAGS.put(Material.LODESTONE, 0);
        MATERIAL_FLAGS.put(Material.NETHERITE_AXE, 0);
        MATERIAL_FLAGS.put(Material.NETHERITE_BLOCK, 0);
        MATERIAL_FLAGS.put(Material.NETHERITE_BOOTS, 0);
        MATERIAL_FLAGS.put(Material.NETHERITE_CHESTPLATE, 0);
        MATERIAL_FLAGS.put(Material.NETHERITE_HELMET, 0);
        MATERIAL_FLAGS.put(Material.NETHERITE_HOE, 0);
        MATERIAL_FLAGS.put(Material.NETHERITE_INGOT, 0);
        MATERIAL_FLAGS.put(Material.NETHERITE_LEGGINGS, 0);
        MATERIAL_FLAGS.put(Material.NETHERITE_PICKAXE, 0);
        MATERIAL_FLAGS.put(Material.NETHERITE_SCRAP, 0);
        MATERIAL_FLAGS.put(Material.NETHERITE_SHOVEL, 0);
        MATERIAL_FLAGS.put(Material.NETHERITE_SWORD, 0);
        MATERIAL_FLAGS.put(Material.NETHER_SPROUTS, 0);
        MATERIAL_FLAGS.put(Material.PIGLIN_BANNER_PATTERN, 0);
        MATERIAL_FLAGS.put(Material.POLISHED_BASALT, 0);
        MATERIAL_FLAGS.put(Material.POLISHED_BLACKSTONE, 0);
        MATERIAL_FLAGS.put(Material.POLISHED_BLACKSTONE_BRICKS, 0);
        MATERIAL_FLAGS.put(Material.POLISHED_BLACKSTONE_PRESSURE_PLATE, 0);
        MATERIAL_FLAGS.put(Material.QUARTZ_BRICKS, 0);
        MATERIAL_FLAGS.put(Material.RESPAWN_ANCHOR, 1);
        MATERIAL_FLAGS.put(Material.SHROOMLIGHT, 0);
        MATERIAL_FLAGS.put(Material.SOUL_CAMPFIRE, 1);
        MATERIAL_FLAGS.put(Material.SOUL_FIRE, 0);
        MATERIAL_FLAGS.put(Material.SOUL_LANTERN, 0);
        MATERIAL_FLAGS.put(Material.SOUL_SOIL, 0);
        MATERIAL_FLAGS.put(Material.SOUL_TORCH, 0);
        MATERIAL_FLAGS.put(Material.SOUL_WALL_TORCH, 0);
        MATERIAL_FLAGS.put(Material.TARGET, 0);
        MATERIAL_FLAGS.put(Material.TWISTING_VINES, 0);
        MATERIAL_FLAGS.put(Material.TWISTING_VINES_PLANT, 0);
        MATERIAL_FLAGS.put(Material.WARPED_FUNGUS, 0);
        MATERIAL_FLAGS.put(Material.WARPED_FUNGUS_ON_A_STICK, 0);
        MATERIAL_FLAGS.put(Material.WARPED_NYLIUM, 0);
        MATERIAL_FLAGS.put(Material.WARPED_ROOTS, 0);
        MATERIAL_FLAGS.put(Material.WARPED_WART_BLOCK, 0);
        MATERIAL_FLAGS.put(Material.WEEPING_VINES, 0);
        MATERIAL_FLAGS.put(Material.WEEPING_VINES_PLANT, 0);
        MATERIAL_FLAGS.put(Material.DEEPSLATE, 0);
        MATERIAL_FLAGS.put(Material.COBBLED_DEEPSLATE, 0);
        MATERIAL_FLAGS.put(Material.POLISHED_DEEPSLATE, 0);
        MATERIAL_FLAGS.put(Material.CALCITE, 0);
        MATERIAL_FLAGS.put(Material.TUFF, 0);
        MATERIAL_FLAGS.put(Material.DRIPSTONE_BLOCK, 0);
        MATERIAL_FLAGS.put(Material.ROOTED_DIRT, 0);
        MATERIAL_FLAGS.put(Material.RAW_IRON_BLOCK, 0);
        MATERIAL_FLAGS.put(Material.RAW_COPPER_BLOCK, 0);
        MATERIAL_FLAGS.put(Material.RAW_GOLD_BLOCK, 0);
        MATERIAL_FLAGS.put(Material.AMETHYST_BLOCK, 0);
        MATERIAL_FLAGS.put(Material.BUDDING_AMETHYST, 0);
        MATERIAL_FLAGS.put(Material.EXPOSED_COPPER, 0);
        MATERIAL_FLAGS.put(Material.WEATHERED_COPPER, 0);
        MATERIAL_FLAGS.put(Material.OXIDIZED_COPPER, 0);
        MATERIAL_FLAGS.put(Material.CUT_COPPER, 0);
        MATERIAL_FLAGS.put(Material.EXPOSED_CUT_COPPER, 0);
        MATERIAL_FLAGS.put(Material.WEATHERED_CUT_COPPER, 0);
        MATERIAL_FLAGS.put(Material.OXIDIZED_CUT_COPPER, 0);
        MATERIAL_FLAGS.put(Material.WAXED_COPPER_BLOCK, 0);
        MATERIAL_FLAGS.put(Material.WAXED_EXPOSED_COPPER, 0);
        MATERIAL_FLAGS.put(Material.WAXED_WEATHERED_COPPER, 0);
        MATERIAL_FLAGS.put(Material.WAXED_OXIDIZED_COPPER, 0);
        MATERIAL_FLAGS.put(Material.AZALEA, 0);
        MATERIAL_FLAGS.put(Material.FLOWERING_AZALEA, 0);
        MATERIAL_FLAGS.put(Material.COPPER_BLOCK, 0);
        MATERIAL_FLAGS.put(Material.WAXED_CUT_COPPER, 0);
        MATERIAL_FLAGS.put(Material.WAXED_EXPOSED_CUT_COPPER, 0);
        MATERIAL_FLAGS.put(Material.WAXED_WEATHERED_CUT_COPPER, 0);
        MATERIAL_FLAGS.put(Material.WAXED_OXIDIZED_CUT_COPPER, 0);
        MATERIAL_FLAGS.put(Material.TINTED_GLASS, 0);
        MATERIAL_FLAGS.put(Material.SPORE_BLOSSOM, 0);
        MATERIAL_FLAGS.put(Material.MOSS_CARPET, 0);
        MATERIAL_FLAGS.put(Material.BIG_DRIPLEAF, 0);
        MATERIAL_FLAGS.put(Material.BIG_DRIPLEAF_STEM, 0);
        MATERIAL_FLAGS.put(Material.SMALL_DRIPLEAF, 0);
        MATERIAL_FLAGS.put(Material.SMOOTH_BASALT, 0);
        MATERIAL_FLAGS.put(Material.INFESTED_DEEPSLATE, 0);
        MATERIAL_FLAGS.put(Material.DEEPSLATE_BRICKS, 0);
        MATERIAL_FLAGS.put(Material.CRACKED_DEEPSLATE_BRICKS, 0);
        MATERIAL_FLAGS.put(Material.DEEPSLATE_TILES, 0);
        MATERIAL_FLAGS.put(Material.CRACKED_DEEPSLATE_TILES, 0);
        MATERIAL_FLAGS.put(Material.CHISELED_DEEPSLATE, 0);
        MATERIAL_FLAGS.put(Material.GLOW_LICHEN, 0);
        MATERIAL_FLAGS.put(Material.LIGHT, 1);
        MATERIAL_FLAGS.put(Material.LIGHTNING_ROD, 0);
        MATERIAL_FLAGS.put(Material.SCULK_SENSOR, 0);
        MATERIAL_FLAGS.put(Material.AMETHYST_SHARD, 0);
        MATERIAL_FLAGS.put(Material.RAW_IRON, 0);
        MATERIAL_FLAGS.put(Material.RAW_COPPER, 0);
        MATERIAL_FLAGS.put(Material.COPPER_INGOT, 0);
        MATERIAL_FLAGS.put(Material.RAW_GOLD, 0);
        MATERIAL_FLAGS.put(Material.POWDER_SNOW_BUCKET, 0);
        MATERIAL_FLAGS.put(Material.AXOLOTL_BUCKET, 0);
        MATERIAL_FLAGS.put(Material.BUNDLE, 0);
        MATERIAL_FLAGS.put(Material.SPYGLASS, 0);
        MATERIAL_FLAGS.put(Material.GLOW_INK_SAC, 0);
        MATERIAL_FLAGS.put(Material.GLOW_ITEM_FRAME, 0);
        MATERIAL_FLAGS.put(Material.GLOW_BERRIES, 0);
        MATERIAL_FLAGS.put(Material.SMALL_AMETHYST_BUD, 0);
        MATERIAL_FLAGS.put(Material.MEDIUM_AMETHYST_BUD, 0);
        MATERIAL_FLAGS.put(Material.LARGE_AMETHYST_BUD, 0);
        MATERIAL_FLAGS.put(Material.AMETHYST_CLUSTER, 0);
        MATERIAL_FLAGS.put(Material.POWDER_SNOW, 0);
        MATERIAL_FLAGS.put(Material.CAVE_VINES, 1);
        MATERIAL_FLAGS.put(Material.CAVE_VINES_PLANT, 1);
        MATERIAL_FLAGS.put(Material.MOSS_BLOCK, 0);
        MATERIAL_FLAGS.put(Material.HANGING_ROOTS, 0);
        MATERIAL_FLAGS.put(Material.POINTED_DRIPSTONE, 0);
        MATERIAL_FLAGS.put(Material.MUD, 0);
        MATERIAL_FLAGS.put(Material.MANGROVE_ROOTS, 0);
        MATERIAL_FLAGS.put(Material.MUDDY_MANGROVE_ROOTS, 0);
        MATERIAL_FLAGS.put(Material.PACKED_MUD, 0);
        MATERIAL_FLAGS.put(Material.MUD_BRICKS, 0);
        MATERIAL_FLAGS.put(Material.SCULK, 0);
        MATERIAL_FLAGS.put(Material.SCULK_VEIN, 0);
        MATERIAL_FLAGS.put(Material.SCULK_CATALYST, 0);
        MATERIAL_FLAGS.put(Material.SCULK_SHRIEKER, 0);
        MATERIAL_FLAGS.put(Material.TADPOLE_BUCKET, 0);
        MATERIAL_FLAGS.put(Material.RECOVERY_COMPASS, 0);
        MATERIAL_FLAGS.put(Material.DISC_FRAGMENT_5, 0);
        MATERIAL_FLAGS.put(Material.GOAT_HORN, 0);
        MATERIAL_FLAGS.put(Material.OCHRE_FROGLIGHT, 0);
        MATERIAL_FLAGS.put(Material.VERDANT_FROGLIGHT, 0);
        MATERIAL_FLAGS.put(Material.PEARLESCENT_FROGLIGHT, 0);
        MATERIAL_FLAGS.put(Material.FROGSPAWN, 0);
        MATERIAL_FLAGS.put(Material.ECHO_SHARD, 0);
        MATERIAL_FLAGS.put(Material.REINFORCED_DEEPSLATE, 0);
        MATERIAL_FLAGS.put(Material.BAMBOO_MOSAIC, 0);
        MATERIAL_FLAGS.put(Material.BAMBOO_BLOCK, 0);
        MATERIAL_FLAGS.put(Material.STRIPPED_BAMBOO_BLOCK, 0);
        MATERIAL_FLAGS.put(Material.SUSPICIOUS_SAND, 0);
        MATERIAL_FLAGS.put(Material.SUSPICIOUS_GRAVEL, 0);
        MATERIAL_FLAGS.put(Material.PITCHER_PLANT, 0);
        MATERIAL_FLAGS.put(Material.CHISELED_BOOKSHELF, 1);
        MATERIAL_FLAGS.put(Material.DECORATED_POT, 1);
        MATERIAL_FLAGS.put(Material.BRUSH, 0);
        MATERIAL_FLAGS.put(Material.SNIFFER_EGG, 0);
        MATERIAL_FLAGS.put(Material.CALIBRATED_SCULK_SENSOR, 0);
        MATERIAL_FLAGS.put(Material.PIGLIN_HEAD, 0);
        MATERIAL_FLAGS.put(Material.PIGLIN_WALL_HEAD, 0);
        MATERIAL_FLAGS.put(Material.TORCHFLOWER_SEEDS, 0);
        MATERIAL_FLAGS.put(Material.TORCHFLOWER_CROP, 0);
        MATERIAL_FLAGS.put(Material.PITCHER_CROP, 0);
        MATERIAL_FLAGS.put(Material.PINK_PETALS, 0);
        MATERIAL_FLAGS.put(Material.PITCHER_POD, 0);
        MATERIAL_FLAGS.put(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE, 0);
        MATERIAL_FLAGS.put(Material.ARMADILLO_SCUTE, 0);
        MATERIAL_FLAGS.put(Material.WOLF_ARMOR, 0);
        MATERIAL_FLAGS.put(Material.CHISELED_TUFF, 0);
        MATERIAL_FLAGS.put(Material.POLISHED_TUFF, 0);
        MATERIAL_FLAGS.put(Material.TUFF_BRICKS, 0);
        MATERIAL_FLAGS.put(Material.CHISELED_TUFF_BRICKS, 0);
        MATERIAL_FLAGS.put(Material.HEAVY_CORE, 0);
        MATERIAL_FLAGS.put(Material.CHISELED_COPPER, 0);
        MATERIAL_FLAGS.put(Material.EXPOSED_CHISELED_COPPER, 0);
        MATERIAL_FLAGS.put(Material.WEATHERED_CHISELED_COPPER, 0);
        MATERIAL_FLAGS.put(Material.OXIDIZED_CHISELED_COPPER, 0);
        MATERIAL_FLAGS.put(Material.WAXED_CHISELED_COPPER, 0);
        MATERIAL_FLAGS.put(Material.WAXED_EXPOSED_CHISELED_COPPER, 0);
        MATERIAL_FLAGS.put(Material.WAXED_WEATHERED_CHISELED_COPPER, 0);
        MATERIAL_FLAGS.put(Material.WAXED_OXIDIZED_CHISELED_COPPER, 0);
        MATERIAL_FLAGS.put(Material.CRAFTER, 0);
        MATERIAL_FLAGS.put(Material.FLOW_BANNER_PATTERN, 0);
        MATERIAL_FLAGS.put(Material.GUSTER_BANNER_PATTERN, 0);
        MATERIAL_FLAGS.put(Material.COPPER_GRATE, 0);
        MATERIAL_FLAGS.put(Material.WEATHERED_COPPER_GRATE, 0);
        MATERIAL_FLAGS.put(Material.EXPOSED_COPPER_GRATE, 0);
        MATERIAL_FLAGS.put(Material.OXIDIZED_COPPER_GRATE, 0);
        MATERIAL_FLAGS.put(Material.WAXED_COPPER_GRATE, 0);
        MATERIAL_FLAGS.put(Material.WAXED_WEATHERED_COPPER_GRATE, 0);
        MATERIAL_FLAGS.put(Material.WAXED_EXPOSED_COPPER_GRATE, 0);
        MATERIAL_FLAGS.put(Material.WAXED_OXIDIZED_COPPER_GRATE, 0);
        MATERIAL_FLAGS.put(Material.COPPER_BULB, 0);
        MATERIAL_FLAGS.put(Material.WEATHERED_COPPER_BULB, 0);
        MATERIAL_FLAGS.put(Material.EXPOSED_COPPER_BULB, 0);
        MATERIAL_FLAGS.put(Material.OXIDIZED_COPPER_BULB, 0);
        MATERIAL_FLAGS.put(Material.WAXED_COPPER_BULB, 0);
        MATERIAL_FLAGS.put(Material.WAXED_WEATHERED_COPPER_BULB, 0);
        MATERIAL_FLAGS.put(Material.WAXED_EXPOSED_COPPER_BULB, 0);
        MATERIAL_FLAGS.put(Material.WAXED_OXIDIZED_COPPER_BULB, 0);
        MATERIAL_FLAGS.put(Material.TRIAL_SPAWNER, 0);
        MATERIAL_FLAGS.put(Material.TRIAL_KEY, 0);
        MATERIAL_FLAGS.put(Material.OMINOUS_TRIAL_KEY, 0);
        MATERIAL_FLAGS.put(Material.VAULT, 1);
        MATERIAL_FLAGS.put(Material.OMINOUS_BOTTLE, 0);
        MATERIAL_FLAGS.put(Material.BREEZE_ROD, 0);
        MATERIAL_FLAGS.put(Material.WIND_CHARGE, 0);
        Materials.putMaterialTag((Tag<Material>)Tag.SHULKER_BOXES, 1);
        Materials.putMaterialTag((Tag<Material>)Tag.ITEMS_BOATS, 0);
        Materials.putMaterialTag((Tag<Material>)Tag.BANNERS, 0);
        Materials.putMaterialTag((Tag<Material>)Tag.SLABS, 0);
        Materials.putMaterialTag((Tag<Material>)Tag.PLANKS, 0);
        Materials.putMaterialTag((Tag<Material>)Tag.WOOL_CARPETS, 0);
        Materials.putMaterialTag((Tag<Material>)Tag.SAPLINGS, 0);
        Materials.putMaterialTag((Tag<Material>)Tag.LOGS, 0);
        Materials.putMaterialTag((Tag<Material>)Tag.LEAVES, 0);
        Materials.putMaterialTag((Tag<Material>)Tag.STAIRS, 0);
        Materials.putMaterialTag((Tag<Material>)Tag.WOOL, 0);
        Materials.putMaterialTag((Tag<Material>)Tag.WOODEN_PRESSURE_PLATES, 0);
        Materials.putMaterialTag((Tag<Material>)Tag.BUTTONS, 1);
        Materials.putMaterialTag((Tag<Material>)Tag.FLOWER_POTS, 1);
        Materials.putMaterialTag((Tag<Material>)Tag.WALLS, 0);
        Materials.putMaterialTag((Tag<Material>)Tag.ALL_SIGNS, 0);
        Materials.putMaterialTag((Tag<Material>)Tag.SMALL_FLOWERS, 0);
        Materials.putMaterialTag((Tag<Material>)Tag.BEDS, 1);
        Materials.putMaterialTag((Tag<Material>)Tag.ITEMS_BANNERS, 0);
        Materials.putMaterialTag((Tag<Material>)Tag.FENCE_GATES, 1);
        Materials.putMaterialTag((Tag<Material>)Tag.FENCES, 0);
        Materials.putMaterialTag((Tag<Material>)Tag.COAL_ORES, 0);
        Materials.putMaterialTag((Tag<Material>)Tag.IRON_ORES, 0);
        Materials.putMaterialTag((Tag<Material>)Tag.GOLD_ORES, 0);
        Materials.putMaterialTag((Tag<Material>)Tag.DIAMOND_ORES, 0);
        Materials.putMaterialTag((Tag<Material>)Tag.REDSTONE_ORES, 0);
        Materials.putMaterialTag((Tag<Material>)Tag.COPPER_ORES, 0);
        Materials.putMaterialTag((Tag<Material>)Tag.EMERALD_ORES, 0);
        Materials.putMaterialTag((Tag<Material>)Tag.LAPIS_ORES, 0);
        Materials.putMaterialTag((Tag<Material>)Tag.CANDLES, 1);
        Materials.putMaterialTag((Tag<Material>)Tag.CANDLE_CAKES, 1);
        Materials.putMaterialTag((Tag<Material>)Tag.CAULDRONS, 1);
        Materials.putMaterialTag((Tag<Material>)Tag.ITEMS_TRIM_TEMPLATES, 0);
        Materials.putMaterialTag((Tag<Material>)Tag.ITEMS_DECORATED_POT_SHERDS, 0);
        Stream.concat(Stream.concat(Tag.CORAL_BLOCKS.getValues().stream(), Tag.CORALS.getValues().stream()), Tag.WALL_CORALS.getValues().stream()).forEach(m -> {
            MATERIAL_FLAGS.put((Material)m, 0);
            Material dead = Material.getMaterial((String)("DEAD_" + m.name()));
            if (dead != null) {
                MATERIAL_FLAGS.put(dead, 0);
            }
        });
        for (Material material : Material.values()) {
            if (material.isLegacy()) continue;
            if (Materials.isSpawnEgg(material)) {
                MATERIAL_FLAGS.put(material, 0);
            }
            if (material.name().startsWith("MUSIC_DISC_")) {
                MATERIAL_FLAGS.put(material, 0);
            }
            if (MATERIAL_FLAGS.containsKey(material)) continue;
            logger.fine("Missing material definition for " + (material.isBlock() ? "block " : "item ") + material.name());
        }
        DAMAGE_EFFECTS.add(PotionEffectType.SLOWNESS);
        DAMAGE_EFFECTS.add(PotionEffectType.MINING_FATIGUE);
        DAMAGE_EFFECTS.add(PotionEffectType.INSTANT_DAMAGE);
        DAMAGE_EFFECTS.add(PotionEffectType.NAUSEA);
        DAMAGE_EFFECTS.add(PotionEffectType.BLINDNESS);
        DAMAGE_EFFECTS.add(PotionEffectType.HUNGER);
        DAMAGE_EFFECTS.add(PotionEffectType.WEAKNESS);
        DAMAGE_EFFECTS.add(PotionEffectType.POISON);
        DAMAGE_EFFECTS.add(PotionEffectType.WITHER);
        DAMAGE_EFFECTS.add(PotionEffectType.GLOWING);
        DAMAGE_EFFECTS.add(PotionEffectType.LEVITATION);
        DAMAGE_EFFECTS.add(PotionEffectType.UNLUCK);
        DAMAGE_EFFECTS.add(PotionEffectType.BAD_OMEN);
        DAMAGE_EFFECTS.add(PotionEffectType.DARKNESS);
        DAMAGE_EFFECTS.add(PotionEffectType.TRIAL_OMEN);
        DAMAGE_EFFECTS.add(PotionEffectType.WIND_CHARGED);
        DAMAGE_EFFECTS.add(PotionEffectType.WEAVING);
        DAMAGE_EFFECTS.add(PotionEffectType.OOZING);
        DAMAGE_EFFECTS.add(PotionEffectType.INFESTED);
    }
}

