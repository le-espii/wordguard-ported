/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonIOException
 *  com.google.gson.JsonParseException
 *  org.apache.logging.log4j.Logger
 */
package com.sk89q.worldedit.session.storage;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.internal.util.LogManagerCompat;
import com.sk89q.worldedit.session.storage.SessionStore;
import com.sk89q.worldedit.util.gson.GsonUtil;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.UUID;
import org.apache.logging.log4j.Logger;

public class JsonFileSessionStore
implements SessionStore {
    private static final Logger LOGGER = LogManagerCompat.getLogger();
    private final Gson gson;
    private final Path dir;

    @Deprecated
    public JsonFileSessionStore(File dir) {
        this(dir.toPath());
    }

    public JsonFileSessionStore(Path dir) {
        Preconditions.checkNotNull((Object)dir);
        try {
            Files.createDirectories(dir, new FileAttribute[0]);
        }
        catch (IOException e) {
            LOGGER.warn("Failed to create directory '" + String.valueOf(dir) + "' for sessions", (Throwable)e);
        }
        this.dir = dir;
        GsonBuilder builder = GsonUtil.createBuilder();
        this.gson = builder.create();
    }

    private Path getPath(UUID id) {
        Preconditions.checkNotNull((Object)id);
        return this.dir.resolve(String.valueOf(id) + ".json");
    }

    @Override
    public LocalSession load(UUID id) throws IOException {
        LocalSession localSession;
        block11: {
            Path file = this.getPath(id);
            BufferedReader reader = Files.newBufferedReader(file);
            try {
                LocalSession session = (LocalSession)this.gson.fromJson((Reader)reader, LocalSession.class);
                if (session == null) {
                    LOGGER.warn("Loaded a null session from {}, creating new session", (Object)file);
                    if (!Files.deleteIfExists(file)) {
                        LOGGER.warn("Failed to delete corrupted session {}", (Object)file);
                    }
                    session = new LocalSession();
                }
                localSession = session;
                if (reader == null) break block11;
            }
            catch (Throwable throwable) {
                try {
                    if (reader != null) {
                        try {
                            reader.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (JsonParseException e) {
                    throw new IOException(e);
                }
                catch (NoSuchFileException e) {
                    return new LocalSession();
                }
            }
            reader.close();
        }
        return localSession;
    }

    @Override
    public void save(UUID id, LocalSession session) throws IOException {
        Preconditions.checkNotNull((Object)session);
        Path finalFile = this.getPath(id);
        Path tempFile = finalFile.getParent().resolve(String.valueOf(finalFile.getFileName()) + ".tmp");
        try (BufferedWriter writer = Files.newBufferedWriter(tempFile, new OpenOption[0]);){
            this.gson.toJson((Object)session, (Appendable)writer);
        }
        catch (JsonIOException e) {
            throw new IOException(e);
        }
        if (Files.size(tempFile) == 0L) {
            throw new IllegalStateException("Gson wrote zero bytes");
        }
        try {
            Files.move(tempFile, finalFile, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException e) {
            LOGGER.warn("Failed to rename temporary session file to " + String.valueOf(finalFile.toAbsolutePath()), (Throwable)e);
        }
    }
}

