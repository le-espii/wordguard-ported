/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldguard.util.profile;

import com.google.common.base.Preconditions;
import java.util.UUID;

public final class Profile {
    private final UUID uniqueId;
    private final String name;

    public Profile(UUID uniqueId, String name) {
        Preconditions.checkNotNull((Object)uniqueId);
        Preconditions.checkNotNull((Object)name);
        this.uniqueId = uniqueId;
        this.name = name;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public Profile setUniqueId(UUID uniqueId) {
        return new Profile(uniqueId, this.name);
    }

    public String getName() {
        return this.name;
    }

    public Profile setName(String name) {
        return new Profile(this.uniqueId, name);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Profile profile = (Profile)o;
        return this.uniqueId.equals(profile.uniqueId);
    }

    public int hashCode() {
        return this.uniqueId.hashCode();
    }

    public String toString() {
        return "Profile{uniqueId=" + this.uniqueId + ", name='" + this.name + "'}";
    }
}

