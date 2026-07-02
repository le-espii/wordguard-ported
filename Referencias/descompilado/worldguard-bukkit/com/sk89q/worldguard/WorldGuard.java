/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.util.concurrent.ListeningExecutorService
 *  com.google.common.util.concurrent.MoreExecutors
 *  com.sk89q.minecraft.util.commands.CommandException
 *  com.sk89q.worldedit.extension.platform.Actor
 *  com.sk89q.worldedit.util.task.SimpleSupervisor
 *  com.sk89q.worldedit.util.task.Supervisor
 *  com.sk89q.worldedit.util.task.Task
 */
package com.sk89q.worldguard;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.util.task.SimpleSupervisor;
import com.sk89q.worldedit.util.task.Supervisor;
import com.sk89q.worldedit.util.task.Task;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.flags.registry.SimpleFlagRegistry;
import com.sk89q.worldguard.util.WorldGuardExceptionConverter;
import com.sk89q.worldguard.util.concurrent.EvenMoreExecutors;
import com.sk89q.worldguard.util.profile.cache.HashMapCache;
import com.sk89q.worldguard.util.profile.cache.ProfileCache;
import com.sk89q.worldguard.util.profile.cache.SQLiteCache;
import com.sk89q.worldguard.util.profile.resolver.ProfileService;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class WorldGuard {
    public static final Logger logger = Logger.getLogger(WorldGuard.class.getCanonicalName());
    private static String version;
    private static final WorldGuard instance;
    private WorldGuardPlatform platform;
    private final SimpleFlagRegistry flagRegistry = new SimpleFlagRegistry();
    private final Supervisor supervisor = new SimpleSupervisor();
    private ProfileCache profileCache;
    private ProfileService profileService;
    private ListeningExecutorService executorService;
    private WorldGuardExceptionConverter exceptionConverter = new WorldGuardExceptionConverter();

    public static WorldGuard getInstance() {
        return instance;
    }

    private WorldGuard() {
    }

    public void setup() {
        this.executorService = MoreExecutors.listeningDecorator((ExecutorService)EvenMoreExecutors.newBoundedCachedThreadPool(0, 1, 20, "WorldGuard Task Executor - %s"));
        File cacheDir = new File(this.getPlatform().getConfigDir().toFile(), "cache");
        cacheDir.mkdirs();
        try {
            this.profileCache = new SQLiteCache(new File(cacheDir, "profiles.sqlite"));
        }
        catch (IOException | UnsatisfiedLinkError ignored) {
            logger.log(Level.WARNING, "Failed to initialize SQLite profile cache. Cache is memory-only.");
            this.profileCache = new HashMapCache();
        }
        this.profileService = this.getPlatform().createProfileService(this.profileCache);
        this.getPlatform().load();
    }

    public WorldGuardPlatform getPlatform() {
        Preconditions.checkNotNull((Object)this.platform, (Object)"WorldGuard is not enabled, unable to access the platform.");
        return this.platform;
    }

    public void setPlatform(WorldGuardPlatform platform) {
        Preconditions.checkNotNull((Object)platform);
        this.platform = platform;
    }

    public FlagRegistry getFlagRegistry() {
        return this.flagRegistry;
    }

    public Supervisor getSupervisor() {
        return this.supervisor;
    }

    public ListeningExecutorService getExecutorService() {
        return this.executorService;
    }

    public ProfileService getProfileService() {
        return this.profileService;
    }

    public ProfileCache getProfileCache() {
        return this.profileCache;
    }

    public WorldGuardExceptionConverter getExceptionConverter() {
        return this.exceptionConverter;
    }

    public LocalPlayer checkPlayer(Actor sender) throws CommandException {
        if (sender instanceof LocalPlayer) {
            return (LocalPlayer)sender;
        }
        throw new CommandException("A player is expected.");
    }

    public void disable() {
        this.executorService.shutdown();
        try {
            logger.log(Level.INFO, "Shutting down executor and cancelling any pending tasks...");
            List tasks = this.supervisor.getTasks();
            if (!tasks.isEmpty()) {
                StringBuilder builder = new StringBuilder("Known tasks:");
                for (Task task : tasks) {
                    builder.append("\n");
                    builder.append(task.getName());
                    task.cancel(true);
                }
                logger.log(Level.INFO, builder.toString());
            }
            this.executorService.awaitTermination(5L, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        this.platform.unload();
    }

    public static String getVersion() {
        if (version != null) {
            return version;
        }
        Package p = WorldGuard.class.getPackage();
        if (p == null) {
            p = Package.getPackage("com.sk89q.worldguard");
        }
        if (p == null) {
            version = "(unknown)";
        } else {
            version = p.getImplementationVersion();
            if (version == null) {
                version = "(unknown)";
            }
        }
        return version;
    }

    static {
        instance = new WorldGuard();
        Flags.registerAll();
    }
}

