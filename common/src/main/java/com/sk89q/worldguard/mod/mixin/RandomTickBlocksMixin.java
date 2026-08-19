package com.sk89q.worldguard.mod.mixin;

import com.sk89q.worldguard.mod.CommonWorldGuardEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.BuddingAmethystBlock;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.TorchflowerCropBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.WeatheringCopperBulbBlock;
import net.minecraft.world.level.block.WeatheringCopperDoorBlock;
import net.minecraft.world.level.block.WeatheringCopperFullBlock;
import net.minecraft.world.level.block.WeatheringCopperGrateBlock;
import net.minecraft.world.level.block.WeatheringCopperSlabBlock;
import net.minecraft.world.level.block.WeatheringCopperStairBlock;
import net.minecraft.world.level.block.WeatheringCopperTrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * BlockBehaviour#randomTick(...) es un método prácticamente vacío en la clase base: cada bloque
 * con comportamiento de "random tick" (cultivos, hojas, hielo, nieve, farmland, hierba, micelio,
 * enredaderas, amatista, goteo de piedra, hongos, cobre oxidándose...) lo SOBREESCRIBE por
 * completo, y ninguno llama a super.randomTick(...) (verificado desensamblando el bytecode de
 * cada clase en el jar de Minecraft 1.21.1 con mappings oficiales). Inyectar en
 * BlockBehaviour.randomTick nunca dispara para estos bloques; hay que apuntar a cada subclase
 * concreta. Sponge Mixin permite que una sola clase de Mixin aplique el mismo @Inject a varias
 * clases objetivo mientras el método tenga la misma firma en todas, así que un solo archivo
 * cubre todos los bloques listados.
 */
@Mixin({
    // Cultivos y crecimiento vegetal -> Flags.CROP_GROWTH
    CropBlock.class, StemBlock.class, SaplingBlock.class, NetherWartBlock.class,
    SweetBerryBushBlock.class, CocoaBlock.class, TorchflowerCropBlock.class,
    // Decaimiento / derretimiento / propagación ya cubiertos antes de la auditoría.
    // Grass/Mycelium no declaran randomTick propio: lo heredan de SpreadingSnowyDirtBlock,
    // así que hay que apuntar ahí (verificado por bytecode) y diferenciar por tipo dentro
    // de CommonWorldGuardEvents#canRandomTick.
    LeavesBlock.class, IceBlock.class, SnowLayerBlock.class, FarmBlock.class,
    SpreadingSnowyDirtBlock.class,
    // Añadidos por la auditoría (hallazgo H11): sin ningún gancho anterior
    VineBlock.class, BuddingAmethystBlock.class, PointedDripstoneBlock.class, MushroomBlock.class,
    WeatheringCopperFullBlock.class, WeatheringCopperBulbBlock.class, WeatheringCopperDoorBlock.class,
    WeatheringCopperGrateBlock.class, WeatheringCopperSlabBlock.class, WeatheringCopperStairBlock.class,
    WeatheringCopperTrapDoorBlock.class
})
public abstract class RandomTickBlocksMixin {

    @Inject(method = "randomTick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V", at = @At("HEAD"), cancellable = true)
    private void worldguard$onRandomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (!CommonWorldGuardEvents.canRandomTick(state, level, pos)) {
            ci.cancel();
        }
    }
}
