/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.extension.platform;

import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.extension.platform.Preference;
import com.sk89q.worldedit.extension.platform.Watchdog;
import com.sk89q.worldedit.internal.util.NonAbstractForCompatibility;
import com.sk89q.worldedit.registry.Keyed;
import com.sk89q.worldedit.util.SideEffect;
import com.sk89q.worldedit.util.io.ResourceLoader;
import com.sk89q.worldedit.util.translation.TranslationManager;
import com.sk89q.worldedit.world.DataFixer;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.registry.Registries;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import org.enginehub.piston.CommandManager;

public interface Platform
extends Keyed {
    public ResourceLoader getResourceLoader();

    public TranslationManager getTranslationManager();

    public Registries getRegistries();

    public int getDataVersion();

    public DataFixer getDataFixer();

    public boolean isValidMobType(String var1);

    public void reload();

    public int schedule(long var1, long var3, Runnable var5);

    @Nullable
    default public Watchdog getWatchdog() {
        return null;
    }

    public List<? extends World> getWorlds();

    @Nullable
    public Player matchPlayer(Player var1);

    @Nullable
    public World matchWorld(World var1);

    public void registerCommands(CommandManager var1);

    @Deprecated
    default public void registerGameHooks() {
        this.setGameHooksEnabled(true);
    }

    public void setGameHooksEnabled(boolean var1);

    public LocalConfiguration getConfiguration();

    public String getVersion();

    public String getPlatformName();

    public String getPlatformVersion();

    @Override
    @NonAbstractForCompatibility(delegateName="getPlatformName", delegateParams={})
    default public String id() {
        return "legacy:" + this.getPlatformName().toLowerCase(Locale.ROOT).replaceAll("[^a-z_.-]", "_");
    }

    public Map<Capability, Preference> getCapabilities();

    public Set<SideEffect> getSupportedSideEffects();

    public long getTickCount();
}

