/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.adapter;

public class Refraction {
    private static final String MOJANG_MAPPED_CLASS_NAME = "net.minecraft.nbt.ListTag";
    private static final boolean IS_MOJANG_MAPPED;

    public static String pickName(String mojangName, String spigotName) {
        return IS_MOJANG_MAPPED ? mojangName : spigotName;
    }

    private Refraction() {
    }

    static {
        boolean isMojangMapped;
        try {
            Class.forName(MOJANG_MAPPED_CLASS_NAME, false, Refraction.class.getClassLoader());
            isMojangMapped = true;
        }
        catch (ClassNotFoundException e) {
            isMojangMapped = false;
        }
        IS_MOJANG_MAPPED = isMojangMapped;
    }
}

