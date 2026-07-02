/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.Logger
 */
package com.sk89q.worldedit.extension.platform;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.command.tool.BlockTool;
import com.sk89q.worldedit.command.tool.DoubleActionBlockTool;
import com.sk89q.worldedit.command.tool.DoubleActionTraceTool;
import com.sk89q.worldedit.command.tool.Tool;
import com.sk89q.worldedit.command.tool.TraceTool;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.event.platform.BlockInteractEvent;
import com.sk89q.worldedit.event.platform.ConfigurationLoadEvent;
import com.sk89q.worldedit.event.platform.Interaction;
import com.sk89q.worldedit.event.platform.PlatformInitializeEvent;
import com.sk89q.worldedit.event.platform.PlatformReadyEvent;
import com.sk89q.worldedit.event.platform.PlatformUnreadyEvent;
import com.sk89q.worldedit.event.platform.PlatformsRegisteredEvent;
import com.sk89q.worldedit.event.platform.PlayerInputEvent;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.extension.platform.NoCapablePlatformException;
import com.sk89q.worldedit.extension.platform.Platform;
import com.sk89q.worldedit.extension.platform.PlatformCommandManager;
import com.sk89q.worldedit.extension.platform.PlayerProxy;
import com.sk89q.worldedit.extension.platform.Preference;
import com.sk89q.worldedit.internal.util.LogManagerCompat;
import com.sk89q.worldedit.session.request.Request;
import com.sk89q.worldedit.util.HandSide;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.SideEffect;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import com.sk89q.worldedit.world.World;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nullable;
import org.apache.logging.log4j.Logger;

public class PlatformManager {
    private static final Logger LOGGER = LogManagerCompat.getLogger();
    private final WorldEdit worldEdit;
    private final PlatformCommandManager platformCommandManager;
    private final List<Platform> platforms = new ArrayList<Platform>();
    private final Map<Capability, Platform> preferences = new EnumMap<Capability, Platform>(Capability.class);
    @Nullable
    private String firstSeenVersion;
    private final AtomicBoolean initialized = new AtomicBoolean();
    private final AtomicBoolean configured = new AtomicBoolean();

    public PlatformManager(WorldEdit worldEdit) {
        Preconditions.checkNotNull((Object)worldEdit);
        this.worldEdit = worldEdit;
        this.platformCommandManager = new PlatformCommandManager(worldEdit, this);
        worldEdit.getEventBus().register(this);
    }

    public synchronized void register(Platform platform) {
        Preconditions.checkNotNull((Object)platform);
        LOGGER.info("Got request to register " + String.valueOf(platform.getClass()) + " with WorldEdit [" + super.toString() + "]");
        this.platforms.add(platform);
        if (this.firstSeenVersion != null) {
            if (!this.firstSeenVersion.equals(platform.getVersion())) {
                LOGGER.warn("Multiple ports of WorldEdit are installed but they report different versions ({} and {}). If these two versions are truly different, then you may run into unexpected crashes and errors.", (Object)this.firstSeenVersion, (Object)platform.getVersion());
            }
        } else {
            this.firstSeenVersion = platform.getVersion();
        }
    }

