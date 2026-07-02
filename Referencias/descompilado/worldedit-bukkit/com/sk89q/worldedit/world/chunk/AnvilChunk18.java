/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.world.chunk;

import com.sk89q.jnbt.CompoundTag;
import com.sk89q.worldedit.bukkit.fastutil.ints.Int2ObjectOpenHashMap;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.world.DataException;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldedit.world.chunk.Chunk;
import com.sk89q.worldedit.world.chunk.PackedIntArrayReader;
import com.sk89q.worldedit.world.storage.InvalidFormatException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import org.enginehub.linbus.tree.LinCompoundTag;
import org.enginehub.linbus.tree.LinListTag;
import org.enginehub.linbus.tree.LinStringTag;
import org.enginehub.linbus.tree.LinTag;
import org.enginehub.linbus.tree.LinTagType;

public class AnvilChunk18
implements Chunk {
    private final LinCompoundTag rootTag;
    private final Int2ObjectOpenHashMap<BlockState[]> blocks;
    private final int rootX;
    private final int rootZ;
    private Map<BlockVector3, LinCompoundTag> tileEntities;

    @Deprecated
    public AnvilChunk18(CompoundTag tag) throws DataException {
        this((LinCompoundTag)tag.toLinTag());
    }

    public AnvilChunk18(LinCompoundTag tag) throws DataException {
        this.rootTag = tag;
        this.rootX = this.rootTag.getTag("xPos", LinTagType.intTag()).valueAsInt();
        this.rootZ = this.rootTag.getTag("zPos", LinTagType.intTag()).valueAsInt();
        Object sections = this.rootTag.getListTag("sections", LinTagType.compoundTag()).value();
        this.blocks = new Int2ObjectOpenHashMap(sections.size());
        Iterator iterator = sections.iterator();
        while (iterator.hasNext()) {
            Object paletteEntries;
            int paletteSize;
            LinCompoundTag sectionTag = (LinCompoundTag)iterator.next();
            Object yValue = ((LinTag)sectionTag.value().get("Y")).value();
            if (!(yValue instanceof Number)) {
                throw new InvalidFormatException("Y is not numeric: " + String.valueOf(yValue));
            }
            Number yNumber = (Number)yValue;
            int y = yNumber.intValue();
            LinCompoundTag blockStatesTag = sectionTag.findTag("block_states", LinTagType.compoundTag());
            if (blockStatesTag == null || (paletteSize = (paletteEntries = blockStatesTag.getListTag("palette", LinTagType.compoundTag()).value()).size()) == 0) continue;
            BlockState[] palette = new BlockState[paletteSize];
            for (int paletteEntryId = 0; paletteEntryId < paletteSize; ++paletteEntryId) {
                LinCompoundTag paletteEntry = (LinCompoundTag)paletteEntries.get(paletteEntryId);
                String typeString = paletteEntry.getTag("Name", LinTagType.stringTag()).value();
                BlockType type = BlockTypes.get(typeString);
                if (type == null) {
                    throw new InvalidFormatException("Invalid block type: " + typeString);
                }
                BlockState blockState = type.getDefaultState();
                LinCompoundTag properties = paletteEntry.findTag("Properties", LinTagType.compoundTag());
                if (properties != null) {
                    for (Property<?> property : blockState.getStates().keySet()) {
                        LinStringTag name = properties.findTag(property.getName(), LinTagType.stringTag());
                        if (name == null) continue;
                        String value = name.value();
                        try {
                            blockState = this.getBlockStateWith(blockState, property, value);
                        }
                        catch (IllegalArgumentException e) {
                            throw new InvalidFormatException("Invalid block state for " + blockState.getBlockType().id() + ", " + property.getName() + ": " + value);
                        }
                    }
                }
                palette[paletteEntryId] = blockState;
            }
            if (paletteSize == 1) {
                this.blocks.put(y, palette);
                continue;
            }
            long[] blockStatesSerialized = blockStatesTag.getTag("data", LinTagType.longArrayTag()).value();
            BlockState[] chunkSectionBlocks = new BlockState[4096];
            this.blocks.put(y, chunkSectionBlocks);
            this.readBlockStates(palette, blockStatesSerialized, chunkSectionBlocks);
        }
    }

    protected void readBlockStates(BlockState[] palette, long[] blockStatesSerialized, BlockState[] chunkSectionBlocks) throws InvalidFormatException {
        PackedIntArrayReader reader = new PackedIntArrayReader(blockStatesSerialized);
        for (int blockPos = 0; blockPos < chunkSectionBlocks.length; ++blockPos) {
            int index = reader.get(blockPos);
            if (index >= palette.length) {
                throw new InvalidFormatException("Invalid block state table entry: " + index);
            }
            chunkSectionBlocks[blockPos] = palette[index];
        }
    }

    private <T> BlockState getBlockStateWith(BlockState source, Property<T> property, String value) {
        return source.with(property, property.getValueFor(value));
    }

    private void populateTileEntities() throws DataException {
        this.tileEntities = new HashMap<BlockVector3, LinCompoundTag>();
        LinListTag<LinCompoundTag> tags = this.rootTag.findListTag("block_entities", LinTagType.compoundTag());
        if (tags == null) {
            return;
        }
        Iterator iterator = tags.value().iterator();
        while (iterator.hasNext()) {
            LinCompoundTag tag = (LinCompoundTag)iterator.next();
            int x = tag.getTag("x", LinTagType.intTag()).valueAsInt();
            int y = tag.getTag("y", LinTagType.intTag()).valueAsInt();
            int z = tag.getTag("z", LinTagType.intTag()).valueAsInt();
            BlockVector3 vec = BlockVector3.at(x, y, z);
            this.tileEntities.put(vec, tag);
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
        int x = position.x() - this.rootX * 16;
        int y = position.y();
        int z = position.z() - this.rootZ * 16;
        int section = y >> 4;
        int yIndex = y & 0xF;
        BlockState[] sectionBlocks = this.blocks.get(section);
        if (sectionBlocks == null) {
            return BlockTypes.AIR.getDefaultState().toBaseBlock();
        }
        BlockState state = sectionBlocks[sectionBlocks.length == 1 ? 0 : yIndex << 8 | z << 4 | x];
        LinCompoundTag tileEntity = this.getBlockTileEntity(position);
        if (tileEntity != null) {
            return state.toBaseBlock(tileEntity);
        }
        return state.toBaseBlock();
    }
}

