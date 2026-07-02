/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Verify
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Sets
 */
package com.sk89q.worldedit.util;

import com.google.common.base.Verify;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.sk89q.worldedit.util.SideEffect;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SideEffectSet {
    private static final SideEffectSet DEFAULT = new SideEffectSet((Map<SideEffect, SideEffect.State>)ImmutableMap.of());
    private static final SideEffectSet NONE = new SideEffectSet(Arrays.stream(SideEffect.values()).filter(SideEffect::isExposed).collect(Collectors.toMap(Function.identity(), state -> SideEffect.State.OFF)));
    private final int sideEffectsBitmap;
    private final Set<SideEffect> sideEffectsToApply;

    private static int shift(SideEffect effect) {
        return effect.ordinal() * 2;
    }

    private static int computeSideEffectsBitmap(Map<SideEffect, SideEffect.State> sideEffects) {
        int sideEffectsBitmap = 0;
        for (SideEffect effect : SideEffect.values()) {
            SideEffect.State state = sideEffects.getOrDefault((Object)effect, effect.getDefaultValue());
            sideEffectsBitmap |= state.ordinal() << SideEffectSet.shift(effect);
        }
        return sideEffectsBitmap;
    }

    public SideEffectSet(Map<SideEffect, SideEffect.State> sideEffects) {
        this(SideEffectSet.computeSideEffectsBitmap(sideEffects));
    }

    private SideEffectSet(int sideEffectsBitmap) {
        this.sideEffectsBitmap = sideEffectsBitmap;
        EnumSet<SideEffect> sideEffectsToApply = EnumSet.noneOf(SideEffect.class);
        for (SideEffect effect : SideEffect.values()) {
            if (!this.shouldApply(effect)) continue;
            sideEffectsToApply.add(effect);
        }
        this.sideEffectsToApply = Sets.immutableEnumSet(sideEffectsToApply);
    }

    public SideEffectSet with(SideEffect sideEffect, SideEffect.State state) {
        int mask = 3 << SideEffectSet.shift(sideEffect);
        int newState = state.ordinal() << SideEffectSet.shift(sideEffect) & mask;
        int newBitmap = this.sideEffectsBitmap & ~mask | newState;
        return new SideEffectSet(newBitmap);
    }

    public boolean doesApplyAny() {
        return this.sideEffectsBitmap != 0;
    }

    public SideEffect.State getState(SideEffect effect) {
        return SideEffect.State.values()[this.getRawState(effect)];
    }

    private int getRawState(SideEffect effect) {
        return this.sideEffectsBitmap >> SideEffectSet.shift(effect) & 3;
    }

    public boolean shouldApply(SideEffect effect) {
        return this.getRawState(effect) != 0;
    }

    public Set<SideEffect> getSideEffectsToApply() {
        return this.sideEffectsToApply;
    }

    public static SideEffectSet defaults() {
        return DEFAULT;
    }

    public static SideEffectSet none() {
        return NONE;
    }

    static {
        Verify.verify((SideEffect.State.values().length == 3 ? 1 : 0) != 0, (String)"Implementation requires specifically 3 values in the SideEffect.State enum", (Object[])new Object[0]);
        int maxEffects = 16;
        Verify.verify((SideEffect.values().length <= maxEffects ? 1 : 0) != 0, (String)("Implementation requires less than " + maxEffects + " side effects"), (Object[])new Object[0]);
        Verify.verify((SideEffect.State.OFF.ordinal() == 0 ? 1 : 0) != 0, (String)"Implementation requires SideEffect.State.OFF to be the first value", (Object[])new Object[0]);
    }
}

