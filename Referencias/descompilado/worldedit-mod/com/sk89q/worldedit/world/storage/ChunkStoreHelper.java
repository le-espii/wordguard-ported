/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.world.storage;

import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.NBTInputStream;
import com.sk89q.jnbt.Tag;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.extension.platform.Platform;
import com.sk89q.worldedit.world.DataException;
import com.sk89q.worldedit.world.DataFixer;
import com.sk89q.worldedit.world.chunk.AnvilChunk;
import com.sk89q.worldedit.world.chunk.AnvilChunk13;
import com.sk89q.worldedit.world.chunk.AnvilChunk16;
import com.sk89q.worldedit.world.chunk.AnvilChunk18;
import com.sk89q.worldedit.world.chunk.Chunk;
import com.sk89q.worldedit.world.chunk.OldChunk;
import com.sk89q.worldedit.world.storage.ChunkStoreException;
import java.io.IOException;
import java.io.InputStream;
import org.enginehub.linbus.tree.LinCompoundTag;
import org.enginehub.linbus.tree.LinNumberTag;
import org.enginehub.linbus.tree.LinTagType;

public class ChunkStoreHelper {
    @Deprecated
    public static CompoundTag readCompoundTag(ChunkDataInputSupplier input) throws DataException, IOException {
        try (InputStream stream = input.openInputStream();){
            CompoundTag compoundTag;
            try (NBTInputStream nbt = new NBTInputStream(stream);){
                Tag<?, ?> tag = nbt.readNamedTag().getTag();
                if (!(tag instanceof CompoundTag)) {
                    throw new ChunkStoreException("CompoundTag expected for chunk; got " + tag.getClass().getName());
                }
                compoundTag = (CompoundTag)tag;
            }
            return compoundTag;
        }
    }

    @Deprecated
    public static Chunk getChunk(CompoundTag rootTag) throws DataException {
        return ChunkStoreHelper.getChunk((LinCompoundTag)rootTag.toLinTag());
    }

    public static Chunk getChunk(LinCompoundTag rootTag) throws DataException {
        DataFixer dataFixer;
        int n;
        Object v = rootTag.value().get("DataVersion");
        if (v instanceof LinNumberTag) {
            LinNumberTag t = (LinNumberTag)v;
            n = ((Number)t.value()).intValue();
        } else {
            n = -1;
        }
        int dataVersion = n;
        Platform platform = WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.WORLD_EDITING);
        int currentDataVersion = platform.getDataVersion();
        if ((dataVersion > 0 || ChunkStoreHelper.hasLevelSections(rootTag)) && dataVersion < currentDataVersion && (dataFixer = platform.getDataFixer()) != null) {
            rootTag = dataFixer.fixUp(DataFixer.FixTypes.CHUNK, rootTag, dataVersion);
            dataVersion = currentDataVersion;
        }
        if (dataVersion >= 2860) {
            return new AnvilChunk18(rootTag);
        }
        LinCompoundTag tag = rootTag.findTag("Level", LinTagType.compoundTag());
        if (tag == null) {
            throw new ChunkStoreException("Missing root 'Level' tag");
        }
        if (dataVersion >= 2566) {
            return new AnvilChunk16(tag);
        }
        if (dataVersion >= 1519) {
            return new AnvilChunk13(tag);
        }
        if (tag.value().containsKey("Sections")) {
            return new AnvilChunk(tag);
        }
        return new OldChunk(tag);
    }

    private static boolean hasLevelSections(LinCompoundTag rootTag) {
        LinCompoundTag levelTag = rootTag.findTag("Level", LinTagType.compoundTag());
        return levelTag != null && levelTag.value().containsKey("Sections");
    }

    private ChunkStoreHelper() {
    }

    @FunctionalInterface
    public static interface ChunkDataInputSupplier {
        public InputStream openInputStream() throws DataException, IOException;
    }
}

