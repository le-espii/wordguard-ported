/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.google.common.util.concurrent.Futures
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Lifecycle
 *  java.lang.runtime.SwitchBootstraps
 *  javax.annotation.Nullable
 *  net.minecraft.SharedConstants
 *  net.minecraft.SystemUtils
 *  net.minecraft.core.BaseBlockPosition
 *  net.minecraft.core.BlockPosition
 *  net.minecraft.core.EnumDirection
 *  net.minecraft.core.Holder
 *  net.minecraft.core.HolderLookup$a
 *  net.minecraft.core.HolderSet$b
 *  net.minecraft.core.IRegistry
 *  net.minecraft.core.IRegistryCustom$Dimension
 *  net.minecraft.core.SectionPosition
 *  net.minecraft.core.component.DataComponentPatch
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.DynamicOpsNBT
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagByte
 *  net.minecraft.nbt.NBTTagByteArray
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagDouble
 *  net.minecraft.nbt.NBTTagEnd
 *  net.minecraft.nbt.NBTTagFloat
 *  net.minecraft.nbt.NBTTagInt
 *  net.minecraft.nbt.NBTTagIntArray
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.nbt.NBTTagLong
 *  net.minecraft.nbt.NBTTagLongArray
 *  net.minecraft.nbt.NBTTagShort
 *  net.minecraft.nbt.NBTTagString
 *  net.minecraft.network.chat.IChatBaseComponent
 *  net.minecraft.network.chat.IChatBaseComponent$ChatSerializer
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.PacketPlayOutEntityStatus
 *  net.minecraft.network.protocol.game.PacketPlayOutTileEntityData
 *  net.minecraft.resources.MinecraftKey
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.dedicated.DedicatedServer
 *  net.minecraft.server.level.ChunkProviderServer
 *  net.minecraft.server.level.WorldServer
 *  net.minecraft.server.level.progress.WorldLoadListener
 *  net.minecraft.util.INamable
 *  net.minecraft.util.RandomSource
 *  net.minecraft.util.thread.IAsyncTaskHandler
 *  net.minecraft.world.Clearable
 *  net.minecraft.world.EnumHand
 *  net.minecraft.world.EnumInteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntitySpawnReason
 *  net.minecraft.world.entity.EntityTypes
 *  net.minecraft.world.entity.player.EntityHuman
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.ItemActionContext
 *  net.minecraft.world.level.ChunkCoordIntPair
 *  net.minecraft.world.level.GeneratorAccessSeed
 *  net.minecraft.world.level.IWorldReader
 *  net.minecraft.world.level.LevelHeightAccessor
 *  net.minecraft.world.level.World
 *  net.minecraft.world.level.WorldSettings
 *  net.minecraft.world.level.biome.BiomeBase
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.entity.TileEntity
 *  net.minecraft.world.level.block.entity.TileEntityStructure
 *  net.minecraft.world.level.block.state.BlockStateList
 *  net.minecraft.world.level.block.state.IBlockData
 *  net.minecraft.world.level.block.state.properties.BlockStateBoolean
 *  net.minecraft.world.level.block.state.properties.BlockStateEnum
 *  net.minecraft.world.level.block.state.properties.BlockStateInteger
 *  net.minecraft.world.level.block.state.properties.IBlockState
 *  net.minecraft.world.level.chunk.Chunk
 *  net.minecraft.world.level.chunk.IChunkAccess
 *  net.minecraft.world.level.chunk.status.ChunkStatus
 *  net.minecraft.world.level.dimension.WorldDimension
 *  net.minecraft.world.level.levelgen.WorldOptions
 *  net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured
 *  net.minecraft.world.level.levelgen.structure.Structure
 *  net.minecraft.world.level.levelgen.structure.StructureBoundingBox
 *  net.minecraft.world.level.levelgen.structure.StructureStart
 *  net.minecraft.world.level.storage.Convertable
 *  net.minecraft.world.level.storage.Convertable$ConversionSession
 *  net.minecraft.world.level.storage.WorldDataServer
 *  net.minecraft.world.level.storage.WorldDataServer$a
 *  net.minecraft.world.phys.MovingObjectPositionBlock
 *  net.minecraft.world.phys.Vec3D
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.World$Environment
 *  org.bukkit.block.data.BlockData
 *  org.bukkit.craftbukkit.v1_21_R2.CraftServer
 *  org.bukkit.craftbukkit.v1_21_R2.CraftWorld
 *  org.bukkit.craftbukkit.v1_21_R2.block.data.CraftBlockData
 *  org.bukkit.craftbukkit.v1_21_R2.entity.CraftEntity
 *  org.bukkit.craftbukkit.v1_21_R2.entity.CraftPlayer
 *  org.bukkit.craftbukkit.v1_21_R2.inventory.CraftItemStack
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.entity.CreatureSpawnEvent$SpawnReason
 *  org.bukkit.generator.ChunkGenerator
 *  org.bukkit.inventory.ItemStack
 *  org.jetbrains.annotations.Nullable
 *  org.spigotmc.SpigotConfig
 *  org.spigotmc.WatchdogThread
 */
