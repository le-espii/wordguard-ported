/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.util.io;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.util.io.ResourceLoader;
import java.nio.file.Path;

public class WorldEditResourceLoader
implements ResourceLoader {
    private final WorldEdit worldEdit;

    public WorldEditResourceLoader(WorldEdit worldEdit) {
        this.worldEdit = worldEdit;
    }

    @Override
    public Path getLocalResource(String pathName) {
        return this.worldEdit.getWorkingDirectoryPath(pathName);
    }
}

