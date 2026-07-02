/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.ParseResults
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Holder
 *  net.minecraft.core.HolderSet$ListBacked
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.ModContainer
 *  net.neoforged.fml.ModLoadingContext
 *  net.neoforged.fml.common.Mod
 *  net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
 *  net.neoforged.fml.loading.FMLPaths
 *  net.neoforged.neoforge.common.NeoForge
 *  net.neoforged.neoforge.event.CommandEvent
 *  net.neoforged.neoforge.event.RegisterCommandsEvent
 *  net.neoforged.neoforge.event.entity.player.PlayerEvent$PlayerLoggedOutEvent
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$LeftClickBlock
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$RightClickItem
 *  net.neoforged.neoforge.event.server.ServerAboutToStartEvent
 *  net.neoforged.neoforge.event.server.ServerStartedEvent
 *  net.neoforged.neoforge.event.server.ServerStoppingEvent
 *  org.apache.logging.log4j.Logger
 */
package com.sk89q.worldedit.neoforge;

import com.google.common.base.Preconditions;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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
import com.sk89q.worldedit.internal.anvil.ChunkDeleter;
import com.sk89q.worldedit.internal.event.InteractionDebouncer;
import com.sk89q.worldedit.internal.util.LogManagerCompat;
import com.sk89q.worldedit.neoforge.CommandWrapper;
import com.sk89q.worldedit.neoforge.NeoForgeAdapter;
import com.sk89q.worldedit.neoforge.NeoForgeConfiguration;
import com.sk89q.worldedit.neoforge.NeoForgePermissionsProvider;
import com.sk89q.worldedit.neoforge.NeoForgePlatform;
import com.sk89q.worldedit.neoforge.NeoForgePlayer;
import com.sk89q.worldedit.neoforge.NeoForgeWorld;
import com.sk89q.worldedit.neoforge.ThreadSafeCache;
import com.sk89q.worldedit.neoforge.net.handler.WECUIPacketHandler;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.Location;
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
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.CommandEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.apache.logging.log4j.Logger;
import org.enginehub.piston.Command;

@Mod(value="worldedit")
public class NeoForgeWorldEdit {
    private static final Logger LOGGER = LogManagerCompat.getLogger();
    public static final String MOD_ID = "worldedit";
    public static final String CUI_PLUGIN_CHANNEL = "cui";
    private NeoForgePermissionsProvider provider;
    public static NeoForgeWorldEdit inst;
    private InteractionDebouncer debouncer;
    private NeoForgePlatform platform;
    private NeoForgeConfiguration config;
    private Path workingDir;
    private ModContainer container;

    public NeoForgeWorldEdit(IEventBus modBus) {
        inst = this;
        modBus.addListener(this::init);
        modBus.register(WECUIPacketHandler.class);
        NeoForge.EVENT_BUS.register((Object)ThreadSafeCache.getInstance());
        NeoForge.EVENT_BUS.register((Object)this);
    }

