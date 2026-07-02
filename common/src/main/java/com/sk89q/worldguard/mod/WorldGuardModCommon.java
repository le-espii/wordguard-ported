package com.sk89q.worldguard.mod;

import com.sk89q.minecraft.util.commands.CommandsManager;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.commands.GeneralCommands;
import com.sk89q.worldguard.commands.ProtectionCommands;
import com.sk89q.worldguard.commands.ToggleCommands;
import com.sk89q.worldguard.protection.flags.registry.SimpleFlagRegistry;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGuardModCommon {
    private static final Logger LOGGER = LogManager.getLogger("WorldGuard");
    private static ModWorldGuardPlatform platform;
    private static CommandsManager<Actor> commands;
    private static MinecraftServer server;
    private static PlatformAdapter adapter;

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

    public static java.util.List<String> getSuggestions(net.minecraft.commands.CommandSourceStack source, String commandName, String argsString) {
        java.util.List<String> suggestions = new java.util.ArrayList<>();
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
        
        if (commandName.equalsIgnoreCase("worldguard") || commandName.equalsIgnoreCase("wg")) {
            if (wordCount == 1) {
                String[] subcommands = {"version", "reload", "report", "profile", "flushstates", "credits", "stopprofile"};
                for (String sub : subcommands) {
                    if (sub.startsWith(lastWord)) {
                        suggestions.add(sub);
                    }
                }
            }
        } else {
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
                    if (flag instanceof com.sk89q.worldguard.protection.flags.StateFlag) {
                        String[] states = {"allow", "deny", "none"};
                        for (String st : states) {
                            if (st.startsWith(lastWord)) {
                                suggestions.add(st);
                            }
                        }
                    }
                }
            }
        }
        
        return suggestions;
    }
}
