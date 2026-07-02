package com.sk89q.worldguard.mod.mixin;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.sk89q.worldguard.mod.WorldGuardModCommon;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {

    @Shadow @Final private Level level;
    @Shadow @Final private it.unimi.dsi.fastutil.objects.ObjectArrayList<BlockPos> toBlow;

    @Shadow @Nullable public abstract net.minecraft.world.entity.LivingEntity getIndirectSourceEntity();

    @Inject(method = "finalizeExplosion", at = @At("HEAD"))
    private void onFinalizeExplosion(boolean spawnParticles, CallbackInfo ci) {
        try {
            com.sk89q.worldedit.world.World world = WorldGuardModCommon.getAdapter().adaptWorld(this.level);
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();

            net.minecraft.world.entity.LivingEntity indirectSource = getIndirectSourceEntity();
            com.sk89q.worldguard.LocalPlayer localPlayer = null;
            if (indirectSource instanceof net.minecraft.server.level.ServerPlayer) {
                localPlayer = WorldGuardModCommon.getAdapter().adaptPlayer((net.minecraft.server.level.ServerPlayer) indirectSource);
            }

            final com.sk89q.worldguard.LocalPlayer finalPlayer = localPlayer;
            StateFlag explosionFlag = worldguard$getExplosionFlag(indirectSource);

            // Filtrar toBlow para remover los bloques protegidos por WorldGuard
            this.toBlow.removeIf(pos -> {
                com.sk89q.worldedit.util.Location weLoc = new com.sk89q.worldedit.util.Location(
                    world, 
                    com.sk89q.worldedit.math.BlockVector3.at(pos.getX(), pos.getY(), pos.getZ()).toVector3()
                );
                
                boolean canExplode;
                if (query.getApplicableRegions(weLoc).size() == 0) {
                    // Si no hay regiones, la explosión está permitida por defecto
                    canExplode = true;
                } else if (finalPlayer != null) {
                    // Si el causante indirecto es un jugador, se chequea Flags.BUILD y el flag de explosión
                    canExplode = query.testState(weLoc, finalPlayer, Flags.BUILD, explosionFlag);
                } else {
                    // Si no hay jugador causante, sólo el flag de explosión (evitamos Flags.BUILD y testState para evitar NullPointerException)
                    canExplode = query.queryState(weLoc, null, explosionFlag) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
                }
                
                return !canExplode;
            });
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Unique
    private StateFlag worldguard$getExplosionFlag(net.minecraft.world.entity.Entity exploder) {
        if (exploder == null) {
            return Flags.OTHER_EXPLOSION;
        }
        String className = exploder.getClass().getName();
        if (className.contains("Creeper")) {
            return Flags.CREEPER_EXPLOSION;
        } else if (className.contains("Wither")) {
            return Flags.WITHER_DAMAGE;
        } else if (className.contains("Fireball")) {
            return Flags.GHAST_FIREBALL;
        } else if (className.contains("WindCharge")) {
            return Flags.WIND_CHARGE_BURST;
        } else if (className.contains("Tnt") || className.contains("TNT")) {
            return Flags.TNT;
        }
        return Flags.OTHER_EXPLOSION;
    }
}


