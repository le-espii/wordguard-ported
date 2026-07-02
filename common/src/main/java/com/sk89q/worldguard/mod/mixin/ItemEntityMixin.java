package com.sk89q.worldguard.mod.mixin;

import com.sk89q.worldguard.mod.CommonWorldGuardEvents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Inject(method = "playerTouch", at = @At("HEAD"), cancellable = true)
    private void onPlayerTouch(Player player, CallbackInfo ci) {
        ItemEntity itemEntity = (ItemEntity) (Object) this;
        if (!CommonWorldGuardEvents.canPickupItem(itemEntity, player)) {
            ci.cancel();
        }
    }
}
