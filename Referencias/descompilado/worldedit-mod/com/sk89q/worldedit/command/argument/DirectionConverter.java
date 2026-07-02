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
import com.sk89q.worldedit.util.Direction;
import java.util.Optional;
import javax.annotation.Nullable;
import org.enginehub.piston.CommandManager;

public final class DirectionConverter
extends AbstractDirectionConverter<Direction> {
    private DirectionConverter(WorldEdit worldEdit, boolean includeDiagonals) {
        super(worldEdit, includeDiagonals);
    }

    public static void register(WorldEdit worldEdit, CommandManager commandManager) {
        for (boolean includeDiagonals : new boolean[]{false, true}) {
            DirectionConverter directionConverter = new DirectionConverter(worldEdit, includeDiagonals);
            DirectionConverter.register(commandManager, directionConverter, Direction.class, includeDiagonals);
        }
    }

    @Override
    protected Direction convertDirection(String argument, @Nullable Player player, boolean includeDiagonals) throws UnknownDirectionException {
        BlockVector3 vec = includeDiagonals ? this.getWorldEdit().getDiagonalDirection(player, argument) : this.getWorldEdit().getDirection(player, argument);
        return Optional.ofNullable(Direction.findClosest(vec.toVector3(), Direction.Flag.ALL)).orElseThrow(() -> new UnknownDirectionException(argument));
    }
}

