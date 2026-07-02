/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nullable
 *  org.bukkit.Bukkit
 *  org.bukkit.plugin.Plugin
 */
package com.sk89q.bukkit.util;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class ClassSourceValidator {
    private static final String SEPARATOR_LINE;
    private static final Method loadClass;
    private static Class<?> pluginClassLoaderClass;
    private final Plugin plugin;
    @Nullable
    private final ClassLoader expectedClassLoader;

    public ClassSourceValidator(Plugin plugin) {
        Preconditions.checkNotNull((Object)plugin, (Object)"plugin");
        this.plugin = plugin;
        this.expectedClassLoader = plugin.getClass().getClassLoader();
        if (loadClass == null) {
            plugin.getLogger().info("Bukkit PluginClassLoader seems to have changed. Class source validation will be skipped.");
        }
    }

    public Map<Class<?>, Plugin> findMismatches(List<Class<?>> classes) {
        Preconditions.checkNotNull(classes, (Object)"classes");
        if (this.expectedClassLoader == null || loadClass == null) {
            return ImmutableMap.of();
        }
        HashMap mismatches = new HashMap();
        for (Plugin target : Bukkit.getPluginManager().getPlugins()) {
            ClassLoader targetLoader;
            if (target == this.plugin || !pluginClassLoaderClass.isAssignableFrom((targetLoader = target.getClass().getClassLoader()).getClass())) continue;
            for (Class<?> testClass : classes) {
                Class targetClass;
                try {
                    targetClass = (Class)loadClass.invoke(targetLoader, testClass.getName(), false, false, false);
                }
                catch (IllegalAccessException | InvocationTargetException ignored) {
                    continue;
                }
                if (targetClass.getClassLoader() == this.expectedClassLoader) continue;
                mismatches.putIfAbsent(testClass, (Plugin)(targetClass.getClassLoader() == targetLoader ? target : null));
            }
        }
        return mismatches;
    }

    public void reportMismatches(List<Class<?>> classes) {
        if (Boolean.getBoolean("enginehub.disable.class.source.validation")) {
            return;
        }
        Map<Class<?>, Plugin> mismatches = this.findMismatches(classes);
        if (mismatches.isEmpty()) {
            return;
        }
        StringBuilder builder = new StringBuilder("\n");
        builder.append(SEPARATOR_LINE).append("\n");
        builder.append("** /!\\    SEVERE WARNING    /!\\\n");
        builder.append("** \n");
        builder.append("** A plugin developer has included a portion of \n");
        builder.append("** ").append(this.plugin.getName()).append(" into their own plugin, so rather than using\n");
        builder.append("** the version of ").append(this.plugin.getName()).append(" that you downloaded, you\n");
        builder.append("** will be using a broken mix of old ").append(this.plugin.getName()).append(" (that came\n");
        builder.append("** with the plugin) and your downloaded version. THIS MAY\n");
        builder.append("** SEVERELY BREAK ").append(this.plugin.getName().toUpperCase(Locale.ROOT)).append(" AND ALL OF ITS FEATURES.\n");
        builder.append("**\n");
        builder.append("** This may have happened because the developer is using\n");
        builder.append("** the ").append(this.plugin.getName()).append(" API and thinks that including\n");
        builder.append("** ").append(this.plugin.getName()).append(" is necessary. However, it is not!\n");
        builder.append("**\n");
        builder.append("** Here are some files that have been overridden:\n");
        builder.append("** \n");
        for (Map.Entry<Class<?>, Plugin> entry : mismatches.entrySet()) {
            Plugin badPlugin = entry.getValue();
            String url = badPlugin == null ? "(unknown)" : badPlugin.getName() + " (" + String.valueOf(badPlugin.getClass().getProtectionDomain().getCodeSource().getLocation()) + ")";
            builder.append("** '").append(entry.getKey().getSimpleName()).append("' came from '").append(url).append("'\n");
        }
        builder.append("**\n");
        builder.append("** Please report this to the plugins' developers.\n");
        builder.append(SEPARATOR_LINE).append("\n");
        this.plugin.getLogger().severe(builder.toString());
    }

    static {
        Method tmp;
        SEPARATOR_LINE = Strings.repeat((String)"*", (int)46);
        try {
            pluginClassLoaderClass = Class.forName("org.bukkit.plugin.java.PluginClassLoader", false, Bukkit.class.getClassLoader());
            tmp = pluginClassLoaderClass.getDeclaredMethod("loadClass0", String.class, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE);
            tmp.setAccessible(true);
        }
        catch (ClassNotFoundException | NoSuchMethodException e) {
            tmp = null;
        }
        loadClass = tmp;
    }
}

