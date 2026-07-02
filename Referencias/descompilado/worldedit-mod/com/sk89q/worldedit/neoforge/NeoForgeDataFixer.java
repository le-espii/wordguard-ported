/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
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
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.FloatTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.NbtOps
 *  net.minecraft.nbt.StringTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.Component$Serializer
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.GsonHelper
 *  net.minecraft.util.StringUtil
 *  net.minecraft.util.datafix.DataFixers
 *  net.minecraft.util.datafix.fixes.References
 *  net.minecraft.world.item.DyeColor
 *  net.neoforged.neoforge.server.ServerLifecycleHooks
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.sk89q.worldedit.neoforge;

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
import com.sk89q.worldedit.neoforge.internal.NBTConverter;
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
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.StringUtil;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.enginehub.linbus.tree.LinCompoundTag;

class NeoForgeDataFixer
implements com.sk89q.worldedit.world.DataFixer {
    private static final NbtOps OPS_NBT = NbtOps.INSTANCE;
    private static final int LEGACY_VERSION = 1343;
    private static int DATA_VERSION;
    private static NeoForgeDataFixer INSTANCE;
    private final Map<LegacyType, List<DataConverter>> converters = new EnumMap<LegacyType, List<DataConverter>>(LegacyType.class);
    private final Map<LegacyType, List<DataInspector>> inspectors = new EnumMap<LegacyType, List<DataInspector>>(LegacyType.class);
    private final DataFixer fixer;
    private static final Map<String, LegacyType> DFU_TO_LEGACY;
    private static final Map<String, ResourceLocation> OLD_ID_TO_KEY_MAP;

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

    private LinCompoundTag fixChunk(LinCompoundTag originalChunk, int srcVer) {
        CompoundTag tag = NBTConverter.toNative(originalChunk);
        CompoundTag fixed = NeoForgeDataFixer.convert(LegacyType.CHUNK, tag, srcVer);
        return NBTConverter.fromNative(fixed);
    }

    private LinCompoundTag fixBlockEntity(LinCompoundTag origTileEnt, int srcVer) {
        CompoundTag tag = NBTConverter.toNative(origTileEnt);
        CompoundTag fixed = NeoForgeDataFixer.convert(LegacyType.BLOCK_ENTITY, tag, srcVer);
        return NBTConverter.fromNative(fixed);
    }

    private LinCompoundTag fixEntity(LinCompoundTag origEnt, int srcVer) {
        CompoundTag tag = NBTConverter.toNative(origEnt);
        CompoundTag fixed = NeoForgeDataFixer.convert(LegacyType.ENTITY, tag, srcVer);
        return NBTConverter.fromNative(fixed);
    }

    private String fixBlockState(String blockState, int srcVer) {
        CompoundTag stateNBT = NeoForgeDataFixer.stateToNBT(blockState);
        Dynamic dynamic = new Dynamic((DynamicOps)OPS_NBT, (Object)stateNBT);
        CompoundTag fixed = (CompoundTag)NeoForgeDataFixer.INSTANCE.fixer.update(References.BLOCK_STATE, dynamic, srcVer, DATA_VERSION).getValue();
        return this.nbtToState(fixed);
    }

    private String nbtToState(CompoundTag tagCompound) {
        StringBuilder sb = new StringBuilder();
        sb.append(tagCompound.getString("Name"));
        if (tagCompound.contains("Properties", 10)) {
            sb.append('[');
            CompoundTag props = tagCompound.getCompound("Properties");
            sb.append(props.getAllKeys().stream().map(k -> k + "=" + props.getString(k).replace("\"", "")).collect(Collectors.joining(",")));
            sb.append(']');
        }
        return sb.toString();
    }

    private static CompoundTag stateToNBT(String blockState) {
        int propIdx = blockState.indexOf(91);
        CompoundTag tag = new CompoundTag();
        if (propIdx < 0) {
            tag.putString("Name", blockState);
        } else {
            String[] propArr;
            tag.putString("Name", blockState.substring(0, propIdx));
            CompoundTag propTag = new CompoundTag();
            String props = blockState.substring(propIdx + 1, blockState.length() - 1);
            for (String pair : propArr = props.split(",")) {
                String[] split = pair.split("=");
                propTag.putString(split[0], split[1]);
            }
            tag.put("Properties", (Tag)propTag);
        }
        return tag;
    }

    private String fixBiome(String key, int srcVer) {
        return NeoForgeDataFixer.fixName(key, srcVer, References.BIOME);
    }

    private String fixItemType(String key, int srcVer) {
        return NeoForgeDataFixer.fixName(key, srcVer, References.ITEM_NAME);
    }

    private static String fixName(String key, int srcVer, DSL.TypeReference type) {
        return NeoForgeDataFixer.INSTANCE.fixer.update(type, new Dynamic((DynamicOps)OPS_NBT, (Object)StringTag.valueOf((String)key)), srcVer, DATA_VERSION).asString().result().orElse(key);
    }

    NeoForgeDataFixer(int dataVersion) {
        DATA_VERSION = dataVersion;
        INSTANCE = this;
        this.registerConverters();
        this.registerInspectors();
        this.fixer = new WrappedDataFixer(DataFixers.getDataFixer());
    }

    public static CompoundTag convert(LegacyType type, CompoundTag cmp) {
        return NeoForgeDataFixer.convert(type.getDFUType(), cmp);
    }

    public static CompoundTag convert(LegacyType type, CompoundTag cmp, int sourceVer) {
        return NeoForgeDataFixer.convert(type.getDFUType(), cmp, sourceVer);
    }

    public static CompoundTag convert(LegacyType type, CompoundTag cmp, int sourceVer, int targetVer) {
        return NeoForgeDataFixer.convert(type.getDFUType(), cmp, sourceVer, targetVer);
    }

    public static CompoundTag convert(DSL.TypeReference type, CompoundTag cmp) {
        int i = cmp.contains("DataVersion", 99) ? cmp.getInt("DataVersion") : -1;
        return NeoForgeDataFixer.convert(type, cmp, i);
    }

    public static CompoundTag convert(DSL.TypeReference type, CompoundTag cmp, int sourceVer) {
        return NeoForgeDataFixer.convert(type, cmp, sourceVer, DATA_VERSION);
    }

    public static CompoundTag convert(DSL.TypeReference type, CompoundTag cmp, int sourceVer, int targetVer) {
        if (sourceVer >= targetVer) {
            return cmp;
        }
        return (CompoundTag)NeoForgeDataFixer.INSTANCE.fixer.update(type, new Dynamic((DynamicOps)OPS_NBT, (Object)cmp), sourceVer, targetVer).getValue();
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

    private static ResourceLocation getKey(String type) {
        ResourceLocation key = OLD_ID_TO_KEY_MAP.get(type);
        if (key == null) {
            throw new IllegalArgumentException("Unknown mapping for " + type);
        }
        return key;
    }

    private static void convertCompound(LegacyType type, CompoundTag cmp, String key, int sourceVer, int targetVer) {
        cmp.put(key, (Tag)NeoForgeDataFixer.convert(type, cmp.getCompound(key), sourceVer, targetVer));
    }

    private static void convertItem(CompoundTag nbttagcompound, String key, int sourceVer, int targetVer) {
        if (nbttagcompound.contains(key, 10)) {
            NeoForgeDataFixer.convertCompound(LegacyType.ITEM_INSTANCE, nbttagcompound, key, sourceVer, targetVer);
        }
    }

    private static void convertItems(CompoundTag nbttagcompound, String key, int sourceVer, int targetVer) {
        if (nbttagcompound.contains(key, 9)) {
            ListTag nbttaglist = nbttagcompound.getList(key, 10);
            for (int j = 0; j < nbttaglist.size(); ++j) {
                nbttaglist.add(j, (Tag)NeoForgeDataFixer.convert(LegacyType.ITEM_INSTANCE, nbttaglist.getCompound(j), sourceVer, targetVer));
            }
        }
    }

    static {
        DFU_TO_LEGACY = new HashMap<String, LegacyType>();
        Map<String, ResourceLocation> map = OLD_ID_TO_KEY_MAP = new HashMap<String, ResourceLocation>();
        map.put("EntityItem", ResourceLocation.parse((String)"item"));
        map.put("EntityExperienceOrb", ResourceLocation.parse((String)"xp_orb"));
        map.put("EntityAreaEffectCloud", ResourceLocation.parse((String)"area_effect_cloud"));
        map.put("EntityGuardianElder", ResourceLocation.parse((String)"elder_guardian"));
        map.put("EntitySkeletonWither", ResourceLocation.parse((String)"wither_skeleton"));
        map.put("EntitySkeletonStray", ResourceLocation.parse((String)"stray"));
        map.put("EntityEgg", ResourceLocation.parse((String)"egg"));
        map.put("EntityLeash", ResourceLocation.parse((String)"leash_knot"));
        map.put("EntityPainting", ResourceLocation.parse((String)"painting"));
        map.put("EntityTippedArrow", ResourceLocation.parse((String)"arrow"));
        map.put("EntitySnowball", ResourceLocation.parse((String)"snowball"));
        map.put("EntityLargeFireball", ResourceLocation.parse((String)"fireball"));
        map.put("EntitySmallFireball", ResourceLocation.parse((String)"small_fireball"));
        map.put("EntityEnderPearl", ResourceLocation.parse((String)"ender_pearl"));
        map.put("EntityEnderSignal", ResourceLocation.parse((String)"eye_of_ender_signal"));
        map.put("EntityPotion", ResourceLocation.parse((String)"potion"));
        map.put("EntityThrownExpBottle", ResourceLocation.parse((String)"xp_bottle"));
        map.put("EntityItemFrame", ResourceLocation.parse((String)"item_frame"));
        map.put("EntityWitherSkull", ResourceLocation.parse((String)"wither_skull"));
        map.put("EntityTNTPrimed", ResourceLocation.parse((String)"tnt"));
        map.put("EntityFallingBlock", ResourceLocation.parse((String)"falling_block"));
        map.put("EntityFireworks", ResourceLocation.parse((String)"fireworks_rocket"));
        map.put("EntityZombieHusk", ResourceLocation.parse((String)"husk"));
        map.put("EntitySpectralArrow", ResourceLocation.parse((String)"spectral_arrow"));
        map.put("EntityShulkerBullet", ResourceLocation.parse((String)"shulker_bullet"));
        map.put("EntityDragonFireball", ResourceLocation.parse((String)"dragon_fireball"));
        map.put("EntityZombieVillager", ResourceLocation.parse((String)"zombie_villager"));
        map.put("EntityHorseSkeleton", ResourceLocation.parse((String)"skeleton_horse"));
        map.put("EntityHorseZombie", ResourceLocation.parse((String)"zombie_horse"));
        map.put("EntityArmorStand", ResourceLocation.parse((String)"armor_stand"));
        map.put("EntityHorseDonkey", ResourceLocation.parse((String)"donkey"));
        map.put("EntityHorseMule", ResourceLocation.parse((String)"mule"));
        map.put("EntityEvokerFangs", ResourceLocation.parse((String)"evocation_fangs"));
        map.put("EntityEvoker", ResourceLocation.parse((String)"evocation_illager"));
        map.put("EntityVex", ResourceLocation.parse((String)"vex"));
        map.put("EntityVindicator", ResourceLocation.parse((String)"vindication_illager"));
        map.put("EntityIllagerIllusioner", ResourceLocation.parse((String)"illusion_illager"));
        map.put("EntityMinecartCommandBlock", ResourceLocation.parse((String)"commandblock_minecart"));
        map.put("EntityBoat", ResourceLocation.parse((String)"boat"));
        map.put("EntityMinecartRideable", ResourceLocation.parse((String)"minecart"));
        map.put("EntityMinecartChest", ResourceLocation.parse((String)"chest_minecart"));
        map.put("EntityMinecartFurnace", ResourceLocation.parse((String)"furnace_minecart"));
        map.put("EntityMinecartTNT", ResourceLocation.parse((String)"tnt_minecart"));
        map.put("EntityMinecartHopper", ResourceLocation.parse((String)"hopper_minecart"));
        map.put("EntityMinecartMobSpawner", ResourceLocation.parse((String)"spawner_minecart"));
        map.put("EntityCreeper", ResourceLocation.parse((String)"creeper"));
        map.put("EntitySkeleton", ResourceLocation.parse((String)"skeleton"));
        map.put("EntitySpider", ResourceLocation.parse((String)"spider"));
        map.put("EntityGiantZombie", ResourceLocation.parse((String)"giant"));
        map.put("EntityZombie", ResourceLocation.parse((String)"zombie"));
        map.put("EntitySlime", ResourceLocation.parse((String)"slime"));
        map.put("EntityGhast", ResourceLocation.parse((String)"ghast"));
        map.put("EntityPigZombie", ResourceLocation.parse((String)"zombie_pigman"));
        map.put("EntityEnderman", ResourceLocation.parse((String)"enderman"));
        map.put("EntityCaveSpider", ResourceLocation.parse((String)"cave_spider"));
        map.put("EntitySilverfish", ResourceLocation.parse((String)"silverfish"));
        map.put("EntityBlaze", ResourceLocation.parse((String)"blaze"));
        map.put("EntityMagmaCube", ResourceLocation.parse((String)"magma_cube"));
        map.put("EntityEnderDragon", ResourceLocation.parse((String)"ender_dragon"));
        map.put("EntityWither", ResourceLocation.parse((String)"wither"));
        map.put("EntityBat", ResourceLocation.parse((String)"bat"));
        map.put("EntityWitch", ResourceLocation.parse((String)"witch"));
        map.put("EntityEndermite", ResourceLocation.parse((String)"endermite"));
        map.put("EntityGuardian", ResourceLocation.parse((String)"guardian"));
        map.put("EntityShulker", ResourceLocation.parse((String)"shulker"));
        map.put("EntityPig", ResourceLocation.parse((String)"pig"));
        map.put("EntitySheep", ResourceLocation.parse((String)"sheep"));
        map.put("EntityCow", ResourceLocation.parse((String)"cow"));
        map.put("EntityChicken", ResourceLocation.parse((String)"chicken"));
        map.put("EntitySquid", ResourceLocation.parse((String)"squid"));
        map.put("EntityWolf", ResourceLocation.parse((String)"wolf"));
        map.put("EntityMushroomCow", ResourceLocation.parse((String)"mooshroom"));
        map.put("EntitySnowman", ResourceLocation.parse((String)"snowman"));
        map.put("EntityOcelot", ResourceLocation.parse((String)"ocelot"));
        map.put("EntityIronGolem", ResourceLocation.parse((String)"villager_golem"));
        map.put("EntityHorse", ResourceLocation.parse((String)"horse"));
        map.put("EntityRabbit", ResourceLocation.parse((String)"rabbit"));
        map.put("EntityPolarBear", ResourceLocation.parse((String)"polar_bear"));
        map.put("EntityLlama", ResourceLocation.parse((String)"llama"));
        map.put("EntityLlamaSpit", ResourceLocation.parse((String)"llama_spit"));
        map.put("EntityParrot", ResourceLocation.parse((String)"parrot"));
        map.put("EntityVillager", ResourceLocation.parse((String)"villager"));
        map.put("EntityEnderCrystal", ResourceLocation.parse((String)"ender_crystal"));
        map.put("TileEntityFurnace", ResourceLocation.parse((String)"furnace"));
        map.put("TileEntityChest", ResourceLocation.parse((String)"chest"));
        map.put("TileEntityEnderChest", ResourceLocation.parse((String)"ender_chest"));
        map.put("TileEntityRecordPlayer", ResourceLocation.parse((String)"jukebox"));
        map.put("TileEntityDispenser", ResourceLocation.parse((String)"dispenser"));
        map.put("TileEntityDropper", ResourceLocation.parse((String)"dropper"));
        map.put("TileEntitySign", ResourceLocation.parse((String)"sign"));
        map.put("TileEntityMobSpawner", ResourceLocation.parse((String)"mob_spawner"));
        map.put("TileEntityNote", ResourceLocation.parse((String)"noteblock"));
        map.put("TileEntityPiston", ResourceLocation.parse((String)"piston"));
        map.put("TileEntityBrewingStand", ResourceLocation.parse((String)"brewing_stand"));
        map.put("TileEntityEnchantTable", ResourceLocation.parse((String)"enchanting_table"));
        map.put("TileEntityEnderPortal", ResourceLocation.parse((String)"end_portal"));
        map.put("TileEntityBeacon", ResourceLocation.parse((String)"beacon"));
        map.put("TileEntitySkull", ResourceLocation.parse((String)"skull"));
        map.put("TileEntityLightDetector", ResourceLocation.parse((String)"daylight_detector"));
        map.put("TileEntityHopper", ResourceLocation.parse((String)"hopper"));
        map.put("TileEntityComparator", ResourceLocation.parse((String)"comparator"));
        map.put("TileEntityFlowerPot", ResourceLocation.parse((String)"flower_pot"));
        map.put("TileEntityBanner", ResourceLocation.parse((String)"banner"));
        map.put("TileEntityStructure", ResourceLocation.parse((String)"structure_block"));
        map.put("TileEntityEndGateway", ResourceLocation.parse((String)"end_gateway"));
        map.put("TileEntityCommand", ResourceLocation.parse((String)"command_block"));
        map.put("TileEntityShulkerBox", ResourceLocation.parse((String)"shulker_box"));
        map.put("TileEntityBed", ResourceLocation.parse((String)"bed"));
    }

    public static enum LegacyType {
        LEVEL(References.LEVEL),
        PLAYER(References.PLAYER),
        CHUNK(References.CHUNK),
        BLOCK_ENTITY(References.BLOCK_ENTITY),
        ENTITY(References.ENTITY),
        ITEM_INSTANCE(References.ITEM_STACK),
        OPTIONS(References.OPTIONS),
        STRUCTURE(References.STRUCTURE);

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
                CompoundTag cmp = (CompoundTag)dynamic.getValue();
                int desiredVersion = Math.min(targetVer, 1343);
                cmp = this.convert(legacyType, cmp, sourceVer, desiredVersion);
                sourceVer = desiredVersion;
                dynamic = new Dynamic((DynamicOps)OPS_NBT, (Object)cmp);
            }
            return this.realFixer.update(type, dynamic, sourceVer, targetVer);
        }

        private CompoundTag convert(LegacyType type, CompoundTag cmp, int sourceVer, int desiredVersion) {
            List<DataInspector> inspectors;
            List<DataConverter> converters = NeoForgeDataFixer.this.converters.get((Object)type);
            if (converters != null && !converters.isEmpty()) {
                for (DataConverter converter : converters) {
                    int dataVersion = converter.getDataVersion();
                    if (dataVersion <= sourceVer || dataVersion > desiredVersion) continue;
                    cmp = converter.convert(cmp);
                }
            }
            if ((inspectors = NeoForgeDataFixer.this.inspectors.get((Object)type)) != null && !inspectors.isEmpty()) {
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

        public CompoundTag convert(CompoundTag var1);
    }

    private static class DataInspectorItem
    extends DataInspectorTagged {
        private final String[] keys;

        DataInspectorItem(String oclass, String ... astring) {
            super(oclass);
            this.keys = astring;
        }

        @Override
        CompoundTag inspectChecked(CompoundTag nbttagcompound, int sourceVer, int targetVer) {
            for (String key : this.keys) {
                NeoForgeDataFixer.convertItem(nbttagcompound, key, sourceVer, targetVer);
            }
            return nbttagcompound;
        }
    }

    public static interface DataInspector {
        public CompoundTag inspect(CompoundTag var1, int var2, int var3);
    }

    private static class DataInspectorItemList
    extends DataInspectorTagged {
        private final String[] keys;

        DataInspectorItemList(String oclass, String ... astring) {
            super(oclass);
            this.keys = astring;
        }

        @Override
        CompoundTag inspectChecked(CompoundTag nbttagcompound, int sourceVer, int targetVer) {
            for (String s : this.keys) {
                NeoForgeDataFixer.convertItems(nbttagcompound, s, sourceVer, targetVer);
            }
            return nbttagcompound;
        }
    }

    private static class DataInspectorMobSpawnerMobs
    implements DataInspector {
        ResourceLocation tileEntityMobSpawner = NeoForgeDataFixer.getKey("TileEntityMobSpawner");

        private DataInspectorMobSpawnerMobs() {
        }

        @Override
        public CompoundTag inspect(CompoundTag cmp, int sourceVer, int targetVer) {
            if (this.tileEntityMobSpawner.equals((Object)ResourceLocation.parse((String)cmp.getString("id")))) {
                if (cmp.contains("SpawnPotentials", 9)) {
                    ListTag nbttaglist = cmp.getList("SpawnPotentials", 10);
                    for (int j = 0; j < nbttaglist.size(); ++j) {
                        CompoundTag nbttagcompound1 = nbttaglist.getCompound(j);
                        NeoForgeDataFixer.convertCompound(LegacyType.ENTITY, nbttagcompound1, "Entity", sourceVer, targetVer);
                    }
                }
                NeoForgeDataFixer.convertCompound(LegacyType.ENTITY, cmp, "SpawnData", sourceVer, targetVer);
            }
            return cmp;
        }
    }

    private static class DataInspectorChunks
    implements DataInspector {
        private DataInspectorChunks() {
        }

        @Override
        public CompoundTag inspect(CompoundTag cmp, int sourceVer, int targetVer) {
            if (cmp.contains("Level", 10)) {
                int j;
                ListTag nbttaglist;
                CompoundTag nbttagcompound1 = cmp.getCompound("Level");
                if (nbttagcompound1.contains("Entities", 9)) {
                    nbttaglist = nbttagcompound1.getList("Entities", 10);
                    for (j = 0; j < nbttaglist.size(); ++j) {
                        nbttaglist.set(j, (Tag)NeoForgeDataFixer.convert(LegacyType.ENTITY, (CompoundTag)nbttaglist.get(j), sourceVer, targetVer));
                    }
                }
                if (nbttagcompound1.contains("TileEntities", 9)) {
                    nbttaglist = nbttagcompound1.getList("TileEntities", 10);
                    for (j = 0; j < nbttaglist.size(); ++j) {
                        nbttaglist.set(j, (Tag)NeoForgeDataFixer.convert(LegacyType.BLOCK_ENTITY, (CompoundTag)nbttaglist.get(j), sourceVer, targetVer));
                    }
                }
            }
            return cmp;
        }
    }

    private static class DataInspectorCommandBlock
    implements DataInspector {
        ResourceLocation tileEntityCommand = NeoForgeDataFixer.getKey("TileEntityCommand");

        private DataInspectorCommandBlock() {
        }

        @Override
        public CompoundTag inspect(CompoundTag cmp, int sourceVer, int targetVer) {
            if (this.tileEntityCommand.equals((Object)ResourceLocation.parse((String)cmp.getString("id")))) {
                cmp.putString("id", "Control");
                NeoForgeDataFixer.convert(LegacyType.BLOCK_ENTITY, cmp, sourceVer, targetVer);
                cmp.putString("id", "MinecartCommandBlock");
            }
            return cmp;
        }
    }

    private static class DataInspectorEntityPassengers
    implements DataInspector {
        private DataInspectorEntityPassengers() {
        }

        @Override
        public CompoundTag inspect(CompoundTag cmp, int sourceVer, int targetVer) {
            if (cmp.contains("Passengers", 9)) {
                ListTag nbttaglist = cmp.getList("Passengers", 10);
                for (int j = 0; j < nbttaglist.size(); ++j) {
                    nbttaglist.set(j, (Tag)NeoForgeDataFixer.convert(LegacyType.ENTITY, nbttaglist.getCompound(j), sourceVer, targetVer));
                }
            }
            return cmp;
        }
    }

    private static class DataInspectorMobSpawnerMinecart
    implements DataInspector {
        ResourceLocation entityMinecartMobSpawner = NeoForgeDataFixer.getKey("EntityMinecartMobSpawner");
        ResourceLocation tileEntityMobSpawner = NeoForgeDataFixer.getKey("TileEntityMobSpawner");

        private DataInspectorMobSpawnerMinecart() {
        }

        @Override
        public CompoundTag inspect(CompoundTag cmp, int sourceVer, int targetVer) {
            String s = cmp.getString("id");
            if (this.entityMinecartMobSpawner.equals((Object)ResourceLocation.parse((String)s))) {
                cmp.putString("id", this.tileEntityMobSpawner.toString());
                NeoForgeDataFixer.convert(LegacyType.BLOCK_ENTITY, cmp, sourceVer, targetVer);
                cmp.putString("id", s);
            }
            return cmp;
        }
    }

    private static class DataInspectorVillagers
    implements DataInspector {
        ResourceLocation entityVillager = NeoForgeDataFixer.getKey("EntityVillager");

        private DataInspectorVillagers() {
        }

        @Override
        public CompoundTag inspect(CompoundTag cmp, int sourceVer, int targetVer) {
            CompoundTag nbttagcompound1;
            if (this.entityVillager.equals((Object)ResourceLocation.parse((String)cmp.getString("id"))) && cmp.contains("Offers", 10) && (nbttagcompound1 = cmp.getCompound("Offers")).contains("Recipes", 9)) {
                ListTag nbttaglist = nbttagcompound1.getList("Recipes", 10);
                for (int j = 0; j < nbttaglist.size(); ++j) {
                    CompoundTag nbttagcompound2 = nbttaglist.getCompound(j);
                    NeoForgeDataFixer.convertItem(nbttagcompound2, "buy", sourceVer, targetVer);
                    NeoForgeDataFixer.convertItem(nbttagcompound2, "buyB", sourceVer, targetVer);
                    NeoForgeDataFixer.convertItem(nbttagcompound2, "sell", sourceVer, targetVer);
                    nbttaglist.set(j, (Tag)nbttagcompound2);
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
            String key = ResourceLocation.parse((String)s).toString();
            if (i < 515 && b.containsKey(key)) {
                return b.get(key);
            }
            return c.get(key);
        }

        @Override
        public CompoundTag inspect(CompoundTag cmp, int sourceVer, int targetVer) {
            if (!cmp.contains("tag", 10)) {
                return cmp;
            }
            CompoundTag nbttagcompound1 = cmp.getCompound("tag");
            if (nbttagcompound1.contains("BlockEntityTag", 10)) {
                boolean flag;
                CompoundTag nbttagcompound2 = nbttagcompound1.getCompound("BlockEntityTag");
                String s = cmp.getString("id");
                String s1 = DataInspectorBlockEntity.convertEntityId(sourceVer, s);
                if (s1 == null) {
                    flag = false;
                } else {
                    flag = !nbttagcompound2.contains("id");
                    nbttagcompound2.putString("id", s1);
                }
                NeoForgeDataFixer.convert(LegacyType.BLOCK_ENTITY, nbttagcompound2, sourceVer, targetVer);
                if (flag) {
                    nbttagcompound2.remove("id");
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
        private static final Logger a = LogManager.getLogger(NeoForgeDataFixer.class);

        DataInspectorEntity() {
        }

        @Override
        public CompoundTag inspect(CompoundTag cmp, int sourceVer, int targetVer) {
            CompoundTag nbttagcompound1 = cmp.getCompound("tag");
            if (nbttagcompound1.contains("EntityTag", 10)) {
                boolean flag;
                String s1;
                CompoundTag nbttagcompound2 = nbttagcompound1.getCompound("EntityTag");
                String s = cmp.getString("id");
                if ("minecraft:armor_stand".equals(s)) {
                    s1 = sourceVer < 515 ? "ArmorStand" : "minecraft:armor_stand";
                } else {
                    if (!"minecraft:spawn_egg".equals(s)) {
                        return cmp;
                    }
                    s1 = nbttagcompound2.getString("id");
                }
                if (s1 == null) {
                    a.warn("Unable to resolve Entity for ItemInstance: {}", (Object)s);
                    flag = false;
                } else {
                    flag = !nbttagcompound2.contains("id", 8);
                    nbttagcompound2.putString("id", s1);
                }
                NeoForgeDataFixer.convert(LegacyType.ENTITY, nbttagcompound2, sourceVer, targetVer);
                if (flag) {
                    nbttagcompound2.remove("id");
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
        public CompoundTag inspect(CompoundTag cmp, int sourceVer, int targetVer) {
            if (cmp.contains("Player", 10)) {
                NeoForgeDataFixer.convertCompound(LegacyType.PLAYER, cmp, "Player", sourceVer, targetVer);
            }
            return cmp;
        }
    }

    private static class DataInspectorPlayer
    implements DataInspector {
        private DataInspectorPlayer() {
        }

        @Override
        public CompoundTag inspect(CompoundTag cmp, int sourceVer, int targetVer) {
            NeoForgeDataFixer.convertItems(cmp, "Inventory", sourceVer, targetVer);
            NeoForgeDataFixer.convertItems(cmp, "EnderItems", sourceVer, targetVer);
            if (cmp.contains("ShoulderEntityLeft", 10)) {
                NeoForgeDataFixer.convertCompound(LegacyType.ENTITY, cmp, "ShoulderEntityLeft", sourceVer, targetVer);
            }
            if (cmp.contains("ShoulderEntityRight", 10)) {
                NeoForgeDataFixer.convertCompound(LegacyType.ENTITY, cmp, "ShoulderEntityRight", sourceVer, targetVer);
            }
            return cmp;
        }
    }

    private static class DataInspectorPlayerVehicle
    implements DataInspector {
        private DataInspectorPlayerVehicle() {
        }

        @Override
        public CompoundTag inspect(CompoundTag cmp, int sourceVer, int targetVer) {
            CompoundTag nbttagcompound1;
            if (cmp.contains("RootVehicle", 10) && (nbttagcompound1 = cmp.getCompound("RootVehicle")).contains("Entity", 10)) {
                NeoForgeDataFixer.convertCompound(LegacyType.ENTITY, nbttagcompound1, "Entity", sourceVer, targetVer);
            }
            return cmp;
        }
    }

    private static class DataInspectorStructure
    implements DataInspector {
        private DataInspectorStructure() {
        }

        @Override
        public CompoundTag inspect(CompoundTag cmp, int sourceVer, int targetVer) {
            CompoundTag nbttagcompound1;
            int j;
            ListTag nbttaglist;
            if (cmp.contains("entities", 9)) {
                nbttaglist = cmp.getList("entities", 10);
                for (j = 0; j < nbttaglist.size(); ++j) {
                    nbttagcompound1 = (CompoundTag)nbttaglist.get(j);
                    if (!nbttagcompound1.contains("nbt", 10)) continue;
                    NeoForgeDataFixer.convertCompound(LegacyType.ENTITY, nbttagcompound1, "nbt", sourceVer, targetVer);
                }
            }
            if (cmp.contains("blocks", 9)) {
                nbttaglist = cmp.getList("blocks", 10);
                for (j = 0; j < nbttaglist.size(); ++j) {
                    nbttagcompound1 = (CompoundTag)nbttaglist.get(j);
                    if (!nbttagcompound1.contains("nbt", 10)) continue;
                    NeoForgeDataFixer.convertCompound(LegacyType.BLOCK_ENTITY, nbttagcompound1, "nbt", sourceVer, targetVer);
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
        public CompoundTag convert(CompoundTag cmp) {
            ListTag nbttaglist1;
            ListTag nbttaglist = cmp.getList("Equipment", 10);
            if (!nbttaglist.isEmpty() && !cmp.contains("HandItems", 10)) {
                nbttaglist1 = new ListTag();
                nbttaglist1.add((Object)nbttaglist.get(0));
                nbttaglist1.add((Object)new CompoundTag());
                cmp.put("HandItems", (Tag)nbttaglist1);
            }
            if (nbttaglist.size() > 1 && !cmp.contains("ArmorItem", 10)) {
                nbttaglist1 = new ListTag();
                nbttaglist1.add((Object)nbttaglist.get(1));
                nbttaglist1.add((Object)nbttaglist.get(2));
                nbttaglist1.add((Object)nbttaglist.get(3));
                nbttaglist1.add((Object)nbttaglist.get(4));
                cmp.put("ArmorItems", (Tag)nbttaglist1);
            }
            cmp.remove("Equipment");
            if (cmp.contains("DropChances", 9)) {
                ListTag nbttaglist2;
                nbttaglist1 = cmp.getList("DropChances", 5);
                if (!cmp.contains("HandDropChances", 10)) {
                    nbttaglist2 = new ListTag();
                    nbttaglist2.add((Object)FloatTag.valueOf((float)nbttaglist1.getFloat(0)));
                    nbttaglist2.add((Object)FloatTag.valueOf((float)0.0f));
                    cmp.put("HandDropChances", (Tag)nbttaglist2);
                }
                if (!cmp.contains("ArmorDropChances", 10)) {
                    nbttaglist2 = new ListTag();
                    nbttaglist2.add((Object)FloatTag.valueOf((float)nbttaglist1.getFloat(1)));
                    nbttaglist2.add((Object)FloatTag.valueOf((float)nbttaglist1.getFloat(2)));
                    nbttaglist2.add((Object)FloatTag.valueOf((float)nbttaglist1.getFloat(3)));
                    nbttaglist2.add((Object)FloatTag.valueOf((float)nbttaglist1.getFloat(4)));
                    cmp.put("ArmorDropChances", (Tag)nbttaglist2);
                }
                cmp.remove("DropChances");
            }
            return cmp;
        }
    }

    private static class DataConverterSignText
    implements DataConverter {
        public static final Gson a = new GsonBuilder().registerTypeAdapter(Component.class, (Object)new JsonDeserializer(){

            MutableComponent a(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
                if (jsonelement.isJsonPrimitive()) {
                    return Component.literal((String)jsonelement.getAsString());
                }
                if (jsonelement.isJsonArray()) {
                    JsonArray jsonarray = jsonelement.getAsJsonArray();
                    MutableComponent iTextComponent = null;
                    for (JsonElement jsonelement1 : jsonarray) {
                        MutableComponent iTextComponent1 = this.a(jsonelement1, jsonelement1.getClass(), jsondeserializationcontext);
                        if (iTextComponent == null) {
                            iTextComponent = iTextComponent1;
                            continue;
                        }
                        iTextComponent.append((Component)iTextComponent1);
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
        public CompoundTag convert(CompoundTag cmp) {
            if ("Sign".equals(cmp.getString("id"))) {
                this.convert(cmp, "Text1");
                this.convert(cmp, "Text2");
                this.convert(cmp, "Text3");
                this.convert(cmp, "Text4");
            }
            return cmp;
        }

        private void convert(CompoundTag nbttagcompound, String s) {
            String s1 = nbttagcompound.getString(s);
            Object object = null;
            if (!"null".equals(s1) && !StringUtil.isNullOrEmpty((String)s1)) {
                if (!(s1.charAt(0) == '\"' && s1.charAt(s1.length() - 1) == '\"' || s1.charAt(0) == '{' && s1.charAt(s1.length() - 1) == '}')) {
                    object = Component.literal((String)s1);
                } else {
                    try {
                        object = GsonHelper.fromJson((Gson)a, (String)s1, Component.class, (boolean)true);
                        if (object == null) {
                            object = Component.literal((String)"");
                        }
                    }
                    catch (JsonParseException jsonParseException) {
                        // empty catch block
                    }
                    if (object == null) {
                        try {
                            object = Component.Serializer.fromJson((String)s1, (HolderLookup.Provider)ServerLifecycleHooks.getCurrentServer().registryAccess());
                        }
                        catch (JsonParseException jsonParseException) {
                            // empty catch block
                        }
                    }
                    if (object == null) {
                        try {
                            object = Component.Serializer.fromJsonLenient((String)s1, (HolderLookup.Provider)ServerLifecycleHooks.getCurrentServer().registryAccess());
                        }
                        catch (JsonParseException jsonParseException) {
                            // empty catch block
                        }
                    }
                    if (object == null) {
                        object = Component.literal((String)s1);
                    }
                }
            } else {
                object = Component.literal((String)"");
            }
            nbttagcompound.putString(s, Component.Serializer.toJson((Component)((Component)object), (HolderLookup.Provider)ServerLifecycleHooks.getCurrentServer().registryAccess()));
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
        public CompoundTag convert(CompoundTag cmp) {
            short short0;
            if (cmp.contains("id", 99) && (short0 = cmp.getShort("id")) > 0 && short0 < materials.length && materials[short0] != null) {
                cmp.putString("id", materials[short0]);
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
        public CompoundTag convert(CompoundTag cmp) {
            if ("minecraft:potion".equals(cmp.getString("id"))) {
                CompoundTag nbttagcompound1 = cmp.getCompound("tag");
                short short0 = cmp.getShort("Damage");
                if (!nbttagcompound1.contains("Potion", 8)) {
                    String s = potions[short0 & 0x7F];
                    nbttagcompound1.putString("Potion", s == null ? "minecraft:water" : s);
                    cmp.put("tag", (Tag)nbttagcompound1);
                    if ((short0 & 0x4000) == 16384) {
                        cmp.putString("id", "minecraft:splash_potion");
                    }
                }
                if (short0 != 0) {
                    cmp.putShort("Damage", (short)0);
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
        public CompoundTag convert(CompoundTag cmp) {
            if ("minecraft:spawn_egg".equals(cmp.getString("id"))) {
                String s;
                CompoundTag nbttagcompound1 = cmp.getCompound("tag");
                CompoundTag nbttagcompound2 = nbttagcompound1.getCompound("EntityTag");
                short short0 = cmp.getShort("Damage");
                if (!nbttagcompound2.contains("id", 8) && (s = eggs[short0 & 0xFF]) != null) {
                    nbttagcompound2.putString("id", s);
                    nbttagcompound1.put("EntityTag", (Tag)nbttagcompound2);
                    cmp.put("tag", (Tag)nbttagcompound1);
                }
                if (short0 != 0) {
                    cmp.putShort("Damage", (short)0);
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
        public CompoundTag convert(CompoundTag cmp) {
            if ("Minecart".equals(cmp.getString("id"))) {
                String s = "MinecartRideable";
                int i = cmp.getInt("Type");
                if (i > 0 && i < a.size()) {
                    s = a.get(i);
                }
                cmp.putString("id", s);
                cmp.remove("Type");
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
        public CompoundTag convert(CompoundTag cmp) {
            if (!"MobSpawner".equals(cmp.getString("id"))) {
                return cmp;
            }
            if (cmp.contains("EntityId", 8)) {
                String s = cmp.getString("EntityId");
                CompoundTag nbttagcompound1 = cmp.getCompound("SpawnData");
                nbttagcompound1.putString("id", s.isEmpty() ? "Pig" : s);
                cmp.put("SpawnData", (Tag)nbttagcompound1);
                cmp.remove("EntityId");
            }
            if (cmp.contains("SpawnPotentials", 9)) {
                ListTag nbttaglist = cmp.getList("SpawnPotentials", 10);
                for (int i = 0; i < nbttaglist.size(); ++i) {
                    CompoundTag nbttagcompound2 = nbttaglist.getCompound(i);
                    if (!nbttagcompound2.contains("Type", 8)) continue;
                    CompoundTag nbttagcompound3 = nbttagcompound2.getCompound("Properties");
                    nbttagcompound3.putString("id", nbttagcompound2.getString("Type"));
                    nbttagcompound2.put("Entity", (Tag)nbttagcompound3);
                    nbttagcompound2.remove("Type");
                    nbttagcompound2.remove("Properties");
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
        public CompoundTag convert(CompoundTag cmp) {
            if (cmp.contains("UUID", 8)) {
                cmp.putUUID("UUID", UUID.fromString(cmp.getString("UUID")));
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
        public CompoundTag convert(CompoundTag cmp) {
            if (a.contains(cmp.getString("id"))) {
                float f;
                if (cmp.contains("HealF", 99)) {
                    f = cmp.getFloat("HealF");
                    cmp.remove("HealF");
                } else {
                    if (!cmp.contains("Health", 99)) {
                        return cmp;
                    }
                    f = cmp.getFloat("Health");
                }
                cmp.putFloat("Health", f);
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
        public CompoundTag convert(CompoundTag cmp) {
            if ("EntityHorse".equals(cmp.getString("id")) && !cmp.contains("SaddleItem", 10) && cmp.getBoolean("Saddle")) {
                CompoundTag nbttagcompound1 = new CompoundTag();
                nbttagcompound1.putString("id", "minecraft:saddle");
                nbttagcompound1.putByte("Count", (byte)1);
                nbttagcompound1.putShort("Damage", (short)0);
                cmp.put("SaddleItem", (Tag)nbttagcompound1);
                cmp.remove("Saddle");
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
        public CompoundTag convert(CompoundTag cmp) {
            String s = cmp.getString("id");
            boolean flag = "Painting".equals(s);
            boolean flag1 = "ItemFrame".equals(s);
            if ((flag || flag1) && !cmp.contains("Facing", 99)) {
                Direction enumdirection;
                if (cmp.contains("Direction", 99)) {
                    enumdirection = Direction.from2DDataValue((int)cmp.getByte("Direction"));
                    cmp.putInt("TileX", cmp.getInt("TileX") + enumdirection.getStepX());
                    cmp.putInt("TileY", cmp.getInt("TileY") + enumdirection.getStepY());
                    cmp.putInt("TileZ", cmp.getInt("TileZ") + enumdirection.getStepZ());
                    cmp.remove("Direction");
                    if (flag1 && cmp.contains("ItemRotation", 99)) {
                        cmp.putByte("ItemRotation", (byte)(cmp.getByte("ItemRotation") * 2));
                    }
                } else {
                    enumdirection = Direction.from2DDataValue((int)cmp.getByte("Dir"));
                    cmp.remove("Dir");
                }
                cmp.putByte("Facing", (byte)enumdirection.get2DDataValue());
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
        public CompoundTag convert(CompoundTag cmp) {
            ListTag nbttaglist;
            if (cmp.contains("HandDropChances", 9) && (nbttaglist = cmp.getList("HandDropChances", 5)).size() == 2 && nbttaglist.getFloat(0) == 0.0f && nbttaglist.getFloat(1) == 0.0f) {
                cmp.remove("HandDropChances");
            }
            if (cmp.contains("ArmorDropChances", 9) && (nbttaglist = cmp.getList("ArmorDropChances", 5)).size() == 4 && nbttaglist.getFloat(0) == 0.0f && nbttaglist.getFloat(1) == 0.0f && nbttaglist.getFloat(2) == 0.0f && nbttaglist.getFloat(3) == 0.0f) {
                cmp.remove("ArmorDropChances");
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
        public CompoundTag convert(CompoundTag cmp) {
            while (cmp.contains("Riding", 10)) {
                CompoundTag nbttagcompound1 = this.b(cmp);
                this.convert(cmp, nbttagcompound1);
                cmp = nbttagcompound1;
            }
            return cmp;
        }

        protected void convert(CompoundTag nbttagcompound, CompoundTag nbttagcompound1) {
            ListTag nbttaglist = new ListTag();
            nbttaglist.add((Object)nbttagcompound);
            nbttagcompound1.put("Passengers", (Tag)nbttaglist);
        }

        protected CompoundTag b(CompoundTag nbttagcompound) {
            CompoundTag nbttagcompound1 = nbttagcompound.getCompound("Riding");
            nbttagcompound.remove("Riding");
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
        public CompoundTag convert(CompoundTag cmp) {
            if ("ArmorStand".equals(cmp.getString("id")) && cmp.getBoolean("Silent") && !cmp.getBoolean("Marker")) {
                cmp.remove("Silent");
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
        public CompoundTag convert(CompoundTag cmp) {
            CompoundTag nbttagcompound1;
            if ("minecraft:written_book".equals(cmp.getString("id")) && (nbttagcompound1 = cmp.getCompound("tag")).contains("pages", 9)) {
                ListTag nbttaglist = nbttagcompound1.getList("pages", 8);
                for (int i = 0; i < nbttaglist.size(); ++i) {
                    String s = nbttaglist.getString(i);
                    Object object = null;
                    if (!"null".equals(s) && !StringUtil.isNullOrEmpty((String)s)) {
                        if (!(s.charAt(0) == '\"' && s.charAt(s.length() - 1) == '\"' || s.charAt(0) == '{' && s.charAt(s.length() - 1) == '}')) {
                            object = Component.literal((String)s);
                        } else {
                            try {
                                object = GsonHelper.fromJson((Gson)DataConverterSignText.a, (String)s, Component.class, (boolean)true);
                                if (object == null) {
                                    object = Component.literal((String)"");
                                }
                            }
                            catch (JsonParseException jsonParseException) {
                                // empty catch block
                            }
                            if (object == null) {
                                try {
                                    object = Component.Serializer.fromJson((String)s, (HolderLookup.Provider)ServerLifecycleHooks.getCurrentServer().registryAccess());
                                }
                                catch (JsonParseException jsonParseException) {
                                    // empty catch block
                                }
                            }
                            if (object == null) {
                                try {
                                    object = Component.Serializer.fromJsonLenient((String)s, (HolderLookup.Provider)ServerLifecycleHooks.getCurrentServer().registryAccess());
                                }
                                catch (JsonParseException jsonParseException) {
                                    // empty catch block
                                }
                            }
                            if (object == null) {
                                object = Component.literal((String)s);
                            }
                        }
                    } else {
                        object = Component.literal((String)"");
                    }
                    nbttaglist.set(i, (Tag)StringTag.valueOf((String)Component.Serializer.toJson((Component)((Component)object), (HolderLookup.Provider)ServerLifecycleHooks.getCurrentServer().registryAccess())));
                }
                nbttagcompound1.put("pages", (Tag)nbttaglist);
            }
            return cmp;
        }
    }

    private static class DataConverterCookedFish
    implements DataConverter {
        private static final ResourceLocation a = ResourceLocation.parse((String)"cooked_fished");

        DataConverterCookedFish() {
        }

        @Override
        public int getDataVersion() {
            return 502;
        }

        @Override
        public CompoundTag convert(CompoundTag cmp) {
            if (cmp.contains("id", 8) && a.equals((Object)ResourceLocation.parse((String)cmp.getString("id")))) {
                cmp.putString("id", "minecraft:cooked_fish");
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
        public CompoundTag convert(CompoundTag cmp) {
            if ("Zombie".equals(cmp.getString("id")) && cmp.getBoolean("IsVillager")) {
                if (!cmp.contains("ZombieType", 99)) {
                    int i = -1;
                    if (cmp.contains("VillagerProfession", 99)) {
                        try {
                            i = this.convert(cmp.getInt("VillagerProfession"));
                        }
                        catch (RuntimeException runtimeException) {
                            // empty catch block
                        }
                    }
                    if (i == -1) {
                        i = this.convert(a.nextInt(6));
                    }
                    cmp.putInt("ZombieType", i);
                }
                cmp.remove("IsVillager");
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
        public CompoundTag convert(CompoundTag cmp) {
            cmp.putString("useVbo", "true");
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
        public CompoundTag convert(CompoundTag cmp) {
            if ("Guardian".equals(cmp.getString("id"))) {
                if (cmp.getBoolean("Elder")) {
                    cmp.putString("id", "ElderGuardian");
                }
                cmp.remove("Elder");
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
        public CompoundTag convert(CompoundTag cmp) {
            String s = cmp.getString("id");
            if ("Skeleton".equals(s)) {
                int i = cmp.getInt("SkeletonType");
                if (i == 1) {
                    cmp.putString("id", "WitherSkeleton");
                } else if (i == 2) {
                    cmp.putString("id", "Stray");
                }
                cmp.remove("SkeletonType");
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
        public CompoundTag convert(CompoundTag cmp) {
            if ("Zombie".equals(cmp.getString("id"))) {
                int i = cmp.getInt("ZombieType");
                switch (i) {
                    case 1: 
                    case 2: 
                    case 3: 
                    case 4: 
                    case 5: {
                        cmp.putString("id", "ZombieVillager");
                        cmp.putInt("Profession", i - 1);
                        break;
                    }
                    case 6: {
                        cmp.putString("id", "Husk");
                    }
                }
                cmp.remove("ZombieType");
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
        public CompoundTag convert(CompoundTag cmp) {
            if ("EntityHorse".equals(cmp.getString("id"))) {
                int i = cmp.getInt("Type");
                switch (i) {
                    case 1: {
                        cmp.putString("id", "Donkey");
                        break;
                    }
                    case 2: {
                        cmp.putString("id", "Mule");
                        break;
                    }
                    case 3: {
                        cmp.putString("id", "ZombieHorse");
                        break;
                    }
                    case 4: {
                        cmp.putString("id", "SkeletonHorse");
                        break;
                    }
                    default: {
                        cmp.putString("id", "Horse");
                    }
                }
                cmp.remove("Type");
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
        public CompoundTag convert(CompoundTag cmp) {
            String s = a.get(cmp.getString("id"));
            if (s != null) {
                cmp.putString("id", s);
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
        public CompoundTag convert(CompoundTag cmp) {
            String s = a.get(cmp.getString("id"));
            if (s != null) {
                cmp.putString("id", s);
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
        public CompoundTag convert(CompoundTag cmp) {
            CompoundTag nbttagcompound2;
            CompoundTag nbttagcompound1;
            if ("minecraft:banner".equals(cmp.getString("id")) && cmp.contains("tag", 10) && (nbttagcompound1 = cmp.getCompound("tag")).contains("BlockEntityTag", 10) && (nbttagcompound2 = nbttagcompound1.getCompound("BlockEntityTag")).contains("Base", 99)) {
                ListTag nbttaglist;
                CompoundTag nbttagcompound3;
                cmp.putShort("Damage", (short)(nbttagcompound2.getShort("Base") & 0xF));
                if (nbttagcompound1.contains("display", 10) && (nbttagcompound3 = nbttagcompound1.getCompound("display")).contains("Lore", 9) && (nbttaglist = nbttagcompound3.getList("Lore", 8)).size() == 1 && "(+NBT)".equals(nbttaglist.getString(0))) {
                    return cmp;
                }
                nbttagcompound2.remove("Base");
                if (nbttagcompound2.isEmpty()) {
                    nbttagcompound1.remove("BlockEntityTag");
                }
                if (nbttagcompound1.isEmpty()) {
                    cmp.remove("tag");
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
        public CompoundTag convert(CompoundTag cmp) {
            String s = cmp.getString("id");
            if ("minecraft:potion".equals(s) || "minecraft:splash_potion".equals(s) || "minecraft:lingering_potion".equals(s) || "minecraft:tipped_arrow".equals(s)) {
                CompoundTag nbttagcompound1 = cmp.getCompound("tag");
                if (!nbttagcompound1.contains("Potion", 8)) {
                    nbttagcompound1.putString("Potion", "minecraft:water");
                }
                if (!cmp.contains("tag", 10)) {
                    cmp.put("tag", (Tag)nbttagcompound1);
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
        public CompoundTag convert(CompoundTag cmp) {
            if ("minecraft:shulker".equals(cmp.getString("id")) && !cmp.contains("Color", 99)) {
                cmp.putByte("Color", (byte)10);
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
        public CompoundTag convert(CompoundTag cmp) {
            CompoundTag nbttagcompound1;
            if ("minecraft:shulker_box".equals(cmp.getString("id")) && cmp.contains("tag", 10) && (nbttagcompound1 = cmp.getCompound("tag")).contains("BlockEntityTag", 10)) {
                CompoundTag nbttagcompound2 = nbttagcompound1.getCompound("BlockEntityTag");
                if (nbttagcompound2.getList("Items", 10).isEmpty()) {
                    nbttagcompound2.remove("Items");
                }
                int i = nbttagcompound2.getInt("Color");
                nbttagcompound2.remove("Color");
                if (nbttagcompound2.isEmpty()) {
                    nbttagcompound1.remove("BlockEntityTag");
                }
                if (nbttagcompound1.isEmpty()) {
                    cmp.remove("tag");
                }
                cmp.putString("id", a[i % 16]);
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
        public CompoundTag convert(CompoundTag cmp) {
            if ("minecraft:shulker".equals(cmp.getString("id"))) {
                cmp.remove("Color");
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
        public CompoundTag convert(CompoundTag cmp) {
            if (cmp.contains("lang", 8)) {
                cmp.putString("lang", cmp.getString("lang").toLowerCase(Locale.ROOT));
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
        public CompoundTag convert(CompoundTag cmp) {
            if ("minecraft:totem".equals(cmp.getString("id"))) {
                cmp.putString("id", "minecraft:totem_of_undying");
            }
            return cmp;
        }
    }

    private static class DataConverterBedBlock
    implements DataConverter {
        private static final Logger a = LogManager.getLogger(NeoForgeDataFixer.class);

        DataConverterBedBlock() {
        }

        @Override
        public int getDataVersion() {
            return 1125;
        }

        @Override
        public CompoundTag convert(CompoundTag cmp) {
            boolean flag = true;
            try {
                CompoundTag nbttagcompound1 = cmp.getCompound("Level");
                int i = nbttagcompound1.getInt("xPos");
                int j = nbttagcompound1.getInt("zPos");
                ListTag nbttaglist = nbttagcompound1.getList("TileEntities", 10);
                ListTag nbttaglist1 = nbttagcompound1.getList("Sections", 10);
                for (int k = 0; k < nbttaglist1.size(); ++k) {
                    CompoundTag nbttagcompound2 = nbttaglist1.getCompound(k);
                    byte b0 = nbttagcompound2.getByte("Y");
                    byte[] abyte = nbttagcompound2.getByteArray("Blocks");
                    for (int l = 0; l < abyte.length; ++l) {
                        if (416 != (abyte[l] & 0xFF) << 4) continue;
                        int i1 = l & 0xF;
                        int j1 = l >> 8 & 0xF;
                        int k1 = l >> 4 & 0xF;
                        CompoundTag nbttagcompound3 = new CompoundTag();
                        nbttagcompound3.putString("id", "bed");
                        nbttagcompound3.putInt("x", i1 + (i << 4));
                        nbttagcompound3.putInt("y", j1 + (b0 << 4));
                        nbttagcompound3.putInt("z", k1 + (j << 4));
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
        public CompoundTag convert(CompoundTag cmp) {
            if ("minecraft:bed".equals(cmp.getString("id")) && cmp.getShort("Damage") == 0) {
                cmp.putShort("Damage", (short)DyeColor.RED.getId());
            }
            return cmp;
        }
    }

    private static abstract class DataInspectorTagged
    implements DataInspector {
        private final ResourceLocation key;

        DataInspectorTagged(String type) {
            this.key = NeoForgeDataFixer.getKey(type);
        }

        @Override
        public CompoundTag inspect(CompoundTag cmp, int sourceVer, int targetVer) {
            if (this.key.equals((Object)ResourceLocation.parse((String)cmp.getString("id")))) {
                cmp = this.inspectChecked(cmp, sourceVer, targetVer);
            }
            return cmp;
        }

        abstract CompoundTag inspectChecked(CompoundTag var1, int var2, int var3);
    }
}

