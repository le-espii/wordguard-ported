/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.fastutil.doubles;

import com.sk89q.worldedit.bukkit.fastutil.Function;
import com.sk89q.worldedit.bukkit.fastutil.bytes.Byte2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.bytes.Byte2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2ByteFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2IntFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2LongFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2ObjectFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.ints.Int2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.ints.Int2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.longs.Long2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.longs.Long2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Object2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Object2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2DoubleFunction;
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
import java.util.function.DoubleToIntFunction;

@FunctionalInterface
public interface Double2ShortFunction
extends Function<Double, Short>,
DoubleToIntFunction {
    @Override
    default public int applyAsInt(double operand) {
        return this.get(operand);
    }

    @Override
    default public short put(double key, short value) {
        throw new UnsupportedOperationException();
    }

    public short get(double var1);

    default public short getOrDefault(double key, short defaultValue) {
        short v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    default public short remove(double key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public Short put(Double key, Short value) {
        double k = key;
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
        double k = (Double)key;
        short v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Short.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Short getOrDefault(Object key, Short defaultValue) {
        if (key == null) {
            return defaultValue;
        }
        double k = (Double)key;
        short v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Short.valueOf(v) : defaultValue;
    }

    @Override
    @Deprecated
    default public Short remove(Object key) {
        if (key == null) {
            return null;
        }
        double k = (Double)key;
        return this.containsKey(k) ? Short.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(double key) {
        return true;
    }

    @Override
    @Deprecated
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Double)key);
    }

    default public void defaultReturnValue(short rv) {
        throw new UnsupportedOperationException();
    }

    default public short defaultReturnValue() {
        return 0;
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<T, Short> compose(java.util.function.Function<? super T, ? extends Double> before) {
        return Function.super.compose(before);
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<Double, T> andThen(java.util.function.Function<? super Short, ? extends T> after) {
        return Function.super.andThen(after);
    }

    default public Double2ByteFunction andThenByte(Short2ByteFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Byte2ShortFunction composeByte(Byte2DoubleFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Double2ShortFunction andThenShort(Short2ShortFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Short2ShortFunction composeShort(Short2DoubleFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Double2IntFunction andThenInt(Short2IntFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Int2ShortFunction composeInt(Int2DoubleFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Double2LongFunction andThenLong(Short2LongFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Long2ShortFunction composeLong(Long2DoubleFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Double2CharFunction andThenChar(Short2CharFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Char2ShortFunction composeChar(Char2DoubleFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Double2FloatFunction andThenFloat(Short2FloatFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Float2ShortFunction composeFloat(Float2DoubleFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Double2DoubleFunction andThenDouble(Short2DoubleFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Double2ShortFunction composeDouble(Double2DoubleFunction before) {
        return k -> this.get(before.get(k));
    }

    default public <T> Double2ObjectFunction<T> andThenObject(Short2ObjectFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Object2ShortFunction<T> composeObject(Object2DoubleFunction<? super T> before) {
        return k -> this.get(before.getDouble(k));
    }

    default public <T> Double2ReferenceFunction<T> andThenReference(Short2ReferenceFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Reference2ShortFunction<T> composeReference(Reference2DoubleFunction<? super T> before) {
        return k -> this.get(before.getDouble(k));
    }
}

