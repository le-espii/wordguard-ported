/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.fastutil.chars;

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
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2IntFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2LongFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2ObjectFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2ShortFunction;
import java.util.function.IntUnaryOperator;

@FunctionalInterface
public interface Char2ShortFunction
extends Function<Character, Short>,
IntUnaryOperator {
    @Override
    @Deprecated
    default public int applyAsInt(int operand) {
        return this.get(SafeMath.safeIntToChar(operand));
    }

    @Override
    default public short put(char key, short value) {
        throw new UnsupportedOperationException();
    }

    public short get(char var1);

    default public short getOrDefault(char key, short defaultValue) {
        short v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    default public short remove(char key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public Short put(Character key, Short value) {
        char k = key.charValue();
        boolean containsKey = this.containsKey(k);
        short v = this.put(k, (short)value);
        return containsKey ? Short.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Short get(Object key) {
        if (key == null) {
            return null;
        }
        char k = ((Character)key).charValue();
        short v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Short.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Short getOrDefault(Object key, Short defaultValue) {
        if (key == null) {
            return defaultValue;
        }
        char k = ((Character)key).charValue();
        short v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Short.valueOf(v) : defaultValue;
    }

    @Override
    @Deprecated
    default public Short remove(Object key) {
        if (key == null) {
            return null;
        }
        char k = ((Character)key).charValue();
        return this.containsKey(k) ? Short.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(char key) {
        return true;
    }

    @Override
    @Deprecated
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey(((Character)key).charValue());
    }

    default public void defaultReturnValue(short rv) {
        throw new UnsupportedOperationException();
    }

    default public short defaultReturnValue() {
        return 0;
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<T, Short> compose(java.util.function.Function<? super T, ? extends Character> before) {
        return Function.super.compose(before);
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<Character, T> andThen(java.util.function.Function<? super Short, ? extends T> after) {
        return Function.super.andThen(after);
    }

    default public Char2ByteFunction andThenByte(Short2ByteFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Byte2ShortFunction composeByte(Byte2CharFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Char2ShortFunction andThenShort(Short2ShortFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Short2ShortFunction composeShort(Short2CharFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Char2IntFunction andThenInt(Short2IntFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Int2ShortFunction composeInt(Int2CharFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Char2LongFunction andThenLong(Short2LongFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Long2ShortFunction composeLong(Long2CharFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Char2CharFunction andThenChar(Short2CharFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Char2ShortFunction composeChar(Char2CharFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Char2FloatFunction andThenFloat(Short2FloatFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Float2ShortFunction composeFloat(Float2CharFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Char2DoubleFunction andThenDouble(Short2DoubleFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Double2ShortFunction composeDouble(Double2CharFunction before) {
        return k -> this.get(before.get(k));
    }

    default public <T> Char2ObjectFunction<T> andThenObject(Short2ObjectFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Object2ShortFunction<T> composeObject(Object2CharFunction<? super T> before) {
        return k -> this.get(before.getChar(k));
    }

    default public <T> Char2ReferenceFunction<T> andThenReference(Short2ReferenceFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Reference2ShortFunction<T> composeReference(Reference2CharFunction<? super T> before) {
        return k -> this.get(before.getChar(k));
    }
}

