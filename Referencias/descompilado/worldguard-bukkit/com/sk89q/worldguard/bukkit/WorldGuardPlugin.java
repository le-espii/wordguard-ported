/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.sk89q.bukkit.util.CommandsManagerRegistration
 *  com.sk89q.minecraft.util.commands.CommandException
 *  com.sk89q.minecraft.util.commands.CommandPermissionsException
 *  com.sk89q.minecraft.util.commands.CommandUsageException
 *  com.sk89q.minecraft.util.commands.CommandsManager
 *  com.sk89q.minecraft.util.commands.Injector
 *  com.sk89q.minecraft.util.commands.MissingNestedCommandException
 *  com.sk89q.minecraft.util.commands.SimpleInjector
 *  com.sk89q.minecraft.util.commands.WrappedCommandException
 *  com.sk89q.wepif.PermissionsResolverManager
 *  com.sk89q.worldedit.bukkit.BukkitAdapter
 *  com.sk89q.worldedit.bukkit.BukkitCommandSender
 *  com.sk89q.worldedit.bukkit.WorldEditPlugin
 *  com.sk89q.worldedit.extension.platform.Actor
 *  com.sk89q.worldedit.world.World
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.World
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.enginehub.piston.exception.CommandException
 */
package com.sk89q.worldguard.bukkit;

