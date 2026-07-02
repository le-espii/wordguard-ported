/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.sk89q.worldedit.function.visitor;

import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.function.RegionFunction;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.RunContext;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;

public class RegionVisitor
implements Operation {
    private final Region region;
    private final RegionFunction function;
    private int affected = 0;

    public RegionVisitor(Region region, RegionFunction function) {
        this.region = region;
        this.function = function;
    }

    public int getAffected() {
        return this.affected;
    }

    @Override
    public Operation resume(RunContext run) throws WorldEditException {
        for (BlockVector3 pt : this.region) {
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
        return ImmutableList.of((Object)TranslatableComponent.of("worldedit.operation.affected.block", TextComponent.of(this.getAffected())).color(TextColor.LIGHT_PURPLE));
    }
}

