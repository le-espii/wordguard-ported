/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.function.factory;

import com.sk89q.worldedit.function.Contextual;
import com.sk89q.worldedit.function.EditContext;
import com.sk89q.worldedit.function.LayerFunction;
import com.sk89q.worldedit.function.block.SnowSimulator;

public class Snow
implements Contextual<LayerFunction> {
    private final boolean stack;

    public Snow(boolean stack) {
        this.stack = stack;
    }

    @Override
    public LayerFunction createFromContext(EditContext context) {
        return new SnowSimulator(context.getDestination(), this.stack);
    }

    public String toString() {
        return "snow" + (this.stack ? " stacker" : "");
    }
}

