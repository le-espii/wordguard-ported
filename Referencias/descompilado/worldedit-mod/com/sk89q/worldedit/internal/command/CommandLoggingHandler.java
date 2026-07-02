/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.internal.command;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.command.util.Logging;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.world.World;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.enginehub.piston.CommandParameters;
import org.enginehub.piston.exception.CommandException;
import org.enginehub.piston.gen.CommandCallListener;
import org.enginehub.piston.inject.Key;

public class CommandLoggingHandler
implements CommandCallListener,
AutoCloseable {
    private final WorldEdit worldEdit;
    private final Logger logger;

    public CommandLoggingHandler(WorldEdit worldEdit, Logger logger) {
        Preconditions.checkNotNull((Object)worldEdit);
        Preconditions.checkNotNull((Object)logger);
        this.worldEdit = worldEdit;
        this.logger = logger;
    }

    @Override
    public void beforeCall(Method method, CommandParameters parameters) {
        World world;
        Logging loggingAnnotation = method.getAnnotation(Logging.class);
        StringBuilder builder = new StringBuilder();
        Logging.LogMode logMode = loggingAnnotation == null ? null : loggingAnnotation.value();
        Optional<Actor> actorOpt = parameters.injectedValue(Key.of(Actor.class));
        if (actorOpt.isEmpty()) {
            return;
        }
        Actor actor = actorOpt.get();
        try {
            Optional<World> worldOpt = parameters.injectedValue(Key.of(World.class));
            if (worldOpt.isEmpty()) {
                return;
            }
            world = worldOpt.get();
        }
        catch (CommandException ex) {
            return;
        }
        builder.append("WorldEdit: ").append(actor.getName());
        builder.append(" (in \"").append(world.getName()).append("\")");
        builder.append(": ").append(parameters.getMetadata().getCalledName());
        builder.append(": ").append(Stream.concat(Stream.of(parameters.getMetadata().getCalledName()), parameters.getMetadata().getArguments().stream()).collect(Collectors.joining(" ")));
        if (logMode != null && actor instanceof Player) {
            Player player = (Player)actor;
            Vector3 position = player.getLocation().toVector();
            LocalSession session = this.worldEdit.getSessionManager().get(actor);
            switch (logMode) {
                case PLACEMENT: {
                    try {
                        position = session.getPlacementPosition(actor).toVector3();
                    }
                    catch (IncompleteRegionException e) {
                        break;
                    }
                }
                case POSITION: {
                    builder.append(" - Position: ").append((Object)position);
                    break;
                }
                case ALL: {
                    builder.append(" - Position: ").append((Object)position);
                }
                case ORIENTATION_REGION: {
                    builder.append(" - Orientation: ").append(player.getCardinalDirection().name());
                }
                case REGION: {
                    try {
                        builder.append(" - Region: ").append(session.getSelection(world));
                    }
                    catch (IncompleteRegionException e) {}
                    break;
                }
            }
        }
        this.logger.info(builder.toString());
    }

    @Override
    public void close() {
        for (Handler h : this.logger.getHandlers()) {
            h.close();
        }
    }
}

