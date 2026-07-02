/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.JarURLConnection;
import java.net.URI;
import java.util.function.Supplier;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import javax.annotation.Nullable;

public class WorldEditManifest {
    public static final String WORLD_EDIT_VERSION = "WorldEdit-Version";
    public static final String WORLD_EDIT_KIND = "WorldEdit-Kind";
    private final String worldEditVersion;
    private final Kind worldEditKind;

    public static WorldEditManifest load() {
        Kind kind;
        Attributes attributes = WorldEditManifest.readAttributes();
        try {
            kind = Kind.valueOf(WorldEditManifest.readAttribute(attributes, WORLD_EDIT_KIND, () -> "UNKNOWN"));
        }
        catch (IllegalArgumentException e) {
            kind = Kind.UNKNOWN;
        }
        return new WorldEditManifest(WorldEditManifest.readAttribute(attributes, WORLD_EDIT_VERSION, () -> "(unknown)"), kind);
    }

    @Nullable
    private static Attributes readAttributes() {
        Class<WorldEditManifest> clazz = WorldEditManifest.class;
        String className = clazz.getSimpleName() + ".class";
        String classPath = clazz.getResource(className).toString();
        if (!classPath.startsWith("jar")) {
            return null;
        }
        try {
            JarURLConnection jarConnection = (JarURLConnection)URI.create(classPath).toURL().openConnection();
            Manifest manifest = jarConnection.getManifest();
            return manifest.getMainAttributes();
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String readAttribute(@Nullable Attributes attributes, String name, Supplier<String> defaultAction) {
        if (attributes == null) {
            return defaultAction.get();
        }
        String value = attributes.getValue(name);
        return value != null ? value : defaultAction.get();
    }

    private WorldEditManifest(String worldEditVersion, Kind worldEditKind) {
        this.worldEditVersion = worldEditVersion;
        this.worldEditKind = worldEditKind;
    }

    public String getWorldEditVersion() {
        return this.worldEditVersion;
    }

    public Kind getWorldEditKind() {
        return this.worldEditKind;
    }

    public static enum Kind {
        MOD("mods"),
        PLUGIN("plugins"),
        UNKNOWN("mods/plugins");

        public final String folderName;

        private Kind(String folderName) {
            this.folderName = folderName;
        }
    }
}

