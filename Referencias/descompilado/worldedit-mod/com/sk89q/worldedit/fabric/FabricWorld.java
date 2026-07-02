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
 *  net.minecraft.class_1268
 *  net.minecraft.class_1269
 *  net.minecraft.class_1297
 *  net.minecraft.class_1299
 *  net.minecraft.class_1542
 *  net.minecraft.class_156
 *  net.minecraft.class_1657
 *  net.minecraft.class_1799
 *  net.minecraft.class_1838
 *  net.minecraft.class_1923
 *  net.minecraft.class_1937
 *  net.minecraft.class_1959
 *  net.minecraft.class_2338
 *  net.minecraft.class_2378
 *  net.minecraft.class_238
 *  net.minecraft.class_2404
 *  net.minecraft.class_2487
 *  net.minecraft.class_2499
 *  net.minecraft.class_2586
 *  net.minecraft.class_2680
 *  net.minecraft.class_27
 *  net.minecraft.class_2791
 *  net.minecraft.class_2802
 *  net.minecraft.class_2806
 *  net.minecraft.class_2818
 *  net.minecraft.class_2818$class_2819
 *  net.minecraft.class_2841
 *  net.minecraft.class_2960
 *  net.minecraft.class_2975
 *  net.minecraft.class_31
 *  net.minecraft.class_3195
 *  net.minecraft.class_32
 *  net.minecraft.class_32$class_5143
 *  net.minecraft.class_3215
 *  net.minecraft.class_3218
 *  net.minecraft.class_3341
 *  net.minecraft.class_3449
 *  net.minecraft.class_3829
 *  net.minecraft.class_3949
 *  net.minecraft.class_3965
 *  net.minecraft.class_4076
 *  net.minecraft.class_4538
 *  net.minecraft.class_5217
 *  net.minecraft.class_5268
 *  net.minecraft.class_5281
 *  net.minecraft.class_5285
 *  net.minecraft.class_5321
 *  net.minecraft.class_5363
 *  net.minecraft.class_5539
 *  net.minecraft.class_5819
 *  net.minecraft.class_6802
 *  net.minecraft.class_6808
 *  net.minecraft.class_7225$class_7874
 *  net.minecraft.class_7924
 *  net.minecraft.server.MinecraftServer
 */
package com.sk89q.worldedit.fabric;

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
import com.sk89q.worldedit.fabric.FabricAdapter;
import com.sk89q.worldedit.fabric.FabricWorldEdit;
import com.sk89q.worldedit.fabric.WorldEditFakePlayer;
import com.sk89q.worldedit.fabric.WorldEditGenListener;
import com.sk89q.worldedit.fabric.internal.ExtendedMinecraftServer;
import com.sk89q.worldedit.fabric.internal.FabricEntity;
import com.sk89q.worldedit.fabric.internal.FabricServerLevelDelegateProxy;
import com.sk89q.worldedit.fabric.internal.FabricWorldNativeAccess;
import com.sk89q.worldedit.fabric.internal.NBTConverter;
import com.sk89q.worldedit.function.mask.AbstractExtentMask;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.internal.Constants;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
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
import java.util.Collections;
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
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1542;
import net.minecraft.class_156;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1838;
import net.minecraft.class_1923;
import net.minecraft.class_1937;
import net.minecraft.class_1959;
import net.minecraft.class_2338;
import net.minecraft.class_2378;
import net.minecraft.class_238;
import net.minecraft.class_2404;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2586;
import net.minecraft.class_2680;
import net.minecraft.class_27;
import net.minecraft.class_2791;
import net.minecraft.class_2802;
import net.minecraft.class_2806;
import net.minecraft.class_2818;
import net.minecraft.class_2841;
import net.minecraft.class_2960;
import net.minecraft.class_2975;
import net.minecraft.class_31;
import net.minecraft.class_3195;
import net.minecraft.class_32;
import net.minecraft.class_3215;
import net.minecraft.class_3218;
import net.minecraft.class_3341;
import net.minecraft.class_3449;
import net.minecraft.class_3829;
import net.minecraft.class_3949;
import net.minecraft.class_3965;
import net.minecraft.class_4076;
import net.minecraft.class_4538;
import net.minecraft.class_5217;
import net.minecraft.class_5268;
import net.minecraft.class_5281;
import net.minecraft.class_5285;
import net.minecraft.class_5321;
import net.minecraft.class_5363;
import net.minecraft.class_5539;
import net.minecraft.class_5819;
import net.minecraft.class_6802;
import net.minecraft.class_6808;
import net.minecraft.class_7225;
import net.minecraft.class_7924;
import net.minecraft.server.MinecraftServer;
import org.enginehub.linbus.common.LinTagId;
import org.enginehub.linbus.tree.LinCompoundTag;

