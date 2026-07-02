/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.protection.managers;

import com.google.common.base.Preconditions;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.index.ChunkHashTable;
import com.sk89q.worldguard.protection.managers.index.ConcurrentRegionIndex;
import com.sk89q.worldguard.protection.managers.index.PriorityRTreeIndex;
import com.sk89q.worldguard.protection.managers.storage.RegionDatabase;
import com.sk89q.worldguard.protection.managers.storage.RegionDriver;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.util.Normal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;

public class RegionContainerImpl {
    private static final Logger log = Logger.getLogger(RegionContainerImpl.class.getCanonicalName());
    private static final int LOAD_ATTEMPT_INTERVAL = 30000;
    private static final int SAVE_INTERVAL = 30000;
    private final ConcurrentMap<Normal, RegionManager> mapping = new ConcurrentHashMap<Normal, RegionManager>();
    private final Object lock = new Object();
    private final RegionDriver driver;
    private final Function<String, ? extends ConcurrentRegionIndex> indexFactory = new ChunkHashTable.Factory(new PriorityRTreeIndex.Factory());
    private final Timer timer = new Timer("WorldGuard Region I/O");
    private final FlagRegistry flagRegistry;
    private final Set<Normal> failingLoads = new HashSet<Normal>();
    private final Set<RegionManager> failingSaves = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));

    public RegionContainerImpl(RegionDriver driver, FlagRegistry flagRegistry) {
        Preconditions.checkNotNull((Object)driver);
        Preconditions.checkNotNull((Object)flagRegistry, (Object)"flagRegistry");
        this.driver = driver;
        this.timer.schedule((TimerTask)new BackgroundLoader(), 30000L, 30000L);
        this.timer.schedule((TimerTask)new BackgroundSaver(), 30000L, 30000L);
        this.flagRegistry = flagRegistry;
    }

    public RegionDriver getDriver() {
        return this.driver;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    public RegionManager load(String name) {
        Preconditions.checkNotNull((Object)name);
        Normal normal = Normal.normal(name);
        Object object = this.lock;
        synchronized (object) {
            RegionManager manager = (RegionManager)this.mapping.get(normal);
            if (manager != null) {
                return manager;
            }
            try {
                manager = this.createAndLoad(name);
                this.mapping.put(normal, manager);
                this.failingLoads.remove(normal);
                return manager;
            }
            catch (StorageException e) {
                log.log(Level.WARNING, "Failed to load the region data for '" + name + "' (periodic attempts will be made to load the data until success)", e);
                this.failingLoads.add(normal);
                return null;
            }
        }
    }

    private RegionManager createAndLoad(String name) throws StorageException {
        RegionDatabase store = this.driver.get(name);
        RegionManager manager = new RegionManager(store, this.indexFactory, this.flagRegistry);
        manager.load();
        return manager;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void unload(String name) {
        Preconditions.checkNotNull((Object)name);
        Normal normal = Normal.normal(name);
        Object object = this.lock;
        synchronized (object) {
            RegionManager manager = (RegionManager)this.mapping.get(normal);
            if (manager != null) {
                try {
                    manager.save();
                }
                catch (StorageException e) {
                    log.log(Level.WARNING, "Failed to save the region data for '" + name + "'", e);
                }
                this.mapping.remove(normal);
                this.failingSaves.remove(manager);
            }
            this.failingLoads.remove(normal);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void unloadAll() {
        Object object = this.lock;
        synchronized (object) {
            for (Map.Entry entry : this.mapping.entrySet()) {
                String name = ((Normal)entry.getKey()).toString();
                RegionManager manager = (RegionManager)entry.getValue();
                try {
                    manager.saveChanges();
                }
                catch (StorageException e) {
                    log.log(Level.WARNING, "Failed to save the region data for '" + name + "' while unloading the data for all worlds", e);
                }
            }
            this.mapping.clear();
            this.failingLoads.clear();
            this.failingSaves.clear();
        }
    }

    public void shutdown() {
        this.timer.cancel();
        this.unloadAll();
    }

    @Nullable
    public RegionManager get(String name) {
        Preconditions.checkNotNull((Object)name);
        return (RegionManager)this.mapping.get(Normal.normal(name));
    }

    public List<RegionManager> getLoaded() {
        return Collections.unmodifiableList(new ArrayList(this.mapping.values()));
    }

    public Set<RegionManager> getSaveFailures() {
        return new HashSet<RegionManager>(this.failingSaves);
    }

    private class BackgroundLoader
    extends TimerTask {
        private String lastMsg;

        private BackgroundLoader() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            Object object = RegionContainerImpl.this.lock;
            synchronized (object) {
                if (!RegionContainerImpl.this.failingLoads.isEmpty()) {
                    log.info("Attempting to load region data that has previously failed to load...");
                    Iterator<Normal> it = RegionContainerImpl.this.failingLoads.iterator();
                    while (it.hasNext()) {
                        Normal normal = it.next();
                        try {
                            RegionManager manager = RegionContainerImpl.this.createAndLoad(normal.toString());
                            RegionContainerImpl.this.mapping.put(normal, manager);
                            it.remove();
                            log.info("Successfully loaded region data for '" + normal.toString() + "'");
                        }
                        catch (StorageException e) {
                            if (e.getCause() != null && e.getCause().getMessage().equals(this.lastMsg)) {
                                log.log(Level.WARNING, "Region data is still failing to load, at least for the world named '" + normal.toString() + "'");
                                break;
                            }
                            log.log(Level.WARNING, "Region data is still failing to load, at least for the world named '" + normal.toString() + "'", e);
                            this.lastMsg = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
                            break;
                        }
                    }
                }
            }
        }
    }

    private class BackgroundSaver
    extends TimerTask {
        private BackgroundSaver() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            Object object = RegionContainerImpl.this.lock;
            synchronized (object) {
                for (Map.Entry entry : RegionContainerImpl.this.mapping.entrySet()) {
                    String name = ((Normal)entry.getKey()).toString();
                    RegionManager manager = (RegionManager)entry.getValue();
                    try {
                        if (manager.saveChanges()) {
                            log.info("Region data changes made in '" + name + "' have been background saved");
                        }
                        RegionContainerImpl.this.failingSaves.remove(manager);
                    }
                    catch (StorageException e) {
                        RegionContainerImpl.this.failingSaves.add(manager);
                        log.log(Level.WARNING, "Failed to save the region data for '" + name + "' during a periodical save", e);
                    }
                    catch (Exception e) {
                        RegionContainerImpl.this.failingSaves.add(manager);
                        log.log(Level.WARNING, "An expected error occurred during a periodical save", e);
                    }
                }
            }
        }
    }
}

