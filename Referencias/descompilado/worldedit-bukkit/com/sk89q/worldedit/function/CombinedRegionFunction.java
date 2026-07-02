/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.function;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.function.RegionFunction;
import com.sk89q.worldedit.math.BlockVector3;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CombinedRegionFunction
implements RegionFunction {
    private final List<RegionFunction> functions = new ArrayList<RegionFunction>();

    public CombinedRegionFunction() {
    }

    public CombinedRegionFunction(Collection<RegionFunction> functions) {
        Preconditions.checkNotNull(functions);
        this.functions.addAll(functions);
    }

    public CombinedRegionFunction(RegionFunction ... function) {
        this(Arrays.asList((RegionFunction[])Preconditions.checkNotNull((Object)function)));
    }

    public void add(Collection<RegionFunction> functions) {
        Preconditions.checkNotNull(functions);
        this.functions.addAll(functions);
    }

    public void add(RegionFunction ... function) {
        this.add(Arrays.asList((RegionFunction[])Preconditions.checkNotNull((Object)function)));
    }

    @Override
    public boolean apply(BlockVector3 position) throws WorldEditException {
        boolean ret = false;
        for (RegionFunction function : this.functions) {
            if (!function.apply(position)) continue;
            ret = true;
        }
        return ret;
    }
}

