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
 *  com.google.common.collect.Streams
 *  com.google.common.util.concurrent.Futures
 *  javax.annotation.Nullable
 *  net.minecraft.Util
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Registry
 *  net.minecraft.core.SectionPos
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.data.worldgen.features.EndFeatures
 *  net.minecraft.data.worldgen.features.TreeFeatures
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerChunkCache
 *  net.minecraft.server.level.ServerChunkCache$MainThreadExecutor
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.progress.ChunkProgressListener
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.Clearable
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelHeightAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.WorldGenLevel
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.block.LiquidBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.ChunkAccess
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.chunk.PalettedContainer
 *  net.minecraft.world.level.chunk.status.ChunkStatus
 *  net.minecraft.world.level.dimension.LevelStem
 *  net.minecraft.world.level.levelgen.WorldOptions
 *  net.minecraft.world.level.levelgen.feature.ConfiguredFeature
 *  net.minecraft.world.level.levelgen.structure.BoundingBox
 *  net.minecraft.world.level.levelgen.structure.Structure
 *  net.minecraft.world.level.levelgen.structure.StructureStart
 *  net.minecraft.world.level.storage.LevelData
 *  net.minecraft.world.level.storage.LevelStorageSource
 *  net.minecraft.world.level.storage.LevelStorageSource$LevelStorageAccess
 *  net.minecraft.world.level.storage.PrimaryLevelData
 *  net.minecraft.world.level.storage.ServerLevelData
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 */
package com.sk89q.worldedit.neoforge;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Streams;
import com.google.common.util.concurrent.Futures;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseItem;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.mask.AbstractExtentMask;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.internal.Constants;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.neoforge.NeoForgeAdapter;
import com.sk89q.worldedit.neoforge.NeoForgeWorldEdit;
import com.sk89q.worldedit.neoforge.WorldEditFakePlayer;
import com.sk89q.worldedit.neoforge.WorldEditGenListener;
import com.sk89q.worldedit.neoforge.internal.NBTConverter;
import com.sk89q.worldedit.neoforge.internal.NeoForgeEntity;
import com.sk89q.worldedit.neoforge.internal.NeoForgeServerLevelDelegateProxy;
import com.sk89q.worldedit.neoforge.internal.NeoForgeWorldNativeAccess;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.SideEffect;
import com.sk89q.worldedit.util.SideEffectSet;
import com.sk89q.worldedit.util.TreeGenerator;
import com.sk89q.worldedit.util.concurrency.LazyReference;
import com.sk89q.worldedit.util.io.file.SafeFiles;
import com.sk89q.worldedit.world.AbstractWorld;
import com.sk89q.worldedit.world.RegenOptions;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.generation.ConfiguredFeatureType;
import com.sk89q.worldedit.world.generation.StructureType;
import com.sk89q.worldedit.world.item.ItemTypes;
import com.sk89q.worldedit.world.weather.WeatherType;
import com.sk89q.worldedit.world.weather.WeatherTypes;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.EndFeatures;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.enginehub.linbus.common.LinTagId;
import org.enginehub.linbus.tree.LinCompoundTag;

