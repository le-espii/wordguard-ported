/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_156
 *  net.minecraft.class_1937
 *  net.minecraft.class_32$class_5143
 *  net.minecraft.server.MinecraftServer
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 */
package com.sk89q.worldedit.fabric.mixin;

import com.sk89q.worldedit.extension.platform.Watchdog;
import com.sk89q.worldedit.fabric.internal.ExtendedMinecraftServer;
import java.nio.file.Path;
import net.minecraft.class_156;
import net.minecraft.class_1937;
import net.minecraft.class_32;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value={MinecraftServer.class})
public abstract class MixinMinecraftServer
implements Watchdog,
ExtendedMinecraftServer {
    @Shadow
    private long field_47139;
    @Final
    @Shadow
    protected class_32.class_5143 field_23784;

    @Override
    @Unique
    public void tick() {
        this.field_47139 = class_156.method_648();
    }

    @Override
    @Unique
    public Path getStoragePath(class_1937 world) {
        return this.field_23784.method_27424(world.method_27983());
    }
}

