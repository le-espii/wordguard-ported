/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Logger
 */
package com.sk89q.worldedit.bukkit.adapter;

import com.sk89q.worldedit.bukkit.adapter.AdapterLoadException;
import com.sk89q.worldedit.bukkit.adapter.BukkitImplAdapter;
import com.sk89q.worldedit.internal.util.LogManagerCompat;
import com.sk89q.worldedit.util.io.Closer;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.logging.log4j.Logger;

public class BukkitImplLoader {
    private static final Logger LOGGER = LogManagerCompat.getLogger();
    private final List<String> adapterCandidates = new ArrayList<String>();
    private String customCandidate;
    private static final String SEARCH_PACKAGE = "com.sk89q.worldedit.bukkit.adapter.impl";
    private static final String SEARCH_PACKAGE_DOT = "com.sk89q.worldedit.bukkit.adapter.impl.";
    private static final String SEARCH_PATH = "com.sk89q.worldedit.bukkit.adapter.impl".replace(".", "/");
    private static final String CLASS_SUFFIX = ".class";
    private static final String LOAD_ERROR_MESSAGE = "\n**********************************************\n** This WorldEdit version does not fully support your version of Bukkit.\n**\n** When working with blocks or undoing, chests will be empty, signs\n** will be blank, and so on. There will be no support for entity\n** and block property-related functions.\n**\n** Please see https://worldedit.enginehub.org/en/latest/faq/#bukkit-adapters\n**********************************************\n";

    public BukkitImplLoader() {
        this.addDefaults();
    }

    private void addDefaults() {
        String className = System.getProperty("worldedit.bukkit.adapter");
        if (className != null) {
            this.customCandidate = className;
            this.adapterCandidates.add(className);
            LOGGER.info("-Dworldedit.bukkit.adapter used to add " + className + " to the list of available Bukkit adapters");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addFromJar(File file) throws IOException {
        Closer closer = Closer.create();
        JarFile jar = closer.register(new JarFile(file));
        try {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String className = jarEntry.getName().replaceAll("[/\\\\]+", ".");
                if (!className.startsWith(SEARCH_PACKAGE_DOT) || jarEntry.isDirectory()) continue;
                int beginIndex = 0;
                int endIndex = className.length() - CLASS_SUFFIX.length();
                className = className.substring(beginIndex, endIndex);
                this.adapterCandidates.add(className);
            }
        }
        finally {
            closer.close();
        }
    }

    public void addFromPath(ClassLoader classLoader) throws IOException {
        Enumeration<URL> resources = classLoader.getResources(SEARCH_PATH);
        while (resources.hasMoreElements()) {
            File file = new File(resources.nextElement().getFile());
            this.addFromPath(file);
        }
    }

    private void addFromPath(File file) {
        String resource = SEARCH_PACKAGE_DOT + file.getName();
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File child : files) {
                    this.addFromPath(child);
                }
            }
        } else if (resource.endsWith(CLASS_SUFFIX)) {
            int beginIndex = 0;
            int endIndex = resource.length() - CLASS_SUFFIX.length();
            String className = resource.substring(beginIndex, endIndex);
            this.adapterCandidates.add(className);
        }
    }

    public BukkitImplAdapter loadAdapter() throws AdapterLoadException {
        for (String className : this.adapterCandidates) {
            try {
                Class<?> cls = Class.forName(className);
                if (cls.isSynthetic() || !BukkitImplAdapter.class.isAssignableFrom(cls)) continue;
                return (BukkitImplAdapter)cls.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            }
            catch (ClassNotFoundException e) {
                LOGGER.warn("Failed to load the Bukkit adapter class '" + className + "' that is not supposed to be missing", (Throwable)e);
            }
            catch (IllegalAccessException e) {
                LOGGER.warn("Failed to load the Bukkit adapter class '" + className + "' that is not supposed to be raising this error", (Throwable)e);
            }
            catch (Throwable e) {
                if (!className.equals(this.customCandidate)) continue;
                LOGGER.warn("Failed to load the Bukkit adapter class '" + className + "'", e);
            }
        }
        throw new AdapterLoadException(LOAD_ERROR_MESSAGE);
    }
}

