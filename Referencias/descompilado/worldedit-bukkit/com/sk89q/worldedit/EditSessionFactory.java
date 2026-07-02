/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.inventory.BlockBag;
import com.sk89q.worldedit.world.World;

@Deprecated
public class EditSessionFactory {
    public EditSession getEditSession(World world, int maxBlocks) {
        throw new RuntimeException("Method needs to be implemented");
    }

    public EditSession getEditSession(World world, int maxBlocks, Actor actor) {
        throw new RuntimeException("Method needs to be implemented");
    }

    public EditSession getEditSession(World world, int maxBlocks, BlockBag blockBag) {
        throw new RuntimeException("Method needs to be implemented");
    }

    public EditSession getEditSession(World world, int maxBlocks, BlockBag blockBag, Actor actor) {
        throw new RuntimeException("Method needs to be implemented");
    }

    static final class EditSessionFactoryImpl
    extends EditSessionFactory {
        EditSessionFactoryImpl() {
        }

        @Override
        public EditSession getEditSession(World world, int maxBlocks) {
            return this.getEditSession(world, maxBlocks, null, null);
        }

        @Override
        public EditSession getEditSession(World world, int maxBlocks, Actor actor) {
            return this.getEditSession(world, maxBlocks, null, actor);
        }

        @Override
        public EditSession getEditSession(World world, int maxBlocks, BlockBag blockBag) {
            return this.getEditSession(world, maxBlocks, blockBag, null);
        }

        @Override
        public EditSession getEditSession(World world, int maxBlocks, BlockBag blockBag, Actor actor) {
            return WorldEdit.getInstance().newEditSessionBuilder().world(world).maxBlocks(maxBlocks).blockBag(blockBag).actor(actor).build();
        }
    }
}

