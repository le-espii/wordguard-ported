/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldguard.domains;

import com.google.common.base.Preconditions;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.domains.Domain;
import com.sk89q.worldguard.util.ChangeTracked;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

public class PlayerDomain
implements Domain,
ChangeTracked {
    private final Set<UUID> uniqueIds = new CopyOnWriteArraySet<UUID>();
    private final Set<String> names = new CopyOnWriteArraySet<String>();
    private boolean dirty = true;

    public PlayerDomain() {
    }

    public PlayerDomain(PlayerDomain domain) {
        Preconditions.checkNotNull((Object)domain, (Object)"domain");
        this.uniqueIds.addAll(domain.getUniqueIds());
        this.names.addAll(domain.getPlayers());
        this.dirty = true;
    }

    @Deprecated
    public PlayerDomain(String[] names) {
        for (String name : names) {
            this.addPlayer(name);
        }
    }

    @Deprecated
    public void addPlayer(String name) {
        Preconditions.checkNotNull((Object)name);
        if (!name.trim().isEmpty()) {
            this.setDirty(true);
            this.names.add(name.trim().toLowerCase());
        }
    }

    public void addPlayer(UUID uniqueId) {
        Preconditions.checkNotNull((Object)uniqueId);
        this.setDirty(true);
        this.uniqueIds.add(uniqueId);
    }

    public void addPlayer(LocalPlayer player) {
        Preconditions.checkNotNull((Object)player);
        this.setDirty(true);
        this.addPlayer(player.getUniqueId());
    }

    @Deprecated
    public void removePlayer(String name) {
        Preconditions.checkNotNull((Object)name);
        this.setDirty(true);
        this.names.remove(name.trim().toLowerCase());
    }

    public void removePlayer(UUID uuid) {
        Preconditions.checkNotNull((Object)uuid);
        this.setDirty(true);
        this.uniqueIds.remove(uuid);
    }

    public void removePlayer(LocalPlayer player) {
        Preconditions.checkNotNull((Object)player);
        this.setDirty(true);
        this.removePlayer(player.getName());
        this.removePlayer(player.getUniqueId());
    }

    @Override
    public boolean contains(LocalPlayer player) {
        Preconditions.checkNotNull((Object)player);
        return this.contains(player.getUniqueId()) || !this.names.isEmpty() && this.contains(player.getName());
    }

    @Deprecated
    public Set<String> getPlayers() {
        return Collections.unmodifiableSet(this.names);
    }

    public Set<UUID> getUniqueIds() {
        return Collections.unmodifiableSet(this.uniqueIds);
    }

    @Override
    public boolean contains(UUID uniqueId) {
        Preconditions.checkNotNull((Object)uniqueId);
        return this.uniqueIds.contains(uniqueId);
    }

    @Override
    public boolean contains(String playerName) {
        Preconditions.checkNotNull((Object)playerName);
        return this.names.contains(playerName.trim().toLowerCase());
    }

    @Override
    public int size() {
        return this.names.size() + this.uniqueIds.size();
    }

    @Override
    public void clear() {
        this.setDirty(true);
        this.uniqueIds.clear();
        this.names.clear();
    }

    @Override
    public boolean isDirty() {
        return this.dirty;
    }

    @Override
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public String toString() {
        return "{uuids=" + String.valueOf(this.uniqueIds) + ", names=" + String.valueOf(this.names) + "}";
    }
}

