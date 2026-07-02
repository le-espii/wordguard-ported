/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.reflect.TypeToken
 *  org.apache.logging.log4j.Logger
 */
package com.sk89q.worldedit.extension.platform;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MissingWorldException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.command.ApplyBrushCommands;
import com.sk89q.worldedit.command.BiomeCommands;
import com.sk89q.worldedit.command.BiomeCommandsRegistration;
import com.sk89q.worldedit.command.BrushCommands;
import com.sk89q.worldedit.command.BrushCommandsRegistration;
import com.sk89q.worldedit.command.ChunkCommands;
import com.sk89q.worldedit.command.ChunkCommandsRegistration;
import com.sk89q.worldedit.command.ClipboardCommands;
import com.sk89q.worldedit.command.ClipboardCommandsRegistration;
import com.sk89q.worldedit.command.ExpandCommands;
import com.sk89q.worldedit.command.GeneralCommands;
import com.sk89q.worldedit.command.GenerationCommands;
import com.sk89q.worldedit.command.GenerationCommandsRegistration;
import com.sk89q.worldedit.command.HistoryCommands;
import com.sk89q.worldedit.command.HistoryCommandsRegistration;
import com.sk89q.worldedit.command.NavigationCommands;
import com.sk89q.worldedit.command.NavigationCommandsRegistration;
import com.sk89q.worldedit.command.PaintBrushCommands;
import com.sk89q.worldedit.command.RegionCommands;
import com.sk89q.worldedit.command.RegionCommandsRegistration;
import com.sk89q.worldedit.command.SchematicCommands;
import com.sk89q.worldedit.command.SchematicCommandsRegistration;
import com.sk89q.worldedit.command.ScriptingCommands;
import com.sk89q.worldedit.command.ScriptingCommandsRegistration;
import com.sk89q.worldedit.command.SelectionCommands;
import com.sk89q.worldedit.command.SelectionCommandsRegistration;
import com.sk89q.worldedit.command.SnapshotCommands;
import com.sk89q.worldedit.command.SnapshotCommandsRegistration;
import com.sk89q.worldedit.command.SnapshotUtilCommands;
import com.sk89q.worldedit.command.SnapshotUtilCommandsRegistration;
import com.sk89q.worldedit.command.SuperPickaxeCommands;
import com.sk89q.worldedit.command.SuperPickaxeCommandsRegistration;
import com.sk89q.worldedit.command.ToolCommands;
import com.sk89q.worldedit.command.ToolUtilCommands;
import com.sk89q.worldedit.command.ToolUtilCommandsRegistration;
import com.sk89q.worldedit.command.UtilityCommands;
import com.sk89q.worldedit.command.UtilityCommandsRegistration;
import com.sk89q.worldedit.command.WorldEditCommands;
import com.sk89q.worldedit.command.WorldEditCommandsRegistration;
import com.sk89q.worldedit.command.argument.Arguments;
import com.sk89q.worldedit.command.argument.BooleanConverter;
import com.sk89q.worldedit.command.argument.Chunk3dVectorConverter;
import com.sk89q.worldedit.command.argument.ClipboardFormatConverter;
import com.sk89q.worldedit.command.argument.ClipboardShareDestinationConverter;
import com.sk89q.worldedit.command.argument.CommaSeparatedValuesConverter;
import com.sk89q.worldedit.command.argument.DirectionConverter;
import com.sk89q.worldedit.command.argument.DirectionVectorConverter;
import com.sk89q.worldedit.command.argument.EntityRemoverConverter;
import com.sk89q.worldedit.command.argument.EnumConverter;
import com.sk89q.worldedit.command.argument.FactoryConverter;
import com.sk89q.worldedit.command.argument.HeightConverter;
import com.sk89q.worldedit.command.argument.OffsetConverter;
import com.sk89q.worldedit.command.argument.RegionFactoryConverter;
import com.sk89q.worldedit.command.argument.RegistryConverter;
import com.sk89q.worldedit.command.argument.SelectorChoiceConverter;
import com.sk89q.worldedit.command.argument.SideEffectConverter;
import com.sk89q.worldedit.command.argument.SideEffectSetConverter;
import com.sk89q.worldedit.command.argument.VectorConverter;
import com.sk89q.worldedit.command.argument.WorldConverter;
import com.sk89q.worldedit.command.argument.ZonedDateTimeConverter;
import com.sk89q.worldedit.command.util.PermissionCondition;
import com.sk89q.worldedit.command.util.SubCommandPermissionCondition;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.event.platform.CommandEvent;
import com.sk89q.worldedit.event.platform.CommandSuggestionEvent;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.Annotations;
import com.sk89q.worldedit.extension.platform.Locatable;
import com.sk89q.worldedit.extension.platform.Platform;
import com.sk89q.worldedit.extension.platform.PlatformManager;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.internal.annotation.OptionalArg;
import com.sk89q.worldedit.internal.annotation.Selection;
import com.sk89q.worldedit.internal.command.CommandArgParser;
import com.sk89q.worldedit.internal.command.CommandLoggingHandler;
import com.sk89q.worldedit.internal.command.CommandRegistrationHandler;
import com.sk89q.worldedit.internal.command.exception.ExceptionConverter;
import com.sk89q.worldedit.internal.command.exception.WorldEditExceptionConverter;
import com.sk89q.worldedit.internal.util.ErrorReporting;
import com.sk89q.worldedit.internal.util.LogManagerCompat;
import com.sk89q.worldedit.internal.util.Substring;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.request.Request;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldedit.util.logging.DynamicStreamHandler;
import com.sk89q.worldedit.util.logging.LogFormat;
import com.sk89q.worldedit.world.World;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.enginehub.piston.Command;
import org.enginehub.piston.CommandManager;
import org.enginehub.piston.converter.ArgumentConverters;
import org.enginehub.piston.exception.CommandException;
import org.enginehub.piston.exception.CommandExecutionException;
import org.enginehub.piston.exception.ConditionFailedException;
import org.enginehub.piston.exception.UsageException;
import org.enginehub.piston.gen.CommandCallListener;
import org.enginehub.piston.gen.CommandRegistration;
import org.enginehub.piston.impl.CommandManagerServiceImpl;
import org.enginehub.piston.inject.InjectedValueStore;
import org.enginehub.piston.inject.Key;
import org.enginehub.piston.inject.MapBackedValueStore;
import org.enginehub.piston.inject.MemoizingValueAccess;
import org.enginehub.piston.inject.MergedValueAccess;
import org.enginehub.piston.part.SubCommandPart;
import org.enginehub.piston.suggestion.Suggestion;
import org.enginehub.piston.util.HelpGenerator;
import org.enginehub.piston.util.ValueProvider;

