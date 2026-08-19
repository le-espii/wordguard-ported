package com.sk89q.worldguard.neoforge;

import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.neoforge.NeoForgeAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.mod.ModPlayer;
import com.sk89q.worldguard.mod.WorldGuardModCommon;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("worldguard")
public class NeoForgeWorldGuardMod {
    private static final Logger LOGGER = LogManager.getLogger("WorldGuard-NeoForge");

    public NeoForgeWorldGuardMod() {
        LOGGER.info("Starting WorldGuard for NeoForge...");

        // Configurar el adaptador de plataforma usando WorldEdit NeoForge
        WorldGuardModCommon.setAdapter(new WorldGuardModCommon.PlatformAdapter() {
            @Override
            public com.sk89q.worldedit.world.World adaptWorld(net.minecraft.world.level.Level level) {
                return NeoForgeAdapter.adapt((net.minecraft.server.level.ServerLevel) level);
            }

            @Override
            public LocalPlayer adaptPlayer(net.minecraft.server.level.ServerPlayer player) {
                return new ModPlayer(player, NeoForgeAdapter.adaptPlayer(player));
            }

            @Override
            public Actor adaptCommandSource(CommandSourceStack source) {
                return NeoForgeAdapter.adaptCommandSource(source);
            }
        });

        // Inicializar el core común
        WorldGuardModCommon.init();

        // Registrar este mod en el bus de eventos de NeoForge
        NeoForge.EVENT_BUS.register(this);

        LOGGER.info("WorldGuard for NeoForge loaded.");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        WorldGuardModCommon.setServer(event.getServer());
        WorldGuard.getInstance().setup();
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        WorldGuard.getInstance().disable();
        WorldGuardModCommon.setServer(null);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        // El árbol de comandos es común a Fabric/NeoForge; vive en WorldGuardModCommon.
        WorldGuardModCommon.registerAllCommands(event.getDispatcher());
    }
}
