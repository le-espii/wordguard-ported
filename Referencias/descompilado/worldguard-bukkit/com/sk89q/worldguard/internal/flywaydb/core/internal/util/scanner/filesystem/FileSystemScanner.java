/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.filesystem;

import com.sk89q.worldguard.internal.flywaydb.core.api.FlywayException;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.Log;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.LogFactory;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.Resource;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.filesystem.FileSystemResource;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

public class FileSystemScanner {
    private static final Log LOG = LogFactory.getLog(FileSystemScanner.class);

    public Resource[] scanForResources(String path, String prefix, String suffix) throws IOException {
        LOG.debug("Scanning for filesystem resources at '" + path + "' (Prefix: '" + prefix + "', Suffix: '" + suffix + "')");
        if (!new File(path).isDirectory()) {
            throw new FlywayException("Invalid filesystem path: " + path);
        }
        TreeSet<FileSystemResource> resources = new TreeSet<FileSystemResource>();
        Set<String> resourceNames = this.findResourceNames(path, prefix, suffix);
        for (String resourceName : resourceNames) {
            resources.add(new FileSystemResource(resourceName));
            LOG.debug("Found filesystem resource: " + resourceName);
        }
        return resources.toArray(new Resource[resources.size()]);
    }

    private Set<String> findResourceNames(String path, String prefix, String suffix) throws IOException {
        Set<String> resourceNames = this.findResourceNamesFromFileSystem(path, new File(path));
        return this.filterResourceNames(resourceNames, prefix, suffix);
    }

    private Set<String> findResourceNamesFromFileSystem(String scanRootLocation, File folder) throws IOException {
        File[] files;
        LOG.debug("Scanning for resources in path: " + folder.getPath() + " (" + scanRootLocation + ")");
        TreeSet<String> resourceNames = new TreeSet<String>();
        for (File file : files = folder.listFiles()) {
            if (!file.canRead()) continue;
            if (file.isDirectory()) {
                resourceNames.addAll(this.findResourceNamesFromFileSystem(scanRootLocation, file));
                continue;
            }
            resourceNames.add(file.getPath());
        }
        return resourceNames;
    }

    private Set<String> filterResourceNames(Set<String> resourceNames, String prefix, String suffix) {
        TreeSet<String> filteredResourceNames = new TreeSet<String>();
        for (String resourceName : resourceNames) {
            String fileName = resourceName.substring(resourceName.lastIndexOf(File.separator) + 1);
            if (fileName.startsWith(prefix) && fileName.endsWith(suffix) && fileName.length() > (prefix + suffix).length()) {
                filteredResourceNames.add(resourceName);
                continue;
            }
            LOG.debug("Filtering out resource: " + resourceName + " (filename: " + fileName + ")");
        }
        return filteredResourceNames;
    }
}

