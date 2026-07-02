/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2960
 *  net.minecraft.class_5321
 *  net.minecraft.class_6862
 *  net.minecraft.class_6880
 *  net.minecraft.class_6885$class_6887
 *  net.minecraft.class_7924
 */
package com.sk89q.worldedit.fabric;

import com.sk89q.worldedit.fabric.FabricAdapter;
import com.sk89q.worldedit.fabric.FabricWorldEdit;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.registry.BlockCategoryRegistry;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.class_2960;
import net.minecraft.class_5321;
import net.minecraft.class_6862;
import net.minecraft.class_6880;
import net.minecraft.class_6885;
import net.minecraft.class_7924;

public class FabricBlockCategoryRegistry
implements BlockCategoryRegistry {
    @Override
    public Set<BlockType> getCategorisedByName(String category) {
        return FabricWorldEdit.getRegistry(class_7924.field_41254).method_40266(class_6862.method_40092((class_5321)class_7924.field_41254, (class_2960)class_2960.method_60654((String)category))).stream().flatMap(class_6885.class_6887::method_40239).map(class_6880::comp_349).map(FabricAdapter::adapt).collect(Collectors.toSet());
    }
}

