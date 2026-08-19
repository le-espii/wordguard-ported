package com.sk89q.worldguard.mod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.sk89q.minecraft.util.commands.CommandsManager;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.commands.GeneralCommands;
import com.sk89q.worldguard.commands.ProtectionCommands;
import com.sk89q.worldguard.commands.ToggleCommands;
import com.sk89q.worldguard.protection.flags.registry.SimpleFlagRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class WorldGuardModCommon {
    private static final Logger LOGGER = LogManager.getLogger("WorldGuard");
    private static ModWorldGuardPlatform platform;
    private static CommandsManager<Actor> commands;
    private static MinecraftServer server;
    private static PlatformAdapter adapter;

    /**
     * Grupos de alias de comandos de nivel superior que WorldGuard registra por su cuenta
     * (no como subcomandos de /wg). El primer elemento de cada grupo es el alias "canonico"
     * que se le pasa a {@link CommandsManager}; el resto son alias adicionales que apuntan
     * al mismo comando. Debe reflejar exactamente lo que registran GeneralCommands/
     * ProtectionCommands/ToggleCommands en el plugin original (WorldGuard-Bukkit).
     */
    private static final String[][] COMMAND_GROUPS = {
        {"region", "rg", "regions"},
        {"worldguard", "wg"},
        {"god"},
        {"ungod"},
        {"heal"},
        {"slay"},
        {"locate"},
        {"stack", ";"},
        {"stopfire"},
        {"allowfire"},
        {"halt-activity", "stoplag", "haltactivity"},
    };

    public interface PlatformAdapter {
        com.sk89q.worldedit.world.World adaptWorld(net.minecraft.world.level.Level level);
        com.sk89q.worldguard.LocalPlayer adaptPlayer(net.minecraft.server.level.ServerPlayer player);
        com.sk89q.worldedit.extension.platform.Actor adaptCommandSource(net.minecraft.commands.CommandSourceStack source);
    }

    public static void init() {
        LOGGER.info("Initializing WorldGuard Mod Common...");

        platform = new ModWorldGuardPlatform();
        WorldGuard.getInstance().setPlatform(platform);

        // Registrar el CommandsManager para procesar los comandos clásicos
        commands = new CommandsManager<Actor>() {
            @Override
            public boolean hasPermission(Actor actor, String perm) {
                return actor.hasPermission(perm);
            }
        };

        commands.setInjector(new com.sk89q.minecraft.util.commands.Injector() {
            @Override
            public Object getInstance(Class<?> clazz) {
                try {
                    try {
                        return clazz.getConstructor(WorldGuard.class).newInstance(WorldGuard.getInstance());
                    } catch (NoSuchMethodException e) {
                        return clazz.getConstructor().newInstance();
                    }
                } catch (Exception e) {
                    LOGGER.error("Failed to inject command class: " + clazz.getName(), e);
                    return null;
                }
            }
        });

        commands.register(ToggleCommands.class);
        commands.register(ProtectionCommands.class);
        commands.register(GeneralCommands.class);

        ((SimpleFlagRegistry) WorldGuard.getInstance().getFlagRegistry()).setInitialized(true);
        LOGGER.info("WorldGuard Mod Common initialized successfully.");
    }

    public static void setAdapter(PlatformAdapter a) {
        adapter = a;
    }

    public static PlatformAdapter getAdapter() {
        return adapter;
    }

    public static void setServer(MinecraftServer s) {
        server = s;
    }

    public static MinecraftServer getServer() {
        return server;
    }

    public static ModWorldGuardPlatform getPlatform() {
        return platform;
    }

    public static CommandsManager<Actor> getCommands() {
        return commands;
    }

    // ------------------------------------------------------------------
    // Registro de comandos Brigadier (compartido entre Fabric y NeoForge)
    // ------------------------------------------------------------------

    /**
     * Registra todos los comandos de nivel superior de WorldGuard (region/wg y los comandos
     * "sueltos" como god/heal/slay/stopfire/etc.) en el dispatcher de Brigadier del servidor.
     * Fabric y NeoForge obtienen el {@link CommandDispatcher} de forma distinta (callback vs
     * evento), pero una vez obtenido, el árbol de comandos que se registra es idéntico en
     * ambos loaders, así que esta lógica vive una sola vez aquí.
     */
    public static void registerAllCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        for (String[] group : COMMAND_GROUPS) {
            String canonicalName = group[0];
            for (String alias : group) {
                dispatcher.register(literal(alias)
                    .then(argument("args", StringArgumentType.greedyString())
                        .suggests(getSuggestionsProvider(canonicalName))
                        .executes(context -> executeCommand(context, canonicalName, StringArgumentType.getString(context, "args")))
                    )
                    .executes(context -> executeCommand(context, canonicalName, ""))
                );
            }
        }
    }

    private static int executeCommand(CommandContext<CommandSourceStack> context, String commandName, String argsString) {
        CommandSourceStack source = context.getSource();

        Actor actor;
        if (source.getPlayer() != null) {
            actor = WorldGuardModCommon.getAdapter().adaptPlayer(source.getPlayer());
        } else {
            actor = WorldGuardModCommon.getAdapter().adaptCommandSource(source);
        }

        String[] args = parseArguments(argsString);

        if (commandName.equalsIgnoreCase("worldguard") && (args.length == 0 || args[0].equalsIgnoreCase("version") || args[0].equalsIgnoreCase("credits"))) {
            source.sendSuccess(() -> Component.literal(WorldGuardLang.get(source.getPlayer(), "credits.block")), false);
        }

        try {
            WorldGuardModCommon.getCommands().execute(commandName, args, actor, actor);
            return 1;
        } catch (com.sk89q.minecraft.util.commands.CommandPermissionsException e) {
            source.sendFailure(Component.literal(WorldGuardLang.get(source.getPlayer(), "command.no_permission")));
            return 0;
        } catch (com.sk89q.minecraft.util.commands.CommandUsageException e) {
            source.sendFailure(Component.literal("§c" + e.getMessage()));
            if (e.getUsage() != null) {
                source.sendFailure(Component.literal("§c" + e.getUsage()));
            }
            return 0;
        } catch (Throwable t) {
            LOGGER.error("Error executing command: " + commandName + " " + argsString, t);
            source.sendFailure(Component.literal(WorldGuardLang.get(source.getPlayer(), "command.exec_error", t.getMessage())));
            return 0;
        }
    }

    /**
     * Divide una cadena de argumentos respetando comillas dobles.
     */
    private static String[] parseArguments(String argsString) {
        if (argsString == null || argsString.trim().isEmpty()) {
            return new String[0];
        }
        List<String> list = new ArrayList<>();
        Matcher m = Pattern.compile("([^\"]\\S*|\"[^\"]*\")\\s*").matcher(argsString);
        while (m.find()) {
            String arg = m.group(1);
            if (arg.startsWith("\"") && arg.endsWith("\"")) {
                arg = arg.substring(1, arg.length() - 1);
            }
            list.add(arg);
        }
        return list.toArray(new String[0]);
    }

    private static com.mojang.brigadier.suggestion.SuggestionProvider<CommandSourceStack> getSuggestionsProvider(String commandName) {
        return (context, builder) -> {
            String argsString = "";
            try {
                argsString = StringArgumentType.getString(context, "args");
            } catch (IllegalArgumentException ignored) {}

            List<String> suggestions = WorldGuardModCommon.getSuggestions(context.getSource(), commandName, argsString);

            String input = context.getInput();
            int lastSpace = input.lastIndexOf(' ');
            int start = lastSpace == -1 ? 0 : lastSpace + 1;
            int end = input.length();

            List<com.mojang.brigadier.suggestion.Suggestion> list = new ArrayList<>();
            for (String suggestion : suggestions) {
                list.add(new com.mojang.brigadier.suggestion.Suggestion(
                    com.mojang.brigadier.context.StringRange.between(start, end),
                    suggestion
                ));
            }
            return CompletableFuture.completedFuture(
                com.mojang.brigadier.suggestion.Suggestions.create(input, list)
            );
        };
    }

    public static List<String> getSuggestions(net.minecraft.commands.CommandSourceStack source, String commandName, String argsString) {
        List<String> suggestions = new ArrayList<>();
        if (argsString == null) argsString = "";

        // Separar en palabras, manteniendo espacios al final
        String[] words;
        if (argsString.isEmpty()) {
            words = new String[]{""};
        } else if (argsString.endsWith(" ")) {
            String[] split = argsString.split(" ");
            words = new String[split.length + 1];
            System.arraycopy(split, 0, words, 0, split.length);
            words[split.length] = "";
        } else {
            words = argsString.split(" ");
        }

        int wordCount = words.length;
        String lastWord = words[wordCount - 1].toLowerCase();

        net.minecraft.server.level.ServerPlayer mcPlayer = null;
        try {
            mcPlayer = source.getPlayerOrException();
        } catch (Exception ignored) {}

        if (commandName.equalsIgnoreCase("worldguard")) {
            if (wordCount == 1) {
                String[] subcommands = {"version", "reload", "report", "profile", "flushstates", "credits", "stopprofile"};
                for (String sub : subcommands) {
                    if (sub.startsWith(lastWord)) {
                        suggestions.add(sub);
                    }
                }
            }
        } else if (commandName.equalsIgnoreCase("region")) {
            // region / rg
            if (wordCount == 1) {
                String[] subcommands = {
                    "define", "flag", "info", "addmember", "removemember", "addowner", "removeowner",
                    "list", "delete", "remove", "redefine", "select", "setparent", "setpriority", "teleport",
                    "load", "save"
                };
                for (String sub : subcommands) {
                    if (sub.startsWith(lastWord)) {
                        suggestions.add(sub);
                    }
                }
            } else if (wordCount == 2) {
                String subcmd = words[0].toLowerCase();
                if (subcmd.equals("flag") || subcmd.equals("info") || subcmd.equals("addmember") ||
                    subcmd.equals("removemember") || subcmd.equals("addowner") || subcmd.equals("removeowner") ||
                    subcmd.equals("delete") || subcmd.equals("remove") || subcmd.equals("redefine") ||
                    subcmd.equals("setparent") || subcmd.equals("setpriority") || subcmd.equals("teleport") ||
                    subcmd.equals("select")) {

                    if (mcPlayer != null) {
                        com.sk89q.worldedit.world.World world = WorldGuardModCommon.getAdapter().adaptWorld(mcPlayer.level());
                        com.sk89q.worldguard.protection.managers.RegionManager manager =
                            WorldGuard.getInstance().getPlatform().getRegionContainer().get(world);
                        if (manager != null) {
                            for (String regionId : manager.getRegions().keySet()) {
                                if (regionId.toLowerCase().startsWith(lastWord)) {
                                    suggestions.add(regionId);
                                }
                            }
                        }
                    }
                }
            } else if (wordCount == 3) {
                String subcmd = words[0].toLowerCase();
                String regionId = words[1];

                if (subcmd.equals("flag")) {
                    for (com.sk89q.worldguard.protection.flags.Flag<?> flag : WorldGuard.getInstance().getFlagRegistry().getAll()) {
                        String flagName = flag.getName().toLowerCase();
                        if (flagName.startsWith(lastWord)) {
                            suggestions.add(flagName);
                        }
                    }
                } else if (subcmd.equals("addmember") || subcmd.equals("addowner") || subcmd.equals("removemember") || subcmd.equals("removeowner")) {
                    if (mcPlayer != null && mcPlayer.getServer() != null) {
                        for (String playerName : mcPlayer.getServer().getPlayerNames()) {
                            if (playerName.toLowerCase().startsWith(lastWord)) {
                                suggestions.add(playerName);
                            }
                        }
                    }
                } else if (subcmd.equals("setparent")) {
                    if (mcPlayer != null) {
                        com.sk89q.worldedit.world.World world = WorldGuardModCommon.getAdapter().adaptWorld(mcPlayer.level());
                        com.sk89q.worldguard.protection.managers.RegionManager manager =
                            WorldGuard.getInstance().getPlatform().getRegionContainer().get(world);
                        if (manager != null) {
                            for (String rId : manager.getRegions().keySet()) {
                                if (!rId.equalsIgnoreCase(regionId) && rId.toLowerCase().startsWith(lastWord)) {
                                    suggestions.add(rId);
                                }
                            }
                        }
                    }
                }
            } else if (wordCount == 4) {
                String subcmd = words[0].toLowerCase();
                String flagName = words[2].toLowerCase();

                if (subcmd.equals("flag")) {
                    com.sk89q.worldguard.protection.flags.Flag<?> flag = WorldGuard.getInstance().getFlagRegistry().get(flagName);
                    addFlagValueSuggestions(suggestions, flag, lastWord);
                }
            }
        } else if (isPlayerTargetingCommand(commandName)) {
            // god / ungod / heal / slay / locate: [player]
            if (wordCount == 1 && mcPlayer != null && mcPlayer.getServer() != null) {
                for (String playerName : mcPlayer.getServer().getPlayerNames()) {
                    if (playerName.toLowerCase().startsWith(lastWord)) {
                        suggestions.add(playerName);
                    }
                }
            }
        } else if (commandName.equalsIgnoreCase("stopfire") || commandName.equalsIgnoreCase("allowfire")) {
            // [world]
            if (wordCount == 1) {
                for (com.sk89q.worldedit.world.World world : com.sk89q.worldedit.WorldEdit.getInstance()
                        .getPlatformManager().queryCapability(com.sk89q.worldedit.extension.platform.Capability.GAME_HOOKS).getWorlds()) {
                    if (world.getName().toLowerCase().startsWith(lastWord)) {
                        suggestions.add(world.getName());
                    }
                }
            }
        } else if (commandName.equalsIgnoreCase("halt-activity")) {
            if (wordCount == 1 && "confirm".startsWith(lastWord)) {
                suggestions.add("confirm");
            }
        }

        return suggestions;
    }

    /**
     * Sugerencias del valor de "/region flag <region> <flag> <TAB>" según el tipo concreto
     * del flag. StateFlag es, con diferencia, el tipo más común (allow/deny/none), pero
     * worldguard-core también define flags con un conjunto de valores igual de discreto y
     * descubrible (booleanos, modo de juego, clima, "#here" para ubicaciones) que deben
     * autocompletarse igual — no solo los StateFlag.
     */
    private static void addFlagValueSuggestions(List<String> suggestions, com.sk89q.worldguard.protection.flags.Flag<?> flag, String lastWord) {
        if (flag == null) return;

        if (flag instanceof com.sk89q.worldguard.protection.flags.StateFlag) {
            addIfStartsWith(suggestions, lastWord, "allow", "deny", "none");
        } else if (flag instanceof com.sk89q.worldguard.protection.flags.BooleanFlag) {
            addIfStartsWith(suggestions, lastWord, "true", "false");
        } else if (flag == com.sk89q.worldguard.protection.flags.Flags.GAME_MODE) {
            addIfStartsWith(suggestions, lastWord, "survival", "creative", "adventure", "spectator");
        } else if (flag == com.sk89q.worldguard.protection.flags.Flags.WEATHER_LOCK) {
            addIfStartsWith(suggestions, lastWord, "clear", "rain", "thunder");
        } else if (flag == com.sk89q.worldguard.protection.flags.Flags.TELE_LOC
                || flag == com.sk89q.worldguard.protection.flags.Flags.SPAWN_LOC) {
            addIfStartsWith(suggestions, lastWord, "#here");
        } else if (flag == com.sk89q.worldguard.protection.flags.Flags.TIME_LOCK) {
            addIfStartsWith(suggestions, lastWord, "day", "night", "noon", "midnight");
        } else if (flag == com.sk89q.worldguard.protection.flags.Flags.DENY_SPAWN) {
            for (net.minecraft.resources.ResourceLocation key : net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.keySet()) {
                String id = key.toString();
                if (id.startsWith(lastWord)) {
                    suggestions.add(id);
                }
            }
        }
        // Flags de texto libre (mensajes, TIME_LOCK con ticks numéricos, comandos permitidos/
        // bloqueados, IntegerFlag/DoubleFlag numéricos) no tienen un conjunto de valores fijo
        // que sugerir; forzar una sugerencia ahí sería inventar un valor, así que se dejan sin
        // completar a propósito.
    }

    private static void addIfStartsWith(List<String> suggestions, String lastWord, String... options) {
        for (String option : options) {
            if (option.startsWith(lastWord)) {
                suggestions.add(option);
            }
        }
    }

    private static boolean isPlayerTargetingCommand(String commandName) {
        return commandName.equalsIgnoreCase("god")
            || commandName.equalsIgnoreCase("ungod")
            || commandName.equalsIgnoreCase("heal")
            || commandName.equalsIgnoreCase("slay")
            || commandName.equalsIgnoreCase("locate");
    }
}
