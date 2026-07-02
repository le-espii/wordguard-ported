/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  org.bukkit.Bukkit
 *  org.bukkit.NamespacedKey
 *  org.bukkit.Registry
 *  org.bukkit.Server
 *  org.bukkit.World
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package com.sk89q.worldedit.bukkit;

import com.google.common.collect.Sets;
import com.sk89q.bukkit.util.CommandInfo;
import com.sk89q.bukkit.util.CommandRegistration;
import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitCommandInspector;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.BukkitRegistries;
import com.sk89q.worldedit.bukkit.BukkitWatchdog;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.adapter.BukkitImplAdapter;
import com.sk89q.worldedit.bukkit.paperlib.PaperLib;
import com.sk89q.worldedit.command.util.PermissionCondition;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.AbstractPlatform;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.extension.platform.MultiUserPlatform;
import com.sk89q.worldedit.extension.platform.Preference;
import com.sk89q.worldedit.extension.platform.Watchdog;
import com.sk89q.worldedit.util.SideEffect;
import com.sk89q.worldedit.util.formatting.WorldEditText;
import com.sk89q.worldedit.util.lifecycle.Lifecycled;
import com.sk89q.worldedit.world.DataFixer;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.registry.Registries;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.enginehub.piston.CommandManager;

public class BukkitServerInterface
extends AbstractPlatform
implements MultiUserPlatform {
    public final Server server;
    public final WorldEditPlugin plugin;
    private final CommandRegistration dynamicCommands;
    private final Lifecycled<Watchdog> watchdog;
    private boolean hookingEvents;
    private static final Set<SideEffect> SUPPORTED_SIDE_EFFECTS = Sets.immutableEnumSet((Enum)SideEffect.NEIGHBORS, (Enum[])new SideEffect[0]);

    public BukkitServerInterface(WorldEditPlugin plugin, Server server) {
        this.plugin = plugin;
        this.server = server;
        this.dynamicCommands = new CommandRegistration((Plugin)plugin);
        this.watchdog = plugin.getLifecycledBukkitImplAdapter().filter(BukkitImplAdapter::supportsWatchdog).map(BukkitWatchdog::new);
    }

    CommandRegistration getDynamicCommands() {
        return this.dynamicCommands;
    }

    boolean isHookingEvents() {
        return this.hookingEvents;
    }

    @Override
    public Registries getRegistries() {
        return BukkitRegistries.getInstance();
    }

    @Override
    public int getDataVersion() {
        if (this.plugin.getLifecycledBukkitImplAdapter().isValid()) {
            return Bukkit.getUnsafe().getDataVersion();
        }
        return -1;
    }

    @Override
    public DataFixer getDataFixer() {
        if (this.plugin.getBukkitImplAdapter() != null) {
            return this.plugin.getBukkitImplAdapter().getDataFixer();
        }
        return null;
    }

    @Override
    public boolean isValidMobType(String type) {
        NamespacedKey entityKey = NamespacedKey.fromString((String)type);
        if (entityKey == null) {
            return false;
        }
        return Registry.ENTITY_TYPE.get(entityKey) != null;
    }

    @Override
    public void reload() {
        this.plugin.loadConfiguration();
        super.reload();
    }

    @Override
    public int schedule(long delay, long period, Runnable task) {
        return Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)this.plugin, task, delay, period);
    }

    @Override
    public Watchdog getWatchdog() {
        return this.watchdog.value().orElse(null);
    }

    public List<World> getWorlds() {
        List worlds = this.server.getWorlds();
        ArrayList<World> ret = new ArrayList<World>(worlds.size());
        for (org.bukkit.World world : worlds) {
            ret.add(BukkitAdapter.adapt(world));
        }
        return ret;
    }

    @Override
    @Nullable
    public Player matchPlayer(Player player) {
        if (player instanceof BukkitPlayer) {
            return player;
        }
        org.bukkit.entity.Player bukkitPlayer = this.server.getPlayerExact(player.getName());
        return bukkitPlayer != null ? WorldEditPlugin.getInstance().wrapPlayer(bukkitPlayer) : null;
    }

    @Override
    @Nullable
    public BukkitWorld matchWorld(World world) {
        if (world instanceof BukkitWorld) {
            return (BukkitWorld)world;
        }
        org.bukkit.World bukkitWorld = this.server.getWorld(world.getName());
        return bukkitWorld != null ? new BukkitWorld(bukkitWorld) : null;
    }

    @Override
    public void registerCommands(CommandManager dispatcher) {
        BukkitCommandInspector inspector = new BukkitCommandInspector(this.plugin, dispatcher);
        this.dynamicCommands.register(dispatcher.getAllCommands().map(command -> {
            String[] permissionsArray = command.getCondition().as(PermissionCondition.class).map(PermissionCondition::getPermissions).map(s -> s.toArray(new String[0])).orElseGet(() -> new String[0]);
            String[] aliases = (String[])Stream.concat(Stream.of(command.getName()), command.getAliases().stream()).toArray(String[]::new);
            return new CommandInfo(WorldEditText.reduceToText(command.getUsage(), WorldEdit.getInstance().getConfiguration().defaultLocale), WorldEditText.reduceToText(command.getDescription(), WorldEdit.getInstance().getConfiguration().defaultLocale), aliases, inspector, permissionsArray);
        }).collect(Collectors.toList()));
    }

    @Override
    public void setGameHooksEnabled(boolean enabled) {
        this.hookingEvents = enabled;
    }

    @Override
    public LocalConfiguration getConfiguration() {
        return this.plugin.getLocalConfiguration();
    }

    @Override
    public String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    @Override
    public String getPlatformName() {
        return "Bukkit-Official";
    }

    @Override
    public String getPlatformVersion() {
        return this.plugin.getDescription().getVersion();
    }

    @Override
    public String id() {
        return "enginehub:bukkit";
    }

    @Override
    public Map<Capability, Preference> getCapabilities() {
        EnumMap<Capability, Preference> capabilities = new EnumMap<Capability, Preference>(Capability.class);
        capabilities.put(Capability.CONFIGURATION, Preference.NORMAL);
        capabilities.put(Capability.WORLDEDIT_CUI, Preference.NORMAL);
        capabilities.put(Capability.GAME_HOOKS, Preference.PREFERRED);
        capabilities.put(Capability.PERMISSIONS, Preference.PREFERRED);
        capabilities.put(Capability.USER_COMMANDS, Preference.PREFERRED);
        capabilities.put(Capability.WORLD_EDITING, Preference.PREFER_OTHERS);
        return capabilities;
    }

    @Override
    public Set<SideEffect> getSupportedSideEffects() {
        if (this.plugin.getBukkitImplAdapter() != null) {
            return this.plugin.getBukkitImplAdapter().getSupportedSideEffects();
        }
        return SUPPORTED_SIDE_EFFECTS;
    }

    @Override
    public long getTickCount() {
        if (PaperLib.isPaper()) {
            return Bukkit.getCurrentTick();
        }
        return super.getTickCount();
    }

    public void unregisterCommands() {
        this.dynamicCommands.unregisterCommands();
    }

    @Override
    public Collection<Actor> getConnectedUsers() {
        ArrayList<Actor> users = new ArrayList<Actor>();
        for (org.bukkit.entity.Player player : Bukkit.getServer().getOnlinePlayers()) {
            users.add(WorldEditPlugin.getInstance().wrapPlayer(player));
        }
        return users;
    }
}

