/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldguard.protection.managers.storage.file;

import com.google.common.base.Preconditions;
import com.sk89q.worldguard.protection.managers.storage.RegionDatabase;
import com.sk89q.worldguard.protection.managers.storage.RegionDriver;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.managers.storage.file.YamlRegionFile;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DirectoryYamlDriver
implements RegionDriver {
    private final File rootDir;
    private final String filename;

    public DirectoryYamlDriver(File rootDir, String filename) {
        Preconditions.checkNotNull((Object)rootDir);
        Preconditions.checkNotNull((Object)filename);
        this.rootDir = rootDir;
        this.filename = filename;
    }

    private File getPath(String id) {
        Preconditions.checkNotNull((Object)id);
        File f = new File(this.rootDir, id + File.separator + this.filename);
        try {
            f.getCanonicalPath();
            return f;
        }
        catch (IOException e) {
            throw new IllegalArgumentException("Invalid file path for the world's regions file");
        }
    }

    @Override
    public RegionDatabase get(String id) {
        Preconditions.checkNotNull((Object)id);
        File file = this.getPath(id);
        return new YamlRegionFile(id, file);
    }

    @Override
    public List<RegionDatabase> getAll() throws StorageException {
        ArrayList<RegionDatabase> stores = new ArrayList<RegionDatabase>();
        File[] files = this.rootDir.listFiles();
        if (files != null) {
            for (File dir : files) {
                if (!dir.isDirectory() || !new File(dir, "regions.yml").isFile()) continue;
                stores.add(new YamlRegionFile(dir.getName(), this.getPath(dir.getName())));
            }
        }
        return stores;
    }
}

