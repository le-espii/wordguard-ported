/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.util;

import com.google.common.base.Preconditions;
import java.text.Normalizer;
import javax.annotation.Nullable;

public final class Normal {
    private final String name;
    @Nullable
    private final String normal;

    private Normal(String name) {
        Preconditions.checkNotNull((Object)name);
        this.name = name;
        String normal = Normal.normalize(name);
        this.normal = !normal.equals(name) ? normal : null;
    }

    public String getName() {
        return this.name;
    }

    public String getNormal() {
        return this.normal != null ? this.normal : this.name;
    }

    public static String normalize(String name) {
        return Normalizer.normalize(name.toLowerCase(), Normalizer.Form.NFC);
    }

    public static Normal normal(String name) {
        return new Normal(name);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Normal that = (Normal)o;
        return this.getNormal().equals(that.getNormal());
    }

    public int hashCode() {
        return this.getNormal().hashCode();
    }

    public String toString() {
        return this.name;
    }
}

