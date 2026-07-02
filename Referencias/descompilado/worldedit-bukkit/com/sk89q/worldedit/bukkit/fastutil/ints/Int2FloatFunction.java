/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.fastutil.ints;

import com.sk89q.worldedit.bukkit.fastutil.Function;
import com.sk89q.worldedit.bukkit.fastutil.bytes.Byte2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.bytes.Byte2IntFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2IntFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2IntFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2ByteFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2IntFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2LongFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2ObjectFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.ints.Int2ByteFunction;
import com.sk89q.worldedit.bukkit.fastutil.ints.Int2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.ints.Int2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.ints.Int2IntFunction;
import com.sk89q.worldedit.bukkit.fastutil.ints.Int2LongFunction;
import com.sk89q.worldedit.bukkit.fastutil.ints.Int2ObjectFunction;
import com.sk89q.worldedit.bukkit.fastutil.ints.Int2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.ints.Int2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.longs.Long2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.longs.Long2IntFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Object2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Object2IntFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2IntFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2IntFunction;
import java.util.function.IntToDoubleFunction;

@FunctionalInterface
public interface Int2FloatFunction
extends Function<Integer, Float>,
IntToDoubleFunction {
    @Override
    default public double applyAsDouble(int operand) {
        return this.get(operand);
    }

    @Override
    default public float put(int key, float value) {
        throw new UnsupportedOperationException();
    }

    public float get(int var1);

    default public float getOrDefault(int key, float defaultValue) {
        float v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    default public float remove(int key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public Float put(Integer key, Float value) {
        int k = key;
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
        int k = (Integer)key;
        float v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Float.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Float getOrDefault(Object key, Float defaultValue) {
        if (key == null) {
            return defaultValue;
        }
        int k = (Integer)key;
        float v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Float.valueOf(v) : defaultValue;
    }

    @Override
    @Deprecated
    default public Float remove(Object key) {
        if (key == null) {
            return null;
        }
        int k = (Integer)key;
        return this.containsKey(k) ? Float.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(int key) {
        return true;
    }

    @Override
    @Deprecated
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Integer)key);
    }

    default public void defaultReturnValue(float rv) {
        throw new UnsupportedOperationException();
    }

    default public float defaultReturnValue() {
        return 0.0f;
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<T, Float> compose(java.util.function.Function<? super T, ? extends Integer> before) {
        return Function.super.compose(before);
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<Integer, T> andThen(java.util.function.Function<? super Float, ? extends T> after) {
        return Function.super.andThen(after);
    }

    default public Int2ByteFunction andThenByte(Float2ByteFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Byte2FloatFunction composeByte(Byte2IntFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Int2ShortFunction andThenShort(Float2ShortFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Short2FloatFunction composeShort(Short2IntFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Int2IntFunction andThenInt(Float2IntFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Int2FloatFunction composeInt(Int2IntFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Int2LongFunction andThenLong(Float2LongFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Long2FloatFunction composeLong(Long2IntFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Int2CharFunction andThenChar(Float2CharFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Char2FloatFunction composeChar(Char2IntFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Int2FloatFunction andThenFloat(Float2FloatFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Float2FloatFunction composeFloat(Float2IntFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Int2DoubleFunction andThenDouble(Float2DoubleFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Double2FloatFunction composeDouble(Double2IntFunction before) {
        return k -> this.get(before.get(k));
    }

    default public <T> Int2ObjectFunction<T> andThenObject(Float2ObjectFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Object2FloatFunction<T> composeObject(Object2IntFunction<? super T> before) {
        return k -> this.get(before.getInt(k));
    }

    default public <T> Int2ReferenceFunction<T> andThenReference(Float2ReferenceFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Reference2FloatFunction<T> composeReference(Reference2IntFunction<? super T> before) {
        return k -> this.get(before.getInt(k));
    }
}

