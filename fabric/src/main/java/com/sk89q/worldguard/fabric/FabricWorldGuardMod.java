package com.sk89q.worldguard.fabric;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.fabric.FabricAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.mod.ModPlayer;
import com.sk89q.worldguard.mod.WorldGuardModCommon;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class FabricWorldGuardMod implements ModInitializer {
    private static final Logger LOGGER = LogManager.getLogger("WorldGuard-Fabric");

    @Override
    public void onInitialize() {
        LOGGER.info("Starting WorldGuard for Fabric...");

        // Configurar el adaptador de plataforma usando WorldEdit Fabric
        WorldGuardModCommon.setAdapter(new WorldGuardModCommon.PlatformAdapter() {
            @Override
            public com.sk89q.worldedit.world.World adaptWorld(net.minecraft.world.level.Level level) {
                return FabricAdapter.adapt(level);
            }

            @Override
            public LocalPlayer adaptPlayer(net.minecraft.server.level.ServerPlayer player) {
                return new ModPlayer(player, FabricAdapter.adaptPlayer(player));
            }

            @Override
            public Actor adaptCommandSource(CommandSourceStack source) {
                return FabricAdapter.adaptCommandSource(source);
            }
        });

        // Inicializar el core de WorldGuard
        WorldGuardModCommon.init();

        // Enlazar eventos de ciclo de vida del servidor
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            WorldGuardModCommon.setServer(server);
            WorldGuard.getInstance().setup();
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            WorldGuard.getInstance().disable();
            WorldGuardModCommon.setServer(null);
        });

        // Registrar comandos en Brigadier
        CommandRegistrationCallback.EVENT.register(this::registerCommands);

        LOGGER.info("WorldGuard for Fabric initialized.");
    }

    private static com.mojang.brigadier.suggestion.SuggestionProvider<CommandSourceStack> getSuggestionsProvider(String commandName) {
        return (context, builder) -> {
            String argsString = "";
            try {
                argsString = StringArgumentType.getString(context, "args");
            } catch (IllegalArgumentException ignored) {}
            
            java.util.List<String> suggestions = WorldGuardModCommon.getSuggestions(context.getSource(), commandName, argsString);
            
            String input = context.getInput();
            int lastSpace = input.lastIndexOf(' ');
            int start = lastSpace == -1 ? 0 : lastSpace + 1;
            int end = input.length();
            
            java.util.List<com.mojang.brigadier.suggestion.Suggestion> list = new java.util.ArrayList<>();
            for (String suggestion : suggestions) {
                list.add(new com.mojang.brigadier.suggestion.Suggestion(
                    com.mojang.brigadier.context.StringRange.between(start, end),
                    suggestion
                ));
            }
            return java.util.concurrent.CompletableFuture.completedFuture(
                com.mojang.brigadier.suggestion.Suggestions.create(input, list)
            );
        };
    }

    private void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection selection) {
        // Registrar /region y su alias /rg
        dispatcher.register(literal("region")
            .then(argument("args", StringArgumentType.greedyString())
                .suggests(getSuggestionsProvider("region"))
                .executes(context -> executeCommand(context, "region", StringArgumentType.getString(context, "args")))
            )
            .executes(context -> executeCommand(context, "region", ""))
        );
        dispatcher.register(literal("rg")
            .then(argument("args", StringArgumentType.greedyString())
                .suggests(getSuggestionsProvider("region"))
                .executes(context -> executeCommand(context, "region", StringArgumentType.getString(context, "args")))
            )
            .executes(context -> executeCommand(context, "region", ""))
        );

        // Registrar /worldguard y su alias /wg
        dispatcher.register(literal("worldguard")
            .then(argument("args", StringArgumentType.greedyString())
                .suggests(getSuggestionsProvider("worldguard"))
                .executes(context -> executeCommand(context, "worldguard", StringArgumentType.getString(context, "args")))
            )
            .executes(context -> executeCommand(context, "worldguard", ""))
        );
        dispatcher.register(literal("wg")
            .then(argument("args", StringArgumentType.greedyString())
                .suggests(getSuggestionsProvider("worldguard"))
                .executes(context -> executeCommand(context, "worldguard", StringArgumentType.getString(context, "args")))
            )
            .executes(context -> executeCommand(context, "worldguard", ""))
        );
    }

    private int executeCommand(CommandContext<CommandSourceStack> context, String commandName, String argsString) {
        CommandSourceStack source = context.getSource();
        
        // Adaptar el CommandSourceStack usando el adaptador
        Actor actor;
        if (source.getPlayer() != null) {
            actor = WorldGuardModCommon.getAdapter().adaptPlayer(source.getPlayer());
        } else {
            actor = WorldGuardModCommon.getAdapter().adaptCommandSource(source);
        }

        // Mapear los argumentos respetando espacios y comillas
        String[] args = parseArguments(argsString);

        // Si el comando es /wg y no se pasan argumentos, o si se pasa "credits" o "version",
        // podemos inyectar un mensaje de Patreon personalizado para Espii
        if (commandName.equalsIgnoreCase("worldguard") && (args.length == 0 || args[0].equalsIgnoreCase("version") || args[0].equalsIgnoreCase("credits"))) {
            source.sendSuccess(() -> Component.literal("§e========================================\n" +
                                                      "§bWorldGuard Mod (NeoForge/Fabric 1.21.1)\n" +
                                                      "§7Creado por EngineHub. Portado y adaptado por: §aEspii\n" +
                                                      "§7Apoya a Espii en Patreon: §dhttps://www.patreon.com/cw/Le_Espii\n" +
                                                      "§e========================================"), false);
        }

        try {
            // Ejecutar el comando en el administrador clásico de WorldGuard
            WorldGuardModCommon.getCommands().execute(commandName, args, actor, actor);
            return 1;
        } catch (com.sk89q.minecraft.util.commands.CommandPermissionsException e) {
            source.sendFailure(Component.literal("§cNo tienes permiso para ejecutar este comando."));
            return 0;
        } catch (com.sk89q.minecraft.util.commands.CommandUsageException e) {
            source.sendFailure(Component.literal("§c" + e.getMessage()));
            if (e.getUsage() != null) {
                source.sendFailure(Component.literal("§c" + e.getUsage()));
            }
            return 0;
        } catch (Throwable t) {
            LOGGER.error("Error executing command: " + commandName + " " + argsString, t);
            source.sendFailure(Component.literal("§cError ejecutando el comando: " + t.getMessage()));
            return 0;
        }
    }

    /**
     * Utilidad para dividir una cadena de argumentos respetando comillas dobles.
     */
    private String[] parseArguments(String argsString) {
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
}
