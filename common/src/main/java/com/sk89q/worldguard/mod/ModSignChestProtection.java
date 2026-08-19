package com.sk89q.worldguard.mod;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.chest.SignChestProtection;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;

/**
 * Puerto del BukkitSignChestProtection original: toda la geometría de adyacencia/doble cofre
 * ya vive en {@link SignChestProtection} (worldguard-core, sin modificar), así que aquí solo
 * hace falta leer el texto real del cartel en la posición dada. "[Lock]" en la línea 0
 * protege; el jugador pasa si su nombre aparece en cualquiera de las líneas 1-3 (auditoría,
 * interruptor nicho pendiente: protección de cofres por cartel).
 */
public class ModSignChestProtection extends SignChestProtection {

    @Override
    public Boolean isProtectedSign(Location block, LocalPlayer player) {
        if (WorldGuardModCommon.getAdapter() == null) return null;
        Level level = WorldGuardModCommon.getAdapter().adaptLevel((com.sk89q.worldedit.world.World) block.getExtent());
        if (level == null) return null;

        com.sk89q.worldedit.math.BlockVector3 bv = block.toVector().toBlockPoint();
        BlockPos pos = new BlockPos(bv.getX(), bv.getY(), bv.getZ());
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof SignBlockEntity sign)) return null;

        SignText text = sign.getFrontText();
        if (!"[Lock]".equalsIgnoreCase(text.getMessage(0, false).getString())) return null;
        if (player == null) return true;

        String name = player.getName();
        for (int line = 1; line <= 3; line++) {
            if (name.equalsIgnoreCase(text.getMessage(line, false).getString().trim())) {
                return false;
            }
        }
        return true;
    }
}
