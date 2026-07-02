/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.extent.clipboard.io;

import com.sk89q.jnbt.Tag;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import java.io.IOException;
import java.util.Map;
import javax.annotation.Nullable;

@Deprecated
public abstract class NBTSchematicReader
implements ClipboardReader {
    protected static <T extends Tag<?, ?>> T requireTag(Map<String, Tag<?, ?>> items, String key, Class<T> expected) throws IOException {
        if (!items.containsKey(key)) {
            throw new IOException("Schematic file is missing a \"" + key + "\" tag of type " + expected.getName());
        }
        Tag<?, ?> tag = items.get(key);
        if (!expected.isInstance(tag)) {
            throw new IOException(key + " tag is not of tag type " + expected.getName() + ", got " + tag.getClass().getName() + " instead");
        }
        return (T)((Tag)expected.cast(tag));
    }

    @Nullable
    protected static <T extends Tag<?, ?>> T getTag(Map<String, Tag<?, ?>> items, String key, Class<T> expected) {
        if (!items.containsKey(key)) {
            return null;
        }
        Tag<?, ?> test = items.get(key);
        if (!expected.isInstance(test)) {
            return null;
        }
        return (T)((Tag)expected.cast(test));
    }
}

