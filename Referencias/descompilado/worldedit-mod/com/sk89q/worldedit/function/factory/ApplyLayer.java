/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.function.factory;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.function.Contextual;
import com.sk89q.worldedit.function.EditContext;
import com.sk89q.worldedit.function.LayerFunction;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.visitor.LayerVisitor;
import com.sk89q.worldedit.regions.FlatRegion;
import com.sk89q.worldedit.regions.NullRegion;
import com.sk89q.worldedit.regions.Regions;
import com.sk89q.worldedit.util.GuavaUtil;

public class ApplyLayer
implements Contextual<Operation> {
    private final FlatRegion region;
    private final Contextual<? extends LayerFunction> function;

    public ApplyLayer(Contextual<? extends LayerFunction> function) {
        this(Regions.asFlatRegion(new NullRegion()), function);
    }

    public ApplyLayer(FlatRegion region, Contextual<? extends LayerFunction> function) {
        Preconditions.checkNotNull((Object)region, (Object)"region");
        Preconditions.checkNotNull(function, (Object)"function");
        this.region = region;
        this.function = function;
    }

    @Override
    public Operation createFromContext(EditContext context) {
        FlatRegion localRegion = Regions.asFlatRegion(GuavaUtil.firstNonNull(context.getRegion(), this.region));
        return new LayerVisitor(localRegion, localRegion.getMinimumPoint().y(), localRegion.getMaximumPoint().y(), this.function.createFromContext(context));
    }

    public String toString() {
        return "set " + String.valueOf(this.function);
    }
}

