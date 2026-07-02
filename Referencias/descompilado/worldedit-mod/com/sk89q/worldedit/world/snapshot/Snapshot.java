/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  de.schlichtherle.util.zip.ZipFile
 *  org.apache.logging.log4j.Logger
 */
package com.sk89q.worldedit.world.snapshot;

import com.sk89q.worldedit.internal.util.LogManagerCompat;
import com.sk89q.worldedit.world.DataException;
import com.sk89q.worldedit.world.snapshot.SnapshotRepository;
import com.sk89q.worldedit.world.storage.ChunkStore;
import com.sk89q.worldedit.world.storage.FileLegacyChunkStore;
import com.sk89q.worldedit.world.storage.FileMcRegionChunkStore;
import com.sk89q.worldedit.world.storage.TrueZipLegacyChunkStore;
import com.sk89q.worldedit.world.storage.TrueZipMcRegionChunkStore;
import com.sk89q.worldedit.world.storage.ZippedLegacyChunkStore;
import com.sk89q.worldedit.world.storage.ZippedMcRegionChunkStore;
import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.zip.ZipFile;
import org.apache.logging.log4j.Logger;

public class Snapshot
implements Comparable<Snapshot> {
    private static final Logger LOGGER = LogManagerCompat.getLogger();
    protected File file;
    protected String name;
    protected ZonedDateTime date;

    public Snapshot(SnapshotRepository repo, String snapshot) {
        this.file = new File(repo.getDirectory(), snapshot);
        this.name = snapshot;
    }

    public ChunkStore getChunkStore() throws IOException, DataException {
        ChunkStore chunkStore = this.internalGetChunkStore();
        LOGGER.info("WorldEdit: Using " + chunkStore.getClass().getCanonicalName() + " for loading snapshot '" + this.file.getAbsolutePath() + "'");
        return chunkStore;
    }

    private ChunkStore internalGetChunkStore() throws IOException, DataException {
        String lowerCaseFileName = this.file.getName().toLowerCase(Locale.ROOT);
        if (lowerCaseFileName.endsWith(".zip")) {
            try {
                TrueZipMcRegionChunkStore chunkStore = new TrueZipMcRegionChunkStore(this.file);
                if (!((ChunkStore)chunkStore).isValid()) {
                    return new TrueZipLegacyChunkStore(this.file);
                }
                return chunkStore;
            }
            catch (NoClassDefFoundError e) {
                ZippedMcRegionChunkStore chunkStore = new ZippedMcRegionChunkStore(this.file);
                if (!((ChunkStore)chunkStore).isValid()) {
                    return new ZippedLegacyChunkStore(this.file);
                }
                return chunkStore;
            }
        }
        if (lowerCaseFileName.endsWith(".tar.bz2") || lowerCaseFileName.endsWith(".tar.gz") || lowerCaseFileName.endsWith(".tar")) {
            try {
                TrueZipMcRegionChunkStore chunkStore = new TrueZipMcRegionChunkStore(this.file);
                if (!((ChunkStore)chunkStore).isValid()) {
                    return new TrueZipLegacyChunkStore(this.file);
                }
                return chunkStore;
            }
            catch (NoClassDefFoundError e) {
                throw new DataException("TrueZIP is required for .tar support");
            }
        }
        FileMcRegionChunkStore chunkStore = new FileMcRegionChunkStore(this.file);
        if (!((ChunkStore)chunkStore).isValid()) {
            return new FileLegacyChunkStore(this.file);
        }
        return chunkStore;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public boolean containsWorld(String worldname) {
        try {
            String lowerCaseFileName = this.file.getName().toLowerCase(Locale.ROOT);
            if (lowerCaseFileName.endsWith(".zip")) {
                try (ZipFile entry = new ZipFile(this.file);){
                    boolean bl = entry.getEntry(worldname) != null || entry.getEntry(worldname + "/level.dat") != null;
                    return bl;
                }
            }
            if (!lowerCaseFileName.endsWith(".tar.bz2") && !lowerCaseFileName.endsWith(".tar.gz")) {
                if (!lowerCaseFileName.endsWith(".tar")) return this.file.getName().equalsIgnoreCase(worldname);
            }
            try {
                de.schlichtherle.util.zip.ZipFile entry = new de.schlichtherle.util.zip.ZipFile(this.file);
                if (entry.getEntry(worldname) == null) return false;
                return true;
            }
            catch (NoClassDefFoundError e) {
                throw new DataException("TrueZIP is required for .tar support");
            }
        }
        catch (IOException ex) {
            LOGGER.info("Could not load snapshot: " + this.file.getPath());
            return false;
        }
        catch (DataException dataException) {
            // empty catch block
        }
        return false;
    }

    public String getName() {
        return this.name;
    }

    public File getFile() {
        return this.file;
    }

    public ZonedDateTime getDate() {
        return this.date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    @Override
    public int compareTo(Snapshot o) {
        if (o.date == null || this.date == null) {
            int ourIndex = this.name.indexOf(47);
            int theirIndex = o.name.indexOf(47);
            return this.name.substring(Math.min(ourIndex, 0)).compareTo(o.name.substring(Math.min(theirIndex, 0)));
        }
        return this.date.compareTo(o.date);
    }

    public boolean equals(Object o) {
        return o instanceof Snapshot && this.file.equals(((Snapshot)o).file);
    }

    public int hashCode() {
        return this.file.hashCode();
    }
}

