/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.fastutil.chars;

import com.sk89q.worldedit.bukkit.fastutil.Function;
import com.sk89q.worldedit.bukkit.fastutil.SafeMath;
import com.sk89q.worldedit.bukkit.fastutil.bytes.Byte2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.bytes.Byte2LongFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2ByteFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2IntFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2ObjectFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2LongFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2LongFunction;
import com.sk89q.worldedit.bukkit.fastutil.ints.Int2CharFunction;
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
import com.sk89q.worldedit.bukkit.fastutil.objects.Object2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Object2LongFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2LongFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2LongFunction;
import java.util.function.IntToLongFunction;

@FunctionalInterface
public interface Char2LongFunction
extends Function<Character, Long>,
IntToLongFunction {
    @Override
    @Deprecated
    default public long applyAsLong(int operand) {
        return this.get(SafeMath.safeIntToChar(operand));
    }

    @Override
    default public long put(char key, long value) {
        throw new UnsupportedOperationException();
    }

    public long get(char var1);

    default public long getOrDefault(char key, long defaultValue) {
        long v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    default public long remove(char key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public Long put(Character key, Long value) {
        char k = key.charValue();
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
        char k = ((Character)key).charValue();
        long v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Long.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Long getOrDefault(Object key, Long defaultValue) {
        if (key == null) {
            return defaultValue;
        }
        char k = ((Character)key).charValue();
        long v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Long.valueOf(v) : defaultValue;
    }

    @Override
    @Deprecated
    default public Long remove(Object key) {
        if (key == null) {
            return null;
        }
        char k = ((Character)key).charValue();
        return this.containsKey(k) ? Long.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(char key) {
        return true;
    }

    @Override
    @Deprecated
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey(((Character)key).charValue());
    }

    default public void defaultReturnValue(long rv) {
        throw new UnsupportedOperationException();
    }

    default public long defaultReturnValue() {
        return 0L;
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<T, Long> compose(java.util.function.Function<? super T, ? extends Character> before) {
        return Function.super.compose(before);
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<Character, T> andThen(java.util.function.Function<? super Long, ? extends T> after) {
        return Function.super.andThen(after);
    }

    default public Char2ByteFunction andThenByte(Long2ByteFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Byte2LongFunction composeByte(Byte2CharFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Char2ShortFunction andThenShort(Long2ShortFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Short2LongFunction composeShort(Short2CharFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Char2IntFunction andThenInt(Long2IntFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Int2LongFunction composeInt(Int2CharFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Char2LongFunction andThenLong(Long2LongFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Long2LongFunction composeLong(Long2CharFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Char2CharFunction andThenChar(Long2CharFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Char2LongFunction composeChar(Char2CharFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Char2FloatFunction andThenFloat(Long2FloatFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Float2LongFunction composeFloat(Float2CharFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Char2DoubleFunction andThenDouble(Long2DoubleFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Double2LongFunction composeDouble(Double2CharFunction before) {
        return k -> this.get(before.get(k));
    }

    default public <T> Char2ObjectFunction<T> andThenObject(Long2ObjectFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Object2LongFunction<T> composeObject(Object2CharFunction<? super T> before) {
        return k -> this.get(before.getChar(k));
    }

    default public <T> Char2ReferenceFunction<T> andThenReference(Long2ReferenceFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Reference2LongFunction<T> composeReference(Reference2CharFunction<? super T> before) {
        return k -> this.get(before.getChar(k));
    }
}

