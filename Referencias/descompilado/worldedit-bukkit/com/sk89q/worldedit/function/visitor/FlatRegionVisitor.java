/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 */
package com.sk89q.worldedit.function.visitor;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.function.FlatRegionFunction;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.RunContext;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.regions.FlatRegion;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;

public class FlatRegionVisitor
implements Operation {
    private final FlatRegion flatRegion;
    private final FlatRegionFunction function;
    private int affected = 0;

    public FlatRegionVisitor(FlatRegion flatRegion, FlatRegionFunction function) {
        Preconditions.checkNotNull((Object)flatRegion);
        Preconditions.checkNotNull((Object)function);
        this.flatRegion = flatRegion;
        this.function = function;
    }

    public int getAffected() {
        return this.affected;
    }

    @Override
    public Operation resume(RunContext run) throws WorldEditException {
        for (BlockVector2 pt : this.flatRegion.asFlatRegion()) {
            if (!this.function.apply(pt)) continue;
            ++this.affected;
        }
        return null;
    }

    @Override
    public void cancel() {
    }

    @Override
    public Iterable<Component> getStatusMessages() {
        return ImmutableList.of((Object)TranslatableComponent.of("worldedit.operation.affected.column", TextComponent.of(this.getAffected())).color(TextColor.LIGHT_PURPLE));
    }
}

