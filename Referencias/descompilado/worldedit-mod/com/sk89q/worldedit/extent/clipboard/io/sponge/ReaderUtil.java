/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.Logger
 */
package com.sk89q.worldedit.extent.clipboard.io.sponge;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.extension.platform.Platform;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.sponge.VersionedDataFixer;
import com.sk89q.worldedit.internal.util.LogManagerCompat;
import com.sk89q.worldedit.internal.util.VarIntIterator;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.concurrency.LazyReference;
import com.sk89q.worldedit.world.DataFixer;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.biome.BiomeTypes;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldedit.world.entity.EntityType;
import com.sk89q.worldedit.world.entity.EntityTypes;
import com.sk89q.worldedit.world.storage.NBTConversions;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.logging.log4j.Logger;
import org.enginehub.linbus.tree.LinCompoundTag;
import org.enginehub.linbus.tree.LinIntArrayTag;
import org.enginehub.linbus.tree.LinIntTag;
import org.enginehub.linbus.tree.LinListTag;
import org.enginehub.linbus.tree.LinTag;
import org.enginehub.linbus.tree.LinTagType;

public class ReaderUtil {
    private static final Logger LOGGER = LogManagerCompat.getLogger();

    static void checkSchematicVersion(int version, LinCompoundTag schematicTag) throws IOException {
        int schematicVersion = ReaderUtil.getSchematicVersion(schematicTag);
        Preconditions.checkState((version == schematicVersion ? 1 : 0) != 0, (String)"Schematic is not version %s, but %s", (int)version, (int)schematicVersion);
    }

    public static int getSchematicVersion(LinCompoundTag schematicTag) throws IOException {
        return schematicTag.getTag("Version", LinTagType.intTag()).valueAsInt();
    }

    static VersionedDataFixer getVersionedDataFixer(LinCompoundTag schematic, Platform platform, int liveDataVersion) {
        DataFixer fixer = null;
        int dataVersion = schematic.getTag("DataVersion", LinTagType.intTag()).valueAsInt();
        if (dataVersion < 0) {
            LOGGER.warn("Schematic has an unknown data version ({}). Data may be incompatible.", (Object)dataVersion);
        } else if (dataVersion > liveDataVersion) {
            LOGGER.warn("Schematic was made in a newer Minecraft version ({} > {}). Data may be incompatible.", (Object)dataVersion, (Object)liveDataVersion);
        } else if (dataVersion < liveDataVersion) {
            fixer = platform.getDataFixer();
            if (fixer != null) {
                LOGGER.debug("Schematic was made in an older Minecraft version ({} < {}), will attempt DFU.", (Object)dataVersion, (Object)liveDataVersion);
            } else {
                LOGGER.info("Schematic was made in an older Minecraft version ({} < {}), but DFU is not available. Data may be incompatible.", (Object)dataVersion, (Object)liveDataVersion);
            }
        }
        return new VersionedDataFixer(dataVersion, fixer);
    }

    static Map<Integer, BlockState> decodePalette(LinCompoundTag paletteObject, VersionedDataFixer fixer) throws IOException {
        HashMap<Integer, BlockState> palette = new HashMap<Integer, BlockState>();
        ParserContext parserContext = new ParserContext();
        parserContext.setRestricted(false);
        parserContext.setTryLegacy(false);
        parserContext.setPreferringWildcard(false);
        for (Map.Entry palettePart : paletteObject.value().entrySet()) {
            BlockState state;
            Object v = palettePart.getValue();
            if (!(v instanceof LinIntTag)) {
                throw new IOException("Invalid palette entry: " + String.valueOf(palettePart));
            }
            LinIntTag idTag = (LinIntTag)v;
            int id = idTag.valueAsInt();
            String paletteName = fixer.fixUp(DataFixer.FixTypes.BLOCK_STATE, (String)palettePart.getKey());
            try {
                state = ((BaseBlock)WorldEdit.getInstance().getBlockFactory().parseFromInput(paletteName, parserContext)).toImmutableState();
            }
            catch (InputParseException e) {
                LOGGER.warn("Invalid BlockState in palette: " + String.valueOf(palettePart) + ". Block will be replaced with air.");
                state = BlockTypes.AIR.getDefaultState();
            }
            palette.put(id, state);
        }
        return palette;
    }

