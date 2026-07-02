/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extent.clipboard.io.sponge;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.fastutil.objects.Object2IntLinkedOpenHashMap;
import com.sk89q.worldedit.bukkit.fastutil.objects.Object2IntMap;
import com.sk89q.worldedit.bukkit.fastutil.objects.Object2IntMaps;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.extension.platform.Platform;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.extent.clipboard.io.sponge.WriterUtil;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BaseBlock;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.function.Function;
import org.enginehub.linbus.stream.LinBinaryIO;
import org.enginehub.linbus.tree.LinCompoundTag;
import org.enginehub.linbus.tree.LinListTag;
import org.enginehub.linbus.tree.LinRootEntry;
import org.enginehub.linbus.tree.LinTagType;

public class SpongeSchematicV3Writer
implements ClipboardWriter {
    private static final int CURRENT_VERSION = 3;
    private static final int MAX_SIZE = 65535;
    private final DataOutputStream outputStream;

    public SpongeSchematicV3Writer(DataOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void write(Clipboard clipboard) throws IOException {
        LinBinaryIO.write(this.outputStream, new LinRootEntry("", LinCompoundTag.builder().put("Schematic", this.write3(clipboard)).build()));
    }

    private LinCompoundTag write3(Clipboard clipboard) {
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
        schematic.putInt("Version", 3);
        schematic.putInt("DataVersion", WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.WORLD_EDITING).getDataVersion());
        LinCompoundTag.Builder metadata = LinCompoundTag.builder();
        metadata.putLong("Date", System.currentTimeMillis());
        LinCompoundTag.Builder worldEditSection = LinCompoundTag.builder();
        worldEditSection.putString("Version", WorldEdit.getVersion());
        worldEditSection.putString("EditingPlatform", WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.WORLD_EDITING).id());
        worldEditSection.putIntArray("Origin", new int[]{origin.x(), origin.y(), origin.z()});
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
        schematic.putIntArray("Offset", new int[]{offset.x(), offset.y(), offset.z()});
        schematic.put("Blocks", this.encodeBlocks(clipboard));
        if (clipboard.hasBiomes()) {
            schematic.put("Biomes", this.encodeBiomes(clipboard));
        }
        if (!clipboard.getEntities().isEmpty() && (value = WriterUtil.encodeEntities(clipboard, true)) != null) {
            schematic.put("Entities", value);
        }
        return schematic.build();
    }

    private LinCompoundTag encodeBlocks(Clipboard clipboard) {
        LinListTag.Builder<LinCompoundTag> blockEntities = LinListTag.builder(LinTagType.compoundTag());
        LinCompoundTag.Builder result = this.encodePalettedData(clipboard, point -> {
            BaseBlock block = clipboard.getFullBlock((BlockVector3)((Object)point));
            LinCompoundTag nbt = block.getNbt();
            if (nbt != null) {
                LinCompoundTag.Builder builder = LinCompoundTag.builder();
                builder.putString("Id", block.getNbtId());
                BlockVector3 adjustedPos = point.subtract(clipboard.getMinimumPoint());
                builder.putIntArray("Pos", new int[]{adjustedPos.x(), adjustedPos.y(), adjustedPos.z()});
                builder.put("Data", nbt);
                blockEntities.add(builder.build());
            }
            return block.toImmutableState().getAsString();
        });
        return result.put("BlockEntities", blockEntities.build()).build();
    }

    private LinCompoundTag encodeBiomes(Clipboard clipboard) {
        return this.encodePalettedData(clipboard, point -> clipboard.getBiome((BlockVector3)((Object)point)).id()).build();
    }

    private LinCompoundTag.Builder encodePalettedData(Clipboard clipboard, Function<BlockVector3, String> keyFunction) {
        BlockVector3 min = clipboard.getMinimumPoint();
        int width = clipboard.getRegion().getWidth();
        int height = clipboard.getRegion().getHeight();
        int length = clipboard.getRegion().getLength();
        PaletteMap paletteMap = new PaletteMap();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(width * height * length);
        for (int y = 0; y < height; ++y) {
            for (int z = 0; z < length; ++z) {
                for (int x = 0; x < width; ++x) {
                    BlockVector3 point = min.add(x, y, z);
                    String key = keyFunction.apply(point);
                    int id = paletteMap.getId(key);
                    while ((id & 0xFFFFFF80) != 0) {
                        buffer.write(id & 0x7F | 0x80);
                        id >>>= 7;
                    }
                    buffer.write(id);
                }
            }
        }
        return LinCompoundTag.builder().put("Palette", paletteMap.toNbt()).putByteArray("Data", buffer.toByteArray());
    }

    @Override
    public void close() throws IOException {
        this.outputStream.close();
    }

    private static final class PaletteMap {
        private final Object2IntMap<String> contents = new Object2IntLinkedOpenHashMap<String>();
        private int nextId = 0;

        private PaletteMap() {
        }

        public int getId(String key) {
            int result = this.contents.getOrDefault((Object)key, -1);
            if (result != -1) {
                return result;
            }
            int newValue = this.nextId++;
            this.contents.put(key, newValue);
            return newValue;
        }

        public LinCompoundTag toNbt() {
            LinCompoundTag.Builder result = LinCompoundTag.builder();
            Object2IntMaps.fastForEach(this.contents, e -> result.putInt((String)e.getKey(), e.getIntValue()));
            return result.build();
        }
    }
}

