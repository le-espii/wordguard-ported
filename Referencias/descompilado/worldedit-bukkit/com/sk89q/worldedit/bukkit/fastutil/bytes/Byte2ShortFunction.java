/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.fastutil.bytes;

import com.sk89q.worldedit.bukkit.fastutil.Function;
import com.sk89q.worldedit.bukkit.fastutil.SafeMath;
import com.sk89q.worldedit.bukkit.fastutil.bytes.Byte2ByteFunction;
import com.sk89q.worldedit.bukkit.fastutil.bytes.Byte2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.bytes.Byte2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.bytes.Byte2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.bytes.Byte2IntFunction;
import com.sk89q.worldedit.bukkit.fastutil.bytes.Byte2LongFunction;
import com.sk89q.worldedit.bukkit.fastutil.bytes.Byte2ObjectFunction;
import com.sk89q.worldedit.bukkit.fastutil.bytes.Byte2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2ByteFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2ByteFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2ByteFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.ints.Int2ByteFunction;
import com.sk89q.worldedit.bukkit.fastutil.ints.Int2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.longs.Long2ByteFunction;
import com.sk89q.worldedit.bukkit.fastutil.longs.Long2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Object2ByteFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Object2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2ByteFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2ByteFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2IntFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2LongFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2ObjectFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2ShortFunction;
import java.util.function.IntUnaryOperator;

@FunctionalInterface
public interface Byte2ShortFunction
extends Function<Byte, Short>,
IntUnaryOperator {
    @Override
    @Deprecated
    default public int applyAsInt(int operand) {
        return this.get(SafeMath.safeIntToByte(operand));
    }

    @Override
    default public short put(byte key, short value) {
        throw new UnsupportedOperationException();
    }

    public short get(byte var1);

    default public short getOrDefault(byte key, short defaultValue) {
        short v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    default public short remove(byte key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public Short put(Byte key, Short value) {
        byte k = key;
        boolean containsKey = this.containsKey(k);
        short v = this.put(k, (short)value);
        return containsKey ? Short.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Short get(Object key) {
        if (key == null) {
            return null;
        }
        byte k = (Byte)key;
        short v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Short.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Short getOrDefault(Object key, Short defaultValue) {
        if (key == null) {
            return defaultValue;
        }
        byte k = (Byte)key;
        short v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Short.valueOf(v) : defaultValue;
    }

    @Override
    @Deprecated
    default public Short remove(Object key) {
        if (key == null) {
            return null;
        }
        byte k = (Byte)key;
        return this.containsKey(k) ? Short.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(byte key) {
        return true;
    }

    @Override
    @Deprecated
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Byte)key);
    }

    default public void defaultReturnValue(short rv) {
        throw new UnsupportedOperationException();
    }

    default public short defaultReturnValue() {
        return 0;
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<T, Short> compose(java.util.function.Function<? super T, ? extends Byte> before) {
        return Function.super.compose(before);
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<Byte, T> andThen(java.util.function.Function<? super Short, ? extends T> after) {
        return Function.super.andThen(after);
    }

    default public Byte2ByteFunction andThenByte(Short2ByteFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Byte2ShortFunction composeByte(Byte2ByteFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Byte2ShortFunction andThenShort(Short2ShortFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Short2ShortFunction composeShort(Short2ByteFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Byte2IntFunction andThenInt(Short2IntFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Int2ShortFunction composeInt(Int2ByteFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Byte2LongFunction andThenLong(Short2LongFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Long2ShortFunction composeLong(Long2ByteFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Byte2CharFunction andThenChar(Short2CharFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Char2ShortFunction composeChar(Char2ByteFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Byte2FloatFunction andThenFloat(Short2FloatFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Float2ShortFunction composeFloat(Float2ByteFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Byte2DoubleFunction andThenDouble(Short2DoubleFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Double2ShortFunction composeDouble(Double2ByteFunction before) {
        return k -> this.get(before.get(k));
    }

    default public <T> Byte2ObjectFunction<T> andThenObject(Short2ObjectFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Object2ShortFunction<T> composeObject(Object2ByteFunction<? super T> before) {
        return k -> this.get(before.getByte(k));
    }

    default public <T> Byte2ReferenceFunction<T> andThenReference(Short2ReferenceFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Reference2ShortFunction<T> composeReference(Reference2ByteFunction<? super T> before) {
        return k -> this.get(before.getByte(k));
    }
}

