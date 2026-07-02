/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.util.concurrent.Futures
 *  com.google.common.util.concurrent.ListenableFuture
 *  com.google.common.util.concurrent.ListeningExecutorService
 *  com.google.common.util.concurrent.MoreExecutors
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.Logger
 */
package com.sk89q.worldedit.session;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.command.tool.InvalidToolBindException;
import com.sk89q.worldedit.command.tool.NavigationWand;
import com.sk89q.worldedit.command.tool.SelectionWand;
import com.sk89q.worldedit.command.tool.Tool;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.event.platform.ConfigurationLoadEvent;
import com.sk89q.worldedit.event.platform.SessionIdleEvent;
import com.sk89q.worldedit.extension.platform.Locatable;
import com.sk89q.worldedit.internal.util.LogManagerCompat;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.Placement;
import com.sk89q.worldedit.session.PlacementType;
import com.sk89q.worldedit.session.SessionKey;
import com.sk89q.worldedit.session.SessionOwner;
import com.sk89q.worldedit.session.request.Request;
import com.sk89q.worldedit.session.storage.JsonFileSessionStore;
import com.sk89q.worldedit.session.storage.SessionStore;
import com.sk89q.worldedit.session.storage.VoidStore;
import com.sk89q.worldedit.util.concurrency.EvenMoreExecutors;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import com.sk89q.worldedit.world.gamemode.GameModes;
import com.sk89q.worldedit.world.item.ItemType;
import com.sk89q.worldedit.world.item.ItemTypes;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import javax.annotation.Nullable;
import org.apache.logging.log4j.Logger;

public class SessionManager {
    public static int EXPIRATION_GRACE = 600000;
    private static final int FLUSH_PERIOD = 30000;
    private static final ListeningExecutorService executorService = MoreExecutors.listeningDecorator((ExecutorService)EvenMoreExecutors.newBoundedCachedThreadPool(0, 1, 5, "WorldEdit Session Saver - %s"));
    private static final Logger LOGGER = LogManagerCompat.getLogger();
    private static final Set<String> warnedInvalidTool = Collections.newSetFromMap(new ConcurrentHashMap());
    private final Timer timer = new Timer("WorldEdit Session Manager");
    private final WorldEdit worldEdit;
    private final Map<UUID, SessionHolder> sessions = new HashMap<UUID, SessionHolder>();
    private SessionStore store = new VoidStore();

    public SessionManager(WorldEdit worldEdit) {
        Preconditions.checkNotNull((Object)worldEdit);
        this.worldEdit = worldEdit;
        worldEdit.getEventBus().register(this);
        this.timer.schedule((TimerTask)new SessionTracker(), 30000L, 30000L);
    }

    public synchronized boolean contains(SessionOwner owner) {
        Preconditions.checkNotNull((Object)owner);
        return this.sessions.containsKey(this.getKey(owner));
    }

    @Nullable
    public synchronized LocalSession findByName(String name) {
        Preconditions.checkNotNull((Object)name);
        for (SessionHolder holder : this.sessions.values()) {
            String test = holder.key.getName();
            if (!name.equals(test)) continue;
            return holder.session;
        }
        return null;
    }

    @Nullable
    public synchronized LocalSession getIfPresent(SessionOwner owner) {
        Preconditions.checkNotNull((Object)owner);
        SessionHolder stored = this.sessions.get(this.getKey(owner));
        if (stored != null) {
            if (stored.sessionIdle && stored.key.isActive()) {
                stored.sessionIdle = false;
            }
            return stored.session;
        }
        return null;
    }

    public synchronized LocalSession get(SessionOwner owner) {
        Preconditions.checkNotNull((Object)owner);
        LocalSession session = this.getIfPresent(owner);
        LocalConfiguration config = this.worldEdit.getConfiguration();
        SessionKey sessionKey = owner.getSessionKey();
        if (session == null) {
            block11: {
                String sessionItem;
                block10: {
                    try {
                        session = this.store.load(this.getKey(sessionKey));
                        session.postLoad();
                    }
                    catch (IOException e) {
                        LOGGER.warn("Failed to load saved session", (Throwable)e);
                        session = new LocalSession();
                    }
                    Request.request().setSession(session);
                    session.setConfiguration(config);
                    session.setBlockChangeLimit(config.defaultChangeLimit);
                    session.setTimeout(config.calculationTimeout);
                    try {
                        sessionItem = session.isWandItemDefault() ? null : session.getWandItem();
                        this.setDefaultWand(sessionItem, config.wandItem, session, new SelectionWand());
                    }
                    catch (InvalidToolBindException e) {
                        if (!warnedInvalidTool.add("selwand")) break block10;
                        LOGGER.warn("Invalid selection wand tool set in config. Tool will not be assigned: " + String.valueOf(e.getItemType()));
                    }
                }
                try {
                    sessionItem = session.isNavWandItemDefault() ? null : session.getNavWandItem();
                    this.setDefaultWand(sessionItem, config.navigationWand, session, new NavigationWand());
                }
                catch (InvalidToolBindException e) {
                    if (!warnedInvalidTool.add("navwand")) break block11;
                    LOGGER.warn("Invalid navigation wand tool set in config. Tool will not be assigned: " + String.valueOf(e.getItemType()));
                }
            }
            session.compareAndResetDirty();
            this.sessions.put(this.getKey(owner), new SessionHolder(sessionKey, session));
        }
        if (this.shouldBoundLimit(owner, "worldedit.limit.unrestricted", session.getBlockChangeLimit(), config.maxChangeLimit)) {
            session.setBlockChangeLimit(config.maxChangeLimit);
        }
        if (this.shouldBoundLimit(owner, "worldedit.timeout.unrestricted", session.getTimeout(), config.maxCalculationTimeout)) {
            session.setTimeout(config.maxCalculationTimeout);
        }
        session.setUseInventory(config.useInventory && (!config.useInventoryOverride || !owner.hasPermission("worldedit.inventory.unrestricted") && (!config.useInventoryCreativeOverride || owner instanceof Player && ((Player)owner).getGameMode() != GameModes.CREATIVE)));
        if (!(owner instanceof Locatable)) {
            session.setPlacement(new Placement(PlacementType.POS1, BlockVector3.ZERO));
        }
        return session;
    }