public final class PlatformCommandManager {
    public static final Pattern COMMAND_CLEAN_PATTERN = Pattern.compile("^[/]+");
    private static final org.apache.logging.log4j.Logger LOGGER = LogManagerCompat.getLogger();
    private static final Logger COMMAND_LOG = Logger.getLogger("com.sk89q.worldedit.CommandLog");
    private final WorldEdit worldEdit;
    private final PlatformManager platformManager;
    private final CommandManagerServiceImpl commandManagerService;
    private final CommandManager commandManager;
    private final InjectedValueStore globalInjectedValues;
    private final DynamicStreamHandler dynamicHandler = new DynamicStreamHandler();
    private final WorldEditExceptionConverter exceptionConverter;
    private final CommandRegistrationHandler registration;

    PlatformCommandManager(WorldEdit worldEdit, PlatformManager platformManager) {
        Preconditions.checkNotNull((Object)worldEdit);
        Preconditions.checkNotNull((Object)platformManager);
        this.worldEdit = worldEdit;
        this.platformManager = platformManager;
        this.exceptionConverter = new WorldEditExceptionConverter(worldEdit);
        this.commandManagerService = new CommandManagerServiceImpl();
        this.commandManager = this.commandManagerService.newCommandManager();
        this.globalInjectedValues = MapBackedValueStore.create();
        this.registration = new CommandRegistrationHandler((List<CommandCallListener>)ImmutableList.of((Object)new CommandLoggingHandler(worldEdit, COMMAND_LOG)));
        this.initialize();
    }

