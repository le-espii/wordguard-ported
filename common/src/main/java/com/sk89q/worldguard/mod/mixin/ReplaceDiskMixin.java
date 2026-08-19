package com.sk89q.worldguard.mod.mixin;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.mod.WorldGuardModCommon;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.ReplaceDisk;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Flags.FROSTED_ICE_FORM (auditoría, hallazgo H8): en 1.12 el original enganchaba
 * FrostWalkerEnchantment#onEntityMoved, una clase Java dedicada que ya no existe desde que
 * Mojang movió los encantamientos a un sistema data-driven (efectos declarados en JSON,
 * data/minecraft/enchantment/frost_walker.json aquí). El efecto real de Frost Walker
 * ("minecraft:location_changed" → "minecraft:replace_disk") lo ejecuta esta clase genérica
 * ReplaceDisk, que sigue siendo el único contenido vanilla que la usa; cancelar aquí equivale
 * a cancelar Frost Walker. A diferencia de fire-spread (jugador nulo), el original comprueba
 * BUILD + FROSTED_ICE_FORM con el jugador real (respeta membresía de región), así que aquí se
 * hace lo mismo. Simplificación: se cancela el disco de hielo entero según la posición del
 * jugador en vez de filtrar bloque a bloque como el PlaceBlockEvent original (ReplaceDisk no
 * expone un punto de inyección por bloque sin tocar su lógica interna privada).
 */
@Mixin(ReplaceDisk.class)
public abstract class ReplaceDiskMixin {

    @Inject(method = "apply", at = @At("HEAD"), cancellable = true)
    private void worldguard$onApply(ServerLevel level, int enchantLevel, EnchantedItemInUse item, Entity entity, Vec3 origin, CallbackInfo ci) {
        if (WorldGuardModCommon.getAdapter() == null) return;

        com.sk89q.worldedit.world.World world = WorldGuardModCommon.getAdapter().adaptWorld(level);
        com.sk89q.worldedit.util.Location weLoc = new com.sk89q.worldedit.util.Location(
            world,
            com.sk89q.worldedit.math.BlockVector3.at(origin.x, origin.y, origin.z).toVector3()
        );

        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        boolean allowed;
        if (entity instanceof ServerPlayer serverPlayer) {
            LocalPlayer localPlayer = WorldGuardModCommon.getAdapter().adaptPlayer(serverPlayer);
            allowed = query.testBuild(weLoc, localPlayer, Flags.BLOCK_PLACE, Flags.FROSTED_ICE_FORM);
        } else {
            allowed = query.testBuild(weLoc, (com.sk89q.worldguard.protection.association.RegionAssociable) null,
                Flags.BLOCK_PLACE, Flags.FROSTED_ICE_FORM);
        }

        if (!allowed) {
            ci.cancel();
        }
    }
}
