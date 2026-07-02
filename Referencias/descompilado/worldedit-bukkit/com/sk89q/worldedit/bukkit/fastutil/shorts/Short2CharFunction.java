/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.fastutil.shorts;

import com.sk89q.worldedit.bukkit.fastutil.Function;
import com.sk89q.worldedit.bukkit.fastutil.SafeMath;
import com.sk89q.worldedit.bukkit.fastutil.bytes.Byte2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.bytes.Byte2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2ByteFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2IntFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2LongFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2ObjectFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.ints.Int2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.ints.Int2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.longs.Long2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.longs.Long2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Object2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Object2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2ByteFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2IntFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2LongFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2ObjectFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2ShortFunction;
import java.util.function.IntUnaryOperator;

@FunctionalInterface
public interface Short2CharFunction
extends Function<Short, Character>,
IntUnaryOperator {
    @Override
    @Deprecated
    default public int applyAsInt(int operand) {
        return this.get(SafeMath.safeIntToShort(operand));
    }

    @Override
    default public char put(short key, char value) {
        throw new UnsupportedOperationException();
    }

    public char get(short var1);

    default public char getOrDefault(short key, char defaultValue) {
        char v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    default public char remove(short key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public Character put(Short key, Character value) {
        short k = key;
        boolean containsKey = this.containsKey(k);
        char v = this.put(k, value.charValue());
        return containsKey ? Character.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Character get(Object key) {
        if (key == null) {
            return null;
        }
        short k = (Short)key;
        char v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Character.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Character getOrDefault(Object key, Character defaultValue) {
        if (key == null) {
            return defaultValue;
        }
        short k = (Short)key;
        char v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Character.valueOf(v) : defaultValue;
    }

    @Override
    @Deprecated
    default public Character remove(Object key) {
        if (key == null) {
            return null;
        }
        short k = (Short)key;
        return this.containsKey(k) ? Character.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(short key) {
        return true;
    }

    @Override
    @Deprecated
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Short)key);
    }

    default public void defaultReturnValue(char rv) {
        throw new UnsupportedOperationException();
    }

    default public char defaultReturnValue() {
        return '\u0000';
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<T, Character> compose(java.util.function.Function<? super T, ? extends Short> before) {
        return Function.super.compose(before);
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<Short, T> andThen(java.util.function.Function<? super Character, ? extends T> after) {
        return Function.super.andThen(after);
    }

    default public Short2ByteFunction andThenByte(Char2ByteFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Byte2CharFunction composeByte(Byte2ShortFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Short2ShortFunction andThenShort(Char2ShortFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Short2CharFunction composeShort(Short2ShortFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Short2IntFunction andThenInt(Char2IntFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Int2CharFunction composeInt(Int2ShortFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Short2LongFunction andThenLong(Char2LongFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Long2CharFunction composeLong(Long2ShortFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Short2CharFunction andThenChar(Char2CharFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Char2CharFunction composeChar(Char2ShortFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Short2FloatFunction andThenFloat(Char2FloatFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Float2CharFunction composeFloat(Float2ShortFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Short2DoubleFunction andThenDouble(Char2DoubleFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Double2CharFunction composeDouble(Double2ShortFunction before) {
        return k -> this.get(before.get(k));
    }

    default public <T> Short2ObjectFunction<T> andThenObject(Char2ObjectFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Object2CharFunction<T> composeObject(Object2ShortFunction<? super T> before) {
        return k -> this.get(before.getShort(k));
    }

    default public <T> Short2ReferenceFunction<T> andThenReference(Char2ReferenceFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Reference2CharFunction<T> composeReference(Reference2ShortFunction<? super T> before) {
        return k -> this.get(before.getShort(k));
    }
}