    private void initialize() {
        this.worldEdit.getEventBus().register(this);
        COMMAND_LOG.addHandler(this.dynamicHandler);
        this.registerAlwaysInjectedValues();
        this.registerArgumentConverters();
        this.registerAllCommands();
    }

    private void registerArgumentConverters() {
        DirectionVectorConverter.register(this.worldEdit, this.commandManager);
        DirectionConverter.register(this.worldEdit, this.commandManager);
        FactoryConverter.register(this.worldEdit, this.commandManager);
        for (int count = 2; count <= 3; ++count) {
            this.commandManager.registerConverter(Key.of(Double.TYPE, (Annotation)Annotations.radii(count)), CommaSeparatedValuesConverter.wrapAndLimit(ArgumentConverters.get(TypeToken.of(Double.TYPE)), count));
        }
        VectorConverter.register(this.commandManager);
        Chunk3dVectorConverter.register(this.commandManager);
        EnumConverter.register(this.commandManager);
        RegistryConverter.register(this.commandManager);
        ZonedDateTimeConverter.register(this.commandManager);
        BooleanConverter.register(this.commandManager);
        EntityRemoverConverter.register(this.commandManager);
        RegionFactoryConverter.register(this.commandManager);
        WorldConverter.register(this.commandManager);
        SideEffectConverter.register(this.commandManager);
        SideEffectSetConverter.register(this.commandManager);
        HeightConverter.register(this.commandManager);
        OffsetConverter.register(this.worldEdit, this.commandManager);
        ClipboardFormatConverter.register(this.commandManager);
        ClipboardShareDestinationConverter.register(this.commandManager);
        SelectorChoiceConverter.register(this.commandManager);
    }

    private void registerAlwaysInjectedValues() {
        this.globalInjectedValues.injectValue(Key.of(Region.class, Selection.class), context -> {
            LocalSession localSession = context.injectedValue(Key.of(LocalSession.class)).orElseThrow(() -> new IllegalStateException("No LocalSession"));
            return context.injectedValue(Key.of(World.class)).map(world -> {
                try {
                    return localSession.getSelection((World)world);
                }
                catch (IncompleteRegionException e) {
                    this.exceptionConverter.convert(e);
                    throw new AssertionError("Should have thrown a new exception.", e);
                }
            });
        });
        this.globalInjectedValues.injectValue(Key.of(EditSession.class), context -> {
            LocalSession localSession = context.injectedValue(Key.of(LocalSession.class)).orElseThrow(() -> new IllegalStateException("No LocalSession"));
            return context.injectedValue(Key.of(Actor.class)).map(actor -> {
                EditSession editSession = localSession.createEditSession((Actor)actor);
                editSession.enableStandardMode();
                return editSession;
            });
        });
        this.globalInjectedValues.injectValue(Key.of(World.class), context -> {
            LocalSession localSession = context.injectedValue(Key.of(LocalSession.class)).orElseThrow(() -> new IllegalStateException("No LocalSession"));
            return context.injectedValue(Key.of(Actor.class)).map(actor -> {
                try {
                    if (localSession.hasWorldOverride()) {
                        return localSession.getWorldOverride();
                    }
                    if (actor instanceof Locatable && ((Locatable)((Object)actor)).getExtent() instanceof World) {
                        return (World)((Locatable)((Object)actor)).getExtent();
                    }
                    throw new MissingWorldException();
                }
                catch (MissingWorldException e) {
                    this.exceptionConverter.convert(e);
                    throw new AssertionError("Should have thrown a new exception.", e);
                }
            });
        });
    }

    public <CI> void registerSubCommands(String name, List<String> aliases, String desc, CommandRegistration<CI> registration, CI instance) {
        this.registerSubCommands(name, aliases, desc, registration, instance, m -> {});
    }

