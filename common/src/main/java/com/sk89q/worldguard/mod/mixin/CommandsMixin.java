package com.sk89q.worldguard.mod.mixin;

import com.mojang.brigadier.ParseResults;
import com.sk89q.worldguard.mod.CommonWorldGuardEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Commands.class)
public abstract class CommandsMixin {

    /**
     * performCommand(ParseResults, String) es el método que realmente ejecuta CUALQUIER
     * comando ya parseado: tanto los comandos que un jugador escribe (vía
     * handleChatCommand/handleSignedChatCommand -> parseCommand -> performCommand) como los
     * de bloques de comando/funciones (vía performPrefixedCommand, que internamente también
     * llama a este mismo método — confirmado desensamblando el bytecode). El Mixin original
     * apuntaba solo a performPrefixedCommand, que los jugadores reales nunca invocan
     * directamente, así que ALLOWED_CMDS/BLOCKED_CMDS nunca se aplicaba a un jugador
     * escribiendo un comando (auditoría, hallazgo M2).
     */
    @Inject(method = "performCommand", at = @At("HEAD"), cancellable = true)
    private void worldguard$onPerformCommand(ParseResults<CommandSourceStack> parseResults, String command, CallbackInfo ci) {
        CommandSourceStack sourceStack = parseResults.getContext().getSource();
        ServerPlayer player = sourceStack.getPlayer();
        if (player != null) {
            if (!CommonWorldGuardEvents.canExecuteCommand(player, command)) {
                ci.cancel();
            }
        }
    }
}
