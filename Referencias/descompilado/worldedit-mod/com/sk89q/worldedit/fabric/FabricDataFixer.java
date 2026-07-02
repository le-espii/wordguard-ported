/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParseException
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  javax.annotation.Nullable
 *  net.minecraft.class_1208
 *  net.minecraft.class_1767
 *  net.minecraft.class_2350
 *  net.minecraft.class_2487
 *  net.minecraft.class_2494
 *  net.minecraft.class_2499
 *  net.minecraft.class_2509
 *  net.minecraft.class_2519
 *  net.minecraft.class_2520
 *  net.minecraft.class_2561
 *  net.minecraft.class_2561$class_2562
 *  net.minecraft.class_2960
 *  net.minecraft.class_3518
 *  net.minecraft.class_3551
 *  net.minecraft.class_5250
 *  net.minecraft.class_7225$class_7874
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.sk89q.worldedit.fabric;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.sk89q.worldedit.fabric.FabricWorldEdit;
import com.sk89q.worldedit.fabric.internal.NBTConverter;
import com.sk89q.worldedit.world.DataFixer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.class_1208;
import net.minecraft.class_1767;
import net.minecraft.class_2350;
import net.minecraft.class_2487;
import net.minecraft.class_2494;
import net.minecraft.class_2499;
import net.minecraft.class_2509;
import net.minecraft.class_2519;
import net.minecraft.class_2520;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_3518;
import net.minecraft.class_3551;
import net.minecraft.class_5250;
import net.minecraft.class_7225;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.enginehub.linbus.tree.LinCompoundTag;