    private boolean shouldBoundLimit(SessionOwner owner, String permission, int currentLimit, int maxLimit) {
        if (maxLimit > -1) {
            return (currentLimit < 0 || currentLimit > maxLimit) && !owner.hasPermission(permission);
        }
        return false;
    }

    private void setDefaultWand(String sessionItem, String configItem, LocalSession session, Tool wand) throws InvalidToolBindException {
        ItemType wandItem = null;
        if (sessionItem != null) {
            wandItem = ItemTypes.get(sessionItem);
        }
        if (wandItem == null) {
            wandItem = ItemTypes.get(configItem);
        }
        if (wandItem != null) {
            session.setTool(wandItem, wand);
        }
    }

    private ListenableFuture<?> commit(Map<SessionKey, LocalSession> sessions) {
        Preconditions.checkNotNull(sessions);
        if (sessions.isEmpty()) {
            return Futures.immediateFuture(sessions);
        }
        return executorService.submit(() -> {
            IOException exception = null;
            for (Map.Entry entry : sessions.entrySet()) {
                SessionKey key = (SessionKey)entry.getKey();
                if (!key.isPersistent()) continue;
                try {
                    this.store.save(this.getKey(key), (LocalSession)entry.getValue());
                }
                catch (IOException e) {
                    LOGGER.warn("Failed to write session for UUID " + String.valueOf(this.getKey(key)), (Throwable)e);
                    exception = e;
                }
            }
            if (exception != null) {
                throw exception;
            }
            return sessions;
        });
    }

    protected UUID getKey(SessionOwner owner) {
        return this.getKey(owner.getSessionKey());
    }

    protected UUID getKey(SessionKey key) {
        return key.getUniqueId();
    }

    public synchronized void remove(SessionOwner owner) {
        Preconditions.checkNotNull((Object)owner);
        this.sessions.remove(this.getKey(owner));
    }

    public synchronized void unload() {
        this.clear();
        this.timer.cancel();
    }

    public synchronized void clear() {
        this.saveChangedSessions();
        this.sessions.clear();
    }

    private synchronized void saveChangedSessions() {
        long now = System.currentTimeMillis();
        Iterator<SessionHolder> it = this.sessions.values().iterator();
        HashMap<SessionKey, LocalSession> saveQueue = new HashMap<SessionKey, LocalSession>();
        while (it.hasNext()) {
            SessionHolder stored = it.next();
            if (stored.key.isActive()) {
                stored.lastActive = now;
                if (!stored.session.compareAndResetDirty()) continue;
                saveQueue.put(stored.key, stored.session);
                continue;
            }
            if (now - stored.lastActive <= (long)EXPIRATION_GRACE) continue;
            if (stored.session.compareAndResetDirty()) {
                saveQueue.put(stored.key, stored.session);
            }
            it.remove();
        }
        if (!saveQueue.isEmpty()) {
            this.commit(saveQueue);
        }
    }

    @Subscribe
    public void onConfigurationLoad(ConfigurationLoadEvent event) {
        LocalConfiguration config = event.getConfiguration();
        Path dir = config.getWorkingDirectoryPath().resolve("sessions");
        this.store = new JsonFileSessionStore(dir);
    }

    @Subscribe
    public void onSessionIdle(SessionIdleEvent event) {
        SessionHolder holder = this.sessions.get(this.getKey(event.getKey()));
        if (holder != null && !holder.sessionIdle) {
            holder.sessionIdle = true;
            LocalSession session = holder.session;
            session.onIdle();
        }
    }

    private class SessionTracker
    extends TimerTask {
        private SessionTracker() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            SessionManager sessionManager = SessionManager.this;
            synchronized (sessionManager) {
                SessionManager.this.saveChangedSessions();
            }
        }
    }

    private static final class SessionHolder {
        private final SessionKey key;
        private final LocalSession session;
        private long lastActive = System.currentTimeMillis();
        private boolean sessionIdle = false;

        private SessionHolder(SessionKey key, LocalSession session) {
            this.key = key;
            this.session = session;
        }
    }
}

