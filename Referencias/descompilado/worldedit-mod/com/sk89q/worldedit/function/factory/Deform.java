/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 */
package com.sk89q.worldedit.function.factory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.NullExtent;
import com.sk89q.worldedit.function.Contextual;
import com.sk89q.worldedit.function.EditContext;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.RunContext;
import com.sk89q.worldedit.internal.expression.Expression;
import com.sk89q.worldedit.internal.expression.ExpressionException;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.NullRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.GuavaUtil;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;

public class Deform
implements Contextual<Operation> {
    private Extent destination;
    private Region region;
    private final Expression expression;
    private Mode mode;
    private Vector3 offset = Vector3.ZERO;

    public Deform(String expression) {
        this(new NullExtent(), new NullRegion(), expression);
    }

    public Deform(String expression, Mode mode) {
        this(new NullExtent(), new NullRegion(), expression, mode);
    }

    public Deform(Extent destination, Region region, String expression) {
        this(destination, region, expression, Mode.UNIT_CUBE);
    }

    public Deform(Extent destination, Region region, String expression, Mode mode) {
        Preconditions.checkNotNull((Object)destination, (Object)"destination");
        Preconditions.checkNotNull((Object)region, (Object)"region");
        Preconditions.checkNotNull((Object)((Object)mode), (Object)"mode");
        Preconditions.checkNotNull((Object)expression, (Object)"expression");
        this.expression = Expression.compile(expression, "x", "y", "z");
        this.expression.optimize();
        this.destination = destination;
        this.region = region;
        this.mode = mode;
    }

    public Extent getDestination() {
        return this.destination;
    }

    public void setDestination(Extent destination) {
        Preconditions.checkNotNull((Object)destination, (Object)"destination");
        this.destination = destination;
    }

    public Region getRegion() {
        return this.region;
    }

    public void setRegion(Region region) {
        Preconditions.checkNotNull((Object)region, (Object)"region");
        this.region = region;
    }

    public Mode getMode() {
        return this.mode;
    }

    public void setMode(Mode mode) {
        Preconditions.checkNotNull((Object)((Object)mode), (Object)"mode");
        this.mode = mode;
    }

    public Vector3 getOffset() {
        return this.offset;
    }

    public void setOffset(Vector3 offset) {
        Preconditions.checkNotNull((Object)((Object)offset), (Object)"offset");
        this.offset = offset;
    }

    public String toString() {
        return "deformation of " + this.expression.getSource();
    }

    @Override
    public Operation createFromContext(EditContext context) {
        Vector3 unit;
        Vector3 zero;
        Region region = GuavaUtil.firstNonNull(context.getRegion(), this.region);
        switch (this.mode.ordinal()) {
            case 2: {
                Vector3 min = region.getMinimumPoint().toVector3();
                Vector3 max = region.getMaximumPoint().toVector3();
                zero = max.add(min).multiply(0.5);
                unit = max.subtract(zero);
                if (unit.x() == 0.0) {
                    unit = unit.withX(1.0);
                }
                if (unit.y() == 0.0) {
                    unit = unit.withY(1.0);
                }
                if (unit.z() != 0.0) break;
                unit = unit.withZ(1.0);
                break;
            }
            case 0: {
                zero = Vector3.ZERO;
                unit = Vector3.ONE;
                break;
            }
            default: {
                zero = this.offset;
                unit = Vector3.ONE;
            }
        }
        LocalSession session = context.getSession();
        return new DeformOperation(context.getDestination(), region, zero, unit, this.expression, session == null ? WorldEdit.getInstance().getConfiguration().calculationTimeout : session.getTimeout());
    }

    public static enum Mode {
        RAW_COORD,
        OFFSET,
        UNIT_CUBE;

    }

    private record DeformOperation(Extent destination, Region region, Vector3 zero, Vector3 unit, Expression expression, int timeout) implements Operation
    {
        @Override
        public Operation resume(RunContext run) throws WorldEditException {
            try {
                ((EditSession)this.destination).deformRegion(this.region, this.zero, this.unit, this.expression, this.timeout);
                return null;
            }
            catch (ExpressionException e) {
                throw new RuntimeException("Failed to execute expression", e);
            }
        }

        @Override
        public void cancel() {
        }

        @Override
        public Iterable<Component> getStatusMessages() {
            return ImmutableList.of((Object)TranslatableComponent.of("worldedit.operation.deform.expression", TextComponent.of(this.expression.getSource()).color(TextColor.LIGHT_PURPLE)));
        }
    }
}

