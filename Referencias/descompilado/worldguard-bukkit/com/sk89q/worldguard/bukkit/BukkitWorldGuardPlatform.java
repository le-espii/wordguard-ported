/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.bukkit.BukkitAdapter
 *  com.sk89q.worldedit.bukkit.BukkitWorld
 *  com.sk89q.worldedit.math.BlockVector3
 *  com.sk89q.worldedit.util.formatting.text.Component
 *  com.sk89q.worldedit.util.formatting.text.TextComponent
 *  com.sk89q.worldedit.util.report.Report
 *  com.sk89q.worldedit.util.report.ReportList
 *  com.sk89q.worldedit.world.World
 *  com.sk89q.worldedit.world.gamemode.GameMode
 *  com.sk89q.worldedit.world.gamemode.GameModes
 *  javax.annotation.Nullable
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.inventory.ItemStack
 */
package com.sk89q.worldguard.bukkit;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.report.Report;
import com.sk89q.worldedit.util.report.ReportList;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.gamemode.GameMode;
import com.sk89q.worldedit.world.gamemode.GameModes;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.BukkitConfigurationManager;
import com.sk89q.worldguard.bukkit.BukkitDebugHandler;
import com.sk89q.worldguard.bukkit.BukkitPlayer;
import com.sk89q.worldguard.bukkit.BukkitRegionContainer;
import com.sk89q.worldguard.bukkit.BukkitStringMatcher;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.paperlib.PaperLib;
import com.sk89q.worldguard.bukkit.protection.events.flags.FlagContextCreateEvent;
import com.sk89q.worldguard.bukkit.session.BukkitSessionManager;
import com.sk89q.worldguard.bukkit.util.report.DatapackReport;
import com.sk89q.worldguard.bukkit.util.report.PerformanceReport;
import com.sk89q.worldguard.bukkit.util.report.PluginReport;
import com.sk89q.worldguard.bukkit.util.report.SchedulerReport;
import com.sk89q.worldguard.bukkit.util.report.ServerReport;
import com.sk89q.worldguard.bukkit.util.report.ServicesReport;
import com.sk89q.worldguard.bukkit.util.report.WorldReport;
import com.sk89q.worldguard.internal.platform.DebugHandler;
import com.sk89q.worldguard.internal.platform.StringMatcher;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.session.SessionManager;
import com.sk89q.worldguard.util.profile.cache.ProfileCache;
import com.sk89q.worldguard.util.profile.resolver.BukkitPlayerService;
import com.sk89q.worldguard.util.profile.resolver.CacheForwardingService;
import com.sk89q.worldguard.util.profile.resolver.CombinedProfileService;
import com.sk89q.worldguard.util.profile.resolver.HttpRepositoryService;
import com.sk89q.worldguard.util.profile.resolver.PaperPlayerService;
import com.sk89q.worldguard.util.profile.resolver.ProfileService;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