package com.sk89q.worldedit.bukkit.adapter.impl.v1_21_3;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Futures;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseItem;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.adapter.BukkitImplAdapter;
import com.sk89q.worldedit.bukkit.adapter.impl.v1_21_3.PaperweightDataConverters;
import com.sk89q.worldedit.bukkit.adapter.impl.v1_21_3.PaperweightFakePlayer;
import com.sk89q.worldedit.bukkit.adapter.impl.v1_21_3.PaperweightServerLevelDelegateProxy;
import com.sk89q.worldedit.bukkit.adapter.impl.v1_21_3.PaperweightWorldNativeAccess;
import com.sk89q.worldedit.bukkit.adapter.impl.v1_21_3.StaticRefraction;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.extension.platform.Watchdog;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.internal.Constants;
import com.sk89q.worldedit.internal.block.BlockStateIdAccess;
import com.sk89q.worldedit.internal.wna.WorldNativeAccess;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.registry.state.BooleanProperty;
import com.sk89q.worldedit.registry.state.DirectionalProperty;
import com.sk89q.worldedit.registry.state.EnumProperty;
import com.sk89q.worldedit.registry.state.IntegerProperty;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.SideEffect;
import com.sk89q.worldedit.util.concurrency.LazyReference;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.formatting.text.serializer.gson.GsonComponentSerializer;
import com.sk89q.worldedit.util.io.file.SafeFiles;
import com.sk89q.worldedit.world.DataFixer;
import com.sk89q.worldedit.world.RegenOptions;
import com.sk89q.worldedit.world.biome.BiomeCategory;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.biome.BiomeTypes;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldedit.world.generation.ConfiguredFeatureType;
import com.sk89q.worldedit.world.generation.StructureType;
import com.sk89q.worldedit.world.item.ItemType;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.runtime.SwitchBootstraps;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import net.minecraft.SharedConstants;
import net.minecraft.SystemUtils;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.SectionPosition;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagEnd;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagLongArray;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutEntityStatus;
import net.minecraft.network.protocol.game.PacketPlayOutTileEntityData;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ChunkProviderServer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.level.progress.WorldLoadListener;
import net.minecraft.util.INamable;
import net.minecraft.util.RandomSource;
import net.minecraft.util.thread.IAsyncTaskHandler;
import net.minecraft.world.Clearable;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.ItemActionContext;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.WorldSettings;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityStructure;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.BlockStateEnum;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.level.block.state.properties.IBlockState;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.dimension.WorldDimension;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.storage.Convertable;
import net.minecraft.world.level.storage.WorldDataServer;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_21_R2.CraftServer;
import org.bukkit.craftbukkit.v1_21_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R2.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_21_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R2.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.generator.ChunkGenerator;
import org.enginehub.linbus.common.LinTagId;
import org.enginehub.linbus.tree.LinByteArrayTag;
import org.enginehub.linbus.tree.LinByteTag;
import org.enginehub.linbus.tree.LinCompoundTag;
import org.enginehub.linbus.tree.LinDoubleTag;
import org.enginehub.linbus.tree.LinEndTag;
import org.enginehub.linbus.tree.LinFloatTag;
import org.enginehub.linbus.tree.LinIntArrayTag;
import org.enginehub.linbus.tree.LinIntTag;
import org.enginehub.linbus.tree.LinListTag;
import org.enginehub.linbus.tree.LinLongArrayTag;
import org.enginehub.linbus.tree.LinLongTag;
import org.enginehub.linbus.tree.LinShortTag;
import org.enginehub.linbus.tree.LinStringTag;
import org.enginehub.linbus.tree.LinTag;
import org.enginehub.linbus.tree.LinTagType;
import org.jetbrains.annotations.Nullable;
import org.spigotmc.SpigotConfig;
import org.spigotmc.WatchdogThread;

