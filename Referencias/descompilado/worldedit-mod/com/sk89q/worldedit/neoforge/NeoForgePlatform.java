/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Sets
 *  com.mojang.brigadier.CommandDispatcher
 *  javax.annotation.Nullable
 *  net.minecraft.SharedConstants
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.dedicated.DedicatedServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.server.players.PlayerList
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.storage.ServerLevelData
 *  net.neoforged.neoforge.server.ServerLifecycleHooks
 */
package com.sk89q.worldedit.neoforge;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.command.util.PermissionCondition;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.AbstractPlatform;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.extension.platform.MultiUserPlatform;
import com.sk89q.worldedit.extension.platform.Preference;
import com.sk89q.worldedit.neoforge.CommandWrapper;
import com.sk89q.worldedit.neoforge.NeoForgeConfiguration;
import com.sk89q.worldedit.neoforge.NeoForgeDataFixer;
import com.sk89q.worldedit.neoforge.NeoForgePlayer;
import com.sk89q.worldedit.neoforge.NeoForgeRegistries;
import com.sk89q.worldedit.neoforge.NeoForgeResourceLoader;
import com.sk89q.worldedit.neoforge.NeoForgeWatchdog;
import com.sk89q.worldedit.neoforge.NeoForgeWorld;
import com.sk89q.worldedit.neoforge.NeoForgeWorldEdit;
import com.sk89q.worldedit.neoforge.internal.ExtendedChunk;
import com.sk89q.worldedit.util.SideEffect;
import com.sk89q.worldedit.util.io.ResourceLoader;
import com.sk89q.worldedit.world.DataFixer;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.registry.Registries;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.storage.ServerLevelData;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.enginehub.piston.Command;
import org.enginehub.piston.CommandManager;

