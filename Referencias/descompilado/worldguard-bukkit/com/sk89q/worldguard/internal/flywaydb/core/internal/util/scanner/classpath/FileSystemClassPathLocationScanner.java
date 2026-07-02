/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.classpath;

import com.sk89q.worldguard.internal.flywaydb.core.internal.util.UrlUtils;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.Log;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.LogFactory;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.classpath.ClassPathLocationScanner;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Set;
import java.util.TreeSet;

public class FileSystemClassPathLocationScanner
implements ClassPathLocationScanner {
    private static final Log LOG = LogFactory.getLog(FileSystemClassPathLocationScanner.class);

    @Override
    public Set<String> findResourceNames(String location, URL locationUrl) throws IOException {
        String filePath = UrlUtils.toFilePath(locationUrl);
        File folder = new File(filePath);
        if (!folder.isDirectory()) {
            LOG.debug("Skipping path as it is not a directory: " + filePath);
            return new TreeSet<String>();
        }
        String classPathRootOnDisk = filePath.substring(0, filePath.length() - location.length());
        if (!classPathRootOnDisk.endsWith("/")) {
            classPathRootOnDisk = classPathRootOnDisk + "/";
        }
        LOG.debug("Scanning starting at classpath root in filesystem: " + classPathRootOnDisk);
        return this.findResourceNamesFromFileSystem(classPathRootOnDisk, location, folder);
    }

    Set<String> findResourceNamesFromFileSystem(String classPathRootOnDisk, String scanRootLocation, File folder) throws IOException {
        File[] files;
        LOG.debug("Scanning for resources in path: " + folder.getPath() + " (" + scanRootLocation + ")");
        TreeSet<String> resourceNames = new TreeSet<String>();
        for (File file : files = folder.listFiles()) {
            if (!file.canRead()) continue;
            if (file.isDirectory()) {
                resourceNames.addAll(this.findResourceNamesFromFileSystem(classPathRootOnDisk, scanRootLocation, file));
                continue;
            }
            resourceNames.add(this.toResourceNameOnClasspath(classPathRootOnDisk, file));
        }
        return resourceNames;
    }

    private String toResourceNameOnClasspath(String classPathRootOnDisk, File file) throws IOException {
        String fileName = URLDecoder.decode(file.toURI().toURL().getFile(), "UTF-8");
        return fileName.substring(classPathRootOnDisk.length());
    }
}

