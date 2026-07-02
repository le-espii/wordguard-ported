/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.sk89q.worldedit.util.formatting.text;

import com.sk89q.worldedit.util.formatting.text.BlockNbtComponent;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.NbtComponentImpl;
import com.sk89q.worldedit.util.formatting.text.format.Style;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

final class BlockNbtComponentImpl
extends NbtComponentImpl<BlockNbtComponent, BlockNbtComponent.Builder>
implements BlockNbtComponent {
    private final BlockNbtComponent.Pos pos;

    BlockNbtComponentImpl(@NonNull List<Component> children, @NonNull Style style, String nbtPath, boolean interpret, @NonNull BlockNbtComponent.Pos pos) {
        super(children, style, nbtPath, interpret);
        this.pos = pos;
    }

    @Override
    public @NonNull BlockNbtComponent nbtPath(@NonNull String nbtPath) {
        if (Objects.equals(this.nbtPath, nbtPath)) {
            return this;
        }
        return new BlockNbtComponentImpl(this.children, this.style, nbtPath, this.interpret, this.pos);
    }

    @Override
    public @NonNull BlockNbtComponent interpret(boolean interpret) {
        if (this.interpret == interpret) {
            return this;
        }
        return new BlockNbtComponentImpl(this.children, this.style, this.nbtPath, interpret, this.pos);
    }

    @Override
    public @NonNull BlockNbtComponent.Pos pos() {
        return this.pos;
    }

    @Override
    public @NonNull BlockNbtComponent pos(@NonNull BlockNbtComponent.Pos pos) {
        return new BlockNbtComponentImpl(this.children, this.style, this.nbtPath, this.interpret, pos);
    }

    @Override
    public @NonNull BlockNbtComponent children(@NonNull List<Component> children) {
        return new BlockNbtComponentImpl(children, this.style, this.nbtPath, this.interpret, this.pos);
    }

    @Override
    public @NonNull BlockNbtComponent style(@NonNull Style style) {
        if (Objects.equals(this.style, style)) {
            return this;
        }
        return new BlockNbtComponentImpl(this.children, style, this.nbtPath, this.interpret, this.pos);
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof BlockNbtComponent)) {
            return false;
        }
        if (!super.equals(other)) {
            return false;
        }
        BlockNbtComponent that = (BlockNbtComponent)other;
        return Objects.equals(this.pos, that.pos());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.pos.hashCode();
        return result;
    }

    @Override
    protected void populateToString(@NonNull Map<String, Object> builder) {
        super.populateToString(builder);
        builder.put("pos", this.pos);
    }

    @Override
    public @NonNull BlockNbtComponent.Builder toBuilder() {
        return new BuilderImpl(this);
    }

    static final class WorldPosImpl
    implements BlockNbtComponent.WorldPos {
        private final BlockNbtComponent.WorldPos.Coordinate x;
        private final BlockNbtComponent.WorldPos.Coordinate y;
        private final BlockNbtComponent.WorldPos.Coordinate z;

        WorldPosImpl(BlockNbtComponent.WorldPos.Coordinate x, BlockNbtComponent.WorldPos.Coordinate y, BlockNbtComponent.WorldPos.Coordinate z) {
            this.x = Objects.requireNonNull(x, "x");
            this.y = Objects.requireNonNull(y, "y");
            this.z = Objects.requireNonNull(z, "z");
        }

        @Override
        public @NonNull BlockNbtComponent.WorldPos.Coordinate x() {
            return this.x;
        }

        @Override
        public @NonNull BlockNbtComponent.WorldPos.Coordinate y() {
            return this.y;
        }

        @Override
        public @NonNull BlockNbtComponent.WorldPos.Coordinate z() {
            return this.z;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof BlockNbtComponent.WorldPos)) {
                return false;
            }
            BlockNbtComponent.WorldPos that = (BlockNbtComponent.WorldPos)other;
            return this.x.equals(that.x()) && this.y.equals(that.y()) && this.z.equals(that.z());
        }

        public int hashCode() {
            int result = this.x.hashCode();
            result = 31 * result + this.y.hashCode();
            result = 31 * result + this.z.hashCode();
            return result;
        }

        public String toString() {
            return this.x.toString() + ' ' + this.y.toString() + ' ' + this.z.toString();
        }

        static final class CoordinateImpl
        implements BlockNbtComponent.WorldPos.Coordinate {
            private final int value;
            private final BlockNbtComponent.WorldPos.Coordinate.Type type;

            CoordinateImpl(int value, @NonNull BlockNbtComponent.WorldPos.Coordinate.Type type) {
                this.value = value;
                this.type = Objects.requireNonNull(type, "type");
            }

            @Override
            public int value() {
                return this.value;
            }

            @Override
            public @NonNull BlockNbtComponent.WorldPos.Coordinate.Type type() {
                return this.type;
            }

            public boolean equals(@Nullable Object other) {
                if (this == other) {
                    return true;
                }
                if (!(other instanceof BlockNbtComponent.WorldPos.Coordinate)) {
                    return false;
                }
                BlockNbtComponent.WorldPos.Coordinate that = (BlockNbtComponent.WorldPos.Coordinate)other;
                return this.value() == that.value() && this.type() == that.type();
            }

            public int hashCode() {
                int result = this.value;
                result = 31 * result + this.type.hashCode();
                return result;
            }

            public String toString() {
                return (this.type == BlockNbtComponent.WorldPos.Coordinate.Type.RELATIVE ? "~" : "") + this.value;
            }
        }
    }

    static final class LocalPosImpl
    implements BlockNbtComponent.LocalPos {
        private final double left;
        private final double up;
        private final double forwards;

        LocalPosImpl(double left, double up, double forwards) {
            this.left = left;
            this.up = up;
            this.forwards = forwards;
        }

        @Override
        public double left() {
            return this.left;
        }

        @Override
        public double up() {
            return this.up;
        }

        @Override
        public double forwards() {
            return this.forwards;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof BlockNbtComponent.LocalPos)) {
                return false;
            }
            BlockNbtComponent.LocalPos that = (BlockNbtComponent.LocalPos)other;
            return LocalPosImpl.equals(that.left(), this.left()) && LocalPosImpl.equals(that.up(), this.up()) && LocalPosImpl.equals(that.forwards(), this.forwards());
        }

        private static boolean equals(double a, double b) {
            return Double.doubleToLongBits(a) == Double.doubleToLongBits(b);
        }

        public int hashCode() {
            int result = Double.hashCode(this.left);
            result = 31 * result + Double.hashCode(this.up);
            result = 31 * result + Double.hashCode(this.forwards);
            return result;
        }

        public String toString() {
            return String.format("^%f ^%f ^%f", this.left, this.up, this.forwards);
        }
    }

    static final class BuilderImpl
    extends NbtComponentImpl.BuilderImpl<BlockNbtComponent, BlockNbtComponent.Builder>
    implements BlockNbtComponent.Builder {
        private @Nullable BlockNbtComponent.Pos pos;

        BuilderImpl() {
        }

        BuilderImpl(@NonNull BlockNbtComponent component) {
            super(component);
            this.pos = component.pos();
        }

        @Override
        public @NonNull BlockNbtComponent.Builder pos(@NonNull BlockNbtComponent.Pos pos) {
            this.pos = pos;
            return this;
        }

        @Override
        public @NonNull BlockNbtComponent build() {
            if (this.nbtPath == null) {
                throw new IllegalStateException("nbt path must be set");
            }
            if (this.pos == null) {
                throw new IllegalStateException("pos must be set");
            }
            return new BlockNbtComponentImpl(this.children, this.buildStyle(), this.nbtPath, this.interpret, this.pos);
        }
    }
}

