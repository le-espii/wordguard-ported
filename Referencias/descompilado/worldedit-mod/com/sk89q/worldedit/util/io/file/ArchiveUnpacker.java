/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.hash.Hasher
 *  com.google.common.hash.Hashing
 *  com.google.common.io.ByteProcessor
 *  com.google.common.io.ByteStreams
 */
package com.sk89q.worldedit.util.io.file;

import com.google.common.collect.ImmutableSet;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteProcessor;
import com.google.common.io.ByteStreams;
import com.sk89q.worldedit.util.io.file.AttributeTarget;
import com.sk89q.worldedit.util.io.file.SafeFiles;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class ArchiveUnpacker {
    private static final String UNPACK_FINISHED = ".unpack_finished";
    private static final Lock lock = new ReentrantLock();
    private final Path unpackDir;

    public ArchiveUnpacker(Path unpackDir) throws IOException {
        this.unpackDir = unpackDir;
        Files.createDirectories(unpackDir, new FileAttribute[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public Path unpackArchive(URL archiveUrl) throws IOException {
        String hash;
        try (InputStream data = archiveUrl.openStream();){
            hash = (String)ByteStreams.readBytes((InputStream)data, (ByteProcessor)new ByteProcessor<String>(this){
                private final Hasher hasher = Hashing.crc32c().newHasher();

                public boolean processBytes(byte[] buf, int off, int len) {
                    this.hasher.putBytes(buf, off, len);
                    return true;
                }

                public String getResult() {
                    return this.hasher.hash().toString();
                }
            });
        }
        Path dest = this.unpackDir.resolve(hash);
        if (Files.exists(dest.resolve(UNPACK_FINISHED), new LinkOption[0])) {
            return dest;
        }
        lock.lock();
        try {
            if (Files.exists(dest.resolve(UNPACK_FINISHED), new LinkOption[0])) {
                Path path = dest;
                return path;
            }
            try (InputStream in = archiveUrl.openStream();
                 ZipInputStream zipReader = new ZipInputStream(in);){
                ZipEntry next;
                while ((next = zipReader.getNextEntry()) != null) {
                    Path resolved = dest.resolve(next.getName());
                    if (!resolved.startsWith(dest)) continue;
                    if (next.isDirectory()) {
                        Files.createDirectories(resolved, SafeFiles.getOwnerOnlyFileAttributes(AttributeTarget.DIRECTORY));
                        continue;
                    }
                    SeekableByteChannel channel = Files.newByteChannel(resolved, (Set<? extends OpenOption>)ImmutableSet.of((Object)StandardOpenOption.CREATE, (Object)StandardOpenOption.WRITE, (Object)StandardOpenOption.TRUNCATE_EXISTING), SafeFiles.getOwnerOnlyFileAttributes(AttributeTarget.FILE));
                    try {
                        ByteStreams.copy((ReadableByteChannel)Channels.newChannel(zipReader), (WritableByteChannel)channel);
                    }
                    finally {
                        if (channel == null) continue;
                        channel.close();
                    }
                }
            }
            Files.createFile(dest.resolve(UNPACK_FINISHED), new FileAttribute[0]);
            Path path = dest;
            return path;
        }
        finally {
            lock.unlock();
        }
    }
}

