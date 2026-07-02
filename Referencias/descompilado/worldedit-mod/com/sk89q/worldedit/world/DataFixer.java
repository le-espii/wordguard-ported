/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.Beta
 */
package com.sk89q.worldedit.world;

import com.google.common.annotations.Beta;
import org.enginehub.linbus.tree.LinCompoundTag;

@Beta
public interface DataFixer {
    default public <T> T fixUp(FixType<T> type, T original) {
        return this.fixUp(type, original, -1);
    }

    public <T> T fixUp(FixType<T> var1, T var2, int var3);

    public static final class FixType<T> {
        private FixType() {
        }
    }

    public static final class FixTypes {
        public static final FixType<LinCompoundTag> CHUNK = new FixType();
        public static final FixType<LinCompoundTag> BLOCK_ENTITY = new FixType();
        public static final FixType<LinCompoundTag> ENTITY = new FixType();
        public static final FixType<String> BLOCK_STATE = new FixType();
        public static final FixType<String> BIOME = new FixType();
        public static final FixType<String> ITEM_TYPE = new FixType();

        private FixTypes() {
        }
    }
}

