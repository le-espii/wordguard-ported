/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.class_1936
 *  net.minecraft.class_1937
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_2487
 *  net.minecraft.class_2586
 *  net.minecraft.class_2680
 *  net.minecraft.class_2818
 *  net.minecraft.class_3194
 *  net.minecraft.class_3215
 *  net.minecraft.class_7225$class_7874
 */
package com.sk89q.worldedit.fabric.internal;

import com.sk89q.worldedit.fabric.FabricAdapter;
import com.sk89q.worldedit.fabric.internal.ExtendedChunk;
import com.sk89q.worldedit.fabric.internal.NBTConverter;
import com.sk89q.worldedit.internal.block.BlockStateIdAccess;
import com.sk89q.worldedit.internal.wna.WorldNativeAccess;
import com.sk89q.worldedit.util.SideEffect;
import com.sk89q.worldedit.util.SideEffectSet;
import com.sk89q.worldedit.world.block.BlockState;
import java.lang.ref.WeakReference;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.class_1936;
import net.minecraft.class_1937;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2487;
import net.minecraft.class_2586;
import net.minecraft.class_2680;
import net.minecraft.class_2818;
import net.minecraft.class_3194;
import net.minecraft.class_3215;
import net.minecraft.class_7225;
import org.enginehub.linbus.tree.LinCompoundTag;

public class FabricWorldNativeAccess
implements WorldNativeAccess<class_2818, class_2680, class_2338> {
    private static final int UPDATE = 1;
    private static final int NOTIFY = 2;
    private final WeakReference<class_1937> world;
    private SideEffectSet sideEffectSet;

    public FabricWorldNativeAccess(WeakReference<class_1937> world) {
        this.world = world;
    }

    private class_1937 getWorld() {
        return Objects.requireNonNull((class_1937)this.world.get(), "The reference to the world was lost");
    }

    @Override
    public void setCurrentSideEffectSet(SideEffectSet sideEffectSet) {
        this.sideEffectSet = sideEffectSet;
    }

    @Override
    public class_2818 getChunk(int x, int z) {
        return this.getWorld().method_8497(x, z);
    }

    @Override
    public class_2680 toNative(BlockState state) {
        int stateId = BlockStateIdAccess.getBlockStateId(state);
        return BlockStateIdAccess.isValidInternalId(stateId) ? class_2248.method_9531((int)stateId) : FabricAdapter.adapt(state);
    }

    @Override
    public class_2680 getBlockState(class_2818 chunk, class_2338 position) {
        return chunk.method_8320(position);
    }

    @Override
    @Nullable
    public class_2680 setBlockState(class_2818 chunk, class_2338 position, class_2680 state) {
        if (chunk instanceof ExtendedChunk) {
            return ((ExtendedChunk)chunk).setBlockState(position, state, false, this.sideEffectSet.shouldApply(SideEffect.UPDATE));
        }
        return chunk.method_12010(position, state, false);
    }

    @Override
    public class_2680 getValidBlockForPosition(class_2680 block, class_2338 position) {
        return class_2248.method_9510((class_2680)block, (class_1936)this.getWorld(), (class_2338)position);
    }

    @Override
    public class_2338 getPosition(int x, int y, int z) {
        return new class_2338(x, y, z);
    }

    @Override
    public void updateLightingForBlock(class_2338 position) {
        this.getWorld().method_8398().method_12130().method_15513(position);
    }

    @Override
    public boolean updateTileEntity(class_2338 position, LinCompoundTag tag) {
        class_2487 nativeTag = NBTConverter.toNative(tag);
        class_1937 level = this.getWorld();
        class_2586 tileEntity = level.method_8500(position).method_8321(position);
        if (tileEntity == null) {
            return false;
        }
        tileEntity.method_58690(nativeTag, (class_7225.class_7874)level.method_30349());
        tileEntity.method_5431();
        return true;
    }

    @Override
    public void notifyBlockUpdate(class_2818 chunk, class_2338 position, class_2680 oldState, class_2680 newState) {
        if (chunk.method_12006()[this.getWorld().method_31602(position.method_10264())] != null) {
            this.getWorld().method_8413(position, oldState, newState, 3);
        }
    }

    @Override
    public boolean isChunkTicking(class_2818 chunk) {
        return chunk.method_12225().method_14014(class_3194.field_44856);
    }

    @Override
    public void markBlockChanged(class_2818 chunk, class_2338 position) {
        if (chunk.method_12006()[this.getWorld().method_31602(position.method_10264())] != null) {
            ((class_3215)this.getWorld().method_8398()).method_14128(position);
        }
    }

    @Override
    public void notifyNeighbors(class_2338 pos, class_2680 oldState, class_2680 newState) {
        this.getWorld().method_8408(pos, oldState.method_26204());
        if (newState.method_26221()) {
            this.getWorld().method_8455(pos, newState.method_26204());
        }
    }

    @Override
    public void updateBlock(class_2338 pos, class_2680 oldState, class_2680 newState) {
        class_1937 world = this.getWorld();
        newState.method_26182(world, pos, oldState, false);
    }

    @Override
    public void updateNeighbors(class_2338 pos, class_2680 oldState, class_2680 newState, int recursionLimit) {
        class_1937 world = this.getWorld();
        oldState.method_26198((class_1936)world, pos, 2, recursionLimit);
        newState.method_26183((class_1936)world, pos, 2, recursionLimit);
        newState.method_26198((class_1936)world, pos, 2, recursionLimit);
    }

    @Override
    public void onBlockStateChange(class_2338 pos, class_2680 oldState, class_2680 newState) {
        this.getWorld().method_19282(pos, oldState, newState);
    }
}

