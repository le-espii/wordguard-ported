/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.fastutil.floats;

import com.sk89q.worldedit.bukkit.fastutil.Function;
import com.sk89q.worldedit.bukkit.fastutil.SafeMath;
import com.sk89q.worldedit.bukkit.fastutil.bytes.Byte2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.bytes.Byte2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2ByteFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2IntFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2LongFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2ObjectFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2ByteFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2IntFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2LongFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2ObjectFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.ints.Int2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.ints.Int2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.longs.Long2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.longs.Long2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Object2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Object2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2FloatFunction;
import java.util.function.DoubleUnaryOperator;

@FunctionalInterface
public interface Float2DoubleFunction
extends Function<Float, Double>,
DoubleUnaryOperator {
    @Override
    @Deprecated
    default public double applyAsDouble(double operand) {
        return this.get(SafeMath.safeDoubleToFloat(operand));
    }

    @Override
    default public double put(float key, double value) {
        throw new UnsupportedOperationException();
    }

    public double get(float var1);

    default public double getOrDefault(float key, double defaultValue) {
        double v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    default public double remove(float key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public Double put(Float key, Double value) {
        float k = key.floatValue();
        boolean containsKey = this.containsKey(k);
        double v = this.put(k, (double)value);
        return containsKey ? Double.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Double get(Object key) {
        if (key == null) {
            return null;
        }
        float k = ((Float)key).floatValue();
        double v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Double.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Double getOrDefault(Object key, Double defaultValue) {
        if (key == null) {
            return defaultValue;
        }
        float k = ((Float)key).floatValue();
        double v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Double.valueOf(v) : defaultValue;
    }

    @Override
    @Deprecated
    default public Double remove(Object key) {
        if (key == null) {
            return null;
        }
        float k = ((Float)key).floatValue();
        return this.containsKey(k) ? Double.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(float key) {
        return true;
    }

    @Override
    @Deprecated
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey(((Float)key).floatValue());
    }

    default public void defaultReturnValue(double rv) {
        throw new UnsupportedOperationException();
    }

    default public double defaultReturnValue() {
        return 0.0;
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<T, Double> compose(java.util.function.Function<? super T, ? extends Float> before) {
        return Function.super.compose(before);
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<Float, T> andThen(java.util.function.Function<? super Double, ? extends T> after) {
        return Function.super.andThen(after);
    }

    default public Float2ByteFunction andThenByte(Double2ByteFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Byte2DoubleFunction composeByte(Byte2FloatFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Float2ShortFunction andThenShort(Double2ShortFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Short2DoubleFunction composeShort(Short2FloatFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Float2IntFunction andThenInt(Double2IntFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Int2DoubleFunction composeInt(Int2FloatFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Float2LongFunction andThenLong(Double2LongFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Long2DoubleFunction composeLong(Long2FloatFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Float2CharFunction andThenChar(Double2CharFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Char2DoubleFunction composeChar(Char2FloatFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Float2FloatFunction andThenFloat(Double2FloatFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Float2DoubleFunction composeFloat(Float2FloatFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Float2DoubleFunction andThenDouble(Double2DoubleFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Double2DoubleFunction composeDouble(Double2FloatFunction before) {
        return k -> this.get(before.get(k));
    }

    default public <T> Float2ObjectFunction<T> andThenObject(Double2ObjectFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Object2DoubleFunction<T> composeObject(Object2FloatFunction<? super T> before) {
        return k -> this.get(before.getFloat(k));
    }

    default public <T> Float2ReferenceFunction<T> andThenReference(Double2ReferenceFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Reference2DoubleFunction<T> composeReference(Reference2FloatFunction<? super T> before) {
        return k -> this.get(before.getFloat(k));
    }
}

