package com.sk89q.worldguard.mod;

import com.sk89q.worldedit.world.gamemode.GameMode;
import com.sk89q.worldedit.world.gamemode.GameModes;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.config.ConfigurationManager;
import com.sk89q.worldguard.internal.platform.DebugHandler;
import com.sk89q.worldguard.internal.platform.StringMatcher;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.session.SessionManager;
import com.sk89q.worldguard.util.profile.resolver.ProfileService;
import com.sk89q.worldguard.util.profile.cache.ProfileCache;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;

public class ModWorldGuardPlatform implements WorldGuardPlatform {
    private final ModRegionContainer regionContainer;
    private final ModConfigurationManager configurationManager;
    private final ModSessionManager sessionManager;
    private final ModStringMatcher stringMatcher;
    private final ModDebugHandler debugHandler;

    public ModWorldGuardPlatform() {
        this.regionContainer = new ModRegionContainer();
        this.configurationManager = new ModConfigurationManager();
        this.sessionManager = new ModSessionManager(this);
        this.stringMatcher = new ModStringMatcher();
        this.debugHandler = new ModDebugHandler();
    }

    @Override
    public String getPlatformName() {
        return "WorldGuard Mod";
    }

    @Override
    public String getPlatformVersion() {
        return "1.21.1-1.0.5-Espii";
    }

    @Override
    public void notifyFlagContextCreate(FlagContext.FlagContextBuilder builder) {
        // No-op
    }

    @Override
    public ConfigurationManager getGlobalStateManager() {
        return this.configurationManager;
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
    public void broadcastNotification(String msg) {
        if (WorldGuardModCommon.getServer() != null) {
            WorldGuardModCommon.getServer().getPlayerList().getPlayers().forEach(player -> {
                if (player.hasPermissions(2)) { // OP nivel 2
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(msg));
                }
            });
        }
    }

    @Override
    public void broadcastNotification(com.sk89q.worldedit.util.formatting.text.TextComponent msg) {
        broadcastNotification(msg.toString());
    }

    @Override
    public void load() {
        this.configurationManager.load();
        this.regionContainer.initialize();
    }

    @Override
    public void unload() {
        this.regionContainer.unload();
        this.configurationManager.unload();
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
        return GameModes.SURVIVAL;
    }

    @Override
    public Path getConfigDir() {
        return Paths.get("config", "worldguard");
    }

    @Override
    public void stackPlayerInventory(LocalPlayer player) {
        // No-op
    }

    @Override
    public void addPlatformReports(com.sk89q.worldedit.util.report.ReportList reports) {
        // No-op
    }

    @Override
    public ProfileService createProfileService(ProfileCache cache) {
        List<ProfileService> services = new ArrayList<>();
        services.add(new MinecraftProfileService());
        return new com.sk89q.worldguard.util.profile.resolver.CacheForwardingService(
            new com.sk89q.worldguard.util.profile.resolver.CombinedProfileService(services), 
            cache
        );
    }
}
