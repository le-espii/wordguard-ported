package com.sk89q.worldguard.mod.mixin;

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

    @Inject(method = "performPrefixedCommand", at = @At("HEAD"), cancellable = true)
    private void onPerformPrefixedCommand(CommandSourceStack sourceStack, String command, CallbackInfo ci) {
        ServerPlayer player = sourceStack.getPlayer();
        if (player != null) {
            if (!CommonWorldGuardEvents.canExecuteCommand(player, command)) {
                ci.cancel();
            }
        }
    }
}
