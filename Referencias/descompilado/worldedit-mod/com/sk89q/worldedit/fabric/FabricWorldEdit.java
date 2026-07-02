/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.mojang.brigadier.CommandDispatcher
 *  net.fabricmc.api.ModInitializer
 *  net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
 *  net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
 *  net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
 *  net.fabricmc.fabric.api.event.player.AttackBlockCallback
 *  net.fabricmc.fabric.api.event.player.UseBlockCallback
 *  net.fabricmc.fabric.api.event.player.UseItemCallback
 *  net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
 *  net.fabricmc.loader.api.FabricLoader
 *  net.fabricmc.loader.api.ModContainer
 *  net.minecraft.class_1268
 *  net.minecraft.class_1269
 *  net.minecraft.class_1271
 *  net.minecraft.class_1657
 *  net.minecraft.class_1799
 *  net.minecraft.class_1937
 *  net.minecraft.class_2168
 *  net.minecraft.class_2170$class_5364
 *  net.minecraft.class_2338
 *  net.minecraft.class_2350
 *  net.minecraft.class_2378
 *  net.minecraft.class_2960
 *  net.minecraft.class_3222
 *  net.minecraft.class_3244
 *  net.minecraft.class_3965
 *  net.minecraft.class_5321
 *  net.minecraft.class_5455
 *  net.minecraft.class_6862
 *  net.minecraft.class_6880
 *  net.minecraft.class_6885$class_6887
 *  net.minecraft.class_7157
 *  net.minecraft.class_7924
 *  net.minecraft.server.MinecraftServer
 *  org.apache.logging.log4j.Logger
 */
package com.sk89q.worldedit.fabric;

import com.google.common.base.Preconditions;
import com.mojang.brigadier.CommandDispatcher;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.command.util.PermissionCondition;
import com.sk89q.worldedit.event.platform.PlatformReadyEvent;
import com.sk89q.worldedit.event.platform.PlatformUnreadyEvent;
import com.sk89q.worldedit.event.platform.PlatformsRegisteredEvent;
import com.sk89q.worldedit.event.platform.SessionIdleEvent;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.extension.platform.Platform;
import com.sk89q.worldedit.extension.platform.PlatformManager;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.fabric.CommandWrapper;
import com.sk89q.worldedit.fabric.FabricAdapter;
import com.sk89q.worldedit.fabric.FabricConfiguration;
import com.sk89q.worldedit.fabric.FabricPermissionsProvider;
import com.sk89q.worldedit.fabric.FabricPlatform;
import com.sk89q.worldedit.fabric.FabricPlayer;
import com.sk89q.worldedit.fabric.FabricWorld;
import com.sk89q.worldedit.fabric.ThreadSafeCache;
import com.sk89q.worldedit.fabric.net.handler.WECUIPacketHandler;
import com.sk89q.worldedit.internal.anvil.ChunkDeleter;
import com.sk89q.worldedit.internal.event.InteractionDebouncer;
import com.sk89q.worldedit.internal.util.LogManagerCompat;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.lifecycle.Lifecycled;
import com.sk89q.worldedit.util.lifecycle.SimpleLifecycled;
import com.sk89q.worldedit.world.biome.BiomeCategory;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.block.BlockCategory;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.entity.EntityType;
import com.sk89q.worldedit.world.generation.ConfiguredFeatureType;
import com.sk89q.worldedit.world.generation.StructureType;
import com.sk89q.worldedit.world.item.ItemCategory;
import com.sk89q.worldedit.world.item.ItemType;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1271;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_2168;
import net.minecraft.class_2170;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2378;
import net.minecraft.class_2960;
import net.minecraft.class_3222;
import net.minecraft.class_3244;
import net.minecraft.class_3965;
import net.minecraft.class_5321;
import net.minecraft.class_5455;
import net.minecraft.class_6862;
import net.minecraft.class_6880;
import net.minecraft.class_6885;
import net.minecraft.class_7157;
import net.minecraft.class_7924;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.Logger;
import org.enginehub.piston.Command;