public class BukkitWorldGuardPlatform
implements WorldGuardPlatform {
    private BukkitSessionManager sessionManager;
    private BukkitConfigurationManager configuration;
    private BukkitRegionContainer regionContainer;
    private BukkitDebugHandler debugHandler;
    private StringMatcher stringMatcher;

    @Override
    public String getPlatformName() {
        return "Bukkit-Official";
    }

    @Override
    public String getPlatformVersion() {
        return WorldGuardPlugin.inst().getDescription().getVersion();
    }

    @Override
    public void notifyFlagContextCreate(FlagContext.FlagContextBuilder flagContextBuilder) {
        Bukkit.getServer().getPluginManager().callEvent((Event)new FlagContextCreateEvent(flagContextBuilder));
    }

    @Override
    public BukkitConfigurationManager getGlobalStateManager() {
        return this.configuration;
    }

    @Override
    public StringMatcher getMatcher() {
        return this.stringMatcher;
    }

    @Override
    public SessionManager getSessionManager() {
        return this.sessionManager;
    }

    @Override
    public void broadcastNotification(String message) {
        Bukkit.broadcast((String)message, (String)"worldguard.notify");
        Set subs = Bukkit.getPluginManager().getPermissionSubscriptions("worldguard.notify");
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (subs.contains(player) && player.hasPermission("worldguard.notify") || !WorldGuardPlugin.inst().hasPermission((CommandSender)player, "worldguard.notify")) continue;
            player.sendMessage(message);
        }
        WorldGuard.logger.info(message);
    }

    @Override
    public void broadcastNotification(TextComponent component) {
        List wgPlayers = Bukkit.getServer().getOnlinePlayers().stream().map(player -> WorldGuardPlugin.inst().wrapPlayer((Player)player)).collect(Collectors.toList());
        for (LocalPlayer player2 : wgPlayers) {
            if (!player2.hasPermission("worldguard.notify")) continue;
            player2.print((Component)component);
        }
    }

    @Override
    public void load() {
        this.stringMatcher = new BukkitStringMatcher();
        this.sessionManager = new BukkitSessionManager();
        this.configuration = new BukkitConfigurationManager(WorldGuardPlugin.inst());
        this.configuration.load();
        this.regionContainer = new BukkitRegionContainer(WorldGuardPlugin.inst());
        this.regionContainer.initialize();
        this.debugHandler = new BukkitDebugHandler(WorldGuardPlugin.inst());
    }

    @Override
    public void unload() {
        this.sessionManager.shutdown();
        this.configuration.unload();
        this.regionContainer.shutdown();
    }

    @Override
    public RegionContainer getRegionContainer() {
        return this.regionContainer;
    }

    @Override
    public DebugHandler getDebugHandler() {
        return this.debugHandler;
    }

    @Override
    public GameMode getDefaultGameMode() {
        return GameModes.get((String)Bukkit.getServer().getDefaultGameMode().name().toLowerCase());
    }

    @Override
    public Path getConfigDir() {
        return WorldGuardPlugin.inst().getDataFolder().toPath();
    }

    @Override
    public void stackPlayerInventory(LocalPlayer localPlayer) {
        boolean ignoreMax = localPlayer.hasPermission("worldguard.stack.illegitimate");
        Player player = ((BukkitPlayer)localPlayer).getPlayer();
        ItemStack[] items = player.getInventory().getContents();
        int len = items.length;
        int affected = 0;
        block0: for (int i = 0; i < len; ++i) {
            int max;
            ItemStack item = items[i];
            if (item == null || item.getAmount() <= 0 || !ignoreMax && item.getMaxStackSize() == 1) continue;
            int n = max = ignoreMax ? 64 : item.getMaxStackSize();
            if (item.getAmount() >= max) continue;
            int needed = max - item.getAmount();
            for (int j = i + 1; j < len; ++j) {
                ItemStack item2 = items[j];
                if (item2 == null || item2.getAmount() <= 0 || !ignoreMax && item.getMaxStackSize() == 1 || !item2.isSimilar(item)) continue;
                if (item2.getAmount() > needed) {
                    item.setAmount(max);
                    item2.setAmount(item2.getAmount() - needed);
                    continue block0;
                }
                items[j] = null;
                item.setAmount(item.getAmount() + item2.getAmount());
                needed = max - item.getAmount();
                ++affected;
            }
        }
        if (affected > 0) {
            player.getInventory().setContents(items);
        }
    }

    @Override
    public void addPlatformReports(ReportList report) {
        report.add((Report)new ServerReport());
        report.add((Report)new PluginReport());
        report.add((Report)new SchedulerReport());
        report.add((Report)new ServicesReport());
        report.add((Report)new WorldReport());
        report.add((Report)new PerformanceReport());
        if (PaperLib.isPaper()) {
            report.add((Report)new DatapackReport());
        }
    }

    @Override
    public ProfileService createProfileService(ProfileCache profileCache) {
        ArrayList<ProfileService> services = new ArrayList<ProfileService>();
        if (PaperLib.isPaper()) {
            services.add(PaperPlayerService.getInstance());
        } else {
            services.add(BukkitPlayerService.getInstance());
        }
        services.add(HttpRepositoryService.forMinecraft());
        return new CacheForwardingService(new CombinedProfileService(services), profileCache);
    }

    @Override
    @Nullable
    public ProtectedRegion getSpawnProtection(World world) {
        int radius;
        org.bukkit.World bWorld;
        if (world instanceof BukkitWorld && (bWorld = ((BukkitWorld)world).getWorld()).getUID().equals(((org.bukkit.World)Bukkit.getServer().getWorlds().get(0)).getUID()) && (radius = Bukkit.getServer().getSpawnRadius()) > 0) {
            BlockVector3 spawnLoc = BukkitAdapter.asBlockVector((Location)bWorld.getSpawnLocation());
            return new ProtectedCuboidRegion("__spawn_protection__", spawnLoc.subtract(radius, 0, radius).withY(world.getMinimumPoint().y()), spawnLoc.add(radius, 0, radius).withY(world.getMaxY()));
        }
        return null;
    }
}

