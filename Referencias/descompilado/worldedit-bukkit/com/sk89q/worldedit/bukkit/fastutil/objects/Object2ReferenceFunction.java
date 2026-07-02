/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.fastutil.objects;

import com.sk89q.worldedit.bukkit.fastutil.Function;
import com.sk89q.worldedit.bukkit.fastutil.bytes.Byte2ObjectFunction;
import com.sk89q.worldedit.bukkit.fastutil.bytes.Byte2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2ObjectFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2ObjectFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2ObjectFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.ints.Int2ObjectFunction;
import com.sk89q.worldedit.bukkit.fastutil.ints.Int2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.longs.Long2ObjectFunction;
import com.sk89q.worldedit.bukkit.fastutil.longs.Long2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Object2ByteFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Object2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Object2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Object2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Object2IntFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Object2LongFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Object2ObjectFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Object2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2ByteFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2IntFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2LongFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2ObjectFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2ObjectFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2ReferenceFunction;

@FunctionalInterface
public interface Object2ReferenceFunction<K, V>
extends Function<K, V> {
    @Override
    default public V put(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V get(Object var1);

    @Override
    default public V getOrDefault(Object key, V defaultValue) {
        V v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    @Override
    default public V remove(Object key) {
        throw new UnsupportedOperationException();
    }

    default public void defaultReturnValue(V rv) {
        throw new UnsupportedOperationException();
    }

    default public V defaultReturnValue() {
        return null;
    }

    default public Object2ByteFunction<K> andThenByte(Reference2ByteFunction<V> after) {
        return k -> after.getByte(this.get(k));
    }

    default public Byte2ReferenceFunction<V> composeByte(Byte2ObjectFunction<K> before) {
        return k -> this.get(before.get(k));
    }

    default public Object2ShortFunction<K> andThenShort(Reference2ShortFunction<V> after) {
        return k -> after.getShort(this.get(k));
    }

    default public Short2ReferenceFunction<V> composeShort(Short2ObjectFunction<K> before) {
        return k -> this.get(before.get(k));
    }

    default public Object2IntFunction<K> andThenInt(Reference2IntFunction<V> after) {
        return k -> after.getInt(this.get(k));
    }

    default public Int2ReferenceFunction<V> composeInt(Int2ObjectFunction<K> before) {
        return k -> this.get(before.get(k));
    }

    default public Object2LongFunction<K> andThenLong(Reference2LongFunction<V> after) {
        return k -> after.getLong(this.get(k));
    }

    default public Long2ReferenceFunction<V> composeLong(Long2ObjectFunction<K> before) {
        return k -> this.get(before.get(k));
    }

    default public Object2CharFunction<K> andThenChar(Reference2CharFunction<V> after) {
        return k -> after.getChar(this.get(k));
    }

    default public Char2ReferenceFunction<V> composeChar(Char2ObjectFunction<K> before) {
        return k -> this.get(before.get(k));
    }

    default public Object2FloatFunction<K> andThenFloat(Reference2FloatFunction<V> after) {
        return k -> after.getFloat(this.get(k));
    }

    default public Float2ReferenceFunction<V> composeFloat(Float2ObjectFunction<K> before) {
        return k -> this.get(before.get(k));
    }

    default public Object2DoubleFunction<K> andThenDouble(Reference2DoubleFunction<V> after) {
        return k -> after.getDouble(this.get(k));
    }

    default public Double2ReferenceFunction<V> composeDouble(Double2ObjectFunction<K> before) {
        return k -> this.get(before.get(k));
    }

    default public <T> Object2ObjectFunction<K, T> andThenObject(Reference2ObjectFunction<? super V, ? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Object2ReferenceFunction<T, V> composeObject(Object2ObjectFunction<? super T, ? extends K> before) {
        return k -> this.get(before.get(k));
    }

    default public <T> Object2ReferenceFunction<K, T> andThenReference(Reference2ReferenceFunction<? super V, ? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Reference2ReferenceFunction<T, V> composeReference(Reference2ObjectFunction<? super T, ? extends K> before) {
        return k -> this.get(before.get(k));
    }
}