public class NeoForgeWorld
extends AbstractWorld {
    private static final RandomSource random = RandomSource.create();
    private final WeakReference<ServerLevel> worldRef;
    private final NeoForgeWorldNativeAccess nativeAccess;
    private static final LoadingCache<ServerLevel, WorldEditFakePlayer> fakePlayers = CacheBuilder.newBuilder().weakKeys().softValues().build(CacheLoader.from(WorldEditFakePlayer::new));

    private static ResourceLocation getDimensionRegistryKey(ServerLevel world) {
        return Objects.requireNonNull(world.getServer(), "server cannot be null").registryAccess().registryOrThrow(Registries.DIMENSION_TYPE).getKey((Object)world.dimensionType());
    }

    NeoForgeWorld(ServerLevel world) {
        Preconditions.checkNotNull((Object)world);
        this.worldRef = new WeakReference<ServerLevel>(world);
        this.nativeAccess = new NeoForgeWorldNativeAccess(this.worldRef);
    }

    public ServerLevel getWorld() {
        ServerLevel world = (ServerLevel)this.worldRef.get();
        if (world != null) {
            return world;
        }
        throw new RuntimeException("The reference to the world was lost (i.e. the world may have been unloaded)");
    }

    @Override
    public String getName() {
        return ((ServerLevelData)this.getWorld().getLevelData()).getLevelName();
    }

    @Override
    public String id() {
        return this.getName() + "_" + String.valueOf(NeoForgeWorld.getDimensionRegistryKey(this.getWorld()));
    }

    @Override
    public Path getStoragePath() {
        ServerLevel world = this.getWorld();
        return world.getServer().storageSource.getDimensionPath(world.dimension());
    }

    @Override
    public <B extends BlockStateHolder<B>> boolean setBlock(BlockVector3 position, B block, SideEffectSet sideEffects) throws WorldEditException {
        this.clearContainerBlockContents(position);
        return this.nativeAccess.setBlock(position, block, sideEffects);
    }

    @Override
    public Set<SideEffect> applySideEffects(BlockVector3 position, BlockState previousType, SideEffectSet sideEffectSet) {
        this.nativeAccess.applySideEffects(position, previousType, sideEffectSet);
        return Sets.intersection(NeoForgeWorldEdit.inst.getPlatform().getSupportedSideEffects(), sideEffectSet.getSideEffectsToApply());
    }

    @Override
    public int getBlockLightLevel(BlockVector3 position) {
        Preconditions.checkNotNull((Object)((Object)position));
        return this.getWorld().getLightEmission(NeoForgeAdapter.toBlockPos(position));
    }

    @Override
    public boolean clearContainerBlockContents(BlockVector3 position) {
        Preconditions.checkNotNull((Object)((Object)position));
        BlockEntity tile = this.getWorld().getBlockEntity(NeoForgeAdapter.toBlockPos(position));
        if (tile instanceof Clearable) {
            ((Clearable)tile).clearContent();
            return true;
        }
        return false;
    }

    @Override
    public BiomeType getBiome(BlockVector3 position) {
        Preconditions.checkNotNull((Object)((Object)position));
        LevelChunk chunk = this.getWorld().getChunk(position.x() >> 4, position.z() >> 4);
        return this.getBiomeInChunk(position, (ChunkAccess)chunk);
    }

    private BiomeType getBiomeInChunk(BlockVector3 position, ChunkAccess chunk) {
        return NeoForgeAdapter.adapt((Biome)chunk.getNoiseBiome(position.x() >> 2, position.y() >> 2, position.z() >> 2).value());
    }

    @Override
    public boolean setBiome(BlockVector3 position, BiomeType biome) {
        Preconditions.checkNotNull((Object)((Object)position));
        Preconditions.checkNotNull((Object)biome);
        LevelChunk chunk = this.getWorld().getChunk(position.x() >> 4, position.z() >> 4);
        PalettedContainer biomes = (PalettedContainer)chunk.getSection(chunk.getSectionIndex(position.y())).getBiomes();
        biomes.getAndSetUnchecked(position.x() & 3, position.y() & 3, position.z() & 3, (Object)((Registry)this.getWorld().registryAccess().registry(Registries.BIOME).orElseThrow()).getHolderOrThrow(ResourceKey.create((ResourceKey)Registries.BIOME, (ResourceLocation)ResourceLocation.parse((String)biome.id()))));
        chunk.setUnsaved(true);
        return true;
    }

    @Override
    public boolean useItem(BlockVector3 position, BaseItem item, Direction face) {
        WorldEditFakePlayer fakePlayer;
        ItemStack stack = NeoForgeAdapter.adapt(new BaseItemStack(item.getType(), item.getNbtReference(), 1));
        ServerLevel world = this.getWorld();
        try {
            fakePlayer = (WorldEditFakePlayer)((Object)fakePlayers.get((Object)world));
        }
        catch (ExecutionException ignored) {
            return false;
        }
        fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, stack);
        fakePlayer.absMoveTo(position.x(), position.y(), position.z(), (float)face.toVector().toYaw(), (float)face.toVector().toPitch());
        BlockPos blockPos = NeoForgeAdapter.toBlockPos(position);
        BlockHitResult rayTraceResult = new BlockHitResult(NeoForgeAdapter.toVec3(position), NeoForgeAdapter.adapt(face), blockPos, false);
        UseOnContext itemUseContext = new UseOnContext((Player)fakePlayer, InteractionHand.MAIN_HAND, rayTraceResult);
        InteractionResult used = stack.onItemUseFirst(itemUseContext);
        if (used != InteractionResult.SUCCESS) {
            InteractionResult resultType = this.getWorld().getBlockState(blockPos).useItemOn(stack, (Level)world, (Player)fakePlayer, InteractionHand.MAIN_HAND, rayTraceResult).result();
            used = resultType.consumesAction() ? resultType : stack.getItem().use((Level)world, (Player)fakePlayer, InteractionHand.MAIN_HAND).getResult();
        }
        return used == InteractionResult.SUCCESS;
    }

    @Override
    public void dropItem(Vector3 position, BaseItemStack item) {
        Preconditions.checkNotNull((Object)((Object)position));
        Preconditions.checkNotNull((Object)item);
        if (item.getType() == ItemTypes.AIR) {
            return;
        }
        ItemEntity entity = new ItemEntity((Level)this.getWorld(), position.x(), position.y(), position.z(), NeoForgeAdapter.adapt(item));
        entity.setPickUpDelay(10);
        this.getWorld().addFreshEntity((net.minecraft.world.entity.Entity)entity);
    }

    @Override
    public void simulateBlockMine(BlockVector3 position) {
        BlockPos pos = NeoForgeAdapter.toBlockPos(position);
        this.getWorld().destroyBlock(pos, true);
    }

    @Override
    public boolean canPlaceAt(BlockVector3 position, BlockState blockState) {
        return NeoForgeAdapter.adapt(blockState).canSurvive((LevelReader)this.getWorld(), NeoForgeAdapter.toBlockPos(position));
    }

    @Override
    public boolean regenerate(Region region, Extent extent, RegenOptions options) {
        try {
            this.doRegen(region, extent, options);
        }
        catch (Exception e) {
            throw new IllegalStateException("Regen failed", e);
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void doRegen(Region region, Extent extent, RegenOptions options) throws Exception {
        Path tempDir = Files.createTempDirectory("WorldEditWorldGen", new FileAttribute[0]);
        LevelStorageSource levelStorage = LevelStorageSource.createDefault((Path)tempDir);
        try (LevelStorageSource.LevelStorageAccess session = levelStorage.createAccess("WorldEditTempGen");){
            ServerLevel originalWorld = this.getWorld();
            PrimaryLevelData levelProperties = (PrimaryLevelData)originalWorld.getServer().getWorldData().overworldData();
            WorldOptions originalOpts = levelProperties.worldGenOptions();
            long seed = options.getSeed().orElse(originalWorld.getSeed());
            levelProperties.worldOptions = options.getSeed().isPresent() ? originalOpts.withSeed(OptionalLong.of(seed)) : originalOpts;
            ResourceKey worldRegKey = originalWorld.dimension();
            try (ServerLevel serverWorld = new ServerLevel(originalWorld.getServer(), (Executor)Util.backgroundExecutor(), session, (ServerLevelData)originalWorld.getLevelData(), worldRegKey, new LevelStem(originalWorld.dimensionTypeRegistration(), originalWorld.getChunkSource().getGenerator()), (ChunkProgressListener)new WorldEditGenListener(), originalWorld.isDebug(), seed, (List)ImmutableList.of(), false, originalWorld.getRandomSequences());){
                this.regenForWorld(region, extent, serverWorld, options);
                while (originalWorld.getServer().pollTask()) {
                    Thread.yield();
                }
            }
            finally {
                levelProperties.worldOptions = originalOpts;
            }
        }
        finally {
            SafeFiles.tryHardToDeleteDir(tempDir);
        }
    }

    private void regenForWorld(Region region, Extent extent, ServerLevel serverWorld, RegenOptions options) throws WorldEditException {
        List<CompletableFuture<ChunkAccess>> chunkLoadings = this.submitChunkLoadTasks(region, serverWorld);
        ServerChunkCache.MainThreadExecutor executor = serverWorld.getChunkSource().mainThreadProcessor;
        executor.managedBlock(() -> {
            if (chunkLoadings.stream().anyMatch(ftr -> ftr.isDone() && Futures.getUnchecked((Future)ftr) == null)) {
                return false;
            }
            return chunkLoadings.stream().allMatch(CompletableFuture::isDone);
        });
        HashMap<ChunkPos, ChunkAccess> chunks = new HashMap<ChunkPos, ChunkAccess>();
        for (CompletableFuture<ChunkAccess> future : chunkLoadings) {
            ChunkAccess chunk = future.getNow(null);
            Preconditions.checkState((chunk != null ? 1 : 0) != 0, (Object)"Failed to generate a chunk, regen failed.");
            chunks.put(chunk.getPos(), chunk);
        }
        for (BlockVector3 vec : region) {
            BlockPos pos = NeoForgeAdapter.toBlockPos(vec);
            ChunkAccess chunk = (ChunkAccess)chunks.get(new ChunkPos(pos));
            BlockStateHolder<BlockState> state = NeoForgeAdapter.adapt(chunk.getBlockState(pos));
            BlockEntity blockEntity = chunk.getBlockEntity(pos);
            if (blockEntity != null) {
                CompoundTag tag = blockEntity.saveWithId((HolderLookup.Provider)serverWorld.registryAccess());
                state = state.toBaseBlock(LazyReference.from(() -> NBTConverter.fromNative(tag)));
            }
            extent.setBlock(vec, state.toBaseBlock());
            if (!options.shouldRegenBiomes()) continue;
            BiomeType biome = this.getBiomeInChunk(vec, chunk);
            extent.setBiome(vec, biome);
        }
    }

    private List<CompletableFuture<ChunkAccess>> submitChunkLoadTasks(Region region, ServerLevel world) {
        ArrayList<CompletableFuture<ChunkAccess>> chunkLoadings = new ArrayList<CompletableFuture<ChunkAccess>>();
        for (BlockVector2 chunk : region.getChunks()) {
            chunkLoadings.add((CompletableFuture<ChunkAccess>)world.getChunkSource().getChunkFuture(chunk.x(), chunk.z(), ChunkStatus.FEATURES, true).thenApply(either -> (ChunkAccess)either.orElse(null)));
        }
        return chunkLoadings;
    }

    @Nullable
    private static ResourceKey<ConfiguredFeature<?, ?>> createTreeFeatureGenerator(TreeGenerator.TreeType type) {
        return switch (type) {
            case TreeGenerator.TreeType.TREE -> TreeFeatures.OAK;
            case TreeGenerator.TreeType.BIG_TREE -> TreeFeatures.FANCY_OAK;
            case TreeGenerator.TreeType.REDWOOD -> TreeFeatures.SPRUCE;
            case TreeGenerator.TreeType.TALL_REDWOOD -> TreeFeatures.MEGA_SPRUCE;
            case TreeGenerator.TreeType.MEGA_REDWOOD -> TreeFeatures.MEGA_PINE;
            case TreeGenerator.TreeType.BIRCH -> TreeFeatures.BIRCH;
            case TreeGenerator.TreeType.JUNGLE -> TreeFeatures.MEGA_JUNGLE_TREE;
            case TreeGenerator.TreeType.SMALL_JUNGLE -> TreeFeatures.JUNGLE_TREE;
            case TreeGenerator.TreeType.SHORT_JUNGLE -> TreeFeatures.JUNGLE_TREE_NO_VINE;
            case TreeGenerator.TreeType.JUNGLE_BUSH -> TreeFeatures.JUNGLE_BUSH;
            case TreeGenerator.TreeType.SWAMP -> TreeFeatures.SWAMP_OAK;
            case TreeGenerator.TreeType.ACACIA -> TreeFeatures.ACACIA;
            case TreeGenerator.TreeType.DARK_OAK -> TreeFeatures.DARK_OAK;
            case TreeGenerator.TreeType.TALL_BIRCH -> TreeFeatures.SUPER_BIRCH_BEES_0002;
            case TreeGenerator.TreeType.RED_MUSHROOM -> TreeFeatures.HUGE_RED_MUSHROOM;
            case TreeGenerator.TreeType.BROWN_MUSHROOM -> TreeFeatures.HUGE_BROWN_MUSHROOM;
            case TreeGenerator.TreeType.WARPED_FUNGUS -> TreeFeatures.WARPED_FUNGUS;
            case TreeGenerator.TreeType.CRIMSON_FUNGUS -> TreeFeatures.CRIMSON_FUNGUS;
            case TreeGenerator.TreeType.CHORUS_PLANT -> EndFeatures.CHORUS_PLANT;
            case TreeGenerator.TreeType.MANGROVE -> TreeFeatures.MANGROVE;
            case TreeGenerator.TreeType.TALL_MANGROVE -> TreeFeatures.TALL_MANGROVE;
            case TreeGenerator.TreeType.CHERRY -> TreeFeatures.CHERRY;
            case TreeGenerator.TreeType.RANDOM -> NeoForgeWorld.createTreeFeatureGenerator(TreeGenerator.TreeType.values()[ThreadLocalRandom.current().nextInt(TreeGenerator.TreeType.values().length)]);
            default -> null;
        };
    }

    @Override
    public boolean generateTree(TreeGenerator.TreeType type, EditSession editSession, BlockVector3 position) {
        ServerLevel world = this.getWorld();
        ConfiguredFeature generator = Optional.ofNullable(NeoForgeWorld.createTreeFeatureGenerator(type)).map(k -> (ConfiguredFeature)world.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE).get(k)).orElse(null);
        ServerChunkCache chunkManager = world.getChunkSource();
        if (type == TreeGenerator.TreeType.CHORUS_PLANT) {
            position = position.add(0, 1, 0);
        }
        WorldGenLevel levelProxy = NeoForgeServerLevelDelegateProxy.newInstance(editSession, world);
        return generator != null && generator.place(levelProxy, chunkManager.getGenerator(), random, NeoForgeAdapter.toBlockPos(position));
    }

    @Override
    public boolean generateFeature(ConfiguredFeatureType type, EditSession editSession, BlockVector3 position) {
        ServerLevel world = this.getWorld();
        ConfiguredFeature k = (ConfiguredFeature)world.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE).get(ResourceLocation.tryParse((String)type.id()));
        ServerChunkCache chunkManager = world.getChunkSource();
        WorldGenLevel levelProxy = NeoForgeServerLevelDelegateProxy.newInstance(editSession, world);
        return k != null && k.place(levelProxy, chunkManager.getGenerator(), random, NeoForgeAdapter.toBlockPos(position));
    }

    @Override
    public boolean generateStructure(StructureType type, EditSession editSession, BlockVector3 position) {
        ServerLevel world = this.getWorld();
        Structure k = (Structure)world.registryAccess().registryOrThrow(Registries.STRUCTURE).get(ResourceLocation.tryParse((String)type.id()));
        if (k == null) {
            return false;
        }
        ServerChunkCache chunkManager = world.getChunkSource();
        WorldGenLevel proxyLevel = NeoForgeServerLevelDelegateProxy.newInstance(editSession, world);
        ChunkPos chunkPos = new ChunkPos(new BlockPos(position.x(), position.y(), position.z()));
        StructureStart structureStart = k.generate(world.registryAccess(), chunkManager.getGenerator(), chunkManager.getGenerator().getBiomeSource(), chunkManager.randomState(), world.getStructureManager(), world.getSeed(), chunkPos, 0, (LevelHeightAccessor)proxyLevel, biome -> true);
        if (!structureStart.isValid()) {
            return false;
        }
        BoundingBox boundingBox = structureStart.getBoundingBox();
        ChunkPos min = new ChunkPos(SectionPos.blockToSectionCoord((int)boundingBox.minX()), SectionPos.blockToSectionCoord((int)boundingBox.minZ()));
        ChunkPos max = new ChunkPos(SectionPos.blockToSectionCoord((int)boundingBox.maxX()), SectionPos.blockToSectionCoord((int)boundingBox.maxZ()));
        ChunkPos.rangeClosed((ChunkPos)min, (ChunkPos)max).forEach(chunkPosx -> structureStart.placeInChunk(proxyLevel, world.structureManager(), chunkManager.getGenerator(), world.getRandom(), new BoundingBox(chunkPosx.getMinBlockX(), world.getMinBuildHeight(), chunkPosx.getMinBlockZ(), chunkPosx.getMaxBlockX(), world.getMaxBuildHeight(), chunkPosx.getMaxBlockZ()), chunkPosx));
        return true;
    }

    @Override
    public void checkLoadedChunk(BlockVector3 pt) {
        this.getWorld().getChunk(NeoForgeAdapter.toBlockPos(pt));
    }

    @Override
    public void fixAfterFastMode(Iterable<BlockVector2> chunks) {
        this.fixLighting(chunks);
    }

    @Override
    public void sendBiomeUpdates(Iterable<BlockVector2> chunks) {
        ArrayList arrayList;
        if (chunks instanceof Collection) {
            Collection chunkCollection = (Collection)chunks;
            arrayList = Lists.newArrayListWithCapacity((int)chunkCollection.size());
        } else {
            arrayList = Lists.newArrayList();
        }
        ArrayList nativeChunks = arrayList;
        for (BlockVector2 chunk : chunks) {
            nativeChunks.add(this.getWorld().getChunk(chunk.x(), chunk.z(), ChunkStatus.BIOMES, false));
        }
        this.getWorld().getChunkSource().chunkMap.resendBiomesForChunks((List)nativeChunks);
    }

    @Override
    public void fixLighting(Iterable<BlockVector2> chunks) {
        ServerLevel world = this.getWorld();
        for (BlockVector2 chunk : chunks) {
            world.getChunkSource().getLightEngine().lightChunk(world.getChunk(chunk.x(), chunk.z(), ChunkStatus.INITIALIZE_LIGHT), false);
        }
    }

    @Override
    public boolean playEffect(Vector3 position, int type, int data) {
        return true;
    }

    @Override
    public WeatherType getWeather() {
        LevelData info = this.getWorld().getLevelData();
        if (info.isThundering()) {
            return WeatherTypes.THUNDER_STORM;
        }
        if (info.isRaining()) {
            return WeatherTypes.RAIN;
        }
        return WeatherTypes.CLEAR;
    }

    @Override
    public long getRemainingWeatherDuration() {
        ServerLevelData info = (ServerLevelData)this.getWorld().getLevelData();
        if (info.isThundering()) {
            return info.getThunderTime();
        }
        if (info.isRaining()) {
            return info.getRainTime();
        }
        return info.getClearWeatherTime();
    }

    @Override
    public void setWeather(WeatherType weatherType) {
        this.setWeather(weatherType, 0L);
    }

    @Override
    public void setWeather(WeatherType weatherType, long duration) {
        ServerLevelData info = (ServerLevelData)this.getWorld().getLevelData();
        if (weatherType == WeatherTypes.THUNDER_STORM) {
            info.setClearWeatherTime(0);
            info.setThundering(true);
            info.setThunderTime((int)duration);
        } else if (weatherType == WeatherTypes.RAIN) {
            info.setClearWeatherTime(0);
            info.setRaining(true);
            info.setRainTime((int)duration);
        } else if (weatherType == WeatherTypes.CLEAR) {
            info.setRaining(false);
            info.setThundering(false);
            info.setClearWeatherTime((int)duration);
        }
    }

    @Override
    public int getMinY() {
        return this.getWorld().getMinBuildHeight();
    }

    @Override
    public int getMaxY() {
        return this.getWorld().getMaxBuildHeight() - 1;
    }

    @Override
    public BlockVector3 getSpawnPosition() {
        return NeoForgeAdapter.adapt(this.getWorld().getLevelData().getSpawnPos());
    }

    @Override
    public BlockState getBlock(BlockVector3 position) {
        net.minecraft.world.level.block.state.BlockState mcState = this.getWorld().getChunk(position.x() >> 4, position.z() >> 4).getBlockState(NeoForgeAdapter.toBlockPos(position));
        return NeoForgeAdapter.adapt(mcState);
    }

    @Override
    public BaseBlock getFullBlock(BlockVector3 position) {
        BlockPos pos = new BlockPos(position.x(), position.y(), position.z());
        BlockEntity tile = this.getWorld().getChunk(pos).getBlockEntity(pos);
        if (tile != null) {
            CompoundTag tag = tile.saveWithId((HolderLookup.Provider)this.getWorld().registryAccess());
            return this.getBlock(position).toBaseBlock(LazyReference.from(() -> NBTConverter.fromNative(tag)));
        }
        return this.getBlock(position).toBaseBlock();
    }

    @Override
    public int hashCode() {
        return this.getWorld().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof NeoForgeWorld) {
            NeoForgeWorld other = (NeoForgeWorld)o;
            Level otherWorld = (Level)other.worldRef.get();
            Level thisWorld = (Level)this.worldRef.get();
            return otherWorld != null && otherWorld.equals(thisWorld);
        }
        if (o instanceof World) {
            return ((World)o).getName().equals(this.getName());
        }
        return false;
    }

    @Override
    public List<? extends Entity> getEntities(Region region) {
        ServerLevel world = this.getWorld();
        AABB box = new AABB(NeoForgeAdapter.toVec3(region.getMinimumPoint()), NeoForgeAdapter.toVec3(region.getMaximumPoint().add(BlockVector3.ONE)));
        List nmsEntities = world.getEntities((net.minecraft.world.entity.Entity)null, box, e -> region.contains(NeoForgeAdapter.adapt(e.blockPosition())));
        return (List)nmsEntities.stream().map(NeoForgeEntity::new).collect(ImmutableList.toImmutableList());
    }

    @Override
    public List<? extends Entity> getEntities() {
        ServerLevel world = this.getWorld();
        return (List)Streams.stream((Iterable)world.getAllEntities()).map(NeoForgeEntity::new).collect(ImmutableList.toImmutableList());
    }

    @Override
    @Nullable
    public Entity createEntity(Location location, BaseEntity entity) {
        CompoundTag tag;
        ServerLevel world = this.getWorld();
        String entityId = entity.getType().id();
        Optional entityType = EntityType.byString((String)entityId);
        if (entityType.isEmpty()) {
            return null;
        }
        LinCompoundTag nativeTag = entity.getNbt();
        if (nativeTag != null) {
            tag = NBTConverter.toNative(nativeTag);
            this.removeUnwantedEntityTagsRecursively(tag);
        } else {
            tag = new CompoundTag();
        }
        tag.putString("id", entityId);
        net.minecraft.world.entity.Entity createdEntity = EntityType.loadEntityRecursive((CompoundTag)tag, (Level)world, loadedEntity -> {
            loadedEntity.absMoveTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            return loadedEntity;
        });
        if (createdEntity != null) {
            world.addFreshEntityWithPassengers(createdEntity);
            return new NeoForgeEntity(createdEntity);
        }
        return null;
    }

    private void removeUnwantedEntityTagsRecursively(CompoundTag tag) {
        for (String name : Constants.NO_COPY_ENTITY_NBT_FIELDS) {
            tag.remove(name);
        }
        if (tag.contains("Passengers", LinTagId.LIST.id())) {
            ListTag nbttaglist = tag.getList("Passengers", LinTagId.COMPOUND.id());
            for (int i = 0; i < nbttaglist.size(); ++i) {
                this.removeUnwantedEntityTagsRecursively(nbttaglist.getCompound(i));
            }
        }
    }

    @Override
    public Mask createLiquidMask() {
        return new AbstractExtentMask(this, this){

            @Override
            public boolean test(BlockVector3 vector) {
                return NeoForgeAdapter.adapt(this.getExtent().getBlock(vector)).getBlock() instanceof LiquidBlock;
            }
        };
    }
}

