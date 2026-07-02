/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Sets$SetView
 *  com.sk89q.worldedit.util.Location
 *  com.sk89q.worldedit.util.formatting.text.Component
 *  com.sk89q.worldedit.util.formatting.text.TextComponent
 *  com.sk89q.worldedit.util.formatting.text.format.TextColor
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.session;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.config.ConfigurationManager;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.SessionManager;
import com.sk89q.worldguard.session.handler.Handler;
import com.sk89q.worldguard.util.Locations;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;

public class Session {
    private final SessionManager manager;
    private boolean disableBypass;
    private final HashMap<Class<?>, Handler> handlers = Maps.newLinkedHashMap();
    private Location lastValid;
    private Set<ProtectedRegion> lastRegionSet;
    private final AtomicBoolean needRefresh = new AtomicBoolean(false);
    private boolean initialized;

    public Session(SessionManager manager) {
        Preconditions.checkNotNull((Object)manager, (Object)"manager");
        this.manager = manager;
    }

    public void register(Handler handler) {
        this.handlers.put(handler.getClass(), handler);
    }

    public SessionManager getManager() {
        return this.manager;
    }

    @Nullable
    public <T extends Handler> T getHandler(Class<T> type) {
        return (T)this.handlers.get(type);
    }

    public void initialize(LocalPlayer player) {
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        Location location = player.getLocation();
        ApplicableRegionSet set = query.getApplicableRegions(location);
        this.lastValid = location;
        this.lastRegionSet = set.getRegions();
        ConfigurationManager cfg = WorldGuard.getInstance().getPlatform().getGlobalStateManager();
        this.disableBypass = cfg.disableDefaultBypass;
        if (cfg.announceBypassStatus && player.hasPermission("worldguard.region.toggle-bypass")) {
            player.printInfo((Component)TextComponent.of((String)("You are " + (this.disableBypass ? "not " : "") + "bypassing region protection. You can toggle this with /rg bypass"), (TextColor)TextColor.DARK_PURPLE));
        }
        for (Handler handler : this.handlers.values()) {
            handler.initialize(player, location, set);
        }
    }

    synchronized void ensureInitialized(LocalPlayer player, BiConsumer<Session, LocalPlayer> initializer) {
        if (this.initialized) {
            return;
        }
        this.initialized = true;
        initializer.accept(this, player);
    }

    public void uninitialize(LocalPlayer player) {
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        Location location = player.getLocation();
        ApplicableRegionSet set = query.getApplicableRegions(location);
        for (Handler handler : this.handlers.values()) {
            handler.uninitialize(player, location, set);
        }
    }

    public void tick(LocalPlayer player) {
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        Location location = player.getLocation();
        ApplicableRegionSet set = query.getApplicableRegions(location);
        for (Handler handler : this.handlers.values()) {
            handler.tick(player, set);
        }
    }

    public void resetState(LocalPlayer player) {
        this.initialize(player);
        this.needRefresh.set(true);
    }

    public boolean isInvincible(LocalPlayer player) {
        boolean invincible = false;
        for (Handler handler : this.handlers.values()) {
            StateFlag.State state = handler.getInvincibility(player);
            if (state == null) continue;
            switch (state) {
                case DENY: {
                    return false;
                }
                case ALLOW: {
                    invincible = true;
                }
            }
        }
        return invincible;
    }

    @Nullable
    public Location testMoveTo(LocalPlayer player, Location to, MoveType moveType) {
        return this.testMoveTo(player, to, moveType, false);
    }

    @Nullable
    public Location testMoveTo(LocalPlayer player, Location to, MoveType moveType, boolean forced) {
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        if (!forced && this.needRefresh.getAndSet(false)) {
            forced = true;
        }
        if (forced || Locations.isDifferentBlock(this.lastValid, to)) {
            ApplicableRegionSet toSet = query.getApplicableRegions(to);
            for (Handler handler : this.handlers.values()) {
                if (handler.testMoveTo(player, this.lastValid, to, toSet, moveType) || !moveType.isCancellable()) continue;
                return this.lastValid;
            }
            Sets.SetView entered = Sets.difference(toSet.getRegions(), this.lastRegionSet);
            Sets.SetView exited = Sets.difference(this.lastRegionSet, toSet.getRegions());
            for (Handler handler : this.handlers.values()) {
                if (handler.onCrossBoundary(player, this.lastValid, to, toSet, (Set<ProtectedRegion>)entered, (Set<ProtectedRegion>)exited, moveType) || !moveType.isCancellable()) continue;
                return this.lastValid;
            }
            this.lastValid = to;
            this.lastRegionSet = toSet.getRegions();
        }
        return null;
    }

    public boolean hasBypassDisabled() {
        return this.disableBypass;
    }

    public void setBypassDisabled(boolean disabled) {
        this.disableBypass = disabled;
    }
}