class FabricDataFixer
implements com.sk89q.worldedit.world.DataFixer {
    private static final class_2509 OPS_NBT = class_2509.field_11560;
    private static final int LEGACY_VERSION = 1343;
    private static int DATA_VERSION;
    private static FabricDataFixer INSTANCE;
    private final Map<LegacyType, List<DataConverter>> converters = new EnumMap<LegacyType, List<DataConverter>>(LegacyType.class);
    private final Map<LegacyType, List<DataInspector>> inspectors = new EnumMap<LegacyType, List<DataInspector>>(LegacyType.class);
    private final DataFixer fixer;
    private static final Map<String, LegacyType> DFU_TO_LEGACY;
    private static final Map<String, class_2960> OLD_ID_TO_KEY_MAP;

    @Override
    public <T> T fixUp(DataFixer.FixType<T> type, T original, int srcVer) {
        if (type == DataFixer.FixTypes.CHUNK) {
            return (T)this.fixChunk((LinCompoundTag)original, srcVer);
        }
        if (type == DataFixer.FixTypes.BLOCK_ENTITY) {
            return (T)this.fixBlockEntity((LinCompoundTag)original, srcVer);
        }
        if (type == DataFixer.FixTypes.ENTITY) {
            return (T)this.fixEntity((LinCompoundTag)original, srcVer);
        }
        if (type == DataFixer.FixTypes.BLOCK_STATE) {
            return (T)this.fixBlockState((String)original, srcVer);
        }
        if (type == DataFixer.FixTypes.ITEM_TYPE) {
            return (T)this.fixItemType((String)original, srcVer);
        }
        if (type == DataFixer.FixTypes.BIOME) {
            return (T)this.fixBiome((String)original, srcVer);
        }
        return original;
    }

    private static LinCompoundTag applyAsNbt(LinCompoundTag original, Function<class_2487, class_2487> function) {
        class_2487 originalNative = NBTConverter.toNative(original);
        class_2487 fixedNative = function.apply(originalNative);
        return NBTConverter.fromNative(fixedNative);
    }

    private LinCompoundTag fixChunk(LinCompoundTag originalChunk, int srcVer) {
        return FabricDataFixer.applyAsNbt(originalChunk, tag -> FabricDataFixer.convert(class_1208.field_5726, tag, srcVer));
    }

    private LinCompoundTag fixBlockEntity(LinCompoundTag origTileEnt, int srcVer) {
        return FabricDataFixer.applyAsNbt(origTileEnt, tag -> FabricDataFixer.convert(class_1208.field_5727, tag, srcVer));
    }

    private LinCompoundTag fixEntity(LinCompoundTag origEnt, int srcVer) {
        return FabricDataFixer.applyAsNbt(origEnt, tag -> FabricDataFixer.convert(class_1208.field_5729, tag, srcVer));
    }

    private String fixBlockState(String blockState, int srcVer) {
        class_2487 stateNBT = FabricDataFixer.stateToNBT(blockState);
        Dynamic dynamic = new Dynamic((DynamicOps)OPS_NBT, (Object)stateNBT);
        class_2487 fixed = (class_2487)FabricDataFixer.INSTANCE.fixer.update(class_1208.field_5720, dynamic, srcVer, DATA_VERSION).getValue();
        return this.nbtToState(fixed);
    }

    private String nbtToState(class_2487 tagCompound) {
        StringBuilder sb = new StringBuilder();
        sb.append(tagCompound.method_10558("Name"));
        if (tagCompound.method_10573("Properties", 10)) {
            sb.append('[');
            class_2487 props = tagCompound.method_10562("Properties");
            sb.append(props.method_10541().stream().map(k -> k + "=" + props.method_10558(k).replace("\"", "")).collect(Collectors.joining(",")));
            sb.append(']');
        }
        return sb.toString();
    }

    private static class_2487 stateToNBT(String blockState) {
        int propIdx = blockState.indexOf(91);
        class_2487 tag = new class_2487();
        if (propIdx < 0) {
            tag.method_10582("Name", blockState);
        } else {
            String[] propArr;
            tag.method_10582("Name", blockState.substring(0, propIdx));
            class_2487 propTag = new class_2487();
            String props = blockState.substring(propIdx + 1, blockState.length() - 1);
            for (String pair : propArr = props.split(",")) {
                String[] split = pair.split("=");
                propTag.method_10582(split[0], split[1]);
            }
            tag.method_10566("Properties", (class_2520)propTag);
        }
        return tag;
    }

    private String fixBiome(String key, int srcVer) {
        return FabricDataFixer.fixName(key, srcVer, class_1208.field_5728);
    }

    private String fixItemType(String key, int srcVer) {
        return FabricDataFixer.fixName(key, srcVer, class_1208.field_5713);
    }

    private static String fixName(String key, int srcVer, DSL.TypeReference type) {
        return FabricDataFixer.INSTANCE.fixer.update(type, new Dynamic((DynamicOps)OPS_NBT, (Object)class_2519.method_23256((String)key)), srcVer, DATA_VERSION).asString().result().orElse(key);
    }

    FabricDataFixer(int dataVersion) {
        DATA_VERSION = dataVersion;
        INSTANCE = this;
        this.registerConverters();
        this.registerInspectors();
        this.fixer = new WrappedDataFixer(class_3551.method_15450());
    }

    public static class_2487 convert(LegacyType type, class_2487 cmp) {
        return FabricDataFixer.convert(type.getDFUType(), cmp);
    }

    public static class_2487 convert(LegacyType type, class_2487 cmp, int sourceVer) {
        return FabricDataFixer.convert(type.getDFUType(), cmp, sourceVer);
    }

    public static class_2487 convert(LegacyType type, class_2487 cmp, int sourceVer, int targetVer) {
        return FabricDataFixer.convert(type.getDFUType(), cmp, sourceVer, targetVer);
    }

    public static class_2487 convert(DSL.TypeReference type, class_2487 cmp) {
        int i = cmp.method_10573("DataVersion", 99) ? cmp.method_10550("DataVersion") : -1;
        return FabricDataFixer.convert(type, cmp, i);
    }

    public static class_2487 convert(DSL.TypeReference type, class_2487 cmp, int sourceVer) {
        return FabricDataFixer.convert(type, cmp, sourceVer, DATA_VERSION);
    }

    public static class_2487 convert(DSL.TypeReference type, class_2487 cmp, int sourceVer, int targetVer) {
        if (sourceVer >= targetVer) {
            return cmp;
        }
        return (class_2487)FabricDataFixer.INSTANCE.fixer.update(type, new Dynamic((DynamicOps)OPS_NBT, (Object)cmp), sourceVer, targetVer).getValue();
    }

    private void registerInspector(LegacyType type, DataInspector inspector) {
        this.inspectors.computeIfAbsent(type, k -> new ArrayList()).add(inspector);
    }

    private void registerConverter(LegacyType type, DataConverter converter) {
        int version = converter.getDataVersion();
        List list = this.converters.computeIfAbsent(type, k -> new ArrayList());
        if (!list.isEmpty() && ((DataConverter)list.get(list.size() - 1)).getDataVersion() > version) {
            for (int j = 0; j < list.size(); ++j) {
                if (((DataConverter)list.get(j)).getDataVersion() <= version) continue;
                list.add(j, converter);
                break;
            }
        } else {
            list.add(converter);
        }
    }

    private void registerInspectors() {
        this.registerEntityItemList("EntityHorseDonkey", "SaddleItem", "Items");
        this.registerEntityItemList("EntityHorseMule", "Items");
        this.registerEntityItemList("EntityMinecartChest", "Items");
        this.registerEntityItemList("EntityMinecartHopper", "Items");
        this.registerEntityItemList("EntityVillager", "Inventory");
        this.registerEntityItemListEquipment("EntityArmorStand");
        this.registerEntityItemListEquipment("EntityBat");
        this.registerEntityItemListEquipment("EntityBlaze");
        this.registerEntityItemListEquipment("EntityCaveSpider");
        this.registerEntityItemListEquipment("EntityChicken");
        this.registerEntityItemListEquipment("EntityCow");
        this.registerEntityItemListEquipment("EntityCreeper");
        this.registerEntityItemListEquipment("EntityEnderDragon");
        this.registerEntityItemListEquipment("EntityEnderman");
        this.registerEntityItemListEquipment("EntityEndermite");
        this.registerEntityItemListEquipment("EntityEvoker");
        this.registerEntityItemListEquipment("EntityGhast");
        this.registerEntityItemListEquipment("EntityGiantZombie");
        this.registerEntityItemListEquipment("EntityGuardian");
        this.registerEntityItemListEquipment("EntityGuardianElder");
        this.registerEntityItemListEquipment("EntityHorse");
        this.registerEntityItemListEquipment("EntityHorseDonkey");
        this.registerEntityItemListEquipment("EntityHorseMule");
        this.registerEntityItemListEquipment("EntityHorseSkeleton");
        this.registerEntityItemListEquipment("EntityHorseZombie");
        this.registerEntityItemListEquipment("EntityIronGolem");
        this.registerEntityItemListEquipment("EntityMagmaCube");
        this.registerEntityItemListEquipment("EntityMushroomCow");
        this.registerEntityItemListEquipment("EntityOcelot");
        this.registerEntityItemListEquipment("EntityPig");
        this.registerEntityItemListEquipment("EntityPigZombie");
        this.registerEntityItemListEquipment("EntityRabbit");
        this.registerEntityItemListEquipment("EntitySheep");
        this.registerEntityItemListEquipment("EntityShulker");
        this.registerEntityItemListEquipment("EntitySilverfish");
        this.registerEntityItemListEquipment("EntitySkeleton");
        this.registerEntityItemListEquipment("EntitySkeletonStray");
        this.registerEntityItemListEquipment("EntitySkeletonWither");
        this.registerEntityItemListEquipment("EntitySlime");
        this.registerEntityItemListEquipment("EntitySnowman");
        this.registerEntityItemListEquipment("EntitySpider");
        this.registerEntityItemListEquipment("EntitySquid");
        this.registerEntityItemListEquipment("EntityVex");
        this.registerEntityItemListEquipment("EntityVillager");
        this.registerEntityItemListEquipment("EntityVindicator");
        this.registerEntityItemListEquipment("EntityWitch");
        this.registerEntityItemListEquipment("EntityWither");
        this.registerEntityItemListEquipment("EntityWolf");
        this.registerEntityItemListEquipment("EntityZombie");
        this.registerEntityItemListEquipment("EntityZombieHusk");
        this.registerEntityItemListEquipment("EntityZombieVillager");
        this.registerEntityItemSingle("EntityFireworks", "FireworksItem");
        this.registerEntityItemSingle("EntityHorse", "ArmorItem");
        this.registerEntityItemSingle("EntityHorse", "SaddleItem");
        this.registerEntityItemSingle("EntityHorseMule", "SaddleItem");
        this.registerEntityItemSingle("EntityHorseSkeleton", "SaddleItem");
        this.registerEntityItemSingle("EntityHorseZombie", "SaddleItem");
        this.registerEntityItemSingle("EntityItem", "Item");
        this.registerEntityItemSingle("EntityItemFrame", "Item");
        this.registerEntityItemSingle("EntityPotion", "Potion");
        this.registerInspector(LegacyType.BLOCK_ENTITY, new DataInspectorItem("TileEntityRecordPlayer", "RecordItem"));
        this.registerInspector(LegacyType.BLOCK_ENTITY, new DataInspectorItemList("TileEntityBrewingStand", "Items"));
        this.registerInspector(LegacyType.BLOCK_ENTITY, new DataInspectorItemList("TileEntityChest", "Items"));
        this.registerInspector(LegacyType.BLOCK_ENTITY, new DataInspectorItemList("TileEntityDispenser", "Items"));
        this.registerInspector(LegacyType.BLOCK_ENTITY, new DataInspectorItemList("TileEntityDropper", "Items"));
        this.registerInspector(LegacyType.BLOCK_ENTITY, new DataInspectorItemList("TileEntityFurnace", "Items"));
        this.registerInspector(LegacyType.BLOCK_ENTITY, new DataInspectorItemList("TileEntityHopper", "Items"));
        this.registerInspector(LegacyType.BLOCK_ENTITY, new DataInspectorItemList("TileEntityShulkerBox", "Items"));
        this.registerInspector(LegacyType.BLOCK_ENTITY, new DataInspectorMobSpawnerMobs());
        this.registerInspector(LegacyType.CHUNK, new DataInspectorChunks());
        this.registerInspector(LegacyType.ENTITY, new DataInspectorCommandBlock());
        this.registerInspector(LegacyType.ENTITY, new DataInspectorEntityPassengers());
        this.registerInspector(LegacyType.ENTITY, new DataInspectorMobSpawnerMinecart());
        this.registerInspector(LegacyType.ENTITY, new DataInspectorVillagers());
        this.registerInspector(LegacyType.ITEM_INSTANCE, new DataInspectorBlockEntity());
        this.registerInspector(LegacyType.ITEM_INSTANCE, new DataInspectorEntity());
        this.registerInspector(LegacyType.LEVEL, new DataInspectorLevelPlayer());
        this.registerInspector(LegacyType.PLAYER, new DataInspectorPlayer());
        this.registerInspector(LegacyType.PLAYER, new DataInspectorPlayerVehicle());
        this.registerInspector(LegacyType.STRUCTURE, new DataInspectorStructure());
    }

    private void registerConverters() {
        this.registerConverter(LegacyType.ENTITY, new DataConverterEquipment());
        this.registerConverter(LegacyType.BLOCK_ENTITY, new DataConverterSignText());
        this.registerConverter(LegacyType.ITEM_INSTANCE, new DataConverterMaterialId());
        this.registerConverter(LegacyType.ITEM_INSTANCE, new DataConverterPotionId());
        this.registerConverter(LegacyType.ITEM_INSTANCE, new DataConverterSpawnEgg());
        this.registerConverter(LegacyType.ENTITY, new DataConverterMinecart());
        this.registerConverter(LegacyType.BLOCK_ENTITY, new DataConverterMobSpawner());
        this.registerConverter(LegacyType.ENTITY, new DataConverterUUID());
        this.registerConverter(LegacyType.ENTITY, new DataConverterHealth());
        this.registerConverter(LegacyType.ENTITY, new DataConverterSaddle());
        this.registerConverter(LegacyType.ENTITY, new DataConverterHanging());
        this.registerConverter(LegacyType.ENTITY, new DataConverterDropChances());
        this.registerConverter(LegacyType.ENTITY, new DataConverterRiding());
        this.registerConverter(LegacyType.ENTITY, new DataConverterArmorStand());
        this.registerConverter(LegacyType.ITEM_INSTANCE, new DataConverterBook());
        this.registerConverter(LegacyType.ITEM_INSTANCE, new DataConverterCookedFish());
        this.registerConverter(LegacyType.ENTITY, new DataConverterZombie());
        this.registerConverter(LegacyType.OPTIONS, new DataConverterVBO());
        this.registerConverter(LegacyType.ENTITY, new DataConverterGuardian());
        this.registerConverter(LegacyType.ENTITY, new DataConverterSkeleton());
        this.registerConverter(LegacyType.ENTITY, new DataConverterZombieType());
        this.registerConverter(LegacyType.ENTITY, new DataConverterHorse());
        this.registerConverter(LegacyType.BLOCK_ENTITY, new DataConverterTileEntity());
        this.registerConverter(LegacyType.ENTITY, new DataConverterEntity());
        this.registerConverter(LegacyType.ITEM_INSTANCE, new DataConverterBanner());
        this.registerConverter(LegacyType.ITEM_INSTANCE, new DataConverterPotionWater());
        this.registerConverter(LegacyType.ENTITY, new DataConverterShulker());
        this.registerConverter(LegacyType.ITEM_INSTANCE, new DataConverterShulkerBoxItem());
        this.registerConverter(LegacyType.BLOCK_ENTITY, new DataConverterShulkerBoxBlock());
        this.registerConverter(LegacyType.OPTIONS, new DataConverterLang());
        this.registerConverter(LegacyType.ITEM_INSTANCE, new DataConverterTotem());
        this.registerConverter(LegacyType.CHUNK, new DataConverterBedBlock());
        this.registerConverter(LegacyType.ITEM_INSTANCE, new DataConverterBedItem());
    }

    private void registerEntityItemList(String type, String ... keys) {
        this.registerInspector(LegacyType.ENTITY, new DataInspectorItemList(type, keys));
    }

    private void registerEntityItemSingle(String type, String key) {
        this.registerInspector(LegacyType.ENTITY, new DataInspectorItem(type, key));
    }

    private void registerEntityItemListEquipment(String type) {
        this.registerEntityItemList(type, "ArmorItems", "HandItems");
    }

    private static class_2960 getKey(String type) {
        class_2960 key = OLD_ID_TO_KEY_MAP.get(type);
        if (key == null) {
            throw new IllegalArgumentException("Unknown mapping for " + type);
        }
        return key;
    }

    private static void convertCompound(LegacyType type, class_2487 cmp, String key, int sourceVer, int targetVer) {
        cmp.method_10566(key, (class_2520)FabricDataFixer.convert(type, cmp.method_10562(key), sourceVer, targetVer));
    }

    private static void convertItem(class_2487 nbttagcompound, String key, int sourceVer, int targetVer) {
        if (nbttagcompound.method_10573(key, 10)) {
            FabricDataFixer.convertCompound(LegacyType.ITEM_INSTANCE, nbttagcompound, key, sourceVer, targetVer);
        }
    }

    private static void convertItems(class_2487 nbttagcompound, String key, int sourceVer, int targetVer) {
        if (nbttagcompound.method_10573(key, 9)) {
            class_2499 nbttaglist = nbttagcompound.method_10554(key, 10);
            for (int j = 0; j < nbttaglist.size(); ++j) {
                nbttaglist.method_10531(j, (class_2520)FabricDataFixer.convert(LegacyType.ITEM_INSTANCE, nbttaglist.method_10602(j), sourceVer, targetVer));
            }
        }
    }

    static {
        DFU_TO_LEGACY = new HashMap<String, LegacyType>();
        Map<String, class_2960> map = OLD_ID_TO_KEY_MAP = new HashMap<String, class_2960>();
        map.put("EntityItem", class_2960.method_60654((String)"item"));
        map.put("EntityExperienceOrb", class_2960.method_60654((String)"xp_orb"));
        map.put("EntityAreaEffectCloud", class_2960.method_60654((String)"area_effect_cloud"));
        map.put("EntityGuardianElder", class_2960.method_60654((String)"elder_guardian"));
        map.put("EntitySkeletonWither", class_2960.method_60654((String)"wither_skeleton"));
        map.put("EntitySkeletonStray", class_2960.method_60654((String)"stray"));
        map.put("EntityEgg", class_2960.method_60654((String)"egg"));
        map.put("EntityLeash", class_2960.method_60654((String)"leash_knot"));
        map.put("EntityPainting", class_2960.method_60654((String)"painting"));
        map.put("EntityTippedArrow", class_2960.method_60654((String)"arrow"));
        map.put("EntitySnowball", class_2960.method_60654((String)"snowball"));
        map.put("EntityLargeFireball", class_2960.method_60654((String)"fireball"));
        map.put("EntitySmallFireball", class_2960.method_60654((String)"small_fireball"));
        map.put("EntityEnderPearl", class_2960.method_60654((String)"ender_pearl"));
        map.put("EntityEnderSignal", class_2960.method_60654((String)"eye_of_ender_signal"));
        map.put("EntityPotion", class_2960.method_60654((String)"potion"));
        map.put("EntityThrownExpBottle", class_2960.method_60654((String)"xp_bottle"));
        map.put("EntityItemFrame", class_2960.method_60654((String)"item_frame"));
        map.put("EntityWitherSkull", class_2960.method_60654((String)"wither_skull"));
        map.put("EntityTNTPrimed", class_2960.method_60654((String)"tnt"));
        map.put("EntityFallingBlock", class_2960.method_60654((String)"falling_block"));
        map.put("EntityFireworks", class_2960.method_60654((String)"fireworks_rocket"));
        map.put("EntityZombieHusk", class_2960.method_60654((String)"husk"));
        map.put("EntitySpectralArrow", class_2960.method_60654((String)"spectral_arrow"));
        map.put("EntityShulkerBullet", class_2960.method_60654((String)"shulker_bullet"));
        map.put("EntityDragonFireball", class_2960.method_60654((String)"dragon_fireball"));
        map.put("EntityZombieVillager", class_2960.method_60654((String)"zombie_villager"));
        map.put("EntityHorseSkeleton", class_2960.method_60654((String)"skeleton_horse"));
        map.put("EntityHorseZombie", class_2960.method_60654((String)"zombie_horse"));
        map.put("EntityArmorStand", class_2960.method_60654((String)"armor_stand"));
        map.put("EntityHorseDonkey", class_2960.method_60654((String)"donkey"));
        map.put("EntityHorseMule", class_2960.method_60654((String)"mule"));
        map.put("EntityEvokerFangs", class_2960.method_60654((String)"evocation_fangs"));
        map.put("EntityEvoker", class_2960.method_60654((String)"evocation_illager"));
        map.put("EntityVex", class_2960.method_60654((String)"vex"));
        map.put("EntityVindicator", class_2960.method_60654((String)"vindication_illager"));
        map.put("EntityIllagerIllusioner", class_2960.method_60654((String)"illusion_illager"));
        map.put("EntityMinecartCommandBlock", class_2960.method_60654((String)"commandblock_minecart"));
        map.put("EntityBoat", class_2960.method_60654((String)"boat"));
        map.put("EntityMinecartRideable", class_2960.method_60654((String)"minecart"));
        map.put("EntityMinecartChest", class_2960.method_60654((String)"chest_minecart"));
        map.put("EntityMinecartFurnace", class_2960.method_60654((String)"furnace_minecart"));
        map.put("EntityMinecartTNT", class_2960.method_60654((String)"tnt_minecart"));
        map.put("EntityMinecartHopper", class_2960.method_60654((String)"hopper_minecart"));
        map.put("EntityMinecartMobSpawner", class_2960.method_60654((String)"spawner_minecart"));
        map.put("EntityCreeper", class_2960.method_60654((String)"creeper"));
        map.put("EntitySkeleton", class_2960.method_60654((String)"skeleton"));
        map.put("EntitySpider", class_2960.method_60654((String)"spider"));
        map.put("EntityGiantZombie", class_2960.method_60654((String)"giant"));
        map.put("EntityZombie", class_2960.method_60654((String)"zombie"));
        map.put("EntitySlime", class_2960.method_60654((String)"slime"));
        map.put("EntityGhast", class_2960.method_60654((String)"ghast"));
        map.put("EntityPigZombie", class_2960.method_60654((String)"zombie_pigman"));
        map.put("EntityEnderman", class_2960.method_60654((String)"enderman"));
        map.put("EntityCaveSpider", class_2960.method_60654((String)"cave_spider"));
        map.put("EntitySilverfish", class_2960.method_60654((String)"silverfish"));
        map.put("EntityBlaze", class_2960.method_60654((String)"blaze"));
        map.put("EntityMagmaCube", class_2960.method_60654((String)"magma_cube"));
        map.put("EntityEnderDragon", class_2960.method_60654((String)"ender_dragon"));
        map.put("EntityWither", class_2960.method_60654((String)"wither"));
        map.put("EntityBat", class_2960.method_60654((String)"bat"));
        map.put("EntityWitch", class_2960.method_60654((String)"witch"));
        map.put("EntityEndermite", class_2960.method_60654((String)"endermite"));
        map.put("EntityGuardian", class_2960.method_60654((String)"guardian"));
        map.put("EntityShulker", class_2960.method_60654((String)"shulker"));
        map.put("EntityPig", class_2960.method_60654((String)"pig"));
        map.put("EntitySheep", class_2960.method_60654((String)"sheep"));
        map.put("EntityCow", class_2960.method_60654((String)"cow"));
        map.put("EntityChicken", class_2960.method_60654((String)"chicken"));
        map.put("EntitySquid", class_2960.method_60654((String)"squid"));
        map.put("EntityWolf", class_2960.method_60654((String)"wolf"));
        map.put("EntityMushroomCow", class_2960.method_60654((String)"mooshroom"));
        map.put("EntitySnowman", class_2960.method_60654((String)"snowman"));
        map.put("EntityOcelot", class_2960.method_60654((String)"ocelot"));
        map.put("EntityIronGolem", class_2960.method_60654((String)"villager_golem"));
        map.put("EntityHorse", class_2960.method_60654((String)"horse"));
        map.put("EntityRabbit", class_2960.method_60654((String)"rabbit"));
        map.put("EntityPolarBear", class_2960.method_60654((String)"polar_bear"));
        map.put("EntityLlama", class_2960.method_60654((String)"llama"));
        map.put("EntityLlamaSpit", class_2960.method_60654((String)"llama_spit"));
        map.put("EntityParrot", class_2960.method_60654((String)"parrot"));
        map.put("EntityVillager", class_2960.method_60654((String)"villager"));
        map.put("EntityEnderCrystal", class_2960.method_60654((String)"ender_crystal"));
        map.put("TileEntityFurnace", class_2960.method_60654((String)"furnace"));
        map.put("TileEntityChest", class_2960.method_60654((String)"chest"));
        map.put("TileEntityEnderChest", class_2960.method_60654((String)"ender_chest"));
        map.put("TileEntityRecordPlayer", class_2960.method_60654((String)"jukebox"));
        map.put("TileEntityDispenser", class_2960.method_60654((String)"dispenser"));
        map.put("TileEntityDropper", class_2960.method_60654((String)"dropper"));
        map.put("TileEntitySign", class_2960.method_60654((String)"sign"));
        map.put("TileEntityMobSpawner", class_2960.method_60654((String)"mob_spawner"));
        map.put("TileEntityNote", class_2960.method_60654((String)"noteblock"));
        map.put("TileEntityPiston", class_2960.method_60654((String)"piston"));
        map.put("TileEntityBrewingStand", class_2960.method_60654((String)"brewing_stand"));
        map.put("TileEntityEnchantTable", class_2960.method_60654((String)"enchanting_table"));
        map.put("TileEntityEnderPortal", class_2960.method_60654((String)"end_portal"));
        map.put("TileEntityBeacon", class_2960.method_60654((String)"beacon"));
        map.put("TileEntitySkull", class_2960.method_60654((String)"skull"));
        map.put("TileEntityLightDetector", class_2960.method_60654((String)"daylight_detector"));
        map.put("TileEntityHopper", class_2960.method_60654((String)"hopper"));
        map.put("TileEntityComparator", class_2960.method_60654((String)"comparator"));
        map.put("TileEntityFlowerPot", class_2960.method_60654((String)"flower_pot"));
        map.put("TileEntityBanner", class_2960.method_60654((String)"banner"));
        map.put("TileEntityStructure", class_2960.method_60654((String)"structure_block"));
        map.put("TileEntityEndGateway", class_2960.method_60654((String)"end_gateway"));
        map.put("TileEntityCommand", class_2960.method_60654((String)"command_block"));
        map.put("TileEntityShulkerBox", class_2960.method_60654((String)"shulker_box"));
        map.put("TileEntityBed", class_2960.method_60654((String)"bed"));
    }

    public static enum LegacyType {
        LEVEL(class_1208.field_5710),
        PLAYER(class_1208.field_5715),
        CHUNK(class_1208.field_5726),
        BLOCK_ENTITY(class_1208.field_5727),
        ENTITY(class_1208.field_5729),
        ITEM_INSTANCE(class_1208.field_5712),
        OPTIONS(class_1208.field_5717),
        STRUCTURE(class_1208.field_5716);

        private final DSL.TypeReference type;

        private LegacyType(DSL.TypeReference type) {
            this.type = type;
            DFU_TO_LEGACY.put(type.typeName(), this);
        }

        public DSL.TypeReference getDFUType() {
            return this.type;
        }
    }

    private class WrappedDataFixer
    implements DataFixer {
        private final DataFixer realFixer;

        WrappedDataFixer(DataFixer realFixer) {
            this.realFixer = realFixer;
        }

        public <T> Dynamic<T> update(DSL.TypeReference type, Dynamic<T> dynamic, int sourceVer, int targetVer) {
            LegacyType legacyType = DFU_TO_LEGACY.get(type.typeName());
            if (sourceVer < 1343 && legacyType != null) {
                class_2487 cmp = (class_2487)dynamic.getValue();
                int desiredVersion = Math.min(targetVer, 1343);
                cmp = this.convert(legacyType, cmp, sourceVer, desiredVersion);
                sourceVer = desiredVersion;
                dynamic = new Dynamic((DynamicOps)OPS_NBT, (Object)cmp);
            }
            return this.realFixer.update(type, dynamic, sourceVer, targetVer);
        }

        private class_2487 convert(LegacyType type, class_2487 cmp, int sourceVer, int desiredVersion) {
            List<DataInspector> inspectors;
            List<DataConverter> converters = FabricDataFixer.this.converters.get((Object)type);
            if (converters != null && !converters.isEmpty()) {
                for (DataConverter converter : converters) {
                    int dataVersion = converter.getDataVersion();
                    if (dataVersion <= sourceVer || dataVersion > desiredVersion) continue;
                    cmp = converter.convert(cmp);
                }
            }
            if ((inspectors = FabricDataFixer.this.inspectors.get((Object)type)) != null && !inspectors.isEmpty()) {
                for (DataInspector inspector : inspectors) {
                    cmp = inspector.inspect(cmp, sourceVer, desiredVersion);
                }
            }
            return cmp;
        }

        public Schema getSchema(int i) {
            return this.realFixer.getSchema(i);
        }
    }

    public static interface DataConverter {
        public int getDataVersion();

        public class_2487 convert(class_2487 var1);
    }

    private static class DataInspectorItem
    extends DataInspectorTagged {
        private final String[] keys;

        DataInspectorItem(String oclass, String ... astring) {
            super(oclass);
            this.keys = astring;
        }

        @Override
        class_2487 inspectChecked(class_2487 nbttagcompound, int sourceVer, int targetVer) {
            for (String key : this.keys) {
                FabricDataFixer.convertItem(nbttagcompound, key, sourceVer, targetVer);
            }
            return nbttagcompound;
        }
    }

    public static interface DataInspector {
        public class_2487 inspect(class_2487 var1, int var2, int var3);
    }

    private static class DataInspectorItemList
    extends DataInspectorTagged {
        private final String[] keys;

        DataInspectorItemList(String oclass, String ... astring) {
            super(oclass);
            this.keys = astring;
        }

        @Override
        class_2487 inspectChecked(class_2487 nbttagcompound, int sourceVer, int targetVer) {
            for (String s : this.keys) {
                FabricDataFixer.convertItems(nbttagcompound, s, sourceVer, targetVer);
            }
            return nbttagcompound;
        }
    }

    private static class DataInspectorMobSpawnerMobs
    implements DataInspector {
        class_2960 tileEntityMobSpawner = FabricDataFixer.getKey("TileEntityMobSpawner");

        private DataInspectorMobSpawnerMobs() {
        }

        @Override
        public class_2487 inspect(class_2487 cmp, int sourceVer, int targetVer) {
            if (this.tileEntityMobSpawner.equals((Object)class_2960.method_60654((String)cmp.method_10558("id")))) {
                if (cmp.method_10573("SpawnPotentials", 9)) {
                    class_2499 nbttaglist = cmp.method_10554("SpawnPotentials", 10);
                    for (int j = 0; j < nbttaglist.size(); ++j) {
                        class_2487 nbttagcompound1 = nbttaglist.method_10602(j);
                        FabricDataFixer.convertCompound(LegacyType.ENTITY, nbttagcompound1, "Entity", sourceVer, targetVer);
                    }
                }
                FabricDataFixer.convertCompound(LegacyType.ENTITY, cmp, "SpawnData", sourceVer, targetVer);
            }
            return cmp;
        }
    }

    private static class DataInspectorChunks
    implements DataInspector {
        private DataInspectorChunks() {
        }

        @Override
        public class_2487 inspect(class_2487 cmp, int sourceVer, int targetVer) {
            if (cmp.method_10573("Level", 10)) {
                int j;
                class_2499 nbttaglist;
                class_2487 nbttagcompound1 = cmp.method_10562("Level");
                if (nbttagcompound1.method_10573("Entities", 9)) {
                    nbttaglist = nbttagcompound1.method_10554("Entities", 10);
                    for (j = 0; j < nbttaglist.size(); ++j) {
                        nbttaglist.method_10606(j, (class_2520)FabricDataFixer.convert(LegacyType.ENTITY, (class_2487)nbttaglist.method_10534(j), sourceVer, targetVer));
                    }
                }
                if (nbttagcompound1.method_10573("TileEntities", 9)) {
                    nbttaglist = nbttagcompound1.method_10554("TileEntities", 10);
                    for (j = 0; j < nbttaglist.size(); ++j) {
                        nbttaglist.method_10606(j, (class_2520)FabricDataFixer.convert(LegacyType.BLOCK_ENTITY, (class_2487)nbttaglist.method_10534(j), sourceVer, targetVer));
                    }
                }
            }
            return cmp;
        }
    }

    private static class DataInspectorCommandBlock
    implements DataInspector {
        class_2960 tileEntityCommand = FabricDataFixer.getKey("TileEntityCommand");

        private DataInspectorCommandBlock() {
        }

        @Override
        public class_2487 inspect(class_2487 cmp, int sourceVer, int targetVer) {
            if (this.tileEntityCommand.equals((Object)class_2960.method_60654((String)cmp.method_10558("id")))) {
                cmp.method_10582("id", "Control");
                FabricDataFixer.convert(LegacyType.BLOCK_ENTITY, cmp, sourceVer, targetVer);
                cmp.method_10582("id", "MinecartCommandBlock");
            }
            return cmp;
        }
    }

    private static class DataInspectorEntityPassengers
    implements DataInspector {
        private DataInspectorEntityPassengers() {
        }

        @Override
        public class_2487 inspect(class_2487 cmp, int sourceVer, int targetVer) {
            if (cmp.method_10573("Passengers", 9)) {
                class_2499 nbttaglist = cmp.method_10554("Passengers", 10);
                for (int j = 0; j < nbttaglist.size(); ++j) {
                    nbttaglist.method_10606(j, (class_2520)FabricDataFixer.convert(LegacyType.ENTITY, nbttaglist.method_10602(j), sourceVer, targetVer));
                }
            }
            return cmp;
        }
    }

    private static class DataInspectorMobSpawnerMinecart
    implements DataInspector {
        class_2960 entityMinecartMobSpawner = FabricDataFixer.getKey("EntityMinecartMobSpawner");
        class_2960 tileEntityMobSpawner = FabricDataFixer.getKey("TileEntityMobSpawner");

        private DataInspectorMobSpawnerMinecart() {
        }

        @Override
        public class_2487 inspect(class_2487 cmp, int sourceVer, int targetVer) {
            String s = cmp.method_10558("id");
            if (this.entityMinecartMobSpawner.equals((Object)class_2960.method_60654((String)s))) {
                cmp.method_10582("id", this.tileEntityMobSpawner.toString());
                FabricDataFixer.convert(LegacyType.BLOCK_ENTITY, cmp, sourceVer, targetVer);
                cmp.method_10582("id", s);
            }
            return cmp;
        }
    }

    private static class DataInspectorVillagers
    implements DataInspector {
        class_2960 entityVillager = FabricDataFixer.getKey("EntityVillager");

        private DataInspectorVillagers() {
        }

        @Override
        public class_2487 inspect(class_2487 cmp, int sourceVer, int targetVer) {
            class_2487 nbttagcompound1;
            if (this.entityVillager.equals((Object)class_2960.method_60654((String)cmp.method_10558("id"))) && cmp.method_10573("Offers", 10) && (nbttagcompound1 = cmp.method_10562("Offers")).method_10573("Recipes", 9)) {
                class_2499 nbttaglist = nbttagcompound1.method_10554("Recipes", 10);
                for (int j = 0; j < nbttaglist.size(); ++j) {
                    class_2487 nbttagcompound2 = nbttaglist.method_10602(j);
                    FabricDataFixer.convertItem(nbttagcompound2, "buy", sourceVer, targetVer);
                    FabricDataFixer.convertItem(nbttagcompound2, "buyB", sourceVer, targetVer);
                    FabricDataFixer.convertItem(nbttagcompound2, "sell", sourceVer, targetVer);
                    nbttaglist.method_10606(j, (class_2520)nbttagcompound2);
                }
            }
            return cmp;
        }
    }

    private static class DataInspectorBlockEntity
    implements DataInspector {
        private static final Map<String, String> b = Maps.newHashMap();
        private static final Map<String, String> c = Maps.newHashMap();

        DataInspectorBlockEntity() {
        }

        @Nullable
        private static String convertEntityId(int i, String s) {
            String key = class_2960.method_60654((String)s).toString();
            if (i < 515 && b.containsKey(key)) {
                return b.get(key);
            }
            return c.get(key);
        }

        @Override
        public class_2487 inspect(class_2487 cmp, int sourceVer, int targetVer) {
            if (!cmp.method_10573("tag", 10)) {
                return cmp;
            }
            class_2487 nbttagcompound1 = cmp.method_10562("tag");
            if (nbttagcompound1.method_10573("BlockEntityTag", 10)) {
                boolean flag;
                class_2487 nbttagcompound2 = nbttagcompound1.method_10562("BlockEntityTag");
                String s = cmp.method_10558("id");
                String s1 = DataInspectorBlockEntity.convertEntityId(sourceVer, s);
                if (s1 == null) {
                    flag = false;
                } else {
                    flag = !nbttagcompound2.method_10545("id");
                    nbttagcompound2.method_10582("id", s1);
                }
                FabricDataFixer.convert(LegacyType.BLOCK_ENTITY, nbttagcompound2, sourceVer, targetVer);
                if (flag) {
                    nbttagcompound2.method_10551("id");
                }
            }
            return cmp;
        }

        static {
            Map<String, String> map = b;
            map.put("minecraft:furnace", "Furnace");
            map.put("minecraft:lit_furnace", "Furnace");
            map.put("minecraft:chest", "Chest");
            map.put("minecraft:trapped_chest", "Chest");
            map.put("minecraft:ender_chest", "EnderChest");
            map.put("minecraft:jukebox", "RecordPlayer");
            map.put("minecraft:dispenser", "Trap");
            map.put("minecraft:dropper", "Dropper");
            map.put("minecraft:sign", "Sign");
            map.put("minecraft:mob_spawner", "MobSpawner");
            map.put("minecraft:noteblock", "Music");
            map.put("minecraft:brewing_stand", "Cauldron");
            map.put("minecraft:enhanting_table", "EnchantTable");
            map.put("minecraft:command_block", "CommandBlock");
            map.put("minecraft:beacon", "Beacon");
            map.put("minecraft:skull", "Skull");
            map.put("minecraft:daylight_detector", "DLDetector");
            map.put("minecraft:hopper", "Hopper");
            map.put("minecraft:banner", "Banner");
            map.put("minecraft:flower_pot", "FlowerPot");
            map.put("minecraft:repeating_command_block", "CommandBlock");
            map.put("minecraft:chain_command_block", "CommandBlock");
            map.put("minecraft:standing_sign", "Sign");
            map.put("minecraft:wall_sign", "Sign");
            map.put("minecraft:piston_head", "Piston");
            map.put("minecraft:daylight_detector_inverted", "DLDetector");
            map.put("minecraft:unpowered_comparator", "Comparator");
            map.put("minecraft:powered_comparator", "Comparator");
            map.put("minecraft:wall_banner", "Banner");
            map.put("minecraft:standing_banner", "Banner");
            map.put("minecraft:structure_block", "Structure");
            map.put("minecraft:end_portal", "Airportal");
            map.put("minecraft:end_gateway", "EndGateway");
            map.put("minecraft:shield", "Shield");
            map = c;
            map.put("minecraft:furnace", "minecraft:furnace");
            map.put("minecraft:lit_furnace", "minecraft:furnace");
            map.put("minecraft:chest", "minecraft:chest");
            map.put("minecraft:trapped_chest", "minecraft:chest");
            map.put("minecraft:ender_chest", "minecraft:enderchest");
            map.put("minecraft:jukebox", "minecraft:jukebox");
            map.put("minecraft:dispenser", "minecraft:dispenser");
            map.put("minecraft:dropper", "minecraft:dropper");
            map.put("minecraft:sign", "minecraft:sign");
            map.put("minecraft:mob_spawner", "minecraft:mob_spawner");
            map.put("minecraft:noteblock", "minecraft:noteblock");
            map.put("minecraft:brewing_stand", "minecraft:brewing_stand");
            map.put("minecraft:enhanting_table", "minecraft:enchanting_table");
            map.put("minecraft:command_block", "minecraft:command_block");
            map.put("minecraft:beacon", "minecraft:beacon");
            map.put("minecraft:skull", "minecraft:skull");
            map.put("minecraft:daylight_detector", "minecraft:daylight_detector");
            map.put("minecraft:hopper", "minecraft:hopper");
            map.put("minecraft:banner", "minecraft:banner");
            map.put("minecraft:flower_pot", "minecraft:flower_pot");
            map.put("minecraft:repeating_command_block", "minecraft:command_block");
            map.put("minecraft:chain_command_block", "minecraft:command_block");
            map.put("minecraft:shulker_box", "minecraft:shulker_box");
            map.put("minecraft:white_shulker_box", "minecraft:shulker_box");
            map.put("minecraft:orange_shulker_box", "minecraft:shulker_box");
            map.put("minecraft:magenta_shulker_box", "minecraft:shulker_box");
            map.put("minecraft:light_blue_shulker_box", "minecraft:shulker_box");
            map.put("minecraft:yellow_shulker_box", "minecraft:shulker_box");
            map.put("minecraft:lime_shulker_box", "minecraft:shulker_box");
            map.put("minecraft:pink_shulker_box", "minecraft:shulker_box");
            map.put("minecraft:gray_shulker_box", "minecraft:shulker_box");
            map.put("minecraft:silver_shulker_box", "minecraft:shulker_box");
            map.put("minecraft:cyan_shulker_box", "minecraft:shulker_box");
            map.put("minecraft:purple_shulker_box", "minecraft:shulker_box");
            map.put("minecraft:blue_shulker_box", "minecraft:shulker_box");
            map.put("minecraft:brown_shulker_box", "minecraft:shulker_box");
            map.put("minecraft:green_shulker_box", "minecraft:shulker_box");
            map.put("minecraft:red_shulker_box", "minecraft:shulker_box");
            map.put("minecraft:black_shulker_box", "minecraft:shulker_box");
            map.put("minecraft:bed", "minecraft:bed");
            map.put("minecraft:standing_sign", "minecraft:sign");
            map.put("minecraft:wall_sign", "minecraft:sign");
            map.put("minecraft:piston_head", "minecraft:piston");
            map.put("minecraft:daylight_detector_inverted", "minecraft:daylight_detector");
            map.put("minecraft:unpowered_comparator", "minecraft:comparator");
            map.put("minecraft:powered_comparator", "minecraft:comparator");
            map.put("minecraft:wall_banner", "minecraft:banner");
            map.put("minecraft:standing_banner", "minecraft:banner");
            map.put("minecraft:structure_block", "minecraft:structure_block");
            map.put("minecraft:end_portal", "minecraft:end_portal");
            map.put("minecraft:end_gateway", "minecraft:end_gateway");
            map.put("minecraft:shield", "minecraft:shield");
        }
    }

    private static class DataInspectorEntity
    implements DataInspector {
        private static final Logger a = LogManager.getLogger(FabricDataFixer.class);

        DataInspectorEntity() {
        }

        @Override
        public class_2487 inspect(class_2487 cmp, int sourceVer, int targetVer) {
            class_2487 nbttagcompound1 = cmp.method_10562("tag");
            if (nbttagcompound1.method_10573("EntityTag", 10)) {
                boolean flag;
                String s1;
                class_2487 nbttagcompound2 = nbttagcompound1.method_10562("EntityTag");
                String s = cmp.method_10558("id");
                if ("minecraft:armor_stand".equals(s)) {
                    s1 = sourceVer < 515 ? "ArmorStand" : "minecraft:armor_stand";
                } else {
                    if (!"minecraft:spawn_egg".equals(s)) {
                        return cmp;
                    }
                    s1 = nbttagcompound2.method_10558("id");
                }
                if (s1 == null) {
                    a.warn("Unable to resolve Entity for ItemInstance: {}", (Object)s);
                    flag = false;
                } else {
                    flag = !nbttagcompound2.method_10573("id", 8);
                    nbttagcompound2.method_10582("id", s1);
                }
                FabricDataFixer.convert(LegacyType.ENTITY, nbttagcompound2, sourceVer, targetVer);
                if (flag) {
                    nbttagcompound2.method_10551("id");
                }
            }
            return cmp;
        }
    }

    private static class DataInspectorLevelPlayer
    implements DataInspector {
        private DataInspectorLevelPlayer() {
        }

        @Override
        public class_2487 inspect(class_2487 cmp, int sourceVer, int targetVer) {
            if (cmp.method_10573("Player", 10)) {
                FabricDataFixer.convertCompound(LegacyType.PLAYER, cmp, "Player", sourceVer, targetVer);
            }
            return cmp;
        }
    }

    private static class DataInspectorPlayer
    implements DataInspector {
        private DataInspectorPlayer() {
        }

        @Override
        public class_2487 inspect(class_2487 cmp, int sourceVer, int targetVer) {
            FabricDataFixer.convertItems(cmp, "Inventory", sourceVer, targetVer);
            FabricDataFixer.convertItems(cmp, "EnderItems", sourceVer, targetVer);
            if (cmp.method_10573("ShoulderEntityLeft", 10)) {
                FabricDataFixer.convertCompound(LegacyType.ENTITY, cmp, "ShoulderEntityLeft", sourceVer, targetVer);
            }
            if (cmp.method_10573("ShoulderEntityRight", 10)) {
                FabricDataFixer.convertCompound(LegacyType.ENTITY, cmp, "ShoulderEntityRight", sourceVer, targetVer);
            }
            return cmp;
        }
    }

    private static class DataInspectorPlayerVehicle
    implements DataInspector {
        private DataInspectorPlayerVehicle() {
        }

        @Override
        public class_2487 inspect(class_2487 cmp, int sourceVer, int targetVer) {
            class_2487 nbttagcompound1;
            if (cmp.method_10573("RootVehicle", 10) && (nbttagcompound1 = cmp.method_10562("RootVehicle")).method_10573("Entity", 10)) {
                FabricDataFixer.convertCompound(LegacyType.ENTITY, nbttagcompound1, "Entity", sourceVer, targetVer);
            }
            return cmp;
        }
    }

    private static class DataInspectorStructure
    implements DataInspector {
        private DataInspectorStructure() {
        }

        @Override
        public class_2487 inspect(class_2487 cmp, int sourceVer, int targetVer) {
            class_2487 nbttagcompound1;
            int j;
            class_2499 nbttaglist;
            if (cmp.method_10573("entities", 9)) {
                nbttaglist = cmp.method_10554("entities", 10);
                for (j = 0; j < nbttaglist.size(); ++j) {
                    nbttagcompound1 = (class_2487)nbttaglist.method_10534(j);
                    if (!nbttagcompound1.method_10573("nbt", 10)) continue;
                    FabricDataFixer.convertCompound(LegacyType.ENTITY, nbttagcompound1, "nbt", sourceVer, targetVer);
                }
            }
            if (cmp.method_10573("blocks", 9)) {
                nbttaglist = cmp.method_10554("blocks", 10);
                for (j = 0; j < nbttaglist.size(); ++j) {
                    nbttagcompound1 = (class_2487)nbttaglist.method_10534(j);
                    if (!nbttagcompound1.method_10573("nbt", 10)) continue;
                    FabricDataFixer.convertCompound(LegacyType.BLOCK_ENTITY, nbttagcompound1, "nbt", sourceVer, targetVer);
                }
            }
            return cmp;
        }
    }

    private static class DataConverterEquipment
    implements DataConverter {
        DataConverterEquipment() {
        }

        @Override
        public int getDataVersion() {
            return 100;
        }

        @Override
        public class_2487 convert(class_2487 cmp) {
            class_2499 nbttaglist1;
            class_2499 nbttaglist = cmp.method_10554("Equipment", 10);
            if (!nbttaglist.isEmpty() && !cmp.method_10573("HandItems", 10)) {
                nbttaglist1 = new class_2499();
                nbttaglist1.add((Object)nbttaglist.method_10534(0));
                nbttaglist1.add((Object)new class_2487());
                cmp.method_10566("HandItems", (class_2520)nbttaglist1);
            }
            if (nbttaglist.size() > 1 && !cmp.method_10573("ArmorItem", 10)) {
                nbttaglist1 = new class_2499();
                nbttaglist1.add((Object)nbttaglist.method_10534(1));
                nbttaglist1.add((Object)nbttaglist.method_10534(2));
                nbttaglist1.add((Object)nbttaglist.method_10534(3));
                nbttaglist1.add((Object)nbttaglist.method_10534(4));
                cmp.method_10566("ArmorItems", (class_2520)nbttaglist1);
            }
            cmp.method_10551("Equipment");
            if (cmp.method_10573("DropChances", 9)) {
                class_2499 nbttaglist2;
                nbttaglist1 = cmp.method_10554("DropChances", 5);
                if (!cmp.method_10573("HandDropChances", 10)) {
                    nbttaglist2 = new class_2499();
                    nbttaglist2.add((Object)class_2494.method_23244((float)nbttaglist1.method_10604(0)));
                    nbttaglist2.add((Object)class_2494.method_23244((float)0.0f));
                    cmp.method_10566("HandDropChances", (class_2520)nbttaglist2);
                }
                if (!cmp.method_10573("ArmorDropChances", 10)) {
                    nbttaglist2 = new class_2499();
                    nbttaglist2.add((Object)class_2494.method_23244((float)nbttaglist1.method_10604(1)));
                    nbttaglist2.add((Object)class_2494.method_23244((float)nbttaglist1.method_10604(2)));
                    nbttaglist2.add((Object)class_2494.method_23244((float)nbttaglist1.method_10604(3)));
                    nbttaglist2.add((Object)class_2494.method_23244((float)nbttaglist1.method_10604(4)));
                    cmp.method_10566("ArmorDropChances", (class_2520)nbttaglist2);
                }
                cmp.method_10551("DropChances");
            }
            return cmp;
        }
    }

    private static class DataConverterSignText
    implements DataConverter {
        public static final Gson a = new GsonBuilder().registerTypeAdapter(class_2561.class, (Object)new JsonDeserializer(){

            class_5250 a(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
                if (jsonelement.isJsonPrimitive()) {
                    return class_2561.method_43470((String)jsonelement.getAsString());
                }
                if (jsonelement.isJsonArray()) {
                    JsonArray jsonarray = jsonelement.getAsJsonArray();
                    class_5250 iTextComponent = null;
                    for (JsonElement jsonelement1 : jsonarray) {
                        class_5250 iTextComponent1 = this.a(jsonelement1, jsonelement1.getClass(), jsondeserializationcontext);
                        if (iTextComponent == null) {
                            iTextComponent = iTextComponent1;
                            continue;
                        }
                        iTextComponent.method_10852((class_2561)iTextComponent1);
                    }
                    return iTextComponent;
                }
                throw new JsonParseException("Don't know how to turn " + String.valueOf(jsonelement) + " into a Component");
            }

            public Object deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
                return this.a(jsonelement, type, jsondeserializationcontext);
            }
        }).create();

        DataConverterSignText() {
        }

        @Override
        public int getDataVersion() {
            return 101;
        }

        @Override
        public class_2487 convert(class_2487 cmp) {
            if ("Sign".equals(cmp.method_10558("id"))) {
                this.convert(cmp, "Text1");
                this.convert(cmp, "Text2");
                this.convert(cmp, "Text3");
                this.convert(cmp, "Text4");
            }
            return cmp;
        }

        private void convert(class_2487 nbttagcompound, String s) {
            String s1 = nbttagcompound.method_10558(s);
            Object object = null;
            if (!"null".equals(s1) && !Strings.isNullOrEmpty((String)s1)) {
                if (!(s1.charAt(0) == '\"' && s1.charAt(s1.length() - 1) == '\"' || s1.charAt(0) == '{' && s1.charAt(s1.length() - 1) == '}')) {
                    object = class_2561.method_43470((String)s1);
                } else {
                    try {
                        object = class_3518.method_15279((Gson)a, (String)s1, class_2561.class, (boolean)true);
                        if (object == null) {
                            object = class_2561.method_43470((String)"");
                        }
                    }
                    catch (JsonParseException jsonParseException) {
                        // empty catch block
                    }
                    if (object == null) {
                        try {
                            object = class_2561.class_2562.method_10877((String)s1, (class_7225.class_7874)FabricWorldEdit.registryAccess());
                        }
                        catch (JsonParseException jsonParseException) {
                            // empty catch block
                        }
                    }
                    if (object == null) {
                        try {
                            object = class_2561.class_2562.method_10873((String)s1, (class_7225.class_7874)FabricWorldEdit.registryAccess());
                        }
                        catch (JsonParseException jsonParseException) {
                            // empty catch block
                        }
                    }
                    if (object == null) {
                        object = class_2561.method_43470((String)s1);
                    }
                }
            } else {
                object = class_2561.method_43470((String)"");
            }
            nbttagcompound.method_10582(s, class_2561.class_2562.method_10867((class_2561)((class_2561)object), (class_7225.class_7874)FabricWorldEdit.registryAccess()));
        }
    }

    private static class DataConverterMaterialId
    implements DataConverter {
        private static final String[] materials = new String[2268];

        DataConverterMaterialId() {
        }

        @Override
        public int getDataVersion() {
            return 102;
        }

        @Override
        public class_2487 convert(class_2487 cmp) {
            short short0;
            if (cmp.method_10573("id", 99) && (short0 = cmp.method_10568("id")) > 0 && short0 < materials.length && materials[short0] != null) {
                cmp.method_10582("id", materials[short0]);
            }
            return cmp;
        }

        static {
            DataConverterMaterialId.materials[1] = "minecraft:stone";
            DataConverterMaterialId.materials[2] = "minecraft:grass";
            DataConverterMaterialId.materials[3] = "minecraft:dirt";
            DataConverterMaterialId.materials[4] = "minecraft:cobblestone";
            DataConverterMaterialId.materials[5] = "minecraft:planks";
            DataConverterMaterialId.materials[6] = "minecraft:sapling";
            DataConverterMaterialId.materials[7] = "minecraft:bedrock";
            DataConverterMaterialId.materials[8] = "minecraft:flowing_water";
            DataConverterMaterialId.materials[9] = "minecraft:water";
            DataConverterMaterialId.materials[10] = "minecraft:flowing_lava";
            DataConverterMaterialId.materials[11] = "minecraft:lava";
            DataConverterMaterialId.materials[12] = "minecraft:sand";
            DataConverterMaterialId.materials[13] = "minecraft:gravel";
            DataConverterMaterialId.materials[14] = "minecraft:gold_ore";
            DataConverterMaterialId.materials[15] = "minecraft:iron_ore";
            DataConverterMaterialId.materials[16] = "minecraft:coal_ore";
            DataConverterMaterialId.materials[17] = "minecraft:log";
            DataConverterMaterialId.materials[18] = "minecraft:leaves";
            DataConverterMaterialId.materials[19] = "minecraft:sponge";
            DataConverterMaterialId.materials[20] = "minecraft:glass";
            DataConverterMaterialId.materials[21] = "minecraft:lapis_ore";
            DataConverterMaterialId.materials[22] = "minecraft:lapis_block";
            DataConverterMaterialId.materials[23] = "minecraft:dispenser";
            DataConverterMaterialId.materials[24] = "minecraft:sandstone";
            DataConverterMaterialId.materials[25] = "minecraft:noteblock";
            DataConverterMaterialId.materials[27] = "minecraft:golden_rail";
            DataConverterMaterialId.materials[28] = "minecraft:detector_rail";
            DataConverterMaterialId.materials[29] = "minecraft:sticky_piston";
            DataConverterMaterialId.materials[30] = "minecraft:web";
            DataConverterMaterialId.materials[31] = "minecraft:tallgrass";
            DataConverterMaterialId.materials[32] = "minecraft:deadbush";
            DataConverterMaterialId.materials[33] = "minecraft:piston";
            DataConverterMaterialId.materials[35] = "minecraft:wool";
            DataConverterMaterialId.materials[37] = "minecraft:yellow_flower";
            DataConverterMaterialId.materials[38] = "minecraft:red_flower";
            DataConverterMaterialId.materials[39] = "minecraft:brown_mushroom";
            DataConverterMaterialId.materials[40] = "minecraft:red_mushroom";
            DataConverterMaterialId.materials[41] = "minecraft:gold_block";
            DataConverterMaterialId.materials[42] = "minecraft:iron_block";
            DataConverterMaterialId.materials[43] = "minecraft:double_stone_slab";
            DataConverterMaterialId.materials[44] = "minecraft:stone_slab";
            DataConverterMaterialId.materials[45] = "minecraft:brick_block";
            DataConverterMaterialId.materials[46] = "minecraft:tnt";
            DataConverterMaterialId.materials[47] = "minecraft:bookshelf";
            DataConverterMaterialId.materials[48] = "minecraft:mossy_cobblestone";
            DataConverterMaterialId.materials[49] = "minecraft:obsidian";
            DataConverterMaterialId.materials[50] = "minecraft:torch";
            DataConverterMaterialId.materials[51] = "minecraft:fire";
            DataConverterMaterialId.materials[52] = "minecraft:mob_spawner";
            DataConverterMaterialId.materials[53] = "minecraft:oak_stairs";
            DataConverterMaterialId.materials[54] = "minecraft:chest";
            DataConverterMaterialId.materials[56] = "minecraft:diamond_ore";
            DataConverterMaterialId.materials[57] = "minecraft:diamond_block";
            DataConverterMaterialId.materials[58] = "minecraft:crafting_table";
            DataConverterMaterialId.materials[60] = "minecraft:farmland";
            DataConverterMaterialId.materials[61] = "minecraft:furnace";
            DataConverterMaterialId.materials[62] = "minecraft:lit_furnace";
            DataConverterMaterialId.materials[65] = "minecraft:ladder";
            DataConverterMaterialId.materials[66] = "minecraft:rail";
            DataConverterMaterialId.materials[67] = "minecraft:stone_stairs";
            DataConverterMaterialId.materials[69] = "minecraft:lever";
            DataConverterMaterialId.materials[70] = "minecraft:stone_pressure_plate";
            DataConverterMaterialId.materials[72] = "minecraft:wooden_pressure_plate";
            DataConverterMaterialId.materials[73] = "minecraft:redstone_ore";
            DataConverterMaterialId.materials[76] = "minecraft:redstone_torch";
            DataConverterMaterialId.materials[77] = "minecraft:stone_button";
            DataConverterMaterialId.materials[78] = "minecraft:snow_layer";
            DataConverterMaterialId.materials[79] = "minecraft:ice";
            DataConverterMaterialId.materials[80] = "minecraft:snow";
            DataConverterMaterialId.materials[81] = "minecraft:cactus";
            DataConverterMaterialId.materials[82] = "minecraft:clay";
            DataConverterMaterialId.materials[84] = "minecraft:jukebox";
            DataConverterMaterialId.materials[85] = "minecraft:fence";
            DataConverterMaterialId.materials[86] = "minecraft:pumpkin";
            DataConverterMaterialId.materials[87] = "minecraft:netherrack";
            DataConverterMaterialId.materials[88] = "minecraft:soul_sand";
            DataConverterMaterialId.materials[89] = "minecraft:glowstone";
            DataConverterMaterialId.materials[90] = "minecraft:portal";
            DataConverterMaterialId.materials[91] = "minecraft:lit_pumpkin";
            DataConverterMaterialId.materials[95] = "minecraft:stained_glass";
            DataConverterMaterialId.materials[96] = "minecraft:trapdoor";
            DataConverterMaterialId.materials[97] = "minecraft:monster_egg";
            DataConverterMaterialId.materials[98] = "minecraft:stonebrick";
            DataConverterMaterialId.materials[99] = "minecraft:brown_mushroom_block";
            DataConverterMaterialId.materials[100] = "minecraft:red_mushroom_block";
            DataConverterMaterialId.materials[101] = "minecraft:iron_bars";
            DataConverterMaterialId.materials[102] = "minecraft:glass_pane";
            DataConverterMaterialId.materials[103] = "minecraft:melon_block";
            DataConverterMaterialId.materials[106] = "minecraft:vine";
            DataConverterMaterialId.materials[107] = "minecraft:fence_gate";
            DataConverterMaterialId.materials[108] = "minecraft:brick_stairs";
            DataConverterMaterialId.materials[109] = "minecraft:stone_brick_stairs";
            DataConverterMaterialId.materials[110] = "minecraft:mycelium";
            DataConverterMaterialId.materials[111] = "minecraft:waterlily";
            DataConverterMaterialId.materials[112] = "minecraft:nether_brick";
            DataConverterMaterialId.materials[113] = "minecraft:nether_brick_fence";
            DataConverterMaterialId.materials[114] = "minecraft:nether_brick_stairs";
            DataConverterMaterialId.materials[116] = "minecraft:enchanting_table";
            DataConverterMaterialId.materials[119] = "minecraft:end_portal";
            DataConverterMaterialId.materials[120] = "minecraft:end_portal_frame";
            DataConverterMaterialId.materials[121] = "minecraft:end_stone";
            DataConverterMaterialId.materials[122] = "minecraft:dragon_egg";
            DataConverterMaterialId.materials[123] = "minecraft:redstone_lamp";
            DataConverterMaterialId.materials[125] = "minecraft:double_wooden_slab";
            DataConverterMaterialId.materials[126] = "minecraft:wooden_slab";
            DataConverterMaterialId.materials[127] = "minecraft:cocoa";
            DataConverterMaterialId.materials[128] = "minecraft:sandstone_stairs";
            DataConverterMaterialId.materials[129] = "minecraft:emerald_ore";
            DataConverterMaterialId.materials[130] = "minecraft:ender_chest";
            DataConverterMaterialId.materials[131] = "minecraft:tripwire_hook";
            DataConverterMaterialId.materials[133] = "minecraft:emerald_block";
            DataConverterMaterialId.materials[134] = "minecraft:spruce_stairs";
            DataConverterMaterialId.materials[135] = "minecraft:birch_stairs";
            DataConverterMaterialId.materials[136] = "minecraft:jungle_stairs";
            DataConverterMaterialId.materials[137] = "minecraft:command_block";
            DataConverterMaterialId.materials[138] = "minecraft:beacon";
            DataConverterMaterialId.materials[139] = "minecraft:cobblestone_wall";
            DataConverterMaterialId.materials[141] = "minecraft:carrots";
            DataConverterMaterialId.materials[142] = "minecraft:potatoes";
            DataConverterMaterialId.materials[143] = "minecraft:wooden_button";
            DataConverterMaterialId.materials[145] = "minecraft:anvil";
            DataConverterMaterialId.materials[146] = "minecraft:trapped_chest";
            DataConverterMaterialId.materials[147] = "minecraft:light_weighted_pressure_plate";
            DataConverterMaterialId.materials[148] = "minecraft:heavy_weighted_pressure_plate";
            DataConverterMaterialId.materials[151] = "minecraft:daylight_detector";
            DataConverterMaterialId.materials[152] = "minecraft:redstone_block";
            DataConverterMaterialId.materials[153] = "minecraft:quartz_ore";
            DataConverterMaterialId.materials[154] = "minecraft:hopper";
            DataConverterMaterialId.materials[155] = "minecraft:quartz_block";
            DataConverterMaterialId.materials[156] = "minecraft:quartz_stairs";
            DataConverterMaterialId.materials[157] = "minecraft:activator_rail";
            DataConverterMaterialId.materials[158] = "minecraft:dropper";
            DataConverterMaterialId.materials[159] = "minecraft:stained_hardened_clay";
            DataConverterMaterialId.materials[160] = "minecraft:stained_glass_pane";
            DataConverterMaterialId.materials[161] = "minecraft:leaves2";
            DataConverterMaterialId.materials[162] = "minecraft:log2";
            DataConverterMaterialId.materials[163] = "minecraft:acacia_stairs";
            DataConverterMaterialId.materials[164] = "minecraft:dark_oak_stairs";
            DataConverterMaterialId.materials[170] = "minecraft:hay_block";
            DataConverterMaterialId.materials[171] = "minecraft:carpet";
            DataConverterMaterialId.materials[172] = "minecraft:hardened_clay";
            DataConverterMaterialId.materials[173] = "minecraft:coal_block";
            DataConverterMaterialId.materials[174] = "minecraft:packed_ice";
            DataConverterMaterialId.materials[175] = "minecraft:double_plant";
            DataConverterMaterialId.materials[256] = "minecraft:iron_shovel";
            DataConverterMaterialId.materials[257] = "minecraft:iron_pickaxe";
            DataConverterMaterialId.materials[258] = "minecraft:iron_axe";
            DataConverterMaterialId.materials[259] = "minecraft:flint_and_steel";
            DataConverterMaterialId.materials[260] = "minecraft:apple";
            DataConverterMaterialId.materials[261] = "minecraft:bow";
            DataConverterMaterialId.materials[262] = "minecraft:arrow";
            DataConverterMaterialId.materials[263] = "minecraft:coal";
            DataConverterMaterialId.materials[264] = "minecraft:diamond";
            DataConverterMaterialId.materials[265] = "minecraft:iron_ingot";
            DataConverterMaterialId.materials[266] = "minecraft:gold_ingot";
            DataConverterMaterialId.materials[267] = "minecraft:iron_sword";
            DataConverterMaterialId.materials[268] = "minecraft:wooden_sword";
            DataConverterMaterialId.materials[269] = "minecraft:wooden_shovel";
            DataConverterMaterialId.materials[270] = "minecraft:wooden_pickaxe";
            DataConverterMaterialId.materials[271] = "minecraft:wooden_axe";
            DataConverterMaterialId.materials[272] = "minecraft:stone_sword";
            DataConverterMaterialId.materials[273] = "minecraft:stone_shovel";
            DataConverterMaterialId.materials[274] = "minecraft:stone_pickaxe";
            DataConverterMaterialId.materials[275] = "minecraft:stone_axe";
            DataConverterMaterialId.materials[276] = "minecraft:diamond_sword";
            DataConverterMaterialId.materials[277] = "minecraft:diamond_shovel";
            DataConverterMaterialId.materials[278] = "minecraft:diamond_pickaxe";
            DataConverterMaterialId.materials[279] = "minecraft:diamond_axe";
            DataConverterMaterialId.materials[280] = "minecraft:stick";
            DataConverterMaterialId.materials[281] = "minecraft:bowl";
            DataConverterMaterialId.materials[282] = "minecraft:mushroom_stew";
            DataConverterMaterialId.materials[283] = "minecraft:golden_sword";
            DataConverterMaterialId.materials[284] = "minecraft:golden_shovel";
            DataConverterMaterialId.materials[285] = "minecraft:golden_pickaxe";
            DataConverterMaterialId.materials[286] = "minecraft:golden_axe";
            DataConverterMaterialId.materials[287] = "minecraft:string";
            DataConverterMaterialId.materials[288] = "minecraft:feather";
            DataConverterMaterialId.materials[289] = "minecraft:gunpowder";
            DataConverterMaterialId.materials[290] = "minecraft:wooden_hoe";
            DataConverterMaterialId.materials[291] = "minecraft:stone_hoe";
            DataConverterMaterialId.materials[292] = "minecraft:iron_hoe";
            DataConverterMaterialId.materials[293] = "minecraft:diamond_hoe";
            DataConverterMaterialId.materials[294] = "minecraft:golden_hoe";
            DataConverterMaterialId.materials[295] = "minecraft:wheat_seeds";
            DataConverterMaterialId.materials[296] = "minecraft:wheat";
            DataConverterMaterialId.materials[297] = "minecraft:bread";
            DataConverterMaterialId.materials[298] = "minecraft:leather_helmet";
            DataConverterMaterialId.materials[299] = "minecraft:leather_chestplate";
            DataConverterMaterialId.materials[300] = "minecraft:leather_leggings";
            DataConverterMaterialId.materials[301] = "minecraft:leather_boots";
            DataConverterMaterialId.materials[302] = "minecraft:chainmail_helmet";
            DataConverterMaterialId.materials[303] = "minecraft:chainmail_chestplate";
            DataConverterMaterialId.materials[304] = "minecraft:chainmail_leggings";
            DataConverterMaterialId.materials[305] = "minecraft:chainmail_boots";
            DataConverterMaterialId.materials[306] = "minecraft:iron_helmet";
            DataConverterMaterialId.materials[307] = "minecraft:iron_chestplate";
            DataConverterMaterialId.materials[308] = "minecraft:iron_leggings";
            DataConverterMaterialId.materials[309] = "minecraft:iron_boots";
            DataConverterMaterialId.materials[310] = "minecraft:diamond_helmet";
            DataConverterMaterialId.materials[311] = "minecraft:diamond_chestplate";
            DataConverterMaterialId.materials[312] = "minecraft:diamond_leggings";
            DataConverterMaterialId.materials[313] = "minecraft:diamond_boots";
            DataConverterMaterialId.materials[314] = "minecraft:golden_helmet";
            DataConverterMaterialId.materials[315] = "minecraft:golden_chestplate";
            DataConverterMaterialId.materials[316] = "minecraft:golden_leggings";
            DataConverterMaterialId.materials[317] = "minecraft:golden_boots";
            DataConverterMaterialId.materials[318] = "minecraft:flint";
            DataConverterMaterialId.materials[319] = "minecraft:porkchop";
            DataConverterMaterialId.materials[320] = "minecraft:cooked_porkchop";
            DataConverterMaterialId.materials[321] = "minecraft:painting";
            DataConverterMaterialId.materials[322] = "minecraft:golden_apple";
            DataConverterMaterialId.materials[323] = "minecraft:sign";
            DataConverterMaterialId.materials[324] = "minecraft:wooden_door";
            DataConverterMaterialId.materials[325] = "minecraft:bucket";
            DataConverterMaterialId.materials[326] = "minecraft:water_bucket";
            DataConverterMaterialId.materials[327] = "minecraft:lava_bucket";
            DataConverterMaterialId.materials[328] = "minecraft:minecart";
            DataConverterMaterialId.materials[329] = "minecraft:saddle";
            DataConverterMaterialId.materials[330] = "minecraft:iron_door";
            DataConverterMaterialId.materials[331] = "minecraft:redstone";
            DataConverterMaterialId.materials[332] = "minecraft:snowball";
            DataConverterMaterialId.materials[333] = "minecraft:boat";
            DataConverterMaterialId.materials[334] = "minecraft:leather";
            DataConverterMaterialId.materials[335] = "minecraft:milk_bucket";
            DataConverterMaterialId.materials[336] = "minecraft:brick";
            DataConverterMaterialId.materials[337] = "minecraft:clay_ball";
            DataConverterMaterialId.materials[338] = "minecraft:reeds";
            DataConverterMaterialId.materials[339] = "minecraft:paper";
            DataConverterMaterialId.materials[340] = "minecraft:book";
            DataConverterMaterialId.materials[341] = "minecraft:slime_ball";
            DataConverterMaterialId.materials[342] = "minecraft:chest_minecart";
            DataConverterMaterialId.materials[343] = "minecraft:furnace_minecart";
            DataConverterMaterialId.materials[344] = "minecraft:egg";
            DataConverterMaterialId.materials[345] = "minecraft:compass";
            DataConverterMaterialId.materials[346] = "minecraft:fishing_rod";
            DataConverterMaterialId.materials[347] = "minecraft:clock";
            DataConverterMaterialId.materials[348] = "minecraft:glowstone_dust";
            DataConverterMaterialId.materials[349] = "minecraft:fish";
            DataConverterMaterialId.materials[350] = "minecraft:cooked_fish";
            DataConverterMaterialId.materials[351] = "minecraft:dye";
            DataConverterMaterialId.materials[352] = "minecraft:bone";
            DataConverterMaterialId.materials[353] = "minecraft:sugar";
            DataConverterMaterialId.materials[354] = "minecraft:cake";
            DataConverterMaterialId.materials[355] = "minecraft:bed";
            DataConverterMaterialId.materials[356] = "minecraft:repeater";
            DataConverterMaterialId.materials[357] = "minecraft:cookie";
            DataConverterMaterialId.materials[358] = "minecraft:filled_map";
            DataConverterMaterialId.materials[359] = "minecraft:shears";
            DataConverterMaterialId.materials[360] = "minecraft:melon";
            DataConverterMaterialId.materials[361] = "minecraft:pumpkin_seeds";
            DataConverterMaterialId.materials[362] = "minecraft:melon_seeds";
            DataConverterMaterialId.materials[363] = "minecraft:beef";
            DataConverterMaterialId.materials[364] = "minecraft:cooked_beef";
            DataConverterMaterialId.materials[365] = "minecraft:chicken";
            DataConverterMaterialId.materials[366] = "minecraft:cooked_chicken";
            DataConverterMaterialId.materials[367] = "minecraft:rotten_flesh";
            DataConverterMaterialId.materials[368] = "minecraft:ender_pearl";
            DataConverterMaterialId.materials[369] = "minecraft:blaze_rod";
            DataConverterMaterialId.materials[370] = "minecraft:ghast_tear";
            DataConverterMaterialId.materials[371] = "minecraft:gold_nugget";
            DataConverterMaterialId.materials[372] = "minecraft:nether_wart";
            DataConverterMaterialId.materials[373] = "minecraft:potion";
            DataConverterMaterialId.materials[374] = "minecraft:glass_bottle";
            DataConverterMaterialId.materials[375] = "minecraft:spider_eye";
            DataConverterMaterialId.materials[376] = "minecraft:fermented_spider_eye";
            DataConverterMaterialId.materials[377] = "minecraft:blaze_powder";
            DataConverterMaterialId.materials[378] = "minecraft:magma_cream";
            DataConverterMaterialId.materials[379] = "minecraft:brewing_stand";
            DataConverterMaterialId.materials[380] = "minecraft:cauldron";
            DataConverterMaterialId.materials[381] = "minecraft:ender_eye";
            DataConverterMaterialId.materials[382] = "minecraft:speckled_melon";
            DataConverterMaterialId.materials[383] = "minecraft:spawn_egg";
            DataConverterMaterialId.materials[384] = "minecraft:experience_bottle";
            DataConverterMaterialId.materials[385] = "minecraft:fire_charge";
            DataConverterMaterialId.materials[386] = "minecraft:writable_book";
            DataConverterMaterialId.materials[387] = "minecraft:written_book";
            DataConverterMaterialId.materials[388] = "minecraft:emerald";
            DataConverterMaterialId.materials[389] = "minecraft:item_frame";
            DataConverterMaterialId.materials[390] = "minecraft:flower_pot";
            DataConverterMaterialId.materials[391] = "minecraft:carrot";
            DataConverterMaterialId.materials[392] = "minecraft:potato";
            DataConverterMaterialId.materials[393] = "minecraft:baked_potato";
            DataConverterMaterialId.materials[394] = "minecraft:poisonous_potato";
            DataConverterMaterialId.materials[395] = "minecraft:map";
            DataConverterMaterialId.materials[396] = "minecraft:golden_carrot";
            DataConverterMaterialId.materials[397] = "minecraft:skull";
            DataConverterMaterialId.materials[398] = "minecraft:carrot_on_a_stick";
            DataConverterMaterialId.materials[399] = "minecraft:nether_star";
            DataConverterMaterialId.materials[400] = "minecraft:pumpkin_pie";
            DataConverterMaterialId.materials[401] = "minecraft:fireworks";
            DataConverterMaterialId.materials[402] = "minecraft:firework_charge";
            DataConverterMaterialId.materials[403] = "minecraft:enchanted_book";
            DataConverterMaterialId.materials[404] = "minecraft:comparator";
            DataConverterMaterialId.materials[405] = "minecraft:netherbrick";
            DataConverterMaterialId.materials[406] = "minecraft:quartz";
            DataConverterMaterialId.materials[407] = "minecraft:tnt_minecart";
            DataConverterMaterialId.materials[408] = "minecraft:hopper_minecart";
            DataConverterMaterialId.materials[417] = "minecraft:iron_horse_armor";
            DataConverterMaterialId.materials[418] = "minecraft:golden_horse_armor";
            DataConverterMaterialId.materials[419] = "minecraft:diamond_horse_armor";
            DataConverterMaterialId.materials[420] = "minecraft:lead";
            DataConverterMaterialId.materials[421] = "minecraft:name_tag";
            DataConverterMaterialId.materials[422] = "minecraft:command_block_minecart";
            DataConverterMaterialId.materials[2256] = "minecraft:record_13";
            DataConverterMaterialId.materials[2257] = "minecraft:record_cat";
            DataConverterMaterialId.materials[2258] = "minecraft:record_blocks";
            DataConverterMaterialId.materials[2259] = "minecraft:record_chirp";
            DataConverterMaterialId.materials[2260] = "minecraft:record_far";
            DataConverterMaterialId.materials[2261] = "minecraft:record_mall";
            DataConverterMaterialId.materials[2262] = "minecraft:record_mellohi";
            DataConverterMaterialId.materials[2263] = "minecraft:record_stal";
            DataConverterMaterialId.materials[2264] = "minecraft:record_strad";
            DataConverterMaterialId.materials[2265] = "minecraft:record_ward";
            DataConverterMaterialId.materials[2266] = "minecraft:record_11";
            DataConverterMaterialId.materials[2267] = "minecraft:record_wait";
        }
    }

    private static class DataConverterPotionId
    implements DataConverter {
        private static final String[] potions = new String[128];

        DataConverterPotionId() {
        }

        @Override
        public int getDataVersion() {
            return 102;
        }

        @Override
        public class_2487 convert(class_2487 cmp) {
            if ("minecraft:potion".equals(cmp.method_10558("id"))) {
                class_2487 nbttagcompound1 = cmp.method_10562("tag");
                short short0 = cmp.method_10568("Damage");
                if (!nbttagcompound1.method_10573("Potion", 8)) {
                    String s = potions[short0 & 0x7F];
                    nbttagcompound1.method_10582("Potion", s == null ? "minecraft:water" : s);
                    cmp.method_10566("tag", (class_2520)nbttagcompound1);
                    if ((short0 & 0x4000) == 16384) {
                        cmp.method_10582("id", "minecraft:splash_potion");
                    }
                }
                if (short0 != 0) {
                    cmp.method_10575("Damage", (short)0);
                }
            }
            return cmp;
        }

        static {
            DataConverterPotionId.potions[0] = "minecraft:water";
            DataConverterPotionId.potions[1] = "minecraft:regeneration";
            DataConverterPotionId.potions[2] = "minecraft:swiftness";
            DataConverterPotionId.potions[3] = "minecraft:fire_resistance";
            DataConverterPotionId.potions[4] = "minecraft:poison";
            DataConverterPotionId.potions[5] = "minecraft:healing";
            DataConverterPotionId.potions[6] = "minecraft:night_vision";
            DataConverterPotionId.potions[7] = null;
            DataConverterPotionId.potions[8] = "minecraft:weakness";
            DataConverterPotionId.potions[9] = "minecraft:strength";
            DataConverterPotionId.potions[10] = "minecraft:slowness";
            DataConverterPotionId.potions[11] = "minecraft:leaping";
            DataConverterPotionId.potions[12] = "minecraft:harming";
            DataConverterPotionId.potions[13] = "minecraft:water_breathing";
            DataConverterPotionId.potions[14] = "minecraft:invisibility";
            DataConverterPotionId.potions[15] = null;
            DataConverterPotionId.potions[16] = "minecraft:awkward";
            DataConverterPotionId.potions[17] = "minecraft:regeneration";
            DataConverterPotionId.potions[18] = "minecraft:swiftness";
            DataConverterPotionId.potions[19] = "minecraft:fire_resistance";
            DataConverterPotionId.potions[20] = "minecraft:poison";
            DataConverterPotionId.potions[21] = "minecraft:healing";
            DataConverterPotionId.potions[22] = "minecraft:night_vision";
            DataConverterPotionId.potions[23] = null;
            DataConverterPotionId.potions[24] = "minecraft:weakness";
            DataConverterPotionId.potions[25] = "minecraft:strength";
            DataConverterPotionId.potions[26] = "minecraft:slowness";
            DataConverterPotionId.potions[27] = "minecraft:leaping";
            DataConverterPotionId.potions[28] = "minecraft:harming";
            DataConverterPotionId.potions[29] = "minecraft:water_breathing";
            DataConverterPotionId.potions[30] = "minecraft:invisibility";
            DataConverterPotionId.potions[31] = null;
            DataConverterPotionId.potions[32] = "minecraft:thick";
            DataConverterPotionId.potions[33] = "minecraft:strong_regeneration";
            DataConverterPotionId.potions[34] = "minecraft:strong_swiftness";
            DataConverterPotionId.potions[35] = "minecraft:fire_resistance";
            DataConverterPotionId.potions[36] = "minecraft:strong_poison";
            DataConverterPotionId.potions[37] = "minecraft:strong_healing";
            DataConverterPotionId.potions[38] = "minecraft:night_vision";
            DataConverterPotionId.potions[39] = null;
            DataConverterPotionId.potions[40] = "minecraft:weakness";
            DataConverterPotionId.potions[41] = "minecraft:strong_strength";
            DataConverterPotionId.potions[42] = "minecraft:slowness";
            DataConverterPotionId.potions[43] = "minecraft:strong_leaping";
            DataConverterPotionId.potions[44] = "minecraft:strong_harming";
            DataConverterPotionId.potions[45] = "minecraft:water_breathing";
            DataConverterPotionId.potions[46] = "minecraft:invisibility";
            DataConverterPotionId.potions[47] = null;
            DataConverterPotionId.potions[48] = null;
            DataConverterPotionId.potions[49] = "minecraft:strong_regeneration";
            DataConverterPotionId.potions[50] = "minecraft:strong_swiftness";
            DataConverterPotionId.potions[51] = "minecraft:fire_resistance";
            DataConverterPotionId.potions[52] = "minecraft:strong_poison";
            DataConverterPotionId.potions[53] = "minecraft:strong_healing";
            DataConverterPotionId.potions[54] = "minecraft:night_vision";
            DataConverterPotionId.potions[55] = null;
            DataConverterPotionId.potions[56] = "minecraft:weakness";
            DataConverterPotionId.potions[57] = "minecraft:strong_strength";
            DataConverterPotionId.potions[58] = "minecraft:slowness";
            DataConverterPotionId.potions[59] = "minecraft:strong_leaping";
            DataConverterPotionId.potions[60] = "minecraft:strong_harming";
            DataConverterPotionId.potions[61] = "minecraft:water_breathing";
            DataConverterPotionId.potions[62] = "minecraft:invisibility";
            DataConverterPotionId.potions[63] = null;
            DataConverterPotionId.potions[64] = "minecraft:mundane";
            DataConverterPotionId.potions[65] = "minecraft:long_regeneration";
            DataConverterPotionId.potions[66] = "minecraft:long_swiftness";
            DataConverterPotionId.potions[67] = "minecraft:long_fire_resistance";
            DataConverterPotionId.potions[68] = "minecraft:long_poison";
            DataConverterPotionId.potions[69] = "minecraft:healing";
            DataConverterPotionId.potions[70] = "minecraft:long_night_vision";
            DataConverterPotionId.potions[71] = null;
            DataConverterPotionId.potions[72] = "minecraft:long_weakness";
            DataConverterPotionId.potions[73] = "minecraft:long_strength";
            DataConverterPotionId.potions[74] = "minecraft:long_slowness";
            DataConverterPotionId.potions[75] = "minecraft:long_leaping";
            DataConverterPotionId.potions[76] = "minecraft:harming";
            DataConverterPotionId.potions[77] = "minecraft:long_water_breathing";
            DataConverterPotionId.potions[78] = "minecraft:long_invisibility";
            DataConverterPotionId.potions[79] = null;
            DataConverterPotionId.potions[80] = "minecraft:awkward";
            DataConverterPotionId.potions[81] = "minecraft:long_regeneration";
            DataConverterPotionId.potions[82] = "minecraft:long_swiftness";
            DataConverterPotionId.potions[83] = "minecraft:long_fire_resistance";
            DataConverterPotionId.potions[84] = "minecraft:long_poison";
            DataConverterPotionId.potions[85] = "minecraft:healing";
            DataConverterPotionId.potions[86] = "minecraft:long_night_vision";
            DataConverterPotionId.potions[87] = null;
            DataConverterPotionId.potions[88] = "minecraft:long_weakness";
            DataConverterPotionId.potions[89] = "minecraft:long_strength";
            DataConverterPotionId.potions[90] = "minecraft:long_slowness";
            DataConverterPotionId.potions[91] = "minecraft:long_leaping";
            DataConverterPotionId.potions[92] = "minecraft:harming";
            DataConverterPotionId.potions[93] = "minecraft:long_water_breathing";
            DataConverterPotionId.potions[94] = "minecraft:long_invisibility";
            DataConverterPotionId.potions[95] = null;
            DataConverterPotionId.potions[96] = "minecraft:thick";
            DataConverterPotionId.potions[97] = "minecraft:regeneration";
            DataConverterPotionId.potions[98] = "minecraft:swiftness";
            DataConverterPotionId.potions[99] = "minecraft:long_fire_resistance";
            DataConverterPotionId.potions[100] = "minecraft:poison";
            DataConverterPotionId.potions[101] = "minecraft:strong_healing";
            DataConverterPotionId.potions[102] = "minecraft:long_night_vision";
            DataConverterPotionId.potions[103] = null;
            DataConverterPotionId.potions[104] = "minecraft:long_weakness";
            DataConverterPotionId.potions[105] = "minecraft:strength";
            DataConverterPotionId.potions[106] = "minecraft:long_slowness";
            DataConverterPotionId.potions[107] = "minecraft:leaping";
            DataConverterPotionId.potions[108] = "minecraft:strong_harming";
            DataConverterPotionId.potions[109] = "minecraft:long_water_breathing";
            DataConverterPotionId.potions[110] = "minecraft:long_invisibility";
            DataConverterPotionId.potions[111] = null;
            DataConverterPotionId.potions[112] = null;
            DataConverterPotionId.potions[113] = "minecraft:regeneration";
            DataConverterPotionId.potions[114] = "minecraft:swiftness";
            DataConverterPotionId.potions[115] = "minecraft:long_fire_resistance";
            DataConverterPotionId.potions[116] = "minecraft:poison";
            DataConverterPotionId.potions[117] = "minecraft:strong_healing";
            DataConverterPotionId.potions[118] = "minecraft:long_night_vision";
            DataConverterPotionId.potions[119] = null;
            DataConverterPotionId.potions[120] = "minecraft:long_weakness";
            DataConverterPotionId.potions[121] = "minecraft:strength";
            DataConverterPotionId.potions[122] = "minecraft:long_slowness";
            DataConverterPotionId.potions[123] = "minecraft:leaping";
            DataConverterPotionId.potions[124] = "minecraft:strong_harming";
            DataConverterPotionId.potions[125] = "minecraft:long_water_breathing";
            DataConverterPotionId.potions[126] = "minecraft:long_invisibility";
            DataConverterPotionId.potions[127] = null;
        }
    }

    private static class DataConverterSpawnEgg
    implements DataConverter {
        private static final String[] eggs = new String[256];

        DataConverterSpawnEgg() {
        }

        @Override
        public int getDataVersion() {
            return 105;
        }

        @Override
        public class_2487 convert(class_2487 cmp) {
            if ("minecraft:spawn_egg".equals(cmp.method_10558("id"))) {
                String s;
                class_2487 nbttagcompound1 = cmp.method_10562("tag");
                class_2487 nbttagcompound2 = nbttagcompound1.method_10562("EntityTag");
                short short0 = cmp.method_10568("Damage");
                if (!nbttagcompound2.method_10573("id", 8) && (s = eggs[short0 & 0xFF]) != null) {
                    nbttagcompound2.method_10582("id", s);
                    nbttagcompound1.method_10566("EntityTag", (class_2520)nbttagcompound2);
                    cmp.method_10566("tag", (class_2520)nbttagcompound1);
                }
                if (short0 != 0) {
                    cmp.method_10575("Damage", (short)0);
                }
            }
            return cmp;
        }

        static {
            DataConverterSpawnEgg.eggs[1] = "Item";
            DataConverterSpawnEgg.eggs[2] = "XPOrb";
            DataConverterSpawnEgg.eggs[7] = "ThrownEgg";
            DataConverterSpawnEgg.eggs[8] = "LeashKnot";
            DataConverterSpawnEgg.eggs[9] = "Painting";
            DataConverterSpawnEgg.eggs[10] = "Arrow";
            DataConverterSpawnEgg.eggs[11] = "Snowball";
            DataConverterSpawnEgg.eggs[12] = "Fireball";
            DataConverterSpawnEgg.eggs[13] = "SmallFireball";
            DataConverterSpawnEgg.eggs[14] = "ThrownEnderpearl";
            DataConverterSpawnEgg.eggs[15] = "EyeOfEnderSignal";
            DataConverterSpawnEgg.eggs[16] = "ThrownPotion";
            DataConverterSpawnEgg.eggs[17] = "ThrownExpBottle";
            DataConverterSpawnEgg.eggs[18] = "ItemFrame";
            DataConverterSpawnEgg.eggs[19] = "WitherSkull";
            DataConverterSpawnEgg.eggs[20] = "PrimedTnt";
            DataConverterSpawnEgg.eggs[21] = "FallingSand";
            DataConverterSpawnEgg.eggs[22] = "FireworksRocketEntity";
            DataConverterSpawnEgg.eggs[23] = "TippedArrow";
            DataConverterSpawnEgg.eggs[24] = "SpectralArrow";
            DataConverterSpawnEgg.eggs[25] = "ShulkerBullet";
            DataConverterSpawnEgg.eggs[26] = "DragonFireball";
            DataConverterSpawnEgg.eggs[30] = "ArmorStand";
            DataConverterSpawnEgg.eggs[41] = "Boat";
            DataConverterSpawnEgg.eggs[42] = "MinecartRideable";
            DataConverterSpawnEgg.eggs[43] = "MinecartChest";
            DataConverterSpawnEgg.eggs[44] = "MinecartFurnace";
            DataConverterSpawnEgg.eggs[45] = "MinecartTNT";
            DataConverterSpawnEgg.eggs[46] = "MinecartHopper";
            DataConverterSpawnEgg.eggs[47] = "MinecartSpawner";
            DataConverterSpawnEgg.eggs[40] = "MinecartCommandBlock";
            DataConverterSpawnEgg.eggs[48] = "Mob";
            DataConverterSpawnEgg.eggs[49] = "Monster";
            DataConverterSpawnEgg.eggs[50] = "Creeper";
            DataConverterSpawnEgg.eggs[51] = "Skeleton";
            DataConverterSpawnEgg.eggs[52] = "Spider";
            DataConverterSpawnEgg.eggs[53] = "Giant";
            DataConverterSpawnEgg.eggs[54] = "Zombie";
            DataConverterSpawnEgg.eggs[55] = "Slime";
            DataConverterSpawnEgg.eggs[56] = "Ghast";
            DataConverterSpawnEgg.eggs[57] = "PigZombie";
            DataConverterSpawnEgg.eggs[58] = "Enderman";
            DataConverterSpawnEgg.eggs[59] = "CaveSpider";
            DataConverterSpawnEgg.eggs[60] = "Silverfish";
            DataConverterSpawnEgg.eggs[61] = "Blaze";
            DataConverterSpawnEgg.eggs[62] = "LavaSlime";
            DataConverterSpawnEgg.eggs[63] = "EnderDragon";
            DataConverterSpawnEgg.eggs[64] = "WitherBoss";
            DataConverterSpawnEgg.eggs[65] = "Bat";
            DataConverterSpawnEgg.eggs[66] = "Witch";
            DataConverterSpawnEgg.eggs[67] = "Endermite";
            DataConverterSpawnEgg.eggs[68] = "Guardian";
            DataConverterSpawnEgg.eggs[69] = "Shulker";
            DataConverterSpawnEgg.eggs[90] = "Pig";
            DataConverterSpawnEgg.eggs[91] = "Sheep";
            DataConverterSpawnEgg.eggs[92] = "Cow";
            DataConverterSpawnEgg.eggs[93] = "Chicken";
            DataConverterSpawnEgg.eggs[94] = "Squid";
            DataConverterSpawnEgg.eggs[95] = "Wolf";
            DataConverterSpawnEgg.eggs[96] = "MushroomCow";
            DataConverterSpawnEgg.eggs[97] = "SnowMan";
            DataConverterSpawnEgg.eggs[98] = "Ozelot";
            DataConverterSpawnEgg.eggs[99] = "VillagerGolem";
            DataConverterSpawnEgg.eggs[100] = "EntityHorse";
            DataConverterSpawnEgg.eggs[101] = "Rabbit";
            DataConverterSpawnEgg.eggs[120] = "Villager";
            DataConverterSpawnEgg.eggs[200] = "EnderCrystal";
        }
    }

    private static class DataConverterMinecart
    implements DataConverter {
        private static final List<String> a = Lists.newArrayList((Object[])new String[]{"MinecartRideable", "MinecartChest", "MinecartFurnace", "MinecartTNT", "MinecartSpawner", "MinecartHopper", "MinecartCommandBlock"});

        DataConverterMinecart() {
        }

        @Override
        public int getDataVersion() {
            return 106;
        }

        @Override
        public class_2487 convert(class_2487 cmp) {
            if ("Minecart".equals(cmp.method_10558("id"))) {
                String s = "MinecartRideable";
                int i = cmp.method_10550("Type");
                if (i > 0 && i < a.size()) {
                    s = a.get(i);
                }
                cmp.method_10582("id", s);
                cmp.method_10551("Type");
            }
            return cmp;
        }
    }

    private static class DataConverterMobSpawner
    implements DataConverter {
        DataConverterMobSpawner() {
        }

        @Override
        public int getDataVersion() {
            return 107;
        }

        @Override
        public class_2487 convert(class_2487 cmp) {
            if (!"MobSpawner".equals(cmp.method_10558("id"))) {
                return cmp;
            }
            if (cmp.method_10573("EntityId", 8)) {
                String s = cmp.method_10558("EntityId");
                class_2487 nbttagcompound1 = cmp.method_10562("SpawnData");
                nbttagcompound1.method_10582("id", s.isEmpty() ? "Pig" : s);
                cmp.method_10566("SpawnData", (class_2520)nbttagcompound1);
                cmp.method_10551("EntityId");
            }
            if (cmp.method_10573("SpawnPotentials", 9)) {
                class_2499 nbttaglist = cmp.method_10554("SpawnPotentials", 10);
                for (int i = 0; i < nbttaglist.size(); ++i) {
                    class_2487 nbttagcompound2 = nbttaglist.method_10602(i);
                    if (!nbttagcompound2.method_10573("Type", 8)) continue;
                    class_2487 nbttagcompound3 = nbttagcompound2.method_10562("Properties");
                    nbttagcompound3.method_10582("id", nbttagcompound2.method_10558("Type"));
                    nbttagcompound2.method_10566("Entity", (class_2520)nbttagcompound3);
                    nbttagcompound2.method_10551("Type");
                    nbttagcompound2.method_10551("Properties");
                }
            }
            return cmp;
        }
    }

    private static class DataConverterUUID
    implements DataConverter {
        DataConverterUUID() {
        }

        @Override
        public int getDataVersion() {
            return 108;
        }

        @Override
        public class_2487 convert(class_2487 cmp) {
            if (cmp.method_10573("UUID", 8)) {
                cmp.method_25927("UUID", UUID.fromString(cmp.method_10558("UUID")));
            }
            return cmp;
        }
    }

    private static class DataConverterHealth
    implements DataConverter {
        private static final Set<String> a = Sets.newHashSet((Object[])new String[]{"ArmorStand", "Bat", "Blaze", "CaveSpider", "Chicken", "Cow", "Creeper", "EnderDragon", "Enderman", "Endermite", "EntityHorse", "Ghast", "Giant", "Guardian", "LavaSlime", "MushroomCow", "Ozelot", "Pig", "PigZombie", "Rabbit", "Sheep", "Shulker", "Silverfish", "Skeleton", "Slime", "SnowMan", "Spider", "Squid", "Villager", "VillagerGolem", "Witch", "WitherBoss", "Wolf", "Zombie"});

        DataConverterHealth() {
        }

        @Override
        public int getDataVersion() {
            return 109;
        }

        @Override
        public class_2487 convert(class_2487 cmp) {
            if (a.contains(cmp.method_10558("id"))) {
                float f;
                if (cmp.method_10573("HealF", 99)) {
                    f = cmp.method_10583("HealF");
                    cmp.method_10551("HealF");
                } else {
                    if (!cmp.method_10573("Health", 99)) {
                        return cmp;
                    }
                    f = cmp.method_10583("Health");
                }
                cmp.method_10548("Health", f);
            }
            return cmp;
        }
    }

    private static class DataConverterSaddle
    implements DataConverter {
        DataConverterSaddle() {
        }

        @Override
        public int getDataVersion() {
            return 110;
        }

        @Override
        public class_2487 convert(class_2487 cmp) {
            if ("EntityHorse".equals(cmp.method_10558("id")) && !cmp.method_10573("SaddleItem", 10) && cmp.method_10577("Saddle")) {
                class_2487 nbttagcompound1 = new class_2487();
                nbttagcompound1.method_10582("id", "minecraft:saddle");
                nbttagcompound1.method_10567("Count", (byte)1);
                nbttagcompound1.method_10575("Damage", (short)0);
                cmp.method_10566("SaddleItem", (class_2520)nbttagcompound1);
                cmp.method_10551("Saddle");
            }
            return cmp;
        }
    }

    private static class DataConverterHanging
    implements DataConverter {
        DataConverterHanging() {
        }

        @Override
        public int getDataVersion() {
            return 111;
        }

        @Override
        public class_2487 convert(class_2487 cmp) {
            String s = cmp.method_10558("id");
            boolean flag = "Painting".equals(s);
            boolean flag1 = "ItemFrame".equals(s);
            if ((flag || flag1) && !cmp.method_10573("Facing", 99)) {
                class_2350 enumdirection;
                if (cmp.method_10573("Direction", 99)) {
                    enumdirection = class_2350.method_10139((int)cmp.method_10571("Direction"));
                    cmp.method_10569("TileX", cmp.method_10550("TileX") + enumdirection.method_10148());
                    cmp.method_10569("TileY", cmp.method_10550("TileY") + enumdirection.method_10164());
                    cmp.method_10569("TileZ", cmp.method_10550("TileZ") + enumdirection.method_10165());
                    cmp.method_10551("Direction");
                    if (flag1 && cmp.method_10573("ItemRotation", 99)) {
                        cmp.method_10567("ItemRotation", (byte)(cmp.method_10571("ItemRotation") * 2));
                    }
                } else {
                    enumdirection = class_2350.method_10139((int)cmp.method_10571("Dir"));
                    cmp.method_10551("Dir");
                }
                cmp.method_10567("Facing", (byte)enumdirection.method_10161());
            }
            return cmp;
        }
    }

    private static class DataConverterDropChances
    implements DataConverter {
        DataConverterDropChances() {
        }

        @Override
        public int getDataVersion() {
            return 113;
        }

        @Override
        public class_2487 convert(class_2487 cmp) {
            class_2499 nbttaglist;
            if (cmp.method_10573("HandDropChances", 9) && (nbttaglist = cmp.method_10554("HandDropChances", 5)).size() == 2 && nbttaglist.method_10604(0) == 0.0f && nbttaglist.method_10604(1) == 0.0f) {
                cmp.method_10551("HandDropChances");
            }
            if (cmp.method_10573("ArmorDropChances", 9) && (nbttaglist = cmp.method_10554("ArmorDropChances", 5)).size() == 4 && nbttaglist.method_10604(0) == 0.0f && nbttaglist.method_10604(1) == 0.0f && nbttaglist.method_10604(2) == 0.0f && nbttaglist.method_10604(3) == 0.0f) {
                cmp.method_10551("ArmorDropChances");
            }
            return cmp;
        }
    }

    private static class DataConverterRiding
    implements DataConverter {
        DataConverterRiding() {
        }

        @Override
        public int getDataVersion() {
            return 135;
        }

        @Override
        public class_2487 convert(class_2487 cmp) {
            while (cmp.method_10573("Riding", 10)) {
                class_2487 nbttagcompound1 = this.b(cmp);
                this.convert(cmp, nbttagcompound1);
                cmp = nbttagcompound1;
            }
            return cmp;
        }

        protected void convert(class_2487 nbttagcompound, class_2487 nbttagcompound1) {
            class_2499 nbttaglist = new class_2499();
            nbttaglist.add((Object)nbttagcompound);
            nbttagcompound1.method_10566("Passengers", (class_2520)nbttaglist);
        }

        protected class_2487 b(class_2487 nbttagcompound) {
            class_2487 nbttagcompound1 = nbttagcompound.method_10562("Riding");
            nbttagcompound.method_10551("Riding");
            return nbttagcompound1;
        }
    }

    private static class DataConverterArmorStand
    implements DataConverter {
        DataConverterArmorStand() {
        }

        @Override
        public int getDataVersion() {
            return 147;
        }

        @Override
        public class_2487 convert(class_2487 cmp) {
            if ("ArmorStand".equals(cmp.method_10558("id")) && cmp.method_10577("Silent") && !cmp.method_10577("Marker")) {
                cmp.method_10551("Silent");
            }
            return cmp;
        }
    }

    private static class DataConverterBook
    implements DataConverter {
        DataConverterBook() {
        }

        @Override
        public int getDataVersion() {
            return 165;
        }

        @Override
        public class_2487 convert(class_2487 cmp) {
            class_2487 nbttagcompound1;
            if ("minecraft:written_book".equals(cmp.method_10558("id")) && (nbttagcompound1 = cmp.method_10562("tag")).method_10573("pages", 9)) {
                class_2499 nbttaglist = nbttagcompound1.method_10554("pages", 8);
                for (int i = 0; i < nbttaglist.size(); ++i) {
                    String s = nbttaglist.method_10608(i);
                    Object object = null;
                    if (!"null".equals(s) && !Strings.isNullOrEmpty((String)s)) {
                        if (!(s.charAt(0) == '\"' && s.charAt(s.length() - 1) == '\"' || s.charAt(0) == '{' && s.charAt(s.length() - 1) == '}')) {
                            object = class_2561.method_43470((String)s);
                        } else {
                            try {
                                object = class_3518.method_15279((Gson)DataConverterSignText.a, (String)s, class_2561.class, (boolean)true);
                                if (object == null) {
                                    object = class_2561.method_43470((String)"");
                                }
                            }
                            catch (JsonParseException jsonParseException) {
                                // empty catch block
                            }
                            if (object == null) {
                                try {
                                    object = class_2561.class_2562.method_10877((String)s, (class_7225.class_7874)FabricWorldEdit.registryAccess());
                                }
                                catch (JsonParseException jsonParseException) {
                                    // empty catch block
                                }
                            }
                            if (object == null) {
                                try {
                                    object = class_2561.class_2562.method_10873((String)s, (class_7225.class_7874)FabricWorldEdit.registryAccess());
                                }
                                catch (JsonParseException jsonParseException) {
                                    // empty catch block
                                }
                            }
                            if (object == null) {
                                object = class_2561.method_43470((String)s);
                            }
                        }
                    } else {
                        object = class_2561.method_43470((String)"");
                    }
                    nbttaglist.method_10606(i, (class_2520)class_2519.method_23256((String)class_2561.class_2562.method_10867((class_2561)((class_2561)object), (class_7225.class_7874)FabricWorldEdit.registryAccess())));
                }
                nbttagcompound1.method_10566("pages", (class_2520)nbttaglist);
            }
            return cmp;
        }
    }

    private static class DataConverterCookedFish
    implements DataConverter {
        private static final class_2960 a = class_2960.method_60654((String)"cooked_fished");

        DataConverterCookedFish() {
        }

        @Override
        public int getDataVersion() {
            return 502;
        }

        @Override
        public class_2487 convert(class_2487 cmp) {
            if (cmp.method_10573("id", 8) && a.equals((Object)class_2960.method_60654((String)cmp.method_10558("id")))) {
                cmp.method_10582("id", "minecraft:cooked_fish");
            }
            return cmp;
        }
    }

    private static class DataConverterZombie
    implements DataConverter {
        private static final Random a = new Random();

        DataConverterZombie() {
        }

        @Override
        public int getDataVersion() {
            return 502;
        }

        @Override
        public class_2487 convert(class_2487 cmp) {
            if ("Zombie".equals(cmp.method_10558("id")) && cmp.method_10577("IsVillager")) {
                if (!cmp.method_10573("ZombieType", 99)) {
                    int i = -1;
                    if (cmp.method_10573("VillagerProfession", 99)) {
                        try {
                            i = this.convert(cmp.method_10550("VillagerProfession"));
                        }
                        catch (RuntimeException runtimeException) {
                            // empty catch block
                        }
                    }
                    if (i == -1) {
                        i = this.convert(a.nextInt(6));
                    }
                    cmp.method_10569("ZombieType", i);
                }
                cmp.method_10551("IsVillager");
            }
            return cmp;
        }

        private int convert(int i) {
            return i >= 0 && i < 6 ? i : -1;
        }
    }

    private static class DataConverterVBO
    implements DataConverter {
        DataConverterVBO() {
        }

        @Override
        public int getDataVersion() {
            return 505;
        }

        @Override
        public class_2487 convert(class_2487 cmp) {
            cmp.method_10582("useVbo", "true");
            return cmp;
        }
    }

    private static class DataConverterGuardian
    implements DataConverter {
        DataConverterGuardian() {
        }

        @Override
        public int getDataVersion() {
            return 700;
        }

        @Override
        public class_2487 convert(class_2487 cmp) {
            if ("Guardian".equals(cmp.method_10558("id"))) {
                if (cmp.method_10577("Elder")) {
                    cmp.method_10582("id", "ElderGuardian");
                }
                cmp.method_10551("Elder");
            }
            return cmp;
        }
    }

    private static class DataConverterSkeleton
    implements DataConverter {
        DataConverterSkeleton() {
        }

        @Override
        public int getDataVersion() {
            return 701;
        }

        @Override
        public class_2487 convert(class_2487 cmp) {
            String s = cmp.method_10558("id");
            if ("Skeleton".equals(s)) {
                int i = cmp.method_10550("SkeletonType");
                if (i == 1) {
                    cmp.method_10582("id", "WitherSkeleton");
                } else if (i == 2) {
                    cmp.method_10582("id", "Stray");
                }
                cmp.method_10551("SkeletonType");
            }
            return cmp;
        }
    }

    private static class DataConverterZombieType
    implements DataConverter {
        DataConverterZombieType() {
        }

        @Override
        public int getDataVersion() {
            return 702;
        }

        @Override
        public class_2487 convert(class_2487 cmp) {
            if ("Zombie".equals(cmp.method_10558("id"))) {
                int i = cmp.method_10550("ZombieType");
                switch (i) {
                    case 1: 
                    case 2: 
                    case 3: 
                    case 4: 
                    case 5: {
                        cmp.method_10582("id", "ZombieVillager");
                        cmp.method_10569("Profession", i - 1);
                        break;
                    }
                    case 6: {
                        cmp.method_10582("id", "Husk");
                    }
                }
                cmp.method_10551("ZombieType");
            }
            return cmp;
        }
    }

    private static class DataConverterHorse
    implements DataConverter {
        DataConverterHorse() {
        }

        @Override
        public int getDataVersion() {
            return 703;
        }

        @Override
        public class_2487 convert(class_2487 cmp) {
            if ("EntityHorse".equals(cmp.method_10558("id"))) {
                int i = cmp.method_10550("Type");
                switch (i) {
                    case 1: {
                        cmp.method_10582("id", "Donkey");
                        break;
                    }
                    case 2: {
                        cmp.method_10582("id", "Mule");
                        break;
                    }
                    case 3: {
                        cmp.method_10582("id", "ZombieHorse");
                        break;
                    }
                    case 4: {
                        cmp.method_10582("id", "SkeletonHorse");
                        break;
                    }
                    default: {
                        cmp.method_10582("id", "Horse");
                    }
                }
                cmp.method_10551("Type");
            }
            return cmp;
        }
    }

    private static class DataConverterTileEntity
    implements DataConverter {
        private static final Map<String, String> a = Maps.newHashMap();

        DataConverterTileEntity() {
        }

        @Override
        public int getDataVersion() {
            return 704;
        }

        @Override
        public class_2487 convert(class_2487 cmp) {
            String s = a.get(cmp.method_10558("id"));
            if (s != null) {
                cmp.method_10582("id", s);
            }
            return cmp;
        }

        static {
            a.put("Airportal", "minecraft:end_portal");
            a.put("Banner", "minecraft:banner");
            a.put("Beacon", "minecraft:beacon");
            a.put("Cauldron", "minecraft:brewing_stand");
            a.put("Chest", "minecraft:chest");
            a.put("Comparator", "minecraft:comparator");
            a.put("Control", "minecraft:command_block");
            a.put("DLDetector", "minecraft:daylight_detector");
            a.put("Dropper", "minecraft:dropper");
            a.put("EnchantTable", "minecraft:enchanting_table");
            a.put("EndGateway", "minecraft:end_gateway");
            a.put("EnderChest", "minecraft:ender_chest");
            a.put("FlowerPot", "minecraft:flower_pot");
            a.put("Furnace", "minecraft:furnace");
            a.put("Hopper", "minecraft:hopper");
            a.put("MobSpawner", "minecraft:mob_spawner");
            a.put("Music", "minecraft:noteblock");
            a.put("Piston", "minecraft:piston");
            a.put("RecordPlayer", "minecraft:jukebox");
            a.put("Sign", "minecraft:sign");
            a.put("Skull", "minecraft:skull");
            a.put("Structure", "minecraft:structure_block");
            a.put("Trap", "minecraft:dispenser");
        }
    }

    private static class DataConverterEntity
    implements DataConverter {
        private static final Map<String, String> a = Maps.newHashMap();

        DataConverterEntity() {
        }

        @Override
        public int getDataVersion() {
            return 704;
        }

        @Override
        public class_2487 convert(class_2487 cmp) {
            String s = a.get(cmp.method_10558("id"));
            if (s != null) {
                cmp.method_10582("id", s);
            }
            return cmp;
        }

        static {
            a.put("AreaEffectCloud", "minecraft:area_effect_cloud");
            a.put("ArmorStand", "minecraft:armor_stand");
            a.put("Arrow", "minecraft:arrow");
            a.put("Bat", "minecraft:bat");
            a.put("Blaze", "minecraft:blaze");
            a.put("Boat", "minecraft:boat");
            a.put("CaveSpider", "minecraft:cave_spider");
            a.put("Chicken", "minecraft:chicken");
            a.put("Cow", "minecraft:cow");
            a.put("Creeper", "minecraft:creeper");
            a.put("Donkey", "minecraft:donkey");
            a.put("DragonFireball", "minecraft:dragon_fireball");
            a.put("ElderGuardian", "minecraft:elder_guardian");
            a.put("EnderCrystal", "minecraft:ender_crystal");
            a.put("EnderDragon", "minecraft:ender_dragon");
            a.put("Enderman", "minecraft:enderman");
            a.put("Endermite", "minecraft:endermite");
            a.put("EyeOfEnderSignal", "minecraft:eye_of_ender_signal");
            a.put("FallingSand", "minecraft:falling_block");
            a.put("Fireball", "minecraft:fireball");
            a.put("FireworksRocketEntity", "minecraft:fireworks_rocket");
            a.put("Ghast", "minecraft:ghast");
            a.put("Giant", "minecraft:giant");
            a.put("Guardian", "minecraft:guardian");
            a.put("Horse", "minecraft:horse");
            a.put("Husk", "minecraft:husk");
            a.put("Item", "minecraft:item");
            a.put("ItemFrame", "minecraft:item_frame");
            a.put("LavaSlime", "minecraft:magma_cube");
            a.put("LeashKnot", "minecraft:leash_knot");
            a.put("MinecartChest", "minecraft:chest_minecart");
            a.put("MinecartCommandBlock", "minecraft:commandblock_minecart");
            a.put("MinecartFurnace", "minecraft:furnace_minecart");
            a.put("MinecartHopper", "minecraft:hopper_minecart");
            a.put("MinecartRideable", "minecraft:minecart");
            a.put("MinecartSpawner", "minecraft:spawner_minecart");
            a.put("MinecartTNT", "minecraft:tnt_minecart");
            a.put("Mule", "minecraft:mule");
            a.put("MushroomCow", "minecraft:mooshroom");
            a.put("Ozelot", "minecraft:ocelot");
            a.put("Painting", "minecraft:painting");
            a.put("Pig", "minecraft:pig");
            a.put("PigZombie", "minecraft:zombie_pigman");
            a.put("PolarBear", "minecraft:polar_bear");
            a.put("PrimedTnt", "minecraft:tnt");
            a.put("Rabbit", "minecraft:rabbit");
            a.put("Sheep", "minecraft:sheep");
            a.put("Shulker", "minecraft:shulker");
            a.put("ShulkerBullet", "minecraft:shulker_bullet");
            a.put("Silverfish", "minecraft:silverfish");
            a.put("Skeleton", "minecraft:skeleton");
            a.put("SkeletonHorse", "minecraft:skeleton_horse");
            a.put("Slime", "minecraft:slime");
            a.put("SmallFireball", "minecraft:small_fireball");
            a.put("SnowMan", "minecraft:snowman");
            a.put("Snowball", "minecraft:snowball");
            a.put("SpectralArrow", "minecraft:spectral_arrow");
            a.put("Spider", "minecraft:spider");
            a.put("Squid", "minecraft:squid");
            a.put("Stray", "minecraft:stray");
            a.put("ThrownEgg", "minecraft:egg");
            a.put("ThrownEnderpearl", "minecraft:ender_pearl");
            a.put("ThrownExpBottle", "minecraft:xp_bottle");
            a.put("ThrownPotion", "minecraft:potion");
            a.put("Villager", "minecraft:villager");
            a.put("VillagerGolem", "minecraft:villager_golem");
            a.put("Witch", "minecraft:witch");
            a.put("WitherBoss", "minecraft:wither");
            a.put("WitherSkeleton", "minecraft:wither_skeleton");
            a.put("WitherSkull", "minecraft:wither_skull");
            a.put("Wolf", "minecraft:wolf");
            a.put("XPOrb", "minecraft:xp_orb");
            a.put("Zombie", "minecraft:zombie");
            a.put("ZombieHorse", "minecraft:zombie_horse");
            a.put("ZombieVillager", "minecraft:zombie_villager");
        }
    }

    private static class DataConverterBanner
    implements DataConverter {
        DataConverterBanner() {
        }

        @Override
        public int getDataVersion() {
            return 804;
        }

        @Override
        public class_2487 convert(class_2487 cmp) {
            class_2487 nbttagcompound2;
            class_2487 nbttagcompound1;
            if ("minecraft:banner".equals(cmp.method_10558("id")) && cmp.method_10573("tag", 10) && (nbttagcompound1 = cmp.method_10562("tag")).method_10573("BlockEntityTag", 10) && (nbttagcompound2 = nbttagcompound1.method_10562("BlockEntityTag")).method_10573("Base", 99)) {
                class_2499 nbttaglist;
                class_2487 nbttagcompound3;
                cmp.method_10575("Damage", (short)(nbttagcompound2.method_10568("Base") & 0xF));
                if (nbttagcompound1.method_10573("display", 10) && (nbttagcompound3 = nbttagcompound1.method_10562("display")).method_10573("Lore", 9) && (nbttaglist = nbttagcompound3.method_10554("Lore", 8)).size() == 1 && "(+NBT)".equals(nbttaglist.method_10608(0))) {
                    return cmp;
                }
                nbttagcompound2.method_10551("Base");
                if (nbttagcompound2.method_33133()) {
                    nbttagcompound1.method_10551("BlockEntityTag");
                }
                if (nbttagcompound1.method_33133()) {
                    cmp.method_10551("tag");
                }
            }
            return cmp;
        }
    }

    private static class DataConverterPotionWater
    implements DataConverter {
        DataConverterPotionWater() {
        }

        @Override
        public int getDataVersion() {
            return 806;
        }

        @Override
        public class_2487 convert(class_2487 cmp) {
            String s = cmp.method_10558("id");
            if ("minecraft:potion".equals(s) || "minecraft:splash_potion".equals(s) || "minecraft:lingering_potion".equals(s) || "minecraft:tipped_arrow".equals(s)) {
                class_2487 nbttagcompound1 = cmp.method_10562("tag");
                if (!nbttagcompound1.method_10573("Potion", 8)) {
                    nbttagcompound1.method_10582("Potion", "minecraft:water");
                }
                if (!cmp.method_10573("tag", 10)) {
                    cmp.method_10566("tag", (class_2520)nbttagcompound1);
                }
            }
            return cmp;
        }
    }

    private static class DataConverterShulker
    implements DataConverter {
        DataConverterShulker() {
        }

        @Override
        public int getDataVersion() {
            return 808;
        }

        @Override
        public class_2487 convert(class_2487 cmp) {
            if ("minecraft:shulker".equals(cmp.method_10558("id")) && !cmp.method_10573("Color", 99)) {
                cmp.method_10567("Color", (byte)10);
            }
            return cmp;
        }
    }

    private static class DataConverterShulkerBoxItem
    implements DataConverter {
        public static final String[] a = new String[]{"minecraft:white_shulker_box", "minecraft:orange_shulker_box", "minecraft:magenta_shulker_box", "minecraft:light_blue_shulker_box", "minecraft:yellow_shulker_box", "minecraft:lime_shulker_box", "minecraft:pink_shulker_box", "minecraft:gray_shulker_box", "minecraft:silver_shulker_box", "minecraft:cyan_shulker_box", "minecraft:purple_shulker_box", "minecraft:blue_shulker_box", "minecraft:brown_shulker_box", "minecraft:green_shulker_box", "minecraft:red_shulker_box", "minecraft:black_shulker_box"};

        DataConverterShulkerBoxItem() {
        }

        @Override
        public int getDataVersion() {
            return 813;
        }

        @Override
        public class_2487 convert(class_2487 cmp) {
            class_2487 nbttagcompound1;
            if ("minecraft:shulker_box".equals(cmp.method_10558("id")) && cmp.method_10573("tag", 10) && (nbttagcompound1 = cmp.method_10562("tag")).method_10573("BlockEntityTag", 10)) {
                class_2487 nbttagcompound2 = nbttagcompound1.method_10562("BlockEntityTag");
                if (nbttagcompound2.method_10554("Items", 10).isEmpty()) {
                    nbttagcompound2.method_10551("Items");
                }
                int i = nbttagcompound2.method_10550("Color");
                nbttagcompound2.method_10551("Color");
                if (nbttagcompound2.method_33133()) {
                    nbttagcompound1.method_10551("BlockEntityTag");
                }
                if (nbttagcompound1.method_33133()) {
                    cmp.method_10551("tag");
                }
                cmp.method_10582("id", a[i % 16]);
            }
            return cmp;
        }
    }

    private static class DataConverterShulkerBoxBlock
    implements DataConverter {
        DataConverterShulkerBoxBlock() {
        }

        @Override
        public int getDataVersion() {
            return 813;
        }

        @Override
        public class_2487 convert(class_2487 cmp) {
            if ("minecraft:shulker".equals(cmp.method_10558("id"))) {
                cmp.method_10551("Color");
            }
            return cmp;
        }
    }

    private static class DataConverterLang
    implements DataConverter {
        DataConverterLang() {
        }

        @Override
        public int getDataVersion() {
            return 816;
        }

        @Override
        public class_2487 convert(class_2487 cmp) {
            if (cmp.method_10573("lang", 8)) {
                cmp.method_10582("lang", cmp.method_10558("lang").toLowerCase(Locale.ROOT));
            }
            return cmp;
        }
    }

    private static class DataConverterTotem
    implements DataConverter {
        DataConverterTotem() {
        }

        @Override
        public int getDataVersion() {
            return 820;
        }

        @Override
        public class_2487 convert(class_2487 cmp) {
            if ("minecraft:totem".equals(cmp.method_10558("id"))) {
                cmp.method_10582("id", "minecraft:totem_of_undying");
            }
            return cmp;
        }
    }

    private static class DataConverterBedBlock
    implements DataConverter {
        private static final Logger a = LogManager.getLogger(FabricDataFixer.class);

        DataConverterBedBlock() {
        }

        @Override
        public int getDataVersion() {
            return 1125;
        }

        @Override
        public class_2487 convert(class_2487 cmp) {
            boolean flag = true;
            try {
                class_2487 nbttagcompound1 = cmp.method_10562("Level");
                int i = nbttagcompound1.method_10550("xPos");
                int j = nbttagcompound1.method_10550("zPos");
                class_2499 nbttaglist = nbttagcompound1.method_10554("TileEntities", 10);
                class_2499 nbttaglist1 = nbttagcompound1.method_10554("Sections", 10);
                for (int k = 0; k < nbttaglist1.size(); ++k) {
                    class_2487 nbttagcompound2 = nbttaglist1.method_10602(k);
                    byte b0 = nbttagcompound2.method_10571("Y");
                    byte[] abyte = nbttagcompound2.method_10547("Blocks");
                    for (int l = 0; l < abyte.length; ++l) {
                        if (416 != (abyte[l] & 0xFF) << 4) continue;
                        int i1 = l & 0xF;
                        int j1 = l >> 8 & 0xF;
                        int k1 = l >> 4 & 0xF;
                        class_2487 nbttagcompound3 = new class_2487();
                        nbttagcompound3.method_10582("id", "bed");
                        nbttagcompound3.method_10569("x", i1 + (i << 4));
                        nbttagcompound3.method_10569("y", j1 + (b0 << 4));
                        nbttagcompound3.method_10569("z", k1 + (j << 4));
                        nbttaglist.add((Object)nbttagcompound3);
                    }
                }
            }
            catch (Exception exception) {
                a.warn("Unable to datafix Bed blocks, level format may be missing tags.");
            }
            return cmp;
        }
    }

    private static class DataConverterBedItem
    implements DataConverter {
        DataConverterBedItem() {
        }

        @Override
        public int getDataVersion() {
            return 1125;
        }

        @Override
        public class_2487 convert(class_2487 cmp) {
            if ("minecraft:bed".equals(cmp.method_10558("id")) && cmp.method_10568("Damage") == 0) {
                cmp.method_10575("Damage", (short)class_1767.field_7964.method_7789());
            }
            return cmp;
        }
    }

    private static abstract class DataInspectorTagged
    implements DataInspector {
        private final class_2960 key;

        DataInspectorTagged(String type) {
            this.key = FabricDataFixer.getKey(type);
        }

        @Override
        public class_2487 inspect(class_2487 cmp, int sourceVer, int targetVer) {
            if (this.key.equals((Object)class_2960.method_60654((String)cmp.method_10558("id")))) {
                cmp = this.inspectChecked(cmp, sourceVer, targetVer);
            }
            return cmp;
        }

        abstract class_2487 inspectChecked(class_2487 var1, int var2, int var3);
    }
}

