/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  javax.annotation.Nullable
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package com.sk89q.worldguard.bukkit.util;

import com.google.common.base.Function;
import com.sk89q.worldguard.bukkit.util.Entities;
import java.lang.reflect.Method;
import java.util.UUID;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public final class InteropUtils {
    @Nullable
    private static final Class<?> forgeFakePlayerClass;
    private static final UUID forgeFakePlayerUuid;
    private static final PlayerHandleFunction playerHandleFunction;

    @Nullable
    private static Class<?> findClass(String name) {
        try {
            return Class.forName(name);
        }
        catch (Throwable ignored) {
            return null;
        }
    }

    private InteropUtils() {
    }

    public static boolean isFakePlayer(Player player) {
        Object handle;
        UUID uuid = player.getUniqueId();
        String name = player.getName();
        if (Entities.isNPC((Entity)player)) {
            return true;
        }
        if (uuid.equals(forgeFakePlayerUuid)) {
            return true;
        }
        if (forgeFakePlayerClass != null && playerHandleFunction != null && (handle = playerHandleFunction.apply(player)) != null && forgeFakePlayerClass.isAssignableFrom(handle.getClass())) {
            return true;
        }
        return name.length() >= 3 && name.charAt(0) == '[' && name.charAt(name.length() - 1) == ']';
    }

    static {
        PlayerHandleFunction function;
        forgeFakePlayerUuid = UUID.fromString("41c82c87-7afb-4024-ba57-13d2c99cae77");
        forgeFakePlayerClass = InteropUtils.findClass("net.minecraftforge.common.util.FakePlayer");
        try {
            function = new PlayerHandleFunction();
        }
        catch (Exception e) {
            function = null;
        }
        playerHandleFunction = function;
    }

    private static final class PlayerHandleFunction
    implements Function<Object, Object> {
        private final Class<?> craftPlayerClass = Class.forName(Bukkit.getServer().getClass().getCanonicalName().replaceAll("CraftServer$", "entity.CraftPlayer"));
        private final Method getHandleMethod = this.craftPlayerClass.getMethod("getHandle", new Class[0]);

        private PlayerHandleFunction() throws NoSuchMethodException, ClassNotFoundException {
        }

        @Nullable
        public Object apply(Object o) {
            if (this.craftPlayerClass.isAssignableFrom(o.getClass())) {
                try {
                    return this.getHandleMethod.invoke(o, new Object[0]);
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
            return null;
        }
    }
}

