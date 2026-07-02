/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.command.factory;

import com.sk89q.worldedit.extent.NullExtent;
import com.sk89q.worldedit.function.Contextual;
import com.sk89q.worldedit.function.EditContext;
import com.sk89q.worldedit.function.RegionFunction;
import com.sk89q.worldedit.function.block.BlockReplace;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.util.GuavaUtil;

public class ReplaceFactory
implements Contextual<RegionFunction> {
    private final Pattern fill;

    public ReplaceFactory(Pattern fill) {
        this.fill = fill;
    }

    @Override
    public RegionFunction createFromContext(EditContext context) {
        return new BlockReplace(GuavaUtil.firstNonNull(context.getDestination(), new NullExtent()), GuavaUtil.firstNonNull(context.getFill(), this.fill));
    }

    public String toString() {
        return "replace blocks";
    }
}

