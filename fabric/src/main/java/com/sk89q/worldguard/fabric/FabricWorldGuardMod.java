package com.sk89q.worldguard.fabric;

import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.fabric.FabricAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.mod.ModPlayer;
import com.sk89q.worldguard.mod.WorldGuardModCommon;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.commands.CommandSourceStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

            @Override
            public net.minecraft.world.level.Level adaptLevel(com.sk89q.worldedit.world.World world) {
                return FabricAdapter.adapt(world);
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

        // Registrar comandos en Brigadier (el árbol de comandos es común a Fabric/NeoForge)
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            WorldGuardModCommon.registerAllCommands(dispatcher));

        LOGGER.info("WorldGuard for Fabric initialized.");
    }
}
