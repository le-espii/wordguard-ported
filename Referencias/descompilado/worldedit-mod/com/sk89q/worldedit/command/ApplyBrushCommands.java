/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  org.enginehub.piston.annotation.Command
 *  org.enginehub.piston.annotation.CommandContainer
 *  org.enginehub.piston.annotation.param.Arg
 */
package com.sk89q.worldedit.command;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseItem;
import com.sk89q.worldedit.command.ApplyBrushCommandsRegistration;
import com.sk89q.worldedit.command.BrushCommands;
import com.sk89q.worldedit.command.factory.ItemUseFactory;
import com.sk89q.worldedit.command.factory.ReplaceFactory;
import com.sk89q.worldedit.command.factory.TreeGeneratorFactory;
import com.sk89q.worldedit.command.util.CommandPermissions;
import com.sk89q.worldedit.command.util.CommandPermissionsConditionGenerator;
import com.sk89q.worldedit.command.util.PermissionCondition;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.function.Contextual;
import com.sk89q.worldedit.function.RegionFunction;
import com.sk89q.worldedit.function.factory.ApplyRegion;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.internal.annotation.Direction;
import com.sk89q.worldedit.internal.command.CommandRegistrationHandler;
import com.sk89q.worldedit.regions.factory.RegionFactory;
import com.sk89q.worldedit.util.TreeGenerator;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldedit.util.formatting.text.format.TextDecoration;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.enginehub.piston.Command;
import org.enginehub.piston.CommandManager;
import org.enginehub.piston.CommandManagerService;
import org.enginehub.piston.CommandParameters;
import org.enginehub.piston.annotation.Command;
import org.enginehub.piston.annotation.CommandContainer;
import org.enginehub.piston.annotation.param.Arg;
import org.enginehub.piston.inject.Key;
import org.enginehub.piston.part.CommandArgument;
import org.enginehub.piston.part.CommandParts;
import org.enginehub.piston.part.SubCommandPart;

@CommandContainer(superTypes={CommandPermissionsConditionGenerator.Registration.class})
public class ApplyBrushCommands {
    private static final CommandArgument REGION_FACTORY = CommandParts.arg(TranslatableComponent.of("shape"), TranslatableComponent.of("worldedit.brush.apply.shape")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(Key.of(RegionFactory.class))).build();
    private static final CommandArgument RADIUS = CommandParts.arg(TranslatableComponent.of("radius"), TranslatableComponent.of("worldedit.brush.apply.radius")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"5")).ofTypes((Collection<Key<?>>)ImmutableList.of(Key.of(Double.TYPE))).build();

    public static void register(CommandManagerService service, CommandManager commandManager, CommandRegistrationHandler registration) {
        commandManager.register("apply", builder -> {
            builder.description(TranslatableComponent.of("worldedit.brush.apply.description"));
            builder.action(Command.Action.NULL_ACTION);
            CommandManager manager = service.newCommandManager();
            registration.register(manager, ApplyBrushCommandsRegistration.builder(), new ApplyBrushCommands());
            builder.condition(new PermissionCondition((Set<String>)ImmutableSet.of((Object)"worldedit.brush.apply")));
            builder.addParts(REGION_FACTORY, RADIUS);
            builder.addPart(SubCommandPart.builder(TranslatableComponent.of("type"), TranslatableComponent.of("worldedit.brush.apply.type")).withCommands(manager.getAllCommands().collect(Collectors.toList())).required().build());
        });
    }

    private void setApplyBrush(CommandParameters parameters, Player player, LocalSession localSession, Contextual<? extends RegionFunction> generatorFactory) throws WorldEditException {
        double radius = Objects.requireNonNull(RADIUS.value(parameters).asSingle(Double.TYPE));
        RegionFactory regionFactory = REGION_FACTORY.value(parameters).asSingle(RegionFactory.class);
        BrushCommands.setOperationBasedBrush(player, localSession, radius, new ApplyRegion(generatorFactory), regionFactory, "worldedit.brush.apply");
    }

    @Command(name="forest", desc="Plant trees")
    public void forest(CommandParameters parameters, Player player, LocalSession localSession, @Arg(desc="The type of tree to plant") TreeGenerator.TreeType type) throws WorldEditException {
        this.setApplyBrush(parameters, player, localSession, new TreeGeneratorFactory(type));
    }

    @Command(name="item", desc="Use an item")
    @CommandPermissions(value={"worldedit.brush.item"})
    public void item(CommandParameters parameters, Player player, LocalSession localSession, @Arg(desc="The type of item to use") BaseItem item, @Direction(includeDiagonals=true) @Arg(desc="The direction in which the item will be applied", def={"up"}) com.sk89q.worldedit.util.Direction direction) throws WorldEditException {
        player.print((Component)((TextComponent.Builder)((TextComponent.Builder)TextComponent.builder().append("WARNING: ", TextColor.RED, TextDecoration.BOLD)).append((Component)TranslatableComponent.of("worldedit.brush.apply.item.warning"))).build());
        this.setApplyBrush(parameters, player, localSession, new ItemUseFactory(item, direction));
    }

    @Command(name="set", desc="Place a block")
    public void set(CommandParameters parameters, Player player, LocalSession localSession, @Arg(desc="The pattern of blocks to use") Pattern pattern) throws WorldEditException {
        this.setApplyBrush(parameters, player, localSession, new ReplaceFactory(pattern));
    }
}

