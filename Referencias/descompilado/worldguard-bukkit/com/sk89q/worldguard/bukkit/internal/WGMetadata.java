/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.bukkit.metadata.FixedMetadataValue
 *  org.bukkit.metadata.MetadataValue
 *  org.bukkit.metadata.Metadatable
 *  org.bukkit.plugin.Plugin
 */
package com.sk89q.worldguard.bukkit.internal;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import java.util.List;
import javax.annotation.Nullable;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;

public final class WGMetadata {
    private WGMetadata() {
    }

    public static void put(Metadatable target, String key, Object value) {
        target.setMetadata(key, (MetadataValue)new FixedMetadataValue((Plugin)WorldGuardPlugin.inst(), value));
    }

    @Nullable
    public static <T> T getIfPresent(Metadatable target, String key, Class<T> expected) {
        List values = target.getMetadata(key);
        WorldGuardPlugin owner = WorldGuardPlugin.inst();
        for (MetadataValue value : values) {
            Object v;
            if (value.getOwningPlugin() != owner || !expected.isInstance(v = value.value())) continue;
            return (T)v;
        }
        return null;
    }

    public static void remove(Metadatable target, String key) {
        target.removeMetadata(key, (Plugin)WorldGuardPlugin.inst());
    }
}