    static void initializeClipboardFromBlocks(Clipboard clipboard, Map<Integer, BlockState> palette, byte[] blocks, LinListTag<LinCompoundTag> tileEntities, VersionedDataFixer fixer, boolean dataIsNested) throws IOException {
        HashMap<BlockVector3, LinCompoundTag> tileEntitiesMap = new HashMap<BlockVector3, LinCompoundTag>();
        if (tileEntities != null) {
            Iterator iterator = tileEntities.value().iterator();
            while (iterator.hasNext()) {
                LinCompoundTag tileEntity = (LinCompoundTag)iterator.next();
                BlockVector3 pt = clipboard.getMinimumPoint().add(ReaderUtil.decodeBlockVector3(tileEntity.getTag("Pos", LinTagType.intArrayTag())));
                LinCompoundTag.Builder values = ReaderUtil.extractData(dataIsNested, tileEntity);
                values.putInt("x", pt.x());
                values.putInt("y", pt.y());
                values.putInt("z", pt.z());
                values.put("id", (LinTag)tileEntity.value().get("Id"));
                tileEntity = fixer.isActive() ? fixer.fixUp(DataFixer.FixTypes.BLOCK_ENTITY, values.build()) : values.build();
                tileEntitiesMap.put(pt, tileEntity);
            }
        }
        int width = clipboard.getRegion().getWidth();
        int length = clipboard.getRegion().getLength();
        int index = 0;
        VarIntIterator iter = new VarIntIterator(blocks);
        while (iter.hasNext()) {
            int nextBlockId = iter.nextInt();
            BlockState state = palette.get(nextBlockId);
            BlockVector3 rawPos = ReaderUtil.decodePositionFromDataIndex(width, length, index);
            try {
                BlockVector3 offsetPos = clipboard.getMinimumPoint().add(rawPos);
                LinCompoundTag tileEntity = (LinCompoundTag)tileEntitiesMap.get((Object)offsetPos);
                clipboard.setBlock(offsetPos, state.toBaseBlock(tileEntity));
            }
            catch (WorldEditException e) {
                throw new IOException("Failed to load a block in the schematic", e);
            }
            ++index;
        }
    }

    private static LinCompoundTag.Builder extractData(boolean dataIsNested, LinCompoundTag tag) {
        if (dataIsNested) {
            LinCompoundTag dataTag = tag.findTag("Data", LinTagType.compoundTag());
            return dataTag != null ? dataTag.toBuilder() : LinCompoundTag.builder();
        }
        LinCompoundTag.Builder values = tag.toBuilder();
        values.remove("Id");
        values.remove("Pos");
        return values;
    }

    static BlockVector3 decodePositionFromDataIndex(int width, int length, int index) {
        int y = index / (width * length);
        int remainder = index - y * width * length;
        int z = remainder / width;
        int x = remainder - z * width;
        return BlockVector3.at(x, y, z);
    }

    static BlockVector3 decodeBlockVector3(@Nullable LinIntArrayTag tag) throws IOException {
        if (tag == null) {
            return BlockVector3.ZERO;
        }
        int[] parts = tag.value();
        if (parts.length != 3) {
            throw new IOException("Invalid block vector specified in schematic.");
        }
        return BlockVector3.at(parts[0], parts[1], parts[2]);
    }

    static void readEntities(BlockArrayClipboard clipboard, List<? extends LinCompoundTag> entList, VersionedDataFixer fixer, boolean positionIsRelative) {
        if (entList.isEmpty()) {
            return;
        }
        for (LinCompoundTag linCompoundTag : entList) {
            String id = linCompoundTag.getTag("Id", LinTagType.stringTag()).value();
            LinCompoundTag.Builder values = ReaderUtil.extractData(positionIsRelative, linCompoundTag);
            LinCompoundTag dataTag = values.putString("id", id).build();
            dataTag = fixer.fixUp(DataFixer.FixTypes.ENTITY, dataTag);
            EntityType entityType = EntityTypes.get(id);
            if (entityType != null) {
                Location location = NBTConversions.toLocation((Extent)clipboard, linCompoundTag.getListTag("Pos", LinTagType.doubleTag()), dataTag.getListTag("Rotation", LinTagType.floatTag()));
                BaseEntity state = new BaseEntity(entityType, LazyReference.computed(dataTag));
                if (positionIsRelative) {
                    location = location.setPosition(location.toVector().add(clipboard.getMinimumPoint().toVector3()));
                }
                clipboard.createEntity(location, state);
                continue;
            }
            LOGGER.warn("Unknown entity when pasting schematic: " + id);
        }
    }

    static Int2ObjectMap<BiomeType> readBiomePalette(VersionedDataFixer fixer, LinCompoundTag paletteTag, Logger logger) throws IOException {
        Int2ObjectLinkedOpenHashMap palette = new Int2ObjectLinkedOpenHashMap(paletteTag.value().size());
        for (Map.Entry palettePart : paletteTag.value().entrySet()) {
            Object v;
            String key = (String)palettePart.getKey();
            BiomeType biome = BiomeTypes.get(key = fixer.fixUp(DataFixer.FixTypes.BIOME, key));
            if (biome == null) {
                logger.warn("Unknown biome type :" + key + " in palette. Are you missing a mod or using a schematic made in a newer version of Minecraft?");
            }
            if (!((v = palettePart.getValue()) instanceof LinIntTag)) {
                throw new IOException("Biome mapped to non-Int tag.");
            }
            LinIntTag idTag = (LinIntTag)v;
            palette.put(idTag.valueAsInt(), (Object)biome);
        }
        return palette;
    }

    private ReaderUtil() {
    }
}