    private void init(FMLCommonSetupEvent event) {
        this.container = ModLoadingContext.get().getActiveContainer();
        this.workingDir = FMLPaths.CONFIGDIR.get().resolve(MOD_ID);
        if (!Files.exists(this.workingDir, new LinkOption[0])) {
            try {
                Files.createDirectory(this.workingDir, new FileAttribute[0]);
            }
            catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        this.setupPlatform();
        LOGGER.info("WorldEdit for NeoForge (version {}) is loaded", (Object)this.getInternalVersion());
    }

    private void setupPlatform() {
        this.platform = new NeoForgePlatform(this);
        this.debouncer = new InteractionDebouncer(this.platform);
        WorldEdit.getInstance().getPlatformManager().register(this.platform);
        this.config = new NeoForgeConfiguration(this);
        this.provider = new NeoForgePermissionsProvider.VanillaPermissionsProvider(this.platform);
    }

    private void setupRegistries(MinecraftServer server) {
        String key;
        String key2;
        for (ResourceLocation name2 : BuiltInRegistries.BLOCK.keySet()) {
            key2 = name2.toString();
            if (BlockType.REGISTRY.get(key2) != null) continue;
            BlockType.REGISTRY.register(key2, new BlockType(key2, input -> NeoForgeAdapter.adapt(NeoForgeAdapter.adapt(input.getBlockType()).defaultBlockState())));
        }
        for (ResourceLocation name2 : BuiltInRegistries.ITEM.keySet()) {
            key2 = name2.toString();
            if (ItemType.REGISTRY.get(key2) != null) continue;
            ItemType.REGISTRY.register(key2, new ItemType(key2));
        }
        for (ResourceLocation name2 : BuiltInRegistries.ENTITY_TYPE.keySet()) {
            key2 = name2.toString();
            if (EntityType.REGISTRY.get(key2) != null) continue;
            EntityType.REGISTRY.register(key2, new EntityType(key2));
        }
        for (ResourceLocation name2 : server.registryAccess().registryOrThrow(Registries.BIOME).keySet()) {
            key2 = name2.toString();
            if (BiomeType.REGISTRY.get(key2) != null) continue;
            BiomeType.REGISTRY.register(key2, new BiomeType(key2));
        }
        server.registryAccess().registryOrThrow(Registries.BLOCK).getTagNames().map(TagKey::location).forEach(name -> {
            String key = name.toString();
            if (BlockCategory.REGISTRY.get(key) == null) {
                BlockCategory.REGISTRY.register(key, new BlockCategory(key));
            }
        });
        server.registryAccess().registryOrThrow(Registries.ITEM).getTagNames().map(TagKey::location).forEach(name -> {
            String key = name.toString();
            if (ItemCategory.REGISTRY.get(key) == null) {
                ItemCategory.REGISTRY.register(key, new ItemCategory(key));
            }
        });
        Registry biomeRegistry = server.registryAccess().registryOrThrow(Registries.BIOME);
        biomeRegistry.getTagNames().forEach(tagKey -> {
            String key = tagKey.location().toString();
            if (BiomeCategory.REGISTRY.get(key) == null) {
                BiomeCategory.REGISTRY.register(key, new BiomeCategory(key, () -> biomeRegistry.getTag(tagKey).stream().flatMap(HolderSet.ListBacked::stream).map(Holder::value).map(NeoForgeAdapter::adapt).collect(Collectors.toSet())));
            }
        });
        for (ResourceLocation name3 : server.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE).keySet()) {
            key = name3.toString();
            if (ConfiguredFeatureType.REGISTRY.get(key) != null) continue;
            ConfiguredFeatureType.REGISTRY.register(key, new ConfiguredFeatureType(key));
        }
        for (ResourceLocation name3 : server.registryAccess().registryOrThrow(Registries.STRUCTURE).keySet()) {
            key = name3.toString();
            if (StructureType.REGISTRY.get(key) != null) continue;
            StructureType.REGISTRY.register(key, new StructureType(key));
        }
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        WorldEdit.getInstance().getEventBus().post(new PlatformsRegisteredEvent());
        PlatformManager manager = WorldEdit.getInstance().getPlatformManager();
        Platform commandsPlatform = manager.queryCapability(Capability.USER_COMMANDS);
        if (commandsPlatform != this.platform || !this.platform.isHookingEvents()) {
            return;
        }
        List commands = manager.getPlatformCommandManager().getCommandManager().getAllCommands().toList();
        for (Command command : commands) {
            CommandWrapper.register((CommandDispatcher<CommandSourceStack>)event.getDispatcher(), command);
            Set perms = command.getCondition().as(PermissionCondition.class).map(PermissionCondition::getPermissions).orElseGet(Collections::emptySet);
            if (perms.isEmpty()) continue;
            perms.forEach(this.getPermissionsProvider()::registerPermission);
        }
    }

    @SubscribeEvent
    public void serverAboutToStart(ServerAboutToStartEvent event) {
        Path delChunks = this.workingDir.resolve("delete_chunks.json");
        if (Files.exists(delChunks, new LinkOption[0])) {
            ChunkDeleter.runFromFile(delChunks, true);
        }
    }

    @SubscribeEvent
    public void serverStopping(ServerStoppingEvent event) {
        WorldEdit worldEdit = WorldEdit.getInstance();
        worldEdit.getSessionManager().unload();
        WorldEdit.getInstance().getEventBus().post(new PlatformUnreadyEvent(this.platform));
    }

    @SubscribeEvent
    public void serverStarted(ServerStartedEvent event) {
        this.setupRegistries(event.getServer());
        this.config.load();
        WorldEdit.getInstance().getEventBus().post(new PlatformReadyEvent(this.platform));
    }

    private boolean skipEvents() {
        return this.platform == null || !this.platform.isHookingEvents();
    }

    private boolean skipInteractionEvent(Player player, InteractionHand hand) {
        return this.skipEvents() || hand != InteractionHand.MAIN_HAND || player.level().isClientSide || !(player instanceof ServerPlayer);
    }

