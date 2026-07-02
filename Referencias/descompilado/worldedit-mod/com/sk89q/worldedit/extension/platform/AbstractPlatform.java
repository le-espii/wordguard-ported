/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extension.platform;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.platform.Platform;
import com.sk89q.worldedit.util.concurrency.LazyReference;
import com.sk89q.worldedit.util.io.ResourceLoader;
import com.sk89q.worldedit.util.io.WorldEditResourceLoader;
import com.sk89q.worldedit.util.io.file.ArchiveUnpacker;
import com.sk89q.worldedit.util.translation.TranslationManager;
import com.sk89q.worldedit.world.DataFixer;
import com.sk89q.worldedit.world.World;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.List;

public abstract class AbstractPlatform
implements Platform {
    private final ResourceLoader resourceLoader = new WorldEditResourceLoader(WorldEdit.getInstance());
    private final LazyReference<ArchiveUnpacker> archiveUnpacker = LazyReference.from(() -> {
        try {
            return new ArchiveUnpacker(this.getConfiguration().getWorkingDirectoryPath().resolve(".archive-unpack"));
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    });
    private final LazyReference<TranslationManager> translationManager = LazyReference.from(() -> {
        try {
            return new TranslationManager(this.archiveUnpacker.getValue(), this.getResourceLoader());
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    });

    @Override
    public ResourceLoader getResourceLoader() {
        return this.resourceLoader;
    }

    @Override
    public TranslationManager getTranslationManager() {
        return this.translationManager.getValue();
    }

    @Override
    public int schedule(long delay, long period, Runnable task) {
        return -1;
    }

    @Override
    public List<? extends World> getWorlds() {
        return Collections.emptyList();
    }

    @Override
    public DataFixer getDataFixer() {
        return null;
    }

    @Override
    public void reload() {
        this.getTranslationManager().reload();
    }

    @Override
    public long getTickCount() {
        return System.nanoTime() / 50000000L;
    }
}

