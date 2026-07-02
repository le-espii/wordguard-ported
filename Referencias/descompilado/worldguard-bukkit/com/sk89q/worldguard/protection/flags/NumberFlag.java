/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.Beta
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldguard.protection.flags;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.RegionGroup;

public abstract class NumberFlag<T extends Number>
extends Flag<T> {
    private static final Number[] EMPTY_NUMBER_ARRAY = new Number[0];
    private Number[] suggestions = EMPTY_NUMBER_ARRAY;

    protected NumberFlag(String name, RegionGroup defaultGroup) {
        super(name, defaultGroup);
    }

    protected NumberFlag(String name) {
        super(name);
    }

    @Beta
    public void setSuggestedValues(Number[] values) {
        this.suggestions = (Number[])Preconditions.checkNotNull((Object)values);
    }

    @Beta
    public Number[] getSuggestedValues() {
        return this.suggestions;
    }
}