class NeoForgePlatform
extends AbstractPlatform
implements MultiUserPlatform {
    private final NeoForgeWorldEdit mod;
    private final NeoForgeDataFixer dataFixer;
    @Nullable
    private NeoForgeWatchdog watchdog;
    private boolean hookingEvents = false;
    private final ResourceLoader resourceLoader = new NeoForgeResourceLoader(WorldEdit.getInstance());
    private static final Set<SideEffect> SUPPORTED_SIDE_EFFECTS_NO_MIXIN = Sets.immutableEnumSet((Enum)SideEffect.VALIDATION, (Enum[])new SideEffect[]{SideEffect.ENTITY_AI, SideEffect.LIGHTING, SideEffect.NEIGHBORS, SideEffect.EVENTS});
    private static final Set<SideEffect> SUPPORTED_SIDE_EFFECTS = Sets.immutableEnumSet((Iterable)Iterables.concat(SUPPORTED_SIDE_EFFECTS_NO_MIXIN, Collections.singleton(SideEffect.UPDATE)));

    NeoForgePlatform(NeoForgeWorldEdit mod) {
        this.mod = mod;
        this.dataFixer = new NeoForgeDataFixer(this.getDataVersion());
    }

    boolean isHookingEvents() {
        return this.hookingEvents;
    }

    @Override
    public ResourceLoader getResourceLoader() {
        return this.resourceLoader;
    }

    @Override
    public Registries getRegistries() {
        return NeoForgeRegistries.getInstance();
    }

    @Override
    public int getDataVersion() {
        return SharedConstants.getCurrentVersion().getDataVersion().getVersion();
    }

    @Override
    public DataFixer getDataFixer() {
        return this.dataFixer;
    }

    @Override
    public boolean isValidMobType(String type) {
        return BuiltInRegistries.ENTITY_TYPE.containsKey(ResourceLocation.parse((String)type));
    }

    @Override
    public void reload() {
        this.getConfiguration().load();
        super.reload();
    }

    @Override
    public int schedule(long delay, long period, Runnable task) {
        return -1;
    }

    @Override
    @Nullable
    public NeoForgeWatchdog getWatchdog() {
        MinecraftServer server;
        if (this.watchdog == null && (server = ServerLifecycleHooks.getCurrentServer()) instanceof DedicatedServer) {
            this.watchdog = new NeoForgeWatchdog((DedicatedServer)server);
        }
        return this.watchdog;
    }

    @Override
    public List<? extends World> getWorlds() {
        Iterable worlds = ServerLifecycleHooks.getCurrentServer().getAllLevels();
        ArrayList<NeoForgeWorld> ret = new ArrayList<NeoForgeWorld>();
        for (ServerLevel world : worlds) {
            ret.add(new NeoForgeWorld(world));
        }
        return ret;
    }

    @Override
    @Nullable
    public Player matchPlayer(Player player) {
        if (player instanceof NeoForgePlayer) {
            return player;
        }
        ServerPlayer entity = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByName(player.getName());
        return entity != null ? new NeoForgePlayer(entity) : null;
    }

    @Override
    @Nullable
    public World matchWorld(World world) {
        if (world instanceof NeoForgeWorld) {
            return world;
        }
        for (ServerLevel ws : ServerLifecycleHooks.getCurrentServer().getAllLevels()) {
            if (!((ServerLevelData)ws.getLevelData()).getLevelName().equals(world.getName())) continue;
            return new NeoForgeWorld(ws);
        }
        return null;
    }

    @Override
    public void registerCommands(CommandManager manager) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return;
        }
        Commands mcMan = server.getCommands();
        for (Command command : manager.getAllCommands().toList()) {
            CommandWrapper.register((CommandDispatcher<CommandSourceStack>)mcMan.getDispatcher(), command);
            Set perms = command.getCondition().as(PermissionCondition.class).map(PermissionCondition::getPermissions).orElseGet(Collections::emptySet);
            if (perms.isEmpty()) continue;
            perms.forEach(NeoForgeWorldEdit.inst.getPermissionsProvider()::registerPermission);
        }
    }

    @Override
    public void setGameHooksEnabled(boolean enabled) {
        this.hookingEvents = enabled;
    }

    @Override
    public NeoForgeConfiguration getConfiguration() {
        return this.mod.getConfig();
    }

    @Override
    public String getVersion() {
        return this.mod.getInternalVersion();
    }

    @Override
    public String getPlatformName() {
        return "Forge-Official";
    }

    @Override
    public String getPlatformVersion() {
        return this.mod.getInternalVersion();
    }

    @Override
    public String id() {
        return "enginehub:forge";
    }

    @Override
    public Map<Capability, Preference> getCapabilities() {
        EnumMap<Capability, Preference> capabilities = new EnumMap<Capability, Preference>(Capability.class);
        capabilities.put(Capability.CONFIGURATION, Preference.PREFER_OTHERS);
        capabilities.put(Capability.WORLDEDIT_CUI, Preference.NORMAL);
        capabilities.put(Capability.GAME_HOOKS, Preference.NORMAL);
        capabilities.put(Capability.PERMISSIONS, Preference.NORMAL);
        capabilities.put(Capability.USER_COMMANDS, Preference.NORMAL);
        capabilities.put(Capability.WORLD_EDITING, Preference.PREFERRED);
        return capabilities;
    }

    @Override
    public Set<SideEffect> getSupportedSideEffects() {
        return ExtendedChunk.class.isAssignableFrom(LevelChunk.class) ? SUPPORTED_SIDE_EFFECTS : SUPPORTED_SIDE_EFFECTS_NO_MIXIN;
    }

    @Override
    public long getTickCount() {
        return ServerLifecycleHooks.getCurrentServer().getTickCount();
    }

    @Override
    public Collection<Actor> getConnectedUsers() {
        ArrayList<Actor> users = new ArrayList<Actor>();
        PlayerList scm = ServerLifecycleHooks.getCurrentServer().getPlayerList();
        for (ServerPlayer entity : scm.getPlayers()) {
            if (entity == null) continue;
            users.add(new NeoForgePlayer(entity));
        }
        return users;
    }
}

