/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.fastutil.floats;

import com.sk89q.worldedit.bukkit.fastutil.Function;
import com.sk89q.worldedit.bukkit.fastutil.SafeMath;
import com.sk89q.worldedit.bukkit.fastutil.bytes.Byte2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.bytes.Byte2LongFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2LongFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2LongFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2ByteFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2IntFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2ObjectFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.ints.Int2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.ints.Int2LongFunction;
import com.sk89q.worldedit.bukkit.fastutil.longs.Long2ByteFunction;
import com.sk89q.worldedit.bukkit.fastutil.longs.Long2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.longs.Long2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.longs.Long2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.longs.Long2IntFunction;
import com.sk89q.worldedit.bukkit.fastutil.longs.Long2LongFunction;
import com.sk89q.worldedit.bukkit.fastutil.longs.Long2ObjectFunction;
import com.sk89q.worldedit.bukkit.fastutil.longs.Long2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.longs.Long2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Object2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Object2LongFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2LongFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2LongFunction;
import java.util.function.DoubleToLongFunction;

@FunctionalInterface
public interface Float2LongFunction
extends Function<Float, Long>,
DoubleToLongFunction {
    @Override
    @Deprecated
    default public long applyAsLong(double operand) {
        return this.get(SafeMath.safeDoubleToFloat(operand));
    }

    @Override
    default public long put(float key, long value) {
        throw new UnsupportedOperationException();
    }

    public long get(float var1);

    default public long getOrDefault(float key, long defaultValue) {
        long v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    default public long remove(float key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public Long put(Float key, Long value) {
        float k = key.floatValue();
        boolean containsKey = this.containsKey(k);
        long v = this.put(k, (long)value);
        return containsKey ? Long.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Long get(Object key) {
        if (key == null) {
            return null;
        }
        float k = ((Float)key).floatValue();
        long v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Long.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Long getOrDefault(Object key, Long defaultValue) {
        if (key == null) {
            return defaultValue;
        }
        float k = ((Float)key).floatValue();
        long v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Long.valueOf(v) : defaultValue;
    }

    @Override
    @Deprecated
    default public Long remove(Object key) {
        if (key == null) {
            return null;
        }
        float k = ((Float)key).floatValue();
        return this.containsKey(k) ? Long.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(float key) {
        return true;
    }

    @Override
    @Deprecated
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey(((Float)key).floatValue());
    }

    default public void defaultReturnValue(long rv) {
        throw new UnsupportedOperationException();
    }

    default public long defaultReturnValue() {
        return 0L;
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<T, Long> compose(java.util.function.Function<? super T, ? extends Float> before) {
        return Function.super.compose(before);
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<Float, T> andThen(java.util.function.Function<? super Long, ? extends T> after) {
        return Function.super.andThen(after);
    }

    default public Float2ByteFunction andThenByte(Long2ByteFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Byte2LongFunction composeByte(Byte2FloatFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Float2ShortFunction andThenShort(Long2ShortFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Short2LongFunction composeShort(Short2FloatFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Float2IntFunction andThenInt(Long2IntFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Int2LongFunction composeInt(Int2FloatFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Float2LongFunction andThenLong(Long2LongFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Long2LongFunction composeLong(Long2FloatFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Float2CharFunction andThenChar(Long2CharFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Char2LongFunction composeChar(Char2FloatFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Float2FloatFunction andThenFloat(Long2FloatFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Float2LongFunction composeFloat(Float2FloatFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Float2DoubleFunction andThenDouble(Long2DoubleFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Double2LongFunction composeDouble(Double2FloatFunction before) {
        return k -> this.get(before.get(k));
    }

    default public <T> Float2ObjectFunction<T> andThenObject(Long2ObjectFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Object2LongFunction<T> composeObject(Object2FloatFunction<? super T> before) {
        return k -> this.get(before.getFloat(k));
    }

    default public <T> Float2ReferenceFunction<T> andThenReference(Long2ReferenceFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Reference2LongFunction<T> composeReference(Reference2FloatFunction<? super T> before) {
        return k -> this.get(before.getFloat(k));
    }
}