public final class PaperweightAdapter
implements BukkitImplAdapter {
    private final Logger logger = Logger.getLogger(this.getClass().getCanonicalName());
    private final Field serverWorldsField;
    private final Method getChunkFutureMethod;
    private final Field chunkProviderExecutorField;
    private final PaperweightDataConverters dataFixer;
    private final Watchdog watchdog;
    private static final RandomSource random = RandomSource.a();
    private static final HashMap<BiomeType, Holder<BiomeBase>> biomeTypeToNMSCache = new HashMap();
    private static final HashMap<Holder<BiomeBase>, BiomeType> biomeTypeFromNMSCache = new HashMap();
    private static final LoadingCache<IBlockState, Property<?>> PROPERTY_CACHE = CacheBuilder.newBuilder().build(new CacheLoader<IBlockState, Property<?>>(){

        public Property<?> load(IBlockState state) {
            IBlockState iBlockState = state;
            Objects.requireNonNull(iBlockState);
            IBlockState iBlockState2 = iBlockState;
            int n = 0;
            return switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{BlockStateBoolean.class, BlockStateEnum.class, BlockStateInteger.class}, (Object)iBlockState2, (int)n)) {
                case 0 -> {
                    BlockStateBoolean ignored = (BlockStateBoolean)iBlockState2;
                    yield new BooleanProperty(state.f(), (List<Boolean>)ImmutableList.copyOf((Collection)state.a()));
                }
                case 1 -> {
                    BlockStateEnum ignored = (BlockStateEnum)iBlockState2;
                    if (state.g() == EnumDirection.class) {
                        yield new DirectionalProperty(state.f(), state.a().stream().map(e -> Direction.valueOf(((INamable)e).c().toUpperCase(Locale.ROOT))).toList());
                    }
                    yield new EnumProperty(state.f(), state.a().stream().map(e -> ((INamable)e).c()).toList());
                }
                case 2 -> {
                    BlockStateInteger ignored = (BlockStateInteger)iBlockState2;
                    yield new IntegerProperty(state.f(), (List<Integer>)ImmutableList.copyOf((Collection)state.a()));
                }
                default -> throw new IllegalArgumentException("WorldEdit needs an update to support " + state.getClass().getSimpleName());
            };
        }
    });
    private static final Codec<DataComponentPatch> COMPONENTS_CODEC = DataComponentPatch.b.optionalFieldOf("components", (Object)DataComponentPatch.a).codec();
    private final LoadingCache<WorldServer, PaperweightFakePlayer> fakePlayers = CacheBuilder.newBuilder().weakKeys().softValues().build(CacheLoader.from(PaperweightFakePlayer::new));
    private static final Set<SideEffect> SUPPORTED_SIDE_EFFECTS = Sets.immutableEnumSet((Enum)SideEffect.NEIGHBORS, (Enum[])new SideEffect[]{SideEffect.LIGHTING, SideEffect.VALIDATION, SideEffect.ENTITY_AI, SideEffect.EVENTS, SideEffect.UPDATE});

    public PaperweightAdapter() throws NoSuchFieldException, NoSuchMethodException {
        Watchdog watchdog;
        CraftServer.class.cast(Bukkit.getServer());
        int dataVersion = SharedConstants.b().d().c();
        if (dataVersion != 4082) {
            throw new UnsupportedClassVersionError("Not 1.21.3!");
        }
        this.serverWorldsField = CraftServer.class.getDeclaredField("worlds");
        this.serverWorldsField.setAccessible(true);
        this.getChunkFutureMethod = ChunkProviderServer.class.getDeclaredMethod(StaticRefraction.GET_CHUNK_FUTURE_MAIN_THREAD, Integer.TYPE, Integer.TYPE, ChunkStatus.class, Boolean.TYPE);
        this.getChunkFutureMethod.setAccessible(true);
        this.chunkProviderExecutorField = ChunkProviderServer.class.getDeclaredField(StaticRefraction.MAIN_THREAD_PROCESSOR);
        this.chunkProviderExecutorField.setAccessible(true);
        this.dataFixer = new PaperweightDataConverters(dataVersion, this);
        try {
            Class.forName("org.spigotmc.WatchdogThread");
            watchdog = new SpigotWatchdog();
        }
        catch (ClassNotFoundException | NoSuchFieldException e) {
            try {
                watchdog = new MojangWatchdog(((CraftServer)Bukkit.getServer()).getServer());
            }
            catch (NoSuchFieldException ex) {
                watchdog = null;
            }
        }
        this.watchdog = watchdog;
        try {
            Class.forName("org.spigotmc.SpigotConfig");
            SpigotConfig.config.set("world-settings.worldeditregentempworld.verbose", (Object)false);
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
    }

    @Override
    public DataFixer getDataFixer() {
        return this.dataFixer;
    }

    static void readTagIntoTileEntity(NBTTagCompound tag, TileEntity tileEntity) {
        tileEntity.c(tag, (HolderLookup.a)MinecraftServer.getServer().ba());
        tileEntity.e();
    }

    private static String getEntityId(net.minecraft.world.entity.Entity entity) {
        return EntityTypes.a((EntityTypes)entity.aq()).toString();
    }

    private static void readEntityIntoTag(net.minecraft.world.entity.Entity entity, NBTTagCompound tag) {
        entity.e(tag);
    }

    private static Block getBlockFromType(BlockType blockType) {
        return (Block)DedicatedServer.getServer().ba().e(Registries.f).a(MinecraftKey.c((String)blockType.id()));
    }

    private static Item getItemFromType(ItemType itemType) {
        return (Item)DedicatedServer.getServer().ba().e(Registries.K).a(MinecraftKey.c((String)itemType.id()));
    }

    @Override
    public OptionalInt getInternalBlockStateId(BlockData data) {
        IBlockData state = ((CraftBlockData)data).getState();
        int combinedId = Block.j((IBlockData)state);
        return combinedId == 0 && state.b() != Blocks.a ? OptionalInt.empty() : OptionalInt.of(combinedId);
    }

    @Override
    public OptionalInt getInternalBlockStateId(BlockState state) {
        Block mcBlock = PaperweightAdapter.getBlockFromType(state.getBlockType());
        IBlockData newState = mcBlock.m();
        Map<Property<?>, Object> states = state.getStates();
        newState = this.applyProperties((BlockStateList<Block, IBlockData>)mcBlock.l(), newState, states);
        int combinedId = Block.j((IBlockData)newState);
        return combinedId == 0 && state.getBlockType() != BlockTypes.AIR ? OptionalInt.empty() : OptionalInt.of(combinedId);
    }

    public BlockState adapt(IBlockData blockState) {
        int internalId = Block.j((IBlockData)blockState);
        BlockState state = BlockStateIdAccess.getBlockStateById(internalId);
        if (state == null) {
            state = BukkitAdapter.adapt((BlockData)CraftBlockData.createData((IBlockData)blockState));
        }
        return state;
    }

    public BiomeType adapt(BiomeBase biome) {
        MinecraftKey mcBiome = ((CraftServer)Bukkit.getServer()).getServer().ba().e(Registries.aI).b((Object)biome);
        if (mcBiome == null) {
            return null;
        }
        return BiomeType.REGISTRY.get(mcBiome.toString());
    }

    public IBlockData adapt(BlockState blockState) {
        int internalId = BlockStateIdAccess.getBlockStateId(blockState);
        return Block.a((int)internalId);
    }

    @Override
    public BlockState getBlock(Location location) {
        Preconditions.checkNotNull((Object)location);
        CraftWorld craftWorld = (CraftWorld)location.getWorld();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        WorldServer handle = craftWorld.getHandle();
        Chunk chunk = handle.d(x >> 4, z >> 4);
        BlockPosition blockPos = new BlockPosition(x, y, z);
        IBlockData blockData = chunk.a_(blockPos);
        return this.adapt(blockData);
    }

    @Override
    public BaseBlock getFullBlock(Location location) {
        BlockPosition blockPos;
        BlockState state = this.getBlock(location);
        CraftWorld craftWorld = (CraftWorld)location.getWorld();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        WorldServer handle = craftWorld.getHandle();
        Chunk chunk = handle.d(x >> 4, z >> 4);
        TileEntity te = chunk.c_(blockPos = new BlockPosition(x, y, z));
        if (te != null) {
            NBTTagCompound tag = te.c((HolderLookup.a)MinecraftServer.getServer().ba());
            return state.toBaseBlock(LazyReference.from(() -> (LinCompoundTag)this.toNative((NBTBase)tag)));
        }
        return state.toBaseBlock();
    }

    @Override
    public BiomeType getBiome(Location location) {
        Preconditions.checkNotNull((Object)location);
        CraftWorld craftWorld = (CraftWorld)location.getWorld();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        WorldServer handle = craftWorld.getHandle();
        Chunk chunk = handle.d(x >> 4, z >> 4);
        return biomeTypeFromNMSCache.computeIfAbsent((Holder<BiomeBase>)chunk.getNoiseBiome(x >> 2, y >> 2, z >> 2), b2 -> BiomeType.REGISTRY.get(((ResourceKey)b2.e().orElseThrow()).a().toString()));
    }

    @Override
    public void setBiome(Location location, BiomeType biome) {
        Preconditions.checkNotNull((Object)location);
        Preconditions.checkNotNull((Object)biome);
        CraftWorld craftWorld = (CraftWorld)location.getWorld();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        WorldServer handle = craftWorld.getHandle();
        Chunk chunk = handle.d(x >> 4, z >> 4);
        chunk.setBiome(x >> 2, y >> 2, z >> 2, biomeTypeToNMSCache.computeIfAbsent(biome, b2 -> ((CraftServer)Bukkit.getServer()).getServer().ba().e(Registries.aI).b(ResourceKey.a((ResourceKey)Registries.aI, (MinecraftKey)MinecraftKey.a((String)b2.id())))));
        chunk.i();
    }

    @Override
    public WorldNativeAccess<?, ?, ?> createWorldNativeAccess(World world) {
        return new PaperweightWorldNativeAccess(this, new WeakReference<WorldServer>(((CraftWorld)world).getHandle()));
    }

    private static EnumDirection adapt(Direction face) {
        return switch (face) {
            case Direction.NORTH -> EnumDirection.c;
            case Direction.SOUTH -> EnumDirection.d;
            case Direction.WEST -> EnumDirection.e;
            case Direction.EAST -> EnumDirection.f;
            case Direction.DOWN -> EnumDirection.a;
            default -> EnumDirection.b;
        };
    }

    private IBlockData applyProperties(BlockStateList<Block, IBlockData> stateContainer, IBlockData newState, Map<Property<?>, Object> states) {
        for (Map.Entry<Property<?>, Object> state : states.entrySet()) {
            IBlockState property = stateContainer.a(state.getKey().getName());
            Comparable value = (Comparable)state.getValue();
            if (property instanceof BlockStateEnum) {
                if (property.g() == EnumDirection.class) {
                    value = PaperweightAdapter.adapt((Direction)((Object)value));
                } else {
                    String enumName = (String)((Object)value);
                    value = (Comparable)((BlockStateEnum)property).b(enumName).orElseThrow(() -> new IllegalStateException("Enum property " + property.f() + " does not contain " + enumName));
                }
            }
            newState = (IBlockData)newState.b(property, value);
        }
        return newState;
    }

    @Override
    public BaseEntity getEntity(Entity entity) {
        Preconditions.checkNotNull((Object)entity);
        CraftEntity craftEntity = (CraftEntity)entity;
        net.minecraft.world.entity.Entity mcEntity = craftEntity.getHandle();
        if (mcEntity.bZ()) {
            return null;
        }
        String id = PaperweightAdapter.getEntityId(mcEntity);
        NBTTagCompound tag = new NBTTagCompound();
        PaperweightAdapter.readEntityIntoTag(mcEntity, tag);
        return new BaseEntity(com.sk89q.worldedit.world.entity.EntityTypes.get(id), LazyReference.from(() -> (LinCompoundTag)this.toNative((NBTBase)tag)));
    }

    @Override
    @javax.annotation.Nullable
    public Entity createEntity(Location location, BaseEntity state) {
        NBTTagCompound tag;
        Preconditions.checkNotNull((Object)location);
        Preconditions.checkNotNull((Object)state);
        CraftWorld craftWorld = (CraftWorld)location.getWorld();
        WorldServer worldServer = craftWorld.getHandle();
        String entityId = state.getType().id();
        LinCompoundTag nativeTag = state.getNbt();
        if (nativeTag != null) {
            tag = (NBTTagCompound)this.fromNative(nativeTag);
            this.removeUnwantedEntityTagsRecursively(tag);
        } else {
            tag = new NBTTagCompound();
        }
        tag.a("id", entityId);
        net.minecraft.world.entity.Entity createdEntity = EntityTypes.a((NBTTagCompound)tag, (net.minecraft.world.level.World)craftWorld.getHandle(), (EntitySpawnReason)EntitySpawnReason.n, loadedEntity -> {
            loadedEntity.a(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            return loadedEntity;
        });
        if (createdEntity != null) {
            worldServer.addFreshEntityWithPassengers(createdEntity, CreatureSpawnEvent.SpawnReason.CUSTOM);
            return createdEntity.getBukkitEntity();
        }
        return null;
    }

    private void removeUnwantedEntityTagsRecursively(NBTTagCompound tag) {
        for (String name : Constants.NO_COPY_ENTITY_NBT_FIELDS) {
            tag.r(name);
        }
        if (tag.b("Passengers", LinTagId.LIST.id())) {
            NBTTagList nbttaglist = tag.c("Passengers", LinTagId.COMPOUND.id());
            for (int i = 0; i < nbttaglist.size(); ++i) {
                this.removeUnwantedEntityTagsRecursively(nbttaglist.a(i));
            }
        }
    }

    @Override
    public Component getRichBlockName(BlockType blockType) {
        return TranslatableComponent.of(PaperweightAdapter.getBlockFromType(blockType).v());
    }

    @Override
    public Component getRichItemName(ItemType itemType) {
        return TranslatableComponent.of(PaperweightAdapter.getItemFromType(itemType).l());
    }

    @Override
    public Component getRichItemName(BaseItemStack itemStack) {
        return GsonComponentSerializer.INSTANCE.deserialize(IChatBaseComponent.ChatSerializer.a((IChatBaseComponent)CraftItemStack.asNMSCopy((org.bukkit.inventory.ItemStack)BukkitAdapter.adapt(itemStack)).z(), (HolderLookup.a)((CraftServer)Bukkit.getServer()).getServer().ba()));
    }

    @Override
    public Map<String, ? extends Property<?>> getProperties(BlockType blockType) {
        TreeMap<String, Property> properties = new TreeMap<String, Property>();
        Block block = PaperweightAdapter.getBlockFromType(blockType);
        BlockStateList blockStateList = block.l();
        for (IBlockState state : blockStateList.d()) {
            Property property = (Property)PROPERTY_CACHE.getUnchecked((Object)state);
            properties.put(property.getName(), property);
        }
        return properties;
    }

    @Override
    public void sendFakeNBT(Player player, BlockVector3 pos, LinCompoundTag nbtData) {
        TileEntityStructure structureBlock = new TileEntityStructure(new BlockPosition(pos.x(), pos.y(), pos.z()), Blocks.pv.m());
        structureBlock.a(((CraftPlayer)player).getHandle().dW());
        ((CraftPlayer)player).getHandle().f.b((Packet)PacketPlayOutTileEntityData.a((TileEntity)structureBlock, (blockEntity, registryAccess) -> (NBTTagCompound)this.fromNative(nbtData)));
    }

    @Override
    public void sendFakeOP(Player player) {
        ((CraftPlayer)player).getHandle().f.b((Packet)new PacketPlayOutEntityStatus((net.minecraft.world.entity.Entity)((CraftPlayer)player).getHandle(), 28));
    }

    @Override
    public org.bukkit.inventory.ItemStack adapt(BaseItemStack baseItemStack) {
        IRegistryCustom.Dimension registryAccess = DedicatedServer.getServer().ba();
        ItemStack stack = new ItemStack((Holder)registryAccess.e(Registries.K).b(ResourceKey.a((ResourceKey)Registries.K, (MinecraftKey)MinecraftKey.c((String)baseItemStack.getType().id()))), baseItemStack.getAmount());
        LinCompoundTag nbt = baseItemStack.getNbt();
        if (nbt != null) {
            DataComponentPatch componentPatch = (DataComponentPatch)COMPONENTS_CODEC.parse((DynamicOps)registryAccess.a((DynamicOps)DynamicOpsNBT.a), (Object)this.fromNative(nbt)).getOrThrow();
            stack.b(componentPatch);
        }
        return CraftItemStack.asCraftMirror((ItemStack)stack);
    }

    @Override
    public BaseItemStack adapt(org.bukkit.inventory.ItemStack itemStack) {
        IRegistryCustom.Dimension registryAccess = DedicatedServer.getServer().ba();
        ItemStack nmsStack = CraftItemStack.asNMSCopy((org.bukkit.inventory.ItemStack)itemStack);
        NBTTagCompound tag = (NBTTagCompound)COMPONENTS_CODEC.encodeStart((DynamicOps)registryAccess.a((DynamicOps)DynamicOpsNBT.a), (Object)nmsStack.e()).getOrThrow();
        return new BaseItemStack(BukkitAdapter.asItemType(itemStack.getType()), LazyReference.from(() -> (LinCompoundTag)this.toNative((NBTBase)tag)), itemStack.getAmount());
    }

    @Override
    public boolean simulateItemUse(World world, BlockVector3 position, BaseItem item, Direction face) {
        PaperweightFakePlayer fakePlayer;
        CraftWorld craftWorld = (CraftWorld)world;
        WorldServer worldServer = craftWorld.getHandle();
        ItemStack stack = CraftItemStack.asNMSCopy((org.bukkit.inventory.ItemStack)this.adapt(item instanceof BaseItemStack ? (BaseItemStack)item : new BaseItemStack(item.getType(), item.getNbtReference(), 1)));
        try {
            fakePlayer = (PaperweightFakePlayer)((Object)this.fakePlayers.get((Object)worldServer));
        }
        catch (ExecutionException ignored) {
            return false;
        }
        fakePlayer.a(EnumHand.a, stack);
        fakePlayer.a(position.x(), position.y(), position.z(), (float)face.toVector().toYaw(), (float)face.toVector().toPitch());
        BlockPosition blockPos = new BlockPosition(position.x(), position.y(), position.z());
        Vec3D blockVec = Vec3D.a((BaseBlockPosition)blockPos);
        EnumDirection enumFacing = PaperweightAdapter.adapt(face);
        MovingObjectPositionBlock rayTrace = new MovingObjectPositionBlock(blockVec, enumFacing, blockPos, false);
        ItemActionContext context = new ItemActionContext((EntityHuman)fakePlayer, EnumHand.a, rayTrace);
        Object result = stack.a(context);
        if (result != EnumInteractionResult.a) {
            result = worldServer.a_(blockPos).a(stack, (net.minecraft.world.level.World)worldServer, (EntityHuman)fakePlayer, EnumHand.a, rayTrace).a() ? EnumInteractionResult.a : stack.h().a((net.minecraft.world.level.World)worldServer, (EntityHuman)fakePlayer, EnumHand.a);
        }
        return result == EnumInteractionResult.a;
    }

    @Override
    public boolean canPlaceAt(World world, BlockVector3 position, BlockState blockState) {
        int internalId = BlockStateIdAccess.getBlockStateId(blockState);
        IBlockData blockData = Block.a((int)internalId);
        return blockData.a((IWorldReader)((CraftWorld)world).getHandle(), new BlockPosition(position.x(), position.y(), position.z()));
    }

    @Override
    public boolean regenerate(World bukkitWorld, Region region, Extent extent, RegenOptions options) {
        try {
            this.doRegen(bukkitWorld, region, extent, options);
        }
        catch (Exception e) {
            throw new IllegalStateException("Regen failed.", e);
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void doRegen(World bukkitWorld, Region region, Extent extent, RegenOptions options) throws Exception {
        World.Environment env = bukkitWorld.getEnvironment();
        ChunkGenerator gen = bukkitWorld.getGenerator();
        Path tempDir = Files.createTempDirectory("WorldEditWorldGen", new FileAttribute[0]);
        Convertable levelStorage = Convertable.b((Path)tempDir);
        ResourceKey<WorldDimension> worldDimKey = this.getWorldDimKey(env);
        try (Convertable.ConversionSession session = levelStorage.createAccess("worldeditregentempworld", worldDimKey);){
            WorldServer originalWorld = ((CraftWorld)bukkitWorld).getHandle();
            WorldDataServer levelProperties = (WorldDataServer)originalWorld.p().aZ().I();
            WorldOptions originalOpts = levelProperties.y();
            long seed = options.getSeed().orElse(originalWorld.D());
            WorldOptions newOpts = options.getSeed().isPresent() ? originalOpts.a(OptionalLong.of(seed)) : originalOpts;
            WorldSettings newWorldSettings = new WorldSettings("worldeditregentempworld", levelProperties.g.b(), levelProperties.g.c(), levelProperties.g.d(), levelProperties.g.e(), levelProperties.g.f(), levelProperties.g.g());
            WorldDataServer.a specialWorldProperty = levelProperties.z() ? WorldDataServer.a.b : (levelProperties.A() ? WorldDataServer.a.c : WorldDataServer.a.a);
            WorldDataServer newWorldData = new WorldDataServer(newWorldSettings, newOpts, specialWorldProperty, Lifecycle.stable());
            WorldServer freshWorld = new WorldServer(originalWorld.p(), originalWorld.p().ay, session, newWorldData, originalWorld.ah(), new WorldDimension(originalWorld.ag(), originalWorld.m().g()), (WorldLoadListener)new NoOpWorldLoadListener(), originalWorld.ai(), seed, (List)ImmutableList.of(), false, originalWorld.M(), env, gen, bukkitWorld.getBiomeProvider());
            try {
                this.regenForWorld(region, extent, freshWorld, options);
            }
            finally {
                freshWorld.m().close(false);
            }
        }
        finally {
            try {
                Map map = (Map)this.serverWorldsField.get(Bukkit.getServer());
                map.remove("worldeditregentempworld");
            }
            catch (IllegalAccessException illegalAccessException) {}
            SafeFiles.tryHardToDeleteDir(tempDir);
        }
    }

    private BiomeType adapt(WorldServer serverWorld, BiomeBase origBiome) {
        MinecraftKey key = serverWorld.K_().e(Registries.aI).b((Object)origBiome);
        if (key == null) {
            return null;
        }
        return BiomeTypes.get(key.toString());
    }

    private void regenForWorld(Region region, Extent extent, WorldServer serverWorld, RegenOptions options) throws WorldEditException {
        IAsyncTaskHandler executor;
        List<CompletableFuture<IChunkAccess>> chunkLoadings = this.submitChunkLoadTasks(region, serverWorld);
        try {
            executor = (IAsyncTaskHandler)this.chunkProviderExecutorField.get(serverWorld.m());
        }
        catch (IllegalAccessException e) {
            throw new IllegalStateException("Couldn't get executor for chunk loading.", e);
        }
        executor.b(() -> {
            if (chunkLoadings.stream().anyMatch(ftr -> ftr.isDone() && Futures.getUnchecked((Future)ftr) == null)) {
                return false;
            }
            return chunkLoadings.stream().allMatch(CompletableFuture::isDone);
        });
        HashMap<ChunkCoordIntPair, IChunkAccess> chunks = new HashMap<ChunkCoordIntPair, IChunkAccess>();
        for (CompletableFuture<IChunkAccess> future : chunkLoadings) {
            IChunkAccess chunk = future.getNow(null);
            Preconditions.checkState((chunk != null ? 1 : 0) != 0, (Object)"Failed to generate a chunk, regen failed.");
            chunks.put(chunk.f(), chunk);
        }
        for (BlockVector3 vec : region) {
            BiomeBase origBiome;
            BiomeType adaptedBiome;
            BlockPosition pos = new BlockPosition(vec.x(), vec.y(), vec.z());
            IChunkAccess chunk = (IChunkAccess)chunks.get(new ChunkCoordIntPair(pos));
            IBlockData blockData = chunk.a_(pos);
            int internalId = Block.j((IBlockData)blockData);
            BlockStateHolder<BlockState> state = BlockStateIdAccess.getBlockStateById(internalId);
            Objects.requireNonNull(state);
            TileEntity blockEntity = chunk.c_(pos);
            if (blockEntity != null) {
                NBTTagCompound tag = blockEntity.c((HolderLookup.a)serverWorld.K_());
                state = state.toBaseBlock(LazyReference.from(() -> (LinCompoundTag)this.toNative((NBTBase)tag)));
            }
            extent.setBlock(vec, state.toBaseBlock());
            if (!options.shouldRegenBiomes() || (adaptedBiome = this.adapt(serverWorld, origBiome = (BiomeBase)chunk.getNoiseBiome(vec.x(), vec.y(), vec.z()).a())) == null) continue;
            extent.setBiome(vec, adaptedBiome);
        }
    }

    private List<CompletableFuture<IChunkAccess>> submitChunkLoadTasks(Region region, WorldServer serverWorld) {
        ChunkProviderServer chunkManager = serverWorld.m();
        ArrayList<CompletableFuture<IChunkAccess>> chunkLoadings = new ArrayList<CompletableFuture<IChunkAccess>>();
        for (BlockVector2 chunk : region.getChunks()) {
            try {
                chunkLoadings.add((CompletableFuture<IChunkAccess>)((CompletableFuture)this.getChunkFutureMethod.invoke(chunkManager, chunk.x(), chunk.z(), ChunkStatus.j, true)).thenApply(either -> (IChunkAccess)either.b(null)));
            }
            catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException("Couldn't load chunk for regen.", e);
            }
        }
        return chunkLoadings;
    }

    private ResourceKey<WorldDimension> getWorldDimKey(World.Environment env) {
        return switch (env) {
            case World.Environment.NETHER -> WorldDimension.c;
            case World.Environment.THE_END -> WorldDimension.d;
            default -> WorldDimension.b;
        };
    }

    @Override
    public Set<SideEffect> getSupportedSideEffects() {
        return SUPPORTED_SIDE_EFFECTS;
    }

    @Override
    public boolean clearContainerBlockContents(World world, BlockVector3 pt) {
        WorldServer originalWorld = ((CraftWorld)world).getHandle();
        TileEntity entity = originalWorld.c_(new BlockPosition(pt.x(), pt.y(), pt.z()));
        if (entity instanceof Clearable) {
            ((Clearable)entity).a();
            return true;
        }
        return false;
    }

    @Override
    public void initializeRegistries() {
        DedicatedServer server = ((CraftServer)Bukkit.getServer()).getServer();
        for (MinecraftKey name : server.ba().e(Registries.aI).i()) {
            if (BiomeType.REGISTRY.get(name.toString()) != null) continue;
            BiomeType.REGISTRY.register(name.toString(), new BiomeType(name.toString()));
        }
        for (MinecraftKey name : server.ba().e(Registries.aL).i()) {
            if (ConfiguredFeatureType.REGISTRY.get(name.toString()) != null) continue;
            ConfiguredFeatureType.REGISTRY.register(name.toString(), new ConfiguredFeatureType(name.toString()));
        }
        for (MinecraftKey name : server.ba().e(Registries.aU).i()) {
            if (StructureType.REGISTRY.get(name.toString()) != null) continue;
            StructureType.REGISTRY.register(name.toString(), new StructureType(name.toString()));
        }
        IRegistry biomeRegistry = server.ba().e(Registries.aI);
        biomeRegistry.l().forEach(tag -> {
            String key = tag.h().b().toString();
            if (BiomeCategory.REGISTRY.get(key) == null) {
                BiomeCategory.REGISTRY.register(key, new BiomeCategory(key, () -> biomeRegistry.a(tag.h()).stream().flatMap(HolderSet.b::a).map(Holder::a).map(this::adapt).collect(Collectors.toSet())));
            }
        });
    }

    @Override
    public boolean generateFeature(ConfiguredFeatureType type, World world, EditSession session, BlockVector3 pt) {
        WorldServer originalWorld = ((CraftWorld)world).getHandle();
        WorldGenFeatureConfigured k = (WorldGenFeatureConfigured)originalWorld.K_().e(Registries.aL).a(MinecraftKey.c((String)type.id()));
        ChunkProviderServer chunkManager = originalWorld.m();
        GeneratorAccessSeed proxyLevel = PaperweightServerLevelDelegateProxy.newInstance(session, originalWorld, this);
        return k != null && k.a(proxyLevel, chunkManager.g(), random, new BlockPosition(pt.x(), pt.y(), pt.z()));
    }

    @Override
    public boolean generateStructure(StructureType type, World world, EditSession session, BlockVector3 pt) {
        WorldServer originalWorld = ((CraftWorld)world).getHandle();
        Structure k = (Structure)originalWorld.K_().e(Registries.aU).a(MinecraftKey.c((String)type.id()));
        if (k == null) {
            return false;
        }
        ChunkProviderServer chunkManager = originalWorld.m();
        GeneratorAccessSeed proxyLevel = PaperweightServerLevelDelegateProxy.newInstance(session, originalWorld, this);
        ChunkCoordIntPair chunkPos = new ChunkCoordIntPair(new BlockPosition(pt.x(), pt.y(), pt.z()));
        StructureStart structureStart = k.a(originalWorld.K_(), chunkManager.g(), chunkManager.g().d(), chunkManager.i(), originalWorld.r(), originalWorld.D(), chunkPos, 0, (LevelHeightAccessor)proxyLevel, biome -> true);
        if (!structureStart.b()) {
            return false;
        }
        StructureBoundingBox boundingBox = structureStart.a();
        ChunkCoordIntPair min = new ChunkCoordIntPair(SectionPosition.a((int)boundingBox.h()), SectionPosition.a((int)boundingBox.j()));
        ChunkCoordIntPair max = new ChunkCoordIntPair(SectionPosition.a((int)boundingBox.k()), SectionPosition.a((int)boundingBox.m()));
        ChunkCoordIntPair.a((ChunkCoordIntPair)min, (ChunkCoordIntPair)max).forEach(chunkPosx -> structureStart.a(proxyLevel, originalWorld.b(), chunkManager.g(), originalWorld.H_(), new StructureBoundingBox(chunkPosx.d(), originalWorld.L_(), chunkPosx.e(), chunkPosx.f(), originalWorld.am(), chunkPosx.g()), chunkPosx));
        return true;
    }

    @Override
    public void sendBiomeUpdates(World world, Iterable<BlockVector2> chunks) {
        ArrayList arrayList;
        WorldServer originalWorld = ((CraftWorld)world).getHandle();
        if (chunks instanceof Collection) {
            Collection chunkCollection = (Collection)chunks;
            arrayList = Lists.newArrayListWithCapacity((int)chunkCollection.size());
        } else {
            arrayList = Lists.newArrayList();
        }
        ArrayList nativeChunks = arrayList;
        for (BlockVector2 chunk : chunks) {
            nativeChunks.add(originalWorld.a(chunk.x(), chunk.z(), ChunkStatus.f, false));
        }
        originalWorld.m().a.a((List)nativeChunks);
    }

    LinTag<?> toNative(NBTBase foreign) {
        if (foreign == null) {
            return null;
        }
        if (foreign instanceof NBTTagCompound) {
            NBTTagCompound compoundTag = (NBTTagCompound)foreign;
            LinCompoundTag.Builder builder = LinCompoundTag.builder();
            for (String entry : compoundTag.e()) {
                builder.put(entry, this.toNative(compoundTag.c(entry)));
            }
            return builder.build();
        }
        if (foreign instanceof NBTTagByte) {
            NBTTagByte byteTag = (NBTTagByte)foreign;
            return LinByteTag.of(byteTag.i());
        }
        if (foreign instanceof NBTTagByteArray) {
            NBTTagByteArray byteArrayTag = (NBTTagByteArray)foreign;
            return LinByteArrayTag.of(byteArrayTag.e());
        }
        if (foreign instanceof NBTTagDouble) {
            NBTTagDouble doubleTag = (NBTTagDouble)foreign;
            return LinDoubleTag.of(doubleTag.j());
        }
        if (foreign instanceof NBTTagFloat) {
            NBTTagFloat floatTag = (NBTTagFloat)foreign;
            return LinFloatTag.of(floatTag.k());
        }
        if (foreign instanceof NBTTagInt) {
            NBTTagInt intTag = (NBTTagInt)foreign;
            return LinIntTag.of(intTag.g());
        }
        if (foreign instanceof NBTTagIntArray) {
            NBTTagIntArray intArrayTag = (NBTTagIntArray)foreign;
            return LinIntArrayTag.of(intArrayTag.g());
        }
        if (foreign instanceof NBTTagLongArray) {
            NBTTagLongArray longArrayTag = (NBTTagLongArray)foreign;
            return LinLongArrayTag.of(longArrayTag.g());
        }
        if (foreign instanceof NBTTagList) {
            NBTTagList listTag = (NBTTagList)foreign;
            try {
                return this.toNativeList(listTag);
            }
            catch (Throwable e) {
                this.logger.log(Level.WARNING, "Failed to convert net.minecraft.nbt.ListTag", e);
                return LinListTag.empty(LinTagType.endTag());
            }
        }
        if (foreign instanceof NBTTagLong) {
            NBTTagLong longTag = (NBTTagLong)foreign;
            return LinLongTag.of(longTag.f());
        }
        if (foreign instanceof NBTTagShort) {
            NBTTagShort shortTag = (NBTTagShort)foreign;
            return LinShortTag.of(shortTag.h());
        }
        if (foreign instanceof NBTTagString) {
            NBTTagString stringTag = (NBTTagString)foreign;
            return LinStringTag.of(stringTag.u_());
        }
        if (foreign instanceof NBTTagEnd) {
            return LinEndTag.instance();
        }
        throw new IllegalArgumentException("Don't know how to make native " + foreign.getClass().getCanonicalName());
    }

    private LinListTag<?> toNativeList(NBTTagList foreign) throws SecurityException, IllegalArgumentException {
        LinListTag.Builder<LinTag<?>> builder = LinListTag.builder(LinTagType.fromId(LinTagId.fromId(foreign.f())));
        for (NBTBase tag : foreign) {
            builder.add(this.toNative(tag));
        }
        return builder.build();
    }

    NBTBase fromNative(LinTag<?> foreign) {
        if (foreign == null) {
            return null;
        }
        if (foreign instanceof LinCompoundTag) {
            LinCompoundTag compoundTag = (LinCompoundTag)foreign;
            NBTTagCompound tag = new NBTTagCompound();
            for (Map.Entry entry : compoundTag.value().entrySet()) {
                tag.a((String)entry.getKey(), this.fromNative((LinTag)entry.getValue()));
            }
            return tag;
        }
        if (foreign instanceof LinByteTag) {
            LinByteTag byteTag = (LinByteTag)foreign;
            return NBTTagByte.a((byte)byteTag.valueAsByte());
        }
        if (foreign instanceof LinByteArrayTag) {
            LinByteArrayTag byteArrayTag = (LinByteArrayTag)foreign;
            return new NBTTagByteArray(byteArrayTag.value());
        }
        if (foreign instanceof LinDoubleTag) {
            LinDoubleTag doubleTag = (LinDoubleTag)foreign;
            return NBTTagDouble.a((double)doubleTag.valueAsDouble());
        }
        if (foreign instanceof LinFloatTag) {
            LinFloatTag floatTag = (LinFloatTag)foreign;
            return NBTTagFloat.a((float)floatTag.valueAsFloat());
        }
        if (foreign instanceof LinIntTag) {
            LinIntTag intTag = (LinIntTag)foreign;
            return NBTTagInt.a((int)intTag.valueAsInt());
        }
        if (foreign instanceof LinIntArrayTag) {
            LinIntArrayTag intArrayTag = (LinIntArrayTag)foreign;
            return new NBTTagIntArray(intArrayTag.value());
        }
        if (foreign instanceof LinLongArrayTag) {
            LinLongArrayTag longArrayTag = (LinLongArrayTag)foreign;
            return new NBTTagLongArray(longArrayTag.value());
        }
        if (foreign instanceof LinListTag) {
            LinListTag listTag = (LinListTag)foreign;
            NBTTagList tag = new NBTTagList();
            Iterator iterator = listTag.value().iterator();
            while (iterator.hasNext()) {
                LinTag t = (LinTag)iterator.next();
                tag.add((Object)this.fromNative(t));
            }
            return tag;
        }
        if (foreign instanceof LinLongTag) {
            LinLongTag longTag = (LinLongTag)foreign;
            return NBTTagLong.a((long)longTag.valueAsLong());
        }
        if (foreign instanceof LinShortTag) {
            LinShortTag shortTag = (LinShortTag)foreign;
            return NBTTagShort.a((short)shortTag.valueAsShort());
        }
        if (foreign instanceof LinStringTag) {
            LinStringTag stringTag = (LinStringTag)foreign;
            return NBTTagString.a((String)stringTag.value());
        }
        if (foreign instanceof LinEndTag) {
            return NBTTagEnd.b;
        }
        throw new IllegalArgumentException("Don't know how to make NMS " + foreign.getClass().getCanonicalName());
    }

    @Override
    public boolean supportsWatchdog() {
        return this.watchdog != null;
    }

    @Override
    public void tickWatchdog() {
        this.watchdog.tick();
    }

    private class SpigotWatchdog
    implements Watchdog {
        private final Field instanceField;
        private final Field lastTickField;

        SpigotWatchdog() throws NoSuchFieldException {
            Field instanceField = WatchdogThread.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            this.instanceField = instanceField;
            Field lastTickField = WatchdogThread.class.getDeclaredField("lastTick");
            lastTickField.setAccessible(true);
            this.lastTickField = lastTickField;
        }

        @Override
        public void tick() {
            try {
                WatchdogThread instance = (WatchdogThread)this.instanceField.get(null);
                if ((Long)this.lastTickField.get(instance) != 0L) {
                    WatchdogThread.tick();
                }
            }
            catch (IllegalAccessException e) {
                PaperweightAdapter.this.logger.log(Level.WARNING, "Failed to tick watchdog", e);
            }
        }
    }

    private static class MojangWatchdog
    implements Watchdog {
        private final DedicatedServer server;
        private final Field tickField;

        MojangWatchdog(DedicatedServer server) throws NoSuchFieldException {
            this.server = server;
            Field tickField = MinecraftServer.class.getDeclaredField(StaticRefraction.NEXT_TICK_TIME);
            if (tickField.getType() != Long.TYPE) {
                throw new IllegalStateException("nextTickTime is not a long field, mapping is likely incorrect");
            }
            tickField.setAccessible(true);
            this.tickField = tickField;
        }

        @Override
        public void tick() {
            try {
                this.tickField.set(this.server, SystemUtils.c());
            }
            catch (IllegalAccessException illegalAccessException) {
                // empty catch block
            }
        }
    }

    private static class NoOpWorldLoadListener
    implements WorldLoadListener {
        private NoOpWorldLoadListener() {
        }

        public void a(ChunkCoordIntPair spawnPos) {
        }

        public void a(ChunkCoordIntPair pos, @Nullable ChunkStatus status) {
        }

        public void a() {
        }

        public void b() {
        }
    }
}