    @SubscribeEvent
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (this.skipInteractionEvent(event.getEntity(), event.getHand()) || event.getUseItem().isFalse()) {
            return;
        }
        ServerPlayer playerEntity = (ServerPlayer)event.getEntity();
        WorldEdit we = WorldEdit.getInstance();
        NeoForgePlayer player = NeoForgeAdapter.adaptPlayer(playerEntity);
        NeoForgeWorld world = this.getWorld((ServerLevel)playerEntity.level());
        Direction direction = NeoForgeAdapter.adaptEnumFacing(event.getFace());
        BlockPos blockPos = event.getPos();
        Location pos = new Location((Extent)world, blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
        boolean result = we.handleBlockLeftClick(player, pos, direction) || we.handleArmSwing(player);
        this.debouncer.setLastInteraction(player, result);
        if (result) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (this.skipInteractionEvent(event.getEntity(), event.getHand()) || event.getUseItem().isFalse()) {
            return;
        }
        ServerPlayer playerEntity = (ServerPlayer)event.getEntity();
        WorldEdit we = WorldEdit.getInstance();
        NeoForgePlayer player = NeoForgeAdapter.adaptPlayer(playerEntity);
        NeoForgeWorld world = this.getWorld((ServerLevel)playerEntity.level());
        Direction direction = NeoForgeAdapter.adaptEnumFacing(event.getFace());
        BlockPos blockPos = event.getPos();
        Location pos = new Location((Extent)world, blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
        boolean result = we.handleBlockRightClick(player, pos, direction) || we.handleRightClick(player);
        this.debouncer.setLastInteraction(player, result);
        if (result) {
            event.setCanceled(true);
        }
    }

    public void onLeftClickAir(ServerPlayer playerEntity, InteractionHand hand) {
        if (this.skipInteractionEvent((Player)playerEntity, hand)) {
            return;
        }
        WorldEdit we = WorldEdit.getInstance();
        NeoForgePlayer player = NeoForgeAdapter.adaptPlayer(playerEntity);
        Optional<Boolean> previousResult = this.debouncer.getDuplicateInteractionResult(player);
        if (previousResult.isPresent()) {
            return;
        }
        boolean result = we.handleArmSwing(player);
        this.debouncer.setLastInteraction(player, result);
    }

    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (this.skipInteractionEvent(event.getEntity(), event.getHand())) {
            return;
        }
        ServerPlayer playerEntity = (ServerPlayer)event.getEntity();
        WorldEdit we = WorldEdit.getInstance();
        NeoForgePlayer player = NeoForgeAdapter.adaptPlayer(playerEntity);
        Optional<Boolean> previousResult = this.debouncer.getDuplicateInteractionResult(player);
        if (previousResult.isPresent()) {
            if (previousResult.get().booleanValue()) {
                event.setCanceled(true);
            }
            return;
        }
        boolean result = we.handleRightClick(player);
        this.debouncer.setLastInteraction(player, result);
        if (result) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onCommandEvent(CommandEvent event) throws CommandSyntaxException {
        ParseResults parseResults = event.getParseResults();
        Entity entity = ((CommandSourceStack)parseResults.getContext().getSource()).getEntity();
        if (entity instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer)entity;
            if (player.level().isClientSide) {
                return;
            }
        }
        if (parseResults.getContext().getCommand() != CommandWrapper.FAKE_COMMAND) {
            return;
        }
        event.setCanceled(true);
        WorldEdit.getInstance().getEventBus().post(new com.sk89q.worldedit.event.platform.CommandEvent(NeoForgeAdapter.adaptCommandSource((CommandSourceStack)parseResults.getContext().getSource()), "/" + parseResults.getReader().getString()));
    }

    @SubscribeEvent
    public void onPlayerLogOut(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer player2 = (ServerPlayer)player;
            this.debouncer.clearInteraction(NeoForgeAdapter.adaptPlayer(player2));
            WorldEdit.getInstance().getEventBus().post(new SessionIdleEvent(new NeoForgePlayer.SessionKeyImpl(player2)));
        }
    }

    NeoForgeConfiguration getConfig() {
        return this.config;
    }

    public LocalSession getSession(ServerPlayer player) {
        Preconditions.checkNotNull((Object)player);
        return WorldEdit.getInstance().getSessionManager().get(NeoForgeAdapter.adaptPlayer(player));
    }

    public NeoForgeWorld getWorld(ServerLevel world) {
        Preconditions.checkNotNull((Object)world);
        return new NeoForgeWorld(world);
    }

    public Platform getPlatform() {
        return this.platform;
    }

    public Path getWorkingDir() {
        return this.workingDir;
    }

    String getInternalVersion() {
        return this.container.getModInfo().getVersion().toString();
    }

    public void setPermissionsProvider(NeoForgePermissionsProvider provider) {
        this.provider = provider;
    }

    public NeoForgePermissionsProvider getPermissionsProvider() {
        return this.provider;
    }
}

