/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.world.chunk;

import com.sk89q.jnbt.CompoundTag;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.DataException;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldedit.world.chunk.Chunk;
import com.sk89q.worldedit.world.registry.LegacyMapper;
import com.sk89q.worldedit.world.storage.InvalidFormatException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.enginehub.linbus.tree.LinCompoundTag;
import org.enginehub.linbus.tree.LinIntTag;
import org.enginehub.linbus.tree.LinListTag;
import org.enginehub.linbus.tree.LinTag;
import org.enginehub.linbus.tree.LinTagType;

public class OldChunk
implements Chunk {
    private final LinCompoundTag rootTag;
    private final byte[] blocks;
    private final byte[] data;
    private final int rootX;
    private final int rootZ;
    private Map<BlockVector3, LinCompoundTag> tileEntities;

    @Deprecated
    public OldChunk(CompoundTag tag) throws DataException {
        this((LinCompoundTag)tag.toLinTag());
    }

    public OldChunk(LinCompoundTag tag) throws DataException {
        this.rootTag = tag;
        this.blocks = this.rootTag.getTag("Blocks", LinTagType.byteArrayTag()).value();
        this.data = this.rootTag.getTag("Data", LinTagType.byteArrayTag()).value();
        this.rootX = this.rootTag.getTag("xPos", LinTagType.intTag()).value();
        this.rootZ = this.rootTag.getTag("zPos", LinTagType.intTag()).value();
        int size = 32768;
        if (this.blocks.length != size) {
            throw new InvalidFormatException("Chunk blocks byte array expected to be " + size + " bytes; found " + this.blocks.length);
        }
        if (this.data.length != size / 2) {
            throw new InvalidFormatException("Chunk block data byte array expected to be " + size + " bytes; found " + this.data.length);
        }
    }

    private void populateTileEntities() throws DataException {
        LinListTag<LinCompoundTag> tags = this.rootTag.getTag("TileEntities", LinTagType.listTag()).asTypeChecked(LinTagType.compoundTag());
        this.tileEntities = new HashMap<BlockVector3, LinCompoundTag>();
        Iterator iterator = tags.value().iterator();
        while (iterator.hasNext()) {
            LinCompoundTag t = (LinCompoundTag)iterator.next();
            int x = 0;
            int y = 0;
            int z = 0;
            LinCompoundTag.Builder values = LinCompoundTag.builder();
            for (String key : t.value().keySet()) {
                LinTag value = (LinTag)t.value().get(key);
                switch (key) {
                    case "x": {
                        if (!(value instanceof LinIntTag)) break;
                        LinIntTag v = (LinIntTag)value;
                        x = v.valueAsInt();
                        break;
                    }
                    case "y": {
                        if (!(value instanceof LinIntTag)) break;
                        LinIntTag v = (LinIntTag)value;
                        y = v.valueAsInt();
                        break;
                    }
                    case "z": {
                        if (!(value instanceof LinIntTag)) break;
                        LinIntTag v = (LinIntTag)value;
                        z = v.valueAsInt();
                        break;
                    }
                }
                values.put(key, value);
            }
            BlockVector3 vec = BlockVector3.at(x, y, z);
            this.tileEntities.put(vec, values.build());
        }
    }

    private LinCompoundTag getBlockTileEntity(BlockVector3 position) throws DataException {
        if (this.tileEntities == null) {
            this.populateTileEntities();
        }
        return this.tileEntities.get((Object)position);
    }

    @Override
    public BaseBlock getBlock(BlockVector3 position) throws DataException {
        int dataVal;
        byte id;
        if (position.y() >= 128) {
            return BlockTypes.VOID_AIR.getDefaultState().toBaseBlock();
        }
        int x = position.x() - this.rootX * 16;
        int y = position.y();
        int z = position.z() - this.rootZ * 16;
        int index = y + (z * 128 + x * 128 * 16);
        try {
            id = this.blocks[index];
        }
        catch (IndexOutOfBoundsException e) {
            throw new DataException("Chunk does not contain position " + String.valueOf((Object)position));
        }
        boolean shift = index % 2 == 0;
        index /= 2;
        try {
            dataVal = !shift ? (this.data[index] & 0xF0) >> 4 : this.data[index] & 0xF;
        }
        catch (IndexOutOfBoundsException e) {
            throw new DataException("Chunk does not contain position " + String.valueOf((Object)position));
        }
        BlockState state = LegacyMapper.getInstance().getBlockFromLegacy(id, dataVal);
        if (state == null) {
            WorldEdit.logger.warn("Unknown legacy block " + id + ":" + dataVal + " found when loading legacy anvil chunk.");
            return BlockTypes.AIR.getDefaultState().toBaseBlock();
        }
        LinCompoundTag tileEntity = this.getBlockTileEntity(position);
        return state.toBaseBlock(tileEntity);
    }
}

