/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.world.fluid;

import com.sk89q.worldedit.registry.Category;
import com.sk89q.worldedit.registry.Keyed;
import com.sk89q.worldedit.registry.NamespacedRegistry;
import com.sk89q.worldedit.world.fluid.FluidType;
import java.lang.invoke.LambdaMetafactory;
import java.util.function.Supplier;

public class FluidCategory
extends Category<FluidType>
implements Keyed {
    public static final NamespacedRegistry<FluidCategory> REGISTRY = new NamespacedRegistry("fluid tag");

    public FluidCategory(String id) {
        super(id, (Supplier)LambdaMetafactory.metafactory(null, null, null, ()Ljava/lang/Object;, of(), ()Ljava/util/Set;)());
    }
}

