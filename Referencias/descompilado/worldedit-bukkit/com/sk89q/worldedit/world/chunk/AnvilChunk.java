/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
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
import javax.annotation.Nullable;
import org.enginehub.linbus.tree.LinByteArrayTag;
import org.enginehub.linbus.tree.LinByteTag;
import org.enginehub.linbus.tree.LinCompoundTag;
import org.enginehub.linbus.tree.LinIntTag;
import org.enginehub.linbus.tree.LinListTag;
import org.enginehub.linbus.tree.LinTag;
import org.enginehub.linbus.tree.LinTagType;

public class AnvilChunk
implements Chunk {
    private final LinCompoundTag rootTag;
    private final byte[][] blocks;
    private final byte[][] blocksAdd;
    private final byte[][] data;
    private final int rootX;
    private final int rootZ;
    private Map<BlockVector3, LinCompoundTag> tileEntities;

    @Deprecated
    public AnvilChunk(CompoundTag tag) throws DataException {
        this((LinCompoundTag)tag.toLinTag());
    }

    public AnvilChunk(LinCompoundTag tag) throws DataException {
        this.rootTag = tag;
        this.rootX = this.rootTag.getTag("xPos", LinTagType.intTag()).value();
        this.rootZ = this.rootTag.getTag("zPos", LinTagType.intTag()).value();
        this.blocks = new byte[16][4096];
        this.blocksAdd = new byte[16][2048];
        this.data = new byte[16][2048];
        LinListTag sections = this.rootTag.getTag("Sections", LinTagType.listTag());
        Iterator iterator = sections.value().iterator();
        while (iterator.hasNext()) {
            byte y;
            LinCompoundTag sectionTag;
            LinByteTag sectionYTag;
            LinTag rawSectionTag = (LinTag)iterator.next();
            if (!(rawSectionTag instanceof LinCompoundTag) || (sectionYTag = (sectionTag = (LinCompoundTag)rawSectionTag).findTag("Y", LinTagType.byteTag())) == null || (y = sectionYTag.value().byteValue()) < 0 || y >= 16) continue;
            this.blocks[y] = sectionTag.getTag("Blocks", LinTagType.byteArrayTag()).value();
            this.data[y] = sectionTag.getTag("Data", LinTagType.byteArrayTag()).value();
            LinByteArrayTag addTag = sectionTag.findTag("Add", LinTagType.byteArrayTag());
            if (addTag == null) continue;
            this.blocksAdd[y] = addTag.value();
        }
        int sectionsize = 4096;
        for (byte[] block : this.blocks) {
            if (block.length == sectionsize) continue;
            throw new InvalidFormatException("Chunk blocks byte array expected to be " + sectionsize + " bytes; found " + block.length);
        }
        for (byte[] aData : this.data) {
            if (aData.length == sectionsize / 2) continue;
            throw new InvalidFormatException("Chunk block data byte array expected to be " + sectionsize + " bytes; found " + aData.length);
        }
    }

    private int getBlockID(BlockVector3 position) throws DataException {
        int x = position.x() - this.rootX * 16;
        int y = position.y();
        int z = position.z() - this.rootZ * 16;
        int section = y >> 4;
        if (section < 0 || section >= this.blocks.length) {
            throw new DataException("Chunk does not contain position " + String.valueOf((Object)position));
        }
        int yindex = y & 0xF;
        int index = x + (z * 16 + yindex * 16 * 16);
        try {
            byte addByte = this.blocksAdd[section][index >> 1];
            int addId = (index & 1) == 0 ? (addByte & 0xF) << 8 : (addByte & 0xF0) << 4;
            return (this.blocks[section][index] & 0xFF) + addId;
        }
        catch (IndexOutOfBoundsException e) {
            throw new DataException("Chunk does not contain position " + String.valueOf((Object)position));
        }
    }

    private int getBlockData(BlockVector3 position) throws DataException {
        int x = position.x() - this.rootX * 16;
        int y = position.y();
        int z = position.z() - this.rootZ * 16;
        int section = y >> 4;
        int yIndex = y & 0xF;
        if (section < 0 || section >= this.blocks.length) {
            throw new DataException("Chunk does not contain position " + String.valueOf((Object)position));
        }
        int index = x + (z * 16 + yIndex * 16 * 16);
        boolean shift = (index & 1) != 0;
        index >>= 2;
        try {
            byte dataByte = this.data[section][index];
            return shift ? (dataByte & 0xF0) >> 4 : dataByte & 0xF;
        }
        catch (IndexOutOfBoundsException e) {
            throw new DataException("Chunk does not contain position " + String.valueOf((Object)position));
        }
    }

    private void populateTileEntities() {
        LinListTag<LinCompoundTag> tags = this.rootTag.getTag("TileEntities", LinTagType.listTag()).asTypeChecked(LinTagType.compoundTag());
        this.tileEntities = new HashMap<BlockVector3, LinCompoundTag>(tags.value().size());
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

    @Nullable
    private LinCompoundTag getBlockTileEntity(BlockVector3 position) throws DataException {
        if (this.tileEntities == null) {
            this.populateTileEntities();
        }
        return this.tileEntities.get((Object)position);
    }

    @Override
    public BaseBlock getBlock(BlockVector3 position) throws DataException {
        int id = this.getBlockID(position);
        int data = this.getBlockData(position);
        BlockState state = LegacyMapper.getInstance().getBlockFromLegacy(id, data);
        if (state == null) {
            WorldEdit.logger.warn("Unknown legacy block " + id + ":" + data + " found when loading legacy anvil chunk.");
            return BlockTypes.AIR.getDefaultState().toBaseBlock();
        }
        LinCompoundTag tileEntity = this.getBlockTileEntity(position);
        if (tileEntity != null) {
            return state.toBaseBlock(tileEntity);
        }
        return state.toBaseBlock();
    }
}