public class FabricWorld
extends AbstractWorld {
    private static final class_5819 random = class_5819.method_43047();
    private final WeakReference<class_1937> worldRef;
    private final FabricWorldNativeAccess worldNativeAccess;
    private static final LoadingCache<class_3218, WorldEditFakePlayer> fakePlayers = CacheBuilder.newBuilder().weakKeys().softValues().build(CacheLoader.from(WorldEditFakePlayer::new));

    private static class_2960 getDimensionRegistryKey(class_1937 world) {
        return Objects.requireNonNull(world.method_8503(), "server cannot be null").method_30611().method_30530(class_7924.field_41241).method_10221((Object)world.method_8597());
    }

    FabricWorld(class_1937 world) {
        Preconditions.checkNotNull((Object)world);
        this.worldRef = new WeakReference<class_1937>(world);
        this.worldNativeAccess = new FabricWorldNativeAccess(this.worldRef);
    }

    public class_1937 getWorld() {
        class_1937 world = (class_1937)this.worldRef.get();
        if (world != null) {
            return world;
        }
        throw new RuntimeException("The reference to the world was lost (i.e. the world may have been unloaded)");
    }

    @Override
    public String getName() {
        class_5217 levelProperties = this.getWorld().method_8401();
        return ((class_5268)levelProperties).method_150();
    }

    @Override
    public String id() {
        return this.getName() + "_" + String.valueOf(FabricWorld.getDimensionRegistryKey(this.getWorld()));
    }

    @Override
    public Path getStoragePath() {
        class_1937 world = this.getWorld();
        MinecraftServer server = world.method_8503();
        Preconditions.checkState((boolean)(server instanceof ExtendedMinecraftServer), (Object)"Need a server world");
        return ((ExtendedMinecraftServer)server).getStoragePath(world);
    }

    @Override
    public <B extends BlockStateHolder<B>> boolean setBlock(BlockVector3 position, B block, SideEffectSet sideEffects) throws WorldEditException {
        this.clearContainerBlockContents(position);
        return this.worldNativeAccess.setBlock(position, block, sideEffects);
    }

    @Override
    public Set<SideEffect> applySideEffects(BlockVector3 position, BlockState previousType, SideEffectSet sideEffectSet) {
        this.worldNativeAccess.applySideEffects(position, previousType, sideEffectSet);
        return Sets.intersection(FabricWorldEdit.inst.getPlatform().getSupportedSideEffects(), sideEffectSet.getSideEffectsToApply());
    }

    @Override
    public int getBlockLightLevel(BlockVector3 position) {
        Preconditions.checkNotNull((Object)((Object)position));
        return this.getWorld().method_22339(FabricAdapter.toBlockPos(position));
    }

    @Override
    public boolean clearContainerBlockContents(BlockVector3 position) {
        Preconditions.checkNotNull((Object)((Object)position));
        class_2586 tile = this.getWorld().method_8321(FabricAdapter.toBlockPos(position));
        if (tile instanceof class_3829) {
            ((class_3829)tile).method_5448();
            return true;
        }
        return false;
    }

    @Override
    public BiomeType getBiome(BlockVector3 position) {
        Preconditions.checkNotNull((Object)((Object)position));
        class_2818 chunk = this.getWorld().method_8497(position.x() >> 4, position.z() >> 4);
        return this.getBiomeInChunk(position, (class_2791)chunk);
    }

    private BiomeType getBiomeInChunk(BlockVector3 position, class_2791 chunk) {
        return FabricAdapter.adapt((class_1959)chunk.method_16359(position.x() >> 2, position.y() >> 2, position.z() >> 2).comp_349());
    }

    @Override
    public boolean setBiome(BlockVector3 position, BiomeType biome) {
        Preconditions.checkNotNull((Object)((Object)position));
        Preconditions.checkNotNull((Object)biome);
        class_2818 chunk = this.getWorld().method_8497(position.x() >> 4, position.z() >> 4);
        class_2841 biomeArray = (class_2841)chunk.method_38259(chunk.method_31602(position.y())).method_38294();
        biomeArray.method_16678(position.x() & 3, position.y() & 3, position.z() & 3, (Object)((class_2378)this.getWorld().method_30349().method_33310(class_7924.field_41236).orElseThrow()).method_40290(class_5321.method_29179((class_5321)class_7924.field_41236, (class_2960)class_2960.method_60654((String)biome.id()))));
        chunk.method_12008(true);
        return true;
    }

    @Override
    public boolean useItem(BlockVector3 position, BaseItem item, Direction face) {
        WorldEditFakePlayer fakePlayer;
        class_1799 stack = FabricAdapter.adapt(new BaseItemStack(item.getType(), item.getNbtReference(), 1));
        class_3218 world = (class_3218)this.getWorld();
        try {
            fakePlayer = (WorldEditFakePlayer)((Object)fakePlayers.get((Object)world));
        }
        catch (ExecutionException ignored) {
            return false;
        }
        fakePlayer.method_6122(class_1268.field_5808, stack);
        fakePlayer.method_5641(position.x(), position.y(), position.z(), (float)face.toVector().toYaw(), (float)face.toVector().toPitch());
        class_2338 blockPos = FabricAdapter.toBlockPos(position);
        class_3965 rayTraceResult = new class_3965(FabricAdapter.toVec3(position), FabricAdapter.adapt(face), blockPos, false);
        class_1838 itemUseContext = new class_1838((class_1657)fakePlayer, class_1268.field_5808, rayTraceResult);
        class_1269 used = stack.method_7981(itemUseContext);
        if (used != class_1269.field_5812) {
            used = this.getWorld().method_8320(blockPos).method_55780(stack, (class_1937)world, (class_1657)fakePlayer, class_1268.field_5808, rayTraceResult).method_55645();
        }
        if (used != class_1269.field_5812) {
            used = stack.method_7913((class_1937)world, (class_1657)fakePlayer, class_1268.field_5808).method_5467();
        }
        return used == class_1269.field_5812;
    }

    @Override
    public void dropItem(Vector3 position, BaseItemStack item) {
        Preconditions.checkNotNull((Object)((Object)position));
        Preconditions.checkNotNull((Object)item);
        if (item.getType() == ItemTypes.AIR) {
            return;
        }
        class_1542 entity = new class_1542(this.getWorld(), position.x(), position.y(), position.z(), FabricAdapter.adapt(item));
        entity.method_6982(10);
        this.getWorld().method_8649((class_1297)entity);
    }

    @Override
    public void simulateBlockMine(BlockVector3 position) {
        class_2338 pos = FabricAdapter.toBlockPos(position);
        this.getWorld().method_22352(pos, true);
    }

    @Override
    public boolean canPlaceAt(BlockVector3 position, BlockState blockState) {
        return FabricAdapter.adapt(blockState).method_26184((class_4538)this.getWorld(), FabricAdapter.toBlockPos(position));
    }

    @Override
    public boolean regenerate(Region region, Extent extent, RegenOptions options) {
        class_2802 provider = this.getWorld().method_8398();
        if (!(provider instanceof class_3215)) {
            return false;
        }
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
        class_32 levelStorage = class_32.method_26999((Path)tempDir);
        try (class_32.class_5143 session = levelStorage.method_27002("WorldEditTempGen");){
            class_3218 originalWorld = (class_3218)this.getWorld();
            class_31 levelProperties = FabricWorld.getPrimaryLevelData(originalWorld.method_8401());
            class_5285 originalOpts = levelProperties.method_28057();
            long seed = options.getSeed().orElse(originalWorld.method_8412());
            levelProperties.field_25425 = options.getSeed().isPresent() ? originalOpts.method_28024(OptionalLong.of(seed)) : originalOpts;
            class_5321 worldRegKey = originalWorld.method_27983();
            try (class_3218 serverWorld = new class_3218(originalWorld.method_8503(), (Executor)class_156.method_18349(), session, (class_5268)originalWorld.method_8401(), worldRegKey, new class_5363(originalWorld.method_40134(), originalWorld.method_14178().method_12129()), (class_3949)new WorldEditGenListener(), originalWorld.method_27982(), seed, (List)ImmutableList.of(), false, originalWorld.method_52168());){
                this.regenForWorld(region, extent, serverWorld, options);
                while (originalWorld.method_8503().method_16075()) {
                    Thread.yield();
                }
            }
            finally {
                levelProperties.field_25425 = originalOpts;
            }
        }
        finally {
            SafeFiles.tryHardToDeleteDir(tempDir);
        }
    }

    private static class_31 getPrimaryLevelData(class_5217 levelData) {
        if (levelData instanceof class_27) {
            class_27 derivedLevelData = (class_27)levelData;
            return FabricWorld.getPrimaryLevelData((class_5217)derivedLevelData.field_139);
        }
        if (levelData instanceof class_31) {
            class_31 primaryLevelData = (class_31)levelData;
            return primaryLevelData;
        }
        throw new IllegalStateException("Unknown level data type: " + String.valueOf(levelData.getClass()));
    }

    private void regenForWorld(Region region, Extent extent, class_3218 serverWorld, RegenOptions options) throws WorldEditException {
        List<CompletableFuture<class_2791>> chunkLoadings = this.submitChunkLoadTasks(region, serverWorld);
        serverWorld.method_14178().field_18809.method_18857(() -> {
            if (chunkLoadings.stream().anyMatch(ftr -> ftr.isDone() && Futures.getUnchecked((Future)ftr) == null)) {
                return false;
            }
            return chunkLoadings.stream().allMatch(CompletableFuture::isDone);
        });
        HashMap<class_1923, class_2791> chunks = new HashMap<class_1923, class_2791>();
        for (CompletableFuture<class_2791> future : chunkLoadings) {
            class_2791 chunk = future.getNow(null);
            Preconditions.checkState((chunk != null ? 1 : 0) != 0, (Object)"Failed to generate a chunk, regen failed.");
            chunks.put(chunk.method_12004(), chunk);
        }
        for (BlockVector3 vec : region) {
            class_2338 pos = FabricAdapter.toBlockPos(vec);
            class_2791 chunk = (class_2791)chunks.get(new class_1923(pos));
            BlockStateHolder<BlockState> state = FabricAdapter.adapt(chunk.method_8320(pos));
            class_2586 blockEntity = chunk.method_8321(pos);
            if (blockEntity != null) {
                class_2487 tag = blockEntity.method_38243((class_7225.class_7874)serverWorld.method_30349());
                state = state.toBaseBlock(LazyReference.from(() -> NBTConverter.fromNative(tag)));
            }
            extent.setBlock(vec, state.toBaseBlock());
            if (!options.shouldRegenBiomes()) continue;
            BiomeType biome = this.getBiomeInChunk(vec, chunk);
            extent.setBiome(vec, biome);
        }
    }

    private List<CompletableFuture<class_2791>> submitChunkLoadTasks(Region region, class_3218 world) {
        ArrayList<CompletableFuture<class_2791>> chunkLoadings = new ArrayList<CompletableFuture<class_2791>>();
        for (BlockVector2 chunk : region.getChunks()) {
            chunkLoadings.add((CompletableFuture<class_2791>)world.method_14178().method_17299(chunk.x(), chunk.z(), class_2806.field_12795, true).thenApply(either -> (class_2791)either.method_57130(null)));
        }
        return chunkLoadings;
    }

    @Nullable
    private static class_5321<class_2975<?, ?>> createTreeFeatureGenerator(TreeGenerator.TreeType type) {
        return switch (type) {
            case TreeGenerator.TreeType.TREE -> class_6808.field_35905;
            case TreeGenerator.TreeType.BIG_TREE -> class_6808.field_35912;
            case TreeGenerator.TreeType.REDWOOD -> class_6808.field_35909;
            case TreeGenerator.TreeType.TALL_REDWOOD -> class_6808.field_35915;
            case TreeGenerator.TreeType.MEGA_REDWOOD -> class_6808.field_35916;
            case TreeGenerator.TreeType.BIRCH -> class_6808.field_35907;
            case TreeGenerator.TreeType.JUNGLE -> class_6808.field_35914;
            case TreeGenerator.TreeType.SMALL_JUNGLE -> class_6808.field_35911;
            case TreeGenerator.TreeType.SHORT_JUNGLE -> class_6808.field_35913;
            case TreeGenerator.TreeType.JUNGLE_BUSH -> class_6808.field_35920;
            case TreeGenerator.TreeType.SWAMP -> class_6808.field_35919;
            case TreeGenerator.TreeType.ACACIA -> class_6808.field_35908;
            case TreeGenerator.TreeType.DARK_OAK -> class_6808.field_35906;
            case TreeGenerator.TreeType.TALL_BIRCH -> class_6808.field_35917;
            case TreeGenerator.TreeType.RED_MUSHROOM -> class_6808.field_35904;
            case TreeGenerator.TreeType.BROWN_MUSHROOM -> class_6808.field_35903;
            case TreeGenerator.TreeType.WARPED_FUNGUS -> class_6808.field_35901;
            case TreeGenerator.TreeType.CRIMSON_FUNGUS -> class_6808.field_35899;
            case TreeGenerator.TreeType.CHORUS_PLANT -> class_6802.field_35798;
            case TreeGenerator.TreeType.MANGROVE -> class_6808.field_38810;
            case TreeGenerator.TreeType.TALL_MANGROVE -> class_6808.field_38811;
            case TreeGenerator.TreeType.CHERRY -> class_6808.field_42960;
            case TreeGenerator.TreeType.RANDOM -> FabricWorld.createTreeFeatureGenerator(TreeGenerator.TreeType.values()[ThreadLocalRandom.current().nextInt(TreeGenerator.TreeType.values().length)]);
            default -> null;
        };
    }

    @Override
    public boolean generateTree(TreeGenerator.TreeType type, EditSession editSession, BlockVector3 position) {
        class_3218 world = (class_3218)this.getWorld();
        class_2975 generator = Optional.ofNullable(FabricWorld.createTreeFeatureGenerator(type)).map(k -> (class_2975)world.method_30349().method_30530(class_7924.field_41239).method_29107(k)).orElse(null);
        class_3215 chunkManager = world.method_14178();
        if (type == TreeGenerator.TreeType.CHORUS_PLANT) {
            position = position.add(0, 1, 0);
        }
        class_5281 proxyLevel = FabricServerLevelDelegateProxy.newInstance(editSession, world);
        return generator != null && generator.method_12862(proxyLevel, chunkManager.method_12129(), random, FabricAdapter.toBlockPos(position));
    }

    @Override
    public boolean generateFeature(ConfiguredFeatureType type, EditSession editSession, BlockVector3 position) {
        class_3218 world = (class_3218)this.getWorld();
        class_2975 k = (class_2975)world.method_30349().method_30530(class_7924.field_41239).method_10223(class_2960.method_12829((String)type.id()));
        class_3215 chunkManager = world.method_14178();
        class_5281 proxyLevel = FabricServerLevelDelegateProxy.newInstance(editSession, world);
        return k != null && k.method_12862(proxyLevel, chunkManager.method_12129(), random, FabricAdapter.toBlockPos(position));
    }

    @Override
    public boolean generateStructure(StructureType type, EditSession editSession, BlockVector3 position) {
        class_3218 world = (class_3218)this.getWorld();
        class_3195 k = (class_3195)world.method_30349().method_30530(class_7924.field_41246).method_10223(class_2960.method_12829((String)type.id()));
        if (k == null) {
            return false;
        }
        class_3215 chunkManager = world.method_14178();
        class_5281 proxyLevel = FabricServerLevelDelegateProxy.newInstance(editSession, world);
        class_1923 chunkPos = new class_1923(new class_2338(position.x(), position.y(), position.z()));
        class_3449 structureStart = k.method_41614(world.method_30349(), chunkManager.method_12129(), chunkManager.method_12129().method_12098(), chunkManager.method_41248(), world.method_14183(), world.method_8412(), chunkPos, 0, (class_5539)proxyLevel, biome -> true);
        if (!structureStart.method_16657()) {
            return false;
        }
        class_3341 boundingBox = structureStart.method_14969();
        class_1923 min = new class_1923(class_4076.method_18675((int)boundingBox.method_35415()), class_4076.method_18675((int)boundingBox.method_35417()));
        class_1923 max = new class_1923(class_4076.method_18675((int)boundingBox.method_35418()), class_4076.method_18675((int)boundingBox.method_35420()));
        class_1923.method_19281((class_1923)min, (class_1923)max).forEach(chunkPosx -> structureStart.method_14974(proxyLevel, world.method_27056(), chunkManager.method_12129(), world.method_8409(), new class_3341(chunkPosx.method_8326(), world.method_31607(), chunkPosx.method_8328(), chunkPosx.method_8327(), world.method_31600(), chunkPosx.method_8329()), chunkPosx));
        return true;
    }

    @Override
    public void checkLoadedChunk(BlockVector3 pt) {
        this.getWorld().method_22350(FabricAdapter.toBlockPos(pt));
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
            nativeChunks.add(this.getWorld().method_8402(chunk.x(), chunk.z(), class_2806.field_12794, false));
        }
        ((class_3218)this.getWorld()).method_14178().field_17254.method_49421((List)nativeChunks);
    }

    @Override
    public void fixLighting(Iterable<BlockVector2> chunks) {
        class_1937 world = this.getWorld();
        for (BlockVector2 chunk : chunks) {
            world.method_8398().method_12130().method_15512(new class_1923(chunk.x(), chunk.z()), true);
        }
    }

    @Override
    public boolean playEffect(Vector3 position, int type, int data) {
        return true;
    }

    @Override
    public WeatherType getWeather() {
        class_5217 info = this.getWorld().method_8401();
        if (info.method_203()) {
            return WeatherTypes.THUNDER_STORM;
        }
        if (info.method_156()) {
            return WeatherTypes.RAIN;
        }
        return WeatherTypes.CLEAR;
    }

    @Override
    public long getRemainingWeatherDuration() {
        class_5268 info = (class_5268)this.getWorld().method_8401();
        if (info.method_203()) {
            return info.method_145();
        }
        if (info.method_156()) {
            return info.method_190();
        }
        return info.method_155();
    }

    @Override
    public void setWeather(WeatherType weatherType) {
        this.setWeather(weatherType, 0L);
    }

    @Override
    public void setWeather(WeatherType weatherType, long duration) {
        class_5268 info = (class_5268)this.getWorld().method_8401();
        if (weatherType == WeatherTypes.THUNDER_STORM) {
            info.method_167(0);
            info.method_147(true);
            info.method_173((int)duration);
        } else if (weatherType == WeatherTypes.RAIN) {
            info.method_167(0);
            info.method_157(true);
            info.method_164((int)duration);
        } else if (weatherType == WeatherTypes.CLEAR) {
            info.method_157(false);
            info.method_147(false);
            info.method_167((int)duration);
        }
    }

    @Override
    public int getMinY() {
        return this.getWorld().method_31607();
    }

    @Override
    public int getMaxY() {
        return this.getWorld().method_31600() - 1;
    }

    @Override
    public BlockVector3 getSpawnPosition() {
        return FabricAdapter.adapt(this.getWorld().method_8401().method_56126());
    }

    @Override
    public BlockState getBlock(BlockVector3 position) {
        class_2680 mcState = this.getWorld().method_8497(position.x() >> 4, position.z() >> 4).method_8320(FabricAdapter.toBlockPos(position));
        return FabricAdapter.adapt(mcState);
    }

    @Override
    public BaseBlock getFullBlock(BlockVector3 position) {
        class_2338 pos = new class_2338(position.x(), position.y(), position.z());
        class_2586 tile = ((class_2818)this.getWorld().method_22350(pos)).method_12201(pos, class_2818.class_2819.field_12859);
        if (tile != null) {
            class_2487 tag = tile.method_38243((class_7225.class_7874)this.getWorld().method_30349());
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
        if (o instanceof FabricWorld) {
            FabricWorld other = (FabricWorld)o;
            class_1937 otherWorld = (class_1937)other.worldRef.get();
            class_1937 thisWorld = (class_1937)this.worldRef.get();
            return otherWorld != null && otherWorld.equals(thisWorld);
        }
        if (o instanceof World) {
            return ((World)o).getName().equals(this.getName());
        }
        return false;
    }

    @Override
    public List<? extends Entity> getEntities(Region region) {
        class_1937 world = this.getWorld();
        class_238 box = new class_238(FabricAdapter.toVec3(region.getMinimumPoint()), FabricAdapter.toVec3(region.getMaximumPoint().add(BlockVector3.ONE)));
        List nmsEntities = world.method_8333((class_1297)null, box, e -> region.contains(FabricAdapter.adapt(e.method_24515())));
        return (List)nmsEntities.stream().map(FabricEntity::new).collect(ImmutableList.toImmutableList());
    }

    @Override
    public List<? extends Entity> getEntities() {
        class_1937 world = this.getWorld();
        if (!(world instanceof class_3218)) {
            return Collections.emptyList();
        }
        return (List)Streams.stream((Iterable)((class_3218)world).method_27909()).map(FabricEntity::new).collect(ImmutableList.toImmutableList());
    }

    @Override
    @Nullable
    public Entity createEntity(Location location, BaseEntity entity) {
        class_2487 tag;
        class_3218 world = (class_3218)this.getWorld();
        String entityId = entity.getType().id();
        Optional entityType = class_1299.method_5898((String)entityId);
        if (entityType.isEmpty()) {
            return null;
        }
        LinCompoundTag linTag = entity.getNbt();
        if (linTag != null) {
            tag = NBTConverter.toNative(linTag);
            this.removeUnwantedEntityTagsRecursively(tag);
        } else {
            tag = new class_2487();
        }
        tag.method_10582("id", entityId);
        class_1297 createdEntity = class_1299.method_17842((class_2487)tag, (class_1937)world, loadedEntity -> {
            loadedEntity.method_5641(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            return loadedEntity;
        });
        if (createdEntity != null) {
            world.method_30771(createdEntity);
            return new FabricEntity(createdEntity);
        }
        return null;
    }

    private void removeUnwantedEntityTagsRecursively(class_2487 tag) {
        for (String name : Constants.NO_COPY_ENTITY_NBT_FIELDS) {
            tag.method_10551(name);
        }
        if (tag.method_10573("Passengers", LinTagId.LIST.id())) {
            class_2499 nbttaglist = tag.method_10554("Passengers", LinTagId.COMPOUND.id());
            for (int i = 0; i < nbttaglist.size(); ++i) {
                this.removeUnwantedEntityTagsRecursively(nbttaglist.method_10602(i));
            }
        }
    }

    @Override
    public Mask createLiquidMask() {
        return new AbstractExtentMask(this, this){

            @Override
            public boolean test(BlockVector3 vector) {
                return FabricAdapter.adapt(this.getExtent().getBlock(vector)).method_26204() instanceof class_2404;
            }
        };
    }
}

