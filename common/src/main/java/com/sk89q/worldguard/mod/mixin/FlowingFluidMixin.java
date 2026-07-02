package com.sk89q.worldguard.mod.mixin;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.sk89q.worldguard.mod.WorldGuardModCommon;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FlowingFluid.class)
public abstract class FlowingFluidMixin {

    @Inject(method = "spreadTo", at = @At("HEAD"), cancellable = true)
    private void onSpreadTo(LevelAccessor level, BlockPos pos, BlockState blockState, Direction direction, FluidState fluidState, CallbackInfo ci) {
        // SpreadTo ocurre en el servidor, por lo que level suele ser una instancia de Level (generalmente ServerLevel)
        if (!(level instanceof Level)) return;
        if (WorldGuardModCommon.getAdapter() == null) return;

        Level mcLevel = (Level) level;
        com.sk89q.worldedit.world.World world = WorldGuardModCommon.getAdapter().adaptWorld(mcLevel);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        com.sk89q.worldedit.util.Location weLoc = new com.sk89q.worldedit.util.Location(
            world, 
            com.sk89q.worldedit.math.BlockVector3.at(pos.getX(), pos.getY(), pos.getZ()).toVector3()
        );

        boolean isWater = fluidState.is(FluidTags.WATER);
        boolean isLava = fluidState.is(FluidTags.LAVA);

        if (isWater) {
            boolean allowed = query.queryState(weLoc, null, Flags.WATER_FLOW) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
            if (!allowed) {
                ci.cancel();
            }
        } else if (isLava) {
            boolean allowed = query.queryState(weLoc, null, Flags.LAVA_FLOW) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
            if (!allowed) {
                ci.cancel();
            }
        }
    }
}
