/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  dalvik.system.DexFile
 *  dalvik.system.PathClassLoader
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.android;

import android.content.Context;
import com.sk89q.worldguard.internal.flywaydb.core.api.FlywayException;
import com.sk89q.worldguard.internal.flywaydb.core.api.android.ContextHolder;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.ClassUtils;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.Log;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.LogFactory;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.Resource;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.android.AndroidResource;
import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;

public class AndroidScanner {
    private static final Log LOG = LogFactory.getLog(AndroidScanner.class);
    private final Context context;
    private final PathClassLoader classLoader;

    public AndroidScanner(ClassLoader classLoader) {
        this.classLoader = (PathClassLoader)classLoader;
        this.context = ContextHolder.getContext();
        if (this.context == null) {
            throw new FlywayException("Unable to scan for Migrations! Context not set. Within an activity you can fix this with org.flywaydb.core.api.android.ContextHolder.setContext(this);");
        }
    }

    public Resource[] scanForResources(String path, String prefix, String suffix) throws Exception {
        ArrayList<AndroidResource> resources = new ArrayList<AndroidResource>();
        for (String asset : this.context.getAssets().list(path)) {
            if (asset.startsWith(prefix) && asset.endsWith(suffix) && asset.length() > (prefix + suffix).length()) {
                resources.add(new AndroidResource(this.context.getAssets(), path, asset));
                continue;
            }
            LOG.debug("Filtering out asset: " + asset);
        }
        return resources.toArray(new Resource[resources.size()]);
    }

    public Class<?>[] scanForClasses(String path, Class<?> implementedInterface) throws Exception {
        String pkg = path.replace("/", ".");
        ArrayList<Class> classes = new ArrayList<Class>();
        DexFile dex = new DexFile(this.context.getApplicationInfo().sourceDir);
        Enumeration entries = dex.entries();
        while (entries.hasMoreElements()) {
            String className = (String)entries.nextElement();
            if (!className.startsWith(pkg)) continue;
            Class clazz = this.classLoader.loadClass(className);
            if (Modifier.isAbstract(clazz.getModifiers())) {
                LOG.debug("Skipping abstract class: " + className);
                continue;
            }
            if (!implementedInterface.isAssignableFrom(clazz)) continue;
            try {
                ClassUtils.instantiate(className, (ClassLoader)this.classLoader);
            }
            catch (Exception e) {
                throw new FlywayException("Unable to instantiate class: " + className);
            }
            classes.add(clazz);
            LOG.debug("Found class: " + className);
        }
        return classes.toArray(new Class[classes.size()]);
    }
}