    private <CI> void registerSubCommands(String name, List<String> aliases, String desc, CommandRegistration<CI> registration, CI instance, Consumer<CommandManager> additionalConfig) {
        this.commandManager.register(name, cmd -> {
            cmd.aliases(aliases);
            cmd.description(TextComponent.of(desc));
            cmd.action(Command.Action.NULL_ACTION);
            CommandManager manager = this.commandManagerService.newCommandManager();
            this.registration.register(manager, registration, instance);
            additionalConfig.accept(manager);
            List<Command> subCommands = manager.getAllCommands().collect(Collectors.toList());
            cmd.addPart(SubCommandPart.builder(TranslatableComponent.of("worldedit.argument.action"), TextComponent.of("Sub-command to run.")).withCommands(subCommands).required().build());
            cmd.condition(new SubCommandPermissionCondition.Generator(subCommands).build());
        });
    }

    private void registerAllCommands() {
        this.registerSubCommands("schematic", (List<String>)ImmutableList.of((Object)"schem", (Object)"/schematic", (Object)"/schem"), "Schematic commands for saving/loading areas", SchematicCommandsRegistration.builder(), new SchematicCommands(this.worldEdit));
        this.registerSubCommands("snapshot", (List<String>)ImmutableList.of((Object)"snap"), "Snapshot commands for restoring backups", SnapshotCommandsRegistration.builder(), new SnapshotCommands(this.worldEdit));
        this.registerSubCommands("superpickaxe", (List<String>)ImmutableList.of((Object)"pickaxe", (Object)"sp"), "Super-pickaxe commands", SuperPickaxeCommandsRegistration.builder(), new SuperPickaxeCommands(this.worldEdit));
        this.registerSubCommands("brush", (List<String>)ImmutableList.of((Object)"br", (Object)"/brush", (Object)"/br"), "Brushing commands", BrushCommandsRegistration.builder(), new BrushCommands(this.worldEdit), manager -> {
            PaintBrushCommands.register(this.commandManagerService, manager, this.registration);
            ApplyBrushCommands.register(this.commandManagerService, manager, this.registration);
        });
        this.registerSubCommands("worldedit", (List<String>)ImmutableList.of((Object)"we"), "WorldEdit commands", WorldEditCommandsRegistration.builder(), new WorldEditCommands(this.worldEdit));
        this.registration.register(this.commandManager, BiomeCommandsRegistration.builder(), new BiomeCommands());
        this.registration.register(this.commandManager, ChunkCommandsRegistration.builder(), new ChunkCommands(this.worldEdit));
        this.registration.register(this.commandManager, ClipboardCommandsRegistration.builder(), new ClipboardCommands());
        GeneralCommands.register(this.registration, this.commandManager, this.commandManagerService, this.worldEdit);
        this.registration.register(this.commandManager, GenerationCommandsRegistration.builder(), new GenerationCommands(this.worldEdit));
        this.registration.register(this.commandManager, HistoryCommandsRegistration.builder(), new HistoryCommands(this.worldEdit));
        this.registration.register(this.commandManager, NavigationCommandsRegistration.builder(), new NavigationCommands(this.worldEdit));
        this.registration.register(this.commandManager, RegionCommandsRegistration.builder(), new RegionCommands());
        this.registration.register(this.commandManager, ScriptingCommandsRegistration.builder(), new ScriptingCommands(this.worldEdit));
        this.registration.register(this.commandManager, SelectionCommandsRegistration.builder(), new SelectionCommands(this.worldEdit));
        ExpandCommands.register(this.registration, this.commandManager, this.commandManagerService);
        this.registration.register(this.commandManager, SnapshotUtilCommandsRegistration.builder(), new SnapshotUtilCommands(this.worldEdit));
        ToolCommands.register(this.registration, this.commandManager, this.commandManagerService, this.worldEdit);
        this.registration.register(this.commandManager, ToolUtilCommandsRegistration.builder(), new ToolUtilCommands(this.worldEdit));
        this.registration.register(this.commandManager, UtilityCommandsRegistration.builder(), new UtilityCommands(this.worldEdit));
    }

