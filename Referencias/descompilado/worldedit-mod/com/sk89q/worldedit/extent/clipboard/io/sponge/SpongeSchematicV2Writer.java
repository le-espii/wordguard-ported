/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMaps
 */
package com.sk89q.worldedit.extent.clipboard.io.sponge;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.extension.platform.Platform;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.extent.clipboard.io.sponge.WriterUtil;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.block.BaseBlock;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.enginehub.linbus.stream.LinBinaryIO;
import org.enginehub.linbus.tree.LinCompoundTag;
import org.enginehub.linbus.tree.LinListTag;
import org.enginehub.linbus.tree.LinRootEntry;
import org.enginehub.linbus.tree.LinTagType;

public class SpongeSchematicV2Writer
implements ClipboardWriter {
    private static final int CURRENT_VERSION = 2;
    private static final int MAX_SIZE = 65535;
    private final DataOutputStream outputStream;

    public SpongeSchematicV2Writer(DataOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void write(Clipboard clipboard) throws IOException {
        LinBinaryIO.write(this.outputStream, new LinRootEntry("Schematic", this.write2(clipboard)));
    }

    private LinCompoundTag write2(Clipboard clipboard) {
        LinListTag<LinCompoundTag> value;
        Region region = clipboard.getRegion();
        BlockVector3 origin = clipboard.getOrigin();
        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 offset = min.subtract(origin);
        int width = region.getWidth();
        int height = region.getHeight();
        int length = region.getLength();
        if (width > 65535) {
            throw new IllegalArgumentException("Width of region too large for a .schematic");
        }
        if (height > 65535) {
            throw new IllegalArgumentException("Height of region too large for a .schematic");
        }
        if (length > 65535) {
            throw new IllegalArgumentException("Length of region too large for a .schematic");
        }
        LinCompoundTag.Builder schematic = LinCompoundTag.builder();
        schematic.putInt("Version", 2);
        schematic.putInt("DataVersion", WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.WORLD_EDITING).getDataVersion());
        LinCompoundTag.Builder metadata = LinCompoundTag.builder();
        metadata.putInt("WEOffsetX", offset.x());
        metadata.putInt("WEOffsetY", offset.y());
        metadata.putInt("WEOffsetZ", offset.z());
        LinCompoundTag.Builder worldEditSection = LinCompoundTag.builder();
        worldEditSection.putString("Version", WorldEdit.getVersion());
        worldEditSection.putString("EditingPlatform", WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.WORLD_EDITING).id());
        worldEditSection.putIntArray("Offset", new int[]{offset.x(), offset.y(), offset.z()});
        LinCompoundTag.Builder platformsSection = LinCompoundTag.builder();
        for (Platform platform : WorldEdit.getInstance().getPlatformManager().getPlatforms()) {
            platformsSection.put(platform.id(), LinCompoundTag.builder().putString("Name", platform.getPlatformName()).putString("Version", platform.getPlatformVersion()).build());
        }
        worldEditSection.put("Platforms", platformsSection.build());
        metadata.put("WorldEdit", worldEditSection.build());
        schematic.put("Metadata", metadata.build());
        schematic.putShort("Width", (short)width);
        schematic.putShort("Height", (short)height);
        schematic.putShort("Length", (short)length);
        schematic.putIntArray("Offset", new int[]{min.x(), min.y(), min.z()});
        int paletteMax = 0;
        Object2IntLinkedOpenHashMap palette = new Object2IntLinkedOpenHashMap();
        LinListTag.Builder<LinCompoundTag> tileEntities = LinListTag.builder(LinTagType.compoundTag());
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(width * height * length);
        for (int y = 0; y < height; ++y) {
            int y0 = min.y() + y;
            for (int z = 0; z < length; ++z) {
                int z0 = min.z() + z;
                for (int x = 0; x < width; ++x) {
                    int blockId;
                    String blockKey;
                    int x0 = min.x() + x;
                    BlockVector3 point = BlockVector3.at(x0, y0, z0);
                    BaseBlock block = clipboard.getFullBlock(point);
                    LinCompoundTag nbt = block.getNbt();
                    if (nbt != null) {
                        LinCompoundTag.Builder values = nbt.toBuilder();
                        values.remove("id");
                        values.remove("x");
                        values.remove("y");
                        values.remove("z");
                        values.putString("Id", block.getNbtId());
                        values.putIntArray("Pos", new int[]{x, y, z});
                        tileEntities.add(values.build());
                    }
                    if (palette.containsKey((Object)(blockKey = block.toImmutableState().getAsString()))) {
                        blockId = palette.getInt((Object)blockKey);
                    } else {
                        blockId = paletteMax++;
                        palette.put((Object)blockKey, blockId);
                    }
                    while ((blockId & 0xFFFFFF80) != 0) {
                        buffer.write(blockId & 0x7F | 0x80);
                        blockId >>>= 7;
                    }
                    buffer.write(blockId);
                }
            }
        }
        schematic.putInt("PaletteMax", paletteMax);
        LinCompoundTag.Builder paletteTag = LinCompoundTag.builder();
        Object2IntMaps.fastForEach((Object2IntMap)palette, e -> paletteTag.putInt((String)e.getKey(), e.getIntValue()));
        schematic.put("Palette", paletteTag.build());
        schematic.putByteArray("BlockData", buffer.toByteArray());
        schematic.put("BlockEntities", tileEntities.build());
        if (clipboard.hasBiomes()) {
            this.writeBiomes(clipboard, schematic);
        }
        if (!clipboard.getEntities().isEmpty() && (value = WriterUtil.encodeEntities(clipboard, false)) != null) {
            schematic.put("Entities", value);
        }
        return schematic.build();
    }

    private void writeBiomes(Clipboard clipboard, LinCompoundTag.Builder schematic) {
        BlockVector3 min = clipboard.getMinimumPoint();
        int width = clipboard.getRegion().getWidth();
        int length = clipboard.getRegion().getLength();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(width * length);
        int paletteMax = 0;
        Object2IntLinkedOpenHashMap palette = new Object2IntLinkedOpenHashMap();
        for (int z = 0; z < length; ++z) {
            int z0 = min.z() + z;
            for (int x = 0; x < width; ++x) {
                int biomeId;
                int x0 = min.x() + x;
                BlockVector3 pt = BlockVector3.at(x0, min.y(), z0);
                BiomeType biome = clipboard.getBiome(pt);
                String biomeKey = biome.id();
                if (palette.containsKey((Object)biomeKey)) {
                    biomeId = palette.getInt((Object)biomeKey);
                } else {
                    biomeId = paletteMax++;
                    palette.put((Object)biomeKey, biomeId);
                }
                while ((biomeId & 0xFFFFFF80) != 0) {
                    buffer.write(biomeId & 0x7F | 0x80);
                    biomeId >>>= 7;
                }
                buffer.write(biomeId);
            }
        }
        schematic.putInt("BiomePaletteMax", paletteMax);
        LinCompoundTag.Builder paletteTag = LinCompoundTag.builder();
        Object2IntMaps.fastForEach((Object2IntMap)palette, e -> paletteTag.putInt((String)e.getKey(), e.getIntValue()));
        schematic.put("BiomePalette", paletteTag.build());
        schematic.putByteArray("BiomeData", buffer.toByteArray());
    }

    @Override
    public void close() throws IOException {
        this.outputStream.close();
    }
}

