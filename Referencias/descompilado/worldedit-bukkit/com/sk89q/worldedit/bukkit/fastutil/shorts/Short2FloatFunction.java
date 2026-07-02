/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.fastutil.shorts;

import com.sk89q.worldedit.bukkit.fastutil.Function;
import com.sk89q.worldedit.bukkit.fastutil.SafeMath;
import com.sk89q.worldedit.bukkit.fastutil.bytes.Byte2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.bytes.Byte2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2ByteFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2IntFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2LongFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2ObjectFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.ints.Int2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.ints.Int2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.longs.Long2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.longs.Long2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Object2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Object2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2ByteFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2IntFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2LongFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2ObjectFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2ShortFunction;
import java.util.function.IntToDoubleFunction;

@FunctionalInterface
public interface Short2FloatFunction
extends Function<Short, Float>,
IntToDoubleFunction {
    @Override
    @Deprecated
    default public double applyAsDouble(int operand) {
        return this.get(SafeMath.safeIntToShort(operand));
    }

    @Override
    default public float put(short key, float value) {
        throw new UnsupportedOperationException();
    }

    public float get(short var1);

    default public float getOrDefault(short key, float defaultValue) {
        float v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    default public float remove(short key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public Float put(Short key, Float value) {
        short k = key;
        boolean containsKey = this.containsKey(k);
        float v = this.put(k, value.floatValue());
        return containsKey ? Float.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Float get(Object key) {
        if (key == null) {
            return null;
        }
        short k = (Short)key;
        float v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Float.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Float getOrDefault(Object key, Float defaultValue) {
        if (key == null) {
            return defaultValue;
        }
        short k = (Short)key;
        float v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Float.valueOf(v) : defaultValue;
    }

    @Override
    @Deprecated
    default public Float remove(Object key) {
        if (key == null) {
            return null;
        }
        short k = (Short)key;
        return this.containsKey(k) ? Float.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(short key) {
        return true;
    }

    @Override
    @Deprecated
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Short)key);
    }

    default public void defaultReturnValue(float rv) {
        throw new UnsupportedOperationException();
    }

    default public float defaultReturnValue() {
        return 0.0f;
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<T, Float> compose(java.util.function.Function<? super T, ? extends Short> before) {
        return Function.super.compose(before);
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<Short, T> andThen(java.util.function.Function<? super Float, ? extends T> after) {
        return Function.super.andThen(after);
    }

    default public Short2ByteFunction andThenByte(Float2ByteFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Byte2FloatFunction composeByte(Byte2ShortFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Short2ShortFunction andThenShort(Float2ShortFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Short2FloatFunction composeShort(Short2ShortFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Short2IntFunction andThenInt(Float2IntFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Int2FloatFunction composeInt(Int2ShortFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Short2LongFunction andThenLong(Float2LongFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Long2FloatFunction composeLong(Long2ShortFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Short2CharFunction andThenChar(Float2CharFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Char2FloatFunction composeChar(Char2ShortFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Short2FloatFunction andThenFloat(Float2FloatFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Float2FloatFunction composeFloat(Float2ShortFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Short2DoubleFunction andThenDouble(Float2DoubleFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Double2FloatFunction composeDouble(Double2ShortFunction before) {
        return k -> this.get(before.get(k));
    }

    default public <T> Short2ObjectFunction<T> andThenObject(Float2ObjectFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Object2FloatFunction<T> composeObject(Object2ShortFunction<? super T> before) {
        return k -> this.get(before.getShort(k));
    }

    default public <T> Short2ReferenceFunction<T> andThenReference(Float2ReferenceFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Reference2FloatFunction<T> composeReference(Reference2ShortFunction<? super T> before) {
        return k -> this.get(before.getShort(k));
    }
}