    public ExceptionConverter getExceptionConverter() {
        return this.exceptionConverter;
    }

    void registerCommandsWith(Platform platform) {
        LOGGER.info("Registering commands with " + platform.getClass().getCanonicalName());
        LocalConfiguration config = platform.getConfiguration();
        boolean logging = config.logCommands;
        String path = config.logFile;
        if (!logging || path.isEmpty()) {
            this.dynamicHandler.setHandler(null);
            COMMAND_LOG.setLevel(Level.OFF);
        } else {
            Path file = config.getWorkingDirectoryPath().resolve(path).toAbsolutePath();
            COMMAND_LOG.setLevel(Level.ALL);
            LOGGER.info("Logging WorldEdit commands to " + String.valueOf(file));
            try {
                this.dynamicHandler.setHandler(new FileHandler(file.toString(), true));
            }
            catch (IOException e) {
                LOGGER.warn("Could not use command log file " + path + ": " + e.getMessage());
            }
            this.dynamicHandler.setFormatter(new LogFormat(config.logFormat));
        }
        platform.registerCommands(this.commandManager);
    }

    void removeCommands() {
        this.dynamicHandler.setHandler(null);
    }

    private Stream<Substring> parseArgs(String input) {
        return CommandArgParser.forArgString(input.substring(1)).parseArgs();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Subscribe
    public void handleCommand(CommandEvent event) {
        block25: {
            Extent extent;
            Request.reset();
            Actor actor = this.platformManager.createProxyActor(event.getActor());
            Object[] split = (String[])this.parseArgs(event.getArguments()).map(Substring::getSubstring).toArray(String[]::new);
            if (!this.commandManager.containsCommand(split[0])) {
                return;
            }
            LocalSession session = this.worldEdit.getSessionManager().get(actor);
            Request.request().setSession(session);
            if (actor instanceof Entity && (extent = ((Entity)((Object)actor)).getExtent()) instanceof World) {
                Request.request().setWorld((World)extent);
            }
            LocalConfiguration config = this.worldEdit.getConfiguration();
            MemoizingValueAccess context = this.initializeInjectedValues(event::getArguments, actor);
            long start = System.currentTimeMillis();
            try {
                try {
                    EditSession editSession;
                    block26: {
                        try {
                            this.commandManager.execute(context, (List<String>)ImmutableList.copyOf((Object[])split));
                            Optional<EditSession> editSessionOpt = context.snapshotMemory().injectedValue(Key.of(EditSession.class));
                            if (!editSessionOpt.isPresent()) break block25;
                            editSession = editSessionOpt.get();
                            session.remember(editSession);
                            editSession.close();
                            if (!config.profile) break block26;
                        }
                        catch (Throwable throwable) {
                            Optional<EditSession> editSessionOpt = context.snapshotMemory().injectedValue(Key.of(EditSession.class));
                            if (editSessionOpt.isPresent()) {
                                EditSession editSession2 = editSessionOpt.get();
                                session.remember(editSession2);
                                editSession2.close();
                                if (config.profile) {
                                    long time = System.currentTimeMillis() - start;
                                    double timeS = (double)time / 1000.0;
                                    int changed = editSession2.getBlockChangeCount();
                                    double throughput = timeS == 0.0 ? (double)changed : (double)changed / timeS;
                                    actor.printDebug(TranslatableComponent.of("worldedit.command.time-elapsed", TextComponent.of(timeS), TextComponent.of(changed), TextComponent.of(Math.round(throughput))));
                                }
                                this.worldEdit.flushBlockBag(actor, editSession2);
                            }
                            throw throwable;
                        }
                        long time = System.currentTimeMillis() - start;
                        double timeS = (double)time / 1000.0;
                        int changed = editSession.getBlockChangeCount();
                        double throughput = timeS == 0.0 ? (double)changed : (double)changed / timeS;
                        actor.printDebug(TranslatableComponent.of("worldedit.command.time-elapsed", TextComponent.of(timeS), TextComponent.of(changed), TextComponent.of(Math.round(throughput))));
                    }
                    this.worldEdit.flushBlockBag(actor, editSession);
                }
                catch (Throwable t) {
                    Throwable next = t;
                    do {
                        this.exceptionConverter.convert(next);
                    } while ((next = next.getCause()) != null);
                    throw t;
                }
            }
            catch (ConditionFailedException e) {
                if (e.getCondition() instanceof PermissionCondition) {
                    actor.printError(TranslatableComponent.of("worldedit.command.permissions"));
                } else {
                    actor.print(e.getRichMessage());
                }
            }
            catch (UsageException e) {
                actor.print((Component)((TextComponent.Builder)((TextComponent.Builder)TextComponent.builder("").color(TextColor.RED)).append(e.getRichMessage())).build());
                ImmutableList<Command> cmd = e.getCommands();
                if (!cmd.isEmpty()) {
                    actor.printError(TranslatableComponent.of("worldedit.error.incorrect-usage", HelpGenerator.create(e.getCommandParseResult()).getUsage()));
                }
            }
            catch (CommandExecutionException e) {
                this.handleUnknownException(actor, e.getCause());
            }
            catch (CommandException e) {
                actor.printError(e.getRichMessage());
            }
            catch (Throwable t) {
                this.handleUnknownException(actor, t);
            }
            finally {
                Request.reset();
            }
        }
        event.setCancelled(true);
    }

    private MemoizingValueAccess initializeInjectedValues(Arguments arguments, Actor actor) {
        MapBackedValueStore store = MapBackedValueStore.create();
        store.injectValue(Key.of(Actor.class), ValueProvider.constant(actor));
        if (actor instanceof Player) {
            store.injectValue(Key.of(Player.class), ValueProvider.constant((Player)actor));
            store.injectValue(Key.of(Player.class, OptionalArg.class), ValueProvider.constant((Player)actor));
        } else {
            store.injectValue(Key.of(Player.class), context -> {
                throw new CommandException(TranslatableComponent.of("worldedit.command.player-only"), (ImmutableList<Command>)ImmutableList.of());
            });
        }
        store.injectValue(Key.of(Arguments.class), ValueProvider.constant(arguments));
        store.injectValue(Key.of(LocalSession.class), context -> {
            LocalSession localSession = this.worldEdit.getSessionManager().get(actor);
            localSession.tellVersion(actor);
            return Optional.of(localSession);
        });
        return MemoizingValueAccess.wrap(MergedValueAccess.of(store, this.globalInjectedValues));
    }

    private void handleUnknownException(Actor actor, Throwable t) {
        LOGGER.error("An unexpected error while handling a WorldEdit command", t);
        ErrorReporting.trigger(actor, t);
    }

    @Subscribe
    public void handleCommandSuggestion(CommandSuggestionEvent event) {
        block5: {
            try {
                ImmutableSet<Suggestion> suggestions;
                String arguments = event.getArguments();
                List split = this.parseArgs(arguments).toList();
                List<String> argStrings = split.stream().map(Substring::getSubstring).collect(Collectors.toList());
                MemoizingValueAccess access = this.initializeInjectedValues(() -> arguments, event.getActor());
                try {
                    suggestions = this.commandManager.getSuggestions(access, argStrings);
                }
                catch (Throwable t) {
                    if (!(t instanceof CommandException)) {
                        LOGGER.debug("Unexpected error occurred while generating suggestions for input: " + arguments, t);
                        return;
                    }
                    throw t;
                }
                event.setSuggestions(suggestions.stream().map(suggestion -> {
                    int noSlashLength = arguments.length() - 1;
                    Substring original = suggestion.getReplacedArgument() == split.size() ? Substring.from(arguments, noSlashLength, noSlashLength) : (Substring)split.get(suggestion.getReplacedArgument());
                    return Substring.wrap(suggestion.getSuggestion(), original.getStart() + 1, original.getEnd() + 1);
                }).collect(Collectors.toList()));
            }
            catch (ConditionFailedException e) {
                if (!(e.getCondition() instanceof PermissionCondition)) break block5;
                event.setSuggestions(new ArrayList<Substring>());
            }
        }
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }
}

