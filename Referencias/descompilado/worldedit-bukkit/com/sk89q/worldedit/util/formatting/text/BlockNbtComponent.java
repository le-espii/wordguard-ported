/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.sk89q.worldedit.util.formatting.text;

import com.sk89q.worldedit.util.formatting.text.AbstractComponentBuilder;
import com.sk89q.worldedit.util.formatting.text.BlockNbtComponentImpl;
import com.sk89q.worldedit.util.formatting.text.NbtComponent;
import com.sk89q.worldedit.util.formatting.text.NbtComponentBuilder;
import com.sk89q.worldedit.util.formatting.text.ScopedComponent;
import java.util.function.Consumer;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface BlockNbtComponent
extends NbtComponent<BlockNbtComponent, Builder>,
ScopedComponent<BlockNbtComponent> {
    public static @NonNull Builder builder() {
        return new BlockNbtComponentImpl.BuilderImpl();
    }

    public static @NonNull BlockNbtComponent of(@NonNull String nbtPath, @NonNull Pos pos) {
        return (BlockNbtComponent)((Builder)BlockNbtComponent.builder().nbtPath(nbtPath)).pos(pos).build();
    }

    public static @NonNull BlockNbtComponent make(@NonNull Consumer<? super Builder> consumer) {
        Builder builder = BlockNbtComponent.builder();
        return (BlockNbtComponent)AbstractComponentBuilder.configureAndBuild(builder, consumer);
    }

    public @NonNull Pos pos();

    public @NonNull BlockNbtComponent pos(@NonNull Pos var1);

    default public @NonNull BlockNbtComponent localPos(double left, double up, double forwards) {
        return this.pos(LocalPos.of(left, up, forwards));
    }

    default public @NonNull BlockNbtComponent worldPos(@NonNull WorldPos.Coordinate x, @NonNull WorldPos.Coordinate y, @NonNull WorldPos.Coordinate z) {
        return this.pos(WorldPos.of(x, y, z));
    }

    default public @NonNull BlockNbtComponent absoluteWorldPos(int x, int y, int z) {
        return this.worldPos(WorldPos.Coordinate.absolute(x), WorldPos.Coordinate.absolute(y), WorldPos.Coordinate.absolute(z));
    }

    default public @NonNull BlockNbtComponent relativeWorldPos(int x, int y, int z) {
        return this.worldPos(WorldPos.Coordinate.relative(x), WorldPos.Coordinate.relative(y), WorldPos.Coordinate.relative(z));
    }

    public static interface WorldPos
    extends Pos {
        public static @NonNull WorldPos of(@NonNull Coordinate x, @NonNull Coordinate y, @NonNull Coordinate z) {
            return new BlockNbtComponentImpl.WorldPosImpl(x, y, z);
        }

        public @NonNull Coordinate x();

        public @NonNull Coordinate y();

        public @NonNull Coordinate z();

        public static interface Coordinate {
            public static @NonNull Coordinate absolute(int value) {
                return Coordinate.of(value, Type.ABSOLUTE);
            }

            public static @NonNull Coordinate relative(int value) {
                return Coordinate.of(value, Type.RELATIVE);
            }

            public static @NonNull Coordinate of(int value, @NonNull Type type) {
                return new BlockNbtComponentImpl.WorldPosImpl.CoordinateImpl(value, type);
            }

            public int value();

            public @NonNull Type type();

            public static enum Type {
                ABSOLUTE,
                RELATIVE;

            }
        }
    }

    public static interface LocalPos
    extends Pos {
        public static @NonNull LocalPos of(double left, double up, double forwards) {
            return new BlockNbtComponentImpl.LocalPosImpl(left, up, forwards);
        }

        public double left();

        public double up();

        public double forwards();
    }

    public static interface Pos {
    }

    public static interface Builder
    extends NbtComponentBuilder<BlockNbtComponent, Builder> {
        public @NonNull Builder pos(@NonNull Pos var1);

        default public @NonNull Builder localPos(double left, double up, double forwards) {
            return this.pos(LocalPos.of(left, up, forwards));
        }

        default public @NonNull Builder worldPos(@NonNull WorldPos.Coordinate x, @NonNull WorldPos.Coordinate y, @NonNull WorldPos.Coordinate z) {
            return this.pos(WorldPos.of(x, y, z));
        }

        default public @NonNull Builder absoluteWorldPos(int x, int y, int z) {
            return this.worldPos(WorldPos.Coordinate.absolute(x), WorldPos.Coordinate.absolute(y), WorldPos.Coordinate.absolute(z));
        }

        default public @NonNull Builder relativeWorldPos(int x, int y, int z) {
            return this.worldPos(WorldPos.Coordinate.relative(x), WorldPos.Coordinate.relative(y), WorldPos.Coordinate.relative(z));
        }
    }
}

