/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 */
package com.sk89q.jnbt;

import com.sk89q.jnbt.ByteArrayTag;
import com.sk89q.jnbt.ByteTag;
import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.DoubleTag;
import com.sk89q.jnbt.EndTag;
import com.sk89q.jnbt.FloatTag;
import com.sk89q.jnbt.IntArrayTag;
import com.sk89q.jnbt.IntTag;
import com.sk89q.jnbt.ListTag;
import com.sk89q.jnbt.LongArrayTag;
import com.sk89q.jnbt.LongTag;
import com.sk89q.jnbt.NBTInputStream;
import com.sk89q.jnbt.NBTOutputStream;
import com.sk89q.jnbt.ShortTag;
import com.sk89q.jnbt.StringTag;
import com.sk89q.jnbt.Tag;
import java.io.DataOutputStream;
import org.enginehub.linbus.common.LinTagId;
import org.enginehub.linbus.stream.LinBinaryIO;
import org.enginehub.linbus.stream.LinStream;
import org.enginehub.linbus.tree.LinByteArrayTag;
import org.enginehub.linbus.tree.LinByteTag;
import org.enginehub.linbus.tree.LinCompoundTag;
import org.enginehub.linbus.tree.LinDoubleTag;
import org.enginehub.linbus.tree.LinFloatTag;
import org.enginehub.linbus.tree.LinIntArrayTag;
import org.enginehub.linbus.tree.LinIntTag;
import org.enginehub.linbus.tree.LinListTag;
import org.enginehub.linbus.tree.LinLongArrayTag;
import org.enginehub.linbus.tree.LinLongTag;
import org.enginehub.linbus.tree.LinShortTag;
import org.enginehub.linbus.tree.LinStringTag;
import org.enginehub.linbus.tree.LinTag;

@Deprecated
public class LinBusConverter {
    public static <V, LT extends LinTag<? extends V>> Tag<V, LT> toJnbtTag(LT tag) {
        return switch (tag.type().id()) {
            default -> throw new MatchException(null, null);
            case LinTagId.BYTE_ARRAY -> new ByteArrayTag((LinByteArrayTag)tag);
            case LinTagId.BYTE -> new ByteTag((LinByteTag)tag);
            case LinTagId.COMPOUND -> new CompoundTag((LinCompoundTag)tag);
            case LinTagId.DOUBLE -> new DoubleTag((LinDoubleTag)tag);
            case LinTagId.END -> new EndTag();
            case LinTagId.FLOAT -> new FloatTag((LinFloatTag)tag);
            case LinTagId.INT_ARRAY -> new IntArrayTag((LinIntArrayTag)tag);
            case LinTagId.INT -> new IntTag((LinIntTag)tag);
            case LinTagId.LIST -> new ListTag((LinListTag)tag);
            case LinTagId.LONG_ARRAY -> new LongArrayTag((LinLongArrayTag)tag);
            case LinTagId.LONG -> new LongTag((LinLongTag)tag);
            case LinTagId.SHORT -> new ShortTag((LinShortTag)tag);
            case LinTagId.STRING -> new StringTag((LinStringTag)tag);
        };
    }

    public static LinStream convertStream(NBTInputStream inputStream) {
        return LinBinaryIO.read(inputStream.is);
    }

    public static DataOutputStream convertStream(NBTOutputStream outputStream) {
        return outputStream.os;
    }

    private LinBusConverter() {
    }
}

