/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2846
 *  net.minecraft.class_2879
 *  net.minecraft.class_3222
 *  net.minecraft.class_3244
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.sk89q.worldedit.fabric.mixin;

import com.sk89q.worldedit.fabric.FabricWorldEdit;
import net.minecraft.class_2846;
import net.minecraft.class_2879;
import net.minecraft.class_3222;
import net.minecraft.class_3244;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={class_3244.class})
public class MixinServerGamePacketListenerImpl {
    @Shadow
    public class_3222 field_14140;
    @Unique
    private int ignoreSwingPackets;

    @Inject(method={"handleAnimate"}, at={@At(value="HEAD")})
    private void onAnimate(class_2879 packet, CallbackInfo ci) {
        if (!this.field_14140.field_13974.field_14003) {
            if (this.ignoreSwingPackets > 0) {
                --this.ignoreSwingPackets;
            } else if (FabricWorldEdit.inst != null) {
                FabricWorldEdit.inst.onLeftClickAir(this.field_14140, packet.method_12512());
            }
        }
    }

    @Inject(method={"handlePlayerAction"}, at={@At(value="HEAD")})
    private void onAction(class_2846 packet, CallbackInfo ci) {
        switch (packet.method_12363()) {
            case field_12975: 
            case field_12970: 
            case field_12968: {
                ++this.ignoreSwingPackets;
                break;
            }
        }
    }
}

