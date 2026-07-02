/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extent.clipboard.io;

import com.sk89q.jnbt.LinBusConverter;
import com.sk89q.jnbt.NBTOutputStream;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.extent.clipboard.io.sponge.SpongeSchematicV2Writer;

@Deprecated
public class SpongeSchematicWriter
extends SpongeSchematicV2Writer
implements ClipboardWriter {
    public SpongeSchematicWriter(NBTOutputStream outputStream) {
        super(LinBusConverter.convertStream(outputStream));
    }
}

