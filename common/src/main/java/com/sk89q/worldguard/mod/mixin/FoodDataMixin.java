package com.sk89q.worldguard.mod.mixin;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.mod.WorldGuardModCommon;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.sk89q.worldguard.session.Session;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Aplica Flags.HUNGER_DRAIN y Flags.HEALTH_REGEN (ninguno de los dos tenía gancho en el port,
 * ver auditoría H2), y hace que el modo dios (session.isInvincible) también impida perder
 * hambre, igual que InvincibilityListener#onFoodLevelChange en el original (H6).
 *
 * FoodData#tick(Player) decide en la misma llamada tanto el drenaje de hambre por exhaustion
 * como la regeneración natural de vida, así que se toma una foto de food/saturación/exhaustion/
 * vida antes de invocar el método original y se revierte selectivamente después, en vez de
 * cancelar el método entero (que rompería la otra mitad de la lógica).
 */
@Mixin(FoodData.class)
public abstract class FoodDataMixin {

    @Shadow private int foodLevel;
    @Shadow private float saturationLevel;
    @Shadow private float exhaustionLevel;

    @Unique private int worldguard$foodBefore;
    @Unique private float worldguard$saturationBefore;
    @Unique private float worldguard$exhaustionBefore;
    @Unique private float worldguard$healthBefore;

    @Inject(method = "tick", at = @At("HEAD"))
    private void worldguard$beforeTick(Player player, CallbackInfo ci) {
        this.worldguard$foodBefore = this.foodLevel;
        this.worldguard$saturationBefore = this.saturationLevel;
        this.worldguard$exhaustionBefore = this.exhaustionLevel;
        this.worldguard$healthBefore = player.getHealth();
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void worldguard$afterTick(Player player, CallbackInfo ci) {
        if (WorldGuardModCommon.getAdapter() == null) return;
        if (player.level().isClientSide()) return;
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        boolean foodDropped = this.foodLevel < this.worldguard$foodBefore;
        boolean healthRose = player.getHealth() > this.worldguard$healthBefore;
        if (!foodDropped && !healthRose) return;

        LocalPlayer localPlayer = WorldGuardModCommon.getAdapter().adaptPlayer(serverPlayer);
        Session session = WorldGuard.getInstance().getPlatform().getSessionManager().get(localPlayer);

        if (foodDropped) {
            boolean invincible = session != null && session.isInvincible(localPlayer);
            boolean drainDenied = !invincible && worldguard$queryState(localPlayer, Flags.HUNGER_DRAIN) == StateFlag.State.DENY;
            if (invincible || drainDenied) {
                this.foodLevel = this.worldguard$foodBefore;
                this.saturationLevel = this.worldguard$saturationBefore;
                this.exhaustionLevel = this.worldguard$exhaustionBefore;
            }
        }

        if (healthRose && worldguard$queryState(localPlayer, Flags.HEALTH_REGEN) == StateFlag.State.DENY) {
            player.setHealth(this.worldguard$healthBefore);
        }
    }

    @Unique
    private static StateFlag.State worldguard$queryState(LocalPlayer localPlayer, StateFlag flag) {
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        return query.queryState(localPlayer.getLocation(), localPlayer, flag);
    }
}
