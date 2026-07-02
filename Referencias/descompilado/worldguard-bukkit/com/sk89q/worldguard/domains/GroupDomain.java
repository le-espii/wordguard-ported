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

public class GroupDomain
implements Domain,
ChangeTracked {
    private final Set<String> groups = new CopyOnWriteArraySet<String>();
    private boolean dirty = true;

    public GroupDomain() {
    }

    public GroupDomain(GroupDomain domain) {
        Preconditions.checkNotNull((Object)domain, (Object)"domain");
        this.groups.addAll(domain.getGroups());
    }

    public GroupDomain(String[] groups) {
        Preconditions.checkNotNull((Object)groups);
        for (String group : groups) {
            this.addGroup(group);
        }
    }

    public void addGroup(String name) {
        Preconditions.checkNotNull((Object)name);
        if (!name.trim().isEmpty()) {
            this.setDirty(true);
            this.groups.add(name.trim().toLowerCase());
        }
    }

    public void removeGroup(String name) {
        Preconditions.checkNotNull((Object)name);
        this.setDirty(true);
        this.groups.remove(name.trim().toLowerCase());
    }

    @Override
    public boolean contains(LocalPlayer player) {
        Preconditions.checkNotNull((Object)player);
        for (String group : this.groups) {
            if (!player.hasGroup(group)) continue;
            return true;
        }
        return false;
    }

    public Set<String> getGroups() {
        return Collections.unmodifiableSet(this.groups);
    }

    @Override
    public boolean contains(UUID uniqueId) {
        return false;
    }

    @Override
    public boolean contains(String playerName) {
        return false;
    }

    @Override
    public int size() {
        return this.groups.size();
    }

    @Override
    public void clear() {
        this.setDirty(true);
        this.groups.clear();
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
        return "{names=" + String.valueOf(this.groups) + "}";
    }
}

