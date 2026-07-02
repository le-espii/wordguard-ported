/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.loader.api.FabricLoader
 *  org.apache.logging.log4j.Logger
 *  org.objectweb.asm.tree.ClassNode
 *  org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
 *  org.spongepowered.asm.mixin.extensibility.IMixinInfo
 */
package com.sk89q.worldedit.fabric.internal;

import com.sk89q.worldedit.internal.util.LogManagerCompat;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class MixinConfigPlugin
implements IMixinConfigPlugin {
    private static final Logger LOGGER = LogManagerCompat.getLogger();

    public void onLoad(String mixinPackage) {
    }

    public String getRefMapperConfig() {
        return null;
    }

    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.equals("com.sk89q.worldedit.fabric.mixin.MixinLevelChunkSetBlockHook")) {
            List conflictingContainers = Stream.of("carpet", "quickcarpet").map(arg_0 -> ((FabricLoader)FabricLoader.getInstance()).getModContainer(arg_0)).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
            if (!conflictingContainers.isEmpty()) {
                List conflictingIds = conflictingContainers.stream().map(mc -> mc.getMetadata().getId()).collect(Collectors.toList());
                LOGGER.warn("{} detected, disabling UPDATE mixin {}", conflictingIds, (Object)mixinClassName);
            }
            return conflictingContainers.isEmpty();
        }
        return true;
    }

    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    public List<String> getMixins() {
        return null;
    }

    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}

