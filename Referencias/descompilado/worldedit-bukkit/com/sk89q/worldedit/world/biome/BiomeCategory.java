/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.world.biome;

import com.sk89q.worldedit.registry.Category;
import com.sk89q.worldedit.registry.Keyed;
import com.sk89q.worldedit.registry.NamespacedRegistry;
import com.sk89q.worldedit.world.biome.BiomeType;
import java.lang.invoke.LambdaMetafactory;
import java.util.Set;
import java.util.function.Supplier;

public class BiomeCategory
extends Category<BiomeType>
implements Keyed {
    public static final NamespacedRegistry<BiomeCategory> REGISTRY = new NamespacedRegistry("biome tag", true);

    public BiomeCategory(String id) {
        super(id, (Supplier)LambdaMetafactory.metafactory(null, null, null, ()Ljava/lang/Object;, of(), ()Ljava/util/Set;)());
    }

    public BiomeCategory(String id, Supplier<Set<BiomeType>> contentSupplier) {
        super(id, contentSupplier);
    }
}

