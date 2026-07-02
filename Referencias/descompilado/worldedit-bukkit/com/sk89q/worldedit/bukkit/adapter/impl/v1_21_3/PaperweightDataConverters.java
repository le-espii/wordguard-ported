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
 *  net.minecraft.core.EnumDirection
 *  net.minecraft.core.HolderLookup$a
 *  net.minecraft.nbt.DynamicOpsNBT
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagFloat
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.nbt.NBTTagString
 *  net.minecraft.network.chat.IChatBaseComponent
 *  net.minecraft.network.chat.IChatBaseComponent$ChatSerializer
 *  net.minecraft.network.chat.IChatMutableComponent
 *  net.minecraft.resources.MinecraftKey
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.util.ChatDeserializer
 *  net.minecraft.util.UtilColor
 *  net.minecraft.util.datafix.DataConverterRegistry
 *  net.minecraft.util.datafix.fixes.DataConverterTypes
 *  net.minecraft.world.item.EnumColor
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.sk89q.worldedit.bukkit.adapter.impl.v1_21_3;

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
import com.sk89q.worldedit.bukkit.adapter.impl.v1_21_3.PaperweightAdapter;
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
import net.minecraft.core.EnumDirection;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.util.UtilColor;
import net.minecraft.util.datafix.DataConverterRegistry;
import net.minecraft.util.datafix.fixes.DataConverterTypes;
import net.minecraft.world.item.EnumColor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.enginehub.linbus.tree.LinCompoundTag;

