/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.fastutil.objects;

import com.sk89q.worldedit.bukkit.fastutil.Function;
import com.sk89q.worldedit.bukkit.fastutil.bytes.Byte2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.bytes.Byte2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2ReferenceFunction;
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
import com.sk89q.worldedit.bukkit.fastutil.ints.Int2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.longs.Long2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.longs.Long2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Object2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Object2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2ByteFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2IntFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2LongFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2ObjectFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2ReferenceFunction;
import java.util.function.ToDoubleFunction;

@FunctionalInterface
public interface Reference2FloatFunction<K>
extends Function<K, Float>,
ToDoubleFunction<K> {
    @Override
    default public double applyAsDouble(K operand) {
        return this.getFloat(operand);
    }

    @Override
    default public float put(K key, float value) {
        throw new UnsupportedOperationException();
    }

    public float getFloat(Object var1);

    @Override
    default public float getOrDefault(Object key, float defaultValue) {
        float v = this.getFloat(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    default public float removeFloat(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public Float put(K key, Float value) {
        K k = key;
        boolean containsKey = this.containsKey(k);
        float v = this.put(k, value.floatValue());
        return containsKey ? Float.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Float get(Object key) {
        Object k = key;
        float v = this.getFloat(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Float.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Float getOrDefault(Object key, Float defaultValue) {
        Object k = key;
        float v = this.getFloat(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Float.valueOf(v) : defaultValue;
    }

    @Override
    @Deprecated
    default public Float remove(Object key) {
        Object k = key;
        return this.containsKey(k) ? Float.valueOf(this.removeFloat(k)) : null;
    }

    default public void defaultReturnValue(float rv) {
        throw new UnsupportedOperationException();
    }

    default public float defaultReturnValue() {
        return 0.0f;
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<K, T> andThen(java.util.function.Function<? super Float, ? extends T> after) {
        return Function.super.andThen(after);
    }

    default public Reference2ByteFunction<K> andThenByte(Float2ByteFunction after) {
        return k -> after.get(this.getFloat(k));
    }

    default public Byte2FloatFunction composeByte(Byte2ReferenceFunction<K> before) {
        return k -> this.getFloat(before.get(k));
    }

    default public Reference2ShortFunction<K> andThenShort(Float2ShortFunction after) {
        return k -> after.get(this.getFloat(k));
    }

    default public Short2FloatFunction composeShort(Short2ReferenceFunction<K> before) {
        return k -> this.getFloat(before.get(k));
    }

    default public Reference2IntFunction<K> andThenInt(Float2IntFunction after) {
        return k -> after.get(this.getFloat(k));
    }

    default public Int2FloatFunction composeInt(Int2ReferenceFunction<K> before) {
        return k -> this.getFloat(before.get(k));
    }

    default public Reference2LongFunction<K> andThenLong(Float2LongFunction after) {
        return k -> after.get(this.getFloat(k));
    }

    default public Long2FloatFunction composeLong(Long2ReferenceFunction<K> before) {
        return k -> this.getFloat(before.get(k));
    }

    default public Reference2CharFunction<K> andThenChar(Float2CharFunction after) {
        return k -> after.get(this.getFloat(k));
    }

    default public Char2FloatFunction composeChar(Char2ReferenceFunction<K> before) {
        return k -> this.getFloat(before.get(k));
    }

    default public Reference2FloatFunction<K> andThenFloat(Float2FloatFunction after) {
        return k -> after.get(this.getFloat(k));
    }

    default public Float2FloatFunction composeFloat(Float2ReferenceFunction<K> before) {
        return k -> this.getFloat(before.get(k));
    }

    default public Reference2DoubleFunction<K> andThenDouble(Float2DoubleFunction after) {
        return k -> after.get(this.getFloat(k));
    }

    default public Double2FloatFunction composeDouble(Double2ReferenceFunction<K> before) {
        return k -> this.getFloat(before.get(k));
    }

    default public <T> Reference2ObjectFunction<K, T> andThenObject(Float2ObjectFunction<? extends T> after) {
        return k -> after.get(this.getFloat(k));
    }

    default public <T> Object2FloatFunction<T> composeObject(Object2ReferenceFunction<? super T, ? extends K> before) {
        return k -> this.getFloat(before.get(k));
    }

    default public <T> Reference2ReferenceFunction<K, T> andThenReference(Float2ReferenceFunction<? extends T> after) {
        return k -> after.get(this.getFloat(k));
    }

    default public <T> Reference2FloatFunction<T> composeReference(Reference2ReferenceFunction<? super T, ? extends K> before) {
        return k -> this.getFloat(before.get(k));
    }
}

