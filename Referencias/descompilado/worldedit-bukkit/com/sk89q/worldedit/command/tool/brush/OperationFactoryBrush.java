/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.command.tool.brush;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.command.tool.brush.Brush;
import com.sk89q.worldedit.function.Contextual;
import com.sk89q.worldedit.function.EditContext;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.factory.RegionFactory;

public class OperationFactoryBrush
implements Brush {
    private final Contextual<? extends Operation> operationFactory;
    private final RegionFactory regionFactory;
    private final LocalSession session;

    public OperationFactoryBrush(Contextual<? extends Operation> operationFactory, RegionFactory regionFactory) {
        this(operationFactory, regionFactory, null);
    }

    public OperationFactoryBrush(Contextual<? extends Operation> operationFactory, RegionFactory regionFactory, LocalSession session) {
        this.operationFactory = operationFactory;
        this.regionFactory = regionFactory;
        this.session = session;
    }

    @Override
    public void build(EditSession editSession, BlockVector3 position, Pattern pattern, double size) throws MaxChangedBlocksException {
        EditContext context = new EditContext();
        context.setDestination(editSession);
        context.setRegion(this.regionFactory.createCenteredAt(position, size));
        context.setFill(pattern);
        context.setSession(this.session);
        Operation operation = this.operationFactory.createFromContext(context);
        Operations.completeLegacy(operation);
    }
}

