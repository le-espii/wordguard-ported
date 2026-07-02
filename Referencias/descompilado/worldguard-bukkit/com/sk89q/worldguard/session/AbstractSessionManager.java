/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.cache.Cache
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.sk89q.worldedit.world.World
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.session;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.SessionManager;
import com.sk89q.worldguard.session.WorldPlayerTuple;
import com.sk89q.worldguard.session.handler.EntryFlag;
import com.sk89q.worldguard.session.handler.ExitFlag;
import com.sk89q.worldguard.session.handler.FarewellFlag;
import com.sk89q.worldguard.session.handler.FeedFlag;
import com.sk89q.worldguard.session.handler.GameModeFlag;
import com.sk89q.worldguard.session.handler.GodMode;
import com.sk89q.worldguard.session.handler.GreetingFlag;
import com.sk89q.worldguard.session.handler.Handler;
import com.sk89q.worldguard.session.handler.HealFlag;
import com.sk89q.worldguard.session.handler.InvincibilityFlag;
import com.sk89q.worldguard.session.handler.NotifyEntryFlag;
import com.sk89q.worldguard.session.handler.NotifyExitFlag;
import com.sk89q.worldguard.session.handler.TimeLockFlag;
import com.sk89q.worldguard.session.handler.WaterBreathing;
import com.sk89q.worldguard.session.handler.WeatherLockFlag;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.BiPredicate;
import java.util.logging.Level;
import javax.annotation.Nullable;

public abstract class AbstractSessionManager
implements SessionManager {
    public static final int RUN_DELAY = 20;
    public static final long SESSION_LIFETIME = 10L;
    private static final BiPredicate<World, LocalPlayer> BYPASS_PERMISSION_TEST = (world, player) -> player.hasPermission("worldguard.region.bypass." + world.getName());
    private final LoadingCache<WorldPlayerTuple, Boolean> bypassCache = CacheBuilder.newBuilder().maximumSize(1000L).expireAfterWrite(2L, TimeUnit.SECONDS).build(CacheLoader.from(tuple -> BYPASS_PERMISSION_TEST.test(tuple.getWorld(), tuple.getPlayer())));
    private final Cache<CacheKey, Session> sessions = CacheBuilder.newBuilder().expireAfterAccess(10L, TimeUnit.MINUTES).build();
    private boolean hasCustom = false;
    private List<Handler.Factory<? extends Handler>> handlers = new LinkedList<Handler.Factory<? extends Handler>>();
    private static final List<Handler.Factory<? extends Handler>> defaultHandlers = new LinkedList<Handler.Factory<? extends Handler>>();

    protected AbstractSessionManager() {
        this.handlers.addAll(defaultHandlers);
    }

    @Override
    public boolean customHandlersRegistered() {
        return this.hasCustom;
    }

    @Override
    public boolean registerHandler(Handler.Factory<? extends Handler> factory, @Nullable Handler.Factory<? extends Handler> after) {
        if (factory == null) {
            return false;
        }
        WorldGuard.logger.log(Level.INFO, "Registering session handler " + factory.getClass().getEnclosingClass().getName());
        this.hasCustom = true;
        if (after == null) {
            this.handlers.add(factory);
        } else {
            int index = this.handlers.indexOf(after);
            if (index == -1) {
                return false;
            }
            this.handlers.add(index + 1, factory);
        }
        return true;
    }

    @Override
    public boolean unregisterHandler(Handler.Factory<? extends Handler> factory) {
        if (defaultHandlers.contains(factory)) {
            WorldGuard.logger.log(Level.WARNING, "Someone is unregistering a default WorldGuard handler: " + factory.getClass().getEnclosingClass().getName() + ". This may cause parts of WorldGuard to stop functioning");
        } else {
            WorldGuard.logger.log(Level.INFO, "Unregistering session handler " + factory.getClass().getEnclosingClass().getName());
        }
        return this.handlers.remove(factory);
    }

    @Override
    public boolean hasBypass(LocalPlayer player, World world) {
        Session sess = this.getIfPresent(player);
        if (sess == null || sess.hasBypassDisabled()) {
            return false;
        }
        if (WorldGuard.getInstance().getPlatform().getGlobalStateManager().disablePermissionCache) {
            return BYPASS_PERMISSION_TEST.test(world, player);
        }
        return (Boolean)this.bypassCache.getUnchecked((Object)new WorldPlayerTuple(world, player));
    }

    @Override
    public void resetState(LocalPlayer player) {
        Preconditions.checkNotNull((Object)player, (Object)"player");
        Session session = this.getIfPresentInternal(new CacheKey(player));
        if (session != null) {
            session.resetState(player);
        }
    }

    @Override
    @Nullable
    public Session getIfPresent(LocalPlayer player) {
        return this.getIfPresentInternal(new CacheKey(player));
    }

    private Session getIfPresentInternal(CacheKey cacheKey) {
        Session session = (Session)this.sessions.getIfPresent((Object)cacheKey);
        if (session != null) {
            session.ensureInitialized((LocalPlayer)cacheKey.playerRef.get(), this::initializeSession);
            return session;
        }
        return null;
    }

    @Override
    public Session get(LocalPlayer player) {
        Session session = this.sessions.asMap().computeIfAbsent(new CacheKey(player), k -> new Session(this));
        session.ensureInitialized(player, this::initializeSession);
        return session;
    }

    @Override
    public Session createSession(LocalPlayer player) {
        return this.get(player);
    }

    private void initializeSession(Session session, LocalPlayer player) {
        for (Handler.Factory<? extends Handler> factory : this.handlers) {
            session.register(factory.create(session));
        }
        session.initialize(player);
    }

    static {
        Handler.Factory[] factories = new Handler.Factory[]{HealFlag.FACTORY, FeedFlag.FACTORY, NotifyEntryFlag.FACTORY, NotifyExitFlag.FACTORY, EntryFlag.FACTORY, ExitFlag.FACTORY, FarewellFlag.FACTORY, GreetingFlag.FACTORY, GameModeFlag.FACTORY, InvincibilityFlag.FACTORY, TimeLockFlag.FACTORY, WeatherLockFlag.FACTORY, GodMode.FACTORY, WaterBreathing.FACTORY};
        defaultHandlers.addAll(Arrays.asList(factories));
    }

    protected static final class CacheKey {
        final WeakReference<LocalPlayer> playerRef;
        final UUID uuid;

        CacheKey(LocalPlayer player) {
            this.playerRef = new WeakReference<LocalPlayer>(player);
            this.uuid = player.getUniqueId();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            CacheKey cacheKey = (CacheKey)o;
            return this.uuid.equals(cacheKey.uuid);
        }

        public int hashCode() {
            return this.uuid.hashCode();
        }
    }
}

