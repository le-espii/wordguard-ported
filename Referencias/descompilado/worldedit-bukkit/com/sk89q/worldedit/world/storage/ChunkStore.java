/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.world.storage;

import com.sk89q.jnbt.CompoundTag;
import com.sk89q.worldedit.internal.util.DeprecationUtil;
import com.sk89q.worldedit.internal.util.NonAbstractForCompatibility;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.DataException;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.chunk.Chunk;
import com.sk89q.worldedit.world.storage.ChunkStoreHelper;
import java.io.Closeable;
import java.io.IOException;
import org.enginehub.linbus.tree.LinCompoundTag;

public abstract class ChunkStore
implements Closeable {
    public static final int CHUNK_SHIFTS = 4;
    public static final int CHUNK_SHIFTS_Y = 8;

    public static BlockVector3 toChunk3d(BlockVector3 position) {
        return position.shr(4, 8, 4);
    }

    public static BlockVector2 toChunk(BlockVector3 position) {
        return BlockVector2.at(position.x() >> 4, position.z() >> 4);
    }

    @Deprecated
    public CompoundTag getChunkTag(BlockVector2 position, World world) throws DataException, IOException {
        return new CompoundTag(this.getChunkData(position, world));
    }

    @NonAbstractForCompatibility(delegateName="getChunkTag", delegateParams={BlockVector2.class, World.class})
    public LinCompoundTag getChunkData(BlockVector2 position, World world) throws DataException, IOException {
        DeprecationUtil.checkDelegatingOverride(this.getClass());
        return (LinCompoundTag)this.getChunkTag(position, world).toLinTag();
    }

    public Chunk getChunk(BlockVector2 position, World world) throws DataException, IOException {
        LinCompoundTag rootTag = this.getChunkData(position, world);
        return ChunkStoreHelper.getChunk(rootTag);
    }

    @Override
    public void close() throws IOException {
    }

    public abstract boolean isValid();
}

