/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.fastutil.doubles;

import com.sk89q.worldedit.bukkit.fastutil.Function;
import com.sk89q.worldedit.bukkit.fastutil.bytes.Byte2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.bytes.Byte2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2ByteFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2IntFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2LongFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2ObjectFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.ints.Int2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.ints.Int2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.longs.Long2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.longs.Long2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Object2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Object2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2ByteFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2IntFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2LongFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2ObjectFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2ReferenceFunction;
import java.util.function.DoubleFunction;

@FunctionalInterface
public interface Double2ReferenceFunction<V>
extends Function<Double, V>,
DoubleFunction<V> {
    @Override
    default public V apply(double operand) {
        return this.get(operand);
    }

    @Override
    default public V put(double key, V value) {
        throw new UnsupportedOperationException();
    }

    public V get(double var1);

    default public V getOrDefault(double key, V defaultValue) {
        V v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    default public V remove(double key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public V put(Double key, V value) {
        double k = key;
        boolean containsKey = this.containsKey(k);
        V v = this.put(k, value);
        return (V)(containsKey ? v : null);
    }

    @Override
    @Deprecated
    default public V get(Object key) {
        if (key == null) {
            return null;
        }
        double k = (Double)key;
        V v = this.get(k);
        return (V)(v != this.defaultReturnValue() || this.containsKey(k) ? v : null);
    }

    @Override
    @Deprecated
    default public V getOrDefault(Object key, V defaultValue) {
        if (key == null) {
            return defaultValue;
        }
        double k = (Double)key;
        V v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? v : defaultValue;
    }

    @Override
    @Deprecated
    default public V remove(Object key) {
        if (key == null) {
            return null;
        }
        double k = (Double)key;
        return this.containsKey(k) ? (V)this.remove(k) : null;
    }

    default public boolean containsKey(double key) {
        return true;
    }

    @Override
    @Deprecated
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Double)key);
    }

    default public void defaultReturnValue(V rv) {
        throw new UnsupportedOperationException();
    }

    default public V defaultReturnValue() {
        return null;
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<T, V> compose(java.util.function.Function<? super T, ? extends Double> before) {
        return Function.super.compose(before);
    }

    default public Double2ByteFunction andThenByte(Reference2ByteFunction<V> after) {
        return k -> after.getByte(this.get(k));
    }

    default public Byte2ReferenceFunction<V> composeByte(Byte2DoubleFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Double2ShortFunction andThenShort(Reference2ShortFunction<V> after) {
        return k -> after.getShort(this.get(k));
    }

    default public Short2ReferenceFunction<V> composeShort(Short2DoubleFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Double2IntFunction andThenInt(Reference2IntFunction<V> after) {
        return k -> after.getInt(this.get(k));
    }

    default public Int2ReferenceFunction<V> composeInt(Int2DoubleFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Double2LongFunction andThenLong(Reference2LongFunction<V> after) {
        return k -> after.getLong(this.get(k));
    }

    default public Long2ReferenceFunction<V> composeLong(Long2DoubleFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Double2CharFunction andThenChar(Reference2CharFunction<V> after) {
        return k -> after.getChar(this.get(k));
    }

    default public Char2ReferenceFunction<V> composeChar(Char2DoubleFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Double2FloatFunction andThenFloat(Reference2FloatFunction<V> after) {
        return k -> after.getFloat(this.get(k));
    }

    default public Float2ReferenceFunction<V> composeFloat(Float2DoubleFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Double2DoubleFunction andThenDouble(Reference2DoubleFunction<V> after) {
        return k -> after.getDouble(this.get(k));
    }

    default public Double2ReferenceFunction<V> composeDouble(Double2DoubleFunction before) {
        return k -> this.get(before.get(k));
    }

    default public <T> Double2ObjectFunction<T> andThenObject(Reference2ObjectFunction<? super V, ? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Object2ReferenceFunction<T, V> composeObject(Object2DoubleFunction<? super T> before) {
        return k -> this.get(before.getDouble(k));
    }

    default public <T> Double2ReferenceFunction<T> andThenReference(Reference2ReferenceFunction<? super V, ? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Reference2ReferenceFunction<T, V> composeReference(Reference2DoubleFunction<? super T> before) {
        return k -> this.get(before.getDouble(k));
    }
}

