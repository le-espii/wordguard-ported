/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.WorldGenLevel
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 */
package com.sk89q.worldedit.neoforge.internal;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.neoforge.NeoForgeAdapter;
import com.sk89q.worldedit.neoforge.internal.NBTConverter;
import com.sk89q.worldedit.neoforge.internal.NeoForgeEntity;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.block.BlockTypes;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class NeoForgeServerLevelDelegateProxy
implements InvocationHandler {
    private final EditSession editSession;
    private final ServerLevel serverLevel;

    private NeoForgeServerLevelDelegateProxy(EditSession editSession, ServerLevel serverLevel) {
        this.editSession = editSession;
        this.serverLevel = serverLevel;
    }

    public static WorldGenLevel newInstance(EditSession editSession, ServerLevel serverLevel) {
        return (WorldGenLevel)Proxy.newProxyInstance(serverLevel.getClass().getClassLoader(), serverLevel.getClass().getInterfaces(), new NeoForgeServerLevelDelegateProxy(editSession, serverLevel));
    }

    @Nullable
    private BlockEntity getBlockEntity(BlockPos blockPos) {
        BlockEntity tileEntity = this.serverLevel.getChunkAt(blockPos).getBlockEntity(blockPos);
        if (tileEntity == null) {
            return null;
        }
        BlockEntity newEntity = tileEntity.getType().create(blockPos, this.getBlockState(blockPos));
        newEntity.loadWithComponents(NBTConverter.toNative(this.editSession.getFullBlock(NeoForgeAdapter.adapt(blockPos)).getNbtReference().getValue()), (HolderLookup.Provider)this.serverLevel.registryAccess());
        return newEntity;
    }

    private BlockState getBlockState(BlockPos blockPos) {
        return NeoForgeAdapter.adapt(this.editSession.getBlock(NeoForgeAdapter.adapt(blockPos)));
    }

    private boolean setBlock(BlockPos blockPos, BlockState blockState) {
        try {
            return this.editSession.setBlock(NeoForgeAdapter.adapt(blockPos), NeoForgeAdapter.adapt(blockState));
        }
        catch (MaxChangedBlocksException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean removeBlock(BlockPos blockPos, boolean bl) {
        try {
            return this.editSession.setBlock(NeoForgeAdapter.adapt(blockPos), BlockTypes.AIR.getDefaultState());
        }
        catch (MaxChangedBlocksException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean addEntity(Entity entity) {
        BaseEntity baseEntity;
        Vector3 pos = NeoForgeAdapter.adapt(entity.getPosition(0.0f));
        Location location = new Location((Extent)NeoForgeAdapter.adapt(this.serverLevel), pos.x(), pos.y(), pos.z());
        return this.editSession.createEntity(location, baseEntity = new NeoForgeEntity(entity).getState()) != null;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        switch (method.getName()) {
            case "getBlockState": 
            case "m_8055_": {
                Object object;
                if (args.length != 1 || !((object = args[0]) instanceof BlockPos)) break;
                BlockPos blockPos = (BlockPos)object;
                return this.getBlockState(blockPos);
            }
            case "getBlockEntity": 
            case "m_7702_": {
                Object object;
                if (args.length != 1 || !((object = args[0]) instanceof BlockPos)) break;
                BlockPos blockPos = (BlockPos)object;
                return this.getBlockEntity(blockPos);
            }
            case "setBlock": 
            case "m_7731_": {
                Object object;
                if (args.length < 2 || !((object = args[0]) instanceof BlockPos)) break;
                BlockPos blockPos = (BlockPos)object;
                object = args[1];
                if (!(object instanceof BlockState)) break;
                BlockState blockState = (BlockState)object;
                return this.setBlock(blockPos, blockState);
            }
            case "removeBlock": 
            case "destroyBlock": 
            case "m_7471_": 
            case "m_7740_": {
                Object object;
                if (args.length < 2 || !((object = args[0]) instanceof BlockPos)) break;
                BlockPos blockPos = (BlockPos)object;
                object = args[1];
                if (!(object instanceof Boolean)) break;
                Boolean bl = (Boolean)object;
                return this.removeBlock(blockPos, bl);
            }
            case "addEntity": 
            case "m_8872_": 
            case "addFreshEntityWithPassengers": 
            case "m_47205_": {
                Object object;
                if (args.length < 1 || !((object = args[0]) instanceof Entity)) break;
                Entity entity = (Entity)object;
                return this.addEntity(entity);
            }
        }
        return method.invoke(this.serverLevel, args);
    }
}