public class FabricWorldEdit
implements ModInitializer {
    private static final Logger LOGGER = LogManagerCompat.getLogger();
    public static final String MOD_ID = "worldedit";
    public static final String CUI_PLUGIN_CHANNEL = "cui";
    public static final Lifecycled<MinecraftServer> LIFECYCLED_SERVER;
    private FabricPermissionsProvider provider;
    public static FabricWorldEdit inst;
    private InteractionDebouncer debouncer;
    private FabricPlatform platform;
    private FabricConfiguration config;
    private Path workingDir;
    private ModContainer container;

    public static class_5455 registryAccess() {
        return LIFECYCLED_SERVER.valueOrThrow().method_30611();
    }

    public static <T> class_2378<T> getRegistry(class_5321<class_2378<T>> key) {
        return LIFECYCLED_SERVER.valueOrThrow().method_30611().method_30530(key);
    }

    public FabricWorldEdit() {
        inst = this;
    }

    public void onInitialize() {
        this.container = (ModContainer)FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow(() -> new IllegalStateException("WorldEdit mod missing in Fabric"));
        this.workingDir = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);
        if (!Files.exists(this.workingDir, new LinkOption[0])) {
            try {
                Files.createDirectory(this.workingDir, new FileAttribute[0]);
            }
            catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        this.platform = new FabricPlatform(this);
        this.debouncer = new InteractionDebouncer(this.platform);
        WorldEdit.getInstance().getPlatformManager().register(this.platform);
        this.config = new FabricConfiguration(this);
        this.provider = this.getInitialPermissionsProvider();
        WECUIPacketHandler.init();
        ServerTickEvents.END_SERVER_TICK.register((Object)ThreadSafeCache.getInstance());
        CommandRegistrationCallback.EVENT.register(this::registerCommands);
        ServerLifecycleEvents.SERVER_STARTING.register(this::onStartingServer);
        ServerLifecycleEvents.SERVER_STARTED.register(this::onStartServer);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onStopServer);
        ServerPlayConnectionEvents.DISCONNECT.register(this::onPlayerDisconnect);
        AttackBlockCallback.EVENT.register(this::onLeftClickBlock);
        UseBlockCallback.EVENT.register(this::onRightClickBlock);
        UseItemCallback.EVENT.register(this::onRightClickAir);
        LOGGER.info("WorldEdit for Fabric (version " + this.getInternalVersion() + ") is loaded");
    }

    private void registerCommands(CommandDispatcher<class_2168> dispatcher, class_7157 registryAccess, class_2170.class_5364 environment) {
        WorldEdit.getInstance().getEventBus().post(new PlatformsRegisteredEvent());
        PlatformManager manager = WorldEdit.getInstance().getPlatformManager();
        Platform commandsPlatform = manager.queryCapability(Capability.USER_COMMANDS);
        if (commandsPlatform != this.platform || !this.platform.isHookingEvents()) {
            return;
        }
        List commands = manager.getPlatformCommandManager().getCommandManager().getAllCommands().toList();
        for (Command command : commands) {
            CommandWrapper.register(dispatcher, command);
            Set perms = command.getCondition().as(PermissionCondition.class).map(PermissionCondition::getPermissions).orElseGet(Collections::emptySet);
            if (perms.isEmpty()) continue;
            perms.forEach(this.getPermissionsProvider()::registerPermission);
        }
    }

    private FabricPermissionsProvider getInitialPermissionsProvider() {
        try {
            Class.forName("me.lucko.fabric.api.permissions.v0.Permissions", false, this.getClass().getClassLoader());
            return new FabricPermissionsProvider.LuckoFabricPermissionsProvider(this.platform);
        }
        catch (ClassNotFoundException classNotFoundException) {
            return new FabricPermissionsProvider.VanillaPermissionsProvider(this.platform);
        }
    }

    private void setupRegistries(MinecraftServer server) {
        String key;
        String key2;
        for (class_2960 name2 : server.method_30611().method_30530(class_7924.field_41254).method_10235()) {
            key2 = name2.toString();
            if (BlockType.REGISTRY.get(key2) != null) continue;
            BlockType.REGISTRY.register(key2, new BlockType(key2, input -> FabricAdapter.adapt(FabricAdapter.adapt(input.getBlockType()).method_9564())));
        }
        for (class_2960 name2 : server.method_30611().method_30530(class_7924.field_41197).method_10235()) {
            key2 = name2.toString();
            if (ItemType.REGISTRY.get(key2) != null) continue;
            ItemType.REGISTRY.register(key2, new ItemType(key2));
        }
        for (class_2960 name2 : server.method_30611().method_30530(class_7924.field_41266).method_10235()) {
            key2 = name2.toString();
            if (EntityType.REGISTRY.get(key2) != null) continue;
            EntityType.REGISTRY.register(key2, new EntityType(key2));
        }
        for (class_2960 name2 : server.method_30611().method_30530(class_7924.field_41236).method_10235()) {
            key2 = name2.toString();
            if (BiomeType.REGISTRY.get(key2) != null) continue;
            BiomeType.REGISTRY.register(key2, new BiomeType(key2));
        }
        server.method_30611().method_30530(class_7924.field_41254).method_40273().map(class_6862::comp_327).forEach(name -> {
            String key = name.toString();
            if (BlockCategory.REGISTRY.get(key) == null) {
                BlockCategory.REGISTRY.register(key, new BlockCategory(key));
            }
        });
        server.method_30611().method_30530(class_7924.field_41197).method_40273().map(class_6862::comp_327).forEach(name -> {
            String key = name.toString();
            if (ItemCategory.REGISTRY.get(key) == null) {
                ItemCategory.REGISTRY.register(key, new ItemCategory(key));
            }
        });
        class_2378 biomeRegistry = server.method_30611().method_30530(class_7924.field_41236);
        biomeRegistry.method_40273().forEach(tagKey -> {
            String key = tagKey.comp_327().toString();
            if (BiomeCategory.REGISTRY.get(key) == null) {
                BiomeCategory.REGISTRY.register(key, new BiomeCategory(key, () -> biomeRegistry.method_40266(tagKey).stream().flatMap(class_6885.class_6887::method_40239).map(class_6880::comp_349).map(FabricAdapter::adapt).collect(Collectors.toSet())));
            }
        });
        for (class_2960 name3 : server.method_30611().method_30530(class_7924.field_41239).method_10235()) {
            key = name3.toString();
            if (ConfiguredFeatureType.REGISTRY.get(key) != null) continue;
            ConfiguredFeatureType.REGISTRY.register(key, new ConfiguredFeatureType(key));
        }
        for (class_2960 name3 : server.method_30611().method_30530(class_7924.field_41246).method_10235()) {
            key = name3.toString();
            if (StructureType.REGISTRY.get(key) != null) continue;
            StructureType.REGISTRY.register(key, new StructureType(key));
        }
    }

    private void onStartingServer(MinecraftServer minecraftServer) {
        Path delChunks = this.workingDir.resolve("delete_chunks.json");
        if (Files.exists(delChunks, new LinkOption[0])) {
            ChunkDeleter.runFromFile(delChunks, true);
        }
    }

    private void onStartServer(MinecraftServer minecraftServer) {
        this.setupRegistries(minecraftServer);
        this.config.load();
        WorldEdit.getInstance().getEventBus().post(new PlatformReadyEvent(this.platform));
    }

    private void onStopServer(MinecraftServer minecraftServer) {
        WorldEdit worldEdit = WorldEdit.getInstance();
        worldEdit.getSessionManager().unload();
        WorldEdit.getInstance().getEventBus().post(new PlatformUnreadyEvent(this.platform));
    }

    private boolean skipEvents() {
        return this.platform == null || !this.platform.isHookingEvents();
    }

    private boolean skipInteractionEvent(class_1657 player, class_1268 hand) {
        return this.skipEvents() || hand != class_1268.field_5808 || player.method_37908().field_9236 || !(player instanceof class_3222);
    }

    private class_1269 onLeftClickBlock(class_1657 playerEntity, class_1937 world, class_1268 hand, class_2338 blockPos, class_2350 direction) {
        Direction weDirection;
        FabricWorld localWorld;
        Location pos;
        FabricPlayer player;
        if (this.skipInteractionEvent(playerEntity, hand)) {
            return class_1269.field_5811;
        }
        WorldEdit we = WorldEdit.getInstance();
        boolean result = we.handleBlockLeftClick(player = FabricAdapter.adaptPlayer((class_3222)playerEntity), pos = new Location((Extent)(localWorld = this.getWorld(world)), blockPos.method_10263(), (double)blockPos.method_10264(), (double)blockPos.method_10260()), weDirection = FabricAdapter.adaptEnumFacing(direction)) || we.handleArmSwing(player);
        this.debouncer.setLastInteraction(player, result);
        return result ? class_1269.field_5812 : class_1269.field_5811;
    }

    private class_1269 onRightClickBlock(class_1657 playerEntity, class_1937 world, class_1268 hand, class_3965 blockHitResult) {
        Direction direction;
        FabricWorld localWorld;
        Location pos;
        FabricPlayer player;
        if (this.skipInteractionEvent(playerEntity, hand)) {
            return class_1269.field_5811;
        }
        WorldEdit we = WorldEdit.getInstance();
        boolean result = we.handleBlockRightClick(player = FabricAdapter.adaptPlayer((class_3222)playerEntity), pos = new Location((Extent)(localWorld = this.getWorld(world)), blockHitResult.method_17777().method_10263(), (double)blockHitResult.method_17777().method_10264(), (double)blockHitResult.method_17777().method_10260()), direction = FabricAdapter.adaptEnumFacing(blockHitResult.method_17780())) || we.handleRightClick(player);
        this.debouncer.setLastInteraction(player, result);
        return result ? class_1269.field_5812 : class_1269.field_5811;
    }

    public void onLeftClickAir(class_3222 playerEntity, class_1268 hand) {
        if (this.skipInteractionEvent((class_1657)playerEntity, hand)) {
            return;
        }
        WorldEdit we = WorldEdit.getInstance();
        FabricPlayer player = FabricAdapter.adaptPlayer(playerEntity);
        Optional<Boolean> previousResult = this.debouncer.getDuplicateInteractionResult(player);
        if (previousResult.isPresent()) {
            return;
        }
        boolean result = we.handleArmSwing(player);
        this.debouncer.setLastInteraction(player, result);
    }

    private class_1271<class_1799> onRightClickAir(class_1657 playerEntity, class_1937 world, class_1268 hand) {
        class_1799 stackInHand = playerEntity.method_5998(hand);
        if (this.skipInteractionEvent(playerEntity, hand)) {
            return class_1271.method_22430((Object)stackInHand);
        }
        WorldEdit we = WorldEdit.getInstance();
        FabricPlayer player = FabricAdapter.adaptPlayer((class_3222)playerEntity);
        Optional<Boolean> previousResult = this.debouncer.getDuplicateInteractionResult(player);
        if (previousResult.isPresent()) {
            return previousResult.get() != false ? class_1271.method_22427((Object)stackInHand) : class_1271.method_22430((Object)stackInHand);
        }
        boolean result = we.handleRightClick(player);
        this.debouncer.setLastInteraction(player, result);
        return result ? class_1271.method_22427((Object)stackInHand) : class_1271.method_22430((Object)stackInHand);
    }

    private void onPlayerDisconnect(class_3244 handler, MinecraftServer server) {
        this.debouncer.clearInteraction(FabricAdapter.adaptPlayer(handler.field_14140));
        WorldEdit.getInstance().getEventBus().post(new SessionIdleEvent(new FabricPlayer.SessionKeyImpl(handler.field_14140)));
    }

    FabricConfiguration getConfig() {
        return this.config;
    }

    public LocalSession getSession(class_3222 player) {
        Preconditions.checkNotNull((Object)player);
        return WorldEdit.getInstance().getSessionManager().get(FabricAdapter.adaptPlayer(player));
    }

    public FabricWorld getWorld(class_1937 world) {
        Preconditions.checkNotNull((Object)world);
        return new FabricWorld(world);
    }

    public Platform getPlatform() {
        return this.platform;
    }

    public Path getWorkingDir() {
        return this.workingDir;
    }

    String getInternalVersion() {
        return this.container.getMetadata().getVersion().getFriendlyString();
    }

    public void setPermissionsProvider(FabricPermissionsProvider provider) {
        this.provider = provider;
    }

    public FabricPermissionsProvider getPermissionsProvider() {
        return this.provider;
    }

    static {
        SimpleLifecycled<MinecraftServer> lifecycledServer = SimpleLifecycled.invalid();
        ServerLifecycleEvents.SERVER_STARTED.register(lifecycledServer::newValue);
        ServerLifecycleEvents.SERVER_STOPPING.register(__ -> lifecycledServer.invalidate());
        LIFECYCLED_SERVER = lifecycledServer;
    }
}

