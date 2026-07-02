/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.fastutil.chars;

import com.sk89q.worldedit.bukkit.fastutil.Function;
import com.sk89q.worldedit.bukkit.fastutil.SafeMath;
import com.sk89q.worldedit.bukkit.fastutil.bytes.Byte2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2ByteFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2DoubleFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2FloatFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2IntFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2LongFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2ObjectFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2ReferenceFunction;
import com.sk89q.worldedit.bukkit.fastutil.chars.Char2ShortFunction;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.floats.Float2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.ints.Int2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.longs.Long2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Object2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.objects.Reference2CharFunction;
import com.sk89q.worldedit.bukkit.fastutil.shorts.Short2CharFunction;
import java.util.function.IntUnaryOperator;

@FunctionalInterface
public interface Char2CharFunction
extends Function<Character, Character>,
IntUnaryOperator {
    @Override
    @Deprecated
    default public int applyAsInt(int operand) {
        return this.get(SafeMath.safeIntToChar(operand));
    }

    @Override
    default public char put(char key, char value) {
        throw new UnsupportedOperationException();
    }

    public char get(char var1);

    default public char getOrDefault(char key, char defaultValue) {
        char v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    default public char remove(char key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public Character put(Character key, Character value) {
        char k = key.charValue();
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
        char k = ((Character)key).charValue();
        char v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Character.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Character getOrDefault(Object key, Character defaultValue) {
        if (key == null) {
            return defaultValue;
        }
        char k = ((Character)key).charValue();
        char v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Character.valueOf(v) : defaultValue;
    }

    @Override
    @Deprecated
    default public Character remove(Object key) {
        if (key == null) {
            return null;
        }
        char k = ((Character)key).charValue();
        return this.containsKey(k) ? Character.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(char key) {
        return true;
    }

    @Override
    @Deprecated
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey(((Character)key).charValue());
    }

    default public void defaultReturnValue(char rv) {
        throw new UnsupportedOperationException();
    }

    default public char defaultReturnValue() {
        return '\u0000';
    }

    public static Char2CharFunction identity() {
        return k -> k;
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<T, Character> compose(java.util.function.Function<? super T, ? extends Character> before) {
        return Function.super.compose(before);
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<Character, T> andThen(java.util.function.Function<? super Character, ? extends T> after) {
        return Function.super.andThen(after);
    }

    default public Char2ByteFunction andThenByte(Char2ByteFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Byte2CharFunction composeByte(Byte2CharFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Char2ShortFunction andThenShort(Char2ShortFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Short2CharFunction composeShort(Short2CharFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Char2IntFunction andThenInt(Char2IntFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Int2CharFunction composeInt(Int2CharFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Char2LongFunction andThenLong(Char2LongFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Long2CharFunction composeLong(Long2CharFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Char2CharFunction andThenChar(Char2CharFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Char2CharFunction composeChar(Char2CharFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Char2FloatFunction andThenFloat(Char2FloatFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Float2CharFunction composeFloat(Float2CharFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Char2DoubleFunction andThenDouble(Char2DoubleFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Double2CharFunction composeDouble(Double2CharFunction before) {
        return k -> this.get(before.get(k));
    }

    default public <T> Char2ObjectFunction<T> andThenObject(Char2ObjectFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Object2CharFunction<T> composeObject(Object2CharFunction<? super T> before) {
        return k -> this.get(before.getChar(k));
    }

    default public <T> Char2ReferenceFunction<T> andThenReference(Char2ReferenceFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Reference2CharFunction<T> composeReference(Reference2CharFunction<? super T> before) {
        return k -> this.get(before.getChar(k));
    }
}