import com.google.common.collect.ImmutableList;
import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.sk89q.minecraft.util.commands.CommandUsageException;
import com.sk89q.minecraft.util.commands.CommandsManager;
import com.sk89q.minecraft.util.commands.Injector;
import com.sk89q.minecraft.util.commands.MissingNestedCommandException;
import com.sk89q.minecraft.util.commands.SimpleInjector;
import com.sk89q.minecraft.util.commands.WrappedCommandException;
import com.sk89q.wepif.PermissionsResolverManager;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitCommandSender;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.blacklist.Blacklist;
import com.sk89q.worldguard.bukkit.BukkitConfigurationManager;
import com.sk89q.worldguard.bukkit.BukkitOfflinePlayer;
import com.sk89q.worldguard.bukkit.BukkitPlayer;
import com.sk89q.worldguard.bukkit.BukkitWorldConfiguration;
import com.sk89q.worldguard.bukkit.BukkitWorldGuardPlatform;
import com.sk89q.worldguard.bukkit.ProtectionQuery;
import com.sk89q.worldguard.bukkit.bstats.bukkit.Metrics;
import com.sk89q.worldguard.bukkit.bstats.charts.DrilldownPie;
import com.sk89q.worldguard.bukkit.bstats.charts.SimplePie;
import com.sk89q.worldguard.bukkit.bstats.charts.SingleLineChart;
import com.sk89q.worldguard.bukkit.event.player.ProcessPlayerEvent;
import com.sk89q.worldguard.bukkit.listener.BlacklistListener;
import com.sk89q.worldguard.bukkit.listener.BlockedPotionsListener;
import com.sk89q.worldguard.bukkit.listener.BuildPermissionListener;
import com.sk89q.worldguard.bukkit.listener.ChestProtectionListener;
import com.sk89q.worldguard.bukkit.listener.DebuggingListener;
import com.sk89q.worldguard.bukkit.listener.EventAbstractionListener;
import com.sk89q.worldguard.bukkit.listener.InvincibilityListener;
import com.sk89q.worldguard.bukkit.listener.PlayerModesListener;
import com.sk89q.worldguard.bukkit.listener.PlayerMoveListener;
import com.sk89q.worldguard.bukkit.listener.RegionFlagsListener;
import com.sk89q.worldguard.bukkit.listener.RegionProtectionListener;
import com.sk89q.worldguard.bukkit.listener.WorldGuardBlockListener;
import com.sk89q.worldguard.bukkit.listener.WorldGuardCommandBookListener;
import com.sk89q.worldguard.bukkit.listener.WorldGuardEntityListener;
import com.sk89q.worldguard.bukkit.listener.WorldGuardHangingListener;
import com.sk89q.worldguard.bukkit.listener.WorldGuardPlayerListener;
import com.sk89q.worldguard.bukkit.listener.WorldGuardServerListener;
import com.sk89q.worldguard.bukkit.listener.WorldGuardVehicleListener;
import com.sk89q.worldguard.bukkit.listener.WorldGuardWeatherListener;
import com.sk89q.worldguard.bukkit.listener.WorldGuardWorldListener;
import com.sk89q.worldguard.bukkit.listener.WorldRulesListener;
import com.sk89q.worldguard.bukkit.session.BukkitSessionManager;
import com.sk89q.worldguard.bukkit.util.ClassSourceValidator;
import com.sk89q.worldguard.bukkit.util.Entities;
import com.sk89q.worldguard.bukkit.util.Events;
import com.sk89q.worldguard.commands.GeneralCommands;
import com.sk89q.worldguard.commands.ProtectionCommands;
import com.sk89q.worldguard.commands.ToggleCommands;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.registry.SimpleFlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.RegionDriver;
import com.sk89q.worldguard.protection.managers.storage.file.DirectoryYamlDriver;
import com.sk89q.worldguard.protection.managers.storage.sql.SQLDriver;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.util.logging.RecordMessagePrefixer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldGuardPlugin
extends JavaPlugin {
    private static WorldGuardPlugin inst;
    private static BukkitWorldGuardPlatform platform;
    private final CommandsManager<Actor> commands;
    private PlayerMoveListener playerMoveListener;
    private static final int BSTATS_PLUGIN_ID = 3283;

    public WorldGuardPlugin() {
        inst = this;
        this.commands = new CommandsManager<Actor>(this){

            public boolean hasPermission(Actor player, String perm) {
                return player.hasPermission(perm);
            }
        };
    }

    public static WorldGuardPlugin inst() {
        return inst;
    }

    public void onEnable() {
        ClassSourceValidator verifier = new ClassSourceValidator((Plugin)this);
        verifier.reportMismatches((List<Class<?>>)ImmutableList.of(WorldGuard.class, ProtectedRegion.class, Flag.class));
        this.configureLogger();
        this.getDataFolder().mkdirs();
        PermissionsResolverManager.initialize((Plugin)this);
        platform = new BukkitWorldGuardPlatform();
        WorldGuard.getInstance().setPlatform(platform);
        WorldGuard.getInstance().setup();
        BukkitSessionManager sessionManager = (BukkitSessionManager)platform.getSessionManager();
        this.commands.setInjector((Injector)new SimpleInjector(new Object[]{WorldGuard.getInstance()}));
        CommandsManagerRegistration reg = new CommandsManagerRegistration((Plugin)this, this.commands);
        reg.register(ToggleCommands.class);
        reg.register(ProtectionCommands.class);
        if (!platform.getGlobalStateManager().hasCommandBookGodMode()) {
            reg.register(GeneralCommands.class);
        }
        this.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)this, (Runnable)sessionManager, 20L, 20L);
        this.getServer().getPluginManager().registerEvents((Listener)sessionManager, (Plugin)this);
        new WorldGuardPlayerListener(this).registerEvents();
        new WorldGuardBlockListener(this).registerEvents();
        new WorldGuardEntityListener(this).registerEvents();
        new WorldGuardWeatherListener(this).registerEvents();
        new WorldGuardVehicleListener(this).registerEvents();
        new WorldGuardServerListener(this).registerEvents();
        new WorldGuardHangingListener(this).registerEvents();
        this.playerMoveListener = new PlayerMoveListener(this);
        this.playerMoveListener.registerEvents();
        new BlacklistListener(this).registerEvents();
        new ChestProtectionListener(this).registerEvents();
        new RegionProtectionListener(this).registerEvents();
        new RegionFlagsListener(this).registerEvents();
        new WorldRulesListener(this).registerEvents();
        new BlockedPotionsListener(this).registerEvents();
        new EventAbstractionListener(this).registerEvents();
        new PlayerModesListener(this).registerEvents();
        new BuildPermissionListener(this).registerEvents();
        new InvincibilityListener(this).registerEvents();
        if ("true".equalsIgnoreCase(System.getProperty("worldguard.debug.listener"))) {
            new DebuggingListener(this, WorldGuard.logger).registerEvents();
        }
        platform.getGlobalStateManager().updateCommandBookGodMode();
        if (this.getServer().getPluginManager().isPluginEnabled("CommandBook")) {
            this.getServer().getPluginManager().registerEvents((Listener)new WorldGuardCommandBookListener(this), (Plugin)this);
        }
        WorldGuardWorldListener worldListener = new WorldGuardWorldListener(this);
        for (World world : this.getServer().getWorlds()) {
            worldListener.initWorld(world);
        }
        worldListener.registerEvents();
        Bukkit.getScheduler().runTask((Plugin)this, () -> {
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                ProcessPlayerEvent event = new ProcessPlayerEvent(player);
                Events.fire(event);
            }
        });
        ((SimpleFlagRegistry)WorldGuard.getInstance().getFlagRegistry()).setInitialized(true);
        Metrics metrics = new Metrics(this, 3283);
        if (WorldGuardPlugin.platform.getGlobalStateManager().extraStats) {
            this.setupCustomCharts(metrics);
        }
    }

    private void setupCustomCharts(Metrics metrics) {
        metrics.addCustomChart(new SingleLineChart("region_count", () -> platform.getRegionContainer().getLoaded().stream().mapToInt(RegionManager::size).sum()));
        metrics.addCustomChart(new SimplePie("region_driver", () -> {
            RegionDriver driver = WorldGuardPlugin.platform.getGlobalStateManager().selectedRegionStoreDriver;
            return driver instanceof DirectoryYamlDriver ? "yaml" : (driver instanceof SQLDriver ? "sql" : "unknown");
        }));
        metrics.addCustomChart(new DrilldownPie("blacklist", () -> {
            int empty = 0;
            HashMap blacklistMap = new HashMap();
            HashMap<String, Integer> whitelistMap = new HashMap<String, Integer>();
            for (BukkitWorldConfiguration worldConfig : platform.getGlobalStateManager().getWorldConfigs()) {
                Blacklist blacklist = worldConfig.getBlacklist();
                if (blacklist != null && !blacklist.isEmpty()) {
                    HashMap<String, Integer> target = blacklist.isWhitelist() ? whitelistMap : blacklistMap;
                    int floor = (blacklist.getItemCount() - 1) / 10 * 10;
                    String range = floor >= 100 ? "101+" : floor + 1 + " - " + (floor + 10);
                    target.merge(range, 1, Integer::sum);
                    continue;
                }
                ++empty;
            }
            HashMap blacklistCounts = new HashMap();
            HashMap<String, Integer> emptyMap = new HashMap<String, Integer>();
            emptyMap.put("empty", empty);
            blacklistCounts.put("empty", emptyMap);
            blacklistCounts.put("blacklist", blacklistMap);
            blacklistCounts.put("whitelist", whitelistMap);
            return blacklistCounts;
        }));
        metrics.addCustomChart(new SimplePie("chest_protection", () -> "" + platform.getGlobalStateManager().getWorldConfigs().stream().anyMatch(cfg -> cfg.signChestProtection)));
        metrics.addCustomChart(new SimplePie("build_permissions", () -> "" + platform.getGlobalStateManager().getWorldConfigs().stream().anyMatch(cfg -> cfg.buildPermissions)));
        metrics.addCustomChart(new SimplePie("custom_flags", () -> "" + (WorldGuard.getInstance().getFlagRegistry().size() > Flags.INBUILT_FLAGS.size())));
        metrics.addCustomChart(new SimplePie("custom_handlers", () -> "" + WorldGuard.getInstance().getPlatform().getSessionManager().customHandlersRegistered()));
    }

    public void onDisable() {
        WorldGuard.getInstance().disable();
        this.getServer().getScheduler().cancelTasks((Plugin)this);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            Actor actor = this.wrapCommandSender(sender);
            try {
                this.commands.execute(cmd.getName(), args, (Object)actor, new Object[]{actor});
            }
            catch (Throwable t) {
                Throwable next = t;
                do {
                    try {
                        WorldGuard.getInstance().getExceptionConverter().convert(next);
                    }
                    catch (org.enginehub.piston.exception.CommandException pce) {
                        if (!(pce.getCause() instanceof CommandException)) continue;
                        throw (CommandException)pce.getCause();
                    }
                } while ((next = next.getCause()) != null);
                throw t;
            }
        }
        catch (CommandPermissionsException e) {
            sender.sendMessage(String.valueOf(ChatColor.RED) + "You don't have permission.");
        }
        catch (MissingNestedCommandException e) {
            sender.sendMessage(String.valueOf(ChatColor.RED) + e.getUsage());
        }
        catch (CommandUsageException e) {
            sender.sendMessage(String.valueOf(ChatColor.RED) + e.getMessage());
            sender.sendMessage(String.valueOf(ChatColor.RED) + e.getUsage());
        }
        catch (WrappedCommandException e) {
            sender.sendMessage(String.valueOf(ChatColor.RED) + e.getCause().getMessage());
        }
        catch (CommandException e) {
            sender.sendMessage(String.valueOf(ChatColor.RED) + e.getMessage());
        }
        return true;
    }

    public boolean inGroup(OfflinePlayer player, String group) {
        try {
            return PermissionsResolverManager.getInstance().inGroup(player, group);
        }
        catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }

    public String[] getGroups(OfflinePlayer player) {
        try {
            return PermissionsResolverManager.getInstance().getGroups(player);
        }
        catch (Throwable t) {
            t.printStackTrace();
            return new String[0];
        }
    }

    public boolean hasPermission(CommandSender sender, String perm) {
        if (sender.isOp()) {
            if (sender instanceof Player) {
                if (WorldGuardPlugin.platform.getGlobalStateManager().get((com.sk89q.worldedit.world.World)BukkitAdapter.adapt((World)((Player)sender).getWorld())).opPermissions) {
                    return true;
                }
            } else {
                return true;
            }
        }
        if (sender instanceof Player) {
            Player player = (Player)sender;
            return PermissionsResolverManager.getInstance().hasPermission(player.getWorld().getName(), (OfflinePlayer)player, perm);
        }
        return false;
    }

    public void checkPermission(CommandSender sender, String perm) throws CommandPermissionsException {
        if (!this.hasPermission(sender, perm)) {
            throw new CommandPermissionsException();
        }
    }

    public WorldEditPlugin getWorldEdit() throws CommandException {
        Plugin worldEdit = this.getServer().getPluginManager().getPlugin("WorldEdit");
        if (worldEdit == null) {
            throw new CommandException("WorldEdit does not appear to be installed.");
        }
        if (!worldEdit.isEnabled()) {
            throw new CommandException("WorldEdit does not appear to be enabled.");
        }
        if (worldEdit instanceof WorldEditPlugin) {
            return (WorldEditPlugin)worldEdit;
        }
        throw new CommandException("WorldEdit detection failed (report error).");
    }

    public LocalPlayer wrapPlayer(Player player) {
        return new BukkitPlayer(this, player);
    }

    public LocalPlayer wrapPlayer(Player player, boolean silenced) {
        return new BukkitPlayer(this, player, silenced);
    }

    public Actor wrapCommandSender(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player)sender;
            if (Entities.isNPC((Entity)player)) {
                return null;
            }
            return this.wrapPlayer(player);
        }
        try {
            return new BukkitCommandSender(this.getWorldEdit(), sender);
        }
        catch (CommandException e) {
            e.printStackTrace();
            return null;
        }
    }

    public CommandSender unwrapActor(Actor sender) {
        if (sender instanceof BukkitPlayer) {
            return ((BukkitPlayer)sender).getPlayer();
        }
        if (sender instanceof BukkitCommandSender) {
            return Bukkit.getConsoleSender();
        }
        throw new IllegalArgumentException("Unknown actor type. Please report");
    }

    public LocalPlayer wrapOfflinePlayer(OfflinePlayer player) {
        return new BukkitOfflinePlayer(this, player);
    }

    public BukkitConfigurationManager getConfigManager() {
        return platform.getGlobalStateManager();
    }

    public ProtectionQuery createProtectionQuery() {
        return new ProtectionQuery();
    }

    private void configureLogger() {
        RecordMessagePrefixer.register(Logger.getLogger("com.sk89q.worldguard"), "[WorldGuard] ");
    }

    public void createDefaultConfiguration(File actual, String defaultName) {
        File parent = actual.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        if (actual.exists()) {
            return;
        }
        try (InputStream stream = this.getResource("defaults/" + defaultName);){
            if (stream == null) {
                throw new FileNotFoundException();
            }
            this.copyDefaultConfig(stream, actual, defaultName);
        }
        catch (IOException e) {
            this.getLogger().severe("Unable to read default configuration: " + defaultName);
        }
    }

    private void copyDefaultConfig(InputStream input, File actual, String name) {
        try (FileOutputStream output = new FileOutputStream(actual);){
            int length;
            byte[] buf = new byte[8192];
            while ((length = input.read(buf)) > 0) {
                output.write(buf, 0, length);
            }
            this.getLogger().info("Default configuration file written: " + name);
        }
        catch (IOException e) {
            this.getLogger().log(Level.WARNING, "Failed to write default config file", e);
        }
    }

    public PlayerMoveListener getPlayerMoveListener() {
        return this.playerMoveListener;
    }
}

