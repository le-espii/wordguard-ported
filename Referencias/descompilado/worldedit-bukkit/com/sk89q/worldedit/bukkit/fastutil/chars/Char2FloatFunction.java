/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.fastutil.chars;

import com.sk89q.worldedit.bukkit.fastutil.Function;
import com.sk89q.worldedit.bukkit.fastutil.SafeMath;
import com.sk89q.worldedit.bukkit.fastutil.bytes.Byte2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.bytes.Byte2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2ByteFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2IntFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2LongFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2ObjectFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2ByteFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2IntFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2LongFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2ObjectFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.ints.Int2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.ints.Int2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.longs.Long2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.longs.Long2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Object2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Object2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2FloatFunction;
import java.util.function.IntToDoubleFunction;

@FunctionalInterface
public interface Char2FloatFunction
extends Function<Character, Float>,
IntToDoubleFunction {
    @Override
    @Deprecated
    default public double applyAsDouble(int operand) {
        return this.get(SafeMath.safeIntToChar(operand));
    }

    @Override
    default public float put(char key, float value) {
        throw new UnsupportedOperationException();
    }

    public float get(char var1);

    default public float getOrDefault(char key, float defaultValue) {
        float v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    default public float remove(char key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public Float put(Character key, Float value) {
        char k = key.charValue();
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
        char k = ((Character)key).charValue();
        float v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Float.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Float getOrDefault(Object key, Float defaultValue) {
        if (key == null) {
            return defaultValue;
        }
        char k = ((Character)key).charValue();
        float v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Float.valueOf(v) : defaultValue;
    }

    @Override
    @Deprecated
    default public Float remove(Object key) {
        if (key == null) {
            return null;
        }
        char k = ((Character)key).charValue();
        return this.containsKey(k) ? Float.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(char key) {
        return true;
    }

    @Override
    @Deprecated
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey(((Character)key).charValue());
    }

    default public void defaultReturnValue(float rv) {
        throw new UnsupportedOperationException();
    }

    default public float defaultReturnValue() {
        return 0.0f;
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<T, Float> compose(java.util.function.Function<? super T, ? extends Character> before) {
        return Function.super.compose(before);
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<Character, T> andThen(java.util.function.Function<? super Float, ? extends T> after) {
        return Function.super.andThen(after);
    }

    default public Char2ByteFunction andThenByte(Float2ByteFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Byte2FloatFunction composeByte(Byte2CharFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Char2ShortFunction andThenShort(Float2ShortFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Short2FloatFunction composeShort(Short2CharFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Char2IntFunction andThenInt(Float2IntFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Int2FloatFunction composeInt(Int2CharFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Char2LongFunction andThenLong(Float2LongFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Long2FloatFunction composeLong(Long2CharFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Char2CharFunction andThenChar(Float2CharFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Char2FloatFunction composeChar(Char2CharFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Char2FloatFunction andThenFloat(Float2FloatFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Float2FloatFunction composeFloat(Float2CharFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Char2DoubleFunction andThenDouble(Float2DoubleFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Double2FloatFunction composeDouble(Double2CharFunction before) {
        return k -> this.get(before.get(k));
    }

    default public <T> Char2ObjectFunction<T> andThenObject(Float2ObjectFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Object2FloatFunction<T> composeObject(Object2CharFunction<? super T> before) {
        return k -> this.get(before.getChar(k));
    }

    default public <T> Char2ReferenceFunction<T> andThenReference(Float2ReferenceFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Reference2FloatFunction<T> composeReference(Reference2CharFunction<? super T> before) {
        return k -> this.get(before.getChar(k));
    }
}

