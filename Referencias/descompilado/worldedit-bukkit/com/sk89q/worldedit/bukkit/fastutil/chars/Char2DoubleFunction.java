/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.fastutil.chars;

import com.sk89q.worldedit.bukkit.fastutil.Function;
import com.sk89q.worldedit.bukkit.fastutil.SafeMath;
import com.sk89q.worldedit.bukkit.fastutil.bytes.Byte2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.bytes.Byte2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2ByteFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2IntFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2LongFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2ObjectFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2ByteFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2IntFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2LongFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2ObjectFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.ints.Int2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.ints.Int2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.longs.Long2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.longs.Long2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Object2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Object2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2DoubleFunction;
import java.util.function.IntToDoubleFunction;

@FunctionalInterface
public interface Char2DoubleFunction
extends Function<Character, Double>,
IntToDoubleFunction {
    @Override
    @Deprecated
    default public double applyAsDouble(int operand) {
        return this.get(SafeMath.safeIntToChar(operand));
    }

    @Override
    default public double put(char key, double value) {
        throw new UnsupportedOperationException();
    }

    public double get(char var1);

    default public double getOrDefault(char key, double defaultValue) {
        double v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    default public double remove(char key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public Double put(Character key, Double value) {
        char k = key.charValue();
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
        char k = ((Character)key).charValue();
        double v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Double.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Double getOrDefault(Object key, Double defaultValue) {
        if (key == null) {
            return defaultValue;
        }
        char k = ((Character)key).charValue();
        double v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Double.valueOf(v) : defaultValue;
    }

    @Override
    @Deprecated
    default public Double remove(Object key) {
        if (key == null) {
            return null;
        }
        char k = ((Character)key).charValue();
        return this.containsKey(k) ? Double.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(char key) {
        return true;
    }

    @Override
    @Deprecated
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey(((Character)key).charValue());
    }

    default public void defaultReturnValue(double rv) {
        throw new UnsupportedOperationException();
    }

    default public double defaultReturnValue() {
        return 0.0;
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<T, Double> compose(java.util.function.Function<? super T, ? extends Character> before) {
        return Function.super.compose(before);
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<Character, T> andThen(java.util.function.Function<? super Double, ? extends T> after) {
        return Function.super.andThen(after);
    }

    default public Char2ByteFunction andThenByte(Double2ByteFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Byte2DoubleFunction composeByte(Byte2CharFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Char2ShortFunction andThenShort(Double2ShortFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Short2DoubleFunction composeShort(Short2CharFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Char2IntFunction andThenInt(Double2IntFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Int2DoubleFunction composeInt(Int2CharFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Char2LongFunction andThenLong(Double2LongFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Long2DoubleFunction composeLong(Long2CharFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Char2CharFunction andThenChar(Double2CharFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Char2DoubleFunction composeChar(Char2CharFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Char2FloatFunction andThenFloat(Double2FloatFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Float2DoubleFunction composeFloat(Float2CharFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Char2DoubleFunction andThenDouble(Double2DoubleFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Double2DoubleFunction composeDouble(Double2CharFunction before) {
        return k -> this.get(before.get(k));
    }

    default public <T> Char2ObjectFunction<T> andThenObject(Double2ObjectFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Object2DoubleFunction<T> composeObject(Object2CharFunction<? super T> before) {
        return k -> this.get(before.getChar(k));
    }

    default public <T> Char2ReferenceFunction<T> andThenReference(Double2ReferenceFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Reference2DoubleFunction<T> composeReference(Reference2CharFunction<? super T> before) {
        return k -> this.get(before.getChar(k));
    }
}

