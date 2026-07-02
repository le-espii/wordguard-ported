/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.util.formatting.text.TextComponent
 *  com.sk89q.worldedit.util.report.ReportList
 *  com.sk89q.worldedit.world.World
 *  com.sk89q.worldedit.world.gamemode.GameMode
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.internal.platform;

import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.report.ReportList;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.gamemode.GameMode;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.config.ConfigurationManager;
import com.sk89q.worldguard.internal.platform.DebugHandler;
import com.sk89q.worldguard.internal.platform.StringMatcher;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.session.SessionManager;
import com.sk89q.worldguard.util.profile.cache.ProfileCache;
import com.sk89q.worldguard.util.profile.resolver.ProfileService;
import java.nio.file.Path;
import javax.annotation.Nullable;

public interface WorldGuardPlatform {
    public String getPlatformName();

    public String getPlatformVersion();

    public void notifyFlagContextCreate(FlagContext.FlagContextBuilder var1);

    public ConfigurationManager getGlobalStateManager();

    public StringMatcher getMatcher();

    public SessionManager getSessionManager();

    public void broadcastNotification(String var1);

    public void broadcastNotification(TextComponent var1);

    public void load();

    public void unload();

    public RegionContainer getRegionContainer();

    public DebugHandler getDebugHandler();

    public GameMode getDefaultGameMode();

    public Path getConfigDir();

    public void stackPlayerInventory(LocalPlayer var1);

    public void addPlatformReports(ReportList var1);

    public ProfileService createProfileService(ProfileCache var1);

    @Nullable
    default public ProtectedRegion getSpawnProtection(World world) {
        return null;
    }
}

