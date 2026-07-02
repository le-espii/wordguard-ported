/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.command.argument;

import com.sk89q.worldedit.UnknownDirectionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.command.argument.AbstractDirectionConverter;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.math.BlockVector3;
import javax.annotation.Nullable;
import org.enginehub.piston.CommandManager;

public final class DirectionVectorConverter
extends AbstractDirectionConverter<BlockVector3> {
    public DirectionVectorConverter(WorldEdit worldEdit, boolean includeDiagonals) {
        super(worldEdit, includeDiagonals);
    }

    public static void register(WorldEdit worldEdit, CommandManager commandManager) {
        for (boolean includeDiagonals : new boolean[]{false, true}) {
            DirectionVectorConverter directionConverter = new DirectionVectorConverter(worldEdit, includeDiagonals);
            DirectionVectorConverter.register(commandManager, directionConverter, BlockVector3.class, includeDiagonals);
        }
    }

    @Override
    protected BlockVector3 convertDirection(String argument, @Nullable Player player, boolean includeDiagonals) throws UnknownDirectionException {
        return includeDiagonals ? this.getWorldEdit().getDiagonalDirection(player, argument) : this.getWorldEdit().getDirection(player, argument);
    }
}