    public synchronized boolean unregister(Platform platform) {
        Preconditions.checkNotNull((Object)platform);
        boolean removed = this.platforms.remove(platform);
        if (removed) {
            LOGGER.info("Unregistering " + platform.getClass().getCanonicalName() + " from WorldEdit");
            boolean choosePreferred = false;
            Iterator<Map.Entry<Capability, Platform>> it = this.preferences.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Capability, Platform> entry = it.next();
                if (!entry.getValue().equals(platform)) continue;
                entry.getKey().uninitialize(this, entry.getValue());
                it.remove();
                choosePreferred = true;
            }
            if (choosePreferred) {
                this.choosePreferred();
            }
        }
        return removed;
    }

    public synchronized Platform queryCapability(Capability capability) throws NoCapablePlatformException {
        Platform platform = this.preferences.get(Preconditions.checkNotNull((Object)((Object)capability)));
        if (platform != null) {
            return platform;
        }
        if (this.preferences.isEmpty()) {
            throw new NoCapablePlatformException("Not all platforms have been registered yet! Please wait until WorldEdit is initialized.");
        }
        throw new NoCapablePlatformException("No platform was found supporting " + capability.name());
    }

    private synchronized void choosePreferred() {
        for (Capability capability : Capability.values()) {
            Platform oldPreferred;
            Platform preferred = this.findMostPreferred(capability);
            if (preferred == null || preferred == (oldPreferred = this.preferences.put(capability, preferred))) continue;
            if (oldPreferred != null) {
                capability.uninitialize(this, oldPreferred);
            }
            capability.initialize(this, preferred);
        }
        if (this.preferences.containsKey((Object)Capability.CONFIGURATION) && this.configured.compareAndSet(false, true)) {
            this.worldEdit.getEventBus().post(new ConfigurationLoadEvent(this.queryCapability(Capability.CONFIGURATION).getConfiguration()));
        }
    }

    @Nullable
    private synchronized Platform findMostPreferred(Capability capability) {
        Platform preferred = null;
        Preference highest = null;
        for (Platform platform : this.platforms) {
            Preference preference = platform.getCapabilities().get((Object)capability);
            if (preference == null || highest != null && !preference.isPreferredOver(highest)) continue;
            preferred = platform;
            highest = preference;
        }
        return preferred;
    }

    public synchronized List<Platform> getPlatforms() {
        return new ArrayList<Platform>(this.platforms);
    }

    public World getWorldForEditing(World base) {
        Preconditions.checkNotNull((Object)base);
        World match = this.queryCapability(Capability.WORLD_EDITING).matchWorld(base);
        return match != null ? match : base;
    }

    public <T extends Actor> T createProxyActor(T base) {
        Preconditions.checkNotNull(base);
        if (base instanceof Player) {
            Player cuiActor;
            Player player = (Player)base;
            Player permActor = this.queryCapability(Capability.PERMISSIONS).matchPlayer(player);
            if (permActor == null) {
                permActor = player;
            }
            if ((cuiActor = this.queryCapability(Capability.WORLDEDIT_CUI).matchPlayer(player)) == null) {
                cuiActor = player;
            }
            return (T)new PlayerProxy(player, permActor, cuiActor, this.getWorldForEditing(player.getWorld()));
        }
        return base;
    }

    public PlatformCommandManager getPlatformCommandManager() {
        return this.platformCommandManager;
    }

    public LocalConfiguration getConfiguration() {
        return this.queryCapability(Capability.CONFIGURATION).getConfiguration();
    }

    public Collection<SideEffect> getSupportedSideEffects() {
        return this.queryCapability(Capability.WORLD_EDITING).getSupportedSideEffects();
    }

    public boolean isInitialized() {
        return this.initialized.get();
    }

    @Deprecated
    public void handlePlatformReady(PlatformReadyEvent event) {
        this.handlePlatformsRegistered(new PlatformsRegisteredEvent());
    }

    @Subscribe
    public void handlePlatformsRegistered(PlatformsRegisteredEvent event) {
        this.choosePreferred();
        if (this.initialized.compareAndSet(false, true)) {
            this.worldEdit.getEventBus().post(new PlatformInitializeEvent());
        }
    }

    @Subscribe
    public void handleNewPlatformReady(PlatformReadyEvent event) {
        this.preferences.forEach((cap, platform) -> cap.ready(this, (Platform)platform));
    }

    @Subscribe
    public void handleNewPlatformUnready(PlatformUnreadyEvent event) {
        this.preferences.forEach((cap, platform) -> cap.unready(this, (Platform)platform));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Subscribe
    public void handleBlockInteract(BlockInteractEvent event) {
        Actor actor = this.createProxyActor(event.getCause());
        Location location = event.getLocation();
        if (!(actor instanceof Player)) {
            return;
        }
        Player player = (Player)actor;
        LocalSession session = this.worldEdit.getSessionManager().get(actor);
        Request.reset();
        Request.request().setSession(session);
        Request.request().setWorld(player.getWorld());
        try {
            Tool tool;
            if (event.getType() == Interaction.HIT) {
                BlockTool superPickaxe;
                if (session.hasSuperPickAxe() && player.isHoldingPickAxe() && (superPickaxe = session.getSuperPickaxe()) != null && superPickaxe.canUse(player)) {
                    if (superPickaxe.actPrimary(this.queryCapability(Capability.WORLD_EDITING), this.getConfiguration(), player, session, location, event.getFace())) {
                        event.setCancelled(true);
                    }
                    return;
                }
                Tool tool2 = session.getTool(player.getItemInHand(HandSide.MAIN_HAND).getType());
                if (tool2 instanceof DoubleActionBlockTool && tool2.canUse(player) && ((DoubleActionBlockTool)tool2).actSecondary(this.queryCapability(Capability.WORLD_EDITING), this.getConfiguration(), player, session, location, event.getFace())) {
                    event.setCancelled(true);
                }
            } else if (event.getType() == Interaction.OPEN && (tool = session.getTool(player.getItemInHand(HandSide.MAIN_HAND).getType())) instanceof BlockTool && tool.canUse(player) && ((BlockTool)tool).actPrimary(this.queryCapability(Capability.WORLD_EDITING), this.getConfiguration(), player, session, location, event.getFace())) {
                event.setCancelled(true);
            }
        }
        finally {
            Request.reset();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Subscribe
    public void handlePlayerInput(PlayerInputEvent event) {
        Player player = this.createProxyActor(event.getPlayer());
        LocalSession session = this.worldEdit.getSessionManager().get(player);
        Request.reset();
        Request.request().setSession(session);
        Request.request().setWorld(player.getWorld());
        try {
            switch (event.getInputType()) {
                case PRIMARY: {
                    Tool tool = session.getTool(player.getItemInHand(HandSide.MAIN_HAND).getType());
                    if (!(tool instanceof DoubleActionTraceTool)) return;
                    if (!tool.canUse(player)) return;
                    if (!((DoubleActionTraceTool)tool).actSecondary(this.queryCapability(Capability.WORLD_EDITING), this.getConfiguration(), player, session)) return;
                    event.setCancelled(true);
                    return;
                }
                case SECONDARY: {
                    Tool tool = session.getTool(player.getItemInHand(HandSide.MAIN_HAND).getType());
                    if (!(tool instanceof TraceTool)) return;
                    if (!tool.canUse(player)) return;
                    if (!((TraceTool)tool).actPrimary(this.queryCapability(Capability.WORLD_EDITING), this.getConfiguration(), player, session)) return;
                    event.setCancelled(true);
                    return;
                }
            }
            return;
        }
        finally {
            Request.reset();
        }
    }
}

