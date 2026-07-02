/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extension.platform;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.platform.Platform;
import com.sk89q.worldedit.extension.platform.PlatformManager;
import com.sk89q.worldedit.internal.block.BlockStateIdAccess;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.registry.BlockRegistry;

public enum Capability {
    GAME_HOOKS{

        @Override
        void initialize(PlatformManager platformManager, Platform platform) {
            platform.setGameHooksEnabled(true);
        }

        @Override
        void uninitialize(PlatformManager platformManager, Platform platform) {
            platform.setGameHooksEnabled(false);
        }
    }
    ,
    CONFIGURATION{

        @Override
        void initialize(PlatformManager platformManager, Platform platform) {
            WorldEdit.getInstance().getAssetLoaders().init();
        }

        @Override
        void uninitialize(PlatformManager platformManager, Platform platform) {
            WorldEdit.getInstance().getAssetLoaders().uninit();
        }
    }
    ,
    USER_COMMANDS{

        @Override
        void initialize(PlatformManager platformManager, Platform platform) {
            platformManager.getPlatformCommandManager().registerCommandsWith(platform);
        }

        @Override
        void uninitialize(PlatformManager platformManager, Platform platform) {
            platformManager.getPlatformCommandManager().removeCommands();
        }
    }
    ,
    PERMISSIONS,
    WORLDEDIT_CUI,
    WORLD_EDITING{

        @Override
        void ready(PlatformManager platformManager, Platform platform) {
            BlockRegistry blockRegistry = platform.getRegistries().getBlockRegistry();
            for (BlockType type : BlockType.REGISTRY) {
                for (BlockState state : type.getAllStates()) {
                    BlockStateIdAccess.register(state, blockRegistry.getInternalBlockStateId(state).orElse(BlockStateIdAccess.invalidId()));
                }
            }
        }

        @Override
        void unready(PlatformManager platformManager, Platform platform) {
            BlockStateIdAccess.clear();
        }
    };


    void initialize(PlatformManager platformManager, Platform platform) {
    }

    void uninitialize(PlatformManager platformManager, Platform platform) {
    }

    void ready(PlatformManager platformManager, Platform platform) {
    }

    void unready(PlatformManager platformManager, Platform platform) {
    }
}

