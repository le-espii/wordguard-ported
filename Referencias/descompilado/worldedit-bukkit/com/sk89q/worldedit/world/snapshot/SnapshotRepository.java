/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.world.snapshot;

import com.sk89q.worldedit.world.snapshot.InvalidSnapshotException;
import com.sk89q.worldedit.world.snapshot.ModificationTimerParser;
import com.sk89q.worldedit.world.snapshot.Snapshot;
import com.sk89q.worldedit.world.snapshot.SnapshotDateParser;
import com.sk89q.worldedit.world.snapshot.YYMMDDHHIISSParser;
import com.sk89q.worldedit.world.storage.MissingWorldException;
import java.io.File;
import java.io.FilenameFilter;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;

public class SnapshotRepository {
    protected File dir;
    protected List<SnapshotDateParser> dateParsers = new ArrayList<SnapshotDateParser>();

    public SnapshotRepository(File dir) {
        this.dir = dir;
        dir.mkdirs();
        this.dateParsers.add(new YYMMDDHHIISSParser());
        this.dateParsers.add(new ModificationTimerParser());
    }

    public SnapshotRepository(String dir) {
        this(new File(dir));
    }

    public List<Snapshot> getSnapshots(boolean newestFirst, String worldName) throws MissingWorldException {
        FilenameFilter filter = (dir, name) -> {
            File f = new File(dir, name);
            return this.isValidSnapshot(f);
        };
        File[] snapshotFiles = this.dir.listFiles();
        if (snapshotFiles == null) {
            throw new MissingWorldException(worldName);
        }
        ArrayList<Snapshot> list = new ArrayList<Snapshot>(snapshotFiles.length);
        for (File file : snapshotFiles) {
            if (this.isValidSnapshot(file)) {
                Snapshot snapshot = new Snapshot(this, file.getName());
                if (!snapshot.containsWorld(worldName)) continue;
                this.detectDate(snapshot);
                list.add(snapshot);
                continue;
            }
            if (!file.isDirectory() || !file.getName().equalsIgnoreCase(worldName)) continue;
            for (String name2 : file.list(filter)) {
                Snapshot snapshot = new Snapshot(this, file.getName() + "/" + name2);
                this.detectDate(snapshot);
                list.add(snapshot);
            }
        }
        if (newestFirst) {
            list.sort(Collections.reverseOrder());
        } else {
            Collections.sort(list);
        }
        return list;
    }

    @Nullable
    public Snapshot getSnapshotAfter(ZonedDateTime date, String world) throws MissingWorldException {
        List<Snapshot> snapshots = this.getSnapshots(true, world);
        Snapshot last = null;
        for (Snapshot snapshot : snapshots) {
            if (snapshot.getDate() != null && snapshot.getDate().compareTo(date) < 0) {
                return last;
            }
            last = snapshot;
        }
        return last;
    }

    @Nullable
    public Snapshot getSnapshotBefore(ZonedDateTime date, String world) throws MissingWorldException {
        List<Snapshot> snapshots = this.getSnapshots(false, world);
        Snapshot last = null;
        for (Snapshot snapshot : snapshots) {
            if (snapshot.getDate().compareTo(date) > 0) {
                return last;
            }
            last = snapshot;
        }
        return last;
    }

    protected void detectDate(Snapshot snapshot) {
        for (SnapshotDateParser parser : this.dateParsers) {
            Calendar date = parser.detectDate(snapshot.getFile());
            if (date == null) continue;
            snapshot.setDate(date.toInstant().atZone(ZoneOffset.UTC));
            return;
        }
        snapshot.setDate(null);
    }

    @Nullable
    public Snapshot getDefaultSnapshot(String world) throws MissingWorldException {
        List<Snapshot> snapshots = this.getSnapshots(true, world);
        if (snapshots.isEmpty()) {
            return null;
        }
        return snapshots.get(0);
    }

    public boolean isValidSnapshotName(String snapshot) {
        return this.isValidSnapshot(new File(this.dir, snapshot));
    }

    protected boolean isValidSnapshot(File file) {
        if (!file.getName().matches("^[A-Za-z0-9_\\- \\./\\\\'\\$@~!%\\^\\*\\(\\)\\[\\]\\+\\{\\},\\?]+$")) {
            return false;
        }
        if (file.isDirectory() && new File(file, "level.dat").exists()) {
            return true;
        }
        if (file.isFile()) {
            String lowerCaseFileName = file.getName().toLowerCase(Locale.ROOT);
            return lowerCaseFileName.endsWith(".zip") || lowerCaseFileName.endsWith(".tar.bz2") || lowerCaseFileName.endsWith(".tar.gz") || lowerCaseFileName.endsWith(".tar");
        }
        return false;
    }

    public Snapshot getSnapshot(String name) throws InvalidSnapshotException {
        if (!this.isValidSnapshotName(name)) {
            throw new InvalidSnapshotException();
        }
        return new Snapshot(this, name);
    }

    public File getDirectory() {
        return this.dir;
    }
}

