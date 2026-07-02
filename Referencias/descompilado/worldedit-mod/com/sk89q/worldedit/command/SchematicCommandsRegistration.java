/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.sk89q.worldedit.command;

import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.command.SchematicCommands;
import com.sk89q.worldedit.command.util.CommandPermissionsConditionGenerator;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.share.ClipboardShareDestination;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.io.file.FilenameException;
import java.lang.reflect.Method;
import java.util.Collection;
import org.enginehub.piston.Command;
import org.enginehub.piston.CommandManager;
import org.enginehub.piston.CommandParameters;
import org.enginehub.piston.gen.CommandCallListener;
import org.enginehub.piston.gen.CommandRegistration;
import org.enginehub.piston.inject.Key;
import org.enginehub.piston.internal.RegistrationUtil;
import org.enginehub.piston.part.ArgAcceptingCommandFlag;
import org.enginehub.piston.part.CommandArgument;
import org.enginehub.piston.part.CommandPart;
import org.enginehub.piston.part.CommandParts;
import org.enginehub.piston.part.NoArgCommandFlag;

public final class SchematicCommandsRegistration
implements CommandRegistration<SchematicCommands>,
CommandPermissionsConditionGenerator.Registration {
    private static final Key<String> string_Key = Key.of(String.class);
    private static final Key<ClipboardFormat> clipboardFormat_Key = Key.of(ClipboardFormat.class);
    private static final Key<ClipboardShareDestination> clipboardShareDestination_Key = Key.of(ClipboardShareDestination.class);
    private static final Key<Integer> integer_Key = Key.of(Integer.class);
    private static final Key<Actor> actor_Key = Key.of(Actor.class);
    private static final Key<LocalSession> localSession_Key = Key.of(LocalSession.class);
    private CommandManager commandManager;
    private SchematicCommands containerInstance;
    private CommandPermissionsConditionGenerator commandPermissionsConditionGenerator;
    private ImmutableList<CommandCallListener> listeners;
    private final CommandArgument filenamePart = CommandParts.arg(TranslatableComponent.of("filename"), TextComponent.of("File name.")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(string_Key)).build();
    private final CommandArgument formatPart = CommandParts.arg(TranslatableComponent.of("format"), TextComponent.of("Format name.")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"sponge")).ofTypes((Collection<Key<?>>)ImmutableList.of(clipboardFormat_Key)).build();
    private final NoArgCommandFlag allowOverwritePart = CommandParts.flag('f', TextComponent.of("Overwrite an existing file.")).build();
    private final CommandArgument schematicNamePart = CommandParts.arg(TranslatableComponent.of("schematicName"), TextComponent.of("Schematic name. Defaults to name-millis")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"")).ofTypes((Collection<Key<?>>)ImmutableList.of(string_Key)).build();
    private final CommandArgument destinationPart = CommandParts.arg(TranslatableComponent.of("destination"), TextComponent.of("Share location")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"ehpaste")).ofTypes((Collection<Key<?>>)ImmutableList.of(clipboardShareDestination_Key)).build();
    private final CommandArgument formatPart2 = CommandParts.arg(TranslatableComponent.of("format"), TextComponent.of("Format name")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"")).ofTypes((Collection<Key<?>>)ImmutableList.of(clipboardFormat_Key)).build();
    private final ArgAcceptingCommandFlag pagePart = CommandParts.flag('p', TextComponent.of("Page to view.")).withRequiredArg().argNamed(TranslatableComponent.of("page")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"1")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final NoArgCommandFlag oldFirstPart = CommandParts.flag('d', TextComponent.of("Sort by date, oldest first")).build();
    private final NoArgCommandFlag newFirstPart = CommandParts.flag('n', TextComponent.of("Sort by date, newest first")).build();

    private SchematicCommandsRegistration() {
        this.listeners = ImmutableList.of();
    }

    public static SchematicCommandsRegistration builder() {
        return new SchematicCommandsRegistration();
    }

    public SchematicCommandsRegistration commandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
        return this;
    }

    public SchematicCommandsRegistration containerInstance(SchematicCommands containerInstance) {
        this.containerInstance = containerInstance;
        return this;
    }

    @Override
    public SchematicCommandsRegistration commandPermissionsConditionGenerator(CommandPermissionsConditionGenerator commandPermissionsConditionGenerator) {
        this.commandPermissionsConditionGenerator = commandPermissionsConditionGenerator;
        return this;
    }

    public SchematicCommandsRegistration listeners(Collection<CommandCallListener> listeners) {
        this.listeners = ImmutableList.copyOf(listeners);
        return this;
    }

    @Override
    public void build() {
        this.commandManager.register("load", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Load a schematic into your clipboard"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.filenamePart, (Object)this.formatPart));
            b.action(this::cmd$load);
            Method commandMethod = RegistrationUtil.getCommandMethod(SchematicCommands.class, "load", Actor.class, LocalSession.class, String.class, ClipboardFormat.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("save", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Save your clipboard into a schematic file"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.filenamePart, (Object)this.formatPart, (Object)this.allowOverwritePart));
            b.action(this::cmd$save);
            Method commandMethod = RegistrationUtil.getCommandMethod(SchematicCommands.class, "save", Actor.class, LocalSession.class, String.class, ClipboardFormat.class, Boolean.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("share", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Share your clipboard as a schematic online"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.schematicNamePart, (Object)this.destinationPart, (Object)this.formatPart2));
            b.action(this::cmd$share);
            Method commandMethod = RegistrationUtil.getCommandMethod(SchematicCommands.class, "share", Actor.class, LocalSession.class, String.class, ClipboardShareDestination.class, ClipboardFormat.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("delete", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"d"));
            b.description(TextComponent.of("Delete a saved schematic"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.filenamePart));
            b.action(this::cmd$delete);
            Method commandMethod = RegistrationUtil.getCommandMethod(SchematicCommands.class, "delete", Actor.class, String.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("formats", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"listformats", (Object)"f"));
            b.description(TextComponent.of("List available formats"));
            b.parts((Collection<CommandPart>)ImmutableList.of());
            b.action(this::cmd$formats);
            Method commandMethod = RegistrationUtil.getCommandMethod(SchematicCommands.class, "formats", Actor.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("list", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"all", (Object)"ls"));
            b.description(TextComponent.of("List saved schematics"));
            b.footer(TextComponent.of("Note: Format is not fully verified until loading."));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.pagePart, (Object)this.oldFirstPart, (Object)this.newFirstPart));
            b.action(this::cmd$list);
            Method commandMethod = RegistrationUtil.getCommandMethod(SchematicCommands.class, "list", Actor.class, Integer.TYPE, Boolean.TYPE, Boolean.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
    }

    private int cmd$load(CommandParameters parameters) throws FilenameException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(SchematicCommands.class, "load", Actor.class, LocalSession.class, String.class, ClipboardFormat.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.load(this.extract$actor(parameters), this.extract$session(parameters), this.extract$filename(parameters), this.extract$format(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$save(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(SchematicCommands.class, "save", Actor.class, LocalSession.class, String.class, ClipboardFormat.class, Boolean.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.save(this.extract$actor(parameters), this.extract$session(parameters), this.extract$filename(parameters), this.extract$format(parameters), this.extract$allowOverwrite(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$share(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(SchematicCommands.class, "share", Actor.class, LocalSession.class, String.class, ClipboardShareDestination.class, ClipboardFormat.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.share(this.extract$actor(parameters), this.extract$session(parameters), this.extract$schematicName(parameters), this.extract$destination(parameters), this.extract$format2(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$delete(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(SchematicCommands.class, "delete", Actor.class, String.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.delete(this.extract$actor(parameters), this.extract$filename(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$formats(CommandParameters parameters) {
        Method cmdMethod = RegistrationUtil.getCommandMethod(SchematicCommands.class, "formats", Actor.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.formats(this.extract$actor(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$list(CommandParameters parameters) {
        Method cmdMethod = RegistrationUtil.getCommandMethod(SchematicCommands.class, "list", Actor.class, Integer.TYPE, Boolean.TYPE, Boolean.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.list(this.extract$actor(parameters), this.extract$page(parameters), this.extract$oldFirst(parameters), this.extract$newFirst(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private Actor extract$actor(CommandParameters parameters) {
        return RegistrationUtil.requireOptional(actor_Key, "actor", parameters.injectedValue(actor_Key));
    }

    private LocalSession extract$session(CommandParameters parameters) {
        return RegistrationUtil.requireOptional(localSession_Key, "session", parameters.injectedValue(localSession_Key));
    }

    private String extract$filename(CommandParameters parameters) {
        return this.filenamePart.value(parameters).asSingle(string_Key);
    }

    private ClipboardFormat extract$format(CommandParameters parameters) {
        return this.formatPart.value(parameters).asSingle(clipboardFormat_Key);
    }

    private boolean extract$allowOverwrite(CommandParameters parameters) {
        return this.allowOverwritePart.in(parameters);
    }

    private String extract$schematicName(CommandParameters parameters) {
        return this.schematicNamePart.value(parameters).asSingle(string_Key);
    }

    private ClipboardShareDestination extract$destination(CommandParameters parameters) {
        return this.destinationPart.value(parameters).asSingle(clipboardShareDestination_Key);
    }

    private ClipboardFormat extract$format2(CommandParameters parameters) {
        return this.formatPart2.value(parameters).asSingle(clipboardFormat_Key);
    }

    private int extract$page(CommandParameters parameters) {
        return this.pagePart.value(parameters).asSingle(integer_Key);
    }

    private boolean extract$oldFirst(CommandParameters parameters) {
        return this.oldFirstPart.in(parameters);
    }

    private boolean extract$newFirst(CommandParameters parameters) {
        return this.newFirstPart.in(parameters);
    }
}

