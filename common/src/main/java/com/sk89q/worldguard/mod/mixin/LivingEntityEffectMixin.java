package com.sk89q.worldguard.mod.mixin;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.config.WorldConfiguration;
import com.sk89q.worldguard.mod.ModWorldConfiguration;
import com.sk89q.worldguard.mod.WorldGuardModCommon;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Aplica gameplay.block-potions (hallazgo H4): antes no existía ningún equivalente a
 * BlockedPotionsListener. En vez de enganchar el ítem de poción/proyectil por separado como
 * el original, se intercepta LivingEntity#addEffect(MobEffectInstance, Entity) — el único
 * método por el que pasan TANTO beber una poción como recibir una splash/lingering potion
 * (la sobrecarga de un solo argumento delega en esta, confirmado por bytecode) — así que un
 * solo punto cubre ambos casos. Diferencia deliberada de alcance frente al original: como
 * este es el único punto de entrada práctico, también bloquea el efecto si llegara de una
 * fuente distinta a una poción (p. ej. un faro), algo que BlockedPotionsListener no hacía.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityEffectMixin {

    @Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",
            at = @At("HEAD"), cancellable = true)
    private void worldguard$onAddEffect(MobEffectInstance instance, Entity source, CallbackInfoReturnable<Boolean> cir) {
        if (WorldGuardModCommon.getAdapter() == null) return;
        LivingEntity self = (LivingEntity) (Object) this;
        Level level = self.level();
        if (level.isClientSide()) return;

        com.sk89q.worldedit.world.World world = WorldGuardModCommon.getAdapter().adaptWorld(level);
        WorldConfiguration wcfg = WorldGuard.getInstance().getPlatform().getGlobalStateManager().get(world);
        if (!(wcfg instanceof ModWorldConfiguration modConfig) || modConfig.blockPotions.isEmpty()) {
            return;
        }

        MobEffect effect = instance.getEffect().value();
        if (!modConfig.blockPotions.contains(effect)) {
            return;
        }

        // worldguard.override.potions exime, salvo que blockPotionsAlways fuerce el bloqueo
        // siempre (igual que BlockedPotionsListener: splash/lingering afectan a terceros, así
        // que no se puede filtrar solo para quien tiene el permiso).
        if (source instanceof ServerPlayer player && !modConfig.blockPotionsAlways) {
            LocalPlayer localPlayer = WorldGuardModCommon.getAdapter().adaptPlayer(player);
            if (localPlayer.hasPermission("worldguard.override.potions")) {
                return;
            }
        }

        cir.setReturnValue(false);
    }
}
