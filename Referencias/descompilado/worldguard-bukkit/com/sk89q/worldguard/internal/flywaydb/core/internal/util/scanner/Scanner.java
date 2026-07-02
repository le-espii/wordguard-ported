/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner;

import com.sk89q.worldguard.internal.flywaydb.core.internal.util.FeatureDetector;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.Location;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.Resource;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.android.AndroidScanner;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.classpath.ClassPathScanner;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.filesystem.FileSystemScanner;

public class Scanner {
    private final ClassLoader classLoader;

    public Scanner(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public Resource[] scanForResources(Location location, String prefix, String suffix) throws Exception {
        if (location.isFileSystem()) {
            return new FileSystemScanner().scanForResources(location.getPath(), prefix, suffix);
        }
        if (new FeatureDetector(this.classLoader).isAndroidAvailable()) {
            return new AndroidScanner(this.classLoader).scanForResources(location.getPath(), prefix, suffix);
        }
        return new ClassPathScanner(this.classLoader).scanForResources(location.getPath(), prefix, suffix);
    }

    public Class<?>[] scanForClasses(Location location, Class<?> implementedInterface) throws Exception {
        if (new FeatureDetector(this.classLoader).isAndroidAvailable()) {
            return new AndroidScanner(this.classLoader).scanForClasses(location.getPath(), implementedInterface);
        }
        return new ClassPathScanner(this.classLoader).scanForClasses(location.getPath(), implementedInterface);
    }
}