class PaperweightDataConverters
implements com.sk89q.worldedit.world.DataFixer {
    private final PaperweightAdapter adapter;
    private static final DynamicOpsNBT OPS_NBT = DynamicOpsNBT.a;
    private static final int LEGACY_VERSION = 1343;
    private static int DATA_VERSION;
    static PaperweightDataConverters INSTANCE;
    private final Map<LegacyType, List<DataConverter>> converters = new EnumMap<LegacyType, List<DataConverter>>(LegacyType.class);
    private final Map<LegacyType, List<DataInspector>> inspectors = new EnumMap<LegacyType, List<DataInspector>>(LegacyType.class);
    private DataFixer fixer;
    private static final Map<String, LegacyType> DFU_TO_LEGACY;
    private static final Map<String, MinecraftKey> OLD_ID_TO_KEY_MAP;

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
        NBTTagCompound tag = (NBTTagCompound)this.adapter.fromNative(originalChunk);
        NBTTagCompound fixed = PaperweightDataConverters.convert(LegacyType.CHUNK, tag, srcVer);
        return (LinCompoundTag)this.adapter.toNative((NBTBase)fixed);
    }

    private LinCompoundTag fixBlockEntity(LinCompoundTag origTileEnt, int srcVer) {
        NBTTagCompound tag = (NBTTagCompound)this.adapter.fromNative(origTileEnt);
        NBTTagCompound fixed = PaperweightDataConverters.convert(LegacyType.BLOCK_ENTITY, tag, srcVer);
        return (LinCompoundTag)this.adapter.toNative((NBTBase)fixed);
    }

    private LinCompoundTag fixEntity(LinCompoundTag origEnt, int srcVer) {
        NBTTagCompound tag = (NBTTagCompound)this.adapter.fromNative(origEnt);
        NBTTagCompound fixed = PaperweightDataConverters.convert(LegacyType.ENTITY, tag, srcVer);
        return (LinCompoundTag)this.adapter.toNative((NBTBase)fixed);
    }

    private String fixBlockState(String blockState, int srcVer) {
        NBTTagCompound stateNBT = PaperweightDataConverters.stateToNBT(blockState);
        Dynamic dynamic = new Dynamic((DynamicOps)OPS_NBT, (Object)stateNBT);
        NBTTagCompound fixed = (NBTTagCompound)PaperweightDataConverters.INSTANCE.fixer.update(DataConverterTypes.u, dynamic, srcVer, DATA_VERSION).getValue();
        return this.nbtToState(fixed);
    }

    private String nbtToState(NBTTagCompound tagCompound) {
        StringBuilder sb = new StringBuilder();
        sb.append(tagCompound.l("Name"));
        if (tagCompound.b("Properties", 10)) {
            sb.append('[');
            NBTTagCompound props = tagCompound.p("Properties");
            sb.append(props.e().stream().map(k -> k + "=" + props.l(k).replace("\"", "")).collect(Collectors.joining(",")));
            sb.append(']');
        }
        return sb.toString();
    }

    private static NBTTagCompound stateToNBT(String blockState) {
        int propIdx = blockState.indexOf(91);
        NBTTagCompound tag = new NBTTagCompound();
        if (propIdx < 0) {
            tag.a("Name", blockState);
        } else {
            String[] propArr;
            tag.a("Name", blockState.substring(0, propIdx));
            NBTTagCompound propTag = new NBTTagCompound();
            String props = blockState.substring(propIdx + 1, blockState.length() - 1);
            for (String pair : propArr = props.split(",")) {
                String[] split = pair.split("=");
                propTag.a(split[0], split[1]);
            }
            tag.a("Properties", (NBTBase)propTag);
        }
        return tag;
    }

    private String fixBiome(String key, int srcVer) {
        return PaperweightDataConverters.fixName(key, srcVer, DataConverterTypes.K);
    }

    private String fixItemType(String key, int srcVer) {
        return PaperweightDataConverters.fixName(key, srcVer, DataConverterTypes.D);
    }

    private static String fixName(String key, int srcVer, DSL.TypeReference type) {
        return ((NBTBase)PaperweightDataConverters.INSTANCE.fixer.update(type, new Dynamic((DynamicOps)OPS_NBT, (Object)NBTTagString.a((String)key)), srcVer, DATA_VERSION).getValue()).u_();
    }

    PaperweightDataConverters(int dataVersion, PaperweightAdapter adapter) {
        DATA_VERSION = dataVersion;
        INSTANCE = this;
        this.adapter = adapter;
        this.registerConverters();
        this.registerInspectors();
        this.fixer = new WrappedDataFixer(DataConverterRegistry.a());
    }

    public static NBTTagCompound convert(LegacyType type, NBTTagCompound cmp) {
        return PaperweightDataConverters.convert(type.getDFUType(), cmp);
    }

    public static NBTTagCompound convert(LegacyType type, NBTTagCompound cmp, int sourceVer) {
        return PaperweightDataConverters.convert(type.getDFUType(), cmp, sourceVer);
    }

    public static NBTTagCompound convert(LegacyType type, NBTTagCompound cmp, int sourceVer, int targetVer) {
        return PaperweightDataConverters.convert(type.getDFUType(), cmp, sourceVer, targetVer);
    }

    public static NBTTagCompound convert(DSL.TypeReference type, NBTTagCompound cmp) {
        int i = cmp.b("DataVersion", 99) ? cmp.h("DataVersion") : -1;
        return PaperweightDataConverters.convert(type, cmp, i);
    }

    public static NBTTagCompound convert(DSL.TypeReference type, NBTTagCompound cmp, int sourceVer) {
        return PaperweightDataConverters.convert(type, cmp, sourceVer, DATA_VERSION);
    }

    public static NBTTagCompound convert(DSL.TypeReference type, NBTTagCompound cmp, int sourceVer, int targetVer) {
        if (sourceVer >= targetVer) {
            return cmp;
        }
        return (NBTTagCompound)PaperweightDataConverters.INSTANCE.fixer.update(type, new Dynamic((DynamicOps)OPS_NBT, (Object)cmp), sourceVer, targetVer).getValue();
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

    private static MinecraftKey getKey(String type) {
        MinecraftKey key = OLD_ID_TO_KEY_MAP.get(type);
        if (key == null) {
            throw new IllegalArgumentException("Unknown mapping for " + type);
        }
        return key;
    }

    private static void convertCompound(LegacyType type, NBTTagCompound cmp, String key, int sourceVer, int targetVer) {
        cmp.a(key, (NBTBase)PaperweightDataConverters.convert(type, cmp.p(key), sourceVer, targetVer));
    }

    private static void convertItem(NBTTagCompound nbttagcompound, String key, int sourceVer, int targetVer) {
        if (nbttagcompound.b(key, 10)) {
            PaperweightDataConverters.convertCompound(LegacyType.ITEM_INSTANCE, nbttagcompound, key, sourceVer, targetVer);
        }
    }

    private static void convertItems(NBTTagCompound nbttagcompound, String key, int sourceVer, int targetVer) {
        if (nbttagcompound.b(key, 9)) {
            NBTTagList nbttaglist = nbttagcompound.c(key, 10);
            for (int j = 0; j < nbttaglist.size(); ++j) {
                nbttaglist.d(j, (NBTBase)PaperweightDataConverters.convert(LegacyType.ITEM_INSTANCE, nbttaglist.a(j), sourceVer, targetVer));
            }
        }
    }

    static {
        DFU_TO_LEGACY = new HashMap<String, LegacyType>();
        Map<String, MinecraftKey> map = OLD_ID_TO_KEY_MAP = new HashMap<String, MinecraftKey>();
        map.put("EntityItem", MinecraftKey.a((String)"item"));
        map.put("EntityExperienceOrb", MinecraftKey.a((String)"xp_orb"));
        map.put("EntityAreaEffectCloud", MinecraftKey.a((String)"area_effect_cloud"));
        map.put("EntityGuardianElder", MinecraftKey.a((String)"elder_guardian"));
        map.put("EntitySkeletonWither", MinecraftKey.a((String)"wither_skeleton"));
        map.put("EntitySkeletonStray", MinecraftKey.a((String)"stray"));
        map.put("EntityEgg", MinecraftKey.a((String)"egg"));
        map.put("EntityLeash", MinecraftKey.a((String)"leash_knot"));
        map.put("EntityPainting", MinecraftKey.a((String)"painting"));
        map.put("EntityTippedArrow", MinecraftKey.a((String)"arrow"));
        map.put("EntitySnowball", MinecraftKey.a((String)"snowball"));
        map.put("EntityLargeFireball", MinecraftKey.a((String)"fireball"));
        map.put("EntitySmallFireball", MinecraftKey.a((String)"small_fireball"));
        map.put("EntityEnderPearl", MinecraftKey.a((String)"ender_pearl"));
        map.put("EntityEnderSignal", MinecraftKey.a((String)"eye_of_ender_signal"));
        map.put("EntityPotion", MinecraftKey.a((String)"potion"));
        map.put("EntityThrownExpBottle", MinecraftKey.a((String)"xp_bottle"));
        map.put("EntityItemFrame", MinecraftKey.a((String)"item_frame"));
        map.put("EntityWitherSkull", MinecraftKey.a((String)"wither_skull"));
        map.put("EntityTNTPrimed", MinecraftKey.a((String)"tnt"));
        map.put("EntityFallingBlock", MinecraftKey.a((String)"falling_block"));
        map.put("EntityFireworks", MinecraftKey.a((String)"fireworks_rocket"));
        map.put("EntityZombieHusk", MinecraftKey.a((String)"husk"));
        map.put("EntitySpectralArrow", MinecraftKey.a((String)"spectral_arrow"));
        map.put("EntityShulkerBullet", MinecraftKey.a((String)"shulker_bullet"));
        map.put("EntityDragonFireball", MinecraftKey.a((String)"dragon_fireball"));
        map.put("EntityZombieVillager", MinecraftKey.a((String)"zombie_villager"));
        map.put("EntityHorseSkeleton", MinecraftKey.a((String)"skeleton_horse"));
        map.put("EntityHorseZombie", MinecraftKey.a((String)"zombie_horse"));
        map.put("EntityArmorStand", MinecraftKey.a((String)"armor_stand"));
        map.put("EntityHorseDonkey", MinecraftKey.a((String)"donkey"));
        map.put("EntityHorseMule", MinecraftKey.a((String)"mule"));
        map.put("EntityEvokerFangs", MinecraftKey.a((String)"evocation_fangs"));
        map.put("EntityEvoker", MinecraftKey.a((String)"evocation_illager"));
        map.put("EntityVex", MinecraftKey.a((String)"vex"));
        map.put("EntityVindicator", MinecraftKey.a((String)"vindication_illager"));
        map.put("EntityIllagerIllusioner", MinecraftKey.a((String)"illusion_illager"));
        map.put("EntityMinecartCommandBlock", MinecraftKey.a((String)"commandblock_minecart"));
        map.put("EntityBoat", MinecraftKey.a((String)"boat"));
        map.put("EntityMinecartRideable", MinecraftKey.a((String)"minecart"));
        map.put("EntityMinecartChest", MinecraftKey.a((String)"chest_minecart"));
        map.put("EntityMinecartFurnace", MinecraftKey.a((String)"furnace_minecart"));
        map.put("EntityMinecartTNT", MinecraftKey.a((String)"tnt_minecart"));
        map.put("EntityMinecartHopper", MinecraftKey.a((String)"hopper_minecart"));
        map.put("EntityMinecartMobSpawner", MinecraftKey.a((String)"spawner_minecart"));
        map.put("EntityCreeper", MinecraftKey.a((String)"creeper"));
        map.put("EntitySkeleton", MinecraftKey.a((String)"skeleton"));
        map.put("EntitySpider", MinecraftKey.a((String)"spider"));
        map.put("EntityGiantZombie", MinecraftKey.a((String)"giant"));
        map.put("EntityZombie", MinecraftKey.a((String)"zombie"));
        map.put("EntitySlime", MinecraftKey.a((String)"slime"));
        map.put("EntityGhast", MinecraftKey.a((String)"ghast"));
        map.put("EntityPigZombie", MinecraftKey.a((String)"zombie_pigman"));
        map.put("EntityEnderman", MinecraftKey.a((String)"enderman"));
        map.put("EntityCaveSpider", MinecraftKey.a((String)"cave_spider"));
        map.put("EntitySilverfish", MinecraftKey.a((String)"silverfish"));
        map.put("EntityBlaze", MinecraftKey.a((String)"blaze"));
        map.put("EntityMagmaCube", MinecraftKey.a((String)"magma_cube"));
        map.put("EntityEnderDragon", MinecraftKey.a((String)"ender_dragon"));
        map.put("EntityWither", MinecraftKey.a((String)"wither"));
        map.put("EntityBat", MinecraftKey.a((String)"bat"));
        map.put("EntityWitch", MinecraftKey.a((String)"witch"));
        map.put("EntityEndermite", MinecraftKey.a((String)"endermite"));
        map.put("EntityGuardian", MinecraftKey.a((String)"guardian"));
        map.put("EntityShulker", MinecraftKey.a((String)"shulker"));
        map.put("EntityPig", MinecraftKey.a((String)"pig"));
        map.put("EntitySheep", MinecraftKey.a((String)"sheep"));
        map.put("EntityCow", MinecraftKey.a((String)"cow"));
        map.put("EntityChicken", MinecraftKey.a((String)"chicken"));
        map.put("EntitySquid", MinecraftKey.a((String)"squid"));
        map.put("EntityWolf", MinecraftKey.a((String)"wolf"));
        map.put("EntityMushroomCow", MinecraftKey.a((String)"mooshroom"));
        map.put("EntitySnowman", MinecraftKey.a((String)"snowman"));
        map.put("EntityOcelot", MinecraftKey.a((String)"ocelot"));
        map.put("EntityIronGolem", MinecraftKey.a((String)"villager_golem"));
        map.put("EntityHorse", MinecraftKey.a((String)"horse"));
        map.put("EntityRabbit", MinecraftKey.a((String)"rabbit"));
        map.put("EntityPolarBear", MinecraftKey.a((String)"polar_bear"));
        map.put("EntityLlama", MinecraftKey.a((String)"llama"));
        map.put("EntityLlamaSpit", MinecraftKey.a((String)"llama_spit"));
        map.put("EntityParrot", MinecraftKey.a((String)"parrot"));
        map.put("EntityVillager", MinecraftKey.a((String)"villager"));
        map.put("EntityEnderCrystal", MinecraftKey.a((String)"ender_crystal"));
        map.put("TileEntityFurnace", MinecraftKey.a((String)"furnace"));
        map.put("TileEntityChest", MinecraftKey.a((String)"chest"));
        map.put("TileEntityEnderChest", MinecraftKey.a((String)"ender_chest"));
        map.put("TileEntityRecordPlayer", MinecraftKey.a((String)"jukebox"));
        map.put("TileEntityDispenser", MinecraftKey.a((String)"dispenser"));
        map.put("TileEntityDropper", MinecraftKey.a((String)"dropper"));
        map.put("TileEntitySign", MinecraftKey.a((String)"sign"));
        map.put("TileEntityMobSpawner", MinecraftKey.a((String)"mob_spawner"));
        map.put("TileEntityNote", MinecraftKey.a((String)"noteblock"));
        map.put("TileEntityPiston", MinecraftKey.a((String)"piston"));
        map.put("TileEntityBrewingStand", MinecraftKey.a((String)"brewing_stand"));
        map.put("TileEntityEnchantTable", MinecraftKey.a((String)"enchanting_table"));
        map.put("TileEntityEnderPortal", MinecraftKey.a((String)"end_portal"));
        map.put("TileEntityBeacon", MinecraftKey.a((String)"beacon"));
        map.put("TileEntitySkull", MinecraftKey.a((String)"skull"));
        map.put("TileEntityLightDetector", MinecraftKey.a((String)"daylight_detector"));
        map.put("TileEntityHopper", MinecraftKey.a((String)"hopper"));
        map.put("TileEntityComparator", MinecraftKey.a((String)"comparator"));
        map.put("TileEntityFlowerPot", MinecraftKey.a((String)"flower_pot"));
        map.put("TileEntityBanner", MinecraftKey.a((String)"banner"));
        map.put("TileEntityStructure", MinecraftKey.a((String)"structure_block"));
        map.put("TileEntityEndGateway", MinecraftKey.a((String)"end_gateway"));
        map.put("TileEntityCommand", MinecraftKey.a((String)"command_block"));
        map.put("TileEntityShulkerBox", MinecraftKey.a((String)"shulker_box"));
        map.put("TileEntityBed", MinecraftKey.a((String)"bed"));
    }

    public static enum LegacyType {
        LEVEL(DataConverterTypes.a),
        PLAYER(DataConverterTypes.b),
        CHUNK(DataConverterTypes.c),
        BLOCK_ENTITY(DataConverterTypes.s),
        ENTITY(DataConverterTypes.B),
        ITEM_INSTANCE(DataConverterTypes.t),
        OPTIONS(DataConverterTypes.e),
        STRUCTURE(DataConverterTypes.f);

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
                NBTTagCompound cmp = (NBTTagCompound)dynamic.getValue();
                int desiredVersion = Math.min(targetVer, 1343);
                cmp = this.convert(legacyType, cmp, sourceVer, desiredVersion);
                sourceVer = desiredVersion;
                dynamic = new Dynamic((DynamicOps)OPS_NBT, (Object)cmp);
            }
            return this.realFixer.update(type, dynamic, sourceVer, targetVer);
        }

        private NBTTagCompound convert(LegacyType type, NBTTagCompound cmp, int sourceVer, int desiredVersion) {
            List<DataInspector> inspectors;
            List<DataConverter> converters = PaperweightDataConverters.this.converters.get((Object)type);
            if (converters != null && !converters.isEmpty()) {
                for (DataConverter converter : converters) {
                    int dataVersion = converter.getDataVersion();
                    if (dataVersion <= sourceVer || dataVersion > desiredVersion) continue;
                    cmp = converter.convert(cmp);
                }
            }
            if ((inspectors = PaperweightDataConverters.this.inspectors.get((Object)type)) != null && !inspectors.isEmpty()) {
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

        public NBTTagCompound convert(NBTTagCompound var1);
    }

    private static class DataInspectorItem
    extends DataInspectorTagged {
        private final String[] keys;

        DataInspectorItem(String oclass, String ... astring) {
            super(oclass);
            this.keys = astring;
        }

        @Override
        NBTTagCompound inspectChecked(NBTTagCompound nbttagcompound, int sourceVer, int targetVer) {
            for (String key : this.keys) {
                PaperweightDataConverters.convertItem(nbttagcompound, key, sourceVer, targetVer);
            }
            return nbttagcompound;
        }
    }

    public static interface DataInspector {
        public NBTTagCompound inspect(NBTTagCompound var1, int var2, int var3);
    }

    private static class DataInspectorItemList
    extends DataInspectorTagged {
        private final String[] keys;

        DataInspectorItemList(String oclass, String ... astring) {
            super(oclass);
            this.keys = astring;
        }

        @Override
        NBTTagCompound inspectChecked(NBTTagCompound nbttagcompound, int sourceVer, int targetVer) {
            for (String s : this.keys) {
                PaperweightDataConverters.convertItems(nbttagcompound, s, sourceVer, targetVer);
            }
            return nbttagcompound;
        }
    }

    private static class DataInspectorMobSpawnerMobs
    implements DataInspector {
        MinecraftKey tileEntityMobSpawner = PaperweightDataConverters.getKey("TileEntityMobSpawner");

        private DataInspectorMobSpawnerMobs() {
        }

        @Override
        public NBTTagCompound inspect(NBTTagCompound cmp, int sourceVer, int targetVer) {
            if (this.tileEntityMobSpawner.equals((Object)MinecraftKey.a((String)cmp.l("id")))) {
                if (cmp.b("SpawnPotentials", 9)) {
                    NBTTagList nbttaglist = cmp.c("SpawnPotentials", 10);
                    for (int j = 0; j < nbttaglist.size(); ++j) {
                        NBTTagCompound nbttagcompound1 = nbttaglist.a(j);
                        PaperweightDataConverters.convertCompound(LegacyType.ENTITY, nbttagcompound1, "Entity", sourceVer, targetVer);
                    }
                }
                PaperweightDataConverters.convertCompound(LegacyType.ENTITY, cmp, "SpawnData", sourceVer, targetVer);
            }
            return cmp;
        }
    }

    private static class DataInspectorChunks
    implements DataInspector {
        private DataInspectorChunks() {
        }

        @Override
        public NBTTagCompound inspect(NBTTagCompound cmp, int sourceVer, int targetVer) {
            if (cmp.b("Level", 10)) {
                int j;
                NBTTagList nbttaglist;
                NBTTagCompound nbttagcompound1 = cmp.p("Level");
                if (nbttagcompound1.b("Entities", 9)) {
                    nbttaglist = nbttagcompound1.c("Entities", 10);
                    for (j = 0; j < nbttaglist.size(); ++j) {
                        nbttaglist.d(j, (NBTBase)PaperweightDataConverters.convert(LegacyType.ENTITY, (NBTTagCompound)nbttaglist.k(j), sourceVer, targetVer));
                    }
                }
                if (nbttagcompound1.b("TileEntities", 9)) {
                    nbttaglist = nbttagcompound1.c("TileEntities", 10);
                    for (j = 0; j < nbttaglist.size(); ++j) {
                        nbttaglist.d(j, (NBTBase)PaperweightDataConverters.convert(LegacyType.BLOCK_ENTITY, (NBTTagCompound)nbttaglist.k(j), sourceVer, targetVer));
                    }
                }
            }
            return cmp;
        }
    }

    private static class DataInspectorCommandBlock
    implements DataInspector {
        MinecraftKey tileEntityCommand = PaperweightDataConverters.getKey("TileEntityCommand");

        private DataInspectorCommandBlock() {
        }

        @Override
        public NBTTagCompound inspect(NBTTagCompound cmp, int sourceVer, int targetVer) {
            if (this.tileEntityCommand.equals((Object)MinecraftKey.a((String)cmp.l("id")))) {
                cmp.a("id", "Control");
                PaperweightDataConverters.convert(LegacyType.BLOCK_ENTITY, cmp, sourceVer, targetVer);
                cmp.a("id", "MinecartCommandBlock");
            }
            return cmp;
        }
    }

    private static class DataInspectorEntityPassengers
    implements DataInspector {
        private DataInspectorEntityPassengers() {
        }

        @Override
        public NBTTagCompound inspect(NBTTagCompound cmp, int sourceVer, int targetVer) {
            if (cmp.b("Passengers", 9)) {
                NBTTagList nbttaglist = cmp.c("Passengers", 10);
                for (int j = 0; j < nbttaglist.size(); ++j) {
                    nbttaglist.d(j, (NBTBase)PaperweightDataConverters.convert(LegacyType.ENTITY, nbttaglist.a(j), sourceVer, targetVer));
                }
            }
            return cmp;
        }
    }

    private static class DataInspectorMobSpawnerMinecart
    implements DataInspector {
        MinecraftKey entityMinecartMobSpawner = PaperweightDataConverters.getKey("EntityMinecartMobSpawner");
        MinecraftKey tileEntityMobSpawner = PaperweightDataConverters.getKey("TileEntityMobSpawner");

        private DataInspectorMobSpawnerMinecart() {
        }

        @Override
        public NBTTagCompound inspect(NBTTagCompound cmp, int sourceVer, int targetVer) {
            String s = cmp.l("id");
            if (this.entityMinecartMobSpawner.equals((Object)MinecraftKey.a((String)s))) {
                cmp.a("id", this.tileEntityMobSpawner.toString());
                PaperweightDataConverters.convert(LegacyType.BLOCK_ENTITY, cmp, sourceVer, targetVer);
                cmp.a("id", s);
            }
            return cmp;
        }
    }

    private static class DataInspectorVillagers
    implements DataInspector {
        MinecraftKey entityVillager = PaperweightDataConverters.getKey("EntityVillager");

        private DataInspectorVillagers() {
        }

        @Override
        public NBTTagCompound inspect(NBTTagCompound cmp, int sourceVer, int targetVer) {
            NBTTagCompound nbttagcompound1;
            if (this.entityVillager.equals((Object)MinecraftKey.a((String)cmp.l("id"))) && cmp.b("Offers", 10) && (nbttagcompound1 = cmp.p("Offers")).b("Recipes", 9)) {
                NBTTagList nbttaglist = nbttagcompound1.c("Recipes", 10);
                for (int j = 0; j < nbttaglist.size(); ++j) {
                    NBTTagCompound nbttagcompound2 = nbttaglist.a(j);
                    PaperweightDataConverters.convertItem(nbttagcompound2, "buy", sourceVer, targetVer);
                    PaperweightDataConverters.convertItem(nbttagcompound2, "buyB", sourceVer, targetVer);
                    PaperweightDataConverters.convertItem(nbttagcompound2, "sell", sourceVer, targetVer);
                    nbttaglist.d(j, (NBTBase)nbttagcompound2);
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
            String key = MinecraftKey.a((String)s).toString();
            if (i < 515 && b.containsKey(key)) {
                return b.get(key);
            }
            return c.get(key);
        }

        @Override
        public NBTTagCompound inspect(NBTTagCompound cmp, int sourceVer, int targetVer) {
            if (!cmp.b("tag", 10)) {
                return cmp;
            }
            NBTTagCompound nbttagcompound1 = cmp.p("tag");
            if (nbttagcompound1.b("BlockEntityTag", 10)) {
                boolean flag;
                NBTTagCompound nbttagcompound2 = nbttagcompound1.p("BlockEntityTag");
                String s = cmp.l("id");
                String s1 = DataInspectorBlockEntity.convertEntityId(sourceVer, s);
                if (s1 == null) {
                    flag = false;
                } else {
                    flag = !nbttagcompound2.e("id");
                    nbttagcompound2.a("id", s1);
                }
                PaperweightDataConverters.convert(LegacyType.BLOCK_ENTITY, nbttagcompound2, sourceVer, targetVer);
                if (flag) {
                    nbttagcompound2.r("id");
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
        DataInspectorEntity() {
        }

        @Override
        public NBTTagCompound inspect(NBTTagCompound cmp, int sourceVer, int targetVer) {
            NBTTagCompound nbttagcompound1 = cmp.p("tag");
            if (nbttagcompound1.b("EntityTag", 10)) {
                String s1;
                NBTTagCompound nbttagcompound2 = nbttagcompound1.p("EntityTag");
                String s = cmp.l("id");
                if ("minecraft:armor_stand".equals(s)) {
                    s1 = sourceVer < 515 ? "ArmorStand" : "minecraft:armor_stand";
                } else {
                    if (!"minecraft:spawn_egg".equals(s)) {
                        return cmp;
                    }
                    s1 = nbttagcompound2.l("id");
                }
                boolean flag = !nbttagcompound2.b("id", 8);
                nbttagcompound2.a("id", s1);
                PaperweightDataConverters.convert(LegacyType.ENTITY, nbttagcompound2, sourceVer, targetVer);
                if (flag) {
                    nbttagcompound2.r("id");
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
        public NBTTagCompound inspect(NBTTagCompound cmp, int sourceVer, int targetVer) {
            if (cmp.b("Player", 10)) {
                PaperweightDataConverters.convertCompound(LegacyType.PLAYER, cmp, "Player", sourceVer, targetVer);
            }
            return cmp;
        }
    }

    private static class DataInspectorPlayer
    implements DataInspector {
        private DataInspectorPlayer() {
        }

        @Override
        public NBTTagCompound inspect(NBTTagCompound cmp, int sourceVer, int targetVer) {
            PaperweightDataConverters.convertItems(cmp, "Inventory", sourceVer, targetVer);
            PaperweightDataConverters.convertItems(cmp, "EnderItems", sourceVer, targetVer);
            if (cmp.b("ShoulderEntityLeft", 10)) {
                PaperweightDataConverters.convertCompound(LegacyType.ENTITY, cmp, "ShoulderEntityLeft", sourceVer, targetVer);
            }
            if (cmp.b("ShoulderEntityRight", 10)) {
                PaperweightDataConverters.convertCompound(LegacyType.ENTITY, cmp, "ShoulderEntityRight", sourceVer, targetVer);
            }
            return cmp;
        }
    }

    private static class DataInspectorPlayerVehicle
    implements DataInspector {
        private DataInspectorPlayerVehicle() {
        }

        @Override
        public NBTTagCompound inspect(NBTTagCompound cmp, int sourceVer, int targetVer) {
            NBTTagCompound nbttagcompound1;
            if (cmp.b("RootVehicle", 10) && (nbttagcompound1 = cmp.p("RootVehicle")).b("Entity", 10)) {
                PaperweightDataConverters.convertCompound(LegacyType.ENTITY, nbttagcompound1, "Entity", sourceVer, targetVer);
            }
            return cmp;
        }
    }

    private static class DataInspectorStructure
    implements DataInspector {
        private DataInspectorStructure() {
        }

        @Override
        public NBTTagCompound inspect(NBTTagCompound cmp, int sourceVer, int targetVer) {
            NBTTagCompound nbttagcompound1;
            int j;
            NBTTagList nbttaglist;
            if (cmp.b("entities", 9)) {
                nbttaglist = cmp.c("entities", 10);
                for (j = 0; j < nbttaglist.size(); ++j) {
                    nbttagcompound1 = (NBTTagCompound)nbttaglist.k(j);
                    if (!nbttagcompound1.b("nbt", 10)) continue;
                    PaperweightDataConverters.convertCompound(LegacyType.ENTITY, nbttagcompound1, "nbt", sourceVer, targetVer);
                }
            }
            if (cmp.b("blocks", 9)) {
                nbttaglist = cmp.c("blocks", 10);
                for (j = 0; j < nbttaglist.size(); ++j) {
                    nbttagcompound1 = (NBTTagCompound)nbttaglist.k(j);
                    if (!nbttagcompound1.b("nbt", 10)) continue;
                    PaperweightDataConverters.convertCompound(LegacyType.BLOCK_ENTITY, nbttagcompound1, "nbt", sourceVer, targetVer);
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
        public NBTTagCompound convert(NBTTagCompound cmp) {
            NBTTagList nbttaglist1;
            NBTTagList nbttaglist = cmp.c("Equipment", 10);
            if (!nbttaglist.isEmpty() && !cmp.b("HandItems", 10)) {
                nbttaglist1 = new NBTTagList();
                nbttaglist1.add((Object)nbttaglist.k(0));
                nbttaglist1.add((Object)new NBTTagCompound());
                cmp.a("HandItems", (NBTBase)nbttaglist1);
            }
            if (nbttaglist.size() > 1 && !cmp.b("ArmorItem", 10)) {
                nbttaglist1 = new NBTTagList();
                nbttaglist1.add((Object)nbttaglist.k(1));
                nbttaglist1.add((Object)nbttaglist.k(2));
                nbttaglist1.add((Object)nbttaglist.k(3));
                nbttaglist1.add((Object)nbttaglist.k(4));
                cmp.a("ArmorItems", (NBTBase)nbttaglist1);
            }
            cmp.r("Equipment");
            if (cmp.b("DropChances", 9)) {
                NBTTagList nbttaglist2;
                nbttaglist1 = cmp.c("DropChances", 5);
                if (!cmp.b("HandDropChances", 10)) {
                    nbttaglist2 = new NBTTagList();
                    nbttaglist2.add((Object)NBTTagFloat.a((float)nbttaglist1.i(0)));
                    nbttaglist2.add((Object)NBTTagFloat.a((float)0.0f));
                    cmp.a("HandDropChances", (NBTBase)nbttaglist2);
                }
                if (!cmp.b("ArmorDropChances", 10)) {
                    nbttaglist2 = new NBTTagList();
                    nbttaglist2.add((Object)NBTTagFloat.a((float)nbttaglist1.i(1)));
                    nbttaglist2.add((Object)NBTTagFloat.a((float)nbttaglist1.i(2)));
                    nbttaglist2.add((Object)NBTTagFloat.a((float)nbttaglist1.i(3)));
                    nbttaglist2.add((Object)NBTTagFloat.a((float)nbttaglist1.i(4)));
                    cmp.a("ArmorDropChances", (NBTBase)nbttaglist2);
                }
                cmp.r("DropChances");
            }
            return cmp;
        }
    }

    private static class DataConverterSignText
    implements DataConverter {
        public static final Gson a = new GsonBuilder().registerTypeAdapter(IChatBaseComponent.class, (Object)new JsonDeserializer(){

            IChatMutableComponent a(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
                if (jsonelement.isJsonPrimitive()) {
                    return IChatBaseComponent.b((String)jsonelement.getAsString());
                }
                if (jsonelement.isJsonArray()) {
                    JsonArray jsonarray = jsonelement.getAsJsonArray();
                    IChatMutableComponent ichatbasecomponent = null;
                    for (JsonElement jsonelement1 : jsonarray) {
                        IChatMutableComponent ichatbasecomponent1 = this.a(jsonelement1, jsonelement1.getClass(), jsondeserializationcontext);
                        if (ichatbasecomponent == null) {
                            ichatbasecomponent = ichatbasecomponent1;
                            continue;
                        }
                        ichatbasecomponent.b((IChatBaseComponent)ichatbasecomponent1);
                    }
                    return ichatbasecomponent;
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
        public NBTTagCompound convert(NBTTagCompound cmp) {
            if ("Sign".equals(cmp.l("id"))) {
                this.convert(cmp, "Text1");
                this.convert(cmp, "Text2");
                this.convert(cmp, "Text3");
                this.convert(cmp, "Text4");
            }
            return cmp;
        }

        private void convert(NBTTagCompound nbttagcompound, String s) {
            String s1 = nbttagcompound.l(s);
            IChatMutableComponent object = null;
            if (!"null".equals(s1) && !UtilColor.b((String)s1)) {
                if (!(s1.charAt(0) == '\"' && s1.charAt(s1.length() - 1) == '\"' || s1.charAt(0) == '{' && s1.charAt(s1.length() - 1) == '}')) {
                    object = IChatBaseComponent.b((String)s1);
                } else {
                    try {
                        object = (IChatBaseComponent)ChatDeserializer.a((Gson)a, (String)s1, IChatBaseComponent.class, (boolean)true);
                        if (object == null) {
                            object = IChatBaseComponent.b((String)"");
                        }
                    }
                    catch (JsonParseException jsonParseException) {
                        // empty catch block
                    }
                    if (object == null) {
                        try {
                            object = IChatBaseComponent.ChatSerializer.a((String)s1, (HolderLookup.a)MinecraftServer.getServer().ba());
                        }
                        catch (JsonParseException jsonParseException) {
                            // empty catch block
                        }
                    }
                    if (object == null) {
                        try {
                            object = IChatBaseComponent.ChatSerializer.b((String)s1, (HolderLookup.a)MinecraftServer.getServer().ba());
                        }
                        catch (JsonParseException jsonParseException) {
                            // empty catch block
                        }
                    }
                    if (object == null) {
                        object = IChatBaseComponent.b((String)s1);
                    }
                }
            } else {
                object = IChatBaseComponent.b((String)"");
            }
            nbttagcompound.a(s, IChatBaseComponent.ChatSerializer.a((IChatBaseComponent)object, (HolderLookup.a)MinecraftServer.getServer().ba()));
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
        public NBTTagCompound convert(NBTTagCompound cmp) {
            short short0;
            if (cmp.b("id", 99) && (short0 = cmp.g("id")) > 0 && short0 < materials.length && materials[short0] != null) {
                cmp.a("id", materials[short0]);
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
            DataConverterMaterialId.materials[409] = "minecraft:prismarine_shard";
            DataConverterMaterialId.materials[410] = "minecraft:prismarine_crystals";
            DataConverterMaterialId.materials[411] = "minecraft:rabbit";
            DataConverterMaterialId.materials[412] = "minecraft:cooked_rabbit";
            DataConverterMaterialId.materials[413] = "minecraft:rabbit_stew";
            DataConverterMaterialId.materials[414] = "minecraft:rabbit_foot";
            DataConverterMaterialId.materials[415] = "minecraft:rabbit_hide";
            DataConverterMaterialId.materials[416] = "minecraft:armor_stand";
            DataConverterMaterialId.materials[423] = "minecraft:mutton";
            DataConverterMaterialId.materials[424] = "minecraft:cooked_mutton";
            DataConverterMaterialId.materials[425] = "minecraft:banner";
            DataConverterMaterialId.materials[426] = "minecraft:end_crystal";
            DataConverterMaterialId.materials[427] = "minecraft:spruce_door";
            DataConverterMaterialId.materials[428] = "minecraft:birch_door";
            DataConverterMaterialId.materials[429] = "minecraft:jungle_door";
            DataConverterMaterialId.materials[430] = "minecraft:acacia_door";
            DataConverterMaterialId.materials[431] = "minecraft:dark_oak_door";
            DataConverterMaterialId.materials[432] = "minecraft:chorus_fruit";
            DataConverterMaterialId.materials[433] = "minecraft:chorus_fruit_popped";
            DataConverterMaterialId.materials[434] = "minecraft:beetroot";
            DataConverterMaterialId.materials[435] = "minecraft:beetroot_seeds";
            DataConverterMaterialId.materials[436] = "minecraft:beetroot_soup";
            DataConverterMaterialId.materials[437] = "minecraft:dragon_breath";
            DataConverterMaterialId.materials[438] = "minecraft:splash_potion";
            DataConverterMaterialId.materials[439] = "minecraft:spectral_arrow";
            DataConverterMaterialId.materials[440] = "minecraft:tipped_arrow";
            DataConverterMaterialId.materials[441] = "minecraft:lingering_potion";
            DataConverterMaterialId.materials[442] = "minecraft:shield";
            DataConverterMaterialId.materials[443] = "minecraft:elytra";
            DataConverterMaterialId.materials[444] = "minecraft:spruce_boat";
            DataConverterMaterialId.materials[445] = "minecraft:birch_boat";
            DataConverterMaterialId.materials[446] = "minecraft:jungle_boat";
            DataConverterMaterialId.materials[447] = "minecraft:acacia_boat";
            DataConverterMaterialId.materials[448] = "minecraft:dark_oak_boat";
            DataConverterMaterialId.materials[449] = "minecraft:totem_of_undying";
            DataConverterMaterialId.materials[450] = "minecraft:shulker_shell";
            DataConverterMaterialId.materials[452] = "minecraft:iron_nugget";
            DataConverterMaterialId.materials[453] = "minecraft:knowledge_book";
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
        public NBTTagCompound convert(NBTTagCompound cmp) {
            if ("minecraft:potion".equals(cmp.l("id"))) {
                NBTTagCompound nbttagcompound1 = cmp.p("tag");
                short short0 = cmp.g("Damage");
                if (!nbttagcompound1.b("Potion", 8)) {
                    String s = potions[short0 & 0x7F];
                    nbttagcompound1.a("Potion", s == null ? "minecraft:water" : s);
                    cmp.a("tag", (NBTBase)nbttagcompound1);
                    if ((short0 & 0x4000) == 16384) {
                        cmp.a("id", "minecraft:splash_potion");
                    }
                }
                if (short0 != 0) {
                    cmp.a("Damage", (short)0);
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
        public NBTTagCompound convert(NBTTagCompound cmp) {
            if ("minecraft:spawn_egg".equals(cmp.l("id"))) {
                String s;
                NBTTagCompound nbttagcompound1 = cmp.p("tag");
                NBTTagCompound nbttagcompound2 = nbttagcompound1.p("EntityTag");
                short short0 = cmp.g("Damage");
                if (!nbttagcompound2.b("id", 8) && (s = eggs[short0 & 0xFF]) != null) {
                    nbttagcompound2.a("id", s);
                    nbttagcompound1.a("EntityTag", (NBTBase)nbttagcompound2);
                    cmp.a("tag", (NBTBase)nbttagcompound1);
                }
                if (short0 != 0) {
                    cmp.a("Damage", (short)0);
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
        public NBTTagCompound convert(NBTTagCompound cmp) {
            if ("Minecart".equals(cmp.l("id"))) {
                String s = "MinecartRideable";
                int i = cmp.h("Type");
                if (i > 0 && i < a.size()) {
                    s = a.get(i);
                }
                cmp.a("id", s);
                cmp.r("Type");
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
        public NBTTagCompound convert(NBTTagCompound cmp) {
            if (!"MobSpawner".equals(cmp.l("id"))) {
                return cmp;
            }
            if (cmp.b("EntityId", 8)) {
                String s = cmp.l("EntityId");
                NBTTagCompound nbttagcompound1 = cmp.p("SpawnData");
                nbttagcompound1.a("id", s.isEmpty() ? "Pig" : s);
                cmp.a("SpawnData", (NBTBase)nbttagcompound1);
                cmp.r("EntityId");
            }
            if (cmp.b("SpawnPotentials", 9)) {
                NBTTagList nbttaglist = cmp.c("SpawnPotentials", 10);
                for (int i = 0; i < nbttaglist.size(); ++i) {
                    NBTTagCompound nbttagcompound2 = nbttaglist.a(i);
                    if (!nbttagcompound2.b("Type", 8)) continue;
                    NBTTagCompound nbttagcompound3 = nbttagcompound2.p("Properties");
                    nbttagcompound3.a("id", nbttagcompound2.l("Type"));
                    nbttagcompound2.a("Entity", (NBTBase)nbttagcompound3);
                    nbttagcompound2.r("Type");
                    nbttagcompound2.r("Properties");
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
        public NBTTagCompound convert(NBTTagCompound cmp) {
            if (cmp.b("UUID", 8)) {
                cmp.a("UUID", UUID.fromString(cmp.l("UUID")));
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
        public NBTTagCompound convert(NBTTagCompound cmp) {
            if (a.contains(cmp.l("id"))) {
                float f;
                if (cmp.b("HealF", 99)) {
                    f = cmp.j("HealF");
                    cmp.r("HealF");
                } else {
                    if (!cmp.b("Health", 99)) {
                        return cmp;
                    }
                    f = cmp.j("Health");
                }
                cmp.a("Health", f);
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
        public NBTTagCompound convert(NBTTagCompound cmp) {
            if ("EntityHorse".equals(cmp.l("id")) && !cmp.b("SaddleItem", 10) && cmp.q("Saddle")) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.a("id", "minecraft:saddle");
                nbttagcompound1.a("Count", (byte)1);
                nbttagcompound1.a("Damage", (short)0);
                cmp.a("SaddleItem", (NBTBase)nbttagcompound1);
                cmp.r("Saddle");
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
        public NBTTagCompound convert(NBTTagCompound cmp) {
            String s = cmp.l("id");
            boolean flag = "Painting".equals(s);
            boolean flag1 = "ItemFrame".equals(s);
            if ((flag || flag1) && !cmp.b("Facing", 99)) {
                EnumDirection enumdirection;
                if (cmp.b("Direction", 99)) {
                    enumdirection = EnumDirection.b((int)cmp.f("Direction"));
                    cmp.a("TileX", cmp.h("TileX") + enumdirection.j());
                    cmp.a("TileY", cmp.h("TileY") + enumdirection.k());
                    cmp.a("TileZ", cmp.h("TileZ") + enumdirection.l());
                    cmp.r("Direction");
                    if (flag1 && cmp.b("ItemRotation", 99)) {
                        cmp.a("ItemRotation", (byte)(cmp.f("ItemRotation") * 2));
                    }
                } else {
                    enumdirection = EnumDirection.b((int)cmp.f("Dir"));
                    cmp.r("Dir");
                }
                cmp.a("Facing", (byte)enumdirection.e());
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
        public NBTTagCompound convert(NBTTagCompound cmp) {
            NBTTagList nbttaglist;
            if (cmp.b("HandDropChances", 9) && (nbttaglist = cmp.c("HandDropChances", 5)).size() == 2 && nbttaglist.i(0) == 0.0f && nbttaglist.i(1) == 0.0f) {
                cmp.r("HandDropChances");
            }
            if (cmp.b("ArmorDropChances", 9) && (nbttaglist = cmp.c("ArmorDropChances", 5)).size() == 4 && nbttaglist.i(0) == 0.0f && nbttaglist.i(1) == 0.0f && nbttaglist.i(2) == 0.0f && nbttaglist.i(3) == 0.0f) {
                cmp.r("ArmorDropChances");
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
        public NBTTagCompound convert(NBTTagCompound cmp) {
            while (cmp.b("Riding", 10)) {
                NBTTagCompound nbttagcompound1 = this.b(cmp);
                this.convert(cmp, nbttagcompound1);
                cmp = nbttagcompound1;
            }
            return cmp;
        }

        protected void convert(NBTTagCompound nbttagcompound, NBTTagCompound nbttagcompound1) {
            NBTTagList nbttaglist = new NBTTagList();
            nbttaglist.add((Object)nbttagcompound);
            nbttagcompound1.a("Passengers", (NBTBase)nbttaglist);
        }

        protected NBTTagCompound b(NBTTagCompound nbttagcompound) {
            NBTTagCompound nbttagcompound1 = nbttagcompound.p("Riding");
            nbttagcompound.r("Riding");
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
        public NBTTagCompound convert(NBTTagCompound cmp) {
            if ("ArmorStand".equals(cmp.l("id")) && cmp.q("Silent") && !cmp.q("Marker")) {
                cmp.r("Silent");
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
        public NBTTagCompound convert(NBTTagCompound cmp) {
            NBTTagCompound nbttagcompound1;
            if ("minecraft:written_book".equals(cmp.l("id")) && (nbttagcompound1 = cmp.p("tag")).b("pages", 9)) {
                NBTTagList nbttaglist = nbttagcompound1.c("pages", 8);
                for (int i = 0; i < nbttaglist.size(); ++i) {
                    String s = nbttaglist.j(i);
                    IChatMutableComponent object = null;
                    if (!"null".equals(s) && !UtilColor.b((String)s)) {
                        if (!(s.charAt(0) == '\"' && s.charAt(s.length() - 1) == '\"' || s.charAt(0) == '{' && s.charAt(s.length() - 1) == '}')) {
                            object = IChatBaseComponent.b((String)s);
                        } else {
                            try {
                                object = (IChatBaseComponent)ChatDeserializer.a((Gson)DataConverterSignText.a, (String)s, IChatBaseComponent.class, (boolean)true);
                                if (object == null) {
                                    object = IChatBaseComponent.b((String)"");
                                }
                            }
                            catch (JsonParseException jsonParseException) {
                                // empty catch block
                            }
                            if (object == null) {
                                try {
                                    object = IChatBaseComponent.ChatSerializer.a((String)s, (HolderLookup.a)MinecraftServer.getServer().ba());
                                }
                                catch (JsonParseException jsonParseException) {
                                    // empty catch block
                                }
                            }
                            if (object == null) {
                                try {
                                    object = IChatBaseComponent.ChatSerializer.b((String)s, (HolderLookup.a)MinecraftServer.getServer().ba());
                                }
                                catch (JsonParseException jsonParseException) {
                                    // empty catch block
                                }
                            }
                            if (object == null) {
                                object = IChatBaseComponent.b((String)s);
                            }
                        }
                    } else {
                        object = IChatBaseComponent.b((String)"");
                    }
                    nbttaglist.d(i, (NBTBase)NBTTagString.a((String)IChatBaseComponent.ChatSerializer.a((IChatBaseComponent)object, (HolderLookup.a)MinecraftServer.getServer().ba())));
                }
                nbttagcompound1.a("pages", (NBTBase)nbttaglist);
            }
            return cmp;
        }
    }

    private static class DataConverterCookedFish
    implements DataConverter {
        private static final MinecraftKey a = MinecraftKey.a((String)"cooked_fished");

        DataConverterCookedFish() {
        }

        @Override
        public int getDataVersion() {
            return 502;
        }

        @Override
        public NBTTagCompound convert(NBTTagCompound cmp) {
            if (cmp.b("id", 8) && a.equals((Object)MinecraftKey.a((String)cmp.l("id")))) {
                cmp.a("id", "minecraft:cooked_fish");
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
        public NBTTagCompound convert(NBTTagCompound cmp) {
            if ("Zombie".equals(cmp.l("id")) && cmp.q("IsVillager")) {
                if (!cmp.b("ZombieType", 99)) {
                    int i = -1;
                    if (cmp.b("VillagerProfession", 99)) {
                        try {
                            i = this.convert(cmp.h("VillagerProfession"));
                        }
                        catch (RuntimeException runtimeException) {
                            // empty catch block
                        }
                    }
                    if (i == -1) {
                        i = this.convert(a.nextInt(6));
                    }
                    cmp.a("ZombieType", i);
                }
                cmp.r("IsVillager");
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
        public NBTTagCompound convert(NBTTagCompound cmp) {
            cmp.a("useVbo", "true");
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
        public NBTTagCompound convert(NBTTagCompound cmp) {
            if ("Guardian".equals(cmp.l("id"))) {
                if (cmp.q("Elder")) {
                    cmp.a("id", "ElderGuardian");
                }
                cmp.r("Elder");
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
        public NBTTagCompound convert(NBTTagCompound cmp) {
            String s = cmp.l("id");
            if ("Skeleton".equals(s)) {
                int i = cmp.h("SkeletonType");
                if (i == 1) {
                    cmp.a("id", "WitherSkeleton");
                } else if (i == 2) {
                    cmp.a("id", "Stray");
                }
                cmp.r("SkeletonType");
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
        public NBTTagCompound convert(NBTTagCompound cmp) {
            if ("Zombie".equals(cmp.l("id"))) {
                int i = cmp.h("ZombieType");
                switch (i) {
                    default: {
                        break;
                    }
                    case 1: 
                    case 2: 
                    case 3: 
                    case 4: 
                    case 5: {
                        cmp.a("id", "ZombieVillager");
                        cmp.a("Profession", i - 1);
                        break;
                    }
                    case 6: {
                        cmp.a("id", "Husk");
                    }
                }
                cmp.r("ZombieType");
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
        public NBTTagCompound convert(NBTTagCompound cmp) {
            if ("EntityHorse".equals(cmp.l("id"))) {
                int i = cmp.h("Type");
                switch (i) {
                    default: {
                        cmp.a("id", "Horse");
                        break;
                    }
                    case 1: {
                        cmp.a("id", "Donkey");
                        break;
                    }
                    case 2: {
                        cmp.a("id", "Mule");
                        break;
                    }
                    case 3: {
                        cmp.a("id", "ZombieHorse");
                        break;
                    }
                    case 4: {
                        cmp.a("id", "SkeletonHorse");
                    }
                }
                cmp.r("Type");
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
        public NBTTagCompound convert(NBTTagCompound cmp) {
            String s = a.get(cmp.l("id"));
            if (s != null) {
                cmp.a("id", s);
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
        public NBTTagCompound convert(NBTTagCompound cmp) {
            String s = a.get(cmp.l("id"));
            if (s != null) {
                cmp.a("id", s);
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
        public NBTTagCompound convert(NBTTagCompound cmp) {
            NBTTagCompound nbttagcompound2;
            NBTTagCompound nbttagcompound1;
            if ("minecraft:banner".equals(cmp.l("id")) && cmp.b("tag", 10) && (nbttagcompound1 = cmp.p("tag")).b("BlockEntityTag", 10) && (nbttagcompound2 = nbttagcompound1.p("BlockEntityTag")).b("Base", 99)) {
                NBTTagList nbttaglist;
                NBTTagCompound nbttagcompound3;
                cmp.a("Damage", (short)(nbttagcompound2.g("Base") & 0xF));
                if (nbttagcompound1.b("display", 10) && (nbttagcompound3 = nbttagcompound1.p("display")).b("Lore", 9) && (nbttaglist = nbttagcompound3.c("Lore", 8)).size() == 1 && "(+NBT)".equals(nbttaglist.j(0))) {
                    return cmp;
                }
                nbttagcompound2.r("Base");
                if (nbttagcompound2.g()) {
                    nbttagcompound1.r("BlockEntityTag");
                }
                if (nbttagcompound1.g()) {
                    cmp.r("tag");
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
        public NBTTagCompound convert(NBTTagCompound cmp) {
            String s = cmp.l("id");
            if ("minecraft:potion".equals(s) || "minecraft:splash_potion".equals(s) || "minecraft:lingering_potion".equals(s) || "minecraft:tipped_arrow".equals(s)) {
                NBTTagCompound nbttagcompound1 = cmp.p("tag");
                if (!nbttagcompound1.b("Potion", 8)) {
                    nbttagcompound1.a("Potion", "minecraft:water");
                }
                if (!cmp.b("tag", 10)) {
                    cmp.a("tag", (NBTBase)nbttagcompound1);
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
        public NBTTagCompound convert(NBTTagCompound cmp) {
            if ("minecraft:shulker".equals(cmp.l("id")) && !cmp.b("Color", 99)) {
                cmp.a("Color", (byte)10);
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
        public NBTTagCompound convert(NBTTagCompound cmp) {
            NBTTagCompound nbttagcompound1;
            if ("minecraft:shulker_box".equals(cmp.l("id")) && cmp.b("tag", 10) && (nbttagcompound1 = cmp.p("tag")).b("BlockEntityTag", 10)) {
                NBTTagCompound nbttagcompound2 = nbttagcompound1.p("BlockEntityTag");
                if (nbttagcompound2.c("Items", 10).isEmpty()) {
                    nbttagcompound2.r("Items");
                }
                int i = nbttagcompound2.h("Color");
                nbttagcompound2.r("Color");
                if (nbttagcompound2.g()) {
                    nbttagcompound1.r("BlockEntityTag");
                }
                if (nbttagcompound1.g()) {
                    cmp.r("tag");
                }
                cmp.a("id", a[i % 16]);
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
        public NBTTagCompound convert(NBTTagCompound cmp) {
            if ("minecraft:shulker".equals(cmp.l("id"))) {
                cmp.r("Color");
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
        public NBTTagCompound convert(NBTTagCompound cmp) {
            if (cmp.b("lang", 8)) {
                cmp.a("lang", cmp.l("lang").toLowerCase(Locale.ROOT));
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
        public NBTTagCompound convert(NBTTagCompound cmp) {
            if ("minecraft:totem".equals(cmp.l("id"))) {
                cmp.a("id", "minecraft:totem_of_undying");
            }
            return cmp;
        }
    }

    private static class DataConverterBedBlock
    implements DataConverter {
        private static final Logger a = LogManager.getLogger(PaperweightDataConverters.class);

        DataConverterBedBlock() {
        }

        @Override
        public int getDataVersion() {
            return 1125;
        }

        @Override
        public NBTTagCompound convert(NBTTagCompound cmp) {
            try {
                NBTTagCompound nbttagcompound1 = cmp.p("Level");
                int i = nbttagcompound1.h("xPos");
                int j = nbttagcompound1.h("zPos");
                NBTTagList nbttaglist = nbttagcompound1.c("TileEntities", 10);
                NBTTagList nbttaglist1 = nbttagcompound1.c("Sections", 10);
                for (int k = 0; k < nbttaglist1.size(); ++k) {
                    NBTTagCompound nbttagcompound2 = nbttaglist1.a(k);
                    byte b0 = nbttagcompound2.f("Y");
                    byte[] abyte = nbttagcompound2.m("Blocks");
                    for (int l = 0; l < abyte.length; ++l) {
                        if (416 != (abyte[l] & 0xFF) << 4) continue;
                        int i1 = l & 0xF;
                        int j1 = l >> 8 & 0xF;
                        int k1 = l >> 4 & 0xF;
                        NBTTagCompound nbttagcompound3 = new NBTTagCompound();
                        nbttagcompound3.a("id", "bed");
                        nbttagcompound3.a("x", i1 + (i << 4));
                        nbttagcompound3.a("y", j1 + (b0 << 4));
                        nbttagcompound3.a("z", k1 + (j << 4));
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
        public NBTTagCompound convert(NBTTagCompound cmp) {
            if ("minecraft:bed".equals(cmp.l("id")) && cmp.g("Damage") == 0) {
                cmp.a("Damage", (short)EnumColor.o.a());
            }
            return cmp;
        }
    }

    private static abstract class DataInspectorTagged
    implements DataInspector {
        private final MinecraftKey key;

        DataInspectorTagged(String type) {
            this.key = PaperweightDataConverters.getKey(type);
        }

        @Override
        public NBTTagCompound inspect(NBTTagCompound cmp, int sourceVer, int targetVer) {
            if (this.key.equals((Object)MinecraftKey.a((String)cmp.l("id")))) {
                cmp = this.inspectChecked(cmp, sourceVer, targetVer);
            }
            return cmp;
        }

        abstract NBTTagCompound inspectChecked(NBTTagCompound var1, int var2, int var3);
    }
}

